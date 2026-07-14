import React, { useMemo, useState } from 'react';
import {
  LayoutChangeEvent,
  Linking,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { ScreenShell } from '../components/ScreenShell';
// import { FilterButton } from '../components/FilterButton';
import {
  colors,
  continuousRadius,
  font,
  layout,
  radius,
  spacing,
} from '../theme';

const DAY_MS = 24 * 60 * 60 * 1000;
const INITIAL_BATTERY = 60;
const BATTERY_STEPS = [0, 20, 40, 60, 80, 100] as const;
const THUMB_SIZE = 28;
const FRIEND_FILTERS = ['all', 'nearby', 'active'] as const;

type BatteryStep = (typeof BATTERY_STEPS)[number];
type FriendFilter = (typeof FRIEND_FILTERS)[number];

interface Friend {
  id: string;
  name: string;
  initials: string;
  status: string;
  distance: string;
  lastSeen: string;
  sharedInterest: string;
  accent: string;
  active: boolean;
  nearby: boolean;
}

const FRIENDS: Friend[] = [
  {
    id: 'lena',
    name: 'Lena',
    initials: 'LE',
    status: 'Online',
    distance: '1.2 km away',
    lastSeen: 'Active now',
    sharedInterest: 'Coffee walks',
    accent: colors.blue,
    active: true,
    nearby: true,
  },
  {
    id: 'marcel',
    name: 'Marcel',
    initials: 'MA',
    status: 'Planning a meetup',
    distance: '3.8 km away',
    lastSeen: 'Seen 18 min ago',
    sharedInterest: 'City quests',
    accent: colors.primary,
    active: true,
    nearby: false,
  },
  {
    id: 'karin',
    name: 'Karin',
    initials: 'KA',
    status: 'Quiet mode',
    distance: 'Nearby',
    lastSeen: 'Seen yesterday',
    sharedInterest: 'Low-pressure events',
    accent: colors.gray200,
    active: false,
    nearby: true,
  },
  {
    id: 'tom',
    name: 'Tom',
    initials: 'TO',
    status: 'Weekend only',
    distance: '7.4 km away',
    lastSeen: 'Seen 2 days ago',
    sharedInterest: 'Group challenges',
    accent: colors.gray100,
    active: false,
    nearby: false,
  },
];

const startOfDay = (date: Date) => new Date(date.getFullYear(), date.getMonth(), date.getDate());

const getClosestBatteryStep = (value: number): BatteryStep =>
  BATTERY_STEPS.reduce<BatteryStep>((closest, step) => {
    return Math.abs(step - value) < Math.abs(closest - value) ? step : closest;
  }, BATTERY_STEPS[0]);

const needsDailyRefresh = (lastCheckAt: Date | null) => {
  if (!lastCheckAt) return true;
  return startOfDay(new Date()).getTime() - startOfDay(lastCheckAt).getTime() >= DAY_MS;
};

const formatLastCheck = (lastCheckAt: Date | null) => {
  if (!lastCheckAt) return 'No daily check-in yet';

  if (!needsDailyRefresh(lastCheckAt)) {
    return 'Updated today';
  }

  return `Last updated ${lastCheckAt.toLocaleDateString('en-GB', {
    day: '2-digit',
    month: 'short',
  })}`;
};

const getBatteryMessage = (battery: number) => {
  if (battery >= 80) return 'Perfect for big social plans and busy group activities.';
  if (battery >= 60) return 'A solid day for balanced plans and casual meetups.';
  if (battery >= 40) return 'Better for lighter challenges and smaller groups.';
  if (battery >= 20) return 'Good moment for low-pressure plans and short activities.';
  return 'Best to keep recommendations calm, short and easy today.';
};

export const ProfileScreen: React.FC = () => {
  const handleOpenSettings = () => {
    void Linking.openSettings();
  };

  const [savedBattery, setSavedBattery] = useState<BatteryStep>(INITIAL_BATTERY);
  const [draftBattery, setDraftBattery] = useState<BatteryStep>(INITIAL_BATTERY);
  const [lastCheckAt, setLastCheckAt] = useState<Date | null>(null);
  const [friendFilter, setFriendFilter] = useState<FriendFilter>('all');
  const [sliderWidth, setSliderWidth] = useState(0);

  const refreshNeeded = needsDailyRefresh(lastCheckAt);
  const hasUnsavedChanges = draftBattery !== savedBattery;

  const batteryMessage = useMemo(() => getBatteryMessage(draftBattery), [draftBattery]);

  const filteredFriends = useMemo(() => {
    if (friendFilter === 'nearby') {
      return FRIENDS.filter(friend => friend.nearby);
    }

    if (friendFilter === 'active') {
      return FRIENDS.filter(friend => friend.active);
    }

    return FRIENDS;
  }, [friendFilter]);

  const friendStats = useMemo(
    () => ({
      total: FRIENDS.length,
      nearby: FRIENDS.filter(friend => friend.nearby).length,
      active: FRIENDS.filter(friend => friend.active).length,
    }),
    [],
  );

  const saveButtonDisabled = !refreshNeeded && !hasUnsavedChanges;
  const saveButtonLabel = refreshNeeded
    ? 'Save today\'s battery'
    : hasUnsavedChanges
    ? 'Update battery'
    : 'Battery checked today';

  const applyBattery = () => {
    setSavedBattery(draftBattery);
    setLastCheckAt(new Date());
  };

  const handleSliderLayout = (event: LayoutChangeEvent) => {
    setSliderWidth(event.nativeEvent.layout.width);
  };

  const updateBatteryFromLocation = (locationX: number) => {
    if (sliderWidth <= 0) return;

    const clampedLocation = Math.min(sliderWidth, Math.max(0, locationX));
    const rawValue = (clampedLocation / sliderWidth) * 100;

    setDraftBattery(getClosestBatteryStep(rawValue));
  };

  const filledSegments = Math.round(draftBattery / 20);

  return (
    <ScreenShell
      // rightButton={<FilterButton open={friendFilter !== 'all'} onPress={cycleFriendFilter} />}
      rightButton={null}
    >
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        <View style={[styles.card, styles.heroCard]}>
          <Text style={styles.sectionEyebrow}>SOCIAL BATTERY</Text>

          <View style={styles.statusRow}>
            <View
              style={[
                styles.statusChip,
                refreshNeeded ? styles.statusChipAlert : styles.statusChipReady,
              ]}
            >
              <Text style={styles.statusChipText}>
                {refreshNeeded ? 'Daily update needed' : 'Checked in'}
              </Text>
            </View>

            <Text style={styles.statusText}>{formatLastCheck(lastCheckAt)}</Text>
          </View>

          <Text style={styles.heroValue}>{draftBattery}%</Text>
          <Text style={styles.heroMessage}>{batteryMessage}</Text>

          <View style={styles.segmentRow}>
            {Array.from({ length: 5 }).map((_, index) => (
              <View
                key={index}
                style={[
                  styles.segment,
                  index < filledSegments ? styles.segmentFilled : styles.segmentEmpty,
                ]}
              />
            ))}
          </View>

        </View>

        <View style={styles.card}>
          <Text style={styles.sectionEyebrow}>TODAY'S INPUT</Text>
          <Text style={styles.sectionTitle}>How social do you feel today?</Text>
          <Text style={styles.sectionText}>
            Pick a value that matches your current energy. The app can use this to
            suggest more suitable challenges.
          </Text>

          <View style={styles.sliderBlock}>
            <View
              style={styles.sliderTouchArea}
              onLayout={handleSliderLayout}
              onStartShouldSetResponder={() => true}
              onMoveShouldSetResponder={() => true}
              onStartShouldSetResponderCapture={() => true}
              onMoveShouldSetResponderCapture={() => true}
              onResponderTerminationRequest={() => false}
              onResponderGrant={event => updateBatteryFromLocation(event.nativeEvent.locationX)}
              onResponderMove={event => updateBatteryFromLocation(event.nativeEvent.locationX)}
            >
              <View style={styles.sliderTrack}>
                <View style={[styles.sliderFill, { width: `${draftBattery}%` }]} />

                {BATTERY_STEPS.map(stepValue => (
                  <View
                    key={stepValue}
                    style={[styles.sliderDotWrap, { left: `${stepValue}%` }]}
                  >
                    <View
                      style={[
                        styles.sliderDot,
                        draftBattery >= stepValue && styles.sliderDotActive,
                        draftBattery === stepValue && styles.sliderDotSelected,
                      ]}
                    />
                  </View>
                ))}

                <View
                  style={[
                    styles.sliderThumb,
                    { left: `${draftBattery}%`, marginLeft: -THUMB_SIZE / 2 },
                  ]}
                />
              </View>
            </View>

            <View style={styles.sliderLabels}>
              {BATTERY_STEPS.map(stepValue => (
                <TouchableOpacity
                  key={stepValue}
                  style={styles.sliderLabelButton}
                  onPress={() => setDraftBattery(stepValue)}
                  activeOpacity={0.85}
                >
                  <Text
                    style={[
                      styles.sliderLabel,
                      draftBattery === stepValue && styles.sliderLabelSelected,
                    ]}
                  >
                    {stepValue}
                  </Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionEyebrow}>PROFILE USE</Text>
          <Text style={styles.sectionTitle}>Used for better recommendations</Text>

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Current saved value</Text>
            <Text style={styles.infoValue}>{savedBattery}%</Text>
          </View>

          <View style={styles.divider} />

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Daily refresh status</Text>
            <Text style={styles.infoValue}>{refreshNeeded ? 'Needed' : 'Done'}</Text>
          </View>

          <View style={styles.divider} />

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Input source today</Text>
            <Text style={styles.infoValue}>{hasUnsavedChanges ? 'Draft changed' : 'Current profile'}</Text>
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionEyebrow}>FRIENDS</Text>
          <Text style={styles.sectionTitle}>People connected to your profile</Text>
          <Text style={styles.sectionText}>
            This view groups close contacts, active people and recent meetup matches.
          </Text>

          <View style={styles.friendStatsRow}>
            <View style={styles.friendStat}>
              <Text style={styles.friendStatValue}>{friendStats.total}</Text>
              <Text style={styles.friendStatLabel}>Friends</Text>
            </View>
            <View style={styles.friendStat}>
              <Text style={styles.friendStatValue}>{friendStats.nearby}</Text>
              <Text style={styles.friendStatLabel}>Nearby</Text>
            </View>
            <View style={styles.friendStat}>
              <Text style={styles.friendStatValue}>{friendStats.active}</Text>
              <Text style={styles.friendStatLabel}>Active</Text>
            </View>
          </View>

          <View style={styles.filterRow}>
            {FRIEND_FILTERS.map(filter => {
              const selected = friendFilter === filter;
              const label =
                filter === 'all' ? 'All' : filter === 'nearby' ? 'Nearby' : 'Active';

              return (
                <TouchableOpacity
                  key={filter}
                  style={[styles.filterChip, selected && styles.filterChipSelected]}
                  onPress={() => setFriendFilter(filter)}
                  activeOpacity={0.85}
                >
                  <Text style={[styles.filterChipText, selected && styles.filterChipTextSelected]}>
                    {label}
                  </Text>
                </TouchableOpacity>
              );
            })}
          </View>

          <View style={styles.friendList}>
            {filteredFriends.map((friend, index) => (
              <View key={friend.id}>
                {index > 0 && <View style={styles.divider} />}

                <View style={styles.friendRow}>
                  <View style={[styles.friendAvatar, { backgroundColor: friend.accent }]}>
                    <Text style={styles.friendAvatarText}>{friend.initials}</Text>
                  </View>

                  <View style={styles.friendTextBlock}>
                    <Text style={styles.friendName}>{friend.name}</Text>
                    <Text style={styles.friendMeta} numberOfLines={1}>
                      {friend.status} · {friend.distance}
                    </Text>
                    <Text style={styles.friendHint}>{friend.sharedInterest}</Text>
                  </View>

                  <View style={styles.friendState}>
                    <Text style={styles.friendStateText}>{friend.lastSeen}</Text>
                  </View>
                </View>
              </View>
            ))}
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionEyebrow}>SETTINGS</Text>
          <Text style={styles.settingsTitle}>Manage your profile space</Text>

          <TouchableOpacity
            style={styles.settingRow}
            onPress={handleOpenSettings}
            activeOpacity={0.85}
          >
            <View style={styles.settingTextBlock}>
              <Text style={styles.settingLabel}>App settings</Text>
              <Text style={styles.settingHint}>Permissions and device access</Text>
            </View>
            <Text style={styles.settingValue}>Open</Text>
          </TouchableOpacity>

          <View style={styles.divider} />

          <View style={styles.settingRow}>
            <View style={styles.settingTextBlock}>
              <Text style={styles.settingLabel}>Notifications</Text>
              <Text style={styles.settingHint}>Manage reminders and updates</Text>
            </View>
            <Text style={styles.settingMuted}>Soon</Text>
          </View>

          <View style={styles.divider} />

          <View style={styles.settingRow}>
            <View style={styles.settingTextBlock}>
              <Text style={styles.settingLabel}>Privacy</Text>
              <Text style={styles.settingHint}>Review profile visibility options</Text>
            </View>
            <Text style={styles.settingMuted}>Soon</Text>
          </View>
        </View>

        <TouchableOpacity
          style={[styles.primaryButton, saveButtonDisabled && styles.primaryButtonDisabled]}
          onPress={applyBattery}
          activeOpacity={0.85}
          disabled={saveButtonDisabled}
        >
          <Text style={styles.primaryButtonText}>{saveButtonLabel}</Text>
        </TouchableOpacity>

        <TouchableOpacity style={styles.logoutButton} activeOpacity={0.85}>
          <Text style={styles.logoutText}>Log out</Text>
        </TouchableOpacity>
      </ScrollView>
    </ScreenShell>
  );
};

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
  },

  content: {
    paddingHorizontal: spacing.md,
    paddingTop: 18,
    paddingBottom: spacing.lg,
    gap: spacing.md,
  },

  card: {
    backgroundColor: colors.white,
    ...continuousRadius({ borderRadius: 28 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    padding: 18,
  },

  heroCard: {
    backgroundColor: colors.blue,
  },

  sectionEyebrow: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '700',
    letterSpacing: 1,
    color: colors.black,
    opacity: 0.65,
  },

  statusRow: {
    marginTop: spacing.sm,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
  },

  statusChip: {
    borderRadius: radius.full,
    borderWidth: 1.5,
    paddingVertical: 7,
    paddingHorizontal: 12,
  },

  statusChipAlert: {
    backgroundColor: colors.primary,
    borderColor: colors.black,
  },

  statusChipReady: {
    backgroundColor: colors.white,
    borderColor: colors.black,
  },

  statusChipText: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.black,
  },

  statusText: {
    flexShrink: 1,
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
    opacity: 0.7,
    textAlign: 'right',
  },

  heroValue: {
    marginTop: spacing.md,
    fontSize: 42,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -1,
  },

  heroMessage: {
    marginTop: 4,
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    lineHeight: 20,
  },

  segmentRow: {
    flexDirection: 'row',
    gap: 8,
    marginTop: spacing.md,
  },

  segment: {
    flex: 1,
    height: 14,
    borderRadius: radius.full,
    borderWidth: 1.5,
    borderColor: colors.black,
  },

  segmentFilled: {
    backgroundColor: colors.black,
  },

  segmentEmpty: {
    backgroundColor: 'rgba(255,255,255,0.55)',
  },

  sectionTitle: {
    marginTop: 6,
    fontSize: 20,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.3,
  },

  sectionText: {
    marginTop: spacing.xs,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 18,
  },

  settingsTitle: {
    marginTop: 6,
    marginBottom: spacing.sm,
    fontSize: 20,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.3,
  },

  settingRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 12,
    paddingVertical: 14,
  },

  settingTextBlock: {
    flex: 1,
    minWidth: 0,
  },

  settingLabel: {
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  settingHint: {
    marginTop: 4,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 18,
  },

  settingValue: {
    fontSize: 13,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.black,
  },

  settingMuted: {
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.gray500,
  },

  sliderBlock: {
    marginTop: spacing.md,
    gap: 12,
  },

  sliderTouchArea: {
    paddingVertical: 20,
    paddingHorizontal: 6,
  },

  sliderTrack: {
    height: 10,
    backgroundColor: colors.gray200,
    borderRadius: radius.full,
    position: 'relative',
  },

  sliderFill: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    left: 0,
    backgroundColor: colors.primary,
    borderRadius: radius.full,
  },

  sliderDotWrap: {
    position: 'absolute',
    top: '50%',
    marginTop: -10,
    marginLeft: -10,
    width: 20,
    height: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },

  sliderDot: {
    width: 10,
    height: 10,
    borderRadius: radius.full,
    backgroundColor: colors.white,
    borderWidth: 1.5,
    borderColor: colors.black,
  },

  sliderDotActive: {
    backgroundColor: colors.black,
  },

  sliderDotSelected: {
    width: 12,
    height: 12,
  },

  sliderThumb: {
    position: 'absolute',
    top: '50%',
    marginTop: -THUMB_SIZE / 2,
    width: THUMB_SIZE,
    height: THUMB_SIZE,
    borderRadius: radius.full,
    backgroundColor: colors.blue,
    borderWidth: layout.border,
    borderColor: colors.black,
  },

  sliderLabels: {
    flexDirection: 'row',
  },

  sliderLabelButton: {
    flex: 1,
    alignItems: 'center',
  },

  sliderLabel: {
    fontSize: 11,
    fontFamily: font.body,
    color: colors.gray500,
  },

  sliderLabelSelected: {
    color: colors.black,
    fontWeight: '800',
  },

  friendStatsRow: {
    flexDirection: 'row',
    gap: 10,
    marginTop: spacing.md,
  },

  friendStat: {
    flex: 1,
    minWidth: 0,
    backgroundColor: colors.gray100,
    ...continuousRadius({ borderRadius: 18 }),
    borderWidth: 1.5,
    borderColor: colors.cardBorder,
    paddingVertical: 14,
    paddingHorizontal: 12,
  },

  friendStatValue: {
    fontSize: 18,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  friendStatLabel: {
    marginTop: 4,
    fontSize: 11,
    fontFamily: font.body,
    color: colors.gray500,
  },

  filterRow: {
    flexDirection: 'row',
    gap: 8,
    marginTop: spacing.md,
    flexWrap: 'wrap',
  },

  filterChip: {
    backgroundColor: colors.gray100,
    borderRadius: radius.full,
    borderWidth: 1.5,
    borderColor: colors.cardBorder,
    paddingVertical: 8,
    paddingHorizontal: 14,
  },

  filterChipSelected: {
    backgroundColor: colors.primary,
    borderColor: colors.black,
  },

  filterChipText: {
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.gray500,
  },

  filterChipTextSelected: {
    color: colors.black,
  },

  friendList: {
    marginTop: spacing.sm,
  },

  friendRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingVertical: 14,
  },

  friendAvatar: {
    width: 44,
    height: 44,
    borderRadius: 22,
    borderWidth: 1.5,
    borderColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },

  friendAvatarText: {
    fontSize: 13,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  friendTextBlock: {
    flex: 1,
    minWidth: 0,
  },

  friendName: {
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  friendMeta: {
    marginTop: 3,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 16,
  },

  friendHint: {
    marginTop: 3,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.7,
    lineHeight: 16,
  },

  friendState: {
    alignItems: 'flex-end',
    justifyContent: 'center',
  },

  friendStateText: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
    opacity: 0.7,
    textAlign: 'right',
  },

  infoRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 12,
    paddingVertical: 14,
  },

  infoLabel: {
    flex: 1,
    minWidth: 0,
    fontSize: 13,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 18,
  },

  infoValue: {
    fontSize: 13,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    textAlign: 'right',
  },

  divider: {
    height: 1,
    backgroundColor: colors.divider,
  },

  primaryButton: {
    width: '100%',
    paddingVertical: 16,
    borderRadius: radius.full,
    backgroundColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.xs,
  },

  primaryButtonDisabled: {
    opacity: 0.35,
  },

  primaryButtonText: {
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.white,
  },

  logoutButton: {
    marginTop: 4,
    marginBottom: spacing.xs,
    width: '100%',
    paddingVertical: 16,
    borderRadius: radius.full,
    backgroundColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },

  logoutText: {
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.white,
  },
});

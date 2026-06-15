import React from 'react';
import {
  Linking,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import {
  colors,
  continuousRadius,
  font,
  layout,
  radius,
  spacing,
} from '../theme';

const SOCIAL_BATTERY = 4;

const batteryHint =
  SOCIAL_BATTERY >= 4
    ? 'Ready for bigger plans today.'
    : SOCIAL_BATTERY >= 2
    ? 'Good for a balanced day out.'
    : 'Best for quieter plans today.';

export const ProfileScreen: React.FC = () => {
  const handleOpenSettings = () => {
    void Linking.openSettings();
  };

  return (
    <ScreenShell rightButton={<FilterButton />}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        <View style={[styles.card, styles.batteryCard]}>
          <Text style={styles.sectionEyebrow}>SOCIAL BATTERY</Text>

          <View style={styles.batteryHeader}>
            <View style={styles.batteryCopy}>
              <Text style={styles.batteryValue}>{SOCIAL_BATTERY}/5</Text>
              <Text style={styles.batteryHint}>{batteryHint}</Text>
            </View>

            <View style={styles.badge}>
              <Text style={styles.badgeText}>80% charged</Text>
            </View>
          </View>

          <View style={styles.batteryScale}>
            {Array.from({ length: 5 }).map((_, index) => (
              <View
                key={index}
                style={[
                  styles.batterySegment,
                  index < SOCIAL_BATTERY
                    ? styles.batterySegmentFilled
                    : styles.batterySegmentEmpty,
                ]}
              />
            ))}
          </View>

          <Text style={styles.batteryNote}>
            Your profile energy level is shown here for quick planning.
          </Text>
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

  batteryCard: {
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

  batteryHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 12,
    marginTop: spacing.sm,
  },

  batteryCopy: {
    flex: 1,
    minWidth: 0,
  },

  batteryValue: {
    fontSize: 38,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.8,
  },

  batteryHint: {
    marginTop: 4,
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    lineHeight: 20,
  },

  badge: {
    backgroundColor: colors.white,
    borderRadius: radius.full,
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 8,
    paddingHorizontal: 14,
  },

  badgeText: {
    fontSize: 12,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  batteryScale: {
    flexDirection: 'row',
    gap: 8,
    marginTop: spacing.md,
  },

  batterySegment: {
    flex: 1,
    height: 14,
    borderRadius: radius.full,
    borderWidth: 1.5,
    borderColor: colors.black,
  },

  batterySegmentFilled: {
    backgroundColor: colors.black,
  },

  batterySegmentEmpty: {
    backgroundColor: 'rgba(255,255,255,0.55)',
  },

  batteryNote: {
    marginTop: spacing.sm,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.7,
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

  divider: {
    height: 1,
    backgroundColor: colors.divider,
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

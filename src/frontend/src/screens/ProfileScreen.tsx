import React, { useMemo, useState } from 'react';
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
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

const DAY_MS = 24 * 60 * 60 * 1000;
const INITIAL_BATTERY = 60;
const QUICK_VALUES = [20, 40, 60, 80, 100];

const clampBattery = (value: number) => Math.min(100, Math.max(0, value));

const startOfDay = (date: Date) => new Date(date.getFullYear(), date.getMonth(), date.getDate());

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
  const [savedBattery, setSavedBattery] = useState(INITIAL_BATTERY);
  const [draftBattery, setDraftBattery] = useState(INITIAL_BATTERY);
  const [lastCheckAt, setLastCheckAt] = useState<Date | null>(null);

  const refreshNeeded = needsDailyRefresh(lastCheckAt);
  const hasUnsavedChanges = draftBattery !== savedBattery;

  const batteryMessage = useMemo(() => getBatteryMessage(draftBattery), [draftBattery]);

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

  const adjustBattery = (delta: number) => {
    setDraftBattery(prev => clampBattery(prev + delta));
  };

  const filledSegments = Math.round(draftBattery / 20);

  return (
    <ScreenShell rightButton={<FilterButton />}>
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

          <View style={styles.stepperRow}>
            <TouchableOpacity
              style={styles.stepperButton}
              onPress={() => adjustBattery(-10)}
              activeOpacity={0.85}
            >
              <Text style={styles.stepperButtonText}>-10</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.stepperButton}
              onPress={() => adjustBattery(10)}
              activeOpacity={0.85}
            >
              <Text style={styles.stepperButtonText}>+10</Text>
            </TouchableOpacity>
          </View>
        </View>

        <View style={styles.card}>
          <Text style={styles.sectionEyebrow}>QUICK SELECT</Text>
          <Text style={styles.sectionTitle}>How social do you feel today?</Text>
          <Text style={styles.sectionText}>
            Pick a value that matches your current energy. The app can use this to
            suggest more suitable challenges.
          </Text>

          <View style={styles.quickGrid}>
            {QUICK_VALUES.map(value => {
              const selected = value === draftBattery;

              return (
                <TouchableOpacity
                  key={value}
                  style={[styles.quickChip, selected && styles.quickChipSelected]}
                  onPress={() => setDraftBattery(value)}
                  activeOpacity={0.85}
                >
                  <Text style={[styles.quickChipValue, selected && styles.quickChipValueSelected]}>
                    {value}%
                  </Text>
                  <Text style={[styles.quickChipLabel, selected && styles.quickChipLabelSelected]}>
                    {value <= 20
                      ? 'Low-key'
                      : value <= 40
                      ? 'Light'
                      : value <= 60
                      ? 'Balanced'
                      : value <= 80
                      ? 'Open'
                      : 'Full send'}
                  </Text>
                </TouchableOpacity>
              );
            })}
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

        <TouchableOpacity
          style={[styles.primaryButton, saveButtonDisabled && styles.primaryButtonDisabled]}
          onPress={applyBattery}
          activeOpacity={0.85}
          disabled={saveButtonDisabled}
        >
          <Text style={styles.primaryButtonText}>{saveButtonLabel}</Text>
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

  stepperRow: {
    flexDirection: 'row',
    gap: 10,
    marginTop: spacing.md,
  },

  stepperButton: {
    flex: 1,
    backgroundColor: colors.white,
    borderRadius: radius.full,
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 14,
    alignItems: 'center',
    justifyContent: 'center',
  },

  stepperButtonText: {
    fontSize: 15,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
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

  quickGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    marginTop: spacing.md,
  },

  quickChip: {
    width: '48%',
    minWidth: 120,
    backgroundColor: colors.gray100,
    ...continuousRadius({ borderRadius: 20 }),
    borderWidth: 1.5,
    borderColor: colors.cardBorder,
    paddingVertical: 14,
    paddingHorizontal: 14,
  },

  quickChipSelected: {
    backgroundColor: colors.blue,
    borderColor: colors.black,
  },

  quickChipValue: {
    fontSize: 18,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  quickChipValueSelected: {
    color: colors.black,
  },

  quickChipLabel: {
    marginTop: 4,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
  },

  quickChipLabelSelected: {
    color: colors.black,
    opacity: 0.75,
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
});

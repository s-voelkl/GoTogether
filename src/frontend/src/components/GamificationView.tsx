import React from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';

import {
  CATEGORY_COLORS,
  CATEGORY_EMOJI,
  Challenge,
  mockChallenges,
} from '../data/mockChallenges';
import {
  colors,
  continuousRadius,
  font,
  layout,
  radius,
  spacing,
} from '../theme';

const LEVEL_XP_STEP = 250;

const MONTHS = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
];

interface Reward {
  id: string;
  label: string;
  title: string;
  subtitle: string;
  accent: string;
  unlocked: boolean;
}

const recentChallenges: Challenge[] = [...mockChallenges]
  .sort((a, b) => new Date(b.startTime).getTime() - new Date(a.startTime).getTime())
  .slice(0, 4);

const totalExperiencePoints = recentChallenges.reduce(
  (sum, challenge) => sum + challenge.experiencePoints,
  0,
);

const totalGbucks = recentChallenges.reduce(
  (sum, challenge) => sum + challenge.points,
  0,
);

const level = Math.max(1, Math.floor(totalExperiencePoints / LEVEL_XP_STEP) + 1);
const levelXp = totalExperiencePoints % LEVEL_XP_STEP;
const levelProgress = Math.min(levelXp / LEVEL_XP_STEP, 1);

const rewards: Reward[] = [
  {
    id: 'coffee-drop',
    label: 'CD',
    title: 'Coffee Drop',
    subtitle: 'Collect 200 G-Bucks',
    accent: colors.primary,
    unlocked: totalGbucks >= 200,
  },
  {
    id: 'fast-track',
    label: 'FT',
    title: 'Fast Track Pass',
    subtitle: 'Reach level 4',
    accent: colors.blue,
    unlocked: level >= 4,
  },
  {
    id: 'city-legend',
    label: 'CL',
    title: 'City Legend Badge',
    subtitle: 'Complete 5 challenges',
    accent: colors.gray200,
    unlocked: recentChallenges.length >= 5,
  },
];

const unlockedRewards = rewards.filter(reward => reward.unlocked).length;

const formatDate = (iso: string) => {
  const date = new Date(iso);
  const day = String(date.getDate()).padStart(2, '0');
  return `${day} ${MONTHS[date.getMonth()]}`;
};

export const GamificationView: React.FC = () => (
  <ScrollView
    style={styles.scrollView}
    contentContainerStyle={styles.content}
    showsVerticalScrollIndicator={false}
  >
    <View style={[styles.card, styles.heroCard]}>
      <Text style={styles.eyebrow}>GAMIFICATION</Text>
      <Text style={styles.heroTitle}>Level {level}</Text>
      <Text style={styles.heroSubtitle}>
        {totalExperiencePoints} XP earned from recent challenges
      </Text>

      <View style={styles.progressTrack}>
        <View
          style={[styles.progressFill, { width: `${Math.max(levelProgress * 100, 0)}%` }]}
        />
      </View>

      <View style={styles.progressRow}>
        <Text style={styles.progressText}>{levelXp}/{LEVEL_XP_STEP} XP this level</Text>
        <Text style={styles.progressText}>Next: Level {level + 1}</Text>
      </View>
    </View>

    <View style={styles.metricsRow}>
      <View style={[styles.card, styles.metricCard]}>
        <Text style={styles.eyebrow}>G-BUCKS</Text>
        <Text style={styles.metricValue}>{totalGbucks}</Text>
        <Text style={styles.metricHint}>Current digital balance</Text>
      </View>

      <View style={[styles.card, styles.metricCard]}>
        <Text style={styles.eyebrow}>REWARDS</Text>
        <Text style={styles.metricValue}>
          {unlockedRewards}/{rewards.length}
        </Text>
        <Text style={styles.metricHint}>Unlocked so far</Text>
      </View>
    </View>

    <View style={styles.card}>
      <View style={styles.sectionHeader}>
        <View style={styles.sectionHeaderText}>
          <Text style={styles.eyebrow}>REWARDS</Text>
          <Text style={styles.sectionTitle}>Keep collecting for unlocks</Text>
        </View>

        <View style={styles.liveChip}>
          <Text style={styles.liveChipText}>{unlockedRewards} live</Text>
        </View>
      </View>

      {rewards.map((reward, index) => (
        <View key={reward.id}>
          {index > 0 && <View style={styles.divider} />}

          <View style={styles.rewardRow}>
            <View style={[styles.rewardBadge, { backgroundColor: reward.accent }]}>
              <Text style={styles.rewardBadgeText}>{reward.label}</Text>
            </View>

            <View style={styles.rewardTextBlock}>
              <Text style={styles.rewardTitle}>{reward.title}</Text>
              <Text style={styles.rewardSubtitle}>{reward.subtitle}</Text>
            </View>

            <View
              style={[
                styles.rewardState,
                reward.unlocked ? styles.rewardStateUnlocked : styles.rewardStateLocked,
              ]}
            >
              <Text
                style={[
                  styles.rewardStateText,
                  reward.unlocked
                    ? styles.rewardStateTextUnlocked
                    : styles.rewardStateTextLocked,
                ]}
              >
                {reward.unlocked ? 'Unlocked' : 'Locked'}
              </Text>
            </View>
          </View>
        </View>
      ))}
    </View>

    <View style={styles.card}>
      <Text style={styles.eyebrow}>CHALLENGE HISTORY</Text>
      <Text style={styles.sectionTitle}>Recent completed challenges</Text>

      {recentChallenges.map((challenge, index) => {
        const accent = CATEGORY_COLORS[challenge.category] ?? colors.blue;

        return (
          <View key={challenge.id}>
            {index > 0 && <View style={styles.divider} />}

            <View style={styles.historyRow}>
              <View style={[styles.historyIconBox, { backgroundColor: accent + '28' }]}>
                <Text style={styles.historyEmoji}>
                  {CATEGORY_EMOJI[challenge.category] ?? 'GO'}
                </Text>
              </View>

              <View style={styles.historyTextBlock}>
                <Text style={styles.historyTitle} numberOfLines={1}>
                  {challenge.name}
                </Text>
                <Text style={styles.historySubtitle}>
                  {challenge.category} / {formatDate(challenge.startTime)}
                </Text>
              </View>

              <View style={styles.historyMeta}>
                <Text style={styles.historyValue}>+{challenge.experiencePoints} XP</Text>
                <Text style={styles.historyHint}>+{challenge.points} G</Text>
              </View>
            </View>
          </View>
        );
      })}
    </View>
  </ScrollView>
);

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

  eyebrow: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '700',
    letterSpacing: 1,
    color: colors.black,
    opacity: 0.65,
  },

  heroTitle: {
    marginTop: spacing.sm,
    fontSize: 34,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.8,
  },

  heroSubtitle: {
    marginTop: 6,
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    lineHeight: 20,
  },

  progressTrack: {
    marginTop: spacing.md,
    height: 16,
    backgroundColor: 'rgba(255,255,255,0.55)',
    borderRadius: radius.full,
    borderWidth: 1.5,
    borderColor: colors.black,
    overflow: 'hidden',
  },

  progressFill: {
    height: '100%',
    backgroundColor: colors.black,
    borderRadius: radius.full,
  },

  progressRow: {
    marginTop: spacing.sm,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
  },

  progressText: {
    flexShrink: 1,
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
    opacity: 0.75,
  },

  metricsRow: {
    flexDirection: 'row',
    gap: spacing.md,
  },

  metricCard: {
    flex: 1,
    minWidth: 0,
  },

  metricValue: {
    marginTop: spacing.sm,
    fontSize: 30,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.6,
  },

  metricHint: {
    marginTop: 4,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 18,
  },

  sectionHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    justifyContent: 'space-between',
    gap: 12,
  },

  sectionHeaderText: {
    flex: 1,
    minWidth: 0,
  },

  sectionTitle: {
    marginTop: 6,
    marginBottom: spacing.sm,
    fontSize: 20,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.3,
  },

  liveChip: {
    backgroundColor: colors.blue,
    borderRadius: radius.full,
    borderWidth: 1.5,
    borderColor: colors.black,
    paddingVertical: 7,
    paddingHorizontal: 12,
  },

  liveChipText: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.black,
  },

  divider: {
    height: 1,
    backgroundColor: colors.divider,
  },

  rewardRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingVertical: 14,
  },

  rewardBadge: {
    width: 48,
    height: 48,
    ...continuousRadius({ borderRadius: 14 }),
    borderWidth: 1.5,
    borderColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },

  rewardBadgeText: {
    fontSize: 13,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  rewardTextBlock: {
    flex: 1,
    minWidth: 0,
  },

  rewardTitle: {
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  rewardSubtitle: {
    marginTop: 4,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 18,
  },

  rewardState: {
    borderRadius: radius.full,
    borderWidth: 1.5,
    paddingVertical: 6,
    paddingHorizontal: 10,
  },

  rewardStateUnlocked: {
    backgroundColor: colors.primary,
    borderColor: colors.black,
  },

  rewardStateLocked: {
    backgroundColor: colors.gray100,
    borderColor: colors.cardBorder,
  },

  rewardStateText: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '800',
  },

  rewardStateTextUnlocked: {
    color: colors.black,
  },

  rewardStateTextLocked: {
    color: colors.gray500,
  },

  historyRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    paddingVertical: 14,
  },

  historyIconBox: {
    width: 52,
    height: 52,
    ...continuousRadius({ borderRadius: 16 }),
    alignItems: 'center',
    justifyContent: 'center',
  },

  historyEmoji: {
    fontSize: 24,
  },

  historyTextBlock: {
    flex: 1,
    minWidth: 0,
  },

  historyTitle: {
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  historySubtitle: {
    marginTop: 4,
    fontSize: 12,
    fontFamily: font.body,
    color: colors.gray500,
  },

  historyMeta: {
    alignItems: 'flex-end',
    gap: 3,
  },

  historyValue: {
    fontSize: 12,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  historyHint: {
    fontSize: 11,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.gray500,
  },
});

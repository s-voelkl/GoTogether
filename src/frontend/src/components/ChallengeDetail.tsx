import React, { useEffect, useRef } from 'react';
import {
  Animated,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import {
  Challenge,
  CATEGORY_COLORS,
  CATEGORY_EMOJI,
  DIFFICULTY_COLORS,
  difficultyFromBattery,
  isChallengeFull,
} from '../data/mockChallenges';

import { colors, font, layout, radius, spacing } from '../theme';
import { LocationArrowIcon } from './Icons';
import { FadeEdge } from './FadeEdge';

const DAYS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

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

const pad = (n: number) => String(n).padStart(2, '0');

const formatWhen = (iso: string) => {
  const d = new Date(iso);

  return `${DAYS[d.getDay()]} ${d.getDate()} ${
    MONTHS[d.getMonth()]
  } · ${pad(d.getHours())}:${pad(d.getMinutes())}`;
};

const formatDuration = (m: number) =>
  m >= 60
    ? `${Math.floor(m / 60)}h${m % 60 ? ` ${m % 60}m` : ''}`
    : `${m} min`;

const formatPlayers = (c: Challenge) =>
  c.maxPlayers === 0
    ? `${c.participants} joined · unlimited`
    : `${c.participants}/${c.maxPlayers} joined`;

interface ChallengeDetailProps {
  open: boolean;
  challenge: Challenge;
  onShowOnMap: (challenge: Challenge) => void;

  /**
   * Used for Home/NearbyOverlay → Challenges handoff.
   * The tab transition already happened, so we show the detail instantly.
   */
  disableOpenAnimation?: boolean;
}

const InfoRow: React.FC<{ label: string; value: string }> = ({
  label,
  value,
}) => (
  <View style={styles.infoRow}>
    <Text style={styles.infoLabel}>{label}</Text>
    <Text style={styles.infoValue}>{value}</Text>
  </View>
);

export const ChallengeDetail: React.FC<ChallengeDetailProps> = ({
  open,
  challenge,
  onShowOnMap,
  disableOpenAnimation = false,
}) => {
  const opacity = useRef(
    new Animated.Value(open && disableOpenAnimation ? 1 : 0),
  ).current;

  const prevOpen = useRef(false);

  useEffect(() => {
    const opening = open && !prevOpen.current;
    prevOpen.current = open;

    if (opening && disableOpenAnimation) {
      opacity.stopAnimation();
      opacity.setValue(1);
      return;
    }

    Animated.timing(opacity, {
      toValue: open ? 1 : 0,
      duration: 200,
      useNativeDriver: true,
    }).start();
  }, [open, opacity, disableOpenAnimation]);

  const accent = CATEGORY_COLORS[challenge.category] ?? colors.muted;
  const difficulty = difficultyFromBattery(challenge.minSocialBattery);
  const diffColor = DIFFICULTY_COLORS[difficulty] ?? colors.muted;
  const full = isChallengeFull(challenge);

  return (
    <Animated.View
      style={[styles.fill, { opacity }]}
      pointerEvents={open ? 'auto' : 'none'}
    >
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scroll}
        showsVerticalScrollIndicator={false}
      >
        <View style={[styles.iconBox, { backgroundColor: accent + '28' }]}>
          <Text style={{ fontSize: 52 }}>
            {CATEGORY_EMOJI[challenge.category] ?? '📍'}
          </Text>
        </View>

        <Text style={styles.name}>{challenge.name}</Text>
        <Text style={styles.host}>Hosted by {challenge.host}</Text>

        <View style={styles.badgeRow}>
          {full && (
            <View style={styles.fullChip}>
              <Text style={styles.fullChipText}>Full</Text>
            </View>
          )}

          <View style={[styles.badge, { backgroundColor: diffColor + '22' }]}>
            <Text style={[styles.badgeText, { color: diffColor }]}>
              {difficulty}
            </Text>
          </View>

          <Text style={styles.ptsText}>{challenge.points} pts</Text>
        </View>

        <View style={styles.infoCard}>
          <InfoRow label="When" value={formatWhen(challenge.startTime)} />

          <View style={styles.divider} />

          <InfoRow
            label="Duration"
            value={formatDuration(challenge.durationMinutes)}
          />

          <View style={styles.divider} />

          <InfoRow label="Players" value={formatPlayers(challenge)} />

          <View style={styles.divider} />

          <InfoRow
            label="Min. social battery"
            value={`${challenge.minSocialBattery}/5`}
          />
        </View>

        <Text style={styles.sectionLabel}>About this challenge</Text>
        <Text style={styles.description}>{challenge.description}</Text>
      </ScrollView>

      <View style={styles.footer}>
        <View style={styles.footerFade} pointerEvents="none">
          <FadeEdge edge="bottom" color={colors.white} height={28} />
        </View>

        <TouchableOpacity
          style={styles.mapBtn}
          onPress={() => onShowOnMap(challenge)}
          activeOpacity={0.85}
        >
          <LocationArrowIcon size={16} color={colors.black} />
          <Text style={styles.mapText}>Show on map</Text>
        </TouchableOpacity>
      </View>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  fill: {
    flex: 1,
    width: '100%',
    height: '100%',
    backgroundColor: colors.white,
  },

  scrollView: {
    flex: 1,
  },

  scroll: {
    paddingHorizontal: spacing.lg,
    paddingTop: 28,
    paddingBottom: spacing.lg,
    alignItems: 'flex-start',
  },

  iconBox: {
    width: 96,
    height: 96,
    borderRadius: 24,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.md,
  },

  name: {
    fontSize: 26,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.6,
  },

  host: {
    fontSize: 13,
    fontFamily: font.body,
    color: colors.gray500,
    marginTop: 4,
  },

  badgeRow: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    marginTop: spacing.md,
    alignSelf: 'stretch',
  },

  badge: {
    borderRadius: radius.full,
    paddingHorizontal: 12,
    paddingVertical: 5,
  },

  badgeText: {
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '700',
    textTransform: 'capitalize',
  },

  fullChip: {
    borderRadius: radius.full,
    paddingHorizontal: 12,
    paddingVertical: 5,
    backgroundColor: '#EF444418',
    borderWidth: layout.border,
    borderColor: DIFFICULTY_COLORS.hard + '18',
  },

  fullChipText: {
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '800',
    color: DIFFICULTY_COLORS.hard,
  },

  ptsText: {
    marginLeft: 'auto',
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },

  infoCard: {
    alignSelf: 'stretch',
    marginTop: spacing.lg,
    backgroundColor: colors.gray100,
    borderRadius: 18,
    paddingHorizontal: 16,
    paddingVertical: 4,
  },

  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    gap: 12,
  },

  infoLabel: {
    fontSize: 13,
    fontFamily: font.body,
    color: colors.gray500,
  },

  infoValue: {
    flexShrink: 1,
    textAlign: 'right',
    fontSize: 13,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
  },

  divider: {
    height: 1,
    backgroundColor: colors.divider,
  },

  sectionLabel: {
    fontSize: 12,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.gray500,
    textTransform: 'uppercase',
    letterSpacing: 0.5,
    marginTop: spacing.lg,
    marginBottom: 8,
  },

  description: {
    fontSize: 15,
    fontFamily: font.body,
    lineHeight: 24,
    color: colors.black,
  },

  footer: {
    paddingHorizontal: spacing.lg,
    paddingTop: spacing.sm,
    paddingBottom: 22,
    backgroundColor: colors.white,
  },

  footerFade: {
    position: 'absolute',
    left: 0,
    right: 0,
    top: -28,
    height: 28,
  },

  mapBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    gap: 8,
    paddingVertical: 16,
    borderRadius: radius.full,
    backgroundColor: colors.blue,
    borderWidth: layout.border,
    borderColor: colors.black,
  },

  mapText: {
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.black,
  },
});
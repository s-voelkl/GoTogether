import React from 'react';
import { StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { colors, font, radius, shadow, continuousRadius } from '../theme';
import {
  Challenge,
  CATEGORY_COLORS,
  CATEGORY_EMOJI,
  DIFFICULTY_COLORS,
} from '../data/mockChallenges';

interface ChallengeCardProps {
  challenge: Challenge;
  /** Card occupies one carousel page; width comes from the carousel container. */
  width: number;
  onPress: () => void;
}

export const ChallengeCard = React.memo<ChallengeCardProps>(
  ({ challenge, width, onPress }) => {
    const accent = CATEGORY_COLORS[challenge.category] ?? '#888';
    const diffColor = DIFFICULTY_COLORS[challenge.difficulty] ?? '#888';
    return (
      <View style={{ width, paddingHorizontal: 14 }}>
        <TouchableOpacity style={styles.card} onPress={onPress} activeOpacity={0.88}>
          <View style={[styles.iconBox, { backgroundColor: accent + '28' }]}>
            <Text style={{ fontSize: 32 }}>
              {CATEGORY_EMOJI[challenge.category] ?? '📍'}
            </Text>
          </View>
          <View style={styles.body}>
            <Text style={styles.name} numberOfLines={1}>{challenge.name}</Text>
            <Text style={styles.category} numberOfLines={1}>{challenge.category}</Text>
            <View style={styles.footer}>
              <View style={[styles.diffBadge, { backgroundColor: diffColor + '22' }]}>
                <Text style={[styles.diffText, { color: diffColor }]}>
                  {challenge.difficulty}
                </Text>
              </View>
              <Text style={styles.points}>{challenge.points} pts</Text>
            </View>
          </View>
        </TouchableOpacity>
      </View>
    );
  },
);

const styles = StyleSheet.create({
  card: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.white,
    ...continuousRadius({ borderRadius: 16 }),
    padding: 16,
    gap: 16,
    borderWidth: 1.5,
    borderColor: 'rgba(0,0,0,0.10)',
    ...shadow.sm,
  },
  iconBox: {
    width: 68,
    height: 68,
    ...continuousRadius({ borderRadius: 14 }),
    alignItems: 'center',
    justifyContent: 'center',
  },
  body: { flex: 1, minWidth: 0 },
  name: {
    fontSize: 15,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },
  category: {
    fontSize: 11,
    fontFamily: font.body,
    color: colors.gray500,
    marginTop: 3,
    marginBottom: 8,
  },
  footer: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
  },
  diffBadge: { borderRadius: radius.full, paddingHorizontal: 9, paddingVertical: 3 },
  diffText: {
    fontSize: 10,
    fontFamily: font.body,
    fontWeight: '700',
    textTransform: 'capitalize',
  },
  points: {
    fontSize: 13,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
  },
});

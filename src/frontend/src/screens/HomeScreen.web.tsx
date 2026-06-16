import React, { useCallback, useState } from 'react';
import { ScrollView, StyleSheet, Text, View } from 'react-native';
import { useNavigation } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { ChallengeCard } from '../components/ChallengeCard';

import { useFilteredChallenges } from '../context/FiltersContext';
import { mockChallenges, Challenge } from '../data/mockChallenges';
import { TabNavigationProp } from '../navigation/types';
import { colors, continuousRadius, font, layout, spacing } from '../theme';

const PLACEHOLDER_USER_INTEREST_IDS = ['Food', 'Culture'];

interface AssistantGreeting {
  message: string;
  matchedInterestIds: string[];
  suggestedChallenge: Challenge | null;
}

function buildAssistantGreeting(
  matchedInterestIds: string[],
  challenges: Challenge[],
): AssistantGreeting {
  const normalizedInterestIds = matchedInterestIds.filter(Boolean);
  const suggestedChallenge =
    normalizedInterestIds.length > 0
      ? challenges.find(challenge => normalizedInterestIds.includes(challenge.category)) ?? null
      : null;

  return {
    message:
      'TBD: Basierend auf deinen Interessen haben wir heute eine passende Challenge fuer dich gefunden.',
    matchedInterestIds: normalizedInterestIds,
    suggestedChallenge,
  };
}

export const HomeScreen: React.FC = () => {
  const navigation = useNavigation<TabNavigationProp<'Home'>>();
  const filtered = useFilteredChallenges();
  const [filterOpen, setFilterOpen] = useState(false);

  const assistantGreeting = buildAssistantGreeting(
    PLACEHOLDER_USER_INTEREST_IDS,
    mockChallenges,
  );

  const toggleFilter = useCallback(() => setFilterOpen(prev => !prev), []);
  const closeFilter = useCallback(() => setFilterOpen(false), []);

  const handleCardPress = useCallback(
    (challenge: Challenge) => {
      navigation.navigate('Challenges', {
        selectedChallengeId: challenge.id,
        selectedTs: Date.now(),
      });
    },
    [navigation],
  );

  return (
    <ScreenShell rightButton={<FilterButton open={filterOpen} onPress={toggleFilter} />}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.content}
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.greetingCard}>
          <Text style={styles.greetingLabel}>KI-Nachricht</Text>
          <Text style={styles.greetingText}>{assistantGreeting.message}</Text>
          {assistantGreeting.suggestedChallenge && (
            <Text style={styles.greetingHint}>
              Passender Vorschlag: {assistantGreeting.suggestedChallenge.name}
            </Text>
          )}
        </View>

        <View style={styles.infoCard}>
          <Text style={styles.infoTitle}>Home im Browser</Text>
          <Text style={styles.infoText}>
            Die Kartenansicht ist in der Web-Vorschau nicht aktiv. Du kannst hier
            trotzdem Challenges und Filter nutzen.
          </Text>
        </View>

        <Text style={styles.sectionTitle}>
          Challenges <Text style={styles.count}>({filtered.length})</Text>
        </Text>

        <View style={styles.list}>
          {filtered.map(challenge => (
            <ChallengeCard
              key={challenge.id}
              challenge={challenge}
              onPress={() => handleCardPress(challenge)}
            />
          ))}
        </View>
      </ScrollView>

      <View
        pointerEvents={filterOpen ? 'auto' : 'none'}
        style={styles.filterLayer}
      >
        <FilterSheet open={filterOpen} onApply={closeFilter} />
      </View>
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

  greetingCard: {
    backgroundColor: colors.primary,
    ...continuousRadius({ borderRadius: 28 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    padding: 18,
  },

  greetingLabel: {
    fontSize: 12,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
    marginBottom: 6,
  },

  greetingText: {
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    lineHeight: 20,
  },

  greetingHint: {
    fontSize: 12,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.7,
    marginTop: 8,
    lineHeight: 18,
  },

  infoCard: {
    backgroundColor: colors.blue,
    ...continuousRadius({ borderRadius: 28 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    padding: 18,
  },

  infoTitle: {
    fontSize: 16,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
    marginBottom: 6,
  },

  infoText: {
    fontSize: 12,
    fontFamily: font.body,
    color: colors.black,
    lineHeight: 18,
  },

  sectionTitle: {
    fontSize: 16,
    fontFamily: font.headingBold,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.3,
  },

  count: {
    color: colors.gray500,
  },

  list: {
    gap: 12,
  },

  filterLayer: {
    position: 'absolute',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
    zIndex: 9999,
    elevation: 9999,
  },
});

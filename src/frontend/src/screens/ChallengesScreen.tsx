import React, { useCallback, useState } from "react";
import { FlatList, StyleSheet, Text, View } from "react-native";
import { useRoute } from "@react-navigation/native";

import { ScreenShell } from "../components/ScreenShell";
import { FilterButton } from "../components/FilterButton";
import { FilterSheet } from "../components/FilterSheet";
import {
  mockChallenges,
  CATEGORY_COLORS,
  CATEGORY_EMOJI,
} from "../data/mockChallenges";
import { TabRouteProp } from "../navigation/types";
import {
  colors,
  continuousRadius,
  font,
  layout,
  radius,
  shadow,
  spacing,
} from "../theme";

export const ChallengesScreen: React.FC = () => {
  const route = useRoute<TabRouteProp<"Challenges">>();
  const [filterOpen, setFilterOpen] = useState(false);

  const toggleFilter = useCallback(() => {
    setFilterOpen((prev) => !prev);
  }, []);

  const closeFilter = useCallback(() => {
    setFilterOpen(false);
  }, []);

  return (
    <ScreenShell
      rightButton={<FilterButton open={filterOpen} onPress={toggleFilter} />}
    >
      <View style={styles.header}>
        <Text style={styles.eyebrow}>GO TOGETHER</Text>
        <Text style={styles.title}>Challenges</Text>
        <Text style={styles.subtitle}>
          Wähle eine lokale Challenge und lerne neue Menschen in deiner Nähe
          kennen.
        </Text>
      </View>

      <FlatList
        data={mockChallenges}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContent}
        renderItem={({ item }) => {
          const cardColor = CATEGORY_COLORS[item.category] ?? colors.white;
          const emoji = CATEGORY_EMOJI[item.category] ?? "✨";

          return (
            <View style={[styles.card, { backgroundColor: cardColor }]}>
              <View style={styles.cardTop}>
                <View style={styles.emojiBadge}>
                  <Text style={styles.emoji}>{emoji}</Text>
                </View>

                <View style={styles.pointsBadge}>
                  <Text style={styles.pointsText}>{item.points} XP</Text>
                </View>
              </View>

              <Text style={styles.cardTitle}>{item.name}</Text>
              <Text style={styles.description}>{item.description}</Text>
            </View>
          );
        }}
      />
    </ScreenShell>
  );
};

const styles = StyleSheet.create({
  header: {
    paddingHorizontal: layout.hMargin,
    paddingTop: spacing.lg,
    paddingBottom: spacing.md,
  },
  eyebrow: {
    fontFamily: font.body,
    fontSize: 11,
    letterSpacing: 1.8,
    color: colors.black,
    opacity: 0.65,
    marginBottom: spacing.xs,
  },
  title: {
    fontFamily: font.heading,
    fontSize: 34,
    color: colors.black,
  },
  subtitle: {
    fontFamily: font.body,
    fontSize: 13,
    lineHeight: 20,
    color: colors.black,
    opacity: 0.72,
    marginTop: spacing.sm,
    maxWidth: 360,
  },
  listContent: {
    paddingHorizontal: layout.hMargin,
    paddingBottom: spacing.xl + 120,
    gap: 24,
  },
  card: {
    minHeight: 140,
    borderWidth: 3,
    borderColor: colors.black,
    backgroundColor: colors.white,
    padding: 20,
    borderRadius: 24,
    shadowColor: "#000",
    shadowOffset: { width: 8, height: 8 },
    shadowOpacity: 1,
    shadowRadius: 0,
    elevation: 12,
  },
  cardTop: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  emojiBadge: {
    width: 58,
    height: 58,
    borderRadius: radius.full,
    borderWidth: 3,
    borderColor: colors.black,
    backgroundColor: colors.white,
    alignItems: "center",
    justifyContent: "center",
  },
  emoji: {
    fontSize: 32,
  },
  pointsBadge: {
    backgroundColor: colors.black,
    paddingHorizontal: 18,
    paddingVertical: 10,
    borderRadius: radius.full,
  },
  pointsText: {
    fontFamily: font.headingBold,
    color: colors.white,
    fontSize: 13,
  },
  cardTitle: {
    fontFamily: font.headingBold,
    color: colors.black,
    fontSize: 24,
    marginTop: spacing.md,
  },
  description: {
    fontFamily: font.body,
    color: colors.black,
    fontSize: 14,
    lineHeight: 22,
    marginTop: spacing.md,
  },
  statusRow: {
    marginTop: spacing.md,
    paddingTop: spacing.md,
    borderTopWidth: 1.5,
    borderTopColor: colors.black,
    flexDirection: "row",
    justifyContent: "space-between",
  },
  statusLabel: {
    fontFamily: font.body,
    color: colors.black,
    fontSize: 12,
    opacity: 0.65,
  },
  statusValue: {
    fontFamily: font.headingBold,
    color: colors.black,
    fontSize: 12,
    textTransform: "uppercase",
  },
  filterLayer: {
    ...StyleSheet.absoluteFill,
    zIndex: 9999,
    elevation: 9999,
  },
});

import React from "react";
import { ScrollView, StyleSheet, Text, View } from "react-native";

import { ScreenShell } from "../components/ScreenShell";
import { FilterButton } from "../components/FilterButton";
import { colors, font, layout, radius, spacing } from "../theme";

export const ProfileScreen: React.FC = () => (
  <ScreenShell rightButton={<FilterButton />}>
    <ScrollView contentContainerStyle={styles.content}>
      <View style={styles.profileCard}>
        <View style={styles.avatar}>
          <Text style={styles.avatarText}>👤</Text>
        </View>

        <Text style={styles.name}>Michael</Text>
        <Text style={styles.subtitle}>GoTogether Member</Text>

        <View style={styles.statsRow}>
          <View style={styles.statBox}>
            <Text style={styles.statValue}>1240</Text>
            <Text style={styles.statLabel}>XP</Text>
          </View>

          <View style={styles.statBox}>
            <Text style={styles.statValue}>320</Text>
            <Text style={styles.statLabel}>Coins</Text>
          </View>

          <View style={styles.statBox}>
            <Text style={styles.statValue}>7</Text>
            <Text style={styles.statLabel}>Badges</Text>
          </View>
        </View>
      </View>

      <View style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Social Battery</Text>
        <Text style={styles.sectionText}>Aktuelles Energielevel: 3 / 5</Text>

        <View style={styles.batteryRow}>
          {[1, 2, 3, 4, 5].map((level) => (
            <View
              key={level}
              style={[styles.batteryDot, level <= 3 && styles.batteryDotActive]}
            />
          ))}
        </View>
      </View>

      <View style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Interessen</Text>

        <View style={styles.chipRow}>
          {["Cafe", "Outdoor", "Games", "Music", "Walking"].map((topic) => (
            <View key={topic} style={styles.chip}>
              <Text style={styles.chipText}>{topic}</Text>
            </View>
          ))}
        </View>
      </View>

      <View style={styles.sectionCard}>
        <Text style={styles.sectionTitle}>Badges</Text>

        <View style={styles.badgeRow}>
          <Text style={styles.badge}>🏆 First Raid</Text>
          <Text style={styles.badge}>☕ Coffee Connector</Text>
          <Text style={styles.badge}>🌅 Sunset Crew</Text>
        </View>
      </View>
    </ScrollView>
  </ScreenShell>
);

const styles = StyleSheet.create({
  content: {
    paddingHorizontal: layout.hMargin,
    paddingTop: spacing.lg,
    paddingBottom: spacing.xl + 120,
    gap: spacing.lg,
  },
  profileCard: {
    alignItems: "center",
    backgroundColor: colors.pink,
    borderWidth: 3,
    borderColor: colors.black,
    borderRadius: 28,
    padding: spacing.lg,
    shadowColor: colors.black,
    shadowOffset: { width: 8, height: 8 },
    shadowOpacity: 1,
    shadowRadius: 0,
    elevation: 12,
  },
  avatar: {
    width: 92,
    height: 92,
    borderRadius: radius.full,
    borderWidth: 3,
    borderColor: colors.black,
    backgroundColor: colors.white,
    alignItems: "center",
    justifyContent: "center",
    marginBottom: spacing.md,
  },
  avatarText: {
    fontSize: 42,
  },
  name: {
    fontFamily: font.headingBold,
    fontSize: 28,
    color: colors.black,
  },
  subtitle: {
    fontFamily: font.body,
    fontSize: 13,
    color: colors.black,
    opacity: 0.7,
    marginTop: spacing.xs,
  },
  statsRow: {
    flexDirection: "row",
    gap: spacing.sm,
    marginTop: spacing.lg,
  },
  statBox: {
    minWidth: 86,
    alignItems: "center",
    backgroundColor: colors.white,
    borderWidth: 2,
    borderColor: colors.black,
    borderRadius: 18,
    padding: spacing.sm,
  },
  statValue: {
    fontFamily: font.headingBold,
    fontSize: 18,
    color: colors.black,
  },
  statLabel: {
    fontFamily: font.body,
    fontSize: 11,
    color: colors.black,
    opacity: 0.65,
  },
  sectionCard: {
    backgroundColor: colors.white,
    borderWidth: 3,
    borderColor: colors.black,
    borderRadius: 24,
    padding: spacing.lg,
    shadowColor: colors.black,
    shadowOffset: { width: 6, height: 6 },
    shadowOpacity: 1,
    shadowRadius: 0,
    elevation: 8,
  },
  sectionTitle: {
    fontFamily: font.headingBold,
    fontSize: 20,
    color: colors.black,
    marginBottom: spacing.sm,
  },
  sectionText: {
    fontFamily: font.body,
    fontSize: 13,
    color: colors.black,
    opacity: 0.75,
  },
  batteryRow: {
    flexDirection: "row",
    gap: spacing.sm,
    marginTop: spacing.md,
  },
  batteryDot: {
    flex: 1,
    height: 18,
    borderRadius: radius.full,
    borderWidth: 2,
    borderColor: colors.black,
    backgroundColor: colors.gray200,
  },
  batteryDotActive: {
    backgroundColor: colors.blue,
  },
  chipRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: spacing.sm,
  },
  chip: {
    backgroundColor: colors.primary,
    borderWidth: 2,
    borderColor: colors.black,
    borderRadius: radius.full,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
  },
  chipText: {
    fontFamily: font.headingBold,
    fontSize: 12,
    color: colors.black,
  },
  badgeRow: {
    gap: spacing.sm,
  },
  badge: {
    fontFamily: font.body,
    fontSize: 14,
    color: colors.black,
  },
});

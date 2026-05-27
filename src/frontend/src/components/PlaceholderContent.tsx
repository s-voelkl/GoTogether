import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors, font, spacing, radius, layout } from '../theme';

interface PlaceholderContentProps {
  emoji: string;
  title: string;
  subtitle: string;
}

/**
 * Centered "Coming soon" body used by screens that are not yet built.
 * Lives inside ScreenShell's content cutout.
 */
export const PlaceholderContent: React.FC<PlaceholderContentProps> = ({
  emoji,
  title,
  subtitle,
}) => (
  <View style={styles.body}>
    <Text style={styles.emoji}>{emoji}</Text>
    <Text style={styles.title}>{title}</Text>
    <Text style={styles.subtitle}>{subtitle}</Text>
    <View style={styles.badge}>
      <Text style={styles.badgeText}>Coming soon</Text>
    </View>
  </View>
);

const styles = StyleSheet.create({
  body: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: spacing.sm,
  },
  emoji: { fontSize: 64, marginBottom: spacing.sm },
  title: {
    fontSize: 26,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.4,
  },
  subtitle: {
    fontSize: 14,
    fontFamily: font.body,
    color: colors.gray400,
    textAlign: 'center',
    paddingHorizontal: spacing.xl,
    lineHeight: 22,
  },
  badge: {
    marginTop: spacing.md,
    backgroundColor: colors.primary,
    borderRadius: radius.full,
    paddingVertical: 8,
    paddingHorizontal: 20,
    borderWidth: layout.border,
    borderColor: colors.black,
  },
  badgeText: {
    fontSize: 13,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
  },
});

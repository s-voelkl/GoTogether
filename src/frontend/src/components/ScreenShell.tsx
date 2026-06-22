import React from 'react';
import { View, StyleSheet, ViewStyle } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { AppHeader } from './AppHeader';
import { colors, layout, spacing, continuousRadius } from '../theme';

interface ScreenShellProps {
  /** The button shown in the header's right slot, usually <FilterButton />. */
  rightButton: React.ReactNode;

  /** Main content rendered inside the cutout. */
  children: React.ReactNode;

  /** Background color for the cutout. Defaults to white. */
  cardBackground?: string;

  /** Extra style for the content card, e.g. to disable padding. */
  cardStyle?: ViewStyle;
}

export const ScreenShell: React.FC<ScreenShellProps> = ({
  rightButton,
  children,
  cardBackground = colors.white,
  cardStyle,
}) => {
  const insets = useSafeAreaInsets();

  return (
    <View style={[styles.root, { paddingTop: insets.top }]}>
      <AppHeader rightButton={rightButton} />

      <View style={styles.cardArea}>
        <View style={[styles.contentCard, { backgroundColor: cardBackground }, cardStyle]}>
          {children}
        </View>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: colors.primary,
  },

  cardArea: {
    flex: 1,
    marginHorizontal: layout.hMargin,
    marginTop: spacing.sm,
    marginBottom: spacing.xs,
  },

  contentCard: {
    flex: 1,
    position: 'relative',
    ...continuousRadius({ borderRadius: layout.cardRadius }),
    overflow: 'hidden',
    borderWidth: layout.border,
    borderColor: colors.black,
  },
});
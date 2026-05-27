import React from 'react';
import { View, Text, StyleSheet, LayoutChangeEvent } from 'react-native';
import { colors, spacing, font } from '../theme';
import { LocationArrowIcon } from './Icons';

interface AppHeaderProps {
  rightButton: React.ReactNode;
  onLayout?: (event: LayoutChangeEvent) => void;
}

const HEADER_SIDE = 38;

export const AppHeader: React.FC<AppHeaderProps> = ({ rightButton, onLayout }) => (
  <View style={styles.header} onLayout={onLayout}>
    <View style={styles.headerTextBlock}>
      <Text style={styles.greeting}>{'Servus,\nMichael'}</Text>
      <View style={styles.locationRow}>
        <LocationArrowIcon size={15} color={colors.black} />
        <Text style={styles.locationText} numberOfLines={1}>
          Amberg, Germany
        </Text>
      </View>
    </View>
    <View style={styles.rightSlot}>{rightButton}</View>
  </View>
);

const styles = StyleSheet.create({
  header: {
    backgroundColor: colors.primary,
    paddingHorizontal: HEADER_SIDE,
    paddingTop: spacing.sm,
    // Tightened gap between the header and the content cutout below.
    // (Applies to every screen since they all use AppHeader.)
    paddingBottom: spacing.xs,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    columnGap: 12,
  },
  headerTextBlock: {
    flex: 1,
    minWidth: 0,
  },
  greeting: {
    fontSize: 32,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    lineHeight: 38,
    letterSpacing: -0.8,
  },
  locationRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 8,
    gap: 7,
    minWidth: 0,
  },
  locationText: {
    flexShrink: 1,
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
  },
  rightSlot: {
    flexShrink: 0,
    alignItems: 'center',
    justifyContent: 'center',
  },
});

import React from 'react';
import { Pressable, View, Text, StyleSheet, LayoutChangeEvent } from 'react-native';
import { useNavigation } from '@react-navigation/native';
import type { BottomTabNavigationProp } from '@react-navigation/bottom-tabs';
import { colors, spacing, font } from '../theme';
import { LocationArrowIcon } from './Icons';
import { useUserLocation } from '../context/LocationContext';
import type { RootTabParamList } from '../navigation/types';

interface AppHeaderProps {
  rightButton: React.ReactNode;
  onLayout?: (event: LayoutChangeEvent) => void;
}

const HEADER_SIDE = 38;

type AnyTabNav = BottomTabNavigationProp<RootTabParamList, keyof RootTabParamList>;

export const AppHeader: React.FC<AppHeaderProps> = ({ rightButton, onLayout }) => {
  const { status, label } = useUserLocation();
  const navigation = useNavigation<AnyTabNav>();

  const locationText =
    status === 'loading'
      ? 'Locating…'
      : status === 'unavailable'
      ? 'Location unavailable'
      : label ?? 'Current location';

  const handleHeaderPress = () => {
    navigation.navigate('Home', { recenterTs: Date.now() });
  };

  return (
    <View style={styles.header} onLayout={onLayout}>
      <Pressable style={styles.headerTextBlock} onPress={handleHeaderPress}>
        <Text style={styles.greeting}>{'Servus,\nMichael'}</Text>
        <View style={styles.locationRow}>
          <LocationArrowIcon size={15} color={colors.black} />
          <Text style={styles.locationText} numberOfLines={1}>
            {locationText}
          </Text>
        </View>
      </Pressable>
      <View style={styles.rightSlot}>{rightButton}</View>
    </View>
  );
};

const styles = StyleSheet.create({
  header: {
    backgroundColor: colors.primary,
    paddingHorizontal: HEADER_SIDE,
    paddingTop: spacing.sm,
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

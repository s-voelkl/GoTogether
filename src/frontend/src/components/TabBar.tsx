import React, { useEffect, useRef, useState } from 'react';
import { Animated, Platform, StyleSheet, TouchableOpacity, View } from 'react-native';
import { BottomTabBarProps } from '@react-navigation/bottom-tabs';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { HomeIcon, TelescopeIcon, TrophyIcon, ProfileIcon } from './Icons';
import { colors, radius, shadow, layout } from '../theme';

const ICONS: Record<string, React.FC<{ size?: number; color?: string }>> = {
  Home: HomeIcon,
  Challenges: TelescopeIcon,
  Gamification: TrophyIcon,
  Profile: ProfileIcon,
};

const PILL_PAD = 6;
const TAB_H = 44;
const INDICATOR_W = 75;
const TAB_SIDE = 38;

export const TabBar: React.FC<BottomTabBarProps> = ({ state, navigation }) => {
  const insets = useSafeAreaInsets();
  const slideX = useRef(new Animated.Value(0)).current;
  const [pillInnerW, setPillInnerW] = useState(0);

  useEffect(() => {
    if (pillInnerW === 0) return;
    const tabW = pillInnerW / state.routes.length;
    const targetX = PILL_PAD + state.index * tabW + (tabW - INDICATOR_W) / 2;
    Animated.spring(slideX, {
      toValue: targetX,
      tension: 180,
      friction: 20,
      useNativeDriver: true,
    }).start();
  }, [state.index, pillInnerW, slideX, state.routes.length]);

  return (
    <View style={[styles.container, { paddingBottom: Math.max(insets.bottom, 10) }]}>
      <View
        style={styles.pill}
        onLayout={e => {
          // Subtract padding AND border so each tab slot matches the actual flex-divided width.
          const w = e.nativeEvent.layout.width - PILL_PAD * 2 - layout.border * 2;
          if (w !== pillInnerW) setPillInnerW(w);
        }}
      >
        {pillInnerW > 0 && (
          <Animated.View style={[styles.indicator, { transform: [{ translateX: slideX }] }]} />
        )}
        {state.routes.map((route, index) => {
          const isFocused = state.index === index;
          const Icon = ICONS[route.name];
          if (!Icon) return null;

          const onPress = () => {
            const event = navigation.emit({
              type: 'tabPress',
              target: route.key,
              canPreventDefault: true,
            });
            if (!isFocused && !event.defaultPrevented) navigation.navigate(route.name);
          };

          return (
            <TouchableOpacity
              key={route.key}
              onPress={onPress}
              style={styles.tab}
              activeOpacity={0.75}
            >
              <Icon size={24} color={colors.black} />
            </TouchableOpacity>
          );
        })}
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.primary,
    paddingTop: 8,
    paddingHorizontal: TAB_SIDE,
  },
  pill: {
    flexDirection: 'row',
    backgroundColor: colors.white,
    borderRadius: radius.full,
    paddingVertical: PILL_PAD,
    paddingHorizontal: PILL_PAD,
    borderWidth: layout.border,
    borderColor: colors.black,
    alignItems: 'center',
    position: 'relative',
    ...shadow.md,
  },
  indicator: {
    position: 'absolute',
    top: PILL_PAD,
    left: 0,
    width: INDICATOR_W,
    height: TAB_H,
    borderRadius: 99,
    borderWidth: layout.border,
    borderColor: colors.black,
    ...(Platform.OS === 'ios' ? { borderCurve: 'continuous' as const } : {}),
    backgroundColor: colors.primary,
    zIndex: 0,
  },
  tab: {
    flex: 1,
    height: TAB_H,
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1,
  },
});

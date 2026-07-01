import React, { useEffect, useRef } from 'react';
import {
  Animated,
  Platform,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  ScrollView,
} from 'react-native';
import { colors, spacing, radius, font, layout, continuousRadius } from '../theme';
import { FILTER_CATEGORIES } from '../data/mockChallenges';
import { useFilters, useFilteredChallenges } from '../context/FiltersContext';

interface FilterSheetProps {
  /** Drives the fade animation. */
  open: boolean;

  /** Called from the Apply / Show button. Usually flips `open` to false. */
  onApply: () => void;
}

/**
 * FilterSheet is NOT absolutely positioned.
 * Its parent screen provides the absolute overlay layer.
 */
export const FilterSheet: React.FC<FilterSheetProps> = ({ open, onApply }) => {
  const opacity = useRef(new Animated.Value(0)).current;
  const { activeFilters, toggleCategory, clearFilters } = useFilters();
  const filtered = useFilteredChallenges();

  useEffect(() => {
    Animated.timing(opacity, {
      toValue: open ? 1 : 0,
      duration: 200,
      useNativeDriver: Platform.OS !== 'web',
    }).start();
  }, [open, opacity]);

  return (
    <Animated.View
      style={[styles.sheet, { opacity, pointerEvents: open ? 'auto' : 'none' }]}
    >
      <Text style={styles.title}>Challenges</Text>
      <Text style={styles.subtitle}>Pick one or more categories</Text>

      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.chipsBox}
        showsVerticalScrollIndicator={false}
      >
        {FILTER_CATEGORIES.map(cat => {
          const on = activeFilters.includes(cat.id);

          return (
            <TouchableOpacity
              key={cat.id}
              style={[styles.chip, on && styles.chipActive]}
              onPress={() => toggleCategory(cat.id)}
              activeOpacity={0.8}
            >
              <View style={styles.chipLeft}>
                <Text style={styles.chipEmoji}>{cat.emoji}</Text>
                <Text style={styles.chipLabel}>{cat.label}</Text>
              </View>

              <View style={[styles.checkbox, on && styles.checkboxActive]}>
                {on && <Text style={styles.checkboxMark}>✓</Text>}
              </View>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      <View style={styles.footer}>
        <TouchableOpacity
          onPress={clearFilters}
          style={styles.clearBtn}
          activeOpacity={0.75}
        >
          <Text style={styles.clearText}>Clear</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={onApply}
          style={styles.applyBtn}
          activeOpacity={0.85}
        >
          <Text style={styles.applyText}>
            Show {filtered.length} challenge{filtered.length === 1 ? '' : 's'}
          </Text>
        </TouchableOpacity>
      </View>
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  sheet: {
    flex: 1,
    width: '100%',
    height: '100%',
    ...continuousRadius({ borderRadius: layout.cardRadius }),
    backgroundColor: colors.blue,
    paddingTop: 35,
    paddingHorizontal: spacing.lg,
    paddingBottom: 22,
  },

  title: {
    fontSize: 22,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.3,
    marginBottom: 4,
  },

  subtitle: {
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.7,
    marginBottom: spacing.lg,
  },

  scroll: {
    flex: 1,
    alignSelf: 'stretch',
  },

  chipsBox: {
    gap: 10,
    paddingBottom: spacing.md,
    alignItems: 'stretch',
  },

  chip: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    backgroundColor: colors.white,
    ...continuousRadius({ borderRadius: 18 }),
    paddingVertical: 14,
    paddingHorizontal: 16,
    borderWidth: layout.border,
    borderColor: colors.black,
  },

  chipActive: {
    backgroundColor: colors.primary,
  },

  chipLeft: {
    flex: 1,
    minWidth: 0,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 12,
    marginRight: 12,
  },

  chipEmoji: {
    fontSize: 20,
  },

  chipLabel: {
    flex: 1,
    minWidth: 0,
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '600',
    color: colors.black,
  },

  checkbox: {
    width: 24,
    height: 24,
    borderRadius: 7,
    borderWidth: layout.border,
    borderColor: colors.black,
    backgroundColor: colors.white,
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0,
  },

  checkboxActive: {
    backgroundColor: colors.black,
  },

  checkboxMark: {
    color: colors.white,
    fontSize: 14,
    fontWeight: '900',
    lineHeight: 16,
  },

  footer: {
    flexDirection: 'row',
    gap: 10,
    paddingTop: spacing.sm,
  },

  clearBtn: {
    paddingVertical: 14,
    paddingHorizontal: 22,
    borderRadius: radius.full,
    backgroundColor: colors.white,
    borderWidth: layout.border,
    borderColor: colors.black,
  },

  clearText: {
    fontSize: 14,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
  },

  applyBtn: {
    flex: 1,
    paddingVertical: 14,
    borderRadius: radius.full,
    backgroundColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },

  applyText: {
    fontSize: 14,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.white,
  },
});
import { Platform } from 'react-native';

export const colors = {
  primary:    '#FAE255',   // brand yellow (Figma)
  background: '#FAE255',
  pink:       '#FEE5EA',   // marker / card accent
  blue:       '#CEF3F9',   // secondary accent
  black:      '#000000',
  white:      '#FFFFFF',
  gray100:    '#F5F5F5',
  gray200:    '#EEEEEE',
  gray300:    '#D4D4D4',
  gray400:    '#A3A3A3',
  gray500:    '#737373',
  userDot:    '#2563EB',
  userAura:   'rgba(37, 99, 235, 0.10)',
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
};

export const radius = {
  sm:   8,
  md:   12,
  lg:   20,
  xl:   28,
  full: 999,
};

/**
 * Shared layout constants used by every screen's "content cutout".
 * Keeping them here ensures the map card, placeholder cards and the
 * filter sheet stay visually aligned.
 */
export const layout = {
  hMargin: spacing.md,        // 16 — horizontal margin of the content cutout
  cardRadius: 44,             // outer radius of the content cutout
  border: 2,                  // standard outer border weight
  nearbyInset: 10,            // inset of the nearby mini-overlay inside the map card
};

// Inner radius for concentric corners: outer − inset
export const nearbyRadius = layout.cardRadius - layout.nearbyInset; // 34

export const shadow = {
  sm: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2,
  },
  md: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.10,
    shadowRadius: 12,
    elevation: 6,
  },
  lg: {
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.14,
    shadowRadius: 20,
    elevation: 12,
  },
};

// Unbounded loaded via expo-font in App.tsx
export const font = {
  heading: 'Unbounded_900Black',
  headingBold: 'Unbounded_700Bold',
  body: 'Unbounded_400Regular',
  headingFallback: Platform.select({ ios: 'Georgia', android: 'serif', default: 'serif' }),
  bodyFallback: Platform.select({ ios: 'System', android: 'Roboto', default: 'System' }),
};

/**
 * On iOS, opt-in to continuous (squircle) corners that match the system
 * curve. No-op on Android. Use as a style spread:
 *   ...continuousRadius({ borderRadius: 20 })
 */
export const continuousRadius = <T extends object>(s: T): T =>
  Platform.OS === 'ios'
    ? ({ ...s, borderCurve: 'continuous' } as T)
    : s;

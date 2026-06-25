import { Platform } from 'react-native';

export const colors = {
  primary: '#FAE255',   // brand yellow (Figma)
  blue:    '#CEF3F9',   // secondary accent
  black:   '#000000',
  white:   '#FFFFFF',

  gray100: '#F5F5F5',
  gray200: '#EEEEEE',
  gray400: '#A3A3A3',
  gray500: '#737373',

  // Centralized from component literals:
  muted:         '#888888',          // fallback when a category/difficulty color is missing
  divider:       'rgba(0,0,0,0.07)', // hairline inside info cards
  cardBorder:    'rgba(0,0,0,0.10)', // challenge card outline
  markerBorder:  'rgba(0,0,0,0.12)', // map marker outline
  handle:        '#D9D9D9',          // drag handle on the nearby overlay
  dotInactive:   '#DDDDDD',          // carousel pagination dots
  mapBackground: '#EFEFEF',          // backdrop behind the map tiles

  userDot:  '#2563EB',               // "you are here" marker
  userAura: 'rgba(37, 99, 235, 0.10)',
};

export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
};

export const radius = {
  full: 999,
};

/**
 * Shared layout constants used by every screen's "content cutout".
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
  sm: Platform.select({
    web: {
      boxShadow: '0px 1px 4px rgba(0, 0, 0, 0.08)',
    },
    default: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 1 },
      shadowOpacity: 0.08,
      shadowRadius: 4,
      elevation: 2,
    },
  }) as object,
  md: Platform.select({
    web: {
      boxShadow: '0px 4px 12px rgba(0, 0, 0, 0.10)',
    },
    default: {
      shadowColor: '#000',
      shadowOffset: { width: 0, height: 4 },
      shadowOpacity: 0.1,
      shadowRadius: 12,
      elevation: 6,
    },
  }) as object,
};

// Unbounded loaded via expo-font in App.tsx
export const font = {
  heading: 'Unbounded_900Black',
  headingBold: 'Unbounded_700Bold',
  body: 'Unbounded_400Regular',
};

/**
 * On iOS, opt-in to continuous (squircle) corners. No-op on Android.
 *   ...continuousRadius({ borderRadius: 20 })
 */
export const continuousRadius = <T extends object>(s: T): T =>
  Platform.OS === 'ios'
    ? ({ ...s, borderCurve: 'continuous' } as T)
    : s;
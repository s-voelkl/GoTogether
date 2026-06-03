import React from 'react';
import { Animated, Platform, Pressable, StyleSheet, Text, View } from 'react-native';
import { ViewAnnotation } from '@maplibre/maplibre-react-native';

import { Challenge, isChallengeFull, CATEGORY_COLORS } from '../data/mockChallenges';
import { colors, font, radius, shadow } from '../theme';
import { VenueIcon } from './Icons';

interface ChallengeMarkerProps {
  challenge: Challenge;
  selected: boolean;
  onPress: () => void;
  /** Animated zoom level from the map — drives the fade-out when zooming far out. */
  zoomAnim: Animated.Value;
}

export const ChallengeMarker = React.memo(
  ({ challenge, onPress, zoomAnim }: ChallengeMarkerProps) => {
    const { id, lat, lng, name } = challenge;
    const full = isChallengeFull(challenge);
    const accent = CATEGORY_COLORS[challenge.category] ?? colors.muted;

    // Progressive fade: full at the default zoom, dimming faster the further
    // you zoom out, and fully gone once the whole region is in view.
const opacity = zoomAnim.interpolate({
  inputRange: [10, 10.8, 11.6, 12.2, 12.8],
  outputRange: [0, 0.1, 0.35, 0.7, 1],
  extrapolate: 'clamp',
});

    return (
      <ViewAnnotation id={`marker-${id}`} lngLat={[lng, lat]} anchor="bottom">
        <Animated.View style={{ opacity }}>
          <Pressable onPress={onPress} hitSlop={6}>
            <View style={[styles.marker, full && styles.markerFull]}>
              <View
                style={[
                  styles.iconBox,
                  { backgroundColor: full ? colors.gray200 : accent + '33' },
                ]}
              >
                <VenueIcon size={14} color={full ? colors.gray500 : colors.black} />
              </View>
              <Text style={[styles.label, full && styles.labelFull]} numberOfLines={1}>
                {name}
              </Text>
            </View>
          </Pressable>
        </Animated.View>
      </ViewAnnotation>
    );
  },
);

const styles = StyleSheet.create({
  marker: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.white,
    borderRadius: radius.full,
    paddingVertical: 5,
    paddingLeft: 5,
    paddingRight: 12,
    borderWidth: 2,
    borderColor: colors.markerBorder,
    ...shadow.sm,
  },
  markerFull: { opacity: 0.85, backgroundColor: colors.gray100 },
  iconBox: {
    width: 28,
    height: 28,
    borderRadius: 999,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 8,
    ...(Platform.OS === 'ios' && { borderCurve: 'continuous' }),
  },
  label: {
    maxWidth: 110,
    fontSize: 12,
    lineHeight: 15,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.black,
  },
  labelFull: { color: colors.gray500 },
});

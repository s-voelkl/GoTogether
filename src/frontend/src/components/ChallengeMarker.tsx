import React from 'react';
import { Platform, StyleSheet, Text, View } from 'react-native';
import { ViewAnnotation } from '@maplibre/maplibre-react-native';

import { Challenge } from '../data/mockChallenges';
import { colors, font, radius, shadow } from '../theme';
import { VenueIcon } from './Icons';

interface ChallengeMarkerProps {
  challenge: Challenge;
  onPress: () => void;
}

export const ChallengeMarker = React.memo(
  ({ challenge, onPress }: ChallengeMarkerProps) => {
    const { id, lat, lng, name } = challenge;

    return (
      <ViewAnnotation
        id={`marker-${id}`}
        lngLat={[lng, lat]}
        anchor="bottom"
        onSelect={onPress}
      >
        <View style={styles.marker}>
          <View style={styles.iconBox}>
            <VenueIcon size={14} color={colors.black} />
          </View>

          <Text style={styles.label} numberOfLines={1}>
            {name}
          </Text>
        </View>
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
    borderColor: 'rgba(0,0,0,0.12)',
    ...shadow.sm,
  },

  iconBox: {
    width: 28,
    height: 28,
    borderRadius: 999,
    backgroundColor: colors.pink,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: 8,
    ...(Platform.OS === 'ios' && {
      borderCurve: 'continuous',
    }),
  },

  label: {
    maxWidth: 110,
    fontSize: 12,
    lineHeight: 15,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.black,
  },
});
import React, { useEffect, useMemo, useRef } from 'react';
import { Animated, Easing, Platform, StyleSheet, View } from 'react-native';
import { GeoJSONSource, Layer, ViewAnnotation } from '@maplibre/maplibre-react-native';
import { colors } from '../theme';

interface UserLocationMarkerProps {
  latitude: number;
  longitude: number;
  /** GPS horizontal accuracy radius in meters, if known. */
  accuracy?: number | null;
}

const EARTH_RADIUS = 6378137; // meters (WGS84)

/**
 * Build a geographic circle polygon so the accuracy ring is drawn in real-world
 * meters and scales correctly with the map zoom.
 */
function geoCircle(
  lng: number,
  lat: number,
  radiusMeters: number,
  steps = 72,
): GeoJSON.Feature {
  const coords: [number, number][] = [];
  const latRad = (lat * Math.PI) / 180;
  for (let i = 0; i <= steps; i++) {
    const theta = (i / steps) * 2 * Math.PI;
    const dx = radiusMeters * Math.cos(theta);
    const dy = radiusMeters * Math.sin(theta);
    const dLng = ((dx / (EARTH_RADIUS * Math.cos(latRad))) * 180) / Math.PI;
    const dLat = ((dy / EARTH_RADIUS) * 180) / Math.PI;
    coords.push([lng + dLng, lat + dLat]);
  }
  return {
    type: 'Feature',
    properties: {},
    geometry: { type: 'Polygon', coordinates: [coords] },
  };
}

export const UserLocationMarker: React.FC<UserLocationMarkerProps> = ({
  latitude,
  longitude,
  accuracy,
}) => {
  // Native-driven pulse loop — no re-renders, runs on the UI thread.
  const pulse = useRef(new Animated.Value(0)).current;
  useEffect(() => {
    const loop = Animated.loop(
      Animated.timing(pulse, {
        toValue: 1,
        duration: 2200,
        easing: Easing.out(Easing.ease),
        useNativeDriver: Platform.OS !== 'web',
      }),
    );
    loop.start();
    return () => loop.stop();
  }, [pulse]);

  const scale = pulse.interpolate({ inputRange: [0, 1], outputRange: [0.4, 2.8] });
  const ringOpacity = pulse.interpolate({
    inputRange: [0, 0.08, 1],
    outputRange: [0, 0.45, 0],
  });

  const circle = useMemo(
    () => (accuracy && accuracy > 0 ? geoCircle(longitude, latitude, accuracy) : null),
    [longitude, latitude, accuracy],
  );

  return (
    <>
      {circle && (
        <GeoJSONSource id="user-accuracy-source" data={circle}>
          <Layer
            id="user-accuracy-fill"
            type="fill"
            paint={{ 'fill-color': colors.userDot, 'fill-opacity': 0.1 }}
          />
          <Layer
            id="user-accuracy-line"
            type="line"
            paint={{ 'line-color': colors.userDot, 'line-width': 1.5, 'line-opacity': 0.45 }}
          />
        </GeoJSONSource>
      )}

      <ViewAnnotation id="user-location" lngLat={[longitude, latitude]} anchor="center">
        <View style={[styles.wrap, { pointerEvents: 'none' }]}>
          <Animated.View
            style={[styles.pulse, { opacity: ringOpacity, transform: [{ scale }] }]}
          />
          <View style={styles.dot} />
        </View>
      </ViewAnnotation>
    </>
  );
};

const DOT = 22;
const RING = 30;

const styles = StyleSheet.create({
  // Large enough that the expanding pulse isn't clipped by the annotation bounds.
  wrap: {
    width: 100,
    height: 100,
    alignItems: 'center',
    justifyContent: 'center',
  },
  pulse: {
    position: 'absolute',
    width: RING,
    height: RING,
    borderRadius: RING / 2,
    backgroundColor: colors.userDot,
  },
  dot: {
    width: DOT,
    height: DOT,
    borderRadius: DOT / 2,
    backgroundColor: colors.userDot,
    borderWidth: 3,
    borderColor: colors.white,
    ...(Platform.select({
      web: {
        boxShadow: '0px 1px 2px rgba(0, 0, 0, 0.25)',
      },
      default: {
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.25,
        shadowRadius: 2,
        elevation: 3,
      },
    }) as object),
  },
});

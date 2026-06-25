import React, { useCallback, useEffect, useRef, useState } from 'react';
import Constants from 'expo-constants';
import { Animated, StyleSheet, View } from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { NearbyOverlay, NearbyOverlayHandle } from '../components/NearbyOverlay';
import { PlaceholderContent } from '../components/PlaceholderContent';

import { useFilteredChallenges, useFilters } from '../context/FiltersContext';
import { useUserLocation } from '../context/LocationContext';
import { mockChallenges, Challenge } from '../data/mockChallenges';
import { TabNavigationProp, TabRouteProp } from '../navigation/types';
import { colors } from '../theme';

const POSITRON_STYLE_URL = 'https://tiles.openfreemap.org/styles/positron';

const AMBERG_CENTER: [number, number] = [11.862, 49.4422];

const DEFAULT_ZOOM = 12.6;
const CHALLENGE_ZOOM = 14.4;

const PANEL_LAT_OFFSET = 0.0015;
const USER_PANEL_LAT_OFFSET = 0.0065;

const isExpoGo = Constants.executionEnvironment === 'storeClient';

type MapLibreModule = typeof import('@maplibre/maplibre-react-native');

const mapLibreModule: MapLibreModule | null = isExpoGo
  ? null
  : require('@maplibre/maplibre-react-native');

const MapView = mapLibreModule?.Map ?? null;
const CameraView = mapLibreModule?.Camera ?? null;

const mapComponents = isExpoGo
  ? null
  : {
      ChallengeMarker: require('../components/ChallengeMarker').ChallengeMarker,
      UserLocationMarker: require('../components/UserLocationMarker')
        .UserLocationMarker,
    };

const ChallengeMarker = mapComponents?.ChallengeMarker ?? null;
const UserLocationMarker = mapComponents?.UserLocationMarker ?? null;

export const HomeScreen: React.FC = () => {
  const navigation = useNavigation<TabNavigationProp<'Home'>>();
  const route = useRoute<TabRouteProp<'Home'>>();

  const cameraRef = useRef<any>(null);
  const nearbyRef = useRef<NearbyOverlayHandle>(null);

  const filtered = useFilteredChallenges();
  const { clearFilters } = useFilters();
  const { coords, accuracy } = useUserLocation();

  const coordsRef = useRef(coords);
  coordsRef.current = coords;

  const [filterOpen, setFilterOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const zoomAnim = useRef(new Animated.Value(DEFAULT_ZOOM)).current;

  const handleRegionChange = useCallback(
    (e: any) => {
      const zoom = e?.nativeEvent?.zoom;
      if (typeof zoom === 'number') zoomAnim.setValue(zoom);
    },
    [zoomAnim],
  );

  const toggleFilter = useCallback(() => setFilterOpen(prev => !prev), []);
  const closeFilter = useCallback(() => setFilterOpen(false), []);

  const animateCameraTo = useCallback(
    (center: [number, number], zoom: number, duration = 700) => {
      const cam = cameraRef.current;
      if (!cam) return;

      if (typeof cam.flyTo === 'function') {
        cam.flyTo({ center, zoom, duration });
      } else if (typeof cam.setStop === 'function') {
        cam.setStop({ center, zoom, easing: 'fly', duration });
      }
    },
    [],
  );

  const moveCameraToUserLocation = useCallback(
    (duration = 800) => {
      const c = coordsRef.current;

      const center: [number, number] = c
        ? [c.longitude, c.latitude - USER_PANEL_LAT_OFFSET]
        : AMBERG_CENTER;

      animateCameraTo(center, DEFAULT_ZOOM, duration);
    },
    [animateCameraTo],
  );

  const moveCameraTo = useCallback(
    (challenge: Challenge) => {
      animateCameraTo(
        [challenge.lng, challenge.lat - PANEL_LAT_OFFSET],
        CHALLENGE_ZOOM,
        650,
      );
    },
    [animateCameraTo],
  );

  const focusChallenge = useCallback(
    (challenge: Challenge, index: number, syncCarousel = true) => {
      setSelectedId(challenge.id);
      moveCameraTo(challenge);

      if (syncCarousel && index >= 0) {
        nearbyRef.current?.scrollToIndex(index);
      }
    },
    [moveCameraTo],
  );

  const handleMarkerPress = useCallback(
    (challenge: Challenge) => {
      const index = filtered.findIndex(c => c.id === challenge.id);
      focusChallenge(challenge, index, true);
    },
    [filtered, focusChallenge],
  );

  const handleCarouselIndexChange = useCallback(
    (index: number, challenge: Challenge) => {
      focusChallenge(challenge, index, false);
    },
    [focusChallenge],
  );

  const handleCardPress = useCallback(
    (challenge: Challenge) => {
      navigation.navigate('Challenges', {
        selectedChallengeId: challenge.id,
        selectedTs: Date.now(),
      });
    },
    [navigation],
  );

  useEffect(() => {
    if (filtered.length === 0) {
      setSelectedId(null);
      return;
    }

    const selectedStillVisible =
      selectedId && filtered.some(c => c.id === selectedId);

    if (!selectedStillVisible) {
      setSelectedId(filtered[0].id);
      nearbyRef.current?.scrollToIndex(0);
    }
  }, [filtered, selectedId]);

  const didInitialCenter = useRef(false);

  useEffect(() => {
    if (isExpoGo || didInitialCenter.current || !coords) return;
    if (!cameraRef.current) return;

    didInitialCenter.current = true;
    moveCameraToUserLocation(900);
  }, [coords, moveCameraToUserLocation]);

  const focusId = route.params?.focusChallengeId;
  const focusTs = route.params?.focusTs;

  useEffect(() => {
    if (!focusId) return;

    const target = mockChallenges.find(c => c.id === focusId);
    if (!target) return;

    const idx = filtered.findIndex(c => c.id === focusId);

    if (idx === -1) {
      clearFilters();
      return;
    }

    focusChallenge(target, idx, true);
  }, [focusId, focusTs, filtered, clearFilters, focusChallenge]);

  const recenterTs = route.params?.recenterTs;

  useEffect(() => {
    if (isExpoGo || !recenterTs) return;
    moveCameraToUserLocation(800);
  }, [recenterTs, moveCameraToUserLocation]);

  useEffect(() => {
    const unsub = navigation.addListener('blur', () => setFilterOpen(false));
    return unsub;
  }, [navigation]);

  return (
    <ScreenShell
      rightButton={<FilterButton open={filterOpen} onPress={toggleFilter} />}
      cardBackground={colors.mapBackground}
      cardStyle={styles.contentCard}
    >
      {isExpoGo || !MapView || !CameraView || !ChallengeMarker || !UserLocationMarker ? (
        <View style={styles.contentHost}>
          <PlaceholderContent
            emoji="🗺️"
            title="Map needs a dev build"
            subtitle="Expo Go does not include the MapLibre native module. Use an Android development build to enable the live map, and keep browsing nearby challenges below in Expo Go."
          />
        </View>
      ) : (
        <View collapsable={false} style={styles.mapHost}>
          <MapView
            style={styles.map}
            mapStyle={POSITRON_STYLE_URL}
            attribution={false}
            logo={false}
            compass={false}
            scaleBar={false}
            touchRotate={false}
            touchPitch={false}
            onRegionIsChanging={handleRegionChange}
            onRegionDidChange={handleRegionChange}
          >
            <CameraView
              ref={cameraRef}
              initialViewState={{
                center: AMBERG_CENTER,
                zoom: DEFAULT_ZOOM,
              }}
            />

            {coords && (
              <UserLocationMarker
                latitude={coords.latitude}
                longitude={coords.longitude}
                accuracy={accuracy}
              />
            )}

            {filtered.map(challenge => (
              <ChallengeMarker
                key={challenge.id}
                challenge={challenge}
                selected={selectedId === challenge.id}
                zoomAnim={zoomAnim}
                onPress={() => handleMarkerPress(challenge)}
              />
            ))}
          </MapView>
        </View>
      )}

      <NearbyOverlay
        ref={nearbyRef}
        challenges={filtered}
        onIndexChange={handleCarouselIndexChange}
        onCardPress={handleCardPress}
      />

      <View
        pointerEvents={filterOpen ? 'auto' : 'none'}
        style={styles.filterLayer}
      >
        <FilterSheet open={filterOpen} onApply={closeFilter} />
      </View>
    </ScreenShell>
  );
};

const styles = StyleSheet.create({
  contentCard: {
    padding: 0,
  },

  contentHost: {
    flex: 1,
    alignItems: 'stretch',
    justifyContent: 'center',
  },

  mapHost: {
    flex: 1,
  },

  map: {
    flex: 1,
  },

  filterLayer: {
    position: 'absolute',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
    zIndex: 9999,
    elevation: 9999,
  },
});
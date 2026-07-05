import React, { useCallback, useEffect, useRef, useState } from 'react';
import { Animated, StyleSheet, Text, View } from 'react-native';
import { Camera, Map } from '@maplibre/maplibre-react-native';
import { useNavigation, useRoute } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { ChallengeMarker } from '../components/ChallengeMarker';
import { UserLocationMarker } from '../components/UserLocationMarker';
import { NearbyOverlay, NearbyOverlayHandle } from '../components/NearbyOverlay';

import { useFilteredChallenges, useFilters } from '../context/FiltersContext';
import { useUserLocation } from '../context/LocationContext';
import { mockChallenges, Challenge } from '../data/mockChallenges';
import { TabNavigationProp, TabRouteProp } from '../navigation/types';
import { colors, continuousRadius, font, layout } from '../theme';

const POSITRON_STYLE_URL = 'https://tiles.openfreemap.org/styles/positron';

const AMBERG_CENTER: [number, number] = [11.862, 49.4422];

const DEFAULT_ZOOM = 12.6;
const CHALLENGE_ZOOM = 14.4;

const PANEL_LAT_OFFSET = 0.0015;
const USER_PANEL_LAT_OFFSET = 0.0065;
const PLACEHOLDER_USER_INTEREST_IDS = ['Food', 'Culture'];

interface AssistantGreeting {
  message: string;
  matchedInterestIds: string[];
  suggestedChallenge: Challenge | null;
}

function buildAssistantGreeting(
  matchedInterestIds: string[],
  challenges: Challenge[],
): AssistantGreeting {
  const normalizedInterestIds = matchedInterestIds.filter(Boolean);
  const suggestedChallenge =
    normalizedInterestIds.length > 0
      ? challenges.find(challenge => normalizedInterestIds.includes(challenge.category))
        ?? null
      : null;

  return {
    message:
      'TBD: Basierend auf deinen Interessen haben wir heute eine passende Challenge fuer dich gefunden.',
    matchedInterestIds: normalizedInterestIds,
    suggestedChallenge,
  };
}

export const HomeScreen: React.FC = () => {
  const navigation = useNavigation<TabNavigationProp<'Home'>>();
  const route = useRoute<TabRouteProp<'Home'>>();

  const cameraRef = useRef<any>(null);
  const nearbyRef = useRef<NearbyOverlayHandle>(null);

  const filtered = useFilteredChallenges();
  const { clearFilters } = useFilters();
  const { coords, accuracy } = useUserLocation();
  const assistantGreeting = buildAssistantGreeting(
    PLACEHOLDER_USER_INTEREST_IDS,
    mockChallenges,
  );

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

      animateCameraTo(center, c ? DEFAULT_ZOOM : DEFAULT_ZOOM, duration);
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
    if (didInitialCenter.current || !coords) return;
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
    if (!recenterTs) return;
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
      cardStyle={styles.mapCard}
    >
      <View collapsable={false} style={styles.mapHost}>
        <View pointerEvents="none" style={styles.greetingWrap}>
          <View style={styles.greetingCard}>
            <Text style={styles.greetingLabel}>KI-Nachricht</Text>
            <Text style={styles.greetingText}>{assistantGreeting.message}</Text>
            {assistantGreeting.suggestedChallenge && (
              <Text style={styles.greetingHint}>
                Passender Vorschlag: {assistantGreeting.suggestedChallenge.name}
              </Text>
            )}
          </View>
        </View>

        <Map
          androidView="texture"
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
          <Camera
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
        </Map>
      </View>

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
  mapCard: {
    padding: 0,
  },

  mapHost: {
    flex: 1,
  },

  greetingWrap: {
    position: 'absolute',
    top: 16,
    left: 16,
    right: 16,
    zIndex: 10,
  },

  greetingCard: {
    backgroundColor: colors.primary,
    ...continuousRadius({ borderRadius: 24 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 14,
    paddingHorizontal: 16,
  },

  greetingLabel: {
    fontSize: 12,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
    marginBottom: 6,
  },

  greetingText: {
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    lineHeight: 20,
  },

  greetingHint: {
    fontSize: 12,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.7,
    marginTop: 8,
    lineHeight: 18,
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
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import {
  Camera,
  Map,
  UserLocation,
} from '@maplibre/maplibre-react-native';
import { useNavigation } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { ChallengeMarker } from '../components/ChallengeMarker';
import {
  NearbyOverlay,
  NearbyOverlayHandle,
} from '../components/NearbyOverlay';

import { useFilteredChallenges } from '../context/FiltersContext';
import { Challenge } from '../data/mockChallenges';
import { TabNavigationProp } from '../navigation/types';

const POSITRON_STYLE_URL = 'https://tiles.openfreemap.org/styles/positron';

const AMBERG_CENTER: [number, number] = [11.862, 49.4422];
const DEFAULT_ZOOM = 12.6;

// Shift camera slightly south so selected marker appears above the bottom overlay.
const PANEL_LAT_OFFSET = 0.0015;

export const HomeScreen: React.FC = () => {
  const navigation = useNavigation<TabNavigationProp<'Home'>>();

  const cameraRef = useRef<any>(null);
  const nearbyRef = useRef<NearbyOverlayHandle>(null);

  const filtered = useFilteredChallenges();

  const [filterOpen, setFilterOpen] = useState(false);
  const [selectedId, setSelectedId] = useState<string | null>(null);

  const toggleFilter = useCallback(() => {
    setFilterOpen(prev => !prev);
  }, []);

  const closeFilter = useCallback(() => {
    setFilterOpen(false);
  }, []);

  const moveCameraTo = useCallback((challenge: Challenge) => {
    const center: [number, number] = [
      challenge.lng,
      challenge.lat - PANEL_LAT_OFFSET,
    ];

    if (cameraRef.current?.setCamera) {
      cameraRef.current.setCamera({
        centerCoordinate: center,
        zoomLevel: 14.4,
        animationMode: 'flyTo',
        animationDuration: 650,
      });
      return;
    }

    cameraRef.current?.setStop?.({
      center,
      zoom: 14.4,
      easing: 'fly',
      duration: 650,
    });
  }, []);

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

  return (
    <ScreenShell
      rightButton={
        <FilterButton open={filterOpen} onPress={toggleFilter} />
      }
      cardBackground="#efefef"
      cardStyle={styles.mapCard}
    >
      <View collapsable={false} style={styles.mapHost}>
        <Map
          style={styles.map}
          mapStyle={POSITRON_STYLE_URL}
          attribution={false}
          logo={false}
          compass={false}
          scaleBar={false}
          touchRotate={false}
          touchPitch={false}
        >
          <Camera
            ref={cameraRef}
            center={AMBERG_CENTER}
            zoom={DEFAULT_ZOOM}
          />

          <UserLocation visible />

          {filtered.map(challenge => (
            <ChallengeMarker
              key={challenge.id}
              challenge={challenge}
              selected={selectedId === challenge.id}
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
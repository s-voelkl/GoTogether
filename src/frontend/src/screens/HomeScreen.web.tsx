import React, { useCallback, useEffect, useRef, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { useNavigation } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { NearbyOverlay, NearbyOverlayHandle } from '../components/NearbyOverlay';
import { PlaceholderContent } from '../components/PlaceholderContent';

import { useFilteredChallenges } from '../context/FiltersContext';
import { Challenge } from '../data/mockChallenges';
import { TabNavigationProp } from '../navigation/types';
import { colors } from '../theme';

export const HomeScreen: React.FC = () => {
  const navigation = useNavigation<TabNavigationProp<'Home'>>();
  const nearbyRef = useRef<NearbyOverlayHandle>(null);

  const filtered = useFilteredChallenges();
  const [filterOpen, setFilterOpen] = useState(false);

  const toggleFilter = useCallback(() => setFilterOpen(prev => !prev), []);
  const closeFilter = useCallback(() => setFilterOpen(false), []);

  const handleCardPress = useCallback(
    (challenge: Challenge) => {
      navigation.navigate('Challenges', {
        selectedChallengeId: challenge.id,
        selectedTs: Date.now(),
      });
    },
    [navigation],
  );

  const handleCarouselIndexChange = useCallback(
    (index: number) => {
      nearbyRef.current?.scrollToIndex(index);
    },
    [],
  );

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
      <View style={styles.contentHost}>
        <PlaceholderContent
          emoji="🗺️"
          title="Map on native devices"
          subtitle="The live map uses MapLibre and is available on iOS and Android. You can still browse nearby challenges on web below."
        />
      </View>

      <NearbyOverlay
        ref={nearbyRef}
        challenges={filtered}
        onIndexChange={handleCarouselIndexChange}
        onCardPress={handleCardPress}
      />

      <View
        style={[styles.filterLayer, { pointerEvents: filterOpen ? 'auto' : 'none' }]}
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

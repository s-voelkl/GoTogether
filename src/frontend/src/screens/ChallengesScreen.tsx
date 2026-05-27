import React, { useCallback, useState } from 'react';
import { StyleSheet, View } from 'react-native';
import { useRoute } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { PlaceholderContent } from '../components/PlaceholderContent';
import { TabRouteProp } from '../navigation/types';

export const ChallengesScreen: React.FC = () => {
  const route = useRoute<TabRouteProp<'Challenges'>>();
  const [filterOpen, setFilterOpen] = useState(false);

  // Hand-off param from HomeScreen. Kept for future detail view.
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const _selectedChallengeId = route.params?.selectedChallengeId;

  const toggleFilter = useCallback(() => {
    setFilterOpen(prev => !prev);
  }, []);

  const closeFilter = useCallback(() => {
    setFilterOpen(false);
  }, []);

  return (
    <ScreenShell
      rightButton={
        <FilterButton open={filterOpen} onPress={toggleFilter} />
      }
    >
      <PlaceholderContent
        emoji="🔭"
        title="Challenges"
        subtitle="List view · Detail view · QR scanner"
      />

      <View
        style={styles.filterLayer}
        pointerEvents={filterOpen ? 'auto' : 'none'}
      >
        <FilterSheet open={filterOpen} onApply={closeFilter} />
      </View>
    </ScreenShell>
  );
};

const styles = StyleSheet.create({
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
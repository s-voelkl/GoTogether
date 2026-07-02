import React, { useCallback, useEffect, useState } from 'react';
import { FlatList, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
import { useNavigation, useRoute } from '@react-navigation/native';

import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { FilterSheet } from '../components/FilterSheet';
import { ChallengeCard } from '../components/ChallengeCard';
import { ChallengeDetail } from '../components/ChallengeDetail';
import { CheckInSheet } from '../components/CheckInSheet';
import { FadeEdge } from '../components/FadeEdge';
import { ScanIcon } from '../components/Icons';

import { useFilteredChallenges } from '../context/FiltersContext';
import { mockChallenges, Challenge } from '../data/mockChallenges';
import { TabNavigationProp, TabRouteProp } from '../navigation/types';
import { colors, font, layout, continuousRadius } from '../theme';

export const ChallengesScreen: React.FC = () => {
  const navigation = useNavigation<TabNavigationProp<'Challenges'>>();
  const route = useRoute<TabRouteProp<'Challenges'>>();

  const filtered = useFilteredChallenges();

  const [filterOpen, setFilterOpen] = useState(false);
  const [checkInOpen, setCheckInOpen] = useState(false);
  const [detailOpen, setDetailOpen] = useState(false);
  const [detailInstantOpen, setDetailInstantOpen] = useState(false);
  const [selected, setSelected] = useState<Challenge | null>(null);

  const anyOverlayOpen = filterOpen || checkInOpen || detailOpen;

  const onHeaderRight = useCallback(() => {
    if (checkInOpen) {
      setCheckInOpen(false);
      return;
    }

    if (detailOpen) {
      setDetailOpen(false);
      setDetailInstantOpen(false);
      return;
    }

    if (filterOpen) {
      setFilterOpen(false);
      return;
    }

    setFilterOpen(true);
  }, [checkInOpen, detailOpen, filterOpen]);

  const openDetail = useCallback((challenge: Challenge) => {
    setFilterOpen(false);
    setCheckInOpen(false);
    setDetailInstantOpen(false);
    setSelected(challenge);
    setDetailOpen(true);
  }, []);

  const openCheckIn = useCallback(() => {
    setFilterOpen(false);
    setDetailOpen(false);
    setDetailInstantOpen(false);
    setCheckInOpen(true);
  }, []);

  const showOnMap = useCallback(
    (challenge: Challenge) => {
      setDetailOpen(false);
      setDetailInstantOpen(false);
      setCheckInOpen(false);
      setFilterOpen(false);

      navigation.navigate('Home', {
        focusChallengeId: challenge.id,
        focusTs: Date.now(),
      });
    },
    [navigation],
  );

  const handoffId = route.params?.selectedChallengeId;
  const handoffTs = route.params?.selectedTs;

  useEffect(() => {
    if (!handoffId) return;

    const match = mockChallenges.find(c => c.id === handoffId);

    if (match) {
      setFilterOpen(false);
      setCheckInOpen(false);
      setDetailInstantOpen(true);
      setSelected(match);
      setDetailOpen(true);
    }
  }, [handoffId, handoffTs]);

  useEffect(() => {
    const unsub = navigation.addListener('blur', () => {
      setFilterOpen(false);
      setCheckInOpen(false);
      setDetailOpen(false);
      setDetailInstantOpen(false);
    });

    return unsub;
  }, [navigation]);

  return (
    <ScreenShell
      rightButton={<FilterButton open={anyOverlayOpen} onPress={onHeaderRight} />}
    >
      <View style={styles.scanRow}>
        <TouchableOpacity
          style={styles.scanBtn}
          onPress={openCheckIn}
          activeOpacity={0.85}
        >
          <View style={styles.scanIconBox}>
            <ScanIcon size={26} color={colors.black} />
          </View>

          <View style={styles.scanTextBox}>
            <Text style={styles.scanTitle}>Scan to check in</Text>
            <Text style={styles.scanSub}>QR code or enter it manually</Text>
          </View>
        </TouchableOpacity>

        <View style={[styles.scanFade, { pointerEvents: 'none' }]}>
          <FadeEdge edge="top" color={colors.white} height={18} />
        </View>
      </View>

      <FlatList
        data={filtered}
        keyExtractor={item => item.id}
        contentContainerStyle={styles.listContent}
        ItemSeparatorComponent={() => <View style={{ height: 12 }} />}
        showsVerticalScrollIndicator={false}
        ListHeaderComponent={
          <Text style={styles.listTitle}>
            All Challenges <Text style={styles.count}>({filtered.length})</Text>
          </Text>
        }
        ListEmptyComponent={
          <View style={styles.empty}>
            <Text style={styles.emptyEmoji}>🔍</Text>
            <Text style={styles.emptyText}>No challenges match your filters.</Text>
          </View>
        }
        renderItem={({ item }) => (
          <ChallengeCard
            challenge={item}
            onPress={() => openDetail(item)}
          />
        )}
      />

      <View
        style={[
          styles.overlay,
          { zIndex: 1000, elevation: 1000, pointerEvents: detailOpen ? 'auto' : 'none' },
        ]}
      >
        {selected && (
          <ChallengeDetail
            open={detailOpen}
            challenge={selected}
            onShowOnMap={showOnMap}
            disableOpenAnimation={detailInstantOpen}
          />
        )}
      </View>

      <View
        style={[
          styles.overlay,
          { zIndex: 2000, elevation: 2000, pointerEvents: checkInOpen ? 'auto' : 'none' },
        ]}
      >
        <CheckInSheet
          open={checkInOpen}
          onClose={() => setCheckInOpen(false)}
        />
      </View>

      <View
        style={[
          styles.overlay,
          { zIndex: 9999, elevation: 9999, pointerEvents: filterOpen ? 'auto' : 'none' },
        ]}
      >
        <FilterSheet
          open={filterOpen}
          onApply={() => setFilterOpen(false)}
        />
      </View>
    </ScreenShell>
  );
};

const styles = StyleSheet.create({
  scanRow: {
    paddingHorizontal: 16,
    paddingTop: 18,
    paddingBottom: 6,
    zIndex: 2,
  },

  scanFade: {
    position: 'absolute',
    left: 0,
    right: 0,
    bottom: -18,
    height: 18,
  },

  scanBtn: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 14,
    backgroundColor: colors.blue,
    ...continuousRadius({ borderRadius: 28 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 14,
    paddingHorizontal: 16,
  },

  scanIconBox: {
    width: 44,
    height: 44,
    ...continuousRadius({ borderRadius: 14 }),
    backgroundColor: colors.white,
    borderWidth: layout.border,
    borderColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },

  scanTextBox: {
    flex: 1,
    minWidth: 0,
  },

  scanTitle: {
    fontSize: 15,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  scanSub: {
    fontSize: 12,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.65,
    marginTop: 2,
  },

  listContent: {
    paddingHorizontal: 16,
    paddingTop: 8,
    paddingBottom: 24,
  },

  listTitle: {
    fontSize: 16,
    fontFamily: font.headingBold,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.3,
    marginBottom: 12,
  },

  count: {
    color: colors.gray500,
  },

  empty: {
    alignItems: 'center',
    paddingTop: 48,
    gap: 10,
  },

  emptyEmoji: {
    fontSize: 44,
  },

  emptyText: {
    fontSize: 14,
    fontFamily: font.body,
    color: colors.gray500,
  },

  overlay: {
    position: 'absolute',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
  },
});
import React, {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useRef,
  useState,
} from 'react';
import {
  NativeScrollEvent,
  NativeSyntheticEvent,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import {
  colors,
  continuousRadius,
  font,
  layout,
  nearbyRadius,
  shadow,
  spacing,
} from '../theme';
import { Challenge } from '../data/mockChallenges';
import { ChallengeCard } from './ChallengeCard';

export interface NearbyOverlayHandle {
  /** Programmatically scroll the carousel to the given index. */
  scrollToIndex: (index: number) => void;
}

interface NearbyOverlayProps {
  challenges: Challenge[];
  /** Fires when the carousel settles on a new card. */
  onIndexChange: (index: number, challenge: Challenge) => void;
  /** Fires when the user taps a card. */
  onCardPress: (challenge: Challenge) => void;
}

/**
 * The white card that floats at the bottom of the map.
 * It stays mounted/visible even when the filter sheet is open;
 * the filter sheet simply overlays it.
 */
export const NearbyOverlay = forwardRef<NearbyOverlayHandle, NearbyOverlayProps>(
  ({ challenges, onIndexChange, onCardPress }, ref) => {
    const scrollRef = useRef<ScrollView>(null);

    const [carouselWidth, setCarouselWidth] = useState(0);
    const [activeIdx, setActiveIdx] = useState(0);

    useEffect(() => {
      if (challenges.length === 0) {
        setActiveIdx(0);
        return;
      }

      if (activeIdx >= challenges.length) {
        setActiveIdx(0);
        requestAnimationFrame(() => {
          scrollRef.current?.scrollTo({ x: 0, animated: false });
        });
      }
    }, [activeIdx, challenges.length]);

    useImperativeHandle(
      ref,
      () => ({
        scrollToIndex: (index: number) => {
          if (carouselWidth <= 0) return;
          if (index < 0 || index >= challenges.length) return;

          scrollRef.current?.scrollTo({
            x: index * carouselWidth,
            animated: true,
          });

          setActiveIdx(index);
        },
      }),
      [carouselWidth, challenges.length],
    );

    const onSettle = useCallback(
      (e: NativeSyntheticEvent<NativeScrollEvent>) => {
        if (carouselWidth === 0) return;

        const idx = Math.round(e.nativeEvent.contentOffset.x / carouselWidth);

        if (idx === activeIdx) return;
        if (idx < 0 || idx >= challenges.length) return;

        const challenge = challenges[idx];
        if (!challenge) return;

        setActiveIdx(idx);
        onIndexChange(idx, challenge);
      },
      [activeIdx, carouselWidth, challenges, onIndexChange],
    );

    if (challenges.length === 0) {
      return null;
    }

    return (
      <View style={styles.wrap} pointerEvents="box-none">
        <View style={styles.card}>
          <View style={styles.handle} />

          <View style={styles.headerRow}>
            <Text style={styles.title}>
              Nearby Challenges <Text style={styles.count}>({challenges.length})</Text>
            </Text>
          </View>

          <View onLayout={e => setCarouselWidth(e.nativeEvent.layout.width)}>
            {carouselWidth > 0 && (
              <ScrollView
                ref={scrollRef}
                horizontal
                pagingEnabled
                showsHorizontalScrollIndicator={false}
                decelerationRate="fast"
                onMomentumScrollEnd={onSettle}
                onScrollEndDrag={onSettle}
                scrollEventThrottle={16}
              >
                {challenges.map(challenge => (
                  <ChallengeCard
                    key={challenge.id}
                    challenge={challenge}
                    width={carouselWidth}
                    onPress={() => onCardPress(challenge)}
                  />
                ))}
              </ScrollView>
            )}

            {challenges.length > 1 && (
              <View style={styles.dotsRow}>
                {challenges.map((_, index) => (
                  <View key={index} style={styles.dotSlot}>
                    <View
                      style={[
                        styles.dot,
                        index === activeIdx && styles.dotActive,
                      ]}
                    />
                  </View>
                ))}
              </View>
            )}
          </View>
        </View>
      </View>
    );
  },
);

NearbyOverlay.displayName = 'NearbyOverlay';

const styles = StyleSheet.create({
  wrap: {
    position: 'absolute',
    left: layout.nearbyInset,
    right: layout.nearbyInset,
    bottom: layout.nearbyInset,
    zIndex: 10,
    elevation: 10,
  },

  card: {
    backgroundColor: colors.white,
    ...continuousRadius({ borderRadius: nearbyRadius }),
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingTop: 10,
    paddingBottom: 16,
    ...shadow.md,
  },

  handle: {
    width: 36,
    height: 4,
    backgroundColor: colors.handle,
    borderRadius: 99,
    alignSelf: 'center',
    marginBottom: 10,
  },

  headerRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: spacing.lg,
    marginBottom: 12,
  },

  title: {
    fontSize: 16,
    fontFamily: font.headingBold,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.3,
  },

  dotsRow: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 10,
  },

  dotSlot: {
    width: 18,
    height: 8,
    alignItems: 'center',
    justifyContent: 'center',
  },

  dot: {
    width: 7,
    height: 7,
    borderRadius: 99,
    backgroundColor: colors.dotInactive,
  },

  dotActive: {
    width: 18,
    backgroundColor: colors.black,
  },

  count: { color: colors.gray500 },
});

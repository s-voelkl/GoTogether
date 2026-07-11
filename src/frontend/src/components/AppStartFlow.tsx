import React, { useMemo, useState } from 'react';
import {
  LayoutChangeEvent,
  Modal,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

import { ChallengeCard } from './ChallengeCard';
import { Challenge, isChallengeFull, mockChallenges } from '../data/mockChallenges';
import {
  colors,
  continuousRadius,
  font,
  layout,
  radius,
  shadow,
  spacing,
} from '../theme';

const BATTERY_STEPS = [0, 20, 40, 60, 80, 100] as const;
const DEFAULT_BATTERY = 60 as const;
const PLACEHOLDER_INTERESTS = ['Food', 'Culture'] as const;
const THUMB_SIZE = 28;

type BatteryStep = (typeof BATTERY_STEPS)[number];
type FlowStep = 'battery' | 'assistant' | 'done';

const BATTERY_HEADLINES: Record<BatteryStep, string> = {
  0: 'Keep it calm',
  20: 'Low-key mode',
  40: 'Light plans fit',
  60: 'Balanced energy',
  80: 'Group-ready mood',
  100: 'Big plans welcome',
};

const BATTERY_MESSAGES: Record<BatteryStep, string> = {
  0: 'Very low energy today. Keep it calm.',
  20: 'Low-key plans are best today.',
  40: 'Light social activities could work.',
  60: 'Balanced energy for casual meetups.',
  80: 'Open for group activities.',
  100: 'Ready for big social plans.',
};

interface AppStartFlowProps {
  onOpenChallenge: (challengeId: string) => void;
}

const clamp = (value: number, min: number, max: number) =>
  Math.min(max, Math.max(min, value));

const getClosestBatteryStep = (value: number): BatteryStep =>
  BATTERY_STEPS.reduce<BatteryStep>((closest, step) => {
    return Math.abs(step - value) < Math.abs(closest - value) ? step : closest;
  }, BATTERY_STEPS[0]);

const toBatteryLevel = (value: BatteryStep) => Math.max(1, Math.ceil(value / 20));

const pickRecommendedChallenge = (socialBattery: BatteryStep): Challenge | null => {
  const availableChallenges = mockChallenges.filter(challenge => !isChallengeFull(challenge));
  const supportedBatteryLevel = toBatteryLevel(socialBattery);
  const interestMatches = availableChallenges.filter(challenge =>
    PLACEHOLDER_INTERESTS.some(interest => interest === challenge.category),
  );

  return (
    interestMatches.find(challenge => challenge.minSocialBattery <= supportedBatteryLevel)
    ?? availableChallenges.find(challenge => challenge.minSocialBattery <= supportedBatteryLevel)
    ?? interestMatches[0]
    ?? availableChallenges[0]
    ?? null
  );
};

const buildAssistantMessage = (recommendedChallenge: Challenge | null) => {
  const interestText = PLACEHOLDER_INTERESTS.join(' and ');

  if (recommendedChallenge) {
    return `TBD: Based on your interests in ${interestText}, ${recommendedChallenge.name} could fit your day.`;
  }

  return `TBD: Based on your interests in ${interestText}, we found a challenge that could fit your day.`;
};

export const AppStartFlow: React.FC<AppStartFlowProps> = ({ onOpenChallenge }) => {
  const [step, setStep] = useState<FlowStep>('battery');
  const [socialBattery, setSocialBattery] = useState<BatteryStep>(DEFAULT_BATTERY);
  const [sliderWidth, setSliderWidth] = useState(0);

  const recommendedChallenge = useMemo(
    () => pickRecommendedChallenge(socialBattery),
    [socialBattery],
  );

  const assistantMessage = useMemo(
    () => buildAssistantMessage(recommendedChallenge),
    [recommendedChallenge],
  );

  const updateBatteryFromLocation = (locationX: number) => {
    if (sliderWidth <= 0) return;

    const clampedLocation = clamp(locationX, 0, sliderWidth);
    const rawValue = (clampedLocation / sliderWidth) * 100;

    setSocialBattery(getClosestBatteryStep(rawValue));
  };

  const handleSliderLayout = (event: LayoutChangeEvent) => {
    setSliderWidth(event.nativeEvent.layout.width);
  };

  const handleConfirmBattery = () => {
    setStep('assistant');
  };

  const handleDismissAssistant = () => {
    setStep('done');
  };

  const handleOpenRecommendedChallenge = () => {
    if (!recommendedChallenge) {
      handleDismissAssistant();
      return;
    }

    handleDismissAssistant();
    onOpenChallenge(recommendedChallenge.id);
  };

  return (
    <Modal
      transparent
      visible={step !== 'done'}
      animationType="fade"
      statusBarTranslucent
      onRequestClose={step === 'assistant' ? handleDismissAssistant : () => undefined}
    >
      <View style={styles.backdrop}>
        <View style={styles.card}>
          {step === 'battery' ? (
            <View style={styles.cardContent}>
                <Text style={styles.eyebrow}>SOCIAL BATTERY</Text>
                <Text style={styles.title}>How social do you feel today?</Text>

                <View style={styles.heroRow}>
                  <Text style={styles.heroValue}>{socialBattery}%</Text>

                  <View style={styles.stateChip}>
                    <Text style={styles.stateChipText}>{BATTERY_HEADLINES[socialBattery]}</Text>
                  </View>
                </View>

                <Text style={styles.helperText}>{BATTERY_MESSAGES[socialBattery]}</Text>

                <View style={styles.sliderBlock}>
                  <View
                    style={styles.sliderTouchArea}
                    onLayout={handleSliderLayout}
                    onStartShouldSetResponder={() => true}
                    onMoveShouldSetResponder={() => true}
                    onResponderGrant={event => updateBatteryFromLocation(event.nativeEvent.locationX)}
                    onResponderMove={event => updateBatteryFromLocation(event.nativeEvent.locationX)}
                  >
                    <View style={styles.sliderTrack}>
                      <View style={[styles.sliderFill, { width: `${socialBattery}%` }]} />

                      {BATTERY_STEPS.map(stepValue => (
                        <View
                          key={stepValue}
                          style={[styles.sliderDotWrap, { left: `${stepValue}%` }]}
                        >
                          <View
                            style={[
                              styles.sliderDot,
                              socialBattery >= stepValue && styles.sliderDotActive,
                              socialBattery === stepValue && styles.sliderDotSelected,
                            ]}
                          />
                        </View>
                      ))}

                      <View
                        style={[
                          styles.sliderThumb,
                          { left: `${socialBattery}%`, marginLeft: -THUMB_SIZE / 2 },
                        ]}
                      />
                    </View>
                  </View>

                  <View style={styles.sliderLabels}>
                    {BATTERY_STEPS.map(stepValue => (
                      <TouchableOpacity
                        key={stepValue}
                        style={styles.sliderLabelButton}
                        onPress={() => setSocialBattery(stepValue)}
                        activeOpacity={0.85}
                      >
                        <Text
                          style={[
                            styles.sliderLabel,
                            socialBattery === stepValue && styles.sliderLabelSelected,
                          ]}
                        >
                          {stepValue}
                        </Text>
                      </TouchableOpacity>
                    ))}
                  </View>
                </View>

                <TouchableOpacity
                  style={styles.primaryButton}
                  onPress={handleConfirmBattery}
                  activeOpacity={0.85}
                >
                  <Text style={styles.primaryButtonText}>Save Social Battery</Text>
                </TouchableOpacity>
            </View>
          ) : (
            <ScrollView
              contentContainerStyle={styles.cardContent}
              showsVerticalScrollIndicator={false}
            >
                <Text style={styles.eyebrow}>AI GREETING</Text>
                <Text style={styles.title}>A quick recommendation for today</Text>
                <Text style={styles.assistantText}>{assistantMessage}</Text>

                {recommendedChallenge && (
                  <View style={styles.recommendationBlock}>
                    <Text style={styles.recommendationLabel}>Recommended challenge</Text>

                    <ChallengeCard
                      challenge={recommendedChallenge}
                      onPress={handleOpenRecommendedChallenge}
                    />
                  </View>
                )}

                <View style={styles.actionRow}>
                  {recommendedChallenge && (
                    <TouchableOpacity
                      style={[styles.actionButton, styles.primaryButton]}
                      onPress={handleOpenRecommendedChallenge}
                      activeOpacity={0.85}
                    >
                      <Text style={styles.primaryButtonText}>View challenge</Text>
                    </TouchableOpacity>
                  )}

                  <TouchableOpacity
                    style={[styles.actionButton, styles.secondaryButton]}
                    onPress={handleDismissAssistant}
                    activeOpacity={0.85}
                  >
                    <Text style={styles.secondaryButtonText}>Maybe later</Text>
                  </TouchableOpacity>
                </View>
            </ScrollView>
          )}
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.48)',
    justifyContent: 'center',
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.xl,
  },

  card: {
    width: '100%',
    maxWidth: 460,
    maxHeight: '92%',
    alignSelf: 'center',
    backgroundColor: colors.white,
    ...continuousRadius({ borderRadius: 32 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    ...shadow.md,
  },

  cardContent: {
    padding: spacing.lg,
    gap: spacing.md,
  },

  eyebrow: {
    fontSize: 12,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.gray500,
    letterSpacing: 0.4,
  },

  title: {
    fontSize: 24,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    lineHeight: 32,
    letterSpacing: -0.6,
  },

  heroRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: spacing.md,
  },

  heroValue: {
    fontSize: 44,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -1.2,
  },

  stateChip: {
    backgroundColor: colors.primary,
    borderRadius: radius.full,
    borderWidth: 1.5,
    borderColor: colors.black,
    paddingHorizontal: 12,
    paddingVertical: 8,
    alignSelf: 'flex-start',
  },

  stateChipText: {
    fontSize: 11,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  helperText: {
    fontSize: 14,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 22,
  },

  sliderBlock: {
    gap: 12,
    paddingVertical: 8,
  },

  sliderTouchArea: {
    paddingVertical: 20,
    paddingHorizontal: 6,
  },

  sliderTrack: {
    height: 10,
    backgroundColor: colors.gray200,
    borderRadius: radius.full,
    position: 'relative',
  },

  sliderFill: {
    position: 'absolute',
    top: 0,
    bottom: 0,
    left: 0,
    backgroundColor: colors.primary,
    borderRadius: radius.full,
  },

  sliderDotWrap: {
    position: 'absolute',
    top: '50%',
    marginTop: -10,
    marginLeft: -10,
    width: 20,
    height: 20,
    alignItems: 'center',
    justifyContent: 'center',
  },

  sliderDot: {
    width: 10,
    height: 10,
    borderRadius: radius.full,
    backgroundColor: colors.white,
    borderWidth: 1.5,
    borderColor: colors.black,
  },

  sliderDotActive: {
    backgroundColor: colors.black,
  },

  sliderDotSelected: {
    width: 12,
    height: 12,
  },

  sliderThumb: {
    position: 'absolute',
    top: '50%',
    marginTop: -THUMB_SIZE / 2,
    width: THUMB_SIZE,
    height: THUMB_SIZE,
    borderRadius: radius.full,
    backgroundColor: colors.blue,
    borderWidth: layout.border,
    borderColor: colors.black,
  },

  sliderLabels: {
    flexDirection: 'row',
  },

  sliderLabelButton: {
    flex: 1,
    alignItems: 'center',
  },

  sliderLabel: {
    fontSize: 11,
    fontFamily: font.body,
    color: colors.gray500,
  },

  sliderLabelSelected: {
    color: colors.black,
    fontWeight: '800',
  },

  assistantText: {
    fontSize: 14,
    fontFamily: font.body,
    color: colors.gray500,
    lineHeight: 22,
  },

  recommendationBlock: {
    gap: 10,
  },

  recommendationLabel: {
    fontSize: 13,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  actionRow: {
    gap: 10,
  },

  actionButton: {
    width: '100%',
  },

  primaryButton: {
    backgroundColor: colors.primary,
    ...continuousRadius({ borderRadius: 22 }),
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 16,
    paddingHorizontal: spacing.md,
    alignItems: 'center',
    justifyContent: 'center',
  },

  primaryButtonText: {
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },

  secondaryButton: {
    backgroundColor: colors.gray100,
    ...continuousRadius({ borderRadius: 22 }),
    borderWidth: 1.5,
    borderColor: colors.cardBorder,
    paddingVertical: 16,
    paddingHorizontal: spacing.md,
    alignItems: 'center',
    justifyContent: 'center',
  },

  secondaryButtonText: {
    fontSize: 14,
    fontFamily: font.headingBold,
    fontWeight: '800',
    color: colors.black,
    letterSpacing: -0.2,
  },
});

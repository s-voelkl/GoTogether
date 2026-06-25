import React, { useCallback, useEffect, useRef, useState } from 'react';
import {
  Animated,
  Linking,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';

import { CameraView, useCameraPermissions } from 'expo-camera';

import { colors, font, layout, radius, spacing } from '../theme';
import { CheckIcon } from './Icons';
import {
  findChallengeByCode,
  Challenge,
  DIFFICULTY_COLORS,
} from '../data/mockChallenges';

interface CheckInSheetProps {
  open: boolean;
  onClose: () => void;
}

type Mode = 'scan' | 'manual' | 'success';

const CODE_LENGTH = 5;

const wellFormed = (s: string) => /^[A-Z0-9]{5}$/.test(s.trim());

export const CheckInSheet: React.FC<CheckInSheetProps> = ({
  open,
  onClose,
}) => {
  const opacity = useRef(new Animated.Value(0)).current;
  const [permission, requestPermission] = useCameraPermissions();

  const [mode, setMode] = useState<Mode>('scan');
  const [code, setCode] = useState('');
  const [invalid, setInvalid] = useState(false);
  const [matched, setMatched] = useState<Challenge | null>(null);

  const lockRef = useRef(false);

  useEffect(() => {
    if (open) {
      setMode('scan');
      setCode('');
      setInvalid(false);
      setMatched(null);
      lockRef.current = false;

      if (!permission?.granted && permission?.canAskAgain !== false) {
        requestPermission();
      }
    }

    Animated.timing(opacity, {
      toValue: open ? 1 : 0,
      duration: 200,
      useNativeDriver: Platform.OS !== 'web',
    }).start();
  }, [open]); // intentionally only runs when the sheet opens/closes

  const handleCameraPermissionPress = useCallback(async () => {
    if (permission?.canAskAgain === false) {
      await Linking.openSettings();
      return;
    }

    await requestPermission();
  }, [permission?.canAskAgain, requestPermission]);

  const tryCode = (raw: string): boolean => {
    const challenge = findChallengeByCode(raw.trim().toUpperCase());

    if (!challenge) return false;

    setMatched(challenge);
    setMode('success');

    return true;
  };

  const onScan = ({ data }: { data: string }) => {
    if (lockRef.current) return;

    const value = data.trim().toUpperCase();

    if (!wellFormed(value)) return;

    lockRef.current = true;

    if (!tryCode(value)) {
      setTimeout(() => {
        lockRef.current = false;
      }, 1200);
    }
  };

  const submitManual = () => {
    if (!wellFormed(code)) return;

    if (!tryCode(code)) {
      setInvalid(true);
    }
  };

  const onChangeCode = (t: string) => {
    setCode(t.toUpperCase().replace(/[^A-Z0-9]/g, ''));

    if (invalid) {
      setInvalid(false);
    }
  };

  const cameraReady = mode === 'scan' && open && permission?.granted;

  const cameraPermissionButtonText =
    permission?.canAskAgain === false ? 'Open settings' : 'Enable camera';

  return (
    <Animated.View
      style={[styles.sheet, { opacity, pointerEvents: open ? 'auto' : 'none' }]}
    >
      <Text style={styles.title}>Check in</Text>

      {mode === 'scan' && (
        <Text style={styles.subtitle}>
          {'Point your camera at the QR code\nshown at the challenge venue'}
        </Text>
      )}

      {mode === 'manual' && (
        <Text style={styles.subtitle}>{'Enter the 5-character code\nshown at the challenge venue'}</Text>
      )}

      {mode === 'success' && matched ? (
        <View style={styles.center}>
          <View style={styles.successCircle}>
            <CheckIcon size={44} color={colors.black} />
          </View>

          <Text style={styles.successTitle}>You're checked in!</Text>
          <Text style={styles.successName}>{matched.name}</Text>

          <View style={styles.codeChip}>
            <Text style={styles.codeChipText}>+{matched.points} pts</Text>
          </View>

          <TouchableOpacity
            style={styles.primaryBtn}
            onPress={onClose}
            activeOpacity={0.85}
          >
            <Text style={styles.primaryText}>Done</Text>
          </TouchableOpacity>
        </View>
      ) : mode === 'scan' ? (
        <View style={styles.scanArea}>
          <View style={styles.cameraFrame}>
            {cameraReady ? (
              <CameraView
                style={StyleSheet.absoluteFill}
                facing="back"
                barcodeScannerSettings={{ barcodeTypes: ['qr'] }}
                onBarcodeScanned={onScan}
              />
            ) : (
              <View style={styles.cameraFallback}>
                <View style={styles.fallbackCenter}>
                    <Text style={styles.fallbackText}>
                    {permission && !permission.granted
                        ? 'Allow camera access to scan your challenge QR code.'
                        : 'Starting camera…'}
                    </Text>

                    {permission && !permission.granted && (
                    <TouchableOpacity
                        style={styles.permBtn}
                        onPress={handleCameraPermissionPress}
                        activeOpacity={0.85}
                    >
                        <Text style={styles.permText}>
                        {cameraPermissionButtonText}
                        </Text>
                    </TouchableOpacity>
                    )}
                </View>
              </View>
            )}

            <View style={[styles.corner, styles.cTL]} />
            <View style={[styles.corner, styles.cTR]} />
            <View style={[styles.corner, styles.cBL]} />
            <View style={[styles.corner, styles.cBR]} />
          </View>

          <TouchableOpacity
            style={styles.linkBtn}
            onPress={() => setMode('manual')}
            activeOpacity={0.7}
          >
            <Text style={styles.linkText}>Enter code manually</Text>
          </TouchableOpacity>
        </View>
      ) : (
        <View style={styles.manualArea}>
          <TextInput
            value={code}
            onChangeText={onChangeCode}
            placeholder="K7M2Q"
            placeholderTextColor={colors.gray400}
            autoCapitalize="characters"
            autoCorrect={false}
            maxLength={CODE_LENGTH}
            style={[styles.input, invalid && { color: DIFFICULTY_COLORS.hard }]}
          />

          <TouchableOpacity
            style={[
              styles.primaryBtn,
              !wellFormed(code) && styles.primaryBtnDisabled,
            ]}
            disabled={!wellFormed(code)}
            onPress={submitManual}
            activeOpacity={0.85}
          >
            <Text style={styles.primaryText}>Check in</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.linkBtn}
            onPress={() => {
              setInvalid(false);
              setMode('scan');
            }}
            activeOpacity={0.7}
          >
            <Text style={styles.linkText}>Scan a QR code instead</Text>
          </TouchableOpacity>
        </View>
      )}
    </Animated.View>
  );
};

const styles = StyleSheet.create({
  sheet: {
    flex: 1,
    width: '100%',
    height: '100%',
    backgroundColor: colors.blue,
    paddingHorizontal: spacing.lg,
    paddingTop: 35,
    paddingBottom: 22,
  },

  title: {
    fontSize: 22,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.3,
    marginBottom: 4,
  },

  subtitle: {
    fontSize: 13,
    fontFamily: font.body,
    color: colors.black,
    opacity: 0.7,
    marginBottom: spacing.lg,
  },

  scanArea: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    paddingBottom: spacing.lg,
  },

  cameraFrame: {
    width: '100%',
    maxWidth: 320,
    aspectRatio: 1,
    maxHeight: 320,
    alignSelf: 'center',
    borderRadius: 28,
    overflow: 'hidden',
    backgroundColor: colors.black,
    borderWidth: layout.border,
    borderColor: colors.black,
  },

  cameraFallback: {
    position: 'absolute',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
    backgroundColor: colors.black,
  },

  fallbackCenter: {
    position: 'absolute',
    top: 0,
    right: 0,
    bottom: 0,
    left: 0,
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: spacing.lg,
    gap: spacing.md,
  },

  fallbackText: {
    color: colors.white,
    fontFamily: font.body,
    fontSize: 13,
    textAlign: 'center',
    lineHeight: 20,
  },

  permBtn: {
    backgroundColor: colors.white,
    borderRadius: radius.full,
    paddingVertical: 10,
    paddingHorizontal: 18,
  },

  permText: {
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
    fontSize: 13,
  },

  corner: {
    position: 'absolute',
    width: 26,
    height: 26,
    borderColor: colors.white,
  },

  cTL: {
    top: 14,
    left: 14,
    borderTopWidth: 3,
    borderLeftWidth: 3,
    borderTopLeftRadius: 8,
  },

  cTR: {
    top: 14,
    right: 14,
    borderTopWidth: 3,
    borderRightWidth: 3,
    borderTopRightRadius: 8,
  },

  cBL: {
    bottom: 14,
    left: 14,
    borderBottomWidth: 3,
    borderLeftWidth: 3,
    borderBottomLeftRadius: 8,
  },

  cBR: {
    bottom: 14,
    right: 14,
    borderBottomWidth: 3,
    borderRightWidth: 3,
    borderBottomRightRadius: 8,
  },

  linkBtn: {
    marginTop: spacing.lg,
    paddingVertical: 8,
    alignSelf: 'center',
  },

  linkText: {
    fontSize: 16,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
    textDecorationLine: 'underline',
  },

  manualArea: {
    flex: 1,
  },

  input: {
    backgroundColor: colors.white,
    borderRadius: 18,
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 16,
    paddingHorizontal: 18,
    fontSize: 22,
    fontFamily: font.headingBold,
    letterSpacing: 6,
    color: colors.black,
    textAlign: 'center',
  },

  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    gap: spacing.sm,
  },

  successCircle: {
    width: 92,
    height: 92,
    borderRadius: radius.full,
    backgroundColor: colors.white,
    borderWidth: layout.border,
    borderColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: 6,
  },

  successTitle: {
    fontSize: 22,
    fontFamily: font.heading,
    fontWeight: '900',
    color: colors.black,
    letterSpacing: -0.4,
  },

  successName: {
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '700',
    color: colors.black,
  },

  codeChip: {
    backgroundColor: colors.white,
    borderRadius: radius.full,
    borderWidth: layout.border,
    borderColor: colors.black,
    paddingVertical: 8,
    paddingHorizontal: 20,
    marginTop: 4,
  },

  codeChipText: {
    fontSize: 14,
    fontFamily: font.headingBold,
    color: colors.black,
  },

  primaryBtn: {
    marginTop: spacing.md,
    width: '100%',
    paddingVertical: 16,
    borderRadius: radius.full,
    backgroundColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
  },

  primaryBtnDisabled: {
    opacity: 0.35,
  },

  primaryText: {
    fontSize: 15,
    fontFamily: font.body,
    fontWeight: '800',
    color: colors.white,
  },
});
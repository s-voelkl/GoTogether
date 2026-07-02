import React, {
  createContext,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import * as Location from 'expo-location';

export type LocationStatus = 'loading' | 'ready' | 'unavailable';

export interface UserLocation {
  status: LocationStatus;
  /** Device coordinates, or null until/unless resolved. */
  coords: { latitude: number; longitude: number } | null;
  /** Horizontal accuracy radius in meters, or null if unknown. */
  accuracy: number | null;
  /** Human-readable location label when available. */
  label: string | null;
}

const initial: UserLocation = {
  status: 'loading',
  coords: null,
  accuracy: null,
  label: null,
};

const LocationContext = createContext<UserLocation>(initial);

export const LocationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<UserLocation>(initial);

  useEffect(() => {
    let cancelled = false;

    (async () => {
      try {
        const { status } = await Location.requestForegroundPermissionsAsync();
        if (status !== 'granted') {
          if (!cancelled) {
            setState({ status: 'unavailable', coords: null, accuracy: null, label: null });
          }
          return;
        }

        const pos = await Location.getCurrentPositionAsync({
          accuracy: Location.Accuracy.Balanced,
        });
        const coords = {
          latitude: pos.coords.latitude,
          longitude: pos.coords.longitude,
        };
        const accuracy =
          typeof pos.coords.accuracy === 'number' ? pos.coords.accuracy : null;

        // Expo SDK 49+ removed geocoding APIs from expo-location.
        const label: string | null = null;

        if (!cancelled) setState({ status: 'ready', coords, accuracy, label });
      } catch {
        if (!cancelled) {
          setState({ status: 'unavailable', coords: null, accuracy: null, label: null });
        }
      }
    })();

    return () => {
      cancelled = true;
    };
  }, []);

  const value = useMemo(() => state, [state]);
  return <LocationContext.Provider value={value}>{children}</LocationContext.Provider>;
};

export function useUserLocation(): UserLocation {
  return useContext(LocationContext);
}

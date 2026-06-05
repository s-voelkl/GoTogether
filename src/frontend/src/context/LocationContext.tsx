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
  /** Reverse-geocoded "City, Country", or null if it couldn't be resolved. */
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

        let label: string | null = null;
        try {
          const [place] = await Location.reverseGeocodeAsync(coords);
          if (place) {
            const city = place.city ?? place.subregion ?? place.region ?? null;
            label = [city, place.country].filter(Boolean).join(', ') || null;
          }
        } catch {
          // Reverse geocoding failed — we still have coordinates.
        }

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

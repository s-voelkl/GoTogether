import React, {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
} from 'react';
import { mockChallenges, Challenge } from '../data/mockChallenges';

/**
 * Shared filter state.
 *
 * `activeFilters` is lifted out of the screens so the map (HomeScreen) and
 * the Challenges tab can stay in sync without prop drilling. The filter
 * sheet UI itself is rendered per-screen — only the *selection* is shared.
 */
interface FiltersContextValue {
  activeFilters: string[];
  toggleCategory: (id: string) => void;
  clearFilters: () => void;
  setActiveFilters: (next: string[]) => void;
}

const FiltersContext = createContext<FiltersContextValue | null>(null);

export const FiltersProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [activeFilters, setActiveFilters] = useState<string[]>([]);

  const toggleCategory = useCallback((id: string) => {
    setActiveFilters(prev =>
      prev.includes(id) ? prev.filter(c => c !== id) : [...prev, id],
    );
  }, []);

  const clearFilters = useCallback(() => setActiveFilters([]), []);

  const value = useMemo<FiltersContextValue>(
    () => ({ activeFilters, toggleCategory, clearFilters, setActiveFilters }),
    [activeFilters, toggleCategory, clearFilters],
  );

  return <FiltersContext.Provider value={value}>{children}</FiltersContext.Provider>;
};

export function useFilters(): FiltersContextValue {
  const ctx = useContext(FiltersContext);
  if (!ctx) throw new Error('useFilters must be used within a FiltersProvider');
  return ctx;
}

/**
 * Convenience hook: returns the challenge list filtered by the current
 * active categories. When no filters are active, returns all challenges.
 */
export function useFilteredChallenges(): Challenge[] {
  const { activeFilters } = useFilters();
  return useMemo(
    () =>
      activeFilters.length > 0
        ? mockChallenges.filter(c => activeFilters.includes(c.category))
        : mockChallenges,
    [activeFilters],
  );
}

export type Difficulty = 'easy' | 'medium' | 'hard';
export type ChallengeStatus = 'active' | 'upcoming' | 'completed';

export interface Challenge {
  id: string;
  name: string;
  category: string;
  lat: number;
  lng: number;
  points: number;
  description: string;
  difficulty: Difficulty;
  status: ChallengeStatus;
}

export const mockChallenges: Challenge[] = [
  {
    id: '1',
    name: 'Café Baroco',
    category: 'Food',
    lat: 49.4452,
    lng: 11.8572,
    points: 150,
    description: 'Visit the historic Café Baroco in the old town and try their signature cake.',
    difficulty: 'easy',
    status: 'active',
  },
  {
    id: '2',
    name: 'TV 1861 Amberg',
    category: 'Sport',
    lat: 49.4389,
    lng: 11.8698,
    points: 300,
    description: 'Attend a home match at the TV 1861 Amberg sports club.',
    difficulty: 'medium',
    status: 'active',
  },
  {
    id: '3',
    name: 'Stadtmuseum',
    category: 'Culture',
    lat: 49.4431,
    lng: 11.8615,
    points: 200,
    description: 'Explore Amberg\'s city museum and discover 1000 years of local history.',
    difficulty: 'easy',
    status: 'upcoming',
  },
  {
    id: '4',
    name: 'Vils Promenade',
    category: 'Nature',
    lat: 49.4478,
    lng: 11.8602,
    points: 100,
    description: 'Take a 3 km walk along the scenic Vils river promenade.',
    difficulty: 'easy',
    status: 'active',
  },
  {
    id: '5',
    name: 'Marktplatz Brunch',
    category: 'Social',
    lat: 49.4461,
    lng: 11.8630,
    points: 250,
    description: 'Meet up with locals for Sunday brunch at the main market square.',
    difficulty: 'easy',
    status: 'active',
  },
  {
    id: '6',
    name: 'Kurfürstenbad',
    category: 'Sport',
    lat: 49.4410,
    lng: 11.8550,
    points: 200,
    description: 'Do 10 laps at the historic Kurfürstenbad indoor swimming pool.',
    difficulty: 'medium',
    status: 'active',
  },
];

export const FILTER_CATEGORIES = [
  { id: 'Food',    label: 'Food',    emoji: '☕' },
  { id: 'Sport',   label: 'Sport',   emoji: '⚽' },
  { id: 'Culture', label: 'Culture', emoji: '🏛️' },
  { id: 'Nature',  label: 'Nature',  emoji: '🌿' },
  { id: 'Social',  label: 'Social',  emoji: '🎉' },
];

export const CATEGORY_COLORS: Record<string, string> = {
  Food:    '#FF6B6B',
  Sport:   '#4ECDC4',
  Culture: '#A78BFA',
  Nature:  '#10B981',
  Social:  '#F59E0B',
};

export const CATEGORY_EMOJI: Record<string, string> = {
  Food:    '☕',
  Sport:   '⚽',
  Culture: '🏛️',
  Nature:  '🌿',
  Social:  '🎉',
};

export const DIFFICULTY_COLORS: Record<string, string> = {
  easy:   '#10B981',
  medium: '#F59E0B',
  hard:   '#EF4444',
};
export type Difficulty = 'easy' | 'medium' | 'hard';

export interface Challenge {
  id: string;
  name: string;
  category: string;
  lat: number;
  lng: number;
  points: number;
  description: string;

  // challenges table
  host: string;             // mandatory host Company
  startTime: string;        // ISO
  durationMinutes: number;
  experiencePoints: number;
  minSocialBattery: number; // 1–5 (difficulty is derived from this)
  maxPlayers: number;       // 0 = unlimited
  participants: number;     // current signed-up users
  verificationCode: string; // 5-char check-in code
}

export const mockChallenges: Challenge[] = [
  {
    id: '1', name: 'Café Baroco', category: 'Food',
    lat: 49.4452, lng: 11.8572, points: 150,
    description: 'Visit the historic Café Baroco in the old town and try their signature cake.',
    host: 'Café Baroco GmbH', startTime: '2026-06-06T15:00:00',
    durationMinutes: 90, experiencePoints: 150, minSocialBattery: 2,
    maxPlayers: 0, participants: 12, verificationCode: 'K7M2Q',
  },
  {
    id: '2', name: 'TV 1861 Amberg', category: 'Sport',
    lat: 49.4389, lng: 11.8698, points: 300,
    description: 'Attend a home match at the TV 1861 Amberg sports club.',
    host: 'TV 1861 Amberg e.V.', startTime: '2026-06-07T16:30:00',
    durationMinutes: 120, experiencePoints: 300, minSocialBattery: 4,
    maxPlayers: 30, participants: 30, verificationCode: 'H5J1W', // full
  },
  {
    id: '3', name: 'Stadtmuseum', category: 'Culture',
    lat: 49.4431, lng: 11.8615, points: 200,
    description: 'Explore Amberg\'s city museum and discover 1000 years of local history.',
    host: 'Stadt Amberg', startTime: '2026-06-08T10:00:00',
    durationMinutes: 60, experiencePoints: 200, minSocialBattery: 2,
    maxPlayers: 25, participants: 9, verificationCode: 'R3T8N',
  },
  {
    id: '4', name: 'Vils Promenade', category: 'Nature',
    lat: 49.4478, lng: 11.8602, points: 100,
    description: 'Take a 3 km walk along the scenic Vils river promenade.',
    host: 'Amberg Tourismus', startTime: '2026-06-06T09:00:00',
    durationMinutes: 45, experiencePoints: 100, minSocialBattery: 1,
    maxPlayers: 0, participants: 5, verificationCode: 'P9X4L',
  },
  {
    id: '5', name: 'Marktplatz Brunch', category: 'Social',
    lat: 49.4461, lng: 11.8630, points: 250,
    description: 'Meet up with locals for Sunday brunch at the main market square.',
    host: 'Local Friends Amberg', startTime: '2026-06-08T11:00:00',
    durationMinutes: 120, experiencePoints: 250, minSocialBattery: 3,
    maxPlayers: 12, participants: 12, verificationCode: 'B2D6V', // full
  },
  {
    id: '6', name: 'Kurfürstenbad', category: 'Sport',
    lat: 49.4410, lng: 11.8550, points: 200,
    description: 'Do 10 laps at the historic Kurfürstenbad indoor swimming pool.',
    host: 'Kurfürstenbad Amberg', startTime: '2026-06-09T18:00:00',
    durationMinutes: 60, experiencePoints: 200, minSocialBattery: 3,
    maxPlayers: 20, participants: 14, verificationCode: 'M8K3Z',
  },
];

/** Derive difficulty from the required social battery (1–5). */
export function difficultyFromBattery(min: number): Difficulty {
  if (min <= 2) return 'easy';
  if (min === 3) return 'medium';
  return 'hard';
}

/** A challenge with a player cap is full once participants reach it. */
export function isChallengeFull(c: Challenge): boolean {
  return c.maxPlayers !== 0 && c.participants >= c.maxPlayers;
}

/** Look up a challenge by its 5-char verification code (case-insensitive). */
export function findChallengeByCode(code: string): Challenge | undefined {
  const v = code.trim().toUpperCase();
  return mockChallenges.find(ch => ch.verificationCode === v);
}

export const FILTER_CATEGORIES = [
  { id: 'Food',    label: 'Food',    emoji: '☕' },
  { id: 'Sport',   label: 'Sport',   emoji: '⚽' },
  { id: 'Culture', label: 'Culture', emoji: '🏛️' },
  { id: 'Nature',  label: 'Nature',  emoji: '🌿' },
  { id: 'Social',  label: 'Social',  emoji: '🎉' },
];

export const CATEGORY_COLORS: Record<string, string> = {
  Food: '#FF6B6B', Sport: '#4ECDC4', Culture: '#A78BFA', Nature: '#10B981', Social: '#F59E0B',
};

export const CATEGORY_EMOJI: Record<string, string> = {
  Food: '☕', Sport: '⚽', Culture: '🏛️', Nature: '🌿', Social: '🎉',
};

export const DIFFICULTY_COLORS: Record<string, string> = {
  easy: '#10B981', medium: '#F59E0B', hard: '#EF4444',
};
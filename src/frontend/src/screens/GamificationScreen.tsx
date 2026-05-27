import React from 'react';
import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { PlaceholderContent } from '../components/PlaceholderContent';

export const GamificationScreen: React.FC = () => (
  <ScreenShell rightButton={<FilterButton />}>
    <PlaceholderContent
      emoji="🏆"
      title="Gamification"
      subtitle="Levels · G-Bucks · Rewards"
    />
  </ScreenShell>
);

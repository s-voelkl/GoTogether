import React from 'react';
import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { GamificationView } from '../components/GamificationView';

export const GamificationScreen: React.FC = () => (
  <ScreenShell rightButton={<FilterButton />}>
    <GamificationView />
  </ScreenShell>
);

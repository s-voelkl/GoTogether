import React from 'react';
import { ScreenShell } from '../components/ScreenShell';
import { GamificationView } from '../components/GamificationView';

export const GamificationScreen: React.FC = () => (
  <ScreenShell rightButton={null}>
    <GamificationView />
  </ScreenShell>
);

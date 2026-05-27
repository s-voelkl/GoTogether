import React from 'react';
import { ScreenShell } from '../components/ScreenShell';
import { FilterButton } from '../components/FilterButton';
import { PlaceholderContent } from '../components/PlaceholderContent';

export const ProfileScreen: React.FC = () => (
  <ScreenShell rightButton={<FilterButton />}>
    <PlaceholderContent
      emoji="👤"
      title="Profile"
      subtitle="Social Battery · Settings"
    />
  </ScreenShell>
);

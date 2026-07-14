import React, { useCallback, useState } from 'react';
import { NavigationContainer, useNavigationContainerRef } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { AppStartFlow } from '../components/AppStartFlow';
import { HomeScreen } from '../screens/HomeScreen';
import { ChallengesScreen } from '../screens/ChallengesScreen';
import { GamificationScreen } from '../screens/GamificationScreen';
import { ProfileScreen } from '../screens/ProfileScreen';
import { TabBar } from '../components/TabBar';
import { RootTabParamList } from './types';

const Tab = createBottomTabNavigator<RootTabParamList>();

export const RootNavigator: React.FC = () => {
  const navigationRef = useNavigationContainerRef<RootTabParamList>();
  const [navigationReady, setNavigationReady] = useState(false);

  const handleOpenRecommendedChallenge = useCallback(
    (challengeId: string) => {
      if (!navigationRef.isReady()) return;

      navigationRef.navigate('Challenges', {
        selectedChallengeId: challengeId,
        selectedTs: Date.now(),
      });
    },
    [navigationRef],
  );

  return (
    <>
      <NavigationContainer ref={navigationRef} onReady={() => setNavigationReady(true)}>
        <Tab.Navigator
          tabBar={props => <TabBar {...props} />}
          screenOptions={{ headerShown: false }}
          initialRouteName="Home"
        >
          <Tab.Screen name="Home" component={HomeScreen} />
          <Tab.Screen name="Challenges" component={ChallengesScreen} />
          <Tab.Screen name="Gamification" component={GamificationScreen} />
          <Tab.Screen name="Profile" component={ProfileScreen} />
        </Tab.Navigator>
      </NavigationContainer>

      {navigationReady && <AppStartFlow onOpenChallenge={handleOpenRecommendedChallenge} />}
    </>
  );
};

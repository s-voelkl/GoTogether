import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { HomeScreen } from '../screens/HomeScreen';
import { ChallengesScreen } from '../screens/ChallengesScreen';
import { GamificationScreen } from '../screens/GamificationScreen';
import { ProfileScreen } from '../screens/ProfileScreen';
import { TabBar } from '../components/TabBar';
import { RootTabParamList } from './types';

const Tab = createBottomTabNavigator<RootTabParamList>();

export const RootNavigator: React.FC = () => (
  <NavigationContainer>
    <Tab.Navigator
      tabBar={props => <TabBar {...props} />}
      screenOptions={{ headerShown: false }}
      initialRouteName="Home"
    >
      <Tab.Screen name="Home"         component={HomeScreen} />
      <Tab.Screen name="Challenges"   component={ChallengesScreen} />
      <Tab.Screen name="Gamification" component={GamificationScreen} />
      <Tab.Screen name="Profile"      component={ProfileScreen} />
    </Tab.Navigator>
  </NavigationContainer>
);

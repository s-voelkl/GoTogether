import { BottomTabNavigationProp } from '@react-navigation/bottom-tabs';
import { RouteProp } from '@react-navigation/native';

/**
 * Bottom-tab param list.
 *
 * `Challenges.selectedChallengeId` is the under-the-hood handoff used by the
 * map's mini overlay when the user taps a challenge card. The Challenges tab
 * receives the id so a future detail view can read it and render accordingly.
 * Today the screen still shows the "Coming soon" placeholder.
 */
export type RootTabParamList = {
  Home: undefined;
  Challenges: { selectedChallengeId?: string } | undefined;
  Gamification: undefined;
  Profile: undefined;
};

export type TabNavigationProp<Screen extends keyof RootTabParamList> =
  BottomTabNavigationProp<RootTabParamList, Screen>;

export type TabRouteProp<Screen extends keyof RootTabParamList> =
  RouteProp<RootTabParamList, Screen>;

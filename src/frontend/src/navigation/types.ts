import { BottomTabNavigationProp } from '@react-navigation/bottom-tabs';
import { RouteProp } from '@react-navigation/native';

export type RootTabParamList = {
  Home:
    | {
        focusChallengeId?: string;
        focusTs?: number;
        recenterTs?: number;
      }
    | undefined;

  Challenges:
    | {
        selectedChallengeId?: string;
        selectedTs?: number;
      }
    | undefined;

  Gamification: undefined;
  Profile: undefined;
};

export type TabNavigationProp<Screen extends keyof RootTabParamList> =
  BottomTabNavigationProp<RootTabParamList, Screen>;

export type TabRouteProp<Screen extends keyof RootTabParamList> =
  RouteProp<RootTabParamList, Screen>;
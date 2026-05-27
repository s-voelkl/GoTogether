import 'react-native-gesture-handler';
import React from 'react';
import { View, StatusBar } from 'react-native';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import {
  useFonts,
  Unbounded_400Regular,
  Unbounded_700Bold,
  Unbounded_900Black,
} from '@expo-google-fonts/unbounded';
import { RootNavigator } from './src/navigation/RootNavigator';
import { FiltersProvider } from './src/context/FiltersContext';
import { colors } from './src/theme';

export default function App() {
  const [fontsLoaded] = useFonts({
    Unbounded_400Regular,
    Unbounded_700Bold,
    Unbounded_900Black,
  });

  if (!fontsLoaded) {
    return <View style={{ flex: 1, backgroundColor: colors.primary }} />;
  }

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <SafeAreaProvider>
        <StatusBar barStyle="dark-content" backgroundColor="transparent" translucent />
        <FiltersProvider>
          <RootNavigator />
        </FiltersProvider>
      </SafeAreaProvider>
    </GestureHandlerRootView>
  );
}

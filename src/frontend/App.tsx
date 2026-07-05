import 'react-native-gesture-handler';
import React from 'react';
import { View, StatusBar, Text, StyleSheet } from 'react-native';
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
import { LocationProvider } from './src/context/LocationContext';
import { colors } from './src/theme';

interface ErrorBoundaryState {
  hasError: boolean;
  message: string;
}

class AppErrorBoundary extends React.Component<
  { children: React.ReactNode },
  ErrorBoundaryState
> {
  state: ErrorBoundaryState = {
    hasError: false,
    message: '',
  };

  static getDerivedStateFromError(error: Error): ErrorBoundaryState {
    return {
      hasError: true,
      message: error?.message ?? 'Unknown runtime error',
    };
  }

  componentDidCatch(error: Error): void {
    console.error('App runtime error:', error);
  }

  render() {
    if (this.state.hasError) {
      return (
          <View style={styles.errorRoot}>
          <Text style={styles.errorTitle}>Something went wrong</Text>
          <Text style={styles.errorBody}>{this.state.message}</Text>
        </View>
      );
    }

    return this.props.children;
  }
}

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
    <AppErrorBoundary>
      <GestureHandlerRootView style={{ flex: 1 }}>
        <SafeAreaProvider>
          <StatusBar barStyle="dark-content" backgroundColor="transparent" translucent />
          <LocationProvider>
            <FiltersProvider>
              <RootNavigator />
            </FiltersProvider>
          </LocationProvider>
        </SafeAreaProvider>
      </GestureHandlerRootView>
    </AppErrorBoundary>
  );
}

const styles = StyleSheet.create({
  errorRoot: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: colors.white,
    paddingHorizontal: 24,
  },
  errorTitle: {
    fontSize: 24,
    fontWeight: '800',
    color: colors.black,
    marginBottom: 12,
    textAlign: 'center',
  },
  errorBody: {
    fontSize: 14,
    color: colors.gray500,
    textAlign: 'center',
  },
});
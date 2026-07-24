import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

export const HomeScreen: React.FC = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>GoTogether Web Preview</Text>
      <Text style={styles.text}>
        Karte ist in der Web-Vorschau nicht verfügbar.
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    minHeight: 600,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#0f1416',
    padding: 24,
  },
  title: {
    color: '#ffffff',
    fontSize: 32,
    fontWeight: '700',
    marginBottom: 12,
  },
  text: {
    color: '#ffffff',
    fontSize: 18,
    textAlign: 'center',
  },
});

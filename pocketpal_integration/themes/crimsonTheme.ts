import { MD3DarkTheme, MD3Theme } from 'react-native-paper';

/**
 * Crimson Neon (Красный Неон) Theme Specification for PocketPal AI
 * Native React Native Paper MD3 Theme
 */
export const CrimsonNeonDarkTheme: MD3Theme = {
  ...MD3DarkTheme,
  roundness: 12,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#FF2A55',            // Яркий неоновый алый
    onPrimary: '#08080A',
    primaryContainer: '#221116',
    onPrimaryContainer: '#FF4D73',
    secondary: '#B5179E',          // Неоновый пурпур
    onSecondary: '#FFFFFF',
    secondaryContainer: '#2A1324',
    onSecondaryContainer: '#E056FD',
    tertiary: '#00F5D4',           // Киберпанк-изумруд
    onTertiary: '#08080A',
    background: '#08080A',         // Глубокий темный графит
    onBackground: '#F0F0F2',
    surface: '#121215',            // Dark Surface 1
    onSurface: '#F0F0F2',
    surfaceVariant: '#1B1B20',     // Dark Surface 2
    onSurfaceVariant: '#9E9EA8',
    outline: '#381620',            // Тонкая алая неоновая граница
    outlineVariant: '#24131A',
    error: '#FF5252',
    onError: '#FFFFFF',
    elevation: {
      level0: 'transparent',
      level1: '#121215',
      level2: '#18181D',
      level3: '#202026',
      level4: '#282830',
      level5: '#32323C',
    }
  },
};

export const CyberpunkRubyTheme: MD3Theme = {
  ...MD3DarkTheme,
  roundness: 12,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#E52E53',
    onPrimary: '#130D12',
    primaryContainer: '#2B1C28',
    onPrimaryContainer: '#FF3B60',
    secondary: '#FF3B60',
    onSecondary: '#FFFFFF',
    secondaryContainer: '#1E141C',
    onSecondaryContainer: '#FF5277',
    tertiary: '#FF7043',
    onTertiary: '#FFFFFF',
    background: '#130D12',
    onBackground: '#F0F0F2',
    surface: '#1E141C',
    onSurface: '#F0F0F2',
    surfaceVariant: '#2B1C28',
    onSurfaceVariant: '#A498A2',
    outline: 'rgba(229, 46, 83, 0.5)',
    error: '#FF5252',
    onError: '#FFFFFF',
  },
};

export const OledBlackRedTheme: MD3Theme = {
  ...MD3DarkTheme,
  roundness: 10,
  colors: {
    ...MD3DarkTheme.colors,
    primary: '#FF1744',
    onPrimary: '#000000',
    primaryContainer: '#141416',
    onPrimaryContainer: '#FF1744',
    secondary: '#FF5252',
    onSecondary: '#FFFFFF',
    background: '#000000',
    onBackground: '#FFFFFF',
    surface: '#080809',
    onSurface: '#FFFFFF',
    surfaceVariant: '#121214',
    onSurfaceVariant: '#A0A0A0',
    outline: 'rgba(255, 23, 68, 0.4)',
    error: '#D50000',
    onError: '#FFFFFF',
  },
};

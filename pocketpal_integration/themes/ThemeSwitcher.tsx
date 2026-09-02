import React from 'react';
import { View, StyleSheet, TouchableOpacity } from 'react-native';
import { Card, Text, useTheme, RadioButton } from 'react-native-paper';

interface ThemeOption {
  id: string;
  title: string;
  description: string;
  accentColor: string;
}

const THEMES: ThemeOption[] = [
  {
    id: 'crimson_neon',
    title: 'Красный Неон (Crimson Neon)',
    description: 'Фирменный алый неоновый стиль со светящимися рамками и графитом',
    accentColor: '#FF2A55',
  },
  {
    id: 'cyberpunk_ruby',
    title: 'Киберпанк Рубин (Ruby)',
    description: 'Глубокий рубиновый с пурпурными неоновыми акцентами',
    accentColor: '#E52E53',
  },
  {
    id: 'oled_red',
    title: 'OLED Черный и Красный',
    description: 'Истинно черный фон #000000 для экономии аккумулятора',
    accentColor: '#FF1744',
  },
];

interface Props {
  currentThemeId: string;
  onSelectTheme: (themeId: string) => void;
}

export const ThemeSwitcher: React.FC<Props> = ({ currentThemeId, onSelectTheme }) => {
  const theme = useTheme();

  return (
    <Card style={[styles.container, { backgroundColor: theme.colors.surface }]}>
      <Card.Content>
        <Text variant="titleMedium" style={[styles.title, { color: theme.colors.primary }]}>
          Оформление и Темы (Crimson Edition)
        </Text>
        <Text variant="bodySmall" style={{ color: theme.colors.onSurfaceVariant, marginBottom: 12 }}>
          Выберите цветовую схему для PocketPal AI
        </Text>

        {THEMES.map(item => {
          const isSelected = currentThemeId === item.id;
          return (
            <TouchableOpacity
              key={item.id}
              onPress={() => onSelectTheme(item.id)}
              activeOpacity={0.7}
            >
              <View
                style={[
                  styles.optionCard,
                  {
                    backgroundColor: isSelected ? theme.colors.primaryContainer : theme.colors.surfaceVariant,
                    borderColor: isSelected ? item.accentColor : theme.colors.outline,
                  },
                ]}
              >
                <View style={styles.leftCol}>
                  <View style={[styles.colorBadge, { backgroundColor: item.accentColor }]} />
                  <View style={styles.textContainer}>
                    <Text variant="titleSmall" style={{ fontWeight: 'bold', color: theme.colors.onSurface }}>
                      {item.title}
                    </Text>
                    <Text variant="bodySmall" style={{ color: theme.colors.onSurfaceVariant }}>
                      {item.description}
                    </Text>
                  </View>
                </View>

                <RadioButton
                  value={item.id}
                  status={isSelected ? 'checked' : 'unchecked'}
                  onPress={() => onSelectTheme(item.id)}
                  color={item.accentColor}
                />
              </View>
            </TouchableOpacity>
          );
        })}
      </Card.Content>
    </Card>
  );
};

const styles = StyleSheet.create({
  container: {
    marginVertical: 8,
    borderRadius: 14,
  },
  title: {
    fontWeight: 'bold',
  },
  optionCard: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: 12,
    borderRadius: 10,
    borderWidth: 1,
    marginBottom: 8,
  },
  leftCol: {
    flexDirection: 'row',
    alignItems: 'center',
    flex: 1,
  },
  colorBadge: {
    width: 20,
    height: 20,
    borderRadius: 10,
    marginRight: 10,
  },
  textContainer: {
    flex: 1,
  },
});

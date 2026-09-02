import React, { useState } from 'react';
import {
  View,
  ScrollView,
  StyleSheet,
  Alert,
  TouchableOpacity,
} from 'react-native';
import {
  Modal,
  Portal,
  Text,
  Button,
  TextInput,
  Card,
  useTheme,
  IconButton,
  Chip,
} from 'react-native-paper';
import { observer } from 'mobx-react-lite';
import { templateStore, PromptTemplate } from '../store/TemplateStore';

interface Props {
  visible: boolean;
  onDismiss: () => void;
  onApplyToSession?: (template: PromptTemplate) => void;
}

export const TemplateManagerModal = observer(({ visible, onDismiss, onApplyToSession }: Props) => {
  const theme = useTheme();

  // Create Form state
  const [isCreating, setIsCreating] = useState(false);
  const [name, setName] = useState('');
  const [desc, setDesc] = useState('');
  const [systemPrompt, setSystemPrompt] = useState('');
  const [temp, setTemp] = useState('0.7');
  const [topP, setTopP] = useState('0.9');

  // Import / Export state
  const [isImporting, setIsImporting] = useState(false);
  const [importJsonText, setImportJsonText] = useState('');

  const handleCreate = async () => {
    if (!name.trim() || !systemPrompt.trim()) {
      Alert.alert('Ошибка', 'Пожалуйста, введите название шаблона и системный промпт.');
      return;
    }

    await templateStore.addCustomTemplate({
      name: name.trim(),
      description: desc.trim() || 'Пользовательский пресет',
      systemPrompt: systemPrompt.trim(),
      temperature: parseFloat(temp) || 0.7,
      topP: parseFloat(topP) || 0.9,
      contextWindow: 4096,
      category: 'Кастомные',
    });

    setName('');
    setDesc('');
    setSystemPrompt('');
    setIsCreating(false);
    Alert.alert('Успех', 'Шаблон создан и готов к использованию!');
  };

  const handleExport = () => {
    const success = templateStore.copyExportToClipboard();
    if (success) {
      Alert.alert('Скопировано!', 'JSON со всеми шаблонами скопирован в буфер обмена.');
    } else {
      Alert.alert('Ошибка', 'Не удалось скопировать данные в буфер.');
    }
  };

  const handleImport = async () => {
    if (!importJsonText.trim()) {
      Alert.alert('Ошибка', 'Вставьте JSON-текст для импорта.');
      return;
    }

    const result = await templateStore.importFromJsonString(importJsonText);
    if (result.success) {
      Alert.alert('Успешно', result.message);
      setImportJsonText('');
      setIsImporting(false);
    } else {
      Alert.alert('Ошибка импорта', result.message);
    }
  };

  return (
    <Portal>
      <Modal
        visible={visible}
        onDismiss={onDismiss}
        contentContainerStyle={[
          styles.modalContainer,
          { backgroundColor: theme.colors.surface, borderColor: theme.colors.outline },
        ]}
      >
        <View style={styles.headerRow}>
          <View>
            <Text variant="titleLarge" style={[styles.title, { color: theme.colors.primary }]}>
              Шаблоны и Промпты (PocketPal)
            </Text>
            <Text variant="bodySmall" style={{ color: theme.colors.onSurfaceVariant }}>
              Управление персонажами, ролями и форматами запросов
            </Text>
          </View>
          <IconButton icon="close" onPress={onDismiss} iconColor={theme.colors.onSurface} />
        </View>

        {/* Action bar */}
        <View style={styles.actionBar}>
          <Button
            mode="contained"
            buttonColor={theme.colors.primary}
            textColor={theme.colors.onPrimary}
            icon="plus"
            onPress={() => {
              setIsCreating(!isCreating);
              setIsImporting(false);
            }}
            compact
          >
            Создать
          </Button>

          <Button
            mode="outlined"
            textColor={theme.colors.secondary}
            style={{ borderColor: theme.colors.secondary }}
            icon="content-copy"
            onPress={handleExport}
            compact
          >
            Экспорт JSON
          </Button>

          <Button
            mode="outlined"
            textColor={theme.colors.tertiary}
            style={{ borderColor: theme.colors.tertiary }}
            icon="download"
            onPress={() => {
              setIsImporting(!isImporting);
              setIsCreating(false);
            }}
            compact
          >
            Импорт JSON
          </Button>
        </View>

        {/* CREATE FORM */}
        {isCreating && (
          <Card style={[styles.formCard, { backgroundColor: theme.colors.surfaceVariant }]}>
            <Card.Content>
              <Text variant="titleMedium" style={{ color: theme.colors.primary, marginBottom: 8 }}>
                Новый шаблон
              </Text>
              <TextInput
                label="Название шаблона *"
                value={name}
                onChangeText={setName}
                mode="outlined"
                style={styles.input}
              />
              <TextInput
                label="Краткое описание"
                value={desc}
                onChangeText={setDesc}
                mode="outlined"
                style={styles.input}
              />
              <TextInput
                label="Системный промпт (Инструкция) *"
                value={systemPrompt}
                onChangeText={setSystemPrompt}
                multiline
                numberOfLines={3}
                mode="outlined"
                style={styles.input}
              />
              <View style={styles.rowInputs}>
                <TextInput
                  label="Temperature"
                  value={temp}
                  onChangeText={setTemp}
                  keyboardType="numeric"
                  mode="outlined"
                  style={[styles.input, { flex: 1, marginRight: 8 }]}
                />
                <TextInput
                  label="Top-P"
                  value={topP}
                  onChangeText={setTopP}
                  keyboardType="numeric"
                  mode="outlined"
                  style={[styles.input, { flex: 1 }]}
                />
              </View>
              <Button mode="contained" onPress={handleCreate} style={{ marginTop: 8 }}>
                Сохранить шаблон
              </Button>
            </Card.Content>
          </Card>
        )}

        {/* IMPORT FORM */}
        {isImporting && (
          <Card style={[styles.formCard, { backgroundColor: theme.colors.surfaceVariant }]}>
            <Card.Content>
              <Text variant="titleMedium" style={{ color: theme.colors.tertiary, marginBottom: 8 }}>
                Импорт шаблонов из JSON
              </Text>
              <TextInput
                placeholder='Вставьте [{"name": "...", "systemPrompt": "..."}]'
                value={importJsonText}
                onChangeText={setImportJsonText}
                multiline
                numberOfLines={4}
                mode="outlined"
                style={styles.input}
              />
              <Button mode="contained" buttonColor={theme.colors.tertiary} onPress={handleImport} style={{ marginTop: 8 }}>
                Импортировать
              </Button>
            </Card.Content>
          </Card>
        )}

        {/* TEMPLATES LIST */}
        <ScrollView style={styles.listContainer}>
          {templateStore.templates.map(tpl => {
            const isActive = templateStore.activeTemplateId === tpl.id;
            return (
              <Card
                key={tpl.id}
                style={[
                  styles.templateCard,
                  {
                    backgroundColor: isActive ? theme.colors.primaryContainer : theme.colors.surfaceVariant,
                    borderColor: isActive ? theme.colors.primary : theme.colors.outline,
                    borderWidth: 1,
                  },
                ]}
              >
                <Card.Content>
                  <View style={styles.headerRow}>
                    <View style={{ flex: 1 }}>
                      <View style={{ flexDirection: 'row', alignItems: 'center' }}>
                        <Text variant="titleSmall" style={{ fontWeight: 'bold', color: theme.colors.onSurface }}>
                          {tpl.name}
                        </Text>
                        <Chip compact style={{ marginLeft: 6, height: 22 }} textStyle={{ fontSize: 9 }}>
                          {tpl.category}
                        </Chip>
                      </View>
                      <Text variant="bodySmall" style={{ color: theme.colors.onSurfaceVariant, marginTop: 2 }}>
                        {tpl.description}
                      </Text>
                    </View>

                    {tpl.isCustom && (
                      <IconButton
                        icon="trash-can-outline"
                        size={18}
                        iconColor={theme.colors.error}
                        onPress={() => templateStore.deleteCustomTemplate(tpl.id)}
                      />
                    )}
                  </View>

                  <Text
                    numberOfLines={2}
                    style={[styles.promptPreview, { color: theme.colors.onSurfaceVariant }]}
                  >
                    {tpl.systemPrompt}
                  </Text>

                  <View style={styles.footerRow}>
                    <Text variant="labelSmall" style={{ color: theme.colors.primary }}>
                      T: {tpl.temperature} • TopP: {tpl.topP}
                    </Text>

                    <Button
                      mode={isActive ? 'text' : 'contained-tonal'}
                      compact
                      onPress={() => {
                        templateStore.setActiveTemplate(tpl.id);
                        if (onApplyToSession) onApplyToSession(tpl);
                      }}
                    >
                      {isActive ? '✓ Активен' : 'Выбрать'}
                    </Button>
                  </View>
                </Card.Content>
              </Card>
            );
          })}
        </ScrollView>
      </Modal>
    </Portal>
  );
});

const styles = StyleSheet.create({
  modalContainer: {
    margin: 16,
    borderRadius: 16,
    padding: 16,
    maxHeight: '90%',
    borderWidth: 1,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  title: {
    fontWeight: 'bold',
  },
  actionBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginVertical: 8,
  },
  formCard: {
    marginVertical: 8,
    borderRadius: 12,
  },
  input: {
    marginBottom: 8,
  },
  rowInputs: {
    flexDirection: 'row',
  },
  listContainer: {
    marginTop: 8,
  },
  templateCard: {
    marginBottom: 8,
    borderRadius: 10,
  },
  promptPreview: {
    fontSize: 11,
    fontFamily: 'monospace',
    marginVertical: 6,
    opacity: 0.85,
  },
  footerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 4,
  },
});

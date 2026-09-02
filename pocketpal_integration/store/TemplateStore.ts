import { makeAutoObservable, runInAction } from 'mobx';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Clipboard from '@react-native-clipboard/clipboard';

export interface PromptTemplate {
  id: string;
  name: string;
  description: string;
  systemPrompt: string;
  temperature: number;
  topP: number;
  contextWindow: number;
  category: string;
  isCustom: boolean;
  createdAt: number;
}

const STORAGE_KEY_TEMPLATES = '@pocketpal_custom_templates';
const STORAGE_KEY_ACTIVE_TEMPLATE = '@pocketpal_active_template_id';

export class TemplateStore {
  templates: PromptTemplate[] = [
    {
      id: 'deepseek_r1_reasoner',
      name: 'DeepSeek-R1 Deep Reasoner',
      description: 'Глубокое пошаговое рассуждение с генерацией мыслей <think>...',
      systemPrompt: 'You are DeepSeek-R1, an expert reasoning AI running natively on local hardware. Think through problems step-by-step inside <think> tags before delivering a concise, rigorous answer.',
      temperature: 0.6,
      topP: 0.95,
      contextWindow: 8192,
      category: 'Логика и R1',
      isCustom: false,
      createdAt: 1700000000000,
    },
    {
      id: 'cpp_rust_architect',
      name: 'C++20 & Rust Архитектор',
      description: 'Генерация эффективного zero-overhead кода для мобильного компилятора.',
      systemPrompt: 'You are a Principal Systems Engineer specializing in C++20/23, Rust, and low-level memory efficiency. Always provide complete, compilable, and highly optimized code.',
      temperature: 0.2,
      topP: 0.85,
      contextWindow: 8192,
      category: 'Код и Системы',
      isCustom: false,
      createdAt: 1700000000000,
    },
    {
      id: 'pocketpal_cyber_assistant',
      name: 'PocketPal AI Cyber Core',
      description: 'Стандартный универсальный помощник со 100% приватностью без интернета.',
      systemPrompt: 'You are PocketPal AI Core, a fast and helpful on-device assistant. Answer directly, concisely, and protect user data at all times.',
      temperature: 0.7,
      topP: 0.9,
      contextWindow: 4096,
      category: 'PocketPal Core',
      isCustom: false,
      createdAt: 1700000000000,
    },
    {
      id: 'creative_storyteller',
      name: 'Креативный сценарист',
      description: 'Высокая образность речи, написание историй и диалогов.',
      systemPrompt: 'You are a master fiction author and screenwriter. Create immersive, atmospheric prose with vivid descriptions and sharp dialog.',
      temperature: 1.1,
      topP: 0.98,
      contextWindow: 4096,
      category: 'Творчество',
      isCustom: false,
      createdAt: 1700000000000,
    }
  ];

  activeTemplateId: string = 'deepseek_r1_reasoner';
  isLoading: boolean = false;

  constructor() {
    makeAutoObservable(this);
    this.loadFromStorage();
  }

  get activeTemplate(): PromptTemplate | undefined {
    return this.templates.find(t => t.id === this.activeTemplateId) || this.templates[0];
  }

  async loadFromStorage() {
    this.isLoading = true;
    try {
      const savedTemplates = await AsyncStorage.getItem(STORAGE_KEY_TEMPLATES);
      const activeId = await AsyncStorage.getItem(STORAGE_KEY_ACTIVE_TEMPLATE);
      
      runInAction(() => {
        if (savedTemplates) {
          const parsed = JSON.parse(savedTemplates);
          // Объединяем дефолтные и сохраненные кастомные
          const custom = parsed.filter((t: PromptTemplate) => t.isCustom);
          this.templates = [...this.templates.filter(t => !t.isCustom), ...custom];
        }
        if (activeId) {
          this.activeTemplateId = activeId;
        }
      });
    } catch (e) {
      console.warn('Failed to load templates from storage', e);
    } finally {
      runInAction(() => {
        this.isLoading = false;
      });
    }
  }

  setActiveTemplate(templateId: string) {
    this.activeTemplateId = templateId;
    AsyncStorage.setItem(STORAGE_KEY_ACTIVE_TEMPLATE, templateId);
  }

  async addCustomTemplate(template: Omit<PromptTemplate, 'id' | 'isCustom' | 'createdAt'>) {
    const newTemplate: PromptTemplate = {
      ...template,
      id: `custom_${Date.now()}`,
      isCustom: true,
      createdAt: Date.now(),
    };

    runInAction(() => {
      this.templates.push(newTemplate);
      this.activeTemplateId = newTemplate.id;
    });

    await this.saveCustomTemplates();
    return newTemplate;
  }

  async deleteCustomTemplate(templateId: string) {
    runInAction(() => {
      this.templates = this.templates.filter(t => t.id !== templateId);
      if (this.activeTemplateId === templateId) {
        this.activeTemplateId = this.templates[0]?.id || '';
      }
    });

    await this.saveCustomTemplates();
  }

  private async saveCustomTemplates() {
    const custom = this.templates.filter(t => t.isCustom);
    await AsyncStorage.setItem(STORAGE_KEY_TEMPLATES, JSON.stringify(custom));
  }

  /**
   * Экспорт всех шаблонов в формат JSON
   */
  exportToJsonString(): string {
    const payload = {
      version: '1.0',
      appName: 'PocketPal AI (Crimson Edition)',
      exportedAt: Date.now(),
      templates: this.templates,
    };
    return JSON.stringify(payload, null, 2);
  }

  copyExportToClipboard(): boolean {
    try {
      const json = this.exportToJsonString();
      Clipboard.setString(json);
      return true;
    } catch (e) {
      return false;
    }
  }

  /**
   * Импорт шаблонов из JSON строки
   */
  async importFromJsonString(jsonString: string): Promise<{ success: boolean; count: number; message: string }> {
    try {
      const parsed = JSON.parse(jsonString);
      let listToImport: any[] = [];

      if (Array.isArray(parsed)) {
        listToImport = parsed;
      } else if (parsed.templates && Array.isArray(parsed.templates)) {
        listToImport = parsed.templates;
      } else if (parsed.name && parsed.systemPrompt) {
        listToImport = [parsed];
      } else {
        return { success: false, count: 0, message: 'Не найдены валидные шаблоны в структуре JSON' };
      }

      let count = 0;
      runInAction(() => {
        listToImport.forEach((item, index) => {
          if (item.name && item.systemPrompt) {
            const newTpl: PromptTemplate = {
              id: `imported_${Date.now()}_${index}`,
              name: item.name,
              description: item.description || 'Импортированный шаблон',
              systemPrompt: item.systemPrompt,
              temperature: typeof item.temperature === 'number' ? item.temperature : 0.7,
              topP: typeof item.topP === 'number' ? item.topP : 0.9,
              contextWindow: typeof item.contextWindow === 'number' ? item.contextWindow : 4096,
              category: item.category || 'Импортированные',
              isCustom: true,
              createdAt: Date.now(),
            };

            // Удаляем существующий с таким же именем если есть
            this.templates = this.templates.filter(t => t.name.toLowerCase() !== newTpl.name.toLowerCase());
            this.templates.push(newTpl);
            count++;
          }
        });
      });

      if (count > 0) {
        await this.saveCustomTemplates();
        return { success: true, count, message: `Успешно импортировано ${count} шаблонов!` };
      } else {
        return { success: false, count: 0, message: 'В JSON не найдено подходящих полей (name, systemPrompt)' };
      }
    } catch (e: any) {
      return { success: false, count: 0, message: `Ошибка парсинга JSON: ${e.message}` };
    }
  }
}

export const templateStore = new TemplateStore();

# 🦊 PocketPal AI Integration Package: Crimson Theme & Prompt Template Exporter/Importer

Этот пакет содержит готовые исходные файлы для интеграции наших функций в официальный репозиторий [PocketPal AI (`a-ghorbani/pocketpal-ai`)](https://github.com/a-ghorbani/pocketpal-ai), который построен на **React Native + React Native Paper + MobX**.

---

## 📁 Структура подготовленных файлов:

1. **`pocketpal_integration/themes/crimsonTheme.ts`**
   - Полная спецификация темы **Crimson Neon (Красный Неон)** и **Cyberpunk Ruby** для `react-native-paper` (MD3DarkTheme / MD3LightTheme).
   - Включает все токены цветов: `primary = #FF2A55`, `background = #08080A`, неоновые границы, акценты для карточек моделей и сообщений.

2. **`pocketpal_integration/store/TemplateStore.ts`**
   - MobX Store для управления шаблонами персонажей (Pals / Prompts):
     - Создание пользовательских шаблонов с системным промптом, температурой, top-p, context size.
     - **Экспорт в JSON** (в буфер обмена или файл).
     - **Импорт из JSON** с автоматической валидацией схемы.

3. **`pocketpal_integration/screens/TemplateManagerModal.tsx`**
   - Готовый React Native UI-компонент (модальное окно / экран) со списком шаблонов, предпросмотром, кнопками «Создать», «Экспорт», «Импорт» и мгновенным применением к активной сессии чата.

4. **`pocketpal_integration/themes/ThemeSwitcher.tsx`**
   - UI-переключатель для экрана настроек Settings в PocketPal AI с превью красной темы.

---

## 🚀 Пошаговая инструкция по внедрению в ваш форк `pocketpal-ai`:

### Шаг 1: Сделайте Fork репозитория
1. Зайдите на https://github.com/a-ghorbani/pocketpal-ai
2. Нажмите **Fork** в правом верхнем углу в свой GitHub-аккаунт.
3. Склонируйте форк на свой компьютер:
   ```bash
   git clone https://github.com/<ВАШ_НИК>/pocketpal-ai.git
   cd pocketpal-ai
   ```

### Шаг 2: Скопируйте файлы из папки `pocketpal_integration/`
- Скопируйте `themes/crimsonTheme.ts` в `src/themes/` или `src/utils/theme.ts`.
- Скопируйте `store/TemplateStore.ts` в `src/store/`.
- Скопируйте `screens/TemplateManagerModal.tsx` в `src/screens/` или `src/components/`.

### Шаг 3: Подключение Темы в `App.tsx` / `RootStore`
В файле `src/store/UIStore.ts` или месте инициализации темы PaperProvider:
```typescript
import { CrimsonNeonDarkTheme, CyberpunkRubyTheme } from './themes/crimsonTheme';

// В списке доступных тем приложения добавьте:
export const themes = {
  // ... стандартные темы
  crimson: CrimsonNeonDarkTheme,
  cyberpunk_ruby: CyberpunkRubyTheme,
};
```

### Шаг 4: Сборка APK с новыми функциями через GitHub Actions
В репозитории PocketPal уже настроен Fastlane / GitHub Actions для сборки Release APK. Достаточно запушить изменения в ваш форк, и GitHub соберёт готовый APK со всеми изменениями!

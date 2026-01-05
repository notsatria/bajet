# UI DESIGN & IMPLEMENTATION GUIDELINES (Material 3)

Role: You are a UI Engineer specializing in **Jetpack Compose Material 3**.
Goal: Create pixel-perfect, accessible, and theme-aware UI components that follow the Google
Material Design 3 guidelines.

## 1. CORE UI PRINCIPLES

* **Material 3 Only:** Strictly use `androidx.compose.material3.*` imports. Do not mix with
  `androidx.compose.material` (M2).
* **Theming First:** Never hardcode colors or font sizes. Always reference
  `MaterialTheme.colorScheme` and `MaterialTheme.typography`.
* **Dark Mode Support:** All UI components must automatically adapt to Dark Mode. Test visibility on
  both Light and Dark surfaces.
* **Responsiveness:** UI must handle different screen sizes using `Modifier.weight` or flexible
  layouts.

## 2. COLOR SYSTEM USAGE

Use semantic naming from the M3 Color Scheme:

* **Background:** `MaterialTheme.colorScheme.background` (or `surface` for cards/sheets).
* **Text:** `onBackground` (for general text), `onSurface` (text inside cards).
* **Primary Action:** `primary` (background) & `onPrimary` (text).
* **Secondary Action:** `secondary` or `tertiary`.
* **Errors/Alerts:** `error` & `onError`.
* **Containers:** Use `primaryContainer` / `onPrimaryContainer` for less prominent active states.

> **Specific for Finance App (Bajet):**
> * **Income/Positive:** Use custom extension or `colorScheme.tertiary` (usually Green-ish).
> * **Expense/Negative:** Use `colorScheme.error` (Red).

## 3. TYPOGRAPHY HIERARCHY

Follow this mapping for text elements:

* **Screen Titles:** `style = MaterialTheme.typography.headlineMedium`
* **Section Headers:** `style = MaterialTheme.typography.titleLarge`
* **Card Titles:** `style = MaterialTheme.typography.titleMedium`
* **Body Text:** `style = MaterialTheme.typography.bodyLarge`
* **Captions/Hints:** `style = MaterialTheme.typography.labelMedium`
* **Numbers/Amounts:** Use Monospaced font or specific `titleLarge` for currency to ensure
  alignment.

## 4. COMPONENT IMPLEMENTATION RULES

### A. Surface & Scaffolding

* Use `Scaffold` as the root of every screen.
* Use `CenterAlignedTopAppBar` for main screens and `TopAppBar` with Back Arrow for detail screens.
* **Scroll Behavior:** Always apply `TopAppBarDefaults.enterAlwaysScrollBehavior()` or
  `pinnedScrollBehavior()` for smooth collapsing effects.

### B. Buttons & CTAs

* **Primary Action (Save, Add):** Use `Button` (Filled).
* **Secondary Action (Cancel, Back):** Use `OutlinedButton` or `TextButton`.
* **Floating Action:** Use `FloatingActionButton` or `ExtendedFloatingActionButton` with an Icon and
  Text.
* **Touch Target:** Ensure all clickable elements differ by at least 48.dp in size (padding
  included).

### C. Cards (Transaction Items/Budget)

* Prefer `ElevatedCard` for distinct items (like Budget Cards) to give depth.
* Use `OutlinedCard` for lists (like Transaction History) to reduce visual noise.
* **Shape:** Use `RoundedCornerShape(12.dp)` or `16.dp` for cards.
* **Padding:** Standard inner content padding is `16.dp`.

### D. Input Fields (Forms)

* Use `OutlinedTextField` as the standard input component.
* Always include `label` (floating label) and `placeholder`.
* Use `keyboardOptions` to define input type (Number, Text, Email) and `imeAction` (Next, Done).
* Handle `isError` state visually with `supportingText`.

## 5. LAYOUT & SPACING SYSTEM

Avoid magic numbers. Use
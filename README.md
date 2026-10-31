# MADT4 Calculator

A Windows 11 Standard Calculator look-alike for Android, built with **Kotlin** and **Jetpack Compose** as the deliverable for **Mobile Application Development Techniques – Lab #4**.

The app reproduces the visual style (colour palette, button shapes, light/dark theme) and the exact calculation behaviour (immediate-execution model, unary operations, memory stack, runtime history) of the Windows 11 built-in calculator.

---

## Table of Contents

1. [Features](#features)
2. [User Guide](#user-guide)
3. [Requirements](#requirements)
4. [How to Build and Run](#how-to-build-and-run)
5. [Project Structure](#project-structure)
6. [Localization](#localization)
7. [Running the Unit Tests](#running-the-unit-tests)
8. [Known Limitations](#known-limitations)
9. [Repository & Licence](#repository--licence)

---

## Features

### Arithmetic operations
| Operation | Button |
|-----------|--------|
| Addition | `+` |
| Subtraction | `−` |
| Multiplication | `×` |
| Division | `÷` |

### Unary operations
| Operation | Button | Example |
|-----------|--------|---------|
| Reciprocal | `1/x` | 1/(8) → 0.125 |
| Square | `x²` | sqr(5) → 25 |
| Square root | `√x` | √(9) → 3 |
| Percentage | `%` | 200 + 10% → 220 |

### Editing controls
| Button | Action |
|--------|--------|
| `⌫` | Delete the last digit of the current entry |
| `CE` | Clear the current entry only; keeps the pending operator and accumulator |
| `C` | Clear everything (display, expression, operator, accumulator) |
| `+/-` | Toggle the sign of the current displayed value |
| `.` | Append a decimal point (ignored if one already exists) |

### Immediate-execution (chained operations)
The calculator uses the same **immediate-execution** model as Windows 11 Standard: pressing a binary operator evaluates any pending operation on the spot.  
Example: `2 + 3 ×` evaluates `2 + 3 = 5` immediately, then starts `5 ×`.

### Repeated equals
Pressing `=` repeatedly re-applies the last operator and operand, matching Windows 11 behaviour.  
Example: `10 + 2 =` → 12, `=` → 14, `=` → 16.

### Runtime calculation history
Every completed `=` press appends a record (expression + result) to an in-memory list, displayed newest-first in the **History** bottom sheet. Tapping a history entry loads its result back into the display.

### Memory stack (multi-item)
The memory stores a list of values (not just one slot):

| Button | Action |
|--------|--------|
| `MC` | Clear the entire memory list (disabled when empty) |
| `MR` | Recall the most recently stored value (disabled when empty) |
| `M+` | Add the current display to the top memory entry (stores it if memory is empty) |
| `M−` | Subtract the current display from the top memory entry (negates and stores if empty) |
| `MS` | Push the current display as a new entry onto the memory stack |
| `M▾` | Open the **Memory** bottom sheet to browse all stored values |

### Edge-case handling
| Situation | Behaviour |
|-----------|-----------|
| Division by zero (`9 ÷ 0 =`) | Displays `Cannot divide by zero`; `isError` flag set |
| Square root of a negative number | Displays `Invalid input`; `isError` flag set |
| Reciprocal of zero (`1/x` on `0`) | Displays `Cannot divide by zero` |
| Any digit input after an error | Clears the error state and starts fresh input |
| Standalone `%` (no pending operator) | Result is `0`, matching Windows 11 Standard |

### Light / Dark theme
The app follows the system theme automatically. Both themes use the authentic Windows 11 calculator colour palette (`WinLightBg`, `WinDarkBg`, accent blue `#0067C0` / `#0078D7`).

---

## User Guide

### Layout overview

```
┌────────────────────────────────────┐
│ Standard                        🕐 │  ← header + history button
│                                    │
│                      12 + 8 =      │  ← expression line (secondary colour)
│                              20    │  ← main display (large, right-aligned)
│                                    │
│  MC   MR   M+   M−   MS   M▾      │  ← memory row
│                                    │
│  %    CE    C    ⌫                │
│  1/x  x²   √x   ÷                 │
│  7    8    9    ×                  │
│  4    5    6    −                  │
│  1    2    3    +                  │
│  +/-  0    .    =                  │  ← = is accent blue
└────────────────────────────────────┘
```

### Step-by-step examples

**Basic addition: 12 + 8 = 20**

1. Tap `1`, `2` → display shows `12`.
2. Tap `+` → expression line shows `12 +`.
3. Tap `8` → display shows `8`.
4. Tap `=` → display shows `20`; expression line shows `12 + 8 =`; the entry is added to History.

**Reciprocal: 1/(8) = 0.125**

1. Tap `8` → display shows `8`.
2. Tap `1/x` → display shows `0.125`; expression line shows `1/(8)`.

**Chained operations: 2 + 3 × 4 = 20**

1. Tap `2`, `+`, `3`.
2. Tap `×` — the pending `2 + 3` is evaluated immediately; display shows `5`, expression shows `5 ×`.
3. Tap `4`, `=` → display shows `20`, expression shows `5 × 4 =`.

**Percentage: 200 + 10% = 220**

1. Tap `2`, `0`, `0`, `+`, `1`, `0`.
2. Tap `%` → display updates to `20` (10% of 200).
3. Tap `=` → display shows `220`.

**Memory: store, add, recall**

1. Tap `5`, `0`, `MS` → 50 is pushed onto the memory stack; `MC` and `MR` become active.
2. Tap `1`, `0`, `M+` → top memory entry becomes `60`.
3. Tap `MR` → display loads `60`.
4. Tap `M▾` → opens the Memory sheet; tap any entry to load it, or tap `✕` next to an entry to remove it.

### History panel

Tap the **🕐 (Refresh icon)** in the top-right corner to open the History bottom sheet.

- Entries are listed newest-first.
- Each entry shows the expression (`7 × 8 =`) above the result (`56`).
- Tap an entry to load its result into the display; the sheet closes automatically.
- Tap the **trash icon** to delete all history entries.
- The sheet shows *"There's no history yet"* when empty.

### Memory panel

Tap `M▾` to open the Memory bottom sheet.

- Each entry shows its stored value.
- Tap an entry to recall it; the sheet closes automatically.
- Tap the **✕** icon next to an entry to delete that single entry.
- Tap the **trash icon** to clear the entire memory.
- The sheet shows *"There's nothing saved in memory"* when empty.

---

## Requirements

| Item | Value |
|------|-------|
| Android Studio | Ladybug (2024.2) or newer |
| JDK | 11 (set in `compileOptions` and Gradle daemon JVM toolchain version 25) |
| Kotlin | 2.0.21 |
| Android Gradle Plugin | 9.4.1 |
| Gradle | 9.6.0 |
| `minSdk` | 24 (Android 7.0 Nougat) |
| `targetSdk` / `compileSdk` | 37 |

---

## How to Build and Run

### 1. Open the project

In Android Studio choose **File → Open** and navigate to the `MADT4_Calculator` folder. Android Studio detects the Gradle project automatically.

### 2. Gradle sync

Android Studio will start a Gradle sync automatically. If it does not, click **File → Sync Project with Gradle Files** (or the elephant icon in the toolbar). Wait for the sync to complete and for the **Build** output to show no errors.

### 3. Run on an emulator

1. Open **Device Manager** (Tools → Device Manager).
2. Create a virtual device (e.g. Pixel 8, API 35) if you do not have one yet.
3. Start the emulator, then click the green **Run ▶** button (or press `Shift+F10`).

### 4. Run on a physical device

1. Enable **Developer Options** on the device and turn on **USB Debugging**.
2. Connect the device via USB; accept the "Allow USB debugging?" prompt.
3. Select the device from the target dropdown and click **Run ▶**.

### 5. Build a release APK (optional)

```
./gradlew assembleRelease
```

The unsigned APK is written to `app/build/outputs/apk/release/`.

---

## Project Structure

```
MADT4_Calculator/
├── app/
│   ├── build.gradle.kts                          # App-level Gradle config (minSdk 24, targetSdk 37)
│   └── src/
│       ├── main/
│       │   ├── java/com/madt/calculator/
│       │   │   ├── MainActivity.kt               # Entry point; hosts CalculatorScreen inside MADT4CalculatorTheme
│       │   │   ├── engine/
│       │   │   │   └── CalculatorEngine.kt       # Pure-Kotlin calculation engine (BigDecimal, history, memory)
│       │   │   ├── model/
│       │   │   │   ├── CalculationHistoryItem.kt # Data class for one history record
│       │   │   │   └── MemoryItem.kt             # Data class for one memory entry
│       │   │   └── ui/
│       │   │       ├── CalculatorScreen.kt       # Root Composable; wires engine state to UI
│       │   │       ├── components/
│       │   │       │   ├── CalculatorButton.kt   # Styled button (DIGIT / OPERATOR / ACCENT types)
│       │   │       │   ├── CalculatorDisplay.kt  # Expression + result display with dynamic font sizing
│       │   │       │   ├── HistorySheet.kt       # Modal bottom sheet for calculation history
│       │   │       │   ├── MemoryRow.kt          # MC MR M+ M− MS M▾ row above the button grid
│       │   │       │   └── MemorySheet.kt        # Modal bottom sheet for memory entries
│       │   │       └── theme/
│       │   │           ├── Color.kt              # Windows 11 light/dark colour tokens
│       │   │           ├── Theme.kt              # MaterialTheme wiring (light/dark colour schemes)
│       │   │           └── Type.kt               # Typography scale
│       │   └── res/
│       │       ├── values/strings.xml            # Localizable UI strings (screen titles, button labels, and content descriptions)
│       │       └── values/themes.xml             # Base Activity theme
│       └── test/
│           └── java/com/madt/calculator/
│               └── CalculatorEngineTest.kt       # 14 JUnit 4 unit tests for the calculation engine
├── gradle/
│   ├── libs.versions.toml                        # Centralised dependency version catalogue
│   └── wrapper/gradle-wrapper.properties         # Gradle 9.6.0 distribution URL
├── build.gradle.kts                              # Root Gradle build file
└── settings.gradle.kts                          # Project/module declarations
```

---

## Localization

All user-visible strings—including screen titles, bottom sheet messages, button labels, and accessibility content descriptions—are externalized and declared in [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml):

```xml
<resources>
    <string name="app_name">Calculator</string>

    <!-- Screen titles -->
    <string name="standard_title">Standard</string>
    <string name="history_title">History</string>
    <string name="memory_title">Memory</string>

    <!-- History / Memory sheet UI strings -->
    <string name="clear_history">Clear all history</string>
    <string name="clear_memory">Clear all memory</string>
    <string name="delete_memory_entry">Delete entry</string>
    <string name="no_history">There\'s no history yet</string>
    <string name="no_memory">There\'s nothing saved in memory</string>

    <!-- Memory row button labels & content descriptions -->
    <string name="btn_mc">MC</string>
    <string name="btn_mr">MR</string>
    <string name="btn_m_add">M+</string>
    <string name="btn_m_sub">M−</string>
    <string name="btn_ms">MS</string>
    <string name="btn_m_open">M▾</string>
    <string name="desc_mc">Memory clear</string>
    ...

    <!-- Operator / function button labels & content descriptions -->
    <string name="btn_percent">%</string>
    <string name="btn_ce">CE</string>
    <string name="btn_c">C</string>
    <string name="btn_backspace">⌫</string>
    <string name="btn_reciprocal">1/x</string>
    <string name="btn_square">x²</string>
    <string name="btn_sqrt">√x</string>
    <string name="btn_divide">÷</string>
    <string name="btn_multiply">×</string>
    <string name="btn_subtract">−</string>
    <string name="btn_add">+</string>
    <string name="btn_equals">=</string>
    <string name="btn_sign">+/−</string>
    <string name="btn_decimal">.</string>
    ...

    <!-- Digit button labels & content descriptions -->
    <string name="btn_digit_0">0</string>
    ...
    <string name="btn_digit_9">9</string>

    <!-- Header navigation icon descriptions -->
    <string name="desc_history_button">Show history</string>
</resources>
```

In the Jetpack Compose UI, strings are referenced dynamically via `stringResource(R.string.<id>)`, allowing full interface localization and screen reader (TalkBack) accessibility.

### How to add a new language

1. **Create the target locale directory:**  
   In `app/src/main/res/`, add a directory qualified with the ISO 639-1 language code (and optional region code), for example:
   - `app/src/main/res/values-lt/` for Lithuanian
   - `app/src/main/res/values-fr/` for French
   - `app/src/main/res/values-de/` for German
   - `app/src/main/res/values-es/` for Spanish

2. **Add translated strings:**  
   Copy [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml) into your new directory and translate the text inside each `<string>` tag:
   - Translate titles, warnings, and sheet labels (`standard_title`, `clear_history`, `no_history`, etc.).
   - Localize button symbols if required (for example, localized decimal comma `,` in `btn_decimal` or localized numerals).
   - Translate all accessibility content descriptions (`desc_*`), which TalkBack reads to visually impaired users.

3. **Compile and test:**  
   Android automatically resolves resources from the directory matching the user's device locale at runtime, falling back to the default `values/strings.xml` for any missing keys. No Kotlin source code changes are required.

---

## Running the Unit Tests

The project contains **14 JUnit 4 unit tests** in [`CalculatorEngineTest.kt`](app/src/test/java/com/madt/calculator/CalculatorEngineTest.kt) that exercise the `CalculatorEngine` directly (no Android framework required).

**From Android Studio:**

Right-click `CalculatorEngineTest.kt` in the Project pane → **Run 'CalculatorEngineTest'**.  
Or right-click the `test` source set → **Run Tests in 'calculator'**.

**From the command line:**

```bash
# Windows
gradlew.bat test

# macOS / Linux
./gradlew test
```

The HTML test report is written to:

```
app/build/reports/tests/testDebugUnitTest/index.html
```

### What is tested

| Test | Scenario |
|------|----------|
| `testSimpleAddition` | 12 + 8 = 20; history entry created |
| `testImmediateExecutionChaining` | 2 + 3 × 4 = 20 (Windows immediate-eval model) |
| `testDivisionByZero` | Error message shown; cleared on next digit |
| `testSquareRoot` | √9 = 3 |
| `testNegativeSquareRoot` | √(−9) → Invalid input error |
| `testSquaring` | 5² = 25 |
| `testReciprocal` | 1/8 = 0.125 |
| `testReciprocalZero` | 1/0 → divide-by-zero error |
| `testPercentageWithPendingOperation` | 200 + 10% = 220 |
| `testBackspace` | Digit-by-digit deletion |
| `testClearEntryVsClearAll` | CE keeps accumulator; C resets everything |
| `testSignToggle` | 42 → −42 → 42 |
| `testDecimalPoint` | Second `.` is ignored |
| `testRepeatedEquals` | 10 + 2 = 12, = 14, = 16 |
| `testMemoryOperations` | MS → MR → M+ → M− → MC full cycle |
| `testHistoryRecall` | 7 × 8 = 56 recalled after clearAll |
| `testUnaryThenBinaryChaining` | Unary result used in subsequent binary chain |
| `testRecallSpecificMemoryItem` | Multiple stored values recalled individually |

---

## Known Limitations

- **No keyboard / hardware-button input.** All input is touch-only.
- **No landscape layout.** The button grid is designed for portrait orientation. Rotating the device may cause clipping.
- **History and memory are not persisted.** Both lists live only in RAM; they are lost when the app process is killed.
- **No thousands separator.** Large numbers are displayed without commas or spaces (e.g. `1000000`, not `1,000,000`).
- **Square root uses `Double` precision.** The implementation converts to `Double` before calling `Math.sqrt`, then wraps back to `BigDecimal`. This may introduce tiny floating-point rounding errors for certain inputs.
- **`compileSdk` uses a preview release API (`release(37)`).** Requires an Android Studio version that supports the corresponding SDK.

---

## Repository & Licence

**Repository:** <https://github.com/navidkids-hue/MADT_4_Calculator>

This project was created for academic purposes as part of the Mobile Application Development Techniques course (Lab #4). No open-source licence has been applied; all rights are reserved by the author unless stated otherwise in the repository.

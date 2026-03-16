# Memory App - Micro-Design System (MVP)

This document defines the foundational visual language for the Memory App. It serves as the single source of truth for UI execution, ensuring a warm, inviting, and emotionally resonant experience.

## Core Aesthetic
* Warm / Cozy / Scrapbook / Journal feel.
* Liquid glass elevation (Apple-style).
* Soft, diffuse shadows (Apple-style).

## Typography
*   **Primary Font (Titles):** `Trocchi` (Google Fonts, free) - Gives the personal, handmade journal feel. Replaced `Playwrite Österreich` as of Task 47.
*   **Secondary Font (Body Text, Entries & Dates):** Highly legible Sans-Serif (e.g., `SF Pro Rounded`, `Nunito`, or `Inter`) - Ensures long journal entries and dates are easy to read.

## Color Palette
### Base Colors
*   **App Background:** `#FDF9F1` (Warm paper)
*   **Memory Card Background:** Apple-style clean surface (e.g., `#FFFFFF`) to create contrast against the warm paper background. 
*   **Primary Text:** `#2C2A29` (Soft black)
*   **Secondary / Hint Text:** Medium warm grey (e.g., `#8E8A86`)
*   **Accent / Primary Brand Gradient (Interactive elements):** `#FF9966` → `#FF6699` (soft orange → soft pink, left-to-right or top-to-bottom as context demands).
    - Applied to: "Save memory" button fill, FABs, waveform bars, gradient strip above bottom sheet, action chips active states, and any other primary interactive surface.
    - Replaces the previous flat `#2C2A29` accent for all interactive elements.

### Emotional Colors (Background Gradients)
**Crucial UI Note:** These colors should NOT be used as solid blocks. They must be implemented as an **extreme blurred, soft gradient** (custom background) behind the memory content, blending with the Apple Glass elevation.

*   **HAPPY:** `#FFD700` (Gold - Like sunshine, warmth, optimism)
*   **SAD:** `#6B9BD1` (Soft Blue - Gentle sadness, not harsh navy)
*   **ANXIOUS:** `#9B8BC6` (Lavender - Soft purple, calming despite anxiety)
*   **CALM:** `#7FB5A0` (Sage Green - Peaceful, like nature, grounded)
*   **EXCITED:** `#FF9F66` (Coral - Vibrant but warm, not aggressive red)
*   **NEUTRAL:** `#D4C5B9` (Beige - Like old diary paper, nostalgic)

## Shapes & Radii
*   **Buttons (FABs, Action Icons):** 100% radius (Perfect circles/pills)
*   **Memory Cards / Containers:** `32px` radius

## Elevation & Shadows
*   **Style:** Soft, diffused Apple-style drop shadows. 
*   **List Separation:** Rely entirely on Apple-style shadows and padding. Do NOT use visible hard borders or divider lines between memory cards in the index or list views.
*   **Implementation Note:** Avoid harsh Material Design default dropshadows. Focus on low y-offset, low opacity (e.g., 5-8% black), and a high blur radius (e.g., 16px-32px).
*   **Glassmorphism (Liquid Glass):** Used for elevated overlays (e.g., the bottom audio player). For MVP compatibility across older devices (API 26+), standard translucent overlays (e.g., 80% opacity) are acceptable in place of OS-level render blurs.

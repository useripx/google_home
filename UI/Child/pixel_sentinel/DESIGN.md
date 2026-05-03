# Design System Specification

## 1. Overview & Creative North Star: "The Silent Sentinel"

This design system is built upon the concept of **"The Silent Sentinel."** It moves beyond the utilitarian nature of standard Material Design 3 to create an experience that feels both authoritative and invisible. For the Parent, the system acts as a high-end data dashboard—sophisticated, calm, and reassuring. For the "Disguise Mode," it pivots to a "System Aesthetic"—utilizing technical minimalism to blend perfectly into the Android OS fabric.

We break the "template" look by rejecting rigid borders and standard shadow depths. Instead, we utilize **Tonal Layering** and **Intentional Asymmetry**. By leveraging high-contrast typography scales against vast, breathing white spaces, we create a premium editorial feel that signals security through precision rather than complexity.

---

## 2. Colors: Tonal Depth & Accents

The palette utilizes a high-clarity `surface` base with Google’s signature primary colors used as "functional highlights" rather than decorative elements.

### The Color Logic
*   **Primary (`#005ac1`):** Reserved for "Action & Security." Use for primary CTAs and active tracking states.
*   **Secondary (`#006e2d`):** "Safe Zones." Use for successful check-ins and "Home" status.
*   **Tertiary (`#7a5a00`):** "Caution/Warning." Use for low battery or "Leaving Zone" alerts.
*   **Error (`#9f403d`):** "Urgent." Use for SOS or disconnection events.

### Style Directives
*   **The "No-Line" Rule:** 1px solid borders are strictly prohibited for sectioning. Boundaries must be defined solely through background shifts. For example, a `surface-container-low` card sitting on a `surface` background.
*   **Surface Hierarchy:** Use nested tiers to define importance.
    *   *Level 0 (Background):* `surface` (#f7f9ff)
    *   *Level 1 (Sections):* `surface-container-low` (#eff4fc)
    *   *Level 2 (Active Elements):* `surface-container-highest` (#d6e4f3)
*   **The Glass & Gradient Rule:** For floating action buttons or high-tier Parent dashboard cards, use a subtle linear gradient from `primary` to `primary-dim`. Apply a `backdrop-blur` (12px-20px) to any floating overlays using 80% opacity on `surface_container_lowest`.

---

## 3. Typography: Editorial Authority

We use **Inter** for its neutral, technical clarity. The hierarchy is designed to feel like a premium news application—large, airy headlines paired with tight, functional labels.

*   **Display (Large/Medium):** Use for "Safe" status or big numerical data (e.g., "Arrived 5m ago"). These should have -2% letter spacing to feel "custom."
*   **Headline (Small):** The workhorse for Parent Dashboard headers. Use `on_surface` to maintain a strong contrast against soft backgrounds.
*   **Title (Medium/Small):** Used for card titles and map labels. Bold weights are encouraged here to contrast with the lightweight body text.
*   **Body (Large/Medium):** High-readability settings. Ensure a line height of at least 1.5x for `body-lg` to maintain the "premium" feel.
*   **Labels:** Use `label-md` in `on_surface_variant` (#53606d) for timestamps and secondary technical metadata.

---

## 4. Elevation & Depth: Tonal Layering

Traditional shadows are often "noisy." This system uses light and color to imply height.

*   **The Layering Principle:** Depth is achieved by stacking. A `surface-container-lowest` card (Pure White) on a `surface-container` background creates a natural lift.
*   **Ambient Shadows:** If a floating element (like a Map FAB) is required, use a shadow with a 32px blur and 6% opacity, tinted with `primary`. It should feel like a soft glow rather than a dark drop-shadow.
*   **The "Ghost Border" Fallback:** If accessibility requires a stroke (e.g., in Parent Mode input fields), use `outline-variant` at 15% opacity. Never use 100% opaque outlines.
*   **Glassmorphism:** Navigation bars and floating headers must use `surface_container_lowest` at 70% opacity with a `backdrop-filter: blur(20px)`. This integrates the UI with the map or content beneath it.

---

## 5. Components: Precision-Engineered

### Buttons
*   **Primary:** Rounded `xl` (1.5rem). Gradient fill (`primary` to `primary_dim`). No shadow.
*   **Secondary:** Ghost style. No background, `primary` text, and a `Ghost Border` only on hover/active states.
*   **Tertiary:** `surface-container-high` background with `on_surface` text.

### The Dashboard Card (Parent Mode)
*   **Rule:** Forbid divider lines.
*   **Structure:** Use a `surface-container-low` base. Separate the "Child Profile" from "Location History" using a vertical 24dp gap and a slight shift to `surface-container-highest` for the active item.

### The "System Update" Component (Kids Mode)
*   **Aesthetic:** Total technical minimalism. 
*   **Design:** Use `surface-dim` background. Typography must be `body-sm` in `on_surface_variant`. 
*   **Interaction:** Replace "App UI" elements with a "Checking for updates..." progress bar (using the `primary` blue) and technical version strings (e.g., `v14.2.0_build_99`). It must look like a native Android OS process.

### Input Fields
*   **Style:** Filled, not outlined. Use `surface-container-highest`.
*   **Indicator:** A 2px bottom-bar in `primary` that only appears on focus.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use asymmetrical padding. A larger top-padding on headlines creates a bespoke, editorial feel.
*   **Do** use `primary` and `secondary` colors for "Data Visualization" dots on the map—keep them small and jewel-like.
*   **Do** prioritize "Breathing Room." If a screen feels crowded, increase the `surface` spacing rather than adding lines.

### Don’t:
*   **Don't** use pure black (#000000). Use `inverse_surface` (#0a0f13) for dark themes to maintain tonal richness.
*   **Don't** use standard Material 2-style "Raised" buttons. Everything should feel like it is part of the surface or floating in a "glass" layer.
*   **Don't** use icons with varying stroke weights. Use a consistent 2px rounded "Linear" icon set to match the Inter typeface.
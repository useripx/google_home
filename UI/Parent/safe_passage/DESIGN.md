# Design System Document: The Invisible Guardian

## 1. Overview & Creative North Star
**Creative North Star: "The Digital Sanctuary"**
This design system moves away from the sterile, technical feel of traditional tracking apps toward a high-end editorial experience that feels protective yet breathable. By leveraging a "disguised" identity under the 'Google Home' moniker, the UI must balance professional authority with a warm, domestic softness. 

We break the "template" look by rejecting rigid grids in favor of **Intentional Asymmetry** and **Tonal Depth**. The system relies on overlapping layers and sophisticated white space to create a sense of calm for parents, ensuring that even in moments of high utility (tracking a location), the interface feels like a premium, well-curated dashboard rather than a cluttered map.

---

## 2. Colors: Tonal Architecture
Our palette uses a foundation of "Google Blue" for trust and "Safe Greens" for reassurance, but we apply them with a sophisticated, tiered approach.

### The "No-Line" Rule
**Strict Mandate:** Designers are prohibited from using 1px solid borders for sectioning. Boundaries must be defined solely through background color shifts. 
- A `surface-container-low` card sitting on a `surface` background is the standard for separation. 
- If elements feel "lost," increase the contrast between the surface tiers rather than adding a stroke.

### Surface Hierarchy & Nesting
Treat the UI as physical layers of fine paper or frosted glass.
- **Base Layer:** `surface` (#f9f9ff)
- **Content Sections:** `surface-container-low` (#f2f3fd) or `surface-container` (#ecedf7)
- **Interactive Elements:** `surface-container-lowest` (#ffffff) to provide a "pop" of clean white.

### The "Glass & Gradient" Rule
To elevate the experience, use **Glassmorphism** for floating action buttons or navigation overlays. 
- **Recipe:** `surface` color at 70% opacity + 20px Backdrop Blur.
- **Signature Textures:** Use a subtle linear gradient (from `primary` #005bbf to `primary_container` #1a73e8) on main CTAs to add "soul" and depth.

---

## 3. Typography: Editorial Authority
We utilize a dual-font strategy to balance character with legibility.

*   **Display & Headlines (Manrope):** A modern geometric sans-serif that feels approachable yet authoritative. Use `display-lg` (3.5rem) with tighter tracking (-2%) for a bold, editorial look in "Safe Zone" confirmations.
*   **Body & Labels (Inter):** A workhorse for readability. Use `body-md` (0.875rem) for all tracking data to ensure clarity at a glance.
*   **The Hierarchy Rule:** Establish dominance by pairing a large `headline-lg` (2rem) with a significantly smaller `label-md` (0.75rem) in `on_surface_variant` (#414754). This high-contrast scale prevents the UI from looking like a generic form.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are too "software-heavy." We use light and tone to imply height.

*   **The Layering Principle:** Depth is achieved by stacking. Place a `surface-container-lowest` card (#ffffff) on a `surface-container-high` (#e6e8f2) background to create a natural "lift."
*   **Ambient Shadows:** If an element must float (e.g., a critical alert), use a shadow with a 32px blur, 0px offset, and 6% opacity using a tint of `on_surface` (#191c23). Never use pure black shadows.
*   **The "Ghost Border" Fallback:** If accessibility requires a stroke, use `outline_variant` (#c1c6d6) at **15% opacity**. It should be felt, not seen.

---

## 5. Components: Refined Primitives

### Buttons
- **Primary:** Gradient fill (`primary` to `primary_container`), `xl` (1.5rem) roundedness. No border.
- **Secondary:** `surface_container_high` background with `on_primary_fixed_variant` text.
- **States:** On press, scale the button down to 98% rather than just changing color to provide a tactile, high-end feel.

### Cards & Lists
- **The Divider Ban:** Explicitly forbid 1px dividers between list items. Use 16px of vertical white space or a subtle shift from `surface-container-low` to `surface-container` to separate entries.
- **Layout:** Use asymmetrical padding (e.g., more padding on the left than the right) for header cards to create an editorial flow.

### Input Fields
- **Styling:** Use `surface_container_highest` (#e0e2ec) for the input track with a `none` border. 
- **Focus State:** Instead of a thick border, use a 2px "glow" using the `surface_tint` (#005bc0) at 30% opacity.

### Navigation (Disguised)
- Since the app is disguised as "Google Home," navigation icons should mimic smart home utility (e.g., a "House" icon for the dashboard, a "Shield" for safety settings) but rendered in a thin-stroke, custom weight.

---

## 6. Do’s and Don’ts

### Do:
- **Do** use `tertiary_fixed_dim` (#fbbc05) for non-critical alerts (e.g., "Battery Low") to maintain the warm palette.
- **Do** allow content to "bleed" off-edge in horizontal carousels to suggest continuity.
- **Do** use `secondary` (#006e2c) for "Safe" statuses—it is the ultimate "trust" color in this system.

### Don’t:
- **Don’t** use pure black (#000000) for text. Always use `on_surface` (#191c23) for a softer, premium contrast.
- **Don’t** use "Default" (0.5rem) corner radius for large containers; reserve `xl` (1.5rem) for main cards to emphasize the "Soft Minimalism" aesthetic.
- **Don’t** ever use a solid divider line. If you think you need one, use more whitespace.
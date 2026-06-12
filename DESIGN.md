# Design

Visual system for the **Mérito** redesign of the Sistema de Moeda Estudantil.
A warm recognition ledger: paper, ink, and a minted vermilion seal. All tokens
live in `:root` in `src/main/resources/public/css/app.css`. One file, no
framework, no JS.

## Theme

Light, warm. The surface is bone paper, not white; text is warm espresso, not
slate-black. The metaphor is an official document or statement, used in
daylight on phones and laptops.

## Color

OKLCH throughout. Every neutral is tinted warm (hue ~70). Color strategy is
**Committed-warm**: neutrals dominate the surface, and a single vermilion seal
accent carries identity, primary actions, and emphasis with real saturation.

### Neutrals (warm paper & ink)

| Token | OKLCH | Role |
|---|---|---|
| `--paper` | `oklch(0.985 0.008 80)` | page background |
| `--surface` | `oklch(0.997 0.004 80)` | cards / sheets |
| `--surface-sunken` | `oklch(0.965 0.010 78)` | sunken panels, table head, filters |
| `--ink` | `oklch(0.255 0.018 55)` | primary text, headings |
| `--ink-2` | `oklch(0.44 0.016 58)` | secondary text |
| `--ink-3` | `oklch(0.56 0.014 60)` | muted labels, captions |
| `--line` | `oklch(0.905 0.012 78)` | hairline borders |
| `--line-strong` | `oklch(0.845 0.014 75)` | input borders, stronger rules |

### Seal (signature vermilion / cinnabar)

| Token | OKLCH | Role |
|---|---|---|
| `--seal` | `oklch(0.585 0.175 38)` | brand, primary buttons, links, accents |
| `--seal-strong` | `oklch(0.515 0.17 36)` | hover / pressed |
| `--seal-ink` | `oklch(0.46 0.15 37)` | vermilion text on tint |
| `--seal-tint` | `oklch(0.955 0.03 50)` | faint wash (balance hero, selected rows) |
| `--seal-tint-line` | `oklch(0.89 0.055 48)` | border on tinted areas |

### Feedback (warm-tuned, distinct from seal)

- **Sucesso / recebido** — green `oklch(0.56 0.115 150)`, tint `oklch(0.955 0.035 150)`.
- **Aviso / pendente** — ochre `oklch(0.70 0.12 72)`, tint `oklch(0.955 0.05 80)`.
- **Erro / perigo** — cooler crimson `oklch(0.545 0.18 25)`, tint `oklch(0.955 0.035 25)`.

Crimson (hue 25) sits cooler and deeper than the seal (hue 38) and appears only
inside labelled alert boxes and destructive actions, so it never competes with
the brand. Status is never color-only: every pill and alert carries a dot/icon
and pt-BR text.

**Ledger amounts:** received is green; **spent is neutral ink, not red** —
redeeming a reward is healthy, not a loss.

## Typography

Two web fonts plus a system mono. Loaded from Google Fonts.

- **Display — `Fraunces`** (`opsz` optical sizing on). An editorial serif with
  character. Used *only* for display moments: page `h1` titles, the wordmark,
  and the large balance figure. Never for labels, buttons, table data.
- **UI / body — `Hanken Grotesk`** (400/500/600/700). Carries everything else:
  headings h2/h3, body, forms, buttons, tables, pills. Warm, legible, not Inter.
- **Mono — system stack** (`ui-monospace, "SF Mono", Menlo, monospace`) for
  coupon codes and verification codes.

Fixed rem scale, ratio ~1.2 with a wider jump at the display end:

`--t-xs 0.75` · `--t-sm 0.8125` · `--t-base 0.9375` · `--t-md 1` · `--t-lg 1.1875` ·
`--t-xl 1.5` · `--t-2xl 2` · `--t-3xl 2.75` (rem).

Prose capped at ~65ch; tables may run denser.

## Geometry, elevation, motion

- **Radius:** `--r-sm 5px` · `--r-md 9px` · `--r-lg 14px` · `--r-pill 999px`.
- **Borders do the structural work.** Warm hairlines (`--line`) define most
  edges; shadows are sparing and warm-tinted (`--shadow-sm`, `--shadow-md`).
- **Motion:** `--ease: cubic-bezier(0.22, 1, 0.36, 1)`, 150–200 ms. State only,
  never decoration. No animated layout properties. `prefers-reduced-motion`
  honored.
- **Focus:** 3px translucent seal ring on every interactive element.

## Components

Class contract preserved from the original markup so every Thymeleaf view
restyles without template churn.

- **Brand seal** — flat vermilion minted seal (concentric rings + serif `M`),
  inline SVG in the header and as a `::before` data-URI coin glyph on balances
  and perk costs. No gradients (flat reads as minted, not as AI gloss).
- **Card / sheet (`.card`, `--narrow` 460, default 760, `--wide` 1080)** — warm
  surface, hairline border, soft warm shadow. Reads as a document sheet.
- **Balance hero (`.saldo-card`)** — seal-tinted block, big Fraunces figure in
  seal ink, uppercase label, seal coin glyph. The one delight moment.
- **Buttons** — primary = solid seal; secondary = surface + hairline; ghost =
  bare. Full default/hover/active/disabled/focus set.
- **Tables (`.tabela-extrato`)** — hairline grid, sunken header, zebra on warm
  tint, seal-tint row hover. Tabular numerals.
- **Status pills / alerts** — tinted background + matching border + dot/icon +
  text, per feedback family.
- **Empty states (`.vazio`)** — dashed warm frame, ringed icon, teaching copy.
- **Catalog (`.galeria-vantagens` / `.card-vantagem`)** — responsive grid of
  perk cards with photo, title, seal-colored cost, company, redeem button.

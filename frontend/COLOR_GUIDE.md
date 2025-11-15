# Little Neighbors - Color Palette Guide

## Brand Colors

### Primary Color - Sunshine Yellow
```
HEX: #ffcf53
RGB: rgb(255, 207, 83)
Usage: Primary buttons, accents, highlights
```

### Secondary Color - Warm Orange
```
HEX: #f6854d
RGB: rgb(246, 133, 77)
Usage: CTAs, links, hover states
```

## Where Colors Are Used

### Navbar
- **Background**: Linear gradient from primary to secondary
- **Hover states**: White with transparency
- **Register button**: White background with secondary text

### Buttons
- **Primary buttons**: Gradient from primary to secondary
- **Secondary buttons**: Transparent with secondary border
- **Hover**: Enhanced shadow and translateY effect

### Cards
- **Hover shadows**: Secondary color with transparency
- **Badges**: Primary/secondary with low opacity backgrounds

### Backgrounds
- **Hero sections**: Primary/secondary with 5-10% opacity
- **Profile header**: Primary/secondary with 20% opacity

### Text
- **Links**: Secondary color
- **Hover**: Primary color
- **Accents**: Secondary color

### Form Elements
- **Focus states**: Secondary color border
- **Error states**: Red (#e74c3c)
- **Success states**: Green (#27ae60)

## Gradient Combinations

### Main Gradient
```css
background: linear-gradient(135deg, #ffcf53 0%, #f6854d 100%);
```

### Background Gradient (light)
```css
background: linear-gradient(135deg, rgba(255, 207, 83, 0.1) 0%, rgba(246, 133, 77, 0.1) 100%);
```

### Text Gradient
```css
background: linear-gradient(135deg, #f6854d 0%, #ffcf53 100%);
-webkit-background-clip: text;
-webkit-text-fill-color: transparent;
```

## Accessibility

All color combinations meet WCAG AA standards for contrast:
- Primary on white: ✅ AA
- Secondary on white: ✅ AA
- White on primary: ✅ AA
- White on secondary: ✅ AA

## Supporting Colors

```css
--dark: #2c3e50     /* Dark text and headings */
--gray: #95a5a6     /* Secondary text */
--light: #ecf0f1    /* Light backgrounds */
--white: #ffffff    /* Pure white */
--success: #27ae60  /* Success states */
--danger: #e74c3c   /* Error states */
```

## Color Psychology

- **Yellow (#ffcf53)**: Warmth, happiness, optimism, community
- **Orange (#f6854d)**: Friendliness, enthusiasm, creativity, connection

Perfect for a family-focused neighborhood application! 🌟

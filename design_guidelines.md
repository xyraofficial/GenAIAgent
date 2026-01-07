# Android GenAI Agent - Material Design Guidelines

## Design Approach
**Material Design 3 (Material You)** - Modern Android AI assistant with dynamic theming, elevated surfaces, and fluid interactions. Reference: Google Messages, ChatGPT mobile, and Gemini app aesthetics.

## Core Design Principles
- **Conversational Focus**: Chat interface is primary, with clean message hierarchy
- **Intelligent Feedback**: Clear AI thinking states and response streaming
- **Secure by Design**: Visual trust indicators without exposing technical implementation
- **Adaptive Layouts**: Seamless rotation and keyboard handling

---

## Typography System
**Font**: Roboto Flex (Material Design 3 standard)

- **Display**: 32sp, Medium - Onboarding headlines
- **Headline**: 24sp, Medium - Screen titles
- **Body Large**: 16sp, Regular - Message content
- **Body Medium**: 14sp, Regular - Input fields, secondary text
- **Label**: 12sp, Medium - Timestamps, status indicators

---

## Layout & Spacing
**Spacing Units**: 4dp, 8dp, 16dp, 24dp

- **Screen Padding**: 16dp horizontal, 8dp vertical minimum
- **Message Spacing**: 12dp between messages, 24dp between conversation groups
- **Component Padding**: 16dp internal padding for cards/surfaces
- **FAB Margin**: 16dp from screen edges

---

## Screen Structures

### 1. Welcome/Onboarding Screen
**Purpose**: First-launch experience showcasing AI capabilities

**Layout**:
- Hero illustration (2/3 screen height): Abstract AI brain network visual with gradient mesh, modern geometric patterns
- Title + tagline (centered): "Your Intelligent Assistant" with 3-4 capability pills below
- Primary CTA: Large rounded button "Get Started"
- Privacy note: Small text with lock icon at bottom

### 2. Main Chat Interface
**Structure** (Top to Bottom):
- **App Bar** (64dp height): Title "AI Agent" + overflow menu (settings, history, clear)
- **Conversation Area** (flex): Scrollable message list with pull-to-refresh
- **Input Bar** (56dp min): Text field + send button, expands on focus
- **System Bar**: Standard Android navigation

**Message Bubbles**:
- User messages: Aligned right, primary container surface, 16dp corner radius (top-left sharp)
- AI responses: Aligned left, secondary container surface, 16dp corner radius (top-right sharp)
- Max width: 80% of screen width
- Padding: 12dp vertical, 16dp horizontal
- Timestamp: 11sp below bubble, subtle color

**Special States**:
- Typing indicator: Three animated dots in AI bubble
- Streaming response: Progressive text reveal with subtle shimmer
- Error state: Outlined red container with retry button

### 3. Settings Screen
**Layout**:
- **Header**: Large title "Settings" with back button
- **Sections** (List Items with dividers):
  - API Configuration (with shield icon - shows "Secure" badge, no key details)
  - Theme Selection (System/Light/Dark toggle group)
  - Chat History Management (Clear/Export options)
  - About & Privacy

---

## Component Library

### Cards & Surfaces
- **Elevation**: 1dp default, 2dp on press, 3dp for dialogs
- **Corner Radius**: 16dp for cards, 28dp for buttons
- **Containers**: Use surface variants for hierarchy (surface, surfaceVariant, surfaceContainerHigh)

### Buttons
- **Primary FAB** (Send): 56dp diameter, surface tint layer, elevation 3dp
- **Extended FAB** (New Chat): Height 56dp, icon + label, appears on scroll up
- **Text Buttons**: 40dp height, 16dp horizontal padding
- **Filled Buttons**: 48dp height, primary container color

### Input Fields
- **Outlined Style**: 56dp height, 16dp padding, 12dp corner radius
- **Hint Text**: "Ask me anything..." with sparkle icon prefix
- **Max Lines**: Expands to 4 lines, then scrolls internally
- **Character Counter**: Show at 80% of limit if set

### Bottom Sheets
- **Model Selector**: Half-screen sheet with radio buttons for GPT models
- **History**: Full-screen sheet with searchable conversation list
- **Corner Radius**: 28dp top corners

---

## Interaction Patterns
- **Send Message**: Button scales 0.95x on press, haptic feedback
- **Message Tap**: Ripple effect, optional copy/share menu
- **Pull to Refresh**: Standard Material refresh indicator
- **Scroll to Bottom**: FAB appears when scrolled up 200dp+
- **Keyboard**: Smooth resize animation, input bar stays anchored

---

## Visual Enhancements
- **Status Indicators**: Small chips showing "Secure Connection" with lock icon in chat header
- **Empty State**: Centered illustration with suggestion chips ("Summarize text", "Creative ideas", "Code help")
- **Loading**: Circular progress with "Processing..." text for initial response
- **Success Toast**: Brief bottom snackbar for actions (copied, saved)

---

## Images
**Hero Image** (Onboarding): Yes - Abstract AI visualization
- Description: Gradient mesh with interconnected nodes, purple-blue tones, floating geometric elements suggesting neural networks
- Placement: Top 60% of onboarding screen
- Style: Modern, soft 3D illustration

**Empty State Graphic**: Abstract chat bubbles with sparkles, minimal line art, centered when no conversations exist

---

**Design Mandate**: Rich, polished Material You experience with smooth animations, intelligent feedback states, and premium feel throughout. Every interaction reinforces the AI's capability and trustworthiness.
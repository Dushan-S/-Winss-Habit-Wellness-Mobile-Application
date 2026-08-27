# WellnessApp - Your Personal Wellness Companion

A comprehensive Android wellness application built with Kotlin that helps you track habits, log moods, and stay hydrated with beautiful purple-themed UI.

## 🌟 Features

### 🏠 Home Dashboard
- **Welcome Message**: Personalized greeting
- **7-Day Calendar**: Visual progress tracking with completion indicators
- **Progress Pie Chart**: Real-time habit completion visualization
- **Today's Habits**: Quick access to daily habits with completion toggle
- **Current Mood Display**: Shows your latest mood entry

### 📝 Habit Tracking
- **Add/Edit/Delete Habits**: Full CRUD operations for habit management
- **Progress Bar**: Visual representation of daily progress
- **Completion Toggle**: Easy one-tap habit completion
- **Persistent Storage**: All habits saved using SharedPreferences

### 😊 Mood Journal
- **Emoji Selector**: 5-point mood scale with emoji selection
- **Mood Notes**: Optional text entries for detailed logging
- **Mood Trends Chart**: 7-day mood visualization using MPAndroidChart
- **Mood History**: Scrollable list of past mood entries
- **Share Functionality**: Share last 5 mood entries via implicit intent

### ⚙️ Settings & Reminders
- **Hydration Reminders**: Customizable interval settings
- **WorkManager Integration**: Background notification scheduling
- **Settings Persistence**: All preferences saved and restored

### 📱 Home Screen Widget
- **Progress Widget**: Shows today's habit completion percentage
- **Real-time Updates**: Automatically refreshes with app data
- **Purple Theme**: Beautiful gradient background

## 🎨 Design Features

### Purple Color Palette
- **Primary Purple**: #FF6B46C1
- **Primary Dark**: #FF4A2C8A
- **Primary Light**: #FF9B7DD1
- **Accent Purple**: #FF8E44AD
- **Background**: #FFF8F5FF
- **Surface**: #FFFFFFFF

### Material Design 3
- Modern Material Design components
- Consistent purple theming throughout
- Smooth animations and transitions
- Responsive layouts

## 🏗️ Architecture

### Project Structure
```
app/
├── src/main/java/com/example/winss/
│   ├── MainActivity.kt                 # Bottom Navigation Host
│   ├── fragments/
│   │   ├── HomeFragment.kt           # Dashboard with calendar & progress
│   │   ├── HabitFragment.kt          # Habit management
│   │   ├── MoodFragment.kt           # Mood logging & trends
│   │   └── SettingsFragment.kt       # Hydration settings
│   ├── adapters/
│   │   ├── HabitAdapter.kt            # Habit RecyclerView adapter
│   │   ├── TodayHabitAdapter.kt      # Today's habits adapter
│   │   └── MoodAdapter.kt             # Mood history adapter
│   ├── models/
│   │   ├── Habit.kt                  # Habit data model
│   │   └── MoodEntry.kt               # Mood entry data model
│   ├── utils/
│   │   ├── SharedPrefManager.kt      # Data persistence
│   │   └── NotificationManager.kt    # WorkManager integration
│   ├── reminders/
│   │   └── HydrationWorker.kt        # Background notifications
│   └── widget/
│       └── HabitWidgetProvider.kt    # Home screen widget
└── src/main/res/
    ├── layout/                       # All fragment & item layouts
    ├── drawable/                     # Icons & backgrounds
    ├── values/                       # Colors, strings, themes
    └── xml/                          # Widget configuration
```

## 🛠️ Technical Implementation

### Dependencies
- **Material Design 3**: Modern UI components
- **MPAndroidChart**: Beautiful mood trends visualization
- **WorkManager**: Reliable background task scheduling
- **Navigation Component**: Fragment navigation
- **RecyclerView**: Efficient list management
- **SharedPreferences**: Data persistence

### Key Components

#### Data Models
- **Habit**: ID, name, description, completion status, date
- **MoodEntry**: ID, emoji, note, date, time, mood value (1-5)

#### SharedPreferences Manager
- Centralized data management
- JSON serialization with Gson
- Date/time utilities
- Progress calculations

#### WorkManager Integration
- Periodic hydration reminders
- Background notification scheduling
- Battery-optimized execution

#### Home Screen Widget
- Real-time progress display
- Click-to-open app functionality
- Automatic updates every 30 minutes

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.8+

### Installation
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Build and run on device/emulator

### Permissions
- `POST_NOTIFICATIONS`: For hydration reminders
- `WAKE_LOCK`: For background work

## 📱 Usage Guide

### Adding Habits
1. Navigate to Habits tab
2. Tap the floating action button
3. Enter habit name and description
4. Save to add to your list

### Logging Mood
1. Go to Mood tab
2. Select an emoji (1-5 scale)
3. Add optional note
4. Tap "Add Mood" to save

### Setting Hydration Reminders
1. Open Settings tab
2. Set desired interval in minutes
3. Tap "Save Settings"
4. Notifications will start automatically

### Using the Widget
1. Long-press home screen
2. Select "Widgets"
3. Find "WellnessApp"
4. Add to home screen
5. Tap to open app

## 🎯 Key Features Explained

### 7-Day Calendar
- Shows last 6 days + today
- Green indicators for days with completed habits
- Today highlighted with purple background
- Click to view historical progress

### Progress Visualization
- Pie chart shows completed vs remaining habits
- Real-time percentage calculation
- Color-coded progress indicators

### Mood Trends
- Line chart displays 7-day mood history
- 1-5 scale visualization
- Smooth animations and interactions

### Data Persistence
- All data stored locally using SharedPreferences
- JSON serialization for complex objects
- Automatic backup and restore

## 🔧 Customization

### Adding New Habit Types
1. Extend the Habit model
2. Update SharedPrefManager
3. Modify adapters and UI

### Changing Color Scheme
1. Update colors.xml
2. Modify theme in themes.xml
3. Adjust drawable resources

### Adding New Mood Types
1. Update emoji selector in MoodFragment
2. Modify mood value mapping
3. Update chart visualization

## 📊 Data Flow

1. **User Input** → Fragment
2. **Fragment** → SharedPrefManager
3. **SharedPrefManager** → SharedPreferences
4. **Data Changes** → UI Updates
5. **Background Tasks** → WorkManager

## 🐛 Troubleshooting

### Common Issues
- **Widget not updating**: Check widget permissions
- **Notifications not showing**: Verify notification permissions
- **Chart not displaying**: Ensure MPAndroidChart dependency is included

### Performance Tips
- Use RecyclerView for large lists
- Implement proper view recycling
- Optimize chart data updates

## 🤝 Contributing

1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- Material Design 3 for beautiful components
- MPAndroidChart for excellent charting
- Android WorkManager for reliable background tasks
- Kotlin for modern Android development

---

**WellnessApp** - Track your habits, log your mood, and stay hydrated! 💜

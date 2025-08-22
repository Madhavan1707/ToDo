# Weekly To-Do App

![API](https://img.shields.io/badge/API-26%2B-blue) 
![Language](https://img.shields.io/badge/Language-Java-orange) 
![Architecture](https://img.shields.io/badge/Architecture-MVVM-green) 
![Database](https://img.shields.io/badge/Database-Room-lightgrey) 
![Background](https://img.shields.io/badge/Background-WorkManager-yellow) 

An Android application that helps you manage and complete daily tasks across the week. This project demonstrates a modern Android architecture using MVVM, local persistence with Room, background scheduling with WorkManager, and Material Design components for a smooth and interactive user experience.  

## Overview

The Weekly To-Do App organizes your tasks into 7 tabs (Monday to Sunday). Each day starts with **5 default tasks** (Creatine, 10k steps, Food, Workout, Clothes) while also allowing you to add custom tasks.  

Users can:  
- View tasks for each day of the week.  
- Mark tasks as complete (earning points, moving them to the bottom, dimmed in light grey).  
- Track daily progress with a circular progress indicator and scorecard.  
- Receive **hourly notifications** (between 8 AM–11:59 PM) reminding them of remaining tasks.  
- Celebrate with **confetti** when all tasks for the day are done.  
- Automatically carry over unfinished tasks to the next day at **00:05 AM**.  

The project follows a clean architecture pattern with separation of concerns:  
- **Presentation layer:** MVVM with LiveData and ViewModel.  
- **Data layer:** Room database for storing tasks and progress locally.  
- **Background tasks:** WorkManager for hourly reminders and daily carry-over.  
- **UI:** Material components with RecyclerView, ViewPager2, TabLayout, and Lottie animations.  

## Features

- **Daily Default Tasks:** Automatically seeded tasks (Creatine, 10k steps, Food, Workout, Clothes).  
- **Custom Tasks:** Add new tasks via a FloatingActionButton.  
- **Task Completion:** Completed tasks move to the bottom and appear faded in light grey instead of disappearing.  
- **Scorecard & Progress Ring:** Displays points earned (10 points per task) and daily completion percentage.  
- **Confetti Animation:** A fun Lottie animation plays when all tasks are done.  
- **Weekly Tabs:** Swipe through Mon–Sun tabs using ViewPager2 with TabLayout.  
- **Hourly Reminders:** Notifications prompt you if tasks remain (within 08:00–23:59).  
- **Carry-Over:** Incomplete tasks at midnight automatically appear in the next day’s list.  
- **Light Theme:** Simple, clean Material 3 styling.  

## Libraries Used

- **Room:** Local database for tasks, progress, and carry-over logic.  
- **Lifecycle:** ViewModel + LiveData for reactive UI updates.  
- **WorkManager:** Background workers for hourly notifications and midnight carry-over.  
- **Material Components:** Material Design UI elements such as progress indicators, FABs, and cards.  
- **Lottie:** For confetti celebration animation.  
- **RecyclerView (ListAdapter + DiffUtil):** Efficient and smooth task list management.  
- **ViewPager2 + TabLayout:** Weekly navigation.  

## Setup

### Prerequisites

- Android Studio (latest stable).  
- Android API Level 26+ minimum (target latest).  
- Java 17 (configured in Gradle).  

### Installation

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/<your-username>/<your-repo>.git
   ```
2. **Open the project** in Android Studio.  
3. **Sync Gradle** to download dependencies.  
4. **Run the App** on an emulator or physical device.  

### Permissions

- **POST_NOTIFICATIONS** (required for Android 13+).  

## Capabilities and Limitations

### Capabilities
- Default + custom daily tasks with persistence.  
- Non-destructive completion (tasks never vanish).  
- Gamified progress tracking with points and a circular progress ring.  
- Hourly reminders (time-window safe, battery-friendly).  
- Confetti celebration for 100% completion.  
- Carry-over of unfinished tasks to the next day.  

### Limitations
- No cloud sync (offline-only, local to device).  
- Notifications are periodic (min 15 min interval due to WorkManager limits).  
- No advanced analytics (weekly charts / streaks planned in roadmap).  
- Limited notification actions (e.g., snooze or quick-complete not yet implemented).  

## Contributing

Feel free to fork this repository and submit pull requests. When contributing, please follow standard Android coding guidelines and best practices.  

---

📸 *Screenshots coming soon*  
(Add screenshots or GIFs of the progress ring, tasks list, and confetti here like in your Movies app README.)  

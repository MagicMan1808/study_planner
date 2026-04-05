# 📚 Study Planner

![Java](https://img.shields.io/badge/Java-17%2B-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue)
![Status](https://img.shields.io/badge/Status-Active-success)
![License](https://img.shields.io/badge/License-Free-lightgrey)

> 🧠 Intelligent study planning with automatic schedule generation

A desktop application built with **JavaFX** that helps students efficiently plan their study sessions based on deadlines, workload, and difficulty.

---

## 🚀 Features

- ➕ Create, edit, and delete tasks  
- 📅 Deadline-based scheduling  
- ⚖️ Difficulty weighting (1–5)  
- 🧠 Automatic study plan generation  
- 📊 Daily learning session distribution  
- 📤 Export study plans as CSV  
- 🎨 Modern dark-mode UI  
- ⚠️ Smart handling of expired tasks  
- 🔄 Automatic invalidation of outdated plans  

---

## 📸 Screenshots

### 🏠 Main Interface
![Main UI](docs/screenshots/main_ui.png)

### ➕ Task Management
![Tasks](docs/screenshots/add_task.png)

### 📊 Study Plan View
![Plan](docs/screenshots/plan_view.png)

> 📁 Store screenshots in `docs/screenshots/`

---

## 🧱 Project Structure

```bash
study_planner/
│
├── model/
│   ├── Task.java
│   └── StudySession.java
│
├── planner/
│   ├── StudyPlannerAlgorithm.java
│   ├── PlanningService.java
│   └── PlanExporter.java
│
├── storage/
│   ├── StorageManager.java
│   └── data.json
│
├── ui/
│   ├── MainApp.java
│   └── style.css
```

---

## 📸 Screenshots

- Java17 or higher
- JavaFX SDK (21+ recommended)

---

## ▶️ Run the Project

### 🔨 Compile

```bash
javac --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls -sourcepath . planner/*.java model/*.java storage/*.java ui/*.java
```
### ▶️ Run
```bash
java --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls ui.MainApp
```

---

## 🧑‍💻 Usage

### 1. Add a Task

- Enter task name
- Set deadline (DD.MM.YYYY)
- Select difficults (1-5)
- Enter estimated hours

### 2. Edit Task

- Select a task
- Click "Task bearbeiten"
- Modify values and save

### 3. Delete a task

- Select a task
- Click "Task löschen"

### 4. Generate Study Plan

- Click "Plan anzeigen"
- The system generates a daily learning schedule

### 5. Export Study Plan
- Click "Plan exportieren"
- Choose a file location
- Save as .csv

---

## 🧠 Planning Algorithm

The Scheduling logic considers:
- ⏳ Remaining days until deadline
- 📚 Total workload (estimated hours)
- ⚖️ Task difficulty

### Behavior:
- Tasks close in time (≤ 3 days apart) are handled in parallel
- Near deadlines → higher priority
- Completed workload → revision sessions added
- Daily study time dynamically adjusted

---

## ⚠️ Important Logic
- 🔄 Any task change invalidates the current plan
- 🚫 Export is only possible with a valid plan
- ⚠️ Expired tasks trigger warnings in UI

---

## 📂 CSV Export Format

```csv
Date,Task,Duration
04.05.2026,Programming 1,2h
05.05.2026,Math,3h
```

---

## 🛠️ Tech Stack
- Java
- JavaFX
- OOP (Modular architecture)
- Custom scheduling algorithm

---

## 🧩 Architecture
- **Model** → Data structures (Task, StudySession)
- **Planner** → Algorithm & export logic
- **Storage** → Persistence layer
- **UI** → JavaFX frontend

---

## 📈 Future Improvements
- ⏱️ Adaptive session lengths (no fixed blocks)
- 📆 Calendar integration (Google Calendar, etc.)
- 🔔 Notifications & reminders
- 📊 Progress tracking system
- ☁️ Cloud synchronization

---

## 🐞 Known Limitations

- No real calender integration
- Basic scheduling heuristic
- Local storage only
- No undo / redo functionality

---

## 📜 License

This project is free to use for educational and personal purposes.

---

## 👨‍💻 Author

Yousef Mahmuod El-Jarousha

---

## ⭐ Why this Project?

This project demonstrates:
- Clean seperation of concerns
- Practical algorithm design
- JavaFX UI developement
- Real-world problem solving
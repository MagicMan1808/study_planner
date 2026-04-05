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

---

## 📸 Screenshots

- Java17 or higher
- JavaFX SDK (21+ recommended)

---

## ▶️ Run the Project

### 🔨 Compile

```bash
javac --module-path "C:\javafx-sdk-26\lib" --add-modules javafx.controls -sourcepath . planner/*.java model/*.java storage/*.java ui/*.java
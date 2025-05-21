# 🎓 Student Course Planner (CLI + Gemini 2.5)

A command-line based smart assistant that helps students create optimized semester course plans based on curriculum requirements, prerequisites, and personal preferences such as workload, topic interest, and availability.

Built with **Java** and integrated with **Google Gemini 2.5** via API.

---

## 🚀 Features

- ✅ **Interactive CLI** — Guided prompts collect your degree, year, course preferences, etc.
- ✅ **LLM‑Powered Planning** — Leverages Google Gemini 2.5 to interpret preferences and generate conflict‑free schedules.
- ✅ **Curriculum Awareness** — Respects prerequisites, degree requirements, and credit limits.
- ✅ **Flexible Preferences** — Customize by instructor, days of week, time slots, interests, GenEd areas.
- ✅ **PostgreSQL Backend** — Stores course, schedule, instructor, student and enrollment data.
- ✅ **Modular Design** — Clear separation: CLI, service layer, data models, and LLM integration.

---

## 🛠️ Tech Stack

- **Language**: Java
- **CLI**: Scanner‑based interactive shell
- **LLM**: Google Gemini 2.5 
- **DB**: PostgreSQL
- **Logging**: SLF4J + Simple logger
- **Version Control**: Git + GitHub

---

## 🔧 Setup Instructions

1. **Clone the repository**  
   ```bash
   git clone git@github.com:Sargis-Hovsepyan/student_course_planner.git
   cd student_course_planner
   
2. **Start up**
   ```
---
## 🔑 API & Environment Notes
- **Gemini Integration**
   - The app reads your GEMINI_API_KEY from the .env file via the Environment utility.
   - Ensure your key is valid and has sufficient quota.
   - The LLM prompt is built in buildPrompt(...) and sent via GeminiClient.sendPrompt(...).

- **Database Connection**
   - Connection parameters (DB_URL, DB_USER, DB_PASSWORD) come from .env.
   - The DatabaseManager handles opening and closing the JDBC connection.
   - Data models (in model/) provide CRUD operations for each table—just call insert(), selectById(), update…(), and delete() on the appropriate class.
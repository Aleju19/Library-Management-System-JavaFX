# 📚 Library & Bookshop Management System

A sophisticated Java application that handles library inventory and bookshop sales, featuring a robust relational database and **CDI** for dependency management.

## 🚀 Key Technical Highlights
* **Database Migrations:** Managed with **Flyway** for version control of the database schema.
* **Complex Relationships:** Implements Many-to-Many (Books-Authors) and One-to-Many (Orders-LineItems) relationships.
* **CDI Architecture:** Utilizes Contexts and Dependency Injection to ensure a decoupled and testable codebase.
* **Build Automation:** Fully managed with **Gradle**.

## 🛠 Tech Stack
* **Language:** Java
* **UI:** JavaFX & Scene Builder
* **Database:** PostgreSQL
* **Tools:** Flyway, Gradle, IntelliJ IDEA

## 🏗 Schema Overview
The system includes:
- `books` & `authors`: Managed via a join table for flexible authorship.
- `inventory`: Real-time tracking of supplied and sold units.
- `purchase_orders` & `line_items`: Complete sales workflow from customer to delivery.

## ⚙️ Setup
1. Clone this repository.
2. Create a PostgreSQL database.
3. The schema will be automatically managed by Flyway (or you can manually run `database_schema.sql`).
4. Run via IntelliJ or use `./gradlew run`.

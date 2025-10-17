# Expense-Tracker

Java desktop application (Swing + JDBC + MySQL) for tracking daily expenses, categorizing them, and generating reports (daily, weekly, monthly, and category-wise).

## Features

- User authentication (register / login, hashed passwords)
- Manage categories (with optional budget limit field placeholder)
- Record expenses (date, amount, category, payment method, description)
- Daily report generation with CSV export (extensible structure for weekly/monthly)
- Configurable via properties file with secure local override support

## Screenshots

<img width="683" height="526" alt="image" src="https://github.com/user-attachments/assets/e98bae7c-8116-4407-a1cc-4d2dd7c8dadc" />

<img width="1242" height="880" alt="image" src="https://github.com/user-attachments/assets/d1dafdde-9ebc-463f-a51c-169062cc9a00" />


## Tech Stack

- Java 24
- Swing UI
- JDBC + MySQL
- Maven build
- SLF4J (simple binding) logging
- JUnit 5 (initial test scaffold)

## Project Structure (high level)

```
src/main/java/com/expensetracker
	model/      (Category, Expense, UserAccount, enums)
	dao/        (DAO interfaces)
	dao/impl/   (JDBC implementations)
	service/    (Business logic services)
	controller/ (AppController)
	view/       (Swing UI frames & panels)
	report/     (Report abstractions & generators)
	util/       (DBConnectionManager, hashing, logging helpers)
src/main/resources/
	config.properties (default config)
	schema.sql        (database DDL)  [ADD if not yet copied]
```

## Getting Started

### 1. Prerequisites

- Java 17 installed (`java -version`)
- Maven 3.8+ (`mvn -v`)
- MySQL 8.x (or compatible) running

### 2. Database Setup

Create the database and tables:

1. Create an empty schema (adjust name if you like):
	 ```sql
	 CREATE DATABASE expense_tracker CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
	 ```
2. Run the contents of `schema.sql` against that database (CLI or a GUI client). This creates tables: `category`, `expense`, `recurring_expense_template` (future), `user_account` and indexes.
3. (Optional) Insert an initial admin user OR register via the UI.

### 3. Configuration

The application loads configuration in the following precedence order (later wins):

1. `config.properties` on the classpath (required, committed)
2. `config.local.properties` on the classpath (optional, untracked)
3. `config.local.properties` in the working directory beside the jar (optional, untracked)
4. Environment variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`

Default template (`src/main/resources/config.properties`):
```
db.url=jdbc:mysql://localhost:3306/expense_tracker?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=CHANGE_ME
db.password=CHANGE_ME
```

Create `config.local.properties` (DO NOT COMMIT) with your real credentials, e.g.:
```
db.user=asus
db.password=3005
```
Or export environment variables (Windows PowerShell example):
```powershell
$env:DB_USER="asus"
$env:DB_PASSWORD="3005"
```

### 4. Build

Run Maven compile and package:
```powershell
mvn clean package
```
The runnable jar will be in `target/Expense-Tracker-*-jar-with-dependencies.jar` (depending on the configured finalName/version).

### 5. Run

While inside the project root:
```powershell
java -jar target/*-jar-with-dependencies.jar
```
Login window appears. Register a new user if none exists (credentials stored hashed in `user_account`).

### 6. Daily Report Export

The Reports tab lets you generate a daily report (for today) and exports a CSV file under the `reports/` directory (created if missing). Future enhancements will add weekly/monthly rollups.

### 7. Testing

Execute tests:
```powershell
mvn test
```
Currently only a placeholder test; more coverage to come.

## Local Override & Security Notes

- Never commit real passwords. `config.local.properties` is ignored by Git (see `.gitignore`).
- Prefer environment variables in production or when sharing machines.
- Consider restricting DB user privileges to only the required CRUD operations.

## Roadmap (Planned)

- Weekly & monthly aggregated reports
- Budget limit alerts (background thread)
- Recurring expense scheduler
- Dashboard charts / visual summaries
- Enhanced test coverage & integration tests

## Troubleshooting

| Issue | Possible Cause | Resolution |
|-------|----------------|-----------|
| Cannot connect (SQLException) | Wrong credentials / DB down | Verify MySQL running and credentials in override file or env vars |
| Tables missing | schema.sql not applied | Run schema script against your database |
| Empty reports | No expenses for the day | Add sample expenses then regenerate |
| UI freezes on long operations | JDBC call on EDT | (Planned) Move heavy operations to background threads |

## License

See `LICENSE` file.


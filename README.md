# Employee Analyzer

A command-line tool that analyzes an organization’s employee data from a CSV file. It detects:
- Managers earning less than a direct subordinate
- Employees whose reporting chain to the top-level (CEO) exceeds a configurable threshold

## Features
- CSV ingestion with validation (ID, names, salary, managerId)
- Salary hierarchy check (manager vs. subordinate)
- Reporting line depth analysis
- Clear textual report output
- Robust error handling with meaningful messages
- Unit and integration tests (JUnit 5 + Mockito)

## Requirements
- Java 21+
- Maven 3.9+

## Build
```bash
mvn clean package
```
Jar produced: `target/employee-analyzer-1.0.0.jar`

## Run
```bash
java -jar target/employee-analyzer-1.0.0.jar path/to/employees.csv
```
Example:
```bash
java -jar target/employee-analyzer-1.0.0.jar src/main/resources/employees.csv
```
If omitted, it attempts a default `employees.csv` from the classpath (adjust logic if needed).

## CSV Format
Header required:
```
Id,firstName,lastName,salary,managerId
```
Rules:
- Id: non-empty string
- firstName / lastName: non-empty
- salary: numeric (double)
- managerId: blank for top-level (CEO) or an existing employee Id

## Sample Usage
### Example input (`employees.csv`)
```csv
Id,firstName,lastName,salary,managerId
123,Joe,Doe,60000,
124,Martin,Chekov,45000,123
125,Bob,Ronstad,47000,123
300,Alice,Hasacat,50000,124
305,Brett,Hardleaf,34000,300
```

### Expected output
```text
Reading employee data from: src/main/resources/employees.csv
Successfully loaded 5 employees.

Analyzing organizational structure...

Organizational Analysis Report
==============================

1. Managers Earning Less Than Their Subordinates
-------------------------------------------------
- Manager Martin Chekov (ID: 124) earns 45000.0, but their subordinate Alice Hasacat (ID: 300) earns 50000.0.

2. Employees with Long Reporting Lines (More Than 4 Levels)
------------------------------------------------------------
- No issues found.
```

## Error Handling
Typical errors:
- Invalid CSV column count
- Non-numeric salary
- Empty ID or name fields
- Missing file or unreadable path
Application exits with a clear error message.

## Testing
```bash
mvn test
```
JUnit 5 + Mockito tests cover parsing, analysis, reporting, and integration flow.

## Project Structure (simplified)
```
src/main/java/com/company/analyzer/
  EmployeeAnalyzerApplication.java
  service/
  model/
  exception/
  util/
src/main/resources/
  employees.csv
```

## Version
Current artifact: 1.0.0



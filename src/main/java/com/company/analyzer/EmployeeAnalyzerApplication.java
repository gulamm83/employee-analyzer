package com.company.analyzer;

import com.company.analyzer.exception.EmployeeDataException;
import com.company.analyzer.model.AnalysisReport;
import com.company.analyzer.model.Employee;
import com.company.analyzer.service.CsvReaderService;
import com.company.analyzer.service.OrganizationAnalyzer;
import com.company.analyzer.service.ReportGenerator;

import java.util.List;

/**
 * Main application for analyzing employee organizational structure.
 *
 * Usage: java -jar employee-analyzer.jar <path-to-csv-file>
 *
 * The application reads employee data from a CSV file and analyzes:
 * 1. Manager salaries relative to their subordinates
 * 2. Reporting line lengths from employees to CEO
 */
public class EmployeeAnalyzerApplication {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java -jar employee-analyzer.jar <path-to-csv-file>");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            // Initialize services
            CsvReaderService csvReader = new CsvReaderService();
            OrganizationAnalyzer analyzer = new OrganizationAnalyzer();
            ReportGenerator reportGenerator = new ReportGenerator();

            // Read employee data
            System.out.println("Reading employee data from: " + filePath);
            List<Employee> employees = csvReader.readEmployees(filePath);
            System.out.println("Successfully loaded " + employees.size() + " employees.");
            System.out.println();

            // Analyze organization
            System.out.println("Analyzing organizational structure...");
            AnalysisReport report = analyzer.analyze(employees);
            System.out.println();

            // Generate and print report
            reportGenerator.printReport(report);

        } catch (EmployeeDataException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
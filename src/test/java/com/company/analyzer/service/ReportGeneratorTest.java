package com.company.analyzer.service;
  
import com.company.analyzer.model.AnalysisReport;  
import com.company.analyzer.model.Employee;  
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;
  
import java.io.ByteArrayOutputStream;  
import java.io.PrintStream;
  
import static org.junit.jupiter.api.Assertions.*;
  
class ReportGeneratorTest {
  
    private ReportGenerator reportGenerator;  
    private ByteArrayOutputStream outputStream;  
    private PrintStream printStream;
  
    @BeforeEach  
    void setUp() {  
        reportGenerator = new ReportGenerator();  
        outputStream = new ByteArrayOutputStream();  
        printStream = new PrintStream(outputStream);  
    }
  
    @Test  
    void testPrintReportWithNoIssues() {  
        // Arrange  
        AnalysisReport report = new AnalysisReport();
  
        // Act  
        reportGenerator.printReport(report, printStream);  
        String output = outputStream.toString();
  
        // Assert  
        assertTrue(output.contains("ORGANIZATIONAL ANALYSIS REPORT"));  
        assertTrue(output.contains("No issues found"));  
    }
  
    @Test  
    void testPrintReportWithUnderpaidManager() {  
        // Arrange  
        AnalysisReport report = new AnalysisReport();  
        Employee manager = new Employee("2", "John", "Manager", 40000, "1");  
        report.addUnderpaidManager(new AnalysisReport.SalaryIssue(manager, 40000, 8000));
  
        // Act  
        reportGenerator.printReport(report, printStream);  
        String output = outputStream.toString();
  
        // Assert  
        assertTrue(output.contains("UNDERPAID MANAGERS"));  
        assertTrue(output.contains("John Manager"));  
        assertTrue(output.contains("ID: 2"));  
        assertTrue(output.contains("$40,000"));  
        assertTrue(output.contains("$8,000"));  
    }
  
    @Test  
    void testPrintReportWithOverpaidManager() {  
        // Arrange  
        AnalysisReport report = new AnalysisReport();  
        Employee manager = new Employee("2", "Jane", "Boss", 70000, "1");  
        report.addOverpaidManager(new AnalysisReport.SalaryIssue(manager, 40000, 10000));
  
        // Act  
        reportGenerator.printReport(report, printStream);  
        String output = outputStream.toString();
  
        // Assert  
        assertTrue(output.contains("OVERPAID MANAGERS"));  
        assertTrue(output.contains("Jane Boss"));  
        assertTrue(output.contains("ID: 2"));  
        assertTrue(output.contains("$70,000"));  
        assertTrue(output.contains("$10,000"));  
    }
  
    @Test  
    void testPrintReportWithLongReportingLine() {  
        // Arrange  
        AnalysisReport report = new AnalysisReport();  
        Employee employee = new Employee("6", "Deep", "Employee", 40000, "5");  
        report.addLongReportingLine(new AnalysisReport.ReportingLineIssue(employee, 5, 1));
  
        // Act  
        reportGenerator.printReport(report, printStream);  
        String output = outputStream.toString();
  
        // Assert  
        assertTrue(output.contains("LONG REPORTING LINES"));  
        assertTrue(output.contains("Deep Employee"));  
        assertTrue(output.contains("ID: 6"));  
        assertTrue(output.contains("Reporting levels: 5"));  
        assertTrue(output.contains("Excess levels: 1"));  
    }
  
    @Test  
    void testPrintReportWithMultipleIssues() {  
        // Arrange  
        AnalysisReport report = new AnalysisReport();
          
        Employee underpaid = new Employee("2", "Under", "Paid", 40000, "1");  
        report.addUnderpaidManager(new AnalysisReport.SalaryIssue(underpaid, 40000, 8000));
          
        Employee overpaid = new Employee("3", "Over", "Paid", 70000, "1");  
        report.addOverpaidManager(new AnalysisReport.SalaryIssue(overpaid, 40000, 10000));
          
        Employee deepEmployee = new Employee("6", "Deep", "Employee", 40000, "5");  
        report.addLongReportingLine(new AnalysisReport.ReportingLineIssue(deepEmployee, 5, 1));
  
        // Act  
        reportGenerator.printReport(report, printStream);  
        String output = outputStream.toString();
  
        // Assert  
        assertTrue(output.contains("UNDERPAID MANAGERS"));  
        assertTrue(output.contains("OVERPAID MANAGERS"));  
        assertTrue(output.contains("LONG REPORTING LINES"));  
        assertTrue(output.contains("Under Paid"));  
        assertTrue(output.contains("Over Paid"));  
        assertTrue(output.contains("Deep Employee"));  
    }  
}  
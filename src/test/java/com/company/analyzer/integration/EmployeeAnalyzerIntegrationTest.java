package com.company.analyzer.integration;
  
import com.company.analyzer.exception.EmployeeDataException;  
import com.company.analyzer.model.AnalysisReport;  
import com.company.analyzer.model.Employee;  
import com.company.analyzer.service.CsvReaderService;  
import com.company.analyzer.service.OrganizationAnalyzer;  
import com.company.analyzer.service.ReportGenerator;  
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.io.TempDir;
  
import java.io.ByteArrayOutputStream;  
import java.io.IOException;  
import java.io.PrintStream;  
import java.nio.file.Files;  
import java.nio.file.Path;  
import java.util.List;
  
import static org.junit.jupiter.api.Assertions.*;
  
class EmployeeAnalyzerIntegrationTest {
  
    @Test  
    void testCompleteWorkflow(@TempDir Path tempDir) throws IOException, EmployeeDataException {  
        // Arrange - Create test CSV file  
        Path csvFile = tempDir.resolve("employees.csv");  
        String content = """  
            Id,firstName,lastName,salary,managerId  
            123,Joe,Doe,60000,  
            124,Martin,Chekov,30000,123  
            125,Bob,Ronstad,47000,123  
            300,Alice,Hasacat,50000,124  
            305,Brett,Hardleaf,34000,300  
            """;  
        Files.writeString(csvFile, content);
  
        // Initialize services  
        CsvReaderService csvReader = new CsvReaderService();  
        OrganizationAnalyzer analyzer = new OrganizationAnalyzer();  
        ReportGenerator reportGenerator = new ReportGenerator();
  
        // Act - Execute complete workflow  
        List<Employee> employees = csvReader.readEmployees(csvFile.toString());  
        AnalysisReport report = analyzer.analyze(employees);
  
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();  
        PrintStream printStream = new PrintStream(outputStream);  
        reportGenerator.printReport(report, printStream);
  
        // Assert  
        assertEquals(5, employees.size());  
        assertTrue(report.hasIssues());
          
        // Martin is underpaid (earns 30k, avg subordinate is 50k, should earn at least 60k)  
        assertEquals(1, report.getUnderpaidManagers().size());
          
        String output = outputStream.toString();  
        assertTrue(output.contains("ORGANIZATIONAL ANALYSIS REPORT"));  
    }

    @Test
    void testLargeOrganization(@TempDir Path tempDir) throws IOException, EmployeeDataException {
        // Arrange - Create a larger organization with deep hierarchy
        Path csvFile = tempDir.resolve("employees.csv");
        StringBuilder content = new StringBuilder("Id,firstName,lastName,salary,managerId\n");

        // CEO
        content.append("1,CEO,Boss,200000,\n");

        // Level 1: 2 VPs
        content.append("2,VP,Sales,120000,1\n");
        content.append("3,VP,Engineering,120000,1\n");

        // Level 2: 2 Directors (1 per VP)
        content.append("4,Director,SalesOps,72000,2\n");
        content.append("5,Director,DevOps,72000,3\n");

        // Level 3: 2 Senior Managers
        content.append("6,SeniorMgr,TeamA,43200,4\n");
        content.append("7,SeniorMgr,TeamB,43200,5\n");

        // Level 4: 2 Managers
        content.append("8,Manager,SubTeamA,25920,6\n");
        content.append("9,Manager,SubTeamB,25920,7\n");

        // Level 5: 4 Team Leads (these will have 5 levels - TOO LONG!)
        content.append("10,TeamLead,LeadA1,15552,8\n");
        content.append("11,TeamLead,LeadA2,15552,8\n");
        content.append("12,TeamLead,LeadB1,15552,9\n");
        content.append("13,TeamLead,LeadB2,15552,9\n");

        // Level 6: 8 Employees (these will have 6 levels - EVEN LONGER!)
        int id = 14;
        for (int lead = 10; lead <= 13; lead++) {
            for (int j = 0; j < 2; j++) {
                content.append(String.format("%d,Employee,Emp%d,9331,%d\n", id, id, lead));
                id++;
            }
        }

        Files.writeString(csvFile, content.toString());

        // Initialize services
        CsvReaderService csvReader = new CsvReaderService();
        OrganizationAnalyzer analyzer = new OrganizationAnalyzer();

        // Act
        List<Employee> employees = csvReader.readEmployees(csvFile.toString());
        AnalysisReport report = analyzer.analyze(employees);

        // Assert
        assertEquals(21, employees.size());
        assertFalse(report.getLongReportingLines().isEmpty());

        // Team Leads have 5 levels (exceeds 4) - should have 4 issues
        // Employees have 6 levels (exceeds 4) - should have 8 issues
        // Total: 12 employees with long reporting lines
        assertEquals(12, report.getLongReportingLines().size());

        // Verify the excess levels
        long fiveLevelEmployees = report.getLongReportingLines().stream()
                .filter(issue -> issue.getReportingLevels() == 5)
                .count();
        assertEquals(4, fiveLevelEmployees); // 4 Team Leads

        long sixLevelEmployees = report.getLongReportingLines().stream()
                .filter(issue -> issue.getReportingLevels() == 6)
                .count();
        assertEquals(8, sixLevelEmployees); // 8 Employees
    }
}  
package com.company.analyzer.service;
  
import com.company.analyzer.exception.EmployeeDataException;  
import com.company.analyzer.model.AnalysisReport;  
import com.company.analyzer.model.Employee;  
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;
  
import java.util.ArrayList;  
import java.util.List;
  
import static org.junit.jupiter.api.Assertions.*;
  
class OrganizationAnalyzerTest {
  
    private OrganizationAnalyzer analyzer;
  
    @BeforeEach  
    void setUp() {  
        analyzer = new OrganizationAnalyzer();  
    }

    @Test
    void testAnalyzeHealthyOrganization() throws EmployeeDataException {
        // Arrange - Create a truly healthy organization
        List<Employee> employees = List.of(
                new Employee("1", "CEO", "Boss", 100000, null),
                new Employee("2", "Manager", "One", 72000, "1"),  // 1.5x of 48k (avg of subordinates)
                new Employee("3", "Employee", "A", 48000, "2"),
                new Employee("4", "Employee", "B", 48000, "2")
        );

        // Act
        AnalysisReport report = analyzer.analyze(employees);

        // Assert
        assertFalse(report.hasIssues());
        assertTrue(report.getUnderpaidManagers().isEmpty());
        assertTrue(report.getOverpaidManagers().isEmpty());
        assertTrue(report.getLongReportingLines().isEmpty());
    }

    @Test  
    void testAnalyzeUnderpaidManager() throws EmployeeDataException {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "Boss", 100000, null),  
            new Employee("2", "Manager", "Underpaid", 40000, "1"),  // Should earn at least 48k (20% more than 40k)  
            new Employee("3", "Employee", "A", 40000, "2"),  
            new Employee("4", "Employee", "B", 40000, "2")  
        );
  
        // Act  
        AnalysisReport report = analyzer.analyze(employees);
  
        // Assert  
        assertEquals(1, report.getUnderpaidManagers().size());
          
        AnalysisReport.SalaryIssue issue = report.getUnderpaidManagers().get(0);  
        assertEquals("2", issue.getManager().getId());  
        assertEquals(40000, issue.getAverageSubordinateSalary());  
        assertEquals(8000, issue.getDifference(), 0.01);  // 48000 - 40000  
    }
  
    @Test  
    void testAnalyzeOverpaidManager() throws EmployeeDataException {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "Boss", 100000, null),  
            new Employee("2", "Manager", "Overpaid", 70000, "1"),  // Should earn at most 60k (50% more than 40k)  
            new Employee("3", "Employee", "A", 40000, "2"),  
            new Employee("4", "Employee", "B", 40000, "2")  
        );
  
        // Act  
        AnalysisReport report = analyzer.analyze(employees);
  
        // Assert  
        assertEquals(1, report.getOverpaidManagers().size());
          
        AnalysisReport.SalaryIssue issue = report.getOverpaidManagers().get(0);  
        assertEquals("2", issue.getManager().getId());  
        assertEquals(40000, issue.getAverageSubordinateSalary());  
        assertEquals(10000, issue.getDifference(), 0.01);  // 70000 - 60000  
    }
  
    @Test  
    void testAnalyzeLongReportingLine() throws EmployeeDataException {  
        // Arrange - Creating a chain of 6 levels (CEO + 5 managers)  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "Boss", 100000, null),  
            new Employee("2", "Manager", "L1", 80000, "1"),  
            new Employee("3", "Manager", "L2", 70000, "2"),  
            new Employee("4", "Manager", "L3", 60000, "3"),  
            new Employee("5", "Manager", "L4", 50000, "4"),  
            new Employee("6", "Employee", "Deep", 40000, "5")  // 5 levels to CEO  
        );
  
        // Act  
        AnalysisReport report = analyzer.analyze(employees);
  
        // Assert  
        assertEquals(1, report.getLongReportingLines().size());
          
        AnalysisReport.ReportingLineIssue issue = report.getLongReportingLines().get(0);  
        assertEquals("6", issue.getEmployee().getId());  
        assertEquals(5, issue.getReportingLevels());  
        assertEquals(1, issue.getExcessLevels());  
    }
  
    @Test  
    void testAnalyzeNoCEO() {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "Employee", "One", 50000, "2"),  
            new Employee("2", "Employee", "Two", 50000, "1")  
        );
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(employees));  
    }
  
    @Test  
    void testAnalyzeMultipleCEOs() {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "One", 100000, null),  
            new Employee("2", "CEO", "Two", 100000, null)  
        );
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(employees));  
    }
  
    @Test  
    void testAnalyzeDuplicateEmployeeIds() {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "Boss", 100000, null),  
            new Employee("1", "Employee", "Duplicate", 50000, "1")  
        );
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(employees));  
    }
  
    @Test  
    void testAnalyzeNonExistentManager() {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "Boss", 100000, null),  
            new Employee("2", "Employee", "Orphan", 50000, "999")  // Manager 999 doesn't exist  
        );
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(employees));  
    }
  
    @Test  
    void testAnalyzeCircularReference() {  
        // Arrange  
        List<Employee> employees = List.of(  
            new Employee("1", "CEO", "Boss", 100000, null),  
            new Employee("2", "Employee", "A", 50000, "3"),  
            new Employee("3", "Employee", "B", 50000, "2")  // Circular: 2 -> 3 -> 2  
        );
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(employees));  
    }
  
    @Test  
    void testAnalyzeEmptyEmployeeList() {  
        // Arrange  
        List<Employee> employees = new ArrayList<>();
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(employees));  
    }
  
    @Test  
    void testAnalyzeNullEmployeeList() {  
        // Act & Assert  
        assertThrows(EmployeeDataException.class, () -> analyzer.analyze(null));  
    }  
}
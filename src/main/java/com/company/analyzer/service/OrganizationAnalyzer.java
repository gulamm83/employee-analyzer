package com.company.analyzer.service;
  
import com.company.analyzer.exception.EmployeeDataException;  
import com.company.analyzer.model.AnalysisReport;  
import com.company.analyzer.model.Employee;  
import com.company.analyzer.util.Constants;
  
import java.util.*;  
import java.util.stream.Collectors;
  
/**  
 * Analyzes organizational structure for salary and reporting line issues.  
 */  
public class OrganizationAnalyzer {

    /**  
     * Performs complete analysis of the organization.  
     *  
     * @param employees list of all employees  
     * @return analysis report containing all identified issues  
     * @throws EmployeeDataException if organizational structure is invalid  
     */  
    public AnalysisReport analyze(List<Employee> employees) throws EmployeeDataException {  
        validateOrganization(employees);
  
        AnalysisReport report = new AnalysisReport();  
        Map<String, Employee> employeeMap = buildEmployeeMap(employees);  
        Map<String, List<Employee>> subordinatesMap = buildSubordinatesMap(employees);
  
        analyzeSalaries(report, employeeMap, subordinatesMap);  
        analyzeReportingLines(report, employees, employeeMap);
  
        return report;  
    }
  
    /**  
     * Validates the organizational structure.  
     */  
    private void validateOrganization(List<Employee> employees) throws EmployeeDataException {  
        if (employees == null || employees.isEmpty()) {  
            throw new EmployeeDataException("Employee list cannot be null or empty");  
        }
  
        // Check for duplicate IDs  
        Set<String> ids = new HashSet<>();  
        for (Employee emp : employees) {  
            if (!ids.add(emp.getId())) {  
                throw new EmployeeDataException("Duplicate employee ID found: " + emp.getId());  
            }  
        }
  
        // Check for exactly one CEO  
        long ceoCount = employees.stream().filter(Employee::isCeo).count();  
        if (ceoCount == 0) {  
            throw new EmployeeDataException("No CEO found (employee with no manager)");  
        }  
        if (ceoCount > 1) {  
            throw new EmployeeDataException("Multiple CEOs found (employees with no manager)");  
        }
  
        // Validate manager references  
        Set<String> employeeIds = employees.stream()  
            .map(Employee::getId)  
            .collect(Collectors.toSet());
  
        for (Employee emp : employees) {  
            if (!emp.isCeo() && !employeeIds.contains(emp.getManagerId())) {  
                throw new EmployeeDataException(  
                    "Employee " + emp.getId() + " references non-existent manager: " + emp.getManagerId());  
            }  
        }
  
        // Check for circular references  
        detectCircularReferences(employees);  
    }
  
    /**  
     * Detects circular references in the reporting structure.  
     */  
    private void detectCircularReferences(List<Employee> employees) throws EmployeeDataException {  
        Map<String, Employee> employeeMap = buildEmployeeMap(employees);
  
        for (Employee employee : employees) {  
            Set<String> visited = new HashSet<>();  
            Employee current = employee;
  
            while (current != null && !current.isCeo()) {  
                if (!visited.add(current.getId())) {  
                    throw new EmployeeDataException(  
                        "Circular reference detected in reporting structure involving employee: " + employee.getId());  
                }  
                current = employeeMap.get(current.getManagerId());  
            }  
        }  
    }
  
    /**  
     * Analyzes manager salaries against subordinate averages.  
     */  
    private void analyzeSalaries(AnalysisReport report,   
                                  Map<String, Employee> employeeMap,  
                                  Map<String, List<Employee>> subordinatesMap) {

        for (Map.Entry<String, List<Employee>> entry : subordinatesMap.entrySet()) {
            String managerId = entry.getKey();
            List<Employee> subordinates = entry.getValue();

            if (subordinates.isEmpty()) {
                continue; // No subordinates, no salary check needed
            }

            Employee manager = employeeMap.get(managerId);
            double avgSubordinateSalary = calculateAverageSalary(subordinates);
            double managerSalary = manager.getSalary();

            double minExpectedSalary = avgSubordinateSalary * Constants.MIN_MANAGER_SALARY_RATIO;
            double maxExpectedSalary = avgSubordinateSalary * Constants.MAX_MANAGER_SALARY_RATIO;

            if (managerSalary < minExpectedSalary) {
                double difference = minExpectedSalary - managerSalary;
                report.addUnderpaidManager(  
                    new AnalysisReport.SalaryIssue(manager, avgSubordinateSalary, difference));  
            } else if (managerSalary > maxExpectedSalary) {
                double difference = managerSalary - maxExpectedSalary;
                report.addOverpaidManager(  
                    new AnalysisReport.SalaryIssue(manager, avgSubordinateSalary, difference));  
            }  
        }  
    }
  
    /**  
     * Analyzes reporting lines for excessive length.  
     */  
    private void analyzeReportingLines(AnalysisReport report,   
                                       List<Employee> employees,  
                                       Map<String, Employee> employeeMap) {
          
        for (Employee employee : employees) {  
            if (employee.isCeo()) {  
                continue; // CEO has no reporting line  
            }
  
            int reportingLevels = countReportingLevels(employee, employeeMap);
              
            if (reportingLevels > Constants.MAX_REPORTING_LEVELS) {  
                int excessLevels = reportingLevels - Constants.MAX_REPORTING_LEVELS;  
                report.addLongReportingLine(  
                    new AnalysisReport.ReportingLineIssue(employee, reportingLevels, excessLevels));  
            }  
        }  
    }
  
    /**  
     * Counts the number of managers between an employee and the CEO.  
     */  
    private int countReportingLevels(Employee employee, Map<String, Employee> employeeMap) {  
        int levels = 0;  
        Employee current = employee;
  
        while (!current.isCeo()) {  
            current = employeeMap.get(current.getManagerId());  
            levels++;  
        }
  
        return levels;  
    }
  
    /**  
     * Calculates average salary for a list of employees.  
     */  
    private double calculateAverageSalary(List<Employee> employees) {  
        return employees.stream()  
            .mapToDouble(Employee::getSalary)  
            .average()  
            .orElse(0.0);  
    }
  
    /**  
     * Builds a map of employee ID to Employee object.  
     */  
    private Map<String, Employee> buildEmployeeMap(List<Employee> employees) {  
        return employees.stream()  
            .collect(Collectors.toMap(Employee::getId, emp -> emp));  
    }
  
    /**  
     * Builds a map of manager ID to list of direct subordinates.  
     */  
    private Map<String, List<Employee>> buildSubordinatesMap(List<Employee> employees) {  
        return employees.stream()  
            .filter(emp -> !emp.isCeo())  
            .collect(Collectors.groupingBy(Employee::getManagerId));  
    }  
}
package com.company.analyzer.model;
  
import java.util.ArrayList;  
import java.util.Collections;  
import java.util.List;
  
/**  
 * Contains the results of organizational analysis.  
 * Includes salary discrepancies and reporting line issues.  
 */  
public class AnalysisReport {  
    private final List<SalaryIssue> underpaidManagers;  
    private final List<SalaryIssue> overpaidManagers;  
    private final List<ReportingLineIssue> longReportingLines;
  
    public AnalysisReport() {  
        this.underpaidManagers = new ArrayList<>();  
        this.overpaidManagers = new ArrayList<>();  
        this.longReportingLines = new ArrayList<>();  
    }
  
    public void addUnderpaidManager(SalaryIssue issue) {  
        underpaidManagers.add(issue);  
    }
  
    public void addOverpaidManager(SalaryIssue issue) {  
        overpaidManagers.add(issue);  
    }
  
    public void addLongReportingLine(ReportingLineIssue issue) {  
        longReportingLines.add(issue);  
    }
  
    public List<SalaryIssue> getUnderpaidManagers() {  
        return Collections.unmodifiableList(underpaidManagers);  
    }
  
    public List<SalaryIssue> getOverpaidManagers() {  
        return Collections.unmodifiableList(overpaidManagers);  
    }
  
    public List<ReportingLineIssue> getLongReportingLines() {  
        return Collections.unmodifiableList(longReportingLines);  
    }
  
    public boolean hasIssues() {  
        return !underpaidManagers.isEmpty() ||   
               !overpaidManagers.isEmpty() ||   
               !longReportingLines.isEmpty();  
    }
  
    /**  
     * Represents a salary discrepancy for a manager.  
     */  
    public static class SalaryIssue {  
        private final Employee manager;  
        private final double averageSubordinateSalary;  
        private final double difference;
  
        public SalaryIssue(Employee manager, double averageSubordinateSalary, double difference) {  
            this.manager = manager;  
            this.averageSubordinateSalary = averageSubordinateSalary;  
            this.difference = difference;  
        }
  
        public Employee getManager() {  
            return manager;  
        }
  
        public double getAverageSubordinateSalary() {  
            return averageSubordinateSalary;  
        }
  
        public double getDifference() {  
            return difference;  
        }  
    }
  
    /**  
     * Represents an employee with too many reporting levels.  
     */  
    public static class ReportingLineIssue {  
        private final Employee employee;  
        private final int reportingLevels;  
        private final int excessLevels;
  
        public ReportingLineIssue(Employee employee, int reportingLevels, int excessLevels) {  
            this.employee = employee;  
            this.reportingLevels = reportingLevels;  
            this.excessLevels = excessLevels;  
        }
  
        public Employee getEmployee() {  
            return employee;  
        }
  
        public int getReportingLevels() {  
            return reportingLevels;  
        }
  
        public int getExcessLevels() {  
            return excessLevels;  
        }  
    }  
}  
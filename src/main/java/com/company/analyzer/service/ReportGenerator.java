package com.company.analyzer.service;
  
import com.company.analyzer.model.AnalysisReport;
  
import java.io.PrintStream;  
import java.text.NumberFormat;  
import java.util.Locale;
  
/**  
 * Generates formatted reports from analysis results.  
 */  
public class ReportGenerator {  
    private final NumberFormat currencyFormat;
  
    public ReportGenerator() {  
        this.currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);  
    }
  
    /**  
     * Prints the analysis report to the console.  
     *  
     * @param report the analysis report to print  
     */  
    public void printReport(AnalysisReport report) {  
        printReport(report, System.out);  
    }
  
    /**  
     * Prints the analysis report to the specified output stream.  
     *  
     * @param report the analysis report to print  
     * @param out the output stream to write to  
     */  
    public void printReport(AnalysisReport report, PrintStream out) {  
        out.println("=".repeat(80));  
        out.println("ORGANIZATIONAL ANALYSIS REPORT");  
        out.println("=".repeat(80));  
        out.println();
  
        if (!report.hasIssues()) {  
            out.println("✓ No issues found. Organization structure is healthy!");  
            out.println();  
            return;  
        }
  
        printUnderpaidManagers(report, out);  
        printOverpaidManagers(report, out);  
        printLongReportingLines(report, out);
  
        out.println("=".repeat(80));  
        out.println("END OF REPORT");  
        out.println("=".repeat(80));  
    }
  
    /**  
     * Prints underpaid managers section.  
     */  
    private void printUnderpaidManagers(AnalysisReport report, PrintStream out) {  
        if (report.getUnderpaidManagers().isEmpty()) {  
            out.println("✓ No underpaid managers found.");  
            out.println();  
            return;  
        }
  
        out.println("⚠ UNDERPAID MANAGERS");  
        out.println("-".repeat(80));  
        out.println("These managers earn less than 20% more than their subordinates' average:");  
        out.println();
  
        for (AnalysisReport.SalaryIssue issue : report.getUnderpaidManagers()) {  
            out.printf("  • %s (ID: %s)%n",   
                issue.getManager().getFullName(),   
                issue.getManager().getId());  
            out.printf("    Current salary: %s%n",   
                currencyFormat.format(issue.getManager().getSalary()));  
            out.printf("    Subordinates' average: %s%n",   
                currencyFormat.format(issue.getAverageSubordinateSalary()));  
            out.printf("    Underpaid by: %s%n",   
                currencyFormat.format(issue.getDifference()));  
            out.println();  
        }  
    }
  
    /**  
     * Prints overpaid managers section.  
     */  
    private void printOverpaidManagers(AnalysisReport report, PrintStream out) {  
        if (report.getOverpaidManagers().isEmpty()) {  
            out.println("✓ No overpaid managers found.");  
            out.println();  
            return;  
        }
  
        out.println("⚠ OVERPAID MANAGERS");  
        out.println("-".repeat(80));  
        out.println("These managers earn more than 50% more than their subordinates' average:");  
        out.println();
  
        for (AnalysisReport.SalaryIssue issue : report.getOverpaidManagers()) {  
            out.printf("  • %s (ID: %s)%n",   
                issue.getManager().getFullName(),   
                issue.getManager().getId());  
            out.printf("    Current salary: %s%n",   
                currencyFormat.format(issue.getManager().getSalary()));  
            out.printf("    Subordinates' average: %s%n",   
                currencyFormat.format(issue.getAverageSubordinateSalary()));  
            out.printf("    Overpaid by: %s%n",   
                currencyFormat.format(issue.getDifference()));  
            out.println();  
        }  
    }
  
    /**  
     * Prints long reporting lines section.  
     */  
    private void printLongReportingLines(AnalysisReport report, PrintStream out) {  
        if (report.getLongReportingLines().isEmpty()) {  
            out.println("✓ No excessively long reporting lines found.");  
            out.println();  
            return;  
        }
  
        out.println("⚠ LONG REPORTING LINES");  
        out.println("-".repeat(80));  
        out.println("These employees have more than 4 managers between them and the CEO:");  
        out.println();
  
        for (AnalysisReport.ReportingLineIssue issue : report.getLongReportingLines()) {  
            out.printf("  • %s (ID: %s)%n",   
                issue.getEmployee().getFullName(),   
                issue.getEmployee().getId());  
            out.printf("    Reporting levels: %d%n",   
                issue.getReportingLevels());  
            out.printf("    Excess levels: %d%n",   
                issue.getExcessLevels());  
            out.println();  
        }  
    }  
}  
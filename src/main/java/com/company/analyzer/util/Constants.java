package com.company.analyzer.util;
  
/**  
 * Application constants.  
 */  
public final class Constants {  
    private Constants() {  
        // Prevent instantiation  
    }
  
    // Salary thresholds  
    public static final double MIN_MANAGER_SALARY_RATIO = 1.20; // 20% more  
    public static final double MAX_MANAGER_SALARY_RATIO = 1.50; // 50% more
  
    // Reporting line threshold  
    public static final int MAX_REPORTING_LEVELS = 4;
  
    // CSV format  
    public static final String CSV_DELIMITER = ",";  
    public static final int CSV_EXPECTED_COLUMNS = 5;  
}  
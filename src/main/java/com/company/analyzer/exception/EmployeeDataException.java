package com.company.analyzer.exception;
  
/**  
 * Custom exception for employee data processing errors.  
 */  
public class EmployeeDataException extends Exception {  
    public EmployeeDataException(String message) {  
        super(message);  
    }
  
    public EmployeeDataException(String message, Throwable cause) {  
        super(message, cause);  
    }  
}  
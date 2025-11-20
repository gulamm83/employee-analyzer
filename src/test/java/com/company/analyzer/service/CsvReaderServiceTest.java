package com.company.analyzer.service;
  
import com.company.analyzer.exception.EmployeeDataException;  
import com.company.analyzer.model.Employee;  
import org.junit.jupiter.api.BeforeEach;  
import org.junit.jupiter.api.Test;  
import org.junit.jupiter.api.io.TempDir;
  
import java.io.IOException;  
import java.nio.file.Files;  
import java.nio.file.Path;  
import java.util.List;
  
import static org.junit.jupiter.api.Assertions.*;
  
class CsvReaderServiceTest {
  
    private CsvReaderService csvReaderService;
  
    @BeforeEach  
    void setUp() {  
        csvReaderService = new CsvReaderService();  
    }
  
    @Test  
    void testReadValidCsvFile(@TempDir Path tempDir) throws IOException, EmployeeDataException {  
        // Arrange  
        Path csvFile = tempDir.resolve("employees.csv");  
        String content = """  
            Id,firstName,lastName,salary,managerId  
            123,Joe,Doe,60000,  
            124,Martin,Chekov,45000,123  
            125,Bob,Ronstad,47000,123  
            """;  
        Files.writeString(csvFile, content);
  
        // Act  
        List<Employee> employees = csvReaderService.readEmployees(csvFile.toString());
  
        // Assert  
        assertEquals(3, employees.size());
          
        Employee ceo = employees.get(0);  
        assertEquals("123", ceo.getId());  
        assertEquals("Joe", ceo.getFirstName());  
        assertEquals("Doe", ceo.getLastName());  
        assertEquals(60000, ceo.getSalary());  
        assertTrue(ceo.isCeo());
          
        Employee employee2 = employees.get(1);  
        assertEquals("124", employee2.getId());  
        assertEquals("123", employee2.getManagerId());  
        assertFalse(employee2.isCeo());  
    }
  
    @Test  
    void testReadCsvWithEmptyLines(@TempDir Path tempDir) throws IOException, EmployeeDataException {  
        // Arrange  
        Path csvFile = tempDir.resolve("employees.csv");  
        String content = """  
            Id,firstName,lastName,salary,managerId  
            123,Joe,Doe,60000,
              
            124,Martin,Chekov,45000,123  
            """;  
        Files.writeString(csvFile, content);
  
        // Act  
        List<Employee> employees = csvReaderService.readEmployees(csvFile.toString());
  
        // Assert  
        assertEquals(2, employees.size());  
    }
  
    @Test  
    void testReadCsvWithInvalidSalary(@TempDir Path tempDir) throws IOException {  
        // Arrange  
        Path csvFile = tempDir.resolve("employees.csv");  
        String content = """  
            Id,firstName,lastName,salary,managerId  
            123,Joe,Doe,invalid,  
            """;  
        Files.writeString(csvFile, content);
  
        // Act & Assert  
        EmployeeDataException exception = assertThrows(  
            EmployeeDataException.class,  
            () -> csvReaderService.readEmployees(csvFile.toString())  
        );  
        assertTrue(exception.getMessage().contains("Invalid salary value"));  
    }
  
    @Test  
    void testReadCsvWithMissingColumns(@TempDir Path tempDir) throws IOException {  
        // Arrange  
        Path csvFile = tempDir.resolve("employees.csv");  
        String content = """  
            Id,firstName,lastName,salary,managerId  
            123,Joe,Doe  
            """;  
        Files.writeString(csvFile, content);
  
        // Act & Assert  
        assertThrows(EmployeeDataException.class,  
            () -> csvReaderService.readEmployees(csvFile.toString()));  
    }
  
    @Test  
    void testReadNonExistentFile() {  
        // Act & Assert  
        EmployeeDataException exception = assertThrows(  
            EmployeeDataException.class,  
            () -> csvReaderService.readEmployees("nonexistent.csv")  
        );  
        assertTrue(exception.getMessage().contains("does not exist"));  
    }
  
    @Test  
    void testReadNullFilePath() {  
        // Act & Assert  
        assertThrows(EmployeeDataException.class,  
            () -> csvReaderService.readEmployees(null));  
    }
  
    @Test  
    void testReadEmptyFile(@TempDir Path tempDir) throws IOException {  
        // Arrange  
        Path csvFile = tempDir.resolve("employees.csv");  
        Files.writeString(csvFile, "Id,firstName,lastName,salary,managerId\n");
  
        // Act & Assert  
        EmployeeDataException exception = assertThrows(  
            EmployeeDataException.class,  
            () -> csvReaderService.readEmployees(csvFile.toString())  
        );  
        assertTrue(exception.getMessage().contains("No employee data found"));  
    }  
}  
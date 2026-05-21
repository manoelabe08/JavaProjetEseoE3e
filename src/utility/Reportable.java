package utility;

/**
 * Defines a contract for generating formatted system reports.
 * Any class responsible for exporting or summarizing data must implement this interface.
 */
public interface Reportable {
    
    /**
     * Generates a comprehensive report containing relevant system data.
     * * @return A formatted String representing the final report.
     */
    String generateReport();
}
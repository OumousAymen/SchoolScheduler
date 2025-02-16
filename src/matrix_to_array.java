import java.util.regex.*;
import java.io.*;

public class matrix_to_array {
    public static String cplexToJavaMultidimensional(String cplexArray) {
        // Replace brackets with proper delimiters
        cplexArray = cplexArray.replace('[', '{').replace(']', '}');

        // Replace multiple spaces or /n with a single comma
        cplexArray = cplexArray.trim().replaceAll("[\\s/]+", ",");

        return cplexArray;
    }

    public static void main(String[] args) {
        // Read the input from rawdata.txt
        StringBuilder cplexInput = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/rawdata.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                cplexInput.append(line).append("\n"); // Append each line to the input
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading the file: " + e.getMessage());
            return; // Exit the program if reading fails
        }

        // Convert CPLEX input to Java array format
        String javaArray = cplexToJavaMultidimensional(cplexInput.toString());
        System.out.println(javaArray); // Print to console for verification

        // Write the result to data.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/data.txt"))) {
            writer.write(javaArray); // Write the array to the file
            System.out.println("Array successfully written to data.txt");
        } catch (IOException e) {
            System.err.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }
}
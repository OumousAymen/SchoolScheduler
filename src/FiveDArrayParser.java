import java.io.*;
import java.util.ArrayList;

class FiveDArrayParser {

    // Reads the entire file into a String.
    public static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    // Parses the input String into a 5D ArrayList.
    // The outermost list represents the list of majors.
    public static ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>> parseFiveDArray(String input) {
        input = input.replaceAll("\\s+", "");
        int[] index = {0};
        return parseNestedArray(input, index);
    }

    // Recursive parser that returns an ArrayList.
    private static ArrayList parseNestedArray(String input, int[] index) {
        ArrayList list = new ArrayList();
        index[0]++; // Skip the opening '{'
        while (index[0] < input.length() && input.charAt(index[0]) != '}') {
            char c = input.charAt(index[0]);
            if (c == '{') {
                list.add(parseNestedArray(input, index));
            } else if (Character.isDigit(c) || c == '-') {
                int num = 0;
                int sign = 1;
                if (c == '-') {
                    sign = -1;
                    index[0]++;
                }
                while (index[0] < input.length() && Character.isDigit(input.charAt(index[0]))) {
                    num = num * 10 + (input.charAt(index[0]) - '0');
                    index[0]++;
                }
                list.add(sign * num);
                index[0]--; // adjust for the outer loop increment
            }
            index[0]++;
        }
        return list;
    }
}

import java.util.*;
public class Main {
    public static void main(String[] args) {
        String[] majors_list;
        majors_list = majors_list().toArray(new String[0]);
        List<String> list = Arrays.asList(majors_list);

        ArrayList<String> subjects_list = new ArrayList<>();
        ArrayList<ArrayList<Integer>> affectMatrix = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Integer>>> combinedMatrix = new ArrayList<>();

        Scanner sc = new Scanner(System.in);
        System.out.println("Write subject name (or exit to quit): ");
        String input_subject = sc.nextLine();
        int counter = 0;

        while (!input_subject.equals("exit")) {
            subjects_list.add(input_subject);

            ArrayList<Integer> single_subject_hours = new ArrayList<>();
            ArrayList<ArrayList<Integer>> subjectCombinedMatrix = new ArrayList<>();

            System.out.println("Which majors are concerned? Write the number of weekly hours next to the concerned majors:");

            // Input weekly hours for each major
            for (String major : majors_list) {
                System.out.println(major);
                int input_hours = sc.nextInt();
                single_subject_hours.add(input_hours / 2);  // i am dividing by 2
            }
            affectMatrix.add(single_subject_hours);

            sc.nextLine(); // Consume the newline left by nextInt()

            // Initialize the subject's combined matrix with 0s
            for (int i = 0; i < majors_list.length; i++) {
                ArrayList<Integer> row = new ArrayList<>();
                for (int j = 0; j < majors_list.length; j++) {
                    if (!affectMatrix.get(counter).get(j).equals(0) && i == j) {
                        row.add(1);
                    } else {
                        row.add(0); // Default to 0 (no combination)
                    }
                }
                subjectCombinedMatrix.add(row);
            }

            System.out.println("Does any majors study together " + input_subject + "? (yes / no)");
            String resp = sc.nextLine();

            if (resp.equals("yes")) {
                System.out.println("Enter which majors study together separated with a space ' ' from these: " + Arrays.toString(majors_list) + " and enter exit to quit inputting the groups");
                String input_together = sc.nextLine();

                while (!input_together.equals("exit")) {
                    String[] majorsTogether = input_together.split(" ");

                    // Mark 1 for all pairs of majors that study together
                    for (String major1 : majorsTogether) {
                        for (String major2 : majorsTogether) {
                            int index1 = list.indexOf(major1);
                            int index2 = list.indexOf(major2);
                            if (index1 != -1 && index2 != -1) {
                                subjectCombinedMatrix.get(index1).set(index2, 1);
                                subjectCombinedMatrix.get(index2).set(index1, 1); // Symmetric
                            }
                        }
                    }

                    input_together = sc.nextLine();
                }
            }
            counter++;

            combinedMatrix.add(subjectCombinedMatrix);

            System.out.println("Write subject name (or exit to quit): ");
            input_subject = sc.nextLine();
        }

        // Add professors and the subjects they teach
        ArrayList<String> professors_list = new ArrayList<>();
        ArrayList<ArrayList<Integer>> professorSubjectMatrix = new ArrayList<>();

        System.out.println("Write professor name (or exit to quit): ");
        String input_professor = sc.nextLine();

        while (!input_professor.equals("exit")) {
            professors_list.add(input_professor);

            ArrayList<Integer> subjectsTaught = new ArrayList<>(Collections.nCopies(subjects_list.size(), 0));

            // Display the list of subjects
            System.out.println("Available subjects: " + subjects_list);
            System.out.println("Which subjects does " + input_professor + " teach? (write subject names separated by space): ");
            String input_subjects = sc.nextLine();
            String[] subjects = input_subjects.split(" ");

            for (String subject : subjects) {
                int index = subjects_list.indexOf(subject);
                if (index != -1) {
                    subjectsTaught.set(index, 1);
                } else {
                    System.out.println("Subject '" + subject + "' not found in the subjects list.");
                }
            }

            professorSubjectMatrix.add(subjectsTaught);

            System.out.println("Write professor name (or exit to quit): ");
            input_professor = sc.nextLine();
        }

        // Output all matrices at the end
        System.out.println("\n--- Final Matrices ---");
        System.out.println("Subjects: " + subjects_list);
        System.out.println("Affect Matrix: " + affectMatrix);
        System.out.println("Combined Matrix: " + combinedMatrix);
        System.out.println("Professors: " + professors_list);
        System.out.println("Professor-Subject Matrix: " + professorSubjectMatrix);
    }

    static public ArrayList<String> majors_list() {
        ArrayList<String> majors_list = new ArrayList<String>();
        System.out.println("write majors names (or exit to quit) : ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        while (!input.equals("exit")) {
            majors_list.add(input);
            input = sc.nextLine();
        }

        return majors_list;
    }
}
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ui extends Application {

    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static final String[] TIME_SLOTS = {"08:00-10:00", "10:00-12:00", "14:00-16:00", "16:00-18:00"};

    // Map where for each major (key) we store its schedule matrix:
    // scheduleMatrix[s][p][c][t] = 1 means subject s is taught by professor p in class c at time t.
    private static final Map<Integer, int[][][][]> X = new HashMap<>();

    static {
        try {
            // Read the entire file "data.txt" into a String.
            String fileContents = FiveDArrayParser.readFile("src/data.txt");
            // Parse the file into a 5D ArrayList.
            // The outermost level corresponds to majors.
            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>> parsedData
                    = FiveDArrayParser.parseFiveDArray(fileContents);
            // Convert the 5D ArrayList into a 5D int array.
            int[][][][][] cplexMatrix = convertToInt5DArray(parsedData);
            // For each major, store its 4D schedule matrix into the map X.
            for (int i = 0; i < cplexMatrix.length; i++) {
                // cplexMatrix[i] is a 4D int array corresponding to major (i+1)
                X.put(i + 1, cplexMatrix[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper: convert a 5D nested ArrayList into a 5D int array.
    private static int[][][][][] convertToInt5DArray(
            ArrayList<ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>>> nestedList) {
        int dim0 = nestedList.size();
        int[][][][][] arr = new int[dim0][][][][];

        for (int i = 0; i < dim0; i++) {
            ArrayList<ArrayList<ArrayList<ArrayList<Integer>>>> level1 = nestedList.get(i);
            int dim1 = level1.size();
            arr[i] = new int[dim1][][][];
            for (int j = 0; j < dim1; j++) {
                ArrayList<ArrayList<ArrayList<Integer>>> level2 = level1.get(j);
                int dim2 = level2.size();
                arr[i][j] = new int[dim2][][];
                for (int k = 0; k < dim2; k++) {
                    ArrayList<ArrayList<Integer>> level3 = level2.get(k);
                    int dim3 = level3.size();
                    arr[i][j][k] = new int[dim3][];
                    for (int l = 0; l < dim3; l++) {
                        ArrayList<Integer> level4 = level3.get(l);
                        int dim4 = level4.size();
                        arr[i][j][k][l] = new int[dim4];
                        for (int m = 0; m < dim4; m++) {
                            arr[i][j][k][l][m] = level4.get(m);
                        }
                    }
                }
            }
        }
        return arr;
    }

    // A helper class for table rows.
    public static class ScheduleRow {
        private String timeSlot;
        private ObservableList<String> dailyEntries;

        public ScheduleRow(String timeSlot) {
            this.timeSlot = timeSlot;
            // Initialize one empty string per day.
            this.dailyEntries = FXCollections.observableArrayList("", "", "", "", "", "");
        }

        public String getTimeSlot() {
            return timeSlot;
        }

        public ObservableList<String> getDailyEntries() {
            return dailyEntries;
        }

        public void setEntry(int dayIndex, String entry) {
            dailyEntries.set(dayIndex, entry);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        // Create a table for each major found in X.
        for (Integer major : X.keySet()) {
            TableView<ScheduleRow> table = createTableForMajor(major);
            Label title = new Label("Schedule for Major " + major);
            title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            root.getChildren().addAll(title, table);
        }

        Scene scene = new Scene(root, 950, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Major Schedules");
        primaryStage.show();
    }

    // Build a TableView for a given major.
    private TableView<ScheduleRow> createTableForMajor(int major) {
        TableView<ScheduleRow> table = new TableView<>();
        table.setStyle("-fx-border-color: black; -fx-border-width: 2px;");
        ObservableList<ScheduleRow> data = FXCollections.observableArrayList();

        // One row per time slot.
        for (String slot : TIME_SLOTS) {
            data.add(new ScheduleRow(slot));
        }

        // First column: time slot.
        TableColumn<ScheduleRow, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        timeColumn.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
        timeColumn.setPrefWidth(120);
        table.getColumns().add(timeColumn);

        // Create one column per day.
        for (int d = 0; d < DAYS.length; d++) {
            final int dayIndex = d;
            TableColumn<ScheduleRow, String> dayColumn = new TableColumn<>(DAYS[d]);
            dayColumn.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDailyEntries().get(dayIndex)));
            dayColumn.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
            dayColumn.setPrefWidth(130);
            table.getColumns().add(dayColumn);
        }

        // Get the 4D schedule matrix for this major.
        int[][][][] scheduleMatrix = X.get(major);
        // Example subject and professor names.
        String[] Subjects = {"gl", "eng", "Reseau", "persistance", "warehouse", "mobile", "ai", "droit",
                "fr", "unSurQuinze", "progFonc", "strat", "iot", "Software", "pnl"};
        String[] Profs = {"ELALAOUY", "mr_hiba", "Hamidoun", "Mohammadi", "Benelallam", "bajta",
                "kabbaj", "abdouni", "zakka", "profunSurQuinze", "effina", "ezzazi", "radgui", "rahou"};

        // Loop through the dimensions:
        // s: subject (expected size 15)
        // p: professor (expected size 14)
        // c: classroom (expected size 5)
        // t: time slot (expected size 22)
        for (int s = 0; s < scheduleMatrix.length; s++) {
            for (int p = 0; p < scheduleMatrix[s].length; p++) {
                for (int c = 0; c < scheduleMatrix[s][p].length; c++) {
                    for (int t = 0; t < scheduleMatrix[s][p][c].length; t++) {
                        if (scheduleMatrix[s][p][c][t] == 1) {
                            // Map t to a day and a time slot.
                            int dayIndex = t / 4;       // 4 time slots per day
                            int timeSlotIndex = t % 4;
                            if (dayIndex < DAYS.length && timeSlotIndex < TIME_SLOTS.length) {
                                String entry = "S: " + Subjects[s] + " (P: " + Profs[p] + ", C: " + (c + 1) + ")";
                                // Place the entry into the corresponding row (time slot) and day.
                                data.get(timeSlotIndex).setEntry(dayIndex, entry);
                            }
                        }
                    }
                }
            }
        }

        table.setItems(data);
        return table;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

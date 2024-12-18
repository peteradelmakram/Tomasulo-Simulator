package TomasuloSimulator;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ReservationStations.ReservationStationEntry;

import java.util.Arrays;
import java.util.function.Function;

import Buffer.LoadBufferEntry;
import Buffer.StoreBufferEntry;
import DataCache.Cache;
import ExecutionTable.ExecutionTableEntry;
import Instruction.Instruction;
import RegisterFile.RegisterFile;
import javafx.scene.layout.GridPane;

public class TomasuloSimulatorGUI extends Application {

    private TomasuloSimulator simulator;
    private TableView<ReservationStationEntry> reservationStationTable;
    private TableView<LoadBufferEntry> loadBuffersTable;
    private TableView<StoreBufferEntry> storeBuffersTable;
    private TableView<ExecutionTableEntry> executionTable;
    private TableView<RegisterFile.Register> registerFileTable;
    private TableView<Instruction> instructionQueueTable;
    private TableView<Cache.CacheBlock> dataCacheTable;

    @Override
    public void start(Stage primaryStage) {
        initializeSetupScreen(primaryStage);
    }

    private void initializeSetupScreen(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Text fields for latencies and sizes
        TextField addLatencyField = new TextField("2");
        TextField subLatencyField = new TextField("2");
        TextField mulLatencyField = new TextField("4");
        TextField divLatencyField = new TextField("5");
        TextField cacheMissLatencyField = new TextField("5");
        TextField loadLatencyField = new TextField("2");
        TextField storeLatencyField = new TextField("2");
        TextField addSubtractRSSizeField = new TextField("3");
        TextField multiplyDivideRSSizeField = new TextField("2");
        TextField loadBufferSizeField = new TextField("3");
        TextField storeBufferSizeField = new TextField("3");
        TextField branchLatencyField = new TextField("1");

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);

        inputGrid.add(new Label("Add Latency:"), 0, 0);
        inputGrid.add(addLatencyField, 1, 0);
        inputGrid.add(new Label("Subtract Latency:"), 0, 1);
        inputGrid.add(subLatencyField, 1, 1);
        inputGrid.add(new Label("Multiply Latency:"), 0, 2);
        inputGrid.add(mulLatencyField, 1, 2);
        inputGrid.add(new Label("Divide Latency:"), 0, 3);
        inputGrid.add(divLatencyField, 1, 3);
        inputGrid.add(new Label("Cache Miss Latency:"), 0, 4);
        inputGrid.add(cacheMissLatencyField, 1, 4);
        inputGrid.add(new Label("Load Latency:"), 0, 5);
        inputGrid.add(loadLatencyField, 1, 5);
        inputGrid.add(new Label("Store Latency:"), 0, 6);
        inputGrid.add(storeLatencyField, 1, 6);
        inputGrid.add(new Label("Add/Subtract RS Size:"), 0, 7);
        inputGrid.add(addSubtractRSSizeField, 1, 7);
        inputGrid.add(new Label("Multiply/Divide RS Size:"), 0, 8);
        inputGrid.add(multiplyDivideRSSizeField, 1, 8);
        inputGrid.add(new Label("Load Buffer Size:"), 0, 9);
        inputGrid.add(loadBufferSizeField, 1, 9);
        inputGrid.add(new Label("Store Buffer Size:"), 0, 10);
        inputGrid.add(storeBufferSizeField, 1, 10);
        inputGrid.add(new Label("Branch Latency:"), 0, 11);
        inputGrid.add(branchLatencyField, 1, 11);

        Button startButton = new Button("Start Simulation");
        startButton.setOnAction(event -> {
            simulator = new TomasuloSimulator(
                    16, // Integer registers
                    16, // Floating-point registers
                    8, // Cache block size
                    Integer.parseInt(addSubtractRSSizeField.getText()),
                    Integer.parseInt(multiplyDivideRSSizeField.getText()),
                    Integer.parseInt(addLatencyField.getText()),
                    Integer.parseInt(subLatencyField.getText()),
                    Integer.parseInt(mulLatencyField.getText()),
                    Integer.parseInt(divLatencyField.getText()),
                    Integer.parseInt(cacheMissLatencyField.getText()),
                    Integer.parseInt(loadLatencyField.getText()),
                    Integer.parseInt(storeLatencyField.getText()),
                    Integer.parseInt(loadBufferSizeField.getText()),
                    Integer.parseInt(storeBufferSizeField.getText()),
                    Integer.parseInt(branchLatencyField.getText())
            );
            initializeSimulationScreen(stage);
        });

        root.getChildren().addAll(inputGrid, startButton);
        stage.setScene(new Scene(root, 400, 600));
        stage.setTitle("Tomasulo Simulator");
        stage.show();
    }

    private void initializeSimulationScreen(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Create tables and set preferred sizes
        reservationStationTable = createReservationStationTable();
        reservationStationTable.setPrefHeight(300); // Adjust height
        reservationStationTable.setPrefWidth(750);  // Adjust width

        loadBuffersTable = createLoadBufferTable();
        loadBuffersTable.setPrefHeight(200);
        loadBuffersTable.setPrefWidth(750);

        storeBuffersTable = createStoreBufferTable();
        storeBuffersTable.setPrefHeight(200);
        storeBuffersTable.setPrefWidth(750);

        executionTable = createExecutionTable();
        executionTable.setPrefHeight(300);
        executionTable.setPrefWidth(750);

        registerFileTable = createRegisterFileTable();
        registerFileTable.setPrefHeight(200);
        registerFileTable.setPrefWidth(750);

        
        instructionQueueTable = createInstructionQueueTable();
        dataCacheTable = createDataCacheTable();

        Label clockCycleLabel = new Label("Clock Cycle: 0");

        // Next cycle button
        Button nextCycleButton = new Button("Next Cycle");
        nextCycleButton.setOnAction(event -> {
            simulator.executeSingleCycle();
            updateTables();
            
            clockCycleLabel.setText("Clock Cycle : " + simulator.getCycle());
            
        });

        // Tabs for each table
        TabPane tabPane = new TabPane();

        Tab rsTab = new Tab("Reservation Stations", reservationStationTable);
        Tab loadTab = new Tab("Load Buffers", loadBuffersTable);
        Tab storeTab = new Tab("Store Buffers", storeBuffersTable);
        Tab execTab = new Tab("Execution Table", executionTable);
        Tab regTab = new Tab("Register File", registerFileTable);
        Tab instrTab = new Tab("Instruction Queue", instructionQueueTable);
        Tab cacheTab = new Tab("Data Cache", dataCacheTable);
        
        tabPane.getTabs().addAll(rsTab, loadTab, storeTab, execTab, regTab, instrTab, cacheTab);

        // Add all components to the root layout
        root.getChildren().addAll(tabPane,clockCycleLabel, nextCycleButton);

        // Set the scene
        stage.setScene(new Scene(root, 800, 800)); // Increase height to accommodate larger tables
        stage.setTitle("Tomasulo Simulator");
        stage.show();

        updateTables();
    }

    private TableView<Instruction> createInstructionQueueTable() {
        TableView<Instruction> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Operation", Instruction::getOperation),
                createColumn("Source 1", Instruction::getSource1),
                createColumn("Source 2", Instruction::getSource2),
                createColumn("Destination", Instruction::getDestination)
        );
        return table;
    }
    
    private TableView<Cache.CacheBlock> createDataCacheTable() {
        TableView<Cache.CacheBlock> table = new TableView<>();

        // Create columns for the table
        TableColumn<Cache.CacheBlock, Boolean> validColumn = createColumn("Valid", Cache.CacheBlock::isValid);
        TableColumn<Cache.CacheBlock, Integer> tagColumn = createColumn("Tag", Cache.CacheBlock::getTag);
        TableColumn<Cache.CacheBlock, String> dataColumn = createColumn("Data", block -> Arrays.toString(block.getData()));

        // Add columns to the table
        table.getColumns().add(validColumn);
        table.getColumns().add(tagColumn);
        table.getColumns().add(dataColumn);

        return table;
    }

    
    private TableView<ReservationStationEntry> createReservationStationTable() {
        TableView<ReservationStationEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Tag", ReservationStationEntry::getTag),
                createColumn("Operation", ReservationStationEntry::getOp),
                createColumn("Vj", ReservationStationEntry::getVj),
                createColumn("Vk", ReservationStationEntry::getVk),
                createColumn("Qj", ReservationStationEntry::getQj),
                createColumn("Qk", ReservationStationEntry::getQk),
                createColumn("Busy", ReservationStationEntry::isBusy)
        );
        return table;
    }

    private TableView<LoadBufferEntry> createLoadBufferTable() {
        TableView<LoadBufferEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Tag", LoadBufferEntry::getTag),
                createColumn("Address", LoadBufferEntry::getAddress),
                createColumn("Busy", LoadBufferEntry::isBusy)
        );
        return table;
    }

    private TableView<StoreBufferEntry> createStoreBufferTable() {
        TableView<StoreBufferEntry> table = new TableView<>();
        table.getColumns().addAll(
                createColumn("Tag", StoreBufferEntry::getTag),
                createColumn("Address", StoreBufferEntry::getAddress),
                createColumn("Busy", StoreBufferEntry::isBusy),
                createColumn("Value", StoreBufferEntry::getValue),
                createColumn("Q", StoreBufferEntry::getQ)
        );
        return table;
    }

    private TableView<ExecutionTableEntry> createExecutionTable() {
        TableView<ExecutionTableEntry> table = new TableView<>();
        table.getColumns().addAll(
            createColumn("Instruction", entry -> entry.getInstruction().getOperation()), // Extract operation from instruction
            createColumn("Destination", ExecutionTableEntry::getDestination),
            createColumn("Source1", ExecutionTableEntry::getSource1),
            createColumn("Source2", ExecutionTableEntry::getSource2),
            createColumn("Issue", ExecutionTableEntry::getIssueCycle),
            createColumn("Start Execution", ExecutionTableEntry::getStartExecutionCycle),
            createColumn("End Execution", ExecutionTableEntry::getEndExecutionCycle),
            createColumn("Write Back", ExecutionTableEntry::getWriteBackCycle)
        );
        return table;
    }

    private TableView<RegisterFile.Register> createRegisterFileTable() {
        TableView<RegisterFile.Register> table = new TableView<>();
        table.getColumns().addAll(
        		createColumn("Name", RegisterFile.Register::getName),
                createColumn("Tag", RegisterFile.Register::getTag),
                createColumn("Value", RegisterFile.Register::getValue)
        );
        return table;
    }

    private <T, R> TableColumn<T, R> createColumn(String title, Function<T, R> mapper) {
        TableColumn<T, R> column = new TableColumn<>(title);
        column.setCellValueFactory(param -> new SimpleObjectProperty<>(mapper.apply(param.getValue())));
        return column;
    }

    private <T> void refreshTable(TableView<T> table) {
        if (table != null) {
            // Force the table to refresh by re-setting its items list
            ObservableList<T> items = table.getItems();
            table.setItems(null); // Clear the table
            table.setItems(items); // Reapply the same list to trigger a refresh
        }
    }


    private void updateTables() {
        // Debug: Log data before populating tables

        // Reservation Stations
        ObservableList<ReservationStationEntry> reservationData = FXCollections.observableArrayList(simulator.getReservationStations().getStationsForDisplay());
        reservationStationTable.setItems(reservationData);
        reservationStationTable.refresh(); // Force a refresh

        // Load Buffers
        ObservableList<LoadBufferEntry> loadBufferData = FXCollections.observableArrayList(simulator.getBuffers().getLoadBuffers().getBuffer());
        loadBuffersTable.setItems(loadBufferData);
        loadBuffersTable.refresh(); // Force a refresh

        // Store Buffers
        ObservableList<StoreBufferEntry> storeBufferData = FXCollections.observableArrayList(simulator.getBuffers().getStoreBuffers().getBuffer());
        storeBuffersTable.setItems(storeBufferData);
        storeBuffersTable.refresh(); // Force a refresh

        // Execution Table
        ObservableList<ExecutionTableEntry> executionData = FXCollections.observableArrayList(simulator.getExecutionTable());
        executionTable.setItems(executionData);
        executionTable.refresh(); // Force a refresh

        // Register File
        ObservableList<RegisterFile.Register> registerData = FXCollections.observableArrayList();
        registerData.addAll(simulator.getRegisterFile().getIntegerRegisters().values());
        registerData.addAll(simulator.getRegisterFile().getFloatingPointRegisters().values());
        registerFileTable.setItems(registerData);
        registerFileTable.refresh(); // Force a refresh
        
        // Update instruction queue
        ObservableList<Instruction> instructionQueueData = FXCollections.observableArrayList(simulator.getInstructionQueue());
        instructionQueueTable.setItems(instructionQueueData);
        instructionQueueTable.refresh();

        // Update data cache
        ObservableList<Cache.CacheBlock> cacheData = FXCollections.observableArrayList(simulator.getDataCache().getCache());
        dataCacheTable.setItems(cacheData);
        dataCacheTable.refresh();
        
        
    }

    public static void main(String[] args) {
        launch(args);
    }
}

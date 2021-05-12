package src.main.java.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import src.main.java.resources.*;

public class Controller implements Initializable {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Menu exportMenu;
    @FXML
    private MenuItem deselectMenuItem;
    @FXML
    private MenuItem deleteSelectionMenuItem;
    @FXML
    private Menu sortListMenu;
    @FXML
    private MenuItem selectAllMenuItem;
    @FXML
    private TableView<Transaction> mainTable;
    @FXML
    private TableColumn<Transaction, Double> amountClm;
    @FXML
    private TableColumn<Transaction, LocalDate> dateClm;
    @FXML
    private TableColumn<Transaction, String> titleClm;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextArea titleField;
    @FXML
    private TextField amountField;
    @FXML
    private Button addBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem itemDeleteMenu;
    @FXML
    private Text totalSpentText;
    @FXML
    private Text totalGainedText;
    @FXML
    private Text totalText;
    @FXML
    private LineChart<Number, Number> chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private XYChart.Series<Number, Number> series;
    private ObservableList<Transaction> transactionsList;
    private ObservableList<Transaction> listCopy;
    private String backupFilename = "data/backup.dat";
    private FileHandler fileHandler;
    private Comparators comparators;
    private Comparator<Transaction> sortingComparator;

    @FXML
    public void showAbout(ActionEvent event) {
        // TODO: add About panel
    }

    @FXML
    public void saveState(ActionEvent event) {
        fileHandler.saveToFile(backupFilename, transactionsList);
    }

    @FXML
    public void importCSV(ActionEvent event) {
        if (fileHandler.importFromCSV(transactionsList))
            onChange();
    }

    @FXML
    public void importTXT(ActionEvent event) {
        if (fileHandler.importFromTXT(transactionsList))
            onChange();
    }

    @FXML
    public void exportCSV(ActionEvent event) {
        fileHandler.exportToCSV(transactionsList);
    }

    @FXML
    public void exportTXT(ActionEvent event) {
        fileHandler.exportToTXT(transactionsList);
    }

    @FXML
    public void exportImage(ActionEvent event) {
        fileHandler.exportToPNG(chart);
    }

    @FXML
    public void closeWindow(ActionEvent event) {
        Platform.exit();
    }

    private void sortList() {
        transactionsList.sort(sortingComparator);
    }

    @FXML
    public void sortAscendingDate(ActionEvent event) {
        sortingComparator = comparators.compareByAscendingDate();
        sortList();
    }

    @FXML
    public void sortDescendingDate(ActionEvent event) {
        sortingComparator = comparators.compareByDescendingDate();
        sortList();
    }

    @FXML
    public void sortAscendingAmount(ActionEvent event) {
        sortingComparator = comparators.compareByAscendingAmount();
        sortList();
    }

    @FXML
    public void sortDescendingAmount(ActionEvent event) {
        sortingComparator = comparators.compareByDescendingAmount();
        sortList();
    }

    @FXML
    public void selectAll(ActionEvent event) {
        mainTable.getSelectionModel().selectAll();
    }

    @FXML
    public void deselect(ActionEvent event) {
        mainTable.getSelectionModel().clearSelection();
    }

    @FXML
    public void deleteSelection(ActionEvent event) {
        transactionsList.removeAll(mainTable.getSelectionModel().getSelectedItems());
        onChange();
    }

    @FXML
    public void addNew(ActionEvent event) {
        double amount = 0;
        try {
            amount = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException e) {
            amount = 0;
        }

        if (amount == 0) {
            AlertUser.show("Error", null, "Please insert a valid amount of money.");
            amountField.clear();
            return;
        }

        Transaction t = new Transaction(amount, datePicker.getValue(), titleField.getText());
        amountField.clear();
        titleField.clear();

        if (transactionsList.contains(t))
            AlertUser.show("Error", null, "Cannot insert duplicate transaction.");
        else {
            transactionsList.add(t);
            onChange();
        }
    }

    @FXML
    public void clearFields(ActionEvent event) {
        amountField.clear();
        titleField.clear();
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    public void titleEdited(TableColumn.CellEditEvent<Transaction, String> event) {
        Transaction t = mainTable.getSelectionModel().getSelectedItem();
        t.setTitle(event.getNewValue());
        fileHandler.saveToFile(backupFilename, transactionsList);
    }

    private int dateDifference(LocalDate d1, LocalDate d2) {
        int dayDiff = d1.getDayOfYear() - d2.getDayOfYear();
        int yearDiff = d1.getYear() - d2.getYear();
        return (dayDiff + 365 * yearDiff);
    }

    private void populateChart() {
        // creates a copy of the main list
        listCopy.clear();
        listCopy.addAll(transactionsList);
        // this copy will always be sorted by ascending date: the first element is the oldest.
        listCopy.sort(new Comparators().compareByAscendingDate());
        // removes the previously saved points from the graph
        series.getData().clear();

        double spent = 0, gained = 0, total = 0;
        for (Transaction t : listCopy) {
            if (t.getAmount() > 0)
                gained += t.getAmount();
            else
                spent -= t.getAmount();
            total += t.getAmount();
            int timeDiff = dateDifference(t.getDate(), LocalDate.now());
            series.getData().add(new XYChart.Data<Number, Number>(timeDiff, total));
        }

        totalGainedText.setText("€ " + String.format("%.2f", gained));
        totalSpentText.setText("€ " + String.format("%.2f", spent));
        totalText.setText("€ " + String.format("%.2f", total));
    }

    // this function is called whenever something changes in the list
    // (e.g. after every addition, removal or title change)
    private void onChange() {
        sortList();
        fileHandler.saveToFile(backupFilename, transactionsList);
        populateChart();
    }

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        transactionsList = FXCollections.observableArrayList();
        listCopy = FXCollections.observableArrayList();
        fileHandler = new FileHandler(rootPane);
        comparators = new Comparators();
        sortingComparator = comparators.compareByDescendingDate();
        // needed to display the chart on the X axis
        // it counts the days going from 0 (today). e.g. yesterday is -1, tomorrow is 1
        // on the Y axis it displays the total amount of money the user had on that X day
        series = new XYChart.Series<>();
        chart.getData().add(series);

        // sets the date picker to "today" by default
        datePicker.setValue(LocalDate.now());

        // makes the proper associations between the table and the properties to display
        mainTable.setItems(transactionsList);
        amountClm.setCellValueFactory(new PropertyValueFactory<Transaction, Double>("amount"));
        dateClm.setCellValueFactory(new PropertyValueFactory<Transaction, LocalDate>("date"));
        titleClm.setCellValueFactory(new PropertyValueFactory<Transaction, String>("title"));
        // to be able to edit this column
        titleClm.setCellFactory(TextFieldTableCell.forTableColumn());
        // to be able to select more items at a time
        mainTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // can't perform some menu actions if the list is empty
        SimpleListProperty<Transaction> s = new SimpleListProperty<>(transactionsList);
        exportMenu.disableProperty().bind(s.emptyProperty());
        itemDeleteMenu.disableProperty().bind(s.emptyProperty());
        sortListMenu.disableProperty().bind(s.emptyProperty());
        selectAllMenuItem.disableProperty().bind(s.emptyProperty());
        // can't delete or deselect if nothing is selected in the table
        deselectMenuItem.disableProperty().bind(mainTable.getSelectionModel().selectedItemProperty().isNull());
        deleteSelectionMenuItem.disableProperty().bind(mainTable.getSelectionModel().selectedItemProperty().isNull());
        // can't add new transaction to the list if an amount is not given
        addBtn.disableProperty().bind(amountField.textProperty().isEmpty());
        // can't clear the input fields if both of them are already empty
        clearBtn.disableProperty().bind(amountField.textProperty().isEmpty().and(titleField.textProperty().isEmpty()));

        // loads the list of transactions from a binary file as soon as the app is launched
        fileHandler.loadFromFile(backupFilename, transactionsList, false);
        onChange();
    }
}

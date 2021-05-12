package src.main.java.resources;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.chart.LineChart;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

public class FileHandler {
    private AnchorPane rootPane;

    public FileHandler(AnchorPane rootPane) {
        this.rootPane = rootPane;
        try {
            Files.createDirectories(Paths.get("data/"));
        } catch (IOException e) {
            AlertUser.show("Error", null, "Could not create folder to store user's data.");
        }
    }

    // launches a FileChooser with the given preferences
    private File chooseFile(String chooserTitle, boolean toSave, String initialFileName, FileChooser.ExtensionFilter extensionFilter) {
        File file;
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle(chooserTitle);
        fileChooser.setInitialFileName(initialFileName);
        fileChooser.getExtensionFilters().add(extensionFilter);

        // different method based on wether the file has to be opened for writing or reading
        if (toSave)
            file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        else
            file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        return file;
    }

    // checks wether the given file exists and has the right extension
    private boolean validFile(File file, String extension) {
        if (file == null)
            return false;
        if (file.getName().endsWith("." + extension))
            return true;

        // shows an alert informing the user that the file won't be used for the desired operations
        AlertUser.show("Error in choosing file", null, "Can only operate with " + extension.toUpperCase() + " files.");
        return false;
    }

    public boolean importFromCSV(ObservableList<Transaction> list) {
        // lets the user choose a file
        File file = chooseFile("Import from...", false, null, new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        if (!validFile(file, "csv"))
            return false;
        // populates the list by reading the content of the file
        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(file)))) {
            list.clear();
            sc.useDelimiter(" \\| |.\n");
            sc.nextLine();
            while (sc.hasNext())
                list.add(new Transaction(sc.nextDouble(), LocalDate.parse(sc.next()), sc.next()));
        } catch (IOException e) {
            AlertUser.show("Error", null, "Exception while importing from CSV file");
            return false;
        }
        return true;
    }

    public boolean importFromTXT(ObservableList<Transaction> list) {
        // lets the user choose a file
        File file = chooseFile("Import from...", false, null, new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        if (!validFile(file, "txt"))
            return false;
        // populates the list by reading the content of the file
        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(file)))) {
            list.clear();
            sc.useDelimiter("Amount: |\nDate:   |\nTitle:  |\nAmount: ");
            while (sc.hasNext())
                list.add(new Transaction(sc.nextDouble(), LocalDate.parse(sc.next()), sc.next()));
        } catch (IOException e) {
            AlertUser.show("Error", null, "Exception while importing from Text file");
            return false;
        }
        return true;
    }

    public void exportToCSV(ObservableList<Transaction> list) {
        // lets the user choose a file
        File file = chooseFile("Export to...", true, "myFinances.csv", new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
        if (!validFile(file, "csv"))
            return;
        // writes the content of the list onto the file with the apposite syntax
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            // first line in the file used to show syntax rules
            pw.print("amount | date | title.\n");
            for (Transaction t : list)
                pw.print(t.getAmount() + " | " + t.getDate() + " | " + t.getTitle().replaceAll(".\n", ";\n") + ".\n");
        } catch (IOException e) {
            AlertUser.show("Error", null, "Exception while exporting to CSV file");
        }
    }

    public void exportToTXT(ObservableList<Transaction> list) {
        // lets the user choose a file
        File file = chooseFile("Export to...", true, "myFinances.txt", new FileChooser.ExtensionFilter("Text files (*.txt)", "*.txt"));
        if (!validFile(file, "txt"))
            return;
        // writes the content of the list onto the file
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            for (Transaction t : list)
                pw.print(t + "\n");
        } catch (IOException e) {
            AlertUser.show("Error", null, "Exception while exporting to TXT file");
        }
    }

    public void exportToPNG(LineChart<Number, Number> chart) {
        // lets the user choose a file
        WritableImage image = chart.snapshot(null, null);
        File file = chooseFile("Save image to...", true, "myFinances.png", new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
        if (!validFile(file, "png"))
            return;
        // writes the image onto the file
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException e) {
            AlertUser.show("Error", null, "Exception while saving PNG file");
        }
    }

    // saves the content of the given list onto the binary file
    public void saveToFile(String filename, ObservableList<Transaction> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))) {
            oos.writeObject(new ArrayList<Transaction>(list));
        } catch (IOException e) {
            AlertUser.show("Error", null, "Exception while saving state to file");
        }
    }

    // reads the list from the binary file
    public void loadFromFile(String filename, ObservableList<Transaction> list, boolean showError) {
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))) {
            list.addAll((ArrayList<Transaction>) ois.readObject());
        } catch (FileNotFoundException e1) {
            // when the user launches the app for the first time the backup file
            // will not be found. In that case I don't want to show an error message
            if (showError)
                AlertUser.show("Error", null, "File not found");
        } catch (IOException | ClassNotFoundException e2) {
            AlertUser.show("Error", null, "Exception while loading state from file");
        }
    }
}

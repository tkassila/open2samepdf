package com.metait.open2samepdf;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Open2SamePdfController {
    @FXML
    private Button buttonOpen;
    @FXML
    private Label labelMsg2;
    @FXML
    private TextArea textAreaMsg;
    @FXML
    private ComboBox<String> comboBoxExtension;
    private final FileChooser fileChooser = new FileChooser();
    private Stage mainStage;
    private final String cstPdfFiles = "Choose only pdf files";
    private final String cstAllFiles = "Choose all kind files";
    private final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)",
            "*.pdf");
    private final AtomicBoolean underDropEvent = new AtomicBoolean(false);

    public void setStage(Stage stage)
    {
        mainStage = stage;
    }


    @FXML
    public void initialize() {
       // System.out.println("second");
        textAreaMsg.setText("Open a file, like pdf, and that will open into 2 temp files, which will be deleted\nafter the execution! You can also drop a open file.\n");
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().add(extFilter);
        comboBoxExtension.getItems().add(cstPdfFiles);
        comboBoxExtension.getItems().add(cstAllFiles);
        comboBoxExtension.getSelectionModel().selectFirst();
    }

    private void openFileInPdfReader(File temp)
            throws IOException, InterruptedException
    {
            String command = "open " +temp.getAbsolutePath();
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(command);
            /*
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int ret = process.waitFor();
            if (ret != 0)
                System.out.println("Process exit value: " +ret);
            reader.close();
            */
    }

    public static String getUniqueFileName(String directory, String extension) {
        String fileName = MessageFormat.format("{0}.{1}", UUID.randomUUID(), extension.trim());
        return Paths.get(directory, fileName).toString();
    }

    @FXML
    protected void buttonOpenPressed() throws IOException {
        int selectedCombo = comboBoxExtension.getSelectionModel().getSelectedIndex();
        if (selectedCombo > -1)
        {
            if (selectedCombo == 0)
            {
                if (fileChooser.getExtensionFilters().isEmpty())
                    fileChooser.getExtensionFilters().add(extFilter);
            }
            else
            {
                if (!fileChooser.getExtensionFilters().isEmpty())
                    fileChooser.getExtensionFilters().remove(extFilter);
            }
        }

        File selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null && selectedFile.exists())
        {
            open2TempFilesByOSWith(selectedFile);
        }
    }

    @FXML
    protected void handleDrop(DragEvent dragEvent)
    {
        List<File> fileList = dragEvent.getDragboard().getFiles();
        if (fileList == null || fileList.isEmpty())
            return;
        File fileDropped = fileList.get(0);
        if (fileDropped == null || !fileDropped.exists() || !fileDropped.canRead())
            return;
        if (underDropEvent.get())
            return;
        underDropEvent.set(true);
        open2TempFilesByOSWith(fileDropped);
        underDropEvent.set(false);
    }

    @FXML
    protected void handleDragOver(DragEvent dragEvent)
    {
        if (dragEvent.getDragboard().hasFiles())
        {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    private void open2TempFilesByOSWith(File selectedFile)
    {

        Thread t = new Thread(() -> {
            //Here write all actions that you want execute on background
            File temp1 = null, temp2 = null;
            try {
                temp1 = File.createTempFile(getUniqueFileName("/tmp","pdf"), null);
                temp1.deleteOnExit();
                Files.copy(selectedFile.toPath(), temp1.toPath(), StandardCopyOption.REPLACE_EXISTING);
                temp2 = File.createTempFile(getUniqueFileName("/tmp","pdf"), null);
                temp2.deleteOnExit();
                Files.copy(selectedFile.toPath(), temp2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                textAreaMsg.setText(textAreaMsg.getText() +"\n Open: " +temp1.getAbsolutePath());
                openFileInPdfReader(temp1);
                try {
                    Thread.sleep(400);
                }catch ( InterruptedException ie)
                {
                }
                textAreaMsg.setText(textAreaMsg.getText() +"\n Open: " +temp2.getAbsolutePath());
                openFileInPdfReader(temp2);
            }catch (IOException | InterruptedException ioe){
                final String strMsg = "Error occurred in opening: " +ioe.getMessage();
                Platform.runLater(() -> {
                    labelMsg2.setText(strMsg);
                });
                if (temp1 != null && temp1.exists())
                    temp1.delete();
                if (temp2 != null && temp2.exists())
                    temp2.delete();
                return;
            }
            Platform.runLater(() -> {
                labelMsg2.setText("Open files.");
            });
        });
        t.start();
    }
}
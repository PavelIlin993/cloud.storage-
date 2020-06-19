package com.pavelilin.cloud.storage.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public final class InterfaceController implements Initializable {

  @FXML
  ListView<FileInfo> fileList;

  @FXML
  TextField pathField;

  @FXML
  Button downloadButton;

  @FXML
  Button uploadButton;

  private ClientFileSharingBehavior clientBehavior;

  private boolean isClientSide = true;

  Path root;

  private void setClientBehavior(ClientFileSharingBehavior behavior) {
    this.clientBehavior = behavior;
  }

  private static String pathToFile;

  public void exitPlatform(ActionEvent actionEvent) {
    Platform.exit();
  }

  public void changeToServerFiles(ActionEvent actionEvent) {
    isClientSide = false;
    // no file is chosen by default; disabling buttons
    downloadButton.setDisable(true);
    uploadButton.setDisable(true);
    List<String> serverFileList = clientBehavior.getServerFileList();
    List<FileInfo> fileInfos = listServerFiles(serverFileList);
    pathField.setText("Server files"); // not letting
    fileList.getItems().clear();
    fileList.getItems().addAll(fileInfos);
  }

  public void changeToClientFiles(ActionEvent actionEvent) {
    isClientSide = true;
    // no file is chosen by default; disabling buttons
    downloadButton.setDisable(true);
    uploadButton.setDisable(true);
    goToPath(Paths.get("Client DIR"));
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    downloadButton.setDisable(true);
    uploadButton.setDisable(true);
    setClientBehavior(new SocketClientFileSharingBehavior());

    // create cells
    fileList.setCellFactory(param -> new FileListCell());
    goToPath(Paths.get("Client DIR"));
  }

  public void uploadFile() {
    try {
      uploadButton.setDisable(true); // disable button during operation
      clientBehavior.uploadFile(Paths.get(pathToFile));
      showConfirmation("Upload is completed",
          String.format("The file %s has been successfully uploaded to server", pathToFile));
    } catch (Throwable e) {
      showError(e, "An error occurred during file uploading.");
    } finally {
      uploadButton.setDisable(true);
    }
  }

  public void downloadFile() {
    try {
      downloadButton.setDisable(true); // disable button during operation
      clientBehavior.downloadFile(pathToFile);
      showConfirmation("Download is completed",
          String.format("The file %s has been successfully downloaded from server", pathToFile));
    } catch (Throwable e) {
      showError(e, "An error occurred during file downloading.");
    } finally {
      downloadButton.setDisable(false);
    }
  }

  public void goToPath(Path path) {
    root = path;
    pathField.setText(root.toAbsolutePath().toString());
    fileList.getItems().clear();
    fileList.getItems().addAll(scanFiles(path));
  }

  public List<FileInfo> scanFiles(Path root) {
    List<FileInfo> files = new ArrayList<>();
    List<Path> pathsInRoot;
    try {
      pathsInRoot = Files.list(root).collect(Collectors.toList());
      for (Path p : pathsInRoot) {
        files.add(new FileInfo(p));
      }
      return files;
    } catch (IOException e) {
      throw new RuntimeException("File scan exception: " + root);
    }
  }

  public List<FileInfo> listServerFiles(List<String> serverFiles) {
    List<FileInfo> files = new ArrayList<>();
    for (String serverFileName : serverFiles) {
      files.add(new FileInfo(serverFileName, -2));
    }
    return files;
  }

  public void filesListClicked(MouseEvent mouseEvent) {
    if (mouseEvent.getClickCount() == 1) {
      FileInfo fileInfo = fileList.getSelectionModel().getSelectedItem();
      if (fileInfo.getLength() == -2) {
        pathToFile = fileInfo.getFileName();
      } else {
        pathToFile = root.toAbsolutePath().toString() + "\\" + fileInfo.getFileName();
      }
      // in order to avoid NPE or unexpected element, disable and enable corresponding buttons
      if (isClientSide) {
        downloadButton.setDisable(true);
        uploadButton.setDisable(false);
      } else {
        downloadButton.setDisable(false);
        uploadButton.setDisable(true);
      }
    }
  }

  private static class FileListCell extends ListCell<FileInfo> {
    @Override
    protected void updateItem(FileInfo item, boolean empty) {
      super.updateItem(item, empty);
      if (item == null || empty) {
        setText(null);
        setStyle(null);
      } else {
        String formattedFileName = String.format("%-30s", item.getFileName());
        String formattedFileLength = String.format("%,d bytes", item.getLength());
        if (item.isDirectory()) {
          formattedFileLength = String.format("%s", "[DIR]");
        }
        if (item.isUpElement() || item.isServerFile()) {
          formattedFileLength = "";
        }
        String text = String.format("%s %-20s", formattedFileName, formattedFileLength);
        setText(text);
      }
    }
  }

  private void showError(Throwable e, String header) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error alert");
    alert.setHeaderText(header);
    VBox dialogPaneContent = new VBox();
    Label label = new Label("Stack Trace:");
    TextArea textArea = new TextArea();
    textArea.setText(Arrays.stream(e.getStackTrace())
        .map(StackTraceElement::toString)
        .collect(Collectors.joining("\n")));
    dialogPaneContent.getChildren().addAll(label, textArea);
    alert.getDialogPane().setContent(dialogPaneContent);
    alert.show();
  }

  private void showConfirmation(String header, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
    alert.setHeaderText(header);
    alert.show();
  }

}

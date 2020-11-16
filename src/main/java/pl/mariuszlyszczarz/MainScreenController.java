package pl.mariuszlyszczarz;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class MainScreenController {

    public static final Logger logger = Logger.getLogger(MainScreenController.class);
    public static final String START_PICTURE = "src/main/resources/image/MainScreen.png";

    @FXML
    BorderPane borderPane;
    private static BorderPane pane;

    @FXML
    TextField sizeXTextField, sizeYTextField, numberOfGrainsTextField, delayTextField, numberOfInclusionsTextField, sizeOfInclusionsTextField;

    @FXML
    ChoiceBox<String> methodChoiceBox, typeOfInclusionsChoiceBox, methodOfPrintChoiceBox;

    @FXML
    Button startButton;

    @FXML
    Label labelStatus;

    @FXML
    CheckBox periodicCheckBox;

    @FXML
    ImageView imageView;

    @FXML
    public void initialize() {
        //BasicConfigurator.configure();
        logger.debug("Initialize has started");

        pane = borderPane;
        logger.debug("Create copy of borderPane");

        FileManager.startReadFileColorInOtherThread(logger, startButton, labelStatus);

        File file = new File(START_PICTURE);

        try {
            imageView.setImage(SwingFXUtils.toFXImage(ImageIO.read(file), null));
            logger.debug("Show start image");
        } catch (IOException e) {
            logger.error("Don't find start image");
        }

        labelStatus.setText("Ready");
        methodChoiceBox.getItems().addAll("Moore", "Von Neumann");
        methodChoiceBox.setValue("Moore");
        typeOfInclusionsChoiceBox.getItems().addAll("Circular", "Square");
        typeOfInclusionsChoiceBox.setValue("Circular");
        methodOfPrintChoiceBox.getItems().addAll("At the beginning", "After simulation");
        methodOfPrintChoiceBox.setValue("At the beginning");

        logger.debug("Initialize has ended");
    }

    @FXML
    public void clickStartButton() {
        logger.info("Start button has been clicked");

        switch (methodChoiceBox.getValue()) {
            case "Moore":
                logger.info("Moore algorithm has been started");

                MooreMethod mooreMethod = new MooreMethod();
                BufferedImage bufferedImage = mooreMethod.prepareImage(Integer.parseInt(sizeXTextField.getText()), Integer.parseInt(sizeYTextField.getText()));
                imageView.setFitWidth(bufferedImage.getWidth());
                imageView.setFitHeight(bufferedImage.getHeight());

                //todo inclusion on the begin

                bufferedImage = mooreMethod.putGrainsToImage(Integer.parseInt(numberOfGrainsTextField.getText()), bufferedImage);
                final BufferedImage[] finalBufferedImage = {bufferedImage};
                Runnable runnable = () -> {
                    imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    while(!mooreMethod.isEndGrow(finalBufferedImage[0])){
                        try {
                            Thread.sleep(Integer.parseInt(delayTextField.getText()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finalBufferedImage[0] = mooreMethod.implementationMethod((finalBufferedImage[0]),periodicCheckBox);
                        imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    }
                };

                Thread thread = new Thread(runnable);
                thread.start();

                break;
            case "Von Neumann":
                logger.info("Moore Von Neumann has been started");

        }

    }

    @FXML
    public void exportDataFile() {
        logger.info("Called method exportDataFile");
        FileManager.saveDataToDataFile();
    }

    @FXML
    public void exportBitmap() {
        logger.info("Called method exportBitmap");
        try {
            FileManager.saveBitmap(imageView);
            logger.info("Bitmap has been saved");
        } catch (IOException e) {
            logger.error("Bitmap hasn't been saved");
            e.printStackTrace();
        }
    }

    ;

    @FXML
    public void importDataFile() {
        logger.info("Called method importDataFile");
        FileManager.readDataFormDataFile();
    }

    @FXML
    public void importBitmap() {
        logger.info("Called method importBitmap");
        try {
            Image image = FileManager.readBitmap();
            imageView.setFitWidth(image.getWidth());
            imageView.setFitHeight(image.getHeight());
            imageView.setImage(image);
            logger.info("Read image");
        } catch (IOException e) {
            logger.error("Can't open image");
        }
    }

    ;

    public static File choiceFile(String extensionOfFile, String typeOfAction) {

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(extensionOfFile.toUpperCase() + " files (*." + extensionOfFile + ")", "*." + extensionOfFile);
        fileChooser.getExtensionFilters().add(extensionFilter);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));

        File file = null;
        switch (typeOfAction) {
            case "open":
                file = fileChooser.showOpenDialog(pane.getScene().getWindow());
                break;
            case "save":
                file = fileChooser.showSaveDialog(pane.getScene().getWindow());
                break;
        }

        return file;
    }

    private void changeStatusToBusy() {
        labelStatus.setText("Busy");
    }

    private void changeStatusToReady() {
        labelStatus.setText("Ready");
    }


}


  /*  public static void main(String[] args) {
        BasicConfigurator.configure();
        logger.info("This is my first log4j's statement");
        logger.debug("This is my first log4j's statement");
        logger.error("This is my first log4j's statement");
        logger.trace("This is my first log4j's statement");
        logger.warn("This is my first log4j's statement");
        logger.fatal("This is my first log4j's statement");
    }*/


//http://home.agh.edu.pl/~lmadej/wp-content/uploads/wyklad_5a.pdf
//http://home.agh.edu.pl/~lmadej/wp-content/uploads/wyklad_6_7-1.pdf

//kupić folie na drzwi
//zapytać o dekoder
//castorama

//http://home.agh.edu.pl/~lmadej/wp-content/uploads/LM_MSM_SN_2020-21_Project_1.pdf

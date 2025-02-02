package pl.mariuszlyszczarz;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainScreenController {

    public static final Logger logger = Logger.getLogger(MainScreenController.class);

    @FXML
    BorderPane borderPane;
    private static BorderPane pane;

    @FXML
    TextField sizeXTextField, sizeYTextField, numberOfGrainsTextField, delayTextField, numberOfInclusionsTextField, sizeOfInclusionsTextField;

    @FXML
    ChoiceBox<String> methodChoiceBox, typeOfInclusionsChoiceBox, methodOfPrintChoiceBox;

    @FXML
    Button startButton, grainBoundariesButton;

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

                final BufferedImage[] finalBufferedImage = {bufferedImage};
                Runnable runnable = () -> {
                    if (Integer.parseInt(numberOfInclusionsTextField.getText()) != 0 && Integer.parseInt(sizeOfInclusionsTextField.getText()) != 0 && methodOfPrintChoiceBox.getValue().equals("At the beginning")) {
                        mooreMethod.putInclusionToPictureBeforeStart(Integer.parseInt(numberOfInclusionsTextField.getText()), Integer.parseInt(sizeOfInclusionsTextField.getText()), typeOfInclusionsChoiceBox, finalBufferedImage[0]);
                        imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                        try {
                            new Robot().delay(Integer.parseInt(delayTextField.getText()));
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                    }

                    finalBufferedImage[0] = mooreMethod.putGrainsToImage(Integer.parseInt(numberOfGrainsTextField.getText()), bufferedImage);

                    imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    while (!mooreMethod.isEndGrow(finalBufferedImage[0])) {
                        try {
                            new Robot().delay(Integer.parseInt(delayTextField.getText()));
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                        finalBufferedImage[0] = mooreMethod.implementationMethod((finalBufferedImage[0]), periodicCheckBox);
                        imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    }
                    if (Integer.parseInt(numberOfInclusionsTextField.getText()) != 0 && Integer.parseInt(sizeOfInclusionsTextField.getText()) != 0 && methodOfPrintChoiceBox.getValue().equals("After simulation")) {
                        mooreMethod.putInclusionToPictureAfterGrainGrowth(Integer.parseInt(numberOfInclusionsTextField.getText()), Integer.parseInt(sizeOfInclusionsTextField.getText()), typeOfInclusionsChoiceBox, finalBufferedImage[0]);
                        try {
                            new Robot().delay(Integer.parseInt(delayTextField.getText()));
                        } catch (AWTException e) {
                            e.printStackTrace();
                        }
                        imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    }

                };

                Thread thread = new Thread(runnable);
                thread.start();

                break;
            case "Von Neumann":
                logger.info("Von Neumann algorithm has been started");

        }

    }

    @FXML
    public void clickGrainBoundariesButton(){
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(),null);
        List<Point> listOfTransitions = GrainGrowthModel.findAllTransitions(bufferedImage);
        Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
        listOfTransitions.forEach(s -> bufferedImage.setRGB(s.x,s.y, Color.BLACK.getRGB()));
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
    }

    @FXML
    public void exportDataFile() {
        logger.info("Called method exportDataFile");
        FileManager.saveDataToDataFile(imageView);
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

    @FXML
    public void importDataFile() {
        logger.info("Called method importDataFile");
        try {
            Image image = FileManager.readDataFormDataFile();
            imageView.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

//http://home.agh.edu.pl/~lmadej/wp-content/uploads/LM_MSM_SN_2020-21_Project_1.pdf

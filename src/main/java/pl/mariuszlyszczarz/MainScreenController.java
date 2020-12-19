package pl.mariuszlyszczarz;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class MainScreenController {

    public static final Logger logger = Logger.getLogger(MainScreenController.class);

    private List<Integer> listOfChoseColorDualPhase = new ArrayList<>();

    @FXML
    BorderPane borderPane;
    private static BorderPane pane;

    @FXML
    TextField sizeXTextField, sizeYTextField, numberOfGrainsTextField, delayTextField, numberOfInclusionsTextField, sizeOfInclusionsTextField, percentChanceToFillTextField, numberOfGrainsSubstructureTextField;

    @FXML
    ChoiceBox<String> methodChoiceBox, typeOfInclusionsChoiceBox, methodOfPrintChoiceBox;

    @FXML
    Button startButton, grainBoundariesButton, generateSubstructureButton, generateDualPhase, clearGrainToDualPhase;

    @FXML
    ToggleButton addGrainToDualPhase;

    @FXML
    Label labelStatus;

    @FXML
    CheckBox periodicCheckBox;

    @FXML
    ImageView imageView;

    @FXML
    TableView<TableInfoGrainModel> tableView;

    @FXML
    public void initialize() {
        //BasicConfigurator.configure();
        logger.debug("Initialize has started");

        pane = borderPane;
        logger.debug("Create copy of borderPane");

        FileManager.startReadFileColorInOtherThread(logger, startButton, labelStatus);

        labelStatus.setText("Ready");
        methodChoiceBox.getItems().addAll("Moore", "Von Neumann", "Grain boundary shape control");
        methodChoiceBox.setValue("Moore");
        typeOfInclusionsChoiceBox.getItems().addAll("Circular", "Square");
        typeOfInclusionsChoiceBox.setValue("Circular");
        methodOfPrintChoiceBox.getItems().addAll("At the beginning", "After simulation");
        methodOfPrintChoiceBox.setValue("At the beginning");

        TableColumn<TableInfoGrainModel, String> id = new TableColumn<>("Id");
        id.setPrefWidth(75.0);
        id.setStyle("-fx-alignment: CENTER");
        TableColumn<TableInfoGrainModel, String> size = new TableColumn<>("Size");
        size.setPrefWidth(75.0);
        size.setStyle("-fx-alignment: CENTER");
        TableColumn<TableInfoGrainModel, String> percent = new TableColumn<>("%");
        percent.setPrefWidth(75.0);
        percent.setStyle("-fx-alignment: CENTER");
        TableColumn<TableInfoGrainModel, String> color = new TableColumn<>("Color");
        Callback<TableColumn<TableInfoGrainModel, String>, TableCell<TableInfoGrainModel, String>> cellFactory =
                new Callback<TableColumn<TableInfoGrainModel, String>, TableCell<TableInfoGrainModel, String>>() {
                    public TableCell<TableInfoGrainModel, String> call(TableColumn p) {
                        TableCell<TableInfoGrainModel, String> cell = new TableCell<TableInfoGrainModel, String>() {
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                setText(empty ? null : getString().substring(21, 28));
                                setStyle("" + getString() + "-fx-alignment: CENTER;");
                            }

                            private String getString() {
                                return getItem() == null ? "" : "-fx-background-color:" + getItem() + ";";
                            }
                        };
                        return cell;
                    }
                };
        color.setCellFactory(cellFactory);
        color.setPrefWidth(75);
        color.setStyle("-fx-alignment: CENTER");
        tableView.getColumns().addAll(id, size, percent, color);
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        percent.setCellValueFactory(new PropertyValueFactory<>("percent"));
        color.setCellValueFactory(new PropertyValueFactory<>("color"));

        imageView.setPickOnBounds(false);

        addGrainToDualPhase();

        logger.debug("Initialize has ended");
    }

    @FXML
    public void clickStartButton() {
        logger.info("Start button has been clicked");

        choseOfTypeGrainGrowth(null, null);

    }

    private void choseOfTypeGrainGrowth(Integer colorDualPhase, BufferedImage bufferedImageDualPhase) {
        Set<Integer> setOfColorBackground = new LinkedHashSet<>();
        setOfColorBackground.add(GrainGrowthModel.IMAGE_BACKGROUND_COLOR);

        switch (methodChoiceBox.getValue()) {
            case "Moore":
                logger.info("Moore algorithm has been started");

                MooreMethod mooreMethod = new MooreMethod();
                runMethodGrow(mooreMethod, setOfColorBackground, colorDualPhase, bufferedImageDualPhase);

                break;
            case "Von Neumann":
                logger.info("Von Neumann algorithm has been started");

                VonNeumannMethod vonNeumannMethod = new VonNeumannMethod();
                runMethodGrow(vonNeumannMethod, setOfColorBackground, colorDualPhase, bufferedImageDualPhase);
                break;
            case "Grain boundary shape control":
                logger.info("Grain boundary shape control algorithm has been started");

                GrainBoundaryShapeControlMethod grainBoundaryShapeControlMethod = new GrainBoundaryShapeControlMethod();
                runMethodGrow(grainBoundaryShapeControlMethod, setOfColorBackground, colorDualPhase, bufferedImageDualPhase);
                break;

        }
    }

    private void runMethodGrow(GrainGrowthModel grainGrowthModel, Set<Integer> setOfColorBackground, Integer colorDualPhase, BufferedImage bufferedImageDualPhase) {
        if (Integer.parseInt(percentChanceToFillTextField.getText().trim()) != 0) {
            disableButtons();
            final BufferedImage[] bufferedImage = {grainGrowthModel.prepareImage(Integer.parseInt(sizeXTextField.getText()), Integer.parseInt(sizeYTextField.getText()))};
            BufferedImage coreBufferedImage = GrainGrowthModel.deepCopyOfBufferedImage(bufferedImage[0]);
            imageView.setFitWidth(bufferedImage[0].getWidth());
            imageView.setFitHeight(bufferedImage[0].getHeight());

            final BufferedImage[] finalBufferedImage = {bufferedImage[0]};
            Runnable runnable = () -> {
                if (Integer.parseInt(numberOfInclusionsTextField.getText()) != 0 && Integer.parseInt(sizeOfInclusionsTextField.getText()) != 0 && methodOfPrintChoiceBox.getValue().equals("At the beginning")) {
                    grainGrowthModel.putInclusionToPictureBeforeStart(Integer.parseInt(numberOfInclusionsTextField.getText()), Integer.parseInt(sizeOfInclusionsTextField.getText()), typeOfInclusionsChoiceBox, finalBufferedImage[0]);
                    imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    try {
                        new Robot().delay(Integer.parseInt(delayTextField.getText()));
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }
                if (bufferedImageDualPhase != null){
                    try {
                        new Robot().delay(Integer.parseInt(delayTextField.getText()));
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    delayTextField.setText("0"); //todo previous value
                    bufferedImage[0] =null;
                    bufferedImage[0] = GrainGrowthModel.deepCopyOfBufferedImage(bufferedImageDualPhase);
                }

                finalBufferedImage[0] = grainGrowthModel.putGrainsToImage(Integer.parseInt(numberOfGrainsTextField.getText()), bufferedImage[0]);

                imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));

                while (!grainGrowthModel.isEndGrow(finalBufferedImage[0])) {
                    try {
                        new Robot().delay(Integer.parseInt(delayTextField.getText()));
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    finalBufferedImage[0] = grainGrowthModel.implementationMethod(coreBufferedImage, (finalBufferedImage[0]), periodicCheckBox, Integer.parseInt(percentChanceToFillTextField.getText().trim()), setOfColorBackground, colorDualPhase);
                    imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                    grainGrowthModel.fillInfoTable(tableView, finalBufferedImage[0]);
                }
                if (Integer.parseInt(numberOfInclusionsTextField.getText()) != 0 && Integer.parseInt(sizeOfInclusionsTextField.getText()) != 0 && methodOfPrintChoiceBox.getValue().equals("After simulation")) {
                    grainGrowthModel.putInclusionToPictureAfterGrainGrowth(Integer.parseInt(numberOfInclusionsTextField.getText()), Integer.parseInt(sizeOfInclusionsTextField.getText()), typeOfInclusionsChoiceBox, finalBufferedImage[0]);
                    try {
                        new Robot().delay(Integer.parseInt(delayTextField.getText()));
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                    imageView.setImage(SwingFXUtils.toFXImage(finalBufferedImage[0], null));
                }
                enableButtons();
            };

            Thread thread = new Thread(runnable);
            thread.start();
        }
    }

    @FXML
    public void clickGrainBoundariesButton() {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        List<Point> listOfTransitions = GrainGrowthModel.findAllTransitions(bufferedImage);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        listOfTransitions.forEach(s -> bufferedImage.setRGB(s.x, s.y, Color.BLACK.getRGB()));
        imageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
    }

    @FXML
    public void clickGenerateSubstructureButton() {
        logger.info("Called method GenerateSubstructureButton");
        disableButtons();

        Image coreImage = imageView.getImage();
        BufferedImage coreBufferedImage = SwingFXUtils.fromFXImage(coreImage, null);

        Set<Integer> setOfColorBackground = new LinkedHashSet<>();
        for (int y = 0; y < coreBufferedImage.getHeight(); y++) {
            for (int x = 0; x < coreBufferedImage.getWidth(); x++) {
                setOfColorBackground.add(coreBufferedImage.getRGB(x, y));
            }
        }

        switch (methodChoiceBox.getValue()) {
            case "Moore":
                logger.info("Moore algorithm has been started");

                MooreMethod mooreMethod = new MooreMethod();
                runMethodGrowSubstructure(mooreMethod, setOfColorBackground);

                break;
            case "Von Neumann":
                logger.info("Von Neumann algorithm has been started");

                VonNeumannMethod vonNeumannMethod = new VonNeumannMethod();
                runMethodGrowSubstructure(vonNeumannMethod, setOfColorBackground);
                break;
            case "Grain boundary shape control":
                logger.info("Grain boundary shape control algorithm has been started");

                GrainBoundaryShapeControlMethod grainBoundaryShapeControlMethod = new GrainBoundaryShapeControlMethod();
                runMethodGrowSubstructure(grainBoundaryShapeControlMethod, setOfColorBackground);
                break;
        }
    }

    private void runMethodGrowSubstructure(GrainGrowthModel grainGrowthModel, Set<Integer> setOfColorBackground) {
        Image coreImage = imageView.getImage();
        BufferedImage coreBufferedImage = SwingFXUtils.fromFXImage(coreImage, null);
        final BufferedImage[] bufferedImage = {GrainGrowthModel.deepCopyOfBufferedImage(coreBufferedImage)};

        Runnable runnable = () -> {
            //System.out.println("wszedłem i myśle");
            bufferedImage[0] = grainGrowthModel.putGrainsToImageSubstructure(coreBufferedImage, Integer.parseInt(numberOfGrainsTextField.getText()), Integer.parseInt(numberOfGrainsSubstructureTextField.getText()), bufferedImage[0]);
            //System.out.println("Przestane myśelsć :D");
            imageView.setImage(SwingFXUtils.toFXImage(bufferedImage[0], null));
            //System.out.println("I co teraz");
            while (!grainGrowthModel.isEndGrowSubstructure(bufferedImage[0], setOfColorBackground)) {
                try {
                    new Robot().delay(Integer.parseInt(delayTextField.getText()));
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                bufferedImage[0] = grainGrowthModel.implementationMethod(coreBufferedImage, bufferedImage[0], periodicCheckBox, Integer.parseInt(percentChanceToFillTextField.getText().trim()), setOfColorBackground, null); //todo color of dualPhase

                imageView.setImage(SwingFXUtils.toFXImage(bufferedImage[0], null));

                grainGrowthModel.fillInfoTable(tableView, bufferedImage[0]);
            }
            //System.out.println("Wyszedłem");
            enableButtons();
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @FXML
    public void clickGenerateDualPhase() {
        BufferedImage bufferedImageDualPhase = SwingFXUtils.fromFXImage(imageView.getImage(),null);

        for (int y = 0; y < bufferedImageDualPhase.getHeight(); y++) {
            for (int x = 0; x < bufferedImageDualPhase.getWidth(); x++) {
                if (listOfChoseColorDualPhase.contains(bufferedImageDualPhase.getRGB(x,y))) {
                    bufferedImageDualPhase.setRGB(x,y,listOfChoseColorDualPhase.get(0));
                }
            }
        }

        imageView.setImage(SwingFXUtils.toFXImage(bufferedImageDualPhase,null));

        try {
            new Robot().delay(Integer.parseInt(delayTextField.getText()));
        } catch (AWTException e) {
            e.printStackTrace();
        }

        for (int y = 0; y < bufferedImageDualPhase.getHeight(); y++) {
            for (int x = 0; x < bufferedImageDualPhase.getWidth(); x++) {
                if (listOfChoseColorDualPhase.get(0) != bufferedImageDualPhase.getRGB(x,y)){
                    bufferedImageDualPhase.setRGB(x,y,GrainGrowthModel.IMAGE_BACKGROUND_COLOR);
                }
            }
        }

        imageView.setImage(SwingFXUtils.toFXImage(bufferedImageDualPhase,null));
        delayTextField.setText("1000");
        choseOfTypeGrainGrowth(listOfChoseColorDualPhase.get(0),bufferedImageDualPhase);
        listOfChoseColorDualPhase.clear();
    }

    public void addGrainToDualPhase(){
        imageView.setOnMouseClicked((e) -> {
            if (addGrainToDualPhase.isSelected()) {
                System.out.println("Cords [" + (int) e.getX() + ", " + (int) e.getY() + "]" +
                        " Color int =" + ColorGenerator.getIntColor(imageView, (int) e.getX(), (int) e.getY()) +
                        ", hexColor = " + ColorGenerator.getHexColor(imageView, (int) e.getX(), (int) e.getY()));
                listOfChoseColorDualPhase.add(Integer.parseInt(ColorGenerator.getIntColor(imageView, (int) e.getX(), (int) e.getY())));
                System.out.println(listOfChoseColorDualPhase.size() + " Rozmiar listy");
            }
        });
    }


    @FXML
    public void clickClearGrainToDualPhase() {
        listOfChoseColorDualPhase.clear();
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

    private void disableButtons() {
        generateSubstructureButton.setDisable(true);
        grainBoundariesButton.setDisable(true);
        startButton.setDisable(true);
        addGrainToDualPhase.setDisable(true);
        clearGrainToDualPhase.setDisable(true);
        generateDualPhase.setDisable(true);
    }

    private void enableButtons() {
        generateSubstructureButton.setDisable(false);
        grainBoundariesButton.setDisable(false);
        startButton.setDisable(false);
        addGrainToDualPhase.setDisable(false);
        clearGrainToDualPhase.setDisable(false);
        generateDualPhase.setDisable(false);
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

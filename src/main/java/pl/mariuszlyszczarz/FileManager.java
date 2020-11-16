package pl.mariuszlyszczarz;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;

public class FileManager {
    private static Map<Integer, Integer> mapOfColor;

    public static void saveDataToDataFile() {
        MainScreenController.choiceFile("txt", "save");

    }

    public static void readDataFormDataFile() {
        MainScreenController.choiceFile("txt", "open");

    }

    public static void saveBitmap(ImageView imageView) throws IOException {
        File file = MainScreenController.choiceFile("png", "save");
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        ImageIO.write(bufferedImage, "png", file);
    }

    public static Image readBitmap() throws IOException {
        File file = MainScreenController.choiceFile("png", "open");

        return SwingFXUtils.toFXImage(ImageIO.read(file), null);
    }

    public static Map<Integer, Integer> readMap() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("./src/main/resources/mapOfColor.bat");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<Integer, Integer> map = (Map<Integer, Integer>) objectInputStream.readObject();
        objectInputStream.close();
        return map;
    }

    public static void startReadFileColorInOtherThread(Logger logger, Button startButton, Label labelStatus) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    startButton.setDisable(true);
                    FileManager.setMapOfColor(FileManager.readMap());
                    startButton.setDisable(false);
                    logger.info("Map color has been read");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    logger.info("Map color hasn't been read");
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public static Map<Integer, Integer> getMapOfColor() {
        return mapOfColor;
    }

    public static void setMapOfColor(Map<Integer, Integer> mapOfColor) {
        FileManager.mapOfColor = mapOfColor;
    }

}

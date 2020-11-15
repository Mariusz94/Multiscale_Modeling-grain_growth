package pl.mariuszlyszczarz;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FileManager {

    public static void saveDataToDataFile(){
        MainScreenController.choiceFile("txt", "save");

    }

    public static void readDataFormDataFile(){
        MainScreenController.choiceFile("txt","open");

    }

    public static void saveBitmap(){
        MainScreenController.choiceFile("png","save");

    }

    public static Image readBitmap() throws IOException {
        File file = MainScreenController.choiceFile("png","open");

        return SwingFXUtils.toFXImage(ImageIO.read(file), null);
    }

}

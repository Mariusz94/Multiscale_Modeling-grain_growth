package pl.mariuszlyszczarz;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Map;

public class FileManager {
    private static Map<Integer, Integer> mapOfColor;
    private static final int INCLUSION_ID = 1000000;

    public static void saveDataToDataFile(ImageView imageView) {
        File file = MainScreenController.choiceFile("txt", "save");
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(imageView.getImage(),null);

        FileWriter fileWriter;
        BufferedWriter bufferedWriter;
        try {
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(bufferedImage.getWidth() + " " + bufferedImage.getHeight());
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    bufferedWriter.newLine();
                    bufferedWriter.write(x + " " + y + " " + 0 + " " + findIdColor(bufferedImage.getRGB(x,y)));
                }
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static int findIdColor(int color){
        if (color == GrainGrowthModel.IMAGE_INCLUSIONS_COLOR) return INCLUSION_ID;
        for (Map.Entry<Integer, Integer> colorEntry : mapOfColor.entrySet()) {
            if (colorEntry.getValue() == color) return colorEntry.getKey();
        }
        return 0;
    }
    public static Image readDataFormDataFile() throws IOException {
        File file = MainScreenController.choiceFile("txt", "open");
        BufferedImage bufferedImage;

        BufferedReader bufferedReader;
        String[] dataFromFile;

            bufferedReader = new BufferedReader(new FileReader(file.getPath()));
            String[] size = bufferedReader.readLine().split(" ");
            bufferedImage = new BufferedImage(Integer.parseInt(size[1]),Integer.parseInt(size[0]),BufferedImage.TYPE_INT_RGB);

            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                dataFromFile = temp.split(" ");
                if (Integer.parseInt(dataFromFile[3]) != INCLUSION_ID){
                    bufferedImage.setRGB(Integer.parseInt(dataFromFile[0]),Integer.parseInt(dataFromFile[1]),mapOfColor.get(Integer.parseInt(dataFromFile[3])));
                }else {
                    bufferedImage.setRGB(Integer.parseInt(dataFromFile[0]),Integer.parseInt(dataFromFile[1]),GrainGrowthModel.IMAGE_INCLUSIONS_COLOR);
                }
            }
            bufferedReader.close();
        return SwingFXUtils.toFXImage(bufferedImage,null);
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

    public Map<Integer, Integer> readMap() throws IOException, ClassNotFoundException {

        InputStream inputStream = getClass().getResourceAsStream("/mapOfColor.bin");

        Map<Integer, Integer> map;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            map = (Map<Integer, Integer>) objectInputStream.readObject();
        }

        return map;
    }

    public static void startReadFileColorInOtherThread(Logger logger, Button startButton, Label labelStatus) {
        Runnable runnable = () -> {
            try {
                startButton.setDisable(true);

                FileManager.setMapOfColor(new FileManager().readMap());
                startButton.setDisable(false);
                logger.info("Map color has been read");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                logger.info("Map color hasn't been read");
                e.printStackTrace();
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

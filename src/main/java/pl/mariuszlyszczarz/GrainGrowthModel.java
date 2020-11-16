package pl.mariuszlyszczarz;

import javafx.scene.control.CheckBox;
import org.apache.log4j.Logger;

import javax.swing.text.html.ImageView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public abstract class GrainGrowthModel {
    public static final Logger logger = Logger.getLogger(GrainGrowthModel.class);

    final int IMAGE_BACKGROUND_COLOR = -1184275;

    public abstract BufferedImage implementationMethod(BufferedImage bufferedImage, CheckBox periodicCheckBox);

    public Point generateRandomPoint(int maxX, int maxY){
        Point point = new Point();
        point.setLocation(generateRandomNumber(maxX),generateRandomNumber(maxY));
        logger.debug("Generate random pont [" + point.x + ", " + point.y + "]");
        return point;
    }

    public int generateRandomNumber(int bound){
        return new Random().nextInt(bound);
    }

    public BufferedImage prepareImage(int width,int height){
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setPaint(new Color(237,237,237));
        graphics2D.fillRect(0, 0, width, height);
        logger.debug("BufferedImage has been prepared");
        return bufferedImage;
    }

    public boolean isPointBusy(BufferedImage bufferedImage, Point point){
        if (bufferedImage.getRGB(point.x,point.y) != IMAGE_BACKGROUND_COLOR){
            logger.debug("This point is busy");
            return true;
        }

        return false;
    }

    public BufferedImage putGrainsToImage(int numberOfGrains,BufferedImage bufferedImage){
        Point point;
        for (int i = 0; i < numberOfGrains; i++) {
            do {
                point = generateRandomPoint(bufferedImage.getWidth(), bufferedImage.getHeight());
            }while(isPointBusy(bufferedImage, point));
            //todo generate random color

            bufferedImage.setRGB(point.x,point.y,FileManager.getMapOfColor().get(i));
            logger.debug("Superimposed grain on the image");
        }

        return bufferedImage;
    }

    public boolean isEndGrow(BufferedImage bufferedImage){
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                if (bufferedImage.getRGB(x,y) == IMAGE_BACKGROUND_COLOR)  return false;
            }
        }
        return true;
    }

    private static Color generateRandomColor(){
        Random random = new Random();
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        Color randomColor = new Color(r, g, b);
        logger.debug("Generate random color");
        return randomColor;
    }
}

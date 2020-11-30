package pl.mariuszlyszczarz;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GrainGrowthModel {
    public static final Logger logger = Logger.getLogger(GrainGrowthModel.class);

    final static int IMAGE_BACKGROUND_COLOR = -1184275;
    final static int IMAGE_INCLUSIONS_COLOR = -16777216;

    public abstract BufferedImage implementationMethod(BufferedImage bufferedImage, CheckBox periodicCheckBox, int percentChanceToFill);

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

    public void putInclusionToPictureBeforeStart(int numberOfInclusions, int sizeOfInclusions, ChoiceBox<String> typeOfInclusionsChoiceBox, BufferedImage bufferedImage) {
        List<Point> listOfInclusions = new ArrayList<>();
        for (int i = 0; i < numberOfInclusions; i++) {
            Point inclusionPoint;
            do{
                inclusionPoint = generateRandomPoint(bufferedImage.getWidth(), bufferedImage.getHeight());
            }
            while (isPointInRangeAnotherInclusion(inclusionPoint, sizeOfInclusions, listOfInclusions));
            listOfInclusions.add(inclusionPoint);
        }

        drawShape(sizeOfInclusions, typeOfInclusionsChoiceBox, bufferedImage, listOfInclusions);
    }
    public void putInclusionToPictureAfterGrainGrowth(int numberOfInclusions, int sizeOfInclusions, ChoiceBox<String> typeOfInclusionsChoiceBox, BufferedImage bufferedImage) {
        List<Point> listOfAllTransitions = findAllTransitions(bufferedImage);
        List<Point> listOfInclusions = new ArrayList<>();

        for (int i = 0; i < numberOfInclusions; i++) {
            Point inclusionPoint;
            do{
                inclusionPoint = listOfAllTransitions.get(new Random().nextInt(listOfAllTransitions.size()));
            }
            while (isPointInRangeAnotherInclusion(inclusionPoint, sizeOfInclusions, listOfInclusions));
            listOfInclusions.add(inclusionPoint);
        }

        drawShape(sizeOfInclusions, typeOfInclusionsChoiceBox, bufferedImage, listOfInclusions);
    }

    public static List<Point> findAllTransitions(BufferedImage bufferedImage) {
        List<Point> listGrainBoundaries = new ArrayList<>();
        boolean isFind;

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                isFind = false;
                for (int y1 = -1; y1 < 2; y1++) {
                    for (int x1 = -1; x1 < 2; x1++) {
                        int tempX = x + x1;
                        int tempY = y + y1;
                        if ((tempX >= 0 && tempX < bufferedImage.getWidth() && tempY >= 0 && tempY < bufferedImage.getHeight())) {
                            int value = bufferedImage.getRGB(tempX,tempY);
                            if (bufferedImage.getRGB(x,y) != value && !isFind){
                                listGrainBoundaries.add(new Point(x,y));
                                isFind = true;
                            }
                        }
                    }
                }
            }
        }

        return listGrainBoundaries;

    }

    private void drawShape(int sizeOfInclusions, ChoiceBox<String> typeOfInclusionsChoiceBox, BufferedImage bufferedImage, List<Point> listOfInclusions) {
        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
        graphics2D.setPaint(new Color(IMAGE_INCLUSIONS_COLOR));

        switch (typeOfInclusionsChoiceBox.getValue()){
            case "Circular":
                listOfInclusions.forEach(s-> graphics2D.fillOval(s.x - sizeOfInclusions,s.y- sizeOfInclusions,2* sizeOfInclusions,2* sizeOfInclusions));
                break;
            case "Square":
                int a = sizeOfInclusions /(int)(2*Math.sqrt(2));
                listOfInclusions.forEach(s-> graphics2D.fillRect(s.x - a,s.y - a,2*a,2*a));
        }
    }

    private boolean isPointInRangeAnotherInclusion(Point point, int sizeOfInclusions, List<Point> listOfInclusions){
        for (Point inclusion : listOfInclusions) {
            if (Math.abs(Math.sqrt(Math.pow(point.x-inclusion.x,2) + Math.pow(point.y-inclusion.y,2))) <= 2*sizeOfInclusions) return true; //todo do distance for square
        }
        return false;
    }

    boolean willBeFill(int percentChanceToFill) {
        Random random = new Random();
        int result = random.nextInt(100);
        logger.debug("The drawn digit: " + result);
        return result < percentChanceToFill;
    }

    public static void printInformationAboutColor(int number){
        String hexColor = String.format("#%06X", (0xFFFFFF & number));
        System.out.println(number + " to hex " + hexColor);
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

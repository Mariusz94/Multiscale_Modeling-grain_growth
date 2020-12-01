package pl.mariuszlyszczarz;

import javafx.scene.control.CheckBox;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GrainBoundaryShapeControlMethod extends GrainGrowthModel {
    @Override
    public BufferedImage implementationMethod(BufferedImage bufferedImage, CheckBox periodicCheckBox, int percentChanceToFill) {
        BufferedImage bufferedImageNew = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {

                //prerequisite
                if (bufferedImage.getRGB(x, y) == IMAGE_BACKGROUND_COLOR) {
                    //Rule 1
                    Map<Integer, Integer> neighborsMap = new HashMap<>();
                    for (int y2 = -1; y2 < 2; y2++) {
                        for (int x2 = -1; x2 < 2; x2++) {
                            int tempX = x + x2;
                            int tempY = y + y2;

                            addNeighborsToMap(bufferedImage, periodicCheckBox, neighborsMap, tempX, tempY);
                        }
                    }

                    boolean isFound = true;
                    if (neighborsMap.size() != 0) {
                        isFound = false;
                        for (Map.Entry<Integer, Integer> entry : neighborsMap.entrySet()) {
                            if (entry.getValue() >= 5) {
                                bufferedImageNew.setRGB(x, y, entry.getKey());
                                bufferedImageNew.setRGB(x, y, Color.RED.getRGB());
                                isFound = true;
                                break;
                            }
                        }
                    }
                    //End Rule 1
                    //Rule 2
                    if (!isFound) {
                        isFound = false;
                        neighborsMap.clear();

                        for (int y2 = -1; y2 < 2; y2++) {
                            for (int x2 = -1; x2 < 2; x2++) {
                                if (y2 == -1 && x2 == -1) continue;
                                if (y2 == -1 && x2 == 1) continue;
                                if (y2 == 0 && x2 == 0) continue;
                                if (y2 == 1 && x2 == -1) continue;
                                if (y2 == 1 && x2 == 1) continue;

                                int tempX = x + x2;
                                int tempY = y + y2;

                                addNeighborsToMap(bufferedImage, periodicCheckBox, neighborsMap, tempX, tempY);
                            }
                        }
                    }
                    if (neighborsMap.size() != 0) {
                        for (Map.Entry<Integer, Integer> entry : neighborsMap.entrySet()) {
                            if (entry.getValue() >= 3) {
                                bufferedImageNew.setRGB(x, y, entry.getKey());
                                isFound = true;
                                break;
                            }
                        }
                    }
                    //End Role 2
                    //Role 3
                    if (!isFound) {
                        isFound = false;
                        neighborsMap.clear();

                        for (int y2 = -1; y2 < 2; y2++) {
                            for (int x2 = -1; x2 < 2; x2++) {
                                if (y2 == -1 && x2 == 0) continue;
                                if (y2 == 0 && x2 == -1) continue;
                                if (y2 == 0 && x2 == 1) continue;
                                if (y2 == 1 && x2 == 0) continue;

                                int tempX = x + x2;
                                int tempY = y + y2;

                                addNeighborsToMap(bufferedImage, periodicCheckBox, neighborsMap, tempX, tempY);
                            }
                        }
                    }
                    if (neighborsMap.size() != 0) {
                        for (Map.Entry<Integer, Integer> entry : neighborsMap.entrySet()) {
                            if (entry.getValue() >= 3) {
                                bufferedImageNew.setRGB(x, y, entry.getKey());
                                isFound = true;
                                break;
                            }
                        }
                    }
                    //End Role 3
                    //Role 4
                    if (!isFound) {
                        if (willBeFill(percentChanceToFill)) {
                            isFound = false;
                            neighborsMap.clear();

                            for (int y2 = -1; y2 < 2; y2++) {
                                for (int x2 = -1; x2 < 2; x2++) {

                                    int tempX = x + x2;
                                    int tempY = y + y2;

                                    addNeighborsToMap(bufferedImage, periodicCheckBox, neighborsMap, tempX, tempY);
                                }
                            }

                            if (neighborsMap.size() != 0) {
                                int mostCommonValue = 0; // todo random color if are the same
                                int counter = 0;
                                for (Map.Entry<Integer, Integer> entry : neighborsMap.entrySet()) {
                                    if (entry.getValue() > counter) {
                                        counter = entry.getValue();
                                        mostCommonValue = entry.getKey();
                                    }
                                }
                                bufferedImageNew.setRGB(x, y, mostCommonValue);
                            } else bufferedImageNew.setRGB(x, y, IMAGE_BACKGROUND_COLOR);

                        }

                    }
                    //End Role 4

                    if (bufferedImageNew.getRGB(x,y) == IMAGE_INCLUSIONS_COLOR) bufferedImageNew.setRGB(x, y, IMAGE_BACKGROUND_COLOR);

                } else {
                    bufferedImageNew.setRGB(x, y, bufferedImage.getRGB(x, y));
                }
            }
        }
        return bufferedImageNew;
    }

    private void addNeighborsToMap(BufferedImage bufferedImage, CheckBox periodicCheckBox, Map<Integer, Integer> neighborsMap, int tempX, int tempY) {
        if (periodicCheckBox.isSelected()) {

            if (tempX < 0) {
                tempX = bufferedImage.getWidth() - 1;
            } else if (tempX > bufferedImage.getWidth() - 1) tempX = 0;
            if (tempY < 0) {
                tempY = bufferedImage.getHeight() - 1;
            } else if (tempY > bufferedImage.getHeight() - 1) tempY = 0;
            int value = bufferedImage.getRGB(tempX, tempY);

            if (value != IMAGE_BACKGROUND_COLOR && value != IMAGE_INCLUSIONS_COLOR) {
                if (neighborsMap.containsKey(value)) {
                    neighborsMap.put(value, neighborsMap.get(value) + 1);
                } else {
                    neighborsMap.put(value, 1);
                }
            }
            return;
        }

        if (!periodicCheckBox.isSelected() && (tempX >= 0 && tempX < bufferedImage.getWidth() && tempY >= 0 && tempY < bufferedImage.getHeight())) {
            int value = bufferedImage.getRGB(tempX, tempY);
            if (value != IMAGE_BACKGROUND_COLOR && value != IMAGE_INCLUSIONS_COLOR) {
                if (neighborsMap.containsKey(value)) {
                    neighborsMap.put(value, neighborsMap.get(value) + 1);
                } else {
                    neighborsMap.put(value, 1);
                }
            }
        }
    }
}

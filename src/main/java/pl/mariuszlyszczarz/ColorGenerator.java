package pl.mariuszlyszczarz;

import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ColorGenerator {
    private static Map<Integer, Integer> mapOfColor;

    private static Color generateRandomColor() {
        Random random = new Random();
        float r = random.nextFloat();
        float g = random.nextFloat();
        float b = random.nextFloat();
        Color randomColor = new Color(r, g, b);
        return randomColor;
    }

    private static void generateManyColor() {
        mapOfColor = new HashMap<>();
        for (int i = 0; i < 100000; i++) {
            mapOfColor.put(i, generateRandomColor().getRGB());
        }
    }

    private void saveMapToFile() throws IOException {
        FileOutputStream fileOut = new FileOutputStream("./src/main/resources/mapOfColor.bin");
        ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
        objectOut.writeObject(mapOfColor);
        objectOut.close();
    }

    private Map<Integer,Integer> readMap() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("./src/main/resources/mapOfColor.bin");
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Map<Integer,Integer> map = (Map<Integer, Integer>) objectInputStream.readObject();
        objectInputStream.close();
        return map;
    }

    private static void printMap(Map<Integer,Integer> map){
        map.entrySet().forEach(s-> System.out.println(s.getKey() + " " + s.getValue()));
    }

    public static void main(String[] args) {
        generateManyColor();
        ColorGenerator colorGenerator = new ColorGenerator();
        try {
            colorGenerator.saveMapToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<Integer,Integer> readMap = null;
        try {
            readMap = colorGenerator.readMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        printMap(readMap);
    }


}

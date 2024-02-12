package main.java.org.seamcat.model.eventprocessing;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class P530V18DigitalMaps {
    public static double getLogK(double lon, double lat) {
        double logK;
        double[] tLat = new double[] {lat};
        double[] tLon = new double[] {lon};

        InputStream resourceStream_LogK = P530V18DigitalMaps.class.getResourceAsStream("LogK.csv");
        InputStream resourceStream_lon = P530V18DigitalMaps.class.getResourceAsStream("LongitudeQuarterDegreeS.csv");
        InputStream resourceStream_lat = P530V18DigitalMaps.class.getResourceAsStream("LatitudeQuarterDegreeS.csv");

        double[][] tableLogK = readCSVFile(resourceStream_LogK);
        double[][] tableLon = readCSVFile(resourceStream_lon);
        double[][] tableLat = readCSVFile(resourceStream_lat);

        double[] doubles = BilinearInterpolation.interp2(tableLon[0], tableLat[0], tableLogK, tLon, tLat);
        logK = doubles[0];

        return logK;
    }

    public static double getdN75(double lon, double lat) {
        double dN75;
        double[] tLat = new double[] {lat};
        double[] tLon = new double[] {lon};

        InputStream resourceStream_dN75 = P530V18DigitalMaps.class.getResourceAsStream("dN75.csv");
        InputStream resourceStream_lon = P530V18DigitalMaps.class.getResourceAsStream("LongitudeQuarterDegreeS.csv");
        InputStream resourceStream_lat = P530V18DigitalMaps.class.getResourceAsStream("LatitudeQuarterDegreeS.csv");

        double[][] table_dN75 = readCSVFile(resourceStream_dN75);
        double[][] tableLon = readCSVFile(resourceStream_lon);
        double[][] tableLat = readCSVFile(resourceStream_lat);

        double[] doubles = BilinearInterpolation.interp2(tableLon[0], tableLat[0], table_dN75, tLon, tLat);
        dN75 = doubles[0];

        return dN75;
    }

    public static double[][] readCSVFile(InputStream input) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
            String line;

            // Read all lines from the file and store them in a list
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Determine the number of rows and columns in the CSV file
        int rowCount = lines.size();
        int colCount = lines.get(0).split(",").length;

        // Create a 2D array to store the values
        double[][] data = new double[rowCount][colCount];

        // Populate the 2D array with the parsed double values
        for (int i = 0; i < rowCount; i++) {
            String[] values = lines.get(i).split(",");
            for (int j = 0; j < colCount; j++) {
                data[i][j] = Double.parseDouble(values[j]);
            }
        }

        return data;
    }


    public static void main(String[] args) {
        // Replace "file_path.csv" with the actual path of your CSV file

        double a= getLogK(15.5, 45.5);
        double b= getdN75(15.5, 45.5);

        System.out.println("LogK: " + a + " dN75: " + b);

        InputStream resourceStream_lon = P530V18DigitalMaps.class.getResourceAsStream("LongitudeQuarterDegreeS.csv");
        double[][] values = readCSVFile(resourceStream_lon);


        if (values != null) {
            // Display the values in the 2D array
            for (double[] row : values) {
                for (double value : row) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("Failed to read the CSV file.");
        }
    }

}


package main.java.org.seamcat.model.eventprocessing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculatePDF;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculateBinValues;

public class Test_FDP_Annex1 {

    DemoEPP_16_FDP eEPP_FDP;

    @Before
    public void setup() { eEPP_FDP = new DemoEPP_16_FDP();}

    @Test
    public void test_FDP_S7() {
        //Setup
        double [] lon = new double[] {15.5, 15.5};
        double [] lat = new double[] {45., 45.};
        double [] he = new double[] {20., 20.};
        double [] hr = new double[] {20., 20.};
        double [] ht = new double[] {0., 0.};
        double [] f = new double[] {6., 6.};
        double [] d = new double[] {30., 30.};
        double [] FM = new double[] {10., 10. };
        double [] VLRNoise = new double[] {-94., -94.0};
        int [] NoBins = new int[] {10, 1000};
        boolean ATPC = false;

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("src/main/java/org/seamcat/model/eventprocessing/iRSS_S7.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result = new HashMap<>();
        Map<String, Double[]> expectedResult = new HashMap<String, Double[]>();
        expectedResult.put("FDP", new Double[] {3.156, 44.417});
        expectedResult.put("FDP_LT", new Double[] {3.156, 31.836});
        expectedResult.put("FDP_ST", new Double[] {0., 12.581});
        expectedResult.put("P00x100", new Double[] {0.64232, 0.64232});
        expectedResult.put("P0ix100", new Double[] {0.662, 0.9276});
        expectedResult.put("P0i_STx100", new Double[] {0.6423, 0.7231});
        expectedResult.put("P0i_LTx100", new Double[] {0.6625, 0.8478});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.095});
        expectedResult.put("IN_ST dB", new Double[] {9.542, 9.542});

        // getting I/N (Z) of VSL in dB
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);

            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);
            System.out.println(result);

            // assert
            Assert.assertEquals(expectedResult.get("FDP")[i], result.get("FDP"), 1e-3);
            Assert.assertEquals(expectedResult.get("FDP_LT")[i], result.get("FDP_LT"), 1e-3);
            Assert.assertEquals(expectedResult.get("FDP_ST")[i], result.get("FDP_ST"), 1e-3);
            Assert.assertEquals(expectedResult.get("P00x100")[i], result.get("P00")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("P0ix100")[i], result.get("P0I")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("P0i_LTx100")[i], result.get("P0I_LT")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("P0i_STx100")[i], result.get("P0I_ST")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("Gammax100")[i], result.get("Gamma")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("IN_ST dB")[i], result.get("IN_ST"), 1e-3);
        }

    }
    // Helper method to read iRSS File
    public static double[] parseCSV(String filePath) throws IOException {
        List<Double> values = new ArrayList<>();
        int numerator=0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Assuming each line contains a single double value separated by comma
                numerator = numerator + 1;
                String[] tokens = line.split(",");
                for (String token : tokens) {
                    try {
                        double value = Double.parseDouble(token.trim());
                        values.add(value);
                    } catch (NumberFormatException e) {
                        // Handle non-numeric values if necessary
                        System.err.println("Warning: Non-numeric value found in the CSV file for value" + token + " in line  " + numerator);
                    }
                }
            }
        }
        // Convert List<Double> to double[]
        double[] result = new double[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }
}

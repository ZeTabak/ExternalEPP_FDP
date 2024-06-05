package Tests;

import main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP;
import main.java.org.seamcat.model.eventprocessing.P530v18MultipathFading;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.seamcat.model.mathematics.Mathematics;

import java.io.*;
import java.util.*;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculatePDF;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculateBinValues;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

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
            iRSS_vect = parseCSV("iRSS_S7.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {9.769, 45.246});
        expectedResult.put("FDP_LT", new Double[] {9.769, 31.893});
        expectedResult.put("FDP_ST", new Double[] {0., 13.3528});
        expectedResult.put("P00x100", new Double[] {0.64232, 0.64232});
        expectedResult.put("P0ix100", new Double[] {0.70507, 0.93295});
        expectedResult.put("P0i_STx100", new Double[] {0.6423, 0.72809});
        expectedResult.put("P0i_LTx100", new Double[] {0.70507, 0.84718});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.0999});
        expectedResult.put("IN_ST dB", new Double[] {9.542, 9.542});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");

            // assert
            doFDPAssert(i, expectedResult, result);
        }
    }

    @Test
    public void test_FDP_S1() {
        //Setup
        double [] lon = new double[] {15.0, 15.7};
        double [] lat = new double[] {45., 45.33};
        double [] he = new double[] {30., 20.};
        double [] hr = new double[] {1.5, 20.};
        double [] ht = new double[] {0., 5.};
        double [] f = new double[] {0.9, 12.};
        double [] d = new double[] {40., 15.};
        double [] FM = new double[] {15., 10. };
        double [] VLRNoise = new double[] {-94., -94.0};
        int [] NoBins = new int[] {1000, 1000};
        boolean ATPC = false;

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("iRSS_S1.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {12.496, 37.6495});
        expectedResult.put("FDP_LT", new Double[] {4.6318, 10.7958});
        expectedResult.put("FDP_ST", new Double[] {7.8644, 26.853});
        expectedResult.put("P00x100", new Double[] {0.14227, 0.14438});
        expectedResult.put("P0ix100", new Double[] {0.16005, 0.198739});
        expectedResult.put("P0i_STx100", new Double[] {0.15346, 0.18315});
        expectedResult.put("P0i_LTx100", new Double[] {0.14886, 0.159967});
        expectedResult.put("Gammax100", new Double[] {1.15E-2, 3.95E-2});
        expectedResult.put("IN_ST dB", new Double[] {14.86, 9.542});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");

            // assert
            doFDPAssert(i, expectedResult, result);
        }
    }

    @Test
    public void test_FDP_Constant() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {15., 20., 25., 30., 35., 40.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000};
        boolean ATPC = false;

        double [] FDP_calc = new double[FM.length];
        double [] FDP_calc_LT = new double[FM.length];
        double [] FDP_calc_ST = new double[FM.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Constant.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {4.230835519913456, 6.559872355258345, 8.452604489921466, 9.929106705254643, 9.929106705254775, 9.929106705254753});
        expectedResult.put("FDP_LT", new Double[] {4.230835519913456, 6.559872355258345, 8.452604489921466, 9.929106705254643, 9.929106705254775, 9.929106705254753});
        expectedResult.put("FDP_ST", new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        /*
        expectedResult.put("P00x100", new Double[] {0.64232, 0.64232});
        expectedResult.put("P0ix100", new Double[] {0.662, 0.9276});
        expectedResult.put("P0i_STx100", new Double[] {0.6423, 0.7231});
        expectedResult.put("P0i_LTx100", new Double[] {0.6625, 0.8467});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.095});
        expectedResult.put("IN_ST dB", new Double[] {9.542, 9.542});
         */

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            FDP_calc[i] = result.get("FDP");
            FDP_calc_LT[i] = result.get("FDP_LT");
            FDP_calc_ST[i] = result.get("FDP_ST");

            /*
            // Printout of detailed results
            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");
             */

            // assert
            //doFDPAssert(i, expectedResult, result);
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Cunstant - No ATPC");
        System.out.println("FM (dB = " + Arrays.toString(FM));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    @Test
    public void test_FDP_Triangular() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {15., 20., 25., 30., 35., 40.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000};
        boolean ATPC = false;

        double [] FDP_calc = new double[FM.length];
        double [] FDP_calc_LT = new double[FM.length];
        double [] FDP_calc_ST = new double[FM.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Triangular.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {0.6727867108528462, 1.0285910878509386, 1.3120241023400592, 1.5260406473390775, 1.5260406473387889, 1.5260406473390553});
        expectedResult.put("FDP_LT", new Double[] {0.6727867108528462, 1.0285910878509386, 1.3120241023400592, 1.5260406473390775, 1.5260406473387889, 1.5260406473390553});
        expectedResult.put("FDP_ST", new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        /*
        expectedResult.put("P00x100", new Double[] {100.0, 0.64232});
        expectedResult.put("P0ix100", new Double[] {99.999, 0.9276});
        expectedResult.put("P0i_STx100", new Double[] {100., 0.7231});
        expectedResult.put("P0i_LTx100", new Double[] {99.999, 0.8467});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.095});
        expectedResult.put("IN_ST dB", new Double[] {24.986, 9.542});
        */

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < FM.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);

            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            FDP_calc[i] = result.get("FDP");
            FDP_calc_LT[i] = result.get("FDP_LT");
            FDP_calc_ST[i] = result.get("FDP_ST");

            /*
            // Printout of detailed results
            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");
             */

            // assert
            //doFDPAssert(i, expectedResult, result);
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Triangular - No ATPC");
        System.out.println("FM (dB = " + Arrays.toString(FM));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    @Test
    public void test_FDP_Uniform() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {15., 20., 25., 30., 35., 40.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000};
        boolean ATPC = false;

        double [] FDP_calc = new double[FM.length];
        double [] FDP_calc_LT = new double[FM.length];
        double [] FDP_calc_ST = new double[FM.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Uniform.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {5.21458814136051, 8.581017673426473, 11.59400350657851, 14.145487582905968, 14.344770702978105, 14.344770702978305});
        expectedResult.put("FDP_LT", new Double[] {5.21458814136051, 8.581017673426473, 11.59400350657851, 14.145487582905968, 14.344770702978105, 14.344770702978305});
        expectedResult.put("FDP_ST", new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        /*
        expectedResult.put("P00x100", new Double[] {100.0, 0.64232});
        expectedResult.put("P0ix100", new Double[] {99.999, 0.9276});
        expectedResult.put("P0i_STx100", new Double[] {100., 0.7231});
        expectedResult.put("P0i_LTx100", new Double[] {99.999, 0.8467});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.095});
        expectedResult.put("IN_ST dB", new Double[] {24.986, 9.542});
         */

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            FDP_calc[i] = result.get("FDP");
            FDP_calc_LT[i] = result.get("FDP_LT");
            FDP_calc_ST[i] = result.get("FDP_ST");
            /*
            // Printout of detailed results
            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");
             */

            // assert
            //doFDPAssert(i, expectedResult, result);
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Uniform - No ATPC");
        System.out.println("FM (dB = " + Arrays.toString(FM));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    @Test
    public void test_FDP_Gaussian() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {15., 20., 25., 30., 35., 40.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000};
        boolean ATPC = false;

        double [] FDP_calc = new double[FM.length];
        double [] FDP_calc_LT = new double[FM.length];
        double [] FDP_calc_ST = new double[FM.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Gaussian.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {14.823102885042161, 21.328614310201054, 34.283750998856235, 57.076536612103524, 94.35010582021026, 141.24777486946644});
        expectedResult.put("FDP_LT", new Double[] {8.409678213047833, 16.44027302674791, 30.323565642309912, 52.54776812937971, 79.94239131281135, 114.93193074412136});
        expectedResult.put("FDP_ST", new Double[] {6.413424671995016, 4.888341283453301, 3.9601853565463196, 4.528768482723788, 14.407714507398861, 26.315844125345023});
        /*
        expectedResult.put("P00x100", new Double[] {100.0, 0.64232});
        expectedResult.put("P0ix100", new Double[] {99.999, 0.9276});
        expectedResult.put("P0i_STx100", new Double[] {100., 0.7231});
        expectedResult.put("P0i_LTx100", new Double[] {99.999, 0.8467});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.095});
        expectedResult.put("IN_ST dB", new Double[] {24.986, 9.542});
         */

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, 0, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            FDP_calc[i] = result.get("FDP");
            FDP_calc_LT[i] = result.get("FDP_LT");
            FDP_calc_ST[i] = result.get("FDP_ST");

            //
            // Printout of detailed results
            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");
             //

            // assert
            //doFDPAssert(i, expectedResult, result);
            //doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Gaussian - No ATPC");
        System.out.println("FM (dB = " + Arrays.toString(FM));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    // Helper method for FDP all results Assertion
    protected static void doFDPAssert(int i, Map<String, Double[]> expectedResult, Map<String, Double> result) {
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

    protected static void doFDPAssertShort(int i, Map<String, Double[]> expectedResult, Map<String, Double> result) {
        Assert.assertEquals(expectedResult.get("FDP")[i], result.get("FDP"), 1e-3);
        Assert.assertEquals(expectedResult.get("FDP_LT")[i], result.get("FDP_LT"), 1e-3);
        Assert.assertEquals(expectedResult.get("FDP_ST")[i], result.get("FDP_ST"), 1e-3);
    }

    // Helper method to read iRSS csv File
    public static double[] parseCSV(String filePath) throws IOException {
        List<Double> values = new ArrayList<>();
        int numerator=0;
        InputStream resourceStream = Test_FDP_Annex1.class.getResourceAsStream(filePath);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceStream))) {
            String line;

            br.mark(3);
            int firstChar = br.read();
            if (firstChar != 0xFEFF) { // Check if the first character is the BOM
                br.reset(); // Not BOM, reset to the beginning
            }

            while ((line = br.readLine()) != null) {
                // Assuming each line contains a single double value separated by comma
                numerator = numerator + 1;
                /*
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
                 */
                    try {
                        double value = Double.parseDouble(line.trim());
                        values.add(value);
                    } catch (NumberFormatException e) {
                        // Handle non-numeric values if necessary
                        System.err.println("Warning: Non-numeric value found in the CSV file for value" + line + " in line  " + numerator);
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

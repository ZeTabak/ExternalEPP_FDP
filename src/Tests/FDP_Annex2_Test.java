package Tests;

import main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP;
import main.java.org.seamcat.model.eventprocessing.P530v18MultipathFading;
import org.junit.Test;
import org.junit.Before;

import java.io.IOException;
import java.util.*;

import static Tests.Test_FDP_Annex1.doFDPAssertShort;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.*;
import static Tests.Test_FDP_Annex1.parseCSV;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

public class FDP_Annex2_Test {
    DemoEPP_16_FDP eEPP_FDP;

    @Before
    public void setup() { eEPP_FDP = new DemoEPP_16_FDP();}

    @Test
    public void test_FDP_AS5() {
        //Setup
        double [] lon = new double[] {15.0, 15.3};
        double [] lat = new double[] {45., 45.7};
        double [] he = new double[] {40., 40.};
        double [] hr = new double[] {20., 1.5};
        double [] ht = new double[] {0., 5.};
        double [] f = new double[] {6., 6.};
        double [] d = new double[] {45., 35.};
        double [] FM = new double[] {35., 30. };
        boolean ATPC = true;
        double atpcRange = 20;
        double [] VLRNoise = new double[] {-94., -94.0};
        int [] NoBins = new int[] {1000, 1000};

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("iRSS_all_WS_AS5.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        Map<String, Double[]> expectedResult = new HashMap<>();
        /* before Integrate change
        expectedResult.put("FDP", new Double[] {20.687, 25.053});
        expectedResult.put("FDP_LT", new Double[] {0.7245, 0.72030});
        expectedResult.put("FDP_ST", new Double[] {19.9628, 24.333});
        expectedResult.put("P00x100", new Double[] {0.00501, 4.1093e-3});
        expectedResult.put("P0ix100", new Double[] {0.00604, 5.14025e-3});
        expectedResult.put("P0i_STx100", new Double[] {0.00601, 5.10945e-3});
        expectedResult.put("P0i_LTx100", new Double[] {0.00505, 4.1402e-3});
        expectedResult.put("Gammax100", new Double[] {0.001, 1e-5});
        expectedResult.put("IN_ST dB", new Double[] {14.86, 9.542});
        */
        expectedResult.put("FDP", new Double[] {40.652, 23.2861});
        expectedResult.put("FDP_LT", new Double[] {0.7265, 0.72059});
        expectedResult.put("FDP_ST", new Double[] {39.9257, 22.56559});
        expectedResult.put("P00x100", new Double[] {0.00509, 8.86226e-3});
        expectedResult.put("P0ix100", new Double[] {0.007045, 1.0925e-2});
        expectedResult.put("P0i_STx100", new Double[] {0.007008, 1.08620e-2});
        expectedResult.put("P0i_LTx100", new Double[] {0.005045, 8.92612e-3});
        expectedResult.put("Gammax100", new Double[] {0.002, 2e-3});
        expectedResult.put("IN_ST dB", new Double[] {14.86, 9.5424});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        /*
        double[] i_n_lin= new double[I_N.length];
        for (int i = 0; i < I_N.length; i++) {
            i_n_lin[i]=Mathematics.dB2Linear(I_N[i]);
        }

        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, NoBins[0]);
        double[] I_N_bins = calculateBinValues(I_N, NoBins[0]);

        double[] pdf_i_n = calculatePDF(i_n_lin, NoBins[0]);
        double[] i_n_bins = calculateBinValues(i_n_lin, NoBins[0]);


        double[] I_N_bins= new double[i_n_bins.length];

        for (int i = 0; i < i_n_bins.length; i++) {
            I_N_bins[i]=Mathematics.linear2dB(i_n_bins[i]);
        }

        double sumLin = integrate(i_n_bins, pdf_i_n, i_n_bins[0], i_n_bins[NoBins[0] - 1]);
        double sumdB = integrate(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[0] - 1]);

        // Map<String, Double> result = new HashMap<String, Double>();
        */

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, atpcRange, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");

            // assert
            Test_FDP_Annex1.doFDPAssert(i, expectedResult, result);
        }
    }

    @Test
    public void test_FDP_AS6() {
        //Setup
        double [] lon = new double[] {15.0, 15.0};
        double [] lat = new double[] {45., 45.};
        double [] he = new double[] {40., 40.};
        double [] hr = new double[] {10., 10.};
        double [] ht = new double[] {0., 0.};
        double [] f = new double[] {6., 6.};
        double [] d = new double[] {30., 60.};
        double [] FM = new double[] {25., 20. };
        boolean ATPC = true;
        double atpcRange = 14;
        double [] VLRNoise = new double[] {-94., -94.0};
        int [] NoBins = new int[] {1000, 1000};

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("iRSS_all_WS_AS6.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        Map<String, Double[]> expectedResult = new HashMap<>();
         /* before Integrate change
        expectedResult.put("FDP", new Double[] {19.396, 21.145});
        expectedResult.put("FDP_LT", new Double[] {8.872, 7.653});
        expectedResult.put("FDP_ST", new Double[] {10.524, 13.491});
        expectedResult.put("P00x100", new Double[] {0.00950, 0.376});
        expectedResult.put("P0ix100", new Double[] {0.0113, 0.456});
        expectedResult.put("P0i_STx100", new Double[] {0.0105, 0.42739});
        expectedResult.put("P0i_LTx100", new Double[] {0.01034, 0.4054});
        expectedResult.put("Gammax100", new Double[] {0.001, 5.09999E-2});
        expectedResult.put("IN_ST dB", new Double[] {10.64, 4.7437});
        */

        expectedResult.put("FDP", new Double[] {29.9225, 21.41167});
        expectedResult.put("FDP_LT", new Double[] {8.8740, 7.6555});
        expectedResult.put("FDP_ST", new Double[] {21.04846, 13.75613});
        expectedResult.put("P00x100", new Double[] {9.50097e-3, 0.376});
        expectedResult.put("P0ix100", new Double[] {1.23439e-2, 0.4572});
        expectedResult.put("P0i_STx100", new Double[] {1.150078e-2, 0.42839});
        expectedResult.put("P0i_LTx100", new Double[] {1.0344e-2, 0.4054});
        expectedResult.put("Gammax100", new Double[] {0.002, 5.19999E-2});
        expectedResult.put("IN_ST dB", new Double[] {10.64, 4.7437});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, atpcRange, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

            System.out.println("Results iteration " + i + ":");
            for (Map.Entry<String, Double> entry : result.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("________________________________");

            // assert
            Test_FDP_Annex1.doFDPAssert(i, expectedResult, result);
        }
    }

    @Test
    public void test_FDP_Constant_ATPC() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761 , 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {25., 25., 25.,25., 25., 25.,25., 25., 25., 25., 25.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000};
        double [] ATPC_range = new double[] {15.0, 15.5, 16.0, 16.5, 17.0, 17.5, 18.0, 18.5, 19.0, 19.5, 20.0};
        boolean ATPC = true;

        double [] FDP_calc = new double[ATPC_range.length];
        double [] FDP_calc_LT = new double[ATPC_range.length];
        double [] FDP_calc_ST = new double[ATPC_range.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Constant.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466});
        expectedResult.put("FDP_LT", new Double[] {8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466, 8.452604489921466});
        expectedResult.put("FDP_ST", new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, ATPC_range[i], NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

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
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Cunstant - ATPC");
        System.out.println("ATPC_Range (dB) = " + Arrays.toString(ATPC_range));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    @Test
    public void test_FDP_Triangular_ATPC() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761 , 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {25., 25., 25.,25., 25., 25.,25., 25., 25., 25., 25.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000};
        double [] ATPC_range = new double[] {15.0, 15.5, 16.0, 16.5, 17.0, 17.5, 18.0, 18.5, 19.0, 19.5, 20.0};
        boolean ATPC = true;

        double [] FDP_calc = new double[ATPC_range.length];
        double [] FDP_calc_LT = new double[ATPC_range.length];
        double [] FDP_calc_ST = new double[ATPC_range.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Triangular.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592});
        expectedResult.put("FDP_LT", new Double[] {1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592, 1.3120241023400592});
        expectedResult.put("FDP_ST", new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, ATPC_range[i], NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

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
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Triangular - ATPC");
        System.out.println("ATPC_Range (dB) = " + Arrays.toString(ATPC_range));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    @Test
    public void test_FDP_Uniform_ATPC() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761 , 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {25., 25., 25.,25., 25., 25.,25., 25., 25., 25., 25.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000};
        double [] ATPC_range = new double[] {15.0, 15.5, 16.0, 16.5, 17.0, 17.5, 18.0, 18.5, 19.0, 19.5, 20.0};
        boolean ATPC = true;

        double [] FDP_calc = new double[ATPC_range.length];
        double [] FDP_calc_LT = new double[ATPC_range.length];
        double [] FDP_calc_ST = new double[ATPC_range.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Uniform.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851});
        expectedResult.put("FDP_LT", new Double[] {11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851, 11.59400350657851});
        expectedResult.put("FDP_ST", new Double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, ATPC_range[i], NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

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
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Uniform - ATPC");
        System.out.println("ATPC_Range (dB) = " + Arrays.toString(ATPC_range));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }

    @Test
    public void test_FDP_Gaussian_ATPC() {
        //Setup
        double [] lon = new double[] {12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683, 12.5683};
        double [] lat = new double[] {55.6761, 55.6761, 55.6761, 55.6761, 55.6761, 55.6761 , 55.6761, 55.6761, 55.6761, 55.6761, 55.6761};
        double [] he = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] hr = new double[] {20., 20., 20., 20., 20.,20., 20., 20., 20., 20.,20.};
        double [] ht = new double[] {0., 0., 0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double [] f = new double[] {6., 6., 6., 6., 6., 6., 6., 6., 6., 6., 6.};
        double [] d = new double[] {60., 60., 60., 60., 60., 60., 60., 60., 60., 60., 60.};
        double [] FM = new double[] {25., 25., 25.,25., 25., 25.,25., 25., 25., 25., 25.};
        double [] VLRNoise = new double[] {-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691, -89.9691, -89.9691, -89.9691,-89.9691,-89.9691};
        int [] NoBins = new int[] {10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000, 10000};
        double [] ATPC_range = new double[] {15.0, 15.5, 16.0, 16.5, 17.0, 17.5, 18.0, 18.5, 19.0, 19.5, 20.0};
        boolean ATPC = true;

        double [] FDP_calc = new double[ATPC_range.length];
        double [] FDP_calc_LT = new double[ATPC_range.length];
        double [] FDP_calc_ST = new double[ATPC_range.length];
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Gaussian.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result;
        // Expected results
        Map<String, Double[]> expectedResult = new HashMap<>();
        expectedResult.put("FDP", new Double[] {101.24567422001167, 109.62586890027194, 120.48049823641149, 130.36969051209056, 139.9807766778897, 152.6428037271236, 165.02801988363794, 176.78633207946177, 195.76996853938147, 210.00832505068385, 230.12792484645365});
        expectedResult.put("FDP_LT", new Double[] {17.36633618826047, 16.489576896547796, 15.442408268722541, 14.563307148433037, 13.783934774263184, 12.843907007538636, 12.004903204092265, 11.294665151953076, 10.253054243931858, 9.566933943278766, 8.716699231111713});
        expectedResult.put("FDP_ST", new Double[] {83.87933803175119, 93.13629200372414, 105.03808996768895, 115.80638336365752, 126.19684190362652, 139.79889671958497, 153.02311667954567, 165.4916669275087, 185.5169142954496, 200.44139110740508, 221.41122561534195});

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < lon.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            // Execution
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, ATPC_range[i], NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);

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
            doFDPAssertShort(i, expectedResult, result);
        }
        System.out.println("Test Gaussian - ATPC");
        System.out.println("ATPC_Range (dB) = " + Arrays.toString(ATPC_range));
        System.out.println("FDP (%) = " + Arrays.toString(FDP_calc));
        System.out.println("FDP_LT (%) = " + Arrays.toString(FDP_calc_LT));
        System.out.println("FDP_ST (%) = " + Arrays.toString(FDP_calc_ST));
        System.out.println("________________________________");
        System.out.println();
    }
}


package main.java.org.seamcat.model.eventprocessing;

import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.seamcat.model.mathematics.Mathematics;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.io.FileReader;

import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.*;
import static main.java.org.seamcat.model.eventprocessing.Test_FDP_Annex1.parseCSV;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

public class Test_FDP_Annex2 {
    DemoEPP_16_FDP eEPP_FDP;

    @Before
    public void setup() { eEPP_FDP = new DemoEPP_16_FDP();}

    @Test
    public void test_FDP_AS5() {
        //Setup
        double [] lon = new double[] {15.0, 15.5};
        double [] lat = new double[] {45., 45.};
        double [] he = new double[] {40., 40.};
        double [] hr = new double[] {20., 10.};
        double [] ht = new double[] {0., 5.};
        double [] f = new double[] {6., 6.};
        double [] d = new double[] {45., 30.};
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
        expectedResult.put("FDP", new Double[] {20.687, 25.053});
        expectedResult.put("FDP_LT", new Double[] {0.7245, 0.72030});
        expectedResult.put("FDP_ST", new Double[] {19.9628, 24.333});
        expectedResult.put("P00x100", new Double[] {0.00501, 4.1093e-3});
        expectedResult.put("P0ix100", new Double[] {0.00604, 5.14025e-3});
        expectedResult.put("P0i_STx100", new Double[] {0.00601, 5.10945e-3});
        expectedResult.put("P0i_LTx100", new Double[] {0.00505, 4.1402e-3});
        expectedResult.put("Gammax100", new Double[] {0.001, 1e-5});
        expectedResult.put("IN_ST dB", new Double[] {14.86, 9.542});

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
        expectedResult.put("FDP", new Double[] {19.396, 21.145});
        expectedResult.put("FDP_LT", new Double[] {8.872, 7.653});
        expectedResult.put("FDP_ST", new Double[] {10.524, 13.491});
        expectedResult.put("P00x100", new Double[] {0.00950, 0.376});
        expectedResult.put("P0ix100", new Double[] {0.0113, 0.456});
        expectedResult.put("P0i_STx100", new Double[] {0.0105, 0.42739});
        expectedResult.put("P0i_LTx100", new Double[] {0.01034, 0.4054});
        expectedResult.put("Gammax100", new Double[] {0.001, 5.09999E-2});
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
}


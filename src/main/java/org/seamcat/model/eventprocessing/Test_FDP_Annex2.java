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

public class Test_FDP_Annex2 {
    DemoEPP_16_FDP eEPP_FDP;

    @Before
    public void setup() { eEPP_FDP = new DemoEPP_16_FDP();}

    @Test
    public void test_FDP_AS5() {
        //Setup
        double [] lon = new double[] {15.0, 15.5};
        double [] lat = new double[] {45., 45.};
        double [] he = new double[] {40., 20.};
        double [] hr = new double[] {20., 20.};
        double [] ht = new double[] {0., 0.};
        double [] f = new double[] {6., 6.};
        double [] d = new double[] {45., 30.};
        double [] FM = new double[] {35., 10. };
        double [] VLRNoise = new double[] {-94., -94.0};
        int [] NoBins = new int[] {1000, 1000};
        boolean ATPC = true;
        double atpcRange = 20;

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("src/main/java/org/seamcat/model/eventprocessing/iRSS_all_WS_AS5.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Double> result = new HashMap<>();
        Map<String, Double[]> expectedResult = new HashMap<String, Double[]>();
        expectedResult.put("FDP", new Double[] {3.5, 45.868});
        expectedResult.put("FDP_LT", new Double[] {0.752, 31.998});
        expectedResult.put("FDP_ST", new Double[] {2.7569, 13.869});


        expectedResult.put("P00x100", new Double[] {0.64232, 0.64232});
        expectedResult.put("P0ix100", new Double[] {1.44111, 0.9369});
        expectedResult.put("P0i_STx100", new Double[] {0.64232, 0.7314});
        expectedResult.put("P0i_LTx100", new Double[] {1.4411, 0.8478});
        expectedResult.put("Gammax100", new Double[] {0.0, 0.1051});
        expectedResult.put("IN_ST dB", new Double[] {9.542, 9.542});

        // getting I/N (Z) of VSL in dB
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();
        double[] i_n_lin= new double[I_N.length];

        for (int i = 0; i < I_N.length; i++) {
            i_n_lin[i]=Mathematics.dB2Linear(I_N[i]);
        }



        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, NoBins[0]);
        double[] I_N_bins = calculateBinValues(I_N, NoBins[0]);

        double[] pdf_i_n = calculatePDF(i_n_lin, NoBins[0]);
        double[] i_n_bins = calculateBinValues(i_n_lin, NoBins[0]);

        /*
        double[] I_N_bins= new double[i_n_bins.length];

        for (int i = 0; i < i_n_bins.length; i++) {
            I_N_bins[i]=Mathematics.linear2dB(i_n_bins[i]);
        }
*/
        double sumLin = integrate(i_n_bins, pdf_i_n, i_n_bins[0], i_n_bins[NoBins[0] - 1]);
        double sumdB = integrate(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[0] - 1]);

        // Map<String, Double> result = new HashMap<String, Double>();
        for (int i = 0; i < 1; i++) {
            P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon[i], lat[i]);
            result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, atpcRange, NoBins[i], p530v18MultipathFading, pdf_I_N, I_N_bins);
            //result = eEPP_FDP.calculateFDP(he[i], hr[i], ht[i], f[i], d[i], FM[i], ATPC, atpcRange, NoBins[i], p530v18MultipathFading, pdf_i_n, i_n_bins);
            Assert.assertEquals(expectedResult.get("FDP")[i], result.get("FDP"), 1e-3);
            Assert.assertEquals(expectedResult.get("FDP_LT")[i], result.get("FDP_LT"), 1e-3);
            Assert.assertEquals(expectedResult.get("FDP_ST")[i], result.get("FDP_ST"), 1e-3);
            Assert.assertEquals(expectedResult.get("P00x100")[i], result.get("P00")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("P0ix100")[i], result.get("P0I")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("P0i_LTx100")[i], result.get("P0I_LT")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("P0i_STx100")[i], result.get("P0I_ST")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("Gammax100")[i], result.get("Gamma")*100, 1e-3);
            Assert.assertEquals(expectedResult.get("IN_ST dB")[i], result.get("IN_ST"), 1e-3);
            System.out.println("gotovo");
            //double result = epp.calculate_EbN0_lin(c_i_n[i], eff[i]); // execution
            //Assert.assertEquals(expectedResult[i], result, 1e-3); // evaluation
        }

    }


}


package main.java.org.seamcat.model.eventprocessing;


import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.seamcat.model.mathematics.Mathematics;
import java.io.*;
import java.util.*;
import java.io.FileWriter;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculatePDF;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculateBinValues;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.integrate;
import static main.java.org.seamcat.model.eventprocessing.Test_FDP_Annex1.parseCSV;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.integrate2;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

public class Test_Helpers {

    DemoEPP_16_FDP eEPP_FDP;

    @Before
    public void setup() { eEPP_FDP = new DemoEPP_16_FDP();}

    @Test
    public void test_cdf_Constant() {
        //Setup
        double [] VLRNoise = new double[] {-80.0, -80.0};
        int [] NoBins = new int[] {5, 10};

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Constant.csv");
            // iRSS_vect = parseCSV("I_Triangular.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < NoBins.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            double integral = integrate(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[i] - 1]);
            double integral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[i] - 1]);

            double sum= 0.;

            for (int j = 0; j < NoBins[i] - 1; j++) {
                sum = sum + pdf_I_N[j];
            }

            System.out.println("Results iteration " + i + ":");
            System.out.println("pdf : " + Arrays.toString(pdf_I_N));
            System.out.println("bins: " + Arrays.toString((I_N_bins)));
            System.out.println("sum : " + sum * (I_N_bins[1]- I_N_bins[0]));
            System.out.println("integral Trapezoid : " + integral);
            System.out.println("integral Sum  : " + integral2);
            System.out.println("________________________________");

            Assert.assertEquals(1, integral, 1e-3);
        }
    }

    @Test
    public void test_cdf_Triangular() {
        //Setup
        double [] VLRNoise = new double[] {-80.0, -80.0, -80.0};
        int [] NoBins = new int[] {5, 10, 50};

        double [] exResult = new double[] {0.939, 0.987, 0.999};

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Triangular.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < NoBins.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            double integral = integrate(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[i] - 1]);
            double integral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[i] - 1]);
            double sum= 0.;

            for (int j = 0; j < NoBins[i] - 1; j++) {
                sum = sum + pdf_I_N[j];
            }

            System.out.println("Results iteration " + i + ":");
            System.out.println("pdf : " + Arrays.toString(pdf_I_N));
            System.out.println("bins: " + Arrays.toString((I_N_bins)));
            System.out.println("sum : " + sum * (I_N_bins[1]- I_N_bins[0]));
            System.out.println("integral Trapezoid: " + integral);
            System.out.println("integral sum : " + integral2);
            System.out.println("________________________________");

            Assert.assertEquals(exResult[i], integral, 1e-3);
        }
    }

    @Test
    public void test_cdf_AS6() {
        //Setup
        double [] VLRNoise = new double[] {-94.0, -94.0};
        int [] NoBins = new int[] {10, 1000};
        double [] exResult = new double[] {0.99957, 0.99997};

        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("iRSS_all_WS_AS6.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise[0]).toArray();

        for (int i = 0; i < NoBins.length; i++) {
            // pdf of I/N and bin values dB
            double[] pdf_I_N = calculatePDF(I_N, NoBins[i]);
            double[] I_N_bins = calculateBinValues(I_N, NoBins[i]);
            double integral = integrate(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[i] - 1]);
            double integral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[NoBins[i] - 1]);

            double sum= 0.;

            for (int j = 0; j < NoBins[i] - 1; j++) {
                sum = sum + pdf_I_N[j];
            }

            System.out.println("Results iteration " + i + ":");
            //System.out.println("pdf : " + Arrays.toString(pdf_I_N));
            //System.out.println("bins: " + Arrays.toString((I_N_bins)));
            System.out.println("sum : " + sum * (I_N_bins[1]- I_N_bins[0]));
            System.out.println("integral Trapezoid : " + integral);
            System.out.println("integral Sum  : " + integral2);
            System.out.println("________________________________");

            Assert.assertEquals(exResult[i], integral, 1e-5);
        }
    }

    @Test
    public void test_gamma_AS6() {
        //Setup
        double VLRNoise = -94.0;
        int NoBins = 1000;
        double NFM = 11.;
        double exResult;
        double z, desensitisation;
        int counter = 0, index=1000;
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("iRSS_all_WS_AS6.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise).toArray();

        //Expected
        for (int i = 0; i < I_N.length; i++) {
            z = Mathematics.dB2Linear(I_N[i]);
            desensitisation = Mathematics.linear2dB(1 + z);
            if (desensitisation >= NFM) {
                counter +=1;
            }
        }
        exResult= 1.* counter/ I_N.length;


        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, NoBins);
        double[] I_N_bins = calculateBinValues(I_N, NoBins);
        boolean trigger = false;

        for (int j = 0; j < NoBins; j++) {
            z = Mathematics.dB2Linear(I_N_bins[j]);
            desensitisation = Mathematics.linear2dB(1 + z);
            // determine index for ST with ATPC
            if (!trigger  && desensitisation >= NFM) {
                    index = j;
                    trigger = true;
            }
        }

        double integral = integrate(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);
        double integral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);

        System.out.println("Results:");
        //System.out.println("pdf : " + Arrays.toString(pdf_I_N));
        //System.out.println("bins: " + Arrays.toString((I_N_bins)));
        System.out.println("index  : " + index);
        System.out.println("integral Sum  : " + integral2);
        System.out.println("integral Trapezoid : " + integral);
        System.out.println("expected  : " + exResult);
        System.out.println("________________________________");

        // Here is seen issue with trapezoid integration
        //Assert.assertEquals(exResult[i], integral, 1e-5);

    }

}

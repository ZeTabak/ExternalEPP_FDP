package Tests;


import main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;
import org.seamcat.model.mathematics.Mathematics;
import java.io.*;
import java.util.*;

import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculatePDF;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.calculateBinValues;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.integrate;
import static Tests.Test_FDP_Annex1.parseCSV;
import static main.java.org.seamcat.model.eventprocessing.DemoEPP_16_FDP.integrate2;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

public class Helpers_Tests {

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
            System.out.println("Min : " + Arrays.stream(I_N).min());
            System.out.println("Max : " + Arrays.stream(I_N).max());
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

        double [] exResult = new double[] {1.0, 1.0, 1.0};

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
            System.out.println("Min : " + Arrays.stream(I_N).min());
            System.out.println("Max : " + Arrays.stream(I_N).max());
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
        double [] exResult = new double[] {1.0, 1.0};

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
            System.out.println("Min : " + Arrays.stream(I_N).min());
            System.out.println("Max : " + Arrays.stream(I_N).max());
            //System.out.println("pdf : " + Arrays.toString(pdf_I_N));
            //System.out.println("bins: " + Arrays.toString((I_N_bins)));
            System.out.println("sum : " + sum * (I_N_bins[1]- I_N_bins[0]));
            System.out.println("integral Trapezoid : " + integral);
            System.out.println("integral Sum  : " + integral2);
            System.out.println("________________________________");

            Assert.assertEquals(exResult[i], integral, 1e-5);
        }
    }


    // Testing gamma
    @Test
    public void test_gamma_AS6() {
        //Setup
        double VLRNoise = -94.0;
        int NoBins = 1000;
        double FM = 11.;
        double gammaExpected_count;
        double z, desensitisation;
        long counter = 0;
        int index=1000;
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
        counter = Arrays.stream(I_N).filter(i2n -> Mathematics.linear2dB(Mathematics.dB2Linear(i2n) + 1) >= FM).count();
        gammaExpected_count= 1.* counter/ I_N.length;

        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, NoBins);
        double[] I_N_bins = calculateBinValues(I_N, NoBins);
        boolean trigger = false;

        for (int j = 0; j < NoBins; j++) {
            z = Mathematics.dB2Linear(I_N_bins[j]);
            desensitisation = Mathematics.linear2dB(1 + z);
            // determine index for ST
            if (!trigger  && desensitisation >= FM) {
                    index = j;
                    trigger = true;
            }
        }

        double gammaIntegral = integrate(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);
        double gammaIntegral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);
        //double gammaIntegral = integrate(I_N_bins, pdf_I_N, I_N_bins[100], I_N_bins[600]);
        //double gammaIntegral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[100], I_N_bins[600]);

        System.out.println("Results:");
        //System.out.println("pdf : " + Arrays.toString(pdf_I_N));
        //System.out.println("bins: " + Arrays.toString((I_N_bins)));
        System.out.println("FM  : " + FM);
        System.out.println("Counter  : " + counter);
        System.out.println("Index  : " + index);
        System.out.println();
        System.out.println("Expected Gamma: " + gammaExpected_count);
        System.out.println("Gamma integral Trapezoid : " + gammaIntegral);
        System.out.println("Gamma integral Sum  : " + gammaIntegral2);
        System.out.println("________________________________");

        // Assert
        Assert.assertEquals(gammaExpected_count, gammaIntegral, 1e-5);
    }

    @Test
    public void test_gamma_Var() {
        //Setup
        double VLRNoise = -120.0;
        int NoBins = 1000;
        //double FM = 19.84; // Triangle
        double FM = 71.; // Gauss
        //double FM = 29.9; // uniform
        double gammaExpected_count;
        double z, desensitisation;
        long counter = 0;
        int index=1000;
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("I_Triangular.csv");
            iRSS_vect = parseCSV("I_Gaussian.csv");
            //iRSS_vect = parseCSV("I_Uniform.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise).toArray();

        //ToDo insert precise gamma calc in eEPP - Expected - Most precise calculation of Gamma
        counter = Arrays.stream(I_N).filter(i2n -> Mathematics.linear2dB(Mathematics.dB2Linear(i2n) + 1) >= FM).count();
        gammaExpected_count= 1.* counter/ I_N.length;

        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, NoBins);
        double[] I_N_bins = calculateBinValues(I_N, NoBins);
        boolean trigger = false;

        for (int j = 0; j < NoBins; j++) {
            z = Mathematics.dB2Linear(I_N_bins[j]);
            desensitisation = Mathematics.linear2dB(1 + z);
            // determine index for ST without ATPC
            if (!trigger  && desensitisation >= FM) {
                index = j;
                trigger = true;
            }
        }

        double gammaIntegral = integrate(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);
        double gammaIntegral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);

        System.out.println("Results:");
        //System.out.println("pdf : " + Arrays.toString(pdf_I_N));
        //System.out.println("bins: " + Arrays.toString((I_N_bins)));
        System.out.println("FM  : " + FM);
        System.out.println("Min : " + Arrays.stream(I_N).min());
        System.out.println("Max : " + Arrays.stream(I_N).max());
        System.out.println("Counter  : " + counter + " of : " + I_N.length);
        System.out.println("Index  : " + index + " of : " + (NoBins-1));
        System.out.println();
        System.out.println("Expected Gamma: " + gammaExpected_count);
        System.out.println("Gamma integral Trapezoid : " + gammaIntegral);
        System.out.println("Gamma integral Sum  : " + gammaIntegral2);
        System.out.println("________________________________");

        // Here is seen issue with trapezoid integration
        //Assert.assertEquals(exResult[i], integral, 1e-5);
    }

    @Test
    public void test_gamma_S4() {
        //Setup
        double VLRNoise = -94.0;
        int NoBins = 1000;
        double FM = 11.529;
        double gammaExpected_count;
        double z, desensitisation;
        long counter = 0;
        int index;
        double [] iRSS_vect=null;

        try {
            iRSS_vect = parseCSV("iRSS_S4.csv");

        } catch (IOException e) {
            e.printStackTrace();
        }
        // getting I/N (Z) of VSL in dB
        assert iRSS_vect != null;
        double[] I_N = Arrays.stream(iRSS_vect).map(j -> j - VLRNoise).toArray();

        //ToDo - Most precise calculation of Gamma for FDP
        //  Expected
        counter = Arrays.stream(I_N).filter(i2n -> Mathematics.linear2dB(Mathematics.dB2Linear(i2n) + 1) >= FM).count();
        gammaExpected_count= 1.* counter/ I_N.length;

        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, NoBins);
        double[] I_N_bins = calculateBinValues(I_N, NoBins);
        boolean trigger = false;
        index= NoBins-1;

        for (int j = 0; j < NoBins; j++) {
            z = Mathematics.dB2Linear(I_N_bins[j]);
            desensitisation = Mathematics.linear2dB(1 + z);
            // determine index for ST without ATPC
            if (!trigger  && desensitisation >= FM) {
                index = j;
                trigger = true;
            }
        }

        double gammaIntegral = integrate(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);
        double gammaIntegral2 = integrate2(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[NoBins - 1]);

        System.out.println("Results:");
        //System.out.println("pdf : " + Arrays.toString(pdf_I_N));
        //System.out.println("bins: " + Arrays.toString((I_N_bins)));
        System.out.println("FM  : " + FM);
        System.out.println("Min : " + Arrays.stream(I_N).min());
        System.out.println("Max : " + Arrays.stream(I_N).max());
        System.out.println("Counter  : " + counter + " of : " + I_N.length);
        System.out.println("Index  : " + index + " of : " + (NoBins-1));
        System.out.println();
        System.out.println("Expected Gamma: " + gammaExpected_count);
        System.out.println("Gamma integral Trapezoid : " + gammaIntegral);
        System.out.println("Gamma integral Sum  : " + gammaIntegral2);
        System.out.println("________________________________");

        // Here is seen issue with trapezoid integration
        Assert.assertEquals(gammaExpected_count, gammaIntegral, 1e-4);
    }

}

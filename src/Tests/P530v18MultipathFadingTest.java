package Tests;

import main.java.org.seamcat.model.eventprocessing.P530v18MultipathFading;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class P530v18MultipathFadingTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void multipathFadingSingleFreq_10() {
        //Setup
        double lon = 15.67;
        double lat = 45.31;
        double he = 20.;
        double hr = 20.;
        double ht = 0.;
        double f = 6.;
        double d =31.3;

        double [] FM = new double[] {-15., -10., -5., 0.0, 5., 10., 15., 20., 25., 30.,35., 40., 45., 50. };
        double []  p_w = new double[14];
        double p0 = 0.0;
        double [] expectedResult = new double[] {319.2945194492428, 100.96979258685386, 31.929451944924274, 10.096979258685385, 3.192945194492428, 1.0096979258685386, 0.31929451944924275, 0.10096979258685385, 0.03192945194492426, 0.01009697925868538, 0.0031929451944924265, 0.001009697925868538, 3.192945194492426E-4, 1.009697925868538E-4};

        // Initialise PMP P.530v18 and get geo-climatic factor per link
        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon, lat);
        // Multipath occurrence factor
        p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, f, d, 0.0);

        // Execution
        for (int i = 0; i < FM.length; i++) {
            // Percentage of time that FM is exceeded in average worst month y; p530v18MultipathFading calculates in percent
            p_w[i]= p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, f, d, FM[i]);

            // Assert
            Assert.assertEquals(expectedResult[i], p_w[i], 1e-5);
        }
        System.out.println("Geo-climatic Factor dN75 : " + p530v18MultipathFading.getdN75());
        System.out.println("Geo-climatic Factor  logK: " + p530v18MultipathFading.getLogK());
        System.out.println("p0  : " + p0);

        System.out.println("FM : " + Arrays.toString(FM));
        System.out.println("pw: " + Arrays.toString((p_w)));
    }

    @Test
    public void multipathFading_10() {
        //Setup
        double lon = 15.67;
        double lat = 45.31;
        double he = 20.;
        double hr = 20.;
        double ht = 0.;
        double f = 6.;
        double d =31.3;

        double [] FM = new double[] {-15., -10., -5., 0.0, 5., 10., 15., 20., 25., 30.,35., 40., 45., 50. };
        double []  p_w = new double[14];
        double p0 = 0.0;
        double [] expectedResult = new double[] {100.0, 100.0, 100.0, 63.212055882855765, 3.20766471743007, 0.7100654346938851, 0.24034584326761976, 0.08678639258954401, 0.031054901542970637, 0.010096979258685386, 0.003192945194492428, 0.0010096979258685387, 3.192945194492428E-4, 1.0096979258685386E-4 };

        // Initialise PMP P.530v18 and get geo-climatic factor per link
        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon, lat);
        // Multipath occurrence factor
        p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, f, d, 0.0);

        // Execution
        for (int i = 0; i < FM.length; i++) {
            // Percentage of time that FM is exceeded in average worst month y; p530v18MultipathFading calculates in percent
            p_w[i]= p530v18MultipathFading.multipathFading(he, hr, ht, f, d, FM[i]);

            // Assert
            Assert.assertEquals(expectedResult[i], p_w[i], 1e-5);
        }
        System.out.println("Geo-climatic Factor dN75 : " + p530v18MultipathFading.getdN75());
        System.out.println("Geo-climatic Factor  logK: " + p530v18MultipathFading.getLogK());
        System.out.println("p0  : " + p0);

        System.out.println("FM : " + Arrays.toString(FM));
        System.out.println("pw: " + Arrays.toString((p_w)));
    }

    @Test
    public void multipathFading_100() {
        //Setup
        double lon = 15.67;
        double lat = 45.31;
        double he = 20.;
        double hr = 20.;
        double ht = 0.;
        double f = 9.0;
        double d =50.;

        double [] FM = new double[] {-15., -10., -5., 0.0, 5., 10., 15., 20., 25., 30.,35., 40., 45., 50. };
        double []  p_w = new double[14];
        double p0 = 0.0;
        double [] expectedResult = new double[] {100.0, 100.0, 100.0, 63.212055882855765, 9.233327489547637, 3.815279047373654, 1.8286207648486763, 0.7877637836999241, 0.2995006446033788, 0.09992254271880467, 0.031598282458689654, 0.009992254271880467, 0.0031598282458689658, 9.992254271880467E-4};

        // Initialise PMP P.530v18 and get geo-climatic factor per link
        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon, lat);
        // Multipath occurrence factor
        p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, f, d, 0.0);

        // Execution
        for (int i = 0; i < FM.length; i++) {
            // Percentage of time that FM is exceeded in average worst month y; p530v18MultipathFading calculates in percent
            p_w[i]= p530v18MultipathFading.multipathFading(he, hr, ht, f, d, FM[i]);

            // Assert
            Assert.assertEquals(expectedResult[i], p_w[i], 1e-5);
        }
        System.out.println("Geo-climatic Factor dN75 : " + p530v18MultipathFading.getdN75());
        System.out.println("Geo-climatic Factor  logK: " + p530v18MultipathFading.getLogK());
        System.out.println("p0  : " + p0);

        System.out.println("FM : " + Arrays.toString(FM));
        System.out.println("pw: " + Arrays.toString((p_w)));
    }

    // when p0 is high pw calculation is not stabile
    @Test
    public void multipathFading_high() {
        //Setup
        double lon = -0.08588;
        double lat = 51.50916;
        double he = 20.;
        double hr = 20.;
        double ht = 0.;
        double f = 6.;
        double d = 100.;

        double [] FM = new double[] {-45., -15., -10., -5., 0.0, 5., 10., 15., 20., 25., 30.,35., 40., 45., 50. };
        double []  p_w = new double[15];
        double p0 = 0.0;
        double [] expectedResult = new double[] {100.0, 100.0, 100.0, 1.0804789076264587, 63.212055882855765, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 4.719217550103676E11, 1.4923476232167404E11};

        // Initialise PMP P.530v18 and get geo-climatic factor per link
        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon, lat);
        // Multipath occurrence factor
        p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, f, d, 0.0);

        // Execution
        for (int i = 0; i < FM.length; i++) {
            // Percentage of time that FM is exceeded in average worst month y; p530v18MultipathFading calculates in percent
            p_w[i]= p530v18MultipathFading.multipathFading(he, hr, ht, f, d, FM[i]);

            // Assert
            Assert.assertEquals(expectedResult[i], p_w[i], 1e-5);
        }
        System.out.println("Geo-climatic Factor dN75 : " + p530v18MultipathFading.getdN75());
        System.out.println("Geo-climatic Factor  logK: " + p530v18MultipathFading.getLogK());
        System.out.println("p0  : " + p0);

        System.out.println("FM : " + Arrays.toString(FM));
        System.out.println("pw: " + Arrays.toString((p_w)));
    }
}
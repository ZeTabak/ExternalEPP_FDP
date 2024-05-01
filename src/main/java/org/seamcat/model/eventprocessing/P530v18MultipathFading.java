package main.java.org.seamcat.model.eventprocessing;

import org.seamcat.model.mathematics.Mathematics;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

public class P530v18MultipathFading {

    double dN75;
    double logK;

    public P530v18MultipathFading(double lon, double lat) {
        logK = P530V18DigitalMaps.getLogK(lon, lat);
        dN75 = P530V18DigitalMaps.getdN75(lon, lat);
    }

    public double multipathFadingSingleFreq(double he, double hr, double ht, double frequency, double d, double A) {
            /*
            Computes the percentage of time pw that fade depth A (dB) is exceeded in the average worst month

            Recommendation ITU-R P.530-18 § 2.3.1

             Inputs
             Variable    Unit     Type     Description
             lon, lat    deg      double    Longitude/Latitude of the path location
             d           km       double    Path distance
             he          masl     double    Emitter antenna height
             hr          masl     double    Receiver antenna height
             ht          masl     double    mean terrain elevation along the path (excluding trees)
             f           GHz      double    Frequency
             A           dB       double    Fade depth

            Outputs:
            pw          %        double    percentage of time that the
                                          fade depth A is exceeded in
                                          the average worst month

            This method is used for predicting the single-frequency (or
            narrow-band) fading distribution at large fade depths in teh
            average worst month in any part of the world. It does not
            make use of the path profile and can be used for initial
            planning, licensing, or design purposes.
            It is valid for small percentages of time
            it needs to be calculated for path lenghts longer than 5 km,
            and can be set to zero for shorter paths
            */

        double K;
        double eps, hc, hL, vsrlim, vsr, arg;
        double pw;

        // Step 1 of 2.3.1
        // Estimating geo-climatic factor K and dN75 for geo coordinates of link is done in class initialisation
        //logK = P530V18DigitalMaps.getLogK(lon, lat);
        //dN75 = P530V18DigitalMaps.getdN75(lon, lat);
        K = Math.pow(10, logK);

        // Step 2
        eps = Math.abs((hr - he) / d);
        hc = 0.5 * (hr + he) - Math.pow(d, 2) / 102. - ht;
        hL = Math.min(he, hr);

        // Step 3
        vsrlim = dN75 * Math.pow(d, 1.5) * Math.pow(frequency, 0.5) / 24730.0;
        vsr = Math.min((Math.pow((dN75 / 50.0), 1.8) * Math.exp(-1 * hc / (2.5 * Math.sqrt(d)))), vsrlim);
        arg = -0.376 * Math.tanh((hc - 147) / 125) - 0.334 * Math.pow(eps, 0.39) - 0.00027 * hL + 17.85 * vsr - A / 10;
        pw = K * Math.pow(d, 3.51) * Math.pow((frequency * frequency + 13), 0.447) * Math.pow(10, arg);

        return pw;
    }

    public double multipathFading(double he, double hr, double ht, double frequency, double d, double A) {
    /*
    Computes the percentage of time pw that fade depth A (dB) is
    exceeded in the average worst month

          Recommendation ITU-R P.530-18 § 2.3.2

          Inputs
          Variable    Unit     Type     Description
          lon, lat    deg      double    Longitude/Latitude of the path location
          d           km       double    Path distance
          he          masl     double    Emitter antenna height
          hr          masl     double    Receiver antenna height
          ht          masl     double    mean terrain elevation along
                                        the path (excluding trees)
          f           GHz      double    Frequency
          A           dB       double    Fade depth

          Outputs:

          pw          %        double    percentage of time that the
                                        fade depth A is exceeded in
                                        the average worst month

    This method predicts the percentage of time that any fade depth is exceeded
    */
        double pw;
        double p0, At, pt, qap, qt, qa;

        // Stap 1 of 2.3.2 multipath occurrence factor
        p0 = multipathFadingSingleFreq(he, hr, ht, frequency, d, 0.0);

        // Step 2
        At = 25 + 1.2 * Math.log10(p0);

        if (A >= At) {
            // Step 3a
            pw = p0 * Mathematics.dB2Linear(-A);

        } else {
            // Step 3b
            pt = p0 * Mathematics.dB2Linear(-At);
            if (pt >= 99.999) pt = 99.999; // Doc 3M/409-E - avoiding numerical instability in qap
            qap = -20 * Math.log10(-1 * Math.log((100 - pt) / 100)) / At;
            qt = (qap - 2) / ((1 + 0.3 * Math.pow(10, -At / 20)) * Math.pow(10, -0.016 * At))
                    - 4.3 * (Math.pow(10, -At / 20) + At / 800);
            qa = 2
                    + (1 + 0.3 * Math.pow(10, -A / 20)) * Math.pow(10, -0.016 * A)
                    * (qt + 4.3 * (Math.pow(10, -A / 20) + A / 800));
            pw = 100 * (1 - Math.exp(-1 * Math.pow(10, (-1 * qa * A / 20))));
        }
        return pw;
    }

    // method for calculating pw - percentage of time that Fade depth A is exceeded in the average worst month
    // ITU-R P:%30-10 Ch 2.3.1
    // K is the geo-climatic factor for UK - improve to get from files
    public static double P530bas(double he, double hr, double frequency, double d, double A) {
        double K = 3.2e-5;
        double pw;
        double eps = Math.abs((hr - he) / d);
        // check         double p0=K * Math.pow(d,3) * (1+eps)^-1.2*10^(0.0033*frequency-0.001* Math.min(he,hr));
        double p0 = K * Math.pow(d, 3) * Math.pow((1 + eps), -1.2)
                * Math.pow(10, (0.033 * frequency - 0.001 * Math.min(he, hr)));
        double At = 25 + 1.2 * Math.log10(p0);

        if (A >= At) {
            pw = p0 * Mathematics.dB2Linear(-A);

        } else {
            double pt = p0 * Mathematics.dB2Linear(-At);
            double qap = -20 * Math.log10(-1 * Math.log((100 - pt) / 100)) / At;
            double qt = (qap - 2) / ((1 + 0.3 * Math.pow(10, -At / 20)) * Math.pow(10, -0.016 * At))
                    - 4.3 * (Math.pow(10, -At / 20) + At / 800);
            double qa = 2
                    + (1 + 0.3 * Math.pow(10, -A / 20)) * Math.pow(10, -0.016 * A)
                    * (qt + 4.3 * (Math.pow(10, -A / 20) + A / 800));
            pw = 100 * (1 - Math.exp(-1 * Math.pow(10, (-1 * qa * A / 20))));
        }
        return pw;
    }

    // for testing
    public static void main(String[] args) {
        // Example usage
        double a1, a2, a3, a4;
        double lon, lat;
        lon = 15.5; lat = 45.5;
        double[] he = new double[] {30};
        double[] hr = new double[] {1.5};
        double[] ht = new double[] {0};
        double[] f_GHz = new double[] {6};
        double[] dist = new double[] {30};
        double[] fadeDepth = new double[] {20};

        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(lon, lat);
        for (int i = 0; i < 1; i++) {
            a1 = p530v18MultipathFading.multipathFadingSingleFreq(he[i], hr[i], ht[i], f_GHz[i], dist[i], 0);
            a2 = p530v18MultipathFading.multipathFadingSingleFreq(he[i], hr[i], ht[i], f_GHz[i], dist[i], fadeDepth[i]);
            a3 = p530v18MultipathFading.multipathFading( he[i], hr[i], ht[i], f_GHz[i], dist[i], fadeDepth[i]);
            a4 = P530v18MultipathFading.P530bas( he[i], hr[i],  f_GHz[i], dist[i], fadeDepth[i]);

            System.out.println("he=" + he[i] + " hr=" +  hr[i] + " ht=" +  ht[i] + " f=" +  f_GHz[i] + " d=" + dist[i] +" A=" +  fadeDepth[i]);
            System.out.println("Iter = " + "i"+ " p0 = " + a1 + " pw_single = " + a2 + " pw_all = " +  a3 + " pw_OLD = " + a4);
        }
    }
}

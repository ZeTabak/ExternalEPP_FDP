package main.java.org.seamcat.model.eventprocessing;

import static org.seamcat.model.factory.Factory.results;
import static org.seamcat.model.plugin.system.BuiltInSystem.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seamcat.model.Scenario;
import org.seamcat.model.factory.Factory;
import org.seamcat.model.mathematics.Mathematics;
import org.seamcat.model.plugin.Config;
import org.seamcat.model.plugin.OptionalValue;
import org.seamcat.model.plugin.eventprocessing.EventProcessingPlugin;
import org.seamcat.model.plugin.eventprocessing.EventProcessingPostProcessor;
import org.seamcat.model.plugin.propagation.TerrainCoordinate;
import org.seamcat.model.plugin.propagation.TerrainData;
import org.seamcat.model.plugin.propagation.TerrainDataPoint;
import org.seamcat.model.plugin.system.ConsistencyCheckContext;
import org.seamcat.model.plugin.system.ContexedSystemPlugin;
import org.seamcat.model.plugin.system.SystemPlugin;
import org.seamcat.model.simulation.result.*;
import org.seamcat.model.types.Description;
import org.seamcat.model.types.Unit;
import org.seamcat.model.types.result.*;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.

public class DemoEPP_16_FDP
    implements EventProcessingPlugin<DemoEPP_16_FDP.Input>, EventProcessingPostProcessor<DemoEPP_16_FDP.Input> {

    private double he, hr; // antenna height (m) of Tx and Rx
    private double ht; // terrain height
    private double frequency; // frequency (GHz)
    private double d; // path length (km)
    private double longitudeMid, latitudeMid;
    private double noiseFloor;

    public interface Input {
        @Config(order = 1, name = "Longitude of VSL path centre", unit = Unit.deg,
            toolTip = "In decimal numbers, E longitudes are positive, W longitudes are negative", information = "If VLR uses terrain PMP, coordinates of VSL path centre are extracted from terrain")
        double
        longitude();
        double longitude = 15.0;

        @Config(order = 2, name = "Latitude of VSL path centre", unit = Unit.deg,
            toolTip = "In decimal numbers, N latitudes are positive, S latitudes are negative", information = "If VLR uses terrain PMP, coordinates of VSL path centre are extracted from terrain")
        double
        latitude();
        double latitude = 45.0;

        @Config(order = 3, name = "Victim System Fading Margin", unit = Unit.dB)
        double fMargin();
        double fMargin = 35.0;

        @Config(order = 5, name = "Number of calculation bins", unit = Unit.count)
        int binNo();
        int binNo = 1000;

        @Config(order = 4, name = "ATPC Range", toolTip = "Check only if VSL uses ATPC and define ATPC Range", unit = Unit.dB)
        OptionalValue<Double> atpcRange();

        // No need for noiseFloor Input - get it from scenario (v.5.5.1 A2)
        // needed for 5.5.0  - will be removed in future
        @Config(order = 6, name = "VLR Noise Floor", toolTip = "input Noise floor or the receiver",unit = Unit.dBm)
        double nFloor();
        double nFloor = -94.0;

    }

    @Override
    public void consistencyCheck(ConsistencyCheckContext context, Input input) {
        if ((Math.abs( input.longitude())>180) || (Math.abs( input.latitude())>90)) {
            context.addError("Coordinate of VSL path centre is outside valid range");
        }
        if (!(input.fMargin()>0)) {
            context.addError("Victim System Fading Margin is not valid (should be greater than 0)");
        }
        if (!(input.binNo()>1)) {
            context.addError("Number of calculation bins is not valid (should be greater than 1)");
        }

        Scenario scenario = context.getScenario();
        SystemPlugin plugin = scenario.getVictim().getSystemPlugin();
        boolean isValid = Factory.in(plugin, GENERIC);
        if (!isValid) {
            context.addError("EPP for FDP calculation can only be applied to the Fixed Service victim system (Generic System type)");
        }
    }

    @Override
    public Description description() {
        return new DescriptionImpl("eEPP 16: FDP - Fractional Degradation of Performance_v1.5",
            "<html>This Event Processing Plugin calculates FDP for FS link with and without ATPC <br>"
                + "It uses definition of FDP in ITU-R Recommendations F.1108, ITU-R F.758, and <br>"
                + "calculates the fade probability based on ITU-R P.530-18 method for all percentages <br>"
                + "of time according to 2.3.2 and methodology derived by WGSE SE19 SE19(23)042A05 <br>"
                + "if terrain data is available longitude and latitude of the link mid point, heights <br>"
                + "of Tx and Rx antenna and terrain height are taken from terrain profile, otherwise <br>"
                + "these values are taken from inputs<br>"
                + "ver 1.0 (Dec 2023) - first implementation of FDP calculations <br>"
                + "ver 1.5 (Apr 2024) - added terrain feature in calculations and multipath occurrence factor to results, implementation Annex 2 for ATPC<br>"
                + "ver 1.6_int1 (May 2024) - improvements</html>");
    }

    @Override
    public void evaluate(Scenario scenario, EventResult result, Input input, Collector collector) {
        if (result.isLastEvent()) {
            LinkResult VictimLinkResult = result.getVictimResult().getVictims().get(0).getLinkResult();
            frequency = VictimLinkResult.getFrequency() / 1000; // to GHz
            d = VictimLinkResult.getTxRxDistance(); // in km

            // initialisation of variables if there is no terrain;
            // he, hr = height of antenna; ht = terrain height = 0; longitude and latitude from UI input
            he = VictimLinkResult.txAntenna().getHeight();
            hr = VictimLinkResult.rxAntenna().getHeight();
            ht = 0;
            longitudeMid = input.longitude();
            latitudeMid = input.latitude();

            // initialisation of variables if there is terrain data;
            TerrainData data = VictimLinkResult.getTerrainData();
            if (data != null) {
                // extracting terrain profile data (height) and calculating heights
                TerrainCoordinate from = data.getFrom();
                TerrainCoordinate to = data.getTo();
                List<TerrainDataPoint> profile = data.getData();
                double[] rHighti = new double[profile.size()];
                for (int i = 0, dataSize = profile.size(); i < dataSize; i++) {
                    TerrainDataPoint dataPoint = profile.get(i);
                    rHighti[i] = dataPoint.getHeight();
                }
                // adjustment of heights to get amsl heights
                he = he + rHighti[0];
                hr = hr + rHighti[rHighti.length - 1];
                ht = Mathematics.getAverage(rHighti);
                // Calculate midpoint of VLR Link
                double[] midCoordinateVLR = midPoint(from.getLat(), from.getLon(), to.getLat(), to.getLon());
                longitudeMid = midCoordinateVLR[0];
                latitudeMid = midCoordinateVLR[1];
            }
            // needed for 5.5.0 - will be removed later on and replaced by commented line
            //noiseFloor= scenario.getVictim().getSystem().getReceiver().getNoiseFloor();
            noiseFloor = input.nFloor();
        }
    }

    @Override
    public void postProcess(Input input, Scenario scenario, Results results, SimulationResult simResult) {
        // create single values and vectors using Result Factory for collecting results
        UniqueValueDef FRACTIONAL_DEGRADATION_PERFORMANCE =
                results().uniqueValue("Fractional Degradation of Performance", "FDP", Unit.percent, false);
        UniqueValueDef FRACTIONAL_DEGRADATION_PERFORMANCE_LT =
                results().uniqueValue("Fractional Degradation of Performance", "FDP - Long Term", Unit.percent, false);
        UniqueValueDef FRACTIONAL_DEGRADATION_PERFORMANCE_ST =
                results().uniqueValue("Fractional Degradation of Performance", "FDP - Short Term", Unit.percent, false);

        // Adapt FDP values to vector for enable extracting results from CL in v5.5.0
        // Will be removed in future
        VectorDef vector_FDP = results().vector("FDPVectors", "FDP_v", Unit.percent, true);
        VectorDef vector_FDP_LT = results().vector("FDPVectors", "FDP_LT_v", Unit.percent, true);
        VectorDef vector_FDP_ST = results().vector("FDPVectors", "FDP_ST_v", Unit.percent, true);

        // Intermediates for testing
        UniqueValueDef PO = results().uniqueValue("Intermediates", "pO multipath occurrence factor", Unit.percent, true);
        UniqueValueDef POO = results().uniqueValue("Intermediates", "POO (x 100)", Unit.percent, true);
        UniqueValueDef P0I = results().uniqueValue("Intermediates", "POI (x 100)", Unit.percent, true);
        UniqueValueDef POI_ST =
                results().uniqueValue("Intermediates", "POI_ST (x 100)", Unit.percent, true);
        UniqueValueDef POI_LT =
                results().uniqueValue("Intermediates", "POI_LT (x 100)", Unit.percent, true);
        UniqueValueDef Gamma =
                results().uniqueValue("Intermediates", "gamma (x 100)", Unit.percent, true);
        UniqueValueDef IN_ST = results().uniqueValue("Intermediates", "I/N st", Unit.dB, true);
        UniqueValueDef Test = results().uniqueValue("Intermediates", "Test", Unit.none, true);
        VectorDef vectorI_N = results().vector("Vectors", "I/N", Unit.dB, true);
        //VectorDef vector_pw = results().vector("Vectors", "Multipath fading percentage", Unit.percent, true);
        //public static final VectorDef vectori_N_pdf = results().vector("Vectors", "I/N pdf", Unit.none, true);

        double p0; //  Multipath occurrence factor ITU-R P.530 Ch 2.3.2 (11)
        double[] pw; // Multipath fading percentage - percentage of time that the fade depth A is
        String test = "True";

        // getting iRSS all of VSL
        Results victimResults = simResult.getVictimResults();
        ContexedSystemPlugin victim = scenario.getVictim();
        VectorResultType iRSS_All = victimResults.findVector(victim.getIRSS_COMBINED());
        double[] iRSSUnwBloc = iRSS_All.value().asArray();

        // getting I/N (Z) of VSL in dB
        double[] I_N = Arrays.stream(iRSSUnwBloc).map(i -> i - noiseFloor).toArray();

        // pdf of I/N and bin values dB
        double[] pdf_I_N = calculatePDF(I_N, input.binNo());
        double[] I_N_bins = calculateBinValues(I_N, input.binNo());

        // Initialise PMP P.530v18 and get geo-climatic factor per link
        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(longitudeMid, latitudeMid);
        // Multipath occurrence factor
        p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, frequency, d, 0.0);

        double ATPCRange = (input.atpcRange().isRelevant()) ? input.atpcRange().getValue() : 0;

        // Calculate FDP & collect all results in map
        Map<String, Double> resultsFDP = calculateFDP(he, hr, ht, frequency, d, input.fMargin(), input.atpcRange().isRelevant(), ATPCRange, input.binNo(), p530v18MultipathFading, pdf_I_N, I_N_bins);

        // Collecting results of EPP
        // Ensuring FDP is not less than zero
        if (resultsFDP.getOrDefault("FDP", 0.0) < 0.0 ||
                resultsFDP.getOrDefault("FDP_LT", 0.0) < 0.0 ||
                resultsFDP.getOrDefault("FDP_ST", 0.0) < 0.0) {
            resultsFDP.replaceAll((key, value) -> Math.max(value, 0.0));
        }

        // Collecting main results
        results.addSingleValueType(new DoubleResultType(FRACTIONAL_DEGRADATION_PERFORMANCE, resultsFDP.getOrDefault("FDP", 0.0)));
        results.addSingleValueType(new DoubleResultType(FRACTIONAL_DEGRADATION_PERFORMANCE_LT, resultsFDP.getOrDefault("FDP_LT", 0.0)));
        results.addSingleValueType(new DoubleResultType(FRACTIONAL_DEGRADATION_PERFORMANCE_ST, resultsFDP.getOrDefault("FDP_ST", 0.0)));

        // Collecting Intermediate values for testing
        results.addSingleValueType(new DoubleResultType(PO, p0));
        results.addSingleValueType(new DoubleResultType(POO, resultsFDP.getOrDefault("P00", 0.0) * 100));
        results.addSingleValueType(new DoubleResultType(P0I, resultsFDP.getOrDefault("P0I", 0.0)  * 100));
        results.addSingleValueType(new DoubleResultType(POI_LT, resultsFDP.getOrDefault("P0I_LT", 0.0)  * 100));
        results.addSingleValueType(new DoubleResultType(POI_ST, resultsFDP.getOrDefault("P0I_ST", 0.0)  * 100));
        results.addSingleValueType(new DoubleResultType(Gamma, resultsFDP.getOrDefault("Gamma", 0.0)  * 100));
        results.addSingleValueType(new DoubleResultType(IN_ST, resultsFDP.get("IN_ST")));
        results.addVectorResultType(new VectorResultType(vectorI_N, I_N));
        results.addSingleValueType(new StringResultType(Test, test));
        //results.addVectorResultType(new VectorResultType(vector_pw, pw));
        //results.addVectorResultType(new VectorResultType(vectori_N_pdf, pdf_I_N));

        // Adding FDP as vector results for easier extract in CommandLine in 5.5.0
        // will be removed later on
        double[] FDP_vect = new double[1];
        double[] FDP_LT_vect = new double[1];
        double[] FDP_ST_vect = new double[1];
        FDP_vect[0] = resultsFDP.get("FDP"); FDP_LT_vect[0] =resultsFDP.get("FDP_LT"); FDP_ST_vect[0]=resultsFDP.get("FDP_ST");
        results.addVectorResultType(new VectorResultType(vector_FDP, FDP_vect));
        results.addVectorResultType(new VectorResultType(vector_FDP_LT, FDP_LT_vect));
        results.addVectorResultType(new VectorResultType(vector_FDP_ST, FDP_ST_vect));
    }

    // Method for calculating FDP
    protected Map<String, Double> calculateFDP (double he, double hr, double ht, double frequency, double d, double fMargin, boolean ATPCisRelevant, double atpcRange, int binNo, P530v18MultipathFading p530v18MultipathFading, double[] pdf_I_N, double[] I_N_bins ){

        double FDP, FDP_LT, FDP_ST;
        double p00, p0I, p0I_LT, p0I_ST;
        double z; // i/n  in lin domain
        double desensitisation; // noise rise 10log(1+z)
        double I_N_st_dB;
        int index; // index for determining division between LT and ST region

        double FMdeg; // FM degradation due to interference = fMargin - desensitisation
        double[] pw; // Multipath fading percentage - percentage of time that the fade depth A is
                     // exceeded in the average worst month ITU-R P.530 Ch 2.3.2 (17)
        double[] weightedFading;
        boolean trigger; // trigger for determining transition between LT and ST in Annex 1
        double gamma; // correction factor for LS & ST
        double FM;

        // for links with ATPC
        boolean triggerAtpc; // trigger for determining transition between LT and ST in Annex 2 (with ATPC)
        double NFM = 0;
        int indexAtpc; // index for determining ST region in ATPC

        // Result collector
        Map<String, Double> results = new HashMap<>();

        pw = new double[I_N_bins.length];
        weightedFading = new double[I_N_bins.length];

        FM = fMargin;
        // calculate NFM for links with ATPC
        if (ATPCisRelevant) {
            NFM = fMargin - atpcRange;
        }

        double p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, frequency, d, 0.0);
        // p00 probability of outage due to fading only; p530v18MultipathFading calculates in percent
        p00 = p530v18MultipathFading.multipathFading(he, hr, ht, frequency, d, fMargin) / 100;
        p0I = p00; // initialisation

        index = binNo - 1; trigger = false;
        indexAtpc = binNo - 1; triggerAtpc = false;

        for (int i = 0; i < binNo; i++) {
            z = Mathematics.dB2Linear(I_N_bins[i]);
            // z = (I_N_bins[i]); if using linear i/n
            desensitisation = Mathematics.linear2dB(1 + z);
            FMdeg = fMargin - desensitisation;

            pw[i] = p530v18MultipathFading.multipathFading(he, hr, ht, frequency, d, FMdeg) / 100;
            weightedFading[i] = pdf_I_N[i] * pw[i];

            // determine index for distinction between LT and ST without ATPC
            if (!trigger && z >= Mathematics.dB2Linear(fMargin) - 1) {
                index = i;
                trigger = true;
            }
            // determine index for ST with ATPC
            if (!triggerAtpc && ATPCisRelevant && desensitisation >= NFM) {
                indexAtpc = i;
                triggerAtpc = true;
            }
        }
        // integration to determine probability of outage from fading and interference p0I, p0I_LT, p0I_ST and
        p0I_LT = integrate(I_N_bins, weightedFading, I_N_bins[0], I_N_bins[index]);

        // Annex 2 - ATPC
        if (ATPCisRelevant) {
            p0I_ST = integrate(I_N_bins, pdf_I_N, I_N_bins[indexAtpc], I_N_bins[binNo - 1]);
            p0I = p0I_LT + p0I_ST;
            gamma = integrate(I_N_bins, pdf_I_N, I_N_bins[indexAtpc], I_N_bins[binNo - 1]);
            I_N_st_dB= Mathematics.linear2dB(Mathematics.dB2Linear(NFM) - 1);
        // Annex 1 - No ATPC
        } else {
            p0I = integrate(I_N_bins, weightedFading, I_N_bins[0], I_N_bins[binNo - 1]);
            p0I_ST = integrate(I_N_bins, weightedFading, I_N_bins[index], I_N_bins[binNo - 1]);
            gamma = integrate(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[binNo - 1]);
            I_N_st_dB = Mathematics.linear2dB(Mathematics.dB2Linear(fMargin) - 1);
        }

        // applying correction factor
        p0I_LT = p0I_LT + gamma * p00;
        p0I_ST = p0I_ST + (1 - gamma) * p00;

        // calculating FDP in percent
        FDP_LT = ((p0I_LT / p00 - 1)) * 100;
        FDP_ST = ((p0I_ST / p00 - 1)) * 100;
        FDP = (ATPCisRelevant) ? FDP_LT + FDP_ST : (p0I / p00 - 1) * 100;

        // collecting results
        results.put("FDP", FDP);
        results.put("FDP_LT", FDP_LT);
        results.put("FDP_ST", FDP_ST);
        results.put("P00", p00);
        results.put("P0I", p0I);
        results.put("P0I_LT", p0I_LT);
        results.put("P0I_ST", p0I_ST);
        results.put("Gamma", gamma);
        results.put("IN_ST", I_N_st_dB);
        return results;
    }

    public static double[] calculatePDF(double[] data, int numBins) {
        double[] pdf = new double[numBins];

        // Find the min and max values in the data
        double min = Arrays.stream(data).min().getAsDouble();
        double max = Arrays.stream(data).max().getAsDouble();
        if (min == max){
            min = min - 1./numBins;
            max = max + 1./numBins;
        }

        // Calculate bin width
        //double binWidth = (max - min) / (numBins);
        double binWidth = (max - min) / (numBins-1);

        // Count data points in each bin
        for (Double value : data) {
            int binIndex = (int) Math.floor((value - min) / binWidth);
            binIndex = (binIndex == numBins) ? binIndex - 1 : binIndex;
            pdf[binIndex]++;
        }

        // Normalize the PDF
        double totalCount = data.length;
        for (int i = 0; i < numBins; i++) {
            pdf[i] /= (totalCount * binWidth);
        }
        return pdf;
    }

    public static double[] calculateBinValues(double[] data, int numBins) {
        double[] binValues = new double[numBins];

        // Find the min and max values in the data
        double min = Arrays.stream(data).min().getAsDouble();
        double max = Arrays.stream(data).max().getAsDouble();
        if (min == max){
            min = min - 1./numBins;
            max = max + 1./numBins;
        }

        // Calculate bin width
        // results in negative fdp and integrate(I_N_bins, pdf_I_N, I_N_bins[0], I_N_bins[input.binNo() - 1]) < 1
        //double binWidth = (max - min) / numBins;
        //correction
        double binWidth = (max - min) / (numBins-1);

        // Generate the bin values
        for (int i = 0; i < numBins; i++) {
            binValues[i] = min + i * binWidth;
        }
        return binValues;
    }

    // Function to perform numerical integration using the trapezoidal rule
    public static double integrate(double[] x, double[] fx, double a, double b) {
        double integral = 0.0;
        double deltaX=0;

        if (x.length != fx.length) {
            throw new IllegalArgumentException("Arrays x and fx must have the same length");
        }
        if (x.length < 2) {
            throw new IllegalArgumentException("Arrays x and fx must have at least two points for integration");
        }

        // Find the index corresponding to 'a' and 'b' in the x array
        int indexA = 0;
        int indexB = 0;

        for (int i = 0; i < x.length; i++) {
            if (x[i] <= a) {
                indexA = i;
            }
            if (x[i] <= b) {
                indexB = i;
            }
        }
        // Trapezoidal rule
        for (int i = indexA; i < indexB; i++) {
            deltaX = x[i + 1] - x[i];
            double area = (fx[i] + fx[i + 1]) * deltaX / 2.0;
            integral += area;
        }
        //integral += (fx[indexA] + fx[indexB]) * deltaX / 2.0;
        return integral;
    }

    // Function to perform numerical integration using sum of pdfs
    public static double integrate2(double[] x, double[] fx, double a, double b) {
        double integral = 0.0;
        double deltaX=0;

        if (x.length != fx.length) {
            throw new IllegalArgumentException("Arrays x and fx must have the same length");
        }
        if (x.length < 2) {
            throw new IllegalArgumentException("Arrays x and fx must have at least two points for integration");
        }

        // Find the index corresponding to 'a' and 'b' in the x array
        int indexA = 0;
        int indexB = 0;

        for (int i = 0; i < x.length; i++) {
            if (x[i] <= a) {
                indexA = i;
            }
            if (x[i] <= b) {
                indexB = i;
            }
        }

        for (int i = indexA; i <= indexB; i++) {
            integral += fx[i];
        }
        integral = integral * (x[1] - x[0]);

        return integral;
    }


    public static double[] midPoint(double lat1, double lon1, double lat2, double lon2) {
        // convert to radians
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(
            Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        return new double[]{Math.toDegrees(lon3), Math.toDegrees(lat3)};
    }
}
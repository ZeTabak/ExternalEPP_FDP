package main.java.org.seamcat.model.eventprocessing;

import static org.seamcat.model.factory.Factory.results;

import java.util.Arrays;
import java.util.List;
import org.seamcat.model.Scenario;
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
import org.seamcat.model.simulation.result.*;
import org.seamcat.model.types.Description;
import org.seamcat.model.types.Unit;
import org.seamcat.model.types.result.DescriptionImpl;
import org.seamcat.model.types.result.DoubleResultType;
import org.seamcat.model.types.result.Results;
import org.seamcat.model.types.result.VectorResultType;

public class DemoEPP_16_FDP
    implements EventProcessingPlugin<DemoEPP_16_FDP.Input>, EventProcessingPostProcessor<DemoEPP_16_FDP.Input> {
    public static final UniqueValueDef FRACTIONAL_DEGRADATION_PERFORMANCE =
        results().uniqueValue("Fractional Degradation of Performance", "FDP", Unit.percent, false);
    public static final UniqueValueDef FRACTIONAL_DEGRADATION_PERFORMANCE_LT =
        results().uniqueValue("Fractional Degradation of Performance", "FDP - Long Term", Unit.percent, false);
    public static final UniqueValueDef FRACTIONAL_DEGRADATION_PERFORMANCE_ST =
        results().uniqueValue("Fractional Degradation of Performance", "FDP - Short Term", Unit.percent, false);

    // Intermediates for testing
    public static final UniqueValueDef PO = results().uniqueValue("Intermediates", "pO multipath occurrence factor", Unit.percent, true);
    public static final UniqueValueDef POO = results().uniqueValue("Intermediates", "POO (x 100)", Unit.percent, true);
    public static final UniqueValueDef P0I = results().uniqueValue("Intermediates", "POI (x 100)", Unit.percent, true);
    public static final UniqueValueDef POI_ST =
        results().uniqueValue("Intermediates", "POI_ST (x 100)", Unit.percent, true);
    public static final UniqueValueDef POI_LT =
        results().uniqueValue("Intermediates", "POI_LT (x 100)", Unit.percent, true);
    public static final UniqueValueDef Gamma =
        results().uniqueValue("Intermediates", "gamma (x 100)", Unit.percent, true);
    public static final UniqueValueDef IN_ST = results().uniqueValue("Intermediates", "I/N st", Unit.dB, true);
    public static final VectorDef vectorI_N = results().vector("Vectors", "I/N", Unit.dB, true);
    public static final VectorDef vector_pw =
        results().vector("Vectors", "Multipath fading percentage", Unit.percent, true);
    //public static final VectorDef vectori_N_pdf = results().vector("Vectors", "I/N pdf", Unit.none, true);

    private double he, hr; // antenna height (m) of Tx and Rx
    private double ht; // terrain height
    private double frequency; // frequency (GHz)
    private double d; // path length (km)
    private double longitudeMid, latitudeMid;

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

        @Config(order = 6, name = "VLR Noise Floor", toolTip = "input Noise floor or the receiver",unit = Unit.dBm)
        double nFloor();
        double nFloor = -94.0;

    }

    @Override
    public void consistencyCheck(ConsistencyCheckContext context, Input input) {}

    @Override
    public Description description() {
        return new DescriptionImpl("EPP 16: FDP - Fractional Degradation of Performance_v2.0",
            "<html>This Event Processing Plugin calculates FDP for FS link with and without ATPC <br>"
                + "It uses definition of FDP in ITU-R Recommendations F.1108, ITU-R F.758, and <br>"
                + "calculates the fade probability based on ITU-R P.530-18 method for all percentages <br>"
                + "of time according to 2.3.2 and methodology derived by WGSE SE19 SE19(23)042A05 <br>"
                + "ver 1.0 (Dec 2023) - first implementation of FDP calculations <br>"
                + "ver 2.0 (Feb 2024) - added terrain feature in calculations and multipath occurrence factor to results</html>");
    }

    @Override
    public void evaluate(Scenario scenario, EventResult result, Input input, Collector collector) {
        if (result.isLastEvent()) {
            LinkResult VictimLinkResult = result.getVictimResult().getVictims().get(0).getLinkResult();
            frequency = VictimLinkResult.getFrequency() / 1000; // to GHz
            d = VictimLinkResult.getTxRxDistance(); // in km

            // initialisation of variables if there is no terrain;
            // he, hr = height of antenna; ht = terrain height = 0
            // Longitude and latitude from input
            he = VictimLinkResult.txAntenna().getHeight();
            hr = VictimLinkResult.rxAntenna().getHeight();
            ht = 0;
            longitudeMid = input.longitude();
            latitudeMid = input.latitude();

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
            //noiseFloor= scenario.getVictim().getSystem().getReceiver().getNoiseFloor();

        }
    }

    @Override
    public void postProcess(Input input, Scenario scenario, Results results, SimulationResult simResult) {
        double p0, p00, p0I, p0I_LT, p0I_ST;
        double z; // i/n  in lin domain
        double FMdeg; // FM degradation due to interference
        // double pw;      // Multipath fading percentage - percentage of time that the fade depth A is
        //                 // exceeded in the average worst month
        double[] pw; // Multipath fading percentage - percentage of time that the fade depth A is
        // exceeded in the average worst month
        double gamma; // correction factor for LS & ST
        int index; // index for determining division between LT and ST region
        boolean trigger = false;
        double FDP, FDP_LT, FDP_ST;
        double noiseFloor;

        // for links with ATPC
        boolean triggerAtpc = false;
        double atpcRange;
        double NFM = 0;
        double desensitisation; // noise rise 10log(1+z)
        int indexAtpc; // index for determining ST region in ATPC

        // calculate NFM for links with ATPC
        if (input.atpcRange().isRelevant()) {
            atpcRange = input.atpcRange().getValue();
            NFM = input.fMargin() - atpcRange;
        }

        // getting iRSS all of VSL
        Results victimResults = simResult.getVictimResults();
        ContexedSystemPlugin victim = scenario.getVictim();
        VectorResultType iRSS_All = victimResults.findVector(victim.getIRSS_COMBINED());
        double[] iRSSUnwBloc = iRSS_All.value().asArray();

        // Can not get in external EPP
        // getting noise of VSL
        //SystemModelGeneric ui = (SystemModelGeneric) victim.getSystemPlugin().getUI();
        //noiseFloor = ui.receiver().receptionCharacteristics().noiseFloor();
        noiseFloor = input.nFloor();


        // getting I/N (Z) of VSL in dB
        double[] I_N = Arrays.stream(iRSSUnwBloc).map(i -> i - noiseFloor).toArray();

        // pdf of I/N
        double[] pdf_I_N = calculatePDF(I_N, input.binNo());
        double[] I_N_bins = calculateBinValues(I_N, input.binNo());

        double[] weightedFading = new double[I_N_bins.length];
        pw = new double[I_N_bins.length];

        // Initialise PMP P.530v18 and get geo-climatic factor per link
        P530v18MultipathFading p530v18MultipathFading = new P530v18MultipathFading(longitudeMid, latitudeMid);
        p0 = p530v18MultipathFading.multipathFadingSingleFreq(he, hr, ht, frequency, d, 0.0);

        // p00 probability of outage due to fading only; p530v18MultipathFading calculates in percent
        p00 = p530v18MultipathFading.multipathFading(he, hr, ht, frequency, d, input.fMargin()) / 100;
        p0I = p00; // initialisation

        index = input.binNo() - 1;
        indexAtpc = input.binNo() - 1;

        for (int i = 0; i < input.binNo(); i++) {
            z = Mathematics.dB2Linear(I_N_bins[i]);
            desensitisation = Mathematics.linear2dB(1 + z);
            FMdeg = input.fMargin() - desensitisation;

            pw[i] = p530v18MultipathFading.multipathFading(he, hr, ht, frequency, d, FMdeg) / 100;
            weightedFading[i] = pdf_I_N[i] * pw[i];

            // determine index for distinction between LT and ST without ATPC
            if (!trigger && z >= Mathematics.dB2Linear(input.fMargin()) - 1) {
                index = i;
                trigger = true;
            }
            // determine index for ST with ATPC
            if (!triggerAtpc && input.atpcRange().isRelevant() && desensitisation >= NFM) {
                indexAtpc = i;
                triggerAtpc = true;
            }
        }

        // integration to determine probability of outage from fading and interference p0I, p0I_LT, p0I_ST and
        // correction factor gamma
        p0I_LT = integrate(I_N_bins, weightedFading, I_N_bins[0], I_N_bins[index]);
        gamma = integrate(I_N_bins, pdf_I_N, I_N_bins[index], I_N_bins[input.binNo() - 1]);

        if (input.atpcRange().isRelevant()) {
            p0I_ST = integrate(I_N_bins, pdf_I_N, I_N_bins[indexAtpc], I_N_bins[input.binNo() - 1]);
        } else {
            p0I = integrate(I_N_bins, weightedFading, I_N_bins[0], I_N_bins[input.binNo() - 1]);
            p0I_ST = integrate(I_N_bins, weightedFading, I_N_bins[index], I_N_bins[input.binNo() - 1]);
        }

        // applying correction factor
        p0I_LT = p0I_LT + gamma * p00;
        p0I_ST = p0I_ST + (1 - gamma) * p00;

        // calculating FDP in percent
        FDP_LT = ((p0I_LT / p00 - 1)) * 100;
        FDP_ST = ((p0I_ST / p00 - 1)) * 100;
        FDP = (input.atpcRange().isRelevant()) ? FDP_LT + FDP_ST : (p0I / p00 - 1) * 100;

        results.addSingleValueType(new DoubleResultType(FRACTIONAL_DEGRADATION_PERFORMANCE, FDP));
        results.addSingleValueType(new DoubleResultType(FRACTIONAL_DEGRADATION_PERFORMANCE_LT, FDP_LT));
        results.addSingleValueType(new DoubleResultType(FRACTIONAL_DEGRADATION_PERFORMANCE_ST, FDP_ST));

        // Collecting Intermediate values for testing
        results.addSingleValueType(new DoubleResultType(PO, p0));
        results.addSingleValueType(new DoubleResultType(POO, p00 * 100));
        results.addSingleValueType(new DoubleResultType(P0I, p0I * 100));
        results.addSingleValueType(new DoubleResultType(POI_LT, p0I_LT * 100));
        results.addSingleValueType(new DoubleResultType(POI_ST, p0I_ST * 100));
        results.addSingleValueType(new DoubleResultType(Gamma, gamma * 100));
        results.addSingleValueType(
            new DoubleResultType(IN_ST, Mathematics.linear2dB(Mathematics.dB2Linear(input.fMargin()) - 1)));
        results.addVectorResultType(new VectorResultType(vectorI_N, I_N));
        results.addVectorResultType(new VectorResultType(vector_pw, pw));
        // results.addVectorResultType(new VectorResultType(vectori_N_pdf, pdf_I_N));
    }

    public static double[] calculatePDF(double[] data, int numBins) {
        double[] pdf = new double[numBins];

        // Find the min and max values in the data
        double min = Arrays.stream(data).min().getAsDouble();
        double max = Arrays.stream(data).max().getAsDouble();

        // Calculate bin width
        double binWidth = (max - min) / (numBins);

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

        // Calculate bin width
        double binWidth = (max - min) / numBins;

        // Generate the bin values
        for (int i = 0; i < numBins; i++) {
            binValues[i] = min + i * binWidth;
        }
        return binValues;
    }

    // Function to perform numerical integration using the trapezoidal rule
    public static double integrate(double[] x, double[] fx, double a, double b) {
        double integral = 0.0;

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
            double h = x[i + 1] - x[i];
            double area = (fx[i] + fx[i + 1]) * h / 2.0;
            integral += area;
        }
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
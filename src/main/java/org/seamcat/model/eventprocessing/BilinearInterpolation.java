package main.java.org.seamcat.model.eventprocessing;

//  @author: Zeljko TABAKOVIC
//  All rights reserved.
// https://en.wikipedia.org/wiki/Bilinear_interpolation

public class BilinearInterpolation {

        public static double[] interp2(double[] X, double[] Y, double[][] V, double[] Xq, double[] Yq) {
            double[] interpolatedValues = new double[Xq.length];

            for (int i = 0; i < Xq.length; i++) {
                int xIdx = findIndex(X, Xq[i],true);
                int yIdx = findIndex(Y, Yq[i], false);

                // bilinear interpolation assumes x1 < x2 and y1 < y2
                if (xIdx != -1 && yIdx != -1) {
                    // when X is sorted in increasing order and Y is sorted in decreasing order
                    double x1 = X[xIdx];
                    double x2 = X[xIdx + 1];
                    double y2 = Y[yIdx];
                    double y1 = Y[yIdx + 1];

                    double v12 = V[yIdx][xIdx];
                    double v22 = V[yIdx][xIdx + 1];
                    double v11 = V[yIdx + 1][xIdx];
                    double v21 = V[yIdx + 1][xIdx + 1];

                    double x = (Xq[i] - x1) / (x2 - x1);
                    double y = (Yq[i] - y1) / (y2 - y1);

                    double value = (1 - x) * (1 - y) * v11 + x * (1 - y) * v21 + (1 - x) * y * v12 + x * y * v22;
                    interpolatedValues[i] = value;
                } else {
                    interpolatedValues[i] = Double.NaN; // Query point outside the range, return NaN
                }
            }

            return interpolatedValues;
        }

        // Helper method to find the index in an array
        private static int findIndex(double[] arr, double value, boolean asc) {
            for (int i = 0; i < arr.length - 1; i++) {
                if (asc){
                    if (value >= arr[i] && value <= arr[i + 1]) {
                        return i;
                    }
                } else {
                    if (value <= arr[i] && value >= arr[i + 1]) {
                        return i;
                    }
                }

            }
            return -1;
        }

}

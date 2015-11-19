/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamstar;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 *
 * @author Umberto
 */
public class Utility {

    public static void printVector(int[] vector) {
        for (int i = 0; i < vector.length; i++) {
            System.out.printf("[%d]", vector[i]);
        }
        System.out.println("");
    }

    public static void printVector(double[] vector) {
        for (int i = 0; i < vector.length; i++) {
            System.out.printf("[%.2f]", vector[i]);
        }
        System.out.println("\n");
    }
    public static void printMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            printVector(m[i]);
        }
        System.out.println("\n");
    }

    public static void printVector(double[] vector, int each) {
        for (int i = 0; i < vector.length; i++) {
            System.out.printf("[%.2f]", vector[i]);
            if ((i + 1) % each == 0) {
                System.out.println("");
            }
        }
        System.out.println("");
    }

    public static void printVector(int[] vector, int each) {
        for (int i = 0; i < vector.length; i++) {
            System.out.printf("[%d]", vector[i]);
            if ((i + 1) % each == 0) {
                System.out.println("");
            }
        }
        System.out.println("");
    }

    public static double[] extractRow(double[][] dataset, int row) {
        //System.out.println("Extracting row...\n");
        double[] tmpArray = new double[dataset[row].length];
        System.arraycopy(dataset[row], 0, tmpArray, 0, dataset[row].length);
        return tmpArray;
    }

    public static int[] extractRow(int[][] dataset, int row) {
        //System.out.println("Extracting row...\n");
        int[] tmpArray = new int[dataset[row].length];
        System.arraycopy(dataset[row], 0, tmpArray, 0, dataset[row].length);
        return tmpArray;
    }

    /**
     * result = a-b
     *
     * @param result
     * @param a
     * @param b
     * @return
     */
    public static double[] arrayDiff(double[] a, double[] b) {
        if (a.length != b.length) {
            System.out.println("Arrays have different lenghts!");
        }
        double[] tmp = new double[Math.min(a.length, b.length)];
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            tmp[i] = a[i] - b[i];
        }
        return tmp;
    }

    public static double[] arraySum(double[] a, double[] b) {
        if (a.length != b.length) {
            System.out.println("Arrays have different lenghts!");
        }
        double[] tmp = new double[Math.min(a.length, b.length)];
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            tmp[i] = a[i] + b[i];
        }
        return tmp;
    }

    public static int[] arraySum(int[] a, int[] b) {
        if (a.length != b.length) {
            System.out.println("Arrays have different lenghts!");
        }
        int[] tmp = new int[Math.min(a.length, b.length)];
        for (int i = 0; i < Math.min(a.length, b.length); i++) {
            tmp[i] = a[i] + b[i];
        }
        return tmp;
    }

    public static double[] arrayMultiplyScalar(double[] a, double coefficent) {
        double[] tmp = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            tmp[i] = a[i] * coefficent;
        }
        return tmp;
    }

    public static double arrayDotProduct(double[] a, double[] b) {
        double tmp = 0;
        if (a.length != b.length) {
            try {
                throw new Exception("Cannot compute dot product of vector with different length");
            } catch (Exception ex) {
                Logger.getLogger(Utility.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        for (int i = 0; i < a.length; i++) {
            tmp += a[i] * b[i];
        }
        return tmp;
    }

    public static double arrayDotProduct(double[] a, List<Double> b) {
        double tmp = 0;
        if (a.length != b.size()) {
            try {
                throw new Exception("Cannot compute dot product of vector with different length");
            } catch (Exception ex) {
                Logger.getLogger(Utility.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        for (int i = 0; i < a.length; i++) {
            tmp += a[i] * b.get(i);
        }
        return tmp;
    }

    public static double[] arrayIntToDouble(int[] a) {
        double[] tmp = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            tmp[i] = a[i];
        }
        return tmp;
    }

    public static double[] arrayNormalize(double[] array) {
        //calculate sum of squares / lenght of array
        double sum = DoubleStream.of(array).map(n -> n * n).sum();
        sum = Math.sqrt(sum);

        //if array is all zeros than there is nothing to normalize
        if (sum == 0) {
            return array;
        }

        double[] tmp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            tmp[i] = array[i] / sum;
        }
        return tmp;
    }

    public static double[] arrayNormalize(int[] array) {
        //calculate sum of squares / lenght of array
        double sum = IntStream.of(array).map(n -> n * n).sum();
        sum = Math.sqrt(sum);

        //if sum is 0 then just return the input as array of double
        if (sum == 0) {
            return arrayIntToDouble(array);
        }

        double[] tmp = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            tmp[i] = array[i] / sum;
        }
        return tmp;
    }

    public static double[] insertNoise(double[] input, int numberOfNoiseBits) {
        double[] tmp = new double[input.length];
        System.arraycopy(input, 0, tmp, 0, input.length);

        int index;
        //for each noise bit to itroduce
        for (int i = 0; i < numberOfNoiseBits; i++) {
            //select one random bit in the vector and reverse it
            index = RandomGenerator.randomInRange(0, input.length - 1);
            tmp[index] = (tmp[index] == 0) ? 1 : 0; //reverse the bit
        }
        return tmp;
    }

    static double[][] getRows(double[] in) {
        int n = (int) Math.sqrt(in.length);
        double[][] tmp = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(in, n * i, tmp[i], 0, n);
        }
        return tmp;
    }

    static double[][] getCols(double[] in) {
        int n = (int) Math.sqrt(in.length);
        double[][] tmp = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tmp[i][j] = in[i + j * n];
            }
        }
        return tmp;
    }

    /**
     * please make a safe call
     *
     * @param rows
     * @param cols
     * @return
     */
    static double[][] cbind(double[][] rows, double[][] cols) {
        if (rows[0].length != cols[0].length) {
            try {
                throw new Exception("Ma che cazzo fai");
            } catch (Exception ex) {
                Logger.getLogger(Utility.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        double[][] tmp = new double[rows.length + cols.length][rows[0].length];
        int i = 0;
        for (; i < rows.length; i++) {
            System.arraycopy(rows[i], 0, tmp[i], 0, rows[i].length);
        }
        int j = 0;
        for (; j < cols.length; j++, i++) {
            System.arraycopy(cols[j], 0, tmp[i], 0, cols[j].length);

        }
        return tmp;
    }
}

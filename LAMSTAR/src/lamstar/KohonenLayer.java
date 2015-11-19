/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamstar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Umberto
 */
public class KohonenLayer {

    private List<Neuron> neurons = new ArrayList<>();
    private final double MAX_ERROR = 0.3;
    private double[] output;

    public void train(double[] input) {
        int index = 0;
        double max = -10000;
        int maxIndex = -1;

        //normalize input
        double[] inputNormalized = Utility.arrayNormalize(input);
        //System.out.println("vector normalized");
        //Utility.printVector(input);
        //skip this training if vector of zeros
//        if (allZeros(inputNormalized)) {
//            System.out.println("all zeros here");
//            return;
//        }

        //check best performing neuron if any
        for (Neuron neu : neurons) {
            double tmp = neu.feedForward(inputNormalized);
            //System.out.printf("i=%d,error is %.2f\n", index, 1 - tmp);
            if (tmp > max) {
                maxIndex = index;
                max = tmp;
            }
            index++;
        }
        //if no neuron find or not enough approximating
        if ((1 - max) > MAX_ERROR || maxIndex == -1) {
          //  System.out.println("No neurons find, creating a new one");
            neurons.add(new Neuron(inputNormalized));
            neurons.get(neurons.size() - 1).feedForward(inputNormalized);
        } else {
            //train the neuron to recognize this input
           // System.out.printf("Neuron %d find, training\n", maxIndex);
            neurons.get(maxIndex).train(inputNormalized);
        }

        setOutput();

        //printRawOutput(inputs);
    }

    public double[] getOutput() {
        return this.output;
    }

    public void test(double[] input) {
        int index = 0;
        double max = -10000;
        int maxIndex = -1;

        //normalize input
        double[] inputNormalized = Utility.arrayNormalize(input);

        //check best performing neuron
        for (Neuron neu : neurons) {
            double tmp = neu.feedForward(inputNormalized);
          //  System.out.printf("i=%d,error is %.2f\n", index, 1 - tmp);
            if (tmp > max) {
                maxIndex = index;
                max = tmp;
            }
            index++;
        }

        //put all output at 0 and only the maximum at 1
        this.output = new double[neurons.size()];
        Arrays.fill(output, 0);
        output[maxIndex] = 1;
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    private boolean allZeros(double[] inputNormalized) {
        for (double n : inputNormalized) {
            if (n != 0) {
                return false;
            }
        }
        return true;
    }

    private void setOutput() {
        double max = -100;
        int maxIndex = -100;
        int index = 0;

        for (Neuron n : neurons) {
            //System.out.printf("%.2f\n", n.getY());

            if (n.getY() > max) {
                max = n.getY();
                maxIndex = index;
            //    System.out.printf("found max at neruon %d\n", maxIndex);
            }
            index++;
        }
        //System.out.println("fine");
        //put all output at 0 and only the maximum at 1
        this.output = new double[neurons.size()];
        Arrays.fill(output, 0);
        output[maxIndex] = 1;
    }
}

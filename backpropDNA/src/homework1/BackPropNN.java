/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homework1;

import static java.lang.Math.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Umberto
 */
public class BackPropNN {

    private List<Layer> layerList;
    private double LEARNING_RATE = 0.1;

    public void addLayer(int numberOfOutputs, int numberOfInputs) {
        //create layer
        Layer tmpLay = new Layer(numberOfOutputs, numberOfInputs);
        tmpLay.setIndex(layerList.size());
        //if layerlist has already layers adjust connections
        if (!layerList.isEmpty()) {
            Layer prevLayer = layerList.get(layerList.size() - 1);
            prevLayer.setNextLayer(tmpLay);
            tmpLay.setPreviousLayer(prevLayer);
        }
        layerList.add(tmpLay);
    }

    public BackPropNN() {
        layerList = new ArrayList<>();
    }

    public void train(double[][] input, int numberOfDataset,
                      double[][] datatrue, int numberOfEpochs, boolean silent) {
        double min = 1000000; //min error achieved
        double sumError = 0.0;
        float success = 0;

        for (int epoch = 0; epoch < numberOfEpochs; epoch++) {
            //feedForward phase
            //for each dataset
            if (!silent) {
                //    System.out.printf("epoch:%d\n", epoch);
            }
            sumError = 0.0;
            success = 0;
            for (int i = 0; i < numberOfDataset; i++) {
                //System.out.printf("\ninput n: %d\n", i);
                layerList.get(0).feedForward(extractRow(input, i));

                //error detection
                double[] error = new double[datatrue[0].length];
                double[] output;

                output = layerList.get(layerList.size() - 1).getOutput();

                // System.out.printf("ERROR is: [");
                for (int j = 0; j < datatrue[0].length; j++) {
                    error[j] = datatrue[i][j] - output[j];
                    sumError += pow(error[j], 2);
                    //    System.out.printf("(%f)", error[j]);
                }
                // System.out.print("]");
                if (abs(error[0]) < 0.1 && abs(error[1]) < 0.1 && abs(error[2]) < 0.1 && abs(error[3]) < 0.1) {
                    //      System.out.println("--RECOGNIZED--");
                    success++;
                } else {
                    //      System.out.println("--NOT RECOGNIZED--");
                }

                //backpropagation
                layerList.get(layerList.size() - 1).
                        backProp(extractRow(datatrue, i));

                //update weights
                layerList.get(0).updateWeights(LEARNING_RATE);
            }

            min = min(sumError / 2, min);
            //updateLearningRate(epoch);
        }
//        for (int i = 0; i < layerList.size(); i++) {
//            layerList.get(i).printWeights();
//        }
        if (!silent) {
            System.out.printf("Train Success is:%f / %d =  %f\n", success, numberOfDataset, (success / numberOfDataset));
            // System.out.printf("Average error is:%f\n", sumError / 2);
        }
//        if (!silent) {
//            System.out.printf("min was: %f\n", min);
//        }

    }

    public void test(double[][] input, int numberOfTestset, double[][] datatrue) {
        float sum = 0;
        for (int i = 0; i < numberOfTestset; i++) {
            //System.out.printf("\nTest n: %d\n", i);
            double[] row = extractRow(input, i);
            sum += singleTest(row, i, datatrue);
        }
        System.out.printf("Test Success is:%f / %d =  %f\n", sum, numberOfTestset, (sum / numberOfTestset));

    }

    private int singleTest(double[] input, int indexOfTestToRun, double[][] datatrue) {
        layerList.get(0).feedForward(input);

        //error detection
        double[] error = new double[datatrue[0].length];
        double[] output = new double[datatrue[0].length];

        output = layerList.get(layerList.size() - 1).getOutput();

        //   System.out.printf("ERROR is: [");
        for (int j = 0; j < datatrue[0].length; j++) {
            error[j] = datatrue[indexOfTestToRun][j] - output[j];
            // System.out.printf("(%f)", error[j]);
        }
        //  System.out.println("]");
        if (abs(error[0]) < 0.1 && abs(error[1]) < 0.1 && abs(error[2]) < 0.1 && abs(error[3]) < 0.1) {
            //      System.out.println("--RECOGNIZED--");
            //success++;
            return 1;
        } else {
            //      System.out.println("--NOT RECOGNIZED--");
            return 0;
        }

    }

    public void robustnessTest(double[][] input, int numberOfDataset, double[][] datatrue) {
        int[] outcome = new int[numberOfDataset];
        oneBitDistortion(numberOfDataset, outcome, input, datatrue);
        twoBitDistortion(numberOfDataset, outcome, input, datatrue);
        threeBitDistortion(numberOfDataset, outcome, input, datatrue);
    }

    private void threeBitDistortion(int numberOfDataset, int[] outcome, double[][] input, double[][] datatrue) {
        //three bit distortion
        System.out.println("\n3 bit distortion");
        for (int t = 0; t < numberOfDataset; t++) {
            outcome[t] = 0;
            //clone input vector
            double[] box = extractRow(input, t);
            //printVector(box);
            //make 2 bit distortion  
            for (int i = 0; i < input[0].length - 2; i++) {
                box[i] = (input[t][i] == 0) ? 1 : 0; //change the first bit                
                //printVector(box);
                for (int j = i + 1; j < input[0].length - 1; j++) {
                    box[j] = (input[t][j] == 0) ? 1 : 0; //change the second bit
                    //printVector(box);
                    for (int k = j + 1; k < input[0].length; k++) {
                        box[k] = (input[t][k] == 0) ? 1 : 0; //change third bit
                        //printVector(box);
                        outcome[t] += singleTest(box, t, datatrue);
                        //reset the third bit
                        box[k] = (box[k] == 0) ? 1 : 0;
                    }
                    //reset the second bit
                    box[j] = (box[j] == 0) ? 1 : 0;
                    //printVector(box);

                }
                //reset the vector to the original
                box = extractRow(input, t);
            }
            System.out.
                    printf("Test %d: %d/%d = %.2f%%\n", t, outcome[t], 41664, (float) (outcome[t]) / (float) (41664) * 100);
            //41664=combination of three bits over 64 bits
        }
    }

    private void twoBitDistortion(int numberOfDataset, int[] outcome, double[][] input, double[][] datatrue) {
        //two bit distortion
        System.out.println("\n2 bit distortion");
        for (int t = 0; t < numberOfDataset; t++) {
            outcome[t] = 0;
            //clone input vector
            double[] box = extractRow(input, t);
            //printVector(box);
            //make 2 bit distortion  
            for (int i = 0; i < input[0].length - 1; i++) {
                box[i] = (input[t][i] == 0) ? 1 : 0; //change the bit                
                //printVector(box);
                for (int j = i + 1; j < input[0].length; j++) {
                    box[j] = (input[t][j] == 0) ? 1 : 0; //change the bit
                    //printVector(box);

                    //run the test feedforward
                    outcome[t] += singleTest(box, t, datatrue);

                    //reset the second bit
                    box[j] = (box[j] == 0) ? 1 : 0;
                    //printVector(box);

                }
                //reset the vector to the original
                box = extractRow(input, t);
            }
            System.out.
                    printf("Test %d: %d/%d = %.2f%%\n", t, outcome[t], 2016, (float) (outcome[t]) / (float) (2016) * 100);
            //2016=combination of two bits over 64 bits
        }
    }

    private void oneBitDistortion(int numberOfDataset, int[] outcome, double[][] input, double[][] datatrue) {
        System.out.println("1 bit distortion");
        //for each element in the dataset
        for (int t = 0; t < numberOfDataset; t++) {
            outcome[t] = 0;
            //clone input vector
            double[] box = extractRow(input, t);
            //make 1 bit distortion
            for (int i = 0; i < input[0].length; i++) {
                box[i] = (input[t][i] == 0) ? 1 : 0; //change the bit

                //run the test feedforward
                outcome[t] += singleTest(box, t, datatrue);

                //reset the vector to the original
                box = extractRow(input, t);
            }
            System.out.
                    printf("Test %d: %d/%d = %.2f%%\n", t, outcome[t], input[0].length, (float) (outcome[t]) / (float) (64) * 100);
        }
    }

    private void updateLearningRate(int epoch) {
        if (epoch % 100 == 0) {
            LEARNING_RATE /= 2;
        }
        if (epoch % 400 == 0) {
            LEARNING_RATE = 1.5;
        }
    }

    //**UTILITY FUNCTIONS **//
    private double[] extractRow(double[][] dataset, int row) {
        //System.out.println("Extracting row...\n");
        double[] tmpArray = new double[dataset[row].length];
        for (int i = 0; i < dataset[row].length; i++) {
            tmpArray[i] = dataset[row][i];
            //System.out.printf("%f|",tmpArray[i]);
        }
        return tmpArray;
    }

    private void printVector(double[] input) {
        System.out.println("[");
        for (int i = 0; i < input.length; i++) {
            System.out.printf("%f|", input[i]);
        }
        System.out.println("]\n");

    }

    public void printLayers() {
        System.out.printf("Total number of layers is %d\n", layerList.size());
        for (int i = 0; i < layerList.size(); i++) {
            System.out.printf("Layer %d:\n", i);
            layerList.get(i).printWeights();
        }
    }

    public void printLayerConnections() {
        for (int i = 1; i < layerList.size() - 1; i++) {
            System.out.printf("Layer %d: previous is %d next is %d\n", i,
                              layerList.get(i).getPreviousLayer().getIndex(),
                              layerList.get(i).getNextLayer().getIndex());
        }
    }

}

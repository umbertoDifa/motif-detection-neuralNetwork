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
public class Lamstar {

    //create kohonen layers
    private List<KohonenLayer> kList = new ArrayList<>();
    private final int numberOfOutputNeurons;
    private List<Double>[][] weights;
    private List<Integer>[][] winnerCount;

    private double PUNISH_FACTOR = -0.025;
    private double REWARD_FACTOR = 0.05;
    private double MULTIPLICATION_FACTOR = 20;

    public Lamstar(int numberOfKohonenLayer, int numberOfOutputNeurons) {
        //System.out.printf("Creating LAMSTAR with %d layer and %d output neurons\n", numberOfKohonenLayer, numberOfOutputNeurons);
        for (int i = 0; i < numberOfKohonenLayer; i++) {
            kList.add(new KohonenLayer());
        }
        this.numberOfOutputNeurons = numberOfOutputNeurons;

        //instantiate weights
        this.weights = new ArrayList[numberOfOutputNeurons][numberOfKohonenLayer];
        this.winnerCount = new ArrayList[numberOfOutputNeurons][numberOfKohonenLayer];
    }

    public void train(double[][] trainInput, double[][] labels) {
        int index = 0;
        int success = 0;
        //for each input
        for (double[] in : trainInput) {
            //System.out.printf("Index = %d\n", index);
            //System.out.println("Input vector:");
            //Utility.printVector(in);
            //split it
            //create rows matrix
            double[][] rows;
            //create cols matrix
            double[][] cols;
            rows = Utility.getRows(in);
            cols = Utility.getCols(in);
            // System.out.println("Row vector:");
            // Utility.printMatrix(rows);
            //Utility.printMatrix(cols);

            double[][] all;
            if (kList.size() == rows.length) {
                all = rows;
            } else {
                all = Utility.cbind(rows, cols);
            }
            //System.out.printf("cbind has %d rows each of %d cols\n",all.length,all[0].length);
            //train the kohonens layer
            for (int i = 0; i < kList.size(); i++) {
                //System.out.printf("layer %d\n",i);
                //Utility.printVector(all[i]);
                kList.get(i).train(all[i]);
            }

            int[] s = {0, 0, 0, 0};
            //train output layers
            //for each neuron
            for (int i = 0; i < this.numberOfOutputNeurons; i++) {
                double sum = 0;
                //for each SOM            
                for (int j = 0; j < kList.size(); j++) {
                    //initialize weights if necessary
                    //   System.out.printf("%d\n", j);
                    //   Utility.printVector(kList.get(j).getOutput());
                    this.weights[i][j]
                            = initializeWeigths(this.weights[i][j],
                                                kList.get(j)
                                                .getOutput().length);

                    sum += Utility.arrayDotProduct(kList.get(j).getOutput(), this.weights[i][j]);

                }
                // System.out.printf("sum is %.2f, label[%d][%d] is %.2f\n", sum, index, i, labels[index][i]);
                if (labels[index][i] == 1 && sum > 0 || labels[index][i] == 0 && sum < 0) {
                    //  System.out.println("Got it");
                    s[i]++;
                } else {
                    // System.out.println("Nope");
                }

                if (labels[index][i] == 1) {
                    //       System.out.println("reward pos\n");
                    reward(i, kList, REWARD_FACTOR);
                } else if (labels[index][i] == 0) {
                    //      System.out.println("reward neg\n");
                    reward(i, kList, PUNISH_FACTOR);
                }
            }
            if (s[0] == 1 && s[1] == 1 && s[2] == 1 && s[3] == 1) {
                success++;
            }
            //  printWeights();

            index++;
        }
//        for (int i = 0; i < kList.size(); i++) {
//            System.out.printf("%d neuron in SOM %d\n", kList.get(i).getNeurons().size(), i);
//        }
//        for (KohonenLayer k : kList) {
//            Utility.printVector(k.getOutput());
//            System.out.println("");
//        }
        // System.out.printf("Training success is %d out of %d\n", success, index);
        //  printWeights();
        //  printCounts();

    }

    private List<Double> initializeWeigths(List<Double> weight, int length) {
        if (weight == null) {
            //if never used before, create the arraylist
            weight = new ArrayList<>();
        }
        while (weight.size() != length) {
            //if there are less weights than needed
            //add them and set them to 0            
            weight.add(0.0);
        }
        return weight;
    }

    private void reward(int outputNeuron, List<KohonenLayer> kList, double REWARD_FACTOR) {
        for (int j = 0; j < kList.size(); j++) {
            KohonenLayer k = kList.get(j);
            for (int i = 0; i < k.getOutput().length; i++) {
                if (k.getOutput()[i] == 1) {//i is the winner neuron

                    //update weight
                    this.weights[outputNeuron][j].set(i, this.weights[outputNeuron][j].get(i) + REWARD_FACTOR * MULTIPLICATION_FACTOR);

                    //initialize count
                    if (this.winnerCount[outputNeuron][j] == null) {
                        this.winnerCount[outputNeuron][j] = new ArrayList<>();
                    }

                    while (this.winnerCount[outputNeuron][j].size() != this.weights[outputNeuron][j].size()) {
                        //if there are less weights than needed
                        //add them and set them to 0    
                        this.winnerCount[outputNeuron][j].add(0);
                    }
                    //update count
                    this.winnerCount[outputNeuron][j].set(i, this.winnerCount[outputNeuron][j].get(i) + 1);

                }
            }
        }
    }

    private void printWeights() {
        for (int i = 0; i < this.numberOfOutputNeurons; i++) {
            System.out.printf("neurone %d\n", i);

            for (int j = 0; j < this.kList.size(); j++) {
                for (int k = 0; k < this.weights[i][j].size(); k++) {
                    System.out.printf("[%.2f]", this.weights[i][j].get(k));
                }
                System.out.println("");
            }
        }
    }

    public int[] test(double[][] testInput, double[][] labels) {
        int index = 0;
        int[] success = new int[testInput.length];
        int total = 0;
        Arrays.fill(success, 0); //reset success

        //for each input
        for (double[] in : testInput) {
            //split it
            //create rows matrix
            double[][] rows;
            //create cols matrix
            double[][] cols;
            rows = Utility.getRows(in);
            cols = Utility.getCols(in);

            double[][] all = Utility.cbind(rows, cols);

            //test the kohonens layer
            for (int i = 0; i < kList.size(); i++) {
                kList.get(i).test(all[i]);
            }

//            for (KohonenLayer k : kList) {
//                Utility.printVector(k.getOutput());
//                System.out.println("");
//            }
            int[] s = {0, 0, 0, 0};
            //train output layers
            //for each neuron
            for (int i = 0; i < this.numberOfOutputNeurons; i++) {
                double sum = 0;
                //for each SOM            
                for (int j = 0; j < kList.size(); j++) {
                    sum += Utility.arrayDotProduct(kList.get(j).getOutput(), this.weights[i][j]);
                }
                // System.out.printf("sum is %.2f, label[%d][%d] is %.2f\n", sum, index, i, labels[index][i]);
                if (labels[index][i] == 1 && sum > 0 || labels[index][i] == 0 && sum < 0) {
                    //       System.out.println("Got it");
                    s[i]++;
                } else {
                    //       System.out.println("Nope");
                }
                //DECOMMENT TO LEARN DURING TEST
//                if (labels[index][i] == 1) {
//                    System.out.println("reward pos\n");
//                    reward(i, kList, REWARD_FACTOR);
//                } else if (labels[index][i] == 0) {
//                    System.out.println("reward neg\n");
//                    reward(i, kList, -REWARD_FACTOR);
//                }
            }
            if (s[0] == 1 && s[1] == 1 && s[2] == 1 && s[2] == 1) {
                success[index] = 1;
                total++;
            }

            index++;
        }
//        for (int i =0 ; i < success.length; i++) {
//            System.out.printf("Success input %d is %d\n", i, success[i]);
//        }
        System.out.printf("success is %.2f\n", (float) total / testInput.length);
        return success;
    }

    private void printCounts() {
        for (int i = 0; i < this.numberOfOutputNeurons; i++) {
            System.out.printf("neurone %d\n", i);

            for (int j = 0; j < this.kList.size(); j++) {
                System.out.printf("Layer %d\n", j);
                for (int k = 0; k < this.winnerCount[i][j].size(); k++) {
                    System.out.printf("[%d]", this.winnerCount[i][j].get(k));
                }
                System.out.println("");
            }
        }
    }

    void normalizeWeights() {
        //for each layer
        for (int j = 0; j < kList.size(); j++) {
            KohonenLayer k = kList.get(j);
            //for each neuron in the layer
            for (int i = 0; i < k.getOutput().length; i++) {
                //for each output neuron
                for (int out = 0; out < numberOfOutputNeurons; out++) {
                    //ultimately this is executed for each link weight
                    //from any neuron in the kohonens layer to any output neuron
                    // System.out.printf("%d\n",this.winnerCount[out][j].get(i));
                    int dividend = (this.winnerCount[out][j].get(i) == 0) ? 1 : this.winnerCount[out][j].get(i);
                    //normalize L
                    this.weights[out][j].set(i, this.weights[out][j].get(i) / dividend);

                }
            }
        }
    }
}

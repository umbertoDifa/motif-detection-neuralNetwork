package homework1;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Umberto
 */
public class Main {

    private static final int N_TRAINSET = 2400;
    private static final int N_INPUTS = 529;
    private static final int N_NEURONS_FIRST = 6;
    private static final int N_NEURONS_SECOND = 5;
    private static final int N_NEURONS_OUTPUT = 4;
    private static final int N_TESTSET = 3;
    private static final int NUMBER_OF_EPOCHS = 40;

    private static double[][] trainingData;

    private static double[][] trainingLabels;
   

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //==============import data
        CSVReader csvReaderTrainingData = new CSVReader(new FileReader(new File("trainingData.csv")));
        List<String[]> list = csvReaderTrainingData.readAll();
        // Convert to 2D array
        String[][] dataArr = new String[list.size()][];
        dataArr = list.toArray(dataArr);

        CSVReader csvReaderTrainingLabels = new CSVReader(new FileReader(new File("trainingLabels.csv")));
        List<String[]> listLabels = csvReaderTrainingLabels.readAll();
        // Convert to 2D array
        String[][] dataLabelsArr = new String[listLabels.size()][];
        dataLabelsArr = listLabels.toArray(dataLabelsArr);

        //============convert data from string to float
        trainingData = new double[dataArr.length][dataArr[0].length];
        for (int i = 0; i < dataArr.length; i++) {
            for (int j = 0; j < dataArr[i].length; j++) {
                trainingData[i][j] = Float.parseFloat(dataArr[i][j]);
                //do whatever you want with the float
            }
        }
        trainingLabels = new double[dataLabelsArr.length][dataLabelsArr[0].length];
        for (int i = 0; i < dataLabelsArr.length; i++) {
            for (int j = 0; j < dataLabelsArr[i].length; j++) {
                trainingLabels[i][j] = Float.parseFloat(dataLabelsArr[i][j]);
                //do whatever you want with the float
            }
        }
//        for (int i = 0; i < trainingLabels.length; i++) {
//            for (int j = 0; j < trainingLabels[i].length; j++) {
//                System.out.printf("%f, ", trainingLabels[i][j]);
//            }
//            System.out.println("");
//        }

        BackPropNN nn = new BackPropNN();

        //add first layer with 529 neurons and 529 inputs
        nn.addLayer(N_NEURONS_FIRST, N_INPUTS);
        //second layer
        nn.addLayer(N_NEURONS_SECOND, N_NEURONS_FIRST);
        //third layer
        nn.addLayer(N_NEURONS_OUTPUT, N_NEURONS_SECOND);
       // nn.printLayers();
        //nn.printLayerConnections();
        //System.out.printf("%d - %d\n",datatrue.length,datatrue[0].length);
        nn.train(trainingData, N_TRAINSET, trainingLabels, NUMBER_OF_EPOCHS);
        //nn.test(testSet, N_TESTSET, targetTest);
        //nn.robustnessTest(dataset, N_TRAINSET, datatrue);
        //gatherStats(nn);

    }

    private static void gatherStats(BackPropNN nn) {
        int NUMB_OF_RUNS = 2000;
        long sum;
        for (int e = 1; e * NUMBER_OF_EPOCHS < 2001; e *= 2) {
            sum = 0;
            for (int i = 0; i < NUMB_OF_RUNS; i++) {
                long startTime = System.currentTimeMillis();
                nn.train(trainingData, N_TRAINSET, trainingLabels, NUMBER_OF_EPOCHS * e);
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                sum += totalTime;
            }
            System.out.
                    printf("Execution time for %d epoch is: %d milliseconds\n", e * NUMBER_OF_EPOCHS, sum / NUMB_OF_RUNS);
        }
    }
}

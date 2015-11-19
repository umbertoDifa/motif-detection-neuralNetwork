package lamstar;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.sqrt;
import java.util.List;

/**
 *
 * @author Umberto
 */
public class Main {

    private static double[][] trainingData;

    private static double[][] testData;

    private static double[][] testLabels;

    private static double[][] trainingLabels;

    private static int OUTPUT_NEURONS = 4;
    private static int N_TESTSET;
    private static int N_TRAINSET;

    public static void main(String[] args) throws Exception {
        //==================import test        
        CSVReader csvReaderTestData = new CSVReader(new FileReader(new File("data/testData.csv")));
        List<String[]> testDatalist = csvReaderTestData.readAll();
        // Convert to 2D array
        String[][] testDataArr = new String[testDatalist.size()][];
        testDataArr = testDatalist.toArray(testDataArr);
        testData = new double[testDataArr.length][testDataArr[0].length];

        N_TESTSET = testData.length;
        for (int i = 0; i < testDataArr.length; i++) {
            for (int j = 0; j < testDataArr[i].length; j++) {
                testData[i][j] = Float.parseFloat(testDataArr[i][j]);
            }
        }

        CSVReader csvReaderTestLabels = new CSVReader(new FileReader(new File("data/testLabels.csv")));
        List<String[]> testLabelslist = csvReaderTestLabels.readAll();
        // Convert to 2D array
        String[][] testLabelsArr = new String[testLabelslist.size()][];
        testLabelsArr = testLabelslist.toArray(testLabelsArr);
        testLabels = new double[testLabelsArr.length][testLabelsArr[0].length];
        for (int i = 0; i < testLabelsArr.length; i++) {
            for (int j = 0; j < testLabelsArr[i].length; j++) {
                testLabels[i][j] = Float.parseFloat(testLabelsArr[i][j]);
            }
        }
        boolean fewLayers = true; //uses only rows
        for (int perc = 1; perc <= 10; perc++) {
            loadTrainingData(perc);
            Lamstar lamstar;
            if (fewLayers == true) {
                lamstar = new Lamstar((int) sqrt(trainingData[0].length), OUTPUT_NEURONS);
            } else {
                lamstar = new Lamstar((int) sqrt(trainingData[0].length) * 2, OUTPUT_NEURONS);
            }

            System.out.printf("-------------------Train %d %%\n", perc * 10);
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < 1; i++) {
                lamstar.train(trainingData, trainingLabels);
            }
            lamstar.normalizeWeights();
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.printf("time %d\n", totalTime);
            System.out.println("--------------------Test-------------------------");
            lamstar.test(testData, testLabels);
        }

    }

    private static void loadTrainingData(int k) throws IOException, NumberFormatException, FileNotFoundException {
        //import train
        CSVReader csvReaderTrainingData = new CSVReader(new FileReader(new File("data/trainingData" + (k * 10) + ".csv")));
        List<String[]> list = csvReaderTrainingData.readAll();
        // Convert to 2D array
        String[][] dataArr = new String[list.size()][];
        dataArr = list.toArray(dataArr);

        CSVReader csvReaderTrainingLabels = new CSVReader(new FileReader(new File("data/trainingLabels" + (k * 10) + ".csv")));
        List<String[]> listLabels = csvReaderTrainingLabels.readAll();
        // Convert to 2D array
        String[][] dataLabelsArr = new String[listLabels.size()][];
        dataLabelsArr = listLabels.toArray(dataLabelsArr);

        //============convert data from string to float
        trainingData = new double[dataArr.length][dataArr[0].length];
        N_TRAINSET = trainingData.length;

        for (int i = 0; i < dataArr.length; i++) {
            for (int j = 0; j < dataArr[i].length; j++) {
                trainingData[i][j] = Float.parseFloat(dataArr[i][j]);
            }
        }
        trainingLabels = new double[dataLabelsArr.length][dataLabelsArr[0].length];
        for (int i = 0; i < dataLabelsArr.length; i++) {
            for (int j = 0; j < dataLabelsArr[i].length; j++) {
                trainingLabels[i][j] = Float.parseFloat(dataLabelsArr[i][j]);
            }
        }
    }
}

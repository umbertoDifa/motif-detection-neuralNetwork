package lamstar;

/**
 *
 * @author Umberto
 */
public class Neuron {

    private double[] weights; //weights of the neuron in the kohonen layer
    private double z;
    private double y;
    private double ALPHA = 0.1; //learning coefficent for weights

    public Neuron(double[] input) {
        //when neuron is created the weights are the same as the input because
        //they maximize the dot product
        this.weights = input;
    }

    public void train(double[] input) {
        //w(n+1) = w(n) + apha * ( x - w)
        double[] delta = Utility.arrayDiff(input, weights);

        double[] newWeights = Utility.arraySum(Utility.
                arrayMultiplyScalar(delta, ALPHA), weights);

        weights = Utility.arrayNormalize(newWeights); //re-normalize vector
    }

    public double feedForward(double[] input) {
        //Utility.printVector(input);
        this.z = Utility.arrayDotProduct(input, weights);
        //System.out.printf("z is %f", this.z);
        this.y = activate(this.z);
        return this.y;
    }

    private double activate(double value) {
        //there's nothing to activate in the kohonen
        return value;
    }

    public double getY() {
        return y;
    }

    public double[] getWeights() {
        return weights;
    }

}

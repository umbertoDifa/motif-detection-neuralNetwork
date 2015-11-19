/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lamstar;

import java.util.Random;

/**
 *
 * @author Umberto
 */
public class RandomGenerator {

    private static Random random = new Random();

    public static int randomInRange(int min, int max) {
        int randomNum = random.nextInt((max - min)) + min;
        return randomNum;
    }
}

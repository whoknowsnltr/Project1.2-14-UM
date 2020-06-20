package com.mygdx.game.gameAdditions;

import com.mygdx.game.MyActor;
/*
Class that allows for slight variation in friction coefficient
 */
public class DifferentFriction {
    public static int Height = 640 * 2;
    public static int Width = 640 * 8 / 3;
    private double[][] frictionValues = new double[Width][Height];
    private double friction;
    public DifferentFriction(double friction){
        this.friction=friction;
    }
    public void setFrictionValues(double probabilityOfDifferentFriction){
        double chances = Math.random();
            for (int i = 0; i<frictionValues.length;i++) {
                for (int j = 0; j<frictionValues[0].length; j++){
                    frictionValues[i][j] = friction;
                    if (chances<=probabilityOfDifferentFriction) {
                        double isPlusOrMinus = Math.random();
                        if (isPlusOrMinus>0.5)
                            frictionValues[i][j] = friction + (Math.random() / 2);
                        else
                            frictionValues[i][j] = friction - (Math.random() / 2);
                    }
                }
            }

    }

    public double[][] getFrictionValues() {
        return frictionValues;
    }
}

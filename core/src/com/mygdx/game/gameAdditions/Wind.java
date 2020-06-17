package com.mygdx.game.gameAdditions;

import com.mygdx.physics.Vector2d;
/*
Class that creates a vector for wind, that will be added to the current shot with a given probability
Preferable probability: low, ie 1-10%, as to not disturb the game too much
 */
public class Wind {
    private double probabilityOfWind;
    private Vector2d velocity;

    /**
     * Creator of Wind class
     * @param velocity - velocity of ball hit
     * @param probabilityOfWind - a probability of wind happening, input 0.1 means 10% probability. In range 0:1
     */
    public Wind(Vector2d velocity, double probabilityOfWind){
        this.velocity=velocity;
        this.probabilityOfWind = probabilityOfWind;
        if (probabilityOfWind>1){
            this.probabilityOfWind=1;
        }
    }

    /**
     * Method to create wind vector
     * @return vector that represents wind
     */
    private Vector2d createWind(){
        double probability = Math.random();
        if (probability>probabilityOfWind){
            return new Vector2d(0,0);
        }
        double xAxis = Math.random()/2;
        double yAxis = Math.random()/2;
        double xAxisSign = Math.random();
        double yAxisSign = Math.random();
        // Allow negative direction of wind
        if (xAxisSign>0.5){
            xAxis= -1*xAxis;
        }
        if (yAxisSign>0.5){
            yAxis= -1*yAxis;
        }
        return new Vector2d(xAxis,yAxis);
    }
    public Vector2d applyWindToVelocity(){
        double x = velocity.get_x();
        double y = velocity.get_y();
        Vector2d wind = createWind();
        double xWind = wind.get_x();
        double yWind = wind.get_y();
        return new Vector2d(x+xWind,y+yWind);
    }
}

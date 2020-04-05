package com.mygdx.physics;

public interface Function2d {
    /**
     * This method will allow us to get height in a given place on the map. Eg: h = x^2+y+5
     * @param p - a vector that inputs specific x's and y's (height changes, co we can compute height only in a specific place at a time
     * @return height
     */
    public double evaluate(Vector2d p);
    /**
     * Is this method supposed to return an angle at which the surface is ?
     * @param p
     * @return
     */
    public Vector2d gradient(Vector2d p);


}

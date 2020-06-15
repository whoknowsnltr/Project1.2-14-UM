package com.mygdx.physics;

public class Vector2d implements TerrainCoordinate {
    double x;
    double y;
    public Vector2d(double x, double y){
        this.x = x;
        this.y = y;
    }
    public double get_x(){
        return x;
    }
    public double get_y(){
        return y;
    }
    public double getScalar(){return Math.sqrt(x*x+y*y);
}
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}

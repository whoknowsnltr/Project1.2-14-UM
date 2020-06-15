package com.mygdx.game.bot;

import com.mygdx.physics.TerrainCoordinate;

public class Node extends Node1 implements TerrainCoordinate  {
    public Node1 parent;
    public int x;
    public int y;
    public double g;
    public double h;
    public Node(){};
    public Node(Node1 parent, int x, int y, double g, double h) {
        super(parent,x,y,g,h);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
    }
    @Override
    public double get_x() {
        return x;
    }

    @Override
    public double get_y() {
        return y;
    }
}

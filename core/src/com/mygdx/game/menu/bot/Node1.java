package com.mygdx.game.bot;

public class Node1 implements Comparable {
    public Node1 parent;
    public int x;
    public int y;
    public double g;
    public double h;
    Node1(){};
    Node1(Node1 parent, int x, int y, double g, double h) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.g = g;
        this.h = h;
    }
    @Override
    public int compareTo(Object object) {
        Node1 object1 = (Node1) object;
        int result = (int)((this.g + this.h) - (object1.g + object1.h));
        return result;
    }

}

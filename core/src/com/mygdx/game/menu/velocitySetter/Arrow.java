package com.mygdx.game.menu.velocitySetter;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.*;
import java.awt.geom.*;

public class Arrow{

    private static Double xLeft;
    private static Double yTop;
    public static Point2D.Double r1;
    public static Point2D.Double r2;
    public static Point2D.Double r3;
    public static Point2D.Double r4;

    public static Line2D a;


    public Arrow(Double x, Double y){
        xLeft = x;
        yTop = y;
    }
    public static void draw(Graphics2D g2){
        r1 = new Point2D.Double(xLeft, yTop);
        r2 = new Point2D.Double(xLeft+50, yTop);
        r3 = new Point2D.Double(xLeft + 40, yTop+10);
        r4 = new Point2D.Double(xLeft + 40, yTop-10);


        a = new Line2D.Double(r1, r2);
        Line2D.Double B = new Line2D.Double(r2, r3);
        Line2D.Double C = new Line2D.Double(r2, r4);

        g2.draw(a);
        g2.draw(B);
        g2.draw(C);
    }

}

package com.mygdx.game.menu.velocitySetter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;


public class ClickListenerLeft implements ActionListener {

    public void actionPerformed(ActionEvent event) { //whenever the action is performed the counter increases and the output appears

        Arrow.r1.setLocation(Arrow.r1.getX()+5, Arrow.r1.getY()+5);
        Arrow.r2.setLocation(Arrow.r2.getX()+5, Arrow.r2.getY()+5);

        System.out.println("trs");

    }
}
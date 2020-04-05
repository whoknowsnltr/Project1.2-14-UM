package com.mygdx.game.menu.velocitySetter;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class ArrowComponent extends JComponent {


    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        Arrow arrow = new Arrow(140.7, 140.8);
        Arrow.draw(g2);
    }
}
package com.mygdx.game.menu.velocitySetter;


import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.*;

public class ArrowViewer {
    public static void main(String[] args) {

        JFrame frame = new JFrame();

        final int FRAME_WIDTH = 250;
        final int FRAME_HEIGHT = 250;

        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ArrowComponent component = new ArrowComponent();

        JButton boutonA = new JButton("Left");
        boutonA.setBounds(20, 20, 20, 20);
        JButton boutonB = new JButton("Right");
        boutonB.setBounds(20, 20, 20, 20);

        JPanel controlPanel = new JPanel();
        controlPanel.setBounds(0, 0, 250, 125);
        controlPanel.add(component);
        controlPanel.add(boutonA);
        controlPanel.add(boutonB);

        ActionListener listener = new ClickListenerLeft();
        ActionListener listener1 = new ClickListenerRight();

        boutonA.addActionListener(listener);
        boutonB.addActionListener(listener1);

        frame.add(controlPanel);
        frame.add(component);
        frame.setVisible(true);
    }
}



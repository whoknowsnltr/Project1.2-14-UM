package com.mygdx.game.menu.velocitySetter;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

public class ProjectArrow implements ActionListener {

    // double velocity=0;
    static JFrame frame = new JFrame("Arrow test");
    public static void main(String[] args) {

        final int WIDTH = 500;
        final int HEIGHT = 100;

        JPanel panel = new JPanel();

        frame.setSize(WIDTH, HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSlider slider = new JSlider(0,50,1);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        //slider.setPaintLabels(true);
        panel.add(slider);
        ActionListener listener = new ClickL();
        JButton button = new JButton("OK");
        button.addActionListener(listener);
        panel.add(button);
        frame.add(panel);
        frame.setVisible(true);

    }


    @Override
    public void actionPerformed(ActionEvent e) {

    }
}

package com.mygdx.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.mygdx.game.MyActor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class PuttingCourse implements Function2d {
    ArrayList<Vector2d>  move;
    Function2d height;
    Vector2d flag;
    Vector2d start;
    double friction_coefficient;
    double maximum_velocity;
    double hole_tolerance;
    double gravitationalAcceleration;
    double mass;
    double gravity=9.8;
    public String formula="";
    public PuttingCourse(Function2d height, Vector2d flag, Vector2d start, String formula, double friction_coefficient, double maximum_velocity, double hole_tolerance, double
                         gravitationalAcceleration, double mass){
        this.height = height; // Is height a formula? eg height = sin(x) + 2y?
        this.flag = flag;
        this.start = start;
        this.formula = formula;
        this.friction_coefficient = friction_coefficient;
        this.maximum_velocity = maximum_velocity;
        this.hole_tolerance = hole_tolerance;
        this.gravitationalAcceleration = gravitationalAcceleration;
        this.mass=mass;
        move = new ArrayList<>();
    }
    public PuttingCourse(){move = new ArrayList<>();};
    public Function2d get_height(){
        return height;
    } // This method should return a formula then??
    public Vector2d get_flag_position(){
        return flag;
    }
    public Vector2d get_start_position(){
        return start;
    }
    public double get_friction_coefficient(){
        return friction_coefficient;
    }
    public double get_maximum_velocity(){
        return  maximum_velocity;
    }
    public double get_hole_tolerance(){
        return hole_tolerance;
    }
    public double get_gravitational_acceleration(){return gravitationalAcceleration;}
    public ArrayList<Vector2d> getMove(){return move;}

// Methods from interface
    // Compute height for a given point on the field
    @Override
    public double evaluate(Vector2d p) {
        double x = p.get_x();
        double y = p.get_y();
       // ...
        return 0;
    }
    // Compute angle of surface at given point on the field in comparison to parallel
    @Override
    public Vector2d gradient(Vector2d p) {
      //  ...
        return null;
    }
    public double get_gravity(){return gravity;}
    public void readFile(String name) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            String st = "";


            while ((st = br.readLine()) != null) {
                String[] splited = st.split("\\s+");
                for (int i = 0; i < splited.length; i++) {
                    if (splited[i].equals("g")) {
                        i+=2;
                        String s = splited[i];
                        gravitationalAcceleration = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("m")) {
                        i+=2;
                        String s = splited[i];
                        mass = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("mu")) {
                        i+=2;
                        String s = splited[i];
                        friction_coefficient = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("vmax")) {
                        i+=2;
                        String s = splited[i];
                        maximum_velocity = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("tol")) {
                        i+=2;
                        String s = splited[i];
                        hole_tolerance = Double.parseDouble(s.substring(0, s.length()-1));
                    }
                    if (splited[i].equals("start")) {
                        i+=2;
                        String s = splited[i].substring(1, splited[i].length()-1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length()-2);
                        start = new Vector2d(Double.parseDouble(s), Double.parseDouble(p));
                    }
                    if (splited[i].equals("goal")) {
                        i+=2;
                        String s = splited[i].substring(1, splited[i].length()-1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length()-2);
                        flag = new Vector2d(Double.parseDouble(s), Double.parseDouble(p));
                    }
                    if (splited[i].equals("height")) {
                        i+=2;
                        for (int j = i; j<splited.length; j++){
                            formula+=splited[j];
                            formula+=" ";
                        }
                        formula = formula.substring(0, formula.length()-1);
                    }
                }
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
    public void readMoves(String name) {
        move = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            String st = "";


            while ((st = br.readLine()) != null) {
                String[] splited = st.split("\\s+");
                for (int i = 0; i < splited.length; i++) {
                        String s = splited[i].substring(1, splited[i].length()-1);
                        i++;
                        String p = splited[i].substring(0, splited[i].length()-2);
                        move.add( new Vector2d(Double.parseDouble(s), Double.parseDouble(p)));

                }
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
}

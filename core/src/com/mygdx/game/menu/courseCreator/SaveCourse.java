package com.mygdx.game.menu.courseCreator;


import com.mygdx.game.MyActor;

import java.io.*;
import java.util.ArrayList;

/**
 * This class saves course parameters  to a text file
 */

public class SaveCourse {
    ArrayList<MyActor> ponds = new ArrayList<>();
    ArrayList<MyActor> sands = new ArrayList<>();
    ArrayList<MyActor> trees = new ArrayList<>();
    int[] holeCoords;
    int[] ballCoords;
    String formula;
    double friction_coefficient;
    double maximum_velocity;
    double hole_tolerance;
    double gravitationalAcceleration;
    double mass_of_ball;
    public SaveCourse(    double friction_coefficient,
            double maximum_velocity,
            double hole_tolerance,
            double gravitationalAcceleration,
            double mass_of_ball, ArrayList<MyActor> ponds, ArrayList<MyActor> sands, ArrayList<MyActor> trees,int[] holeCoords, int[] ballCoords,String formula           ){
        this.ponds = ponds;
        this.sands = sands;
        this.trees = trees;
        this.holeCoords = holeCoords;
        this.ballCoords = ballCoords;
        this.formula = formula;
        this.friction_coefficient = friction_coefficient;
        this.maximum_velocity = maximum_velocity;
        this.hole_tolerance = hole_tolerance;
        this.gravitationalAcceleration = gravitationalAcceleration;
        this.mass_of_ball = mass_of_ball;
    }

    /**
     * We create a new file with a name, and in this file we store info about what components are where on the course (how the course looks like)
     * @param name
     */
    public void saveToFile(String name){
        // If user names file ""MyCourse", the file needs to be saved as "MyCourse.txt"
        name += ".txt";
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(name), "utf-8"))) {
            writer.write(filecontent());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * We save in a text file a name of object and where on the screen it is
     * @return
     */
    public String filecontent(){
        String string = "";

        string += "goal =";
        string+=" ";
        string+="("+holeCoords[0]+", " + holeCoords[1]+");";
        string+=" ";
        string += "tol =";
        string+=" ";
        string+=holeCoords[2]+";";
        string+=" ";
        string += "start =";
        string+=" ";
        string+="("+ballCoords[0]+", " + ballCoords[1]+");";
        string+=" ";
        string+="g = ";
        string+=" ";
        string+=gravitationalAcceleration+";";
        string+=" ";
        string+="mu = ";
        string+=" ";
        string+=friction_coefficient+";";
        string+=" ";
        string+="m = ";
        string+=" ";
        string+=mass_of_ball+";";
        string+=" ";
        string+="tol = ";
        string+=" ";
        string+=hole_tolerance+";";
        string+=" ";
        string+="vmax = ";
        string+=" ";
        string+=maximum_velocity+"; ";

        for (MyActor pond : ponds){
            string += "Pond";
            string+=" ";
            string+=pond.getXcoords();
            string+=" ";
            string+=pond.getYcoords();
            string+=" ";
        }
        for (MyActor sand : sands){
            string += "Sand";
            string+=" ";
            string+=sand.getXcoords();
            string+=" ";
            string+=sand.getYcoords();
            string+=" ";
        }
        for (MyActor tree : trees){
            string += "Tree";
            string+=" ";
            string+=tree.getXcoords();
            string+=" ";
            string+=tree.getYcoords();
            string+=" ";
        }
        string+="height = ";
        string+=" ";
        string+=formula+";";
        return string;
    }

}

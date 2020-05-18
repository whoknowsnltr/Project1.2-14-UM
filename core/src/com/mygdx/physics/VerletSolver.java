package com.mygdx.physics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyActor;

import javax.imageio.stream.ImageInputStream;
import java.util.Vector;

public class VerletSolver {
    private double h1;
    private Vector2d velocity;
    private Vector2d acceleration;
    private Vector2d position;
    private Vector2d previousAcceleration;
    private PuttingCourse puttingCourse;



    public VerletSolver( double h1,PuttingCourse course){
        this.h1=h1;

        this.puttingCourse = course;
        this.position = course.get_start_position();
    }

    public Vector2d velocityCalculator(){

        double vX = velocity.get_x() + (previousAcceleration.get_x() + acceleration.get_x())*(h1*0.5);
        double vY = velocity.get_y() + (previousAcceleration.get_y() + acceleration.get_y())*(h1*0.5);
        return new Vector2d(vX,vY);
    }
    public Vector2d verletPlacementCalculator(){

        double posX = (position.get_x() + velocity.get_x()*h1 + 0.5*acceleration.get_x()*h1*h1);
        double posY = (position.get_y() + velocity.get_y()*h1 + 0.5*acceleration.get_y()*h1*h1);
        return new Vector2d(posX,posY);
    }

    public Vector2d accelerationCalculator(Vector2d velocity){
        double aX ,aY;
        double mu = puttingCourse.get_friction_coefficient();
        double g = puttingCourse.get_gravity();
        Vector2d gradient = puttingCourse.get_height().gradient(position);
        aX = (-g*(gradient.get_x())) - (mu*g*velocity.get_x()/velocity.getScalar());
        aY = (-g*(gradient.get_y())) - (mu*g*velocity.get_y()/velocity.getScalar());
        return new Vector2d(aX,aY);

    }

    public void giveVel(Vector2d initial_ball_velocity){

        this.velocity = initial_ball_velocity;
        previousAcceleration = new Vector2d(0,0);
        acceleration = new Vector2d(0,0);
        Vector2d velocityToStop = new Vector2d(0.01,0.01);
        boolean goOn = true;
        while(goOn){
            Vector2d tempAcc = acceleration;
            acceleration = accelerationCalculator(velocity);
            previousAcceleration = tempAcc;
            Vector2d prePos=position;
            position = verletPlacementCalculator();
            System.out.println(" ");
            System.out.println("VELOCITY:"+velocity.getScalar());
            velocity =  velocityCalculator();
            if(velocity.getScalar()<velocityToStop.getScalar() && acceleration.getScalar()< accelerationCalculator(velocityToStop).getScalar()){
                goOn = false;
            }
        }
    }

//    public static void main(String[] args) {
//        PuttingCourse pt=new PuttingCourse();
//        pt.readFile("//Users//ziynetarslanoglu//IdeaProjects//Project1-2.2-14-UM-masterVeryLastVersion//core//src//com//mygdx//physics//testerForSolvers.txt");
//        VerletSolver vs = new VerletSolver(0.01,pt);
//        vs.giveVel(new Vector2d(3,3));
//    }
}

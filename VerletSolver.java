package com.mygdx.physics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyActor;

import java.util.Vector;

public class VerletSolver {
    private EulerSolver eulerSolver;
    private Vector2d velocity;
    private Vector2d acceleration;
    private Vector2d position;
    private Vector2d previousAcceleration;
    private PuttingCourse puttingCourse;

    MyActor ball;

    public VerletSolver(EulerSolver engine, PuttingCourse course,MyActor ball){
        this.eulerSolver = engine;
        this.ball=ball;
        this.puttingCourse = course;
        this.position = course.get_start_position();
    }

    public Vector2d velocityCalculator(){
        double h = eulerSolver.get_step_size();
        double vX = velocity.get_x() + (previousAcceleration.get_x() + acceleration.get_x())*(h*0.5);
        double vY = velocity.get_y() + (previousAcceleration.get_y() + acceleration.get_y())*(h*0.5);
        return new Vector2d(vX,vY);
    }
    public Vector2d verletPlacementCalculator(){
        double h = eulerSolver.get_step_size();
        double posX = (position.get_x() + velocity.get_x()*h + 0.5*acceleration.get_x()*h*h);
        double posY = (position.get_y() + velocity.get_y()*h + 0.5*acceleration.get_y()*h*h);
        return new Vector2d(posX,posY);
    }

    public Vector2d accelerationCalculator(Vector2d velocity){
        double aX ,aY;
        double mu = puttingCourse.get_friction_coefficient();
        double g = puttingCourse.get_gravity();
        Vector2d gradient = puttingCourse.get_height().gradient(position);
        aX = (-g*(gradient.get_x())) - (mu*g*velocity.get_x()/velocity.get_scalar());
        aY = (-g*(gradient.get_y())) - (mu*g*velocity.get_y()/velocity.get_scalar());
        return new Vector2d(aX,aY);

    }

    public void shot(Vector2d initial_ball_velocity){

        this.velocity = initial_ball_velocity;
        previousAcceleration = new Vector2d(0,0);
        acceleration = new Vector2d(0,0);
        Vector2d stopV = new Vector2d(0.01,0.01);
        boolean cont = true;
        while(cont){
            Vector2d tempAcc = acceleration;
            acceleration = accelerationCalculator(velocity);
            previousAcceleration = tempAcc;
            position = verletPlacementCalculator();
            double moveX=position.x-ball.getXcoords();
            double moveY=position.x-ball.getYcoords();
            ball.moveBy((float)moveX,(float) moveY);
            velocity =  velocityCalculator();
            if(velocity.get_scalar()<stopV.get_scalar() && acceleration.get_scalar()< accelerationCalculator(stopV).get_scalar()){
                cont = false;
            }
        }
    }
}
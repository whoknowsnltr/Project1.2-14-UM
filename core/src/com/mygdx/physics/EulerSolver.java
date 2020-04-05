package com.mygdx.physics;


public class EulerSolver implements PhysicsEngine {
    double h;
    double mass;
    double gravityConstant;
    double frictionCoefficient;
    public double velocityX;
    public double velocityY;
    public boolean isNull=false;
    public EulerSolver (double h, double mass, double gravityConstant, double frictionCoefficient){
        this.mass = mass;
        this.h = h;
        this.gravityConstant = gravityConstant;
        this.frictionCoefficient = frictionCoefficient;
    }
    public void setMass(double mass){
        this.mass = mass;
    }
    public void set_step_size(double h){
        this.h = h;
    }
    public Vector2d position(Vector2d beginningPosition, Vector2d velocity){
        if (beginningPosition.equals(null) || (velocity.equals(null))){
            return null;
        }
        else{
            double endPositionX;
            double endPositionY;
            double beginningPositionX = beginningPosition.get_x();
            double beginningPositionY = beginningPosition.get_y();
            double velocityX = velocity.get_x();
            double velocityY = velocity.get_y();

            endPositionX = beginningPositionX + (h * velocityX);
            endPositionY = beginningPositionY + (h * velocityY);

            Vector2d vector2d = new Vector2d(endPositionX, endPositionY);

            return vector2d;
        }
    }
    public Vector2d velocity(Vector2d beginningVelocity, double angleX, double angleY){
        double endVelocityX;
        double endVelocityY;
        double beginningVelocityX = beginningVelocity.get_x();
        double beginningVelocityY = beginningVelocity.get_y();


        endVelocityY = beginningVelocityY - frictionForce(frictionCoefficient, angleX).get_x();
        if (endVelocityY < 1){
            endVelocityY = 0;

        }
        endVelocityX = beginningVelocityX - frictionForce(frictionCoefficient, angleY).get_x();
        if (endVelocityX < 1){
            endVelocityX = 0;
        }
        if (endVelocityX == 0 && endVelocityY == 0){
            velocityX = 0;
            velocityY = 0;
        }
        if (endVelocityX == 0 && endVelocityY == 0){
            isNull = true;
        }
        Vector2d vector2d = new Vector2d(endVelocityX, endVelocityY);
        return vector2d;
    }

    @Override
    public Vector2d gravityForce(double gravityConstant) {
        Vector2d vector2d = new Vector2d(0, (gravityConstant*mass));
        return vector2d;
    }

    @Override
    public Vector2d frictionForce(double frictionCoefficient, double angle) {
        double frictionForce = gravityForce(gravityConstant).get_y() * Math.cos(Math.atan(angle));
        double frictionForceX = frictionForce * Math.cos(Math.atan(angle));
        double frictionForceY = frictionForce * Math.sin(Math.atan(angle));

        return new Vector2d(frictionForceX, frictionForceY);
    }

}

package com.mygdx.physics;

public class RungeKutta implements PhysicsEngine {
    private Vector2d acceleration;
    private Vector2d position;
    private Vector2d velocity;
    private PuttingCourse puttingCourse;
    private double stepSize;

    public RungeKutta(PuttingCourse puttingCourse, double stepSize) {
        this.puttingCourse = puttingCourse;
        this.stepSize = stepSize;
    }
    public Vector2d giveVel(Vector2d currentVelocity, Vector2d currentPosition) {
            velocity = currentVelocity;
            position = currentPosition;
            acceleration = new Vector2d(0, 0);
            Vector2d stopV = new Vector2d(0.01,0.01);
            boolean goOn = true;
            while(goOn) {
                if (currentVelocity.get_x() == 0 && currentVelocity.get_y() == 0) {
                    break;
                }
                acceleration = accelerationCalculator(position, velocity);
                position = rungePlacementCalculator(position,velocity);
//                this.kuttaSeries.add(position.get_x(),position.get_y());
                velocity = velocityCalculator(velocity,position);
               // System.out.println(position.toString());

                System.out.println("Velocity:"+velocity.getScalar());
                if (velocity.getScalar() < stopV.getScalar() && acceleration.getScalar() < accelerationCalculator(position,stopV).getScalar()) {
                    goOn = false;
                }

            }
            return position;
        }
    public Vector2d velocityCalculator(Vector2d velocity, Vector2d position){//This method calculates the velocity of the ball
        Vector2d velK1;
        Vector2d velK2;
        Vector2d velK3;
        Vector2d velK4;
        velK1 = accelerationCalculator(position,velocity);
        velK2 = accelerationCalculator(position, new Vector2d(velocity.get_x() + velK1.get_x() * stepSize / 2, velocity.get_y() + velK1.get_y() * stepSize / 2));
        velK3 = accelerationCalculator(position, new Vector2d(velocity.get_x() + velK2.get_x() * stepSize / 2, velocity.get_y() + velK2.get_y() * stepSize / 2));
        velK4 = accelerationCalculator(position, new Vector2d(velocity.get_x() + velK3.get_x() * stepSize, velocity.get_y() + velK3.get_y() * stepSize));

        velocity.setX(velocity.get_x() + (stepSize/6)*(velK1.get_x() + 2*velK2.get_x() + 2*velK3.get_x() + velK4.get_x()));
        velocity.setY(velocity.get_y() + (stepSize/6)*(velK1.get_y() + 2*velK2.get_y() + 2*velK3.get_y() + velK4.get_y()));
        return new Vector2d(velocity.get_x(),velocity.get_y());
    }
    public Vector2d rungePlacementCalculator(Vector2d position, Vector2d velocity){
        double sX = position.get_x()+stepSize*velocity.get_x();
        double sY = position.get_y()+stepSize*velocity.get_y();
        return new Vector2d(sX,sY);
    }
    public Vector2d accelerationCalculator(Vector2d position, Vector2d velocity){
        double aX ,aY;
        double mu = puttingCourse.get_friction_coefficient();
        double g = puttingCourse.get_gravity();
        Vector2d gradient1 = puttingCourse.get_height().gradient(position);
        aX = (-g*(gradient1.get_x())) - (mu*g*velocity.get_x()/velocity.getScalar());
        aY = (-g*(gradient1.get_y())) - (mu*g*velocity.get_y()/velocity.getScalar());
        return new Vector2d(aX,aY);
    }
    @Override
    public Vector2d gravityForce(double gravityConstant) {
        return null;
    }

    @Override
    public Vector2d frictionForce(double frictionCoefficient, double angle) {
        return null;
    }
//    public static void main(String args[]) {
//        PuttingCourse course1 = new PuttingCourse();
//        course1.readFile("//Users//ziynetarslanoglu//IdeaProjects//Project1-2.2-14-UM-masterVeryLastVersion//core//src//com//mygdx//physics//testerForSolvers.txt");
//        RungeKutta kutta = new RungeKutta(course1,0.01);
//        kutta.giveVel(new Vector2d(3,3),new Vector2d(0,0));
//    }
}

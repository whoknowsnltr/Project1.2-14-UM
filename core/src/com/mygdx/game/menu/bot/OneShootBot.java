package com.mygdx.game.bot;

import com.mygdx.physics.EulerSolver;
import com.mygdx.physics.FunctionReader;
import com.mygdx.physics.TerrainCoordinate;
import com.mygdx.physics.Vector2d;

/*
Class that computes the velocity vector for making ball go from point A to point B, with the lowest possible speed
 */
public class OneShootBot {
    private double maxVelocity;
    private Node finalPoint;
    private Node startingPoint;
    private double holeTolerance;
    private String formula;
    private FunctionReader reader;
    private EulerSolver eulerSolver;
    public static int Height = 640 * 2;
    public static int Width = 640 * 8 / 3;
    private Vector2d velocity;
    private Vector2d foundVelocity;
    private Vector2d velocityA = new Vector2d(0, 0);
    private Vector2d velocityB = new Vector2d(0, 0);
    private boolean[] midpointRule = new boolean[3];

    public OneShootBot(double maxVelocity, Node startingPoint, Node finalPoint, double holeTolerance, String formula, EulerSolver eulerSolver) {
        this.maxVelocity = maxVelocity;
        this.finalPoint = finalPoint ;
        this.startingPoint = startingPoint;
        this.holeTolerance = holeTolerance;
        this.formula = formula;
        this.eulerSolver = eulerSolver;
        velocity = new Vector2d(getXVelocity(1), getYVelocity(1));
        reader = new FunctionReader(formula);
    }

    public double getXVelocity(double maxVelocity) {
        double length = Math.sqrt(((finalPoint.get_x() - startingPoint.get_x()) * (finalPoint.get_x() - startingPoint.get_x())) + ((finalPoint.get_y() - startingPoint.get_y()) * (finalPoint.get_y() - startingPoint.get_y())));
        return ((finalPoint.get_x() - startingPoint.get_x()) * maxVelocity / length);
    }

    public double getYVelocity(double maxVelocity) {
        double length = Math.sqrt(((finalPoint.get_x() - startingPoint.get_x()) * (finalPoint.get_x() - startingPoint.get_x())) + ((finalPoint.get_y() - startingPoint.get_y()) * (finalPoint.get_y() - startingPoint.get_y())));
        return ((finalPoint.get_y() - startingPoint.get_y()) * maxVelocity / length);
    }

    public Vector2d computeVelocity(double constant) {
        System.out.println("COnStAnT " + constant);

        Vector2d velocityVectorCopy = new Vector2d(velocity.get_x(), velocity.get_y());
        double[] constant2 = new double[2];
        if (velocityVectorCopy.get_x() < velocityVectorCopy.get_y()) {
            constant2[0] = velocityVectorCopy.get_x() / velocityVectorCopy.get_y();
            constant2[1] = 1.0;
        } else {
            constant2[0] = 1.0;
            constant2[1] = velocityVectorCopy.get_y() / velocityVectorCopy.get_x();
        }
        boolean running = true;
        Vector2d newPosition = new Vector2d(startingPoint.get_x(), startingPoint.get_y());
        int i = 0;
        Vector2d finalPosition = new Vector2d(startingPoint.get_x(), startingPoint.get_y());

        while (running) {
            i++;
            newPosition = throwBall(newPosition);
            // Move the ball every 20 steps, to prevent game from lagging
            if (i % 20 == 0) {
                finalPosition = new Vector2d(newPosition.get_x(), (float) newPosition.get_y());

            }
            if (Math.abs(velocity.get_y()) < 1 && Math.abs(velocity.get_x()) < 1) {
                finalPosition = new Vector2d(newPosition.get_x(), (float) newPosition.get_y());
                running = false;
            }

        }
        double distanceBetweenEndBallPositionAndHole = Math.sqrt((finalPosition.get_x() - finalPoint.get_x()) * (finalPosition.get_x() - finalPoint.get_x()) + ((finalPosition.get_y() - finalPoint.get_y()) * (finalPosition.get_y() - finalPoint.get_y())));
        if (distanceBetweenEndBallPositionAndHole > holeTolerance && (Math.sqrt((velocity.get_x() * velocity.get_x()) + (velocity.get_y() * velocity.get_y())) < maxVelocity)) {
            if (distanceBetweenEndBallPositionAndHole < 2 * holeTolerance) {
                constant = distanceBetweenEndBallPositionAndHole / 4000;
            } else {
                constant = distanceBetweenEndBallPositionAndHole / 1000;
            }
            System.out.println("DISTANCE |HOLE - BALL|: " + distanceBetweenEndBallPositionAndHole + " HOLE TOLERANCE : " + holeTolerance);
            velocityA = new Vector2d(velocity.get_x(), velocity.get_y());
            velocity = new Vector2d(velocityVectorCopy.get_x() + constant2[0] * constant, velocityVectorCopy.get_y() + constant2[1] * constant );
            //     velocity = new Vector2d(velocityVectorCopy.get_x() + constant*constant2[0], velocityVectorCopy.get_y() + constant*constant2[1]);
            // if (usePreviousVelocity){
            //     velocity=new Vector2d(velocityA.get_x()+constant*constant2[0], velocityA.get_y() + constant*constant2[1]);
            //  }

            System.out.println(" COMPUTING, CURRENT VELOCITY: " + velocity.get_x() + "    " + velocity.get_y());
            System.out.println(" END POS " + finalPosition.get_x() + "    " + finalPosition.get_y());
            foundVelocity = new Vector2d(velocity.get_x(), velocity.get_y());
            System.out.println(computeLength(finalPoint) + " <------ HOLE COORDS");
            System.out.println(computeLength(finalPosition) + " <------ BALL CÖÖRDS");
       /*     if (computeLength(holeCoords)<computeLength(finalPosition) && distanceBetweenEndBallPositionAndHole < 2*holeTolerance){
                System.out.println("LOG 1");
                midpointRule[0] = true;
              //  if (midpointRule[1]|| midpointRule[1] && previousDistance<=distanceBetweenEndBallPositionAndHole)
                 //   constant=-constant*3/2 ;
                constant=distanceBetweenEndBallPositionAndHole/1000;
           //     velocity=velocityA;
         //       k=-Math.abs(k);
            }
            else if (computeLength(holeCoords)>computeLength(finalPosition) && distanceBetweenEndBallPositionAndHole < 2*holeTolerance){
                System.out.println("LOG 2");
                midpointRule[1] = true;
                //if (midpointRule[0])
                 //   constant=constant/2 ;
                constant=distanceBetweenEndBallPositionAndHole/2000;

             //   k=Math.abs(k);
            }
            if (previousDistance<=distanceBetweenEndBallPositionAndHole&& distanceBetweenEndBallPositionAndHole < 2*holeTolerance) {
              //  velocity=velocityA;
                constant = distanceBetweenEndBallPositionAndHole / 1000;
            //    k=-Math.abs(k);
            }*/
            if (midpointRule[0] && midpointRule[1]) {
                midpointRule[0] = false;
                midpointRule[1] = false;
            }

            computeVelocity(constant);


        }

        return foundVelocity;
    }

    public double computeLength(TerrainCoordinate one) {
        return Math.sqrt((one.get_x() * one.get_x()) + (one.get_y() * one.get_y()));
    }

    public Vector2d throwBall(Vector2d initialPosition) {
        // Read the mathematical formula
        FunctionReader reader = new FunctionReader(formula);
        // Get initial position
        // Compute angles for x and y axis
        double angleX = reader.derivativeX(initialPosition);
        double angleY = reader.derivativeY(initialPosition);
        // Compute velocity after a step of time
        Vector2d vector2d = hitWall(velocity, initialPosition);
        velocity = eulerSolver.velocity(vector2d, angleX, angleY);
        // Compute position after a step of time
        Vector2d endPosition = eulerSolver.position(initialPosition, velocity);
        return endPosition;
    }

    /**
     * Method that defines what the ball does after collision with a wall
     *
     * @param initialVelocity
     * @return
     */
    public Vector2d hitWall(Vector2d initialVelocity, Vector2d position) {
        Vector2d velocityAfterCollision = new Vector2d(initialVelocity.get_x(), initialVelocity.get_y());
        // Check which wall did the ball hit
        if (position.get_x() <= 0 || position.get_x() >= Width) {
            velocityAfterCollision = new Vector2d((initialVelocity.get_x() * (-1)), initialVelocity.get_y());
        }
        if (position.get_y() <= 0 || position.get_y() >= Height) {
            velocityAfterCollision = new Vector2d(initialVelocity.get_x(), (initialVelocity.get_y() * (-1)));
        }

        return velocityAfterCollision;
    }

}

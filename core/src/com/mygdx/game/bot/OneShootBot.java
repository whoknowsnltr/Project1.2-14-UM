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
    private Vector2d velocity;
    private Vector2d foundVelocity;
    private double constant;
    double[][]friction;
    double step_size, mass, gravityConstant;

    public OneShootBot(double maxVelocity, Node startingPoint, Node finalPoint, double holeTolerance, String formula,  double[][]friction,double step_size, double mass, double gravityConstant) {
        this.maxVelocity = maxVelocity;
        this.finalPoint = finalPoint;
        this.startingPoint = startingPoint;
        this.holeTolerance = holeTolerance;
        this.formula = formula;
        this.friction=friction;
        this.step_size=step_size;
        this.gravityConstant=gravityConstant;
        this.mass=mass;
        velocity = new Vector2d(getXVelocity(1), getYVelocity(1));
    }
    /**
     * Compute the X velocity direction
     * @param maxVelocity maximal velocity
     * @return computed velocity direction
     */
    private double getXVelocity(double maxVelocity) {
        double length = Math.sqrt(((finalPoint.get_x() - startingPoint.get_x()) * (finalPoint.get_x() - startingPoint.get_x())) + ((finalPoint.get_y() - startingPoint.get_y()) * (finalPoint.get_y() - startingPoint.get_y())));
        return ((finalPoint.get_x() - startingPoint.get_x()) * maxVelocity / length);
    }

    /**
     * Compute the Y velocity direction
     * @param maxVelocity maximal velocity
     * @return computed velocity direction
     */
    private double getYVelocity(double maxVelocity) {
        double length = Math.sqrt(((finalPoint.get_x() - startingPoint.get_x()) * (finalPoint.get_x() - startingPoint.get_x())) + ((finalPoint.get_y() - startingPoint.get_y()) * (finalPoint.get_y() - startingPoint.get_y())));
        return ((finalPoint.get_y() - startingPoint.get_y()) * maxVelocity / length);
    }

    /**
     * Method that computes the velocity so that if ball is hit by it, it lands within the hole tolerance
     *
     * @return computed velocity vector
     */
    public Vector2d computeVelocity() {
        System.out.println("CONSTANT " + constant);
        Vector2d velocityVectorCopy = new Vector2d(velocity.get_x(), velocity.get_y());
        // This array makes sure that the x and y axis of velocity keep their ratio
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

        // hit ball once with default velocity
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
        // If ball is still out of hole tolerance area, look for different velocity
        double distanceBetweenEndBallPositionAndHole = Math.sqrt((finalPosition.get_x() - finalPoint.get_x()) * (finalPosition.get_x() - finalPoint.get_x()) + ((finalPosition.get_y() - finalPoint.get_y()) * (finalPosition.get_y() - finalPoint.get_y())));
        if (distanceBetweenEndBallPositionAndHole > holeTolerance && (Math.sqrt((velocity.get_x() * velocity.get_x()) + (velocity.get_y() * velocity.get_y())) < maxVelocity)) {
            if (distanceBetweenEndBallPositionAndHole > 150) {
                constant = distanceBetweenEndBallPositionAndHole / 100;
            }
            if (distanceBetweenEndBallPositionAndHole < 100) {
                // If the hole is near the hole tolerance area, be more precise with velocity
                constant = distanceBetweenEndBallPositionAndHole / 10000;
            }
            if (distanceBetweenEndBallPositionAndHole < 2*holeTolerance ) {
                // If the hole is near the hole tolerance area, be more precise with velocity
                constant = distanceBetweenEndBallPositionAndHole / 20000;
            }
            if (distanceBetweenEndBallPositionAndHole<60){
                constant = distanceBetweenEndBallPositionAndHole / 30000;

            } else{
                constant = distanceBetweenEndBallPositionAndHole / 500;
            }
            System.out.println("DISTANCE |HOLE - BALL|: " + distanceBetweenEndBallPositionAndHole + " HOLE TOLERANCE : " + holeTolerance);
            // assign new velocity
            velocity = new Vector2d(velocityVectorCopy.get_x() + constant2[0] * constant, velocityVectorCopy.get_y() + constant2[1] * constant);

            System.out.println(" COMPUTING, CURRENT VELOCITY: " + velocity.get_x() + "    " + velocity.get_y());
            System.out.println(" END POS " + finalPosition.get_x() + "    " + finalPosition.get_y());
            foundVelocity = new Vector2d(velocity.get_x(), velocity.get_y());
            System.out.println(computeLength(finalPoint) + " <------ HOLE COORDS");
            System.out.println(computeLength(finalPosition) + " <------ BALL C00RDS");
            // Do recursively until found
            computeVelocity();
        }
        return foundVelocity;
    }

    /**
     * Method that computes the length of a 2d terrain coordinate according to point (0,0)
     *
     * @param one coordinate
     * @return length of one
     */
    private double computeLength(TerrainCoordinate one) {
        return Math.sqrt((one.get_x() * one.get_x()) + (one.get_y() * one.get_y()));
    }

    /**
     * Method that computes what happens to ball after being shot
     *
     * @param initialPosition - initial position of ball
     * @return - vector that simulates ball movement
     */
    private Vector2d throwBall(Vector2d initialPosition) {
        // Read the mathematical formula
        FunctionReader reader = new FunctionReader(formula);
        // Get initial position
        // Compute angles for x and y axis
        double angleX = reader.derivativeX(initialPosition);
        double angleY = reader.derivativeY(initialPosition);
        double initialPositionX=initialPosition.get_x();
        double initialPositionY=initialPosition.get_y();
        if (initialPosition.get_x()>friction.length){
            initialPositionX = (friction.length-1);
        }
        if (initialPosition.get_y()>friction[0].length){
            initialPositionY = (friction[0].length-1);
        }
        if (initialPosition.get_x()<0){
            initialPositionX = (0);
        }
        if (initialPosition.get_y()<0){
            initialPositionY = (0);
        }
        double frictionValue = friction[(int) initialPositionX][(int) initialPositionY];

        EulerSolver eulerSolver = new EulerSolver(step_size, mass,gravityConstant,frictionValue);
        // Compute velocity after a step of time
        Vector2d vector2d = hitWall(velocity, initialPosition);
        velocity = eulerSolver.velocity(vector2d, angleX, angleY);
        // Compute position after a step of time
        return eulerSolver.position(initialPosition, velocity);
    }

    /**
     * Method that handles wall collisions
     *
     * @param initialVelocity - starting velocity of ball
     * @param position        - starting position of ball
     * @return vector being result of collision
     */
    private Vector2d hitWall(Vector2d initialVelocity, Vector2d position) {
        Vector2d velocityAfterCollision = new Vector2d(initialVelocity.get_x(), initialVelocity.get_y());
        // Check which wall did the ball hit
        int width = 640 * 8 / 3;
        if (position.get_x() <= 0 || position.get_x() >= width) {
            velocityAfterCollision = new Vector2d((initialVelocity.get_x() * (-1)), initialVelocity.get_y());
        }
        int height = 640 * 2;
        if (position.get_y() <= 0 || position.get_y() >= height) {
            velocityAfterCollision = new Vector2d(initialVelocity.get_x(), (initialVelocity.get_y() * (-1)));
        }

        return velocityAfterCollision;
    }

}

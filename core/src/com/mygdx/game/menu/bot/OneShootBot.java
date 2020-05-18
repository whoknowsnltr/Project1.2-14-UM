package com.mygdx.game.bot;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.mygdx.physics.EulerSolver;
import com.mygdx.physics.FunctionReader;
import com.mygdx.physics.Vector2d;

/*
This class checks if the ball can land in a hole within a single hit
 */
public class OneShootBot {
    private double maxVelocity;
    private Vector2d holeCoords;
    private Vector2d ballcoords;
    private double holeTolerance;
    private String formula;
    private FunctionReader reader;
    private EulerSolver eulerSolver;
    public static int Height = 640 * 2;
    public static int Width = 640 * 8 / 3;
    private Vector2d velocity;

    public  OneShootBot(double maxVelocity, Vector2d holeCoords, Vector2d ballcoords, double holeTolerance, String formula, EulerSolver eulerSolver){
        this.maxVelocity = maxVelocity;
        this.holeCoords = holeCoords;
        this.ballcoords = ballcoords;
        this.holeTolerance = holeTolerance;
        this.formula=formula;
        this.eulerSolver=eulerSolver;
        velocity = new Vector2d(getXVelocity(), getYVelocity());
        reader = new FunctionReader(formula);
    }
    public double getXVelocity(){
        double length = Math.sqrt(((holeCoords.get_x()-ballcoords.get_x())*(holeCoords.get_x()-ballcoords.get_x()))+((holeCoords.get_y()-ballcoords.get_y())*(holeCoords.get_y()-ballcoords.get_y())));
       return ((holeCoords.get_x()-ballcoords.get_x())*maxVelocity/length);
    }
    public double getYVelocity(){
        double length = Math.sqrt(((holeCoords.get_x()-ballcoords.get_x())*(holeCoords.get_x()-ballcoords.get_x()))+((holeCoords.get_y()-ballcoords.get_y())*(holeCoords.get_y()-ballcoords.get_y())));
        return  ((holeCoords.get_y()-ballcoords.get_y())*maxVelocity/length);
    }

    public Vector2d computeVelocity(Vector2d velocityVector) {
        Vector2d velocityVectorCopy = new Vector2d(velocityVector.get_x(), velocityVector.get_y());
        boolean running = true;
        Vector2d newPosition = new Vector2d(ballcoords.get_x(), ballcoords.get_y());
        SequenceAction sequenceAction = new SequenceAction();
        double length = Math.sqrt(((holeCoords.get_x()-ballcoords.get_x())*(holeCoords.get_x()-ballcoords.get_x()))+((holeCoords.get_y()-ballcoords.get_y())*(holeCoords.get_y()-ballcoords.get_y())));
        int i = 0;
        Vector2d finalPosition = new Vector2d(ballcoords.get_x(), ballcoords.get_y());
        while (running) {
            i++;
            newPosition = throwBall(newPosition, velocityVector);
            // Move the ball every 20 steps, to prevent game from lagging
            if (i % 20 == 0) {
           //     Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y());
             //   sequenceAction.addAction(action);
                finalPosition = new Vector2d(newPosition.get_x(), (float) newPosition.get_y());

            }
            if (Math.abs(velocityVector.get_y()) < 1 && Math.abs(velocityVector.get_x()) < 1) {
            //    Action action = Actions.moveTo((float) newPosition.get_x(), (float) newPosition.get_y());
                finalPosition = new Vector2d(newPosition.get_x(), (float) newPosition.get_y());
            //    sequenceAction.addAction(action);
                running = false;
            }

        }
        System.out.println(velocityVector);
        double lengthOfFinalPosition = Math.sqrt((finalPosition.get_x()-ballcoords.get_x())*(finalPosition.get_x()-ballcoords.get_x())+(finalPosition.get_x()-ballcoords.get_x())*(finalPosition.get_x()-ballcoords.get_x()));
        if (lengthOfFinalPosition>length){
            velocityVector = new Vector2d(velocityVectorCopy.get_x()-2, velocityVectorCopy.get_y()-2);
            computeVelocity(velocityVector);
        }
            return velocityVectorCopy;
    }
    public Vector2d throwBall(Vector2d initialPosition, Vector2d velocityVector) {
        // Read the mathematical formula
        FunctionReader reader = new FunctionReader(formula);
        // Get initial position
        // Compute angles for x and y axis
        double angleX = reader.derivativeX(initialPosition);
        double angleY = reader.derivativeY(initialPosition);
        // Compute velocity after a step of time
        Vector2d vector2d = hitWall(velocityVector, initialPosition);
        velocityVector = eulerSolver.velocity(vector2d, angleX, angleY);
        // Compute position after a step of time
        Vector2d endPosition = eulerSolver.position(initialPosition, velocityVector);
        return endPosition;
    }

    /**
     * Method that defines what the ball does after collision with a wall
     *
     * @param initialVelocity
     * @return
     */
    public Vector2d hitWall(Vector2d initialVelocity, Vector2d position) {
        Vector2d velocityAfterCollision = new Vector2d(initialVelocity.get_x(),initialVelocity.get_y());
        // Check which wall did the ball hit
        if (position.get_x() <= 0 || position.get_x() >= Width) {
            velocityAfterCollision = new Vector2d((initialVelocity.get_x() * (-1)), initialVelocity.get_y());
        }
        if (position.get_y() <= 0 || position.get_y() >= Height) {
            velocityAfterCollision = new Vector2d(initialVelocity.get_x(), (initialVelocity.get_y() * (-1)));
        }
        //     System.out.println(velocityAfterCollision);
        return velocityAfterCollision;
    }
}

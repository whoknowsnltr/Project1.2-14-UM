package com.mygdx.game.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.mygdx.game.MyActor;
import com.mygdx.physics.Vector2d;

import java.awt.*;

public class Obstacles {
    private MyActor ball;
    private MyActor obstacle;
    private double friction_coeff;

    public Obstacles(MyActor ball, MyActor obstacle, double friction_coeff){
        this.ball=ball;
        this.obstacle=obstacle;
        this.friction_coeff=friction_coeff;
    }

    public Rectangle getObstacleBounds(){
        return new Rectangle((int)obstacle.getX(), (int) obstacle.getY(), (int) obstacle.getWidth(), (int) obstacle.getHeight());
    }

    public boolean isHit(Rectangle ballBounds){
        return (getObstacleBounds().intersects(ballBounds));
    }

    public Vector2d handleSand(Rectangle ballBounds){
        if (isHit(ballBounds)) {
            return new Vector2d(friction_coeff + 1, 9999);
        }
        return new Vector2d(friction_coeff,9999);
    }

    public Vector2d handlePond(Rectangle ballBounds, Vector2d lastPositionNotColliding){
        if (isHit(ballBounds)) {
            Vector2d position = new Vector2d(ball.getX(), ball.getY());
            float pondLeft = obstacle.getX();
            float pondBottom = obstacle.getY();
            float pondRight = obstacle.getX() + obstacle.getWidth();
            float pondTop = obstacle.getY() + obstacle.getHeight();
            Vector2d endposition=new Vector2d(0,0);
            double vertical=0;
            double horizontal=0;
            // Check which wall did the ball hit
            if (position.get_x() <= pondRight) {
                horizontal=lastPositionNotColliding.get_x()-3;
            }
            if (position.get_x() >= pondLeft) {
                horizontal= lastPositionNotColliding.get_x()+3;
            }
            if (position.get_y() <= pondTop) {
                vertical=lastPositionNotColliding.get_y()-3;
            }
            if (position.get_y() >= pondBottom) {
                vertical=lastPositionNotColliding.get_y()+3;
            }
           endposition = new Vector2d(horizontal,vertical);
           return endposition;
        }
        return new Vector2d(9999,9999);
    }
    public Vector2d handleTree(Vector2d initialVelocity, Rectangle ballBounds) {
        if (isHit(ballBounds)) {
            Vector2d position = new Vector2d(ball.getX(), ball.getY());
            double treeLeft = obstacle.getX();
            double treeBottom = obstacle.getY();
            double treeRight = obstacle.getX() + obstacle.getWidth();
            double treeTop = obstacle.getY() + obstacle.getHeight();
            Vector2d velocityAfterCollision = new Vector2d(initialVelocity.get_x(), initialVelocity.get_y());
            // Check which wall did the ball hit
            if (position.get_x() <= treeRight || position.get_x() >= treeLeft) {
                velocityAfterCollision = new Vector2d((initialVelocity.get_x() * (-1)), initialVelocity.get_y());
            }
            if (position.get_y() <= treeTop || position.get_y() >= treeBottom) {
                velocityAfterCollision = new Vector2d(initialVelocity.get_x(), (initialVelocity.get_y() * (-1)));
            }
            return velocityAfterCollision;
        }
        return new Vector2d(9999,9999);
    }
    public Vector2d collisionHandler(Vector2d velocity, Rectangle ballBounds, Vector2d lastPosition){
            switch (obstacle.getName()) {
                case "pond":
                    return handlePond(ballBounds, lastPosition);
                case "tree":
                    return handleTree(velocity,ballBounds);
                case "sand":
                    return handleSand(ballBounds);
            }
        return new Vector2d(9999, 9999);
    }

    @Override
    public String toString() {
        return obstacle.getName() + " " + obstacle.getX() + " " + obstacle.getY();
    }
    public String getName(){
        return obstacle.getName();
    }
}

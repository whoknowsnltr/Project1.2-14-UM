package com.mygdx.physics;

public interface PhysicsEngine {
    public Vector2d gravityForce(double gravityConstant);
    public Vector2d frictionForce(double frictionCoefficient,  double angle);
}

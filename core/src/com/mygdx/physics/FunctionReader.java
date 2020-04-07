package com.mygdx.physics;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * This class evaluates function from a string, and computes derivatives
 */
public class FunctionReader implements Function2d {
   String formula; // Mathematical equation of the terrain

    public FunctionReader(String formula) {
        this.formula = formula;
    }

    @Override
    public double evaluate(Vector2d p) {
        return evaluateFunction(p, this.formula);
    }

    /**
     * Method that for a given x and y (Vector 2d) computes height
     * @param p
     * @param formula
     * @return
     */
    public double evaluateFunction(Vector2d p, String formula){
        Argument x = new Argument("x = " + p.get_x());
        Argument y = new Argument("y = " + p.get_y());
        Expression e = new Expression(formula, x, y);
        return e.calculate();
    }

    /**
     * Method that for given x and y computes x derivative ( that helps to find an angle of terrain for a given point )
     * @param p
     * @return
     */
    public double derivativeX(Vector2d p){
        Argument x = new Argument("x = " + p.get_x());
        Argument y = new Argument("y = " + p.get_y());
        Expression e = new Expression(formula,x , y);
        double delta = 0.00001;
        Argument xAndDelta = new Argument("x = " + (p.get_x()+delta));
        Expression eDelta = new Expression(formula, xAndDelta, y);
        return ((eDelta.calculate()-e.calculate())/delta);
    }

    /**
     * Method that for given x and y computes y derivative
     * @param p
     * @return
     */
    public double derivativeY(Vector2d p){
        Argument y = new Argument("y = " + p.get_y());
        Argument x = new Argument("x = " + 0);
        Expression e = new Expression(formula, x, y);
        double delta = 0.00001;
        Argument yAndDelta = new Argument("y = " + (p.get_y()+delta));
        Expression eDelta = new Expression(formula, yAndDelta, x);
        return ((eDelta.calculate()-e.calculate())/delta);
    }

    /**
     * Method that combines derivative x and y, and creates vector of those
     * @param p
     * @return
     */
    @Override
    public Vector2d gradient(Vector2d p) {
        double x = derivativeX(p);
        double y = derivativeY(p);
        return new Vector2d(x, y);
    }
}

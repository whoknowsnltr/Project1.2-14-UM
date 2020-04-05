package com.mygdx.physics;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * This class evaluates function from a string, and computes derivatives
 */
public class FunctionReader implements Function2d {
   String formula;

    public FunctionReader(String formula) {
        this.formula = formula;
    }

    @Override
    public double evaluate(Vector2d p) {
        return evaluateFunction(p, this.formula);
    }

    public double evaluateFunction(Vector2d p, String formula){
        Argument x = new Argument("x = " + p.get_x());
        Argument y = new Argument("y = " + p.get_y());
        Expression e = new Expression(formula, x, y);
        return e.calculate();
    }

    public double derivativeX(Vector2d p){
        Argument x = new Argument("x = " + p.get_x());
        Argument y = new Argument("y = " + 0);
        Expression e = new Expression("der (" + formula + ")", x, y);
        return e.calculate();
    }
    public double derivativeY(Vector2d p){
        Argument x = new Argument("x = " + 0);
        Argument y = new Argument("y = " + p.get_y());
        Expression e = new Expression("der (" + formula + ")", x, y);
        return e.calculate();
    }
    @Override
    public Vector2d gradient(Vector2d p) {
        double x = derivativeX(p);
        double y = derivativeY(p);
        return new Vector2d(x, y);
    }
}

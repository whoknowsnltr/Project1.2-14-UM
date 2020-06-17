package com.mygdx.game.bot;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that finds the longest line in a path of given nodes
 * ---Approach: every 2 points create a line, but 3 or more might not---
 * 1. We take an array of points given by A* algorithm
 * 2. We take 2 consecutive points
 * 3. We compute the equation of the line that connects those 2 nodes
 * 4. Then we see if the 3rd, 4th and so on point also lies in the line
 * 5. We gather as many points that lie in the same line until we get one that doesn't
 * 6. We generate a shot, that considering terrain, will make the ball roll from first to last found point
 * 7. We repeat until we figured out all points
 *
 */
public class LineFinder {
    private List<Node> pathPoints;
    private List<Integer> pointsToLines = new ArrayList<>();
    LineFinder(List<Node> pathPoints){
        this.pathPoints = pathPoints;
    }

    /**
     * Method that looks for the longest line
     * @param index - index of node in pathPoints from which we start
     * @return index of the last node that is in straight line with the initial index node
     */
    int getSetOfPointsInOneLine(int index){
        //   System.out.println("===START===" + index);
        Node firstNode = pathPoints.get(index);
        if (index<pathPoints.size()-1){
            index++;
            Node secondNode = pathPoints.get(index);
            int[] lineCoefficients = lineEquationGetter(firstNode,secondNode);
            int a = lineCoefficients[0];
            int b = lineCoefficients[1];
            int c = lineCoefficients[2];

            while ((index<pathPoints.size()-1 && lineCoefficients[0] == a && lineCoefficients[1] == b && lineCoefficients[2] == c ) ||
                    (index<pathPoints.size()-1 && lineCoefficients[2] == c && c!=-1)) {
                //   System.out.println("TO COMPARE: " + a + " " + b + " " + c);
                // System.out.println("current:    " + lineCoefficients[0] + " " + lineCoefficients[1] + " " + lineCoefficients[2]);
                //index++;
                Node nodeOne = pathPoints.get(index);
                Node nodeTwo = pathPoints.get(index+1);
                index++;
                lineCoefficients = lineEquationGetter(nodeOne,nodeTwo);
                //lineCoefficients = lineEquationGetter(nodeOne,nodeOne);
            }
        }
        // System.out.println("===END===" + index);
        return index;
    }

    /**
     * Method that given 2 nodes computes the equation of line
     * @param first - first node
     * @param second - second node
     * @return - int array, where first entry is the a coefficient, and second is b coefficient
     */
    int[] lineEquationGetter(Node first, Node second){
        /*
        Logic:
        a*first.getX + b = first.getY
        a*second.getX + b = second.getY
        compute a and b
         */
        int a=0;
        int b=0;
        int c=-1; // If the equation is a vertical line (x1=x2), then take just c as a line coefficient, else - don't consider it
        int x1 = first.x;
        int x2 = second.x;
        int y1 = first.y;
        int y2 = second.y;
        int[] result = new int[3];
        /*
        Logic:
        if x1!=x2
        b = y1-a*x1
        a*x2+y1-a*x1=y2
        a(x2-x1)=(y2-y1)
        a=(y2-y1)/(x2-x1)
         */
        if (x1!=x2) {
            a = (y2 - y1) / (x2 - x1);
            b = y1 - a * x1;
            result[0] = a;
            result[1] = b;
            result[2] = -1;
        }
        /*
        else x=c
         */
        else{
            c=x1;
            result[0] = -1;
            result[1] = -1;
            result[2] = c;
        }

        return result;
    }

    /**
     * A method that sets the array with end/start points of lines
     */
    void setPointsToLines() {
        int index = 0;
        int endOfLine = 0;
        while (index < pathPoints.size()-1) {
            endOfLine = getSetOfPointsInOneLine(index);
            index=endOfLine;
            pointsToLines.add(index);
            System.out.println(index);
        }
    }

    public List<Integer> getPointsToLines() {
        return pointsToLines;
    }
    public List<Node> getFinalNodes(){
        List<Node> finalNodes = new ArrayList<>();
        for (Integer i : pointsToLines){

            finalNodes.add(pathPoints.get(i));
        }

        return finalNodes;
    }

    @Override
    public String toString(){
        String result = "";
        for (Integer i : pointsToLines){
            System.out.println(i);
            result += ("(" + pathPoints.get(i).x + ", " + pathPoints.get(i).y + "), ");
        }
        return result;
    }
}


package com.mygdx.game.bot;


import com.mygdx.physics.EulerSolver;
import com.mygdx.physics.Vector2d;

import java.util.ArrayList;
import java.util.List;

public class AStarBot {
    private static int Height = 640 * 2;
    private static int Width = 640 * 8 / 3;
    private int[][] nodes;
    private double maxVelocity;
    private double tolerance;
    private String formula;
    private EulerSolver eulerSolver;
    private int partition;

    public AStarBot(double maxVelocity, String formula, EulerSolver eulerSolver, double tolerance, int partition){
        this.maxVelocity=maxVelocity;
        this.formula=formula;
        this.eulerSolver=eulerSolver;
        this.tolerance=tolerance;
        this.partition=partition;
    }

    /**
     * Method that sets the node table, here we need to set obstacles
     */
    public void setNodes() {
        nodes = new int[Width/partition][Height/partition];
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes[0].length; j++) {
                nodes[i][j] = 0;
            }
        }
    }

    /**
     * This method puts the work of bot together
     * @param ballNode - node where the ball is
     * @param holeNode - node where the hole is
     * @return an array of vectors that represent the velocity that should be applied to ball one by one
     */
    public ArrayList<Vector2d> appliedBots(Node ballNode, Node holeNode) {
        // Set the array
        setNodes();
        ArrayList<Vector2d> moves = new ArrayList<>();
        AStarAlgorithm aStarAlgorithmBot = new AStarAlgorithm(nodes,(int) ballNode.get_x()/partition, (int)ballNode.get_y()/partition,(int)holeNode.get_x()/partition, (int)holeNode.get_y()/partition);
        System.out.println("BALL " + ballNode.x + "  " + ballNode.y);
        System.out.println("HOLE " + holeNode.x + "  " + holeNode.y);
        // Compute the path using A* algorithm
        List<Node> path = aStarAlgorithmBot.wayBetweenNodesFinder();
        for (Node n : path){
            System.out.println("path[" + n.x + ", " + n.y + "] ");
        }
        // Find lines
        LineFinder lineFinder = new LineFinder(path);
        lineFinder.setPointsToLines();
        List<Integer> lines = lineFinder.getPointsToLines();
        for (Integer n : lines){
            System.out.println("lines[" + n + "] ");
        }
        List<Node> foundNodes = lineFinder.getFinalNodes();
        for (Node n : foundNodes){
            System.out.println("foundNodes[" + n.x + ", " + n.y + "] ");
        }
        // Find and add velocities to returned list
        Node first = new Node(new Node(), (int)ballNode.get_x(),(int)ballNode.get_y(),ballNode.g, ballNode.h);
        for(int i = 0; i<foundNodes.size(); i++){
            System.out.println( "NODE FROM: " + first.get_x() + "   " + first.get_y());
            Node second = new Node(new Node(), (int)foundNodes.get(i).get_x()*partition,(int)foundNodes.get(i).get_y()*partition,foundNodes.get(i).g, foundNodes.get(i).h);
            OneShootBot oneShootBot;
            if (second.get_x() == holeNode.get_x() && second.get_y() == holeNode.get_y()) {
                oneShootBot = new OneShootBot(maxVelocity, first, second, tolerance, formula, eulerSolver);
            }
            else{
                oneShootBot = new OneShootBot(maxVelocity, first, second, 60, formula, eulerSolver);
            }
            System.out.println( "NODE TO: " + second.get_x()+ "   " + second.get_y());
            System.out.println("----------ROUND " + i + "----------"  );
            moves.add(oneShootBot.computeVelocity());
            System.out.println("----------DONE ROUND " + i + "----------"  );
            first = new Node(new Node(), (int)foundNodes.get(i).get_x()*partition,(int)foundNodes.get(i).get_y()*partition,foundNodes.get(i).g, foundNodes.get(i).h);
        }
        return moves;
    }
}

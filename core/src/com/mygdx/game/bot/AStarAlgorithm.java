package com.mygdx.game.bot;

import java.util.ArrayList;
import java.util.List;

class AStarAlgorithm {
    private int[][] terrain;
    private List<Node> nodesToGoThrough = new ArrayList<>(); // nodes visited but not expanded
    private List<Node> nodesAlreadyTraversed = new ArrayList<>(); // nodes visited and expanded
    private List<Node> listOfNodesFound = new ArrayList<>();
    private Node currentNode; // Node that we are considering now
    private int first_x;
    private int first_y;
    private int end_x;
    private int end_y;

    AStarAlgorithm(int[][] terrain, int first_x, int first_y, int end_x, int end_y) {
        this.terrain = terrain;
        this.first_x = first_x;
        this.first_y = first_y;
        this.end_x = end_x;
        this.end_y = end_y;
        this.currentNode = new Node(null, first_x, first_y, 0, 0);
    }

    /**
     * Method that finds path to a given coordinates
     *
     * @return - list of nodes that will be traversed when getting to node
     */
    List<Node> wayBetweenNodesFinder() {
        nodesAlreadyTraversed.add(currentNode); // We considered the starting node
        getReachableNodes(); // Get nodes we can reach
        while (currentNode.x != end_x || currentNode.y != end_y) {
            if (nodesToGoThrough.isEmpty()) { // Check if we have nodes left
                return null;
            }
            currentNode = nodesToGoThrough.get(0);
            nodesToGoThrough.remove(0);
            nodesAlreadyTraversed.add(currentNode);
            getReachableNodes();
        }
        listOfNodesFound.add(0, currentNode);
        while (currentNode.x != first_x || currentNode.y != first_y) {
            currentNode = (Node) currentNode.parent;
            listOfNodesFound.add(0, currentNode);
        }
        return listOfNodesFound;
    }

    /**
     * Method that computes the distance between node next to current one, and the goal
     *
     * @param x - x distance between current node and the one we consider next
     * @param y - y distance between current node and the one we consider next
     * @return distance
     */
    private double distance(int x, int y) {
        double distance = Math.sqrt((currentNode.x + x - end_x) * (currentNode.x + x - end_x) + (currentNode.y + y - end_y) * (currentNode.y + y - end_y));
        return distance;
    }

    /**
     * Method that determines if node has neighbours
     *
     * @param node  - node for which we look for neighbours
     * @param nodes - a list of nodes to look for
     * @return true if it has, false otherwise
     */
    private boolean doesNodeHaveNeighbours(Node node, List<Node> nodes) {
        boolean isSurroundedBySomething = false;
        for (Node n : nodes) {
            if (n.get_x() == node.get_x() && n.get_y() == node.get_y()) {
                isSurroundedBySomething = true;
                break;
            }
        }
        return isSurroundedBySomething;
    }

    /**
     * Method that checks if node is within terrain nodes or there are no obstacles
     *
     * @param i - x axis shift
     * @param j - y axis shift
     * @return true if next node would fit, false otherwise
     */
    private boolean isNodeReachable(int i, int j) {
        boolean isWithinTerrainBounds = (currentNode.y + j >= 0 && currentNode.y + j < terrain.length && currentNode.x + i >= 0 && currentNode.x + i < terrain[0].length);
        boolean areObstacles = false;
        if (isWithinTerrainBounds)
            areObstacles = (terrain[currentNode.y + j][currentNode.x + i] != -1);
        return (isWithinTerrainBounds && areObstacles);
    }

    /**
     * Method that adds to the list nodesToGoThrough nodes that are reachable from the current position
     */
    private void getReachableNodes() {
        Node node;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                node = new Node(currentNode, currentNode.x + i, currentNode.y + j, currentNode.g, distance(i, j));
                if ((i != 0 || j != 0) && isNodeReachable(i, j) && !doesNodeHaveNeighbours(node, nodesToGoThrough) && !doesNodeHaveNeighbours(node, nodesAlreadyTraversed)) {
                    node.g = node.parent.g + 1; // adding the distance
                    node.g += terrain[currentNode.y + j][currentNode.x + i]; // adding the value from terrain
                    nodesToGoThrough.add(node);
                }
            }
        }
    }
}
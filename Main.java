package com.company;

import java.util.*;

public class Main {

    public static void main(String[] args) throws NullPointerException {

        //maze generation
        for (int l = 0; l < 1; l++) {
            //setting the dimension size to be 101
            int dim = 8;
            //actual full grid
            int values[][] = new int[dim][dim];
            //agent's knowledge grid
            int dummyValues[][] = new int[dim][dim];

            double p = Math.random();
            while (p > 0.33) {
                p = Math.random();
            }

            //creating a dummy grid with no blocked cells
            for (int rn = 0; rn < dummyValues.length; rn++) {
                for (int cl = 0; cl < dummyValues[rn].length; cl++) {
                    dummyValues[rn][cl] = 0;
                    System.out.print(dummyValues[rn][cl]);
                }
                System.out.println();
            }
            System.out.println("Done printing the dummy grid");

            //generating the actual grid
            for (int i = 0; i < values.length; i++) {
                for (int j = 0; j < values[i].length; j++) {
                    if (i == 0 && j == 0 || i == dim - 1 && j == dim - 1) {
                        values[i][j] = 0;
                    } else {
                        if (Math.random() >= p) {
                            values[i][j] = 0;
                        } else {
                            values[i][j] = 1;
                        }
                    }
                    System.out.print(values[i][j]);
                }
                // add a new line
                System.out.println();
            }
            System.out.println("Done Printing the actual grid");

            Solution sol = new Solution();
            //grid is the full grid(actual maze);
            int[][] grid = values;

            //newGrid is the agent's knowledge with no blockage
            int[][] newGrid = dummyValues;

            int ans = sol.RFAstar(newGrid, 0, 0, 1, grid);
            System.out.println(ans);

        }
    }
}

class Solution {
    //To keep track of the path from A*
    List<Cell> track = new ArrayList<Cell>();

    //Creating a class for nodes in the gridworld
    class Cell {

        public int row;//represnts the current row of the cell
        public int col;//represents the current col of the cell
        public int distance; //represents g(n) value
        public int priorityEstimate;//represents f(n);

        //Creating a constructor for the class
        public Cell(int row, int col, int distance, int priorityEstimate) {
            this.row = row;
            this.col = col;
            this.distance = distance;
            this.priorityEstimate = priorityEstimate;
        }


    }

    //Defining the directions the nodes can traverse (NSEW)
    private static final int[][] directions = new int[][]{{-1, 0}, {0, -1}, {0, 1}, {1, 0}};

    //Function to generate neighbours
    private List<int[]> getNeighbours(int row, int col, int[][] grid) {
        List<int[]> neighbors = new ArrayList<>();
        for (int i = 0; i < directions.length; i++) {
            int newRow = row + directions[i][0];
            int newCol = col + directions[i][1];
            //to check whether it is within the boundary and also check if it is unblocked
            if (newRow < 0 || newCol < 0 || newRow >= grid.length
                    || newCol >= grid[0].length
                    || grid[newRow][newCol] != 0) {
                continue;
            }
            //if the condition is met neighbour is getting added to the list
            neighbors.add(new int[]{newRow, newCol});
        }
        return neighbors;
    }

    // Heuristic estimation
    private int heuristics(int row, int col, int[][] grid) {
        //Manhattan distance
        int heuristic = Math.abs(row - (grid.length - 1)) + Math.abs(col - (grid[0].length - 1));
        return heuristic;
    }


    //Astar Implementation
    public int Astar(int[][] newGrid, int row, int col, int distance) {

        // To Check start and goal cells are not blocked
        if (newGrid[0][0] != 0 || newGrid[newGrid.length - 1][newGrid[0].length - 1] != 0) {
            return -1;
        }
        //Using Priority queue as a fringe
        Queue<Cell> pq = new PriorityQueue<>((a, b) -> a.priorityEstimate - b.priorityEstimate);
        //adding the source node to priority queue
        pq.add(new Cell(row, col, distance, heuristics(0, 0, newGrid)));
        //visited is maintained to identify whether a cell has been popped from the fringe or not
        //If a cell gets popped out of the priority queue visited is changed to true for that respective cell
        boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];
        //Creating a hashmap to backtrack the shortestPath
        Map<Cell, Cell> pathMap = new HashMap<Cell, Cell>();


        // Checking priority queue is empty or not
        while (!pq.isEmpty()) {

            Cell bestPriority = pq.remove();

            // To check if the cell popped from the fringe is the target
            if (bestPriority.row == newGrid.length - 1 && bestPriority.col == newGrid[0].length - 1) {
                //If the track list is empty A* is called for the first time
                if (track.isEmpty()) {
                    //Backtraking the path using the hashmap
                    Cell result = pathMap.get(bestPriority);
                    System.out.print(bestPriority.row + "," + bestPriority.col);
                    System.out.print("->");
                    //Trying to fetch the path to the goal in a list
                    track.add(bestPriority);
                    //Creating a source to iterate through the hashmap
                    Cell source = new Cell(row, col, distance, heuristics(row, col, newGrid));

                    while (result.row != source.row || result.col != source.col) {
                        System.out.print(result.row + "," + result.col);
                        track.add(result);
                        System.out.print("->");
                        result = pathMap.get(result);

                    }
                    System.out.println(source.row + "," + source.col);
                    track.add(source);

                } else {
                    //Empty track list
                    track.clear();
                    Cell result = pathMap.get(bestPriority);
                    System.out.print(bestPriority.row + "," + bestPriority.col);
                    System.out.print("->");
                    track.add(bestPriority);
                    Cell source = new Cell(row, col, distance, heuristics(row, col, newGrid));
                    //backtracking to find the node
                    try {
                        while (result.row != source.row || result.col != source.col) {
                            System.out.print(result.row + "," + result.col);
                            track.add(result);
                            System.out.print("->");

                            result = pathMap.get(result);


                        }
                    } catch (Exception g) {
                        System.out.println("No path");
                        return -1;
                    }
                    track.add(source);
                    System.out.println();

                }
                //Return the shortest Path Length from start to goal if goal node is reached
                return bestPriority.distance;
            }

            // Have we popped this cell from the fringe in the past
            if (visited[bestPriority.row][bestPriority.col]) {
                continue;
            }

            visited[bestPriority.row][bestPriority.col] = true;


            for (int[] neighbour : getNeighbours(bestPriority.row, bestPriority.col, newGrid)) {
                int neighbourRow = neighbour[0];
                int neighbourCol = neighbour[1];


                //To check if the neighbours are already popped from the fringe
                if (visited[neighbourRow][neighbourCol]) {
                    continue;
                }


                //Otherwise we put that into the fringe
                int newDistance = bestPriority.distance + 1;
                int priorityEstimate = newDistance + heuristics(neighbourRow, neighbourCol, newGrid);
                Cell cell = new Cell(neighbourRow, neighbourCol, newDistance, priorityEstimate);
                pq.add(cell);

                //add the parent of the neighbour as the value in the hashmap
                pathMap.put(cell, bestPriority);
            }

        }

        // The target was unreachable.
        return -1;
    }

    //Repeated A star variant : Agent has limited view, it will be able to update the knowledge only when it bumps a block
    public int RFAstarVariant(int[][] newGrid, int row, int col, int distance, int[][] grid) {
        //Trajectory Length is set to zero
        int trajectoryLen = 0;
        //Calling Astar
        Astar(newGrid, row, col, distance);
        //reverse the track list in order to get the path in the right order
        Collections.reverse(track);

        for (int i = 0; i < track.size(); i++) {
            Cell cellValue = track.get(i);
            //check if knowledge grid is similar to the actual grid
            if (newGrid[cellValue.row][cellValue.col] != grid[cellValue.row][cellValue.col]) {
                //if it is not similar update the cell(it indicates a block)
                newGrid[cellValue.row][cellValue.col] = grid[cellValue.row][cellValue.col];
                //increase trajectory because we go the block
                trajectoryLen++;
                //increase trajectory because backtracking to the parentNode
                trajectoryLen++;
                //Parent of the blocked cell
                Cell newSource = track.get(i - 1);
                //recalling Astar with the new source as parent
                Astar(newGrid, newSource.row, newSource.col, 0);
                Collections.reverse(track);
                i = -1;
            } else {
                //increase trajectory when we go to a unblocked state
                trajectoryLen++;
            }

        }
        //the knowledge grid is fed into the Astar to derive the final path
        Astar(newGrid, 0, 0, 1);
        return trajectoryLen;

    }

    //Repeated Astar
    public int RFAstar(int[][] newGrid, int row, int col, int distance, int[][] grid) {
        //Trajectory Length is set to zero
        int trajectoryLen = 0;
        //Call A star with the knowledge grid which is currently unblocked as there is no update
        Astar(newGrid, row, col, distance);
        //reverse the track list to find path in the right order
        Collections.reverse(track);
        //to check if the node is visited or not (to check if the knowledge of the blocked cell was updated in the knowledge grid)
        boolean[][] visited = new boolean[newGrid.length][newGrid[0].length];

        for (int i = 0; i < track.size(); i++) {
            Cell cellValue = track.get(i);
            //updating the neighbour values of the cuurent cell in the knowledge grid
            for (int[] neighbour : getNeighbours(cellValue.row, cellValue.col, newGrid)) {
                int neighbourRow = neighbour[0];
                int neighbourCol = neighbour[1];
                //If you come across a block
                if (newGrid[neighbourRow][neighbourCol] != grid[neighbourRow][neighbourCol]) {
                    //update the knowledge grid
                    newGrid[neighbourRow][neighbourCol] = grid[neighbourRow][neighbourCol];
                    //change visited to true
                    visited[neighbourRow][neighbourCol] = true;
                }
            }
            //if the current cell is blocked
            if (newGrid[cellValue.row][cellValue.col] != grid[cellValue.row][cellValue.col] || visited[cellValue.row][cellValue.col] == true) {
                //rack the parent
                Cell newSource = track.get(i - 1);
                //trajectory Increases as we bump into the block
                trajectoryLen++;
                //trajectory increases as we backtrack to the parent
                trajectoryLen++;
                //call Astar with new source as the parent of the block
                Astar(newGrid, newSource.row, newSource.col, 1);

                Collections.reverse(track);
                i = -1;

            } else {
                //Increase trajectory if you traverse a unblocked cell
                trajectoryLen++;
            }

        }
        //Pass the knowledge grid to the Astar to calculate the final path
        Astar(newGrid, 0, 0, 1);
        return trajectoryLen;


    }
}

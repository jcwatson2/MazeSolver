package p5_java;

import java.io.IOException;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;



public class MazeSolver {

	// make sure to change these values based on the maze dimensions 
	// and modify svg file path
	private static final int WIDTH = 57;
	private static final int HEIGHT = 58;
	private static final String SVGFILEPATH = "src/57 by 58 orthogonal maze.svg";

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		SVGParser svgParser = new SVGParser(SVGFILEPATH, WIDTH, HEIGHT);

		svgParser.parseXML();

		Cell[] startAndFinishCells = svgParser.generateMaze();

		// identify start and finish cells
		Cell start = startAndFinishCells[0];
		Cell finish = startAndFinishCells[1];
		
		System.out.println("\nPrinting Successor Matrix:");
		printMaze(svgParser.printSuccessorMatrix(), start, finish);
		
		System.out.println("\nStarted running BFS Algorithm !");
		
		BFS(start, finish);
		
		System.out.println("\nStarted running DFS Algorithm !");

		DFS(start, finish);
		
		System.out.println("\nStarted running A* Search Algorithm !");

		// call A* search on this maze 
		aStarSearch(start, finish);
		
		System.out.print("\nPrinting the solution path:");
		printMazeSolution(svgParser.printSuccessorMatrix(), start, finish);
	}
	
	/*
	 * BFS Algorithm
	 * This method returns the number of visited cells. 
	 */
	public static int BFS(Cell start, Cell finish){
		HashMap<String,Cell> cells = SVGParser.cells;
		List<Cell> visited = new ArrayList<Cell>();
		LinkedList<Cell> queue = new LinkedList<Cell>(); 
		boolean reachedFinish = false;
		
		queue.add(start); 
		visited.add(start);
		
		while(!queue.isEmpty() && !reachedFinish){
			Cell curr = queue.poll();
			if (curr.xCoord == finish.xCoord && curr.yCoord == finish.yCoord) reachedFinish=true;
				
			ArrayList<Cell> neighbors = curr.getNeighbors();
			for (Cell neighbor: neighbors){
				if (!visited.contains(neighbor)){
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}
		System.out.println("Number of Expanded Vertices = " + visited.size());
		
		for(int i = 0; i < HEIGHT; i++) {
			for(int j = 0; j < WIDTH; j++) {
				Cell tmp = new Cell(j,i);
				boolean contains = false;
				for(Cell c : visited) {
					if(tmp.equals(c)) {
						contains = true;
						break;
					}
				}
				if(contains == true) System.out.print("1,");
				else System.out.print("0,");
			}
			System.out.println();
		}
		return visited.size();	
	}
	
	
	
	public static int DFS(Cell start, Cell finish) {
		HashMap<String,Cell> cells = SVGParser.cells;
		List<Cell> visited = new ArrayList<Cell>();
		Stack<Cell> stack = new Stack<Cell>(); 
		boolean reachedFinish = false;
		
		stack.add(start); 
		visited.add(start);
		
		while(!stack.isEmpty() && !reachedFinish){
			Cell curr = stack.pop();
			if (curr.xCoord == finish.xCoord && curr.yCoord == finish.yCoord) reachedFinish=true;
				
			ArrayList<Cell> neighbors = curr.getNeighbors();
			for (Cell neighbor: neighbors){
				if (!visited.contains(neighbor)){
					visited.add(neighbor);
					stack.push(neighbor);
				}
			}
		}
		System.out.println("Number of Expanded Vertices = " + visited.size());
		
		for(int i = 0; i < HEIGHT; i++) {
			for(int j = 0; j < WIDTH; j++) {
				Cell tmp = new Cell(j,i);
				boolean contains = false;
				for(Cell c : visited) {
					if(tmp.equals(c)) {
						contains = true;
						break;
					}
				}
				if(contains == true) System.out.print("1,");
				else System.out.print("0,");
			}
			System.out.println();
		}
		return visited.size();
	}

	/*
	 * 
	 * This method represents A* search algorithm. 
	 * It returns the number of total expanded cells for reaching finish cell from start cell.
	 * 
	 */
	public static int aStarSearch(Cell start, Cell finish){

		HashSet<Cell> visited = new HashSet<Cell>();
		boolean reachedFinish = false;

		// priority queue based on the f value 
		PriorityQueue<Cell> queue = new PriorityQueue<Cell>(WIDTH*HEIGHT, new Comparator<Cell>(){

			public int compare(Cell cell1, Cell cell2){
				if (cell1.f > cell2.f) return 1;
				if (cell1.f < cell2.f) return -1;
				return 0;
			}

		});

		// initialize g cost for the start cell
		start.g = 0;
		queue.add(start);

		while (!queue.isEmpty() && !reachedFinish){

			// retrieve cell with the lowest f value
			Cell current = queue.poll();

			// to track cells that were expanded
			visited.add(current);

			// case when finish cell is dequeued
			if (current.xCoord == finish.xCoord && current.yCoord == finish.yCoord){
				reachedFinish = true;
			}

			ArrayList<Cell> neighbors = current.getNeighbors();

			// consider all the neighbors of the current cell
			for (Cell adjacentCell : neighbors){

				// since one step is needed to move from current cell to its neighbor, increment g value
				double g = current.g + 1; 

				// Manhattan distance is used to compute h from adjacent cell to the finish cell
				// f = g + h
				//MANHATTAN DISTANCE
				double f = g + Math.abs(adjacentCell.xCoord-finish.xCoord) + 
						Math.abs(adjacentCell.yCoord-finish.yCoord);
				// EUCLIDEAN DISTANCE
//				double f = g + Math.sqrt(Math.pow((adjacentCell.xCoord-finish.xCoord), 2) + 
//						Math.pow((adjacentCell.yCoord-finish.yCoord), 2));

				// if this cell was already expanded then do not add to the queue;
				// for our maze examples we do not need to worry about cases when there are 
				// multiple paths to the same cell and we could have several f costs for same cell (no loops)
				if (visited.contains(adjacentCell)){
					continue;
				} else { 

					// set f and g values for adjacent cell
					adjacentCell.g = g;
					adjacentCell.f = f;

					// need this for backtracking purposes
					adjacentCell.parent = current;

					// add adjacent cell to the queue so that it's expanded later
					queue.add(adjacentCell);
				}
			}
		}
		
		System.out.println("Number of Expanded Vertices = " + visited.size());
		
		for(int i = 0; i < HEIGHT; i++) {
			for(int j = 0; j < WIDTH; j++) {
				Cell tmp = new Cell(j,i);
				if(j == HEIGHT-1) {
					System.out.print(Math.abs(tmp.xCoord-finish.xCoord) + 
						Math.abs(tmp.yCoord-finish.yCoord));
				}
				else {
					System.out.print(Math.abs(tmp.xCoord-finish.xCoord) + 
							Math.abs(tmp.yCoord-finish.yCoord) + ",");
				}
			}
			System.out.println();
		}
		
		System.out.println();
		
		for(int i = 0; i < HEIGHT; i++) {
			for(int j = 0; j < WIDTH; j++) {
				Cell tmp = new Cell(j,i);
				boolean contains = false;
				for(Cell c : visited) {
					if(tmp.equals(c)) {
						contains = true;
						break;
					}
				}
				if(contains == true) System.out.print("1,");
				else System.out.print("0,");
			}
			System.out.println();
		}
		return visited.size();

	}
	
	public static void printMaze(String[][] succMatrix, Cell start, Cell finish) {
		System.out.println();
		int startX = start.xCoord;
		int startY = start.yCoord;
		int endX = finish.xCoord;
		int endY = finish.yCoord;
		for(int i = 0; i < succMatrix[0].length; i++) {
			if(i == startX) {
				System.out.print("+  ");
			}
			else if(i == succMatrix[0].length-1) {
				System.out.print("+--+");
			}
			else {
				System.out.print("+--");
			}
		}
		System.out.println();
		for(int i = 0; i < succMatrix.length; i ++) {
			StringBuilder sbVert = new StringBuilder();
			sbVert.append("+");
			StringBuilder sbHorz = new StringBuilder();
			sbHorz.append("|");
			for(int j = 0; j < succMatrix[0].length; j++) {
				if(succMatrix[i][j].contains("R")) {
					sbHorz.append("   ");
				}
				else {
					sbHorz.append("  |");
				}
				if(succMatrix[i][j].contains("D")) {
					sbVert.append("  +");
				}
				else {
					sbVert.append("--+");
				}
			}
			System.out.println(sbHorz);
			System.out.println(sbVert);
			
		}
	}
	
	public static void printMazeSolution(String[][] succMatrix, Cell start, Cell finish) {
		System.out.println();
		List<Cell> solution = solutionCells(finish);
		int startX = start.xCoord;
		int startY = start.yCoord;
		int endX = finish.xCoord;
		int endY = finish.yCoord;
		for(int i = 0; i < succMatrix[0].length; i++) {
			if(i == startX) {
				System.out.print("+  ");
			}
			else if(i == succMatrix[0].length-1) {
				System.out.print("+--+");
			}
			else {
				System.out.print("+--");
			}
		}
		System.out.println();
		for(int i = 0; i < succMatrix.length; i ++) {
			Cell currentCell = solution.get(0);
			ArrayList<Integer> xCors = new ArrayList<Integer>();
			for(int x = 0; x < solution.size(); x++) {
				if(solution.get(x).yCoord == i) {
					xCors.add(solution.get(x).xCoord);
				}
				
			}
			
			StringBuilder sbVert = new StringBuilder();
			sbVert.append("+");
			StringBuilder sbHorz = new StringBuilder();
			sbHorz.append("|");
			for(int j = 0; j < succMatrix[0].length; j++) {
				if(succMatrix[i][j].contains("R")) {
					if(xCors.contains(j)) {
						sbHorz.append("## ");
					}
					else {
						sbHorz.append("   ");
					}
				}
				else {
					if(xCors.contains(j)) {
						sbHorz.append("##|");
					}
					else {
						sbHorz.append("  |");
					}
				}
				if(succMatrix[i][j].contains("D")) {
					sbVert.append("  +");
				}
				else {
					sbVert.append("--+");
				}
			}
			System.out.println(sbHorz);
			System.out.println(sbVert);
			
		}
	}
	
	public static List<Cell> solutionCells(Cell finish){
			
		Cell curr = finish;
		Cell prev = finish.parent;
		
		System.out.println();
		ArrayList<Cell> solutionCell = new ArrayList<Cell>();
		solutionCell.add(finish);
		
		while(prev!=null){
			curr = prev;
			solutionCell.add(curr);
			
			prev = curr.parent;
		}		
		// reverse the solutionPath list 
		Collections.reverse(solutionCell);
		return solutionCell;
	}
	
	public static ArrayList<String> printSolution(Cell finish){
		
		Cell curr = finish;
		Cell prev = finish.parent;
		
		System.out.println();
		ArrayList<String> solutionPath = new ArrayList<String>();
		solutionPath.add(String.valueOf(finish.xCoord + "+" + finish.yCoord));
		
		StringBuilder sb = new StringBuilder();
	
		while(prev!=null){
			if (curr.xCoord < prev.xCoord) sb.append("L");
			if (curr.xCoord > prev.xCoord) sb.append("R");
			if (curr.yCoord > prev.yCoord) sb.append("D");
			if (curr.yCoord < prev.yCoord) sb.append("U");
			curr = prev;
			// add curr to the solution path
			solutionPath.add(String.valueOf(curr.xCoord + "+" + curr.yCoord));
			
			prev = curr.parent;
		}
		System.out.println(sb.reverse().toString());
		
		// reverse the solutionPath list 
		Collections.reverse(solutionPath);
		return solutionPath;
	}
}

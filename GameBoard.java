import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class GameBoard {

	private int[] board;
	private int numOfVehicles;
	private HashSet<HashKey> visited = new HashSet<>();

	/**
	 * Initializes GameBoard to array of int with position -1 as an empty positoin
	 */
	public GameBoard() {
		board = new int[37];
		for (int i = 0; i < 37; i++) {
			board[i] = -1;
		}
	}

	/**
	 * Parses input file
	 * 
	 * @param FileName
	 * @throws IOException
	 */
	public void readInput(String FileName) throws IOException {
		File file = new File(FileName);
		if (!file.exists()) {
			throw new IOException("The specified file " + FileName + " does not exist.");
		}

		BufferedReader reader = new BufferedReader(new FileReader(file));
		numOfVehicles = Integer.parseInt(reader.readLine().trim());
		board[0] = numOfVehicles;

		for (int vehicleId = 0; vehicleId < numOfVehicles; vehicleId++) {
			String[] positions = reader.readLine().split("\\s+");

			for (String pos : positions) {
				int position = Integer.parseInt(pos);
				board[position] = vehicleId;
			}
		}
		reader.close();
	}

	/**
	 * Returns one of the shortest paths for the ice cream truck to reach the exit
	 * position
	 * 
	 * @return
	 */
	public ArrayList<Pair> getPlan() {
		Queue<State> queue = new LinkedList<>();

		HashKey initialKey = new HashKey(board);
		State initialState = new State(initialKey, null, null);

		queue.add(initialState);
		visited.add(initialKey);

		while (!queue.isEmpty()) {
			State currentState = queue.poll();

			if (isGoalState(currentState.key)) {
				return reconstructPath(currentState);
			}

			int[] currentBoard = currentState.key.c;
			List<MoveAndBoard> successors = generateSuccessors(currentBoard);
			for (MoveAndBoard successor : successors) {
				HashKey successorKey = new HashKey(successor.board);
				if (!visited.contains(successorKey)) {
					visited.add(successorKey);
					State successorState = new State(successorKey, currentState, successor.move);
					queue.add(successorState);
				}
			}
		}
		return new ArrayList<Pair>();
	}

	/**
	 * Returns the total number of shortests paths for the ice cream truck to reach
	 * the final position
	 * 
	 * @return
	 */
	public int getNumOfPaths() {
		Queue<State> queue = new LinkedList<>();
		HashMap<HashKey, State> visited = new HashMap<>();

		HashKey initialKey = new HashKey(board);
		State initialState = new State(initialKey, null, null);
		initialState.explored = true;
		initialState.layer = 0;
		initialState.cnt = 1;

		queue.add(initialState);
		visited.put(initialKey, initialState);

		while (!queue.isEmpty()) {
			State currentState = queue.poll();

			// Check target configuration is reached
			if (isGoalState(currentState.key)) {
				return currentState.cnt; // Number of shortest paths to target state
			}

			int[] currentBoard = currentState.key.c;
			List<MoveAndBoard> successors = generateSuccessors(currentBoard);
			for (MoveAndBoard successor : successors) {
				HashKey successorKey = new HashKey(successor.board);
				if (!visited.containsKey(successorKey)) {
					State successorState = new State(successorKey, currentState, successor.move);
					successorState.explored = true;
					successorState.layer = currentState.layer + 1;
					successorState.cnt = currentState.cnt;
					visited.put(successorKey, successorState);
					queue.add(successorState);
				} else {
					State retrievedState = visited.get(successorKey);
					// w was explored via a different vertex already
					if (retrievedState.layer == currentState.layer + 1) {
						retrievedState.cnt += currentState.cnt;
					}
				}
			}
		}

		// If we exit the loop without returning, the target was not reachable
		return 0;
	}

	/**
	 * Checks if goal state has been reached; returns false if ice cream truck is in
	 * goal state but is vertical
	 * 
	 * @param key - HashKey of configuration to check
	 * @return true or false, depending on if state is goal state
	 */
	private boolean isGoalState(HashKey key) {
		// Check if the red car can exit using the board state represented by the key.
		int[] boardConfiguration = key.c;

		if (boardConfiguration[18] == 0 && boardConfiguration[18 + 6] != 0 && boardConfiguration[18 - 6] != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Generates the successors (neighbors) of a current board configuration
	 * 
	 * @param currentBoard - the current board configuration
	 * @return list of MoveAndBoard class, which stores the successor (neighbor)
	 *         board configuration and the move (Pair) used to get to that
	 *         configuration
	 */
	private List<MoveAndBoard> generateSuccessors(int[] currentBoard) {
		// Generate and return list of valid successor board configurations.
		List<MoveAndBoard> successors = new ArrayList<>();

		for (int vehicle = 0; vehicle < currentBoard[0]; vehicle++) {
			List<Pair> possibleMoves = generatePossibleMoves(vehicle, currentBoard);

			for (Pair move : possibleMoves) {
				int[] newBoard = applyMoveToBoard(move, currentBoard);
				successors.add(new MoveAndBoard(newBoard, move));
			}
		}
		return successors;
	}

	/**
	 * Generates all possible moves that a specific vehicle can make on the current
	 * board.
	 * 
	 * @param vehicle      - vehicle number
	 * @param currentBoard - current board configuration
	 * @return - list of all possible moves (Pair)
	 */
	private List<Pair> generatePossibleMoves(int vehicle, int[] currentBoard) {
		List<Pair> possibleMoves = new ArrayList<>();
		int[] positions = getVehiclePosition(vehicle, currentBoard);
		int vehicleLength = positions.length;
		int startPos = positions[0];
		int endPos = positions[vehicleLength - 1];

		if (positions[1] - positions[0] == 6) {
			int movesUp = -6;
			int movesDown = 6;

			if (startPos - 6 <= 0 || startPos + movesUp <= 0) {
			} else if (currentBoard[startPos + movesUp] == -1) {
				possibleMoves.add(new Pair(vehicle, 'n'));
				movesUp += -6;
			} else {
			}

			if (endPos + 6 > 36 || endPos + movesDown > 36) {
			} else if (currentBoard[endPos + movesDown] == -1) {
				possibleMoves.add(new Pair(vehicle, 's'));
				movesDown += 6;
			} else {
			}
		} else {
			int moveRight = 1;
			int moveLeft = -1;

			if (endPos % 6 == 0) {
			} else if (currentBoard[endPos + moveRight] == -1) {
				possibleMoves.add(new Pair(vehicle, 'e'));
				moveRight++;
			} else {
			}

			if ((endPos + moveRight) % 6 == 0) {
			}

			if ((startPos - 1) % 6 == 0) {
			} else if (currentBoard[startPos + moveLeft] == -1) {
				possibleMoves.add(new Pair(vehicle, 'w'));
				moveLeft--;
			} else {
			}

			if ((startPos + moveLeft - 1) % 6 == 0) {
			}

		}

		return possibleMoves;
	}

	/**
	 * Applies a move to a current board
	 * 
	 * @param move         - move to be applied
	 * @param currentBoard - the current board configuration
	 * @return a new board after the move is applied
	 */
	private int[] applyMoveToBoard(Pair move, int[] currentBoard) {
		int[] newBoard = copyBoard(currentBoard);
		char direction = move.getDirection();
		int vehicleId = move.getId();
		int[] newPosition = getVehiclePosition(vehicleId, currentBoard);

		switch (direction) {
		case 'n':
			for (int i = 0; i < newPosition.length; i++) {
				newBoard[newPosition[i]] = -1;
				newPosition[i] -= 6;
				newBoard[newPosition[i]] = vehicleId;
			}
			break;
		case 's':
			for (int i = newPosition.length - 1; i >= 0; i--) {
				newBoard[newPosition[i]] = -1;
				newPosition[i] += 6;
				newBoard[newPosition[i]] = vehicleId;
			}
			break;
		case 'e':
			for (int i = newPosition.length - 1; i >= 0; i--) {
				newBoard[newPosition[i]] = -1;
				newPosition[i] += 1;
				newBoard[newPosition[i]] = vehicleId;
			}
			break;
		case 'w':
			for (int i = 0; i < newPosition.length; i++) {
				newBoard[newPosition[i]] = -1;
				newPosition[i] -= 1;
				newBoard[newPosition[i]] = vehicleId;
			}
			break;
		}

		return newBoard;
	}

	/**
	 * Reconstructs the shortest path
	 * 
	 * @param endState - final state after goal has been reached
	 * @return - returns the list of moves (Pair) that created the path
	 */
	private ArrayList<Pair> reconstructPath(State endState) {
		ArrayList<Pair> path = new ArrayList<>();

		State currentState = endState;

		while (currentState.prevState != null) {
			path.add(0, currentState.moveMadeToReachThisState);
			currentState = currentState.prevState;
		}

		return path;
	}

	/**
	 * Makes a deep copy of a board
	 * 
	 * @param board
	 * @return
	 */
	public int[] copyBoard(int[] board) {
		return board.clone();
	}

	/**
	 * Retrieves the starting and ending position of a vehicle in a board
	 * 
	 * @param id           - vehicle number
	 * @param currentBoard - current board configuration
	 * @return list of the positions of the vehicle
	 */
	public int[] getVehiclePosition(int id, int[] currentBoard) {
		List<Integer> positions = new ArrayList<>();
		for (int i = 1; i < currentBoard.length; i++) {
			if (currentBoard[i] == id) {
				positions.add(i);
			}
		}
		return positions.stream().mapToInt(i -> i).toArray();
	}

}

/**
 * Pair class that was given in assignment description
 * 
 * @author Rangsimun Bargmann
 *
 */
class Pair {
	// as described above
	// Any other helper attributes and methods
	int id;
	char direction; // {’e’, ’w’, ’n’, ’s’}

	public Pair(int i, char d) {
		id = i;
		direction = d;
	}

	char getDirection() {
		return direction;
	}

	int getId() {
		return id;
	}

	void setDirection(char d) {
		direction = d;
	}

	void setId(int i) {
		id = i;
	}
}

/**
 * HashKey class that was given in assignment description
 * 
 * @author Rangsimun Bargmann
 *
 */
class HashKey {
	int[] c; // attribute

	public HashKey(int[] inputc) {
		c = new int[inputc.length];
		c = inputc;
	}

	public boolean equals(Object o) {
		boolean flag = true;
		if (this == o)
			return true; // same object
		if ((o instanceof HashKey)) {
			HashKey h = (HashKey) o;
			int[] locs1 = h.c;
			int[] locs = c;
			if (locs1.length == locs.length) {
				for (int i = 0; i < locs1.length; i++) { // mismatch
					if (locs1[i] != locs[i]) {
						flag = false;
						break;
					}
				}
			} else // different size
				flag = false;
		} else // not an instance of HashKey
			flag = false;
		return flag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Arrays.hashCode(c); // using default hashing of arrays
	}
}

/**
 * State class which represents the current state of the board Stores key of the
 * board, it's parent state, and the move made to reach this state
 * 
 * @author rangsimunbargmann
 *
 */
class State {
	HashKey key;
	State prevState;
	Pair moveMadeToReachThisState;
	int cnt;
	int layer;
	boolean explored;

	public State(HashKey key, State prevState, Pair move) {
		this.key = key;
		this.prevState = prevState;
		this.moveMadeToReachThisState = move;
	}
}

/**
 * Stores a board configuration and the move made to reach that configuration
 * 
 * @author Rangsimun Bargmann
 *
 */
class MoveAndBoard {
	int[] board;
	Pair move;

	public MoveAndBoard(int[] board, Pair move) {
		this.board = board;
		this.move = move;
	}
}

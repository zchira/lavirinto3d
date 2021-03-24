package levelgenerator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class Path extends ArrayList<Integer> {
}

/**
 * Auto generate level.
 * 
 * @author zchira
 *
 */
public class LevelGenerator {

	// level width
	int w;
	// level height
	int h;
	// max number of fields (max w*h)
	int maxFields;
	// prng seed
	long seed;
	Random rnd;

	// level matix
	int[][] matrix;
	// start field. Fields are enumerated from 0 (top,left) to w*h-1 (bottom, right)
	int start = 0;

	List<Path> paths = new ArrayList<Path>();
	Path currentPath;
	int stackPointer = -1;

	public LevelGenerator(int w, int h, long seed, int maxFields) {
		this.w = w;
		this.h = h;
		this.maxFields = maxFields;
		this.seed = seed;
		rnd = new Random(seed);
		this.matrix = new int[w * 3][h * 3];

		start = (int) (rnd.nextFloat() * w * h);

		currentPath = new Path();
		currentPath.add(start);
		paths.add(currentPath);
		stackPointer = 0;
		generate(start);
		printMatrix();
		mix();
	}

	public void print() {
		for (Path p : paths) {
			for (int i = 0; i < p.size(); i++) {
				System.out.println(p.get(i));
			}
			System.out.println();
		}
	}

	public void printMatrix() {
		int sx = getX(start) * 3 + 1;
		int sy = getY(start) * 3 + 1;
		
		
		for (int y = 0; y < h * 3; y++) {
			// if (y % 3 == 0)
			// System.out.println();

			for (int x = 0; x < w * 3; x++) {
				if (x % 3 == 0)
					System.out.print("");

				int val = matrix[x][y];
				String c = val == 0 ? "-" : "O";
				if (sx == x && sy == y)
					c = "X";

				System.out.print(c);

			}
			System.out.println();
		}
	}

	private void generate(int currentId) {

		while (pathSize() < this.maxFields) {
			int nextId = getRandomFreeNeghbour(currentId);
			if (nextId > -1) {
				if (stackPointer < currentPath.size() - 1) {
					currentPath = new Path();
					currentPath.add(currentId);
					stackPointer = 0;
					paths.add(currentPath);
				}

				connectFields(currentId, nextId);
				// addRandomConnections(currentId);
				currentPath.add(nextId);
				stackPointer = currentPath.size() - 1;
				currentId = nextId;
			} else {
				stackPointer--;
				if (stackPointer < 0) {
					// take previous path
					int i = paths.indexOf(currentPath);
					currentPath = paths.get(i - 1);
					stackPointer = currentPath.size() - 1;
				}
				currentId = currentPath.get(stackPointer);
			}

		}
	}

	private void mix() {
		for (Path currentPath : paths) {
			//currentPath = paths.get(paths.size() - 1);
			int pathPointer = currentPath.size() - 1;
			while (pathPointer > 0) {
				int currentId = currentPath.get(pathPointer);
				int rotate = rnd.nextInt(4);
				rotateCw(currentId, rotate);
				pathPointer--;
				int nextId = currentPath.get(pathPointer);
				connectFields(currentId, nextId);
			}

		}
	}

	private void rotateCw(int id, int n) {
		for (int i = 0; i < n; i++) {
			int x = getX(id) * 3 + 1;
			int y = getY(id) * 3 + 1;
			int tmp = matrix[x + 1][y]; // tmp = right
			matrix[x + 1][y] = matrix[x][y - 1]; // r = u
			matrix[x][y - 1] = matrix[x - 1][y]; // u = l
			matrix[x - 1][y] = matrix[x][y + 1]; // l = d
			matrix[x][y + 1] = tmp; // d = tmp
		}

	}

	private int pathSize() {
		int toRet = 0;
		for (Path path : paths) {
			toRet += path.size();
		}
		// beginning of every path exists in other path(s)
		toRet = toRet - paths.size() + 1;
		return toRet;
	}

	private boolean pathsContains(int currentId) {
		for (Path path : paths) {
			if (path.contains(currentId))
				return true;
		}
		return false;
	}

	private void addRandomConnections(int currentId) {
		// TODO Auto-generated method stub

	}

	private void connectFields(int currentId, int nextId) {
		int x1 = getX(currentId) * 3 + 1;
		int y1 = getY(currentId) * 3 + 1;
		int x2 = getX(nextId) * 3 + 1;
		int y2 = getY(nextId) * 3 + 1;

		if (x1 == x2) {
			int start = y1 < y2 ? y1 : y2;
			int end = y1 < y2 ? y2 : y1;
			for (int i = start; i <= end; i++) {
				matrix[x1][i] = 1;
			}
		} else if (y1 == y2) {
			int start = x1 < x2 ? x1 : x2;
			int end = x1 < x2 ? x2 : x1;
			for (int i = start; i <= end; i++) {
				matrix[i][y1] = 1;
			}
		}
	}

	private int getRandomFreeNeghbour(int fieldId) {
		List<Integer> free = getFreeNeighbours(fieldId);
		int size = free.size();
		if (size == 0)
			return -1;

		float f = rnd.nextFloat();
		int rndIndex = Math.round(f * (size - 1));
		return free.get(rndIndex);
	}

	/**
	 * 
	 * @param currentId
	 * @return collection of neghbour field that are not traversed (not in stack)
	 */
	private List<Integer> getFreeNeighbours(int currentId) {
		List<Integer> allN = getAllNeighbours(currentId);
		Iterator<Integer> it = allN.iterator();
		while (it.hasNext()) {
			int next = it.next();
			if (pathsContains(next))
				it.remove();
		}

		return allN;
	}

	/**
	 * 
	 * @param fieldId
	 * @return x coord of field
	 */
	private int getX(int fieldId) {
		return fieldId % this.w;
	}

	private int getY(int fieldId) {
		return fieldId / this.w;
	}

	/**
	 * Converts x and y coords to field id
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private int coordsToId(int x, int y) {
		return y * w + x;
	}

	/**
	 * 
	 * @return id of left field or -1
	 */
	private int getLeftId(int fieldId) {
		int x = getX(fieldId);
		int y = getY(fieldId);
		if (x == 0) {
			return -1;
		}

		x--;
		return coordsToId(x, y);
	}

	private int getRightId(int fieldId) {
		int x = getX(fieldId);
		int y = getY(fieldId);
		if (x == this.w - 1) {
			return -1;
		}

		x++;
		return coordsToId(x, y);
	}

	private int getUpId(int fieldId) {
		int x = getX(fieldId);
		int y = getY(fieldId);
		if (y == 0) {
			return -1;
		}

		y--;
		return coordsToId(x, y);
	}

	private int getDownId(int fieldId) {
		int x = getX(fieldId);
		int y = getY(fieldId);
		if (y == this.h - 1) {
			return -1;
		}

		y++;
		return coordsToId(x, y);
	}

	private List<Integer> getAllNeighbours(int fieldId) {
		int x = getX(fieldId);
		int y = getY(fieldId);
		List<Integer> arr = new ArrayList<Integer>();

		int id = getUpId(fieldId);
		if (id > -1)
			arr.add(id);

		id = getRightId(fieldId);
		if (id > -1)
			arr.add(id);

		id = getDownId(fieldId);
		if (id > -1)
			arr.add(id);

		id = getLeftId(fieldId);
		if (id > -1)
			arr.add(id);

		return arr;

	}
}

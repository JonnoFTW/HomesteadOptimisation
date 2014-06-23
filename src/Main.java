import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

class Main {
	private static double STEP = 0.01;
	private double[] xPoints = new double[] { 0, 5, 20, 30, 50 };
	private double[] yPoints = new double[] { 0, 15, -10, 10, 0 };
	private double maxX = xPoints[0];
	private double maxY = yPoints[0];
	private double minX = xPoints[0];
	private double minY = yPoints[0];

	private static Random rand = new Random();
	/**
	 * Setup the variables for the bounding rectangle
	 */
	private void setMax() {
		for (int i = 0; i < xPoints.length; i++) {
			maxX = Math.max(maxX, xPoints[i]);
			maxY = Math.max(maxY, yPoints[i]);
			minX = Math.min(minX, xPoints[i]);
			minY = Math.min(minY, yPoints[i]);
		}
	}

	/**
	 * A class that represents a point
	 * @author Jonathan
	 *
	 */
	private class Point {
		public double x, y,starLength,equationLength;
		public Point equationStart,equationEnd;

		public Point(Double x, Double y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Calculate the distance from this point to the
		 * other point 
		 * @param p the other point
		 * @return the distance between this and p
		 */
		public double distance(Point p) {
			return Math.sqrt(Math.pow((x - p.x), 2) + Math.pow((y - p.y), 2));
		}

		/**
		 * The distance from this point to a line
		 * @param p the line where x is slope and y is the intercept
		 * @return
		 */
		public double distanceToLine(Point p) {
			return Math.sqrt(Math.pow(y - p.x * x + p.y, 2) / (p.x * p.x + 1));
		}
		/**
		 * Find the length of cable required if this was a
		 * candidate point using the star setup
		 * @return the length of cable
		 */
		public double findStarLength() {
			double length = 0;
			for (int i = 0; i < xPoints.length; i++) {
				length += distance(new Point(xPoints[i], yPoints[i]));
			}
			starLength = length;
			return length;
		}
		/**
		 * Find the length of cable required if this was a line
		 * @return
		 */
		public double findEquationLength() {
			double candidateTotalLength = 0;
			ArrayList<Point> points = new ArrayList<Point>(5);
			for (int i = 0; i < xPoints.length; i++) {
				double x1 = xPoints[i];
				double y1 = yPoints[i];
				candidateTotalLength += new Point(x1, y1).distanceToLine(this);
				// find where these perpendicular points meet the line
				double m = x;
				double c = y;
				points.add(new Point((m * y1 + x1 - m*c) / (m*m + 1),(m*m * y1 + m*x1 + c) / (m*m + 1)));
			}
			// Add the greatest distance between the points
			double lineLength = Double.MIN_VALUE;
			for (Point point : points) {
				for (Point point2 : points) {
					if(point != point2) {
						double dist = point.distance(point2);
						if(dist > lineLength) {
							lineLength = dist;
							equationStart = point;
							equationEnd = point2;
						}
					}
				}
			}
			candidateTotalLength += lineLength;
			equationLength = candidateTotalLength;
			return equationLength;
		}
		public String toString() {
			return x + "," + y;
		}
	}
	/**
	 * Solve the homestead problem if we use a star.
	 * We only search in the bounding rectangle
	 * This search is exhaustive within the rectangle
	 */
	public void solveMid() {
		Point minimum = null;
		double totalLengthMin = Double.MAX_VALUE;
		
		for (double x = minX; x <= maxX; x += STEP) {
			for (double y = minY; y <= maxY; y += STEP) {
				Point candidate = new Point(x, y);
				double candidateTotalLength = candidate.findStarLength();
				if (candidateTotalLength < totalLengthMin) {
					totalLengthMin = candidateTotalLength;
					minimum = candidate;
				}
			}
		}
		System.out.println("Minimum point is :" + minimum
				+ " with total length " + totalLengthMin);
	}
	/**
	 * Solve the problem as though it was a line going through the area
	 */
	public void solveEquation() {
		Point minimum = null;
		double totalLengthMin = Double.MAX_VALUE;
		
		for (double x = -500; x <= 500; x += 1) {
			for (double y = -3000 ; y <= 3000; y += 1) {
				Point candidate = new Point(x, y);
				double candidateTotalLength = candidate.findEquationLength();
				if (candidateTotalLength < totalLengthMin) {
					totalLengthMin = candidateTotalLength;
					minimum = candidate;
				}
			}
		}
		System.out.println(String.format(
				"Minimum line is : %fx+%f with total length %f from %s to %s", minimum.x,
				minimum.y, minimum.equationLength,minimum.equationStart,minimum.equationEnd));
	}
	/**
	 * This method solves the star problem using the hill climbing method
	 * I wrote it after I had submitted the assignment
	 */
	public void solvemidHillClimbing() {
		// Take a guess at where the minimum is
		Point minimum = new Point(0d,0d);
		while(true) {
			// find the length of the points around the current
			ArrayList<Point> surroundingPoints = new ArrayList<>(8);
			double x = minimum.x;
			double y = minimum.y;
			surroundingPoints.add(new Point(x+STEP, y));
			surroundingPoints.add(new Point(x-STEP, y));
			surroundingPoints.add(new Point(x+STEP, y+STEP));
			surroundingPoints.add(new Point(x+STEP, y-STEP));
			surroundingPoints.add(new Point(x-STEP, y+STEP));
			surroundingPoints.add(new Point(x-STEP, y-STEP));
			surroundingPoints.add(new Point(x, y+STEP));
			surroundingPoints.add(new Point(x, y-STEP));
			for (Point point : surroundingPoints) {
				point.findStarLength();
			}
			
			Point candidateMinimum = Collections.min(surroundingPoints, new Comparator<Point>() {
				@Override
				public int compare(Point o1, Point o2) {
					return Double.compare(o1.starLength, o2.starLength);
				}
			});
			// If the current length is the less than those around it we have found the minimum
			if(minimum.findStarLength() < candidateMinimum.starLength) {
				break;
			}
			// otherwise we repeat with the minimum of the surrounding
			minimum = candidateMinimum;
		}
		System.out.println(String.format(
				"Minimum point is : %fx+%f with total length %f", minimum.x,minimum.y, minimum.starLength));
	}
	/**
	 * This method solves the line problem using the hill climbing method
	 * I wrote it after I had submitted the assignment.
	 * 
	 *  This should be implemented using simulated 
	 * annealing to attempt to find a global minimum
	 * http://en.wikipedia.org/wiki/Simulated_annealing
	 * 
	 * This will perform a random walk, once it has found a local minimum
	 * It will 'jump' off somewhere and see if that leads to another local minimum
	 * 
	 * It seems that there are many local minima and that the surface isn't smooth
	 * This is probably the case where actually doing the calculus would yield a more reliable answer
	 * 
	 * I've sort of cheated here and put the initial guess at the same location that 
	 * the brute force method uses (so it does find the right answer).
	 *  Moving the initial guess away will likely not find the  global minimum.
	 *  
	 *  Performance could be improved by using multiple threads.
	 */
	public void solveEquationHillClimbing() {
		// Take a guess at where the minimum is
		Point minimum = new Point(5d,5d);
		int kMax =  50;
		int k = 0;
		Point globalMinimum = null;
		while(true) {
			// find the length of the points around the current
			ArrayList<Point> surroundingPoints = new ArrayList<>(8);
			double x = minimum.x;
			double y = minimum.y;
			surroundingPoints.add(new Point(x+STEP, y));
			surroundingPoints.add(new Point(x-STEP, y));
			surroundingPoints.add(new Point(x+STEP, y+STEP));
			surroundingPoints.add(new Point(x+STEP, y-STEP));
			surroundingPoints.add(new Point(x-STEP, y+STEP));
			surroundingPoints.add(new Point(x-STEP, y-STEP));
			surroundingPoints.add(new Point(x, y+STEP));
			surroundingPoints.add(new Point(x, y-STEP));
			for (Point point : surroundingPoints) {
				point.findEquationLength();				
			}
			// If the current length is the less than those around it
			// we have found the minimum
			Point candidateMinimum = Collections.min(surroundingPoints, new Comparator<Point>() {
				@Override
				public int compare(Point o1, Point o2) {
					return Double.compare(o1.equationLength, o2.equationLength);
				}
			});
			if(minimum.findEquationLength() < candidateMinimum.equationLength) {
				// Repeat this process kMax times 
				if(k > kMax) {
					break;
				} else {
					k++;
					if(globalMinimum == null || minimum.equationLength < globalMinimum.equationLength)
						globalMinimum = minimum;
					// Perform a random jump somewhere else in the vicinity
					minimum = new Point((double)randIntRange(-100, 100),(double)randIntRange(-100, 100));
				}
				
			} else {
				// otherwise we repeat with the minimum of the surrounding
				minimum = candidateMinimum;
			}
		}
		// find the global minimum from our kMax attempts
		System.out.println(String.format(
				"Minimum line is : %fx+%f with total length %f from %s to %s", globalMinimum.x,
				globalMinimum.y, globalMinimum.equationLength,globalMinimum.equationStart,globalMinimum.equationEnd));
	}
	private int randIntRange(int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
    private static double acceptanceProbability(double currentEnergy, double neighbourEnergy, double temperature) {
        // If the new solution is better, accept it
        if (neighbourEnergy < currentEnergy) {
            return 1.0;
        }
        // If the new solution is worse, calculate an acceptance probability
        return Math.exp((currentEnergy - neighbourEnergy) / temperature);
    }
	/**
	 * Attempt to solve the line equation using simulated annealing
	 * as an extension to hill climbing
	 * Probably the fastest method of all even if the 
	 * initial guess is far away from the global minimum
	 * http://cs.gettysburg.edu/~tneller/papers/flairs05.pdf
	 */
	private void solveEquationSimulatedAnnealing() {
		int kMax = 50;
		int k = 0;
		double temp = 10000;
		double coolingRate = 1 - 0.003;
		// Take a guess at the solution
		Point minimum = new Point(100d,100d);
		while(temp > 1) {
			double currentEnergy = minimum.findEquationLength();
			Point newSolution = new Point(minimum.x*rand.nextGaussian(),minimum.y*rand.nextGaussian());
			double neighbourEnergy = newSolution.findEquationLength();
			if(acceptanceProbability(currentEnergy, neighbourEnergy, temp) > rand.nextDouble()) {
				minimum = newSolution; 
			}
			temp *= coolingRate; 
		}
		System.out.println(String.format(
				"Minimum line is : %fx+%f with total length %f from %s to %s", minimum.x,
				minimum.y, minimum.equationLength,minimum.equationStart,minimum.equationEnd));
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		Main m = new Main();
		m.setMax();
		m.solvemidHillClimbing();
		System.out.println("\tStar Hill climbing: Found in "
				+ ((System.nanoTime() - start) / 1000000000f));
		start = System.nanoTime();
		m.solveMid();
		System.out.println("\tStar: Found in "
				+ ((System.nanoTime() - start) / 1000000000f));
		start = System.nanoTime();
		m.solveEquationHillClimbing();
		System.out.println("\tEquation hill climbing: Found in "
				+ ((System.nanoTime() - start) / 1000000000f));
		start = System.nanoTime();
		m.solveEquationSimulatedAnnealing();
		System.out.println("\tEquation simulated annealing: Found in "
				+ ((System.nanoTime() - start) / 1000000000f));
		
		start = System.nanoTime();
		m.solveEquation();
		System.out.println("\tEquation: Found in "
				+ ((System.nanoTime() - start) / 1000000000f));
	}

}
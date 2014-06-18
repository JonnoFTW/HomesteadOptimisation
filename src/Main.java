import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class Main {
	private static double STEP = 0.01;
	private double[] xPoints = new double[] { 0, 5, 20, 30, 50 };
	private double[] yPoints = new double[] { 0, 15, -10, 10, 0 };
	private double maxX = xPoints[0];
	private double maxY = yPoints[0];
	private double minX = xPoints[0];
	private double minY = yPoints[0];

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
		public double x, y;

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

		public String toString() {
			return x + "," + y;
		}
	}

	public void solveMid() {
		Point minimum = null;
		Point maximum = null;
		double totalLengthMin = Double.MAX_VALUE;
		double totalLengthMax = Double.MIN_VALUE;
		//File f= new File("plot2.dat");
		//try {
		//if(!f.exists())
		//	f.createNewFile();
		
		//	BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (double x = minX; x <= maxX; x += STEP) {
				for (double y = minY; y <= maxY; y += STEP) {
					Point candidate = new Point(x, y);
					float candidateTotalLength = 0;
					for (int i = 0; i < xPoints.length; i++) {
						candidateTotalLength += new Point(xPoints[i], yPoints[i])
								.distance(candidate);
					}
			//		bw.write(String.format("%1.2f %1.2f %1.2f%n", x,y,candidateTotalLength));
					if (candidateTotalLength < totalLengthMin) {
						totalLengthMin = candidateTotalLength;
						minimum = candidate;
					}
					if (candidateTotalLength > totalLengthMax) {
						totalLengthMax = candidateTotalLength;
						maximum = candidate;
					}
				}
			}
			//bw.close();
		//} catch (IOException e) {
		//	// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		
		System.out.println("Minimum point is :" + minimum
				+ " with total length " + totalLengthMin);
		System.out.println("Maximum point is :" + maximum
				+ " with total length " + totalLengthMax);
	}

	public void solveEquation() {
		Point minimum = null;
		Point maximum = null;
		double totalLengthMin = Double.MAX_VALUE;
		//double totalLengthMax = Double.MIN_VALUE;
		Point start = null;
		Point end = null;
		for (double m = -500; m <= 500; m += 1) {
			for (double c = minY; c <= 3000; c += 1) {
				// candidate is a line
				// it's length is something...
				Point candidate = new Point(m, c);
				float candidateTotalLength = 0;
				ArrayList<Point> points = new ArrayList<Point>(5);
				for (int i = 0; i < xPoints.length; i++) {
					double x1 = xPoints[i];
					double y1 = yPoints[i];
					candidateTotalLength += new Point(x1, y1)
							.distanceToLine(candidate);
					// find where these perpendicular points meet the line
					points.add(new Point((m * y1 + x1 - m*c) / (m*m + 1),(m*m * y1 + m*x1 + c) / (m*m + 1)));
				}
				// Add the greatest distance between the points
				Point candidateStart = null;
				Point candidateEnd = null;
				double lineLength = Double.MIN_VALUE;
				for (Point point : points) {
					for (Point point2 : points) {
						double dist = point.distance(point2);
						if(dist > lineLength) {
							lineLength = dist;
							candidateEnd = point;
							candidateStart = point2;
						}
					}
				}
				candidateTotalLength += lineLength;
				if (candidateTotalLength < totalLengthMin) {
					totalLengthMin = candidateTotalLength;
					minimum = candidate;
					end = candidateEnd;
					start = candidateStart;
				}
				/*if (candidateTotalLength > totalLengthMax) {
					totalLengthMax = candidateTotalLength;
					maximum = candidate;
				}*/
			}
		}
		System.out.println(String.format(
				"Minimum line is : %fx+%f with total length %f from "+ start+" to "+end, minimum.x,
				minimum.y, totalLengthMin));
		/*System.out.println(String.format(
				"Maximum line is : %fx+%f with total length %f", maximum.x,
				maximum.y, totalLengthMax));*/
	}

	public static void main(String[] args) {
		long start = System.nanoTime();
		Main m = new Main();
		m.setMax();
		/*m.solveMid();
		System.out.println("Found in "
				+ ((System.nanoTime() - start) / 1000000000f));*/
		start = System.nanoTime();
		m.solveEquation();
		System.out.println("Found in "
				+ ((System.nanoTime() - start) / 1000000000f));
	}
}
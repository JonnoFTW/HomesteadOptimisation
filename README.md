HomesteadOptimisation
=====================

Brute forces the sum of distances between a central point and some given points. It reports the maximum and minimum lengths.

It calculates using both a central point and a straight line.

The following methods are used:

Star
-----
 
- Brute force: calculates the distance from all points to the mystery point in the region bounded by the outermost homesteads
- Hill climbing: starting from some guess, the program iteratively moves toward the point with lower total length

Line
-----
 
- Brute force calculates the distance from each homestead to a candidate line using the formula y=mx+c
- Hill climbing with random walks, performs hill climbing but makes a random jump when a candidate minimum is found, probably won't find the global minimum
- Simulated annealing: uses simulated annealing, gradually cooling the system until a global minimum is found (by far the fastest method), see: http://cs.gettysburg.edu/~tneller/papers/flairs05.pdf

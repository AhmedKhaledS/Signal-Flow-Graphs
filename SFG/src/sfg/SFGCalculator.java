package sfg;

import java.util.ArrayList;
import java.util.List;

public class SFGCalculator {
	private double delta = 1.0;
	private List<Pair<Path, Double>> loops;
	private List<Pair<Path, Double>> forwardPaths;
	private double[] deltas;
	private boolean[] marker;
	private boolean changed;
	private static final int MAX_NODES = 100;

	
	public SFGCalculator(GraphTraversal graph) {
		loops = graph.getLoops();
		forwardPaths = graph.getForwardPaths();
		marker = new boolean[MAX_NODES];
	}
	
	
       public void computeDeltas() {
		//double delta = 1.0;
		for (Pair<Path, Double> indivLoops : loops) {
			delta -= indivLoops.getR();
		}
		deltas = new double[forwardPaths.size()];
		changed = true;
		// Computing Delta.
		for (int sz = 2; sz < MAX_NODES && changed; sz++) {
			changed = false;
			for (int i = 0; i < MAX_NODES; i++) {
				marker[i] = false;
			}
			getNonTouchingLoopsCombGain(0, 0, sz, -1, new ArrayList<>());
		}
		changed = true;
		// Computing deltas with each forward path.
		for (int pathIndex = 0; pathIndex < forwardPaths.size(); pathIndex++) {
			deltas[pathIndex] = 1.0;
			changed = true;
			for (int sz = 1; sz < 100 && changed; sz++) {
				changed = false;
				for (int i = 0; i < 100; i++) {
					marker[i] = false;
				}
				getNonTouchingLoopsCombGain(0, 0, sz, pathIndex, forwardPaths.get(pathIndex).getL().getPath());
			}
		}
	}
	
	private void getNonTouchingLoopsCombGain(int index, int taken, int sz, int pathIndex, List<String> path) {
		if (taken == sz) {
			if (checkForValidCombination(path)) {
				double temp = 1.0;
				for (int i = 0; i < loops.size(); i++) {
					if (marker[i]) {
						changed = true;
						temp *= loops.get(i).getR();
					}
				}
				if (pathIndex == -1) {
					if (sz % 2 == 0) {
						delta += temp;
					} else {
						delta -= temp;
					}					
				} else {
					if (sz % 2 == 0) {
						deltas[pathIndex] += temp;
					} else {
						deltas[pathIndex] -= temp;
					}
				}
			}
			return;
		}
		if (index >= loops.size()) return;
		marker[index] = true;
		getNonTouchingLoopsCombGain(index + 1, taken + 1, sz, pathIndex, path);
		marker[index] = false;
		getNonTouchingLoopsCombGain(index + 1, taken, sz, pathIndex, path);
	}
	
	private boolean checkForValidCombination(List<String> path) {
		for (int i = 0; i < loops.size(); i++) {
			if (marker[i]) {
				List<String> reference = new ArrayList<>();
				for (String node : loops.get(i).getL().getPath()) {
					reference.add(node);
				}
				for (int k = 0; k < path.size(); k++) {
					for (int l = 0; l < reference.size(); l++) {
						if (path.get(k).equals(reference.get(l))) {
							return false;
						}
					}
				}
				for (int j = i + 1; j < loops.size(); j++) {
					List<String> compared = new ArrayList<>();
					if (marker[j]) {
						for (String node : loops.get(j).getL().getPath()) {
							compared.add(node);
						}
					}
					for (int k = 0; k < reference.size(); k++) {
						for (int l = 0; l < compared.size(); l++) {
							if (reference.get(k).equals(compared.get(l))) {
								return false;
							}
						}
					}
					for (int k = 0; k < path.size(); k++) {
						for (int l = 0; l < compared.size(); l++) {
							if (path.get(k).equals(compared.get(l))) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	public double computeTransferFunction() {
		double transferFunction = 0.0;
		for (int i = 0; i < forwardPaths.size(); i++) {
			transferFunction += (deltas[i]*forwardPaths.get(i).getR()) / delta;
		}
		return transferFunction;
	}
}

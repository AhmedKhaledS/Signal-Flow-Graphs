package sfg;

import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphTraversal {
	private SingleGraph graph;
	private List<String> loop;
	private boolean[] visited;
	private List<Pair<Path, Double>> loops;
	private List<Pair<Path, Double>> forwardPaths;
	private List<String> path;
	
	private static final int MAX_NODES = 100;

	public List<Pair<Path, Double>> getLoops() {
		return loops;
	}
	public List<Pair<Path, Double>> getForwardPaths() {
		return forwardPaths;
	}


	
	
	public GraphTraversal(SingleGraph graph) {
		this.graph = graph;
		loop = new ArrayList<>();
		loops = new ArrayList<>();
		forwardPaths = new ArrayList<>();
		path = new ArrayList<>();
		visited = new boolean[MAX_NODES];
	}
	public void loops_dfs(int node, int parent, double cost, int src, int sz) {
		loop.add(graph.getNode(node).toString());
		if (visited[node]) {
			// loop.add(graph.getNode(node).toString());
			if (node == src) {
				loops.add(new Pair<Path, Double>(new Path(loop), cost));
			}
			loop.remove(loop.size() - 1);
			return;
		}
		visited[node] = true;
		int adjNodeIndex = 0;
		for (int i = 0; i < GraphAttributes.size(node); i++) {
			int child = (int) GraphAttributes.getChild(node, i).getR();
			loops_dfs(child, node, cost*(double)GraphAttributes.getChild(node, adjNodeIndex)
					.getL(), src, sz + 1);	
			adjNodeIndex++;
		}
		visited[node] = false;
		loop.remove(loop.size() - 1);
	}
	
	public void paths_dfs(int node, int parent, double cost, int dest) {
		if (visited[node]) {
			return;
		}
		if (node == dest) {
			path.add(GraphAttributes.getStingValue(dest));
			forwardPaths.add(new Pair<Path, Double>(new Path(path), cost));
			path.remove(path.size() - 1);
		}
		visited[node] = true;
		path.add(graph.getNode(node).toString());
		int adjNodeIndex = 0;
		for (int i = 0; i < GraphAttributes.size(node); i++) {
			int child = (int) GraphAttributes.getChild(node, i).getR();
			paths_dfs(child, node, cost*(double)GraphAttributes.getChild(node, adjNodeIndex)
					.getL(), dest);	
			adjNodeIndex++;
		}
		visited[node] = false;
		path.remove(path.size() - 1);
	}
	
	public void removeDuplicates() {
		List<String> reference;
		Map<Integer, Boolean> invalidLoopsIndices = new HashMap<>();
		for (int i = 0; i < loops.size(); i++) {
			reference = new ArrayList<>();
			for (int j = 0; j < loops.get(i).getL().size() - 1; j++) {
				reference.add(loops.get(i).getL().getPath().get(j));
			}
			List<String> compared;
			for (int j = i + 1; j < loops.size(); j++) {
				compared = new ArrayList<>();
				boolean identical = true;
				for (int k = 0; k < loops.get(j).getL().size() - 1; k++) {
					compared.add(loops.get(j).getL().getPath().get(k));
				}
				
				Collections.sort(reference);
				Collections.sort(compared);
				if (reference.size() == compared.size()) {
					for (int k = 0; k < reference.size() && identical; k++) {
						if (!reference.get(k).equals(compared.get(k))) {
							identical = false;
						}
					}
				} else {
					identical = false;
				}
				if (identical) {
					invalidLoopsIndices.put(j, true);
				}
			}
		}
		List<Pair<Path, Double>> tmp = new ArrayList<>();
		for (int i = 0; i < loops.size(); i++) {
			if (invalidLoopsIndices.get(i) == null) {
				tmp.add(new Pair<Path, Double>(new Path(loops.get(i).getL().getPath()), loops.get(i).getR()));
			}
		}
		loops = tmp;
	}
	
	public void clear() {
		for (int i = 0; i < 100; i++) {
			visited[i] = false;
		}
	}
}

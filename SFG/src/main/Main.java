package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.implementations.SingleGraph;
import org.omg.CORBA.DynAnyPackage.Invalid;

public class Main {
	private static int size = 0;
	private static List<Pair<Path, Double>> forwardPaths;
	private static List<Pair<Path, Double>> loops;
	private static List<String> path;
	private static Map<String, Integer> nodes;
	private static Map<Integer, String> nodeName;
	private static double delta = 1.0;
	
	// To detect whether there is more combination of non-touching loops or not.
	private static boolean changed;
	private static boolean marker[];
	
	
	private static List<main.Edge> edgePath;
	
	
	private static List<String> loop;
	private static List<main.List<Double, Integer>> adjList;
	private static boolean[] visited;
	private static SingleGraph graph;
	
	private static int noOfNodes;
	private static int noOfEdges;
	
	
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		declare();
		try {
			System.out.println("Enter number of nodes: ");
			noOfNodes = input.nextInt();
			for (int i = 0; i < noOfNodes; i++) {
				graph.addNode("Node" + i);
			}
			fillMaps(noOfNodes);
			System.out.println("Enter number of edges: ");
			noOfEdges = input.nextInt();
			System.out.println("Enter edges in the form \"source destination weight\":");
			for (int i = 0; i < noOfEdges; i++) {
				double tmp;
				String src, dest;
				src = input.next();
				dest = input.next();
				tmp = input.nextDouble();
				graph.addEdge("edge" + i, nodes.get(src), nodes.get(dest), true);
				adjList.get(nodes.get(src)).add(new Pair<>(new Double(tmp), new Integer(nodes.get(dest))));
			}
		} catch (EdgeRejectedException e) {
			e.printStackTrace();
		}
		System.out.println("Enter the source node-name:");
		String src = input.next();
		int source = nodes.get(src);
		System.out.println("Enter the destination node-name:");
		String dest = input.next();
		int destination = nodes.get(dest);
		// Extracting all loops.
		for (int i = 0; i < noOfNodes; i++) {
			loops_dfs(i, -1, 1, i, 1);
			clear();
		}
		removeDuplicates();
		// Extracting all forward paths.
		paths_dfs(source, -1, 1, destination);
		graph.display();
		System.out.println();
		System.out.println("Forward paths:");
		for (Pair<Path, Double> forPath : forwardPaths) {
			for (String name : forPath.getL().getPath()) {
				System.out.print(name + " ");
			}
			System.out.println();
		}
		System.out.println("Loops:");
		for (Pair<Path, Double> currLoop : loops) {
			for (String name : currLoop.getL().getPath()) {
				System.out.print(name + " ");
			}
			System.out.println(currLoop.getR());
		}
		computeDelta();
		
	}
	
	
	static void clear() {
		for (int i = 0; i < 100; i++) {
			visited[i] = false;
		}
	}
	static void fillMaps(int sz) {
		for (int i = 0; i < sz; i++) {
			nodes.put("Node" + i, i);
			nodeName.put(i, "Node" + i);
		}
	}
	static void declare() {
		adjList = new ArrayList<>(1000);
		edgePath = new ArrayList<>();
		forwardPaths = new ArrayList<>(1000);
		loops = new ArrayList<>(1000);
		path = new ArrayList<>();
		loop = new ArrayList<>();
		visited = new boolean[100];
		nodes = new HashMap<>();
		nodeName = new HashMap<>();
		marker = new boolean[100];
		for (int i = 0; i < 1000; i++) {
			adjList.add(new main.List<>());
		}
		graph = new SingleGraph("Graph");
	}
	
	static void loops_dfs(int node, int parent, double cost, int src, int sz) {
		if (parent != -1) {
			edgePath.add(new main.Edge(graph.getNode(parent).toString(),
					graph.getNode(node).toString(), cost));
		}
		loop.add(graph.getNode(node).toString());
		if (visited[node]) {
			// loop.add(graph.getNode(node).toString());
			if (node == src) {
				loops.add(new Pair<Path, Double>(new Path(loop), cost));
			}
			loop.remove(loop.size() - 1);
			return;
		}
		//if (sz > noOfNodes) return;
		visited[node] = true;
		int adjNodeIndex = 0;
		for (int i = 0; i < adjList.get(node).size(); i++) {
			int child = (int) adjList.get(node).get(i).getR();
			loops_dfs(child, node, cost*(double)adjList.get(node)
					.get(adjNodeIndex).getL(), src, sz + 1);	
			adjNodeIndex++;
		}
		visited[node] = false;
		loop.remove(loop.size() - 1);
	}
	static void removeDuplicates() {
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
					System.out.println(j);
					invalidLoopsIndices.put(j, true);
				}
			}
		}
		List<Pair<Path, Double>> tmp = new ArrayList<>();
		for (int i = 0; i < loops.size(); i++) {
			if (invalidLoopsIndices.get(i) == null) {
				tmp.add(new Pair(new Path(loops.get(i).getL().getPath()), loops.get(i).getR()));
			}
		}
		loops = tmp;
	}
	
	static void paths_dfs(int node, int parent, double cost, int dest) {
		if (parent != -1) {
			edgePath.add(new main.Edge(graph.getNode(parent).toString(), graph.getNode(node).toString(), cost));
		}
		if (visited[node]) {
			return;
		}
		if (node == dest) {
			path.add(nodeName.get(dest));
			forwardPaths.add(new Pair<Path, Double>(new Path(path), cost));
			path.remove(path.size() - 1);
		}
		visited[node] = true;
		path.add(graph.getNode(node).toString());
		int adjNodeIndex = 0;
		for (int i = 0; i < adjList.get(node).size(); i++) {
			int child = (int) adjList.get(node).get(i).getR();
			paths_dfs(child, node, cost*(double)adjList.get(node)
					.get(adjNodeIndex).getL(), dest);	
			adjNodeIndex++;
		}
		visited[node] = false;
		path.remove(path.size() - 1);
	} 
	static void computeDelta() {
		//double delta = 1.0;
		int sign = -1;
		for (Pair<Path, Double> indivLoops : loops) {
			delta += sign*indivLoops.getR();
		}
		changed = true;
		for (int sz = 2; sz < 100 && changed; sz++) {
			changed = false;
			for (int i = 0; i < 100; i++) {
				marker[i] = false;
			}
			getNonTouchingLoopsCombGain(0, 0, sz);
		}
		System.out.println(delta);
	}
	private static void getNonTouchingLoopsCombGain(int index, int taken, int sz) {
		if (taken == sz) {
			if (checkForValidCombination()) {
				double temp = 1.0;
				for (int i = 0; i < loops.size(); i++) {
					if (marker[i]) {
						changed = true;
						temp *= loops.get(i).getR();
					}
				}
				if (sz % 2 == 0) {
					delta += temp;
				} else {
					delta -= temp;
				}
			}
			return;
		}
		if (index >= loops.size()) return;
		marker[index] = true;
		getNonTouchingLoopsCombGain(index + 1, taken + 1, sz);
		marker[index] = false;
		getNonTouchingLoopsCombGain(index + 1, taken, sz);
	}
	
	private static boolean checkForValidCombination() {
		for (int i = 0; i < loops.size(); i++) {
			if (marker[i]) {
				List<String> reference = new ArrayList<>();
				for (String node : loops.get(i).getL().getPath()) {
					reference.add(node);
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
				}
			}
		}
		return true;
	}
}

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
	private static List<Path> forwardPaths;
	private static List<Path> loops;
	private static List<String> path;
	private static Map<String, Integer> nodes;
	private static Map<Integer, String> nodeName;
	
	
	private static List<main.Edge> edgePath;
	
	
	private static List<String> loop;
	private static List<main.List<Double, Integer>> adjList;
	private static boolean[] visited;
	private static SingleGraph graph;
	
	private static Integer scaler = 0;
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
//			for (int i = 0; i < 3; i++) {
//				graph.addEdge("Node" + i + (i + 1), i, i + 1, true);
//				adjList.get(i).add(new Pair<>(new Double(5.0), new Integer(i + 1)));
//			}
//			graph.addEdge("Node30", 3, 0, true);
//			adjList.get(3).add(new Pair<>(new Double(5.0), new Integer(0)));
//			//graph.addEdge("Node10", 1, 0, true);
//			graph.addEdge("Node31", 3, 1, true);
//			adjList.get(3).add(new Pair<>(new Double(5.0), new Integer(1)));
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
			loops_dfs(i, -1, -1, i);
			clear();
		}
		removeDuplicates();
		// Extracting all forward paths.
		paths_dfs(source, -1, -1, destination);
		graph.display();
		System.out.println();
		System.out.println("Forward paths:");
		for (Path forPath : forwardPaths) {
			for (String name : forPath.getPath()) {
				System.out.print(name + " ");
			}
			System.out.println();
		}
		System.out.println("Loops:");
		for (Path currLoop : loops) {
			for (String name : currLoop.getPath()) {
				System.out.print(name + " ");
			}
			System.out.println();
		}
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
		forwardPaths = new ArrayList<Path>(1000);
		loops = new ArrayList<Path>(1000);
		path = new ArrayList<>();
		loop = new ArrayList<>();
		visited = new boolean[100];
		nodes = new HashMap<>();
		nodeName = new HashMap<>();
		for (int i = 0; i < 1000; i++) {
			adjList.add(new main.List<>());
		}
		graph = new SingleGraph("Graph");
	}
	
	static void loops_dfs(int node, int parent, double cost, int src) {
		if (parent != -1) {
			String t1 = graph.getNode(parent).toString();
			String t2 = graph.getNode(node).toString();
			edgePath.add(new main.Edge(graph.getNode(parent).toString(), graph.getNode(node).toString(), cost));
		}
		loop.add(graph.getNode(node).toString());
		if (visited[node]) {
			// loop.add(graph.getNode(node).toString());
			if (node == src) {
				loops.add(new Path(loop));
			}
			loop.remove(loop.size() - 1);
			return;
		}
		visited[node] = true;
		int adjNodeIndex = 0;
		for (Edge adjacentEdge : graph.getNode(node).getEachLeavingEdge()) {
			int adjacentIndex = adjacentEdge.getTargetNode().getIndex();
			loops_dfs(adjacentIndex, adjacentEdge.getSourceNode().getIndex(),
					(double)adjList.get(node).get(adjNodeIndex).getL(), src);	
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
			for (int j = 0; j < loops.get(i).size() - 1; j++) {
				reference.add(loops.get(i).getPath().get(j));
			}
			List<String> compared;
			for (int j = i + 1; j < loops.size(); j++) {
				compared = new ArrayList<>();
				boolean identical = true;
				for (int k = 0; k < loops.get(j).size() - 1; k++) {
					compared.add(loops.get(j).getPath().get(k));
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
		List<Path> tmp = new ArrayList<>();
		for (int i = 0; i < loops.size(); i++) {
			if (invalidLoopsIndices.get(i) == null) {
				tmp.add(new Path(loops.get(i).getPath()));
			}
		}
		loops = tmp;
	}
	
	static void paths_dfs(int node, int parent, double cost, int destination) {
		if (parent != -1) {
			String t1 = graph.getNode(parent).toString();
			String t2 = graph.getNode(node).toString();
			edgePath.add(new main.Edge(graph.getNode(parent).toString(), graph.getNode(node).toString(), cost));
		}
		if (visited[node]) {
			return;
		}
		if (node == destination) {
			path.add(nodeName.get(destination));
			forwardPaths.add(new Path(path));
			path.remove(path.size() - 1);
		}
		visited[node] = true;
		path.add(graph.getNode(node).toString());
		int adjNodeIndex = 0;
		for (Edge adjacentEdge : graph.getNode(node).getEachLeavingEdge()) {
			int adjacentIndex = adjacentEdge.getTargetNode().getIndex();
			paths_dfs(adjacentIndex, adjacentEdge.getSourceNode().getIndex(),
					(double)adjList.get(node).get(adjNodeIndex).getL(), destination);	
			adjNodeIndex++;
		}
		visited[node] = false;
		path.remove(path.size() - 1);
	} 
	static double computeDelta() {
		double delta = 1.0;
		//for ()
		return 1.0;
	}
}

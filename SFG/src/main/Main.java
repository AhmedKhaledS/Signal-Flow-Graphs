package main;

import java.util.ArrayList;
import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.implementations.SingleGraph;

public class Main {

	private static int destination;
	private static ArrayList<String> path;
	private static ArrayList<String> loops;
	private static boolean[] visited;
	private static SingleGraph graph;
	public static void main(String[] args) {
		graph = new SingleGraph("Graph");
		for (int i = 0; i < 4; i++) {
			graph.addNode("Node" + i);
		}
		try {
			for (int i = 0; i < 3; i++) {
				graph.addEdge("Node" + i + (i + 1), i, i + 1, true);
			}
			graph.addEdge("Node30", 3, 0, true);
			//graph.addEdge("Node10", 1, 0, true);
			graph.addEdge("Node31", 3, 1, true);
		} catch (EdgeRejectedException e) {
			e.printStackTrace();
		}
		path = new ArrayList<>();
		loops = new ArrayList<>();
		visited = new boolean[10];
		destination = 3;
		dfs(0);
		graph.display();
	}

	static void dfs(int node) {
		if (node == destination) {
			printPath();
		}
		if (visited[node]) {
			loops.add(graph.getNode(node).toString());
			printLoop();
			loops.remove(loops.size() - 1);
			return;
		}
		visited[node] = true;
		path.add(graph.getNode(node).toString());
		loops.add(graph.getNode(node).toString());
		for (Edge adjacentEdge : graph.getNode(node).getEachLeavingEdge()) {
			int adjacentIndex = adjacentEdge.getTargetNode().getIndex();
			dfs(adjacentIndex);	
		}
		path.remove(path.size() - 1);
		loops.remove(loops.size() - 1);
	}
	static void printPath() {
		System.out.print("Path ");
		for (String node : path) {
			System.out.print(node + " ");
		}
		System.out.println();
	}
	static void printLoop() {
		System.out.print("Loop ");
		for (String node : loops) {
			System.out.print(node + " ");
		}
		System.out.println();
	}
}

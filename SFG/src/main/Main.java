package main;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.implementations.SingleGraph;

public class Main {
	private static int size = 0;
	private static int destination;
	private static List<Path> forwardPaths;
	private static List<Path> loops;
	private static List<String> path;
	private static List<String> loop;
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
		forwardPaths = new ArrayList<Path>(1000);
		loops = new ArrayList<Path>(1000);
		path = new ArrayList<>();
		loop = new ArrayList<>();
		visited = new boolean[10];
		destination = 3;
		dfs(0);
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

	static void dfs(int node) {
		if (node == destination) {
			forwardPaths.add(new Path(path));
			printPath();
		}
		if (visited[node]) {
			loop.add(graph.getNode(node).toString());
			loops.add(new Path(loop));
			printLoop();
			loop.remove(loop.size() - 1);
			return;
		}
		visited[node] = true;
		path.add(graph.getNode(node).toString());
		loop.add(graph.getNode(node).toString());
		for (Edge adjacentEdge : graph.getNode(node).getEachLeavingEdge()) {
			int adjacentIndex = adjacentEdge.getTargetNode().getIndex();
			dfs(adjacentIndex);	
		}
		path.remove(path.size() - 1);
		loop.remove(loop.size() - 1);
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
		for (String node : loop) {
			System.out.print(node + " ");
		}
		System.out.println();
	}
	static double computeDelta() {
		double delta = 1.0;
		for ()
		return 1.0;
	}
}

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
	
	
	private static List<main.Edge> edgePath;
	
	
	private static List<String> loop;
	private static List<main.List<Double, Integer>> adjList;
	private static boolean[] visited;
	private static SingleGraph graph;
	
	public static void main(String[] args) {
		adjList = new ArrayList<>(1000);
		edgePath = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			adjList.add(new main.List<>());
		}
		graph = new SingleGraph("Graph");
		for (int i = 0; i < 4; i++) {
			graph.addNode("Node" + i);
		}
		try {
			for (int i = 0; i < 3; i++) {
				graph.addEdge("Node" + i + (i + 1), i, i + 1, true);
				adjList.get(i).add(new Pair<>(new Double(5.0), new Integer(i + 1)));
			}
			graph.addEdge("Node30", 3, 0, true);
			adjList.get(3).add(new Pair<>(new Double(5.0), new Integer(0)));
			//graph.addEdge("Node10", 1, 0, true);
			graph.addEdge("Node31", 3, 1, true);
			adjList.get(3).add(new Pair<>(new Double(5.0), new Integer(1)));
		} catch (EdgeRejectedException e) {
			e.printStackTrace();
		}
		forwardPaths = new ArrayList<Path>(1000);
		loops = new ArrayList<Path>(1000);
		path = new ArrayList<>();
		loop = new ArrayList<>();
		visited = new boolean[10];
		destination = 3;
		dfs(0, -1, -1);
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

	static void dfs(int node, int parent, double cost) {
		if (parent != -1) {
			String t1 = graph.getNode(parent).toString();
			String t2 = graph.getNode(node).toString();
			edgePath.add(new main.Edge(graph.getNode(parent).toString(), graph.getNode(node).toString(), cost));
		}
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
		int adjNodeIndex = 0;
		for (Edge adjacentEdge : graph.getNode(node).getEachLeavingEdge()) {
			int adjacentIndex = adjacentEdge.getTargetNode().getIndex();
			dfs(adjacentIndex, adjacentEdge.getSourceNode().getIndex(),
					(double)adjList.get(node).get(adjNodeIndex).getL());	
			adjNodeIndex++;
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
		//for ()
		return 1.0;
	}
}

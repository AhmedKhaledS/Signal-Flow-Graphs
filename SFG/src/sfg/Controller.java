package sfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Controller  {
	private static List<Pair<Path, Double>> loops;
	private static List<String> path;
	private static Map<String, Integer> nodes;
	private static Map<Integer, String> nodeName;
	private static double delta = 1.0;
	private static double[] deltas;
	private static double transferFunction = 0.0;
	
	// To detect whether there is more combination of non-touching loops or not.
	private static boolean changed;
	private static boolean marker[];
	
	
	private static List<sfg.Edge> edgePath;
	
	
	private static List<String> loop;
	private static List<sfg.List<Double, Integer>> adjList;
	private static boolean[] visited;
	private static SingleGraph graph;
	
	private static int noOfNodes;
	private static int noOfEdges;
	private static Scanner input;
	
	
	public static void main(String[] args) {
		graph = new SingleGraph("Graph");
		input = new Scanner(System.in);
		GraphAttributes.initialize();
		GraphTraversal graphTr = new GraphTraversal(graph);
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
				int src, dest;
				src = input.nextInt();
				dest = input.nextInt();
				tmp = input.nextDouble();
				graph.addEdge("edge" + i, src, dest, true);
				GraphAttributes.addChild(src, dest, tmp);
			}
		} catch (EdgeRejectedException e) {
			e.printStackTrace();
		}
		System.out.println("Enter the source node-name:");
		String src = input.next();
		int source = GraphAttributes.getIntegerValue(src);
		System.out.println("Enter the destination node-name:");
		String dest = input.next();
		int destination = GraphAttributes.getIntegerValue(dest);
		// Extracting all loops.
		for (int i = 0; i < noOfNodes; i++) {
			graphTr.loops_dfs(i, -1, 1, i, 1);
			graphTr.clear();
		}
		graphTr.removeDuplicates();
		// Extracting all forward paths.
		graphTr.paths_dfs(source, -1, 1, destination);
		graph.display();
		System.out.println();
		System.out.println("Forward paths:");
		for (Pair<Path, Double> forPath : graphTr.getForwardPaths()) {
			for (String name : forPath.getL().getPath()) {
				System.out.print(name + " ");
			}
			System.out.println("\t" + "Gain: " + forPath.getR());
		}
		System.out.println("******************************************");
		System.out.println("Loops:");
		for (Pair<Path, Double> currLoop : graphTr.getLoops()) {
			for (String name : currLoop.getL().getPath()) {
				System.out.print(name + " ");
			}
			System.out.println("Gain: " + currLoop.getR());
		}
		SFGCalculator calculator = new SFGCalculator(graphTr);
		System.out.println("******************************************");
		calculator.computeDeltas();
		System.out.println("Delta: " + delta);
		System.out.println("******************************************");
		transferFunction = calculator.computeTransferFunction();
		System.out.println("Overall Transfer function: " + transferFunction);
	}
	
	static void fillMaps(int sz) {
		for (int i = 0; i < sz; i++) {
			GraphAttributes.addNode("Node" + i, i);
		}
	}
}

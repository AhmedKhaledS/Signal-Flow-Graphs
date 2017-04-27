package gui;

import java.util.ArrayList;


import org.graphstream.graph.Edge;
import org.graphstream.graph.EdgeRejectedException;
import org.graphstream.graph.Element;
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

public class Main extends Application{

	private static int destination;
	private static ArrayList<String> path;
	private static ArrayList<String> loops;
	private static boolean[] visited;
	private static SingleGraph graph;
	
	public static void main(String[] args) {
		System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new SingleGraph("Graph");
		for (int i = 0; i < 5; i++) {
			Element cur = graph.addNode("Node" + i);
			cur.addAttribute("ui.label", "Node" + i);
		}
		try {
			for (int i = 0; i < 4; i++) {
				Element cur = graph.addEdge("Node" + i + (i + 1), i, i + 1, true);
				cur.addAttribute("ui.label", cur.getId());
			}
			graph.addEdge("Node30", 4, 2, true);
			//graph.addEdge("Node10", 1, 0, true);
			graph.addEdge("Node31", 0, 0, true);
		} catch (EdgeRejectedException e) {
			e.printStackTrace();
		}
		path = new ArrayList<>();
		loops = new ArrayList<>();
		visited = new boolean[10];
		destination = 4;
		dfs(0);
		System.out.println(" --- ");
		for (int i = 0; i < 5; i++) {
			System.out.println("From Node " + i);
			loops.clear();
			visited = new boolean[10];
			destination = i;
			dfs(0);
		}
		//graph.addAttribute("ui.stylesheet", "graph { fill-color: blue; }");
		//graph.display();
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SFG Solver");
        
        StackPane root = new StackPane();
        prepareButtons(root);
        primaryStage.setScene(new Scene(root, 1000, 250));
        primaryStage.show();
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
//		System.out.print("Path ");
//		for (String node : path) {
//			System.out.print(node + " ");
//		}
//		System.out.println();
	}

	static void printLoop() {
//		System.out.print("Loop ");
//		for (String node : loops) {
//			System.out.print(node + " ");
//		}
//		System.out.println();
	}
	
	public void prepareButtons(StackPane root) {
   	 VBox interfaceBox = new VBox(10);
   	 interfaceBox.setPrefWidth(90);
   	 addNodeButtons(interfaceBox);
   	 addEdgeButtons(interfaceBox);
   	 initCanvas(interfaceBox);
   	 addSolveButton(interfaceBox);
   	 root.getChildren().add(interfaceBox);
   }

	private void addSolveButton(VBox interfaceBox) {
		Button solve = new Button();
	    solve.setText("Solve");
	    solve.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				//TODO :  Call Solver
			}
	    });
	    interfaceBox.getChildren().add(solve);
	}
	
	private void initCanvas(VBox interfaceBox) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        ViewPanel viewPanel = viewer.addDefaultView(false);
        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(CloseFramePolicy.EXIT);
        viewPanel.setSize(500, 300);
        SwingNode node = new SwingNode();
        node.setLayoutX(0);
        node.setLayoutY(40);
        node.setContent(viewPanel);
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(node);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        pane.setPrefHeight(600);
        interfaceBox.getChildren().add(pane);
        return ;
    }

	private void addNodeButtons(VBox interfaceBox) {
		HBox addNodeBox = new HBox();
	   	TextField nodeName = new TextField();
	   	nodeName.setText("Node Name");
	   	Button addNode = new Button();
	    addNode.setText("Add Node");
	    addNode.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				String nodeNameString = nodeName.getText();
				// call addNode Method
				graph.addNode(nodeNameString);
			}
	    });
	    addNodeBox.getChildren().addAll(addNode, nodeName);
	    interfaceBox.getChildren().add(addNodeBox);
	}

	private void addEdgeButtons(VBox interfaceBox) {
		HBox addEdgeBox = new HBox();
	    TextField edgeSource = new TextField();
	    edgeSource.setText("Source Node");
	    TextField edgeDestination = new TextField();
	    edgeDestination.setText("Destination Node");
	    TextField edgeWeight = new TextField();
	    edgeWeight.setText("Edge Weight");
	    Button addEdge = new Button();
	    addEdge.setText("Add Edge");
	    addEdge.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				// TODO Auto-generated method stub
				String sourceNodeName = edgeSource.getText();
				String targetNodeName = edgeDestination.getText();
				String edgeWeightString = edgeWeight.getText();
				// call addEdge Method
				Platform.runLater(() -> graph.addEdge(edgeWeightString, sourceNodeName, targetNodeName));;
			}

		});
	    addEdgeBox.getChildren().addAll(addEdge, edgeSource, edgeDestination, edgeWeight);
	    interfaceBox.getChildren().add(addEdgeBox);
	}
}

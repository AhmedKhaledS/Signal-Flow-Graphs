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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import sfg.GraphAttributes;
import sfg.GraphTraversal;
import sfg.SFGCalculator;

public class Main extends Application {

	private static SingleGraph graph;
	private GraphTraversal graphTr;
	private int noOfNodes = 0;
	private int noOfEdges = 0;
	
	public static void main(String[] args) {
		//System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		graph = new SingleGraph("Graph");
		GraphAttributes.initialize();
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
		HBox solveBox = new HBox(10);
		Button solve = new Button();
		Label sourceLabel = new Label("Source node");
		Label targetLabel = new Label("Target node");
		TextField sourceNode = new TextField();
		TextField targetNode = new TextField();
		TextFlow txtFlow = new TextFlow();
	    solve.setText("Solve");
	    solve.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				GraphTraversal graphTr = new GraphTraversal(graph);
				for (int i = 0; i < noOfNodes; i++) {
					graphTr.loops_dfs(i, -1, 1, i, 1);
					graphTr.clear();
				}
				graphTr.removeDuplicates();
				graphTr.paths_dfs(Integer.parseInt(sourceNode.getText()), -1, 1, Integer.parseInt(targetNode.getText()));
				graph.display();
				SFGCalculator calculator = new SFGCalculator(graphTr);
				calculator.computeDeltas();
				double transferFunction = calculator.computeTransferFunction();
				prettyPrinting(graphTr, txtFlow, transferFunction, calculator);
			}
	    });
	    solveBox.getChildren().addAll(solve, sourceLabel, sourceNode, targetLabel, targetNode);
	    interfaceBox.getChildren().add(solveBox);
	    interfaceBox.getChildren().add(txtFlow);
	}
	
	private void prettyPrinting(GraphTraversal graph, TextFlow txtFlow, double transFunction, SFGCalculator calculator) {
		Text forwardPaths = new Text("Forwad Paths\n");
		txtFlow.getChildren().add(forwardPaths);
		double[] deltas = calculator.getDeltas();
		for (int i = 0; i < graph.getForwardPaths().size(); i++) {
			Text curForwardPath;
			String forwardPath = "";
			for (String node : graph.getForwardPaths().get(i).getL().getPath()) {
				forwardPath += node + " ";
			}
			forwardPath += "\t\t Gain:" + graph.getForwardPaths().get(i).getR().toString() + "\t Delta:" + deltas[i];
			curForwardPath = new Text(forwardPath + "\n");
			txtFlow.getChildren().add(curForwardPath);
		}
		Text loops = new Text("Loops\n");
		txtFlow.getChildren().add(loops);
		for (int i = 0; i < graph.getLoops().size(); i++) {
			Text curLoop;
			String loop = "";
			for (String node : graph.getLoops().get(i).getL().getPath()) {
				loop += node + " ";
			}
			loop += "\t\t Gain:" + graph.getForwardPaths().get(i).getR().toString();
			curLoop = new Text(loop + "\n");
			txtFlow.getChildren().add(curLoop);
		}
		Text delta = new Text("Delta: " + calculator.getDelta() + "\n");
		Text tFunction = new Text("Transfer function: " + transFunction);
		txtFlow.getChildren().addAll(delta, tFunction);
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
				Element node = graph.addNode(nodeNameString);
				node.addAttribute("ui.label", "Node "+ node.getId());
				GraphAttributes.addNode(nodeNameString, noOfNodes++);
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
				Edge currEdge = graph.addEdge(sourceNodeName + "-->" + targetNodeName, sourceNodeName, targetNodeName, true);
				currEdge.addAttribute("ui.label", edgeWeightString);
				GraphAttributes.addChild(Integer.parseInt(sourceNodeName), Integer.parseInt(targetNodeName), Double.parseDouble(edgeWeightString));
				noOfEdges++;
			}

		});
	    addEdgeBox.getChildren().addAll(addEdge, edgeSource, edgeDestination, edgeWeight);
	    interfaceBox.getChildren().add(addEdgeBox);
	}
}

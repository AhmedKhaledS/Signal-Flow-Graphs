package sfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphAttributes {
	private static Map<String, Integer> nodes;
	private static Map<Integer, String> nodeName;
	private static List<sfg.List<Double, Integer>> adjacencyList;

	///////////////////////////////////////////////////////////////////////////////////////////////////
	public static void initialize() {
		nodeName = new HashMap<>();
		nodes = new HashMap<>();
		adjacencyList = new ArrayList<>(1000);
		for (int i = 0; i < 1000; i++) {
			adjacencyList.add(new sfg.List<>());
		}
	}
	public static Integer getIntegerValue(String key) {
		return nodes.get(key);
	}
	public static String getStingValue(int key) {
		return nodeName.get(key);
	}
	public static void addNode(String key, int value) {
		nodes.put(key, value);
		nodeName.put(value, key);
	}

	public static void addChild(int parent, int child, double weight) {
		adjacencyList.get(parent).add(new Pair<Double, Integer>(weight, child));
	}
	public static Pair<Double, Integer> getChild(int parent, int index) {
		return adjacencyList.get(parent).get(index);
	}
	public static int size(int parent) {
		return adjacencyList.get(parent).size();
	}

	
}

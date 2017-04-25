package main;

public class Edge {
	private String source;
	private String destination;
	private Double weight;
	
	Edge(String source, String destination, Double weight) {
		this.source = source;
		this.destination= destination;
		this.weight = weight;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public Double getWeight() {
		return weight;
	}
}

package main;

public class Node implements INode {
	
	private String name;

	@Override
	public void setName(String name) {
		this.name = name;
		
	}

	@Override
	public String getName() {
		return name;
	}

}

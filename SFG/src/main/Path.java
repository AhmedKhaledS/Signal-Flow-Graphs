package main;

import java.util.ArrayList;
import java.util.List;

public class Path {
	private List<String> path;
	
	Path() {
		this.path = new ArrayList<>();
	}
	
	Path(List<String> path) {
		this.path = new ArrayList<>();
		for (String name : path) {
			path.add(name);
		}
	}
	
	public List<String> getPath() {
		return this.path;
	}
}

package main;

import java.util.ArrayList;
import main.Pair;

public class List<L, R> {
	private ArrayList<Pair<L, R>> list;
	List() {
		list = new ArrayList<>();
	}
	
	public void add(Pair pair) {
		list.add(new Pair(pair.getL(), pair.getR()));
	}
	
	public Pair get(int index) {
		return list.get(index);
	}
	
	public int size() {
		return list.size();
	}
}

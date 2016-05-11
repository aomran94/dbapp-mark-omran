package AnaMshKarimAnaKaram;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class Node implements Comparable, Serializable {
	
	private static final long serialVersionUID = 4364945622421539325L;
	ArrayList<Item> items;
	Node parent;
	String type;
	static final int N = BPlusTree.N;
	
	
	public Node(String type, Node parent) {
		this.type = type;
		this.parent = parent;
		items = new ArrayList<Item>();
		
	}
	
	public  ArrayList<Item> getItems() {
		return items;
	}


	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int compareTo(Object o) {
		if(((Node)o).getItems().get(0).key.getClass().getSimpleName().equals("Integer"))
		return  ((Integer)(this.getItems().get(0).key))-((Integer)((Node)o).getItems().get(0).key);
		
		if(((Node)o).getItems().get(0).key.getClass().getSimpleName().equals("Double"))
			return  ((Double)(this.getItems().get(0).key)).compareTo(((Double)((Node)o).getItems().get(0).key));
		
		return  ((String)(this.getItems().get(0).key)).compareTo(((String)((Node)o).getItems().get(0).key));
	}
	public String toString(){
		return this.getItems()+"";
	}

}

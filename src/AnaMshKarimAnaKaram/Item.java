package AnaMshKarimAnaKaram;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class Item implements Comparable, Serializable {
	
	private static final long serialVersionUID = -5043816589871286858L;
	Object key;
	Node left;
	Node right;
	
	public Item(Object key,Node left,Node right){
		this.key=key;
		this.left=left;
		this.right=right;
	}

	public int compareTo(Object o) {
		if(((Item)(o)).key.getClass().getSimpleName().equals("Integer"))
		return ((Integer)this.key - ((Integer)((Item)o).key));
		
		if(((Item)(o)).key.getClass().getSimpleName().equals("Double"))
		return ((Double)this.key).compareTo((Double)((Item)o).key);
		
		return ((String)this.key).compareTo((String)((Item)o).key);
	}
  public String toString(){
	  return key.toString()+"";
  }
 
}


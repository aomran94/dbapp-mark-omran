package AnaMshKarimAnaKaram;

import java.io.Serializable;

@SuppressWarnings("rawtypes")
public class Pair implements Comparable, Serializable {
	
	private static final long serialVersionUID = -547737534493471817L;
	Integer page;
	Integer row;
	
	public Pair(Integer page, Integer row) {
		this.page = page;
		this.row = row;
	}
	
	public Integer getPage() {
		return page;
	}
	
	public Integer getRow() {
		return row;
	}
	
	@Override
	public boolean equals(Object obj) {
		Pair t = (Pair) obj;
		return (t.getPage().equals(page) && t.getRow().equals(row));
	}

	@Override
	public String toString() {
		return "(( " + page.toString() + ", " + row.toString() + " ))";
	}

	@Override
	public int compareTo(Object o) {
		Pair t = (Pair) o;
		return this.page.intValue() - t.getPage().intValue();
	}
}

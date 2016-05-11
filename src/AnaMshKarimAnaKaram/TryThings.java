package AnaMshKarimAnaKaram;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings("unused")
public class TryThings {
	/*
	public static void main(String[] args) throws FileNotFoundException,
			IOException, ClassNotFoundException, DBEngineException {
		/*
		// Testing properties Reading
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("config/DBApp.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String value = prop.getProperty("MaximumRowsCountinPage");
		int MaxNo = Integer.parseInt(value);
		System.out.println(MaxNo + "blabla");

		// Testing Splitting with "." delimiter
		String t = "Faculty.ID";
		String[] tt = t.split("\\.");
		System.out.println(tt[0] + "  " + tt[1]);

		// Viewing tabledata.class File
		ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(
				new File("data/tabledata.class")));
		@SuppressWarnings("unchecked")
		Hashtable<String, ArrayList<Object>> ht = (Hashtable<String, ArrayList<Object>>) ois2
				.readObject();
		ois2.close();

		Set<String> keys = ht.keySet();

		for (String key : keys) {
			System.out.println(key + "  -->  " + ht.get(key));
		}
		
		
		Hashtable<String, Integer> ht1 = new Hashtable<String, Integer>();
		ht1.put("MarkNader", new Integer("-1"));
		ht1.put("AhmedOmran", new Integer("0"));
		Hashtable<String, Integer> ht2 = new Hashtable<String, Integer>();
		ht2.put("MarkNader", new Integer("-1"));
		ht2.put("AhmedOmran", new Integer("0"));
		
		System.out.println(ht1.equals(ht2));
		*/
		
		
		/*
		//Printing Table Data.
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File("data/tabledata.class")));
		Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois.readObject();
		ois.close();
		
		Set<String> keys = tabledata.keySet();
		for(String key:keys) {
			System.out.println(key + " ---> " + tabledata.get(key));
		}
		System.out.println("\n=================================================\n");
		*/
		
	/*	
		String x = "Integer";
		System.out.println(x.startsWith("java.lang."));
		String y = "java.lang." + x;
		System.out.println(y);
		System.out.println(y.equals("java.lang.Integer"));
	*/	
	
		/*
		//Printing a Table Page.
		ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(new File("data/Student3.class")));
		ArrayList<Hashtable<String, Object>> t = (ArrayList<Hashtable<String, Object>>) ois2.readObject();
		ois2.close();
		for (int i = 0; i < t.size(); i++) { 
			Set<String> keys2 = t.get(i).keySet(); 
			String tt = "( "; 
			for (String key : keys2) { 
				tt += t.get(i).get(key) + "  ,  "; 
			} 
			tt += "  )  "; 
			System.out.println(tt);
		}
		
		System.out.println("\n=================================================\n");
		//*/
		
		
		/*
		DBApp myDB = new DBApp();
		myDB.init();
		
		Hashtable<String, Object> delColNameValue1 = new Hashtable<String, Object>();
		delColNameValue1.put("First_Name", "FN795");
		delColNameValue1.put("Last_Name", "LN794");
		Iterator<Hashtable<String, Object>> itr = myDB.selectFromTable("Student", delColNameValue1, "OR");

		while(itr.hasNext()) {
			
			System.out.println(itr.next());
		}
		
		ArrayList<ArrayList<Node>> a = new ArrayList<ArrayList<Node>>();
		
		Node tmp = new Node("leaf",null);
		tmp.getItems().add(new Item(5,null,null));
		ArrayList <Node> tmpArray = new ArrayList<Node>();
		tmpArray.add(tmp);
		a.add(tmpArray);
		System.out.println(a.get(0));
		tmp.getItems().add(new Item(8,null,null));
		System.out.println(a.get(0));
		
		ArrayList<ArrayList<Integer>> b = new ArrayList<ArrayList<Integer>>();
		Integer tmp2= new Integer(5);
		ArrayList<Integer> tmpArray2= new ArrayList<Integer>();
		tmpArray2.add(tmp2);
		b.add(tmpArray2);
		System.out.println(b.get(0));
		tmpArray2.add(new Integer(7));
		System.out.println(b.get(0));
	}
	*/

	/*
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		ArrayList<Pair> p = new ArrayList<Pair>();
		
		Pair p1 = new Pair(1,5);
		Pair p2 = new Pair(7,2);
		Pair p3 = new Pair(1,5);
		
		p.add(p2);
		p.add(p1);
		
		Hashtable<Integer, String> ht = new Hashtable<Integer, String>();
		ht.put(2, "mark.zip");
		ht.put(5, "omran.zip");
		ht.put(7, "mo'men.zip");
		ht.put(3, "maher.zip");
		
		Set<Integer> set = ht.keySet();
		Set<Integer> set2 = ht.keySet();
		
		ArrayList<Integer> arr = new ArrayList<Integer>(ht.keySet());
		
		System.out.println(arr);
		System.out.println(set2 + "" + set2.size());
		System.out.println(ht.keySet());
		
		
		arr.remove(new Integer(5));
		
		set2.add(5050);
		
		System.out.println(arr);
		System.out.println(set2);
		System.out.println(ht.keySet());
		
		
		System.out.println(ht.get(2));
		System.out.println(ht.get(1));
		
		System.out.println(p);
		Collections.sort(p);
		System.out.println(p);
		
	}
	*/
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		// create a new DBApp
		DBApp myDB = new DBApp();
		
		// initialize it
		myDB.init();
		
		System.out.println(myDB.tableColumns("Student").contains("ID"));
		
		/*
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File("data/Student.ID.index.class")));
		BPlusTree bt1 = (BPlusTree) ois.readObject();
		ois.close();
		
		bt1.printSameLevel();
		
		System.out.println("\n\n");
		
		ObjectInputStream ois2 = new ObjectInputStream(
				new FileInputStream(new File("data/Student.First_Name.index.class")));
		BPlusTree bt2 = (BPlusTree) ois2.readObject();
		ois2.close();
		
		bt2.printSameLevel();
		*/
		
		/*
		//Printing Table Data.
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File("data/tabledata.class")));
		Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois.readObject();
		ois.close();
		
		Set<String> keys = tabledata.keySet();
		for(String key:keys) {
			System.out.println(key + " ---> " + tabledata.get(key));
		}
		System.out.println("=================================================\n\n\n");
		
		// create a new DBApp
		DBApp myDB = new DBApp();

		// initialize it
		myDB.init();
		
		System.out.println(myDB.tableColumns("Student"));
		
		
		System.out.println(bt.sameLevel.get(1));
		
		*/
		
		
	}
	
	
	
}

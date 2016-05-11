package AnaMshKarimAnaKaram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

class DBAppException extends Exception {

	private static final long serialVersionUID = 1043972173135662546L;

}

class DBEngineException extends Exception {

	private static final long serialVersionUID = -7440227403439796055L;

}

public class DBApp {

	public void init() {
		try {
			File file = new File("data/metadata.csv");
			if (!file.exists()) {
				FileWriter metaData = new FileWriter("data/metadata.csv");
				metaData.append("Table Name, Column Name, Column Type, Key, Indexed, References");
				metaData.close();
			}
			File file2 = new File("data/tabledata.class");
			if(!file2.exists()){
				Hashtable<String, ArrayList<Object>> tabledata = new Hashtable<String, ArrayList<Object>>();
				ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream(new File("data/tabledata.class")));
				oos.writeObject(tabledata);
				oos.close();
			}
			Properties prop = new Properties();
			prop.load(new FileInputStream("config/DBApp.properties"));
			String Nvalue = prop.getProperty("BPlusTreeN");
			int N = Integer.parseInt(Nvalue);
			BPlusTree.N = N;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public boolean tableExists(String strTableName) throws IOException {
		BufferedReader b = null;
		String line = "";
		try {
			b = new BufferedReader(new FileReader("data/metadata.csv"));
			while ((line = b.readLine()) != null) {
				String[] s = line.split(",");
				if (strTableName.equals(s[0])) {
					b.close();
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		b.close();
		return false;
	}

	public boolean checkDataTypes(String strTableName,
			Hashtable<String, Object> htblColNameValue) {
		try {
			BufferedReader b = new BufferedReader(
					new FileReader("data/metadata.csv"));
			String line = "";
			while ((line = b.readLine()) != null) {
				String[] temp = line.split(",");
				if (temp[0].equals(strTableName)) {
					Object t = htblColNameValue.get(temp[1]);
					if (t != null) {
						if (!temp[2].equals(t.getClass().getName())) {
							b.close();
							return false;
						}
					}

				}
			}
			b.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void createTable(String strTableName,
			Hashtable<String, String> htblColNameType,
			Hashtable<String, String> htblColNameRefs, String strKeyColName)
			throws DBAppException {

		try {
			if (!tableExists(strTableName)) {
				
				
				Set<String> colTypes = htblColNameType.keySet();
				for(String type : colTypes) {
					String typ = htblColNameType.get(type);
					String type2 = (typ.startsWith("java.lang.")?"":"java.lang.") + typ;
					
					boolean zz = (type2.equals("java.lang.Integer")
									|| type2.equals("java.lang.String")
									|| type2.equals("java.lang.Double")
									|| type2.equals("java.lang.Boolean")
									|| type2.equals("java.lang.Date"));
					
					if(!zz) return;
				}
				
				
				
				Set<String> refSet = htblColNameRefs.keySet();
				for(String col : refSet){
					String[] refTemp = htblColNameRefs.get(col).split("\\.");
					
					if(!tableExists(refTemp[0])) return;
					
					ObjectInputStream ois = new ObjectInputStream(
							new FileInputStream(new File("data/tabledata.class")));
					@SuppressWarnings("unchecked")
					Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois
							.readObject();
					ois.close();
					
					String pKeyType = (String) tabledata.get(refTemp[0]).get(2);
					String tt = htblColNameType.get(col);
					String ttt = ((tt.startsWith("java.lang."))?"":"java.lang.") + tt;
					if(!pKeyType.equals(ttt)) return;
					
				}
				
				
				
				FileWriter w = new FileWriter("data/metadata.csv", true);

				Set<String> keys = htblColNameType.keySet();
				for (String key : keys) {
					String temp = strTableName + ",";
					temp += key + ",";
					String temptemp = "";
					if (!htblColNameType.get(key).startsWith("java.lang."))
						temptemp = "java.lang.";
					temp += (temptemp + htblColNameType.get(key) + ",");
					temp += ((key == strKeyColName) ? "True" : "False") + ",";
					temp += "False,";
					temp += htblColNameRefs.get(key);

					
					
					w.append("\n" + temp);
					
					
				}
				w.close();
				ObjectInputStream ois2 = new ObjectInputStream(
						new FileInputStream(new File("data/tabledata.class")));
				@SuppressWarnings("unchecked")
				Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois2
						.readObject();
				ois2.close();
				ArrayList<Object> arr = new ArrayList<Object>();
				arr.add(new Integer(1));
				arr.add(strKeyColName);
				arr.add("java.lang." + htblColNameType.get(strKeyColName));
				tabledata.put(strTableName, arr);

				ObjectOutputStream oos2 = new ObjectOutputStream(
						new FileOutputStream(new File("data/tabledata.class")));
				oos2.writeObject(tabledata);
				oos2.close();
				
				//Create Index For Primary Key
				createIndex(strTableName, strKeyColName);

			} else {
				System.out.println("CREATE TABLE: Table Already Exists !");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public void createIndex(String strTableName, String strColName)
			throws DBAppException, IOException, ClassNotFoundException {
		File indexFile = new File("data/" + strTableName + "." + strColName + ".index.class");
		if(tableExists(strTableName) && !indexFile.exists() && tableColumns(strTableName).contains(strColName)) {
			
			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream("config/DBApp.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			String Nvalue = prop.getProperty("BPlusTreeN");
			int N = Integer.parseInt(Nvalue);
			
			BPlusTree bt = new BPlusTree(N);
			
			int pageNo = 1;
			File page = new File("data/" + strTableName + "" + pageNo + ".class");
			while(page.exists()) {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(page));
				ArrayList<Hashtable<String, Object>> tablePage = (ArrayList<Hashtable<String, Object>>) ois
						.readObject();
				ois.close();
				
				for(int i=0; i<tablePage.size(); i++) {
					if(tablePage.get(i) != null) {
						bt.insert(tablePage.get(i).get(strColName), pageNo, i);
					}
				}
				
				pageNo++;
				page = new File("data/" + strTableName + "" + pageNo + ".class");
			}
			
			//Saving The B+Tree on the Hard Disk
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(new File("data/" + strTableName 
							+ "." + strColName + ".index.class")));
			oos.writeObject(bt);
			oos.close();
			
			//Updating the metadata.csv
			ArrayList<String[]> lines = new ArrayList<String[]>();
			BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] s = line.split(",");
				if(strTableName.equals(s[0]) && strColName.equals(s[1]))
					s[4] = "True"; 
				lines.add(s);
			}
			br.close();
			FileWriter w = new FileWriter("data/metadata.csv", false);
			for(int c=0; c<lines.size(); c++) {
				String[] s = lines.get(c);
				line = "";
				for(int d=0; d<s.length; d++) line += ("," + s[d]);
				w.write(line.substring(1) + "\n");
			}
			w.close();
			
			
		}
	}

	@SuppressWarnings("unchecked")
	public void insertIntoTable(String strTableName,
			Hashtable<String, Object> htblColNameValue) throws DBAppException {
		try {
			if (tableExists(strTableName)) {
				ObjectInputStream ois = new ObjectInputStream(
						new FileInputStream(new File("data/tabledata.class")));
				Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois
						.readObject();
				ois.close();
				if (checkDataTypes(strTableName, htblColNameValue)) {
					
					String pKey = (String)(tabledata.get(strTableName).get(1));
					
					Hashtable<String, Object> htblTemp = new Hashtable<String, Object>();
					htblTemp.put(pKey, htblColNameValue.get(pKey));
					Iterator<Hashtable<String, Object>> itr;
					try {
						itr = selectFromTable(strTableName, 
								htblTemp, "OR");
					} catch (DBEngineException e) {
						e.printStackTrace();
						return;
					}
					
					if(htblColNameValue.get(pKey) == null || itr.hasNext()) {
						System.out.println("Insert Error : either Primary key is Null or repeated !");
						return;
					}
					
					// Check for foreign keys 
					ArrayList<ArrayList<String>> fKeys = hasForeignKey(strTableName);
					for ( ArrayList<String> fKey : fKeys ) {
						try {
							Hashtable<String, Object> htblQuery = new Hashtable<String, Object>();
							htblQuery.put(fKey.get(2), htblColNameValue.get(fKey.get(0)));
							itr = selectFromTable(fKey.get(1), htblQuery, "OR");
							
							if(!itr.hasNext()) {
								System.out.println("Insert Error : foreign key ( " + fKey.get(0) 
										+ " ) references a non existing value in " + fKey.get(1) + "." + fKey.get(2));
								return;
							}
						} catch (DBEngineException e) {
							e.printStackTrace();
							return;
						}
					}
					
					htblColNameValue.put("TouchDate", LocalDateTime.now());
					int nextPage = ((Integer) tabledata.get(strTableName).get(0)).intValue();
					File f = new File("data/" + strTableName + nextPage + ".class");
					ArrayList<Hashtable<String, Object>> tuple;

					if (!f.exists()) {
						
						tuple = new ArrayList<Hashtable<String, Object>>();
					} else {
						
						ObjectInputStream ois2 = new ObjectInputStream(
								new FileInputStream(new File("data/" + strTableName
										+ nextPage + ".class")));
						
						tuple = (ArrayList<Hashtable<String, Object>>) ois2
								.readObject();
						ois2.close();
					}

					tuple.add(htblColNameValue);

					ObjectOutputStream oos2 = new ObjectOutputStream(
							new FileOutputStream(new File("data/" + strTableName
									+ nextPage + ".class")));
					oos2.writeObject(tuple);
					oos2.close();
					
					Properties prop = new Properties();
					try {
						prop.load(new FileInputStream("config/DBApp.properties"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					String value = prop.getProperty("MaximumRowsCountinPage");
					int MaxNo = Integer.parseInt(value);
					
					
					if (tuple.size() == MaxNo) {
						ObjectInputStream ois3 = new ObjectInputStream(
								new FileInputStream(new File("data/tabledata.class")));
						Hashtable<String, ArrayList<Object>> td = (Hashtable<String, ArrayList<Object>>) ois3
								.readObject();
						ArrayList<Object> arr = td.get(strTableName);
						arr.set(0, ++nextPage);
						ois3.close();
						ObjectOutputStream oos3 = new ObjectOutputStream(
								new FileOutputStream(
										new File("data/tabledata.class")));
						oos3.writeObject(td);
						oos3.close();
					}
					
					//Insertion in B+Tree for indexed columns.
					Set<String> keys = htblColNameValue.keySet();
					for(String key : keys) {
						File indexFile = new File("data/" + strTableName + "." + key + ".index.class");
						if(indexFile.exists() && htblColNameValue.get(key) != null) {
							ObjectInputStream ois4 = new ObjectInputStream(new FileInputStream(indexFile));
							BPlusTree bt = (BPlusTree) ois4.readObject();
							ois4.close();
							
							bt.insert(htblColNameValue.get(key), nextPage, (tuple.size()-1));
							
							ObjectOutputStream oos4 = new ObjectOutputStream(new FileOutputStream(indexFile));
							oos4.writeObject(bt);
							oos4.close();
						}
					}

				} else {
					System.out.println("Not Valid DataTypes !!");
				}

			} else {
				System.out.println("No Table");
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void updateTable(String strTableName, Object objKey,
			Hashtable<String, Object> htblColNameValue) throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException {
		
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File("data/tabledata.class")));
		Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois.readObject();
		ois.close();
		
		String keyName = (String) tabledata.get(strTableName).get(1);
		
		
		Hashtable<String, Object> row = null;
		ArrayList<Hashtable<String, Object>> page = null;
		Pair pair = null;
		
		Hashtable<String, BPlusTree> indicesLoaded = new Hashtable<String, BPlusTree>();
		
		//Load indices in memory for the indexed columns
		Set<String> cols = htblColNameValue.keySet();
		for( String col : cols ) {
			if(hasIndex(strTableName, col)) {
				ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(
						new File("data/" + strTableName + "." + col + ".index.class")));
				BPlusTree bt = (BPlusTree) ois2.readObject();
				ois2.close();
				
				indicesLoaded.put(col, bt);
			}
		}
		
		
		//Load Primary Key Index
		ObjectInputStream ois3 = new ObjectInputStream(new FileInputStream(
				new File("data/" + strTableName + "." + keyName + ".index.class")));
		BPlusTree bt = (BPlusTree) ois3.readObject();
		ois3.close();
		
		//Search in the index
		Node leaf = bt.findLeaf(objKey);
		ArrayList<Item> leafItems = leaf.getItems();
		for(int i=0; i<leafItems.size(); i++) {
			if(leafItems.get(i).key.equals(objKey)) {
				pair = new Pair(((Integer)leaf.getItems().get(i).left.getItems().get(0).key),
						((Integer)leaf.getItems().get(i).left.getItems().get(1).key));
				break;
			}
		}
		
		//If record with specified key was not found print error message then terminate:
		if(pair == null) {
			System.out.println("Update Method Error: No such record with the specified Key !");
			return;
		}
		
		//Load page and get row
		ObjectInputStream ois2 = new ObjectInputStream(
				new FileInputStream(new File("data/" + strTableName + "" + pair.getPage() + ".class")));
		page = (ArrayList<Hashtable<String, Object>>) ois2.readObject();
		ois2.close();
		row = page.get(pair.getRow().intValue());
		
		//Saving the old row data
		Hashtable<String, Object> oldRow = new Hashtable<String, Object>();
		
		
		//Update row and saving old data
		Set<String> keys = htblColNameValue.keySet();
		for( String key : keys ) {
			oldRow.put(key, row.get(key));
			row.put(key, htblColNameValue.get(key));
		}
		row.put("TouchDate", LocalDateTime.now());
		
		//Write page again.
		ObjectOutputStream oos = new ObjectOutputStream(
				new FileOutputStream(new File("data/" + strTableName + "" + pair.getPage() + ".class")));
		oos.writeObject(page);
		oos.close();
		
		//Update Indices:
		Set<String> indices = indicesLoaded.keySet();
		for( String index : indices ) {
			if(hasIndex(strTableName, index)) {
				boolean found = false;
				ArrayList<Pair> tempPairs = new ArrayList<Pair>();
				while(!found) {
					ArrayList<Object> tmp = indicesLoaded.get(index).delete(oldRow.get(index));
					Pair p = new Pair((Integer)tmp.get(0), (Integer)tmp.get(1));
					if(p.equals(pair)) found = true;
					else tempPairs.add(p);
				}
				System.out.println(index + "  " + htblColNameValue.get(index));
				indicesLoaded.get(index).insert(htblColNameValue.get(index), pair.getPage().intValue(), pair.getRow().intValue());
				while(!tempPairs.isEmpty()) {
					Pair t = tempPairs.remove(tempPairs.size()-1);
					indicesLoaded.get(index).insert(oldRow.get(index), t.getPage().intValue(), t.getRow().intValue());
				}
			}
		}
		
		//Writing back Indices to HardDisk
		for( String index : indices ) {
			ObjectOutputStream oos2 = new ObjectOutputStream(
					new FileOutputStream(new File("data/" + strTableName + "." + index + ".index.class")));
			oos2.writeObject(indicesLoaded.get(index));
			oos2.close();
		}
		
		
	}
	
	public boolean belongsTo(Hashtable<String, Object> row, ArrayList<Hashtable<String, Object>> arr) {
		
		for(int i=0; i<arr.size(); i++) {
			if(row.equals(arr.get(i))) {
				return true;
			}
		}
		return false;
		
	}

	@SuppressWarnings("unchecked")
	public void deleteFromTable(String strTableName,
			Hashtable<String, Object> htblColNameValue, String strOperator)
			throws DBEngineException, ClassNotFoundException, IOException {
		
		//Load Table data to get the Primary key column name
		ObjectInputStream ois = new ObjectInputStream(
				new FileInputStream(new File("data/tabledata.class")));
		Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois.readObject();
		ois.close();
		String keyName = (String) tabledata.get(strTableName).get(1);
		
		//Get records by making a Query using selectFromTable
		Iterator<Hashtable<String, Object>> itr = selectFromTable(strTableName, htblColNameValue, strOperator);
		ArrayList<Hashtable<String, Object>> arr = new ArrayList<Hashtable<String, Object>>();
		while(itr.hasNext()) {
			arr.add(itr.next());
		}
		
		//Two Hashtables for lazy loading of pages and indices
		Hashtable<Integer, ArrayList<Hashtable<String, Object>>> pagesLoaded = 
				new Hashtable<Integer, ArrayList<Hashtable<String, Object>>>();
		Hashtable<String, BPlusTree> indicesLoaded = new Hashtable<String, BPlusTree>();
		
		
		//Load indices in memory for the indexed columns
		Set<String> cols = tableColumns(strTableName);
		for( String col : cols ) {
			if(hasIndex(strTableName, col)) {
				ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(
						new File("data/" + strTableName + "." + col + ".index.class")));
				BPlusTree bt = (BPlusTree) ois2.readObject();
				ois2.close();
				
				indicesLoaded.put(col, bt);
			}
		}
		
		//Iterate over resulting query records
		for(int i=0; i<arr.size(); i++) {
			
			//Get the PKey Index; Delete the record entry from it then using the page no. and row afterwards
			BPlusTree pKeyIndex = indicesLoaded.get(keyName);
			ArrayList<Object> del = pKeyIndex.delete(arr.get(i).get(keyName));
			Pair pair = new Pair((Integer)del.get(0), (Integer)del.get(1));
			
			//If the page has not been loaded before; Load it.
			if(pagesLoaded.get(pair.getPage()) == null) {
				ObjectInputStream ois3 = new ObjectInputStream(
						new FileInputStream(new File("data/" + strTableName + "" + pair.getPage() + ".class")));
				pagesLoaded.put(pair.getPage(), (ArrayList<Hashtable<String, Object>>) ois3.readObject());
				ois3.close();
			}
			
			//Remove it from the page.
			pagesLoaded.get(pair.getPage()).set(pair.getRow().intValue(), null);
			
			//Update Non-Primary Key Indices
			for( String col : cols ) {
				if(hasIndex(strTableName, col) && !col.equals(keyName)) {
					BPlusTree bt = indicesLoaded.get(col);
					boolean found = false;
					ArrayList<Pair> tempPairs = new ArrayList<Pair>();
					while(!found) {
						ArrayList<Object> tmp = bt.delete(arr.get(i).get(col));
						Pair p = new Pair((Integer)tmp.get(0), (Integer)tmp.get(1));
						if(p.equals(pair)) found = true;
						else tempPairs.add(p);
					}
					
					while(!tempPairs.isEmpty()) {
						Pair t = tempPairs.remove(tempPairs.size()-1);
						bt.insert(arr.get(i).get(col), t.getPage().intValue(), t.getRow().intValue());
					}
				}
			}
			
		}
		
		//Writing Edited Pages Again.
		Set<Integer> pages = pagesLoaded.keySet();
		for( Integer page : pages ) {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(new File("data/" + strTableName + "" + page + ".class")));
			oos.writeObject(pagesLoaded.get(page));
			oos.close();
		}
		
		//Writing Edited Indices Again.
		Set<String> indices = indicesLoaded.keySet();
		for( String index : indices ) {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(new File("data/" + strTableName + "." + index + ".index.class")));
			oos.writeObject(indicesLoaded.get(index));
			oos.close();
		}
		
	}

	@SuppressWarnings("unchecked")
	public Iterator<Hashtable<String, Object>> selectFromTable(String strTable,
			Hashtable<String, Object> htblColNameValue, String strOperator)
			throws DBEngineException, IOException, ClassNotFoundException {
		if (!tableExists(strTable)
				|| (!strOperator.equals("AND") && !strOperator.equals("OR"))) {
			throw new DBEngineException();
		}

		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
				new File("data/tabledata.class")));
		Hashtable<String, ArrayList<Object>> tabledata = (Hashtable<String, ArrayList<Object>>) ois
				.readObject();
		ois.close();

		int nextPage = ((Integer) tabledata.get(strTable).get(0)).intValue();
		ArrayList<Hashtable<String, Object>> elrayes = new ArrayList<Hashtable<String, Object>>();
		
		//ArrayList containing pairs of (page,row) that will be the result of the query
		ArrayList<Pair> pairs = new ArrayList<Pair>();
		
		
		if(strOperator.equalsIgnoreCase("AND")) {
			
			boolean firstIndex = true;
			//Using Indexed Columns First:
			ArrayList<String> queryCols = new ArrayList<String>(htblColNameValue.keySet());
			ArrayList<String> queryCols2 = new ArrayList<String>(htblColNameValue.keySet());
			for( String col : queryCols ) {
				if(hasIndex(strTable, col)) {
					ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(
							new File("data/" + strTable + "." + col + ".index.class")));
					BPlusTree bt = (BPlusTree) ois2.readObject();
					ois2.close();
					
					ArrayList<Pair> tempPairs = new ArrayList<Pair>();
					
					Node leaf = bt.findLeaf(htblColNameValue.get(col));
					ArrayList<Node> leafLevel = bt.sameLevel.get(0);
					
					boolean finished = false;
					while(!finished) {
						for(int i=leaf.getItems().size()-1; i>=0; i--) {
							if(bt.compare(leaf.getItems().get(i).key, htblColNameValue.get(col)) == 0) {
								tempPairs.add(new 
										Pair(((Integer)leaf.getItems().get(i).left.getItems().get(0).key),
										((Integer)leaf.getItems().get(i).left.getItems().get(1).key)));
								
							} else if(bt.compare(leaf.getItems().get(i).key, htblColNameValue.get(col)) < 0) {
								finished = true; break;
							}
						}
						
						int idx = leafLevel.indexOf(leaf);
						if(idx > 0) leaf = leafLevel.get(idx-1);
						else break;
					}
					
					if(firstIndex) {
						for(int i=tempPairs.size()-1; i>=0; i--)
							pairs.add(tempPairs.get(i));
						firstIndex = false;
					} else {
						for(int i=pairs.size()-1; i>=0; i--)
							if(!tempPairs.contains(pairs.get(i))) pairs.remove(i);
					}
					
					queryCols2.remove(col);
					
				}
			}
			
			//Handling non Indexed Query Cols:
			//First Condition operates when there was Indexed Columns Processed above.
			if(queryCols2.size() < htblColNameValue.keySet().size()) {
				Hashtable<Integer, ArrayList<Hashtable<String, Object>>> pagesOpened = 
						new Hashtable<Integer, ArrayList<Hashtable<String, Object>>>();
				for(int i=0; i<pairs.size(); i++) {
					Pair p = pairs.get(i);
					if(pagesOpened.get(p.getPage()) == null) {
						ObjectInputStream ois3 = new ObjectInputStream(new FileInputStream(
								new File("data/" + strTable + "" + p.getPage().intValue() + ".class")));
						ArrayList<Hashtable<String, Object>> tablePage = (ArrayList<Hashtable<String, Object>>) ois3
								.readObject();
						ois3.close();
						
						pagesOpened.put(p.getPage(), tablePage);
					}
					
					Hashtable<String, Object> record = pagesOpened.get(p.getPage()).get(p.getRow());
					boolean takeIt = true;
					for( String col : queryCols2 ) 
						if(!record.get(col).equals(htblColNameValue.get(col))) { takeIt = false; break; }
					if(takeIt) elrayes.add(record);
				}
			//This condition operates when there was no indexed columns thus scans the whole table
			} else {
				
				for (int i = 1; i <= nextPage; i++) {

					if (i == 1 || i == nextPage) {
						File ff = new File("data/" + strTable + i + ".class");
						if (!ff.exists())
							break;
					}
		                       
					ObjectInputStream badawy = new ObjectInputStream(
							new FileInputStream(new File("data/" + strTable + i + ".class")));
					ArrayList<Hashtable<String, Object>> list = (ArrayList<Hashtable<String, Object>>) badawy
							.readObject();
					badawy.close();

					for (int j = 0; j < list.size(); j++) {

						Hashtable<String, Object> tuple = list.get(j);
						
						if(tuple == null) continue;
						
						Set<String> keys = htblColNameValue.keySet();
						boolean z = true;
						for (String key : keys) {
							z = z
									&& (tuple.get(key).equals(htblColNameValue
											.get(key)));
						}
						if (z)
							elrayes.add(tuple);

					}

				}
				
			}
			
		} else if(strOperator.equalsIgnoreCase("OR")) {
			
			boolean allHasIndex = true;
			Set<String> cols = htblColNameValue.keySet();
			for( String col : cols ) 
				if(!hasIndex(strTable, col)) { allHasIndex = false; break; }
			
			if(allHasIndex) {
				for( String col : cols ) {
					ObjectInputStream ois2 = new ObjectInputStream(new FileInputStream(
							new File("data/" + strTable + "." + col + ".index.class")));
					BPlusTree bt = (BPlusTree) ois2.readObject();
					ois2.close();
					
					//ArrayList<Pair> tempPairs = new ArrayList<Pair>();
					
					Node leaf = bt.findLeaf(htblColNameValue.get(col));
					
					if(bt.sameLevel.size()==0) return elrayes.iterator();
					ArrayList<Node> leafLevel = bt.sameLevel.get(0);
					
					
					boolean finished = false;
					while(!finished) {
						for(int i=leaf.getItems().size()-1; i>=0; i--) {
							
							if(bt.compare(leaf.getItems().get(i).key, htblColNameValue.get(col)) == 0) {
								
								Pair temp = new 
										Pair(((Integer)leaf.getItems().get(i).left.getItems().get(0).key),
										((Integer)leaf.getItems().get(i).left.getItems().get(1).key));
								if(!pairs.contains(temp)) pairs.add(temp);
								
							} else if(bt.compare(leaf.getItems().get(i).key, htblColNameValue.get(col)) < 0) {
								finished = true; break;
							}
						}
						
						int idx = leafLevel.indexOf(leaf);
						if(idx > 0) leaf = leafLevel.get(idx-1);
						else break;
					}
					
				}
				
				Hashtable<Integer, ArrayList<Hashtable<String, Object>>> pagesOpened = 
						new Hashtable<Integer, ArrayList<Hashtable<String, Object>>>();
				for(int i=0; i<pairs.size(); i++) {
					Pair p = pairs.get(i);
					if(pagesOpened.get(p.getPage()) == null) {
						ObjectInputStream ois3 = new ObjectInputStream(new FileInputStream(
								new File("data/" + strTable + "" + p.getPage().intValue() + ".class")));
						ArrayList<Hashtable<String, Object>> tablePage = (ArrayList<Hashtable<String, Object>>) ois3
								.readObject();
						ois3.close();
						
						pagesOpened.put(p.getPage(), tablePage);
					}
					
					Hashtable<String, Object> record = pagesOpened.get(p.getPage()).get(p.getRow());
					elrayes.add(record);
				}
				
			//If at least one of them has no index we must scan the whole table
			} else {
				for (int i = 1; i <= nextPage; i++) {

					if (i == 1 || i == nextPage) {
						File ff = new File("data/" + strTable + i + ".class");
						if (!ff.exists())
							break;
					}
		                       
					ObjectInputStream badawy = new ObjectInputStream(
							new FileInputStream(new File("data/" + strTable + i + ".class")));
					ArrayList<Hashtable<String, Object>> list = (ArrayList<Hashtable<String, Object>>) badawy
							.readObject();
					badawy.close();

					for (int j = 0; j < list.size(); j++) {

						Hashtable<String, Object> tuple = list.get(j);

						if(tuple == null) continue;
						
						Set<String> keys = htblColNameValue.keySet();
						for (String key : keys) {
							if (tuple.get(key).equals(htblColNameValue.get(key))) {
								elrayes.add(tuple); break;
							}
						}

					}

				}
			}
			
		} else {
			System.out.println("Invalid Operator; It must be either AND or OR !");
		}
		
		//Make the Iterator needed from "elrayes" result of the query ArrayList
		Iterator<Hashtable<String, Object>> itr = elrayes.iterator();
		return itr;
	}

	public boolean hasIndex(String strTableName, String strColName) {
		File f = new File("data/" + strTableName + "." + strColName + ".index.class");
		return f.exists();
	}
	
	public Set<String> tableColumns(String strTableName) throws IOException {
		
		if(!tableExists(strTableName)) return null;
		Hashtable<String, Integer> colSet = new Hashtable<String, Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader("data/metadata.csv"));
		String line = "";
		while ((line = br.readLine()) != null) {
			String[] s = line.split(",");
			if(s.length==0) continue;
			if(s[0].equals(strTableName)) colSet.put(s[1], new Integer("1"));
		}
		br.close();
	
		return colSet.keySet();
	}
	
	public ArrayList<ArrayList<String>> hasForeignKey(String strTableName) throws IOException{
		
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		File metaData = new File("data/metadata.csv");
		BufferedReader br = null;
		
		if(metaData.exists()){
			String line="";
			try {
				br = new BufferedReader(new FileReader("data/metadata.csv"));
				while((line=br.readLine())!=null){
					String[] s = line.split(",");
					if(s[0].equals(strTableName)&& !s[5].equals("null")){
						ArrayList<String> singleReference = new ArrayList<String>();
						singleReference.add(s[1]);
						String[] tableReference = s[5].split("\\.");
						singleReference.add(tableReference[0]);
						singleReference.add(tableReference[1]);
						data.add(singleReference);
					}
				}
			} catch (FileNotFoundException e){
				e.printStackTrace();
			
			}
		}
		br.close();
		return data;
		
	}
	
}

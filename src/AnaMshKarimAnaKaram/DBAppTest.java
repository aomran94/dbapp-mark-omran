package AnaMshKarimAnaKaram;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

public class DBAppTest {

	public static void main(String[] args) throws DBAppException,
			DBEngineException, ClassNotFoundException, IOException {
		
		// create a new DBApp
		DBApp myDB = new DBApp();

		// initialize it
		myDB.init();
		
		
		// creating table "Faculty" with additional Index on "Name"

		Hashtable<String, String> fTblColNameType = new Hashtable<String, String>();
		fTblColNameType.put("ID", "Integer");
		fTblColNameType.put("Name", "String");

		Hashtable<String, String> fTblColNameRefs = new Hashtable<String, String>();

		myDB.createTable("Faculty", fTblColNameType, fTblColNameRefs, "ID");
		myDB.createIndex("Faculty", "Name");
		
		System.out.println("Done 1 : " + "creating table \"Faculty\" with additional Index on \"Name\"");
		
		
		
		

		
		// creating table "Major" with additonal Index on "Name"

		Hashtable<String, String> mTblColNameType = new Hashtable<String, String>();
		mTblColNameType.put("ID", "Integer");
		mTblColNameType.put("Name", "String");
		mTblColNameType.put("Faculty_ID", "Integer");

		Hashtable<String, String> mTblColNameRefs = new Hashtable<String, String>();
		mTblColNameRefs.put("Faculty_ID", "Faculty.ID");

		myDB.createTable("Major", mTblColNameType, mTblColNameRefs, "ID");
		myDB.createIndex("Major", "Name");
		
		System.out.println("Done 2 : " + "creating table \"Major\" with additonal Index on \"Name\"");
		
		
		
		
		
		// creating table "Course" with additional Index on "Hours"

		Hashtable<String, String> coTblColNameType = new Hashtable<String, String>();
		coTblColNameType.put("ID", "Integer");
		coTblColNameType.put("Name", "String");
		coTblColNameType.put("Code", "String");
		coTblColNameType.put("Hours", "Integer");
		coTblColNameType.put("Semester", "Integer");
		coTblColNameType.put("Major_ID", "Integer");

		Hashtable<String, String> coTblColNameRefs = new Hashtable<String, String>();
		coTblColNameRefs.put("Major_ID", "Major.ID");

		myDB.createTable("Course", coTblColNameType, coTblColNameRefs, "ID");
		myDB.createIndex("Course", "Hours");

		System.out.println("Done 3 : " + "creating table \"Course\" with additonal Index on \"Hours\"");
		
		
		
		
		
		// creating table "Student" with additional Index on "First_Name"
		
		Hashtable<String, String> stTblColNameType = new Hashtable<String, String>();
		stTblColNameType.put("ID", "Integer");
		stTblColNameType.put("First_Name", "String");
		stTblColNameType.put("Last_Name", "String");
		stTblColNameType.put("GPA", "Double");
		stTblColNameType.put("Age", "Integer");

		Hashtable<String, String> stTblColNameRefs = new Hashtable<String, String>();

		myDB.createTable("Student", stTblColNameType, stTblColNameRefs, "ID");
		myDB.createIndex("Student", "First_Name");
		
		System.out.println("Done 4 : " + "creating table \"Student\" with additonal Index on \"First_Name\"");
		
		
		

		
		// creating table "Student_in_Course"

		Hashtable<String, String> scTblColNameType = new Hashtable<String, String>();
		scTblColNameType.put("ID", "Integer");
		scTblColNameType.put("Student_ID", "Integer");
		scTblColNameType.put("Course_ID", "Integer");

		Hashtable<String, String> scTblColNameRefs = new Hashtable<String, String>();
		scTblColNameRefs.put("Student_ID", "Student.ID");
		scTblColNameRefs.put("Course_ID", "Course.ID");

		myDB.createTable("Student_in_Course", scTblColNameType,
				scTblColNameRefs, "ID");
		
		System.out.println("Done 5 : " + "creating table \"Student_in_Course\"");
		
		
		
   
		
		// insert in table "Faculty"

		Hashtable<String, Object> ftblColNameValue1 = new Hashtable<String, Object>();
		ftblColNameValue1.put("ID", Integer.valueOf("0"));
		ftblColNameValue1.put("Name", "Media Engineering and Technology");
		myDB.insertIntoTable("Faculty", ftblColNameValue1);

		Hashtable<String, Object> ftblColNameValue2 = new Hashtable<String, Object>();
		ftblColNameValue2.put("ID", Integer.valueOf("1"));
		ftblColNameValue2.put("Name", "Management Technology");
		myDB.insertIntoTable("Faculty", ftblColNameValue2);
		
		
		for (int i = 0; i < 150; i++) {
			Hashtable<String, Object> ftblColNameValueI = new Hashtable<String, Object>();
			ftblColNameValueI.put("ID", Integer.valueOf(("" + (i + 2))));
			ftblColNameValueI.put("Name", "f" + (i + 2));
			myDB.insertIntoTable("Faculty", ftblColNameValueI);
		}
		
		System.out.println("Done 6 : " + "insert 2+150rec. in table \"Faculty\"");
		
		
		
		
		
		// insert in table "Major"

		Hashtable<String, Object> mtblColNameValue1 = new Hashtable<String, Object>();
		mtblColNameValue1.put("ID", Integer.valueOf("0"));
		mtblColNameValue1.put("Name", "Computer Science & Engineering");
		mtblColNameValue1.put("Faculty_ID", Integer.valueOf("1"));
		myDB.insertIntoTable("Major", mtblColNameValue1);

		Hashtable<String, Object> mtblColNameValue2 = new Hashtable<String, Object>();
		mtblColNameValue2.put("ID", Integer.valueOf("1"));
		mtblColNameValue2.put("Name", "Business Informatics");
		mtblColNameValue2.put("Faculty_ID", Integer.valueOf("2"));
		myDB.insertIntoTable("Major", mtblColNameValue2);

		for (int i = 0; i < 30; i++) {
			Hashtable<String, Object> mtblColNameValueI = new Hashtable<String, Object>();
			mtblColNameValueI.put("ID", Integer.valueOf(("" + (i + 2))));
			mtblColNameValueI.put("Name", "m" + (i + 2));
			mtblColNameValueI
					.put("Faculty_ID", Integer.valueOf(("" + (i + 2))));
			myDB.insertIntoTable("Major", mtblColNameValueI);
		}
		
		System.out.println("Done 7 : " + "insert 2+30rec. in table \"Major\"");
		
		
		
		
		// insert in table "Course"

		Hashtable<String, Object> ctblColNameValue1 = new Hashtable<String, Object>();
		ctblColNameValue1.put("ID", Integer.valueOf("0"));
		ctblColNameValue1.put("Name", "Data Bases II");
		ctblColNameValue1.put("Code", "CSEN 604");
		ctblColNameValue1.put("Hours", Integer.valueOf("4"));
		ctblColNameValue1.put("Semester", Integer.valueOf("6"));
		ctblColNameValue1.put("Major_ID", Integer.valueOf("1"));
		myDB.insertIntoTable("Course", ctblColNameValue1);

		Hashtable<String, Object> ctblColNameValue2 = new Hashtable<String, Object>();
		ctblColNameValue2.put("ID", Integer.valueOf("1"));
		ctblColNameValue2.put("Name", "Data Bases II");
		ctblColNameValue2.put("Code", "CSEN 604");
		ctblColNameValue2.put("Hours", Integer.valueOf("4"));
		ctblColNameValue2.put("Semester", Integer.valueOf("6"));
		ctblColNameValue2.put("Major_ID", Integer.valueOf("2"));
		myDB.insertIntoTable("Course", ctblColNameValue2);

		for (int i = 0; i < 30; i++) {
			Hashtable<String, Object> ctblColNameValueI = new Hashtable<String, Object>();
			ctblColNameValueI.put("ID", Integer.valueOf(("" + (i + 2))));
			ctblColNameValueI.put("Name", "c" + (i + 2));
			ctblColNameValueI.put("Code", "co " + (i + 2));
			ctblColNameValueI.put("Hours", Integer.valueOf("4"));
			ctblColNameValueI.put("Semester", Integer.valueOf("6"));
			ctblColNameValueI.put("Major_ID", Integer.valueOf(("" + (i + 2))));
			myDB.insertIntoTable("Course", ctblColNameValueI);
		}
		
		System.out.println("Done 8 : " + "insert 2+30rec. in table \"Course\"");
		
		
		

		
		// insert in table "Student"

		for (int i = 0; i < 1450; i++) {
			Hashtable<String, Object> sttblColNameValueI = new Hashtable<String, Object>();
			sttblColNameValueI.put("ID", Integer.valueOf(("" + i)));
			sttblColNameValueI.put("First_Name", "FN" + i);
			sttblColNameValueI.put("Last_Name", "LN" + i);
			sttblColNameValueI.put("GPA", Double.valueOf("0.7"));
			sttblColNameValueI.put("Age", Integer.valueOf("20"));
			myDB.insertIntoTable("Student", sttblColNameValueI);
			// changed it to student instead of course
		}
		Hashtable<String, Object> sttblColNameValueI = new Hashtable<String, Object>();
		sttblColNameValueI.put("ID", Integer.valueOf(("" + 1532)));
		sttblColNameValueI.put("First_Name", "blbezo1");
		sttblColNameValueI.put("Last_Name", "bb1");
		sttblColNameValueI.put("GPA", Double.valueOf("1.0"));
		sttblColNameValueI.put("Age", Integer.valueOf("22"));
		myDB.insertIntoTable("Student", sttblColNameValueI);
		
		sttblColNameValueI.put("ID", Integer.valueOf(("" + 1533)));
		sttblColNameValueI.put("First_Name", "blbezo1");
		sttblColNameValueI.put("Last_Name", "bb2");
		sttblColNameValueI.put("GPA", Double.valueOf("1.3"));
		sttblColNameValueI.put("Age", Integer.valueOf("22"));
		myDB.insertIntoTable("Student", sttblColNameValueI);
		
		sttblColNameValueI.put("ID", Integer.valueOf(("" + 1534)));
		sttblColNameValueI.put("First_Name", "blbezo2");
		sttblColNameValueI.put("Last_Name", "bb2");
		sttblColNameValueI.put("GPA", Double.valueOf("1.7"));
		sttblColNameValueI.put("Age", Integer.valueOf("20"));
		myDB.insertIntoTable("Student", sttblColNameValueI);
		
		sttblColNameValueI.put("ID", Integer.valueOf(("" + 1535)));
		sttblColNameValueI.put("First_Name", "blbezo3");
		sttblColNameValueI.put("Last_Name", "bb3");
		sttblColNameValueI.put("GPA", Double.valueOf("1.3"));
		sttblColNameValueI.put("Age", Integer.valueOf("23"));
		myDB.insertIntoTable("Student", sttblColNameValueI);
		
		System.out.println("Done 9 : " + "insert 4+1450rec. in table \"Student\"");
		
		
		
		

		
		// Selecting from table "Student": feel free to edit & try any queries 
		
		Hashtable<String, Object> stblColNameValue = new Hashtable<String, Object>();
		stblColNameValue.put("ID", Integer.valueOf("17"));
		//stblColNameValue.put("First_Name", "FN17");
		//stblColNameValue.put("Age", Integer.valueOf("20"));

		long startTime = System.currentTimeMillis();
		Iterator<Hashtable<String, Object>> myIt = myDB.selectFromTable(
				"Student", stblColNameValue, "AND");
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;

		System.out.println(totalTime);
		while (myIt.hasNext()) {
			System.out.println(myIt.next());
		}
		
		Hashtable<String, Object> stblColNameValue3 = new Hashtable<String, Object>();
		stblColNameValue3.put("GPA", Double.valueOf("1.0"));

		long startTime3 = System.currentTimeMillis();
		Iterator<Hashtable<String, Object>> myIt3 = myDB.selectFromTable(
				"Student", stblColNameValue3, "AND");
		long endTime3 = System.currentTimeMillis();
		long totalTime3 = endTime3 - startTime3;

		System.out.println("Query Took : " + totalTime3 + " ms.");
		while (myIt3.hasNext()) {
			System.out.println(myIt3.next());
		}
		
		// Creating Index for "GPA" column
		myDB.createIndex("Student", "GPA");
		
		Hashtable<String, Object> stblColNameValue2 = new Hashtable<String, Object>();
		stblColNameValue2.put("GPA", Double.valueOf("1.0"));

		long startTime2 = System.currentTimeMillis();
		Iterator<Hashtable<String, Object>> myIt2 = myDB.selectFromTable(
				"Student", stblColNameValue2, "AND");
		long endTime2 = System.currentTimeMillis();
		long totalTime2 = endTime2 - startTime2;

		System.out.println("Query Took : " + totalTime2 + " ms.");
		while (myIt2.hasNext()) {
			System.out.println(myIt2.next());
		}
		
		System.out.println("Done 10 : " + "selecting from table \"Student\"");

		
		
		
		// Selecting from table "Major": feel free to edit & try any queries
		 
		Hashtable<String, Object> stblColNameValue4 = new Hashtable<String, Object>();
		stblColNameValue4.put("Name", "m7");
		stblColNameValue4.put("Faculty_ID", Integer.valueOf("7"));

		long startTime4 = System.currentTimeMillis();
		Iterator<Hashtable<String, Object>> myIt4 = myDB.selectFromTable(
				"Major", stblColNameValue4, "AND");
		long endTime4 = System.currentTimeMillis();
		long totalTime4 = endTime4 - startTime4;
		System.out.println("Query Took : " + totalTime4 + " ms.");
		while (myIt4.hasNext()) {
			System.out.println(myIt4.next());
		}
		
		System.out.println("Done 11 : " + "selecting from table \"Major\"");
		
		
		
		
		
		// Updating Table "Student" at row with ID "17" & "1532"
		
		Hashtable<String, Object> updColNameValue1 = new Hashtable<String, Object>();
		updColNameValue1.put("First_Name", "FNBlbezo");
		updColNameValue1.put("Last_Name", "LNBlbezo");
		myDB.updateTable("Student", new Integer("17"), updColNameValue1);
		
		Hashtable<String,Object> updColNameValue2 = new Hashtable<String,Object>();
		updColNameValue2.put("Age", Integer.valueOf("18"));
		myDB.updateTable("Student",new Integer("1532"),updColNameValue2);
		
		System.out.println("Done 12 : " + "Updating Table \"Student\" at row with ID \"17\" & \"1532\"");
		
		
		
		
		// Deleting from table "Student"
		
		Hashtable<String, Object> delColNameValue1 = new Hashtable<String, Object>();
		delColNameValue1.put("First_Name", "FNBlbezo");
		delColNameValue1.put("Last_Name", "LNBlbezo");
		myDB.deleteFromTable("Student", delColNameValue1, "OR");
		
		System.out.println("Done 13 : " + "Deleting from table \"Student\"");
		
		
		
		// Inserting into table "Course" to test PRIMARY KEY constraint check
		
		// This should give an error message as inserting with a "duplicate" entry for a Primary Key
		ctblColNameValue2.put("ID", Integer.valueOf("1"));
		ctblColNameValue2.put("Name", "Data Bases II");
		ctblColNameValue2.put("Code", "CSEN 604");
		ctblColNameValue2.put("Hours", Integer.valueOf("4"));
		ctblColNameValue2.put("Semester", Integer.valueOf("6"));
		ctblColNameValue2.put("Major_ID", Integer.valueOf("2"));
		myDB.insertIntoTable("Course", mtblColNameValue2);
		
		System.out.println("Done 14 : " 
				+ "Inserting into table \"Course\" to test PRIMARY KEY constraint check");
		
		
		
		// Inserting into table "Student_in_Course" to test FOREIGN KEY constraint check
		
		// This should insert properly
		Hashtable<String, Object> sctblColNameValueI = new Hashtable<String, Object>();
		sctblColNameValueI.put("ID", Integer.valueOf(("" + 1)));
		sctblColNameValueI.put("Student_ID", Integer.valueOf(("" + 1300)));
		sctblColNameValueI.put("Course_ID", Integer.valueOf(("" + 25)));
		myDB.insertIntoTable("Student_in_Course", sctblColNameValueI);
		
		// This should print an error message as 34 is not in "Course.ID"
		sctblColNameValueI.put("ID", Integer.valueOf(("" + 2)));
		sctblColNameValueI.put("Student_ID", Integer.valueOf(("" + 1301)));
		sctblColNameValueI.put("Course_ID", Integer.valueOf(("" + 34)));
		myDB.insertIntoTable("Student_in_Course", sctblColNameValueI);
		
		// This should print an error message as 1500 is not in "Student.ID"
		sctblColNameValueI.put("ID", Integer.valueOf(("" + 3)));
		sctblColNameValueI.put("Student_ID", Integer.valueOf(("" + 1500)));
		sctblColNameValueI.put("Course_ID", Integer.valueOf(("" + 25)));
		myDB.insertIntoTable("Student_in_Course", sctblColNameValueI);
		
		System.out.println("Done 15 : " 
				+ "Inserting into table \"Student_in_Course\" to test FOREIGN KEY constraint check");

		
		// Final Message
		System.out.println("Done All !! AnaMshKarimAnaKaram tells you goodluck !!");
		
	}
}

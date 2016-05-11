
Ahmed Omran / Mark Nader ::
TeamName: AnaMshKarimAnaKaram :))

DBApp Documentation : The Following will contain a documentation for both How to Run the Application, Overview on tests performed and explaination for the Methods of both the DBApp and the B+Tree
==========================================================================================

First : How to run the Application 
-------------------------
To run the application run the file DBAppTest.java it contains a plenty of tests each with good commentary and numbered with description for how and what that test does, or follow this documentation :


You have to create a new Instance of the DBApp like the following way 
// create a new DBApp
DBApp myDB = new DBApp();

then you have to invoke the .init() method like this so it makes some essential checks on the files needed for the App to run properly
// initialize it
myDB.init();

After then through the App instance "myDB" you have a plenty of methods you could invoke to do different things like table creation, insertion, queries, index creation, updating, deleting etc..., here We'll go through a brief explaination for how those methods work.

1- .createTable(tableName) : first it checks if the table exists before or not; if not it proceeds to check datatypes and check the referencing columns are referencing existing tables. If all checks are positive proceed editing the "metadata.csv" & "tabledata.class" file with the data of the table.

2- .createIndex(tableName, colName) : first it ensures that the table exists, has the required column and there is no index created before; if the checks are positive proceed with creating a new B+Tree, scanning the whole table and inserting the values in the B+Tree afterthen saving it to the Harddisk named [table_name].[column_name].index.class

3- .insertIntoTable(tableName, record)  : first it ensures that the table exists, the datatypes are valid, the primary key is neither null nor duplicate, the foreign keys values are existing in the referenced tables; if tests positive, proceed inserting it in the last page inserted and for each indexed column load the index from the harddisk, insert the entry and then write it back.

4- .selectFromTable(tableName, queryCondition, queryOperator) : first it checks if the table exists and the operator is a valid one; then it has two cases: if the operator is "AND" it restricts the search space using the indexed columns first then if there is non-indexed columns in the queryCondition it scans the past restricted space to apply conditions; if there were no indexed columns of the queryCondition, it scans the whole column
if the operator was an "OR", either we'll have all indexed query cols then It will use the indexes to calculate the union then load the pages to get records, or we'll have at least one non-indexed column where we'll have now to scan the whole table.

5- .deleteFromTable(tableName, queryCondition, queryOperator) : it applies a query using .selectFromTable then for each record it searches for its position using the Pkey index, then it puts a null in its position then goes to all the non-Primarykey indexes and deletes the entries from them; duplicates here are handled also.

6- .updateTable(tableName, key, record) : it searches for the record using the Primary key index then loads the page, updates the record and goes to all indices updating then entries in them.

Also there were some helper methods defined in the App:
----------------------------

1- .tableExists(tableName) : checks whether a table named [tableName] exists or not.

2- .checkDataTypes(tableName, record) : checks whether this [record] satisfies the predefined datatypes for the table [tableName].

3- .belongsTo(row, page) : checks if a row belongs to a specific page.

4- .hasIndex(tableName, colName) : checks if the column [colName] in table [tableName] has an index or not.

5- .tableColumns(tableName) : gets a set containing the names of all the columns of the table [tableName].

6- .hasForeignKey(tableName) : returns an ArrayList containing a description of every foreign key that table has; used in ForeignKey checks.


==========================================================================================
******************************************************************************************
==========================================================================================


The B+Tree Works as follows :-
  A-) Insertion :-
            I-) Leaf Insertion :- whenever an Item is inserted in the leaf , a check is made to see if there is an overflow
that happened , if there is , a split will occur with the left node having (Ceil)((n+1)/2)  and right node having the rest
which is implicitly (Floor)((n+1)/2) , so if n = 4 and 1 more key is inserted .. left will have 3 keys and right 2 keys
if n=5 .. left and will right will both have 3 keys ... Then the smallest key in the right node is inserted in upperLevel
with its left pointing to the left node and its right pointing to the right node
            
            II-) Inner Node Insertion :- a node is inserted in the inner leaf , if an overflow happens , the split is 
applied as follows :- the First (Ceil)(n/2) remains in left node .... the last (Floor)(n/2) goes to the right node...
and we have now an item in the middle .. because (Ceil)(n/2) + (Floor)(n/2) = n items ... and we have n+1 items due to 
the overflow ... This middle item ( which is called in the code :- ItemOFInterest ) is inserted in the upperLevel with 
its left pointing to the left node and its right pointing to the right node ... the iteration keeps up till an insertion
in upper level does not cause an over flow ... the root was overflowed ... it is split and the ItemOfInterest will be 
put in the new upper node forming our new Root



B-) Deletion :-
            I-) Leaf Deletion :-
This can cause one of three senarios :-
                     i-) Remaning space in the leaf 
                     ii-) UnderFlow but possibility of borrow
                     iii-) UnderFlow with essence of Merge

i-) Remaning space in the leaf :- this is the easiest case .. whenever you delete from the node but the remaining items 
do not cause an underFlow .. in this case just an update is happened in upper Parent if it was the leftmost item (except
if that left most item was in the very left most of the tree) . During the update , whenever the deleted key is met .. it
is replaced with the new leftmost key in the node from which the key was deleted

ii-) UnderFlow but possibility of borrow  :- in this case the node looks to its left first and then its right to borrow 
from ( except if it was the leftmost in its subtree .. so it will have one option :- just to look to its right .... or 
if it was the rightmost in its subtree... so it will have one option :- just look to its left ...)
Borrowing from right node is tricky because two possible updates maybe needed :- one to the borrowed from ( this update 
always happen) as we borrow its left most item ... the second is to the borrower ( doesnt always happen ) if the deleted
item from it was the leftmost one ... and again we mean by update to go to its parent updating the old leftmost with the 
new leftmost ... and keep iterating through parents till finding ... if found .. update and break ..

iii-) UnderFlow with essence of Merge :- in this case .. the node could not borrow from its neighbours ... so each one is 
merged with the left node ( except the left most one which is merged with the right one ) ... the necessary updates are 
made and the common parent is deleted ... and if at the parent ... the node had a right item to that deleted one ... its 
left pointer is set to the new merged node ... and the InnerDeletion is called


            II-) Inner Deletion :-
This can cause one of three senarios :-
                     i-) Remaning space in the node
                     ii-) UnderFlow but possibility of borrow
                     iii-) UnderFlow with essence of Merge

i-) Remaning space in the node :- if there was space in the node without causing underflow ... the method returns and that
's it

ii-) UnderFlow but possibility of borrow :- the borrow here is different as when a node borrows from a neighbour ...
it borrows the common parent ... and the common parent is replaced with the item borrowed from the neighbour ... 
and the parent relationships and ponters are handled and then it returns 

iii-) UnderFlow with essence of Merge :- the merge is also different .. as not only the two nodes are merged but also 
an extra item is added between them which is their common parent ... and their common parent is deleted from the parent 
node ... this keeps happening and calling DeleteInner ... till it either returns due to space non-violation or borrow
senario or reaching the root and deleting the last item from it .. and in this case the child merged node becomes our 
new Root .

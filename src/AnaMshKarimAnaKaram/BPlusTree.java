package AnaMshKarimAnaKaram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;



public class BPlusTree implements Serializable {
	
	private static final long serialVersionUID = 8171517037663009515L;
	Node root;
	static int N;
	int minKeysLeaf;
	int minKeysInner;
	ArrayList<ArrayList<Node>> sameLevel;

	public BPlusTree(int n) {
		N = n;
		sameLevel=new ArrayList<ArrayList<Node>>();
		minKeysLeaf = ((int) Math.floor((N+1.0)/2.0));
		minKeysInner= ((int) Math.ceil((N+1.0)/2.0)) - 1;
	}

	public Node findLeaf(Object key) {
		return findLeafHelper(key, this.root);
	}

	public Node findLeafHelper(Object key, Node node) {

		if (node == null)
			return null;

		if (node.getType().equals("Leaf"))
			return node;

		int i = 0;
		boolean broke = false;
		for (i = 0; i < node.getItems().size(); i++)
			if (this.compare(key, node.getItems().get(i).key)<=-1){
				broke = true; break;
			}
		
		Node t;
        if(broke)
        	t = node.getItems().get(i).left;
        else
        	t = node.getItems().get(i-1).right;
        
		return findLeafHelper(key, t);

	}

	@SuppressWarnings("unchecked")
	public void insert(Object key,int page,int row) {
		Node node = this.findLeaf(key);
		
		Node Data = new Node("Data",null);
		Data.getItems().add(new Item(page,null,null));
		Data.getItems().add(new Item(row,null,null));
		
		if(node==null){
			this.root= new Node("Leaf",null);
			this.root.getItems().add(new Item(key,Data,null));
			
			//added recently
			sameLevel.add(new ArrayList<Node>());
			sameLevel.get(0).add(this.root);
			return;
		}
		node.getItems().add(new Item(key,Data,null));
		Collections.sort(node.getItems());
		if(node.getItems().size()>N)
			splitLeaf(node);
		
	}
	
	@SuppressWarnings("unchecked")
	public void insertInner(Node oldNode, Node newNode, Object key){
		
		Node parent = oldNode.getParent();
		Item item = new Item(key, oldNode, newNode);
		if(parent==null){
			this.root=new Node("Inner",null);
			this.root.setParent(null);
			this.root.getItems().add(new Item(key,oldNode,newNode));
			oldNode.setParent(this.root);
			newNode.setParent(this.root);
			
			//added recently
			ArrayList<Node> newLevel= new ArrayList<Node>();
			sameLevel.add(newLevel);
			int index = sameLevel.indexOf(newLevel);
			sameLevel.get(index).add(this.root);
			
			return;
		}
		
		
			parent.getItems().add(item);
			Collections.sort(parent.getItems());
			
			int idx = parent.getItems().indexOf(item);
			if(idx < parent.getItems().size()-1) {
				parent.getItems().get(idx+1).left = newNode;
			                                     }
			if(parent.getItems().size()>N)
			splitInner(parent);
			
	
	}

	@SuppressWarnings("unchecked")
	public void splitLeaf(Node oldNode) {
		
		int oldSize = ((int) Math.ceil((N+1.0)/2.0));
		Node newNode = new Node("Leaf",oldNode.getParent());
		
		
		while(oldNode.getItems().size() > oldSize)
			newNode.getItems().add(oldNode.getItems().remove(oldSize));
		
	
		sameLevel.get(0).add(newNode);
		Collections.sort(sameLevel.get(0));
		
		Object SmallestInRightSubTree = newNode.getItems().get(0).key;
		
		insertInner(oldNode, newNode, SmallestInRightSubTree);
		
	}
	
	@SuppressWarnings("unchecked")
	public void splitInner(Node toBeSplit) {
		int oldSize = ((int) Math.ceil(N/2.0));
		Node newNode = new Node("Inner",toBeSplit.getParent());
		
		int j;
		for(j=1;j<sameLevel.size();j++){
			if(sameLevel.get(j).contains(toBeSplit)){
				break;
			}
		}
		
		Object itemOfInterest = toBeSplit.getItems().get(oldSize).key;
		toBeSplit.getItems().remove(oldSize);
        while(toBeSplit.getItems().size()>oldSize)    
        	newNode.getItems().add(toBeSplit.getItems().remove(oldSize));
                                                    
        for(int i=0;i<newNode.getItems().size();i++){
        	newNode.getItems().get(i).left.setParent(newNode);
        	newNode.getItems().get(i).right.setParent(newNode);
        }
        sameLevel.get(j).add(newNode);
        Collections.sort(sameLevel.get(j));
        
        
			insertInner(toBeSplit, newNode,itemOfInterest);
	}
	
	
	//////////////////////////
	@SuppressWarnings("unchecked")
	public ArrayList<Object> delete (Object key){
		ArrayList <Object>data = new ArrayList<Object>();
		Node node = this.findLeaf(key);
		Node neighbour;
		if(node==null){
			System.out.println("The Tree is Empty , Insert something first!");
			return null;
		}
		
		int i;
		for(i=node.getItems().size()-1;i>=0;i--){
			if(this.compare(key, node.getItems().get(i).key)==0){
				data.add(node.getItems().get(i).left.getItems().get(0).key);
				data.add(node.getItems().get(i).left.getItems().get(1).key);
				node.getItems().remove(i);
				break;
			}
				
		}
		
		if(i==-1){
			System.out.println("No Such Item in the tree");
			return null;
		         }
		
		
		else{
		   if(sameLevel.size()==1){
			      if(sameLevel.get(0).get(0).getItems().size()==0){
			    	  this.root=null;
			    	  sameLevel.remove(0);
			                                                      }
			      return data;
		                           }
		   
		  	if(node.getItems().size()>=this.minKeysLeaf){
		  		if(i==0 && sameLevel.get(0).indexOf(node)!=0){
		  			this.updateKey(node.getParent(),key,node.getItems().get(0).key);
		  		                                             }
		  		return data;
		  	                                        }
		  	
		  	
		   int indexOfParentItem = this.ParentItemIndex(node,node.getParent());
		   
		   if(indexOfParentItem==-1){
			   neighbour = node.getParent().getItems().get(0).right;
			   if(neighbour.getItems().size()>this.minKeysLeaf){
				   node.getItems().add(neighbour.getItems().remove(0));
				   this.updateKey(node.getParent(),node.getItems().get(node.getItems().size()-1).key, 
						   neighbour.getItems().get(0).key);
				   if(i==0)
				this.updateKey(node.getParent(),key,node.getItems().get(0).key);
				   return data;
				                                           }
			             merge(node,neighbour,-1,key,i);
			                        }
		   //else it is not leftmost
		   else{
			   neighbour = node.getParent().getItems().get(indexOfParentItem).left;
			   if(neighbour.getItems().size()>this.minKeysLeaf){
				   node.getItems().add(neighbour.getItems().remove(neighbour.getItems().size()-1));
				   Collections.sort(node.getItems());
				   
				   if(i!=0)
				   this.updateKey(node.getParent(),node.getItems().get(1).key, node.getItems().get(0).key);
				   else
				   this.updateKey(node.getParent(),key, node.getItems().get(0).key);
			                                               }
			   else{
			         if(indexOfParentItem!=node.getParent().getItems().size()-1){
			        	 neighbour = node.getParent().getItems().get(indexOfParentItem+1).right;
			        	 if(neighbour.getItems().size()>this.minKeysLeaf){
							   node.getItems().add(neighbour.getItems().remove(0));
							   this.updateKey(node.getParent(),node.getItems().get(node.getItems().size()-1).key, 
									   neighbour.getItems().get(0).key);
					     if(i==0)
			     		 this.updateKey(node.getParent(),key,node.getItems().get(0).key);

							   return data;
							                                          }
			                                                                    }
			         neighbour = node.getParent().getItems().get(indexOfParentItem).left;
			         //doesnt matter the last two parameters here !! put them anything
			         merge(node,neighbour,indexOfParentItem,null,i);
			       }
		       }
		   
		    }
		return data;
		
	}
	
	
	
	
	private void merge(Node node, Node neighbour, int indexOfParentItem,Object key, int position) {
		if(indexOfParentItem==-1){
			 while(neighbour.getItems().size()>0)
			 node.getItems().add(neighbour.getItems().remove(0));
			 
			 sameLevel.get(0).remove(neighbour);
			 if(position==0)
				 this.updateKey(node.getParent(),key,node.getItems().get(0).key);
		                         }
		 
		
		else {
			while(node.getItems().size()>0)
			 neighbour.getItems().add(node.getItems().remove(0));
			 
			 sameLevel.get(0).remove(node);
			 node=neighbour;
		     }
		  
		if(indexOfParentItem==-1) indexOfParentItem=0;  
		 
		
		node.getParent().getItems().remove(indexOfParentItem);
		//if it wasn't the last item .. it is made so because the check is after removal , so size decreased
		 // if it was last item , then size = indexofParentItem now
		 // remember :- when a remove is done , items are left-shifted .. so the right of removed node is 
		 // now also at index of (indexOfParentItem)
		 if(indexOfParentItem < node.getParent().getItems().size()) {
			 node.getParent().getItems().get(indexOfParentItem).left=node;
		                                                            }
		
		 
		if(node.getParent()==this.root){
			 if(this.root.getItems().size()==0){
				 this.root=node;
				 this.root.setParent(null);
				 this.root.type="Leaf";
				 sameLevel.remove(sameLevel.size()-1);
			                                   }
			 return;
		                               }
		
		deleteInner(node.getParent(),1);
	}

	
	
	
	@SuppressWarnings("unchecked")
	private void deleteInner(Node node, int level) {
		if(node.getItems().size()>=this.minKeysInner)
			return;
		
		Node neighbour;
		int indexOfParentItem = this.ParentItemIndex(node,node.getParent());
		if(indexOfParentItem==-1){
			   neighbour = node.getParent().getItems().get(0).right;
			   if(neighbour.getItems().size()>this.minKeysInner){
			//the left is retrieved by this way to avoid null pointer exception if the deleted item in merge 
		    //was the last one and hence node.right will be null.right which is an exception
				   Node LeftMostChildOfNeighbour = neighbour.getItems().get(0).left;
				   int index = sameLevel.get(level-1).indexOf(LeftMostChildOfNeighbour);
				   Node LeftChild = sameLevel.get(level-1).get(index-1);
				  Item borrowed = new Item(node.getParent().getItems().get(0).key,LeftChild,LeftMostChildOfNeighbour);
				  
				  node.getParent().getItems().get(0).key=neighbour.getItems().get(0).key;
				  neighbour.getItems().remove(0);
				  borrowed.right.setParent(node);
				  node.getItems().add(borrowed);
				   return;
				                                                }
			             mergeInner(node,neighbour,-1,level);
			                       }
		
		 //DONT FORGET TO CHANGE PARENT FOR SOME GUY HERE , THE ONE U WILL BORROW FROM
		   else{
			   neighbour = node.getParent().getItems().get(indexOfParentItem).left;
			   if(neighbour.getItems().size()>this.minKeysInner){
				   Node RightMostChildOfNeighbour = neighbour.getItems().get(neighbour.getItems().size()-1).right;
			       int index = sameLevel.get(level-1).indexOf(RightMostChildOfNeighbour);
			       Node RightChild =  sameLevel.get(level-1).get(index+1);
			       Object key = node.getParent().getItems().get(indexOfParentItem).key;
				  Item borrowed = new Item(key,RightMostChildOfNeighbour,RightChild);
				  
				  node.getParent().getItems().get(indexOfParentItem).key=neighbour.getItems()
						  .get(neighbour.getItems().size()-1).key;
				  
				  neighbour.getItems().remove(neighbour.getItems().size()-1);
				  borrowed.left.setParent(node);
				    node.getItems().add(borrowed);
				    Collections.sort(node.getItems());
			                                                    }
			   else{
			         if(indexOfParentItem!=node.getParent().getItems().size()-1){
			        	 neighbour = node.getParent().getItems().get(indexOfParentItem+1).right;
			        	 if(neighbour.getItems().size()>this.minKeysInner){
			        		 Node LeftMostChildOfNeighbour = neighbour.getItems().get(0).left;
			        		 int index = sameLevel.get(level-1).indexOf(LeftMostChildOfNeighbour);
			        		 Node LeftChild = sameLevel.get(level-1).get(index-1);
			        		 Object key = node.getParent().getItems().get(indexOfParentItem).key;
							  Item borrowed = new Item(key,LeftChild,LeftMostChildOfNeighbour);
							  
							  node.getParent().getItems().get(indexOfParentItem).key=neighbour.getItems().get(0).key;
							  neighbour.getItems().remove(0);
							  borrowed.right.setParent(node);
							  node.getItems().add(borrowed); 
							   return;
							                                          }
			                                                                    }
			         neighbour = node.getParent().getItems().get(indexOfParentItem).left;
			         mergeInner(node,neighbour,indexOfParentItem,level);
			       }
		       }
	}
	
	
  private void mergeInner(Node node, Node neighbour, int indexOfParentItem,int level) {
	
		//dont forget to set parent for each one here if u merge!!
	  if(indexOfParentItem==-1){
		  Node LeftMostChildOfNeighbour = neighbour.getItems().get(0).left;
		  Object key = node.getParent().getItems().get(0).key;
		  int index = sameLevel.get(level-1).indexOf(LeftMostChildOfNeighbour);
		  Node LeftChild = sameLevel.get(level-1).get(index-1);
		  Item inserted = new Item(key,LeftChild,LeftMostChildOfNeighbour);
		  inserted.right.setParent(node);
		  node.getItems().add(inserted);
		  while(neighbour.getItems().size()>0){
			  neighbour.getItems().get(0).left.setParent(node);
			  neighbour.getItems().get(0).right.setParent(node);
			  node.getItems().add(neighbour.getItems().remove(0));
		                                      }
		  sameLevel.get(level).remove(neighbour);
	                           }
	  else {
		  Node RightMostChildOfNeighbour = neighbour.getItems().get(neighbour.getItems().size()-1).right;
		  int index = sameLevel.get(level-1).indexOf(RightMostChildOfNeighbour);
		  Node RightChild = sameLevel.get(level-1).get(index+1);
		  Object key = node.getParent().getItems().get(indexOfParentItem).key;
		  Item inserted = new Item(key,RightMostChildOfNeighbour,RightChild);
		  inserted.right.setParent(neighbour);
		  neighbour.getItems().add(inserted);
		  while(node.getItems().size()>0){
			  node.getItems().get(0).left.setParent(neighbour);
			  node.getItems().get(0).right.setParent(neighbour);
			  neighbour.getItems().add(node.getItems().remove(0));
		                                 }
		   sameLevel.get(level).remove(node);
		   node = neighbour;
	       }
	  if(indexOfParentItem==-1) indexOfParentItem=0;
	  
	     node.getParent().getItems().remove(indexOfParentItem);
		
		 if(indexOfParentItem < node.getParent().getItems().size()) {
			 node.getParent().getItems().get(indexOfParentItem).left=node;
		                                                            }
		 
		 if(node.getParent()==this.root){
			 if(this.root.getItems().size()==0){
				 this.root=node;
				 this.root.setParent(null);
				 this.root.type="Inner";
				 sameLevel.remove(sameLevel.size()-1);
			                                   }
			 return;
		                               }
		 deleteInner(node.getParent(), level+1);
	}

	/////////////////////////////////////////
	public int ParentItemIndex (Node child,Node Parent){
		for(int i=0;i<Parent.getItems().size();i++){
			if(Parent.getItems().get(i).right==child)
				return i;
		}
		
			return -1;
	}
	
	public void updateKey(Node node,Object oldKey, Object newKey){
		if(node==null)
			return;
		
		for(int i=node.getItems().size()-1;i>=0;i--){
			if(this.compare(node.getItems().get(i).key, oldKey)==0){
				node.getItems().get(i).key= newKey;
				return;
			}
		}
		updateKey(node.getParent(),oldKey,newKey);
	}
	
     public int compare(Object firstKey, Object secondKey){
    	 if(firstKey.getClass().getSimpleName().equals("Integer"))
    		 return ((Integer)(firstKey))- ((Integer)(secondKey));
    	 
    	 if(firstKey.getClass().getSimpleName().equals("Double"))
    		 return ((Double)(firstKey)).compareTo(((Double)(secondKey)));
    	
    		 return ((String)(firstKey)).compareTo(((String)(secondKey)));
     }
	/////////////////////////
	
	public void printSameLevel(){
		for(int i=sameLevel.size()-1;i>=0;i--){
			System.out.print("Level "+ i +" : " + sameLevel.get(i) +"\n");
		}
	}

public static void main(String[] args) {
	BPlusTree x = new BPlusTree(3);
	
	x.insert(20.7,200,2000);
	
	x.insert(80.9,800,8000);
	x.insert(40.5,400,4000);
	x.insert(100.7,1000,1000);
	
	x.insert(90.8,900,9000);
	x.insert(70.7,700,7000);
	x.insert(75.0,750,7500);
	x.insert(60.2,600,6000);
	x.insert(63.5,630,6300);
	x.insert(110.1,1100,11000);
	x.insert(120.5,1200,12000);
	x.insert(105.6,1050,10500);
	x.insert(106.9,1060,10600);
	x.insert(130.0,1300,13000);
	x.insert(140.1,1400,14000);
	x.insert(81.7,810,8100);
	x.insert(82.8,820,8200);
	x.insert(83.9,830,8300);
	x.insert(84.8,840,8400);
	x.insert(71.5,710,7100);
	x.insert(72.7,720,7200);
	x.insert(73.6,730,7300);
	x.insert(74.4,740,7400);
	x.insert(85.8,850,8500);
	x.insert(86.1,860,8600);
	x.insert(87.9,870,8700);
	x.insert(88.2,880,8800);
	x.insert(21.3,210,2100);
	x.insert(22.9,220,2200);
	x.insert(23.1,230,2300);
	x.insert(24.2,240,2400);
	x.insert(91.5,910,9100);
	x.insert(23.1, 1, 1);
	x.insert(23.1, 2, 2);
	x.insert(23.1, 3, 3);
	x.insert(23.1, 4, 4);
	x.insert(23.1,5,5);
	
	//x.printSameLevel();
	//System.out.println();
	//System.out.println(x.delete(130));
	//System.out.println(x.delete(110));
	System.out.println(x.delete(23.1));
	System.out.println(x.delete(23.1));
	System.out.println(x.delete(23.1));
	System.out.println(x.delete(23.1));
	System.out.println(x.delete(23.1));
	x.printSameLevel();
	System.out.println(x.delete(23.1));
	
	
	x.printSameLevel();
}
 
}

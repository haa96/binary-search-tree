import java.io.*;
import java.util.*;

///////////////////////////////////////////////////////////////////////////////
class BSTNode<T>
{	T key;
	BSTNode<T> left,right;
	BSTNode( T key, BSTNode<T> left, BSTNode<T> right )
	{	this.key = key;
		this.left = left;
		this.right = right;
	}
}
///////////////////////////////////////////////////////////////////////////////////////
class Queue<T>
{	LinkedList<BSTNode<T>> queue;
	Queue() { queue =  new LinkedList<BSTNode<T>>(); }
	boolean empty() { return queue.size() == 0; }
	void enqueue( BSTNode<T>  node ) { queue.addLast( node ); }
	BSTNode<T>  dequeue() { return queue.removeFirst(); }
	// THROWS NO SUCH ELEMENT EXCEPTION IF Q EMPTY
}
////////////////////////////////////////////////////////////////////////////////
class BSTreeP7<T>
{
	private BSTNode<T> root;
	private boolean addAttemptWasDupe=false;
	@SuppressWarnings("unchecked")
	public BSTreeP7( String infileName ) throws Exception
	{
		root=null;
		Scanner infile = new Scanner( new File( infileName ) );
		while ( infile.hasNext() )
			add( (T) infile.next() ); // THIS CAST RPODUCES THE WARNING
		infile.close();
	}

	// DUPES BOUNCE OFF & RETURN FALSE ELSE INCR COUNT & RETURN TRUE
	@SuppressWarnings("unchecked")
	public boolean add( T key )
	{	addAttemptWasDupe=false;
		root = addHelper( this.root, key );
		return !addAttemptWasDupe;
	}
	@SuppressWarnings("unchecked")
	private BSTNode<T> addHelper( BSTNode<T> root, T key )
	{
		if (root == null) return new BSTNode<T>(key,null,null);
		int comp = ((Comparable)key).compareTo( root.key );
		if ( comp == 0 )
			{ addAttemptWasDupe=true; return root; }
		else if (comp < 0)
			root.left = addHelper( root.left, key );
		else
			root.right = addHelper( root.right, key );

		return root;
    } // END addHelper




	//////////////////////////////////////////////////////////////////////////////////////
	// # # # #   WRITE THE REMOVE METHOD AND ALL HELPERS / SUPPORTING METHODS   # # # # # 
	public int countNodes() // DYNAMIC COUNT ON THE FLY TRAVERSES TREE
	{
		return countNodes( this.root );
	}
	private int countNodes( BSTNode<T> root )
	{
		if (root==null) return 0;
		return 1 + countNodes( root.left ) + countNodes( root.right );
	}

	// INORDER TRAVERSAL REQUIRES RECURSION
	public void printInOrder()
	{
		printInOrder( this.root );
		System.out.println();
	}
	private void printInOrder( BSTNode<T> root )
	{
		if (root == null) return;
		printInOrder( root.left );
		System.out.print( root.key + " " );
		printInOrder( root.right );
	}

	// PRE ORDER TRAVERSAL REQUIRES RECURSION
	public void printPreOrder()
	{	printPreOrder( this.root );
		System.out.println();
	}
	private void printPreOrder( BSTNode<T> root )
	{	if (root == null) return;
		System.out.print( root.key + " " );
		printPreOrder( root.left );
		printPreOrder( root.right );
	}

	// POST ORDER TRAVERSAL REQUIRES RECURSION
	public void printPostOrder()
	{	printPostOrder( this.root );
		System.out.println();
	}
	private void printPostOrder( BSTNode<T> root )
	{	if (root == null) return;
		printPostOrder( root.left );
		printPostOrder( root.right );
		System.out.print( root.key + " " );
	}

	public void printLevelOrder()
	{	if (this.root == null) return;
		Queue<T> q = new Queue<T>();
		q.enqueue( this.root ); // this. just for emphasis/clarity
		while ( !q.empty() )
		{	BSTNode<T> n = q.dequeue();
			System.out.print( n.key + " " );
			if ( n.left  != null ) q.enqueue( n.left );
			if ( n.right != null ) q.enqueue( n.right );
		}
		System.out.println();
	}

	public int countLevels()
	{
		return countLevels( root ); 
	}
	private int countLevels( BSTNode root)
	{
		if (root==null) return 0;
		return 1 + Math.max( countLevels(root.left), countLevels(root.right) );
	}

	public int[] calcLevelCounts()
	{
		int levelCounts[] = new int[countLevels()];
		calcLevelCounts( root, levelCounts, 0 );
		return levelCounts;
	}
	private void calcLevelCounts( BSTNode root, int levelCounts[], int level )
	{
		if (root==null)return;
		++levelCounts[level];
		calcLevelCounts( root.left, levelCounts, level+1 );
		calcLevelCounts( root.right, levelCounts, level+1 );
	}

	// return true only if it finds/removes the node
	public boolean remove( T key2remove )
	{
		return removeHelper(root, key2remove);
	}
	
	private boolean removeHelper (BSTNode<T> root, T deadKey)
	{
		if (root == null) return false;
		BSTNode<T> deadsParent = findDeadParent(root, deadKey);
		BSTNode<T> deadNode = null;

		// ROOT OF ROOTS SPECIAL CASE:
		if (deadsParent == null && root.key.equals(deadKey))
		{
			if (root.left != null && root.right == null) 
			{
				this.root = root.left;
				return true;
			} 
			else if(root.right != null && root.left == null) 
			{ 
				this.root = root.right;
				return true;
			} 
			else if(root.right != null && root.left != null) 
			{
				T key = findPreds(this.root);
				T predsKey = key;
				removeHelper(this.root, predsKey);
				this.root.key = predsKey;
				return true;
			} 
			else 
			{
				this.root = null;
				return true;
			}

		} 
		else if (deadsParent == null) 
		{
			return false;
		}
		
		if (deadsParent.left != null && deadsParent.left.key.equals(deadKey))
		{
			deadNode = deadsParent.left;
		}
		else if (deadsParent.right.key.equals(deadKey))
		{
			deadNode = deadsParent.right;
		}
		// LEAF CASE
		if (deadNode.left == null && deadNode.right == null)
		{
			
			if (deadNode == this.root)
			{
				root = null;
				return true;
			}
			if (deadsParent.left == deadNode)
			{
				deadsParent.left = null;
				return true;
			}
			else
			{
				deadsParent.right = null;
				return true;
			}
		}

		//HAS 1 CHILD
		if (deadNode.left == null)
		{
			if (deadsParent.left == deadNode)
			{
				deadsParent.left = deadNode.right;
				return true;
			}
			else
			{
				deadsParent.right = deadNode.right;
				return true;
			}
		}
		if (deadNode.right == null)
		{
			if (deadsParent.left == deadNode)
			{
				deadsParent.left = deadNode.left;
				return true;
			}
			else
			{
				deadsParent.right = deadNode.left;
				return true;
			}
		}
		// HAS 2 CHILDREN
		if (deadNode.left != null && deadNode.right != null)
		{
			T key = findPreds(deadNode);
			T predsKey = key;
			removeHelper(this.root, predsKey);
			deadNode.key = predsKey;
			return true;
		}
		
		return false;
	}
	private BSTNode<T> findDeadParent (BSTNode<T>root, T deadKey)
	{
		if (root == null) return null;
		if (root.left == null && root.right == null) return null;
		if (root.left != null && root.left.key.equals(deadKey))	return root;
		if (root.right != null && root.right.key.equals(deadKey))	return root;
		BSTNode<T> nodeL = findDeadParent(root.left, deadKey);
		if (nodeL != null)	return nodeL;
		BSTNode<T> nodeR = findDeadParent(root.right, deadKey);
		if (nodeR != null)	return nodeR;
		
		return null;
		/*int temp =((Comparable)key2r).compareTo(root.key);
  		if(temp<0) return findDeadParent(root.left, key2r);
  		return findDeadParent(root.right, key2r);*/
	}
	private T findPreds(BSTNode<T> node)
	{
		BSTNode<T> curr = node.left;
		while (curr.right != null)
		{
			curr = curr.right;
		}
		return curr.key;
	}	
} // END BSTREEP7 CLASS

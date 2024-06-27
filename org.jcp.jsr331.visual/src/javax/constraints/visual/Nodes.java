package javax.constraints.visual;

import java.util.Vector;

public class Nodes {
	
	Vector<Node> nodes;
	int currentId;
	
	public Nodes() {
		nodes = new Vector<Node>();
		currentId = -1;
	}
	
	Node addNode() {
		int parentId = 0;
		if (!nodes.isEmpty())
			parentId = nodes.lastElement().getId();
		currentId++;
		Node node = new Node(currentId,parentId);
		nodes.add(node);
		return node;
	}
	
	int incrCurrentId() {
		currentId++;
		return currentId;
	}
	
	int getCurrentId() {
		return currentId;
	}

	void removeNode() {
		if (!nodes.isEmpty())
			nodes.remove(nodes.size()-1);
	}
	
	public Node getCurrent() {
		return nodes.lastElement();
	}
	
	boolean isEmpty() {
		return nodes.isEmpty();
	}
	
	public class Node {
		int id;
		int parentId;

		public Node(int id, int parentId) {
			this.id = id;
			this.parentId = parentId;
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}
		
	}

}

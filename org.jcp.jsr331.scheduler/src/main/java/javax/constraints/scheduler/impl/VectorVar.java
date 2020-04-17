//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import javax.constraints.Var;

public final class VectorVar implements java.io.Serializable, Cloneable {
	
	static final int VECTOR_INITIAL_SIZE = 5;

	private Var[] elements;

	private int size;

	public VectorVar() {
		this(VECTOR_INITIAL_SIZE);
	}

	public Var[] elements() {
		return elements;
	}

	public VectorVar(Var[] c) {
		this(c, 0, c.length - 1);
	}

	public VectorVar(Var[] c, int fromIndex, int toIndex) {
		this(Math.max(0, toIndex - fromIndex + 1));
		if (size > 0)
			System.arraycopy(elements, fromIndex, elements, 0, size);
	}

	public VectorVar(int capacity) {
		size = 0;
		if (capacity == 0)
			capacity = VECTOR_INITIAL_SIZE;
		elements = new Var[capacity];
	}

	public final boolean isEmpty() {
		return size == 0;
	}

	public int size() {
		return size;
	}

	public Var elementAt(int i) {
		return elements[i];
	}

	public final void add(Var element) {
		if (size == elements.length)
			grow();

		elements[size++] = element;
	}

	public final void addElement(Var element) {
		if (size == elements.length)
			grow();

		elements[size++] = element;
	}

	//public VectorVar clone() {  // does not work with 1.4
	public Object clone() {
		try {
			VectorVar v = (VectorVar) super.clone();
			v.elements = (Var[]) elements.clone();
			return v;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	public void insertElementAt(Var element, int index) {
		if (size == elements.length)
			grow();

		System.arraycopy(elements, index, elements, index + 1, size - index);
		elements[index] = element;
		size++;
	}

	public final Var removeLast() {
		return elements[--size];
	}

	public final Var peek() {
		return elements[size - 1];
	}

	public void clear() {
		size = 0;
		elements = new Var[elements.length];
	}

	public Var firstElement() {
		return elements[0];
	}

	public Var lastElement() {
		return elements[size - 1];
	}

	public void removeElementAt(int index) {
		int j = size - index - 1;
		if (j > 0) {
			System.arraycopy(elements, index + 1, elements, index, j);
		}
		size--;
		elements[size] = null;
	}

	public boolean removeElement(Var element) {
		int i = indexOf(element);
		if (i >= 0) {
			removeElementAt(i);
			return true;
		}
		return false;
	}

	public int indexOf(Var element) {
		if (element == null) {
			for (int i = 0; i < size; i++)
				if (elements[i] == null)
					return i;
		} else {
			for (int i = 0; i < size; i++)
				if (element.equals(elements[i]))
					return i;
		}
		return -1;
	}

	void grow() {
		Var[] old = elements;

		elements = new Var[elements.length * 2];
		System.arraycopy(old, 0, elements, 0, size);
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");
		int maxIndex = size - 1;
		for (int i = 0; i <= maxIndex; i++) {
			buf.append(String.valueOf(elements[i]));
			if (i < maxIndex)
				buf.append(", ");
		}
		buf.append("]");
		return buf.toString();
	}

	/**
	 * Returns an array containing all of the elements in this Vector in the
	 * correct order.
	 * 
	 * @see java.util.Vector#toArray()
	 */
	public Var[] toArray() {
		Var[] result = new Var[size];
		System.arraycopy(elements, 0, result, 0, size);
		return result;
	}

}

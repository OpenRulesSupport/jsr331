package javax.constraints.linear.impl;

public class XmlVar {

//	  <variable name="take-0" index="0" status="LL" value="0" reducedCost="88.8958333333333"/>
	String name;
	int index;
	double value;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "XmlVar [name=" + name + ", index=" + index + ", value="
				+ value + "]";
	}

}

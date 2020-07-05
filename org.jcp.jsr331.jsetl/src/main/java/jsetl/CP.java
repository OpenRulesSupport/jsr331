package jsetl;

import jsetl.exception.NotInitLObjectException;

import java.util.ArrayList;

public class CP extends LRel implements Cloneable, Visitable {
    
    /******************** DATA MEMBERS ********************/

    private LSet setA;
    private LSet setB;
    
    /******************** CONSTRUCTORS ********************/
    
    public CP() {
        super();
    }
             
    public CP(String n) {
        super(n);
    }

    public CP(LSet a, LSet b) {
        super();
        this.setA = a;
        this.setB = b;
    }
    
    public CP(String n, LSet a, LSet b) {
        this(a,b);
        this.name = n;
    }
    
    public CP(CP cp) {
        super();
        this.initialized = true;
        this.equ  = cp;
    }
    
    public CP(String n, CP cp)  {
        this(cp);
        this.name = n;
    }

    /******************** GENERAL METHODS ********************/
    
    @Override
    public CP clone() {    
        //return (CP)super.visit();
        if(this.equ == null) {
            CP cp = new CP(this.setA, this.setB);
            cp.initialized = this.initialized;
            cp.name = this.name;
            cp.equ  = null;
            return cp;
        }
        else return (CP)this.getEqu().clone();
    }
    
    public boolean equals(CP cp) {
        return (this.setA.equals(cp.setA) && this.setB.equals(cp.setB));
 
    }
       
    @Override
    public int getSize() {
        return (setA.getSize()*setB.getSize());
    }

    @Override
    public boolean isEmpty() {
        return setA.isEmpty() || setB.isEmpty(); 
    }
    
    @Override
    public boolean isGround() {
        return setA.isGround() && setB.isGround();
    }
    
    @Override
    protected boolean isInitialized() {
        return setA.isInitialized() && setB.isInitialized();
    }
    
    @Override
    protected boolean isVariable() {
        return setA.isVariable() || setB.isVariable();
    }
 
    public CP setName(String n) {
        return (CP)super.setName(n);  
    }

    public LSet expand() {
        if(!this.isInitialized())
            throw new NotInitLObjectException();
        LSet r = new LSet();
        r.setInitialized(true);
        r.setName(name);
        r.rest = LSet.empty();
        ArrayList<LPair> arrayList = new ArrayList<>();
        r.elements = arrayList;
        for(Object setAElement : setA)
            for(Object setBElement : setB)
                arrayList.add(new LPair(setAElement, setBElement));

        return r;
    }
    
    public String toString() {
        if (this.equ == null)
            return setA + " x " + setB;
        else return this.equ.toString();
    }
    
    public void output() {
        System.out.print(this.name + " = " + this.toString() + '\n');
        return;
    }

    public Object accept(Visitor visitor){
        return visitor.visit(this);
    }
    public LSet getFirstSet() {
        return setA;
    }
    
    public LSet getSecondSet() {
        return setB;
    }
    
    protected void setFirstSet(LSet a) {
        setA = a;
        return;
    }
    
    protected void setSecondSet(LSet b) {
        setB = b;
        return;
    }
    
}

package jsetl;

import jsetl.annotation.NotNull;
import jsetl.annotation.Nullable;
import jsetl.exception.NotValidDomainException;

import java.util.*;

/**
 * Objects of this class are finite sets over integers. These sets have a domain associated,
 * which is a set interval of which they must be an element. Sets of this type and their constraints
 * are solved by means similar to those used for finite domains.
 * @see SetInterval
 */
public class SetLVar extends LVar 
                   implements Cloneable, Visitable {

    ///////////////////////////////////////////////////////////////
    //////////////// STATIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs and returns a new integer set variable which is the singleton {@code {element}}.
     * @param element the only element of the singleton.
     * @return the constructed singleton.
     */
    public static @NotNull SetLVar
    singleton(@NotNull IntLVar element) {
        Objects.requireNonNull(element);

        SetLVar z = new SetLVar();
        z.constraint.addAll(z.eq(element));
        z.card.constraint.addAll(z.constraint);
        assert z != null;
        return z;
    }

    /**
     * Returns a labeling constraint for each {@code SetLVar} in {@code setLVars} with default labeling options.
     * @param setLVars list containing the set logical variables to label. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code setLVars} contains {@code null} values.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull List<SetLVar> setLVars) {
        Objects.requireNonNull(setLVars);
        if(setLVars.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        return new ConstraintClass(Environment.labelCode, new ArrayList<>(setLVars),
                new LabelingOptions());
    }

    /**
     * Returns a labeling constraint for each {@code SetLVar} in {@code setLVars} with given labeling options.
     * @param labelingOptions labeling options to use.
     * @param setLVars list containing the set logical variables to label. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code setLVars} contains {@code null} values.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions, @NotNull List<SetLVar> setLVars) {
        Objects.requireNonNull(setLVars);
        if(setLVars.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");
        Objects.requireNonNull(labelingOptions);
        return new ConstraintClass(Environment.labelCode, new ArrayList<>(setLVars), labelingOptions);
    }

    /**
     * Returns a labeling constraint for each {@code SetLVar} in {@code setLVars} with default labeling options.
     * @param setLVars array containing the set logical variables to label. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code setLVars} contains {@code null} values.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull SetLVar... setLVars) {
        Objects.requireNonNull(setLVars);
        if(Arrays.stream(setLVars).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");
        LabelingOptions labelingOptions = new LabelingOptions();
        ConstraintClass result = label(labelingOptions, setLVars);
        assert result != null;
        return result;
    }

    /**
     * Returns a labeling constraint for each {@code SetLVar} in {@code setLVars} with given labeling options.
     * @param labelingOptions labeling options to use.
     * @param setLVars array containing the set logical variables to label. It must not contain {@code null} values.
     * @return the constructed constraint.
     * @throws NullPointerException if {@code setLVars} contains {@code null} values.
     */
    public static @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions, @NotNull SetLVar... setLVars) {
        Objects.requireNonNull(setLVars);
        if(Arrays.stream(setLVars).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");
        Objects.requireNonNull(labelingOptions);

        ConstraintClass result = label(labelingOptions, Arrays.asList(setLVars));
        assert result != null;
        return result;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// DATA MEMBERS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Variable domain.
     */
    private SetInterval domain;

    /**
     * {@code IntLVar} that represents the cardinality of the set.
     */
    private IntLVar card;
    
    /**
     * Variable constraint; empty if no constraint is associated.
     */
    protected ConstraintClass constraint;


    ///////////////////////////////////////////////////////////////
    //////////////// CONSTRUCTORS /////////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Constructs an unspecified logical set with default name.
     */
    public 
    SetLVar() { 
        this(defaultName());
    }

    /**
     * Constructs an unspecified logical set with given name.
     * @param name name of the set.
     */
    public 
    SetLVar(@NotNull String name) {
        super(name);
        assert name != null;
        domain = SetInterval.universe();
        card = new IntLVar(0, SetInterval.SUP.size());
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + this.name;
    }

    /**
     * Constructs a new logical set with the given value and default name.
     * @param multiInterval the value of this logical set.
     */
    public
    SetLVar(@NotNull MultiInterval multiInterval)
    throws NotValidDomainException {
        this(defaultName(), multiInterval);
        assert multiInterval != null;
    }

    /**
     * Constructs a new logical set with the given value and name.
     * @param name name of the set.
     * @param multiInterval the value of this logical set.
     */
    public
    SetLVar(@NotNull String name, @NotNull MultiInterval multiInterval)
    throws NotValidDomainException {
        super(name);
        assert name != null;
        assert multiInterval != null;
        SetInterval dom = new SetInterval(multiInterval);
        initialized = true;
        val = multiInterval;
        domain = dom;
        if (dom.isEmpty())
            throw new NotValidDomainException();
        card = new IntLVar(domain.getGlb().size());
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + name;
    }

    /**
     * Constructs a new logical set with the given value and default name.
     * @param integers the value of this logical set.
     */
    public 
    SetLVar(@NotNull Set<Integer> integers)
    throws NotValidDomainException {
        this(defaultName(), integers);
        assert integers != null;
        assert integers.stream().noneMatch(Objects::isNull);
    }

    /**
     * Constructs a new logical set with the given value and name.
     * @param name name of the set.
     * @param integers the value of this logical set.
     */
    public 
    SetLVar(@NotNull String name, @NotNull Set<Integer> integers)
    throws NotValidDomainException {
        super(name);
        assert name != null;
        Objects.requireNonNull(integers);
        if(integers.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        SetInterval dom = new SetInterval(new MultiInterval(integers));
        initialized = true;
        val = integers;
        domain = dom;
        if (dom.isEmpty())
            throw new NotValidDomainException();
        card = new IntLVar(domain.getGlb().size());
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + name;
    }

    /**
     * Constructs a copy of the parameter {@code setLVar} with default name.
     * Using this constructor is equivalent to creating a new unbound {@code SetLVar}
     * and posting and solving the constraint {@code this.eq(setLVar)}.
     * @param setLVar set to copy.
     */
    public 
    SetLVar(@NotNull SetLVar setLVar) {
        this(defaultName(), setLVar);

    }

    /**
     * Constructs a copy of the parameter {@code setLVar} with given name.
     * Using this constructor is equivalent to creating a new unbound {@code SetLVar}
     * and posting and solving the constraint {@code this.eq(setLVar)}.
     * @param name name of the set.
     * @param setLVar set to copy.
     */
    public 
    SetLVar(@NotNull String name, @NotNull SetLVar setLVar) {
        super(name, setLVar);
        assert setLVar != null;
        assert name != null;
        domain = setLVar.domain;
        card = setLVar.card;
        constraint = setLVar.constraint;
    }

    /**
     * Constructs glb logical variable set with domain {@code [glb,lub]} with default name.
     * @param glb greatest lower bound of the domain set interval.
     * @param lub least upper bound of the domain set interval.
     * @throws NotValidDomainException if the domain {@code [glb,lub]} is empty.
     */
    public 
    SetLVar(@NotNull MultiInterval glb, @NotNull MultiInterval lub)
    throws NotValidDomainException {
        this(defaultName(), glb, lub);
        assert glb != null;
        assert lub != null;
    }

    /**
     * Constructs glb logical variable set with domain {@code [glb,lub]} with given name.
     * @param name name of the s
     * @param glb greatest lower bound of the domain set interval
     * @param lub least upper bound of the domain set interval
     * @throws NotValidDomainException if the domain {@code [glb,lub]} is empty.
     */
    public
    SetLVar(@NotNull String name, @NotNull MultiInterval glb, @NotNull MultiInterval lub)
    throws NotValidDomainException {
        super(name);
        assert name != null;
        assert glb != null;
        assert lub != null;
        SetInterval dom = new SetInterval(glb, lub);
        domain = dom;
        if (dom.isEmpty())
            throw new NotValidDomainException();
        if (dom.isSingleton()) {
            this.initialized = true;
            this.val = dom.getGlb();
        }
        card = new IntLVar(dom.getGlb().size(), dom.getLub().size());
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + this.name;
    }

    /**
     * Constructs glb logical variable set with domain {@code [glb,lub]} with default name.
     * @param glb greatest lower bound of the domain set interval.
     * @param lub least upper bound of the domain set interval.
     * @throws NotValidDomainException if the domain {@code [glb,lub]} is empty.
     */
    public
    SetLVar(@NotNull Set<Integer> glb, @NotNull Set<Integer> lub)
    throws NotValidDomainException {
        this(defaultName(), glb, lub);
        assert glb != null;
        assert lub != null;
    }

    /**
     * Constructs glb logical variable set with domain {@code [glb,lub]} with given name.
     * @param name name of the s
     * @param glb greatest lower bound of the domain set interval
     * @param lub least upper bound of the domain set interval
     * @throws NotValidDomainException if the domain {@code [glb,lub]} is empty.
     */
    public
    SetLVar(@NotNull String name, @NotNull Set<Integer> glb, @NotNull Set<Integer> lub)
    throws NotValidDomainException {
        super(name);
        assert name != null;
        assert glb != null;
        assert lub != null;
        SetInterval dom = new SetInterval(new MultiInterval(glb),
                new MultiInterval(lub));
        domain = dom;

        if (dom.isEmpty())
            throw new NotValidDomainException();
        if (dom.isSingleton()) {
            this.initialized = true;
            this.val = dom.getGlb();
        }
        card = new IntLVar(dom.getGlb().size(), dom.getLub().size());
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + this.name;
    }

    /**
     * Constructs a set logical variable with the given domain and default name.
     * @param domain the domain of the set.
     * @throws NotValidDomainException if the domain {@code set} is empty.
     */
    public
    SetLVar(@NotNull SetInterval domain)
    throws NotValidDomainException {
        this(defaultName(), domain);
        assert domain != null;
    }

    /**
     * Constructs a set logical variable with the given domain and name.
     * @param name name of the set.
     * @param domain the domain of the set.
     * @throws NotValidDomainException if the domain {@code set} is empty.
     */
    public
    SetLVar(@NotNull String name, @NotNull SetInterval domain)
    throws NotValidDomainException {
        super(name);
        assert name != null;
        assert domain != null;
        this.domain = domain;

        if (domain.isEmpty())
            throw new NotValidDomainException();
        if (domain.isSingleton()) {
            this.initialized = true;
            this.val = domain.getGlb();
        }
        card = new IntLVar(domain.getGlb().size(), domain.getLub().size());
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + this.name;
    }

    /**
     * Constructs a domain logical variable with the given domain, cardinality and with default name.
     * @param domain the domain of the domain.
     * @param cardinality cardinality of the domain.
     * @throws NotValidDomainException if the domain {@code domain} is empty.
     */
    public
    SetLVar(@NotNull SetInterval domain, @NotNull Integer cardinality)
    throws NotValidDomainException {
        this(defaultName(), domain, cardinality);
        assert domain != null;
        assert cardinality != null;

    }

    /**
     * Constructs a set logical variable with the given domain, cardinality and name.
     * @param name name of the set.
     * @param domain the domain of the set.
     * @param cardinality cardinality of the set.
     * @throws NotValidDomainException if the domain {@code set} is empty.
     */
    public
    SetLVar(String name, SetInterval domain, Integer cardinality) {
        this(name, domain, new MultiInterval(cardinality));
        assert domain != null;
        assert cardinality != null;
        card.name = "#" + this.name;
    }

    /**
     * Constructs a domain logical variable with the given domain, cardinality domain and with default name.
     * @param domain the domain of the domain
     * @param cardinalityDomain domain of the cardinality
     * @throws NotValidDomainException if the domain {@code domain} or the domain of cardinality are empty
     */
    public
    SetLVar(@NotNull SetInterval domain, @NotNull MultiInterval cardinalityDomain)
    throws NotValidDomainException {
        this(defaultName(), domain, cardinalityDomain);
        assert domain != null;
        assert cardinalityDomain != null;
    }

    /**
     * Constructs a set logical variable with the given domain, cardinality domain and name.
     * @param name name of the set.
     * @param domain the domain of the set.
     * @param cardinalityDomain domain of the cardinality.
     * @throws NotValidDomainException if the domain {@code set} or the domain of cardinality are empty.
     */
    public
    SetLVar(@NotNull String name, @NotNull SetInterval domain, @NotNull MultiInterval cardinalityDomain)
    throws NotValidDomainException {
        super(name);
        assert name != null;
        assert domain != null;
        assert cardinalityDomain != null;
        this.domain = domain;

        if (domain.isEmpty())
            throw new NotValidDomainException();
        if (domain.isSingleton()) {
            this.initialized = true;
            this.val = domain.getGlb();
        }
        MultiInterval dom = cardinalityDomain.intersect(
                new MultiInterval(domain.getGlb().size(), domain.getLub().size()));
        if (dom.isEmpty())
            throw new NotValidDomainException();
        if (dom.isSingleton()) {
            MultiInterval glb = domain.getGlb();
            MultiInterval lub = domain.getLub();
            int value = dom.getGlb();
            if (value == glb.size()) {
                initialized = true;
                val = glb;
            }
            else if (value == lub.size()) {
                initialized = true;
                val = lub;
            }
        }
        card = new IntLVar(dom);
        card.constraint.add(new AConstraint(Environment.sizeCode, this, card));
        constraint = new ConstraintClass();
        card.name = "#" + this.name;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PUBLIC METHODS ///////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Returns the value of the set (as a multi-interval).
     * @return the value of the set.
     */
    @Override
    public @Nullable MultiInterval
    getValue() {
        return (MultiInterval) super.getValue();
    }

    /**
     * Sets the name of the set.
     * @param name new name of the set.
     * @return {@code this} set with its name changed.
     */
    @Override
    public @NotNull SetLVar
    setName(@NotNull String name) {
        super.setName(name);
        assert name != null;
        card.name = "#" + name;
        return this;
    }

    /**
     * Gets the domain of this logical set variable.
     * @return the domain of this logical set variable.
     */
    public @NotNull SetInterval
    getDomain() {
        SetInterval domain = getEndOfEquChain().domain;
        assert domain != null;
        return domain;
    }

    /**
     * Gets the constraint associated with this logical set variable.
     * @return the constraint associated with this logical set variable.
     */
    public @NotNull
    ConstraintClass getConstraint(){
        ConstraintClass result = this.constraint.and(this.card.getConstraint());
        assert result != null;
        return result;
    }

    /**
     * Needed for the visitor pattern.
     * @param visitor the visitor that wants to visit the object.
     * @return the result of {@code visitor.visit(this)}.
     */
    @Override
    public @Nullable Object accept(@NotNull Visitor visitor){
        assert visitor != null;
        return visitor.visit(this);
    }


    /**
     * Tests whether {@code this} is equal to {@code other}.
     * @param other other parameter of the equality test.
     * @return {@code true} if {@code this} and {@code other} are equal, {@code false} otherwise.
     */
    @Override
    public boolean
    equals(@Nullable Object other) {
        if (this == other)
            return true;
        else if(other instanceof SetLVar)
            return super.equals(other) && (this.getDomain()).equals(((SetLVar)other).getDomain());
        else
            return false;
    }

    /**
     * Constructs and returns a bit-by-bit clone of {@code this}.
     * @return a bit-by-bit clone of {@code this}.
     */
    @Override
    public SetLVar
    clone() {
        SetLVar original = getEndOfEquChain();
        SetLVar lvarClone = new SetLVar();
        lvarClone.initialized = original.initialized;
        lvarClone.name = original.name;
        lvarClone.equ  = null;
        lvarClone.val = original.val;
        lvarClone.card = original.card;
        return lvarClone;

    }

    /**
     * Outputs info about {@code this} to standard output.
     * The output is of the form
     * "name = value -- Domain : domain -- Size: cardinality_size[ -- ConstraintClass: constraint]\n"
     */
    public void
    output() {
        super.outputLine();
        if (!isBound()) {
            System.out.print(" -- Domain: " + getDomain());
            System.out.print(" -- Size: " + card.getDomain());
            if (!this.constraint.isEmpty()) {
                System.out.print(" -- ConstraintClass: ");
                System.out.print(this.constraint);
            }
        }
        System.out.println();
    }

    /**
     * Creates and returns an integer logical variable representing the set cardinality.
     * @return an integer logical variable representing the set cardinality.
     */
    public @NotNull IntLVar
    card() {
        IntLVar card = getEndOfEquChain().card;
        assert card != null;
        return card;
    }

    /**
     * Returns glb new constraint which demands that the domain of {@code this} is contained in the set interval {@code [glb,lub]}.
     * @param glb greatest lower bound of the set interval.
     * @param lub least upper bound of the set interval.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    dom(@NotNull MultiInterval glb, @NotNull MultiInterval lub) {
        Objects.requireNonNull(glb);
        Objects.requireNonNull(lub);
        ConstraintClass result = dom(new SetInterval(glb, lub));
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that the domain of {@code this} is contained in the domain interval {@code domain}.
     * @param domain domain interval.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    dom(@NotNull SetInterval domain) {
        Objects.requireNonNull(domain);
        ConstraintClass result = genCons(Environment.domCode, domain);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this == setLVar}.
     * @param setLVar logical set variable.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);

        constraint.add(new AConstraint(Environment.eqCode, card, setLVar.card));
        ConstraintClass result = genCons(Environment.eqCode, setLVar);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this == multiInterval}.
     * @param multiInterval multi-interval.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        ConstraintClass result = genCons(Environment.eqCode, multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this != setLVar}.
     * @param setLVar logical set variable.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    neq(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);
        ConstraintClass result = genCons(Environment.neqCode, setLVar);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this != multiInterval}.
     * @param multiInterval multi-interval.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    neq(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        ConstraintClass result = genCons(Environment.neqCode, multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Returns a new logical set variable representing the complement of {@code this}.
     * @return a new logical set variable representing the complement of {@code this}.
     */
    public @NotNull SetLVar
    compl() {
        SetLVar setLVar = new SetLVar(genExpr(Environment.complCode));
        constraint.addAll((card.sum(setLVar.card)).eq(SetInterval.SUP.size()));
        assert setLVar != null;
        return setLVar;
    }

    /**
     * Returns a new constraint which demands that {@code this} and {@code setLVar} are disjoint.
     * @param setLVar the other integer set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    disj(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);
        constraint.addAll(card.sum(setLVar.card).le(SetInterval.SUP.size()));
        ConstraintClass result = genCons(Environment.disjCode, setLVar);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this} and {@code multiInterval} are disjoint.
     * @param multiInterval the other integer set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    disj(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        ConstraintClass result = genCons(Environment.disjCode, multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this} is a strict subset of {@code setLVar}.
     * @param setLVar the other integer set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    strictSubset(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);
        constraint.add(new AConstraint(Environment.ltCode, card, setLVar.card));
        ConstraintClass result = genCons(Environment.subsetCode, setLVar);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this} is a strict subset of {@code multiInterval}.
     * @param multiInterval the other integer set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    strictSubset(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        constraint.add(new AConstraint(Environment.ltCode, card, multiInterval.size()));
        ConstraintClass result = genCons(Environment.subsetCode, multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this} is a subset of {@code setLVar}.
     * @param setLVar the other integer set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    subset(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);
        constraint.add(new AConstraint(Environment.leCode, card, setLVar.card));
        ConstraintClass result = genCons(Environment.subsetCode, setLVar);
        assert result != null;
        return result;
    }

    /**
     * Returns a new constraint which demands that {@code this} is a subset of {@code multiInterval}.
     * @param multiInterval the other integer set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    subset(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        ConstraintClass result = genCons(Environment.subsetCode, multiInterval);
        assert result != null;
        return result;
    }

    /**
     * Creates and returns a new integer set variable which is the intersection of {@code this} and {@code setLVar}.
     * @param setLVar the other argument of the intersection.
     * @return the constructed {@code SetLVar}, i.e. the intersection.
     */
    public @NotNull SetLVar
    intersect(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);

        SetLVar z = genExpr(Environment.intersCode, setLVar);
        z.constraint.addAll(z.subset(setLVar).add(z.subset(this)
                                .add(z.card.dom(intersSize(this, setLVar)))));
        assert setLVar != null;
        return z;
    }

    /**
     * Creates and returns a new integer set variable which is the intersection of {@code this} and {@code multiInterval}.
     * @param multiInterval the other argument of the intersection.
     * @return the constructed {@code SetLVar}, i.e. the intersection.
     */
    public @NotNull SetLVar
    intersect(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        SetLVar result = this.intersect(new SetLVar(multiInterval));
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a new integer set variable which is the result of the union of {@code this} and {@code setLVar}.
     * @param setLVar the other argument of union.
     * @return the constructed {@code SetLVar}, i.e. the union.
     */
    public @NotNull SetLVar
    union(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);
        SetLVar z = genExpr(Environment.unionCode, setLVar);
        z.constraint.addAll(this.subset(z).add(setLVar.subset(z))
                                   .add(z.card.le(this.card.sum(setLVar.card)))
                                   .add(z.card.dom(unionSize(this, setLVar))));
        assert z != null;
        return z;
    }

    /**
     * Constructs and returns a new integer set variable which is the result of the union of {@code this} and {@code multiInterval}.
     * @param multiInterval the other argument of union.
     * @return the constructed {@code SetLVar}, i.e. the union.
     */
    public @NotNull SetLVar
    union(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        SetLVar result = this.union(new SetLVar(multiInterval));
        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a constraint of the kind {@code this = {element}}.
     * @param element an integer logical variable.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull IntLVar element) {
        Objects.requireNonNull(element);
        ConstraintClass result;
        if (element.isBound())
            result = this.eq(new MultiInterval(element.getValue()));
        else {
            result = new ConstraintClass();
            result.add(element.in(this));
            result.add(card.eq(1));
        }

        assert result != null;
        return result;
    }

    /**
     * Constructs and returns a constraint of the kind {@code this = {x_1, x_2, ..., x_n}} where {@code intLVars}
     * is an array of integer logical variables {@code x_1, x_2, ..., x_n}.
     * @param intLVars an array of integer logical variables. None of its elements can be {@code null}.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull IntLVar[] intLVars) {
        Objects.requireNonNull(intLVars);
        if(Arrays.stream(intLVars).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        int n = intLVars.length;
        if (n == 0)
            return this.eq(new MultiInterval());
        ArrayList<SetLVar> fsvars = new ArrayList<SetLVar>();
        fsvars.add(new SetLVar(new MultiInterval()));
        ConstraintClass constraint = new ConstraintClass();
        MultiInterval set = new MultiInterval();
        int j = 0;
        for (int i = 1; i <= n; ++i) {
            IntLVar tmp = intLVars[i - 1];
            if (tmp.isBound())
                set.add(tmp.getValue());
            else {
                ++j;
                fsvars.add(new SetLVar());
                SetLVar s = new SetLVar("{" + tmp.getName() + "}");
                constraint = constraint.add(tmp.in(s)).add(s.card.eq(1))
                           .add(tmp.in(this))
                           .add(fsvars.get(j).eq(fsvars.get(j - 1).union(s)));
            }
        }
        if (j > 0) {
            if (!set.isEmpty())
                constraint = constraint.add(this.eq(fsvars.get(j).union(set)));
            else
                constraint = constraint.add(this.eq(fsvars.get(j)));
        }
        else
            constraint = constraint.add(this.eq(set));

        assert constraint != null;
        return constraint;
    }

    /**
     * Constructs and returns a constraint of the kind {@code this = {x_1, x_2, ..., x_n}} where {@code intLVars}
     * is a collection of integer logical variables {@code x_1, x_2, ..., x_n}.
     * @param intLVars a collection of integer logical variables.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull Collection<IntLVar> intLVars) {
        Objects.requireNonNull(intLVars);
        if(intLVars.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        ArrayList<SetLVar> vars = new ArrayList<>();
        vars.add(new SetLVar(new MultiInterval()));
        ConstraintClass constraint = new ConstraintClass();
        Iterator<IntLVar> it = intLVars.iterator();
        int i = 0;
        MultiInterval set = new MultiInterval();
        while (it.hasNext()) {
            IntLVar tmp = it.next();
            if (tmp.isBound())
                set.add(tmp.getValue());
            else {
                ++i;
                vars.add(new SetLVar());
                SetLVar s = new SetLVar("{" + tmp.getName() + "}");
                constraint = constraint.add(tmp.in(s)).add(s.card.eq(1))
                           .add(tmp.in(this))
                           .add(vars.get(i).eq(vars.get(i - 1).union(s)));
            }
        }
        if (i > 0) {
            if (!set.isEmpty())
                constraint = constraint.add(this.eq(vars.get(i).union(set)));
            else
                constraint = constraint.add(this.eq(vars.get(i)));
        }
        else
            constraint = constraint.add(this.eq(set));
        assert constraint != null;
        return constraint;
    }

    /**
     * Constructs and returns a constraint of the kind {@code this = {element | rest}}.
     * @param element one known element of the second set.
     * @param rest the rest of the second set.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull IntLVar element, @NotNull SetLVar rest) {
        Objects.requireNonNull(element);
        Objects.requireNonNull(rest);

        SetLVar tmp = new SetLVar();
        ConstraintClass constraint = tmp.eq(element);
        constraint.add(this.eq(tmp.union(rest))).add(element.in(this));
        assert constraint != null;
        return constraint;
    }

    /**
     * Constructs and returns a constraint of the kind {@code this = {x_1, x_2, ..., x_n | rest}} where {@code elements}
     * is a collection of integer logical variables {@code x_1, x_2, ..., x_n}.
     * @param elements a collection of integer logical variables. None of its elements can be {@code null}.
     * @param rest the rest of the second set in the equality.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull Collection<IntLVar> elements, @NotNull SetLVar rest) {
        Objects.requireNonNull(elements);
        if(elements.stream().anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        SetLVar tmp = new SetLVar();
        ConstraintClass constraint = tmp.eq(elements);
        constraint.add(this.eq(tmp.union(rest)));
        assert constraint != null;
        return constraint;
    }

    /**
     * Constructs and returns a constraint of the kind {@code this = {x_1, x_2, ..., x_n | R}} where {@code coll}
     * is an array of integer logical variables {@code x_1, x_2, ..., x_n}.
     * @param elements an array of integer logical variables. None of its elements can be {@code null}.
     * @param rest the rest of the second set in the equality.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    eq(@NotNull IntLVar[] elements, @NotNull SetLVar rest) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(rest);
        if(Arrays.stream(elements).anyMatch(Objects::isNull))
            throw new NullPointerException("NULL VALUES NOT ALLOWED");

        SetLVar tmp = new SetLVar();
        ConstraintClass constraint = tmp.eq(elements);
        constraint.add(this.eq(tmp.union(rest)));
        assert constraint != null;
        return constraint;
    }

    /**
     * Constructs and returns a new integer set logical variable which is equal to the difference {@code this - setLVar}.
     * @param setLVar second argument of the difference.
     * @return the constructed integer set logical variable, i.e. the difference.
     */
    public @NotNull SetLVar
    diff(@NotNull SetLVar setLVar) {
        Objects.requireNonNull(setLVar);

        SetLVar z = genExpr(Environment.diffCode, setLVar);
        z.constraint.addAll(z.subset(this).add(setLVar.disj(z))
                                   .add(z.card.ge(this.card.sub(setLVar.card)))
                                   .add(z.card.dom(diffSize(this, setLVar))));
        assert z != null;
        return z;
    }

    /**
     * Constructs and returns a new integer set logical variable which is equal to the difference {@code this - multiInterval}.
     * @param multiInterval second argument of the difference.
     * @return the constructed integer set logical variable, i.e. the difference.
     */
    public @NotNull SetLVar
    diff(@NotNull MultiInterval multiInterval) {
        Objects.requireNonNull(multiInterval);
        SetLVar result = diff(new SetLVar(multiInterval));
        assert result != null;
        return result;
    }

    /**
     * Returns a labeling constraint for this with default labeling options.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    label() {
        ConstraintClass result = genCons(Environment.labelCode, new LabelingOptions());
        assert result != null;
        return result;
    }

    /**
     * Returns a labeling constraint for this with given labeling options.
     * @param labelingOptions labeling options to use.
     * @return the constructed constraint.
     */
    public @NotNull
    ConstraintClass
    label(@NotNull LabelingOptions labelingOptions) {
        Objects.requireNonNull(labelingOptions);
        ConstraintClass result = genCons(Environment.labelCode, labelingOptions);
        assert result != null;
        return result;
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PROTECTED METHODS ////////////////////////////
    ///////////////////////////////////////////////////////////////

    /**
     * Sets the domain of this logical set variable.
     * @param domain new domain of the set.
     */
    protected void
    setDomain(@NotNull SetInterval domain) {
        assert domain != null;
        getEndOfEquChain().domain = domain;
    }

    /**
     * Follows the {@code equ} chain and returns its last node.
     * @return the last node of the equ chain.
     */
    protected @Nullable SetLVar getEndOfEquChain(){
        return (SetLVar) super.getEndOfEquChain();
    }


    ///////////////////////////////////////////////////////////////
    //////////////// PRIVATE METHODS //////////////////////////////
    ///////////////////////////////////////////////////////////////
    
    /**
     * Computes and returns the domain of the cardinality of the intersection of the two parameters.
     * @param x first argument of intersection.
     * @param y second argument of intersection.
     * @return the domain of the cardinality of the intersection of the two parameters.
     */
    private static @NotNull MultiInterval
    intersSize(@NotNull SetLVar x, @NotNull SetLVar y) {
        assert x != null;
        assert y != null;

        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        MultiInterval lubX = domX.getLub();
        MultiInterval lubY = domY.getLub();
        int a = (domX.getGlb().diff(lubY)).size();
        int b = (domY.getGlb().diff(lubX)).size();
        int u = lubX.union(lubY).size();
        java.util.LinkedList<Interval> listX =
                x.card.getDomain().getIntervals();
        java.util.LinkedList<Interval> listY =
                y.card.getDomain().getIntervals();
        java.util.LinkedList<Interval> result =
                new java.util.LinkedList<>();
        for (Interval aListX : listX)
            for (Interval aListY : listY) {
                Interval listXi = aListX;
                Interval listYj = aListY;
                int lb = listXi.getGlb() + listYj.getGlb() - u;
                int ub = Math.min(listXi.getLub() - a,
                        listYj.getLub() - b);
                result.add(new Interval(lb, ub));
            }
        return new MultiInterval(result);
    }

    /**
     * Computes and returns the domain of the cardinality of the union of {@code x} and {@code y}.
     * @param x first argument of union.
     * @param y second argument of union.
     * @return the domain of the cardinality of the union of {@code x} and {@code y}.
     */
    private static @NotNull MultiInterval
    unionSize(@NotNull SetLVar x, @NotNull SetLVar y) {
        assert x != null;
        assert y != null;

        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        MultiInterval lubX = domX.getLub();
        MultiInterval lubY = domY.getLub();
        int a = (domX.getGlb().diff(lubY)).size();
        int b = (domY.getGlb().diff(lubX)).size();
        int u = lubX.union(lubY).size();
        java.util.LinkedList<Interval> listX =
                x.card.getDomain().getIntervals();
        java.util.LinkedList<Interval> listY =
                y.card.getDomain().getIntervals();
        java.util.LinkedList<Interval> result =
                new java.util.LinkedList<Interval>();
        for (Interval aListX : listX)
            for (Interval aListY : listY) {
                Interval listXi = aListX;
                Interval listYj = aListY;
                int lb = Math.max(listXi.getGlb() + b,
                        listYj.getGlb() + a);
                int ub = Math.min(listXi.getLub() + listYj.getLub(), u);
                result.add(new Interval(lb, ub));
            }
        return new MultiInterval(result);
    }

    /**
     * Computes and returns the domain of the cardinality of the difference of {@code x} and {@code y}.
     * @param x first argument of difference.
     * @param y second argument of difference.
     * @return the domain of the cardinality of the difference of {@code x} and {@code y}.
     */
    private static @NotNull MultiInterval
    diffSize(@NotNull SetLVar x, @NotNull SetLVar y) {
        assert x != null;
        assert y != null;

        SetInterval domX = x.getDomain();
        SetInterval domY = y.getDomain();
        MultiInterval lubX = domX.getLub();
        MultiInterval lubY = domY.getLub();
        int a = (domX.getGlb().intersect(domY.getGlb())).size();
        int b = (domX.getLub().intersect(domY.getLub())).size();
        int u = lubX.union(lubY).size();
        java.util.LinkedList<Interval> listX =
                x.card.getDomain().getIntervals();
        java.util.LinkedList<Interval> listY =
                y.card.getDomain().getIntervals();
        java.util.LinkedList<Interval> result =
                new java.util.LinkedList<Interval>();
        for (Interval aListX : listX)
            for (Interval aListY : listY) {
                Interval listXi = aListX;
                Interval listYj = aListY;
                int lb = Math.max(listXi.getGlb() - listYj.getLub(),
                        listXi.getGlb() - b);
                int ub = Math.min(listXi.getLub() - a,
                        u - listYj.getGlb());
                result.add(new Interval(lb, ub));
            }
        return new MultiInterval(result);
    }

    /**
     * Constructs and returns a constraint of the kind represented by {@code code} which has
     * {@code this} as its first argument and {@code x} as its second argument.
     * The constraint conjunction created also contains the constraints associated to {@code this} or {@code x}.
     * @param code an integer code representing the kind of constraint to create.
     * @param x the second argument of the constraint.
     * @return the constructed constraint.
     */
    private @NotNull
    ConstraintClass
    genCons(int code, @NotNull SetLVar x) {
        assert x != null;
        ConstraintClass constraint = new ConstraintClass();
        constraint.add(new AConstraint(code, this, x));
        constraint.addAll(this.constraint);
        constraint.addAll(x.constraint);
        assert constraint != null;
        return constraint;
    }

    /**
     * Constructs and returns a constraint of the kind represented by {@code code} which has
     * {@code this} as its first argument and {@code x} as its second argument.
     * The constraint conjunction created also contains the constraints associated to {@code this} or {@code x}.
     * @param code an integer code representing the kind of constraint to create.
     * @param x the second argument of the constraint.
     * @return the constructed constraint.
     */
    private @NotNull
    ConstraintClass
    genCons(int code, @NotNull Object x) {
        assert x != null;
        ConstraintClass constraint = new ConstraintClass();
        constraint.add(new AConstraint(code, this, x));
        constraint.addAll(this.constraint);
        assert constraint != null;
        return constraint;
    }

    /**
     * Generates a new integer set variable which is the result of the expression of kind represented by {@code code}
     * applied to {@code this}.
     * @param code an integer code representing the kind of expression to create.
     * @return the constructed integer set variable.
     */
    private @NotNull SetLVar
    genExpr(int code) {
        SetLVar z = new SetLVar();
        z.constraint.add(new AConstraint(code, z, this));
        z.constraint.addAll(this.constraint);
        z.card.constraint.addAll(z.constraint);
        assert z != null;
        return z;
    }

    /**
     * Generates a new integer set variable which is the result of the expression of kind represented by {@code code}
     * applied to {@code this} and {@code y}.
     * @param code an integer code representing the kind of expression to create.
     * @param y second argument of the expression.
     * @return the constructed integer set variable.
     */
    private @NotNull SetLVar
    genExpr(int code, @NotNull SetLVar y) {
        assert y != null;
        SetLVar z = new SetLVar();
        z.constraint.add(new AConstraint(code, z, this, y));
        z.constraint.addAll(y.constraint);
        z.constraint.addAll(this.constraint);
        z.card.constraint.addAll(z.constraint);
        assert z != null;
        return z;
    }
}

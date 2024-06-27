package ie.ucc.cddc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Solver.Objective;

public class ProblemElement {
	/// the collections that this PO is part of
	private Map<String, ProblemElement>               _owners;
	/// the collections that this PO tracks
	private Map<String, Set<ProblemElement> >         _containers;
	/// the values of the items stored in accumulators
	private Map<String, Map<String, Map<ProblemElement, Double> > >_values;
	/// the total accumulated value
	private Map<String, Map<String, Double> >        _accumulators;
	/// an (optional) maximum value that the accumulator is not to exceed
	private Map<String, Map<String, Double> >        _maxValuesForAccumulator;
	/// a set of CSP variables that have to do with this PO
	private Map<String, Var>                         _variables;
	/// the name of this PO
	private String                                   _id;
	
	/// attributes of this object
	private Map<String, String>                      _attributes;
	private Map<String, String[]>                    _multiAttributes;

	/// Sensor readings associated with this object
	private Map<String, Double[]>					 _sensorHistories;
	private Map<String, Integer>					 _sensorFrequencies;
	
	/// the last collection added
	private String _lastCollectionSeen;
	private ProblemElement _lastCollectionItemSeen;

	public static Solution solve(Solver s, Var objective)
	{
		return s.findOptimalSolution(Objective.MAXIMIZE, objective);
	}
	
	/**
	 * Constructor
	 * 
	 * @param name the identifier for this object
	 */
	public ProblemElement(String name) {
		_id = name;

		_owners                  = new HashMap<String, ProblemElement>();
		_containers              = new HashMap<String, Set<ProblemElement> >();
		_accumulators            = new HashMap<String, Map <String, Double> >();
		_values                  = new HashMap<String, Map<String, Map<ProblemElement, Double> > >();
		_variables               = new HashMap<String, Var>();
		_maxValuesForAccumulator = new HashMap<String, Map<String, Double> >();
		_sensorHistories         = new HashMap<String, Double[]>();
		_sensorFrequencies       = new HashMap<String, Integer>();
		_attributes              = new HashMap<String, String>();
		_multiAttributes         = new HashMap<String, String[]>();
	}
	
	/**
	 * Retrieve the identifier given to this object upon creation
	 * 
	 * @return the name of this object
	 */
	public String getId() {
		return _id;
	}
	
	
	/**
	 * Marks this item as part of some collection
	 * 
	 * @param tag    the name of the collection
	 * @param owner  the object tracking the collection
	 */
	private void setOwner(String collection, ProblemElement owner) {
		_owners.put(collection, owner);
	}
		
	/**
	 * Returns the object tracking the collection that this item is part of
	 * 
	 * @param collection the name of the collection
	 * @return
	 */
	public ProblemElement getOwner(String collection) {
		if ( _owners.containsKey(collection) )
			return _owners.get(collection);
		else
			throw new RuntimeException("getOwner :: tag \"" + collection + "\" not found.");
	}
	
	/**
	 * Adds an item to this collection. 
	 * 
	 * @param collection the name of the collection
	 * @param item the item to add
	 */
    public void add(String collection, ProblemElement item) {
    	if ( !_containers.containsKey(collection) )
    	{
    		_containers.put(collection, new HashSet<ProblemElement>());
    	}
    	
   		_containers.get(collection).add(item);
   		item.setOwner(collection, this);
    	
    	_lastCollectionSeen = collection;
    	_lastCollectionItemSeen = item;
    }

	/**
	 * Adds an item to, or replaces the item in, this collection.
	 * To be used for singleton sets. 
	 * 
	 * @param collection the name of the collection
	 * @param item the item to add
	 */
    public void replace(String collection, ProblemElement item) {    	
    	if ( !_containers.containsKey(collection) )
    		_containers.put(collection, new HashSet<ProblemElement>());
    	else if ( _containers.get(collection).size() > 1 )
    		throw new RuntimeException("replace :: collection \"" + collection + "\" is not a singleton set");
    	else
    	{
    		for ( ProblemElement po: _containers.get(collection) )
    			remove(collection, po);
    	}
    	
   		_containers.get(collection).add(item);
   		item.setOwner(collection, this);
    }

    /**
     * Add to the accumulator of a given collection
     * 
     * @param collection the collection of which the item is part
     * @param tag the property that is being tracked
     * @param item the item itself
     * @param val the value of the property
     */
    public void accumulate(String collection, String tag, ProblemElement item, Double val) {
    	if ( !_accumulators.containsKey(collection) )
    		_accumulators.put(collection, new HashMap<String, Double>());
    	if ( !_accumulators.get(collection).containsKey(tag) )
    		_accumulators.get(collection).put(tag, new Double(0));
    	
    	_accumulators.get(collection).put(tag, _accumulators.get(collection).get(tag) + val );
    	
    	if ( !_values.containsKey(collection) )
    		_values.put(collection, new HashMap<String, Map<ProblemElement, Double> >() );
    	if ( !_values.get(collection).containsKey(tag) )
    		_values.get(collection).put(tag, new HashMap<ProblemElement, Double>() );
    	
    	_values.get(collection).get(tag).put(item, val);
    	
    	if( _maxValuesForAccumulator.containsKey(collection) )
    		if ( _maxValuesForAccumulator.get(collection).containsKey(tag) )
    			if ( _accumulators.get(collection).get(tag) > _maxValuesForAccumulator.get(collection).get(tag) )
    				throw new RuntimeException("accumulate :: value for \"" + tag + "\" of \"" + collection + "\" is too large" );
    }
    
    /**
     * Shortcut function to add to the accumulator of a given collection.
     * This assumes that this property belongs to the last item added to a collection 
     * 
     * @param tag the property tracked
     * @param val the value of this property
     */
    public void accumulate(String tag, Double val) {
    	accumulate(_lastCollectionSeen, tag, _lastCollectionItemSeen, val);
    }
    
    /**
     * Assign a maximum value to an accumulator
     * 
     * @param collection the name of the accumulator
     * @param max the value that should not be exceeded
     */
    public void setMax(String collection, String tag, Double max) {
    	if ( !_maxValuesForAccumulator.containsKey(collection) )
    		_maxValuesForAccumulator.put(collection, new HashMap<String, Double>() );
    	
    	_maxValuesForAccumulator.get(collection).put(tag, max);
    }
    
    /**
     * Retrieve the current value of an accumulator
     * 
     * @param collection the name of the collection
     * @param tag the property accumulated
     * @return the current accumulated value
     */
    public Double getAccumulatorValue(String collection, String tag)  {
    	if ( _accumulators.containsKey(collection))
    		if ( _accumulators.get(collection).containsKey(tag) )
    		{
    			return _accumulators.get(collection).get(tag);
    		}
    	
    	return new Double(0);
    }

    public boolean hasAccumulatorValue(String collection, String tag)  {
    	System.out.println(_accumulators.keySet());
    	System.out.println(_accumulators.values());
    	
    	if ( _accumulators.containsKey(collection))
    		if ( _accumulators.get(collection).containsKey(tag) )
    			return true;
    	
    	return false;
    }

    public Set<String> getAccumulators() {
    	return _accumulators.keySet();
    }
    
    public Set<String> getAccumulatorTags(String collection) {
    	if( !_accumulators.containsKey(collection) )
    		throw new RuntimeException("Collection \"" + collection + "\" not found");
    	
    	return _accumulators.get(collection).keySet();
    }
    
    /**
     * Checks whether a given item is part of the collection
     * 
     * @param tag the name of the collection
     * @param item the item to check for
     * @return
     */
    public boolean contains(String tag, ProblemElement item) {
    	return _containers.get(tag).contains(item);
    }
    
    /**
     * Remove an item from a collection, updating the value
     * of the associated accumulator if necessary
     * 
     * @param collection the name of the collection
     * @param item the item to remove
     */
    public Map<String, Double> remove(String collection, ProblemElement item) {
    	Map<String, Double> removedValues = new HashMap<String, Double>();
    	
    	if ( _accumulators.containsKey(collection) ) {
    		for( Entry<String, Double> e: _accumulators.get(collection).entrySet() ) {
    			String key = e.getKey();
    			
    			Double stored = _values.get(collection).get(key).get(item);
    			if ( stored == null )
    				throw new RuntimeException("Remove :: stored value for \"" + item.getId() + "\" in collection \"" + collection + "\", property \"" + key + "\" not found");
    	
    			removedValues.put(key, stored);
    			
    			_accumulators.get(collection).put(key, _accumulators.get(collection).get(key) - stored );
    			_values.get(collection).get(key).remove(item);
    		}
    	}
    	
    	_containers.get(collection).remove(item);
    	
    	return removedValues;
    }
    
    /**
     * Stores a variable associated with this object under
     * a given id
     * 
     * @param tag the identifier for this variable
     * @param v the variable to add
     */
    public void associateVar(String tag, Var v) {
    	_variables.put(tag, v);
    }
    
    
    /**
     * Retrieves a variable stored earlier
     * 
     * @param tag the identifier for the variable
     * @return
     */
    public Var getVar(String tag) {
    	if ( _variables.containsKey(tag) )
    		return _variables.get(tag);
    	
    	throw new RuntimeException("getVar :: tag \"" + tag + "\" not found");
    }
        
    /**
     * Returns an iterator to the items in a collection
     * 
     * @param collection the name of the collection 
     * @return an iterator
     */
    @SuppressWarnings("unchecked")
	public Iterator uncheckedIterator(String collection) {
    	return (Iterator) _containers.get(collection).iterator();
    }

	public Iterator<ProblemElement> iterator(String collection) {
    	return  _containers.get(collection).iterator();
    }

    /**
     * Store a list of sensor readings 
     * 
     * @param sensor identifier for the type of sensor
     * @param readings the values recorded
     */
    public void addSensorHistory(String sensor, Double [] readings)
    {
    	_sensorHistories.put(sensor, readings);
    }
    
    /**
     * Retrieve a set of sensor readings
     * 
     * @param sensor the identifier of the sensor
     * @return the readins stored previously
     */
    public Double[] getSensorHistory(String sensor)
    {
    	if ( _sensorHistories.containsKey(sensor) )
    		return _sensorHistories.get(sensor);
    	
    	throw new RuntimeException("getSensorHistory :: sensor \"" + sensor + "\" not found");    	
    }

    /**
     * Returns the number of samples over a given threshold
     * 
     * @param sensor the sensor we're interested in
     * @param val the threshold
     * @return
     */
    public int numReadingsAbove(String sensor, Double val) {
    	int count = 0;
    	
    	if ( !_sensorHistories.containsKey(sensor) )
    		throw new RuntimeException("numReadingsAbove :: sensor \"" + sensor + "\" not found");
    	
    	for( Double v: _sensorHistories.get(sensor) )
    		if (v > val)
    			++count;
    	
    	return count;
    }

    /**
     * Returns the number of samples equal to or over a given threshold
     * 
     * @param sensor the sensor we're interested in
     * @param val the threshold
     * @return
     */
    public int numReadingsEqualAbove(String sensor, Double val) {
    	int count = 0;
    	
    	if ( !_sensorHistories.containsKey(sensor) )
    		throw new RuntimeException("numReadingsAbove :: sensor \"" + sensor + "\" not found");
    	
    	for( Double v: _sensorHistories.get(sensor) )
    		if (v >= val)
    			++count;
    	
    	return count;
    }

    /**
     * Returns the number of samples in the longest consecutive run of samples at or above a given threshold
     * 
     * @param sensor the sensor we're interested in
     * @param val the threshold
     * @return
     */
    public int numConsecutiveReadingsEqualAbove(String sensor, Double val) {
    	int count = 0;
    	int max = 0;
    	
    	if ( !_sensorHistories.containsKey(sensor) )
    		throw new RuntimeException("numReadingsAbove :: sensor \"" + sensor + "\" not found");
    	
    	for( Double v: _sensorHistories.get(sensor) )
    	{
    		if (v >= val)
    			++count;
    		else
    		{
    			if ( count > max )
    				max = count;
    			count = 0;
    		}
    	}
    	
    	return max;
    }
    
    /**
     * Returns the number of samples in the longest consecutive run of samples at or above a given threshold
     * 
     * @param sensor the sensor we're interested in
     * @param val the threshold
     * @return
     */
    public int numConsecutiveReadingsAbove(String sensor, Double val) {
    	int count = 0;
    	int max = 0;
    	
    	if ( !_sensorHistories.containsKey(sensor) )
    		throw new RuntimeException("numReadingsAbove :: sensor \"" + sensor + "\" not found");
    	
    	for( Double v: _sensorHistories.get(sensor) )
    	{
    		if (v > val)
    			++count;
    		else
    		{
    			if ( count > max )
    				max = count;
    			count = 0;
    		}
    	}
    	
    	return max;
    }

    /**
     * Store an attribute [a (variable, value)-pair]
     * 
     * @param name the name fo the attribute
     * @param value the value
     */
    public void setAttribute(String name, String value)
    {
    	_attributes.put(name, value);
    }
    
    /**
     * Retrieves the value of an attribute as a string
     * 
     * @param name the attribute to find
    */
    public String getAttributeAsString(String name)
    {
    	return _attributes.get(name);
    }

    /**
     * Retrieves the value of an attribute as an integer
     * 
     * @param name the attribute to find
    */
    public int getAttributeAsInteger(String name)
    {
    	return Integer.parseInt( _attributes.get(name) );
    }

    /**
     * Retrieves the value of an attribute as a double
     * 
     * @param name the attribute to find
    */
    public double getAttributeAsDouble(String name)
    {
    	return Double.parseDouble( _attributes.get(name) );
    }

    /** 
     * Store a set of values for a given attribute
     * 
     * @param name the name of the attribute
     * @param values the values to store
     */
    public void setMultiAttribute(String name, String [] values)
    {
    	_multiAttributes.put(name, values);
    }
    
    /**
     * Returns the set of values stored for a given attribute
     * 
     * @param name the name of the attribute
     */
    public String[] getMultiAttribute(String name)
    {
    	return _multiAttributes.get(name);
    }
    
    /**
     * Store the sensor history 
     * 
     * @param name the name of the sensor
     * @param values list of values
     */
    public void setSensorHistory(String name, double [] values)
    {
    	Double[] valueArray = new Double[values.length];
    	for(int i=0; i<values.length; ++i)
    		valueArray[i] = values[i];
    	
    	_sensorHistories.put(name, valueArray);
    }

    /**
     * Store the sensor history 
     * 
     * @param name the name of the sensor
     * @param values list of values
     */
    public void setSensorHistory(String name, int [] values)
    {
    	Double[] valueArray = new Double[values.length];
    	for(int i=0; i<values.length; ++i)
    		valueArray[i] = (double)values[i];
    	
    	_sensorHistories.put(name, valueArray);
    }
    
    /** 
     * store the frequency with which this sensor was read
     * 
     * @param name
     * @param values
     */
    public void setSensorFrequency(String name, int frequency)
    {
    	_sensorFrequencies.put(name, new Integer(frequency));
    }
    

    /**
	 * Dump the contents of the object to standard out
	 */
    public void dump()
    {
    	System.out.println("  " + _id );
    	System.out.println("  - Attributes :: { ");
    	for( Entry<String, String> e : _attributes.entrySet() )
    		System.out.println("          (" + e.getKey() + " = " + e.getValue() + ")");
    	System.out.println("    }");
    	
    	System.out.println("  - Multi-attributes :: { ");
    	for( Entry<String, String[]> e : _multiAttributes.entrySet() )
    	{
    		System.out.print("          (" + e.getKey() + " =" );
    		for(int i = 0; i<e.getValue().length; ++i)
    			System.out.print( " "  + e.getValue()[i]);
    		System.out.println(")");
    	}
    	System.out.println("    }");
    	
    	System.out.println("  - Parts :: {");
    	for( Entry<String, Set<ProblemElement>> e: _containers.entrySet())
    	{
    		System.out.print("          (" + e.getKey() + " =");
    		for( ProblemElement po: e.getValue() )
    			System.out.print(" " + po.getId());
    		System.out.println(")");
    	}
    	System.out.println("    }");
    	
//    	private Map<String, Map<String, Double> >        _accumulators;
    	System.out.println("  - Accumulators :: {");
    	for( Entry<String, Map<String, Double>> e: _accumulators.entrySet())
    	{
    		System.out.print("          (" + e.getKey() + " =");
    		for( Entry<String, Double> e2: e.getValue().entrySet() )
    			System.out.print(" (" + e2.getKey() + "=" + e2.getValue()+")");
    		System.out.println(")");
    	}
    	System.out.println("    }");

    	
    }
}

package ie.ucc.cddc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

public class Problem {

	static private Map<String, Map<String, ProblemElement>> _elements = new HashMap<String, Map<String, ProblemElement>>();
	static private Map<String, Set<String []>> _inconsistencies = new HashMap<String, Set<String []>>();

	/**
	 * Adds a new object to the problem and returns it. If an object already
	 * exists by the same name, return that instead
	 * 
	 * @param type the type of object (disk, server, application, ...)
	 * @param name the name of the object
	 * @return
	 */
	static public ProblemElement add(String type, String name)
	{
		checkContainer(type);
		
		return findOrCreate(type, name);
	}
	
	/**
	 * Stores that a certain component is part of a container
	 * 
	 * @param containerType the type of container
	 * @param containerName the name of the container
	 * @param partType the type of component part
	 * @param part the component part
	 */
	static public void setPart(String containerType, String containerName, String partType, ProblemElement part)
	{
		checkContainer(containerType);
		ProblemElement po = findOrCreate(containerType, containerName);
		
		po.add(partType, part);
	}

	static public void setPartOf(String containerType, String containerName, String partType, ProblemElement part)
	{		
		setPart(containerType, containerName, partType, part);
	}

	/**
	 * Stores that a certain component is part of a container
	 * 
	 * @param containerType the type of container
	 * @param container the container object
	 * @param partType the type of component part
	 * @param partName the component name
	 */	
	static public void setPart(String containerType, ProblemElement container, String partType, String partName)
	{
		checkContainer(partType);
		ProblemElement po = findOrCreate(partType, partName);
		
		container.add(partType, po);
	}

	/**
	 * Increase the accumulator of the given type for the given containner
	 * 
	 * @param containerType the type of container
	 * @param containerName the name of the container
	 * @param accumulator the name of the accumulator
	 * @param value the value to add
	 */
	static public void accumulate(String containerType, String containerName, String accumulator, double value)
	{
		checkContainer(containerType);
		ProblemElement po = findOrCreate(containerType, containerName);
		
		po.accumulate(accumulator, value);
	}

	/**
	 * Check if we have already seen a container of this type, if not create the necessary data structures
	 * @param type
	 */
	static private void checkContainer(String type)
	{
		if ( !_elements.containsKey(type) )
		{
			_elements.put(type, new HashMap<String, ProblemElement>());
		}
	}
	
	/**
	 * Find the object of the given type/name. If it does not exist yet, create one
	 * 
	 * @param type the type of object
	 * @param name the name of the object
	 */
	static private ProblemElement findOrCreate(String type, String name)
	{
		checkContainer(type);
		if ( _elements.get(type).containsKey(name) )
			return _elements.get(type).get(name);

		
		ProblemElement newPO = new ProblemElement(name);
		_elements.get(type).put(name, newPO);
		return newPO;
	}
	
	/**
	 * Find the object of the given type/name.
	 * 
	 * @param type the type of object
	 * @param name the name of the object
	 */
	static public ProblemElement find(String type, String name)
	{
		checkContainer(type);
		if ( _elements.get(type).containsKey(name) )
			return _elements.get(type).get(name);

		throw new RuntimeException("cannot find wanted item");
	}
	
	/**
	 * Dump the contents of the problem to standard out
	 */
	static public void dump()
	{
		for (Entry<String, Map<String, ProblemElement>> e : _elements.entrySet() )
		{
			System.out.println("********************************************");
			System.out.println("****    " + e.getKey());
			System.out.println("********************************************");
			for( Entry<String, ProblemElement> item: e.getValue().entrySet())
			{
				item.getValue().dump();
			}
		}
	}
	
	/**
	 * Get the set of objects of a given type 
	 * 
	 * @param type the type of objects we're looking for
	 */
	public static ProblemElement[] getObjects(String type)
	{
		Vector<ProblemElement> matches = new Vector<ProblemElement>();
		
		checkContainer(type);		
		for(ProblemElement po : _elements.get(type).values() )
			matches.add(po);
		
		ProblemElement [] matchArray = new ProblemElement[matches.size()];
		int i=0;
		for(ProblemElement po: matches)
			matchArray[i++] = po;
		return matchArray;
	}
	
	/**
	 * Mark that a set of objects (identified by their names) are inconsistent with each other,
	 * i.e. these cannot be part of the same container
	 * 
	 * @param type the type of objects
	 * @param names the names of the incompatible objects
	 */
	public static void inconsistent(String type, String[] names)
	{
		if ( !_inconsistencies.containsKey(type) )
			_inconsistencies.put(type, new HashSet<String []>());
		_inconsistencies.get(type).add(names);
	}
	
	/**
	 * Get the set of inconsistencies of a given object type 
	 * 
	 * @param type the type of objects we are looking for
	 * @return
	 */
	public static Set<String []> getInconsistencies(String type)
	{
		if (_inconsistencies.containsKey(type))
			return _inconsistencies.get(type);
		else
			return null;
	}
	
}

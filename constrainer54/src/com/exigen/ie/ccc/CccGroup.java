/*
 * 
 * Created on 19.05.2003
 */
package com.exigen.ie.ccc;

import java.util.Vector;

/**
 * 
 * @author alexeyenko
 */
public class CccGroup
{

	private String name;
	private CccCore core;
	private Vector objects;
	
	public CccGroup(CccCore core, String name)
	{
		this.core = core;
		this.name = name;
		objects = new Vector();
	}
	
	public int size()
	{
		return objects.size();
	}
	
	public String getName()
	{
		return name;
	}

	public String add(CccObject ci)
	{
		if (objects.contains(ci))
			return getObjectId(ci);
		String id = name+core.getDivider()+objects.size();
		objects.add(ci);
		return id;
	}

	public String getObjectId(CccObject o)
	{
		if (!objects.contains(o))
			return null;
		return name+core.getDivider()+objects.indexOf(o);
	}

	public void reset()
	{
		objects.clear();
	}

	public CccObject getObject(int i)
	{
		return (CccObject)objects.get(i);
	}

}

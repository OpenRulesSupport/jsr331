package com.exigen.ie.scheduler;

///////////////////////////////////////////////////////////////////////////////
/*
 * Copyright Exigen Group 1998, 1999, 2000, 2002
 * 320 Amboy Ave., Metuchen, NJ, 08840, USA, www.exigengroup.com
 *
 * The copyright to the computer program(s) herein
 * is the property of Exigen Group, USA. All rights reserved.
 * The program(s) may be used and/or copied only with
 * the written permission of Exigen Group
 * or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which
 * the program(s) have been supplied.
 */
///////////////////////////////////////////////////////////////////////////////

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;

/**
 * An interface for the representation of the different Resources (people, machines, materials).
 * <p>
 * Resource has diferent capacities in different times.
 * There are require-constraints between jobs and resources.
 *
 * @see Job
 * @see ResourceDiscrete
 * @see ResourceUnary
 */
public interface Resource {

    /**
     * Assigns an object to resource.
     * @param o Object to assign
     */
    public void setObject(Object o);

    /**
     * Get assigned object.
     * @return Assigned object
     */
    public Object getObject();

    /**
     * Sets resource maximum capacity at the specified moment of time
     * Capacity cannot be more than initial passed to constructor
     * @param time Time moment for new capacity
     * @param capacity New capacity
     * @throws Failure
     */
    public void setCapacityMax(int time, int capacity) throws Failure;

    /**
     * Sets resource minimum capacity at the specified moment of time
     * Capacity cannot be more than initial passed to constructor
     * @param time Time moment for new capacity
     * @param capacity New capacity
     * @throws Failure
     */
    public void setCapacityMin(int time, int capacity) throws Failure;

    /**
     * Sets resource maximum capacity at the specified interval of time
     * Capacity cannot be more than initial passed to constructor
     * @param time1 A start time
     * @param time2 An end time
     * @param capacity New capacity
     * @throws Failure
     */
    public void setCapacityMax(int time1, int time2, int capacity) throws Failure;

    /**
     * Sets resource minimum capacity at the specified interval of time
     * Capacity cannot be more than initial passed to constructor
     * @param time1 A start time
     * @param time2 An end time
     * @param capacity New capacity
     * @throws Failure
     */
    public void setCapacityMin(int time1, int time2, int capacity) throws Failure;
    /**
     * Returns resource maximum capacity at the specified moment of time
     * @param time Time for capacity query
     * @throws Failure
     * @return Capacity
     */
    public int getCapacityMax(int time) throws Failure;

    /**
     * Returns resource minimum capacity at the specified moment of time
     * @param time Time for capacity query
     * @throws Failure
     * @return Capacity
     */
    public int getCapacityMin(int time) throws Failure;

    /**
     * Returns resource availability durarion
     * @return Resource availability durarion
     */
    public int duration();

    /**
     * Returns internal variable associated with capacity at specified
     * moment of time
     * @param time Moment of time
     * @return Capacity variable
     * @throws Failure
     */
    public IntExp getCapacityVar(int time) throws Failure;

    /**
     * Returns resource availability start time
     * @return Resource availability start time
     */
    public int timeMin();

    /**
     * Returns resource availability end time
     * @return Resource availability end time
     */
    public int timeMax();

    /**
     * Returns constrainer this resource associated with
     * @return  constrainer this resource associated with
     */
    public Constrainer constrainer();

    /**
     * Returns an array of capacity variables
     * @return An array of capacity variables
     */
    public IntExpArray caps();

    /**
     * Sets resource name
     * @param name New resource name
     */
    public void setName(String name);

    /**
     * Returns resource name
     * @return Resource name
     */
    public String getName();

    /**
     * Returns text representation of job assignment
     * @return Assigned jobs
     */
    public String getAssignment();

    public String toString();

    /**
     * Returns string representation of current state of resource
     * @return String representation
     */
    public String mapString();
}
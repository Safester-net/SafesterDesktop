/*
 * This file is part of Safester.                                    
 * Copyright (C) 2019, KawanSoft SAS
 * (https://www.Safester.net). All rights reserved.                                
 *                                                                               
 * Safester is free software; you can redistribute it and/or                 
 * modify it under the terms of the GNU Lesser General Public                    
 * License as published by the Free Software Foundation; either                  
 * version 2.1 of the License, or (at your option) any later version.            
 *                                                                               
 * Safester is distributed in the hope that it will be useful,               
 * but WITHOUT ANY WARRANTY; without even the implied warranty of                
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU             
 * Lesser General Public License for more details.                               
 *                                                                               
 * You should have received a copy of the GNU Lesser General Public              
 * License along with this library; if not, write to the Free Software           
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301  USA
 * 
 * Any modifications to this file must keep this entire header
 * intact.
 */
package com.safelogic.utilx.collection;

// 12/07/02 16:40 GR - creation
// 12/07/02 16:50 GR - javadoc
// 16/07/02 18:45 GR - add getElementAt()
// 16/07/02 18:55 GR - getLastElement()
// 24/07/02 12:00 GR - add & use Println() in commented self-test code

import java.util.EmptyStackException;
import java.util.Enumeration;
import java.util.Vector;


/**
 * This class is a simple fifo stack implementation using a Vector
 * to store elements.<br>
 * It has to modes: if the pushOut flag is set, data will be poped
 * automatically if push() is called and the capacity has been reached;
 * if the flag isn't set, an IllegalStateException is raised when the
 * capacity is reached.
 */

public class FifoStack
{
	/**	The Vector where the stack elements are stored */
	private Vector m_vStack ;
	
	/**	The maximum stack capacity */
	private int m_nCapacity ;
	
	/**	Indicates whether the automatic pushOut is on or off */
	private boolean m_pushOut ;
	
	
	/**
	 * Creates a new fifo stack of the given capacity.<br>
	 * If the pushOut flag is set, data will be poped automatically if
	 * push() is called and the capacity has been reached.
	 * If the flag isn't set, an IllegalStateException is raised when the
	 * capacity is reached.
	 * @param	nCapacity	the stack capcity (must be > 0)
	 * @param	pushOut		true to set automatic push out,
	 *						false to raise an Exception if capacity is reached
	 */
	
	public FifoStack(int nCapacity, boolean pushOut)
	{
		if(nCapacity < 1)
			throw new IllegalArgumentException("Capacity must be > 0") ;
		m_nCapacity = nCapacity ;
		m_pushOut = pushOut ;
		m_vStack = new Vector(m_nCapacity) ;
	}
	
	
	/**
	 * Pushes the given Object in stack.
	 * @param		obj		the Object to push in stack
	 * @exception	IllegalStateException
	 *				if capacity is reached and auto pushOut isn't set
	 */
	
	public void push(Object obj)
		throws IllegalStateException
	{
		if(m_vStack.size() == m_nCapacity)
		{
			if(m_pushOut)
				pushOut() ;
			else
				throw new IllegalStateException("Maximum stack capacity reached") ;
		}
		
		m_vStack.addElement(obj) ;
	}
	
	
	/**
	 * Looks at the first object of this stack without removing it from the stack.
	 * @return		the first object of this stack
	 * @exception	EmptyStackException		if this stack is empty
	 */
	
	public Object peek()
		throws EmptyStackException
	{
		try{
			return m_vStack.elementAt(0) ;
		}
		catch(ArrayIndexOutOfBoundsException aioobe)
		{
			throw new EmptyStackException() ;
		}
	}
	
	
	/**
	 * Removes the first object of this stack and returns it.
	 * @return		the first object of this stack
	 * @exception	EmptyStackException		if this stack is empty
	 */
	
	public Object pop()
		throws EmptyStackException
	{
		Object obj = peek() ;
		pushOut() ;
		return obj ;
	}
	
	
	/**
	 * Removes the first object of this stack.
	 * @exception	EmptyStackException		if this stack is empty
	 */
	
	public void pushOut()
		throws EmptyStackException
	{
		removeElementAt(0) ;
	}
	
	
	/**
	 * Removes the element at the specified index.
	 * @param		nIndex		the index of the object to remove
	 * @exception	EmptyStackException		if this stack is empty
	 */
	
	public void removeElementAt(int nIndex)
		throws EmptyStackException
	{
		try{
			m_vStack.removeElementAt(nIndex) ;
		}
		catch(ArrayIndexOutOfBoundsException aioobe)
		{
			throw new EmptyStackException() ;
		}
	}
	
	
	/**
	 * Returns the element at the specified index.
	 * @param	nIndex		the index of the object to remove
	 * @return	the element at the specified index
	 */
	
	public Object getElementAt(int nIndex)
	{
		return m_vStack.elementAt(nIndex) ;
	}
	
	
	/**
	 * Gets the last element added to this stack.
	 * @return	the last element added to this stack
	 * @exception	EmptyStackException		if this stack is empty
	 */
	
	public Object getLastElement()
		throws EmptyStackException
	{
		if(empty())
			throw new EmptyStackException() ;
		return getElementAt(size() - 1) ;
	}
	
	/**
	 * Returns an enumeration of the components of this stack.
	 * @return	an enumeration of the components of this stack
	 */
	
	public Enumeration getElements()
	{
		return m_vStack.elements() ;
	}
	
	
	/**
	 * Indicates if this stack is empty.
	 * @return	true if this stack is empty, false otherwise
	 */
	
	public boolean empty()
	{
		return (m_vStack.size() == 0) ;
	}
	
	
	/**
	 * Returns the number of components in this stack.
	 * @return	the number of components in this stack
	 */
	
	public int size()
	{
		return m_vStack.size() ;
	}
	
	
	/**
	 * Returns a string representation of this stack.
	 * @return	a string representation of this stack
	 */
	
	public String toString()
	{
		return m_vStack.toString() ;
	}
	
	
	/**
	 * Returns where an object is on this stack.
	 * @param	obj		the desired object
	 * @return	the index of the given object in this stack;
	 *			the return value -1 indicates that the object is not on the stack
	 */
	
	public int search(Object obj)
	{
		return m_vStack.indexOf(obj) ;
	}
	
	
	/**
	 * Gets this stack capacity.
	 * @return	this stack capacity
	 */
	
	public int getCapacity()
	{
		return m_nCapacity ;
	}
	
	
	/**
	 * Indicates if this stack is in auto pushOut mode.
	 * @return	true is this stack is in auto pushOut mode, false otherwise
	 */
	
	public boolean isAutoPushOut()
	{
		return m_pushOut ;
	}
	
	// SELF TEST CODE: BEGIN
	/*
	
	public static void main(String args[])
	{
		try{
			selfTest() ;
		}
		catch(Exception eAll)
		{
			System.err.println("Error during self test:") ;
			eAll.printStackTrace() ;
		}
		
		try{
			Println("Type <ENTER> to end test") ;
			System.in.read() ;
		}
		catch(Exception eAll)
		{
			// do nothing
		}
	}
	
	public static void selfTest()
	{
		test(10, 5, true) ;
		try{
			test(7, 5, false) ;
		}
		catch(IllegalStateException ise)
		{
			Println("Error caused on purpose!") ;
			ise.printStackTrace(System.out) ;
		}
	}
	
	public static void test(int nLimit, int nCapacity, boolean pushOut)
	{
		FifoStack fifo = new FifoStack(nCapacity, pushOut) ;
		Println("Test with limit=" + nLimit
				+ ", capacity=" + nCapacity
				+ ", pushOut=" + pushOut) ;
		for(int n = 0 ; n < nLimit ; n++)
		{
			if(n == nCapacity)
			{
				Println("peek()=" + fifo.peek() + " : " + fifo) ;
				Println("pop()=" + fifo.pop() + " : " + fifo) ;
			}
			
			fifo.push(new Integer(n)) ;
			Println("push(" + n + "): " + fifo) ;
		}
	}
	
	private static void Println(String sMsg)
	{
		System.out.println(sMsg) ;
	}
	
	*/
	// SELF TEST CODE: END
}

/*******************************************************************************
 * realMethods Confidential
 * 
 * 2018 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.cloudmigrate.codetemplate.model.method;

/**
 * Wrapper class for name and type of a method argument
 * @author realMethods, Inc.
 *
 */
public class MethodArg
{

	/**
	 * Default constructor
	 */
	public MethodArg() {
		//no_op
	}
	
	/**
	 * Constructor
	 * @param name
	 * @param type
	 */
    public MethodArg(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Returns the name field.
     * 
     * @return
     */
    public String getName() { 
    	return name; 
    }

    /**
     * Assigns the name field the provided argument.
     * 
     * @param name
     */
    public void setName( String name ) { 
    	this.name = name; 
    }

    /**
     * Return the type field.
     * 
     * @return
     */
    public String getType() { 
    	return type; 
    }

    /**
     * Assigns the type field the provided argument.
     * 
     * @param type
     */
    public void setType( String type ) { 
    	this.type = type; 
    }

// attributes
    
    protected String name = null;
    protected String type = null;
}

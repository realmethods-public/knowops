/*******************************************************************************
 * realMethods Confidential
 * 
 * 2021 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
package com.realmethods.codetemplate.model.method;

import java.util.ArrayList;
import java.util.List;

import com.realmethods.codetemplate.*;

/**
 * Encapsulates the notions of arguments for a method. Includes
 * a list of MethodArg objects as the input args and a returnType.
 * 
 * @author realMethods, Inc.
 *
 */
public class MethodArgs extends AppGenObject
{

	/** 
	 * Default constructor
	 */
    public MethodArgs() {
        args = new ArrayList<>();
    }

    
    /**
     * Returns the args field.
     * 
     * @return List<MethodArg>
     */
    public List<MethodArg> getArgs() { 
    	return( args ); 
    }
    
    /**
     * Assigns the args field the provided argument.
     * 
     * @param args
     */
    public void setArgs( List<MethodArg> args )
    { this.args = args; }
    
    /**
     * Returns the return type.
     * 
     * @return String
     */
    public String getReturnType() {
    	return returnType;
    }
    
    public void setReturnType( String returnType ) {
    	this.returnType = returnType;
    }
    
    // attributes
    protected List<MethodArg> args	= null;
    protected String returnType 	= null;
}

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

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.codetemplate.*;
import com.cloudmigrate.codetemplate.parser.*;
import com.cloudmigrate.common.helpers.Utils;

/**
 * Base class for all method class definitions to support the different model types
 * @author realMethods, Inc.
 *
 */
public class MethodObject extends AppGenObject
{
	/**
	 * Default constructor
	 */
    public MethodObject(){
    	// no_op
    }

    /**
     * Returns the name field.
     * 
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Assigns the name field the provided argument.
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the visibility field.
     * 
     * @return String
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Assigns the visibility field the provided argument.
     * 
     * If null is provided, the visbility field is forced to 'public'.
     * @param arg
     */
    public void setVisibility(String arg) {
    	if ( arg != null )
    		visibility = arg;
    	else
    		visibility = "public";
    }

    /**
     * Returns the returnType field of the method.
     * 
     * @return String
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Assigns the returnType field the provided argument.
     * 
     * @param type
     */
    public void setReturnType(String type) {
        returnType = type;
    }

    /**
     * Returns the isStatic field.
     * 
     * @return boolean
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Assigns the isStatic field the provided argument.
     * @param is
     */
    public void isStatic(boolean is) {
        isStatic = is;
    }

    /**
     * Helper method to determine if this is a findAll type of method.
     * 
     * Assumes the name of the method has been prefixed with 'findAll'
     * 
     * @return boolean
     */
    public boolean isFindAllBy()
    {
    	return( name.startsWith( FIND_ALL ) );
    }
    
    /**
     * Returns the arguments field.
     * 
     * @return MethodArgs
     */
    public MethodArgs getArguments() {
        return arguments;
    }
    
    /**
     * Assigns the arguments field the provided argument.
     * 
     * @param args
     */
    public void setArguments( MethodArgs args ) {
    	arguments = args;
    }

    /** 
     * Returns true if the arguments field is non-null and not empty.
     * 
     * @return boolean
     */
    public boolean hasArguments() {    	
    	return( arguments != null && !arguments.getArgs().isEmpty());
    }
    
    /** 
     * Returns true if the documentation field is non-null and not empty.
     * 
     * @return boolean
     */    
    public boolean hasDocumentation() {
        return documentation != null && !documentation.isEmpty();
    }

    /**
     * Returns the documentation field.
     * 
     * @return String
     */
    public String getDocumentation() {
        return documentation;
    }
    
    /**
     * Assigns the documentation field the provided argument.
     * 
     * @param doc
     */
    public void setDocumentation( String doc ) {
    	documentation = doc;
    }

    /**
     * Returns a String representation of itself.
     * 
     * @return String
     */
    public String toString() {
    	return( name );
    }

    /**
     * Delegates internally to formattedString()
     * 
     * @return String
     */
    public String getAsFormattedString() {
    	return formattedString();    	
    }
    
    /**
     * Returns a String representing the typical declaration structure of a method
     * in the form
     * <visibility> <return_type> <name> ( <list of args> )
     * 
     * @return
     */
    public String formattedString() {
        StringBuilder buffer = new StringBuilder();
        
        if(isStatic())
            buffer.append("static ");
        
        buffer.append(getVisibility() + " " + getReturnType() + " " + getName() + "(" + getAsDeclaration() + ")");
        
        return buffer.toString();
    }
    
    /**
     * Returns a string representing the arguments as though in a method argument calling format
     * @return
     */
    public String getArgumentList() {
        StringBuilder buffer = new StringBuilder();
        Collection args 	= arguments.args;
        
        if(args != null && !args.isEmpty()) {
            Iterator iter = args.iterator();
            MethodArg arg = null;
            
            while( iter.hasNext() )  {
                arg = (MethodArg)iter.next();
                buffer.append(arg.name);
                
                if( iter.hasNext() )
                    buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    /**
     * Returns a string representing the arguments as though in a method declarative argument format
     * @return
     */
    public String getAsDeclaration()
    {
        StringBuilder buffer = new StringBuilder();
        Collection<MethodArg> args 			= arguments.args;
        
        if(args != null && !args.isEmpty()) {
            Iterator iter = args.iterator();
            MethodArg arg = null;
            
            while( iter.hasNext() )  {
                arg = (MethodArg)iter.next();
                buffer.append(arg.type + ' ' + arg.name);
                
                if( iter.hasNext() )
                    buffer.append(", ");
            }
        }
        return buffer.toString();
    }

    /**
     * Returns true for a non-void returnType
     * @return
     */
    public boolean hasReturnValue() {
        return returnType != null && !returnType.equalsIgnoreCase("void");
    }
    
    @Override
    public boolean equals( Object object ) 
    {
    	if ( super.equals(object) && object instanceof MethodObject)
	    		return( name.equalsIgnoreCase( ((MethodObject)object).getName() ) );
	    return( false );
    }

    @Override
    public int hashCode() {
    	int code = visibility.hashCode();
    	
    	if ( name != null )
    		code = code * name.hashCode();
    	
    	return code;
    }
    
    /**
     * Helper method used to fix up the return type in the case no value or an errant value is applied
     */
    protected void reconcileReturnType()
    {
        if(returnType == null)
            returnType = "void";
        else
            returnType = reconcileType(returnType);
        
        if ( name != null && name.startsWith( "findAllBy" )
        	&& !returnType.equalsIgnoreCase("Collection") 
        	&& !returnType.equalsIgnoreCase("java.util.Collection") ) {
        	final String msg = "Invalid return type for findAllBy method. "
					+ "findAllBy methods must be declared to return a Collection." 
					+ name + " with return type " + returnType 
					+ " will be changed to java.util.Collection."; 
    		LOGGER.log( Level.WARNING, msg);
        	returnType = "Collection";
        }
    }
    /**
     * Helper method used to fix up the arguments in the case none or an errant value is applied
     */
    protected void reconcileArguments()
    {
        List<MethodArg> tempArgs 	= new ArrayList<>(arguments.args.size());
        Iterator iter 					= arguments.args.iterator();
        MethodArg arg 					= null;
        String type 					= null;
        
        for(; iter.hasNext(); tempArgs.add(new MethodArg(arg.name, type)))
        {
            arg = (MethodArg)iter.next();
            type = reconcileType(arg.type);
        }
        arguments.args = tempArgs;
    }

    /**
     * Helper method used to coerce an unsupported type into a supported type
     * @param type
     * @return
     */
    protected String reconcileType(String type)
    {
        String reconcileType = Utils.reconcileType(type);
        
        if(reconcileType == null)
        {
        	final String msg = "Type Validation Failure Failed to determine the validity of type " + type ;
            LOGGER.log(Level.WARNING, msg);
            reconcileType = type;
        }
        return reconcileType;
    }

    // attributes
    protected String visibility 		= "public";    		
    protected String name				= null;
    protected String returnType			= null;
    protected String documentation		= null;
    protected MethodArgs arguments		= null;
    protected boolean isStatic			= false;
    
    private static final String FIND_ALL = "findAll";
    private static final Logger LOGGER 	= Logger.getLogger(MethodObject.class.getName());
}

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
package com.realmethods.codetemplate;

import java.util.Arrays;
import java.util.List;

/**
 * enumeration of processed model node types
 * 
 * @author realMethods, Inc.
 *
 */
public enum UMLRootType 
{	CONTAINERS,
	SUBSYSTEMS,
    COMPONENTS,
    CLASSES,
    INTERFACES,
    SERVICES,
    DTOS,
    ENUMS,
    ROLES;


	/**
	 * Static method to return a List (ArrayList) of the enumerated values.
	 * 
	 * @return List 	supported uml root types
	 */	

	public static List<UMLRootType> getValues()
	{
		return Arrays.asList(UMLRootType.values());
	}

	/**
	 * Static method returns the default value of CLASSES.
	 * 
	 * @return ModelType	default uml root type
	 */
	public static UMLRootType getDefaultValue()
	{
		return (CLASSES);
	}
 
	@Override
	/**
	 * Returns a String representation 
	 * 
	 * @return		String representation
	 */
	public String toString() {
		switch(this) {
		  	case CONTAINERS: 
		  		return( "__containers__" );
		  	case SUBSYSTEMS: return( "__subsystems__" );
		  	case COMPONENTS: return( "__components__" );
		  	case CLASSES: return( "__classes__" );
		  	case INTERFACES: return( "__interfaces__" );
		  	case SERVICES: return( "__services__" );
		  	case DTOS: return( "__dtos__" );
		  	case ENUMS: return( "__enums__" );
		  	case ROLES: return( "__roles__" );
		  	default: throw new IllegalArgumentException(); 
		  }
	}

	/**
	 * Static method determines which value of this enum best matches
	 * the provided name.
	 * 
	 * @param					name representation of the UMLRootType
	 * @return 	UMLRootType		uml root type
	 */	 
    public static UMLRootType whichOne(String name)
    {	  
	    
    	if (name.equalsIgnoreCase("containers__")) {
    		return (UMLRootType.CONTAINERS);
    	}
    	
    	if (name.equalsIgnoreCase("SUBSYSTEMS")) {
    		return (UMLRootType.SUBSYSTEMS);
    	}

    	if (name.equalsIgnoreCase("COMPONENTS")) {
    		return (UMLRootType.COMPONENTS);
    	}
     
    	if (name.equalsIgnoreCase("CLASSES")) {
    		return (UMLRootType.CLASSES);
    	}

    	if (name.equalsIgnoreCase("INTERFACES")) {
    		return (UMLRootType.INTERFACES);
    	}

    	if (name.equalsIgnoreCase("SERVICES")) {
    		return (UMLRootType.SERVICES);
    	}

    	if (name.equalsIgnoreCase("ENUMS")) {
    		return (UMLRootType.ENUMS);
    	}
     
    	if (name.equalsIgnoreCase("DTOS")) {
    		return (UMLRootType.DTOS);
    	}
     
    	if (name.equalsIgnoreCase("ROLES")) {
    		return (UMLRootType.ROLES);
    	}
    	else {
    		return (getDefaultValue());
    	}
    }

}

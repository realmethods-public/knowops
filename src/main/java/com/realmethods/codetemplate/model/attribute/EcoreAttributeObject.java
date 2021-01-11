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
package com.realmethods.codetemplate.model.attribute;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EEnumLiteral;

import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.parser.ModelParser;
import com.realmethods.common.helpers.Utils;

/**
 * Wraper class to extract data from an Eclipse EMC Ecore EStructuralFeature into
 * the subclass AttributeObject
 * 
 * @author realMethods, Inc.
 *
 */
public class EcoreAttributeObject extends AttributeObject
{
	/**
	 * Extracts data from the EStructuralFeature to store in local variables
	 * 
	 * @param classObject
	 * @param eAttribute
	 */
    public EcoreAttributeObject( ClassObject classObject, EStructuralFeature eAttribute ) {
    	super( classObject );

    	setName( eAttribute.getName() );
    	setType( Utils.convertType( eAttribute.getEType().getName() ) );
    	
        if ( eAttribute.getDefaultValue() != null ) {
        	// if it is an enum, treat the default value differently
        	if ( ModelParser.modelParser().findEnum(eAttribute.getEType().getName()) == null) {
        		applyDefaultValue( eAttribute.getDefaultValue().toString() );
        	}
        	else {
        		applyDefaultValue( eAttribute.getEType().getName() 
        				+ "." 
        				+ Utils.capitalizeFirstLetter(eAttribute.getDefaultValue().toString()) );
        	}
    	}
    
        setVisibility( "protected" );
        canBeNull( true );
        isPrimaryKey( eAttribute.isRequired() );
        isIntrinsicType( false );
        isStatic( false );
        isFinal( eAttribute.isUnsettable() );
        isTransient( eAttribute.isTransient() );
        isVolatile( eAttribute.isVolatile() );
        isUserIdentifiable( getUserIdentifiableIndicator( eAttribute ) );
        
        applyUniqueName();
    }

	/**
	 * Extracts data from the EEnumLiteral to store in local variables
	 * 
	 * @param classObject
	 * @param eEnumLiteral
	 */
    public EcoreAttributeObject( ClassObject classObject, EEnumLiteral eEnumLiteral )
    {
    	super( classObject );
	    this.name = eEnumLiteral.getInstance().getName();
	    // force all enum types to String
	    setType( "String" );
	    this.defaultValue = "\"" + Integer.toString( eEnumLiteral.getInstance().getValue() ) + "\"";
	    
	    this.visibility = "protected";
	    isPrimaryKey( false );
	    this.canBeNull = true;
	    this.isIntrinsicType = false;
	    this.isStatic = false;
	    this.isFinal = false;
	    this.isTransient = false;
	    this.isVolatile = false;
	    this.isUserIdentifiable = false;
	    applyUniqueName();
    }
    
    /**
     * Simple binding of data from an exsiting attribute via copy
     * 
     * @param classObject
     * @param attribute
     */
    public EcoreAttributeObject( ClassObject classObject, AttributeObject attribute)
    {
    	super( classObject );
        copy(attribute);
    }

    /**
     * 
     * @param eAttribute
     * @return
     */
    public boolean getUserIdentifiableIndicator( EStructuralFeature eAttribute )
    {
    	boolean isTheIndicator 	= false;
    	EAnnotation eAnnotation = eAttribute.getEAnnotation( "keywords" );
    	
    	if ( eAnnotation != null ) {
    		java.util.Iterator<String> iter = eAnnotation.getDetails().values().iterator();
    		String keyword = null;
    		while( iter.hasNext() && !isTheIndicator ) {
    			keyword = iter.next();
    			if ( keyword.lastIndexOf( "Identif" ) >= 0 )
    				isTheIndicator =  true;
    			else 
    				isTheIndicator =  false;	
    		}
    	}   	
    	return( isTheIndicator );
    }
    
// attributes

}

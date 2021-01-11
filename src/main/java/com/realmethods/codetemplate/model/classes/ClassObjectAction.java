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
package com.realmethods.codetemplate.model.classes;
	    
import com.realmethods.codetemplate.*;

import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.association.*;
import com.realmethods.codetemplate.model.attribute.*;
import com.realmethods.codetemplate.model.method.*;
import com.realmethods.codetemplate.parser.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles the implementation of all action related methods declared on a ClassObject
 * @author realMethods, Inc.
 *
 */
public class ClassObjectAction extends AppGenObject
	implements IClassObjectAction
{

	public ClassObjectAction( ClassObject classObject) {
		this.classObject = classObject;
	}

	public void applySelfToAssociations() {
		for( AssociationEndObject association : classObject.getAssociations() )
			association.setClassObject( classObject );
	}


	public void sortAttributes()
	{
		List<AttributeObject> attributes = new ArrayList<>();
		
		attributes.addAll( classObject.getAttributes());
		
        Collections.sort( attributes, new AttributeNameComparator() );
	}

	public void addMethod(MethodObject method)
	{
		if ( method != null ) {
			if (methodIsUnique(method)) {
				final String msg = method.toString(); 
				LOGGER.info(msg);
				classObject.getBusinessMethods().add(method);
			}
			else {
				final String msg = "Duplicate method " + method.toString()
				+ " found, but will not be added to class " + classObject.getName();
				LOGGER.info(msg);
			}
		}
	}

	public boolean methodIsUnique(MethodObject method)
	{
		Iterator<MethodObject> iter = classObject.getBusinessMethods().iterator();
		boolean unique = true;
		while (iter.hasNext() && (unique))
		{
			if (method.toString().equals(iter.next().toString()))
				unique = false;
		}

		return (unique);
	}

	public void addAttribute(AttributeObject attributeObject)
	{
		String attributeObjectName = attributeObject.getName();
		if (attributeObjectName.indexOf("lnk") == -1)
		{
			AttributeObject attributeToFind = classObject.findAttribute(attributeObjectName); 
			if ( attributeToFind == null )
			{
				classObject.getAttributes().add( attributeObject );
				final String msg = attributeObjectName
						+ ":" + attributeObject.getType() + "["
						+ attributeObject.getDefaultValue() + "]"; 
				LOGGER.info( msg );
			}
			else
			{
				final String msg = "Duplication of attribute - Class " + classObject.getName()
						+ " already has an attribute name "
						+ attributeObjectName + ".";
				LOGGER.log(Level.WARNING, msg);
			}
		}
	}

	public void addAssociation(AssociationEndObject association)
	{
		if (association != null)
		{
			String roleName = association.getRoleName();
			Iterator iter = classObject.getAssociations().iterator();
			boolean found;
			for (found = false; iter.hasNext() && !found; found = roleName.equalsIgnoreCase(((AssociationEndObject) iter.next())
							.getRoleName()))
				;
			if (!found)
			{
				association.setClassObject( classObject );
				classObject.getAssociations().add(association)
				;
				LOGGER.info(association.getRoleName());
			}
			else
			{
				final String msg = "Class " + classObject.getName()
						+ " already has an association with role name "
						+ roleName + ".";
				LOGGER.log( Level.WARNING, msg);
			}
		}
	}

	public void removeAttribute(AttributeObject attribute)
	{
		Iterator<AttributeObject > i = classObject.getAttributes().iterator();
		 
		while(i.hasNext()) {
		    AttributeObject e = i.next();
		    if (e.getName().equalsIgnoreCase( attribute.getName())) {
		        i.remove();
		    }
		}
	}

	public void removeAssociation(AssociationEndObject association)
	{
		if ( association != null )
			classObject.getAssociations().removeIf(e -> e.getRoleName().equalsIgnoreCase( association.getRoleName() ));
	}

	public void removeMethod(MethodObject method)
	{
		if ( method != null )
			classObject.getBusinessMethods().removeIf(e -> e.formattedString().equalsIgnoreCase( method.formattedString() ));
	}

	public void modifyPrimaryKeysTo( String transferToType )
    {
    	Iterator<AttributeObject> pks = classObject.getAllPrimaryKeysInHierarchy().iterator();
    	AttributeObject attr = null;
    	while( pks.hasNext() )
    	{
    		attr = pks.next();
    		attr.setType( transferToType );
    		attr.setDefaultValue( null );
    	}
    }

	public AttributeObject transformAssociationToAttribute(AssociationEndObject associationEndObject)
	{
		AttributeObject attributeObject = null;
		String attributeName = null;
		String attributeType = null;
		String instantiationType = null;
		String defaultValue = null;

		attributeName = associationEndObject.getRoleName();
		
		if (!associationEndObject.isMultivalued())
		{
			attributeType 		= associationEndObject.getType();
			instantiationType 	= attributeType;
		}
		else
		{
			attributeType = "Set<" + associationEndObject.getType() + ">";
			instantiationType 	= "HashSet<" + associationEndObject.getType() + ">";
		}
				
		if ( !attributeType.equalsIgnoreCase(this.classObject.getName() ) )
			defaultValue = "new " + instantiationType + "()";
		else // avoid a race condition
			defaultValue = "null";
		
		if (ModelParser.modelParser().findEnum(associationEndObject.getType()) != null)
		{
			attributeType = associationEndObject.getType();
			attributeObject = new AttributeObject();
			attributeObject.setClassObject(classObject);
			attributeObject.setName( attributeName );
			attributeObject.setType(attributeType);
			attributeObject.setVisibility("protected");
			attributeObject.isPrimaryKey(false);
			attributeObject.canBeNull(true);
			attributeObject.isIntrinsicType(false);
			attributeObject.isStatic(false);
			attributeObject.isFinal(false);
			attributeObject.isTransient(false);
			attributeObject.isVolatile(false);
			attributeObject.isFromEnumerator(true);
			attributeObject.setDefaultValue(attributeType
					+ ".getDefaultValue()");
		}
		else
		{
			AttributeObject tmpAttrObj= new AttributeObject();
			tmpAttrObj.setName( attributeName );
			tmpAttrObj.setType(attributeType);
			tmpAttrObj.setVisibility("protected");
			tmpAttrObj.isPrimaryKey(false);
			tmpAttrObj.canBeNull(true);
			tmpAttrObj.isIntrinsicType(false);
			tmpAttrObj.isStatic(false);
			tmpAttrObj.isFinal(false);
			tmpAttrObj.isTransient(false);
			tmpAttrObj.isVolatile(false);
			tmpAttrObj.isFromEnumerator(false);
			tmpAttrObj.setDefaultValue(defaultValue);
			tmpAttrObj.isFromAssociation(true);
			tmpAttrObj.canBeNull(true);
			tmpAttrObj.setDisplayName(associationEndObject.getRoleName());
			tmpAttrObj.isComposite(associationEndObject.isComposite());
			tmpAttrObj.setClassObject(classObject);
			
			attributeObject = new AssociationAttributeObject( classObject, tmpAttrObj );
			attributeObject.setDefaultValue( defaultValue );
			((AssociationAttributeObject) attributeObject).applyAssociation(associationEndObject);

		}

		if (associationEndObject.isMultivalued())
			attributeObject.isFromMultiValueAssociation(true);

		if (!ModelParser.hasIdentity(associationEndObject.getType()))
			attributeObject.isComponent(true);

		addAttribute(attributeObject);

		return (attributeObject);
	}

	public AttributeObject findAttribute( String attributeName )
	{
		if ( attributeName != null )
		{
			for( AttributeObject attributeObject : classObject.getAttributes() )
			{
				if( attributeName.equalsIgnoreCase( attributeObject.getName() ) )
					return( attributeObject );
			}
		}
		return null;
	}
	
	// attributes
	protected ClassObject classObject 				= null;
    private static final Logger LOGGER 				= Logger.getLogger(ClassObjectAction.class.getName());
	
    /**
     * inner class used to sort a List of AttributeObject by name
     * @author realMethods, Inc.
     *
     */
	public class AttributeNameComparator implements java.util.Comparator<AttributeObject>
    {
	    public int compare(AttributeObject a, AttributeObject b)
	    {
	        return (a.getName().compareTo(b.getName()));
	    }
    }

}

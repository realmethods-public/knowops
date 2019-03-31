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
package com.cloudmigrate.codetemplate.model.classes;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.codetemplate.model.association.AssociationEndObject;
import com.cloudmigrate.codetemplate.model.attribute.AttributeObject;
import com.cloudmigrate.codetemplate.model.factory.AttributeFactory;
import com.cloudmigrate.codetemplate.model.method.ClassMethodObject;
import com.cloudmigrate.codetemplate.model.method.MethodObject;
import com.cloudmigrate.codetemplate.parser.ModelParser;

/**
 * ClassObject subclass to mock a Java Class discovered during reflection
 * 
 * @author realMethods, Inc.
 * 
 */
public class ClassClassObject extends ClassObject {
	public ClassClassObject(Class theClass) {
		this.theClass = theClass;
	}

	@Override
	/**
	 * Signals the model is fully loaded and it is safe to
	 * load methods, attributes and associations
	 */
	public void finishLoading() {
		loadMethods();
		loadAttributes();
		loadAssociations();
	}

	/**
	 * Loads the methods from the EMF
	 */
	public void loadMethods() {
		LOGGER.log(Level.INFO, "ClassClassObject.loadMethods() - Loading methods for class " + theClass.getName() );

		try {
			MethodObject methodObject = null;
			Method[] methods = theClass.getDeclaredMethods();
			String name = null;
			String formatted = null;
			List<String> methodDecls = new java.util.ArrayList<>();
	
			for (int i = 0; i < methods.length; i++) {
				methodObject = new ClassMethodObject(methods[i]);
				name = methodObject.getName();
				// only looking for business methods, so any getters, setters,
				// equals, or toString
				// will be excluded
				if (!name.startsWith("get")  && !name.startsWith("set") 
						&& !name.startsWith("equals") && !name.startsWith("toString")
						&& !name.startsWith("copy") && !name.startsWith("is")) {
					formatted = methodObject.formattedString();
					if (!methodDecls.contains(formatted)) {
						addMethod(methodObject);
						methodDecls.add(formatted);
					}
				}
			}
		}
		catch( Exception exc )
		{
			LOGGER.log( Level.WARNING, "Unable to load methods for class {0}", theClass.getName() );
		}
	}

	/**
	 * Loads the attributes from the EMF.
	 */
	public void loadAttributes() {
		LOGGER.log(Level.INFO, "ClassClassObject.loadAttributes() - Loading attributes" );

		try {
			Field[] fields 				= theClass.getDeclaredFields();
			AttributeObject attribute 	= null;
			String type 				= null;
			
			for (int i = 0; i < fields.length; i++) {
				final String roleName	= null;
				int iUpperRange	 		= 1;
				attribute	 			= AttributeFactory.getInstance().createInstance(this, fields[i]);
				
				// check to see if it's one of our class types..if so, treat as
				// an association attribute
				if (!attribute.isCollection()
						&& ModelParser.modelParser().findClass(attribute.getType()) == null) {
					addAttribute(attribute);
				} else {
					type = attribute.getType();

					if (attribute.isCollection()) {
						iUpperRange = -1;
						// type is java.util.ArrayList<com.sample.className>
						// ...need to extract ClassName
						int lastIndexOfDot = attribute.getType().lastIndexOf('.');
						int lastIndexOfGreaterThanSymbol = attribute.getType().lastIndexOf('>');
						type = attribute.getType().substring(lastIndexOfDot + 1, lastIndexOfGreaterThanSymbol);
					}

					AssociationEndObject association = new AssociationEndObject();
					association.setVisibility("public");
					association.setType( type );
					association.isNavigable( true );
					association.isOrdered( false );
					association.isAggregate( false );
					association.isComposite( false );
					association.setLowerRange(1);
					association.setUpperRange(iUpperRange);
					association.setRoleName( roleName );
				
					// need to create a non-navigable sibling association as
					AssociationEndObject sibling = new AssociationEndObject();
					sibling.setVisibility("public");
					sibling.setType( type );
					sibling.isNavigable( false );
					sibling.isOrdered( false );
					sibling.isAggregate( false );
					sibling.isComposite( false );
					sibling.setLowerRange(1);
					sibling.setUpperRange(iUpperRange);
					sibling.setRoleName( roleName );

					// make it bi-directional
					association.setSiblingAssociationEndObject(sibling);
					association.setClassObject(this);
					addAssociation(association);
					
					final String msg = "Added role " + roleName;
					LOGGER.log(Level.INFO, msg);
				}
			}
		} catch (java.lang.NoClassDefFoundError noClassDefFoundError) {
			LOGGER.log(Level.WARNING, "ClassClassObject.loadAttributes() - failed to load attributes for class " 
						+ getName()
						+ ". Unable to locate attribute class file " 
						+ noClassDefFoundError.toString());
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "ClassClassObject.loadAttributes() for class:" + getName(), exc );
		}
	}

	/**
	 * A no_op since the associations are loaded during attribute loading within the EMF.
	 */
	public void loadAssociations() {
		// no_op...handled by loadAttributes
	}

	// attributes
	protected Class theClass 			= null;
	private static final Logger LOGGER	= Logger.getLogger(ClassClassObject.class.getName());	
}

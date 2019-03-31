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

import java.util.Iterator;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.cloudmigrate.codetemplate.model.attribute.AttributeObject;
import com.cloudmigrate.codetemplate.model.factory.MethodFactory;
import com.cloudmigrate.codetemplate.model.factory.AttributeFactory;
import com.cloudmigrate.codetemplate.model.factory.AssociationFactory;
import com.cloudmigrate.codetemplate.parser.ModelParser;

/**
 * Abstraction for Eclipse EMF notion of a ClassObject.
 * 
 * @author realMethods, Inc.
 *
 */
public class EcoreClassObject extends ClassObject {
	public EcoreClassObject(EClass eClass) {
		this.eClass = eClass;
	}

	@Override
	/**
	 * Signals loading is complete of the entire ECORE EMF and it is now
	 * safe to load methods, attributes, and associations
	 */
	public void finishLoading() {
		loadMethods();
		loadAttributes();
		loadAssociations();
	}

	/**
	 * Loads the methods
	 */
	public void loadMethods() {
		LOGGER.log(Level.INFO, "Loading methods for class " + getName());

		Iterator iter = eClass.getEAllOperations().iterator();

		while (iter.hasNext())
			addMethod(MethodFactory.getInstance().createInstance((EOperation) iter.next()));
	}

	/**
	 * Loads the attributes
	 */
	public void loadAttributes() {
		LOGGER.log(Level.INFO, "Loading the attributes for class" + getName());

		// deal with attributes first
		Iterator iter = eClass.getEAttributes().iterator();
		EStructuralFeature eStructuralFeature = null;
		AttributeObject attribute = null;
		String name = null;
		
		while (iter.hasNext()) {
			eStructuralFeature = (EStructuralFeature) iter.next();
			attribute = AttributeFactory.getInstance().createInstance(this, eStructuralFeature);
			name = attribute.getName();
			if ( name != null && name.toUpperCase().endsWith( "ID" ) ) {
			    attribute.isPrimaryKey( true );
			}
			addAttribute(attribute);
		}

		// next, deal with references
		iter = eClass.getEReferences().iterator();

		while (iter.hasNext()) {
			eStructuralFeature = (EStructuralFeature) iter.next();
			// if the type is one of our business objects, treat it as an
			// association
			if (ModelParser.modelParser().findClass(eStructuralFeature.getEType().getName()) == null)
				addAttribute(AttributeFactory.getInstance().createInstance(this, eStructuralFeature));
			else
				addAssociation(AssociationFactory.getInstance().createInstance(getName(),
						(EReference) eStructuralFeature, true));
		}
	}

	/**
	 * It is a no_op on loading associations since 
	 * they are loaded during attribute loading
	 */
	public void loadAssociations() {
		// no_op...handled by loadAttributes
	}

	// attributes
	protected EClass eClass 			= null;
	private static final Logger LOGGER	= Logger.getLogger(EcoreClassObject.class.getName());
}

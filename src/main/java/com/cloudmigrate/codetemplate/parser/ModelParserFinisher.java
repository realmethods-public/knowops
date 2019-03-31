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
package com.cloudmigrate.codetemplate.parser;

import com.cloudmigrate.codetemplate.model.* ;
import com.cloudmigrate.codetemplate.model.attribute.*;
import com.cloudmigrate.codetemplate.model.classes.*;
import com.cloudmigrate.common.helpers.Utils;
import com.cloudmigrate.codetemplate.model.association.*;
import com.cloudmigrate.codetemplate.*;
import com.cloudmigrate.exception.*;
import com.cloudmigrate.foundational.common.exception.ProcessingException;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Helper class to assist a ModelParser with the finishing step of parsing
 * into the goFramework Generation-Time model.
 * 
 * @author realMethods, Inc.
 *
 */
public class ModelParserFinisher extends AppGenObject
{
	/**
	 * Constructor
	 * 
	 * @param modelParser
	 */
	public ModelParserFinisher( ModelParser modelParser ) {
		this.modelParser = modelParser;
	}

	/**
	 * Helper to delegate to the modelParser.
	 * 
	 * @return List<BaseModelObject>
	 */
	public List<BaseModelObject> getAllInHierarchy()
	{
		return modelParser.getAllInHierarchy();
	}

	/**
	 * Helper to delegate to the modelParser.
	 * 
	 * @return List<ClassObject>
	 */
	public List<ClassObject> getAllClassesInHierarchy()
	{
		return modelParser.getAllClassesInHierarchy();
	}
	
	/**
	 * Called externally to denote the model itself has been fully 
	 * loaded and post processing can safely take place.
	 *  
	 * @throws GenerationException
	 */
	public void finish() throws GenerationException
	{
		try {
			LOGGER.log(Level.INFO, "Post processing of loaded model");
		
			// it is safe for all classes, components, and subsystems to finish
			// loading their contained stuff since all defined types should
			// be loaded at this point
			finishLoading();
				
			removeJavaPackageClasses();
			reconcileSuperTypes();
			makeAssociatedAttributesIntoAssociations();
			generateAttributesForAssociations();
			generateMethods();
			applyIdentity();
			sortAttributes();
			evaluateMethodArgs();
			applyAuditable();
			checkOneToOneWithCompoundPK();
			applyAssociationEndObjects();			
		}
		catch (Exception exc) {
			LOGGER.log(Level.WARNING, "Exception thrown during finishing of model loading", exc);
			throw new GenerationException( "Failure during ModelParserFinisher.finish() - " + exc);
		}
		
	}
	
	/**
	 * Helper method to invoke finishLoading(List) uses all objects in the modelParse.
	 */
	protected void finishLoading()
	{
		finishLoading( modelParser.getAllInHierarchy());
	}

	/**
	 * Helper method to inform each object to finish loading itself.
	 * 
	 * @param objects
	 */
	protected void finishLoading(List<? extends BaseModelObject> objects )
	{
		objects.forEach( baseModelObject -> {
			final String msg = "Finishing loading component " + baseModelObject.getName();
			LOGGER.log(Level.INFO, msg);
			baseModelObject.finishLoading();
		});
	}

	/**
	 * Apply identity to the classobject, if required.
	 * 
	 * Applying identity means the classoject will have at least one primary key field attribute.
	 * 
	 * @param classObject
	 */
	public void applyIdentity(ClassObject classObject)
	{
		final String appllyIdentityMsg = "Applying identity for class " + classObject.getName();
		LOGGER.log(Level.INFO, appllyIdentityMsg);
		
		if ( !modelParser.hasIdentity(classObject) && !classObject.hasParent() )
		{
			if ( !classObject.hasIdentity()) {
				String attributeName = Utils.lowercaseFirstLetter(classObject.getName()) + "Id";
				String attributeType = "Long";
				AttributeObject attribute = createAnAttributeForClass( classObject, attributeName, attributeType );
				
				attribute.isPrimaryKey(true);
				classObject.hasForcedIdentity(true);
			}
		}
		else{
			if ( modelParser.hasIdentity(classObject) ) {
				final String msg = "Skipping applying identity for class " 
						+ classObject.getName() + " because it already has identity";
				LOGGER.log(Level.INFO, msg);
			} else if ( classObject.hasParent() ) {
				final String msg = "Skipping applying identity for class " 
						+ classObject.getName() + " because it already has a parent " + classObject.getParentName();
				LOGGER.log(Level.INFO, msg);
			}
		}

	}

	/**
	 * Applies identity throughout the entire model.
	 * 
	 * @throws ProcessingException
	 */
	public void applyIdentity()
	{
		LOGGER.log(Level.INFO, "Applying identity where needed throughout model...");
		Consumer<ClassObject> methodRef = this::applyIdentity;
		getAllClassesInHierarchy().forEach( methodRef );
	}
	
	/**
	 * This is a no_op for now.
	 */
	private void evaluateMethodArgs()
	{
		LOGGER.log(Level.INFO, "ModelParserFinisher.evaluateMethodArgs() - Evaluating method arguments" );
	}

	/**
	 * Often, a model parser will unintentionally include primitive types into the model. 
	 * They need to be removed.  This is often true for classes and interfaces
	 * 
	 * Delegates internally to removeJavaPackageClasses( List ) on both the clases and interfaces.
	 */
	private void removeJavaPackageClasses()
	{
		LOGGER.log(Level.INFO, "ModelParserFinisher.removeJavaPackageClasses() - Removing standard Java classes and interfaces... ");

		// handle classes, interfaces, and components
		removeJavaPackageClasses( this.getAllInHierarchy() );
		removeJavaPackageClasses( this.modelParser.getInterfaces() );
	}

	/**
	 * Often, a model parser will unintentionally include primitive types into the model. 
	 * They need to be removed.
	 * 
	 * Delegates internally to removeJavaPackageClass(BaseModelObject)
	 * @param list
	 */
	private void removeJavaPackageClasses( List<? extends BaseModelObject> list )
	{
		BaseModelObject baseModelObject;
		Iterator<? extends BaseModelObject> iter = list.iterator(); 
		while (iter.hasNext() )  {
			baseModelObject = iter.next();
			if ( removeJavaPackageClass( baseModelObject )  ) {
		        this.modelParser.remove(baseModelObject.getName());
			}
		}		

	}

	/**
	 * Often, a model parser will unintentionally include primitive types into the model. 
	 * They need to be removed.
	 * 
	 * @return	boolean
	 */	
	private boolean removeJavaPackageClass(BaseModelObject baseModelObject)
	{
		boolean remove = false;
		if (baseModelObject != null)
		{
			String name 		= baseModelObject.getName();
			String packageName 	= null;

			if (name != null && (name.indexOf("java.") != -1 || name.indexOf("Java.") != -1)) {
				remove = true;
				LOGGER.log(Level.WARNING, " - Removing standard java class " + baseModelObject.getName());
			}

			if (!remove )
			{
				packageName = baseModelObject.getPackageName();
				if (packageName != null && (packageName.indexOf("java.") != -1 || packageName.indexOf("Java.") != -1)) {
					remove = true;
					final String msg = "Removing standard java class " + name;
					LOGGER.log(Level.INFO, msg);
				}
			}
		}

		return (remove);
	}

	/**
	 * Sorts the attributes on everything in the model
	 */
	private void sortAttributes()
	{
		LOGGER.info("Sorting attributes, primary keys first");
		
		modelParser.getAllClassesInHierarchy().forEach(baseModelObject -> {
			baseModelObject.sortAttributes();
			LOGGER.info(" - Done sorting attributes for " + baseModelObject.getName());
		});
	}

	/**
	 * Forces each ComponentObject in the modelto reconcile it's super types
	 */
	private void reconcileSuperTypes()
	{
		LOGGER.log(Level.INFO, "Reconciling generalizations and realizations");
				
		getAllInHierarchy().forEach( componentObject -> {
			String msg = "Class " + componentObject.getName() + ", parent is : "
					+ componentObject.getParentName() + ", interfaces are : "
					+ componentObject.getInterfaces();
			componentObject.reconcileSuperTypes();
		
			LOGGER.log(Level.INFO, msg);
		});
		
		LOGGER.info("Verifying lack of Eerator generalizations and realizations...");
		
		final String ENUM_CLASS_MSG_PREFIX = "Enumerator class ";
		modelParser.getEnums().forEach( enumObj -> {
			enumObj.reconcileSuperTypes();
			
			String msg = null;
			
			if (enumObj.hasParent())
				msg = ENUM_CLASS_MSG_PREFIX + enumObj.getName() + " should not have a parent.";
			if (enumObj.getInterfaces().isEmpty())
				msg = ENUM_CLASS_MSG_PREFIX + enumObj.getName() + " should not interfaces.";
			if (enumObj.getAssociations().isEmpty())
				msg = ENUM_CLASS_MSG_PREFIX + enumObj.getName() + " should not have any associations.";
			if (enumObj.getAttributes().isEmpty())
				msg = ENUM_CLASS_MSG_PREFIX + enumObj.getName()
						+ " has no attributes.An enumerated type should have at least one attribute defined.";
			
			if (msg != null)
				LOGGER.log(Level.WARNING, msg);
 		});
	}

	/**
	 * A no_op 
	 */
	private void generateMethods()
	{
		LOGGER.log(Level.INFO, "ModelParserFinisher.generateMethods() - Generating business methods for classes, services, and intefaces...");
	}
	
	/** 
	 * Delegates internally to have each entity in the model inspect its attributes to determine which
	 * in fact should be an association.
	 */
	private void makeAssociatedAttributesIntoAssociations()
	{
		makeAssociatedAttributesIntoAssociations( this.getAllClassesInHierarchy() );
	}
	
	private void makeAssociatedAttributesIntoAssociations( List<ClassObject> classes )
	{
		LOGGER.log(Level.INFO, "Turning attributes declared as classes into Associations...");
		
		classes.forEach( classObject -> {
			Iterator<AttributeObject> attribIter = classObject.getAttributes().iterator();
			ClassObject refClassModelObject = null;
			AssociationEndObject association = null;
			AssociationEndObject sibling = null;
			AttributeObject attribute = null;
			String visibility = "public";
			String type = null;
			String rolename = null;
			boolean isNavigable = false;
			boolean isOrdered = false;
			boolean isAggregate = false;
			boolean isComposite = false;
			int iLowerRange = 1;
			int iUpperRange = 1;
						
			while ( attribIter.hasNext()  )
			{
				attribute = attribIter.next();
				
				refClassModelObject 	= (ClassObject) modelParser.locateBaseModelObject( attribute.getType() );
				
				if (refClassModelObject != null)
				{
					logWarnMessage(refClassModelObject.getName()
							+ " is declared as an attribute but should be an association to "
							+ classObject.getName());
							
					isNavigable 	= true;
					type 			= attribute.getType();
					rolename 		= attribute.getName();
					
					attribIter.remove();

					// create the association
					association = new AssociationEndObject();
					association.setVisibility(visibility);
					association.setType(type);
					association.setRoleName(rolename);
					association.isNavigable(isNavigable);
					association.isOrdered(isOrdered);
					association.isAggregate(isAggregate);
					association.isComposite(isComposite);
					association.setLowerRange(iLowerRange);
					association.setUpperRange(iUpperRange);
					
					// create it's sibling
					isNavigable = false;
					type 		= classObject.getName();
					rolename 	= attribute.getName();
					
					sibling = new AssociationEndObject();
					sibling.setVisibility(visibility);
					sibling.setType(type);
					sibling.setRoleName(rolename);
					sibling.isNavigable(isNavigable);
					sibling.isOrdered(isOrdered);
					sibling.isAggregate(isAggregate);
					sibling.isComposite(isComposite);
					sibling.setLowerRange(iLowerRange);
					sibling.setUpperRange(iUpperRange);

					classObject.addAssociation(association);
					refClassModelObject.addAssociation(sibling);
					association.setSiblingAssociationEndObject(sibling);
					sibling.setSiblingAssociationEndObject(association);
				}
				else // find it as an enum
				{
					if ( modelParser.findEnumInHiearchy( attribute.getType() ) != null )
						attribute.isFromEnumerator(true);
				}
			}
		});
	}

	/**
	 * Helper method to turn attributes into association where applicable.
	 * 
	 */
	private void generateAttributesForAssociations()
	{
		LOGGER.log(Level.INFO, "Turning single and multiple associations into appropriate contained attributes...");
		
		getAllClassesInHierarchy().forEach( classObject -> {
			AttributeObject attributeObject 			= null;
			AssociationEndObject associationEndObject 	= null;
			Iterator iter 								= null;			

			for (iter = classObject.getAssociations().iterator(); iter
					.hasNext();)
			{
				associationEndObject = (AssociationEndObject) iter.next();
				if (associationEndObject.isNavigable()
						&& !associationEndObject.isZeroToZero())
				{
					attributeObject = classObject.transformAssociationToAttribute(associationEndObject);
					if (attributeObject.isFromEnumerator())
						iter.remove();
				}
			}
		});
	}

	/**
	 * Often associations in the model do not have an associated end (sibling. This 
	 * tactic forces the classObject to make itself the end point of its association.
	 */
	private void applyAssociationEndObjects() {
		Consumer<ClassObject> methodRef = ClassObject::applySelfToAssociations;
		getAllClassesInHierarchy().forEach( methodRef );
	}

	/**
	 * Internal helper method to inspect the expression of 1-to-1 associations with another
	 * that itself has a compound key.
	 * 
	 * Although the goFramework support the notion of compound keys, it is discouraged.
	 */
	private void checkOneToOneWithCompoundPK() {
		LOGGER.log(Level.INFO, "Checking for one-to-one associations to entities with compound keys...");		

		getAllClassesInHierarchy().forEach( classObject -> 
			classObject.getSingleAssociations().forEach( association -> {
				ClassObject tmpClassObject 	= modelParser.findClass(association.getType());
				if (tmpClassObject != null && classObject.hasCompoundPrimaryKey()) {
					final String msg = "Compound Key Warning : Discovered a compound key on " + classObject.getName();
					LOGGER.warning(msg);
				}
			})
		);
	}

	/**
	 * If a class is declared auditable in its originating model, an attribute is
	 * assigned with the name 'lastUpdateTimestamp' of Date type.
	 * 
	 */
	protected void applyAuditable()
	{
		LOGGER.log(Level.INFO, "Applying audit attributes where Auditable is declared...");

		getAllClassesInHierarchy().stream().filter( ClassObject::isAuditable )
		.collect(Collectors.toList()).forEach( classObject -> {
			AttributeObject attribute = createAnAttributeForClass( classObject, "lastUpdateTimestamp", "java.util.Date" );
			attribute.setVisibility("private");
		});
	}

	/**
	 * Internal helper class to create a primary key attribute for the provided ClassObject.
	 * 
	 * @param	ClassObject
	 * @return	AttributeObject
	 */
	protected AttributeObject createAnAttributeForClass( ClassObject classObject, String name, String type ) {
		AttributeObject attribute = new AttributeObject();
		
		attribute.setClassObject(classObject);
		attribute.setName( name );
		attribute.setType( type );
		attribute.setDefaultValue("null");
		attribute.setVisibility("protected");
		attribute.isPrimaryKey(true);
		attribute.canBeNull(false);
		attribute.isIntrinsicType(false);
		attribute.isStatic(false);
		attribute.isFinal(false);
		attribute.isTransient(false);
		attribute.isVolatile(false);
				
		classObject.addAttribute(attribute);
		
		return attribute;
		
	}
	
	// attribute 
	protected ModelParser modelParser 	= null;
	private static final Logger LOGGER 	= Logger.getLogger(ModelParserFinisher.class.getName());

}

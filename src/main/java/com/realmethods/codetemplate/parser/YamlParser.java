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
package com.realmethods.codetemplate.parser;

import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.factory.*;
import com.realmethods.foundational.common.exception.ProcessingException;

import com.realmethods.codetemplate.model.association.AssociationEndObject;
import com.realmethods.codetemplate.model.attribute.AttributeObject;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.container.ContainerObject;
import com.realmethods.codetemplate.model.classes.enumerate.EnumClassObject;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.TypeDescription;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Core class for handling parsing of an YAML file, normally with either
 * a .yaml or .yml extension.  The extension is ignored here.
 * 
 * @author realMethods, Inc.
 *
 */
public class YamlParser extends BaseModelParser
{
	/**
	 * YamlParser - default constructor
	 */
	protected YamlParser() {
		// intentionally left blank
	}
	
	/**
	 * Constructor
	 * 
	 * Delegates to the super class
	 * 
	 * @param yamlFileFilePathAndName
	 */
    public YamlParser( String yamlFileFilePathAndName )
    {
    	this.yamlFileFilePathAndName = yamlFileFilePathAndName;
    }
   
    @Override
    /**
     * Handles the parsing and creation of the internal goFramework model from an Eclipse EMF model,
     */
    protected void doRun() throws ProcessingException {
        try {
        	yamlLoad();
        	finishLoading();
	    }
	    catch(Exception exc) {
	    	LOGGER.log(Level.WARNING, "YamlParser.doRun()", exc);
	    	throw new ProcessingException( "YamlParser:doRun()", exc);
	    }

    }

    public void yamlLoad() throws ProcessingException {
    	LOGGER.log(Level.INFO, "Starting to parse and load the YAML file {0}", yamlFileFilePathAndName);

    	if(yamlFileFilePathAndName != null && !yamlFileFilePathAndName.isEmpty()) {
    		Constructor constructor 				= new Constructor(YamlModel.class);
    		TypeDescription modelTypeDescription 	= new TypeDescription(YamlModel.class);
    		
    		modelTypeDescription.addPropertyParameters("containers", YamlContainer.class);
    		modelTypeDescription.addPropertyParameters("entities", YamlEntity.class);
    		modelTypeDescription.addPropertyParameters("data", YamlData.class);
    		modelTypeDescription.addPropertyParameters("relations", YamlRelation.class);
    		modelTypeDescription.addPropertyParameters("enums", YamlEnum.class);
    		
    		constructor.addTypeDescription(modelTypeDescription);
    		Yaml yaml = new Yaml(constructor);
    		
    	    try {
    	    	InputStream inputStream = new FileInputStream( new File(yamlFileFilePathAndName) );
    	    	YamlModel yamlModel = yaml.load(inputStream);
    	    	StringWriter writer = new StringWriter();
    	        yaml.dump(yamlModel, writer);     
    	        LOGGER.log(Level.INFO, "YAML file contents are {0}", writer);
    	        processYamlModel( yamlModel );
    	    }
    	    catch( Exception exc ) {
    	    	String msg = "Error while parsing YAML model file " + this.yamlFileFilePathAndName;
    	    	LOGGER.log( Level.WARNING, msg, exc );
    	    	throw new ProcessingException( msg, exc );
    	    }
    		
	    } 
    	else {
    		LOGGER.log(Level.SEVERE, "Invalid YAML model file provided - {0}", yamlFileFilePathAndName);
	    }
    }	

    /**
     * Helper method to process parsing a YAML Model
     * 
     * @param YamlModel 
     */
    public void processYamlModel( YamlModel yamlModel ) {
    	if ( yamlModel != null ) {
			List<ContainerObject> containerObjects = new ArrayList<>();
    		List<ClassObject> classObjects = new ArrayList<>();
			List<EnumClassObject> enumObjects = new ArrayList<>();

    		if ( yamlModel.containers != null )
    			yamlModel.containers.forEach(container -> containerObjects.add(processYamlContainer(container)) );

    		if ( yamlModel.entities != null )
    			yamlModel.entities.forEach(entity -> classObjects.add(processYamlEntity(entity)) );
    		
    		if ( yamlModel.enums != null ) 
    			yamlModel.enums.forEach(enumType -> enumObjects.add(processYamlEnum(enumType)) );

    		ModelParser.modelParser().setContainers(containerObjects);
    		ModelParser.modelParser().setClasses( classObjects );
    		ModelParser.modelParser().setEnums(enumObjects);
    	} 
    }
    
    /**
     * Process the entire contents of an YamlContainer.
     * 
     * @param YamlContainer 
     */
    protected ContainerObject processYamlContainer( YamlContainer yamlContainer ) {    	
		final ContainerObject containerObject = new ContainerObject();

    	if( yamlContainer != null ) {
        	LOGGER.log(Level.INFO, "{0}", yamlContainer.name );
			
			containerObject.setName( yamlContainer.name );
			containerObject.setHost( yamlContainer.host );
			containerObject.setPort( yamlContainer.port );

			List<ClassObject> classObjects 		= new ArrayList<>();
			List<EnumClassObject> enumObjects 	= new ArrayList<>();
			
    		if ( yamlContainer.entities != null )
    		    yamlContainer.entities.forEach(entity -> classObjects.add(processYamlEntity(entity)) );
    		
    		if( yamlContainer.enums != null )
    			yamlContainer.enums.forEach(enumType -> enumObjects.add(processYamlEnum(enumType)) );
    		
    		// apply the container as the containment context to each newly created class
    		classObjects.stream().forEach(classObject -> classObject.setContainmentContext(containerObject));
    		    		
			containerObject.setChildrenClassObjects( classObjects );
			containerObject.setChildrenEnumClassObjects( enumObjects );
    	}   
    	return containerObject;
    }
    
    protected ClassObject processYamlEntity( YamlEntity yamlEntity) {
		ClassObject classObject = new ClassObject();

    	if( yamlEntity != null ) {
        	LOGGER.log(Level.INFO, "{0}", yamlEntity.name);
			classObject.setName( yamlEntity.name );
			
    		if ( yamlEntity.data != null ) {
    			List<AttributeObject> attributes = new ArrayList<>();
    			yamlEntity.data.forEach(data -> attributes.add( processYamlData(data) ) );
    			classObject.setAttributes(attributes);
    		}
    		if( yamlEntity.relations != null ) {
    			List<AssociationEndObject> associations = new ArrayList<>();
    			yamlEntity.relations.forEach(relation -> associations.add(processYamlRelation(relation)) );
    			
    			classObject = ClassObject.addAssociationsHelper( classObject, associations );
    		}
    	}    	
    	return classObject;
    }

    protected AttributeObject processYamlData( YamlData yamlData) {
    	AttributeObject attributeObject = new AttributeObject();    	
    	if( yamlData != null ) {
        	LOGGER.log(Level.INFO, "{0}", yamlData.name );
        	attributeObject.setName(yamlData.name);
        	attributeObject.setType(yamlData.type);
        	attributeObject.setDefaultValue(yamlData.defaultValue);
    	}    	
    	return attributeObject;
    }

    protected AssociationEndObject processYamlRelation( YamlRelation yamlRelation) {
    	AssociationEndObject associationEndObject = new AssociationEndObject();
    	if( yamlRelation != null ) {    		
        	LOGGER.log(Level.INFO, "{0}", yamlRelation.name);
        	associationEndObject = AssociationEndObject.createBothEndsHelper(yamlRelation.name, 
        													yamlRelation.type, 
        													yamlRelation.multiplicity, 
        													yamlRelation.navigableRoleName);
    	}    	
    	return associationEndObject;
    }

    protected EnumClassObject processYamlEnum( YamlEnum yamlEnum) {
    	ClassObject innerClassObject = new ClassObject();

    	if( yamlEnum != null ) {
        	LOGGER.log(Level.INFO, "YamlEnum is {0}", yamlEnum.name);
    		if ( yamlEnum.data != null ) {
    			innerClassObject.setName( yamlEnum.name );
    			List<AttributeObject> attributes = new ArrayList<>();
    			yamlEnum.data.forEach(data -> attributes.add( processYamlData(data) ) );
    			innerClassObject.setAttributes( attributes );
    		}
    	}
    	
    	return new EnumClassObject(innerClassObject);
    }

    /**
     * Notifies each child in the model that loading has come to an end.
     * 
     * This method should be moved to a more central location.
     */
    protected void finishLoading() {
    	for( BaseModelObject baseModelObject : ModelParser.modelParser().getAllInHierarchy() ) {
    		baseModelObject.finishLoading();
    	}
    }

    // attributes
    private String yamlFileFilePathAndName 	= null;
	private static final Logger LOGGER 		= Logger.getLogger(YamlParser.class.getName());

	/** inner classes for Yaml Model Support **/
	public static class YamlModel {
		public YamlModel() { 
			containers	= null;
			entities	= null;
			enums		= null;
		}
		
		public List<YamlContainer> getContainers() { return containers; }
		public void setContainers( List<YamlContainer> containers ) { this.containers = containers; }

		public List<YamlEntity> getEntities() { return entities; }
		public void setEntities( List<YamlEntity> entities ) { this.entities = entities; }

		public List<YamlEnum> getEnums() { return enums; }
		public void setEnums( List<YamlEnum> enums) { this.enums = enums; }

		protected List<YamlContainer> containers;
		protected List<YamlEntity> entities;
		protected List<YamlEnum> enums;
	}
	
	public static class YamlContainer {
		public YamlContainer() { 
			name		= null;
			port		= null;
			entities	= null;
			enums		= null;
			host 		= "localhost";
		}

		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }
		
		protected String name;
		protected String host;
		protected String port;
		protected List<YamlEntity> entities;
		protected List<YamlEnum> enums;
	}
	
	public static class YamlEntity {
		public YamlEntity() { 
			name		= null; 
			data		= null;
			relations	= null;
		}
		
		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }
		
		public List<YamlData> getData() { return data; }
		public void setData( List<YamlData> data ) { this.data = data; }
		
		public List<YamlRelation> getRelations() { return relations; }
		public void setRelations( List<YamlRelation> relations ) { this.relations = relations; }
		
		protected String name; 
		protected List<YamlData> data;
		protected List<YamlRelation> relations;
	}
	
	public static class YamlData {
		public YamlData() { 
			name 			= null;
			type 			= "String";
			defaultValue 	= "null";
		}

		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }

		public String getType() { return type; }
		public void setType( String type ) { this.type = type; }

		public String getDefaultValue() { return defaultValue; }
		public void setDefaultValue( String defaultValue ) { this.defaultValue = defaultValue; }

		protected String name;
		protected String type;
		protected String defaultValue;
	}
	
	public static class YamlRelation {
		public YamlRelation() {
			name = null;
			type = null;
			navigableRoleName = null;
			multiplicity = "OneToOne";
		}

		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }

		public String getType() { return type; }
		public void setType( String type ) { this.type = type; }

		public String getMultiplicity() { return multiplicity; }
		public void setMultiplicity( String multiplicity ) { this.multiplicity = multiplicity; }

		public String getNavigableRoleName() { return navigableRoleName; }
		public void setNavigableRoleName( String navigableRoleName ) { this.navigableRoleName = navigableRoleName; }

		protected String name;
		protected String type;
		protected String navigableRoleName;
		protected String multiplicity;		
	}
	
	public static class YamlEnum {
		public YamlEnum() {
			name = null;
			data = null;
		}

		public String getName() { return name; }
		public void setName( String name ) { this.name = name; }

		public List<YamlData> getData() { return data; }
		public void setData( List<YamlData> data ) { this.data = data; }

		protected String name;
		protected List<YamlData> data;
	}
}

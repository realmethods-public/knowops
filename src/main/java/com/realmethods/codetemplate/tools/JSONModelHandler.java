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
package com.realmethods.codetemplate.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.reflect.Type;

import com.google.gson.*;
import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.container.*;
import com.realmethods.codetemplate.model.subsystem.*;
import com.realmethods.codetemplate.model.component.*;
import com.realmethods.codetemplate.model.classes.*;
import com.realmethods.codetemplate.model.classes.enumerate.*;
import com.realmethods.codetemplate.model.association.*;
import com.realmethods.codetemplate.model.attribute.*;
import com.realmethods.codetemplate.model.method.*;
import com.realmethods.codetemplate.parser.*;
import com.realmethods.entity.*;

/**
 * Helper class used to turn the parsed UML model into a JSON structure. Defines
 * a set of serializer used to apply the relevant fields to make up a
 * well-formed model structure, that accurately represents the UML model itself
 * 
 * @author realMethods, Inc.
 * 
 */
public class JSONModelHandler {
	
	public JSONModelHandler() {
		gsonBuilder.registerTypeAdapter(ContainerObject.class, new ContainerSerializer());

		gsonBuilder.registerTypeAdapter(NodeSubsystemObject.class, new SubsystemSerializer());
		gsonBuilder.registerTypeAdapter(SubsystemObject.class, new SubsystemSerializer());

		gsonBuilder.registerTypeAdapter(NodeComponentObject.class, new ComponentSerializer());
		gsonBuilder.registerTypeAdapter(ComponentObject.class, new ComponentSerializer());

		gsonBuilder.registerTypeAdapter(NodeClassObject.class, new ClassSerializer());
		gsonBuilder.registerTypeAdapter(InterfaceClassObject.class, new ClassSerializer());
		gsonBuilder.registerTypeAdapter(ClassClassObject.class, new ClassSerializer());
		gsonBuilder.registerTypeAdapter(EcoreClassObject.class, new ClassSerializer());
		gsonBuilder.registerTypeAdapter(ClassObject.class, new ClassSerializer());
		
		gsonBuilder.registerTypeAdapter(ClassEnumClassObject.class, new ClassSerializer());
		gsonBuilder.registerTypeAdapter(EnumClassObject.class, new EnumSerializer());
		gsonBuilder.registerTypeAdapter(EcoreEnumClassObject.class, new EnumSerializer());

		gsonBuilder.registerTypeAdapter(NodeAttributeObject.class, new AttributeObjectSerializer());
		gsonBuilder.registerTypeAdapter(AggregateAttributeObject.class, new AttributeObjectSerializer());
		gsonBuilder.registerTypeAdapter(AssociationAttributeObject.class, new AttributeObjectSerializer());
		gsonBuilder.registerTypeAdapter(CompositeAttributeObject.class, new AttributeObjectSerializer());
		gsonBuilder.registerTypeAdapter(AttributeObject.class, new AttributeObjectSerializer());
		gsonBuilder.registerTypeAdapter(ClassAttributeObject.class, new AttributeObjectSerializer());
		gsonBuilder.registerTypeAdapter(EcoreAttributeObject.class, new AttributeObjectSerializer());

		gsonBuilder.registerTypeAdapter(NodeAssociationEndObject.class, new AssociationEndObjectSerializer());
		gsonBuilder.registerTypeAdapter(ClassAssociationEndObject.class, new AssociationEndObjectSerializer());
		gsonBuilder.registerTypeAdapter(AssociationEndObject.class, new AssociationEndObjectSerializer());
		gsonBuilder.registerTypeAdapter(EcoreAssociationEndObject.class, new AssociationEndObjectSerializer());

		gsonBuilder.registerTypeAdapter(NodeMethodObject.class, new MethodSerializer());
		gsonBuilder.registerTypeAdapter(ClassMethodObject.class, new MethodSerializer());
		gsonBuilder.registerTypeAdapter(EcoreMethodObject.class, new MethodSerializer());
		gsonBuilder.registerTypeAdapter(MethodObject.class, new MethodSerializer());
		gsonBuilder.registerTypeAdapter(MethodArgs.class, new MethodArgsSerializer());
		gsonBuilder.registerTypeAdapter(MethodArg.class, new MethodArgSerializer());
		
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<NodeSubsystemObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<NodeComponentObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<NodeClassObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<EcoreClassObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<ClassClassObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<NodeAttributeObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<EcoreAttributeObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<ClassAttributeObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<NodeAssociationEndObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<AssociationEndObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<EcoreAssociationEndObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<ClassAssociationEndObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<NodeMethodObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<EcoreMethodObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<EnumClassObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<EcoreEnumClassObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<ClassEnumClassObject>());
		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<MethodArg>());

		gsonBuilder.registerTypeAdapter(List.class, new ComponentListSerializer<InterfaceClassObject>());
		
	}

	/**
	 * Loads a goFramework Generation-Time Model from a jsonFile.
	 * 
	 * @param jsonFile
	 */
	public void loadModelData( String jsonFile )
	{
		Gson gson 				= gsonBuilder.create();
		final String msgPrefix 	= "JSONModelHandler.loadModelData() - ";

		try {
			ModelData modelData = gson.fromJson(new java.io.FileReader( new java.io.File(jsonFile) ), ModelData.class );
			// assign modelData to the current threads ModelParser
			if ( modelData.containers != null )
				ModelParser.modelParser().setContainers( modelData.containers);
			
			if ( modelData.subsystems != null )
				ModelParser.modelParser().setSubsystems( modelData.subsystems );
			
			if ( modelData.components != null )			
				ModelParser.modelParser().setComponents( modelData.components );
			
			if ( modelData.classes != null )
				ModelParser.modelParser().setClasses( modelData.classes );
			
			if ( modelData.interfaces != null )
				ModelParser.modelParser().setInterfaces( modelData.interfaces );
			
			if ( modelData.services != null )
				ModelParser.modelParser().setServices( modelData.services );
			
			if ( modelData.enums != null )
				ModelParser.modelParser().setEnums( modelData.enums );
			
			// notify model loading is complete
			ModelParser.modelParser().postModelLoading();
			
			LOGGER.log(Level.INFO, () -> msgPrefix + ModelParser.modelParser().getContainers() );
			LOGGER.log(Level.INFO, () -> msgPrefix + ModelParser.modelParser().getSubsystems() );
			LOGGER.log(Level.INFO, () -> msgPrefix + ModelParser.modelParser().getComponents() );
			LOGGER.log(Level.INFO, () -> msgPrefix + ModelParser.modelParser().getClasses() );
			LOGGER.log(Level.INFO, () -> msgPrefix + ModelParser.modelParser().getEnums() );
			
		} catch (Exception exc) {
			LOGGER.log(Level.WARNING, "JSONModelHandler.loadModelData()", exc);
		}
	}
	
	/**
	 * Places the already loaded model into a single JSON object and returns it's
	 * string representation
	 * 
	 * @return
	 */
	public String json() {

		Gson gson = gsonBuilder.create();
		String retVal = null;

		try {
			retVal = gson.toJson(new ModelData());
		} catch (Exception exc) {
			LOGGER.log(Level.SEVERE, "Failed to load the model into a JSON object", exc);
		}
		return retVal;
	}

	/*
	 * Simple method to ensure null String is avoided, replaced with an empty one
	 * 
	 * @param	String
	 * @return	String
	 */
	private String eval(String in) {
		if (in == null || "[]".equals(in)) 
			in = "";
		return in;
	}

	// *******************************
	// inner classes
	// *******************************

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of ComponentObjects
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */
	public class ComponentListSerializer<E> implements JsonSerializer<List<E>>/*, JsonDeserializer<Collection<E>> */{
		public JsonElement serialize(List<E> components, Type typeOfSrc, JsonSerializationContext context) {
			JsonArray result = new JsonArray();

			for (E component : components)
				result.add(context.serialize(component));

			return result;
		}		
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of ContainerObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */
	public class ContainerSerializer implements JsonSerializer<ContainerObject>, JsonDeserializer<ContainerObject> {
		
		public JsonElement serialize(ContainerObject containerObject, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(containerObject.getName()));
			result.add("host", new JsonPrimitive(eval(containerObject.getHost())));
			result.add("port", new JsonPrimitive(eval(containerObject.getPort())));

			result.add(CLASSES, context.serialize(containerObject.getChildrenClassObjects()));
			result.add("enums", context.serialize(containerObject.getChildrenEnumClassObjects()));
			result.add("services", context.serialize(containerObject.getChildrenServiceObjects()));
			result.add("dtos", context.serialize(containerObject.getChildrenDTOObjects()));
			
			return result;
		}
		
		public ContainerObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			 {
			
			ContainerObject containerObject = new ContainerObject();
			JsonObject jsonObject = json.getAsJsonObject();
			
			containerObject.setName( deserializerHelper( jsonObject, NAME, true ) );
			containerObject.setHost( deserializerHelper( jsonObject, "host", false ) );
			containerObject.setPort( deserializerHelper( jsonObject, "port", false ) );

			containerObject.setChildrenClassObjects( listHelper((ClassObject[])context.deserialize( jsonObject.get(CLASSES), ClassObject[].class ) ) );
			containerObject.setChildrenEnumClassObjects( listHelper((EnumClassObject[])context.deserialize( jsonObject.get("enums"), EnumClassObject[].class ) ) );
			containerObject.setChildrenServiceObjects( listHelper((ClassObject[])context.deserialize( jsonObject.get("services"), ClassObject[].class ) ) );
			containerObject.setChildrenDTOObjects( listHelper((ClassObject[])context.deserialize( jsonObject.get("dtos"), ClassObject[].class ) ) );
			
			return containerObject;
		}
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of SubsystemObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */
	public class SubsystemSerializer implements JsonSerializer<SubsystemObject>, JsonDeserializer<SubsystemObject> {
		
		public JsonElement serialize(SubsystemObject subsystemObject, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(subsystemObject.getName()));
			result.add(PARENT_NAME, new JsonPrimitive(eval(subsystemObject.getParentName())));
			result.add(PACKAGE_NAME, new JsonPrimitive(eval(subsystemObject.getPackageName())));
			result.add(INTERFACES, new JsonPrimitive(eval(subsystemObject.getSuperTypes().toString())));

			result.add("subsystems", context.serialize(subsystemObject.getChildrenSubsystemObjects()));
			result.add(COMPONENTS, context.serialize(subsystemObject.getChildrenComponentObjects()));
			result.add(CLASSES, context.serialize(subsystemObject.getChildrenClassObjects()));
			result.add(METHODS, context.serialize(subsystemObject.getBusinessMethods()));

			return result;
		}
		
		public SubsystemObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
			 {
			
			SubsystemObject subsystemObject = new SubsystemObject();
			JsonObject jsonObject = json.getAsJsonObject();

			subsystemObject.setName( deserializerHelper( jsonObject, NAME, true ) );
			subsystemObject.setParentName( deserializerHelper( jsonObject, PARENT_NAME, false ) );
			
			subsystemObject.setChildrenSubsystemObjects( listHelper( (SubsystemObject[])context.deserialize( jsonObject.get("subsystems"), SubsystemObject[].class ) ) );
			subsystemObject.setChildrenComponentObjects( listHelper( (ComponentObject[])context.deserialize( jsonObject.get(COMPONENTS), ComponentObject[].class ) ) );
			subsystemObject.setChildrenClassObjects( listHelper((ClassObject[])context.deserialize( jsonObject.get(CLASSES), ClassObject[].class ) ) );
			
			return subsystemObject;
		}
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of ComponentObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */
	public class ComponentSerializer implements JsonSerializer<NodeComponentObject>, JsonDeserializer<ComponentObject> {
		public JsonElement serialize(NodeComponentObject componentObject, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(componentObject.getName()));
			result.add(PARENT_NAME, new JsonPrimitive(eval(componentObject.getParentName())));
			result.add(PACKAGE_NAME, new JsonPrimitive(eval(componentObject.getPackageName())));
			result.add(INTERFACES, new JsonPrimitive(!componentObject.getSuperTypes().isEmpty() 
											? ""
											: eval(componentObject.getSuperTypes().toString())));

			result.add(COMPONENTS, context.serialize(componentObject.getChildrenComponentObjects()));
			result.add(CLASSES, context.serialize(componentObject.getChildrenClassObjects()));
			result.add(METHODS, context.serialize(componentObject.getBusinessMethods()));

			return result;
		}

		public ComponentObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {
			ComponentObject componentObject = new ComponentObject();
			JsonObject jsonObject = json.getAsJsonObject();
			
			componentObject.setName( deserializerHelper( jsonObject, NAME, true ) );
			componentObject.setParentName( deserializerHelper( jsonObject, PARENT_NAME, false ) );
					
			if ( jsonObject.get(INTERFACES) != null )
			{
				StringTokenizer tokenizer = new StringTokenizer( jsonObject.get(INTERFACES).getAsString(), "," );
				List<String> interfaces = new ArrayList<>();
				while( tokenizer.hasMoreTokens() )
					interfaces.add( tokenizer.nextToken().trim() );
				
				componentObject.setSuperTypes( interfaces );
			}

			componentObject.setChildrenComponentObjects( listHelper( (ComponentObject[])context.deserialize( jsonObject.get(COMPONENTS), ComponentObject[].class ) ) );
			componentObject.setChildrenClassObjects( listHelper( (ClassObject[])context.deserialize( jsonObject.get(CLASSES), ClassObject[].class ) ) );

			return componentObject;
		}
		
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of ClassObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */
	public class ClassSerializer implements JsonSerializer<ClassObject>, JsonDeserializer<ClassObject>  {
		public JsonElement serialize(ClassObject classObject, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(classObject.getName()));
			result.add(PARENT_NAME, new JsonPrimitive(eval(classObject.getParentName())));
			result.add(PACKAGE_NAME, new JsonPrimitive(eval(classObject.getPackageName())));
			result.add(INTERFACES, new JsonPrimitive(eval(classObject.getSuperTypes().toString())));
			
			List<AttributeObject> attributes = classObject.getDirectAttributes(true);
			attributes.addAll( classObject.getAllComposites() );
					
			result.add(ATTRIBUTES, context.serialize( attributes ) );
			result.add(ASSOCIATIONS, context.serialize(classObject.getAssociations()));
			result.add(METHODS, context.serialize(classObject.getBusinessMethods()));

			return result;
		}

		
		public ClassObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {
			ClassObject classObject = new ClassObject();
			JsonObject jsonObject = json.getAsJsonObject();
			
			if ( jsonObject.get(NAME) != null )
				classObject.setName( deserializerHelper( jsonObject, NAME, true ) );
			
			if ( jsonObject.get(PARENT_NAME) != null )
				classObject.setParentName( deserializerHelper( jsonObject, PARENT_NAME, false ) );
						
			if ( jsonObject.get(INTERFACES) != null )
			{
				StringTokenizer tokenizer = new StringTokenizer( jsonObject.get(INTERFACES).getAsString(), "," );
				List<String> interfaces = new ArrayList<>();
				while( tokenizer.hasMoreTokens() )
					interfaces.add( tokenizer.nextToken().trim() );
				
				classObject.setSuperTypes( interfaces );
			}
			
			classObject.setAttributes( listHelper( (AttributeObject[])context.deserialize(jsonObject.get(ATTRIBUTES), AttributeObject[].class ) ) );
			List<AssociationEndObject> associations = listHelper( (AssociationEndObject[])context.deserialize(jsonObject.get(ASSOCIATIONS), AssociationEndObject[].class ) );
			
			classObject = ClassObject.addAssociationsHelper( classObject, associations );
			
			classObject.setBusinessMethods( listHelper( (MethodObject[])context.deserialize(jsonObject.get(METHODS), MethodObject[].class ) ) );
			
			return classObject;
		}
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of AssociationEndObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */	
	public class AssociationEndObjectSerializer implements JsonSerializer<AssociationEndObject>, JsonDeserializer<AssociationEndObject> {
		public JsonElement serialize(AssociationEndObject associationEndObject, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(associationEndObject.getRoleName()));
			result.add("type", new JsonPrimitive(associationEndObject.getType()));
			result.add(MULTIPLICITY, new JsonPrimitive(associationEndObject.getMultiplicityAsString()));

			if ( associationEndObject.hasNavigableSiblingAssociationEndObject() )
				result.add(NAVIGABLE, new JsonPrimitive(associationEndObject.getSiblingAssociationEndObject().getRoleName()));
			
			return result;
		}

		public AssociationEndObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
		{
			JsonObject jsonObject = json.getAsJsonObject();

 			String type 		= deserializerHelper( jsonObject, "type", true );
 			String roleName 	= jsonObject.get(NAME).getAsString();
			String multiplicity = deserializerHelper( jsonObject, MULTIPLICITY, true ); 
 			String navigable	= deserializerHelper( jsonObject, MULTIPLICITY, false );	
			return( AssociationEndObject.createBothEndsHelper(roleName, type, multiplicity, navigable ) );
		}
		
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of AttributeObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */	
	public class AttributeObjectSerializer implements JsonSerializer<AttributeObject>, JsonDeserializer<AttributeObject> {
		public JsonElement serialize(AttributeObject attributeObject, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(attributeObject.getName()));
			result.add("type", new JsonPrimitive(attributeObject.getType()));
			result.add("visibility", new JsonPrimitive(attributeObject.getVisibility()));
			result.add(PRIMARY_KEY, new JsonPrimitive( attributeObject.isPrimaryKey() ));
			result.add(CAN_BE_NULL, new JsonPrimitive( attributeObject.canBeNull() ));
			
			String defValue = attributeObject.getDefaultValue();
			if (defValue == null)
				defValue = "";

			result.add(DEFAULTVALUE, new JsonPrimitive(defValue));

			return result;
		}

		public AttributeObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {
			AttributeObject attributeObject = new AttributeObject();
			JsonObject jsonObject = json.getAsJsonObject();

			attributeObject.setName( deserializerHelper( jsonObject, NAME, true ) );
			attributeObject.setType( deserializerHelper( jsonObject, "type", true ) );
			attributeObject.setDefaultValue( deserializerHelper( jsonObject, DEFAULTVALUE, false ) );
			attributeObject.setDefaultValue( deserializerHelper( jsonObject, DEFAULTVALUE, false ) );
			
			if ( jsonObject.get(PRIMARY_KEY) != null && 
					jsonObject.get(PRIMARY_KEY).getAsString().equalsIgnoreCase("true") ) {	
					attributeObject.isPrimaryKey( true );
			}
			
			if ( jsonObject.get(CAN_BE_NULL) != null && 
					jsonObject.get(CAN_BE_NULL).getAsString().equalsIgnoreCase("false") ) {	
					attributeObject.canBeNull( false );
			}
			
			return attributeObject;
		}
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of MethodObjects.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */	
	public class MethodSerializer implements JsonSerializer<MethodObject>, JsonDeserializer<MethodObject>  {
		public JsonElement serialize(MethodObject methodObject, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(methodObject.getAsFormattedString()));
			return result;
		}
		
		public MethodObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {
			MethodObject methodObject = new MethodObject();
			JsonObject jsonObject = json.getAsJsonObject();
			
			methodObject.setName( deserializerHelper( jsonObject, NAME, true ) );
			
			if ( jsonObject.get("visiblity") != null )
				methodObject.setVisibility( deserializerHelper( jsonObject, "visiblity", false ) );
			
			if ( jsonObject.get("returnType") != null )
				methodObject.setReturnType( deserializerHelper( jsonObject, "returnType", false ) );
			
			methodObject.setArguments( (MethodArgs)context.deserialize(jsonObject.get("params"), MethodArgs.class ) );
			
			return methodObject;
		}
		
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of MethodArgs.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */		
	public class MethodArgsSerializer implements JsonSerializer<MethodArgs>, JsonDeserializer<MethodArgs>  {
		public JsonElement serialize(MethodArgs methodArgs, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add("input", context.serialize( methodArgs.getArgs() ) );
			return result;
		}
		public MethodArgs deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {
			MethodArgs methodArgs = new MethodArgs();
			JsonObject jsonObject = json.getAsJsonObject();
			
			methodArgs.setArgs(listHelper( (MethodArg[])context.deserialize( jsonObject.get("input"), MethodArg[].class ) ));
			
			return methodArgs;
		}
		
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of a MethodArg.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */		
	public class MethodArgSerializer implements JsonSerializer<MethodArg>, JsonDeserializer<MethodArg>  {
		public JsonElement serialize(MethodArg methodArg, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(methodArg.getName()));
			result.add("type", new JsonPrimitive(methodArg.getType()));
			return result;
		}
		public MethodArg deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {
			MethodArg methodArg = new MethodArg();
			JsonObject jsonObject = json.getAsJsonObject();
			
			methodArg.setName( deserializerHelper( jsonObject, NAME, true ) );
			methodArg.setType( deserializerHelper( jsonObject, "type", true ) );
			
			return methodArg;
		}
	}

	/** 
	 * Inner templatized class to handle the serialization and de-serialization of EnumClassObject.
	 * 
	 * @author realMethods, Inc.
	 *
	 * @param <E>
	 */		
	public class EnumSerializer implements JsonSerializer<EnumClassObject>, JsonDeserializer<EnumClassObject> {
		public JsonElement serialize(EnumClassObject enumClassObject, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject result = new JsonObject();

			result.add(NAME, new JsonPrimitive(enumClassObject.getName()));
			result.add(ATTRIBUTES, context.serialize(enumClassObject.getAttributes()));

			return result;
		}

		public EnumClassObject deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
				 {

			JsonObject jsonObject = json.getAsJsonObject();

			// create an innerClassObject then bind with an EnumClassObject
			ClassObject innerClassObject 	= context.deserialize( jsonObject, ClassObject.class );						
			return new EnumClassObject(innerClassObject);
		}

	}

	/**
	 * Helper method to turn an array of T objects into a List of T objects.
	 * 
	 * 
	 * @param arrayIn
	 * @return <T> List<T>
	 */
	protected <T> List<T> listHelper( T[] arrayIn )
	{
		List<T> arrayOut = new ArrayList<>();
	
		if ( arrayIn != null ) {
			
			for ( T t : arrayIn ) {
				arrayOut.add( t );
			}
		}
		
		return( arrayOut );
	}

	/**
	 * Inner class.  Model Data holder
	 * @author realMethods, Inc.
	 *
	 */
	public class ModelData {
		ModelData() {

			containers = ModelParser.modelParser().getContainers();
			Collections.sort(containers, new BaseModelNameComparator());

			subsystems = ModelParser.modelParser().getSubsystems();
			Collections.sort(subsystems, new BaseModelNameComparator());

			subsystems.forEach( subSystemObject ->  
				LOGGER.log( Level.INFO, () -> 
					"JSONModelHandler.ModelData() - root system object is : " + subSystemObject.getName()
				)
			);

			components = ModelParser.modelParser().getComponents();
			Collections.sort(components, new BaseModelNameComparator());

			classes = ModelParser.modelParser().getClasses();
			Collections.sort(classes, new BaseModelNameComparator());

			interfaces = ModelParser.modelParser().getInterfaces();
			Collections.sort(interfaces, new BaseModelNameComparator());

			services = ModelParser.modelParser().getServices();
			Collections.sort(services, new BaseModelNameComparator());

			enums = ModelParser.modelParser().getEnums();
			Collections.sort(enums, new BaseModelNameComparator());

		}

		// attributes

		protected List<ContainerObject> containers;
		protected List<SubsystemObject> subsystems;
		protected List<ComponentObject> components;
		protected List<ClassObject> classes;
		protected List<ClassObject> interfaces;
		protected List<ClassObject> services;
		protected List<EnumClassObject> enums;

	}

	protected String deserializerHelper( JsonObject object, String fieldName, boolean required )
			
	{
		String val = "";
		
		if ( object != null )
		{
			JsonElement element = object.get( fieldName );
			
			if ( element != null ) {
				val = element.getAsString();
				if ( required && (val == null || val.length() == 0) )
					throw new JsonParseException( "Required field " + fieldName + " is empty" );
			}
			else if ( required )
				throw new JsonParseException( "Required field " + fieldName + " has not been provided" );
		}
		
		return( val );
	}

	/**
	 * Inner class.  Comparator to assist in sorting BaseModelObjects
	 * 
	 * @author realMethods, Inc.
	 *
	 */
	class BaseModelNameComparator implements java.util.Comparator<BaseModelObject> {
		public int compare(BaseModelObject a, BaseModelObject b) {
			return (a.getName().compareTo(b.getName()));
		}
	}

	// atributes
	protected GsonBuilder gsonBuilder 				= new GsonBuilder();
	private static final String NAME				= "name";
	private static final String PARENT_NAME			= "parentName";
	private static final String PACKAGE_NAME		= "packageName";
	private static final String INTERFACES			= "interfaces";
	private static final String COMPONENTS			= "components";
	private static final String ASSOCIATIONS		= "associations";
	private static final String ATTRIBUTES			= "attributes";
	private static final String CLASSES				= "classes";
	private static final String METHODS				= "methods";
	private static final String PRIMARY_KEY			= "primarykey";
	private static final String NAVIGABLE			= "navigable";
	private static final String CAN_BE_NULL			= "canBeNull";	
	private static final String DEFAULTVALUE		= "defaultValue";
	private static final String MULTIPLICITY		= "multiplicity";
	private static final Logger LOGGER 				= Logger.getLogger(ReverseEngineerJava.class.getName());

}

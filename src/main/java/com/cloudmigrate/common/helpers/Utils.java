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
package com.cloudmigrate.common.helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.foundational.common.FrameworkBaseObject;

/**
 * Utility class with helper methods for goFramework code generation
 * internally and externally within custom template files.
 * 
 * @author realMethods, Inc.
 * 
 */
public class Utils extends FrameworkBaseObject {

	/**
	 * default constructor
	 */
	public Utils() {
		// no_op
	}

	/**
	 * Removes white space and periods as in dot notation and returns the result
	 * 
	 * @param src
	 * @return
	 */
	public static String noWhiteSpaceNoDots(String src) {
		String str = noWhiteSpace(src);

		CharSequence s1 = ".";
		CharSequence s2 = "_";
		// replace sequence s1 with s2

		return (str.replace(s1, s2));
	}

	/**
	 * Removes all whitespace
	 * 
	 * @param src
	 * @return
	 */
	public static String noWhiteSpace(String src) {
		return src.replaceAll(" ", "");
	}

	/**
	 * Forces the first letter to be lowercase.
	 * 
	 * @param target
	 * @return
	 */
	public static String lowercaseFirstLetter(String target) {
		StringBuilder buffer = new StringBuilder();
		buffer.append(Character.toLowerCase(target.charAt(0)));
		buffer.append(target.substring(1, target.length()));
		return buffer.toString();
	}

	/**
	 * Captalizes the first letter.
	 * 
	 * @param target
	 * @return
	 */
	public static String capitalizeFirstLetter(String target) {
		StringBuilder buffer = new StringBuilder();
		if (target != null && target.length() > 0) {
			buffer.append(Character.toUpperCase(target.charAt(0)));
			if (target.length() > 1)
				buffer.append(target.substring(1, target.length()));
		}

		return buffer.toString();
	}

	/**
	 * Google App Engine handling, converting a date type to a java.util.Date
	 * 
	 * @param type
	 * @return
	 */
	public static String convertTypeForGoogleAppEngine(final String type) {
		if (type.equals(CALENDAR) || type.equals("Date") || type.equals("java.sql.Calendar")
				|| type.equals(JAVA_SQL_DATE)) {
			LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.util.Date.");
			return "java.util.Date";
		} else {
			return convertType(type);
		}
	}

	/**
	 * Special handler to coerce data types to what Hibernate can handle.
	 * 
	 * @param type
	 * @return
	 */
	public static String convertTypeForHibernate(String type) {
		return (convertType(type));
	}

	/**
	 * Each supported model has slight variances on the types declared and
	 * supported. Convert those types to known types that realMethos can handle.
	 * 
	 * @param type
	 * @return
	 */
	public static String convertType(final String type) {

		if ( type == null )
			return STRING;

		switch( type ) {
			case "EString": 
				return STRING;
			case CALENDAR: 
			case "Date": 
			case "java.sql.Calendar":
			case JAVA_SQL_DATE: 
			case "EDate" :
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.util.Date.");
				return "java.util.Date";
			case "Time":
			case "ETime":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.sql.Time.");
				return "java.sql.Time";
			case "Timestamp":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.sql.Timestamp.");
				return "java.sql.Timestamp";
			case "boolean":
			case "EBoolean":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.lang.Boolean.");
				return "java.lang.Boolean";
			case "int":
			case "EInt":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.lang.Integer.");
				return "java.lang.Integer";
			case "long":
			case "ELong":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.lang.Long.");
				return "java.lang.Long";
			case SHORT:
			case "EShort": 
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.lang.Short.");
				return "java.lang.Short";
			case FLOAT:
			case "EFloat":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.lang.Float.");
				return "java.lang.Float";
			case DOUBLE:
			case "EDouble":
				LOGGER.log(Level.INFO, () -> CHANGING_TYPE_FROM + type + " to java.lang.Double.");
				return "java.lang.Double";
			default:
				return type;
		}
	}
	
	public static String reconcileType(String type) {
		String reconcileType = convertType(type);
		if (reconcileType == type) {
			reconcileType = capitalizeFirstLetter(type);
		}
		return reconcileType;
	}

	/**
	 * Standard java types converted XML Types
	 * 
	 * @param type
	 * @return
	 */
	public static String getTypeAsXMLType(final String type) {

		if (type.endsWith(".Date"))
			return "XMLType.XSD_DATE";
		else if (type.endsWith("[]") || type.equals("Vector"))
			return "XMLType.SOAP_ARRAY";
		
		switch( type ) {
			case STRING: 	return XMLTYPE_XSD_STRING;
			case INTEGER:	return"XMLType.XSD_INTEGER";
			case "Long":	return"XMLType.XSD_LONG";
			case DOUBLE:	return"XMLType.XSD_DOUBLE";
			case FLOAT:		return"XMLType.XSD_FLOAT";
			case SHORT:		return"XMLType.XSD_SHORT";
			case BOOLEAN: 	return"XMLType.XSD_BOOLEAN";
			case "Collection": 
				LOGGER.log(Level.INFO, "Collections as args or return types do not work well with Axis.  Consider an object array or Vector.");
				return "XMLType.SOAP_ARRAY";
			default :
				final String msg = "Unable to map type " + type + " to a well-defined XMLType.   Will apply XML_String.";
				LOGGER.log(Level.INFO, msg);
				return XMLTYPE_XSD_STRING;
		}
	}

	/**
	 * Turns a String into a phrase.
	 * 
	 * @param value
	 * @return
	 */
	public static String turnIntoPhrase(String value) {
		StringBuilder phrase = new StringBuilder();
		String temp = capitalizeFirstLetter(value);
		int length = temp.length();

		for (int i = 0; i < length; i++) {
			char c = temp.charAt(i);
			if (Character.isUpperCase(c) && i > 0 && Character.isLowerCase(temp.charAt(i - 1)))
				phrase.append(' ');
			phrase.append(c);
		}

		return phrase.toString();
	}

	/**
	 * Builds a name using the format parentName.childName
	 * 
	 * @param parentName
	 * @param childName
	 * @return
	 */
	public static String buildName(String parentName, String childName) {
		return lowercaseFirstLetter(parentName) + "." + lowercaseFirstLetter(childName);
	}

	/**
	 * The Collection of supported data types.
	 * 
	 * @return
	 */
	public static Collection<String> getValidDataTypes() {
		Collection<String> types = new ArrayList<>();

		types.add(STRING);
		types.add(BOOLEAN);
		types.add(INTEGER);
		types.add("Long");
		types.add(SHORT);
		types.add(DOUBLE);
		types.add(FLOAT);
		types.add(JAVA_SQL_DATE);
		types.add("java.sql.Time");
		types.add("java.sql.Timestamp");

		return (types);
	}

	/**
	 * Returns the Collection of valid ata types as an array
	 * 
	 * @return
	 */
	public static String[] getValidDataTypesAsArray() {
		return getValidDataTypes().toArray(new String[] {});
	}

	/**
	 * Remove spaces
	 * 
	 * @param sourceString
	 * @return
	 */
	public static String removeSpaces(String sourceString) {
		return (sourceString.replace(" ", ""));
	}

	/**
	 * Write the XML content to the provided file
	 * 
	 * @param xml
	 * @param tmpLocalModelFile
	 * @throws IOException
	 */
	public static void writeXmlToFile(String xml, File tmpLocalModelFile) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(tmpLocalModelFile))){
			writer.write(xml);
		} 
	}

	public static String mapTypeToDotNetType( String type ) 
	{
		String changeToType = null;
		
		if ( type == null  )
			return "string";
		if (type.endsWith(INTEGER))
			return "int";
		if (type.endsWith("Long"))
			return "long";
		if (type.endsWith(SHORT))
			return SHORT;
		if (type.endsWith(FLOAT))
			return FLOAT;
		if (type.endsWith(DOUBLE))
			return DOUBLE;
		if (type.endsWith(DECIMAL))
			return DECIMAL;
		
		switch( type ) {
			case STRING: 
				return "string";
			case CALENDAR:
			case "Date":
			case "EDate": 
			case "Time":
			case "Timestamp":
				changeToType = "DateTime";
				break;
			case "boolean":
			case "EBoolean":
			case "BOOLEAN":
				changeToType = "bool";
				break;
			case "int":
			case "EInt":
				changeToType = "int";
				break;
			case "long":
			case "ELong":
				changeToType = "long";
				break;
			case "short":
			case "EShort":
				changeToType = "short";
				break;
			case "float":
			case "EFloat":
				changeToType = "float";
				break;
			case "double":
			case "EDouble":
				changeToType = "double";
				break;
			case DECIMAL:
			case "BigDecimal":
				changeToType = DECIMAL;
				break;
			default:
				changeToType = type;
		}
		
		return changeToType;
	}
		
	// attributes

	public static final String SPACES	 			= "                                              ";
	public static final String PK_SUFFIX 			= "_CM_PK";
	private static final String JAVA_SQL_DATE 		= "java.sql.Date";
	private static final String XMLTYPE_XSD_STRING 	= "XMLType.XSD_STRING";
	private static final String BOOLEAN 			= "Boolean";
	private static final String CALENDAR			= "Calendar";
	private static final String CHANGING_TYPE_FROM	= "Changing type from ";
	private static final String DOUBLE				= "Double";
	private static final String FLOAT				= "Float";
	private static final String INTEGER				= "Integer";
	private static final String SHORT				= "Short";
	private static final String STRING				= "String";
	private static final String DECIMAL				= "decimal";
	private static final Logger LOGGER 				= Logger.getLogger(Utils.class.getName());

}

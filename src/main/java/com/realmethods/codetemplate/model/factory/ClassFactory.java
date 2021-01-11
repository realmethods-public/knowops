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
package com.realmethods.codetemplate.model.factory;

import java.lang.reflect.Modifier;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EAnnotation;

import org.w3c.dom.Node;

import com.realmethods.codetemplate.model.*;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.classes.ClassClassObject;
import com.realmethods.codetemplate.model.classes.EcoreClassObject;
import com.realmethods.codetemplate.model.classes.NodeClassObject;
import com.realmethods.codetemplate.model.classes.enumerate.*;
import com.realmethods.codetemplate.xmi.*;
import com.realmethods.codetemplate.*;
import com.realmethods.common.helpers.Utils;

public class ClassFactory extends BaseModelFactory {
	
	/**
	 * intentionally protected
	 */
	protected ClassFactory()  {
		super();
	}

	/**
	 * Singleton factory pattern
	 * 
	 * @return
	 */
	public static ClassFactory getInstance() {
		if (self == null)
			self = new ClassFactory();

		return (self);
	}

	/**
	 * Creates a ClassObject using the input DOM node.
	 * 
	 * @param node
	 * @return
	 */
	public ClassObject createClassObject(Node node) {
		ClassObject classObject = new NodeClassObject(node);

		assignData(node, classObject);
		LOGGER.info( " - Done Class Factory Creating : "
										+ classObject.getName());	
		return (classObject);
	}

	/**
	 * Creates a ClassObject using the input Java Class.
	 * 
	 * @param theClass
	 * @return
	 */
	public ClassObject createInstance(Class theClass) {
		ClassObject classObject = null;

		try
		{
			if (!theClass.isEnum())
				classObject = new ClassClassObject(theClass);
			else
				classObject = new ClassEnumClassObject(theClass);
	
			assignName( classObject, theClass.getSimpleName() );
		
			// interface assignment
			Class[] interfaces 		= theClass.getInterfaces();
			List<String> list = new ArrayList<>();
			String interfaceName 	= null;
			
			for (int i = 0; i < interfaces.length; i++) 
			{
				interfaceName = interfaces[i].getSimpleName();
			
				if (interfaceName.equalsIgnoreCase("Serializable"))
					interfaceName = "java.io.Serializable";
	
				list.add(interfaceName);
			}
	
			// NO LONGER SUPPORT INTERFACES WHEN LOADING FROM POJOS
			assignInterfaces(classObject, list);
			assignAbstract(classObject, Modifier.isAbstract(theClass.getModifiers()));
			assignStereoType(classObject, "");
			assignDocumentation(classObject, "");
	
			if (theClass.getSuperclass() != null)
				assignParentName(classObject, theClass.getSuperclass().getSimpleName());
	
		}
		catch( Exception exc )
		{
			LOGGER.log(Level.WARNING, "ClassFactory.createInstance", exc );
		}
		
		return (classObject);
	}
	/**
	 * Creates a ClassObject using the input EClass.
	 * 
	 * @param eClass
	 * @return
	 */
	public ClassObject createInstance(EClass eClass) {
		EcoreClassObject classObject = new EcoreClassObject(eClass);

		assignName(classObject, Utils.removeSpaces(eClass.getName()));
		classObject.isInterface(eClass.isInterface());

		// interfaces assignment
		List<String> list = new ArrayList<>();
		Iterator iter 			= eClass.getESuperTypes().iterator();
		EClass eclass 			= null;
		StringBuilder pkgName 	= new StringBuilder();

		while (iter.hasNext()) 
		{
			eclass = ((EClass) iter.next());
			pkgName.append( eclass.getEPackage().getName() );
	
			if (pkgName.length() > 0)
				pkgName.append(".");
			else
				pkgName = new StringBuilder();

			pkgName.append( eclass.getName() );
			
			list.add(pkgName.toString());
		}

		assignInterfaces(classObject, list);
		assignAbstract(classObject, eClass.isAbstract());

		// package assignment
		StringBuilder pkg = new StringBuilder();
		EPackage ePackage = eClass.getEPackage();

		while (ePackage != null) {
			pkg.append(ePackage.getName());
			ePackage = ePackage.getESuperPackage();

			if (ePackage != null)
				pkg.append(" ");
		}
	
		// stereotype assignment
		String stereoType = null;
		EAnnotation eAnnotation = eClass.getEAnnotation("keywords");

		if (eAnnotation != null) 
			stereoType = eAnnotation.getDetails().values().iterator()
					.next();
		
		assignStereoType(classObject, stereoType);
		assignDocumentation(classObject, "");

		return (classObject);
	}

	/**
	 * Creates a ClassObject from an EMF EEnum.
	 * 
	 * @param eEnum
	 * @return
	 */
	public ClassObject createInstance(EEnum eEnum) {
		EcoreEnumClassObject classObject = new EcoreEnumClassObject(eEnum);

		assignName(classObject, Utils.capitalizeFirstLetter(eEnum.getName().trim()));

		// package assignment
		StringBuilder pkg = new StringBuilder();
		EPackage ePackage = eEnum.getEPackage();

		while (ePackage != null) {
			pkg.append(ePackage.getName());
			ePackage = ePackage.getESuperPackage();

			if (ePackage != null)
				pkg.append(" ");
		}

		assignStereoType(classObject, "enum");
		assignDocumentation(classObject, "");

		return (classObject);
	}


	// attributes
	private static ClassFactory self 	= null;
	private static final Logger LOGGER 	= Logger.getLogger(ClassFactory.class.getName());    	
	
}

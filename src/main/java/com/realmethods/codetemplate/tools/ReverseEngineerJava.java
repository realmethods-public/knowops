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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.nio.file.*;

import com.realmethods.api.PojoParams;
import com.realmethods.codetemplate.AppGenObject;
import com.realmethods.codetemplate.parser.*;
import com.realmethods.foundational.common.namespace.FrameworkNameSpace;
import com.realmethods.codetemplate.model.classes.ClassObject;
import com.realmethods.codetemplate.model.factory.*;

/**
 * Reverse engineer Java .class files from a directory structure into a
 * set of ClassObjects to then populate the realMethods meta-model for the
 * current thread.
 * 
 * @author realMethods, Inc.
 * 
 */
public class ReverseEngineerJava extends AppGenObject {
	
	/**
	 * Default constructor
	 */
	public ReverseEngineerJava() {
	}

	/**
	 * Constructor includes a PojoParams instance
	 * @param PojoParams
	 */
	public ReverseEngineerJava( PojoParams pojoParams ) {
		this.pojoParams = pojoParams;
	}

	/**
	 * Delegates internally to reverse(String classRootDirectory, String classSuffix) with 
	 * classSuffix="".
	 * @param classRootDirectory
	 * @return
	 */
	public Collection<ClassObject> reverse(String classRootDirectory) {
		return (reverse(classRootDirectory, ""));
	}

	/**
	 * Reverse engineers a collection of ClassObjects from .class files found
	 * recursively starting at the class root directory.
	 * 
	 * @param classRootDirectory
	 * @param clssSuffix
	 * @return
	 */
	public Collection<ClassObject> reverse(String classRootDirectory, String classSuffix) {
		classRootDir = classRootDirectory;

		final String msg = "pojoParams are " + pojoParams;
		LOGGER.info( msg );
		
		//////////////////////////////////////////////////////////////////////////////////////////
		// copy the class files to reverse engineer into the realMethods class path.
		//////////////////////////////////////////////////////////////////////////////////////////
		try {
			copyRevEngClassesIntoClassPath();
		} catch( Exception exc ) {
			LOGGER.log( Level.SEVERE, "ReverseEngineerJava.reverse", exc);
		}

		String className 				= null;
		Collection<ClassObject> classes = new ArrayList<>();
		ClassObject classObject 		= null;
		
		//////////////////////////////////////////////////////////////////////////////////////////
		// returns a collection of Java class files to iterator over
		//////////////////////////////////////////////////////////////////////////////////////////
		Collection<File> files 			= getJavaFiles(null);
		Iterator<File> iterator 		= files.iterator();

		if (files.isEmpty()) {
			LOGGER.log( Level.WARNING, () -> 
				"Java class file load failure. No class files were located under root "
				+ classRootDirectory + ".  Please recheck this directory.");
			return (classes);
		}
		
		//////////////////////////////////////////////////////////////////////////////////////////
		// iterate over each class file and reverse its contents
		//////////////////////////////////////////////////////////////////////////////////////////
		while (iterator.hasNext()) {
			classObject = reverse(iterator.next());

			if (classObject != null) {
				/////////////////////////////////////////////////////////////////////////////////
				// adjust the class name and remove the provided suffix
				/////////////////////////////////////////////////////////////////////////////////
				if (classSuffix != null && classSuffix.length() > 0) {
					className = classObject.getName();

					// remove it if there...
					if (className.endsWith(classSuffix)) {
						className = className.substring(0, className.lastIndexOf(classSuffix));
						classObject.setName(className);
					}
				}

				/////////////////////////////////////////////////////////////////////////////////
				// add the classObject to the Collection of ClassObjects.
				/////////////////////////////////////////////////////////////////////////////////
				classes.add(classObject);
			}
		}

		return (classes);
	}

	/**
	 * For the provided directory, recursively collects on files with the .class extension.
	 * 
	 * @param directory
	 * @return
	 */
	protected Collection<File> getJavaFiles(File directory) {
		Collection<File> files = new ArrayList<>();

		/////////////////////////////////////////////////////////
		// first pass in recursion it is null
        /////////////////////////////////////////////////////////
		if (directory == null) {
			directory = new File(classRootDir);
		}

		File javaFile 		= null;
		
		/////////////////////////////////////////////////////////
		// use the Java file filter to return only .class files
		// discovered on the input directory
        /////////////////////////////////////////////////////////		
		File[] javaFiles 	= directory.listFiles(new JavaFileFilter());

		if ( javaFiles != null ) {
			for (int i = 0; i < javaFiles.length; i++) {
				///////////////////////////////////////////
				// run down the root recursively
				///////////////////////////////////////////
				javaFile = javaFiles[i];
				if (javaFile.isDirectory()) {
					///////////////////////////////////////////
					// recurse on discovering a directory
					///////////////////////////////////////////
					files.addAll(getJavaFiles(javaFile));
				} else { 
					///////////////////////////////////////////
					// must be a file so add it directly
					///////////////////////////////////////////
					files.add(javaFile);
				}
			}
		}
				
		return (files);
	}

	/**
	 * Turns a Java .class file reference into a ClassObject
	 * 
	 * @param javaFile
	 * @return
	 */
	protected ClassObject reverse(File javaFile) {
		ClassObject classObject = null;

		if (javaFile != null) {
			//////////////////////////////////////////////////////////////
			// returns a Class object from a java class file
			//////////////////////////////////////////////////////////////
			Class javaClass = getClassFromFile(javaFile);
			
			//////////////////////////////////////////////////////////////
			// check that the Class object belongs to a package to be 
			// concerned with
			//////////////////////////////////////////////////////////////
			if (javaClass != null && validPackage( javaClass  ))
				//////////////////////////////////////////////////////////////
				// delete to the ClassFactory to turn a Java Class object 
				// into a realMethods meta-model ClassObject
				//////////////////////////////////////////////////////////////
				classObject = ClassFactory.getInstance().createInstance(javaClass);
			else
				LOGGER.log( Level.INFO, () -> "Returned a null Class for " + javaFile.toString());
		} else {
			LOGGER.warning("java file arg is null ");
		}
		
		if (classObject == null)
			LOGGER.warning("No class object created from the ");

		return (classObject);
	}

	/**
	 * Helper method used to instantiate the associated .class file
	 * 
	 * @param javaFile
	 * @return
	 */
	protected Class getClassFromFile(File javaFile) {
		Class javaClass = null;

		//////////////////////////////////////////////////////////////////
		// At this point, it is crucial the javaFile (.class file) is
		// in the classpath.  This should have been handled before this
		// call.
		//////////////////////////////////////////////////////////////////
		try {
			javaClass = new ClassFileLoader().findClass(javaFile);
		} catch (Exception exc) {
			LOGGER.warning("Failed to find class in classsloader for " + javaFile.getAbsolutePath());
		}

		return (javaClass);
	}

	// *******************************
	// inner classes
	// *******************************

	/**
	 * Filters on directories and Java .class files only
	 * 
	 * @author realMethods, Inc.
	 * 
	 */
	class JavaFileFilter implements java.io.FileFilter {
		////////////////////////////////////////////////////////
		// FileFilter implementation, check if the pathname
		// argument is a directory or .class file.
		////////////////////////////////////////////////////////
		public boolean accept(File pathname) {
			boolean acceptable = false;

			if (pathname != null && 
					(pathname.isDirectory() || pathname.getName().endsWith(".class")))
				acceptable = true;

			return (acceptable);
		}
	}

	/**
	 * Internal class helper, to handle loading a class, and placing the root
	 * .class directory into the classpath
	 * 
	 * @author realMethods, Inc.
	 * 
	 */
	public class ClassFileLoader extends ClassLoader {
		protected ClassFileLoader() {
		}

		/**
		 * finds and loads the related Java Class from the file
		 * 
		 * @param file
		 * @return
		 * @throws ClassNotFoundException, IOException
		 */
		protected Class findClass(File file) throws ClassNotFoundException, IOException {
			int index = -1;

			//////////////////////////////////////////////////////////////////
			// Replace the path separator with a '.'
            //////////////////////////////////////////////////////////////////
			String className = file.getAbsolutePath().replace(File.separator, ".");
			
			////////////////////////////////////////////////////////////////
			// Iterate over each root package name
			////////////////////////////////////////////////////////////////
			for( String rootPackageName : pojoParams.getJavaRootPackageNames()) {
				index = className.indexOf( rootPackageName + "." );
				
				////////////////////////////////////////////////////////////////////
				// check if root package name is part of the file absolute path
				////////////////////////////////////////////////////////////////////
				if( index > -1 ) {
					////////////////////////////////////////////////////////////////////
					// Strip everything up to the index of the current package name
                    ////////////////////////////////////////////////////////////////////
					className = className.substring( index );		
					break;
				}
			}
			
			if ( index > -1 ) {
				/////////////////////////////////////////
				// remove the .class extension
				/////////////////////////////////////////
				className = className.replace(".class", "");
					
				URL[] myNewURL = new URL[] { new File(classRootDir).toURI().toURL() };
	
				try (URLClassLoader systemClassLoader = new URLClassLoader(myNewURL, Thread.currentThread().getContextClassLoader())){
					///////////////////////////////////////////////////////////////
					// load the className using the system class loader 
					// of the current thread
					///////////////////////////////////////////////////////////////
					return systemClassLoader.loadClass(className);
				} catch (final Exception ex) {
					LOGGER.log( Level.SEVERE, "Error in load class from system class loader3", ex);
					throw new ClassNotFoundException(className);
				}
			}
			//////////////////////////////////////////////////////////////
			// found a class that is not part of an exceptable package
			//////////////////////////////////////////////////////////////
			else {
				return null;
			}
		}
	}

	/**
	 * Copy the reverse engineered compiled Java classes into the realMethods classpath 
	 * so it's class loader can find them during reflection interrogation.
	 * 
	 * @exception	IOException
	 */
	private void copyRevEngClassesIntoClassPath() throws IOException {

		final String msg = "Copy directory " 
								+ classRootDir 
								+ " into the current classpath " 
								+ FrameworkNameSpace.REALMETHODS_CLASSPATH_ROOT_DIR;
		LOGGER.info( msg );
		FileUtils.copyDirectoryToDirectory(new File(classRootDir), 
											new File(FrameworkNameSpace.REALMETHODS_CLASSPATH_ROOT_DIR) );		
	}

	/**
	 * For each root package name provided, confirms a class is within the package structure
	 * @return	boolean
	 */
	private boolean validPackage( Class classArg )
	{
		boolean isValid = false;
		
		String[] rootPackageNames 	= pojoParams.getJavaRootPackageNames();				
		Package classArgPkg 		= classArg.getPackage();

		if ( rootPackageNames != null 
				&& classArgPkg != null 
				&& classArgPkg.getName() != null ) {
			for( String rootPackageName : rootPackageNames ) {
				if ( classArgPkg.getName().startsWith( rootPackageName ) )
					return true;
			}
		}
		
		return( isValid );
	}
	
	// attributes
	
	////////////////////////////////////////////////////////////////
	// class root directory
    ////////////////////////////////////////////////////////////////	
	private String classRootDir 		= null;
	////////////////////////////////////////////////////////////////
	// POJO params
    ////////////////////////////////////////////////////////////////	
	private PojoParams pojoParams		= null;
	////////////////////////////////////////////////////////////////
	// Logging facility
    ////////////////////////////////////////////////////////////////	
	private static final Logger LOGGER 	= Logger.getLogger(ReverseEngineerJava.class.getName());

}

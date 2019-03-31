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
package com.cloudmigrate.codetemplate.tools;

import java.io.File;
import java.io.IOException;

import java.net.URLClassLoader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.cloudmigrate.codetemplate.AppGenObject;
import com.cloudmigrate.codetemplate.parser.*;
import com.cloudmigrate.codetemplate.model.classes.ClassObject;
import com.cloudmigrate.codetemplate.model.factory.*;

/**
 * Reverse engineer Java .class files from a single directory structure into a
 * set of fully populated ClassObjects
 * 
 * @author realMethods, Inc.
 * 
 */
public class ReverseEngineerJava extends AppGenObject {
	public ReverseEngineerJava() {
	}

	public ReverseEngineerJava( String rootPackageName ) {
		this.rootPackageName = rootPackageName;
	}

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
		
		final String msg = "ReverseEngineerJava.reverse() - class dir is " + classRootDir;
		LOGGER.log( Level.INFO, msg );
		
		try {
			putRootDirInClasspath();
		} catch( Exception exc ) {
			LOGGER.log( Level.SEVERE, "ReverseEngineerJava.reverse", exc);
		}

		Collection<ClassObject> classes = new ArrayList<>();
		Collection<File> files = getJavaFiles(null);
		Iterator<File> iterator = files.iterator();
		ClassObject classObject = null;
		String className = null;

		if (files.isEmpty()) {
			LOGGER.log( Level.WARNING, () -> 
				"ReverseEngineerJava.reverse() - Java class file load failure. No class files were located under root "
				+ classRootDirectory + ".  Please recheck this directory.");
			return (classes);
		}

		// iterate over the files and reverse it's contents
		while (iterator.hasNext()) {
			classObject = reverse(iterator.next());

			if (classObject != null) {
				// adjust the class name and remove the provided suffix
				if (classSuffix != null && classSuffix.length() > 0) {
					className = classObject.getName();

					// remove it if there...
					if (className.endsWith(classSuffix)) {
						className = className.substring(0, className.lastIndexOf(classSuffix));
						classObject.setName(className);
					}
				}

				classes.add(classObject);
			}
		}

		return (classes);
	}

	protected Collection<File> getJavaFiles(File directory) {
		Collection<File> files = new ArrayList<>();

		// first pass in recursion
		if (directory == null) {
			directory = new File(classRootDir);
		}

		File[] javaFiles = directory.listFiles(new JavaFileFilter());
		File javaFile = null;

		if ( javaFiles != null )
		{
			for (int i = 0; i < javaFiles.length; i++) {
				// run down the root recursively
				javaFile = javaFiles[i];
				if (javaFile.isDirectory()) {
					// recurse
					files.addAll(getJavaFiles(javaFile));
				} else // must be a file
				{
					files.add(javaFile);
				}
			}
		}
		
		if ( files.isEmpty() )
			LOGGER.severe( "No Java files were created during reverse engineering." );
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
			Class javaClass = getClassFromFile(javaFile);
			if (javaClass != null && validPackage( javaClass  ))
				classObject = ClassFactory.getInstance().createInstance(javaClass);
			else
				LOGGER.log( Level.INFO, () -> 
							"ReverseEngineerJava.reverse(File) - getClassFromFile(...) returned a null Class for "
								+ javaFile.toString());
		} else {
			LOGGER.warning("ReverseEngineerJava.reverse(File) - java file arg is null ");
		}
		
		if (classObject == null)
			LOGGER.warning("ReverseEngineerJava.reverse(File) - java file arg is null ");

		return (classObject);
	}

	/**
	 * instantiates the associatied .class file
	 * 
	 * @param javaFile
	 * @return
	 */
	protected Class getClassFromFile(File javaFile) {
		Class javaClass = null;

		try {
			javaClass = new ClassFileLoader().findClass(javaFile);
		} catch (Exception exc) {
			LOGGER.warning("ReverseEngineerJava.instantiateFromFile(File) - " + exc.toString());
		}

		return (javaClass);
	}

	// *******************************
	// inner classes
	// *******************************

	/**
	 * Filters on Java .class files only
	 * 
	 * @author realMethods, Inc.
	 * 
	 */
	class JavaFileFilter implements java.io.FileFilter {
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

			String className = file.getAbsolutePath();
		
			// first, strip away the path part
			className = className.substring( classRootDir.length()+1 );

			// remove the .class
			className = className.replace(".class", "");

			// finally, replace the path separator with a '.'
			className = className.replace(File.separator, ".");
			URL[] myNewURL = new URL[] { new File(classRootDir).toURI().toURL() };

			try (URLClassLoader systemClassLoader = new URLClassLoader(myNewURL, Thread.currentThread().getContextClassLoader())){
					
				return systemClassLoader.loadClass(className);
			} catch (final Exception ex) {
				LOGGER.log( Level.SEVERE, "ReverseEngineer.findClass", ex);
				throw new ClassNotFoundException(className);
			} 
		}
	}

	/**
	 * Stuffs the root directory containing the .class files of the path
	 * provided into the classpath itself.
	 * 
	 * Analysis tool will gripe about modifying the accessibility of the addURL method
	 * on the URLClassLoader, but this is the only place it it used and it is contained
	 * within a private method.
	 * 
	 * @exception IOException
	 */
	private void putRootDirInClasspath() throws IOException {

		URL[] myNewURL = new URL[] { new File(classRootDir).toURI().toURL() };

		try (URLClassLoader systemClassLoader = new URLClassLoader(myNewURL, Thread.currentThread().getContextClassLoader())) {
			java.lang.reflect.Method method = URLClassLoader.class.getDeclaredMethod("addURL",new Class[] { URL.class } );

			method.setAccessible(true);
			method.invoke(systemClassLoader, myNewURL[0]);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "ReverseEngineerJava.putRootDirInClasspath()", e );
		} 
	}

	/**
	 * if a root package name is provided, confirms a class is within the package structure
	 * @return	boolean
	 */
	private boolean validPackage( Class classArg )
	{
		boolean isValid = true;
		
		if ( classArg != null ) {
			if ( rootPackageName != null ) {
				Package classArgPkg = classArg.getPackage();
				if ( classArgPkg != null ) {
					String classArgPkgName = classArgPkg.getName();
					if ( classArgPkgName != null )
						isValid = classArgPkgName.startsWith( rootPackageName );
				}
				else
					isValid = false;
			}
		}
		else
			isValid = false;
		
		return( isValid );
	}
	
	// attributes
	private String classRootDir 		= null;
	private String rootPackageName 		= null;
	private static final Logger LOGGER 	= Logger.getLogger(ReverseEngineerJava.class.getName());

}

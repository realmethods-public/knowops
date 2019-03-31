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
package com.cloudmigrate.codetemplate.model.method;

import java.util.Iterator;

import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;

/**
 * Wrapper of a Eclipse Meta-model Framework Method
 * 
 * @author realMethods, Inc.
 *
 */
public class EcoreMethodObject extends MethodObject {
	/**
	 * Default constructor
	 * 
	 * Use EcoreMethodObject(EOperation operation)
	 */
	protected EcoreMethodObject() {
		// deter instantiation
	}

	/**
	 * Constructor
	 * 
	 * Interrogate the input arg to populate the internal data.
	 * 
	 * @param operation
	 */
	public EcoreMethodObject(EOperation operation) {
		this.eOperation = operation;

		if (operation.getEType() != null)
			setReturnType(operation.getEType().getName());

		setName(eOperation.getName());
		setVisibility("public");
		setArguments(getArguments(eOperation));
		setDocumentation(null);
		isStatic(false);

		reconcileReturnType();
		reconcileArguments();
	}

	/**
	 * Returns the MethodArgs as prepared using the input argument.
	 * 
	 * @param operation
	 * @return MethodArgs
	 */
	protected MethodArgs getArguments(EOperation operation) {
		MethodArgs methodArgs = new MethodArgs();

		Iterator iter = operation.getEParameters().iterator();
		EParameter parameter = null;

		while (iter.hasNext()) {
			parameter = (EParameter) iter.next();
			methodArgs.args.add(new MethodArg(parameter.getName(),
					parameter.getEType().getName()));
		}

		return (methodArgs);
	}

	@Override
	public boolean equals(Object object) {

		if (!super.equals(object))
			return false;

		if (object == this)
			return true;

		if (!(object instanceof EcoreMethodObject))
			return false;

		return (this.eOperation == ((EcoreMethodObject) object).eOperation);
	}

	@Override
	public int hashCode() {
		return (super.hashCode());
	}

	// attributes

	private EOperation eOperation = null;
}

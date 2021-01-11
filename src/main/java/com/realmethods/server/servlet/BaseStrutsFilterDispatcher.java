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
package com.realmethods.server.servlet;

/**
 * A no_op class for now, but should extend 
 * com.realmethods.foundational.presentation.servlet.FrameworkBaseStrutsFilterDispatcher
 * if the entire goFramework persistence mechanism is required
 * @author realMethods, Inc.
 *
 */
public class BaseStrutsFilterDispatcher
	// uncomment to use the entire framework persistence mechanism
    //extends com.realmethods.foundational.presentation.servlet.FrameworkBaseStrutsFilterDispatcher
extends org.apache.struts2.dispatcher.filter.StrutsPrepareAndExecuteFilter
{
}

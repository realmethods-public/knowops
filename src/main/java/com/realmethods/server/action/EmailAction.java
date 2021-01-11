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
package com.realmethods.server.action;

import com.google.gson.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.realmethods.api.JsonResult;
import com.realmethods.api.JsonResultCode;
import com.realmethods.foundational.common.mail.SendMailUsingAuthentication;

/**
 * Struts action class to handle email related actions.
 * 
 * @author realMethods, Inc.
 *
 */
public class EmailAction extends BaseStrutsAction {
	
	/**
	 * default constructor
	 */
	public EmailAction() {
		// intentionally empty
	}

	public void setEmail( String email) {
		this.fromEmail = email;
	}
	
	public void setSubject( String subject ) {
		this.subject = subject;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	/**
	 * Returns the jsonResult field.
	 * 
	 * @return
	 */
	public JsonResult getResult() {
		return jsonResult;
	}
	
	/**
	 * sends an email
	 * 
	 * @return	String
	 */
	public String sendEmail() {

		String msg = "Failed to send email...";
		
		try {
			
			if ( fromEmail == null ) // assume it is a logged in user making the request
				fromEmail = getUser().getEmail();
			
			String [] recipients = new String[1];
			recipients[0] = toEmail; // send it support@realmethods.com
			
			SendMailUsingAuthentication mail = new SendMailUsingAuthentication();
			mail.sendEmail(recipients, subject, "from: " + fromEmail + "\n\ntopic: " + topic + "\n\nmessage:" + message);
			
			jsonResult.setProcessingMessage("Message sent successfully");
			return SUCCESS;
		}
		catch( Exception exc ) {
			LOGGER.log( Level.WARNING, msg, exc);	
		}
		
		jsonResult.setResultCode(JsonResultCode.REQUEST_EXECUTION_ERROR);
		jsonResult.setProcessingMessage(msg);
		return ERROR;
	}
	
	// attributes
	protected String toEmail 					= "support@realmethods.com";
	protected String fromEmail					= null;
	protected String subject					= null;
	protected String message					= null;
	protected String topic						= null;
	protected transient JsonResult jsonResult	= new JsonResult();
	protected transient Gson gson 				= new GsonBuilder().create();
	private static final Logger LOGGER 			= Logger.getLogger(EmailAction.class.getName());
	
}

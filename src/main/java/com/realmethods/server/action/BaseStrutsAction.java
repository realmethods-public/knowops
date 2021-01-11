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

import com.realmethods.common.helpers.AppGenHelper;
import com.realmethods.entity.User;
import com.realmethods.foundational.presentation.struts.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Base class of all application Struts Action classes.
 * 
 * @author realMethods, Inc.
 */
public class BaseStrutsAction 
extends com.opensymphony.xwork2.ActionSupport
implements com.opensymphony.xwork2.Preparable, org.apache.struts2.interceptor.ServletRequestAware {

	public void setServletRequest( HttpServletRequest request )
	{
		servletRequest = request;
	}
	
// helper methods

	protected HttpServletRequest getServletRequest()
	{
		return servletRequest;
	}
	    
   
    /**
     * Helper method to return a parameter value from the HttpServletRequest.
     * <p>
     * @param	id				name of the parameter
     * @return	the parameter
     */
    protected String getServletRequestParameter( String id )
    {
        return( getServletRequest().getParameter( id ) );
    }

	/**
	 * Maps a String array of inputs options to the a Map<String, String>,
	 * tokenized on the provided token.
	 * 
	 * @param inputOptions
	 * @param token
	 * @return Map<String, String>
	 */
	protected Map<String, String> arrayArgToStringMap(String[] inputOptions,
			String token) {
		Map<String, String> map = new HashMap<>();
		StringTokenizer tokenizer;
		String key;
		String val;

		for (String inputOption : inputOptions) {
			val = "";
			tokenizer = new StringTokenizer(inputOption, token);
			key = tokenizer.nextToken();

			if (tokenizer.hasMoreTokens())
				val = tokenizer.nextToken();

			map.put(key, val);
		}
		return (map);
	}

	/**
	 * A no_op method for mapping a Struts Action to pass unconditionally.
	 * 
	 * @return String
	 */
	public String noOp() {
		return SUCCESS;
	}

	/**
	 * Assign the clientId field using the provided argument.
	 * 
	 * @param clientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * Called by Struts so the Action can prepare itself. Implemented from
	 * FrameworkBaseStrutsAction Overload to something more specific, but be
	 * sure to call this class as well
	 */
	public void prepare() {
		onPrepare();

		// might want to do something interesting here...
	}

	/**
	 * Helper method to preparations
	 */
	protected void onPrepare() {
		// no_op
	}

	/**
	 * Returns the id field
	 * 
	 * @return Long
	 * @throws Exception
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Assigns the id field the provided argument.
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	/**
	 * Delegates to the noOp method. Expected to be overridden.
	 * 
	 * @return
	 * @throws Exception
	 */
	public String execute() {
		LOGGER.warning(
				"BaseStrutsAction.execute() - called but not overloaded");
		return (noOp());
	}
	
	public String logoff() {
		javax.servlet.http.HttpSession currentHttpSession = null;
		
		try {
            // terminate the session
            currentHttpSession = getServletRequest().getSession(false);
        }
        catch (Exception generalExc) {
            return "LOGIN_FAILURE";
        }

        if (currentHttpSession != null) {
            try {
                currentHttpSession.invalidate();
            }
            catch (IllegalStateException exc) {
                // do nothing because it is thrown if the session is 
                // already invalidated
            }
        }

        return SUCCESS;
    }
	

	public String checkStatus() {
		return (SUCCESS);
	}

	public String stages() {
		return (SUCCESS);
	}

	/**
	 * Returns the stages field.
	 * 
	 * @return ArrayList
	 */
	protected List<String> getStages() {
		return stages;
	}

	protected void assignStages(List<String> stages) {
		this.stages = stages;
		updateStatus();
	}
	
	protected HttpSession getTheSession() {
		return getServletRequest().getSession(true);
	}

	protected void addInfoMessage(String stage, String msg) {
		this.status.currentStage = stage;
		addInfoMessage(msg);
	}

	protected void addInfoMessage(String msg) {
		this.status.infoMsgs.append(msg + LINE_BREAK);
		updateStatus();
	}

	protected void addWarningMessage(String stage, String msg) {
		this.status.currentStage = stage;
		addWarningMessage(msg);
	}

	protected void addWarningMessage(String msg) {
		this.status.warningMsgs.append(msg + LINE_BREAK);
		updateStatus();
	}

	protected void addErrorMessage(String stage, String msg) {
		this.status.currentStage = stage;
		addErrorMessage(msg);
	}

	protected void addErrorMessage(String msg) {
		this.status.errorMsgs.append(msg + LINE_BREAK);
		this.status.setSuccess(false);
		updateStatus();
	}

	public ActionStatus getStatus() {
		return (status);
	}

	protected void updateStatus() {
		getTheSession().setAttribute(clientId,
				new StagingContainer(stages, status));
	}

	public StagingContainer getStagingContainer() {
		return (StagingContainer) getTheSession().getAttribute(clientId);
	}

	public String getFormattedMessages() {
		StringBuilder buf = new StringBuilder();
		buf.append("Info: " + status.infoMsgs);
		buf.append("Warning: " + status.warningMsgs);
		buf.append("Error: " + status.errorMsgs);
		return buf.toString();
	}

	/**
	 * Return the use from the cache
	 * 
	 * @return
	 */
	protected User getUser() {
		return (User)getTheSession().getAttribute(USER);
	}
	/**
	 * Cache to the session
	 * 
	 * @param user
	 */
	protected void setUser( User user ) {
		getTheSession().setAttribute(USER, user);	
	}
	
	/**
	 * Method used to generate and assign a unique name for the current thread.  This name
	 * is used as the key when creating or accessing a ModelParser from the ModelParserFactory.
	 * 
	 * The purpose for this method is that not all Servlet Engines are thread safe.
	 * 
	 * @param appendModel
	 */
	protected void applyUniqueThreadName(boolean appendModel) {
		// change the thread name since the ModelParserFactory uses the thread
		// name to determine
		// if it should use the current one or create a new one
		// only do this if the model should not be appended to the current one
		String threadNameToUse = null;

		if (!appendModel) {
			threadNameToUse = org.apache.commons.lang.RandomStringUtils
					.randomNumeric(8);
			getTheSession().setAttribute(AppGenHelper.MODEL_NAME,
					threadNameToUse);
		} else {
			threadNameToUse = (String) getTheSession()
					.getAttribute(AppGenHelper.MODEL_NAME);
			if (threadNameToUse == null) {
				getTheSession().setAttribute(AppGenHelper.MODEL_NAME,
						Thread.currentThread().getName());
			}
		}

		if (threadNameToUse != null) {
			Thread t = Thread.currentThread();
			// change the thread name
			t.setName(threadNameToUse);
		}
	}

	// attribute

	protected Long id 									= null;
	protected String clientId 							= "";
	private transient List<String> stages 				= null;
	private transient ActionStatus status 				= new ActionStatus();
	private static final String LINE_BREAK 				= "<br>";
	protected static final ResourceBundle labels 		= ResourceBundle.getBundle("jsonAPIMessages", new Locale("en", "US"));
	protected transient HttpServletRequest servletRequest = null;
	private static final String USER					= "USER";
	private static final Logger LOGGER 					= Logger.getLogger(BaseStrutsAction.class.getName());

	// inner class
	/**
	 * Used to build up messages and results for a single action execution
	 * cycle. The client will decide how best to interpret and consume the
	 * results.
	 * 
	 * @author realMethods, Inc.
	 *
	 */
	protected class ActionStatus {

		/**
		 * Returns the infoMsgs field as a String.
		 * 
		 * @return String
		 */
		public String getInfoMessages() {
			return this.infoMsgs.toString();
		}

		/**
		 * Returns the warningMsgs field as a String.
		 * 
		 * @return String
		 */
		public String getWarningMessages() {
			return this.warningMsgs.toString();
		}

		/**
		 * Returns the errorMsgs field as a String.
		 * 
		 * @return String
		 */
		public String getErrMessages() {
			return this.errorMsgs.toString();
		}

		/**
		 * Returns the success field.
		 * 
		 * @return String
		 */
		public boolean getSuccess() {
			return success;
		}

		/**
		 * Assigns the success flag from the provided argument.
		 * 
		 * @param success
		 */
		public void setSuccess(boolean success) {
			this.success = success;
		}

		/**
		 * Returns the stages field.
		 * 
		 * @return List<String>
		 */
		public List<String> getStages() {
			return stages;
		}

		/**
		 * Returns the currentStage field.
		 * 
		 * @return String
		 */
		public String getCurrentStage() {
			return currentStage;
		}

		/**
		 * Assigns the currentStage field the provided argument. Adds the stage
		 * to the stages field if not already contained.
		 * 
		 * @param currentStage
		 */
		public void setCurrentStage(String currentStage) {
			this.currentStage = currentStage;
			if (!stages.contains(currentStage))
				stages.add(currentStage);
		}

		// attributes
		protected boolean success 			= true;
		protected String currentStage 		= null;
		protected List<String> stages 		= new ArrayList<>();
		protected StringBuilder warningMsgs = new StringBuilder();
		protected StringBuilder infoMsgs 	= new StringBuilder();
		protected StringBuilder errorMsgs 	= new StringBuilder();
	}

	/**
	 * Inner class to encapsulate a series of stages of a particular action,
	 * along with status of the action
	 * 
	 * @author realMethods, Inc.
	 *
	 */
	protected static class StagingContainer implements Serializable {

		private static final long serialVersionUID = 1L;

		public StagingContainer(List<String> stages, ActionStatus status) {
			this.stages = stages;
			this.status = status;
		}

		public List<String> getStages() {
			return stages;
		}

		public ActionStatus getStatus() {
			return status;
		}

		protected transient List<String> stages = null;
		protected transient ActionStatus status = null;
	}
}

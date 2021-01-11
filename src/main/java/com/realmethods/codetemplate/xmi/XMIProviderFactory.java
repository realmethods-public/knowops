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
package com.realmethods.codetemplate.xmi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.realmethods.codetemplate.AppGenObject;
import org.apache.xpath.XPathAPI;

/**
 * XMI Provider Factory for conditionally creating the correct XMIProvider
 * interface based on the information of the provided xmiDocument as an XML
 * document node
 * 
 * @author realMethods, Inc.
 *
 */
public class XMIProviderFactory extends AppGenObject {

	/**
	 * protected constructor, only internally accessible
	 * 
	 * @param xmiDocument
	 */
	protected XMIProviderFactory(Document xmiDocument) {
		xmiProvider = null;
		document = null;
		xmiVersion = null;
		document = xmiDocument;
	}

	/**
	 * Returns an instance once it is bound to the provided Document
	 * 
	 * @param xmiDocument
	 * @return
	 */
	public static XMIProviderFactory getInstance(Document xmiDocument) {
		instance = new XMIProviderFactory(xmiDocument);
		return instance;
	}

	/**
	 * Returns the singleton instance of XMIProviderFactory, only allowed to be
	 * invoked once the getInstance(Document) version has been called. It is
	 * here as a convenience method.
	 * 
	 * @return
	 */
	public static XMIProviderFactory getInstance() {
		if (instance == null)
			LOGGER.warning(
					"XMIProviderFactory - cannot call empty getInstance() without calling version that requires Document");
		return instance;
	}

	/**
	 * Uses the XMI version to determine which IXMIProvider implementation to
	 * return
	 * 
	 * @return IXMIProvider
	 */
	public IXMIProvider getXMIProvider() {
		if (xmiProvider != null)
			return xmiProvider;

		if (xmiVersion == null)
			discoverXMIVersion();

		try {
			if (xmiVersion.startsWith("1.0") || xmiVersion.startsWith("1.1"))
				xmiProvider = new XMIProvider10();
			else if (xmiVersion.startsWith("1.2"))
				xmiProvider = new XMIProvider12();
			else if (xmiVersion.startsWith("2"))
				xmiProvider = new XMIProvider21();
			else {
				final String msg = "Unable to handle a version greater than "
						+ DEFAULT_XMI_VERSION;
				LOGGER.log(Level.SEVERE, msg);
			}
		} catch (Exception exc) {
			final String msg = "Failed to determine the XMI Provider";
			LOGGER.log(Level.SEVERE, msg, exc);
		}

		return xmiProvider;
	}

	/**
	 * Returns the XMI version of the bound XMI document.
	 * 
	 * @return String
	 */
	public String getXMIVersion() {
		return discoverXMIVersion();
	}

	/**
	 * Does the internal work to figure out the XMI version of the bound XMI
	 * document.
	 * 
	 * @return
	 */
	protected String discoverXMIVersion() {
		if (document != null && xmiVersion == null) {
			
			String versionPath = "//*/@xmi.version";
			xmiVersion 			= DEFAULT_XMI_VERSION;
			try {
				Node n = XPathAPI.selectSingleNode(document, versionPath);
					
				if ( n == null )
					n = XPathAPI.selectSingleNode(document, "//*/@xmi:version");
				
				if (n != null) {
					xmiVersion = n.getNodeValue();
					final String msg = "XMI version is " + xmiVersion;
					LOGGER.log(Level.INFO, msg);
				} else {
					LOGGER.log(Level.INFO, "XMI Version not found, assuming "
							+ DEFAULT_XMI_VERSION);
				}
			} catch (Exception exc) {
				final String msg = "Failed to acquire XMI version. Will assume version "
						+ DEFAULT_XMI_VERSION;
				LOGGER.log(Level.WARNING, msg, exc);
			}
		} else {
			xmiVersion = DEFAULT_XMI_VERSION;
		}

		LOGGER.log(Level.INFO, "XMI Version is {0}", xmiVersion);
		return xmiVersion;
	}

	// attributes
	protected String xmiVersion = null;
	private static XMIProviderFactory instance = null;
	private IXMIProvider xmiProvider = null;
	private Document document = null;
	private static final String DEFAULT_XMI_VERSION = "1.2";
	private static final Logger LOGGER = Logger
			.getLogger(XMIProviderFactory.class.getName());
}

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
package com.hxtt.support.hibernate;

import java.sql.Types;

/**
 * An SQL dialect for HXTT Paradox.
 */
public class HxttParadoxDialect  extends HxttXMLDialect {

    public HxttParadoxDialect() {
        super();
        //complete map
        registerColumnType(Types.OTHER, "currency");
        registerColumnType(Types.OTHER, "graphics");
        registerColumnType(Types.OTHER, "ole");

    }

}

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
 * An SQL dialect for HXTT Text (CSV).
 */
public class HxttTextDialect extends HxttXMLDialect {

    public HxttTextDialect() {
        super();
        //complete map
        registerColumnType( Types.DECIMAL, "decimal($p,$s)" );
        registerColumnType( Types.JAVA_OBJECT, "java_object" );

    }

    @Override
    /**
     * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
     *
     * @return True if we support altering of tables; false otherwise.
     */
    public boolean hasAlterTable() {
            return false;
    }    

}

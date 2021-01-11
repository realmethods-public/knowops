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

/**
 * An SQL dialect for HXTT Cobol.
 */
public class HxttCobolDialect extends HxttTextDialect {

    public HxttCobolDialect() {
        super();
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

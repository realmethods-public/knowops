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
package com.cloudmigrate.foundational.business.bo;

import java.io.Serializable; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Types;  
import org.hibernate.usertype.UserType; 

public class FrameworkHibernateEnumerator<E extends Enum<E>> 
implements UserType 
{ 
	public FrameworkHibernateEnumerator() {
	}
	
    protected FrameworkHibernateEnumerator(Class<E> c) 
    { 
        this.clazz = c; 
    } 
 
    private static final int[] SQL_TYPES = {Types.VARCHAR}; 
    public int[] sqlTypes() 
    { 
        return SQL_TYPES; 
    } 
 
    public Class returnedClass() 
    { 
        return clazz; 
    } 
 
    public Object nullSafeGet(ResultSet resultSet, 
    			String[] names, 
    			org.hibernate.engine.spi.SessionImplementor paramSessionImplementor, 
    			Object paramObject) throws SQLException 
    {
        String name = resultSet.getString(names[0]); 

        E result = null; 
        if (!resultSet.wasNull()) 
        { 
            result = Enum.valueOf(clazz, name);
        }    

        return result; 
    } 

    
    public void nullSafeSet(PreparedStatement preparedStatement, 
    		Object value, 
    		int index, 
    		org.hibernate.engine.spi.SessionImplementor paramSessionImplementor) throws SQLException 
    {
        if (null == value) 
        {
            preparedStatement.setNull(index, Types.VARCHAR); 
        } 
        else 
        {	
            preparedStatement.setString(index, ((Enum)value).name()); 
        } 
    } 
 
    public Object deepCopy(Object value) 
    { 
        return value; 
    } 
 
    public boolean isMutable() 
    { 
        return false; 
    } 
 
    public Object assemble(Serializable cached, Object owner) 
    {
         return cached;
    } 

    public Serializable disassemble(Object value)  
    { 
        return (Serializable)value; 
    } 
 
    public Object replace(Object original, Object target, Object owner)  
    { 
        return original; 
    } 
    
    public int hashCode(Object x)  
    { 
        return x.hashCode(); 
    } 
    
    public boolean equals(Object x, Object y)  
    { 
        if (x == y) 
            return true; 
        if (null == x || null == y) 
            return false; 
        return x.equals(y); 
    } 

// attributes
    
    private Class<E> clazz = null; 
    
} 

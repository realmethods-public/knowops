#header()
package ${aib.getRootPackageName(true)}.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

import org.apache.struts2.util.StrutsTypeConverter;

public class FrameworkStrutsDateTimeConverter extends StrutsTypeConverter 
{			
	public FrameworkStrutsDateTimeConverter()
	{}
	
    public Object convertFromString(Map context, String[] values, Class toClass) 
    {
    	String indate = values[0];
    	
    	LOGGER.info( "indate=" + indate);
    	
        if (indate != null ) 
        {
        	indate = indate.trim();
        	
        	if ( indate.length() > 0 )
        	{
	            try 
	            {
	            	if ( toClass.getName().equals( "java.sql.Timestamp" ) )
	            		return new java.sql.Timestamp( new java.text.SimpleDateFormat( FrameworkStrutsDateTimeConverter.timeFormat ).parse( indate ).getTime() );
	            	else if ( toClass.getName().equals( "java.util.Date" ) )
	            	{
	                    java.util.Calendar cal = java.util.Calendar.getInstance();
	                    cal.setTime( new java.text.SimpleDateFormat( FrameworkStrutsDateTimeConverter.dateTimeFormat ).parse( indate ) );
	                    return new java.sql.Date( cal.getTime().getTime() );
	            	}
	            }
	            catch(Throwable e) 
	            {
	            	LOGGER.severe( "- error converting value [" + values[0] + "] to Date " + e );            	
	            }
        	}
        }
        return null;
    }
    
    public String convertToString(Map context, Object o) 
    {
    	LOGGER.info( o.toString() );    	

        if ( o instanceof java.sql.Timestamp )
        {
        	return( new java.text.SimpleDateFormat( FrameworkStrutsDateTimeConverter.timeFormat ).format( (java.sql.Timestamp)o ) );
        }
        else if ( o instanceof java.util.Date )
        {
        	return( new java.text.SimpleDateFormat( FrameworkStrutsDateTimeConverter.dateTimeFormat ).format( (java.util.Date)o ) );
        }
        return "";
    }
    
    protected Object performFallbackConversion(Map context, Object o, Class toClass)
    {
    	LOGGER.info( o.getClass().getName() + " and " + toClass.getName() );    	

    	return null;
    }
    
    // attributes
    private static final Logger LOGGER 	= Logger.getLogger(FrameworkStrutsDateTimeConverter.class.getName());
	private static final String timeFormat 				= "hh:mm:ss";
	private static final String dateTimeFormat 			= "yyyy-mm-dd hh:mm:ss";
    
}


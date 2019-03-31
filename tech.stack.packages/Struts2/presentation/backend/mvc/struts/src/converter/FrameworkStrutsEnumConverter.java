#header()
package ${aib.getRootPackageName(true)}.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import java.sql.*;

import org.apache.struts2.util.StrutsTypeConverter;


public class FrameworkStrutsEnumConverter extends StrutsTypeConverter 
{
    public Object convertFromString(Map context, String[] values, Class toClass) 
    {
    	Enum e = null;

    	if (values == null )
    	{
    		LOGGER.info( "***FrameworkStrutsEnumConverter.convertFromString() values[] is null " );
    		return e;
    	}

    	if (values.length == 0 )
    	{
    		LOGGER.info( "***FrameworkStrutsEnumConverter.convertFromString() values[] is empty" );
    		return e;
    	}
    	
    	String enumValue = values[0];
        if (enumValue!= null && enumValue.length() > 0 && enumValue.trim().length() > 0) 
        {
        	e = Enum.valueOf(toClass, enumValue);
        }
    	LOGGER.info( "***FrameworkStrutsEnumConverter.convertFromString() enumClass: " + toClass + ", enumValue: " + values[0] + ", returning enum: " + e.toString());    	

        return e;
    }

    public String convertToString(Map context, Object o) 
    {
    	if ( o == null )
    		LOGGER.info( "***FrameworkStrutsEnumConverter.convertToString() object arg is null.");
    	
    	LOGGER.info( "***FrameworkStrutsEnumConverter.convertToString() with " + o.toString());
    	
    	return( o.toString() );
    }
    
    protected Object performFallbackConversion(Map context, Object o, Class toClass)
    {
    	LOGGER.info( "***inside of FrameworkStrutsEnumConverter.performFallbackConversion() with " + o.getClass().getName() + " and " + toClass.getName() );    	

    	return super.performFallbackConversion(context, o, toClass);
    }    

    // attributes
    private static final Logger LOGGER = Logger.getLogger(FrameworkStrutsEnumConverter.class.getName());

}


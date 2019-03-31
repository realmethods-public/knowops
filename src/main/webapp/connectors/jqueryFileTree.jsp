<%@ page import="java.io.File, java.io.FilenameFilter"%>

<%
/**
  * jQuery File Tree JSP Connector
  * Version 1.0
  * Copyright 2008 Joshua Gould
  * 21 April 2008
*/	
    String dir = request.getParameter("dir");
    String inputFile = request.getParameter("file");
    
    if (dir != null) 
    {
		if (dir.charAt(dir.length()-1) == '\\') {
	    	dir = dir.substring(0, dir.length()-1) + "/";
		} else if (dir.charAt(dir.length()-1) != '/') {
		    dir += "/";
		}
		
		dir = java.net.URLDecoder.decode(dir, "UTF-8");	
		
	    if (new File(dir).exists()) 
	    {
	    	
			String[] files = new File(dir).list(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
					return name.charAt(0) != '.';
			    }
			});
			
			//java.util.Arrays.sort(files, String.CASE_INSENSITIVE_ORDER);
			out.print("<ul class='jqueryFileTree' style='text-align:left'>");
			// All dirs
			for (String file : files) {
			    if (new File(dir, file).isDirectory()) {
					out.print("<li class=\"directory collapsed\"><a href=\"#\" rel=\"" + dir + file + "/\">"
						+ file + "</a></li>");
			    }
			}
			// All files
			for (String file : files) {
			    if (!new File(dir, file).isDirectory()) {
					int dotIndex = file.lastIndexOf('.');
					String ext = dotIndex > 0 ? file.substring(dotIndex + 1) : "";
					out.print("<li class=\"file ext_" + ext + "\"><a href=\"#\" rel=\"" + dir + file + "\">"
						+ file + "</a></li>");
			    	}
			}
			out.print("</ul>");
	    }
	    else
	    	out.print(" dir " + dir + " doesn't exist" );
	}
	
	if ( inputFile != null )
	{
		try
		{
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(inputFile));
    		String line;
    		while ((line = reader.readLine()) != null)
    		{
    			line = line.replaceAll( "\t", "    " );
    			if ( inputFile.endsWith( ".java" ) == true )
      				out.println( org.apache.commons.lang.StringEscapeUtils.escapeJava(line) + "</br>" );
    			else if ( inputFile.endsWith( ".xml" ) == true )
      				out.println( org.apache.commons.lang.StringEscapeUtils.escapeXml(line) + "</br>" );
      			else if ( inputFile.endsWith( ".js" ) == true )
      				out.println( org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(line) + "</br>" );
      			else 
      				out.println( org.apache.commons.lang.StringEscapeUtils.escapeHtml(line) + "</br>" );
      			
    		}
    		
    		reader.close();		    		
    	}
    	catch (Exception e)
  		{
  		}
	}
%>
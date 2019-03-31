<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
	<link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Libre Franklin">

  <style>
  .sectionClass2 {
  	background : white;
  	border : 2px solid black;
  	padding : 4px;
  }

  .sectionHeaderClass2 {
  	background : #2F4F4F;
  	padding : 4px;
  	color : white;  	
  }
  
  .sectionTitleClass2{
  	font-size : 14;
  	font-weight : bold;
  }

  .sectionBodyClass2 {
	font-size : 12;
	background : white;
	color : black;
	overflow-y : auto;
  }
  
  </style>
</head>

<body>
<s:if test="%{errMessages.length()> 0}">    
	<div class="sectionClass2">
		<div class="sectionHeaderClass2">
			<span class="sectionTitleClass2"> 
				Error 
			</span>
		</div>
	   <div class="sectionBodyClass2">
			<s:property value="errMessages" escapeHtml="false"/>
		</div>
	</div>
</s:if>
<s:if test="%{warningMessages.length()> 0}">    
	<div class="sectionClass">
		<div class="sectionHeaderClass2">
			<span class="sectionTitleClass"> 
				Warning 
			</span>
		</div>
	   <div class="sectionBodyClass2">
			<s:property value="warningMessages" escapeHtml="false"/>
		</div>
	</div>
</s:if>
<s:if test="%{infoMessages.length()> 0}">    
	<div class="sectionClass">
		<div class="sectionHeaderClass2">
			<span class="sectionTitleClass"> 
				Information 
			</span>
		</div>
	   <div class="sectionBodyClass2">
			<s:property value="infoMessages" escapeHtml="false"/>
		</div>
	</div>
</s:if>
</body>
</html>
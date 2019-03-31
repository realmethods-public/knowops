#xmlHeader()
<html>
<head>

	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
	<!-- 3rd party style sheets -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	<link href="http://fonts.googleapis.com/css?family=Libre Franklin" rel="stylesheet" type="text/css"/>

		
	<!-- 3rd part javascript functions, etc -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
 	
</head>

<script type="text/javascript">
	$(document).ready(function() 
	{
  	});
	
	function query()
	{	
		document.getElementById("searchResultsDivId").innerHTML = "";
		jQuery('#searchIndicatorDivId').show();
	
		var keywords = document.getElementById("keywords");
		
		if ( keywords.value == '' || keywords.value == null )
		{
			keywords.value = '*';
		}
			
 		jQuery.ajax(
		{
	   		type: "POST",
	   		url: "${pageContext.request.contextPath}/search",
	   		data: $("#queryFormId").serialize(),
	        success: function (data)
	        {	       
	        	jQuery('#searchResultsDivId').show();
	        	document.getElementById("searchResultsDivId").innerHTML = data;
	        }
		});
		
		jQuery('#searchIndicatorDivId').hide();	   		
	}
</script>

<html>
<body style="border:20px solid black">

<center>
<form id="queryFormId">  		  	
  <table style="padding:6px" >
    <tr style="padding:6px">
      <td style="padding:6px">
        <img width="48" height="48" align="right" src="${pageContext.request.contextPath}/img/oculus_small.png"/>
      </td>
	  <td style="vertical-align:center;padding:6px">
	  	<input size="50" id="keywords" name="keywords" placeholder="enter keyword(s) here"/>
	  </td>
	  <td style="vertical-align:center;padding:6px">
	    <a href="#" onclick="query()" >
	      <button type="button" class="btn btn-warning btn-sm">Search</button>
		</a>
	  </td>
	  <td style="vertical-align:center;padding:6px">
	    <div id="searchIndicatorDivId" style="display:none">
	    	<img id="searchIndicatorId" src="${pageContext.request.contextPath}/img/searchloader.gif" />
	    </div>
	  </td>
	</tr>
  </table>
</form>
<p>
<div style="padding:20px">
  <div id="searchResultsDivId" style="height:500px;overflow-y:auto;;padding:10px;border:2px solid lightgray;text-align:left;display:none">
  </div>
</div>
</center>

</body>
</html>
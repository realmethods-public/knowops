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

	<!-- app specific style sheets -->
	<link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet" type="text/css"/>
		
	<!-- 3rd part javascript functions, etc -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
 	
 	<!-- app specific javascript functions -->
 	<script src="${pageContext.request.contextPath}/js/common.js"></script> 

</head>

<script>
function test()
{
	$('#loadingDivId').show();
	document.getElementById("testResultsDivId").innerHTML = '';
	
	jQuery.ajax(
	{
  		url: 'test',
  		dataType: 'text',
	}
	).always(function( data ) 
	{
		document.getElementById("testResultsDivId").innerHTML = data;
		$('#loadingDivId').hide();
    });
}
</script>

<body style="border:20px solid black;padding;4px">
<center>
<div class="header">
	<center>
	<table>
	  <tr>
		<td>
			<img align="left" src="https://a248.e.akamai.net/secure.meetupstatic.com/photos/event/9/d/a/3/global_437440355.jpeg" 
				alt="app logo" border="0" style="width:64px;height:64px"/>
		</td>
	    <td>
			<img align="center" src="${aib.getApplicationLogoURL()}" 
				alt="app logo" border="0"/>
		</td>
		<td>
			<img align="right" src="https://www.mysql.com/common/logos/logo-mysql-170x115.png"
				alt="app logo" border="0" style="width:64px;height:64px"/>
		</td>
	  </tr>
	</table>

	<br>
#set( $appName = $aib.getApplicationName() )
#if ( $aib.getApplicationName() == "no_app_name_assigned" )
	#set( $appName = "" )
#else
	#set( $appName = "${appName} - " )
#end
	    <h4>${appName}Spring RESTful Services Test Using JUnit</h4>
	</center>
</div>

<table>
  <tr style="padding:4px">
    <td style="align:right">
      <a href="#" onclick="test()" >
        <button type="button" class="btn btn-warning btn-sm">
          Start Test
	    </button>
	  </a>
	</td>
	<td style="align:left"> 
	  <div id="loadingDivId" style = "display:none;">
	    <img width="32" height="32" src="${pageContext.request.contextPath}/img/load_new.gif">
	  </div>
	</td>
  </tr>
</table>

<p>

<div id="testResultsDivId" style="text-align:left;overflow-y:auto;height:300px;width:500px;border:2px solid grey">
</div>
</center>

</body>

</html>




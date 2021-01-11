<html>

<head>

	<meta charset="utf-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	
		<!-- 3rd party style sheets -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap-theme.min.css" integrity="sha384-rHyoN1iRsVXV4nD0JutlnGaslCJuC7uwjduW9SVrLvRYooPp2bWYgmgJQIXwl/Sp" crossorigin="anonymous">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css" rel="stylesheet" />	
	<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap3-dialog/1.34.9/css/bootstrap-dialog.min.css" rel="stylesheet" type="text/css" />
	<link href="http://fonts.googleapis.com/css?family=Libre Franklin" rel="stylesheet" type="text/css"/>
	<!-- app specific style sheets -->
	<link href="${pageContext.request.contextPath}/css/common.css" rel="stylesheet" type="text/css"/>
	
		<!-- 3rd part javascript functions, etc -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js" integrity="sha384-Tc5IQib027qvyjSMfHjOMaLkfuWVxZxUPnCJA7l2mCWNIpG9mGCD8wGNIcPD7Txa" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>
 	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap3-dialog/1.34.9/js/bootstrap-dialog.min.js"></script>
 	
 	<!-- app specific javascript functions -->
 	<script src="${pageContext.request.contextPath}/js/common.js"></script>
 	 
</head>

<script type="text/javascript">

	function doPrint()
	{
		window.print();
	}


	$( document ).ready(function()
	{
		//========================================================
		// initialize the jstree
		//========================================================
		$('#modelTreeId').jstree(
		{
			"core" : {
				"multiple" : false,
				"animation" : 0,
				"themes" : { "stripes" : true }
				}
		});
		
		populateTree();
			
		$('#modelTreeId').on('changed.jstree', function (e, data) 
		{
			var name = $('#modelTreeId').jstree('get_selected');
			$( "#treeSelectPanelDivId" ).load( "${pageContext.request.contextPath}/jsp/" + name + "ViewAllList.jsp" );        	 
		});
	});
	
	function populateTree()
	{
		var content = '<ul>';
	
	#foreach( $class in $aib.getClassesWithIdentity() )
		content = content + '<li id="${class.getName()}"><font style="font-size:0.8em;">${class.getName()}</font></li>';
	#end ##foreach( $class in $aib.getClassesWithIdentity() )
			
		content = content + '</ul>';
	
		//=====================================================				
		// apply the content to the tree div then refresh
		//=====================================================			
		$('#modelTreeId').jstree(true).settings.core.data = content;
		$('#modelTreeId').jstree(true).refresh();
	
	}
	
	function localAction( action )
	{
		jQuery.ajax(
		{
	  		url: action,
	  		dataType: 'text',
		}
		).always(function( data ) 
		{
			document.getElementById("homePageDivId").innerHTML = data;
	    });
	}
		
</script>

<body>

<div id="homePageDivId" style="width:100%;height:520px;vertical-align:top">

<!-  simple div providing the toolbar section -->

<div id="toolbar">

	<img align="center" src="${pageContext.request.contextPath}/img/printer.png" 
				border="0" alt="print" onclick="print()" style="width:32px;height:32px"/>
        
	<img align="center" src="${pageContext.request.contextPath}/img/logout.png" 
			border="0" alt="logoff" onclick="localAction('${pageContext.request.contextPath}/logoff.action')" style="width:32px;height:32px"/>
	
</div>


<!-- home page div -->

  <table>
    <tr>
	  <td style="width:100px;vertical-align:top;background:white;padding:8px;text-align:left;vertical-align:top">
	    <div id="modelTreeId" style="height:480px;overflow-y:auto">
	    </div>	    
	  </td>
	  <td style="width:100%;height:480px;padding:8px;background:white;text-align:left;vertical-align:top">
		<div id="treeSelectPanelDivId" style="height:100%;background:white">
		<h6>Select a tree option to display relevant options and data</h6>
		</div>
	  </td>
	</tr>
  </table>
</div> <!-- homepageId -->

##############################
## Vender specific footer
##############################
#jsFooter()
<p>

<div id="loadingDivId" style = "display:none;">
	<!-- this is the default loading gif -->
	<img src="${pageContext.request.contextPath}/img/loader.gif">
</div>

</body>

</body>
</html>
    
       
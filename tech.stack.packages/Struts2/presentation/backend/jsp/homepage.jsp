<html>

#jsHeadDeclaration()

<script type="text/javascript">

	function doPrint()
	{
		window.print();
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
			document.getElementById("mainSection").innerHTML = data;
	    });
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

##jsHeader()

<div id="homePageDivId" style="width:100%;height:520px;vertical-align:top">

<!-  simple div providing the toolbar section -->

<div id="toolbar">

	<img align="center" src="${pageContext.request.contextPath}/img/printer.png" 
				border="0" alt="print" onclick="print()" style="width:32px;height:32px"/>
        
	<img align="center" src="${pageContext.request.contextPath}/img/logout.png" 
			border="0" alt="logoff" onclick="localAction('logoff.action')" style="width:32px;height:32px"/>
	
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
    
       
#set( $className = $classObject.getName() )
#set( $lowercaseName = $Utils.lowercaseFirstLetter( $className ) )
#set( $pkName = $classObject.getFirstPrimaryKey().getName() )
#set( $pkExpression = "${lowercaseName}.${pkName}" ) 
#set( $jQueryClassName = "#${className}" )
#set( $jQueryGridTableId = "${jQueryClassName}GridTableId")
#set( $attributes = $classObject.getAttributes() )
#set( $attributeCollections = $classObject.getAttributesOrderedAsCollectionsInHierarchy( true ) )
#set( $pkAttributes = $attributeCollections.get(0) )
#set( $directAttributes = $attributeCollections.get(1) )
#set( $compositeAttributes = $attributeCollections.get(2) )
#set( $firstAttribName = $classObject.findAttributeAsBestFitName() )
#set( $colNames = "" )
#set( $colModels = "" )
<link href="http://code.jquery.com/ui/1.12.1/themes/cupertino/jquery-ui.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/css/ui.jqgrid.css" rel="stylesheet" type="text/css"/>

<style>
	.ui-jqgrid tr.jqgrow td,
	.ui-jqgrid th {
		font-size:0.8em
	}
</style> 

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jqGrid.min.js"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.jqGrid.src.js"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/dual-list-box.min.js"/>
<script type="text/javascript" src="http://trirand.com/blog/jqgrid/js/i18n/grid.locale-en.js"></script>

<script type="text/javascript">

 	$( document ).on('refresh${className}Grid', function() {
        populate${className}Grid();
    });

	$( document ).ready(function()
	{
		$('#selectFor${className}').DualListBox();
		
		var rowNum, height;
		var caption = '';
		var modelUrl = '<%=request.getParameter( "modelUrl" )%>';
		
		if (  modelUrl == 'null' )
		{
			caption 	= '${className} List';
			modelUrl 	= '${pageContext.request.contextPath}/${className}/viewAll';
			rowNum		= 20;
			height		= 280; 
		}
		else
		{
			rowNum		= 5;
			height		= 100;
			$('#dualListButtonFor${className}DivId').show(); 
		}
			
        var grid = jQuery("${jQueryGridTableId}");
        grid.jqGrid({
        	url: modelUrl,
			datatype: "json",
			#foreach( $pk in $pkAttributes )
				#set( $modifiedName = ${Utils.noWhiteSpaceNoDots( ${pk.getName()} )} )
				#set( $phrase = ${Utils.turnIntoPhrase( ${pk.getName()} )} )
				#set( $colNames = "${colNames}'${phrase}'," )
				#set( $colModels = "${colModels}{name:'${pk.getName()}',index:'${pk.getName()}',hidden:true}," )
			#end
			#foreach( $attribute in $directAttributes )
				#if ( $attribute.isUserViewable() == true && $attribute.isFromAssociation() == false && $velocityCount <= 4 ) ## only show 4 cols max
				#set( $modifiedName = ${Utils.noWhiteSpaceNoDots( ${attribute.getName()} )} )
				#set( $modifiedName = $Utils.lowercaseFirstLetter( $modifiedName ) )
				#set( $phrase = ${Utils.turnIntoPhrase( ${attribute.getName()} )} )
					#set( $colNames = "${colNames}'${phrase}'," )
					#set( $colModels = "${colModels}{name:'${modifiedName}',index:'${modifiedName}',align:'center'}," )
			    #end
			#end	 
			colNames:[ ${colNames} ],
   			colModel:[ ${colModels} ],
   			loadtext: "Loading ${className} List",
			altRows:true,
			sortable:true,
			autowidth:true,
			shrinkToFit : true,
			multiselect:true,
			pager:'${jQueryClassName}Pager',
		    rowNum:rowNum,
		    height:height,
		    rownumbers:true,
		    loadonce:false,
		    gridview:true,
		    jsonReader: {root:'rows',page:'page',total:'total',records:'records',repeatitems:false},
		    rowList: [5, 10, 20, 50],
		    viewrecords: true,
		    pgtext : "<span style='font-size: 0.8em'>Page {0} of {1}</span>",
		    recordtext: "<span style='font-size: 0.8em'>View {0} - {1} of {2}</span>",		    
		    caption: caption
		}); 
	});

	jQuery('${jQueryGridTableId}').dblclick(function () 
	{
		edit${className}();
	}); 			    

	function add${className}()
	{
		var title = "Add New ${className}";
		var url	= '<%=request.getParameter( "addUrl" )%>';		
		var eventToFire = "refresh${className}Grid";
		
		if( url == 'null' )
			url = "${pageContext.request.contextPath}/jsp/${className}ProfileForm.jsp?action=create"
			
		inspectionDialog( title, url, eventToFire );
	}

	function edit${className}()
	{
		var id 	= getSelectedIdFromGrid( jQuery('${jQueryGridTableId}'), '${pkName}' );
	
		if ( id != null )
		{
			var title = "Edit ${className}";
			var url = '${pageContext.request.contextPath}/jsp/${className}ProfileForm.jsp?action=edit&${lowercaseName}.${pkName}=' + id;
			
			var eventToFire = "refresh${className}Grid";
			
			inspectionDialog( title, url, eventToFire );
		}
		else
		{
			BootstrapDialog.alert('Please first select a ${className}');
		}	
	}
	
	function delete${className}()
	{
		var ids 	= getSelectedIdsFromGrid( jQuery('${jQueryGridTableId}'), '${pkName}' );
		if ( ids == null )
		{
			BootstrapDialog.alert('Please first select a ${className}');
		}
		else
		{
	    	BootstrapDialog.confirm
	    	({
	        	title: 'WARNING',
	        	message: 'Are you sure?',
	            type: BootstrapDialog.TYPE_WARNING, // <-- Default value is BootstrapDialog.TYPE_PRIMARY
	            closable: true, // <-- Default value is false
	            btnCancelLabel: 'No', // <-- Default value is 'Cancel',
	            btnOKLabel: 'Yes', // <-- Default value is 'OK',
	            btnOKClass: 'btn-warning', // <-- If you didn't specify it, dialog type will be used,
	            callback: function(result) 
	            {
	                // result will be true if button was click, while it will be false if 
	                // users close the dialog directly.
	                if(result) 
	                {
	                	var url = '<%=request.getParameter( "deleteUrl" )%>';
	                	
	                	if ( url == 'null' )
	                		url = '${pageContext.request.contextPath}/${classObject.getName()}/delete?a=a';
	                	
	                	for (i = 0; i < ids.length; i++)
	                	{
	                		url = url + '&childIds=' + ids[i];
	                	}
	                							
						jQuery.ajax(
						{
					  		url: url,
					  		dataType: 'json',
						}).always(function( data ) 
						{
							populate${className}Grid()
						});		
	                }
	            }
	    	});
	    }
	}

	function add${className}FromAllList()
	{
		var sourceUrl	= '${pageContext.request.contextPath}/${className}/viewAll';
		var modelUrl 	= '<%=request.getParameter( "modelUrl" )%>';
		var value 		= '${pkName}';
		var text 		= '${firstAttribName}';
		var roleName	= '<%=request.getParameter( "roleName" )%>';
		
		if ( roleName == 'null' )
			roleName = 'All-${className}';
			
		multiselect( sourceUrl, modelUrl, roleName, value, text, assign${className}SelectionsFromAllList );  
	}

	function assign${className}SelectionsFromAllList( ids )
	{
		var url = '<%=request.getParameter( "parentUrl" )%>';
		for (i = 0; i < ids.length; i++)
    	{
    		url = url + '&childIds=' + ids[i];
    	}
				
		jQuery.ajax(
		{
	  		url: url,
	  		dataType: 'json',
		}).always(function( data ) 
		{
			populate${className}Grid()
		});		
	}
	
	function populate${className}Grid()
	{
		$('#loadingDivId').show();
		$('${jQueryGridTableId}').trigger( 'reloadGrid' );
		$('#loadingDivId').hide();			
	}
	
</script>

<table id="${className}GridTableId"></table>
<div id="${className}Pager" style="font-size:0.9em"></div>
<div>
<br>
<table
  <tr>
    <td>
		<a href="#" data-toggle="tooltip" data-placement="below" title="refresh" onclick="populate${className}Grid()" >
		    <button type="button" class="btn btn-outline-primary">
		      <span class="glyphicon glyphicon-refresh">
		      </span>
			</button>
		</a>
		<a href="#" data-toggle="tooltip" data-placement="below" title="add ${classObject.getName()}" onclick="add${className}()">
		    <button type="button" class="btn btn-outline-primary">
		      <span class="glyphicon glyphicon-plus">
		      </span>
			</button>
		</a>
		<a href="#" data-toggle="tooltip" data-placement="below" title="edit ${classObject.getName()}" onclick="edit${className}()" >
		    <button type="button" class="btn btn-outline-primary">
		      <span class="glyphicon glyphicon-pencil">
		      </span>
			</button>
		</a>
		<a href="#" data-toggle="tooltip" data-placement="below" title="delete ${classObject.getName()}" onclick="delete${className}()">
		    <button type="button" class="btn btn-outline-primary">
		      <span class="glyphicon glyphicon-trash">
		      </span>
			</button>
		</a>
	  </td>
	  <td>
		<div id="dualListButtonFor${className}DivId" style="display:none">
		  <a href="#" data-toggle="tooltip" data-placement="below" title="add ${classObject.getName()} From List" onclick="add${className}FromAllList()">
		    <button type="button" class="btn btn-outline-primary">
		      <span class="glyphicon glyphicon-th-list">
		      </span>
			</button>
		  </a>
		<div>
 	  </td>
 	</tr>
</table>

<div id="loadingDivId" style="display:none;color:black">
  loading all ${className} entities...<image src="${pageContext.request.contextPath}/img/load_new.gif" width=48 height=48/>
</div>				  				  


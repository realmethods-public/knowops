#set( $className = $classObject.getName() )
#set( $lowercaseClassName = $Utils.lowercaseFirstLetter( $className ) )
#set( $includePKs = true )
#set( $attributeCollections = $classObject.getAttributesOrderedAsCollectionsInHierarchy( $includePKs ) )
#set( $pkAttributes = $attributeCollections.get(0) )
#set( $directAttributes = $attributeCollections.get(1) )
#set( $compositeAttributes = $attributeCollections.get(2) )
#set( $singleAssociations = $classObject.getSingleAssociationsWithSourceRole() )
#set( $readOnly = false )
#set( $pk = ${classObject.getFirstPrimaryKey()} )		
#set( $pkExpression = "${lowercaseClassName}.${pk.getName()}" ) 
#set( $jQueryClassName = "#${className}" )
#set( $jQueryLoadingDivId = "${jQueryClassName}FormLoadingDivId")
#set( $jQueryProfileFormId = "${jQueryClassName}ProfileFormId")
#set( $jQueryAssociationDivId = "${jQueryClassName}AssociationDivId")
<%
  String parentId	= null;
  String parentUrl	= null;
  String addUrl 	= null;
  String deleteUrl 	= null;
  String modelUrl 	= null;
%>	              
<link href="${pageContext.request.contextPath}/css/toggle.checkbox.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/css/bootstrap-datetimepicker.css" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-mask-as-number.js"></script>

<style>
  @import url('http://code.ionicframework.com/ionicons/2.0.0/css/ionicons.min.css');

	* {
		margin: 0;
		padding: 0;
		box-sizing: border-box;
		font-family: 'Lato', sans-serif;
	}

</style>

<script>
    $('.maskedTextField').maskAsNumber();
</script>

<script type="text/javascript">
    $(".form_datetime").datetimepicker({format: 'yyyy-mm-dd hh:mm:ss',autoclose: true,todayBtn: true});
</script>             

<script type="text/javascript">

	$( document ).ready(function()
	{
		// initialize the dom selectizing select controls
		
		$( '.selectizing_class' ).each(function()
		{
			$(this).selectizing();
		});

		var action = '<%=request.getParameter("action")%>';

	 	if ( action == 'edit' )
			$( '${jQueryAssociationDivId}' ).show();
	 
	 	var id = '<%=request.getParameter("${pkExpression}")%>';
#set( $includeComposites = false )
#foreach( $association in $classObject.getSingleAssociations( ${includeComposites} ) )
	#set( $roleName = $association.getRoleName() )
	#set( $associationClassType = $association.getType() )
	#set( $childClassObject = $aib.getClassObject( $associationClassType) )
	#set( $childKeyFieldName = $childClassObject.getAllPrimaryKeysInHierarchy().iterator().next().getName() )		
	#set( $childFirstAttribName = $childClassObject.findAttributeAsBestFitName() )
	#set( $inputName = "${roleName}InputId" )
		
		$( "#multiSelectFor${roleName}" ).on("change", function(e)  
		{
			//assign the selected value
			var input = document.getElementById("${roleName}InputId");				
			input.value = this.options[this.selectedIndex].text;
	
			if ( input.value != 'no selection' )
			{
				// save the ${roleName}
				var parentId = document.getElementById( '${pkExpression}' ).value;
		 		var childId = this.options[this.selectedIndex].value;
		 		var url 	= '${pageContext.request.contextPath}/${className}/save${roleName}.?${pkExpression}=' + parentId + "&childId=" + childId;
	
				jQuery.ajax(
				{
			  		url: url,
			  		dataType: 'json',
				}).always(function( data ) 
				{
					doneSaving${className}();
				});
			}
			// hide the select
			$("#multiSelectFor${roleName}DivId").hide();
		});
	
	 	$( document ).on('refresh${roleName}For${className}', function() 
	 	{			
	 		// reload ${className} and apply the ${roleName}
	 		var url 	= '${pageContext.request.contextPath}/${classObject.getName()}/load?${pkExpression}=' + id;
	 		var element = document.getElementById( '${roleName}InputId' );
	 		var field 	= '${Utils.lowercaseFirstLetter( ${roleName} )}.${childFirstAttribName}';
	 		
			jQuery.ajax(
			{
		  		url: url,
		  		dataType: 'json',
			}).always(function( data ) 
			{
				var val;
				var indexes = field.split( "." );
				
				if ( indexes.length == 1 )
					val = data[indexes[0]];
				else if ( indexes.length == 2 )
					val = data[indexes[0]][indexes[1]];

		 		element.value = val;
			});
	    });
#end

		// if edit, populate form fields		
		if ( action == 'edit' )
		{
			var id = '<%=request.getParameter("${pkExpression}")%>';
			
			jQuery.ajax(
			{
		  		url: '${pageContext.request.contextPath}/${classObject.getName()}/load?${pkExpression}=' + id,
		  		dataType: 'json',
			}).always(function( data ) 
			{
				var allControls = document.querySelectorAll("input, select, textArea");

				for(var i=0; i < allControls.length; i++)
				{
					var element = allControls[i];
					var elementName = element.name.replace( "${lowercaseClassName}.", "" );
					var val;
					
					var indexes = elementName.split( "." );
					if ( indexes.length == 1 )
						val = data[indexes[0]];
					else if ( indexes.length == 2 )
						val = data[indexes[0]][indexes[1]];

					if ( val !== undefined && element.type )
					{
						if ( element.type === 'checkbox' )
						{
							if ( val == true )
								element.checked = true;
							else
								element.checked = false;
						}
						else if ( element.type === 'select' )
						{
							// select the right option
						}
						element.value = val;
					}
				}
			});
		}
	});
	
	function doneSaving${className}()
	{
		document.getElementById("${className}FormStatusDivId").innerHTML = '<span style="color:blue">done saving</span>';
	}
	
	function reset${className}()
	{
		document.getElementById("${className}ProfileFormId").reset();
	}
	
	function save${className}()
	{
		$('${jQueryLoadingDivId}').show();

		// need to force the val to be handled by the Boolean type
		$("${jQueryProfileFormId} input:checkbox").each(function()
		{
    		if ( $(this).is(':checked') == false ) 
    			$(this).val( 'false' );    			
    		else
    			$(this).val('true');
  		});

		var url 		= '${pageContext.request.contextPath}/${classObject.getName()}/save';		
		var parentUrl 	= '<%=request.getParameter("parentUrl")%>';
		var formData 	= $('${jQueryProfileFormId}').serialize();
		var action 		= '<%=request.getParameter("action")%>';
		
		if ( action == 'create' ) 
			url = '${pageContext.request.contextPath}/${classObject.getName()}/create';
			
		jQuery.ajax(
		{
	  		url: url,
	  		dataType: 'json',
	  		data : formData,
		}).always(function( data ) 
		{
			if ( parentUrl != 'null' )
			{
				parentUrl = parentUrl + '&childId=' + data['${pk.getName()}'];
				jQuery.ajax(
				{
			  		url: parentUrl,
			  		dataType: 'json',
				}).always(function( data ) 
				{
				});
			}

			if ( action == 'create' )
				$( '${jQueryAssociationDivId}' ).show();

		    $('${jQueryLoadingDivId}').hide();			
				
			doneSaving${className}();
		});
		
	
	}
#foreach( $singleAssociation in $classObject.getSingleAssociationsWithoutSourceRole() )
	#if ( $singleAssociation.isListable() == true && $singleAssociation.isComposite() == false )
		#set( $roleName = $singleAssociation.getRoleName() )
		#set( $associationClassType = $singleAssociation.getType() )
		#set( $childClassObject = $aib.getClassObject( $associationClassType) )
		#set( $childKeyFieldName = $childClassObject.getAllPrimaryKeysInHierarchy().iterator().next().getName() )		
		#set( $ownerName = $className )
		#set( $param = "${lowercaseClassName}.${Utils.lowercaseFirstLetter( ${roleName} )}.${childKeyFieldName}}" )
		#set( $childFirstAttribName = $childClassObject.findAttributeAsBestFitName() )
	function add${roleName}()
	{
		addHelperFor${className}( '${associationClassType}', '${roleName}' );
	}
	
	function delete${roleName}()
	{ 
		deleteHelperFor${className}( '${roleName}', '${childFirstAttribName}' );
	}

	function add${roleName}FromList()
	{
		// display the multi-select (viewAll and ${roleName} lists)
		$("#multiSelectFor${roleName}DivId").show();

		jQuery.ajax(
		{
		    url: "${pageContext.request.contextPath}/${associationClassType}/viewAll",
		    dataType: 'json',
		}).always(function( data ) 
		{
			$("#multiSelectFor${roleName}").empty();
			
			var selectId	= "multiSelectFor${roleName}";
			var text 		= '${childFirstAttribName}';
			var value 		= '${childKeyFieldName}';
			var includeBlank = true;
			
			loadOptionsWithJSONData( selectId, data, value, text, includeBlank  );
		});
	}
		
    #end
#end
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
	#if ( $multiAssociation.isListable() == true )
		#set( $roleName = $multiAssociation.getRoleName() )
		#set( $associationClassType = $multiAssociation.getType() )
		#set( $childClassObject = $aib.getClassObject( $associationClassType) )
		#set( $childKeyFieldName = $childClassObject.getAllPrimaryKeysInHierarchy().iterator().next().getName() )		
		#set( $ownerName = $className )
		#set( $param = "${lowercaseClassName}.${Utils.lowercaseFirstLetter( ${roleName} )}.${childKeyFieldName}}" )
		#set( $childFirstAttribName = $childClassObject.findAttributeAsBestFitName() )
	function add${roleName}()
	{
		addHelperFor${className}( '${associationClassType}', '${roleName}' );
	}

	function delete${roleName}()
	{
		var ids;
		deleteHelperFor${className}( '${roleName}', '${childFirstAttribName}', ids );
	}
	
	function add${roleName}FromList()
	{
	}
	
    #end
#end
    function addHelperFor${className}( associationClassType, roleName )
	{    
		var title = "Add New " + associationClassType + " for ${className} " + roleName;
		var parentId = document.getElementById("${pkExpression}").value;
		parentUrl = '${pageContext.request.contextPath}/${className}/save' + roleName + '?${pkExpression}=' + parentId;
		var url = '${pageContext.request.contextPath}/jsp/' + associationClassType + 'ProfileForm.jsp?action=create&parentUrl=' + parentUrl + '&parentName=${lowercaseClassName}&roleName=' + roleName;
		var eventToFire = 'refresh' + roleName + 'For${className}';
		inspectionDialog( title, url, eventToFire );
    }

	function deleteHelperFor${className}( nameOfRole, keyFieldName, ids )
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
				var elementName = "${lowercaseClassName}." + nameOfRole.toLowerCase() + "." + keyFieldName;
				var element 	= document.getElementsByName(elementName)[0];
				var url 		= '${pageContext.request.contextPath}/${classObject.getName()}/delete' + nameOfRole + '';
				
				url = url + '?childIds=' + ids;
				
				jQuery.ajax(
				{
			  		url: url,
			  		dataType: 'text',
			  		data : $('${jQueryProfileFormId}').serialize(),
				}).always(function( data ) 
				{
					document.getElementById("${className}FormStatusDivId").innerHTML = '<span style="color:blue">done deleting</span>';
					element.value = ""; // clear it
				});
			}
    	});
	
	}
</script>

<div id="${className}ProfileFormDiv" style="padding:4px;border:2px solid lightgray">
  <form id="${className}ProfileFormId" class="formTableClass">
    <input type=hidden id="${pkExpression}" name="${pkExpression}" />  		  	
########################################################
## First output the direct attributes of the class
########################################################
<!-- Direct Attributes -->          	
#set( $parentPrefix = "" )
#generateStrutsFormFields( $directAttributes $parentPrefix )

##################################################################
## Next, output the composites from single associations as fields
##################################################################
<!-- Composites -->          	
## Next, output the Composites
    <table class="formTableClass">
#foreach( $composite in $compositeAttributes)
	#if ( $composite.isFromSingleValueAssociation() == true )
	  <tr class="formRowClass">		
	    <td bgcolor="#0000ff" colspan="2" align="left">
		  <b><font color="#fffff+f">${composite.getDisplayName()}</font></b>
		 </td>
		    #set( $parentPrefix = "${Utils.lowercaseFirstLetter( ${composite.getName()} )}" )
		    #generateStrutsFormFields( $composite.getAttributes() $parentPrefix )
	  </tr>
	#end ##if ( $composite.isFromAssociation() == true )
#end  		  	
    </table>
  	<br>
	<div>
		<a href="#" data-toggle="tooltip" data-placement="below" title="save ${classObject.getName()}" onclick="save${className}()">
		    <button type="button" class="btn btn-outline-primary">
		      	Save
			</button>
		</a>
		<a href="#" data-toggle="tooltip" data-placement="below" title="reset" onclick="reset${className}()">
		    <button type="button" class="btn btn-outline-primary">
		      	Reset
			</button>
		</a>
		<div id="${className}FormLoadingDivId" style="display:none;color:black">
  			saving $className...<image src="${pageContext.request.contextPath}/img/load_new.gif" width=48 height=48/>
		</div>				  				  
		<div id="${className}FormStatusDivId">
		</div>	
	</div> 
  </form>
</div>

<div id="${className}AssociationDivId" style="display:none">
  <div class="singleAssociationClass">
    <table class="associationTableClass">
##################################################################
## Next, output the single associations
##################################################################
#foreach( $singleAssociation in $classObject.getSingleAssociationsWithoutSourceRole() )
	#if ( $singleAssociation.isListable() == true && $singleAssociation.isComposite() == false )
		#set( $roleName = $singleAssociation.getRoleName() )
		#set( $associationClassType = $singleAssociation.getType() )
		#set( $childClassObject = $aib.getClassObject( $associationClassType) )
		#set( $childKeyFieldName = $childClassObject.getAllPrimaryKeysInHierarchy().iterator().next().getName() )
		#set( $childFirstAttribName = $childClassObject.findAttributeAsBestFitName() )
		#set( $ownerName = $className )
		#set( $inputName = "${lowercaseClassName}.${Utils.lowercaseFirstLetter( ${roleName} )}.${childFirstAttribName}" )	
	  <tr class="formTRClass">
	    <td class="formTDClass">
		  <div class="sectionClass">
		    <div class="sectionHeaderClass">
		      <span class="sectionTitleClass">
		        ${roleName}
		      </span>
		    </div>
		    <div class="sectionBodyClass">	  
	          <table class="formTableClass">
	            <tr class="formTRClass">
	              <td class="formTDClass">
	      		    <input type='text' id="${roleName}InputId" name="${inputName}" class="form-control" disabled/>
	      	      </td>
	              <td  class="formTDClass" style="vertical-align:center">
	                <div id="multiSelectFor${roleName}DivId" style="display:none">
	                  <select id="multiSelectFor${roleName}" />
	                  <a href="#" onclick="$('#multiSelectFor${roleName}DivId').hide();">
				        <button type="button" class="btn btn-outline-primary">
				          <span class="glyphicon glyphicon-remove"/>
				 	    </button>
				      </a>
	                </div>
				    <a href="#" data-toggle="tooltip" data-placement="below" title="create ${associationClassType}" onclick="add${roleName}()">
  				      <button type="button" class="btn btn-outline-primary">
				        <span class="glyphicon glyphicon-plus"/>
					  </button>
				    </a>
				    <a href="#" data-toggle="tooltip" data-placement="below" title="add ${associationClassType} from list" onclick="add${roleName}FromList()">
				      <button type="button" class="btn btn-outline-primary">
				        <span class="glyphicon glyphicon-th-list"/>
					  </button>
				    </a>
				    <a href="#" data-toggle="tooltip" data-placement="below" title="delete ${associationClassType}" onclick="delete${roleName}()">
				      <button type="button" class="btn btn-default btn-sm">
				        <span class="glyphicon glyphicon-trash"/>
				 	  </button>
				    </a>
	              </td>
	            </tr>
		      </table>
		    </div>
		  </div>
	    </td>
	  </tr>
	#end 
#end
	</table>
  </div>

  <div "class=multipleAssociationClass">  
	<table class="associationTableClass">  
##################################################################
## finally, output the multiple associations
##################################################################
#foreach( $multiAssociation in $classObject.getMultipleAssociations() )
	#if ( $multiAssociation.isListable() == true )
		#set( $roleName = $multiAssociation.getRoleName() )
		#set( $associationClassType = $multiAssociation.getType() )
	  <tr class="formRowClass">
	    <td class="formTDClass">
		  <div class="sectionClass">
		    <div class="sectionHeaderClass">
		      <span class="sectionTitleClass">
		        ${roleName}
		      </span>
		    </div>
		    <div class="sectionBodyClass">	  
	          <table class="formTableClass">
	            <tr class="formTRClass">
	              <td class="formTDClass">
<%
  parentId	= "${pkExpression}=" + request.getParameter("${pkExpression}");
  parentUrl	= "/${className}/save${roleName}?" + parentId;
  addUrl 	= "/jsp/${associationClassType}ProfileForm.jsp?action=create&parentUrl=" + parentUrl;
  deleteUrl = "/${className}/delete${roleName}?" + parentId;
  modelUrl 	= "/${className}/load${roleName}?" + parentId;
%>	              
	                <jsp:include page="${associationClassType}ViewAllList.jsp">
	                	<jsp:param name="roleName" value="${roleName}"/>
	                	<jsp:param name="addUrl" value="<%=addUrl%>"/>
	                	<jsp:param name="deleteUrl" value="<%=deleteUrl%>"/>
	                	<jsp:param name="modelUrl" value="<%=modelUrl%>"/>
	                	<jsp:param name="parentUrl" value="<%=parentUrl%>"/>
	                </jsp:include>
	      	      </td>
	            </tr>
		      </table>
		    </div>
		  </div>
	    </td>
	  </tr>
	#end 
#end
	</table>

  </div>
</div>
			  				  



// global declarations
var containers, subsystems, components, classes, relations, attributes;


function buildModelTreeUsingModelData( modelData ) {
	// reset the global folder open indicator for the tree
	globalFolderOpen =  false;
	
	// update the package, fd, and model div
	updateTechStackModelStatusDiv();

	// reset counters
	containers = 0;
	subsystems = 0;
	components = 0;
	classes = 0;
	relations = 0;
	attributes = 0;
	
	// load the tree  
	var content = '<ul >';
	content = content + treeContainersOutput( modelData );
	content = content + treeSubsystemsOutput( modelData );
	content = content + treeComponentsOutput( modelData );
	content = content + treeClassesOutput( modelData );
	content = content + treeServicesOutput( modelData );
	content = content + treeInterfacesOutput( modelData );
	content = content + treeEnumsOutput( modelData );
	content = content + treeUserInterfacesOutput( modelData );
	content = content + '</ul>';
		
	//=====================================================				
	// show the tree's parent div
	//=====================================================		
	$('#modelTreeDivId').show();
		
	//document.getElementById("modelTreeId").innerHTML = content;

	// refresh the tree
	$('#modelTreeId').jstree(true).settings.core.data = content;
	$('#modelTreeId').jstree(true).refresh();

	var results;
	results = "<span style='font-size:0.9em'>Totals: Containers(" + containers + ")"
		+ ", Classes(" + classes + ")"
		+ ", Relations(" + relations + ")"
		+ ", Attributes(" + attributes + ')</span><br>'
		/*+ '<label for="primaryKeyId" style="color:black;font-size:0.8em">primary key type, leave blank for default:</label>'
		+ "<input type='text' size='80' id='primaryKeyId'/>"*/
		+ "<button class='btn'  aria-hidden='true' onclick='viewModelAsJson()'>View as JSON</button>";
		
	document.getElementById("modelTreeResultsId").innerHTML = results;
	
	return content;
}

//==========================================================
// build the containers tree node
//==========================================================

function treeContainersOutput( modelData ) {
	var content = '';

	if ( typeof( modelData.containers ) != "undefined" && modelData.containers.length > 0)
	{					
//		console.log( 'containers total : ' + modelData.containers.length );

		content = content + "<li ";
		
		if ( globalFolderOpen == false )
		{
			content = content + 'class=\"jstree-open\"';
			globalFolderOpen = true;
		}
			 
		content = content + " data-jstree='{\"icon\":\"img/container.png\"}'  style='color:black'>Containers";
		content = content + "<ul>";				

		for( counterB = 0; counterB < modelData.containers.length; counterB++ )
		{
			containers = containers + 1;
		
			content = content + applyContainerData( modelData.containers[counterB], 'container_' );		
		}
		content = content + "</ul></li>";
	}
	
	return( content );
}


//==========================================================
// build the subsystems tree node
//==========================================================

function treeSubsystemsOutput( modelData ) {
	var content = '';

	if ( typeof( modelData.subsystems ) != "undefined" && modelData.subsystems.length > 0)
	{					
		//console.log( 'subsystems total : ' + modelData.subsystems.length );

		content = content + "<li ";
		
		if ( globalFolderOpen == false )
		{
			content = content + 'class=\"jstree-open\"';
			globalFolderOpen = true;
		}
			 
		content = content + " data-jstree='{\"icon\":\"img/subsystem.png\"}'  style='color:black'>Subsystems";
		content = content + "<ul>";				

		for( counter = 0; counter < modelData.subsystems.length; counter++ )
		{
			subsystems = subsystems + 1;
		
			content = content + applySubsystemData( modelData.subsystems[counter], 'subsystem_' );		
		}
		content = content + "</ul></li>";
	}
	
	return( content );
}

	
//==========================================================
// build the components tree node
//==========================================================

function treeComponentsOutput( modelData ) {
	var content = '';
	if ( typeof( modelData.components ) != "undefined" && modelData.components.length > 0)
	{					
		content = content + "<li ";
		
		if ( globalFolderOpen == false )
		{
			content = content + 'class=\"jstree-open\"';
			globalFolderOpen = true;
		}
		
		content = content + " data-jstree='{\"icon\":\"img/component.png\"}' style='color:black'>Components";
		content = content + "<ul>";				

		for( index_99 = 0; index_99 < modelData.components.length; index_99++ )
		{
			components = components + 1;
			content = content + applyComponentData( modelData.components[index_99], 'component_'  );			
		}
		content = content + "</ul></li>";
	}
	
	return( content );
}

//==========================================================
// build the classes tree node
//==========================================================

function treeClassesOutput( modelData )
{
	var content = '';
	if ( typeof( modelData.classes ) != "undefined" && modelData.classes.length > 0 )
	{	
		content = content + "<li ";
		
		if ( globalFolderOpen == false )
		{
			content = content + 'class=\"jstree-open\"';
			globalFolderOpen = true;
		}
		
		content = content + " data-jstree='{\"icon\":\"img/classes.png\"}'>Classes";				
		content = content + "<ul>";
		
		for( i = 0; i < modelData.classes.length; i++ )
		{
			content = content + applyClassData( modelData.classes[i], 'class_'  );			
		}
		content = content + "</ul></li>";
	}
	
	return( content );
	
}

//==========================================================
// build the service tree node
//==========================================================

function treeServicesOutput( modelData )
{		
	var content = '';
	if ( typeof( modelData.serviceClasses ) != "undefined" && modelData.serviceClasses.length > 0 )
	{									
		content = content + "<li data-jstree='{\"icon\":\"img/services.png\"}'>Services";
		content = content + "<ul>";	
		
		components = 0;						// reset the global service count
		
		for( j = 0; j < modelData.serviceClasses.length; j++ )
		{
			content = content + applyClassData( modelData.serviceClasses[j], 'serviceClass_'  );			
		}
		content = content + "</ul></li>";
	}
	
	return content;
}				


//==========================================================
// build the interface tree node
//==========================================================

function treeInterfacesOutput( modelData )
{
	var content = '';
	if ( typeof( modelData.interfaces ) != "undefined" && modelData.interfaces.length > 0 )
	{								
		content = content + "<li data-jstree='{\"icon\":\"img/interface.png\"}'>Interfaces";
		content = content + "<ul>";				
		for( k = 0; k < modelData.interfaces.length; k++ )
		{
			content = content + applyClassData( modelData.interfaces[k], 'interfaces_'  );			
		}
		content = content + "</ul></li>";				
	}
	
	return content;
}

//==========================================================
// build the enum tree node
//==========================================================

function treeEnumsOutput( modelData )
{
	var content = '';
	if ( typeof( modelData.enums ) != "undefined" && modelData.enums.length > 0 )
	{									
		content = content + "<li data-jstree='{\"icon\":\"img/enum.png\"}'>Enums";
		content = content + "<ul>";				
		for( l = 0; l < modelData.enums.length; l++ )
		{
			 content = content + applyClassData( modelData.enums[l], 'enumClass_' );			
		}
		content = content + "</ul></li>";
	}
	
	return content;
}
		
//==========================================================
// build the user interface tree node
//==========================================================
function treeUserInterfacesOutput( modelData )
{
	var content = '';
	if ( typeof( modelData.userInterfaces ) != "undefined" )
	{									
		content = content + "<li data-jstree='{\"icon\":\"img/userinterface.png\"}'>User Interfaces";
		content = content + "<ul>";				
		for( m = 0; m < modelData.userInterfaces.length; m++ )
		{
			content = content + applyClassData( modelData.userInterfaces[m], 'uiClass_' );			
		}
		content = content + "</ul></li>";
		
	}
	
	return( content );
}				

//================================================
// function : apply the container data
//================================================
function applyContainerData( the_container, typePrefix )
{
	var content = '';
	content = content + "<li data-jstree='{\"icon\":\"img/folder.png\"}'" + 'id="' + typePrefix + the_container.name + '">' + the_container.name;

	content = content   + "<ul>"
						  + "<li data-jstree='{\"icon\":\"img/host.png\"}'>Host: " + forDisplay( the_container.host, 'none' ) + '</li>'
						  + "<li data-jstree='{\"icon\":\"img/port.png\"}'>Port: " + forDisplay( the_container.port, 'none' ) + '</li>';
	
	content = content   + "</ul>";

	var the_classes = the_container.classes;

	if ( the_classes !== undefined && the_classes.length > 0 )
	{
		content = content   + "<ul>";
		content = content 	+ "<li data-jstree='{\"icon\":\"img/classes.png\"}'>Classes";
		content = content 	  + "<ul>";				
	
		for( indx2 = 0; indx2 < the_classes.length; indx2++ )
		{
			content = content + applyClassData( the_classes[indx2], typePrefix + the_classes[indx2].name + "_component_" );
		}
		content = content     + '</ul>'
							+ '</li>'
						  + '</ul>';
	}
	the_classes = the_container.enums;
	if ( the_classes !== undefined && the_classes.length > 0 )
	{
		content = content   + "<ul>";
		content = content 	+ "<li data-jstree='{\"icon\":\"img/enum.png\"}'>Enums";
		content = content 	  + "<ul>";				
	
		for( indx223 = 0; indx223 < the_classes.length; indx223++ )
		{
			content = content + applyClassData( the_classes[indx223], typePrefix + the_classes[indx223].name + "_component_" );
		}
		content = content     + '</ul>'
							+ '</li>'
						  + '</ul>';
	}

	the_classes = the_container.services;

	if ( the_classes !== undefined && the_classes.length > 0 )
	{
		content = content   + "<ul>";
		content = content 	+ "<li data-jstree='{\"icon\":\"img/services.png\"}'>Services";
		content = content 	  + "<ul>";				
	
		for( indx22 = 0; indx22 < the_classes.length; indx22++ )
		{
			content = content + applyServiceData( the_classes[indx22], typePrefix + the_classes[indx22].name + "_component_" );
		}
		content = content     + '</ul>'
							+ '</li>'
						  + '</ul>';
	}

	the_classes = the_container.dtos;

	if ( the_classes !== undefined && the_classes.length > 0 )
	{
		content = content   + "<ul>";
		content = content 	+ "<li data-jstree='{\"icon\":\"img/services.png\"}'>DTOs";
		content = content 	  + "<ul>";				
	
		for( indx222 = 0; indx222 < the_classes.length; indx222++ )
		{
			content = content + applyDtoData( the_classes[indx222], typePrefix + the_classes[indx222].name + "_component_" );
		}
		content = content     + '</ul>'
							+ '</li>'
						  + '</ul>';
	}
		
	content = content + '</li>';
	
	return content;
}

//================================================
// function : apply the subsystem data
//================================================
function applySubsystemData( the_subsystem, typePrefix )
{
	var content = '';
	content = content + "<li data-jstree='{\"icon\":\"img/folder.png\"}'" + 'id="' + typePrefix + the_subsystem.name + '">' + the_subsystem.name;

	content = content   + "<ul>"
						  + "<li data-jstree='{\"icon\":\"img/parents.png\"}'>Parent: " + forDisplay( the_subsystem.parentName, 'none' ) + '</li>'
						  // + "<li data-jstree='{\"icon\":\"img/package.png\"}'>Package: " + forDisplay( the_subsystem.packageName, 'none' ) + '</li>'
						  + "<li data-jstree='{\"icon\":\"img/interface.png\"}'>Realizations: " + forDisplay( the_subsystem.interfaces, 'none' ) + '</li>';
	
	content = content   + "</ul>";

	var the_subsystems = the_subsystem.subsystems;
	
	if ( the_subsystems !== undefined && the_subsystems.length > 0 )
	{
		content = content   + "<ul>";
		content = content + "<li data-jstree='{\"icon\":\"img/container.png\"}'>Contained Subsystems";
		content = content   + "<ul>";				
	
		for( indx = 0; indx < the_subsystems.length; indx++ )
		{
			content = content + applySubsystemData( the_subsystems[indx], typePrefix + the_subsystems[indx].name + "_subsystem_" );
		}
		content = content   + '</ul></li></ul>';
	}
	
	var the_components = the_subsystem.components;

	if ( the_components !== undefined && the_components.length > 0 )
	{
		content = content   + "<ul>";
		content = content 	+ "<li data-jstree='{\"icon\":\"img/container.png\"}'>Contained Components";
		content = content 	  + "<ul>";				
	
		for( indx1 = 0; indx1 < the_components.length; indx1++ )
		{
			content = content + applyComponentData( the_components[indx1], typePrefix + the_components[indx1].name + "_component_" );
		}
		content = content     + '</ul>'
							+ '</li>'
						  + '</ul>';
	}

	var the_classes = the_subsystem.classes;

	if ( the_classes !== undefined && the_classes.length > 0 )
	{
		content = content   + "<ul>";
		content = content 	+ "<li data-jstree='{\"icon\":\"img/container.png\"}'>Contained Classes";
		content = content 	  + "<ul>";				
	
		for( indx2 = 0; indx2 < the_classes.length; indx2++ )
		{
			content = content + applyClassData( the_classes[indx2], typePrefix + the_classes[indx2].name + "_component_" );
		}
		content = content     + '</ul>'
							+ '</li>'
						  + '</ul>';
	}
	
	content = content + applyMethods( the_subsystem );
	
	content = content + '</li>';
	
	return content;
}

//================================================
// function : apply the component data
//================================================
function applyComponentData( the_component, typePrefix )
{
	var content = '';
	
	content = content + "<li data-jstree='{\"icon\":\"img/folder.png\"}'" + 'id="' + typePrefix + the_component.name + '">' + the_component.name;

	content = content + "<ul>"
					+ 	  "<li data-jstree='{\"icon\":\"img/parents.png\"}'>Parent: " + forDisplay( the_component.parentName, 'none' ) + '</li>'
					//+ 	  "<li data-jstree='{\"icon\":\"img/package.png\"}'>Package: " + forDisplay( the_component.packageName, 'none' ) + '</li>'
					+ 	  "<li data-jstree='{\"icon\":\"img/interface.png\"}'>Realizations: " + forDisplay( the_component.interfaces, 'none' ) + '</li>';
	content = content   + "</ul>";

	if ( the_component.components !== undefined && the_component.components.length > 0 )
	{
		content = content   + "<ul>";	
		content = content + "<li data-jstree='{\"icon\":\"img/container.png\"}'>Contained Components";
		content = content   + "<ul>";				
	
		for( indx6 = 0; indx6 < the_component.components.length; indx6++ )
		{
			content = content + applyComponentData( the_component.components[indx6], typePrefix + the_component.components[indx6].name + "_class_" );
		}
		
		content = content + '</ul></li></ul>';
	}
	
	if ( the_component.classes !== undefined && the_component.classes.length > 0 )
	{	
		content = content   + "<ul>";	
		content = content + "<li data-jstree='{\"icon\":\"img/container.png\"}'>Contained Classes";
		content = content + "<ul>";				
	
		var the_class;
		for( ind_x = 0; ind_x < the_component.classes.length; ind_x++ )
		{
			the_class = the_component.classes[ind_x];
			content = content + applyClassData( the_class, typePrefix + the_component.name + "_class_" );
		}
		
		content = content + '</ul></li></ul>';
	}

	content = content + applyMethods( the_component );
		
	content = content + '</li>';
	
	return content;
}

//================================================
// function : apply the class data
//================================================
function applyClassData( the_class, typePrefix )
{
	var content = "<li data-jstree='{\"icon\":\"img/folder.png\"}'" +  'id="' + typePrefix + the_class.name + '">' + the_class.name;
	var parent = forDisplay( the_class.parentName, 'none' );
	var realizations = forDisplay( the_class.interfaces, 'none' );

	// increment
	classes = classes + 1;
		
	// create children as attributes, associations, and methods data
	content = content 
		+ 	'<ul>'
		+ 		"<li data-jstree='{\"icon\":\"img/parents.png\"}'>Parent: " + parent + '</li>'
		// + 		"<li data-jstree='{\"icon\":\"img/package.png\"}'>Package: " + forDisplay( the_class.packageName, 'none' ) + '</li>'
		+ 		"<li data-jstree='{\"icon\":\"img/interface.png\"}'>Realizations: " + realizations + '</li>'		
		+  	'</ul>';
	content = content 
		+ 	applyClassAttributes( the_class );
	content = content			 
		+ 	applyClassAssociations( the_class ) 
		+ 	applyMethods( the_class )
		+ '</li>';
		
	if ( parent != 'none' )
		relations = relations + 1;
	
	if ( realizations != 'none' )
		relations = relations + 1;
		
	return content;
}
	

//================================================
// function : apply the service data
//================================================
function applyServiceData( the_service, typePrefix )
{
	var content = "<li data-jstree='{\"icon\":\"img/folder.png\"}'" +  'id="' + typePrefix + the_service.name + '">' + the_service.name;
		
	// create methods data
	content = content			 
		+ 	applyMethods( the_service )
		+ '</li>';
		
	return content;
}

//================================================
// function : apply the dto data
//================================================
function applyDtoData( the_dto, typePrefix )
{
	var content = "<li data-jstree='{\"icon\":\"img/folder.png\"}'" +  'id="' + typePrefix + the_dto.name + '">' + the_dto.name;
		
	// create attributes data
	content = content			 
		+ 	applyAttributes( the_dto)
		+ '</li>';
				
	return content;
}

//================================================
// function : apply the class data
//================================================

function applyClassAttributes( the_class )
{
	var content = '';

	if ( typeof( the_class.attributes ) != "undefined" && the_class.attributes.length > 0 )
	{
		content = "<ul>"
					+ "<li data-jstree='{\"icon\":\"img/attributes.png\"}'>Attributes"
						+ "<ul>";

		for( var z = 0; z < the_class.attributes.length; z++ )
		{
			attributes = attributes + 1;
			 
			content = content 
			+ "<li data-jstree='{\"icon\":\"img/folder.png\"}'" + ' id="' + the_class.name + '_attribute_' + forDisplay( the_class.attributes[z].name ) + '">' + forDisplay( the_class.attributes[z].name )
			+ 	'<ul>'
			+ 		'<li>Primary Key : ' + forDisplay( the_class.attributes[z].primarykey) + '</li>'
			+ 		'<li>Type : ' + forDisplay( the_class.attributes[z].type) + '</li>'
			+ 		'<li>Visibility : ' + forDisplay( the_class.attributes[z].visibility) + '</li>'
			+ 		'<li>Default : ' + forDisplay( the_class.attributes[z].defaultValue) + '</li>'
			+ 	'</ul>'
			+ '</li>';
		}
		content = content + '</ul></li></ul>';
	}
			
	return content;
}

//================================================
// function : apply the associations of the class
//================================================

function applyClassAssociations( the_class )
{
	var content = '';
	var multiplicity;
	
	if ( typeof( the_class.associations ) != "undefined" && the_class.associations.length > 0 )
	{
		content = "<ul>"
			+	    "<li data-jstree='{\"icon\":\"img/associations.png\"}'>Associations";

		for( a = 0; a < the_class.associations.length; a++ )
		{
			multiplicity = forDisplay( the_class.associations[a].multiplicity );
			
			content = content
			+ '<ul>'		 
			+   '<li id="' + the_class.name + '_association_' + forDisplay( the_class.associations[a].name ) + '">';
			content = content + forDisplay( the_class.associations[a].name )
			+ 	  '<ul>'
			+ 		'<li><span style="color:blue">Type : ' + forDisplay( the_class.associations[a].type) + '</span></li>'
			+ 		'<li><span style="color:blue">Multiplicity : ' + multiplicity + '</span></li>'
			+ 	  '</ul>'
			+   '</li>'
			+  '</ul>';
			
			relations = relations + 1;
			
		}
		content = content
			+    '</li>'
			+  '</ul>';
		
	}
			
	return content;
}

//================================================
// function : apply the methods of the class
//================================================

function applyMethods( the_parent )
{
	var content = '';

	if ( typeof(the_parent.methods ) != "undefined" && the_parent.methods.length > 0 )
	{		
//		console.log( 'methods are ' + the_parent.methods );
	
		content = "<ul><li data-jstree='{\"icon\":\"img/methods.png\"}'>Methods<ul>";
		var theObj;
		
		for( var method_index = 0; method_index < the_parent.methods.length; method_index++ )
		{
			theObj = the_parent.methods[method_index];
			content = content + "<li data-jstree='{\"icon\":\"img/folder.png\"}'" + 'id="' + the_parent.name + '_method_'  + forDisplay( theObj.name ) + '">' + forDisplay( theObj.name );
		}
		content = content + '</ul></li></ul>';
	}
	
	return content;
}



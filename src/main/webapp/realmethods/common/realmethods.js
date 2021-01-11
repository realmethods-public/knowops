/*******************************************************************************
 * realMethods Confidential
 * 
 * 2018 realMethods, Inc.
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'license.txt', which is part of this source code package.
 *  
 * Contributors :
 *       realMethods Inc - General Release
 ******************************************************************************/
//==============================================================
// global declarations
//==============================================================
var appVersion = '1.2'
var FILE_STORE_URL = "https://s3.amazonaws.com/goframework-cli-bucket/";
var LOADING_MODEL_PLEASE_WAIT_MSG = " loading...";
var MISSING_MODEL_FILE_MSG = " missing model file";
var stepper;
var currentStage = "Prepare";
var treema = null;

var globalAppOptions, globalModel, gSelectedPackageRow, gTechStackPackages, gOptions, genAppStats;
var globalModelName, globalModelData, gProjectData;
var globalFolderOpen 	= false;
var gSharedModels, gSelectedFrameworkPackage, gModelType;

var SHOW_APP_GEN_OPTION_EMPTY_REQUIRED_FIELDS = false;
		

//========================================================
//function : load the available packages from the server
//========================================================
function loadArchivedAppLibrary() {

	//========================================================
	// build the url
	//========================================================
	var url 			= "service.action?input=";
	var serviceRequest 	= {"serviceRequestType" : "ARCHIVED_APP_LIST" };
	var scope 			= $('#archivedAppSelectId option:selected').val();
	
	serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	//========================================================
	// clear the table of archived apps
	//========================================================
	document.getElementById("componentLibraryDivId").innerHTML = "";
	
	//========================================================
	// show the loading image
	//========================================================
	$('#archivesLoadingDivId').show();
	
	$.ajax(
	{
		url: url,
		dataType: 'json',
	}
	).done(function( data ) 
	{
		var result 	= JSON.parse(data.result);
		var content = '';

		if ( data.resultCode == "SUCCESS") {
			//========================================================
			// create the table of application components
			//========================================================
			content = content  
					+ 	"<div class='componentDivClass'>"
					+    "<table id='componentLibraryTableId' class='componentLibraryTabClass'>"
					+     "<tr ><thead>"
					+       "<th>&nbsp</th><th>Name</th>"
					+		"<th>Date-Time</th>"
					+		"<th>Description</th>"
					+		"<th>Scope</th>"
					+       "<th>Contributor</th>"
					+ 	  "</thead></tr>" 
			
			var rowClass, obj;
			for( var idx = 0; idx < result.length; idx++ ) {
				obj = result[idx];
				rowClass = idx%2 == 0 ? "componentLibraryRowClass1" : "componentLibraryRowClass2";
				content = content 
					+ "<tr class='" + rowClass +  "'>"
					+   "<td style='width:24px' id='" + obj.id + "'>"
					+     '<a href="' + FILE_STORE_URL + obj.filePath + '">'
		    		+       '<button type="button" class="btn btn-default btn-small"><span class="glyphicon glyphicon-arrow-down"></span></button>'
		      		+     '</a>'							 
		    		+    '</td>'
		    		+   "<td style='text-align:center'><div style = 'width:200px; word-wrap: break-word'>" + JSON.parse(obj.saveParams).name + "</td>"
					+ 	"<td style='text-align:center'>" + obj.dateTime  + "</td>"
					+ 	"<td style='text-align:left'><div style = 'width:350px; word-wrap: break-word'>" + JSON.parse(obj.saveParams).description + "<div></td>"
					+ 	"<td style='text-align:center'>" + obj.scopeType  + "</td>"
					+ 	"<td style='text-align:center'>" + obj.contributor  + "</td>"
					+ "</tr>";
			}
			content = content + '</table>';
			content = content + '</div>';			
		}
		else
			content = data.processingMessage;
			
		//========================================================	
		// hide the loading image
		//========================================================
		$('#archivesLoadingDivId').hide();

		//========================================================
		// apply the table content
		//========================================================
		document.getElementById('componentLibraryDivId').innerHTML = content;
	});
}


//********************************************************
//helper to populate the shared model table
//********************************************************
function loadModels() {

	//========================================================
	// build out the url
	//========================================================
	var url 			= "service.action?input=";
	var serviceRequest 	= {"serviceRequestType" : "MODEL_LIST" };
	var scope 			= $('#modelSelectId option:selected').val();
	
	serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	$('#modelsLoadingDivId').show();
	
	$.ajax(
	{
		url: 		url,
		dataType: 	'json',
	})
	.done(function( data ) 
	{
		var content = '';
		
		var result = JSON.parse(data.result);
		
		if ( data.resultCode == "SUCCESS") {
			//========================================================
			// build out the models table
			//========================================================
			gSharedModels = result;
			content = content  
					+ 	"<div style='height:200px;overflow-y:auto;padding:0px 10px 0px 0px'>"
					+    "<table id='sharedModelTableId' class='sharedModelTableClass'>"
					+     "<tr ><thead>"
					+       "<th style='display:none;text-align:left'></th><th style='display:none;text-align:left'><th style='text-align:left;display:none'></th><th style='text-align:left'>Type</th><th style='text-align:left'>Name</th><th style='text-align:left'>Description</th><th style='text-align:left'>Contributor</th>"
					+ 	  "</thead></tr>" 
			
			var obj;
			for( var idx = 0; idx < result.length; idx++ )
			{
				obj = result[idx];
				content = content + "<tr class='sharedModelRowClass'>"
							+		  "<td style='display:none;text-align:left' class='keyFieldClass shareModelTDClass' id='" 
											+ obj.id + "'>" + obj.id + "</td>"
							+		  "<td style='display:none;text-align:left' class='shareModelTDClass filePathClass'>" 
											+ obj.fileName + "</td>"
							+		  "<td style='display:none;text-align:left' class='shareModelTDClass modelTypeClass'>" 
											+ obj.modelType + "</td>"
							+		  '<td style="text-align:left;vertical-align: middle;" class="shareModelTDClass icon" style="width:28px;padding-left:4px"><img style="width:24px;height:24px" src="'  
											+ determineModelImageUrl(obj.modelType) + '"></td>'
							+ 		  "<td class='nameFieldClass shareModelTDClass'><b><div style = 'text-align:left;vertical-align: middle;word-wrap: break-word'>" 
											+ JSON.parse(obj.saveParams).name  + "</div></b></td>"
							+ 		  "<td class='shareModelTDClass'><div style = 'text-align:left;vertical-align: middle;word-wrap: break-word'>" 
											+ JSON.parse(obj.saveParams).description  + "</div></td>"
							+ 		  "<td class='shareModelTDClass'>" 
											+ obj.contributor + "</td>"											
						  	+ 		"</tr>";
			}
			content = content + '</table>';
			content = content + '</div>';
			
/*			
			content = content  + '<div class="checkbox checkbox-slider--b" style="display:none">' 	  			  	
						+ '<label>'
	    	      		+ '<input class="checkSpanClass" id="appendSharedModelCheckboxId" type="checkbox"/><span>Append To Loaded Model</span>' 
						+ '</label></div>';
*/						
		}
		else
			content = data.responseText;
		
		//========================================================
		// hide the loading image
		//========================================================
		$('#modelsLoadingDivId').hide();

		//========================================================
		// apply model table
		//========================================================
		$('#sharedModelBodyDivId').empty();
		$('#sharedModelBodyDivId').append( content );
	});
}

//******************************************************
// loads the selected model from the back-end and
// applies it to the model tree and elsewhere
//******************************************************
function loadSelectedModel() {
	var table 				= $('#sharedModelTableId');
	var row 				= table.find('tr.modelTableRowHighlight').eq( 0 );
	var id					= row.find('td.keyFieldClass').eq(0).attr('id');
	var modelFilePath		= row.find('td.filePathClass').eq(0).text();
	var modelType			= row.find('td.modelTypeClass').eq(0).text();

    //==============================================================
    // back end call, deduced by applying model type to the name
    //==============================================================
	var url 				= 'find' + modelType + 'Model?id=' + id; 
	var appendModelFlag = 'false';
	if ( $('#appendSharedModelCheckboxId').is(':checked') )
		appendModelFlag = 'true';				

	$('#sharedModelLoadingDivId').show();

	$.ajax(
	{
  		url: 		url,
  		dataType: 	'json',
	}
	).done(function( data ) 
	{
		var status = data;
		if ( status.done == false ) {
			var msg = handleStatusOutputCleanly( status );
			showStatusDialog( "Error Loading from Local Repository", msg );
		}
		else {
			globalModelName = modelFilePath;
			
			//===============================================
			// load the model into the model content div
			//===============================================  
			loadModelContent( data );

			//===============================================
			// update the stack and model div
			//===============================================		
			updateTechStackModelStatusDiv();
			
			//===============================================
			// hide the shared model dialog
			//===============================================
			$('#load_Shared_Model_Modal').modal("hide");
			
		}
		
		//===============================================
		// hide the loading div
		//===============================================
		$('#sharedModelLoadingDivId').hide();			
	});
}

//========================================
// function : load model content 
//========================================		
function loadModelContent( modelData ) {

	//========================================================
	// contains a model as Json as (modelAsJson) and
	// a LocalModel object as (localModel)
	//========================================================
	globalModelData = modelData;
	
	//========================================================
	// save the model as an object..might not be published
	// if the user requested to load a model without saving
	//========================================================
	globalModel = modelData.localModel;		

	//========================================================
	// function found in modeltreebuilder.js
	//========================================================
	buildModelTreeUsingModelData( JSON.parse(modelData.modelAsJson) );
}

//========================================================
// function : load the available packages from the server
//========================================================
		
function loadPackages() {
	
	//========================================================
	// build url to call backend
	//========================================================
	var url 			= "service.action?input=";
	var serviceRequest 	= {"serviceRequestType" : "TECH_STACK_PACKAGE_LIST" };
	var scope 			= $('#techStackSelectId option:selected').val();
	
	serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	//========================================================
	// clear the views
	//========================================================
	document.getElementById("frameworkPackageViewId").innerHTML = "";
	document.getElementById("packageInformationAreaId").innerHTML = "";
	
	//========================================================
	// show the loading image
	//========================================================
	$('#packagesLoadingDivId').show();
	
	$.ajax(
	{
  		url: url,
  		dataType: 'json',
	}
	).done(function( data ) 
	{
		//========================================================
		// hide the loading image
		//========================================================
		$('#packagesLoadingDivId').hide();

		//========================================================
		// assign globally
		//========================================================
		gTechStackPackages = JSON.parse(data.result);		
		
		//========================================================
		// show the tech stack packages
		//========================================================
		showPackages( gTechStackPackages );
		
		//========================================================
		// hint to get started
		//========================================================
//		document.getElementById("packageInformationAreaId").innerHTML = '<h5>Select a Package to see more details</h5>';
		
		//========================================================
		// no tech stack packages selected
		//========================================================
		gSelectedPackageRow = null;
		
		//========================================================
		// force an update to clear out any previously selected 
		// tech stack from the status div
		//========================================================
		updateTechStackModelStatusDiv();	
		
		//========================================================
		// update the project name in the title bar
		//========================================================
		updateProjectDiv();
		
	});
}

//========================================================
//function : display the packages into the relevant div
//========================================================
function showPackages( packages ) {
	var content = '<span id="tooltip_tech_stack_package_content">'
				+ '<div class="packageDivClass"><table style="width:100%;table-layout:fixed;">';

	var icon, packageName, appType, status, infoPageUrl, version, contributor, filePath;

	//========================================================	
	// create the package table header
	//========================================================
	content = content + '<thead><tr>'
						+ '<th style="width:40px"></th>'
						+ '<th>Tech Stack</th>'
						+ '<th>Contributor</th>'
						+ '<th>Type</th>'
						+ '<th>Version</th>'
						+ '<th>Status</th>'
						+ '<th style="display:none;">Id</th>'
						+ '<th style="display:none;">InfoPageUrl</th>'
						+ '<th style="display:none;">Name</th>'
						+ '<th style="display:none;">localZipFilePath</th>'
						+ '</tr></thead>';
	
	//========================================================
	// iterate through each framework package		
	//========================================================
	var id;
	for( var i=0; i < packages.length; i++ ) {
		packageName	= forDisplay( JSON.parse(packages[i].saveParams).name );
		icon		= packages[i].iconUrl;	
		infoPageUrl	= packages[i].infoPageUrl;
		appType		= packages[i].type;
		version		= packages[i].version;
		status		= packages[i].status;
		id			= forDisplay( packages[i].id );
		contributor = packages[i].contributor;
		filePath    = packages[i].localZipFilePath;

		//========================================================
		// build out the framework package table
		//========================================================
		content = content + '<tr class="packageRowClass">'
			+ '<td class="packageColumnClass icon" style="width:32px;padding-left:4px"><img style="width:32px;height:32px" src="' + icon + '"/></td>'
			+ '<td class="packageColumnClass packageName">' + packageName + '</td>' 
			+ '<td class="packageColumnClass contributor">' + contributor + '</td>'
			+ '<td class="packageColumnClass appType">' + appType + '</td>'
			+ '<td class="packageColumnClass version">' + version + '</td>'
			+ '<td class="packageColumnClass release"">' + status + '</td>'			
			+ '<td class="packageColumnClass packageId" style="display:none;">' + id + '</td>'
			+ '<td class="packageColumnClass infoPageUrl" style="display:none">' + infoPageUrl + '</td>'
			+ '<td class="packageColumnClass descriptor" style="display:none">' + packageName + '</td>'
			+ '<td class="packageColumnClass filePath" style="display:none">' + filePath + '</td>'

			+ '</tr>';
	}
									
	content = content + '</table></div></span>';
	
	//========================================================
	// output to the framework package div	
	//========================================================
	document.getElementById("frameworkPackageViewId").innerHTML = content;
}
		
//*****************************************************************
// helper function to column text for the named column
// of the currently selected tech stack package
//*****************************************************************
function getPkgRowSelection( columnClassName ) {
	if ( gSelectedPackageRow === undefined || gSelectedPackageRow == null)
		return '';
	else
		return gSelectedPackageRow.find( 'td.' + columnClassName ).text();	
}

//*****************************************************************
// Helper to determine if a tech stack package is selected
//*****************************************************************
function checkTechStackSelected() {

	//========================================================
	// if there is no package/framework selected...say so		 		
	//========================================================

	if ( getPkgRowSelection( "descriptor" ).length == 0 ) {
		//========================================================
		// show a warning to select a tech stack
		//========================================================
		showStatusDialog('Warning', 'Please select a Tech Stack Package.' );
		
		//========================================================
		// close all slide divs and open the package slide div
		//========================================================
    	closeOtherSlideDivs( packageSlideDiv );
    	packageSlideDiv.slideReveal("show", false);

    	return false;
	}
	
	return true;
}

//******************************************************
// handler for when a package is selected in the table
//******************************************************
function handlePackageSelection( row ) {
	
	highLightRow(row);
	
	gSelectedPackageRow = row;
		
	//===============================================
	// get the content of the selected tech stack 
	// package td's id
	//===============================================	
	var fd_selected_text = gSelectedPackageRow.find("td.descriptor").text();
	if ( fd_selected_text.length > 0 ) {						
		//===============================================
		// get the id of the selected tech stack pkg
		//===============================================
		var packageId 	= getPkgRowSelection( "packageId" );
		
		//===============================================
		// update the application options 
		//===============================================
		updateAppGenOptions( packageId ); 				

		//===============================================
		// update the package content area
		//===============================================
		updateTechStackDetails( packageId );
		
		//===============================================
		// update status div that shows which stack and model are selected
		//=============================================== 
		updateTechStackModelStatusDiv();
		
		//===============================================
		// update the href of the package download bttn
		//===============================================
		updateDownloadPackageHref()
		
	}
}

//=======================================================
// upon stack selection updates the content area with
// its details
//=======================================================

function updateTechStackDetails( packageId ) {
	
	//===============================================
	// show the loading div
	//===============================================
	$('#packageDetailsLoadingDivId').show();
	
	findTechStackPackage( packageId, function(pkg) {
	
		var xml 			= $.parseXML( pkg.packageXML );
		var packageXML		= $(xml).find('tech-stack-package');
		var content			= '<div class="packageDivClass"><div class="container col-lg-12">'
								+ '<div class="row justify-content-md-center">'
								+ '<div class="col-lg-4 text-left" style="vertical-align:top">';
		var lang_content	= "<ul>";
		
		//===============================================
		// loop through each target-languages
		//===============================================
		packageXML.find("target-language").each(function()
		{
			lang_content += '<li>' + forDisplay( $(this).attr('name') ) + '<br>';
		});
		lang_content += "</ul>";
		content = content + sectionCreator( "Target Language(s)", lang_content );	
		content = content + '</div><div class="col-lg-6 text-right" style="vertical-align:top">';
		
		//===============================================
		// loop through each tech stack component
		//===============================================		
		var stack_content = "<table class='sharedModelTableClass'>"
			+ "<tr><thead>"
			+ "<th style='text-align:center;width:25%'>Name</th>"
			+ "<th style='text-align:center;width:50%'>Description</th>"
			+ "<th style='text-align:center;width:25%'>App Layer</th>"
			+ "</thead></tr>" 
		
		packageXML.find("tech-stack-component").each(function()
		{
			stack_content = stack_content 
				+ "<tr class='sharedModelRowClass'>"
				+ "<td style='text-align:center' class='shareModelTDClass'>" + $(this).attr('name') + "</td>"
				+ "<td style='text-align:center' class='shareModelTDClass'>" + forDisplay( $(this).attr('description') ) + "</td>"
				+ "<td style='text-align:center' class='shareModelTDClass'>" + forDisplay( $(this).attr('app-layer') ) + "</td>"
			  	+ "</tr>";
	
		});
		
		stack_content += "</table>";
		
		content 	= content + sectionCreator( "Tech Stack Components", stack_content );
		content 	= content + "</div></div></div>";
		
		//===============================================
		// update the information div with the information
		//===============================================
//		document.getElementById("packageInformationAreaId").innerHTML = content;
		
		//===============================================
		// hide the loading div
		//===============================================
		$('#packageDetailsLoadingDivId').hide();
	});

}

//*****************************************************************
// Helper to determine if a model has been selected
//*****************************************************************
function checkModelLoaded() {
	console.log( 'globalModel  = ' + globalModel );
	//========================================================
	// if no model loaded..say so
	//========================================================
	if ( globalModel == undefined ) {
		//========================================================
		// show warning that a model needs to be loaded
		//========================================================
		showStatusDialog('Warning', 'Please load a new model file, reverse engineer one, or use one from the repository.')

		//========================================================
		// close all slide divs and open the model slide div
		//========================================================
		closeOtherSlideDivs( modelSlideDiv );
		modelSlideDiv.slideReveal("show", false);
		
		return false;
	}

	return true;
}

//***************************************************************************
// If using Git, determine if all params have been provided
//***************************************************************************
function checkGitSetup() {

	//========================================================
	// if git not in use, assume it is setup
	//========================================================
	if ( $('#gitCheckboxId').is(':checked') == false )
		return true;
	
	//========================================================
	// show the giti form if any of its req'd field are empty
	//======================================================== 
	if ( checkForNoEmptyRequiredFields('gitFormId') == false ) {
		showGitParamsModal();
		return false;
	}
	else
		return true;
} 

//**********************************************************************************
// Certain models do not lend themselves well to certain tech stack packages,
// but this concerne requires more refinement.
//**********************************************************************************
function checkMicroServicesValid() {
	//========================================================
	// if the architecture type is microservice yet the model has 
	// no components or subcomponents defined
	// warn this will likely result in an unpredictable project 
	// generation session
	//========================================================
	if ( "microservices" == getPkgRowSelection( "architecture" ).toString().trim() ) {
		
		if ( containers == 0 ) {
		
			//========================================================
			// output a warning if the model does not contain a container
			//========================================================
			var microServiceGenErrMsg = 'The framework package selected targets a microservices architecture, yet there were no subsystems or components defined within the loaded model. '
						+ 'Continuing forward with app generation may result in unpredictable output.';
						
        	BootstrapDialog.confirm({  
        		title: 'WARNING',
            	message: microServiceGenErrMsg,
	            type: BootstrapDialog.TYPE_WARNING, // <-- Default value is BootstrapDialog.TYPE_PRIMARY
	            closable: true, // <-- Default value is false
	            btnCancelLabel: 'Cancel', // <-- Default value is 'Cancel',
	            btnOKLabel: 'Continue', // <-- Default value is 'OK',
	            btnOKClass: 'btn-warning', // <-- If you didn't specify it, dialog type will be used,
	            callback: function(result) 
	            {
	    			//========================================================
	                // result will be true if button was click, while it will be false if 
	                // users close the dialog directly.
	                //========================================================
	                if(result) 
	                	handleTheAppCreate();
	            }
        	});
        	return false;						
		}
	}

	return true;
}

//****************************************************************
// determine if conditions are acceptable to generate code
//****************************************************************
function checkIfCanAppGen() {
	if ( checkTechStackSelected() &&
			checkModelLoaded() &&
			//checkForNoEmptyRequiredFields('appGenOptionsDivId') &&
			checkGitSetup() && 	
			checkToUsePayPal() )
		return true;		
	
	return false;

}

//****************************************************************
// if ok to generate, then generate
//****************************************************************
function appCreate() {
	if ( checkIfCanAppGen() === true )
		handleTheAppCreate();
}

//****************************************************************
// handles the core of generating an application
//****************************************************************
function handleTheAppCreate() {	
	
//	$('#appGenRunningDivId').show();

	var key, value;	
	var startTime 	= new Date();
	var options 	= getAppOptions();
	
	//==============================================================
	// apply the overwrite flag: 12/16/2021 - ignored
	//==============================================================
	var overWriteChecked = "false";
	if ( $('#appGenOverwriteCheckboxId').is(':checked') )
		overWriteChecked = "true";				
	 
	var bundleAppChecked	= "true";		// default
	var gitChecked 			= "false";		// default
	var scopeType 			= "PRIVATE";	// default
	
	//==============================================================
	// notify git is in use
	//==============================================================
	if ( $('#gitCheckboxId').is(':checked') )
		gitChecked = "true";
	
	//==============================================================
	// assign a random clientId for uniqueness when communicating
	// with the server
	//==============================================================
	var clientId = 			Math.random().toString(36).substring(2, 15) 
								+ Math.random().toString(36).substring(2, 15);
	

	//==============================================================
	// assign the scope to PUBLIC if checked
	//==============================================================	
	if ( $('#scopeTypeCheckboxId').is(':checked') ) {
		//bundleAppChecked = "false";
		scopeType = "PUBLIC";				
	}
	
	//==============================================================
	// assign the project id
	//==============================================================
	var projectId = '';
	
	if ( gProjectData != null && gProjectData != undefined )
		projectId = gProjectData.id;
		
	var microServices = false;
	
	if ( getPkgRowSelection( "architecture" ).toString().trim() == "microservices"  )
		microServices = true;
	
	//==============================================================
	// persist app by default: 12/16/19 - app persistence is ignored
	//==============================================================
	var persistAppGen = "true";
	//if ( $('#persistAppGenerationId').is(':checked') == false )
		//persistAppGen = "false";				

	//==============================================================
	// build url
	//==============================================================
	var url = 'generateApp.action?packageId=' + getPkgRowSelection( "packageId" )
			    + '&clientId=' + clientId
				+ '&frameworkDescriptorName=' + getPkgRowSelection( "descriptor" ) 
				+ '&modelFileName=' + globalModelName
				+ '&projectId=' + projectId
				+ '&overWrite=' + overWriteChecked
//				+ '&workingDir=' + $('#appGenDirectoryInputId').val()
				+ '&bundleApp=' + bundleAppChecked
				+ '&gitProject=' + gitChecked
				+ '&scopeType=' + scopeType
				+ '&microServices=' + microServices
				+ '&persistAppGen=' + persistAppGen
				+ '&nameToPersistApp=' + $('#nameToPersistAppGenerationId').val()
				+ '&description=' + $('#persistAppGenerationDescriptionId').val();

	//==============================================================
	// encode the url 
	//==============================================================	
	url = encodeURI(url);

	//==============================================================
	// encode the options 
	//==============================================================	

	url = url + "&options=" + encodeURIComponent(options);
	
	//==============================================================	
	// clear the app gen results of any old results 
	//==============================================================			
	document.getElementById("appGenResultsDivId").innerHTML = '';

	//==============================================================	
	// clear the app gen results of any old results 
	//==============================================================			
	$('#gitCloneDivId').hide();
	$('#gitCloneInputId').val( '' );
	
	//==============================================================	
	// show the app progress div
	//==============================================================	
	genAppProgressShow(clientId);
	
	$.ajax(
	{
    	url: 			url,
  	    data: 			JSON.stringify(getGitParams()),
  	    dataType: 		'json',
  	    contentType: 	'application/json',
  	    type: 			'POST',  		
	}
	).done(function( data ) 
	{
				
		var content = '';

		genAppStats = data;
		
		//==============================================================					
		//	check the generate app status for success
		//==============================================================	
		if ( typeof( genAppStats.done ) == "undefined") {
			//==============================================================	
			// later record end time
			//==============================================================				
			var endTime = new Date();
			
			//==============================================================
			// time difference in ms
			//==============================================================
			var duration = secondsToTime( (endTime - startTime)/1000 );
			
			//==============================================================
			// build out the content with the app generation stats
			//==============================================================
			content = content + "<h4>Generation Stats</h4>";
			content = content + "<table class='genResultsTable'>";
			content = content + "<tr><td style='padding:6px;text-align:left;font-weight:700'>Elasped time</td><td style='text-align:left'>" + duration.minutes +  "m:" + duration.seconds + 's</td></tr>';
			content = content + "<tr><td style='padding:6px;text-align:left;font-weight:700'>Files Processed</td><td style='text-align:left'>  " + addCommas( genAppStats.totalProcessed ) + '</td></tr>';
			content = content + "<tr><td style='padding:6px;text-align:left;font-weight:700'>Lines Generated</td><td style='text-align:left'>" + addCommas( genAppStats.totalLines ) + '</td></tr>';
			content = content + "</table>";
			 
/*			content = content + "<table style='border:2px solid black;padding:3px'><tr><td>"
			+ 	"<table class='genResultsTable'><tr><thead><th>File Type</th>"
			+ 		"<th>&nbsp</th><th>Total Count</th></thead></tr>" 
		
			for (var key in genAppStats.extMapping) 
			{
				content = content + "<tr><td>" + key
							+ "</td><td style='width:30px;'>&nbsp<td style='text-align:right'>"
							+ genAppStats.extMapping[key] 
							+ "</td></tr>";
			}		

			content = content + "<tr style='border-top:4px solid blue'><td style='text-align:right' colspan='2'><b>Total Files Processed</b></td>"
					+ "<td style='text-align:right'><b>"
					+ addCommas( genAppStats.totalProcessed )
					+ "</b></td></tr>";

			content = content + "<tr><td style='text-align:right' colspan='2'><b>Total Lines Created</b></td>"
					+ "<td style='text-align:right'><b>"
					+ addCommas( genAppStats.totalLines );
					+ "</b></td></tr>";
					
			content = content + '</table>';
*/	
			//==============================================================
			// reload the component library if persisted - commented 12/16/19
			//==============================================================
			// loadArchivedAppLibrary();
			
			//==============================================================
			// make visible the launch app button if the build checkbox is checked
			//==============================================================
			if ( $('#gitCheckboxId').is(':checked') )
				$('#launchAppBttnDivId').toggle();
		}
		//==============================================================
		// treat it like an action status		
		//==============================================================
		else 
			content = handleStatusOutputCleanly( data );
		
		//==============================================================
		// apply content to the app generation results div
		//==============================================================
		document.getElementById("appGenResultsDivId").innerHTML = content;
		
		//==============================================================
		// apply the git clone directive
		//==============================================================
		if ( $('#gitCheckboxId').is(':checked') == true ) {
			console.log( 'git clone here' );
			
			var username	= $('#gitUserNameId').val();
			var repository	= $('#gitRepositoryId').val();
			var host		= $('#gitHostId').val();
			var gitCloneText = 'git clone https://' + username + '@' + host + '/' + username + '/' + repository + '.git';
			
			$('#gitCloneInputId').val( gitCloneText );
			$('#gitCloneDivId').show();
		}
		
//	$('#appGenRunningDivId').hide();
	});
}

//========================================================
// function : update the app generation options div
//				using the provided parent package name
//========================================================				
function updateAppGenOptions( packageId ) {

	//==============================================================	
	// build the url to call
	//============================================================== 
	var url = 'retrieveOptions.action?frameworkPackageId=' + packageId;

	$.ajax(
	{
  		url: url,
  		dataType: 'text',
	}
	).done(function( data ) 
	{
		//==============================================================
		// parse the options into an XML object
		//==============================================================
		var xmldoc = $.parseXML(data);

		//==============================================================
		// cache the options globally
		//==============================================================
		gOptions = $( xmldoc );
		
		//==============================================================
		// display the app gen options
		//==============================================================
		return showAppGenOptions( gOptions );				
	});
	
}

//========================================================
// function : show the app gen options contained in the
//				options XML node
//========================================================
function showAppGenOptions( options ) {
	var content 		= '';
	var tabDeclContent 	= '<ul class="etabs">';
	var pkg_options 	= options.find("pkg_options");
	var maxPerRow;
	var name;
	var tabName;
	
	console.log( options );

	//========================================================
	// iterate through all the options and create an options style div
	//========================================================	
	pkg_options.find("options").each(function(){
		maxPerRow = $(this).attr('maxPerRow');
		if ( maxPerRow == undefined )
			maxPerRow = 1;
		
		name = $(this).attr('name');
		tabName = 'tabs-' + name.replace(' ', '');
		
		tabDeclContent = tabDeclContent + '<li class="tab"><a href="#' + tabName + '">' + name + '</a></li>';
					
		content = content + '<div id="' + tabName + '" style="border:2px solid black;padding-top:15px;padding-left:10px;padding-bottom:10px">';
		content = content + optionsCreator( $(this).attr('name'), $(this), maxPerRow ) ;
		content = content + "</div>";
	});
	
	tabDeclContent = tabDeclContent + "</ul>";

	//========================================================
	// deliver the content into the appGenOptionsDivId
	//========================================================	
	content = tabDeclContent + content;
	$("#appGenOptionsDivId").empty();
	$("#appGenOptionsDivId").html( content );
	$('#appGenOptionsDivId').easytabs();
	
	return content;
}



//========================================================
// file upload handler : framework package file
//========================================================

$(function ()  {
    'use strict';
    	    
    $('#framework_package_upload').fileupload(
    {
        url: 'uploadPackage.action',
        dataType: 'json',
        replaceFileInput:true,
        add: function (e, data) 
        {
	        //========================================================        
    		// show the framework package loading div
    		//========================================================    		
			$('#frameworkPackageLoadingDivId').show();
				
			//========================================================				
			// forget this and the rl will not be fired
			//========================================================			
			data.submit();		
		},
        done: function (e, data) 
        {
        	//========================================================        
			// hide the loading div
			//========================================================			
			$('#frameworkPackageLoadingDivId').hide();
			
			var status = data.result;
			
	        if ( status.success == false ) {
	        	var msg = handleStatusOutputCleanly( status );
	        	showStatusDialog( "Tech Stack Package Upload Error", msg );
			}
			else {
			    //========================================================			    
			    // try to reload the packages
			    //========================================================			    
			    loadPackages();			         			

				showStatusDialog( "Info", "Tech Stack Package upload and validation successful.");
			}	
			
			//========================================================
			// hide the load file dialog
			//========================================================				
			$('#upload_Framework_Package_Modal').modal("hide");
        }
	}).on('fileuploadsend', function (e, data) 
    {
    	//========================================================
		// show the loading div
		//========================================================		
		$('#frameworkPackageLoadingDivId').show();
	});    		
});


// ========================================================
// file upload handler : all model types except Git are handled here
// ========================================================

$(function ()  {
    'use strict';
       		
	//========================================================       		
    // call the backend server to load and process the 
    // provided local model file
    //========================================================
    
    var url = 'uploadModel.action';
     
    $('#model_upload_id').fileupload(
    {
        url: url,
        dataType: 'json',
        replaceFileInput:true,
        add: function (e, data) 
        {      
        	//========================================================
        	// check for non-empty required fields
        	//========================================================        
			if ( checkForNoEmptyRequiredFields('localModelFormId') == false )
				return;		
			
        	//========================================================
        	// determine the url to call based on the model type
        	// 12/16/19: the backend now handles deducing things
        	// based on the model file extension
        	//========================================================        
		    if ( gModelType === 'sql' )
		    	data.url = 'uploadSqlScript.action';
		    else if ( gModelType === 'yaml' )
		    	data.url = 'uploadYaml.action';
		    else if ( gModelType === 'json' )
		    	data.url = 'uploadJson.action';
		    else if ( gModelType === 'xmi' || gModelType === 'uml' )
		    	data.url = 'uploadXMI.action';
		    else if ( gModelType === 'ecore'  )
		    	data.url = 'uploadEMF.action';
		    else if ( gModelType === 'jar' )
		    	data.url = 'uploadPojo.action';

			//========================================================
			// share the model flag 
			//========================================================			
        	var shareModelFlag = 'false';
			if ( $('#shareModelCheckboxId').is(':checked') )
				shareModelFlag = 'true';				

			//========================================================
			// append this model to one already loaded : defaults to false
			//========================================================			        
        	var appendModelFlag = 'false';
			if ( $('#appendModelCheckboxId').is(':checked') )
				appendModelFlag = 'true';				
        	
        	//========================================================
			// apply data to the FormData
			//========================================================			        	
            var formData = new FormData();
			formData.append('shareModel', shareModelFlag);
			formData.append('appendModel', appendModelFlag);
			formData.append('name', $('#shareModelNameId').val());
			formData.append('description', $('#shareModelDescriptionId').val());
/*			formData.append('contributor', $('#shareModelContributorId').val());
			formData.append('company', $('#shareModelCompanyId').val());
			formData.append('email', $('#shareModelEmailId').val());
*/			
			formData.append('modelType', gModelType );
			formData.append('pojoParams.rootPackageName', $('#rootPackageNameId').val());
			formData.append('pojoParams.primaryKeyField', $('#primaryKeyFieldId').val());
			
			data.formData = formData;
			data.submit().done(function (result, textStatus, jqXHR) 
			{
				//========================================================
			    // hide the loading div
			    //========================================================
				$('#modelLoadingDivId').hide();
			
		        if ( result.done == false ) {
		        	var msg = handleStatusOutputCleanly( result );
		        	showStatusDialog( "Upload Model Error", msg );
			    
					return;
				}
				else {
					//========================================================        
			        // hide the load model file dialog
			        //========================================================
			        $('#load_Local_Model_Modal').modal("hide");
						
					//===============================================
					// load the model into the model content div
					//===============================================  
			        loadModelContent( result );			

					//===============================================
					// update the stack and model div
					//===============================================		
					updateTechStackModelStatusDiv();
					
				}
			})
            .fail(function (jqXHR, textStatus, errorThrown) 
        	{
        		//========================================================
        		// hide the loading div
        		//========================================================
				$('#modelLoadingDivId').hide();
        		showStatusDialog( "Upload Model Error", errorThrown + "<br>Check file content and size" );
        	});
        	
        }
    }).on('fileuploadsend', function (e, data) 
    {
    	//========================================================
		// show the loading div
		//========================================================
		$('#modelLoadingDivId').show();
	}).on('fileuploadadd', function (e, data) 
	{
		$.each(data.files, function (index, file) 
		{
			//========================================================
			// assign the file globally
			//========================================================
			globalModelName = file.name		         
		});
	});
});
		

//========================================
//function : format for display
//=====================================

function forDisplay( data, defValue ) {
	if ( typeof( data ) == "undefined" || data.length == 0 || data == '[]' ) {
		if ( typeof( defValue ) == "undefined" )
			data = '';
		else
			data = defValue;
	}
	
	return( data );
}
		
//==============================================
// updates the status div with the selected
// tech stack and model names and images
//================================================
function updateTechStackModelStatusDiv() {
	var content = '<span style="vertical-align:middle;text-align:center;padding-top:6px;padding-left:6px;font-size:0.8em;color:#000000">Tech Stack: ';
	var pkgName = getPkgRowSelection( "packageName" );
	var icon 	= null;
	
	if ( pkgName.length == 0 )
		content = content + "<b>none selected</b>";
	else {
		icon 	= gSelectedFrameworkPackage.iconUrl;
		content = content + '<img style="width:16px;height:16px" src="' + icon + '"/> ' + pkgName;
	}
				
	//========================================================		
	// apply the model
	//========================================================
	content = content + '&nbsp&nbsp&nbsp&nbspModel: ';
	var modelFile = forDisplay( globalModelName );
	if ( globalModelName == LOADING_MODEL_PLEASE_WAIT_MSG ) {
		content = content + '<img style="width:16px;height:16px" src="./img/load_new.gif"/>';
	}
	else if ( globalModelName == MISSING_MODEL_FILE_MSG ) {
		content = content + '<img style="width:24px;height:24px" src="./img/question-mark.png"/>';
	}
	else if ( modelFile != null && modelFile.length == 0 )
		content = content + "<b>none selected</b>";
	else {
		//========================================================
		// parse to remove any file path information
		//========================================================
		modelFile = modelFile.substring(modelFile.lastIndexOf('/') + 1);
		
		content = content + '<img style="width:16px;height:16px" src="';
		
		//========================================================
		// based on the current model, determine which image to display
		//========================================================
		if ( modelFile.endsWith( 'xmi' ) || modelFile.endsWith( 'uml' ) )
			content = content + './img/uml.png';
		else if ( modelFile.endsWith( 'yaml' ) || modelFile.endsWith( 'yml' ) )
			content = content + './img/yaml.png';
		else if ( modelFile.endsWith( 'core' ) )
			content = content + './img/eclipse.jpg';
		else if ( modelFile.endsWith( 'jar' ) )
			content = content + './img/java-jar.png';
		else if ( modelFile.endsWith( 'sql' ) )
			content = content + './img/sql_script.png';
		else if ( modelFile.endsWith( 'json' ) )
			content = content + './img/json-logo.png';
		else if ( modelFile.endsWith( 'git' ) )
			content = content + './img/git.png';
		
		content = content + '"/> ';
	}
	
	content = content + modelFile; 
	content = content + "</span>";
	document.getElementById('package_fd_model_div_id').innerHTML = content;	
}


//===========================================================================
// function : helper to create an input section, 
//            using the values as and applying
// 			  so many per row as indicated
//===========================================================================
function optionsCreator( header, values, max_inputs_per_row ) {

	// input is 1's based, but the calc below is 0's based so adjust
	//max_inputs_per_row--;
	
	//========================================================
	// values are an xml represenation of option key/value pairs
	//========================================================
	var name, value, required, modifiable, type, fullName, placeholder;
	var flag 			= 0;
	var errorOptions 	= "";
	var options 		= '<div class="container col-lg-12">';
	var numOptions 		= values.find("option").length;

	//=====================================================================
	// iterate through the option node and build a table of form inputs
	//=====================================================================
	
	values.find("option").each(function(index,element)
	{
		if ( flag == 0 )	// start a new row
			options = options + '<div class="row">';
		
		flag = flag + 1;	// increment the flag counter			
					
		//====================================					
		// apply the relevant input fields
		//====================================					
		name 		= $(this).attr('name');
		value 		= $(this).attr('value');
		required 	= $(this).attr('required');
		modifiable 	= $(this).attr('modifiable');
		type 		= $(this).attr('type');
		placeholder = $(this).attr('placeholder');
		
		if ( name == undefined )
			errorOptions = errorOptions + 'name option is missing for ' + header + 'options<br>';
		if ( placeholder != undefined )
			placeholder = 'placeholder=' + placeholder;
		else
			placeholder = '';
			
		fullName = header + '.'  + $(this).attr('name');

		//=======================================================
		// if there is a loaded project, use it's value instead
		// if there is no value found in the project, use the
		// default as assigned in the options XML
		//=======================================================
		if ( gProjectData != null && gProjectData != undefined )
		    if ( gProjectData.options[fullName] != null && gProjectData.options[fullName] != undefined )
			  value = gProjectData.options[fullName];
		
		//====================================
		// figure out type
		//====================================
		if ( type == undefined )
			type = "input";
			 
		//====================================
		// figure out if disabled
		//====================================
		if ( modifiable == undefined || modifiable == true )
			modifiable = '';
		else
			modifiable = "disabled";
		
		//====================================
		// figure out if required
		//====================================
		if ( required == undefined || required == false )
			required = '';
		else {
			required = "required";
//			name = '<b>' + name + ' *</b>';
		}

		options = options + '<div class="col-3">' + name + '</div>'
				+ '<div class="col-2">';
		
		//====================================
		// figure out input type and turn into 
		// a form control
		//====================================
		
		//====================================
		// input type becomes a input form control
		//====================================		
		if ( type == "input" ) { 
			options = options + '<input class="optionsInputClass form-control" type="text" id="' + fullName + '" name="' + fullName 
			+ '" value="' + value + '"' + ' ' + required + ' ' + modifiable + ' ' + placeholder + '/>';
		}
		//====================================
		// password type becomes a password input form control
		//====================================		
		else if ( type == "password" ) { 
			options = options + '<input class="optionsInputClass form-control" type="password" name="' + fullName 
			+ '" value="' + value + '"' + ' ' + required + ' ' + modifiable + ' ' + placeholder + '/>';
		}
		//====================================
		// textarea type becomes a textarea form control
		//====================================				
		else if ( type == "textarea" ) { 
			options = options + '<textarea class="optionsInputClass form-control" rows="4 cols="32" name="' + fullName 
			+ '" value="' + value + '"' + ' ' + required + ' ' + modifiable + '/>';
		}
		//====================================
		// boolean type becomes a boolean form control
		//====================================						
		else if ( type == "boolean" ) {
			var checked = "";
			if ( value == "true" )
				checked = "checked";
						
			options = options 
					+ '<div class="checkbox checkbox-slider--b">' 	  			  	
						+ '<label><input class="optionsCheckboxClass form-control" type="checkbox" value="' + fullName + '" name="' 
							+ fullName + '" ' + checked + ' ' + modifiable + '/><span></span>'
					+ '</label></div>';
		}
		//====================================
		// textarea type becomes a textarea form control
		//====================================						
		else if ( type == "spinner" ) {
			var min = $(this).attr('min');
			if ( min == undefined ) {
				errorOptions = errorOptions + fullName + " - min attribute not provided, defaulting to 0<br>";
				min = 0;
			}
				
			var max = $(this).attr('max');
			if ( max == undefined ) {
				errorOptions = errorOptions + fullName + " - max attribute not provided, defaulting to 100<br>";
				max = 100;
			}
				
			var step = $(this).attr('step');
			if ( step == undefined ) {
				errorOptions = errorOptions + fullName + " - spin attribute not provided, defaulting to 1<br>";
				step = 1;
			}			

			options = options + '<input class="form-control"'
				+ ' min="' + min + '" max="' + max + '" step="' + step 
				+ '" type="number" name="' + fullName 
				+ '" value="' + value + '" ' + required + ' ' + modifiable + '/>'			
		}
		//====================================
		// select type becomes a select form control
		//====================================						
		else if ( type == "select" ) {
			var selections = $(this).attr('selections');
			
			if ( selections == undefined )
				errorOptions = errorOptions + "selections is missing on " + fullName;
			else {
				options = options + '<select class="form-control optionsSelectClass" name="' + fullName 
								+ '" ' + required + ' ' + modifiable + '>';
						
				var selectOptionsArray = selections.split(',');
				var selectValue;
				
				for(var i = 0; i < selectOptionsArray.length; i++) {
					selectValue = selectOptionsArray[i];
					options = options + '<option value="' + selectValue + '" ';
					
					if ( value == selectValue )
						options = options + 'selected=selected';
						
					options = options + ">" + selectValue + "</option>";
				}
				options = options + '</select>';
			}
		}
		//====================================
		// unrecognized type discovered
		//====================================				
		else
			errorOptions = errorOptions + "type " + type + " is unrecognized<br>";	
		
		options = options + "</div>";
						
		//====================================
		// reset and terminate the row 			
		//====================================			
		if ( flag == max_inputs_per_row )	{
			flag = 0;
			options = options + '</div>';
		}
		else if ( index === (numOptions - 1) )
			options = options + '</div>';
		//else 								// create a new column
			//options = options + '<td style="width:4px"></td>';
	});
						
	options = options + '</div>';

	if ( errorOptions.length > 0 ) {
		showStatusDialog( 'Warning', 'The following fields were formatted incorrectly:<br>' + errorOptions)
	}
	
    return options;
}


//******************************************************
// helper to determine the model image url based on
// the model type
//******************************************************
function determineModelImageUrl( modelType ) {
	var image;
	
	if ( modelType == 'XMI' || modelType == 'UML' )
		image = './img/uml.png';
	else if ( modelType == 'ECORE' )
		image = './img/eclipse.jpg';
	else if ( modelType == 'POJO' )
		image = './img/java-jar.png';
	else if ( modelType == 'SQL_SCRIPT' )
		image = './img/sql_script.png';
	else if ( modelType == 'YAML' )
		image = './img/yaml.png';			
	else if ( modelType == 'JSON' )
		image = './img/json-logo.png';			
	else if ( modelType == 'GIT'  )
		image = './img/git.png';		
	else
		image = './img/unknown.png';

	return image;
}


//===========================================================================
//function : helper to create a standard section, with a header and content
//===========================================================================
function sectionCreator( header, content ) {
	var section = '<div class="sectionClass"><div class="sectionHeaderClass"><span class="sectionTitleClass">' 
				+ header + '</span></div><div class="sectionBodyClass">' + content + '</div></div>';
	return section;

}

//===========================================================================
//function : shows a status dialog as modal
//===========================================================================
function showStatusDialog( title, msg ) {
	Swal.fire({
		  title: title,
		  html: msg,
		  type: 'info',
		  footer: 'realMethods ' + appVersion,
		  confirmButtonText: 'OK'
		})
}

//===========================================================================
//function : updates the href of the selected model.  
//===========================================================================
function updateDownloadModelHref() {
	var table 		= $('#sharedModelTableId');
	var row 		= table.find('tr.modelTableRowHighlight').eq( 0 );
	var filePath	= gSharedModels[row.index()].filePath;
	$("#downloadUMLHrefId").attr( 'href', FILE_STORE_URL + filePath );
}

//===========================================================================
//function : updates the href of the selected package.  
//===========================================================================
function updateDownloadPackageHref() {
	var filePath = gSelectedPackageRow.find("td.filePath").text();
	$("#downloadPackageRefId").attr( 'href', filePath );
}

//===========================================================================
//function : show the load model modal, hiding/showing form control
// based on the model type
//===========================================================================
function showLocalModelModal( modelType ) {

    //=========================================================
    // store for global reference as the current model type
    //=========================================================
    gModelType = modelType;
    
    //=========================================================
    // only show for jar file type
    //=========================================================
    $('#rootPackageNameDivId').hide();
    
    //=========================================================
    // reset the form controls
    //=========================================================
    $('#localModelFormId')[0].reset();
    
    if ( modelType == 'sql' ) {
    	$('#shareModelInfoDivId').show();
    	$('#shareModelNameId').attr("placeholder", "DB name used in SQL file");
    }
    else if ( modelType == 'jar' || modelType == 'git' ) {
    	$('#rootPackageNameDivId').show();    	
    }
    else
        $('#shareModelNameId').attr("placeholder", "Enter name");

	//=========================================================
	// hide/show for git or non-git model type in use
	//=========================================================
	if ( modelType == 'git' ) {
		$('#fileUploadDivId').hide();
		$('#gitUrlDivId').show();
	} else {
		$('#fileUploadDivId').show();
		$('#gitUrlDivId').hide();
	}

   //=========================================================
   // update the title
   //========================================================= 
	var title = "Load model from ";

	if ( modelType == 'sql' )
		title = title + 'SQL script file';
	else if ( modelType == 'yaml' )
		title = title + 'YAML file';
	else if ( modelType == 'json' )
		title = title + 'JSON file';
	else if ( modelType == 'xmi' )
		title = title + 'XMI file';
	else if ( modelType == 'uml' )
		title = title + 'UML file';
	else if ( modelType == 'ecore'  )
		title = title + 'Eclipse Ecore file';
	else if ( modelType == 'jar' )
		title = title + 'JAR/WAR/EAR file';
	else if ( modelType == 'git' )
		title = title + 'public Git repo';

	$('#loadLocalModelLabelId').html( title );
       
   //=========================================================
   // show the local model modal
   //========================================================= 
	$('#load_Local_Model_Modal').modal('show');
}

//**************************************************************************************
// show the shared model modal 
//**************************************************************************************
function showSharedModelModal() {
    $('#load_Shared_Model_Modal').modal('show');
}


//**************************************************************************************
// provide the callback with the tech stack package as JSON as well as a callback handler
//**************************************************************************************
function findTechStackPackage( packageId, callback ) {
	
	var url = "findPackage.action?frameworkPackageId=" + packageId;
			
	$.ajax(
	{
  		url: url,
  		dataType: 'json',
  		async: false
	}
	).done(function( data ) 
	{
		//=========================================================
		// assign the tech stack package data global reference
		//=========================================================
		gSelectedFrameworkPackage = data;
		
		//=========================================================
		// call the callback with the tech stack package data
		//=========================================================
		callback( data );
	});	
}

//*************************************************************
// outputs the details of back end call with order. Not all
// back end calls provide this level of detail
//*************************************************************
function handleStatusOutputCleanly( data )
{
	var msg = '';
	
	if ( data.infoMessages.length > 0 )
		msg = msg + sectionCreator( 'Info', data.infoMessages );
		
	if ( data.warningMessages.length > 0 )
		msg = msg + sectionCreator( 'Warning', data.warningMessages )
		
	if ( data.errMessages.length > 0 )
		msg = msg + sectionCreator( 'Error', data.errMessages )
	
	return( msg );
}

//******************************************************
// displays more detail about a selected tech stack
//******************************************************
function viewPackage() {
	if ( gSelectedPackageRow != undefined )
		window.open(gSelectedFrameworkPackage.infoPageUrl, "realMethods");
	else
		showStatusDialog('Warning', 'No tech stack selected');
}

//******************************************************
// allows for the browsing of the generated code in a directory format
//******************************************************
function viewCode() {
	displayFileTree( genAppStats.resultPath, 'Browse Generated Code' );
}

//******************************************************
// displays a directory hierarchy and files in a modal
//******************************************************
function displayFileTree( rootDir, title ) {	
	$('#file_Tree_Modal').modal('show');
	
	document.getElementById("fileContentDivId").innerHTML = "";
	document.getElementById("file_Tree_Label_Id").innerHTML = title;
	
	//=========================================================
	// load the file.tree with the root directory of the 
	// selected framework package
	//========================================================= 
	$('#fileTreeDivId').fileTree({ root: rootDir, script: 'connectors/jqueryFileTree.jsp', folderEvent: 'dblclick', expandSpeed: 1, collapseSpeed: 1 }, function(file) 
	{ 
		var url = 'connectors/jqueryFileTree.jsp?file=' + file;
		
		$.ajax(
		{
	  		url: url,
	  		dataType: 'text',
		}
		).done(function( data ) 
		{
			$('#fileContentDivId').show();
			document.getElementById("fileContentDivId").innerHTML = data;
		});
	    
	}); 
	
}

//***************************************************
// applies commas to a numeric value
//***************************************************
function addCommas(intNum) {
  return (intNum + '').replace(/(\d)(?=(\d{3})+$)/g, '$1,');
}
//***************************************************
// helper to extract a query param from a url
//***************************************************
function getParamHelper( name, url ) {
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( url );
    var retVal;
    
    if ( results == null || results == 'null')
    	retVal = null;
    else
    	retVal = results[1];
    	    
    return retVal;
    
}

//***************************************************
// delegates to getParamHelper(name, url) using
// locationl.href as the url
//***************************************************
function getParam( name ) {
  return( getParamHelper( name, location.href ) );
}

//***************************************************
// turns seconds into minutes and hours
//***************************************************
function secondsToTime(secs) {
    secs = Math.round(secs);
    var hours = Math.floor(secs / (60 * 60));

    var divisor_for_minutes = secs % (60 * 60);
    var minutes = Math.floor(divisor_for_minutes / 60);

    var divisor_for_seconds = divisor_for_minutes % 60;
    var seconds = Math.ceil(divisor_for_seconds);

    var obj = {
        "hours": hours,
        "minutes": minutes,
        "seconds": seconds
    };
    return obj;
}

//***************************************************
// show the app create slider and hide all others
//***************************************************
function showAppCreateSlide() {
	closeOtherSlideDivs( createAppSlideDiv );
	createAppSlideDiv.slideReveal("show", false);
}

//***************************************************
// show factory schema modal - unused
//***************************************************
function factorySchema() {
	$('#Factory_Schema_Modal').modal('show');
}

//***************************************************
// show factory scheduler modal - unused
//***************************************************
function factoryScheduler() {
	$('#Factory_Scheduler_Modal').modal('show');
}

//***************************************************
// show factory options modal - unused
//***************************************************
function factoryOptions() { 
	$('#Factory_Options_Modal').modal('show');
}

//***************************************************
// start the factory
//***************************************************
function fireUpFactory() {
}

//***************************************************
// factor ops structure
//***************************************************
var factorOps = {
	techStackPkgName :	'',
	schemaDir : 		'',
	schemaFile : 		'',
	frequencyChkVal : 	'1',
	frequencyChkUnit : 'hour',
	buidApp : 			true,
	saveStats : 		false,
	saveWorkingDir : 	false 
}

//***************************************************
// single migrate kickoff - unused
//***************************************************
function singleMigrate() {
	$('#Factory_Modal').modal('hide');
	navBarDiv.slideReveal( 'show', true );
	handleReverseEngClick();
}

//***************************************************
// view loaded model as JSON
//***************************************************
function viewModelAsJson() {
	showStatusDialog( 'Model as JSON', globalModelData.modelAsJson )
}

//***************************************************
// display the readme into the provided element
//***************************************************
function displayReadme( element ) {
	var $div = $('<div>');
	$div.load( encodeURI(gSelectedFrameworkPackage.infoPageUrl), function(){
	    element.innerHTML = $(this).html();
	});
}

//***************************************************
// view an example JSON file - unused
//***************************************************
function viewJsonExample() {
}


//************************************************
// determine of PayPal should be used - for future usage
//************************************************
function checkToUsePayPal() {
	return true;
}

//*****************************************************
// handler for showing application generation progress
//*****************************************************
function genAppProgressShow(clientId) {
	$('#resultsDivId').show();
	$('#stepperDivId').show();
//	$('#genOutputDivId').show();
	
	//===============================================================	
	// a pause is good here to give the back end a chance to get started
	//===============================================================
	sleep(1000).then(() => {
		continouslyUpdateGetAppProgress(clientId);
	})				  		
}

//===============================================================
// sleep variable/function 
//===============================================================
const sleep = (milliseconds) => {
	  return new Promise(resolve => setTimeout(resolve, milliseconds))
	}

//*****************************************************
// does the bulk of the work to check on the
// back-end progress of an application generation session
//*****************************************************
function continouslyUpdateGetAppProgress( clientId )
{
  	setTimeout(function () {
		$.ajax(
		{
	  		url: "stages.action?clientId=" + clientId,
	  		dataType: 'json',
		}
		).done(function( data ) 
		{
			if ( data != null ) 
			{
				var currentStage = data.status.currentStage;
				var stages = data.stages;
				var index = stages.indexOf( currentStage );
				$('.step-container').stepMaker({
					steps: stages,
					currentStep: index+1
				});
				
//				$('#genOutputDivId').html( data.status.infoMessages 
//						/*+ data.status.warningMessages 
//						+ data.status.errMessages */ );
				
				if ( index < stages.length-1 )
					continouslyUpdateGetAppProgress( clientId );
			}
		});
  	}, 100);
}

//*****************************************************
// hide the application progress step div
//*****************************************************
function genAppProgressHide() {
	$('#stepperDivId').hide();
}

//*****************************************************
// for now, unconditionally shows the log-on screen
// but eventually will check with the back end to 
// to see if a session exists with a user cached
//*****************************************************
function checkLogon(forceLogon)  {
	if ( forceLogon == true )
		$('#Logon_Modal').modal('show');
	else {
	    if ( getParam("checkoutSuccess") == 'true' )
		    $('#Register_User_Modal').modal('show');
	    else
		    $('#Logon_Modal').modal('show');
	}
}

//*****************************************************
// handles the request to log-on
//*****************************************************
function handleLogonRequest() {

	var url = 'logon.action?userId=' + $('#logonUserNameId').val() + "&password=" + $('#logonPasswordId').val();		
	
	document.getElementById("logonStatusDivId").innerHTML = "Checking credentials...";
	
	$.ajax(
	{
  		url: 		url,
  		dataType: 	'json'
	}
	).done(function( data ) 
	{
		if ( data.resultCode == "SUCCESS" )
		{
			document.getElementById("logonStatusDivId").innerHTML = "";
			$('#Logon_Modal').modal('hide');
			logonSuccessful();
		}
		else
			document.getElementById("logonStatusDivId").innerHTML = data.processingMessage;
	});
 
}

//*****************************************************
// helper to handle getting the application ready
// to be used upon a successful log-on
//*****************************************************
function logonSuccessful() {
	
	var url = 'userInfo.action';
	
	//====================================================
	// load the user data in the user options form
	//====================================================
	$.ajax(
	{
  		url: 		url,
  		dataType: 	'json',
	}	).done(function( data ) 
	{
		if ( data.resultCode == "SUCCESS" ) {
			var result = JSON.parse(data.result);

			$('#firstNameInputId').val(result.firstName);
			$('#lastNameInputId').val(result.lastName);
			$('#emailInputId').val(result.email);
			$('#companyInputId').val(result.company);
			$('#notificationCheckboxId').prop('checked', result.notifyFlag );		
			$('#planTypeSelectId').val(result.userType);			
			$('#userNameInputId').val(result.userId);
			$('#passwordInputId').val(result.password);					
			$('#tokenInputId').val(result.internalIdentifier);
		}
		else
			console.log( data.processingMessage );
	});
	
	//========================================================
	// tool tip kickoff
	//========================================================
	$('.theToolTip').tooltipster({
	    theme: 'tooltipster-shadow',
	    animation: 'fade',
			delay: 3000,
	});
	//========================================================
	// declare all event handlers
	//========================================================	
	declareEventHandlers();

	//=================================================
	// load the packages available on the server
	//=================================================			 
	loadPackages();
	
	//=================================================
	// load the archived application library
	//=================================================						
	// loadArchivedAppLibrary();

	//=================================================
	// load the published resources
	//=================================================						
	loadResources();
	
	//=================================================
	// finally, show the logo slide
	//=================================================		
	closeOtherSlideDivs(logoSlideDiv);
	logoSlideDiv.slideReveal("show", false);

}
		
//========================================================
// declare all event handlers
//========================================================	
function declareEventHandlers() {

	//========================================================
	// handle show event on load model modal to load models
	//========================================================
	$('#load_Shared_Model_Modal').on('shown.bs.modal', function () 
	{
		$('#sharedModelBodyDivId').empty();
		loadModels();
	}); 

	//========================================================
	// handle show event on load builds modal to load builds 
	// for the current project
	//========================================================
	$('#load_Builds_Modal').on('shown.bs.modal', function () 
	{
		$('#buildsDivId').empty();
		loadBuilds();
	}); 

	//========================================================
	// handle show event on save project to reset the form
	//========================================================
	$('#new_Project_Modal').on('shown.bs.modal', function () 
	{
		$('#projectFormId')[0].reset();
	}); 

	//========================================================
	// slow toggle the share model div area when the build
	// app checkbox is clicked
	//========================================================
	$('#gitCheckboxId').on('click', function() 
	{
		$('#launchAppBttnDivId').toggle('slow', function() {
		});
	}); 

	//========================================================
	// enable/disable according to the checkbox
	//========================================================
	$('#persistAppGenerationId').on('click', function() 
	{
		$('#nameToPersistAppGenerationId').prop('disabled', !this.checked);
		$('#persistAppGenerationDescriptionId').prop('disabled', !this.checked); 			
	}); 
	 
	//========================================================
	// initialize the jstree
	//========================================================
	//$.jstree.defaults.core.themes.variant = "large";
	
	$('#modelTreeId').jstree(
	{
 		"core" : 
 		{
    		"multiple" : false,
    		"animation" : 0,
    		"themes" : { "stripes" : false }
  			}
	});
	
	//===============================================
	// shared model view row click function
	//===============================================
	$('#sharedModelBodyDivId').on('click', 'tr', function() 
	{
		$(this).addClass('modelTableRowHighlight').siblings().removeClass('modelTableRowHighlight');
		updateDownloadModelHref();
	});

	//===============================================
	// tech stack package view row click function
	//===============================================
	$('#frameworkPackageViewId').on('click', 'tr', function() 
	{
		handlePackageSelection( $(this) );
	});		

	//===============================================
	// builds view row click function
	//===============================================
	$('#buildsDivId').on('click', 'tr', function() 
	{
		highLightRow( $(this) );
	});		

}

//***************************************************
//shows the subscription notification model
//***************************************************
function displaySubscriptionDialog() {
	$('#Subscription_Modal').modal('show');
}

//***************************************************
// shows the user registration model
//***************************************************
function displayRegisterUserDialog() {
	$('#Register_User_Modal').modal('show');
}


//***************************************************
//shows the forgot password model
//***************************************************
function displayForgotPasswordDialog() {
	$('#Forgot_Password_Modal').modal('show');		
}

//***************************************************
// handler for sending the user his/her password
//***************************************************
function sendPassword() {
	
	var url = 'sendPassword.action?userId=' + $('#forgotPasswordUserNameId').val();		
	
	$("#forgotPasswordStatusDivId").text('Sending password via email...');
	
	$.ajax(
	{
  		url: 		url,
  		dataType: 	'json',
  		async: 		true
	}
	).done(function( data ) 
	{
		if ( data.resultCode == "SUCCESS" )
			$("#forgotPasswordStatusDivId").text('Password sent successfully.  Check you email.');
		else
			$("#forgotPasswordStatusDivId").text(data.processingMessage);
	});
 
}

//***************************************************
//handler for registering a new user
//***************************************************
function registerUserData() {

	if ( $('#acceptEULACheckboxId').is(':checked') == false ) {
		showStatusDialog('Warning', 'You must accept the EULA in order to register');
		return;
	}
		
	if ( checkForNoEmptyRequiredFields('regUserOptionsFormId') == true ) {
		var statusDiv = $("#registerNewUserStatusDivId");
			
		statusDiv.text('Registering with realMethods....');
		
		var notificationChecked = $("#regNotificationCheckboxId").is(":checked") ? "true" : "false";
		
		var url = 'registerUser.action?firstName=' + $('#regFirstNameInputId').val()
			+ '&lastName=' + $('#regLastNameInputId').val()
			+ '&email=' + $('#regEmailInputId').val()
			+ '&company=' + $('#regCompanyInputId').val()
			+ '&userType=' + $('#regPlanTypeSelectId option:selected').val()
			+ '&notifyFlag=' + notificationChecked
			+ '&userId=' + $('#regUserNameInputId').val()
			+ '&password=' + $('#regPasswordInputId').val()
				
		$.ajax(
		{
			url: url,
			dataType: 'json',
		}
		).done(function( data ) 
		{			
			if ( data.resultCode == "SUCCESS" ) {
				var internalIdentifier = JSON.parse(data.result).internalIdentifier;
				statusDiv.text("Done registering and your API token is " + internalIdentifier + ".");
			}
			else		
				statusDiv.text(data.processingMessage);
		});
	}  
}

//**********************************************
// iterates a form and checks and warns for
// empty required text, password, and email
// input fields
//**********************************************
function checkForNoEmptyRequiredFields(formId) {
	var errMsg = '';
	var setFocusTo = null;

	var inputs = document.getElementById(formId).querySelectorAll("input[type=text],input[type=email],input[type=password],textarea");
	
	[].forEach.call(inputs, function(input) {

		if (input.value === "" 
				&& input.getAttribute("required") != undefined
				&& input.parentElement.style.display !== "none" ) {
			if ( setFocusTo == null )
				setFocusTo = input;			
			
			console.log( input.getAttribute("name") + " " + input.parentElement.style.display );
			errMsg = errMsg + input.getAttribute("name") + "<br>"; 
		}
	});
				
	if ( errMsg != '' ) {
		showStatusDialog('Warning', 'The following fields cannot be left blank:<br><br>' + errMsg);
		setFocusTo.focus();
		return false;
	}      
	
	return true;
}

//**********************************************
// show modal with Git parameters
//**********************************************
function showGitParamsModal() {
	$('#Git_Modal').modal('show');
}

//**********************************************
// show the clone Git repo modal
//**********************************************
function cloneGitRepo() {

	if ( checkForNoEmptyRequiredFields('localModelFormId') == false )
		return;

	//======================================
	// apply the model file name globally
	//======================================	
	globalModelName = $('#gitUrlId').val();
	

	var shareModelFlag = 'false';
	if ( $('#shareModelCheckboxId').is(':checked') )
		shareModelFlag = 'true';				

	var appendModelFlag = 'false';
	if ( $('#appendModelCheckboxId').is(':checked') )
		appendModelFlag = 'true';				
		
	//======================================
	// build and encode the url
	//======================================		
	var url = "uploadGit.action?gitPullUrl=" + $('#gitUrlId').val();
	
	url = url + "&javaRootPackageNames=" + $('#rootPackageNameId').val().split(",");
	url = url + "&primaryKeyPattern=" + $('#primaryKeyPatternId').val();
	url = url + "&name=" + $('#shareModelNameId').val();
	url = url + "&description=" + $('#shareModelDescriptionId').val();
	url = url + "&shareModel=" + shareModelFlag;
	url = url + "&appendModel=" + appendModelFlag;

    url = encodeURI( url );

	//======================================
	// show the loading div
	//======================================
	$('#modelLoadingDivId').show();
  	
	$.ajax(
	{
  		url: url,
  	    dataType: 'json',	
	}
	).done(function( data ) 
	{
		//======================================
		// hide the load model file dilog
		//======================================
		$('#load_Local_Model_Modal').modal("hide");
			
		//===============================================
		// load the model into the model content div
		//===============================================  
		loadModelContent( data );	        
		
		//===============================================
		// update the stack and model div
		//===============================================		
		updateTechStackModelStatusDiv();


		//======================================
		// hide the loading div
		//======================================
		$('#modelLoadingDivId').hide();	
	}); 
}


//========================================================
//function : load the published resource from the server
//========================================================
function loadResources() {

	//======================================
	// build the url
	//======================================
	var url 			= "service.action?input=";
	var serviceRequest 	= {"serviceRequestType" : "RESOURCE_LIST" };
	var scope 			= $('#resourceSelectId option:selected').val();
	
	serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	//======================================
	// clear the table of published resources
	//======================================
	document.getElementById("resourcesListDivId").innerHTML = "";
	
	//======================================
	// show the loading image
	//======================================
	$('#resourcesLoadingDivId').show();
	
	$.ajax(
	{
		url: url,
		dataType: 'json',
	}).done(function( data ) 
	{
		var result 	= JSON.parse(data.result);
		var content = '';

		//======================================
		// create a table of resource data
		//======================================
		if ( data.resultCode == "SUCCESS") {
			content = content  
					+ 	"<div class='componentDivClass'>"
					+    "<table id='componentLibraryTableId' class='componentLibraryTabClass'>"
					+     "<tr><thead>"
					+       "<th>&nbsp</th><th style='text-align:left'>Name</th>"
					+		"<th style='text-align:left'>File Name</th>"
					+		"<th style='text-align:left'>Type</th>"
					+		"<th style='text-align:left'>Scope</th>"
					+       "<th style='text-align:left'>Contributor</th>"
					+ 	  "</thead></tr>" 
			
			var rowClass, obj;
			for( var idx = 0; idx < result.length; idx++ ) {
				obj = result[idx];
				rowClass = idx%2 == 0 ? "componentLibraryRowClass1" : "componentLibraryRowClass2";
				content = content 
					+ "<tr class='" + rowClass +  "'>"
					+   "<td style='width:24px' id='" + obj.id + "'></td>"
		    		+   "<td style='text-align:left'>" + JSON.parse(obj.saveParams).name + "</td>"
					+ 	"<td style='text-align:left'>" + obj.fileName  + "</td>"
					+ 	"<td style='text-align:left'>" + obj.resourceType  + "</td>"
		    		+ 	"<td style='text-align:left'>" + obj.scopeType  + "</td>"
					+ 	"<td style='text-align:left'>" + obj.contributor  + "</td>"
					+ "</tr>";
			}
			content = content + '</table>';
			content = content + '</div>';			
		}
		else
			content = data.processingMessage;
			
		//======================================
		// hide the loading image
		//======================================
		$('#resourcesLoadingDivId').hide();

		//======================================
		// assign the resource table content
		//======================================
		document.getElementById('resourcesListDivId').innerHTML = content;
	});
}


//========================================================
//function : load the project details and publish as private
//========================================================
function openProject() {
	$('#load_Project_Modal').modal("show");
	loadProjects();
}

//========================================================
//function : load the project details and publish as private
//========================================================
function loadProjects() {

	//========================================================
	// build out the url
	//========================================================
	var url = "loadAllProjectsForUser.action";
	
	//========================================================
	// show the loading image
	//========================================================
	$('#projectLoadingDivId').show();
	$('#projectProceesingTextId').text( "Loading projects..." );

	$.ajax(
	{
		url: 		url,
		dataType: 	'json',
		async:      true
	})
	.done(function( data ) 
	{
		if ( data != null ) {
			// populate the select control
			let dropdown = document.getElementById('projectSelectId');
			dropdown.length = 0;

			let defaultOption = document.createElement('option');
			defaultOption.text = 'Choose A Project';

			dropdown.add(defaultOption);
			dropdown.selectedIndex = 0;

			let option;
			for (let i = 0; i < data.length; i++) {
			  option = document.createElement('option');
			  option.text = data[i].name;
			  option.value = data[i].id;
			  dropdown.add(option);
			}
		}
		
		//========================================================
		// hide the loading image
		//========================================================
		$('#projectLoadingDivId').hide();

	});
}

function deleteSelectedProject() {

	let dropdown = document.getElementById('projectSelectId');

	if ( dropdown.selectedIndex == 0 ) {
		showStatusDialog( "Error", "No project selected to delete" );
		return;
	}

	Swal.fire({
	  title: 'Are you sure?',
	  text: "Your will not be able to recover this project!",
	  icon: 'warning',
	  showCancelButton: true,
	  confirmButtonColor: '#3085d6',
	  cancelButtonColor: '#d33',
	  confirmButtonText: 'Yes, delete it!'
	}).then((result) => {
		if (result.value) {
			var url = "deleteProject.action?projectId=" + $('#projectSelectId option:selected').val()

			$('#projectLoadingDivId').show();
			$('#projectProceesingTextId').text( "Please wait while deleting..." );
			
			$.ajax(
			{
				url: 		url,
				dataType: 	'json',
			})
			.done(function( data ) 
			{	
				$('#projectLoadingDivId').hide();
				
				if ( data.success == true )
					loadProjects();
				else
					showStatusDialog("Error", "Failed to deleted project");
			});
		}
	})
}
	
function loadSelectedProject() {

	let dropdown = document.getElementById('projectSelectId');

	if ( dropdown.selectedIndex == 0 ) {
		showStatusDialog( "Error", "No project selected to load" );
		return;
	}


	var url = "findProject.action?projectId=" + $('#projectSelectId option:selected').val()
	
	$('#projectLoadingDivId').show();
	$('#projectProceesingTextId').text( "Please wait while loading..." );
	
	$.ajax(
	{
		url: 		url,
		dataType: 	'json',
	})
	.done(function( data ) 
	{		
		gProjectData = data;

		if (gProjectData == null) {
			showStatusDialog('Error','Project failed to load');
		}
		else {
			globalModelName = LOADING_MODEL_PLEASE_WAIT_MSG;
			
			// ==========================================================================
			// select the tech stack package associated with data.techStackPackageName
			// ==========================================================================
			var tableRow = $("#frameworkPackageViewId td").filter(function() {
				return $(this).text() == gProjectData.techStackPackageName;
			}).closest("tr");
			handlePackageSelection(tableRow);

			// ===============================
			// populate the Git inputs
			// ===============================
			var gitParams = gProjectData.gitParams;
			if ( gitParams != null ) {
				$('#gitUserNameId').val(gitParams.username);
				$('#gitPasswordId').val(gitParams.password); 
				$('#gitRepositoryId').val(gitParams.repository);
				$('#gitTagId').val(gitParams.tag);
				$('#gitHostId').val(gitParams.host);
			}
			
			// ===================================================
			// select the model and spoof a click event
			// ===================================================
			loadModels();
			sleep(3000).then(() => {
				tableRow = $("#sharedModelTableId td").filter(function() {
//					console.log( $(this).text() + ' == ' + gProjectData.modelId );
					return $(this).text() == gProjectData.modelId;
				}).closest("tr");
				
//				console.log( 'table row is ' + tableRow.text() + 'ascds' );
				
				if ( tableRow == null || tableRow.text().length == 0 ) { // none found
					globalModelName = MISSING_MODEL_FILE_MSG;
					updateTechStackModelStatusDiv();	// update explicitly
					console.log( 'empty table row' );
				} else {
					// high light the relevent row in the model table
					tableRow.addClass('modelTableRowHighlight').siblings().removeClass('modelTableRowHighlight');					
					loadSelectedModel();
				}
				
				// update the project in the title
				updateProjectDiv();
				
				$('#projectLoadingDivId').hide();

				// hide the load project modal
				$('#load_Project_Modal').modal('hide');
			})
		}

	});
}

//========================================================
//function : create the project details and publish as private
//========================================================
function createProject() {
	
	//========================================================
	// the tech stack package id
	//========================================================
	var selectedTechStackName;
	
	if ( checkTechStackSelected() == false )
		return;
	else 
		selectedTechStackName = getPkgRowSelection( "packageName" );
	
	//========================================================
	// the model in use
	//========================================================
	var selectedModelId;
	
	if ( checkModelLoaded() == false )
		return;
	else {	
	    //========================================================
		// if the loaded model is not from a published source
		// unable to save the project
		//========================================================
		if ( globalModel == undefined ) {
			showStatusDialog('Error', 'The loaded model has not been published yet.' );
			return;
		}
		else
			selectedModelId = globalModel.id;
	}

	//========================================================
	// show the new project dialog status div
	//========================================================
	$('#newProjectStatusDivId').show();
	
	//========================================================
	// apply the options data to the options controls
	//========================================================
	var options = getAppOptions();

	//========================================================
	// apply the options data to the options controls
	//========================================================	
	var url = 'saveProject.action';
	
	url = url + "?techStackPackageName=" + selectedTechStackName;
	url = url + "&modelId=" + selectedModelId;
	url = url + "&name=" + $('#newProjectNameId').val();
	url = url + "&description=" + $('#newProjectDescriptionId').val();
	url = url + "&scopeType=" + $('#newProjectScopeTypeSelectId option:selected').val();

	//==============================================================
	// encode the url 
	//==============================================================	
	url = encodeURI(url);

	//==============================================================
	// encode the options 
	//==============================================================	
	url = url + "&options=" + encodeURIComponent(options);

	
	$.ajax(
	{
		url: 			url,
		dataType: 		'json',
  	    data: 			JSON.stringify(getGitParams()),
  	    contentType: 	'application/json',
  	    type: 			'POST',  		
	}).done(function( data ) 
	{
		gProjectData = data;

		if (gProjectData == null) {
			showStatusDialog('Error','Project failed to create');
		}
		else {
			showStatusDialog('Success','Project created');
			$('#save_Project_Modal').modal('hide');
			updateProjectDiv();
		}
		
		//========================================================
		// hide the new project dialog status div
		//========================================================
		$('#newProjectStatusDivId').hide();
		
		//========================================================
		// hide the new project dialog status div
		//========================================================
		$('#new_Project_Modal').modal('hide');
			
	});
	
}

//========================================================
//function : load the project details and create as private
//========================================================
function saveProject() {
	if (gProjectData == null) {
		showStatusDialog('Error','A project needs to first be loaded');
		return;
	}
	
	//========================================================
	// the tech stack package id
	//========================================================
	var selectedTechStackName;
	
	if ( checkTechStackSelected() == false )
		return;
	else 
		selectedTechStackName = getPkgRowSelection( "packageName" );
	
	//========================================================
	// the model in use
	//========================================================
	var selectedModelId;
	
	if ( checkModelLoaded() == false )
		return;
	else {	
	    //========================================================
		// if the loaded model is not from a published source
		// unable to save the project
		//========================================================
		if ( globalModel == undefined ) {
			showStatusDialog('Error', 'The loaded model has not been published yet.' );
			return;
		}
		else
			selectedModelId = globalModel.id;
	}
	
	//========================================================
	// apply the options data to the options controls
	//========================================================
	var options = getAppOptions();

	//========================================================
	// apply the options data to the options controls
	//========================================================	
	var url = 'saveProject.action';
	
	url = url + "?techStackPackageName=" + selectedTechStackName;
	url = url + "&modelId=" + selectedModelId;
	url = url + "&projectId=" + gProjectData.id;

		//==============================================================
	// encode the url 
	//==============================================================	
	url = encodeURI(url);

	//==============================================================
	// encode the options 
	//==============================================================	
	url = url + "&options=" + encodeURIComponent(options);

	$.ajax(
	{
		url: 			url,
		dataType: 		'json',
  	    data: 			JSON.stringify(getGitParams()),
  	    dataType: 		'json',
  	    contentType: 	'application/json',
  	    type: 			'POST',  		
	}).done(function( data ) 
	{
		if (data == null) {
			gProjectData = data;
			showStatusDialog('Error','Project failed to save');
		}
		else {
			showStatusDialog('Success','Project saved');
		}
	});
}

//========================================================
//function : load the project details and create as private
//========================================================
function showNewProjectDialog() {
	if ( checkTechStackSelected() == true  &&
			checkModelLoaded() == true ) {
		$('#new_Project_Modal').modal('show');
	}
}

//========================================================
//function : formats the tech stack package option values
//========================================================
function getAppOptions() {
	var options = "";

	//==============================================================
	// loop through the inputs to apply to the URL as parameters
	//==============================================================
	$('.optionsInputClass').each(function()
	{
		key 	= $(this).attr('name');
	    value 	= $(this).val();
	    options = options + key + '|' + value + '!';
	})							

	//==============================================================
	// loop through the project option selects to apply to the URL as parameters
	//==============================================================
	$('.optionsSelectClass').each(function()
	{
		key 	= $(this).attr('name');
		value 	= $(this).find(":selected").text();
	    options = options + key + '|' + value + '!';
	});

	//==============================================================
	// loop through the project option checkboxes to apply to the URL as parameters
	//==============================================================
	$('.optionsCheckboxClass').each(function()
	{
		key = $(this).attr('name');
		if ( $(this).is(':checked') == false ) 
			value = 'false';    			
		else
			value = 'true';
	    options = options + key + '|' + value + '!';
	})							
	
	return options;
}

//========================================================
// update the project div with the current project name
//========================================================
function updateProjectDiv() {
	var content = "<span style='vertical-align;middle;text-align:center;font-size:0.8em'><b>";
	
	if ( gProjectData == null ) {
		content = content + "";
		$('#project_buttons_div_id').hide();
	}
	else {
		content = content + gProjectData.name;
		$('#project_buttons_div_id').show();
	}

	content = content + '</b></span>';
	document.getElementById('project_div_id').innerHTML = content;	
		
}

//========================================================
//function : showBuilds modal
//========================================================
function showBuilds() {
	$('#load_Builds_Modal').modal("show");
}

//========================================================
//function : load the builds for the current project
//========================================================
function loadBuilds() {
	//========================================================
	// build out the url
	//========================================================
	
	if ( gProjectData == null || gProjectData == undefined ) {
		showStatusDialog( "General Error", "Please first select a project." );
		openProject();
		return;
	}

	var url = "loadBuilds.action?projectId=" + gProjectData.id;
	
	//========================================================
	// show the loading image
	//========================================================
	$('#buildsLoadingDivId').show();

	$.ajax(
	{
		url: 		url,
		dataType: 	'json',
		async:      true
	})
	.done(function( data ) 
	{
		if (data == null) {
			showStatusDialog('Error','Failed to load builds for project ' + gProjectData.name );
		} else {

			//========================================================
			// populate the build table
			//========================================================
			var content = "<div style='height:200px;overflow-y:auto;padding:0px 10px 0px 0px'>"
					+    "<table id='buildTableId' class='sharedModelTableClass'>"
					+     "<tr ><thead>"
					+   	"<th style='display:none;text-align:left'></th>"
					+   	"<th style='display:none;text-align:left'></th>"
					+       "<th style='text-align:left'>No.</th>"
				 	+ 		"<th style='text-align:left'>Start Time</th>"
				 	+ 		"<th style='text-align:left'>End Time</th>"
				 	+ 		"<th style='text-align:left'>Status</th>"
					+ 	  "</thead></tr>" 
			
			var obj;
			for( var idx = 0; idx < data.length; idx++ )
			{
				obj = data[idx];
				content = content + "<tr class='sharedModelRowClass'>"
							+		  "<td style='display:none;text-align:left' class='keyFieldClass shareModelTDClass' id='" 
											+ obj.id + "'>" + obj.id + "</td>"
							+		  "<td style='display:none;text-align:left' class='keyFieldClass shareModelTDClass' " 
											+ obj.logFileURL + "</td>"
							+ 		  "<td class='buildNumberClass shareModelTDClass'><div style = 'text-align:left;vertical-align: middle;'>" 
											+ obj.buildNumber + '</div></td>'
							+ 		  "<td class='buildDateTimeClass shareModelTDClass'><div style = 'text-align:left;vertical-align: middle;'>" 
											+ obj.startDateTime + "</div></td>"
							+ 		  "<td class='buildDateTimeClass shareModelTDClass'><div style = 'text-align:left;vertical-align: middle;'>" 
											+ obj.endDateTime + "</div></td>"
							+ 		  "<td class='statusClass shareModelTDClass'><div style = 'text-align:left;vertical-align: middle;'>" 
											+ obj.status + "</div></td>"
						  	+ 		"</tr>";
			}
			content = content + '</table>';
			content = content + '</div>';			
		
			//========================================================
			// apply model table
			//========================================================
			$('#buildsDivId').empty();
			$('#buildsDivId').append( content );

		}
		
		//========================================================
		// hide the loading image
		//========================================================
		$('#buildsLoadingDivId').hide();
	});
}

//========================================================
// hide the loading image
//========================================================
function viewBuildSummary() {

	// load the selected build
	var table 		= $('#buildTableId');
	var row 		= table.find('tr.modelTableRowHighlight').eq( 0 );
	var buildId 	= row.find( 'td.keyFieldClass' ).text()
	var url 		= "loadBuildSummary.action?buildId=" + buildId;
	console.log( 'loadbuildsummary url is ' + url );
	$.ajax(
	{
		url: 		url,
		dataType: 	'json',
	})
	.done(function( data ) 
	{
		if (data == null) {
			showStatusDialog('Error','Failed to load build No. ' + row.find( 'td.buildNumberClass' ).text() );
		} else {
			showStatusDialog('Build Summary', '<div style="text-align:left"><pre>' + data + '</pre></div>');
		}
	});
}

//========================================================
// hide the loading image
//========================================================
function viewBuildLog() {
}

//========================================================
// function: hightLigthRow
//========================================================
function highLightRow( row ) {
	row.addClass('modelTableRowHighlight').siblings().removeClass('modelTableRowHighlight');
}

function showUploadPackageModal() {
	$('#upload_Framework_Package_Modal').modal("show");
}

function getGitParams() {
		//==============================================================
	// assign the git parameters
	//==============================================================
	var gitParams = {};
	
	if ( $('#gitCheckboxId').is(':checked') ) {
		gitParams = { 
			"gitParams" : {
				"username":   $('#gitUserNameId').val(), 
				"password":   $('#gitPasswordId').val(), 
				"repository": $('#gitRepositoryId').val(),
				"tag":        $('#gitTagId').val(),
				"host":       $('#gitHostId').val()
			}
		};
	}
	return gitParams;
}
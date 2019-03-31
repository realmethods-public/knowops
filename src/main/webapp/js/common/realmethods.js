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
// global declaration
//==============================================================
var FILE_STORE_URL = "https://s3.amazonaws.com/goframework-cli-bucket/";
var stepper;
var currentStage = "Prepare";
var treema = null;

var globalAppOptions, globalModel, gSelectedPackageRow, gTechStackPackages, gOptions, genAppStats, globalModelName;
var globalFolderOpen 	= false;
var gSharedModels, gSelectedFrameworkPackage;

var SHOW_APP_GEN_OPTION_EMPTY_REQUIRED_FIELDS = false;
		
//========================================================
// function : load the available packages from the server
//========================================================
		
function loadPackages() {
	
	// build url to call backend
	var url = "service.action?input=";
	
	var serviceRequest = {"serviceRequestType" : "TECH_STACK_PACKAGE_LIST" };
	var scope = $('#techStackSelectId option:selected').val();
	
//	if ( scope == "ALL"  )
	//	serviceRequest.scopeType = '';
	//else
		serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	// clear the views
	document.getElementById("frameworkPackageViewId").innerHTML = "";
	document.getElementById("packageInformationAreaId").innerHTML = "";
	
	// show the loading image
	$('#packagesLoadingDivId').show();
	
	$.ajax(
	{
  		url: url,
  		dataType: 'json',
	}
	).done(function( data ) 
	{
		// hide the loading image
		$('#packagesLoadingDivId').hide();

		// assign globally
		gTechStackPackages = JSON.parse(data.result);		
		
		// show the packages into
		showPackages( gTechStackPackages );
		
		// hint to get started
//		document.getElementById("packageInformationAreaId").innerHTML = '<h5>Select a Package to see more details</h5>';
		
		// none selected
		gSelectedPackageRow = null;
		
		// force an update to clear out any previously selected tech stack from the status div
		updateTechStackModelStatusDiv();	
		
	});
}

//========================================================
//function : load the available packages from the server
//========================================================
function loadArchivedAppLibrary() {

	var url 			= "service.action?input=";
	var serviceRequest 	= {"serviceRequestType" : "ARCHIVED_APP_LIST" };
	var scope 			= $('#archivedAppSelectId option:selected').val();
	
	serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	// clear the table of archived apps
	document.getElementById("componentLibraryDivId").innerHTML = "";
	
	// show the loading image
	$('#archivesLoadingDivId').show();
	
	$.ajax(
	{
		url: url,
		dataType: 'json',
	}
	).always(function( data ) 
	{
		var result 	= JSON.parse(data.result);
		var content = '';
		console.log( data );
		if ( data.resultCode == "SUCCESS") {
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
			for( idx = 0; idx < result.length; idx++ ) {
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
			
		// hide the loading image
		$('#archivesLoadingDivId').hide();

		document.getElementById('componentLibraryDivId').innerHTML = content;
	});
}


//********************************************************
//helper to populate the shared model table
//********************************************************
function loadModels() {
	var url = "service.action?input=";
	
	var serviceRequest 	= {"serviceRequestType" : "MODEL_LIST" };
	var scope 			= $('#modelSelectId option:selected').val();
	
	serviceRequest.scopeType = scope;
	
	url = url + encodeURI(JSON.stringify(serviceRequest));
	
	// show the loading image
	$('#modelsLoadingDivId').show();

	$.ajax(
	{
		url: 		url,
		dataType: 	'json',
	})
	.always(function( data ) 
	{
		var content = '';
		
		var result = JSON.parse(data.result);
		
		if ( data.resultCode == "SUCCESS") {
			gSharedModels = result;
			content = content  
					+ 	"<div style='height:200px;overflow-y:auto;padding:0px 10px 0px 0px'>"
					+    "<table id='sharedModelTableId' class='sharedModelTableClass'>"
					+     "<tr ><thead>"
					+       "<th style='display:none'></th><th style='display:none'><th style='display:none'></th><th>Type</th><th>Name</th><th>Description</th><th>Contributor</th>"
					+ 	  "</thead></tr>" 
			
			var obj;
			for( idx = 0; idx < result.length; idx++ )
			{
				obj = result[idx];
				content = content + "<tr class='sharedModelRowClass'>"
							+		  "<td style='display:none;text-align:center' class='keyFieldClass shareModelTDClass' id='" 
											+ obj.id + "'>" + obj.id + "</td>"
							+		  "<td style='display:none;text-align:center' class='shareModelTDClass filePathClass'>" 
											+ obj.filePath + "</td>"
							+		  "<td style='display:none;text-align:center' class='shareModelTDClass modelTypeClass'>" 
											+ obj.modelType + "</td>"
							+		  '<td style="text-align:center" class="shareModelTDClass icon" style="width:28px;padding-left:4px"><img style="width:24px;height:24px" src="'  
											+ determineModelImageUrl(obj.modelType) + '"></td>'
							+ 		  "<td class='nameFieldClass shareModelTDClass'><b><div style = 'word-wrap: break-word'>" 
											+ JSON.parse(obj.saveParams).name  + "</div></b></td>"
							+ 		  "<td class='shareModelTDClass'><div style = 'word-wrap: break-word'>" 
											+ JSON.parse(obj.saveParams).description  + "</div></td>"
							+ 		  "<td class='shareModelTDClass'>" 
											+ obj.contributor + "</td>"											
						  	+ 		"</tr>";
			}
			content = content + '</table>';
			content = content + '</div>';
			
			content = content  + '<div class="checkbox checkbox-slider--b">' 	  			  	
						+ '<label>'
	    	      		+ '<input class="checkSpanClass" id="appendSharedModelCheckboxId" type="checkbox"/><span>Append To Loaded Model</span>' 
						+ '</label></div>';
		}
		else
			content = data.responseText;
			
		// hide the loading image
		$('#modelsLoadingDivId').hide();

		document.getElementById('sharedModelBodyDivId').innerHTML = content;
	});
}


//========================================================
//function : display the packages into the relevant div
//========================================================
function showPackages( packages ) {
	var content = '<span id="tooltip_tech_stack_package_content">'
				+ '<div class="packageDivClass"><table style="width:100%;table-layout:fixed;">';

	var icon, packageName, appType, release, status, infoPage, version, contributor;
	
	// create the package table header
	content = content + '<thead><tr>'
						+ '<th style="width:40px"></th>'
						+ '<th>Tech Stack</th>'
						+ '<th>Contributor</th>'
						+ '<th>Type</th>'
						+ '<th>Version</th>'
						+ '<th>Status</th>'
						+ '<th style="display:none;">Id</th>'
						+ '<th style="display:none;"></th>'
						+ '<th style="display:none;"></th>'
						//+ '<th style="display:none;"></th>'
						+ '</tr></thead>';
	
	// iterate through each framework package		

	var id;
	for( var i=0; i < packages.length; i++ ) {
		packageName	= forDisplay( JSON.parse(packages[i].saveParams).name );
		icon		= packages[i].iconUrl;	
		infoPageUrl	= packages[i].infoPageUrl;
		release		= packages[i].releaseStatus;
		appType		= packages[i].type;
		version		= packages[i].version;
		status		= packages[i].status;
		id			= forDisplay( packages[i].id );
		contributor = packages[i].contributor;

		content = content + '<tr class="packageRowClass">'
			+ '<td class="packageColumnClass icon" style="width:32px;padding-left:4px"><img style="width:24px;height:24px" src="' + icon + '"/></td>'
			+ '<td class="packageColumnClass packageName">' + packageName + '</td>' 
			+ '<td class="packageColumnClass contributor">' + contributor + '</td>'
			+ '<td class="packageColumnClass appType">' + appType + '</td>'
			+ '<td class="packageColumnClass version">' + version + '</td>'
			+ '<td class="packageColumnClass release"">' + status + '</td>'			
			+ '<td class="packageColumnClass packageId" style="display:none;">' + id + '</td>'
			+ '<td class="packageColumnClass infoPageUrl" style="display:none">' + infoPageUrl + '</td>'
			+ '<td class="packageColumnClass descriptor" style="display:none">' + packageName + '</td>'
			//+ '<td class="packageColumnClass filePath" style="display:none">' + filePath + '</td>'

			+ '</tr>';
	}
									
	content = content + '</table></div></span>';
	
	// output to the framework package div	
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
	// if there is no package/framework selected...say so		 		
	
	if ( getPkgRowSelection( "descriptor" ).length == 0 )
	{
		showStatusDialog('Warning', 'Please select a Tech Stack.' );
		
    	closeOtherSlideDivs( packageSlideDiv );
    	packageSlideDiv.slideReveal("show", false);

    	return false;
	}
	
	return true;
}

//*****************************************************************
// Helper to determine if a model has been selected
//*****************************************************************
function checkModelLoaded() {
	// if no model loaded..say so
	
	if ( globalModel == undefined ) {
		showStatusDialog('Warning', 'Please load a new model file, reverse engineer one, or use one from the repository.')

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
	if ( $('#gitCheckboxId').is(':checked') == false )
		return true;
	
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
	// if the architecture type is microservice yet the modal has no components or subcomponents defined
	// warn this will likely result in an unpredictable app generation session
	if ( "microservices" == getPkgRowSelection( "architecture" ).toString().trim() )
	{
		if ( containers == 0 )
		{
			var microServiceGenErrMsg = 'The framework package selected targets a microservices architecture, yet there were no subsystems or components defined within the loaded model. '
						+ 'Continuing forward with app generation may result in unpredictable output.';
						
        	BootstrapDialog.confirm
        	({
            	title: 'WARNING',
            	message: microServiceGenErrMsg,
	            type: BootstrapDialog.TYPE_WARNING, // <-- Default value is BootstrapDialog.TYPE_PRIMARY
	            closable: true, // <-- Default value is false
	            btnCancelLabel: 'Cancel', // <-- Default value is 'Cancel',
	            btnOKLabel: 'Continue', // <-- Default value is 'OK',
	            btnOKClass: 'btn-warning', // <-- If you didn't specify it, dialog type will be used,
	            callback: function(result) 
	            {
	                // result will be true if button was click, while it will be false if 
	                // users close the dialog directly.
	                if(result) 
	                {
	                	handleTheAppCreate();
	                }
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
	
	var startTime = new Date();
	var options = '&options=';
	var key, value;
	
	//==============================================================
	// loop through the inputs to apply to the URL as parameters
	//==============================================================
	
	$('.optionsInputClass').each(function()
	{
		key = $(this).attr('name');
	    value = $(this).val();
	    options = options + key + '|' + value + '!';
	})							

	$('.optionsSelectClass').each(function()
	{
		key = $(this).attr('name');
		value = $(this).find(":selected").text();
	    options = options + key + '|' + value + '!';
	});

	$('.optionsCheckboxClass').each(function()
	{
		key = $(this).attr('name');
		if ( $(this).is(':checked') == false ) 
			value = 'false';    			
		else
			value = 'true';
	    options = options + key + '|' + value + '!';
	})							

	var overWriteChecked = "false";
	if ( $('#appGenOverwriteCheckboxId').is(':checked') )
		overWriteChecked = "true";				
	 
	var bundleAppChecked = "true";
	var gitChecked = "false";
	var scopeType = "PRIVATE";
	
	var clientId = Math.random().toString(36).substring(2, 15) 
					+ Math.random().toString(36).substring(2, 15);
	
	if ( $('#gitCheckboxId').is(':checked') )
	{
		//bundleAppChecked = "false";
		gitChecked = "true";				
	}
	
	if ( $('#scopeTypeCheckboxId').is(':checked') )
	{
		//bundleAppChecked = "false";
		scopeType = "PUBLIC";				
	}
	
	
	var microServices = false;
	
	if ( getPkgRowSelection( "architecture" ).toString().trim() == "microservices"  )
		microServices = true;
	
	var persistAppGen = "true";
	//if ( $('#persistAppGenerationId').is(':checked') == false )
		//persistAppGen = "false";				

	var url = 'generateApp.action?packageId=' + getPkgRowSelection( "packageId" )
			    + '&clientId=' + clientId
				+ '&frameworkDescriptorName=' + getPkgRowSelection( "descriptor" ) 
				+ '&modelFileName=' + globalModelName
				+ '&overWrite=' + overWriteChecked
//				+ '&workingDir=' + $('#appGenDirectoryInputId').val()
				+ '&bundleApp=' + bundleAppChecked
				+ '&gitProject=' + gitChecked
				+ '&scopeType=' + scopeType
				+ '&microServices=' + microServices
				+ '&persistAppGen=' + persistAppGen
				+ '&nameToPersistApp=' + $('#nameToPersistAppGenerationId').val()
				+ '&description=' + $('#persistAppGenerationDescriptionId').val()
				
				/*+ '&primaryKeyType=' + $('#primaryKeyId').val()*/
				+ options;

	//encode the url
	url = encodeURI( url );
	
	// clear the app gen results of any old results and show the the loading indicator	
	
	document.getElementById("appGenResultsDivId").innerHTML = '';
	
	// open the app progress dialog
	genAppProgressShow(clientId);
	
	$.ajax(
	{
  		url: url,
  		dataType: 'json',
	}
	).always(function( data ) 
	{
				
		var content = '';

		genAppStats = data;
				
//		it is either a genAppStats or just a failure status
		if ( typeof( genAppStats.success ) == "undefined") {
			// later record end time
			var endTime = new Date();
			
			// time difference in ms
			var duration = secondsToTime( (endTime - startTime)/1000 );
			
			content = content + "<div style='font-size:1.1em'>";
			content = content + "Creation time:    " + duration.minutes +  "m:" + duration.seconds + "s<br>";
			content = content + "Files Processed:  " + addCommas( genAppStats.totalProcessed ) + '<br>';
			content = content + "Lines Generated:  " + addCommas( genAppStats.totalLines ) + '<br><br>';
			//content = content + "creation status : " + genAppStats.generateStatus + "<br>";
			//content = content + '<button type="button" class="btn btn-primary" onclick="viewCode()">Browse Code</button><br>';

			/*if ( bundleAppChecked === "true" ) {
				content = content + '<p><div class="btn-group">'
				+ '<a class="btn btn-primary btn-md" href="' + genAppStats.resultLocation + '">'
				+ '<i class="fa fa-download" aria-hidden="true"></i> Download Code'
	    		+ '</a></div>&nbsp';			
			}*/
			

/*			 
			content = content + "<table style='border:2px solid black;padding:3px'><tr><td>"
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
			// reload the component library if persisted
			loadArchivedAppLibrary();
				
			// make visible the launch app button if the build checkbox is checked
			if ( $('#gitCheckboxId').is(':checked') )
				$('#launchAppBttnDivId').toggle();
		}
		else // treat it like an action status		{
			content = handleStatusOutputCleanly( data );
		
		document.getElementById("appGenResultsDivId").innerHTML = content;
		
//		$('#appGenRunningDivId').hide();
	});
}

//========================================================
// function : update the app generation options div
//				using the provided parent package name
//========================================================				
function updateAppGenOptions( packageId ) {
	
	// build the url to call 
	var url = 'retrieveOptions.action?frameworkPackageId=' + packageId;

	$.ajax(
	{
  		url: url,
  		dataType: 'text',
	}
	).done(function( data ) 
	{
		// parse the options into an XML object
		var xmldoc = $.parseXML(data);

		// cache the options globally
		gOptions = $( xmldoc );
		
		// display the app gen options
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
	
	// iterate through all the options and create an options style div
	var name;
	var tabName;
	
	pkg_options.find("options").each(function(){
		maxPerRow = $(this).attr('maxPerRow');
		if ( maxPerRow == undefined )
			maxPerRow = 2;
		
		name = $(this).attr('name');
		tabName = 'tabs-' + name.replace(' ', '');
		
		tabDeclContent = tabDeclContent + '<li class="tab"><a href="#' + tabName + '">' + name + '</a></li>';
					
		content = content + '<div id="' + tabName + '" style="border:2px solid black;padding-top:15px;padding-left:10px;padding-bottom:10px">';
		content = content + optionsCreator( $(this).attr('name'), $(this), maxPerRow ) ;
		content = content + "</div>";
	});
	
	tabDeclContent = tabDeclContent + "</ul>";

	// deliver the content into the appGenOptionsDivId
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
    		// show the loading div
    		
			$('#frameworkPackageLoadingDivId').show();
				
			// forget this and the rl will not be fired
			data.submit();		
		},
        done: function (e, data) 
        {
			// hide the loading div
			$('#frameworkPackageLoadingDivId').hide();
			
			var status = data.result;
			
	        if ( status.success == false )
	        {
	        	var msg = handleStatusOutputCleanly( status );
	        	showStatusDialog( "Tech Stack Package Upload Error", msg );
		    
				return;
			}
			else
			{
			    // try to reload the packages
			    loadPackages();			         			

				// hide the load file dialog
	        	$('#upload_Framework_Package_Modal').modal("hide");
	        	
			}	
        }
	}).on('fileuploadsend', function (e, data) 
    {
		// show the loading div
		$('#frameworkPackageLoadingDivId').show();
	});    		
});


// ========================================================
// file upload handler : all model types handled here
// ========================================================

$(function ()  {
    'use strict';
       		
    // call the backend server to load and process the provided local model file
    var url = 'uploadModel.action';
     
    $('#model_upload_id').fileupload(
    {
        url: url,
        dataType: 'json',
        replaceFileInput:true,
        add: function (e, data) 
        {        
		    if ( gModelType === 'sql' )
		    	data.url = 'uploadSqlScript.action';
		    else if ( gModelType === 'json' )
		    	data.url = 'uploadJson.action';
		    else if ( gModelType === 'xmi' || gModelType === 'uml' )
		    	data.url = 'uploadXMI.action';
		    else if ( gModelType === 'ecore'  )
		    	data.url = 'uploadEMF.action';
		    else if ( gModelType === 'jar' )
		    	data.url = 'uploadPojo.action';

        	var shareModelFlag = 'false';
			if ( $('#shareModelCheckboxId').is(':checked') )
				shareModelFlag = 'true';				
        
        	var appendModelFlag = 'false';
			if ( $('#appendModelCheckboxId').is(':checked') )
				appendModelFlag = 'true';				
        	
            var formData = new FormData();
			formData.append('shareModel', shareModelFlag);
			formData.append('appendModel', shareModelFlag);
			formData.append('name', $('#shareModelNameId').val());
			formData.append('description', $('#shareModelDescriptionId').val());
/*			formData.append('contributor', $('#shareModelContributorId').val());
			formData.append('company', $('#shareModelCompanyId').val());
			formData.append('email', $('#shareModelEmailId').val());
*/			
			formData.append('modelType', gModelType );
			formData.append('rootPackageName', $('#rootPackageNameId').val());

			data.formData = formData;
			data.submit().done(function (result, textStatus, jqXHR) 
			{
			    // hide the loading div
				$('#modelLoadingDivId').hide();
			
		        if ( result.success == false )
		        {
		        	var msg = handleStatusOutputCleanly( status );
		        	showStatusDialog( "Upload Model Error", msg );
			    
					return;
				}
				else
				{        
			        // hide the load model file dialog
			        $('#load_Local_Model_Modal').modal("hide");
						
					//===============================================
					// load the model into the model content div
					//===============================================  
			        loadModelContent( result );
			        
				}
			})
            .fail(function (jqXHR, textStatus, errorThrown) 
        	{
        		// hide the loading div
				$('#modelLoadingDivId').hide();
        		showStatusDialog( "Upload Model Error", errorThrown + "\nCheck file content and size" );
        	});
        	
        }
    }).on('fileuploadsend', function (e, data) 
    {
		// show the loading div
		$('#modelLoadingDivId').show();
	}).on('fileuploadadd', function (e, data) 
	{
		$.each(data.files, function (index, file) 
		{
			// assign the file globally
			globalModelName = file.name		         
		});
	});
});
		

//========================================
// function : load model content 
//========================================		
function loadModelContent( modelData ) {
	globalModelData = modelData;
	
	// parse the model data as a JSON object and assign globally
	globalModel = JSON.parse( modelData );		
	
	modelData = globalModel;

	// function fond in modeltreebuilder.js
	buildModelTreeUsingModelData( modelData );
}

//========================================
//function : format for display
//========================================		
function forDisplay( data, defValue ) {
	if ( typeof( data ) == "undefined" || data.length == 0 || data == '[]' )
	{
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
	var content = '<span style="text-align:center;padding-top:6px;padding-left:6px;font-size:1.0em;color:#000000">Tech Stack: ';
	var pkgName = getPkgRowSelection( "packageName" );
	var icon 	= null;
	
	if ( pkgName.length == 0 )
		content = content + "<b>none selected</b>";
	else
	{
		icon = gSelectedFrameworkPackage.iconUrl;
		content = content + '<img style="width:24px;height:24px" src="' + icon + '"/> ' + pkgName;
	}
						
	// apply the model
	content = content + '&nbsp&nbsp&nbsp&nbspModel: ';
	var modelFile = forDisplay( globalModelName );
	if ( modelFile.length == 0 )
		content = content + "<b>none selected</b>";
	else		
	{
		// parse to remove any file path information
		modelFile = modelFile.substring(modelFile.lastIndexOf('/') + 1);
		
		content = content + '<img style="width:24px;height:24px" src="';
		
		if ( modelFile.endsWith( 'xmi' ) || modelFile.endsWith( 'uml' ) )
			content = content + './img/uml.png';
		else if ( modelFile.endsWith( 'core' ) )
			content = content + './img/eclipse.jpg';
		else if ( modelFile.endsWith( 'jar' ) )
			content = content + './img/java-jar.png';
		else if ( modelFile.endsWith( 'sql' ) )
			content = content + './img/sql_script.png';
		else if ( modelFile.endsWith( 'json' ) )
			content = content + './img/json-logo.png';			
		else
			content = content + './img/database.png';
			
		content = content + '"/> ' + modelFile; 
	}
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
	
	// values are an xml represenation of option key/value pairs
	var name, value, required, modifiable, type, fullName, placeholder;
	var flag 			= 0;
	var trimName 		= header.replace(' ', '');
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
		name = $(this).attr('name');
		value = $(this).attr('value');
		required = $(this).attr('required');
		modifiable = $(this).attr('modifiable');
		type = $(this).attr('type');
		placeholder = $(this).attr('placeholder');

		if ( name == undefined )
			errorOptions = errorOptions + 'name option is missing for ' + header + 'options<br>';
		if ( placeholder != undefined )
			placeholder = 'placeholder=' + placeholder;
		else
			placeholder = '';
			
		fullName = header + '.'  + $(this).attr('name');
		
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
		else
		{
			required = "required";
			name = '<b>' + name + ' *</b>';
		}

		options = options + '<div class="col-3">' + name + '</div>'
				+ '<div class="col-2">';
		
		//====================================
		// figure out input type
		//====================================		
		if ( type == "input" )
		{ 
			options = options + '<input class="optionsInputClass form-control" type="text" id="' + fullName + '" name="' + fullName 
			+ '" value="' + value + '"' + ' ' + required + ' ' + modifiable + ' ' + placeholder + '/>';
		}
		else if ( type == "password" )
		{ 
			options = options + '<input class="optionsInputClass form-control" type="password" name="' + fullName 
			+ '" value="' + value + '"' + ' ' + required + ' ' + modifiable + ' ' + placeholder + '/>';
		}
		else if ( type == "textarea" )
		{ 
			options = options + '<textarea class="optionsInputClass form-control" rows="4 cols="32" name="' + fullName 
			+ '" value="' + value + '"' + ' ' + required + ' ' + modifiable + '/>';
		}
		else if ( type == "boolean" )
		{
			var checked = "";
			if ( value == "true" )
				checked = "checked";
						
			options = options 
					+ '<div class="checkbox checkbox-slider--b">' 	  			  	
						+ '<label><input class="optionsCheckboxClass form-control" type="checkbox" value="' + fullName + '" name="' 
							+ fullName + '" ' + checked + ' ' + modifiable + '/><span></span>'
					+ '</label></div>';
		}
		else if ( type == "spinner" )
		{
			var min = $(this).attr('min');
			if ( min == undefined )
			{
				errorOptions = errorOptions + fullName + " - min attribute not provided, defaulting to 0\n";
				min = 0;
			}
				
			var max = $(this).attr('max');
			if ( max == undefined )			
			{
				errorOptions = errorOptions + fullName + " - max attribute not provided, defaulting to 100\n";
				max = 100;
			}
				
			var step = $(this).attr('step');
			if ( step == undefined )
			{
				errorOptions = errorOptions + fullName + " - spin attribute not provided, defaulting to 1\n";
				step = 1;
			}			
			
			options = options + '<input class="form-control optionsInputClass" type="text" name="' + fullName 
			+ '" value="' + value + '" ' + required + ' ' + modifiable + '/>'			
		}
		else if ( type == "select" )
		{
			var selections = $(this).attr('selections');
			
			if ( selections == undefined )
				errorOptions = errorOptions + "selections is missing on " + fullName;
			else
			{
				options = options + '<select class="form-control optionsSelectClass" name="' + fullName 
								+ '" ' + required + ' ' + modifiable + '>';
						
				var selectOptionsArray = selections.split(',');
				var selectValue;
				
				for(var i = 0; i < selectOptionsArray.length; i++)
				{
					selectValue = selectOptionsArray[i];
					options = options + '<option value="' + selectValue + '" ';
					
					if ( value == selectValue )
						options = options + 'selected=selected';
						
					options = options + ">" + selectValue + "</option>";
				}
				options = options + '</select>';
			}
		}
		else
			errorOptions = errorOptions + "type " + type + " is unrecognized\n";	
		// end column declaration
		
		options = options + "</div>";
						
		if ( flag == max_inputs_per_row )	// reset and terminate the row
		{
			flag = 0;
			options = options + '</div>';
		}
		else if ( index === (numOptions - 1) )
			options = options + '</div>';
		//else 								// create a new column
			//options = options + '<td style="width:4px"></td>';
	});
						
	options = options + '</div>';

	if ( errorOptions.length > 0 )
		showStatusDialog( 'Warning', 'The following fields were formatted incorrectly:\n' + errorOptions)

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
	else if ( modelType == 'JSON' )
		image = './img/json-logo.png';			
	else
		image + './img/unknown.png';

	return image;
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
	var url 				= 'find' + modelType + 'Model?id=' + id; // back end call, deduced by applying model type to the name
	
	var appendModelFlag = 'false';
	if ( $('#appendSharedModelCheckboxId').is(':checked') )
		appendModelFlag = 'true';				

	url = url + '&appendModel=' + appendModelFlag;

	$.ajax(
	{
  		url: 		url,
  		dataType: 	'json',
  	    beforeSend: function( data )
  		{
  			$('#sharedModelLoadingDivId').show();
  		}, 
  		success: function( data ) 
		{
        	var status = data;
	        if ( status.success == false )
	        {
	        	var msg = handleStatusOutputCleanly( status );
	        	showStatusDialog( "Error Loading from Local Repository", msg );
			}
			else
			{
				globalModelName = modelFilePath;
				
				//===============================================
				// load the model into the model content div
				//===============================================  
			    loadModelContent( data );
			
		        // hide the shared model dialog
		        $('#load_Shared_Model_Modal').modal("hide");
		        
		        // show the tech stack package wizard
//		        showTSPWizard();
		    }
			
			// hide the loading div
			$('#sharedModelLoadingDivId').hide();			
		}		        
	});
}

//******************************************************
// handler for when a package is selected in the table
//******************************************************
function handlePackageSelection( row ) {
	gSelectedPackageRow = row;
	
	gSelectedPackageRow.addClass('modelTableRowHighlight').siblings().removeClass('modelTableRowHighlight');
	// get the content of the td's id
	
	var fd_selected_text = gSelectedPackageRow.find("td.descriptor").text();
	if ( fd_selected_text.length > 0 )
	{						
		var packageId 	= getPkgRowSelection( "packageId" );
		
		// update the application options 
		updateAppGenOptions( packageId ); 				

		// update the package content area
		updateTechStackDetails( packageId );
		
		// update status div that shows which stack and model are selected 
		updateTechStackModelStatusDiv();
		
	}
}

//=======================================================
// upon stack selection updates the content area with
// its details
//=======================================================

function updateTechStackDetails( packageId ) {
	
	// show the loading div
	$('#packageDetailsLoadingDivId').show();
	
	findTechStackPackage( packageId, function(pkg) {
	
		var xml  		= $.parseXML( pkg.packageXML );
		var packageXML 	= $(xml).find('tech-stack-package');
		var content		= '<div class="packageDivClass"><div class="container col-lg-12">'
							+ '<div class="row justify-content-md-center">'
							+ '<div class="col-lg-4 text-left" style="vertical-align:top">';
		
		var lang_content="<ul>";
		
		// loop through each target-languages
		packageXML.find("target-language").each(function()
		{
			lang_content += '<li>' + forDisplay( $(this).attr('name') ) + '<br>';
		});
		lang_content += "</ul>";
		content = content + sectionCreator( "Target Language(s)", lang_content );	
		content = content + '</div><div class="col-lg-6 text-right" style="vertical-align:top">';
		
		// loop through each tech stack component
		
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
		
		// update the information div with the information
		document.getElementById("packageInformationAreaId").innerHTML = content;
		
		// hide the loading div
		$('#packageDetailsLoadingDivId').hide();
	});

}

//===========================================================================
//function : helper to create a standard section, with a header and content
//===========================================================================

function sectionCreator( header, content ) {
	var section = '<div class="sectionClass"><div class="sectionHeaderClass"><span class="sectionTitleClass">' 
				+ header + '</span></div><div class="sectionBodyClass">' + content + '</div></div>';
	return section;

}

function showStatusDialog( title, msg ) {
	alert( msg );
}

function updateDownloadModelHref( row ) {
	var table 		= $('#sharedModelTableId');
	var row 		= table.find('tr.modelTableRowHighlight').eq( 0 );
	var filePath	= gSharedModels[row.index()].filePath;
	$("#downloadUMLHrefId").attr( 'href', FILE_STORE_URL + filePath );
}

function showLocalModelModal( modelType ) {
    gModelType = modelType;
    
    // only show for jar file type
    $('#rootPackageNameDivId').hide();
    
    // reset the form controls
    $('#localModelFormId')[0].reset();
    
    if ( modelType == 'sql' )
    {
    	$('#shareModelInfoDivId').show();
    	$('#shareModelNameId').attr("placeholder", "DB name used in SQL file");
    }
    else if ( modelType == 'jar' )
    {
    	$('#rootPackageNameDivId').show();	
    }
    else
        $('#shareModelNameId').attr("placeholder", "Enter name");
        
	$('#load_Local_Model_Modal').modal('show');
}

//**************************************************************************************
// show the shared model modal 
//**************************************************************************************
function showSharedModelModal() {
    $('#modelsLoadingDivId').hide();	
    $('#load_Shared_Model_Modal').modal('show');
}


//**************************************************************************************
// provide the callback with the tech stack package as JSON
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
		gSelectedFrameworkPackage = data;
		callback( data);
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
// allows for the browsing of the generated code
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
	
	// load the file.tree with the root directory of the selected framework package 
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


function showAppCreateSlide()
{
	closeOtherSlideDivs( createAppSlideDiv );
	createAppSlideDiv.slideReveal("show", false);
}

function factorySchema()
{
	$('#Factory_Schema_Modal').modal('show');
}

function factoryScheduler()
{
	$('#Factory_Scheduler_Modal').modal('show');
}

function factoryOptions()
{ 
	$('#Factory_Options_Modal').modal('show');
}

function fireUpFactory()
{
}

var factorOps = {
	techStackPkgName : '',
	schemaDir : '',
	schemaFile : '',
	frequencyChkVal : '1',
	frequencyChkUnit : 'hour',
	buidApp : true,
	saveStats : false,
	saveWorkingDir : false 
}

function singleMigrate()
{
	$('#Factory_Modal').modal('hide');
	navBarDiv.slideReveal( 'show', true );
	handleReverseEngClick();
}

function viewModelAsJson()
{
	showStatusDialog( 'Model as JSON', globalModelData )
}

function displayReadme( element )
{
	var $div = $('<div>');
	$div.load( encodeURI(gSelectedFrameworkPackage.infoPageUrl), function(){
	    element.innerHTML = $(this).html();
	});
}


function viewJsonExample() {
}


//************************************************
// for future usage
//************************************************
function checkToUsePayPal() {
	return true;
}

function getParam( name )
{
    var url = location.href;
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( url );
    return results == null ? null : results[1];
}

//*****************************************************
// handler for showing application generation progress
//*****************************************************
function genAppProgressShow(clientId) {
	$('#resultsDivId').show();
	$('#stepperDivId').show();
//	$('#genOutputDivId').show();
	
	// a pause is good here to give the back end a chance to get started
	sleep(1000).then(() => {
		continouslyUpdateGetAppProgress(clientId);
	})				  		
}

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
		).always(function( data ) 
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

function genAppProgressHide() {
	$('#stepperDivId').hide();
}

//*****************************************************
// for now, unconditionally shows the log-on screen
// but eventually will check with the back end to 
// to see if a session exists with a user cached
//*****************************************************
function checkLogon()  {
	// if not logged in
		$('#Logon_Modal').modal('show');
	//
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
	
	// load the user data in the user options form
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
			delay: 4000,
	});
	
		
	//========================================================
	// handle close even on the load local model dialog
	// to clear the checkbox and input/textarea content
	//========================================================
	$('#load_Shared_Model_Modal').on('shown.bs.modal', function () 
	{
		loadModels();
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
		updateDownloadModelHref( $(this) );
	});

	//===============================================
	// tech stack package view row click function
	//===============================================
	$('#frameworkPackageViewId').on('click', 'tr', function() 
	{
		handlePackageSelection( $(this) );
	});		


	//=================================================
	// load the packages available on the server
	//=================================================			 
	loadPackages();
	
	//=================================================
	// load the archived application library
	//=================================================						
	loadArchivedAppLibrary();
	
	//=================================================
	// finally, show the logo slide
	//=================================================		
	closeOtherSlideDivs(logoSlideDiv);
	logoSlideDiv.slideReveal("show", false);

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

		if (input.value === "" && input.getAttribute("required") != undefined)
		{
			if ( setFocusTo == null )
				setFocusTo = input;
			
			errMsg = errMsg + input.getAttribute("name") + "\n"; 
		}
	});
				
	if ( errMsg != '' )
	{
		showStatusDialog('Warning', 'The following fields cannot be left blank:\n\n' + errMsg);
		setFocusTo.focus();
		return false;
	}      
	
	return true;
}

function showGitParamsModal() {
	$('#Git_Modal').modal('show');
}

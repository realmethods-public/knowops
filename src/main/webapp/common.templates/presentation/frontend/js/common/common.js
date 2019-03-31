/*******************************************************************************
  ${aib.getParam("application.company name"} Confidential
  
  2018 ${aib.getParam("application.company name"}
  All Rights Reserved.
  
  This file is subject to the terms and conditions defined in
  file 'license.txt', which is part of this source code package.
   
  Contributors :
        ${aib.getParam("application.company name"} - General Release
 ******************************************************************************/
var urlLinkList = new Array();
var globalUrl = null;

String.prototype.replaceAll = function(target, replacement) {
  return this.split(target).join(replacement);
};

function detectMobileDevice() 
{ 
var isMobile = (/iphone|ipod|android|ie|blackberry|fennec/).test
         (navigator.userAgent.toLowerCase());
    return isMobile;
}

function inspectionDialog( title, theUrl, eventToFire, wFactor, hFactor )
{

globalUrl = theUrl;

	var dialog = new BootstrapDialog
	({
		message: function(dialog) 
		{
        	var $message = $('<div style="height:75%;width:100%"></div>');
        	var pageToLoad = dialog.getData('pageToLoad');
        	$message.load(pageToLoad);

    	    return $message;
	    },
	    title : title,
	    data: 
	    {
	        'pageToLoad': theUrl
	    },
		buttons: 
		[{
            label: 'Close',
            icon: 'glyphicon glyphicon-ban-remove',
            action: function(dialogItself){
                dialogItself.close();
                $( document ).trigger( eventToFire );
                globalUrl = null;
            }
        }],         	
        closable: true
    });

	// default max height and width
	var maxHeight = 643;
	var maxWidth = 1366;
	
	// if not a factor but using old px values, 
	// factor based on common screen as originally built out
	if ( hFactor > 1 )
		hFactor = hFactor/maxHeight;
	else
		hFactor = 1;
		
	if ( wFactor > 1 )
		wFactor = wFactor/maxWidth;
	else
		wFactor = 1;

	// if a dialog is not provided, uses the global inspectionDialog
	if ( typeof(dialog) === "undefined" || dialog == null )
		dialog = $('#inspectionModalId');


	var w = window.innerWidth;
	var h = window.innerHeight;

	// apply factor only if not on a mobile device
	if( detectMobileDevice() == false )
	{
		w = w * wFactor;
		h = h * hFactor;
	}   			

    dialog.realize();
    
    dialog.getModalBody().css('height', 450);
    dialog.getModalBody().css('width', 500);
    dialog.getModalBody().css('overflow-y', 'auto');
    dialog.open();

}

/*
	Displays a dialog to the load the results of executing the provided url
	title : 		Title to display
	theurl :		The URL to fire off through an ajax call.  The results are loaded into this dialog
	modal :			true/false indicator whether this dialog is display as modal
	eventToFire : 	upon closing, will fire off the event provided
	hFactor :		the multiplier factor (0.0 to 1.0) to provide in order to adjust the dialog height by its max. 
						if greater than 1, assumes it is the height to use
	wFactor : 		the multiplier factor (0.0 to 1.0) to provide in order to adjust the dialog width by its max.
						if greater than 1, assumes it is the width to use
	shouldCenter : 	true/false to determine if the dialog should be centered. defaults to true
	dialog :		if provided, theurl's results are loaded into it instead of this one
	loadingDiv :	if provided, will show and hide it during the loading of theurl's results
*/
function inspectInDialog( title, theurl, modal, eventToFire, hFactor, wFactor, shouldCenter, dialog, loadingDiv )
{

	// default max height and width
	var maxHeight = 643;
	var maxWidth = 1366;
	
	// if not a factor but using old px values, 
	// factor based on common screen as originally built out
	if ( hFactor > 1 )
		hFactor = hFactor/maxHeight;
		
	if ( wFactor > 1 )
		wFactor = wFactor/maxWidth;

	// if a dialog is not provided, uses the global inspectionDialog
	if ( typeof(dialog) === "undefined" || dialog == null )
		dialog = $('#inspectionModalId');
	
	// if a loadingDiv is not provided, use the predefined one found on the homepage
	if ( typeof(loadingDiv) === "undefined" || loadingDiv == null )
		loadingDiv = $('#LoadingDivId');

	// show the loading div
	loadingDiv.show();

	var w = window.innerWidth;
	var h = window.innerHeight;

	// apply factor only if not on a mobile device
	if( detectMobileDevice() == false )
	{
		w = w * wFactor;
		h = h * hFactor;
	}   			

	$('#inspectionModalHeaderId').html( title );
			   		   		
	// if theurl is empty, simply display the dialog provided
	if ( typeof(theurl) === "undefined" || theurl == null )
	{
			$('#inspectionModalBodyId').html( 'Loading' );
/*	      	dialog.dialog("option", "modal", modal);
	      	dialog.dialog("option", "height", h );
	      	dialog.dialog("option", "width", w);
	      	if ( typeof(shouldCenter) === "undefined" || shouldCenter == true ) 
				dialog.dialog( "option", "position", { my: "center", at: "center", of: window } );
	      	else
		      	dialog.dialog( "option", "position", { my: "left top", at: "left top", of: window } );		      	
*/
	      	dialog.modal('show');   			
	}
	else	// theurl is provided
	{
		// fire off an ajax call to the back end to load its results upon execution completion
		jQuery.ajax
		({
			url: theurl,
	   		type: "POST",
	   		async: true,
	   		beforeSend : function ()
	   		{
	   			$('#inspectionModalHeaderId').html( title );
	   			$('#inspectionModalBodyId').html( 'Loading' );
		      	$('#inspectionModalId').modal('show');
	   		},
		    error: function(data, status)
			{
			console.log( 'error' );
				$('#inspectionModalBodyId').html("Error executing request and loading page. Please press the Close button and try again.");
			},
		    complete: function(data,status)
		    {
		    console.log( 'complete' );
				$('#inspectionModalBodyId').html(data.responseText);
					
				$('#inspectionModalBodyId').on('hidden.bs.modal', function () 
				{
					$( document ).trigger( eventToFire );
				}); 
				
		        loadingDiv.hide();
		    }
		});
	}
}

/*
	closes the provided dialog.  if the provided dialog is null, uses the inspectionDialog
*/
function closeInspectionDialog( dialog )
{
	if ( dialog == null )
		dialog = $('#inspectionDialog');
		
//	dialog.dialog('close');
};

/*
	helper method uses to mimic a linked list of urls pushed and pop for the sake of tracking navigation
	not necessary and needs more formal communication to use accurately. 
*/
function pushUrlToDiv( divTarget, url )
{
	urlLinkList.push( url );
	divTarget.load( url );
}

/*
	helper method uses to mimic a linked list of urls pushed and pop for the sake of tracking navigation
	not necessary and needs more formal communication to use accurately. 
*/
function popUrlToDiv( divTarget )
{
	if ( urlLinkList.length > 1 )
	{
		var lastUrl = urlLinkList[ urlLinkList.length - 2 ];
		urlLinkList.pop();	
		divTarget.load( lastUrl );
	}
}

function getSelectedIdFromGrid( grid, rowId )
{
	var sel_id = grid.jqGrid('getGridParam', 'selrow'); 
	var objectId;

	if ( sel_id )
	{
		objectId = grid.jqGrid('getCell', sel_id, rowId);
	}
	return( objectId );
}

function getSelectedIdsFromGrid( grid, rowId )
{
	var sel_ids = grid.jqGrid('getGridParam', 'selarrrow'); 
	var objectIds = [];

	if ( sel_ids )
	{
		for (i = 0; i < sel_ids.length; i++) 
    		objectIds[i] = grid.jqGrid('getCell', sel_ids[i], rowId);
	}
	return( objectIds );
}

function getParamHelper( name, url )
{
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( url );
    if ( results == null || results == 'null')
    	retVal = null;
    else
    	retVal = results[1];
    	
    if ( retVal == null && globalUrl != null )
    {
    	results = regex.exec( globalUrl );
	    if ( results == null || results == 'null')
	    	retVal = null;
	    else
	    	retVal = results[1];
    }
    
    return retVal;
    
}

function getParam( name )
{
	if ( globalUrl != null )
	{
    	var retVal = getParamHelper( name, globalUrl );
    	if ( retVal == null || retVal == 'null' )
    		return( getParamHelper( name, location.href ) );
    	else
    		return retVal;
    }	
  	else
	    return( getParamHelper( name, location.href ) );
}

function showInMainPanel( url )
{
	$( "#treeSelectPanelDivId" ).load( url );
}

function sectionCreator( header, content )
{
	var section = '<div class="sectionClass"><div class="sectionHeaderClass"><span class="sectionTitleClass">' 
				+ header + '</span></div><div class="sectionBodyClass">' + content + '</div></div>';
	return section + '<br>';
}

function upperCaseFirstLetter( s )
{
	return s[0].toUpperCase() + s.slice(1);
}

function lowerCaseFirstLetter( s )
{
	return s[0].toLowerCase() + s.slice(1);
}

function invocationHelper( url, field, id )
{
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

	    return( val );
	});
}

function multiselect( sourceUrl, modelUrl, roleName, value, text, callBackFunc )
{
	var dialog = new BootstrapDialog
	({
		message: function(dialog) 
		{
        	var $message = $('<div style="height:80%;width:100%"></div>');
        	var pageToLoad = dialog.getData('pageToLoad');
        	$message.load(pageToLoad);

    	    return $message;
	    },
	    title : 'Selection for ' + roleName,
	    data: 
	    {
	        'pageToLoad': '/html/multiSelect.html?sourceUrl='+sourceUrl
	        	+'&modelUrl='+modelUrl
	        	+'&roleName='+roleName
	        	+'&value='+value+'&text='+text
	    },
		buttons: 
		[{
            label: 'Save and Close',
            icon: 'glyphicon glyphicon-ban-save',
            action: function(dialogItself)
            {
                dialogItself.close();
                var ids = [];
                $("#multiselect_to_1 option").each(function()
				{
					ids.push( $(this).val() );
				});
				callBackFunc(ids);
            }
        },
        {
            label: 'Close',
            icon: 'glyphicon glyphicon-ban-remove',
            action: function(dialogItself){
                dialogItself.close();
            }
        }],         	
        closable: true
    });
    
    dialog.realize();
    dialog.getModalBody().css('height', 200 );
    dialog.getModalBody().css('width', 200 );
    dialog.open();
    
}

function loadOptionsWithJSONData( selectId, data, val, text, includeBlank )
{
	// $("'#" + selectId + "'").empty();

	var newOption;
	if ( includeBlank == true )
	{
		newOption = document.createElement("option");
		newOption.value = 'no selection';
		newOption.text = '--Make A Selection--';
		document.getElementById(selectId).appendChild(newOption);		
	}
	
	jQuery.each(data, function (index, value) 
	{
    	if (typeof value == 'object') 
    	{
    		newOption = document.createElement("option");
			newOption.value = value[val];
			newOption.text = value[text];
			document.getElementById(selectId).appendChild(newOption);
    	}
	});
}

function formDataToJson($o) 
{
    var o = {},
        real_value = function($field) {
            var val = $field.val() || "";

            // additional cleaning here, if needed

            return val;
        };

    if (typeof o != "object") {
        $o = $(o);
    }

    $(":input[name]", $o).each(function(i, field) {
        var $field = $(field),
            name = $field.attr("name"),
            value = real_value($field);

        if (o[name]) {
            if (!$.isArray(o[name])) {
                o[name] = [o[name]];
            }

            o[name].push(value);
        }

        else {
            o[name] = value;
        }
    });

    return o;
}

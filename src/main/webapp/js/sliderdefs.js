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

var navBarDiv, packageSlideDiv, modelSlideDiv, reverseSlideDiv, optionsSlideDiv, createAppSlideDiv, librarySlideDiv, helpSlideDiv, logoSlideDivWithInstructions, logoSlideDiv;

$(function()
{
	// main side nav bar
	navBarDiv = $("#navBarDivId").slideReveal(
	{
	  trigger: $("#menuTriggerId"),
	  push: true,
	  overlay: false,
	  position : "left",
	  width : 96,
	  speed : 500,
	  shown: function(obj)
	  {
	    document.getElementById("menuTriggerId").innerHTML = '<span id="closeChevronId" style="z-index:1000;font-size:0.7em" class="glyphicon glyphicon-chevron-left theToolTip" title="Close the control panel"></span>';
	    $('#menuTriggerId').on('mouseenter', '.theToolTip:not(.tooltipstered)', function(){
		    $(this)
		        .tooltipster({
		        				theme: 'tooltipster-shadow',
		    					animation: 'fade',
   								delay: 2500,
   								})
		        .tooltipster('open');
		});
//	    obj.addClass("left-shadow-overlay");
	  },
	  hidden: function(obj)
	  {
	    document.getElementById("menuTriggerId").innerHTML = '<span id="openChevronId" style="font-size:0.7em" class="glyphicon glyphicon-chevron-right theToolTip" title="Open the control panel"></span>';
	    $('#menuTriggerId').on('mouseenter', '.theToolTip:not(.tooltipstered)', function(){
		    $(this)
		        .tooltipster({
		        				theme: 'tooltipster-shadow',
		    					animation: 'fade',
   								delay: 2500,
   								})
		        .tooltipster('open');
		});

//	    obj.removeClass("left-shadow-overlay");
		closeOtherSlideDivs(null);		
	  }    
	});

	// help slider
	helpSlideDiv = $("#helpSlideId").slideReveal(
	{
	  trigger: $("#helpTriggerId"),
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '95%',
	  speed : 700,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs( helpSlideDiv );
	  }
	});

	
	// package slider
	packageSlideDiv = $("#frameworkPackageSlideId").slideReveal(
	{
	  trigger: $("#frameworkPackageTriggerId"),
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs(packageSlideDiv);
	  }
	});

	// model slider
	modelSlideDiv = $("#modelSlideId").slideReveal(
	{
	  trigger: $("#modelTriggerId"),
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs(modelSlideDiv);
	  	$('#reverseEngineerDivId').hide();
	  	$('#loadModelDivId').show();
	  }
	});

	// options slider
	optionsSlideDiv = $("#optionsSlideId").slideReveal(
	{
	  trigger: $("#optionsTriggerId"),
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs( optionsSlideDiv );
	  }
	});

	// create app slider
	createAppSlideDiv = $("#createAppSlideId").slideReveal(
	{
	  trigger: $("#createAppTriggerId"),
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs( createAppSlideDiv );
	  }
	});

	// library slider
	librarySlideDiv = $("#librarySlideId").slideReveal(
	{
	  trigger: $("#libraryTriggerId"),
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs( librarySlideDiv );
	  }
	});

    // logo slider with instructions
	logoSlideDivWithInstructions = $("#logoWithInstructionSlideId").slideReveal(
	{
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '70%',
	  speed : 500,
	  show: function(obj)
	  {
	  }
	});

    // logo slider 
	logoSlideDiv = $("#logoSlideId").slideReveal(
	{
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  }
	});

	$('#json-panel-id').scotchPanel(
	{
        hoverSelector: '.toggle-json-panel',
        containerSelector: '#toggle-json-panel-div-id',
        direction: 'left',
        duration: 600,
        transition: 'ease-in-out',
        distanceX: '50%',
        enableEscapeKey: true,
    });

	$('#uml-panel-id').scotchPanel(
	{
        hoverSelector: '.toggle-uml-panel',
        containerSelector: '#toggle-uml-panel-div-id',
        direction: 'left',
        duration: 600,
        transition: 'ease-in-out',
        distanceX: '70%',
        enableEscapeKey: true,
    });
       
	$('#ecore-panel-id').scotchPanel(
	{
        hoverSelector: '.toggle-ecore-panel',
        containerSelector: '#toggle-ecore-panel-div-id',
        direction: 'left',
        duration: 1000,
        transition: 'ease-in-out',
        distanceX: '50%',        
        enableEscapeKey: true
    });
    
	$('#repository-panel-id').scotchPanel(
	{
        hoverSelector: '.toggle-repository-panel',
        containerSelector: '#toggle-repository-panel-div-id',
        direction: 'right',
        duration: 1000,
        transition: 'ease-in-out',
        distanceX: '50%',
        enableEscapeKey: true
    });	

    $('#j2ee-jar-panel-id').scotchPanel(
	{
		hoverSelector: '.toggle-j2ee-jar-panel',	
        containerSelector: '#toggle-j2ee-jar-panel-div-id',
        direction: 'left',
        duration: 1000,
        transition: 'ease-in-out',
        distanceX: '50%',
        enableEscapeKey: true
    });	
    
    $('#java-jar-panel-id').scotchPanel(
	{
		hoverSelector: '.toggle-java-jar-panel',	
        containerSelector: '#toggle-java-jar-panel-div-id',
        direction: 'left',
        duration: 1000,
        transition: 'ease-in-out',
        distanceX: '50%',
        enableEscapeKey: true
    });	
    
    $('#sql-script-panel-id').scotchPanel(
	{
        hoverSelector: '.toggle-sql-script-panel',
        containerSelector: '#toggle-sql-script-panel-div-id',
        direction: 'left',
        duration: 1000,
        transition: 'ease-in-out',
        distanceX: '50%',        
        enableEscapeKey: true
    });	
    
    $('#database-panel-id').scotchPanel(
	{
        hoverSelector: '.toggle-database-panel',
        containerSelector: '#toggle-database-panel-div-id',
        direction: 'right',
        duration: 1000,
        transition: 'ease-in-out',
        distanceX: '50%',
        enableEscapeKey: true
    });

	 closeOtherSlideDivs( null );
	 //logoSlideDivWithInstructions.slideReveal("show", false);
                        	
  });

  function handleReverseEngClick()
  {
    $('#loadModelDivId').hide();
	$('#reverseEngineerDivId').show();

	closeOtherSlideDivs( modelSlideDiv );
	
	modelSlideDiv.slideReveal('toggle', false);
	
	return false;
  }

  function closeOtherSlideDivs( notThisDiv )
  {
	if ( notThisDiv != packageSlideDiv )
		packageSlideDiv.slideReveal("hide", false);

	if ( notThisDiv != modelSlideDiv )
		modelSlideDiv.slideReveal("hide", false);

	if ( notThisDiv != optionsSlideDiv )
		optionsSlideDiv.slideReveal("hide", false);

	if ( notThisDiv != createAppSlideDiv )
		createAppSlideDiv.slideReveal("hide", false);

	if ( notThisDiv != librarySlideDiv )
		librarySlideDiv.slideReveal("hide", false);

	if ( notThisDiv !=  helpSlideDiv )
		helpSlideDiv.slideReveal("hide", false);

	if ( notThisDiv == null )
		logoSlideDiv.slideReveal("show", false);
	else
	 	logoSlideDiv.slideReveal("hide", false);
	 	
/*	if ( notThisDiv !=   factorySlideDiv )
		factorySlideDiv.slideReveal("hide", false);

	if ( notThisDiv !=   wizardSlideDiv )
		wizardSlideDiv.slideReveal("hide", false);
*/
	// always force a hide
	logoSlideDivWithInstructions.slideReveal("hide", false);
}
  	
  	
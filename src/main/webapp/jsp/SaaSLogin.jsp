<!-- * Copyright (c) 2017 realMethods, Inc. All rights reserved.
 * This software is the proprietary information of cloudMigrate, Inc.
 * Use is subject to license terms formally and mutually agreed to 
 * by cloudMigrate, Inc. and you.
 * 
 * Contributor
 *     realMethods, Inc - initial API and implementation
-->
<head>
    <meta charset="utf-8">
    <title>realMethods DevOps Project Generator</title>
    
	<link rel="icon" href="./img/favicon-16x16.fw.png">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="realMethods platform, applies a tech stack package to a business model to create rich fully functional apps">
    <meta name="author" content="realMethods">
     
	<link rel="stylesheet" href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">

<!-- css links -->

	<!-- Bootstrap -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" 
		integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">

    <!-- tool tip -->
    <link rel="stylesheet" type="text/css" href="./css/tooltipster/tooltipster.bundle.min.css" />
    <link rel="stylesheet" type="text/css" href="./css/tooltipster/themes/tooltipster-sideTip-shadow.min.css" />

    <!-- toggle checkbox -->
    <link href="./css/toggle.checkbox.css" rel="stylesheet">

    <!-- file upload -->
    <link href="./js/jquery.file.upload/css/style.css">
	<link href="./js/jquery.file.upload/css/jquery.fileupload.css">

	<!-- tree -->
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css">

	<!-- google font -->
	<link rel="stylesheet" type="text/css" href="http://fonts.googleapis.com/css?family=Libre Franklin">

	<!-- awesome font -->	
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/prism/0.0.1/prism.min.css">
	
	<!-- bootstrap glyphicons-->	
	<link href="//netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap-glyphicons.css" rel="stylesheet">
	
	<!-- file tree viewer -->
	<link href="./js/file.tree/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen" />
	
	<!-- treema json viewer -->
	<link href="./js/treema/treema.css" rel="stylesheet" type="text/css" media="screen" />
	
	<!-- wizard stepper -->
	<link rel="stylesheet" href="./css/stepper/jquery.step-maker.css">
	
	<!-- realmethods - last so its contents take precedent -->
    <link href="./css/common/realmethods.css" rel="stylesheet">
    
	<!-- js links -->
	<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js" integrity="sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q" crossorigin="anonymous"></script>
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>

	<!-- tab -->
	<script src="./js/tab/jquery.easytabs.js" type="text/javascript"></script>

	<!-- modal message -->
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@8"></script>

	<!-- tool tips -->
	<script type="text/javascript" src="./js/tooltipster/tooltipster.bundle.min.js"></script>

	<!-- file upload -->
	<script src="./js/jquery.file.upload/js/vendor/jquery.ui.widget.js"></script>
	<script src="./js/jquery.file.upload/js/jquery.iframe-transport.js"></script>
	<script src="./js/jquery.file.upload/js/jquery.fileupload.js"></script>

	<!-- tree -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"></script>

    <!-- stepper wizard -->
	<script src="./js/stepper/jquery.step-maker.min.js"></script>

	<!-- slide reveal -->	
	<script src="./js/slide.reveal/dist/jquery.slidereveal.js"></script>
	<script src="	https://raw.githubusercontent.com/nnattawat/slidereveal/master/dist/jquery.slidereveal.min.js"></script>
	
	<!-- easing -->
	<script src="./js/file.tree/jquery.easing.js" type="text/javascript"></script>
	
	<!-- file tree viewer -->
	<script src="./js/file.tree/jqueryFileTree.js" type="text/javascript"></script>

	<!-- treema json viewer -->
	<script src="./js/treema/treema.js" type="text/javascript"></script>
	<script src="./js/treema/treema-utils.js" type="text/javascript"></script>

	<!-- tiny validator -->
	<script src="./js/tv/tv4.js" type="text/javascript"></script>

 	<!-- realmethods -->
	<script src="./realmethods/common/realmethods.js"></script>
	<script src="./realmethods/common/modeltreebuilder.js"></script>
	<script src="./realmethods/common/wizardnav.js"></script>

<script>

(function ($) {
    $.include = function (url) {
        $.ajax({
            url: url,
            async: false,
            success: function (result) {
                document.write(result);
            }
        });
    };
}(jQuery));
    
//========================================================
// jQuery document ready function
//========================================================

$( document ).ready(function()
{			
	$("body").show();
	$('#Logon_Modal').modal('show');

    $('#registerUserBttnId').hide();
    $('#subscriptionBttnId').show();
	
});
</script>

</head>

<body id="realmethodsBodyDivId" style="display:none">


<script>
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
//		closeOtherSlideDivs(null);		
		closeOtherSlideDivs(logoSlideDiv);
		logoSlideDiv.slideReveal("show", false);
	  }    
	});

	// help slider
	helpSlideDiv = $("#helpSlideId").slideReveal(
	{
	  trigger: $("#helpTriggerId"),
	  top: 40,
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
	  top: 40,
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
	  top: 40,
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs(modelSlideDiv);
	  //	$('#reverseEngineerDivId').hide();
	  //	$('#loadModelDivId').show();
	  }
	});

	// options slider
	optionsSlideDiv = $("#optionsSlideId").slideReveal(
	{
	  trigger: $("#optionsTriggerId"),
	  top: 40,
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
	  top: 40,
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
	  top: 40,
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

	// resources slider
	resourcesSlideDiv = $("#resourcesSlideId").slideReveal(
	{
	  trigger: $("#resourcesTriggerId"),
	  top: 40,
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs( resourcesSlideDiv );
	  }
	});
	// user options slider
	userOptionsSlideDiv = $("#userOptionsSlideId").slideReveal(
	{
	  trigger: $("#userOptionsTriggerId"),
	  top: 40,
	  push: false,
	  overlay: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  	closeOtherSlideDivs( userOptionsSlideDiv );
	  }
	});

    // logo slider with instructions
	logoSlideDivWithInstructions = $("#logoWithInstructionSlideId").slideReveal(
	{
	  top: 40,
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
	  top: 40,
	  push: false,
	  position : "right",
	  width: '90%',
	  speed : 500,
	  show: function(obj)
	  {
	  }
	});

	 //closeOtherSlideDivs( null );
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
	if ( notThisDiv != logoSlideDiv )
		logoSlideDiv.slideReveal("hide", false);

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

	if ( notThisDiv != resourcesSlideDiv )
		resourcesSlideDiv.slideReveal("hide", false);

	if ( notThisDiv != userOptionsSlideDiv )
		userOptionsSlideDiv.slideReveal("hide", false);				

	if ( notThisDiv !=  helpSlideDiv )
		helpSlideDiv.slideReveal("hide", false);

/*	if ( notThisDiv == null )
		logoSlideDiv.slideReveal("show", false);
	else
	 	logoSlideDiv.slideReveal("hide", false);
*/
	 	
/*	if ( notThisDiv !=   factorySlideDiv )
		factorySlideDiv.slideReveal("hide", false);

	if ( notThisDiv !=   wizardSlideDiv )
		wizardSlideDiv.slideReveal("hide", false);
*/
	// always force a hide
	logoSlideDivWithInstructions.slideReveal("hide", false);
  }
  	
  	
</script>

<div "headerId">

	<div  id="headerTableId" class="container-fluid statusTableClass">
		<div class="row align-items-center">
			<div class="col-4 align-self-center" style="vertical-align:middle">
			
	<!--			<div id="wizardDivId">
		  			<a href="#" onclick="wizard()">
						<img src="./img/wizard.hat.png" class="theToolTip" title="Get started with the Creation Wizard"/>
		  			</a>
				  			
		  		</div> -->
				<!-- wizardDivId -->

				<div id="newProjectDivId">
		  			<a href="#" onclick="showNewProjectDialog()">
						<img src="./img/new-circle.png" class="theToolTip leftImageClass" title="New project"/>
		  			</a>
		  		</div> <!-- saveProjectDivId -->

				<div id="saveProjectDivId">
		  			<a href="#" onclick="saveProject()">
						<img src="./img/save-disk.png" class="theToolTip leftImageClass" title="Save project"/>
		  			</a>	
		  		</div> <!-- saveProjectDivId -->

				<div id="openProjectDivId">
		  			<a href="#" onclick="openProject()">
						<img src="./img/open-folder.png" class="theToolTip leftImageClass" title="open project"/>
		  			</a>	
		  		</div> <!-- openProjectDivId -->

				<div class="container-fluid ">
					<div class="row justify-content-start">
						<div class="col-4 align-self-center" style="color:black;vertical-align:middle;text-align:right">
							<div id="project_div_id">
							</div> <!-- project_div_id -->
						</div>
						<div class="col-4 align-self-center" id="project_buttons_div_id" style="display:none;vertical-align:middle;text-align:left">
							<a href="#" onclick="showBuilds()">
								<img src="./img/useroptions.png" class="theToolTip leftImageClass" title="Show builds for project"/>
							</a>
							<a href="#" onclick="showGitParamsModal()">
								<img src="./img/git-menu.png" class="theToolTip leftImageClass" title="Set Git parameters"/>
							</a>

						</div> <!-- project_buttons_div_id -->
					</div>
				</div>
	  		</div>

	  		<div class="col-6" style="vertical-align:middle;text-align:center">		    
			    <div class="container">
				    <div class="row align-items-center">
						<div id="package_fd_model_div_id" style="vertical-align:middle;text-align:center">
							&nbsp
						</div> <!-- package_fd_model_div_id -->
					</div>
				</div>
			</div>
			
	  		<div class="col" style="vertical-align:middle">
				<a href="#" id="helpTriggerId">
					<img src="./img/help.png" class="theToolTip leftImageClass" title="Launch the User's Guide"/>
				</a> <!-- helpTriggerId -->
		
				<div id="helpSlideId" class="slideClass">
					<iframe src="https://realmethods.com/home/usersguide/" height=500 width=1300 scrolling="auto"></iframe>
				</div>
						<!-- helpSlideId -->

				<div id="supportImageDivId">
					<a href="#" data-toggle="modal" data-target="#Submit_Request_Modal">
						<img src="./img/support.png" class="leftImageClass"/>
					</a>
				</div> <!-- supportImageDivId -->

				<div id="logoutDivId">
					<a href="#" onclick="logout()">
						<img src="./img/powerdown.png" class="leftImageClass"/>
					</a>
				</div> <!-- logoutDivId -->
			</div>
    	</div>
  	</div> <!-- headerTableId --> 

</div> <!-- headerId -->

<div id="navBarDivId" class="sideNavBarClass">

	<table id="navBarTableId" class="mainTableClass">
    	<tr>      
      		<td>      
				<table class="sideNavImageTableClass">
        
<!-- navbar for tech stack package -->        
        
          			<tr class="sideNavImageTableRowClass"  id="techStackSideNavRowId">          
            			<td class="sideNavBarImageClass">
            				<!-- load techstackpage.html -->
            				<div w3-include-html="techstackpackage.html" id="techStackPageDivId">
            					<script>$.include("./html/techstackpackage.html");</script>
            				</div>
            			</td>
          			</tr> <!-- techStackPackageSideNavRowId -->
          
<!-- navbar for new model  -->
          
					<tr class="sideNavImageTableRowClass" id="newModelSideNavRowId">
          				<td class="sideNavBarImageClass">
              				<!-- load nodemodel.html -->
			  				<div w3-include-html="newmodel.html" id="newModelPageId"/>
			  					<script>$.include("./html/newmodel.html");</script>
			  				</div>                                      
            			</td>
          			</tr> <!-- newModelSideNavRowId-->

<!-- navbar for reverse engineer 

          			<tr class="sideNavImageTableRowClass" id="reverseEngineerSideNavRowId">
            			<td class="sideNavBarImageClass"> -->
              				<!-- load revengineer.html -->
			  				<!--<div w3-include-html="revengineer.html" id="revEngineerPageId"/>
			  					<script>$.include("./html/revengineer.html");</script>
			  				</div>              
            			</td>
          			</tr> -->  <!-- reverseEngineerSideNavRowId -->

<!-- navbar for app options -->
          
          			<tr class="sideNavImageTableRowClass" id="appOptionsSideNavRowId">
            			<td class="sideNavBarImageClass">
              				<!-- load appOptions.html -->
			  				<div w3-include-html="appoptions.html" id="appOptionsPageId"/>
			  					<script>$.include("./html/appoptions.html");</script>
			  				</div>              
            			</td>
          			</tr> <!-- appOptionsSideNavRowId -->

<!-- navbar for resources -->
         
          			<tr class="sideNavImageTableRowClass" id="resourcesSideNavRowId">
						<td class="sideNavBarImageClass">
			  				<!-- load the resources.html -->
			  				<div w3-include-html="resources.html" id="resourcesPageId"/>
			  					<script>$.include("./html/resources.html");</script>
			  				</div>
            			</td>
          			</tr> <!-- resourcesSideNavRowId -->

<!-- navbar for app archive -->
<!--         
          			<tr class="sideNavImageTableRowClass" id="appArchiveSideNavRowId">
						<td class="sideNavBarImageClass">
			  				<div w3-include-html="apparchive.html" id="appArchivePageId"/>
			  					<script>$.include("./html/apparchive.html");</script>
			  				</div>
            			</td>
          			</tr> 
-->
<!-- appArchiveSideNavRowId -->
          
<!-- navbar for app generation -->
          
          			<tr class="sideNavImageTableRowClass" id="generateSlideNavRowId">
            			<td class="sideNavBarImageClass">
               				<!-- load the generate.html -->
			   				<div w3-include-html="generate.html" id="generatePageId"/>
			   					<script>$.include("./html/generate.html");</script>
			   				</div>            
	 					</td> 
 			   		</tr> <!-- generateSlideNavRowId -->

<!-- navbar for user options -->
          
          			<tr class="sideNavImageTableRowClass" id="generateSlideNavRowId">
            			<td class="sideNavBarImageClass">
               				<!-- load the useroptions.html -->
			   				<div w3-include-html="useroptions.html" id="userOptionsId"/>
			   					<script>$.include("./html/useroptions.html");</script>
			   				</div>            
	 					</td> 
 			   		</tr> <!-- generateSlideNavRowId -->
 			   		
 				</table> <!-- sideNavImageTableClass -->
      		</td>
    	</tr>
	
	</table> <!-- mainTableId -->
           
</div> <!-- navBarDivId -- >

<!-- GlyphIcon to open the control panel -->
<div id="menuTriggerId">
  <span style="font-size:0.7em" class="btn-lg glyphicon glyphicon-chevron-right theToolTip" title="Open the control panel"></span>
</div>


<!-- load logopage.html -->
<div w3-include-html="logopage.html" id="logoPageId">
	<script>$.include("./html/logopage.html");</script>
</div>

<!-- load wizard.dialogs.html -->
<div w3-include-html="wizard.dialogs.html" id="wizardDialogsDivId">
	<script>$.include("./html/wizard.dialogs.html");</script>
</div>

<!-- load wizard.dialogs.html -->
<div w3-include-html="app.dialogs.html" id="appDialogsDivId">
	<script>$.include("./html/app.dialogs.html");</script>
</div>


</body>


</html>

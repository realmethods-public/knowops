// globals
var usingUI 		= true;
var usingContainer 	= true;
var usingCache 		= false;
var mvcType 		= "";
var delegateType 	= "";
var appCreationType = "";

function wizard() {
    $('#Wizard_Modal').modal('show');
}

function wizardMigrateorInnovate()
{
 	$('#Wizard_Modal').modal('hide');
    $('#Migrate_Or_Innovate_Modal').modal('show');
}

function migrate()
{
	appCreationType = "migrate";
    $('#Migrate_Or_Innovate_Modal').modal('hide');
    $('#Migrate_Modal').modal('show');
}

function innovate()
{
	appCreationType = "innovate";
    $('#Migrate_Or_Innovate_Modal').modal('hide');
    $('#Innovate_Modal').modal('show');
}

function renovate()
{
	appCreationType = "renovate";
    $('#Migrate_Or_Innovate_Modal').modal('hide');
    $('#Renovate_Modal').modal('show');
}

function migratePojoWizard()
{
	$('#Migrate_Modal').modal('hide');
	showLocalModelModal('jar');
}

function migrateDatabaseWizard()
{
	$('#Migrate_Modal').modal('hide');
	showRevEngModal();
}

function migrateSQLScriptWizard()
{
	$('#Migrate_Modal').modal('hide');
	showLocalModelModal('sql')
}

function innovateJSONWizard()
{
	$('#Innovate_Modal').modal('hide');
	showLocalModelModal('json');
}

function innovateUMLWizard()
{
	$('#Innovate_Modal').modal('hide');
	showLocalModelModal('xmi');
}


function innovateEcoreWizard()
{
	$('#Innovate_Modal').modal('hide');
	showLocalModelModal('ecore');
}

function innovateExistingModelWizard()
{
	$('#Innovate_Modal').modal('hide');
	showSharedModelModal();
}

function showTSPWizard()
{
	$('#TSP_Wizard_Modal').modal('show');
}

function chooseTSPWizard()
{
	$('#TSP_Wizard_Modal').modal('hide');
	factoryTSPSelect();
//	$('#UI_Wizard_Modal').modal('show');
}

function UIWizard( yesNo )
{
	usingUI = yesNo;
	$('#UI_Wizard_Modal').modal('hide');
	$('#MVC_Wizard_Modal').modal('show');
}

function MVCWizard( type )
{
	mvcType = type;
	$('#MVC_Wizard_Modal').modal('hide');
	$('#Delegate_Wizard_Modal').modal('show');
}

function DelegateWizard( type )
{
	delegateType = type;
	$('#Delegate_Wizard_Modal').modal('hide');
	$('#Persistence_Wizard_Modal').modal('show');
}

function PersistenceWizard( type )
{
	persistenceType = type;
	$('#Persistence_Wizard_Modal').modal('hide');
	$('#Cache_Wizard_Modal').modal('show');
}

function CacheWizard( yesNo )
{
	usingCache = yesNo;
	$('#Cache_Wizard_Modal').modal('hide');
	determineTechStackPackage();	
}


function determineTechStackPackage()
{
}

function factory()
{
	$('#Factory_Modal').modal('show');
}

function factoryTSPSelect()
{
/*	var url = 'loadAllPackages.action';
	
	$.ajax(
	{
  		url: url,
  		dataType: 'json',
	}
	).done(function( packages ) 
	{
		var newOption;
		
		$("#package_select_id").empty();
		
		for( i=0; i < packages.length; i++ )		
		{
    		newOption = document.createElement("option");
			newOption.value = packages[i].id;
			newOption.text = packages[i].name;
			document.getElementById("package_select_id").appendChild(newOption);
		}
	});
*/	

	$( "#tspWizardGridId" ).empty();
	$( "#frameworkPackageViewId" ).clone(true,true).appendTo( "#tspWizardGridId" );
	
	$('#Factory_Modal').modal('hide');
	$('#Factory_TSP_Select_Modal').modal('show');
}

function wizardDone()
{
	$('#Factory_TSP_Select_Modal').modal('hide');
	$('#Wizard_Done_Modal').modal('show');
}

function wizardClose()
{
	$('#Wizard_Done_Modal').modal('hide');
	logoSlideDiv.slideReveal("hide", false);
	logoSlideDivWithInstructions.slideReveal("hide", false);
	$('#appCreateNextBttnId').show();
	closeOtherSlideDivs( optionsSlideDiv );
	optionsSlideDiv.slideReveal("show", false);
	
}

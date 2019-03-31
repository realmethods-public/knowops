<script type="text/javascript" src="../js/multiselect.min.js"/>
<script type="text/javascript" src="../js/multiselect.min.js"/>

<script>

	$( document ).ready(function()
	{
		$('#multiselect_from_1').multiselect();
		
		var sourceUrl 	= '<%=request.getParameter( "sourceUrl" )%>';
		var modelUrl 	= '<%=request.getParameter( "modelUrl" )%>';
		var roleName	= '<%=request.getParameter( "roleName" )%>';
		var value		= '<%=request.getParameter( "value" )%>';
		var text		= '<%=request.getParameter( "text" )%>';
		
		jQuery.ajax(
		{
	  		url: sourceUrl,
	  		dataType: 'json',
		}).always(function( fromData ) 
		{
			jQuery.ajax(
			{
		  		url: modelUrl,
		  		dataType: 'json',
			}).always(function( toData ) 
			{
				jQuery.each(fromData.rows, function (fromIndex, fromValue)
				{
					if (typeof fromValue == 'object') 
					{
						foundAMatch = null;
						jQuery.each(toData.rows, function (toIndex, toValue)
						{
							if (typeof toValue == 'object') 
							{
								//console.log( fromValue[value] + ":" + toValue[value] );
								
								if ( fromValue['matchupID'] == toValue['matchupID'] )
								{
									delete fromData[fromIndex];
									return false;
								}
							}
						});
					}
				});


				loadOptionsWithJSONData( 'multiselect_from_1', fromData, value, text, false);
				loadOptionsWithJSONData( 'multiselect_to_1', toData, value, text, false );
									
			});		

		});
	});
</script>

<div style="width:600px">
<center>
<table>
  <tr>
    <td style="width:250px;padding:10px">
        <select style="width:100%;font-size:0.8em" id="multiselect_from_1" name="from[]" class="multiselect form-control" size="8" multiple="multiple" data-right="#multiselect_to_1" data-right-all="#right_All_1" data-right-selected="#right_Selected_1" data-left-all="#left_All_1" data-left-selected="#left_Selected_1">
        </select>
	</td>
	<td style="width:50px;padding:10px">    
        <button type="button" id="right_All_1" class="btn btn-block"><i class="glyphicon glyphicon-forward"></i></button>
        <button type="button" id="right_Selected_1" class="btn btn-block"><i class="glyphicon glyphicon-chevron-right"></i></button>
        <button type="button" id="left_Selected_1" class="btn btn-block"><i class="glyphicon glyphicon-chevron-left"></i></button>
        <button type="button" id="left_All_1" class="btn btn-block"><i class="glyphicon glyphicon-backward"></i></button>
    </td>
    
    <td style="width:250px;padding:10px">
        <select style="width:100%" name="to[]" id="multiselect_to_1" class="form-control" size="8" multiple="multiple"></select>
    </td>
  </tr>
</table>
</div>
</center>


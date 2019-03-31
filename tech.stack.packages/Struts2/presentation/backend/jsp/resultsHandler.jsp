<%@ taglib prefix="s" uri="/struts-tags"%>

<s:if test="hasActionMessages()">
	<div class="errors">
		<span style='font-size:x-small;color:blue'>
  			<s:actionmessage/>
  		</span>
	</div>
</s:if>

<s:if test="hasActionErrors()">
	<div class="errors">
		<span style='font-size:x-small;color:red'>
  			<s:actionerror/>
  		</span>
	</div>
</s:if>

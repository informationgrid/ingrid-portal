<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>

<script type="text/javascript">
_container_.addOnLoad(function() {
	var msgDiv = dojo.byId("messageDiv");
	msgDiv.innerHTML = dojo.string.substituteParams(message.get("dialog.addDescriptors.message"), _container_.customParams.descriptorTitle);

	for (var i = 0; i < _container_.customParams.descriptors.length; ++i) {
		_container_.customParams.descriptors[i].Id = i;
	}
	var table = dojo.widget.byId("descriptorList");
	table.store.setData(_container_.customParams.descriptors);
});

_container_.addOnUnload(function() {
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (_container_.customParams.resultHandler.fired == -1) {
		_container_.customParams.resultHandler.errback();
	}
});


// 'Yes Button' onClick function
yesButtonFunc = function() {
	_container_.customParams.resultHandler.callback();
	_container_.closeWindow();
}

// 'No Button' onClick function
noButtonFunc = function() {
	_container_.customParams.resultHandler.errback();
	_container_.closeWindow();
}

</script>
</head>



<body>
<div dojoType="ContentPane">
	<div id="contentPane" layoutAlign="client" class="contentBlockWhite top">
		<div id="dialogContent" class="content">
			<div id="messageDiv" class="field grey w296">
			</div>

			<div class="grey tableContainer headHiddenRows4 halfInside">
      	    <table id="descriptorList" dojoType="ingrid:FilteringTable" minRows="4" headClass="hidden" cellspacing="0" class="filteringTable nosort readonly">
				<thead>
					<tr>
						<th nosort="true" field="title" dataType="String">Deskriptoren</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			</div>

			<div class="inputContainer grey noSpaceBelow half" style="height:30px;">
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="<fmt:message key="general.no" />" onClick="noButtonFunc"><fmt:message key="general.no" /></button></span>
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="<fmt:message key="general.yes" />" onClick="yesButtonFunc"><fmt:message key="general.yes" /></button></span>
			</div>
	  	</div>
	</div>
</div>
</body>
</html>

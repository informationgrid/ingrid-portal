<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html xmlns="http://www.w3.org/1999/xhtml" lang="de">
<head>

<script type="text/javascript">
var scriptScope = this;

_container_.addOnUnload(function() {
	// If the dialog was cancelled via the dialogs close button
	// we need to signal an error (cancel action)
	if (_container_.customParams.resultHandler.fired == -1) {
		_container_.customParams.resultHandler.errback();
	}
});

scriptScope.selectFile = function() {
	if (_container_.customParams.resultHandler) {
		var file = dwr.util.getValue("selectFileInputField");

		if (file && file.textLength != 0 && file.value != "") {
			_container_.customParams.resultHandler.callback(file);

		} else {
			_container_.customParams.resultHandler.errback();
		}
	}
	
	_container_.closeWindow(); 
}

</script>
</head>

<body>

<div dojoType="ContentPane">

	<div class="contentBlockWhite top">
		<div id="winNavi">
			<a href="javascript:void(0);" onclick="javascript:dialog.showContextHelp(arguments[0], 8090)" title="Hilfe">[?]</a>
		</div>
		<div class="content">

			<!-- LEFT HAND SIDE CONTENT START -->
			<div class="spacer"></div>
			<div class="spacer"></div>
			<div class="inputContainer field grey noSpaceBelow w550">
				<span class="label"><label for="selectFileInputField" onclick="javascript:dialog.showContextHelp(arguments[0], 8060, '<fmt:message key="dialog.file.selectFile" />')"><fmt:message key="dialog.file.selectFile" /></label></span>
					<span>
						<input type="file" id="selectFileInputField" size="80" />
					</span>
				<div class="spacerField"></div>
			</div>

			<div class="grey noSpaceBelow" style="height:30px;">
		        <span style="float:right; margin-top:5px;"><button dojoType="ingrid:Button" title="<fmt:message key="dialog.file.select" />" onClick="javascript:scriptScope.selectFile();"><fmt:message key="dialog.file.select" /></button></span>
			</div>
			<!-- LEFT HAND SIDE CONTENT END -->
		</div>
	</div>
</div>

</body>
</html>

function displayErrorMessage(err) {
    // Show errors depending on outcome
    if (err && err.message) {

        // In case an exception is wrappen inside another exception (dwr exception isn't of type Error)
        while(typeof(err.message) == "object") {
            dojo.debug("Error message is obj. Extracting...");
            err = err.message;
            dojo.debug("err.message: "+err.message);
        }

        if (err.message.indexOf("OPERATION_CANCELLED") != -1) {
            return;

        } else if (err.message.indexOf("LOAD_CANCELLED") != -1) {
            return;

        } else if (err.message.indexOf("ENTITY_REFERENCED_BY_OBJ") != -1) {
            handleEntityReferencedException(err);

        } else if (err.message.indexOf("ADDRESS_IS_AUSKUNFT") != -1) {
            handleEntityReferencedException(err);

        } else if (err.message.indexOf("ADDRESS_HAS_NO_EMAIL") != -1) {
            dialog.show(message.get("general.error"), message.get("operation.error.addressHasNoEmail"), dialog.WARNING);

        } else if (err.message.indexOf("USER_HAS_NO_PERMISSION") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.noPermissionError"), dialog.WARNING);

        } else if (err.message.indexOf("INVALID_REQUIRED_BUT_EMPTY") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
        
        } else if (err.message.indexOf("INVALID_INPUT_INVALID") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
        
        } else if (err.message.indexOf("INVALID_INPUT_OUT_OF_RANGE") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.inputInvalidError"), dialog.WARNING);
        
        } else if (err.message.indexOf("INVALID_INPUT_HTML_TAG_INVALID") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.inputInvalidHtmlTagError"), dialog.WARNING);
        
        } else if (err.message.indexOf("ERROR_GETCAP_ERROR") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.getcap.error"), dialog.WARNING);

        } else if (err.message.indexOf("ERROR_GETCAP_XPATH") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.getcap.xpathError"), dialog.WARNING);

        } else if (err.message.indexOf("ERROR_GETCAP_INVALID_URL") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.getcap.invalidUrlError"), dialog.WARNING);

        } else if (err.message.indexOf("HQL_NOT_VALID") != -1) {
            dialog.show(message.get("general.error"), message.get("dialog.hqlQueryInvalidError"), dialog.WARNING);

        } else if (err.message.indexOf("ADDRESS_IS_IDCUSER_ADDRESS") != -1) {
            dialog.show(message.get("general.error"), message.get("operation.error.deletedAddressIsIdcUser"), dialog.WARNING);

        } else if (err.message.indexOf("ADDRESS_TYPE_CONFLICT") != -1) {
            dialog.show(message.get("general.error"), message.get("operation.error.addressTypeConflict"), dialog.WARNING);

        } else if (err.message.indexOf("PARENT_NOT_PUBLISHED") != -1) {
            if (currentUdk.nodeAppType == "O")
                dialog.show(message.get("general.error"), message.get("operation.error.object.parentNotPublishedError"), dialog.WARNING);
            else
                dialog.show(message.get("general.error"), message.get("operation.error.address.parentNotPublishedError"), dialog.WARNING);
            
        } else if (err.message.indexOf("TARGET_IS_SUBNODE_OF_SOURCE") != -1) {
            dialog.show(message.get("general.error"), message.get("operation.error.targetIsSubnodeOfSourceError"), dialog.WARNING);
        
        } else if (err.message.indexOf("SUBTREE_HAS_WORKING_COPIES") != -1) {
            if (err.nodeAppType == "O")
                dialog.show(message.get("general.error"), message.get("operation.error.object.subTreeHasWorkingCopiesError"), dialog.WARNING);
            else
                dialog.show(message.get("general.error"), message.get("operation.error.address.subTreeHasWorkingCopiesError"), dialog.WARNING);

        } else if (err.message.indexOf("PARENT_HAS_SMALLER_PUBLICATION_CONDITION") != -1) {
            dialog.show(message.get("general.error"), message.get("operation.error.parentHasSmallerPublicationConditionError"), dialog.WARNING);
        
        } else if (err.message.indexOf("USER_LOGIN_ERROR") != -1) {
            //dialog.show(message.get("general.error"), message.get("dialog.sessionTimeoutError"), dialog.WARNING);
            window.onbeforeunload = function(evt) {};
            document.location.href = "session_expired.jsp"
        } else {
            dialog.show(message.get("general.error"), dojo.string.substituteParams(message.get("dialog.generalError"), err.message), dialog.WARNING, null, 350, 350);               
        }
    } else {
        // Show error message if we can't determine what went wrong
        dialog.show(message.get("general.error"), message.get("dialog.undefinedError"), dialog.WARNING);
    }
}

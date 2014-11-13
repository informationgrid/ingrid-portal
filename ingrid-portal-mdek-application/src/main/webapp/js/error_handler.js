/*
 * **************************************************-
 * Ingrid Portal MDEK Application
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
function displayErrorMessage(err) {
    console.error(err);
    require([
        "dojo/_base/array",
        "dojo/string",
        "ingrid/dialog",
        "ingrid/message",
        "ingrid/utils/Exceptions"
    ], function(array, string, dialog, message, Exceptions) {
        
        
        if (!(err instanceof Object)) {
            err = {message:err};
        }
        // Show errors depending on outcome
        if (err && err.message) {
            
            // In case an exception is wrappen inside another exception (dwr exception isn't of type Error)
            while(typeof(err.message) == "object") {
                dojo.debug("Error message is obj. Extracting...");
                err = err.message;
                dojo.debug("err.message: "+err.message);
            }
            
            var extraInfo = "";
            if (err.cause && err.cause.message) extraInfo = "<br /><i>(" + err.cause.message + ")</i>";
            
            if (err.message.indexOf("OPERATION_CANCELLED") != -1) {
                return;
    
            } else if (err.message.indexOf("LOAD_CANCELLED") != -1) {
                return;
    
            } else if (err.message.indexOf("ENTITY_REFERENCED_BY_OBJ") != -1) {
                Exceptions.handleEntityReferencedException(err);
    
            } else if (err.message.indexOf("ADDRESS_IS_VERWALTER") != -1) {
                Exceptions.handleEntityReferencedException(err);
    
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
                dialog.show(message.get("general.error"), message.get("dialog.getcap.error") + extraInfo, dialog.WARNING);
                
            } else if (err.message.indexOf("ERROR_GETCAP_XPATH") != -1) {
                dialog.show(message.get("general.error"), message.get("dialog.getcap.xpathError") + extraInfo, dialog.WARNING);
    
            } else if (err.message.indexOf("ERROR_GETCAP_INVALID_URL") != -1) {
                dialog.show(message.get("general.error"), message.get("dialog.getcap.invalidUrlError") + extraInfo, dialog.WARNING);
    
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
                if (message.nodeAppType == "O")
                    dialog.show(message.get("general.error"), message.get("operation.error.object.subTreeHasWorkingCopiesError"), dialog.WARNING);
                else
                    dialog.show(message.get("general.error"), message.get("operation.error.address.subTreeHasWorkingCopiesError"), dialog.WARNING);
    
            } else if (err.message.indexOf("PARENT_HAS_SMALLER_PUBLICATION_CONDITION") != -1) {
                dialog.show(message.get("general.error"), message.get("operation.error.parentHasSmallerPublicationConditionError"), dialog.WARNING);
            
            } else if (err.message.indexOf("USER_LOGIN_ERROR") != -1) {
                eventWindowUnload.remove();
                document.location.href = "session_expired.jsp";
            } else if (err.message.indexOf("REFERENCED_ADDRESSES_NOT_PUBLISHED") != -1) {
                Exceptions.handleAddressNeverPublishedException(err);
            } else if (err.message.indexOf("REFERENCING_OBJECTS_HAVE_LARGER_PUBLICATION_CONDITION") != -1) {
                var objectList = formatObjectsFromList(err.referencedConflictingObjects);
                dialog.show(message.get("general.error"), string.substitute(message.get("operation.error.address.referencingObjectsHaveLargerPublicationCondition"), [objectList]), dialog.WARNING);
            } else if (err.message.indexOf("REFERENCED_ADDRESSES_HAVE_SMALLER_PUBLICATION_CONDITION") != -1) {
                var addressList = formatAddressesFromList(err.referencedConflictingAddresses);
                dialog.show(message.get("general.error"), string.substitute(message.get("operation.error.address.referencedAddressesHaveSmallerPublicationCondition"), [addressList]), dialog.WARNING);
            } else {
                dialog.show(message.get("general.error"), string.substitute(message.get("dialog.generalError"), [err.message]), dialog.WARNING, null, 800, 350);
            }
        } else {
            // Show error message if we can't determine what went wrong
            var text = err;
            if (err instanceof Object) {
                text = err.javaClassName + ": " + printStackTrace( err );
            }
            dialog.show(message.get("general.error"), message.get("dialog.undefinedError") + ": " + text, dialog.WARNING);
        }
    });
}

function printStackTrace(error) {
    var stack = "";
    
    if (error.stackTrace.length > 0) {
        stack = "<br /><br />STACKTRACE<br />";
    }
    
    array.forEach(error.stackTrace, function(stackPart) {
        stack += "\tclassName: " + stackPart.className +"<br />";
        stack += "\tfileName: " + stackPart.fileName +"<br />";
        stack += "\tlineNumber: " + stackPart.lineNumber +"<br />";
        stack += "\tmethodName: " + stackPart.methodName +"<br />";
        stack += "---";
    });
    
    return stack;
}

function formatObjectsFromList(objects) {
    var result = "<ul>";
    require("dojo/_base/array").forEach(objects, function(object) {
        var pubName = require("ingrid/utils/Syslist").getSyslistEntryName(3571, object.extraInfoPublishArea);
        result += "<li>"+object.title+" ("+pubName+")</li>";
    });
    result += "</ul>";
    return result;
}

function formatAddressesFromList(addresses) {
    var result = "<ul>";
    require("dojo/_base/array").forEach(addresses, function(address) {
        var pubName = require("ingrid/utils/Syslist").getSyslistEntryName(3571, address.extraInfoPublishArea);
        result += "<li>"+require("ingrid/utils/Address").createAddressTitle(address)+" ("+pubName+")</li>";
    });
    result += "</ul>";
    return result;
}

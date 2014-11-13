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
define([
    "dojo/_base/declare",
    "dojo/_base/lang",
    "dojo/on",
    "dojo/dom",
    "dojo/Deferred",
    "dojo/cookie",
    "ingrid/message",
    "ingrid/dialog"
], function(declare, lang, on, dom, Deferred, cookie, message, dialog) {
	
    return declare(null, {

		sessionExpired: false,

        // Returns the stack trace of an exception as string
        getStackTrace: function(exception) {
            if (exception.cause) {
                exception = exception.cause;
            }
            var stackTrace = "";
            stackTrace += exception.javaClassName + ": " + exception.message + "<br>";
            for (var i = 0; i < exception.stackTrace.length; ++i) {
                var ex = exception.stackTrace[i];
                if (!ex.nativeMethod) {
                    stackTrace += "&nbsp;&nbsp;at " + ex.className + "." + ex.methodName + "(" + ex.fileName + ":" + ex.lineNumber + ")" + "<br>";
                } else {
                    stackTrace += "&nbsp;&nbsp;at " + ex.className + "." + ex.methodName + "(Native Method)" + "<br>";
                }
            }
            return stackTrace;
        },

        refreshSession: function() {
            UtilityService.refreshSession();
        },

        sessionValid: function() {
            // check periodically if session is still alive
            var self = this;
            UtilityService.sessionValid({
                callback: function(valid) {
                    console.debug("session valid: " + valid);
                    if (!valid) {
                        self.showSessionTimoutMessage();
                    } else {
                        // check at a later expected timeout time again
                        setTimeout(self.sessionValid, (self.sessionTimeoutInterval + 10) * 1000);
                    }
                    if (cookie("JSESSIONID") != self.initialJSessionId) {
                        console.debug("Session ID has changed! Everything still okay?");
                        this.sessionExpired = true;
                    }
                },
                errorHandler: function(err) {
                    console.error("error: " + err);
                    if (cookie("JSESSIONID") != self.initialJSessionId) {
                        console.debug("Session ID has changed! Everything still okay?");
                        this.sessionExpired = true;
                    }
                    // an error occured which might be because of a session problem
                    // check again in a minute
                    self.showSessionTimoutMessage();
                }
            });
        },

        showSessionTimoutMessage: function() {
            var dlg = dialog.show(message.get("dialog.sessionTimeoutErrorTitle"), message.get("dialog.sessionTimeoutError"), dialog.INFO, [{
                caption: message.get("general.ok"),
                action: function() {
                    this.sessionExpired = true;
                    document.location.href = "session_expired.jsp";
                }
            }]);
            on(dlg, "Hide", function() {
                this.sessionExpired = true;
                document.location.href = "session_expired.jsp";
            });
        },

        generateRandomString: function(strLength) {
            var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
            var string_length = strLength;
            var str = '';
            for (var i = 0; i < string_length; i++) {
                var rnum = Math.floor(Math.random() * chars.length);
                str += chars.substring(rnum, rnum + 1);
            }
            return str;
        },

        generateUUID: function() {
            var def = new Deferred();

            UtilityService.getRandomUUID({
                callback: function(res) {
                    def.resolve(res);
                }
            });

            return def;
        },

        askUserAndInvokeOrCancel: function(text, invocation) {
            dialog.show(message.get('general.hint'), text, dialog.INFO, [{
                caption: message.get('general.no'),
                action: function() {}
            }, {
                caption: message.get('general.yes'),
                action: function() {
                    invocation.call();
                }
            }]);
        },

        getNumberFromDijit: function(id) {
            var value = dijit.byId(id).get("value");
            return isNaN(value) ? null : value;
        },

        //check passed value: returns false if undefined or null or "" or "    " or NaN;
        hasValue: function(val) {
            if (typeof val == "undefined") {
                return false;
            } else if (val === null) {
                return false;
            } else if (lang.trim(val + "") === "") {
                return false;
            } else if ((val + "") == (Number.NaN + "")) {
                return false;
            }

            return true;
        },

        printDivContent: function(divId, /*optional*/ frame, /*optional*/ divElement) {
            if (!frame) {
                frame = parent.printFrame;
            }
            if (!divId)
                frame.document.body.innerHTML = divElement.innerHTML;
            else
                frame.document.body.innerHTML = dom.byId(divId).innerHTML;

            frame.focus();
            frame.print();
        }

    })();
});
define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/_base/array',
    'dojo/request/xhr',
    'dojo/Deferred',
    'dojo/json',
    'dojo/string',
    'dojo/on',
    'dojo/aspect',
    'dojo/query',
    'dojo/dom',
    'dojo/dom-construct',
    'dojo/dom-style',
    'dojo/dom-class',
    'dijit/_WidgetBase',
    'dijit/ConfirmDialog',
    'dijit/form/Button',
    'ingrid/dialog',
    './fine-uploader/fine-uploader',
    'dojo/text!./template/UploadWidget.html',
    'dojo/text!./template/UploadWidget.css'
], function(
    declare,
    lang,
    array,
    xhr,
    Deferred,
    json,
    string,
    on,
    aspect,
    query,
    dom,
    domConstruct,
    domStyle,
    domClass,
    _WidgetBase,
    Dialog,
    Button,
    IngridDialog,
    qq,
    template,
    styles
) {
    return declare([_WidgetBase], {

        uploadUrl: "",

        uploader: null,
        dialog: null,
        deferred: null,

        styleEl: null,
        templateEl: null,
        btnHandles: [],
        uploadBtns: {
            retry: {},
            replace: {},
            rename: {},
            keep: {}
        },
        uploadErrors: {},

        resultParts: {},
        uploads: [],

        postCreate: function() {
            this.inherited(arguments);

            // dialog
            // TODO: should be created when upload link was clicked
            this.dialog = new Dialog({
                title: "Dokument-Upload",
                style: "width: 840px",
                execute: lang.hitch(this, function(cancelEvent) {
                    // resolve the deferred created in open method
                    // with the uploaded documents
                    var uploads = this.removeDuplicates(this.uploads);
                    this.deferred.resolve(uploads);
                }),
                onHide: lang.hitch(this, function(cancelEvent) {
                    // empty the upload list
                    this.uploads = [];
                    this.close();
                })
            });
            this.dialog.set("buttonOk", "Übernehmen");
            this.setOkButtonState(false);

            // override dialog.hide to allow to ask for confirmation, if the user
            // attempts to cancel the dialog with already finished uploads
            this.dialog.origHide = this.dialog.hide;
            this.dialog.hide = lang.hitch(this, function(cancelEvent) {
                // cancel event only exists, if the cancel button was clicked
                if (cancelEvent && this.uploads.length > 0) {
                    var message = string.substitute("Abgeschlossene Dokument-Uploads werden nicht übernommen und vom Server gelöscht!<br><br>Wollen Sie das Fenster wirklich schließen ohne die Dokument-Uploads zu übernehmen?");
                    IngridDialog.show("Ungespeicherte Dokument-Uploads", message, IngridDialog.INFO, [{
                        caption: "Ja",
                        action: lang.hitch(this, function() {
                            // delete documents from server?
                            var uploads = this.removeDuplicates(this.uploads);
                            array.forEach(uploads, function (upload) {
                                xhr.del(upload.uri);
                            }, this);
                            this.dialog.origHide();
                        })
                    }, {
                        caption: "Nein",
                        action: IngridDialog.CLOSE_ACTION
                    }]);
                }
                else {
                    this.dialog.origHide();
                }
            });
            this.dialog.startup();
        },

        /**
         * Set the enabled status of the OK button
         * @param isEnabled
         */
        setOkButtonState: function(isEnabled) {
            this.dialog.okButton.set("disabled", !isEnabled);
        },

        /**
         * Set the visibility of a button
         * @param button
         * @param isVisible
         */
        setButtonVisibility: function(button, isVisible) {
            domStyle.set(button, "display", isVisible ? "inline" : "none");
        },

        /**
         * Initialize a button for a file upload
         * @param uploadId
         * @param type
         * @param callback A function receiving the upload id and event when the button is clicked (optional)
         */
        initializeButton: function(uploadId, type, callback) {
            var fileEl = this.getFileEl(uploadId);
            if (fileEl) {
                var buttons = query(".qq-upload-"+type+"-selector", fileEl);
                if (buttons.length > 0) {
                    var button = buttons[0];
                    this.uploadBtns[type][uploadId] = button;
                    this.setButtonVisibility(button, false);
                    if (callback instanceof Function) {
                        this.btnHandles.push(on(button, "click", lang.partial(lang.hitch(this, callback), uploadId)));
                    }
                }
            }
        },
        
        /**
         * Get the container dom element for a file upload
         * @param uploadId
         */
        getFileEl: function(uploadId) {
            var elements = query(".qq-file-id-"+uploadId);
            if (elements.length > 0) {
                return elements[0];
            }
            return null;
        },

        /**
         * Show the upload dialog
         * @param path The upload path
         * @return Deferred that resolves to an array of upload items with properties uri, type, size
         */
        open: function(path) {
            this.deferred = new Deferred();

            // uploader styles
            this.styleEl = domConstruct.create("style", {
                innerHTML: styles.replace(/url\("/g, 'url("../fine-uploader/')
            });
            query("head").forEach(lang.hitch(this, function(node) {
                domConstruct.place(this.styleEl, node, "last");
            }));

            // uploader template
            this.templateEl = domConstruct.create("div", { innerHTML: template });

            // uploader
            this.uploader = new qq.FineUploader({
                debug: false,
                element: this.dialog.containerNode,
                template: this.templateEl,
                autoUpload: true,
                text: {
                    defaultResponseError: "Upload fehlgeschlagen",
                    formatProgress: "{percent}% von {total_size}",
                    failUpload: "Upload fehlgeschlagen",
                    waitingForResponse: "Wird bearbeitet...",
                    paused: "Pausiert"
                },
                messages: {
                    tooManyFilesError: "Sie können nur eine Datei einfügen",
                    unsupportedBrowser: "Fehler - Ihr Browser unterstützt keine Art von Dateiupload"
                },
                failedUploadTextDisplay: {
                    mode: 'custom',
                    responseProperty: 'error'
                },
                request: {
                    endpoint: this.uploadUrl,
                    filenameParam: "filename",
                    inputName: "file",
                    uuidName: "id",
                    totalFileSizeName: "size",
                    params: this.getUploadParams(path, false)
                },
                retry: {
                    enableAuto: false,
                    autoRetryNote: "Wiederhole {retryNum}/{maxAuto}..."
                },
                chunking: {
                    enabled: true,
                    concurrent: {
                        // when concurrency is enabled, onError will be called with
                        // an xhr with status 0 which makes correct error handling impossible
                        enabled: false
                    },
                    paramNames: {
                        chunkSize: "parts_size",
                        partByteOffset: "parts_offset",
                        partIndex: "parts_index",
                        totalParts: "parts_total"
                    },
                    success: {
                        endpoint: this.uploadUrl
                    }
                },
                callbacks: {
                    onSubmitted: lang.hitch(this, function(id, name) {
                        // set event handlers for buttons
                        this.initializeButton(id, 'retry');
                        this.initializeButton(id, 'replace', function(id, e) {
                            // retry with replace parameter set to true
                            this.uploader.setName(id, name);
                            this.uploader.setParams(this.getUploadParams(path, true), id);
                            this.uploader.retry(id);
                        });
                        this.initializeButton(id, 'rename', function(id, e) {
                            var data = this.uploadErrors[id] || {};
                            if (data.alt) {
                                // retry with new name replace parameter set to false
                                this.uploader.setName(id, data.alt);
                                this.uploader.setParams(this.getUploadParams(path, false), id);
                                this.uploader.retry(id);
                            }
                        });
                        this.initializeButton(id, 'keep', function(id, e) {
                            var data = this.uploadErrors[id] || {};
                            if (data.file) {
                                // finish the upload
                                this.finishUpload(id, data.file);
                            }
                        });
                    }),
                    onUpload: lang.hitch(this, function(id, name) {
                        this.setOkButtonState(false);
                    }),
                    onComplete: lang.hitch(this, function(id, name, responseJSON, xhrOrXdr) {
                        if (responseJSON.success) {
                            // hide buttons
                            for (type in this.uploadBtns) {
                                this.setButtonVisibility(this.uploadBtns[type][id], false);
                            }
                            // collect files from response by id
                            this.resultParts[id] = responseJSON.files;
                        }
                        else {
                            // set error message according to response status
                            var status = xhrOrXdr ? xhrOrXdr.status : "default";
                            var messages = {
                                401: "Sie haben keine Berechtigung für den Upload. Eventuell ist die Session abgelaufen.",
                                409: "Die Datei existiert bereits.",
                                "default": "Beim Upload ist ein Fehler aufgetreten." 
                            };
                            var message = messages[status] ? messages[status] : messages["default"];
                            var fileEl = this.getFileEl(id);
                            if (fileEl) {
                                query(".qq-upload-status-text-selector", fileEl).forEach(function(node) {
                                    node.innerHTML = message;
                                });
                            }
                        }
                    }),
                    onAllComplete: lang.hitch(this, function(succeeded, failed) {
                        // combine succeeded uploads into result
                        array.forEach(succeeded, function(id) {
                            this.uploads = this.uploads.concat(this.resultParts[id]);
                        }, this);
                        this.setOkButtonState(true);
                    }),
                    onError: lang.hitch(this, function(id, name, errorReason, xhrOrXdr) {
                        // store error data for later use
                        var errorData = {};
                        try {
                            errorData = json.parse(xhrOrXdr.response).errorData;
                        }
                        catch (err) {}
                        this.uploadErrors[id] = errorData;

                        // set button states according to response status
                        var status = xhrOrXdr ? xhrOrXdr.status : "default";
                        var buttonStates = {
                            409: {
                                "retry": false,
                                "replace": true,
                                "rename": true,
                                "keep": true
                            },
                            "default": {
                                "retry": true,
                                "replace": false,
                                "rename": false,
                                "keep": false
                            } 
                        };
                        var buttonState = buttonStates[status] ? buttonStates[status] : buttonStates["default"];
                        for (button in buttonState) {
                            this.setButtonVisibility(this.uploadBtns[button][id], buttonState[button]);
                        }
                    })
                },
                showMessage: function(message) {
                    IngridDialog.show("Info", message, IngridDialog.INFO);
                }
            });

            // show uploader
            this.dialog.show();

            return this.deferred;
        },

        /**
         * Called, when the uploader window is closed
         */
        close: function() {
            // cleanup all resources that were created in the open method
            if (this.styleEl) {
                domConstruct.destroy(this.styleEl);
            }
            if (this.templateEl) {
                domConstruct.destroy(this.templateEl);
            }

            for (var i=0, count=this.btnHandles.length; i<count; i++) {
                this.btnHandles[i].remove();
            }

            this.uploader.reset();
            delete this.uploader;
        },

        /**
         * Called, when the widget is destroyed
         */
        destroy: function() {
            // cleanup all resources that were created in the post create method
            this.dialog.destroy();
            this.inherited(arguments);
        },

        /**
         * Get parameters for uploader
         * @param path The path for uploads
         * @param replace Boolean whether to replace existing files or not
         * @return Object
         */
        getUploadParams: function(path, replace) {
            return {
                path: path,
                replace: replace
            };
        },

        /**
         * Finish the given upload adding the data to the successful uploads
         * @param id The id of the upload
         * @param data The file data, object with properties size, type, uri
         */
        finishUpload: function(id, data) {
            var fileEl = this.getFileEl(id);
            if (fileEl) {
                domClass.add(fileEl, "qq-upload-success");
                domClass.remove(fileEl, "qq-upload-fail");
                query(".qq-upload-status-text-selector,.qq-btn", fileEl).forEach(function(node) {
                    domStyle.set(node, "display", "none");
                });
                
            }
            this.uploads.push(data);
        },

        /**
         * Remove duplicates from the upload list
         * @param uploads Array of uploads
         * @return Array
         */
        removeDuplicates: function(uploads) {
            var lookup = {};
            return array.filter(uploads, function(upload) {
                if(lookup[upload.uri] !== true) {
                    lookup[upload.uri] = true;
                    return true;
                }
                return false;
            });
        }
    });
});

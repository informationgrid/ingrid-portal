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
        uploadHandle: null,

        resultParts: {},
        uploads: [],

        postCreate: function() {
            this.inherited(arguments);

            // dialog
            // TODO: should be created when upload link was clicked
            this.dialog = new Dialog({
                // id: "uploadDialog",
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
            this.setOkEnabled(false);

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
        setOkEnabled: function(isEnabled) {
            this.dialog.okButton.set("disabled", !isEnabled);
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
                    autoAttemptDelay: 5,
                    maxAutoAttempts: 3,
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
                    onUpload: lang.hitch(this, function(id, name) {
                        this.setOkEnabled(false);
                    }),
                    onComplete: lang.hitch(this, function(id, name, responseJSON) {
                        // collect files from response by id
                        this.resultParts[id] = responseJSON.files;
                    }),
                    onAllComplete: lang.hitch(this, function(succeeded, failed) {
                        // combine succeeded uploads into result
                        array.forEach(succeeded, function(id) {
                            this.uploads = this.uploads.concat(this.resultParts[id]);
                        }, this);
                        this.setOkEnabled(true);
                    }),
                    onError: lang.hitch(this, function(id, name, errorReason, xhrOrXdr) {
                        var message = null;
                        if (xhrOrXdr && xhrOrXdr.status == 409) {
                            this.uploader.pauseUpload(id);
                            // TODO add file name input field?
                            message = string.substitute("Die Datei '${0}' existiert bereits. Soll die existierende Datei überschrieben werden?", [name]);
                            IngridDialog.show("Upload Konflikt", message, IngridDialog.INFO, [{
                                caption: "Ja",
                                action: lang.hitch(this, function() {
                                    // retry with new name replace parameter set to true
                                    this.uploader.setName(id, name);
                                    this.uploader.setParams(this.getUploadParams(path, true), id);
                                    this.uploader.retry(id);
                                })
                            }, {
                                caption: "Nein",
                                action: IngridDialog.CLOSE_ACTION
                            }]);
                        }
                        else {
                            message = name ? string.substitute("Fehler beim Upload von '${0}': ${1}", [name, errorReason]) : errorReason;
                            IngridDialog.show("Fehler", message, IngridDialog.WARNING);
                        }
                    })
                },
                showMessage: function(message) {
                    IngridDialog.show("Info", message, IngridDialog.INFO);
                }
            });

            // add upload button handler
            var uploadBtns = query(".qq-upload-button-upload", this.dialog.containerNode);
            if (uploadBtns.length > 0) {
                this.uploadHandle = on(uploadBtns[0], "click", lang.hitch(this, function() {
                    this.uploader.uploadStoredFiles();
                }));
            }

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

            if (this.uploadHandle) {
                this.uploadHandle.remove();
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

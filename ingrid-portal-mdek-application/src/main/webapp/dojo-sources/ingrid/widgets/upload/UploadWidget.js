define([
    'dojo/_base/declare',
    'dojo/_base/lang',
    'dojo/_base/array',
    'dojo/Deferred',
    'dojo/on',
    'dojo/query',
    'dojo/dom',
    'dojo/dom-construct',
    'dijit/_WidgetBase',
    'dijit/Dialog',
    './fine-uploader/fine-uploader',
    'dojo/text!./template/UploadWidget.html',
    'dojo/text!./template/UploadWidget.css'
], function(
    declare,
    lang,
    array,
    Deferred,
    on,
    query,
    dom,
    domConstruct,
    _WidgetBase,
    Dialog,
    qq,
    template,
    styles
) {
    return declare([_WidgetBase], {

        uploadUrl: "",
        path: "",

        uploader: null,
        dialog: null,
        deferred: null,

        styleEl: null,
        templateEl: null,
        clickHandles: [],

        resultParts: {},

        documents: [],

        postCreate: function() {
            this.inherited(arguments);

            // dialog
            this.dialog = new Dialog({
                title: "Dokument-Upload",
                style: "width: 840px",
                onHide: lang.hitch(this, function() {
                    this.close();
                })
            });
            this.dialog.startup();
        },

        open: function() {
            this.deferred = new Deferred();

            // uploader styles
            this.styleEl = domConstruct.create("style", { innerHTML: styles.replace(/url\("/g, 'url("../fine-uploader/') });
            query("head").forEach(lang.hitch(this, function(node) {
                domConstruct.place(this.styleEl, node, "last");
            }));

            // uploader template
            this.templateEl = domConstruct.create("div", { innerHTML: template });

            // uploader
            this.uploader = new qq.FineUploader({
                debug: true,
                element: this.dialog.containerNode,
                template: this.templateEl,
                autoUpload: false,
                request: {
                    endpoint: this.uploadUrl,
                    filenameParam: "filename",
                    inputName: "file",
                    uuidName: "id",
                    totalFileSizeName: "size",
                    params: {
                        path: this.path,
                        replace: true
                    }
                },
                retry: {
                    enableAuto: true,
                    autoAttemptDelay: 5,
                    maxAutoAttempts: 10
                },
                chunking: {
                    enabled: true,
                    paramNames: {
                        chunkSize: "parts_size",
                        partByteOffset: "parts_offset",
                        partIndex: "parts_index",
                        totalParts: "parts_total"
                    },
                    success: {
                        endpoint: this.uploadUrl+"/done"
                    }
                },
                callbacks: {
                    onComplete: lang.hitch(this, function(id, name, responseJSON) {
                        // collect response files by id
                        this.resultParts[id] = responseJSON.files;
                    }),
                    onAllComplete: lang.hitch(this, function(succeeded, failed) {
                        // combine succeeded files into result
                        array.forEach(succeeded, function(id) {
                            this.documents = this.documents.concat(this.resultParts[id]);
                        }, this);
                    })
                }
            });
            this.clickHandles = [
                on(dom.byId("trigger-upload"), "click", lang.hitch(this, function() {
                    this.uploader.uploadStoredFiles();
                })),
                on(dom.byId("trigger-finish"), "click", lang.hitch(this, function() {
                    this.deferred.resolve(this.documents);
                    this.documents = [];
                    this.dialog.hide();
                }))
            ];
            this.dialog.show();

            return this.deferred;
        },

        close: function() {
            domConstruct.destroy(this.styleEl);
            array.forEach(this.clickHandles, function(handle) {
                handle.remove();
            });
        }
    });
});

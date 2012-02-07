function ingrid_openWindow(url, winWidth, winHeight)
{
  popupWin = window.open(url, "InternalWin", 'width=' + winWidth + ',height=' + winHeight + ',resizable=yes,scrollbars=yes,location=no,toolbar=yes');
  popupWin.focus();
}

function ingrid_checkAll(group) {
  // NOTICE: first field in group has to be "checkAll" field
  if (group[0]) {
      if (group[0].checked == true) {
          for (i=1; i<group.length; i++) {
              group[i].checked = true;
          }
      }	else {
          for (i=1; i<group.length; i++) {
              group[i].checked = false;
          }
      }
  }
}

// Select all or nothing in group1 and force group2 to same selection state.
function ingrid_checkAll2Groups(group1, group2) {
    group2[0].checked = group1[0].checked;
    ingrid_checkAll(group1);
    ingrid_checkAll(group2);
}

//Select all or nothing in group1 and adapt only "all field" in group2.
function ingrid_checkAllAdapt(group1, group2) {
    ingrid_checkAll(group1);
    if (group1[0].checked == false) {
        group2[0].checked = false;
    } else {
        ingrid_checkGroup(group2);
    }
}

function ingrid_checkGroup(group) {
    // NOTICE: first field in group has to be "checkAll" field
    var allChecked = true;
    for (i=1; i<group.length; i++) {
        if (group[i].checked != true) {
            allChecked = false;
            break;
        }
    }
    if (allChecked) {
        group[0].checked = true;
    } else {
        group[0].checked = false;       
    }
}

//Check for selection of all field in group1 and then adapt group 2.
function ingrid_checkGroupAdapt(group1, group2) {
    ingrid_checkGroup(group1);
    if (group1[0].checked == false) {
        group2[0].checked = false;
    } else {
        ingrid_checkGroup(group2);
    }
}

function login() {
	if (document.getElementById("login").value == "admin") {
    	document.location.href="mpu_admin.html";
    } else if (document.getElementById("passwd").value != "") {
      	document.location.href="mpu_home.html";
    }
}

function clearUser() {
    document.getElementById("login").value = "";
}

function clearPasswd() {
    document.getElementById("passwd").value = "";
}


function adaptProviderNodes(partnerElementName, formName) {
  var partnerIdent = document.forms[formName].elements[partnerElementName].id;
  if (partnerIdent == "bund") {
    partnerIdent = "bu";
  }
  var checked = document.forms[formName].elements[partnerElementName].checked;
  for (i=0; i<document.forms[formName].elements.length; i++) {
    if (document.forms[formName].elements[i].id.indexOf(partnerIdent+'_') == 0) {
      document.forms[formName].elements[i].checked = checked;
    }
  }
}

function adaptPartnerNode(providerElementName, formName) {
  var partnerIdent = document.forms[formName].elements[providerElementName].id;
  partnerIdent = partnerIdent.substring(0, partnerIdent.indexOf('_'));
  var checked = document.forms[formName].elements[providerElementName].checked;
  var checkPartner = false;
  if (!checked) {
	  for (i=0; i<document.forms[formName].elements.length; i++) {
	    if (document.forms[formName].elements[i].id.indexOf(partnerIdent+'_') == 0) {
	      if (document.forms[formName].elements[i].checked) {
	      	checkPartner = true;
	      	break;
	      }
	    }
	  }
  } else {
      checkPartner = true;
  }
  if (partnerIdent == "bu") {
    partnerIdent = "bund";
  }
  document.forms[formName].elements["chk_"+partnerIdent].checked = checkPartner;
}

/* Open and close categories */
function openNode(element){
	var status = document.getElementById(element).style.display;
	var status_img = document.getElementById(element + "_img").src;
	
	document.getElementById(element).style.display = 'none';
	if(status == "none"){
		document.getElementById(element).style.display = "block";
	}
	
	document.getElementById(element + "_img").src = '/ingrid-portal-apps/images/facete/facete_cat_close.png';
	if(status_img.indexOf('/ingrid-portal-apps/images/facete/facete_cat_close.png') != -1){
		document.getElementById(element + "_img").src = '/ingrid-portal-apps/images/facete/facete_cat_open.png';
	}	
	
}

/* Open and close facete */
function openFaceteNode(element, id_facete, id_hits){
	var status_img = document.getElementById(element + "_img").src;
	
	if(status_img.indexOf('/ingrid-portal-apps/images/facete/facete_close.png') > -1){
		document.getElementById(element).style.display = "block";
		document.getElementById(element + "_img").src = '/ingrid-portal-apps/images/facete/facete_open.png';
		document.getElementById(id_hits).setAttribute('class', "closeNode");
		document.getElementById(id_facete).setAttribute('class', "openNode");
	}else{
		document.getElementById(element).style.display = 'none';
		document.getElementById(element + "_img").src = '/ingrid-portal-apps/images/facete/facete_close.png';
		document.getElementById(id_hits).setAttribute('class', "openNode");
		document.getElementById(id_facete).setAttribute('class', "closeNode");
	}
	
}

/* select all checkboxes in form */
function faceteDialogSelectAll(field){
	for (i = 0; i < field.length; i++)
		field[i].checked = true ;
}

/* deselect all checkboxes in form */
function faceteDialogDeselectAll(field){
	for (i = 0; i < field.length; i++)
		field[i].checked = false ;
}

/* cancel dialog */
function faceteDialogCancel(id){
	var dialog = dijit.byId(id);
	dialog.hide();
}

/* open dialog by onclick-event */
function prepareDialog (id) {
   var dialog = dijit.byId(id);
   dialog.show();
}

/* open dialog for map */
function prepareDialogMap (id, wms, divId, iframeId) {
		var dialog = dijit.byId(id);
		var map = document.getElementById(divId);
		if (map.childNodes.length <= 1) {
			var iframe = document.createElement('iframe');
			iframe.setAttribute('id', iframeId);
			iframe.setAttribute('class', 'facete_map');
			iframe.setAttribute('name', 'wms_viewer');
			iframe.setAttribute('marginheight', '0');
			iframe.setAttribute('marginwidth', '0');
			iframe.setAttribute('frameborder', '0');
			iframe.setAttribute('src', wms);
			map.appendChild(iframe);
		}
		dialog.show();
}

/* open dialog */
function loadDialog(id){
	var dialog = id;
	dialog.show();
}

function loadingProgressDialog(element){
	var status = document.getElementById(element).style.display;
	if(status == "inline"){
		document.getElementById(element).style.display = "none";
	}
	if(status == "none"){
		document.getElementById(element).style.display = "inline";
	}
}

function showButtonSelectCheckboxForm (form, button, coordDiv){
	var status = document.getElementById(button).style.display;
	var isSelect = false
	var divStatus = document.getElementById(coordDiv).firstChild;
	for (i = 0; i < form.length; i++){
		if(form[i].checked){
			isSelect = true;
			break;
		}		
	}
	
	if(isSelect && divStatus != null){
		if(status == "none"){
			document.getElementById(button).style.display = "inline";
		}
	}else{
		if(status == "inline"){
			document.getElementById(button).style.display = "none";
		}
	}
	
}
/* open dialog for map */
function prepareDialogWebMapClient(id) {
	var jsList = new Array(
			"/ingrid-webmap-client/lib/openlayers/lib/OpenLayers.js",
			"/ingrid-webmap-client/lib/openlayers.addins/LoadingPanel.js",
			"/ingrid-webmap-client/lib/extjs/adapter/ext/ext-base-debug.js",
			"/ingrid-webmap-client/lib/extjs/ext-all-debug.js",
			"/ingrid-webmap-client/lib/geoext/lib/GeoExt.js",
			"http://proj4js.org/lib/proj4js-compressed.js",
			"/ingrid-webmap-client/lib/geoext.ux/SimplePrint.js",
			"/ingrid-webmap-client/shared/js/config.js",
			"/ingrid-webmap-client/shared/js/Message.js",
			"/ingrid-webmap-client/shared/js/Configuration.js",
			"/ingrid-webmap-client/shared/js/data/StoreHelper.js",
			"/ingrid-webmap-client/shared/js/model/WmsProxy.js",
			"/ingrid-webmap-client/frontend/js/data/Session.js",
			"/ingrid-webmap-client/frontend/js/data/SessionState.js",
			"/ingrid-webmap-client/frontend/js/data/Service.js",
			"/ingrid-webmap-client/frontend/js/controls/ActiveServicesPanel.js",
			"/ingrid-webmap-client/frontend/js/controls/ServiceCategoryPanel.js",
			"/ingrid-webmap-client/frontend/js/controls/SettingsDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/OpacityDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/NewServiceDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/MetaDataDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/FeatureInfoControl.js",
			"/ingrid-webmap-client/frontend/js/controls/FeatureInfoDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/LoadDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/SaveDialog.js",
			"/ingrid-webmap-client/frontend/js/controls/PrintDialog.js",
			"/ingrid-webmap-client/frontend/js/PanelWorkspace.js",
			"/ingrid-webmap-client/frontend/js/main.js");

	var cssList = new Array(
			"/ingrid-webmap-client/lib/extjs/resources/css/ext-all.css",
			"/ingrid-webmap-client/frontend/css/style.css",
			"/ingrid-webmap-client/lib/openlayers.addins/loadingpanel.css");

	var dialog = dijit.byId(id);

	dialog.show();

}
/*
 * jsDynaLoad() - A dynamic Javascript file loader with support for arrays and callbacks
 * - http://github.com/hay/jsdynaload
 * Tutorial:
 * - http://www.haykranen.nl/?p=1290
 * Based on Nicholas C. Zakas' script:
 * - http://www.nczonline.net/blog/2009/07/28/the-best-way-to-load-external-javascript/
 * Licensed under the MIT / X11 License:
 * - http://opensource.org/licenses/mit-license.php
 *
 * USAGE
 * You can either load a single script (just provide the URL) or an array
 * of scripts. You can then provide a callback for your 'init' function
 *
 * - Single script:
 * jsDynaLoad("http://example.com/myscript.js", function() {
 * alert('loaded!');
 * });
 *
 * - Multiple scripts:
 * var scripts = ['script1.js', 'script2.js', 'script3.js'];
 * jsDynaLoad(scripts, function() {
 * alert("All scripts loaded!");
 * });
 *
 **/

function jsDynaLoad(arg, cb) {
	// Callback is not required
	cb = cb || function() {
	};

	function load(url, callback) {
		// Callback is not required
		callback = callback || function() {
		};

		var script = document.createElement("script")
		script.type = "text/javascript";

		if (script.readyState) { // IE
			script.onreadystatechange = function() {
				// IE sometimes gives back the one state, and sometimes the
				// other, so we need to check for both
				if (script.readyState === "loaded"
						|| script.readyState === "complete") {
					script.onreadystatechange = null;
					callback();
				}
			};
		} else { // Others
			script.onload = function() {
				callback();
			};
		}

		script.src = url;
		// Adding to the <head> is safer than the end of the <body>
		document.getElementsByTagName("head")[0].appendChild(script);
	}

	// Used for loading one single file
	if (typeof arg === "string") {
		load(arg, cb);
	} else if (arg instanceof Array) {
		// Load an array of files
		var i = 0, l = arg.length;

		function loadCb() {
			if (i >= l) {
				// Execute the original callback
				cb();
				return false;
			}

			load(arg[i], loadCb);
			i++;
		}

		loadCb();
	}
}

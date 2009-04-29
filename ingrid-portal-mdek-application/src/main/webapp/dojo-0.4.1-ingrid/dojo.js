/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(typeof dojo=="undefined"){
var dj_global=this;
var dj_currentContext=this;
function dj_undef(_1,_2){
return (typeof (_2||dj_currentContext)[_1]=="undefined");
}
if(dj_undef("djConfig",this)){
var djConfig={};
}
if(dj_undef("dojo",this)){
var dojo={};
}
dojo.global=function(){
return dj_currentContext;
};
dojo.locale=djConfig.locale;
dojo.version={major:0,minor:0,patch:0,flag:"dev",revision:Number("$Rev: 8615 $".match(/[0-9]+/)[0]),toString:function(){
with(dojo.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
dojo.evalProp=function(_3,_4,_5){
if((!_4)||(!_3)){
return undefined;
}
if(!dj_undef(_3,_4)){
return _4[_3];
}
return (_5?(_4[_3]={}):undefined);
};
dojo.parseObjPath=function(_6,_7,_8){
var _9=(_7||dojo.global());
var _a=_6.split(".");
var _b=_a.pop();
for(var i=0,l=_a.length;i<l&&_9;i++){
_9=dojo.evalProp(_a[i],_9,_8);
}
return {obj:_9,prop:_b};
};
dojo.evalObjPath=function(_e,_f){
if(typeof _e!="string"){
return dojo.global();
}
if(_e.indexOf(".")==-1){
return dojo.evalProp(_e,dojo.global(),_f);
}
var ref=dojo.parseObjPath(_e,dojo.global(),_f);
if(ref){
return dojo.evalProp(ref.prop,ref.obj,_f);
}
return null;
};
dojo.errorToString=function(_11){
if(!dj_undef("message",_11)){
return _11.message;
}else{
if(!dj_undef("description",_11)){
return _11.description;
}else{
return _11;
}
}
};
dojo.raise=function(_12,_13){
if(_13){
_12=_12+": "+dojo.errorToString(_13);
}else{
_12=dojo.errorToString(_12);
}
try{
if(djConfig.isDebug){
dojo.hostenv.println("FATAL exception raised: "+_12);
}
}
catch(e){
}
throw _13||Error(_12);
};
dojo.debug=function(){
};
dojo.debugShallow=function(obj){
};
dojo.profile={start:function(){
},end:function(){
},stop:function(){
},dump:function(){
}};
function dj_eval(_15){
return dj_global.eval?dj_global.eval(_15):eval(_15);
}
dojo.unimplemented=function(_16,_17){
var _18="'"+_16+"' not implemented";
if(_17!=null){
_18+=" "+_17;
}
dojo.raise(_18);
};
dojo.deprecated=function(_19,_1a,_1b){
var _1c="DEPRECATED: "+_19;
if(_1a){
_1c+=" "+_1a;
}
if(_1b){
_1c+=" -- will be removed in version: "+_1b;
}
dojo.debug(_1c);
};
dojo.render=(function(){
function vscaffold(_1d,_1e){
var tmp={capable:false,support:{builtin:false,plugin:false},prefixes:_1d};
for(var i=0;i<_1e.length;i++){
tmp[_1e[i]]=false;
}
return tmp;
}
return {name:"",ver:dojo.version,os:{win:false,linux:false,osx:false},html:vscaffold(["html"],["ie","opera","khtml","safari","moz"]),svg:vscaffold(["svg"],["corel","adobe","batik"]),vml:vscaffold(["vml"],["ie"]),swf:vscaffold(["Swf","Flash","Mm"],["mm"]),swt:vscaffold(["Swt"],["ibm"])};
})();
dojo.hostenv=(function(){
var _21={isDebug:false,allowQueryConfig:false,baseScriptUri:"",baseRelativePath:"",libraryScriptUri:"",iePreventClobber:false,ieClobberMinimal:true,preventBackButtonFix:true,delayMozLoadingFix:false,searchIds:[],parseWidgets:true};
if(typeof djConfig=="undefined"){
djConfig=_21;
}else{
for(var _22 in _21){
if(typeof djConfig[_22]=="undefined"){
djConfig[_22]=_21[_22];
}
}
}
return {name_:"(unset)",version_:"(unset)",getName:function(){
return this.name_;
},getVersion:function(){
return this.version_;
},getText:function(uri){
dojo.unimplemented("getText","uri="+uri);
}};
})();
dojo.hostenv.getBaseScriptUri=function(){
if(djConfig.baseScriptUri.length){
return djConfig.baseScriptUri;
}
var uri=new String(djConfig.libraryScriptUri||djConfig.baseRelativePath);
if(!uri){
dojo.raise("Nothing returned by getLibraryScriptUri(): "+uri);
}
var _25=uri.lastIndexOf("/");
djConfig.baseScriptUri=djConfig.baseRelativePath;
return djConfig.baseScriptUri;
};
(function(){
var _26={pkgFileName:"__package__",loading_modules_:{},loaded_modules_:{},addedToLoadingCount:[],removedFromLoadingCount:[],inFlightCount:0,modulePrefixes_:{dojo:{name:"dojo",value:"src"}},setModulePrefix:function(_27,_28){
this.modulePrefixes_[_27]={name:_27,value:_28};
},moduleHasPrefix:function(_29){
var mp=this.modulePrefixes_;
return Boolean(mp[_29]&&mp[_29].value);
},getModulePrefix:function(_2b){
if(this.moduleHasPrefix(_2b)){
return this.modulePrefixes_[_2b].value;
}
return _2b;
},getTextStack:[],loadUriStack:[],loadedUris:[],post_load_:false,modulesLoadedListeners:[],unloadListeners:[],loadNotifying:false};
for(var _2c in _26){
dojo.hostenv[_2c]=_26[_2c];
}
})();
dojo.hostenv.loadPath=function(_2d,_2e,cb){
var uri;
if(_2d.charAt(0)=="/"||_2d.match(/^\w+:/)){
uri=_2d;
}else{
uri=this.getBaseScriptUri()+_2d;
}
if(djConfig.cacheBust&&dojo.render.html.capable){
uri+="?"+String(djConfig.cacheBust).replace(/\W+/g,"");
}
try{
return !_2e?this.loadUri(uri,cb):this.loadUriAndCheck(uri,_2e,cb);
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.hostenv.loadUri=function(uri,cb){
if(this.loadedUris[uri]){
return true;
}
var _33=this.getText(uri,null,true);
if(!_33){
return false;
}
this.loadedUris[uri]=true;
if(cb){
_33="("+_33+")";
}
var _34=dj_eval(_33);
if(cb){
cb(_34);
}
return true;
};
dojo.hostenv.loadUriAndCheck=function(uri,_36,cb){
var ok=true;
try{
ok=this.loadUri(uri,cb);
}
catch(e){
dojo.debug("failed loading ",uri," with error: ",e);
}
return Boolean(ok&&this.findModule(_36,false));
};
dojo.loaded=function(){
};
dojo.unloaded=function(){
};
dojo.hostenv.loaded=function(){
this.loadNotifying=true;
this.post_load_=true;
var mll=this.modulesLoadedListeners;
for(var x=0;x<mll.length;x++){
mll[x]();
}
this.modulesLoadedListeners=[];
this.loadNotifying=false;
dojo.loaded();
};
dojo.hostenv.unloaded=function(){
var mll=this.unloadListeners;
while(mll.length){
(mll.pop())();
}
dojo.unloaded();
};
dojo.addOnLoad=function(obj,_3d){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.modulesLoadedListeners.push(obj);
}else{
if(arguments.length>1){
dh.modulesLoadedListeners.push(function(){
obj[_3d]();
});
}
}
if(dh.post_load_&&dh.inFlightCount==0&&!dh.loadNotifying){
dh.callLoaded();
}
};
dojo.addOnUnload=function(obj,_40){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.unloadListeners.push(obj);
}else{
if(arguments.length>1){
dh.unloadListeners.push(function(){
obj[_40]();
});
}
}
};
dojo.hostenv.modulesLoaded=function(){
if(this.post_load_){
return;
}
if(this.loadUriStack.length==0&&this.getTextStack.length==0){
if(this.inFlightCount>0){
dojo.debug("files still in flight!");
return;
}
dojo.hostenv.callLoaded();
}
};
dojo.hostenv.callLoaded=function(){
if(typeof setTimeout=="object"){
setTimeout("dojo.hostenv.loaded();",0);
}else{
dojo.hostenv.loaded();
}
};
dojo.hostenv.getModuleSymbols=function(_42){
var _43=_42.split(".");
for(var i=_43.length;i>0;i--){
var _45=_43.slice(0,i).join(".");
if((i==1)&&!this.moduleHasPrefix(_45)){
_43[0]="../"+_43[0];
}else{
var _46=this.getModulePrefix(_45);
if(_46!=_45){
_43.splice(0,i,_46);
break;
}
}
}
return _43;
};
dojo.hostenv._global_omit_module_check=false;
dojo.hostenv.loadModule=function(_47,_48,_49){
if(!_47){
return;
}
_49=this._global_omit_module_check||_49;
var _4a=this.findModule(_47,false);
if(_4a){
return _4a;
}
if(dj_undef(_47,this.loading_modules_)){
this.addedToLoadingCount.push(_47);
}
this.loading_modules_[_47]=1;
var _4b=_47.replace(/\./g,"/")+".js";
var _4c=_47.split(".");
var _4d=this.getModuleSymbols(_47);
var _4e=((_4d[0].charAt(0)!="/")&&!_4d[0].match(/^\w+:/));
var _4f=_4d[_4d.length-1];
var ok;
if(_4f=="*"){
_47=_4c.slice(0,-1).join(".");
while(_4d.length){
_4d.pop();
_4d.push(this.pkgFileName);
_4b=_4d.join("/")+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,!_49?_47:null);
if(ok){
break;
}
_4d.pop();
}
}else{
_4b=_4d.join("/")+".js";
_47=_4c.join(".");
var _51=!_49?_47:null;
ok=this.loadPath(_4b,_51);
if(!ok&&!_48){
_4d.pop();
while(_4d.length){
_4b=_4d.join("/")+".js";
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
_4d.pop();
_4b=_4d.join("/")+"/"+this.pkgFileName+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
}
}
if(!ok&&!_49){
dojo.raise("Could not load '"+_47+"'; last tried '"+_4b+"'");
}
}
if(!_49&&!this["isXDomain"]){
_4a=this.findModule(_47,false);
if(!_4a){
dojo.raise("symbol '"+_47+"' is not defined after loading '"+_4b+"'");
}
}
return _4a;
};
dojo.hostenv.startPackage=function(_52){
var _53=String(_52);
var _54=_53;
var _55=_52.split(/\./);
if(_55[_55.length-1]=="*"){
_55.pop();
_54=_55.join(".");
}
var _56=dojo.evalObjPath(_54,true);
this.loaded_modules_[_53]=_56;
this.loaded_modules_[_54]=_56;
return _56;
};
dojo.hostenv.findModule=function(_57,_58){
var lmn=String(_57);
if(this.loaded_modules_[lmn]){
return this.loaded_modules_[lmn];
}
if(_58){
dojo.raise("no loaded module named '"+_57+"'");
}
return null;
};
dojo.kwCompoundRequire=function(_5a){
var _5b=_5a["common"]||[];
var _5c=_5a[dojo.hostenv.name_]?_5b.concat(_5a[dojo.hostenv.name_]||[]):_5b.concat(_5a["default"]||[]);
for(var x=0;x<_5c.length;x++){
var _5e=_5c[x];
if(_5e.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_5e);
}else{
dojo.hostenv.loadModule(_5e);
}
}
};
dojo.require=function(_5f){
dojo.hostenv.loadModule.apply(dojo.hostenv,arguments);
};
dojo.requireIf=function(_60,_61){
var _62=arguments[0];
if((_62===true)||(_62=="common")||(_62&&dojo.render[_62].capable)){
var _63=[];
for(var i=1;i<arguments.length;i++){
_63.push(arguments[i]);
}
dojo.require.apply(dojo,_63);
}
};
dojo.requireAfterIf=dojo.requireIf;
dojo.provide=function(_65){
return dojo.hostenv.startPackage.apply(dojo.hostenv,arguments);
};
dojo.registerModulePath=function(_66,_67){
return dojo.hostenv.setModulePrefix(_66,_67);
};
dojo.setModulePrefix=function(_68,_69){
dojo.deprecated("dojo.setModulePrefix(\""+_68+"\", \""+_69+"\")","replaced by dojo.registerModulePath","0.5");
return dojo.registerModulePath(_68,_69);
};
dojo.exists=function(obj,_6b){
var p=_6b.split(".");
for(var i=0;i<p.length;i++){
if(!obj[p[i]]){
return false;
}
obj=obj[p[i]];
}
return true;
};
dojo.hostenv.normalizeLocale=function(_6e){
var _6f=_6e?_6e.toLowerCase():dojo.locale;
if(_6f=="root"){
_6f="ROOT";
}
return _6f;
};
dojo.hostenv.searchLocalePath=function(_70,_71,_72){
_70=dojo.hostenv.normalizeLocale(_70);
var _73=_70.split("-");
var _74=[];
for(var i=_73.length;i>0;i--){
_74.push(_73.slice(0,i).join("-"));
}
_74.push(false);
if(_71){
_74.reverse();
}
for(var j=_74.length-1;j>=0;j--){
var loc=_74[j]||"ROOT";
var _78=_72(loc);
if(_78){
break;
}
}
};
dojo.hostenv.localesGenerated=["ROOT","es-es","es","it-it","pt-br","de","fr-fr","zh-cn","pt","en-us","zh","fr","zh-tw","it","en-gb","xx","de-de","ko-kr","ja-jp","ko","en","ja"];
dojo.hostenv.registerNlsPrefix=function(){
dojo.registerModulePath("nls","nls");
};
dojo.hostenv.preloadLocalizations=function(){
if(dojo.hostenv.localesGenerated){
dojo.hostenv.registerNlsPrefix();
function preload(_79){
_79=dojo.hostenv.normalizeLocale(_79);
dojo.hostenv.searchLocalePath(_79,true,function(loc){
for(var i=0;i<dojo.hostenv.localesGenerated.length;i++){
if(dojo.hostenv.localesGenerated[i]==loc){
dojo["require"]("nls.dojo_"+loc);
return true;
}
}
return false;
});
}
preload();
var _7c=djConfig.extraLocale||[];
for(var i=0;i<_7c.length;i++){
preload(_7c[i]);
}
}
dojo.hostenv.preloadLocalizations=function(){
};
};
dojo.requireLocalization=function(_7e,_7f,_80,_81){
dojo.hostenv.preloadLocalizations();
var _82=dojo.hostenv.normalizeLocale(_80);
var _83=[_7e,"nls",_7f].join(".");
var _84="";
if(_81){
var _85=_81.split(",");
for(var i=0;i<_85.length;i++){
if(_82.indexOf(_85[i])==0){
if(_85[i].length>_84.length){
_84=_85[i];
}
}
}
if(!_84){
_84="ROOT";
}
}
var _87=_81?_84:_82;
var _88=dojo.hostenv.findModule(_83);
var _89=null;
if(_88){
if(djConfig.localizationComplete&&_88._built){
return;
}
var _8a=_87.replace("-","_");
var _8b=_83+"."+_8a;
_89=dojo.hostenv.findModule(_8b);
}
if(!_89){
_88=dojo.hostenv.startPackage(_83);
var _8c=dojo.hostenv.getModuleSymbols(_7e);
var _8d=_8c.concat("nls").join("/");
var _8e;
dojo.hostenv.searchLocalePath(_87,_81,function(loc){
var _90=loc.replace("-","_");
var _91=_83+"."+_90;
var _92=false;
if(!dojo.hostenv.findModule(_91)){
dojo.hostenv.startPackage(_91);
var _93=[_8d];
if(loc!="ROOT"){
_93.push(loc);
}
_93.push(_7f);
var _94=_93.join("/")+".js";
_92=dojo.hostenv.loadPath(_94,null,function(_95){
var _96=function(){
};
_96.prototype=_8e;
_88[_90]=new _96();
for(var j in _95){
_88[_90][j]=_95[j];
}
});
}else{
_92=true;
}
if(_92&&_88[_90]){
_8e=_88[_90];
}else{
_88[_90]=_8e;
}
if(_81){
return true;
}
});
}
if(_81&&_82!=_84){
_88[_82.replace("-","_")]=_88[_84.replace("-","_")];
}
};
(function(){
var _98=djConfig.extraLocale;
if(_98){
if(!_98 instanceof Array){
_98=[_98];
}
var req=dojo.requireLocalization;
dojo.requireLocalization=function(m,b,_9c,_9d){
req(m,b,_9c,_9d);
if(_9c){
return;
}
for(var i=0;i<_98.length;i++){
req(m,b,_98[i],_9d);
}
};
}
})();
}
if(typeof window!="undefined"){
(function(){
if(djConfig.allowQueryConfig){
var _9f=document.location.toString();
var _a0=_9f.split("?",2);
if(_a0.length>1){
var _a1=_a0[1];
var _a2=_a1.split("&");
for(var x in _a2){
var sp=_a2[x].split("=");
if((sp[0].length>9)&&(sp[0].substr(0,9)=="djConfig.")){
var opt=sp[0].substr(9);
try{
djConfig[opt]=eval(sp[1]);
}
catch(e){
djConfig[opt]=sp[1];
}
}
}
}
}
if(((djConfig["baseScriptUri"]=="")||(djConfig["baseRelativePath"]==""))&&(document&&document.getElementsByTagName)){
var _a6=document.getElementsByTagName("script");
var _a7=/(__package__|dojo|bootstrap1)\.js([\?\.]|$)/i;
for(var i=0;i<_a6.length;i++){
var src=_a6[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_a7);
if(m){
var _ab=src.substring(0,m.index);
if(src.indexOf("bootstrap1")>-1){
_ab+="../";
}
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=_ab;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=_ab;
}
break;
}
}
}
var dr=dojo.render;
var drh=dojo.render.html;
var drs=dojo.render.svg;
var dua=(drh.UA=navigator.userAgent);
var dav=(drh.AV=navigator.appVersion);
var t=true;
var f=false;
drh.capable=t;
drh.support.builtin=t;
dr.ver=parseFloat(drh.AV);
dr.os.mac=dav.indexOf("Macintosh")>=0;
dr.os.win=dav.indexOf("Windows")>=0;
dr.os.linux=dav.indexOf("X11")>=0;
drh.opera=dua.indexOf("Opera")>=0;
drh.khtml=(dav.indexOf("Konqueror")>=0)||(dav.indexOf("Safari")>=0);
drh.safari=dav.indexOf("Safari")>=0;
var _b3=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_b3>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_b3+6,_b3+14);
}
drh.ie=(document.all)&&(!drh.opera);
drh.ie50=drh.ie&&dav.indexOf("MSIE 5.0")>=0;
drh.ie55=drh.ie&&dav.indexOf("MSIE 5.5")>=0;
drh.ie60=drh.ie&&dav.indexOf("MSIE 6.0")>=0;
drh.ie70=drh.ie&&dav.indexOf("MSIE 7.0")>=0;
var cm=document["compatMode"];
drh.quirks=(cm=="BackCompat")||(cm=="QuirksMode")||drh.ie55||drh.ie50;
dojo.locale=dojo.locale||(drh.ie?navigator.userLanguage:navigator.language).toLowerCase();
dr.vml.capable=drh.ie;
drs.capable=f;
drs.support.plugin=f;
drs.support.builtin=f;
var _b5=window["document"];
var tdi=_b5["implementation"];
if((tdi)&&(tdi["hasFeature"])&&(tdi.hasFeature("org.w3c.dom.svg","1.0"))){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
if(drh.safari){
var tmp=dua.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
}else{
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.render.name=dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
dojo.hostenv._XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _b9=null;
var _ba=null;
try{
_b9=new XMLHttpRequest();
}
catch(e){
}
if(!_b9){
for(var i=0;i<3;++i){
var _bc=dojo.hostenv._XMLHTTP_PROGIDS[i];
try{
_b9=new ActiveXObject(_bc);
}
catch(e){
_ba=e;
}
if(_b9){
dojo.hostenv._XMLHTTP_PROGIDS=[_bc];
break;
}
}
}
if(!_b9){
return dojo.raise("XMLHTTP not available",_ba);
}
return _b9;
};
dojo.hostenv._blockAsync=false;
dojo.hostenv.getText=function(uri,_be,_bf){
if(!_be){
this._blockAsync=true;
}
var _c0=this.getXmlhttpObject();
function isDocumentOk(_c1){
var _c2=_c1["status"];
return Boolean((!_c2)||((200<=_c2)&&(300>_c2))||(_c2==304));
}
if(_be){
var _c3=this,_c4=null,gbl=dojo.global();
var xhr=dojo.evalObjPath("dojo.io.XMLHTTPTransport");
_c0.onreadystatechange=function(){
if(_c4){
gbl.clearTimeout(_c4);
_c4=null;
}
if(_c3._blockAsync||(xhr&&xhr._blockAsync)){
_c4=gbl.setTimeout(function(){
_c0.onreadystatechange.apply(this);
},10);
}else{
if(4==_c0.readyState){
if(isDocumentOk(_c0)){
_be(_c0.responseText);
}
}
}
};
}
_c0.open("GET",uri,_be?true:false);
try{
_c0.send(null);
if(_be){
return null;
}
if(!isDocumentOk(_c0)){
var err=Error("Unable to load "+uri+" status:"+_c0.status);
err.status=_c0.status;
err.responseText=_c0.responseText;
throw err;
}
}
catch(e){
this._blockAsync=false;
if((_bf)&&(!_be)){
return null;
}else{
throw e;
}
}
this._blockAsync=false;
return _c0.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_c8){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_c8);
}else{
try{
var _c9=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_c9){
_c9=dojo.body();
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_c8));
_c9.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_c8+"</div>");
}
catch(e2){
window.status=_c8;
}
}
}
};
dojo.addOnLoad(function(){
dojo.hostenv._println_safe=true;
while(dojo.hostenv._println_buffer.length>0){
dojo.hostenv.println(dojo.hostenv._println_buffer.shift());
}
});
function dj_addNodeEvtHdlr(_cb,_cc,fp){
var _ce=_cb["on"+_cc]||function(){
};
_cb["on"+_cc]=function(){
fp.apply(_cb,arguments);
_ce.apply(_cb,arguments);
};
return true;
}
function dj_load_init(e){
var _d0=(e&&e.type)?e.type.toLowerCase():"load";
if(arguments.callee.initialized||(_d0!="domcontentloaded"&&_d0!="load")){
return;
}
arguments.callee.initialized=true;
if(typeof (_timer)!="undefined"){
clearInterval(_timer);
delete _timer;
}
var _d1=function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
};
if(dojo.hostenv.inFlightCount==0){
_d1();
dojo.hostenv.modulesLoaded();
}else{
dojo.hostenv.modulesLoadedListeners.unshift(_d1);
}
}
if(document.addEventListener){
if(dojo.render.html.opera||(dojo.render.html.moz&&!djConfig.delayMozLoadingFix)){
document.addEventListener("DOMContentLoaded",dj_load_init,null);
}
window.addEventListener("load",dj_load_init,null);
}
if(dojo.render.html.ie&&dojo.render.os.win){
document.attachEvent("onreadystatechange",function(e){
if(document.readyState=="complete"){
dj_load_init();
}
});
}
if(/(WebKit|khtml)/i.test(navigator.userAgent)){
var _timer=setInterval(function(){
if(/loaded|complete/.test(document.readyState)){
dj_load_init();
}
},10);
}
if(dojo.render.html.ie){
dj_addNodeEvtHdlr(window,"beforeunload",function(){
dojo.hostenv._unloading=true;
window.setTimeout(function(){
dojo.hostenv._unloading=false;
},0);
});
}
dj_addNodeEvtHdlr(window,"unload",function(){
dojo.hostenv.unloaded();
if((!dojo.render.html.ie)||(dojo.render.html.ie&&dojo.hostenv._unloading)){
dojo.hostenv.unloaded();
}
});
dojo.hostenv.makeWidgets=function(){
var _d3=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_d3=_d3.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_d3=_d3.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_d3.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
var _d4=new dojo.xml.Parse();
if(_d3.length>0){
for(var x=0;x<_d3.length;x++){
var _d6=document.getElementById(_d3[x]);
if(!_d6){
continue;
}
var _d7=_d4.parseElement(_d6,null,true);
dojo.widget.getParser().createComponents(_d7);
}
}else{
if(djConfig.parseWidgets){
var _d7=_d4.parseElement(dojo.body(),null,true);
dojo.widget.getParser().createComponents(_d7);
}
}
}
}
};
dojo.addOnLoad(function(){
if(!dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
});
try{
if(dojo.render.html.ie){
document.namespaces.add("v","urn:schemas-microsoft-com:vml");
document.createStyleSheet().addRule("v\\:*","behavior:url(#default#VML)");
}
}
catch(e){
}
dojo.hostenv.writeIncludes=function(){
};
if(!dj_undef("document",this)){
dj_currentDocument=this.document;
}
dojo.doc=function(){
return dj_currentDocument;
};
dojo.body=function(){
return dojo.doc().body||dojo.doc().getElementsByTagName("body")[0];
};
dojo.byId=function(id,doc){
if((id)&&((typeof id=="string")||(id instanceof String))){
if(!doc){
doc=dj_currentDocument;
}
var ele=doc.getElementById(id);
if(ele&&(ele.id!=id)&&doc.all){
ele=null;
eles=doc.all[id];
if(eles){
if(eles.length){
for(var i=0;i<eles.length;i++){
if(eles[i].id==id){
ele=eles[i];
break;
}
}
}else{
ele=eles;
}
}
}
return ele;
}
return id;
};
dojo.setContext=function(_dc,_dd){
dj_currentContext=_dc;
dj_currentDocument=_dd;
};
dojo._fireCallback=function(_de,_df,_e0){
if((_df)&&((typeof _de=="string")||(_de instanceof String))){
_de=_df[_de];
}
return (_df?_de.apply(_df,_e0||[]):_de());
};
dojo.withGlobal=function(_e1,_e2,_e3,_e4){
var _e5;
var _e6=dj_currentContext;
var _e7=dj_currentDocument;
try{
dojo.setContext(_e1,_e1.document);
_e5=dojo._fireCallback(_e2,_e3,_e4);
}
finally{
dojo.setContext(_e6,_e7);
}
return _e5;
};
dojo.withDoc=function(_e8,_e9,_ea,_eb){
var _ec;
var _ed=dj_currentDocument;
try{
dj_currentDocument=_e8;
_ec=dojo._fireCallback(_e9,_ea,_eb);
}
finally{
dj_currentDocument=_ed;
}
return _ec;
};
}
(function(){
if(typeof dj_usingBootstrap!="undefined"){
return;
}
var _ee=false;
var _ef=false;
var _f0=false;
if((typeof this["load"]=="function")&&((typeof this["Packages"]=="function")||(typeof this["Packages"]=="object"))){
_ee=true;
}else{
if(typeof this["load"]=="function"){
_ef=true;
}else{
if(window.widget){
_f0=true;
}
}
}
var _f1=[];
if((this["djConfig"])&&((djConfig["isDebug"])||(djConfig["debugAtAllCosts"]))){
_f1.push("debug.js");
}
if((this["djConfig"])&&(djConfig["debugAtAllCosts"])&&(!_ee)&&(!_f0)){
_f1.push("browser_debug.js");
}
var _f2=djConfig["baseScriptUri"];
if((this["djConfig"])&&(djConfig["baseLoaderUri"])){
_f2=djConfig["baseLoaderUri"];
}
for(var x=0;x<_f1.length;x++){
var _f4=_f2+"src/"+_f1[x];
if(_ee||_ef){
load(_f4);
}else{
try{
document.write("<scr"+"ipt type='text/javascript' src='"+_f4+"'></scr"+"ipt>");
}
catch(e){
var _f5=document.createElement("script");
_f5.src=_f4;
document.getElementsByTagName("head")[0].appendChild(_f5);
}
}
}
})();
dojo.provide("dojo.dom");
dojo.dom.ELEMENT_NODE=1;
dojo.dom.ATTRIBUTE_NODE=2;
dojo.dom.TEXT_NODE=3;
dojo.dom.CDATA_SECTION_NODE=4;
dojo.dom.ENTITY_REFERENCE_NODE=5;
dojo.dom.ENTITY_NODE=6;
dojo.dom.PROCESSING_INSTRUCTION_NODE=7;
dojo.dom.COMMENT_NODE=8;
dojo.dom.DOCUMENT_NODE=9;
dojo.dom.DOCUMENT_TYPE_NODE=10;
dojo.dom.DOCUMENT_FRAGMENT_NODE=11;
dojo.dom.NOTATION_NODE=12;
dojo.dom.dojoml="http://www.dojotoolkit.org/2004/dojoml";
dojo.dom.xmlns={svg:"http://www.w3.org/2000/svg",smil:"http://www.w3.org/2001/SMIL20/",mml:"http://www.w3.org/1998/Math/MathML",cml:"http://www.xml-cml.org",xlink:"http://www.w3.org/1999/xlink",xhtml:"http://www.w3.org/1999/xhtml",xul:"http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul",xbl:"http://www.mozilla.org/xbl",fo:"http://www.w3.org/1999/XSL/Format",xsl:"http://www.w3.org/1999/XSL/Transform",xslt:"http://www.w3.org/1999/XSL/Transform",xi:"http://www.w3.org/2001/XInclude",xforms:"http://www.w3.org/2002/01/xforms",saxon:"http://icl.com/saxon",xalan:"http://xml.apache.org/xslt",xsd:"http://www.w3.org/2001/XMLSchema",dt:"http://www.w3.org/2001/XMLSchema-datatypes",xsi:"http://www.w3.org/2001/XMLSchema-instance",rdf:"http://www.w3.org/1999/02/22-rdf-syntax-ns#",rdfs:"http://www.w3.org/2000/01/rdf-schema#",dc:"http://purl.org/dc/elements/1.1/",dcq:"http://purl.org/dc/qualifiers/1.0","soap-env":"http://schemas.xmlsoap.org/soap/envelope/",wsdl:"http://schemas.xmlsoap.org/wsdl/",AdobeExtensions:"http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"};
dojo.dom.isNode=function(wh){
if(typeof Element=="function"){
try{
return wh instanceof Element;
}
catch(e){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.dom.getUniqueId=function(){
var _f7=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_f7.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_f9,_fa){
var _fb=_f9.firstChild;
while(_fb&&_fb.nodeType!=dojo.dom.ELEMENT_NODE){
_fb=_fb.nextSibling;
}
if(_fa&&_fb&&_fb.tagName&&_fb.tagName.toLowerCase()!=_fa.toLowerCase()){
_fb=dojo.dom.nextElement(_fb,_fa);
}
return _fb;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_fc,_fd){
var _fe=_fc.lastChild;
while(_fe&&_fe.nodeType!=dojo.dom.ELEMENT_NODE){
_fe=_fe.previousSibling;
}
if(_fd&&_fe&&_fe.tagName&&_fe.tagName.toLowerCase()!=_fd.toLowerCase()){
_fe=dojo.dom.prevElement(_fe,_fd);
}
return _fe;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(_ff,_100){
if(!_ff){
return null;
}
do{
_ff=_ff.nextSibling;
}while(_ff&&_ff.nodeType!=dojo.dom.ELEMENT_NODE);
if(_ff&&_100&&_100.toLowerCase()!=_ff.tagName.toLowerCase()){
return dojo.dom.nextElement(_ff,_100);
}
return _ff;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_102){
if(!node){
return null;
}
if(_102){
_102=_102.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_102&&_102.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_102);
}
return node;
};
dojo.dom.moveChildren=function(_103,_104,trim){
var _106=0;
if(trim){
while(_103.hasChildNodes()&&_103.firstChild.nodeType==dojo.dom.TEXT_NODE){
_103.removeChild(_103.firstChild);
}
while(_103.hasChildNodes()&&_103.lastChild.nodeType==dojo.dom.TEXT_NODE){
_103.removeChild(_103.lastChild);
}
}
while(_103.hasChildNodes()){
_104.appendChild(_103.firstChild);
_106++;
}
return _106;
};
dojo.dom.copyChildren=function(_107,_108,trim){
var _10a=_107.cloneNode(true);
return this.moveChildren(_10a,_108,trim);
};
dojo.dom.replaceChildren=function(node,_10c){
var _10d=[];
if(dojo.render.html.ie){
for(var i=0;i<node.childNodes.length;i++){
_10d.push(node.childNodes[i]);
}
}
dojo.dom.removeChildren(node);
node.appendChild(_10c);
for(var i=0;i<_10d.length;i++){
dojo.dom.destroyNode(_10d[i]);
}
};
dojo.dom.removeChildren=function(node){
var _110=node.childNodes.length;
while(node.hasChildNodes()){
dojo.dom.removeNode(node.firstChild);
}
return _110;
};
dojo.dom.replaceNode=function(node,_112){
return node.parentNode.replaceChild(_112,node);
};
dojo.dom.destroyNode=function(node){
if(node.parentNode){
node=dojo.dom.removeNode(node);
}
if(node.nodeType!=3){
if(dojo.evalObjPath("dojo.event.browser.clean",false)){
dojo.event.browser.clean(node);
}
if(dojo.render.html.ie){
node.outerHTML="";
}
}
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_116,_117){
var _118=[];
var _119=(_116&&(_116 instanceof Function||typeof _116=="function"));
while(node){
if(!_119||_116(node)){
_118.push(node);
}
if(_117&&_118.length>0){
return _118[0];
}
node=node.parentNode;
}
if(_117){
return null;
}
return _118;
};
dojo.dom.getAncestorsByTag=function(node,tag,_11c){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_11c);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_121,_122){
if(_122&&node){
node=node.parentNode;
}
while(node){
if(node==_121){
return true;
}
node=node.parentNode;
}
return false;
};
dojo.dom.innerXML=function(node){
if(node.innerXML){
return node.innerXML;
}else{
if(node.xml){
return node.xml;
}else{
if(typeof XMLSerializer!="undefined"){
return (new XMLSerializer()).serializeToString(node);
}
}
}
};
dojo.dom.createDocument=function(){
var doc=null;
var _125=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _126=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_126.length;i++){
try{
doc=new ActiveXObject(_126[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_125.implementation)&&(_125.implementation.createDocument)){
doc=_125.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_129){
if(!_129){
_129="text/xml";
}
if(!dj_undef("DOMParser")){
var _12a=new DOMParser();
return _12a.parseFromString(str,_129);
}else{
if(!dj_undef("ActiveXObject")){
var _12b=dojo.dom.createDocument();
if(_12b){
_12b.async=false;
_12b.loadXML(str);
return _12b;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _12c=dojo.doc();
if(_12c.createElement){
var tmp=_12c.createElement("xml");
tmp.innerHTML=str;
if(_12c.implementation&&_12c.implementation.createDocument){
var _12e=_12c.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_12e.importNode(tmp.childNodes.item(i),true);
}
return _12e;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_131){
if(_131.firstChild){
_131.insertBefore(node,_131.firstChild);
}else{
_131.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_134){
if((_134!=true)&&(node===ref||node.nextSibling===ref)){
return false;
}
var _135=ref.parentNode;
_135.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_138){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_138!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_138);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_13c){
if((!node)||(!ref)||(!_13c)){
return false;
}
switch(_13c.toLowerCase()){
case "before":
return dojo.dom.insertBefore(node,ref);
case "after":
return dojo.dom.insertAfter(node,ref);
case "first":
if(ref.firstChild){
return dojo.dom.insertBefore(node,ref.firstChild);
}else{
ref.appendChild(node);
return true;
}
break;
default:
ref.appendChild(node);
return true;
}
};
dojo.dom.insertAtIndex=function(node,_13e,_13f){
var _140=_13e.childNodes;
if(!_140.length||_140.length==_13f){
_13e.appendChild(node);
return true;
}
if(_13f==0){
return dojo.dom.prependChild(node,_13e);
}
return dojo.dom.insertAfter(node,_140[_13f-1]);
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _143=dojo.doc();
dojo.dom.replaceChildren(node,_143.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _144="";
if(node==null){
return _144;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_144+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_144+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _144;
}
};
dojo.dom.hasParent=function(node){
return Boolean(node&&node.parentNode&&dojo.dom.isNode(node.parentNode));
};
dojo.dom.isTag=function(node){
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName==String(arguments[i])){
return String(arguments[i]);
}
}
}
return "";
};
dojo.dom.setAttributeNS=function(elem,_14a,_14b,_14c){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_14a,_14b,_14c);
}else{
var _14d=elem.ownerDocument;
var _14e=_14d.createNode(2,_14b,_14a);
_14e.nodeValue=_14c;
elem.setAttributeNode(_14e);
}
};
dojo.provide("dojo.xml.Parse");
dojo.xml.Parse=function(){
var isIE=((dojo.render.html.capable)&&(dojo.render.html.ie));
function getTagName(node){
try{
return node.tagName.toLowerCase();
}
catch(e){
return "";
}
}
function getDojoTagName(node){
var _152=getTagName(node);
if(!_152){
return "";
}
if((dojo.widget)&&(dojo.widget.tags[_152])){
return _152;
}
var p=_152.indexOf(":");
if(p>=0){
return _152;
}
if(_152.substr(0,5)=="dojo:"){
return _152;
}
if(dojo.render.html.capable&&dojo.render.html.ie&&node.scopeName!="HTML"){
return node.scopeName.toLowerCase()+":"+_152;
}
if(_152.substr(0,4)=="dojo"){
return "dojo:"+_152.substring(4);
}
var djt=node.getAttribute("dojoType")||node.getAttribute("dojotype");
if(djt){
if(djt.indexOf(":")<0){
djt="dojo:"+djt;
}
return djt.toLowerCase();
}
djt=node.getAttributeNS&&node.getAttributeNS(dojo.dom.dojoml,"type");
if(djt){
return "dojo:"+djt.toLowerCase();
}
try{
djt=node.getAttribute("dojo:type");
}
catch(e){
}
if(djt){
return "dojo:"+djt.toLowerCase();
}
if((dj_global["djConfig"])&&(!djConfig["ignoreClassNames"])){
var _155=node.className||node.getAttribute("class");
if((_155)&&(_155.indexOf)&&(_155.indexOf("dojo-")!=-1)){
var _156=_155.split(" ");
for(var x=0,c=_156.length;x<c;x++){
if(_156[x].slice(0,5)=="dojo-"){
return "dojo:"+_156[x].substr(5).toLowerCase();
}
}
}
}
return "";
}
this.parseElement=function(node,_15a,_15b,_15c){
var _15d=getTagName(node);
if(isIE&&_15d.indexOf("/")==0){
return null;
}
try{
var attr=node.getAttribute("parseWidgets");
if(attr&&attr.toLowerCase()=="false"){
return {};
}
}
catch(e){
}
var _15f=true;
if(_15b){
var _160=getDojoTagName(node);
_15d=_160||_15d;
_15f=Boolean(_160);
}
var _161={};
_161[_15d]=[];
var pos=_15d.indexOf(":");
if(pos>0){
var ns=_15d.substring(0,pos);
_161["ns"]=ns;
if((dojo.ns)&&(!dojo.ns.allow(ns))){
_15f=false;
}
}
if(_15f){
var _164=this.parseAttributes(node);
for(var attr in _164){
if((!_161[_15d][attr])||(typeof _161[_15d][attr]!="array")){
_161[_15d][attr]=[];
}
_161[_15d][attr].push(_164[attr]);
}
_161[_15d].nodeRef=node;
_161.tagName=_15d;
_161.index=_15c||0;
}
var _165=0;
for(var i=0;i<node.childNodes.length;i++){
var tcn=node.childNodes.item(i);
switch(tcn.nodeType){
case dojo.dom.ELEMENT_NODE:
var ctn=getDojoTagName(tcn)||getTagName(tcn);
if(!_161[ctn]){
_161[ctn]=[];
}
_161[ctn].push(this.parseElement(tcn,true,_15b,_165));
if((tcn.childNodes.length==1)&&(tcn.childNodes.item(0).nodeType==dojo.dom.TEXT_NODE)){
_161[ctn][_161[ctn].length-1].value=tcn.childNodes.item(0).nodeValue;
}
_165++;
break;
case dojo.dom.TEXT_NODE:
if(node.childNodes.length==1){
_161[_15d].push({value:node.childNodes.item(0).nodeValue});
}
break;
default:
break;
}
}
return _161;
};
this.parseAttributes=function(node){
var _16a={};
var atts=node.attributes;
var _16c,i=0;
while((_16c=atts[i++])){
if(isIE){
if(!_16c){
continue;
}
if((typeof _16c=="object")&&(typeof _16c.nodeValue=="undefined")||(_16c.nodeValue==null)||(_16c.nodeValue=="")){
continue;
}
}
var nn=_16c.nodeName.split(":");
nn=(nn.length==2)?nn[1]:_16c.nodeName;
_16a[nn]={value:_16c.nodeValue};
}
return _16a;
};
};
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_16f,_170){
if(!dojo.lang.isFunction(_170)){
dojo.raise("dojo.inherits: superclass argument ["+_170+"] must be a function (subclass: ["+_16f+"']");
}
_16f.prototype=new _170();
_16f.prototype.constructor=_16f;
_16f.superclass=_170.prototype;
_16f["super"]=_170.prototype;
};
dojo.lang._mixin=function(obj,_172){
var tobj={};
for(var x in _172){
if((typeof tobj[x]=="undefined")||(tobj[x]!=_172[x])){
obj[x]=_172[x];
}
}
if(dojo.render.html.ie&&(typeof (_172["toString"])=="function")&&(_172["toString"]!=obj["toString"])&&(_172["toString"]!=tobj["toString"])){
obj.toString=_172.toString;
}
return obj;
};
dojo.lang.mixin=function(obj,_176){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(obj,arguments[i]);
}
return obj;
};
dojo.lang.extend=function(_179,_17a){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(_179.prototype,arguments[i]);
}
return _179;
};
dojo.inherits=dojo.lang.inherits;
dojo.mixin=dojo.lang.mixin;
dojo.extend=dojo.lang.extend;
dojo.lang.find=function(_17d,_17e,_17f,_180){
if(!dojo.lang.isArrayLike(_17d)&&dojo.lang.isArrayLike(_17e)){
dojo.deprecated("dojo.lang.find(value, array)","use dojo.lang.find(array, value) instead","0.5");
var temp=_17d;
_17d=_17e;
_17e=temp;
}
var _182=dojo.lang.isString(_17d);
if(_182){
_17d=_17d.split("");
}
if(_180){
var step=-1;
var i=_17d.length-1;
var end=-1;
}else{
var step=1;
var i=0;
var end=_17d.length;
}
if(_17f){
while(i!=end){
if(_17d[i]===_17e){
return i;
}
i+=step;
}
}else{
while(i!=end){
if(_17d[i]==_17e){
return i;
}
i+=step;
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(_186,_187,_188){
return dojo.lang.find(_186,_187,_188,true);
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(_189,_18a){
return dojo.lang.find(_189,_18a)>-1;
};
dojo.lang.isObject=function(it){
if(typeof it=="undefined"){
return false;
}
return (typeof it=="object"||it===null||dojo.lang.isArray(it)||dojo.lang.isFunction(it));
};
dojo.lang.isArray=function(it){
return (it&&it instanceof Array||typeof it=="array");
};
dojo.lang.isArrayLike=function(it){
if((!it)||(dojo.lang.isUndefined(it))){
return false;
}
if(dojo.lang.isString(it)){
return false;
}
if(dojo.lang.isFunction(it)){
return false;
}
if(dojo.lang.isArray(it)){
return true;
}
if((it.tagName)&&(it.tagName.toLowerCase()=="form")){
return false;
}
if(dojo.lang.isNumber(it.length)&&isFinite(it.length)){
return true;
}
return false;
};
dojo.lang.isFunction=function(it){
return (it instanceof Function||typeof it=="function");
};
(function(){
if((dojo.render.html.capable)&&(dojo.render.html["safari"])){
dojo.lang.isFunction=function(it){
if((typeof (it)=="function")&&(it=="[object NodeList]")){
return false;
}
return (it instanceof Function||typeof it=="function");
};
}
})();
dojo.lang.isString=function(it){
return (typeof it=="string"||it instanceof String);
};
dojo.lang.isAlien=function(it){
if(!it){
return false;
}
return !dojo.lang.isFunction(it)&&/\{\s*\[native code\]\s*\}/.test(String(it));
};
dojo.lang.isBoolean=function(it){
return (it instanceof Boolean||typeof it=="boolean");
};
dojo.lang.isNumber=function(it){
return (it instanceof Number||typeof it=="number");
};
dojo.lang.isUndefined=function(it){
return ((typeof (it)=="undefined")&&(it==undefined));
};
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_195,_196){
var fcn=(dojo.lang.isString(_196)?_195[_196]:_196)||function(){
};
return function(){
return fcn.apply(_195,arguments);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_198,_199,_19a){
var nso=(_199||dojo.lang.anon);
if((_19a)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_198){
return x;
}
}
catch(e){
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_198;
return ret;
};
dojo.lang.forward=function(_19e){
return function(){
return this[_19e].apply(this,arguments);
};
};
dojo.lang.curry=function(_19f,func){
var _1a1=[];
_19f=_19f||dj_global;
if(dojo.lang.isString(func)){
func=_19f[func];
}
for(var x=2;x<arguments.length;x++){
_1a1.push(arguments[x]);
}
var _1a3=(func["__preJoinArity"]||func.length)-_1a1.length;
function gather(_1a4,_1a5,_1a6){
var _1a7=_1a6;
var _1a8=_1a5.slice(0);
for(var x=0;x<_1a4.length;x++){
_1a8.push(_1a4[x]);
}
_1a6=_1a6-_1a4.length;
if(_1a6<=0){
var res=func.apply(_19f,_1a8);
_1a6=_1a7;
return res;
}else{
return function(){
return gather(arguments,_1a8,_1a6);
};
}
}
return gather([],_1a1,_1a3);
};
dojo.lang.curryArguments=function(_1ab,func,args,_1ae){
var _1af=[];
var x=_1ae||0;
for(x=_1ae;x<args.length;x++){
_1af.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[_1ab,func].concat(_1af));
};
dojo.lang.tryThese=function(){
for(var x=0;x<arguments.length;x++){
try{
if(typeof arguments[x]=="function"){
var ret=(arguments[x]());
if(ret){
return ret;
}
}
}
catch(e){
dojo.debug(e);
}
}
};
dojo.lang.delayThese=function(farr,cb,_1b5,_1b6){
if(!farr.length){
if(typeof _1b6=="function"){
_1b6();
}
return;
}
if((typeof _1b5=="undefined")&&(typeof cb=="number")){
_1b5=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_1b5){
_1b5=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_1b5,_1b6);
},_1b5);
};
dojo.provide("dojo.lang.array");
dojo.lang.mixin(dojo.lang,{has:function(obj,name){
try{
return typeof obj[name]!="undefined";
}
catch(e){
return false;
}
},isEmpty:function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _1bb=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_1bb++;
break;
}
}
return _1bb==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
},map:function(arr,obj,_1bf){
var _1c0=dojo.lang.isString(arr);
if(_1c0){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_1bf)){
_1bf=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_1bf){
var _1c1=obj;
obj=_1bf;
_1bf=_1c1;
}
}
if(Array.map){
var _1c2=Array.map(arr,_1bf,obj);
}else{
var _1c2=[];
for(var i=0;i<arr.length;++i){
_1c2.push(_1bf.call(obj,arr[i]));
}
}
if(_1c0){
return _1c2.join("");
}else{
return _1c2;
}
},reduce:function(arr,_1c5,obj,_1c7){
var _1c8=_1c5;
if(arguments.length==1){
dojo.debug("dojo.lang.reduce called with too few arguments!");
return false;
}else{
if(arguments.length==2){
_1c7=_1c5;
_1c8=arr.shift();
}else{
if(arguments.lenght==3){
if(dojo.lang.isFunction(obj)){
_1c7=obj;
obj=null;
}
}else{
if(dojo.lang.isFunction(obj)){
var tmp=_1c7;
_1c7=obj;
obj=tmp;
}
}
}
}
var ob=obj?obj:dj_global;
dojo.lang.map(arr,function(val){
_1c8=_1c7.call(ob,_1c8,val);
});
return _1c8;
},forEach:function(_1cc,_1cd,_1ce){
if(dojo.lang.isString(_1cc)){
_1cc=_1cc.split("");
}
if(Array.forEach){
Array.forEach(_1cc,_1cd,_1ce);
}else{
if(!_1ce){
_1ce=dj_global;
}
for(var i=0,l=_1cc.length;i<l;i++){
_1cd.call(_1ce,_1cc[i],i,_1cc);
}
}
},_everyOrSome:function(_1d1,arr,_1d3,_1d4){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_1d1?"every":"some"](arr,_1d3,_1d4);
}else{
if(!_1d4){
_1d4=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _1d7=_1d3.call(_1d4,arr[i],i,arr);
if(_1d1&&!_1d7){
return false;
}else{
if((!_1d1)&&(_1d7)){
return true;
}
}
}
return Boolean(_1d1);
}
},every:function(arr,_1d9,_1da){
return this._everyOrSome(true,arr,_1d9,_1da);
},some:function(arr,_1dc,_1dd){
return this._everyOrSome(false,arr,_1dc,_1dd);
},filter:function(arr,_1df,_1e0){
var _1e1=dojo.lang.isString(arr);
if(_1e1){
arr=arr.split("");
}
var _1e2;
if(Array.filter){
_1e2=Array.filter(arr,_1df,_1e0);
}else{
if(!_1e0){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_1e0=dj_global;
}
_1e2=[];
for(var i=0;i<arr.length;i++){
if(_1df.call(_1e0,arr[i],i,arr)){
_1e2.push(arr[i]);
}
}
}
if(_1e1){
return _1e2.join("");
}else{
return _1e2;
}
},unnest:function(){
var out=[];
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isArrayLike(arguments[i])){
var add=dojo.lang.unnest.apply(this,arguments[i]);
out=out.concat(add);
}else{
out.push(arguments[i]);
}
}
return out;
},toArray:function(_1e7,_1e8){
var _1e9=[];
for(var i=_1e8||0;i<_1e7.length;i++){
_1e9.push(_1e7[i]);
}
return _1e9;
}});
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_1ec){
var _1ed=window,_1ee=2;
if(!dojo.lang.isFunction(func)){
_1ed=func;
func=_1ec;
_1ec=arguments[2];
_1ee++;
}
if(dojo.lang.isString(func)){
func=_1ed[func];
}
var args=[];
for(var i=_1ee;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_1ed,args);
},_1ec);
};
dojo.lang.clearTimeout=function(_1f1){
dojo.global().clearTimeout(_1f1);
};
dojo.lang.getNameInObj=function(ns,item){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===item){
return new String(x);
}
}
return null;
};
dojo.lang.shallowCopy=function(obj,deep){
var i,ret;
if(obj===null){
return null;
}
if(dojo.lang.isObject(obj)){
ret=new obj.constructor();
for(i in obj){
if(dojo.lang.isUndefined(ret[i])){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}
}else{
if(dojo.lang.isArray(obj)){
ret=[];
for(i=0;i<obj.length;i++){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}else{
ret=obj;
}
}
return ret;
};
dojo.lang.firstValued=function(){
for(var i=0;i<arguments.length;i++){
if(typeof arguments[i]!="undefined"){
return arguments[i];
}
}
return undefined;
};
dojo.lang.getObjPathValue=function(_1fa,_1fb,_1fc){
with(dojo.parseObjPath(_1fa,_1fb,_1fc)){
return dojo.evalProp(prop,obj,_1fc);
}
};
dojo.lang.setObjPathValue=function(_1fd,_1fe,_1ff,_200){
dojo.deprecated("dojo.lang.setObjPathValue","use dojo.parseObjPath and the '=' operator","0.6");
if(arguments.length<4){
_200=true;
}
with(dojo.parseObjPath(_1fd,_1ff,_200)){
if(obj&&(_200||(prop in obj))){
obj[prop]=_1fe;
}
}
};
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_201,_202,init,_204){
if((dojo.lang.isFunction(_204))||((!_204)&&(!dojo.lang.isFunction(init)))){
var temp=_204;
_204=init;
init=temp;
}
var _206=[];
if(dojo.lang.isArray(_202)){
_206=_202;
_202=_206.shift();
}
if(!init){
init=dojo.evalObjPath(_201,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_202?_202.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _202();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_206;
for(var i=0,l=_206.length;i<l;i++){
dojo.lang.extend(ctor,_206[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_201;
if(dojo.lang.isArray(_204)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_204));
}else{
dojo.lang.extend(ctor,(_204)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
var _20b=dojo.parseObjPath(_201,null,true);
_20b.obj[_20b.prop]=ctor;
return ctor;
};
dojo.lang.declare._makeConstructor=function(){
return function(){
var self=this._getPropContext();
var s=self.constructor.superclass;
if((s)&&(s.constructor)){
if(s.constructor==arguments.callee){
this._inherited("constructor",arguments);
}else{
this._contextMethod(s,"constructor",arguments);
}
}
var ms=(self.constructor.mixins)||([]);
for(var i=0,m;(m=ms[i]);i++){
(((m.prototype)&&(m.prototype.initializer))||(m)).apply(this,arguments);
}
if((!this.prototyping)&&(self.initializer)){
self.initializer.apply(this,arguments);
}
};
};
dojo.lang.declare._common={_getPropContext:function(){
return (this.___proto||this);
},_contextMethod:function(_211,_212,args){
var _214,_215=this.___proto;
this.___proto=_211;
try{
_214=_211[_212].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_215;
}
return _214;
},_inherited:function(prop,args){
var p=this._getPropContext();
do{
if((!p.constructor)||(!p.constructor.superclass)){
return;
}
p=p.constructor.superclass;
}while(!(prop in p));
return (dojo.lang.isFunction(p[prop])?this._contextMethod(p,prop,args):p[prop]);
},inherited:function(prop,args){
dojo.deprecated("'inherited' method is dangerous, do not up-call! 'inherited' is slated for removal in 0.5; name your super class (or use superclass property) instead.","0.5");
this._inherited(prop,args);
}};
dojo.declare=dojo.lang.declare;
dojo.provide("dojo.ns");
dojo.ns={namespaces:{},failed:{},loading:{},loaded:{},register:function(name,_21c,_21d,_21e){
if(!_21e||!this.namespaces[name]){
this.namespaces[name]=new dojo.ns.Ns(name,_21c,_21d);
}
},allow:function(name){
if(this.failed[name]){
return false;
}
if((djConfig.excludeNamespace)&&(dojo.lang.inArray(djConfig.excludeNamespace,name))){
return false;
}
return ((name==this.dojo)||(!djConfig.includeNamespace)||(dojo.lang.inArray(djConfig.includeNamespace,name)));
},get:function(name){
return this.namespaces[name];
},require:function(name){
var ns=this.namespaces[name];
if((ns)&&(this.loaded[name])){
return ns;
}
if(!this.allow(name)){
return false;
}
if(this.loading[name]){
dojo.debug("dojo.namespace.require: re-entrant request to load namespace \""+name+"\" must fail.");
return false;
}
var req=dojo.require;
this.loading[name]=true;
try{
if(name=="dojo"){
req("dojo.namespaces.dojo");
}else{
if(!dojo.hostenv.moduleHasPrefix(name)){
dojo.registerModulePath(name,"../"+name);
}
req([name,"manifest"].join("."),false,true);
}
if(!this.namespaces[name]){
this.failed[name]=true;
}
}
finally{
this.loading[name]=false;
}
return this.namespaces[name];
}};
dojo.ns.Ns=function(name,_225,_226){
this.name=name;
this.module=_225;
this.resolver=_226;
this._loaded=[];
this._failed=[];
};
dojo.ns.Ns.prototype.resolve=function(name,_228,_229){
if(!this.resolver||djConfig["skipAutoRequire"]){
return false;
}
var _22a=this.resolver(name,_228);
if((_22a)&&(!this._loaded[_22a])&&(!this._failed[_22a])){
var req=dojo.require;
req(_22a,false,true);
if(dojo.hostenv.findModule(_22a,false)){
this._loaded[_22a]=true;
}else{
if(!_229){
dojo.raise("dojo.ns.Ns.resolve: module '"+_22a+"' not found after loading via namespace '"+this.name+"'");
}
this._failed[_22a]=true;
}
}
return Boolean(this._loaded[_22a]);
};
dojo.registerNamespace=function(name,_22d,_22e){
dojo.ns.register.apply(dojo.ns,arguments);
};
dojo.registerNamespaceResolver=function(name,_230){
var n=dojo.ns.namespaces[name];
if(n){
n.resolver=_230;
}
};
dojo.registerNamespaceManifest=function(_232,path,name,_235,_236){
dojo.registerModulePath(name,path);
dojo.registerNamespace(name,_235,_236);
};
dojo.registerNamespace("dojo","dojo.widget");
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_238){
var dl=dojo.lang;
var ao={srcObj:dj_global,srcFunc:null,adviceObj:dj_global,adviceFunc:null,aroundObj:null,aroundFunc:null,adviceType:(args.length>2)?args[0]:"after",precedence:"last",once:false,delay:null,rate:0,adviceMsg:false};
switch(args.length){
case 0:
return;
case 1:
return;
case 2:
ao.srcFunc=args[0];
ao.adviceFunc=args[1];
break;
case 3:
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isFunction(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
var _23b=dl.nameAnonFunc(args[2],ao.adviceObj,_238);
ao.adviceFunc=_23b;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _23b=dl.nameAnonFunc(args[0],ao.srcObj,_238);
ao.srcFunc=_23b;
ao.adviceObj=args[1];
ao.adviceFunc=args[2];
}
}
}
}
break;
case 4:
if((dl.isObject(args[0]))&&(dl.isObject(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isString(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isFunction(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
var _23b=dl.nameAnonFunc(args[1],dj_global,_238);
ao.srcFunc=_23b;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _23b=dl.nameAnonFunc(args[3],dj_global,_238);
ao.adviceObj=dj_global;
ao.adviceFunc=_23b;
}else{
if(dl.isObject(args[1])){
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=dj_global;
ao.adviceFunc=args[3];
}else{
if(dl.isObject(args[2])){
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
ao.srcObj=ao.adviceObj=ao.aroundObj=dj_global;
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
ao.aroundFunc=args[3];
}
}
}
}
}
}
break;
case 6:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundFunc=args[5];
ao.aroundObj=dj_global;
break;
default:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundObj=args[5];
ao.aroundFunc=args[6];
ao.once=args[7];
ao.delay=args[8];
ao.rate=args[9];
ao.adviceMsg=args[10];
break;
}
if(dl.isFunction(ao.aroundFunc)){
var _23b=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_238);
ao.aroundFunc=_23b;
}
if(dl.isFunction(ao.srcFunc)){
ao.srcFunc=dl.getNameInObj(ao.srcObj,ao.srcFunc);
}
if(dl.isFunction(ao.adviceFunc)){
ao.adviceFunc=dl.getNameInObj(ao.adviceObj,ao.adviceFunc);
}
if((ao.aroundObj)&&(dl.isFunction(ao.aroundFunc))){
ao.aroundFunc=dl.getNameInObj(ao.aroundObj,ao.aroundFunc);
}
if(!ao.srcObj){
dojo.raise("bad srcObj for srcFunc: "+ao.srcFunc);
}
if(!ao.adviceObj){
dojo.raise("bad adviceObj for adviceFunc: "+ao.adviceFunc);
}
if(!ao.adviceFunc){
dojo.debug("bad adviceFunc for srcFunc: "+ao.srcFunc);
dojo.debugShallow(ao);
}
return ao;
}
this.connect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.connect(ao);
}
ao.srcFunc="onkeypress";
}
if(dojo.lang.isArray(ao.srcObj)&&ao.srcObj!=""){
var _23d={};
for(var x in ao){
_23d[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_23d.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_23d));
});
return mjps;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
if(ao.adviceFunc){
var mjp2=dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj,ao.adviceFunc);
}
mjp.kwAddAdvice(ao);
return mjp;
};
this.log=function(a1,a2){
var _245;
if((arguments.length==1)&&(typeof a1=="object")){
_245=a1;
}else{
_245={srcObj:a1,srcFunc:a2};
}
_245.adviceFunc=function(){
var _246=[];
for(var x=0;x<arguments.length;x++){
_246.push(arguments[x]);
}
dojo.debug("("+_245.srcObj+")."+_245.srcFunc,":",_246.join(", "));
};
this.kwConnect(_245);
};
this.connectBefore=function(){
var args=["before"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectAround=function(){
var args=["around"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.once=true;
return this.connect(ao);
};
this._kwConnectImpl=function(_24d,_24e){
var fn=(_24e)?"disconnect":"connect";
if(typeof _24d["srcFunc"]=="function"){
_24d.srcObj=_24d["srcObj"]||dj_global;
var _250=dojo.lang.nameAnonFunc(_24d.srcFunc,_24d.srcObj,true);
_24d.srcFunc=_250;
}
if(typeof _24d["adviceFunc"]=="function"){
_24d.adviceObj=_24d["adviceObj"]||dj_global;
var _250=dojo.lang.nameAnonFunc(_24d.adviceFunc,_24d.adviceObj,true);
_24d.adviceFunc=_250;
}
_24d.srcObj=_24d["srcObj"]||dj_global;
_24d.adviceObj=_24d["adviceObj"]||_24d["targetObj"]||dj_global;
_24d.adviceFunc=_24d["adviceFunc"]||_24d["targetFunc"];
return dojo.event[fn](_24d);
};
this.kwConnect=function(_251){
return this._kwConnectImpl(_251,false);
};
this.disconnect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(!ao.adviceFunc){
return;
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.disconnect(ao);
}
ao.srcFunc="onkeypress";
}
if(!ao.srcObj[ao.srcFunc]){
return null;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc,true);
mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
return mjp;
};
this.kwDisconnect=function(_254){
return this._kwConnectImpl(_254,true);
};
};
dojo.event.MethodInvocation=function(_255,obj,args){
this.jp_=_255;
this.object=obj;
this.args=[];
for(var x=0;x<args.length;x++){
this.args[x]=args[x];
}
this.around_index=-1;
};
dojo.event.MethodInvocation.prototype.proceed=function(){
this.around_index++;
if(this.around_index>=this.jp_.around.length){
return this.jp_.object[this.jp_.methodname].apply(this.jp_.object,this.args);
}else{
var ti=this.jp_.around[this.around_index];
var mobj=ti[0]||dj_global;
var meth=ti[1];
return mobj[meth].call(mobj,this);
}
};
dojo.event.MethodJoinPoint=function(obj,_25d){
this.object=obj||dj_global;
this.methodname=_25d;
this.methodfunc=this.object[_25d];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_25f){
if(!obj){
obj=dj_global;
}
if(!obj[_25f]){
obj[_25f]=function(){
};
if(!obj[_25f]){
dojo.raise("Cannot set do-nothing method on that object "+_25f);
}
}else{
if((!dojo.lang.isFunction(obj[_25f]))&&(!dojo.lang.isAlien(obj[_25f]))){
return null;
}
}
var _260=_25f+"$joinpoint";
var _261=_25f+"$joinpoint$method";
var _262=obj[_260];
if(!_262){
var _263=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_263=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_260,_261,_25f]);
}
}
var _264=obj[_25f].length;
obj[_261]=obj[_25f];
_262=obj[_260]=new dojo.event.MethodJoinPoint(obj,_261);
obj[_25f]=function(){
var args=[];
if((_263)&&(!arguments.length)){
var evt=null;
try{
if(obj.ownerDocument){
evt=obj.ownerDocument.parentWindow.event;
}else{
if(obj.documentElement){
evt=obj.documentElement.ownerDocument.parentWindow.event;
}else{
if(obj.event){
evt=obj.event;
}else{
evt=window.event;
}
}
}
}
catch(e){
evt=window.event;
}
if(evt){
args.push(dojo.event.browser.fixEvent(evt,this));
}
}else{
for(var x=0;x<arguments.length;x++){
if((x==0)&&(_263)&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x],this));
}else{
args.push(arguments[x]);
}
}
}
return _262.run.apply(_262,args);
};
obj[_25f].__preJoinArity=_264;
}
return _262;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _26a=[];
for(var x=0;x<args.length;x++){
_26a[x]=args[x];
}
var _26c=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _26e=marr[0]||dj_global;
var _26f=marr[1];
if(!_26e[_26f]){
dojo.raise("function \""+_26f+"\" does not exist on \""+_26e+"\"");
}
var _270=marr[2]||dj_global;
var _271=marr[3];
var msg=marr[6];
var _273;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _26e[_26f].apply(_26e,to.args);
}};
to.args=_26a;
var _275=parseInt(marr[4]);
var _276=((!isNaN(_275))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _279=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_26c(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_271){
_270[_271].call(_270,to);
}else{
if((_276)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_26e[_26f].call(_26e,to);
}else{
_26e[_26f].apply(_26e,args);
}
},_275);
}else{
if(msg){
_26e[_26f].call(_26e,to);
}else{
_26e[_26f].apply(_26e,args);
}
}
}
};
var _27c=function(){
if(this.squelch){
try{
return _26c.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _26c.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_27c);
}
var _27d;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_27d=mi.proceed();
}else{
if(this.methodfunc){
_27d=this.object[this.methodname].apply(this.object,args);
}
}
}
catch(e){
if(!this.squelch){
dojo.debug(e,"when calling",this.methodname,"on",this.object,"with arguments",args);
dojo.raise(e);
}
}
if((this["after"])&&(this.after.length>0)){
dojo.lang.forEach(this.after.concat(new Array()),_27c);
}
return (this.methodfunc)?_27d:null;
},getArr:function(kind){
var type="after";
if((typeof kind=="string")&&(kind.indexOf("before")!=-1)){
type="before";
}else{
if(kind=="around"){
type="around";
}
}
if(!this[type]){
this[type]=[];
}
return this[type];
},kwAddAdvice:function(args){
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"]);
},addAdvice:function(_282,_283,_284,_285,_286,_287,once,_289,rate,_28b){
var arr=this.getArr(_286);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_282,_283,_284,_285,_289,rate,_28b];
if(once){
if(this.hasAdvice(_282,_283,_286,arr)>=0){
return;
}
}
if(_287=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_28e,_28f,_290,arr){
if(!arr){
arr=this.getArr(_290);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _28f=="object")?(new String(_28f)).toString():_28f;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_28e)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_296,_297,_298,once){
var arr=this.getArr(_298);
var ind=this.hasAdvice(_296,_297,_298,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_296,_297,_298,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_29c){
if(!this.topics[_29c]){
this.topics[_29c]=new this.TopicImpl(_29c);
}
return this.topics[_29c];
};
this.registerPublisher=function(_29d,obj,_29f){
var _29d=this.getTopic(_29d);
_29d.registerPublisher(obj,_29f);
};
this.subscribe=function(_2a0,obj,_2a2){
var _2a0=this.getTopic(_2a0);
_2a0.subscribe(obj,_2a2);
};
this.unsubscribe=function(_2a3,obj,_2a5){
var _2a3=this.getTopic(_2a3);
_2a3.unsubscribe(obj,_2a5);
};
this.destroy=function(_2a6){
this.getTopic(_2a6).destroy();
delete this.topics[_2a6];
};
this.publishApply=function(_2a7,args){
var _2a7=this.getTopic(_2a7);
_2a7.sendMessage.apply(_2a7,args);
};
this.publish=function(_2a9,_2aa){
var _2a9=this.getTopic(_2a9);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_2a9.sendMessage.apply(_2a9,args);
};
};
dojo.event.topic.TopicImpl=function(_2ad){
this.topicName=_2ad;
this.subscribe=function(_2ae,_2af){
var tf=_2af||_2ae;
var to=(!_2af)?dj_global:_2ae;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_2b2,_2b3){
var tf=(!_2b3)?_2b2:_2b3;
var to=(!_2b3)?null:_2b2;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_2b6){
this._getJoinPoint().squelch=_2b6;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_2b7,_2b8){
dojo.event.connect(_2b7,_2b8,this,"sendMessage");
};
this.sendMessage=function(_2b9){
};
};
dojo.provide("dojo.event.browser");
dojo._ie_clobber=new function(){
this.clobberNodes=[];
function nukeProp(node,prop){
try{
node[prop]=null;
}
catch(e){
}
try{
delete node[prop];
}
catch(e){
}
try{
node.removeAttribute(prop);
}
catch(e){
}
}
this.clobber=function(_2bc){
var na;
var tna;
if(_2bc){
tna=_2bc.all||_2bc.getElementsByTagName("*");
na=[_2bc];
for(var x=0;x<tna.length;x++){
if(tna[x]["__doClobber__"]){
na.push(tna[x]);
}
}
}else{
try{
window.onload=null;
}
catch(e){
}
na=(this.clobberNodes.length)?this.clobberNodes:document.all;
}
tna=null;
var _2c0={};
for(var i=na.length-1;i>=0;i=i-1){
var el=na[i];
try{
if(el&&el["__clobberAttrs__"]){
for(var j=0;j<el.__clobberAttrs__.length;j++){
nukeProp(el,el.__clobberAttrs__[j]);
}
nukeProp(el,"__clobberAttrs__");
nukeProp(el,"__doClobber__");
}
}
catch(e){
}
}
na=null;
};
};
if(dojo.render.html.ie){
dojo.addOnUnload(function(){
dojo._ie_clobber.clobber();
try{
if((dojo["widget"])&&(dojo.widget["manager"])){
dojo.widget.manager.destroyAll();
}
}
catch(e){
}
if(dojo.widget){
for(var name in dojo.widget._templateCache){
if(dojo.widget._templateCache[name].node){
dojo.dom.destroyNode(dojo.widget._templateCache[name].node);
dojo.widget._templateCache[name].node=null;
delete dojo.widget._templateCache[name].node;
}
}
}
try{
window.onload=null;
}
catch(e){
}
try{
window.onunload=null;
}
catch(e){
}
dojo._ie_clobber.clobberNodes=[];
});
}
dojo.event.browser=new function(){
var _2c5=0;
this.normalizedEventName=function(_2c6){
switch(_2c6){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _2c6;
break;
default:
return _2c6.toLowerCase();
break;
}
};
this.clean=function(node){
if(dojo.render.html.ie){
dojo._ie_clobber.clobber(node);
}
};
this.addClobberNode=function(node){
if(!dojo.render.html.ie){
return;
}
if(!node["__doClobber__"]){
node.__doClobber__=true;
dojo._ie_clobber.clobberNodes.push(node);
node.__clobberAttrs__=[];
}
};
this.addClobberNodeAttrs=function(node,_2ca){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_2ca.length;x++){
node.__clobberAttrs__.push(_2ca[x]);
}
};
this.removeListener=function(node,_2cd,fp,_2cf){
if(!_2cf){
var _2cf=false;
}
_2cd=dojo.event.browser.normalizedEventName(_2cd);
if((_2cd=="onkey")||(_2cd=="key")){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_2cf);
}
_2cd="onkeypress";
}
if(_2cd.substr(0,2)=="on"){
_2cd=_2cd.substr(2);
}
if(node.removeEventListener){
node.removeEventListener(_2cd,fp,_2cf);
}
};
this.addListener=function(node,_2d1,fp,_2d3,_2d4){
if(!node){
return;
}
if(!_2d3){
var _2d3=false;
}
_2d1=dojo.event.browser.normalizedEventName(_2d1);
if((_2d1=="onkey")||(_2d1=="key")){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_2d3,_2d4);
}
_2d1="onkeypress";
}
if(_2d1.substr(0,2)!="on"){
_2d1="on"+_2d1;
}
if(!_2d4){
var _2d5=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_2d3){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_2d5=fp;
}
if(node.addEventListener){
node.addEventListener(_2d1.substr(2),_2d5,_2d3);
return _2d5;
}else{
if(typeof node[_2d1]=="function"){
var _2d8=node[_2d1];
node[_2d1]=function(e){
_2d8(e);
return _2d5(e);
};
}else{
node[_2d1]=_2d5;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_2d1]);
}
return _2d5;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(obj)&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_2db,_2dc){
if(typeof _2db!="function"){
dojo.raise("listener not a function: "+_2db);
}
dojo.event.browser.currentEvent.currentTarget=_2dc;
return _2db.call(_2dc,dojo.event.browser.currentEvent);
};
this._stopPropagation=function(){
dojo.event.browser.currentEvent.cancelBubble=true;
};
this._preventDefault=function(){
dojo.event.browser.currentEvent.returnValue=false;
};
this.keys={KEY_BACKSPACE:8,KEY_TAB:9,KEY_CLEAR:12,KEY_ENTER:13,KEY_SHIFT:16,KEY_CTRL:17,KEY_ALT:18,KEY_PAUSE:19,KEY_CAPS_LOCK:20,KEY_ESCAPE:27,KEY_SPACE:32,KEY_PAGE_UP:33,KEY_PAGE_DOWN:34,KEY_END:35,KEY_HOME:36,KEY_LEFT_ARROW:37,KEY_UP_ARROW:38,KEY_RIGHT_ARROW:39,KEY_DOWN_ARROW:40,KEY_INSERT:45,KEY_DELETE:46,KEY_HELP:47,KEY_LEFT_WINDOW:91,KEY_RIGHT_WINDOW:92,KEY_SELECT:93,KEY_NUMPAD_0:96,KEY_NUMPAD_1:97,KEY_NUMPAD_2:98,KEY_NUMPAD_3:99,KEY_NUMPAD_4:100,KEY_NUMPAD_5:101,KEY_NUMPAD_6:102,KEY_NUMPAD_7:103,KEY_NUMPAD_8:104,KEY_NUMPAD_9:105,KEY_NUMPAD_MULTIPLY:106,KEY_NUMPAD_PLUS:107,KEY_NUMPAD_ENTER:108,KEY_NUMPAD_MINUS:109,KEY_NUMPAD_PERIOD:110,KEY_NUMPAD_DIVIDE:111,KEY_F1:112,KEY_F2:113,KEY_F3:114,KEY_F4:115,KEY_F5:116,KEY_F6:117,KEY_F7:118,KEY_F8:119,KEY_F9:120,KEY_F10:121,KEY_F11:122,KEY_F12:123,KEY_F13:124,KEY_F14:125,KEY_F15:126,KEY_NUM_LOCK:144,KEY_SCROLL_LOCK:145};
this.revKeys=[];
for(var key in this.keys){
this.revKeys[this.keys[key]]=key;
}
this.fixEvent=function(evt,_2df){
if(!evt){
if(window["event"]){
evt=window.event;
}
}
if((evt["type"])&&(evt["type"].indexOf("key")==0)){
evt.keys=this.revKeys;
for(var key in this.keys){
evt[key]=this.keys[key];
}
if(evt["type"]=="keydown"&&dojo.render.html.ie){
switch(evt.keyCode){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_LEFT_WINDOW:
case evt.KEY_RIGHT_WINDOW:
case evt.KEY_SELECT:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
case evt.KEY_NUMPAD_0:
case evt.KEY_NUMPAD_1:
case evt.KEY_NUMPAD_2:
case evt.KEY_NUMPAD_3:
case evt.KEY_NUMPAD_4:
case evt.KEY_NUMPAD_5:
case evt.KEY_NUMPAD_6:
case evt.KEY_NUMPAD_7:
case evt.KEY_NUMPAD_8:
case evt.KEY_NUMPAD_9:
case evt.KEY_NUMPAD_PERIOD:
break;
case evt.KEY_NUMPAD_MULTIPLY:
case evt.KEY_NUMPAD_PLUS:
case evt.KEY_NUMPAD_ENTER:
case evt.KEY_NUMPAD_MINUS:
case evt.KEY_NUMPAD_DIVIDE:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
case evt.KEY_PAGE_UP:
case evt.KEY_PAGE_DOWN:
case evt.KEY_END:
case evt.KEY_HOME:
case evt.KEY_LEFT_ARROW:
case evt.KEY_UP_ARROW:
case evt.KEY_RIGHT_ARROW:
case evt.KEY_DOWN_ARROW:
case evt.KEY_INSERT:
case evt.KEY_DELETE:
case evt.KEY_F1:
case evt.KEY_F2:
case evt.KEY_F3:
case evt.KEY_F4:
case evt.KEY_F5:
case evt.KEY_F6:
case evt.KEY_F7:
case evt.KEY_F8:
case evt.KEY_F9:
case evt.KEY_F10:
case evt.KEY_F11:
case evt.KEY_F12:
case evt.KEY_F12:
case evt.KEY_F13:
case evt.KEY_F14:
case evt.KEY_F15:
case evt.KEY_CLEAR:
case evt.KEY_HELP:
evt.key=evt.keyCode;
break;
default:
if(evt.ctrlKey||evt.altKey){
var _2e1=evt.keyCode;
if(_2e1>=65&&_2e1<=90&&evt.shiftKey==false){
_2e1+=32;
}
if(_2e1>=1&&_2e1<=26&&evt.ctrlKey){
_2e1+=96;
}
evt.key=String.fromCharCode(_2e1);
}
}
}else{
if(evt["type"]=="keypress"){
if(dojo.render.html.opera){
if(evt.which==0){
evt.key=evt.keyCode;
}else{
if(evt.which>0){
switch(evt.which){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
evt.key=evt.which;
break;
default:
var _2e1=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_2e1+=32;
}
evt.key=String.fromCharCode(_2e1);
}
}
}
}else{
if(dojo.render.html.ie){
if(!evt.ctrlKey&&!evt.altKey&&evt.keyCode>=evt.KEY_SPACE){
evt.key=String.fromCharCode(evt.keyCode);
}
}else{
if(dojo.render.html.safari){
switch(evt.keyCode){
case 25:
evt.key=evt.KEY_TAB;
evt.shift=true;
break;
case 63232:
evt.key=evt.KEY_UP_ARROW;
break;
case 63233:
evt.key=evt.KEY_DOWN_ARROW;
break;
case 63234:
evt.key=evt.KEY_LEFT_ARROW;
break;
case 63235:
evt.key=evt.KEY_RIGHT_ARROW;
break;
case 63236:
evt.key=evt.KEY_F1;
break;
case 63237:
evt.key=evt.KEY_F2;
break;
case 63238:
evt.key=evt.KEY_F3;
break;
case 63239:
evt.key=evt.KEY_F4;
break;
case 63240:
evt.key=evt.KEY_F5;
break;
case 63241:
evt.key=evt.KEY_F6;
break;
case 63242:
evt.key=evt.KEY_F7;
break;
case 63243:
evt.key=evt.KEY_F8;
break;
case 63244:
evt.key=evt.KEY_F9;
break;
case 63245:
evt.key=evt.KEY_F10;
break;
case 63246:
evt.key=evt.KEY_F11;
break;
case 63247:
evt.key=evt.KEY_F12;
break;
case 63250:
evt.key=evt.KEY_PAUSE;
break;
case 63272:
evt.key=evt.KEY_DELETE;
break;
case 63273:
evt.key=evt.KEY_HOME;
break;
case 63275:
evt.key=evt.KEY_END;
break;
case 63276:
evt.key=evt.KEY_PAGE_UP;
break;
case 63277:
evt.key=evt.KEY_PAGE_DOWN;
break;
case 63302:
evt.key=evt.KEY_INSERT;
break;
case 63248:
case 63249:
case 63289:
break;
default:
evt.key=evt.charCode>=evt.KEY_SPACE?String.fromCharCode(evt.charCode):evt.keyCode;
}
}else{
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}
}
}
}
}
if(dojo.render.html.ie){
if(!evt.target){
evt.target=evt.srcElement;
}
if(!evt.currentTarget){
evt.currentTarget=(_2df?_2df:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _2e3=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_2e3.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_2e3.scrollTop||0);
}
if(evt.type=="mouseover"){
evt.relatedTarget=evt.fromElement;
}
if(evt.type=="mouseout"){
evt.relatedTarget=evt.toElement;
}
this.currentEvent=evt;
evt.callListener=this.callListener;
evt.stopPropagation=this._stopPropagation;
evt.preventDefault=this._preventDefault;
}
return evt;
};
this.stopEvent=function(evt){
if(window.event){
evt.cancelBubble=true;
evt.returnValue=false;
}else{
evt.preventDefault();
evt.stopPropagation();
}
};
};
dojo.provide("dojo.event.*");
dojo.provide("dojo.widget.Manager");
dojo.widget.manager=new function(){
this.widgets=[];
this.widgetIds=[];
this.topWidgets={};
var _2e5={};
var _2e6=[];
this.getUniqueId=function(_2e7){
var _2e8;
do{
_2e8=_2e7+"_"+(_2e5[_2e7]!=undefined?++_2e5[_2e7]:_2e5[_2e7]=0);
}while(this.getWidgetById(_2e8));
return _2e8;
};
this.add=function(_2e9){
this.widgets.push(_2e9);
if(!_2e9.extraArgs["id"]){
_2e9.extraArgs["id"]=_2e9.extraArgs["ID"];
}
if(_2e9.widgetId==""){
if(_2e9["id"]){
_2e9.widgetId=_2e9["id"];
}else{
if(_2e9.extraArgs["id"]){
_2e9.widgetId=_2e9.extraArgs["id"];
}else{
_2e9.widgetId=this.getUniqueId(_2e9.ns+"_"+_2e9.widgetType);
}
}
}
if(this.widgetIds[_2e9.widgetId]){
dojo.debug("widget ID collision on ID: "+_2e9.widgetId);
}
this.widgetIds[_2e9.widgetId]=_2e9;
};
this.destroyAll=function(){
for(var x=this.widgets.length-1;x>=0;x--){
try{
this.widgets[x].destroy(true);
delete this.widgets[x];
}
catch(e){
}
}
};
this.remove=function(_2eb){
if(dojo.lang.isNumber(_2eb)){
var tw=this.widgets[_2eb].widgetId;
delete this.widgetIds[tw];
this.widgets.splice(_2eb,1);
}else{
this.removeById(_2eb);
}
};
this.removeById=function(id){
if(!dojo.lang.isString(id)){
id=id["widgetId"];
if(!id){
dojo.debug("invalid widget or id passed to removeById");
return;
}
}
for(var i=0;i<this.widgets.length;i++){
if(this.widgets[i].widgetId==id){
this.remove(i);
break;
}
}
};
this.getWidgetById=function(id){
if(dojo.lang.isString(id)){
return this.widgetIds[id];
}
return id;
};
this.getWidgetsByType=function(type){
var lt=type.toLowerCase();
var _2f2=(type.indexOf(":")<0?function(x){
return x.widgetType.toLowerCase();
}:function(x){
return x.getNamespacedType();
});
var ret=[];
dojo.lang.forEach(this.widgets,function(x){
if(_2f2(x)==lt){
ret.push(x);
}
});
return ret;
};
this.getWidgetsByFilter=function(_2f7,_2f8){
var ret=[];
dojo.lang.every(this.widgets,function(x){
if(_2f7(x)){
ret.push(x);
if(_2f8){
return false;
}
}
return true;
});
return (_2f8?ret[0]:ret);
};
this.getAllWidgets=function(){
return this.widgets.concat();
};
this.getWidgetByNode=function(node){
var w=this.getAllWidgets();
node=dojo.byId(node);
for(var i=0;i<w.length;i++){
if(w[i].domNode==node){
return w[i];
}
}
return null;
};
this.byId=this.getWidgetById;
this.byType=this.getWidgetsByType;
this.byFilter=this.getWidgetsByFilter;
this.byNode=this.getWidgetByNode;
var _2fe={};
var _2ff=["dojo.widget"];
for(var i=0;i<_2ff.length;i++){
_2ff[_2ff[i]]=true;
}
this.registerWidgetPackage=function(_301){
if(!_2ff[_301]){
_2ff[_301]=true;
_2ff.push(_301);
}
};
this.getWidgetPackageList=function(){
return dojo.lang.map(_2ff,function(elt){
return (elt!==true?elt:undefined);
});
};
this.getImplementation=function(_303,_304,_305,ns){
var impl=this.getImplementationName(_303,ns);
if(impl){
var ret=_304?new impl(_304):new impl();
return ret;
}
};
function buildPrefixCache(){
for(var _309 in dojo.render){
if(dojo.render[_309]["capable"]===true){
var _30a=dojo.render[_309].prefixes;
for(var i=0;i<_30a.length;i++){
_2e6.push(_30a[i].toLowerCase());
}
}
}
}
var _30c=function(_30d,_30e){
if(!_30e){
return null;
}
for(var i=0,l=_2e6.length,_311;i<=l;i++){
_311=(i<l?_30e[_2e6[i]]:_30e);
if(!_311){
continue;
}
for(var name in _311){
if(name.toLowerCase()==_30d){
return _311[name];
}
}
}
return null;
};
var _313=function(_314,_315){
var _316=dojo.evalObjPath(_315,false);
return (_316?_30c(_314,_316):null);
};
this.getImplementationName=function(_317,ns){
var _319=_317.toLowerCase();
ns=ns||"dojo";
var imps=_2fe[ns]||(_2fe[ns]={});
var impl=imps[_319];
if(impl){
return impl;
}
if(!_2e6.length){
buildPrefixCache();
}
var _31c=dojo.ns.get(ns);
if(!_31c){
dojo.ns.register(ns,ns+".widget");
_31c=dojo.ns.get(ns);
}
if(_31c){
_31c.resolve(_317);
}
impl=_313(_319,_31c.module);
if(impl){
return (imps[_319]=impl);
}
_31c=dojo.ns.require(ns);
if((_31c)&&(_31c.resolver)){
_31c.resolve(_317);
impl=_313(_319,_31c.module);
if(impl){
return (imps[_319]=impl);
}
}
dojo.deprecated("dojo.widget.Manager.getImplementationName","Could not locate widget implementation for \""+_317+"\" in \""+_31c.module+"\" registered to namespace \""+_31c.name+"\". "+"Developers must specify correct namespaces for all non-Dojo widgets","0.5");
for(var i=0;i<_2ff.length;i++){
impl=_313(_319,_2ff[i]);
if(impl){
return (imps[_319]=impl);
}
}
throw new Error("Could not locate widget implementation for \""+_317+"\" in \""+_31c.module+"\" registered to namespace \""+_31c.name+"\"");
};
this.resizing=false;
this.onWindowResized=function(){
if(this.resizing){
return;
}
try{
this.resizing=true;
for(var id in this.topWidgets){
var _31f=this.topWidgets[id];
if(_31f.checkSize){
_31f.checkSize();
}
}
}
catch(e){
}
finally{
this.resizing=false;
}
};
if(typeof window!="undefined"){
dojo.addOnLoad(this,"onWindowResized");
dojo.event.connect(window,"onresize",this,"onWindowResized");
}
};
(function(){
var dw=dojo.widget;
var dwm=dw.manager;
var h=dojo.lang.curry(dojo.lang,"hitch",dwm);
var g=function(_324,_325){
dw[(_325||_324)]=h(_324);
};
g("add","addWidget");
g("destroyAll","destroyAllWidgets");
g("remove","removeWidget");
g("removeById","removeWidgetById");
g("getWidgetById");
g("getWidgetById","byId");
g("getWidgetsByType");
g("getWidgetsByFilter");
g("getWidgetsByType","byType");
g("getWidgetsByFilter","byFilter");
g("getWidgetByNode","byNode");
dw.all=function(n){
var _327=dwm.getAllWidgets.apply(dwm,arguments);
if(arguments.length>0){
return _327[n];
}
return _327;
};
g("registerWidgetPackage");
g("getImplementation","getWidgetImplementation");
g("getImplementationName","getWidgetImplementationName");
dw.widgets=dwm.widgets;
dw.widgetIds=dwm.widgetIds;
dw.root=dwm.root;
})();
dojo.provide("dojo.uri.Uri");
dojo.uri=new function(){
this.dojoUri=function(uri){
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(),uri);
};
this.moduleUri=function(_329,uri){
var loc=dojo.hostenv.getModuleSymbols(_329).join("/");
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri()+loc,uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _32e=new dojo.uri.Uri(arguments[i].toString());
var _32f=new dojo.uri.Uri(uri.toString());
if((_32e.path=="")&&(_32e.scheme==null)&&(_32e.authority==null)&&(_32e.query==null)){
if(_32e.fragment!=null){
_32f.fragment=_32e.fragment;
}
_32e=_32f;
}else{
if(_32e.scheme==null){
_32e.scheme=_32f.scheme;
if(_32e.authority==null){
_32e.authority=_32f.authority;
if(_32e.path.charAt(0)!="/"){
var path=_32f.path.substring(0,_32f.path.lastIndexOf("/")+1)+_32e.path;
var segs=path.split("/");
for(var j=0;j<segs.length;j++){
if(segs[j]=="."){
if(j==segs.length-1){
segs[j]="";
}else{
segs.splice(j,1);
j--;
}
}else{
if(j>0&&!(j==1&&segs[0]=="")&&segs[j]==".."&&segs[j-1]!=".."){
if(j==segs.length-1){
segs.splice(j,1);
segs[j-1]="";
}else{
segs.splice(j-1,2);
j-=2;
}
}
}
}
_32e.path=segs.join("/");
}
}
}
}
uri="";
if(_32e.scheme!=null){
uri+=_32e.scheme+":";
}
if(_32e.authority!=null){
uri+="//"+_32e.authority;
}
uri+=_32e.path;
if(_32e.query!=null){
uri+="?"+_32e.query;
}
if(_32e.fragment!=null){
uri+="#"+_32e.fragment;
}
}
this.uri=uri.toString();
var _333="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_333));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_333="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_333));
this.user=r[3]||null;
this.password=r[4]||null;
this.host=r[5];
this.port=r[7]||null;
}
this.toString=function(){
return this.uri;
};
};
};
dojo.provide("dojo.uri.*");
dojo.provide("dojo.html.common");
dojo.lang.mixin(dojo.html,dojo.dom);
dojo.html.body=function(){
dojo.deprecated("dojo.html.body() moved to dojo.body()","0.5");
return dojo.body();
};
dojo.html.getEventTarget=function(evt){
if(!evt){
evt=dojo.global().event||{};
}
var t=(evt.srcElement?evt.srcElement:(evt.target?evt.target:null));
while((t)&&(t.nodeType!=1)){
t=t.parentNode;
}
return t;
};
dojo.html.getViewport=function(){
var _337=dojo.global();
var _338=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_338.documentElement.clientWidth;
h=_337.innerHeight;
}else{
if(!dojo.render.html.opera&&_337.innerWidth){
w=_337.innerWidth;
h=_337.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_338,"documentElement.clientWidth")){
var w2=_338.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_338.documentElement.clientHeight;
}else{
if(dojo.body().clientWidth){
w=dojo.body().clientWidth;
h=dojo.body().clientHeight;
}
}
}
}
return {width:w,height:h};
};
dojo.html.getScroll=function(){
var _33c=dojo.global();
var _33d=dojo.doc();
var top=_33c.pageYOffset||_33d.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_33c.pageXOffset||_33d.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _342=dojo.doc();
var _343=dojo.byId(node);
type=type.toLowerCase();
while((_343)&&(_343.nodeName.toLowerCase()!=type)){
if(_343==(_342["body"]||_342["documentElement"])){
return null;
}
_343=_343.parentNode;
}
return _343;
};
dojo.html.getAttribute=function(node,attr){
node=dojo.byId(node);
if((!node)||(!node.getAttribute)){
return null;
}
var ta=typeof attr=="string"?attr:new String(attr);
var v=node.getAttribute(ta.toUpperCase());
if((v)&&(typeof v=="string")&&(v!="")){
return v;
}
if(v&&v.value){
return v.value;
}
if((node.getAttributeNode)&&(node.getAttributeNode(ta))){
return (node.getAttributeNode(ta)).value;
}else{
if(node.getAttribute(ta)){
return node.getAttribute(ta);
}else{
if(node.getAttribute(ta.toLowerCase())){
return node.getAttribute(ta.toLowerCase());
}
}
}
return null;
};
dojo.html.hasAttribute=function(node,attr){
return dojo.html.getAttribute(dojo.byId(node),attr)?true:false;
};
dojo.html.getCursorPosition=function(e){
e=e||dojo.global().event;
var _34b={x:0,y:0};
if(e.pageX||e.pageY){
_34b.x=e.pageX;
_34b.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_34b.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_34b.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _34b;
};
dojo.html.isTag=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName.toLowerCase()==String(arguments[i]).toLowerCase()){
return String(arguments[i]).toLowerCase();
}
}
}
return "";
};
if(dojo.render.html.ie&&!dojo.render.html.ie70){
if(window.location.href.substr(0,6).toLowerCase()!="https:"){
(function(){
var _350=dojo.doc().createElement("script");
_350.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_350);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_353,_354,args,_356,_357){
dojo.deprecated("dojo.html."+_353,"replaced by dojo.html."+_354+"("+(_356?"node, {"+_356+": "+_356+"}":"")+")"+(_357?"."+_357:""),"0.5");
var _358=[];
if(_356){
var _359={};
_359[_356]=args[1];
_358.push(args[0]);
_358.push(_359);
}else{
_358=args;
}
var ret=dojo.html[_354].apply(dojo.html,args);
if(_357){
return ret[_357];
}else{
return ret;
}
};
dojo.html.getViewportWidth=function(){
return dojo.html._callDeprecated("getViewportWidth","getViewport",arguments,null,"width");
};
dojo.html.getViewportHeight=function(){
return dojo.html._callDeprecated("getViewportHeight","getViewport",arguments,null,"height");
};
dojo.html.getViewportSize=function(){
return dojo.html._callDeprecated("getViewportSize","getViewport",arguments);
};
dojo.html.getScrollTop=function(){
return dojo.html._callDeprecated("getScrollTop","getScroll",arguments,null,"top");
};
dojo.html.getScrollLeft=function(){
return dojo.html._callDeprecated("getScrollLeft","getScroll",arguments,null,"left");
};
dojo.html.getScrollOffset=function(){
return dojo.html._callDeprecated("getScrollOffset","getScroll",arguments,null,"offset");
};
dojo.provide("dojo.a11y");
dojo.a11y={imgPath:dojo.uri.dojoUri("src/widget/templates/images"),doAccessibleCheck:true,accessible:null,checkAccessible:function(){
if(this.accessible===null){
this.accessible=false;
if(this.doAccessibleCheck==true){
this.accessible=this.testAccessible();
}
}
return this.accessible;
},testAccessible:function(){
this.accessible=false;
if(dojo.render.html.ie||dojo.render.html.mozilla){
var div=document.createElement("div");
div.style.backgroundImage="url(\""+this.imgPath+"/tab_close.gif\")";
dojo.body().appendChild(div);
var _35c=null;
if(window.getComputedStyle){
var _35d=getComputedStyle(div,"");
_35c=_35d.getPropertyValue("background-image");
}else{
_35c=div.currentStyle.backgroundImage;
}
var _35e=false;
if(_35c!=null&&(_35c=="none"||_35c=="url(invalid-url:)")){
this.accessible=true;
}
dojo.body().removeChild(div);
}
return this.accessible;
},setCheckAccessible:function(_35f){
this.doAccessibleCheck=_35f;
},setAccessibleMode:function(){
if(this.accessible===null){
if(this.checkAccessible()){
dojo.render.html.prefixes.unshift("a11y");
}
}
return this.accessible;
}};
dojo.provide("dojo.widget.Widget");
dojo.declare("dojo.widget.Widget",null,function(){
this.children=[];
this.extraArgs={};
},{parent:null,isTopLevel:false,disabled:false,isContainer:false,widgetId:"",widgetType:"Widget",ns:"dojo",getNamespacedType:function(){
return (this.ns?this.ns+":"+this.widgetType:this.widgetType).toLowerCase();
},toString:function(){
return "[Widget "+this.getNamespacedType()+", "+(this.widgetId||"NO ID")+"]";
},repr:function(){
return this.toString();
},enable:function(){
this.disabled=false;
},disable:function(){
this.disabled=true;
},onResized:function(){
this.notifyChildrenOfResize();
},notifyChildrenOfResize:function(){
for(var i=0;i<this.children.length;i++){
var _361=this.children[i];
if(_361.onResized){
_361.onResized();
}
}
},create:function(args,_363,_364,ns){
if(ns){
this.ns=ns;
}
this.satisfyPropertySets(args,_363,_364);
this.mixInProperties(args,_363,_364);
this.postMixInProperties(args,_363,_364);
dojo.widget.manager.add(this);
this.buildRendering(args,_363,_364);
this.initialize(args,_363,_364);
this.postInitialize(args,_363,_364);
this.postCreate(args,_363,_364);
return this;
},destroy:function(_366){
if(this.parent){
this.parent.removeChild(this);
}
this.destroyChildren();
this.uninitialize();
this.destroyRendering(_366);
dojo.widget.manager.removeById(this.widgetId);
},destroyChildren:function(){
var _367;
var i=0;
while(this.children.length>i){
_367=this.children[i];
if(_367 instanceof dojo.widget.Widget){
this.removeChild(_367);
_367.destroy();
continue;
}
i++;
}
},getChildrenOfType:function(type,_36a){
var ret=[];
var _36c=dojo.lang.isFunction(type);
if(!_36c){
type=type.toLowerCase();
}
for(var x=0;x<this.children.length;x++){
if(_36c){
if(this.children[x] instanceof type){
ret.push(this.children[x]);
}
}else{
if(this.children[x].widgetType.toLowerCase()==type){
ret.push(this.children[x]);
}
}
if(_36a){
ret=ret.concat(this.children[x].getChildrenOfType(type,_36a));
}
}
return ret;
},getDescendants:function(){
var _36e=[];
var _36f=[this];
var elem;
while((elem=_36f.pop())){
_36e.push(elem);
if(elem.children){
dojo.lang.forEach(elem.children,function(elem){
_36f.push(elem);
});
}
}
return _36e;
},isFirstChild:function(){
return this===this.parent.children[0];
},isLastChild:function(){
return this===this.parent.children[this.parent.children.length-1];
},satisfyPropertySets:function(args){
return args;
},mixInProperties:function(args,frag){
if((args["fastMixIn"])||(frag["fastMixIn"])){
for(var x in args){
this[x]=args[x];
}
return;
}
var _376;
var _377=dojo.widget.lcArgsCache[this.widgetType];
if(_377==null){
_377={};
for(var y in this){
_377[((new String(y)).toLowerCase())]=y;
}
dojo.widget.lcArgsCache[this.widgetType]=_377;
}
var _379={};
for(var x in args){
if(!this[x]){
var y=_377[(new String(x)).toLowerCase()];
if(y){
args[y]=args[x];
x=y;
}
}
if(_379[x]){
continue;
}
_379[x]=true;
if((typeof this[x])!=(typeof _376)){
if(typeof args[x]!="string"){
this[x]=args[x];
}else{
if(dojo.lang.isString(this[x])){
this[x]=args[x];
}else{
if(dojo.lang.isNumber(this[x])){
this[x]=new Number(args[x]);
}else{
if(dojo.lang.isBoolean(this[x])){
this[x]=(args[x].toLowerCase()=="false")?false:true;
}else{
if(dojo.lang.isFunction(this[x])){
if(args[x].search(/[^\w\.]+/i)==-1){
this[x]=dojo.evalObjPath(args[x],false);
}else{
var tn=dojo.lang.nameAnonFunc(new Function(args[x]),this);
dojo.event.kwConnect({srcObj:this,srcFunc:x,adviceObj:this,adviceFunc:tn});
}
}else{
if(dojo.lang.isArray(this[x])){
this[x]=args[x].split(";");
}else{
if(this[x] instanceof Date){
this[x]=new Date(Number(args[x]));
}else{
if(typeof this[x]=="object"){
if(this[x] instanceof dojo.uri.Uri){
this[x]=dojo.uri.dojoUri(args[x]);
}else{
var _37b=args[x].split(";");
for(var y=0;y<_37b.length;y++){
var si=_37b[y].indexOf(":");
if((si!=-1)&&(_37b[y].length>si)){
this[x][_37b[y].substr(0,si).replace(/^\s+|\s+$/g,"")]=_37b[y].substr(si+1);
}
}
}
}else{
this[x]=args[x];
}
}
}
}
}
}
}
}
}else{
this.extraArgs[x.toLowerCase()]=args[x];
}
}
},postMixInProperties:function(args,frag,_37f){
},initialize:function(args,frag,_382){
return false;
},postInitialize:function(args,frag,_385){
return false;
},postCreate:function(args,frag,_388){
return false;
},uninitialize:function(){
return false;
},buildRendering:function(args,frag,_38b){
dojo.unimplemented("dojo.widget.Widget.buildRendering, on "+this.toString()+", ");
return false;
},destroyRendering:function(){
dojo.unimplemented("dojo.widget.Widget.destroyRendering");
return false;
},addedTo:function(_38c){
},addChild:function(_38d){
dojo.unimplemented("dojo.widget.Widget.addChild");
return false;
},removeChild:function(_38e){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_38e){
this.children.splice(x,1);
_38e.parent=null;
break;
}
}
return _38e;
},getPreviousSibling:function(){
var idx=this.getParentIndex();
if(idx<=0){
return null;
}
return this.parent.children[idx-1];
},getSiblings:function(){
return this.parent.children;
},getParentIndex:function(){
return dojo.lang.indexOf(this.parent.children,this,true);
},getNextSibling:function(){
var idx=this.getParentIndex();
if(idx==this.parent.children.length-1){
return null;
}
if(idx<0){
return null;
}
return this.parent.children[idx+1];
}});
dojo.widget.lcArgsCache={};
dojo.widget.tags={};
dojo.widget.tags.addParseTreeHandler=function(type){
dojo.deprecated("addParseTreeHandler",". ParseTreeHandlers are now reserved for components. Any unfiltered DojoML tag without a ParseTreeHandler is assumed to be a widget","0.5");
};
dojo.widget.tags["dojo:propertyset"]=function(_393,_394,_395){
var _396=_394.parseProperties(_393["dojo:propertyset"]);
};
dojo.widget.tags["dojo:connect"]=function(_397,_398,_399){
var _39a=_398.parseProperties(_397["dojo:connect"]);
};
dojo.widget.buildWidgetFromParseTree=function(type,frag,_39d,_39e,_39f,_3a0){
dojo.a11y.setAccessibleMode();
var _3a1=type.split(":");
_3a1=(_3a1.length==2)?_3a1[1]:type;
var _3a2=_3a0||_39d.parseProperties(frag[frag["ns"]+":"+_3a1]);
var _3a3=dojo.widget.manager.getImplementation(_3a1,null,null,frag["ns"]);
if(!_3a3){
throw new Error("cannot find \""+type+"\" widget");
}else{
if(!_3a3.create){
throw new Error("\""+type+"\" widget object has no \"create\" method and does not appear to implement *Widget");
}
}
_3a2["dojoinsertionindex"]=_39f;
var ret=_3a3.create(_3a2,frag,_39e,frag["ns"]);
return ret;
};
dojo.widget.defineWidget=function(_3a5,_3a6,_3a7,init,_3a9){
if(dojo.lang.isString(arguments[3])){
dojo.widget._defineWidget(arguments[0],arguments[3],arguments[1],arguments[4],arguments[2]);
}else{
var args=[arguments[0]],p=3;
if(dojo.lang.isString(arguments[1])){
args.push(arguments[1],arguments[2]);
}else{
args.push("",arguments[1]);
p=2;
}
if(dojo.lang.isFunction(arguments[p])){
args.push(arguments[p],arguments[p+1]);
}else{
args.push(null,arguments[p]);
}
dojo.widget._defineWidget.apply(this,args);
}
};
dojo.widget.defineWidget.renderers="html|svg|vml";
dojo.widget._defineWidget=function(_3ac,_3ad,_3ae,init,_3b0){
var _3b1=_3ac.split(".");
var type=_3b1.pop();
var regx="\\.("+(_3ad?_3ad+"|":"")+dojo.widget.defineWidget.renderers+")\\.";
var r=_3ac.search(new RegExp(regx));
_3b1=(r<0?_3b1.join("."):_3ac.substr(0,r));
dojo.widget.manager.registerWidgetPackage(_3b1);
var pos=_3b1.indexOf(".");
var _3b6=(pos>-1)?_3b1.substring(0,pos):_3b1;
_3b0=(_3b0)||{};
_3b0.widgetType=type;
if((!init)&&(_3b0["classConstructor"])){
init=_3b0.classConstructor;
delete _3b0.classConstructor;
}
dojo.declare(_3ac,_3ae,init,_3b0);
};
dojo.provide("dojo.widget.Parse");
dojo.widget.Parse=function(_3b7){
this.propertySetsList=[];
this.fragment=_3b7;
this.createComponents=function(frag,_3b9){
var _3ba=[];
var _3bb=false;
try{
if(frag&&frag.tagName&&(frag!=frag.nodeRef)){
var _3bc=dojo.widget.tags;
var tna=String(frag.tagName).split(";");
for(var x=0;x<tna.length;x++){
var ltn=tna[x].replace(/^\s+|\s+$/g,"").toLowerCase();
frag.tagName=ltn;
var ret;
if(_3bc[ltn]){
_3bb=true;
ret=_3bc[ltn](frag,this,_3b9,frag.index);
_3ba.push(ret);
}else{
if(ltn.indexOf(":")==-1){
ltn="dojo:"+ltn;
}
ret=dojo.widget.buildWidgetFromParseTree(ltn,frag,this,_3b9,frag.index);
if(ret){
_3bb=true;
_3ba.push(ret);
}
}
}
}
}
catch(e){
dojo.debug("dojo.widget.Parse: error:",e);
}
if(!_3bb){
_3ba=_3ba.concat(this.createSubComponents(frag,_3b9));
}
return _3ba;
};
this.createSubComponents=function(_3c1,_3c2){
var frag,_3c4=[];
for(var item in _3c1){
frag=_3c1[item];
if(frag&&typeof frag=="object"&&(frag!=_3c1.nodeRef)&&(frag!=_3c1.tagName)&&(!dojo.dom.isNode(frag))){
_3c4=_3c4.concat(this.createComponents(frag,_3c2));
}
}
return _3c4;
};
this.parsePropertySets=function(_3c6){
return [];
};
this.parseProperties=function(_3c7){
var _3c8={};
for(var item in _3c7){
if((_3c7[item]==_3c7.tagName)||(_3c7[item]==_3c7.nodeRef)){
}else{
var frag=_3c7[item];
if(frag.tagName&&dojo.widget.tags[frag.tagName.toLowerCase()]){
}else{
if(frag[0]&&frag[0].value!=""&&frag[0].value!=null){
try{
if(item.toLowerCase()=="dataprovider"){
var _3cb=this;
this.getDataProvider(_3cb,frag[0].value);
_3c8.dataProvider=this.dataProvider;
}
_3c8[item]=frag[0].value;
var _3cc=this.parseProperties(frag);
for(var _3cd in _3cc){
_3c8[_3cd]=_3cc[_3cd];
}
}
catch(e){
dojo.debug(e);
}
}
}
switch(item.toLowerCase()){
case "checked":
case "disabled":
if(typeof _3c8[item]!="boolean"){
_3c8[item]=true;
}
break;
}
}
}
return _3c8;
};
this.getDataProvider=function(_3ce,_3cf){
dojo.io.bind({url:_3cf,load:function(type,_3d1){
if(type=="load"){
_3ce.dataProvider=_3d1;
}
},mimetype:"text/javascript",sync:true});
};
this.getPropertySetById=function(_3d2){
for(var x=0;x<this.propertySetsList.length;x++){
if(_3d2==this.propertySetsList[x]["id"][0].value){
return this.propertySetsList[x];
}
}
return "";
};
this.getPropertySetsByType=function(_3d4){
var _3d5=[];
for(var x=0;x<this.propertySetsList.length;x++){
var cpl=this.propertySetsList[x];
var cpcc=cpl.componentClass||cpl.componentType||null;
var _3d9=this.propertySetsList[x]["id"][0].value;
if(cpcc&&(_3d9==cpcc[0].value)){
_3d5.push(cpl);
}
}
return _3d5;
};
this.getPropertySets=function(_3da){
var ppl="dojo:propertyproviderlist";
var _3dc=[];
var _3dd=_3da.tagName;
if(_3da[ppl]){
var _3de=_3da[ppl].value.split(" ");
for(var _3df in _3de){
if((_3df.indexOf("..")==-1)&&(_3df.indexOf("://")==-1)){
var _3e0=this.getPropertySetById(_3df);
if(_3e0!=""){
_3dc.push(_3e0);
}
}else{
}
}
}
return this.getPropertySetsByType(_3dd).concat(_3dc);
};
this.createComponentFromScript=function(_3e1,_3e2,_3e3,ns){
_3e3.fastMixIn=true;
var ltn=(ns||"dojo")+":"+_3e2.toLowerCase();
if(dojo.widget.tags[ltn]){
return [dojo.widget.tags[ltn](_3e3,this,null,null,_3e3)];
}
return [dojo.widget.buildWidgetFromParseTree(ltn,_3e3,this,null,null,_3e3)];
};
};
dojo.widget._parser_collection={"dojo":new dojo.widget.Parse()};
dojo.widget.getParser=function(name){
if(!name){
name="dojo";
}
if(!this._parser_collection[name]){
this._parser_collection[name]=new dojo.widget.Parse();
}
return this._parser_collection[name];
};
dojo.widget.createWidget=function(name,_3e8,_3e9,_3ea){
var _3eb=false;
var _3ec=(typeof name=="string");
if(_3ec){
var pos=name.indexOf(":");
var ns=(pos>-1)?name.substring(0,pos):"dojo";
if(pos>-1){
name=name.substring(pos+1);
}
var _3ef=name.toLowerCase();
var _3f0=ns+":"+_3ef;
_3eb=(dojo.byId(name)&&!dojo.widget.tags[_3f0]);
}
if((arguments.length==1)&&(_3eb||!_3ec)){
var xp=new dojo.xml.Parse();
var tn=_3eb?dojo.byId(name):name;
return dojo.widget.getParser().createComponents(xp.parseElement(tn,null,true))[0];
}
function fromScript(_3f3,name,_3f5,ns){
_3f5[_3f0]={dojotype:[{value:_3ef}],nodeRef:_3f3,fastMixIn:true};
_3f5.ns=ns;
return dojo.widget.getParser().createComponentFromScript(_3f3,name,_3f5,ns);
}
_3e8=_3e8||{};
var _3f7=false;
var tn=null;
var h=dojo.render.html.capable;
if(h){
tn=document.createElement("span");
}
if(!_3e9){
_3f7=true;
_3e9=tn;
if(h){
dojo.body().appendChild(_3e9);
}
}else{
if(_3ea){
dojo.dom.insertAtPosition(tn,_3e9,_3ea);
}else{
tn=_3e9;
}
}
var _3f9=fromScript(tn,name.toLowerCase(),_3e8,ns);
if((!_3f9)||(!_3f9[0])||(typeof _3f9[0].widgetType=="undefined")){
throw new Error("createWidget: Creation of \""+name+"\" widget failed.");
}
try{
if(_3f7&&_3f9[0].domNode.parentNode){
_3f9[0].domNode.parentNode.removeChild(_3f9[0].domNode);
}
}
catch(e){
dojo.debug(e);
}
return _3f9[0];
};
dojo.provide("dojo.html.style");
dojo.html.getClass=function(node){
node=dojo.byId(node);
if(!node){
return "";
}
var cs="";
if(node.className){
cs=node.className;
}else{
if(dojo.html.hasAttribute(node,"class")){
cs=dojo.html.getAttribute(node,"class");
}
}
return cs.replace(/^\s+|\s+$/g,"");
};
dojo.html.getClasses=function(node){
var c=dojo.html.getClass(node);
return (c=="")?[]:c.split(/\s+/g);
};
dojo.html.hasClass=function(node,_3ff){
return (new RegExp("(^|\\s+)"+_3ff+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_401){
_401+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_401);
};
dojo.html.addClass=function(node,_403){
if(dojo.html.hasClass(node,_403)){
return false;
}
_403=(dojo.html.getClass(node)+" "+_403).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_403);
};
dojo.html.setClass=function(node,_405){
node=dojo.byId(node);
var cs=new String(_405);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_405);
node.className=cs;
}else{
return false;
}
}
}
catch(e){
dojo.debug("dojo.html.setClass() failed",e);
}
return true;
};
dojo.html.removeClass=function(node,_408,_409){
try{
if(!_409){
var _40a=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_408+"(\\s+|$)"),"$1$2");
}else{
var _40a=dojo.html.getClass(node).replace(_408,"");
}
dojo.html.setClass(node,_40a);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_40c,_40d){
dojo.html.removeClass(node,_40d);
dojo.html.addClass(node,_40c);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_40e,_40f,_410,_411,_412){
_412=false;
var _413=dojo.doc();
_40f=dojo.byId(_40f)||_413;
var _414=_40e.split(/\s+/g);
var _415=[];
if(_411!=1&&_411!=2){
_411=0;
}
var _416=new RegExp("(\\s|^)(("+_414.join(")|(")+"))(\\s|$)");
var _417=_414.join(" ").length;
var _418=[];
if(!_412&&_413.evaluate){
var _419=".//"+(_410||"*")+"[contains(";
if(_411!=dojo.html.classMatchType.ContainsAny){
_419+="concat(' ',@class,' '), ' "+_414.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_411==2){
_419+=" and string-length(@class)="+_417+"]";
}else{
_419+="]";
}
}else{
_419+="concat(' ',@class,' '), ' "+_414.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _41a=_413.evaluate(_419,_40f,null,XPathResult.ANY_TYPE,null);
var _41b=_41a.iterateNext();
while(_41b){
try{
_418.push(_41b);
_41b=_41a.iterateNext();
}
catch(e){
break;
}
}
return _418;
}else{
if(!_410){
_410="*";
}
_418=_40f.getElementsByTagName(_410);
var node,i=0;
outer:
while(node=_418[i++]){
var _41e=dojo.html.getClasses(node);
if(_41e.length==0){
continue outer;
}
var _41f=0;
for(var j=0;j<_41e.length;j++){
if(_416.test(_41e[j])){
if(_411==dojo.html.classMatchType.ContainsAny){
_415.push(node);
continue outer;
}else{
_41f++;
}
}else{
if(_411==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_41f==_414.length){
if((_411==dojo.html.classMatchType.IsOnly)&&(_41f==_41e.length)){
_415.push(node);
}else{
if(_411==dojo.html.classMatchType.ContainsAll){
_415.push(node);
}
}
}
}
return _415;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_421){
var arr=_421.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_425){
return _425.replace(/([A-Z])/g,"-$1").toLowerCase();
};
dojo.html.getComputedStyle=function(node,_427,_428){
node=dojo.byId(node);
var _427=dojo.html.toSelectorCase(_427);
var _429=dojo.html.toCamelCase(_427);
if(!node||!node.style){
return _428;
}else{
if(document.defaultView&&dojo.html.isDescendantOf(node,node.ownerDocument)){
try{
var cs=document.defaultView.getComputedStyle(node,"");
if(cs){
return cs.getPropertyValue(_427);
}
}
catch(e){
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_427);
}else{
return _428;
}
}
}else{
if(node.currentStyle){
return node.currentStyle[_429];
}
}
}
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_427);
}else{
return _428;
}
};
dojo.html.getStyleProperty=function(node,_42c){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_42c)]:undefined);
};
dojo.html.getStyle=function(node,_42e){
var _42f=dojo.html.getStyleProperty(node,_42e);
return (_42f?_42f:dojo.html.getComputedStyle(node,_42e));
};
dojo.html.setStyle=function(node,_431,_432){
node=dojo.byId(node);
if(node&&node.style){
var _433=dojo.html.toCamelCase(_431);
node.style[_433]=_432;
}
};
dojo.html.setStyleText=function(_434,text){
try{
_434.style.cssText=text;
}
catch(e){
_434.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_436,_437){
if(!_437.style.cssText){
_436.setAttribute("style",_437.getAttribute("style"));
}else{
_436.style.cssText=_437.style.cssText;
}
dojo.html.addClass(_436,dojo.html.getClass(_437));
};
dojo.html.getUnitValue=function(node,_439,_43a){
var s=dojo.html.getComputedStyle(node,_439);
if((!s)||((s=="auto")&&(_43a))){
return {value:0,units:"px"};
}
var _43c=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_43c){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_43c[1]),units:_43c[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
dojo.html.getPixelValue=function(node,_43e,_43f){
var _440=dojo.html.getUnitValue(node,_43e,_43f);
if(isNaN(_440.value)){
return 0;
}
if((_440.value)&&(_440.units!="px")){
return NaN;
}
return _440.value;
};
dojo.html.setPositivePixelValue=function(node,_442,_443){
if(isNaN(_443)){
return false;
}
node.style[_442]=Math.max(0,_443)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_444,_445,_446){
if(!dojo.html.styleSheet){
if(document.createStyleSheet){
dojo.html.styleSheet=document.createStyleSheet();
}else{
if(document.styleSheets[0]){
dojo.html.styleSheet=document.styleSheets[0];
}else{
return null;
}
}
}
if(arguments.length<3){
if(dojo.html.styleSheet.cssRules){
_446=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_446=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_444+" { "+_445+" }";
return dojo.html.styleSheet.insertRule(rule,_446);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_444,_445,_446);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_448){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_448){
_448=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_448);
}
}else{
if(document.styleSheets[0]){
if(!_448){
_448=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_448);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_44b,_44c){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _44d=dojo.hostenv.getText(URI,false,_44c);
if(_44d===null){
return;
}
_44d=dojo.html.fixPathsInCssText(_44d,URI);
if(_44b){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_44d)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _452=doc.getElementsByTagName("style");
for(var i=0;i<_452.length;i++){
if(_452[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _453=dojo.html.insertCssText(_44d,doc);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_44d,"nodeRef":_453});
if(_453&&djConfig.isDebug){
_453.setAttribute("dbgHref",URI);
}
return _453;
};
dojo.html.insertCssText=function(_454,doc,URI){
if(!_454){
return;
}
if(!doc){
doc=document;
}
if(URI){
_454=dojo.html.fixPathsInCssText(_454,URI);
}
var _457=doc.createElement("style");
_457.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_457);
}
if(_457.styleSheet){
var _459=function(){
try{
_457.styleSheet.cssText=_454;
}
catch(e){
dojo.debug(e);
}
};
if(_457.styleSheet.disabled){
setTimeout(_459,10);
}else{
_459();
}
}else{
var _45a=doc.createTextNode(_454);
_457.appendChild(_45a);
}
return _457;
};
dojo.html.fixPathsInCssText=function(_45b,URI){
if(!_45b||!URI){
return;
}
var _45d,str="",url="",_460="[\\t\\s\\w\\(\\)\\/\\.\\\\'\"-:#=&?~]+";
var _461=new RegExp("url\\(\\s*("+_460+")\\s*\\)");
var _462=/(file|https?|ftps?):\/\//;
regexTrim=new RegExp("^[\\s]*(['\"]?)("+_460+")\\1[\\s]*?$");
if(dojo.render.html.ie55||dojo.render.html.ie60){
var _463=new RegExp("AlphaImageLoader\\((.*)src=['\"]("+_460+")['\"]");
while(_45d=_463.exec(_45b)){
url=_45d[2].replace(regexTrim,"$2");
if(!_462.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_45b.substring(0,_45d.index)+"AlphaImageLoader("+_45d[1]+"src='"+url+"'";
_45b=_45b.substr(_45d.index+_45d[0].length);
}
_45b=str+_45b;
str="";
}
while(_45d=_461.exec(_45b)){
url=_45d[1].replace(regexTrim,"$2");
if(!_462.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_45b.substring(0,_45d.index)+"url("+url+")";
_45b=_45b.substr(_45d.index+_45d[0].length);
}
return str+_45b;
};
dojo.html.setActiveStyleSheet=function(_464){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_464){
a.disabled=false;
}
}
}
};
dojo.html.getActiveStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")&&!a.disabled){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.getPreferredStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("rel").indexOf("alt")==-1&&a.getAttribute("title")){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.applyBrowserClass=function(node){
var drh=dojo.render.html;
var _470={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _470){
if(_470[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.provide("dojo.widget.DomWidget");
dojo.widget._cssFiles={};
dojo.widget._cssStrings={};
dojo.widget._templateCache={};
dojo.widget.defaultStrings={dojoRoot:dojo.hostenv.getBaseScriptUri(),baseScriptUri:dojo.hostenv.getBaseScriptUri()};
dojo.widget.fillFromTemplateCache=function(obj,_473,_474,_475){
var _476=_473||obj.templatePath;
var _477=dojo.widget._templateCache;
if(!_476&&!obj["widgetType"]){
do{
var _478="__dummyTemplate__"+dojo.widget._templateCache.dummyCount++;
}while(_477[_478]);
obj.widgetType=_478;
}
var wt=_476?_476.toString():obj.widgetType;
var ts=_477[wt];
if(!ts){
_477[wt]={"string":null,"node":null};
if(_475){
ts={};
}else{
ts=_477[wt];
}
}
if((!obj.templateString)&&(!_475)){
obj.templateString=_474||ts["string"];
}
if((!obj.templateNode)&&(!_475)){
obj.templateNode=ts["node"];
}
if((!obj.templateNode)&&(!obj.templateString)&&(_476)){
var _47b=dojo.hostenv.getText(_476);
if(_47b){
_47b=_47b.replace(/^\s*<\?xml(\s)+version=[\'\"](\d)*.(\d)*[\'\"](\s)*\?>/im,"");
var _47c=_47b.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_47c){
_47b=_47c[1];
}
}else{
_47b="";
}
obj.templateString=_47b;
if(!_475){
_477[wt]["string"]=_47b;
}
}
if((!ts["string"])&&(!_475)){
ts.string=obj.templateString;
}
};
dojo.widget._templateCache.dummyCount=0;
dojo.widget.attachProperties=["dojoAttachPoint","id"];
dojo.widget.eventAttachProperty="dojoAttachEvent";
dojo.widget.onBuildProperty="dojoOnBuild";
dojo.widget.waiNames=["waiRole","waiState"];
dojo.widget.wai={waiRole:{name:"waiRole","namespace":"http://www.w3.org/TR/xhtml2",alias:"x2",prefix:"wairole:"},waiState:{name:"waiState","namespace":"http://www.w3.org/2005/07/aaa",alias:"aaa",prefix:""},setAttr:function(node,ns,attr,_480){
if(dojo.render.html.ie){
node.setAttribute(this[ns].alias+":"+attr,this[ns].prefix+_480);
}else{
node.setAttributeNS(this[ns]["namespace"],attr,this[ns].prefix+_480);
}
},getAttr:function(node,ns,attr){
if(dojo.render.html.ie){
return node.getAttribute(this[ns].alias+":"+attr);
}else{
return node.getAttributeNS(this[ns]["namespace"],attr);
}
},removeAttr:function(node,ns,attr){
var _487=true;
if(dojo.render.html.ie){
_487=node.removeAttribute(this[ns].alias+":"+attr);
}else{
node.removeAttributeNS(this[ns]["namespace"],attr);
}
return _487;
}};
dojo.widget.attachTemplateNodes=function(_488,_489,_48a){
var _48b=dojo.dom.ELEMENT_NODE;
function trim(str){
return str.replace(/^\s+|\s+$/g,"");
}
if(!_488){
_488=_489.domNode;
}
if(_488.nodeType!=_48b){
return;
}
var _48d=_488.all||_488.getElementsByTagName("*");
var _48e=_489;
for(var x=-1;x<_48d.length;x++){
var _490=(x==-1)?_488:_48d[x];
var _491=[];
if(!_489.widgetsInTemplate||!_490.getAttribute("dojoType")){
for(var y=0;y<this.attachProperties.length;y++){
var _493=_490.getAttribute(this.attachProperties[y]);
if(_493){
_491=_493.split(";");
for(var z=0;z<_491.length;z++){
if(dojo.lang.isArray(_489[_491[z]])){
_489[_491[z]].push(_490);
}else{
_489[_491[z]]=_490;
}
}
break;
}
}
var _495=_490.getAttribute(this.eventAttachProperty);
if(_495){
var evts=_495.split(";");
for(var y=0;y<evts.length;y++){
if((!evts[y])||(!evts[y].length)){
continue;
}
var _497=null;
var tevt=trim(evts[y]);
if(evts[y].indexOf(":")>=0){
var _499=tevt.split(":");
tevt=trim(_499[0]);
_497=trim(_499[1]);
}
if(!_497){
_497=tevt;
}
var tf=function(){
var ntf=new String(_497);
return function(evt){
if(_48e[ntf]){
_48e[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_490,tevt,tf,false,true);
}
}
for(var y=0;y<_48a.length;y++){
var _49d=_490.getAttribute(_48a[y]);
if((_49d)&&(_49d.length)){
var _497=null;
var _49e=_48a[y].substr(4);
_497=trim(_49d);
var _49f=[_497];
if(_497.indexOf(";")>=0){
_49f=dojo.lang.map(_497.split(";"),trim);
}
for(var z=0;z<_49f.length;z++){
if(!_49f[z].length){
continue;
}
var tf=function(){
var ntf=new String(_49f[z]);
return function(evt){
if(_48e[ntf]){
_48e[ntf](dojo.event.browser.fixEvent(evt,this));
}
};
}();
dojo.event.browser.addListener(_490,_49e,tf,false,true);
}
}
}
}
var _4a2=_490.getAttribute(this.templateProperty);
if(_4a2){
_489[_4a2]=_490;
}
dojo.lang.forEach(dojo.widget.waiNames,function(name){
var wai=dojo.widget.wai[name];
var val=_490.getAttribute(wai.name);
if(val){
if(val.indexOf("-")==-1){
dojo.widget.wai.setAttr(_490,wai.name,"role",val);
}else{
var _4a6=val.split("-");
dojo.widget.wai.setAttr(_490,wai.name,_4a6[0],_4a6[1]);
}
}
},this);
var _4a7=_490.getAttribute(this.onBuildProperty);
if(_4a7){
eval("var node = baseNode; var widget = targetObj; "+_4a7);
}
}
};
dojo.widget.getDojoEventsFromStr=function(str){
var re=/(dojoOn([a-z]+)(\s?))=/gi;
var evts=str?str.match(re)||[]:[];
var ret=[];
var lem={};
for(var x=0;x<evts.length;x++){
if(evts[x].length<1){
continue;
}
var cm=evts[x].replace(/\s/,"");
cm=(cm.slice(0,cm.length-1));
if(!lem[cm]){
lem[cm]=true;
ret.push(cm);
}
}
return ret;
};
dojo.declare("dojo.widget.DomWidget",dojo.widget.Widget,function(){
if((arguments.length>0)&&(typeof arguments[0]=="object")){
this.create(arguments[0]);
}
},{templateNode:null,templateString:null,templateCssString:null,preventClobber:false,domNode:null,containerNode:null,widgetsInTemplate:false,addChild:function(_4af,_4b0,pos,ref,_4b3){
if(!this.isContainer){
dojo.debug("dojo.widget.DomWidget.addChild() attempted on non-container widget");
return null;
}else{
if(_4b3==undefined){
_4b3=this.children.length;
}
this.addWidgetAsDirectChild(_4af,_4b0,pos,ref,_4b3);
this.registerChild(_4af,_4b3);
}
return _4af;
},addWidgetAsDirectChild:function(_4b4,_4b5,pos,ref,_4b8){
if((!this.containerNode)&&(!_4b5)){
this.containerNode=this.domNode;
}
var cn=(_4b5)?_4b5:this.containerNode;
if(!pos){
pos="after";
}
if(!ref){
if(!cn){
cn=dojo.body();
}
ref=cn.lastChild;
}
if(!_4b8){
_4b8=0;
}
_4b4.domNode.setAttribute("dojoinsertionindex",_4b8);
if(!ref){
cn.appendChild(_4b4.domNode);
}else{
if(pos=="insertAtIndex"){
dojo.dom.insertAtIndex(_4b4.domNode,ref.parentNode,_4b8);
}else{
if((pos=="after")&&(ref===cn.lastChild)){
cn.appendChild(_4b4.domNode);
}else{
dojo.dom.insertAtPosition(_4b4.domNode,cn,pos);
}
}
}
},registerChild:function(_4ba,_4bb){
_4ba.dojoInsertionIndex=_4bb;
var idx=-1;
for(var i=0;i<this.children.length;i++){
if(this.children[i].dojoInsertionIndex<=_4bb){
idx=i;
}
}
this.children.splice(idx+1,0,_4ba);
_4ba.parent=this;
_4ba.addedTo(this,idx+1);
delete dojo.widget.manager.topWidgets[_4ba.widgetId];
},removeChild:function(_4be){
dojo.dom.removeNode(_4be.domNode);
return dojo.widget.DomWidget.superclass.removeChild.call(this,_4be);
},getFragNodeRef:function(frag){
if(!frag){
return null;
}
if(!frag[this.getNamespacedType()]){
dojo.raise("Error: no frag for widget type "+this.getNamespacedType()+", id "+this.widgetId+" (maybe a widget has set it's type incorrectly)");
}
return frag[this.getNamespacedType()]["nodeRef"];
},postInitialize:function(args,frag,_4c2){
var _4c3=this.getFragNodeRef(frag);
if(_4c2&&(_4c2.snarfChildDomOutput||!_4c3)){
_4c2.addWidgetAsDirectChild(this,"","insertAtIndex","",args["dojoinsertionindex"],_4c3);
}else{
if(_4c3){
if(this.domNode&&(this.domNode!==_4c3)){
this._sourceNodeRef=dojo.dom.replaceNode(_4c3,this.domNode);
}
}
}
if(_4c2){
_4c2.registerChild(this,args.dojoinsertionindex);
}else{
dojo.widget.manager.topWidgets[this.widgetId]=this;
}
if(this.widgetsInTemplate){
var _4c4=new dojo.xml.Parse();
var _4c5;
var _4c6=this.domNode.getElementsByTagName("*");
for(var i=0;i<_4c6.length;i++){
if(_4c6[i].getAttribute("dojoAttachPoint")=="subContainerWidget"){
_4c5=_4c6[i];
}
if(_4c6[i].getAttribute("dojoType")){
_4c6[i].setAttribute("isSubWidget",true);
}
}
if(this.isContainer&&!this.containerNode){
if(_4c5){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,_4c5);
frag["dojoDontFollow"]=true;
}
}else{
dojo.debug("No subContainerWidget node can be found in template file for widget "+this);
}
}
var _4c9=_4c4.parseElement(this.domNode,null,true);
dojo.widget.getParser().createSubComponents(_4c9,this);
var _4ca=[];
var _4cb=[this];
var w;
while((w=_4cb.pop())){
for(var i=0;i<w.children.length;i++){
var _4cd=w.children[i];
if(_4cd._processedSubWidgets||!_4cd.extraArgs["issubwidget"]){
continue;
}
_4ca.push(_4cd);
if(_4cd.isContainer){
_4cb.push(_4cd);
}
}
}
for(var i=0;i<_4ca.length;i++){
var _4ce=_4ca[i];
if(_4ce._processedSubWidgets){
dojo.debug("This should not happen: widget._processedSubWidgets is already true!");
return;
}
_4ce._processedSubWidgets=true;
if(_4ce.extraArgs["dojoattachevent"]){
var evts=_4ce.extraArgs["dojoattachevent"].split(";");
for(var j=0;j<evts.length;j++){
var _4d1=null;
var tevt=dojo.string.trim(evts[j]);
if(tevt.indexOf(":")>=0){
var _4d3=tevt.split(":");
tevt=dojo.string.trim(_4d3[0]);
_4d1=dojo.string.trim(_4d3[1]);
}
if(!_4d1){
_4d1=tevt;
}
if(dojo.lang.isFunction(_4ce[tevt])){
dojo.event.kwConnect({srcObj:_4ce,srcFunc:tevt,targetObj:this,targetFunc:_4d1});
}else{
alert(tevt+" is not a function in widget "+_4ce);
}
}
}
if(_4ce.extraArgs["dojoattachpoint"]){
this[_4ce.extraArgs["dojoattachpoint"]]=_4ce;
}
}
}
if(this.isContainer&&!frag["dojoDontFollow"]){
dojo.widget.getParser().createSubComponents(frag,this);
}
},buildRendering:function(args,frag){
var ts=dojo.widget._templateCache[this.widgetType];
if(args["templatecsspath"]){
args["templateCssPath"]=args["templatecsspath"];
}
var _4d7=args["templateCssPath"]||this.templateCssPath;
if(_4d7&&!dojo.widget._cssFiles[_4d7.toString()]){
if((!this.templateCssString)&&(_4d7)){
this.templateCssString=dojo.hostenv.getText(_4d7);
this.templateCssPath=null;
}
dojo.widget._cssFiles[_4d7.toString()]=true;
}
if((this["templateCssString"])&&(!dojo.widget._cssStrings[this.templateCssString])){
dojo.html.insertCssText(this.templateCssString,null,_4d7);
dojo.widget._cssStrings[this.templateCssString]=true;
}
if((!this.preventClobber)&&((this.templatePath)||(this.templateNode)||((this["templateString"])&&(this.templateString.length))||((typeof ts!="undefined")&&((ts["string"])||(ts["node"]))))){
this.buildFromTemplate(args,frag);
}else{
this.domNode=this.getFragNodeRef(frag);
}
this.fillInTemplate(args,frag);
},buildFromTemplate:function(args,frag){
var _4da=false;
if(args["templatepath"]){
args["templatePath"]=args["templatepath"];
}
dojo.widget.fillFromTemplateCache(this,args["templatePath"],null,_4da);
var ts=dojo.widget._templateCache[this.templatePath?this.templatePath.toString():this.widgetType];
if((ts)&&(!_4da)){
if(!this.templateString.length){
this.templateString=ts["string"];
}
if(!this.templateNode){
this.templateNode=ts["node"];
}
}
var _4dc=false;
var node=null;
var tstr=this.templateString;
if((!this.templateNode)&&(this.templateString)){
_4dc=this.templateString.match(/\$\{([^\}]+)\}/g);
if(_4dc){
var hash=this.strings||{};
for(var key in dojo.widget.defaultStrings){
if(dojo.lang.isUndefined(hash[key])){
hash[key]=dojo.widget.defaultStrings[key];
}
}
for(var i=0;i<_4dc.length;i++){
var key=_4dc[i];
key=key.substring(2,key.length-1);
var kval=(key.substring(0,5)=="this.")?dojo.lang.getObjPathValue(key.substring(5),this):hash[key];
var _4e3;
if((kval)||(dojo.lang.isString(kval))){
_4e3=new String((dojo.lang.isFunction(kval))?kval.call(this,key,this.templateString):kval);
while(_4e3.indexOf("\"")>-1){
_4e3=_4e3.replace("\"","&quot;");
}
tstr=tstr.replace(_4dc[i],_4e3);
}
}
}else{
this.templateNode=this.createNodesFromText(this.templateString,true)[0];
if(!_4da){
ts.node=this.templateNode;
}
}
}
if((!this.templateNode)&&(!_4dc)){
dojo.debug("DomWidget.buildFromTemplate: could not create template");
return false;
}else{
if(!_4dc){
node=this.templateNode.cloneNode(true);
if(!node){
return false;
}
}else{
node=this.createNodesFromText(tstr,true)[0];
}
}
this.domNode=node;
this.attachTemplateNodes();
if(this.isContainer&&this.containerNode){
var src=this.getFragNodeRef(frag);
if(src){
dojo.dom.moveChildren(src,this.containerNode);
}
}
},attachTemplateNodes:function(_4e5,_4e6){
if(!_4e5){
_4e5=this.domNode;
}
if(!_4e6){
_4e6=this;
}
return dojo.widget.attachTemplateNodes(_4e5,_4e6,dojo.widget.getDojoEventsFromStr(this.templateString));
},fillInTemplate:function(){
},destroyRendering:function(){
try{
dojo.dom.destroyNode(this.domNode);
delete this.domNode;
}
catch(e){
}
if(this._sourceNodeRef){
try{
dojo.dom.destroyNode(this._sourceNodeRef);
}
catch(e){
}
}
},createNodesFromText:function(){
dojo.unimplemented("dojo.widget.DomWidget.createNodesFromText");
}});
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_4e8,_4e9){
node=dojo.byId(node);
_4e9(node,!_4e8(node));
return _4e8(node);
};
dojo.html.show=function(node){
node=dojo.byId(node);
if(dojo.html.getStyleProperty(node,"display")=="none"){
dojo.html.setStyle(node,"display",(node.dojoDisplayCache||""));
node.dojoDisplayCache=undefined;
}
};
dojo.html.hide=function(node){
node=dojo.byId(node);
if(typeof node["dojoDisplayCache"]=="undefined"){
var d=dojo.html.getStyleProperty(node,"display");
if(d!="none"){
node.dojoDisplayCache=d;
}
}
dojo.html.setStyle(node,"display","none");
};
dojo.html.setShowing=function(node,_4ee){
dojo.html[(_4ee?"show":"hide")](node);
};
dojo.html.isShowing=function(node){
return (dojo.html.getStyleProperty(node,"display")!="none");
};
dojo.html.toggleShowing=function(node){
return dojo.html._toggle(node,dojo.html.isShowing,dojo.html.setShowing);
};
dojo.html.displayMap={tr:"",td:"",th:"",img:"inline",span:"inline",input:"inline",button:"inline"};
dojo.html.suggestDisplayByTagName=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
var tag=node.tagName.toLowerCase();
return (tag in dojo.html.displayMap?dojo.html.displayMap[tag]:"block");
}
};
dojo.html.setDisplay=function(node,_4f4){
dojo.html.setStyle(node,"display",((_4f4 instanceof String||typeof _4f4=="string")?_4f4:(_4f4?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_4f8){
dojo.html.setStyle(node,"visibility",((_4f8 instanceof String||typeof _4f8=="string")?_4f8:(_4f8?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_4fc,_4fd){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_4fd){
if(_4fc>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_4fc=0.999999;
}
}else{
if(_4fc<0){
_4fc=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_4fc*100+")";
}
}
node.style.filter="Alpha(Opacity="+_4fc*100+")";
}else{
if(h.moz){
node.style.opacity=_4fc;
node.style.MozOpacity=_4fc;
}else{
if(h.safari){
node.style.opacity=_4fc;
node.style.KhtmlOpacity=_4fc;
}else{
node.style.opacity=_4fc;
}
}
}
};
dojo.html.clearOpacity=function(node){
node=dojo.byId(node);
var ns=node.style;
var h=dojo.render.html;
if(h.ie){
try{
if(node.filters&&node.filters.alpha){
ns.filter="";
}
}
catch(e){
}
}else{
if(h.moz){
ns.opacity=1;
ns.MozOpacity=1;
}else{
if(h.safari){
ns.opacity=1;
ns.KhtmlOpacity=1;
}else{
ns.opacity=1;
}
}
}
};
dojo.html.getOpacity=function(node){
node=dojo.byId(node);
var h=dojo.render.html;
if(h.ie){
var opac=(node.filters&&node.filters.alpha&&typeof node.filters.alpha.opacity=="number"?node.filters.alpha.opacity:100)/100;
}else{
var opac=node.style.opacity||node.style.MozOpacity||node.style.KhtmlOpacity||1;
}
return opac>=0.999999?1:Number(opac);
};
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _509=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_509+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _509;
};
dojo.html.setStyleAttributes=function(node,_50c){
node=dojo.byId(node);
var _50d=_50c.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_50d.length;i++){
var _50f=_50d[i].split(":");
var name=_50f[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _511=_50f[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_511);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_511});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_511});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_511});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_511});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_511;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_513,_514){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_514){
_514=bs.CONTENT_BOX;
}
var _517=2;
var _518;
switch(_514){
case bs.MARGIN_BOX:
_518=3;
break;
case bs.BORDER_BOX:
_518=2;
break;
case bs.PADDING_BOX:
default:
_518=1;
break;
case bs.CONTENT_BOX:
_518=0;
break;
}
var h=dojo.render.html;
var db=document["body"]||document["documentElement"];
if(h.ie){
with(node.getBoundingClientRect()){
ret.x=left-2;
ret.y=top-2;
}
}else{
if(document.getBoxObjectFor){
_517=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _51c;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_51c=db;
}else{
_51c=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _51e=node;
do{
var n=_51e["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_51e["offsetTop"];
ret.y+=isNaN(m)?0:m;
_51e=_51e.offsetParent;
}while((_51e!=_51c)&&(_51e!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_513){
var _521=dojo.html.getScroll();
ret.y+=_521.top;
ret.x+=_521.left;
}
var _522=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_517>_518){
for(var i=_518;i<_517;++i){
ret.y+=_522[i](node,"top");
ret.x+=_522[i](node,"left");
}
}else{
if(_517<_518){
for(var i=_518;i>_517;--i){
ret.y-=_522[i-1](node,"top");
ret.x-=_522[i-1](node,"left");
}
}
}
ret.top=ret.y;
ret.left=ret.x;
return ret;
};
dojo.html.isPositionAbsolute=function(node){
return (dojo.html.getComputedStyle(node,"position")=="absolute");
};
dojo.html._sumPixelValues=function(node,_526,_527){
var _528=0;
for(var x=0;x<_526.length;x++){
_528+=dojo.html.getPixelValue(node,_526[x],_527);
}
return _528;
};
dojo.html.getMargin=function(node){
return {width:dojo.html._sumPixelValues(node,["margin-left","margin-right"],(dojo.html.getComputedStyle(node,"position")=="absolute")),height:dojo.html._sumPixelValues(node,["margin-top","margin-bottom"],(dojo.html.getComputedStyle(node,"position")=="absolute"))};
};
dojo.html.getBorder=function(node){
return {width:dojo.html.getBorderExtent(node,"left")+dojo.html.getBorderExtent(node,"right"),height:dojo.html.getBorderExtent(node,"top")+dojo.html.getBorderExtent(node,"bottom")};
};
dojo.html.getBorderExtent=function(node,side){
return (dojo.html.getStyle(node,"border-"+side+"-style")=="none"?0:dojo.html.getPixelValue(node,"border-"+side+"-width"));
};
dojo.html.getMarginExtent=function(node,side){
return dojo.html._sumPixelValues(node,["margin-"+side],dojo.html.isPositionAbsolute(node));
};
dojo.html.getPaddingExtent=function(node,side){
return dojo.html._sumPixelValues(node,["padding-"+side],true);
};
dojo.html.getPadding=function(node){
return {width:dojo.html._sumPixelValues(node,["padding-left","padding-right"],true),height:dojo.html._sumPixelValues(node,["padding-top","padding-bottom"],true)};
};
dojo.html.getPadBorder=function(node){
var pad=dojo.html.getPadding(node);
var _535=dojo.html.getBorder(node);
return {width:pad.width+_535.width,height:pad.height+_535.height};
};
dojo.html.getBoxSizing=function(node){
var h=dojo.render.html;
var bs=dojo.html.boxSizing;
if(((h.ie)||(h.opera))&&node.nodeName!="IMG"){
var cm=document["compatMode"];
if((cm=="BackCompat")||(cm=="QuirksMode")){
return bs.BORDER_BOX;
}else{
return bs.CONTENT_BOX;
}
}else{
if(arguments.length==0){
node=document.documentElement;
}
var _53a=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_53a){
_53a=dojo.html.getStyle(node,"box-sizing");
}
return (_53a?_53a:bs.CONTENT_BOX);
}
};
dojo.html.isBorderBox=function(node){
return (dojo.html.getBoxSizing(node)==dojo.html.boxSizing.BORDER_BOX);
};
dojo.html.getBorderBox=function(node){
node=dojo.byId(node);
return {width:node.offsetWidth,height:node.offsetHeight};
};
dojo.html.getPaddingBox=function(node){
var box=dojo.html.getBorderBox(node);
var _53f=dojo.html.getBorder(node);
return {width:box.width-_53f.width,height:box.height-_53f.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _541=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_541.width,height:node.offsetHeight-_541.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _544=0;
var _545=0;
var isbb=dojo.html.isBorderBox(node);
var _547=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_544=args.width+_547.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_544);
}
if(typeof args.height!="undefined"){
_545=args.height+_547.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_545);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _54a=dojo.html.getBorderBox(node);
var _54b=dojo.html.getMargin(node);
return {width:_54a.width+_54b.width,height:_54a.height+_54b.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _54e=0;
var _54f=0;
var isbb=dojo.html.isBorderBox(node);
var _551=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _552=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_54e=args.width-_551.width;
_54e-=_552.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_54e);
}
if(typeof args.height!="undefined"){
_54f=args.height-_551.height;
_54f-=_552.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_54f);
}
return ret;
};
dojo.html.getElementBox=function(node,type){
var bs=dojo.html.boxSizing;
switch(type){
case bs.MARGIN_BOX:
return dojo.html.getMarginBox(node);
case bs.BORDER_BOX:
return dojo.html.getBorderBox(node);
case bs.PADDING_BOX:
return dojo.html.getPaddingBox(node);
case bs.CONTENT_BOX:
default:
return dojo.html.getContentBox(node);
}
};
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_557,_558,_559){
if(_557 instanceof Array||typeof _557=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_557.length<4){
_557.push(0);
}
while(_557.length>4){
_557.pop();
}
var ret={left:_557[0],top:_557[1],width:_557[2],height:_557[3]};
}else{
if(!_557.nodeType&&!(_557 instanceof String||typeof _557=="string")&&("width" in _557||"height" in _557||"left" in _557||"x" in _557||"top" in _557||"y" in _557)){
var ret={left:_557.left||_557.x||0,top:_557.top||_557.y||0,width:_557.width||0,height:_557.height||0};
}else{
var node=dojo.byId(_557);
var pos=dojo.html.abs(node,_558,_559);
var _55d=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_55d.width,height:_55d.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_55f){
return dojo.html._callDeprecated("setMarginBoxWidth","setMarginBox",arguments,"width");
};
dojo.html.setMarginBoxHeight=dojo.html.setOuterHeight=function(){
return dojo.html._callDeprecated("setMarginBoxHeight","setMarginBox",arguments,"height");
};
dojo.html.getMarginBoxWidth=dojo.html.getOuterWidth=function(){
return dojo.html._callDeprecated("getMarginBoxWidth","getMarginBox",arguments,null,"width");
};
dojo.html.getMarginBoxHeight=dojo.html.getOuterHeight=function(){
return dojo.html._callDeprecated("getMarginBoxHeight","getMarginBox",arguments,null,"height");
};
dojo.html.getTotalOffset=function(node,type,_562){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_564){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_566){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_568){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_56a){
return dojo.html._callDeprecated("totalOffsetTop","getAbsolutePosition",arguments,null,"top");
};
dojo.html.getMarginWidth=function(node){
return dojo.html._callDeprecated("getMarginWidth","getMargin",arguments,null,"width");
};
dojo.html.getMarginHeight=function(node){
return dojo.html._callDeprecated("getMarginHeight","getMargin",arguments,null,"height");
};
dojo.html.getBorderWidth=function(node){
return dojo.html._callDeprecated("getBorderWidth","getBorder",arguments,null,"width");
};
dojo.html.getBorderHeight=function(node){
return dojo.html._callDeprecated("getBorderHeight","getBorder",arguments,null,"height");
};
dojo.html.getPaddingWidth=function(node){
return dojo.html._callDeprecated("getPaddingWidth","getPadding",arguments,null,"width");
};
dojo.html.getPaddingHeight=function(node){
return dojo.html._callDeprecated("getPaddingHeight","getPadding",arguments,null,"height");
};
dojo.html.getPadBorderWidth=function(node){
return dojo.html._callDeprecated("getPadBorderWidth","getPadBorder",arguments,null,"width");
};
dojo.html.getPadBorderHeight=function(node){
return dojo.html._callDeprecated("getPadBorderHeight","getPadBorder",arguments,null,"height");
};
dojo.html.getBorderBoxWidth=dojo.html.getInnerWidth=function(){
return dojo.html._callDeprecated("getBorderBoxWidth","getBorderBox",arguments,null,"width");
};
dojo.html.getBorderBoxHeight=dojo.html.getInnerHeight=function(){
return dojo.html._callDeprecated("getBorderBoxHeight","getBorderBox",arguments,null,"height");
};
dojo.html.getContentBoxWidth=dojo.html.getContentWidth=function(){
return dojo.html._callDeprecated("getContentBoxWidth","getContentBox",arguments,null,"width");
};
dojo.html.getContentBoxHeight=dojo.html.getContentHeight=function(){
return dojo.html._callDeprecated("getContentBoxHeight","getContentBox",arguments,null,"height");
};
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_574){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_576){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_577){
return dojo.html.getDocumentWindow(_577.ownerDocument);
};
dojo.html.getDocumentWindow=function(doc){
if(dojo.render.html.safari&&!doc._parentWindow){
var fix=function(win){
win.document._parentWindow=win;
for(var i=0;i<win.frames.length;i++){
fix(win.frames[i]);
}
};
fix(window.top);
}
if(dojo.render.html.ie&&window!==document.parentWindow&&!doc._parentWindow){
doc.parentWindow.execScript("document._parentWindow = window;","Javascript");
var win=doc._parentWindow;
doc._parentWindow=null;
return win;
}
return doc._parentWindow||doc.parentWindow||doc.defaultView;
};
dojo.html.gravity=function(node,e){
node=dojo.byId(node);
var _57f=dojo.html.getCursorPosition(e);
with(dojo.html){
var _580=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _582=_580.x+(bb.width/2);
var _583=_580.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_57f.x<_582?WEST:EAST)|(_57f.y<_583?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_584,e){
_584=dojo.byId(_584);
var _586=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_584);
var _588=dojo.html.getAbsolutePosition(_584,true,dojo.html.boxSizing.BORDER_BOX);
var top=_588.y;
var _58a=top+bb.height;
var left=_588.x;
var _58c=left+bb.width;
return (_586.x>=left&&_586.x<=_58c&&_586.y>=top&&_586.y<=_58a);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _58e="";
if(node==null){
return _58e;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _590="unknown";
try{
_590=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_590){
case "block":
case "list-item":
case "run-in":
case "table":
case "table-row-group":
case "table-header-group":
case "table-footer-group":
case "table-row":
case "table-column-group":
case "table-column":
case "table-cell":
case "table-caption":
_58e+="\n";
_58e+=dojo.html.renderedTextContent(node.childNodes[i]);
_58e+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_58e+="\n";
}else{
_58e+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _592="unknown";
try{
_592=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_592){
case "capitalize":
var _593=text.split(" ");
for(var i=0;i<_593.length;i++){
_593[i]=_593[i].charAt(0).toUpperCase()+_593[i].substring(1);
}
text=_593.join(" ");
break;
case "uppercase":
text=text.toUpperCase();
break;
case "lowercase":
text=text.toLowerCase();
break;
default:
break;
}
switch(_592){
case "nowrap":
break;
case "pre-wrap":
break;
case "pre-line":
break;
case "pre":
break;
default:
text=text.replace(/\s+/," ");
if(/\s$/.test(_58e)){
text.replace(/^\s/,"");
}
break;
}
_58e+=text;
break;
default:
break;
}
}
return _58e;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _597="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_597="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_597="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_597="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _598=null;
switch(_597){
case "cell":
_598=tn.getElementsByTagName("tr")[0];
break;
case "row":
_598=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_598=tn.getElementsByTagName("table")[0];
break;
default:
_598=tn;
break;
}
var _599=[];
for(var x=0;x<_598.childNodes.length;x++){
_599.push(_598.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.html.destroyNode(tn);
return _599;
};
dojo.html.placeOnScreen=function(node,_59c,_59d,_59e,_59f,_5a0,_5a1){
if(_59c instanceof Array||typeof _59c=="array"){
_5a1=_5a0;
_5a0=_59f;
_59f=_59e;
_59e=_59d;
_59d=_59c[1];
_59c=_59c[0];
}
if(_5a0 instanceof String||typeof _5a0=="string"){
_5a0=_5a0.split(",");
}
if(!isNaN(_59e)){
_59e=[Number(_59e),Number(_59e)];
}else{
if(!(_59e instanceof Array||typeof _59e=="array")){
_59e=[0,0];
}
}
var _5a2=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _5a4=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_5a4;
if(!(_5a0 instanceof Array||typeof _5a0=="array")){
_5a0=["TL"];
}
var _5a8,_5a9,_5aa=Infinity,_5ab;
for(var _5ac=0;_5ac<_5a0.length;++_5ac){
var _5ad=_5a0[_5ac];
var _5ae=true;
var tryX=_59c-(_5ad.charAt(1)=="L"?0:w)+_59e[0]*(_5ad.charAt(1)=="L"?1:-1);
var tryY=_59d-(_5ad.charAt(0)=="T"?0:h)+_59e[1]*(_5ad.charAt(0)=="T"?1:-1);
if(_59f){
tryX-=_5a2.x;
tryY-=_5a2.y;
}
if(tryX<0){
tryX=0;
_5ae=false;
}
if(tryY<0){
tryY=0;
_5ae=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_5ae=false;
}else{
x=tryX;
}
x=Math.max(_59e[0],x)+_5a2.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_5ae=false;
}else{
y=tryY;
}
y=Math.max(_59e[1],y)+_5a2.y;
if(_5ae){
_5a8=x;
_5a9=y;
_5aa=0;
_5ab=_5ad;
break;
}else{
var dist=Math.pow(x-tryX-_5a2.x,2)+Math.pow(y-tryY-_5a2.y,2);
if(_5aa>dist){
_5aa=dist;
_5a8=x;
_5a9=y;
_5ab=_5ad;
}
}
}
if(!_5a1){
node.style.left=_5a8+"px";
node.style.top=_5a9+"px";
}
return {left:_5a8,top:_5a9,x:_5a8,y:_5a9,dist:_5aa,corner:_5ab};
};
dojo.html.placeOnScreenPoint=function(node,_5b5,_5b6,_5b7,_5b8){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_5b5,_5b6,_5b7,_5b8,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_5ba,_5bb,_5bc,_5bd,_5be){
var best,_5c0=Infinity;
_5ba=dojo.byId(_5ba);
var _5c1=_5ba.style.display;
_5ba.style.display="";
var mb=dojo.html.getElementBox(_5ba,_5bc);
var _5c3=mb.width;
var _5c4=mb.height;
var _5c5=dojo.html.getAbsolutePosition(_5ba,true,_5bc);
_5ba.style.display=_5c1;
for(var _5c6 in _5bd){
var pos,_5c8,_5c9;
var _5ca=_5bd[_5c6];
_5c8=_5c5.x+(_5c6.charAt(1)=="L"?0:_5c3);
_5c9=_5c5.y+(_5c6.charAt(0)=="T"?0:_5c4);
pos=dojo.html.placeOnScreen(node,_5c8,_5c9,_5bb,true,_5ca,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_5c0>pos.dist){
_5c0=pos.dist;
best=pos;
}
}
}
if(!_5be){
node.style.left=best.left+"px";
node.style.top=best.top+"px";
}
return best;
};
dojo.html.scrollIntoView=function(node){
if(!node){
return;
}
if(dojo.render.html.ie){
if(dojo.html.getBorderBox(node.parentNode).height<=node.parentNode.scrollHeight){
node.scrollIntoView(false);
}
}else{
if(dojo.render.html.mozilla){
node.scrollIntoView(false);
}else{
var _5cc=node.parentNode;
var _5cd=_5cc.scrollTop+dojo.html.getBorderBox(_5cc).height;
var _5ce=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_5cd<_5ce){
_5cc.scrollTop+=(_5ce-_5cd);
}else{
if(_5cc.scrollTop>node.offsetTop){
_5cc.scrollTop-=(_5cc.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.gfx.color");
dojo.gfx.color.Color=function(r,g,b,a){
if(dojo.lang.isArray(r)){
this.r=r[0];
this.g=r[1];
this.b=r[2];
this.a=r[3]||1;
}else{
if(dojo.lang.isString(r)){
var rgb=dojo.gfx.color.extractRGB(r);
this.r=rgb[0];
this.g=rgb[1];
this.b=rgb[2];
this.a=g||1;
}else{
if(r instanceof dojo.gfx.color.Color){
this.r=r.r;
this.b=r.b;
this.g=r.g;
this.a=r.a;
}else{
this.r=r;
this.g=g;
this.b=b;
this.a=a;
}
}
}
};
dojo.gfx.color.Color.fromArray=function(arr){
return new dojo.gfx.color.Color(arr[0],arr[1],arr[2],arr[3]);
};
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_5d5){
if(_5d5){
return this.toRgba();
}else{
return [this.r,this.g,this.b];
}
},toRgba:function(){
return [this.r,this.g,this.b,this.a];
},toHex:function(){
return dojo.gfx.color.rgb2hex(this.toRgb());
},toCss:function(){
return "rgb("+this.toRgb().join()+")";
},toString:function(){
return this.toHex();
},blend:function(_5d6,_5d7){
var rgb=null;
if(dojo.lang.isArray(_5d6)){
rgb=_5d6;
}else{
if(_5d6 instanceof dojo.gfx.color.Color){
rgb=_5d6.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_5d6).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_5d7);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_5db){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_5db);
}
if(!_5db){
_5db=0;
}
_5db=Math.min(Math.max(-1,_5db),1);
_5db=((_5db+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_5db));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_5e0){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_5e0));
};
dojo.gfx.color.extractRGB=function(_5e1){
var hex="0123456789abcdef";
_5e1=_5e1.toLowerCase();
if(_5e1.indexOf("rgb")==0){
var _5e3=_5e1.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_5e3.splice(1,3);
return ret;
}else{
var _5e5=dojo.gfx.color.hex2rgb(_5e1);
if(_5e5){
return _5e5;
}else{
return dojo.gfx.color.named[_5e1]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _5e7="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_5e7+"]","g"),"")!=""){
return null;
}
if(hex.length==3){
rgb[0]=hex.charAt(0)+hex.charAt(0);
rgb[1]=hex.charAt(1)+hex.charAt(1);
rgb[2]=hex.charAt(2)+hex.charAt(2);
}else{
rgb[0]=hex.substring(0,2);
rgb[1]=hex.substring(2,4);
rgb[2]=hex.substring(4);
}
for(var i=0;i<rgb.length;i++){
rgb[i]=_5e7.indexOf(rgb[i].charAt(0))*16+_5e7.indexOf(rgb[i].charAt(1));
}
return rgb;
};
dojo.gfx.color.rgb2hex=function(r,g,b){
if(dojo.lang.isArray(r)){
g=r[1]||0;
b=r[2]||0;
r=r[0]||0;
}
var ret=dojo.lang.map([r,g,b],function(x){
x=new Number(x);
var s=x.toString(16);
while(s.length<2){
s="0"+s;
}
return s;
});
ret.unshift("#");
return ret.join("");
};
dojo.provide("dojo.lfx.Animation");
dojo.lfx.Line=function(_5f0,end){
this.start=_5f0;
this.end=end;
if(dojo.lang.isArray(_5f0)){
var diff=[];
dojo.lang.forEach(this.start,function(s,i){
diff[i]=this.end[i]-s;
},this);
this.getValue=function(n){
var res=[];
dojo.lang.forEach(this.start,function(s,i){
res[i]=(diff[i]*n)+s;
},this);
return res;
};
}else{
var diff=end-_5f0;
this.getValue=function(n){
return (diff*n)+this.start;
};
}
};
dojo.lfx.easeDefault=function(n){
if(dojo.render.html.khtml){
return (parseFloat("0.5")+((Math.sin((n+parseFloat("1.5"))*Math.PI))/2));
}else{
return (0.5+((Math.sin((n+1.5)*Math.PI))/2));
}
};
dojo.lfx.easeIn=function(n){
return Math.pow(n,3);
};
dojo.lfx.easeOut=function(n){
return (1-Math.pow(1-n,3));
};
dojo.lfx.easeInOut=function(n){
return ((3*Math.pow(n,2))-(2*Math.pow(n,3)));
};
dojo.lfx.IAnimation=function(){
};
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:25,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_5ff,_600){
if(!_600){
_600=_5ff;
_5ff=this;
}
_600=dojo.lang.hitch(_5ff,_600);
var _601=this[evt]||function(){
};
this[evt]=function(){
var ret=_601.apply(this,arguments);
_600.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_605){
this.repeatCount=_605;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_606,_607,_608,_609,_60a,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_606)||(!_606&&_607.getValue)){
rate=_60a;
_60a=_609;
_609=_608;
_608=_607;
_607=_606;
_606=null;
}else{
if(_606.getValue||dojo.lang.isArray(_606)){
rate=_609;
_60a=_608;
_609=_607;
_608=_606;
_607=null;
_606=null;
}
}
if(dojo.lang.isArray(_608)){
this.curve=new dojo.lfx.Line(_608[0],_608[1]);
}else{
this.curve=_608;
}
if(_607!=null&&_607>0){
this.duration=_607;
}
if(_60a){
this.repeatCount=_60a;
}
if(rate){
this.rate=rate;
}
if(_606){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_606[item]){
this.connect(item,_606[item]);
}
},this);
}
if(_609&&dojo.lang.isFunction(_609)){
this.easing=_609;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_60d,_60e){
if(_60e){
clearTimeout(this._timer);
this._active=false;
this._paused=false;
this._percent=0;
}else{
if(this._active&&!this._paused){
return this;
}
}
this.fire("handler",["beforeBegin"]);
this.fire("beforeBegin");
if(_60d>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_60e);
}),_60d);
return this;
}
this._startTime=new Date().valueOf();
if(this._paused){
this._startTime-=(this.duration*this._percent/100);
}
this._endTime=this._startTime+this.duration;
this._active=true;
this._paused=false;
var step=this._percent/100;
var _610=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_610]);
this.fire("onBegin",[_610]);
}
this.fire("handler",["play",_610]);
this.fire("onPlay",[_610]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _611=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_611]);
this.fire("onPause",[_611]);
return this;
},gotoPercent:function(pct,_613){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_613){
this.play();
}
return this;
},stop:function(_614){
clearTimeout(this._timer);
var step=this._percent/100;
if(_614){
step=1;
}
var _616=this.curve.getValue(step);
this.fire("handler",["stop",_616]);
this.fire("onStop",[_616]);
this._active=false;
this._paused=false;
return this;
},status:function(){
if(this._active){
return this._paused?"paused":"playing";
}else{
return "stopped";
}
return this;
},_cycle:function(){
clearTimeout(this._timer);
if(this._active){
var curr=new Date().valueOf();
var step=(curr-this._startTime)/(this._endTime-this._startTime);
if(step>=1){
step=1;
this._percent=100;
}else{
this._percent=step*100;
}
if((this.easing)&&(dojo.lang.isFunction(this.easing))){
step=this.easing(step);
}
var _619=this.curve.getValue(step);
this.fire("handler",["animate",_619]);
this.fire("onAnimate",[_619]);
if(step<1){
this._timer=setTimeout(dojo.lang.hitch(this,"_cycle"),this.rate);
}else{
this._active=false;
this.fire("handler",["end"]);
this.fire("onEnd");
if(this.repeatCount>0){
this.repeatCount--;
this.play(null,true);
}else{
if(this.repeatCount==-1){
this.play(null,true);
}else{
if(this._startRepeatCount){
this.repeatCount=this._startRepeatCount;
this._startRepeatCount=0;
}
}
}
}
}
return this;
}});
dojo.lfx.Combine=function(_61a){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _61b=arguments;
if(_61b.length==1&&(dojo.lang.isArray(_61b[0])||dojo.lang.isArrayLike(_61b[0]))){
_61b=_61b[0];
}
dojo.lang.forEach(_61b,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_61d,_61e){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_61d>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_61e);
}),_61d);
return this;
}
if(_61e||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_61e);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_61f){
this.fire("onStop");
this._animsCall("stop",_61f);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_620){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _623=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_620](args);
},_623);
return this;
}});
dojo.lfx.Chain=function(_625){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _626=arguments;
if(_626.length==1&&(dojo.lang.isArray(_626[0])||dojo.lang.isArrayLike(_626[0]))){
_626=_626[0];
}
var _627=this;
dojo.lang.forEach(_626,function(anim,i,_62a){
this._anims.push(anim);
if(i<_62a.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_62b,_62c){
if(!this._anims.length){
return this;
}
if(_62c||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _62d=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_62b>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_62c);
}),_62b);
return this;
}
if(_62d){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_62d.play(null,_62c);
}
return this;
},pause:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].pause();
this.fire("onPause",[this._currAnim]);
}
return this;
},playPause:function(){
if(this._anims.length==0){
return this;
}
if(this._currAnim==-1){
this._currAnim=0;
}
var _62e=this._anims[this._currAnim];
if(_62e){
if(!_62e._active||_62e._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _62f=this._anims[this._currAnim];
if(_62f){
_62f.stop();
this.fire("onStop",[this._currAnim]);
}
return _62f;
},_playNext:function(){
if(this._currAnim==-1||this._anims.length==0){
return this;
}
this._currAnim++;
if(this._anims[this._currAnim]){
this._anims[this._currAnim].play(null,true);
}
return this;
}});
dojo.lfx.combine=function(_630){
var _631=arguments;
if(dojo.lang.isArray(arguments[0])){
_631=arguments[0];
}
if(_631.length==1){
return _631[0];
}
return new dojo.lfx.Combine(_631);
};
dojo.lfx.chain=function(_632){
var _633=arguments;
if(dojo.lang.isArray(arguments[0])){
_633=arguments[0];
}
if(_633.length==1){
return _633[0];
}
return new dojo.lfx.Chain(_633);
};
dojo.provide("dojo.html.color");
dojo.html.getBackgroundColor=function(node){
node=dojo.byId(node);
var _635;
do{
_635=dojo.html.getStyle(node,"background-color");
if(_635.toLowerCase()=="rgba(0, 0, 0, 0)"){
_635="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_635));
if(_635=="transparent"){
_635=[255,255,255,0];
}else{
_635=dojo.gfx.color.extractRGB(_635);
}
return _635;
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_636){
if(!_636){
return [];
}
if(dojo.lang.isArrayLike(_636)){
if(!_636.alreadyChecked){
var n=[];
dojo.lang.forEach(_636,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _636;
}
}else{
var n=[];
n.push(dojo.byId(_636));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_639,_63a,_63b,_63c,_63d){
_639=dojo.lfx.html._byId(_639);
var _63e={"propertyMap":_63a,"nodes":_639,"duration":_63b,"easing":_63c||dojo.lfx.easeDefault};
var _63f=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _643 in pm){
pm[_643].property=_643;
parr.push(pm[_643]);
}
pm=args.propertyMap=parr;
}
dojo.lang.forEach(pm,function(prop){
if(dj_undef("start",prop)){
if(prop.property!="opacity"){
prop.start=parseInt(dojo.html.getComputedStyle(args.nodes[0],prop.property));
}else{
prop.start=dojo.html.getOpacity(args.nodes[0]);
}
}
});
}
};
var _645=function(_646){
var _647=[];
dojo.lang.forEach(_646,function(c){
_647.push(Math.round(c));
});
return _647;
};
var _649=function(n,_64b){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _64b){
try{
if(s=="opacity"){
dojo.html.setOpacity(n,_64b[s]);
}else{
n.style[s]=_64b[s];
}
}
catch(e){
dojo.debug(e);
}
}
};
var _64d=function(_64e){
this._properties=_64e;
this.diffs=new Array(_64e.length);
dojo.lang.forEach(_64e,function(prop,i){
if(dojo.lang.isFunction(prop.start)){
prop.start=prop.start(prop,i);
}
if(dojo.lang.isFunction(prop.end)){
prop.end=prop.end(prop,i);
}
if(dojo.lang.isArray(prop.start)){
this.diffs[i]=null;
}else{
if(prop.start instanceof dojo.gfx.color.Color){
prop.startRgb=prop.start.toRgb();
prop.endRgb=prop.end.toRgb();
}else{
this.diffs[i]=prop.end-prop.start;
}
}
},this);
this.getValue=function(n){
var ret={};
dojo.lang.forEach(this._properties,function(prop,i){
var _655=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_655=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_655+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_655+=")";
}else{
_655=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_655;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_63f(_63e);
anim.curve=new _64d(_63e.propertyMap);
},onAnimate:function(_658){
dojo.lang.forEach(_63e.nodes,function(node){
_649(node,_658);
});
}},_63e.duration,null,_63e.easing);
if(_63d){
for(var x in _63d){
if(dojo.lang.isFunction(_63d[x])){
anim.connect(x,anim,_63d[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_65b){
var _65c=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_65b)){
dojo.lang.forEach(_65b,_65c);
}else{
_65c(_65b);
}
};
dojo.lfx.html.fade=function(_65e,_65f,_660,_661,_662){
_65e=dojo.lfx.html._byId(_65e);
var _663={property:"opacity"};
if(!dj_undef("start",_65f)){
_663.start=_65f.start;
}else{
_663.start=function(){
return dojo.html.getOpacity(_65e[0]);
};
}
if(!dj_undef("end",_65f)){
_663.end=_65f.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_65e,[_663],_660,_661);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_65e);
});
if(_662){
anim.connect("onEnd",function(){
_662(_65e,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_665,_666,_667,_668){
return dojo.lfx.html.fade(_665,{end:1},_666,_667,_668);
};
dojo.lfx.html.fadeOut=function(_669,_66a,_66b,_66c){
return dojo.lfx.html.fade(_669,{end:0},_66a,_66b,_66c);
};
dojo.lfx.html.fadeShow=function(_66d,_66e,_66f,_670){
_66d=dojo.lfx.html._byId(_66d);
dojo.lang.forEach(_66d,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_66d,_66e,_66f,_670);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_66d)){
dojo.lang.forEach(_66d,dojo.html.show);
}else{
dojo.html.show(_66d);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_673,_674,_675,_676){
var anim=dojo.lfx.html.fadeOut(_673,_674,_675,function(){
if(dojo.lang.isArrayLike(_673)){
dojo.lang.forEach(_673,dojo.html.hide);
}else{
dojo.html.hide(_673);
}
if(_676){
_676(_673,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_678,_679,_67a,_67b){
_678=dojo.lfx.html._byId(_678);
var _67c=[];
dojo.lang.forEach(_678,function(node){
var _67e={};
var _67f,_680,_681;
with(node.style){
_67f=top;
_680=left;
_681=position;
top="-9999px";
left="-9999px";
position="absolute";
display="";
}
var _682=dojo.html.getBorderBox(node).height;
with(node.style){
top=_67f;
left=_680;
position=_681;
display="none";
}
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _682;
}}},_679,_67a);
anim.connect("beforeBegin",function(){
_67e.overflow=node.style.overflow;
_67e.height=node.style.height;
with(node.style){
overflow="hidden";
_682="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_67e.overflow;
_682=_67e.height;
}
if(_67b){
_67b(node,anim);
}
});
_67c.push(anim);
});
return dojo.lfx.combine(_67c);
};
dojo.lfx.html.wipeOut=function(_684,_685,_686,_687){
_684=dojo.lfx.html._byId(_684);
var _688=[];
dojo.lang.forEach(_684,function(node){
var _68a={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_685,_686,{"beforeBegin":function(){
_68a.overflow=node.style.overflow;
_68a.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_68a.overflow;
height=_68a.height;
}
if(_687){
_687(node,anim);
}
}});
_688.push(anim);
});
return dojo.lfx.combine(_688);
};
dojo.lfx.html.slideTo=function(_68c,_68d,_68e,_68f,_690){
_68c=dojo.lfx.html._byId(_68c);
var _691=[];
var _692=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_68d)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_68d={top:_68d[0],left:_68d[1]};
}
dojo.lang.forEach(_68c,function(node){
var top=null;
var left=null;
var init=(function(){
var _697=node;
return function(){
var pos=_692(_697,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_692(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_692(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_697,true);
dojo.html.setStyleAttributes(_697,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_68d.top||0)},"left":{start:left,end:(_68d.left||0)}},_68e,_68f,{"beforeBegin":init});
if(_690){
anim.connect("onEnd",function(){
_690(_68c,anim);
});
}
_691.push(anim);
});
return dojo.lfx.combine(_691);
};
dojo.lfx.html.slideBy=function(_69b,_69c,_69d,_69e,_69f){
_69b=dojo.lfx.html._byId(_69b);
var _6a0=[];
var _6a1=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_69c)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_69c={top:_69c[0],left:_69c[1]};
}
dojo.lang.forEach(_69b,function(node){
var top=null;
var left=null;
var init=(function(){
var _6a6=node;
return function(){
var pos=_6a1(_6a6,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_6a1(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_6a1(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_6a6,true);
dojo.html.setStyleAttributes(_6a6,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_69c.top||0)},"left":{start:left,end:left+(_69c.left||0)}},_69d,_69e).connect("beforeBegin",init);
if(_69f){
anim.connect("onEnd",function(){
_69f(_69b,anim);
});
}
_6a0.push(anim);
});
return dojo.lfx.combine(_6a0);
};
dojo.lfx.html.explode=function(_6aa,_6ab,_6ac,_6ad,_6ae){
var h=dojo.html;
_6aa=dojo.byId(_6aa);
_6ab=dojo.byId(_6ab);
var _6b0=h.toCoordinateObject(_6aa,true);
var _6b1=document.createElement("div");
h.copyStyle(_6b1,_6ab);
if(_6ab.explodeClassName){
_6b1.className=_6ab.explodeClassName;
}
with(_6b1.style){
position="absolute";
display="none";
var _6b2=h.getStyle(_6aa,"background-color");
backgroundColor=_6b2?_6b2.toLowerCase():"transparent";
backgroundColor=(backgroundColor=="transparent")?"rgb(221, 221, 221)":backgroundColor;
}
dojo.body().appendChild(_6b1);
with(_6ab.style){
visibility="hidden";
display="block";
}
var _6b3=h.toCoordinateObject(_6ab,true);
with(_6ab.style){
display="none";
visibility="visible";
}
var _6b4={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_6b4[type]={start:_6b0[type],end:_6b3[type]};
});
var anim=new dojo.lfx.propertyAnimation(_6b1,_6b4,_6ac,_6ad,{"beforeBegin":function(){
h.setDisplay(_6b1,"block");
},"onEnd":function(){
h.setDisplay(_6ab,"block");
_6b1.parentNode.removeChild(_6b1);
}});
if(_6ae){
anim.connect("onEnd",function(){
_6ae(_6ab,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_6b7,end,_6b9,_6ba,_6bb){
var h=dojo.html;
_6b7=dojo.byId(_6b7);
end=dojo.byId(end);
var _6bd=dojo.html.toCoordinateObject(_6b7,true);
var _6be=dojo.html.toCoordinateObject(end,true);
var _6bf=document.createElement("div");
dojo.html.copyStyle(_6bf,_6b7);
if(_6b7.explodeClassName){
_6bf.className=_6b7.explodeClassName;
}
dojo.html.setOpacity(_6bf,0.3);
with(_6bf.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_6b7,"background-color").toLowerCase();
}
dojo.body().appendChild(_6bf);
var _6c0={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_6c0[type]={start:_6bd[type],end:_6be[type]};
});
var anim=new dojo.lfx.propertyAnimation(_6bf,_6c0,_6b9,_6ba,{"beforeBegin":function(){
dojo.html.hide(_6b7);
dojo.html.show(_6bf);
},"onEnd":function(){
_6bf.parentNode.removeChild(_6bf);
}});
if(_6bb){
anim.connect("onEnd",function(){
_6bb(_6b7,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_6c3,_6c4,_6c5,_6c6,_6c7){
_6c3=dojo.lfx.html._byId(_6c3);
var _6c8=[];
dojo.lang.forEach(_6c3,function(node){
var _6ca=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _6cc=dojo.html.getStyle(node,"background-image");
var _6cd=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_6ca.length>3){
_6ca.pop();
}
var rgb=new dojo.gfx.color.Color(_6c4);
var _6cf=new dojo.gfx.color.Color(_6ca);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_6cf}},_6c5,_6c6,{"beforeBegin":function(){
if(_6cc){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_6cc){
node.style.backgroundImage=_6cc;
}
if(_6cd){
node.style.backgroundColor="transparent";
}
if(_6c7){
_6c7(node,anim);
}
}});
_6c8.push(anim);
});
return dojo.lfx.combine(_6c8);
};
dojo.lfx.html.unhighlight=function(_6d1,_6d2,_6d3,_6d4,_6d5){
_6d1=dojo.lfx.html._byId(_6d1);
var _6d6=[];
dojo.lang.forEach(_6d1,function(node){
var _6d8=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_6d2);
var _6da=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_6d8,end:rgb}},_6d3,_6d4,{"beforeBegin":function(){
if(_6da){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_6d8.toRgb().join(",")+")";
},"onEnd":function(){
if(_6d5){
_6d5(node,anim);
}
}});
_6d6.push(anim);
});
return dojo.lfx.combine(_6d6);
};
dojo.lang.mixin(dojo.lfx,dojo.lfx.html);
dojo.provide("dojo.lfx.*");
dojo.provide("dojo.lfx.toggle");
dojo.lfx.toggle.plain={show:function(node,_6dd,_6de,_6df){
dojo.html.show(node);
if(dojo.lang.isFunction(_6df)){
_6df();
}
},hide:function(node,_6e1,_6e2,_6e3){
dojo.html.hide(node);
if(dojo.lang.isFunction(_6e3)){
_6e3();
}
}};
dojo.lfx.toggle.fade={show:function(node,_6e5,_6e6,_6e7){
dojo.lfx.fadeShow(node,_6e5,_6e6,_6e7).play();
},hide:function(node,_6e9,_6ea,_6eb){
dojo.lfx.fadeHide(node,_6e9,_6ea,_6eb).play();
}};
dojo.lfx.toggle.wipe={show:function(node,_6ed,_6ee,_6ef){
dojo.lfx.wipeIn(node,_6ed,_6ee,_6ef).play();
},hide:function(node,_6f1,_6f2,_6f3){
dojo.lfx.wipeOut(node,_6f1,_6f2,_6f3).play();
}};
dojo.lfx.toggle.explode={show:function(node,_6f5,_6f6,_6f7,_6f8){
dojo.lfx.explode(_6f8||{x:0,y:0,width:0,height:0},node,_6f5,_6f6,_6f7).play();
},hide:function(node,_6fa,_6fb,_6fc,_6fd){
dojo.lfx.implode(node,_6fd||{x:0,y:0,width:0,height:0},_6fa,_6fb,_6fc).play();
}};
dojo.provide("dojo.widget.HtmlWidget");
dojo.declare("dojo.widget.HtmlWidget",dojo.widget.DomWidget,{templateCssPath:null,templatePath:null,lang:"",toggle:"plain",toggleDuration:150,initialize:function(args,frag){
},postMixInProperties:function(args,frag){
if(this.lang===""){
this.lang=null;
}
this.toggleObj=dojo.lfx.toggle[this.toggle.toLowerCase()]||dojo.lfx.toggle.plain;
},createNodesFromText:function(txt,wrap){
return dojo.html.createNodesFromText(txt,wrap);
},destroyRendering:function(_704){
try{
if(this.bgIframe){
this.bgIframe.remove();
delete this.bgIframe;
}
if(!_704&&this.domNode){
dojo.event.browser.clean(this.domNode);
}
dojo.widget.HtmlWidget.superclass.destroyRendering.call(this);
}
catch(e){
}
},isShowing:function(){
return dojo.html.isShowing(this.domNode);
},toggleShowing:function(){
if(this.isShowing()){
this.hide();
}else{
this.show();
}
},show:function(){
if(this.isShowing()){
return;
}
this.animationInProgress=true;
this.toggleObj.show(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onShow),this.explodeSrc);
},onShow:function(){
this.animationInProgress=false;
this.checkSize();
},hide:function(){
if(!this.isShowing()){
return;
}
this.animationInProgress=true;
this.toggleObj.hide(this.domNode,this.toggleDuration,null,dojo.lang.hitch(this,this.onHide),this.explodeSrc);
},onHide:function(){
this.animationInProgress=false;
},_isResized:function(w,h){
if(!this.isShowing()){
return false;
}
var wh=dojo.html.getMarginBox(this.domNode);
var _708=w||wh.width;
var _709=h||wh.height;
if(this.width==_708&&this.height==_709){
return false;
}
this.width=_708;
this.height=_709;
return true;
},checkSize:function(){
if(!this._isResized()){
return;
}
this.onResized();
},resizeTo:function(w,h){
dojo.html.setMarginBox(this.domNode,{width:w,height:h});
if(this.isShowing()){
this.onResized();
}
},resizeSoon:function(){
if(this.isShowing()){
dojo.lang.setTimeout(this,this.onResized,0);
}
},onResized:function(){
dojo.lang.forEach(this.children,function(_70c){
if(_70c.checkSize){
_70c.checkSize();
}
});
}});
dojo.provide("dojo.widget.*");
dojo.provide("dojo.string.common");
dojo.string.trim=function(str,wh){
if(!str.replace){
return str;
}
if(!str.length){
return str;
}
var re=(wh>0)?(/^\s+/):(wh<0)?(/\s+$/):(/^\s+|\s+$/g);
return str.replace(re,"");
};
dojo.string.trimStart=function(str){
return dojo.string.trim(str,1);
};
dojo.string.trimEnd=function(str){
return dojo.string.trim(str,-1);
};
dojo.string.repeat=function(str,_713,_714){
var out="";
for(var i=0;i<_713;i++){
out+=str;
if(_714&&i<_713-1){
out+=_714;
}
}
return out;
};
dojo.string.pad=function(str,len,c,dir){
var out=String(str);
if(!c){
c="0";
}
if(!dir){
dir=1;
}
while(out.length<len){
if(dir>0){
out=c+out;
}else{
out+=c;
}
}
return out;
};
dojo.string.padLeft=function(str,len,c){
return dojo.string.pad(str,len,c,1);
};
dojo.string.padRight=function(str,len,c){
return dojo.string.pad(str,len,c,-1);
};
dojo.provide("dojo.string");
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_723,_724,_725){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_723){
this.mimetype=_723;
}
if(_724){
this.transport=_724;
}
if(arguments.length>=4){
this.changeUrl=_725;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,_728,_729){
},error:function(type,_72b,_72c,_72d){
},timeout:function(type,_72f,_730,_731){
},handle:function(type,data,_734,_735){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_736){
if(_736["url"]){
_736.url=_736.url.toString();
}
if(_736["formNode"]){
_736.formNode=dojo.byId(_736.formNode);
}
if(!_736["method"]&&_736["formNode"]&&_736["formNode"].method){
_736.method=_736["formNode"].method;
}
if(!_736["handle"]&&_736["handler"]){
_736.handle=_736.handler;
}
if(!_736["load"]&&_736["loaded"]){
_736.load=_736.loaded;
}
if(!_736["changeUrl"]&&_736["changeURL"]){
_736.changeUrl=_736.changeURL;
}
_736.encoding=dojo.lang.firstValued(_736["encoding"],djConfig["bindEncoding"],"");
_736.sendTransport=dojo.lang.firstValued(_736["sendTransport"],djConfig["ioSendTransport"],false);
var _737=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_736[fn]&&_737(_736[fn])){
continue;
}
if(_736["handle"]&&_737(_736["handle"])){
_736[fn]=_736.handle;
}
}
dojo.lang.mixin(this,_736);
}});
dojo.io.Error=function(msg,type,num){
this.message=msg;
this.type=type||"unknown";
this.number=num||0;
};
dojo.io.transports.addTransport=function(name){
this.push(name);
this[name]=dojo.io[name];
};
dojo.io.bind=function(_73e){
if(!(_73e instanceof dojo.io.Request)){
try{
_73e=new dojo.io.Request(_73e);
}
catch(e){
dojo.debug(e);
}
}
var _73f="";
if(_73e["transport"]){
_73f=_73e["transport"];
if(!this[_73f]){
dojo.io.sendBindError(_73e,"No dojo.io.bind() transport with name '"+_73e["transport"]+"'.");
return _73e;
}
if(!this[_73f].canHandle(_73e)){
dojo.io.sendBindError(_73e,"dojo.io.bind() transport with name '"+_73e["transport"]+"' cannot handle this type of request.");
return _73e;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_73e))){
_73f=tmp;
break;
}
}
if(_73f==""){
dojo.io.sendBindError(_73e,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _73e;
}
}
this[_73f].bind(_73e);
_73e.bindSuccess=true;
return _73e;
};
dojo.io.sendBindError=function(_742,_743){
if((typeof _742.error=="function"||typeof _742.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _744=new dojo.io.Error(_743);
setTimeout(function(){
_742[(typeof _742.error=="function")?"error":"handle"]("error",_744,null,_742);
},50);
}else{
dojo.raise(_743);
}
};
dojo.io.queueBind=function(_745){
if(!(_745 instanceof dojo.io.Request)){
try{
_745=new dojo.io.Request(_745);
}
catch(e){
dojo.debug(e);
}
}
var _746=_745.load;
_745.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_746.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _748=_745.error;
_745.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_748.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_745);
dojo.io._dispatchNextQueueBind();
return _745;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
if(dojo.io._bindQueue.length>0){
dojo.io.bind(dojo.io._bindQueue.shift());
}else{
dojo.io._queueBindInFlight=false;
}
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_74b,last){
var enc=/utf/i.test(_74b||"")?encodeURIComponent:dojo.string.encodeAscii;
var _74e=[];
var _74f=new Object();
for(var name in map){
var _751=function(elt){
var val=enc(name)+"="+enc(elt);
_74e[(last==name)?"push":"unshift"](val);
};
if(!_74f[name]){
var _754=map[name];
if(dojo.lang.isArray(_754)){
dojo.lang.forEach(_754,_751);
}else{
_751(_754);
}
}
}
return _74e.join("&");
};
dojo.io.setIFrameSrc=function(_755,src,_757){
try{
var r=dojo.render.html;
if(!_757){
if(r.safari){
_755.location=src;
}else{
frames[_755.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_755.contentWindow.document;
}else{
if(r.safari){
idoc=_755.document;
}else{
idoc=_755.contentWindow;
}
}
if(!idoc){
_755.location=src;
return;
}else{
idoc.location.replace(src);
}
}
}
catch(e){
dojo.debug(e);
dojo.debug("setIFrameSrc: "+e);
}
};
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_75a,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _75a.replace(/\%\{(\w+)\}/g,function(_75d,key){
if(typeof (map[key])!="undefined"&&map[key]!=null){
return map[key];
}
dojo.raise("Substitution not found: "+key);
});
};
dojo.string.capitalize=function(str){
if(!dojo.lang.isString(str)){
return "";
}
if(arguments.length==0){
str=this;
}
var _760=str.split(" ");
for(var i=0;i<_760.length;i++){
_760[i]=_760[i].charAt(0).toUpperCase()+_760[i].substring(1);
}
return _760.join(" ");
};
dojo.string.isBlank=function(str){
if(!dojo.lang.isString(str)){
return true;
}
return (dojo.string.trim(str).length==0);
};
dojo.string.encodeAscii=function(str){
if(!dojo.lang.isString(str)){
return str;
}
var ret="";
var _765=escape(str);
var _766,re=/%u([0-9A-F]{4})/i;
while((_766=_765.match(re))){
var num=Number("0x"+_766[1]);
var _769=escape("&#"+num+";");
ret+=_765.substring(0,_766.index)+_769;
_765=_765.substring(_766.index+_766[0].length);
}
ret+=_765.replace(/\+/g,"%2B");
return ret;
};
dojo.string.escape=function(type,str){
var args=dojo.lang.toArray(arguments,1);
switch(type.toLowerCase()){
case "xml":
case "html":
case "xhtml":
return dojo.string.escapeXml.apply(this,args);
case "sql":
return dojo.string.escapeSql.apply(this,args);
case "regexp":
case "regex":
return dojo.string.escapeRegExp.apply(this,args);
case "javascript":
case "jscript":
case "js":
return dojo.string.escapeJavaScript.apply(this,args);
case "ascii":
return dojo.string.encodeAscii.apply(this,args);
default:
return str;
}
};
dojo.string.escapeXml=function(str,_76e){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_76e){
str=str.replace(/'/gm,"&#39;");
}
return str;
};
dojo.string.escapeSql=function(str){
return str.replace(/'/gm,"''");
};
dojo.string.escapeRegExp=function(str){
return str.replace(/\\/gm,"\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm,"\\$1");
};
dojo.string.escapeJavaScript=function(str){
return str.replace(/(["'\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.escapeString=function(str){
return ("\""+str.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");
};
dojo.string.summary=function(str,len){
if(!len||str.length<=len){
return str;
}
return str.substring(0,len).replace(/\.+$/,"")+"...";
};
dojo.string.endsWith=function(str,end,_777){
if(_777){
str=str.toLowerCase();
end=end.toLowerCase();
}
if((str.length-end.length)<0){
return false;
}
return str.lastIndexOf(end)==str.length-end.length;
};
dojo.string.endsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.endsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.startsWith=function(str,_77b,_77c){
if(_77c){
str=str.toLowerCase();
_77b=_77b.toLowerCase();
}
return str.indexOf(_77b)==0;
};
dojo.string.startsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.startsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.has=function(str){
for(var i=1;i<arguments.length;i++){
if(str.indexOf(arguments[i])>-1){
return true;
}
}
return false;
};
dojo.string.normalizeNewlines=function(text,_782){
if(_782=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_782=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_784){
var _785=[];
for(var i=0,_787=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_784){
_785.push(str.substring(_787,i));
_787=i+1;
}
}
_785.push(str.substr(_787));
return _785;
};
dojo.provide("dojo.undo.browser");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
if(dojo.render.html.opera){
dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser={initialHref:(!dj_undef("window"))?window.location.href:"",initialHash:(!dj_undef("window"))?window.location.hash:"",moveForward:false,historyStack:[],forwardStack:[],historyIframe:null,bookmarkAnchor:null,locationTimer:null,setInitialState:function(args){
this.initialState=this._createState(this.initialHref,args,this.initialHash);
},addToHistory:function(args){
this.forwardStack=[];
var hash=null;
var url=null;
if(!this.historyIframe){
this.historyIframe=window.frames["djhistory"];
}
if(!this.bookmarkAnchor){
this.bookmarkAnchor=document.createElement("a");
dojo.body().appendChild(this.bookmarkAnchor);
this.bookmarkAnchor.style.display="none";
}
if(args["changeUrl"]){
hash="#"+((args["changeUrl"]!==true)?args["changeUrl"]:(new Date()).getTime());
if(this.historyStack.length==0&&this.initialState.urlHash==hash){
this.initialState=this._createState(url,args,hash);
return;
}else{
if(this.historyStack.length>0&&this.historyStack[this.historyStack.length-1].urlHash==hash){
this.historyStack[this.historyStack.length-1]=this._createState(url,args,hash);
return;
}
}
this.changingUrl=true;
setTimeout("window.location.href = '"+hash+"'; dojo.undo.browser.changingUrl = false;",1);
this.bookmarkAnchor.href=hash;
if(dojo.render.html.ie){
url=this._loadIframeHistory();
var _78c=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_78e){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_78c.apply(this,[_78e]);
};
if(args["back"]){
args.back=tcb;
}else{
if(args["backButton"]){
args.backButton=tcb;
}else{
if(args["handle"]){
args.handle=tcb;
}
}
}
var _78f=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_791){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_78f){
_78f.apply(this,[_791]);
}
};
if(args["forward"]){
args.forward=tfw;
}else{
if(args["forwardButton"]){
args.forwardButton=tfw;
}else{
if(args["handle"]){
args.handle=tfw;
}
}
}
}else{
if(dojo.render.html.moz){
if(!this.locationTimer){
this.locationTimer=setInterval("dojo.undo.browser.checkLocation();",200);
}
}
}
}else{
url=this._loadIframeHistory();
}
this.historyStack.push(this._createState(url,args,hash));
},checkLocation:function(){
if(!this.changingUrl){
var hsl=this.historyStack.length;
if((window.location.hash==this.initialHash||window.location.href==this.initialHref)&&(hsl==1)){
this.handleBackButton();
return;
}
if(this.forwardStack.length>0){
if(this.forwardStack[this.forwardStack.length-1].urlHash==window.location.hash){
this.handleForwardButton();
return;
}
}
if((hsl>=2)&&(this.historyStack[hsl-2])){
if(this.historyStack[hsl-2].urlHash==window.location.hash){
this.handleBackButton();
return;
}
}
}
},iframeLoaded:function(evt,_794){
if(!dojo.render.html.opera){
var _795=this._getUrlQuery(_794.href);
if(_795==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_795==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_795==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _796=this.historyStack.pop();
if(!_796){
return;
}
var last=this.historyStack[this.historyStack.length-1];
if(!last&&this.historyStack.length==0){
last=this.initialState;
}
if(last){
if(last.kwArgs["back"]){
last.kwArgs["back"]();
}else{
if(last.kwArgs["backButton"]){
last.kwArgs["backButton"]();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("back");
}
}
}
}
this.forwardStack.push(_796);
},handleForwardButton:function(){
var last=this.forwardStack.pop();
if(!last){
return;
}
if(last.kwArgs["forward"]){
last.kwArgs.forward();
}else{
if(last.kwArgs["forwardButton"]){
last.kwArgs.forwardButton();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("forward");
}
}
}
this.historyStack.push(last);
},_createState:function(url,args,hash){
return {"url":url,"kwArgs":args,"urlHash":hash};
},_getUrlQuery:function(url){
var _79d=url.split("?");
if(_79d.length<2){
return null;
}else{
return _79d[1];
}
},_loadIframeHistory:function(){
var url=dojo.hostenv.getBaseScriptUri()+"iframe_history.html?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
return url;
}};
dojo.provide("dojo.io.BrowserIO");
if(!dj_undef("window")){
dojo.io.checkChildrenForFile=function(node){
var _7a0=false;
var _7a1=node.getElementsByTagName("input");
dojo.lang.forEach(_7a1,function(_7a2){
if(_7a0){
return;
}
if(_7a2.getAttribute("type")=="file"){
_7a0=true;
}
});
return _7a0;
};
dojo.io.formHasFile=function(_7a3){
return dojo.io.checkChildrenForFile(_7a3);
};
dojo.io.updateNode=function(node,_7a5){
node=dojo.byId(node);
var args=_7a5;
if(dojo.lang.isString(_7a5)){
args={url:_7a5};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
dojo.dom.destroyNode(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_7ac,_7ad,_7ae){
if((!_7ac)||(!_7ac.tagName)||(!_7ac.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_7ae){
_7ae=dojo.io.formFilter;
}
var enc=/utf/i.test(_7ad||"")?encodeURIComponent:dojo.string.encodeAscii;
var _7b0=[];
for(var i=0;i<_7ac.elements.length;i++){
var elm=_7ac.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_7ae(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_7b0.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_7b0.push(name+"="+enc(elm.value));
}
}else{
_7b0.push(name+"="+enc(elm.value));
}
}
}
var _7b6=_7ac.getElementsByTagName("input");
for(var i=0;i<_7b6.length;i++){
var _7b7=_7b6[i];
if(_7b7.type.toLowerCase()=="image"&&_7b7.form==_7ac&&_7ae(_7b7)){
var name=enc(_7b7.name);
_7b0.push(name+"="+enc(_7b7.value));
_7b0.push(name+".x=0");
_7b0.push(name+".y=0");
}
}
return _7b0.join("&")+"&";
};
dojo.io.FormBind=function(args){
this.bindArgs={};
if(args&&args.formNode){
this.init(args);
}else{
if(args){
this.init({formNode:args});
}
}
};
dojo.lang.extend(dojo.io.FormBind,{form:null,bindArgs:null,clickedButton:null,init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _7bd=form.getElementsByTagName("input");
for(var i=0;i<_7bd.length;i++){
var _7be=_7bd[i];
if(_7be.type.toLowerCase()=="image"&&_7be.form==form){
this.connect(_7be,"onclick","click");
}
}
},onSubmit:function(form){
return true;
},submit:function(e){
e.preventDefault();
if(this.onSubmit(this.form)){
dojo.io.bind(dojo.lang.mixin(this.bindArgs,{formFilter:dojo.lang.hitch(this,"formFilter")}));
}
},click:function(e){
var node=e.currentTarget;
if(node.disabled){
return;
}
this.clickedButton=node;
},formFilter:function(node){
var type=(node.type||"").toLowerCase();
var _7c5=false;
if(node.disabled||!node.name){
_7c5=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_7c5=node==this.clickedButton;
}else{
_7c5=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _7c5;
},connect:function(_7c6,_7c7,_7c8){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_7c6,_7c7,this,_7c8);
}else{
var fcn=dojo.lang.hitch(this,_7c8);
_7c6[_7c7]=function(e){
if(!e){
e=window.event;
}
if(!e.currentTarget){
e.currentTarget=e.srcElement;
}
if(!e.preventDefault){
e.preventDefault=function(){
window.event.returnValue=false;
};
}
fcn(e);
};
}
}});
dojo.io.XMLHTTPTransport=new function(){
var _7cb=this;
var _7cc={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_7ce,_7cf){
return url+"|"+_7ce+"|"+_7cf.toLowerCase();
}
function addToCache(url,_7d1,_7d2,http){
_7cc[getCacheKey(url,_7d1,_7d2)]=http;
}
function getFromCache(url,_7d5,_7d6){
return _7cc[getCacheKey(url,_7d5,_7d6)];
}
this.clearCache=function(){
_7cc={};
};
function doLoad(_7d7,http,url,_7da,_7db){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_7d7.method.toLowerCase()=="head"){
var _7dd=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _7dd;
};
var _7de=_7dd.split(/[\r\n]+/g);
for(var i=0;i<_7de.length;i++){
var pair=_7de[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_7d7.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_7d7.mimetype=="text/json"||_7d7.mimetype=="application/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_7d7.mimetype=="application/xml")||(_7d7.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"||!http.getResponseHeader("Content-Type")){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_7db){
addToCache(url,_7da,_7d7.method,http);
}
_7d7[(typeof _7d7.load=="function")?"load":"handle"]("load",ret,http,_7d7);
}else{
var _7e1=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_7d7[(typeof _7d7.error=="function")?"error":"handle"]("error",_7e1,http,_7d7);
}
}
function setHeaders(http,_7e3){
if(_7e3["headers"]){
for(var _7e4 in _7e3["headers"]){
if(_7e4.toLowerCase()=="content-type"&&!_7e3["contentType"]){
_7e3["contentType"]=_7e3["headers"][_7e4];
}else{
http.setRequestHeader(_7e4,_7e3["headers"][_7e4]);
}
}
}
}
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
var now=null;
if(!dojo.hostenv._blockAsync&&!_7cb._blockAsync){
for(var x=this.inFlight.length-1;x>=0;x--){
try{
var tif=this.inFlight[x];
if(!tif||tif.http._aborted||!tif.http.readyState){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
}else{
if(tif.startTime){
if(!now){
now=(new Date()).getTime();
}
if(tif.startTime+(tif.req.timeoutSeconds*1000)<now){
if(typeof tif.http.abort=="function"){
tif.http.abort();
}
this.inFlight.splice(x,1);
tif.req[(typeof tif.req.timeout=="function")?"timeout":"handle"]("timeout",null,tif.http,tif.req);
}
}
}
}
catch(e){
try{
var _7e8=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_7e8,tif.http,tif.req);
}
catch(e2){
dojo.debug("XMLHttpTransport error callback failed: "+e2);
}
}
}
}
clearTimeout(this.inFlightTimer);
if(this.inFlight.length==0){
this.inFlightTimer=null;
return;
}
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
};
var _7e9=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_7ea){
return _7e9&&dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript","text/json","application/json"],(_7ea["mimetype"].toLowerCase()||""))&&!(_7ea["formNode"]&&dojo.io.formHasFile(_7ea["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_7eb){
if(!_7eb["url"]){
if(!_7eb["formNode"]&&(_7eb["backButton"]||_7eb["back"]||_7eb["changeUrl"]||_7eb["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_7eb);
return true;
}
}
var url=_7eb.url;
var _7ed="";
if(_7eb["formNode"]){
var ta=_7eb.formNode.getAttribute("action");
if((ta)&&(!_7eb["url"])){
url=ta;
}
var tp=_7eb.formNode.getAttribute("method");
if((tp)&&(!_7eb["method"])){
_7eb.method=tp;
}
_7ed+=dojo.io.encodeForm(_7eb.formNode,_7eb.encoding,_7eb["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_7eb["file"]){
_7eb.method="post";
}
if(!_7eb["method"]){
_7eb.method="get";
}
if(_7eb.method.toLowerCase()=="get"){
_7eb.multipart=false;
}else{
if(_7eb["file"]){
_7eb.multipart=true;
}else{
if(!_7eb["multipart"]){
_7eb.multipart=false;
}
}
}
if(_7eb["backButton"]||_7eb["back"]||_7eb["changeUrl"]){
dojo.undo.browser.addToHistory(_7eb);
}
var _7f0=_7eb["content"]||{};
if(_7eb.sendTransport){
_7f0["dojo.transport"]="xmlhttp";
}
do{
if(_7eb.postContent){
_7ed=_7eb.postContent;
break;
}
if(_7f0){
_7ed+=dojo.io.argsFromMap(_7f0,_7eb.encoding);
}
if(_7eb.method.toLowerCase()=="get"||!_7eb.multipart){
break;
}
var t=[];
if(_7ed.length){
var q=_7ed.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_7eb.file){
if(dojo.lang.isArray(_7eb.file)){
for(var i=0;i<_7eb.file.length;++i){
var o=_7eb.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_7eb.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_7ed=t.join("\r\n");
}
}while(false);
var _7f6=_7eb["sync"]?false:true;
var _7f7=_7eb["preventCache"]||(this.preventCache==true&&_7eb["preventCache"]!=false);
var _7f8=_7eb["useCache"]==true||(this.useCache==true&&_7eb["useCache"]!=false);
if(!_7f7&&_7f8){
var _7f9=getFromCache(url,_7ed,_7eb.method);
if(_7f9){
doLoad(_7eb,_7f9,url,_7ed,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_7eb);
var _7fb=false;
if(_7f6){
var _7fc=this.inFlight.push({"req":_7eb,"http":http,"url":url,"query":_7ed,"useCache":_7f8,"startTime":_7eb.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_7cb._blockAsync=true;
}
if(_7eb.method.toLowerCase()=="post"){
if(!_7eb.user){
http.open("POST",url,_7f6);
}else{
http.open("POST",url,_7f6,_7eb.user,_7eb.password);
}
setHeaders(http,_7eb);
http.setRequestHeader("Content-Type",_7eb.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_7eb.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_7ed);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_7eb,{status:404},url,_7ed,_7f8);
}
}else{
var _7fd=url;
if(_7ed!=""){
_7fd+=(_7fd.indexOf("?")>-1?"&":"?")+_7ed;
}
if(_7f7){
_7fd+=(dojo.string.endsWithAny(_7fd,"?","&")?"":(_7fd.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_7eb.user){
http.open(_7eb.method.toUpperCase(),_7fd,_7f6);
}else{
http.open(_7eb.method.toUpperCase(),_7fd,_7f6,_7eb.user,_7eb.password);
}
setHeaders(http,_7eb);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_7eb,{status:404},url,_7ed,_7f8);
}
}
if(!_7f6){
doLoad(_7eb,http,url,_7ed,_7f8);
_7cb._blockAsync=false;
}
_7eb.abort=function(){
try{
http._aborted=true;
}
catch(e){
}
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
}
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_7ff,days,path,_802,_803){
var _804=-1;
if((typeof days=="number")&&(days>=0)){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_804=d.toGMTString();
}
_7ff=escape(_7ff);
document.cookie=name+"="+_7ff+";"+(_804!=-1?" expires="+_804+";":"")+(path?"path="+path:"")+(_802?"; domain="+_802:"")+(_803?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _808=document.cookie.substring(idx+name.length+1);
var end=_808.indexOf(";");
if(end==-1){
end=_808.length;
}
_808=_808.substring(0,end);
_808=unescape(_808);
return _808;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_80f,_810,_811){
if(arguments.length==5){
_811=_80f;
_80f=null;
_810=null;
}
var _812=[],_813,_814="";
if(!_811){
_813=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_813){
_813={};
}
for(var prop in obj){
if(obj[prop]==null){
delete _813[prop];
}else{
if((typeof obj[prop]=="string")||(typeof obj[prop]=="number")){
_813[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _813){
_812.push(escape(prop)+"="+escape(_813[prop]));
}
_814=_812.join("&");
}
dojo.io.cookie.setCookie(name,_814,days,path,_80f,_810);
};
dojo.io.cookie.getObjectCookie=function(name){
var _817=null,_818=dojo.io.cookie.getCookie(name);
if(_818){
_817={};
var _819=_818.split("&");
for(var i=0;i<_819.length;i++){
var pair=_819[i].split("=");
var _81c=pair[1];
if(isNaN(_81c)){
_81c=unescape(pair[1]);
}
_817[unescape(pair[0])]=_81c;
}
}
return _817;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _81d=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_81d=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.provide("dojo.io.*");
dojo.provide("dojo.widget.ContentPane");
dojo.widget.defineWidget("dojo.widget.ContentPane",dojo.widget.HtmlWidget,function(){
this._styleNodes=[];
this._onLoadStack=[];
this._onUnloadStack=[];
this._callOnUnload=false;
this._ioBindObj;
this.scriptScope;
this.bindArgs={};
},{isContainer:true,adjustPaths:true,href:"",extractContent:true,parseContent:true,cacheContent:true,preload:false,refreshOnShow:false,handler:"",executeScripts:false,scriptSeparation:true,loadingMessage:"Loading...",isLoaded:false,postCreate:function(args,frag,_820){
if(this.handler!==""){
this.setHandler(this.handler);
}
if(this.isShowing()||this.preload){
this.loadContents();
}
},show:function(){
if(this.refreshOnShow){
this.refresh();
}else{
this.loadContents();
}
dojo.widget.ContentPane.superclass.show.call(this);
},refresh:function(){
this.isLoaded=false;
this.loadContents();
},loadContents:function(){
if(this.isLoaded){
return;
}
if(dojo.lang.isFunction(this.handler)){
this._runHandler();
}else{
if(this.href!=""){
this._downloadExternalContent(this.href,this.cacheContent&&!this.refreshOnShow);
}
}
},setUrl:function(url){
this.href=url;
this.isLoaded=false;
if(this.preload||this.isShowing()){
this.loadContents();
}
},abort:function(){
var bind=this._ioBindObj;
if(!bind||!bind.abort){
return;
}
bind.abort();
delete this._ioBindObj;
},_downloadExternalContent:function(url,_824){
this.abort();
this._handleDefaults(this.loadingMessage,"onDownloadStart");
var self=this;
this._ioBindObj=dojo.io.bind(this._cacheSetting({url:url,mimetype:"text/html",handler:function(type,data,xhr){
delete self._ioBindObj;
if(type=="load"){
self.onDownloadEnd.call(self,url,data);
}else{
var e={responseText:xhr.responseText,status:xhr.status,statusText:xhr.statusText,responseHeaders:xhr.getAllResponseHeaders(),text:"Error loading '"+url+"' ("+xhr.status+" "+xhr.statusText+")"};
self._handleDefaults.call(self,e,"onDownloadError");
self.onLoad();
}
}},_824));
},_cacheSetting:function(_82a,_82b){
for(var x in this.bindArgs){
if(dojo.lang.isUndefined(_82a[x])){
_82a[x]=this.bindArgs[x];
}
}
if(dojo.lang.isUndefined(_82a.useCache)){
_82a.useCache=_82b;
}
if(dojo.lang.isUndefined(_82a.preventCache)){
_82a.preventCache=!_82b;
}
if(dojo.lang.isUndefined(_82a.mimetype)){
_82a.mimetype="text/html";
}
return _82a;
},onLoad:function(e){
this._runStack("_onLoadStack");
this.isLoaded=true;
},onUnLoad:function(e){
dojo.deprecated(this.widgetType+".onUnLoad, use .onUnload (lowercased load)",0.5);
},onUnload:function(e){
this._runStack("_onUnloadStack");
delete this.scriptScope;
if(this.onUnLoad!==dojo.widget.ContentPane.prototype.onUnLoad){
this.onUnLoad.apply(this,arguments);
}
},_runStack:function(_830){
var st=this[_830];
var err="";
var _833=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_833);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_830]=[];
if(err.length){
var name=(_830=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},addOnLoad:function(obj,func){
this._pushOnStack(this._onLoadStack,obj,func);
},addOnUnload:function(obj,func){
this._pushOnStack(this._onUnloadStack,obj,func);
},addOnUnLoad:function(){
dojo.deprecated(this.widgetType+".addOnUnLoad, use addOnUnload instead. (lowercased Load)",0.5);
this.addOnUnload.apply(this,arguments);
},_pushOnStack:function(_83a,obj,func){
if(typeof func=="undefined"){
_83a.push(obj);
}else{
_83a.push(function(){
obj[func]();
});
}
},destroy:function(){
this.onUnload();
dojo.widget.ContentPane.superclass.destroy.call(this);
},onExecError:function(e){
},onContentError:function(e){
},onDownloadError:function(e){
},onDownloadStart:function(e){
},onDownloadEnd:function(url,data){
data=this.splitAndFixPaths(data,url);
this.setContent(data);
},_handleDefaults:function(e,_844,_845){
if(!_844){
_844="onContentError";
}
if(dojo.lang.isString(e)){
e={text:e};
}
if(!e.text){
e.text=e.toString();
}
e.toString=function(){
return this.text;
};
if(typeof e.returnValue!="boolean"){
e.returnValue=true;
}
if(typeof e.preventDefault!="function"){
e.preventDefault=function(){
this.returnValue=false;
};
}
this[_844](e);
if(e.returnValue){
switch(_845){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
dojo.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
dojo.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString());
}
}
}
arguments.callee._loopStop=false;
},splitAndFixPaths:function(s,url){
var _848=[],_849=[],tmp=[];
var _84b=[],_84c=[],attr=[],_84e=[];
var str="",path="",fix="",_852="",tag="",_854="";
if(!url){
url="./";
}
if(s){
var _855=/<title[^>]*>([\s\S]*?)<\/title>/i;
while(_84b=_855.exec(s)){
_848.push(_84b[1]);
s=s.substring(0,_84b.index)+s.substr(_84b.index+_84b[0].length);
}
if(this.adjustPaths){
var _856=/<[a-z][a-z0-9]*[^>]*\s(?:(?:src|href|style)=[^>])+[^>]*>/i;
var _857=/\s(src|href|style)=(['"]?)([\w()\[\]\/.,\\'"-:;#=&?\s@]+?)\2/i;
var _858=/^(?:[#]|(?:(?:https?|ftps?|file|javascript|mailto|news):))/;
while(tag=_856.exec(s)){
str+=s.substring(0,tag.index);
s=s.substring((tag.index+tag[0].length),s.length);
tag=tag[0];
_852="";
while(attr=_857.exec(tag)){
path="";
_854=attr[3];
switch(attr[1].toLowerCase()){
case "src":
case "href":
if(_858.exec(_854)){
path=_854;
}else{
path=(new dojo.uri.Uri(url,_854).toString());
}
break;
case "style":
path=dojo.html.fixPathsInCssText(_854,url);
break;
default:
path=_854;
}
fix=" "+attr[1]+"="+attr[2]+path+attr[2];
_852+=tag.substring(0,attr.index)+fix;
tag=tag.substring((attr.index+attr[0].length),tag.length);
}
str+=_852+tag;
}
s=str+s;
}
_855=/(?:<(style)[^>]*>([\s\S]*?)<\/style>|<link ([^>]*rel=['"]?stylesheet['"]?[^>]*)>)/i;
while(_84b=_855.exec(s)){
if(_84b[1]&&_84b[1].toLowerCase()=="style"){
_84e.push(dojo.html.fixPathsInCssText(_84b[2],url));
}else{
if(attr=_84b[3].match(/href=(['"]?)([^'">]*)\1/i)){
_84e.push({path:attr[2]});
}
}
s=s.substring(0,_84b.index)+s.substr(_84b.index+_84b[0].length);
}
var _855=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _859=/src=(['"]?)([^"']*)\1/i;
var _85a=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _85b=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _85c=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(_84b=_855.exec(s)){
if(this.executeScripts&&_84b[1]){
if(attr=_859.exec(_84b[1])){
if(_85a.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_849.push({path:attr[2]});
}
}
}
if(_84b[2]){
var sc=_84b[2].replace(_85b,"");
if(!sc){
continue;
}
while(tmp=_85c.exec(sc)){
_84c.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
if(this.executeScripts){
_849.push(sc);
}
}
s=s.substr(0,_84b.index)+s.substr(_84b.index+_84b[0].length);
}
if(this.extractContent){
_84b=s.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
if(_84b){
s=_84b[1];
}
}
if(this.executeScripts&&this.scriptSeparation){
var _855=/(<[a-zA-Z][a-zA-Z0-9]*\s[^>]*?\S=)((['"])[^>]*scriptScope[^>]*>)/;
var _85e=/([\s'";:\(])scriptScope(.*)/;
str="";
while(tag=_855.exec(s)){
tmp=((tag[3]=="'")?"\"":"'");
fix="";
str+=s.substring(0,tag.index)+tag[1];
while(attr=_85e.exec(tag[2])){
tag[2]=tag[2].substring(0,attr.index)+attr[1]+"dojo.widget.byId("+tmp+this.widgetId+tmp+").scriptScope"+attr[2];
}
str+=tag[2];
s=s.substr(tag.index+tag[0].length);
}
s=str+s;
}
}
return {"xml":s,"styles":_84e,"titles":_848,"requires":_84c,"scripts":_849,"url":url};
},_setContent:function(cont){
this.destroyChildren();
for(var i=0;i<this._styleNodes.length;i++){
if(this._styleNodes[i]&&this._styleNodes[i].parentNode){
this._styleNodes[i].parentNode.removeChild(this._styleNodes[i]);
}
}
this._styleNodes=[];
try{
var node=this.containerNode||this.domNode;
while(node.firstChild){
dojo.html.destroyNode(node.firstChild);
}
if(typeof cont!="string"){
node.appendChild(cont);
}else{
node.innerHTML=cont;
}
}
catch(e){
e.text="Couldn't load content:"+e.description;
this._handleDefaults(e,"onContentError");
}
},setContent:function(data){
this.abort();
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
if(!data||dojo.html.isNode(data)){
this._setContent(data);
this.onResized();
this.onLoad();
}else{
if(typeof data.xml!="string"){
this.href="";
data=this.splitAndFixPaths(data);
}
this._setContent(data.xml);
for(var i=0;i<data.styles.length;i++){
if(data.styles[i].path){
this._styleNodes.push(dojo.html.insertCssFile(data.styles[i].path,dojo.doc(),false,true));
}else{
this._styleNodes.push(dojo.html.insertCssText(data.styles[i]));
}
}
if(this.parseContent){
for(var i=0;i<data.requires.length;i++){
try{
eval(data.requires[i]);
}
catch(e){
e.text="ContentPane: error in package loading calls, "+(e.description||e);
this._handleDefaults(e,"onContentError","debug");
}
}
}
var _864=this;
function asyncParse(){
if(_864.executeScripts){
_864._executeScripts(data.scripts);
}
if(_864.parseContent){
var node=_864.containerNode||_864.domNode;
var _866=new dojo.xml.Parse();
var frag=_866.parseElement(node,null,true);
dojo.widget.getParser().createSubComponents(frag,_864);
}
_864.onResized();
_864.onLoad();
}
if(dojo.hostenv.isXDomain&&data.requires.length){
dojo.addOnLoad(asyncParse);
}else{
asyncParse();
}
}
},setHandler:function(_868){
var fcn=dojo.lang.isFunction(_868)?_868:window[_868];
if(!dojo.lang.isFunction(fcn)){
this._handleDefaults("Unable to set handler, '"+_868+"' not a function.","onExecError",true);
return;
}
this.handler=function(){
return fcn.apply(this,arguments);
};
},_runHandler:function(){
var ret=true;
if(dojo.lang.isFunction(this.handler)){
this.handler(this,this.domNode);
ret=false;
}
this.onLoad();
return ret;
},_executeScripts:function(_86b){
var self=this;
var tmp="",code="";
for(var i=0;i<_86b.length;i++){
if(_86b[i].path){
dojo.io.bind(this._cacheSetting({"url":_86b[i].path,"load":function(type,_871){
dojo.lang.hitch(self,tmp=";"+_871);
},"error":function(type,_873){
_873.text=type+" downloading remote script";
self._handleDefaults.call(self,_873,"onExecError","debug");
},"mimetype":"text/plain","sync":true},this.cacheContent));
code+=tmp;
}else{
code+=_86b[i];
}
}
try{
if(this.scriptSeparation){
delete this.scriptScope;
this.scriptScope=new (new Function("_container_",code+"; return this;"))(self);
}else{
var djg=dojo.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=dojo.doc();
var sc=djd.createElement("script");
sc.appendChild(djd.createTextNode(code));
(this.containerNode||this.domNode).appendChild(sc);
}
}
}
catch(e){
e.text="Error running scripts from content:\n"+e.description;
this._handleDefaults(e,"onExecError","debug");
}
}});
dojo.provide("dojo.html.selection");
dojo.html.selectionType={NONE:0,TEXT:1,CONTROL:2};
dojo.html.clearSelection=function(){
var _877=dojo.global();
var _878=dojo.doc();
try{
if(_877["getSelection"]){
if(dojo.render.html.safari){
_877.getSelection().collapse();
}else{
_877.getSelection().removeAllRanges();
}
}else{
if(_878.selection){
if(_878.selection.empty){
_878.selection.empty();
}else{
if(_878.selection.clear){
_878.selection.clear();
}
}
}
}
return true;
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.html.disableSelection=function(_879){
_879=dojo.byId(_879)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_879.style.MozUserSelect="none";
}else{
if(h.safari){
_879.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_879.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_87b){
_87b=dojo.byId(_87b)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_87b.style.MozUserSelect="";
}else{
if(h.safari){
_87b.style.KhtmlUserSelect="";
}else{
if(h.ie){
_87b.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_87d){
dojo.deprecated("dojo.html.selectElement","replaced by dojo.html.selection.selectElementChildren",0.5);
};
dojo.html.selectInputText=function(_87e){
var _87f=dojo.global();
var _880=dojo.doc();
_87e=dojo.byId(_87e);
if(_880["selection"]&&dojo.body()["createTextRange"]){
var _881=_87e.createTextRange();
_881.moveStart("character",0);
_881.moveEnd("character",_87e.value.length);
_881.select();
}else{
if(_87f["getSelection"]){
var _882=_87f.getSelection();
_87e.setSelectionRange(0,_87e.value.length);
}
}
_87e.focus();
};
dojo.html.isSelectionCollapsed=function(){
dojo.deprecated("dojo.html.isSelectionCollapsed","replaced by dojo.html.selection.isCollapsed",0.5);
return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection,{getType:function(){
if(dojo.doc()["selection"]){
return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
}else{
var _883=dojo.html.selectionType.TEXT;
var oSel;
try{
oSel=dojo.global().getSelection();
}
catch(e){
}
if(oSel&&oSel.rangeCount==1){
var _885=oSel.getRangeAt(0);
if(_885.startContainer==_885.endContainer&&(_885.endOffset-_885.startOffset)==1&&_885.startContainer.nodeType!=dojo.dom.TEXT_NODE){
_883=dojo.html.selectionType.CONTROL;
}
}
return _883;
}
},isCollapsed:function(){
var _886=dojo.global();
var _887=dojo.doc();
if(_887["selection"]){
return _887.selection.createRange().text=="";
}else{
if(_886["getSelection"]){
var _888=_886.getSelection();
if(dojo.lang.isString(_888)){
return _888=="";
}else{
return _888.isCollapsed||_888.toString()=="";
}
}
}
},getSelectedElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
if(dojo.doc()["selection"]){
var _889=dojo.doc().selection.createRange();
if(_889&&_889.item){
return dojo.doc().selection.createRange().item(0);
}
}else{
var _88a=dojo.global().getSelection();
return _88a.anchorNode.childNodes[_88a.anchorOffset];
}
}
},getParentElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
var p=dojo.html.selection.getSelectedElement();
if(p){
return p.parentNode;
}
}else{
if(dojo.doc()["selection"]){
return dojo.doc().selection.createRange().parentElement();
}else{
var _88c=dojo.global().getSelection();
if(_88c){
var node=_88c.anchorNode;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.parentNode;
}
return node;
}
}
}
},getSelectedText:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().text;
}else{
var _88e=dojo.global().getSelection();
if(_88e){
return _88e.toString();
}
}
},getSelectedHtml:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().htmlText;
}else{
var _88f=dojo.global().getSelection();
if(_88f&&_88f.rangeCount){
var frag=_88f.getRangeAt(0).cloneContents();
var div=document.createElement("div");
div.appendChild(frag);
return div.innerHTML;
}
return null;
}
},hasAncestorElement:function(_892){
return (dojo.html.selection.getAncestorElement.apply(this,arguments)!=null);
},getAncestorElement:function(_893){
var node=dojo.html.selection.getSelectedElement()||dojo.html.selection.getParentElement();
while(node){
if(dojo.html.selection.isTag(node,arguments).length>0){
return node;
}
node=node.parentNode;
}
return null;
},isTag:function(node,tags){
if(node&&node.tagName){
for(var i=0;i<tags.length;i++){
if(node.tagName.toLowerCase()==String(tags[i]).toLowerCase()){
return String(tags[i]).toLowerCase();
}
}
}
return "";
},selectElement:function(_898){
var _899=dojo.global();
var _89a=dojo.doc();
_898=dojo.byId(_898);
if(_89a.selection&&dojo.body().createTextRange){
try{
var _89b=dojo.body().createControlRange();
_89b.addElement(_898);
_89b.select();
}
catch(e){
dojo.html.selection.selectElementChildren(_898);
}
}else{
if(_899["getSelection"]){
var _89c=_899.getSelection();
if(_89c["removeAllRanges"]){
var _89b=_89a.createRange();
_89b.selectNode(_898);
_89c.removeAllRanges();
_89c.addRange(_89b);
}
}
}
},selectElementChildren:function(_89d){
var _89e=dojo.global();
var _89f=dojo.doc();
_89d=dojo.byId(_89d);
if(_89f.selection&&dojo.body().createTextRange){
var _8a0=dojo.body().createTextRange();
_8a0.moveToElementText(_89d);
_8a0.select();
}else{
if(_89e["getSelection"]){
var _8a1=_89e.getSelection();
if(_8a1["setBaseAndExtent"]){
_8a1.setBaseAndExtent(_89d,0,_89d,_89d.innerText.length-1);
}else{
if(_8a1["selectAllChildren"]){
_8a1.selectAllChildren(_89d);
}
}
}
}
},getBookmark:function(){
var _8a2;
var _8a3=dojo.doc();
if(_8a3["selection"]){
var _8a4=_8a3.selection.createRange();
_8a2=_8a4.getBookmark();
}else{
var _8a5;
try{
_8a5=dojo.global().getSelection();
}
catch(e){
}
if(_8a5){
var _8a4=_8a5.getRangeAt(0);
_8a2=_8a4.cloneRange();
}else{
dojo.debug("No idea how to store the current selection for this browser!");
}
}
return _8a2;
},moveToBookmark:function(_8a6){
var _8a7=dojo.doc();
if(_8a7["selection"]){
var _8a8=_8a7.selection.createRange();
_8a8.moveToBookmark(_8a6);
_8a8.select();
}else{
var _8a9;
try{
_8a9=dojo.global().getSelection();
}
catch(e){
}
if(_8a9&&_8a9["removeAllRanges"]){
_8a9.removeAllRanges();
_8a9.addRange(_8a6);
}else{
dojo.debug("No idea how to restore selection for this browser!");
}
}
},collapse:function(_8aa){
if(dojo.global()["getSelection"]){
var _8ab=dojo.global().getSelection();
if(_8ab.removeAllRanges){
if(_8aa){
_8ab.collapseToStart();
}else{
_8ab.collapseToEnd();
}
}else{
dojo.global().getSelection().collapse(_8aa);
}
}else{
if(dojo.doc().selection){
var _8ac=dojo.doc().selection.createRange();
_8ac.collapse(_8aa);
_8ac.select();
}
}
},remove:function(){
if(dojo.doc().selection){
var _8ad=dojo.doc().selection;
if(_8ad.type.toUpperCase()!="NONE"){
_8ad.clear();
}
return _8ad;
}else{
var _8ad=dojo.global().getSelection();
for(var i=0;i<_8ad.rangeCount;i++){
_8ad.getRangeAt(i).deleteContents();
}
return _8ad;
}
}});
dojo.provide("dojo.widget.SplitContainer");
dojo.widget.defineWidget("dojo.widget.SplitContainer",dojo.widget.HtmlWidget,function(){
this.sizers=[];
},{isContainer:true,templateCssPath:dojo.uri.dojoUri("src/widget/templates/SplitContainer.css"),activeSizing:false,sizerWidth:15,orientation:"horizontal",persist:true,postMixInProperties:function(){
dojo.widget.SplitContainer.superclass.postMixInProperties.apply(this,arguments);
this.isHorizontal=(this.orientation=="horizontal");
},fillInTemplate:function(){
dojo.widget.SplitContainer.superclass.fillInTemplate.apply(this,arguments);
dojo.html.addClass(this.domNode,"dojoSplitContainer");
if(dojo.render.html.moz){
this.domNode.style.overflow="-moz-scrollbars-none";
}
var _8af=dojo.html.getContentBox(this.domNode);
this.paneWidth=_8af.width;
this.paneHeight=_8af.height;
},onResized:function(e){
var _8b1=dojo.html.getContentBox(this.domNode);
this.paneWidth=_8b1.width;
this.paneHeight=_8b1.height;
this._layoutPanels();
},postCreate:function(args,_8b3,_8b4){
dojo.widget.SplitContainer.superclass.postCreate.apply(this,arguments);
for(var i=0;i<this.children.length;i++){
with(this.children[i].domNode.style){
position="absolute";
}
dojo.html.addClass(this.children[i].domNode,"dojoSplitPane");
if(i==this.children.length-1){
break;
}
this._addSizer();
}
if(typeof this.sizerWidth=="object"){
try{
this.sizerWidth=parseInt(this.sizerWidth.toString());
}
catch(e){
this.sizerWidth=15;
}
}
this.virtualSizer=document.createElement("div");
this.virtualSizer.style.position="absolute";
this.virtualSizer.style.display="none";
this.virtualSizer.style.zIndex=10;
this.virtualSizer.className=this.isHorizontal?"dojoSplitContainerVirtualSizerH":"dojoSplitContainerVirtualSizerV";
this.domNode.appendChild(this.virtualSizer);
dojo.html.disableSelection(this.virtualSizer);
if(this.persist){
this._restoreState();
}
this.resizeSoon();
},_injectChild:function(_8b6){
with(_8b6.domNode.style){
position="absolute";
}
dojo.html.addClass(_8b6.domNode,"dojoSplitPane");
},_addSizer:function(){
var i=this.sizers.length;
this.sizers[i]=document.createElement("div");
this.sizers[i].style.position="absolute";
this.sizers[i].className=this.isHorizontal?"dojoSplitContainerSizerH":"dojoSplitContainerSizerV";
var self=this;
var _8b9=(function(){
var _8ba=i;
return function(e){
self.beginSizing(e,_8ba);
};
})();
dojo.event.connect(this.sizers[i],"onmousedown",_8b9);
this.domNode.appendChild(this.sizers[i]);
dojo.html.disableSelection(this.sizers[i]);
},removeChild:function(_8bc){
if(this.sizers.length>0){
for(var x=0;x<this.children.length;x++){
if(this.children[x]===_8bc){
var i=this.sizers.length-1;
this.domNode.removeChild(this.sizers[i]);
this.sizers.length=i;
break;
}
}
}
dojo.widget.SplitContainer.superclass.removeChild.call(this,_8bc,arguments);
this.onResized();
},addChild:function(_8bf){
dojo.widget.SplitContainer.superclass.addChild.apply(this,arguments);
this._injectChild(_8bf);
if(this.children.length>1){
this._addSizer();
}
this._layoutPanels();
},_layoutPanels:function(){
if(this.children.length==0){
return;
}
var _8c0=this.isHorizontal?this.paneWidth:this.paneHeight;
if(this.children.length>1){
_8c0-=this.sizerWidth*(this.children.length-1);
}
var _8c1=0;
for(var i=0;i<this.children.length;i++){
_8c1+=this.children[i].sizeShare;
}
var _8c3=_8c0/_8c1;
var _8c4=0;
for(var i=0;i<this.children.length-1;i++){
var size=Math.round(_8c3*this.children[i].sizeShare);
this.children[i].sizeActual=size;
_8c4+=size;
}
this.children[this.children.length-1].sizeActual=_8c0-_8c4;
this._checkSizes();
var pos=0;
var size=this.children[0].sizeActual;
this._movePanel(this.children[0],pos,size);
this.children[0].position=pos;
pos+=size;
for(var i=1;i<this.children.length;i++){
this._moveSlider(this.sizers[i-1],pos,this.sizerWidth);
this.sizers[i-1].position=pos;
pos+=this.sizerWidth;
size=this.children[i].sizeActual;
this._movePanel(this.children[i],pos,size);
this.children[i].position=pos;
pos+=size;
}
},_movePanel:function(_8c7,pos,size){
if(this.isHorizontal){
_8c7.domNode.style.left=pos+"px";
_8c7.domNode.style.top=0;
_8c7.resizeTo(size,this.paneHeight);
}else{
_8c7.domNode.style.left=0;
_8c7.domNode.style.top=pos+"px";
_8c7.resizeTo(this.paneWidth,size);
}
},_moveSlider:function(_8ca,pos,size){
if(this.isHorizontal){
_8ca.style.left=pos+"px";
_8ca.style.top=0;
dojo.html.setMarginBox(_8ca,{width:size,height:this.paneHeight});
}else{
_8ca.style.left=0;
_8ca.style.top=pos+"px";
dojo.html.setMarginBox(_8ca,{width:this.paneWidth,height:size});
}
},_growPane:function(_8cd,pane){
if(_8cd>0){
if(pane.sizeActual>pane.sizeMin){
if((pane.sizeActual-pane.sizeMin)>_8cd){
pane.sizeActual=pane.sizeActual-_8cd;
_8cd=0;
}else{
_8cd-=pane.sizeActual-pane.sizeMin;
pane.sizeActual=pane.sizeMin;
}
}
}
return _8cd;
},_checkSizes:function(){
var _8cf=0;
var _8d0=0;
for(var i=0;i<this.children.length;i++){
_8d0+=this.children[i].sizeActual;
_8cf+=this.children[i].sizeMin;
}
if(_8cf<=_8d0){
var _8d2=0;
for(var i=0;i<this.children.length;i++){
if(this.children[i].sizeActual<this.children[i].sizeMin){
_8d2+=this.children[i].sizeMin-this.children[i].sizeActual;
this.children[i].sizeActual=this.children[i].sizeMin;
}
}
if(_8d2>0){
if(this.isDraggingLeft){
for(var i=this.children.length-1;i>=0;i--){
_8d2=this._growPane(_8d2,this.children[i]);
}
}else{
for(var i=0;i<this.children.length;i++){
_8d2=this._growPane(_8d2,this.children[i]);
}
}
}
}else{
for(var i=0;i<this.children.length;i++){
this.children[i].sizeActual=Math.round(_8d0*(this.children[i].sizeMin/_8cf));
}
}
},beginSizing:function(e,i){
this.paneBefore=this.children[i];
this.paneAfter=this.children[i+1];
this.isSizing=true;
this.sizingSplitter=this.sizers[i];
this.originPos=dojo.html.getAbsolutePosition(this.children[0].domNode,true,dojo.html.boxSizing.MARGIN_BOX);
if(this.isHorizontal){
var _8d5=(e.layerX?e.layerX:e.offsetX);
var _8d6=e.pageX;
this.originPos=this.originPos.x;
}else{
var _8d5=(e.layerY?e.layerY:e.offsetY);
var _8d6=e.pageY;
this.originPos=this.originPos.y;
}
this.startPoint=this.lastPoint=_8d6;
this.screenToClientOffset=_8d6-_8d5;
this.dragOffset=this.lastPoint-this.paneBefore.sizeActual-this.originPos-this.paneBefore.position;
if(!this.activeSizing){
this._showSizingLine();
}
dojo.event.connect(document.documentElement,"onmousemove",this,"changeSizing");
dojo.event.connect(document.documentElement,"onmouseup",this,"endSizing");
dojo.event.browser.stopEvent(e);
},changeSizing:function(e){
this.lastPoint=this.isHorizontal?e.pageX:e.pageY;
if(this.activeSizing){
this.movePoint();
this._updateSize();
}else{
this.movePoint();
this._moveSizingLine();
}
dojo.event.browser.stopEvent(e);
},endSizing:function(e){
if(!this.activeSizing){
this._hideSizingLine();
}
this._updateSize();
this.isSizing=false;
dojo.event.disconnect(document.documentElement,"onmousemove",this,"changeSizing");
dojo.event.disconnect(document.documentElement,"onmouseup",this,"endSizing");
if(this.persist){
this._saveState(this);
}
},movePoint:function(){
var p=this.lastPoint-this.screenToClientOffset;
var a=p-this.dragOffset;
a=this.legaliseSplitPoint(a);
p=a+this.dragOffset;
this.lastPoint=p+this.screenToClientOffset;
},legaliseSplitPoint:function(a){
a+=this.sizingSplitter.position;
this.isDraggingLeft=(a>0)?true:false;
if(!this.activeSizing){
if(a<this.paneBefore.position+this.paneBefore.sizeMin){
a=this.paneBefore.position+this.paneBefore.sizeMin;
}
if(a>this.paneAfter.position+(this.paneAfter.sizeActual-(this.sizerWidth+this.paneAfter.sizeMin))){
a=this.paneAfter.position+(this.paneAfter.sizeActual-(this.sizerWidth+this.paneAfter.sizeMin));
}
}
a-=this.sizingSplitter.position;
this._checkSizes();
return a;
},_updateSize:function(){
var pos=this.lastPoint-this.dragOffset-this.originPos;
var _8dd=this.paneBefore.position;
var _8de=this.paneAfter.position+this.paneAfter.sizeActual;
this.paneBefore.sizeActual=pos-_8dd;
this.paneAfter.position=pos+this.sizerWidth;
this.paneAfter.sizeActual=_8de-this.paneAfter.position;
for(var i=0;i<this.children.length;i++){
this.children[i].sizeShare=this.children[i].sizeActual;
}
this._layoutPanels();
},_showSizingLine:function(){
this._moveSizingLine();
if(this.isHorizontal){
dojo.html.setMarginBox(this.virtualSizer,{width:this.sizerWidth,height:this.paneHeight});
}else{
dojo.html.setMarginBox(this.virtualSizer,{width:this.paneWidth,height:this.sizerWidth});
}
this.virtualSizer.style.display="block";
},_hideSizingLine:function(){
this.virtualSizer.style.display="none";
},_moveSizingLine:function(){
var pos=this.lastPoint-this.startPoint+this.sizingSplitter.position;
if(this.isHorizontal){
this.virtualSizer.style.left=pos+"px";
}else{
var pos=(this.lastPoint-this.startPoint)+this.sizingSplitter.position;
this.virtualSizer.style.top=pos+"px";
}
},_getCookieName:function(i){
return this.widgetId+"_"+i;
},_restoreState:function(){
for(var i=0;i<this.children.length;i++){
var _8e3=this._getCookieName(i);
var _8e4=dojo.io.cookie.getCookie(_8e3);
if(_8e4!=null){
var pos=parseInt(_8e4);
if(typeof pos=="number"){
this.children[i].sizeShare=pos;
}
}
}
},_saveState:function(){
for(var i=0;i<this.children.length;i++){
var _8e7=this._getCookieName(i);
dojo.io.cookie.setCookie(_8e7,this.children[i].sizeShare,null,null,null,null);
}
}});
dojo.lang.extend(dojo.widget.Widget,{sizeMin:10,sizeShare:10});
dojo.widget.defineWidget("dojo.widget.SplitContainerPanel",dojo.widget.ContentPane,{});
dojo.provide("dojo.widget.html.layout");
dojo.widget.html.layout=function(_8e8,_8e9,_8ea){
dojo.html.addClass(_8e8,"dojoLayoutContainer");
_8e9=dojo.lang.filter(_8e9,function(_8eb,idx){
_8eb.idx=idx;
return dojo.lang.inArray(["top","bottom","left","right","client","flood"],_8eb.layoutAlign);
});
if(_8ea&&_8ea!="none"){
var rank=function(_8ee){
switch(_8ee.layoutAlign){
case "flood":
return 1;
case "left":
case "right":
return (_8ea=="left-right")?2:3;
case "top":
case "bottom":
return (_8ea=="left-right")?3:2;
default:
return 4;
}
};
_8e9.sort(function(a,b){
return (rank(a)-rank(b))||(a.idx-b.idx);
});
}
var f={top:dojo.html.getPixelValue(_8e8,"padding-top",true),left:dojo.html.getPixelValue(_8e8,"padding-left",true)};
dojo.lang.mixin(f,dojo.html.getContentBox(_8e8));
dojo.lang.forEach(_8e9,function(_8f2){
var elm=_8f2.domNode;
var pos=_8f2.layoutAlign;
with(elm.style){
left=f.left+"px";
top=f.top+"px";
bottom="auto";
right="auto";
}
dojo.html.addClass(elm,"dojoAlign"+dojo.string.capitalize(pos));
if((pos=="top")||(pos=="bottom")){
dojo.html.setMarginBox(elm,{width:f.width});
var h=dojo.html.getMarginBox(elm).height;
f.height-=h;
if(pos=="top"){
f.top+=h;
}else{
elm.style.top=f.top+f.height+"px";
}
if(_8f2.onResized){
_8f2.onResized();
}
}else{
if(pos=="left"||pos=="right"){
var w=dojo.html.getMarginBox(elm).width;
if(_8f2.resizeTo){
_8f2.resizeTo(w,f.height);
}else{
dojo.html.setMarginBox(elm,{width:w,height:f.height});
}
f.width-=w;
if(pos=="left"){
f.left+=w;
}else{
elm.style.left=f.left+f.width+"px";
}
}else{
if(pos=="flood"||pos=="client"){
if(_8f2.resizeTo){
_8f2.resizeTo(f.width,f.height);
}else{
dojo.html.setMarginBox(elm,{width:f.width,height:f.height});
}
}
}
}
});
};
dojo.html.insertCssText(".dojoLayoutContainer{ position: relative; display: block; overflow: hidden; }\n"+"body .dojoAlignTop, body .dojoAlignBottom, body .dojoAlignLeft, body .dojoAlignRight { position: absolute; overflow: hidden; }\n"+"body .dojoAlignClient { position: absolute }\n"+".dojoAlignClient { overflow: auto; }\n");
dojo.provide("dojo.widget.LayoutContainer");
dojo.widget.defineWidget("dojo.widget.LayoutContainer",dojo.widget.HtmlWidget,{isContainer:true,layoutChildPriority:"top-bottom",postCreate:function(){
dojo.widget.html.layout(this.domNode,this.children,this.layoutChildPriority);
},addChild:function(_8f7,_8f8,pos,ref,_8fb){
dojo.widget.LayoutContainer.superclass.addChild.call(this,_8f7,_8f8,pos,ref,_8fb);
dojo.widget.html.layout(this.domNode,this.children,this.layoutChildPriority);
},removeChild:function(pane){
dojo.widget.LayoutContainer.superclass.removeChild.call(this,pane);
dojo.widget.html.layout(this.domNode,this.children,this.layoutChildPriority);
},onResized:function(){
dojo.widget.html.layout(this.domNode,this.children,this.layoutChildPriority);
},show:function(){
this.domNode.style.display="";
this.checkSize();
this.domNode.style.display="none";
this.domNode.style.visibility="";
dojo.widget.LayoutContainer.superclass.show.call(this);
}});
dojo.lang.extend(dojo.widget.Widget,{layoutAlign:"none"});
dojo.provide("dojo.i18n.common");
dojo.i18n.getLocalization=function(_8fd,_8fe,_8ff){
dojo.hostenv.preloadLocalizations();
_8ff=dojo.hostenv.normalizeLocale(_8ff);
var _900=_8ff.split("-");
var _901=[_8fd,"nls",_8fe].join(".");
var _902=dojo.hostenv.findModule(_901,true);
var _903;
for(var i=_900.length;i>0;i--){
var loc=_900.slice(0,i).join("_");
if(_902[loc]){
_903=_902[loc];
break;
}
}
if(!_903){
_903=_902.ROOT;
}
if(_903){
var _906=function(){
};
_906.prototype=_903;
return new _906();
}
dojo.raise("Bundle not found: "+_8fe+" in "+_8fd+" , locale="+_8ff);
};
dojo.i18n.isLTR=function(_907){
var lang=dojo.hostenv.normalizeLocale(_907).split("-")[0];
var RTL={ar:true,fa:true,he:true,ur:true,yi:true};
return !RTL[lang];
};
dojo.provide("dojo.html.iframe");
dojo.html.iframeContentWindow=function(_90a){
var win=dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(_90a))||dojo.html.iframeContentDocument(_90a).__parent__||(_90a.name&&document.frames[_90a.name])||null;
return win;
};
dojo.html.iframeContentDocument=function(_90c){
var doc=_90c.contentDocument||((_90c.contentWindow)&&(_90c.contentWindow.document))||((_90c.name)&&(document.frames[_90c.name])&&(document.frames[_90c.name].document))||null;
return doc;
};
dojo.html.BackgroundIframe=function(node){
if(dojo.render.html.ie55||dojo.render.html.ie60){
var html="<iframe src='javascript:false'"+" style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%;"+"z-index: -1; filter:Alpha(Opacity=\"0\");' "+">";
this.iframe=dojo.doc().createElement(html);
this.iframe.tabIndex=-1;
if(node){
node.appendChild(this.iframe);
this.domNode=node;
}else{
dojo.body().appendChild(this.iframe);
this.iframe.style.display="none";
}
}
};
dojo.lang.extend(dojo.html.BackgroundIframe,{iframe:null,onResized:function(){
if(this.iframe&&this.domNode&&this.domNode.parentNode){
var _910=dojo.html.getMarginBox(this.domNode);
if(_910.width==0||_910.height==0){
dojo.lang.setTimeout(this,this.onResized,100);
return;
}
this.iframe.style.width=_910.width+"px";
this.iframe.style.height=_910.height+"px";
}
},size:function(node){
if(!this.iframe){
return;
}
var _912=dojo.html.toCoordinateObject(node,true,dojo.html.boxSizing.BORDER_BOX);
with(this.iframe.style){
width=_912.width+"px";
height=_912.height+"px";
left=_912.left+"px";
top=_912.top+"px";
}
},setZIndex:function(node){
if(!this.iframe){
return;
}
if(dojo.dom.isNode(node)){
this.iframe.style.zIndex=dojo.html.getStyle(node,"z-index")-1;
}else{
if(!isNaN(node)){
this.iframe.style.zIndex=node;
}
}
},show:function(){
if(this.iframe){
this.iframe.style.display="block";
}
},hide:function(){
if(this.iframe){
this.iframe.style.display="none";
}
},remove:function(){
if(this.iframe){
dojo.html.removeNode(this.iframe,true);
delete this.iframe;
this.iframe=null;
}
}});
dojo.provide("dojo.widget.PopupContainer");
dojo.declare("dojo.widget.PopupContainerBase",null,function(){
this.queueOnAnimationFinish=[];
},{isContainer:true,templateString:"<div dojoAttachPoint=\"containerNode\" style=\"display:none;position:absolute;\" class=\"dojoPopupContainer\" ></div>",isShowingNow:false,currentSubpopup:null,beginZIndex:1000,parentPopup:null,parent:null,popupIndex:0,aroundBox:dojo.html.boxSizing.BORDER_BOX,openedForWindow:null,processKey:function(evt){
return false;
},applyPopupBasicStyle:function(){
with(this.domNode.style){
display="none";
position="absolute";
}
},aboutToShow:function(){
},open:function(x,y,_917,_918,_919,_91a){
if(this.isShowingNow){
return;
}
if(this.animationInProgress){
this.queueOnAnimationFinish.push(this.open,arguments);
return;
}
this.aboutToShow();
var _91b=false,node,_91d;
if(typeof x=="object"){
node=x;
_91d=_918;
_918=_917;
_917=y;
_91b=true;
}
this.parent=_917;
dojo.body().appendChild(this.domNode);
_918=_918||_917["domNode"]||[];
var _91e=null;
this.isTopLevel=true;
while(_917){
if(_917!==this&&(_917.setOpenedSubpopup!=undefined&&_917.applyPopupBasicStyle!=undefined)){
_91e=_917;
this.isTopLevel=false;
_91e.setOpenedSubpopup(this);
break;
}
_917=_917.parent;
}
this.parentPopup=_91e;
this.popupIndex=_91e?_91e.popupIndex+1:1;
if(this.isTopLevel){
var _91f=dojo.html.isNode(_918)?_918:null;
dojo.widget.PopupManager.opened(this,_91f);
}
if(this.isTopLevel&&!dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.isCollapsed)){
this._bookmark=dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.getBookmark);
}else{
this._bookmark=null;
}
if(_918 instanceof Array){
_918={left:_918[0],top:_918[1],width:0,height:0};
}
with(this.domNode.style){
display="";
zIndex=this.beginZIndex+this.popupIndex;
}
if(_91b){
this.move(node,_91a,_91d);
}else{
this.move(x,y,_91a,_919);
}
this.domNode.style.display="none";
this.explodeSrc=_918;
this.show();
this.isShowingNow=true;
},move:function(x,y,_922,_923){
var _924=(typeof x=="object");
if(_924){
var _925=_922;
var node=x;
_922=y;
if(!_925){
_925={"BL":"TL","TL":"BL"};
}
dojo.html.placeOnScreenAroundElement(this.domNode,node,_922,this.aroundBox,_925);
}else{
if(!_923){
_923="TL,TR,BL,BR";
}
dojo.html.placeOnScreen(this.domNode,x,y,_922,true,_923);
}
},close:function(_927){
if(_927){
this.domNode.style.display="none";
}
if(this.animationInProgress){
this.queueOnAnimationFinish.push(this.close,[]);
return;
}
this.closeSubpopup(_927);
this.hide();
if(this.bgIframe){
this.bgIframe.hide();
this.bgIframe.size({left:0,top:0,width:0,height:0});
}
if(this.isTopLevel){
dojo.widget.PopupManager.closed(this);
}
this.isShowingNow=false;
if(this.parent){
setTimeout(dojo.lang.hitch(this,function(){
try{
if(this.parent["focus"]){
this.parent.focus();
}else{
this.parent.domNode.focus();
}
}
catch(e){
dojo.debug("No idea how to focus to parent",e);
}
}),10);
}
if(this._bookmark&&dojo.withGlobal(this.openedForWindow||dojo.global(),dojo.html.selection.isCollapsed)){
if(this.openedForWindow){
this.openedForWindow.focus();
}
try{
dojo.withGlobal(this.openedForWindow||dojo.global(),"moveToBookmark",dojo.html.selection,[this._bookmark]);
}
catch(e){
}
}
this._bookmark=null;
},closeAll:function(_928){
if(this.parentPopup){
this.parentPopup.closeAll(_928);
}else{
this.close(_928);
}
},setOpenedSubpopup:function(_929){
this.currentSubpopup=_929;
},closeSubpopup:function(_92a){
if(this.currentSubpopup==null){
return;
}
this.currentSubpopup.close(_92a);
this.currentSubpopup=null;
},onShow:function(){
dojo.widget.PopupContainer.superclass.onShow.apply(this,arguments);
this.openedSize={w:this.domNode.style.width,h:this.domNode.style.height};
if(dojo.render.html.ie){
if(!this.bgIframe){
this.bgIframe=new dojo.html.BackgroundIframe();
this.bgIframe.setZIndex(this.domNode);
}
this.bgIframe.size(this.domNode);
this.bgIframe.show();
}
this.processQueue();
},processQueue:function(){
if(!this.queueOnAnimationFinish.length){
return;
}
var func=this.queueOnAnimationFinish.shift();
var args=this.queueOnAnimationFinish.shift();
func.apply(this,args);
},onHide:function(){
dojo.widget.HtmlWidget.prototype.onHide.call(this);
if(this.openedSize){
with(this.domNode.style){
width=this.openedSize.w;
height=this.openedSize.h;
}
}
this.processQueue();
}});
dojo.widget.defineWidget("dojo.widget.PopupContainer",[dojo.widget.HtmlWidget,dojo.widget.PopupContainerBase],{});
dojo.widget.PopupManager=new function(){
this.currentMenu=null;
this.currentButton=null;
this.currentFocusMenu=null;
this.focusNode=null;
this.registeredWindows=[];
this.registerWin=function(win){
if(!win.__PopupManagerRegistered){
dojo.event.connect(win.document,"onmousedown",this,"onClick");
dojo.event.connect(win,"onscroll",this,"onClick");
dojo.event.connect(win.document,"onkey",this,"onKey");
win.__PopupManagerRegistered=true;
this.registeredWindows.push(win);
}
};
this.registerAllWindows=function(_92e){
if(!_92e){
_92e=dojo.html.getDocumentWindow(window.top&&window.top.document||window.document);
}
this.registerWin(_92e);
for(var i=0;i<_92e.frames.length;i++){
try{
var win=dojo.html.getDocumentWindow(_92e.frames[i].document);
if(win){
this.registerAllWindows(win);
}
}
catch(e){
}
}
};
this.unRegisterWin=function(win){
if(win.__PopupManagerRegistered){
dojo.event.disconnect(win.document,"onmousedown",this,"onClick");
dojo.event.disconnect(win,"onscroll",this,"onClick");
dojo.event.disconnect(win.document,"onkey",this,"onKey");
win.__PopupManagerRegistered=false;
}
};
this.unRegisterAllWindows=function(){
for(var i=0;i<this.registeredWindows.length;++i){
this.unRegisterWin(this.registeredWindows[i]);
}
this.registeredWindows=[];
};
dojo.addOnLoad(this,"registerAllWindows");
dojo.addOnUnload(this,"unRegisterAllWindows");
this.closed=function(menu){
if(this.currentMenu==menu){
this.currentMenu=null;
this.currentButton=null;
this.currentFocusMenu=null;
}
};
this.opened=function(menu,_935){
if(menu==this.currentMenu){
return;
}
if(this.currentMenu){
this.currentMenu.close();
}
this.currentMenu=menu;
this.currentFocusMenu=menu;
this.currentButton=_935;
};
this.setFocusedMenu=function(menu){
this.currentFocusMenu=menu;
};
this.onKey=function(e){
if(!e.key){
return;
}
if(!this.currentMenu||!this.currentMenu.isShowingNow){
return;
}
var m=this.currentFocusMenu;
while(m){
if(m.processKey(e)){
e.preventDefault();
e.stopPropagation();
break;
}
m=m.parentPopup;
}
},this.onClick=function(e){
if(!this.currentMenu){
return;
}
var _93a=dojo.html.getScroll().offset;
var m=this.currentMenu;
while(m){
if(dojo.html.overElement(m.domNode,e)||dojo.html.isDescendantOf(e.target,m.domNode)){
return;
}
m=m.currentSubpopup;
}
if(this.currentButton&&dojo.html.overElement(this.currentButton,e)){
return;
}
this.currentMenu.close();
};
};
dojo.provide("dojo.widget.Tooltip");
dojo.widget.defineWidget("dojo.widget.Tooltip",[dojo.widget.ContentPane,dojo.widget.PopupContainerBase],{caption:"",showDelay:500,hideDelay:100,connectId:"",templateCssPath:dojo.uri.dojoUri("src/widget/templates/TooltipTemplate.css"),fillInTemplate:function(args,frag){
if(this.caption!=""){
this.domNode.appendChild(document.createTextNode(this.caption));
}
this._connectNode=dojo.byId(this.connectId);
dojo.widget.Tooltip.superclass.fillInTemplate.call(this,args,frag);
this.addOnLoad(this,"_loadedContent");
dojo.html.addClass(this.domNode,"dojoTooltip");
var _93e=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_93e);
this.applyPopupBasicStyle();
},postCreate:function(args,frag){
dojo.event.connect(this._connectNode,"onmouseover",this,"_onMouseOver");
dojo.widget.Tooltip.superclass.postCreate.call(this,args,frag);
},_onMouseOver:function(e){
this._mouse={x:e.pageX,y:e.pageY};
if(!this._tracking){
dojo.event.connect(document.documentElement,"onmousemove",this,"_onMouseMove");
this._tracking=true;
}
this._onHover(e);
},_onMouseMove:function(e){
this._mouse={x:e.pageX,y:e.pageY};
if(dojo.html.overElement(this._connectNode,e)||dojo.html.overElement(this.domNode,e)){
this._onHover(e);
}else{
this._onUnHover(e);
}
},_onHover:function(e){
if(this._hover){
return;
}
this._hover=true;
if(this._hideTimer){
clearTimeout(this._hideTimer);
delete this._hideTimer;
}
if(!this.isShowingNow&&!this._showTimer){
this._showTimer=setTimeout(dojo.lang.hitch(this,"open"),this.showDelay);
}
},_onUnHover:function(e){
if(!this._hover){
return;
}
this._hover=false;
if(this._showTimer){
clearTimeout(this._showTimer);
delete this._showTimer;
}
if(this.isShowingNow&&!this._hideTimer){
this._hideTimer=setTimeout(dojo.lang.hitch(this,"close"),this.hideDelay);
}
if(!this.isShowingNow){
dojo.event.disconnect(document.documentElement,"onmousemove",this,"_onMouseMove");
this._tracking=false;
}
},open:function(){
if(this.isShowingNow){
return;
}
dojo.widget.PopupContainerBase.prototype.open.call(this,this._mouse.x,this._mouse.y,null,[this._mouse.x,this._mouse.y],"TL,TR,BL,BR",[10,15]);
},close:function(){
if(this.isShowingNow){
if(this._showTimer){
clearTimeout(this._showTimer);
delete this._showTimer;
}
if(this._hideTimer){
clearTimeout(this._hideTimer);
delete this._hideTimer;
}
dojo.event.disconnect(document.documentElement,"onmousemove",this,"_onMouseMove");
this._tracking=false;
dojo.widget.PopupContainerBase.prototype.close.call(this);
}
},_position:function(){
this.move(this._mouse.x,this._mouse.y,[10,15],"TL,TR,BL,BR");
},_loadedContent:function(){
if(this.isShowingNow){
this._position();
}
},checkSize:function(){
},uninitialize:function(){
this.close();
dojo.event.disconnect(this._connectNode,"onmouseover",this,"_onMouseOver");
}});
dojo.provide("dojo.widget.TreeCommon");
dojo.declare("dojo.widget.TreeCommon",null,{listenTreeEvents:[],listenedTrees:{},listenNodeFilter:null,listenTree:function(tree){
var _946=this;
if(this.listenedTrees[tree.widgetId]){
return;
}
dojo.lang.forEach(this.listenTreeEvents,function(_947){
var _948="on"+_947.charAt(0).toUpperCase()+_947.substr(1);
dojo.event.topic.subscribe(tree.eventNames[_947],_946,_948);
});
var _949;
if(this.listenNodeFilter){
this.processDescendants(tree,this.listenNodeFilter,this.listenNode,true);
}
this.listenedTrees[tree.widgetId]=true;
},listenNode:function(){
},unlistenNode:function(){
},unlistenTree:function(tree,_94b){
var _94c=this;
if(!this.listenedTrees[tree.widgetId]){
return;
}
dojo.lang.forEach(this.listenTreeEvents,function(_94d){
var _94e="on"+_94d.charAt(0).toUpperCase()+_94d.substr(1);
dojo.event.topic.unsubscribe(tree.eventNames[_94d],_94c,_94e);
});
if(this.listenNodeFilter){
this.processDescendants(tree,this.listenNodeFilter,this.unlistenNode,true);
}
delete this.listenedTrees[tree.widgetId];
},checkPathCondition:function(_94f,_950){
while(_94f&&!_94f.widgetId){
if(_950.call(null,_94f)){
return true;
}
_94f=_94f.parentNode;
}
return false;
},domElement2TreeNode:function(_951){
while(_951&&!_951.widgetId){
_951=_951.parentNode;
}
if(!_951){
return null;
}
var _952=dojo.widget.byId(_951.widgetId);
if(!_952.isTreeNode){
return null;
}
return _952;
},processDescendants:function(elem,_954,func,_956){
var _957=this;
if(!_956){
if(!_954.call(_957,elem)){
return;
}
func.call(_957,elem);
}
var _958=[elem];
while(elem=_958.pop()){
dojo.lang.forEach(elem.children,function(elem){
if(_954.call(_957,elem)){
func.call(_957,elem);
_958.push(elem);
}
});
}
}});
dojo.provide("dojo.widget.TreeSelectorV3");
dojo.widget.defineWidget("dojo.widget.TreeSelectorV3",[dojo.widget.HtmlWidget,dojo.widget.TreeCommon],function(){
this.eventNames={};
this.listenedTrees={};
this.selectedNodes=[];
this.lastClicked={};
},{listenTreeEvents:["afterTreeCreate","afterCollapse","afterChangeTree","afterDetach","beforeTreeDestroy"],listenNodeFilter:function(elem){
return elem instanceof dojo.widget.Widget;
},allowedMulti:true,dblselectTimeout:300,eventNamesDefault:{select:"select",deselect:"deselect",dblselect:"dblselect"},onAfterTreeCreate:function(_95b){
var tree=_95b.source;
dojo.event.browser.addListener(tree.domNode,"onclick",dojo.lang.hitch(this,this.onTreeClick));
if(dojo.render.html.ie){
dojo.event.browser.addListener(tree.domNode,"ondblclick",dojo.lang.hitch(this,this.onTreeDblClick));
}
dojo.event.browser.addListener(tree.domNode,"onKey",dojo.lang.hitch(this,this.onKey));
},onKey:function(e){
if(!e.key||e.ctrkKey||e.altKey){
return;
}
switch(e.key){
case e.KEY_ENTER:
var node=this.domElement2TreeNode(e.target);
if(node){
this.processNode(node,e);
}
}
},onAfterChangeTree:function(_95f){
if(!_95f.oldTree&&_95f.node.selected){
this.select(_95f.node);
}
if(!_95f.newTree||!this.listenedTrees[_95f.newTree.widgetId]){
if(this.selectedNode&&_95f.node.children){
this.deselectIfAncestorMatch(_95f.node);
}
}
},initialize:function(args){
for(name in this.eventNamesDefault){
if(dojo.lang.isUndefined(this.eventNames[name])){
this.eventNames[name]=this.widgetId+"/"+this.eventNamesDefault[name];
}
}
},onBeforeTreeDestroy:function(_961){
this.unlistenTree(_961.source);
},onAfterCollapse:function(_962){
this.deselectIfAncestorMatch(_962.source);
},onTreeDblClick:function(_963){
this.onTreeClick(_963);
},checkSpecialEvent:function(_964){
return _964.shiftKey||_964.ctrlKey;
},onTreeClick:function(_965){
var node=this.domElement2TreeNode(_965.target);
if(!node){
return;
}
var _967=function(_968){
return _968===node.labelNode;
};
if(this.checkPathCondition(_965.target,_967)){
this.processNode(node,_965);
}
},processNode:function(node,_96a){
if(node.actionIsDisabled(node.actions.SELECT)){
return;
}
if(dojo.lang.inArray(this.selectedNodes,node)){
if(this.checkSpecialEvent(_96a)){
this.deselect(node);
return;
}
var _96b=this;
var i=0;
var _96d;
while(this.selectedNodes.length>i){
_96d=this.selectedNodes[i];
if(_96d!==node){
this.deselect(_96d);
continue;
}
i++;
}
var _96e=this.checkRecentClick(node);
eventName=_96e?this.eventNames.dblselect:this.eventNames.select;
if(_96e){
eventName=this.eventNames.dblselect;
this.forgetLastClicked();
}else{
eventName=this.eventNames.select;
this.setLastClicked(node);
}
dojo.event.topic.publish(eventName,{node:node});
return;
}
this.deselectIfNoMulti(_96a);
this.setLastClicked(node);
this.select(node);
},forgetLastClicked:function(){
this.lastClicked={};
},setLastClicked:function(node){
this.lastClicked.date=new Date();
this.lastClicked.node=node;
},checkRecentClick:function(node){
var diff=new Date()-this.lastClicked.date;
if(this.lastClicked.node&&diff<this.dblselectTimeout){
return true;
}else{
return false;
}
},deselectIfNoMulti:function(_972){
if(!this.checkSpecialEvent(_972)||!this.allowedMulti){
this.deselectAll();
}
},deselectIfAncestorMatch:function(_973){
var _974=this;
dojo.lang.forEach(this.selectedNodes,function(node){
var _976=node;
node=node.parent;
while(node&&node.isTreeNode){
if(node===_973){
_974.deselect(_976);
return;
}
node=node.parent;
}
});
},onAfterDetach:function(_977){
this.deselectIfAncestorMatch(_977.child);
},select:function(node){
var _979=dojo.lang.find(this.selectedNodes,node,true);
if(_979>=0){
return;
}
this.selectedNodes.push(node);
dojo.event.topic.publish(this.eventNames.select,{node:node});
},deselect:function(node){
var _97b=dojo.lang.find(this.selectedNodes,node,true);
if(_97b<0){
return;
}
this.selectedNodes.splice(_97b,1);
dojo.event.topic.publish(this.eventNames.deselect,{node:node});
},deselectAll:function(){
while(this.selectedNodes.length){
this.deselect(this.selectedNodes[0]);
}
}});
dojo.provide("dojo.html.*");
dojo.provide("dojo.lfx.shadow");
dojo.lfx.shadow=function(node){
this.shadowPng=dojo.uri.dojoUri("src/html/images/shadow");
this.shadowThickness=8;
this.shadowOffset=15;
this.init(node);
};
dojo.extend(dojo.lfx.shadow,{init:function(node){
this.node=node;
this.pieces={};
var x1=-1*this.shadowThickness;
var y0=this.shadowOffset;
var y1=this.shadowOffset+this.shadowThickness;
this._makePiece("tl","top",y0,"left",x1);
this._makePiece("l","top",y1,"left",x1,"scale");
this._makePiece("tr","top",y0,"left",0);
this._makePiece("r","top",y1,"left",0,"scale");
this._makePiece("bl","top",0,"left",x1);
this._makePiece("b","top",0,"left",0,"crop");
this._makePiece("br","top",0,"left",0);
},_makePiece:function(name,_982,_983,_984,_985,_986){
var img;
var url=this.shadowPng+name.toUpperCase()+".png";
if(dojo.render.html.ie55||dojo.render.html.ie60){
img=dojo.doc().createElement("div");
img.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"'"+(_986?", sizingMethod='"+_986+"'":"")+")";
}else{
img=dojo.doc().createElement("img");
img.src=url;
}
img.style.position="absolute";
img.style[_982]=_983+"px";
img.style[_984]=_985+"px";
img.style.width=this.shadowThickness+"px";
img.style.height=this.shadowThickness+"px";
this.pieces[name]=img;
this.node.appendChild(img);
},size:function(_989,_98a){
var _98b=_98a-(this.shadowOffset+this.shadowThickness+1);
if(_98b<0){
_98b=0;
}
if(_98a<1){
_98a=1;
}
if(_989<1){
_989=1;
}
with(this.pieces){
l.style.height=_98b+"px";
r.style.height=_98b+"px";
b.style.width=(_989-1)+"px";
bl.style.top=(_98a-1)+"px";
b.style.top=(_98a-1)+"px";
br.style.top=(_98a-1)+"px";
tr.style.left=(_989-1)+"px";
r.style.left=(_989-1)+"px";
br.style.left=(_989-1)+"px";
}
}});
dojo.provide("dojo.dnd.DragAndDrop");
dojo.declare("dojo.dnd.DragSource",null,{type:"",onDragEnd:function(evt){
},onDragStart:function(evt){
},onSelected:function(evt){
},unregister:function(){
dojo.dnd.dragManager.unregisterDragSource(this);
},reregister:function(){
dojo.dnd.dragManager.registerDragSource(this);
}});
dojo.declare("dojo.dnd.DragObject",null,{type:"",register:function(){
var dm=dojo.dnd.dragManager;
if(dm["registerDragObject"]){
dm.registerDragObject(this);
}
},onDragStart:function(evt){
},onDragMove:function(evt){
},onDragOver:function(evt){
},onDragOut:function(evt){
},onDragEnd:function(evt){
},onDragLeave:dojo.lang.forward("onDragOut"),onDragEnter:dojo.lang.forward("onDragOver"),ondragout:dojo.lang.forward("onDragOut"),ondragover:dojo.lang.forward("onDragOver")});
dojo.declare("dojo.dnd.DropTarget",null,{acceptsType:function(type){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
if(!dojo.lang.inArray(this.acceptedTypes,type)){
return false;
}
}
return true;
},accepts:function(_996){
if(!dojo.lang.inArray(this.acceptedTypes,"*")){
for(var i=0;i<_996.length;i++){
if(!dojo.lang.inArray(this.acceptedTypes,_996[i].type)){
return false;
}
}
}
return true;
},unregister:function(){
dojo.dnd.dragManager.unregisterDropTarget(this);
},onDragOver:function(evt){
},onDragOut:function(evt){
},onDragMove:function(evt){
},onDropStart:function(evt){
},onDrop:function(evt){
},onDropEnd:function(){
}},function(){
this.acceptedTypes=[];
});
dojo.dnd.DragEvent=function(){
this.dragSource=null;
this.dragObject=null;
this.target=null;
this.eventStatus="success";
};
dojo.declare("dojo.dnd.DragManager",null,{selectedSources:[],dragObjects:[],dragSources:[],registerDragSource:function(_99d){
},dropTargets:[],registerDropTarget:function(_99e){
},lastDragTarget:null,currentDragTarget:null,onKeyDown:function(){
},onMouseOut:function(){
},onMouseMove:function(){
},onMouseUp:function(){
}});
dojo.provide("dojo.dnd.HtmlDragManager");
dojo.declare("dojo.dnd.HtmlDragManager",dojo.dnd.DragManager,{disabled:false,nestedTargets:false,mouseDownTimer:null,dsCounter:0,dsPrefix:"dojoDragSource",dropTargetDimensions:[],currentDropTarget:null,previousDropTarget:null,_dragTriggered:false,selectedSources:[],dragObjects:[],dragSources:[],currentX:null,currentY:null,lastX:null,lastY:null,mouseDownX:null,mouseDownY:null,threshold:7,dropAcceptable:false,cancelEvent:function(e){
e.stopPropagation();
e.preventDefault();
},registerDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _9a2=dp+"Idx_"+(this.dsCounter++);
ds.dragSourceId=_9a2;
this.dragSources[_9a2]=ds;
ds.domNode.setAttribute(dp,_9a2);
if(dojo.render.html.ie){
dojo.event.browser.addListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},unregisterDragSource:function(ds){
if(ds["domNode"]){
var dp=this.dsPrefix;
var _9a5=ds.dragSourceId;
delete ds.dragSourceId;
delete this.dragSources[_9a5];
ds.domNode.setAttribute(dp,null);
if(dojo.render.html.ie){
dojo.event.browser.removeListener(ds.domNode,"ondragstart",this.cancelEvent);
}
}
},registerDropTarget:function(dt){
this.dropTargets.push(dt);
},unregisterDropTarget:function(dt){
var _9a8=dojo.lang.find(this.dropTargets,dt,true);
if(_9a8>=0){
this.dropTargets.splice(_9a8,1);
}
},getDragSource:function(e){
var tn=e.target;
if(tn===dojo.body()){
return;
}
var ta=dojo.html.getAttribute(tn,this.dsPrefix);
while((!ta)&&(tn)){
tn=tn.parentNode;
if((!tn)||(tn===dojo.body())){
return;
}
ta=dojo.html.getAttribute(tn,this.dsPrefix);
}
return this.dragSources[ta];
},onKeyDown:function(e){
},onMouseDown:function(e){
if(this.disabled){
return;
}
if(dojo.render.html.ie){
if(e.button!=1){
return;
}
}else{
if(e.which!=1){
return;
}
}
var _9ae=e.target.nodeType==dojo.html.TEXT_NODE?e.target.parentNode:e.target;
if(dojo.html.isTag(_9ae,"button","textarea","input","select","option")){
return;
}
var ds=this.getDragSource(e);
if(!ds){
return;
}
if(!dojo.lang.inArray(this.selectedSources,ds)){
this.selectedSources.push(ds);
ds.onSelected();
}
this.mouseDownX=e.pageX;
this.mouseDownY=e.pageY;
e.preventDefault();
dojo.event.connect(document,"onmousemove",this,"onMouseMove");
},onMouseUp:function(e,_9b1){
if(this.selectedSources.length==0){
return;
}
this.mouseDownX=null;
this.mouseDownY=null;
this._dragTriggered=false;
e.dragSource=this.dragSource;
if((!e.shiftKey)&&(!e.ctrlKey)){
if(this.currentDropTarget){
this.currentDropTarget.onDropStart();
}
dojo.lang.forEach(this.dragObjects,function(_9b2){
var ret=null;
if(!_9b2){
return;
}
if(this.currentDropTarget){
e.dragObject=_9b2;
var ce=this.currentDropTarget.domNode.childNodes;
if(ce.length>0){
e.dropTarget=ce[0];
while(e.dropTarget==_9b2.domNode){
e.dropTarget=e.dropTarget.nextSibling;
}
}else{
e.dropTarget=this.currentDropTarget.domNode;
}
if(this.dropAcceptable){
ret=this.currentDropTarget.onDrop(e);
}else{
this.currentDropTarget.onDragOut(e);
}
}
e.dragStatus=this.dropAcceptable&&ret?"dropSuccess":"dropFailure";
dojo.lang.delayThese([function(){
try{
_9b2.dragSource.onDragEnd(e);
}
catch(err){
var _9b5={};
for(var i in e){
if(i=="type"){
_9b5.type="mouseup";
continue;
}
_9b5[i]=e[i];
}
_9b2.dragSource.onDragEnd(_9b5);
}
},function(){
_9b2.onDragEnd(e);
}]);
},this);
this.selectedSources=[];
this.dragObjects=[];
this.dragSource=null;
if(this.currentDropTarget){
this.currentDropTarget.onDropEnd();
}
}else{
}
dojo.event.disconnect(document,"onmousemove",this,"onMouseMove");
this.currentDropTarget=null;
},onScroll:function(){
for(var i=0;i<this.dragObjects.length;i++){
if(this.dragObjects[i].updateDragOffset){
this.dragObjects[i].updateDragOffset();
}
}
if(this.dragObjects.length){
this.cacheTargetLocations();
}
},_dragStartDistance:function(x,y){
if((!this.mouseDownX)||(!this.mouseDownX)){
return;
}
var dx=Math.abs(x-this.mouseDownX);
var dx2=dx*dx;
var dy=Math.abs(y-this.mouseDownY);
var dy2=dy*dy;
return parseInt(Math.sqrt(dx2+dy2),10);
},cacheTargetLocations:function(){
dojo.profile.start("cacheTargetLocations");
this.dropTargetDimensions=[];
dojo.lang.forEach(this.dropTargets,function(_9be){
var tn=_9be.domNode;
if(!tn||!_9be.accepts([this.dragSource])){
return;
}
var abs=dojo.html.getAbsolutePosition(tn,true);
var bb=dojo.html.getBorderBox(tn);
this.dropTargetDimensions.push([[abs.x,abs.y],[abs.x+bb.width,abs.y+bb.height],_9be]);
},this);
dojo.profile.end("cacheTargetLocations");
},onMouseMove:function(e){
if((dojo.render.html.ie)&&(e.button!=1)){
this.currentDropTarget=null;
this.onMouseUp(e,true);
return;
}
if((this.selectedSources.length)&&(!this.dragObjects.length)){
var dx;
var dy;
if(!this._dragTriggered){
this._dragTriggered=(this._dragStartDistance(e.pageX,e.pageY)>this.threshold);
if(!this._dragTriggered){
return;
}
dx=e.pageX-this.mouseDownX;
dy=e.pageY-this.mouseDownY;
}
this.dragSource=this.selectedSources[0];
dojo.lang.forEach(this.selectedSources,function(_9c5){
if(!_9c5){
return;
}
var tdo=_9c5.onDragStart(e);
if(tdo){
tdo.onDragStart(e);
tdo.dragOffset.y+=dy;
tdo.dragOffset.x+=dx;
tdo.dragSource=_9c5;
this.dragObjects.push(tdo);
}
},this);
this.previousDropTarget=null;
this.cacheTargetLocations();
}
dojo.lang.forEach(this.dragObjects,function(_9c7){
if(_9c7){
_9c7.onDragMove(e);
}
});
if(this.currentDropTarget){
var c=dojo.html.toCoordinateObject(this.currentDropTarget.domNode,true);
var dtp=[[c.x,c.y],[c.x+c.width,c.y+c.height]];
}
if((!this.nestedTargets)&&(dtp)&&(this.isInsideBox(e,dtp))){
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}else{
var _9ca=this.findBestTarget(e);
if(_9ca.target===null){
if(this.currentDropTarget){
this.currentDropTarget.onDragOut(e);
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget=null;
}
this.dropAcceptable=false;
return;
}
if(this.currentDropTarget!==_9ca.target){
if(this.currentDropTarget){
this.previousDropTarget=this.currentDropTarget;
this.currentDropTarget.onDragOut(e);
}
this.currentDropTarget=_9ca.target;
e.dragObjects=this.dragObjects;
this.dropAcceptable=this.currentDropTarget.onDragOver(e);
}else{
if(this.dropAcceptable){
this.currentDropTarget.onDragMove(e,this.dragObjects);
}
}
}
},findBestTarget:function(e){
var _9cc=this;
var _9cd=new Object();
_9cd.target=null;
_9cd.points=null;
dojo.lang.every(this.dropTargetDimensions,function(_9ce){
if(!_9cc.isInsideBox(e,_9ce)){
return true;
}
_9cd.target=_9ce[2];
_9cd.points=_9ce;
return Boolean(_9cc.nestedTargets);
});
return _9cd;
},isInsideBox:function(e,_9d0){
if((e.pageX>_9d0[0][0])&&(e.pageX<_9d0[1][0])&&(e.pageY>_9d0[0][1])&&(e.pageY<_9d0[1][1])){
return true;
}
return false;
},onMouseOver:function(e){
},onMouseOut:function(e){
}});
dojo.dnd.dragManager=new dojo.dnd.HtmlDragManager();
(function(){
var d=document;
var dm=dojo.dnd.dragManager;
dojo.event.connect(d,"onkeydown",dm,"onKeyDown");
dojo.event.connect(d,"onmouseover",dm,"onMouseOver");
dojo.event.connect(d,"onmouseout",dm,"onMouseOut");
dojo.event.connect(d,"onmousedown",dm,"onMouseDown");
dojo.event.connect(d,"onmouseup",dm,"onMouseUp");
dojo.event.connect(window,"onscroll",dm,"onScroll");
})();
dojo.provide("dojo.dnd.HtmlDragAndDrop");
dojo.declare("dojo.dnd.HtmlDragSource",dojo.dnd.DragSource,{dragClass:"",onDragStart:function(){
var _9d5=new dojo.dnd.HtmlDragObject(this.dragObject,this.type);
if(this.dragClass){
_9d5.dragClass=this.dragClass;
}
if(this.constrainToContainer){
_9d5.constrainTo(this.constrainingContainer||this.domNode.parentNode);
}
return _9d5;
},setDragHandle:function(node){
node=dojo.byId(node);
dojo.dnd.dragManager.unregisterDragSource(this);
this.domNode=node;
dojo.dnd.dragManager.registerDragSource(this);
},setDragTarget:function(node){
this.dragObject=node;
},constrainTo:function(_9d8){
this.constrainToContainer=true;
if(_9d8){
this.constrainingContainer=_9d8;
}
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragSource(this.dragObjects[i]));
}
},addDragObjects:function(el){
for(var i=0;i<arguments.length;i++){
this.dragObjects.push(dojo.byId(arguments[i]));
}
}},function(node,type){
node=dojo.byId(node);
this.dragObjects=[];
this.constrainToContainer=false;
if(node){
this.domNode=node;
this.dragObject=node;
this.type=(type)||(this.domNode.nodeName.toLowerCase());
dojo.dnd.DragSource.prototype.reregister.call(this);
}
});
dojo.declare("dojo.dnd.HtmlDragObject",dojo.dnd.DragObject,{dragClass:"",opacity:0.5,createIframe:true,disableX:false,disableY:false,createDragNode:function(){
var node=this.domNode.cloneNode(true);
if(this.dragClass){
dojo.html.addClass(node,this.dragClass);
}
if(this.opacity<1){
dojo.html.setOpacity(node,this.opacity);
}
var ltn=node.tagName.toLowerCase();
var isTr=(ltn=="tr");
if((isTr)||(ltn=="tbody")){
var doc=this.domNode.ownerDocument;
var _9e2=doc.createElement("table");
if(isTr){
var _9e3=doc.createElement("tbody");
_9e2.appendChild(_9e3);
_9e3.appendChild(node);
}else{
_9e2.appendChild(node);
}
var _9e4=((isTr)?this.domNode:this.domNode.firstChild);
var _9e5=((isTr)?node:node.firstChild);
var _9e6=tdp.childNodes;
var _9e7=_9e5.childNodes;
for(var i=0;i<_9e6.length;i++){
if((_9e7[i])&&(_9e7[i].style)){
_9e7[i].style.width=dojo.html.getContentBox(_9e6[i]).width+"px";
}
}
node=_9e2;
}
if((dojo.render.html.ie55||dojo.render.html.ie60)&&this.createIframe){
with(node.style){
top="0px";
left="0px";
}
var _9e9=document.createElement("div");
_9e9.appendChild(node);
this.bgIframe=new dojo.html.BackgroundIframe(_9e9);
_9e9.appendChild(this.bgIframe.iframe);
node=_9e9;
}
node.style.zIndex=999;
return node;
},onDragStart:function(e){
dojo.html.clearSelection();
this.scrollOffset=dojo.html.getScroll().offset;
this.dragStartPosition=dojo.html.getAbsolutePosition(this.domNode,true);
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.dragClone=this.createDragNode();
this.containingBlockPosition=this.domNode.offsetParent?dojo.html.getAbsolutePosition(this.domNode.offsetParent,true):{x:0,y:0};
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
with(this.dragClone.style){
position="absolute";
top=this.dragOffset.y+e.pageY+"px";
left=this.dragOffset.x+e.pageX+"px";
}
dojo.body().appendChild(this.dragClone);
dojo.event.topic.publish("dragStart",{source:this});
},getConstraints:function(){
if(this.constrainingContainer.nodeName.toLowerCase()=="body"){
var _9eb=dojo.html.getViewport();
var _9ec=_9eb.width;
var _9ed=_9eb.height;
var _9ee=dojo.html.getScroll().offset;
var x=_9ee.x;
var y=_9ee.y;
}else{
var _9f1=dojo.html.getContentBox(this.constrainingContainer);
_9ec=_9f1.width;
_9ed=_9f1.height;
x=this.containingBlockPosition.x+dojo.html.getPixelValue(this.constrainingContainer,"padding-left",true)+dojo.html.getBorderExtent(this.constrainingContainer,"left");
y=this.containingBlockPosition.y+dojo.html.getPixelValue(this.constrainingContainer,"padding-top",true)+dojo.html.getBorderExtent(this.constrainingContainer,"top");
}
var mb=dojo.html.getMarginBox(this.domNode);
return {minX:x,minY:y,maxX:x+_9ec-mb.width,maxY:y+_9ed-mb.height};
},updateDragOffset:function(){
var _9f3=dojo.html.getScroll().offset;
if(_9f3.y!=this.scrollOffset.y){
var diff=_9f3.y-this.scrollOffset.y;
this.dragOffset.y+=diff;
this.scrollOffset.y=_9f3.y;
}
if(_9f3.x!=this.scrollOffset.x){
var diff=_9f3.x-this.scrollOffset.x;
this.dragOffset.x+=diff;
this.scrollOffset.x=_9f3.x;
}
},onDragMove:function(e){
this.updateDragOffset();
var x=this.dragOffset.x+e.pageX;
var y=this.dragOffset.y+e.pageY;
if(this.constrainToContainer){
if(x<this.constraints.minX){
x=this.constraints.minX;
}
if(y<this.constraints.minY){
y=this.constraints.minY;
}
if(x>this.constraints.maxX){
x=this.constraints.maxX;
}
if(y>this.constraints.maxY){
y=this.constraints.maxY;
}
}
this.setAbsolutePosition(x,y);
dojo.event.topic.publish("dragMove",{source:this});
},setAbsolutePosition:function(x,y){
if(!this.disableY){
this.dragClone.style.top=y+"px";
}
if(!this.disableX){
this.dragClone.style.left=x+"px";
}
},onDragEnd:function(e){
switch(e.dragStatus){
case "dropSuccess":
dojo.html.removeNode(this.dragClone);
this.dragClone=null;
break;
case "dropFailure":
var _9fb=dojo.html.getAbsolutePosition(this.dragClone,true);
var _9fc={left:this.dragStartPosition.x+1,top:this.dragStartPosition.y+1};
var anim=dojo.lfx.slideTo(this.dragClone,_9fc,300);
var _9fe=this;
dojo.event.connect(anim,"onEnd",function(e){
dojo.html.removeNode(_9fe.dragClone);
_9fe.dragClone=null;
});
anim.play();
break;
}
dojo.event.topic.publish("dragEnd",{source:this});
},constrainTo:function(_a00){
this.constrainToContainer=true;
if(_a00){
this.constrainingContainer=_a00;
}else{
this.constrainingContainer=this.domNode.parentNode;
}
}},function(node,type){
this.domNode=dojo.byId(node);
this.type=type;
this.constrainToContainer=false;
this.dragSource=null;
dojo.dnd.DragObject.prototype.register.call(this);
});
dojo.declare("dojo.dnd.HtmlDropTarget",dojo.dnd.DropTarget,{vertical:false,onDragOver:function(e){
if(!this.accepts(e.dragObjects)){
return false;
}
this.childBoxes=[];
for(var i=0,_a05;i<this.domNode.childNodes.length;i++){
_a05=this.domNode.childNodes[i];
if(_a05.nodeType!=dojo.html.ELEMENT_NODE){
continue;
}
var pos=dojo.html.getAbsolutePosition(_a05,true);
var _a07=dojo.html.getBorderBox(_a05);
this.childBoxes.push({top:pos.y,bottom:pos.y+_a07.height,left:pos.x,right:pos.x+_a07.width,height:_a07.height,width:_a07.width,node:_a05});
}
return true;
},_getNodeUnderMouse:function(e){
for(var i=0,_a0a;i<this.childBoxes.length;i++){
with(this.childBoxes[i]){
if(e.pageX>=left&&e.pageX<=right&&e.pageY>=top&&e.pageY<=bottom){
return i;
}
}
}
return -1;
},createDropIndicator:function(){
this.dropIndicator=document.createElement("div");
with(this.dropIndicator.style){
position="absolute";
zIndex=999;
if(this.vertical){
borderLeftWidth="1px";
borderLeftColor="black";
borderLeftStyle="solid";
height=dojo.html.getBorderBox(this.domNode).height+"px";
top=dojo.html.getAbsolutePosition(this.domNode,true).y+"px";
}else{
borderTopWidth="1px";
borderTopColor="black";
borderTopStyle="solid";
width=dojo.html.getBorderBox(this.domNode).width+"px";
left=dojo.html.getAbsolutePosition(this.domNode,true).x+"px";
}
}
},onDragMove:function(e,_a0c){
var i=this._getNodeUnderMouse(e);
if(!this.dropIndicator){
this.createDropIndicator();
}
var _a0e=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
var hide=false;
if(i<0){
if(this.childBoxes.length){
var _a10=(dojo.html.gravity(this.childBoxes[0].node,e)&_a0e);
if(_a10){
hide=true;
}
}else{
var _a10=true;
}
}else{
var _a11=this.childBoxes[i];
var _a10=(dojo.html.gravity(_a11.node,e)&_a0e);
if(_a11.node===_a0c[0].dragSource.domNode){
hide=true;
}else{
var _a12=_a10?(i>0?this.childBoxes[i-1]:_a11):(i<this.childBoxes.length-1?this.childBoxes[i+1]:_a11);
if(_a12.node===_a0c[0].dragSource.domNode){
hide=true;
}
}
}
if(hide){
this.dropIndicator.style.display="none";
return;
}else{
this.dropIndicator.style.display="";
}
this.placeIndicator(e,_a0c,i,_a10);
if(!dojo.html.hasParent(this.dropIndicator)){
dojo.body().appendChild(this.dropIndicator);
}
},placeIndicator:function(e,_a14,_a15,_a16){
var _a17=this.vertical?"left":"top";
var _a18;
if(_a15<0){
if(this.childBoxes.length){
_a18=_a16?this.childBoxes[0]:this.childBoxes[this.childBoxes.length-1];
}else{
this.dropIndicator.style[_a17]=dojo.html.getAbsolutePosition(this.domNode,true)[this.vertical?"x":"y"]+"px";
}
}else{
_a18=this.childBoxes[_a15];
}
if(_a18){
this.dropIndicator.style[_a17]=(_a16?_a18[_a17]:_a18[this.vertical?"right":"bottom"])+"px";
if(this.vertical){
this.dropIndicator.style.height=_a18.height+"px";
this.dropIndicator.style.top=_a18.top+"px";
}else{
this.dropIndicator.style.width=_a18.width+"px";
this.dropIndicator.style.left=_a18.left+"px";
}
}
},onDragOut:function(e){
if(this.dropIndicator){
dojo.html.removeNode(this.dropIndicator);
delete this.dropIndicator;
}
},onDrop:function(e){
this.onDragOut(e);
var i=this._getNodeUnderMouse(e);
var _a1c=this.vertical?dojo.html.gravity.WEST:dojo.html.gravity.NORTH;
if(i<0){
if(this.childBoxes.length){
if(dojo.html.gravity(this.childBoxes[0].node,e)&_a1c){
return this.insert(e,this.childBoxes[0].node,"before");
}else{
return this.insert(e,this.childBoxes[this.childBoxes.length-1].node,"after");
}
}
return this.insert(e,this.domNode,"append");
}
var _a1d=this.childBoxes[i];
if(dojo.html.gravity(_a1d.node,e)&_a1c){
return this.insert(e,_a1d.node,"before");
}else{
return this.insert(e,_a1d.node,"after");
}
},insert:function(e,_a1f,_a20){
var node=e.dragObject.domNode;
if(_a20=="before"){
return dojo.html.insertBefore(node,_a1f);
}else{
if(_a20=="after"){
return dojo.html.insertAfter(node,_a1f);
}else{
if(_a20=="append"){
_a1f.appendChild(node);
return true;
}
}
}
return false;
}},function(node,_a23){
if(arguments.length==0){
return;
}
this.domNode=dojo.byId(node);
dojo.dnd.DropTarget.call(this);
if(_a23&&dojo.lang.isString(_a23)){
_a23=[_a23];
}
this.acceptedTypes=_a23||[];
dojo.dnd.dragManager.registerDropTarget(this);
});
dojo.provide("dojo.dnd.*");
dojo.provide("dojo.dnd.HtmlDragMove");
dojo.declare("dojo.dnd.HtmlDragMoveSource",dojo.dnd.HtmlDragSource,{onDragStart:function(){
var _a24=new dojo.dnd.HtmlDragMoveObject(this.dragObject,this.type);
if(this.constrainToContainer){
_a24.constrainTo(this.constrainingContainer);
}
return _a24;
},onSelected:function(){
for(var i=0;i<this.dragObjects.length;i++){
dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.HtmlDragMoveSource(this.dragObjects[i]));
}
}});
dojo.declare("dojo.dnd.HtmlDragMoveObject",dojo.dnd.HtmlDragObject,{onDragStart:function(e){
dojo.html.clearSelection();
this.dragClone=this.domNode;
if(dojo.html.getComputedStyle(this.domNode,"position")!="absolute"){
this.domNode.style.position="relative";
}
var left=parseInt(dojo.html.getComputedStyle(this.domNode,"left"));
var top=parseInt(dojo.html.getComputedStyle(this.domNode,"top"));
this.dragStartPosition={x:isNaN(left)?0:left,y:isNaN(top)?0:top};
this.scrollOffset=dojo.html.getScroll().offset;
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.containingBlockPosition={x:0,y:0};
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
dojo.event.connect(this.domNode,"onclick",this,"_squelchOnClick");
},onDragEnd:function(e){
},setAbsolutePosition:function(x,y){
if(!this.disableY){
this.domNode.style.top=y+"px";
}
if(!this.disableX){
this.domNode.style.left=x+"px";
}
},_squelchOnClick:function(e){
dojo.event.browser.stopEvent(e);
dojo.event.disconnect(this.domNode,"onclick",this,"_squelchOnClick");
}});
dojo.provide("dojo.widget.Dialog");
dojo.declare("dojo.widget.ModalDialogBase",null,{isContainer:true,focusElement:"",bgColor:"black",bgOpacity:0.4,followScroll:true,closeOnBackgroundClick:false,trapTabs:function(e){
if(e.target==this.tabStartOuter){
if(this._fromTrap){
this.tabStart.focus();
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabEnd.focus();
}
}else{
if(e.target==this.tabStart){
if(this._fromTrap){
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabEnd.focus();
}
}else{
if(e.target==this.tabEndOuter){
if(this._fromTrap){
this.tabEnd.focus();
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabStart.focus();
}
}else{
if(e.target==this.tabEnd){
if(this._fromTrap){
this._fromTrap=false;
}else{
this._fromTrap=true;
this.tabStart.focus();
}
}
}
}
}
},clearTrap:function(e){
var _a2f=this;
setTimeout(function(){
_a2f._fromTrap=false;
},100);
},postCreate:function(){
with(this.domNode.style){
position="absolute";
zIndex=999;
display="none";
overflow="visible";
}
var b=dojo.body();
b.appendChild(this.domNode);
this.bg=document.createElement("div");
this.bg.className="dialogUnderlay";
with(this.bg.style){
position="absolute";
left=top="0px";
zIndex=998;
display="none";
}
b.appendChild(this.bg);
this.setBackgroundColor(this.bgColor);
this.bgIframe=new dojo.html.BackgroundIframe();
if(this.bgIframe.iframe){
with(this.bgIframe.iframe.style){
position="absolute";
left=top="0px";
zIndex=90;
display="none";
}
}
if(this.closeOnBackgroundClick){
dojo.event.kwConnect({srcObj:this.bg,srcFunc:"onclick",adviceObj:this,adviceFunc:"onBackgroundClick",once:true});
}
},uninitialize:function(){
this.bgIframe.remove();
dojo.html.removeNode(this.bg,true);
},setBackgroundColor:function(_a31){
if(arguments.length>=3){
_a31=new dojo.gfx.color.Color(arguments[0],arguments[1],arguments[2]);
}else{
_a31=new dojo.gfx.color.Color(_a31);
}
this.bg.style.backgroundColor=_a31.toString();
return this.bgColor=_a31;
},setBackgroundOpacity:function(op){
if(arguments.length==0){
op=this.bgOpacity;
}
dojo.html.setOpacity(this.bg,op);
try{
this.bgOpacity=dojo.html.getOpacity(this.bg);
}
catch(e){
this.bgOpacity=op;
}
return this.bgOpacity;
},_sizeBackground:function(){
if(this.bgOpacity>0){
var _a33=dojo.html.getViewport();
var h=_a33.height;
var w=_a33.width;
with(this.bg.style){
width=w+"px";
height=h+"px";
}
var _a36=dojo.html.getScroll().offset;
this.bg.style.top=_a36.y+"px";
this.bg.style.left=_a36.x+"px";
var _a33=dojo.html.getViewport();
if(_a33.width!=w){
this.bg.style.width=_a33.width+"px";
}
if(_a33.height!=h){
this.bg.style.height=_a33.height+"px";
}
}
this.bgIframe.size(this.bg);
},_showBackground:function(){
if(this.bgOpacity>0){
this.bg.style.display="block";
}
if(this.bgIframe.iframe){
this.bgIframe.iframe.style.display="block";
}
},placeModalDialog:function(){
var _a37=dojo.html.getScroll().offset;
var _a38=dojo.html.getViewport();
var mb;
if(this.isShowing()){
mb=dojo.html.getMarginBox(this.domNode);
}else{
dojo.html.setVisibility(this.domNode,false);
dojo.html.show(this.domNode);
mb=dojo.html.getMarginBox(this.domNode);
dojo.html.hide(this.domNode);
dojo.html.setVisibility(this.domNode,true);
}
var x=_a37.x+(_a38.width-mb.width)/2;
var y=_a37.y+(_a38.height-mb.height)/2;
with(this.domNode.style){
left=x+"px";
top=y+"px";
}
},_onKey:function(evt){
if(evt.key){
var node=evt.target;
while(node!=null){
if(node==this.domNode){
return;
}
node=node.parentNode;
}
if(evt.key!=evt.KEY_TAB){
dojo.event.browser.stopEvent(evt);
}else{
if(!dojo.render.html.opera){
try{
this.tabStart.focus();
}
catch(e){
}
}
}
}
},showModalDialog:function(){
if(this.followScroll&&!this._scrollConnected){
this._scrollConnected=true;
dojo.event.connect(window,"onscroll",this,"_onScroll");
}
dojo.event.connect(document.documentElement,"onkey",this,"_onKey");
this.placeModalDialog();
this.setBackgroundOpacity();
this._sizeBackground();
this._showBackground();
this._fromTrap=true;
setTimeout(dojo.lang.hitch(this,function(){
try{
this.tabStart.focus();
}
catch(e){
}
}),50);
},hideModalDialog:function(){
if(this.focusElement){
dojo.byId(this.focusElement).focus();
dojo.byId(this.focusElement).blur();
}
this.bg.style.display="none";
this.bg.style.width=this.bg.style.height="1px";
if(this.bgIframe.iframe){
this.bgIframe.iframe.style.display="none";
}
dojo.event.disconnect(document.documentElement,"onkey",this,"_onKey");
if(this._scrollConnected){
this._scrollConnected=false;
dojo.event.disconnect(window,"onscroll",this,"_onScroll");
}
},_onScroll:function(){
var _a3e=dojo.html.getScroll().offset;
this.bg.style.top=_a3e.y+"px";
this.bg.style.left=_a3e.x+"px";
this.placeModalDialog();
},checkSize:function(){
if(this.isShowing()){
this._sizeBackground();
this.placeModalDialog();
this.onResized();
}
},onBackgroundClick:function(){
if(this.lifetime-this.timeRemaining>=this.blockDuration){
return;
}
this.hide();
}});
dojo.widget.defineWidget("dojo.widget.Dialog",[dojo.widget.ContentPane,dojo.widget.ModalDialogBase],{templatePath:dojo.uri.dojoUri("src/widget/templates/Dialog.html"),blockDuration:0,lifetime:0,closeNode:"",postMixInProperties:function(){
dojo.widget.Dialog.superclass.postMixInProperties.apply(this,arguments);
if(this.closeNode){
this.setCloseControl(this.closeNode);
}
},postCreate:function(){
dojo.widget.Dialog.superclass.postCreate.apply(this,arguments);
dojo.widget.ModalDialogBase.prototype.postCreate.apply(this,arguments);
},show:function(){
if(this.lifetime){
this.timeRemaining=this.lifetime;
if(this.timerNode){
this.timerNode.innerHTML=Math.ceil(this.timeRemaining/1000);
}
if(this.blockDuration&&this.closeNode){
if(this.lifetime>this.blockDuration){
this.closeNode.style.visibility="hidden";
}else{
this.closeNode.style.display="none";
}
}
if(this.timer){
clearInterval(this.timer);
}
this.timer=setInterval(dojo.lang.hitch(this,"_onTick"),100);
}
this.showModalDialog();
dojo.widget.Dialog.superclass.show.call(this);
},onLoad:function(){
this.placeModalDialog();
dojo.widget.Dialog.superclass.onLoad.call(this);
},fillInTemplate:function(){
},hide:function(){
this.hideModalDialog();
dojo.widget.Dialog.superclass.hide.call(this);
if(this.timer){
clearInterval(this.timer);
}
},setTimerNode:function(node){
this.timerNode=node;
},setCloseControl:function(node){
this.closeNode=dojo.byId(node);
dojo.event.connect(this.closeNode,"onclick",this,"hide");
},setShowControl:function(node){
node=dojo.byId(node);
dojo.event.connect(node,"onclick",this,"show");
},_onTick:function(){
if(this.timer){
this.timeRemaining-=100;
if(this.lifetime-this.timeRemaining>=this.blockDuration){
if(this.closeNode){
this.closeNode.style.visibility="visible";
}
}
if(!this.timeRemaining){
clearInterval(this.timer);
this.hide();
}else{
if(this.timerNode){
this.timerNode.innerHTML=Math.ceil(this.timeRemaining/1000);
}
}
}
}});
dojo.provide("dojo.widget.ResizeHandle");
dojo.widget.defineWidget("dojo.widget.ResizeHandle",dojo.widget.HtmlWidget,{targetElmId:"",templateCssPath:dojo.uri.dojoUri("src/widget/templates/ResizeHandle.css"),templateString:"<div class=\"dojoHtmlResizeHandle\"><div></div></div>",postCreate:function(){
dojo.event.connect(this.domNode,"onmousedown",this,"_beginSizing");
},_beginSizing:function(e){
if(this._isSizing){
return false;
}
this.targetWidget=dojo.widget.byId(this.targetElmId);
this.targetDomNode=this.targetWidget?this.targetWidget.domNode:dojo.byId(this.targetElmId);
if(!this.targetDomNode){
return;
}
this._isSizing=true;
this.startPoint={"x":e.clientX,"y":e.clientY};
var mb=dojo.html.getMarginBox(this.targetDomNode);
this.startSize={"w":mb.width,"h":mb.height};
dojo.event.kwConnect({srcObj:dojo.body(),srcFunc:"onmousemove",targetObj:this,targetFunc:"_changeSizing",rate:25});
dojo.event.connect(dojo.body(),"onmouseup",this,"_endSizing");
e.preventDefault();
},_changeSizing:function(e){
try{
if(!e.clientX||!e.clientY){
return;
}
}
catch(e){
return;
}
var dx=this.startPoint.x-e.clientX;
var dy=this.startPoint.y-e.clientY;
var newW=this.startSize.w-dx;
var newH=this.startSize.h-dy;
if(this.minSize){
var mb=dojo.html.getMarginBox(this.targetDomNode);
if(newW<this.minSize.w){
newW=mb.width;
}
if(newH<this.minSize.h){
newH=mb.height;
}
}
if(this.targetWidget){
this.targetWidget.resizeTo(newW,newH);
}else{
dojo.html.setMarginBox(this.targetDomNode,{width:newW,height:newH});
}
e.preventDefault();
},_endSizing:function(e){
dojo.event.disconnect(dojo.body(),"onmousemove",this,"_changeSizing");
dojo.event.disconnect(dojo.body(),"onmouseup",this,"_endSizing");
this._isSizing=false;
}});
dojo.provide("dojo.widget.FloatingPane");
dojo.declare("dojo.widget.FloatingPaneBase",null,{title:"",iconSrc:"",hasShadow:false,constrainToContainer:false,taskBarId:"",resizable:true,titleBarDisplay:true,windowState:"normal",displayCloseAction:false,displayMinimizeAction:false,displayMaximizeAction:false,_max_taskBarConnectAttempts:5,_taskBarConnectAttempts:0,templatePath:dojo.uri.dojoUri("src/widget/templates/FloatingPane.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/FloatingPane.css"),fillInFloatingPaneTemplate:function(args,frag){
var _a4d=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_a4d);
dojo.body().appendChild(this.domNode);
if(!this.isShowing()){
this.windowState="minimized";
}
if(this.iconSrc==""){
dojo.html.removeNode(this.titleBarIcon);
}else{
this.titleBarIcon.src=this.iconSrc.toString();
}
if(this.titleBarDisplay){
this.titleBar.style.display="";
dojo.html.disableSelection(this.titleBar);
this.titleBarIcon.style.display=(this.iconSrc==""?"none":"");
this.minimizeAction.style.display=(this.displayMinimizeAction?"":"none");
this.maximizeAction.style.display=(this.displayMaximizeAction&&this.windowState!="maximized"?"":"none");
this.restoreAction.style.display=(this.displayMaximizeAction&&this.windowState=="maximized"?"":"none");
this.closeAction.style.display=(this.displayCloseAction?"":"none");
this.drag=new dojo.dnd.HtmlDragMoveSource(this.domNode);
if(this.constrainToContainer){
this.drag.constrainTo();
}
this.drag.setDragHandle(this.titleBar);
var self=this;
dojo.event.topic.subscribe("dragMove",function(info){
if(info.source.domNode==self.domNode){
dojo.event.topic.publish("floatingPaneMove",{source:self});
}
});
}
if(this.resizable){
this.resizeBar.style.display="";
this.resizeHandle=dojo.widget.createWidget("ResizeHandle",{targetElmId:this.widgetId,id:this.widgetId+"_resize"});
this.resizeBar.appendChild(this.resizeHandle.domNode);
}
if(this.hasShadow){
this.shadow=new dojo.lfx.shadow(this.domNode);
}
this.bgIframe=new dojo.html.BackgroundIframe(this.domNode);
if(this.taskBarId){
this._taskBarSetup();
}
dojo.body().removeChild(this.domNode);
},postCreate:function(){
if(dojo.hostenv.post_load_){
this._setInitialWindowState();
}else{
dojo.addOnLoad(this,"_setInitialWindowState");
}
},maximizeWindow:function(evt){
var mb=dojo.html.getMarginBox(this.domNode);
this.previous={width:mb.width||this.width,height:mb.height||this.height,left:this.domNode.style.left,top:this.domNode.style.top,bottom:this.domNode.style.bottom,right:this.domNode.style.right};
if(this.domNode.parentNode.style.overflow.toLowerCase()!="hidden"){
this.parentPrevious={overflow:this.domNode.parentNode.style.overflow};
dojo.debug(this.domNode.parentNode.style.overflow);
this.domNode.parentNode.style.overflow="hidden";
}
this.domNode.style.left=dojo.html.getPixelValue(this.domNode.parentNode,"padding-left",true)+"px";
this.domNode.style.top=dojo.html.getPixelValue(this.domNode.parentNode,"padding-top",true)+"px";
if((this.domNode.parentNode.nodeName.toLowerCase()=="body")){
var _a52=dojo.html.getViewport();
var _a53=dojo.html.getPadding(dojo.body());
this.resizeTo(_a52.width-_a53.width,_a52.height-_a53.height);
}else{
var _a54=dojo.html.getContentBox(this.domNode.parentNode);
this.resizeTo(_a54.width,_a54.height);
}
this.maximizeAction.style.display="none";
this.restoreAction.style.display="";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="none";
}
this.drag.setDragHandle(null);
this.windowState="maximized";
},minimizeWindow:function(evt){
this.hide();
for(var attr in this.parentPrevious){
this.domNode.parentNode.style[attr]=this.parentPrevious[attr];
}
this.lastWindowState=this.windowState;
this.windowState="minimized";
},restoreWindow:function(evt){
if(this.windowState=="minimized"){
this.show();
if(this.lastWindowState=="maximized"){
this.domNode.parentNode.style.overflow="hidden";
this.windowState="maximized";
}else{
this.windowState="normal";
}
}else{
if(this.windowState=="maximized"){
for(var attr in this.previous){
this.domNode.style[attr]=this.previous[attr];
}
for(var attr in this.parentPrevious){
this.domNode.parentNode.style[attr]=this.parentPrevious[attr];
}
this.resizeTo(this.previous.width,this.previous.height);
this.previous=null;
this.parentPrevious=null;
this.restoreAction.style.display="none";
this.maximizeAction.style.display=this.displayMaximizeAction?"":"none";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="";
}
this.drag.setDragHandle(this.titleBar);
this.windowState="normal";
}else{
}
}
},toggleDisplay:function(){
if(this.windowState=="minimized"){
this.restoreWindow();
}else{
this.minimizeWindow();
}
},closeWindow:function(evt){
dojo.html.removeNode(this.domNode);
this.destroy();
},onMouseDown:function(evt){
this.bringToTop();
},bringToTop:function(){
var _a5b=dojo.widget.manager.getWidgetsByType(this.widgetType);
var _a5c=[];
for(var x=0;x<_a5b.length;x++){
if(this.widgetId!=_a5b[x].widgetId){
_a5c.push(_a5b[x]);
}
}
_a5c.sort(function(a,b){
return a.domNode.style.zIndex-b.domNode.style.zIndex;
});
_a5c.push(this);
var _a60=100;
for(x=0;x<_a5c.length;x++){
_a5c[x].domNode.style.zIndex=_a60+x*2;
}
},_setInitialWindowState:function(){
if(this.isShowing()){
this.width=-1;
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
}
if(this.windowState=="maximized"){
this.maximizeWindow();
this.show();
return;
}
if(this.windowState=="normal"){
this.show();
return;
}
if(this.windowState=="minimized"){
this.hide();
return;
}
this.windowState="minimized";
},_taskBarSetup:function(){
var _a62=dojo.widget.getWidgetById(this.taskBarId);
if(!_a62){
if(this._taskBarConnectAttempts<this._max_taskBarConnectAttempts){
dojo.lang.setTimeout(this,this._taskBarSetup,50);
this._taskBarConnectAttempts++;
}else{
dojo.debug("Unable to connect to the taskBar");
}
return;
}
_a62.addChild(this);
},showFloatingPane:function(){
this.bringToTop();
},onFloatingPaneShow:function(){
var mb=dojo.html.getMarginBox(this.domNode);
this.resizeTo(mb.width,mb.height);
},resizeTo:function(_a64,_a65){
dojo.html.setMarginBox(this.domNode,{width:_a64,height:_a65});
dojo.widget.html.layout(this.domNode,[{domNode:this.titleBar,layoutAlign:"top"},{domNode:this.resizeBar,layoutAlign:"bottom"},{domNode:this.containerNode,layoutAlign:"client"}]);
dojo.widget.html.layout(this.containerNode,this.children,"top-bottom");
this.bgIframe.onResized();
if(this.shadow){
this.shadow.size(_a64,_a65);
}
this.onResized();
},checkSize:function(){
},destroyFloatingPane:function(){
if(this.resizeHandle){
this.resizeHandle.destroy();
this.resizeHandle=null;
}
}});
dojo.widget.defineWidget("dojo.widget.FloatingPane",[dojo.widget.ContentPane,dojo.widget.FloatingPaneBase],{fillInTemplate:function(args,frag){
this.fillInFloatingPaneTemplate(args,frag);
dojo.widget.FloatingPane.superclass.fillInTemplate.call(this,args,frag);
},postCreate:function(){
dojo.widget.FloatingPaneBase.prototype.postCreate.apply(this,arguments);
dojo.widget.FloatingPane.superclass.postCreate.apply(this,arguments);
},show:function(){
dojo.widget.FloatingPane.superclass.show.apply(this,arguments);
this.showFloatingPane();
},onShow:function(){
dojo.widget.FloatingPane.superclass.onShow.call(this);
this.onFloatingPaneShow();
},destroy:function(){
this.destroyFloatingPane();
dojo.widget.FloatingPane.superclass.destroy.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.ModalFloatingPane",[dojo.widget.FloatingPane,dojo.widget.ModalDialogBase],{windowState:"minimized",displayCloseAction:true,postCreate:function(){
dojo.widget.ModalDialogBase.prototype.postCreate.call(this);
dojo.widget.ModalFloatingPane.superclass.postCreate.call(this);
},show:function(){
this.showModalDialog();
dojo.widget.ModalFloatingPane.superclass.show.apply(this,arguments);
this.bg.style.zIndex=this.domNode.style.zIndex-1;
},hide:function(){
this.hideModalDialog();
dojo.widget.ModalFloatingPane.superclass.hide.apply(this,arguments);
},closeWindow:function(){
this.hide();
dojo.widget.ModalFloatingPane.superclass.closeWindow.apply(this,arguments);
}});
dojo.provide("dojo.widget.Textbox");
dojo.widget.defineWidget("dojo.widget.Textbox",dojo.widget.HtmlWidget,{className:"",name:"",value:"",type:"",trim:false,uppercase:false,lowercase:false,ucFirst:false,digit:false,htmlfloat:"none",templatePath:dojo.uri.dojoUri("src/widget/templates/Textbox.html"),textbox:null,fillInTemplate:function(){
this.textbox.value=this.value;
},filter:function(){
if(this.trim){
this.textbox.value=this.textbox.value.replace(/(^\s*|\s*$)/g,"");
}
if(this.uppercase){
this.textbox.value=this.textbox.value.toUpperCase();
}
if(this.lowercase){
this.textbox.value=this.textbox.value.toLowerCase();
}
if(this.ucFirst){
this.textbox.value=this.textbox.value.replace(/\b\w+\b/g,function(word){
return word.substring(0,1).toUpperCase()+word.substring(1).toLowerCase();
});
}
if(this.digit){
this.textbox.value=this.textbox.value.replace(/\D/g,"");
}
},onfocus:function(){
},onblur:function(){
this.filter();
},mixInProperties:function(_a69,frag){
dojo.widget.Textbox.superclass.mixInProperties.apply(this,arguments);
if(_a69["class"]){
this.className=_a69["class"];
}
}});
dojo.provide("dojo.widget.ValidationTextbox");
dojo.widget.defineWidget("dojo.widget.ValidationTextbox",dojo.widget.Textbox,function(){
this.flags={};
},{required:false,rangeClass:"range",invalidClass:"invalid",missingClass:"missing",classPrefix:"dojoValidate",size:"",maxlength:"",promptMessage:"",invalidMessage:"",missingMessage:"",rangeMessage:"",listenOnKeyPress:true,htmlfloat:"none",lastCheckedValue:null,templatePath:dojo.uri.dojoUri("src/widget/templates/ValidationTextbox.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/Validate.css"),invalidSpan:null,missingSpan:null,rangeSpan:null,getValue:function(){
return this.textbox.value;
},setValue:function(_a6b){
this.textbox.value=_a6b;
this.update();
},isValid:function(){
return true;
},isInRange:function(){
return true;
},isEmpty:function(){
return (/^\s*$/.test(this.textbox.value));
},isMissing:function(){
return (this.required&&this.isEmpty());
},update:function(){
this.lastCheckedValue=this.textbox.value;
this.missingSpan.style.display="none";
this.invalidSpan.style.display="none";
this.rangeSpan.style.display="none";
var _a6c=this.isEmpty();
var _a6d=true;
if(this.promptMessage!=this.textbox.value){
_a6d=this.isValid();
}
var _a6e=this.isMissing();
if(_a6e){
this.missingSpan.style.display="";
}else{
if(!_a6c&&!_a6d){
this.invalidSpan.style.display="";
}else{
if(!_a6c&&!this.isInRange()){
this.rangeSpan.style.display="";
}
}
}
this.highlight();
},updateClass:function(_a6f){
var pre=this.classPrefix;
dojo.html.removeClass(this.textbox,pre+"Empty");
dojo.html.removeClass(this.textbox,pre+"Valid");
dojo.html.removeClass(this.textbox,pre+"Invalid");
dojo.html.addClass(this.textbox,pre+_a6f);
},highlight:function(){
if(this.isEmpty()){
this.updateClass("Empty");
}else{
if(this.isValid()&&this.isInRange()){
this.updateClass("Valid");
}else{
if(this.textbox.value!=this.promptMessage){
this.updateClass("Invalid");
}else{
this.updateClass("Empty");
}
}
}
},onfocus:function(evt){
if(!this.listenOnKeyPress){
this.updateClass("Empty");
}
},onblur:function(evt){
this.filter();
this.update();
},onkeyup:function(evt){
if(this.listenOnKeyPress){
this.update();
}else{
if(this.textbox.value!=this.lastCheckedValue){
this.updateClass("Empty");
}
}
},postMixInProperties:function(_a74,frag){
dojo.widget.ValidationTextbox.superclass.postMixInProperties.apply(this,arguments);
this.messages=dojo.i18n.getLocalization("dojo.widget","validate",this.lang);
dojo.lang.forEach(["invalidMessage","missingMessage","rangeMessage"],function(prop){
if(this[prop]){
this.messages[prop]=this[prop];
}
},this);
},fillInTemplate:function(){
dojo.widget.ValidationTextbox.superclass.fillInTemplate.apply(this,arguments);
this.textbox.isValid=function(){
this.isValid.call(this);
};
this.textbox.isMissing=function(){
this.isMissing.call(this);
};
this.textbox.isInRange=function(){
this.isInRange.call(this);
};
dojo.html.setClass(this.invalidSpan,this.invalidClass);
this.update();
this.filter();
if(dojo.render.html.ie){
dojo.html.addClass(this.domNode,"ie");
}
if(dojo.render.html.moz){
dojo.html.addClass(this.domNode,"moz");
}
if(dojo.render.html.opera){
dojo.html.addClass(this.domNode,"opera");
}
if(dojo.render.html.safari){
dojo.html.addClass(this.domNode,"safari");
}
}});
dojo.provide("dojo.regexp");
dojo.evalObjPath("dojo.regexp.us",true);
dojo.regexp.tld=function(_a77){
_a77=(typeof _a77=="object")?_a77:{};
if(typeof _a77.allowCC!="boolean"){
_a77.allowCC=true;
}
if(typeof _a77.allowInfra!="boolean"){
_a77.allowInfra=true;
}
if(typeof _a77.allowGeneric!="boolean"){
_a77.allowGeneric=true;
}
var _a78="arpa";
var _a79="aero|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|xxx|jobs|mobi|post";
var ccRE="ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|"+"bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|"+"ec|ee|eg|er|eu|es|et|fi|fj|fk|fm|fo|fr|ga|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|"+"gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kr|kw|ky|kz|"+"la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|"+"my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|"+"re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sk|sl|sm|sn|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|"+"tn|to|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw";
var a=[];
if(_a77.allowInfra){
a.push(_a78);
}
if(_a77.allowGeneric){
a.push(_a79);
}
if(_a77.allowCC){
a.push(ccRE);
}
var _a7c="";
if(a.length>0){
_a7c="("+a.join("|")+")";
}
return _a7c;
};
dojo.regexp.ipAddress=function(_a7d){
_a7d=(typeof _a7d=="object")?_a7d:{};
if(typeof _a7d.allowDottedDecimal!="boolean"){
_a7d.allowDottedDecimal=true;
}
if(typeof _a7d.allowDottedHex!="boolean"){
_a7d.allowDottedHex=true;
}
if(typeof _a7d.allowDottedOctal!="boolean"){
_a7d.allowDottedOctal=true;
}
if(typeof _a7d.allowDecimal!="boolean"){
_a7d.allowDecimal=true;
}
if(typeof _a7d.allowHex!="boolean"){
_a7d.allowHex=true;
}
if(typeof _a7d.allowIPv6!="boolean"){
_a7d.allowIPv6=true;
}
if(typeof _a7d.allowHybrid!="boolean"){
_a7d.allowHybrid=true;
}
var _a7e="((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])";
var _a7f="(0[xX]0*[\\da-fA-F]?[\\da-fA-F]\\.){3}0[xX]0*[\\da-fA-F]?[\\da-fA-F]";
var _a80="(0+[0-3][0-7][0-7]\\.){3}0+[0-3][0-7][0-7]";
var _a81="(0|[1-9]\\d{0,8}|[1-3]\\d{9}|4[01]\\d{8}|42[0-8]\\d{7}|429[0-3]\\d{6}|"+"4294[0-8]\\d{5}|42949[0-5]\\d{4}|429496[0-6]\\d{3}|4294967[01]\\d{2}|42949672[0-8]\\d|429496729[0-5])";
var _a82="0[xX]0*[\\da-fA-F]{1,8}";
var _a83="([\\da-fA-F]{1,4}\\:){7}[\\da-fA-F]{1,4}";
var _a84="([\\da-fA-F]{1,4}\\:){6}"+"((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])";
var a=[];
if(_a7d.allowDottedDecimal){
a.push(_a7e);
}
if(_a7d.allowDottedHex){
a.push(_a7f);
}
if(_a7d.allowDottedOctal){
a.push(_a80);
}
if(_a7d.allowDecimal){
a.push(_a81);
}
if(_a7d.allowHex){
a.push(_a82);
}
if(_a7d.allowIPv6){
a.push(_a83);
}
if(_a7d.allowHybrid){
a.push(_a84);
}
var _a86="";
if(a.length>0){
_a86="("+a.join("|")+")";
}
return _a86;
};
dojo.regexp.host=function(_a87){
_a87=(typeof _a87=="object")?_a87:{};
if(typeof _a87.allowIP!="boolean"){
_a87.allowIP=true;
}
if(typeof _a87.allowLocal!="boolean"){
_a87.allowLocal=false;
}
if(typeof _a87.allowPort!="boolean"){
_a87.allowPort=true;
}
var _a88="([0-9a-zA-Z]([-0-9a-zA-Z]{0,61}[0-9a-zA-Z])?\\.)+"+dojo.regexp.tld(_a87);
var _a89=(_a87.allowPort)?"(\\:"+dojo.regexp.integer({signed:false})+")?":"";
var _a8a=_a88;
if(_a87.allowIP){
_a8a+="|"+dojo.regexp.ipAddress(_a87);
}
if(_a87.allowLocal){
_a8a+="|localhost";
}
return "("+_a8a+")"+_a89;
};
dojo.regexp.url=function(_a8b){
_a8b=(typeof _a8b=="object")?_a8b:{};
if(typeof _a8b.scheme=="undefined"){
_a8b.scheme=[true,false];
}
var _a8c=dojo.regexp.buildGroupRE(_a8b.scheme,function(q){
if(q){
return "(https?|ftps?)\\://";
}
return "";
});
var _a8e="(/([^?#\\s/]+/)*)?([^?#\\s/]+(\\?[^?#\\s/]*)?(#[A-Za-z][\\w.:-]*)?)?";
return _a8c+dojo.regexp.host(_a8b)+_a8e;
};
dojo.regexp.emailAddress=function(_a8f){
_a8f=(typeof _a8f=="object")?_a8f:{};
if(typeof _a8f.allowCruft!="boolean"){
_a8f.allowCruft=false;
}
_a8f.allowPort=false;
var _a90="([\\da-z]+[-._+&'])*[\\da-z]+";
var _a91=_a90+"@"+dojo.regexp.host(_a8f);
if(_a8f.allowCruft){
_a91="<?(mailto\\:)?"+_a91+">?";
}
return _a91;
};
dojo.regexp.emailAddressList=function(_a92){
_a92=(typeof _a92=="object")?_a92:{};
if(typeof _a92.listSeparator!="string"){
_a92.listSeparator="\\s;,";
}
var _a93=dojo.regexp.emailAddress(_a92);
var _a94="("+_a93+"\\s*["+_a92.listSeparator+"]\\s*)*"+_a93+"\\s*["+_a92.listSeparator+"]?\\s*";
return _a94;
};
dojo.regexp.integer=function(_a95){
_a95=(typeof _a95=="object")?_a95:{};
if(typeof _a95.signed=="undefined"){
_a95.signed=[true,false];
}
if(typeof _a95.separator=="undefined"){
_a95.separator="";
}else{
if(typeof _a95.groupSize=="undefined"){
_a95.groupSize=3;
}
}
var _a96=dojo.regexp.buildGroupRE(_a95.signed,function(q){
return q?"[-+]":"";
});
var _a98=dojo.regexp.buildGroupRE(_a95.separator,function(sep){
if(sep==""){
return "(0|[1-9]\\d*)";
}
var grp=_a95.groupSize,grp2=_a95.groupSize2;
if(typeof grp2!="undefined"){
var _a9c="(0|[1-9]\\d{0,"+(grp2-1)+"}(["+sep+"]\\d{"+grp2+"})*["+sep+"]\\d{"+grp+"})";
return ((grp-grp2)>0)?"("+_a9c+"|(0|[1-9]\\d{0,"+(grp-1)+"}))":_a9c;
}
return "(0|[1-9]\\d{0,"+(grp-1)+"}(["+sep+"]\\d{"+grp+"})*)";
});
return _a96+_a98;
};
dojo.regexp.realNumber=function(_a9d){
_a9d=(typeof _a9d=="object")?_a9d:{};
if(typeof _a9d.places!="number"){
_a9d.places=Infinity;
}
if(typeof _a9d.decimal!="string"){
_a9d.decimal=".";
}
if(typeof _a9d.fractional=="undefined"){
_a9d.fractional=[true,false];
}
if(typeof _a9d.exponent=="undefined"){
_a9d.exponent=[true,false];
}
if(typeof _a9d.eSigned=="undefined"){
_a9d.eSigned=[true,false];
}
var _a9e=dojo.regexp.integer(_a9d);
var _a9f=dojo.regexp.buildGroupRE(_a9d.fractional,function(q){
var re="";
if(q&&(_a9d.places>0)){
re="\\"+_a9d.decimal;
if(_a9d.places==Infinity){
re="("+re+"\\d+)?";
}else{
re=re+"\\d{"+_a9d.places+"}";
}
}
return re;
});
var _aa2=dojo.regexp.buildGroupRE(_a9d.exponent,function(q){
if(q){
return "([eE]"+dojo.regexp.integer({signed:_a9d.eSigned})+")";
}
return "";
});
return _a9e+_a9f+_aa2;
};
dojo.regexp.currency=function(_aa4){
_aa4=(typeof _aa4=="object")?_aa4:{};
if(typeof _aa4.signed=="undefined"){
_aa4.signed=[true,false];
}
if(typeof _aa4.symbol=="undefined"){
_aa4.symbol="$";
}
if(typeof _aa4.placement!="string"){
_aa4.placement="before";
}
if(typeof _aa4.signPlacement!="string"){
_aa4.signPlacement="before";
}
if(typeof _aa4.separator=="undefined"){
_aa4.separator=",";
}
if(typeof _aa4.fractional=="undefined"&&typeof _aa4.cents!="undefined"){
dojo.deprecated("dojo.regexp.currency: flags.cents","use flags.fractional instead","0.5");
_aa4.fractional=_aa4.cents;
}
if(typeof _aa4.decimal!="string"){
_aa4.decimal=".";
}
var _aa5=dojo.regexp.buildGroupRE(_aa4.signed,function(q){
if(q){
return "[-+]";
}
return "";
});
var _aa7=dojo.regexp.buildGroupRE(_aa4.symbol,function(_aa8){
return "\\s?"+_aa8.replace(/([.$?*!=:|\\\/^])/g,"\\$1")+"\\s?";
});
switch(_aa4.signPlacement){
case "before":
_aa7=_aa5+_aa7;
break;
case "after":
_aa7=_aa7+_aa5;
break;
}
var _aa9=_aa4;
_aa9.signed=false;
_aa9.exponent=false;
var _aaa=dojo.regexp.realNumber(_aa9);
var _aab;
switch(_aa4.placement){
case "before":
_aab=_aa7+_aaa;
break;
case "after":
_aab=_aaa+_aa7;
break;
}
switch(_aa4.signPlacement){
case "around":
_aab="("+_aab+"|"+"\\("+_aab+"\\)"+")";
break;
case "begin":
_aab=_aa5+_aab;
break;
case "end":
_aab=_aab+_aa5;
break;
}
return _aab;
};
dojo.regexp.us.state=function(_aac){
_aac=(typeof _aac=="object")?_aac:{};
if(typeof _aac.allowTerritories!="boolean"){
_aac.allowTerritories=true;
}
if(typeof _aac.allowMilitary!="boolean"){
_aac.allowMilitary=true;
}
var _aad="AL|AK|AZ|AR|CA|CO|CT|DE|DC|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|"+"NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY";
var _aae="AS|FM|GU|MH|MP|PW|PR|VI";
var _aaf="AA|AE|AP";
if(_aac.allowTerritories){
_aad+="|"+_aae;
}
if(_aac.allowMilitary){
_aad+="|"+_aaf;
}
return "("+_aad+")";
};
dojo.regexp.time=function(_ab0){
dojo.deprecated("dojo.regexp.time","Use dojo.date.parse instead","0.5");
_ab0=(typeof _ab0=="object")?_ab0:{};
if(typeof _ab0.format=="undefined"){
_ab0.format="h:mm:ss t";
}
if(typeof _ab0.amSymbol!="string"){
_ab0.amSymbol="AM";
}
if(typeof _ab0.pmSymbol!="string"){
_ab0.pmSymbol="PM";
}
var _ab1=function(_ab2){
_ab2=_ab2.replace(/([.$?*!=:|{}\(\)\[\]\\\/^])/g,"\\$1");
var amRE=_ab0.amSymbol.replace(/([.$?*!=:|{}\(\)\[\]\\\/^])/g,"\\$1");
var pmRE=_ab0.pmSymbol.replace(/([.$?*!=:|{}\(\)\[\]\\\/^])/g,"\\$1");
_ab2=_ab2.replace("hh","(0[1-9]|1[0-2])");
_ab2=_ab2.replace("h","([1-9]|1[0-2])");
_ab2=_ab2.replace("HH","([01][0-9]|2[0-3])");
_ab2=_ab2.replace("H","([0-9]|1[0-9]|2[0-3])");
_ab2=_ab2.replace("mm","([0-5][0-9])");
_ab2=_ab2.replace("m","([1-5][0-9]|[0-9])");
_ab2=_ab2.replace("ss","([0-5][0-9])");
_ab2=_ab2.replace("s","([1-5][0-9]|[0-9])");
_ab2=_ab2.replace("t","\\s?("+amRE+"|"+pmRE+")\\s?");
return _ab2;
};
return dojo.regexp.buildGroupRE(_ab0.format,_ab1);
};
dojo.regexp.numberFormat=function(_ab5){
_ab5=(typeof _ab5=="object")?_ab5:{};
if(typeof _ab5.format=="undefined"){
_ab5.format="###-###-####";
}
var _ab6=function(_ab7){
_ab7=_ab7.replace(/([.$*!=:|{}\(\)\[\]\\\/^])/g,"\\$1");
_ab7=_ab7.replace(/\?/g,"\\d?");
_ab7=_ab7.replace(/#/g,"\\d");
return _ab7;
};
return dojo.regexp.buildGroupRE(_ab5.format,_ab6);
};
dojo.regexp.buildGroupRE=function(a,re){
if(!(a instanceof Array)){
return re(a);
}
var b=[];
for(var i=0;i<a.length;i++){
b.push(re(a[i]));
}
return "("+b.join("|")+")";
};
dojo.provide("dojo.validate.common");
dojo.validate.isText=function(_abc,_abd){
_abd=(typeof _abd=="object")?_abd:{};
if(/^\s*$/.test(_abc)){
return false;
}
if(typeof _abd.length=="number"&&_abd.length!=_abc.length){
return false;
}
if(typeof _abd.minlength=="number"&&_abd.minlength>_abc.length){
return false;
}
if(typeof _abd.maxlength=="number"&&_abd.maxlength<_abc.length){
return false;
}
return true;
};
dojo.validate.isInteger=function(_abe,_abf){
var re=new RegExp("^"+dojo.regexp.integer(_abf)+"$");
return re.test(_abe);
};
dojo.validate.isRealNumber=function(_ac1,_ac2){
var re=new RegExp("^"+dojo.regexp.realNumber(_ac2)+"$");
return re.test(_ac1);
};
dojo.validate.isCurrency=function(_ac4,_ac5){
var re=new RegExp("^"+dojo.regexp.currency(_ac5)+"$");
return re.test(_ac4);
};
dojo.validate.isInRange=function(_ac7,_ac8){
_ac7=_ac7.replace(dojo.lang.has(_ac8,"separator")?_ac8.separator:",","","g").replace(dojo.lang.has(_ac8,"symbol")?_ac8.symbol:"$","");
if(isNaN(_ac7)){
return false;
}
_ac8=(typeof _ac8=="object")?_ac8:{};
var max=(typeof _ac8.max=="number")?_ac8.max:Infinity;
var min=(typeof _ac8.min=="number")?_ac8.min:-Infinity;
var dec=(typeof _ac8.decimal=="string")?_ac8.decimal:".";
var _acc="[^"+dec+"\\deE+-]";
_ac7=_ac7.replace(RegExp(_acc,"g"),"");
_ac7=_ac7.replace(/^([+-]?)(\D*)/,"$1");
_ac7=_ac7.replace(/(\D*)$/,"");
_acc="(\\d)["+dec+"](\\d)";
_ac7=_ac7.replace(RegExp(_acc,"g"),"$1.$2");
_ac7=Number(_ac7);
if(_ac7<min||_ac7>max){
return false;
}
return true;
};
dojo.validate.isNumberFormat=function(_acd,_ace){
var re=new RegExp("^"+dojo.regexp.numberFormat(_ace)+"$","i");
return re.test(_acd);
};
dojo.validate.isValidLuhn=function(_ad0){
var sum,_ad2,_ad3;
if(typeof _ad0!="string"){
_ad0=String(_ad0);
}
_ad0=_ad0.replace(/[- ]/g,"");
_ad2=_ad0.length%2;
sum=0;
for(var i=0;i<_ad0.length;i++){
_ad3=parseInt(_ad0.charAt(i));
if(i%2==_ad2){
_ad3*=2;
}
if(_ad3>9){
_ad3-=9;
}
sum+=_ad3;
}
return !(sum%10);
};
dojo.provide("dojo.widget.IntegerTextbox");
dojo.widget.defineWidget("dojo.widget.IntegerTextbox",dojo.widget.ValidationTextbox,{mixInProperties:function(_ad5,frag){
dojo.widget.IntegerTextbox.superclass.mixInProperties.apply(this,arguments);
if((_ad5.signed=="true")||(_ad5.signed=="always")){
this.flags.signed=true;
}else{
if((_ad5.signed=="false")||(_ad5.signed=="never")){
this.flags.signed=false;
this.flags.min=0;
}else{
this.flags.signed=[true,false];
}
}
if(_ad5.separator){
this.flags.separator=_ad5.separator;
}
if(_ad5.min){
this.flags.min=parseInt(_ad5.min);
}
if(_ad5.max){
this.flags.max=parseInt(_ad5.max);
}
},isValid:function(){
return dojo.validate.isInteger(this.textbox.value,this.flags);
},isInRange:function(){
return dojo.validate.isInRange(this.textbox.value,this.flags);
}});
dojo.provide("dojo.widget.RealNumberTextbox");
dojo.widget.defineWidget("dojo.widget.RealNumberTextbox",dojo.widget.IntegerTextbox,{mixInProperties:function(_ad7,frag){
dojo.widget.RealNumberTextbox.superclass.mixInProperties.apply(this,arguments);
if(_ad7.places){
this.flags.places=Number(_ad7.places);
}
if((_ad7.exponent=="true")||(_ad7.exponent=="always")){
this.flags.exponent=true;
}else{
if((_ad7.exponent=="false")||(_ad7.exponent=="never")){
this.flags.exponent=false;
}else{
this.flags.exponent=[true,false];
}
}
if((_ad7.esigned=="true")||(_ad7.esigned=="always")){
this.flags.eSigned=true;
}else{
if((_ad7.esigned=="false")||(_ad7.esigned=="never")){
this.flags.eSigned=false;
}else{
this.flags.eSigned=[true,false];
}
}
if(_ad7.min){
this.flags.min=parseFloat(_ad7.min);
}
if(_ad7.max){
this.flags.max=parseFloat(_ad7.max);
}
},isValid:function(){
return dojo.validate.isRealNumber(this.textbox.value,this.flags);
},isInRange:function(){
return dojo.validate.isInRange(this.textbox.value,this.flags);
}});
dojo.provide("dojo.widget.Checkbox");
dojo.widget.defineWidget("dojo.widget.Checkbox",dojo.widget.HtmlWidget,{templatePath:dojo.uri.dojoUri("src/widget/templates/Checkbox.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/Checkbox.css"),name:"",id:"",checked:false,tabIndex:"",value:"on",postMixInProperties:function(){
dojo.widget.Checkbox.superclass.postMixInProperties.apply(this,arguments);
if(!this.disabled&&this.tabIndex==""){
this.tabIndex="0";
}
},fillInTemplate:function(){
this._setInfo();
},postCreate:function(){
var _ad9=true;
this.id=this.id!=""?this.id:this.widgetId;
if(this.id!=""){
var _ada=document.getElementsByTagName("label");
if(_ada!=null&&_ada.length>0){
for(var i=0;i<_ada.length;i++){
if(_ada[i].htmlFor==this.id){
_ada[i].id=(_ada[i].htmlFor+"label");
this._connectEvents(_ada[i]);
dojo.widget.wai.setAttr(this.domNode,"waiState","labelledby",_ada[i].id);
break;
}
}
}
}
this._connectEvents(this.domNode);
this.inputNode.checked=this.checked;
},_connectEvents:function(node){
dojo.event.connect(node,"onmouseover",this,"mouseOver");
dojo.event.connect(node,"onmouseout",this,"mouseOut");
dojo.event.connect(node,"onkey",this,"onKey");
dojo.event.connect(node,"onclick",this,"_onClick");
dojo.html.disableSelection(node);
},_onClick:function(e){
if(this.disabled==false){
this.checked=!this.checked;
this._setInfo();
}
e.preventDefault();
e.stopPropagation();
this.onClick();
},setValue:function(bool){
if(this.disabled==false){
this.checked=bool;
this._setInfo();
}
},onClick:function(){
},onKey:function(e){
var k=dojo.event.browser.keys;
if(e.key==" "){
this._onClick(e);
}
},mouseOver:function(e){
this._hover(e,true);
},mouseOut:function(e){
this._hover(e,false);
},_hover:function(e,_ae4){
if(this.disabled==false){
var _ae5=this.checked?"On":"Off";
var _ae6="dojoHtmlCheckbox"+_ae5+"Hover";
if(_ae4){
dojo.html.addClass(this.imageNode,_ae6);
}else{
dojo.html.removeClass(this.imageNode,_ae6);
}
}
},_setInfo:function(){
var _ae7="dojoHtmlCheckbox"+(this.disabled?"Disabled":"")+(this.checked?"On":"Off");
dojo.html.setClass(this.imageNode,"dojoHtmlCheckbox "+_ae7);
this.inputNode.checked=this.checked;
if(this.disabled){
this.inputNode.setAttribute("disabled",true);
}else{
this.inputNode.removeAttribute("disabled");
}
dojo.widget.wai.setAttr(this.domNode,"waiState","checked",this.checked);
}});
dojo.widget.defineWidget("dojo.widget.a11y.Checkbox",dojo.widget.Checkbox,{templatePath:dojo.uri.dojoUri("src/widget/templates/CheckboxA11y.html"),fillInTemplate:function(){
},postCreate:function(args,frag){
this.inputNode.checked=this.checked;
if(this.disabled){
this.inputNode.setAttribute("disabled",true);
}
},_onClick:function(){
this.onClick();
}});
dojo.provide("dojo.widget.TreeWithNode");
dojo.declare("dojo.widget.TreeWithNode",null,function(){
},{loadStates:{UNCHECKED:"UNCHECKED",LOADING:"LOADING",LOADED:"LOADED"},state:"UNCHECKED",objectId:"",isContainer:true,lockLevel:0,lock:function(){
this.lockLevel++;
},unlock:function(){
if(!this.lockLevel){
dojo.raise(this.widgetType+" unlock: not locked");
}
this.lockLevel--;
},expandLevel:0,loadLevel:0,hasLock:function(){
return this.lockLevel>0;
},isLocked:function(){
var node=this;
while(true){
if(node.lockLevel){
return true;
}
if(!node.parent||node.isTree){
break;
}
node=node.parent;
}
return false;
},flushLock:function(){
this.lockLevel=0;
},actionIsDisabled:function(_aeb){
var _aec=false;
if(dojo.lang.inArray(this.actionsDisabled,_aeb)){
_aec=true;
}
if(this.isTreeNode){
if(!this.tree.allowAddChildToLeaf&&_aeb==this.actions.ADDCHILD&&!this.isFolder){
_aec=true;
}
}
return _aec;
},actionIsDisabledNow:function(_aed){
return this.actionIsDisabled(_aed)||this.isLocked();
},setChildren:function(_aee){
if(this.isTreeNode&&!this.isFolder){
this.setFolder();
}else{
if(this.isTreeNode){
this.state=this.loadStates.LOADED;
}
}
var _aef=this.children.length>0;
if(_aef&&_aee){
this.destroyChildren();
}
if(_aee){
this.children=_aee;
}
var _af0=this.children.length>0;
if(this.isTreeNode&&_af0!=_aef){
this.viewSetHasChildren();
}
for(var i=0;i<this.children.length;i++){
var _af2=this.children[i];
if(!(_af2 instanceof dojo.widget.Widget)){
_af2=this.children[i]=this.tree.createNode(_af2);
var _af3=true;
}else{
var _af3=false;
}
if(!_af2.parent){
_af2.parent=this;
if(this.tree!==_af2.tree){
_af2.updateTree(this.tree);
}
_af2.viewAddLayout();
this.containerNode.appendChild(_af2.domNode);
var _af4={child:_af2,index:i,parent:this,childWidgetCreated:_af3};
delete dojo.widget.manager.topWidgets[_af2.widgetId];
dojo.event.topic.publish(this.tree.eventNames.afterAddChild,_af4);
}
if(this.tree.eagerWidgetInstantiation){
dojo.lang.forEach(this.children,function(_af5){
_af5.setChildren();
});
}
}
},doAddChild:function(_af6,_af7){
return this.addChild(_af6,_af7,true);
},addChild:function(_af8,_af9,_afa){
if(dojo.lang.isUndefined(_af9)){
_af9=this.children.length;
}
if(!_af8.isTreeNode){
dojo.raise("You can only add TreeNode widgets to a "+this.widgetType+" widget!");
return;
}
this.children.splice(_af9,0,_af8);
_af8.parent=this;
_af8.addedTo(this,_af9,_afa);
delete dojo.widget.manager.topWidgets[_af8.widgetId];
},onShow:function(){
this.animationInProgress=false;
},onHide:function(){
this.animationInProgress=false;
}});
dojo.provide("dojo.widget.TreeNodeV3");
dojo.widget.defineWidget("dojo.widget.TreeNodeV3",[dojo.widget.HtmlWidget,dojo.widget.TreeWithNode],function(){
this.actionsDisabled=[];
this.object={};
},{tryLazyInit:true,actions:{MOVE:"MOVE",DETACH:"DETACH",EDIT:"EDIT",ADDCHILD:"ADDCHILD",SELECT:"SELECT"},labelClass:"",contentClass:"",expandNode:null,labelNode:null,nodeDocType:"",selected:false,getnodeDocType:function(){
return this.nodeDocType;
},cloneProperties:["actionsDisabled","tryLazyInit","nodeDocType","objectId","object","title","isFolder","isExpanded","state"],clone:function(deep){
var ret=new this.constructor();
for(var i=0;i<this.cloneProperties.length;i++){
var prop=this.cloneProperties[i];
ret[prop]=dojo.lang.shallowCopy(this[prop],true);
}
if(this.tree.unsetFolderOnEmpty&&!deep&&this.isFolder){
ret.isFolder=false;
}
ret.toggleObj=this.toggleObj;
dojo.widget.manager.add(ret);
ret.tree=this.tree;
ret.buildRendering({},{});
ret.initialize({},{});
if(deep&&this.children.length){
for(var i=0;i<this.children.length;i++){
var _aff=this.children[i];
if(_aff.clone){
ret.children.push(_aff.clone(deep));
}else{
ret.children.push(dojo.lang.shallowCopy(_aff,deep));
}
}
ret.setChildren();
}
return ret;
},markProcessing:function(){
this.markProcessingSavedClass=dojo.html.getClass(this.expandNode);
dojo.html.setClass(this.expandNode,this.tree.classPrefix+"ExpandLoading");
},unmarkProcessing:function(){
dojo.html.setClass(this.expandNode,this.markProcessingSavedClass);
},buildRendering:function(args,_b01,_b02){
if(args.tree){
this.tree=dojo.lang.isString(args.tree)?dojo.widget.manager.getWidgetById(args.tree):args.tree;
}else{
if(_b02&&_b02.tree){
this.tree=_b02.tree;
}
}
if(!this.tree){
dojo.raise("Can't evaluate tree from arguments or parent");
}
this.domNode=this.tree.nodeTemplate.cloneNode(true);
this.expandNode=this.domNode.firstChild;
this.contentNode=this.domNode.childNodes[1];
this.labelNode=this.contentNode.firstChild;
if(this.labelClass){
dojo.html.addClass(this.labelNode,this.labelClass);
}
if(this.contentClass){
dojo.html.addClass(this.contentNode,this.contentClass);
}
this.domNode.widgetId=this.widgetId;
this.labelNode.innerHTML=this.title;
},isTreeNode:true,object:{},title:"",isFolder:null,contentNode:null,expandClass:"",isExpanded:false,containerNode:null,getInfo:function(){
var info={widgetId:this.widgetId,objectId:this.objectId,index:this.getParentIndex()};
return info;
},setFolder:function(){
this.isFolder=true;
this.viewSetExpand();
if(!this.containerNode){
this.viewAddContainer();
}
dojo.event.topic.publish(this.tree.eventNames.afterSetFolder,{source:this});
},initialize:function(args,frag,_b06){
if(args.isFolder){
this.isFolder=true;
}
if(this.children.length||this.isFolder){
this.setFolder();
}else{
this.viewSetExpand();
}
for(var i=0;i<this.actionsDisabled.length;i++){
this.actionsDisabled[i]=this.actionsDisabled[i].toUpperCase();
}
dojo.event.topic.publish(this.tree.eventNames.afterChangeTree,{oldTree:null,newTree:this.tree,node:this});
},unsetFolder:function(){
this.isFolder=false;
this.viewSetExpand();
dojo.event.topic.publish(this.tree.eventNames.afterUnsetFolder,{source:this});
},insertNode:function(_b08,_b09){
if(!_b09){
_b09=0;
}
if(_b09==0){
dojo.html.prependChild(this.domNode,_b08.containerNode);
}else{
dojo.html.insertAfter(this.domNode,_b08.children[_b09-1].domNode);
}
},updateTree:function(_b0a){
if(this.tree===_b0a){
return;
}
var _b0b=this.tree;
dojo.lang.forEach(this.getDescendants(),function(elem){
elem.tree=_b0a;
});
if(_b0b.classPrefix!=_b0a.classPrefix){
var _b0d=[this.domNode];
var elem;
var reg=new RegExp("(^|\\s)"+_b0b.classPrefix,"g");
while(elem=_b0d.pop()){
for(var i=0;i<elem.childNodes.length;i++){
var _b11=elem.childNodes[i];
if(_b11.nodeDocType!=1){
continue;
}
dojo.html.setClass(_b11,dojo.html.getClass(_b11).replace(reg,"$1"+_b0a.classPrefix));
_b0d.push(_b11);
}
}
}
var _b12={oldTree:_b0b,newTree:_b0a,node:this};
dojo.event.topic.publish(this.tree.eventNames.afterChangeTree,_b12);
dojo.event.topic.publish(_b0a.eventNames.afterChangeTree,_b12);
},addedTo:function(_b13,_b14,_b15){
if(this.tree!==_b13.tree){
this.updateTree(_b13.tree);
}
if(_b13.isTreeNode){
if(!_b13.isFolder){
_b13.setFolder();
_b13.state=_b13.loadStates.LOADED;
}
}
var _b16=_b13.children.length;
this.insertNode(_b13,_b14);
this.viewAddLayout();
if(_b16>1){
if(_b14==0&&_b13.children[1] instanceof dojo.widget.Widget){
_b13.children[1].viewUpdateLayout();
}
if(_b14==_b16-1&&_b13.children[_b16-2] instanceof dojo.widget.Widget){
_b13.children[_b16-2].viewUpdateLayout();
}
}else{
if(_b13.isTreeNode){
_b13.viewSetHasChildren();
}
}
if(!_b15){
var _b17={child:this,index:_b14,parent:_b13};
dojo.event.topic.publish(this.tree.eventNames.afterAddChild,_b17);
}
},createSimple:function(args,_b19){
if(args.tree){
var tree=args.tree;
}else{
if(_b19){
var tree=_b19.tree;
}else{
dojo.raise("createSimple: can't evaluate tree");
}
}
tree=dojo.widget.byId(tree);
var _b1b=new tree.defaultChildWidget();
for(var x in args){
_b1b[x]=args[x];
}
_b1b.toggleObj=dojo.lfx.toggle[_b1b.toggle.toLowerCase()]||dojo.lfx.toggle.plain;
dojo.widget.manager.add(_b1b);
_b1b.buildRendering(args,{},_b19);
_b1b.initialize(args,{},_b19);
if(_b1b.parent){
delete dojo.widget.manager.topWidgets[_b1b.widgetId];
}
return _b1b;
},viewUpdateLayout:function(){
this.viewRemoveLayout();
this.viewAddLayout();
},viewAddContainer:function(){
this.containerNode=this.tree.containerNodeTemplate.cloneNode(true);
this.domNode.appendChild(this.containerNode);
},viewAddLayout:function(){
if(this.parent["isTree"]){
dojo.html.setClass(this.domNode,dojo.html.getClass(this.domNode)+" "+this.tree.classPrefix+"IsRoot");
}
if(this.isLastChild()){
dojo.html.setClass(this.domNode,dojo.html.getClass(this.domNode)+" "+this.tree.classPrefix+"IsLast");
}
},viewRemoveLayout:function(){
dojo.html.removeClass(this.domNode,this.tree.classPrefix+"IsRoot");
dojo.html.removeClass(this.domNode,this.tree.classPrefix+"IsLast");
},viewGetExpandClass:function(){
if(this.isFolder){
return this.isExpanded?"ExpandOpen":"ExpandClosed";
}else{
return "ExpandLeaf";
}
},viewSetExpand:function(){
var _b1d=this.tree.classPrefix+this.viewGetExpandClass();
var reg=new RegExp("(^|\\s)"+this.tree.classPrefix+"Expand\\w+","g");
dojo.html.setClass(this.domNode,dojo.html.getClass(this.domNode).replace(reg,"")+" "+_b1d);
this.viewSetHasChildrenAndExpand();
},viewGetChildrenClass:function(){
return "Children"+(this.children.length?"Yes":"No");
},viewSetHasChildren:function(){
var _b1f=this.tree.classPrefix+this.viewGetChildrenClass();
var reg=new RegExp("(^|\\s)"+this.tree.classPrefix+"Children\\w+","g");
dojo.html.setClass(this.domNode,dojo.html.getClass(this.domNode).replace(reg,"")+" "+_b1f);
this.viewSetHasChildrenAndExpand();
},viewSetHasChildrenAndExpand:function(){
var _b21=this.tree.classPrefix+"State"+this.viewGetChildrenClass()+"-"+this.viewGetExpandClass();
var reg=new RegExp("(^|\\s)"+this.tree.classPrefix+"State[\\w-]+","g");
dojo.html.setClass(this.domNode,dojo.html.getClass(this.domNode).replace(reg,"")+" "+_b21);
},viewUnfocus:function(){
dojo.html.removeClass(this.labelNode,this.tree.classPrefix+"LabelFocused");
},viewFocus:function(){
dojo.html.addClass(this.labelNode,this.tree.classPrefix+"LabelFocused");
},viewEmphasize:function(){
dojo.html.clearSelection(this.labelNode);
dojo.html.addClass(this.labelNode,this.tree.classPrefix+"NodeEmphasized");
},viewUnemphasize:function(){
dojo.html.removeClass(this.labelNode,this.tree.classPrefix+"NodeEmphasized");
},detach:function(){
if(!this.parent){
return;
}
var _b23=this.parent;
var _b24=this.getParentIndex();
this.doDetach.apply(this,arguments);
dojo.event.topic.publish(this.tree.eventNames.afterDetach,{child:this,parent:_b23,index:_b24});
},doDetach:function(){
var _b25=this.parent;
if(!_b25){
return;
}
var _b26=this.getParentIndex();
this.viewRemoveLayout();
dojo.widget.DomWidget.prototype.removeChild.call(_b25,this);
var _b27=_b25.children.length;
if(_b27>0){
if(_b26==0){
_b25.children[0].viewUpdateLayout();
}
if(_b26==_b27){
_b25.children[_b27-1].viewUpdateLayout();
}
}else{
if(_b25.isTreeNode){
_b25.viewSetHasChildren();
}
}
if(this.tree.unsetFolderOnEmpty&&!_b25.children.length&&_b25.isTreeNode){
_b25.unsetFolder();
}
this.parent=null;
},destroy:function(){
dojo.event.topic.publish(this.tree.eventNames.beforeNodeDestroy,{source:this});
this.detach();
return dojo.widget.HtmlWidget.prototype.destroy.apply(this,arguments);
},expand:function(){
if(this.isExpanded){
return;
}
if(this.tryLazyInit){
this.setChildren();
this.tryLazyInit=false;
}
this.isExpanded=true;
this.viewSetExpand();
this.showChildren();
},collapse:function(){
if(!this.isExpanded){
return;
}
this.isExpanded=false;
this.hideChildren();
},hideChildren:function(){
this.tree.toggleObj.hide(this.containerNode,this.tree.toggleDuration,this.explodeSrc,dojo.lang.hitch(this,"onHideChildren"));
},showChildren:function(){
this.tree.toggleObj.show(this.containerNode,this.tree.toggleDuration,this.explodeSrc,dojo.lang.hitch(this,"onShowChildren"));
},onShowChildren:function(){
this.onShow();
dojo.event.topic.publish(this.tree.eventNames.afterExpand,{source:this});
},onHideChildren:function(){
this.viewSetExpand();
this.onHide();
dojo.event.topic.publish(this.tree.eventNames.afterCollapse,{source:this});
},setTitle:function(_b28){
var _b29=this.title;
this.labelNode.innerHTML=this.title=_b28;
dojo.event.topic.publish(this.tree.eventNames.afterSetTitle,{source:this,oldTitle:_b29});
},toString:function(){
return "["+this.widgetType+", "+this.title+"]";
}});
dojo.provide("dojo.widget.TreeV3");
dojo.widget.defineWidget("dojo.widget.TreeV3",[dojo.widget.HtmlWidget,dojo.widget.TreeWithNode],function(){
this.eventNames={};
this.DndAcceptTypes=[];
this.actionsDisabled=[];
this.listeners=[];
this.tree=this;
},{DndMode:"",defaultChildWidget:null,defaultChildTitle:"New Node",eagerWidgetInstantiation:false,eventNamesDefault:{afterTreeCreate:"afterTreeCreate",beforeTreeDestroy:"beforeTreeDestroy",beforeNodeDestroy:"beforeNodeDestroy",afterChangeTree:"afterChangeTree",afterSetFolder:"afterSetFolder",afterUnsetFolder:"afterUnsetFolder",beforeMoveFrom:"beforeMoveFrom",beforeMoveTo:"beforeMoveTo",afterMoveFrom:"afterMoveFrom",afterMoveTo:"afterMoveTo",afterAddChild:"afterAddChild",afterDetach:"afterDetach",afterExpand:"afterExpand",beforeExpand:"beforeExpand",afterSetTitle:"afterSetTitle",afterCollapse:"afterCollapse",beforeCollapse:"beforeCollapse"},classPrefix:"Tree",style:"",allowAddChildToLeaf:true,unsetFolderOnEmpty:true,DndModes:{BETWEEN:1,ONTO:2},DndAcceptTypes:"",templateCssPath:dojo.uri.dojoUri("src/widget/templates/TreeV3.css"),templateString:"<div style=\"${this.style}\">\n</div>",isExpanded:true,isTree:true,createNode:function(data){
data.tree=this.widgetId;
if(data.widgetName){
return dojo.widget.createWidget(data.widgetName,data);
}else{
if(this.defaultChildWidget.prototype.createSimple){
return this.defaultChildWidget.prototype.createSimple(data);
}else{
var ns=this.defaultChildWidget.prototype.ns;
var wt=this.defaultChildWidget.prototype.widgetType;
return dojo.widget.createWidget(ns+":"+wt,data);
}
}
},makeNodeTemplate:function(){
var _b2d=document.createElement("div");
dojo.html.setClass(_b2d,this.classPrefix+"Node "+this.classPrefix+"ExpandLeaf "+this.classPrefix+"ChildrenNo");
this.nodeTemplate=_b2d;
var _b2e=document.createElement("div");
var _b2f=this.classPrefix+"Expand";
if(dojo.render.html.ie){
_b2f=_b2f+" "+this.classPrefix+"IEExpand";
}
dojo.html.setClass(_b2e,_b2f);
this.expandNodeTemplate=_b2e;
var _b30=document.createElement("span");
dojo.html.setClass(_b30,this.classPrefix+"Label");
this.labelNodeTemplate=_b30;
var _b31=document.createElement("div");
var _b2f=this.classPrefix+"Content";
if(dojo.render.html.ie&&!dojo.render.html.ie70){
_b2f=_b2f+" "+this.classPrefix+"IEContent";
}
dojo.html.setClass(_b31,_b2f);
this.contentNodeTemplate=_b31;
_b2d.appendChild(_b2e);
_b2d.appendChild(_b31);
_b31.appendChild(_b30);
},makeContainerNodeTemplate:function(){
var div=document.createElement("div");
div.style.display="none";
dojo.html.setClass(div,this.classPrefix+"Container");
this.containerNodeTemplate=div;
},actions:{ADDCHILD:"ADDCHILD"},getInfo:function(){
var info={widgetId:this.widgetId,objectId:this.objectId};
return info;
},adjustEventNames:function(){
for(var name in this.eventNamesDefault){
if(dojo.lang.isUndefined(this.eventNames[name])){
this.eventNames[name]=this.widgetId+"/"+this.eventNamesDefault[name];
}
}
},adjustDndMode:function(){
var _b35=this;
var _b36=0;
dojo.lang.forEach(this.DndMode.split(";"),function(elem){
var mode=_b35.DndModes[dojo.string.trim(elem).toUpperCase()];
if(mode){
_b36=_b36|mode;
}
});
this.DndMode=_b36;
},destroy:function(){
dojo.event.topic.publish(this.tree.eventNames.beforeTreeDestroy,{source:this});
return dojo.widget.HtmlWidget.prototype.destroy.apply(this,arguments);
},initialize:function(args){
this.domNode.widgetId=this.widgetId;
for(var i=0;i<this.actionsDisabled.length;i++){
this.actionsDisabled[i]=this.actionsDisabled[i].toUpperCase();
}
if(!args.defaultChildWidget){
this.defaultChildWidget=dojo.widget.TreeNodeV3;
}else{
this.defaultChildWidget=dojo.lang.getObjPathValue(args.defaultChildWidget);
}
this.adjustEventNames();
this.adjustDndMode();
this.makeNodeTemplate();
this.makeContainerNodeTemplate();
this.containerNode=this.domNode;
dojo.html.setClass(this.domNode,this.classPrefix+"Container");
var _b3b=this;
dojo.lang.forEach(this.listeners,function(elem){
var t=dojo.lang.isString(elem)?dojo.widget.byId(elem):elem;
t.listenTree(_b3b);
});
},postCreate:function(){
dojo.event.topic.publish(this.eventNames.afterTreeCreate,{source:this});
},move:function(_b3e,_b3f,_b40){
if(!_b3e.parent){
dojo.raise(this.widgetType+": child can be moved only while it's attached");
}
var _b41=_b3e.parent;
var _b42=_b3e.tree;
var _b43=_b3e.getParentIndex();
var _b44=_b3f.tree;
var _b3f=_b3f;
var _b45=_b40;
var _b46={oldParent:_b41,oldTree:_b42,oldIndex:_b43,newParent:_b3f,newTree:_b44,newIndex:_b45,child:_b3e};
dojo.event.topic.publish(_b42.eventNames.beforeMoveFrom,_b46);
dojo.event.topic.publish(_b44.eventNames.beforeMoveTo,_b46);
this.doMove.apply(this,arguments);
dojo.event.topic.publish(_b42.eventNames.afterMoveFrom,_b46);
dojo.event.topic.publish(_b44.eventNames.afterMoveTo,_b46);
},doMove:function(_b47,_b48,_b49){
_b47.doDetach();
_b48.doAddChild(_b47,_b49);
},toString:function(){
return "["+this.widgetType+" ID:"+this.widgetId+"]";
}});
dojo.provide("dojo.widget.Menu2");
dojo.widget.defineWidget("dojo.widget.PopupMenu2",dojo.widget.PopupContainer,function(){
this.targetNodeIds=[];
this.eventNames={open:""};
},{snarfChildDomOutput:true,eventNaming:"default",templateString:"<table class=\"dojoPopupMenu2\" border=0 cellspacing=0 cellpadding=0 style=\"display: none;\"><tbody dojoAttachPoint=\"containerNode\"></tbody></table>",templateCssPath:dojo.uri.dojoUri("src/widget/templates/Menu2.css"),templateCssString:"",submenuDelay:500,submenuOverlap:5,contextMenuForWindow:false,initialize:function(args,frag){
if(this.eventNaming=="default"){
for(var _b4c in this.eventNames){
this.eventNames[_b4c]=this.widgetId+"/"+_b4c;
}
}
},postCreate:function(){
if(this.contextMenuForWindow){
var doc=dojo.body();
this.bindDomNode(doc);
}else{
if(this.targetNodeIds.length>0){
dojo.lang.forEach(this.targetNodeIds,this.bindDomNode,this);
}
}
this._subscribeSubitemsOnOpen();
},_subscribeSubitemsOnOpen:function(){
var _b4e=this.getChildrenOfType(dojo.widget.MenuItem2);
for(var i=0;i<_b4e.length;i++){
dojo.event.topic.subscribe(this.eventNames.open,_b4e[i],"menuOpen");
}
},getTopOpenEvent:function(){
var menu=this;
while(menu.parentPopup){
menu=menu.parentPopup;
}
return menu.openEvent;
},bindDomNode:function(node){
node=dojo.byId(node);
var win=dojo.html.getElementWindow(node);
if(dojo.html.isTag(node,"iframe")=="iframe"){
win=dojo.html.iframeContentWindow(node);
node=dojo.withGlobal(win,dojo.body);
}
dojo.widget.Menu2.OperaAndKonqFixer.fixNode(node);
dojo.event.kwConnect({srcObj:node,srcFunc:"oncontextmenu",targetObj:this,targetFunc:"onOpen",once:true});
if(dojo.render.html.moz&&win.document.designMode.toLowerCase()=="on"){
dojo.event.browser.addListener(node,"contextmenu",dojo.lang.hitch(this,"onOpen"));
}
dojo.widget.PopupManager.registerWin(win);
},unBindDomNode:function(_b53){
var node=dojo.byId(_b53);
dojo.event.kwDisconnect({srcObj:node,srcFunc:"oncontextmenu",targetObj:this,targetFunc:"onOpen",once:true});
dojo.widget.Menu2.OperaAndKonqFixer.cleanNode(node);
},_moveToNext:function(evt){
this._highlightOption(1);
return true;
},_moveToPrevious:function(evt){
this._highlightOption(-1);
return true;
},_moveToParentMenu:function(evt){
if(this._highlighted_option&&this.parentPopup){
if(evt._menu2UpKeyProcessed){
return true;
}else{
this._highlighted_option.onUnhover();
this.closeSubpopup();
evt._menu2UpKeyProcessed=true;
}
}
return false;
},_moveToChildMenu:function(evt){
if(this._highlighted_option&&this._highlighted_option.submenuId){
this._highlighted_option._onClick(true);
return true;
}
return false;
},_selectCurrentItem:function(evt){
if(this._highlighted_option){
this._highlighted_option._onClick();
return true;
}
return false;
},processKey:function(evt){
if(evt.ctrlKey||evt.altKey||!evt.key){
return false;
}
var rval=false;
switch(evt.key){
case evt.KEY_DOWN_ARROW:
rval=this._moveToNext(evt);
break;
case evt.KEY_UP_ARROW:
rval=this._moveToPrevious(evt);
break;
case evt.KEY_RIGHT_ARROW:
rval=this._moveToChildMenu(evt);
break;
case evt.KEY_LEFT_ARROW:
rval=this._moveToParentMenu(evt);
break;
case " ":
case evt.KEY_ENTER:
if(rval=this._selectCurrentItem(evt)){
break;
}
case evt.KEY_ESCAPE:
dojo.widget.PopupManager.currentMenu.close();
rval=true;
break;
}
return rval;
},_findValidItem:function(dir,_b5d){
if(_b5d){
_b5d=dir>0?_b5d.getNextSibling():_b5d.getPreviousSibling();
}
for(var i=0;i<this.children.length;++i){
if(!_b5d){
_b5d=dir>0?this.children[0]:this.children[this.children.length-1];
}
if(_b5d.onHover&&_b5d.isShowing()){
return _b5d;
}
_b5d=dir>0?_b5d.getNextSibling():_b5d.getPreviousSibling();
}
},_highlightOption:function(dir){
var item;
if((!this._highlighted_option)){
item=this._findValidItem(dir);
}else{
item=this._findValidItem(dir,this._highlighted_option);
}
if(item){
if(this._highlighted_option){
this._highlighted_option.onUnhover();
}
item.onHover();
dojo.html.scrollIntoView(item.domNode);
try{
var node=dojo.html.getElementsByClass("dojoMenuItem2Label",item.domNode)[0];
node.focus();
}
catch(e){
}
}
},onItemClick:function(item){
},close:function(_b63){
if(this.animationInProgress){
dojo.widget.PopupMenu2.superclass.close.apply(this,arguments);
return;
}
if(this._highlighted_option){
this._highlighted_option.onUnhover();
}
dojo.widget.PopupMenu2.superclass.close.apply(this,arguments);
},closeSubpopup:function(_b64){
if(this.currentSubpopup==null){
return;
}
this.currentSubpopup.close(_b64);
this.currentSubpopup=null;
this.currentSubmenuTrigger.is_open=false;
this.currentSubmenuTrigger._closedSubmenu(_b64);
this.currentSubmenuTrigger=null;
},_openSubmenu:function(_b65,_b66){
var _b67=dojo.html.getAbsolutePosition(_b66.domNode,true);
var _b68=dojo.html.getMarginBox(this.domNode).width;
var x=_b67.x+_b68-this.submenuOverlap;
var y=_b67.y;
_b65.open(x,y,this,_b66.domNode);
this.currentSubmenuTrigger=_b66;
this.currentSubmenuTrigger.is_open=true;
},onOpen:function(e){
this.openEvent=e;
if(e["target"]){
this.openedForWindow=dojo.html.getElementWindow(e.target);
}else{
this.openedForWindow=null;
}
var x=e.pageX,y=e.pageY;
var win=dojo.html.getElementWindow(e.target);
var _b6f=win._frameElement||win.frameElement;
if(_b6f){
var cood=dojo.html.abs(_b6f,true);
x+=cood.x-dojo.withGlobal(win,dojo.html.getScroll).left;
y+=cood.y-dojo.withGlobal(win,dojo.html.getScroll).top;
}
this.open(x,y,null,[x,y]);
e.preventDefault();
e.stopPropagation();
}});
dojo.widget.defineWidget("dojo.widget.MenuItem2",dojo.widget.HtmlWidget,function(){
this.eventNames={engage:""};
},{templateString:"<tr class=\"dojoMenuItem2\" dojoAttachEvent=\"onMouseOver: onHover; onMouseOut: onUnhover; onClick: _onClick; onKey:onKey;\">"+"<td><div class=\"${this.iconClass}\" style=\"${this.iconStyle}\"></div></td>"+"<td tabIndex=\"-1\" class=\"dojoMenuItem2Label\">${this.caption}</td>"+"<td class=\"dojoMenuItem2Accel\">${this.accelKey}</td>"+"<td><div class=\"dojoMenuItem2Submenu\" style=\"display:${this.arrowDisplay};\"></div></td>"+"</tr>",is_hovering:false,hover_timer:null,is_open:false,topPosition:0,caption:"Untitled",accelKey:"",iconSrc:"",disabledClass:"dojoMenuItem2Disabled",iconClass:"dojoMenuItem2Icon",submenuId:"",eventNaming:"default",highlightClass:"dojoMenuItem2Hover",postMixInProperties:function(){
this.iconStyle="";
if(this.iconSrc){
if((this.iconSrc.toLowerCase().substring(this.iconSrc.length-4)==".png")&&(dojo.render.html.ie55||dojo.render.html.ie60)){
this.iconStyle="filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+this.iconSrc+"', sizingMethod='image')";
}else{
this.iconStyle="background-image: url("+this.iconSrc+")";
}
}
this.arrowDisplay=this.submenuId?"block":"none";
dojo.widget.MenuItem2.superclass.postMixInProperties.apply(this,arguments);
},fillInTemplate:function(){
dojo.html.disableSelection(this.domNode);
if(this.disabled){
this.setDisabled(true);
}
if(this.eventNaming=="default"){
for(var _b71 in this.eventNames){
this.eventNames[_b71]=this.widgetId+"/"+_b71;
}
}
},onHover:function(){
this.onUnhover();
if(this.is_hovering){
return;
}
if(this.is_open){
return;
}
if(this.parent._highlighted_option){
this.parent._highlighted_option.onUnhover();
}
this.parent.closeSubpopup();
this.parent._highlighted_option=this;
dojo.widget.PopupManager.setFocusedMenu(this.parent);
this._highlightItem();
if(this.is_hovering){
this._stopSubmenuTimer();
}
this.is_hovering=true;
this._startSubmenuTimer();
},onUnhover:function(){
if(!this.is_open){
this._unhighlightItem();
}
this.is_hovering=false;
this.parent._highlighted_option=null;
if(this.parent.parentPopup){
dojo.widget.PopupManager.setFocusedMenu(this.parent.parentPopup);
}
this._stopSubmenuTimer();
},_onClick:function(_b72){
var _b73=false;
if(this.disabled){
return false;
}
if(this.submenuId){
if(!this.is_open){
this._stopSubmenuTimer();
this._openSubmenu();
}
_b73=true;
}else{
this.onUnhover();
this.parent.closeAll(true);
}
this.onClick();
dojo.event.topic.publish(this.eventNames.engage,this);
if(_b73&&_b72){
dojo.widget.getWidgetById(this.submenuId)._highlightOption(1);
}
return;
},onClick:function(){
this.parent.onItemClick(this);
},_highlightItem:function(){
dojo.html.addClass(this.domNode,this.highlightClass);
},_unhighlightItem:function(){
dojo.html.removeClass(this.domNode,this.highlightClass);
},_startSubmenuTimer:function(){
this._stopSubmenuTimer();
if(this.disabled){
return;
}
var self=this;
var _b75=function(){
return function(){
self._openSubmenu();
};
}();
this.hover_timer=dojo.lang.setTimeout(_b75,this.parent.submenuDelay);
},_stopSubmenuTimer:function(){
if(this.hover_timer){
dojo.lang.clearTimeout(this.hover_timer);
this.hover_timer=null;
}
},_openSubmenu:function(){
if(this.disabled){
return;
}
this.parent.closeSubpopup();
var _b76=dojo.widget.getWidgetById(this.submenuId);
if(_b76){
this.parent._openSubmenu(_b76,this);
}
},_closedSubmenu:function(){
this.onUnhover();
},setDisabled:function(_b77){
this.disabled=_b77;
if(this.disabled){
dojo.html.addClass(this.domNode,this.disabledClass);
}else{
dojo.html.removeClass(this.domNode,this.disabledClass);
}
},enable:function(){
this.setDisabled(false);
},disable:function(){
this.setDisabled(true);
},menuOpen:function(_b78){
}});
dojo.widget.defineWidget("dojo.widget.MenuSeparator2",dojo.widget.HtmlWidget,{templateString:"<tr class=\"dojoMenuSeparator2\"><td colspan=4>"+"<div class=\"dojoMenuSeparator2Top\"></div>"+"<div class=\"dojoMenuSeparator2Bottom\"></div>"+"</td></tr>",postCreate:function(){
dojo.html.disableSelection(this.domNode);
}});
dojo.widget.defineWidget("dojo.widget.MenuBar2",dojo.widget.PopupMenu2,{menuOverlap:2,templateString:"<div class=\"dojoMenuBar2\" tabIndex=\"0\"><table class=\"dojoMenuBar2Client\"><tr dojoAttachPoint=\"containerNode\"></tr></table></div>",close:function(_b79){
if(this._highlighted_option){
this._highlighted_option.onUnhover();
}
this.closeSubpopup(_b79);
},processKey:function(evt){
if(evt.ctrlKey||evt.altKey){
return false;
}
if(!dojo.html.hasClass(evt.target,"dojoMenuBar2")){
return false;
}
var rval=false;
switch(evt.key){
case evt.KEY_DOWN_ARROW:
rval=this._moveToChildMenu(evt);
break;
case evt.KEY_UP_ARROW:
rval=this._moveToParentMenu(evt);
break;
case evt.KEY_RIGHT_ARROW:
rval=this._moveToNext(evt);
break;
case evt.KEY_LEFT_ARROW:
rval=this._moveToPrevious(evt);
break;
default:
rval=dojo.widget.MenuBar2.superclass.processKey.apply(this,arguments);
break;
}
return rval;
},postCreate:function(){
dojo.widget.MenuBar2.superclass.postCreate.apply(this,arguments);
dojo.widget.PopupManager.opened(this);
this.isShowingNow=true;
},_openSubmenu:function(_b7c,_b7d){
var _b7e=dojo.html.getAbsolutePosition(_b7d.domNode,true);
var _b7f=dojo.html.getAbsolutePosition(this.domNode,true);
var _b80=dojo.html.getBorderBox(this.domNode).height;
var x=_b7e.x;
var y=_b7f.y+_b80-this.menuOverlap;
_b7c.open(x,y,this,_b7d.domNode);
this.currentSubmenuTrigger=_b7d;
this.currentSubmenuTrigger.is_open=true;
}});
dojo.widget.defineWidget("dojo.widget.MenuBarItem2",dojo.widget.MenuItem2,{templateString:"<td class=\"dojoMenuBarItem2\" dojoAttachEvent=\"onMouseOver: onHover; onMouseOut: onUnhover; onClick: _onClick;\">"+"<span>${this.caption}</span>"+"</td>",highlightClass:"dojoMenuBarItem2Hover",setDisabled:function(_b83){
this.disabled=_b83;
if(this.disabled){
dojo.html.addClass(this.domNode,"dojoMenuBarItem2Disabled");
}else{
dojo.html.removeClass(this.domNode,"dojoMenuBarItem2Disabled");
}
}});
dojo.widget.Menu2.OperaAndKonqFixer=new function(){
var _b84=true;
var _b85=false;
if(!dojo.lang.isFunction(dojo.doc().oncontextmenu)){
dojo.doc().oncontextmenu=function(){
_b84=false;
_b85=true;
};
}
if(dojo.doc().createEvent){
try{
var e=dojo.doc().createEvent("MouseEvents");
e.initMouseEvent("contextmenu",1,1,dojo.global(),1,0,0,0,0,0,0,0,0,0,null);
dojo.doc().dispatchEvent(e);
}
catch(e){
}
}else{
_b84=false;
}
if(_b85){
delete dojo.doc().oncontextmenu;
}
this.fixNode=function(node){
if(_b84){
if(!dojo.lang.isFunction(node.oncontextmenu)){
node.oncontextmenu=function(e){
};
}
if(dojo.render.html.opera){
node._menufixer_opera=function(e){
if(e.ctrlKey){
this.oncontextmenu(e);
}
};
dojo.event.connect(node,"onclick",node,"_menufixer_opera");
}else{
node._menufixer_konq=function(e){
if(e.button==2){
e.preventDefault();
this.oncontextmenu(e);
}
};
dojo.event.connect(node,"onmousedown",node,"_menufixer_konq");
}
}
};
this.cleanNode=function(node){
if(_b84){
if(node._menufixer_opera){
dojo.event.disconnect(node,"onclick",node,"_menufixer_opera");
delete node._menufixer_opera;
}else{
if(node._menufixer_konq){
dojo.event.disconnect(node,"onmousedown",node,"_menufixer_konq");
delete node._menufixer_konq;
}
}
if(node.oncontextmenu){
delete node.oncontextmenu;
}
}
};
};
dojo.provide("dojo.widget.TreeContextMenuV3");
dojo.widget.defineWidget("dojo.widget.TreeContextMenuV3",[dojo.widget.PopupMenu2,dojo.widget.TreeCommon],function(){
this.listenedTrees={};
},{listenTreeEvents:["afterTreeCreate","beforeTreeDestroy"],listenNodeFilter:function(elem){
return elem instanceof dojo.widget.Widget;
},onAfterTreeCreate:function(_b8d){
var tree=_b8d.source;
this.bindDomNode(tree.domNode);
},onBeforeTreeDestroy:function(_b8f){
this.unBindDomNode(_b8f.source.domNode);
},getTreeNode:function(){
var _b90=this.getTopOpenEvent().target;
var _b91=this.domElement2TreeNode(_b90);
return _b91;
},open:function(){
var _b92=dojo.widget.PopupMenu2.prototype.open.apply(this,arguments);
for(var i=0;i<this.children.length;i++){
if(this.children[i].menuOpen){
this.children[i].menuOpen(this.getTreeNode());
}
}
return _b92;
},close:function(){
for(var i=0;i<this.children.length;i++){
if(this.children[i].menuClose){
this.children[i].menuClose(this.getTreeNode());
}
}
var _b95=dojo.widget.PopupMenu2.prototype.close.apply(this,arguments);
return _b95;
}});
dojo.widget.defineWidget("dojo.widget.TreeMenuItemV3",[dojo.widget.MenuItem2,dojo.widget.TreeCommon],function(){
this.treeActions=[];
},{treeActions:"",initialize:function(args,frag){
for(var i=0;i<this.treeActions.length;i++){
this.treeActions[i]=this.treeActions[i].toUpperCase();
}
},getTreeNode:function(){
var menu=this;
while(!(menu instanceof dojo.widget.TreeContextMenuV3)){
menu=menu.parent;
}
var _b9a=menu.getTreeNode();
return _b9a;
},menuOpen:function(_b9b){
_b9b.viewEmphasize();
this.setDisabled(false);
var _b9c=this;
dojo.lang.forEach(_b9c.treeActions,function(_b9d){
_b9c.setDisabled(_b9b.actionIsDisabledNow(_b9d));
});
},menuClose:function(_b9e){
_b9e.viewUnemphasize();
},toString:function(){
return "["+this.widgetType+" node "+this.getTreeNode()+"]";
}});
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_b9f){
this.pairs=[];
this.returnWrappers=_b9f||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_ba1,wrap,_ba3,_ba4){
var type=(_ba4)?"unshift":"push";
this.pairs[type]([name,_ba1,wrap,_ba3]);
},match:function(){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[1].apply(this,arguments)){
if((pair[3])||(this.returnWrappers)){
return pair[2];
}else{
return pair[2].apply(this,arguments);
}
}
}
throw new Error("No match found");
},unregister:function(name){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[0]==name){
this.pairs.splice(i,1);
return true;
}
}
return false;
}});
dojo.provide("dojo.json");
dojo.json={jsonRegistry:new dojo.AdapterRegistry(),register:function(name,_bac,wrap,_bae){
dojo.json.jsonRegistry.register(name,_bac,wrap,_bae);
},evalJson:function(json){
try{
return eval("("+json+")");
}
catch(e){
dojo.debug(e);
return json;
}
},serialize:function(o){
var _bb1=typeof (o);
if(_bb1=="undefined"){
return "undefined";
}else{
if((_bb1=="number")||(_bb1=="boolean")){
return o+"";
}else{
if(o===null){
return "null";
}
}
}
if(_bb1=="string"){
return dojo.string.escapeString(o);
}
var me=arguments.callee;
var _bb3;
if(typeof (o.__json__)=="function"){
_bb3=o.__json__();
if(o!==_bb3){
return me(_bb3);
}
}
if(typeof (o.json)=="function"){
_bb3=o.json();
if(o!==_bb3){
return me(_bb3);
}
}
if(_bb1!="function"&&typeof (o.length)=="number"){
var res=[];
for(var i=0;i<o.length;i++){
var val=me(o[i]);
if(typeof (val)!="string"){
val="undefined";
}
res.push(val);
}
return "["+res.join(",")+"]";
}
try{
window.o=o;
_bb3=dojo.json.jsonRegistry.match(o);
return me(_bb3);
}
catch(e){
}
if(_bb1=="function"){
return null;
}
res=[];
for(var k in o){
var _bb8;
if(typeof (k)=="number"){
_bb8="\""+k+"\"";
}else{
if(typeof (k)=="string"){
_bb8=dojo.string.escapeString(k);
}else{
continue;
}
}
val=me(o[k]);
if(typeof (val)!="string"){
continue;
}
res.push(_bb8+":"+val);
}
return "{"+res.join(",")+"}";
}};
dojo.provide("dojo.widget.TreeTimeoutIterator");
dojo.declare("dojo.widget.TreeTimeoutIterator",null,function(elem,_bba,_bbb){
var _bbc=this;
this.currentParent=elem;
this.callFunc=_bba;
this.callObj=_bbb?_bbb:this;
this.stack=[];
},{maxStackDepth:Number.POSITIVE_INFINITY,stack:null,currentParent:null,currentIndex:0,filterFunc:function(){
return true;
},finishFunc:function(){
return true;
},setFilter:function(func,obj){
this.filterFunc=func;
this.filterObj=obj;
},setMaxLevel:function(_bbf){
this.maxStackDepth=_bbf-2;
},forward:function(_bc0){
var _bc1=this;
if(this.timeout){
var tid=setTimeout(function(){
_bc1.processNext();
clearTimeout(tid);
},_bc1.timeout);
}else{
return this.processNext();
}
},start:function(_bc3){
if(_bc3){
return this.callFunc.call(this.callObj,this.currentParent,this);
}
return this.processNext();
},processNext:function(){
var _bc4;
var _bc5=this;
var _bc6;
var next;
if(this.maxStackDepth==-2){
return;
}
while(true){
var _bc8=this.currentParent.children;
if(_bc8&&_bc8.length){
do{
next=_bc8[this.currentIndex];
}while(this.currentIndex++<_bc8.length&&!(_bc6=this.filterFunc.call(this.filterObj,next)));
if(_bc6){
if(next.isFolder&&this.stack.length<=this.maxStackDepth){
this.moveParent(next,0);
}
return this.callFunc.call(this.callObj,next,this);
}
}
if(this.stack.length){
this.popParent();
continue;
}
break;
}
return this.finishFunc.call(this.finishObj);
},setFinish:function(func,obj){
this.finishFunc=func;
this.finishObj=obj;
},popParent:function(){
var p=this.stack.pop();
this.currentParent=p[0];
this.currentIndex=p[1];
},moveParent:function(_bcc,_bcd){
this.stack.push([this.currentParent,this.currentIndex]);
this.currentParent=_bcc;
this.currentIndex=_bcd;
}});
dojo.provide("dojo.widget.TreeBasicControllerV3");
dojo.widget.defineWidget("dojo.widget.TreeBasicControllerV3",[dojo.widget.HtmlWidget,dojo.widget.TreeCommon],function(){
this.listenedTrees={};
},{listenTreeEvents:["afterSetFolder","afterTreeCreate","beforeTreeDestroy"],listenNodeFilter:function(elem){
return elem instanceof dojo.widget.Widget;
},editor:null,initialize:function(args){
if(args.editor){
this.editor=dojo.widget.byId(args.editor);
this.editor.controller=this;
}
},getInfo:function(elem){
return elem.getInfo();
},onBeforeTreeDestroy:function(_bd1){
this.unlistenTree(_bd1.source);
},onAfterSetFolder:function(_bd2){
if(_bd2.source.expandLevel>0){
this.expandToLevel(_bd2.source,_bd2.source.expandLevel);
}
if(_bd2.source.loadLevel>0){
this.loadToLevel(_bd2.source,_bd2.source.loadLevel);
}
},_focusNextVisible:function(_bd3){
if(_bd3.isFolder&&_bd3.isExpanded&&_bd3.children.length>0){
_bd4=_bd3.children[0];
}else{
while(_bd3.isTreeNode&&_bd3.isLastChild()){
_bd3=_bd3.parent;
}
if(_bd3.isTreeNode){
var _bd4=_bd3.parent.children[_bd3.getParentIndex()+1];
}
}
if(_bd4&&_bd4.isTreeNode){
this._focusLabel(_bd4);
return _bd4;
}
},_focusPreviousVisible:function(_bd5){
var _bd6=_bd5;
if(!_bd5.isFirstChild()){
var _bd7=_bd5.parent.children[_bd5.getParentIndex()-1];
_bd5=_bd7;
while(_bd5.isFolder&&_bd5.isExpanded&&_bd5.children.length>0){
_bd6=_bd5;
_bd5=_bd5.children[_bd5.children.length-1];
}
}else{
_bd5=_bd5.parent;
}
if(_bd5&&_bd5.isTreeNode){
_bd6=_bd5;
}
if(_bd6&&_bd6.isTreeNode){
this._focusLabel(_bd6);
return _bd6;
}
},_focusZoomIn:function(_bd8){
var _bd9=_bd8;
if(_bd8.isFolder&&!_bd8.isExpanded){
this.expand(_bd8);
}else{
if(_bd8.children.length>0){
_bd8=_bd8.children[0];
}
}
if(_bd8&&_bd8.isTreeNode){
_bd9=_bd8;
}
if(_bd9&&_bd9.isTreeNode){
this._focusLabel(_bd9);
return _bd9;
}
},_focusZoomOut:function(node){
var _bdb=node;
if(node.isFolder&&node.isExpanded){
this.collapse(node);
}else{
node=node.parent;
}
if(node&&node.isTreeNode){
_bdb=node;
}
if(_bdb&&_bdb.isTreeNode){
this._focusLabel(_bdb);
return _bdb;
}
},onFocusNode:function(e){
var node=this.domElement2TreeNode(e.target);
if(node){
node.viewFocus();
dojo.event.browser.stopEvent(e);
}
},onBlurNode:function(e){
var node=this.domElement2TreeNode(e.target);
if(!node){
return;
}
var _be0=node.labelNode;
_be0.setAttribute("tabIndex","-1");
node.viewUnfocus();
dojo.event.browser.stopEvent(e);
node.tree.domNode.setAttribute("tabIndex","0");
},_focusLabel:function(node){
var _be2=node.tree.lastFocused;
var _be3;
if(_be2&&_be2.labelNode){
_be3=_be2.labelNode;
dojo.event.disconnect(_be3,"onblur",this,"onBlurNode");
_be3.setAttribute("tabIndex","-1");
dojo.html.removeClass(_be3,"TreeLabelFocused");
}
_be3=node.labelNode;
_be3.setAttribute("tabIndex","0");
node.tree.lastFocused=node;
dojo.html.addClass(_be3,"TreeLabelFocused");
dojo.event.connectOnce(_be3,"onblur",this,"onBlurNode");
dojo.event.connectOnce(_be3,"onfocus",this,"onFocusNode");
_be3.focus();
},onKey:function(e){
if(!e.key||e.ctrkKey||e.altKey){
return;
}
var _be5=this.domElement2TreeNode(e.target);
if(!_be5){
return;
}
var _be6=_be5.tree;
if(_be6.lastFocused&&_be6.lastFocused.labelNode){
_be5=_be6.lastFocused;
}
switch(e.key){
case e.KEY_TAB:
if(e.shiftKey){
_be6.domNode.setAttribute("tabIndex","-1");
}
break;
case e.KEY_RIGHT_ARROW:
this._focusZoomIn(_be5);
dojo.event.browser.stopEvent(e);
break;
case e.KEY_LEFT_ARROW:
this._focusZoomOut(_be5);
dojo.event.browser.stopEvent(e);
break;
case e.KEY_UP_ARROW:
this._focusPreviousVisible(_be5);
dojo.event.browser.stopEvent(e);
break;
case e.KEY_DOWN_ARROW:
this._focusNextVisible(_be5);
dojo.event.browser.stopEvent(e);
break;
}
},onFocusTree:function(e){
if(!e.currentTarget){
return;
}
try{
var _be8=this.getWidgetByNode(e.currentTarget);
if(!_be8||!_be8.isTree){
return;
}
var _be9=this.getWidgetByNode(_be8.domNode.firstChild);
if(_be9&&_be9.isTreeNode){
if(_be8.lastFocused&&_be8.lastFocused.isTreeNode){
_be9=_be8.lastFocused;
}
this._focusLabel(_be9);
}
}
catch(e){
}
},onAfterTreeCreate:function(_bea){
var tree=_bea.source;
dojo.event.browser.addListener(tree.domNode,"onKey",dojo.lang.hitch(this,this.onKey));
dojo.event.browser.addListener(tree.domNode,"onmousedown",dojo.lang.hitch(this,this.onTreeMouseDown));
dojo.event.browser.addListener(tree.domNode,"onclick",dojo.lang.hitch(this,this.onTreeClick));
dojo.event.browser.addListener(tree.domNode,"onfocus",dojo.lang.hitch(this,this.onFocusTree));
tree.domNode.setAttribute("tabIndex","0");
if(tree.expandLevel){
this.expandToLevel(tree,tree.expandLevel);
}
if(tree.loadLevel){
this.loadToLevel(tree,tree.loadLevel);
}
},onTreeMouseDown:function(e){
},onTreeClick:function(e){
var _bee=e.target;
var node=this.domElement2TreeNode(_bee);
if(!node||!node.isTreeNode){
return;
}
var _bf0=function(el){
return el===node.expandNode;
};
if(this.checkPathCondition(_bee,_bf0)){
this.processExpandClick(node);
}
this._focusLabel(node);
},processExpandClick:function(node){
if(node.isExpanded){
this.collapse(node);
}else{
this.expand(node);
}
},batchExpandTimeout:20,expandAll:function(_bf3){
return this.expandToLevel(_bf3,Number.POSITIVE_INFINITY);
},collapseAll:function(_bf4){
var _bf5=this;
var _bf6=function(elem){
return (elem instanceof dojo.widget.Widget)&&elem.isFolder&&elem.isExpanded;
};
if(_bf4.isTreeNode){
this.processDescendants(_bf4,_bf6,this.collapse);
}else{
if(_bf4.isTree){
dojo.lang.forEach(_bf4.children,function(c){
_bf5.processDescendants(c,_bf6,_bf5.collapse);
});
}
}
},expandToNode:function(node,_bfa){
n=_bfa?node:node.parent;
s=[];
while(!n.isExpanded){
s.push(n);
n=n.parent;
}
dojo.lang.forEach(s,function(n){
n.expand();
});
},expandToLevel:function(_bfc,_bfd){
dojo.require("dojo.widget.TreeTimeoutIterator");
var _bfe=this;
var _bff=function(elem){
var res=elem.isFolder||elem.children&&elem.children.length;
return res;
};
var _c02=function(node,_c04){
_bfe.expand(node,true);
_c04.forward();
};
var _c05=new dojo.widget.TreeTimeoutIterator(_bfc,_c02,this);
_c05.setFilter(_bff);
_c05.timeout=this.batchExpandTimeout;
_c05.setMaxLevel(_bfc.isTreeNode?_bfd-1:_bfd);
return _c05.start(_bfc.isTreeNode);
},getWidgetByNode:function(node){
var _c07;
var _c08=node;
while(!(_c07=_c08.widgetId)){
_c08=_c08.parentNode;
if(_c08==null){
break;
}
}
if(_c07){
return dojo.widget.byId(_c07);
}else{
if(node==null){
return null;
}else{
return dojo.widget.manager.byNode(node);
}
}
},expand:function(node){
if(node.isFolder){
node.expand();
}
},collapse:function(node){
if(node.isFolder){
node.collapse();
}
},canEditLabel:function(node){
if(node.actionIsDisabledNow(node.actions.EDIT)){
return false;
}
return true;
},editLabelStart:function(node){
if(!this.canEditLabel(node)){
return false;
}
if(!this.editor.isClosed()){
this.editLabelFinish(this.editor.saveOnBlur);
}
this.doEditLabelStart(node);
},editLabelFinish:function(save){
this.doEditLabelFinish(save);
},doEditLabelStart:function(node){
if(!this.editor){
dojo.raise(this.widgetType+": no editor specified");
}
this.editor.open(node);
},doEditLabelFinish:function(save,_c10){
if(!this.editor){
dojo.raise(this.widgetType+": no editor specified");
}
var node=this.editor.node;
var _c12=this.editor.getContents();
this.editor.close(save);
if(save){
var data={title:_c12};
if(_c10){
dojo.lang.mixin(data,_c10);
}
if(node.isPhantom){
var _c14=node.parent;
var _c15=node.getParentIndex();
node.destroy();
dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(this,_c14,_c15,data);
}else{
var _c16=_c10&&_c10.title?_c10.title:_c12;
node.setTitle(_c16);
}
}else{
if(node.isPhantom){
node.destroy();
}
}
},makeDefaultNode:function(_c17,_c18){
var data={title:_c17.tree.defaultChildTitle};
return dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(this,_c17,_c18,data);
},runStages:function(_c1a,_c1b,make,_c1d,_c1e,args){
if(_c1a&&!_c1a.apply(this,args)){
return false;
}
if(_c1b&&!_c1b.apply(this,args)){
return false;
}
var _c20=make.apply(this,args);
if(_c1d){
_c1d.apply(this,args);
}
if(!_c20){
return _c20;
}
if(_c1e){
_c1e.apply(this,args);
}
return _c20;
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3,{createAndEdit:function(_c21,_c22){
var data={title:_c21.tree.defaultChildTitle};
if(!this.canCreateChild(_c21,_c22,data)){
return false;
}
var _c24=this.doCreateChild(_c21,_c22,data);
if(!_c24){
return false;
}
this.exposeCreateChild(_c21,_c22,data);
_c24.isPhantom=true;
if(!this.editor.isClosed()){
this.editLabelFinish(this.editor.saveOnBlur);
}
this.doEditLabelStart(_c24);
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3,{canClone:function(_c25,_c26,_c27,deep){
return true;
},clone:function(_c29,_c2a,_c2b,deep){
return this.runStages(this.canClone,this.prepareClone,this.doClone,this.finalizeClone,this.exposeClone,arguments);
},exposeClone:function(_c2d,_c2e){
if(_c2e.isTreeNode){
this.expand(_c2e);
}
},doClone:function(_c2f,_c30,_c31,deep){
var _c33=_c2f.clone(deep);
_c30.addChild(_c33,_c31);
return _c33;
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3,{canDetach:function(_c34){
if(_c34.actionIsDisabledNow(_c34.actions.DETACH)){
return false;
}
return true;
},detach:function(node){
return this.runStages(this.canDetach,this.prepareDetach,this.doDetach,this.finalizeDetach,this.exposeDetach,arguments);
},doDetach:function(node,_c37,_c38){
node.detach();
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3,{canDestroyChild:function(_c39){
if(_c39.parent&&!this.canDetach(_c39)){
return false;
}
return true;
},destroyChild:function(node){
return this.runStages(this.canDestroyChild,this.prepareDestroyChild,this.doDestroyChild,this.finalizeDestroyChild,this.exposeDestroyChild,arguments);
},doDestroyChild:function(node){
node.destroy();
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3,{canMoveNotANode:function(_c3c,_c3d){
if(_c3c.treeCanMove){
return _c3c.treeCanMove(_c3d);
}
return true;
},canMove:function(_c3e,_c3f){
if(!_c3e.isTreeNode){
return this.canMoveNotANode(_c3e,_c3f);
}
if(_c3e.actionIsDisabledNow(_c3e.actions.MOVE)){
return false;
}
if(_c3e.parent!==_c3f&&_c3f.actionIsDisabledNow(_c3f.actions.ADDCHILD)){
return false;
}
var node=_c3f;
while(node.isTreeNode){
if(node===_c3e){
return false;
}
node=node.parent;
}
return true;
},move:function(_c41,_c42,_c43){
return this.runStages(this.canMove,this.prepareMove,this.doMove,this.finalizeMove,this.exposeMove,arguments);
},doMove:function(_c44,_c45,_c46){
_c44.tree.move(_c44,_c45,_c46);
return true;
},exposeMove:function(_c47,_c48){
if(_c48.isTreeNode){
this.expand(_c48);
}
}});
dojo.lang.extend(dojo.widget.TreeBasicControllerV3,{canCreateChild:function(_c49,_c4a,data){
if(_c49.actionIsDisabledNow(_c49.actions.ADDCHILD)){
return false;
}
return true;
},createChild:function(_c4c,_c4d,data){
if(!data){
data={title:_c4c.tree.defaultChildTitle};
}
return this.runStages(this.canCreateChild,this.prepareCreateChild,this.doCreateChild,this.finalizeCreateChild,this.exposeCreateChild,[_c4c,_c4d,data]);
},prepareCreateChild:function(){
return true;
},finalizeCreateChild:function(){
},doCreateChild:function(_c4f,_c50,data){
var _c52=_c4f.tree.createNode(data);
_c4f.addChild(_c52,_c50);
return _c52;
},exposeCreateChild:function(_c53){
return this.expand(_c53);
}});
dojo.provide("dojo.Deferred");
dojo.Deferred=function(_c54){
this.chain=[];
this.id=this._nextId();
this.fired=-1;
this.paused=0;
this.results=[null,null];
this.canceller=_c54;
this.silentlyCancelled=false;
};
dojo.lang.extend(dojo.Deferred,{getFunctionFromArgs:function(){
var a=arguments;
if((a[0])&&(!a[1])){
if(dojo.lang.isFunction(a[0])){
return a[0];
}else{
if(dojo.lang.isString(a[0])){
return dj_global[a[0]];
}
}
}else{
if((a[0])&&(a[1])){
return dojo.lang.hitch(a[0],a[1]);
}
}
return null;
},makeCalled:function(){
var _c56=new dojo.Deferred();
_c56.callback();
return _c56;
},repr:function(){
var _c57;
if(this.fired==-1){
_c57="unfired";
}else{
if(this.fired==0){
_c57="success";
}else{
_c57="error";
}
}
return "Deferred("+this.id+", "+_c57+")";
},toString:dojo.lang.forward("repr"),_nextId:(function(){
var n=1;
return function(){
return n++;
};
})(),cancel:function(){
if(this.fired==-1){
if(this.canceller){
this.canceller(this);
}else{
this.silentlyCancelled=true;
}
if(this.fired==-1){
this.errback(new Error(this.repr()));
}
}else{
if((this.fired==0)&&(this.results[0] instanceof dojo.Deferred)){
this.results[0].cancel();
}
}
},_pause:function(){
this.paused++;
},_unpause:function(){
this.paused--;
if((this.paused==0)&&(this.fired>=0)){
this._fire();
}
},_continue:function(res){
this._resback(res);
this._unpause();
},_resback:function(res){
this.fired=((res instanceof Error)?1:0);
this.results[this.fired]=res;
this._fire();
},_check:function(){
if(this.fired!=-1){
if(!this.silentlyCancelled){
dojo.raise("already called!");
}
this.silentlyCancelled=false;
return;
}
},callback:function(res){
this._check();
this._resback(res);
},errback:function(res){
this._check();
if(!(res instanceof Error)){
res=new Error(res);
}
this._resback(res);
},addBoth:function(cb,cbfn){
var _c5f=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_c5f=dojo.lang.curryArguments(null,_c5f,arguments,2);
}
return this.addCallbacks(_c5f,_c5f);
},addCallback:function(cb,cbfn){
var _c62=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_c62=dojo.lang.curryArguments(null,_c62,arguments,2);
}
return this.addCallbacks(_c62,null);
},addErrback:function(cb,cbfn){
var _c65=this.getFunctionFromArgs(cb,cbfn);
if(arguments.length>2){
_c65=dojo.lang.curryArguments(null,_c65,arguments,2);
}
return this.addCallbacks(null,_c65);
return this.addCallbacks(null,cbfn);
},addCallbacks:function(cb,eb){
this.chain.push([cb,eb]);
if(this.fired>=0){
this._fire();
}
return this;
},_fire:function(){
var _c68=this.chain;
var _c69=this.fired;
var res=this.results[_c69];
var self=this;
var cb=null;
while(_c68.length>0&&this.paused==0){
var pair=_c68.shift();
var f=pair[_c69];
if(f==null){
continue;
}
try{
res=f(res);
_c69=((res instanceof Error)?1:0);
if(res instanceof dojo.Deferred){
cb=function(res){
self._continue(res);
};
this._pause();
}
}
catch(err){
_c69=1;
res=err;
}
}
this.fired=_c69;
this.results[_c69]=res;
if((cb)&&(this.paused)){
res.addBoth(cb);
}
}});
dojo.provide("dojo.DeferredList");
dojo.DeferredList=function(list,_c71,_c72,_c73,_c74){
this.list=list;
this.resultList=new Array(this.list.length);
this.chain=[];
this.id=this._nextId();
this.fired=-1;
this.paused=0;
this.results=[null,null];
this.canceller=_c74;
this.silentlyCancelled=false;
if(this.list.length===0&&!_c71){
this.callback(this.resultList);
}
this.finishedCount=0;
this.fireOnOneCallback=_c71;
this.fireOnOneErrback=_c72;
this.consumeErrors=_c73;
var _c75=0;
var _c76=this;
dojo.lang.forEach(this.list,function(d){
var _c78=_c75;
d.addCallback(function(r){
_c76._cbDeferred(_c78,true,r);
});
d.addErrback(function(r){
_c76._cbDeferred(_c78,false,r);
});
_c75++;
});
};
dojo.inherits(dojo.DeferredList,dojo.Deferred);
dojo.lang.extend(dojo.DeferredList,{_cbDeferred:function(_c7b,_c7c,_c7d){
this.resultList[_c7b]=[_c7c,_c7d];
this.finishedCount+=1;
if(this.fired!==0){
if(_c7c&&this.fireOnOneCallback){
this.callback([_c7b,_c7d]);
}else{
if(!_c7c&&this.fireOnOneErrback){
this.errback(_c7d);
}else{
if(this.finishedCount==this.list.length){
this.callback(this.resultList);
}
}
}
}
if(!_c7c&&this.consumeErrors){
_c7d=null;
}
return _c7d;
},gatherResults:function(_c7e){
var d=new dojo.DeferredList(_c7e,false,true,false);
d.addCallback(function(_c80){
var ret=[];
for(var i=0;i<_c80.length;i++){
ret.push(_c80[i][1]);
}
return ret;
});
return d;
}});
dojo.provide("dojo.widget.TreeLoadingControllerV3");
dojo.declare("dojo.Error",Error,function(_c83,_c84){
this.message=_c83;
this.extra=_c84;
this.stack=(new Error()).stack;
});
dojo.declare("dojo.CommunicationError",dojo.Error,function(){
this.name="CommunicationError";
});
dojo.declare("dojo.LockedError",dojo.Error,function(){
this.name="LockedError";
});
dojo.declare("dojo.FormatError",dojo.Error,function(){
this.name="FormatError";
});
dojo.declare("dojo.RpcError",dojo.Error,function(){
this.name="RpcError";
});
dojo.widget.defineWidget("dojo.widget.TreeLoadingControllerV3",dojo.widget.TreeBasicControllerV3,{RpcUrl:"",RpcActionParam:"action",preventCache:true,checkValidRpcResponse:function(type,obj){
if(type!="load"){
var _c87={};
for(var i=1;i<arguments.length;i++){
dojo.lang.mixin(_c87,arguments[i]);
}
return new dojo.CommunicationError(obj,_c87);
}
if(typeof obj!="object"){
return new dojo.FormatError("Wrong server answer format "+(obj&&obj.toSource?obj.toSource():obj)+" type "+(typeof obj),obj);
}
if(!dojo.lang.isUndefined(obj.error)){
return new dojo.RpcError(obj.error,obj);
}
return false;
},getDeferredBindHandler:function(_c89){
return dojo.lang.hitch(this,function(type,obj){
var _c8c=this.checkValidRpcResponse.apply(this,arguments);
if(_c8c){
_c89.errback(_c8c);
return;
}
_c89.callback(obj);
});
},getRpcUrl:function(_c8d){
if(this.RpcUrl=="local"){
var dir=document.location.href.substr(0,document.location.href.lastIndexOf("/"));
var _c8f=dir+"/local/"+_c8d;
return _c8f;
}
if(!this.RpcUrl){
dojo.raise("Empty RpcUrl: can't load");
}
var url=this.RpcUrl;
if(url.indexOf("/")!=0){
var _c91=document.location.href.replace(/:\/\/.*/,"");
var _c92=document.location.href.substring(_c91.length+3);
if(_c92.lastIndexOf("/")!=_c92.length-1){
_c92=_c92.replace(/\/[^\/]+$/,"/");
}
if(_c92.lastIndexOf("/")!=_c92.length-1){
_c92=_c92+"/";
}
url=_c91+"://"+_c92+url;
}
return url+(url.indexOf("?")>-1?"&":"?")+this.RpcActionParam+"="+_c8d;
},loadProcessResponse:function(node,_c94){
if(!dojo.lang.isArray(_c94)){
throw new dojo.FormatError("loadProcessResponse: Not array loaded: "+_c94);
}
node.setChildren(_c94);
},runRpc:function(kw){
var _c96=this;
var _c97=new dojo.Deferred();
dojo.io.bind({url:kw.url,handle:this.getDeferredBindHandler(_c97),mimetype:"text/javascript",preventCache:this.preventCache,sync:kw.sync,content:{data:dojo.json.serialize(kw.params)}});
return _c97;
},loadRemote:function(node,sync){
var _c9a=this;
var _c9b={node:this.getInfo(node),tree:this.getInfo(node.tree)};
var _c9c=this.runRpc({url:this.getRpcUrl("getChildren"),sync:sync,params:_c9b});
_c9c.addCallback(function(res){
return _c9a.loadProcessResponse(node,res);
});
return _c9c;
},batchExpandTimeout:0,recurseToLevel:function(_c9e,_c9f,_ca0,_ca1,_ca2,sync){
if(_c9f==0){
return;
}
if(!_ca2){
var _ca4=_ca0.call(_ca1,_c9e,sync);
}else{
var _ca4=dojo.Deferred.prototype.makeCalled();
}
var _ca5=this;
var _ca6=function(){
var _ca7=_c9e.children;
var _ca8=[];
for(var i=0;i<_ca7.length;i++){
_ca8.push(_ca5.recurseToLevel(_ca7[i],_c9f-1,_ca0,_ca1,sync));
}
return new dojo.DeferredList(_ca8);
};
_ca4.addCallback(_ca6);
return _ca4;
},expandToLevel:function(_caa,_cab,sync){
return this.recurseToLevel(_caa,_caa.isTree?_cab+1:_cab,this.expand,this,_caa.isTree,sync);
},loadToLevel:function(_cad,_cae,sync){
return this.recurseToLevel(_cad,_cad.isTree?_cae+1:_cae,this.loadIfNeeded,this,_cad.isTree,sync);
},loadAll:function(_cb0,sync){
return this.loadToLevel(_cb0,Number.POSITIVE_INFINITY,sync);
},expand:function(node,sync){
var _cb4=this;
var _cb5=this.startProcessing(node);
_cb5.addCallback(function(){
return _cb4.loadIfNeeded(node,sync);
});
_cb5.addCallback(function(res){
dojo.widget.TreeBasicControllerV3.prototype.expand(node);
return res;
});
_cb5.addBoth(function(res){
_cb4.finishProcessing(node);
return res;
});
return _cb5;
},loadIfNeeded:function(node,sync){
var _cba;
if(node.state==node.loadStates.UNCHECKED&&node.isFolder&&!node.children.length){
_cba=this.loadRemote(node,sync);
}else{
_cba=new dojo.Deferred();
_cba.callback();
}
return _cba;
},runStages:function(_cbb,_cbc,make,_cbe,_cbf,args){
var _cc1=this;
if(_cbb&&!_cbb.apply(this,args)){
return false;
}
var _cc2=dojo.Deferred.prototype.makeCalled();
if(_cbc){
_cc2.addCallback(function(){
return _cbc.apply(_cc1,args);
});
}
if(make){
_cc2.addCallback(function(){
var res=make.apply(_cc1,args);
return res;
});
}
if(_cbe){
_cc2.addBoth(function(res){
_cbe.apply(_cc1,args);
return res;
});
}
if(_cbf){
_cc2.addCallback(function(res){
_cbf.apply(_cc1,args);
return res;
});
}
return _cc2;
},startProcessing:function(_cc6){
var _cc7=new dojo.Deferred();
var _cc8=dojo.lang.isArray(_cc6)?_cc6:arguments;
for(var i=0;i<_cc8.length;i++){
if(_cc8[i].isLocked()){
_cc7.errback(new dojo.LockedError("item locked "+_cc8[i],_cc8[i]));
return _cc7;
}
if(_cc8[i].isTreeNode){
_cc8[i].markProcessing();
}
_cc8[i].lock();
}
_cc7.callback();
return _cc7;
},finishProcessing:function(_cca){
var _ccb=dojo.lang.isArray(_cca)?_cca:arguments;
for(var i=0;i<_ccb.length;i++){
if(!_ccb[i].hasLock()){
continue;
}
_ccb[i].unlock();
if(_ccb[i].isTreeNode){
_ccb[i].unmarkProcessing();
}
}
},refreshChildren:function(_ccd,sync){
return this.runStages(null,this.prepareRefreshChildren,this.doRefreshChildren,this.finalizeRefreshChildren,this.exposeRefreshChildren,arguments);
},prepareRefreshChildren:function(_ccf,sync){
var _cd1=this.startProcessing(_ccf);
_ccf.destroyChildren();
_ccf.state=_ccf.loadStates.UNCHECKED;
return _cd1;
},doRefreshChildren:function(_cd2,sync){
return this.loadRemote(_cd2,sync);
},finalizeRefreshChildren:function(_cd4,sync){
this.finishProcessing(_cd4);
},exposeRefreshChildren:function(_cd6,sync){
_cd6.expand();
},move:function(_cd8,_cd9,_cda){
return this.runStages(this.canMove,this.prepareMove,this.doMove,this.finalizeMove,this.exposeMove,arguments);
},doMove:function(_cdb,_cdc,_cdd){
_cdb.tree.move(_cdb,_cdc,_cdd);
return true;
},prepareMove:function(_cde,_cdf,_ce0,sync){
var _ce2=this.startProcessing(_cdf);
_ce2.addCallback(dojo.lang.hitch(this,function(){
return this.loadIfNeeded(_cdf,sync);
}));
return _ce2;
},finalizeMove:function(_ce3,_ce4){
this.finishProcessing(_ce4);
},prepareCreateChild:function(_ce5,_ce6,data,sync){
var _ce9=this.startProcessing(_ce5);
_ce9.addCallback(dojo.lang.hitch(this,function(){
return this.loadIfNeeded(_ce5,sync);
}));
return _ce9;
},finalizeCreateChild:function(_cea){
this.finishProcessing(_cea);
},prepareClone:function(_ceb,_cec,_ced,deep,sync){
var _cf0=this.startProcessing(_ceb,_cec);
_cf0.addCallback(dojo.lang.hitch(this,function(){
return this.loadIfNeeded(_cec,sync);
}));
return _cf0;
},finalizeClone:function(_cf1,_cf2){
this.finishProcessing(_cf1,_cf2);
}});
dojo.provide("dojo.widget.TreeRpcControllerV3");
dojo.widget.defineWidget("dojo.widget.TreeRpcControllerV3",dojo.widget.TreeLoadingControllerV3,{extraRpcOnEdit:false,doMove:function(_cf3,_cf4,_cf5,sync){
var _cf7={child:this.getInfo(_cf3),childTree:this.getInfo(_cf3.tree),oldParent:this.getInfo(_cf3.parent),oldParentTree:this.getInfo(_cf3.parent.tree),newParent:this.getInfo(_cf4),newParentTree:this.getInfo(_cf4.tree),newIndex:_cf5};
var _cf8=this.runRpc({url:this.getRpcUrl("move"),sync:sync,params:_cf7});
var _cf9=this;
var args=arguments;
_cf8.addCallback(function(){
dojo.widget.TreeBasicControllerV3.prototype.doMove.apply(_cf9,args);
});
return _cf8;
},prepareDetach:function(node,sync){
var _cfd=this.startProcessing(node);
return _cfd;
},finalizeDetach:function(node){
this.finishProcessing(node);
},doDetach:function(node,sync){
var _d01={node:this.getInfo(node),tree:this.getInfo(node.tree)};
var _d02=this.runRpc({url:this.getRpcUrl("detach"),sync:sync,params:_d01});
var _d03=this;
var args=arguments;
_d02.addCallback(function(){
dojo.widget.TreeBasicControllerV3.prototype.doDetach.apply(_d03,args);
});
return _d02;
},requestEditConfirmation:function(node,_d06,sync){
if(!this.extraRpcOnEdit){
return dojo.Deferred.prototype.makeCalled();
}
var _d08=this;
var _d09=this.startProcessing(node);
var _d0a={node:this.getInfo(node),tree:this.getInfo(node.tree)};
_d09.addCallback(function(){
return _d08.runRpc({url:_d08.getRpcUrl(_d06),sync:sync,params:_d0a});
});
_d09.addBoth(function(r){
_d08.finishProcessing(node);
return r;
});
return _d09;
},editLabelSave:function(node,_d0d,sync){
var _d0f=this.startProcessing(node);
var _d10=this;
var _d11={node:this.getInfo(node),tree:this.getInfo(node.tree),newContent:_d0d};
_d0f.addCallback(function(){
return _d10.runRpc({url:_d10.getRpcUrl("editLabelSave"),sync:sync,params:_d11});
});
_d0f.addBoth(function(r){
_d10.finishProcessing(node);
return r;
});
return _d0f;
},editLabelStart:function(node,sync){
if(!this.canEditLabel(node)){
return false;
}
var _d15=this;
if(!this.editor.isClosed()){
var _d16=this.editLabelFinish(this.editor.saveOnBlur,sync);
_d16.addCallback(function(){
return _d15.editLabelStart(node,sync);
});
return _d16;
}
var _d16=this.requestEditConfirmation(node,"editLabelStart",sync);
_d16.addCallback(function(){
_d15.doEditLabelStart(node);
});
return _d16;
},editLabelFinish:function(save,sync){
var _d19=this;
var node=this.editor.node;
var _d1b=dojo.Deferred.prototype.makeCalled();
if(!save&&!node.isPhantom){
_d1b=this.requestEditConfirmation(this.editor.node,"editLabelFinishCancel",sync);
}
if(save){
if(node.isPhantom){
_d1b=this.sendCreateChildRequest(node.parent,node.getParentIndex(),{title:this.editor.getContents()},sync);
}else{
_d1b=this.editLabelSave(node,this.editor.getContents(),sync);
}
}
_d1b.addCallback(function(_d1c){
_d19.doEditLabelFinish(save,_d1c);
});
_d1b.addErrback(function(r){
_d19.doEditLabelFinish(false);
return false;
});
return _d1b;
},createAndEdit:function(_d1e,_d1f,sync){
var data={title:_d1e.tree.defaultChildTitle};
if(!this.canCreateChild(_d1e,_d1f,data)){
return false;
}
if(!this.editor.isClosed()){
var _d22=this.editLabelFinish(this.editor.saveOnBlur,sync);
_d22.addCallback(function(){
return _d23.createAndEdit(_d1e,_d1f,sync);
});
return _d22;
}
var _d23=this;
var _d22=this.prepareCreateChild(_d1e,_d1f,data,sync);
_d22.addCallback(function(){
var _d24=_d23.makeDefaultNode(_d1e,_d1f);
_d24.isPhantom=true;
return _d24;
});
_d22.addBoth(function(r){
_d23.finalizeCreateChild(_d1e,_d1f,data,sync);
return r;
});
_d22.addCallback(function(_d26){
var d=_d23.exposeCreateChild(_d1e,_d1f,data,sync);
d.addCallback(function(){
return _d26;
});
return d;
});
_d22.addCallback(function(_d28){
_d23.doEditLabelStart(_d28);
return _d28;
});
return _d22;
},prepareDestroyChild:function(node,sync){
var _d2b=this.startProcessing(node);
return _d2b;
},finalizeDestroyChild:function(node){
this.finishProcessing(node);
},doDestroyChild:function(node,sync){
var _d2f={node:this.getInfo(node),tree:this.getInfo(node.tree)};
var _d30=this.runRpc({url:this.getRpcUrl("destroyChild"),sync:sync,params:_d2f});
var _d31=this;
var args=arguments;
_d30.addCallback(function(){
dojo.widget.TreeBasicControllerV3.prototype.doDestroyChild.apply(_d31,args);
});
return _d30;
},sendCreateChildRequest:function(_d33,_d34,data,sync){
var _d37={tree:this.getInfo(_d33.tree),parent:this.getInfo(_d33),index:_d34,data:data};
var _d38=this.runRpc({url:this.getRpcUrl("createChild"),sync:sync,params:_d37});
return _d38;
},doCreateChild:function(_d39,_d3a,data,sync){
if(dojo.lang.isUndefined(data.title)){
data.title=_d39.tree.defaultChildTitle;
}
var _d3d=this.sendCreateChildRequest(_d39,_d3a,data,sync);
var _d3e=this;
var args=arguments;
_d3d.addCallback(function(_d40){
dojo.lang.mixin(data,_d40);
return dojo.widget.TreeBasicControllerV3.prototype.doCreateChild.call(_d3e,_d39,_d3a,data);
});
return _d3d;
},doClone:function(_d41,_d42,_d43,deep,sync){
var _d46={child:this.getInfo(_d41),oldParent:this.getInfo(_d41.parent),oldParentTree:this.getInfo(_d41.parent.tree),newParent:this.getInfo(_d42),newParentTree:this.getInfo(_d42.tree),index:_d43,deep:deep?true:false,tree:this.getInfo(_d41.tree)};
var _d47=this.runRpc({url:this.getRpcUrl("clone"),sync:sync,params:_d46});
var _d48=this;
var args=arguments;
_d47.addCallback(function(){
dojo.widget.TreeBasicControllerV3.prototype.doClone.apply(_d48,args);
});
return _d47;
}});
dojo.provide("dojo.widget.TreeExtension");
dojo.widget.defineWidget("dojo.widget.TreeExtension",[dojo.widget.HtmlWidget,dojo.widget.TreeCommon],function(){
this.listenedTrees={};
},{});
dojo.provide("dojo.widget.TreeDocIconExtension");
dojo.widget.defineWidget("dojo.widget.TreeDocIconExtension",[dojo.widget.TreeExtension],{templateCssPath:dojo.uri.dojoUri("src/widget/templates/TreeDocIcon.css"),listenTreeEvents:["afterChangeTree","afterSetFolder","afterUnsetFolder"],listenNodeFilter:function(elem){
return elem instanceof dojo.widget.Widget;
},getnodeDocType:function(node){
var _d4c=node.getnodeDocType();
if(!_d4c){
_d4c=node.isFolder?"Folder":"Document";
}
return _d4c;
},setnodeDocTypeClass:function(node){
var reg=new RegExp("(^|\\s)"+node.tree.classPrefix+"Icon\\w+","g");
var _d4f=dojo.html.getClass(node.iconNode).replace(reg,"")+" "+node.tree.classPrefix+"Icon"+this.getnodeDocType(node);
dojo.html.setClass(node.iconNode,_d4f);
},onAfterSetFolder:function(_d50){
if(_d50.source.iconNode){
this.setnodeDocTypeClass(_d50.source);
}
},onAfterUnsetFolder:function(_d51){
this.setnodeDocTypeClass(_d51.source);
},listenNode:function(node){
node.contentIconNode=document.createElement("div");
var _d53=node.tree.classPrefix+"IconContent";
if(dojo.render.html.ie){
_d53=_d53+" "+node.tree.classPrefix+"IEIconContent";
}
dojo.html.setClass(node.contentIconNode,_d53);
node.contentNode.parentNode.replaceChild(node.contentIconNode,node.expandNode);
node.iconNode=document.createElement("div");
dojo.html.setClass(node.iconNode,node.tree.classPrefix+"Icon"+" "+node.tree.classPrefix+"Icon"+this.getnodeDocType(node));
node.contentIconNode.appendChild(node.expandNode);
node.contentIconNode.appendChild(node.iconNode);
dojo.dom.removeNode(node.contentNode);
node.contentIconNode.appendChild(node.contentNode);
if(node.noPortalLogin){
dojo.html.setStyle(node.contentNode,'color','red');
}
},onAfterChangeTree:function(_d54){
var _d55=this;
if(!_d54.oldTree||!this.listenedTrees[_d54.oldTree.widgetId]){
this.processDescendants(_d54.node,this.listenNodeFilter,this.listenNode);
}
}});
dojo.provide("dojo.widget.html.stabile");
dojo.widget.html.stabile={_sqQuotables:new RegExp("([\\\\'])","g"),_depth:0,_recur:false,depthLimit:2};
dojo.widget.html.stabile.getState=function(id){
dojo.widget.html.stabile.setup();
return dojo.widget.html.stabile.widgetState[id];
};
dojo.widget.html.stabile.setState=function(id,_d58,_d59){
dojo.widget.html.stabile.setup();
dojo.widget.html.stabile.widgetState[id]=_d58;
if(_d59){
dojo.widget.html.stabile.commit(dojo.widget.html.stabile.widgetState);
}
};
dojo.widget.html.stabile.setup=function(){
if(!dojo.widget.html.stabile.widgetState){
var text=dojo.widget.html.stabile._getStorage().value;
dojo.widget.html.stabile.widgetState=text?dj_eval("("+text+")"):{};
}
};
dojo.widget.html.stabile.commit=function(_d5b){
dojo.widget.html.stabile._getStorage().value=dojo.widget.html.stabile.description(_d5b);
};
dojo.widget.html.stabile.description=function(v,_d5d){
var _d5e=dojo.widget.html.stabile._depth;
var _d5f=function(){
return this.description(this,true);
};
try{
if(v===void (0)){
return "undefined";
}
if(v===null){
return "null";
}
if(typeof (v)=="boolean"||typeof (v)=="number"||v instanceof Boolean||v instanceof Number){
return v.toString();
}
if(typeof (v)=="string"||v instanceof String){
var v1=v.replace(dojo.widget.html.stabile._sqQuotables,"\\$1");
v1=v1.replace(/\n/g,"\\n");
v1=v1.replace(/\r/g,"\\r");
return "'"+v1+"'";
}
if(v instanceof Date){
return "new Date("+d.getFullYear+","+d.getMonth()+","+d.getDate()+")";
}
var d;
if(v instanceof Array||v.push){
if(_d5e>=dojo.widget.html.stabile.depthLimit){
return "[ ... ]";
}
d="[";
var _d62=true;
dojo.widget.html.stabile._depth++;
for(var i=0;i<v.length;i++){
if(_d62){
_d62=false;
}else{
d+=",";
}
d+=arguments.callee(v[i],_d5d);
}
return d+"]";
}
if(v.constructor==Object||v.toString==_d5f){
if(_d5e>=dojo.widget.html.stabile.depthLimit){
return "{ ... }";
}
if(typeof (v.hasOwnProperty)!="function"&&v.prototype){
throw new Error("description: "+v+" not supported by script engine");
}
var _d62=true;
d="{";
dojo.widget.html.stabile._depth++;
for(var key in v){
if(v[key]==void (0)||typeof (v[key])=="function"){
continue;
}
if(_d62){
_d62=false;
}else{
d+=", ";
}
var kd=key;
if(!kd.match(/^[a-zA-Z_][a-zA-Z0-9_]*$/)){
kd=arguments.callee(key,_d5d);
}
d+=kd+": "+arguments.callee(v[key],_d5d);
}
return d+"}";
}
if(_d5d){
if(dojo.widget.html.stabile._recur){
var _d66=Object.prototype.toString;
return _d66.apply(v,[]);
}else{
dojo.widget.html.stabile._recur=true;
return v.toString();
}
}else{
throw new Error("Unknown type: "+v);
return "'unknown'";
}
}
finally{
dojo.widget.html.stabile._depth=_d5e;
}
};
dojo.widget.html.stabile._getStorage=function(){
if(dojo.widget.html.stabile.dataField){
return dojo.widget.html.stabile.dataField;
}
var form=document.forms._dojo_form;
return dojo.widget.html.stabile.dataField=form?form.stabile:{value:""};
};
dojo.provide("dojo.widget.ComboBox");
dojo.declare("dojo.widget.incrementalComboBoxDataProvider",null,function(_d68){
this.searchUrl=_d68.dataUrl;
this._cache={};
this._inFlight=false;
this._lastRequest=null;
this.allowCache=false;
},{_addToCache:function(_d69,data){
if(this.allowCache){
this._cache[_d69]=data;
}
},startSearch:function(_d6b,_d6c){
if(this._inFlight){
}
var tss=encodeURIComponent(_d6b);
var _d6e=dojo.string.substituteParams(this.searchUrl,{"searchString":tss});
var _d6f=this;
var _d70=this._lastRequest=dojo.io.bind({url:_d6e,method:"get",mimetype:"text/json",load:function(type,data,evt){
_d6f._inFlight=false;
if(!dojo.lang.isArray(data)){
var _d74=[];
for(var key in data){
_d74.push([data[key],key]);
}
data=_d74;
}
_d6f._addToCache(_d6b,data);
if(_d70==_d6f._lastRequest){
_d6c(data);
}
}});
this._inFlight=true;
}});
dojo.declare("dojo.widget.basicComboBoxDataProvider",null,function(_d76,node){
this._data=[];
this.searchLimit=30;
this.searchType="STARTSTRING";
this.caseSensitive=false;
if(!dj_undef("dataUrl",_d76)&&!dojo.string.isBlank(_d76.dataUrl)){
this._getData(_d76.dataUrl);
}else{
if((node)&&(node.nodeName.toLowerCase()=="select")){
var opts=node.getElementsByTagName("option");
var ol=opts.length;
var data=[];
for(var x=0;x<ol;x++){
var text=opts[x].textContent||opts[x].innerText||opts[x].innerHTML;
var _d7d=[String(text),String(opts[x].value)];
data.push(_d7d);
if(opts[x].selected){
_d76.setAllValues(_d7d[0],_d7d[1]);
}
}
this.setData(data);
}
}
},{_getData:function(url){
dojo.io.bind({url:url,load:dojo.lang.hitch(this,function(type,data,evt){
if(!dojo.lang.isArray(data)){
var _d82=[];
for(var key in data){
_d82.push([data[key],key]);
}
data=_d82;
}
this.setData(data);
}),mimetype:"text/json"});
},startSearch:function(_d84,_d85){
this._performSearch(_d84,_d85);
},_performSearch:function(_d86,_d87){
var st=this.searchType;
var ret=[];
if(!this.caseSensitive){
_d86=_d86.toLowerCase();
}
for(var x=0;x<this._data.length;x++){
if((this.searchLimit>0)&&(ret.length>=this.searchLimit)){
break;
}
var _d8b=new String((!this.caseSensitive)?this._data[x][0].toLowerCase():this._data[x][0]);
if(_d8b.length<_d86.length){
continue;
}
if(st=="STARTSTRING"){
if(_d86==_d8b.substr(0,_d86.length)){
ret.push(this._data[x]);
}
}else{
if(st=="SUBSTRING"){
if(_d8b.indexOf(_d86)>=0){
ret.push(this._data[x]);
}
}else{
if(st=="STARTWORD"){
var idx=_d8b.indexOf(_d86);
if(idx==0){
ret.push(this._data[x]);
}
if(idx<=0){
continue;
}
var _d8d=false;
while(idx!=-1){
if(" ,/(".indexOf(_d8b.charAt(idx-1))!=-1){
_d8d=true;
break;
}
idx=_d8b.indexOf(_d86,idx+1);
}
if(!_d8d){
continue;
}else{
ret.push(this._data[x]);
}
}
}
}
}
_d87(ret);
},setData:function(_d8e){
this._data=_d8e;
}});
dojo.widget.defineWidget("dojo.widget.ComboBox",dojo.widget.HtmlWidget,{forceValidOption:false,searchType:"stringstart",dataProvider:null,autoComplete:true,searchDelay:100,dataUrl:"",fadeTime:200,maxListLength:8,mode:"local",selectedResult:null,dataProviderClass:"",buttonSrc:dojo.uri.dojoUri("src/widget/templates/images/combo_box_arrow.png"),dropdownToggle:"fade",templatePath:dojo.uri.dojoUri("src/widget/templates/ComboBox.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/ComboBox.css"),setValue:function(_d8f){
this.comboBoxValue.value=_d8f;
if(this.textInputNode.value!=_d8f){
this.textInputNode.value=_d8f;
dojo.widget.html.stabile.setState(this.widgetId,this.getState(),true);
this.onValueChanged(_d8f);
}
},onValueChanged:function(_d90){
},getValue:function(){
return this.comboBoxValue.value;
},getState:function(){
return {value:this.getValue()};
},setState:function(_d91){
this.setValue(_d91.value);
},enable:function(){
this.disabled=false;
this.textInputNode.removeAttribute("disabled");
},disable:function(){
this.disabled=true;
this.textInputNode.setAttribute("disabled",true);
},_getCaretPos:function(_d92){
if(dojo.lang.isNumber(_d92.selectionStart)){
return _d92.selectionStart;
}else{
if(dojo.render.html.ie){
var tr=document.selection.createRange().duplicate();
var ntr=_d92.createTextRange();
tr.move("character",0);
ntr.move("character",0);
try{
ntr.setEndPoint("EndToEnd",tr);
return String(ntr.text).replace(/\r/g,"").length;
}
catch(e){
return 0;
}
}
}
},_setCaretPos:function(_d95,_d96){
_d96=parseInt(_d96);
this._setSelectedRange(_d95,_d96,_d96);
},_setSelectedRange:function(_d97,_d98,end){
if(!end){
end=_d97.value.length;
}
if(_d97.setSelectionRange){
_d97.focus();
_d97.setSelectionRange(_d98,end);
}else{
if(_d97.createTextRange){
var _d9a=_d97.createTextRange();
with(_d9a){
collapse(true);
moveEnd("character",end);
moveStart("character",_d98);
select();
}
}else{
_d97.value=_d97.value;
_d97.blur();
_d97.focus();
var dist=parseInt(_d97.value.length)-end;
var _d9c=String.fromCharCode(37);
var tcc=_d9c.charCodeAt(0);
for(var x=0;x<dist;x++){
var te=document.createEvent("KeyEvents");
te.initKeyEvent("keypress",true,true,null,false,false,false,false,tcc,tcc);
_d97.dispatchEvent(te);
}
}
}
},_handleKeyEvents:function(evt){
if(evt.ctrlKey||evt.altKey||!evt.key){
return;
}
this._prev_key_backspace=false;
this._prev_key_esc=false;
var k=dojo.event.browser.keys;
var _da2=true;
switch(evt.key){
case k.KEY_DOWN_ARROW:
if(!this.popupWidget.isShowingNow){
this._startSearchFromInput();
}
this._highlightNextOption();
dojo.event.browser.stopEvent(evt);
return;
case k.KEY_UP_ARROW:
this._highlightPrevOption();
dojo.event.browser.stopEvent(evt);
return;
case k.KEY_TAB:
if(!this.autoComplete&&this.popupWidget.isShowingNow&&this._highlighted_option){
dojo.event.browser.stopEvent(evt);
this._selectOption({"target":this._highlighted_option,"noHide":false});
this._setSelectedRange(this.textInputNode,this.textInputNode.value.length,null);
}else{
this._selectOption();
return;
}
break;
case k.KEY_ENTER:
if(this.popupWidget.isShowingNow){
dojo.event.browser.stopEvent(evt);
}
if(this.autoComplete){
this._selectOption();
return;
}
case " ":
if(this.popupWidget.isShowingNow&&this._highlighted_option){
dojo.event.browser.stopEvent(evt);
this._selectOption();
this._hideResultList();
return;
}
break;
case k.KEY_ESCAPE:
this._hideResultList();
this._prev_key_esc=true;
return;
case k.KEY_BACKSPACE:
this._prev_key_backspace=true;
if(!this.textInputNode.value.length){
this.setAllValues("","");
this._hideResultList();
_da2=false;
}
break;
case k.KEY_RIGHT_ARROW:
case k.KEY_LEFT_ARROW:
_da2=false;
break;
default:
if(evt.charCode==0){
_da2=false;
}
}
if(this.searchTimer){
clearTimeout(this.searchTimer);
}
if(_da2){
this._blurOptionNode();
this.searchTimer=setTimeout(dojo.lang.hitch(this,this._startSearchFromInput),this.searchDelay);
}
},compositionEnd:function(evt){
evt.key=evt.keyCode;
this._handleKeyEvents(evt);
},onKeyUp:function(evt){
this.setValue(this.textInputNode.value);
},setSelectedValue:function(_da5){
this.comboBoxSelectionValue.value=_da5;
},setAllValues:function(_da6,_da7){
this.setSelectedValue(_da7);
this.setValue(_da6);
},_focusOptionNode:function(node){
if(this._highlighted_option!=node){
this._blurOptionNode();
this._highlighted_option=node;
dojo.html.addClass(this._highlighted_option,"dojoComboBoxItemHighlight");
}
},_blurOptionNode:function(){
if(this._highlighted_option){
dojo.html.removeClass(this._highlighted_option,"dojoComboBoxItemHighlight");
this._highlighted_option=null;
}
},_highlightNextOption:function(){
if((!this._highlighted_option)||!this._highlighted_option.parentNode){
this._focusOptionNode(this.optionsListNode.firstChild);
}else{
if(this._highlighted_option.nextSibling){
this._focusOptionNode(this._highlighted_option.nextSibling);
}
}
dojo.html.scrollIntoView(this._highlighted_option);
},_highlightPrevOption:function(){
if(this._highlighted_option&&this._highlighted_option.previousSibling){
this._focusOptionNode(this._highlighted_option.previousSibling);
}else{
this._highlighted_option=null;
this._hideResultList();
return;
}
dojo.html.scrollIntoView(this._highlighted_option);
},_itemMouseOver:function(evt){
if(evt.target===this.optionsListNode){
return;
}
this._focusOptionNode(evt.target);
dojo.html.addClass(this._highlighted_option,"dojoComboBoxItemHighlight");
},_itemMouseOut:function(evt){
if(evt.target===this.optionsListNode){
return;
}
this._blurOptionNode();
},onResize:function(){
var _dab=dojo.html.getContentBox(this.textInputNode);
if(_dab.height<=0){
dojo.lang.setTimeout(this,"onResize",100);
return;
}
var _dac={width:_dab.height,height:_dab.height};
dojo.html.setContentBox(this.downArrowNode,_dac);
},fillInTemplate:function(args,frag){
dojo.html.applyBrowserClass(this.domNode);
var _daf=this.getFragNodeRef(frag);
if(!this.name&&_daf.name){
this.name=_daf.name;
}
this.comboBoxValue.name=this.name;
this.comboBoxSelectionValue.name=this.name+"_selected";
dojo.html.copyStyle(this.domNode,_daf);
dojo.html.copyStyle(this.textInputNode,_daf);
dojo.html.copyStyle(this.downArrowNode,_daf);
with(this.downArrowNode.style){
width="0px";
height="0px";
}
var _db0;
if(this.dataProviderClass){
if(typeof this.dataProviderClass=="string"){
_db0=dojo.evalObjPath(this.dataProviderClass);
}else{
_db0=this.dataProviderClass;
}
}else{
if(this.mode=="remote"){
_db0=dojo.widget.incrementalComboBoxDataProvider;
}else{
_db0=dojo.widget.basicComboBoxDataProvider;
}
}
this.dataProvider=new _db0(this,this.getFragNodeRef(frag));
this.popupWidget=new dojo.widget.createWidget("PopupContainer",{toggle:this.dropdownToggle,toggleDuration:this.toggleDuration});
dojo.event.connect(this,"destroy",this.popupWidget,"destroy");
this.optionsListNode=this.popupWidget.domNode;
this.domNode.appendChild(this.optionsListNode);
dojo.html.addClass(this.optionsListNode,"dojoComboBoxOptions");
dojo.event.connect(this.optionsListNode,"onclick",this,"_selectOption");
dojo.event.connect(this.optionsListNode,"onmouseover",this,"_onMouseOver");
dojo.event.connect(this.optionsListNode,"onmouseout",this,"_onMouseOut");
dojo.event.connect(this.optionsListNode,"onmouseover",this,"_itemMouseOver");
dojo.event.connect(this.optionsListNode,"onmouseout",this,"_itemMouseOut");
},_openResultList:function(_db1){
if(this.disabled){
return;
}
this._clearResultList();
if(!_db1.length){
this._hideResultList();
}
if((this.autoComplete)&&(_db1.length)&&(!this._prev_key_backspace)&&(this.textInputNode.value.length>0)){
var cpos=this._getCaretPos(this.textInputNode);
if((cpos+1)>this.textInputNode.value.length){
this.textInputNode.value+=_db1[0][0].substr(cpos);
this._setSelectedRange(this.textInputNode,cpos,this.textInputNode.value.length);
}
}
var even=true;
while(_db1.length){
var tr=_db1.shift();
if(tr){
var td=document.createElement("div");
td.appendChild(document.createTextNode(tr[0]));
td.setAttribute("resultName",tr[0]);
td.setAttribute("resultValue",tr[1]);
td.className="dojoComboBoxItem "+((even)?"dojoComboBoxItemEven":"dojoComboBoxItemOdd");
even=(!even);
this.optionsListNode.appendChild(td);
}
}
this._showResultList();
},_onFocusInput:function(){
this._hasFocus=true;
},_onBlurInput:function(){
this._hasFocus=false;
this._handleBlurTimer(true,500);
},_handleBlurTimer:function(_db6,_db7){
if(this.blurTimer&&(_db6||_db7)){
clearTimeout(this.blurTimer);
}
if(_db7){
this.blurTimer=dojo.lang.setTimeout(this,"_checkBlurred",_db7);
}
},_onMouseOver:function(evt){
if(!this._mouseover_list){
this._handleBlurTimer(true,0);
this._mouseover_list=true;
}
},_onMouseOut:function(evt){
var _dba=evt.relatedTarget;
try{
if(!_dba||_dba.parentNode!=this.optionsListNode){
this._mouseover_list=false;
this._handleBlurTimer(true,100);
this._tryFocus();
}
}
catch(e){
}
},_isInputEqualToResult:function(_dbb){
var _dbc=this.textInputNode.value;
if(!this.dataProvider.caseSensitive){
_dbc=_dbc.toLowerCase();
_dbb=_dbb.toLowerCase();
}
return (_dbc==_dbb);
},_isValidOption:function(){
var tgt=dojo.html.firstElement(this.optionsListNode);
var _dbe=false;
while(!_dbe&&tgt){
if(this._isInputEqualToResult(tgt.getAttribute("resultName"))){
_dbe=true;
}else{
tgt=dojo.html.nextElement(tgt);
}
}
return _dbe;
},_checkBlurred:function(){
if(!this._hasFocus&&!this._mouseover_list){
this._hideResultList();
if(!this.textInputNode.value.length){
this.setAllValues("","");
return;
}
var _dbf=this._isValidOption();
if(this.forceValidOption&&!_dbf){
this.setAllValues("","");
return;
}
if(!_dbf){
this.setSelectedValue("");
}
}
},_selectOption:function(evt){
var tgt=null;
if(!evt){
evt={target:this._highlighted_option};
}
if(!dojo.html.isDescendantOf(evt.target,this.optionsListNode)){
if(!this.textInputNode.value.length){
return;
}
tgt=dojo.html.firstElement(this.optionsListNode);
if(!tgt||!this._isInputEqualToResult(tgt.getAttribute("resultName"))){
return;
}
}else{
tgt=evt.target;
}
while((tgt.nodeType!=1)||(!tgt.getAttribute("resultName"))){
tgt=tgt.parentNode;
if(tgt===dojo.body()){
return false;
}
}
this.selectedResult=[tgt.getAttribute("resultName"),tgt.getAttribute("resultValue")];
this.setAllValues(tgt.getAttribute("resultName"),tgt.getAttribute("resultValue"));
if(!evt.noHide){
this._hideResultList();
this._setSelectedRange(this.textInputNode,0,null);
}
this._tryFocus();
},_clearResultList:function(){
if(this.optionsListNode.innerHTML){
this.optionsListNode.innerHTML="";
}
},_hideResultList:function(){
this.popupWidget.close();
},_showResultList:function(){
var _dc2=this.optionsListNode.childNodes;
if(_dc2.length){
var _dc3=Math.min(_dc2.length,this.maxListLength);
with(this.optionsListNode.style){
display="";
if(_dc3==_dc2.length){
height="";
}else{
height=_dc3*dojo.html.getMarginBox(_dc2[0]).height+"px";
}
width=(dojo.html.getMarginBox(this.domNode).width-2)+"px";
}
this.popupWidget.open(this.domNode,this,this.downArrowNode);
}else{
this._hideResultList();
}
},handleArrowClick:function(){
this._handleBlurTimer(true,0);
this._tryFocus();
if(this.popupWidget.isShowingNow){
this._hideResultList();
}else{
this._startSearch("");
}
},_tryFocus:function(){
try{
this.textInputNode.focus();
}
catch(e){
}
},_startSearchFromInput:function(){
this._startSearch(this.textInputNode.value);
},_startSearch:function(key){
this.dataProvider.startSearch(key,dojo.lang.hitch(this,"_openResultList"));
},postCreate:function(){
this.onResize();
dojo.event.connect(this.textInputNode,"onblur",this,"_onBlurInput");
dojo.event.connect(this.textInputNode,"onfocus",this,"_onFocusInput");
if(this.disabled){
this.disable();
}
var s=dojo.widget.html.stabile.getState(this.widgetId);
if(s){
this.setState(s);
}
}});
dojo.provide("dojo.widget.Select");
dojo.widget.defineWidget("dojo.widget.Select",dojo.widget.ComboBox,{forceValidOption:true,setValue:function(_dc6){
this.comboBoxValue.value=_dc6;
dojo.widget.html.stabile.setState(this.widgetId,this.getState(),true);
this.onValueChanged(_dc6);
},setLabel:function(_dc7){
this.comboBoxSelectionValue.value=_dc7;
if(this.textInputNode.value!=_dc7){
this.textInputNode.value=_dc7;
}
},getLabel:function(){
return this.comboBoxSelectionValue.value;
},getState:function(){
return {value:this.getValue(),label:this.getLabel()};
},onKeyUp:function(evt){
this.setLabel(this.textInputNode.value);
},setState:function(_dc9){
this.setValue(_dc9.value);
this.setLabel(_dc9.label);
},setAllValues:function(_dca,_dcb){
this.setLabel(_dca);
this.setValue(_dcb);
}});
dojo.provide("dojo.widget.DropdownContainer");
dojo.widget.defineWidget("dojo.widget.DropdownContainer",dojo.widget.HtmlWidget,{inputWidth:"7em",id:"",inputId:"",inputName:"",iconURL:dojo.uri.dojoUri("src/widget/templates/images/combo_box_arrow.png"),copyClasses:false,iconAlt:"",containerToggle:"plain",containerToggleDuration:150,templateString:"<span style=\"white-space:nowrap\"><input type=\"hidden\" name=\"\" value=\"\" dojoAttachPoint=\"valueNode\" /><input name=\"\" type=\"text\" value=\"\" style=\"vertical-align:middle;\" dojoAttachPoint=\"inputNode\" autocomplete=\"off\" /> <img src=\"${this.iconURL}\" alt=\"${this.iconAlt}\" dojoAttachEvent=\"onclick:onIconClick\" dojoAttachPoint=\"buttonNode\" style=\"vertical-align:middle; cursor:pointer; cursor:hand\" /></span>",templateCssPath:"",isContainer:true,attachTemplateNodes:function(){
dojo.widget.DropdownContainer.superclass.attachTemplateNodes.apply(this,arguments);
this.popup=dojo.widget.createWidget("PopupContainer",{toggle:this.containerToggle,toggleDuration:this.containerToggleDuration});
this.containerNode=this.popup.domNode;
},fillInTemplate:function(args,frag){
this.domNode.appendChild(this.popup.domNode);
if(this.id){
this.domNode.id=this.id;
}
if(this.inputId){
this.inputNode.id=this.inputId;
}
if(this.inputName){
this.inputNode.name=this.inputName;
}
this.inputNode.style.width=this.inputWidth;
this.inputNode.disabled=this.disabled;
if(this.copyClasses){
this.inputNode.style="";
this.inputNode.className=this.getFragNodeRef(frag).className;
}
dojo.event.connect(this.inputNode,"onchange",this,"onInputChange");
},onIconClick:function(evt){
if(this.disabled){
return;
}
if(!this.popup.isShowingNow){
this.popup.open(this.inputNode,this,this.buttonNode);
}else{
this.popup.close();
}
},hideContainer:function(){
if(this.popup.isShowingNow){
this.popup.close();
}
},onInputChange:function(){
},enable:function(){
this.inputNode.disabled=false;
dojo.widget.DropdownContainer.superclass.enable.apply(this,arguments);
},disable:function(){
this.inputNode.disabled=true;
dojo.widget.DropdownContainer.superclass.disable.apply(this,arguments);
}});
dojo.provide("dojo.date.common");
dojo.date.setDayOfYear=function(_dcf,_dd0){
_dcf.setMonth(0);
_dcf.setDate(_dd0);
return _dcf;
};
dojo.date.getDayOfYear=function(_dd1){
var _dd2=_dd1.getFullYear();
var _dd3=new Date(_dd2-1,11,31);
return Math.floor((_dd1.getTime()-_dd3.getTime())/86400000);
};
dojo.date.setWeekOfYear=function(_dd4,week,_dd6){
if(arguments.length==1){
_dd6=0;
}
dojo.unimplemented("dojo.date.setWeekOfYear");
};
dojo.date.getWeekOfYear=function(_dd7,_dd8){
if(arguments.length==1){
_dd8=0;
}
var _dd9=new Date(_dd7.getFullYear(),0,1);
var day=_dd9.getDay();
_dd9.setDate(_dd9.getDate()-day+_dd8-(day>_dd8?7:0));
return Math.floor((_dd7.getTime()-_dd9.getTime())/604800000);
};
dojo.date.setIsoWeekOfYear=function(_ddb,week,_ddd){
if(arguments.length==1){
_ddd=1;
}
dojo.unimplemented("dojo.date.setIsoWeekOfYear");
};
dojo.date.getIsoWeekOfYear=function(_dde,_ddf){
if(arguments.length==1){
_ddf=1;
}
dojo.unimplemented("dojo.date.getIsoWeekOfYear");
};
dojo.date.shortTimezones=["IDLW","BET","HST","MART","AKST","PST","MST","CST","EST","AST","NFT","BST","FST","AT","GMT","CET","EET","MSK","IRT","GST","AFT","AGTT","IST","NPT","ALMT","MMT","JT","AWST","JST","ACST","AEST","LHST","VUT","NFT","NZT","CHAST","PHOT","LINT"];
dojo.date.timezoneOffsets=[-720,-660,-600,-570,-540,-480,-420,-360,-300,-240,-210,-180,-120,-60,0,60,120,180,210,240,270,300,330,345,360,390,420,480,540,570,600,630,660,690,720,765,780,840];
dojo.date.getDaysInMonth=function(_de0){
var _de1=_de0.getMonth();
var days=[31,28,31,30,31,30,31,31,30,31,30,31];
if(_de1==1&&dojo.date.isLeapYear(_de0)){
return 29;
}else{
return days[_de1];
}
};
dojo.date.isLeapYear=function(_de3){
var year=_de3.getFullYear();
return (year%400==0)?true:(year%100==0)?false:(year%4==0)?true:false;
};
dojo.date.getTimezoneName=function(_de5){
var str=_de5.toString();
var tz="";
var _de8;
var pos=str.indexOf("(");
if(pos>-1){
pos++;
tz=str.substring(pos,str.indexOf(")"));
}else{
var pat=/([A-Z\/]+) \d{4}$/;
if((_de8=str.match(pat))){
tz=_de8[1];
}else{
str=_de5.toLocaleString();
pat=/ ([A-Z\/]+)$/;
if((_de8=str.match(pat))){
tz=_de8[1];
}
}
}
return tz=="AM"||tz=="PM"?"":tz;
};
dojo.date.getOrdinal=function(_deb){
var date=_deb.getDate();
if(date%100!=11&&date%10==1){
return "st";
}else{
if(date%100!=12&&date%10==2){
return "nd";
}else{
if(date%100!=13&&date%10==3){
return "rd";
}else{
return "th";
}
}
}
};
dojo.date.compareTypes={DATE:1,TIME:2};
dojo.date.compare=function(_ded,_dee,_def){
var dA=_ded;
var dB=_dee||new Date();
var now=new Date();
with(dojo.date.compareTypes){
var opt=_def||(DATE|TIME);
var d1=new Date((opt&DATE)?dA.getFullYear():now.getFullYear(),(opt&DATE)?dA.getMonth():now.getMonth(),(opt&DATE)?dA.getDate():now.getDate(),(opt&TIME)?dA.getHours():0,(opt&TIME)?dA.getMinutes():0,(opt&TIME)?dA.getSeconds():0);
var d2=new Date((opt&DATE)?dB.getFullYear():now.getFullYear(),(opt&DATE)?dB.getMonth():now.getMonth(),(opt&DATE)?dB.getDate():now.getDate(),(opt&TIME)?dB.getHours():0,(opt&TIME)?dB.getMinutes():0,(opt&TIME)?dB.getSeconds():0);
}
if(d1.valueOf()>d2.valueOf()){
return 1;
}
if(d1.valueOf()<d2.valueOf()){
return -1;
}
return 0;
};
dojo.date.dateParts={YEAR:0,MONTH:1,DAY:2,HOUR:3,MINUTE:4,SECOND:5,MILLISECOND:6,QUARTER:7,WEEK:8,WEEKDAY:9};
dojo.date.add=function(dt,_df7,incr){
if(typeof dt=="number"){
dt=new Date(dt);
}
function fixOvershoot(){
if(sum.getDate()<dt.getDate()){
sum.setDate(0);
}
}
var sum=new Date(dt);
with(dojo.date.dateParts){
switch(_df7){
case YEAR:
sum.setFullYear(dt.getFullYear()+incr);
fixOvershoot();
break;
case QUARTER:
incr*=3;
case MONTH:
sum.setMonth(dt.getMonth()+incr);
fixOvershoot();
break;
case WEEK:
incr*=7;
case DAY:
sum.setDate(dt.getDate()+incr);
break;
case WEEKDAY:
var dat=dt.getDate();
var _dfb=0;
var days=0;
var strt=0;
var trgt=0;
var adj=0;
var mod=incr%5;
if(mod==0){
days=(incr>0)?5:-5;
_dfb=(incr>0)?((incr-5)/5):((incr+5)/5);
}else{
days=mod;
_dfb=parseInt(incr/5);
}
strt=dt.getDay();
if(strt==6&&incr>0){
adj=1;
}else{
if(strt==0&&incr<0){
adj=-1;
}
}
trgt=(strt+days);
if(trgt==0||trgt==6){
adj=(incr>0)?2:-2;
}
sum.setDate(dat+(7*_dfb)+days+adj);
break;
case HOUR:
sum.setHours(sum.getHours()+incr);
break;
case MINUTE:
sum.setMinutes(sum.getMinutes()+incr);
break;
case SECOND:
sum.setSeconds(sum.getSeconds()+incr);
break;
case MILLISECOND:
sum.setMilliseconds(sum.getMilliseconds()+incr);
break;
default:
break;
}
}
return sum;
};
dojo.date.diff=function(dtA,dtB,_e03){
if(typeof dtA=="number"){
dtA=new Date(dtA);
}
if(typeof dtB=="number"){
dtB=new Date(dtB);
}
var _e04=dtB.getFullYear()-dtA.getFullYear();
var _e05=(dtB.getMonth()-dtA.getMonth())+(_e04*12);
var _e06=dtB.getTime()-dtA.getTime();
var _e07=_e06/1000;
var _e08=_e07/60;
var _e09=_e08/60;
var _e0a=_e09/24;
var _e0b=_e0a/7;
var _e0c=0;
with(dojo.date.dateParts){
switch(_e03){
case YEAR:
_e0c=_e04;
break;
case QUARTER:
var mA=dtA.getMonth();
var mB=dtB.getMonth();
var qA=Math.floor(mA/3)+1;
var qB=Math.floor(mB/3)+1;
qB+=(_e04*4);
_e0c=qB-qA;
break;
case MONTH:
_e0c=_e05;
break;
case WEEK:
_e0c=parseInt(_e0b);
break;
case DAY:
_e0c=_e0a;
break;
case WEEKDAY:
var days=Math.round(_e0a);
var _e12=parseInt(days/7);
var mod=days%7;
if(mod==0){
days=_e12*5;
}else{
var adj=0;
var aDay=dtA.getDay();
var bDay=dtB.getDay();
_e12=parseInt(days/7);
mod=days%7;
var _e17=new Date(dtA);
_e17.setDate(_e17.getDate()+(_e12*7));
var _e18=_e17.getDay();
if(_e0a>0){
switch(true){
case aDay==6:
adj=-1;
break;
case aDay==0:
adj=0;
break;
case bDay==6:
adj=-1;
break;
case bDay==0:
adj=-2;
break;
case (_e18+mod)>5:
adj=-2;
break;
default:
break;
}
}else{
if(_e0a<0){
switch(true){
case aDay==6:
adj=0;
break;
case aDay==0:
adj=1;
break;
case bDay==6:
adj=2;
break;
case bDay==0:
adj=1;
break;
case (_e18+mod)<0:
adj=2;
break;
default:
break;
}
}
}
days+=adj;
days-=(_e12*2);
}
_e0c=days;
break;
case HOUR:
_e0c=_e09;
break;
case MINUTE:
_e0c=_e08;
break;
case SECOND:
_e0c=_e07;
break;
case MILLISECOND:
_e0c=_e06;
break;
default:
break;
}
}
return Math.round(_e0c);
};
dojo.provide("dojo.date.supplemental");
dojo.date.getFirstDayOfWeek=function(_e19){
var _e1a={mv:5,ae:6,af:6,bh:6,dj:6,dz:6,eg:6,er:6,et:6,iq:6,ir:6,jo:6,ke:6,kw:6,lb:6,ly:6,ma:6,om:6,qa:6,sa:6,sd:6,so:6,tn:6,ye:6,as:0,au:0,az:0,bw:0,ca:0,cn:0,fo:0,ge:0,gl:0,gu:0,hk:0,ie:0,il:0,is:0,jm:0,jp:0,kg:0,kr:0,la:0,mh:0,mo:0,mp:0,mt:0,nz:0,ph:0,pk:0,sg:0,th:0,tt:0,tw:0,um:0,us:0,uz:0,vi:0,za:0,zw:0,et:0,mw:0,ng:0,tj:0,gb:0,sy:4};
_e19=dojo.hostenv.normalizeLocale(_e19);
var _e1b=_e19.split("-")[1];
var dow=_e1a[_e1b];
return (typeof dow=="undefined")?1:dow;
};
dojo.date.getWeekend=function(_e1d){
var _e1e={eg:5,il:5,sy:5,"in":0,ae:4,bh:4,dz:4,iq:4,jo:4,kw:4,lb:4,ly:4,ma:4,om:4,qa:4,sa:4,sd:4,tn:4,ye:4};
var _e1f={ae:5,bh:5,dz:5,iq:5,jo:5,kw:5,lb:5,ly:5,ma:5,om:5,qa:5,sa:5,sd:5,tn:5,ye:5,af:5,ir:5,eg:6,il:6,sy:6};
_e1d=dojo.hostenv.normalizeLocale(_e1d);
var _e20=_e1d.split("-")[1];
var _e21=_e1e[_e20];
var end=_e1f[_e20];
if(typeof _e21=="undefined"){
_e21=6;
}
if(typeof end=="undefined"){
end=0;
}
return {start:_e21,end:end};
};
dojo.date.isWeekend=function(_e23,_e24){
var _e25=dojo.date.getWeekend(_e24);
var day=(_e23||new Date()).getDay();
if(_e25.end<_e25.start){
_e25.end+=7;
if(day<_e25.start){
day+=7;
}
}
return day>=_e25.start&&day<=_e25.end;
};
dojo.provide("dojo.date.format");
(function(){
dojo.date.format=function(_e27,_e28){
if(typeof _e28=="string"){
dojo.deprecated("dojo.date.format","To format dates with POSIX-style strings, please use dojo.date.strftime instead","0.5");
return dojo.date.strftime(_e27,_e28);
}
function formatPattern(_e29,_e2a){
return _e2a.replace(/([a-z])\1*/ig,function(_e2b){
var s;
var c=_e2b.charAt(0);
var l=_e2b.length;
var pad;
var _e30=["abbr","wide","narrow"];
switch(c){
case "G":
if(l>3){
dojo.unimplemented("Era format not implemented");
}
s=info.eras[_e29.getFullYear()<0?1:0];
break;
case "y":
s=_e29.getFullYear();
switch(l){
case 1:
break;
case 2:
s=String(s).substr(-2);
break;
default:
pad=true;
}
break;
case "Q":
case "q":
s=Math.ceil((_e29.getMonth()+1)/3);
switch(l){
case 1:
case 2:
pad=true;
break;
case 3:
case 4:
dojo.unimplemented("Quarter format not implemented");
}
break;
case "M":
case "L":
var m=_e29.getMonth();
var _e33;
switch(l){
case 1:
case 2:
s=m+1;
pad=true;
break;
case 3:
case 4:
case 5:
_e33=_e30[l-3];
break;
}
if(_e33){
var type=(c=="L")?"standalone":"format";
var prop=["months",type,_e33].join("-");
s=info[prop][m];
}
break;
case "w":
var _e36=0;
s=dojo.date.getWeekOfYear(_e29,_e36);
pad=true;
break;
case "d":
s=_e29.getDate();
pad=true;
break;
case "D":
s=dojo.date.getDayOfYear(_e29);
pad=true;
break;
case "E":
case "e":
case "c":
var d=_e29.getDay();
var _e33;
switch(l){
case 1:
case 2:
if(c=="e"){
var _e38=dojo.date.getFirstDayOfWeek(_e28.locale);
d=(d-_e38+7)%7;
}
if(c!="c"){
s=d+1;
pad=true;
break;
}
case 3:
case 4:
case 5:
_e33=_e30[l-3];
break;
}
if(_e33){
var type=(c=="c")?"standalone":"format";
var prop=["days",type,_e33].join("-");
s=info[prop][d];
}
break;
case "a":
var _e39=(_e29.getHours()<12)?"am":"pm";
s=info[_e39];
break;
case "h":
case "H":
case "K":
case "k":
var h=_e29.getHours();
switch(c){
case "h":
s=(h%12)||12;
break;
case "H":
s=h;
break;
case "K":
s=(h%12);
break;
case "k":
s=h||24;
break;
}
pad=true;
break;
case "m":
s=_e29.getMinutes();
pad=true;
break;
case "s":
s=_e29.getSeconds();
pad=true;
break;
case "S":
s=Math.round(_e29.getMilliseconds()*Math.pow(10,l-3));
break;
case "v":
case "z":
s=dojo.date.getTimezoneName(_e29);
if(s){
break;
}
l=4;
case "Z":
var _e3b=_e29.getTimezoneOffset();
var tz=[(_e3b<=0?"+":"-"),dojo.string.pad(Math.floor(Math.abs(_e3b)/60),2),dojo.string.pad(Math.abs(_e3b)%60,2)];
if(l==4){
tz.splice(0,0,"GMT");
tz.splice(3,0,":");
}
s=tz.join("");
break;
case "Y":
case "u":
case "W":
case "F":
case "g":
case "A":
dojo.debug(_e2b+" modifier not yet implemented");
s="?";
break;
default:
dojo.raise("dojo.date.format: invalid pattern char: "+_e2a);
}
if(pad){
s=dojo.string.pad(s,l);
}
return s;
});
}
_e28=_e28||{};
var _e3d=dojo.hostenv.normalizeLocale(_e28.locale);
var _e3e=_e28.formatLength||"full";
var info=dojo.date._getGregorianBundle(_e3d);
var str=[];
var _e40=dojo.lang.curry(this,formatPattern,_e27);
if(_e28.selector!="timeOnly"){
var _e41=_e28.datePattern||info["dateFormat-"+_e3e];
if(_e41){
str.push(_processPattern(_e41,_e40));
}
}
if(_e28.selector!="dateOnly"){
var _e42=_e28.timePattern||info["timeFormat-"+_e3e];
if(_e42){
str.push(_processPattern(_e42,_e40));
}
}
var _e43=str.join(" ");
return _e43;
};
dojo.date.parse=function(_e44,_e45){
_e45=_e45||{};
var _e46=dojo.hostenv.normalizeLocale(_e45.locale);
var info=dojo.date._getGregorianBundle(_e46);
var _e48=_e45.formatLength||"full";
if(!_e45.selector){
_e45.selector="dateOnly";
}
var _e49=_e45.datePattern||info["dateFormat-"+_e48];
var _e4a=_e45.timePattern||info["timeFormat-"+_e48];
var _e4b;
if(_e45.selector=="dateOnly"){
_e4b=_e49;
}else{
if(_e45.selector=="timeOnly"){
_e4b=_e4a;
}else{
if(_e45.selector=="dateTime"){
_e4b=_e49+" "+_e4a;
}else{
var msg="dojo.date.parse: Unknown selector param passed: '"+_e45.selector+"'.";
msg+=" Defaulting to date pattern.";
dojo.debug(msg);
_e4b=_e49;
}
}
}
var _e4d=[];
var _e4e=_processPattern(_e4b,dojo.lang.curry(this,_buildDateTimeRE,_e4d,info,_e45));
var _e4f=new RegExp("^"+_e4e+"$");
var _e50=_e4f.exec(_e44);
if(!_e50){
return null;
}
var _e51=["abbr","wide","narrow"];
var _e52=new Date(1972,0);
var _e53={};
for(var i=1;i<_e50.length;i++){
var grp=_e4d[i-1];
var l=grp.length;
var v=_e50[i];
switch(grp.charAt(0)){
case "y":
if(l!=2){
_e52.setFullYear(v);
_e53.year=v;
}else{
if(v<100){
v=Number(v);
var year=""+new Date().getFullYear();
var _e59=year.substring(0,2)*100;
var _e5a=Number(year.substring(2,4));
var _e5b=Math.min(_e5a+20,99);
var num=(v<_e5b)?_e59+v:_e59-100+v;
_e52.setFullYear(num);
_e53.year=num;
}else{
if(_e45.strict){
return null;
}
_e52.setFullYear(v);
_e53.year=v;
}
}
break;
case "M":
if(l>2){
if(!_e45.strict){
v=v.replace(/\./g,"");
v=v.toLowerCase();
}
var _e5d=info["months-format-"+_e51[l-3]].concat();
for(var j=0;j<_e5d.length;j++){
if(!_e45.strict){
_e5d[j]=_e5d[j].toLowerCase();
}
if(v==_e5d[j]){
_e52.setMonth(j);
_e53.month=j;
break;
}
}
if(j==_e5d.length){
dojo.debug("dojo.date.parse: Could not parse month name: '"+v+"'.");
return null;
}
}else{
_e52.setMonth(v-1);
_e53.month=v-1;
}
break;
case "E":
case "e":
if(!_e45.strict){
v=v.toLowerCase();
}
var days=info["days-format-"+_e51[l-3]].concat();
for(var j=0;j<days.length;j++){
if(!_e45.strict){
days[j]=days[j].toLowerCase();
}
if(v==days[j]){
break;
}
}
if(j==days.length){
dojo.debug("dojo.date.parse: Could not parse weekday name: '"+v+"'.");
return null;
}
break;
case "d":
_e52.setDate(v);
_e53.date=v;
break;
case "a":
var am=_e45.am||info.am;
var pm=_e45.pm||info.pm;
if(!_e45.strict){
v=v.replace(/\./g,"").toLowerCase();
am=am.replace(/\./g,"").toLowerCase();
pm=pm.replace(/\./g,"").toLowerCase();
}
if(_e45.strict&&v!=am&&v!=pm){
dojo.debug("dojo.date.parse: Could not parse am/pm part.");
return null;
}
var _e62=_e52.getHours();
if(v==pm&&_e62<12){
_e52.setHours(_e62+12);
}else{
if(v==am&&_e62==12){
_e52.setHours(0);
}
}
break;
case "K":
if(v==24){
v=0;
}
case "h":
case "H":
case "k":
if(v>23){
dojo.debug("dojo.date.parse: Illegal hours value");
return null;
}
_e52.setHours(v);
break;
case "m":
_e52.setMinutes(v);
break;
case "s":
_e52.setSeconds(v);
break;
case "S":
_e52.setMilliseconds(v);
break;
default:
dojo.unimplemented("dojo.date.parse: unsupported pattern char="+grp.charAt(0));
}
}
if(_e53.year&&_e52.getFullYear()!=_e53.year){
dojo.debug("Parsed year: '"+_e52.getFullYear()+"' did not match input year: '"+_e53.year+"'.");
return null;
}
if(_e53.month&&_e52.getMonth()!=_e53.month){
dojo.debug("Parsed month: '"+_e52.getMonth()+"' did not match input month: '"+_e53.month+"'.");
return null;
}
if(_e53.date&&_e52.getDate()!=_e53.date){
dojo.debug("Parsed day of month: '"+_e52.getDate()+"' did not match input day of month: '"+_e53.date+"'.");
return null;
}
return _e52;
};
function _processPattern(_e63,_e64,_e65,_e66){
var _e67=function(x){
return x;
};
_e64=_e64||_e67;
_e65=_e65||_e67;
_e66=_e66||_e67;
var _e69=_e63.match(/(''|[^'])+/g);
var _e6a=false;
for(var i=0;i<_e69.length;i++){
if(!_e69[i]){
_e69[i]="";
}else{
_e69[i]=(_e6a?_e65:_e64)(_e69[i]);
_e6a=!_e6a;
}
}
return _e66(_e69.join(""));
}
function _buildDateTimeRE(_e6c,info,_e6e,_e6f){
return _e6f.replace(/([a-z])\1*/ig,function(_e70){
var s;
var c=_e70.charAt(0);
var l=_e70.length;
switch(c){
case "y":
s="\\d"+((l==2)?"{2,4}":"+");
break;
case "M":
s=(l>2)?"\\S+":"\\d{1,2}";
break;
case "d":
s="\\d{1,2}";
break;
case "E":
s="\\S+";
break;
case "h":
case "H":
case "K":
case "k":
s="\\d{1,2}";
break;
case "m":
case "s":
s="[0-5]\\d";
break;
case "S":
s="\\d{1,3}";
break;
case "a":
var am=_e6e.am||info.am||"AM";
var pm=_e6e.pm||info.pm||"PM";
if(_e6e.strict){
s=am+"|"+pm;
}else{
s=am;
s+=(am!=am.toLowerCase())?"|"+am.toLowerCase():"";
s+="|";
s+=(pm!=pm.toLowerCase())?pm+"|"+pm.toLowerCase():pm;
}
break;
default:
dojo.unimplemented("parse of date format, pattern="+_e6f);
}
if(_e6c){
_e6c.push(_e70);
}
return "\\s*("+s+")\\s*";
});
}
})();
dojo.date.strftime=function(_e76,_e77,_e78){
var _e79=null;
function _(s,n){
return dojo.string.pad(s,n||2,_e79||"0");
}
var info=dojo.date._getGregorianBundle(_e78);
function $(_e7d){
switch(_e7d){
case "a":
return dojo.date.getDayShortName(_e76,_e78);
case "A":
return dojo.date.getDayName(_e76,_e78);
case "b":
case "h":
return dojo.date.getMonthShortName(_e76,_e78);
case "B":
return dojo.date.getMonthName(_e76,_e78);
case "c":
return dojo.date.format(_e76,{locale:_e78});
case "C":
return _(Math.floor(_e76.getFullYear()/100));
case "d":
return _(_e76.getDate());
case "D":
return $("m")+"/"+$("d")+"/"+$("y");
case "e":
if(_e79==null){
_e79=" ";
}
return _(_e76.getDate());
case "f":
if(_e79==null){
_e79=" ";
}
return _(_e76.getMonth()+1);
case "g":
break;
case "G":
dojo.unimplemented("unimplemented modifier 'G'");
break;
case "F":
return $("Y")+"-"+$("m")+"-"+$("d");
case "H":
return _(_e76.getHours());
case "I":
return _(_e76.getHours()%12||12);
case "j":
return _(dojo.date.getDayOfYear(_e76),3);
case "k":
if(_e79==null){
_e79=" ";
}
return _(_e76.getHours());
case "l":
if(_e79==null){
_e79=" ";
}
return _(_e76.getHours()%12||12);
case "m":
return _(_e76.getMonth()+1);
case "M":
return _(_e76.getMinutes());
case "n":
return "\n";
case "p":
return info[_e76.getHours()<12?"am":"pm"];
case "r":
return $("I")+":"+$("M")+":"+$("S")+" "+$("p");
case "R":
return $("H")+":"+$("M");
case "S":
return _(_e76.getSeconds());
case "t":
return "\t";
case "T":
return $("H")+":"+$("M")+":"+$("S");
case "u":
return String(_e76.getDay()||7);
case "U":
return _(dojo.date.getWeekOfYear(_e76));
case "V":
return _(dojo.date.getIsoWeekOfYear(_e76));
case "W":
return _(dojo.date.getWeekOfYear(_e76,1));
case "w":
return String(_e76.getDay());
case "x":
return dojo.date.format(_e76,{selector:"dateOnly",locale:_e78});
case "X":
return dojo.date.format(_e76,{selector:"timeOnly",locale:_e78});
case "y":
return _(_e76.getFullYear()%100);
case "Y":
return String(_e76.getFullYear());
case "z":
var _e7e=_e76.getTimezoneOffset();
return (_e7e>0?"-":"+")+_(Math.floor(Math.abs(_e7e)/60))+":"+_(Math.abs(_e7e)%60);
case "Z":
return dojo.date.getTimezoneName(_e76);
case "%":
return "%";
}
}
var _e7f="";
var i=0;
var _e81=0;
var _e82=null;
while((_e81=_e77.indexOf("%",i))!=-1){
_e7f+=_e77.substring(i,_e81++);
switch(_e77.charAt(_e81++)){
case "_":
_e79=" ";
break;
case "-":
_e79="";
break;
case "0":
_e79="0";
break;
case "^":
_e82="upper";
break;
case "*":
_e82="lower";
break;
case "#":
_e82="swap";
break;
default:
_e79=null;
_e81--;
break;
}
var _e83=$(_e77.charAt(_e81++));
switch(_e82){
case "upper":
_e83=_e83.toUpperCase();
break;
case "lower":
_e83=_e83.toLowerCase();
break;
case "swap":
var _e84=_e83.toLowerCase();
var _e85="";
var j=0;
var ch="";
while(j<_e83.length){
ch=_e83.charAt(j);
_e85+=(ch==_e84.charAt(j))?ch.toUpperCase():ch.toLowerCase();
j++;
}
_e83=_e85;
break;
default:
break;
}
_e82=null;
_e7f+=_e83;
i=_e81;
}
_e7f+=_e77.substring(i);
return _e7f;
};
(function(){
var _e88=[];
dojo.date.addCustomFormats=function(_e89,_e8a){
_e88.push({pkg:_e89,name:_e8a});
};
dojo.date._getGregorianBundle=function(_e8b){
var _e8c={};
dojo.lang.forEach(_e88,function(desc){
var _e8e=dojo.i18n.getLocalization(desc.pkg,desc.name,_e8b);
_e8c=dojo.lang.mixin(_e8c,_e8e);
},this);
return _e8c;
};
})();
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorian");
dojo.date.addCustomFormats("dojo.i18n.calendar","gregorianExtras");
dojo.date.getNames=function(item,type,use,_e92){
var _e93;
var _e94=dojo.date._getGregorianBundle(_e92);
var _e95=[item,use,type];
if(use=="standAlone"){
_e93=_e94[_e95.join("-")];
}
_e95[1]="format";
return (_e93||_e94[_e95.join("-")]).concat();
};
dojo.date.getDayName=function(_e96,_e97){
return dojo.date.getNames("days","wide","format",_e97)[_e96.getDay()];
};
dojo.date.getDayShortName=function(_e98,_e99){
return dojo.date.getNames("days","abbr","format",_e99)[_e98.getDay()];
};
dojo.date.getMonthName=function(_e9a,_e9b){
return dojo.date.getNames("months","wide","format",_e9b)[_e9a.getMonth()];
};
dojo.date.getMonthShortName=function(_e9c,_e9d){
return dojo.date.getNames("months","abbr","format",_e9d)[_e9c.getMonth()];
};
dojo.date.toRelativeString=function(_e9e){
var now=new Date();
var diff=(now-_e9e)/1000;
var end=" ago";
var _ea2=false;
if(diff<0){
_ea2=true;
end=" from now";
diff=-diff;
}
if(diff<60){
diff=Math.round(diff);
return diff+" second"+(diff==1?"":"s")+end;
}
if(diff<60*60){
diff=Math.round(diff/60);
return diff+" minute"+(diff==1?"":"s")+end;
}
if(diff<60*60*24){
diff=Math.round(diff/3600);
return diff+" hour"+(diff==1?"":"s")+end;
}
if(diff<60*60*24*7){
diff=Math.round(diff/(3600*24));
if(diff==1){
return _ea2?"Tomorrow":"Yesterday";
}else{
return diff+" days"+end;
}
}
return dojo.date.format(_e9e);
};
dojo.date.toSql=function(_ea3,_ea4){
return dojo.date.strftime(_ea3,"%F"+!_ea4?" %T":"");
};
dojo.date.fromSql=function(_ea5){
var _ea6=_ea5.split(/[\- :]/g);
while(_ea6.length<6){
_ea6.push(0);
}
return new Date(_ea6[0],(parseInt(_ea6[1],10)-1),_ea6[2],_ea6[3],_ea6[4],_ea6[5]);
};
dojo.provide("dojo.date.serialize");
dojo.date.setIso8601=function(_ea7,_ea8){
var _ea9=(_ea8.indexOf("T")==-1)?_ea8.split(" "):_ea8.split("T");
_ea7=dojo.date.setIso8601Date(_ea7,_ea9[0]);
if(_ea9.length==2){
_ea7=dojo.date.setIso8601Time(_ea7,_ea9[1]);
}
return _ea7;
};
dojo.date.fromIso8601=function(_eaa){
return dojo.date.setIso8601(new Date(0,0),_eaa);
};
dojo.date.setIso8601Date=function(_eab,_eac){
var _ead="^([0-9]{4})((-?([0-9]{2})(-?([0-9]{2}))?)|"+"(-?([0-9]{3}))|(-?W([0-9]{2})(-?([1-7]))?))?$";
var d=_eac.match(new RegExp(_ead));
if(!d){
dojo.debug("invalid date string: "+_eac);
return null;
}
var year=d[1];
var _eb0=d[4];
var date=d[6];
var _eb2=d[8];
var week=d[10];
var _eb4=d[12]?d[12]:1;
_eab.setFullYear(year);
if(_eb2){
_eab.setMonth(0);
_eab.setDate(Number(_eb2));
}else{
if(week){
_eab.setMonth(0);
_eab.setDate(1);
var gd=_eab.getDay();
var day=gd?gd:7;
var _eb7=Number(_eb4)+(7*Number(week));
if(day<=4){
_eab.setDate(_eb7+1-day);
}else{
_eab.setDate(_eb7+8-day);
}
}else{
if(_eb0){
_eab.setDate(1);
_eab.setMonth(_eb0-1);
}
if(date){
_eab.setDate(date);
}
}
}
return _eab;
};
dojo.date.fromIso8601Date=function(_eb8){
return dojo.date.setIso8601Date(new Date(0,0),_eb8);
};
dojo.date.setIso8601Time=function(_eb9,_eba){
var _ebb="Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
var d=_eba.match(new RegExp(_ebb));
var _ebd=0;
if(d){
if(d[0]!="Z"){
_ebd=(Number(d[3])*60)+Number(d[5]);
_ebd*=((d[2]=="-")?1:-1);
}
_ebd-=_eb9.getTimezoneOffset();
_eba=_eba.substr(0,_eba.length-d[0].length);
}
var _ebe="^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(.([0-9]+))?)?)?$";
d=_eba.match(new RegExp(_ebe));
if(!d){
dojo.debug("invalid time string: "+_eba);
return null;
}
var _ebf=d[1];
var mins=Number((d[3])?d[3]:0);
var secs=(d[5])?d[5]:0;
var ms=d[7]?(Number("0."+d[7])*1000):0;
_eb9.setHours(_ebf);
_eb9.setMinutes(mins);
_eb9.setSeconds(secs);
_eb9.setMilliseconds(ms);
if(_ebd!==0){
_eb9.setTime(_eb9.getTime()+_ebd*60000);
}
return _eb9;
};
dojo.date.fromIso8601Time=function(_ec3){
return dojo.date.setIso8601Time(new Date(0,0),_ec3);
};
dojo.date.toRfc3339=function(_ec4,_ec5){
if(!_ec4){
_ec4=new Date();
}
var _=dojo.string.pad;
var _ec7=[];
if(_ec5!="timeOnly"){
var date=[_(_ec4.getFullYear(),4),_(_ec4.getMonth()+1,2),_(_ec4.getDate(),2)].join("-");
_ec7.push(date);
}
if(_ec5!="dateOnly"){
var time=[_(_ec4.getHours(),2),_(_ec4.getMinutes(),2),_(_ec4.getSeconds(),2)].join(":");
var _eca=_ec4.getTimezoneOffset();
time+=(_eca>0?"-":"+")+_(Math.floor(Math.abs(_eca)/60),2)+":"+_(Math.abs(_eca)%60,2);
_ec7.push(time);
}
return _ec7.join("T");
};
dojo.date.fromRfc3339=function(_ecb){
if(_ecb.indexOf("Tany")!=-1){
_ecb=_ecb.replace("Tany","");
}
var _ecc=new Date();
return dojo.date.setIso8601(_ecc,_ecb);
};
dojo.provide("dojo.widget.DatePicker");
dojo.widget.defineWidget("dojo.widget.DatePicker",dojo.widget.HtmlWidget,{value:"",name:"",displayWeeks:6,adjustWeeks:false,startDate:"1492-10-12",endDate:"2941-10-12",weekStartsOn:"",storedDate:"",staticDisplay:false,dayWidth:"narrow",classNames:{previous:"previousMonth",disabledPrevious:"previousMonthDisabled",current:"currentMonth",disabledCurrent:"currentMonthDisabled",next:"nextMonth",disabledNext:"nextMonthDisabled",currentDate:"currentDate",selectedDate:"selectedItem"},templatePath:dojo.uri.dojoUri("src/widget/templates/DatePicker.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/DatePicker.css"),postMixInProperties:function(){
dojo.widget.DatePicker.superclass.postMixInProperties.apply(this,arguments);
if(this.storedDate){
dojo.deprecated("dojo.widget.DatePicker","use 'value' instead of 'storedDate'","0.5");
this.value=this.storedDate;
}
this.startDate=dojo.date.fromRfc3339(this.startDate);
this.endDate=dojo.date.fromRfc3339(this.endDate);
this.startDate.setHours(0,0,0,0);
this.endDate.setHours(24,0,0,-1);
if(!this.weekStartsOn){
this.weekStartsOn=dojo.date.getFirstDayOfWeek(this.lang);
}
this.today=new Date();
this.today.setHours(0,0,0,0);
if(typeof (this.value)=="string"&&this.value.toLowerCase()=="today"){
this.value=new Date();
}else{
if(this.value&&(typeof this.value=="string")&&(this.value.split("-").length>2)){
this.value=dojo.date.fromRfc3339(this.value);
this.value.setHours(0,0,0,0);
}
}
},fillInTemplate:function(args,frag){
dojo.widget.DatePicker.superclass.fillInTemplate.apply(this,arguments);
var _ecf=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_ecf);
this.weekTemplate=dojo.dom.removeNode(this.calendarWeekTemplate);
this._preInitUI(this.value?this.value:this.today,false,true);
var _ed0=dojo.lang.unnest(dojo.date.getNames("days",this.dayWidth,"standAlone",this.lang));
if(this.weekStartsOn>0){
for(var i=0;i<this.weekStartsOn;i++){
_ed0.push(_ed0.shift());
}
}
var _ed2=this.dayLabelsRow.getElementsByTagName("td");
for(i=0;i<7;i++){
_ed2.item(i).innerHTML=_ed0[i];
}
if(this.value){
this.setValue(this.value);
}
},getValue:function(){
return dojo.date.toRfc3339(new Date(this.value),"dateOnly");
},getDate:function(){
return this.value;
},setValue:function(_ed3){
this.setDate(_ed3);
},setDate:function(_ed4){
if(typeof _ed4=="string"){
this.value=dojo.date.fromRfc3339(_ed4);
}else{
this.value=new Date(_ed4);
}
this.value.setHours(0,0,0,0);
if(this.selectedNode!=null){
dojo.html.removeClass(this.selectedNode,this.classNames.selectedDate);
}
if(this.clickedNode!=null){
dojo.html.addClass(this.clickedNode,this.classNames.selectedDate);
this.selectedNode=this.clickedNode;
}else{
this._preInitUI(this.value,false,true);
}
this.clickedNode=null;
this.onValueChanged(this.value);
},_preInitUI:function(_ed5,_ed6,_ed7){
if(_ed5<this.startDate||_ed5>this.endDate){
_ed5=new Date((_ed5<this.startDate)?this.startDate:this.endDate);
}
this.firstDay=this._initFirstDay(_ed5,_ed6);
this.selectedIsUsed=false;
this.currentIsUsed=false;
var _ed8=new Date(this.firstDay);
var _ed9=_ed8.getMonth();
this.curMonth=new Date(_ed8);
this.curMonth.setDate(_ed8.getDate()+6);
this.curMonth.setDate(1);
if(this.displayWeeks==""||this.adjustWeeks){
this.adjustWeeks=true;
this.displayWeeks=Math.ceil((dojo.date.getDaysInMonth(this.curMonth)+this._getAdjustedDay(this.curMonth))/7);
}
var days=this.displayWeeks*7;
if(dojo.date.diff(this.startDate,this.endDate,dojo.date.dateParts.DAY)<days){
this.staticDisplay=true;
if(dojo.date.diff(_ed8,this.endDate,dojo.date.dateParts.DAY)>days){
this._preInitUI(this.startDate,true,false);
_ed8=new Date(this.firstDay);
}
this.curMonth=new Date(_ed8);
this.curMonth.setDate(_ed8.getDate()+6);
this.curMonth.setDate(1);
var _edb=(_ed8.getMonth()==this.curMonth.getMonth())?"current":"previous";
}
if(_ed7){
this._initUI(days);
}
},_initUI:function(days){
dojo.dom.removeChildren(this.calendarDatesContainerNode);
for(var i=0;i<this.displayWeeks;i++){
this.calendarDatesContainerNode.appendChild(this.weekTemplate.cloneNode(true));
}
var _ede=new Date(this.firstDay);
this._setMonthLabel(this.curMonth.getMonth());
this._setYearLabels(this.curMonth.getFullYear());
var _edf=this.calendarDatesContainerNode.getElementsByTagName("td");
var _ee0=this.calendarDatesContainerNode.getElementsByTagName("tr");
var _ee1;
for(i=0;i<days;i++){
_ee1=_edf.item(i);
_ee1.innerHTML=_ede.getDate();
var _ee2=(_ede.getMonth()<this.curMonth.getMonth())?"previous":(_ede.getMonth()==this.curMonth.getMonth())?"current":"next";
var _ee3=_ee2;
if(this._isDisabledDate(_ede)){
var _ee4={previous:"disabledPrevious",current:"disabledCurrent",next:"disabledNext"};
_ee3=_ee4[_ee2];
}
dojo.html.setClass(_ee1,this._getDateClassName(_ede,_ee3));
if(dojo.html.hasClass(_ee1,this.classNames.selectedDate)){
this.selectedNode=_ee1;
}
_ede=dojo.date.add(_ede,dojo.date.dateParts.DAY,1);
}
this.lastDay=dojo.date.add(_ede,dojo.date.dateParts.DAY,-1);
this._initControls();
},_initControls:function(){
var d=this.firstDay;
var d2=this.lastDay;
var _ee7,_ee8,_ee9,_eea,_eeb,_eec;
_ee7=_ee8=_ee9=_eea=_eeb=_eec=!this.staticDisplay;
with(dojo.date.dateParts){
var add=dojo.date.add;
if(_ee7&&add(d,DAY,(-1*(this._getAdjustedDay(d)+1)))<this.startDate){
_ee7=_ee9=_eeb=false;
}
if(_ee8&&d2>this.endDate){
_ee8=_eea=_eec=false;
}
if(_ee9&&add(d,DAY,-1)<this.startDate){
_ee9=_eeb=false;
}
if(_eea&&add(d2,DAY,1)>this.endDate){
_eea=_eec=false;
}
if(_eeb&&add(d2,YEAR,-1)<this.startDate){
_eeb=false;
}
if(_eec&&add(d,YEAR,1)>this.endDate){
_eec=false;
}
}
function enableControl(node,_eef){
dojo.html.setVisibility(node,_eef?"":"hidden");
}
enableControl(this.decreaseWeekNode,_ee7);
enableControl(this.increaseWeekNode,_ee8);
enableControl(this.decreaseMonthNode,_ee9);
enableControl(this.increaseMonthNode,_eea);
enableControl(this.previousYearLabelNode,_eeb);
enableControl(this.nextYearLabelNode,_eec);
},_incrementWeek:function(evt){
var d=new Date(this.firstDay);
switch(evt.target){
case this.increaseWeekNode.getElementsByTagName("img").item(0):
case this.increaseWeekNode:
var _ef2=dojo.date.add(d,dojo.date.dateParts.WEEK,1);
if(_ef2<this.endDate){
d=dojo.date.add(d,dojo.date.dateParts.WEEK,1);
}
break;
case this.decreaseWeekNode.getElementsByTagName("img").item(0):
case this.decreaseWeekNode:
if(d>=this.startDate){
d=dojo.date.add(d,dojo.date.dateParts.WEEK,-1);
}
break;
}
this._preInitUI(d,true,true);
},_incrementMonth:function(evt){
var d=new Date(this.curMonth);
var _ef5=new Date(this.firstDay);
switch(evt.currentTarget){
case this.increaseMonthNode.getElementsByTagName("img").item(0):
case this.increaseMonthNode:
_ef5=dojo.date.add(_ef5,dojo.date.dateParts.DAY,this.displayWeeks*7);
if(_ef5<this.endDate){
d=dojo.date.add(d,dojo.date.dateParts.MONTH,1);
}else{
var _ef6=true;
}
break;
case this.decreaseMonthNode.getElementsByTagName("img").item(0):
case this.decreaseMonthNode:
if(_ef5>this.startDate){
d=dojo.date.add(d,dojo.date.dateParts.MONTH,-1);
}else{
var _ef7=true;
}
break;
}
if(_ef7){
d=new Date(this.startDate);
}else{
if(_ef6){
d=new Date(this.endDate);
}
}
this._preInitUI(d,false,true);
},_incrementYear:function(evt){
var year=this.curMonth.getFullYear();
var _efa=new Date(this.firstDay);
switch(evt.target){
case this.nextYearLabelNode:
_efa=dojo.date.add(_efa,dojo.date.dateParts.YEAR,1);
if(_efa<this.endDate){
year++;
}else{
var _efb=true;
}
break;
case this.previousYearLabelNode:
_efa=dojo.date.add(_efa,dojo.date.dateParts.YEAR,-1);
if(_efa>this.startDate){
year--;
}else{
var _efc=true;
}
break;
}
var d;
if(_efc){
d=new Date(this.startDate);
}else{
if(_efb){
d=new Date(this.endDate);
}else{
d=new Date(year,this.curMonth.getMonth(),1);
}
}
this._preInitUI(d,false,true);
},onIncrementWeek:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementWeek(evt);
}
},onIncrementMonth:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementMonth(evt);
}
},onIncrementYear:function(evt){
evt.stopPropagation();
if(!this.staticDisplay){
this._incrementYear(evt);
}
},_setMonthLabel:function(_f01){
this.monthLabelNode.innerHTML=dojo.date.getNames("months","wide","standAlone",this.lang)[_f01];
},_setYearLabels:function(year){
var y=year-1;
var that=this;
function f(n){
that[n+"YearLabelNode"].innerHTML=dojo.date.format(new Date(y++,0),{formatLength:"yearOnly",locale:that.lang});
}
f("previous");
f("current");
f("next");
},_getDateClassName:function(date,_f07){
var _f08=this.classNames[_f07];
if((!this.selectedIsUsed&&this.value)&&(Number(date)==Number(this.value))){
_f08=this.classNames.selectedDate+" "+_f08;
this.selectedIsUsed=true;
}
if((!this.currentIsUsed)&&(Number(date)==Number(this.today))){
_f08=_f08+" "+this.classNames.currentDate;
this.currentIsUsed=true;
}
return _f08;
},onClick:function(evt){
dojo.event.browser.stopEvent(evt);
},_handleUiClick:function(evt){
var _f0b=evt.target;
if(_f0b.nodeType!=dojo.dom.ELEMENT_NODE){
_f0b=_f0b.parentNode;
}
dojo.event.browser.stopEvent(evt);
this.selectedIsUsed=this.todayIsUsed=false;
var _f0c=this.curMonth.getMonth();
var year=this.curMonth.getFullYear();
if(dojo.html.hasClass(_f0b,this.classNames["disabledPrevious"])||dojo.html.hasClass(_f0b,this.classNames["disabledCurrent"])||dojo.html.hasClass(_f0b,this.classNames["disabledNext"])){
return;
}else{
if(dojo.html.hasClass(_f0b,this.classNames["next"])){
_f0c=++_f0c%12;
if(_f0c===0){
++year;
}
}else{
if(dojo.html.hasClass(_f0b,this.classNames["previous"])){
_f0c=--_f0c%12;
if(_f0c==11){
--year;
}
}
}
}
this.clickedNode=_f0b;
this.setDate(new Date(year,_f0c,_f0b.innerHTML));
},onValueChanged:function(date){
},_isDisabledDate:function(_f0f){
if(_f0f<this.startDate||_f0f>this.endDate){
return true;
}
return this.isDisabledDate(_f0f,this.lang);
},isDisabledDate:function(_f10,_f11){
return false;
},_initFirstDay:function(_f12,adj){
var d=new Date(_f12);
if(!adj){
d.setDate(1);
}
d.setDate(d.getDate()-this._getAdjustedDay(d,this.weekStartsOn));
d.setHours(0,0,0,0);
return d;
},_getAdjustedDay:function(_f15){
var days=[0,1,2,3,4,5,6];
if(this.weekStartsOn>0){
for(var i=0;i<this.weekStartsOn;i++){
days.unshift(days.pop());
}
}
return days[_f15.getDay()];
},destroy:function(){
dojo.widget.DatePicker.superclass.destroy.apply(this,arguments);
dojo.html.destroyNode(this.weekTemplate);
}});
dojo.provide("dojo.widget.DropdownDatePicker");
dojo.widget.defineWidget("dojo.widget.DropdownDatePicker",dojo.widget.DropdownContainer,{iconURL:dojo.uri.dojoUri("src/widget/templates/images/dateIcon.gif"),formatLength:"short",displayFormat:"",dateFormat:"",saveFormat:"",value:"",name:"",displayWeeks:6,adjustWeeks:false,startDate:"1492-10-12",endDate:"2941-10-12",weekStartsOn:"",staticDisplay:false,postMixInProperties:function(_f18,frag){
dojo.widget.DropdownDatePicker.superclass.postMixInProperties.apply(this,arguments);
var _f1a=dojo.i18n.getLocalization("dojo.widget","DropdownDatePicker",this.lang);
this.iconAlt=_f1a.selectDate;
if(typeof (this.value)=="string"&&this.value.toLowerCase()=="today"){
this.value=new Date();
}
if(this.value&&isNaN(this.value)){
var orig=this.value;
this.value=dojo.date.fromRfc3339(this.value);
if(!this.value){
this.value=new Date(orig);
dojo.deprecated("dojo.widget.DropdownDatePicker","date attributes must be passed in Rfc3339 format","0.5");
}
}
if(this.value&&!isNaN(this.value)){
this.value=new Date(this.value);
}
},fillInTemplate:function(args,frag){
dojo.widget.DropdownDatePicker.superclass.fillInTemplate.call(this,args,frag);
var _f1e={widgetContainerId:this.widgetId,lang:this.lang,value:this.value,startDate:this.startDate,endDate:this.endDate,displayWeeks:this.displayWeeks,weekStartsOn:this.weekStartsOn,adjustWeeks:this.adjustWeeks,staticDisplay:this.staticDisplay};
this.datePicker=dojo.widget.createWidget("DatePicker",_f1e,this.containerNode,"child");
dojo.event.connect(this.datePicker,"onValueChanged",this,"_updateText");
if(this.value){
this._updateText();
}
this.containerNode.explodeClassName="calendarBodyContainer";
this.valueNode.name=this.name;
},getValue:function(){
return this.valueNode.value;
},getDate:function(){
return this.datePicker.value;
},setValue:function(_f1f){
this.setDate(_f1f);
},setDate:function(_f20){
this.datePicker.setDate(_f20);
this._syncValueNode();
},_updateText:function(){
if(this.dateFormat){
dojo.deprecated("dojo.widget.DropdownDatePicker","Must use displayFormat attribute instead of dateFormat.  See dojo.date.format for specification.","0.5");
this.inputNode.value=dojo.date.strftime(this.datePicker.value,this.dateFormat,this.lang);
}else{
if(this.datePicker.value){
this.inputNode.value=dojo.date.format(this.datePicker.value,{formatLength:this.formatLength,datePattern:this.displayFormat,selector:"dateOnly",locale:this.lang});
}else{
this.inputNode.value="";
}
}
if(this.value<this.datePicker.startDate||this.value>this.datePicker.endDate){
this.inputNode.value="";
}
this._syncValueNode();
this.onValueChanged(this.getDate());
this.hideContainer();
},onValueChanged:function(_f21){
},onInputChange:function(){
if(this.dateFormat){
dojo.deprecated("dojo.widget.DropdownDatePicker","Cannot parse user input.  Must use displayFormat attribute instead of dateFormat.  See dojo.date.format for specification.","0.5");
}else{
var _f22=dojo.string.trim(this.inputNode.value);
if(_f22){
var _f23=dojo.date.parse(_f22,{formatLength:this.formatLength,datePattern:this.displayFormat,selector:"dateOnly",locale:this.lang});
if(_f23){
this.setDate(_f23);
}
}else{
this.valueNode.value=_f22;
}
}
if(_f22){
this._updateText();
}
},_syncValueNode:function(){
var date=this.datePicker.value;
var _f25="";
switch(this.saveFormat.toLowerCase()){
case "rfc":
case "iso":
case "":
_f25=dojo.date.toRfc3339(date,"dateOnly");
break;
case "posix":
case "unix":
_f25=Number(date);
break;
default:
if(date){
_f25=dojo.date.format(date,{datePattern:this.saveFormat,selector:"dateOnly",locale:this.lang});
}
}
this.valueNode.value=_f25;
},destroy:function(_f26){
this.datePicker.destroy(_f26);
dojo.widget.DropdownDatePicker.superclass.destroy.apply(this,arguments);
}});
dojo.provide("dojo.collections.Store");
dojo.collections.Store=function(_f27){
var data=[];
this.keyField="Id";
this.get=function(){
return data;
};
this.getByKey=function(key){
for(var i=0;i<data.length;i++){
if(data[i].key==key){
return data[i];
}
}
return null;
};
this.getByIndex=function(idx){
return data[idx];
};
this.getData=function(){
var arr=[];
for(var i=0;i<data.length;i++){
arr.push(data[i].src);
}
return arr;
};
this.getDataByKey=function(key){
for(var i=0;i<data.length;i++){
if(data[i].key==key){
return data[i].src;
}
}
return null;
};
this.getDataByIndex=function(idx){
return data[idx].src;
};
this.update=function(obj,_f32,val){
var _f34=_f32.split("."),i=0,o=obj,_f37;
if(_f34.length>1){
_f37=_f34.pop();
do{
if(_f34[i].indexOf("()")>-1){
var temp=_f34[i++].split("()")[0];
if(!o[temp]){
dojo.raise("dojo.collections.Store.getField(obj, '"+_f37+"'): '"+temp+"' is not a property of the passed object.");
}else{
o=o[temp]();
}
}else{
o=o[_f34[i++]];
}
}while(i<_f34.length&&o!=null);
}else{
_f37=_f34[0];
}
obj[_f37]=val;
this.onUpdateField(obj,_f32,val);
};
this.forEach=function(fn){
if(Array.forEach){
Array.forEach(data,fn,this);
}else{
for(var i=0;i<data.length;i++){
fn.call(this,data[i]);
}
}
};
this.forEachData=function(fn){
if(Array.forEach){
Array.forEach(this.getData(),fn,this);
}else{
var a=this.getData();
for(var i=0;i<a.length;i++){
fn.call(this,a[i]);
}
}
};
this.setData=function(arr){
data=[];
for(var i=0;i<arr.length;i++){
data.push({key:arr[i][this.keyField],src:arr[i]});
}
this.onSetData();
};
this.clearData=function(){
data=[];
this.onClearData();
};
this.addData=function(obj,key){
var k=key||obj[this.keyField];
if(this.getByKey(k)){
var o=this.getByKey(k);
o.src=obj;
}else{
var o={key:k,src:obj};
data.push(o);
}
this.onAddData(o);
};
this.addDataRange=function(arr){
var _f45=[];
for(var i=0;i<arr.length;i++){
var k=arr[i][this.keyField];
if(this.getByKey(k)){
var o=this.getByKey(k);
o.src=obj;
}else{
var o={key:k,src:arr[i]};
data.push(o);
}
_f45.push(o);
}
this.onAddDataRange(_f45);
};
this.removeData=function(obj){
var idx=-1;
var o=null;
for(var i=0;i<data.length;i++){
if(data[i].src==obj){
idx=i;
o=data[i];
break;
}
}
this.onRemoveData(o);
if(idx>-1){
data.splice(idx,1);
}
};
this.removeDataByKey=function(key){
this.removeData(this.getDataByKey(key));
};
this.removeDataByIndex=function(idx){
this.removeData(this.getDataByIndex(idx));
};
if(_f27&&_f27.length&&_f27[0]){
this.setData(_f27);
}
};
dojo.extend(dojo.collections.Store,{getField:function(obj,_f50){
var _f51=_f50.split("."),i=0,o=obj;
do{
if(_f51[i].indexOf("()")>-1){
var temp=_f51[i++].split("()")[0];
if(!o[temp]){
dojo.raise("dojo.collections.Store.getField(obj, '"+_f50+"'): '"+temp+"' is not a property of the passed object.");
}else{
o=o[temp]();
}
}else{
o=o[_f51[i++]];
}
}while(i<_f51.length&&o!=null);
if(i<_f51.length){
dojo.raise("dojo.collections.Store.getField(obj, '"+_f50+"'): '"+_f50+"' is not a property of the passed object.");
}
return o;
},getFromHtml:function(meta,body,_f57){
var rows=body.rows;
var ctor=function(row){
var obj={};
for(var i=0;i<meta.length;i++){
var o=obj;
var data=row.cells[i].innerHTML;
var p=meta[i].getField();
if(p.indexOf(".")>-1){
p=p.split(".");
while(p.length>1){
var pr=p.shift();
o[pr]={};
o=o[pr];
}
p=p[0];
}
var type=meta[i].getType();
if(type==String){
o[p]=data;
}else{
if(data){
o[p]=new type(data);
}else{
o[p]=new type();
}
}
}
return obj;
};
var arr=[];
for(var i=0;i<rows.length;i++){
var o=ctor(rows[i]);
if(_f57){
_f57(o,rows[i]);
}
arr.push(o);
}
return arr;
},onSetData:function(){
},onClearData:function(){
},onAddData:function(obj){
},onAddDataRange:function(arr){
},onRemoveData:function(obj){
},onUpdateField:function(obj,_f69,val){
}});
dojo.provide("dojo.widget.FilteringTable");
dojo.widget.defineWidget("dojo.widget.FilteringTable",dojo.widget.HtmlWidget,function(){
this.store=new dojo.collections.Store();
this.valueField="Id";
this.multiple=false;
this.maxSelect=0;
this.maxSortable=1;
this.minRows=0;
this.defaultDateFormat="%D";
this.isInitialized=false;
this.alternateRows=false;
this.columns=[];
this.sortInformation=[{index:0,direction:0}];
this.headClass="";
this.tbodyClass="";
this.headerClass="";
this.headerUpClass="selectedUp";
this.headerDownClass="selectedDown";
this.rowClass="";
this.rowAlternateClass="alt";
this.rowSelectedClass="selected";
this.columnSelected="sorted-column";
},{isContainer:false,templatePath:null,templateCssPath:null,getTypeFromString:function(s){
var _f6c=s.split("."),i=0,obj=dj_global;
do{
obj=obj[_f6c[i++]];
}while(i<_f6c.length&&obj);
return (obj!=dj_global)?obj:null;
},getByRow:function(row){
return this.store.getByKey(dojo.html.getAttribute(row,"value"));
},getDataByRow:function(row){
return this.store.getDataByKey(dojo.html.getAttribute(row,"value"));
},getRow:function(obj){
var rows=this.domNode.tBodies[0].rows;
for(var i=0;i<rows.length;i++){
if(this.store.getDataByKey(dojo.html.getAttribute(rows[i],"value"))==obj){
return rows[i];
}
}
return null;
},getColumnIndex:function(_f74){
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].getField()==_f74){
return i;
}
}
return -1;
},getSelectedData:function(){
var data=this.store.get();
var a=[];
for(var i=0;i<data.length;i++){
if(data[i].isSelected){
a.push(data[i].src);
}
}
if(this.multiple){
return a;
}else{
return a[0];
}
},isSelected:function(obj){
var data=this.store.get();
for(var i=0;i<data.length;i++){
if(data[i].src==obj){
return true;
}
}
return false;
},isValueSelected:function(val){
var v=this.store.getByKey(val);
if(v){
return v.isSelected;
}
return false;
},isIndexSelected:function(idx){
var v=this.store.getByIndex(idx);
if(v){
return v.isSelected;
}
return false;
},isRowSelected:function(row){
var v=this.getByRow(row);
if(v){
return v.isSelected;
}
return false;
},reset:function(){
this.store.clearData();
this.columns=[];
this.sortInformation=[{index:0,direction:0}];
this.resetSelections();
this.isInitialized=false;
this.onReset();
},resetSelections:function(){
this.store.forEach(function(_f82){
_f82.isSelected=false;
});
},onReset:function(){
},select:function(obj){
var data=this.store.get();
for(var i=0;i<data.length;i++){
if(data[i].src==obj){
data[i].isSelected=true;
break;
}
}
this.onDataSelect(obj);
},selectByValue:function(val){
this.select(this.store.getDataByKey(val));
},selectByIndex:function(idx){
this.select(this.store.getDataByIndex(idx));
},selectByRow:function(row){
this.select(this.getDataByRow(row));
},selectAll:function(){
this.store.forEach(function(_f89){
_f89.isSelected=true;
});
},onDataSelect:function(obj){
},toggleSelection:function(obj){
var data=this.store.get();
for(var i=0;i<data.length;i++){
if(data[i].src==obj){
data[i].isSelected=!data[i].isSelected;
break;
}
}
this.onDataToggle(obj);
},toggleSelectionByValue:function(val){
this.toggleSelection(this.store.getDataByKey(val));
},toggleSelectionByIndex:function(idx){
this.toggleSelection(this.store.getDataByIndex(idx));
},toggleSelectionByRow:function(row){
this.toggleSelection(this.getDataByRow(row));
},toggleAll:function(){
this.store.forEach(function(_f91){
_f91.isSelected=!_f91.isSelected;
});
},onDataToggle:function(obj){
},_meta:{field:null,format:null,filterer:null,noSort:false,sortType:"String",dataType:String,sortFunction:null,filterFunction:null,label:null,align:"left",valign:"middle",getField:function(){
return this.field||this.label;
},getType:function(){
return this.dataType;
}},createMetaData:function(obj){
for(var p in this._meta){
if(!obj[p]){
obj[p]=this._meta[p];
}
}
if(!obj.label){
obj.label=obj.field;
}
if(!obj.filterFunction){
obj.filterFunction=this._defaultFilter;
}
return obj;
},parseMetadata:function(head){
this.columns=[];
this.sortInformation=[];
var row=head.getElementsByTagName("tr")[0];
var _f97=row.getElementsByTagName("td");
if(_f97.length==0){
_f97=row.getElementsByTagName("th");
}
for(var i=0;i<_f97.length;i++){
var o=this.createMetaData({});
if(dojo.html.hasAttribute(_f97[i],"align")){
o.align=dojo.html.getAttribute(_f97[i],"align");
}
if(dojo.html.hasAttribute(_f97[i],"valign")){
o.valign=dojo.html.getAttribute(_f97[i],"valign");
}
if(dojo.html.hasAttribute(_f97[i],"nosort")){
o.noSort=(dojo.html.getAttribute(_f97[i],"nosort")=="true");
}
if(dojo.html.hasAttribute(_f97[i],"sortusing")){
var _f9a=dojo.html.getAttribute(_f97[i],"sortusing");
var f=this.getTypeFromString(_f9a);
if(f!=null&&f!=window&&typeof (f)=="function"){
o.sortFunction=f;
}
}
o.label=dojo.html.renderedTextContent(_f97[i]);
if(dojo.html.hasAttribute(_f97[i],"field")){
o.field=dojo.html.getAttribute(_f97[i],"field");
}else{
if(o.label.length>0){
o.field=o.label;
}else{
o.field="field"+i;
}
}
if(dojo.html.hasAttribute(_f97[i],"format")){
o.format=dojo.html.getAttribute(_f97[i],"format");
}
if(dojo.html.hasAttribute(_f97[i],"dataType")){
var _f9c=dojo.html.getAttribute(_f97[i],"dataType");
if(_f9c.toLowerCase()=="html"||_f9c.toLowerCase()=="markup"){
o.sortType="__markup__";
}else{
var type=this.getTypeFromString(_f9c);
if(type){
o.sortType=_f9c;
o.dataType=type;
}
}
}
if(dojo.html.hasAttribute(_f97[i],"filterusing")){
var _f9a=dojo.html.getAttribute(_f97[i],"filterusing");
var f=this.getTypeFromString(_f9a);
if(f!=null&&f!=window&&typeof (f)=="function"){
o.filterFunction=f;
}
}
this.columns.push(o);
if(dojo.html.hasAttribute(_f97[i],"sort")){
var info={index:i,direction:0};
var dir=dojo.html.getAttribute(_f97[i],"sort");
if(!isNaN(parseInt(dir))){
dir=parseInt(dir);
info.direction=(dir!=0)?1:0;
}else{
info.direction=(dir.toLowerCase()=="desc")?1:0;
}
this.sortInformation.push(info);
}
}
if(this.sortInformation.length==0){
this.sortInformation.push({index:0,direction:0});
}else{
if(this.sortInformation.length>this.maxSortable){
this.sortInformation.length=this.maxSortable;
}
}
},parseData:function(body){
if(body.rows.length==0&&this.columns.length==0){
return;
}
var self=this;
this["__selected__"]=[];
var arr=this.store.getFromHtml(this.columns,body,function(obj,row){
if(typeof (obj[self.valueField])=="undefined"||obj[self.valueField]==null){
obj[self.valueField]=dojo.html.getAttribute(row,"value");
}
if(dojo.html.getAttribute(row,"selected")=="true"){
self["__selected__"].push(obj);
}
});
this.store.setData(arr,true);
this.render();
for(var i=0;i<this["__selected__"].length;i++){
this.select(this["__selected__"][i]);
}
this.renderSelections();
delete this["__selected__"];
this.isInitialized=true;
},onSelect:function(e){
var row=dojo.html.getParentByType(e.target,"tr");
if(dojo.html.hasAttribute(row,"emptyRow")){
return;
}
var body=dojo.html.getParentByType(row,"tbody");
if(this.multiple){
if(e.shiftKey){
var _fa9;
var rows=body.rows;
for(var i=0;i<rows.length;i++){
if(rows[i]==row){
break;
}
if(this.isRowSelected(rows[i])){
_fa9=rows[i];
}
}
if(!_fa9){
_fa9=row;
for(;i<rows.length;i++){
if(this.isRowSelected(rows[i])){
row=rows[i];
break;
}
}
}
this.resetSelections();
if(_fa9==row){
this.toggleSelectionByRow(row);
}else{
var _fac=false;
for(var i=0;i<rows.length;i++){
if(rows[i]==_fa9){
_fac=true;
}
if(_fac){
this.selectByRow(rows[i]);
}
if(rows[i]==row){
_fac=false;
}
}
}
}else{
this.toggleSelectionByRow(row);
}
}else{
this.resetSelections();
this.toggleSelectionByRow(row);
}
this.renderSelections();
},onSort:function(e){
var _fae=this.sortIndex;
var _faf=this.sortDirection;
var _fb0=e.target;
var row=dojo.html.getParentByType(_fb0,"tr");
var _fb2="td";
if(row.getElementsByTagName(_fb2).length==0){
_fb2="th";
}
var _fb3=row.getElementsByTagName(_fb2);
var _fb4=dojo.html.getParentByType(_fb0,_fb2);
for(var i=0;i<_fb3.length;i++){
dojo.html.setClass(_fb3[i],this.headerClass);
if(_fb3[i]==_fb4){
if(this.sortInformation[0].index!=i){
this.sortInformation.unshift({index:i,direction:0});
}else{
this.sortInformation[0]={index:i,direction:(~this.sortInformation[0].direction)&1};
}
}
}
this.sortInformation.length=Math.min(this.sortInformation.length,this.maxSortable);
for(var i=0;i<this.sortInformation.length;i++){
var idx=this.sortInformation[i].index;
var dir=(~this.sortInformation[i].direction)&1;
dojo.html.setClass(_fb3[idx],dir==0?this.headerDownClass:this.headerUpClass);
}
this.render();
},onFilter:function(){
},_defaultFilter:function(obj){
return true;
},setFilter:function(_fb9,fn){
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].getField()==_fb9){
this.columns[i].filterFunction=fn;
break;
}
}
this.applyFilters();
},setFilterByIndex:function(idx,fn){
this.columns[idx].filterFunction=fn;
this.applyFilters();
},clearFilter:function(_fbe){
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].getField()==_fbe){
this.columns[i].filterFunction=this._defaultFilter;
break;
}
}
this.applyFilters();
},clearFilterByIndex:function(idx){
this.columns[idx].filterFunction=this._defaultFilter;
this.applyFilters();
},clearFilters:function(){
for(var i=0;i<this.columns.length;i++){
this.columns[i].filterFunction=this._defaultFilter;
}
var rows=this.domNode.tBodies[0].rows;
for(var i=0;i<rows.length;i++){
rows[i].style.display="";
if(this.alternateRows){
dojo.html[((i%2==1)?"addClass":"removeClass")](rows[i],this.rowAlternateClass);
}
}
this.onFilter();
},applyFilters:function(){
var alt=0;
var rows=this.domNode.tBodies[0].rows;
for(var i=0;i<rows.length;i++){
var b=true;
var row=rows[i];
for(var j=0;j<this.columns.length;j++){
var _fc9=this.store.getField(this.getDataByRow(row),this.columns[j].getField());
if(this.columns[j].getType()==Date&&_fc9!=null&&!_fc9.getYear){
_fc9=new Date(_fc9);
}
if(!this.columns[j].filterFunction(_fc9)){
b=false;
break;
}
}
row.style.display=(b?"":"none");
if(b&&this.alternateRows){
dojo.html[((alt++%2==1)?"addClass":"removeClass")](row,this.rowAlternateClass);
}
}
this.onFilter();
},createSorter:function(info){
var self=this;
var _fcc=[];
function createSortFunction(_fcd,dir){
var meta=self.columns[_fcd];
var _fd0=meta.getField();
return function(rowA,rowB){
if(dojo.html.hasAttribute(rowA,"emptyRow")){
return 1;
}
if(dojo.html.hasAttribute(rowB,"emptyRow")){
return -1;
}
var a=self.store.getField(self.getDataByRow(rowA),_fd0);
var b=self.store.getField(self.getDataByRow(rowB),_fd0);
var ret=0;
if(a>b){
ret=1;
}
if(a<b){
ret=-1;
}
return dir*ret;
};
}
var _fd6=0;
var max=Math.min(info.length,this.maxSortable,this.columns.length);
while(_fd6<max){
var _fd8=(info[_fd6].direction==0)?1:-1;
_fcc.push(createSortFunction(info[_fd6].index,_fd8));
_fd6++;
}
return function(rowA,rowB){
var idx=0;
while(idx<_fcc.length){
var ret=_fcc[idx++](rowA,rowB);
if(ret!=0){
return ret;
}
}
return 0;
};
},createRow:function(obj){
var row=document.createElement("tr");
dojo.html.disableSelection(row);
if(obj.key!=null){
row.setAttribute("value",obj.key);
}
for(var j=0;j<this.columns.length;j++){
var cell=document.createElement("td");
cell.setAttribute("align",this.columns[j].align);
cell.setAttribute("valign",this.columns[j].valign);
dojo.html.disableSelection(cell);
var val=this.store.getField(obj.src,this.columns[j].getField());
if(typeof (val)=="undefined"){
val="";
}
this.fillCell(cell,this.columns[j],val);
row.appendChild(cell);
}
return row;
},fillCell:function(cell,meta,val){
if(meta.sortType=="__markup__"){
cell.innerHTML=val;
}else{
if(meta.getType()==Date){
val=new Date(val);
if(!isNaN(val)){
var _fe5=this.defaultDateFormat;
if(meta.format){
_fe5=meta.format;
}
cell.innerHTML=dojo.date.strftime(val,_fe5);
}else{
cell.innerHTML=val;
}
}else{
if("Number number int Integer float Float".indexOf(meta.getType())>-1){
if(val.length==0){
val="0";
}
var n=parseFloat(val,10)+"";
if(n.indexOf(".")>-1){
n=dojo.math.round(parseFloat(val,10),2);
}
cell.innerHTML=n;
}else{
cell.innerHTML=val;
}
}
}
},prefill:function(){
this.isInitialized=false;
var body=this.domNode.tBodies[0];
while(body.childNodes.length>0){
body.removeChild(body.childNodes[0]);
}
if(this.minRows>0){
for(var i=0;i<this.minRows;i++){
var row=document.createElement("tr");
if(this.alternateRows){
dojo.html[((i%2==1)?"addClass":"removeClass")](row,this.rowAlternateClass);
}
row.setAttribute("emptyRow","true");
for(var j=0;j<this.columns.length;j++){
var cell=document.createElement("td");
cell.innerHTML="&nbsp;";
row.appendChild(cell);
}
body.appendChild(row);
}
}
},init:function(){
this.isInitialized=false;
var head=this.domNode.getElementsByTagName("thead")[0];
if(head.getElementsByTagName("tr").length==0){
var row=document.createElement("tr");
for(var i=0;i<this.columns.length;i++){
var cell=document.createElement("td");
cell.setAttribute("align",this.columns[i].align);
cell.setAttribute("valign",this.columns[i].valign);
dojo.html.disableSelection(cell);
cell.innerHTML=this.columns[i].label;
row.appendChild(cell);
if(!this.columns[i].noSort){
dojo.event.connect(cell,"onclick",this,"onSort");
}
}
dojo.html.prependChild(row,head);
}
if(this.store.get().length==0){
return false;
}
var idx=this.domNode.tBodies[0].rows.length;
if(!idx||idx==0||this.domNode.tBodies[0].rows[0].getAttribute("emptyRow")=="true"){
idx=0;
var body=this.domNode.tBodies[0];
while(body.childNodes.length>0){
body.removeChild(body.childNodes[0]);
}
var data=this.store.get();
for(var i=0;i<data.length;i++){
var row=this.createRow(data[i]);
body.appendChild(row);
idx++;
}
}
if(this.minRows>0&&idx<this.minRows){
idx=this.minRows-idx;
for(var i=0;i<idx;i++){
row=document.createElement("tr");
row.setAttribute("emptyRow","true");
for(var j=0;j<this.columns.length;j++){
cell=document.createElement("td");
cell.innerHTML="&nbsp;";
row.appendChild(cell);
}
body.appendChild(row);
}
}
var row=this.domNode.getElementsByTagName("thead")[0].rows[0];
var _ff4="td";
if(row.getElementsByTagName(_ff4).length==0){
_ff4="th";
}
var _ff5=row.getElementsByTagName(_ff4);
for(var i=0;i<_ff5.length;i++){
dojo.html.setClass(_ff5[i],this.headerClass);
}
for(var i=0;i<this.sortInformation.length;i++){
var idx=this.sortInformation[i].index;
var dir=(~this.sortInformation[i].direction)&1;
dojo.html.setClass(_ff5[idx],dir==0?this.headerDownClass:this.headerUpClass);
}
this.isInitialized=true;
return this.isInitialized;
},render:function(){
if(!this.isInitialized){
var b=this.init();
if(!b){
this.prefill();
return;
}
}
var rows=[];
var body=this.domNode.tBodies[0];
var _ffa=-1;
for(var i=0;i<body.rows.length;i++){
rows.push(body.rows[i]);
}
var _ffc=this.createSorter(this.sortInformation);
if(_ffc){
rows.sort(_ffc);
}
for(var i=0;i<rows.length;i++){
if(this.alternateRows){
dojo.html[((i%2==1)?"addClass":"removeClass")](rows[i],this.rowAlternateClass);
}
dojo.html[(this.isRowSelected(body.rows[i])?"addClass":"removeClass")](body.rows[i],this.rowSelectedClass);
body.appendChild(rows[i]);
}
},renderSelections:function(){
var body=this.domNode.tBodies[0];
for(var i=0;i<body.rows.length;i++){
dojo.html[(this.isRowSelected(body.rows[i])?"addClass":"removeClass")](body.rows[i],this.rowSelectedClass);
}
},initialize:function(){
var self=this;
dojo.event.connect(this.store,"onSetData",function(){
self.store.forEach(function(_1000){
_1000.isSelected=false;
});
self.isInitialized=false;
var body=self.domNode.tBodies[0];
if(body){
while(body.childNodes.length>0){
body.removeChild(body.childNodes[0]);
}
}
self.render();
});
dojo.event.connect(this.store,"onClearData",function(){
self.isInitialized=false;
self.render();
});
dojo.event.connect(this.store,"onAddData",function(_1002){
var row=self.createRow(_1002);
self.domNode.tBodies[0].appendChild(row);
self.render();
});
dojo.event.connect(this.store,"onAddDataRange",function(arr){
for(var i=0;i<arr.length;i++){
arr[i].isSelected=false;
var row=self.createRow(arr[i]);
self.domNode.tBodies[0].appendChild(row);
}
self.render();
});
dojo.event.connect(this.store,"onRemoveData",function(_1007){
var rows=self.domNode.tBodies[0].rows;
for(var i=0;i<rows.length;i++){
if(self.getDataByRow(rows[i])==_1007.src){
rows[i].parentNode.removeChild(rows[i]);
break;
}
}
self.render();
});
dojo.event.connect(this.store,"onUpdateField",function(obj,_100b,val){
var row=self.getRow(obj);
var idx=self.getColumnIndex(_100b);
if(row&&row.cells[idx]&&self.columns[idx]){
self.fillCell(row.cells[idx],self.columns[idx],val);
}
});
},postCreate:function(){
this.store.keyField=this.valueField;
if(this.domNode){
if(this.domNode.nodeName.toLowerCase()!="table"){
}
if(this.domNode.getElementsByTagName("thead")[0]){
var head=this.domNode.getElementsByTagName("thead")[0];
if(this.headClass.length>0){
head.className=this.headClass;
}
dojo.html.disableSelection(this.domNode);
this.parseMetadata(head);
var _1010="td";
if(head.getElementsByTagName(_1010).length==0){
_1010="th";
}
var _1011=head.getElementsByTagName(_1010);
for(var i=0;i<_1011.length;i++){
if(!this.columns[i].noSort){
dojo.event.connect(_1011[i],"onclick",this,"onSort");
}
}
}else{
this.domNode.appendChild(document.createElement("thead"));
}
if(this.domNode.tBodies.length<1){
var body=document.createElement("tbody");
this.domNode.appendChild(body);
}else{
var body=this.domNode.tBodies[0];
}
if(this.tbodyClass.length>0){
body.className=this.tbodyClass;
}
dojo.event.connect(body,"onclick",this,"onSelect");
this.parseData(body);
}
}});
dojo.provide("dojo.widget.PageContainer");
dojo.widget.defineWidget("dojo.widget.PageContainer",dojo.widget.HtmlWidget,{isContainer:true,doLayout:true,templateString:"<div dojoAttachPoint='containerNode'></div>",selectedChild:"",fillInTemplate:function(args,frag){
var _1016=this.getFragNodeRef(frag);
dojo.html.copyStyle(this.domNode,_1016);
dojo.widget.PageContainer.superclass.fillInTemplate.apply(this,arguments);
},postCreate:function(args,frag){
if(this.children.length){
dojo.lang.forEach(this.children,this._setupChild,this);
var _1019;
if(this.selectedChild){
this.selectChild(this.selectedChild);
}else{
for(var i=0;i<this.children.length;i++){
if(this.children[i].selected){
this.selectChild(this.children[i]);
break;
}
}
if(!this.selectedChildWidget){
this.selectChild(this.children[0]);
}
}
}
},addChild:function(child){
dojo.widget.PageContainer.superclass.addChild.apply(this,arguments);
this._setupChild(child);
this.onResized();
if(!this.selectedChildWidget){
this.selectChild(child);
}
},_setupChild:function(page){
page.hide();
page.domNode.style.position="relative";
dojo.event.topic.publish(this.widgetId+"-addChild",page);
},removeChild:function(page){
dojo.widget.PageContainer.superclass.removeChild.apply(this,arguments);
if(this._beingDestroyed){
return;
}
dojo.event.topic.publish(this.widgetId+"-removeChild",page);
this.onResized();
if(this.selectedChildWidget===page){
this.selectedChildWidget=undefined;
if(this.children.length>0){
this.selectChild(this.children[0],true);
}
}
},selectChild:function(page,_101f){
page=dojo.widget.byId(page);
this.correspondingPageButton=_101f;
if(this.selectedChildWidget){
this._hideChild(this.selectedChildWidget);
}
this.selectedChildWidget=page;
this.selectedChild=page.widgetId;
this._showChild(page);
page.isFirstChild=(page==this.children[0]);
page.isLastChild=(page==this.children[this.children.length-1]);
dojo.event.topic.publish(this.widgetId+"-selectChild",page);
},forward:function(){
var index=dojo.lang.find(this.children,this.selectedChildWidget);
this.selectChild(this.children[index+1]);
},back:function(){
var index=dojo.lang.find(this.children,this.selectedChildWidget);
this.selectChild(this.children[index-1]);
},onResized:function(){
if(this.doLayout&&this.selectedChildWidget){
with(this.selectedChildWidget.domNode.style){
top=dojo.html.getPixelValue(this.containerNode,"padding-top",true);
left=dojo.html.getPixelValue(this.containerNode,"padding-left",true);
}
var _1022=dojo.html.getContentBox(this.containerNode);
this.selectedChildWidget.resizeTo(_1022.width,_1022.height);
}
},_showChild:function(page){
if(this.doLayout){
var _1024=dojo.html.getContentBox(this.containerNode);
page.resizeTo(_1024.width,_1024.height);
}
page.selected=true;
page.show();
},_hideChild:function(page){
page.selected=false;
page.hide();
},closeChild:function(page){
var _1027=page.onClose(this,page);
if(_1027){
this.removeChild(page);
page.destroy();
}
},destroy:function(){
this._beingDestroyed=true;
dojo.event.topic.destroy(this.widgetId+"-addChild");
dojo.event.topic.destroy(this.widgetId+"-removeChild");
dojo.event.topic.destroy(this.widgetId+"-selectChild");
dojo.widget.PageContainer.superclass.destroy.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.PageController",dojo.widget.HtmlWidget,{templateString:"<span wairole='tablist' dojoAttachEvent='onKey'></span>",isContainer:true,containerId:"",buttonWidget:"PageButton","class":"dojoPageController",fillInTemplate:function(){
dojo.html.addClass(this.domNode,this["class"]);
dojo.widget.wai.setAttr(this.domNode,"waiRole","role","tablist");
},postCreate:function(){
this.pane2button={};
var _1028=dojo.widget.byId(this.containerId);
if(_1028){
dojo.lang.forEach(_1028.children,this.onAddChild,this);
}
dojo.event.topic.subscribe(this.containerId+"-addChild",this,"onAddChild");
dojo.event.topic.subscribe(this.containerId+"-removeChild",this,"onRemoveChild");
dojo.event.topic.subscribe(this.containerId+"-selectChild",this,"onSelectChild");
},destroy:function(){
dojo.event.topic.unsubscribe(this.containerId+"-addChild",this,"onAddChild");
dojo.event.topic.unsubscribe(this.containerId+"-removeChild",this,"onRemoveChild");
dojo.event.topic.unsubscribe(this.containerId+"-selectChild",this,"onSelectChild");
dojo.widget.PageController.superclass.destroy.apply(this,arguments);
},onAddChild:function(page){
var _102a=dojo.widget.createWidget(this.buttonWidget,{label:page.label,closeButton:page.closable});
this.addChild(_102a);
this.domNode.appendChild(_102a.domNode);
this.pane2button[page]=_102a;
page.controlButton=_102a;
var _this=this;
dojo.event.connect(_102a,"onClick",function(){
_this.onButtonClick(page);
});
dojo.event.connect(_102a,"onCloseButtonClick",function(){
_this.onCloseButtonClick(page);
});
},onRemoveChild:function(page){
if(this._currentChild==page){
this._currentChild=null;
}
var _102d=this.pane2button[page];
if(_102d){
_102d.destroy();
}
this.pane2button[page]=null;
},onSelectChild:function(page){
if(this._currentChild){
var _102f=this.pane2button[this._currentChild];
_102f.clearSelected();
}
var _1030=this.pane2button[page];
_1030.setSelected();
this._currentChild=page;
},onButtonClick:function(page){
var _1032=dojo.widget.byId(this.containerId);
_1032.selectChild(page,false,this);
},onCloseButtonClick:function(page){
var _1034=dojo.widget.byId(this.containerId);
_1034.closeChild(page);
},onKey:function(evt){
if((evt.keyCode==evt.KEY_RIGHT_ARROW)||(evt.keyCode==evt.KEY_LEFT_ARROW)){
var _1036=0;
var next=null;
var _1036=dojo.lang.find(this.children,this.pane2button[this._currentChild]);
if(evt.keyCode==evt.KEY_RIGHT_ARROW){
next=this.children[(_1036+1)%this.children.length];
}else{
next=this.children[(_1036+(this.children.length-1))%this.children.length];
}
dojo.event.browser.stopEvent(evt);
next.onClick();
}
}});
dojo.widget.defineWidget("dojo.widget.PageButton",dojo.widget.HtmlWidget,{templateString:"<span class='item'>"+"<span dojoAttachEvent='onClick' dojoAttachPoint='titleNode' class='selectButton'>${this.label}</span>"+"<span dojoAttachEvent='onClick:onCloseButtonClick' class='closeButton'>[X]</span>"+"</span>",label:"foo",closeButton:false,onClick:function(){
this.focus();
},onCloseButtonMouseOver:function(){
dojo.html.addClass(this.closeButtonNode,"closeHover");
},onCloseButtonMouseOut:function(){
dojo.html.removeClass(this.closeButtonNode,"closeHover");
},onCloseButtonClick:function(evt){
},setSelected:function(){
dojo.html.addClass(this.domNode,"current");
this.titleNode.setAttribute("tabIndex","0");
},clearSelected:function(){
dojo.html.removeClass(this.domNode,"current");
this.titleNode.setAttribute("tabIndex","-1");
},focus:function(){
if(this.titleNode.focus){
this.titleNode.focus();
}
}});
dojo.lang.extend(dojo.widget.Widget,{label:"",selected:false,closable:false,onClose:function(){
return true;
}});
dojo.provide("dojo.widget.TabContainer");
dojo.widget.defineWidget("dojo.widget.TabContainer",dojo.widget.PageContainer,{labelPosition:"top",closeButton:"none",templateString:null,templatePath:dojo.uri.dojoUri("src/widget/templates/TabContainer.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/TabContainer.css"),selectedTab:"",postMixInProperties:function(){
if(this.selectedTab){
dojo.deprecated("selectedTab deprecated, use selectedChild instead, will be removed in","0.5");
this.selectedChild=this.selectedTab;
}
if(this.closeButton!="none"){
dojo.deprecated("closeButton deprecated, use closable='true' on each child instead, will be removed in","0.5");
}
dojo.widget.TabContainer.superclass.postMixInProperties.apply(this,arguments);
},fillInTemplate:function(){
this.tablist=dojo.widget.createWidget("TabController",{id:this.widgetId+"_tablist",labelPosition:this.labelPosition,doLayout:this.doLayout,containerId:this.widgetId},this.tablistNode);
dojo.widget.TabContainer.superclass.fillInTemplate.apply(this,arguments);
},postCreate:function(args,frag){
dojo.widget.TabContainer.superclass.postCreate.apply(this,arguments);
this.onResized();
},_setupChild:function(tab){
if(this.closeButton=="tab"||this.closeButton=="pane"){
tab.closable=true;
}
dojo.html.addClass(tab.domNode,"dojoTabPane");
dojo.widget.TabContainer.superclass._setupChild.apply(this,arguments);
},onResized:function(){
if(!this.doLayout){
return;
}
var _103c=this.labelPosition.replace(/-h/,"");
var _103d=[{domNode:this.tablist.domNode,layoutAlign:_103c},{domNode:this.containerNode,layoutAlign:"client"}];
dojo.widget.html.layout(this.domNode,_103d);
if(this.selectedChildWidget){
var _103e=dojo.html.getContentBox(this.containerNode);
this.selectedChildWidget.resizeTo(_103e.width,_103e.height);
}
},selectTab:function(tab,_1040){
dojo.deprecated("use selectChild() rather than selectTab(), selectTab() will be removed in","0.5");
this.selectChild(tab,_1040);
},onKey:function(e){
if(e.keyCode==e.KEY_UP_ARROW&&e.ctrlKey){
var _1042=this.correspondingTabButton||this.selectedTabWidget.tabButton;
_1042.focus();
dojo.event.browser.stopEvent(e);
}else{
if(e.keyCode==e.KEY_DELETE&&e.altKey){
if(this.selectedChildWidget.closable){
this.closeChild(this.selectedChildWidget);
dojo.event.browser.stopEvent(e);
}
}
}
},destroy:function(){
this.tablist.destroy();
dojo.widget.TabContainer.superclass.destroy.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.TabController",dojo.widget.PageController,{templateString:"<div wairole='tablist' dojoAttachEvent='onKey'></div>",labelPosition:"top",doLayout:true,"class":"",buttonWidget:"TabButton",postMixInProperties:function(){
if(!this["class"]){
this["class"]="dojoTabLabels-"+this.labelPosition+(this.doLayout?"":" dojoTabNoLayout");
}
dojo.widget.TabController.superclass.postMixInProperties.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.TabButton",dojo.widget.PageButton,{templateString:"<div class='dojoTab' dojoAttachEvent='onClick'>"+"<div dojoAttachPoint='innerDiv'>"+"<span dojoAttachPoint='titleNode' tabIndex='-1' waiRole='tab'>${this.label}</span>"+"<span dojoAttachPoint='closeButtonNode' class='close closeImage' style='${this.closeButtonStyle}'"+"    dojoAttachEvent='onMouseOver:onCloseButtonMouseOver; onMouseOut:onCloseButtonMouseOut; onClick:onCloseButtonClick'></span>"+"</div>"+"</div>",postMixInProperties:function(){
this.closeButtonStyle=this.closeButton?"":"display: none";
dojo.widget.TabButton.superclass.postMixInProperties.apply(this,arguments);
},fillInTemplate:function(){
dojo.html.disableSelection(this.titleNode);
dojo.widget.TabButton.superclass.fillInTemplate.apply(this,arguments);
},onCloseButtonClick:function(evt){
evt.stopPropagation();
dojo.widget.TabButton.superclass.onCloseButtonClick.apply(this,arguments);
}});
dojo.widget.defineWidget("dojo.widget.a11y.TabButton",dojo.widget.TabButton,{imgPath:dojo.uri.dojoUri("src/widget/templates/images/tab_close.gif"),templateString:"<div class='dojoTab' dojoAttachEvent='onClick;onKey'>"+"<div dojoAttachPoint='innerDiv'>"+"<span dojoAttachPoint='titleNode' tabIndex='-1' waiRole='tab'>${this.label}</span>"+"<img class='close' src='${this.imgPath}' alt='[x]' style='${this.closeButtonStyle}'"+"    dojoAttachEvent='onClick:onCloseButtonClick'>"+"</div>"+"</div>"});
dojo.provide("dojo.widget.Button");
dojo.widget.defineWidget("dojo.widget.Button",dojo.widget.HtmlWidget,{isContainer:true,caption:"",templatePath:dojo.uri.dojoUri("src/widget/templates/ButtonTemplate.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/ButtonTemplate.css"),inactiveImg:"src/widget/templates/images/soriaButton-",activeImg:"src/widget/templates/images/soriaActive-",pressedImg:"src/widget/templates/images/soriaPressed-",disabledImg:"src/widget/templates/images/soriaDisabled-",width2height:1/3,fillInTemplate:function(){
if(this.caption){
this.containerNode.appendChild(document.createTextNode(this.caption));
}
dojo.html.disableSelection(this.containerNode);
},postCreate:function(){
this._sizeMyself();
},_sizeMyself:function(){
if(this.domNode.parentNode){
var _1044=document.createElement("span");
dojo.html.insertBefore(_1044,this.domNode);
}
dojo.body().appendChild(this.domNode);
this._sizeMyselfHelper();
if(_1044){
dojo.html.insertBefore(this.domNode,_1044);
dojo.html.removeNode(_1044);
}
},_sizeMyselfHelper:function(){
var mb=dojo.html.getMarginBox(this.containerNode);
this.height=mb.height;
this.containerWidth=mb.width;
var _1046=this.height*this.width2height;
this.containerNode.style.left=_1046+"px";
this.leftImage.height=this.rightImage.height=this.centerImage.height=this.height;
this.leftImage.width=this.rightImage.width=_1046+1;
this.centerImage.width=this.containerWidth;
this.centerImage.style.left=_1046+"px";
this._setImage(this.disabled?this.disabledImg:this.inactiveImg);
if(this.disabled){
dojo.html.prependClass(this.domNode,"dojoButtonDisabled");
this.domNode.removeAttribute("tabIndex");
dojo.widget.wai.setAttr(this.domNode,"waiState","disabled",true);
}else{
dojo.html.removeClass(this.domNode,"dojoButtonDisabled");
this.domNode.setAttribute("tabIndex","0");
dojo.widget.wai.setAttr(this.domNode,"waiState","disabled",false);
}
this.domNode.style.height=this.height+"px";
this.domNode.style.width=(this.containerWidth+2*_1046)+"px";
},onMouseOver:function(e){
if(this.disabled){
return;
}
dojo.html.prependClass(this.buttonNode,"dojoButtonHover");
this._setImage(this.activeImg);
},onMouseDown:function(e){
if(this.disabled){
return;
}
dojo.html.prependClass(this.buttonNode,"dojoButtonDepressed");
dojo.html.removeClass(this.buttonNode,"dojoButtonHover");
this._setImage(this.pressedImg);
},onMouseUp:function(e){
if(this.disabled){
return;
}
dojo.html.prependClass(this.buttonNode,"dojoButtonHover");
dojo.html.removeClass(this.buttonNode,"dojoButtonDepressed");
this._setImage(this.activeImg);
},onMouseOut:function(e){
if(this.disabled){
return;
}
if(e.toElement&&dojo.html.isDescendantOf(e.toElement,this.buttonNode)){
return;
}
dojo.html.removeClass(this.buttonNode,"dojoButtonHover");
dojo.html.removeClass(this.buttonNode,"dojoButtonDepressed");
this._setImage(this.inactiveImg);
},onKey:function(e){
if(!e.key){
return;
}
var menu=dojo.widget.getWidgetById(this.menuId);
if(e.key==e.KEY_ENTER||e.key==" "){
this.onMouseDown(e);
this.buttonClick(e);
dojo.lang.setTimeout(this,"onMouseUp",75,e);
dojo.event.browser.stopEvent(e);
}
if(menu&&menu.isShowingNow&&e.key==e.KEY_DOWN_ARROW){
dojo.event.disconnect(this.domNode,"onblur",this,"onBlur");
}
},onFocus:function(e){
var menu=dojo.widget.getWidgetById(this.menuId);
if(menu){
dojo.event.connectOnce(this.domNode,"onblur",this,"onBlur");
}
},onBlur:function(e){
var menu=dojo.widget.getWidgetById(this.menuId);
if(!menu){
return;
}
if(menu.close&&menu.isShowingNow){
menu.close();
}
},buttonClick:function(e){
if(!this.disabled){
try{
this.domNode.focus();
}
catch(e2){
}
this.onClick(e);
}
},onClick:function(e){
},_setImage:function(_1053){
this.leftImage.src=dojo.uri.dojoUri(_1053+"l.gif");
this.centerImage.src=dojo.uri.dojoUri(_1053+"c.gif");
this.rightImage.src=dojo.uri.dojoUri(_1053+"r.gif");
},_toggleMenu:function(_1054){
var menu=dojo.widget.getWidgetById(_1054);
if(!menu){
return;
}
if(menu.open&&!menu.isShowingNow){
var pos=dojo.html.getAbsolutePosition(this.domNode,false);
menu.open(pos.x,pos.y+this.height,this);
}else{
if(menu.close&&menu.isShowingNow){
menu.close();
}else{
menu.toggle();
}
}
},setCaption:function(_1057){
this.caption=_1057;
this.containerNode.innerHTML=_1057;
this._sizeMyself();
},setDisabled:function(_1058){
this.disabled=_1058;
this._sizeMyself();
}});
dojo.widget.defineWidget("dojo.widget.DropDownButton",dojo.widget.Button,{menuId:"",downArrow:"src/widget/templates/images/whiteDownArrow.gif",disabledDownArrow:"src/widget/templates/images/whiteDownArrow.gif",fillInTemplate:function(){
dojo.widget.DropDownButton.superclass.fillInTemplate.apply(this,arguments);
this.arrow=document.createElement("img");
dojo.html.setClass(this.arrow,"downArrow");
dojo.widget.wai.setAttr(this.domNode,"waiState","haspopup",this.menuId);
},_sizeMyselfHelper:function(){
this.arrow.src=dojo.uri.dojoUri(this.disabled?this.disabledDownArrow:this.downArrow);
this.containerNode.appendChild(this.arrow);
dojo.widget.DropDownButton.superclass._sizeMyselfHelper.call(this);
},onClick:function(e){
this._toggleMenu(this.menuId);
}});
dojo.widget.defineWidget("dojo.widget.ComboButton",dojo.widget.Button,{menuId:"",templatePath:dojo.uri.dojoUri("src/widget/templates/ComboButtonTemplate.html"),splitWidth:2,arrowWidth:5,_sizeMyselfHelper:function(e){
var mb=dojo.html.getMarginBox(this.containerNode);
this.height=mb.height;
this.containerWidth=mb.width;
var _105c=this.height/3;
if(this.disabled){
dojo.widget.wai.setAttr(this.domNode,"waiState","disabled",true);
this.domNode.removeAttribute("tabIndex");
}else{
dojo.widget.wai.setAttr(this.domNode,"waiState","disabled",false);
this.domNode.setAttribute("tabIndex","0");
}
this.leftImage.height=this.rightImage.height=this.centerImage.height=this.arrowBackgroundImage.height=this.height;
this.leftImage.width=_105c+1;
this.centerImage.width=this.containerWidth;
this.buttonNode.style.height=this.height+"px";
this.buttonNode.style.width=_105c+this.containerWidth+"px";
this._setImage(this.disabled?this.disabledImg:this.inactiveImg);
this.arrowBackgroundImage.width=this.arrowWidth;
this.rightImage.width=_105c+1;
this.rightPart.style.height=this.height+"px";
this.rightPart.style.width=this.arrowWidth+_105c+"px";
this._setImageR(this.disabled?this.disabledImg:this.inactiveImg);
this.domNode.style.height=this.height+"px";
var _105d=this.containerWidth+this.splitWidth+this.arrowWidth+2*_105c;
this.domNode.style.width=_105d+"px";
},_setImage:function(_105e){
this.leftImage.src=dojo.uri.dojoUri(_105e+"l.gif");
this.centerImage.src=dojo.uri.dojoUri(_105e+"c.gif");
},rightOver:function(e){
if(this.disabled){
return;
}
dojo.html.prependClass(this.rightPart,"dojoButtonHover");
this._setImageR(this.activeImg);
},rightDown:function(e){
if(this.disabled){
return;
}
dojo.html.prependClass(this.rightPart,"dojoButtonDepressed");
dojo.html.removeClass(this.rightPart,"dojoButtonHover");
this._setImageR(this.pressedImg);
},rightUp:function(e){
if(this.disabled){
return;
}
dojo.html.prependClass(this.rightPart,"dojoButtonHover");
dojo.html.removeClass(this.rightPart,"dojoButtonDepressed");
this._setImageR(this.activeImg);
},rightOut:function(e){
if(this.disabled){
return;
}
dojo.html.removeClass(this.rightPart,"dojoButtonHover");
dojo.html.removeClass(this.rightPart,"dojoButtonDepressed");
this._setImageR(this.inactiveImg);
},rightClick:function(e){
if(this.disabled){
return;
}
try{
this.domNode.focus();
}
catch(e2){
}
this._toggleMenu(this.menuId);
},_setImageR:function(_1064){
this.arrowBackgroundImage.src=dojo.uri.dojoUri(_1064+"c.gif");
this.rightImage.src=dojo.uri.dojoUri(_1064+"r.gif");
},onKey:function(e){
if(!e.key){
return;
}
var menu=dojo.widget.getWidgetById(this.menuId);
if(e.key==e.KEY_ENTER||e.key==" "){
this.onMouseDown(e);
this.buttonClick(e);
dojo.lang.setTimeout(this,"onMouseUp",75,e);
dojo.event.browser.stopEvent(e);
}else{
if(e.key==e.KEY_DOWN_ARROW&&e.altKey){
this.rightDown(e);
this.rightClick(e);
dojo.lang.setTimeout(this,"rightUp",75,e);
dojo.event.browser.stopEvent(e);
}else{
if(menu&&menu.isShowingNow&&e.key==e.KEY_DOWN_ARROW){
dojo.event.disconnect(this.domNode,"onblur",this,"onBlur");
}
}
}
}});
dojo.provide("dojo.widget.Toolbar");
dojo.widget.defineWidget("dojo.widget.ToolbarContainer",dojo.widget.HtmlWidget,{isContainer:true,templateString:"<div class=\"toolbarContainer\" dojoAttachPoint=\"containerNode\"></div>",templateCssPath:dojo.uri.dojoUri("src/widget/templates/Toolbar.css"),getItem:function(name){
if(name instanceof dojo.widget.ToolbarItem){
return name;
}
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
var item=child.getItem(name);
if(item){
return item;
}
}
}
return null;
},getItems:function(){
var items=[];
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
items=items.concat(child.getItems());
}
}
return items;
},enable:function(){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
child.enable.apply(child,arguments);
}
}
},disable:function(){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
child.disable.apply(child,arguments);
}
}
},select:function(name){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
child.select(arguments);
}
}
},deselect:function(name){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
child.deselect(arguments);
}
}
},getItemsState:function(){
var _1078={};
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
dojo.lang.mixin(_1078,child.getItemsState());
}
}
return _1078;
},getItemsActiveState:function(){
var _107b={};
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
dojo.lang.mixin(_107b,child.getItemsActiveState());
}
}
return _107b;
},getItemsSelectedState:function(){
var _107e={};
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.Toolbar){
dojo.lang.mixin(_107e,child.getItemsSelectedState());
}
}
return _107e;
}});
dojo.widget.defineWidget("dojo.widget.Toolbar",dojo.widget.HtmlWidget,{isContainer:true,templateString:"<div class=\"toolbar\" dojoAttachPoint=\"containerNode\" unselectable=\"on\" dojoOnMouseover=\"_onmouseover\" dojoOnMouseout=\"_onmouseout\" dojoOnClick=\"_onclick\" dojoOnMousedown=\"_onmousedown\" dojoOnMouseup=\"_onmouseup\"></div>",_getItem:function(node){
var start=new Date();
var _1083=null;
while(node&&node!=this.domNode){
if(dojo.html.hasClass(node,"toolbarItem")){
var _1084=dojo.widget.manager.getWidgetsByFilter(function(w){
return w.domNode==node;
});
if(_1084.length==1){
_1083=_1084[0];
break;
}else{
if(_1084.length>1){
dojo.raise("Toolbar._getItem: More than one widget matches the node");
}
}
}
node=node.parentNode;
}
return _1083;
},_onmouseover:function(e){
var _1087=this._getItem(e.target);
if(_1087&&_1087._onmouseover){
_1087._onmouseover(e);
}
},_onmouseout:function(e){
var _1089=this._getItem(e.target);
if(_1089&&_1089._onmouseout){
_1089._onmouseout(e);
}
},_onclick:function(e){
var _108b=this._getItem(e.target);
if(_108b&&_108b._onclick){
_108b._onclick(e);
}
},_onmousedown:function(e){
var _108d=this._getItem(e.target);
if(_108d&&_108d._onmousedown){
_108d._onmousedown(e);
}
},_onmouseup:function(e){
var _108f=this._getItem(e.target);
if(_108f&&_108f._onmouseup){
_108f._onmouseup(e);
}
},addChild:function(item,pos,props){
var _1093=dojo.widget.ToolbarItem.make(item,null,props);
var ret=dojo.widget.Toolbar.superclass.addChild.call(this,_1093,null,pos,null);
return ret;
},push:function(){
for(var i=0;i<arguments.length;i++){
this.addChild(arguments[i]);
}
},getItem:function(name){
if(name instanceof dojo.widget.ToolbarItem){
return name;
}
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem&&child._name==name){
return child;
}
}
return null;
},getItems:function(){
var items=[];
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem){
items.push(child);
}
}
return items;
},getItemsState:function(){
var _109c={};
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem){
_109c[child._name]={selected:child._selected,enabled:!child.disabled};
}
}
return _109c;
},getItemsActiveState:function(){
var _109f=this.getItemsState();
for(var item in _109f){
_109f[item]=_109f[item].enabled;
}
return _109f;
},getItemsSelectedState:function(){
var _10a1=this.getItemsState();
for(var item in _10a1){
_10a1[item]=_10a1[item].selected;
}
return _10a1;
},enable:function(){
var items=arguments.length?arguments:this.children;
for(var i=0;i<items.length;i++){
var child=this.getItem(items[i]);
if(child instanceof dojo.widget.ToolbarItem){
child.enable(false,true);
}
}
},disable:function(){
var items=arguments.length?arguments:this.children;
for(var i=0;i<items.length;i++){
var child=this.getItem(items[i]);
if(child instanceof dojo.widget.ToolbarItem){
child.disable();
}
}
},select:function(){
for(var i=0;i<arguments.length;i++){
var name=arguments[i];
var item=this.getItem(name);
if(item){
item.select();
}
}
},deselect:function(){
for(var i=0;i<arguments.length;i++){
var name=arguments[i];
var item=this.getItem(name);
if(item){
item.disable();
}
}
},setValue:function(){
for(var i=0;i<arguments.length;i+=2){
var name=arguments[i],value=arguments[i+1];
var item=this.getItem(name);
if(item){
if(item instanceof dojo.widget.ToolbarItem){
item.setValue(value);
}
}
}
}});
dojo.widget.defineWidget("dojo.widget.ToolbarItem",dojo.widget.HtmlWidget,{templateString:"<span unselectable=\"on\" class=\"toolbarItem\"></span>",_name:null,getName:function(){
return this._name;
},setName:function(value){
return (this._name=value);
},getValue:function(){
return this.getName();
},setValue:function(value){
return this.setName(value);
},_selected:false,isSelected:function(){
return this._selected;
},setSelected:function(is,force,_10b7){
if(!this._toggleItem&&!force){
return;
}
is=Boolean(is);
if(force||!this.disabled&&this._selected!=is){
this._selected=is;
this.update();
if(!_10b7){
this._fireEvent(is?"onSelect":"onDeselect");
this._fireEvent("onChangeSelect");
}
}
},select:function(force,_10b9){
return this.setSelected(true,force,_10b9);
},deselect:function(force,_10bb){
return this.setSelected(false,force,_10bb);
},_toggleItem:false,isToggleItem:function(){
return this._toggleItem;
},setToggleItem:function(value){
this._toggleItem=Boolean(value);
},toggleSelected:function(force){
return this.setSelected(!this._selected,force);
},isEnabled:function(){
return !this.disabled;
},setEnabled:function(is,force,_10c0){
is=Boolean(is);
if(force||this.disabled==is){
this.disabled=!is;
this.update();
if(!_10c0){
this._fireEvent(this.disabled?"onDisable":"onEnable");
this._fireEvent("onChangeEnabled");
}
}
return !this.disabled;
},enable:function(force,_10c2){
return this.setEnabled(true,force,_10c2);
},disable:function(force,_10c4){
return this.setEnabled(false,force,_10c4);
},toggleEnabled:function(force,_10c6){
return this.setEnabled(this.disabled,force,_10c6);
},_icon:null,getIcon:function(){
return this._icon;
},setIcon:function(value){
var icon=dojo.widget.Icon.make(value);
if(this._icon){
this._icon.setIcon(icon);
}else{
this._icon=icon;
}
var _10c9=this._icon.getNode();
if(_10c9.parentNode!=this.domNode){
if(this.domNode.hasChildNodes()){
this.domNode.insertBefore(_10c9,this.domNode.firstChild);
}else{
this.domNode.appendChild(_10c9);
}
}
return this._icon;
},_label:"",getLabel:function(){
return this._label;
},setLabel:function(value){
var ret=(this._label=value);
if(!this.labelNode){
this.labelNode=document.createElement("span");
this.domNode.appendChild(this.labelNode);
}
this.labelNode.innerHTML="";
this.labelNode.appendChild(document.createTextNode(this._label));
this.update();
return ret;
},update:function(){
if(this.disabled){
this._selected=false;
dojo.html.addClass(this.domNode,"disabled");
dojo.html.removeClass(this.domNode,"down");
dojo.html.removeClass(this.domNode,"hover");
}else{
dojo.html.removeClass(this.domNode,"disabled");
if(this._selected){
dojo.html.addClass(this.domNode,"selected");
}else{
dojo.html.removeClass(this.domNode,"selected");
}
}
this._updateIcon();
},_updateIcon:function(){
if(this._icon){
if(this.disabled){
this._icon.disable();
}else{
if(this._cssHover){
this._icon.hover();
}else{
if(this._selected){
this._icon.select();
}else{
this._icon.enable();
}
}
}
}
},_fireEvent:function(evt){
if(typeof this[evt]=="function"){
var args=[this];
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
this[evt].apply(this,args);
}
},_onmouseover:function(e){
if(this.disabled){
return;
}
dojo.html.addClass(this.domNode,"hover");
this._fireEvent("onMouseOver");
},_onmouseout:function(e){
dojo.html.removeClass(this.domNode,"hover");
dojo.html.removeClass(this.domNode,"down");
if(!this._selected){
dojo.html.removeClass(this.domNode,"selected");
}
this._fireEvent("onMouseOut");
},_onclick:function(e){
if(!this.disabled&&!this._toggleItem){
this._fireEvent("onClick");
}
},_onmousedown:function(e){
if(e.preventDefault){
e.preventDefault();
}
if(this.disabled){
return;
}
dojo.html.addClass(this.domNode,"down");
if(this._toggleItem){
if(this.parent.preventDeselect&&this._selected){
return;
}
this.toggleSelected();
}
this._fireEvent("onMouseDown");
},_onmouseup:function(e){
dojo.html.removeClass(this.domNode,"down");
this._fireEvent("onMouseUp");
},onClick:function(){
},onMouseOver:function(){
},onMouseOut:function(){
},onMouseDown:function(){
},onMouseUp:function(){
},fillInTemplate:function(args,frag){
if(args.name){
this._name=args.name;
}
if(args.selected){
this.select();
}
if(args.disabled){
this.disable();
}
if(args.label){
this.setLabel(args.label);
}
if(args.icon){
this.setIcon(args.icon);
}
if(args.toggleitem||args.toggleItem){
this.setToggleItem(true);
}
}});
dojo.widget.ToolbarItem.make=function(wh,_10d7,props){
var item=null;
if(wh instanceof Array){
item=dojo.widget.createWidget("ToolbarButtonGroup",props);
item.setName(wh[0]);
for(var i=1;i<wh.length;i++){
item.addChild(wh[i]);
}
}else{
if(wh instanceof dojo.widget.ToolbarItem){
item=wh;
}else{
if(wh instanceof dojo.uri.Uri){
item=dojo.widget.createWidget("ToolbarButton",dojo.lang.mixin(props||{},{icon:new dojo.widget.Icon(wh.toString())}));
}else{
if(_10d7){
item=dojo.widget.createWidget(wh,props);
}else{
if(typeof wh=="string"||wh instanceof String){
switch(wh.charAt(0)){
case "|":
case "-":
case "/":
item=dojo.widget.createWidget("ToolbarSeparator",props);
break;
case " ":
if(wh.length==1){
item=dojo.widget.createWidget("ToolbarSpace",props);
}else{
item=dojo.widget.createWidget("ToolbarFlexibleSpace",props);
}
break;
default:
if(/\.(gif|jpg|jpeg|png)$/i.test(wh)){
item=dojo.widget.createWidget("ToolbarButton",dojo.lang.mixin(props||{},{icon:new dojo.widget.Icon(wh.toString())}));
}else{
item=dojo.widget.createWidget("ToolbarButton",dojo.lang.mixin(props||{},{label:wh.toString()}));
}
}
}else{
if(wh&&wh.tagName&&/^img$/i.test(wh.tagName)){
item=dojo.widget.createWidget("ToolbarButton",dojo.lang.mixin(props||{},{icon:wh}));
}else{
item=dojo.widget.createWidget("ToolbarButton",dojo.lang.mixin(props||{},{label:wh.toString()}));
}
}
}
}
}
}
return item;
};
dojo.widget.defineWidget("dojo.widget.ToolbarButtonGroup",dojo.widget.ToolbarItem,{isContainer:true,templateString:"<span unselectable=\"on\" class=\"toolbarButtonGroup\" dojoAttachPoint=\"containerNode\"></span>",defaultButton:"",postCreate:function(){
for(var i=0;i<this.children.length;i++){
this._injectChild(this.children[i]);
}
},addChild:function(item,pos,props){
var _10df=dojo.widget.ToolbarItem.make(item,null,dojo.lang.mixin(props||{},{toggleItem:true}));
var ret=dojo.widget.ToolbarButtonGroup.superclass.addChild.call(this,_10df,null,pos,null);
this._injectChild(_10df);
return ret;
},_injectChild:function(_10e1){
dojo.event.connect(_10e1,"onSelect",this,"onChildSelected");
dojo.event.connect(_10e1,"onDeselect",this,"onChildDeSelected");
if(_10e1._name==this.defaultButton||(typeof this.defaultButton=="number"&&this.children.length-1==this.defaultButton)){
_10e1.select(false,true);
}
},getItem:function(name){
if(name instanceof dojo.widget.ToolbarItem){
return name;
}
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem&&child._name==name){
return child;
}
}
return null;
},getItems:function(){
var items=[];
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem){
items.push(child);
}
}
return items;
},onChildSelected:function(e){
this.select(e._name);
},onChildDeSelected:function(e){
this._fireEvent("onChangeSelect",this._value);
},enable:function(force,_10eb){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem){
child.enable(force,_10eb);
if(child._name==this._value){
child.select(force,_10eb);
}
}
}
},disable:function(force,_10ef){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem){
child.disable(force,_10ef);
}
}
},_value:"",getValue:function(){
return this._value;
},select:function(name,force,_10f4){
for(var i=0;i<this.children.length;i++){
var child=this.children[i];
if(child instanceof dojo.widget.ToolbarItem){
if(child._name==name){
child.select(force,_10f4);
this._value=name;
}else{
child.deselect(true,true);
}
}
}
if(!_10f4){
this._fireEvent("onSelect",this._value);
this._fireEvent("onChangeSelect",this._value);
}
},setValue:this.select,preventDeselect:false});
dojo.widget.defineWidget("dojo.widget.ToolbarButton",dojo.widget.ToolbarItem,{fillInTemplate:function(args,frag){
dojo.widget.ToolbarButton.superclass.fillInTemplate.call(this,args,frag);
dojo.html.addClass(this.domNode,"toolbarButton");
if(this._icon){
this.setIcon(this._icon);
}
if(this._label){
this.setLabel(this._label);
}
if(!this._name){
if(this._label){
this.setName(this._label);
}else{
if(this._icon){
var src=this._icon.getSrc("enabled").match(/[\/^]([^\.\/]+)\.(gif|jpg|jpeg|png)$/i);
if(src){
this.setName(src[1]);
}
}else{
this._name=this._widgetId;
}
}
}
}});
dojo.widget.defineWidget("dojo.widget.ToolbarDialog",dojo.widget.ToolbarButton,{fillInTemplate:function(args,frag){
dojo.widget.ToolbarDialog.superclass.fillInTemplate.call(this,args,frag);
dojo.event.connect(this,"onSelect",this,"showDialog");
dojo.event.connect(this,"onDeselect",this,"hideDialog");
},showDialog:function(e){
dojo.lang.setTimeout(dojo.event.connect,1,document,"onmousedown",this,"deselect");
},hideDialog:function(e){
dojo.event.disconnect(document,"onmousedown",this,"deselect");
}});
dojo.widget.defineWidget("dojo.widget.ToolbarMenu",dojo.widget.ToolbarDialog,{});
dojo.widget.ToolbarMenuItem=function(){
};
dojo.widget.defineWidget("dojo.widget.ToolbarSeparator",dojo.widget.ToolbarItem,{templateString:"<span unselectable=\"on\" class=\"toolbarItem toolbarSeparator\"></span>",defaultIconPath:new dojo.uri.dojoUri("src/widget/templates/buttons/sep.gif"),fillInTemplate:function(args,frag,skip){
dojo.widget.ToolbarSeparator.superclass.fillInTemplate.call(this,args,frag);
this._name=this.widgetId;
if(!skip){
if(!this._icon){
this.setIcon(this.defaultIconPath);
}
this.domNode.appendChild(this._icon.getNode());
}
},_onmouseover:null,_onmouseout:null,_onclick:null,_onmousedown:null,_onmouseup:null});
dojo.widget.defineWidget("dojo.widget.ToolbarSpace",dojo.widget.ToolbarSeparator,{fillInTemplate:function(args,frag,skip){
dojo.widget.ToolbarSpace.superclass.fillInTemplate.call(this,args,frag,true);
if(!skip){
dojo.html.addClass(this.domNode,"toolbarSpace");
}
}});
dojo.widget.defineWidget("dojo.widget.ToolbarSelect",dojo.widget.ToolbarItem,{templateString:"<span class=\"toolbarItem toolbarSelect\" unselectable=\"on\"><select dojoAttachPoint=\"selectBox\" dojoOnChange=\"changed\"></select></span>",fillInTemplate:function(args,frag){
dojo.widget.ToolbarSelect.superclass.fillInTemplate.call(this,args,frag,true);
var keys=args.values;
var i=0;
for(var val in keys){
var opt=document.createElement("option");
opt.setAttribute("value",keys[val]);
opt.innerHTML=val;
this.selectBox.appendChild(opt);
}
},changed:function(e){
this._fireEvent("onSetValue",this.selectBox.value);
},setEnabled:function(is,force,_110d){
var ret=dojo.widget.ToolbarSelect.superclass.setEnabled.call(this,is,force,_110d);
this.selectBox.disabled=this.disabled;
return ret;
},_onmouseover:null,_onmouseout:null,_onclick:null,_onmousedown:null,_onmouseup:null});
dojo.widget.Icon=function(_110f,_1110,_1111,_1112){
if(!arguments.length){
throw new Error("Icon must have at least an enabled state");
}
var _1113=["enabled","disabled","hovered","selected"];
var _1114="enabled";
var _1115=document.createElement("img");
this.getState=function(){
return _1114;
};
this.setState=function(value){
if(dojo.lang.inArray(_1113,value)){
if(this[value]){
_1114=value;
var img=this[_1114];
if((dojo.render.html.ie55||dojo.render.html.ie60)&&img.src&&img.src.match(/[.]png$/i)){
_1115.width=img.width||img.offsetWidth;
_1115.height=img.height||img.offsetHeight;
_1115.setAttribute("src",dojo.uri.dojoUri("src/widget/templates/images/blank.gif").uri);
_1115.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+img.src+"',sizingMethod='image')";
}else{
_1115.setAttribute("src",img.src);
}
}
}else{
throw new Error("Invalid state set on Icon (state: "+value+")");
}
};
this.setSrc=function(state,value){
if(/^img$/i.test(value.tagName)){
this[state]=value;
}else{
if(typeof value=="string"||value instanceof String||value instanceof dojo.uri.Uri){
this[state]=new Image();
this[state].src=value.toString();
}
}
return this[state];
};
this.setIcon=function(icon){
for(var i=0;i<_1113.length;i++){
if(icon[_1113[i]]){
this.setSrc(_1113[i],icon[_1113[i]]);
}
}
this.update();
};
this.enable=function(){
this.setState("enabled");
};
this.disable=function(){
this.setState("disabled");
};
this.hover=function(){
this.setState("hovered");
};
this.select=function(){
this.setState("selected");
};
this.getSize=function(){
return {width:_1115.width||_1115.offsetWidth,height:_1115.height||_1115.offsetHeight};
};
this.setSize=function(w,h){
_1115.width=w;
_1115.height=h;
return {width:w,height:h};
};
this.getNode=function(){
return _1115;
};
this.getSrc=function(state){
if(state){
return this[state].src;
}
return _1115.src||"";
};
this.update=function(){
this.setState(_1114);
};
for(var i=0;i<_1113.length;i++){
var arg=arguments[i];
var state=_1113[i];
this[state]=null;
if(!arg){
continue;
}
this.setSrc(state,arg);
}
this.enable();
};
dojo.widget.Icon.make=function(a,b,c,d){
for(var i=0;i<arguments.length;i++){
if(arguments[i] instanceof dojo.widget.Icon){
return arguments[i];
}
}
return new dojo.widget.Icon(a,b,c,d);
};
dojo.widget.defineWidget("dojo.widget.ToolbarColorDialog",dojo.widget.ToolbarDialog,{palette:"7x10",fillInTemplate:function(args,frag){
dojo.widget.ToolbarColorDialog.superclass.fillInTemplate.call(this,args,frag);
this.dialog=dojo.widget.createWidget("ColorPalette",{palette:this.palette});
this.dialog.domNode.style.position="absolute";
dojo.event.connect(this.dialog,"onColorSelect",this,"_setValue");
},_setValue:function(color){
this._value=color;
this._fireEvent("onSetValue",color);
},showDialog:function(e){
dojo.widget.ToolbarColorDialog.superclass.showDialog.call(this,e);
var abs=dojo.html.getAbsolutePosition(this.domNode,true);
var y=abs.y+dojo.html.getBorderBox(this.domNode).height;
this.dialog.showAt(abs.x,y);
},hideDialog:function(e){
dojo.widget.ToolbarColorDialog.superclass.hideDialog.call(this,e);
this.dialog.hide();
}});
dojo.provide("dojo.widget.AccordionContainer");
dojo.widget.defineWidget("dojo.widget.AccordionContainer",dojo.widget.HtmlWidget,{isContainer:true,labelNodeClass:"label",containerNodeClass:"accBody",duration:250,fillInTemplate:function(){
with(this.domNode.style){
if(position!="absolute"){
position="relative";
}
overflow="hidden";
}
},addChild:function(_112e){
var child=this._addChild(_112e);
this._setSizes();
return child;
},_addChild:function(_1130){
if(_1130.open){
dojo.deprecated("open parameter deprecated, use 'selected=true' instead will be removed in ","0.5");
dojo.debug(_1130.widgetId+": open == "+_1130.open);
_1130.selected=true;
}
if(_1130.widgetType!="AccordionPane"){
var _1131=dojo.widget.createWidget("AccordionPane",{label:_1130.label,selected:_1130.selected,labelNodeClass:this.labelNodeClass,containerNodeClass:this.containerNodeClass,allowCollapse:this.allowCollapse});
_1131.addChild(_1130);
this.addWidgetAsDirectChild(_1131);
this.registerChild(_1131,this.children.length);
return _1131;
}else{
dojo.html.addClass(_1130.containerNode,this.containerNodeClass);
dojo.html.addClass(_1130.labelNode,this.labelNodeClass);
this.addWidgetAsDirectChild(_1130);
this.registerChild(_1130,this.children.length);
return _1130;
}
},postCreate:function(){
var _1132=this.children;
this.children=[];
dojo.html.removeChildren(this.domNode);
dojo.lang.forEach(_1132,dojo.lang.hitch(this,"_addChild"));
this._setSizes();
},removeChild:function(_1133){
dojo.widget.AccordionContainer.superclass.removeChild.call(this,_1133);
this._setSizes();
},onResized:function(){
this._setSizes();
},_setSizes:function(){
var _1134=0;
var _1135=0;
dojo.lang.forEach(this.children,function(child,idx){
_1134+=child.getLabelHeight();
if(child.selected){
_1135=idx;
}
});
var _1138=dojo.html.getContentBox(this.domNode);
var y=0;
dojo.lang.forEach(this.children,function(child,idx){
var _113c=child.getLabelHeight();
child.resizeTo(_1138.width,_1138.height-_1134+_113c);
child.domNode.style.zIndex=idx+1;
child.domNode.style.position="absolute";
child.domNode.style.top=y+"px";
y+=(idx==_1135)?dojo.html.getBorderBox(child.domNode).height:_113c;
});
},selectChild:function(page){
dojo.lang.forEach(this.children,function(child){
child.setSelected(child==page);
});
var y=0;
var anims=[];
dojo.lang.forEach(this.children,function(child,idx){
if(child.domNode.style.top!=(y+"px")){
anims.push(dojo.lfx.html.slideTo(child.domNode,{top:y,left:0},this.duration));
}
y+=child.selected?dojo.html.getBorderBox(child.domNode).height:child.getLabelHeight();
},this);
dojo.lfx.combine(anims).play();
}});
dojo.widget.defineWidget("dojo.widget.AccordionPane",dojo.widget.HtmlWidget,{label:"","class":"dojoAccordionPane",labelNodeClass:"label",containerNodeClass:"accBody",selected:false,templatePath:dojo.uri.dojoUri("src/widget/templates/AccordionPane.html"),templateCssPath:dojo.uri.dojoUri("src/widget/templates/AccordionPane.css"),isContainer:true,fillInTemplate:function(){
dojo.html.addClass(this.domNode,this["class"]);
dojo.widget.AccordionPane.superclass.fillInTemplate.call(this);
dojo.html.disableSelection(this.labelNode);
this.setSelected(this.selected);
},setLabel:function(label){
this.labelNode.innerHTML=label;
},resizeTo:function(width,_1145){
dojo.html.setMarginBox(this.domNode,{width:width,height:_1145});
var _1146=[{domNode:this.labelNode,layoutAlign:"top"},{domNode:this.containerNode,layoutAlign:"client"}];
dojo.widget.html.layout(this.domNode,_1146);
var _1147=dojo.html.getContentBox(this.containerNode);
this.children[0].resizeTo(_1147.width,_1147.height);
},getLabelHeight:function(){
return dojo.html.getMarginBox(this.labelNode).height;
},onLabelClick:function(){
this.parent.selectChild(this);
},setSelected:function(_1148){
this.selected=_1148;
(_1148?dojo.html.addClass:dojo.html.removeClass)(this.domNode,this["class"]+"-selected");
var child=this.children[0];
if(child){
if(_1148){
if(!child.isShowing()){
child.show();
}else{
child.onShow();
}
}else{
child.onHide();
}
}
}});
dojo.lang.extend(dojo.widget.Widget,{open:false});
dojo.provide("dojo.namespaces.dojo");
(function(){
var map={html:{"accordioncontainer":"dojo.widget.AccordionContainer","animatedpng":"dojo.widget.AnimatedPng","button":"dojo.widget.Button","chart":"dojo.widget.Chart","checkbox":"dojo.widget.Checkbox","clock":"dojo.widget.Clock","colorpalette":"dojo.widget.ColorPalette","combobox":"dojo.widget.ComboBox","combobutton":"dojo.widget.Button","contentpane":"dojo.widget.ContentPane","currencytextbox":"dojo.widget.CurrencyTextbox","datepicker":"dojo.widget.DatePicker","datetextbox":"dojo.widget.DateTextbox","debugconsole":"dojo.widget.DebugConsole","dialog":"dojo.widget.Dialog","dropdownbutton":"dojo.widget.Button","dropdowndatepicker":"dojo.widget.DropdownDatePicker","dropdowntimepicker":"dojo.widget.DropdownTimePicker","emaillisttextbox":"dojo.widget.InternetTextbox","emailtextbox":"dojo.widget.InternetTextbox","editor":"dojo.widget.Editor","editor2":"dojo.widget.Editor2","filteringtable":"dojo.widget.FilteringTable","fisheyelist":"dojo.widget.FisheyeList","fisheyelistitem":"dojo.widget.FisheyeList","floatingpane":"dojo.widget.FloatingPane","modalfloatingpane":"dojo.widget.FloatingPane","form":"dojo.widget.Form","googlemap":"dojo.widget.GoogleMap","inlineeditbox":"dojo.widget.InlineEditBox","integerspinner":"dojo.widget.Spinner","integertextbox":"dojo.widget.IntegerTextbox","ipaddresstextbox":"dojo.widget.InternetTextbox","layoutcontainer":"dojo.widget.LayoutContainer","linkpane":"dojo.widget.LinkPane","popupmenu2":"dojo.widget.Menu2","menuitem2":"dojo.widget.Menu2","menuseparator2":"dojo.widget.Menu2","menubar2":"dojo.widget.Menu2","menubaritem2":"dojo.widget.Menu2","pagecontainer":"dojo.widget.PageContainer","pagecontroller":"dojo.widget.PageContainer","popupcontainer":"dojo.widget.PopupContainer","progressbar":"dojo.widget.ProgressBar","radiogroup":"dojo.widget.RadioGroup","realnumbertextbox":"dojo.widget.RealNumberTextbox","regexptextbox":"dojo.widget.RegexpTextbox","repeater":"dojo.widget.Repeater","resizabletextarea":"dojo.widget.ResizableTextarea","richtext":"dojo.widget.RichText","select":"dojo.widget.Select","show":"dojo.widget.Show","showaction":"dojo.widget.ShowAction","showslide":"dojo.widget.ShowSlide","slidervertical":"dojo.widget.Slider","sliderhorizontal":"dojo.widget.Slider","slider":"dojo.widget.Slider","slideshow":"dojo.widget.SlideShow","sortabletable":"dojo.widget.SortableTable","splitcontainer":"dojo.widget.SplitContainer","tabcontainer":"dojo.widget.TabContainer","tabcontroller":"dojo.widget.TabContainer","taskbar":"dojo.widget.TaskBar","textbox":"dojo.widget.Textbox","timepicker":"dojo.widget.TimePicker","timetextbox":"dojo.widget.DateTextbox","titlepane":"dojo.widget.TitlePane","toaster":"dojo.widget.Toaster","toggler":"dojo.widget.Toggler","toolbar":"dojo.widget.Toolbar","toolbarcontainer":"dojo.widget.Toolbar","toolbaritem":"dojo.widget.Toolbar","toolbarbuttongroup":"dojo.widget.Toolbar","toolbarbutton":"dojo.widget.Toolbar","toolbardialog":"dojo.widget.Toolbar","toolbarmenu":"dojo.widget.Toolbar","toolbarseparator":"dojo.widget.Toolbar","toolbarspace":"dojo.widget.Toolbar","toolbarselect":"dojo.widget.Toolbar","toolbarcolordialog":"dojo.widget.Toolbar","tooltip":"dojo.widget.Tooltip","tree":"dojo.widget.Tree","treebasiccontroller":"dojo.widget.TreeBasicController","treecontextmenu":"dojo.widget.TreeContextMenu","treedisablewrapextension":"dojo.widget.TreeDisableWrapExtension","treedociconextension":"dojo.widget.TreeDocIconExtension","treeeditor":"dojo.widget.TreeEditor","treeemphasizeonselect":"dojo.widget.TreeEmphasizeOnSelect","treeexpandtonodeonselect":"dojo.widget.TreeExpandToNodeOnSelect","treelinkextension":"dojo.widget.TreeLinkExtension","treeloadingcontroller":"dojo.widget.TreeLoadingController","treemenuitem":"dojo.widget.TreeContextMenu","treenode":"dojo.widget.TreeNode","treerpccontroller":"dojo.widget.TreeRPCController","treeselector":"dojo.widget.TreeSelector","treetoggleonselect":"dojo.widget.TreeToggleOnSelect","treev3":"dojo.widget.TreeV3","treebasiccontrollerv3":"dojo.widget.TreeBasicControllerV3","treecontextmenuv3":"dojo.widget.TreeContextMenuV3","treedndcontrollerv3":"dojo.widget.TreeDndControllerV3","treeloadingcontrollerv3":"dojo.widget.TreeLoadingControllerV3","treemenuitemv3":"dojo.widget.TreeContextMenuV3","treerpccontrollerv3":"dojo.widget.TreeRpcControllerV3","treeselectorv3":"dojo.widget.TreeSelectorV3","urltextbox":"dojo.widget.InternetTextbox","usphonenumbertextbox":"dojo.widget.UsTextbox","ussocialsecuritynumbertextbox":"dojo.widget.UsTextbox","usstatetextbox":"dojo.widget.UsTextbox","usziptextbox":"dojo.widget.UsTextbox","validationtextbox":"dojo.widget.ValidationTextbox","treeloadingcontroller":"dojo.widget.TreeLoadingController","wizardcontainer":"dojo.widget.Wizard","wizardpane":"dojo.widget.Wizard","yahoomap":"dojo.widget.YahooMap"},svg:{"chart":"dojo.widget.svg.Chart"},vml:{"chart":"dojo.widget.vml.Chart"}};
dojo.addDojoNamespaceMapping=function(_114b,_114c){
map[_114b]=_114c;
};
function dojoNamespaceResolver(name,_114e){
if(!_114e){
_114e="html";
}
if(!map[_114e]){
return null;
}
return map[_114e][name];
}
dojo.registerNamespaceResolver("dojo",dojoNamespaceResolver);
})();
if(!this["dojo"]){
alert("\"dojo/__package__.js\" is now located at \"dojo/dojo.js\". Please update your includes accordingly");
}


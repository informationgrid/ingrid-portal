/*-
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
   /* Start variables_JSON */
         var json_url_mobileMenu = 'SiteGlobals/Modules/MobileNavigation/DE/Allgemein/MobileNavigation.html?view=renderMobileNavigation';
      var json_url_mobileSearch = 'SiteGlobals/Forms/Suche/Servicesuche_Autosuggest_Formular.html?view=renderJSON';
      var json_url_planfeststellung = 'SiteGlobals/Forms/Suche/Servicesuche_Autosuggest_Formular.html?view=renderJSON';
   /* Ende variables_JSON */
   /* Start variables_IMG */
       var image_url_close = '/SiteGlobals/Frontend/Images/icons/close-b.png?__blob=normal';
    var image_url_next = '/SiteGlobals/Frontend/Images/icons/next-upright.png?__blob=normal';
    var image_url_back = '/SiteGlobals/Frontend/Images/icons/back-upright.png?__blob=normal';
    var image_url_back_g = '/SiteGlobals/Frontend/Images/icons/back-g-upright.png?__blob=normal';
    var image_url_next_g = '/SiteGlobals/Frontend/Images/icons/next-g-upright.png?__blob=normal';
    var image_url_close_w = '/SiteGlobals/Frontend/Images/icons/close-w.png?__blob=normal';
    var image_url_loupe_b = '/SiteGlobals/Frontend/Images/icons/loupe-b.png?__blob=normal';
    var image_url_paused = '/SiteGlobals/Frontend/Images/icons/pause.png?__blob=normal';
    var image_url_play = '/SiteGlobals/Frontend/Images/icons/play.png?__blob=normal';
    var image_url_share_facebook_inactive = '/SiteGlobals/Frontend/Images/icons/socialshareprivacy/share_facebook_inactive.png.png?__blob=normal';
    var image_url_share_twitter_inactive = '/SiteGlobals/Frontend/Images/icons/socialshareprivacy/share_twitter_inactive.png?__blob=normal';
    var image_url_share_gplus_inactive = '/SiteGlobals/Frontend/Images/icons/socialshareprivacy/share_gplus_inactive.png?__blob=normal';
    var image_url_pub_next = '/SiteGlobals/Frontend/Images/icons/publication-carousel-right.png?__blob=normal';
    var image_url_pub_back = '/SiteGlobals/Frontend/Images/icons/publication-carousel-left.png?__blob=normal';
    var image_url_pub_back_g = '/SiteGlobals/Frontend/Images/icons/publication-carousel-left-inactive.png?__blob=normal';
    var image_url_pub_next_g = '/SiteGlobals/Frontend/Images/icons/publication-carousel-right-inactive.png?__blob=normal';
    var image_url_gallery_supplement_next = '/SiteGlobals/Frontend/Images/icons/slideshow-gallery-supplement-right.png?__blob=normal';
    var image_url_gallery_supplement_next_g = '/SiteGlobals/Frontend/Images/icons/slideshow-gallery-supplement-right-inactive.png?__blob=normal';
    var image_url_gallery_supplement_back = '/SiteGlobals/Frontend/Images/icons/slideshow-gallery-supplement-left.png?__blob=normal';
    var image_url_gallery_supplement_back_g = '/SiteGlobals/Frontend/Images/icons/slideshow-gallery-supplement-left-inactive.png?__blob=normal';
    var image_url_map_marker = '/SiteGlobals/Frontend/Images/icons/map-marker.png?__blob=normal';
    var image_url_wsv_logo = '/decorations/layout/ingrid/images/template/logo.png?__blob=normal';
    var image_url_shrinkheader_wsv_logo = '/decorations/layout/ingrid/images/template/additionalLogoShrinkheader.jpg?__blob=normal';
   /* Ende variables_IMG */
   /* Start custom.modernizr */
   /*! Modernizr 2.6.2 (Custom Build) | MIT & BSD
 * Build: http://modernizr.com/download/#-inlinesvg-svg-svgclippaths-touch-shiv-mq-cssclasses-teststyles-prefixes-ie8compat-load
 */
window.Modernizr=function(a,b,c){function y(a){j.cssText=a;}function z(a,b){return y(m.join(a+";")+(b||""));}function A(a,b){return typeof a===b;}function B(a,b){return !!~(""+a).indexOf(b);}function C(a,b,d){for(var e in a){var f=b[a[e]];if(f!==c){return d===!1?a[e]:A(f,"function")?f.bind(d||b):f;}}return !1;}var d="2.6.2",e={},f=!0,g=b.documentElement,h="modernizr",i=b.createElement(h),j=i.style,k,l={}.toString,m=" -webkit- -moz- -o- -ms- ".split(" "),n={svg:"http://www.w3.org/2000/svg"},o={},p={},q={},r=[],s=r.slice,t,u=function(a,c,d,e){var f,i,j,k,l=b.createElement("div"),m=b.body,n=m||b.createElement("body");if(parseInt(d,10)){while(d--){j=b.createElement("div"),j.id=e?e[d]:h+(d+1),l.appendChild(j);}}return f=["&#173;",'<style id="s',h,'">',a,"</style>"].join(""),l.id=h,(m?l:n).innerHTML+=f,n.appendChild(l),m||(n.style.background="",n.style.overflow="hidden",k=g.style.overflow,g.style.overflow="hidden",g.appendChild(n)),i=c(l,a),m?l.parentNode.removeChild(l):(n.parentNode.removeChild(n),g.style.overflow=k),!!i;},v=function(b){var c=a.matchMedia||a.msMatchMedia;if(c){return c(b).matches;}var d;return u("@media "+b+" { #"+h+" { position: absolute; } }",function(b){d=(a.getComputedStyle?getComputedStyle(b,null):b.currentStyle)["position"]=="absolute";}),d;},w={}.hasOwnProperty,x;!A(w,"undefined")&&!A(w.call,"undefined")?x=function(a,b){return w.call(a,b);}:x=function(a,b){return b in a&&A(a.constructor.prototype[b],"undefined");},Function.prototype.bind||(Function.prototype.bind=function(b){var c=this;if(typeof c!="function"){throw new TypeError;}var d=s.call(arguments,1),e=function(){if(this instanceof e){var a=function(){};a.prototype=c.prototype;var f=new a,g=c.apply(f,d.concat(s.call(arguments)));return Object(g)===g?g:f;}return c.apply(b,d.concat(s.call(arguments)));};return e;}),o.touch=function(){var c;return"ontouchstart" in a||a.DocumentTouch&&b instanceof DocumentTouch?c=!0:u(["@media (",m.join("touch-enabled),("),h,")","{#modernizr{top:9px;position:absolute}}"].join(""),function(a){c=a.offsetTop===9;}),c;},o.svg=function(){return !!b.createElementNS&&!!b.createElementNS(n.svg,"svg").createSVGRect;},o.inlinesvg=function(){var a=b.createElement("div");return a.innerHTML="<svg/>",(a.firstChild&&a.firstChild.namespaceURI)==n.svg;},o.svgclippaths=function(){return !!b.createElementNS&&/SVGClipPath/.test(l.call(b.createElementNS(n.svg,"clipPath")));};for(var D in o){x(o,D)&&(t=D.toLowerCase(),e[t]=o[D](),r.push((e[t]?"":"no-")+t));}return e.addTest=function(a,b){if(typeof a=="object"){for(var d in a){x(a,d)&&e.addTest(d,a[d]);}}else{a=a.toLowerCase();if(e[a]!==c){return e;}b=typeof b=="function"?b():b,typeof f!="undefined"&&f&&(g.className+=" "+(b?"":"no-")+a),e[a]=b;}return e;},y(""),i=k=null,function(a,b){function k(a,b){var c=a.createElement("p"),d=a.getElementsByTagName("head")[0]||a.documentElement;return c.innerHTML="x<style>"+b+"</style>",d.insertBefore(c.lastChild,d.firstChild);}function l(){var a=r.elements;return typeof a=="string"?a.split(" "):a;}function m(a){var b=i[a[g]];return b||(b={},h++,a[g]=h,i[h]=b),b;}function n(a,c,f){c||(c=b);if(j){return c.createElement(a);}f||(f=m(c));var g;return f.cache[a]?g=f.cache[a].cloneNode():e.test(a)?g=(f.cache[a]=f.createElem(a)).cloneNode():g=f.createElem(a),g.canHaveChildren&&!d.test(a)?f.frag.appendChild(g):g;}function o(a,c){a||(a=b);if(j){return a.createDocumentFragment();}c=c||m(a);var d=c.frag.cloneNode(),e=0,f=l(),g=f.length;for(;e<g;e++){d.createElement(f[e]);}return d;}function p(a,b){b.cache||(b.cache={},b.createElem=a.createElement,b.createFrag=a.createDocumentFragment,b.frag=b.createFrag()),a.createElement=function(c){return r.shivMethods?n(c,a,b):b.createElem(c);},a.createDocumentFragment=Function("h,f","return function(){var n=f.cloneNode(),c=n.createElement;h.shivMethods&&("+l().join().replace(/\w+/g,function(a){return b.createElem(a),b.frag.createElement(a),'c("'+a+'")';})+");return n}")(r,b.frag);}function q(a){a||(a=b);var c=m(a);return r.shivCSS&&!f&&!c.hasCSS&&(c.hasCSS=!!k(a,"article,aside,figcaption,figure,footer,header,hgroup,nav,section{display:block}mark{background:#FF0;color:#000}")),j||p(a,c),a;}var c=a.html5||{},d=/^<|^(?:button|map|select|textarea|object|iframe|option|optgroup)$/i,e=/^(?:a|b|code|div|fieldset|h1|h2|h3|h4|h5|h6|i|label|li|ol|p|q|span|strong|style|table|tbody|td|th|tr|ul)$/i,f,g="_html5shiv",h=0,i={},j;(function(){try{var a=b.createElement("a");a.innerHTML="<xyz></xyz>",f="hidden" in a,j=a.childNodes.length==1||function(){b.createElement("a");var a=b.createDocumentFragment();return typeof a.cloneNode=="undefined"||typeof a.createDocumentFragment=="undefined"||typeof a.createElement=="undefined";}();}catch(c){f=!0,j=!0;}})();var r={elements:c.elements||"abbr article aside audio bdi canvas data datalist details figcaption figure footer header hgroup mark meter nav output progress section summary time video",shivCSS:c.shivCSS!==!1,supportsUnknownElements:j,shivMethods:c.shivMethods!==!1,type:"default",shivDocument:q,createElement:n,createDocumentFragment:o};a.html5=r,q(b);}(this,b),e._version=d,e._prefixes=m,e.mq=v,e.testStyles=u,g.className=g.className.replace(/(^|\s)no-js(\s|$)/,"$1$2")+(f?" js "+r.join(" "):""),e;}(this,this.document),function(a,b,c){function d(a){return"[object Function]"==o.call(a);}function e(a){return"string"==typeof a;}function f(){}function g(a){return !a||"loaded"==a||"complete"==a||"uninitialized"==a;}function h(){var a=p.shift();q=1,a?a.t?m(function(){("c"==a.t?B.injectCss:B.injectJs)(a.s,0,a.a,a.x,a.e,1);},0):(a(),h()):q=0;}function i(a,c,d,e,f,i,j){function k(b){if(!o&&g(l.readyState)&&(u.r=o=1,!q&&h(),l.onload=l.onreadystatechange=null,b)){"img"!=a&&m(function(){t.removeChild(l);},50);for(var d in y[c]){y[c].hasOwnProperty(d)&&y[c][d].onload();}}}var j=j||B.errorTimeout,l=b.createElement(a),o=0,r=0,u={t:d,s:c,e:f,a:i,x:j};1===y[c]&&(r=1,y[c]=[]),"object"==a?l.data=c:(l.src=c,l.type=a),l.width=l.height="0",l.onerror=l.onload=l.onreadystatechange=function(){k.call(this,r);},p.splice(e,0,u),"img"!=a&&(r||2===y[c]?(t.insertBefore(l,s?null:n),m(k,j)):y[c].push(l));}function j(a,b,c,d,f){return q=0,b=b||"j",e(a)?i("c"==b?v:u,a,b,this.i++,c,d,f):(p.splice(this.i++,0,a),1==p.length&&h()),this;}function k(){var a=B;return a.loader={load:j,i:0},a;}var l=b.documentElement,m=a.setTimeout,n=b.getElementsByTagName("script")[0],o={}.toString,p=[],q=0,r="MozAppearance" in l.style,s=r&&!!b.createRange().compareNode,t=s?l:n.parentNode,l=a.opera&&"[object Opera]"==o.call(a.opera),l=!!b.attachEvent&&!l,u=r?"object":l?"script":"img",v=l?"script":u,w=Array.isArray||function(a){return"[object Array]"==o.call(a);},x=[],y={},z={timeout:function(a,b){return b.length&&(a.timeout=b[0]),a;}},A,B;B=function(a){function b(a){var a=a.split("!"),b=x.length,c=a.pop(),d=a.length,c={url:c,origUrl:c,prefixes:a},e,f,g;for(f=0;f<d;f++){g=a[f].split("="),(e=z[g.shift()])&&(c=e(c,g));}for(f=0;f<b;f++){c=x[f](c);}return c;}function g(a,e,f,g,h){var i=b(a),j=i.autoCallback;i.url.split(".").pop().split("?").shift(),i.bypass||(e&&(e=d(e)?e:e[a]||e[g]||e[a.split("/").pop().split("?")[0]]),i.instead?i.instead(a,e,f,g,h):(y[i.url]?i.noexec=!0:y[i.url]=1,f.load(i.url,i.forceCSS||!i.forceJS&&"css"==i.url.split(".").pop().split("?").shift()?"c":c,i.noexec,i.attrs,i.timeout),(d(e)||d(j))&&f.load(function(){k(),e&&e(i.origUrl,h,g),j&&j(i.origUrl,h,g),y[i.url]=2;})));}function h(a,b){function c(a,c){if(a){if(e(a)){c||(j=function(){var a=[].slice.call(arguments);k.apply(this,a),l();}),g(a,j,b,0,h);}else{if(Object(a)===a){for(n in m=function(){var b=0,c;for(c in a){a.hasOwnProperty(c)&&b++;}return b;}(),a){a.hasOwnProperty(n)&&(!c&&!--m&&(d(j)?j=function(){var a=[].slice.call(arguments);k.apply(this,a),l();}:j[n]=function(a){return function(){var b=[].slice.call(arguments);a&&a.apply(this,b),l();};}(k[n])),g(a[n],j,b,n,h));}}}}else{!c&&l();}}var h=!!a.test,i=a.load||a.both,j=a.callback||f,k=j,l=a.complete||f,m,n;c(h?a.yep:a.nope,!!i),i&&c(i);}var i,j,l=this.yepnope.loader;if(e(a)){g(a,0,l,0);}else{if(w(a)){for(i=0;i<a.length;i++){j=a[i],e(j)?g(j,0,l,0):w(j)?B(j):Object(j)===j&&h(j,l);}}else{Object(a)===a&&h(a,l);}}},B.addPrefix=function(a,b){z[a]=b;},B.addFilter=function(a){x.push(a);},B.errorTimeout=10000,null==b.readyState&&b.addEventListener&&(b.readyState="loading",b.addEventListener("DOMContentLoaded",A=function(){b.removeEventListener("DOMContentLoaded",A,0),b.readyState="complete";},0)),a.yepnope=k(),a.yepnope.executeStack=h,a.yepnope.injectJs=function(a,c,d,e,i,j){var k=b.createElement("script"),l,o,e=e||B.errorTimeout;k.src=a;for(o in d){k.setAttribute(o,d[o]);}c=j?h:c||f,k.onreadystatechange=k.onload=function(){!l&&g(k.readyState)&&(l=1,c(),k.onload=k.onreadystatechange=null);},m(function(){l||(l=1,c(1));},e),i?k.onload():n.parentNode.insertBefore(k,n);},a.yepnope.injectCss=function(a,c,d,e,g,i){var e=b.createElement("link"),j,c=i?h:c||f;e.href=a,e.rel="stylesheet",e.type="text/css";for(j in d){e.setAttribute(j,d[j]);}g||(n.parentNode.insertBefore(e,n),m(c,0));};}(this,document),Modernizr.load=function(){yepnope.apply(window,[].slice.call(arguments,0));},Modernizr.addTest("ie8compat",function(){return !window.addEventListener&&document.documentMode&&document.documentMode===7;});
   /* Ende custom.modernizr */
   /* Start jquery */
   /*!
 * jQuery JavaScript Library v1.11.1
 * http://jquery.com/
 *
 * Includes Sizzle.js
 * http://sizzlejs.com/
 *
 * Copyright 2005, 2014 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2014-05-01T17:42Z
 */
(function(global,factory){if(typeof module==="object"&&typeof module.exports==="object"){module.exports=global.document?factory(global,true):function(w){if(!w.document){throw new Error("jQuery requires a window with a document");}return factory(w);};}else{factory(global);}}(typeof window!=="undefined"?window:this,function(window,noGlobal){var deletedIds=[];var slice=deletedIds.slice;var concat=deletedIds.concat;var push=deletedIds.push;var indexOf=deletedIds.indexOf;var class2type={};var toString=class2type.toString;var hasOwn=class2type.hasOwnProperty;var support={};var version="1.11.1",jQuery=function(selector,context){return new jQuery.fn.init(selector,context);},rtrim=/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,rmsPrefix=/^-ms-/,rdashAlpha=/-([\da-z])/gi,fcamelCase=function(all,letter){return letter.toUpperCase();};jQuery.fn=jQuery.prototype={jquery:version,constructor:jQuery,selector:"",length:0,toArray:function(){return slice.call(this);},get:function(num){return num!=null?(num<0?this[num+this.length]:this[num]):slice.call(this);},pushStack:function(elems){var ret=jQuery.merge(this.constructor(),elems);ret.prevObject=this;ret.context=this.context;return ret;},each:function(callback,args){return jQuery.each(this,callback,args);},map:function(callback){return this.pushStack(jQuery.map(this,function(elem,i){return callback.call(elem,i,elem);}));},slice:function(){return this.pushStack(slice.apply(this,arguments));},first:function(){return this.eq(0);},last:function(){return this.eq(-1);},eq:function(i){var len=this.length,j=+i+(i<0?len:0);return this.pushStack(j>=0&&j<len?[this[j]]:[]);},end:function(){return this.prevObject||this.constructor(null);},push:push,sort:deletedIds.sort,splice:deletedIds.splice};jQuery.extend=jQuery.fn.extend=function(){var src,copyIsArray,copy,name,options,clone,target=arguments[0]||{},i=1,length=arguments.length,deep=false;if(typeof target==="boolean"){deep=target;target=arguments[i]||{};i++;}if(typeof target!=="object"&&!jQuery.isFunction(target)){target={};}if(i===length){target=this;i--;}for(;i<length;i++){if((options=arguments[i])!=null){for(name in options){src=target[name];copy=options[name];if(target===copy){continue;}if(deep&&copy&&(jQuery.isPlainObject(copy)||(copyIsArray=jQuery.isArray(copy)))){if(copyIsArray){copyIsArray=false;clone=src&&jQuery.isArray(src)?src:[];}else{clone=src&&jQuery.isPlainObject(src)?src:{};}target[name]=jQuery.extend(deep,clone,copy);}else{if(copy!==undefined){target[name]=copy;}}}}}return target;};jQuery.extend({expando:"jQuery"+(version+Math.random()).replace(/\D/g,""),isReady:true,error:function(msg){throw new Error(msg);},noop:function(){},isFunction:function(obj){return jQuery.type(obj)==="function";},isArray:Array.isArray||function(obj){return jQuery.type(obj)==="array";},isWindow:function(obj){return obj!=null&&obj==obj.window;},isNumeric:function(obj){return !jQuery.isArray(obj)&&obj-parseFloat(obj)>=0;},isEmptyObject:function(obj){var name;for(name in obj){return false;}return true;},isPlainObject:function(obj){var key;if(!obj||jQuery.type(obj)!=="object"||obj.nodeType||jQuery.isWindow(obj)){return false;}try{if(obj.constructor&&!hasOwn.call(obj,"constructor")&&!hasOwn.call(obj.constructor.prototype,"isPrototypeOf")){return false;}}catch(e){return false;}if(support.ownLast){for(key in obj){return hasOwn.call(obj,key);}}for(key in obj){}return key===undefined||hasOwn.call(obj,key);},type:function(obj){if(obj==null){return obj+"";}return typeof obj==="object"||typeof obj==="function"?class2type[toString.call(obj)]||"object":typeof obj;},globalEval:function(data){if(data&&jQuery.trim(data)){(window.execScript||function(data){window["eval"].call(window,data);})(data);}},camelCase:function(string){return string.replace(rmsPrefix,"ms-").replace(rdashAlpha,fcamelCase);},nodeName:function(elem,name){return elem.nodeName&&elem.nodeName.toLowerCase()===name.toLowerCase();},each:function(obj,callback,args){var value,i=0,length=obj.length,isArray=isArraylike(obj);if(args){if(isArray){for(;i<length;i++){value=callback.apply(obj[i],args);if(value===false){break;}}}else{for(i in obj){value=callback.apply(obj[i],args);if(value===false){break;}}}}else{if(isArray){for(;i<length;i++){value=callback.call(obj[i],i,obj[i]);if(value===false){break;}}}else{for(i in obj){value=callback.call(obj[i],i,obj[i]);if(value===false){break;}}}}return obj;},trim:function(text){return text==null?"":(text+"").replace(rtrim,"");},makeArray:function(arr,results){var ret=results||[];if(arr!=null){if(isArraylike(Object(arr))){jQuery.merge(ret,typeof arr==="string"?[arr]:arr);}else{push.call(ret,arr);}}return ret;},inArray:function(elem,arr,i){var len;if(arr){if(indexOf){return indexOf.call(arr,elem,i);}len=arr.length;i=i?i<0?Math.max(0,len+i):i:0;for(;i<len;i++){if(i in arr&&arr[i]===elem){return i;}}}return -1;},merge:function(first,second){var len=+second.length,j=0,i=first.length;while(j<len){first[i++]=second[j++];}if(len!==len){while(second[j]!==undefined){first[i++]=second[j++];}}first.length=i;return first;},grep:function(elems,callback,invert){var callbackInverse,matches=[],i=0,length=elems.length,callbackExpect=!invert;for(;i<length;i++){callbackInverse=!callback(elems[i],i);if(callbackInverse!==callbackExpect){matches.push(elems[i]);}}return matches;},map:function(elems,callback,arg){var value,i=0,length=elems.length,isArray=isArraylike(elems),ret=[];if(isArray){for(;i<length;i++){value=callback(elems[i],i,arg);if(value!=null){ret.push(value);}}}else{for(i in elems){value=callback(elems[i],i,arg);if(value!=null){ret.push(value);}}}return concat.apply([],ret);},guid:1,proxy:function(fn,context){var args,proxy,tmp;if(typeof context==="string"){tmp=fn[context];context=fn;fn=tmp;}if(!jQuery.isFunction(fn)){return undefined;}args=slice.call(arguments,2);proxy=function(){return fn.apply(context||this,args.concat(slice.call(arguments)));};proxy.guid=fn.guid=fn.guid||jQuery.guid++;return proxy;},now:function(){return +(new Date());},support:support});jQuery.each("Boolean Number String Function Array Date RegExp Object Error".split(" "),function(i,name){class2type["[object "+name+"]"]=name.toLowerCase();});function isArraylike(obj){var length=obj.length,type=jQuery.type(obj);if(type==="function"||jQuery.isWindow(obj)){return false;}if(obj.nodeType===1&&length){return true;}return type==="array"||length===0||typeof length==="number"&&length>0&&(length-1) in obj;}var Sizzle=
/*!
 * Sizzle CSS Selector Engine v1.10.19
 * http://sizzlejs.com/
 *
 * Copyright 2013 jQuery Foundation, Inc. and other contributors
 * Released under the MIT license
 * http://jquery.org/license
 *
 * Date: 2014-04-18
 */
(function(window){var i,support,Expr,getText,isXML,tokenize,compile,select,outermostContext,sortInput,hasDuplicate,setDocument,document,docElem,documentIsHTML,rbuggyQSA,rbuggyMatches,matches,contains,expando="sizzle"+-(new Date()),preferredDoc=window.document,dirruns=0,done=0,classCache=createCache(),tokenCache=createCache(),compilerCache=createCache(),sortOrder=function(a,b){if(a===b){hasDuplicate=true;}return 0;},strundefined=typeof undefined,MAX_NEGATIVE=1<<31,hasOwn=({}).hasOwnProperty,arr=[],pop=arr.pop,push_native=arr.push,push=arr.push,slice=arr.slice,indexOf=arr.indexOf||function(elem){var i=0,len=this.length;for(;i<len;i++){if(this[i]===elem){return i;}}return -1;},booleans="checked|selected|async|autofocus|autoplay|controls|defer|disabled|hidden|ismap|loop|multiple|open|readonly|required|scoped",whitespace="[\\x20\\t\\r\\n\\f]",characterEncoding="(?:\\\\.|[\\w-]|[^\\x00-\\xa0])+",identifier=characterEncoding.replace("w","w#"),attributes="\\["+whitespace+"*("+characterEncoding+")(?:"+whitespace+"*([*^$|!~]?=)"+whitespace+"*(?:'((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\"|("+identifier+"))|)"+whitespace+"*\\]",pseudos=":("+characterEncoding+")(?:\\(("+"('((?:\\\\.|[^\\\\'])*)'|\"((?:\\\\.|[^\\\\\"])*)\")|"+"((?:\\\\.|[^\\\\()[\\]]|"+attributes+")*)|"+".*"+")\\)|)",rtrim=new RegExp("^"+whitespace+"+|((?:^|[^\\\\])(?:\\\\.)*)"+whitespace+"+$","g"),rcomma=new RegExp("^"+whitespace+"*,"+whitespace+"*"),rcombinators=new RegExp("^"+whitespace+"*([>+~]|"+whitespace+")"+whitespace+"*"),rattributeQuotes=new RegExp("="+whitespace+"*([^\\]'\"]*?)"+whitespace+"*\\]","g"),rpseudo=new RegExp(pseudos),ridentifier=new RegExp("^"+identifier+"$"),matchExpr={"ID":new RegExp("^#("+characterEncoding+")"),"CLASS":new RegExp("^\\.("+characterEncoding+")"),"TAG":new RegExp("^("+characterEncoding.replace("w","w*")+")"),"ATTR":new RegExp("^"+attributes),"PSEUDO":new RegExp("^"+pseudos),"CHILD":new RegExp("^:(only|first|last|nth|nth-last)-(child|of-type)(?:\\("+whitespace+"*(even|odd|(([+-]|)(\\d*)n|)"+whitespace+"*(?:([+-]|)"+whitespace+"*(\\d+)|))"+whitespace+"*\\)|)","i"),"bool":new RegExp("^(?:"+booleans+")$","i"),"needsContext":new RegExp("^"+whitespace+"*[>+~]|:(even|odd|eq|gt|lt|nth|first|last)(?:\\("+whitespace+"*((?:-\\d)?\\d*)"+whitespace+"*\\)|)(?=[^-]|$)","i")},rinputs=/^(?:input|select|textarea|button)$/i,rheader=/^h\d$/i,rnative=/^[^{]+\{\s*\[native \w/,rquickExpr=/^(?:#([\w-]+)|(\w+)|\.([\w-]+))$/,rsibling=/[+~]/,rescape=/'|\\/g,runescape=new RegExp("\\\\([\\da-f]{1,6}"+whitespace+"?|("+whitespace+")|.)","ig"),funescape=function(_,escaped,escapedWhitespace){var high="0x"+escaped-65536;return high!==high||escapedWhitespace?escaped:high<0?String.fromCharCode(high+65536):String.fromCharCode(high>>10|55296,high&1023|56320);};try{push.apply((arr=slice.call(preferredDoc.childNodes)),preferredDoc.childNodes);arr[preferredDoc.childNodes.length].nodeType;}catch(e){push={apply:arr.length?function(target,els){push_native.apply(target,slice.call(els));}:function(target,els){var j=target.length,i=0;while((target[j++]=els[i++])){}target.length=j-1;}};}function Sizzle(selector,context,results,seed){var match,elem,m,nodeType,i,groups,old,nid,newContext,newSelector;if((context?context.ownerDocument||context:preferredDoc)!==document){setDocument(context);}context=context||document;results=results||[];if(!selector||typeof selector!=="string"){return results;}if((nodeType=context.nodeType)!==1&&nodeType!==9){return[];}if(documentIsHTML&&!seed){if((match=rquickExpr.exec(selector))){if((m=match[1])){if(nodeType===9){elem=context.getElementById(m);if(elem&&elem.parentNode){if(elem.id===m){results.push(elem);return results;}}else{return results;}}else{if(context.ownerDocument&&(elem=context.ownerDocument.getElementById(m))&&contains(context,elem)&&elem.id===m){results.push(elem);return results;}}}else{if(match[2]){push.apply(results,context.getElementsByTagName(selector));return results;}else{if((m=match[3])&&support.getElementsByClassName&&context.getElementsByClassName){push.apply(results,context.getElementsByClassName(m));return results;}}}}if(support.qsa&&(!rbuggyQSA||!rbuggyQSA.test(selector))){nid=old=expando;newContext=context;newSelector=nodeType===9&&selector;if(nodeType===1&&context.nodeName.toLowerCase()!=="object"){groups=tokenize(selector);if((old=context.getAttribute("id"))){nid=old.replace(rescape,"\\$&");}else{context.setAttribute("id",nid);}nid="[id='"+nid+"'] ";i=groups.length;while(i--){groups[i]=nid+toSelector(groups[i]);}newContext=rsibling.test(selector)&&testContext(context.parentNode)||context;newSelector=groups.join(",");}if(newSelector){try{push.apply(results,newContext.querySelectorAll(newSelector));return results;}catch(qsaError){}finally{if(!old){context.removeAttribute("id");}}}}}return select(selector.replace(rtrim,"$1"),context,results,seed);}function createCache(){var keys=[];function cache(key,value){if(keys.push(key+" ")>Expr.cacheLength){delete cache[keys.shift()];}return(cache[key+" "]=value);}return cache;}function markFunction(fn){fn[expando]=true;return fn;}function assert(fn){var div=document.createElement("div");try{return !!fn(div);}catch(e){return false;}finally{if(div.parentNode){div.parentNode.removeChild(div);}div=null;}}function addHandle(attrs,handler){var arr=attrs.split("|"),i=attrs.length;while(i--){Expr.attrHandle[arr[i]]=handler;}}function siblingCheck(a,b){var cur=b&&a,diff=cur&&a.nodeType===1&&b.nodeType===1&&(~b.sourceIndex||MAX_NEGATIVE)-(~a.sourceIndex||MAX_NEGATIVE);if(diff){return diff;}if(cur){while((cur=cur.nextSibling)){if(cur===b){return -1;}}}return a?1:-1;}function createInputPseudo(type){return function(elem){var name=elem.nodeName.toLowerCase();return name==="input"&&elem.type===type;};}function createButtonPseudo(type){return function(elem){var name=elem.nodeName.toLowerCase();return(name==="input"||name==="button")&&elem.type===type;};}function createPositionalPseudo(fn){return markFunction(function(argument){argument=+argument;return markFunction(function(seed,matches){var j,matchIndexes=fn([],seed.length,argument),i=matchIndexes.length;while(i--){if(seed[(j=matchIndexes[i])]){seed[j]=!(matches[j]=seed[j]);}}});});}function testContext(context){return context&&typeof context.getElementsByTagName!==strundefined&&context;}support=Sizzle.support={};isXML=Sizzle.isXML=function(elem){var documentElement=elem&&(elem.ownerDocument||elem).documentElement;return documentElement?documentElement.nodeName!=="HTML":false;};setDocument=Sizzle.setDocument=function(node){var hasCompare,doc=node?node.ownerDocument||node:preferredDoc,parent=doc.defaultView;if(doc===document||doc.nodeType!==9||!doc.documentElement){return document;}document=doc;docElem=doc.documentElement;documentIsHTML=!isXML(doc);if(parent&&parent!==parent.top){if(parent.addEventListener){parent.addEventListener("unload",function(){setDocument();},false);}else{if(parent.attachEvent){parent.attachEvent("onunload",function(){setDocument();});}}}support.attributes=assert(function(div){div.className="i";return !div.getAttribute("className");});support.getElementsByTagName=assert(function(div){div.appendChild(doc.createComment(""));return !div.getElementsByTagName("*").length;});support.getElementsByClassName=rnative.test(doc.getElementsByClassName)&&assert(function(div){div.innerHTML="<div class='a'></div><div class='a i'></div>";div.firstChild.className="i";return div.getElementsByClassName("i").length===2;});support.getById=assert(function(div){docElem.appendChild(div).id=expando;return !doc.getElementsByName||!doc.getElementsByName(expando).length;});if(support.getById){Expr.find["ID"]=function(id,context){if(typeof context.getElementById!==strundefined&&documentIsHTML){var m=context.getElementById(id);return m&&m.parentNode?[m]:[];}};Expr.filter["ID"]=function(id){var attrId=id.replace(runescape,funescape);return function(elem){return elem.getAttribute("id")===attrId;};};}else{delete Expr.find["ID"];Expr.filter["ID"]=function(id){var attrId=id.replace(runescape,funescape);return function(elem){var node=typeof elem.getAttributeNode!==strundefined&&elem.getAttributeNode("id");return node&&node.value===attrId;};};}Expr.find["TAG"]=support.getElementsByTagName?function(tag,context){if(typeof context.getElementsByTagName!==strundefined){return context.getElementsByTagName(tag);}}:function(tag,context){var elem,tmp=[],i=0,results=context.getElementsByTagName(tag);if(tag==="*"){while((elem=results[i++])){if(elem.nodeType===1){tmp.push(elem);}}return tmp;}return results;};Expr.find["CLASS"]=support.getElementsByClassName&&function(className,context){if(typeof context.getElementsByClassName!==strundefined&&documentIsHTML){return context.getElementsByClassName(className);}};rbuggyMatches=[];rbuggyQSA=[];if((support.qsa=rnative.test(doc.querySelectorAll))){assert(function(div){div.innerHTML="<select msallowclip=''><option selected=''></option></select>";if(div.querySelectorAll("[msallowclip^='']").length){rbuggyQSA.push("[*^$]="+whitespace+"*(?:''|\"\")");}if(!div.querySelectorAll("[selected]").length){rbuggyQSA.push("\\["+whitespace+"*(?:value|"+booleans+")");}if(!div.querySelectorAll(":checked").length){rbuggyQSA.push(":checked");}});assert(function(div){var input=doc.createElement("input");input.setAttribute("type","hidden");div.appendChild(input).setAttribute("name","D");if(div.querySelectorAll("[name=d]").length){rbuggyQSA.push("name"+whitespace+"*[*^$|!~]?=");}if(!div.querySelectorAll(":enabled").length){rbuggyQSA.push(":enabled",":disabled");}div.querySelectorAll("*,:x");rbuggyQSA.push(",.*:");});}if((support.matchesSelector=rnative.test((matches=docElem.matches||docElem.webkitMatchesSelector||docElem.mozMatchesSelector||docElem.oMatchesSelector||docElem.msMatchesSelector)))){assert(function(div){support.disconnectedMatch=matches.call(div,"div");matches.call(div,"[s!='']:x");rbuggyMatches.push("!=",pseudos);});}rbuggyQSA=rbuggyQSA.length&&new RegExp(rbuggyQSA.join("|"));rbuggyMatches=rbuggyMatches.length&&new RegExp(rbuggyMatches.join("|"));hasCompare=rnative.test(docElem.compareDocumentPosition);contains=hasCompare||rnative.test(docElem.contains)?function(a,b){var adown=a.nodeType===9?a.documentElement:a,bup=b&&b.parentNode;return a===bup||!!(bup&&bup.nodeType===1&&(adown.contains?adown.contains(bup):a.compareDocumentPosition&&a.compareDocumentPosition(bup)&16));}:function(a,b){if(b){while((b=b.parentNode)){if(b===a){return true;}}}return false;};sortOrder=hasCompare?function(a,b){if(a===b){hasDuplicate=true;return 0;}var compare=!a.compareDocumentPosition-!b.compareDocumentPosition;if(compare){return compare;}compare=(a.ownerDocument||a)===(b.ownerDocument||b)?a.compareDocumentPosition(b):1;if(compare&1||(!support.sortDetached&&b.compareDocumentPosition(a)===compare)){if(a===doc||a.ownerDocument===preferredDoc&&contains(preferredDoc,a)){return -1;}if(b===doc||b.ownerDocument===preferredDoc&&contains(preferredDoc,b)){return 1;}return sortInput?(indexOf.call(sortInput,a)-indexOf.call(sortInput,b)):0;}return compare&4?-1:1;}:function(a,b){if(a===b){hasDuplicate=true;return 0;}var cur,i=0,aup=a.parentNode,bup=b.parentNode,ap=[a],bp=[b];if(!aup||!bup){return a===doc?-1:b===doc?1:aup?-1:bup?1:sortInput?(indexOf.call(sortInput,a)-indexOf.call(sortInput,b)):0;}else{if(aup===bup){return siblingCheck(a,b);}}cur=a;while((cur=cur.parentNode)){ap.unshift(cur);}cur=b;while((cur=cur.parentNode)){bp.unshift(cur);}while(ap[i]===bp[i]){i++;}return i?siblingCheck(ap[i],bp[i]):ap[i]===preferredDoc?-1:bp[i]===preferredDoc?1:0;};return doc;};Sizzle.matches=function(expr,elements){return Sizzle(expr,null,null,elements);};Sizzle.matchesSelector=function(elem,expr){if((elem.ownerDocument||elem)!==document){setDocument(elem);}expr=expr.replace(rattributeQuotes,"='$1']");if(support.matchesSelector&&documentIsHTML&&(!rbuggyMatches||!rbuggyMatches.test(expr))&&(!rbuggyQSA||!rbuggyQSA.test(expr))){try{var ret=matches.call(elem,expr);if(ret||support.disconnectedMatch||elem.document&&elem.document.nodeType!==11){return ret;}}catch(e){}}return Sizzle(expr,document,null,[elem]).length>0;};Sizzle.contains=function(context,elem){if((context.ownerDocument||context)!==document){setDocument(context);}return contains(context,elem);};Sizzle.attr=function(elem,name){if((elem.ownerDocument||elem)!==document){setDocument(elem);}var fn=Expr.attrHandle[name.toLowerCase()],val=fn&&hasOwn.call(Expr.attrHandle,name.toLowerCase())?fn(elem,name,!documentIsHTML):undefined;return val!==undefined?val:support.attributes||!documentIsHTML?elem.getAttribute(name):(val=elem.getAttributeNode(name))&&val.specified?val.value:null;};Sizzle.error=function(msg){throw new Error("Syntax error, unrecognized expression: "+msg);};Sizzle.uniqueSort=function(results){var elem,duplicates=[],j=0,i=0;hasDuplicate=!support.detectDuplicates;sortInput=!support.sortStable&&results.slice(0);results.sort(sortOrder);if(hasDuplicate){while((elem=results[i++])){if(elem===results[i]){j=duplicates.push(i);}}while(j--){results.splice(duplicates[j],1);}}sortInput=null;return results;};getText=Sizzle.getText=function(elem){var node,ret="",i=0,nodeType=elem.nodeType;if(!nodeType){while((node=elem[i++])){ret+=getText(node);}}else{if(nodeType===1||nodeType===9||nodeType===11){if(typeof elem.textContent==="string"){return elem.textContent;}else{for(elem=elem.firstChild;elem;elem=elem.nextSibling){ret+=getText(elem);}}}else{if(nodeType===3||nodeType===4){return elem.nodeValue;}}}return ret;};Expr=Sizzle.selectors={cacheLength:50,createPseudo:markFunction,match:matchExpr,attrHandle:{},find:{},relative:{">":{dir:"parentNode",first:true}," ":{dir:"parentNode"},"+":{dir:"previousSibling",first:true},"~":{dir:"previousSibling"}},preFilter:{"ATTR":function(match){match[1]=match[1].replace(runescape,funescape);match[3]=(match[3]||match[4]||match[5]||"").replace(runescape,funescape);if(match[2]==="~="){match[3]=" "+match[3]+" ";}return match.slice(0,4);},"CHILD":function(match){match[1]=match[1].toLowerCase();if(match[1].slice(0,3)==="nth"){if(!match[3]){Sizzle.error(match[0]);}match[4]=+(match[4]?match[5]+(match[6]||1):2*(match[3]==="even"||match[3]==="odd"));match[5]=+((match[7]+match[8])||match[3]==="odd");}else{if(match[3]){Sizzle.error(match[0]);}}return match;},"PSEUDO":function(match){var excess,unquoted=!match[6]&&match[2];if(matchExpr["CHILD"].test(match[0])){return null;}if(match[3]){match[2]=match[4]||match[5]||"";}else{if(unquoted&&rpseudo.test(unquoted)&&(excess=tokenize(unquoted,true))&&(excess=unquoted.indexOf(")",unquoted.length-excess)-unquoted.length)){match[0]=match[0].slice(0,excess);match[2]=unquoted.slice(0,excess);}}return match.slice(0,3);}},filter:{"TAG":function(nodeNameSelector){var nodeName=nodeNameSelector.replace(runescape,funescape).toLowerCase();return nodeNameSelector==="*"?function(){return true;}:function(elem){return elem.nodeName&&elem.nodeName.toLowerCase()===nodeName;};},"CLASS":function(className){var pattern=classCache[className+" "];return pattern||(pattern=new RegExp("(^|"+whitespace+")"+className+"("+whitespace+"|$)"))&&classCache(className,function(elem){return pattern.test(typeof elem.className==="string"&&elem.className||typeof elem.getAttribute!==strundefined&&elem.getAttribute("class")||"");});},"ATTR":function(name,operator,check){return function(elem){var result=Sizzle.attr(elem,name);if(result==null){return operator==="!=";}if(!operator){return true;}result+="";return operator==="="?result===check:operator==="!="?result!==check:operator==="^="?check&&result.indexOf(check)===0:operator==="*="?check&&result.indexOf(check)>-1:operator==="$="?check&&result.slice(-check.length)===check:operator==="~="?(" "+result+" ").indexOf(check)>-1:operator==="|="?result===check||result.slice(0,check.length+1)===check+"-":false;};},"CHILD":function(type,what,argument,first,last){var simple=type.slice(0,3)!=="nth",forward=type.slice(-4)!=="last",ofType=what==="of-type";return first===1&&last===0?function(elem){return !!elem.parentNode;}:function(elem,context,xml){var cache,outerCache,node,diff,nodeIndex,start,dir=simple!==forward?"nextSibling":"previousSibling",parent=elem.parentNode,name=ofType&&elem.nodeName.toLowerCase(),useCache=!xml&&!ofType;if(parent){if(simple){while(dir){node=elem;while((node=node[dir])){if(ofType?node.nodeName.toLowerCase()===name:node.nodeType===1){return false;}}start=dir=type==="only"&&!start&&"nextSibling";}return true;}start=[forward?parent.firstChild:parent.lastChild];if(forward&&useCache){outerCache=parent[expando]||(parent[expando]={});cache=outerCache[type]||[];nodeIndex=cache[0]===dirruns&&cache[1];diff=cache[0]===dirruns&&cache[2];node=nodeIndex&&parent.childNodes[nodeIndex];while((node=++nodeIndex&&node&&node[dir]||(diff=nodeIndex=0)||start.pop())){if(node.nodeType===1&&++diff&&node===elem){outerCache[type]=[dirruns,nodeIndex,diff];break;}}}else{if(useCache&&(cache=(elem[expando]||(elem[expando]={}))[type])&&cache[0]===dirruns){diff=cache[1];}else{while((node=++nodeIndex&&node&&node[dir]||(diff=nodeIndex=0)||start.pop())){if((ofType?node.nodeName.toLowerCase()===name:node.nodeType===1)&&++diff){if(useCache){(node[expando]||(node[expando]={}))[type]=[dirruns,diff];}if(node===elem){break;}}}}}diff-=last;return diff===first||(diff%first===0&&diff/first>=0);}};},"PSEUDO":function(pseudo,argument){var args,fn=Expr.pseudos[pseudo]||Expr.setFilters[pseudo.toLowerCase()]||Sizzle.error("unsupported pseudo: "+pseudo);if(fn[expando]){return fn(argument);}if(fn.length>1){args=[pseudo,pseudo,"",argument];return Expr.setFilters.hasOwnProperty(pseudo.toLowerCase())?markFunction(function(seed,matches){var idx,matched=fn(seed,argument),i=matched.length;while(i--){idx=indexOf.call(seed,matched[i]);seed[idx]=!(matches[idx]=matched[i]);}}):function(elem){return fn(elem,0,args);};}return fn;}},pseudos:{"not":markFunction(function(selector){var input=[],results=[],matcher=compile(selector.replace(rtrim,"$1"));return matcher[expando]?markFunction(function(seed,matches,context,xml){var elem,unmatched=matcher(seed,null,xml,[]),i=seed.length;while(i--){if((elem=unmatched[i])){seed[i]=!(matches[i]=elem);}}}):function(elem,context,xml){input[0]=elem;matcher(input,null,xml,results);return !results.pop();};}),"has":markFunction(function(selector){return function(elem){return Sizzle(selector,elem).length>0;};}),"contains":markFunction(function(text){return function(elem){return(elem.textContent||elem.innerText||getText(elem)).indexOf(text)>-1;};}),"lang":markFunction(function(lang){if(!ridentifier.test(lang||"")){Sizzle.error("unsupported lang: "+lang);}lang=lang.replace(runescape,funescape).toLowerCase();return function(elem){var elemLang;do{if((elemLang=documentIsHTML?elem.lang:elem.getAttribute("xml:lang")||elem.getAttribute("lang"))){elemLang=elemLang.toLowerCase();return elemLang===lang||elemLang.indexOf(lang+"-")===0;}}while((elem=elem.parentNode)&&elem.nodeType===1);return false;};}),"target":function(elem){var hash=window.location&&window.location.hash;return hash&&hash.slice(1)===elem.id;},"root":function(elem){return elem===docElem;},"focus":function(elem){return elem===document.activeElement&&(!document.hasFocus||document.hasFocus())&&!!(elem.type||elem.href||~elem.tabIndex);},"enabled":function(elem){return elem.disabled===false;},"disabled":function(elem){return elem.disabled===true;},"checked":function(elem){var nodeName=elem.nodeName.toLowerCase();return(nodeName==="input"&&!!elem.checked)||(nodeName==="option"&&!!elem.selected);},"selected":function(elem){if(elem.parentNode){elem.parentNode.selectedIndex;}return elem.selected===true;},"empty":function(elem){for(elem=elem.firstChild;elem;elem=elem.nextSibling){if(elem.nodeType<6){return false;}}return true;},"parent":function(elem){return !Expr.pseudos["empty"](elem);},"header":function(elem){return rheader.test(elem.nodeName);},"input":function(elem){return rinputs.test(elem.nodeName);},"button":function(elem){var name=elem.nodeName.toLowerCase();return name==="input"&&elem.type==="button"||name==="button";},"text":function(elem){var attr;return elem.nodeName.toLowerCase()==="input"&&elem.type==="text"&&((attr=elem.getAttribute("type"))==null||attr.toLowerCase()==="text");},"first":createPositionalPseudo(function(){return[0];}),"last":createPositionalPseudo(function(matchIndexes,length){return[length-1];}),"eq":createPositionalPseudo(function(matchIndexes,length,argument){return[argument<0?argument+length:argument];}),"even":createPositionalPseudo(function(matchIndexes,length){var i=0;for(;i<length;i+=2){matchIndexes.push(i);}return matchIndexes;}),"odd":createPositionalPseudo(function(matchIndexes,length){var i=1;for(;i<length;i+=2){matchIndexes.push(i);}return matchIndexes;}),"lt":createPositionalPseudo(function(matchIndexes,length,argument){var i=argument<0?argument+length:argument;for(;--i>=0;){matchIndexes.push(i);}return matchIndexes;}),"gt":createPositionalPseudo(function(matchIndexes,length,argument){var i=argument<0?argument+length:argument;for(;++i<length;){matchIndexes.push(i);}return matchIndexes;})}};Expr.pseudos["nth"]=Expr.pseudos["eq"];for(i in {radio:true,checkbox:true,file:true,password:true,image:true}){Expr.pseudos[i]=createInputPseudo(i);}for(i in {submit:true,reset:true}){Expr.pseudos[i]=createButtonPseudo(i);}function setFilters(){}setFilters.prototype=Expr.filters=Expr.pseudos;Expr.setFilters=new setFilters();tokenize=Sizzle.tokenize=function(selector,parseOnly){var matched,match,tokens,type,soFar,groups,preFilters,cached=tokenCache[selector+" "];if(cached){return parseOnly?0:cached.slice(0);}soFar=selector;groups=[];preFilters=Expr.preFilter;while(soFar){if(!matched||(match=rcomma.exec(soFar))){if(match){soFar=soFar.slice(match[0].length)||soFar;}groups.push((tokens=[]));}matched=false;if((match=rcombinators.exec(soFar))){matched=match.shift();tokens.push({value:matched,type:match[0].replace(rtrim," ")});soFar=soFar.slice(matched.length);}for(type in Expr.filter){if((match=matchExpr[type].exec(soFar))&&(!preFilters[type]||(match=preFilters[type](match)))){matched=match.shift();tokens.push({value:matched,type:type,matches:match});soFar=soFar.slice(matched.length);}}if(!matched){break;}}return parseOnly?soFar.length:soFar?Sizzle.error(selector):tokenCache(selector,groups).slice(0);};function toSelector(tokens){var i=0,len=tokens.length,selector="";for(;i<len;i++){selector+=tokens[i].value;}return selector;}function addCombinator(matcher,combinator,base){var dir=combinator.dir,checkNonElements=base&&dir==="parentNode",doneName=done++;return combinator.first?function(elem,context,xml){while((elem=elem[dir])){if(elem.nodeType===1||checkNonElements){return matcher(elem,context,xml);}}}:function(elem,context,xml){var oldCache,outerCache,newCache=[dirruns,doneName];if(xml){while((elem=elem[dir])){if(elem.nodeType===1||checkNonElements){if(matcher(elem,context,xml)){return true;}}}}else{while((elem=elem[dir])){if(elem.nodeType===1||checkNonElements){outerCache=elem[expando]||(elem[expando]={});if((oldCache=outerCache[dir])&&oldCache[0]===dirruns&&oldCache[1]===doneName){return(newCache[2]=oldCache[2]);}else{outerCache[dir]=newCache;if((newCache[2]=matcher(elem,context,xml))){return true;}}}}}};}function elementMatcher(matchers){return matchers.length>1?function(elem,context,xml){var i=matchers.length;while(i--){if(!matchers[i](elem,context,xml)){return false;}}return true;}:matchers[0];}function multipleContexts(selector,contexts,results){var i=0,len=contexts.length;for(;i<len;i++){Sizzle(selector,contexts[i],results);}return results;}function condense(unmatched,map,filter,context,xml){var elem,newUnmatched=[],i=0,len=unmatched.length,mapped=map!=null;for(;i<len;i++){if((elem=unmatched[i])){if(!filter||filter(elem,context,xml)){newUnmatched.push(elem);if(mapped){map.push(i);}}}}return newUnmatched;}function setMatcher(preFilter,selector,matcher,postFilter,postFinder,postSelector){if(postFilter&&!postFilter[expando]){postFilter=setMatcher(postFilter);}if(postFinder&&!postFinder[expando]){postFinder=setMatcher(postFinder,postSelector);}return markFunction(function(seed,results,context,xml){var temp,i,elem,preMap=[],postMap=[],preexisting=results.length,elems=seed||multipleContexts(selector||"*",context.nodeType?[context]:context,[]),matcherIn=preFilter&&(seed||!selector)?condense(elems,preMap,preFilter,context,xml):elems,matcherOut=matcher?postFinder||(seed?preFilter:preexisting||postFilter)?[]:results:matcherIn;if(matcher){matcher(matcherIn,matcherOut,context,xml);}if(postFilter){temp=condense(matcherOut,postMap);postFilter(temp,[],context,xml);i=temp.length;while(i--){if((elem=temp[i])){matcherOut[postMap[i]]=!(matcherIn[postMap[i]]=elem);}}}if(seed){if(postFinder||preFilter){if(postFinder){temp=[];i=matcherOut.length;while(i--){if((elem=matcherOut[i])){temp.push((matcherIn[i]=elem));}}postFinder(null,(matcherOut=[]),temp,xml);}i=matcherOut.length;while(i--){if((elem=matcherOut[i])&&(temp=postFinder?indexOf.call(seed,elem):preMap[i])>-1){seed[temp]=!(results[temp]=elem);}}}}else{matcherOut=condense(matcherOut===results?matcherOut.splice(preexisting,matcherOut.length):matcherOut);if(postFinder){postFinder(null,results,matcherOut,xml);}else{push.apply(results,matcherOut);}}});}function matcherFromTokens(tokens){var checkContext,matcher,j,len=tokens.length,leadingRelative=Expr.relative[tokens[0].type],implicitRelative=leadingRelative||Expr.relative[" "],i=leadingRelative?1:0,matchContext=addCombinator(function(elem){return elem===checkContext;},implicitRelative,true),matchAnyContext=addCombinator(function(elem){return indexOf.call(checkContext,elem)>-1;},implicitRelative,true),matchers=[function(elem,context,xml){return(!leadingRelative&&(xml||context!==outermostContext))||((checkContext=context).nodeType?matchContext(elem,context,xml):matchAnyContext(elem,context,xml));}];for(;i<len;i++){if((matcher=Expr.relative[tokens[i].type])){matchers=[addCombinator(elementMatcher(matchers),matcher)];}else{matcher=Expr.filter[tokens[i].type].apply(null,tokens[i].matches);if(matcher[expando]){j=++i;for(;j<len;j++){if(Expr.relative[tokens[j].type]){break;}}return setMatcher(i>1&&elementMatcher(matchers),i>1&&toSelector(tokens.slice(0,i-1).concat({value:tokens[i-2].type===" "?"*":""})).replace(rtrim,"$1"),matcher,i<j&&matcherFromTokens(tokens.slice(i,j)),j<len&&matcherFromTokens((tokens=tokens.slice(j))),j<len&&toSelector(tokens));}matchers.push(matcher);}}return elementMatcher(matchers);}function matcherFromGroupMatchers(elementMatchers,setMatchers){var bySet=setMatchers.length>0,byElement=elementMatchers.length>0,superMatcher=function(seed,context,xml,results,outermost){var elem,j,matcher,matchedCount=0,i="0",unmatched=seed&&[],setMatched=[],contextBackup=outermostContext,elems=seed||byElement&&Expr.find["TAG"]("*",outermost),dirrunsUnique=(dirruns+=contextBackup==null?1:Math.random()||0.1),len=elems.length;if(outermost){outermostContext=context!==document&&context;}for(;i!==len&&(elem=elems[i])!=null;i++){if(byElement&&elem){j=0;while((matcher=elementMatchers[j++])){if(matcher(elem,context,xml)){results.push(elem);break;}}if(outermost){dirruns=dirrunsUnique;}}if(bySet){if((elem=!matcher&&elem)){matchedCount--;}if(seed){unmatched.push(elem);}}}matchedCount+=i;if(bySet&&i!==matchedCount){j=0;while((matcher=setMatchers[j++])){matcher(unmatched,setMatched,context,xml);}if(seed){if(matchedCount>0){while(i--){if(!(unmatched[i]||setMatched[i])){setMatched[i]=pop.call(results);}}}setMatched=condense(setMatched);}push.apply(results,setMatched);if(outermost&&!seed&&setMatched.length>0&&(matchedCount+setMatchers.length)>1){Sizzle.uniqueSort(results);}}if(outermost){dirruns=dirrunsUnique;outermostContext=contextBackup;}return unmatched;};return bySet?markFunction(superMatcher):superMatcher;}compile=Sizzle.compile=function(selector,match){var i,setMatchers=[],elementMatchers=[],cached=compilerCache[selector+" "];if(!cached){if(!match){match=tokenize(selector);}i=match.length;while(i--){cached=matcherFromTokens(match[i]);if(cached[expando]){setMatchers.push(cached);}else{elementMatchers.push(cached);}}cached=compilerCache(selector,matcherFromGroupMatchers(elementMatchers,setMatchers));cached.selector=selector;}return cached;};select=Sizzle.select=function(selector,context,results,seed){var i,tokens,token,type,find,compiled=typeof selector==="function"&&selector,match=!seed&&tokenize((selector=compiled.selector||selector));results=results||[];if(match.length===1){tokens=match[0]=match[0].slice(0);if(tokens.length>2&&(token=tokens[0]).type==="ID"&&support.getById&&context.nodeType===9&&documentIsHTML&&Expr.relative[tokens[1].type]){context=(Expr.find["ID"](token.matches[0].replace(runescape,funescape),context)||[])[0];if(!context){return results;}else{if(compiled){context=context.parentNode;}}selector=selector.slice(tokens.shift().value.length);}i=matchExpr["needsContext"].test(selector)?0:tokens.length;while(i--){token=tokens[i];if(Expr.relative[(type=token.type)]){break;}if((find=Expr.find[type])){if((seed=find(token.matches[0].replace(runescape,funescape),rsibling.test(tokens[0].type)&&testContext(context.parentNode)||context))){tokens.splice(i,1);selector=seed.length&&toSelector(tokens);if(!selector){push.apply(results,seed);return results;}break;}}}}(compiled||compile(selector,match))(seed,context,!documentIsHTML,results,rsibling.test(selector)&&testContext(context.parentNode)||context);return results;};support.sortStable=expando.split("").sort(sortOrder).join("")===expando;support.detectDuplicates=!!hasDuplicate;setDocument();support.sortDetached=assert(function(div1){return div1.compareDocumentPosition(document.createElement("div"))&1;});if(!assert(function(div){div.innerHTML="<a href='#'></a>";return div.firstChild.getAttribute("href")==="#";})){addHandle("type|href|height|width",function(elem,name,isXML){if(!isXML){return elem.getAttribute(name,name.toLowerCase()==="type"?1:2);}});}if(!support.attributes||!assert(function(div){div.innerHTML="<input/>";div.firstChild.setAttribute("value","");return div.firstChild.getAttribute("value")==="";})){addHandle("value",function(elem,name,isXML){if(!isXML&&elem.nodeName.toLowerCase()==="input"){return elem.defaultValue;}});}if(!assert(function(div){return div.getAttribute("disabled")==null;})){addHandle(booleans,function(elem,name,isXML){var val;if(!isXML){return elem[name]===true?name.toLowerCase():(val=elem.getAttributeNode(name))&&val.specified?val.value:null;}});}return Sizzle;})(window);jQuery.find=Sizzle;jQuery.expr=Sizzle.selectors;jQuery.expr[":"]=jQuery.expr.pseudos;jQuery.unique=Sizzle.uniqueSort;jQuery.text=Sizzle.getText;jQuery.isXMLDoc=Sizzle.isXML;jQuery.contains=Sizzle.contains;var rneedsContext=jQuery.expr.match.needsContext;var rsingleTag=(/^<(\w+)\s*\/?>(?:<\/\1>|)$/);var risSimple=/^.[^:#\[\.,]*$/;function winnow(elements,qualifier,not){if(jQuery.isFunction(qualifier)){return jQuery.grep(elements,function(elem,i){return !!qualifier.call(elem,i,elem)!==not;});}if(qualifier.nodeType){return jQuery.grep(elements,function(elem){return(elem===qualifier)!==not;});}if(typeof qualifier==="string"){if(risSimple.test(qualifier)){return jQuery.filter(qualifier,elements,not);}qualifier=jQuery.filter(qualifier,elements);}return jQuery.grep(elements,function(elem){return(jQuery.inArray(elem,qualifier)>=0)!==not;});}jQuery.filter=function(expr,elems,not){var elem=elems[0];if(not){expr=":not("+expr+")";}return elems.length===1&&elem.nodeType===1?jQuery.find.matchesSelector(elem,expr)?[elem]:[]:jQuery.find.matches(expr,jQuery.grep(elems,function(elem){return elem.nodeType===1;}));};jQuery.fn.extend({find:function(selector){var i,ret=[],self=this,len=self.length;if(typeof selector!=="string"){return this.pushStack(jQuery(selector).filter(function(){for(i=0;i<len;i++){if(jQuery.contains(self[i],this)){return true;}}}));}for(i=0;i<len;i++){jQuery.find(selector,self[i],ret);}ret=this.pushStack(len>1?jQuery.unique(ret):ret);ret.selector=this.selector?this.selector+" "+selector:selector;return ret;},filter:function(selector){return this.pushStack(winnow(this,selector||[],false));},not:function(selector){return this.pushStack(winnow(this,selector||[],true));},is:function(selector){return !!winnow(this,typeof selector==="string"&&rneedsContext.test(selector)?jQuery(selector):selector||[],false).length;}});var rootjQuery,document=window.document,rquickExpr=/^(?:\s*(<[\w\W]+>)[^>]*|#([\w-]*))$/,init=jQuery.fn.init=function(selector,context){var match,elem;if(!selector){return this;}if(typeof selector==="string"){if(selector.charAt(0)==="<"&&selector.charAt(selector.length-1)===">"&&selector.length>=3){match=[null,selector,null];}else{match=rquickExpr.exec(selector);}if(match&&(match[1]||!context)){if(match[1]){context=context instanceof jQuery?context[0]:context;jQuery.merge(this,jQuery.parseHTML(match[1],context&&context.nodeType?context.ownerDocument||context:document,true));if(rsingleTag.test(match[1])&&jQuery.isPlainObject(context)){for(match in context){if(jQuery.isFunction(this[match])){this[match](context[match]);}else{this.attr(match,context[match]);}}}return this;}else{elem=document.getElementById(match[2]);if(elem&&elem.parentNode){if(elem.id!==match[2]){return rootjQuery.find(selector);}this.length=1;this[0]=elem;}this.context=document;this.selector=selector;return this;}}else{if(!context||context.jquery){return(context||rootjQuery).find(selector);}else{return this.constructor(context).find(selector);}}}else{if(selector.nodeType){this.context=this[0]=selector;this.length=1;return this;}else{if(jQuery.isFunction(selector)){return typeof rootjQuery.ready!=="undefined"?rootjQuery.ready(selector):selector(jQuery);}}}if(selector.selector!==undefined){this.selector=selector.selector;this.context=selector.context;}return jQuery.makeArray(selector,this);};init.prototype=jQuery.fn;rootjQuery=jQuery(document);var rparentsprev=/^(?:parents|prev(?:Until|All))/,guaranteedUnique={children:true,contents:true,next:true,prev:true};jQuery.extend({dir:function(elem,dir,until){var matched=[],cur=elem[dir];while(cur&&cur.nodeType!==9&&(until===undefined||cur.nodeType!==1||!jQuery(cur).is(until))){if(cur.nodeType===1){matched.push(cur);}cur=cur[dir];}return matched;},sibling:function(n,elem){var r=[];for(;n;n=n.nextSibling){if(n.nodeType===1&&n!==elem){r.push(n);}}return r;}});jQuery.fn.extend({has:function(target){var i,targets=jQuery(target,this),len=targets.length;return this.filter(function(){for(i=0;i<len;i++){if(jQuery.contains(this,targets[i])){return true;}}});},closest:function(selectors,context){var cur,i=0,l=this.length,matched=[],pos=rneedsContext.test(selectors)||typeof selectors!=="string"?jQuery(selectors,context||this.context):0;for(;i<l;i++){for(cur=this[i];cur&&cur!==context;cur=cur.parentNode){if(cur.nodeType<11&&(pos?pos.index(cur)>-1:cur.nodeType===1&&jQuery.find.matchesSelector(cur,selectors))){matched.push(cur);break;}}}return this.pushStack(matched.length>1?jQuery.unique(matched):matched);},index:function(elem){if(!elem){return(this[0]&&this[0].parentNode)?this.first().prevAll().length:-1;}if(typeof elem==="string"){return jQuery.inArray(this[0],jQuery(elem));}return jQuery.inArray(elem.jquery?elem[0]:elem,this);},add:function(selector,context){return this.pushStack(jQuery.unique(jQuery.merge(this.get(),jQuery(selector,context))));},addBack:function(selector){return this.add(selector==null?this.prevObject:this.prevObject.filter(selector));}});function sibling(cur,dir){do{cur=cur[dir];}while(cur&&cur.nodeType!==1);return cur;}jQuery.each({parent:function(elem){var parent=elem.parentNode;return parent&&parent.nodeType!==11?parent:null;},parents:function(elem){return jQuery.dir(elem,"parentNode");},parentsUntil:function(elem,i,until){return jQuery.dir(elem,"parentNode",until);},next:function(elem){return sibling(elem,"nextSibling");},prev:function(elem){return sibling(elem,"previousSibling");},nextAll:function(elem){return jQuery.dir(elem,"nextSibling");},prevAll:function(elem){return jQuery.dir(elem,"previousSibling");},nextUntil:function(elem,i,until){return jQuery.dir(elem,"nextSibling",until);},prevUntil:function(elem,i,until){return jQuery.dir(elem,"previousSibling",until);},siblings:function(elem){return jQuery.sibling((elem.parentNode||{}).firstChild,elem);},children:function(elem){return jQuery.sibling(elem.firstChild);},contents:function(elem){return jQuery.nodeName(elem,"iframe")?elem.contentDocument||elem.contentWindow.document:jQuery.merge([],elem.childNodes);}},function(name,fn){jQuery.fn[name]=function(until,selector){var ret=jQuery.map(this,fn,until);if(name.slice(-5)!=="Until"){selector=until;}if(selector&&typeof selector==="string"){ret=jQuery.filter(selector,ret);}if(this.length>1){if(!guaranteedUnique[name]){ret=jQuery.unique(ret);}if(rparentsprev.test(name)){ret=ret.reverse();}}return this.pushStack(ret);};});var rnotwhite=(/\S+/g);var optionsCache={};function createOptions(options){var object=optionsCache[options]={};jQuery.each(options.match(rnotwhite)||[],function(_,flag){object[flag]=true;});return object;}jQuery.Callbacks=function(options){options=typeof options==="string"?(optionsCache[options]||createOptions(options)):jQuery.extend({},options);var firing,memory,fired,firingLength,firingIndex,firingStart,list=[],stack=!options.once&&[],fire=function(data){memory=options.memory&&data;fired=true;firingIndex=firingStart||0;firingStart=0;firingLength=list.length;firing=true;for(;list&&firingIndex<firingLength;firingIndex++){if(list[firingIndex].apply(data[0],data[1])===false&&options.stopOnFalse){memory=false;break;}}firing=false;if(list){if(stack){if(stack.length){fire(stack.shift());}}else{if(memory){list=[];}else{self.disable();}}}},self={add:function(){if(list){var start=list.length;(function add(args){jQuery.each(args,function(_,arg){var type=jQuery.type(arg);if(type==="function"){if(!options.unique||!self.has(arg)){list.push(arg);}}else{if(arg&&arg.length&&type!=="string"){add(arg);}}});})(arguments);if(firing){firingLength=list.length;}else{if(memory){firingStart=start;fire(memory);}}}return this;},remove:function(){if(list){jQuery.each(arguments,function(_,arg){var index;while((index=jQuery.inArray(arg,list,index))>-1){list.splice(index,1);if(firing){if(index<=firingLength){firingLength--;}if(index<=firingIndex){firingIndex--;}}}});}return this;},has:function(fn){return fn?jQuery.inArray(fn,list)>-1:!!(list&&list.length);},empty:function(){list=[];firingLength=0;return this;},disable:function(){list=stack=memory=undefined;return this;},disabled:function(){return !list;},lock:function(){stack=undefined;if(!memory){self.disable();}return this;},locked:function(){return !stack;},fireWith:function(context,args){if(list&&(!fired||stack)){args=args||[];args=[context,args.slice?args.slice():args];if(firing){stack.push(args);}else{fire(args);}}return this;},fire:function(){self.fireWith(this,arguments);return this;},fired:function(){return !!fired;}};return self;};jQuery.extend({Deferred:function(func){var tuples=[["resolve","done",jQuery.Callbacks("once memory"),"resolved"],["reject","fail",jQuery.Callbacks("once memory"),"rejected"],["notify","progress",jQuery.Callbacks("memory")]],state="pending",promise={state:function(){return state;},always:function(){deferred.done(arguments).fail(arguments);return this;},then:function(){var fns=arguments;return jQuery.Deferred(function(newDefer){jQuery.each(tuples,function(i,tuple){var fn=jQuery.isFunction(fns[i])&&fns[i];deferred[tuple[1]](function(){var returned=fn&&fn.apply(this,arguments);if(returned&&jQuery.isFunction(returned.promise)){returned.promise().done(newDefer.resolve).fail(newDefer.reject).progress(newDefer.notify);}else{newDefer[tuple[0]+"With"](this===promise?newDefer.promise():this,fn?[returned]:arguments);}});});fns=null;}).promise();},promise:function(obj){return obj!=null?jQuery.extend(obj,promise):promise;}},deferred={};promise.pipe=promise.then;jQuery.each(tuples,function(i,tuple){var list=tuple[2],stateString=tuple[3];promise[tuple[1]]=list.add;if(stateString){list.add(function(){state=stateString;},tuples[i^1][2].disable,tuples[2][2].lock);}deferred[tuple[0]]=function(){deferred[tuple[0]+"With"](this===deferred?promise:this,arguments);return this;};deferred[tuple[0]+"With"]=list.fireWith;});promise.promise(deferred);if(func){func.call(deferred,deferred);}return deferred;},when:function(subordinate){var i=0,resolveValues=slice.call(arguments),length=resolveValues.length,remaining=length!==1||(subordinate&&jQuery.isFunction(subordinate.promise))?length:0,deferred=remaining===1?subordinate:jQuery.Deferred(),updateFunc=function(i,contexts,values){return function(value){contexts[i]=this;values[i]=arguments.length>1?slice.call(arguments):value;if(values===progressValues){deferred.notifyWith(contexts,values);}else{if(!(--remaining)){deferred.resolveWith(contexts,values);}}};},progressValues,progressContexts,resolveContexts;if(length>1){progressValues=new Array(length);progressContexts=new Array(length);resolveContexts=new Array(length);for(;i<length;i++){if(resolveValues[i]&&jQuery.isFunction(resolveValues[i].promise)){resolveValues[i].promise().done(updateFunc(i,resolveContexts,resolveValues)).fail(deferred.reject).progress(updateFunc(i,progressContexts,progressValues));}else{--remaining;}}}if(!remaining){deferred.resolveWith(resolveContexts,resolveValues);}return deferred.promise();}});var readyList;jQuery.fn.ready=function(fn){jQuery.ready.promise().done(fn);return this;};jQuery.extend({isReady:false,readyWait:1,holdReady:function(hold){if(hold){jQuery.readyWait++;}else{jQuery.ready(true);}},ready:function(wait){if(wait===true?--jQuery.readyWait:jQuery.isReady){return;}if(!document.body){return setTimeout(jQuery.ready);}jQuery.isReady=true;if(wait!==true&&--jQuery.readyWait>0){return;}readyList.resolveWith(document,[jQuery]);if(jQuery.fn.triggerHandler){jQuery(document).triggerHandler("ready");jQuery(document).off("ready");}}});function detach(){if(document.addEventListener){document.removeEventListener("DOMContentLoaded",completed,false);window.removeEventListener("load",completed,false);}else{document.detachEvent("onreadystatechange",completed);window.detachEvent("onload",completed);}}function completed(){if(document.addEventListener||event.type==="load"||document.readyState==="complete"){detach();jQuery.ready();}}jQuery.ready.promise=function(obj){if(!readyList){readyList=jQuery.Deferred();if(document.readyState==="complete"){setTimeout(jQuery.ready);}else{if(document.addEventListener){document.addEventListener("DOMContentLoaded",completed,false);window.addEventListener("load",completed,false);}else{document.attachEvent("onreadystatechange",completed);window.attachEvent("onload",completed);var top=false;try{top=window.frameElement==null&&document.documentElement;}catch(e){}if(top&&top.doScroll){(function doScrollCheck(){if(!jQuery.isReady){try{top.doScroll("left");}catch(e){return setTimeout(doScrollCheck,50);}detach();jQuery.ready();}})();}}}}return readyList.promise(obj);};var strundefined=typeof undefined;var i;for(i in jQuery(support)){break;}support.ownLast=i!=="0";support.inlineBlockNeedsLayout=false;jQuery(function(){var val,div,body,container;body=document.getElementsByTagName("body")[0];if(!body||!body.style){return;}div=document.createElement("div");container=document.createElement("div");container.style.cssText="position:absolute;border:0;width:0;height:0;top:0;left:-9999px";body.appendChild(container).appendChild(div);if(typeof div.style.zoom!==strundefined){div.style.cssText="display:inline;margin:0;border:0;padding:1px;width:1px;zoom:1";support.inlineBlockNeedsLayout=val=div.offsetWidth===3;if(val){body.style.zoom=1;}}body.removeChild(container);});(function(){var div=document.createElement("div");if(support.deleteExpando==null){support.deleteExpando=true;try{delete div.test;}catch(e){support.deleteExpando=false;}}div=null;})();jQuery.acceptData=function(elem){var noData=jQuery.noData[(elem.nodeName+" ").toLowerCase()],nodeType=+elem.nodeType||1;return nodeType!==1&&nodeType!==9?false:!noData||noData!==true&&elem.getAttribute("classid")===noData;};var rbrace=/^(?:\{[\w\W]*\}|\[[\w\W]*\])$/,rmultiDash=/([A-Z])/g;function dataAttr(elem,key,data){if(data===undefined&&elem.nodeType===1){var name="data-"+key.replace(rmultiDash,"-$1").toLowerCase();data=elem.getAttribute(name);if(typeof data==="string"){try{data=data==="true"?true:data==="false"?false:data==="null"?null:+data+""===data?+data:rbrace.test(data)?jQuery.parseJSON(data):data;}catch(e){}jQuery.data(elem,key,data);}else{data=undefined;}}return data;}function isEmptyDataObject(obj){var name;for(name in obj){if(name==="data"&&jQuery.isEmptyObject(obj[name])){continue;}if(name!=="toJSON"){return false;}}return true;}function internalData(elem,name,data,pvt){if(!jQuery.acceptData(elem)){return;}var ret,thisCache,internalKey=jQuery.expando,isNode=elem.nodeType,cache=isNode?jQuery.cache:elem,id=isNode?elem[internalKey]:elem[internalKey]&&internalKey;if((!id||!cache[id]||(!pvt&&!cache[id].data))&&data===undefined&&typeof name==="string"){return;}if(!id){if(isNode){id=elem[internalKey]=deletedIds.pop()||jQuery.guid++;}else{id=internalKey;}}if(!cache[id]){cache[id]=isNode?{}:{toJSON:jQuery.noop};}if(typeof name==="object"||typeof name==="function"){if(pvt){cache[id]=jQuery.extend(cache[id],name);}else{cache[id].data=jQuery.extend(cache[id].data,name);}}thisCache=cache[id];if(!pvt){if(!thisCache.data){thisCache.data={};}thisCache=thisCache.data;}if(data!==undefined){thisCache[jQuery.camelCase(name)]=data;}if(typeof name==="string"){ret=thisCache[name];if(ret==null){ret=thisCache[jQuery.camelCase(name)];}}else{ret=thisCache;}return ret;}function internalRemoveData(elem,name,pvt){if(!jQuery.acceptData(elem)){return;}var thisCache,i,isNode=elem.nodeType,cache=isNode?jQuery.cache:elem,id=isNode?elem[jQuery.expando]:jQuery.expando;if(!cache[id]){return;}if(name){thisCache=pvt?cache[id]:cache[id].data;if(thisCache){if(!jQuery.isArray(name)){if(name in thisCache){name=[name];}else{name=jQuery.camelCase(name);if(name in thisCache){name=[name];}else{name=name.split(" ");}}}else{name=name.concat(jQuery.map(name,jQuery.camelCase));}i=name.length;while(i--){delete thisCache[name[i]];}if(pvt?!isEmptyDataObject(thisCache):!jQuery.isEmptyObject(thisCache)){return;}}}if(!pvt){delete cache[id].data;if(!isEmptyDataObject(cache[id])){return;}}if(isNode){jQuery.cleanData([elem],true);}else{if(support.deleteExpando||cache!=cache.window){delete cache[id];}else{cache[id]=null;}}}jQuery.extend({cache:{},noData:{"applet ":true,"embed ":true,"object ":"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"},hasData:function(elem){elem=elem.nodeType?jQuery.cache[elem[jQuery.expando]]:elem[jQuery.expando];return !!elem&&!isEmptyDataObject(elem);},data:function(elem,name,data){return internalData(elem,name,data);},removeData:function(elem,name){return internalRemoveData(elem,name);},_data:function(elem,name,data){return internalData(elem,name,data,true);},_removeData:function(elem,name){return internalRemoveData(elem,name,true);}});jQuery.fn.extend({data:function(key,value){var i,name,data,elem=this[0],attrs=elem&&elem.attributes;if(key===undefined){if(this.length){data=jQuery.data(elem);if(elem.nodeType===1&&!jQuery._data(elem,"parsedAttrs")){i=attrs.length;while(i--){if(attrs[i]){name=attrs[i].name;if(name.indexOf("data-")===0){name=jQuery.camelCase(name.slice(5));dataAttr(elem,name,data[name]);}}}jQuery._data(elem,"parsedAttrs",true);}}return data;}if(typeof key==="object"){return this.each(function(){jQuery.data(this,key);});}return arguments.length>1?this.each(function(){jQuery.data(this,key,value);}):elem?dataAttr(elem,key,jQuery.data(elem,key)):undefined;},removeData:function(key){return this.each(function(){jQuery.removeData(this,key);});}});jQuery.extend({queue:function(elem,type,data){var queue;if(elem){type=(type||"fx")+"queue";queue=jQuery._data(elem,type);if(data){if(!queue||jQuery.isArray(data)){queue=jQuery._data(elem,type,jQuery.makeArray(data));}else{queue.push(data);}}return queue||[];}},dequeue:function(elem,type){type=type||"fx";var queue=jQuery.queue(elem,type),startLength=queue.length,fn=queue.shift(),hooks=jQuery._queueHooks(elem,type),next=function(){jQuery.dequeue(elem,type);};if(fn==="inprogress"){fn=queue.shift();startLength--;}if(fn){if(type==="fx"){queue.unshift("inprogress");}delete hooks.stop;fn.call(elem,next,hooks);}if(!startLength&&hooks){hooks.empty.fire();}},_queueHooks:function(elem,type){var key=type+"queueHooks";return jQuery._data(elem,key)||jQuery._data(elem,key,{empty:jQuery.Callbacks("once memory").add(function(){jQuery._removeData(elem,type+"queue");jQuery._removeData(elem,key);})});}});jQuery.fn.extend({queue:function(type,data){var setter=2;if(typeof type!=="string"){data=type;type="fx";setter--;}if(arguments.length<setter){return jQuery.queue(this[0],type);}return data===undefined?this:this.each(function(){var queue=jQuery.queue(this,type,data);jQuery._queueHooks(this,type);if(type==="fx"&&queue[0]!=="inprogress"){jQuery.dequeue(this,type);}});},dequeue:function(type){return this.each(function(){jQuery.dequeue(this,type);});},clearQueue:function(type){return this.queue(type||"fx",[]);},promise:function(type,obj){var tmp,count=1,defer=jQuery.Deferred(),elements=this,i=this.length,resolve=function(){if(!(--count)){defer.resolveWith(elements,[elements]);}};if(typeof type!=="string"){obj=type;type=undefined;}type=type||"fx";while(i--){tmp=jQuery._data(elements[i],type+"queueHooks");if(tmp&&tmp.empty){count++;tmp.empty.add(resolve);}}resolve();return defer.promise(obj);}});var pnum=(/[+-]?(?:\d*\.|)\d+(?:[eE][+-]?\d+|)/).source;var cssExpand=["Top","Right","Bottom","Left"];var isHidden=function(elem,el){elem=el||elem;return jQuery.css(elem,"display")==="none"||!jQuery.contains(elem.ownerDocument,elem);};var access=jQuery.access=function(elems,fn,key,value,chainable,emptyGet,raw){var i=0,length=elems.length,bulk=key==null;if(jQuery.type(key)==="object"){chainable=true;for(i in key){jQuery.access(elems,fn,i,key[i],true,emptyGet,raw);}}else{if(value!==undefined){chainable=true;if(!jQuery.isFunction(value)){raw=true;}if(bulk){if(raw){fn.call(elems,value);fn=null;}else{bulk=fn;fn=function(elem,key,value){return bulk.call(jQuery(elem),value);};}}if(fn){for(;i<length;i++){fn(elems[i],key,raw?value:value.call(elems[i],i,fn(elems[i],key)));}}}}return chainable?elems:bulk?fn.call(elems):length?fn(elems[0],key):emptyGet;};var rcheckableType=(/^(?:checkbox|radio)$/i);(function(){var input=document.createElement("input"),div=document.createElement("div"),fragment=document.createDocumentFragment();div.innerHTML="  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";support.leadingWhitespace=div.firstChild.nodeType===3;support.tbody=!div.getElementsByTagName("tbody").length;support.htmlSerialize=!!div.getElementsByTagName("link").length;support.html5Clone=document.createElement("nav").cloneNode(true).outerHTML!=="<:nav></:nav>";input.type="checkbox";input.checked=true;fragment.appendChild(input);support.appendChecked=input.checked;div.innerHTML="<textarea>x</textarea>";support.noCloneChecked=!!div.cloneNode(true).lastChild.defaultValue;fragment.appendChild(div);div.innerHTML="<input type='radio' checked='checked' name='t'/>";support.checkClone=div.cloneNode(true).cloneNode(true).lastChild.checked;support.noCloneEvent=true;if(div.attachEvent){div.attachEvent("onclick",function(){support.noCloneEvent=false;});div.cloneNode(true).click();}if(support.deleteExpando==null){support.deleteExpando=true;try{delete div.test;}catch(e){support.deleteExpando=false;}}})();(function(){var i,eventName,div=document.createElement("div");for(i in {submit:true,change:true,focusin:true}){eventName="on"+i;if(!(support[i+"Bubbles"]=eventName in window)){div.setAttribute(eventName,"t");support[i+"Bubbles"]=div.attributes[eventName].expando===false;}}div=null;})();var rformElems=/^(?:input|select|textarea)$/i,rkeyEvent=/^key/,rmouseEvent=/^(?:mouse|pointer|contextmenu)|click/,rfocusMorph=/^(?:focusinfocus|focusoutblur)$/,rtypenamespace=/^([^.]*)(?:\.(.+)|)$/;function returnTrue(){return true;}function returnFalse(){return false;}function safeActiveElement(){try{return document.activeElement;}catch(err){}}jQuery.event={global:{},add:function(elem,types,handler,data,selector){var tmp,events,t,handleObjIn,special,eventHandle,handleObj,handlers,type,namespaces,origType,elemData=jQuery._data(elem);if(!elemData){return;}if(handler.handler){handleObjIn=handler;handler=handleObjIn.handler;selector=handleObjIn.selector;}if(!handler.guid){handler.guid=jQuery.guid++;}if(!(events=elemData.events)){events=elemData.events={};}if(!(eventHandle=elemData.handle)){eventHandle=elemData.handle=function(e){return typeof jQuery!==strundefined&&(!e||jQuery.event.triggered!==e.type)?jQuery.event.dispatch.apply(eventHandle.elem,arguments):undefined;};eventHandle.elem=elem;}types=(types||"").match(rnotwhite)||[""];t=types.length;while(t--){tmp=rtypenamespace.exec(types[t])||[];type=origType=tmp[1];namespaces=(tmp[2]||"").split(".").sort();if(!type){continue;}special=jQuery.event.special[type]||{};type=(selector?special.delegateType:special.bindType)||type;special=jQuery.event.special[type]||{};handleObj=jQuery.extend({type:type,origType:origType,data:data,handler:handler,guid:handler.guid,selector:selector,needsContext:selector&&jQuery.expr.match.needsContext.test(selector),namespace:namespaces.join(".")},handleObjIn);if(!(handlers=events[type])){handlers=events[type]=[];handlers.delegateCount=0;if(!special.setup||special.setup.call(elem,data,namespaces,eventHandle)===false){if(elem.addEventListener){elem.addEventListener(type,eventHandle,false);}else{if(elem.attachEvent){elem.attachEvent("on"+type,eventHandle);}}}}if(special.add){special.add.call(elem,handleObj);if(!handleObj.handler.guid){handleObj.handler.guid=handler.guid;}}if(selector){handlers.splice(handlers.delegateCount++,0,handleObj);}else{handlers.push(handleObj);}jQuery.event.global[type]=true;}elem=null;},remove:function(elem,types,handler,selector,mappedTypes){var j,handleObj,tmp,origCount,t,events,special,handlers,type,namespaces,origType,elemData=jQuery.hasData(elem)&&jQuery._data(elem);if(!elemData||!(events=elemData.events)){return;}types=(types||"").match(rnotwhite)||[""];t=types.length;while(t--){tmp=rtypenamespace.exec(types[t])||[];type=origType=tmp[1];namespaces=(tmp[2]||"").split(".").sort();if(!type){for(type in events){jQuery.event.remove(elem,type+types[t],handler,selector,true);}continue;}special=jQuery.event.special[type]||{};type=(selector?special.delegateType:special.bindType)||type;handlers=events[type]||[];tmp=tmp[2]&&new RegExp("(^|\\.)"+namespaces.join("\\.(?:.*\\.|)")+"(\\.|$)");origCount=j=handlers.length;while(j--){handleObj=handlers[j];if((mappedTypes||origType===handleObj.origType)&&(!handler||handler.guid===handleObj.guid)&&(!tmp||tmp.test(handleObj.namespace))&&(!selector||selector===handleObj.selector||selector==="**"&&handleObj.selector)){handlers.splice(j,1);if(handleObj.selector){handlers.delegateCount--;}if(special.remove){special.remove.call(elem,handleObj);}}}if(origCount&&!handlers.length){if(!special.teardown||special.teardown.call(elem,namespaces,elemData.handle)===false){jQuery.removeEvent(elem,type,elemData.handle);}delete events[type];}}if(jQuery.isEmptyObject(events)){delete elemData.handle;jQuery._removeData(elem,"events");}},trigger:function(event,data,elem,onlyHandlers){var handle,ontype,cur,bubbleType,special,tmp,i,eventPath=[elem||document],type=hasOwn.call(event,"type")?event.type:event,namespaces=hasOwn.call(event,"namespace")?event.namespace.split("."):[];cur=tmp=elem=elem||document;if(elem.nodeType===3||elem.nodeType===8){return;}if(rfocusMorph.test(type+jQuery.event.triggered)){return;}if(type.indexOf(".")>=0){namespaces=type.split(".");type=namespaces.shift();namespaces.sort();}ontype=type.indexOf(":")<0&&"on"+type;event=event[jQuery.expando]?event:new jQuery.Event(type,typeof event==="object"&&event);event.isTrigger=onlyHandlers?2:3;event.namespace=namespaces.join(".");event.namespace_re=event.namespace?new RegExp("(^|\\.)"+namespaces.join("\\.(?:.*\\.|)")+"(\\.|$)"):null;event.result=undefined;if(!event.target){event.target=elem;}data=data==null?[event]:jQuery.makeArray(data,[event]);special=jQuery.event.special[type]||{};if(!onlyHandlers&&special.trigger&&special.trigger.apply(elem,data)===false){return;}if(!onlyHandlers&&!special.noBubble&&!jQuery.isWindow(elem)){bubbleType=special.delegateType||type;if(!rfocusMorph.test(bubbleType+type)){cur=cur.parentNode;}for(;cur;cur=cur.parentNode){eventPath.push(cur);tmp=cur;}if(tmp===(elem.ownerDocument||document)){eventPath.push(tmp.defaultView||tmp.parentWindow||window);}}i=0;while((cur=eventPath[i++])&&!event.isPropagationStopped()){event.type=i>1?bubbleType:special.bindType||type;handle=(jQuery._data(cur,"events")||{})[event.type]&&jQuery._data(cur,"handle");if(handle){handle.apply(cur,data);}handle=ontype&&cur[ontype];if(handle&&handle.apply&&jQuery.acceptData(cur)){event.result=handle.apply(cur,data);if(event.result===false){event.preventDefault();}}}event.type=type;if(!onlyHandlers&&!event.isDefaultPrevented()){if((!special._default||special._default.apply(eventPath.pop(),data)===false)&&jQuery.acceptData(elem)){if(ontype&&elem[type]&&!jQuery.isWindow(elem)){tmp=elem[ontype];if(tmp){elem[ontype]=null;}jQuery.event.triggered=type;try{elem[type]();}catch(e){}jQuery.event.triggered=undefined;if(tmp){elem[ontype]=tmp;}}}}return event.result;},dispatch:function(event){event=jQuery.event.fix(event);var i,ret,handleObj,matched,j,handlerQueue=[],args=slice.call(arguments),handlers=(jQuery._data(this,"events")||{})[event.type]||[],special=jQuery.event.special[event.type]||{};args[0]=event;event.delegateTarget=this;if(special.preDispatch&&special.preDispatch.call(this,event)===false){return;}handlerQueue=jQuery.event.handlers.call(this,event,handlers);i=0;while((matched=handlerQueue[i++])&&!event.isPropagationStopped()){event.currentTarget=matched.elem;j=0;while((handleObj=matched.handlers[j++])&&!event.isImmediatePropagationStopped()){if(!event.namespace_re||event.namespace_re.test(handleObj.namespace)){event.handleObj=handleObj;event.data=handleObj.data;ret=((jQuery.event.special[handleObj.origType]||{}).handle||handleObj.handler).apply(matched.elem,args);if(ret!==undefined){if((event.result=ret)===false){event.preventDefault();event.stopPropagation();}}}}}if(special.postDispatch){special.postDispatch.call(this,event);}return event.result;},handlers:function(event,handlers){var sel,handleObj,matches,i,handlerQueue=[],delegateCount=handlers.delegateCount,cur=event.target;if(delegateCount&&cur.nodeType&&(!event.button||event.type!=="click")){for(;cur!=this;cur=cur.parentNode||this){if(cur.nodeType===1&&(cur.disabled!==true||event.type!=="click")){matches=[];for(i=0;i<delegateCount;i++){handleObj=handlers[i];sel=handleObj.selector+" ";if(matches[sel]===undefined){matches[sel]=handleObj.needsContext?jQuery(sel,this).index(cur)>=0:jQuery.find(sel,this,null,[cur]).length;}if(matches[sel]){matches.push(handleObj);}}if(matches.length){handlerQueue.push({elem:cur,handlers:matches});}}}}if(delegateCount<handlers.length){handlerQueue.push({elem:this,handlers:handlers.slice(delegateCount)});}return handlerQueue;},fix:function(event){if(event[jQuery.expando]){return event;}var i,prop,copy,type=event.type,originalEvent=event,fixHook=this.fixHooks[type];if(!fixHook){this.fixHooks[type]=fixHook=rmouseEvent.test(type)?this.mouseHooks:rkeyEvent.test(type)?this.keyHooks:{};}copy=fixHook.props?this.props.concat(fixHook.props):this.props;event=new jQuery.Event(originalEvent);i=copy.length;while(i--){prop=copy[i];event[prop]=originalEvent[prop];}if(!event.target){event.target=originalEvent.srcElement||document;}if(event.target.nodeType===3){event.target=event.target.parentNode;}event.metaKey=!!event.metaKey;return fixHook.filter?fixHook.filter(event,originalEvent):event;},props:"altKey bubbles cancelable ctrlKey currentTarget eventPhase metaKey relatedTarget shiftKey target timeStamp view which".split(" "),fixHooks:{},keyHooks:{props:"char charCode key keyCode".split(" "),filter:function(event,original){if(event.which==null){event.which=original.charCode!=null?original.charCode:original.keyCode;}return event;}},mouseHooks:{props:"button buttons clientX clientY fromElement offsetX offsetY pageX pageY screenX screenY toElement".split(" "),filter:function(event,original){var body,eventDoc,doc,button=original.button,fromElement=original.fromElement;if(event.pageX==null&&original.clientX!=null){eventDoc=event.target.ownerDocument||document;doc=eventDoc.documentElement;body=eventDoc.body;event.pageX=original.clientX+(doc&&doc.scrollLeft||body&&body.scrollLeft||0)-(doc&&doc.clientLeft||body&&body.clientLeft||0);event.pageY=original.clientY+(doc&&doc.scrollTop||body&&body.scrollTop||0)-(doc&&doc.clientTop||body&&body.clientTop||0);}if(!event.relatedTarget&&fromElement){event.relatedTarget=fromElement===event.target?original.toElement:fromElement;}if(!event.which&&button!==undefined){event.which=(button&1?1:(button&2?3:(button&4?2:0)));}return event;}},special:{load:{noBubble:true},focus:{trigger:function(){if(this!==safeActiveElement()&&this.focus){try{this.focus();return false;}catch(e){}}},delegateType:"focusin"},blur:{trigger:function(){if(this===safeActiveElement()&&this.blur){this.blur();return false;}},delegateType:"focusout"},click:{trigger:function(){if(jQuery.nodeName(this,"input")&&this.type==="checkbox"&&this.click){this.click();return false;}},_default:function(event){return jQuery.nodeName(event.target,"a");}},beforeunload:{postDispatch:function(event){if(event.result!==undefined&&event.originalEvent){event.originalEvent.returnValue=event.result;}}}},simulate:function(type,elem,event,bubble){var e=jQuery.extend(new jQuery.Event(),event,{type:type,isSimulated:true,originalEvent:{}});if(bubble){jQuery.event.trigger(e,null,elem);}else{jQuery.event.dispatch.call(elem,e);}if(e.isDefaultPrevented()){event.preventDefault();}}};jQuery.removeEvent=document.removeEventListener?function(elem,type,handle){if(elem.removeEventListener){elem.removeEventListener(type,handle,false);}}:function(elem,type,handle){var name="on"+type;if(elem.detachEvent){if(typeof elem[name]===strundefined){elem[name]=null;}elem.detachEvent(name,handle);}};jQuery.Event=function(src,props){if(!(this instanceof jQuery.Event)){return new jQuery.Event(src,props);}if(src&&src.type){this.originalEvent=src;this.type=src.type;this.isDefaultPrevented=src.defaultPrevented||src.defaultPrevented===undefined&&src.returnValue===false?returnTrue:returnFalse;}else{this.type=src;}if(props){jQuery.extend(this,props);}this.timeStamp=src&&src.timeStamp||jQuery.now();this[jQuery.expando]=true;};jQuery.Event.prototype={isDefaultPrevented:returnFalse,isPropagationStopped:returnFalse,isImmediatePropagationStopped:returnFalse,preventDefault:function(){var e=this.originalEvent;this.isDefaultPrevented=returnTrue;if(!e){return;}if(e.preventDefault){e.preventDefault();}else{e.returnValue=false;}},stopPropagation:function(){var e=this.originalEvent;this.isPropagationStopped=returnTrue;if(!e){return;}if(e.stopPropagation){e.stopPropagation();}e.cancelBubble=true;},stopImmediatePropagation:function(){var e=this.originalEvent;this.isImmediatePropagationStopped=returnTrue;if(e&&e.stopImmediatePropagation){e.stopImmediatePropagation();}this.stopPropagation();}};jQuery.each({mouseenter:"mouseover",mouseleave:"mouseout",pointerenter:"pointerover",pointerleave:"pointerout"},function(orig,fix){jQuery.event.special[orig]={delegateType:fix,bindType:fix,handle:function(event){var ret,target=this,related=event.relatedTarget,handleObj=event.handleObj;if(!related||(related!==target&&!jQuery.contains(target,related))){event.type=handleObj.origType;ret=handleObj.handler.apply(this,arguments);event.type=fix;}return ret;}};});if(!support.submitBubbles){jQuery.event.special.submit={setup:function(){if(jQuery.nodeName(this,"form")){return false;}jQuery.event.add(this,"click._submit keypress._submit",function(e){var elem=e.target,form=jQuery.nodeName(elem,"input")||jQuery.nodeName(elem,"button")?elem.form:undefined;if(form&&!jQuery._data(form,"submitBubbles")){jQuery.event.add(form,"submit._submit",function(event){event._submit_bubble=true;});jQuery._data(form,"submitBubbles",true);}});},postDispatch:function(event){if(event._submit_bubble){delete event._submit_bubble;if(this.parentNode&&!event.isTrigger){jQuery.event.simulate("submit",this.parentNode,event,true);}}},teardown:function(){if(jQuery.nodeName(this,"form")){return false;}jQuery.event.remove(this,"._submit");}};}if(!support.changeBubbles){jQuery.event.special.change={setup:function(){if(rformElems.test(this.nodeName)){if(this.type==="checkbox"||this.type==="radio"){jQuery.event.add(this,"propertychange._change",function(event){if(event.originalEvent.propertyName==="checked"){this._just_changed=true;}});jQuery.event.add(this,"click._change",function(event){if(this._just_changed&&!event.isTrigger){this._just_changed=false;}jQuery.event.simulate("change",this,event,true);});}return false;}jQuery.event.add(this,"beforeactivate._change",function(e){var elem=e.target;if(rformElems.test(elem.nodeName)&&!jQuery._data(elem,"changeBubbles")){jQuery.event.add(elem,"change._change",function(event){if(this.parentNode&&!event.isSimulated&&!event.isTrigger){jQuery.event.simulate("change",this.parentNode,event,true);}});jQuery._data(elem,"changeBubbles",true);}});},handle:function(event){var elem=event.target;if(this!==elem||event.isSimulated||event.isTrigger||(elem.type!=="radio"&&elem.type!=="checkbox")){return event.handleObj.handler.apply(this,arguments);}},teardown:function(){jQuery.event.remove(this,"._change");return !rformElems.test(this.nodeName);}};}if(!support.focusinBubbles){jQuery.each({focus:"focusin",blur:"focusout"},function(orig,fix){var handler=function(event){jQuery.event.simulate(fix,event.target,jQuery.event.fix(event),true);};jQuery.event.special[fix]={setup:function(){var doc=this.ownerDocument||this,attaches=jQuery._data(doc,fix);if(!attaches){doc.addEventListener(orig,handler,true);}jQuery._data(doc,fix,(attaches||0)+1);},teardown:function(){var doc=this.ownerDocument||this,attaches=jQuery._data(doc,fix)-1;if(!attaches){doc.removeEventListener(orig,handler,true);jQuery._removeData(doc,fix);}else{jQuery._data(doc,fix,attaches);}}};});}jQuery.fn.extend({on:function(types,selector,data,fn,one){var type,origFn;if(typeof types==="object"){if(typeof selector!=="string"){data=data||selector;selector=undefined;}for(type in types){this.on(type,selector,data,types[type],one);}return this;}if(data==null&&fn==null){fn=selector;data=selector=undefined;}else{if(fn==null){if(typeof selector==="string"){fn=data;data=undefined;}else{fn=data;data=selector;selector=undefined;}}}if(fn===false){fn=returnFalse;}else{if(!fn){return this;}}if(one===1){origFn=fn;fn=function(event){jQuery().off(event);return origFn.apply(this,arguments);};fn.guid=origFn.guid||(origFn.guid=jQuery.guid++);}return this.each(function(){jQuery.event.add(this,types,fn,data,selector);});},one:function(types,selector,data,fn){return this.on(types,selector,data,fn,1);},off:function(types,selector,fn){var handleObj,type;if(types&&types.preventDefault&&types.handleObj){handleObj=types.handleObj;jQuery(types.delegateTarget).off(handleObj.namespace?handleObj.origType+"."+handleObj.namespace:handleObj.origType,handleObj.selector,handleObj.handler);return this;}if(typeof types==="object"){for(type in types){this.off(type,selector,types[type]);}return this;}if(selector===false||typeof selector==="function"){fn=selector;selector=undefined;}if(fn===false){fn=returnFalse;}return this.each(function(){jQuery.event.remove(this,types,fn,selector);});},trigger:function(type,data){return this.each(function(){jQuery.event.trigger(type,data,this);});},triggerHandler:function(type,data){var elem=this[0];if(elem){return jQuery.event.trigger(type,data,elem,true);}}});function createSafeFragment(document){var list=nodeNames.split("|"),safeFrag=document.createDocumentFragment();if(safeFrag.createElement){while(list.length){safeFrag.createElement(list.pop());}}return safeFrag;}var nodeNames="abbr|article|aside|audio|bdi|canvas|data|datalist|details|figcaption|figure|footer|"+"header|hgroup|mark|meter|nav|output|progress|section|summary|time|video",rinlinejQuery=/ jQuery\d+="(?:null|\d+)"/g,rnoshimcache=new RegExp("<(?:"+nodeNames+")[\\s/>]","i"),rleadingWhitespace=/^\s+/,rxhtmlTag=/<(?!area|br|col|embed|hr|img|input|link|meta|param)(([\w:]+)[^>]*)\/>/gi,rtagName=/<([\w:]+)/,rtbody=/<tbody/i,rhtml=/<|&#?\w+;/,rnoInnerhtml=/<(?:script|style|link)/i,rchecked=/checked\s*(?:[^=]|=\s*.checked.)/i,rscriptType=/^$|\/(?:java|ecma)script/i,rscriptTypeMasked=/^true\/(.*)/,rcleanScript=/^\s*<!(?:\[CDATA\[|--)|(?:\]\]|--)>\s*$/g,wrapMap={option:[1,"<select multiple='multiple'>","</select>"],legend:[1,"<fieldset>","</fieldset>"],area:[1,"<map>","</map>"],param:[1,"<object>","</object>"],thead:[1,"<table>","</table>"],tr:[2,"<table><tbody>","</tbody></table>"],col:[2,"<table><tbody></tbody><colgroup>","</colgroup></table>"],td:[3,"<table><tbody><tr>","</tr></tbody></table>"],_default:support.htmlSerialize?[0,"",""]:[1,"X<div>","</div>"]},safeFragment=createSafeFragment(document),fragmentDiv=safeFragment.appendChild(document.createElement("div"));wrapMap.optgroup=wrapMap.option;wrapMap.tbody=wrapMap.tfoot=wrapMap.colgroup=wrapMap.caption=wrapMap.thead;wrapMap.th=wrapMap.td;function getAll(context,tag){var elems,elem,i=0,found=typeof context.getElementsByTagName!==strundefined?context.getElementsByTagName(tag||"*"):typeof context.querySelectorAll!==strundefined?context.querySelectorAll(tag||"*"):undefined;if(!found){for(found=[],elems=context.childNodes||context;(elem=elems[i])!=null;i++){if(!tag||jQuery.nodeName(elem,tag)){found.push(elem);}else{jQuery.merge(found,getAll(elem,tag));}}}return tag===undefined||tag&&jQuery.nodeName(context,tag)?jQuery.merge([context],found):found;}function fixDefaultChecked(elem){if(rcheckableType.test(elem.type)){elem.defaultChecked=elem.checked;}}function manipulationTarget(elem,content){return jQuery.nodeName(elem,"table")&&jQuery.nodeName(content.nodeType!==11?content:content.firstChild,"tr")?elem.getElementsByTagName("tbody")[0]||elem.appendChild(elem.ownerDocument.createElement("tbody")):elem;}function disableScript(elem){elem.type=(jQuery.find.attr(elem,"type")!==null)+"/"+elem.type;return elem;}function restoreScript(elem){var match=rscriptTypeMasked.exec(elem.type);if(match){elem.type=match[1];}else{elem.removeAttribute("type");}return elem;}function setGlobalEval(elems,refElements){var elem,i=0;for(;(elem=elems[i])!=null;i++){jQuery._data(elem,"globalEval",!refElements||jQuery._data(refElements[i],"globalEval"));}}function cloneCopyEvent(src,dest){if(dest.nodeType!==1||!jQuery.hasData(src)){return;}var type,i,l,oldData=jQuery._data(src),curData=jQuery._data(dest,oldData),events=oldData.events;if(events){delete curData.handle;curData.events={};for(type in events){for(i=0,l=events[type].length;i<l;i++){jQuery.event.add(dest,type,events[type][i]);}}}if(curData.data){curData.data=jQuery.extend({},curData.data);}}function fixCloneNodeIssues(src,dest){var nodeName,e,data;if(dest.nodeType!==1){return;}nodeName=dest.nodeName.toLowerCase();if(!support.noCloneEvent&&dest[jQuery.expando]){data=jQuery._data(dest);for(e in data.events){jQuery.removeEvent(dest,e,data.handle);}dest.removeAttribute(jQuery.expando);}if(nodeName==="script"&&dest.text!==src.text){disableScript(dest).text=src.text;restoreScript(dest);}else{if(nodeName==="object"){if(dest.parentNode){dest.outerHTML=src.outerHTML;}if(support.html5Clone&&(src.innerHTML&&!jQuery.trim(dest.innerHTML))){dest.innerHTML=src.innerHTML;}}else{if(nodeName==="input"&&rcheckableType.test(src.type)){dest.defaultChecked=dest.checked=src.checked;if(dest.value!==src.value){dest.value=src.value;}}else{if(nodeName==="option"){dest.defaultSelected=dest.selected=src.defaultSelected;}else{if(nodeName==="input"||nodeName==="textarea"){dest.defaultValue=src.defaultValue;}}}}}}jQuery.extend({clone:function(elem,dataAndEvents,deepDataAndEvents){var destElements,node,clone,i,srcElements,inPage=jQuery.contains(elem.ownerDocument,elem);if(support.html5Clone||jQuery.isXMLDoc(elem)||!rnoshimcache.test("<"+elem.nodeName+">")){clone=elem.cloneNode(true);}else{fragmentDiv.innerHTML=elem.outerHTML;fragmentDiv.removeChild(clone=fragmentDiv.firstChild);}if((!support.noCloneEvent||!support.noCloneChecked)&&(elem.nodeType===1||elem.nodeType===11)&&!jQuery.isXMLDoc(elem)){destElements=getAll(clone);srcElements=getAll(elem);for(i=0;(node=srcElements[i])!=null;++i){if(destElements[i]){fixCloneNodeIssues(node,destElements[i]);}}}if(dataAndEvents){if(deepDataAndEvents){srcElements=srcElements||getAll(elem);destElements=destElements||getAll(clone);for(i=0;(node=srcElements[i])!=null;i++){cloneCopyEvent(node,destElements[i]);}}else{cloneCopyEvent(elem,clone);}}destElements=getAll(clone,"script");if(destElements.length>0){setGlobalEval(destElements,!inPage&&getAll(elem,"script"));}destElements=srcElements=node=null;return clone;},buildFragment:function(elems,context,scripts,selection){var j,elem,contains,tmp,tag,tbody,wrap,l=elems.length,safe=createSafeFragment(context),nodes=[],i=0;for(;i<l;i++){elem=elems[i];if(elem||elem===0){if(jQuery.type(elem)==="object"){jQuery.merge(nodes,elem.nodeType?[elem]:elem);}else{if(!rhtml.test(elem)){nodes.push(context.createTextNode(elem));}else{tmp=tmp||safe.appendChild(context.createElement("div"));tag=(rtagName.exec(elem)||["",""])[1].toLowerCase();wrap=wrapMap[tag]||wrapMap._default;tmp.innerHTML=wrap[1]+elem.replace(rxhtmlTag,"<$1></$2>")+wrap[2];j=wrap[0];while(j--){tmp=tmp.lastChild;}if(!support.leadingWhitespace&&rleadingWhitespace.test(elem)){nodes.push(context.createTextNode(rleadingWhitespace.exec(elem)[0]));}if(!support.tbody){elem=tag==="table"&&!rtbody.test(elem)?tmp.firstChild:wrap[1]==="<table>"&&!rtbody.test(elem)?tmp:0;j=elem&&elem.childNodes.length;while(j--){if(jQuery.nodeName((tbody=elem.childNodes[j]),"tbody")&&!tbody.childNodes.length){elem.removeChild(tbody);}}}jQuery.merge(nodes,tmp.childNodes);tmp.textContent="";while(tmp.firstChild){tmp.removeChild(tmp.firstChild);}tmp=safe.lastChild;}}}}if(tmp){safe.removeChild(tmp);}if(!support.appendChecked){jQuery.grep(getAll(nodes,"input"),fixDefaultChecked);}i=0;while((elem=nodes[i++])){if(selection&&jQuery.inArray(elem,selection)!==-1){continue;}contains=jQuery.contains(elem.ownerDocument,elem);tmp=getAll(safe.appendChild(elem),"script");if(contains){setGlobalEval(tmp);}if(scripts){j=0;while((elem=tmp[j++])){if(rscriptType.test(elem.type||"")){scripts.push(elem);}}}}tmp=null;return safe;},cleanData:function(elems,acceptData){var elem,type,id,data,i=0,internalKey=jQuery.expando,cache=jQuery.cache,deleteExpando=support.deleteExpando,special=jQuery.event.special;for(;(elem=elems[i])!=null;i++){if(acceptData||jQuery.acceptData(elem)){id=elem[internalKey];data=id&&cache[id];if(data){if(data.events){for(type in data.events){if(special[type]){jQuery.event.remove(elem,type);}else{jQuery.removeEvent(elem,type,data.handle);}}}if(cache[id]){delete cache[id];if(deleteExpando){delete elem[internalKey];}else{if(typeof elem.removeAttribute!==strundefined){elem.removeAttribute(internalKey);}else{elem[internalKey]=null;}}deletedIds.push(id);}}}}}});jQuery.fn.extend({text:function(value){return access(this,function(value){return value===undefined?jQuery.text(this):this.empty().append((this[0]&&this[0].ownerDocument||document).createTextNode(value));},null,value,arguments.length);},append:function(){return this.domManip(arguments,function(elem){if(this.nodeType===1||this.nodeType===11||this.nodeType===9){var target=manipulationTarget(this,elem);target.appendChild(elem);}});},prepend:function(){return this.domManip(arguments,function(elem){if(this.nodeType===1||this.nodeType===11||this.nodeType===9){var target=manipulationTarget(this,elem);target.insertBefore(elem,target.firstChild);}});},before:function(){return this.domManip(arguments,function(elem){if(this.parentNode){this.parentNode.insertBefore(elem,this);}});},after:function(){return this.domManip(arguments,function(elem){if(this.parentNode){this.parentNode.insertBefore(elem,this.nextSibling);}});},remove:function(selector,keepData){var elem,elems=selector?jQuery.filter(selector,this):this,i=0;for(;(elem=elems[i])!=null;i++){if(!keepData&&elem.nodeType===1){jQuery.cleanData(getAll(elem));}if(elem.parentNode){if(keepData&&jQuery.contains(elem.ownerDocument,elem)){setGlobalEval(getAll(elem,"script"));}elem.parentNode.removeChild(elem);}}return this;},empty:function(){var elem,i=0;for(;(elem=this[i])!=null;i++){if(elem.nodeType===1){jQuery.cleanData(getAll(elem,false));}while(elem.firstChild){elem.removeChild(elem.firstChild);}if(elem.options&&jQuery.nodeName(elem,"select")){elem.options.length=0;}}return this;},clone:function(dataAndEvents,deepDataAndEvents){dataAndEvents=dataAndEvents==null?false:dataAndEvents;deepDataAndEvents=deepDataAndEvents==null?dataAndEvents:deepDataAndEvents;return this.map(function(){return jQuery.clone(this,dataAndEvents,deepDataAndEvents);});},html:function(value){return access(this,function(value){var elem=this[0]||{},i=0,l=this.length;if(value===undefined){return elem.nodeType===1?elem.innerHTML.replace(rinlinejQuery,""):undefined;}if(typeof value==="string"&&!rnoInnerhtml.test(value)&&(support.htmlSerialize||!rnoshimcache.test(value))&&(support.leadingWhitespace||!rleadingWhitespace.test(value))&&!wrapMap[(rtagName.exec(value)||["",""])[1].toLowerCase()]){value=value.replace(rxhtmlTag,"<$1></$2>");try{for(;i<l;i++){elem=this[i]||{};if(elem.nodeType===1){jQuery.cleanData(getAll(elem,false));elem.innerHTML=value;}}elem=0;}catch(e){}}if(elem){this.empty().append(value);}},null,value,arguments.length);},replaceWith:function(){var arg=arguments[0];this.domManip(arguments,function(elem){arg=this.parentNode;jQuery.cleanData(getAll(this));if(arg){arg.replaceChild(elem,this);}});return arg&&(arg.length||arg.nodeType)?this:this.remove();},detach:function(selector){return this.remove(selector,true);},domManip:function(args,callback){args=concat.apply([],args);var first,node,hasScripts,scripts,doc,fragment,i=0,l=this.length,set=this,iNoClone=l-1,value=args[0],isFunction=jQuery.isFunction(value);if(isFunction||(l>1&&typeof value==="string"&&!support.checkClone&&rchecked.test(value))){return this.each(function(index){var self=set.eq(index);if(isFunction){args[0]=value.call(this,index,self.html());}self.domManip(args,callback);});}if(l){fragment=jQuery.buildFragment(args,this[0].ownerDocument,false,this);first=fragment.firstChild;if(fragment.childNodes.length===1){fragment=first;}if(first){scripts=jQuery.map(getAll(fragment,"script"),disableScript);hasScripts=scripts.length;for(;i<l;i++){node=fragment;if(i!==iNoClone){node=jQuery.clone(node,true,true);if(hasScripts){jQuery.merge(scripts,getAll(node,"script"));}}callback.call(this[i],node,i);}if(hasScripts){doc=scripts[scripts.length-1].ownerDocument;jQuery.map(scripts,restoreScript);for(i=0;i<hasScripts;i++){node=scripts[i];if(rscriptType.test(node.type||"")&&!jQuery._data(node,"globalEval")&&jQuery.contains(doc,node)){if(node.src){if(jQuery._evalUrl){jQuery._evalUrl(node.src);}}else{jQuery.globalEval((node.text||node.textContent||node.innerHTML||"").replace(rcleanScript,""));}}}}fragment=first=null;}}return this;}});jQuery.each({appendTo:"append",prependTo:"prepend",insertBefore:"before",insertAfter:"after",replaceAll:"replaceWith"},function(name,original){jQuery.fn[name]=function(selector){var elems,i=0,ret=[],insert=jQuery(selector),last=insert.length-1;for(;i<=last;i++){elems=i===last?this:this.clone(true);jQuery(insert[i])[original](elems);push.apply(ret,elems.get());}return this.pushStack(ret);};});var iframe,elemdisplay={};function actualDisplay(name,doc){var style,elem=jQuery(doc.createElement(name)).appendTo(doc.body),display=window.getDefaultComputedStyle&&(style=window.getDefaultComputedStyle(elem[0]))?style.display:jQuery.css(elem[0],"display");elem.detach();return display;}function defaultDisplay(nodeName){var doc=document,display=elemdisplay[nodeName];if(!display){display=actualDisplay(nodeName,doc);if(display==="none"||!display){iframe=(iframe||jQuery("<iframe frameborder='0' width='0' height='0'/>")).appendTo(doc.documentElement);doc=(iframe[0].contentWindow||iframe[0].contentDocument).document;doc.write();doc.close();display=actualDisplay(nodeName,doc);iframe.detach();}elemdisplay[nodeName]=display;}return display;}(function(){var shrinkWrapBlocksVal;support.shrinkWrapBlocks=function(){if(shrinkWrapBlocksVal!=null){return shrinkWrapBlocksVal;}shrinkWrapBlocksVal=false;var div,body,container;body=document.getElementsByTagName("body")[0];if(!body||!body.style){return;}div=document.createElement("div");container=document.createElement("div");container.style.cssText="position:absolute;border:0;width:0;height:0;top:0;left:-9999px";body.appendChild(container).appendChild(div);if(typeof div.style.zoom!==strundefined){div.style.cssText="-webkit-box-sizing:content-box;-moz-box-sizing:content-box;"+"box-sizing:content-box;display:block;margin:0;border:0;"+"padding:1px;width:1px;zoom:1";div.appendChild(document.createElement("div")).style.width="5px";shrinkWrapBlocksVal=div.offsetWidth!==3;}body.removeChild(container);return shrinkWrapBlocksVal;};})();var rmargin=(/^margin/);var rnumnonpx=new RegExp("^("+pnum+")(?!px)[a-z%]+$","i");var getStyles,curCSS,rposition=/^(top|right|bottom|left)$/;if(window.getComputedStyle){getStyles=function(elem){return elem.ownerDocument.defaultView.getComputedStyle(elem,null);};curCSS=function(elem,name,computed){var width,minWidth,maxWidth,ret,style=elem.style;computed=computed||getStyles(elem);ret=computed?computed.getPropertyValue(name)||computed[name]:undefined;if(computed){if(ret===""&&!jQuery.contains(elem.ownerDocument,elem)){ret=jQuery.style(elem,name);}if(rnumnonpx.test(ret)&&rmargin.test(name)){width=style.width;minWidth=style.minWidth;maxWidth=style.maxWidth;style.minWidth=style.maxWidth=style.width=ret;ret=computed.width;style.width=width;style.minWidth=minWidth;style.maxWidth=maxWidth;}}return ret===undefined?ret:ret+"";};}else{if(document.documentElement.currentStyle){getStyles=function(elem){return elem.currentStyle;};curCSS=function(elem,name,computed){var left,rs,rsLeft,ret,style=elem.style;computed=computed||getStyles(elem);ret=computed?computed[name]:undefined;if(ret==null&&style&&style[name]){ret=style[name];}if(rnumnonpx.test(ret)&&!rposition.test(name)){left=style.left;rs=elem.runtimeStyle;rsLeft=rs&&rs.left;if(rsLeft){rs.left=elem.currentStyle.left;}style.left=name==="fontSize"?"1em":ret;ret=style.pixelLeft+"px";style.left=left;if(rsLeft){rs.left=rsLeft;}}return ret===undefined?ret:ret+""||"auto";};}}function addGetHookIf(conditionFn,hookFn){return{get:function(){var condition=conditionFn();if(condition==null){return;}if(condition){delete this.get;return;}return(this.get=hookFn).apply(this,arguments);}};}(function(){var div,style,a,pixelPositionVal,boxSizingReliableVal,reliableHiddenOffsetsVal,reliableMarginRightVal;div=document.createElement("div");div.innerHTML="  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";a=div.getElementsByTagName("a")[0];style=a&&a.style;if(!style){return;}style.cssText="float:left;opacity:.5";support.opacity=style.opacity==="0.5";support.cssFloat=!!style.cssFloat;div.style.backgroundClip="content-box";div.cloneNode(true).style.backgroundClip="";support.clearCloneStyle=div.style.backgroundClip==="content-box";support.boxSizing=style.boxSizing===""||style.MozBoxSizing===""||style.WebkitBoxSizing==="";jQuery.extend(support,{reliableHiddenOffsets:function(){if(reliableHiddenOffsetsVal==null){computeStyleTests();}return reliableHiddenOffsetsVal;},boxSizingReliable:function(){if(boxSizingReliableVal==null){computeStyleTests();}return boxSizingReliableVal;},pixelPosition:function(){if(pixelPositionVal==null){computeStyleTests();}return pixelPositionVal;},reliableMarginRight:function(){if(reliableMarginRightVal==null){computeStyleTests();}return reliableMarginRightVal;}});function computeStyleTests(){var div,body,container,contents;body=document.getElementsByTagName("body")[0];if(!body||!body.style){return;}div=document.createElement("div");container=document.createElement("div");container.style.cssText="position:absolute;border:0;width:0;height:0;top:0;left:-9999px";body.appendChild(container).appendChild(div);div.style.cssText="-webkit-box-sizing:border-box;-moz-box-sizing:border-box;"+"box-sizing:border-box;display:block;margin-top:1%;top:1%;"+"border:1px;padding:1px;width:4px;position:absolute";pixelPositionVal=boxSizingReliableVal=false;reliableMarginRightVal=true;if(window.getComputedStyle){pixelPositionVal=(window.getComputedStyle(div,null)||{}).top!=="1%";boxSizingReliableVal=(window.getComputedStyle(div,null)||{width:"4px"}).width==="4px";contents=div.appendChild(document.createElement("div"));contents.style.cssText=div.style.cssText="-webkit-box-sizing:content-box;-moz-box-sizing:content-box;"+"box-sizing:content-box;display:block;margin:0;border:0;padding:0";contents.style.marginRight=contents.style.width="0";div.style.width="1px";reliableMarginRightVal=!parseFloat((window.getComputedStyle(contents,null)||{}).marginRight);}div.innerHTML="<table><tr><td></td><td>t</td></tr></table>";contents=div.getElementsByTagName("td");contents[0].style.cssText="margin:0;border:0;padding:0;display:none";reliableHiddenOffsetsVal=contents[0].offsetHeight===0;if(reliableHiddenOffsetsVal){contents[0].style.display="";contents[1].style.display="none";reliableHiddenOffsetsVal=contents[0].offsetHeight===0;}body.removeChild(container);}})();jQuery.swap=function(elem,options,callback,args){var ret,name,old={};for(name in options){old[name]=elem.style[name];elem.style[name]=options[name];}ret=callback.apply(elem,args||[]);for(name in options){elem.style[name]=old[name];}return ret;};var ralpha=/alpha\([^)]*\)/i,ropacity=/opacity\s*=\s*([^)]*)/,rdisplayswap=/^(none|table(?!-c[ea]).+)/,rnumsplit=new RegExp("^("+pnum+")(.*)$","i"),rrelNum=new RegExp("^([+-])=("+pnum+")","i"),cssShow={position:"absolute",visibility:"hidden",display:"block"},cssNormalTransform={letterSpacing:"0",fontWeight:"400"},cssPrefixes=["Webkit","O","Moz","ms"];function vendorPropName(style,name){if(name in style){return name;}var capName=name.charAt(0).toUpperCase()+name.slice(1),origName=name,i=cssPrefixes.length;while(i--){name=cssPrefixes[i]+capName;if(name in style){return name;}}return origName;}function showHide(elements,show){var display,elem,hidden,values=[],index=0,length=elements.length;for(;index<length;index++){elem=elements[index];if(!elem.style){continue;}values[index]=jQuery._data(elem,"olddisplay");display=elem.style.display;if(show){if(!values[index]&&display==="none"){elem.style.display="";}if(elem.style.display===""&&isHidden(elem)){values[index]=jQuery._data(elem,"olddisplay",defaultDisplay(elem.nodeName));}}else{hidden=isHidden(elem);if(display&&display!=="none"||!hidden){jQuery._data(elem,"olddisplay",hidden?display:jQuery.css(elem,"display"));}}}for(index=0;index<length;index++){elem=elements[index];if(!elem.style){continue;}if(!show||elem.style.display==="none"||elem.style.display===""){elem.style.display=show?values[index]||"":"none";}}return elements;}function setPositiveNumber(elem,value,subtract){var matches=rnumsplit.exec(value);return matches?Math.max(0,matches[1]-(subtract||0))+(matches[2]||"px"):value;}function augmentWidthOrHeight(elem,name,extra,isBorderBox,styles){var i=extra===(isBorderBox?"border":"content")?4:name==="width"?1:0,val=0;for(;i<4;i+=2){if(extra==="margin"){val+=jQuery.css(elem,extra+cssExpand[i],true,styles);}if(isBorderBox){if(extra==="content"){val-=jQuery.css(elem,"padding"+cssExpand[i],true,styles);}if(extra!=="margin"){val-=jQuery.css(elem,"border"+cssExpand[i]+"Width",true,styles);}}else{val+=jQuery.css(elem,"padding"+cssExpand[i],true,styles);if(extra!=="padding"){val+=jQuery.css(elem,"border"+cssExpand[i]+"Width",true,styles);}}}return val;}function getWidthOrHeight(elem,name,extra){var valueIsBorderBox=true,val=name==="width"?elem.offsetWidth:elem.offsetHeight,styles=getStyles(elem),isBorderBox=support.boxSizing&&jQuery.css(elem,"boxSizing",false,styles)==="border-box";if(val<=0||val==null){val=curCSS(elem,name,styles);if(val<0||val==null){val=elem.style[name];}if(rnumnonpx.test(val)){return val;}valueIsBorderBox=isBorderBox&&(support.boxSizingReliable()||val===elem.style[name]);val=parseFloat(val)||0;}return(val+augmentWidthOrHeight(elem,name,extra||(isBorderBox?"border":"content"),valueIsBorderBox,styles))+"px";}jQuery.extend({cssHooks:{opacity:{get:function(elem,computed){if(computed){var ret=curCSS(elem,"opacity");return ret===""?"1":ret;}}}},cssNumber:{"columnCount":true,"fillOpacity":true,"flexGrow":true,"flexShrink":true,"fontWeight":true,"lineHeight":true,"opacity":true,"order":true,"orphans":true,"widows":true,"zIndex":true,"zoom":true},cssProps:{"float":support.cssFloat?"cssFloat":"styleFloat"},style:function(elem,name,value,extra){if(!elem||elem.nodeType===3||elem.nodeType===8||!elem.style){return;}var ret,type,hooks,origName=jQuery.camelCase(name),style=elem.style;name=jQuery.cssProps[origName]||(jQuery.cssProps[origName]=vendorPropName(style,origName));hooks=jQuery.cssHooks[name]||jQuery.cssHooks[origName];if(value!==undefined){type=typeof value;if(type==="string"&&(ret=rrelNum.exec(value))){value=(ret[1]+1)*ret[2]+parseFloat(jQuery.css(elem,name));type="number";}if(value==null||value!==value){return;}if(type==="number"&&!jQuery.cssNumber[origName]){value+="px";}if(!support.clearCloneStyle&&value===""&&name.indexOf("background")===0){style[name]="inherit";}if(!hooks||!("set" in hooks)||(value=hooks.set(elem,value,extra))!==undefined){try{style[name]=value;}catch(e){}}}else{if(hooks&&"get" in hooks&&(ret=hooks.get(elem,false,extra))!==undefined){return ret;}return style[name];}},css:function(elem,name,extra,styles){var num,val,hooks,origName=jQuery.camelCase(name);name=jQuery.cssProps[origName]||(jQuery.cssProps[origName]=vendorPropName(elem.style,origName));hooks=jQuery.cssHooks[name]||jQuery.cssHooks[origName];if(hooks&&"get" in hooks){val=hooks.get(elem,true,extra);}if(val===undefined){val=curCSS(elem,name,styles);}if(val==="normal"&&name in cssNormalTransform){val=cssNormalTransform[name];}if(extra===""||extra){num=parseFloat(val);return extra===true||jQuery.isNumeric(num)?num||0:val;}return val;}});jQuery.each(["height","width"],function(i,name){jQuery.cssHooks[name]={get:function(elem,computed,extra){if(computed){return rdisplayswap.test(jQuery.css(elem,"display"))&&elem.offsetWidth===0?jQuery.swap(elem,cssShow,function(){return getWidthOrHeight(elem,name,extra);}):getWidthOrHeight(elem,name,extra);}},set:function(elem,value,extra){var styles=extra&&getStyles(elem);return setPositiveNumber(elem,value,extra?augmentWidthOrHeight(elem,name,extra,support.boxSizing&&jQuery.css(elem,"boxSizing",false,styles)==="border-box",styles):0);}};});if(!support.opacity){jQuery.cssHooks.opacity={get:function(elem,computed){return ropacity.test((computed&&elem.currentStyle?elem.currentStyle.filter:elem.style.filter)||"")?(0.01*parseFloat(RegExp.$1))+"":computed?"1":"";},set:function(elem,value){var style=elem.style,currentStyle=elem.currentStyle,opacity=jQuery.isNumeric(value)?"alpha(opacity="+value*100+")":"",filter=currentStyle&&currentStyle.filter||style.filter||"";style.zoom=1;if((value>=1||value==="")&&jQuery.trim(filter.replace(ralpha,""))===""&&style.removeAttribute){style.removeAttribute("filter");if(value===""||currentStyle&&!currentStyle.filter){return;}}style.filter=ralpha.test(filter)?filter.replace(ralpha,opacity):filter+" "+opacity;}};}jQuery.cssHooks.marginRight=addGetHookIf(support.reliableMarginRight,function(elem,computed){if(computed){return jQuery.swap(elem,{"display":"inline-block"},curCSS,[elem,"marginRight"]);}});jQuery.each({margin:"",padding:"",border:"Width"},function(prefix,suffix){jQuery.cssHooks[prefix+suffix]={expand:function(value){var i=0,expanded={},parts=typeof value==="string"?value.split(" "):[value];for(;i<4;i++){expanded[prefix+cssExpand[i]+suffix]=parts[i]||parts[i-2]||parts[0];}return expanded;}};if(!rmargin.test(prefix)){jQuery.cssHooks[prefix+suffix].set=setPositiveNumber;}});jQuery.fn.extend({css:function(name,value){return access(this,function(elem,name,value){var styles,len,map={},i=0;if(jQuery.isArray(name)){styles=getStyles(elem);len=name.length;for(;i<len;i++){map[name[i]]=jQuery.css(elem,name[i],false,styles);}return map;}return value!==undefined?jQuery.style(elem,name,value):jQuery.css(elem,name);},name,value,arguments.length>1);},show:function(){return showHide(this,true);},hide:function(){return showHide(this);},toggle:function(state){if(typeof state==="boolean"){return state?this.show():this.hide();}return this.each(function(){if(isHidden(this)){jQuery(this).show();}else{jQuery(this).hide();}});}});function Tween(elem,options,prop,end,easing){return new Tween.prototype.init(elem,options,prop,end,easing);}jQuery.Tween=Tween;Tween.prototype={constructor:Tween,init:function(elem,options,prop,end,easing,unit){this.elem=elem;this.prop=prop;this.easing=easing||"swing";this.options=options;this.start=this.now=this.cur();this.end=end;this.unit=unit||(jQuery.cssNumber[prop]?"":"px");},cur:function(){var hooks=Tween.propHooks[this.prop];return hooks&&hooks.get?hooks.get(this):Tween.propHooks._default.get(this);},run:function(percent){var eased,hooks=Tween.propHooks[this.prop];if(this.options.duration){this.pos=eased=jQuery.easing[this.easing](percent,this.options.duration*percent,0,1,this.options.duration);}else{this.pos=eased=percent;}this.now=(this.end-this.start)*eased+this.start;if(this.options.step){this.options.step.call(this.elem,this.now,this);}if(hooks&&hooks.set){hooks.set(this);}else{Tween.propHooks._default.set(this);}return this;}};Tween.prototype.init.prototype=Tween.prototype;Tween.propHooks={_default:{get:function(tween){var result;if(tween.elem[tween.prop]!=null&&(!tween.elem.style||tween.elem.style[tween.prop]==null)){return tween.elem[tween.prop];}result=jQuery.css(tween.elem,tween.prop,"");return !result||result==="auto"?0:result;},set:function(tween){if(jQuery.fx.step[tween.prop]){jQuery.fx.step[tween.prop](tween);}else{if(tween.elem.style&&(tween.elem.style[jQuery.cssProps[tween.prop]]!=null||jQuery.cssHooks[tween.prop])){jQuery.style(tween.elem,tween.prop,tween.now+tween.unit);}else{tween.elem[tween.prop]=tween.now;}}}}};Tween.propHooks.scrollTop=Tween.propHooks.scrollLeft={set:function(tween){if(tween.elem.nodeType&&tween.elem.parentNode){tween.elem[tween.prop]=tween.now;}}};jQuery.easing={linear:function(p){return p;},swing:function(p){return 0.5-Math.cos(p*Math.PI)/2;}};jQuery.fx=Tween.prototype.init;jQuery.fx.step={};var fxNow,timerId,rfxtypes=/^(?:toggle|show|hide)$/,rfxnum=new RegExp("^(?:([+-])=|)("+pnum+")([a-z%]*)$","i"),rrun=/queueHooks$/,animationPrefilters=[defaultPrefilter],tweeners={"*":[function(prop,value){var tween=this.createTween(prop,value),target=tween.cur(),parts=rfxnum.exec(value),unit=parts&&parts[3]||(jQuery.cssNumber[prop]?"":"px"),start=(jQuery.cssNumber[prop]||unit!=="px"&&+target)&&rfxnum.exec(jQuery.css(tween.elem,prop)),scale=1,maxIterations=20;if(start&&start[3]!==unit){unit=unit||start[3];parts=parts||[];start=+target||1;do{scale=scale||".5";start=start/scale;jQuery.style(tween.elem,prop,start+unit);}while(scale!==(scale=tween.cur()/target)&&scale!==1&&--maxIterations);}if(parts){start=tween.start=+start||+target||0;tween.unit=unit;tween.end=parts[1]?start+(parts[1]+1)*parts[2]:+parts[2];}return tween;}]};function createFxNow(){setTimeout(function(){fxNow=undefined;});return(fxNow=jQuery.now());}function genFx(type,includeWidth){var which,attrs={height:type},i=0;includeWidth=includeWidth?1:0;for(;i<4;i+=2-includeWidth){which=cssExpand[i];attrs["margin"+which]=attrs["padding"+which]=type;}if(includeWidth){attrs.opacity=attrs.width=type;}return attrs;}function createTween(value,prop,animation){var tween,collection=(tweeners[prop]||[]).concat(tweeners["*"]),index=0,length=collection.length;for(;index<length;index++){if((tween=collection[index].call(animation,prop,value))){return tween;}}}function defaultPrefilter(elem,props,opts){var prop,value,toggle,tween,hooks,oldfire,display,checkDisplay,anim=this,orig={},style=elem.style,hidden=elem.nodeType&&isHidden(elem),dataShow=jQuery._data(elem,"fxshow");if(!opts.queue){hooks=jQuery._queueHooks(elem,"fx");if(hooks.unqueued==null){hooks.unqueued=0;oldfire=hooks.empty.fire;hooks.empty.fire=function(){if(!hooks.unqueued){oldfire();}};}hooks.unqueued++;anim.always(function(){anim.always(function(){hooks.unqueued--;if(!jQuery.queue(elem,"fx").length){hooks.empty.fire();}});});}if(elem.nodeType===1&&("height" in props||"width" in props)){opts.overflow=[style.overflow,style.overflowX,style.overflowY];display=jQuery.css(elem,"display");checkDisplay=display==="none"?jQuery._data(elem,"olddisplay")||defaultDisplay(elem.nodeName):display;if(checkDisplay==="inline"&&jQuery.css(elem,"float")==="none"){if(!support.inlineBlockNeedsLayout||defaultDisplay(elem.nodeName)==="inline"){style.display="inline-block";}else{style.zoom=1;}}}if(opts.overflow){style.overflow="hidden";if(!support.shrinkWrapBlocks()){anim.always(function(){style.overflow=opts.overflow[0];style.overflowX=opts.overflow[1];style.overflowY=opts.overflow[2];});}}for(prop in props){value=props[prop];if(rfxtypes.exec(value)){delete props[prop];toggle=toggle||value==="toggle";if(value===(hidden?"hide":"show")){if(value==="show"&&dataShow&&dataShow[prop]!==undefined){hidden=true;}else{continue;}}orig[prop]=dataShow&&dataShow[prop]||jQuery.style(elem,prop);}else{display=undefined;}}if(!jQuery.isEmptyObject(orig)){if(dataShow){if("hidden" in dataShow){hidden=dataShow.hidden;}}else{dataShow=jQuery._data(elem,"fxshow",{});}if(toggle){dataShow.hidden=!hidden;}if(hidden){jQuery(elem).show();}else{anim.done(function(){jQuery(elem).hide();});}anim.done(function(){var prop;jQuery._removeData(elem,"fxshow");for(prop in orig){jQuery.style(elem,prop,orig[prop]);}});for(prop in orig){tween=createTween(hidden?dataShow[prop]:0,prop,anim);if(!(prop in dataShow)){dataShow[prop]=tween.start;if(hidden){tween.end=tween.start;tween.start=prop==="width"||prop==="height"?1:0;}}}}else{if((display==="none"?defaultDisplay(elem.nodeName):display)==="inline"){style.display=display;}}}function propFilter(props,specialEasing){var index,name,easing,value,hooks;for(index in props){name=jQuery.camelCase(index);easing=specialEasing[name];value=props[index];if(jQuery.isArray(value)){easing=value[1];value=props[index]=value[0];}if(index!==name){props[name]=value;delete props[index];}hooks=jQuery.cssHooks[name];if(hooks&&"expand" in hooks){value=hooks.expand(value);delete props[name];for(index in value){if(!(index in props)){props[index]=value[index];specialEasing[index]=easing;}}}else{specialEasing[name]=easing;}}}function Animation(elem,properties,options){var result,stopped,index=0,length=animationPrefilters.length,deferred=jQuery.Deferred().always(function(){delete tick.elem;}),tick=function(){if(stopped){return false;}var currentTime=fxNow||createFxNow(),remaining=Math.max(0,animation.startTime+animation.duration-currentTime),temp=remaining/animation.duration||0,percent=1-temp,index=0,length=animation.tweens.length;for(;index<length;index++){animation.tweens[index].run(percent);}deferred.notifyWith(elem,[animation,percent,remaining]);if(percent<1&&length){return remaining;}else{deferred.resolveWith(elem,[animation]);return false;}},animation=deferred.promise({elem:elem,props:jQuery.extend({},properties),opts:jQuery.extend(true,{specialEasing:{}},options),originalProperties:properties,originalOptions:options,startTime:fxNow||createFxNow(),duration:options.duration,tweens:[],createTween:function(prop,end){var tween=jQuery.Tween(elem,animation.opts,prop,end,animation.opts.specialEasing[prop]||animation.opts.easing);animation.tweens.push(tween);return tween;},stop:function(gotoEnd){var index=0,length=gotoEnd?animation.tweens.length:0;if(stopped){return this;}stopped=true;for(;index<length;index++){animation.tweens[index].run(1);}if(gotoEnd){deferred.resolveWith(elem,[animation,gotoEnd]);}else{deferred.rejectWith(elem,[animation,gotoEnd]);}return this;}}),props=animation.props;propFilter(props,animation.opts.specialEasing);for(;index<length;index++){result=animationPrefilters[index].call(animation,elem,props,animation.opts);if(result){return result;}}jQuery.map(props,createTween,animation);if(jQuery.isFunction(animation.opts.start)){animation.opts.start.call(elem,animation);}jQuery.fx.timer(jQuery.extend(tick,{elem:elem,anim:animation,queue:animation.opts.queue}));return animation.progress(animation.opts.progress).done(animation.opts.done,animation.opts.complete).fail(animation.opts.fail).always(animation.opts.always);}jQuery.Animation=jQuery.extend(Animation,{tweener:function(props,callback){if(jQuery.isFunction(props)){callback=props;props=["*"];}else{props=props.split(" ");}var prop,index=0,length=props.length;for(;index<length;index++){prop=props[index];tweeners[prop]=tweeners[prop]||[];tweeners[prop].unshift(callback);}},prefilter:function(callback,prepend){if(prepend){animationPrefilters.unshift(callback);}else{animationPrefilters.push(callback);}}});jQuery.speed=function(speed,easing,fn){var opt=speed&&typeof speed==="object"?jQuery.extend({},speed):{complete:fn||!fn&&easing||jQuery.isFunction(speed)&&speed,duration:speed,easing:fn&&easing||easing&&!jQuery.isFunction(easing)&&easing};opt.duration=jQuery.fx.off?0:typeof opt.duration==="number"?opt.duration:opt.duration in jQuery.fx.speeds?jQuery.fx.speeds[opt.duration]:jQuery.fx.speeds._default;if(opt.queue==null||opt.queue===true){opt.queue="fx";}opt.old=opt.complete;opt.complete=function(){if(jQuery.isFunction(opt.old)){opt.old.call(this);}if(opt.queue){jQuery.dequeue(this,opt.queue);}};return opt;};jQuery.fn.extend({fadeTo:function(speed,to,easing,callback){return this.filter(isHidden).css("opacity",0).show().end().animate({opacity:to},speed,easing,callback);},animate:function(prop,speed,easing,callback){var empty=jQuery.isEmptyObject(prop),optall=jQuery.speed(speed,easing,callback),doAnimation=function(){var anim=Animation(this,jQuery.extend({},prop),optall);if(empty||jQuery._data(this,"finish")){anim.stop(true);}};doAnimation.finish=doAnimation;return empty||optall.queue===false?this.each(doAnimation):this.queue(optall.queue,doAnimation);},stop:function(type,clearQueue,gotoEnd){var stopQueue=function(hooks){var stop=hooks.stop;delete hooks.stop;stop(gotoEnd);};if(typeof type!=="string"){gotoEnd=clearQueue;clearQueue=type;type=undefined;}if(clearQueue&&type!==false){this.queue(type||"fx",[]);}return this.each(function(){var dequeue=true,index=type!=null&&type+"queueHooks",timers=jQuery.timers,data=jQuery._data(this);if(index){if(data[index]&&data[index].stop){stopQueue(data[index]);}}else{for(index in data){if(data[index]&&data[index].stop&&rrun.test(index)){stopQueue(data[index]);}}}for(index=timers.length;index--;){if(timers[index].elem===this&&(type==null||timers[index].queue===type)){timers[index].anim.stop(gotoEnd);dequeue=false;timers.splice(index,1);}}if(dequeue||!gotoEnd){jQuery.dequeue(this,type);}});},finish:function(type){if(type!==false){type=type||"fx";}return this.each(function(){var index,data=jQuery._data(this),queue=data[type+"queue"],hooks=data[type+"queueHooks"],timers=jQuery.timers,length=queue?queue.length:0;data.finish=true;jQuery.queue(this,type,[]);if(hooks&&hooks.stop){hooks.stop.call(this,true);}for(index=timers.length;index--;){if(timers[index].elem===this&&timers[index].queue===type){timers[index].anim.stop(true);timers.splice(index,1);}}for(index=0;index<length;index++){if(queue[index]&&queue[index].finish){queue[index].finish.call(this);}}delete data.finish;});}});jQuery.each(["toggle","show","hide"],function(i,name){var cssFn=jQuery.fn[name];jQuery.fn[name]=function(speed,easing,callback){return speed==null||typeof speed==="boolean"?cssFn.apply(this,arguments):this.animate(genFx(name,true),speed,easing,callback);};});jQuery.each({slideDown:genFx("show"),slideUp:genFx("hide"),slideToggle:genFx("toggle"),fadeIn:{opacity:"show"},fadeOut:{opacity:"hide"},fadeToggle:{opacity:"toggle"}},function(name,props){jQuery.fn[name]=function(speed,easing,callback){return this.animate(props,speed,easing,callback);};});jQuery.timers=[];jQuery.fx.tick=function(){var timer,timers=jQuery.timers,i=0;fxNow=jQuery.now();for(;i<timers.length;i++){timer=timers[i];if(!timer()&&timers[i]===timer){timers.splice(i--,1);}}if(!timers.length){jQuery.fx.stop();}fxNow=undefined;};jQuery.fx.timer=function(timer){jQuery.timers.push(timer);if(timer()){jQuery.fx.start();}else{jQuery.timers.pop();}};jQuery.fx.interval=13;jQuery.fx.start=function(){if(!timerId){timerId=setInterval(jQuery.fx.tick,jQuery.fx.interval);}};jQuery.fx.stop=function(){clearInterval(timerId);timerId=null;};jQuery.fx.speeds={slow:600,fast:200,_default:400};jQuery.fn.delay=function(time,type){time=jQuery.fx?jQuery.fx.speeds[time]||time:time;type=type||"fx";return this.queue(type,function(next,hooks){var timeout=setTimeout(next,time);hooks.stop=function(){clearTimeout(timeout);};});};(function(){var input,div,select,a,opt;div=document.createElement("div");div.setAttribute("className","t");div.innerHTML="  <link/><table></table><a href='/a'>a</a><input type='checkbox'/>";a=div.getElementsByTagName("a")[0];select=document.createElement("select");opt=select.appendChild(document.createElement("option"));input=div.getElementsByTagName("input")[0];a.style.cssText="top:1px";support.getSetAttribute=div.className!=="t";support.style=/top/.test(a.getAttribute("style"));support.hrefNormalized=a.getAttribute("href")==="/a";support.checkOn=!!input.value;support.optSelected=opt.selected;support.enctype=!!document.createElement("form").enctype;select.disabled=true;support.optDisabled=!opt.disabled;input=document.createElement("input");input.setAttribute("value","");support.input=input.getAttribute("value")==="";input.value="t";input.setAttribute("type","radio");support.radioValue=input.value==="t";})();var rreturn=/\r/g;jQuery.fn.extend({val:function(value){var hooks,ret,isFunction,elem=this[0];if(!arguments.length){if(elem){hooks=jQuery.valHooks[elem.type]||jQuery.valHooks[elem.nodeName.toLowerCase()];if(hooks&&"get" in hooks&&(ret=hooks.get(elem,"value"))!==undefined){return ret;}ret=elem.value;return typeof ret==="string"?ret.replace(rreturn,""):ret==null?"":ret;}return;}isFunction=jQuery.isFunction(value);return this.each(function(i){var val;if(this.nodeType!==1){return;}if(isFunction){val=value.call(this,i,jQuery(this).val());}else{val=value;}if(val==null){val="";}else{if(typeof val==="number"){val+="";}else{if(jQuery.isArray(val)){val=jQuery.map(val,function(value){return value==null?"":value+"";});}}}hooks=jQuery.valHooks[this.type]||jQuery.valHooks[this.nodeName.toLowerCase()];if(!hooks||!("set" in hooks)||hooks.set(this,val,"value")===undefined){this.value=val;}});}});jQuery.extend({valHooks:{option:{get:function(elem){var val=jQuery.find.attr(elem,"value");return val!=null?val:jQuery.trim(jQuery.text(elem));}},select:{get:function(elem){var value,option,options=elem.options,index=elem.selectedIndex,one=elem.type==="select-one"||index<0,values=one?null:[],max=one?index+1:options.length,i=index<0?max:one?index:0;for(;i<max;i++){option=options[i];if((option.selected||i===index)&&(support.optDisabled?!option.disabled:option.getAttribute("disabled")===null)&&(!option.parentNode.disabled||!jQuery.nodeName(option.parentNode,"optgroup"))){value=jQuery(option).val();if(one){return value;}values.push(value);}}return values;},set:function(elem,value){var optionSet,option,options=elem.options,values=jQuery.makeArray(value),i=options.length;while(i--){option=options[i];if(jQuery.inArray(jQuery.valHooks.option.get(option),values)>=0){try{option.selected=optionSet=true;}catch(_){option.scrollHeight;}}else{option.selected=false;}}if(!optionSet){elem.selectedIndex=-1;}return options;}}}});jQuery.each(["radio","checkbox"],function(){jQuery.valHooks[this]={set:function(elem,value){if(jQuery.isArray(value)){return(elem.checked=jQuery.inArray(jQuery(elem).val(),value)>=0);}}};if(!support.checkOn){jQuery.valHooks[this].get=function(elem){return elem.getAttribute("value")===null?"on":elem.value;};}});var nodeHook,boolHook,attrHandle=jQuery.expr.attrHandle,ruseDefault=/^(?:checked|selected)$/i,getSetAttribute=support.getSetAttribute,getSetInput=support.input;jQuery.fn.extend({attr:function(name,value){return access(this,jQuery.attr,name,value,arguments.length>1);},removeAttr:function(name){return this.each(function(){jQuery.removeAttr(this,name);});}});jQuery.extend({attr:function(elem,name,value){var hooks,ret,nType=elem.nodeType;if(!elem||nType===3||nType===8||nType===2){return;}if(typeof elem.getAttribute===strundefined){return jQuery.prop(elem,name,value);}if(nType!==1||!jQuery.isXMLDoc(elem)){name=name.toLowerCase();hooks=jQuery.attrHooks[name]||(jQuery.expr.match.bool.test(name)?boolHook:nodeHook);}if(value!==undefined){if(value===null){jQuery.removeAttr(elem,name);}else{if(hooks&&"set" in hooks&&(ret=hooks.set(elem,value,name))!==undefined){return ret;}else{elem.setAttribute(name,value+"");return value;}}}else{if(hooks&&"get" in hooks&&(ret=hooks.get(elem,name))!==null){return ret;}else{ret=jQuery.find.attr(elem,name);return ret==null?undefined:ret;}}},removeAttr:function(elem,value){var name,propName,i=0,attrNames=value&&value.match(rnotwhite);if(attrNames&&elem.nodeType===1){while((name=attrNames[i++])){propName=jQuery.propFix[name]||name;if(jQuery.expr.match.bool.test(name)){if(getSetInput&&getSetAttribute||!ruseDefault.test(name)){elem[propName]=false;}else{elem[jQuery.camelCase("default-"+name)]=elem[propName]=false;}}else{jQuery.attr(elem,name,"");}elem.removeAttribute(getSetAttribute?name:propName);}}},attrHooks:{type:{set:function(elem,value){if(!support.radioValue&&value==="radio"&&jQuery.nodeName(elem,"input")){var val=elem.value;elem.setAttribute("type",value);if(val){elem.value=val;}return value;}}}}});boolHook={set:function(elem,value,name){if(value===false){jQuery.removeAttr(elem,name);}else{if(getSetInput&&getSetAttribute||!ruseDefault.test(name)){elem.setAttribute(!getSetAttribute&&jQuery.propFix[name]||name,name);}else{elem[jQuery.camelCase("default-"+name)]=elem[name]=true;}}return name;}};jQuery.each(jQuery.expr.match.bool.source.match(/\w+/g),function(i,name){var getter=attrHandle[name]||jQuery.find.attr;attrHandle[name]=getSetInput&&getSetAttribute||!ruseDefault.test(name)?function(elem,name,isXML){var ret,handle;if(!isXML){handle=attrHandle[name];attrHandle[name]=ret;ret=getter(elem,name,isXML)!=null?name.toLowerCase():null;attrHandle[name]=handle;}return ret;}:function(elem,name,isXML){if(!isXML){return elem[jQuery.camelCase("default-"+name)]?name.toLowerCase():null;}};});if(!getSetInput||!getSetAttribute){jQuery.attrHooks.value={set:function(elem,value,name){if(jQuery.nodeName(elem,"input")){elem.defaultValue=value;}else{return nodeHook&&nodeHook.set(elem,value,name);}}};}if(!getSetAttribute){nodeHook={set:function(elem,value,name){var ret=elem.getAttributeNode(name);if(!ret){elem.setAttributeNode((ret=elem.ownerDocument.createAttribute(name)));}ret.value=value+="";if(name==="value"||value===elem.getAttribute(name)){return value;}}};attrHandle.id=attrHandle.name=attrHandle.coords=function(elem,name,isXML){var ret;if(!isXML){return(ret=elem.getAttributeNode(name))&&ret.value!==""?ret.value:null;}};jQuery.valHooks.button={get:function(elem,name){var ret=elem.getAttributeNode(name);if(ret&&ret.specified){return ret.value;}},set:nodeHook.set};jQuery.attrHooks.contenteditable={set:function(elem,value,name){nodeHook.set(elem,value===""?false:value,name);}};jQuery.each(["width","height"],function(i,name){jQuery.attrHooks[name]={set:function(elem,value){if(value===""){elem.setAttribute(name,"auto");return value;}}};});}if(!support.style){jQuery.attrHooks.style={get:function(elem){return elem.style.cssText||undefined;},set:function(elem,value){return(elem.style.cssText=value+"");}};}var rfocusable=/^(?:input|select|textarea|button|object)$/i,rclickable=/^(?:a|area)$/i;jQuery.fn.extend({prop:function(name,value){return access(this,jQuery.prop,name,value,arguments.length>1);},removeProp:function(name){name=jQuery.propFix[name]||name;return this.each(function(){try{this[name]=undefined;delete this[name];}catch(e){}});}});jQuery.extend({propFix:{"for":"htmlFor","class":"className"},prop:function(elem,name,value){var ret,hooks,notxml,nType=elem.nodeType;if(!elem||nType===3||nType===8||nType===2){return;}notxml=nType!==1||!jQuery.isXMLDoc(elem);if(notxml){name=jQuery.propFix[name]||name;hooks=jQuery.propHooks[name];}if(value!==undefined){return hooks&&"set" in hooks&&(ret=hooks.set(elem,value,name))!==undefined?ret:(elem[name]=value);}else{return hooks&&"get" in hooks&&(ret=hooks.get(elem,name))!==null?ret:elem[name];}},propHooks:{tabIndex:{get:function(elem){var tabindex=jQuery.find.attr(elem,"tabindex");return tabindex?parseInt(tabindex,10):rfocusable.test(elem.nodeName)||rclickable.test(elem.nodeName)&&elem.href?0:-1;}}}});if(!support.hrefNormalized){jQuery.each(["href","src"],function(i,name){jQuery.propHooks[name]={get:function(elem){return elem.getAttribute(name,4);}};});}if(!support.optSelected){jQuery.propHooks.selected={get:function(elem){var parent=elem.parentNode;if(parent){parent.selectedIndex;if(parent.parentNode){parent.parentNode.selectedIndex;}}return null;}};}jQuery.each(["tabIndex","readOnly","maxLength","cellSpacing","cellPadding","rowSpan","colSpan","useMap","frameBorder","contentEditable"],function(){jQuery.propFix[this.toLowerCase()]=this;});if(!support.enctype){jQuery.propFix.enctype="encoding";}var rclass=/[\t\r\n\f]/g;jQuery.fn.extend({addClass:function(value){var classes,elem,cur,clazz,j,finalValue,i=0,len=this.length,proceed=typeof value==="string"&&value;if(jQuery.isFunction(value)){return this.each(function(j){jQuery(this).addClass(value.call(this,j,this.className));});}if(proceed){classes=(value||"").match(rnotwhite)||[];for(;i<len;i++){elem=this[i];cur=elem.nodeType===1&&(elem.className?(" "+elem.className+" ").replace(rclass," "):" ");if(cur){j=0;while((clazz=classes[j++])){if(cur.indexOf(" "+clazz+" ")<0){cur+=clazz+" ";}}finalValue=jQuery.trim(cur);if(elem.className!==finalValue){elem.className=finalValue;}}}}return this;},removeClass:function(value){var classes,elem,cur,clazz,j,finalValue,i=0,len=this.length,proceed=arguments.length===0||typeof value==="string"&&value;if(jQuery.isFunction(value)){return this.each(function(j){jQuery(this).removeClass(value.call(this,j,this.className));});}if(proceed){classes=(value||"").match(rnotwhite)||[];for(;i<len;i++){elem=this[i];cur=elem.nodeType===1&&(elem.className?(" "+elem.className+" ").replace(rclass," "):"");if(cur){j=0;while((clazz=classes[j++])){while(cur.indexOf(" "+clazz+" ")>=0){cur=cur.replace(" "+clazz+" "," ");}}finalValue=value?jQuery.trim(cur):"";if(elem.className!==finalValue){elem.className=finalValue;}}}}return this;},toggleClass:function(value,stateVal){var type=typeof value;if(typeof stateVal==="boolean"&&type==="string"){return stateVal?this.addClass(value):this.removeClass(value);}if(jQuery.isFunction(value)){return this.each(function(i){jQuery(this).toggleClass(value.call(this,i,this.className,stateVal),stateVal);});}return this.each(function(){if(type==="string"){var className,i=0,self=jQuery(this),classNames=value.match(rnotwhite)||[];while((className=classNames[i++])){if(self.hasClass(className)){self.removeClass(className);}else{self.addClass(className);}}}else{if(type===strundefined||type==="boolean"){if(this.className){jQuery._data(this,"__className__",this.className);}this.className=this.className||value===false?"":jQuery._data(this,"__className__")||"";}}});},hasClass:function(selector){var className=" "+selector+" ",i=0,l=this.length;for(;i<l;i++){if(this[i].nodeType===1&&(" "+this[i].className+" ").replace(rclass," ").indexOf(className)>=0){return true;}}return false;}});jQuery.each(("blur focus focusin focusout load resize scroll unload click dblclick "+"mousedown mouseup mousemove mouseover mouseout mouseenter mouseleave "+"change select submit keydown keypress keyup error contextmenu").split(" "),function(i,name){jQuery.fn[name]=function(data,fn){return arguments.length>0?this.on(name,null,data,fn):this.trigger(name);};});jQuery.fn.extend({hover:function(fnOver,fnOut){return this.mouseenter(fnOver).mouseleave(fnOut||fnOver);},bind:function(types,data,fn){return this.on(types,null,data,fn);},unbind:function(types,fn){return this.off(types,null,fn);},delegate:function(selector,types,data,fn){return this.on(types,selector,data,fn);},undelegate:function(selector,types,fn){return arguments.length===1?this.off(selector,"**"):this.off(types,selector||"**",fn);}});var nonce=jQuery.now();var rquery=(/\?/);var rvalidtokens=/(,)|(\[|{)|(}|])|"(?:[^"\\\r\n]|\\["\\\/bfnrt]|\\u[\da-fA-F]{4})*"\s*:?|true|false|null|-?(?!0\d)\d+(?:\.\d+|)(?:[eE][+-]?\d+|)/g;jQuery.parseJSON=function(data){if(window.JSON&&window.JSON.parse){return window.JSON.parse(data+"");}var requireNonComma,depth=null,str=jQuery.trim(data+"");return str&&!jQuery.trim(str.replace(rvalidtokens,function(token,comma,open,close){if(requireNonComma&&comma){depth=0;}if(depth===0){return token;}requireNonComma=open||comma;depth+=!close-!open;return"";}))?(Function("return "+str))():jQuery.error("Invalid JSON: "+data);};jQuery.parseXML=function(data){var xml,tmp;if(!data||typeof data!=="string"){return null;}try{if(window.DOMParser){tmp=new DOMParser();xml=tmp.parseFromString(data,"text/xml");}else{xml=new ActiveXObject("Microsoft.XMLDOM");xml.async="false";xml.loadXML(data);}}catch(e){xml=undefined;}if(!xml||!xml.documentElement||xml.getElementsByTagName("parsererror").length){jQuery.error("Invalid XML: "+data);}return xml;};var ajaxLocParts,ajaxLocation,rhash=/#.*$/,rts=/([?&])_=[^&]*/,rheaders=/^(.*?):[ \t]*([^\r\n]*)\r?$/mg,rlocalProtocol=/^(?:about|app|app-storage|.+-extension|file|res|widget):$/,rnoContent=/^(?:GET|HEAD)$/,rprotocol=/^\/\//,rurl=/^([\w.+-]+:)(?:\/\/(?:[^\/?#]*@|)([^\/?#:]*)(?::(\d+)|)|)/,prefilters={},transports={},allTypes="*/".concat("*");try{ajaxLocation=location.href;}catch(e){ajaxLocation=document.createElement("a");ajaxLocation.href="";ajaxLocation=ajaxLocation.href;}ajaxLocParts=rurl.exec(ajaxLocation.toLowerCase())||[];function addToPrefiltersOrTransports(structure){return function(dataTypeExpression,func){if(typeof dataTypeExpression!=="string"){func=dataTypeExpression;dataTypeExpression="*";}var dataType,i=0,dataTypes=dataTypeExpression.toLowerCase().match(rnotwhite)||[];if(jQuery.isFunction(func)){while((dataType=dataTypes[i++])){if(dataType.charAt(0)==="+"){dataType=dataType.slice(1)||"*";(structure[dataType]=structure[dataType]||[]).unshift(func);}else{(structure[dataType]=structure[dataType]||[]).push(func);}}}};}function inspectPrefiltersOrTransports(structure,options,originalOptions,jqXHR){var inspected={},seekingTransport=(structure===transports);function inspect(dataType){var selected;inspected[dataType]=true;jQuery.each(structure[dataType]||[],function(_,prefilterOrFactory){var dataTypeOrTransport=prefilterOrFactory(options,originalOptions,jqXHR);if(typeof dataTypeOrTransport==="string"&&!seekingTransport&&!inspected[dataTypeOrTransport]){options.dataTypes.unshift(dataTypeOrTransport);inspect(dataTypeOrTransport);return false;}else{if(seekingTransport){return !(selected=dataTypeOrTransport);}}});return selected;}return inspect(options.dataTypes[0])||!inspected["*"]&&inspect("*");}function ajaxExtend(target,src){var deep,key,flatOptions=jQuery.ajaxSettings.flatOptions||{};for(key in src){if(src[key]!==undefined){(flatOptions[key]?target:(deep||(deep={})))[key]=src[key];}}if(deep){jQuery.extend(true,target,deep);}return target;}function ajaxHandleResponses(s,jqXHR,responses){var firstDataType,ct,finalDataType,type,contents=s.contents,dataTypes=s.dataTypes;while(dataTypes[0]==="*"){dataTypes.shift();if(ct===undefined){ct=s.mimeType||jqXHR.getResponseHeader("Content-Type");}}if(ct){for(type in contents){if(contents[type]&&contents[type].test(ct)){dataTypes.unshift(type);break;}}}if(dataTypes[0] in responses){finalDataType=dataTypes[0];}else{for(type in responses){if(!dataTypes[0]||s.converters[type+" "+dataTypes[0]]){finalDataType=type;break;}if(!firstDataType){firstDataType=type;}}finalDataType=finalDataType||firstDataType;}if(finalDataType){if(finalDataType!==dataTypes[0]){dataTypes.unshift(finalDataType);}return responses[finalDataType];}}function ajaxConvert(s,response,jqXHR,isSuccess){var conv2,current,conv,tmp,prev,converters={},dataTypes=s.dataTypes.slice();if(dataTypes[1]){for(conv in s.converters){converters[conv.toLowerCase()]=s.converters[conv];}}current=dataTypes.shift();while(current){if(s.responseFields[current]){jqXHR[s.responseFields[current]]=response;}if(!prev&&isSuccess&&s.dataFilter){response=s.dataFilter(response,s.dataType);}prev=current;current=dataTypes.shift();if(current){if(current==="*"){current=prev;}else{if(prev!=="*"&&prev!==current){conv=converters[prev+" "+current]||converters["* "+current];if(!conv){for(conv2 in converters){tmp=conv2.split(" ");if(tmp[1]===current){conv=converters[prev+" "+tmp[0]]||converters["* "+tmp[0]];if(conv){if(conv===true){conv=converters[conv2];}else{if(converters[conv2]!==true){current=tmp[0];dataTypes.unshift(tmp[1]);}}break;}}}}if(conv!==true){if(conv&&s["throws"]){response=conv(response);}else{try{response=conv(response);}catch(e){return{state:"parsererror",error:conv?e:"No conversion from "+prev+" to "+current};}}}}}}}return{state:"success",data:response};}jQuery.extend({active:0,lastModified:{},etag:{},ajaxSettings:{url:ajaxLocation,type:"GET",isLocal:rlocalProtocol.test(ajaxLocParts[1]),global:true,processData:true,async:true,contentType:"application/x-www-form-urlencoded; charset=UTF-8",accepts:{"*":allTypes,text:"text/plain",html:"text/html",xml:"application/xml, text/xml",json:"application/json, text/javascript"},contents:{xml:/xml/,html:/html/,json:/json/},responseFields:{xml:"responseXML",text:"responseText",json:"responseJSON"},converters:{"* text":String,"text html":true,"text json":jQuery.parseJSON,"text xml":jQuery.parseXML},flatOptions:{url:true,context:true}},ajaxSetup:function(target,settings){return settings?ajaxExtend(ajaxExtend(target,jQuery.ajaxSettings),settings):ajaxExtend(jQuery.ajaxSettings,target);},ajaxPrefilter:addToPrefiltersOrTransports(prefilters),ajaxTransport:addToPrefiltersOrTransports(transports),ajax:function(url,options){if(typeof url==="object"){options=url;url=undefined;}options=options||{};var parts,i,cacheURL,responseHeadersString,timeoutTimer,fireGlobals,transport,responseHeaders,s=jQuery.ajaxSetup({},options),callbackContext=s.context||s,globalEventContext=s.context&&(callbackContext.nodeType||callbackContext.jquery)?jQuery(callbackContext):jQuery.event,deferred=jQuery.Deferred(),completeDeferred=jQuery.Callbacks("once memory"),statusCode=s.statusCode||{},requestHeaders={},requestHeadersNames={},state=0,strAbort="canceled",jqXHR={readyState:0,getResponseHeader:function(key){var match;if(state===2){if(!responseHeaders){responseHeaders={};while((match=rheaders.exec(responseHeadersString))){responseHeaders[match[1].toLowerCase()]=match[2];}}match=responseHeaders[key.toLowerCase()];}return match==null?null:match;},getAllResponseHeaders:function(){return state===2?responseHeadersString:null;},setRequestHeader:function(name,value){var lname=name.toLowerCase();if(!state){name=requestHeadersNames[lname]=requestHeadersNames[lname]||name;requestHeaders[name]=value;}return this;},overrideMimeType:function(type){if(!state){s.mimeType=type;}return this;},statusCode:function(map){var code;if(map){if(state<2){for(code in map){statusCode[code]=[statusCode[code],map[code]];}}else{jqXHR.always(map[jqXHR.status]);}}return this;},abort:function(statusText){var finalText=statusText||strAbort;if(transport){transport.abort(finalText);}done(0,finalText);return this;}};deferred.promise(jqXHR).complete=completeDeferred.add;jqXHR.success=jqXHR.done;jqXHR.error=jqXHR.fail;s.url=((url||s.url||ajaxLocation)+"").replace(rhash,"").replace(rprotocol,ajaxLocParts[1]+"//");s.type=options.method||options.type||s.method||s.type;s.dataTypes=jQuery.trim(s.dataType||"*").toLowerCase().match(rnotwhite)||[""];if(s.crossDomain==null){parts=rurl.exec(s.url.toLowerCase());s.crossDomain=!!(parts&&(parts[1]!==ajaxLocParts[1]||parts[2]!==ajaxLocParts[2]||(parts[3]||(parts[1]==="http:"?"80":"443"))!==(ajaxLocParts[3]||(ajaxLocParts[1]==="http:"?"80":"443"))));}if(s.data&&s.processData&&typeof s.data!=="string"){s.data=jQuery.param(s.data,s.traditional);}inspectPrefiltersOrTransports(prefilters,s,options,jqXHR);if(state===2){return jqXHR;}fireGlobals=s.global;if(fireGlobals&&jQuery.active++===0){jQuery.event.trigger("ajaxStart");}s.type=s.type.toUpperCase();s.hasContent=!rnoContent.test(s.type);cacheURL=s.url;if(!s.hasContent){if(s.data){cacheURL=(s.url+=(rquery.test(cacheURL)?"&":"?")+s.data);delete s.data;}if(s.cache===false){s.url=rts.test(cacheURL)?cacheURL.replace(rts,"$1_="+nonce++):cacheURL+(rquery.test(cacheURL)?"&":"?")+"_="+nonce++;}}if(s.ifModified){if(jQuery.lastModified[cacheURL]){jqXHR.setRequestHeader("If-Modified-Since",jQuery.lastModified[cacheURL]);}if(jQuery.etag[cacheURL]){jqXHR.setRequestHeader("If-None-Match",jQuery.etag[cacheURL]);}}if(s.data&&s.hasContent&&s.contentType!==false||options.contentType){jqXHR.setRequestHeader("Content-Type",s.contentType);}jqXHR.setRequestHeader("Accept",s.dataTypes[0]&&s.accepts[s.dataTypes[0]]?s.accepts[s.dataTypes[0]]+(s.dataTypes[0]!=="*"?", "+allTypes+"; q=0.01":""):s.accepts["*"]);for(i in s.headers){jqXHR.setRequestHeader(i,s.headers[i]);}if(s.beforeSend&&(s.beforeSend.call(callbackContext,jqXHR,s)===false||state===2)){return jqXHR.abort();}strAbort="abort";for(i in {success:1,error:1,complete:1}){jqXHR[i](s[i]);}transport=inspectPrefiltersOrTransports(transports,s,options,jqXHR);if(!transport){done(-1,"No Transport");}else{jqXHR.readyState=1;if(fireGlobals){globalEventContext.trigger("ajaxSend",[jqXHR,s]);}if(s.async&&s.timeout>0){timeoutTimer=setTimeout(function(){jqXHR.abort("timeout");},s.timeout);}try{state=1;transport.send(requestHeaders,done);}catch(e){if(state<2){done(-1,e);}else{throw e;}}}function done(status,nativeStatusText,responses,headers){var isSuccess,success,error,response,modified,statusText=nativeStatusText;if(state===2){return;}state=2;if(timeoutTimer){clearTimeout(timeoutTimer);}transport=undefined;responseHeadersString=headers||"";jqXHR.readyState=status>0?4:0;isSuccess=status>=200&&status<300||status===304;if(responses){response=ajaxHandleResponses(s,jqXHR,responses);}response=ajaxConvert(s,response,jqXHR,isSuccess);if(isSuccess){if(s.ifModified){modified=jqXHR.getResponseHeader("Last-Modified");if(modified){jQuery.lastModified[cacheURL]=modified;}modified=jqXHR.getResponseHeader("etag");if(modified){jQuery.etag[cacheURL]=modified;}}if(status===204||s.type==="HEAD"){statusText="nocontent";}else{if(status===304){statusText="notmodified";}else{statusText=response.state;success=response.data;error=response.error;isSuccess=!error;}}}else{error=statusText;if(status||!statusText){statusText="error";if(status<0){status=0;}}}jqXHR.status=status;jqXHR.statusText=(nativeStatusText||statusText)+"";if(isSuccess){deferred.resolveWith(callbackContext,[success,statusText,jqXHR]);}else{deferred.rejectWith(callbackContext,[jqXHR,statusText,error]);}jqXHR.statusCode(statusCode);statusCode=undefined;if(fireGlobals){globalEventContext.trigger(isSuccess?"ajaxSuccess":"ajaxError",[jqXHR,s,isSuccess?success:error]);}completeDeferred.fireWith(callbackContext,[jqXHR,statusText]);if(fireGlobals){globalEventContext.trigger("ajaxComplete",[jqXHR,s]);if(!(--jQuery.active)){jQuery.event.trigger("ajaxStop");}}}return jqXHR;},getJSON:function(url,data,callback){return jQuery.get(url,data,callback,"json");},getScript:function(url,callback){return jQuery.get(url,undefined,callback,"script");}});jQuery.each(["get","post"],function(i,method){jQuery[method]=function(url,data,callback,type){if(jQuery.isFunction(data)){type=type||callback;callback=data;data=undefined;}return jQuery.ajax({url:url,type:method,dataType:type,data:data,success:callback});};});jQuery.each(["ajaxStart","ajaxStop","ajaxComplete","ajaxError","ajaxSuccess","ajaxSend"],function(i,type){jQuery.fn[type]=function(fn){return this.on(type,fn);};});jQuery._evalUrl=function(url){return jQuery.ajax({url:url,type:"GET",dataType:"script",async:false,global:false,"throws":true});};jQuery.fn.extend({wrapAll:function(html){if(jQuery.isFunction(html)){return this.each(function(i){jQuery(this).wrapAll(html.call(this,i));});}if(this[0]){var wrap=jQuery(html,this[0].ownerDocument).eq(0).clone(true);if(this[0].parentNode){wrap.insertBefore(this[0]);}wrap.map(function(){var elem=this;while(elem.firstChild&&elem.firstChild.nodeType===1){elem=elem.firstChild;}return elem;}).append(this);}return this;},wrapInner:function(html){if(jQuery.isFunction(html)){return this.each(function(i){jQuery(this).wrapInner(html.call(this,i));});}return this.each(function(){var self=jQuery(this),contents=self.contents();if(contents.length){contents.wrapAll(html);}else{self.append(html);}});},wrap:function(html){var isFunction=jQuery.isFunction(html);return this.each(function(i){jQuery(this).wrapAll(isFunction?html.call(this,i):html);});},unwrap:function(){return this.parent().each(function(){if(!jQuery.nodeName(this,"body")){jQuery(this).replaceWith(this.childNodes);}}).end();}});jQuery.expr.filters.hidden=function(elem){return elem.offsetWidth<=0&&elem.offsetHeight<=0||(!support.reliableHiddenOffsets()&&((elem.style&&elem.style.display)||jQuery.css(elem,"display"))==="none");};jQuery.expr.filters.visible=function(elem){return !jQuery.expr.filters.hidden(elem);};var r20=/%20/g,rbracket=/\[\]$/,rCRLF=/\r?\n/g,rsubmitterTypes=/^(?:submit|button|image|reset|file)$/i,rsubmittable=/^(?:input|select|textarea|keygen)/i;function buildParams(prefix,obj,traditional,add){var name;if(jQuery.isArray(obj)){jQuery.each(obj,function(i,v){if(traditional||rbracket.test(prefix)){add(prefix,v);}else{buildParams(prefix+"["+(typeof v==="object"?i:"")+"]",v,traditional,add);}});}else{if(!traditional&&jQuery.type(obj)==="object"){for(name in obj){buildParams(prefix+"["+name+"]",obj[name],traditional,add);}}else{add(prefix,obj);}}}jQuery.param=function(a,traditional){var prefix,s=[],add=function(key,value){value=jQuery.isFunction(value)?value():(value==null?"":value);s[s.length]=encodeURIComponent(key)+"="+encodeURIComponent(value);};if(traditional===undefined){traditional=jQuery.ajaxSettings&&jQuery.ajaxSettings.traditional;}if(jQuery.isArray(a)||(a.jquery&&!jQuery.isPlainObject(a))){jQuery.each(a,function(){add(this.name,this.value);});}else{for(prefix in a){buildParams(prefix,a[prefix],traditional,add);}}return s.join("&").replace(r20,"+");};jQuery.fn.extend({serialize:function(){return jQuery.param(this.serializeArray());},serializeArray:function(){return this.map(function(){var elements=jQuery.prop(this,"elements");return elements?jQuery.makeArray(elements):this;}).filter(function(){var type=this.type;return this.name&&!jQuery(this).is(":disabled")&&rsubmittable.test(this.nodeName)&&!rsubmitterTypes.test(type)&&(this.checked||!rcheckableType.test(type));}).map(function(i,elem){var val=jQuery(this).val();return val==null?null:jQuery.isArray(val)?jQuery.map(val,function(val){return{name:elem.name,value:val.replace(rCRLF,"\r\n")};}):{name:elem.name,value:val.replace(rCRLF,"\r\n")};}).get();}});jQuery.ajaxSettings.xhr=window.ActiveXObject!==undefined?function(){return !this.isLocal&&/^(get|post|head|put|delete|options)$/i.test(this.type)&&createStandardXHR()||createActiveXHR();}:createStandardXHR;var xhrId=0,xhrCallbacks={},xhrSupported=jQuery.ajaxSettings.xhr();if(window.ActiveXObject){jQuery(window).on("unload",function(){for(var key in xhrCallbacks){xhrCallbacks[key](undefined,true);}});}support.cors=!!xhrSupported&&("withCredentials" in xhrSupported);xhrSupported=support.ajax=!!xhrSupported;if(xhrSupported){jQuery.ajaxTransport(function(options){if(!options.crossDomain||support.cors){var callback;return{send:function(headers,complete){var i,xhr=options.xhr(),id=++xhrId;xhr.open(options.type,options.url,options.async,options.username,options.password);if(options.xhrFields){for(i in options.xhrFields){xhr[i]=options.xhrFields[i];}}if(options.mimeType&&xhr.overrideMimeType){xhr.overrideMimeType(options.mimeType);}if(!options.crossDomain&&!headers["X-Requested-With"]){headers["X-Requested-With"]="XMLHttpRequest";}for(i in headers){if(headers[i]!==undefined){xhr.setRequestHeader(i,headers[i]+"");}}xhr.send((options.hasContent&&options.data)||null);callback=function(_,isAbort){var status,statusText,responses;if(callback&&(isAbort||xhr.readyState===4)){delete xhrCallbacks[id];callback=undefined;xhr.onreadystatechange=jQuery.noop;if(isAbort){if(xhr.readyState!==4){xhr.abort();}}else{responses={};status=xhr.status;if(typeof xhr.responseText==="string"){responses.text=xhr.responseText;}try{statusText=xhr.statusText;}catch(e){statusText="";}if(!status&&options.isLocal&&!options.crossDomain){status=responses.text?200:404;}else{if(status===1223){status=204;}}}}if(responses){complete(status,statusText,responses,xhr.getAllResponseHeaders());}};if(!options.async){callback();}else{if(xhr.readyState===4){setTimeout(callback);}else{xhr.onreadystatechange=xhrCallbacks[id]=callback;}}},abort:function(){if(callback){callback(undefined,true);}}};}});}function createStandardXHR(){try{return new window.XMLHttpRequest();}catch(e){}}function createActiveXHR(){try{return new window.ActiveXObject("Microsoft.XMLHTTP");}catch(e){}}jQuery.ajaxSetup({accepts:{script:"text/javascript, application/javascript, application/ecmascript, application/x-ecmascript"},contents:{script:/(?:java|ecma)script/},converters:{"text script":function(text){jQuery.globalEval(text);return text;}}});jQuery.ajaxPrefilter("script",function(s){if(s.cache===undefined){s.cache=false;}if(s.crossDomain){s.type="GET";s.global=false;}});jQuery.ajaxTransport("script",function(s){if(s.crossDomain){var script,head=document.head||jQuery("head")[0]||document.documentElement;return{send:function(_,callback){script=document.createElement("script");script.async=true;if(s.scriptCharset){script.charset=s.scriptCharset;}script.src=s.url;script.onload=script.onreadystatechange=function(_,isAbort){if(isAbort||!script.readyState||/loaded|complete/.test(script.readyState)){script.onload=script.onreadystatechange=null;if(script.parentNode){script.parentNode.removeChild(script);}script=null;if(!isAbort){callback(200,"success");}}};head.insertBefore(script,head.firstChild);},abort:function(){if(script){script.onload(undefined,true);}}};}});var oldCallbacks=[],rjsonp=/(=)\?(?=&|$)|\?\?/;jQuery.ajaxSetup({jsonp:"callback",jsonpCallback:function(){var callback=oldCallbacks.pop()||(jQuery.expando+"_"+(nonce++));this[callback]=true;return callback;}});jQuery.ajaxPrefilter("json jsonp",function(s,originalSettings,jqXHR){var callbackName,overwritten,responseContainer,jsonProp=s.jsonp!==false&&(rjsonp.test(s.url)?"url":typeof s.data==="string"&&!(s.contentType||"").indexOf("application/x-www-form-urlencoded")&&rjsonp.test(s.data)&&"data");if(jsonProp||s.dataTypes[0]==="jsonp"){callbackName=s.jsonpCallback=jQuery.isFunction(s.jsonpCallback)?s.jsonpCallback():s.jsonpCallback;if(jsonProp){s[jsonProp]=s[jsonProp].replace(rjsonp,"$1"+callbackName);}else{if(s.jsonp!==false){s.url+=(rquery.test(s.url)?"&":"?")+s.jsonp+"="+callbackName;}}s.converters["script json"]=function(){if(!responseContainer){jQuery.error(callbackName+" was not called");}return responseContainer[0];};s.dataTypes[0]="json";overwritten=window[callbackName];window[callbackName]=function(){responseContainer=arguments;};jqXHR.always(function(){window[callbackName]=overwritten;if(s[callbackName]){s.jsonpCallback=originalSettings.jsonpCallback;oldCallbacks.push(callbackName);}if(responseContainer&&jQuery.isFunction(overwritten)){overwritten(responseContainer[0]);}responseContainer=overwritten=undefined;});return"script";}});jQuery.parseHTML=function(data,context,keepScripts){if(!data||typeof data!=="string"){return null;}if(typeof context==="boolean"){keepScripts=context;context=false;}context=context||document;var parsed=rsingleTag.exec(data),scripts=!keepScripts&&[];if(parsed){return[context.createElement(parsed[1])];}parsed=jQuery.buildFragment([data],context,scripts);if(scripts&&scripts.length){jQuery(scripts).remove();}return jQuery.merge([],parsed.childNodes);};var _load=jQuery.fn.load;jQuery.fn.load=function(url,params,callback){if(typeof url!=="string"&&_load){return _load.apply(this,arguments);}var selector,response,type,self=this,off=url.indexOf(" ");if(off>=0){selector=jQuery.trim(url.slice(off,url.length));url=url.slice(0,off);}if(jQuery.isFunction(params)){callback=params;params=undefined;}else{if(params&&typeof params==="object"){type="POST";}}if(self.length>0){jQuery.ajax({url:url,type:type,dataType:"html",data:params}).done(function(responseText){response=arguments;self.html(selector?jQuery("<div>").append(jQuery.parseHTML(responseText)).find(selector):responseText);}).complete(callback&&function(jqXHR,status){self.each(callback,response||[jqXHR.responseText,status,jqXHR]);});}return this;};jQuery.expr.filters.animated=function(elem){return jQuery.grep(jQuery.timers,function(fn){return elem===fn.elem;}).length;};var docElem=window.document.documentElement;function getWindow(elem){return jQuery.isWindow(elem)?elem:elem.nodeType===9?elem.defaultView||elem.parentWindow:false;}jQuery.offset={setOffset:function(elem,options,i){var curPosition,curLeft,curCSSTop,curTop,curOffset,curCSSLeft,calculatePosition,position=jQuery.css(elem,"position"),curElem=jQuery(elem),props={};if(position==="static"){elem.style.position="relative";}curOffset=curElem.offset();curCSSTop=jQuery.css(elem,"top");curCSSLeft=jQuery.css(elem,"left");calculatePosition=(position==="absolute"||position==="fixed")&&jQuery.inArray("auto",[curCSSTop,curCSSLeft])>-1;if(calculatePosition){curPosition=curElem.position();curTop=curPosition.top;curLeft=curPosition.left;}else{curTop=parseFloat(curCSSTop)||0;curLeft=parseFloat(curCSSLeft)||0;}if(jQuery.isFunction(options)){options=options.call(elem,i,curOffset);}if(options.top!=null){props.top=(options.top-curOffset.top)+curTop;}if(options.left!=null){props.left=(options.left-curOffset.left)+curLeft;}if("using" in options){options.using.call(elem,props);}else{curElem.css(props);}}};jQuery.fn.extend({offset:function(options){if(arguments.length){return options===undefined?this:this.each(function(i){jQuery.offset.setOffset(this,options,i);});}var docElem,win,box={top:0,left:0},elem=this[0],doc=elem&&elem.ownerDocument;if(!doc){return;}docElem=doc.documentElement;if(!jQuery.contains(docElem,elem)){return box;}if(typeof elem.getBoundingClientRect!==strundefined){box=elem.getBoundingClientRect();}win=getWindow(doc);return{top:box.top+(win.pageYOffset||docElem.scrollTop)-(docElem.clientTop||0),left:box.left+(win.pageXOffset||docElem.scrollLeft)-(docElem.clientLeft||0)};},position:function(){if(!this[0]){return;}var offsetParent,offset,parentOffset={top:0,left:0},elem=this[0];if(jQuery.css(elem,"position")==="fixed"){offset=elem.getBoundingClientRect();}else{offsetParent=this.offsetParent();offset=this.offset();if(!jQuery.nodeName(offsetParent[0],"html")){parentOffset=offsetParent.offset();}parentOffset.top+=jQuery.css(offsetParent[0],"borderTopWidth",true);parentOffset.left+=jQuery.css(offsetParent[0],"borderLeftWidth",true);}return{top:offset.top-parentOffset.top-jQuery.css(elem,"marginTop",true),left:offset.left-parentOffset.left-jQuery.css(elem,"marginLeft",true)};},offsetParent:function(){return this.map(function(){var offsetParent=this.offsetParent||docElem;while(offsetParent&&(!jQuery.nodeName(offsetParent,"html")&&jQuery.css(offsetParent,"position")==="static")){offsetParent=offsetParent.offsetParent;}return offsetParent||docElem;});}});jQuery.each({scrollLeft:"pageXOffset",scrollTop:"pageYOffset"},function(method,prop){var top=/Y/.test(prop);jQuery.fn[method]=function(val){return access(this,function(elem,method,val){var win=getWindow(elem);if(val===undefined){return win?(prop in win)?win[prop]:win.document.documentElement[method]:elem[method];}if(win){win.scrollTo(!top?val:jQuery(win).scrollLeft(),top?val:jQuery(win).scrollTop());}else{elem[method]=val;}},method,val,arguments.length,null);};});jQuery.each(["top","left"],function(i,prop){jQuery.cssHooks[prop]=addGetHookIf(support.pixelPosition,function(elem,computed){if(computed){computed=curCSS(elem,prop);return rnumnonpx.test(computed)?jQuery(elem).position()[prop]+"px":computed;}});});jQuery.each({Height:"height",Width:"width"},function(name,type){jQuery.each({padding:"inner"+name,content:type,"":"outer"+name},function(defaultExtra,funcName){jQuery.fn[funcName]=function(margin,value){var chainable=arguments.length&&(defaultExtra||typeof margin!=="boolean"),extra=defaultExtra||(margin===true||value===true?"margin":"border");return access(this,function(elem,type,value){var doc;if(jQuery.isWindow(elem)){return elem.document.documentElement["client"+name];}if(elem.nodeType===9){doc=elem.documentElement;return Math.max(elem.body["scroll"+name],doc["scroll"+name],elem.body["offset"+name],doc["offset"+name],doc["client"+name]);}return value===undefined?jQuery.css(elem,type,extra):jQuery.style(elem,type,value,extra);},type,chainable?margin:undefined,chainable,null);};});});jQuery.fn.size=function(){return this.length;};jQuery.fn.andSelf=jQuery.fn.addBack;if(typeof define==="function"&&define.amd){define("jquery",[],function(){return jQuery;});}var _jQuery=window.jQuery,_$=window.$;jQuery.noConflict=function(deep){if(window.$===jQuery){window.$=_$;}if(deep&&window.jQuery===jQuery){window.jQuery=_jQuery;}return jQuery;};if(typeof noGlobal===strundefined){window.jQuery=window.$=jQuery;}return jQuery;}));
   /* Ende jquery */
   /* Start gsb_responsiveListener */
   /*!
 * @name responsiveListener
 * @author @anuebel/pespeloe
 * Licensed under the MIT license
 *
 * Callback function je Breakpoint
 * */
(function($){if(!$.gsb){$.gsb={};}$.gsb.responsiveListener=function(pluginBase){var base=this;this.base=base;this.pluginBase=pluginBase;this.base.breakpoints=[];this.base.breakpointSettings=[];this.base.activeBreakpoint=null;this.base.windowWidth=0;base.init=function(){base.options=pluginBase.options;base.originalOptions=base.options;this.base.options=base.options;this.base.originalOptions=base.originalOptions;base.initResponsive();base.checkResponsive();if(base.options.respondToEvents===true){base.initEvents();}};$.gsb.responsiveListener.prototype.initEvents=function(){var $window=$(window),onResize=function(){if($(window).width()!==base.windowWidth){clearTimeout(base.windowDelay);base.windowDelay=window.setTimeout(function(){base.windowWidth=$(window).width();base.checkResponsive();},100);}};$window.on("resize",onResize);$window.on("orientationchange",function(){base.checkResponsive();});};base.init();};$.gsb.responsiveListener.prototype.initResponsive=function(){var breakpoint,responsiveSettings=this.base.options.responsive||null;if(responsiveSettings&&responsiveSettings.length>-1){for(breakpoint in responsiveSettings){if(responsiveSettings.hasOwnProperty(breakpoint)){this.base.breakpoints.push(responsiveSettings[breakpoint].breakpoint);this.base.breakpointSettings[responsiveSettings[breakpoint].breakpoint]=responsiveSettings[breakpoint];}}this.base.breakpoints.sort(function(a,b){return b-a;});}};$.gsb.responsiveListener.prototype.checkResponsive=function(){var breakpoint,targetBreakpoint,respondToWidth=window.innerWidth||$(window).width();if(this.base.originalOptions.responsive&&this.base.originalOptions.responsive.length>-1&&this.base.originalOptions.responsive!==null){targetBreakpoint=null;if(this.base.activeBreakpoint!==null&&respondToWidth>=this.base.breakpoints[0]){this.base.activeBreakpoint=null;this.pluginBase.options=this.base.originalOptions;}for(breakpoint in this.base.breakpoints){if(this.base.breakpoints.hasOwnProperty(breakpoint)){if(respondToWidth<this.base.breakpoints[breakpoint]){targetBreakpoint=this.base.breakpoints[breakpoint];this.base.highestResolution=false;}}}if(this.base.highestResolution){return;}if(targetBreakpoint!==null){if(this.base.activeBreakpoint!==null){if(targetBreakpoint!==this.base.activeBreakpoint){this.base.activeBreakpoint=targetBreakpoint;this.pluginBase.options=$.extend({},this.base.originalOptions,this.base.breakpointSettings[targetBreakpoint]);this.base.triggerRefresh();}}else{this.base.activeBreakpoint=targetBreakpoint;this.pluginBase.options=$.extend({},this.base.originalOptions,this.base.breakpointSettings[targetBreakpoint]);this.base.triggerRefresh();}}else{if(this.base.activeBreakpoint!==null){this.base.activeBreakpoint=null;this.base.options=this.base.originalOptions;this.base.triggerRefresh();}else{this.base.highestResolution=true;this.base.triggerRefresh();}}}else{if(this.base.originalOptions.onRefresh&&this.base.originalOptions.onRefresh.length>-1&&this.base.originalOptions.onRefresh!==null){if(this.base.highestResolution){return;}this.base.triggerRefresh();this.base.highestResolution=true;}}};$.gsb.responsiveListener.prototype.triggerRefresh=function(){if(typeof this.pluginBase.options.onRefresh==="function"){this.pluginBase.options.onRefresh.call(this.pluginBase);}};$.fn.gsb_responsiveListener=function(pluginBase){return this.each(function(){(new $.gsb.responsiveListener(pluginBase));});};})(jQuery);
   /* Ende gsb_responsiveListener */
   /* Start foundation */
   (function($,window,document,undefined){var header_helpers=function(class_array){var i=class_array.length;var head=$("head");while(i--){if(head.has("."+class_array[i]).length===0){head.append('<meta class="'+class_array[i]+'" />');}}};header_helpers(["foundation-mq-small","foundation-mq-medium","foundation-mq-large","foundation-mq-xlarge","foundation-mq-xxlarge","foundation-data-attribute-namespace"]);$(function(){if(typeof FastClick!=="undefined"){if(typeof document.body!=="undefined"){FastClick.attach(document.body);}}});var S=function(selector,context){if(typeof selector==="string"){if(context){var cont;if(context.jquery){cont=context[0];if(!cont){return context;}}else{cont=context;}return $(cont.querySelectorAll(selector));}return $(document.querySelectorAll(selector));}return $(selector,context);};var attr_name=function(init){var arr=[];if(!init){arr.push("data");}if(this.namespace.length>0){arr.push(this.namespace);}arr.push(this.name);return arr.join("-");};var add_namespace=function(str){var parts=str.split("-"),i=parts.length,arr=[];while(i--){if(i!==0){arr.push(parts[i]);}else{if(this.namespace.length>0){arr.push(this.namespace,parts[i]);}else{arr.push(parts[i]);}}}return arr.reverse().join("-");};var bindings=function(method,options){var self=this,should_bind_events=!S(this).data(this.attr_name(true));if(S(this.scope).is("["+this.attr_name()+"]")){S(this.scope).data(this.attr_name(true)+"-init",$.extend({},this.settings,(options||method),this.data_options(S(this.scope))));if(should_bind_events){this.events(this.scope);}}else{S("["+this.attr_name()+"]",this.scope).each(function(){var should_bind_events=!S(this).data(self.attr_name(true)+"-init");S(this).data(self.attr_name(true)+"-init",$.extend({},self.settings,(options||method),self.data_options(S(this))));if(should_bind_events){self.events(this);}});}if(typeof method==="string"){return this[method].call(this,options);}};var single_image_loaded=function(image,callback){function loaded(){callback(image[0]);}function bindLoad(){this.one("load",loaded);if(/MSIE (\d+\.\d+);/.test(navigator.userAgent)){var src=this.attr("src"),param=src.match(/\?/)?"&":"?";param+="random="+(new Date()).getTime();this.attr("src",src+param);}}if(!image.attr("src")){loaded();return;}if(image[0].complete||image[0].readyState===4){loaded();}else{bindLoad.call(image);}};window.matchMedia=window.matchMedia||(function(doc){var bool,docElem=doc.documentElement,refNode=docElem.firstElementChild||docElem.firstChild,fakeBody=doc.createElement("body"),div=doc.createElement("div");div.id="mq-test-1";div.style.cssText="position:absolute;top:-100em";fakeBody.style.background="none";fakeBody.appendChild(div);return function(q){div.innerHTML='&shy;<style media="'+q+'"> #mq-test-1 { width: 42px; }</style>';docElem.insertBefore(fakeBody,refNode);bool=div.offsetWidth===42;docElem.removeChild(fakeBody);return{matches:bool,media:q};};}(document));function removeQuotes(string){if(typeof string==="string"||string instanceof String){string=string.replace(/^['\\/"]+|(;\s?})+|['\\/"]+$/g,"");}return string;}window.Foundation={name:"Foundation",version:"{{VERSION}}",media_queries:{small:S(".foundation-mq-small").css("font-family").replace(/^[\/\\'"]+|(;\s?})+|[\/\\'"]+$/g,""),medium:S(".foundation-mq-medium").css("font-family").replace(/^[\/\\'"]+|(;\s?})+|[\/\\'"]+$/g,""),large:S(".foundation-mq-large").css("font-family").replace(/^[\/\\'"]+|(;\s?})+|[\/\\'"]+$/g,""),xlarge:S(".foundation-mq-xlarge").css("font-family").replace(/^[\/\\'"]+|(;\s?})+|[\/\\'"]+$/g,""),xxlarge:S(".foundation-mq-xxlarge").css("font-family").replace(/^[\/\\'"]+|(;\s?})+|[\/\\'"]+$/g,"")},stylesheet:$("<style></style>").appendTo("head")[0].sheet,global:{namespace:undefined},init:function(scope,libraries,method,options,response){var args=[scope,method,options,response],responses=[];this.rtl=/rtl/i.test(S("html").attr("dir"));this.scope=scope||this.scope;this.set_namespace();if(libraries&&typeof libraries==="string"&&!/reflow/i.test(libraries)){if(this.libs.hasOwnProperty(libraries)){responses.push(this.init_lib(libraries,args));}}else{for(var lib in this.libs){responses.push(this.init_lib(lib,libraries));}}S(window).load(function(){S(window).trigger("resize.fndtn.clearing").trigger("resize.fndtn.dropdown").trigger("resize.fndtn.equalizer").trigger("resize.fndtn.interchange").trigger("resize.fndtn.joyride").trigger("resize.fndtn.magellan").trigger("resize.fndtn.topbar").trigger("resize.fndtn.slider");});return scope;},init_lib:function(lib,args){if(this.libs.hasOwnProperty(lib)){this.patch(this.libs[lib]);if(args&&args.hasOwnProperty(lib)){if(typeof this.libs[lib].settings!=="undefined"){$.extend(true,this.libs[lib].settings,args[lib]);}else{if(typeof this.libs[lib].defaults!=="undefined"){$.extend(true,this.libs[lib].defaults,args[lib]);}}return this.libs[lib].init.apply(this.libs[lib],[this.scope,args[lib]]);}args=args instanceof Array?args:new Array(args);return this.libs[lib].init.apply(this.libs[lib],args);}return function(){};},patch:function(lib){lib.scope=this.scope;lib.namespace=this.global.namespace;lib.rtl=this.rtl;lib["data_options"]=this.utils.data_options;lib["attr_name"]=attr_name;lib["add_namespace"]=add_namespace;lib["bindings"]=bindings;lib["S"]=this.utils.S;},inherit:function(scope,methods){var methods_arr=methods.split(" "),i=methods_arr.length;while(i--){if(this.utils.hasOwnProperty(methods_arr[i])){scope[methods_arr[i]]=this.utils[methods_arr[i]];}}},set_namespace:function(){var namespace=(this.global.namespace===undefined)?$(".foundation-data-attribute-namespace").css("font-family"):this.global.namespace;this.global.namespace=(namespace===undefined||/false/i.test(namespace))?"":namespace;},libs:{},utils:{S:S,throttle:function(func,delay){var timer=null;return function(){var context=this,args=arguments;if(timer==null){timer=setTimeout(function(){func.apply(context,args);timer=null;},delay);}};},debounce:function(func,delay,immediate){var timeout,result;return function(){var context=this,args=arguments;var later=function(){timeout=null;if(!immediate){result=func.apply(context,args);}};var callNow=immediate&&!timeout;clearTimeout(timeout);timeout=setTimeout(later,delay);if(callNow){result=func.apply(context,args);}return result;};},data_options:function(el,data_attr_name){data_attr_name=data_attr_name||"options";var opts={},ii,p,opts_arr,data_options=function(el){var namespace=Foundation.global.namespace;if(namespace.length>0){return el.data(namespace+"-"+data_attr_name);}return el.data(data_attr_name);};var cached_options=data_options(el);if(typeof cached_options==="object"){return cached_options;}opts_arr=(cached_options||":").split(";");ii=opts_arr.length;function isNumber(o){return !isNaN(o-0)&&o!==null&&o!==""&&o!==false&&o!==true;}function trim(str){if(typeof str==="string"){return $.trim(str);}return str;}while(ii--){p=opts_arr[ii].split(":");p=[p[0],p.slice(1).join(":")];if(/true/i.test(p[1])){p[1]=true;}if(/false/i.test(p[1])){p[1]=false;}if(isNumber(p[1])){if(p[1].indexOf(".")===-1){p[1]=parseInt(p[1],10);}else{p[1]=parseFloat(p[1]);}}if(p.length===2&&p[0].length>0){opts[trim(p[0])]=trim(p[1]);}}return opts;},register_media:function(media,media_class){if(Foundation.media_queries[media]===undefined){$("head").append('<meta class="'+media_class+'"/>');Foundation.media_queries[media]=removeQuotes($("."+media_class).css("font-family"));}},add_custom_rule:function(rule,media){if(media===undefined&&Foundation.stylesheet){Foundation.stylesheet.insertRule(rule,Foundation.stylesheet.cssRules.length);}else{var query=Foundation.media_queries[media];if(query!==undefined){Foundation.stylesheet.insertRule("@media "+Foundation.media_queries[media]+"{ "+rule+" }");}}},image_loaded:function(images,callback){var self=this,unloaded=images.length;if(unloaded===0){callback(images);}images.each(function(){single_image_loaded(self.S(this),function(){unloaded-=1;if(unloaded===0){callback(images);}});});},random_str:function(){if(!this.fidx){this.fidx=0;}this.prefix=this.prefix||[(this.name||"F"),(+new Date).toString(36)].join("-");return this.prefix+(this.fidx++).toString(36);}}};$.fn.foundation=function(){var args=Array.prototype.slice.call(arguments,0);return this.each(function(){Foundation.init.apply(Foundation,[this].concat(args));return this;});};}(jQuery,window,window.document));
   /* Ende foundation */
   /* Start foundation.tooltip */
   (function($,window,document,undefined){Foundation.libs.tooltip={name:"tooltip",version:"{{VERSION}}",settings:{additional_inheritable_classes:[],tooltip_class:".tooltip",append_to:"body",touch_close_text:"Tap To Close",disable_for_touch:false,hover_delay:200,show_on:"all",tip_template:function(selector,content){return'<span data-selector="'+selector+'" id="'+selector+'" class="'+Foundation.libs.tooltip.settings.tooltip_class.substring(1)+'" role="tooltip">'+content+'<span class="nub"></span></span>';}},cache:{},init:function(scope,method,options){Foundation.inherit(this,"random_str");this.bindings(method,options);},should_show:function(target,tip){var settings=$.extend({},this.settings,this.data_options(target));if(settings.show_on==="all"){return true;}else{if(this.small()&&settings.show_on==="small"){return true;}else{if(this.medium()&&settings.show_on==="medium"){return true;}else{if(this.large()&&settings.show_on==="large"){return true;}}}}return false;},medium:function(){return matchMedia(Foundation.media_queries["medium"]).matches;},large:function(){return matchMedia(Foundation.media_queries["large"]).matches;},events:function(instance){var self=this,S=self.S;self.create(this.S(instance));$(this.scope).off(".tooltip").on("mouseenter.fndtn.tooltip mouseleave.fndtn.tooltip touchstart.fndtn.tooltip MSPointerDown.fndtn.tooltip","["+this.attr_name()+"]",function(e){var $this=S(this),settings=$.extend({},self.settings,self.data_options($this)),is_touch=false;if(Modernizr.touch&&/touchstart|MSPointerDown/i.test(e.type)&&S(e.target).is("a")){return false;}if(/mouse/i.test(e.type)&&self.ie_touch(e)){return false;}if($this.hasClass("open")){if(Modernizr.touch&&/touchstart|MSPointerDown/i.test(e.type)){e.preventDefault();}self.hide($this);}else{if(settings.disable_for_touch&&Modernizr.touch&&/touchstart|MSPointerDown/i.test(e.type)){return;}else{if(!settings.disable_for_touch&&Modernizr.touch&&/touchstart|MSPointerDown/i.test(e.type)){e.preventDefault();S(settings.tooltip_class+".open").hide();is_touch=true;}}if(/enter|over/i.test(e.type)){this.timer=setTimeout(function(){var tip=self.showTip($this);}.bind(this),self.settings.hover_delay);}else{if(e.type==="mouseout"||e.type==="mouseleave"){clearTimeout(this.timer);self.hide($this);}else{self.showTip($this);}}}}).on("mouseleave.fndtn.tooltip touchstart.fndtn.tooltip MSPointerDown.fndtn.tooltip","["+this.attr_name()+"].open",function(e){if(/mouse/i.test(e.type)&&self.ie_touch(e)){return false;}if($(this).data("tooltip-open-event-type")=="touch"&&e.type=="mouseleave"){return;}else{if($(this).data("tooltip-open-event-type")=="mouse"&&/MSPointerDown|touchstart/i.test(e.type)){self.convert_to_touch($(this));}else{self.hide($(this));}}}).on("DOMNodeRemoved DOMAttrModified","["+this.attr_name()+"]:not(a)",function(e){self.hide(S(this));});},ie_touch:function(e){return false;},showTip:function($target){var $tip=this.getTip($target);if(this.should_show($target,$tip)){return this.show($target);}return;},getTip:function($target){var selector=this.selector($target),settings=$.extend({},this.settings,this.data_options($target)),tip=null;if(selector){tip=this.S('span[data-selector="'+selector+'"]'+settings.tooltip_class);}return(typeof tip==="object")?tip:false;},selector:function($target){var id=$target.attr("id"),dataSelector=$target.attr(this.attr_name())||$target.attr("data-selector");if((id&&id.length<1||!id)&&typeof dataSelector!="string"){dataSelector=this.random_str(6);$target.attr("data-selector",dataSelector).attr("aria-describedby",dataSelector);}return(id&&id.length>0)?id:dataSelector;},create:function($target){var self=this,settings=$.extend({},this.settings,this.data_options($target)),tip_template=this.settings.tip_template;if(typeof settings.tip_template==="string"&&window.hasOwnProperty(settings.tip_template)){tip_template=window[settings.tip_template];}var $tip=$(tip_template(this.selector($target),$("<div></div>").html($target.attr("title")).html())),classes=this.inheritable_classes($target);$tip.addClass(classes).appendTo(settings.append_to);if(Modernizr.touch){$tip.append('<span class="tap-to-close">'+settings.touch_close_text+"</span>");$tip.on("touchstart.fndtn.tooltip MSPointerDown.fndtn.tooltip",function(e){self.hide($target);});}$target.removeAttr("title").attr("title","");},reposition:function(target,tip,classes){var width,nub,nubHeight,nubWidth,column,objPos;tip.css("visibility","hidden").show();width=target.data("width");nub=tip.children(".nub");nubHeight=nub.outerHeight();nubWidth=nub.outerHeight();if(this.small()){tip.css({"width":"100%"});}else{tip.css({"width":(width)?width:"auto"});}objPos=function(obj,top,right,bottom,left,width){return obj.css({"top":(top)?top:"auto","bottom":(bottom)?bottom:"auto","left":(left)?left:"auto","right":(right)?right:"auto"}).end();};objPos(tip,(target.offset().top+target.outerHeight()+10),"auto","auto",target.offset().left);if(this.small()){objPos(tip,(target.offset().top+target.outerHeight()+10),"auto","auto",12.5,$(this.scope).width());tip.addClass("tip-override");objPos(nub,-nubHeight,"auto","auto",target.offset().left);}else{var left=target.offset().left;if(Foundation.rtl){nub.addClass("rtl");left=target.offset().left+target.outerWidth()-tip.outerWidth();}objPos(tip,(target.offset().top+target.outerHeight()+10),"auto","auto",left);tip.removeClass("tip-override");if(classes&&classes.indexOf("tip-top")>-1){if(Foundation.rtl){nub.addClass("rtl");}objPos(tip,(target.offset().top-tip.outerHeight()),"auto","auto",left).removeClass("tip-override");}else{if(classes&&classes.indexOf("tip-left")>-1){objPos(tip,(target.offset().top+(target.outerHeight()/2)-(tip.outerHeight()/2)),"auto","auto",(target.offset().left-tip.outerWidth()-nubHeight)).removeClass("tip-override");nub.removeClass("rtl");}else{if(classes&&classes.indexOf("tip-right")>-1){objPos(tip,(target.offset().top+(target.outerHeight()/2)-(tip.outerHeight()/2)),"auto","auto",(target.offset().left+target.outerWidth()+nubHeight)).removeClass("tip-override");nub.removeClass("rtl");}}}}tip.css("visibility","visible").hide();},small:function(){return matchMedia(Foundation.media_queries.small).matches&&!matchMedia(Foundation.media_queries.medium).matches;},inheritable_classes:function($target){var settings=$.extend({},this.settings,this.data_options($target)),inheritables=["tip-top","tip-left","tip-bottom","tip-right","radius","round"].concat(settings.additional_inheritable_classes),classes=$target.attr("class"),filtered=classes?$.map(classes.split(" "),function(el,i){if($.inArray(el,inheritables)!==-1){return el;}}).join(" "):"";return $.trim(filtered);},convert_to_touch:function($target){var self=this,$tip=self.getTip($target),settings=$.extend({},self.settings,self.data_options($target));if($tip.find(".tap-to-close").length===0){$tip.append('<span class="tap-to-close">'+settings.touch_close_text+"</span>");$tip.on("click.fndtn.tooltip.tapclose touchstart.fndtn.tooltip.tapclose MSPointerDown.fndtn.tooltip.tapclose",function(e){self.hide($target);});}$target.data("tooltip-open-event-type","touch");},show:function($target){var $tip=this.getTip($target);if($target.data("tooltip-open-event-type")=="touch"){this.convert_to_touch($target);}this.reposition($target,$tip,$target.attr("class"));$target.addClass("open");$tip.fadeIn(150);},hide:function($target){var $tip=this.getTip($target);$tip.fadeOut(150,function(){$tip.find(".tap-to-close").remove();$tip.off("click.fndtn.tooltip.tapclose MSPointerDown.fndtn.tapclose");$target.removeClass("open");});},off:function(){var self=this;this.S(this.scope).off(".fndtn.tooltip");this.S(this.settings.tooltip_class).each(function(i){$("["+self.attr_name()+"]").eq(i).attr("title",$(this).text());}).remove();},reflow:function(){}};}(jQuery,window,window.document));
   /* Ende foundation.tooltip */
   /* Start hammer */
   /*! Hammer.JS - v2.0.2 - 2014-07-28  * http://hammerjs.github.io/  *  * Copyright (c) 2014 Jorik Tangelder <j.tangelder@gmail.com>;  * Licensed under the MIT license */ !function(a,b,c,d){function e(a,b,c){return setTimeout(k(a,c),b);}function f(a,b,c){return Array.isArray(a)?(g(a,c[b],c),!0):!1;}function g(a,b,c){var e,f;if(a){if(a.forEach){a.forEach(b,c);}else{if(a.length!==d){for(e=0,f=a.length;f>e;e++){b.call(c,a[e],e,a);}}else{for(e in a){a.hasOwnProperty(e)&&b.call(c,a[e],e,a);}}}}}function h(a,b,c){for(var e=Object.keys(b),f=0,g=e.length;g>f;f++){(!c||c&&a[e[f]]===d)&&(a[e[f]]=b[e[f]]);}return a;}function i(a,b){return h(a,b,!0);}function j(a,b,c){var d,e=b.prototype;d=a.prototype=Object.create(e),d.constructor=a,d._super=e,c&&h(d,c);}function k(a,b){return function(){return a.apply(b,arguments);};}function l(a,b){return typeof a==hb?a.apply(b?b[0]||d:d,b):a;}function m(a,b){return a===d?b:a;}function n(a,b,c){g(r(b),function(b){a.addEventListener(b,c,!1);});}function o(a,b,c){g(r(b),function(b){a.removeEventListener(b,c,!1);});}function p(a,b){for(;a;){if(a==b){return !0;}a=a.parentNode;}return !1;}function q(a,b){return a.indexOf(b)>-1;}function r(a){return a.trim().split(/\s+/g);}function s(a,b,c){if(a.indexOf&&!c){return a.indexOf(b);}for(var d=0,e=a.length;e>d;d++){if(c&&a[d][c]==b||!c&&a[d]===b){return d;}}return -1;}function t(a){return Array.prototype.slice.call(a,0);}function u(a,b,c){for(var d=[],e=[],f=0,g=a.length;g>f;f++){var h=b?a[f][b]:a[f];s(e,h)<0&&d.push(a[f]),e[f]=h;}return c&&(d=b?d.sort(function(a,c){return a[b]>c[b];}):d.sort()),d;}function v(a,b){for(var c,e,f=b[0].toUpperCase()+b.slice(1),g=0,h=fb.length;h>g;g++){if(c=fb[g],e=c?c+f:b,e in a){return e;}}return d;}function w(){return lb++;}function x(b,c){var d=this;this.manager=b,this.callback=c,this.element=b.element,this.target=b.options.inputTarget,this.domHandler=function(a){l(b.options.enable,[b])&&d.handler(a);},this.evEl&&n(this.element,this.evEl,this.domHandler),this.evTarget&&n(this.target,this.evTarget,this.domHandler),this.evWin&&n(a,this.evWin,this.domHandler);}function y(a){var b;return new (b=ob?M:pb?N:nb?P:L)(a,z);}function z(a,b,c){var d=c.pointers.length,e=c.changedPointers.length,f=b&vb&&d-e===0,g=b&(xb|yb)&&d-e===0;c.isFirst=!!f,c.isFinal=!!g,f&&(a.session={}),c.eventType=b,A(a,c),a.emit("hammer.input",c),a.recognize(c),a.session.prevInput=c;}function A(a,b){var c=a.session,d=b.pointers,e=d.length;c.firstInput||(c.firstInput=D(b)),e>1&&!c.firstMultiple?c.firstMultiple=D(b):1===e&&(c.firstMultiple=!1);var f=c.firstInput,g=c.firstMultiple,h=g?g.center:f.center,i=b.center=E(d);b.timeStamp=kb(),b.deltaTime=b.timeStamp-f.timeStamp,b.angle=I(h,i),b.distance=H(h,i),B(c,b),b.offsetDirection=G(b.deltaX,b.deltaY),b.scale=g?K(g.pointers,d):1,b.rotation=g?J(g.pointers,d):0,C(c,b);var j=a.element;p(b.srcEvent.target,j)&&(j=b.srcEvent.target),b.target=j;}function B(a,b){var c=b.center,d=a.offsetDelta||{},e=a.prevDelta||{},f=a.prevInput||{};(b.eventType===vb||f.eventType===xb)&&(e=a.prevDelta={x:f.deltaX||0,y:f.deltaY||0},d=a.offsetDelta={x:c.x,y:c.y}),b.deltaX=e.x+(c.x-d.x),b.deltaY=e.y+(c.y-d.y);}function C(a,b){var c,e,f,g,h=a.lastInterval||b,i=b.timeStamp-h.timeStamp;if(b.eventType!=yb&&(i>ub||h.velocity===d)){var j=h.deltaX-b.deltaX,k=h.deltaY-b.deltaY,l=F(i,j,k);e=l.x,f=l.y,c=jb(l.x)>jb(l.y)?l.x:l.y,g=G(j,k),a.lastInterval=b;}else{c=h.velocity,e=h.velocityX,f=h.velocityY,g=h.direction;}b.velocity=c,b.velocityX=e,b.velocityY=f,b.direction=g;}function D(a){for(var b=[],c=0;c<a.pointers.length;c++){b[c]={clientX:ib(a.pointers[c].clientX),clientY:ib(a.pointers[c].clientY)};}return{timeStamp:kb(),pointers:b,center:E(b),deltaX:a.deltaX,deltaY:a.deltaY};}function E(a){var b=a.length;if(1===b){return{x:ib(a[0].clientX),y:ib(a[0].clientY)};}for(var c=0,d=0,e=0;b>e;e++){c+=a[e].clientX,d+=a[e].clientY;}return{x:ib(c/b),y:ib(d/b)};}function F(a,b,c){return{x:b/a||0,y:c/a||0};}function G(a,b){return a===b?zb:jb(a)>=jb(b)?a>0?Ab:Bb:b>0?Cb:Db;}function H(a,b,c){c||(c=Hb);var d=b[c[0]]-a[c[0]],e=b[c[1]]-a[c[1]];return Math.sqrt(d*d+e*e);}function I(a,b,c){c||(c=Hb);var d=b[c[0]]-a[c[0]],e=b[c[1]]-a[c[1]];return 180*Math.atan2(e,d)/Math.PI;}function J(a,b){return I(b[1],b[0],Ib)-I(a[1],a[0],Ib);}function K(a,b){return H(b[0],b[1],Ib)/H(a[0],a[1],Ib);}function L(){this.evEl=Kb,this.evWin=Lb,this.allow=!0,this.pressed=!1,x.apply(this,arguments);}function M(){this.evEl=Ob,this.evWin=Pb,x.apply(this,arguments),this.store=this.manager.session.pointerEvents=[];}function N(){this.evTarget=Rb,this.targetIds={},x.apply(this,arguments);}function O(a,b){var c=t(a.touches),d=this.targetIds;if(b&(vb|wb)&&1===c.length){return d[c[0].identifier]=!0,[c,c];}var e,f,g=t(a.targetTouches),h=t(a.changedTouches),i=[];if(b===vb){for(e=0,f=g.length;f>e;e++){d[g[e].identifier]=!0;}}for(e=0,f=h.length;f>e;e++){d[h[e].identifier]&&i.push(h[e]),b&(xb|yb)&&delete d[h[e].identifier];}return i.length?[u(g.concat(i),"identifier",!0),i]:void 0;}function P(){x.apply(this,arguments);var a=k(this.handler,this);this.touch=new N(this.manager,a),this.mouse=new L(this.manager,a);}function Q(a,b){this.manager=a,this.set(b);}function R(a){if(q(a,Xb)){return Xb;}var b=q(a,Yb),c=q(a,Zb);return b&&c?Yb+" "+Zb:b||c?b?Yb:Zb:q(a,Wb)?Wb:Vb;}function S(a){this.id=w(),this.manager=null,this.options=i(a||{},this.defaults),this.options.enable=m(this.options.enable,!0),this.state=$b,this.simultaneous={},this.requireFail=[];}function T(a){return a&dc?"cancel":a&bc?"end":a&ac?"move":a&_b?"start":"";}function U(a){return a==Db?"down":a==Cb?"up":a==Ab?"left":a==Bb?"right":"";}function V(a,b){var c=b.manager;return c?c.get(a):a;}function W(){S.apply(this,arguments);}function X(){W.apply(this,arguments),this.pX=null,this.pY=null;}function Y(){W.apply(this,arguments);}function Z(){S.apply(this,arguments),this._timer=null,this._input=null;}function $(){W.apply(this,arguments);}function _(){W.apply(this,arguments);}function ab(){S.apply(this,arguments),this.pTime=!1,this.pCenter=!1,this._timer=null,this._input=null,this.count=0;}function bb(a,b){return b=b||{},b.recognizers=m(b.recognizers,bb.defaults.preset),new cb(a,b);}function cb(a,b){b=b||{},this.options=i(b,bb.defaults),this.options.inputTarget=this.options.inputTarget||a,this.handlers={},this.session={},this.recognizers=[],this.element=a,this.input=y(this),this.touchAction=new Q(this,this.options.touchAction),db(this,!0),g(b.recognizers,function(a){var b=this.add(new a[0](a[1]));a[2]&&b.recognizeWith(a[2]),a[3]&&b.requireFailure(a[2]);},this);}function db(a,b){var c=a.element;g(a.options.cssProps,function(a,d){c.style[v(c.style,d)]=b?a:"";});}function eb(a,c){var d=b.createEvent("Event");d.initEvent(a,!0,!0),d.gesture=c,c.target.dispatchEvent(d);}var fb=["","webkit","moz","MS","ms","o"],gb=b.createElement("div"),hb="function",ib=Math.round,jb=Math.abs,kb=Date.now,lb=1,mb=/mobile|tablet|ip(ad|hone|od)|android/i,nb="ontouchstart" in a,ob=v(a,"PointerEvent")!==d,pb=nb&&mb.test(navigator.userAgent),qb="touch",rb="pen",sb="mouse",tb="kinect",ub=25,vb=1,wb=2,xb=4,yb=8,zb=1,Ab=2,Bb=4,Cb=8,Db=16,Eb=Ab|Bb,Fb=Cb|Db,Gb=Eb|Fb,Hb=["x","y"],Ib=["clientX","clientY"];x.prototype={handler:function(){},destroy:function(){this.evEl&&o(this.element,this.evEl,this.domHandler),this.evTarget&&o(this.target,this.evTarget,this.domHandler),this.evWin&&o(a,this.evWin,this.domHandler);}};var Jb={mousedown:vb,mousemove:wb,mouseup:xb},Kb="mousedown",Lb="mousemove mouseup";j(L,x,{handler:function(a){var b=Jb[a.type];b&vb&&0===a.button&&(this.pressed=!0),b&wb&&1!==a.which&&(b=xb),this.pressed&&this.allow&&(b&xb&&(this.pressed=!1),this.callback(this.manager,b,{pointers:[a],changedPointers:[a],pointerType:sb,srcEvent:a}));}});var Mb={pointerdown:vb,pointermove:wb,pointerup:xb,pointercancel:yb,pointerout:yb},Nb={2:qb,3:rb,4:sb,5:tb},Ob="pointerdown",Pb="pointermove pointerup pointercancel";a.MSPointerEvent&&(Ob="MSPointerDown",Pb="MSPointerMove MSPointerUp MSPointerCancel"),j(M,x,{handler:function(a){var b=this.store,c=!1,d=a.type.toLowerCase().replace("ms",""),e=Mb[d],f=Nb[a.pointerType]||a.pointerType,g=f==qb;e&vb&&(0===a.button||g)?b.push(a):e&(xb|yb)&&(c=!0);var h=s(b,a.pointerId,"pointerId");0>h||(b[h]=a,this.callback(this.manager,e,{pointers:b,changedPointers:[a],pointerType:f,srcEvent:a}),c&&b.splice(h,1));}});var Qb={touchstart:vb,touchmove:wb,touchend:xb,touchcancel:yb},Rb="touchstart touchmove touchend touchcancel";j(N,x,{handler:function(a){var b=Qb[a.type],c=O.call(this,a,b);c&&this.callback(this.manager,b,{pointers:c[0],changedPointers:c[1],pointerType:qb,srcEvent:a});}}),j(P,x,{handler:function(a,b,c){var d=c.pointerType==qb,e=c.pointerType==sb;if(d){this.mouse.allow=!1;}else{if(e&&!this.mouse.allow){return;}}b&(xb|yb)&&(this.mouse.allow=!0),this.callback(a,b,c);},destroy:function(){this.touch.destroy(),this.mouse.destroy();}});var Sb=v(gb.style,"touchAction"),Tb=Sb!==d,Ub="compute",Vb="auto",Wb="manipulation",Xb="none",Yb="pan-x",Zb="pan-y";Q.prototype={set:function(a){a==Ub&&(a=this.compute()),Tb&&(this.manager.element.style[Sb]=a),this.actions=a.toLowerCase().trim();},update:function(){this.set(this.manager.options.touchAction);},compute:function(){var a=[];return g(this.manager.recognizers,function(b){l(b.options.enable,[b])&&(a=a.concat(b.getTouchAction()));}),R(a.join(" "));},preventDefaults:function(a){if(!Tb){var b=a.srcEvent,c=a.offsetDirection;if(this.manager.session.prevented){return void b.preventDefault();}var d=this.actions,e=q(d,Xb),f=q(d,Zb),g=q(d,Yb);return e||f&&g||f&&c&Eb||g&&c&Fb?this.preventSrc(b):void 0;}},preventSrc:function(a){this.manager.session.prevented=!0,a.preventDefault();}};var $b=1,_b=2,ac=4,bc=8,cc=bc,dc=16,ec=32;S.prototype={defaults:{},set:function(a){return h(this.options,a),this.manager&&this.manager.touchAction.update(),this;},recognizeWith:function(a){if(f(a,"recognizeWith",this)){return this;}var b=this.simultaneous;return a=V(a,this),b[a.id]||(b[a.id]=a,a.recognizeWith(this)),this;},dropRecognizeWith:function(a){return f(a,"dropRecognizeWith",this)?this:(a=V(a,this),delete this.simultaneous[a.id],this);},requireFailure:function(a){if(f(a,"requireFailure",this)){return this;}var b=this.requireFail;return a=V(a,this),-1===s(b,a)&&(b.push(a),a.requireFailure(this)),this;},dropRequireFailure:function(a){if(f(a,"dropRequireFailure",this)){return this;}a=V(a,this);var b=s(this.requireFail,a);return b>-1&&this.requireFail.splice(b,1),this;},hasRequireFailures:function(){return this.requireFail.length>0;},canRecognizeWith:function(a){return !!this.simultaneous[a.id];},emit:function(a){function b(b){c.manager.emit(c.options.event+(b?T(d):""),a);}var c=this,d=this.state;bc>d&&b(!0),b(),d>=bc&&b(!0);},tryEmit:function(a){return this.canEmit()?this.emit(a):void (this.state=ec);},canEmit:function(){for(var a=0;a<this.requireFail.length;a++){if(!(this.requireFail[a].state&(ec|$b))){return !1;}}return !0;},recognize:function(a){var b=h({},a);return l(this.options.enable,[this,b])?(this.state&(cc|dc|ec)&&(this.state=$b),this.state=this.process(b),void (this.state&(_b|ac|bc|dc)&&this.tryEmit(b))):(this.reset(),void (this.state=ec));},process:function(){},getTouchAction:function(){},reset:function(){}},j(W,S,{defaults:{pointers:1},attrTest:function(a){var b=this.options.pointers;return 0===b||a.pointers.length===b;},process:function(a){var b=this.state,c=a.eventType,d=b&(_b|ac),e=this.attrTest(a);return d&&(c&yb||!e)?b|dc:d||e?c&xb?b|bc:b&_b?b|ac:_b:ec;}}),j(X,W,{defaults:{event:"pan",threshold:10,pointers:1,direction:Gb},getTouchAction:function(){var a=this.options.direction;if(a===Gb){return[Xb];}var b=[];return a&Eb&&b.push(Zb),a&Fb&&b.push(Yb),b;},directionTest:function(a){var b=this.options,c=!0,d=a.distance,e=a.direction,f=a.deltaX,g=a.deltaY;return e&b.direction||(b.direction&Eb?(e=0===f?zb:0>f?Ab:Bb,c=f!=this.pX,d=Math.abs(a.deltaX)):(e=0===g?zb:0>g?Cb:Db,c=g!=this.pY,d=Math.abs(a.deltaY))),a.direction=e,c&&d>b.threshold&&e&b.direction;},attrTest:function(a){return W.prototype.attrTest.call(this,a)&&(this.state&_b||!(this.state&_b)&&this.directionTest(a));},emit:function(a){this.pX=a.deltaX,this.pY=a.deltaY;var b=U(a.direction);b&&this.manager.emit(this.options.event+b,a),this._super.emit.call(this,a);}}),j(Y,W,{defaults:{event:"pinch",threshold:0,pointers:2},getTouchAction:function(){return[Xb];},attrTest:function(a){return this._super.attrTest.call(this,a)&&(Math.abs(a.scale-1)>this.options.threshold||this.state&_b);},emit:function(a){if(this._super.emit.call(this,a),1!==a.scale){var b=a.scale<1?"in":"out";this.manager.emit(this.options.event+b,a);}}}),j(Z,S,{defaults:{event:"press",pointers:1,time:500,threshold:5},getTouchAction:function(){return[Vb];},process:function(a){var b=this.options,c=a.pointers.length===b.pointers,d=a.distance<b.threshold,f=a.deltaTime>b.time;if(this._input=a,!d||!c||a.eventType&(xb|yb)&&!f){this.reset();}else{if(a.eventType&vb){this.reset(),this._timer=e(function(){this.state=cc,this.tryEmit();},b.time,this);}else{if(a.eventType&xb){return cc;}}}return ec;},reset:function(){clearTimeout(this._timer);},emit:function(a){this.state===cc&&(a&&a.eventType&xb?this.manager.emit(this.options.event+"up",a):(this._input.timeStamp=kb(),this.manager.emit(this.options.event,this._input)));}}),j($,W,{defaults:{event:"rotate",threshold:0,pointers:2},getTouchAction:function(){return[Xb];},attrTest:function(a){return this._super.attrTest.call(this,a)&&(Math.abs(a.rotation)>this.options.threshold||this.state&_b);}}),j(_,W,{defaults:{event:"swipe",threshold:10,velocity:0.65,direction:Eb|Fb,pointers:1},getTouchAction:function(){return X.prototype.getTouchAction.call(this);},attrTest:function(a){var b,c=this.options.direction;return c&(Eb|Fb)?b=a.velocity:c&Eb?b=a.velocityX:c&Fb&&(b=a.velocityY),this._super.attrTest.call(this,a)&&c&a.direction&&jb(b)>this.options.velocity&&a.eventType&xb;},emit:function(a){var b=U(a.direction);b&&this.manager.emit(this.options.event+b,a),this.manager.emit(this.options.event,a);}}),j(ab,S,{defaults:{event:"tap",pointers:1,taps:1,interval:300,time:250,threshold:2,posThreshold:10},getTouchAction:function(){return[Wb];},process:function(a){var b=this.options,c=a.pointers.length===b.pointers,d=a.distance<b.threshold,f=a.deltaTime<b.time;if(this.reset(),a.eventType&vb&&0===this.count){return this.failTimeout();}if(d&&f&&c){if(a.eventType!=xb){return this.failTimeout();}var g=this.pTime?a.timeStamp-this.pTime<b.interval:!0,h=!this.pCenter||H(this.pCenter,a.center)<b.posThreshold;this.pTime=a.timeStamp,this.pCenter=a.center,h&&g?this.count+=1:this.count=1,this._input=a;var i=this.count%b.taps;if(0===i){return this.hasRequireFailures()?(this._timer=e(function(){this.state=cc,this.tryEmit();},b.interval,this),_b):cc;}}return ec;},failTimeout:function(){return this._timer=e(function(){this.state=ec;},this.options.interval,this),ec;},reset:function(){clearTimeout(this._timer);},emit:function(){this.state==cc&&(this._input.tapCount=this.count,this.manager.emit(this.options.event,this._input));}}),bb.VERSION="2.0.2",bb.defaults={domEvents:!1,touchAction:Ub,inputTarget:null,enable:!0,preset:[[$,{enable:!1}],[Y,{enable:!1},["rotate"]],[_,{direction:Eb}],[X,{direction:Eb},["swipe"]],[ab],[ab,{event:"doubletap",taps:2},["tap"]],[Z]],cssProps:{userSelect:"none",touchSelect:"none",touchCallout:"none",contentZooming:"none",userDrag:"none",tapHighlightColor:"rgba(0,0,0,0)"}};var fc=1,gc=2;cb.prototype={set:function(a){return h(this.options,a),this;},stop:function(a){this.session.stopped=a?gc:fc;},recognize:function(a){var b=this.session;if(!b.stopped){this.touchAction.preventDefaults(a);var c,d=this.recognizers,e=b.curRecognizer;(!e||e&&e.state&cc)&&(e=b.curRecognizer=null);for(var f=0,g=d.length;g>f;f++){c=d[f],b.stopped===gc||e&&c!=e&&!c.canRecognizeWith(e)?c.reset():c.recognize(a),!e&&c.state&(_b|ac|bc)&&(e=b.curRecognizer=c);}}},get:function(a){if(a instanceof S){return a;}for(var b=this.recognizers,c=0;c<b.length;c++){if(b[c].options.event==a){return b[c];}}return null;},add:function(a){if(f(a,"add",this)){return this;}var b=this.get(a.options.event);return b&&this.remove(b),this.recognizers.push(a),a.manager=this,this.touchAction.update(),a;},remove:function(a){if(f(a,"remove",this)){return this;}var b=this.recognizers;return a=this.get(a),b.splice(s(b,a),1),this.touchAction.update(),this;},on:function(a,b){var c=this.handlers;return g(r(a),function(a){c[a]=c[a]||[],c[a].push(b);}),this;},off:function(a,b){var c=this.handlers;return g(r(a),function(a){b?c[a].splice(s(c[a],b),1):delete c[a];}),this;},emit:function(a,b){this.options.domEvents&&eb(a,b);var c=this.handlers[a]&&this.handlers[a].slice();if(c&&c.length){b.type=a,b.preventDefault=function(){b.srcEvent.preventDefault();};for(var d=0,e=c.length;e>d;d++){c[d](b);}}},destroy:function(){this.element&&db(this,!1),this.handlers={},this.session={},this.input.destroy(),this.element=null;}},h(bb,{INPUT_START:vb,INPUT_MOVE:wb,INPUT_END:xb,INPUT_CANCEL:yb,STATE_POSSIBLE:$b,STATE_BEGAN:_b,STATE_CHANGED:ac,STATE_ENDED:bc,STATE_RECOGNIZED:cc,STATE_CANCELLED:dc,STATE_FAILED:ec,DIRECTION_NONE:zb,DIRECTION_LEFT:Ab,DIRECTION_RIGHT:Bb,DIRECTION_UP:Cb,DIRECTION_DOWN:Db,DIRECTION_HORIZONTAL:Eb,DIRECTION_VERTICAL:Fb,DIRECTION_ALL:Gb,Manager:cb,Input:x,TouchAction:Q,Recognizer:S,AttrRecognizer:W,Tap:ab,Pan:X,Swipe:_,Pinch:Y,Rotate:$,Press:Z,on:n,off:o,each:g,merge:i,extend:h,inherit:j,bindFn:k,prefixed:v}),typeof define==hb&&define.amd?define(function(){return bb;}):"undefined"!=typeof module&&module.exports?module.exports=bb:a[c]=bb;}(window,document,"Hammer");
   /* Ende hammer */
   /* Start jquery.hammer */
   (function($,Hammer,dataAttr){function hammerify(el,options){var $el=$(el);if(!$el.data(dataAttr)){$el.data(dataAttr,new Hammer($el[0],options));}}$.fn.hammer=function(options){return this.each(function(){hammerify(this,options);});};Hammer.Manager.prototype.emit=(function(originalEmit){return function(type,data){originalEmit.call(this,type,data);jQuery(this.element).triggerHandler({type:type,gesture:data});};})(Hammer.Manager.prototype.emit);})(jQuery,Hammer,"hammer");
   /* Ende jquery.hammer */
   /* Start jquery.mmenu */
   /*!
 * jQuery mmenu v4.4.0
 * @requires jQuery 1.7.0 or later
 *
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 * www.frebsite.nl
 *
 * Licensed under the MIT license:
 * http://en.wikipedia.org/wiki/MIT_License
 */
!function(e){function n(n,s,t){if(t){if("object"!=typeof n&&(n={}),"boolean"!=typeof n.isMenu){var i=t.children();n.isMenu=1==i.length&&i.is(s.panelNodetype);}return n;}n=e.extend(!0,{},e[a].defaults,n);for(var o=["position","zposition","modal","moveBackground"],l=0,d=o.length;d>l;l++){"undefined"!=typeof n[o[l]]&&(e[a].deprecated('The option "'+o[l]+'"',"offCanvas."+o[l]),n.offCanvas=n.offCanvas||{},n.offCanvas[o[l]]=n[o[l]]);}return n;}function s(n){n=e.extend(!0,{},e[a].configuration,n);for(var s=["panel","list","selected","label","spacer"],t=0,i=s.length;i>t;t++){"undefined"!=typeof n[s[t]+"Class"]&&(e[a].deprecated('The configuration option "'+s[t]+'Class"',"classNames."+s[t]),n.classNames[s[t]]=n[s[t]+"Class"]);}if("undefined"!=typeof n.counterClass&&(e[a].deprecated('The configuration option "counterClass"',"classNames.counters.counter"),n.classNames.counters=n.classNames.counters||{},n.classNames.counters.counter=n.counterClass),"undefined"!=typeof n.collapsedClass&&(e[a].deprecated('The configuration option "collapsedClass"',"classNames.labels.collapsed"),n.classNames.labels=n.classNames.labels||{},n.classNames.labels.collapsed=n.collapsedClass),"undefined"!=typeof n.header){for(var s=["panelHeader","panelNext","panelPrev"],t=0,i=s.length;i>t;t++){"undefined"!=typeof n.header[s[t]+"Class"]&&(e[a].deprecated('The configuration option "header.'+s[t]+'Class"',"classNames.header."+s[t]),n.classNames.header=n.classNames.header||{},n.classNames.header[s[t]]=n.header[s[t]+"Class"]);}}for(var s=["pageNodetype","pageSelector","menuWrapperSelector","menuInjectMethod"],t=0,i=s.length;i>t;t++){"undefined"!=typeof n[s[t]]&&(e[a].deprecated('The configuration option "'+s[t]+'"',"offCanvas."+s[t]),n.offCanvas=n.offCanvas||{},n.offCanvas[s[t]]=n[s[t]]);}return n;}function t(){r=!0,c.$wndw=e(window),c.$html=e("html"),c.$body=e("body"),e.each([o,l,d],function(e,n){n.add=function(e){e=e.split(" ");for(var s in e){n[e[s]]=n.mm(e[s]);}};}),o.mm=function(e){return"mm-"+e;},o.add("wrapper menu ismenu inline panel list subtitle selected label spacer current highest hidden opened subopened subopen fullsubopen subclose"),o.umm=function(e){return"mm-"==e.slice(0,3)&&(e=e.slice(3)),e;},l.mm=function(e){return"mm-"+e;},l.add("parent"),d.mm=function(e){return e+".mm";},d.add("toggle open close setSelected transitionend webkitTransitionEnd mousedown mouseup touchstart touchmove touchend scroll resize click keydown keyup"),e[a]._c=o,e[a]._d=l,e[a]._e=d,e[a].glbl=c;}var a="mmenu",i="4.4.0";if(!e[a]){var o={},l={},d={},r=!1,c={$wndw:null,$html:null,$body:null};e[a]=function(e,s,t){return this.$menu=e,this.opts=s,this.conf=t,this.vars={},this.opts=n(this.opts,this.conf,this.$menu),this._initMenu(),this._init(this.$menu.children(this.conf.panelNodetype)),this;},e[a].addons=[],e[a].uniqueId=0,e[a].prototype={_init:function(n){n=this._initPanels(n),n=this._initLinks(n),n=this._bindCustomEvents(n);for(var s=0;s<e[a].addons.length;s++){"function"==typeof this["_init_"+e[a].addons[s]]&&this["_init_"+e[a].addons[s]](n);}this._update();},_initMenu:function(){this.opts.offCanvas&&this.conf.clone&&(this.$menu=this.$menu.clone(!0),this.$menu.add(this.$menu.find("*")).filter("[id]").each(function(){e(this).attr("id",o.mm(e(this).attr("id")));})),this.$menu.contents().each(function(){3==e(this)[0].nodeType&&e(this).remove();}),this.$menu.parent().addClass(o.wrapper);var n=[o.menu];n.push(o.mm(this.opts.slidingSubmenus?"horizontal":"vertical")),this.opts.classes&&n.push(this.opts.classes),this.opts.isMenu&&n.push(o.ismenu),this.$menu.addClass(n.join(" "));},_initPanels:function(n){var s=this;this.__refactorClass(this.__findAddBack(n,"."+this.conf.classNames.list),this.conf.classNames.list,"list"),this.opts.isMenu&&this.__findAddBack(n,"ul, ol").not(".mm-nolist").addClass(o.list);var t=this.__findAddBack(n,"."+o.list).find("> li");this.__refactorClass(t,this.conf.classNames.selected,"selected"),this.__refactorClass(t,this.conf.classNames.label,"label"),this.__refactorClass(t,this.conf.classNames.spacer,"spacer"),t.off(d.setSelected).on(d.setSelected,function(n,s){n.stopPropagation(),t.removeClass(o.selected),"boolean"!=typeof s&&(s=!0),s&&e(this).addClass(o.selected);}),this.__refactorClass(this.__findAddBack(n,"."+this.conf.classNames.panel),this.conf.classNames.panel,"panel"),n.add(this.__findAddBack(n,"."+o.list).children().children().filter(this.conf.panelNodetype)).addClass(o.panel);var a=this.__findAddBack(n,"."+o.panel),i=e("."+o.panel,this.$menu);a.each(function(){var n=e(this),t=n.attr("id")||s.__getUniqueId();n.attr("id",t);}),a.each(function(){var n=e(this),t=n.is("ul, ol")?n:n.find("ul ,ol").first(),a=n.parent(),i=a.find("> a, > span"),d=a.closest("."+o.panel);if(a.parent().is("."+o.list)){n.data(l.parent,a);var r=e('<a class="'+o.subopen+'" href="#'+n.attr("id")+'" />').insertBefore(i);i.is("a")||r.addClass(o.fullsubopen),s.opts.slidingSubmenus&&t.prepend('<li class="'+o.subtitle+'"><a class="'+o.subclose+'" href="#'+d.attr("id")+'">'+i.text()+"</a></li>");}});var r=this.opts.slidingSubmenus?d.open:d.toggle;if(i.each(function(){var n=e(this),s=n.attr("id");e('a[href="#'+s+'"]',i).off(d.click).on(d.click,function(e){e.preventDefault(),n.trigger(r);});}),this.opts.slidingSubmenus){var c=this.__findAddBack(n,"."+o.list).find("> li."+o.selected);c.parents("li").removeClass(o.selected).end().add(c.parents("li")).each(function(){var n=e(this),s=n.find("> ."+o.panel);s.length&&(n.parents("."+o.panel).addClass(o.subopened),s.addClass(o.opened));}).closest("."+o.panel).addClass(o.opened).parents("."+o.panel).addClass(o.subopened);}else{var c=e("li."+o.selected,i);c.parents("li").removeClass(o.selected).end().add(c.parents("li")).addClass(o.opened);}var u=i.filter("."+o.opened);return u.length||(u=a.first()),u.addClass(o.opened).last().addClass(o.current),this.opts.slidingSubmenus&&a.not(u.last()).addClass(o.hidden).end().appendTo(this.$menu),a;},_initLinks:function(n){var s=this;return this.__findAddBack(n,"."+o.list).find("> li > a").not("."+o.subopen).not("."+o.subclose).not('[rel="external"]').not('[target="_blank"]').off(d.click).on(d.click,function(n){var t=e(this),a=t.attr("href")||"";s.__valueOrFn(s.opts.onClick.setSelected,t)&&t.parent().trigger(d.setSelected);var i=s.__valueOrFn(s.opts.onClick.preventDefault,t,"#"==a.slice(0,1));i&&n.preventDefault(),s.__valueOrFn(s.opts.onClick.blockUI,t,!i)&&c.$html.addClass(o.blocking),s.__valueOrFn(s.opts.onClick.close,t,i)&&s.$menu.triggerHandler(d.close);}),n;},_bindCustomEvents:function(n){var s=this;return n.off(d.toggle+" "+d.open+" "+d.close).on(d.toggle+" "+d.open+" "+d.close,function(e){e.stopPropagation();}),this.opts.slidingSubmenus?n.on(d.open,function(){return s._openSubmenuHorizontal(e(this));}):n.on(d.toggle,function(){var n=e(this);return n.triggerHandler(n.parent().hasClass(o.opened)?d.close:d.open);}).on(d.open,function(){return e(this).parent().addClass(o.opened),"open";}).on(d.close,function(){return e(this).parent().removeClass(o.opened),"close";}),n;},_openSubmenuHorizontal:function(n){if(n.hasClass(o.current)){return !1;}var s=e("."+o.panel,this.$menu),t=s.filter("."+o.current);return s.removeClass(o.highest).removeClass(o.current).not(n).not(t).addClass(o.hidden),n.hasClass(o.opened)?t.addClass(o.highest).removeClass(o.opened).removeClass(o.subopened):(n.addClass(o.highest),t.addClass(o.subopened)),n.removeClass(o.hidden).addClass(o.current),setTimeout(function(){n.removeClass(o.subopened).addClass(o.opened);},this.conf.openingInterval),"open";},_update:function(e){if(this.updates||(this.updates=[]),"function"==typeof e){this.updates.push(e);}else{for(var n=0,s=this.updates.length;s>n;n++){this.updates[n].call(this,e);}}},__valueOrFn:function(e,n,s){return"function"==typeof e?e.call(n[0]):"undefined"==typeof e&&"undefined"!=typeof s?s:e;},__refactorClass:function(e,n,s){e.filter("."+n).removeClass(n).addClass(o[s]);},__findAddBack:function(e,n){return e.find(n).add(e.filter(n));},__transitionend:function(e,n,s){var t=!1,a=function(){t||n.call(e[0]),t=!0;};e.one(d.transitionend,a),e.one(d.webkitTransitionEnd,a),setTimeout(a,1.1*s);},__getUniqueId:function(){return o.mm(e[a].uniqueId++);}},e.fn[a]=function(i,o){return r||t(),i=n(i,o),o=s(o),this.each(function(){var n=e(this);n.data(a)||n.data(a,new e[a](n,i,o));});},e[a].version=i,e[a].defaults={classes:"",slidingSubmenus:!0,onClick:{setSelected:!0}},e[a].configuration={panelNodetype:"ul, ol, div",transitionDuration:400,openingInterval:25,classNames:{panel:"Panle",list:"List",selected:"Selected",label:"Label",spacer:"Spacer"}},function(){var n=window.document,s=window.navigator.userAgent,t="ontouchstart" in n,i="WebkitOverflowScrolling" in n.documentElement.style,o=function(){return s.indexOf("Android")>=0?2.4>parseFloat(s.slice(s.indexOf("Android")+8)):!1;}();e[a].support={touch:t,oldAndroidBrowser:o,overflowscrolling:function(){return t?i?!0:o?!1:!0:!0;}()};}(),e[a].debug=function(){},e[a].deprecated=function(e,n){};}}(jQuery);!function(e){function o(o){return("top"==o.position||"bottom"==o.position)&&("back"==o.zposition||"next"==o.zposition)&&(e[r].deprecated('Using position "'+o.position+'" in combination with zposition "'+o.zposition+'"','zposition "front"'),o.zposition="front"),o;}function t(e){return"string"!=typeof e.pageSelector&&(e.pageSelector="> "+e.pageNodetype),e;}function n(){c=!0,s=e[r]._c,i=e[r]._d,p=e[r]._e,s.add("offcanvas modal background opening blocker page"),i.add("style"),p.add("opening opened closing closed setPage"),a=e[r].glbl,a.$allMenus=(a.$allMenus||e()).add(this.$menu),a.$wndw.on(p.keydown,function(e){return a.$html.hasClass(s.opened)&&9==e.keyCode?(e.preventDefault(),!1):void 0;});var o=0;a.$wndw.on(p.resize,function(e,t){if(t||a.$html.hasClass(s.opened)){var n=a.$wndw.height();(t||n!=o)&&(o=n,a.$page.css("minHeight",n));}});}var s,i,p,a,r="mmenu",l="offCanvas",c=!1;e[r].prototype["_init_"+l]=function(){if(this.opts[l]&&!this.vars[l+"_added"]){this.vars[l+"_added"]=!0,c||n(),this.opts[l]=o(this.opts[l]),this.conf[l]=t(this.conf[l]);var e=this.opts[l],i=this.conf[l],p=[s.offcanvas];"boolean"!=typeof this.vars.opened&&(this.vars.opened=!1),"left"!=e.position&&p.push(s.mm(e.position)),"back"!=e.zposition&&p.push(s.mm(e.zposition)),this.$menu.addClass(p.join(" ")).parent().removeClass(s.wrapper),this[l+"_initPage"](a.$page),this[l+"_initBlocker"](),this[l+"_initOpenClose"](),this[l+"_bindCustomEvents"](),this.$menu[i.menuInjectMethod+"To"](i.menuWrapperSelector);}},e[r].addons.push(l),e[r].defaults[l]={position:"left",zposition:"back",modal:!1,moveBackground:!0},e[r].configuration[l]={pageNodetype:"div",pageSelector:null,menuWrapperSelector:"body",menuInjectMethod:"prepend"},e[r].prototype.open=function(){if(this.vars.opened){return !1;}var e=this;return this._openSetup(),setTimeout(function(){e._openFinish();},this.conf.openingInterval),"open";},e[r].prototype._openSetup=function(){a.$allMenus.not(this.$menu).trigger(p.close),a.$page.data(i.style,a.$page.attr("style")||""),a.$wndw.trigger(p.resize,[!0]);var e=[s.opened];this.opts[l].modal&&e.push(s.modal),this.opts[l].moveBackground&&e.push(s.background),"left"!=this.opts[l].position&&e.push(s.mm(this.opts[l].position)),"back"!=this.opts[l].zposition&&e.push(s.mm(this.opts[l].zposition)),this.opts.classes&&e.push(this.opts.classes),a.$html.addClass(e.join(" ")),this.vars.opened=!0,this.$menu.addClass(s.current+" "+s.opened);},e[r].prototype._openFinish=function(){var e=this;this.__transitionend(a.$page,function(){e.$menu.trigger(p.opened);},this.conf.transitionDuration),a.$html.addClass(s.opening),this.$menu.trigger(p.opening);},e[r].prototype.close=function(){if(!this.vars.opened){return !1;}var e=this;return this.__transitionend(a.$page,function(){e.$menu.removeClass(s.current).removeClass(s.opened),a.$html.removeClass(s.opened).removeClass(s.modal).removeClass(s.background).removeClass(s.mm(e.opts[l].position)).removeClass(s.mm(e.opts[l].zposition)),e.opts.classes&&a.$html.removeClass(e.opts.classes),a.$page.attr("style",a.$page.data(i.style)),e.vars.opened=!1,e.$menu.trigger(p.closed);},this.conf.transitionDuration),a.$html.removeClass(s.opening),this.$menu.trigger(p.closing),"close";},e[r].prototype[l+"_initBlocker"]=function(){var o=this;a.$blck||(a.$blck=e('<div id="'+s.blocker+'" />').appendTo(a.$body)),a.$blck.off(p.touchstart).on(p.touchstart,function(e){e.preventDefault(),e.stopPropagation(),a.$blck.trigger(p.mousedown);}).on(p.mousedown,function(e){e.preventDefault(),a.$html.hasClass(s.modal)||o.close();});},e[r].prototype[l+"_initPage"]=function(o){o||(o=e(this.conf[l].pageSelector,a.$body),o.length>1&&(e[r].debug("Multiple nodes found for the page-node, all nodes are wrapped in one <"+this.conf[l].pageNodetype+">."),o=o.wrapAll("<"+this.conf[l].pageNodetype+" />").parent())),o.addClass(s.page),a.$page=o;},e[r].prototype[l+"_initOpenClose"]=function(){var o=this,t=this.$menu.attr("id");t&&t.length&&(this.conf.clone&&(t=s.umm(t)),e('a[href="#'+t+'"]').off(p.click).on(p.click,function(e){e.preventDefault(),o.open();}));var t=a.$page.attr("id");t&&t.length&&e('a[href="#'+t+'"]').on(p.click,function(e){e.preventDefault(),o.close();});},e[r].prototype[l+"_bindCustomEvents"]=function(){var e=this,o=p.open+" "+p.opening+" "+p.opened+" "+p.close+" "+p.closing+" "+p.closed+" "+p.setPage;this.$menu.off(o).on(o,function(e){e.stopPropagation();}),this.$menu.on(p.open,function(){e.open();}).on(p.close,function(){e.close();}).on(p.setPage,function(o,t){e[l+"_initPage"](t),e[l+"_initOpenClose"]();});};}(jQuery);
   /* Ende jquery.mmenu */
   /* Start jquery.mmenu.searchfield */
   /*!
 * jQuery mmenu searchfield addon
 * mmenu.frebsite.nl
 *
 * Copyright (c) Fred Heusschen
 */
!function(e){function s(e){switch(e){case 9:case 16:case 17:case 18:case 37:case 38:case 39:case 40:return !0;}return !1;}function n(s){return"boolean"==typeof s&&(s={add:s,search:s}),"object"!=typeof s&&(s={}),s=e.extend(!0,{},e[r].defaults[o],s),"boolean"!=typeof s.showLinksOnly&&(s.showLinksOnly="menu"==s.addTo),s;}function t(e){return e;}function a(){c=!0,i=e[r]._c,l=e[r]._d,d=e[r]._e,i.add("search hassearch noresultsmsg noresults nosubresults"),d.add("search reset change"),h=e[r].glbl;}var r="mmenu",o="searchfield";e[r].prototype["_init_"+o]=function(r){c||a();var h=this.vars[o+"_added"];this.vars[o+"_added"]=!0,h||(this.opts[o]=n(this.opts[o]),this.conf[o]=t(this.conf[o]));var u=this,f=this.opts[o];if(this.conf[o],f.add){switch(f.addTo){case"menu":var p=this.$menu;break;case"panels":var p=r;break;default:var p=e(f.addTo,this.$menu).filter("."+i.panel);}p.length&&p.each(function(){var s=e(this),n=s.is("."+i.list)?"li":"div";if(!s.children(n+"."+i.search).length){if(s.is("."+i.menu)){var t=u.$menu,a="prependTo";}else{var t=s.children().first(),a=t.is("."+i.subtitle)?"insertAfter":"insertBefore";}e("<"+n+' class="'+i.search+'" />').append('<input placeholder="'+f.placeholder+'" type="text" autocomplete="off" />')[a](t);}f.noResults&&(s.is("."+i.menu)&&(s=s.children("."+i.panel).first()),n=s.is("."+i.list)?"li":"div",s.children(n+"."+i.noresultsmsg).length||e("<"+n+' class="'+i.noresultsmsg+'" />').html(f.noResults).appendTo(s));});}if(this.$menu.children("div."+i.search).length&&this.$menu.addClass(i.hassearch),f.search){var v=e("."+i.search,this.$menu);v.length&&v.each(function(){var n=e(this);if("menu"==f.addTo){var t=e("."+i.panel,u.$menu),a=u.$menu;}else{var t=n.closest("."+i.panel),a=t;}var r=n.children("input"),o=u.__findAddBack(t,"."+i.list).children("li"),h=o.filter("."+i.label),c=o.not("."+i.subtitle).not("."+i.label).not("."+i.search).not("."+i.noresultsmsg),p="> a";f.showLinksOnly||(p+=", > span"),r.off(d.keyup+" "+d.change).on(d.keyup,function(e){s(e.keyCode)||n.trigger(d.search);}).on(d.change,function(){n.trigger(d.search);}),n.off(d.reset+" "+d.search).on(d.reset+" "+d.search,function(e){e.stopPropagation();}).on(d.reset,function(){n.trigger(d.search,[""]);}).on(d.search,function(s,n){"string"==typeof n?r.val(n):n=r.val(),n=n.toLowerCase(),t.scrollTop(0),c.add(h).addClass(i.hidden),c.each(function(){var s=e(this);e(p,s).text().toLowerCase().indexOf(n)>-1&&s.add(s.prevAll("."+i.label).first()).removeClass(i.hidden);}),e(t.get().reverse()).each(function(s){var n=e(this),t=n.data(l.parent);if(t){var a=n.add(n.find("> ."+i.list)).find("> li").not("."+i.subtitle).not("."+i.search).not("."+i.noresultsmsg).not("."+i.label).not("."+i.hidden);a.length?t.removeClass(i.hidden).removeClass(i.nosubresults).prevAll("."+i.label).first().removeClass(i.hidden):"menu"==f.addTo&&(n.hasClass(i.opened)&&setTimeout(function(){t.trigger(d.open);},1.5*(s+1)*u.conf.openingInterval),t.addClass(i.nosubresults));}}),a[c.not("."+i.hidden).length?"removeClass":"addClass"](i.noresults),u._update();});});}},e[r].addons.push(o),e[r].defaults[o]={add:!1,addTo:"menu",search:!1,placeholder:"Search",noResults:"No results found."};var i,l,d,h,c=!1;}(jQuery);
   /* Ende jquery.mmenu.searchfield */
   /* Start mediaelement */
   /*!
* MediaElement.js
* HTML5 <video> and <audio> shim and player
* http://mediaelementjs.com/
*
* Creates a JavaScript object that mimics HTML5 MediaElement API
* for browsers that don't understand HTML5 or can't play the provided codec
* Can play MP4 (H.264), Ogg, WebM, FLV, WMV, WMA, ACC, and MP3
*
* Copyright 2010-2014, John Dyer (http://j.hn)
* License: MIT
*
*/
var mejs=mejs||{};mejs.version="2.15.1";mejs.meIndex=0;mejs.plugins={silverlight:[{version:[3,0],types:["video/mp4","video/m4v","video/mov","video/wmv","audio/wma","audio/m4a","audio/mp3","audio/wav","audio/mpeg"]}],flash:[{version:[9,0,124],types:["video/mp4","video/m4v","video/mov","video/flv","video/rtmp","video/x-flv","audio/flv","audio/x-flv","audio/mp3","audio/m4a","audio/mpeg","video/youtube","video/x-youtube","application/x-mpegURL"]}],youtube:[{version:null,types:["video/youtube","video/x-youtube","audio/youtube","audio/x-youtube"]}],vimeo:[{version:null,types:["video/vimeo","video/x-vimeo"]}]};mejs.Utility={encodeUrl:function(url){return encodeURIComponent(url);},escapeHTML:function(s){return s.toString().split("&").join("&amp;").split("<").join("&lt;").split('"').join("&quot;");},absolutizeUrl:function(url){var el=document.createElement("div");el.innerHTML='<a href="'+this.escapeHTML(url)+'">x</a>';return el.firstChild.href;},getScriptPath:function(scriptNames){var i=0,j,codePath="",testname="",slashPos,filenamePos,scriptUrl,scriptPath,scriptFilename,scripts=document.getElementsByTagName("script"),il=scripts.length,jl=scriptNames.length;for(;i<il;i++){scriptUrl=scripts[i].src;slashPos=scriptUrl.lastIndexOf("/");if(slashPos>-1){scriptFilename=scriptUrl.substring(slashPos+1);scriptPath=scriptUrl.substring(0,slashPos+1);}else{scriptFilename=scriptUrl;scriptPath="";}for(j=0;j<jl;j++){testname=scriptNames[j];filenamePos=scriptFilename.indexOf(testname);if(filenamePos>-1){codePath=scriptPath;break;}}if(codePath!==""){break;}}return codePath;},secondsToTimeCode:function(time,forceHours,showFrameCount,fps){if(typeof showFrameCount=="undefined"){showFrameCount=false;}else{if(typeof fps=="undefined"){fps=25;}}var hours=Math.floor(time/3600)%24,minutes=Math.floor(time/60)%60,seconds=Math.floor(time%60),frames=Math.floor(((time%1)*fps).toFixed(3)),result=((forceHours||hours>0)?(hours<10?"0"+hours:hours)+":":"")+(minutes<10?"0"+minutes:minutes)+":"+(seconds<10?"0"+seconds:seconds)+((showFrameCount)?":"+(frames<10?"0"+frames:frames):"");return result;},timeCodeToSeconds:function(hh_mm_ss_ff,forceHours,showFrameCount,fps){if(typeof showFrameCount=="undefined"){showFrameCount=false;}else{if(typeof fps=="undefined"){fps=25;}}var tc_array=hh_mm_ss_ff.split(":"),tc_hh=parseInt(tc_array[0],10),tc_mm=parseInt(tc_array[1],10),tc_ss=parseInt(tc_array[2],10),tc_ff=0,tc_in_seconds=0;if(showFrameCount){tc_ff=parseInt(tc_array[3])/fps;}tc_in_seconds=(tc_hh*3600)+(tc_mm*60)+tc_ss+tc_ff;return tc_in_seconds;},convertSMPTEtoSeconds:function(SMPTE){if(typeof SMPTE!="string"){return false;}SMPTE=SMPTE.replace(",",".");var secs=0,decimalLen=(SMPTE.indexOf(".")!=-1)?SMPTE.split(".")[1].length:0,multiplier=1;SMPTE=SMPTE.split(":").reverse();for(var i=0;i<SMPTE.length;i++){multiplier=1;if(i>0){multiplier=Math.pow(60,i);}secs+=Number(SMPTE[i])*multiplier;}return Number(secs.toFixed(decimalLen));},removeSwf:function(id){var obj=document.getElementById(id);if(obj&&/object|embed/i.test(obj.nodeName)){if(mejs.MediaFeatures.isIE){obj.style.display="none";(function(){if(obj.readyState==4){mejs.Utility.removeObjectInIE(id);}else{setTimeout(arguments.callee,10);}})();}else{obj.parentNode.removeChild(obj);}}},removeObjectInIE:function(id){var obj=document.getElementById(id);if(obj){for(var i in obj){if(typeof obj[i]=="function"){obj[i]=null;}}obj.parentNode.removeChild(obj);}}};mejs.PluginDetector={hasPluginVersion:function(plugin,v){var pv=this.plugins[plugin];v[1]=v[1]||0;v[2]=v[2]||0;return(pv[0]>v[0]||(pv[0]==v[0]&&pv[1]>v[1])||(pv[0]==v[0]&&pv[1]==v[1]&&pv[2]>=v[2]))?true:false;},nav:window.navigator,ua:window.navigator.userAgent.toLowerCase(),plugins:[],addPlugin:function(p,pluginName,mimeType,activeX,axDetect){this.plugins[p]=this.detectPlugin(pluginName,mimeType,activeX,axDetect);},detectPlugin:function(pluginName,mimeType,activeX,axDetect){var version=[0,0,0],description,i,ax;if(typeof(this.nav.plugins)!="undefined"&&typeof this.nav.plugins[pluginName]=="object"){description=this.nav.plugins[pluginName].description;if(description&&!(typeof this.nav.mimeTypes!="undefined"&&this.nav.mimeTypes[mimeType]&&!this.nav.mimeTypes[mimeType].enabledPlugin)){version=description.replace(pluginName,"").replace(/^\s+/,"").replace(/\sr/gi,".").split(".");for(i=0;i<version.length;i++){version[i]=parseInt(version[i].match(/\d+/),10);}}}else{if(typeof(window.ActiveXObject)!="undefined"){try{ax=new ActiveXObject(activeX);if(ax){version=axDetect(ax);}}catch(e){}}}return version;}};mejs.PluginDetector.addPlugin("flash","Shockwave Flash","application/x-shockwave-flash","ShockwaveFlash.ShockwaveFlash",function(ax){var version=[],d=ax.GetVariable("$version");if(d){d=d.split(" ")[1].split(",");version=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];}return version;});mejs.PluginDetector.addPlugin("silverlight","Silverlight Plug-In","application/x-silverlight-2","AgControl.AgControl",function(ax){var v=[0,0,0,0],loopMatch=function(ax,v,i,n){while(ax.isVersionSupported(v[0]+"."+v[1]+"."+v[2]+"."+v[3])){v[i]+=n;}v[i]-=n;};loopMatch(ax,v,0,1);loopMatch(ax,v,1,1);loopMatch(ax,v,2,10000);loopMatch(ax,v,2,1000);loopMatch(ax,v,2,100);loopMatch(ax,v,2,10);loopMatch(ax,v,2,1);loopMatch(ax,v,3,1);return v;});mejs.MediaFeatures={init:function(){var t=this,d=document,nav=mejs.PluginDetector.nav,ua=mejs.PluginDetector.ua.toLowerCase(),i,v,html5Elements=["source","track","audio","video"];t.isiPad=(ua.match(/ipad/i)!==null);t.isiPhone=(ua.match(/iphone/i)!==null);t.isiOS=t.isiPhone||t.isiPad;t.isAndroid=(ua.match(/android/i)!==null);t.isBustedAndroid=(ua.match(/android 2\.[12]/)!==null);t.isBustedNativeHTTPS=(location.protocol==="https:"&&(ua.match(/android [12]\./)!==null||ua.match(/macintosh.* version.* safari/)!==null));t.isIE=(nav.appName.toLowerCase().indexOf("microsoft")!=-1||nav.appName.toLowerCase().match(/trident/gi)!==null);t.isChrome=(ua.match(/chrome/gi)!==null);t.isChromium=(ua.match(/chromium/gi)!==null);t.isFirefox=(ua.match(/firefox/gi)!==null);t.isWebkit=(ua.match(/webkit/gi)!==null);t.isGecko=(ua.match(/gecko/gi)!==null)&&!t.isWebkit&&!t.isIE;t.isOpera=(ua.match(/opera/gi)!==null);t.hasTouch=("ontouchstart" in window);t.svg=!!document.createElementNS&&!!document.createElementNS("http://www.w3.org/2000/svg","svg").createSVGRect;for(i=0;i<html5Elements.length;i++){v=document.createElement(html5Elements[i]);}t.supportsMediaTag=(typeof v.canPlayType!=="undefined"||t.isBustedAndroid);try{v.canPlayType("video/mp4");}catch(e){t.supportsMediaTag=false;}t.hasSemiNativeFullScreen=(typeof v.webkitEnterFullscreen!=="undefined");t.hasNativeFullscreen=(typeof v.requestFullscreen!=="undefined");t.hasWebkitNativeFullScreen=(typeof v.webkitRequestFullScreen!=="undefined");t.hasMozNativeFullScreen=(typeof v.mozRequestFullScreen!=="undefined");t.hasMsNativeFullScreen=(typeof v.msRequestFullscreen!=="undefined");t.hasTrueNativeFullScreen=(t.hasWebkitNativeFullScreen||t.hasMozNativeFullScreen||t.hasMsNativeFullScreen);t.nativeFullScreenEnabled=t.hasTrueNativeFullScreen;if(t.hasMozNativeFullScreen){t.nativeFullScreenEnabled=document.mozFullScreenEnabled;}else{if(t.hasMsNativeFullScreen){t.nativeFullScreenEnabled=document.msFullscreenEnabled;}}if(t.isChrome){t.hasSemiNativeFullScreen=false;}if(t.hasTrueNativeFullScreen){t.fullScreenEventName="";if(t.hasWebkitNativeFullScreen){t.fullScreenEventName="webkitfullscreenchange";}else{if(t.hasMozNativeFullScreen){t.fullScreenEventName="mozfullscreenchange";}else{if(t.hasMsNativeFullScreen){t.fullScreenEventName="MSFullscreenChange";}}}t.isFullScreen=function(){if(t.hasMozNativeFullScreen){return d.mozFullScreen;}else{if(t.hasWebkitNativeFullScreen){return d.webkitIsFullScreen;}else{if(t.hasMsNativeFullScreen){return d.msFullscreenElement!==null;}}}};t.requestFullScreen=function(el){if(t.hasWebkitNativeFullScreen){el.webkitRequestFullScreen();}else{if(t.hasMozNativeFullScreen){el.mozRequestFullScreen();}else{if(t.hasMsNativeFullScreen){el.msRequestFullscreen();}}}};t.cancelFullScreen=function(){if(t.hasWebkitNativeFullScreen){document.webkitCancelFullScreen();}else{if(t.hasMozNativeFullScreen){document.mozCancelFullScreen();}else{if(t.hasMsNativeFullScreen){document.msExitFullscreen();}}}};}if(t.hasSemiNativeFullScreen&&ua.match(/mac os x 10_5/i)){t.hasNativeFullScreen=false;t.hasSemiNativeFullScreen=false;}}};mejs.MediaFeatures.init();mejs.HtmlMediaElement={pluginType:"native",isFullScreen:false,setCurrentTime:function(time){this.currentTime=time;},setMuted:function(muted){this.muted=muted;},setVolume:function(volume){this.volume=volume;},stop:function(){this.pause();},setSrc:function(url){var existingSources=this.getElementsByTagName("source");while(existingSources.length>0){this.removeChild(existingSources[0]);}if(typeof url=="string"){this.src=url;}else{var i,media;for(i=0;i<url.length;i++){media=url[i];if(this.canPlayType(media.type)){this.src=media.src;break;}}}},setVideoSize:function(width,height){this.width=width;this.height=height;}};mejs.PluginMediaElement=function(pluginid,pluginType,mediaUrl){this.id=pluginid;this.pluginType=pluginType;this.src=mediaUrl;this.events={};this.attributes={};};mejs.PluginMediaElement.prototype={pluginElement:null,pluginType:"",isFullScreen:false,playbackRate:-1,defaultPlaybackRate:-1,seekable:[],played:[],paused:true,ended:false,seeking:false,duration:0,error:null,tagName:"",muted:false,volume:1,currentTime:0,play:function(){if(this.pluginApi!=null){if(this.pluginType=="youtube"||this.pluginType=="vimeo"){this.pluginApi.playVideo();}else{this.pluginApi.playMedia();}this.paused=false;}},load:function(){if(this.pluginApi!=null){if(this.pluginType=="youtube"||this.pluginType=="vimeo"){}else{this.pluginApi.loadMedia();}this.paused=false;}},pause:function(){if(this.pluginApi!=null){if(this.pluginType=="youtube"||this.pluginType=="vimeo"){this.pluginApi.pauseVideo();}else{this.pluginApi.pauseMedia();}this.paused=true;}},stop:function(){if(this.pluginApi!=null){if(this.pluginType=="youtube"||this.pluginType=="vimeo"){this.pluginApi.stopVideo();}else{this.pluginApi.stopMedia();}this.paused=true;}},canPlayType:function(type){var i,j,pluginInfo,pluginVersions=mejs.plugins[this.pluginType];for(i=0;i<pluginVersions.length;i++){pluginInfo=pluginVersions[i];if(mejs.PluginDetector.hasPluginVersion(this.pluginType,pluginInfo.version)){for(j=0;j<pluginInfo.types.length;j++){if(type==pluginInfo.types[j]){return"probably";}}}}return"";},positionFullscreenButton:function(x,y,visibleAndAbove){if(this.pluginApi!=null&&this.pluginApi.positionFullscreenButton){this.pluginApi.positionFullscreenButton(Math.floor(x),Math.floor(y),visibleAndAbove);}},hideFullscreenButton:function(){if(this.pluginApi!=null&&this.pluginApi.hideFullscreenButton){this.pluginApi.hideFullscreenButton();}},setSrc:function(url){if(typeof url=="string"){this.pluginApi.setSrc(mejs.Utility.absolutizeUrl(url));this.src=mejs.Utility.absolutizeUrl(url);}else{var i,media;for(i=0;i<url.length;i++){media=url[i];if(this.canPlayType(media.type)){this.pluginApi.setSrc(mejs.Utility.absolutizeUrl(media.src));this.src=mejs.Utility.absolutizeUrl(url);break;}}}},setCurrentTime:function(time){if(this.pluginApi!=null){if(this.pluginType=="youtube"||this.pluginType=="vimeo"){this.pluginApi.seekTo(time);}else{this.pluginApi.setCurrentTime(time);}this.currentTime=time;}},setVolume:function(volume){if(this.pluginApi!=null){if(this.pluginType=="youtube"){this.pluginApi.setVolume(volume*100);}else{this.pluginApi.setVolume(volume);}this.volume=volume;}},setMuted:function(muted){if(this.pluginApi!=null){if(this.pluginType=="youtube"){if(muted){this.pluginApi.mute();}else{this.pluginApi.unMute();}this.muted=muted;this.dispatchEvent("volumechange");}else{this.pluginApi.setMuted(muted);}this.muted=muted;}},setVideoSize:function(width,height){if(this.pluginElement&&this.pluginElement.style){this.pluginElement.style.width=width+"px";this.pluginElement.style.height=height+"px";}if(this.pluginApi!=null&&this.pluginApi.setVideoSize){this.pluginApi.setVideoSize(width,height);}},setFullscreen:function(fullscreen){if(this.pluginApi!=null&&this.pluginApi.setFullscreen){this.pluginApi.setFullscreen(fullscreen);}},enterFullScreen:function(){if(this.pluginApi!=null&&this.pluginApi.setFullscreen){this.setFullscreen(true);}},exitFullScreen:function(){if(this.pluginApi!=null&&this.pluginApi.setFullscreen){this.setFullscreen(false);}},addEventListener:function(eventName,callback,bubble){this.events[eventName]=this.events[eventName]||[];this.events[eventName].push(callback);},removeEventListener:function(eventName,callback){if(!eventName){this.events={};return true;}var callbacks=this.events[eventName];if(!callbacks){return true;}if(!callback){this.events[eventName]=[];return true;}for(var i=0;i<callbacks.length;i++){if(callbacks[i]===callback){this.events[eventName].splice(i,1);return true;}}return false;},dispatchEvent:function(eventName){var i,args,callbacks=this.events[eventName];if(callbacks){args=Array.prototype.slice.call(arguments,1);for(i=0;i<callbacks.length;i++){callbacks[i].apply(null,args);}}},hasAttribute:function(name){return(name in this.attributes);},removeAttribute:function(name){delete this.attributes[name];},getAttribute:function(name){if(this.hasAttribute(name)){return this.attributes[name];}return"";},setAttribute:function(name,value){this.attributes[name]=value;},remove:function(){mejs.Utility.removeSwf(this.pluginElement.id);mejs.MediaPluginBridge.unregisterPluginElement(this.pluginElement.id);}};mejs.MediaPluginBridge={pluginMediaElements:{},htmlMediaElements:{},registerPluginElement:function(id,pluginMediaElement,htmlMediaElement){this.pluginMediaElements[id]=pluginMediaElement;this.htmlMediaElements[id]=htmlMediaElement;},unregisterPluginElement:function(id){delete this.pluginMediaElements[id];delete this.htmlMediaElements[id];},initPlugin:function(id){var pluginMediaElement=this.pluginMediaElements[id],htmlMediaElement=this.htmlMediaElements[id];if(pluginMediaElement){switch(pluginMediaElement.pluginType){case"flash":pluginMediaElement.pluginElement=pluginMediaElement.pluginApi=document.getElementById(id);break;case"silverlight":pluginMediaElement.pluginElement=document.getElementById(pluginMediaElement.id);pluginMediaElement.pluginApi=pluginMediaElement.pluginElement.Content.MediaElementJS;break;}if(pluginMediaElement.pluginApi!=null&&pluginMediaElement.success){pluginMediaElement.success(pluginMediaElement,htmlMediaElement);}}},fireEvent:function(id,eventName,values){var e,i,bufferedTime,pluginMediaElement=this.pluginMediaElements[id];if(!pluginMediaElement){return;}e={type:eventName,target:pluginMediaElement};for(i in values){pluginMediaElement[i]=values[i];e[i]=values[i];}bufferedTime=values.bufferedTime||0;e.target.buffered=e.buffered={start:function(index){return 0;},end:function(index){return bufferedTime;},length:1};pluginMediaElement.dispatchEvent(e.type,e);}};mejs.MediaElementDefaults={mode:"auto",plugins:["flash","silverlight","youtube","vimeo"],enablePluginDebug:false,httpsBasicAuthSite:false,type:"",pluginPath:mejs.Utility.getScriptPath(["mediaelement.js","mediaelement.min.js","mediaelement-and-player.js","mediaelement-and-player.min.js"]),flashName:"flashmediaelement.swf",flashStreamer:"",enablePluginSmoothing:false,enablePseudoStreaming:false,pseudoStreamingStartQueryParam:"start",silverlightName:"silverlightmediaelement.xap",defaultVideoWidth:480,defaultVideoHeight:270,pluginWidth:-1,pluginHeight:-1,pluginVars:[],timerRate:250,startVolume:0.8,success:function(){},error:function(){}};mejs.MediaElement=function(el,o){return mejs.HtmlMediaElementShim.create(el,o);};mejs.HtmlMediaElementShim={create:function(el,o){var options=mejs.MediaElementDefaults,htmlMediaElement=(typeof(el)=="string")?document.getElementById(el):el,tagName=htmlMediaElement.tagName.toLowerCase(),isMediaTag=(tagName==="audio"||tagName==="video"),src=(isMediaTag)?htmlMediaElement.getAttribute("src"):htmlMediaElement.getAttribute("href"),poster=htmlMediaElement.getAttribute("poster"),autoplay=htmlMediaElement.getAttribute("autoplay"),preload=htmlMediaElement.getAttribute("preload"),controls=htmlMediaElement.getAttribute("controls"),playback,prop;for(prop in o){options[prop]=o[prop];}src=(typeof src=="undefined"||src===null||src=="")?null:src;poster=(typeof poster=="undefined"||poster===null)?"":poster;preload=(typeof preload=="undefined"||preload===null||preload==="false")?"none":preload;autoplay=!(typeof autoplay=="undefined"||autoplay===null||autoplay==="false");controls=!(typeof controls=="undefined"||controls===null||controls==="false");playback=this.determinePlayback(htmlMediaElement,options,mejs.MediaFeatures.supportsMediaTag,isMediaTag,src);playback.url=(playback.url!==null)?mejs.Utility.absolutizeUrl(playback.url):"";if(playback.method=="native"){if(mejs.MediaFeatures.isBustedAndroid){htmlMediaElement.src=playback.url;htmlMediaElement.addEventListener("click",function(){htmlMediaElement.play();},false);}return this.updateNative(playback,options,autoplay,preload);}else{if(playback.method!==""){return this.createPlugin(playback,options,poster,autoplay,preload,controls);}else{this.createErrorMessage(playback,options,poster);return this;}}},determinePlayback:function(htmlMediaElement,options,supportsMediaTag,isMediaTag,src){var mediaFiles=[],i,j,k,l,n,type,result={method:"",url:"",htmlMediaElement:htmlMediaElement,isVideo:(htmlMediaElement.tagName.toLowerCase()!="audio")},pluginName,pluginVersions,pluginInfo,dummy,media;if(typeof options.type!="undefined"&&options.type!==""){if(typeof options.type=="string"){mediaFiles.push({type:options.type,url:src});}else{for(i=0;i<options.type.length;i++){mediaFiles.push({type:options.type[i],url:src});}}}else{if(src!==null){type=this.formatType(src,htmlMediaElement.getAttribute("type"));mediaFiles.push({type:type,url:src});}else{for(i=0;i<htmlMediaElement.childNodes.length;i++){n=htmlMediaElement.childNodes[i];if(n.nodeType==1&&n.tagName.toLowerCase()=="source"){src=n.getAttribute("src");type=this.formatType(src,n.getAttribute("type"));media=n.getAttribute("media");if(!media||!window.matchMedia||(window.matchMedia&&window.matchMedia(media).matches)){mediaFiles.push({type:type,url:src});}}}}}if(!isMediaTag&&mediaFiles.length>0&&mediaFiles[0].url!==null&&this.getTypeFromFile(mediaFiles[0].url).indexOf("audio")>-1){result.isVideo=false;}if(mejs.MediaFeatures.isBustedAndroid){htmlMediaElement.canPlayType=function(type){return(type.match(/video\/(mp4|m4v)/gi)!==null)?"maybe":"";};}if(mejs.MediaFeatures.isChromium){htmlMediaElement.canPlayType=function(type){return(type.match(/video\/(webm|ogv|ogg)/gi)!==null)?"maybe":"";};}if(supportsMediaTag&&(options.mode==="auto"||options.mode==="auto_plugin"||options.mode==="native")&&!(mejs.MediaFeatures.isBustedNativeHTTPS&&options.httpsBasicAuthSite===true)){if(!isMediaTag){dummy=document.createElement(result.isVideo?"video":"audio");htmlMediaElement.parentNode.insertBefore(dummy,htmlMediaElement);htmlMediaElement.style.display="none";result.htmlMediaElement=htmlMediaElement=dummy;}for(i=0;i<mediaFiles.length;i++){if(mediaFiles[i].type=="video/m3u8"||htmlMediaElement.canPlayType(mediaFiles[i].type).replace(/no/,"")!==""||htmlMediaElement.canPlayType(mediaFiles[i].type.replace(/mp3/,"mpeg")).replace(/no/,"")!==""||htmlMediaElement.canPlayType(mediaFiles[i].type.replace(/m4a/,"mp4")).replace(/no/,"")!==""){result.method="native";result.url=mediaFiles[i].url;break;}}if(result.method==="native"){if(result.url!==null){htmlMediaElement.src=result.url;}if(options.mode!=="auto_plugin"){return result;}}}if(options.mode==="auto"||options.mode==="auto_plugin"||options.mode==="shim"){for(i=0;i<mediaFiles.length;i++){type=mediaFiles[i].type;for(j=0;j<options.plugins.length;j++){pluginName=options.plugins[j];pluginVersions=mejs.plugins[pluginName];for(k=0;k<pluginVersions.length;k++){pluginInfo=pluginVersions[k];if(pluginInfo.version==null||mejs.PluginDetector.hasPluginVersion(pluginName,pluginInfo.version)){for(l=0;l<pluginInfo.types.length;l++){if(type==pluginInfo.types[l]){result.method=pluginName;result.url=mediaFiles[i].url;return result;}}}}}}}if(options.mode==="auto_plugin"&&result.method==="native"){return result;}if(result.method===""&&mediaFiles.length>0){result.url=mediaFiles[0].url;}return result;},formatType:function(url,type){var ext;if(url&&!type){return this.getTypeFromFile(url);}else{if(type&&~type.indexOf(";")){return type.substr(0,type.indexOf(";"));}else{return type;}}},getTypeFromFile:function(url){url=url.split("?")[0];var ext=url.substring(url.lastIndexOf(".")+1).toLowerCase();return(/(mp4|m4v|ogg|ogv|m3u8|webm|webmv|flv|wmv|mpeg|mov)/gi.test(ext)?"video":"audio")+"/"+this.getTypeFromExtension(ext);},getTypeFromExtension:function(ext){switch(ext){case"mp4":case"m4v":case"m4a":return"mp4";case"webm":case"webma":case"webmv":return"webm";case"ogg":case"oga":case"ogv":return"ogg";default:return ext;}},createErrorMessage:function(playback,options,poster){var htmlMediaElement=playback.htmlMediaElement,errorContainer=document.createElement("div");errorContainer.className="me-cannotplay";try{errorContainer.style.width=htmlMediaElement.width+"px";errorContainer.style.height=htmlMediaElement.height+"px";}catch(e){}if(options.customError){errorContainer.innerHTML=options.customError;}else{errorContainer.innerHTML=(poster!=="")?'<a href="'+playback.url+'"><img src="'+poster+'" width="100%" height="100%" /></a>':'<a href="'+playback.url+'"><span>'+mejs.i18n.t("Download File")+"</span></a>";}htmlMediaElement.parentNode.insertBefore(errorContainer,htmlMediaElement);htmlMediaElement.style.display="none";options.error(htmlMediaElement);},createPlugin:function(playback,options,poster,autoplay,preload,controls){var htmlMediaElement=playback.htmlMediaElement,width=1,height=1,pluginid="me_"+playback.method+"_"+(mejs.meIndex++),pluginMediaElement=new mejs.PluginMediaElement(pluginid,playback.method,playback.url),container=document.createElement("div"),specialIEContainer,node,initVars;pluginMediaElement.tagName=htmlMediaElement.tagName;for(var i=0;i<htmlMediaElement.attributes.length;i++){var attribute=htmlMediaElement.attributes[i];if(attribute.specified==true){pluginMediaElement.setAttribute(attribute.name,attribute.value);}}node=htmlMediaElement.parentNode;while(node!==null&&node.tagName.toLowerCase()!=="body"&&node.parentNode!=null){if(node.parentNode.tagName.toLowerCase()==="p"){node.parentNode.parentNode.insertBefore(node,node.parentNode);break;}node=node.parentNode;}if(playback.isVideo){width=(options.pluginWidth>0)?options.pluginWidth:(options.videoWidth>0)?options.videoWidth:(htmlMediaElement.getAttribute("width")!==null)?htmlMediaElement.getAttribute("width"):options.defaultVideoWidth;height=(options.pluginHeight>0)?options.pluginHeight:(options.videoHeight>0)?options.videoHeight:(htmlMediaElement.getAttribute("height")!==null)?htmlMediaElement.getAttribute("height"):options.defaultVideoHeight;width=mejs.Utility.encodeUrl(width);height=mejs.Utility.encodeUrl(height);}else{if(options.enablePluginDebug){width=320;height=240;}}pluginMediaElement.success=options.success;mejs.MediaPluginBridge.registerPluginElement(pluginid,pluginMediaElement,htmlMediaElement);container.className="me-plugin";container.id=pluginid+"_container";if(playback.isVideo){htmlMediaElement.parentNode.insertBefore(container,htmlMediaElement);}else{document.body.insertBefore(container,document.body.childNodes[0]);}initVars=["id="+pluginid,"isvideo="+((playback.isVideo)?"true":"false"),"autoplay="+((autoplay)?"true":"false"),"preload="+preload,"width="+width,"startvolume="+options.startVolume,"timerrate="+options.timerRate,"flashstreamer="+options.flashStreamer,"height="+height,"pseudostreamstart="+options.pseudoStreamingStartQueryParam];if(playback.url!==null){if(playback.method=="flash"){initVars.push("file="+mejs.Utility.encodeUrl(playback.url));}else{initVars.push("file="+playback.url);}}if(options.enablePluginDebug){initVars.push("debug=true");}if(options.enablePluginSmoothing){initVars.push("smoothing=true");}if(options.enablePseudoStreaming){initVars.push("pseudostreaming=true");}if(controls){initVars.push("controls=true");}if(options.pluginVars){initVars=initVars.concat(options.pluginVars);}switch(playback.method){case"silverlight":container.innerHTML='<object data="data:application/x-silverlight-2," type="application/x-silverlight-2" id="'+pluginid+'" name="'+pluginid+'" width="'+width+'" height="'+height+'" class="mejs-shim">'+'<param name="initParams" value="'+initVars.join(",")+'" />'+'<param name="windowless" value="true" />'+'<param name="background" value="black" />'+'<param name="minRuntimeVersion" value="3.0.0.0" />'+'<param name="autoUpgrade" value="true" />'+'<param name="source" value="'+options.pluginPath+options.silverlightName+'" />'+"</object>";break;case"flash":if(mejs.MediaFeatures.isIE){specialIEContainer=document.createElement("div");container.appendChild(specialIEContainer);specialIEContainer.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="//download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab" '+'id="'+pluginid+'" width="'+width+'" height="'+height+'" class="mejs-shim">'+'<param name="movie" value="'+options.pluginPath+options.flashName+"?x="+(new Date())+'" />'+'<param name="flashvars" value="'+initVars.join("&amp;")+'" />'+'<param name="quality" value="high" />'+'<param name="bgcolor" value="#000000" />'+'<param name="wmode" value="transparent" />'+'<param name="allowScriptAccess" value="always" />'+'<param name="allowFullScreen" value="true" />'+'<param name="scale" value="default" />'+"</object>";}else{container.innerHTML='<embed id="'+pluginid+'" name="'+pluginid+'" '+'play="true" '+'loop="false" '+'quality="high" '+'bgcolor="#000000" '+'wmode="transparent" '+'allowScriptAccess="always" '+'allowFullScreen="true" '+'type="application/x-shockwave-flash" pluginspage="//www.macromedia.com/go/getflashplayer" '+'src="'+options.pluginPath+options.flashName+'" '+'flashvars="'+initVars.join("&")+'" '+'width="'+width+'" '+'height="'+height+'" '+'scale="default"'+'class="mejs-shim"></embed>';}break;case"youtube":var videoId;if(playback.url.lastIndexOf("youtu.be")!=-1){videoId=playback.url.substr(playback.url.lastIndexOf("/")+1);if(videoId.indexOf("?")!=-1){videoId=videoId.substr(0,videoId.indexOf("?"));}}else{videoId=playback.url.substr(playback.url.lastIndexOf("=")+1);}youtubeSettings={container:container,containerId:container.id,pluginMediaElement:pluginMediaElement,pluginId:pluginid,videoId:videoId,height:height,width:width};if(mejs.PluginDetector.hasPluginVersion("flash",[10,0,0])){mejs.YouTubeApi.createFlash(youtubeSettings);}else{mejs.YouTubeApi.enqueueIframe(youtubeSettings);}break;case"vimeo":var player_id=pluginid+"_player";pluginMediaElement.vimeoid=playback.url.substr(playback.url.lastIndexOf("/")+1);container.innerHTML='<iframe src="//player.vimeo.com/video/'+pluginMediaElement.vimeoid+"?api=1&portrait=0&byline=0&title=0&player_id="+player_id+'" width="'+width+'" height="'+height+'" frameborder="0" class="mejs-shim" id="'+player_id+'"></iframe>';if(typeof($f)=="function"){var player=$f(container.childNodes[0]);player.addEvent("ready",function(){$.extend(player,{playVideo:function(){player.api("play");},stopVideo:function(){player.api("unload");},pauseVideo:function(){player.api("pause");},seekTo:function(seconds){player.api("seekTo",seconds);},setVolume:function(volume){player.api("setVolume",volume);},setMuted:function(muted){if(muted){player.lastVolume=player.api("getVolume");player.api("setVolume",0);}else{player.api("setVolume",player.lastVolume);delete player.lastVolume;}}});function createEvent(player,pluginMediaElement,eventName,e){var obj={type:eventName,target:pluginMediaElement};if(eventName=="timeupdate"){pluginMediaElement.currentTime=obj.currentTime=e.seconds;pluginMediaElement.duration=obj.duration=e.duration;}pluginMediaElement.dispatchEvent(obj.type,obj);}player.addEvent("play",function(){createEvent(player,pluginMediaElement,"play");createEvent(player,pluginMediaElement,"playing");});player.addEvent("pause",function(){createEvent(player,pluginMediaElement,"pause");});player.addEvent("finish",function(){createEvent(player,pluginMediaElement,"ended");});player.addEvent("playProgress",function(e){createEvent(player,pluginMediaElement,"timeupdate",e);});pluginMediaElement.pluginElement=container;pluginMediaElement.pluginApi=player;mejs.MediaPluginBridge.initPlugin(pluginid);});}else{console.warn("You need to include froogaloop for vimeo to work");}break;}htmlMediaElement.style.display="none";htmlMediaElement.removeAttribute("autoplay");return pluginMediaElement;},updateNative:function(playback,options,autoplay,preload){var htmlMediaElement=playback.htmlMediaElement,m;for(m in mejs.HtmlMediaElement){htmlMediaElement[m]=mejs.HtmlMediaElement[m];}options.success(htmlMediaElement,htmlMediaElement);return htmlMediaElement;}};mejs.YouTubeApi={isIframeStarted:false,isIframeLoaded:false,loadIframeApi:function(){if(!this.isIframeStarted){var tag=document.createElement("script");tag.src="//www.youtube.com/player_api";var firstScriptTag=document.getElementsByTagName("script")[0];firstScriptTag.parentNode.insertBefore(tag,firstScriptTag);this.isIframeStarted=true;}},iframeQueue:[],enqueueIframe:function(yt){if(this.isLoaded){this.createIframe(yt);}else{this.loadIframeApi();this.iframeQueue.push(yt);}},createIframe:function(settings){var pluginMediaElement=settings.pluginMediaElement,player=new YT.Player(settings.containerId,{height:settings.height,width:settings.width,videoId:settings.videoId,playerVars:{controls:0},events:{"onReady":function(){settings.pluginMediaElement.pluginApi=player;mejs.MediaPluginBridge.initPlugin(settings.pluginId);setInterval(function(){mejs.YouTubeApi.createEvent(player,pluginMediaElement,"timeupdate");},250);},"onStateChange":function(e){mejs.YouTubeApi.handleStateChange(e.data,player,pluginMediaElement);}}});},createEvent:function(player,pluginMediaElement,eventName){var obj={type:eventName,target:pluginMediaElement};if(player&&player.getDuration){pluginMediaElement.currentTime=obj.currentTime=player.getCurrentTime();pluginMediaElement.duration=obj.duration=player.getDuration();obj.paused=pluginMediaElement.paused;obj.ended=pluginMediaElement.ended;obj.muted=player.isMuted();obj.volume=player.getVolume()/100;obj.bytesTotal=player.getVideoBytesTotal();obj.bufferedBytes=player.getVideoBytesLoaded();var bufferedTime=obj.bufferedBytes/obj.bytesTotal*obj.duration;obj.target.buffered=obj.buffered={start:function(index){return 0;},end:function(index){return bufferedTime;},length:1};}pluginMediaElement.dispatchEvent(obj.type,obj);},iFrameReady:function(){this.isLoaded=true;this.isIframeLoaded=true;while(this.iframeQueue.length>0){var settings=this.iframeQueue.pop();this.createIframe(settings);}},flashPlayers:{},createFlash:function(settings){this.flashPlayers[settings.pluginId]=settings;var specialIEContainer,youtubeUrl="//www.youtube.com/apiplayer?enablejsapi=1&amp;playerapiid="+settings.pluginId+"&amp;version=3&amp;autoplay=0&amp;controls=0&amp;modestbranding=1&loop=0";if(mejs.MediaFeatures.isIE){specialIEContainer=document.createElement("div");settings.container.appendChild(specialIEContainer);specialIEContainer.outerHTML='<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="//download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab" '+'id="'+settings.pluginId+'" width="'+settings.width+'" height="'+settings.height+'" class="mejs-shim">'+'<param name="movie" value="'+youtubeUrl+'" />'+'<param name="wmode" value="transparent" />'+'<param name="allowScriptAccess" value="always" />'+'<param name="allowFullScreen" value="true" />'+"</object>";}else{settings.container.innerHTML='<object type="application/x-shockwave-flash" id="'+settings.pluginId+'" data="'+youtubeUrl+'" '+'width="'+settings.width+'" height="'+settings.height+'" style="visibility: visible; " class="mejs-shim">'+'<param name="allowScriptAccess" value="always">'+'<param name="wmode" value="transparent">'+"</object>";}},flashReady:function(id){var settings=this.flashPlayers[id],player=document.getElementById(id),pluginMediaElement=settings.pluginMediaElement;pluginMediaElement.pluginApi=pluginMediaElement.pluginElement=player;mejs.MediaPluginBridge.initPlugin(id);player.cueVideoById(settings.videoId);var callbackName=settings.containerId+"_callback";window[callbackName]=function(e){mejs.YouTubeApi.handleStateChange(e,player,pluginMediaElement);};player.addEventListener("onStateChange",callbackName);setInterval(function(){mejs.YouTubeApi.createEvent(player,pluginMediaElement,"timeupdate");},250);mejs.YouTubeApi.createEvent(player,pluginMediaElement,"canplay");},handleStateChange:function(youTubeState,player,pluginMediaElement){switch(youTubeState){case -1:pluginMediaElement.paused=true;pluginMediaElement.ended=true;mejs.YouTubeApi.createEvent(player,pluginMediaElement,"loadedmetadata");break;case 0:pluginMediaElement.paused=false;pluginMediaElement.ended=true;mejs.YouTubeApi.createEvent(player,pluginMediaElement,"ended");break;case 1:pluginMediaElement.paused=false;pluginMediaElement.ended=false;mejs.YouTubeApi.createEvent(player,pluginMediaElement,"play");mejs.YouTubeApi.createEvent(player,pluginMediaElement,"playing");break;case 2:pluginMediaElement.paused=true;pluginMediaElement.ended=false;mejs.YouTubeApi.createEvent(player,pluginMediaElement,"pause");break;case 3:mejs.YouTubeApi.createEvent(player,pluginMediaElement,"progress");break;case 5:break;}}};function onYouTubePlayerAPIReady(){mejs.YouTubeApi.iFrameReady();}function onYouTubePlayerReady(id){mejs.YouTubeApi.flashReady(id);}window.mejs=mejs;window.MediaElement=mejs.MediaElement;
/*!
 * Adds Internationalization and localization to mediaelement.
 *
 * This file does not contain translations, you have to add the manually.
 * The schema is always the same: me-i18n-locale-[ISO_639-1 Code].js
 *
 * Examples are provided both for german and chinese translation.
 *
 *
 * What is the concept beyond i18n?
 *   http://en.wikipedia.org/wiki/Internationalization_and_localization
 *
 * What langcode should i use?
 *   http://en.wikipedia.org/wiki/ISO_639-1
 *
 *
 * License?
 *
 *   The i18n file uses methods from the Drupal project (drupal.js):
 *     - i18n.methods.t() (modified)
 *     - i18n.methods.checkPlain() (full copy)
 *
 *   The Drupal project is (like mediaelementjs) licensed under GPLv2.
 *    - http://drupal.org/licensing/faq/#q1
 *    - https://github.com/johndyer/mediaelement
 *    - http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 *
 * @author
 *   Tim Latz (latz.tim@gmail.com)
 *
 *
 * @params
 *  - context - document, iframe ..
 *  - exports - CommonJS, window ..
 *
 */
(function(context,exports,undefined){var i18n={"locale":{"language":"","strings":{}},"methods":{}};i18n.getLanguage=function(){var language=i18n.locale.language||window.navigator.userLanguage||window.navigator.language;return language.substr(0,2).toLowerCase();};if(typeof mejsL10n!="undefined"){i18n.locale.language=mejsL10n.language;}i18n.methods.checkPlain=function(str){var character,regex,replace={"&":"&amp;",'"':"&quot;","<":"&lt;",">":"&gt;"};str=String(str);for(character in replace){if(replace.hasOwnProperty(character)){regex=new RegExp(character,"g");str=str.replace(regex,replace[character]);}}return str;};i18n.methods.t=function(str,options){if(i18n.locale.strings&&i18n.locale.strings[options.context]&&i18n.locale.strings[options.context][str]){str=i18n.locale.strings[options.context][str];}return i18n.methods.checkPlain(str);};i18n.t=function(str,options){if(typeof str==="string"&&str.length>0){var language=i18n.getLanguage();options=options||{"context":language};return i18n.methods.t(str,options);}else{throw {"name":"InvalidArgumentException","message":"First argument is either not a string or empty."};}};exports.i18n=i18n;}(document,mejs));(function(exports,undefined){if(typeof mejsL10n!="undefined"){exports[mejsL10n.language]=mejsL10n.strings;}}(mejs.i18n.locale.strings));
/*!
 * This is a i18n.locale language object.
 *
 * German translation by Tim Latz, latz.tim@gmail.com
 *
 * @author
 *   Tim Latz (latz.tim@gmail.com)
 *
 * @see
 *   me-i18n.js
 *
 * @params
 *  - exports - CommonJS, window ..
 */
(function(exports,undefined){if(typeof exports.de==="undefined"){exports.de={"Fullscreen":"Vollbild","Go Fullscreen":"Vollbild an","Turn off Fullscreen":"Vollbild aus","Close":"Schließen"};}}(mejs.i18n.locale.strings));
/*!
 * This is a i18n.locale language object.
 *
 * Traditional chinese translation by Tim Latz, latz.tim@gmail.com
 *
 * @author
 *   Tim Latz (latz.tim@gmail.com)
 *
 * @see
 *   me-i18n.js
 *
 * @params
 *  - exports - CommonJS, window ..
 */
(function(exports,undefined){if(typeof exports.zh==="undefined"){exports.zh={"Fullscreen":"全螢幕","Go Fullscreen":"全�?模�?","Turn off Fullscreen":"退出全�?模�?","Close":"關閉"};}}(mejs.i18n.locale.strings));
   /* Ende mediaelement */
   /* Start mediaelement_i18n_de */
   (function(exports,undefined){exports.de={"None":"Entfällt","Unmute":"Lautstärke einschalten","Fullscreen":"Vollbild","Download File":"Download der Datei","Mute Toggle":"Stummschalten","Play/Pause":"Abspielen/Pause","Captions/Subtitles":"Unterschriften/Untertitel","Download Video":"Video herunterladen","Turn off Fullscreen":"Vollbild abschalten","Go Fullscreen":"Vollbild einschalten","Close":"Schließen"};}(mejs.i18n.locale.strings));
   /* Ende mediaelement_i18n_de */
   /* Start mediaelementplayer */
   /*!
 * MediaElementPlayer
 * http://mediaelementjs.com/
 *
 * Creates a controller bar for HTML5 <video> add <audio> tags
 * using jQuery and MediaElement.js (HTML5 Flash/Silverlight wrapper)
 *
 * Copyright 2010-2013, John Dyer (http://j.hn/)
 * License: MIT
 *
 */
if(typeof jQuery!="undefined"){mejs.$=jQuery;}else{if(typeof ender!="undefined"){mejs.$=ender;}}(function($){mejs.MepDefaults={poster:"",showPosterWhenEnded:false,defaultVideoWidth:480,defaultVideoHeight:270,videoWidth:-1,videoHeight:-1,defaultAudioWidth:400,defaultAudioHeight:30,defaultSeekBackwardInterval:function(media){return(media.duration*0.05);},defaultSeekForwardInterval:function(media){return(media.duration*0.05);},setDimensions:true,audioWidth:-1,audioHeight:-1,startVolume:0.8,loop:false,autoRewind:true,enableAutosize:true,alwaysShowHours:false,showTimecodeFrameCount:false,framesPerSecond:25,autosizeProgress:true,alwaysShowControls:false,hideVideoControlsOnLoad:false,clickToPlayPause:true,iPadUseNativeControls:false,iPhoneUseNativeControls:false,AndroidUseNativeControls:false,features:["playpause","current","progress","duration","tracks","volume","fullscreen"],isVideo:true,enableKeyboard:true,pauseOtherPlayers:true,keyActions:[{keys:[32,179],action:function(player,media){if(media.paused||media.ended){player.play();}else{player.pause();}}},{keys:[38],action:function(player,media){player.container.find(".mejs-volume-slider").css("display","block");if(player.isVideo){player.showControls();player.startControlsTimer();}var newVolume=Math.min(media.volume+0.1,1);media.setVolume(newVolume);}},{keys:[40],action:function(player,media){player.container.find(".mejs-volume-slider").css("display","block");if(player.isVideo){player.showControls();player.startControlsTimer();}var newVolume=Math.max(media.volume-0.1,0);media.setVolume(newVolume);}},{keys:[37,227],action:function(player,media){if(!isNaN(media.duration)&&media.duration>0){if(player.isVideo){player.showControls();player.startControlsTimer();}var newTime=Math.max(media.currentTime-player.options.defaultSeekBackwardInterval(media),0);media.setCurrentTime(newTime);}}},{keys:[39,228],action:function(player,media){if(!isNaN(media.duration)&&media.duration>0){if(player.isVideo){player.showControls();player.startControlsTimer();}var newTime=Math.min(media.currentTime+player.options.defaultSeekForwardInterval(media),media.duration);media.setCurrentTime(newTime);}}},{keys:[70],action:function(player,media){if(typeof player.enterFullScreen!="undefined"){if(player.isFullScreen){player.exitFullScreen();}else{player.enterFullScreen();}}}},{keys:[77],action:function(player,media){player.container.find(".mejs-volume-slider").css("display","block");if(player.isVideo){player.showControls();player.startControlsTimer();}if(player.media.muted){player.setMuted(false);}else{player.setMuted(true);}}}]};mejs.mepIndex=0;mejs.players={};mejs.MediaElementPlayer=function(node,o){if(!(this instanceof mejs.MediaElementPlayer)){return new mejs.MediaElementPlayer(node,o);}var t=this;t.$media=t.$node=$(node);t.node=t.media=t.$media[0];if(typeof t.node.player!="undefined"){return t.node.player;}else{t.node.player=t;}if(typeof o=="undefined"){o=t.$node.data("mejsoptions");}t.options=$.extend({},mejs.MepDefaults,o);t.id="mep_"+mejs.mepIndex++;mejs.players[t.id]=t;t.init();return t;};mejs.MediaElementPlayer.prototype={hasFocus:false,controlsAreVisible:true,init:function(){var t=this,mf=mejs.MediaFeatures,meOptions=$.extend(true,{},t.options,{success:function(media,domNode){t.meReady(media,domNode);},error:function(e){t.handleError(e);}}),tagName=t.media.tagName.toLowerCase();t.isDynamic=(tagName!=="audio"&&tagName!=="video");if(t.isDynamic){t.isVideo=t.options.isVideo;}else{t.isVideo=(tagName!=="audio"&&t.options.isVideo);}if((mf.isiPad&&t.options.iPadUseNativeControls)||(mf.isiPhone&&t.options.iPhoneUseNativeControls)){t.$media.attr("controls","controls");if(mf.isiPad&&t.media.getAttribute("autoplay")!==null){t.play();}}else{if(mf.isAndroid&&t.options.AndroidUseNativeControls){}else{t.$media.removeAttr("controls");t.container=$('<div id="'+t.id+'" class="mejs-container '+(mejs.MediaFeatures.svg?"svg":"no-svg")+'">'+'<div class="mejs-inner">'+'<div class="mejs-mediaelement"></div>'+'<div class="mejs-layers"></div>'+'<div class="mejs-controls"></div>'+'<div class="mejs-clear"></div>'+"</div>"+"</div>").addClass(t.$media[0].className).insertBefore(t.$media);t.container.addClass((mf.isAndroid?"mejs-android ":"")+(mf.isiOS?"mejs-ios ":"")+(mf.isiPad?"mejs-ipad ":"")+(mf.isiPhone?"mejs-iphone ":"")+(t.isVideo?"mejs-video ":"mejs-audio "));if(mf.isiOS){var $newMedia=t.$media.clone();t.container.find(".mejs-mediaelement").append($newMedia);t.$media.remove();t.$node=t.$media=$newMedia;t.node=t.media=$newMedia[0];}else{t.container.find(".mejs-mediaelement").append(t.$media);}t.controls=t.container.find(".mejs-controls");t.layers=t.container.find(".mejs-layers");var tagType=(t.isVideo?"video":"audio"),capsTagName=tagType.substring(0,1).toUpperCase()+tagType.substring(1);if(t.options[tagType+"Width"]>0||t.options[tagType+"Width"].toString().indexOf("%")>-1){t.width=t.options[tagType+"Width"];}else{if(t.media.style.width!==""&&t.media.style.width!==null){t.width=t.media.style.width;}else{if(t.media.getAttribute("width")!==null){t.width=t.$media.attr("width");}else{t.width=t.options["default"+capsTagName+"Width"];}}}if(t.options[tagType+"Height"]>0||t.options[tagType+"Height"].toString().indexOf("%")>-1){t.height=t.options[tagType+"Height"];}else{if(t.media.style.height!==""&&t.media.style.height!==null){t.height=t.media.style.height;}else{if(t.$media[0].getAttribute("height")!==null){t.height=t.$media.attr("height");}else{t.height=t.options["default"+capsTagName+"Height"];}}}t.setPlayerSize(t.width,t.height);meOptions.pluginWidth=t.width;meOptions.pluginHeight=t.height;}}mejs.MediaElement(t.$media[0],meOptions);if(typeof(t.container)!="undefined"&&t.controlsAreVisible){t.container.trigger("controlsshown");}},showControls:function(doAnimation){var t=this;doAnimation=typeof doAnimation=="undefined"||doAnimation;if(t.controlsAreVisible){return;}if(doAnimation){t.controls.css("visibility","visible").stop(true,true).fadeIn(200,function(){t.controlsAreVisible=true;t.container.trigger("controlsshown");});t.container.find(".mejs-control").css("visibility","visible").stop(true,true).fadeIn(200,function(){t.controlsAreVisible=true;});}else{t.controls.css("visibility","visible").css("display","block");t.container.find(".mejs-control").css("visibility","visible").css("display","block");t.controlsAreVisible=true;t.container.trigger("controlsshown");}t.setControlsSize();},hideControls:function(doAnimation){var t=this;doAnimation=typeof doAnimation=="undefined"||doAnimation;if(!t.controlsAreVisible||t.options.alwaysShowControls){return;}if(doAnimation){t.controls.stop(true,true).fadeOut(200,function(){$(this).css("visibility","hidden").css("display","block");t.controlsAreVisible=false;t.container.trigger("controlshidden");});t.container.find(".mejs-control").stop(true,true).fadeOut(200,function(){$(this).css("visibility","hidden").css("display","block");});}else{t.controls.css("visibility","hidden").css("display","block");t.container.find(".mejs-control").css("visibility","hidden").css("display","block");t.controlsAreVisible=false;t.container.trigger("controlshidden");}},controlsTimer:null,startControlsTimer:function(timeout){var t=this;timeout=typeof timeout!="undefined"?timeout:1500;t.killControlsTimer("start");t.controlsTimer=setTimeout(function(){t.hideControls();t.killControlsTimer("hide");},timeout);},killControlsTimer:function(src){var t=this;if(t.controlsTimer!==null){clearTimeout(t.controlsTimer);delete t.controlsTimer;t.controlsTimer=null;}},controlsEnabled:true,disableControls:function(){var t=this;t.killControlsTimer();t.hideControls(false);this.controlsEnabled=false;},enableControls:function(){var t=this;t.showControls(false);t.controlsEnabled=true;},meReady:function(media,domNode){var t=this,mf=mejs.MediaFeatures,autoplayAttr=domNode.getAttribute("autoplay"),autoplay=!(typeof autoplayAttr=="undefined"||autoplayAttr===null||autoplayAttr==="false"),featureIndex,feature;if(t.created){return;}else{t.created=true;}t.media=media;t.domNode=domNode;if(!(mf.isAndroid&&t.options.AndroidUseNativeControls)&&!(mf.isiPad&&t.options.iPadUseNativeControls)&&!(mf.isiPhone&&t.options.iPhoneUseNativeControls)){t.buildposter(t,t.controls,t.layers,t.media);t.buildkeyboard(t,t.controls,t.layers,t.media);t.buildoverlays(t,t.controls,t.layers,t.media);t.findTracks();for(featureIndex in t.options.features){feature=t.options.features[featureIndex];if(t["build"+feature]){try{t["build"+feature](t,t.controls,t.layers,t.media);}catch(e){}}}t.container.trigger("controlsready");t.setPlayerSize(t.width,t.height);t.setControlsSize();if(t.isVideo){if(mejs.MediaFeatures.hasTouch){t.$media.bind("touchstart",function(){if(t.controlsAreVisible){t.hideControls(false);}else{if(t.controlsEnabled){t.showControls(false);}}});}else{t.clickToPlayPauseCallback=function(){if(t.options.clickToPlayPause){if(t.media.paused){t.play();}else{t.pause();}}};t.media.addEventListener("click",t.clickToPlayPauseCallback,false);t.container.bind("mouseenter mouseover",function(){if(t.controlsEnabled){if(!t.options.alwaysShowControls){t.killControlsTimer("enter");t.showControls();t.startControlsTimer(2500);}}}).bind("mousemove",function(){if(t.controlsEnabled){if(!t.controlsAreVisible){t.showControls();}if(!t.options.alwaysShowControls){t.startControlsTimer(2500);}}}).bind("mouseleave",function(){if(t.controlsEnabled){if(!t.media.paused&&!t.options.alwaysShowControls){t.startControlsTimer(1000);}}});}if(t.options.hideVideoControlsOnLoad){t.hideControls(false);}if(autoplay&&!t.options.alwaysShowControls){t.hideControls();}if(t.options.enableAutosize){t.media.addEventListener("loadedmetadata",function(e){if(t.options.videoHeight<=0&&t.domNode.getAttribute("height")===null&&!isNaN(e.target.videoHeight)){t.setPlayerSize(e.target.videoWidth,e.target.videoHeight);t.setControlsSize();t.media.setVideoSize(e.target.videoWidth,e.target.videoHeight);}},false);}}media.addEventListener("play",function(){var playerIndex;for(playerIndex in mejs.players){var p=mejs.players[playerIndex];if(p.id!=t.id&&t.options.pauseOtherPlayers&&!p.paused&&!p.ended){p.pause();}p.hasFocus=false;}t.hasFocus=true;},false);t.media.addEventListener("ended",function(e){if(t.options.autoRewind){try{t.media.setCurrentTime(0);}catch(exp){}}t.media.pause();if(t.setProgressRail){t.setProgressRail();}if(t.setCurrentRail){t.setCurrentRail();}if(t.options.loop){t.play();}else{if(!t.options.alwaysShowControls&&t.controlsEnabled){t.showControls();}}},false);t.media.addEventListener("loadedmetadata",function(e){if(t.updateDuration){t.updateDuration();}if(t.updateCurrent){t.updateCurrent();}if(!t.isFullScreen){t.setPlayerSize(t.width,t.height);t.setControlsSize();}},false);setTimeout(function(){t.setPlayerSize(t.width,t.height);t.setControlsSize();},50);t.globalBind("resize",function(){if(!(t.isFullScreen||(mejs.MediaFeatures.hasTrueNativeFullScreen&&document.webkitIsFullScreen))){t.setPlayerSize(t.width,t.height);}t.setControlsSize();});if(t.media.pluginType=="youtube"&&t.options.autoplay){t.container.find(".mejs-overlay-play").hide();}}if(autoplay&&media.pluginType=="native"){t.play();}if(t.options.success){if(typeof t.options.success=="string"){window[t.options.success](t.media,t.domNode,t);}else{t.options.success(t.media,t.domNode,t);}}},handleError:function(e){var t=this;t.controls.hide();if(t.options.error){t.options.error(e);}},setPlayerSize:function(width,height){var t=this;if(!t.options.setDimensions){return false;}if(typeof width!="undefined"){t.width=width;}if(typeof height!="undefined"){t.height=height;}if(t.height.toString().indexOf("%")>0||t.$node.css("max-width")==="100%"||(t.$node[0].currentStyle&&t.$node[0].currentStyle.maxWidth==="100%")){var nativeWidth=(function(){if(t.isVideo){if(t.media.videoWidth&&t.media.videoWidth>0){return t.media.videoWidth;}else{if(t.media.getAttribute("width")!==null){return t.media.getAttribute("width");}else{return t.options.defaultVideoWidth;}}}else{return t.options.defaultAudioWidth;}})();var nativeHeight=(function(){if(t.isVideo){if(t.media.videoHeight&&t.media.videoHeight>0){return t.media.videoHeight;}else{if(t.media.getAttribute("height")!==null){return t.media.getAttribute("height");}else{return t.options.defaultVideoHeight;}}}else{return t.options.defaultAudioHeight;}})();var parentWidth=t.container.parent().closest(":visible").width(),parentHeight=t.container.parent().closest(":visible").height(),newHeight=t.isVideo||!t.options.autosizeProgress?parseInt(parentWidth*nativeHeight/nativeWidth,10):nativeHeight;if(isNaN(newHeight)||(parentHeight!=0&&newHeight>parentHeight)){newHeight=parentHeight;}if(t.container.parent()[0].tagName.toLowerCase()==="body"){parentWidth=$(window).width();newHeight=$(window).height();}if(newHeight!=0&&parentWidth!=0){t.container.width(parentWidth).height(newHeight);t.$media.add(t.container.find(".mejs-shim")).width("100%").height("100%");if(t.isVideo){if(t.media.setVideoSize){t.media.setVideoSize(parentWidth,newHeight);}}t.layers.children(".mejs-layer").width("100%").height("100%");}}else{t.container.width(t.width).height(t.height);t.layers.children(".mejs-layer").width(t.width).height(t.height);}var playLayer=t.layers.find(".mejs-overlay-play"),playButton=playLayer.find(".mejs-overlay-button");playLayer.height(t.container.height()-t.controls.height());playButton.css("margin-top","-"+(playButton.height()/2-t.controls.height()/2).toString()+"px");},setControlsSize:function(){var t=this,usedWidth=0,railWidth=0,rail=t.controls.find(".mejs-time-rail"),total=t.controls.find(".mejs-time-total"),current=t.controls.find(".mejs-time-current"),loaded=t.controls.find(".mejs-time-loaded"),others=rail.siblings(),lastControl=others.last(),lastControlPosition=null;if(!t.container.is(":visible")||!rail.length||!rail.is(":visible")){return;}if(t.options&&!t.options.autosizeProgress){railWidth=parseInt(rail.css("width"));}if(railWidth===0||!railWidth){others.each(function(){var $this=$(this);if($this.css("position")!="absolute"&&$this.is(":visible")){usedWidth+=$(this).outerWidth(true);}});railWidth=t.controls.width()-usedWidth-(rail.outerWidth(true)-rail.width());}do{rail.width(railWidth);total.width(railWidth-(total.outerWidth(true)-total.width()));if(lastControl.css("position")!="absolute"){lastControlPosition=lastControl.position();railWidth--;}}while(lastControlPosition!=null&&lastControlPosition.top>0&&railWidth>0);if(t.setProgressRail){t.setProgressRail();}if(t.setCurrentRail){t.setCurrentRail();}},buildposter:function(player,controls,layers,media){var t=this,poster=$('<div class="mejs-poster mejs-layer">'+"</div>").appendTo(layers),posterUrl=player.$media.attr("poster");if(player.options.poster!==""){posterUrl=player.options.poster;}if(posterUrl!==""&&posterUrl!=null){t.setPoster(posterUrl);}else{poster.hide();}media.addEventListener("play",function(){poster.hide();},false);if(player.options.showPosterWhenEnded&&player.options.autoRewind){media.addEventListener("ended",function(){poster.show();},false);}},setPoster:function(url){var t=this,posterDiv=t.container.find(".mejs-poster"),posterImg=posterDiv.find("img");if(posterImg.length==0){posterImg=$('<img width="100%" height="100%" />').appendTo(posterDiv);}posterImg.attr("src",url);posterDiv.css({"background-image":"url("+url+")"});},buildoverlays:function(player,controls,layers,media){var t=this;if(!player.isVideo){return;}var loading=$('<div class="mejs-overlay mejs-layer">'+'<div class="mejs-overlay-loading"><span></span></div>'+"</div>").hide().appendTo(layers),error=$('<div class="mejs-overlay mejs-layer">'+'<div class="mejs-overlay-error"></div>'+"</div>").hide().appendTo(layers),bigPlay=$('<div class="mejs-overlay mejs-layer mejs-overlay-play">'+'<div class="mejs-overlay-button"></div>'+"</div>").appendTo(layers).bind("click",function(){if(t.options.clickToPlayPause){if(media.paused){media.play();}}});media.addEventListener("play",function(){bigPlay.hide();loading.hide();controls.find(".mejs-time-buffering").hide();error.hide();},false);media.addEventListener("playing",function(){bigPlay.hide();loading.hide();controls.find(".mejs-time-buffering").hide();error.hide();},false);media.addEventListener("seeking",function(){loading.show();controls.find(".mejs-time-buffering").show();},false);media.addEventListener("seeked",function(){loading.hide();controls.find(".mejs-time-buffering").hide();},false);media.addEventListener("pause",function(){if(!mejs.MediaFeatures.isiPhone){bigPlay.show();}},false);media.addEventListener("waiting",function(){loading.show();controls.find(".mejs-time-buffering").show();},false);media.addEventListener("loadeddata",function(){loading.show();controls.find(".mejs-time-buffering").show();},false);media.addEventListener("canplay",function(){loading.hide();controls.find(".mejs-time-buffering").hide();},false);media.addEventListener("error",function(){loading.hide();controls.find(".mejs-time-buffering").hide();error.show();error.find("mejs-overlay-error").html("Error loading this resource");},false);media.addEventListener("keydown",function(e){t.onkeydown(player,media,e);},false);},buildkeyboard:function(player,controls,layers,media){var t=this;t.globalBind("keydown",function(e){return t.onkeydown(player,media,e);});t.globalBind("click",function(event){player.hasFocus=$(event.target).closest(".mejs-container").length!=0;});},onkeydown:function(player,media,e){if(player.hasFocus&&player.options.enableKeyboard){for(var i=0,il=player.options.keyActions.length;i<il;i++){var keyAction=player.options.keyActions[i];for(var j=0,jl=keyAction.keys.length;j<jl;j++){if(e.keyCode==keyAction.keys[j]){if(typeof(e.preventDefault)=="function"){e.preventDefault();}keyAction.action(player,media,e.keyCode);return false;}}}}return true;},findTracks:function(){var t=this,tracktags=t.$media.find("track");t.tracks=[];tracktags.each(function(index,track){track=$(track);t.tracks.push({srclang:(track.attr("srclang"))?track.attr("srclang").toLowerCase():"",src:track.attr("src"),kind:track.attr("kind"),label:track.attr("label")||"",entries:[],isLoaded:false});});},changeSkin:function(className){this.container[0].className="mejs-container "+className;this.setPlayerSize(this.width,this.height);this.setControlsSize();},play:function(){this.load();this.media.play();},pause:function(){try{this.media.pause();}catch(e){}},load:function(){if(!this.isLoaded){this.media.load();}this.isLoaded=true;},setMuted:function(muted){this.media.setMuted(muted);},setCurrentTime:function(time){this.media.setCurrentTime(time);},getCurrentTime:function(){return this.media.currentTime;},setVolume:function(volume){this.media.setVolume(volume);},getVolume:function(){return this.media.volume;},setSrc:function(src){this.media.setSrc(src);},remove:function(){var t=this,featureIndex,feature;for(featureIndex in t.options.features){feature=t.options.features[featureIndex];if(t["clean"+feature]){try{t["clean"+feature](t);}catch(e){}}}if(!t.isDynamic){t.$media.prop("controls",true);t.$node.clone().insertBefore(t.container).show();t.$node.remove();}else{t.$node.insertBefore(t.container);}if(t.media.pluginType!=="native"){t.media.remove();}delete mejs.players[t.id];if(typeof t.container=="object"){t.container.remove();}t.globalUnbind();delete t.node.player;}};(function(){var rwindow=/^((after|before)print|(before)?unload|hashchange|message|o(ff|n)line|page(hide|show)|popstate|resize|storage)\b/;function splitEvents(events,id){var ret={d:[],w:[]};$.each((events||"").split(" "),function(k,v){var eventname=v+"."+id;if(eventname.indexOf(".")===0){ret.d.push(eventname);ret.w.push(eventname);}else{ret[rwindow.test(v)?"w":"d"].push(eventname);}});ret.d=ret.d.join(" ");ret.w=ret.w.join(" ");return ret;}mejs.MediaElementPlayer.prototype.globalBind=function(events,data,callback){var t=this;events=splitEvents(events,t.id);if(events.d){$(document).bind(events.d,data,callback);}if(events.w){$(window).bind(events.w,data,callback);}};mejs.MediaElementPlayer.prototype.globalUnbind=function(events,callback){var t=this;events=splitEvents(events,t.id);if(events.d){$(document).unbind(events.d,callback);}if(events.w){$(window).unbind(events.w,callback);}};})();if(typeof $!="undefined"){$.fn.mediaelementplayer=function(options){if(options===false){this.each(function(){var player=$(this).data("mediaelementplayer");if(player){player.remove();}$(this).removeData("mediaelementplayer");});}else{this.each(function(){$(this).data("mediaelementplayer",new mejs.MediaElementPlayer(this,options));});}return this;};$(document).ready(function(){$(".mejs-player").mediaelementplayer();});}window.MediaElementPlayer=mejs.MediaElementPlayer;})(mejs.$);(function($){$.extend(mejs.MepDefaults,{playpauseText:mejs.i18n.t("Play/Pause")});$.extend(MediaElementPlayer.prototype,{buildplaypause:function(player,controls,layers,media){var t=this,play=$('<div class="mejs-button mejs-playpause-button mejs-play" >'+'<button type="button" aria-controls="'+t.id+'" title="'+t.options.playpauseText+'" aria-label="'+t.options.playpauseText+'"></button>'+"</div>").appendTo(controls).click(function(e){e.preventDefault();if(media.paused){media.play();}else{media.pause();}return false;});media.addEventListener("play",function(){play.removeClass("mejs-play").addClass("mejs-pause");},false);media.addEventListener("playing",function(){play.removeClass("mejs-play").addClass("mejs-pause");},false);media.addEventListener("pause",function(){play.removeClass("mejs-pause").addClass("mejs-play");},false);media.addEventListener("paused",function(){play.removeClass("mejs-pause").addClass("mejs-play");},false);}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{stopText:"Stop"});$.extend(MediaElementPlayer.prototype,{buildstop:function(player,controls,layers,media){var t=this,stop=$('<div class="mejs-button mejs-stop-button mejs-stop">'+'<button type="button" aria-controls="'+t.id+'" title="'+t.options.stopText+'" aria-label="'+t.options.stopText+'"></button>'+"</div>").appendTo(controls).click(function(){if(!media.paused){media.pause();}if(media.currentTime>0){media.setCurrentTime(0);media.pause();controls.find(".mejs-time-current").width("0px");controls.find(".mejs-time-handle").css("left","0px");controls.find(".mejs-time-float-current").html(mejs.Utility.secondsToTimeCode(0));controls.find(".mejs-currenttime").html(mejs.Utility.secondsToTimeCode(0));layers.find(".mejs-poster").show();}});}});})(mejs.$);(function($){$.extend(MediaElementPlayer.prototype,{buildprogress:function(player,controls,layers,media){$('<div class="mejs-time-rail">'+'<span class="mejs-time-total">'+'<span class="mejs-time-buffering"></span>'+'<span class="mejs-time-loaded"></span>'+'<span class="mejs-time-current"></span>'+'<span class="mejs-time-handle"></span>'+'<span class="mejs-time-float">'+'<span class="mejs-time-float-current">00:00</span>'+'<span class="mejs-time-float-corner"></span>'+"</span>"+"</span>"+"</div>").appendTo(controls);controls.find(".mejs-time-buffering").hide();var t=this,total=controls.find(".mejs-time-total"),loaded=controls.find(".mejs-time-loaded"),current=controls.find(".mejs-time-current"),handle=controls.find(".mejs-time-handle"),timefloat=controls.find(".mejs-time-float"),timefloatcurrent=controls.find(".mejs-time-float-current"),handleMouseMove=function(e){if(e.originalEvent.changedTouches){var x=e.originalEvent.changedTouches[0].pageX;}else{var x=e.pageX;}var offset=total.offset(),width=total.outerWidth(true),percentage=0,newTime=0,pos=0;if(media.duration){if(x<offset.left){x=offset.left;}else{if(x>width+offset.left){x=width+offset.left;}}pos=x-offset.left;percentage=(pos/width);newTime=(percentage<=0.02)?0:percentage*media.duration;if(mouseIsDown&&newTime!==media.currentTime){media.setCurrentTime(newTime);}if(!mejs.MediaFeatures.hasTouch){timefloat.css("left",pos);timefloatcurrent.html(mejs.Utility.secondsToTimeCode(newTime));timefloat.show();}}},mouseIsDown=false,mouseIsOver=false;total.bind("mousedown touchstart",function(e){if(e.which===1||e.which===0){mouseIsDown=true;handleMouseMove(e);t.globalBind("mousemove.dur touchmove.dur",function(e){handleMouseMove(e);});t.globalBind("mouseup.dur touchend.dur",function(e){mouseIsDown=false;timefloat.hide();t.globalUnbind(".dur");});return false;}}).bind("mouseenter",function(e){mouseIsOver=true;t.globalBind("mousemove.dur",function(e){handleMouseMove(e);});if(!mejs.MediaFeatures.hasTouch){timefloat.show();}}).bind("mouseleave",function(e){mouseIsOver=false;if(!mouseIsDown){t.globalUnbind(".dur");timefloat.hide();}});media.addEventListener("progress",function(e){player.setProgressRail(e);player.setCurrentRail(e);},false);media.addEventListener("timeupdate",function(e){player.setProgressRail(e);player.setCurrentRail(e);},false);t.loaded=loaded;t.total=total;t.current=current;t.handle=handle;},setProgressRail:function(e){var t=this,target=(e!=undefined)?e.target:t.media,percent=null;if(target&&target.buffered&&target.buffered.length>0&&target.buffered.end&&target.duration){percent=target.buffered.end(0)/target.duration;}else{if(target&&target.bytesTotal!=undefined&&target.bytesTotal>0&&target.bufferedBytes!=undefined){percent=target.bufferedBytes/target.bytesTotal;}else{if(e&&e.lengthComputable&&e.total!=0){percent=e.loaded/e.total;}}}if(percent!==null){percent=Math.min(1,Math.max(0,percent));if(t.loaded&&t.total){t.loaded.width(t.total.width()*percent);}}},setCurrentRail:function(){var t=this;if(t.media.currentTime!=undefined&&t.media.duration){if(t.total&&t.handle){var newWidth=Math.round(t.total.width()*t.media.currentTime/t.media.duration),handlePos=newWidth-Math.round(t.handle.outerWidth(true)/2);t.current.width(newWidth);t.handle.css("left",handlePos);}}}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{duration:-1,timeAndDurationSeparator:"<span> | </span>"});$.extend(MediaElementPlayer.prototype,{buildcurrent:function(player,controls,layers,media){var t=this;$('<div class="mejs-time">'+'<span class="mejs-currenttime">'+(player.options.alwaysShowHours?"00:":"")+(player.options.showTimecodeFrameCount?"00:00:00":"00:00")+"</span>"+"</div>").appendTo(controls);t.currenttime=t.controls.find(".mejs-currenttime");media.addEventListener("timeupdate",function(){player.updateCurrent();},false);},buildduration:function(player,controls,layers,media){var t=this;if(controls.children().last().find(".mejs-currenttime").length>0){$(t.options.timeAndDurationSeparator+'<span class="mejs-duration">'+(t.options.duration>0?mejs.Utility.secondsToTimeCode(t.options.duration,t.options.alwaysShowHours||t.media.duration>3600,t.options.showTimecodeFrameCount,t.options.framesPerSecond||25):((player.options.alwaysShowHours?"00:":"")+(player.options.showTimecodeFrameCount?"00:00:00":"00:00")))+"</span>").appendTo(controls.find(".mejs-time"));}else{controls.find(".mejs-currenttime").parent().addClass("mejs-currenttime-container");$('<div class="mejs-time mejs-duration-container">'+'<span class="mejs-duration">'+(t.options.duration>0?mejs.Utility.secondsToTimeCode(t.options.duration,t.options.alwaysShowHours||t.media.duration>3600,t.options.showTimecodeFrameCount,t.options.framesPerSecond||25):((player.options.alwaysShowHours?"00:":"")+(player.options.showTimecodeFrameCount?"00:00:00":"00:00")))+"</span>"+"</div>").appendTo(controls);}t.durationD=t.controls.find(".mejs-duration");media.addEventListener("timeupdate",function(){player.updateDuration();},false);},updateCurrent:function(){var t=this;if(t.currenttime){t.currenttime.html(mejs.Utility.secondsToTimeCode(t.media.currentTime,t.options.alwaysShowHours||t.media.duration>3600,t.options.showTimecodeFrameCount,t.options.framesPerSecond||25));}},updateDuration:function(){var t=this;t.container.toggleClass("mejs-long-video",t.media.duration>3600);if(t.durationD&&(t.options.duration>0||t.media.duration)){t.durationD.html(mejs.Utility.secondsToTimeCode(t.options.duration>0?t.options.duration:t.media.duration,t.options.alwaysShowHours,t.options.showTimecodeFrameCount,t.options.framesPerSecond||25));}}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{muteText:mejs.i18n.t("Mute Toggle"),hideVolumeOnTouchDevices:true,audioVolume:"horizontal",videoVolume:"vertical"});$.extend(MediaElementPlayer.prototype,{buildvolume:function(player,controls,layers,media){if((mejs.MediaFeatures.isAndroid||mejs.MediaFeatures.isiOS)&&this.options.hideVolumeOnTouchDevices){return;}var t=this,mode=(t.isVideo)?t.options.videoVolume:t.options.audioVolume,mute=(mode=="horizontal")?$('<div class="mejs-button mejs-volume-button mejs-mute">'+'<button type="button" aria-controls="'+t.id+'" title="'+t.options.muteText+'" aria-label="'+t.options.muteText+'"></button>'+"</div>"+'<div class="mejs-horizontal-volume-slider">'+'<div class="mejs-horizontal-volume-total"></div>'+'<div class="mejs-horizontal-volume-current"></div>'+'<div class="mejs-horizontal-volume-handle"></div>'+"</div>").appendTo(controls):$('<div class="mejs-button mejs-volume-button mejs-mute">'+'<button type="button" aria-controls="'+t.id+'" title="'+t.options.muteText+'" aria-label="'+t.options.muteText+'"></button>'+'<div class="mejs-volume-slider">'+'<div class="mejs-volume-total"></div>'+'<div class="mejs-volume-current"></div>'+'<div class="mejs-volume-handle"></div>'+"</div>"+"</div>").appendTo(controls),volumeSlider=t.container.find(".mejs-volume-slider, .mejs-horizontal-volume-slider"),volumeTotal=t.container.find(".mejs-volume-total, .mejs-horizontal-volume-total"),volumeCurrent=t.container.find(".mejs-volume-current, .mejs-horizontal-volume-current"),volumeHandle=t.container.find(".mejs-volume-handle, .mejs-horizontal-volume-handle"),positionVolumeHandle=function(volume,secondTry){if(!volumeSlider.is(":visible")&&typeof secondTry=="undefined"){volumeSlider.show();positionVolumeHandle(volume,true);volumeSlider.hide();return;}volume=Math.max(0,volume);volume=Math.min(volume,1);if(volume==0){mute.removeClass("mejs-mute").addClass("mejs-unmute");}else{mute.removeClass("mejs-unmute").addClass("mejs-mute");}if(mode=="vertical"){var totalHeight=volumeTotal.height(),totalPosition=volumeTotal.position(),newTop=totalHeight-(totalHeight*volume);volumeHandle.css("top",Math.round(totalPosition.top+newTop-(volumeHandle.height()/2)));volumeCurrent.height(totalHeight-newTop);volumeCurrent.css("top",totalPosition.top+newTop);}else{var totalWidth=volumeTotal.width(),totalPosition=volumeTotal.position(),newLeft=totalWidth*volume;volumeHandle.css("left",Math.round(totalPosition.left+newLeft-(volumeHandle.width()/2)));volumeCurrent.width(Math.round(newLeft));}},handleVolumeMove=function(e){var volume=null,totalOffset=volumeTotal.offset();if(mode=="vertical"){var railHeight=volumeTotal.height(),totalTop=parseInt(volumeTotal.css("top").replace(/px/,""),10),newY=e.pageY-totalOffset.top;volume=(railHeight-newY)/railHeight;if(totalOffset.top==0||totalOffset.left==0){return;}}else{var railWidth=volumeTotal.width(),newX=e.pageX-totalOffset.left;volume=newX/railWidth;}volume=Math.max(0,volume);volume=Math.min(volume,1);positionVolumeHandle(volume);if(volume==0){media.setMuted(true);}else{media.setMuted(false);}media.setVolume(volume);},mouseIsDown=false,mouseIsOver=false;mute.hover(function(){volumeSlider.show();mouseIsOver=true;},function(){mouseIsOver=false;if(!mouseIsDown&&mode=="vertical"){volumeSlider.hide();}});volumeSlider.bind("mouseover",function(){mouseIsOver=true;}).bind("mousedown",function(e){handleVolumeMove(e);t.globalBind("mousemove.vol",function(e){handleVolumeMove(e);});t.globalBind("mouseup.vol",function(){mouseIsDown=false;t.globalUnbind(".vol");if(!mouseIsOver&&mode=="vertical"){volumeSlider.hide();}});mouseIsDown=true;return false;});mute.find("button").click(function(){media.setMuted(!media.muted);});media.addEventListener("volumechange",function(e){if(!mouseIsDown){if(media.muted){positionVolumeHandle(0);mute.removeClass("mejs-mute").addClass("mejs-unmute");}else{positionVolumeHandle(media.volume);mute.removeClass("mejs-unmute").addClass("mejs-mute");}}},false);if(t.container.is(":visible")){positionVolumeHandle(player.options.startVolume);if(player.options.startVolume===0){media.setMuted(true);}if(media.pluginType==="native"){media.setVolume(player.options.startVolume);}}}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{usePluginFullScreen:true,newWindowCallback:function(){return"";},fullscreenText:mejs.i18n.t("Fullscreen")});$.extend(MediaElementPlayer.prototype,{isFullScreen:false,isNativeFullScreen:false,isInIframe:false,buildfullscreen:function(player,controls,layers,media){if(!player.isVideo){return;}player.isInIframe=(window.location!=window.parent.location);if(mejs.MediaFeatures.hasTrueNativeFullScreen){var func=function(e){if(player.isFullScreen){if(mejs.MediaFeatures.isFullScreen()){player.isNativeFullScreen=true;player.setControlsSize();}else{player.isNativeFullScreen=false;player.exitFullScreen();}}};player.globalBind(mejs.MediaFeatures.fullScreenEventName,func);}var t=this,normalHeight=0,normalWidth=0,container=player.container,fullscreenBtn=$('<div class="mejs-button mejs-fullscreen-button">'+'<button type="button" aria-controls="'+t.id+'" title="'+t.options.fullscreenText+'" aria-label="'+t.options.fullscreenText+'"></button>'+"</div>").appendTo(controls);if(t.media.pluginType==="native"||(!t.options.usePluginFullScreen&&!mejs.MediaFeatures.isFirefox)){fullscreenBtn.click(function(){var isFullScreen=(mejs.MediaFeatures.hasTrueNativeFullScreen&&mejs.MediaFeatures.isFullScreen())||player.isFullScreen;if(isFullScreen){player.exitFullScreen();}else{player.enterFullScreen();}});}else{var hideTimeout=null,supportsPointerEvents=(function(){var element=document.createElement("x"),documentElement=document.documentElement,getComputedStyle=window.getComputedStyle,supports;if(!("pointerEvents" in element.style)){return false;}element.style.pointerEvents="auto";element.style.pointerEvents="x";documentElement.appendChild(element);supports=getComputedStyle&&getComputedStyle(element,"").pointerEvents==="auto";documentElement.removeChild(element);return !!supports;})();if(supportsPointerEvents&&!mejs.MediaFeatures.isOpera){var fullscreenIsDisabled=false,restoreControls=function(){if(fullscreenIsDisabled){for(var i in hoverDivs){hoverDivs[i].hide();}fullscreenBtn.css("pointer-events","");t.controls.css("pointer-events","");t.media.removeEventListener("click",t.clickToPlayPauseCallback);fullscreenIsDisabled=false;}},hoverDivs={},hoverDivNames=["top","left","right","bottom"],i,len,positionHoverDivs=function(){var fullScreenBtnOffsetLeft=fullscreenBtn.offset().left-t.container.offset().left,fullScreenBtnOffsetTop=fullscreenBtn.offset().top-t.container.offset().top,fullScreenBtnWidth=fullscreenBtn.outerWidth(true),fullScreenBtnHeight=fullscreenBtn.outerHeight(true),containerWidth=t.container.width(),containerHeight=t.container.height();for(i in hoverDivs){hoverDivs[i].css({position:"absolute",top:0,left:0});}hoverDivs["top"].width(containerWidth).height(fullScreenBtnOffsetTop);hoverDivs["left"].width(fullScreenBtnOffsetLeft).height(fullScreenBtnHeight).css({top:fullScreenBtnOffsetTop});hoverDivs["right"].width(containerWidth-fullScreenBtnOffsetLeft-fullScreenBtnWidth).height(fullScreenBtnHeight).css({top:fullScreenBtnOffsetTop,left:fullScreenBtnOffsetLeft+fullScreenBtnWidth});hoverDivs["bottom"].width(containerWidth).height(containerHeight-fullScreenBtnHeight-fullScreenBtnOffsetTop).css({top:fullScreenBtnOffsetTop+fullScreenBtnHeight});};t.globalBind("resize",function(){positionHoverDivs();});for(i=0,len=hoverDivNames.length;i<len;i++){hoverDivs[hoverDivNames[i]]=$('<div class="mejs-fullscreen-hover" />').appendTo(t.container).mouseover(restoreControls).hide();}fullscreenBtn.on("mouseover",function(){if(!t.isFullScreen){var buttonPos=fullscreenBtn.offset(),containerPos=player.container.offset();media.positionFullscreenButton(buttonPos.left-containerPos.left,buttonPos.top-containerPos.top,false);fullscreenBtn.css("pointer-events","none");t.controls.css("pointer-events","none");t.media.addEventListener("click",t.clickToPlayPauseCallback);for(i in hoverDivs){hoverDivs[i].show();}positionHoverDivs();fullscreenIsDisabled=true;}});media.addEventListener("fullscreenchange",function(e){t.isFullScreen=!t.isFullScreen;if(t.isFullScreen){t.media.removeEventListener("click",t.clickToPlayPauseCallback);}else{t.media.addEventListener("click",t.clickToPlayPauseCallback);}restoreControls();});t.globalBind("mousemove",function(e){if(fullscreenIsDisabled){var fullscreenBtnPos=fullscreenBtn.offset();if(e.pageY<fullscreenBtnPos.top||e.pageY>fullscreenBtnPos.top+fullscreenBtn.outerHeight(true)||e.pageX<fullscreenBtnPos.left||e.pageX>fullscreenBtnPos.left+fullscreenBtn.outerWidth(true)){fullscreenBtn.css("pointer-events","");t.controls.css("pointer-events","");fullscreenIsDisabled=false;}}});}else{fullscreenBtn.on("mouseover",function(){if(hideTimeout!==null){clearTimeout(hideTimeout);delete hideTimeout;}var buttonPos=fullscreenBtn.offset(),containerPos=player.container.offset();media.positionFullscreenButton(buttonPos.left-containerPos.left,buttonPos.top-containerPos.top,true);}).on("mouseout",function(){if(hideTimeout!==null){clearTimeout(hideTimeout);delete hideTimeout;}hideTimeout=setTimeout(function(){media.hideFullscreenButton();},1500);});}}player.fullscreenBtn=fullscreenBtn;t.globalBind("keydown",function(e){if(((mejs.MediaFeatures.hasTrueNativeFullScreen&&mejs.MediaFeatures.isFullScreen())||t.isFullScreen)&&e.keyCode==27){player.exitFullScreen();}});},cleanfullscreen:function(player){player.exitFullScreen();},containerSizeTimeout:null,enterFullScreen:function(){var t=this;if(t.media.pluginType!=="native"&&(mejs.MediaFeatures.isFirefox||t.options.usePluginFullScreen)){return;}$(document.documentElement).addClass("mejs-fullscreen");normalHeight=t.container.height();normalWidth=t.container.width();if(t.media.pluginType==="native"){if(mejs.MediaFeatures.hasTrueNativeFullScreen){mejs.MediaFeatures.requestFullScreen(t.container[0]);if(t.isInIframe){setTimeout(function checkFullscreen(){if(t.isNativeFullScreen){var zoomMultiplier=window["devicePixelRatio"]||1;var percentErrorMargin=0.002;var windowWidth=zoomMultiplier*$(window).width();var screenWidth=screen.width;var absDiff=Math.abs(screenWidth-windowWidth);var marginError=screenWidth*percentErrorMargin;if(absDiff>marginError){t.exitFullScreen();}else{setTimeout(checkFullscreen,500);}}},500);}}else{if(mejs.MediaFeatures.hasSemiNativeFullScreen){t.media.webkitEnterFullscreen();return;}}}if(t.isInIframe){var url=t.options.newWindowCallback(this);if(url!==""){if(!mejs.MediaFeatures.hasTrueNativeFullScreen){t.pause();window.open(url,t.id,"top=0,left=0,width="+screen.availWidth+",height="+screen.availHeight+",resizable=yes,scrollbars=no,status=no,toolbar=no");return;}else{setTimeout(function(){if(!t.isNativeFullScreen){t.pause();window.open(url,t.id,"top=0,left=0,width="+screen.availWidth+",height="+screen.availHeight+",resizable=yes,scrollbars=no,status=no,toolbar=no");}},250);}}}t.container.addClass("mejs-container-fullscreen").width("100%").height("100%");t.containerSizeTimeout=setTimeout(function(){t.container.css({width:"100%",height:"100%"});t.setControlsSize();},500);if(t.media.pluginType==="native"){t.$media.width("100%").height("100%");}else{t.container.find(".mejs-shim").width("100%").height("100%");t.media.setVideoSize($(window).width(),$(window).height());}t.layers.children("div").width("100%").height("100%");if(t.fullscreenBtn){t.fullscreenBtn.removeClass("mejs-fullscreen").addClass("mejs-unfullscreen");}t.setControlsSize();t.isFullScreen=true;t.container.find(".mejs-captions-text").css("font-size",screen.width/t.width*1*100+"%");t.container.find(".mejs-captions-position").css("bottom","45px");},exitFullScreen:function(){var t=this;clearTimeout(t.containerSizeTimeout);if(t.media.pluginType!=="native"&&mejs.MediaFeatures.isFirefox){t.media.setFullscreen(false);return;}if(mejs.MediaFeatures.hasTrueNativeFullScreen&&(mejs.MediaFeatures.isFullScreen()||t.isFullScreen)){mejs.MediaFeatures.cancelFullScreen();}$(document.documentElement).removeClass("mejs-fullscreen");t.container.removeClass("mejs-container-fullscreen").width(normalWidth).height(normalHeight);if(t.media.pluginType==="native"){t.$media.width(normalWidth).height(normalHeight);}else{t.container.find(".mejs-shim").width(normalWidth).height(normalHeight);t.media.setVideoSize(normalWidth,normalHeight);}t.layers.children("div").width(normalWidth).height(normalHeight);t.fullscreenBtn.removeClass("mejs-unfullscreen").addClass("mejs-fullscreen");t.setControlsSize();t.isFullScreen=false;t.container.find(".mejs-captions-text").css("font-size","");t.container.find(".mejs-captions-position").css("bottom","");}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{speeds:["1.50","1.25","1.00","0.75"],defaultSpeed:"1.00"});$.extend(MediaElementPlayer.prototype,{buildspeed:function(player,controls,layers,media){var t=this;if(t.media.pluginType=="native"){var s='<div class="mejs-button mejs-speed-button"><button type="button">'+t.options.defaultSpeed+'x</button><div class="mejs-speed-selector"><ul>';var i,ss;if($.inArray(t.options.defaultSpeed,t.options.speeds)===-1){t.options.speeds.push(t.options.defaultSpeed);}t.options.speeds.sort(function(a,b){return parseFloat(b)-parseFloat(a);});for(i=0;i<t.options.speeds.length;i++){s+='<li><input type="radio" name="speed" value="'+t.options.speeds[i]+'" id="'+t.options.speeds[i]+'" ';if(t.options.speeds[i]==t.options.defaultSpeed){s+="checked=true ";s+='/><label for="'+t.options.speeds[i]+'" class="mejs-speed-selected">'+t.options.speeds[i]+"x</label></li>";}else{s+='/><label for="'+t.options.speeds[i]+'">'+t.options.speeds[i]+"x</label></li>";}}s+="</ul></div></div>";player.speedButton=$(s).appendTo(controls);player.playbackspeed=t.options.defaultSpeed;player.speedButton.on("click","input[type=radio]",function(){player.playbackspeed=$(this).attr("value");media.playbackRate=parseFloat(player.playbackspeed);player.speedButton.find("button").text(player.playbackspeed+"x");player.speedButton.find(".mejs-speed-selected").removeClass("mejs-speed-selected");player.speedButton.find("input[type=radio]:checked").next().addClass("mejs-speed-selected");});ss=player.speedButton.find(".mejs-speed-selector");ss.height(this.speedButton.find(".mejs-speed-selector ul").outerHeight(true)+player.speedButton.find(".mejs-speed-translations").outerHeight(true));ss.css("top",(-1*ss.height())+"px");}}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{startLanguage:"",tracksText:mejs.i18n.t("Captions/Subtitles"),hideCaptionsButtonWhenEmpty:true,toggleCaptionsButtonWhenOnlyOne:false,slidesSelector:""});$.extend(MediaElementPlayer.prototype,{hasChapters:false,buildtracks:function(player,controls,layers,media){if(player.tracks.length===0){return;}var t=this,i,options="";if(t.domNode.textTracks){for(i=t.domNode.textTracks.length-1;i>=0;i--){t.domNode.textTracks[i].mode="hidden";}}player.chapters=$('<div class="mejs-chapters mejs-layer"></div>').prependTo(layers).hide();player.captions=$('<div class="mejs-captions-layer mejs-layer"><div class="mejs-captions-position mejs-captions-position-hover"><span class="mejs-captions-text"></span></div></div>').prependTo(layers).hide();player.captionsText=player.captions.find(".mejs-captions-text");player.captionsButton=$('<div class="mejs-button mejs-captions-button">'+'<button type="button" aria-controls="'+t.id+'" title="'+t.options.tracksText+'" aria-label="'+t.options.tracksText+'"></button>'+'<div class="mejs-captions-selector">'+"<ul>"+"<li>"+'<input type="radio" name="'+player.id+'_captions" id="'+player.id+'_captions_none" value="none" checked="checked" />'+'<label for="'+player.id+'_captions_none">'+mejs.i18n.t("None")+"</label>"+"</li>"+"</ul>"+"</div>"+"</div>").appendTo(controls);var subtitleCount=0;for(i=0;i<player.tracks.length;i++){if(player.tracks[i].kind=="subtitles"){subtitleCount++;}}if(t.options.toggleCaptionsButtonWhenOnlyOne&&subtitleCount==1){player.captionsButton.on("click",function(){if(player.selectedTrack===null){lang=player.tracks[0].srclang;}else{lang="none";}player.setTrack(lang);});}else{player.captionsButton.on("mouseenter focusin",function(){$(this).find(".mejs-captions-selector").css("visibility","visible");}).on("click","input[type=radio]",function(){lang=this.value;player.setTrack(lang);});player.captionsButton.on("mouseleave focusout",function(){$(this).find(".mejs-captions-selector").css("visibility","hidden");});}if(!player.options.alwaysShowControls){player.container.bind("controlsshown",function(){player.container.find(".mejs-captions-position").addClass("mejs-captions-position-hover");}).bind("controlshidden",function(){if(!media.paused){player.container.find(".mejs-captions-position").removeClass("mejs-captions-position-hover");}});}else{player.container.find(".mejs-captions-position").addClass("mejs-captions-position-hover");}player.trackToLoad=-1;player.selectedTrack=null;player.isLoadingTrack=false;for(i=0;i<player.tracks.length;i++){if(player.tracks[i].kind=="subtitles"){player.addTrackButton(player.tracks[i].srclang,player.tracks[i].label);}}player.loadNextTrack();media.addEventListener("timeupdate",function(e){player.displayCaptions();},false);if(player.options.slidesSelector!==""){player.slidesContainer=$(player.options.slidesSelector);media.addEventListener("timeupdate",function(e){player.displaySlides();},false);}media.addEventListener("loadedmetadata",function(e){player.displayChapters();},false);player.container.hover(function(){if(player.hasChapters){player.chapters.css("visibility","visible");player.chapters.fadeIn(200).height(player.chapters.find(".mejs-chapter").outerHeight());}},function(){if(player.hasChapters&&!media.paused){player.chapters.fadeOut(200,function(){$(this).css("visibility","hidden");$(this).css("display","block");});}});if(player.node.getAttribute("autoplay")!==null){player.chapters.css("visibility","hidden");}},setTrack:function(lang){var t=this,i;if(lang=="none"){t.selectedTrack=null;t.captionsButton.removeClass("mejs-captions-enabled");}else{for(i=0;i<t.tracks.length;i++){if(t.tracks[i].srclang==lang){if(t.selectedTrack===null){t.captionsButton.addClass("mejs-captions-enabled");}t.selectedTrack=t.tracks[i];t.captions.attr("lang",t.selectedTrack.srclang);t.displayCaptions();break;}}}},loadNextTrack:function(){var t=this;t.trackToLoad++;if(t.trackToLoad<t.tracks.length){t.isLoadingTrack=true;t.loadTrack(t.trackToLoad);}else{t.isLoadingTrack=false;t.checkForTracks();}},loadTrack:function(index){var t=this,track=t.tracks[index],after=function(){track.isLoaded=true;t.enableTrackButton(track.srclang,track.label);t.loadNextTrack();};$.ajax({url:track.src,dataType:"text",success:function(d){if(typeof d=="string"&&(/<tt\s+xml/ig).exec(d)){track.entries=mejs.TrackFormatParser.dfxp.parse(d);}else{track.entries=mejs.TrackFormatParser.webvtt.parse(d);}after();if(track.kind=="chapters"){t.media.addEventListener("play",function(e){if(t.media.duration>0){t.displayChapters(track);}},false);}if(track.kind=="slides"){t.setupSlides(track);}},error:function(){t.loadNextTrack();}});},enableTrackButton:function(lang,label){var t=this;if(label===""){label=mejs.language.codes[lang]||lang;}t.captionsButton.find("input[value="+lang+"]").prop("disabled",false).siblings("label").html(label);if(t.options.startLanguage==lang){$("#"+t.id+"_captions_"+lang).prop("checked",true).trigger("click");}t.adjustLanguageBox();},addTrackButton:function(lang,label){var t=this;if(label===""){label=mejs.language.codes[lang]||lang;}t.captionsButton.find("ul").append($("<li>"+'<input type="radio" name="'+t.id+'_captions" id="'+t.id+"_captions_"+lang+'" value="'+lang+'" disabled="disabled" />'+'<label for="'+t.id+"_captions_"+lang+'">'+label+" (loading)"+"</label>"+"</li>"));t.adjustLanguageBox();t.container.find(".mejs-captions-translations option[value="+lang+"]").remove();},adjustLanguageBox:function(){var t=this;t.captionsButton.find(".mejs-captions-selector").height(t.captionsButton.find(".mejs-captions-selector ul").outerHeight(true)+t.captionsButton.find(".mejs-captions-translations").outerHeight(true));},checkForTracks:function(){var t=this,hasSubtitles=false;if(t.options.hideCaptionsButtonWhenEmpty){for(i=0;i<t.tracks.length;i++){if(t.tracks[i].kind=="subtitles"){hasSubtitles=true;break;}}if(!hasSubtitles){t.captionsButton.hide();t.setControlsSize();}}},displayCaptions:function(){if(typeof this.tracks=="undefined"){return;}var t=this,i,track=t.selectedTrack;if(track!==null&&track.isLoaded){for(i=0;i<track.entries.times.length;i++){if(t.media.currentTime>=track.entries.times[i].start&&t.media.currentTime<=track.entries.times[i].stop){t.captionsText.html(track.entries.text[i]).attr("class","mejs-captions-text "+(track.entries.times[i].identifier||""));t.captions.show().height(0);return;}}t.captions.hide();}else{t.captions.hide();}},setupSlides:function(track){var t=this;t.slides=track;t.slides.entries.imgs=[t.slides.entries.text.length];t.showSlide(0);},showSlide:function(index){if(typeof this.tracks=="undefined"||typeof this.slidesContainer=="undefined"){return;}var t=this,url=t.slides.entries.text[index],img=t.slides.entries.imgs[index];if(typeof img=="undefined"||typeof img.fadeIn=="undefined"){t.slides.entries.imgs[index]=img=$('<img src="'+url+'">').on("load",function(){img.appendTo(t.slidesContainer).hide().fadeIn().siblings(":visible").fadeOut();});}else{if(!img.is(":visible")&&!img.is(":animated")){img.fadeIn().siblings(":visible").fadeOut();}}},displaySlides:function(){if(typeof this.slides=="undefined"){return;}var t=this,slides=t.slides,i;for(i=0;i<slides.entries.times.length;i++){if(t.media.currentTime>=slides.entries.times[i].start&&t.media.currentTime<=slides.entries.times[i].stop){t.showSlide(i);return;}}},displayChapters:function(){var t=this,i;for(i=0;i<t.tracks.length;i++){if(t.tracks[i].kind=="chapters"&&t.tracks[i].isLoaded){t.drawChapters(t.tracks[i]);t.hasChapters=true;break;}}},drawChapters:function(chapters){var t=this,i,dur,percent=0,usedPercent=0;t.chapters.empty();for(i=0;i<chapters.entries.times.length;i++){dur=chapters.entries.times[i].stop-chapters.entries.times[i].start;percent=Math.floor(dur/t.media.duration*100);if(percent+usedPercent>100||i==chapters.entries.times.length-1&&percent+usedPercent<100){percent=100-usedPercent;}t.chapters.append($('<div class="mejs-chapter" rel="'+chapters.entries.times[i].start+'" style="left: '+usedPercent.toString()+"%;width: "+percent.toString()+'%;">'+'<div class="mejs-chapter-block'+((i==chapters.entries.times.length-1)?" mejs-chapter-block-last":"")+'">'+'<span class="ch-title">'+chapters.entries.text[i]+"</span>"+'<span class="ch-time">'+mejs.Utility.secondsToTimeCode(chapters.entries.times[i].start)+"&ndash;"+mejs.Utility.secondsToTimeCode(chapters.entries.times[i].stop)+"</span>"+"</div>"+"</div>"));usedPercent+=percent;}t.chapters.find("div.mejs-chapter").click(function(){t.media.setCurrentTime(parseFloat($(this).attr("rel")));if(t.media.paused){t.media.play();}});t.chapters.show();}});mejs.language={codes:{af:"Afrikaans",sq:"Albanian",ar:"Arabic",be:"Belarusian",bg:"Bulgarian",ca:"Catalan",zh:"Chinese","zh-cn":"Chinese Simplified","zh-tw":"Chinese Traditional",hr:"Croatian",cs:"Czech",da:"Danish",nl:"Dutch",en:"English",et:"Estonian",fl:"Filipino",fi:"Finnish",fr:"French",gl:"Galician",de:"German",el:"Greek",ht:"Haitian Creole",iw:"Hebrew",hi:"Hindi",hu:"Hungarian",is:"Icelandic",id:"Indonesian",ga:"Irish",it:"Italian",ja:"Japanese",ko:"Korean",lv:"Latvian",lt:"Lithuanian",mk:"Macedonian",ms:"Malay",mt:"Maltese",no:"Norwegian",fa:"Persian",pl:"Polish",pt:"Portuguese",ro:"Romanian",ru:"Russian",sr:"Serbian",sk:"Slovak",sl:"Slovenian",es:"Spanish",sw:"Swahili",sv:"Swedish",tl:"Tagalog",th:"Thai",tr:"Turkish",uk:"Ukrainian",vi:"Vietnamese",cy:"Welsh",yi:"Yiddish"}};mejs.TrackFormatParser={webvtt:{pattern_timecode:/^((?:[0-9]{1,2}:)?[0-9]{2}:[0-9]{2}([,.][0-9]{1,3})?) --\> ((?:[0-9]{1,2}:)?[0-9]{2}:[0-9]{2}([,.][0-9]{3})?)(.*)$/,parse:function(trackText){var i=0,lines=mejs.TrackFormatParser.split2(trackText,/\r?\n/),entries={text:[],times:[]},timecode,text,identifier;for(;i<lines.length;i++){timecode=this.pattern_timecode.exec(lines[i]);if(timecode&&i<lines.length){if((i-1)>=0&&lines[i-1]!==""){identifier=lines[i-1];}i++;text=lines[i];i++;while(lines[i]!==""&&i<lines.length){text=text+"\n"+lines[i];i++;}text=$.trim(text).replace(/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig,"<a href='$1' target='_blank'>$1</a>");entries.text.push(text);entries.times.push({identifier:identifier,start:(mejs.Utility.convertSMPTEtoSeconds(timecode[1])===0)?0.2:mejs.Utility.convertSMPTEtoSeconds(timecode[1]),stop:mejs.Utility.convertSMPTEtoSeconds(timecode[3]),settings:timecode[5]});}identifier="";}return entries;}},dfxp:{parse:function(trackText){trackText=$(trackText).filter("tt");var i=0,container=trackText.children("div").eq(0),lines=container.find("p"),styleNode=trackText.find("#"+container.attr("style")),styles,begin,end,text,entries={text:[],times:[]};if(styleNode.length){var attributes=styleNode.removeAttr("id").get(0).attributes;if(attributes.length){styles={};for(i=0;i<attributes.length;i++){styles[attributes[i].name.split(":")[1]]=attributes[i].value;}}}for(i=0;i<lines.length;i++){var style;var _temp_times={start:null,stop:null,style:null};if(lines.eq(i).attr("begin")){_temp_times.start=mejs.Utility.convertSMPTEtoSeconds(lines.eq(i).attr("begin"));}if(!_temp_times.start&&lines.eq(i-1).attr("end")){_temp_times.start=mejs.Utility.convertSMPTEtoSeconds(lines.eq(i-1).attr("end"));}if(lines.eq(i).attr("end")){_temp_times.stop=mejs.Utility.convertSMPTEtoSeconds(lines.eq(i).attr("end"));}if(!_temp_times.stop&&lines.eq(i+1).attr("begin")){_temp_times.stop=mejs.Utility.convertSMPTEtoSeconds(lines.eq(i+1).attr("begin"));}if(styles){style="";for(var _style in styles){style+=_style+":"+styles[_style]+";";}}if(style){_temp_times.style=style;}if(_temp_times.start===0){_temp_times.start=0.2;}entries.times.push(_temp_times);text=$.trim(lines.eq(i).html()).replace(/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig,"<a href='$1' target='_blank'>$1</a>");entries.text.push(text);if(entries.times.start===0){entries.times.start=2;}}return entries;}},split2:function(text,regex){return text.split(regex);}};if("x\n\ny".split(/\n/gi).length!=3){mejs.TrackFormatParser.split2=function(text,regex){var parts=[],chunk="",i;for(i=0;i<text.length;i++){chunk+=text.substring(i,i+1);if(regex.test(chunk)){parts.push(chunk.replace(regex,""));chunk="";}}parts.push(chunk);return parts;};}})(mejs.$);(function($){$.extend(mejs.MepDefaults,{"contextMenuItems":[{render:function(player){if(typeof player.enterFullScreen=="undefined"){return null;}if(player.isFullScreen){return mejs.i18n.t("Turn off Fullscreen");}else{return mejs.i18n.t("Go Fullscreen");}},click:function(player){if(player.isFullScreen){player.exitFullScreen();}else{player.enterFullScreen();}}},{render:function(player){if(player.media.muted){return mejs.i18n.t("Unmute");}else{return mejs.i18n.t("Mute");}},click:function(player){if(player.media.muted){player.setMuted(false);}else{player.setMuted(true);}}},{isSeparator:true},{render:function(player){return mejs.i18n.t("Download Video");},click:function(player){window.location.href=player.media.currentSrc;}}]});$.extend(MediaElementPlayer.prototype,{buildcontextmenu:function(player,controls,layers,media){player.contextMenu=$('<div class="mejs-contextmenu"></div>').appendTo($("body")).hide();player.container.bind("contextmenu",function(e){if(player.isContextMenuEnabled){e.preventDefault();player.renderContextMenu(e.clientX-1,e.clientY-1);return false;}});player.container.bind("click",function(){player.contextMenu.hide();});player.contextMenu.bind("mouseleave",function(){player.startContextMenuTimer();});},cleancontextmenu:function(player){player.contextMenu.remove();},isContextMenuEnabled:true,enableContextMenu:function(){this.isContextMenuEnabled=true;},disableContextMenu:function(){this.isContextMenuEnabled=false;},contextMenuTimeout:null,startContextMenuTimer:function(){var t=this;t.killContextMenuTimer();t.contextMenuTimer=setTimeout(function(){t.hideContextMenu();t.killContextMenuTimer();},750);},killContextMenuTimer:function(){var timer=this.contextMenuTimer;if(timer!=null){clearTimeout(timer);delete timer;timer=null;}},hideContextMenu:function(){this.contextMenu.hide();},renderContextMenu:function(x,y){var t=this,html="",items=t.options.contextMenuItems;for(var i=0,il=items.length;i<il;i++){if(items[i].isSeparator){html+='<div class="mejs-contextmenu-separator"></div>';}else{var rendered=items[i].render(t);if(rendered!=null){html+='<div class="mejs-contextmenu-item" data-itemindex="'+i+'" id="element-'+(Math.random()*1000000)+'">'+rendered+"</div>";}}}t.contextMenu.empty().append($(html)).css({top:y,left:x}).show();t.contextMenu.find(".mejs-contextmenu-item").each(function(){var $dom=$(this),itemIndex=parseInt($dom.data("itemindex"),10),item=t.options.contextMenuItems[itemIndex];if(typeof item.show!="undefined"){item.show($dom,t);}$dom.click(function(){if(typeof item.click!="undefined"){item.click(t);}t.contextMenu.hide();});});setTimeout(function(){t.killControlsTimer("rev3");},100);}});})(mejs.$);(function($){$.extend(mejs.MepDefaults,{postrollCloseText:mejs.i18n.t("Close")});$.extend(MediaElementPlayer.prototype,{buildpostroll:function(player,controls,layers,media){var t=this,postrollLink=t.container.find('link[rel="postroll"]').attr("href");if(typeof postrollLink!=="undefined"){player.postroll=$('<div class="mejs-postroll-layer mejs-layer"><a class="mejs-postroll-close" onclick="$(this).parent().hide();return false;">'+t.options.postrollCloseText+'</a><div class="mejs-postroll-layer-content"></div></div>').prependTo(layers).hide();t.media.addEventListener("ended",function(e){$.ajax({dataType:"html",url:postrollLink,success:function(data,textStatus){layers.find(".mejs-postroll-layer-content").html(data);}});player.postroll.show();},false);}}});})(mejs.$);
   /* Ende mediaelementplayer */
   /* Start jquery.socialshareprivacy */
   /*!
 * jquery.socialshareprivacy.js | 2 Klicks fuer mehr Datenschutz
 * 
 * Version 1.4.1
 * 
 * https://github.com/patrickheck/socialshareprivacy
 * http://www.heise.de/ct/artikel/2-Klicks-fuer-mehr-Datenschutz-1333879.html
 *
 * Copyright (c) 2011 Hilko Holweg, Sebastian Hilbig, Nicolas Heiringhoff, Juergen Schmidt
 * Heise Zeitschriften Verlag GmbH & Co. KG, http://www.heise.de,
 *
 * Copyright (c) 2011 Tilmann Kuhn
 * object-zoo, http://www.object-zoo.net
 *
 * is released under the MIT License http://www.opensource.org/licenses/mit-license.php
 *
 * Spread the word, link to us if you can.
 */
(function($){function abbreviateText(text,length){var abbreviated=decodeURIComponent(text);if(abbreviated.length<=length){return text;}var lastWhitespaceIndex=abbreviated.substring(0,length-1).lastIndexOf(" ");abbreviated=encodeURIComponent(abbreviated.substring(0,lastWhitespaceIndex))+"\u2026";return abbreviated;}function getMeta(name){var metaContent=$('meta[name="'+name+'"]').attr("content");return metaContent||"";}function getTweetText(){var title=getMeta("DC.title");var creator=getMeta("DC.creator");if(title.length>0&&creator.length>0){title+=" - "+creator;}else{title=$("title").text();}return encodeURIComponent(title);}function getURI(){var uri=document.location.href;var canonical=$("link[rel=canonical]").attr("href");if(canonical&&canonical.length>0){if(canonical.indexOf("http")<0){canonical=document.location.protocol+"//"+document.location.host+canonical;}uri=canonical;}return uri;}function cookieSet(name,value,days,path,domain){var expires=new Date();expires.setTime(expires.getTime()+(days*24*60*60*1000));document.cookie=name+"="+value+"; expires="+expires.toUTCString()+"; path="+path+"; domain="+domain;}function cookieDel(name,value,path,domain){var expires=new Date();expires.setTime(expires.getTime()-100);document.cookie=name+"="+value+"; expires="+expires.toUTCString()+"; path="+path+"; domain="+domain;}$.fn.socialSharePrivacy=function(settings){var defaults={"services":{"facebook":{"status":"on","dummy_img":"","txt_info":"2 Klicks f&uuml;r mehr Datenschutz: Erst wenn Sie hier klicken, wird der Button aktiv und Sie k&ouml;nnen Ihre Empfehlung an Facebook senden. Schon beim Aktivieren werden Daten an Dritte &uuml;bertragen &ndash; siehe <em>i</em>.","txt_fb_off":"nicht mit Facebook verbunden","txt_fb_on":"mit Facebook verbunden","perma_option":"on","display_name":"Facebook","referrer_track":"","language":"de_DE","action":"recommend","dummy_caption":"Empfehlen"},"twitter":{"status":"on","dummy_img":"","txt_info":"2 Klicks f&uuml;r mehr Datenschutz: Erst wenn Sie hier klicken, wird der Button aktiv und Sie k&ouml;nnen Ihre Empfehlung an Twitter senden. Schon beim Aktivieren werden Daten an Dritte &uuml;bertragen &ndash; siehe <em>i</em>.","txt_twitter_off":"nicht mit Twitter verbunden","txt_twitter_on":"mit Twitter verbunden","perma_option":"on","display_name":"Twitter","referrer_track":"","tweet_text":getTweetText,"language":"en","dummy_caption":"Tweet"},"gplus":{"status":"on","dummy_img":"","txt_info":"2 Klicks f&uuml;r mehr Datenschutz: Erst wenn Sie hier klicken, wird der Button aktiv und Sie k&ouml;nnen Ihre Empfehlung an Google+ senden. Schon beim Aktivieren werden Daten an Dritte &uuml;bertragen &ndash; siehe <em>i</em>.","txt_gplus_off":"nicht mit Google+ verbunden","txt_gplus_on":"mit Google+ verbunden","perma_option":"on","display_name":"Google+","referrer_track":"","language":"de"}},"info_link":"http://www.heise.de/ct/artikel/2-Klicks-fuer-mehr-Datenschutz-1333879.html","txt_help":"Wenn Sie diese Felder durch einen Klick aktivieren, werden Informationen an Facebook, Twitter oder Google in die USA &uuml;bertragen und unter Umst&auml;nden auch dort gespeichert. N&auml;heres erfahren Sie durch einen Klick auf das <em>i</em>.","settings_perma":"Dauerhaft aktivieren und Daten&uuml;ber&shy;tragung zustimmen:","cookie_path":"/","cookie_domain":document.location.host,"cookie_expires":"365","css_path":"socialshareprivacy/socialshareprivacy.css","uri":getURI};var options=$.extend(true,defaults,settings);var facebook_on=(options.services.facebook.status==="on");var twitter_on=(options.services.twitter.status==="on");var gplus_on=(options.services.gplus.status==="on");if(!facebook_on&&!twitter_on&&!gplus_on){return;}if(options.css_path.length>0){if(document.createStyleSheet){document.createStyleSheet(options.css_path);}else{$("head").append('<link rel="stylesheet" type="text/css" href="'+options.css_path+'" />');}}return this.each(function(){$(this).prepend('<ul class="social_share_privacy_area"></ul>');var context=$(".social_share_privacy_area",this);var uri=options.uri;if(typeof uri==="function"){uri=uri(context);}if(facebook_on){var fb_enc_uri=encodeURIComponent(uri+options.services.facebook.referrer_track);var fb_code='<iframe src="http://www.facebook.com/plugins/like.php?locale='+options.services.facebook.language+"&amp;href="+fb_enc_uri+"&amp;send=false&amp;layout=button_count&amp;width=120&amp;show_faces=false&amp;action="+options.services.facebook.action+'&amp;colorscheme=light&amp;font&amp;height=21" scrolling="no" frameborder="0" style="border:none; overflow:hidden; width:145px; height:21px;" allowTransparency="true"></iframe>';var fb_dummy_btn;if(options.services.facebook.dummy_img){fb_dummy_btn='<img class="fb_like_privacy_dummy" src="'+options.services.facebook.dummy_img+'" alt="'+options.services.facebook.dummy_caption+'" />';}else{fb_dummy_btn='<div class="fb_like_privacy_dummy"><span>'+options.services.facebook.dummy_caption+"</span></div>";}context.append('<li class="facebook help_info"><span class="info">'+options.services.facebook.txt_info+'</span><span class="switch off">'+options.services.facebook.txt_fb_off+'</span><div class="fb_like dummy_btn">'+fb_dummy_btn+"</div></li>");var $container_fb=$("li.facebook",context);context.on("click","li.facebook div.fb_like .fb_like_privacy_dummy,li.facebook span.switch",function(){if($container_fb.find("span.switch").hasClass("off")){$container_fb.addClass("info_off");$container_fb.find("span.switch").addClass("on").removeClass("off").html(options.services.facebook.txt_fb_on);$container_fb.find(".fb_like_privacy_dummy").replaceWith(fb_code);}else{$container_fb.removeClass("info_off");$container_fb.find("span.switch").addClass("off").removeClass("on").html(options.services.facebook.txt_fb_off);$container_fb.find(".fb_like").html(fb_dummy_btn);}});}if(twitter_on){var text=options.services.twitter.tweet_text;if(typeof text==="function"){text=text();}text=abbreviateText(text,"120");var twitter_enc_uri=encodeURIComponent(uri+options.services.twitter.referrer_track);var twitter_count_url=encodeURIComponent(uri);var twitter_code='<iframe allowtransparency="true" frameborder="0" scrolling="no" src="http://platform.twitter.com/widgets/tweet_button.html?url='+twitter_enc_uri+"&amp;counturl="+twitter_count_url+"&amp;text="+text+"&amp;count=horizontal&amp;lang="+options.services.twitter.language+'" style="width:130px; height:25px;"></iframe>';var twitter_dummy_btn;if(options.services.twitter.dummy_img){twitter_dummy_btn='<img class="tweet_this_dummy" src="'+options.services.twitter.dummy_img+'" alt="'+options.services.twitter.dummy_caption+'" />';}else{twitter_dummy_btn='<div class="tweet_this_dummy"><span>'+options.services.twitter.dummy_caption+"</span></div>";}context.append('<li class="twitter help_info"><span class="info">'+options.services.twitter.txt_info+'</span><span class="switch off">'+options.services.twitter.txt_twitter_off+'</span><div class="tweet dummy_btn">'+twitter_dummy_btn+"</div></li>");var $container_tw=$("li.twitter",context);context.on("click","li.twitter .tweet_this_dummy,li.twitter span.switch",function(){if($container_tw.find("span.switch").hasClass("off")){$container_tw.addClass("info_off");$container_tw.find("span.switch").addClass("on").removeClass("off").html(options.services.twitter.txt_twitter_on);$container_tw.find(".tweet_this_dummy").replaceWith(twitter_code);}else{$container_tw.removeClass("info_off");$container_tw.find("span.switch").addClass("off").removeClass("on").html(options.services.twitter.txt_twitter_off);$container_tw.find(".tweet").html(twitter_dummy_btn);}});}if(gplus_on){var gplus_uri=uri+options.services.gplus.referrer_track;var gplus_code='<div class="g-plusone" data-size="medium" data-href="'+gplus_uri+'"></div><script type="text/javascript">window.___gcfg = {lang: "'+options.services.gplus.language+'"}; (function() { var po = document.createElement("script"); po.type = "text/javascript"; po.async = true; po.src = "https://apis.google.com/js/plusone.js"; var s = document.getElementsByTagName("script")[0]; s.parentNode.insertBefore(po, s); })(); <\/script>';var gplus_dummy_btn;if(options.services.gplus.dummy_img){gplus_dummy_btn='<img src="'+options.services.gplus.dummy_img+'" alt="+1" class="gplus_one_dummy" />';}else{gplus_dummy_btn='<div class="gplus_one_dummy">+1</div>';}context.append('<li class="gplus help_info"><span class="info">'+options.services.gplus.txt_info+'</span><span class="switch off">'+options.services.gplus.txt_gplus_off+'</span><div class="gplusone dummy_btn">'+gplus_dummy_btn+"</div></li>");var $container_gplus=$("li.gplus",context);context.on("click","li.gplus div.gplusone .gplus_one_dummy,li.gplus span.switch",function(){if($container_gplus.find("span.switch").hasClass("off")){$container_gplus.addClass("info_off");$container_gplus.find("span.switch").addClass("on").removeClass("off").html(options.services.gplus.txt_gplus_on);$container_gplus.find(".gplus_one_dummy").replaceWith(gplus_code);}else{$container_gplus.removeClass("info_off");$container_gplus.find("span.switch").addClass("off").removeClass("on").html(options.services.gplus.txt_gplus_off);$container_gplus.find(".gplusone").html(gplus_dummy_btn);}});}context.append('<li class="settings_info"><div class="settings_info_menu off perma_option_off"><a href="'+options.info_link+'"><span class="help_info icon"><span class="info">'+options.txt_help+"</span></span></a></div></li>");context.on("mouseenter",".help_info:not(.info_off)",function(){var $info_wrapper=$(this);var timeout_id=window.setTimeout(function(){$($info_wrapper).addClass("display");},500);$(this).data("timeout_id",timeout_id);});context.on("mouseleave",".help_info",function(){var timeout_id=$(this).data("timeout_id");window.clearTimeout(timeout_id);if($(this).hasClass("display")){$(this).removeClass("display");}});var facebook_perma=(options.services.facebook.perma_option==="on");var twitter_perma=(options.services.twitter.perma_option==="on");var gplus_perma=(options.services.gplus.perma_option==="on");if(((facebook_on&&facebook_perma)||(twitter_on&&twitter_perma)||(gplus_on&&gplus_perma))&&(!!JSON&&!!JSON.parse)){var cookie_list=document.cookie.split(";");var cookies="{";var i=0;for(;i<cookie_list.length;i+=1){var foo=cookie_list[i].split("=");cookies+='"'+$.trim(foo[0])+'":"'+$.trim(foo[1])+'"';if(i<cookie_list.length-1){cookies+=",";}}cookies+="}";cookies=JSON.parse(cookies);var $container_settings_info=$("li.settings_info",context);$container_settings_info.find(".settings_info_menu").removeClass("perma_option_off");$container_settings_info.find(".settings_info_menu").append('<span class="settings">Einstellungen</span><form><fieldset><legend>'+options.settings_perma+"</legend></fieldset></form>");var checked=' checked="checked"';if(facebook_on&&facebook_perma){var perma_status_facebook=cookies.socialSharePrivacy_facebook==="perma_on"?checked:"";$container_settings_info.find("form fieldset").append('<input type="checkbox" name="perma_status_facebook" id="perma_status_facebook"'+perma_status_facebook+' /><label for="perma_status_facebook">'+options.services.facebook.display_name+"</label>");}if(twitter_on&&twitter_perma){var perma_status_twitter=cookies.socialSharePrivacy_twitter==="perma_on"?checked:"";$container_settings_info.find("form fieldset").append('<input type="checkbox" name="perma_status_twitter" id="perma_status_twitter"'+perma_status_twitter+' /><label for="perma_status_twitter">'+options.services.twitter.display_name+"</label>");}if(gplus_on&&gplus_perma){var perma_status_gplus=cookies.socialSharePrivacy_gplus==="perma_on"?checked:"";$container_settings_info.find("form fieldset").append('<input type="checkbox" name="perma_status_gplus" id="perma_status_gplus"'+perma_status_gplus+' /><label for="perma_status_gplus">'+options.services.gplus.display_name+"</label>");}$container_settings_info.find("span.settings").css("cursor","pointer");$container_settings_info.on("mouseenter","span.settings",function(){var timeout_id=window.setTimeout(function(){$container_settings_info.find(".settings_info_menu").removeClass("off").addClass("on");},500);$(this).data("timeout_id",timeout_id);});$container_settings_info.on("mouseleave",function(){var timeout_id=$(this).data("timeout_id");window.clearTimeout(timeout_id);$container_settings_info.find(".settings_info_menu").removeClass("on").addClass("off");});$container_settings_info.on("click","fieldset input",function(event){var click=event.target.id;var service=click.substr(click.lastIndexOf("_")+1,click.length);var cookie_name="socialSharePrivacy_"+service;if($("#"+event.target.id+":checked").length){cookieSet(cookie_name,"perma_on",options.cookie_expires,options.cookie_path,options.cookie_domain);$("form fieldset label[for="+click+"]",context).addClass("checked");}else{cookieDel(cookie_name,"perma_on",options.cookie_path,options.cookie_domain);$("form fieldset label[for="+click+"]",context).removeClass("checked");}});if(facebook_on&&facebook_perma&&cookies.socialSharePrivacy_facebook==="perma_on"){$("li.facebook span.switch",context).click();}if(twitter_on&&twitter_perma&&cookies.socialSharePrivacy_twitter==="perma_on"){$("li.twitter span.switch",context).click();}if(gplus_on&&gplus_perma&&cookies.socialSharePrivacy_gplus==="perma_on"){$("li.gplus span.switch",context).click();}}});};}(jQuery));
   /* Ende jquery.socialshareprivacy */
   /* Start slick */
   /*!
     _ _      _       _
 ___| (_) ___| | __  (_)___
/ __| | |/ __| |/ /  | / __|
\__ \ | | (__|   < _ | \__ \
|___/_|_|\___|_|\_(_)/ |___/
                   |__/

 Version: 1.3.15
  Author: Ken Wheeler
 Website: http://kenwheeler.github.io
    Docs: http://kenwheeler.github.io/slick
    Repo: http://github.com/kenwheeler/slick
  Issues: http://github.com/kenwheeler/slick/issues

 */
(function(factory){if(typeof define==="function"&&define.amd){define(["jquery"],factory);}else{if(typeof exports!=="undefined"){module.exports=factory(require("jquery"));}else{factory(jQuery);}}}(function($){var Slick=window.Slick||{};Slick=(function(){var instanceUid=0;function Slick(element,settings){var _=this,responsiveSettings,breakpoint;_.defaults={accessibility:true,adaptiveHeight:false,appendArrows:$(element),appendDots:$(element),arrows:true,asNavFor:null,prevArrow:'<button type="button" data-role="none" class="slick-prev">Previous</button>',nextArrow:'<button type="button" data-role="none" class="slick-next">Next</button>',autoplay:false,autoplaySpeed:3000,centerMode:false,centerPadding:"50px",cssEase:"ease",customPaging:function(slider,i){return'<button type="button" data-role="none">'+(i+1)+"</button>";},dots:false,dotsClass:"slick-dots",draggable:true,easing:"linear",fade:false,focusOnSelect:false,infinite:true,initialSlide:0,lazyLoad:"ondemand",onBeforeChange:null,onAfterChange:null,onInit:null,onReInit:null,onSetPosition:null,pauseOnHover:true,pauseOnDotsHover:false,respondTo:"window",responsive:null,rtl:false,slide:"div",slidesToShow:1,slidesToScroll:1,speed:500,swipe:true,swipeToSlide:false,touchMove:true,touchThreshold:5,useCSS:true,variableWidth:false,vertical:false,waitForAnimate:true};_.initials={animating:false,dragging:false,autoPlayTimer:null,currentDirection:0,currentLeft:null,currentSlide:0,direction:1,$dots:null,listWidth:null,listHeight:null,loadIndex:0,$nextArrow:null,$prevArrow:null,slideCount:null,slideWidth:null,$slideTrack:null,$slides:null,sliding:false,slideOffset:0,swipeLeft:null,$list:null,touchObject:{},transformsEnabled:false};$.extend(_,_.initials);_.activeBreakpoint=null;_.animType=null;_.animProp=null;_.breakpoints=[];_.breakpointSettings=[];_.cssTransitions=false;_.paused=false;_.positionProp=null;_.respondTo=null;_.shouldClick=true;_.$slider=$(element);_.$slidesCache=null;_.transformType=null;_.transitionType=null;_.windowWidth=0;_.windowTimer=null;_.options=$.extend({},_.defaults,settings);_.currentSlide=_.options.initialSlide;_.originalSettings=_.options;responsiveSettings=_.options.responsive||null;if(responsiveSettings&&responsiveSettings.length>-1){_.respondTo=_.options.respondTo||"window";for(breakpoint in responsiveSettings){if(responsiveSettings.hasOwnProperty(breakpoint)){_.breakpoints.push(responsiveSettings[breakpoint].breakpoint);_.breakpointSettings[responsiveSettings[breakpoint].breakpoint]=responsiveSettings[breakpoint].settings;}}_.breakpoints.sort(function(a,b){return b-a;});}_.autoPlay=$.proxy(_.autoPlay,_);_.autoPlayClear=$.proxy(_.autoPlayClear,_);_.changeSlide=$.proxy(_.changeSlide,_);_.clickHandler=$.proxy(_.clickHandler,_);_.selectHandler=$.proxy(_.selectHandler,_);_.setPosition=$.proxy(_.setPosition,_);_.swipeHandler=$.proxy(_.swipeHandler,_);_.dragHandler=$.proxy(_.dragHandler,_);_.keyHandler=$.proxy(_.keyHandler,_);_.autoPlayIterator=$.proxy(_.autoPlayIterator,_);_.instanceUid=instanceUid++;_.htmlExpr=/^(?:\s*(<[\w\W]+>)[^>]*)$/;_.init();_.checkResponsive();}return Slick;}());Slick.prototype.addSlide=function(markup,index,addBefore){var _=this;if(typeof(index)==="boolean"){addBefore=index;index=null;}else{if(index<0||(index>=_.slideCount)){return false;}}_.unload();if(typeof(index)==="number"){if(index===0&&_.$slides.length===0){$(markup).appendTo(_.$slideTrack);}else{if(addBefore){$(markup).insertBefore(_.$slides.eq(index));}else{$(markup).insertAfter(_.$slides.eq(index));}}}else{if(addBefore===true){$(markup).prependTo(_.$slideTrack);}else{$(markup).appendTo(_.$slideTrack);}}_.$slides=_.$slideTrack.children(this.options.slide);_.$slideTrack.children(this.options.slide).detach();_.$slideTrack.append(_.$slides);_.$slides.each(function(index,element){$(element).attr("index",index);});_.$slidesCache=_.$slides;_.reinit();};Slick.prototype.animateSlide=function(targetLeft,callback){var animProps={},_=this;if(_.options.slidesToShow===1&&_.options.adaptiveHeight===true&&_.options.vertical===false){var targetHeight=_.$slides.eq(_.currentSlide).outerHeight(true);_.$list.animate({height:targetHeight},_.options.speed);}if(_.options.rtl===true&&_.options.vertical===false){targetLeft=-targetLeft;}if(_.transformsEnabled===false){if(_.options.vertical===false){_.$slideTrack.animate({left:targetLeft},_.options.speed,_.options.easing,callback);}else{_.$slideTrack.animate({top:targetLeft},_.options.speed,_.options.easing,callback);}}else{if(_.cssTransitions===false){$({animStart:_.currentLeft}).animate({animStart:targetLeft},{duration:_.options.speed,easing:_.options.easing,step:function(now){if(_.options.vertical===false){animProps[_.animType]="translate("+now+"px, 0px)";_.$slideTrack.css(animProps);}else{animProps[_.animType]="translate(0px,"+now+"px)";_.$slideTrack.css(animProps);}},complete:function(){if(callback){callback.call();}}});}else{_.applyTransition();if(_.options.vertical===false){animProps[_.animType]="translate3d("+targetLeft+"px, 0px, 0px)";}else{animProps[_.animType]="translate3d(0px,"+targetLeft+"px, 0px)";}_.$slideTrack.css(animProps);if(callback){setTimeout(function(){_.disableTransition();callback.call();},_.options.speed);}}}};Slick.prototype.asNavFor=function(index){var _=this,asNavFor=_.options.asNavFor!=null?$(_.options.asNavFor).getSlick():null;if(asNavFor!=null){asNavFor.slideHandler(index,true);}};Slick.prototype.applyTransition=function(slide){var _=this,transition={};if(_.options.fade===false){transition[_.transitionType]=_.transformType+" "+_.options.speed+"ms "+_.options.cssEase;}else{transition[_.transitionType]="opacity "+_.options.speed+"ms "+_.options.cssEase;}if(_.options.fade===false){_.$slideTrack.css(transition);}else{_.$slides.eq(slide).css(transition);}};Slick.prototype.autoPlay=function(){var _=this;if(_.autoPlayTimer){clearInterval(_.autoPlayTimer);}if(_.slideCount>_.options.slidesToShow&&_.paused!==true){_.autoPlayTimer=setInterval(_.autoPlayIterator,_.options.autoplaySpeed);}};Slick.prototype.autoPlayClear=function(){var _=this;if(_.autoPlayTimer){clearInterval(_.autoPlayTimer);}};Slick.prototype.autoPlayIterator=function(){var _=this;if(_.options.infinite===false){if(_.direction===1){if((_.currentSlide+1)===_.slideCount-1){_.direction=0;}_.slideHandler(_.currentSlide+_.options.slidesToScroll);}else{if((_.currentSlide-1===0)){_.direction=1;}_.slideHandler(_.currentSlide-_.options.slidesToScroll);}}else{_.slideHandler(_.currentSlide+_.options.slidesToScroll);}};Slick.prototype.buildArrows=function(){var _=this;if(_.options.arrows===true&&_.slideCount>_.options.slidesToShow){_.$prevArrow=$(_.options.prevArrow);_.$nextArrow=$(_.options.nextArrow);if(_.htmlExpr.test(_.options.prevArrow)){_.$prevArrow.appendTo(_.options.appendArrows);}if(_.htmlExpr.test(_.options.nextArrow)){_.$nextArrow.appendTo(_.options.appendArrows);}if(_.options.infinite!==true){_.$prevArrow.addClass("slick-disabled");}}};Slick.prototype.buildDots=function(){var _=this,i,dotString;if(_.options.dots===true&&_.slideCount>_.options.slidesToShow){dotString='<ul class="'+_.options.dotsClass+'">';for(i=0;i<=_.getDotCount();i+=1){dotString+="<li>"+_.options.customPaging.call(this,_,i)+"</li>";}dotString+="</ul>";_.$dots=$(dotString).appendTo(_.options.appendDots);_.$dots.find("li").first().addClass("slick-active");}};Slick.prototype.buildOut=function(){var _=this;_.$slides=_.$slider.children(_.options.slide+":not(.slick-cloned)").addClass("slick-slide");_.slideCount=_.$slides.length;_.$slides.each(function(index,element){$(element).attr("index",index);});_.$slidesCache=_.$slides;_.$slider.addClass("slick-slider");_.$slideTrack=(_.slideCount===0)?$('<div class="slick-track"/>').appendTo(_.$slider):_.$slides.wrapAll('<div class="slick-track"/>').parent();_.$list=_.$slideTrack.wrap('<div class="slick-list"/>').parent();_.$slideTrack.css("opacity",0);if(_.options.centerMode===true){_.options.slidesToScroll=1;}$("img[data-lazy]",_.$slider).not("[src]").addClass("slick-loading");_.setupInfinite();_.buildArrows();_.buildDots();_.updateDots();if(_.options.accessibility===true){_.$list.prop("tabIndex",0);}_.setSlideClasses(typeof this.currentSlide==="number"?this.currentSlide:0);if(_.options.draggable===true){_.$list.addClass("draggable");}};Slick.prototype.checkResponsive=function(){var _=this,breakpoint,targetBreakpoint,respondToWidth;var sliderWidth=_.$slider.width();var windowWidth=window.innerWidth||$(window).width();if(_.respondTo==="window"){respondToWidth=windowWidth;}else{if(_.respondTo==="slider"){respondToWidth=sliderWidth;}else{if(_.respondTo==="min"){respondToWidth=Math.min(windowWidth,sliderWidth);}}}if(_.originalSettings.responsive&&_.originalSettings.responsive.length>-1&&_.originalSettings.responsive!==null){targetBreakpoint=null;for(breakpoint in _.breakpoints){if(_.breakpoints.hasOwnProperty(breakpoint)){if(respondToWidth<_.breakpoints[breakpoint]){targetBreakpoint=_.breakpoints[breakpoint];}}}if(targetBreakpoint!==null){if(_.activeBreakpoint!==null){if(targetBreakpoint!==_.activeBreakpoint){_.activeBreakpoint=targetBreakpoint;_.options=$.extend({},_.originalSettings,_.breakpointSettings[targetBreakpoint]);_.refresh();}}else{_.activeBreakpoint=targetBreakpoint;_.options=$.extend({},_.originalSettings,_.breakpointSettings[targetBreakpoint]);_.refresh();}}else{if(_.activeBreakpoint!==null){_.activeBreakpoint=null;_.options=_.originalSettings;_.refresh();}}}};Slick.prototype.changeSlide=function(event,dontAnimate){var _=this,$target=$(event.target),indexOffset,slideOffset,unevenOffset,navigables,prevNavigable;$target.is("a")&&event.preventDefault();unevenOffset=(_.slideCount%_.options.slidesToScroll!==0);indexOffset=unevenOffset?0:(_.slideCount-_.currentSlide)%_.options.slidesToScroll;switch(event.data.message){case"previous":slideOffset=indexOffset===0?_.options.slidesToScroll:_.options.slidesToShow-indexOffset;if(_.slideCount>_.options.slidesToShow){_.slideHandler(_.currentSlide-slideOffset,false,dontAnimate);}break;case"next":slideOffset=indexOffset===0?_.options.slidesToScroll:indexOffset;if(_.slideCount>_.options.slidesToShow){_.slideHandler(_.currentSlide+slideOffset,false,dontAnimate);}break;case"index":var index=event.data.index===0?0:event.data.index||$(event.target).parent().index()*_.options.slidesToScroll;navigables=_.getNavigableIndexes();prevNavigable=0;if(navigables[index]&&navigables[index]===index){if(index>navigables[navigables.length-1]){index=navigables[navigables.length-1];}else{for(var n in navigables){if(index<navigables[n]){index=prevNavigable;break;}prevNavigable=navigables[n];}}}_.slideHandler(index,false,dontAnimate);default:return;}};Slick.prototype.clickHandler=function(event){var _=this;if(_.shouldClick===false){event.stopImmediatePropagation();event.stopPropagation();event.preventDefault();}};Slick.prototype.destroy=function(){var _=this;_.autoPlayClear();_.touchObject={};$(".slick-cloned",_.$slider).remove();if(_.$dots){_.$dots.remove();}if(_.$prevArrow&&(typeof _.options.prevArrow!=="object")){_.$prevArrow.remove();}if(_.$nextArrow&&(typeof _.options.nextArrow!=="object")){_.$nextArrow.remove();}if(_.$slides.parent().hasClass("slick-track")){_.$slides.unwrap().unwrap();}_.$slides.removeClass("slick-slide slick-active slick-center slick-visible").removeAttr("index").css({position:"",left:"",top:"",zIndex:"",opacity:"",width:""});_.$slider.removeClass("slick-slider");_.$slider.removeClass("slick-initialized");_.$list.off(".slick");$(window).off(".slick-"+_.instanceUid);$(document).off(".slick-"+_.instanceUid);};Slick.prototype.disableTransition=function(slide){var _=this,transition={};transition[_.transitionType]="";if(_.options.fade===false){_.$slideTrack.css(transition);}else{_.$slides.eq(slide).css(transition);}};Slick.prototype.fadeSlide=function(oldSlide,slideIndex,callback){var _=this;if(_.cssTransitions===false){_.$slides.eq(slideIndex).css({zIndex:1000});_.$slides.eq(slideIndex).animate({opacity:1},_.options.speed,_.options.easing,callback);_.$slides.eq(oldSlide).animate({opacity:0},_.options.speed,_.options.easing);}else{_.applyTransition(slideIndex);_.applyTransition(oldSlide);_.$slides.eq(slideIndex).css({opacity:1,zIndex:1000});_.$slides.eq(oldSlide).css({opacity:0});if(callback){setTimeout(function(){_.disableTransition(slideIndex);_.disableTransition(oldSlide);callback.call();},_.options.speed);}}};Slick.prototype.filterSlides=function(filter){var _=this;if(filter!==null){_.unload();_.$slideTrack.children(this.options.slide).detach();_.$slidesCache.filter(filter).appendTo(_.$slideTrack);_.reinit();}};Slick.prototype.getCurrent=function(){var _=this;return _.currentSlide;};Slick.prototype.getDotCount=function(){var _=this;var breakPoint=0;var counter=0;var pagerQty=0;if(_.options.infinite===true){pagerQty=Math.ceil(_.slideCount/_.options.slidesToScroll);}else{while(breakPoint<_.slideCount){++pagerQty;breakPoint=counter+_.options.slidesToShow;counter+=_.options.slidesToScroll<=_.options.slidesToShow?_.options.slidesToScroll:_.options.slidesToShow;}}return pagerQty-1;};Slick.prototype.getLeft=function(slideIndex){var _=this,targetLeft,verticalHeight,verticalOffset=0,slideWidth,targetSlide;_.slideOffset=0;verticalHeight=_.$slides.first().outerHeight();if(_.options.infinite===true){if(_.slideCount>_.options.slidesToShow){_.slideOffset=(_.slideWidth*_.options.slidesToShow)*-1;verticalOffset=(verticalHeight*_.options.slidesToShow)*-1;}if(_.slideCount%_.options.slidesToScroll!==0){if(slideIndex+_.options.slidesToScroll>_.slideCount&&_.slideCount>_.options.slidesToShow){if(slideIndex>_.slideCount){_.slideOffset=((_.options.slidesToShow-(slideIndex-_.slideCount))*_.slideWidth)*-1;verticalOffset=((_.options.slidesToShow-(slideIndex-_.slideCount))*verticalHeight)*-1;}else{_.slideOffset=((_.slideCount%_.options.slidesToScroll)*_.slideWidth)*-1;verticalOffset=((_.slideCount%_.options.slidesToScroll)*verticalHeight)*-1;}}}}else{if(slideIndex+_.options.slidesToShow>_.slideCount){_.slideOffset=((slideIndex+_.options.slidesToShow)-_.slideCount)*_.slideWidth;verticalOffset=((slideIndex+_.options.slidesToShow)-_.slideCount)*verticalHeight;}}if(_.slideCount<=_.options.slidesToShow){_.slideOffset=0;verticalOffset=0;}if(_.options.centerMode===true&&_.options.infinite===true){_.slideOffset+=_.slideWidth*Math.floor(_.options.slidesToShow/2)-_.slideWidth;}else{if(_.options.centerMode===true){_.slideOffset=0;_.slideOffset+=_.slideWidth*Math.floor(_.options.slidesToShow/2);}}if(_.options.vertical===false){targetLeft=((slideIndex*_.slideWidth)*-1)+_.slideOffset;}else{targetLeft=((slideIndex*verticalHeight)*-1)+verticalOffset;}if(_.options.variableWidth===true){if(_.slideCount<=_.options.slidesToShow||_.options.infinite===false){targetSlide=_.$slideTrack.children(".slick-slide").eq(slideIndex);}else{targetSlide=_.$slideTrack.children(".slick-slide").eq(slideIndex+_.options.slidesToShow);}targetLeft=targetSlide[0]?targetSlide[0].offsetLeft*-1:0;if(_.options.centerMode===true){if(_.options.infinite===false){targetSlide=_.$slideTrack.children(".slick-slide").eq(slideIndex);}else{targetSlide=_.$slideTrack.children(".slick-slide").eq(slideIndex+_.options.slidesToShow+1);}targetLeft=targetSlide[0]?targetSlide[0].offsetLeft*-1:0;targetLeft+=(_.$list.width()-targetSlide.outerWidth())/2;}}return targetLeft;};Slick.prototype.getNavigableIndexes=function(){var _=this;var breakPoint=0;var counter=0;var indexes=[];while(breakPoint<_.slideCount){indexes.push(breakPoint);breakPoint=counter+_.options.slidesToScroll;counter+=_.options.slidesToScroll<=_.options.slidesToShow?_.options.slidesToScroll:_.options.slidesToShow;}return indexes;};Slick.prototype.getSlideCount=function(){var _=this,slidesTraversed;if(_.options.swipeToSlide===true){var swipedSlide=null;_.$slideTrack.find(".slick-slide").each(function(index,slide){if(slide.offsetLeft+($(slide).outerWidth()/2)>(_.swipeLeft*-1)){swipedSlide=slide;return false;}});slidesTraversed=Math.abs($(swipedSlide).attr("index")-_.currentSlide);return slidesTraversed;}else{return _.options.slidesToScroll;}};Slick.prototype.init=function(){var _=this;if(!$(_.$slider).hasClass("slick-initialized")){$(_.$slider).addClass("slick-initialized");_.buildOut();_.setProps();_.startLoad();_.loadSlider();_.initializeEvents();_.updateArrows();_.updateDots();}if(_.options.onInit!==null){_.options.onInit.call(this,_);}};Slick.prototype.initArrowEvents=function(){var _=this;if(_.options.arrows===true&&_.slideCount>_.options.slidesToShow){_.$prevArrow.on("click.slick",{message:"previous"},_.changeSlide);_.$nextArrow.on("click.slick",{message:"next"},_.changeSlide);}};Slick.prototype.initDotEvents=function(){var _=this;if(_.options.dots===true&&_.slideCount>_.options.slidesToShow){$("li",_.$dots).on("click.slick",{message:"index"},_.changeSlide);}if(_.options.dots===true&&_.options.pauseOnDotsHover===true&&_.options.autoplay===true){$("li",_.$dots).on("mouseenter.slick",function(){_.paused=true;_.autoPlayClear();}).on("mouseleave.slick",function(){_.paused=false;_.autoPlay();});}};Slick.prototype.initializeEvents=function(){var _=this;_.initArrowEvents();_.initDotEvents();_.$list.on("touchstart.slick mousedown.slick",{action:"start"},_.swipeHandler);_.$list.on("touchmove.slick mousemove.slick",{action:"move"},_.swipeHandler);_.$list.on("touchend.slick mouseup.slick",{action:"end"},_.swipeHandler);_.$list.on("touchcancel.slick mouseleave.slick",{action:"end"},_.swipeHandler);_.$list.on("click.slick",_.clickHandler);if(_.options.pauseOnHover===true&&_.options.autoplay===true){_.$list.on("mouseenter.slick",function(){_.paused=true;_.autoPlayClear();});_.$list.on("mouseleave.slick",function(){_.paused=false;_.autoPlay();});}if(_.options.accessibility===true){_.$list.on("keydown.slick",_.keyHandler);}if(_.options.focusOnSelect===true){$(_.options.slide,_.$slideTrack).on("click.slick",_.selectHandler);}$(window).on("orientationchange.slick.slick-"+_.instanceUid,function(){_.checkResponsive();_.setPosition();});$(window).on("resize.slick.slick-"+_.instanceUid,function(){if($(window).width()!==_.windowWidth){clearTimeout(_.windowDelay);_.windowDelay=window.setTimeout(function(){_.windowWidth=$(window).width();_.checkResponsive();_.setPosition();},50);}});$("*[draggable!=true]",_.$slideTrack).on("dragstart",function(e){e.preventDefault();});$(window).on("load.slick.slick-"+_.instanceUid,_.setPosition);$(document).on("ready.slick.slick-"+_.instanceUid,_.setPosition);};Slick.prototype.initUI=function(){var _=this;if(_.options.arrows===true&&_.slideCount>_.options.slidesToShow){_.$prevArrow.show();_.$nextArrow.show();}if(_.options.dots===true&&_.slideCount>_.options.slidesToShow){_.$dots.show();}if(_.options.autoplay===true){_.autoPlay();}};Slick.prototype.keyHandler=function(event){var _=this;if(event.keyCode===37&&_.options.accessibility===true){_.changeSlide({data:{message:"previous"}});}else{if(event.keyCode===39&&_.options.accessibility===true){_.changeSlide({data:{message:"next"}});}}};Slick.prototype.lazyLoad=function(){var _=this,loadRange,cloneRange,rangeStart,rangeEnd;function loadImages(imagesScope){$("img[data-lazy]",imagesScope).each(function(){var image=$(this),imageSource=$(this).attr("data-lazy");image.load(function(){image.animate({opacity:1},200);}).css({opacity:0}).attr("src",imageSource).removeAttr("data-lazy").removeClass("slick-loading");});}if(_.options.centerMode===true){if(_.options.infinite===true){rangeStart=_.currentSlide+(_.options.slidesToShow/2+1);rangeEnd=rangeStart+_.options.slidesToShow+2;}else{rangeStart=Math.max(0,_.currentSlide-(_.options.slidesToShow/2+1));rangeEnd=2+(_.options.slidesToShow/2+1)+_.currentSlide;}}else{rangeStart=_.options.infinite?_.options.slidesToShow+_.currentSlide:_.currentSlide;rangeEnd=rangeStart+_.options.slidesToShow;if(_.options.fade===true){if(rangeStart>0){rangeStart--;}if(rangeEnd<=_.slideCount){rangeEnd++;}}}loadRange=_.$slider.find(".slick-slide").slice(rangeStart,rangeEnd);loadImages(loadRange);if(_.slideCount<=_.options.slidesToShow){cloneRange=_.$slider.find(".slick-slide");loadImages(cloneRange);}else{if(_.currentSlide>=_.slideCount-_.options.slidesToShow){cloneRange=_.$slider.find(".slick-cloned").slice(0,_.options.slidesToShow);loadImages(cloneRange);}else{if(_.currentSlide===0){cloneRange=_.$slider.find(".slick-cloned").slice(_.options.slidesToShow*-1);loadImages(cloneRange);}}}};Slick.prototype.loadSlider=function(){var _=this;_.setPosition();_.$slideTrack.css({opacity:1});_.$slider.removeClass("slick-loading");_.initUI();if(_.options.lazyLoad==="progressive"){_.progressiveLazyLoad();}};Slick.prototype.postSlide=function(index){var _=this;if(_.options.onAfterChange!==null){_.options.onAfterChange.call(this,_,index);}_.animating=false;_.setPosition();_.swipeLeft=null;if(_.options.autoplay===true&&_.paused===false){_.autoPlay();}};Slick.prototype.progressiveLazyLoad=function(){var _=this,imgCount,targetImage;imgCount=$("img[data-lazy]",_.$slider).length;if(imgCount>0){targetImage=$("img[data-lazy]",_.$slider).first();targetImage.attr("src",targetImage.attr("data-lazy")).removeClass("slick-loading").load(function(){targetImage.removeAttr("data-lazy");_.progressiveLazyLoad();}).error(function(){targetImage.removeAttr("data-lazy");_.progressiveLazyLoad();});}};Slick.prototype.refresh=function(){var _=this,currentSlide=_.currentSlide;_.destroy();$.extend(_,_.initials);_.init();_.changeSlide({data:{message:"index",index:currentSlide,}},true);};Slick.prototype.reinit=function(){var _=this;_.$slides=_.$slideTrack.children(_.options.slide).addClass("slick-slide");_.slideCount=_.$slides.length;if(_.currentSlide>=_.slideCount&&_.currentSlide!==0){_.currentSlide=_.currentSlide-_.options.slidesToScroll;}if(_.slideCount<=_.options.slidesToShow){_.currentSlide=0;}_.setProps();_.setupInfinite();_.buildArrows();_.updateArrows();_.initArrowEvents();_.buildDots();_.updateDots();_.initDotEvents();if(_.options.focusOnSelect===true){$(_.options.slide,_.$slideTrack).on("click.slick",_.selectHandler);}_.setSlideClasses(0);_.setPosition();if(_.options.onReInit!==null){_.options.onReInit.call(this,_);}};Slick.prototype.removeSlide=function(index,removeBefore,removeAll){var _=this;if(typeof(index)==="boolean"){removeBefore=index;index=removeBefore===true?0:_.slideCount-1;}else{index=removeBefore===true?--index:index;}if(_.slideCount<1||index<0||index>_.slideCount-1){return false;}_.unload();if(removeAll===true){_.$slideTrack.children().remove();}else{_.$slideTrack.children(this.options.slide).eq(index).remove();}_.$slides=_.$slideTrack.children(this.options.slide);_.$slideTrack.children(this.options.slide).detach();_.$slideTrack.append(_.$slides);_.$slidesCache=_.$slides;_.reinit();};Slick.prototype.setCSS=function(position){var _=this,positionProps={},x,y;if(_.options.rtl===true){position=-position;}x=_.positionProp=="left"?position+"px":"0px";y=_.positionProp=="top"?position+"px":"0px";positionProps[_.positionProp]=position;if(_.transformsEnabled===false){_.$slideTrack.css(positionProps);}else{positionProps={};if(_.cssTransitions===false){positionProps[_.animType]="translate("+x+", "+y+")";_.$slideTrack.css(positionProps);}else{positionProps[_.animType]="translate3d("+x+", "+y+", 0px)";_.$slideTrack.css(positionProps);}}};Slick.prototype.setDimensions=function(){var _=this;if(_.options.vertical===false){if(_.options.centerMode===true){_.$list.css({padding:("0px "+_.options.centerPadding)});}}else{_.$list.height(_.$slides.first().outerHeight(true)*_.options.slidesToShow);if(_.options.centerMode===true){_.$list.css({padding:(_.options.centerPadding+" 0px")});}}_.listWidth=_.$list.width();_.listHeight=_.$list.height();if(_.options.vertical===false&&_.options.variableWidth===false){_.slideWidth=Math.ceil(_.listWidth/_.options.slidesToShow);_.$slideTrack.width(Math.ceil((_.slideWidth*_.$slideTrack.children(".slick-slide").length)));}else{if(_.options.variableWidth===true){var trackWidth=0;_.slideWidth=Math.ceil(_.listWidth/_.options.slidesToShow);_.$slideTrack.children(".slick-slide").each(function(){trackWidth+=Math.ceil($(this).outerWidth(true));});_.$slideTrack.width(Math.ceil(trackWidth)+1);}else{_.slideWidth=Math.ceil(_.listWidth);_.$slideTrack.height(Math.ceil((_.$slides.first().outerHeight(true)*_.$slideTrack.children(".slick-slide").length)));}}var offset=_.$slides.first().outerWidth(true)-_.$slides.first().width();if(_.options.variableWidth===false){_.$slideTrack.children(".slick-slide").width(_.slideWidth-offset);}};Slick.prototype.setFade=function(){var _=this,targetLeft;_.$slides.each(function(index,element){targetLeft=(_.slideWidth*index)*-1;if(_.options.rtl===true){$(element).css({position:"relative",right:targetLeft,top:0,zIndex:800,opacity:0});}else{$(element).css({position:"relative",left:targetLeft,top:0,zIndex:800,opacity:0});}});_.$slides.eq(_.currentSlide).css({zIndex:900,opacity:1});};Slick.prototype.setHeight=function(){var _=this;if(_.options.slidesToShow===1&&_.options.adaptiveHeight===true&&_.options.vertical===false){var targetHeight=_.$slides.eq(_.currentSlide).outerHeight(true);_.$list.css("height",targetHeight);}};Slick.prototype.setPosition=function(){var _=this;_.setDimensions();_.setHeight();if(_.options.fade===false){_.setCSS(_.getLeft(_.currentSlide));}else{_.setFade();}if(_.options.onSetPosition!==null){_.options.onSetPosition.call(this,_);}};Slick.prototype.setProps=function(){var _=this,bodyStyle=document.body.style;_.positionProp=_.options.vertical===true?"top":"left";if(_.positionProp==="top"){_.$slider.addClass("slick-vertical");}else{_.$slider.removeClass("slick-vertical");}if(bodyStyle.WebkitTransition!==undefined||bodyStyle.MozTransition!==undefined||bodyStyle.msTransition!==undefined){if(_.options.useCSS===true){_.cssTransitions=true;}}if(bodyStyle.OTransform!==undefined){_.animType="OTransform";_.transformType="-o-transform";_.transitionType="OTransition";if(bodyStyle.perspectiveProperty===undefined&&bodyStyle.webkitPerspective===undefined){_.animType=false;}}if(bodyStyle.MozTransform!==undefined){_.animType="MozTransform";_.transformType="-moz-transform";_.transitionType="MozTransition";if(bodyStyle.perspectiveProperty===undefined&&bodyStyle.MozPerspective===undefined){_.animType=false;}}if(bodyStyle.webkitTransform!==undefined){_.animType="webkitTransform";_.transformType="-webkit-transform";_.transitionType="webkitTransition";if(bodyStyle.perspectiveProperty===undefined&&bodyStyle.webkitPerspective===undefined){_.animType=false;}}if(bodyStyle.msTransform!==undefined){_.animType="msTransform";_.transformType="-ms-transform";_.transitionType="msTransition";if(bodyStyle.msTransform===undefined){_.animType=false;}}if(bodyStyle.transform!==undefined&&_.animType!==false){_.animType="transform";_.transformType="transform";_.transitionType="transition";}_.transformsEnabled=(_.animType!==null&&_.animType!==false);};Slick.prototype.setSlideClasses=function(index){var _=this,centerOffset,allSlides,indexOffset,remainder;_.$slider.find(".slick-slide").removeClass("slick-active").removeClass("slick-center");allSlides=_.$slider.find(".slick-slide");if(_.options.centerMode===true){centerOffset=Math.floor(_.options.slidesToShow/2);if(_.options.infinite===true){if(index>=centerOffset&&index<=(_.slideCount-1)-centerOffset){_.$slides.slice(index-centerOffset,index+centerOffset+1).addClass("slick-active");}else{indexOffset=_.options.slidesToShow+index;allSlides.slice(indexOffset-centerOffset+1,indexOffset+centerOffset+2).addClass("slick-active");}if(index===0){allSlides.eq(allSlides.length-1-_.options.slidesToShow).addClass("slick-center");}else{if(index===_.slideCount-1){allSlides.eq(_.options.slidesToShow).addClass("slick-center");}}}_.$slides.eq(index).addClass("slick-center");}else{if(index>=0&&index<=(_.slideCount-_.options.slidesToShow)){_.$slides.slice(index,index+_.options.slidesToShow).addClass("slick-active");}else{if(allSlides.length<=_.options.slidesToShow){allSlides.addClass("slick-active");}else{remainder=_.slideCount%_.options.slidesToShow;indexOffset=_.options.infinite===true?_.options.slidesToShow+index:index;if(_.options.slidesToShow==_.options.slidesToScroll&&(_.slideCount-index)<_.options.slidesToShow){allSlides.slice(indexOffset-(_.options.slidesToShow-remainder),indexOffset+remainder).addClass("slick-active");}else{allSlides.slice(indexOffset,indexOffset+_.options.slidesToShow).addClass("slick-active");}}}}if(_.options.lazyLoad==="ondemand"){_.lazyLoad();}};Slick.prototype.setupInfinite=function(){var _=this,i,slideIndex,infiniteCount;if(_.options.fade===true){_.options.centerMode=false;}if(_.options.infinite===true&&_.options.fade===false){slideIndex=null;if(_.slideCount>_.options.slidesToShow){if(_.options.centerMode===true){infiniteCount=_.options.slidesToShow+1;}else{infiniteCount=_.options.slidesToShow;}for(i=_.slideCount;i>(_.slideCount-infiniteCount);i-=1){slideIndex=i-1;$(_.$slides[slideIndex]).clone(true).attr("id","").attr("index",slideIndex-_.slideCount).prependTo(_.$slideTrack).addClass("slick-cloned");}for(i=0;i<infiniteCount;i+=1){slideIndex=i;$(_.$slides[slideIndex]).clone(true).attr("id","").attr("index",slideIndex+_.slideCount).appendTo(_.$slideTrack).addClass("slick-cloned");}_.$slideTrack.find(".slick-cloned").find("[id]").each(function(){$(this).attr("id","");});}}};Slick.prototype.selectHandler=function(event){var _=this;var index=parseInt($(event.target).parents(".slick-slide").attr("index"));if(!index){index=0;}if(_.slideCount<=_.options.slidesToShow){_.$slider.find(".slick-slide").removeClass("slick-active");_.$slides.eq(index).addClass("slick-active");if(_.options.centerMode===true){_.$slider.find(".slick-slide").removeClass("slick-center");_.$slides.eq(index).addClass("slick-center");}_.asNavFor(index);return;}_.slideHandler(index);};Slick.prototype.slideHandler=function(index,sync,dontAnimate){var targetSlide,animSlide,oldSlide,slideLeft,unevenOffset,targetLeft=null,_=this;sync=sync||false;if(_.animating===true&&_.options.waitForAnimate===true){return;}if(_.options.fade===true&&_.currentSlide===index){return;}if(_.slideCount<=_.options.slidesToShow){return;}if(sync===false){_.asNavFor(index);}targetSlide=index;targetLeft=_.getLeft(targetSlide);slideLeft=_.getLeft(_.currentSlide);_.currentLeft=_.swipeLeft===null?slideLeft:_.swipeLeft;if(_.options.infinite===false&&_.options.centerMode===false&&(index<0||index>_.getDotCount()*_.options.slidesToScroll)){if(_.options.fade===false){targetSlide=_.currentSlide;if(dontAnimate!==true){_.animateSlide(slideLeft,function(){_.postSlide(targetSlide);});}else{_.postSlide(targetSlide);}}return;}else{if(_.options.infinite===false&&_.options.centerMode===true&&(index<0||index>(_.slideCount-_.options.slidesToScroll))){if(_.options.fade===false){targetSlide=_.currentSlide;if(dontAnimate!==true){_.animateSlide(slideLeft,function(){_.postSlide(targetSlide);});}else{_.postSlide(targetSlide);}}return;}}if(_.options.autoplay===true){clearInterval(_.autoPlayTimer);}if(targetSlide<0){if(_.slideCount%_.options.slidesToScroll!==0){animSlide=_.slideCount-(_.slideCount%_.options.slidesToScroll);}else{animSlide=_.slideCount+targetSlide;}}else{if(targetSlide>=_.slideCount){if(_.slideCount%_.options.slidesToScroll!==0){animSlide=0;}else{animSlide=targetSlide-_.slideCount;}}else{animSlide=targetSlide;}}_.animating=true;if(_.options.onBeforeChange!==null&&index!==_.currentSlide){_.options.onBeforeChange.call(this,_,_.currentSlide,animSlide);}oldSlide=_.currentSlide;_.currentSlide=animSlide;_.setSlideClasses(_.currentSlide);_.updateDots();_.updateArrows();if(_.options.fade===true){if(dontAnimate!==true){_.fadeSlide(oldSlide,animSlide,function(){_.postSlide(animSlide);});}else{_.postSlide(animSlide);}return;}if(dontAnimate!==true){_.animateSlide(targetLeft,function(){_.postSlide(animSlide);});}else{_.postSlide(animSlide);}};Slick.prototype.startLoad=function(){var _=this;if(_.options.arrows===true&&_.slideCount>_.options.slidesToShow){_.$prevArrow.hide();_.$nextArrow.hide();}if(_.options.dots===true&&_.slideCount>_.options.slidesToShow){_.$dots.hide();}_.$slider.addClass("slick-loading");};Slick.prototype.swipeDirection=function(){var xDist,yDist,r,swipeAngle,_=this;xDist=_.touchObject.startX-_.touchObject.curX;yDist=_.touchObject.startY-_.touchObject.curY;r=Math.atan2(yDist,xDist);swipeAngle=Math.round(r*180/Math.PI);if(swipeAngle<0){swipeAngle=360-Math.abs(swipeAngle);}if((swipeAngle<=45)&&(swipeAngle>=0)){return(_.options.rtl===false?"left":"right");}if((swipeAngle<=360)&&(swipeAngle>=315)){return(_.options.rtl===false?"left":"right");}if((swipeAngle>=135)&&(swipeAngle<=225)){return(_.options.rtl===false?"right":"left");}return"vertical";};Slick.prototype.swipeEnd=function(event){var _=this,slideCount;_.dragging=false;_.shouldClick=(_.touchObject.swipeLength>10)?false:true;if(_.touchObject.curX===undefined){return false;}if(_.touchObject.swipeLength>=_.touchObject.minSwipe){switch(_.swipeDirection()){case"left":_.slideHandler(_.currentSlide+_.getSlideCount());_.currentDirection=0;_.touchObject={};break;case"right":_.slideHandler(_.currentSlide-_.getSlideCount());_.currentDirection=1;_.touchObject={};break;}}else{if(_.touchObject.startX!==_.touchObject.curX){_.slideHandler(_.currentSlide);_.touchObject={};}}};Slick.prototype.swipeHandler=function(event){var _=this;if((_.options.swipe===false)||("ontouchend" in document&&_.options.swipe===false)){return;}else{if(_.options.draggable===false&&event.type.indexOf("mouse")!==-1){return;}}_.touchObject.fingerCount=event.originalEvent&&event.originalEvent.touches!==undefined?event.originalEvent.touches.length:1;_.touchObject.minSwipe=_.listWidth/_.options.touchThreshold;switch(event.data.action){case"start":_.swipeStart(event);break;case"move":_.swipeMove(event);break;case"end":_.swipeEnd(event);break;}};Slick.prototype.swipeMove=function(event){var _=this,curLeft,swipeDirection,positionOffset,touches;touches=event.originalEvent!==undefined?event.originalEvent.touches:null;if(!_.dragging||touches&&touches.length!==1){return false;}curLeft=_.getLeft(_.currentSlide);_.touchObject.curX=touches!==undefined?touches[0].pageX:event.clientX;_.touchObject.curY=touches!==undefined?touches[0].pageY:event.clientY;_.touchObject.swipeLength=Math.round(Math.sqrt(Math.pow(_.touchObject.curX-_.touchObject.startX,2)));swipeDirection=_.swipeDirection();if(swipeDirection==="vertical"){return;}if(event.originalEvent!==undefined&&_.touchObject.swipeLength>4){event.preventDefault();}positionOffset=(_.options.rtl===false?1:-1)*(_.touchObject.curX>_.touchObject.startX?1:-1);if(_.options.vertical===false){_.swipeLeft=curLeft+_.touchObject.swipeLength*positionOffset;}else{_.swipeLeft=curLeft+(_.touchObject.swipeLength*(_.$list.height()/_.listWidth))*positionOffset;}if(_.options.fade===true||_.options.touchMove===false){return false;}if(_.animating===true){_.swipeLeft=null;return false;}_.setCSS(_.swipeLeft);};Slick.prototype.swipeStart=function(event){var _=this,touches;if(_.touchObject.fingerCount!==1||_.slideCount<=_.options.slidesToShow){_.touchObject={};return false;}if(event.originalEvent!==undefined&&event.originalEvent.touches!==undefined){touches=event.originalEvent.touches[0];}_.touchObject.startX=_.touchObject.curX=touches!==undefined?touches.pageX:event.clientX;_.touchObject.startY=_.touchObject.curY=touches!==undefined?touches.pageY:event.clientY;_.dragging=true;};Slick.prototype.unfilterSlides=function(){var _=this;if(_.$slidesCache!==null){_.unload();_.$slideTrack.children(this.options.slide).detach();_.$slidesCache.appendTo(_.$slideTrack);_.reinit();}};Slick.prototype.unload=function(){var _=this;$(".slick-cloned",_.$slider).remove();if(_.$dots){_.$dots.remove();}if(_.$prevArrow&&(typeof _.options.prevArrow!=="object")){_.$prevArrow.remove();}if(_.$nextArrow&&(typeof _.options.nextArrow!=="object")){_.$nextArrow.remove();}_.$slides.removeClass("slick-slide slick-active slick-visible").css("width","");};Slick.prototype.updateArrows=function(){var _=this,centerOffset;centerOffset=Math.floor(_.options.slidesToShow/2);if(_.options.arrows===true&&_.options.infinite!==true&&_.slideCount>_.options.slidesToShow){_.$prevArrow.removeClass("slick-disabled");_.$nextArrow.removeClass("slick-disabled");if(_.currentSlide===0){_.$prevArrow.addClass("slick-disabled");_.$nextArrow.removeClass("slick-disabled");}else{if(_.currentSlide>=_.slideCount-_.options.slidesToShow&&_.options.centerMode===false){_.$nextArrow.addClass("slick-disabled");_.$prevArrow.removeClass("slick-disabled");}else{if(_.currentSlide>_.slideCount-_.options.slidesToShow+centerOffset&&_.options.centerMode===true){_.$nextArrow.addClass("slick-disabled");_.$prevArrow.removeClass("slick-disabled");}}}}};Slick.prototype.updateDots=function(){var _=this;if(_.$dots!==null){_.$dots.find("li").removeClass("slick-active");_.$dots.find("li").eq(Math.floor(_.currentSlide/_.options.slidesToScroll)).addClass("slick-active");}};$.fn.slick=function(options){var _=this;return _.each(function(index,element){element.slick=new Slick(element,options);});};$.fn.slickAdd=function(slide,slideIndex,addBefore){var _=this;return _.each(function(index,element){element.slick.addSlide(slide,slideIndex,addBefore);});};$.fn.slickCurrentSlide=function(){var _=this;return _.get(0).slick.getCurrent();};$.fn.slickFilter=function(filter){var _=this;return _.each(function(index,element){element.slick.filterSlides(filter);});};$.fn.slickGoTo=function(slide,dontAnimate){var _=this;return _.each(function(index,element){element.slick.changeSlide({data:{message:"index",index:parseInt(slide)}},dontAnimate);});};$.fn.slickNext=function(){var _=this;return _.each(function(index,element){element.slick.changeSlide({data:{message:"next"}});});};$.fn.slickPause=function(){var _=this;return _.each(function(index,element){element.slick.autoPlayClear();element.slick.paused=true;});};$.fn.slickPlay=function(){var _=this;return _.each(function(index,element){element.slick.paused=false;element.slick.autoPlay();});};$.fn.slickPrev=function(){var _=this;return _.each(function(index,element){element.slick.changeSlide({data:{message:"previous"}});});};$.fn.slickRemove=function(slideIndex,removeBefore){var _=this;return _.each(function(index,element){element.slick.removeSlide(slideIndex,removeBefore);});};$.fn.slickRemoveAll=function(){var _=this;return _.each(function(index,element){element.slick.removeSlide(null,null,true);});};$.fn.slickGetOption=function(option){var _=this;return _.get(0).slick.options[option];};$.fn.slickSetOption=function(option,value,refresh){var _=this;return _.each(function(index,element){element.slick.options[option]=value;if(refresh===true){element.slick.unload();element.slick.reinit();}});};$.fn.slickUnfilter=function(){var _=this;return _.each(function(index,element){element.slick.unfilterSlides();});};$.fn.unslick=function(){var _=this;return _.each(function(index,element){if(element.slick){element.slick.destroy();}});};$.fn.getSlick=function(){var s=null;var _=this;_.each(function(index,element){s=element.slick;});return s;};}));
   /* Ende slick */
   /* Start jqueryUI */
   /*! jQuery UI - v1.12.1 - 2017-04-24
* http://jqueryui.com
* Includes: widget.js, position.js, keycode.js, unique-id.js, widgets/tooltip.js
* Copyright jQuery Foundation and other contributors; Licensed MIT */
(function( factory ) {
 if ( typeof define === "function" && define.amd ) {
  // AMD. Register as an anonymous module.
  define([ "jquery" ], factory );
 } else {
  // Browser globals
  factory( jQuery );
 }
}(function( $ ) {
$.ui = $.ui || {};
var version = $.ui.version = "1.12.1";
/*!
 * jQuery UI Widget 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */
//>>label: Widget
//>>group: Core
//>>description: Provides a factory for creating stateful widgets with a common API.
//>>docs: http://api.jqueryui.com/jQuery.widget/
//>>demos: http://jqueryui.com/widget/
var widgetUuid = 0;
var widgetSlice = Array.prototype.slice;
$.cleanData = ( function( orig ) {
 return function( elems ) {
  var events, elem, i;
  for ( i = 0; ( elem = elems[ i ] ) != null; i++ ) {
   try {
    // Only trigger remove when necessary to save time
    events = $._data( elem, "events" );
    if ( events && events.remove ) {
     $( elem ).triggerHandler( "remove" );
    }
   // Http://bugs.jquery.com/ticket/8235
   } catch ( e ) {}
  }
  orig( elems );
 };
} )( $.cleanData );
$.widget = function( name, base, prototype ) {
 var existingConstructor, constructor, basePrototype;
 // ProxiedPrototype allows the provided prototype to remain unmodified
 // so that it can be used as a mixin for multiple widgets (#8876)
 var proxiedPrototype = {};
 var namespace = name.split( "." )[ 0 ];
 name = name.split( "." )[ 1 ];
 var fullName = namespace + "-" + name;
 if ( !prototype ) {
  prototype = base;
  base = $.Widget;
 }
 if ( $.isArray( prototype ) ) {
  prototype = $.extend.apply( null, [ {} ].concat( prototype ) );
 }
 // Create selector for plugin
 $.expr[ ":" ][ fullName.toLowerCase() ] = function( elem ) {
  return !!$.data( elem, fullName );
 };
 $[ namespace ] = $[ namespace ] || {};
 existingConstructor = $[ namespace ][ name ];
 constructor = $[ namespace ][ name ] = function( options, element ) {
  // Allow instantiation without "new" keyword
  if ( !this._createWidget ) {
   return new constructor( options, element );
  }
  // Allow instantiation without initializing for simple inheritance
  // must use "new" keyword (the code above always passes args)
  if ( arguments.length ) {
   this._createWidget( options, element );
  }
 };
 // Extend with the existing constructor to carry over any static properties
 $.extend( constructor, existingConstructor, {
  version: prototype.version,
  // Copy the object used to create the prototype in case we need to
  // redefine the widget later
  _proto: $.extend( {}, prototype ),
  // Track widgets that inherit from this widget in case this widget is
  // redefined after a widget inherits from it
  _childConstructors: []
 } );
 basePrototype = new base();
 // We need to make the options hash a property directly on the new instance
 // otherwise we'll modify the options hash on the prototype that we're
 // inheriting from
 basePrototype.options = $.widget.extend( {}, basePrototype.options );
 $.each( prototype, function( prop, value ) {
  if ( !$.isFunction( value ) ) {
   proxiedPrototype[ prop ] = value;
   return;
  }
  proxiedPrototype[ prop ] = ( function() {
   function _super() {
    return base.prototype[ prop ].apply( this, arguments );
   }
   function _superApply( args ) {
    return base.prototype[ prop ].apply( this, args );
   }
   return function() {
    var __super = this._super;
    var __superApply = this._superApply;
    var returnValue;
    this._super = _super;
    this._superApply = _superApply;
    returnValue = value.apply( this, arguments );
    this._super = __super;
    this._superApply = __superApply;
    return returnValue;
   };
  } )();
 } );
 constructor.prototype = $.widget.extend( basePrototype, {
  // TODO: remove support for widgetEventPrefix
  // always use the name + a colon as the prefix, e.g., draggable:start
  // don't prefix for widgets that aren't DOM-based
  widgetEventPrefix: existingConstructor ? ( basePrototype.widgetEventPrefix || name ) : name
 }, proxiedPrototype, {
  constructor: constructor,
  namespace: namespace,
  widgetName: name,
  widgetFullName: fullName
 } );
 // If this widget is being redefined then we need to find all widgets that
 // are inheriting from it and redefine all of them so that they inherit from
 // the new version of this widget. We're essentially trying to replace one
 // level in the prototype chain.
 if ( existingConstructor ) {
  $.each( existingConstructor._childConstructors, function( i, child ) {
   var childPrototype = child.prototype;
   // Redefine the child widget using the same prototype that was
   // originally used, but inherit from the new version of the base
   $.widget( childPrototype.namespace + "." + childPrototype.widgetName, constructor,
    child._proto );
  } );
  // Remove the list of existing child constructors from the old constructor
  // so the old child constructors can be garbage collected
  delete existingConstructor._childConstructors;
 } else {
  base._childConstructors.push( constructor );
 }
 $.widget.bridge( name, constructor );
 return constructor;
};
$.widget.extend = function( target ) {
 var input = widgetSlice.call( arguments, 1 );
 var inputIndex = 0;
 var inputLength = input.length;
 var key;
 var value;
 for ( ; inputIndex < inputLength; inputIndex++ ) {
  for ( key in input[ inputIndex ] ) {
   value = input[ inputIndex ][ key ];
   if ( input[ inputIndex ].hasOwnProperty( key ) && value !== undefined ) {
    // Clone objects
    if ( $.isPlainObject( value ) ) {
     target[ key ] = $.isPlainObject( target[ key ] ) ?
      $.widget.extend( {}, target[ key ], value ) :
      // Don't extend strings, arrays, etc. with objects
      $.widget.extend( {}, value );
    // Copy everything else by reference
    } else {
     target[ key ] = value;
    }
   }
  }
 }
 return target;
};
$.widget.bridge = function( name, object ) {
 var fullName = object.prototype.widgetFullName || name;
 $.fn[ name ] = function( options ) {
  var isMethodCall = typeof options === "string";
  var args = widgetSlice.call( arguments, 1 );
  var returnValue = this;
  if ( isMethodCall ) {
   // If this is an empty collection, we need to have the instance method
   // return undefined instead of the jQuery instance
   if ( !this.length && options === "instance" ) {
    returnValue = undefined;
   } else {
    this.each( function() {
     var methodValue;
     var instance = $.data( this, fullName );
     if ( options === "instance" ) {
      returnValue = instance;
      return false;
     }
     if ( !instance ) {
      return $.error( "cannot call methods on " + name +
       " prior to initialization; " +
       "attempted to call method '" + options + "'" );
     }
     if ( !$.isFunction( instance[ options ] ) || options.charAt( 0 ) === "_" ) {
      return $.error( "no such method '" + options + "' for " + name +
       " widget instance" );
     }
     methodValue = instance[ options ].apply( instance, args );
     if ( methodValue !== instance && methodValue !== undefined ) {
      returnValue = methodValue && methodValue.jquery ?
       returnValue.pushStack( methodValue.get() ) :
       methodValue;
      return false;
     }
    } );
   }
  } else {
   // Allow multiple hashes to be passed on init
   if ( args.length ) {
    options = $.widget.extend.apply( null, [ options ].concat( args ) );
   }
   this.each( function() {
    var instance = $.data( this, fullName );
    if ( instance ) {
     instance.option( options || {} );
     if ( instance._init ) {
      instance._init();
     }
    } else {
     $.data( this, fullName, new object( options, this ) );
    }
   } );
  }
  return returnValue;
 };
};
$.Widget = function( /* options, element */ ) {};
$.Widget._childConstructors = [];
$.Widget.prototype = {
 widgetName: "widget",
 widgetEventPrefix: "",
 defaultElement: "<div>",
 options: {
  classes: {},
  disabled: false,
  // Callbacks
  create: null
 },
 _createWidget: function( options, element ) {
  element = $( element || this.defaultElement || this )[ 0 ];
  this.element = $( element );
  this.uuid = widgetUuid++;
  this.eventNamespace = "." + this.widgetName + this.uuid;
  this.bindings = $();
  this.hoverable = $();
  this.focusable = $();
  this.classesElementLookup = {};
  if ( element !== this ) {
   $.data( element, this.widgetFullName, this );
   this._on( true, this.element, {
    remove: function( event ) {
     if ( event.target === element ) {
      this.destroy();
     }
    }
   } );
   this.document = $( element.style ?
    // Element within the document
    element.ownerDocument :
    // Element is window or document
    element.document || element );
   this.window = $( this.document[ 0 ].defaultView || this.document[ 0 ].parentWindow );
  }
  this.options = $.widget.extend( {},
   this.options,
   this._getCreateOptions(),
   options );
  this._create();
  if ( this.options.disabled ) {
   this._setOptionDisabled( this.options.disabled );
  }
  this._trigger( "create", null, this._getCreateEventData() );
  this._init();
 },
 _getCreateOptions: function() {
  return {};
 },
 _getCreateEventData: $.noop,
 _create: $.noop,
 _init: $.noop,
 destroy: function() {
  var that = this;
  this._destroy();
  $.each( this.classesElementLookup, function( key, value ) {
   that._removeClass( value, key );
  } );
  // We can probably remove the unbind calls in 2.0
  // all event bindings should go through this._on()
  this.element
   .off( this.eventNamespace )
   .removeData( this.widgetFullName );
  this.widget()
   .off( this.eventNamespace )
   .removeAttr( "aria-disabled" );
  // Clean up events and states
  this.bindings.off( this.eventNamespace );
 },
 _destroy: $.noop,
 widget: function() {
  return this.element;
 },
 option: function( key, value ) {
  var options = key;
  var parts;
  var curOption;
  var i;
  if ( arguments.length === 0 ) {
   // Don't return a reference to the internal hash
   return $.widget.extend( {}, this.options );
  }
  if ( typeof key === "string" ) {
   // Handle nested keys, e.g., "foo.bar" => { foo: { bar: ___ } }
   options = {};
   parts = key.split( "." );
   key = parts.shift();
   if ( parts.length ) {
    curOption = options[ key ] = $.widget.extend( {}, this.options[ key ] );
    for ( i = 0; i < parts.length - 1; i++ ) {
     curOption[ parts[ i ] ] = curOption[ parts[ i ] ] || {};
     curOption = curOption[ parts[ i ] ];
    }
    key = parts.pop();
    if ( arguments.length === 1 ) {
     return curOption[ key ] === undefined ? null : curOption[ key ];
    }
    curOption[ key ] = value;
   } else {
    if ( arguments.length === 1 ) {
     return this.options[ key ] === undefined ? null : this.options[ key ];
    }
    options[ key ] = value;
   }
  }
  this._setOptions( options );
  return this;
 },
 _setOptions: function( options ) {
  var key;
  for ( key in options ) {
   this._setOption( key, options[ key ] );
  }
  return this;
 },
 _setOption: function( key, value ) {
  if ( key === "classes" ) {
   this._setOptionClasses( value );
  }
  this.options[ key ] = value;
  if ( key === "disabled" ) {
   this._setOptionDisabled( value );
  }
  return this;
 },
 _setOptionClasses: function( value ) {
  var classKey, elements, currentElements;
  for ( classKey in value ) {
   currentElements = this.classesElementLookup[ classKey ];
   if ( value[ classKey ] === this.options.classes[ classKey ] ||
     !currentElements ||
     !currentElements.length ) {
    continue;
   }
   // We are doing this to create a new jQuery object because the _removeClass() call
   // on the next line is going to destroy the reference to the current elements being
   // tracked. We need to save a copy of this collection so that we can add the new classes
   // below.
   elements = $( currentElements.get() );
   this._removeClass( currentElements, classKey );
   // We don't use _addClass() here, because that uses this.options.classes
   // for generating the string of classes. We want to use the value passed in from
   // _setOption(), this is the new value of the classes option which was passed to
   // _setOption(). We pass this value directly to _classes().
   elements.addClass( this._classes( {
    element: elements,
    keys: classKey,
    classes: value,
    add: true
   } ) );
  }
 },
 _setOptionDisabled: function( value ) {
  this._toggleClass( this.widget(), this.widgetFullName + "-disabled", null, !!value );
  // If the widget is becoming disabled, then nothing is interactive
  if ( value ) {
   this._removeClass( this.hoverable, null, "ui-state-hover" );
   this._removeClass( this.focusable, null, "ui-state-focus" );
  }
 },
 enable: function() {
  return this._setOptions( { disabled: false } );
 },
 disable: function() {
  return this._setOptions( { disabled: true } );
 },
 _classes: function( options ) {
  var full = [];
  var that = this;
  options = $.extend( {
   element: this.element,
   classes: this.options.classes || {}
  }, options );
  function processClassString( classes, checkOption ) {
   var current, i;
   for ( i = 0; i < classes.length; i++ ) {
    current = that.classesElementLookup[ classes[ i ] ] || $();
    if ( options.add ) {
     current = $( $.unique( current.get().concat( options.element.get() ) ) );
    } else {
     current = $( current.not( options.element ).get() );
    }
    that.classesElementLookup[ classes[ i ] ] = current;
    full.push( classes[ i ] );
    if ( checkOption && options.classes[ classes[ i ] ] ) {
     full.push( options.classes[ classes[ i ] ] );
    }
   }
  }
  this._on( options.element, {
   "remove": "_untrackClassesElement"
  } );
  if ( options.keys ) {
   processClassString( options.keys.match( /\S+/g ) || [], true );
  }
  if ( options.extra ) {
   processClassString( options.extra.match( /\S+/g ) || [] );
  }
  return full.join( " " );
 },
 _untrackClassesElement: function( event ) {
  var that = this;
  $.each( that.classesElementLookup, function( key, value ) {
   if ( $.inArray( event.target, value ) !== -1 ) {
    that.classesElementLookup[ key ] = $( value.not( event.target ).get() );
   }
  } );
 },
 _removeClass: function( element, keys, extra ) {
  return this._toggleClass( element, keys, extra, false );
 },
 _addClass: function( element, keys, extra ) {
  return this._toggleClass( element, keys, extra, true );
 },
 _toggleClass: function( element, keys, extra, add ) {
  add = ( typeof add === "boolean" ) ? add : extra;
  var shift = ( typeof element === "string" || element === null ),
   options = {
    extra: shift ? keys : extra,
    keys: shift ? element : keys,
    element: shift ? this.element : element,
    add: add
   };
  options.element.toggleClass( this._classes( options ), add );
  return this;
 },
 _on: function( suppressDisabledCheck, element, handlers ) {
  var delegateElement;
  var instance = this;
  // No suppressDisabledCheck flag, shuffle arguments
  if ( typeof suppressDisabledCheck !== "boolean" ) {
   handlers = element;
   element = suppressDisabledCheck;
   suppressDisabledCheck = false;
  }
  // No element argument, shuffle and use this.element
  if ( !handlers ) {
   handlers = element;
   element = this.element;
   delegateElement = this.widget();
  } else {
   element = delegateElement = $( element );
   this.bindings = this.bindings.add( element );
  }
  $.each( handlers, function( event, handler ) {
   function handlerProxy() {
    // Allow widgets to customize the disabled handling
    // - disabled as an array instead of boolean
    // - disabled class as method for disabling individual parts
    if ( !suppressDisabledCheck &&
      ( instance.options.disabled === true ||
      $( this ).hasClass( "ui-state-disabled" ) ) ) {
     return;
    }
    return ( typeof handler === "string" ? instance[ handler ] : handler )
     .apply( instance, arguments );
   }
   // Copy the guid so direct unbinding works
   if ( typeof handler !== "string" ) {
    handlerProxy.guid = handler.guid =
     handler.guid || handlerProxy.guid || $.guid++;
   }
   var match = event.match( /^([\w:-]*)\s*(.*)$/ );
   var eventName = match[ 1 ] + instance.eventNamespace;
   var selector = match[ 2 ];
   if ( selector ) {
    delegateElement.on( eventName, selector, handlerProxy );
   } else {
    element.on( eventName, handlerProxy );
   }
  } );
 },
 _off: function( element, eventName ) {
  eventName = ( eventName || "" ).split( " " ).join( this.eventNamespace + " " ) +
   this.eventNamespace;
  element.off( eventName ).off( eventName );
  // Clear the stack to avoid memory leaks (#10056)
  this.bindings = $( this.bindings.not( element ).get() );
  this.focusable = $( this.focusable.not( element ).get() );
  this.hoverable = $( this.hoverable.not( element ).get() );
 },
 _delay: function( handler, delay ) {
  function handlerProxy() {
   return ( typeof handler === "string" ? instance[ handler ] : handler )
    .apply( instance, arguments );
  }
  var instance = this;
  return setTimeout( handlerProxy, delay || 0 );
 },
 _hoverable: function( element ) {
  this.hoverable = this.hoverable.add( element );
  this._on( element, {
   mouseenter: function( event ) {
    this._addClass( $( event.currentTarget ), null, "ui-state-hover" );
   },
   mouseleave: function( event ) {
    this._removeClass( $( event.currentTarget ), null, "ui-state-hover" );
   }
  } );
 },
 _focusable: function( element ) {
  this.focusable = this.focusable.add( element );
  this._on( element, {
   focusin: function( event ) {
    this._addClass( $( event.currentTarget ), null, "ui-state-focus" );
   },
   focusout: function( event ) {
    this._removeClass( $( event.currentTarget ), null, "ui-state-focus" );
   }
  } );
 },
 _trigger: function( type, event, data ) {
  var prop, orig;
  var callback = this.options[ type ];
  data = data || {};
  event = $.Event( event );
  event.type = ( type === this.widgetEventPrefix ?
   type :
   this.widgetEventPrefix + type ).toLowerCase();
  // The original event may come from any element
  // so we need to reset the target on the new event
  event.target = this.element[ 0 ];
  // Copy original event properties over to the new event
  orig = event.originalEvent;
  if ( orig ) {
   for ( prop in orig ) {
    if ( !( prop in event ) ) {
     event[ prop ] = orig[ prop ];
    }
   }
  }
  this.element.trigger( event, data );
  return !( $.isFunction( callback ) &&
   callback.apply( this.element[ 0 ], [ event ].concat( data ) ) === false ||
   event.isDefaultPrevented() );
 }
};
$.each( { show: "fadeIn", hide: "fadeOut" }, function( method, defaultEffect ) {
 $.Widget.prototype[ "_" + method ] = function( element, options, callback ) {
  if ( typeof options === "string" ) {
   options = { effect: options };
  }
  var hasOptions;
  var effectName = !options ?
   method :
   options === true || typeof options === "number" ?
    defaultEffect :
    options.effect || defaultEffect;
  options = options || {};
  if ( typeof options === "number" ) {
   options = { duration: options };
  }
  hasOptions = !$.isEmptyObject( options );
  options.complete = callback;
  if ( options.delay ) {
   element.delay( options.delay );
  }
  if ( hasOptions && $.effects && $.effects.effect[ effectName ] ) {
   element[ method ]( options );
  } else if ( effectName !== method && element[ effectName ] ) {
   element[ effectName ]( options.duration, options.easing, callback );
  } else {
   element.queue( function( next ) {
    $( this )[ method ]();
    if ( callback ) {
     callback.call( element[ 0 ] );
    }
    next();
   } );
  }
 };
} );
var widget = $.widget;
/*!
 * jQuery UI Position 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/position/
 */
//>>label: Position
//>>group: Core
//>>description: Positions elements relative to other elements.
//>>docs: http://api.jqueryui.com/position/
//>>demos: http://jqueryui.com/position/
( function() {
var cachedScrollbarWidth,
 max = Math.max,
 abs = Math.abs,
 rhorizontal = /left|center|right/,
 rvertical = /top|center|bottom/,
 roffset = /[\+\-]\d+(\.[\d]+)?%?/,
 rposition = /^\w+/,
 rpercent = /%$/,
 _position = $.fn.position;
function getOffsets( offsets, width, height ) {
 return [
  parseFloat( offsets[ 0 ] ) * ( rpercent.test( offsets[ 0 ] ) ? width / 100 : 1 ),
  parseFloat( offsets[ 1 ] ) * ( rpercent.test( offsets[ 1 ] ) ? height / 100 : 1 )
 ];
}
function parseCss( element, property ) {
 return parseInt( $.css( element, property ), 10 ) || 0;
}
function getDimensions( elem ) {
 var raw = elem[ 0 ];
 if ( raw.nodeType === 9 ) {
  return {
   width: elem.width(),
   height: elem.height(),
   offset: { top: 0, left: 0 }
  };
 }
 if ( $.isWindow( raw ) ) {
  return {
   width: elem.width(),
   height: elem.height(),
   offset: { top: elem.scrollTop(), left: elem.scrollLeft() }
  };
 }
 if ( raw.preventDefault ) {
  return {
   width: 0,
   height: 0,
   offset: { top: raw.pageY, left: raw.pageX }
  };
 }
 return {
  width: elem.outerWidth(),
  height: elem.outerHeight(),
  offset: elem.offset()
 };
}
$.position = {
 scrollbarWidth: function() {
  if ( cachedScrollbarWidth !== undefined ) {
   return cachedScrollbarWidth;
  }
  var w1, w2,
   div = $( "<div " +
    "style='display:block;position:absolute;width:50px;height:50px;overflow:hidden;'>" +
    "<div style='height:100px;width:auto;'></div></div>" ),
   innerDiv = div.children()[ 0 ];
  $( "body" ).append( div );
  w1 = innerDiv.offsetWidth;
  div.css( "overflow", "scroll" );
  w2 = innerDiv.offsetWidth;
  if ( w1 === w2 ) {
   w2 = div[ 0 ].clientWidth;
  }
  div.remove();
  return ( cachedScrollbarWidth = w1 - w2 );
 },
 getScrollInfo: function( within ) {
  var overflowX = within.isWindow || within.isDocument ? "" :
    within.element.css( "overflow-x" ),
   overflowY = within.isWindow || within.isDocument ? "" :
    within.element.css( "overflow-y" ),
   hasOverflowX = overflowX === "scroll" ||
    ( overflowX === "auto" && within.width < within.element[ 0 ].scrollWidth ),
   hasOverflowY = overflowY === "scroll" ||
    ( overflowY === "auto" && within.height < within.element[ 0 ].scrollHeight );
  return {
   width: hasOverflowY ? $.position.scrollbarWidth() : 0,
   height: hasOverflowX ? $.position.scrollbarWidth() : 0
  };
 },
 getWithinInfo: function( element ) {
  var withinElement = $( element || window ),
   isWindow = $.isWindow( withinElement[ 0 ] ),
   isDocument = !!withinElement[ 0 ] && withinElement[ 0 ].nodeType === 9,
   hasOffset = !isWindow && !isDocument;
  return {
   element: withinElement,
   isWindow: isWindow,
   isDocument: isDocument,
   offset: hasOffset ? $( element ).offset() : { left: 0, top: 0 },
   scrollLeft: withinElement.scrollLeft(),
   scrollTop: withinElement.scrollTop(),
   width: withinElement.outerWidth(),
   height: withinElement.outerHeight()
  };
 }
};
$.fn.position = function( options ) {
 if ( !options || !options.of ) {
  return _position.apply( this, arguments );
 }
 // Make a copy, we don't want to modify arguments
 options = $.extend( {}, options );
 var atOffset, targetWidth, targetHeight, targetOffset, basePosition, dimensions,
  target = $( options.of ),
  within = $.position.getWithinInfo( options.within ),
  scrollInfo = $.position.getScrollInfo( within ),
  collision = ( options.collision || "flip" ).split( " " ),
  offsets = {};
 dimensions = getDimensions( target );
 if ( target[ 0 ].preventDefault ) {
  // Force left top to allow flipping
  options.at = "left top";
 }
 targetWidth = dimensions.width;
 targetHeight = dimensions.height;
 targetOffset = dimensions.offset;
 // Clone to reuse original targetOffset later
 basePosition = $.extend( {}, targetOffset );
 // Force my and at to have valid horizontal and vertical positions
 // if a value is missing or invalid, it will be converted to center
 $.each( [ "my", "at" ], function() {
  var pos = ( options[ this ] || "" ).split( " " ),
   horizontalOffset,
   verticalOffset;
  if ( pos.length === 1 ) {
   pos = rhorizontal.test( pos[ 0 ] ) ?
    pos.concat( [ "center" ] ) :
    rvertical.test( pos[ 0 ] ) ?
     [ "center" ].concat( pos ) :
     [ "center", "center" ];
  }
  pos[ 0 ] = rhorizontal.test( pos[ 0 ] ) ? pos[ 0 ] : "center";
  pos[ 1 ] = rvertical.test( pos[ 1 ] ) ? pos[ 1 ] : "center";
  // Calculate offsets
  horizontalOffset = roffset.exec( pos[ 0 ] );
  verticalOffset = roffset.exec( pos[ 1 ] );
  offsets[ this ] = [
   horizontalOffset ? horizontalOffset[ 0 ] : 0,
   verticalOffset ? verticalOffset[ 0 ] : 0
  ];
  // Reduce to just the positions without the offsets
  options[ this ] = [
   rposition.exec( pos[ 0 ] )[ 0 ],
   rposition.exec( pos[ 1 ] )[ 0 ]
  ];
 } );
 // Normalize collision option
 if ( collision.length === 1 ) {
  collision[ 1 ] = collision[ 0 ];
 }
 if ( options.at[ 0 ] === "right" ) {
  basePosition.left += targetWidth;
 } else if ( options.at[ 0 ] === "center" ) {
  basePosition.left += targetWidth / 2;
 }
 if ( options.at[ 1 ] === "bottom" ) {
  basePosition.top += targetHeight;
 } else if ( options.at[ 1 ] === "center" ) {
  basePosition.top += targetHeight / 2;
 }
 atOffset = getOffsets( offsets.at, targetWidth, targetHeight );
 basePosition.left += atOffset[ 0 ];
 basePosition.top += atOffset[ 1 ];
 return this.each( function() {
  var collisionPosition, using,
   elem = $( this ),
   elemWidth = elem.outerWidth(),
   elemHeight = elem.outerHeight(),
   marginLeft = parseCss( this, "marginLeft" ),
   marginTop = parseCss( this, "marginTop" ),
   collisionWidth = elemWidth + marginLeft + parseCss( this, "marginRight" ) +
    scrollInfo.width,
   collisionHeight = elemHeight + marginTop + parseCss( this, "marginBottom" ) +
    scrollInfo.height,
   position = $.extend( {}, basePosition ),
   myOffset = getOffsets( offsets.my, elem.outerWidth(), elem.outerHeight() );
  if ( options.my[ 0 ] === "right" ) {
   position.left -= elemWidth;
  } else if ( options.my[ 0 ] === "center" ) {
   position.left -= elemWidth / 2;
  }
  if ( options.my[ 1 ] === "bottom" ) {
   position.top -= elemHeight;
  } else if ( options.my[ 1 ] === "center" ) {
   position.top -= elemHeight / 2;
  }
  position.left += myOffset[ 0 ];
  position.top += myOffset[ 1 ];
  collisionPosition = {
   marginLeft: marginLeft,
   marginTop: marginTop
  };
  $.each( [ "left", "top" ], function( i, dir ) {
   if ( $.ui.position[ collision[ i ] ] ) {
    $.ui.position[ collision[ i ] ][ dir ]( position, {
     targetWidth: targetWidth,
     targetHeight: targetHeight,
     elemWidth: elemWidth,
     elemHeight: elemHeight,
     collisionPosition: collisionPosition,
     collisionWidth: collisionWidth,
     collisionHeight: collisionHeight,
     offset: [ atOffset[ 0 ] + myOffset[ 0 ], atOffset [ 1 ] + myOffset[ 1 ] ],
     my: options.my,
     at: options.at,
     within: within,
     elem: elem
    } );
   }
  } );
  if ( options.using ) {
   // Adds feedback as second argument to using callback, if present
   using = function( props ) {
    var left = targetOffset.left - position.left,
     right = left + targetWidth - elemWidth,
     top = targetOffset.top - position.top,
     bottom = top + targetHeight - elemHeight,
     feedback = {
      target: {
       element: target,
       left: targetOffset.left,
       top: targetOffset.top,
       width: targetWidth,
       height: targetHeight
      },
      element: {
       element: elem,
       left: position.left,
       top: position.top,
       width: elemWidth,
       height: elemHeight
      },
      horizontal: right < 0 ? "left" : left > 0 ? "right" : "center",
      vertical: bottom < 0 ? "top" : top > 0 ? "bottom" : "middle"
     };
    if ( targetWidth < elemWidth && abs( left + right ) < targetWidth ) {
     feedback.horizontal = "center";
    }
    if ( targetHeight < elemHeight && abs( top + bottom ) < targetHeight ) {
     feedback.vertical = "middle";
    }
    if ( max( abs( left ), abs( right ) ) > max( abs( top ), abs( bottom ) ) ) {
     feedback.important = "horizontal";
    } else {
     feedback.important = "vertical";
    }
    options.using.call( this, props, feedback );
   };
  }
  elem.offset( $.extend( position, { using: using } ) );
 } );
};
$.ui.position = {
 fit: {
  left: function( position, data ) {
   var within = data.within,
    withinOffset = within.isWindow ? within.scrollLeft : within.offset.left,
    outerWidth = within.width,
    collisionPosLeft = position.left - data.collisionPosition.marginLeft,
    overLeft = withinOffset - collisionPosLeft,
    overRight = collisionPosLeft + data.collisionWidth - outerWidth - withinOffset,
    newOverRight;
   // Element is wider than within
   if ( data.collisionWidth > outerWidth ) {
    // Element is initially over the left side of within
    if ( overLeft > 0 && overRight <= 0 ) {
     newOverRight = position.left + overLeft + data.collisionWidth - outerWidth -
      withinOffset;
     position.left += overLeft - newOverRight;
    // Element is initially over right side of within
    } else if ( overRight > 0 && overLeft <= 0 ) {
     position.left = withinOffset;
    // Element is initially over both left and right sides of within
    } else {
     if ( overLeft > overRight ) {
      position.left = withinOffset + outerWidth - data.collisionWidth;
     } else {
      position.left = withinOffset;
     }
    }
   // Too far left -> align with left edge
   } else if ( overLeft > 0 ) {
    position.left += overLeft;
   // Too far right -> align with right edge
   } else if ( overRight > 0 ) {
    position.left -= overRight;
   // Adjust based on position and margin
   } else {
    position.left = max( position.left - collisionPosLeft, position.left );
   }
  },
  top: function( position, data ) {
   var within = data.within,
    withinOffset = within.isWindow ? within.scrollTop : within.offset.top,
    outerHeight = data.within.height,
    collisionPosTop = position.top - data.collisionPosition.marginTop,
    overTop = withinOffset - collisionPosTop,
    overBottom = collisionPosTop + data.collisionHeight - outerHeight - withinOffset,
    newOverBottom;
   // Element is taller than within
   if ( data.collisionHeight > outerHeight ) {
    // Element is initially over the top of within
    if ( overTop > 0 && overBottom <= 0 ) {
     newOverBottom = position.top + overTop + data.collisionHeight - outerHeight -
      withinOffset;
     position.top += overTop - newOverBottom;
    // Element is initially over bottom of within
    } else if ( overBottom > 0 && overTop <= 0 ) {
     position.top = withinOffset;
    // Element is initially over both top and bottom of within
    } else {
     if ( overTop > overBottom ) {
      position.top = withinOffset + outerHeight - data.collisionHeight;
     } else {
      position.top = withinOffset;
     }
    }
   // Too far up -> align with top
   } else if ( overTop > 0 ) {
    position.top += overTop;
   // Too far down -> align with bottom edge
   } else if ( overBottom > 0 ) {
    position.top -= overBottom;
   // Adjust based on position and margin
   } else {
    position.top = max( position.top - collisionPosTop, position.top );
   }
  }
 },
 flip: {
  left: function( position, data ) {
   var within = data.within,
    withinOffset = within.offset.left + within.scrollLeft,
    outerWidth = within.width,
    offsetLeft = within.isWindow ? within.scrollLeft : within.offset.left,
    collisionPosLeft = position.left - data.collisionPosition.marginLeft,
    overLeft = collisionPosLeft - offsetLeft,
    overRight = collisionPosLeft + data.collisionWidth - outerWidth - offsetLeft,
    myOffset = data.my[ 0 ] === "left" ?
     -data.elemWidth :
     data.my[ 0 ] === "right" ?
      data.elemWidth :
      0,
    atOffset = data.at[ 0 ] === "left" ?
     data.targetWidth :
     data.at[ 0 ] === "right" ?
      -data.targetWidth :
      0,
    offset = -2 * data.offset[ 0 ],
    newOverRight,
    newOverLeft;
   if ( overLeft < 0 ) {
    newOverRight = position.left + myOffset + atOffset + offset + data.collisionWidth -
     outerWidth - withinOffset;
    if ( newOverRight < 0 || newOverRight < abs( overLeft ) ) {
     position.left += myOffset + atOffset + offset;
    }
   } else if ( overRight > 0 ) {
    newOverLeft = position.left - data.collisionPosition.marginLeft + myOffset +
     atOffset + offset - offsetLeft;
    if ( newOverLeft > 0 || abs( newOverLeft ) < overRight ) {
     position.left += myOffset + atOffset + offset;
    }
   }
  },
  top: function( position, data ) {
   var within = data.within,
    withinOffset = within.offset.top + within.scrollTop,
    outerHeight = within.height,
    offsetTop = within.isWindow ? within.scrollTop : within.offset.top,
    collisionPosTop = position.top - data.collisionPosition.marginTop,
    overTop = collisionPosTop - offsetTop,
    overBottom = collisionPosTop + data.collisionHeight - outerHeight - offsetTop,
    top = data.my[ 1 ] === "top",
    myOffset = top ?
     -data.elemHeight :
     data.my[ 1 ] === "bottom" ?
      data.elemHeight :
      0,
    atOffset = data.at[ 1 ] === "top" ?
     data.targetHeight :
     data.at[ 1 ] === "bottom" ?
      -data.targetHeight :
      0,
    offset = -2 * data.offset[ 1 ],
    newOverTop,
    newOverBottom;
   if ( overTop < 0 ) {
    newOverBottom = position.top + myOffset + atOffset + offset + data.collisionHeight -
     outerHeight - withinOffset;
    if ( newOverBottom < 0 || newOverBottom < abs( overTop ) ) {
     position.top += myOffset + atOffset + offset;
    }
   } else if ( overBottom > 0 ) {
    newOverTop = position.top - data.collisionPosition.marginTop + myOffset + atOffset +
     offset - offsetTop;
    if ( newOverTop > 0 || abs( newOverTop ) < overBottom ) {
     position.top += myOffset + atOffset + offset;
    }
   }
  }
 },
 flipfit: {
  left: function() {
   $.ui.position.flip.left.apply( this, arguments );
   $.ui.position.fit.left.apply( this, arguments );
  },
  top: function() {
   $.ui.position.flip.top.apply( this, arguments );
   $.ui.position.fit.top.apply( this, arguments );
  }
 }
};
} )();
var position = $.ui.position;
/*!
 * jQuery UI Keycode 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */
//>>label: Keycode
//>>group: Core
//>>description: Provide keycodes as keynames
//>>docs: http://api.jqueryui.com/jQuery.ui.keyCode/
var keycode = $.ui.keyCode = {
 BACKSPACE: 8,
 COMMA: 188,
 DELETE: 46,
 DOWN: 40,
 END: 35,
 ENTER: 13,
 ESCAPE: 27,
 HOME: 36,
 LEFT: 37,
 PAGE_DOWN: 34,
 PAGE_UP: 33,
 PERIOD: 190,
 RIGHT: 39,
 SPACE: 32,
 TAB: 9,
 UP: 38
};
/*!
 * jQuery UI Unique ID 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */
//>>label: uniqueId
//>>group: Core
//>>description: Functions to generate and remove uniqueId's
//>>docs: http://api.jqueryui.com/uniqueId/
var uniqueId = $.fn.extend( {
 uniqueId: ( function() {
  var uuid = 0;
  return function() {
   return this.each( function() {
    if ( !this.id ) {
     this.id = "ui-id-" + ( ++uuid );
    }
   } );
  };
 } )(),
 removeUniqueId: function() {
  return this.each( function() {
   if ( /^ui-id-\d+$/.test( this.id ) ) {
    $( this ).removeAttr( "id" );
   }
  } );
 }
} );
/*!
 * jQuery UI Tooltip 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 */
//>>label: Tooltip
//>>group: Widgets
//>>description: Shows additional information for any element on hover or focus.
//>>docs: http://api.jqueryui.com/tooltip/
//>>demos: http://jqueryui.com/tooltip/
//>>css.structure: ../../themes/base/core.css
//>>css.structure: ../../themes/base/tooltip.css
//>>css.theme: ../../themes/base/theme.css
$.widget( "ui.tooltip", {
 version: "1.12.1",
 options: {
  classes: {
   "ui-tooltip": "ui-corner-all ui-widget-shadow"
  },
  content: function() {
   // support: IE<9, Opera in jQuery <1.7
   // .text() can't accept undefined, so coerce to a string
   var title = $( this ).attr( "title" ) || "";
   // Escape title, since we're going from an attribute to raw HTML
   return $( "<a>" ).text( title ).html();
  },
  hide: true,
  // Disabled elements have inconsistent behavior across browsers (#8661)
  items: "[title]:not([disabled])",
  position: {
   my: "left top+15",
   at: "left bottom",
   collision: "flipfit flip"
  },
  show: true,
  track: false,
  // Callbacks
  close: null,
  open: null
 },
 _addDescribedBy: function( elem, id ) {
  var describedby = ( elem.attr( "aria-describedby" ) || "" ).split( /\s+/ );
  describedby.push( id );
  elem
   .data( "ui-tooltip-id", id )
   .attr( "aria-describedby", $.trim( describedby.join( " " ) ) );
 },
 _removeDescribedBy: function( elem ) {
  var id = elem.data( "ui-tooltip-id" ),
   describedby = ( elem.attr( "aria-describedby" ) || "" ).split( /\s+/ ),
   index = $.inArray( id, describedby );
  if ( index !== -1 ) {
   describedby.splice( index, 1 );
  }
  elem.removeData( "ui-tooltip-id" );
  describedby = $.trim( describedby.join( " " ) );
  if ( describedby ) {
   elem.attr( "aria-describedby", describedby );
  } else {
   elem.removeAttr( "aria-describedby" );
  }
 },
 _create: function() {
  this._on( {
   mouseover: "open",
   focusin: "open"
  } );
  // IDs of generated tooltips, needed for destroy
  this.tooltips = {};
  // IDs of parent tooltips where we removed the title attribute
  this.parents = {};
  // Append the aria-live region so tooltips announce correctly
  this.liveRegion = $( "<div>" )
   .attr( {
    role: "log",
    "aria-live": "assertive",
    "aria-relevant": "additions"
   } )
   .appendTo( this.document[ 0 ].body );
  this._addClass( this.liveRegion, null, "ui-helper-hidden-accessible" );
  this.disabledTitles = $( [] );
 },
 _setOption: function( key, value ) {
  var that = this;
  this._super( key, value );
  if ( key === "content" ) {
   $.each( this.tooltips, function( id, tooltipData ) {
    that._updateContent( tooltipData.element );
   } );
  }
 },
 _setOptionDisabled: function( value ) {
  this[ value ? "_disable" : "_enable" ]();
 },
 _disable: function() {
  var that = this;
  // Close open tooltips
  $.each( this.tooltips, function( id, tooltipData ) {
   var event = $.Event( "blur" );
   event.target = event.currentTarget = tooltipData.element[ 0 ];
   that.close( event, true );
  } );
  // Remove title attributes to prevent native tooltips
  this.disabledTitles = this.disabledTitles.add(
   this.element.find( this.options.items ).addBack()
    .filter( function() {
     var element = $( this );
     if ( element.is( "[title]" ) ) {
      return element
       .data( "ui-tooltip-title", element.attr( "title" ) )
       .removeAttr( "title" );
     }
    } )
  );
 },
 _enable: function() {
  // restore title attributes
  this.disabledTitles.each( function() {
   var element = $( this );
   if ( element.data( "ui-tooltip-title" ) ) {
    element.attr( "title", element.data( "ui-tooltip-title" ) );
   }
  } );
  this.disabledTitles = $( [] );
 },
 open: function( event ) {
  var that = this,
   target = $( event ? event.target : this.element )
    // we need closest here due to mouseover bubbling,
    // but always pointing at the same event target
    .closest( this.options.items );
  // No element to show a tooltip for or the tooltip is already open
  if ( !target.length || target.data( "ui-tooltip-id" ) ) {
   return;
  }
  if ( target.attr( "title" ) ) {
   target.data( "ui-tooltip-title", target.attr( "title" ) );
  }
  target.data( "ui-tooltip-open", true );
  // Kill parent tooltips, custom or native, for hover
  if ( event && event.type === "mouseover" ) {
   target.parents().each( function() {
    var parent = $( this ),
     blurEvent;
    if ( parent.data( "ui-tooltip-open" ) ) {
     blurEvent = $.Event( "blur" );
     blurEvent.target = blurEvent.currentTarget = this;
     that.close( blurEvent, true );
    }
    if ( parent.attr( "title" ) ) {
     parent.uniqueId();
     that.parents[ this.id ] = {
      element: this,
      title: parent.attr( "title" )
     };
     parent.attr( "title", "" );
    }
   } );
  }
  this._registerCloseHandlers( event, target );
  this._updateContent( target, event );
 },
 _updateContent: function( target, event ) {
  var content,
   contentOption = this.options.content,
   that = this,
   eventType = event ? event.type : null;
  if ( typeof contentOption === "string" || contentOption.nodeType ||
    contentOption.jquery ) {
   return this._open( event, target, contentOption );
  }
  content = contentOption.call( target[ 0 ], function( response ) {
   // IE may instantly serve a cached response for ajax requests
   // delay this call to _open so the other call to _open runs first
   that._delay( function() {
    // Ignore async response if tooltip was closed already
    if ( !target.data( "ui-tooltip-open" ) ) {
     return;
    }
    // JQuery creates a special event for focusin when it doesn't
    // exist natively. To improve performance, the native event
    // object is reused and the type is changed. Therefore, we can't
    // rely on the type being correct after the event finished
    // bubbling, so we set it back to the previous value. (#8740)
    if ( event ) {
     event.type = eventType;
    }
    this._open( event, target, response );
   } );
  } );
  if ( content ) {
   this._open( event, target, content );
  }
 },
 _open: function( event, target, content ) {
  var tooltipData, tooltip, delayedShow, a11yContent,
   positionOption = $.extend( {}, this.options.position );
  if ( !content ) {
   return;
  }
  // Content can be updated multiple times. If the tooltip already
  // exists, then just update the content and bail.
  tooltipData = this._find( target );
  if ( tooltipData ) {
   tooltipData.tooltip.find( ".ui-tooltip-content" ).html( content );
   return;
  }
  // If we have a title, clear it to prevent the native tooltip
  // we have to check first to avoid defining a title if none exists
  // (we don't want to cause an element to start matching [title])
  //
  // We use removeAttr only for key events, to allow IE to export the correct
  // accessible attributes. For mouse events, set to empty string to avoid
  // native tooltip showing up (happens only when removing inside mouseover).
  if ( target.is( "[title]" ) ) {
   if ( event && event.type === "mouseover" ) {
    target.attr( "title", "" );
   } else {
    target.removeAttr( "title" );
   }
  }
  tooltipData = this._tooltip( target );
  tooltip = tooltipData.tooltip;
  this._addDescribedBy( target, tooltip.attr( "id" ) );
  tooltip.find( ".ui-tooltip-content" ).html( content );
  // Support: Voiceover on OS X, JAWS on IE <= 9
  // JAWS announces deletions even when aria-relevant="additions"
  // Voiceover will sometimes re-read the entire log region's contents from the beginning
  this.liveRegion.children().hide();
  a11yContent = $( "<div>" ).html( tooltip.find( ".ui-tooltip-content" ).html() );
  a11yContent.removeAttr( "name" ).find( "[name]" ).removeAttr( "name" );
  a11yContent.removeAttr( "id" ).find( "[id]" ).removeAttr( "id" );
  a11yContent.appendTo( this.liveRegion );
  function position( event ) {
   positionOption.of = event;
   if ( tooltip.is( ":hidden" ) ) {
    return;
   }
   tooltip.position( positionOption );
  }
  if ( this.options.track && event && /^mouse/.test( event.type ) ) {
   this._on( this.document, {
    mousemove: position
   } );
   // trigger once to override element-relative positioning
   position( event );
  } else {
   tooltip.position( $.extend( {
    of: target
   }, this.options.position ) );
  }
  tooltip.hide();
  this._show( tooltip, this.options.show );
  // Handle tracking tooltips that are shown with a delay (#8644). As soon
  // as the tooltip is visible, position the tooltip using the most recent
  // event.
  // Adds the check to add the timers only when both delay and track options are set (#14682)
  if ( this.options.track && this.options.show && this.options.show.delay ) {
   delayedShow = this.delayedShow = setInterval( function() {
    if ( tooltip.is( ":visible" ) ) {
     position( positionOption.of );
     clearInterval( delayedShow );
    }
   }, $.fx.interval );
  }
  this._trigger( "open", event, { tooltip: tooltip } );
 },
 _registerCloseHandlers: function( event, target ) {
  var events = {
   keyup: function( event ) {
    if ( event.keyCode === $.ui.keyCode.ESCAPE ) {
     var fakeEvent = $.Event( event );
     fakeEvent.currentTarget = target[ 0 ];
     this.close( fakeEvent, true );
    }
   }
  };
  // Only bind remove handler for delegated targets. Non-delegated
  // tooltips will handle this in destroy.
  if ( target[ 0 ] !== this.element[ 0 ] ) {
   events.remove = function() {
    this._removeTooltip( this._find( target ).tooltip );
   };
  }
  if ( !event || event.type === "mouseover" ) {
   events.mouseleave = "close";
  }
  if ( !event || event.type === "focusin" ) {
   events.focusout = "close";
  }
  this._on( true, target, events );
 },
 close: function( event ) {
  var tooltip,
   that = this,
   target = $( event ? event.currentTarget : this.element ),
   tooltipData = this._find( target );
  // The tooltip may already be closed
  if ( !tooltipData ) {
   // We set ui-tooltip-open immediately upon open (in open()), but only set the
   // additional data once there's actually content to show (in _open()). So even if the
   // tooltip doesn't have full data, we always remove ui-tooltip-open in case we're in
   // the period between open() and _open().
   target.removeData( "ui-tooltip-open" );
   return;
  }
  tooltip = tooltipData.tooltip;
  // Disabling closes the tooltip, so we need to track when we're closing
  // to avoid an infinite loop in case the tooltip becomes disabled on close
  if ( tooltipData.closing ) {
   return;
  }
  // Clear the interval for delayed tracking tooltips
  clearInterval( this.delayedShow );
  // Only set title if we had one before (see comment in _open())
  // If the title attribute has changed since open(), don't restore
  if ( target.data( "ui-tooltip-title" ) && !target.attr( "title" ) ) {
   target.attr( "title", target.data( "ui-tooltip-title" ) );
  }
  this._removeDescribedBy( target );
  tooltipData.hiding = true;
  tooltip.stop( true );
  this._hide( tooltip, this.options.hide, function() {
   that._removeTooltip( $( this ) );
  } );
  target.removeData( "ui-tooltip-open" );
  this._off( target, "mouseleave focusout keyup" );
  // Remove 'remove' binding only on delegated targets
  if ( target[ 0 ] !== this.element[ 0 ] ) {
   this._off( target, "remove" );
  }
  this._off( this.document, "mousemove" );
  if ( event && event.type === "mouseleave" ) {
   $.each( this.parents, function( id, parent ) {
    $( parent.element ).attr( "title", parent.title );
    delete that.parents[ id ];
   } );
  }
  tooltipData.closing = true;
  this._trigger( "close", event, { tooltip: tooltip } );
  if ( !tooltipData.hiding ) {
   tooltipData.closing = false;
  }
 },
 _tooltip: function( element ) {
  var tooltip = $( "<div>" ).attr( "role", "tooltip" ),
   content = $( "<div>" ).appendTo( tooltip ),
   id = tooltip.uniqueId().attr( "id" );
  this._addClass( content, "ui-tooltip-content" );
  this._addClass( tooltip, "ui-tooltip", "ui-widget ui-widget-content" );
  tooltip.appendTo( this._appendTo( element ) );
  return this.tooltips[ id ] = {
   element: element,
   tooltip: tooltip
  };
 },
 _find: function( target ) {
  var id = target.data( "ui-tooltip-id" );
  return id ? this.tooltips[ id ] : null;
 },
 _removeTooltip: function( tooltip ) {
  tooltip.remove();
  delete this.tooltips[ tooltip.attr( "id" ) ];
 },
 _appendTo: function( target ) {
  var element = target.closest( ".ui-front, dialog" );
  if ( !element.length ) {
   element = this.document[ 0 ].body;
  }
  return element;
 },
 _destroy: function() {
  var that = this;
  // Close open tooltips
  $.each( this.tooltips, function( id, tooltipData ) {
   // Delegate to close method to handle common cleanup
   var event = $.Event( "blur" ),
    element = tooltipData.element;
   event.target = event.currentTarget = element[ 0 ];
   that.close( event, true );
   // Remove immediately; destroying an open tooltip doesn't use the
   // hide animation
   $( "#" + id ).remove();
   // Restore the title
   if ( element.data( "ui-tooltip-title" ) ) {
    // If the title attribute has changed since open(), don't restore
    if ( !element.attr( "title" ) ) {
     element.attr( "title", element.data( "ui-tooltip-title" ) );
    }
    element.removeData( "ui-tooltip-title" );
   }
  } );
  this.liveRegion.remove();
 }
} );
// DEPRECATED
// TODO: Switch return back to widget declaration at top of file when this is removed
if ( $.uiBackCompat !== false ) {
 // Backcompat for tooltipClass option
 $.widget( "ui.tooltip", $.ui.tooltip, {
  options: {
   tooltipClass: null
  },
  _tooltip: function() {
   var tooltipData = this._superApply( arguments );
   if ( this.options.tooltipClass ) {
    tooltipData.tooltip.addClass( this.options.tooltipClass );
   }
   return tooltipData;
  }
 } );
}
var widgetsTooltip = $.ui.tooltip;
}));
   /* Ende jqueryUI */
   /* Start jquery_maphilight */
   /*!
 * Copyright (c) 2008 David Lynch, http://davidlynch.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
(function(root,factory){if(typeof define==="function"&&define.amd){define(["jquery"],factory);}else{factory(root.jQuery);}})(this,function($){var has_VML,has_canvas,create_canvas_for,add_shape_to,clear_canvas,shape_from_area,canvas_style,hex_to_decimal,css3color,is_image_loaded,options_from_area;has_canvas=!!document.createElement("canvas").getContext;has_VML=(function(){var a=document.createElement("div");a.innerHTML='<v:shape id="vml_flag1" adj="1" />';var b=a.firstChild;b.style.behavior="url(#default#VML)";return b?typeof b.adj=="object":true;})();if(!(has_canvas||has_VML)){$.fn.maphilight=function(){return this;};return;}if(has_canvas){hex_to_decimal=function(hex){return Math.max(0,Math.min(parseInt(hex,16),255));};css3color=function(color,opacity){return"rgba("+hex_to_decimal(color.substr(0,2))+","+hex_to_decimal(color.substr(2,2))+","+hex_to_decimal(color.substr(4,2))+","+opacity+")";};create_canvas_for=function(img){var c=$('<canvas style="width:'+$(img).width()+"px;height:"+$(img).height()+'px;"></canvas>').get(0);c.getContext("2d").clearRect(0,0,$(img).width(),$(img).height());return c;};var draw_shape=function(context,shape,coords,x_shift,y_shift){x_shift=x_shift||0;y_shift=y_shift||0;context.beginPath();if(shape=="rect"){context.rect(coords[0]+x_shift,coords[1]+y_shift,coords[2]-coords[0],coords[3]-coords[1]);}else{if(shape=="poly"){context.moveTo(coords[0]+x_shift,coords[1]+y_shift);for(i=2;i<coords.length;i+=2){context.lineTo(coords[i]+x_shift,coords[i+1]+y_shift);}}else{if(shape=="circ"){context.arc(coords[0]+x_shift,coords[1]+y_shift,coords[2],0,Math.PI*2,false);}}}context.closePath();};add_shape_to=function(canvas,shape,coords,options,name){var i,context=canvas.getContext("2d");if(options.shadow){context.save();if(options.shadowPosition=="inside"){draw_shape(context,shape,coords);context.clip();}var x_shift=canvas.width*100;var y_shift=canvas.height*100;draw_shape(context,shape,coords,x_shift,y_shift);context.shadowOffsetX=options.shadowX-x_shift;context.shadowOffsetY=options.shadowY-y_shift;context.shadowBlur=options.shadowRadius;context.shadowColor=css3color(options.shadowColor,options.shadowOpacity);var shadowFrom=options.shadowFrom;if(!shadowFrom){if(options.shadowPosition=="outside"){shadowFrom="fill";}else{shadowFrom="stroke";}}if(shadowFrom=="stroke"){context.strokeStyle="rgba(0,0,0,1)";context.stroke();}else{if(shadowFrom=="fill"){context.fillStyle="rgba(0,0,0,1)";context.fill();}}context.restore();if(options.shadowPosition=="outside"){context.save();draw_shape(context,shape,coords);context.globalCompositeOperation="destination-out";context.fillStyle="rgba(0,0,0,1);";context.fill();context.restore();}}context.save();draw_shape(context,shape,coords);if(options.fill){context.fillStyle=css3color(options.fillColor,options.fillOpacity);context.fill();}if(options.stroke){context.strokeStyle=css3color(options.strokeColor,options.strokeOpacity);context.lineWidth=options.strokeWidth;context.stroke();}context.restore();if(options.fade){$(canvas).css("opacity",0).animate({opacity:1},100);}};clear_canvas=function(canvas){canvas.getContext("2d").clearRect(0,0,canvas.width,canvas.height);};}else{create_canvas_for=function(img){return $('<var style="zoom:1;overflow:hidden;display:block;width:'+img.width+"px;height:"+img.height+'px;"></var>').get(0);};add_shape_to=function(canvas,shape,coords,options,name){var fill,stroke,opacity,e;for(var i in coords){coords[i]=parseInt(coords[i],10);}fill='<v:fill color="#'+options.fillColor+'" opacity="'+(options.fill?options.fillOpacity:0)+'" />';stroke=(options.stroke?'strokeweight="'+options.strokeWidth+'" stroked="t" strokecolor="#'+options.strokeColor+'"':'stroked="f"');opacity='<v:stroke opacity="'+options.strokeOpacity+'"/>';if(shape=="rect"){e=$('<v:rect name="'+name+'" filled="t" '+stroke+' style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:'+coords[0]+"px;top:"+coords[1]+"px;width:"+(coords[2]-coords[0])+"px;height:"+(coords[3]-coords[1])+'px;"></v:rect>');}else{if(shape=="poly"){e=$('<v:shape name="'+name+'" filled="t" '+stroke+' coordorigin="0,0" coordsize="'+canvas.width+","+canvas.height+'" path="m '+coords[0]+","+coords[1]+" l "+coords.join(",")+' x e" style="zoom:1;margin:0;padding:0;display:block;position:absolute;top:0px;left:0px;width:'+canvas.width+"px;height:"+canvas.height+'px;"></v:shape>');}else{if(shape=="circ"){e=$('<v:oval name="'+name+'" filled="t" '+stroke+' style="zoom:1;margin:0;padding:0;display:block;position:absolute;left:'+(coords[0]-coords[2])+"px;top:"+(coords[1]-coords[2])+"px;width:"+(coords[2]*2)+"px;height:"+(coords[2]*2)+'px;"></v:oval>');}}}e.get(0).innerHTML=fill+opacity;$(canvas).append(e);};clear_canvas=function(canvas){var $html=$("<div>"+canvas.innerHTML+"</div>");$html.children("[name=highlighted]").remove();canvas.innerHTML=$html.html();};}shape_from_area=function(area){var i,coords=area.getAttribute("coords").split(",");for(i=0;i<coords.length;i++){coords[i]=parseFloat(coords[i]);}return[area.getAttribute("shape").toLowerCase().substr(0,4),coords];};options_from_area=function(area,options){var $area=$(area);return $.extend({},options,$.metadata?$area.metadata():false,$area.data("maphilight"));};is_image_loaded=function(img){if(!img.complete){return false;}if(typeof img.naturalWidth!="undefined"&&img.naturalWidth===0){return false;}return true;};canvas_style={position:"absolute",left:0,top:0,padding:0,border:0};var ie_hax_done=false;$.fn.maphilight=function(opts){opts=$.extend({},$.fn.maphilight.defaults,opts);if(!has_canvas&&!ie_hax_done){$(window).ready(function(){document.namespaces.add("v","urn:schemas-microsoft-com:vml");var style=document.createStyleSheet();var shapes=["shape","rect","oval","circ","fill","stroke","imagedata","group","textbox"];$.each(shapes,function(){style.addRule("v\\:"+this,"behavior: url(#default#VML); antialias:true");});});ie_hax_done=true;}return this.each(function(){var img,wrap,options,map,canvas,canvas_always,highlighted_shape,usemap;img=$(this);if(!is_image_loaded(this)){return window.setTimeout(function(){img.maphilight(opts);},200);}options=$.extend({},opts,$.metadata?img.metadata():false,img.data("maphilight"));usemap=img.get(0).getAttribute("usemap");if(!usemap){return;}map=$('map[name="'+usemap.substr(1)+'"]');if(!(img.is('img,input[type="image"]')&&usemap&&map.length>0)){return;}if(img.hasClass("maphilighted")){var wrapper=img.parent();img.insertBefore(wrapper);wrapper.remove();$(map).unbind(".maphilight");}wrap=$("<div></div>").css({display:"block",backgroundImage:'url("'+this.src+'")',backgroundSize:"contain",position:"relative",padding:0,width:this.width,height:this.height});if(options.wrapClass){if(options.wrapClass===true){wrap.addClass($(this).attr("class"));}else{wrap.addClass(options.wrapClass);}}img.before(wrap).css("opacity",0).css(canvas_style).remove();if(has_VML){img.css("filter","Alpha(opacity=0)");}wrap.append(img);canvas=create_canvas_for(this);$(canvas).css(canvas_style);canvas.height=this.height;canvas.width=this.width;$(map).bind("alwaysOn.maphilight",function(){if(canvas_always){clear_canvas(canvas_always);}if(!has_canvas){$(canvas).empty();}$(map).find("area[coords]").each(function(){var shape,area_options;area_options=options_from_area(this,options);if(area_options.alwaysOn){if(!canvas_always&&has_canvas){canvas_always=create_canvas_for(img[0]);$(canvas_always).css(canvas_style);canvas_always.width=img[0].width;canvas_always.height=img[0].height;img.before(canvas_always);}area_options.fade=area_options.alwaysOnFade;shape=shape_from_area(this);if(has_canvas){add_shape_to(canvas_always,shape[0],shape[1],area_options,"");}else{add_shape_to(canvas,shape[0],shape[1],area_options,"");}}});}).trigger("alwaysOn.maphilight").bind("mouseover.maphilight, focus.maphilight",function(e){var shape,area_options,area=e.target;area_options=options_from_area(area,options);if(!area_options.neverOn&&!area_options.alwaysOn){shape=shape_from_area(area);add_shape_to(canvas,shape[0],shape[1],area_options,"highlighted");if(area_options.groupBy){var areas;if(/^[a-zA-Z][\-a-zA-Z]+$/.test(area_options.groupBy)){areas=map.find("area["+area_options.groupBy+'="'+$(area).attr(area_options.groupBy)+'"]');}else{areas=map.find(area_options.groupBy);}var first=area;areas.each(function(){if(this!=first){var subarea_options=options_from_area(this,options);if(!subarea_options.neverOn&&!subarea_options.alwaysOn){var shape=shape_from_area(this);add_shape_to(canvas,shape[0],shape[1],subarea_options,"highlighted");}}});}if(!has_canvas){$(canvas).append("<v:rect></v:rect>");}}}).bind("mouseout.maphilight, blur.maphilight",function(e){clear_canvas(canvas);});img.before(canvas);img.addClass("maphilighted");});};$.fn.maphilight.defaults={fill:true,fillColor:"000000",fillOpacity:0.2,stroke:true,strokeColor:"ff0000",strokeOpacity:1,strokeWidth:1,fade:true,alwaysOn:false,neverOn:false,groupBy:false,wrapClass:true,shadow:false,shadowX:0,shadowY:0,shadowRadius:6,shadowColor:"000000",shadowOpacity:0.8,shadowPosition:"outside",shadowFrom:false};});
   /* Ende jquery_maphilight */
   /* Start jquery.magnific-popup */
   /*! Magnific Popup - v0.9.9 - 2013-12-27
* http://dimsemenov.com/plugins/magnific-popup/
* Copyright (c) 2013 Dmitry Semenov; */
(function($){var CLOSE_EVENT="Close",BEFORE_CLOSE_EVENT="BeforeClose",AFTER_CLOSE_EVENT="AfterClose",BEFORE_APPEND_EVENT="BeforeAppend",MARKUP_PARSE_EVENT="MarkupParse",OPEN_EVENT="Open",CHANGE_EVENT="Change",NS="mfp",EVENT_NS="."+NS,READY_CLASS="mfp-ready",REMOVING_CLASS="mfp-removing",PREVENT_CLOSE_CLASS="mfp-prevent-close";var mfp,MagnificPopup=function(){},_isJQ=!!(window.jQuery),_prevStatus,_window=$(window),_body,_document,_prevContentType,_wrapClasses,_currPopupType;var _mfpOn=function(name,f){mfp.ev.on(NS+name+EVENT_NS,f);},_getEl=function(className,appendTo,html,raw){var el=document.createElement("div");el.className="mfp-"+className;if(html){el.innerHTML=html;}if(!raw){el=$(el);if(appendTo){el.appendTo(appendTo);}}else{if(appendTo){appendTo.appendChild(el);}}return el;},_mfpTrigger=function(e,data){mfp.ev.triggerHandler(NS+e,data);if(mfp.st.callbacks){e=e.charAt(0).toLowerCase()+e.slice(1);if(mfp.st.callbacks[e]){mfp.st.callbacks[e].apply(mfp,$.isArray(data)?data:[data]);}}},_getCloseBtn=function(type){if(type!==_currPopupType||!mfp.currTemplate.closeBtn){mfp.currTemplate.closeBtn=$(mfp.st.closeMarkup.replace("%title%",mfp.st.tClose));_currPopupType=type;}return mfp.currTemplate.closeBtn;},_checkInstance=function(){if(!$.magnificPopup.instance){mfp=new MagnificPopup();mfp.init();$.magnificPopup.instance=mfp;}},supportsTransitions=function(){var s=document.createElement("p").style,v=["ms","O","Moz","Webkit"];if(s["transition"]!==undefined){return true;}while(v.length){if(v.pop()+"Transition" in s){return true;}}return false;};MagnificPopup.prototype={constructor:MagnificPopup,init:function(){var appVersion=navigator.appVersion;mfp.isIE7=appVersion.indexOf("MSIE 7.")!==-1;mfp.isIE8=appVersion.indexOf("MSIE 8.")!==-1;mfp.isLowIE=mfp.isIE7||mfp.isIE8;mfp.isAndroid=(/android/gi).test(appVersion);mfp.isIOS=(/iphone|ipad|ipod/gi).test(appVersion);mfp.supportsTransition=supportsTransitions();mfp.probablyMobile=(mfp.isAndroid||mfp.isIOS||/(Opera Mini)|Kindle|webOS|BlackBerry|(Opera Mobi)|(Windows Phone)|IEMobile/i.test(navigator.userAgent));_document=$(document);mfp.popupsCache={};},open:function(data){if(!_body){_body=$(document.body);}var i;if(data.isObj===false){mfp.items=data.items.toArray();mfp.index=0;var items=data.items,item;for(i=0;i<items.length;i++){item=items[i];if(item.parsed){item=item.el[0];}if(item===data.el[0]){mfp.index=i;break;}}}else{mfp.items=$.isArray(data.items)?data.items:[data.items];mfp.index=data.index||0;}if(mfp.isOpen){mfp.updateItemHTML();return;}mfp.types=[];_wrapClasses="";if(data.mainEl&&data.mainEl.length){mfp.ev=data.mainEl.eq(0);}else{mfp.ev=_document;}if(data.key){if(!mfp.popupsCache[data.key]){mfp.popupsCache[data.key]={};}mfp.currTemplate=mfp.popupsCache[data.key];}else{mfp.currTemplate={};}mfp.st=$.extend(true,{},$.magnificPopup.defaults,data);mfp.fixedContentPos=mfp.st.fixedContentPos==="auto"?!mfp.probablyMobile:mfp.st.fixedContentPos;if(mfp.st.modal){mfp.st.closeOnContentClick=false;mfp.st.closeOnBgClick=false;mfp.st.showCloseBtn=false;mfp.st.enableEscapeKey=false;}if(!mfp.bgOverlay){mfp.bgOverlay=_getEl("bg").on("click"+EVENT_NS,function(){mfp.close();});mfp.wrap=_getEl("wrap").attr("tabindex",-1).on("click"+EVENT_NS,function(e){if(mfp._checkIfClose(e.target)){mfp.close();}});mfp.container=_getEl("container",mfp.wrap);}mfp.contentContainer=_getEl("content");if(mfp.st.preloader){mfp.preloader=_getEl("preloader",mfp.container,mfp.st.tLoading);}var modules=$.magnificPopup.modules;for(i=0;i<modules.length;i++){var n=modules[i];n=n.charAt(0).toUpperCase()+n.slice(1);mfp["init"+n].call(mfp);}_mfpTrigger("BeforeOpen");if(mfp.st.showCloseBtn){if(!mfp.st.closeBtnInside){mfp.wrap.append(_getCloseBtn());}else{_mfpOn(MARKUP_PARSE_EVENT,function(e,template,values,item){values.close_replaceWith=_getCloseBtn(item.type);});_wrapClasses+=" mfp-close-btn-in";}}if(mfp.st.alignTop){_wrapClasses+=" mfp-align-top";}if(mfp.fixedContentPos){mfp.wrap.css({overflow:mfp.st.overflowY,overflowX:"hidden",overflowY:mfp.st.overflowY});}else{mfp.wrap.css({top:_window.scrollTop(),position:"absolute"});}if(mfp.st.fixedBgPos===false||(mfp.st.fixedBgPos==="auto"&&!mfp.fixedContentPos)){mfp.bgOverlay.css({height:_document.height(),position:"absolute"});}if(mfp.st.enableEscapeKey){_document.on("keyup"+EVENT_NS,function(e){if(e.keyCode===27){mfp.close();}});}_window.on("resize"+EVENT_NS,function(){mfp.updateSize();});if(!mfp.st.closeOnContentClick){_wrapClasses+=" mfp-auto-cursor";}if(_wrapClasses){mfp.wrap.addClass(_wrapClasses);}var windowHeight=mfp.wH=_window.height();var windowStyles={};if(mfp.fixedContentPos){if(mfp._hasScrollBar(windowHeight)){var s=mfp._getScrollbarSize();if(s){windowStyles.marginRight=s;}}}if(mfp.fixedContentPos){if(!mfp.isIE7){windowStyles.overflow="hidden";}else{$("body, html").css("overflow","hidden");}}var classesToadd=mfp.st.mainClass;if(mfp.isIE7){classesToadd+=" mfp-ie7";}if(classesToadd){mfp._addClassToMFP(classesToadd);}mfp.updateItemHTML();_mfpTrigger("BuildControls");$("html").css(windowStyles);mfp.bgOverlay.add(mfp.wrap).prependTo(mfp.st.prependTo||_body);mfp._lastFocusedEl=document.activeElement;setTimeout(function(){if(mfp.content){mfp._addClassToMFP(READY_CLASS);mfp._setFocus();}else{mfp.bgOverlay.addClass(READY_CLASS);}_document.on("focusin"+EVENT_NS,mfp._onFocusIn);},16);mfp.isOpen=true;mfp.updateSize(windowHeight);_mfpTrigger(OPEN_EVENT);return data;},close:function(){if(!mfp.isOpen){return;}_mfpTrigger(BEFORE_CLOSE_EVENT);mfp.isOpen=false;if(mfp.st.removalDelay&&!mfp.isLowIE&&mfp.supportsTransition){mfp._addClassToMFP(REMOVING_CLASS);setTimeout(function(){mfp._close();},mfp.st.removalDelay);}else{mfp._close();}},_close:function(){_mfpTrigger(CLOSE_EVENT);var classesToRemove=REMOVING_CLASS+" "+READY_CLASS+" ";mfp.bgOverlay.detach();mfp.wrap.detach();mfp.container.empty();if(mfp.st.mainClass){classesToRemove+=mfp.st.mainClass+" ";}mfp._removeClassFromMFP(classesToRemove);if(mfp.fixedContentPos){var windowStyles={marginRight:""};if(mfp.isIE7){$("body, html").css("overflow","");}else{windowStyles.overflow="";}$("html").css(windowStyles);}_document.off("keyup"+EVENT_NS+" focusin"+EVENT_NS);mfp.ev.off(EVENT_NS);mfp.wrap.attr("class","mfp-wrap").removeAttr("style");mfp.bgOverlay.attr("class","mfp-bg");mfp.container.attr("class","mfp-container");if(mfp.st.showCloseBtn&&(!mfp.st.closeBtnInside||mfp.currTemplate[mfp.currItem.type]===true)){if(mfp.currTemplate.closeBtn){mfp.currTemplate.closeBtn.detach();}}if(mfp._lastFocusedEl){$(mfp._lastFocusedEl).focus();}mfp.currItem=null;mfp.content=null;mfp.currTemplate=null;mfp.prevHeight=0;_mfpTrigger(AFTER_CLOSE_EVENT);},updateSize:function(winHeight){if(mfp.isIOS){var zoomLevel=document.documentElement.clientWidth/window.innerWidth;var height=window.innerHeight*zoomLevel;mfp.wrap.css("height",height);mfp.wH=height;}else{mfp.wH=winHeight||_window.height();}if(!mfp.fixedContentPos){mfp.wrap.css("height",mfp.wH);}_mfpTrigger("Resize");},updateItemHTML:function(){var item=mfp.items[mfp.index];mfp.contentContainer.detach();if(mfp.content){mfp.content.detach();}if(!item.parsed){item=mfp.parseEl(mfp.index);}var type=item.type;_mfpTrigger("BeforeChange",[mfp.currItem?mfp.currItem.type:"",type]);mfp.currItem=item;if(!mfp.currTemplate[type]){var markup=mfp.st[type]?mfp.st[type].markup:false;_mfpTrigger("FirstMarkupParse",markup);if(markup){mfp.currTemplate[type]=$(markup);}else{mfp.currTemplate[type]=true;}}if(_prevContentType&&_prevContentType!==item.type){mfp.container.removeClass("mfp-"+_prevContentType+"-holder");}var newContent=mfp["get"+type.charAt(0).toUpperCase()+type.slice(1)](item,mfp.currTemplate[type]);mfp.appendContent(newContent,type);item.preloaded=true;_mfpTrigger(CHANGE_EVENT,item);_prevContentType=item.type;mfp.container.prepend(mfp.contentContainer);_mfpTrigger("AfterChange");},appendContent:function(newContent,type){mfp.content=newContent;if(newContent){if(mfp.st.showCloseBtn&&mfp.st.closeBtnInside&&mfp.currTemplate[type]===true){if(!mfp.content.find(".mfp-close").length){mfp.content.append(_getCloseBtn());}}else{mfp.content=newContent;}}else{mfp.content="";}_mfpTrigger(BEFORE_APPEND_EVENT);mfp.container.addClass("mfp-"+type+"-holder");mfp.contentContainer.append(mfp.content);},parseEl:function(index){var item=mfp.items[index],type;if(item.tagName){item={el:$(item)};}else{type=item.type;item={data:item,src:item.src};}if(item.el){var types=mfp.types;for(var i=0;i<types.length;i++){if(item.el.hasClass("mfp-"+types[i])){type=types[i];break;}}item.src=item.el.attr("data-mfp-src");if(!item.src){item.src=item.el.attr("href");}}item.type=type||mfp.st.type||"inline";item.index=index;item.parsed=true;mfp.items[index]=item;_mfpTrigger("ElementParse",item);return mfp.items[index];},addGroup:function(el,options){var eHandler=function(e){e.mfpEl=this;mfp._openClick(e,el,options);};if(!options){options={};}var eName="click.magnificPopup";options.mainEl=el;if(options.items){options.isObj=true;el.off(eName).on(eName,eHandler);}else{options.isObj=false;if(options.delegate){el.off(eName).on(eName,options.delegate,eHandler);}else{options.items=el;el.off(eName).on(eName,eHandler);}}},_openClick:function(e,el,options){var midClick=options.midClick!==undefined?options.midClick:$.magnificPopup.defaults.midClick;if(!midClick&&(e.which===2||e.ctrlKey||e.metaKey)){return;}var disableOn=options.disableOn!==undefined?options.disableOn:$.magnificPopup.defaults.disableOn;if(disableOn){if($.isFunction(disableOn)){if(!disableOn.call(mfp)){return true;}}else{if(_window.width()<disableOn){return true;}}}if(e.type){e.preventDefault();if(mfp.isOpen){e.stopPropagation();}}options.el=$(e.mfpEl);if(options.delegate){options.items=el.find(options.delegate);}mfp.open(options);},updateStatus:function(status,text){if(mfp.preloader){if(_prevStatus!==status){mfp.container.removeClass("mfp-s-"+_prevStatus);}if(!text&&status==="loading"){text=mfp.st.tLoading;}var data={status:status,text:text};_mfpTrigger("UpdateStatus",data);status=data.status;text=data.text;mfp.preloader.html(text);mfp.preloader.find("a").on("click",function(e){e.stopImmediatePropagation();});mfp.container.addClass("mfp-s-"+status);_prevStatus=status;}},_checkIfClose:function(target){if($(target).hasClass(PREVENT_CLOSE_CLASS)){return;}var closeOnContent=mfp.st.closeOnContentClick;var closeOnBg=mfp.st.closeOnBgClick;if(closeOnContent&&closeOnBg){return true;}else{if(!mfp.content||$(target).hasClass("mfp-close")||(mfp.preloader&&target===mfp.preloader[0])){return true;}if((target!==mfp.content[0]&&!$.contains(mfp.content[0],target))){if(closeOnBg){if($.contains(document,target)){return true;}}}else{if(closeOnContent){return true;}}}return false;},_addClassToMFP:function(cName){mfp.bgOverlay.addClass(cName);mfp.wrap.addClass(cName);},_removeClassFromMFP:function(cName){this.bgOverlay.removeClass(cName);mfp.wrap.removeClass(cName);},_hasScrollBar:function(winHeight){return((mfp.isIE7?_document.height():document.body.scrollHeight)>(winHeight||_window.height()));},_setFocus:function(){(mfp.st.focus?mfp.content.find(mfp.st.focus).eq(0):mfp.wrap).focus();},_onFocusIn:function(e){if(e.target!==mfp.wrap[0]&&!$.contains(mfp.wrap[0],e.target)){mfp._setFocus();return false;}},_parseMarkup:function(template,values,item){var arr;if(item.data){values=$.extend(item.data,values);}_mfpTrigger(MARKUP_PARSE_EVENT,[template,values,item]);$.each(values,function(key,value){if(value===undefined||value===false){return true;}arr=key.split("_");if(arr.length>1){var el=template.find(EVENT_NS+"-"+arr[0]);if(el.length>0){var attr=arr[1];if(attr==="replaceWith"){if(el[0]!==value[0]){el.replaceWith(value);}}else{if(attr==="img"){if(el.is("img")){el.attr("src",value);}else{el.replaceWith('<img src="'+value+'" class="'+el.attr("class")+'" />');}}else{el.attr(arr[1],value);}}}}else{template.find(EVENT_NS+"-"+key).html(value);}});},_getScrollbarSize:function(){if(mfp.scrollbarSize===undefined){var scrollDiv=document.createElement("div");scrollDiv.id="mfp-sbm";scrollDiv.style.cssText="width: 99px; height: 99px; overflow: scroll; position: absolute; top: -9999px;";document.body.appendChild(scrollDiv);mfp.scrollbarSize=scrollDiv.offsetWidth-scrollDiv.clientWidth;document.body.removeChild(scrollDiv);}return mfp.scrollbarSize;}};$.magnificPopup={instance:null,proto:MagnificPopup.prototype,modules:[],open:function(options,index){_checkInstance();if(!options){options={};}else{options=$.extend(true,{},options);}options.isObj=true;options.index=index||0;return this.instance.open(options);},close:function(){return $.magnificPopup.instance&&$.magnificPopup.instance.close();},registerModule:function(name,module){if(module.options){$.magnificPopup.defaults[name]=module.options;}$.extend(this.proto,module.proto);this.modules.push(name);},defaults:{disableOn:0,key:null,midClick:false,mainClass:"",preloader:true,focus:"",closeOnContentClick:false,closeOnBgClick:true,closeBtnInside:true,showCloseBtn:true,enableEscapeKey:true,modal:false,alignTop:false,removalDelay:0,prependTo:null,fixedContentPos:"auto",fixedBgPos:"auto",overflowY:"auto",closeMarkup:'<button title="%title%" type="button" class="mfp-close">&times;</button>',tClose:"Close (Esc)",tLoading:"Loading..."}};$.fn.magnificPopup=function(options){_checkInstance();var jqEl=$(this);if(typeof options==="string"){if(options==="open"){var items,itemOpts=_isJQ?jqEl.data("magnificPopup"):jqEl[0].magnificPopup,index=parseInt(arguments[1],10)||0;if(itemOpts.items){items=itemOpts.items[index];}else{items=jqEl;if(itemOpts.delegate){items=items.find(itemOpts.delegate);}items=items.eq(index);}mfp._openClick({mfpEl:items},jqEl,itemOpts);}else{if(mfp.isOpen){mfp[options].apply(mfp,Array.prototype.slice.call(arguments,1));}}}else{options=$.extend(true,{},options);if(_isJQ){jqEl.data("magnificPopup",options);}else{jqEl[0].magnificPopup=options;}mfp.addGroup(jqEl,options);}return jqEl;};var INLINE_NS="inline",_hiddenClass,_inlinePlaceholder,_lastInlineElement,_putInlineElementsBack=function(){if(_lastInlineElement){_inlinePlaceholder.after(_lastInlineElement.addClass(_hiddenClass)).detach();_lastInlineElement=null;}};$.magnificPopup.registerModule(INLINE_NS,{options:{hiddenClass:"hide",markup:"",tNotFound:"Content not found"},proto:{initInline:function(){mfp.types.push(INLINE_NS);_mfpOn(CLOSE_EVENT+"."+INLINE_NS,function(){_putInlineElementsBack();});},getInline:function(item,template){_putInlineElementsBack();if(item.src){var inlineSt=mfp.st.inline,el=$(item.src);if(el.length){var parent=el[0].parentNode;if(parent&&parent.tagName){if(!_inlinePlaceholder){_hiddenClass=inlineSt.hiddenClass;_inlinePlaceholder=_getEl(_hiddenClass);_hiddenClass="mfp-"+_hiddenClass;}_lastInlineElement=el.after(_inlinePlaceholder).detach().removeClass(_hiddenClass);}mfp.updateStatus("ready");}else{mfp.updateStatus("error",inlineSt.tNotFound);el=$("<div>");}item.inlineElement=el;return el;}mfp.updateStatus("ready");mfp._parseMarkup(template,{},item);return template;}}});var AJAX_NS="ajax",_ajaxCur,_removeAjaxCursor=function(){if(_ajaxCur){_body.removeClass(_ajaxCur);}},_destroyAjaxRequest=function(){_removeAjaxCursor();if(mfp.req){mfp.req.abort();}};$.magnificPopup.registerModule(AJAX_NS,{options:{settings:null,cursor:"mfp-ajax-cur",tError:'<a href="%url%">The content</a> could not be loaded.'},proto:{initAjax:function(){mfp.types.push(AJAX_NS);_ajaxCur=mfp.st.ajax.cursor;_mfpOn(CLOSE_EVENT+"."+AJAX_NS,_destroyAjaxRequest);_mfpOn("BeforeChange."+AJAX_NS,_destroyAjaxRequest);},getAjax:function(item){if(_ajaxCur){_body.addClass(_ajaxCur);}mfp.updateStatus("loading");var opts=$.extend({url:item.src,success:function(data,textStatus,jqXHR){var temp={data:data,xhr:jqXHR};_mfpTrigger("ParseAjax",temp);mfp.appendContent($(temp.data),AJAX_NS);item.finished=true;_removeAjaxCursor();mfp._setFocus();setTimeout(function(){mfp.wrap.addClass(READY_CLASS);},16);mfp.updateStatus("ready");_mfpTrigger("AjaxContentAdded");},error:function(){_removeAjaxCursor();item.finished=item.loadError=true;mfp.updateStatus("error",mfp.st.ajax.tError.replace("%url%",item.src));}},mfp.st.ajax.settings);mfp.req=$.ajax(opts);return"";}}});var _imgInterval,_getTitle=function(item){if(item.data&&item.data.title!==undefined){return item.data.title;}var src=mfp.st.image.titleSrc;if(src){if($.isFunction(src)){return src.call(mfp,item);}else{if(item.el){return item.el.attr(src)||"";}}}return"";};$.magnificPopup.registerModule("image",{options:{markup:'<div class="mfp-figure">'+'<div class="mfp-close"></div>'+"<figure>"+'<div class="mfp-img"></div>'+"<figcaption>"+'<div class="mfp-bottom-bar">'+'<div class="mfp-title"></div>'+'<div class="mfp-counter"></div>'+"</div>"+"</figcaption>"+"</figure>"+"</div>",cursor:"mfp-zoom-out-cur",titleSrc:"title",verticalFit:true,tError:'<a href="%url%">The image</a> could not be loaded.'},proto:{initImage:function(){var imgSt=mfp.st.image,ns=".image";mfp.types.push("image");_mfpOn(OPEN_EVENT+ns,function(){if(mfp.currItem.type==="image"&&imgSt.cursor){_body.addClass(imgSt.cursor);}});_mfpOn(CLOSE_EVENT+ns,function(){if(imgSt.cursor){_body.removeClass(imgSt.cursor);}_window.off("resize"+EVENT_NS);});_mfpOn("Resize"+ns,mfp.resizeImage);if(mfp.isLowIE){_mfpOn("AfterChange",mfp.resizeImage);}},resizeImage:function(){var item=mfp.currItem;if(!item||!item.img){return;}if(mfp.st.image.verticalFit){var decr=0;if(mfp.isLowIE){decr=parseInt(item.img.css("padding-top"),10)+parseInt(item.img.css("padding-bottom"),10);}item.img.css("max-height",mfp.wH-decr);}},_onImageHasSize:function(item){if(item.img){item.hasSize=true;if(_imgInterval){clearInterval(_imgInterval);}item.isCheckingImgSize=false;_mfpTrigger("ImageHasSize",item);if(item.imgHidden){if(mfp.content){mfp.content.removeClass("mfp-loading");}item.imgHidden=false;}}},findImageSize:function(item){var counter=0,img=item.img[0],mfpSetInterval=function(delay){if(_imgInterval){clearInterval(_imgInterval);}_imgInterval=setInterval(function(){if(img.naturalWidth>0){mfp._onImageHasSize(item);return;}if(counter>200){clearInterval(_imgInterval);}counter++;if(counter===3){mfpSetInterval(10);}else{if(counter===40){mfpSetInterval(50);}else{if(counter===100){mfpSetInterval(500);}}}},delay);};mfpSetInterval(1);},getImage:function(item,template){var guard=0,onLoadComplete=function(){if(item){if(item.img[0].complete){item.img.off(".mfploader");if(item===mfp.currItem){mfp._onImageHasSize(item);mfp.updateStatus("ready");}item.hasSize=true;item.loaded=true;_mfpTrigger("ImageLoadComplete");}else{guard++;if(guard<200){setTimeout(onLoadComplete,100);}else{onLoadError();}}}},onLoadError=function(){if(item){item.img.off(".mfploader");if(item===mfp.currItem){mfp._onImageHasSize(item);mfp.updateStatus("error",imgSt.tError.replace("%url%",item.src));}item.hasSize=true;item.loaded=true;item.loadError=true;}},imgSt=mfp.st.image;var el=template.find(".mfp-img");if(el.length){var img=document.createElement("img");img.className="mfp-img";item.img=$(img).on("load.mfploader",onLoadComplete).on("error.mfploader",onLoadError);img.src=item.src;if(el.is("img")){item.img=item.img.clone();}img=item.img[0];if(img.naturalWidth>0){item.hasSize=true;}else{if(!img.width){item.hasSize=false;}}}mfp._parseMarkup(template,{title:_getTitle(item),img_replaceWith:item.img},item);mfp.resizeImage();if(item.hasSize){if(_imgInterval){clearInterval(_imgInterval);}if(item.loadError){template.addClass("mfp-loading");mfp.updateStatus("error",imgSt.tError.replace("%url%",item.src));}else{template.removeClass("mfp-loading");mfp.updateStatus("ready");}return template;}mfp.updateStatus("loading");item.loading=true;if(!item.hasSize){item.imgHidden=true;template.addClass("mfp-loading");mfp.findImageSize(item);}return template;}}});var hasMozTransform,getHasMozTransform=function(){if(hasMozTransform===undefined){hasMozTransform=document.createElement("p").style.MozTransform!==undefined;}return hasMozTransform;};$.magnificPopup.registerModule("zoom",{options:{enabled:false,easing:"ease-in-out",duration:300,opener:function(element){return element.is("img")?element:element.find("img");}},proto:{initZoom:function(){var zoomSt=mfp.st.zoom,ns=".zoom",image;if(!zoomSt.enabled||!mfp.supportsTransition){return;}var duration=zoomSt.duration,getElToAnimate=function(image){var newImg=image.clone().removeAttr("style").removeAttr("class").addClass("mfp-animated-image"),transition="all "+(zoomSt.duration/1000)+"s "+zoomSt.easing,cssObj={position:"fixed",zIndex:9999,left:0,top:0,"-webkit-backface-visibility":"hidden"},t="transition";cssObj["-webkit-"+t]=cssObj["-moz-"+t]=cssObj["-o-"+t]=cssObj[t]=transition;newImg.css(cssObj);return newImg;},showMainContent=function(){mfp.content.css("visibility","visible");},openTimeout,animatedImg;_mfpOn("BuildControls"+ns,function(){if(mfp._allowZoom()){clearTimeout(openTimeout);mfp.content.css("visibility","hidden");image=mfp._getItemToZoom();if(!image){showMainContent();return;}animatedImg=getElToAnimate(image);animatedImg.css(mfp._getOffset());mfp.wrap.append(animatedImg);openTimeout=setTimeout(function(){animatedImg.css(mfp._getOffset(true));openTimeout=setTimeout(function(){showMainContent();setTimeout(function(){animatedImg.remove();image=animatedImg=null;_mfpTrigger("ZoomAnimationEnded");},16);},duration);},16);}});_mfpOn(BEFORE_CLOSE_EVENT+ns,function(){if(mfp._allowZoom()){clearTimeout(openTimeout);mfp.st.removalDelay=duration;if(!image){image=mfp._getItemToZoom();if(!image){return;}animatedImg=getElToAnimate(image);}animatedImg.css(mfp._getOffset(true));mfp.wrap.append(animatedImg);mfp.content.css("visibility","hidden");setTimeout(function(){animatedImg.css(mfp._getOffset());},16);}});_mfpOn(CLOSE_EVENT+ns,function(){if(mfp._allowZoom()){showMainContent();if(animatedImg){animatedImg.remove();}image=null;}});},_allowZoom:function(){return mfp.currItem.type==="image";},_getItemToZoom:function(){if(mfp.currItem.hasSize){return mfp.currItem.img;}else{return false;}},_getOffset:function(isLarge){var el;if(isLarge){el=mfp.currItem.img;}else{el=mfp.st.zoom.opener(mfp.currItem.el||mfp.currItem);}var offset=el.offset();var paddingTop=parseInt(el.css("padding-top"),10);var paddingBottom=parseInt(el.css("padding-bottom"),10);offset.top-=($(window).scrollTop()-paddingTop);var obj={width:el.width(),height:(_isJQ?el.innerHeight():el[0].offsetHeight)-paddingBottom-paddingTop};if(getHasMozTransform()){obj["-moz-transform"]=obj["transform"]="translate("+offset.left+"px,"+offset.top+"px)";}else{obj.left=offset.left;obj.top=offset.top;}return obj;}}});var IFRAME_NS="iframe",_emptyPage="//about:blank",_fixIframeBugs=function(isShowing){if(mfp.currTemplate[IFRAME_NS]){var el=mfp.currTemplate[IFRAME_NS].find("iframe");if(el.length){if(!isShowing){el[0].src=_emptyPage;}if(mfp.isIE8){el.css("display",isShowing?"block":"none");}}}};$.magnificPopup.registerModule(IFRAME_NS,{options:{markup:'<div class="mfp-iframe-scaler">'+'<div class="mfp-close"></div>'+'<iframe class="mfp-iframe" src="//about:blank" frameborder="0" allowfullscreen></iframe>'+"</div>",srcAction:"iframe_src",patterns:{youtube:{index:"youtube.com",id:"v=",src:"//www.youtube.com/embed/%id%?autoplay=1"},vimeo:{index:"vimeo.com/",id:"/",src:"//player.vimeo.com/video/%id%?autoplay=1"},gmaps:{index:"//maps.google.",src:"%id%&output=embed"}}},proto:{initIframe:function(){mfp.types.push(IFRAME_NS);_mfpOn("BeforeChange",function(e,prevType,newType){if(prevType!==newType){if(prevType===IFRAME_NS){_fixIframeBugs();}else{if(newType===IFRAME_NS){_fixIframeBugs(true);}}}});_mfpOn(CLOSE_EVENT+"."+IFRAME_NS,function(){_fixIframeBugs();});},getIframe:function(item,template){var embedSrc=item.src;var iframeSt=mfp.st.iframe;$.each(iframeSt.patterns,function(){if(embedSrc.indexOf(this.index)>-1){if(this.id){if(typeof this.id==="string"){embedSrc=embedSrc.substr(embedSrc.lastIndexOf(this.id)+this.id.length,embedSrc.length);}else{embedSrc=this.id.call(this,embedSrc);}}embedSrc=this.src.replace("%id%",embedSrc);return false;}});var dataObj={};if(iframeSt.srcAction){dataObj[iframeSt.srcAction]=embedSrc;}mfp._parseMarkup(template,dataObj,item);mfp.updateStatus("ready");return template;}}});var _getLoopedId=function(index){var numSlides=mfp.items.length;if(index>numSlides-1){return index-numSlides;}else{if(index<0){return numSlides+index;}}return index;},_replaceCurrTotal=function(text,curr,total){return text.replace(/%curr%/gi,curr+1).replace(/%total%/gi,total);};$.magnificPopup.registerModule("gallery",{options:{enabled:false,arrowMarkup:'<button title="%title%" type="button" class="mfp-arrow mfp-arrow-%dir%"></button>',preload:[0,2],navigateByImgClick:true,arrows:true,tPrev:"Previous (Left arrow key)",tNext:"Next (Right arrow key)",tCounter:"%curr% of %total%"},proto:{initGallery:function(){var gSt=mfp.st.gallery,ns=".mfp-gallery",supportsFastClick=Boolean($.fn.mfpFastClick);mfp.direction=true;if(!gSt||!gSt.enabled){return false;}_wrapClasses+=" mfp-gallery";_mfpOn(OPEN_EVENT+ns,function(){if(gSt.navigateByImgClick){mfp.wrap.on("click"+ns,".mfp-img",function(){if(mfp.items.length>1){mfp.next();return false;}});}_document.on("keydown"+ns,function(e){if(e.keyCode===37){mfp.prev();}else{if(e.keyCode===39){mfp.next();}}});});_mfpOn("UpdateStatus"+ns,function(e,data){if(data.text){data.text=_replaceCurrTotal(data.text,mfp.currItem.index,mfp.items.length);}});_mfpOn(MARKUP_PARSE_EVENT+ns,function(e,element,values,item){var l=mfp.items.length;values.counter=l>1?_replaceCurrTotal(gSt.tCounter,item.index,l):"";});_mfpOn("BuildControls"+ns,function(){if(mfp.items.length>1&&gSt.arrows&&!mfp.arrowLeft){var markup=gSt.arrowMarkup,arrowLeft=mfp.arrowLeft=$(markup.replace(/%title%/gi,gSt.tPrev).replace(/%dir%/gi,"left")).addClass(PREVENT_CLOSE_CLASS),arrowRight=mfp.arrowRight=$(markup.replace(/%title%/gi,gSt.tNext).replace(/%dir%/gi,"right")).addClass(PREVENT_CLOSE_CLASS);var eName=supportsFastClick?"mfpFastClick":"click";arrowLeft[eName](function(){mfp.prev();});arrowRight[eName](function(){mfp.next();});if(mfp.isIE7){_getEl("b",arrowLeft[0],false,true);_getEl("a",arrowLeft[0],false,true);_getEl("b",arrowRight[0],false,true);_getEl("a",arrowRight[0],false,true);}mfp.container.append(arrowLeft.add(arrowRight));}});_mfpOn(CHANGE_EVENT+ns,function(){if(mfp._preloadTimeout){clearTimeout(mfp._preloadTimeout);}mfp._preloadTimeout=setTimeout(function(){mfp.preloadNearbyImages();mfp._preloadTimeout=null;},16);});_mfpOn(CLOSE_EVENT+ns,function(){_document.off(ns);mfp.wrap.off("click"+ns);if(mfp.arrowLeft&&supportsFastClick){mfp.arrowLeft.add(mfp.arrowRight).destroyMfpFastClick();}mfp.arrowRight=mfp.arrowLeft=null;});},next:function(){mfp.direction=true;mfp.index=_getLoopedId(mfp.index+1);mfp.updateItemHTML();},prev:function(){mfp.direction=false;mfp.index=_getLoopedId(mfp.index-1);mfp.updateItemHTML();},goTo:function(newIndex){mfp.direction=(newIndex>=mfp.index);mfp.index=newIndex;mfp.updateItemHTML();},preloadNearbyImages:function(){var p=mfp.st.gallery.preload,preloadBefore=Math.min(p[0],mfp.items.length),preloadAfter=Math.min(p[1],mfp.items.length),i;for(i=1;i<=(mfp.direction?preloadAfter:preloadBefore);i++){mfp._preloadItem(mfp.index+i);}for(i=1;i<=(mfp.direction?preloadBefore:preloadAfter);i++){mfp._preloadItem(mfp.index-i);}},_preloadItem:function(index){index=_getLoopedId(index);if(mfp.items[index].preloaded){return;}var item=mfp.items[index];if(!item.parsed){item=mfp.parseEl(index);}_mfpTrigger("LazyLoad",item);if(item.type==="image"){item.img=$('<img class="mfp-img" />').on("load.mfploader",function(){item.hasSize=true;}).on("error.mfploader",function(){item.hasSize=true;item.loadError=true;_mfpTrigger("LazyLoadError",item);}).attr("src",item.src);}item.preloaded=true;}}});var RETINA_NS="retina";$.magnificPopup.registerModule(RETINA_NS,{options:{replaceSrc:function(item){return item.src.replace(/\.\w+$/,function(m){return"@2x"+m;});},ratio:1},proto:{initRetina:function(){if(window.devicePixelRatio>1){var st=mfp.st.retina,ratio=st.ratio;ratio=!isNaN(ratio)?ratio:ratio();if(ratio>1){_mfpOn("ImageHasSize"+"."+RETINA_NS,function(e,item){item.img.css({"max-width":item.img[0].naturalWidth/ratio,"width":"100%"});});_mfpOn("ElementParse"+"."+RETINA_NS,function(e,item){item.src=st.replaceSrc(item,ratio);});}}}}});(function(){var ghostClickDelay=1000,supportsTouch="ontouchstart" in window,unbindTouchMove=function(){_window.off("touchmove"+ns+" touchend"+ns);},eName="mfpFastClick",ns="."+eName;$.fn.mfpFastClick=function(callback){return $(this).each(function(){var elem=$(this),lock;if(supportsTouch){var timeout,startX,startY,pointerMoved,point,numPointers;elem.on("touchstart"+ns,function(e){pointerMoved=false;numPointers=1;point=e.originalEvent?e.originalEvent.touches[0]:e.touches[0];startX=point.clientX;startY=point.clientY;_window.on("touchmove"+ns,function(e){point=e.originalEvent?e.originalEvent.touches:e.touches;numPointers=point.length;point=point[0];if(Math.abs(point.clientX-startX)>10||Math.abs(point.clientY-startY)>10){pointerMoved=true;unbindTouchMove();}}).on("touchend"+ns,function(e){unbindTouchMove();if(pointerMoved||numPointers>1){return;}lock=true;e.preventDefault();clearTimeout(timeout);timeout=setTimeout(function(){lock=false;},ghostClickDelay);callback();});});}elem.on("click"+ns,function(){if(!lock){callback();}});});};$.fn.destroyMfpFastClick=function(){$(this).off("touchstart"+ns+" click"+ns);if(supportsTouch){_window.off("touchmove"+ns+" touchend"+ns);}};})();_checkInstance();})(window.jQuery||window.Zepto);
   /* Ende jquery.magnific-popup */
   /* Start picturefill */
   /*! Picturefill - v2.2.0 - 2014-10-30
 * http://scottjehl.github.io/picturefill
 * Copyright (c) 2014 https://github.com/scottjehl/picturefill/blob/master/Authors.txt; Licensed MIT */
/*! matchMedia() polyfill - Test a CSS media type/query in JS. Authors & copyright (c) 2012: Scott Jehl, Paul Irish, Nicholas Zakas, David Knight. Dual MIT/BSD license */
window.matchMedia||(window.matchMedia=function(){var styleMedia=(window.styleMedia||window.media);if(!styleMedia){var style=document.createElement("style"),script=document.getElementsByTagName("script")[0],info=null;style.type="text/css";style.id="matchmediajs-test";script.parentNode.insertBefore(style,script);info=("getComputedStyle" in window)&&window.getComputedStyle(style,null)||style.currentStyle;styleMedia={matchMedium:function(media){var text="@media "+media+"{ #matchmediajs-test { width: 1px; } }";if(style.styleSheet){style.styleSheet.cssText=text;}else{style.textContent=text;}return info.width==="1px";}};}return function(media){return{matches:styleMedia.matchMedium(media||"all"),media:media||"all"};};}());
/*! Picturefill - Responsive Images that work today.
 * Author: Scott Jehl, Filament Group, 2012 ( new proposal implemented by Shawn Jansepar )
 * License: MIT/GPLv2
 * Spec: http://picture.responsiveimages.org/
 */
(function(w,doc,image){if(w.HTMLPictureElement){w.picturefill=function(){};return;}doc.createElement("picture");var pf={};pf.ns="picturefill";(function(){pf.srcsetSupported="srcset" in image;pf.sizesSupported="sizes" in image;})();pf.trim=function(str){return str.trim?str.trim():str.replace(/^\s+|\s+$/g,"");};pf.endsWith=function(str,suffix){return str.endsWith?str.endsWith(suffix):str.indexOf(suffix,str.length-suffix.length)!==-1;};pf.restrictsMixedContent=function(){return w.location.protocol==="https:";};pf.matchesMedia=function(media){return w.matchMedia&&w.matchMedia(media).matches;};pf.getDpr=function(){return(w.devicePixelRatio||1);};pf.getWidthFromLength=function(length){length=length&&length.indexOf("%")>-1===false&&(parseFloat(length)>0||length.indexOf("calc(")>-1)?length:"100vw";length=length.replace("vw","%");if(!pf.lengthEl){pf.lengthEl=doc.createElement("div");pf.lengthEl.style.cssText="border:0;display:block;font-size:1em;left:0;margin:0;padding:0;position:absolute;visibility:hidden";}pf.lengthEl.style.width=length;doc.body.appendChild(pf.lengthEl);pf.lengthEl.className="helper-from-picturefill-js";if(pf.lengthEl.offsetWidth<=0){pf.lengthEl.style.width=doc.documentElement.offsetWidth+"px";}var offsetWidth=pf.lengthEl.offsetWidth;doc.body.removeChild(pf.lengthEl);return offsetWidth;};pf.types={};pf.types["image/jpeg"]=true;pf.types["image/gif"]=true;pf.types["image/png"]=true;pf.types["image/svg+xml"]=doc.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#Image","1.1");pf.types["image/webp"]=function(){var type="image/webp";image.onerror=function(){pf.types[type]=false;picturefill();};image.onload=function(){pf.types[type]=image.width===1;picturefill();};image.src="data:image/webp;base64,UklGRh4AAABXRUJQVlA4TBEAAAAvAAAAAAfQ//73v/+BiOh/AAA=";};pf.verifyTypeSupport=function(source){var type=source.getAttribute("type");if(type===null||type===""){return true;}else{if(typeof(pf.types[type])==="function"){pf.types[type]();return"pending";}else{return pf.types[type];}}};pf.parseSize=function(sourceSizeStr){var match=/(\([^)]+\))?\s*(.+)/g.exec(sourceSizeStr);return{media:match&&match[1],length:match&&match[2]};};pf.findWidthFromSourceSize=function(sourceSizeListStr){var sourceSizeList=pf.trim(sourceSizeListStr).split(/\s*,\s*/),winningLength;for(var i=0,len=sourceSizeList.length;i<len;i++){var sourceSize=sourceSizeList[i],parsedSize=pf.parseSize(sourceSize),length=parsedSize.length,media=parsedSize.media;if(!length){continue;}if(!media||pf.matchesMedia(media)){winningLength=length;break;}}return pf.getWidthFromLength(winningLength);};pf.parseSrcset=function(srcset){var candidates=[];while(srcset!==""){srcset=srcset.replace(/^\s+/g,"");var pos=srcset.search(/\s/g),url,descriptor=null;if(pos!==-1){url=srcset.slice(0,pos);var last=url.slice(-1);if(last===","||url===""){url=url.replace(/,+$/,"");descriptor="";}srcset=srcset.slice(pos+1);if(descriptor===null){var descpos=srcset.indexOf(",");if(descpos!==-1){descriptor=srcset.slice(0,descpos);srcset=srcset.slice(descpos+1);}else{descriptor=srcset;srcset="";}}}else{url=srcset;srcset="";}if(url||descriptor){candidates.push({url:url,descriptor:descriptor});}}return candidates;};pf.parseDescriptor=function(descriptor,sizesattr){var sizes=sizesattr||"100vw",sizeDescriptor=descriptor&&descriptor.replace(/(^\s+|\s+$)/g,""),widthInCssPixels=pf.findWidthFromSourceSize(sizes),resCandidate;if(sizeDescriptor){var splitDescriptor=sizeDescriptor.split(" ");for(var i=splitDescriptor.length-1;i>=0;i--){var curr=splitDescriptor[i],lastchar=curr&&curr.slice(curr.length-1);if((lastchar==="h"||lastchar==="w")&&!pf.sizesSupported){resCandidate=parseFloat((parseInt(curr,10)/widthInCssPixels));}else{if(lastchar==="x"){var res=curr&&parseFloat(curr,10);resCandidate=res&&!isNaN(res)?res:1;}}}}return resCandidate||1;};pf.getCandidatesFromSourceSet=function(srcset,sizes){var candidates=pf.parseSrcset(srcset),formattedCandidates=[];for(var i=0,len=candidates.length;i<len;i++){var candidate=candidates[i];formattedCandidates.push({url:candidate.url,resolution:pf.parseDescriptor(candidate.descriptor,sizes)});}return formattedCandidates;};pf.dodgeSrcset=function(img){if(img.srcset){img[pf.ns].srcset=img.srcset;img.removeAttribute("srcset");}};pf.processSourceSet=function(el){var srcset=el.getAttribute("srcset"),sizes=el.getAttribute("sizes"),candidates=[];if(el.nodeName.toUpperCase()==="IMG"&&el[pf.ns]&&el[pf.ns].srcset){srcset=el[pf.ns].srcset;}if(srcset){candidates=pf.getCandidatesFromSourceSet(srcset,sizes);}return candidates;};pf.applyBestCandidate=function(candidates,picImg){var candidate,length,bestCandidate;candidates.sort(pf.ascendingSort);length=candidates.length;bestCandidate=candidates[length-1];for(var i=0;i<length;i++){candidate=candidates[i];if(candidate.resolution>=pf.getDpr()){bestCandidate=candidate;break;}}if(bestCandidate&&!pf.endsWith(picImg.src,bestCandidate.url)){if(pf.restrictsMixedContent()&&bestCandidate.url.substr(0,"http:".length).toLowerCase()==="http:"){if(typeof console!==undefined){console.warn("Blocked mixed content image "+bestCandidate.url);}}else{picImg.src=bestCandidate.url;picImg.currentSrc=picImg.src;var style=picImg.style||{},WebkitBackfaceVisibility="webkitBackfaceVisibility" in style,currentZoom=style.zoom;if(WebkitBackfaceVisibility){style.zoom=".999";WebkitBackfaceVisibility=picImg.offsetWidth;style.zoom=currentZoom;}}}};pf.ascendingSort=function(a,b){return a.resolution-b.resolution;};pf.removeVideoShim=function(picture){var videos=picture.getElementsByTagName("video");if(videos.length){var video=videos[0],vsources=video.getElementsByTagName("source");while(vsources.length){picture.insertBefore(vsources[0],video);}video.parentNode.removeChild(video);}};pf.getAllElements=function(){var elems=[],imgs=doc.getElementsByTagName("img");for(var h=0,len=imgs.length;h<len;h++){var currImg=imgs[h];if(currImg.parentNode.nodeName.toUpperCase()==="PICTURE"||(currImg.getAttribute("srcset")!==null)||currImg[pf.ns]&&currImg[pf.ns].srcset!==null){elems.push(currImg);}}return elems;};pf.getMatch=function(img,picture){var sources=picture.childNodes,match;for(var j=0,slen=sources.length;j<slen;j++){var source=sources[j];if(source.nodeType!==1){continue;}if(source===img){return match;}if(source.nodeName.toUpperCase()!=="SOURCE"){continue;}if(source.getAttribute("src")!==null&&typeof console!==undefined){console.warn("The `src` attribute is invalid on `picture` `source` element; instead, use `srcset`.");}var media=source.getAttribute("media");if(!source.getAttribute("srcset")){continue;}if((!media||pf.matchesMedia(media))){var typeSupported=pf.verifyTypeSupport(source);if(typeSupported===true){match=source;break;}else{if(typeSupported==="pending"){return false;}}}}return match;};function picturefill(opt){var elements,element,parent,firstMatch,candidates,options=opt||{};elements=options.elements||pf.getAllElements();for(var i=0,plen=elements.length;i<plen;i++){element=elements[i];parent=element.parentNode;firstMatch=undefined;candidates=undefined;if(element.nodeName.toUpperCase()!=="IMG"){continue;}if(!element[pf.ns]){element[pf.ns]={};}if(!options.reevaluate&&element[pf.ns].evaluated){continue;}if(parent.nodeName.toUpperCase()==="PICTURE"){pf.removeVideoShim(parent);firstMatch=pf.getMatch(element,parent);if(firstMatch===false){continue;}}else{firstMatch=undefined;}if(parent.nodeName.toUpperCase()==="PICTURE"||(element.srcset&&!pf.srcsetSupported)||(!pf.sizesSupported&&(element.srcset&&element.srcset.indexOf("w")>-1))){pf.dodgeSrcset(element);}if(firstMatch){candidates=pf.processSourceSet(firstMatch);pf.applyBestCandidate(candidates,element);}else{candidates=pf.processSourceSet(element);if(element.srcset===undefined||element[pf.ns].srcset){pf.applyBestCandidate(candidates,element);}}element[pf.ns].evaluated=true;}}function runPicturefill(){picturefill();var intervalId=setInterval(function(){picturefill();if(/^loaded|^i|^c/.test(doc.readyState)){clearInterval(intervalId);return;}},250);function checkResize(){var resizeThrottle;if(!w._picturefillWorking){w._picturefillWorking=true;w.clearTimeout(resizeThrottle);resizeThrottle=w.setTimeout(function(){picturefill({reevaluate:true});w._picturefillWorking=false;},60);}}if(w.addEventListener){w.addEventListener("resize",checkResize,false);}else{if(w.attachEvent){w.attachEvent("onresize",checkResize);}}}runPicturefill();picturefill._=pf;if(typeof module==="object"&&typeof module.exports==="object"){module.exports=picturefill;}else{if(typeof define==="function"&&define.amd){define(function(){return picturefill;});}else{if(typeof w==="object"){w.picturefill=picturefill;}}}})(this,this.document,new this.Image());
   /* Ende picturefill */
   /* Start gsb_shrinkheader */
   /**
 * @module gsb_shrinkheader
 * @class $.gsb.shrinkheader
 * @version 1.1.0-zdm
 * @author boelenbe / pespeloe / amenkovi
 *
 * @desc GSB-Plugin zur Darstellung eines fixierten und verkleinerten Seitenkopfes.
 *
 * @requires  jQuery >= 1.11.1
 * @requires  gsb_responsiveListener
 *
 * @option
 * changeImage: Angabe von Optionen zum Austauschen von Logos im Shrinkheader (optional (default: leer))
 *
 * */
;(function($) {
 if (!$.gsb) {
  $.gsb = {};
 };
 $.gsb.shrinkheader = function(el, options) {
  // To avoid scope issues, use 'base' instead of 'this'
  // to reference this class from internal events and functions.
    var base = this;
    base.$el = $(el);
    base.el = el;
  // Add a reverse reference to the DOM object
  base.$el.data("gsb.shrinkheader", base);
  base.init = function() {
   //base.myFunctionParam = myFunctionParam;
      base.options = $.extend({}, $.gsb.shrinkheader.defaultOptions, options);
      base.$el.gsb_responsiveListener(base);
  };
    /**
     * @method initScrollEvent
     * @memberOf $.gsb.shrinkheader
     * @desc Initialisierung des Scripts bzw. des Scroll-Events
     */
    $.gsb.shrinkheader.prototype.initScrollEvent = function(){
      if(!$.isEmptyObject(base.options.changeImage)){
        base.imageArray = [];
        base.shrinkLogo = base.$el.find('.logo:eq(0)').clone().removeClass('large-3 logo').addClass('large-2 addShrinkLogo');
        base.shrinkLogo.insertBefore(base.$el.find('#navPrimary h2'));
        base.options.changeImage.img = base.shrinkLogo.find('img');
        base.setImageArray();
      }
      $(window).on('scroll.shrinkheader', function() {
        var distanceY = window.pageYOffset;
        if(distanceY > base.options.shrinkOn) {
          if(!base.$el.hasClass(base.options.shrinkCssClass)){
            // Header verkleinern
            if(base.options.changeImage && base.imageArray){
              base.changeImage(base.options.changeImage.from, base.options.changeImage.to);
            }
            var headerHeightBeforeShrink = $('#header').height();
            base.$el.addClass(base.options.shrinkCssClass);
            $('#wrapperDivisions').addClass(base.options.shrinkCssClass);
            $('#wrapperDivisions.smaller').css('margin-top', headerHeightBeforeShrink * 0.75);
            if(base.shrinkLogo && base.shrinkLogo.length) {
              base.shrinkLogo.show();
            }
          }
        } else {
          // Header Ausgangszustand
          if(base.$el.hasClass(base.options.shrinkCssClass)){
            if(base.options.changeImage && base.imageArray){
              base.changeImage(base.options.changeImage.to, base.options.changeImage.from);
            }
            base.$el.removeClass(base.options.shrinkCssClass);
            $('#wrapperDivisions.smaller').css('margin-top', '');
            $('#wrapperDivisions.smaller').removeClass(base.options.shrinkCssClass);
            if(base.shrinkLogo && base.shrinkLogo.length) {
              base.shrinkLogo.hide();
            }
          }
        }
      });
    }
    /**
     * @method removeScrollEvent
     * @memberOf $.gsb.shrinkheader
     * @desc Entfernt das Scroll-Event scroll.shrinkheader
     */
    $.gsb.shrinkheader.prototype.removeScrollEvent = function(){
      if(base.$el.hasClass(base.options.shrinkCssClass)){
        base.$el.removeClass(base.options.shrinkCssClass);
      }
      $(window).off('scroll.shrinkheader');
    }
    /**
     * @method changeImage
     * @memberOf $.gsb.shrinkheader
     * @param from {string} was gesucht werden soll
     * @param to {string} wodurch es ersetzt werden soll
     * @desc tauscht blob=x durch blob=y
     */
    $.gsb.shrinkheader.prototype.changeImage = function(from, to){
        var preparedFrom = from.replace('?__blob=normal', '');
        var preparedTo = to.replace('?__blob=normal', '');
      $.each(base.imageArray, function(){
        var image = base.$el.find(this);
        $.each(image, function(){
          $(this).prop("src", $(this).prop("src").replace(preparedFrom, preparedTo));
        });
      })
    }
    /**
     * @method setImageArray
     * @memberOf $.gsb.shrinkheader
     * @desc erstellt einen Array aus Bildern, die getauscht werden sollen
     */
    $.gsb.shrinkheader.prototype.setImageArray = function(){
      // Wurden mehrere Bilder selektiert? - Array von Selektoren
      if($.isArray(base.options.changeImage.img)){
        $.each(base.options.changeImage.img, function(){
          base.imageArray.push(base.$el.find(this.toString()));
        });
      } else{
        base.imageArray.push(base.$el.find(base.options.changeImage.img));
      }
    }
  // Run initializer
  base.init();
 };
  /**
   * Represents a book.
   * @property {object}   options                                       - Default Options des Scripts
   * @property {int} [options.shrinkOn = 300]                           - Wert in Pixel, wann der Header verkleinert werden soll.
   * @property {string} [options.shrinkCssClass = smaller]              - Name der CSS-Klasse, die gesetzt werden soll.
   * @property {Object} [options.changeImage = {}]                      - Properties fuer changeImage
   * @property {string | Object[]} [options.changeImage.img = no value] - ein Selektor > string, mehrere Selektoren > Object[string,string]
   * @property {string} [options.changeImage.from = no value]           - was soll gesucht werden
   * @property {string} [options.changeImage.to = no value]             - durch was soll ersetzt werden
   */
 $.gsb.shrinkheader.defaultOptions = {
    shrinkOn : 300,
    shrinkCssClass : 'smaller',
    respondToEvents: true,
    changeImage: {},
    onRefresh: function () {
      this.initScrollEvent();
    },
    responsive: [
      {
        breakpoint: 1024,
        onRefresh: function () {
          this.removeScrollEvent();
        }
      }
    ]
 };
 $.fn.gsb_shrinkheader = function(options) {
  return this.each(function() {(new $.gsb.shrinkheader(this, options));
  });
 };
})(jQuery);
   /* Ende gsb_shrinkheader */
   /* Start gsb_autosuggest */
   /*!
 * GSB-Plugin zur Darstellung von Suchvorschlaegen
 *
 * Author: gsb
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.AutoSuggestBoxCounter=1;$.gsb.AutoSuggest=function(el,actionUrl,options){var base=this;base.$el=$(el);base.el=el;base.boxNumber=$.gsb.AutoSuggestBoxCounter++;base.$input=base.$el.find("input[type=text]:first");base.$submit=base.$el.find("input[type=submit]:first");if(base.$submit.length==0){base.$submit=base.$el.find("input[type=image]:first");}base.$el.data("gsb.AutoSuggest",base);base.reqQuery=false;base.queries=[];base.enterPos=-1;base.debug=false;base.init=function(){base.actionUrl=actionUrl;var positions=base.$input.position();base.$box=$('<p id="searchAutoSuggestBox'+base.boxNumber+'" class="searchAutoSuggestBox"></p>');base.$box.css({"width":(base.$input.outerWidth()-2)+"px","top":(positions.top+base.$input.outerHeight())+"px","left":positions.left+"px"});base.$box.hide();$(window).resize(function(){positions=base.$input.position();base.$box.css({"top":(positions.top+base.$input.outerHeight())+"px","left":positions.left+"px"});});base.$input.attr("autocomplete","off");base.$input.get(0).onkeydown=function(e){e=e||window.event;if(e){var code=(e.keyCode!=undefined&&null!=e.keyCode?e.keyCode:e.which);if(code==13){return base.keydownEnter();}else{if(code==38){return base.keydownUp();}else{if(code==40){return base.keydownDown();}}}}};base.$input.get(0).onkeyup=function(e){var go=false;e=e||window.event;if(e){var code=(e.keyCode!=undefined&&null!=e.keyCode?e.keyCode:e.which);if(code!=13&&code!=38&&code!=40){go=true;}}else{go=true;}if(go){if(base.$input.val().length>=2){base.query(base.$input.val());}else{base.showSuggestionBox(false);}}};base.$input.after(base.$box);base.$box=base.$el.find("#searchAutoSuggestBox"+base.boxNumber);base.$input.bind("blur",function(){base.showSuggestionBox(false);});};base.query=function(value){base.reqQuery=value;if(typeof base.queries[base.reqQuery]=="undefined"){if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log('AutoSuggest-AJAX-Anfrage für "'+base.reqQuery+'"');}$.ajax({url:base.actionUrl,dataType:"json",data:{"userQuery":value},error:function(jqXHR,textStatus,errorThrown){if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log("AJAX Fehler beim Abruf der JSON-Resource "+base.actionUrl+": "+textStatus+" :: "+errorThrown);}},success:function(data){base.queries[value]=data.suggestions;if(base.reqQuery==value){if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log('AJAX-Antwort für "'+base.reqQuery+'" wird nun verarbeitet...');}if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log(data);}base.showSuggestions(data.suggestions);}else{if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log('AJAX-Antwort für "'+base.reqQuery+'" ist nicht mehr relevant...');}}}});}else{base.showSuggestions(base.queries[base.reqQuery]);}};base.showSuggestions=function(suggestions){base.$box.empty();var count=0;for(var s in suggestions){if(suggestions.hasOwnProperty(s)&&suggestions[s].name){var $link=$('<a href="#"></a>');$link.html(suggestions[s].name);$link.click(function(e){e.preventDefault();base.$input.val($(this).text());base.$submit.click();});$link.appendTo(base.$box);count++;}}if(count>0){base.showSuggestionBox(true);}else{base.showSuggestionBox(false);}};base.showSuggestionBox=function(show){base.enterPos=-1;if(show){base.$box.fadeIn("fast");}else{base.$box.fadeOut("fast");}};base.keydownDown=function(){if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log("KEYDOWN down");}if(base.enterPos<0&&base.$box.find("a").length>0){base.$box.find("a").removeClass("active");base.enterPos=0;$(base.$box.find("a")[base.enterPos]).addClass("active");}else{if(base.enterPos<base.$box.find("a").length-1){base.$box.find("a").removeClass("active");base.enterPos++;$(base.$box.find("a")[base.enterPos]).addClass("active");}}};base.keydownUp=function(){if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log("KEYDOWN up");}if(base.enterPos<0&&base.$box.find("a").length>0){base.$box.find("a").removeClass("active");base.enterPos=base.$box.find("a").length-1;$(base.$box.find("a")[base.enterPos]).addClass("active");}else{if(base.enterPos>0){base.$box.find("a").removeClass("active");base.enterPos--;$(base.$box.find("a")[base.enterPos]).addClass("active");}}};base.keydownEnter=function(){if(base.debug&&typeof console!="undefined"&&console&&console.log){console.log("KEYDOWN enter");}if(base.enterPos>=0&&base.$box.find("a").length>base.enterPos){base.$box.find("a")[base.enterPos].click();return false;}};base.init();};$.fn.gsb_AutoSuggest=function(actionUrl,options){return this.each(function(){new $.gsb.AutoSuggest(this,actionUrl,options);});};})(jQuery);
   /* Ende gsb_autosuggest */
   /* Start gsb_banner */
   (function($){if(!$.gsb){$.gsb={};}$.gsb.banner=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.banner",base);base.init=function(){base.options=$.extend({},$.gsb.banner.defaultOptions,options);base.$el.gsb_responsiveListener(base);};$.gsb.banner.prototype.createBanner=function(){var bannerHeight=base.$el.outerHeight()+base.options.topOffset;var detachedElement=base.$el.detach();$("ul.navSkip").after(detachedElement);if(!$.cookie("gsbbanner")){base.$el.animate({bottom:0},base.options.animationSpeed);$.cookie("gsbbanner","visible",{expires:base.options.cookieLifeTimeInDaysVisible,path:"/"});}else{if($.cookie("gsbbanner")==="visible"){base.$el.css("bottom",0);}else{base.$el.css("bottom",-bannerHeight);}}base.$el.find(".close").click(function(e){e.preventDefault();e.stopPropagation();base.$el.animate({bottom:-bannerHeight},base.options.animationSpeed,function(){base.$el.css("display","none");});$.cookie("gsbbanner","closed",{expires:base.options.cookieLifeTimeInDaysClosed,path:"/"});});};base.init();};$.gsb.banner.defaultOptions={animationSpeed:1800,topOffset:20,cookieLifeTimeInDaysVisible:2,cookieLifeTimeInDaysClosed:1,respondToEvents:true,onRefresh:function(){this.createBanner();}};$.fn.gsb_banner=function(options){return this.each(function(){(new $.gsb.banner(this,options));});};})(jQuery);
   /* Ende gsb_banner */
   /* Start gsb_calendar */
   /*!
 * GSB-Plugin zur Animation und zum Nachladen von Kalender
 *
 * Author: pespeloer
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.calendar=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.calendar",base);base.init=function(){base.options=$.extend({},$.gsb.calendar.defaultOptions,options);options=base.options;base.$el.on("click touchstart","td > a",function(e){e.preventDefault();e.stopPropagation();if($(this).parent().hasClass(options.detailsOpenedClass)){$(this).parent().removeClass(options.detailsOpenedClass).addClass(options.detailsClosedClass);}else{base.$el.find("."+options.detailsOpenedClass).removeClass(options.detailsOpenedClass);$(this).parent().removeClass(options.detailsClosedClass).addClass(options.detailsOpenedClass);}});base.$el.on("click touchstart",options.prevButton,function(e){e.preventDefault();e.stopPropagation();base.loadCalendar($(this).data("href"));});base.$el.on("click touchstart",options.nextButton,function(e){e.preventDefault();e.stopPropagation();base.loadCalendar($(this).data("href"));});};base.loadCalendar=function(href){if(typeof options.beforeInit==="function"){base.options.beforeInit.call($(this),base.$el);}base.$el.fadeTo(options.duration,0,function(){base.$el.load(href,function(){base.$el.fadeTo(options.duration,1,function(){base.addA11y();if(typeof options.afterInit==="function"){base.options.afterInit.call($(this),base.$el);}});});});};base.addA11y=function(elem){elem=elem||base.$el;var focusElements=elem.find("input,textarea,select,a,button").not(":hidden");focusElements[0].focus();};base.init();};$.gsb.calendar.defaultOptions={nextButton:".next > a",prevButton:".prev > a",fadeDuration:400,detailsOpenedClass:"opened",detailsClosedClass:"closed",beforeInit:undefined,afterInit:undefined};$.fn.gsb_calendar=function(options){return this.each(function(){(new $.gsb.calendar(this,options));});};})(jQuery);
   /* Ende gsb_calendar */
   /* Start gsb_clearfields */
   /*!
 * GSB-Plugin zum autom. Leeren von Text-Input-Feldern
 *
 * Author: sbaecker
 */
(function($){
jQuery.fn.attachToField = function (id, defaultValue){
    var field = $('#'+id);
    var input_text = field.attr('value');
    return jQuery(field).bind('focus',function(){
       if( input_text == defaultValue ) {
          field.attr('value','');
        }
      }).bind('blur',function(){
        if(field.attr('value')==''){
          field.attr('value',input_text);
        }
        input_text = field.attr('value');
      });
};
jQuery.gsb_clearfields = function(){
// attach events to search form input
// HINWEIS: Bitte folgenden zeilen im GSB einkommentieren
      jQuery('#f10950d16094').attachToField('f10950d16094','');
      jQuery('#f16096d16094').attachToField('f16096d16094','');
      jQuery('#f16098d16100').attachToField('f16098d16100','Ihre E-Mail-Adresse');
      jQuery('#f16102d16104').attachToField('f16102d16104','Ihre E-Mail-Adresse');
      jQuery('#f16102d16106').attachToField('f16102d16106','search item');
      jQuery('#f16102d16094').attachToField('f16102d16094','');
      jQuery('#f16102d16108').attachToField('f16102d16108','');
return;
};
})(jQuery);
   /* Ende gsb_clearfields */
   /* Start gsb_lightbox */
   /*!
 * GSB-Wrapper-Plugin fuer magnific popup
 *
 * s. auch http://dimsemenov.com/plugins/magnific-popup/
 *
 * Weitere Abhaengigkeit besteht zu hammer.js und jquery.hammer.js fuer's Swipen
 *
 * Author: rkrusenb
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.lightbox=function(el,userOptions){var lightbox=this,$el=lightbox.$el=$(el),options,disabled;function openerClickHandler(ev){if(!disabled){ev&&typeof ev.preventDefault=="function"&&ev.preventDefault();$el.data("magnificPopup").index=$(this).data("gsb.lightboxopener").index;}$el.magnificPopup("open");}function checkArrowState(mfp){if(!options.cycle&&mfp.arrowLeft&&mfp.arrowRight){if(mfp.index==0){mfp.arrowLeft.find("img").attr("src",options.imagePrevInactive);}else{mfp.arrowLeft.find("img").attr("src",options.imagePrev);}if(mfp.index==mfp.items.length-1){mfp.arrowRight.find("img").attr("src",options.imageNextInactive);}else{mfp.arrowRight.find("img").attr("src",options.imageNext);}}}function makeGallery(){options.reallyMagnificOptions.callbacks.open=function(){var mfp=this;mfp.wrap.find(".mfp-close").appendTo(mfp.contentContainer);if(options.imagePrev){$(".mfp-arrow-left").append('<img alt="'+options.reallyMagnificOptions.gallery.tPrev+'" src="'+options.imagePrev+'">');}if(options.imageNext){$(".mfp-arrow-right").append('<img alt="'+options.reallyMagnificOptions.gallery.tNext+'" src="'+options.imageNext+'">');}mfp.prev=function(){if(options.cycle||mfp.index!=0){$.magnificPopup.proto.prev.call(this);}};mfp.next=function(){if(options.cycle||mfp.index!=mfp.items.length-1){$.magnificPopup.proto.next.call(this);}};checkArrowState(this);if(typeof mfp.contentContainer.hammer=="function"&&!mfp.wrap.data("gsb.swipe")){mfp.contentContainer.hammer({drag_block_horizontal:true,stop_browser_behavior:{touchAction:"auto"},swipe_velocity:0.3}).on("swiperight",function(){mfp.prev();}).on("swipeleft",function(){mfp.next();}).data("gsb.swipe",{});}};options.reallyMagnificOptions.callbacks.change=function(){checkArrowState(this);};$.extend(options.reallyMagnificOptions,{items:$.map($(this).find(options.element),function(item){return{src:$(item).clone().find(".loupe").remove().end().removeClass("slick-slide").attr("style",null),type:"inline"};})},(userOptions||{}).magnificOptions);$el.magnificPopup(options.reallyMagnificOptions).unbind("click");}lightbox.el=el;lightbox.$el.data("gsb.lightbox",lightbox);lightbox.init=function(){var url,closeText,defer;options=lightbox.options=$.extend({},$.gsb.lightbox.defaultOptions,userOptions);options.reallyMagnificOptions=$.gsb.lightbox.defaultOptions.magnificOptions;closeText=options.magnificOptions.tClose;$.extend(options.reallyMagnificOptions,{disableOn:function(){return !disabled;},closeMarkup:'<button class="mfp-close">%title%</button>',});if(options.lightboxType=="single"){url=$el.data("lightbox-href");if(url){options.reallyMagnificOptions.type="ajax";options.reallyMagnificOptions.ajax.settings.url=url;}else{if($el.parents(options.element).length){options.reallyMagnificOptions.type="image";}}$.extend(options.reallyMagnificOptions,(userOptions||{}).magnificOptions);$el.magnificPopup(options.reallyMagnificOptions);}else{if(options.lightboxType=="multiple"){url=$el.data("lightbox-href");if(url){$("<div />").load(url,makeGallery);}else{$el.on("init.slideshow.gsb",makeGallery);}$el.on("init.slideshow.gsb",function(){$el.find(".loupe").each(function(idx){var opener=$(this),openerData=opener.data("gsb.lightboxopener")||{};openerData.index=idx;opener.data("gsb.lightboxopener",openerData);opener.click(openerClickHandler).keydown(function(e){if(e.which==23){$(this).click();}});});});$.extend(true,options.reallyMagnificOptions,{gallery:{enabled:true}});$.extend(options.reallyMagnificOptions,(userOptions||{}).magnificOptions);}}};lightbox.closeLightbox=function(){$el.magnificPopup("close");};lightbox.disable=function(){disabled=true;};lightbox.enable=function(){disabled=false;};lightbox.addA11y=function(){};lightbox.init();};$.gsb.lightbox.defaultOptions={lightboxType:"single",cycle:false,element:".picture",imagePrev:"",imageNext:"",magnificOptions:{tClose:typeof CLOSE=="undefined"?"Schließen (Esc)":CLOSE,gallery:{tPrev:typeof PREV=="undefined"?"Vorheriges (linke Pfeiltaste)":PREV,tNext:typeof NEXT=="undefined"?"Nächstes (rechte Pfeiltaste)":NEXT,tCounter:typeof LIGHTBOX_X_OF_Y=="undefined"?"%curr% von %total%":LIGHTBOX_X_OF_Y},type:"ajax",closeOnContentClick:false,closeBtnInside:false,image:{verticalFit:true,titleSrc:function(){return $(this.currItem&&this.currItem.el).find("img").attr("alt");}},ajax:{settings:{}},callbacks:{afterChange:function(){$(this.content).find("video, audio").each(function(_,el){var data=$(el).data("gsb.Multimedia");if(!data){data=$(el).gsb_Multimedia().data("gsb.Multimedia");}if(!data.workaround&&typeof data.triggerRefresh=="function"){data.workaround=true;data.triggerRefresh();}});},buildControls:function(){if(this.contentContainer&&this.arrowLeft&&this.arrowRight){this.contentContainer.append(this.arrowLeft.add(this.arrowRight));}},open:function(){if(this.wrap&&this.wrap.find){this.wrap.find(".mfp-close").appendTo(this.contentContainer);}}}}};$.fn.gsb_lightbox=function(options){return this.each(function(){(new $.gsb.lightbox(this,options));});};})(jQuery);
   /* Ende gsb_lightbox */
   /* Start gsb_navigation */
   /*!
 * @name Navigation
 * @author @pespeloe
 * Licensed under the MIT license
 *
 * @requires jQuery >= 1.11.1, gsb_responsiveListener
 * */
(function($){if(!$.gsb){$.gsb={};}$.gsb.navigation=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.navigation",base);base.init=function(){base.options=$.extend({},$.gsb.navigation.defaultOptions,options);base.delegateTo="#"+base.$el.attr("id")+base.options.hoverItem;base.$el.gsb_responsiveListener(base);};base._largeNavigation=function(){$(base.delegateTo).attr("tabindex","0").focus(function(e){$(this).trigger("mouseenter").find("a").last().blur(function(){$(this).parent().parentsUntil("li").trigger("mouseleave");});});$(base.delegateTo).on("mouseenter mouseleave",function(e){e.stopImmediatePropagation();var $box=$(this).find(base.options.displayItem).finish(),$root=$(this);if(e.type==="mouseenter"){if(!$(base.delegateTo).find(e.relatedTarget).length){$box.addClass("on").stop().slideDown({duration:base.options.animSpeed}).css({display:"none",opacity:1}).fadeIn({duration:base.options.animSpeed,queue:false});}else{$box.addClass("on").css({opacity:1}).show();}$root.addClass("hovered");}else{if(!$(base.delegateTo).find(e.relatedTarget).length){$box.removeClass("on").slideUp({duration:base.options.animSpeed}).fadeOut({duration:base.options.animSpeed,queue:false});}else{$box.removeClass("on").hide();}$root.removeClass("hovered");}});base.$el.find(base.options.navTouchElement).on("touchstart",function(e){var $box=$(this).find(base.options.displayItem).stop(),$root=$(this).children("div");if($(this).parent().parent().hasClass("hovered")){e.preventDefault();}});},base.init();};$.gsb.navigation.defaultOptions={navTouchElement:"h3 > a",displayItem:".menu-box",hoverItem:"> ul > li",animSpeed:500,respondToEvents:true,onRefresh:function(){this._largeNavigation();},responsive:[{breakpoint:1024,onRefresh:function(){}}]};$.fn.gsb_navigation=function(options){return this.each(function(){(new $.gsb.navigation(this,options));});};})(jQuery);
   /* Ende gsb_navigation */
   /* Start gsb_serviceborder */
   /*!
 * GSB-Plugin fuer Serviceleiste unter der Buehne
 *
 * Dabei wird die folgende HTML-Struktur erwartet
 *
 * <div id="service-border">
 *   <div id="tab">
 *     <ul class="tab">
 *       <li class="t-entry-1">
 *         <a class="navServiceNewsletter" href="...">Newsletter</a>
 *       </li>
 *       ...
 *     </ul>
 *   </div>
 *   <div id="section-1" class="container">
 *     ...
 *   </div>
 *   ...
 * </div>
 *
 * Aufruf:
 *  $("#navContent").gsb_Serviceborder();
 *
 * Author: sbaecker
 *
 * @requires jQuery >= 1.11.1, gsb_responsiveListener
 *
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.Serviceborder=function(el,options){var base=this,closeclick;base.$el=$(el);base.el=el;base.$el.data("gsb.Serviceborder",base);base.init=function(){base.options=$.extend({},$.gsb.Serviceborder.defaultOptions,options);base.$el.gsb_responsiveListener(base);};base._configuration=function(){closeclick=false;if(!base.$el.length){return;}if(base.$el.find(base.options.navnodes+" span.aural").length==0){base.$el.find(base.options.navnodes).append('<span class="aural"> '+base.options.show_more_text+"</span>");}base.$el.find(base.options.navnodes).each(function(){$(this).on("click",function(e){e.preventDefault();e.stopPropagation();base.showTab($(this));return false;});});base.tabActive=false;base.tabToShow=false;};base.showTab=function(obj,close){if(typeof obj!="undefined"&&obj){var regExpMatch=obj.parent("li:first").attr("class").match(/^.*\w+-(\w+)-(\d+)( .*)?$/);base.tabToShow=regExpMatch[2];}else{closeclick=true;base.tabToShow=false;}if(base.tabActive==base.tabToShow){var tabActiveId=base.options.container+base.tabActive;var $tabActive=base.$el.find(tabActiveId);$tabActive.stop(true,true).fadeOut({duration:base.options.fadeDuration,easing:"linear",queue:false}).slideUp({duration:base.options.slideDuration,easing:"linear"});base.tabActive=false;base.$el.find(" ul > li .aural").text(" "+base.options.show_more_text);base.$el.find(" ul > li").removeClass("active");}else{if(base.tabToShow!=base.tabActive&&false!==base.tabToShow){var tabToShowId=base.options.container+base.tabToShow;var $tabToShow=base.$el.find(tabToShowId);if(!$tabToShow.length){alert("Tab "+tabToShowId+" nicht vorhanden...");return;}if(!$tabToShow.find(".containerinner .close").length){var $closeButton;if(base.options.close_button_markup_mode==="image"){$closeButton=$('<p><button tabindex="0" class="close" title="'+base.options.close+'" ><img src="'+image_url_close+'" alt="'+base.options.close+'" /></button></p>');}else{$closeButton=$('<p><button tabindex="0" class="close">'+base.options.close+"</button></p>");}$closeButton.appendTo(tabToShowId+" > .containerinner:first").on("click",function(){base.showTab();return false;});}base.$el.find("> div .close").unbind("click").on("click",function(e){e.preventDefault();e.stopPropagation();var tabActiveId=base.options.container+base.tabActive;var $tabActive=base.$el.find(tabActiveId);$tabToShow.stop(true,true).fadeOut({duration:base.options.fadeDuration,easing:"linear",queue:false}).slideUp({duration:base.options.slideDuration,easing:"linear"});obj.focus();base.tabActive=false;base.$el.find(" ul > li span.aural").text(" "+base.options.show_more_text);base.$el.find("ul > li").removeClass("active");});if(false!==base.tabActive){var tabActiveId=base.options.container+base.tabActive;var $tabActive=base.$el.find(tabActiveId);$tabActive.slideUp();}else{$tabToShow.stop(true,true).fadeIn({duration:base.options.fadeDuration,easing:"linear",queue:false}).css("display","none").slideDown({duration:base.options.slideDuration,easing:"linear"});}$tabToShow.slideDown();base.tabActive=base.tabToShow;base.$el.find(" ul > li span.aural").text(" "+base.options.show_more_text);base.$el.find(" ul > li").removeClass("active");obj.parent(":first").addClass("active");base.$el.find(" ul > li.active .aural").text(" "+base.options.show_less_text);}else{if(false===base.tabToShow&&false!==base.tabActive){var tabActiveId=base.options.container+base.tabActive;var $tabActive=base.$el.find(tabActiveId);if(closeclick=true){$tabActive.slideUp("fast");closeclick=false;}else{$tabActive.hide();}base.tabActive=false;base.$el.find(" ul > li span.aural").text(" "+base.options.show_more_text);base.$el.find(" ul > li").removeClass("active");}}}};base._uninit=function(){var closeLink=base.$el.find("a.close");if(closeLink.length>0){if(closeLink.closest("p").length>0){closeLink.closest("p").off();}else{closeLink.off();}}if(base.$el.find("div .close").length>0){base.$el.find("div .close").off();}if(base.$el.find(base.options.navnodes).length>0){base.$el.find(base.options.navnodes).off();}base.$el.find(".container").hide();base.$el.find(" ul > li span.aural").text(" "+base.options.show_more_text);base.$el.find(">ul>li").removeClass("active");};base.init();};$.gsb.Serviceborder.defaultOptions={close:typeof(CLOSE)=="undefined"?"Schließen":CLOSE,close_button_markup_mode:"image",navnodes:" > ul > li > a",container:"#section-",show_more_text:typeof(SHOW_MORE)=="undefined"?"Mehr anzeigen":SHOW_MORE,show_less_text:typeof(SHOW_LESS)=="undefined"?"Weniger anzeigen":SHOW_LESS,slideDuration:400,fadeDuration:600,respondToEvents:true,onRefresh:function(){this._configuration();},responsive:[{breakpoint:600,onRefresh:function(){this._uninit();}}]};$.fn.gsb_Serviceborder=function(options){return this.each(function(){(new $.gsb.Serviceborder(this,options));});};})(jQuery);
   /* Ende gsb_serviceborder */
   /* Start gsb_print */
   /*!
 * GSB-Plugin zum autom. Leeren von Text-Input-Feldern
 *
 * Das Addon fügt einen Link "Seite drucken" in eine vorhandene Liste ein
 * Aufruf: $("#element").gsb_init_printlink();
 * #element muss dabei ein <ul> sein, damit das html valide bleibt.
 *
 * Author: sbaecker
 *
 * @requires jQuery >= 1.11.1, gsb_responsiveListener
 *
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.printlink=function(el,options){var base=this;var printInited=false;base.$el=$(el);base.el=el;base.$el.data("gsb.printlink",base);base.init=function(){base.options=$.extend({},$.gsb.printlink.defaultOptions,options);base.$el.gsb_responsiveListener(base);};base._printlink=function(){if(!base.$el.length){return;}if($("#"+base.options.printlink_id).length==0){pattern=$('<li id="'+base.options.printlink_id+'"><a href="#" title="'+base.options.tooltip+'">'+base.options.page_text+"</a></li>");base.$el.prepend(pattern);}base.$el.parent().find("#"+base.options.printlink_id+" a").on("click",function(e){e.preventDefault();e.stopPropagation();window.print();});};base._uninit=function(){if(base.$el.find("#"+base.options.printlink_id)){base.$el.find("#"+base.options.printlink_id).off().remove();}};base.init();};$.gsb.printlink.defaultOptions={tooltip:typeof(PRINT_TOOLTIP)=="undefined"?"Drucken (öffnet Dialog)":PRINT_TOOLTIP,page_text:typeof(PRINT_PAGE_TEXT)=="undefined"?"drucken":PRINT_PAGE_TEXT,printlink_id:"navFunctionsPrint",respondToEvents:true,onRefresh:function(){this._printlink();},responsive:[{breakpoint:1024,onRefresh:function(){this._uninit();}}]};$.fn.gsb_printlink=function(options){return this.each(function(){(new $.gsb.printlink(this,options));});};})(jQuery);
   /* Ende gsb_print */
   /* Start gsb_slideshow */
   /*!
 * GSB-Wrapper-Plugin fuer slick.js.
 *
 * s. auch http://kenwheeler.github.io/slick/
 *
 * Optionen:
 * nextButtonImage: Das Hintergrundbild fuer den aktiven weiter-Link der Navigation(default: null)
 * nextButtonInactiveImage: Das Hintergrundbild fuer den inaktiven weiter-Link der Navigation(default: null)
 * nextTitle: Das title-Attribut fuer den aktiven weiter-Link der Navigation
 * (default: SLIDER_NEXT bzw. 'Nächste Seite')
 * nextTitleInactive: Das title-Attribut fuer den inaktiven weiter-Link der Navigation
 * (default: SLIDER_NEXT_INACTIVE bzw. 'Keine nächste Seite')
 * prevButtonImage: Das Hintergrundbild fuer den aktiven zurueck-Link der Navigation(default: null)
 * prevButtonInactiveImage: Das Hintergrundbild fuer den aktiven zurueck-Link der Navigation(default: null)
 * prevTitle: Das title-Attribut fuer den aktiven zurueck-Link der Navigation
 * (default: SLIDER_BACK bzw. 'Vorherige Seite')
 * prevTitleInactive: Das title-Attribut fuer den aktiven zurueck-Link der Navigation
 * (default: SLIDER_BACK_INACTIVE bzw. 'Keine vorherige Seite')
 * playButtonImagePlaying: Das Hintergrundbild fuer den Playbutton im abspielenden Zustand(default: null)
 * playButtonImagePaused: Das Hintergrundbild fuer den Playbutton im pausierten Zustand(default: null)
 * playButtonAltTextPlaying: Das alt-Attribut fuer den Playbutton im abspielenden Zustand
 * (default: SLIDER_PLAY bzw. 'Animation starten')
 * playButtonAltTextPaused: Das alt-Attribut fuer den Playbutton im pausierten Zustand
 * (default: SLIDER_PAUSE bzw. 'Animation stoppen')
 * slickOptions: native Optionen fuer das Slick Plugin(default: null, siehe Doku oben)
 * slickOptions.gsb.playButton: Soll ein Playbutton angelegt werden(default: true), an dieser Stelle
 * hinterlegt, um responsiven Reinitialisierungmechanismus von Slick zu nutzen
 *
 * Author: anuebel
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.Slideshow=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.Slideshow",base);base.defaultSlickOptions={slide:".slide",fade:true,infinite:false,draggable:false,adaptiveHeight:false,onBeforeChange:function(slick){slick.$slides.find("video, object").show();},onAfterChange:function(slick){base.updateNavigation(slick);base.updateTabindex(slick);},onInit:function(slick){var slideshowPluginInstance=slick.$slider.data("gsb.Slideshow");var onSlickInit=slideshowPluginInstance.options.onSlickInit;if(typeof onSlickInit=="function"){onSlickInit.call(slick,slick);}base.updateNavigation(slick);base.initMultimedia();base.updateTabindex(slick);base.$el.trigger("init.slideshow.gsb");},gsb:{playButton:true}};base.initMultimedia=function initMultimedia(){if(typeof $.fn.gsb_Multimedia=="function"){base.$el.find("video, audio").gsb_Multimedia();}};base.updateTabindex=function updateTabindex(slick){var focusableItems="a, [tabindex], textarea, input, select, button, video, object",buggedItems="video, object";slick.$slides.not(".slick-active").find(focusableItems).attr("tabindex",-1).end().find(buggedItems).hide();slick.$slides.filter(".slick-active").find(focusableItems).attr("tabindex",0).end().find(buggedItems).show();};base.updateNavigationArrow=function($arrow,activeImage,inactiveImage,activeArrowButtonTitle,inactiveArrowButtonTitle){if($arrow){var $arrowButton=$arrow.is("button")?$arrow:$arrow.find("button"),$arrowImage=$arrow.find("img");if($arrow.hasClass("slick-disabled")){$arrowButton.attr("disabled","");$arrowButton.attr("title",inactiveArrowButtonTitle);$arrowImage.attr("src",inactiveImage);$arrowImage.attr("alt","");}else{$arrowButton.removeAttr("disabled");$arrowButton.attr("title",activeArrowButtonTitle);$arrowImage.attr("src",activeImage);$arrowImage.attr("alt",activeArrowButtonTitle);}}};base.updateNavigation=function(slick){base.updateNavigationArrow(slick.$prevArrow,base.options.prevButtonImage,base.options.prevButtonInactiveImage,base.options.prevTitle,base.options.prevTitleInactive);base.updateNavigationArrow(slick.$nextArrow,base.options.nextButtonImage,base.options.nextButtonInactiveImage,base.options.nextTitle,base.options.nextTitleInactive);};base.handleLegacyOptions=function(slickOptions,options){$.each($.gsb.Slideshow.slickOptionsForLegacyOptions,function(legacyOption,mappedSlickOption){if(options.hasOwnProperty(legacyOption)){var savedValue=options[legacyOption];delete options[legacyOption];slickOptions[mappedSlickOption]=savedValue;}});};base.handleNavigationArrows=function(slickOptions){var navigationArrowOptions={nextArrow:base.$el.find(".next"),prevArrow:base.$el.find(".prev")};return $.extend({},navigationArrowOptions,slickOptions);};base.handlePlayButton=function(slickOptions){(function(existingOnInit){slickOptions.onInit=function(slick){existingOnInit.call(slick,slick);var initializedSlickOptions=slick.options;function shouldCreatePlayButton(){var playButtonImagesAreConfigured=base.options.playButtonImagePlaying!==null&&base.options.playButtonImagePaused!==null,createPlayButton=initializedSlickOptions.gsb.playButton;return createPlayButton&&playButtonImagesAreConfigured;}function preventPlay(){slick.paused=true;slick.autoPlayClear();}var $existingPlayButton=base.$el.parent().find(".navigation button.playbutton"),playButtonExists=$existingPlayButton.length;if(shouldCreatePlayButton()&&!playButtonExists){var slideshowStartsPlaying=initializedSlickOptions.autoplay,initialClassForPlayButton=slideshowStartsPlaying&&"playbutton-playing"||"playbutton-paused",initialTitleAttrForPlayButton=slideshowStartsPlaying&&base.options.playButtonAltTextPlaying||base.options.playButtonAltTextPaused,initialSrcAttrForPlayButtonImage=slideshowStartsPlaying&&base.options.playButtonImagePlaying||base.options.playButtonImagePaused,initialAltAttrForPlayButtonImage=slideshowStartsPlaying&&base.options.playButtonAltTextPlaying||base.options.playButtonAltTextPaused,$playButton=$("<button"+' class="playbutton '+initialClassForPlayButton+'"'+' title="'+initialTitleAttrForPlayButton+'">'+"<img"+' src="'+initialSrcAttrForPlayButtonImage+'"'+' alt="'+initialAltAttrForPlayButtonImage+'">'+"</button>"),playButtonImage=$playButton.find("img");base.$el.parent().find(".navigation").append($playButton);$playButton.click(function(){if($playButton.is(".playbutton-playing")){$playButton.removeClass().addClass("playbutton-paused").attr("title",base.options.playButtonAltTextPaused);playButtonImage.attr("src",base.options.playButtonImagePaused).attr("alt",base.options.playButtonAltTextPaused);base.$el.slickPause();slick.$list.on("mouseleave.slick",preventPlay);}else{$playButton.removeClass().addClass("playbutton-playing").attr("title",base.options.playButtonAltTextPlaying);playButtonImage.attr("src",base.options.playButtonImagePlaying).attr("alt",base.options.playButtonAltTextPlaying);base.$el.slickPlay();slick.$list.off("mouseleave.slick",preventPlay);}});}else{if(!shouldCreatePlayButton()&&playButtonExists){$existingPlayButton.remove();}}};}(slickOptions.onInit));return slickOptions;};base.init=function(){base.options=$.extend({},$.gsb.Slideshow.defaultOptions,options);var allElementsUrl=base.$el.data("href"),initSlick=function(){var rawSlickOptions=base.options.slickOptions;delete rawSlickOptions.onInit;var slickOptions=$.extend({},base.defaultSlickOptions,rawSlickOptions);base.handleLegacyOptions(slickOptions,base.options);slickOptions=base.handleNavigationArrows(slickOptions);slickOptions=base.handlePlayButton(slickOptions);base.$el.slick(slickOptions);},onReceiveAllElements=function(data){base.$el.html(data);initSlick();};if(allElementsUrl){$.get(allElementsUrl,onReceiveAllElements);}else{initSlick();}};base.init();};$.gsb.Slideshow.defaultOptions={nextButtonImage:null,nextButtonInactiveImage:null,nextTitle:typeof(NEXT)=="undefined"?"Nächste Seite":NEXT,nextTitleInactive:typeof(NEXT_INACTIVE)=="undefined"?"Keine nächste Seite":NEXT_INACTIVE,prevButtonImage:null,prevButtonInactiveImage:null,prevTitle:typeof(BACK)=="undefined"?"Vorherige Seite":BACK,prevTitleInactive:typeof(BACK_INACTIVE)=="undefined"?"Keine vorherige Seite":BACK_INACTIVE,playButtonImagePaused:null,playButtonImagePlaying:null,playButtonAltTextPaused:typeof(PLAY)=="undefined"?"Animation starten":PLAY,playButtonAltTextPlaying:typeof(PAUSE)=="undefined"?"Animation stoppen":PAUSE,paginationTitle:typeof(PAGE)=="undefined"?"Seite":PAGE,slickOptions:null};$.gsb.Slideshow.slickOptionsForLegacyOptions={containerElement:"slide",elementsToSlide:"slidesToShow",autoplay:"autoplay",autoSpeed:"autoplaySpeed",pagination:"dots",pauseOnHover:"pauseOnHover"};$.fn.gsb_Slideshow=function(options){return this.each(function(){return new $.gsb.Slideshow(this,options);});};}(jQuery));
   /* Ende gsb_slideshow */
   /* Start gsb_toggle */
   (function($){if(!$.gsb){$.gsb={};}$.gsb.toggle=function(el,options){var base=this;base.$el=$(el);base.el=el;this.base=base;base.$el.data("gsb.toggle",base);base.init=function(){this.base.options=$.extend({},$.gsb.toggle.defaultOptions,options);var _=this.base;if(_.options.accordion&&_.options.richTextAccordion){_.addRichtextContainer();}else{if(base.$el.data("href")){$.get(base.$el.data("href"),function(data){base.$el.html(data);base.$el.gsb_responsiveListener(_);});}else{base.$el.gsb_responsiveListener(_);}}};base.init();};$.gsb.toggle.defaultOptions={tab:true,tabControls:".tabs-list > li a",accordion:true,richTextAccordion:false,accordionCloseOther:true,accordionPanelHeight:false,autoplay:false,playButtonImagePaused:null,playButtonImagePlaying:null,playButtonAltTextPaused:typeof(PLAY)=="undefined"?"Animation starten":PLAY,playButtonAltTextPlaying:typeof(PAUSE)=="undefined"?"Animation stoppen":PAUSE,autoSpeed:"5000",playOnLoad:false,pauseOnHover:true,changeOnHover:false,allOpen:false,animateTab:false,respondToEvents:true,onRefresh:function(){this.initialize();this.refreshTab();},responsive:[{breakpoint:600,onRefresh:function(){this.initialize();this.autoplayStop();this.$el.off("mouseenter mouseleave");this.refreshAccordion();},autoplay:false}]};$.gsb.toggle.prototype.initialize=function(){var _=this.base;if(!_.$el.hasClass("gsb-toggle")){_.$el.addClass("gsb-toggle");_.addClasses();_.addClickfunction();}if(_.options.autoplay){_.autoplay();}};$.gsb.toggle.prototype.addClasses=function(){var _=this.base;if(_.options.tab===true){_.$el.find(_.options.tabControls).each(function(index){if(!$(this).is("a")||$(this).find(">a").length==0){$(this).prop("tabindex",0);}$(this).addClass("switch-panel"+index);var currElem=localStorage.getItem("elem");if(currElem!==null&&typeof currElem!=="undefined"){if(currElem===index.toString()){$(this).addClass("active-control");}else{$(this).addClass("inactive-control");}}else{if(index===0){$(this).addClass("active-control");}else{$(this).addClass("inactive-control");}}});_.$el.find(".tabs-container > div").each(function(index){$(this).addClass("switch-panel"+index);var currElem=localStorage.getItem("elem");if(currElem!==null&&typeof currElem!=="undefined"){if(currElem===index.toString()){$(this).addClass("panel-opened");}else{$(this).addClass("panel-closed");}}else{if(index===0){$(this).addClass("panel-opened");}else{$(this).addClass("panel-closed");}}});}if(_.options.accordion===true){var hasOpendElements=false;if(_.$el.find(".active-control").length>0){hasOpendElements=true;}_.$el.find(".heading").each(function(){if(!$(this).is("button")||!$(this).is("a")){if(_.options.accordionPanelHeight){var height=0;$(this).next().find(">").each(function(){height+=$(this).outerHeight(true);});$(this).next().height(height);}$(this).prop("tabindex",0);if(_.options.allOpen&&!_.options.tab){$(this).next().show();$(this).addClass("active-control");}else{if(hasOpendElements){if(!$(this).hasClass("active-control")){$(this).addClass("inactive-control");$(this).next().hide();}else{$(this).next().show();}}else{$(this).addClass("inactive-control");$(this).next().hide();}}}});}};$.gsb.toggle.prototype.addClickfunction=function(){var _=this.base;if(_.options.accordion===true){_.$el.on("click keydown",".tabs-container > .heading",function(e){if(e.type==="click"||e.which==13){e.preventDefault();e.stopPropagation();_.clickedElem=$(this);_.changeStateAccordion();}});}if(_.options.tab===true){if(_.options.changeOnHover&&!_.options.autoplay){_.$el.on("mouseenter focus touchend",_.options.tabControls,function(e){e.preventDefault();e.stopPropagation();_.clickedElem=$(this);_.changeStateTab($(this));});_.$el.on("click",_.options.tabControls,function(e){e.preventDefault();e.stopPropagation();});}else{_.$el.on("click keydown",_.options.tabControls,function(e){if(e.type==="click"||e.which==13){e.preventDefault();e.stopPropagation();_.clickedElem=$(this);_.changeStateTab($(this));}});}}};$.gsb.toggle.prototype.changeStateAccordion=function(){var _=this.base;if(_.clickedElem.hasClass("inactive-control")){if(_.options.accordionCloseOther){_.animationAccordion(_.clickedElem.parent().find("> .active-control").next(),"close");_.$el.find(".tabs-container > .active-control").removeClass("active-control").addClass("inactive-control");}_.clickedElem.attr("aria-expanded","true");_.clickedElem.attr("aria-selected","true");_.clickedElem.removeClass("inactive-control").addClass("active-control");_.animationAccordion(_.clickedElem.next(),"open");_.clickedElem.next().attr("aria-hidden","false");}else{_.clickedElem.attr("aria-expanded","false");_.clickedElem.attr("aria-selected","false");_.clickedElem.removeClass("active-control").addClass("inactive-control");_.animationAccordion(_.clickedElem.next(),"close");_.clickedElem.next().attr("aria-hidden","true");}};$.gsb.toggle.prototype.changeStateTab=function(elem){var _=this.base;if(_.clickedElem.hasClass("inactive-control")){var panelToClose=_.$el.find(".panel-opened");_.$el.find("[aria-hidden=false]").attr("aria-hidden","true");_.$el.find("[aria-selected=true]").attr("aria-selected","false");var ariaControl=_.clickedElem.attr("aria-controls");_.$el.find("#"+ariaControl).attr("aria-hidden","false");_.clickedElem.attr("aria-selected","true");panelToClose.removeClass("panel-opened").addClass("panel-closed");_.$el.find(".active-control").removeClass("active-control").addClass("inactive-control");_.clickedElem.removeClass("inactive-control");var panelToOpen=_.$el.find(".tabs-container > ."+_.getSwitchPanelClass(_.clickedElem));panelToOpen.removeClass("panel-closed").addClass("panel-opened");_.clickedElem.addClass("active-control");_.animationTab(panelToClose,panelToOpen);if(_.$el.closest(".cardwrapper").length>0){_.$el.find(_.options.tabControls).each(function(index){if($(this).attr("class")===$(elem).attr("class")){localStorage.setItem("elem",index);}});}}};$.gsb.toggle.prototype.animationAccordion=function(elem,type){if(type==="open"){elem.slideDown();}else{elem.slideUp();}};$.gsb.toggle.prototype.animationTab=function(panelToClose,panelToOpen){var _=this.base;if(_.options.animateTab){panelToClose.fadeOut("slow",function(){panelToOpen.fadeIn("slow");});}else{panelToClose.hide();panelToOpen.show();}};$.gsb.toggle.prototype.autoplay=function(){var _=this.base;if(_.$el.find("> button").length===0){var button=document.createElement("button");button.className="paused";var image=document.createElement("img");image.src=_.options.playButtonImagePlaying;image.alt=_.options.playButtonAltTextPlaying;playbutton=_.$el.append($(button).append(image));}_.playbutton=_.$el.find("button.paused");_.playbutton.on("click autoplayStart",function(e){e.preventDefault();e.stopPropagation();if(_.playbutton.hasClass("paused")){$(this).removeClass("paused").addClass("played");_.autoplayStart();_.changeStatePlayPause(_.options.playButtonImagePlaying,_.options.playButtonAltTextPlaying);_.isPaused=false;if(e.type!="autoplayStart"){_.isClicked=true;}}else{$(this).addClass("paused").removeClass("played");_.autoplayStop();_.changeStatePlayPause(_.options.playButtonImagePaused,_.options.playButtonAltTextPaused);_.isPaused=true;}});if(_.options.pauseOnHover){_.$el.on("mouseenter mouseleave",function(e){if(!_.isPaused){if(e.type=="mouseenter"){_.autoplayStop();}else{if(_.playbutton.hasClass("played")&&!_.isClicked){_.autoplayStart();}_.isClicked=false;}}});}if(_.options.playOnLoad){_.playbutton.trigger("autoplayStart");}};$.gsb.toggle.prototype.autoplayStart=function(){var _=this.base;_.rotation=setInterval(function(){var lastTab=_.getSwitchPanelClass(_.$el.find(".tabs-list > li").last());if(_.$el.find(".tabs-list .active-control").hasClass(lastTab[0])){_.$el.find(".tabs-list > li").first().click();}else{_.$el.find(".tabs-list .active-control").next().click();}},_.options.autoSpeed);};$.gsb.toggle.prototype.autoplayStop=function(){var _=this.base;clearInterval(_.rotation);};$.gsb.toggle.prototype.refreshTab=function(){var _=this.base;if(_.options.tab){_.$el.find(".tabs-container").removeAttr("multiselectable");_.$el.find(".tabs-container").removeAttr("role");_.$el.find(".heading").each(function(){$(this).removeAttr("aria-expanded");});if(_.options.changeOnHover&&!_.options.autoplay){_.$el.on("mouseenter focus",_.options.tabControls,function(e){_.clickedElem=$(this);_.changeStateTab($(this));});}_.$el.find(".panel-closed").hide();_.$el.find(".panel-opened").show();}};$.gsb.toggle.prototype.refreshAccordion=function(){var _=this.base;if(_.options.accordion){_.$el.find(".tabs-container").attr("role","tablist");if(!_.options.accordionCloseOther){_.$el.find(".tabs-container").attr("multiselectable","true");}_.$el.find(".heading").each(function(){$(this).attr("aria-expanded","false");$(this).hasClass("inactive-control")?$(this).next().hide():"";});}};$.gsb.toggle.prototype.changeStatePlayPause=function(imgsrc,imgalt){var _=this.base;var image=_.playbutton.find("img");image.prop("alt",imgalt).prop("src",imgsrc);};$.gsb.toggle.prototype.getSwitchPanelClass=function(elem){var panelClass=$.grep(elem.prop("class").split(" "),function(value){return value.indexOf("switch-panel")>-1;});return panelClass;};$.gsb.toggle.prototype.uninitAccordion=function(){var _=this.base;_.$el.off();_.$el.removeClass("gsb-toggle");_.$el.find(".active-control").removeClass("active-control").next().removeAttr("style");_.$el.find(".inactive-control").removeClass("inactive-control").next().removeAttr("style");};$.gsb.toggle.prototype.addA11y=function(elem,tabPanel){var _=this.base;elem=elem||_.$el;var focusElements=elem.find("input,textarea,select,a,button").not(":hidden");if(focusElements.length){focusElements[0].focus();focusElements.last().keydown(function(e){});}};$.gsb.toggle.prototype.addRichtextContainer=function(){var _=this.base;_.$el.find(".startaccordion").each(function(){$(this).nextUntil(".endaccordion").addBack().add($(this).find("~.endaccordion").first()).wrapAll('<div class="tabs-container"/>');var wrapper=$(this).parent();wrapper.find(".startaccordion, .accordionheadline").addClass("heading");wrapper.find(".heading").each(function(){$(this).nextUntil(".heading").wrapAll("<div/>");});wrapper.find(".heading").last().nextUntil(".accordionend").add(wrapper.find(".accordionend")).wrapAll("<div/>");wrapper.wrap('<div class="richtext-accordion"/>');});_.$el.removeData("gsb.toggle");_.$el.find(".richtext-accordion").gsb_toggle($.extend({},_.options,{richTextAccordion:false}));};$.fn.gsb_toggle=function(options){return this.each(function(){(new $.gsb.toggle(this,options));});};})(jQuery);
   /* Ende gsb_toggle */
   /* Start gsb_togglebar */
   /*!
 * GSB-Plugin zum Erstellung von Menue-Leiste fuer mobile Navigation
 *
 * Author: sbaecker
 *
 * @requires jQuery >= 1.11.1, gsb_responsiveListener
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.togglebar=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.togglebar",base);base.init=function(){base.options=$.extend({},$.gsb.togglebar.defaultOptions,options);base.$el.gsb_responsiveListener(base);};base.generate=function(){if($("#navServiceGS").length>0&&$("#navServiceLS").length>0){var gs='<li class="navServiceGS">'+$("#navServiceGS").html()+"</li>";var ls='<li class="navServiceLS">'+$("#navServiceLS").html()+"</li>";}else{var gs="";var ls="";}if($("#togglenav").length==0){base.$el.prepend('<div id="togglenav">'+'<ul class="left">'+'<li id="navMobileMenu">'+'<a href="#">Menü</a>'+"</li>"+'<li id="navMobileSearch">'+'<a href="#">Suche</a>'+"</li>"+"</ul>"+'<ul class="right">'+gs+ls+"</ul></div>");}};base._uninit=function(){$("#togglenav").remove();};base.init();};$.gsb.togglebar.defaultOptions={respondToEvents:true,onRefresh:function(){this._uninit();},responsive:[{breakpoint:1024,onRefresh:function(){this.generate();}}]};$.fn.gsb_togglebar=function(options){return this.each(function(){(new $.gsb.togglebar(this,options));});};})(jQuery);
   /* Ende gsb_togglebar */
   /* Start gsb_facetSlider */
   /*!
 * @name Navigation
 * @author @tbodenbe
 * Licensed under the MIT license
 *
 * */
(function($){if(!$.gsb){$.gsb={};}$.gsb.facetSlider=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.facetSlider",base);base.init=function(){base.options=$.extend({},$.gsb.facetSlider.defaultOptions,options);base._initStructure();};base._initStructure=function(){var styleHide="";if(base.$el.find("li").length<base.options.elementsToShow){styleHide="style='display:none";}$('<ul class="links toggleFacetElements" style="display:none"></ul><h4 class="triggerToggle" '+styleHide+">"+base.options.slideDownText+"</h4>").insertAfter(base.$el);var arrHideElements=[];base.$el.find(base.options.hidingElem).each(function(){arrHideElements.push($(this).detach());});var toggleFacetSelector=base.$el.next($(".toggleFacetElements"));$.each(arrHideElements,function(){toggleFacetSelector.append($(this));});base._toggleFacets($("#addContent").find(".triggerToggle"),"prev");};base._toggleFacets=function(elem,direction){direction=direction||"next";elem.off();elem.on("click",function(){var toggleElement;if(direction=="next"){toggleElement=$(this).next();}else{toggleElement=$(this).prev();}if(toggleElement.is(":hidden")){toggleElement.slideDown("slow");$(this).addClass("opened").removeClass("closed");$(this).text(base.options.slideUpText);}else{toggleElement.slideUp("slow");$(this).addClass("closed").removeClass("opened");$(this).text(base.options.slideDownText);}});};base.init();};$.gsb.facetSlider.defaultOptions={elementsToShow:5,hidingElem:">li.toggleHide",slideUpText:typeof(SLIDER_UP_TEXT)=="undefined"?"Weniger anzeigen":SLIDER_UP_TEXT,slideDownText:typeof(SLIDER_DOWN_TEXT)=="undefined"?"Mehr anzeigen":SLIDER_DOWN_TEXT};$.fn.gsb_facetSlider=function(options){return this.each(function(){(new $.gsb.facetSlider(this,options));});};})(jQuery);
   /* Ende gsb_facetSlider */
   /* Start gsb_navigation_mobile */
   /*!
 * GSB-Wrapper-Plugin fuer mmenu
 *
 * s. auch http://mmenu.frebsite.nl
 *
 * Wichtig: Soll der Menue-Button in einer Liste sein ( li > a), muss dieser schon vorhanden sein
 *
 * Author: rkrusenb
 *
 * @requires jQuery >= 1.11.1, gsb_responsiveListener, mmenu v4.4.0, mmenu searchfield addon
 *
 */
(function(){if(!$.gsb){$.gsb={};}$.gsb.navigation_mobile=function(container,options){var base=this;var listInited=false;base.$el=$(container);base.el=container;base.$el.data("gsb.navigation_mobile",base);var menuId="menu",menu=$("<div />").attr("id",menuId),list=$("<ul />").appendTo(menu),button,buttonSpan,notAPath=/:\/\/|^[.]*\//;base.init=function(){base.options=options=$.extend({},$.gsb.navigation_mobile.defaultOptions,options);$("body").gsb_responsiveListener(base);};base.configuration=function(){buttonSpan=$('<span class="aural" />').text(options.moreText);if(!options.list){if(base.$el.find("p.navigationMobileButton").length==0){button=$("<p />").addClass("navigationMobileButton").append($("<a />").text(options.menuText).attr("href","#").append(buttonSpan).click(function(){if(!$("html.mm-opened").length){buttonSpan.text(options.lessText);}}));$(container).append(button);$.ajax({url:json_url_mobileMenu,dataType:"json",success:processData});}}else{if($("#navMobileMenu >a").length>0){if(!listInited){$.ajax({url:json_url_mobileMenu,dataType:"json",success:processData});$("#navMobileMenu a").on("click",function(event){event.stopPropagation();event.preventDefault();if($("html").hasClass("mm-opened")){$("#menu").trigger("close.mm");}else{$("#menu").trigger("open.mm");}});listInited=true;}}else{console.log("li#navMobileMenu > a nicht gefunden!");}}};function processData(items){var i,item,element,newelem,ul;list.empty();for(i in items){if(items.hasOwnProperty(i)){item=items[i];newelem=$("<li />").addClass(item.css).attr("id","mmm"+item.id.replace(/\//g,"-"));if(item.nolink||item.id==window.NAV_MENU_NODE){newelem.append($("<span />").append($("<strong />").append(item.title)));}else{newelem.append($("<a />").attr("href",notAPath.test(item.path)?item.path:"/"+item.path).attr("target",item.target||null).append(item.title));}if(items[i-1]){if(item.level>items[i-1].level){ul=element.find("> ul");}else{var j=items[i-1].level-item.level,elem=element;while(j>-1){elem=elem.parent().parent();j--;}ul=elem.find(" > ul");}ul=ul.length?ul:$("<ul />").appendTo(ul.end());element=newelem.appendTo(ul);}else{element=newelem.appendTo(list);}}}sitewrapper=$("#sitewrapper")[0]||$("<div id='sitewrapper' />")[0];$(sitewrapper).append($("body").contents()).appendTo($("body"));if(!options.list){button.find("a").attr("href","#"+menuId).after(menu);}list.appendTo(menu.empty());menu.mmenu(options.mmenuOptions,{pageNodetype:"div"});menu.on("closing.mm",function(ev){buttonSpan.text(options.moreText);});if(options.autosuggestURL){menu.on("open.mm",function mobileautosuggest(){$(this).off("open.mm",mobileautosuggest).gsb_AutoSuggest(options.autosuggestURL);});}menu.prepend($("<h1 />").addClass("close-button").append($('<a href="#" >'+options.menuText+"</a>").append('<img src="'+options.closeButtonImage+'" alt="'+options.closeMenuText+'" /><span class="aural">'+options.lessText+"</span>").click(function(ev){ev.preventDefault();menu.trigger("close");})));menu.find("#mmm"+(window.NAV_MENU_NODE||"").replace(/\//g,"-")).parentsUntil(".mm-menu > ul").filter("ul").trigger("open.mm");menu.find(".mm-search").append('<input type="image" class="image" src="'+options.searchImage+'" />');menu.find(".mm-search input:text").on("keydown",function(event){if(event.keyCode==13&&$(this).val()!=""){if(!menu.data("gsb.AutoSuggest")||menu.data("gsb.AutoSuggest").enterPos<0){$("#search form input:text").val($(this).val());$("#menu input:image").trigger($.Event("click"));}}});$("#menu .mm-search input:image").on("keydown click",function(event){var textInput=$("#menu .mm-search input:text");if((event.keyCode==13||event.type=="click")&&textInput.val()!=""){$("#search form input:text").val(textInput.val());$("#search form input:image").trigger($.Event("click"));$("#foo").trigger("close");}});if($(options.searchButtonToggleBarSelector).length>0){$(options.searchButtonToggleBarSelector).on("click",function(event){event.stopPropagation();event.preventDefault();$("#menu").trigger("open.mm");$(".mm-search input:text").focus();});}}base._uninit=function(){menu.trigger("close");$("#menu").remove();$("#mm-blocker").remove();base.$el.empty();listInited=false;};base.init();};$.gsb.navigation_mobile.defaultOptions={moreText:typeof(SHOW_MORE)=="undefined"?"Einblenden":SHOW_MORE,lessText:typeof(SHOW_LESS)=="undefined"?"Ausblenden":SHOW_LESS,menuText:typeof(NAV_MOBILE_MENU)=="undefined"?"Menü":NAV_MOBILE_MENU,closeMenuText:typeof(CLOSE)=="undefined"?"Schließen":CLOSE,searchButtonToggleBarSelector:"#navMobileSearch > a",list:false,mmenuOptions:{classes:"mm-light",position:"left",zposition:"back",slidingSubmenus:false,searchfield:{add:true,search:false,placeholder:typeof(NAV_MOBILE_SEARCH)=="undefined"?"Search":NAV_MOBILE_SEARCH}},respondToEvents:true,onRefresh:function(){this._uninit();},responsive:[{breakpoint:1024,onRefresh:function(){var _=this;setTimeout(function(){_.configuration();},200);}}]};$.fn.gsb_navigation_mobile=function(options){$.gsb.navigation_mobile(this.first()[0],options);};})(jQuery);
   /* Ende gsb_navigation_mobile */
   /* Start gsb_responsivetables */
   /*!
 * GSB-Plugin zum wrappen von Tabellen fuer die mobile Darstellung
 *
 * Author: sbaecker
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.responsiveTables=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.responsiveTables",base);base.init=function(){base.options=$.extend({},$.gsb.responsiveTables.defaultOptions,options);if(base.$el.find("table").length>0){base.$el.find("table").each(function(){switch(base.options.way){case"scroll":$(this).wrap('<div class="responsiveTable"></div>');break;default:$(this).wrap('<div class="responsiveTable"></div>');break;}});}};base.init();};$.gsb.responsiveTables.defaultOptions={way:"scroll"};$.fn.gsb_responsiveTables=function(options){return this.each(function(){(new $.gsb.responsiveTables(this,options));});};})(jQuery);
   /* Ende gsb_responsivetables */
   /* Start gsb_tooltip */
   /*!
 * GSB-Wrapper-Plugin fuer Foundation Tooltip-Plugin. Arbeitet als Polyfill, um von Foundation benoetigte
 * data-Attribute und Klassen hinzuzufuegen.
 *
 * s. auch http://foundation.zurb.com/docs/components/tooltips.html
 *
 * Optionen:
 * tooltipPosition: Die Position des Tooltips(top,bottom(default),left,right)
 * tooltipCorners: Layoutmodus des Tooltip(''(default), 'radius', 'round')
 * tooltipFoundationOptions: native Optionen fuer das Foundation Plugin(undefined(default), siehe Doku oben)
 *
 * Abhängigkeiten:
 *
 * - modernizr(wegen Foundation)
 * - foundation
 * - foundation-tooltip
 *
 * Author: anuebel
 */
(function($){if(!$.gsb){$.gsb={};}$.gsb.tooltip=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.tooltip",base);base.init=function(){base.options=$.extend({},$.gsb.tooltip.defaultOptions,options);base.$el.addClass("has-tip tip-"+base.options.tooltipPosition+" "+base.options.tooltipCorners).attr("data-tooltip","").attr("aria-haspopup","true");if(base.options.tooltipFoundationOptions){var serializedFoundationOptions=JSON.stringify(base.options.tooltipFoundationOptions);base.$el.attr("data-options",serializedFoundationOptions);}};base.init();};$.gsb.tooltip.defaultOptions={tooltipPosition:"bottom",tooltipCorners:"",tooltipFoundationOptions:undefined};$.fn.gsb_tooltip=function(options){return this.each(function(){(new $.gsb.tooltip(this,options));});};})(jQuery);
   /* Ende gsb_tooltip */
   /* Start gsb_twoclickshare */
   /*!
 * GSB-Wrapper-Plugin fuer Heise-2-Klickloesung
 *
 * s. auch http://www.heise.de/extras/socialshareprivacy/
 *
 * Author: rkrusenb
 */
(function($){
  if(!$ .gsb){
    $ .gsb = {};
  }
  $ .gsb.twoclickshare = function(el, userOptions){
    var state = {},
      options,
      wrapper,
      toggleElement,
      wrapperOuter;
    function init(){
      options = state.options = $ .extend(true, {}, $ .gsb.twoclickshare.defaultOptions, userOptions);
      //wrapperOuter = $('<' + options.wrapperTag + ' />').attr('id', options.wrapperId);
      //wrapper = state.wrapper = $('<div/>').addClass(options.wrapperInnerClass);
      //wrapperOuter.append(wrapper);
      wrapper = $('<' + options.wrapperTag + ' />').attr('id', options.wrapperId);
      wrapperInner = state.wrapper = $('<div/>').addClass(options.wrapperInnerClass);
      wrapper.append(wrapperInner);
      if(options.insert == 'prepend'){
        $(el).prepend(wrapper);
      } else if(options.insert == 'append'){
        $(el).append(wrapper);
      } else if(options.insert == 'before'){
        $(el).before(wrapper);
      } else if(options.insert == 'after'){
        $(el).after(wrapper);
      }
      wrapperInner.socialSharePrivacy(options.socialSharePrivacyOptions)
        .prepend('<h3>' + options.labelHeader + '</h3>')
        .find('.switch, .settings').attr('tabindex', 0).keydown(function(ev){
          if(ev.which == 13) {
            $(this).trigger('click');
          }
        });
      if(options.toggleElement) {
        toggleElement = $(options.toggleElement, el);
        wrapper.find('li:first').before($('<li class="email"><a>' + options.labelFormLink + '</a></li>').find('a').attr('href', toggleElement.attr('href')).end());
        wrapper.data('gsb.twoclickshare', {oldHeight: wrapper.height()}).css({top: 0, display: 'none'}).attr('aria-hidden', 'true');
        toggleElement.attr('aria-haspopup', true).attr('aria-owns', options.wrapperId).attr('title', options.toggleTitleClosed).click(function(ev){
          var oldHeight = wrapper.data('gsb.twoclickshare').oldHeight;
          ev.preventDefault();
          ev.stopPropagation();
          if(wrapper.is('[aria-hidden=true]')) {
            /* CSS sorgt dafür, dass es "hoch" slidet */
            wrapper.attr('aria-hidden', 'false').css({height: 0, display: 'block'}).animate({top: -oldHeight-2, height: oldHeight}, 'slow').promise().done(function(){$(this).css('height', '').css({top: -$(this).height()-2})});
            wrapper.find('.close').focus();
            toggleElement.attr("title",options.toggleTitleOpened);
          } else {
            /* CSS sorgt dafür, dass es "runter" slidet */
            wrapper.attr('aria-hidden', 'true').data('gsb.twoclickshare', {oldHeight: wrapper.height()}).animate({top: 0, height: 0}, 'slow').promise().done(function(){$(this).css({display: 'none'})});
            toggleElement.attr("title",options.toggleTitleClosed);
          }
        });
      }
      wrapperInner.prepend($('<button class="close" tabindex="0">' + options.labelCloseButton + '</button>').click(function(){
        toggleElement.trigger('click');
        toggleElement.focus();
      }));
    }
    init();
  };
  $ .gsb.twoclickshare.defaultOptions = {
    insert: 'after', //where the wrapper is inserted, in relation to el,
    wrapperId: 'share', //ID of the wrapper element,
    wrapperTag: 'div', //HTML tag of the wrapper element
    toggleElement: '',
    toggleTitleClosed: typeof SHOW_MORE != "undefined" ? SHOW_MORE : 'Mehr anzeigen',
    toggleTitleOpened: typeof SHOW_LESS != "undefined" ? SHOW_LESS : 'Schließen',
    wrapperInnerClass: 'wrapper-share',
    labelHeader: typeof TWOCLICKSHARE_TITLE != "undefined" ? TWOCLICKSHARE_TITLE : 'In sozialen Medien teilen',
    labelFormLink: typeof TWOCLICKSHARE_FORMLINKTEXT != "undefined" ? TWOCLICKSHARE_FORMLINKTEXT : 'E-Mail',
    labelCloseButton: typeof CLOSE != "undefined" ? CLOSE : 'Schließen',
    socialSharePrivacyOptions: { //see jquery.socialshareprivacy.js for all possible options
      css_path: '',
      services: {
        facebook: {
          dummy_img: typeof(image_url_share_facebook_inactive) != "undefined" ? image_url_share_facebook_inactive : ''
        },
        twitter: {
          dummy_img: typeof(image_url_share_twitter_inactive) != "undefined" ? image_url_share_twitter_inactive : ''
        },
        gplus: {
          dummy_img: typeof(image_url_share_gplus_inactive) != "undefined" ? image_url_share_gplus_inactive : ''
        }
      }
    }
  };
  $ .fn.gsb_twoclickshare = function(options){
    return $ (this).map(function(){
      return $ .gsb.twoclickshare(this, options);
    });
  };
}(jQuery));
   /* Ende gsb_twoclickshare */
   /* Start gsb_multimedia */
   /*!
 * GSB-Wrapper-Plugin fuer mediaelement.js.
 *
 * s. auch http://mediaelementjs.com
 *
 * Optionen:
 * center: Soll das Multimedia-Element zentriert werden(false(default), true)(nur fuer Videos)
 * responsive: Objekt mit Angaben zu den Groessen in den verschiedenen Aufloesungen
 * mediaelementplayerOptions: native Optionen fuer das mediaelement.js Plugin(null(default), siehe Doku oben)
 *
 * @author anuebel
 *
 * @requires jQuery >= 1.11.1, gsb_responsiveListener, MediaElementPlayer
 *
 * @global jQuery, console
 *
 */
(function($,window){if(!$.gsb){$.gsb={};}$.gsb.Multimedia=function(el,options){var base=this;base.$el=$(el);base.$elWrapper=base.$el.closest(".mejs-wrapper");base.el=el;base.$el.data("gsb.Multimedia",base);base.reloadHref=base.$el.data("reload-href");base.init(options);};$.gsb.Multimedia.prototype.centerVideoIfRequired=function(){var base=this,mediaelementplayerOptions=base.options.mediaelementplayerOptions,videoWidth=mediaelementplayerOptions&&mediaelementplayerOptions.videoWidth||base.$el.attr("width");if(base.options.center&&videoWidth){var centerContainer,parent=base.$el.parent();if(!parent.is(".mejs-centered")){centerContainer=$('<div class="mejs-centered" style="margin: 0 auto;"></div>');centerContainer.css("width",videoWidth+"px");base.$el.wrap(centerContainer);}else{centerContainer=parent;centerContainer.css("width",videoWidth+"px");}}};$.gsb.Multimedia.prototype.reinitializeVideoElement=function(){var base=this;$.get(base.reloadHref,function(data){var $newVideoElementContainer=$(data);base.$elWrapper.replaceWith($newVideoElementContainer);var newVideoElement=$newVideoElementContainer.find("video").get(0);base.onVideoElementReinitialized(newVideoElement);});};$.gsb.Multimedia.prototype.triggerRefresh=function(){var base=this;if(base.$el.is("video")&&base.reloadHref){base.reinitializeVideoElement();}};$.gsb.Multimedia.prototype.onVideoElementReinitialized=function(newVideoElement){var base=this;base.$el=$(newVideoElement);base.$elWrapper=base.$el.closest(".mejs-wrapper");base.el=newVideoElement;base.$el.data("gsb.Multimedia",base);base.initializeMediaElement();};$.gsb.Multimedia.prototype.initializeMediaElement=function(){var base=this,mediaelementplayerOptions=$.extend({},$.gsb.Multimedia.defaultMediaelementplayerOptions,base.options.settings.mediaelementplayerOptions,base.mediaelementplayerOptionsFromDataAttribute());if(mediaelementplayerOptions.hasOwnProperty("videoWidth")){base.$el.attr("width",mediaelementplayerOptions.videoWidth);}if(mediaelementplayerOptions.hasOwnProperty("videoHeight")){base.$el.attr("height",mediaelementplayerOptions.videoHeight);}base.centerVideoIfRequired();base.$el.mediaelementplayer(mediaelementplayerOptions);};$.gsb.Multimedia.prototype.mediaelementplayerOptionsFromDataAttribute=function(){var base=this,optionsFromDataAttributeAsString=base.$el.data("options");var opts={},ii,p,opts_arr=(optionsFromDataAttributeAsString||":").split(";");ii=opts_arr.length;function isNumber(o){return !isNaN(o-0)&&o!==null&&o!==""&&o!==false&&o!==true;}function trim(str){if(typeof str==="string"){return $.trim(str);}return str;}while(ii--){p=opts_arr[ii].split(":");p=[p[0],p.slice(1).join(":")];if(/true/i.test(p[1])){p[1]=true;}if(/false/i.test(p[1])){p[1]=false;}if(isNumber(p[1])){if(p[1].indexOf(".")===-1){p[1]=parseInt(p[1],10);}else{p[1]=parseFloat(p[1]);}}if(p.length===2&&p[0].length>0){opts[trim(p[0])]=trim(p[1]);}}return opts;};$.gsb.Multimedia.prototype.init=function(options){var base=this;base.options=$.extend({},$.gsb.Multimedia.defaultOptions,options);base.$el.gsb_responsiveListener(base.$el.data("gsb.Multimedia"));base.initializeMediaElement();};$.gsb.Multimedia.defaultOptions={onRefresh:function(){this.triggerRefresh();},responsive:[{breakpoint:600,onRefresh:function(){this.triggerRefresh();},settings:{mediaelementplayerOptions:{videoWidth:440,videoHeight:248}}},{breakpoint:440,onRefresh:function(){this.triggerRefresh();},settings:{mediaelementplayerOptions:{videoWidth:280,videoHeight:158}}}],center:false,respondToEvents:true,settings:{mediaelementplayerOptions:{}}};$.gsb.Multimedia.defaultMediaelementplayerOptions={flashName:"/static/mediaElementPlayer/flashmediaelement.swf",showPosterWhenEnded:true};$.fn.gsb_Multimedia=function(options){return this.each(function(){return new $.gsb.Multimedia(this,options);});};}(jQuery,window));
   /* Ende gsb_multimedia */
   /* Start gsb_webcode */
   /*!
* @name gsb_webcodeRedirect
* @author @
* Licensed under the MIT license
*
* @requires jQuery >= 1.7.2,
* */
(function($){if(!$.gsb){$.gsb={};}$.gsb.webcodeRedirect=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.webcodeRedirect",base);base.init=function(){base.options=$.extend({},$.gsb.webcodeRedirect.defaultOptions,options);base.$el.on("change",function(e){e.preventDefault();e.stopPropagation();var location=$("base").attr("href");location=location.concat(base.options.webcodeURLSuffix);if($(this).find(":selected").data("href")){location=location.concat($(this).find(":selected").data("href"));window.location.href=location;}});};base.init();};$.gsb.webcodeRedirect.defaultOptions={webcodeURLSuffix:"webcode/"};$.fn.gsb_webcodeRedirect=function(options){return this.each(function(){(new $.gsb.webcodeRedirect(this,options));});};})(jQuery);
   /* Ende gsb_webcode */
   /* Start gsb_printTable */
   /*!
 * @name Navigation
 * @author @tbodenbe
 * Licensed under the MIT license
 *
 * */
(function($){if(!$.gsb){$.gsb={};}$.gsb.printTable=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.printTable",base);base.init=function(){base.options=$.extend({},$.gsb.printTable.defaultOptions,options);base._print();};base._print=function(){var printLink=$('<span class="tablePrint"><a href="#">'+base.options.printText+"</a></span>");$(base.$el).before(printLink);$(printLink).on("click",function(e){e.preventDefault();e.stopPropagation();var contents=$(base.$el).html();var win=window.open();self.focus();win.document.open();win.document.write("<"+"html"+"><"+"head"+"><"+"style"+">");win.document.write("body, td { font-family: Verdana; font-size: 10pt;}");win.document.write("table { table-layout: fixed; width: 100%;}");win.document.write("th, td { border: 1px solid black;}");win.document.write("th { word-break: break-word; font-weight: bold; background: #99bfc2 ; padding: 10px; text-align: left;}");win.document.write("td { padding: 8px; text-align: left; vertical-align: top;}");win.document.write("<"+"/"+"style"+"><"+"/"+"head"+"><"+"body"+">");win.document.write("<table>"+contents+"</table");win.document.write("<"+"/"+"body"+"><"+"/"+"html"+">");win.document.close();win.print();win.close();});};base.init();};$.gsb.printTable.defaultOptions={printText:typeof(PRINTTABLE_TEXT)!=="undefined"?PRINTTABLE_TEXT:"Tabelle drucken"};$.fn.gsb_printTable=function(options){return this.each(function(){(new $.gsb.printTable(this,options));});};})(jQuery);
   /* Ende gsb_printTable */
   /* Start gsb_maxHeight */
   /*!
 * @name maxHeight
 * @author @pespeloe
 * Licensed under the MIT license
 *
 * Container per Javascript auf die Höhe des höchsten
 * Containers setzen
 * */
(function($){if(!$.materna){$.materna={};}$.materna.maxHeight=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("materna.maxHeight",base);base.init=function(){base.options=$.extend({},$.materna.maxHeight.defaultOptions,options);base.setHeights=function(){if(typeof base.options.beforeInit==="function"){base.options.beforeInit.call($(this),base.$el);}var $items=base.$el.find(base.options.boxes);$items.css("height","auto");var maxHeight=0,maxHeightAbsoluteContainer=0;$items.each(function(){var itemHeight=parseInt($(this).outerHeight());var moreButton;if(base.options.absoluteElements!==undefined){moreButton=parseInt($(this).find(base.options.absoluteElements).outerHeight(true));if(moreButton>maxHeightAbsoluteContainer){maxHeightAbsoluteContainer=moreButton;}}if(itemHeight>maxHeight){maxHeight=itemHeight;}});$items.each(function(){$(this).css("height",maxHeight+maxHeightAbsoluteContainer);});if(typeof base.options.afterInit==="function"){base.options.afterInit.call($(this),base.$el,$items);}};$(window).on("resize load",function(){base.setHeights();});};base.init();};$.materna.maxHeight.defaultOptions={boxes:".slick-slide",absoluteElements:undefined,beforeInit:undefined,afterInit:undefined};$.fn.materna_maxHeight=function(options){return this.each(function(){(new $.materna.maxHeight(this,options));});};})(jQuery);
   /* Ende gsb_maxHeight */
   /* Start responsive_imagemap */
   function ResponsiveMaps(){var maps=Array();var resetCoords=Array();this.add=function(img,mapName,actualWidth){var addImgMapInfo=[img,mapName,actualWidth];maps.push(addImgMapInfo);var areas=getAreas(mapName);var aCoordsAttr=Array();areas.each(function(){aCoordsAttr.push($(this).attr("coords"));});resetCoords.push(aCoordsAttr);this.update();};this.update=function(){for(var i=0;i<maps.length;i++){var currentImgMapInfo=maps[i];iterateMap(currentImgMapInfo[0],currentImgMapInfo[1],currentImgMapInfo[2],i);}};function iterateMap(img,mapName,actualWidth,step){var factor=$(img).width()/actualWidth;var areas=getAreas(mapName);var oldCoords=resetCoords[step];areas.each(function(i){var currentArea=$(this);var coords=oldCoords[i];coords=mapTransfer(coords,factor);currentArea.attr("coords",coords);});}function mapTransfer(coord,factor){var output=[];var newCoordString="";var coords=coord.split(", ");if(coords.length==1){coords=coord.split(",");}for(var i=0;i<coords.length;i++){newCoordString+=Math.floor((coords[i]*factor));output.push(newCoordString);newCoordString="";}return output.join(",");}function getAreas(mapName){return $("map[name='"+mapName+"']").children();}}var respMaps=new ResponsiveMaps();$(window).resize(function(){respMaps.update();});$(document).ready(function(){var maps=$("map");maps.each(function(){var currentMap=$(this);var mapName=currentMap.attr("name");var imgs=$("img[usemap='#"+mapName+"']");imgs.each(function(){var currentImg=$(this);var actualWidth=currentImg.prop("naturalWidth");respMaps.add(currentImg,mapName,actualWidth);});});});
   /* Ende responsive_imagemap */
   /* Start imgmapHandler */
   (function($){if(!$.gsb){$.gsb={};}$.gsb.imgmapHandler=function(el,parentImg){var base=$(el);var replaceFrom="/pics_k/";var replaceTo="/pics/";function init(){base.on("click",function(evt){evt.preventDefault();$(parentImg).attr("src",$(base).find("span.wrapper img").attr("src").replace(replaceFrom,replaceTo));});}init();};$.fn.imgmapHandler=function(parentImg){return $(this).map(function(){return $.gsb.imgmapHandler(this,parentImg);});};}(jQuery));
   /* Ende imgmapHandler */
   /* Start leaflet_bibliothek */
   /* @preserve
 * Leaflet 1.2.0+Detached: 1ac320ba232cb85b73ac81f3d82780c9d07f0d4e.1ac320b, a JS library for interactive maps. http://leafletjs.com
 * (c) 2010-2017 Vladimir Agafonkin, (c) 2010-2011 CloudMade
 */
(function (global, factory) {
	typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports) :
	typeof define === 'function' && define.amd ? define(['exports'], factory) :
	(factory((global.L = {})));
}(this, (function (exports) { 'use strict';

var version = "1.2.0+HEAD.1ac320b";

/*
 * @namespace Util
 *
 * Various utility functions, used by Leaflet internally.
 */

var freeze = Object.freeze;
Object.freeze = function (obj) { return obj; };

// @function extend(dest: Object, src?: Object): Object
// Merges the properties of the `src` object (or multiple objects) into `dest` object and returns the latter. Has an `L.extend` shortcut.
function extend(dest) {
	var i, j, len, src;

	for (j = 1, len = arguments.length; j < len; j++) {
		src = arguments[j];
		for (i in src) {
			dest[i] = src[i];
		}
	}
	return dest;
}

// @function create(proto: Object, properties?: Object): Object
// Compatibility polyfill for [Object.create](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Object/create)
var create = Object.create || (function () {
	function F() {}
	return function (proto) {
		F.prototype = proto;
		return new F();
	};
})();

// @function bind(fn: Function, …): Function
// Returns a new function bound to the arguments passed, like [Function.prototype.bind](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Function/bind).
// Has a `L.bind()` shortcut.
function bind(fn, obj) {
	var slice = Array.prototype.slice;

	if (fn.bind) {
		return fn.bind.apply(fn, slice.call(arguments, 1));
	}

	var args = slice.call(arguments, 2);

	return function () {
		return fn.apply(obj, args.length ? args.concat(slice.call(arguments)) : arguments);
	};
}

// @property lastId: Number
// Last unique ID used by [`stamp()`](#util-stamp)
var lastId = 0;

// @function stamp(obj: Object): Number
// Returns the unique ID of an object, assiging it one if it doesn't have it.
function stamp(obj) {
	/*eslint-disable */
	obj._leaflet_id = obj._leaflet_id || ++lastId;
	return obj._leaflet_id;
	/*eslint-enable */
}

// @function throttle(fn: Function, time: Number, context: Object): Function
// Returns a function which executes function `fn` with the given scope `context`
// (so that the `this` keyword refers to `context` inside `fn`'s code). The function
// `fn` will be called no more than one time per given amount of `time`. The arguments
// received by the bound function will be any arguments passed when binding the
// function, followed by any arguments passed when invoking the bound function.
// Has an `L.throttle` shortcut.
function throttle(fn, time, context) {
	var lock, args, wrapperFn, later;

	later = function () {
		// reset lock and call if queued
		lock = false;
		if (args) {
			wrapperFn.apply(context, args);
			args = false;
		}
	};

	wrapperFn = function () {
		if (lock) {
			// called too soon, queue to call later
			args = arguments;

		} else {
			// call and lock until later
			fn.apply(context, arguments);
			setTimeout(later, time);
			lock = true;
		}
	};

	return wrapperFn;
}

// @function wrapNum(num: Number, range: Number[], includeMax?: Boolean): Number
// Returns the number `num` modulo `range` in such a way so it lies within
// `range[0]` and `range[1]`. The returned value will be always smaller than
// `range[1]` unless `includeMax` is set to `true`.
function wrapNum(x, range, includeMax) {
	var max = range[1],
	    min = range[0],
	    d = max - min;
	return x === max && includeMax ? x : ((x - min) % d + d) % d + min;
}

// @function falseFn(): Function
// Returns a function which always returns `false`.
function falseFn() { return false; }

// @function formatNum(num: Number, digits?: Number): Number
// Returns the number `num` rounded to `digits` decimals, or to 5 decimals by default.
function formatNum(num, digits) {
	var pow = Math.pow(10, digits || 5);
	return Math.round(num * pow) / pow;
}

// @function trim(str: String): String
// Compatibility polyfill for [String.prototype.trim](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/String/Trim)
function trim(str) {
	return str.trim ? str.trim() : str.replace(/^\s+|\s+$/g, '');
}

// @function splitWords(str: String): String[]
// Trims and splits the string on whitespace and returns the array of parts.
function splitWords(str) {
	return trim(str).split(/\s+/);
}

// @function setOptions(obj: Object, options: Object): Object
// Merges the given properties to the `options` of the `obj` object, returning the resulting options. See `Class options`. Has an `L.setOptions` shortcut.
function setOptions(obj, options) {
	if (!obj.hasOwnProperty('options')) {
		obj.options = obj.options ? create(obj.options) : {};
	}
	for (var i in options) {
		obj.options[i] = options[i];
	}
	return obj.options;
}

// @function getParamString(obj: Object, existingUrl?: String, uppercase?: Boolean): String
// Converts an object into a parameter URL string, e.g. `{a: "foo", b: "bar"}`
// translates to `'?a=foo&b=bar'`. If `existingUrl` is set, the parameters will
// be appended at the end. If `uppercase` is `true`, the parameter names will
// be uppercased (e.g. `'?A=foo&B=bar'`)
function getParamString(obj, existingUrl, uppercase) {
	var params = [];
	for (var i in obj) {
		params.push(encodeURIComponent(uppercase ? i.toUpperCase() : i) + '=' + encodeURIComponent(obj[i]));
	}
	return ((!existingUrl || existingUrl.indexOf('?') === -1) ? '?' : '&') + params.join('&');
}

var templateRe = /\{ *([\w_\-]+) *\}/g;

// @function template(str: String, data: Object): String
// Simple templating facility, accepts a template string of the form `'Hello {a}, {b}'`
// and a data object like `{a: 'foo', b: 'bar'}`, returns evaluated string
// `('Hello foo, bar')`. You can also specify functions instead of strings for
// data values — they will be evaluated passing `data` as an argument.
function template(str, data) {
	return str.replace(templateRe, function (str, key) {
		var value = data[key];

		if (value === undefined) {
			throw new Error('No value provided for variable ' + str);

		} else if (typeof value === 'function') {
			value = value(data);
		}
		return value;
	});
}

// @function isArray(obj): Boolean
// Compatibility polyfill for [Array.isArray](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Array/isArray)
var isArray = Array.isArray || function (obj) {
	return (Object.prototype.toString.call(obj) === '[object Array]');
};

// @function indexOf(array: Array, el: Object): Number
// Compatibility polyfill for [Array.prototype.indexOf](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Array/indexOf)
function indexOf(array, el) {
	for (var i = 0; i < array.length; i++) {
		if (array[i] === el) { return i; }
	}
	return -1;
}

// @property emptyImageUrl: String
// Data URI string containing a base64-encoded empty GIF image.
// Used as a hack to free memory from unused images on WebKit-powered
// mobile devices (by setting image `src` to this string).
var emptyImageUrl = 'data:image/gif;base64,R0lGODlhAQABAAD/ACwAAAAAAQABAAACADs=';

// inspired by http://paulirish.com/2011/requestanimationframe-for-smart-animating/

function getPrefixed(name) {
	return window['webkit' + name] || window['moz' + name] || window['ms' + name];
}

var lastTime = 0;

// fallback for IE 7-8
function timeoutDefer(fn) {
	var time = +new Date(),
	    timeToCall = Math.max(0, 16 - (time - lastTime));

	lastTime = time + timeToCall;
	return window.setTimeout(fn, timeToCall);
}

var requestFn = window.requestAnimationFrame || getPrefixed('RequestAnimationFrame') || timeoutDefer;
var cancelFn = window.cancelAnimationFrame || getPrefixed('CancelAnimationFrame') ||
		getPrefixed('CancelRequestAnimationFrame') || function (id) { window.clearTimeout(id); };

// @function requestAnimFrame(fn: Function, context?: Object, immediate?: Boolean): Number
// Schedules `fn` to be executed when the browser repaints. `fn` is bound to
// `context` if given. When `immediate` is set, `fn` is called immediately if
// the browser doesn't have native support for
// [`window.requestAnimationFrame`](https://developer.mozilla.org/docs/Web/API/window/requestAnimationFrame),
// otherwise it's delayed. Returns a request ID that can be used to cancel the request.
function requestAnimFrame(fn, context, immediate) {
	if (immediate && requestFn === timeoutDefer) {
		fn.call(context);
	} else {
		return requestFn.call(window, bind(fn, context));
	}
}

// @function cancelAnimFrame(id: Number): undefined
// Cancels a previous `requestAnimFrame`. See also [window.cancelAnimationFrame](https://developer.mozilla.org/docs/Web/API/window/cancelAnimationFrame).
function cancelAnimFrame(id) {
	if (id) {
		cancelFn.call(window, id);
	}
}


var Util = (Object.freeze || Object)({
	freeze: freeze,
	extend: extend,
	create: create,
	bind: bind,
	lastId: lastId,
	stamp: stamp,
	throttle: throttle,
	wrapNum: wrapNum,
	falseFn: falseFn,
	formatNum: formatNum,
	trim: trim,
	splitWords: splitWords,
	setOptions: setOptions,
	getParamString: getParamString,
	template: template,
	isArray: isArray,
	indexOf: indexOf,
	emptyImageUrl: emptyImageUrl,
	requestFn: requestFn,
	cancelFn: cancelFn,
	requestAnimFrame: requestAnimFrame,
	cancelAnimFrame: cancelAnimFrame
});

// @class Class
// @aka L.Class

// @section
// @uninheritable

// Thanks to John Resig and Dean Edwards for inspiration!

function Class() {}

Class.extend = function (props) {

	// @function extend(props: Object): Function
	// [Extends the current class](#class-inheritance) given the properties to be included.
	// Returns a Javascript function that is a class constructor (to be called with `new`).
	var NewClass = function () {

		// call the constructor
		if (this.initialize) {
			this.initialize.apply(this, arguments);
		}

		// call all constructor hooks
		this.callInitHooks();
	};

	var parentProto = NewClass.__super__ = this.prototype;

	var proto = create(parentProto);
	proto.constructor = NewClass;

	NewClass.prototype = proto;

	// inherit parent's statics
	for (var i in this) {
		if (this.hasOwnProperty(i) && i !== 'prototype' && i !== '__super__') {
			NewClass[i] = this[i];
		}
	}

	// mix static properties into the class
	if (props.statics) {
		extend(NewClass, props.statics);
		delete props.statics;
	}

	// mix includes into the prototype
	if (props.includes) {
		checkDeprecatedMixinEvents(props.includes);
		extend.apply(null, [proto].concat(props.includes));
		delete props.includes;
	}

	// merge options
	if (proto.options) {
		props.options = extend(create(proto.options), props.options);
	}

	// mix given properties into the prototype
	extend(proto, props);

	proto._initHooks = [];

	// add method for calling all hooks
	proto.callInitHooks = function () {

		if (this._initHooksCalled) { return; }

		if (parentProto.callInitHooks) {
			parentProto.callInitHooks.call(this);
		}

		this._initHooksCalled = true;

		for (var i = 0, len = proto._initHooks.length; i < len; i++) {
			proto._initHooks[i].call(this);
		}
	};

	return NewClass;
};


// @function include(properties: Object): this
// [Includes a mixin](#class-includes) into the current class.
Class.include = function (props) {
	extend(this.prototype, props);
	return this;
};

// @function mergeOptions(options: Object): this
// [Merges `options`](#class-options) into the defaults of the class.
Class.mergeOptions = function (options) {
	extend(this.prototype.options, options);
	return this;
};

// @function addInitHook(fn: Function): this
// Adds a [constructor hook](#class-constructor-hooks) to the class.
Class.addInitHook = function (fn) { // (Function) || (String, args...)
	var args = Array.prototype.slice.call(arguments, 1);

	var init = typeof fn === 'function' ? fn : function () {
		this[fn].apply(this, args);
	};

	this.prototype._initHooks = this.prototype._initHooks || [];
	this.prototype._initHooks.push(init);
	return this;
};

function checkDeprecatedMixinEvents(includes) {
	if (!L || !L.Mixin) { return; }

	includes = isArray(includes) ? includes : [includes];

	for (var i = 0; i < includes.length; i++) {
		if (includes[i] === L.Mixin.Events) {
			console.warn('Deprecated include of L.Mixin.Events: ' +
				'this property will be removed in future releases, ' +
				'please inherit from L.Evented instead.', new Error().stack);
		}
	}
}

/*
 * @class Evented
 * @aka L.Evented
 * @inherits Class
 *
 * A set of methods shared between event-powered classes (like `Map` and `Marker`). Generally, events allow you to execute some function when something happens with an object (e.g. the user clicks on the map, causing the map to fire `'click'` event).
 *
 * @example
 *
 * ```js
 * map.on('click', function(e) {
 * 	alert(e.latlng);
 * } );
 * ```
 *
 * Leaflet deals with event listeners by reference, so if you want to add a listener and then remove it, define it as a function:
 *
 * ```js
 * function onClick(e) { ... }
 *
 * map.on('click', onClick);
 * map.off('click', onClick);
 * ```
 */

var Events = {
	/* @method on(type: String, fn: Function, context?: Object): this
	 * Adds a listener function (`fn`) to a particular event type of the object. You can optionally specify the context of the listener (object the this keyword will point to). You can also pass several space-separated types (e.g. `'click dblclick'`).
	 *
	 * @alternative
	 * @method on(eventMap: Object): this
	 * Adds a set of type/listener pairs, e.g. `{click: onClick, mousemove: onMouseMove}`
	 */
	on: function (types, fn, context) {

		// types can be a map of types/handlers
		if (typeof types === 'object') {
			for (var type in types) {
				// we don't process space-separated events here for performance;
				// it's a hot path since Layer uses the on(obj) syntax
				this._on(type, types[type], fn);
			}

		} else {
			// types can be a string of space-separated words
			types = splitWords(types);

			for (var i = 0, len = types.length; i < len; i++) {
				this._on(types[i], fn, context);
			}
		}

		return this;
	},

	/* @method off(type: String, fn?: Function, context?: Object): this
	 * Removes a previously added listener function. If no function is specified, it will remove all the listeners of that particular event from the object. Note that if you passed a custom context to `on`, you must pass the same context to `off` in order to remove the listener.
	 *
	 * @alternative
	 * @method off(eventMap: Object): this
	 * Removes a set of type/listener pairs.
	 *
	 * @alternative
	 * @method off: this
	 * Removes all listeners to all events on the object.
	 */
	off: function (types, fn, context) {

		if (!types) {
			// clear all listeners if called without arguments
			delete this._events;

		} else if (typeof types === 'object') {
			for (var type in types) {
				this._off(type, types[type], fn);
			}

		} else {
			types = splitWords(types);

			for (var i = 0, len = types.length; i < len; i++) {
				this._off(types[i], fn, context);
			}
		}

		return this;
	},

	// attach listener (without syntactic sugar now)
	_on: function (type, fn, context) {
		this._events = this._events || {};

		/* get/init listeners for type */
		var typeListeners = this._events[type];
		if (!typeListeners) {
			typeListeners = [];
			this._events[type] = typeListeners;
		}

		if (context === this) {
			// Less memory footprint.
			context = undefined;
		}
		var newListener = {fn: fn, ctx: context},
		    listeners = typeListeners;

		// check if fn already there
		for (var i = 0, len = listeners.length; i < len; i++) {
			if (listeners[i].fn === fn && listeners[i].ctx === context) {
				return;
			}
		}

		listeners.push(newListener);
	},

	_off: function (type, fn, context) {
		var listeners,
		    i,
		    len;

		if (!this._events) { return; }

		listeners = this._events[type];

		if (!listeners) {
			return;
		}

		if (!fn) {
			// Set all removed listeners to noop so they are not called if remove happens in fire
			for (i = 0, len = listeners.length; i < len; i++) {
				listeners[i].fn = falseFn;
			}
			// clear all listeners for a type if function isn't specified
			delete this._events[type];
			return;
		}

		if (context === this) {
			context = undefined;
		}

		if (listeners) {

			// find fn and remove it
			for (i = 0, len = listeners.length; i < len; i++) {
				var l = listeners[i];
				if (l.ctx !== context) { continue; }
				if (l.fn === fn) {

					// set the removed listener to noop so that's not called if remove happens in fire
					l.fn = falseFn;

					if (this._firingCount) {
						/* copy array in case events are being fired */
						this._events[type] = listeners = listeners.slice();
					}
					listeners.splice(i, 1);

					return;
				}
			}
		}
	},

	// @method fire(type: String, data?: Object, propagate?: Boolean): this
	// Fires an event of the specified type. You can optionally provide an data
	// object — the first argument of the listener function will contain its
	// properties. The event can optionally be propagated to event parents.
	fire: function (type, data, propagate) {
		if (!this.listens(type, propagate)) { return this; }

		var event = extend({}, data, {type: type, target: this});

		if (this._events) {
			var listeners = this._events[type];

			if (listeners) {
				this._firingCount = (this._firingCount + 1) || 1;
				for (var i = 0, len = listeners.length; i < len; i++) {
					var l = listeners[i];
					l.fn.call(l.ctx || this, event);
				}

				this._firingCount--;
			}
		}

		if (propagate) {
			// propagate the event to parents (set with addEventParent)
			this._propagateEvent(event);
		}

		return this;
	},

	// @method listens(type: String): Boolean
	// Returns `true` if a particular event type has any listeners attached to it.
	listens: function (type, propagate) {
		var listeners = this._events && this._events[type];
		if (listeners && listeners.length) { return true; }

		if (propagate) {
			// also check parents for listeners if event propagates
			for (var id in this._eventParents) {
				if (this._eventParents[id].listens(type, propagate)) { return true; }
			}
		}
		return false;
	},

	// @method once(…): this
	// Behaves as [`on(…)`](#evented-on), except the listener will only get fired once and then removed.
	once: function (types, fn, context) {

		if (typeof types === 'object') {
			for (var type in types) {
				this.once(type, types[type], fn);
			}
			return this;
		}

		var handler = bind(function () {
			this
			    .off(types, fn, context)
			    .off(types, handler, context);
		}, this);

		// add a listener that's executed once and removed after that
		return this
		    .on(types, fn, context)
		    .on(types, handler, context);
	},

	// @method addEventParent(obj: Evented): this
	// Adds an event parent - an `Evented` that will receive propagated events
	addEventParent: function (obj) {
		this._eventParents = this._eventParents || {};
		this._eventParents[stamp(obj)] = obj;
		return this;
	},

	// @method removeEventParent(obj: Evented): this
	// Removes an event parent, so it will stop receiving propagated events
	removeEventParent: function (obj) {
		if (this._eventParents) {
			delete this._eventParents[stamp(obj)];
		}
		return this;
	},

	_propagateEvent: function (e) {
		for (var id in this._eventParents) {
			this._eventParents[id].fire(e.type, extend({layer: e.target}, e), true);
		}
	}
};

// aliases; we should ditch those eventually

// @method addEventListener(…): this
// Alias to [`on(…)`](#evented-on)
Events.addEventListener = Events.on;

// @method removeEventListener(…): this
// Alias to [`off(…)`](#evented-off)

// @method clearAllEventListeners(…): this
// Alias to [`off()`](#evented-off)
Events.removeEventListener = Events.clearAllEventListeners = Events.off;

// @method addOneTimeEventListener(…): this
// Alias to [`once(…)`](#evented-once)
Events.addOneTimeEventListener = Events.once;

// @method fireEvent(…): this
// Alias to [`fire(…)`](#evented-fire)
Events.fireEvent = Events.fire;

// @method hasEventListeners(…): Boolean
// Alias to [`listens(…)`](#evented-listens)
Events.hasEventListeners = Events.listens;

var Evented = Class.extend(Events);

/*
 * @class Point
 * @aka L.Point
 *
 * Represents a point with `x` and `y` coordinates in pixels.
 *
 * @example
 *
 * ```js
 * var point = L.point(200, 300);
 * ```
 *
 * All Leaflet methods and options that accept `Point` objects also accept them in a simple Array form (unless noted otherwise), so these lines are equivalent:
 *
 * ```js
 * map.panBy([200, 300]);
 * map.panBy(L.point(200, 300));
 * ```
 */

function Point(x, y, round) {
	// @property x: Number; The `x` coordinate of the point
	this.x = (round ? Math.round(x) : x);
	// @property y: Number; The `y` coordinate of the point
	this.y = (round ? Math.round(y) : y);
}

Point.prototype = {

	// @method clone(): Point
	// Returns a copy of the current point.
	clone: function () {
		return new Point(this.x, this.y);
	},

	// @method add(otherPoint: Point): Point
	// Returns the result of addition of the current and the given points.
	add: function (point) {
		// non-destructive, returns a new point
		return this.clone()._add(toPoint(point));
	},

	_add: function (point) {
		// destructive, used directly for performance in situations where it's safe to modify existing point
		this.x += point.x;
		this.y += point.y;
		return this;
	},

	// @method subtract(otherPoint: Point): Point
	// Returns the result of subtraction of the given point from the current.
	subtract: function (point) {
		return this.clone()._subtract(toPoint(point));
	},

	_subtract: function (point) {
		this.x -= point.x;
		this.y -= point.y;
		return this;
	},

	// @method divideBy(num: Number): Point
	// Returns the result of division of the current point by the given number.
	divideBy: function (num) {
		return this.clone()._divideBy(num);
	},

	_divideBy: function (num) {
		this.x /= num;
		this.y /= num;
		return this;
	},

	// @method multiplyBy(num: Number): Point
	// Returns the result of multiplication of the current point by the given number.
	multiplyBy: function (num) {
		return this.clone()._multiplyBy(num);
	},

	_multiplyBy: function (num) {
		this.x *= num;
		this.y *= num;
		return this;
	},

	// @method scaleBy(scale: Point): Point
	// Multiply each coordinate of the current point by each coordinate of
	// `scale`. In linear algebra terms, multiply the point by the
	// [scaling matrix](https://en.wikipedia.org/wiki/Scaling_%28geometry%29#Matrix_representation)
	// defined by `scale`.
	scaleBy: function (point) {
		return new Point(this.x * point.x, this.y * point.y);
	},

	// @method unscaleBy(scale: Point): Point
	// Inverse of `scaleBy`. Divide each coordinate of the current point by
	// each coordinate of `scale`.
	unscaleBy: function (point) {
		return new Point(this.x / point.x, this.y / point.y);
	},

	// @method round(): Point
	// Returns a copy of the current point with rounded coordinates.
	round: function () {
		return this.clone()._round();
	},

	_round: function () {
		this.x = Math.round(this.x);
		this.y = Math.round(this.y);
		return this;
	},

	// @method floor(): Point
	// Returns a copy of the current point with floored coordinates (rounded down).
	floor: function () {
		return this.clone()._floor();
	},

	_floor: function () {
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		return this;
	},

	// @method ceil(): Point
	// Returns a copy of the current point with ceiled coordinates (rounded up).
	ceil: function () {
		return this.clone()._ceil();
	},

	_ceil: function () {
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		return this;
	},

	// @method distanceTo(otherPoint: Point): Number
	// Returns the cartesian distance between the current and the given points.
	distanceTo: function (point) {
		point = toPoint(point);

		var x = point.x - this.x,
		    y = point.y - this.y;

		return Math.sqrt(x * x + y * y);
	},

	// @method equals(otherPoint: Point): Boolean
	// Returns `true` if the given point has the same coordinates.
	equals: function (point) {
		point = toPoint(point);

		return point.x === this.x &&
		       point.y === this.y;
	},

	// @method contains(otherPoint: Point): Boolean
	// Returns `true` if both coordinates of the given point are less than the corresponding current point coordinates (in absolute values).
	contains: function (point) {
		point = toPoint(point);

		return Math.abs(point.x) <= Math.abs(this.x) &&
		       Math.abs(point.y) <= Math.abs(this.y);
	},

	// @method toString(): String
	// Returns a string representation of the point for debugging purposes.
	toString: function () {
		return 'Point(' +
		        formatNum(this.x) + ', ' +
		        formatNum(this.y) + ')';
	}
};

// @factory L.point(x: Number, y: Number, round?: Boolean)
// Creates a Point object with the given `x` and `y` coordinates. If optional `round` is set to true, rounds the `x` and `y` values.

// @alternative
// @factory L.point(coords: Number[])
// Expects an array of the form `[x, y]` instead.

// @alternative
// @factory L.point(coords: Object)
// Expects a plain object of the form `{x: Number, y: Number}` instead.
function toPoint(x, y, round) {
	if (x instanceof Point) {
		return x;
	}
	if (isArray(x)) {
		return new Point(x[0], x[1]);
	}
	if (x === undefined || x === null) {
		return x;
	}
	if (typeof x === 'object' && 'x' in x && 'y' in x) {
		return new Point(x.x, x.y);
	}
	return new Point(x, y, round);
}

/*
 * @class Bounds
 * @aka L.Bounds
 *
 * Represents a rectangular area in pixel coordinates.
 *
 * @example
 *
 * ```js
 * var p1 = L.point(10, 10),
 * p2 = L.point(40, 60),
 * bounds = L.bounds(p1, p2);
 * ```
 *
 * All Leaflet methods that accept `Bounds` objects also accept them in a simple Array form (unless noted otherwise), so the bounds example above can be passed like this:
 *
 * ```js
 * otherBounds.intersects([[10, 10], [40, 60]]);
 * ```
 */

function Bounds(a, b) {
	if (!a) { return; }

	var points = b ? [a, b] : a;

	for (var i = 0, len = points.length; i < len; i++) {
		this.extend(points[i]);
	}
}

Bounds.prototype = {
	// @method extend(point: Point): this
	// Extends the bounds to contain the given point.
	extend: function (point) { // (Point)
		point = toPoint(point);

		// @property min: Point
		// The top left corner of the rectangle.
		// @property max: Point
		// The bottom right corner of the rectangle.
		if (!this.min && !this.max) {
			this.min = point.clone();
			this.max = point.clone();
		} else {
			this.min.x = Math.min(point.x, this.min.x);
			this.max.x = Math.max(point.x, this.max.x);
			this.min.y = Math.min(point.y, this.min.y);
			this.max.y = Math.max(point.y, this.max.y);
		}
		return this;
	},

	// @method getCenter(round?: Boolean): Point
	// Returns the center point of the bounds.
	getCenter: function (round) {
		return new Point(
		        (this.min.x + this.max.x) / 2,
		        (this.min.y + this.max.y) / 2, round);
	},

	// @method getBottomLeft(): Point
	// Returns the bottom-left point of the bounds.
	getBottomLeft: function () {
		return new Point(this.min.x, this.max.y);
	},

	// @method getTopRight(): Point
	// Returns the top-right point of the bounds.
	getTopRight: function () { // -> Point
		return new Point(this.max.x, this.min.y);
	},

	// @method getTopLeft(): Point
	// Returns the top-left point of the bounds (i.e. [`this.min`](#bounds-min)).
	getTopLeft: function () {
		return this.min; // left, top
	},

	// @method getBottomRight(): Point
	// Returns the bottom-right point of the bounds (i.e. [`this.max`](#bounds-max)).
	getBottomRight: function () {
		return this.max; // right, bottom
	},

	// @method getSize(): Point
	// Returns the size of the given bounds
	getSize: function () {
		return this.max.subtract(this.min);
	},

	// @method contains(otherBounds: Bounds): Boolean
	// Returns `true` if the rectangle contains the given one.
	// @alternative
	// @method contains(point: Point): Boolean
	// Returns `true` if the rectangle contains the given point.
	contains: function (obj) {
		var min, max;

		if (typeof obj[0] === 'number' || obj instanceof Point) {
			obj = toPoint(obj);
		} else {
			obj = toBounds(obj);
		}

		if (obj instanceof Bounds) {
			min = obj.min;
			max = obj.max;
		} else {
			min = max = obj;
		}

		return (min.x >= this.min.x) &&
		       (max.x <= this.max.x) &&
		       (min.y >= this.min.y) &&
		       (max.y <= this.max.y);
	},

	// @method intersects(otherBounds: Bounds): Boolean
	// Returns `true` if the rectangle intersects the given bounds. Two bounds
	// intersect if they have at least one point in common.
	intersects: function (bounds) { // (Bounds) -> Boolean
		bounds = toBounds(bounds);

		var min = this.min,
		    max = this.max,
		    min2 = bounds.min,
		    max2 = bounds.max,
		    xIntersects = (max2.x >= min.x) && (min2.x <= max.x),
		    yIntersects = (max2.y >= min.y) && (min2.y <= max.y);

		return xIntersects && yIntersects;
	},

	// @method overlaps(otherBounds: Bounds): Boolean
	// Returns `true` if the rectangle overlaps the given bounds. Two bounds
	// overlap if their intersection is an area.
	overlaps: function (bounds) { // (Bounds) -> Boolean
		bounds = toBounds(bounds);

		var min = this.min,
		    max = this.max,
		    min2 = bounds.min,
		    max2 = bounds.max,
		    xOverlaps = (max2.x > min.x) && (min2.x < max.x),
		    yOverlaps = (max2.y > min.y) && (min2.y < max.y);

		return xOverlaps && yOverlaps;
	},

	isValid: function () {
		return !!(this.min && this.max);
	}
};


// @factory L.bounds(corner1: Point, corner2: Point)
// Creates a Bounds object from two corners coordinate pairs.
// @alternative
// @factory L.bounds(points: Point[])
// Creates a Bounds object from the given array of points.
function toBounds(a, b) {
	if (!a || a instanceof Bounds) {
		return a;
	}
	return new Bounds(a, b);
}

/*
 * @class LatLngBounds
 * @aka L.LatLngBounds
 *
 * Represents a rectangular geographical area on a map.
 *
 * @example
 *
 * ```js
 * var corner1 = L.latLng(40.712, -74.227),
 * corner2 = L.latLng(40.774, -74.125),
 * bounds = L.latLngBounds(corner1, corner2);
 * ```
 *
 * All Leaflet methods that accept LatLngBounds objects also accept them in a simple Array form (unless noted otherwise), so the bounds example above can be passed like this:
 *
 * ```js
 * map.fitBounds([
 * 	[40.712, -74.227],
 * 	[40.774, -74.125]
 * ]);
 * ```
 *
 * Caution: if the area crosses the antimeridian (often confused with the International Date Line), you must specify corners _outside_ the [-180, 180] degrees longitude range.
 */

function LatLngBounds(corner1, corner2) { // (LatLng, LatLng) or (LatLng[])
	if (!corner1) { return; }

	var latlngs = corner2 ? [corner1, corner2] : corner1;

	for (var i = 0, len = latlngs.length; i < len; i++) {
		this.extend(latlngs[i]);
	}
}

LatLngBounds.prototype = {

	// @method extend(latlng: LatLng): this
	// Extend the bounds to contain the given point

	// @alternative
	// @method extend(otherBounds: LatLngBounds): this
	// Extend the bounds to contain the given bounds
	extend: function (obj) {
		var sw = this._southWest,
		    ne = this._northEast,
		    sw2, ne2;

		if (obj instanceof LatLng) {
			sw2 = obj;
			ne2 = obj;

		} else if (obj instanceof LatLngBounds) {
			sw2 = obj._southWest;
			ne2 = obj._northEast;

			if (!sw2 || !ne2) { return this; }

		} else {
			return obj ? this.extend(toLatLng(obj) || toLatLngBounds(obj)) : this;
		}

		if (!sw && !ne) {
			this._southWest = new LatLng(sw2.lat, sw2.lng);
			this._northEast = new LatLng(ne2.lat, ne2.lng);
		} else {
			sw.lat = Math.min(sw2.lat, sw.lat);
			sw.lng = Math.min(sw2.lng, sw.lng);
			ne.lat = Math.max(ne2.lat, ne.lat);
			ne.lng = Math.max(ne2.lng, ne.lng);
		}

		return this;
	},

	// @method pad(bufferRatio: Number): LatLngBounds
	// Returns bigger bounds created by extending the current bounds by a given percentage in each direction.
	pad: function (bufferRatio) {
		var sw = this._southWest,
		    ne = this._northEast,
		    heightBuffer = Math.abs(sw.lat - ne.lat) * bufferRatio,
		    widthBuffer = Math.abs(sw.lng - ne.lng) * bufferRatio;

		return new LatLngBounds(
		        new LatLng(sw.lat - heightBuffer, sw.lng - widthBuffer),
		        new LatLng(ne.lat + heightBuffer, ne.lng + widthBuffer));
	},

	// @method getCenter(): LatLng
	// Returns the center point of the bounds.
	getCenter: function () {
		return new LatLng(
		        (this._southWest.lat + this._northEast.lat) / 2,
		        (this._southWest.lng + this._northEast.lng) / 2);
	},

	// @method getSouthWest(): LatLng
	// Returns the south-west point of the bounds.
	getSouthWest: function () {
		return this._southWest;
	},

	// @method getNorthEast(): LatLng
	// Returns the north-east point of the bounds.
	getNorthEast: function () {
		return this._northEast;
	},

	// @method getNorthWest(): LatLng
	// Returns the north-west point of the bounds.
	getNorthWest: function () {
		return new LatLng(this.getNorth(), this.getWest());
	},

	// @method getSouthEast(): LatLng
	// Returns the south-east point of the bounds.
	getSouthEast: function () {
		return new LatLng(this.getSouth(), this.getEast());
	},

	// @method getWest(): Number
	// Returns the west longitude of the bounds
	getWest: function () {
		return this._southWest.lng;
	},

	// @method getSouth(): Number
	// Returns the south latitude of the bounds
	getSouth: function () {
		return this._southWest.lat;
	},

	// @method getEast(): Number
	// Returns the east longitude of the bounds
	getEast: function () {
		return this._northEast.lng;
	},

	// @method getNorth(): Number
	// Returns the north latitude of the bounds
	getNorth: function () {
		return this._northEast.lat;
	},

	// @method contains(otherBounds: LatLngBounds): Boolean
	// Returns `true` if the rectangle contains the given one.

	// @alternative
	// @method contains (latlng: LatLng): Boolean
	// Returns `true` if the rectangle contains the given point.
	contains: function (obj) { // (LatLngBounds) or (LatLng) -> Boolean
		if (typeof obj[0] === 'number' || obj instanceof LatLng || 'lat' in obj) {
			obj = toLatLng(obj);
		} else {
			obj = toLatLngBounds(obj);
		}

		var sw = this._southWest,
		    ne = this._northEast,
		    sw2, ne2;

		if (obj instanceof LatLngBounds) {
			sw2 = obj.getSouthWest();
			ne2 = obj.getNorthEast();
		} else {
			sw2 = ne2 = obj;
		}

		return (sw2.lat >= sw.lat) && (ne2.lat <= ne.lat) &&
		       (sw2.lng >= sw.lng) && (ne2.lng <= ne.lng);
	},

	// @method intersects(otherBounds: LatLngBounds): Boolean
	// Returns `true` if the rectangle intersects the given bounds. Two bounds intersect if they have at least one point in common.
	intersects: function (bounds) {
		bounds = toLatLngBounds(bounds);

		var sw = this._southWest,
		    ne = this._northEast,
		    sw2 = bounds.getSouthWest(),
		    ne2 = bounds.getNorthEast(),

		    latIntersects = (ne2.lat >= sw.lat) && (sw2.lat <= ne.lat),
		    lngIntersects = (ne2.lng >= sw.lng) && (sw2.lng <= ne.lng);

		return latIntersects && lngIntersects;
	},

	// @method overlaps(otherBounds: Bounds): Boolean
	// Returns `true` if the rectangle overlaps the given bounds. Two bounds overlap if their intersection is an area.
	overlaps: function (bounds) {
		bounds = toLatLngBounds(bounds);

		var sw = this._southWest,
		    ne = this._northEast,
		    sw2 = bounds.getSouthWest(),
		    ne2 = bounds.getNorthEast(),

		    latOverlaps = (ne2.lat > sw.lat) && (sw2.lat < ne.lat),
		    lngOverlaps = (ne2.lng > sw.lng) && (sw2.lng < ne.lng);

		return latOverlaps && lngOverlaps;
	},

	// @method toBBoxString(): String
	// Returns a string with bounding box coordinates in a 'southwest_lng,southwest_lat,northeast_lng,northeast_lat' format. Useful for sending requests to web services that return geo data.
	toBBoxString: function () {
		return [this.getWest(), this.getSouth(), this.getEast(), this.getNorth()].join(',');
	},

	// @method equals(otherBounds: LatLngBounds, maxMargin?: Number): Boolean
	// Returns `true` if the rectangle is equivalent (within a small margin of error) to the given bounds. The margin of error can be overriden by setting `maxMargin` to a small number.
	equals: function (bounds, maxMargin) {
		if (!bounds) { return false; }

		bounds = toLatLngBounds(bounds);

		return this._southWest.equals(bounds.getSouthWest(), maxMargin) &&
		       this._northEast.equals(bounds.getNorthEast(), maxMargin);
	},

	// @method isValid(): Boolean
	// Returns `true` if the bounds are properly initialized.
	isValid: function () {
		return !!(this._southWest && this._northEast);
	}
};

// TODO International date line?

// @factory L.latLngBounds(corner1: LatLng, corner2: LatLng)
// Creates a `LatLngBounds` object by defining two diagonally opposite corners of the rectangle.

// @alternative
// @factory L.latLngBounds(latlngs: LatLng[])
// Creates a `LatLngBounds` object defined by the geographical points it contains. Very useful for zooming the map to fit a particular set of locations with [`fitBounds`](#map-fitbounds).
function toLatLngBounds(a, b) {
	if (a instanceof LatLngBounds) {
		return a;
	}
	return new LatLngBounds(a, b);
}

/* @class LatLng
 * @aka L.LatLng
 *
 * Represents a geographical point with a certain latitude and longitude.
 *
 * @example
 *
 * ```
 * var latlng = L.latLng(50.5, 30.5);
 * ```
 *
 * All Leaflet methods that accept LatLng objects also accept them in a simple Array form and simple object form (unless noted otherwise), so these lines are equivalent:
 *
 * ```
 * map.panTo([50, 30]);
 * map.panTo({lon: 30, lat: 50});
 * map.panTo({lat: 50, lng: 30});
 * map.panTo(L.latLng(50, 30));
 * ```
 */

function LatLng(lat, lng, alt) {
	if (isNaN(lat) || isNaN(lng)) {
		throw new Error('Invalid LatLng object: (' + lat + ', ' + lng + ')');
	}

	// @property lat: Number
	// Latitude in degrees
	this.lat = +lat;

	// @property lng: Number
	// Longitude in degrees
	this.lng = +lng;

	// @property alt: Number
	// Altitude in meters (optional)
	if (alt !== undefined) {
		this.alt = +alt;
	}
}

LatLng.prototype = {
	// @method equals(otherLatLng: LatLng, maxMargin?: Number): Boolean
	// Returns `true` if the given `LatLng` point is at the same position (within a small margin of error). The margin of error can be overriden by setting `maxMargin` to a small number.
	equals: function (obj, maxMargin) {
		if (!obj) { return false; }

		obj = toLatLng(obj);

		var margin = Math.max(
		        Math.abs(this.lat - obj.lat),
		        Math.abs(this.lng - obj.lng));

		return margin <= (maxMargin === undefined ? 1.0E-9 : maxMargin);
	},

	// @method toString(): String
	// Returns a string representation of the point (for debugging purposes).
	toString: function (precision) {
		return 'LatLng(' +
		        formatNum(this.lat, precision) + ', ' +
		        formatNum(this.lng, precision) + ')';
	},

	// @method distanceTo(otherLatLng: LatLng): Number
	// Returns the distance (in meters) to the given `LatLng` calculated using the [Haversine formula](http://en.wikipedia.org/wiki/Haversine_formula).
	distanceTo: function (other) {
		return Earth.distance(this, toLatLng(other));
	},

	// @method wrap(): LatLng
	// Returns a new `LatLng` object with the longitude wrapped so it's always between -180 and +180 degrees.
	wrap: function () {
		return Earth.wrapLatLng(this);
	},

	// @method toBounds(sizeInMeters: Number): LatLngBounds
	// Returns a new `LatLngBounds` object in which each boundary is `sizeInMeters/2` meters apart from the `LatLng`.
	toBounds: function (sizeInMeters) {
		var latAccuracy = 180 * sizeInMeters / 40075017,
		    lngAccuracy = latAccuracy / Math.cos((Math.PI / 180) * this.lat);

		return toLatLngBounds(
		        [this.lat - latAccuracy, this.lng - lngAccuracy],
		        [this.lat + latAccuracy, this.lng + lngAccuracy]);
	},

	clone: function () {
		return new LatLng(this.lat, this.lng, this.alt);
	}
};



// @factory L.latLng(latitude: Number, longitude: Number, altitude?: Number): LatLng
// Creates an object representing a geographical point with the given latitude and longitude (and optionally altitude).

// @alternative
// @factory L.latLng(coords: Array): LatLng
// Expects an array of the form `[Number, Number]` or `[Number, Number, Number]` instead.

// @alternative
// @factory L.latLng(coords: Object): LatLng
// Expects an plain object of the form `{lat: Number, lng: Number}` or `{lat: Number, lng: Number, alt: Number}` instead.

function toLatLng(a, b, c) {
	if (a instanceof LatLng) {
		return a;
	}
	if (isArray(a) && typeof a[0] !== 'object') {
		if (a.length === 3) {
			return new LatLng(a[0], a[1], a[2]);
		}
		if (a.length === 2) {
			return new LatLng(a[0], a[1]);
		}
		return null;
	}
	if (a === undefined || a === null) {
		return a;
	}
	if (typeof a === 'object' && 'lat' in a) {
		return new LatLng(a.lat, 'lng' in a ? a.lng : a.lon, a.alt);
	}
	if (b === undefined) {
		return null;
	}
	return new LatLng(a, b, c);
}

/*
 * @namespace CRS
 * @crs L.CRS.Base
 * Object that defines coordinate reference systems for projecting
 * geographical points into pixel (screen) coordinates and back (and to
 * coordinates in other units for [WMS](https://en.wikipedia.org/wiki/Web_Map_Service) services). See
 * [spatial reference system](http://en.wikipedia.org/wiki/Coordinate_reference_system).
 *
 * Leaflet defines the most usual CRSs by default. If you want to use a
 * CRS not defined by default, take a look at the
 * [Proj4Leaflet](https://github.com/kartena/Proj4Leaflet) plugin.
 */

var CRS = {
	// @method latLngToPoint(latlng: LatLng, zoom: Number): Point
	// Projects geographical coordinates into pixel coordinates for a given zoom.
	latLngToPoint: function (latlng, zoom) {
		var projectedPoint = this.projection.project(latlng),
		    scale = this.scale(zoom);

		return this.transformation._transform(projectedPoint, scale);
	},

	// @method pointToLatLng(point: Point, zoom: Number): LatLng
	// The inverse of `latLngToPoint`. Projects pixel coordinates on a given
	// zoom into geographical coordinates.
	pointToLatLng: function (point, zoom) {
		var scale = this.scale(zoom),
		    untransformedPoint = this.transformation.untransform(point, scale);

		return this.projection.unproject(untransformedPoint);
	},

	// @method project(latlng: LatLng): Point
	// Projects geographical coordinates into coordinates in units accepted for
	// this CRS (e.g. meters for EPSG:3857, for passing it to WMS services).
	project: function (latlng) {
		return this.projection.project(latlng);
	},

	// @method unproject(point: Point): LatLng
	// Given a projected coordinate returns the corresponding LatLng.
	// The inverse of `project`.
	unproject: function (point) {
		return this.projection.unproject(point);
	},

	// @method scale(zoom: Number): Number
	// Returns the scale used when transforming projected coordinates into
	// pixel coordinates for a particular zoom. For example, it returns
	// `256 * 2^zoom` for Mercator-based CRS.
	scale: function (zoom) {
		return 256 * Math.pow(2, zoom);
	},

	// @method zoom(scale: Number): Number
	// Inverse of `scale()`, returns the zoom level corresponding to a scale
	// factor of `scale`.
	zoom: function (scale) {
		return Math.log(scale / 256) / Math.LN2;
	},

	// @method getProjectedBounds(zoom: Number): Bounds
	// Returns the projection's bounds scaled and transformed for the provided `zoom`.
	getProjectedBounds: function (zoom) {
		if (this.infinite) { return null; }

		var b = this.projection.bounds,
		    s = this.scale(zoom),
		    min = this.transformation.transform(b.min, s),
		    max = this.transformation.transform(b.max, s);

		return new Bounds(min, max);
	},

	// @method distance(latlng1: LatLng, latlng2: LatLng): Number
	// Returns the distance between two geographical coordinates.

	// @property code: String
	// Standard code name of the CRS passed into WMS services (e.g. `'EPSG:3857'`)
	//
	// @property wrapLng: Number[]
	// An array of two numbers defining whether the longitude (horizontal) coordinate
	// axis wraps around a given range and how. Defaults to `[-180, 180]` in most
	// geographical CRSs. If `undefined`, the longitude axis does not wrap around.
	//
	// @property wrapLat: Number[]
	// Like `wrapLng`, but for the latitude (vertical) axis.

	// wrapLng: [min, max],
	// wrapLat: [min, max],

	// @property infinite: Boolean
	// If true, the coordinate space will be unbounded (infinite in both axes)
	infinite: false,

	// @method wrapLatLng(latlng: LatLng): LatLng
	// Returns a `LatLng` where lat and lng has been wrapped according to the
	// CRS's `wrapLat` and `wrapLng` properties, if they are outside the CRS's bounds.
	wrapLatLng: function (latlng) {
		var lng = this.wrapLng ? wrapNum(latlng.lng, this.wrapLng, true) : latlng.lng,
		    lat = this.wrapLat ? wrapNum(latlng.lat, this.wrapLat, true) : latlng.lat,
		    alt = latlng.alt;

		return new LatLng(lat, lng, alt);
	},

	// @method wrapLatLngBounds(bounds: LatLngBounds): LatLngBounds
	// Returns a `LatLngBounds` with the same size as the given one, ensuring
	// that its center is within the CRS's bounds.
	// Only accepts actual `L.LatLngBounds` instances, not arrays.
	wrapLatLngBounds: function (bounds) {
		var center = bounds.getCenter(),
		    newCenter = this.wrapLatLng(center),
		    latShift = center.lat - newCenter.lat,
		    lngShift = center.lng - newCenter.lng;

		if (latShift === 0 && lngShift === 0) {
			return bounds;
		}

		var sw = bounds.getSouthWest(),
		    ne = bounds.getNorthEast(),
		    newSw = new LatLng(sw.lat - latShift, sw.lng - lngShift),
		    newNe = new LatLng(ne.lat - latShift, ne.lng - lngShift);

		return new LatLngBounds(newSw, newNe);
	}
};

/*
 * @namespace CRS
 * @crs L.CRS.Earth
 *
 * Serves as the base for CRS that are global such that they cover the earth.
 * Can only be used as the base for other CRS and cannot be used directly,
 * since it does not have a `code`, `projection` or `transformation`. `distance()` returns
 * meters.
 */

var Earth = extend({}, CRS, {
	wrapLng: [-180, 180],

	// Mean Earth Radius, as recommended for use by
	// the International Union of Geodesy and Geophysics,
	// see http://rosettacode.org/wiki/Haversine_formula
	R: 6371000,

	// distance between two geographical points using spherical law of cosines approximation
	distance: function (latlng1, latlng2) {
		var rad = Math.PI / 180,
		    lat1 = latlng1.lat * rad,
		    lat2 = latlng2.lat * rad,
		    a = Math.sin(lat1) * Math.sin(lat2) +
		        Math.cos(lat1) * Math.cos(lat2) * Math.cos((latlng2.lng - latlng1.lng) * rad);

		return this.R * Math.acos(Math.min(a, 1));
	}
});

/*
 * @namespace Projection
 * @projection L.Projection.SphericalMercator
 *
 * Spherical Mercator projection — the most common projection for online maps,
 * used by almost all free and commercial tile providers. Assumes that Earth is
 * a sphere. Used by the `EPSG:3857` CRS.
 */

var SphericalMercator = {

	R: 6378137,
	MAX_LATITUDE: 85.0511287798,

	project: function (latlng) {
		var d = Math.PI / 180,
		    max = this.MAX_LATITUDE,
		    lat = Math.max(Math.min(max, latlng.lat), -max),
		    sin = Math.sin(lat * d);

		return new Point(
				this.R * latlng.lng * d,
				this.R * Math.log((1 + sin) / (1 - sin)) / 2);
	},

	unproject: function (point) {
		var d = 180 / Math.PI;

		return new LatLng(
			(2 * Math.atan(Math.exp(point.y / this.R)) - (Math.PI / 2)) * d,
			point.x * d / this.R);
	},

	bounds: (function () {
		var d = 6378137 * Math.PI;
		return new Bounds([-d, -d], [d, d]);
	})()
};

/*
 * @class Transformation
 * @aka L.Transformation
 *
 * Represents an affine transformation: a set of coefficients `a`, `b`, `c`, `d`
 * for transforming a point of a form `(x, y)` into `(a*x + b, c*y + d)` and doing
 * the reverse. Used by Leaflet in its projections code.
 *
 * @example
 *
 * ```js
 * var transformation = L.transformation(2, 5, -1, 10),
 * 	p = L.point(1, 2),
 * 	p2 = transformation.transform(p), //  L.point(7, 8)
 * 	p3 = transformation.untransform(p2); //  L.point(1, 2)
 * ```
 */


// factory new L.Transformation(a: Number, b: Number, c: Number, d: Number)
// Creates a `Transformation` object with the given coefficients.
function Transformation(a, b, c, d) {
	if (isArray(a)) {
		// use array properties
		this._a = a[0];
		this._b = a[1];
		this._c = a[2];
		this._d = a[3];
		return;
	}
	this._a = a;
	this._b = b;
	this._c = c;
	this._d = d;
}

Transformation.prototype = {
	// @method transform(point: Point, scale?: Number): Point
	// Returns a transformed point, optionally multiplied by the given scale.
	// Only accepts actual `L.Point` instances, not arrays.
	transform: function (point, scale) { // (Point, Number) -> Point
		return this._transform(point.clone(), scale);
	},

	// destructive transform (faster)
	_transform: function (point, scale) {
		scale = scale || 1;
		point.x = scale * (this._a * point.x + this._b);
		point.y = scale * (this._c * point.y + this._d);
		return point;
	},

	// @method untransform(point: Point, scale?: Number): Point
	// Returns the reverse transformation of the given point, optionally divided
	// by the given scale. Only accepts actual `L.Point` instances, not arrays.
	untransform: function (point, scale) {
		scale = scale || 1;
		return new Point(
		        (point.x / scale - this._b) / this._a,
		        (point.y / scale - this._d) / this._c);
	}
};

// factory L.transformation(a: Number, b: Number, c: Number, d: Number)

// @factory L.transformation(a: Number, b: Number, c: Number, d: Number)
// Instantiates a Transformation object with the given coefficients.

// @alternative
// @factory L.transformation(coefficients: Array): Transformation
// Expects an coeficients array of the form
// `[a: Number, b: Number, c: Number, d: Number]`.

function toTransformation(a, b, c, d) {
	return new Transformation(a, b, c, d);
}

/*
 * @namespace CRS
 * @crs L.CRS.EPSG3857
 *
 * The most common CRS for online maps, used by almost all free and commercial
 * tile providers. Uses Spherical Mercator projection. Set in by default in
 * Map's `crs` option.
 */

var EPSG3857 = extend({}, Earth, {
	code: 'EPSG:3857',
	projection: SphericalMercator,

	transformation: (function () {
		var scale = 0.5 / (Math.PI * SphericalMercator.R);
		return toTransformation(scale, 0.5, -scale, 0.5);
	}())
});

var EPSG900913 = extend({}, EPSG3857, {
	code: 'EPSG:900913'
});

// @namespace SVG; @section
// There are several static functions which can be called without instantiating L.SVG:

// @function create(name: String): SVGElement
// Returns a instance of [SVGElement](https://developer.mozilla.org/docs/Web/API/SVGElement),
// corresponding to the class name passed. For example, using 'line' will return
// an instance of [SVGLineElement](https://developer.mozilla.org/docs/Web/API/SVGLineElement).
function svgCreate(name) {
	return document.createElementNS('http://www.w3.org/2000/svg', name);
}

// @function pointsToPath(rings: Point[], closed: Boolean): String
// Generates a SVG path string for multiple rings, with each ring turning
// into "M..L..L.." instructions
function pointsToPath(rings, closed) {
	var str = '',
	i, j, len, len2, points, p;

	for (i = 0, len = rings.length; i < len; i++) {
		points = rings[i];

		for (j = 0, len2 = points.length; j < len2; j++) {
			p = points[j];
			str += (j ? 'L' : 'M') + p.x + ' ' + p.y;
		}

		// closes the ring for polygons; "x" is VML syntax
		str += closed ? (svg ? 'z' : 'x') : '';
	}

	// SVG complains about empty path strings
	return str || 'M0 0';
}

/*
 * @namespace Browser
 * @aka L.Browser
 *
 * A namespace with static properties for browser/feature detection used by Leaflet internally.
 *
 * @example
 *
 * ```js
 * if (L.Browser.ielt9) {
 *   alert('Upgrade your browser, dude!');
 * }
 * ```
 */

var style$1 = document.documentElement.style;

// @property ie: Boolean; `true` for all Internet Explorer versions (not Edge).
var ie = 'ActiveXObject' in window;

// @property ielt9: Boolean; `true` for Internet Explorer versions less than 9.
var ielt9 = ie && !document.addEventListener;

// @property edge: Boolean; `true` for the Edge web browser.
var edge = 'msLaunchUri' in navigator && !('documentMode' in document);

// @property webkit: Boolean;
// `true` for webkit-based browsers like Chrome and Safari (including mobile versions).
var webkit = userAgentContains('webkit');

// @property android: Boolean
// `true` for any browser running on an Android platform.
var android = userAgentContains('android');

// @property android23: Boolean; `true` for browsers running on Android 2 or Android 3.
var android23 = userAgentContains('android 2') || userAgentContains('android 3');

// @property opera: Boolean; `true` for the Opera browser
var opera = !!window.opera;

// @property chrome: Boolean; `true` for the Chrome browser.
var chrome = userAgentContains('chrome');

// @property gecko: Boolean; `true` for gecko-based browsers like Firefox.
var gecko = userAgentContains('gecko') && !webkit && !opera && !ie;

// @property safari: Boolean; `true` for the Safari browser.
var safari = !chrome && userAgentContains('safari');

var phantom = userAgentContains('phantom');

// @property opera12: Boolean
// `true` for the Opera browser supporting CSS transforms (version 12 or later).
var opera12 = 'OTransition' in style$1;

// @property win: Boolean; `true` when the browser is running in a Windows platform
var win = navigator.platform.indexOf('Win') === 0;

// @property ie3d: Boolean; `true` for all Internet Explorer versions supporting CSS transforms.
var ie3d = ie && ('transition' in style$1);

// @property webkit3d: Boolean; `true` for webkit-based browsers supporting CSS transforms.
var webkit3d = ('WebKitCSSMatrix' in window) && ('m11' in new window.WebKitCSSMatrix()) && !android23;

// @property gecko3d: Boolean; `true` for gecko-based browsers supporting CSS transforms.
var gecko3d = 'MozPerspective' in style$1;

// @property any3d: Boolean
// `true` for all browsers supporting CSS transforms.
var any3d = !window.L_DISABLE_3D && (ie3d || webkit3d || gecko3d) && !opera12 && !phantom;

// @property mobile: Boolean; `true` for all browsers running in a mobile device.
var mobile = typeof orientation !== 'undefined' || userAgentContains('mobile');

// @property mobileWebkit: Boolean; `true` for all webkit-based browsers in a mobile device.
var mobileWebkit = mobile && webkit;

// @property mobileWebkit3d: Boolean
// `true` for all webkit-based browsers in a mobile device supporting CSS transforms.
var mobileWebkit3d = mobile && webkit3d;

// @property msPointer: Boolean
// `true` for browsers implementing the Microsoft touch events model (notably IE10).
var msPointer = !window.PointerEvent && window.MSPointerEvent;

// @property pointer: Boolean
// `true` for all browsers supporting [pointer events](https://msdn.microsoft.com/en-us/library/dn433244%28v=vs.85%29.aspx).
var pointer = !!(window.PointerEvent || msPointer);

// @property touch: Boolean
// `true` for all browsers supporting [touch events](https://developer.mozilla.org/docs/Web/API/Touch_events).
// This does not necessarily mean that the browser is running in a computer with
// a touchscreen, it only means that the browser is capable of understanding
// touch events.
var touch = !window.L_NO_TOUCH && (pointer || 'ontouchstart' in window ||
		(window.DocumentTouch && document instanceof window.DocumentTouch));

// @property mobileOpera: Boolean; `true` for the Opera browser in a mobile device.
var mobileOpera = mobile && opera;

// @property mobileGecko: Boolean
// `true` for gecko-based browsers running in a mobile device.
var mobileGecko = mobile && gecko;

// @property retina: Boolean
// `true` for browsers on a high-resolution "retina" screen.
var retina = (window.devicePixelRatio || (window.screen.deviceXDPI / window.screen.logicalXDPI)) > 1;


// @property canvas: Boolean
// `true` when the browser supports [`<canvas>`](https://developer.mozilla.org/docs/Web/API/Canvas_API).
var canvas = (function () {
	return !!document.createElement('canvas').getContext;
}());

// @property svg: Boolean
// `true` when the browser supports [SVG](https://developer.mozilla.org/docs/Web/SVG).
var svg = !!(document.createElementNS && svgCreate('svg').createSVGRect);

// @property vml: Boolean
// `true` if the browser supports [VML](https://en.wikipedia.org/wiki/Vector_Markup_Language).
var vml = !svg && (function () {
	try {
		var div = document.createElement('div');
		div.innerHTML = '<v:shape adj="1"/>';

		var shape = div.firstChild;
		shape.style.behavior = 'url(#default#VML)';

		return shape && (typeof shape.adj === 'object');

	} catch (e) {
		return false;
	}
}());


function userAgentContains(str) {
	return navigator.userAgent.toLowerCase().indexOf(str) >= 0;
}


var Browser = (Object.freeze || Object)({
	ie: ie,
	ielt9: ielt9,
	edge: edge,
	webkit: webkit,
	android: android,
	android23: android23,
	opera: opera,
	chrome: chrome,
	gecko: gecko,
	safari: safari,
	phantom: phantom,
	opera12: opera12,
	win: win,
	ie3d: ie3d,
	webkit3d: webkit3d,
	gecko3d: gecko3d,
	any3d: any3d,
	mobile: mobile,
	mobileWebkit: mobileWebkit,
	mobileWebkit3d: mobileWebkit3d,
	msPointer: msPointer,
	pointer: pointer,
	touch: touch,
	mobileOpera: mobileOpera,
	mobileGecko: mobileGecko,
	retina: retina,
	canvas: canvas,
	svg: svg,
	vml: vml
});

/*
 * Extends L.DomEvent to provide touch support for Internet Explorer and Windows-based devices.
 */


var POINTER_DOWN =   msPointer ? 'MSPointerDown'   : 'pointerdown';
var POINTER_MOVE =   msPointer ? 'MSPointerMove'   : 'pointermove';
var POINTER_UP =     msPointer ? 'MSPointerUp'     : 'pointerup';
var POINTER_CANCEL = msPointer ? 'MSPointerCancel' : 'pointercancel';
var TAG_WHITE_LIST = ['INPUT', 'SELECT', 'OPTION'];
var _pointers = {};
var _pointerDocListener = false;

// DomEvent.DoubleTap needs to know about this
var _pointersCount = 0;

// Provides a touch events wrapper for (ms)pointer events.
// ref http://www.w3.org/TR/pointerevents/ https://www.w3.org/Bugs/Public/show_bug.cgi?id=22890

function addPointerListener(obj, type, handler, id) {
	if (type === 'touchstart') {
		_addPointerStart(obj, handler, id);

	} else if (type === 'touchmove') {
		_addPointerMove(obj, handler, id);

	} else if (type === 'touchend') {
		_addPointerEnd(obj, handler, id);
	}

	return this;
}

function removePointerListener(obj, type, id) {
	var handler = obj['_leaflet_' + type + id];

	if (type === 'touchstart') {
		obj.removeEventListener(POINTER_DOWN, handler, false);

	} else if (type === 'touchmove') {
		obj.removeEventListener(POINTER_MOVE, handler, false);

	} else if (type === 'touchend') {
		obj.removeEventListener(POINTER_UP, handler, false);
		obj.removeEventListener(POINTER_CANCEL, handler, false);
	}

	return this;
}

function _addPointerStart(obj, handler, id) {
	var onDown = bind(function (e) {
		if (e.pointerType !== 'mouse' && e.pointerType !== e.MSPOINTER_TYPE_MOUSE && e.pointerType !== e.MSPOINTER_TYPE_MOUSE) {
			// In IE11, some touch events needs to fire for form controls, or
			// the controls will stop working. We keep a whitelist of tag names that
			// need these events. For other target tags, we prevent default on the event.
			if (TAG_WHITE_LIST.indexOf(e.target.tagName) < 0) {
				preventDefault(e);
			} else {
				return;
			}
		}

		_handlePointer(e, handler);
	});

	obj['_leaflet_touchstart' + id] = onDown;
	obj.addEventListener(POINTER_DOWN, onDown, false);

	// need to keep track of what pointers and how many are active to provide e.touches emulation
	if (!_pointerDocListener) {
		// we listen documentElement as any drags that end by moving the touch off the screen get fired there
		document.documentElement.addEventListener(POINTER_DOWN, _globalPointerDown, true);
		document.documentElement.addEventListener(POINTER_MOVE, _globalPointerMove, true);
		document.documentElement.addEventListener(POINTER_UP, _globalPointerUp, true);
		document.documentElement.addEventListener(POINTER_CANCEL, _globalPointerUp, true);

		_pointerDocListener = true;
	}
}

function _globalPointerDown(e) {
	_pointers[e.pointerId] = e;
	_pointersCount++;
}

function _globalPointerMove(e) {
	if (_pointers[e.pointerId]) {
		_pointers[e.pointerId] = e;
	}
}

function _globalPointerUp(e) {
	delete _pointers[e.pointerId];
	_pointersCount--;
}

function _handlePointer(e, handler) {
	e.touches = [];
	for (var i in _pointers) {
		e.touches.push(_pointers[i]);
	}
	e.changedTouches = [e];

	handler(e);
}

function _addPointerMove(obj, handler, id) {
	var onMove = function (e) {
		// don't fire touch moves when mouse isn't down
		if ((e.pointerType === e.MSPOINTER_TYPE_MOUSE || e.pointerType === 'mouse') && e.buttons === 0) { return; }

		_handlePointer(e, handler);
	};

	obj['_leaflet_touchmove' + id] = onMove;
	obj.addEventListener(POINTER_MOVE, onMove, false);
}

function _addPointerEnd(obj, handler, id) {
	var onUp = function (e) {
		_handlePointer(e, handler);
	};

	obj['_leaflet_touchend' + id] = onUp;
	obj.addEventListener(POINTER_UP, onUp, false);
	obj.addEventListener(POINTER_CANCEL, onUp, false);
}

/*
 * Extends the event handling code with double tap support for mobile browsers.
 */

var _touchstart = msPointer ? 'MSPointerDown' : pointer ? 'pointerdown' : 'touchstart';
var _touchend = msPointer ? 'MSPointerUp' : pointer ? 'pointerup' : 'touchend';
var _pre = '_leaflet_';

// inspired by Zepto touch code by Thomas Fuchs
function addDoubleTapListener(obj, handler, id) {
	var last, touch$$1,
	    doubleTap = false,
	    delay = 250;

	function onTouchStart(e) {
		var count;

		if (pointer) {
			if ((!edge) || e.pointerType === 'mouse') { return; }
			count = _pointersCount;
		} else {
			count = e.touches.length;
		}

		if (count > 1) { return; }

		var now = Date.now(),
		    delta = now - (last || now);

		touch$$1 = e.touches ? e.touches[0] : e;
		doubleTap = (delta > 0 && delta <= delay);
		last = now;
	}

	function onTouchEnd(e) {
		if (doubleTap && !touch$$1.cancelBubble) {
			if (pointer) {
				if ((!edge) || e.pointerType === 'mouse') { return; }
				// work around .type being readonly with MSPointer* events
				var newTouch = {},
				    prop, i;

				for (i in touch$$1) {
					prop = touch$$1[i];
					newTouch[i] = prop && prop.bind ? prop.bind(touch$$1) : prop;
				}
				touch$$1 = newTouch;
			}
			touch$$1.type = 'dblclick';
			handler(touch$$1);
			last = null;
		}
	}

	obj[_pre + _touchstart + id] = onTouchStart;
	obj[_pre + _touchend + id] = onTouchEnd;
	obj[_pre + 'dblclick' + id] = handler;

	obj.addEventListener(_touchstart, onTouchStart, false);
	obj.addEventListener(_touchend, onTouchEnd, false);

	// On some platforms (notably, chrome<55 on win10 + touchscreen + mouse),
	// the browser doesn't fire touchend/pointerup events but does fire
	// native dblclicks. See #4127.
	// Edge 14 also fires native dblclicks, but only for pointerType mouse, see #5180.
	obj.addEventListener('dblclick', handler, false);

	return this;
}

function removeDoubleTapListener(obj, id) {
	var touchstart = obj[_pre + _touchstart + id],
	    touchend = obj[_pre + _touchend + id],
	    dblclick = obj[_pre + 'dblclick' + id];

	obj.removeEventListener(_touchstart, touchstart, false);
	obj.removeEventListener(_touchend, touchend, false);
	if (!edge) {
		obj.removeEventListener('dblclick', dblclick, false);
	}

	return this;
}

/*
 * @namespace DomEvent
 * Utility functions to work with the [DOM events](https://developer.mozilla.org/docs/Web/API/Event), used by Leaflet internally.
 */

// Inspired by John Resig, Dean Edwards and YUI addEvent implementations.

// @function on(el: HTMLElement, types: String, fn: Function, context?: Object): this
// Adds a listener function (`fn`) to a particular DOM event type of the
// element `el`. You can optionally specify the context of the listener
// (object the `this` keyword will point to). You can also pass several
// space-separated types (e.g. `'click dblclick'`).

// @alternative
// @function on(el: HTMLElement, eventMap: Object, context?: Object): this
// Adds a set of type/listener pairs, e.g. `{click: onClick, mousemove: onMouseMove}`
function on(obj, types, fn, context) {

	if (typeof types === 'object') {
		for (var type in types) {
			addOne(obj, type, types[type], fn);
		}
	} else {
		types = splitWords(types);

		for (var i = 0, len = types.length; i < len; i++) {
			addOne(obj, types[i], fn, context);
		}
	}

	return this;
}

var eventsKey = '_leaflet_events';

// @function off(el: HTMLElement, types: String, fn: Function, context?: Object): this
// Removes a previously added listener function. If no function is specified,
// it will remove all the listeners of that particular DOM event from the element.
// Note that if you passed a custom context to on, you must pass the same
// context to `off` in order to remove the listener.

// @alternative
// @function off(el: HTMLElement, eventMap: Object, context?: Object): this
// Removes a set of type/listener pairs, e.g. `{click: onClick, mousemove: onMouseMove}`

// @alternative
// @function off(el: HTMLElement): this
// Removes all known event listeners
function off(obj, types, fn, context) {

	if (typeof types === 'object') {
		for (var type in types) {
			removeOne(obj, type, types[type], fn);
		}
	} else if (types) {
		types = splitWords(types);

		for (var i = 0, len = types.length; i < len; i++) {
			removeOne(obj, types[i], fn, context);
		}
	} else {
		for (var j in obj[eventsKey]) {
			removeOne(obj, j, obj[eventsKey][j]);
		}
		delete obj[eventsKey];
	}

	return this;
}

function addOne(obj, type, fn, context) {
	var id = type + stamp(fn) + (context ? '_' + stamp(context) : '');

	if (obj[eventsKey] && obj[eventsKey][id]) { return this; }

	var handler = function (e) {
		return fn.call(context || obj, e || window.event);
	};

	var originalHandler = handler;

	if (pointer && type.indexOf('touch') === 0) {
		// Needs DomEvent.Pointer.js
		addPointerListener(obj, type, handler, id);

	} else if (touch && (type === 'dblclick') && addDoubleTapListener &&
	           !(pointer && chrome)) {
		// Chrome >55 does not need the synthetic dblclicks from addDoubleTapListener
		// See #5180
		addDoubleTapListener(obj, handler, id);

	} else if ('addEventListener' in obj) {

		if (type === 'mousewheel') {
			obj.addEventListener('onwheel' in obj ? 'wheel' : 'mousewheel', handler, false);

		} else if ((type === 'mouseenter') || (type === 'mouseleave')) {
			handler = function (e) {
				e = e || window.event;
				if (isExternalTarget(obj, e)) {
					originalHandler(e);
				}
			};
			obj.addEventListener(type === 'mouseenter' ? 'mouseover' : 'mouseout', handler, false);

		} else {
			if (type === 'click' && android) {
				handler = function (e) {
					filterClick(e, originalHandler);
				};
			}
			obj.addEventListener(type, handler, false);
		}

	} else if ('attachEvent' in obj) {
		obj.attachEvent('on' + type, handler);
	}

	obj[eventsKey] = obj[eventsKey] || {};
	obj[eventsKey][id] = handler;
}

function removeOne(obj, type, fn, context) {

	var id = type + stamp(fn) + (context ? '_' + stamp(context) : ''),
	    handler = obj[eventsKey] && obj[eventsKey][id];

	if (!handler) { return this; }

	if (pointer && type.indexOf('touch') === 0) {
		removePointerListener(obj, type, id);

	} else if (touch && (type === 'dblclick') && removeDoubleTapListener) {
		removeDoubleTapListener(obj, id);

	} else if ('removeEventListener' in obj) {

		if (type === 'mousewheel') {
			obj.removeEventListener('onwheel' in obj ? 'wheel' : 'mousewheel', handler, false);

		} else {
			obj.removeEventListener(
				type === 'mouseenter' ? 'mouseover' :
				type === 'mouseleave' ? 'mouseout' : type, handler, false);
		}

	} else if ('detachEvent' in obj) {
		obj.detachEvent('on' + type, handler);
	}

	obj[eventsKey][id] = null;
}

// @function stopPropagation(ev: DOMEvent): this
// Stop the given event from propagation to parent elements. Used inside the listener functions:
// ```js
// L.DomEvent.on(div, 'click', function (ev) {
// 	L.DomEvent.stopPropagation(ev);
// });
// ```
function stopPropagation(e) {

	if (e.stopPropagation) {
		e.stopPropagation();
	} else if (e.originalEvent) {  // In case of Leaflet event.
		e.originalEvent._stopped = true;
	} else {
		e.cancelBubble = true;
	}
	skipped(e);

	return this;
}

// @function disableScrollPropagation(el: HTMLElement): this
// Adds `stopPropagation` to the element's `'mousewheel'` events (plus browser variants).
function disableScrollPropagation(el) {
	addOne(el, 'mousewheel', stopPropagation);
	return this;
}

// @function disableClickPropagation(el: HTMLElement): this
// Adds `stopPropagation` to the element's `'click'`, `'doubleclick'`,
// `'mousedown'` and `'touchstart'` events (plus browser variants).
function disableClickPropagation(el) {
	on(el, 'mousedown touchstart dblclick', stopPropagation);
	addOne(el, 'click', fakeStop);
	return this;
}

// @function preventDefault(ev: DOMEvent): this
// Prevents the default action of the DOM Event `ev` from happening (such as
// following a link in the href of the a element, or doing a POST request
// with page reload when a `<form>` is submitted).
// Use it inside listener functions.
function preventDefault(e) {
	if (e.preventDefault) {
		e.preventDefault();
	} else {
		e.returnValue = false;
	}
	return this;
}

// @function stop(ev): this
// Does `stopPropagation` and `preventDefault` at the same time.
function stop(e) {
	preventDefault(e);
	stopPropagation(e);
	return this;
}

// @function getMousePosition(ev: DOMEvent, container?: HTMLElement): Point
// Gets normalized mouse position from a DOM event relative to the
// `container` or to the whole page if not specified.
function getMousePosition(e, container) {
	if (!container) {
		return new Point(e.clientX, e.clientY);
	}

	var rect = container.getBoundingClientRect();

	return new Point(
		e.clientX - rect.left - container.clientLeft,
		e.clientY - rect.top - container.clientTop);
}

// Chrome on Win scrolls double the pixels as in other platforms (see #4538),
// and Firefox scrolls device pixels, not CSS pixels
var wheelPxFactor =
	(win && chrome) ? 2 * window.devicePixelRatio :
	gecko ? window.devicePixelRatio : 1;

// @function getWheelDelta(ev: DOMEvent): Number
// Gets normalized wheel delta from a mousewheel DOM event, in vertical
// pixels scrolled (negative if scrolling down).
// Events from pointing devices without precise scrolling are mapped to
// a best guess of 60 pixels.
function getWheelDelta(e) {
	return (edge) ? e.wheelDeltaY / 2 : // Don't trust window-geometry-based delta
	       (e.deltaY && e.deltaMode === 0) ? -e.deltaY / wheelPxFactor : // Pixels
	       (e.deltaY && e.deltaMode === 1) ? -e.deltaY * 20 : // Lines
	       (e.deltaY && e.deltaMode === 2) ? -e.deltaY * 60 : // Pages
	       (e.deltaX || e.deltaZ) ? 0 :	// Skip horizontal/depth wheel events
	       e.wheelDelta ? (e.wheelDeltaY || e.wheelDelta) / 2 : // Legacy IE pixels
	       (e.detail && Math.abs(e.detail) < 32765) ? -e.detail * 20 : // Legacy Moz lines
	       e.detail ? e.detail / -32765 * 60 : // Legacy Moz pages
	       0;
}

var skipEvents = {};

function fakeStop(e) {
	// fakes stopPropagation by setting a special event flag, checked/reset with skipped(e)
	skipEvents[e.type] = true;
}

function skipped(e) {
	var events = skipEvents[e.type];
	// reset when checking, as it's only used in map container and propagates outside of the map
	skipEvents[e.type] = false;
	return events;
}

// check if element really left/entered the event target (for mouseenter/mouseleave)
function isExternalTarget(el, e) {

	var related = e.relatedTarget;

	if (!related) { return true; }

	try {
		while (related && (related !== el)) {
			related = related.parentNode;
		}
	} catch (err) {
		return false;
	}
	return (related !== el);
}

var lastClick;

// this is a horrible workaround for a bug in Android where a single touch triggers two click events
function filterClick(e, handler) {
	var timeStamp = (e.timeStamp || (e.originalEvent && e.originalEvent.timeStamp)),
	    elapsed = lastClick && (timeStamp - lastClick);

	// are they closer together than 500ms yet more than 100ms?
	// Android typically triggers them ~300ms apart while multiple listeners
	// on the same event should be triggered far faster;
	// or check if click is simulated on the element, and if it is, reject any non-simulated events

	if ((elapsed && elapsed > 100 && elapsed < 500) || (e.target._simulatedClick && !e._simulated)) {
		stop(e);
		return;
	}
	lastClick = timeStamp;

	handler(e);
}




var DomEvent = (Object.freeze || Object)({
	on: on,
	off: off,
	stopPropagation: stopPropagation,
	disableScrollPropagation: disableScrollPropagation,
	disableClickPropagation: disableClickPropagation,
	preventDefault: preventDefault,
	stop: stop,
	getMousePosition: getMousePosition,
	getWheelDelta: getWheelDelta,
	fakeStop: fakeStop,
	skipped: skipped,
	isExternalTarget: isExternalTarget,
	addListener: on,
	removeListener: off
});

/*
 * @namespace DomUtil
 *
 * Utility functions to work with the [DOM](https://developer.mozilla.org/docs/Web/API/Document_Object_Model)
 * tree, used by Leaflet internally.
 *
 * Most functions expecting or returning a `HTMLElement` also work for
 * SVG elements. The only difference is that classes refer to CSS classes
 * in HTML and SVG classes in SVG.
 */


// @property TRANSFORM: String
// Vendor-prefixed transform style name (e.g. `'webkitTransform'` for WebKit).
var TRANSFORM = testProp(
	['transform', 'WebkitTransform', 'OTransform', 'MozTransform', 'msTransform']);

// webkitTransition comes first because some browser versions that drop vendor prefix don't do
// the same for the transitionend event, in particular the Android 4.1 stock browser

// @property TRANSITION: String
// Vendor-prefixed transition style name.
var TRANSITION = testProp(
	['webkitTransition', 'transition', 'OTransition', 'MozTransition', 'msTransition']);

// @property TRANSITION_END: String
// Vendor-prefixed transitionend event name.
var TRANSITION_END =
	TRANSITION === 'webkitTransition' || TRANSITION === 'OTransition' ? TRANSITION + 'End' : 'transitionend';


// @function get(id: String|HTMLElement): HTMLElement
// Returns an element given its DOM id, or returns the element itself
// if it was passed directly.
function get(id) {
	return typeof id === 'string' ? document.getElementById(id) : id;
}

// @function getStyle(el: HTMLElement, styleAttrib: String): String
// Returns the value for a certain style attribute on an element,
// including computed values or values set through CSS.
function getStyle(el, style) {
	var value = el.style[style] || (el.currentStyle && el.currentStyle[style]);

	if ((!value || value === 'auto') && document.defaultView) {
		var css = document.defaultView.getComputedStyle(el, null);
		value = css ? css[style] : null;
	}
	return value === 'auto' ? null : value;
}

// @function create(tagName: String, className?: String, container?: HTMLElement): HTMLElement
// Creates an HTML element with `tagName`, sets its class to `className`, and optionally appends it to `container` element.
function create$1(tagName, className, container) {
	var el = document.createElement(tagName);
	el.className = className || '';

	if (container) {
		container.appendChild(el);
	}
	return el;
}

// @function remove(el: HTMLElement)
// Removes `el` from its parent element
function remove(el) {
	var parent = el.parentNode;
	if (parent) {
		parent.removeChild(el);
	}
}

// @function empty(el: HTMLElement)
// Removes all of `el`'s children elements from `el`
function empty(el) {
	while (el.firstChild) {
		el.removeChild(el.firstChild);
	}
}

// @function toFront(el: HTMLElement)
// Makes `el` the last child of its parent, so it renders in front of the other children.
function toFront(el) {
	var parent = el.parentNode;
	if (parent.lastChild !== el) {
		parent.appendChild(el);
	}
}

// @function toBack(el: HTMLElement)
// Makes `el` the first child of its parent, so it renders behind the other children.
function toBack(el) {
	var parent = el.parentNode;
	if (parent.firstChild !== el) {
		parent.insertBefore(el, parent.firstChild);
	}
}

// @function hasClass(el: HTMLElement, name: String): Boolean
// Returns `true` if the element's class attribute contains `name`.
function hasClass(el, name) {
	if (el.classList !== undefined) {
		return el.classList.contains(name);
	}
	var className = getClass(el);
	return className.length > 0 && new RegExp('(^|\\s)' + name + '(\\s|$)').test(className);
}

// @function addClass(el: HTMLElement, name: String)
// Adds `name` to the element's class attribute.
function addClass(el, name) {
	if (el.classList !== undefined) {
		var classes = splitWords(name);
		for (var i = 0, len = classes.length; i < len; i++) {
			el.classList.add(classes[i]);
		}
	} else if (!hasClass(el, name)) {
		var className = getClass(el);
		setClass(el, (className ? className + ' ' : '') + name);
	}
}

// @function removeClass(el: HTMLElement, name: String)
// Removes `name` from the element's class attribute.
function removeClass(el, name) {
	if (el.classList !== undefined) {
		el.classList.remove(name);
	} else {
		setClass(el, trim((' ' + getClass(el) + ' ').replace(' ' + name + ' ', ' ')));
	}
}

// @function setClass(el: HTMLElement, name: String)
// Sets the element's class.
function setClass(el, name) {
	if (el.className.baseVal === undefined) {
		el.className = name;
	} else {
		// in case of SVG element
		el.className.baseVal = name;
	}
}

// @function getClass(el: HTMLElement): String
// Returns the element's class.
function getClass(el) {
	return el.className.baseVal === undefined ? el.className : el.className.baseVal;
}

// @function setOpacity(el: HTMLElement, opacity: Number)
// Set the opacity of an element (including old IE support).
// `opacity` must be a number from `0` to `1`.
function setOpacity(el, value) {
	if ('opacity' in el.style) {
		el.style.opacity = value;
	} else if ('filter' in el.style) {
		_setOpacityIE(el, value);
	}
}

function _setOpacityIE(el, value) {
	var filter = false,
	    filterName = 'DXImageTransform.Microsoft.Alpha';

	// filters collection throws an error if we try to retrieve a filter that doesn't exist
	try {
		filter = el.filters.item(filterName);
	} catch (e) {
		// don't set opacity to 1 if we haven't already set an opacity,
		// it isn't needed and breaks transparent pngs.
		if (value === 1) { return; }
	}

	value = Math.round(value * 100);

	if (filter) {
		filter.Enabled = (value !== 100);
		filter.Opacity = value;
	} else {
		el.style.filter += ' progid:' + filterName + '(opacity=' + value + ')';
	}
}

// @function testProp(props: String[]): String|false
// Goes through the array of style names and returns the first name
// that is a valid style name for an element. If no such name is found,
// it returns false. Useful for vendor-prefixed styles like `transform`.
function testProp(props) {
	var style = document.documentElement.style;

	for (var i = 0; i < props.length; i++) {
		if (props[i] in style) {
			return props[i];
		}
	}
	return false;
}

// @function setTransform(el: HTMLElement, offset: Point, scale?: Number)
// Resets the 3D CSS transform of `el` so it is translated by `offset` pixels
// and optionally scaled by `scale`. Does not have an effect if the
// browser doesn't support 3D CSS transforms.
function setTransform(el, offset, scale) {
	var pos = offset || new Point(0, 0);

	el.style[TRANSFORM] =
		(ie3d ?
			'translate(' + pos.x + 'px,' + pos.y + 'px)' :
			'translate3d(' + pos.x + 'px,' + pos.y + 'px,0)') +
		(scale ? ' scale(' + scale + ')' : '');
}

// @function setPosition(el: HTMLElement, position: Point)
// Sets the position of `el` to coordinates specified by `position`,
// using CSS translate or top/left positioning depending on the browser
// (used by Leaflet internally to position its layers).
function setPosition(el, point) {

	/*eslint-disable */
	el._leaflet_pos = point;
	/*eslint-enable */

	if (any3d) {
		setTransform(el, point);
	} else {
		el.style.left = point.x + 'px';
		el.style.top = point.y + 'px';
	}
}

// @function getPosition(el: HTMLElement): Point
// Returns the coordinates of an element previously positioned with setPosition.
function getPosition(el) {
	// this method is only used for elements previously positioned using setPosition,
	// so it's safe to cache the position for performance

	return el._leaflet_pos || new Point(0, 0);
}

// @function disableTextSelection()
// Prevents the user from generating `selectstart` DOM events, usually generated
// when the user drags the mouse through a page with text. Used internally
// by Leaflet to override the behaviour of any click-and-drag interaction on
// the map. Affects drag interactions on the whole document.

// @function enableTextSelection()
// Cancels the effects of a previous [`L.DomUtil.disableTextSelection`](#domutil-disabletextselection).
var disableTextSelection;
var enableTextSelection;
var _userSelect;
if ('onselectstart' in document) {
	disableTextSelection = function () {
		on(window, 'selectstart', preventDefault);
	};
	enableTextSelection = function () {
		off(window, 'selectstart', preventDefault);
	};
} else {
	var userSelectProperty = testProp(
		['userSelect', 'WebkitUserSelect', 'OUserSelect', 'MozUserSelect', 'msUserSelect']);

	disableTextSelection = function () {
		if (userSelectProperty) {
			var style = document.documentElement.style;
			_userSelect = style[userSelectProperty];
			style[userSelectProperty] = 'none';
		}
	};
	enableTextSelection = function () {
		if (userSelectProperty) {
			document.documentElement.style[userSelectProperty] = _userSelect;
			_userSelect = undefined;
		}
	};
}

// @function disableImageDrag()
// As [`L.DomUtil.disableTextSelection`](#domutil-disabletextselection), but
// for `dragstart` DOM events, usually generated when the user drags an image.
function disableImageDrag() {
	on(window, 'dragstart', preventDefault);
}

// @function enableImageDrag()
// Cancels the effects of a previous [`L.DomUtil.disableImageDrag`](#domutil-disabletextselection).
function enableImageDrag() {
	off(window, 'dragstart', preventDefault);
}

var _outlineElement;
var _outlineStyle;
// @function preventOutline(el: HTMLElement)
// Makes the [outline](https://developer.mozilla.org/docs/Web/CSS/outline)
// of the element `el` invisible. Used internally by Leaflet to prevent
// focusable elements from displaying an outline when the user performs a
// drag interaction on them.
function preventOutline(element) {
	while (element.tabIndex === -1) {
		element = element.parentNode;
	}
	if (!element.style) { return; }
	restoreOutline();
	_outlineElement = element;
	_outlineStyle = element.style.outline;
	element.style.outline = 'none';
	on(window, 'keydown', restoreOutline);
}

// @function restoreOutline()
// Cancels the effects of a previous [`L.DomUtil.preventOutline`]().
function restoreOutline() {
	if (!_outlineElement) { return; }
	_outlineElement.style.outline = _outlineStyle;
	_outlineElement = undefined;
	_outlineStyle = undefined;
	off(window, 'keydown', restoreOutline);
}


var DomUtil = (Object.freeze || Object)({
	TRANSFORM: TRANSFORM,
	TRANSITION: TRANSITION,
	TRANSITION_END: TRANSITION_END,
	get: get,
	getStyle: getStyle,
	create: create$1,
	remove: remove,
	empty: empty,
	toFront: toFront,
	toBack: toBack,
	hasClass: hasClass,
	addClass: addClass,
	removeClass: removeClass,
	setClass: setClass,
	getClass: getClass,
	setOpacity: setOpacity,
	testProp: testProp,
	setTransform: setTransform,
	setPosition: setPosition,
	getPosition: getPosition,
	disableTextSelection: disableTextSelection,
	enableTextSelection: enableTextSelection,
	disableImageDrag: disableImageDrag,
	enableImageDrag: enableImageDrag,
	preventOutline: preventOutline,
	restoreOutline: restoreOutline
});

/*
 * @class PosAnimation
 * @aka L.PosAnimation
 * @inherits Evented
 * Used internally for panning animations, utilizing CSS3 Transitions for modern browsers and a timer fallback for IE6-9.
 *
 * @example
 * ```js
 * var fx = new L.PosAnimation();
 * fx.run(el, [300, 500], 0.5);
 * ```
 *
 * @constructor L.PosAnimation()
 * Creates a `PosAnimation` object.
 *
 */

var PosAnimation = Evented.extend({

	// @method run(el: HTMLElement, newPos: Point, duration?: Number, easeLinearity?: Number)
	// Run an animation of a given element to a new position, optionally setting
	// duration in seconds (`0.25` by default) and easing linearity factor (3rd
	// argument of the [cubic bezier curve](http://cubic-bezier.com/#0,0,.5,1),
	// `0.5` by default).
	run: function (el, newPos, duration, easeLinearity) {
		this.stop();

		this._el = el;
		this._inProgress = true;
		this._duration = duration || 0.25;
		this._easeOutPower = 1 / Math.max(easeLinearity || 0.5, 0.2);

		this._startPos = getPosition(el);
		this._offset = newPos.subtract(this._startPos);
		this._startTime = +new Date();

		// @event start: Event
		// Fired when the animation starts
		this.fire('start');

		this._animate();
	},

	// @method stop()
	// Stops the animation (if currently running).
	stop: function () {
		if (!this._inProgress) { return; }

		this._step(true);
		this._complete();
	},

	_animate: function () {
		// animation loop
		this._animId = requestAnimFrame(this._animate, this);
		this._step();
	},

	_step: function (round) {
		var elapsed = (+new Date()) - this._startTime,
		    duration = this._duration * 1000;

		if (elapsed < duration) {
			this._runFrame(this._easeOut(elapsed / duration), round);
		} else {
			this._runFrame(1);
			this._complete();
		}
	},

	_runFrame: function (progress, round) {
		var pos = this._startPos.add(this._offset.multiplyBy(progress));
		if (round) {
			pos._round();
		}
		setPosition(this._el, pos);

		// @event step: Event
		// Fired continuously during the animation.
		this.fire('step');
	},

	_complete: function () {
		cancelAnimFrame(this._animId);

		this._inProgress = false;
		// @event end: Event
		// Fired when the animation ends.
		this.fire('end');
	},

	_easeOut: function (t) {
		return 1 - Math.pow(1 - t, this._easeOutPower);
	}
});

/*
 * @class Map
 * @aka L.Map
 * @inherits Evented
 *
 * The central class of the API — it is used to create a map on a page and manipulate it.
 *
 * @example
 *
 * ```js
 * // initialize the map on the "map" div with a given center and zoom
 * var map = L.map('map', {
 * 	center: [51.505, -0.09],
 * 	zoom: 13
 * });
 * ```
 *
 */

var Map = Evented.extend({

	options: {
		// @section Map State Options
		// @option crs: CRS = L.CRS.EPSG3857
		// The [Coordinate Reference System](#crs) to use. Don't change this if you're not
		// sure what it means.
		crs: EPSG3857,

		// @option center: LatLng = undefined
		// Initial geographic center of the map
		center: undefined,

		// @option zoom: Number = undefined
		// Initial map zoom level
		zoom: undefined,

		// @option minZoom: Number = *
		// Minimum zoom level of the map.
		// If not specified and at least one `GridLayer` or `TileLayer` is in the map,
		// the lowest of their `minZoom` options will be used instead.
		minZoom: undefined,

		// @option maxZoom: Number = *
		// Maximum zoom level of the map.
		// If not specified and at least one `GridLayer` or `TileLayer` is in the map,
		// the highest of their `maxZoom` options will be used instead.
		maxZoom: undefined,

		// @option layers: Layer[] = []
		// Array of layers that will be added to the map initially
		layers: [],

		// @option maxBounds: LatLngBounds = null
		// When this option is set, the map restricts the view to the given
		// geographical bounds, bouncing the user back if the user tries to pan
		// outside the view. To set the restriction dynamically, use
		// [`setMaxBounds`](#map-setmaxbounds) method.
		maxBounds: undefined,

		// @option renderer: Renderer = *
		// The default method for drawing vector layers on the map. `L.SVG`
		// or `L.Canvas` by default depending on browser support.
		renderer: undefined,


		// @section Animation Options
		// @option zoomAnimation: Boolean = true
		// Whether the map zoom animation is enabled. By default it's enabled
		// in all browsers that support CSS3 Transitions except Android.
		zoomAnimation: true,

		// @option zoomAnimationThreshold: Number = 4
		// Won't animate zoom if the zoom difference exceeds this value.
		zoomAnimationThreshold: 4,

		// @option fadeAnimation: Boolean = true
		// Whether the tile fade animation is enabled. By default it's enabled
		// in all browsers that support CSS3 Transitions except Android.
		fadeAnimation: true,

		// @option markerZoomAnimation: Boolean = true
		// Whether markers animate their zoom with the zoom animation, if disabled
		// they will disappear for the length of the animation. By default it's
		// enabled in all browsers that support CSS3 Transitions except Android.
		markerZoomAnimation: true,

		// @option transform3DLimit: Number = 2^23
		// Defines the maximum size of a CSS translation transform. The default
		// value should not be changed unless a web browser positions layers in
		// the wrong place after doing a large `panBy`.
		transform3DLimit: 8388608, // Precision limit of a 32-bit float

		// @section Interaction Options
		// @option zoomSnap: Number = 1
		// Forces the map's zoom level to always be a multiple of this, particularly
		// right after a [`fitBounds()`](#map-fitbounds) or a pinch-zoom.
		// By default, the zoom level snaps to the nearest integer; lower values
		// (e.g. `0.5` or `0.1`) allow for greater granularity. A value of `0`
		// means the zoom level will not be snapped after `fitBounds` or a pinch-zoom.
		zoomSnap: 1,

		// @option zoomDelta: Number = 1
		// Controls how much the map's zoom level will change after a
		// [`zoomIn()`](#map-zoomin), [`zoomOut()`](#map-zoomout), pressing `+`
		// or `-` on the keyboard, or using the [zoom controls](#control-zoom).
		// Values smaller than `1` (e.g. `0.5`) allow for greater granularity.
		zoomDelta: 1,

		// @option trackResize: Boolean = true
		// Whether the map automatically handles browser window resize to update itself.
		trackResize: true
	},

	initialize: function (id, options) { // (HTMLElement or String, Object)
		options = setOptions(this, options);

		this._initContainer(id);
		this._initLayout();

		// hack for https://github.com/Leaflet/Leaflet/issues/1980
		this._onResize = bind(this._onResize, this);

		this._initEvents();

		if (options.maxBounds) {
			this.setMaxBounds(options.maxBounds);
		}

		if (options.zoom !== undefined) {
			this._zoom = this._limitZoom(options.zoom);
		}

		if (options.center && options.zoom !== undefined) {
			this.setView(toLatLng(options.center), options.zoom, {reset: true});
		}

		this._handlers = [];
		this._layers = {};
		this._zoomBoundLayers = {};
		this._sizeChanged = true;

		this.callInitHooks();

		// don't animate on browsers without hardware-accelerated transitions or old Android/Opera
		this._zoomAnimated = TRANSITION && any3d && !mobileOpera &&
				this.options.zoomAnimation;

		// zoom transitions run with the same duration for all layers, so if one of transitionend events
		// happens after starting zoom animation (propagating to the map pane), we know that it ended globally
		if (this._zoomAnimated) {
			this._createAnimProxy();
			on(this._proxy, TRANSITION_END, this._catchTransitionEnd, this);
		}

		this._addLayers(this.options.layers);
	},


	// @section Methods for modifying map state

	// @method setView(center: LatLng, zoom: Number, options?: Zoom/pan options): this
	// Sets the view of the map (geographical center and zoom) with the given
	// animation options.
	setView: function (center, zoom, options) {

		zoom = zoom === undefined ? this._zoom : this._limitZoom(zoom);
		center = this._limitCenter(toLatLng(center), zoom, this.options.maxBounds);
		options = options || {};

		this._stop();

		if (this._loaded && !options.reset && options !== true) {

			if (options.animate !== undefined) {
				options.zoom = extend({animate: options.animate}, options.zoom);
				options.pan = extend({animate: options.animate, duration: options.duration}, options.pan);
			}

			// try animating pan or zoom
			var moved = (this._zoom !== zoom) ?
				this._tryAnimatedZoom && this._tryAnimatedZoom(center, zoom, options.zoom) :
				this._tryAnimatedPan(center, options.pan);

			if (moved) {
				// prevent resize handler call, the view will refresh after animation anyway
				clearTimeout(this._sizeTimer);
				return this;
			}
		}

		// animation didn't start, just reset the map view
		this._resetView(center, zoom);

		return this;
	},

	// @method setZoom(zoom: Number, options?: Zoom/pan options): this
	// Sets the zoom of the map.
	setZoom: function (zoom, options) {
		if (!this._loaded) {
			this._zoom = zoom;
			return this;
		}
		return this.setView(this.getCenter(), zoom, {zoom: options});
	},

	// @method zoomIn(delta?: Number, options?: Zoom options): this
	// Increases the zoom of the map by `delta` ([`zoomDelta`](#map-zoomdelta) by default).
	zoomIn: function (delta, options) {
		delta = delta || (any3d ? this.options.zoomDelta : 1);
		return this.setZoom(this._zoom + delta, options);
	},

	// @method zoomOut(delta?: Number, options?: Zoom options): this
	// Decreases the zoom of the map by `delta` ([`zoomDelta`](#map-zoomdelta) by default).
	zoomOut: function (delta, options) {
		delta = delta || (any3d ? this.options.zoomDelta : 1);
		return this.setZoom(this._zoom - delta, options);
	},

	// @method setZoomAround(latlng: LatLng, zoom: Number, options: Zoom options): this
	// Zooms the map while keeping a specified geographical point on the map
	// stationary (e.g. used internally for scroll zoom and double-click zoom).
	// @alternative
	// @method setZoomAround(offset: Point, zoom: Number, options: Zoom options): this
	// Zooms the map while keeping a specified pixel on the map (relative to the top-left corner) stationary.
	setZoomAround: function (latlng, zoom, options) {
		var scale = this.getZoomScale(zoom),
		    viewHalf = this.getSize().divideBy(2),
		    containerPoint = latlng instanceof Point ? latlng : this.latLngToContainerPoint(latlng),

		    centerOffset = containerPoint.subtract(viewHalf).multiplyBy(1 - 1 / scale),
		    newCenter = this.containerPointToLatLng(viewHalf.add(centerOffset));

		return this.setView(newCenter, zoom, {zoom: options});
	},

	_getBoundsCenterZoom: function (bounds, options) {

		options = options || {};
		bounds = bounds.getBounds ? bounds.getBounds() : toLatLngBounds(bounds);

		var paddingTL = toPoint(options.paddingTopLeft || options.padding || [0, 0]),
		    paddingBR = toPoint(options.paddingBottomRight || options.padding || [0, 0]),

		    zoom = this.getBoundsZoom(bounds, false, paddingTL.add(paddingBR));

		zoom = (typeof options.maxZoom === 'number') ? Math.min(options.maxZoom, zoom) : zoom;

		if (zoom === Infinity) {
			return {
				center: bounds.getCenter(),
				zoom: zoom
			};
		}

		var paddingOffset = paddingBR.subtract(paddingTL).divideBy(2),

		    swPoint = this.project(bounds.getSouthWest(), zoom),
		    nePoint = this.project(bounds.getNorthEast(), zoom),
		    center = this.unproject(swPoint.add(nePoint).divideBy(2).add(paddingOffset), zoom);

		return {
			center: center,
			zoom: zoom
		};
	},

	// @method fitBounds(bounds: LatLngBounds, options?: fitBounds options): this
	// Sets a map view that contains the given geographical bounds with the
	// maximum zoom level possible.
	fitBounds: function (bounds, options) {

		bounds = toLatLngBounds(bounds);

		if (!bounds.isValid()) {
			throw new Error('Bounds are not valid.');
		}

		var target = this._getBoundsCenterZoom(bounds, options);
		return this.setView(target.center, target.zoom, options);
	},

	// @method fitWorld(options?: fitBounds options): this
	// Sets a map view that mostly contains the whole world with the maximum
	// zoom level possible.
	fitWorld: function (options) {
		return this.fitBounds([[-90, -180], [90, 180]], options);
	},

	// @method panTo(latlng: LatLng, options?: Pan options): this
	// Pans the map to a given center.
	panTo: function (center, options) { // (LatLng)
		return this.setView(center, this._zoom, {pan: options});
	},

	// @method panBy(offset: Point, options?: Pan options): this
	// Pans the map by a given number of pixels (animated).
	panBy: function (offset, options) {
		offset = toPoint(offset).round();
		options = options || {};

		if (!offset.x && !offset.y) {
			return this.fire('moveend');
		}
		// If we pan too far, Chrome gets issues with tiles
		// and makes them disappear or appear in the wrong place (slightly offset) #2602
		if (options.animate !== true && !this.getSize().contains(offset)) {
			this._resetView(this.unproject(this.project(this.getCenter()).add(offset)), this.getZoom());
			return this;
		}

		if (!this._panAnim) {
			this._panAnim = new PosAnimation();

			this._panAnim.on({
				'step': this._onPanTransitionStep,
				'end': this._onPanTransitionEnd
			}, this);
		}

		// don't fire movestart if animating inertia
		if (!options.noMoveStart) {
			this.fire('movestart');
		}

		// animate pan unless animate: false specified
		if (options.animate !== false) {
			addClass(this._mapPane, 'leaflet-pan-anim');

			var newPos = this._getMapPanePos().subtract(offset).round();
			this._panAnim.run(this._mapPane, newPos, options.duration || 0.25, options.easeLinearity);
		} else {
			this._rawPanBy(offset);
			this.fire('move').fire('moveend');
		}

		return this;
	},

	// @method flyTo(latlng: LatLng, zoom?: Number, options?: Zoom/pan options): this
	// Sets the view of the map (geographical center and zoom) performing a smooth
	// pan-zoom animation.
	flyTo: function (targetCenter, targetZoom, options) {

		options = options || {};
		if (options.animate === false || !any3d) {
			return this.setView(targetCenter, targetZoom, options);
		}

		this._stop();

		var from = this.project(this.getCenter()),
		    to = this.project(targetCenter),
		    size = this.getSize(),
		    startZoom = this._zoom;

		targetCenter = toLatLng(targetCenter);
		targetZoom = targetZoom === undefined ? startZoom : targetZoom;

		var w0 = Math.max(size.x, size.y),
		    w1 = w0 * this.getZoomScale(startZoom, targetZoom),
		    u1 = (to.distanceTo(from)) || 1,
		    rho = 1.42,
		    rho2 = rho * rho;

		function r(i) {
			var s1 = i ? -1 : 1,
			    s2 = i ? w1 : w0,
			    t1 = w1 * w1 - w0 * w0 + s1 * rho2 * rho2 * u1 * u1,
			    b1 = 2 * s2 * rho2 * u1,
			    b = t1 / b1,
			    sq = Math.sqrt(b * b + 1) - b;

			    // workaround for floating point precision bug when sq = 0, log = -Infinite,
			    // thus triggering an infinite loop in flyTo
			    var log = sq < 0.000000001 ? -18 : Math.log(sq);

			return log;
		}

		function sinh(n) { return (Math.exp(n) - Math.exp(-n)) / 2; }
		function cosh(n) { return (Math.exp(n) + Math.exp(-n)) / 2; }
		function tanh(n) { return sinh(n) / cosh(n); }

		var r0 = r(0);

		function w(s) { return w0 * (cosh(r0) / cosh(r0 + rho * s)); }
		function u(s) { return w0 * (cosh(r0) * tanh(r0 + rho * s) - sinh(r0)) / rho2; }

		function easeOut(t) { return 1 - Math.pow(1 - t, 1.5); }

		var start = Date.now(),
		    S = (r(1) - r0) / rho,
		    duration = options.duration ? 1000 * options.duration : 1000 * S * 0.8;

		function frame() {
			var t = (Date.now() - start) / duration,
			    s = easeOut(t) * S;

			if (t <= 1) {
				this._flyToFrame = requestAnimFrame(frame, this);

				this._move(
					this.unproject(from.add(to.subtract(from).multiplyBy(u(s) / u1)), startZoom),
					this.getScaleZoom(w0 / w(s), startZoom),
					{flyTo: true});

			} else {
				this
					._move(targetCenter, targetZoom)
					._moveEnd(true);
			}
		}

		this._moveStart(true);

		frame.call(this);
		return this;
	},

	// @method flyToBounds(bounds: LatLngBounds, options?: fitBounds options): this
	// Sets the view of the map with a smooth animation like [`flyTo`](#map-flyto),
	// but takes a bounds parameter like [`fitBounds`](#map-fitbounds).
	flyToBounds: function (bounds, options) {
		var target = this._getBoundsCenterZoom(bounds, options);
		return this.flyTo(target.center, target.zoom, options);
	},

	// @method setMaxBounds(bounds: Bounds): this
	// Restricts the map view to the given bounds (see the [maxBounds](#map-maxbounds) option).
	setMaxBounds: function (bounds) {
		bounds = toLatLngBounds(bounds);

		if (!bounds.isValid()) {
			this.options.maxBounds = null;
			return this.off('moveend', this._panInsideMaxBounds);
		} else if (this.options.maxBounds) {
			this.off('moveend', this._panInsideMaxBounds);
		}

		this.options.maxBounds = bounds;

		if (this._loaded) {
			this._panInsideMaxBounds();
		}

		return this.on('moveend', this._panInsideMaxBounds);
	},

	// @method setMinZoom(zoom: Number): this
	// Sets the lower limit for the available zoom levels (see the [minZoom](#map-minzoom) option).
	setMinZoom: function (zoom) {
		this.options.minZoom = zoom;

		if (this._loaded && this.getZoom() < this.options.minZoom) {
			return this.setZoom(zoom);
		}

		return this;
	},

	// @method setMaxZoom(zoom: Number): this
	// Sets the upper limit for the available zoom levels (see the [maxZoom](#map-maxzoom) option).
	setMaxZoom: function (zoom) {
		this.options.maxZoom = zoom;

		if (this._loaded && (this.getZoom() > this.options.maxZoom)) {
			return this.setZoom(zoom);
		}

		return this;
	},

	// @method panInsideBounds(bounds: LatLngBounds, options?: Pan options): this
	// Pans the map to the closest view that would lie inside the given bounds (if it's not already), controlling the animation using the options specific, if any.
	panInsideBounds: function (bounds, options) {
		this._enforcingBounds = true;
		var center = this.getCenter(),
		    newCenter = this._limitCenter(center, this._zoom, toLatLngBounds(bounds));

		if (!center.equals(newCenter)) {
			this.panTo(newCenter, options);
		}

		this._enforcingBounds = false;
		return this;
	},

	// @method invalidateSize(options: Zoom/Pan options): this
	// Checks if the map container size changed and updates the map if so —
	// call it after you've changed the map size dynamically, also animating
	// pan by default. If `options.pan` is `false`, panning will not occur.
	// If `options.debounceMoveend` is `true`, it will delay `moveend` event so
	// that it doesn't happen often even if the method is called many
	// times in a row.

	// @alternative
	// @method invalidateSize(animate: Boolean): this
	// Checks if the map container size changed and updates the map if so —
	// call it after you've changed the map size dynamically, also animating
	// pan by default.
	invalidateSize: function (options) {
		if (!this._loaded) { return this; }

		options = extend({
			animate: false,
			pan: true
		}, options === true ? {animate: true} : options);

		var oldSize = this.getSize();
		this._sizeChanged = true;
		this._lastCenter = null;

		var newSize = this.getSize(),
		    oldCenter = oldSize.divideBy(2).round(),
		    newCenter = newSize.divideBy(2).round(),
		    offset = oldCenter.subtract(newCenter);

		if (!offset.x && !offset.y) { return this; }

		if (options.animate && options.pan) {
			this.panBy(offset);

		} else {
			if (options.pan) {
				this._rawPanBy(offset);
			}

			this.fire('move');

			if (options.debounceMoveend) {
				clearTimeout(this._sizeTimer);
				this._sizeTimer = setTimeout(bind(this.fire, this, 'moveend'), 200);
			} else {
				this.fire('moveend');
			}
		}

		// @section Map state change events
		// @event resize: ResizeEvent
		// Fired when the map is resized.
		return this.fire('resize', {
			oldSize: oldSize,
			newSize: newSize
		});
	},

	// @section Methods for modifying map state
	// @method stop(): this
	// Stops the currently running `panTo` or `flyTo` animation, if any.
	stop: function () {
		this.setZoom(this._limitZoom(this._zoom));
		if (!this.options.zoomSnap) {
			this.fire('viewreset');
		}
		return this._stop();
	},

	// @section Geolocation methods
	// @method locate(options?: Locate options): this
	// Tries to locate the user using the Geolocation API, firing a [`locationfound`](#map-locationfound)
	// event with location data on success or a [`locationerror`](#map-locationerror) event on failure,
	// and optionally sets the map view to the user's location with respect to
	// detection accuracy (or to the world view if geolocation failed).
	// Note that, if your page doesn't use HTTPS, this method will fail in
	// modern browsers ([Chrome 50 and newer](https://sites.google.com/a/chromium.org/dev/Home/chromium-security/deprecating-powerful-features-on-insecure-origins))
	// See `Locate options` for more details.
	locate: function (options) {

		options = this._locateOptions = extend({
			timeout: 10000,
			watch: false
			// setView: false
			// maxZoom: <Number>
			// maximumAge: 0
			// enableHighAccuracy: false
		}, options);

		if (!('geolocation' in navigator)) {
			this._handleGeolocationError({
				code: 0,
				message: 'Geolocation not supported.'
			});
			return this;
		}

		var onResponse = bind(this._handleGeolocationResponse, this),
		    onError = bind(this._handleGeolocationError, this);

		if (options.watch) {
			this._locationWatchId =
			        navigator.geolocation.watchPosition(onResponse, onError, options);
		} else {
			navigator.geolocation.getCurrentPosition(onResponse, onError, options);
		}
		return this;
	},

	// @method stopLocate(): this
	// Stops watching location previously initiated by `map.locate({watch: true})`
	// and aborts resetting the map view if map.locate was called with
	// `{setView: true}`.
	stopLocate: function () {
		if (navigator.geolocation && navigator.geolocation.clearWatch) {
			navigator.geolocation.clearWatch(this._locationWatchId);
		}
		if (this._locateOptions) {
			this._locateOptions.setView = false;
		}
		return this;
	},

	_handleGeolocationError: function (error) {
		var c = error.code,
		    message = error.message ||
		            (c === 1 ? 'permission denied' :
		            (c === 2 ? 'position unavailable' : 'timeout'));

		if (this._locateOptions.setView && !this._loaded) {
			this.fitWorld();
		}

		// @section Location events
		// @event locationerror: ErrorEvent
		// Fired when geolocation (using the [`locate`](#map-locate) method) failed.
		this.fire('locationerror', {
			code: c,
			message: 'Geolocation error: ' + message + '.'
		});
	},

	_handleGeolocationResponse: function (pos) {
		var lat = pos.coords.latitude,
		    lng = pos.coords.longitude,
		    latlng = new LatLng(lat, lng),
		    bounds = latlng.toBounds(pos.coords.accuracy),
		    options = this._locateOptions;

		if (options.setView) {
			var zoom = this.getBoundsZoom(bounds);
			this.setView(latlng, options.maxZoom ? Math.min(zoom, options.maxZoom) : zoom);
		}

		var data = {
			latlng: latlng,
			bounds: bounds,
			timestamp: pos.timestamp
		};

		for (var i in pos.coords) {
			if (typeof pos.coords[i] === 'number') {
				data[i] = pos.coords[i];
			}
		}

		// @event locationfound: LocationEvent
		// Fired when geolocation (using the [`locate`](#map-locate) method)
		// went successfully.
		this.fire('locationfound', data);
	},

	// TODO handler.addTo
	// TODO Appropiate docs section?
	// @section Other Methods
	// @method addHandler(name: String, HandlerClass: Function): this
	// Adds a new `Handler` to the map, given its name and constructor function.
	addHandler: function (name, HandlerClass) {
		if (!HandlerClass) { return this; }

		var handler = this[name] = new HandlerClass(this);

		this._handlers.push(handler);

		if (this.options[name]) {
			handler.enable();
		}

		return this;
	},

	// @method remove(): this
	// Destroys the map and clears all related event listeners.
	remove: function () {

		this._initEvents(true);

		if (this._containerId !== this._container._leaflet_id) {
			throw new Error('Map container is being reused by another instance');
		}

		try {
			// throws error in IE6-8
			delete this._container._leaflet_id;
			delete this._containerId;
		} catch (e) {
			/*eslint-disable */
			this._container._leaflet_id = undefined;
			/*eslint-enable */
			this._containerId = undefined;
		}

		remove(this._mapPane);

		if (this._clearControlPos) {
			this._clearControlPos();
		}

		this._clearHandlers();

		if (this._loaded) {
			// @section Map state change events
			// @event unload: Event
			// Fired when the map is destroyed with [remove](#map-remove) method.
			this.fire('unload');
		}

		var i;
		for (i in this._layers) {
			this._layers[i].remove();
		}
		for (i in this._panes) {
			remove(this._panes[i]);
		}

		this._layers = [];
		this._panes = [];
		delete this._mapPane;
		delete this._renderer;

		return this;
	},

	// @section Other Methods
	// @method createPane(name: String, container?: HTMLElement): HTMLElement
	// Creates a new [map pane](#map-pane) with the given name if it doesn't exist already,
	// then returns it. The pane is created as a child of `container`, or
	// as a child of the main map pane if not set.
	createPane: function (name, container) {
		var className = 'leaflet-pane' + (name ? ' leaflet-' + name.replace('Pane', '') + '-pane' : ''),
		    pane = create$1('div', className, container || this._mapPane);

		if (name) {
			this._panes[name] = pane;
		}
		return pane;
	},

	// @section Methods for Getting Map State

	// @method getCenter(): LatLng
	// Returns the geographical center of the map view
	getCenter: function () {
		this._checkIfLoaded();

		if (this._lastCenter && !this._moved()) {
			return this._lastCenter;
		}
		return this.layerPointToLatLng(this._getCenterLayerPoint());
	},

	// @method getZoom(): Number
	// Returns the current zoom level of the map view
	getZoom: function () {
		return this._zoom;
	},

	// @method getBounds(): LatLngBounds
	// Returns the geographical bounds visible in the current map view
	getBounds: function () {
		var bounds = this.getPixelBounds(),
		    sw = this.unproject(bounds.getBottomLeft()),
		    ne = this.unproject(bounds.getTopRight());

		return new LatLngBounds(sw, ne);
	},

	// @method getMinZoom(): Number
	// Returns the minimum zoom level of the map (if set in the `minZoom` option of the map or of any layers), or `0` by default.
	getMinZoom: function () {
		return this.options.minZoom === undefined ? this._layersMinZoom || 0 : this.options.minZoom;
	},

	// @method getMaxZoom(): Number
	// Returns the maximum zoom level of the map (if set in the `maxZoom` option of the map or of any layers).
	getMaxZoom: function () {
		return this.options.maxZoom === undefined ?
			(this._layersMaxZoom === undefined ? Infinity : this._layersMaxZoom) :
			this.options.maxZoom;
	},

	// @method getBoundsZoom(bounds: LatLngBounds, inside?: Boolean): Number
	// Returns the maximum zoom level on which the given bounds fit to the map
	// view in its entirety. If `inside` (optional) is set to `true`, the method
	// instead returns the minimum zoom level on which the map view fits into
	// the given bounds in its entirety.
	getBoundsZoom: function (bounds, inside, padding) { // (LatLngBounds[, Boolean, Point]) -> Number
		bounds = toLatLngBounds(bounds);
		padding = toPoint(padding || [0, 0]);

		var zoom = this.getZoom() || 0,
		    min = this.getMinZoom(),
		    max = this.getMaxZoom(),
		    nw = bounds.getNorthWest(),
		    se = bounds.getSouthEast(),
		    size = this.getSize().subtract(padding),
		    boundsSize = toBounds(this.project(se, zoom), this.project(nw, zoom)).getSize(),
		    snap = any3d ? this.options.zoomSnap : 1,
		    scalex = size.x / boundsSize.x,
		    scaley = size.y / boundsSize.y,
		    scale = inside ? Math.max(scalex, scaley) : Math.min(scalex, scaley);

		zoom = this.getScaleZoom(scale, zoom);

		if (snap) {
			zoom = Math.round(zoom / (snap / 100)) * (snap / 100); // don't jump if within 1% of a snap level
			zoom = inside ? Math.ceil(zoom / snap) * snap : Math.floor(zoom / snap) * snap;
		}

		return Math.max(min, Math.min(max, zoom));
	},

	// @method getSize(): Point
	// Returns the current size of the map container (in pixels).
	getSize: function () {
		if (!this._size || this._sizeChanged) {
			this._size = new Point(
				this._container.clientWidth || 0,
				this._container.clientHeight || 0);

			this._sizeChanged = false;
		}
		return this._size.clone();
	},

	// @method getPixelBounds(): Bounds
	// Returns the bounds of the current map view in projected pixel
	// coordinates (sometimes useful in layer and overlay implementations).
	getPixelBounds: function (center, zoom) {
		var topLeftPoint = this._getTopLeftPoint(center, zoom);
		return new Bounds(topLeftPoint, topLeftPoint.add(this.getSize()));
	},

	// TODO: Check semantics - isn't the pixel origin the 0,0 coord relative to
	// the map pane? "left point of the map layer" can be confusing, specially
	// since there can be negative offsets.
	// @method getPixelOrigin(): Point
	// Returns the projected pixel coordinates of the top left point of
	// the map layer (useful in custom layer and overlay implementations).
	getPixelOrigin: function () {
		this._checkIfLoaded();
		return this._pixelOrigin;
	},

	// @method getPixelWorldBounds(zoom?: Number): Bounds
	// Returns the world's bounds in pixel coordinates for zoom level `zoom`.
	// If `zoom` is omitted, the map's current zoom level is used.
	getPixelWorldBounds: function (zoom) {
		return this.options.crs.getProjectedBounds(zoom === undefined ? this.getZoom() : zoom);
	},

	// @section Other Methods

	// @method getPane(pane: String|HTMLElement): HTMLElement
	// Returns a [map pane](#map-pane), given its name or its HTML element (its identity).
	getPane: function (pane) {
		return typeof pane === 'string' ? this._panes[pane] : pane;
	},

	// @method getPanes(): Object
	// Returns a plain object containing the names of all [panes](#map-pane) as keys and
	// the panes as values.
	getPanes: function () {
		return this._panes;
	},

	// @method getContainer: HTMLElement
	// Returns the HTML element that contains the map.
	getContainer: function () {
		return this._container;
	},


	// @section Conversion Methods

	// @method getZoomScale(toZoom: Number, fromZoom: Number): Number
	// Returns the scale factor to be applied to a map transition from zoom level
	// `fromZoom` to `toZoom`. Used internally to help with zoom animations.
	getZoomScale: function (toZoom, fromZoom) {
		// TODO replace with universal implementation after refactoring projections
		var crs = this.options.crs;
		fromZoom = fromZoom === undefined ? this._zoom : fromZoom;
		return crs.scale(toZoom) / crs.scale(fromZoom);
	},

	// @method getScaleZoom(scale: Number, fromZoom: Number): Number
	// Returns the zoom level that the map would end up at, if it is at `fromZoom`
	// level and everything is scaled by a factor of `scale`. Inverse of
	// [`getZoomScale`](#map-getZoomScale).
	getScaleZoom: function (scale, fromZoom) {
		var crs = this.options.crs;
		fromZoom = fromZoom === undefined ? this._zoom : fromZoom;
		var zoom = crs.zoom(scale * crs.scale(fromZoom));
		return isNaN(zoom) ? Infinity : zoom;
	},

	// @method project(latlng: LatLng, zoom: Number): Point
	// Projects a geographical coordinate `LatLng` according to the projection
	// of the map's CRS, then scales it according to `zoom` and the CRS's
	// `Transformation`. The result is pixel coordinate relative to
	// the CRS origin.
	project: function (latlng, zoom) {
		zoom = zoom === undefined ? this._zoom : zoom;
		return this.options.crs.latLngToPoint(toLatLng(latlng), zoom);
	},

	// @method unproject(point: Point, zoom: Number): LatLng
	// Inverse of [`project`](#map-project).
	unproject: function (point, zoom) {
		zoom = zoom === undefined ? this._zoom : zoom;
		return this.options.crs.pointToLatLng(toPoint(point), zoom);
	},

	// @method layerPointToLatLng(point: Point): LatLng
	// Given a pixel coordinate relative to the [origin pixel](#map-getpixelorigin),
	// returns the corresponding geographical coordinate (for the current zoom level).
	layerPointToLatLng: function (point) {
		var projectedPoint = toPoint(point).add(this.getPixelOrigin());
		return this.unproject(projectedPoint);
	},

	// @method latLngToLayerPoint(latlng: LatLng): Point
	// Given a geographical coordinate, returns the corresponding pixel coordinate
	// relative to the [origin pixel](#map-getpixelorigin).
	latLngToLayerPoint: function (latlng) {
		var projectedPoint = this.project(toLatLng(latlng))._round();
		return projectedPoint._subtract(this.getPixelOrigin());
	},

	// @method wrapLatLng(latlng: LatLng): LatLng
	// Returns a `LatLng` where `lat` and `lng` has been wrapped according to the
	// map's CRS's `wrapLat` and `wrapLng` properties, if they are outside the
	// CRS's bounds.
	// By default this means longitude is wrapped around the dateline so its
	// value is between -180 and +180 degrees.
	wrapLatLng: function (latlng) {
		return this.options.crs.wrapLatLng(toLatLng(latlng));
	},

	// @method wrapLatLngBounds(bounds: LatLngBounds): LatLngBounds
	// Returns a `LatLngBounds` with the same size as the given one, ensuring that
	// its center is within the CRS's bounds.
	// By default this means the center longitude is wrapped around the dateline so its
	// value is between -180 and +180 degrees, and the majority of the bounds
	// overlaps the CRS's bounds.
	wrapLatLngBounds: function (latlng) {
		return this.options.crs.wrapLatLngBounds(toLatLngBounds(latlng));
	},

	// @method distance(latlng1: LatLng, latlng2: LatLng): Number
	// Returns the distance between two geographical coordinates according to
	// the map's CRS. By default this measures distance in meters.
	distance: function (latlng1, latlng2) {
		return this.options.crs.distance(toLatLng(latlng1), toLatLng(latlng2));
	},

	// @method containerPointToLayerPoint(point: Point): Point
	// Given a pixel coordinate relative to the map container, returns the corresponding
	// pixel coordinate relative to the [origin pixel](#map-getpixelorigin).
	containerPointToLayerPoint: function (point) { // (Point)
		return toPoint(point).subtract(this._getMapPanePos());
	},

	// @method layerPointToContainerPoint(point: Point): Point
	// Given a pixel coordinate relative to the [origin pixel](#map-getpixelorigin),
	// returns the corresponding pixel coordinate relative to the map container.
	layerPointToContainerPoint: function (point) { // (Point)
		return toPoint(point).add(this._getMapPanePos());
	},

	// @method containerPointToLatLng(point: Point): LatLng
	// Given a pixel coordinate relative to the map container, returns
	// the corresponding geographical coordinate (for the current zoom level).
	containerPointToLatLng: function (point) {
		var layerPoint = this.containerPointToLayerPoint(toPoint(point));
		return this.layerPointToLatLng(layerPoint);
	},

	// @method latLngToContainerPoint(latlng: LatLng): Point
	// Given a geographical coordinate, returns the corresponding pixel coordinate
	// relative to the map container.
	latLngToContainerPoint: function (latlng) {
		return this.layerPointToContainerPoint(this.latLngToLayerPoint(toLatLng(latlng)));
	},

	// @method mouseEventToContainerPoint(ev: MouseEvent): Point
	// Given a MouseEvent object, returns the pixel coordinate relative to the
	// map container where the event took place.
	mouseEventToContainerPoint: function (e) {
		return getMousePosition(e, this._container);
	},

	// @method mouseEventToLayerPoint(ev: MouseEvent): Point
	// Given a MouseEvent object, returns the pixel coordinate relative to
	// the [origin pixel](#map-getpixelorigin) where the event took place.
	mouseEventToLayerPoint: function (e) {
		return this.containerPointToLayerPoint(this.mouseEventToContainerPoint(e));
	},

	// @method mouseEventToLatLng(ev: MouseEvent): LatLng
	// Given a MouseEvent object, returns geographical coordinate where the
	// event took place.
	mouseEventToLatLng: function (e) { // (MouseEvent)
		return this.layerPointToLatLng(this.mouseEventToLayerPoint(e));
	},


	// map initialization methods

	_initContainer: function (id) {
		var container = this._container = get(id);

		if (!container) {
			throw new Error('Map container not found.');
		} else if (container._leaflet_id) {
			throw new Error('Map container is already initialized.');
		}

		on(container, 'scroll', this._onScroll, this);
		this._containerId = stamp(container);
	},

	_initLayout: function () {
		var container = this._container;

		this._fadeAnimated = this.options.fadeAnimation && any3d;

		addClass(container, 'leaflet-container' +
			(touch ? ' leaflet-touch' : '') +
			(retina ? ' leaflet-retina' : '') +
			(ielt9 ? ' leaflet-oldie' : '') +
			(safari ? ' leaflet-safari' : '') +
			(this._fadeAnimated ? ' leaflet-fade-anim' : ''));

		var position = getStyle(container, 'position');

		if (position !== 'absolute' && position !== 'relative' && position !== 'fixed') {
			container.style.position = 'relative';
		}

		this._initPanes();

		if (this._initControlPos) {
			this._initControlPos();
		}
	},

	_initPanes: function () {
		var panes = this._panes = {};
		this._paneRenderers = {};

		// @section
		//
		// Panes are DOM elements used to control the ordering of layers on the map. You
		// can access panes with [`map.getPane`](#map-getpane) or
		// [`map.getPanes`](#map-getpanes) methods. New panes can be created with the
		// [`map.createPane`](#map-createpane) method.
		//
		// Every map has the following default panes that differ only in zIndex.
		//
		// @pane mapPane: HTMLElement = 'auto'
		// Pane that contains all other map panes

		this._mapPane = this.createPane('mapPane', this._container);
		setPosition(this._mapPane, new Point(0, 0));

		// @pane tilePane: HTMLElement = 200
		// Pane for `GridLayer`s and `TileLayer`s
		this.createPane('tilePane');
		// @pane overlayPane: HTMLElement = 400
		// Pane for vector overlays (`Path`s), like `Polyline`s and `Polygon`s
		this.createPane('shadowPane');
		// @pane shadowPane: HTMLElement = 500
		// Pane for overlay shadows (e.g. `Marker` shadows)
		this.createPane('overlayPane');
		// @pane markerPane: HTMLElement = 600
		// Pane for `Icon`s of `Marker`s
		this.createPane('markerPane');
		// @pane tooltipPane: HTMLElement = 650
		// Pane for tooltip.
		this.createPane('tooltipPane');
		// @pane popupPane: HTMLElement = 700
		// Pane for `Popup`s.
		this.createPane('popupPane');

		if (!this.options.markerZoomAnimation) {
			addClass(panes.markerPane, 'leaflet-zoom-hide');
			addClass(panes.shadowPane, 'leaflet-zoom-hide');
		}
	},


	// private methods that modify map state

	// @section Map state change events
	_resetView: function (center, zoom) {
		setPosition(this._mapPane, new Point(0, 0));

		var loading = !this._loaded;
		this._loaded = true;
		zoom = this._limitZoom(zoom);

		this.fire('viewprereset');

		var zoomChanged = this._zoom !== zoom;
		this
			._moveStart(zoomChanged)
			._move(center, zoom)
			._moveEnd(zoomChanged);

		// @event viewreset: Event
		// Fired when the map needs to redraw its content (this usually happens
		// on map zoom or load). Very useful for creating custom overlays.
		this.fire('viewreset');

		// @event load: Event
		// Fired when the map is initialized (when its center and zoom are set
		// for the first time).
		if (loading) {
			this.fire('load');
		}
	},

	_moveStart: function (zoomChanged) {
		// @event zoomstart: Event
		// Fired when the map zoom is about to change (e.g. before zoom animation).
		// @event movestart: Event
		// Fired when the view of the map starts changing (e.g. user starts dragging the map).
		if (zoomChanged) {
			this.fire('zoomstart');
		}
		return this.fire('movestart');
	},

	_move: function (center, zoom, data) {
		if (zoom === undefined) {
			zoom = this._zoom;
		}
		var zoomChanged = this._zoom !== zoom;

		this._zoom = zoom;
		this._lastCenter = center;
		this._pixelOrigin = this._getNewPixelOrigin(center);

		// @event zoom: Event
		// Fired repeatedly during any change in zoom level, including zoom
		// and fly animations.
		if (zoomChanged || (data && data.pinch)) {	// Always fire 'zoom' if pinching because #3530
			this.fire('zoom', data);
		}

		// @event move: Event
		// Fired repeatedly during any movement of the map, including pan and
		// fly animations.
		return this.fire('move', data);
	},

	_moveEnd: function (zoomChanged) {
		// @event zoomend: Event
		// Fired when the map has changed, after any animations.
		if (zoomChanged) {
			this.fire('zoomend');
		}

		// @event moveend: Event
		// Fired when the center of the map stops changing (e.g. user stopped
		// dragging the map).
		return this.fire('moveend');
	},

	_stop: function () {
		cancelAnimFrame(this._flyToFrame);
		if (this._panAnim) {
			this._panAnim.stop();
		}
		return this;
	},

	_rawPanBy: function (offset) {
		setPosition(this._mapPane, this._getMapPanePos().subtract(offset));
	},

	_getZoomSpan: function () {
		return this.getMaxZoom() - this.getMinZoom();
	},

	_panInsideMaxBounds: function () {
		if (!this._enforcingBounds) {
			this.panInsideBounds(this.options.maxBounds);
		}
	},

	_checkIfLoaded: function () {
		if (!this._loaded) {
			throw new Error('Set map center and zoom first.');
		}
	},

	// DOM event handling

	// @section Interaction events
	_initEvents: function (remove$$1) {
		this._targets = {};
		this._targets[stamp(this._container)] = this;

		var onOff = remove$$1 ? off : on;

		// @event click: MouseEvent
		// Fired when the user clicks (or taps) the map.
		// @event dblclick: MouseEvent
		// Fired when the user double-clicks (or double-taps) the map.
		// @event mousedown: MouseEvent
		// Fired when the user pushes the mouse button on the map.
		// @event mouseup: MouseEvent
		// Fired when the user releases the mouse button on the map.
		// @event mouseover: MouseEvent
		// Fired when the mouse enters the map.
		// @event mouseout: MouseEvent
		// Fired when the mouse leaves the map.
		// @event mousemove: MouseEvent
		// Fired while the mouse moves over the map.
		// @event contextmenu: MouseEvent
		// Fired when the user pushes the right mouse button on the map, prevents
		// default browser context menu from showing if there are listeners on
		// this event. Also fired on mobile when the user holds a single touch
		// for a second (also called long press).
		// @event keypress: KeyboardEvent
		// Fired when the user presses a key from the keyboard while the map is focused.
		onOff(this._container, 'click dblclick mousedown mouseup ' +
			'mouseover mouseout mousemove contextmenu keypress', this._handleDOMEvent, this);

		if (this.options.trackResize) {
			onOff(window, 'resize', this._onResize, this);
		}

		if (any3d && this.options.transform3DLimit) {
			(remove$$1 ? this.off : this.on).call(this, 'moveend', this._onMoveEnd);
		}
	},

	_onResize: function () {
		cancelAnimFrame(this._resizeRequest);
		this._resizeRequest = requestAnimFrame(
		        function () { this.invalidateSize({debounceMoveend: true}); }, this);
	},

	_onScroll: function () {
		this._container.scrollTop  = 0;
		this._container.scrollLeft = 0;
	},

	_onMoveEnd: function () {
		var pos = this._getMapPanePos();
		if (Math.max(Math.abs(pos.x), Math.abs(pos.y)) >= this.options.transform3DLimit) {
			// https://bugzilla.mozilla.org/show_bug.cgi?id=1203873 but Webkit also have
			// a pixel offset on very high values, see: http://jsfiddle.net/dg6r5hhb/
			this._resetView(this.getCenter(), this.getZoom());
		}
	},

	_findEventTargets: function (e, type) {
		var targets = [],
		    target,
		    isHover = type === 'mouseout' || type === 'mouseover',
		    src = e.target || e.srcElement,
		    dragging = false;

		while (src) {
			target = this._targets[stamp(src)];
			if (target && (type === 'click' || type === 'preclick') && !e._simulated && this._draggableMoved(target)) {
				// Prevent firing click after you just dragged an object.
				dragging = true;
				break;
			}
			if (target && target.listens(type, true)) {
				if (isHover && !isExternalTarget(src, e)) { break; }
				targets.push(target);
				if (isHover) { break; }
			}
			if (src === this._container) { break; }
			src = src.parentNode;
		}
		if (!targets.length && !dragging && !isHover && isExternalTarget(src, e)) {
			targets = [this];
		}
		return targets;
	},

	_handleDOMEvent: function (e) {
		if (!this._loaded || skipped(e)) { return; }

		var type = e.type;

		if (type === 'mousedown' || type === 'keypress') {
			// prevents outline when clicking on keyboard-focusable element
			preventOutline(e.target || e.srcElement);
		}

		this._fireDOMEvent(e, type);
	},

	_mouseEvents: ['click', 'dblclick', 'mouseover', 'mouseout', 'contextmenu'],

	_fireDOMEvent: function (e, type, targets) {

		if (e.type === 'click') {
			// Fire a synthetic 'preclick' event which propagates up (mainly for closing popups).
			// @event preclick: MouseEvent
			// Fired before mouse click on the map (sometimes useful when you
			// want something to happen on click before any existing click
			// handlers start running).
			var synth = extend({}, e);
			synth.type = 'preclick';
			this._fireDOMEvent(synth, synth.type, targets);
		}

		if (e._stopped) { return; }

		// Find the layer the event is propagating from and its parents.
		targets = (targets || []).concat(this._findEventTargets(e, type));

		if (!targets.length) { return; }

		var target = targets[0];
		if (type === 'contextmenu' && target.listens(type, true)) {
			preventDefault(e);
		}

		var data = {
			originalEvent: e
		};

		if (e.type !== 'keypress') {
			var isMarker = (target.options && 'icon' in target.options);
			data.containerPoint = isMarker ?
					this.latLngToContainerPoint(target.getLatLng()) : this.mouseEventToContainerPoint(e);
			data.layerPoint = this.containerPointToLayerPoint(data.containerPoint);
			data.latlng = isMarker ? target.getLatLng() : this.layerPointToLatLng(data.layerPoint);
		}

		for (var i = 0; i < targets.length; i++) {
			targets[i].fire(type, data, true);
			if (data.originalEvent._stopped ||
				(targets[i].options.bubblingMouseEvents === false && indexOf(this._mouseEvents, type) !== -1)) { return; }
		}
	},

	_draggableMoved: function (obj) {
		obj = obj.dragging && obj.dragging.enabled() ? obj : this;
		return (obj.dragging && obj.dragging.moved()) || (this.boxZoom && this.boxZoom.moved());
	},

	_clearHandlers: function () {
		for (var i = 0, len = this._handlers.length; i < len; i++) {
			this._handlers[i].disable();
		}
	},

	// @section Other Methods

	// @method whenReady(fn: Function, context?: Object): this
	// Runs the given function `fn` when the map gets initialized with
	// a view (center and zoom) and at least one layer, or immediately
	// if it's already initialized, optionally passing a function context.
	whenReady: function (callback, context) {
		if (this._loaded) {
			callback.call(context || this, {target: this});
		} else {
			this.on('load', callback, context);
		}
		return this;
	},


	// private methods for getting map state

	_getMapPanePos: function () {
		return getPosition(this._mapPane) || new Point(0, 0);
	},

	_moved: function () {
		var pos = this._getMapPanePos();
		return pos && !pos.equals([0, 0]);
	},

	_getTopLeftPoint: function (center, zoom) {
		var pixelOrigin = center && zoom !== undefined ?
			this._getNewPixelOrigin(center, zoom) :
			this.getPixelOrigin();
		return pixelOrigin.subtract(this._getMapPanePos());
	},

	_getNewPixelOrigin: function (center, zoom) {
		var viewHalf = this.getSize()._divideBy(2);
		return this.project(center, zoom)._subtract(viewHalf)._add(this._getMapPanePos())._round();
	},

	_latLngToNewLayerPoint: function (latlng, zoom, center) {
		var topLeft = this._getNewPixelOrigin(center, zoom);
		return this.project(latlng, zoom)._subtract(topLeft);
	},

	_latLngBoundsToNewLayerBounds: function (latLngBounds, zoom, center) {
		var topLeft = this._getNewPixelOrigin(center, zoom);
		return toBounds([
			this.project(latLngBounds.getSouthWest(), zoom)._subtract(topLeft),
			this.project(latLngBounds.getNorthWest(), zoom)._subtract(topLeft),
			this.project(latLngBounds.getSouthEast(), zoom)._subtract(topLeft),
			this.project(latLngBounds.getNorthEast(), zoom)._subtract(topLeft)
		]);
	},

	// layer point of the current center
	_getCenterLayerPoint: function () {
		return this.containerPointToLayerPoint(this.getSize()._divideBy(2));
	},

	// offset of the specified place to the current center in pixels
	_getCenterOffset: function (latlng) {
		return this.latLngToLayerPoint(latlng).subtract(this._getCenterLayerPoint());
	},

	// adjust center for view to get inside bounds
	_limitCenter: function (center, zoom, bounds) {

		if (!bounds) { return center; }

		var centerPoint = this.project(center, zoom),
		    viewHalf = this.getSize().divideBy(2),
		    viewBounds = new Bounds(centerPoint.subtract(viewHalf), centerPoint.add(viewHalf)),
		    offset = this._getBoundsOffset(viewBounds, bounds, zoom);

		// If offset is less than a pixel, ignore.
		// This prevents unstable projections from getting into
		// an infinite loop of tiny offsets.
		if (offset.round().equals([0, 0])) {
			return center;
		}

		return this.unproject(centerPoint.add(offset), zoom);
	},

	// adjust offset for view to get inside bounds
	_limitOffset: function (offset, bounds) {
		if (!bounds) { return offset; }

		var viewBounds = this.getPixelBounds(),
		    newBounds = new Bounds(viewBounds.min.add(offset), viewBounds.max.add(offset));

		return offset.add(this._getBoundsOffset(newBounds, bounds));
	},

	// returns offset needed for pxBounds to get inside maxBounds at a specified zoom
	_getBoundsOffset: function (pxBounds, maxBounds, zoom) {
		var projectedMaxBounds = toBounds(
		        this.project(maxBounds.getNorthEast(), zoom),
		        this.project(maxBounds.getSouthWest(), zoom)
		    ),
		    minOffset = projectedMaxBounds.min.subtract(pxBounds.min),
		    maxOffset = projectedMaxBounds.max.subtract(pxBounds.max),

		    dx = this._rebound(minOffset.x, -maxOffset.x),
		    dy = this._rebound(minOffset.y, -maxOffset.y);

		return new Point(dx, dy);
	},

	_rebound: function (left, right) {
		return left + right > 0 ?
			Math.round(left - right) / 2 :
			Math.max(0, Math.ceil(left)) - Math.max(0, Math.floor(right));
	},

	_limitZoom: function (zoom) {
		var min = this.getMinZoom(),
		    max = this.getMaxZoom(),
		    snap = any3d ? this.options.zoomSnap : 1;
		if (snap) {
			zoom = Math.round(zoom / snap) * snap;
		}
		return Math.max(min, Math.min(max, zoom));
	},

	_onPanTransitionStep: function () {
		this.fire('move');
	},

	_onPanTransitionEnd: function () {
		removeClass(this._mapPane, 'leaflet-pan-anim');
		this.fire('moveend');
	},

	_tryAnimatedPan: function (center, options) {
		// difference between the new and current centers in pixels
		var offset = this._getCenterOffset(center)._floor();

		// don't animate too far unless animate: true specified in options
		if ((options && options.animate) !== true && !this.getSize().contains(offset)) { return false; }

		this.panBy(offset, options);

		return true;
	},

	_createAnimProxy: function () {

		var proxy = this._proxy = create$1('div', 'leaflet-proxy leaflet-zoom-animated');
		this._panes.mapPane.appendChild(proxy);

		this.on('zoomanim', function (e) {
			var prop = TRANSFORM,
			    transform = this._proxy.style[prop];

			setTransform(this._proxy, this.project(e.center, e.zoom), this.getZoomScale(e.zoom, 1));

			// workaround for case when transform is the same and so transitionend event is not fired
			if (transform === this._proxy.style[prop] && this._animatingZoom) {
				this._onZoomTransitionEnd();
			}
		}, this);

		this.on('load moveend', function () {
			var c = this.getCenter(),
			    z = this.getZoom();
			setTransform(this._proxy, this.project(c, z), this.getZoomScale(z, 1));
		}, this);

		this._on('unload', this._destroyAnimProxy, this);
	},

	_destroyAnimProxy: function () {
		remove(this._proxy);
		delete this._proxy;
	},

	_catchTransitionEnd: function (e) {
		if (this._animatingZoom && e.propertyName.indexOf('transform') >= 0) {
			this._onZoomTransitionEnd();
		}
	},

	_nothingToAnimate: function () {
		return !this._container.getElementsByClassName('leaflet-zoom-animated').length;
	},

	_tryAnimatedZoom: function (center, zoom, options) {

		if (this._animatingZoom) { return true; }

		options = options || {};

		// don't animate if disabled, not supported or zoom difference is too large
		if (!this._zoomAnimated || options.animate === false || this._nothingToAnimate() ||
		        Math.abs(zoom - this._zoom) > this.options.zoomAnimationThreshold) { return false; }

		// offset is the pixel coords of the zoom origin relative to the current center
		var scale = this.getZoomScale(zoom),
		    offset = this._getCenterOffset(center)._divideBy(1 - 1 / scale);

		// don't animate if the zoom origin isn't within one screen from the current center, unless forced
		if (options.animate !== true && !this.getSize().contains(offset)) { return false; }

		requestAnimFrame(function () {
			this
			    ._moveStart(true)
			    ._animateZoom(center, zoom, true);
		}, this);

		return true;
	},

	_animateZoom: function (center, zoom, startAnim, noUpdate) {
		if (startAnim) {
			this._animatingZoom = true;

			// remember what center/zoom to set after animation
			this._animateToCenter = center;
			this._animateToZoom = zoom;

			addClass(this._mapPane, 'leaflet-zoom-anim');
		}

		// @event zoomanim: ZoomAnimEvent
		// Fired on every frame of a zoom animation
		this.fire('zoomanim', {
			center: center,
			zoom: zoom,
			noUpdate: noUpdate
		});

		// Work around webkit not firing 'transitionend', see https://github.com/Leaflet/Leaflet/issues/3689, 2693
		setTimeout(bind(this._onZoomTransitionEnd, this), 250);
	},

	_onZoomTransitionEnd: function () {
		if (!this._animatingZoom) { return; }

		removeClass(this._mapPane, 'leaflet-zoom-anim');

		this._animatingZoom = false;

		this._move(this._animateToCenter, this._animateToZoom);

		// This anim frame should prevent an obscure iOS webkit tile loading race condition.
		requestAnimFrame(function () {
			this._moveEnd(true);
		}, this);
	}
});

// @section

// @factory L.map(id: String, options?: Map options)
// Instantiates a map object given the DOM ID of a `<div>` element
// and optionally an object literal with `Map options`.
//
// @alternative
// @factory L.map(el: HTMLElement, options?: Map options)
// Instantiates a map object given an instance of a `<div>` HTML element
// and optionally an object literal with `Map options`.
function createMap(id, options) {
	return new Map(id, options);
}

/*
 * @class Control
 * @aka L.Control
 * @inherits Class
 *
 * L.Control is a base class for implementing map controls. Handles positioning.
 * All other controls extend from this class.
 */

var Control = Class.extend({
	// @section
	// @aka Control options
	options: {
		// @option position: String = 'topright'
		// The position of the control (one of the map corners). Possible values are `'topleft'`,
		// `'topright'`, `'bottomleft'` or `'bottomright'`
		position: 'topright'
	},

	initialize: function (options) {
		setOptions(this, options);
	},

	/* @section
	 * Classes extending L.Control will inherit the following methods:
	 *
	 * @method getPosition: string
	 * Returns the position of the control.
	 */
	getPosition: function () {
		return this.options.position;
	},

	// @method setPosition(position: string): this
	// Sets the position of the control.
	setPosition: function (position) {
		var map = this._map;

		if (map) {
			map.removeControl(this);
		}

		this.options.position = position;

		if (map) {
			map.addControl(this);
		}

		return this;
	},

	// @method getContainer: HTMLElement
	// Returns the HTMLElement that contains the control.
	getContainer: function () {
		return this._container;
	},

	// @method addTo(map: Map): this
	// Adds the control to the given map.
	addTo: function (map) {
		this.remove();
		this._map = map;

		var container = this._container = this.onAdd(map),
		    pos = this.getPosition(),
		    corner = map._controlCorners[pos];

		addClass(container, 'leaflet-control');

		if (pos.indexOf('bottom') !== -1) {
			corner.insertBefore(container, corner.firstChild);
		} else {
			corner.appendChild(container);
		}

		return this;
	},

	// @method remove: this
	// Removes the control from the map it is currently active on.
	remove: function () {
		if (!this._map) {
			return this;
		}

		remove(this._container);

		if (this.onRemove) {
			this.onRemove(this._map);
		}

		this._map = null;

		return this;
	},

	_refocusOnMap: function (e) {
		// if map exists and event is not a keyboard event
		if (this._map && e && e.screenX > 0 && e.screenY > 0) {
			this._map.getContainer().focus();
		}
	}
});

var control = function (options) {
	return new Control(options);
};

/* @section Extension methods
 * @uninheritable
 *
 * Every control should extend from `L.Control` and (re-)implement the following methods.
 *
 * @method onAdd(map: Map): HTMLElement
 * Should return the container DOM element for the control and add listeners on relevant map events. Called on [`control.addTo(map)`](#control-addTo).
 *
 * @method onRemove(map: Map)
 * Optional method. Should contain all clean up code that removes the listeners previously added in [`onAdd`](#control-onadd). Called on [`control.remove()`](#control-remove).
 */

/* @namespace Map
 * @section Methods for Layers and Controls
 */
Map.include({
	// @method addControl(control: Control): this
	// Adds the given control to the map
	addControl: function (control) {
		control.addTo(this);
		return this;
	},

	// @method removeControl(control: Control): this
	// Removes the given control from the map
	removeControl: function (control) {
		control.remove();
		return this;
	},

	_initControlPos: function () {
		var corners = this._controlCorners = {},
		    l = 'leaflet-',
		    container = this._controlContainer =
		            create$1('div', l + 'control-container', this._container);

		function createCorner(vSide, hSide) {
			var className = l + vSide + ' ' + l + hSide;

			corners[vSide + hSide] = create$1('div', className, container);
		}

		createCorner('top', 'left');
		createCorner('top', 'right');
		createCorner('bottom', 'left');
		createCorner('bottom', 'right');
	},

	_clearControlPos: function () {
		for (var i in this._controlCorners) {
			remove(this._controlCorners[i]);
		}
		remove(this._controlContainer);
		delete this._controlCorners;
		delete this._controlContainer;
	}
});

/*
 * @class Control.Layers
 * @aka L.Control.Layers
 * @inherits Control
 *
 * The layers control gives users the ability to switch between different base layers and switch overlays on/off (check out the [detailed example](http://leafletjs.com/examples/layers-control/)). Extends `Control`.
 *
 * @example
 *
 * ```js
 * var baseLayers = {
 * 	"Mapbox": mapbox,
 * 	"OpenStreetMap": osm
 * };
 *
 * var overlays = {
 * 	"Marker": marker,
 * 	"Roads": roadsLayer
 * };
 *
 * L.control.layers(baseLayers, overlays).addTo(map);
 * ```
 *
 * The `baseLayers` and `overlays` parameters are object literals with layer names as keys and `Layer` objects as values:
 *
 * ```js
 * {
 *     "<someName1>": layer1,
 *     "<someName2>": layer2
 * }
 * ```
 *
 * The layer names can contain HTML, which allows you to add additional styling to the items:
 *
 * ```js
 * {"<img src='my-layer-icon' /> <span class='my-layer-item'>My Layer</span>": myLayer}
 * ```
 */

var Layers = Control.extend({
	// @section
	// @aka Control.Layers options
	options: {
		// @option collapsed: Boolean = true
		// If `true`, the control will be collapsed into an icon and expanded on mouse hover or touch.
		collapsed: true,
		position: 'topright',

		// @option autoZIndex: Boolean = true
		// If `true`, the control will assign zIndexes in increasing order to all of its layers so that the order is preserved when switching them on/off.
		autoZIndex: true,

		// @option hideSingleBase: Boolean = false
		// If `true`, the base layers in the control will be hidden when there is only one.
		hideSingleBase: false,

		// @option sortLayers: Boolean = false
		// Whether to sort the layers. When `false`, layers will keep the order
		// in which they were added to the control.
		sortLayers: false,

		// @option sortFunction: Function = *
		// A [compare function](https://developer.mozilla.org/docs/Web/JavaScript/Reference/Global_Objects/Array/sort)
		// that will be used for sorting the layers, when `sortLayers` is `true`.
		// The function receives both the `L.Layer` instances and their names, as in
		// `sortFunction(layerA, layerB, nameA, nameB)`.
		// By default, it sorts layers alphabetically by their name.
		sortFunction: function (layerA, layerB, nameA, nameB) {
			return nameA < nameB ? -1 : (nameB < nameA ? 1 : 0);
		}
	},

	initialize: function (baseLayers, overlays, options) {
		setOptions(this, options);

		this._layerControlInputs = [];
		this._layers = [];
		this._lastZIndex = 0;
		this._handlingClick = false;

		for (var i in baseLayers) {
			this._addLayer(baseLayers[i], i);
		}

		for (i in overlays) {
			this._addLayer(overlays[i], i, true);
		}
	},

	onAdd: function (map) {
		this._initLayout();
		this._update();

		this._map = map;
		map.on('zoomend', this._checkDisabledLayers, this);

		for (var i = 0; i < this._layers.length; i++) {
			this._layers[i].layer.on('add remove', this._onLayerChange, this);
		}

		return this._container;
	},

	addTo: function (map) {
		Control.prototype.addTo.call(this, map);
		// Trigger expand after Layers Control has been inserted into DOM so that is now has an actual height.
		return this._expandIfNotCollapsed();
	},

	onRemove: function () {
		this._map.off('zoomend', this._checkDisabledLayers, this);

		for (var i = 0; i < this._layers.length; i++) {
			this._layers[i].layer.off('add remove', this._onLayerChange, this);
		}
	},

	// @method addBaseLayer(layer: Layer, name: String): this
	// Adds a base layer (radio button entry) with the given name to the control.
	addBaseLayer: function (layer, name) {
		this._addLayer(layer, name);
		return (this._map) ? this._update() : this;
	},

	// @method addOverlay(layer: Layer, name: String): this
	// Adds an overlay (checkbox entry) with the given name to the control.
	addOverlay: function (layer, name) {
		this._addLayer(layer, name, true);
		return (this._map) ? this._update() : this;
	},

	// @method removeLayer(layer: Layer): this
	// Remove the given layer from the control.
	removeLayer: function (layer) {
		layer.off('add remove', this._onLayerChange, this);

		var obj = this._getLayer(stamp(layer));
		if (obj) {
			this._layers.splice(this._layers.indexOf(obj), 1);
		}
		return (this._map) ? this._update() : this;
	},

	// @method expand(): this
	// Expand the control container if collapsed.
	expand: function () {
		addClass(this._container, 'leaflet-control-layers-expanded');
		this._form.style.height = null;
		var acceptableHeight = this._map.getSize().y - (this._container.offsetTop + 50);
		if (acceptableHeight < this._form.clientHeight) {
			addClass(this._form, 'leaflet-control-layers-scrollbar');
			this._form.style.height = acceptableHeight + 'px';
		} else {
			removeClass(this._form, 'leaflet-control-layers-scrollbar');
		}
		this._checkDisabledLayers();
		return this;
	},

	// @method collapse(): this
	// Collapse the control container if expanded.
	collapse: function () {
		removeClass(this._container, 'leaflet-control-layers-expanded');
		return this;
	},

	_initLayout: function () {
		var className = 'leaflet-control-layers',
		    container = this._container = create$1('div', className),
		    collapsed = this.options.collapsed;

		// makes this work on IE touch devices by stopping it from firing a mouseout event when the touch is released
		container.setAttribute('aria-haspopup', true);

		disableClickPropagation(container);
		disableScrollPropagation(container);

		var form = this._form = create$1('form', className + '-list');

		if (collapsed) {
			this._map.on('click', this.collapse, this);

			if (!android) {
				on(container, {
					mouseenter: this.expand,
					mouseleave: this.collapse
				}, this);
			}
		}

		var link = this._layersLink = create$1('a', className + '-toggle', container);
		link.href = '#';
		link.title = 'Layers';

		if (touch) {
			on(link, 'click', stop);
			on(link, 'click', this.expand, this);
		} else {
			on(link, 'focus', this.expand, this);
		}

		if (!collapsed) {
			this.expand();
		}

		this._baseLayersList = create$1('div', className + '-base', form);
		this._separator = create$1('div', className + '-separator', form);
		this._overlaysList = create$1('div', className + '-overlays', form);

		container.appendChild(form);
	},

	_getLayer: function (id) {
		for (var i = 0; i < this._layers.length; i++) {

			if (this._layers[i] && stamp(this._layers[i].layer) === id) {
				return this._layers[i];
			}
		}
	},

	_addLayer: function (layer, name, overlay) {
		if (this._map) {
			layer.on('add remove', this._onLayerChange, this);
		}

		this._layers.push({
			layer: layer,
			name: name,
			overlay: overlay
		});

		if (this.options.sortLayers) {
			this._layers.sort(bind(function (a, b) {
				return this.options.sortFunction(a.layer, b.layer, a.name, b.name);
			}, this));
		}

		if (this.options.autoZIndex && layer.setZIndex) {
			this._lastZIndex++;
			layer.setZIndex(this._lastZIndex);
		}

		this._expandIfNotCollapsed();
	},

	_update: function () {
		if (!this._container) { return this; }

		empty(this._baseLayersList);
		empty(this._overlaysList);

		this._layerControlInputs = [];
		var baseLayersPresent, overlaysPresent, i, obj, baseLayersCount = 0;

		for (i = 0; i < this._layers.length; i++) {
			obj = this._layers[i];
			this._addItem(obj);
			overlaysPresent = overlaysPresent || obj.overlay;
			baseLayersPresent = baseLayersPresent || !obj.overlay;
			baseLayersCount += !obj.overlay ? 1 : 0;
		}

		// Hide base layers section if there's only one layer.
		if (this.options.hideSingleBase) {
			baseLayersPresent = baseLayersPresent && baseLayersCount > 1;
			this._baseLayersList.style.display = baseLayersPresent ? '' : 'none';
		}

		this._separator.style.display = overlaysPresent && baseLayersPresent ? '' : 'none';

		return this;
	},

	_onLayerChange: function (e) {
		if (!this._handlingClick) {
			this._update();
		}

		var obj = this._getLayer(stamp(e.target));

		// @namespace Map
		// @section Layer events
		// @event baselayerchange: LayersControlEvent
		// Fired when the base layer is changed through the [layer control](#control-layers).
		// @event overlayadd: LayersControlEvent
		// Fired when an overlay is selected through the [layer control](#control-layers).
		// @event overlayremove: LayersControlEvent
		// Fired when an overlay is deselected through the [layer control](#control-layers).
		// @namespace Control.Layers
		var type = obj.overlay ?
			(e.type === 'add' ? 'overlayadd' : 'overlayremove') :
			(e.type === 'add' ? 'baselayerchange' : null);

		if (type) {
			this._map.fire(type, obj);
		}
	},

	// IE7 bugs out if you create a radio dynamically, so you have to do it this hacky way (see http://bit.ly/PqYLBe)
	_createRadioElement: function (name, checked) {

		var radioHtml = '<input type="radio" class="leaflet-control-layers-selector" name="' +
				name + '"' + (checked ? ' checked="checked"' : '') + '/>';

		var radioFragment = document.createElement('div');
		radioFragment.innerHTML = radioHtml;

		return radioFragment.firstChild;
	},

	_addItem: function (obj) {
		var label = document.createElement('label'),
		    checked = this._map.hasLayer(obj.layer),
		    input;

		if (obj.overlay) {
			input = document.createElement('input');
			input.type = 'checkbox';
			input.className = 'leaflet-control-layers-selector';
			input.defaultChecked = checked;
		} else {
			input = this._createRadioElement('leaflet-base-layers', checked);
		}

		this._layerControlInputs.push(input);
		input.layerId = stamp(obj.layer);

		on(input, 'click', this._onInputClick, this);

		var name = document.createElement('span');
		name.innerHTML = ' ' + obj.name;

		// Helps from preventing layer control flicker when checkboxes are disabled
		// https://github.com/Leaflet/Leaflet/issues/2771
		var holder = document.createElement('div');

		label.appendChild(holder);
		holder.appendChild(input);
		holder.appendChild(name);

		var container = obj.overlay ? this._overlaysList : this._baseLayersList;
		container.appendChild(label);

		this._checkDisabledLayers();
		return label;
	},

	_onInputClick: function () {
		var inputs = this._layerControlInputs,
		    input, layer;
		var addedLayers = [],
		    removedLayers = [];

		this._handlingClick = true;

		for (var i = inputs.length - 1; i >= 0; i--) {
			input = inputs[i];
			layer = this._getLayer(input.layerId).layer;

			if (input.checked) {
				addedLayers.push(layer);
			} else if (!input.checked) {
				removedLayers.push(layer);
			}
		}

		// Bugfix issue 2318: Should remove all old layers before readding new ones
		for (i = 0; i < removedLayers.length; i++) {
			if (this._map.hasLayer(removedLayers[i])) {
				this._map.removeLayer(removedLayers[i]);
			}
		}
		for (i = 0; i < addedLayers.length; i++) {
			if (!this._map.hasLayer(addedLayers[i])) {
				this._map.addLayer(addedLayers[i]);
			}
		}

		this._handlingClick = false;

		this._refocusOnMap();
	},

	_checkDisabledLayers: function () {
		var inputs = this._layerControlInputs,
		    input,
		    layer,
		    zoom = this._map.getZoom();

		for (var i = inputs.length - 1; i >= 0; i--) {
			input = inputs[i];
			layer = this._getLayer(input.layerId).layer;
			input.disabled = (layer.options.minZoom !== undefined && zoom < layer.options.minZoom) ||
			                 (layer.options.maxZoom !== undefined && zoom > layer.options.maxZoom);

		}
	},

	_expandIfNotCollapsed: function () {
		if (this._map && !this.options.collapsed) {
			this.expand();
		}
		return this;
	},

	_expand: function () {
		// Backward compatibility, remove me in 1.1.
		return this.expand();
	},

	_collapse: function () {
		// Backward compatibility, remove me in 1.1.
		return this.collapse();
	}

});


// @factory L.control.layers(baselayers?: Object, overlays?: Object, options?: Control.Layers options)
// Creates an attribution control with the given layers. Base layers will be switched with radio buttons, while overlays will be switched with checkboxes. Note that all base layers should be passed in the base layers object, but only one should be added to the map during map instantiation.
var layers = function (baseLayers, overlays, options) {
	return new Layers(baseLayers, overlays, options);
};

/*
 * @class Control.Zoom
 * @aka L.Control.Zoom
 * @inherits Control
 *
 * A basic zoom control with two buttons (zoom in and zoom out). It is put on the map by default unless you set its [`zoomControl` option](#map-zoomcontrol) to `false`. Extends `Control`.
 */

var Zoom = Control.extend({
	// @section
	// @aka Control.Zoom options
	options: {
		position: 'topleft',

		// @option zoomInText: String = '+'
		// The text set on the 'zoom in' button.
		zoomInText: '+',

		// @option zoomInTitle: String = 'Zoom in'
		// The title set on the 'zoom in' button.
		zoomInTitle: 'Zoom in',

		// @option zoomOutText: String = '&#x2212;'
		// The text set on the 'zoom out' button.
		zoomOutText: '&#x2212;',

		// @option zoomOutTitle: String = 'Zoom out'
		// The title set on the 'zoom out' button.
		zoomOutTitle: 'Zoom out'
	},

	onAdd: function (map) {
		var zoomName = 'leaflet-control-zoom',
		    container = create$1('div', zoomName + ' leaflet-bar'),
		    options = this.options;

		this._zoomInButton  = this._createButton(options.zoomInText, options.zoomInTitle,
		        zoomName + '-in',  container, this._zoomIn);
		this._zoomOutButton = this._createButton(options.zoomOutText, options.zoomOutTitle,
		        zoomName + '-out', container, this._zoomOut);

		this._updateDisabled();
		map.on('zoomend zoomlevelschange', this._updateDisabled, this);

		return container;
	},

	onRemove: function (map) {
		map.off('zoomend zoomlevelschange', this._updateDisabled, this);
	},

	disable: function () {
		this._disabled = true;
		this._updateDisabled();
		return this;
	},

	enable: function () {
		this._disabled = false;
		this._updateDisabled();
		return this;
	},

	_zoomIn: function (e) {
		if (!this._disabled && this._map._zoom < this._map.getMaxZoom()) {
			this._map.zoomIn(this._map.options.zoomDelta * (e.shiftKey ? 3 : 1));
		}
	},

	_zoomOut: function (e) {
		if (!this._disabled && this._map._zoom > this._map.getMinZoom()) {
			this._map.zoomOut(this._map.options.zoomDelta * (e.shiftKey ? 3 : 1));
		}
	},

	_createButton: function (html, title, className, container, fn) {
		var link = create$1('a', className, container);
		link.innerHTML = html;
		link.href = '#';
		link.title = title;

		/*
		 * Will force screen readers like VoiceOver to read this as "Zoom in - button"
		 */
		link.setAttribute('role', 'button');
		link.setAttribute('aria-label', title);

		disableClickPropagation(link);
		on(link, 'click', stop);
		on(link, 'click', fn, this);
		on(link, 'click', this._refocusOnMap, this);

		return link;
	},

	_updateDisabled: function () {
		var map = this._map,
		    className = 'leaflet-disabled';

		removeClass(this._zoomInButton, className);
		removeClass(this._zoomOutButton, className);

		if (this._disabled || map._zoom === map.getMinZoom()) {
			addClass(this._zoomOutButton, className);
		}
		if (this._disabled || map._zoom === map.getMaxZoom()) {
			addClass(this._zoomInButton, className);
		}
	}
});

// @namespace Map
// @section Control options
// @option zoomControl: Boolean = true
// Whether a [zoom control](#control-zoom) is added to the map by default.
Map.mergeOptions({
	zoomControl: true
});

Map.addInitHook(function () {
	if (this.options.zoomControl) {
		this.zoomControl = new Zoom();
		this.addControl(this.zoomControl);
	}
});

// @namespace Control.Zoom
// @factory L.control.zoom(options: Control.Zoom options)
// Creates a zoom control
var zoom = function (options) {
	return new Zoom(options);
};

/*
 * @class Control.Scale
 * @aka L.Control.Scale
 * @inherits Control
 *
 * A simple scale control that shows the scale of the current center of screen in metric (m/km) and imperial (mi/ft) systems. Extends `Control`.
 *
 * @example
 *
 * ```js
 * L.control.scale().addTo(map);
 * ```
 */

var Scale = Control.extend({
	// @section
	// @aka Control.Scale options
	options: {
		position: 'bottomleft',

		// @option maxWidth: Number = 100
		// Maximum width of the control in pixels. The width is set dynamically to show round values (e.g. 100, 200, 500).
		maxWidth: 100,

		// @option metric: Boolean = True
		// Whether to show the metric scale line (m/km).
		metric: true,

		// @option imperial: Boolean = True
		// Whether to show the imperial scale line (mi/ft).
		imperial: true

		// @option updateWhenIdle: Boolean = false
		// If `true`, the control is updated on [`moveend`](#map-moveend), otherwise it's always up-to-date (updated on [`move`](#map-move)).
	},

	onAdd: function (map) {
		var className = 'leaflet-control-scale',
		    container = create$1('div', className),
		    options = this.options;

		this._addScales(options, className + '-line', container);

		map.on(options.updateWhenIdle ? 'moveend' : 'move', this._update, this);
		map.whenReady(this._update, this);

		return container;
	},

	onRemove: function (map) {
		map.off(this.options.updateWhenIdle ? 'moveend' : 'move', this._update, this);
	},

	_addScales: function (options, className, container) {
		if (options.metric) {
			this._mScale = create$1('div', className, container);
		}
		if (options.imperial) {
			this._iScale = create$1('div', className, container);
		}
	},

	_update: function () {
		var map = this._map,
		    y = map.getSize().y / 2;

		var maxMeters = map.distance(
				map.containerPointToLatLng([0, y]),
				map.containerPointToLatLng([this.options.maxWidth, y]));

		this._updateScales(maxMeters);
	},

	_updateScales: function (maxMeters) {
		if (this.options.metric && maxMeters) {
			this._updateMetric(maxMeters);
		}
		if (this.options.imperial && maxMeters) {
			this._updateImperial(maxMeters);
		}
	},

	_updateMetric: function (maxMeters) {
		var meters = this._getRoundNum(maxMeters),
		    label = meters < 1000 ? meters + ' m' : (meters / 1000) + ' km';

		this._updateScale(this._mScale, label, meters / maxMeters);
	},

	_updateImperial: function (maxMeters) {
		var maxFeet = maxMeters * 3.2808399,
		    maxMiles, miles, feet;

		if (maxFeet > 5280) {
			maxMiles = maxFeet / 5280;
			miles = this._getRoundNum(maxMiles);
			this._updateScale(this._iScale, miles + ' mi', miles / maxMiles);

		} else {
			feet = this._getRoundNum(maxFeet);
			this._updateScale(this._iScale, feet + ' ft', feet / maxFeet);
		}
	},

	_updateScale: function (scale, text, ratio) {
		scale.style.width = Math.round(this.options.maxWidth * ratio) + 'px';
		scale.innerHTML = text;
	},

	_getRoundNum: function (num) {
		var pow10 = Math.pow(10, (Math.floor(num) + '').length - 1),
		    d = num / pow10;

		d = d >= 10 ? 10 :
		    d >= 5 ? 5 :
		    d >= 3 ? 3 :
		    d >= 2 ? 2 : 1;

		return pow10 * d;
	}
});


// @factory L.control.scale(options?: Control.Scale options)
// Creates an scale control with the given options.
var scale = function (options) {
	return new Scale(options);
};

/*
 * @class Control.Attribution
 * @aka L.Control.Attribution
 * @inherits Control
 *
 * The attribution control allows you to display attribution data in a small text box on a map. It is put on the map by default unless you set its [`attributionControl` option](#map-attributioncontrol) to `false`, and it fetches attribution texts from layers with the [`getAttribution` method](#layer-getattribution) automatically. Extends Control.
 */

var Attribution = Control.extend({
	// @section
	// @aka Control.Attribution options
	options: {
		position: 'bottomright',

		// @option prefix: String = 'Leaflet'
		// The HTML text shown before the attributions. Pass `false` to disable.
		prefix: '<a href="http://leafletjs.com" title="A JS library for interactive maps">Leaflet</a>'
	},

	initialize: function (options) {
		setOptions(this, options);

		this._attributions = {};
	},

	onAdd: function (map) {
		map.attributionControl = this;
		this._container = create$1('div', 'leaflet-control-attribution');
		disableClickPropagation(this._container);

		// TODO ugly, refactor
		for (var i in map._layers) {
			if (map._layers[i].getAttribution) {
				this.addAttribution(map._layers[i].getAttribution());
			}
		}

		this._update();

		return this._container;
	},

	// @method setPrefix(prefix: String): this
	// Sets the text before the attributions.
	setPrefix: function (prefix) {
		this.options.prefix = prefix;
		this._update();
		return this;
	},

	// @method addAttribution(text: String): this
	// Adds an attribution text (e.g. `'Vector data &copy; Mapbox'`).
	addAttribution: function (text) {
		if (!text) { return this; }

		if (!this._attributions[text]) {
			this._attributions[text] = 0;
		}
		this._attributions[text]++;

		this._update();

		return this;
	},

	// @method removeAttribution(text: String): this
	// Removes an attribution text.
	removeAttribution: function (text) {
		if (!text) { return this; }

		if (this._attributions[text]) {
			this._attributions[text]--;
			this._update();
		}

		return this;
	},

	_update: function () {
		if (!this._map) { return; }

		var attribs = [];

		for (var i in this._attributions) {
			if (this._attributions[i]) {
				attribs.push(i);
			}
		}

		var prefixAndAttribs = [];

		if (this.options.prefix) {
			prefixAndAttribs.push(this.options.prefix);
		}
		if (attribs.length) {
			prefixAndAttribs.push(attribs.join(', '));
		}

		this._container.innerHTML = prefixAndAttribs.join(' | ');
	}
});

// @namespace Map
// @section Control options
// @option attributionControl: Boolean = true
// Whether a [attribution control](#control-attribution) is added to the map by default.
Map.mergeOptions({
	attributionControl: true
});

Map.addInitHook(function () {
	if (this.options.attributionControl) {
		new Attribution().addTo(this);
	}
});

// @namespace Control.Attribution
// @factory L.control.attribution(options: Control.Attribution options)
// Creates an attribution control.
var attribution = function (options) {
	return new Attribution(options);
};

Control.Layers = Layers;
Control.Zoom = Zoom;
Control.Scale = Scale;
Control.Attribution = Attribution;

control.layers = layers;
control.zoom = zoom;
control.scale = scale;
control.attribution = attribution;

/*
	L.Handler is a base class for handler classes that are used internally to inject
	interaction features like dragging to classes like Map and Marker.
*/

// @class Handler
// @aka L.Handler
// Abstract class for map interaction handlers

var Handler = Class.extend({
	initialize: function (map) {
		this._map = map;
	},

	// @method enable(): this
	// Enables the handler
	enable: function () {
		if (this._enabled) { return this; }

		this._enabled = true;
		this.addHooks();
		return this;
	},

	// @method disable(): this
	// Disables the handler
	disable: function () {
		if (!this._enabled) { return this; }

		this._enabled = false;
		this.removeHooks();
		return this;
	},

	// @method enabled(): Boolean
	// Returns `true` if the handler is enabled
	enabled: function () {
		return !!this._enabled;
	}

	// @section Extension methods
	// Classes inheriting from `Handler` must implement the two following methods:
	// @method addHooks()
	// Called when the handler is enabled, should add event hooks.
	// @method removeHooks()
	// Called when the handler is disabled, should remove the event hooks added previously.
});

var Mixin = {Events: Events};

/*
 * @class Draggable
 * @aka L.Draggable
 * @inherits Evented
 *
 * A class for making DOM elements draggable (including touch support).
 * Used internally for map and marker dragging. Only works for elements
 * that were positioned with [`L.DomUtil.setPosition`](#domutil-setposition).
 *
 * @example
 * ```js
 * var draggable = new L.Draggable(elementToDrag);
 * draggable.enable();
 * ```
 */

var START = touch ? 'touchstart mousedown' : 'mousedown';
var END = {
	mousedown: 'mouseup',
	touchstart: 'touchend',
	pointerdown: 'touchend',
	MSPointerDown: 'touchend'
};
var MOVE = {
	mousedown: 'mousemove',
	touchstart: 'touchmove',
	pointerdown: 'touchmove',
	MSPointerDown: 'touchmove'
};


var Draggable = Evented.extend({

	options: {
		// @section
		// @aka Draggable options
		// @option clickTolerance: Number = 3
		// The max number of pixels a user can shift the mouse pointer during a click
		// for it to be considered a valid click (as opposed to a mouse drag).
		clickTolerance: 3
	},

	// @constructor L.Draggable(el: HTMLElement, dragHandle?: HTMLElement, preventOutline?: Boolean, options?: Draggable options)
	// Creates a `Draggable` object for moving `el` when you start dragging the `dragHandle` element (equals `el` itself by default).
	initialize: function (element, dragStartTarget, preventOutline$$1, options) {
		setOptions(this, options);

		this._element = element;
		this._dragStartTarget = dragStartTarget || element;
		this._preventOutline = preventOutline$$1;
	},

	// @method enable()
	// Enables the dragging ability
	enable: function () {
		if (this._enabled) { return; }

		on(this._dragStartTarget, START, this._onDown, this);

		this._enabled = true;
	},

	// @method disable()
	// Disables the dragging ability
	disable: function () {
		if (!this._enabled) { return; }

		// If we're currently dragging this draggable,
		// disabling it counts as first ending the drag.
		if (Draggable._dragging === this) {
			this.finishDrag();
		}

		off(this._dragStartTarget, START, this._onDown, this);

		this._enabled = false;
		this._moved = false;
	},

	_onDown: function (e) {
		// Ignore simulated events, since we handle both touch and
		// mouse explicitly; otherwise we risk getting duplicates of
		// touch events, see #4315.
		// Also ignore the event if disabled; this happens in IE11
		// under some circumstances, see #3666.
		if (e._simulated || !this._enabled) { return; }

		this._moved = false;

		if (hasClass(this._element, 'leaflet-zoom-anim')) { return; }

		if (Draggable._dragging || e.shiftKey || ((e.which !== 1) && (e.button !== 1) && !e.touches)) { return; }
		Draggable._dragging = this;  // Prevent dragging multiple objects at once.

		if (this._preventOutline) {
			preventOutline(this._element);
		}

		disableImageDrag();
		disableTextSelection();

		if (this._moving) { return; }

		// @event down: Event
		// Fired when a drag is about to start.
		this.fire('down');

		var first = e.touches ? e.touches[0] : e;

		this._startPoint = new Point(first.clientX, first.clientY);

		on(document, MOVE[e.type], this._onMove, this);
		on(document, END[e.type], this._onUp, this);
	},

	_onMove: function (e) {
		// Ignore simulated events, since we handle both touch and
		// mouse explicitly; otherwise we risk getting duplicates of
		// touch events, see #4315.
		// Also ignore the event if disabled; this happens in IE11
		// under some circumstances, see #3666.
		if (e._simulated || !this._enabled) { return; }

		if (e.touches && e.touches.length > 1) {
			this._moved = true;
			return;
		}

		var first = (e.touches && e.touches.length === 1 ? e.touches[0] : e),
		    newPoint = new Point(first.clientX, first.clientY),
		    offset = newPoint.subtract(this._startPoint);

		if (!offset.x && !offset.y) { return; }
		if (Math.abs(offset.x) + Math.abs(offset.y) < this.options.clickTolerance) { return; }

		preventDefault(e);

		if (!this._moved) {
			// @event dragstart: Event
			// Fired when a drag starts
			this.fire('dragstart');

			this._moved = true;
			this._startPos = getPosition(this._element).subtract(offset);

			addClass(document.body, 'leaflet-dragging');

			this._lastTarget = e.target || e.srcElement;
			// IE and Edge do not give the <use> element, so fetch it
			// if necessary
			if ((window.SVGElementInstance) && (this._lastTarget instanceof SVGElementInstance)) {
				this._lastTarget = this._lastTarget.correspondingUseElement;
			}
			addClass(this._lastTarget, 'leaflet-drag-target');
		}

		this._newPos = this._startPos.add(offset);
		this._moving = true;

		cancelAnimFrame(this._animRequest);
		this._lastEvent = e;
		this._animRequest = requestAnimFrame(this._updatePosition, this, true);
	},

	_updatePosition: function () {
		var e = {originalEvent: this._lastEvent};

		// @event predrag: Event
		// Fired continuously during dragging *before* each corresponding
		// update of the element's position.
		this.fire('predrag', e);
		setPosition(this._element, this._newPos);

		// @event drag: Event
		// Fired continuously during dragging.
		this.fire('drag', e);
	},

	_onUp: function (e) {
		// Ignore simulated events, since we handle both touch and
		// mouse explicitly; otherwise we risk getting duplicates of
		// touch events, see #4315.
		// Also ignore the event if disabled; this happens in IE11
		// under some circumstances, see #3666.
		if (e._simulated || !this._enabled) { return; }
		this.finishDrag();
	},

	finishDrag: function () {
		removeClass(document.body, 'leaflet-dragging');

		if (this._lastTarget) {
			removeClass(this._lastTarget, 'leaflet-drag-target');
			this._lastTarget = null;
		}

		for (var i in MOVE) {
			off(document, MOVE[i], this._onMove, this);
			off(document, END[i], this._onUp, this);
		}

		enableImageDrag();
		enableTextSelection();

		if (this._moved && this._moving) {
			// ensure drag is not fired after dragend
			cancelAnimFrame(this._animRequest);

			// @event dragend: DragEndEvent
			// Fired when the drag ends.
			this.fire('dragend', {
				distance: this._newPos.distanceTo(this._startPos)
			});
		}

		this._moving = false;
		Draggable._dragging = false;
	}

});

/*
 * @namespace LineUtil
 *
 * Various utility functions for polyine points processing, used by Leaflet internally to make polylines lightning-fast.
 */

// Simplify polyline with vertex reduction and Douglas-Peucker simplification.
// Improves rendering performance dramatically by lessening the number of points to draw.

// @function simplify(points: Point[], tolerance: Number): Point[]
// Dramatically reduces the number of points in a polyline while retaining
// its shape and returns a new array of simplified points, using the
// [Douglas-Peucker algorithm](http://en.wikipedia.org/wiki/Douglas-Peucker_algorithm).
// Used for a huge performance boost when processing/displaying Leaflet polylines for
// each zoom level and also reducing visual noise. tolerance affects the amount of
// simplification (lesser value means higher quality but slower and with more points).
// Also released as a separated micro-library [Simplify.js](http://mourner.github.com/simplify-js/).
function simplify(points, tolerance) {
	if (!tolerance || !points.length) {
		return points.slice();
	}

	var sqTolerance = tolerance * tolerance;

	    // stage 1: vertex reduction
	    points = _reducePoints(points, sqTolerance);

	    // stage 2: Douglas-Peucker simplification
	    points = _simplifyDP(points, sqTolerance);

	return points;
}

// @function pointToSegmentDistance(p: Point, p1: Point, p2: Point): Number
// Returns the distance between point `p` and segment `p1` to `p2`.
function pointToSegmentDistance(p, p1, p2) {
	return Math.sqrt(_sqClosestPointOnSegment(p, p1, p2, true));
}

// @function closestPointOnSegment(p: Point, p1: Point, p2: Point): Number
// Returns the closest point from a point `p` on a segment `p1` to `p2`.
function closestPointOnSegment(p, p1, p2) {
	return _sqClosestPointOnSegment(p, p1, p2);
}

// Douglas-Peucker simplification, see http://en.wikipedia.org/wiki/Douglas-Peucker_algorithm
function _simplifyDP(points, sqTolerance) {

	var len = points.length,
	    ArrayConstructor = typeof Uint8Array !== undefined + '' ? Uint8Array : Array,
	    markers = new ArrayConstructor(len);

	    markers[0] = markers[len - 1] = 1;

	_simplifyDPStep(points, markers, sqTolerance, 0, len - 1);

	var i,
	    newPoints = [];

	for (i = 0; i < len; i++) {
		if (markers[i]) {
			newPoints.push(points[i]);
		}
	}

	return newPoints;
}

function _simplifyDPStep(points, markers, sqTolerance, first, last) {

	var maxSqDist = 0,
	index, i, sqDist;

	for (i = first + 1; i <= last - 1; i++) {
		sqDist = _sqClosestPointOnSegment(points[i], points[first], points[last], true);

		if (sqDist > maxSqDist) {
			index = i;
			maxSqDist = sqDist;
		}
	}

	if (maxSqDist > sqTolerance) {
		markers[index] = 1;

		_simplifyDPStep(points, markers, sqTolerance, first, index);
		_simplifyDPStep(points, markers, sqTolerance, index, last);
	}
}

// reduce points that are too close to each other to a single point
function _reducePoints(points, sqTolerance) {
	var reducedPoints = [points[0]];

	for (var i = 1, prev = 0, len = points.length; i < len; i++) {
		if (_sqDist(points[i], points[prev]) > sqTolerance) {
			reducedPoints.push(points[i]);
			prev = i;
		}
	}
	if (prev < len - 1) {
		reducedPoints.push(points[len - 1]);
	}
	return reducedPoints;
}

var _lastCode;

// @function clipSegment(a: Point, b: Point, bounds: Bounds, useLastCode?: Boolean, round?: Boolean): Point[]|Boolean
// Clips the segment a to b by rectangular bounds with the
// [Cohen-Sutherland algorithm](https://en.wikipedia.org/wiki/Cohen%E2%80%93Sutherland_algorithm)
// (modifying the segment points directly!). Used by Leaflet to only show polyline
// points that are on the screen or near, increasing performance.
function clipSegment(a, b, bounds, useLastCode, round) {
	var codeA = useLastCode ? _lastCode : _getBitCode(a, bounds),
	    codeB = _getBitCode(b, bounds),

	    codeOut, p, newCode;

	    // save 2nd code to avoid calculating it on the next segment
	    _lastCode = codeB;

	while (true) {
		// if a,b is inside the clip window (trivial accept)
		if (!(codeA | codeB)) {
			return [a, b];
		}

		// if a,b is outside the clip window (trivial reject)
		if (codeA & codeB) {
			return false;
		}

		// other cases
		codeOut = codeA || codeB;
		p = _getEdgeIntersection(a, b, codeOut, bounds, round);
		newCode = _getBitCode(p, bounds);

		if (codeOut === codeA) {
			a = p;
			codeA = newCode;
		} else {
			b = p;
			codeB = newCode;
		}
	}
}

function _getEdgeIntersection(a, b, code, bounds, round) {
	var dx = b.x - a.x,
	    dy = b.y - a.y,
	    min = bounds.min,
	    max = bounds.max,
	    x, y;

	if (code & 8) { // top
		x = a.x + dx * (max.y - a.y) / dy;
		y = max.y;

	} else if (code & 4) { // bottom
		x = a.x + dx * (min.y - a.y) / dy;
		y = min.y;

	} else if (code & 2) { // right
		x = max.x;
		y = a.y + dy * (max.x - a.x) / dx;

	} else if (code & 1) { // left
		x = min.x;
		y = a.y + dy * (min.x - a.x) / dx;
	}

	return new Point(x, y, round);
}

function _getBitCode(p, bounds) {
	var code = 0;

	if (p.x < bounds.min.x) { // left
		code |= 1;
	} else if (p.x > bounds.max.x) { // right
		code |= 2;
	}

	if (p.y < bounds.min.y) { // bottom
		code |= 4;
	} else if (p.y > bounds.max.y) { // top
		code |= 8;
	}

	return code;
}

// square distance (to avoid unnecessary Math.sqrt calls)
function _sqDist(p1, p2) {
	var dx = p2.x - p1.x,
	    dy = p2.y - p1.y;
	return dx * dx + dy * dy;
}

// return closest point on segment or distance to that point
function _sqClosestPointOnSegment(p, p1, p2, sqDist) {
	var x = p1.x,
	    y = p1.y,
	    dx = p2.x - x,
	    dy = p2.y - y,
	    dot = dx * dx + dy * dy,
	    t;

	if (dot > 0) {
		t = ((p.x - x) * dx + (p.y - y) * dy) / dot;

		if (t > 1) {
			x = p2.x;
			y = p2.y;
		} else if (t > 0) {
			x += dx * t;
			y += dy * t;
		}
	}

	dx = p.x - x;
	dy = p.y - y;

	return sqDist ? dx * dx + dy * dy : new Point(x, y);
}


// @function isFlat(latlngs: LatLng[]): Boolean
// Returns true if `latlngs` is a flat array, false is nested.
function isFlat(latlngs) {
	return !isArray(latlngs[0]) || (typeof latlngs[0][0] !== 'object' && typeof latlngs[0][0] !== 'undefined');
}

function _flat(latlngs) {
	console.warn('Deprecated use of _flat, please use L.LineUtil.isFlat instead.');
	return isFlat(latlngs);
}


var LineUtil = (Object.freeze || Object)({
	simplify: simplify,
	pointToSegmentDistance: pointToSegmentDistance,
	closestPointOnSegment: closestPointOnSegment,
	clipSegment: clipSegment,
	_getEdgeIntersection: _getEdgeIntersection,
	_getBitCode: _getBitCode,
	_sqClosestPointOnSegment: _sqClosestPointOnSegment,
	isFlat: isFlat,
	_flat: _flat
});

/*
 * @namespace PolyUtil
 * Various utility functions for polygon geometries.
 */

/* @function clipPolygon(points: Point[], bounds: Bounds, round?: Boolean): Point[]
 * Clips the polygon geometry defined by the given `points` by the given bounds (using the [Sutherland-Hodgeman algorithm](https://en.wikipedia.org/wiki/Sutherland%E2%80%93Hodgman_algorithm)).
 * Used by Leaflet to only show polygon points that are on the screen or near, increasing
 * performance. Note that polygon points needs different algorithm for clipping
 * than polyline, so there's a seperate method for it.
 */
function clipPolygon(points, bounds, round) {
	var clippedPoints,
	    edges = [1, 4, 2, 8],
	    i, j, k,
	    a, b,
	    len, edge, p;

	for (i = 0, len = points.length; i < len; i++) {
		points[i]._code = _getBitCode(points[i], bounds);
	}

	// for each edge (left, bottom, right, top)
	for (k = 0; k < 4; k++) {
		edge = edges[k];
		clippedPoints = [];

		for (i = 0, len = points.length, j = len - 1; i < len; j = i++) {
			a = points[i];
			b = points[j];

			// if a is inside the clip window
			if (!(a._code & edge)) {
				// if b is outside the clip window (a->b goes out of screen)
				if (b._code & edge) {
					p = _getEdgeIntersection(b, a, edge, bounds, round);
					p._code = _getBitCode(p, bounds);
					clippedPoints.push(p);
				}
				clippedPoints.push(a);

			// else if b is inside the clip window (a->b enters the screen)
			} else if (!(b._code & edge)) {
				p = _getEdgeIntersection(b, a, edge, bounds, round);
				p._code = _getBitCode(p, bounds);
				clippedPoints.push(p);
			}
		}
		points = clippedPoints;
	}

	return points;
}


var PolyUtil = (Object.freeze || Object)({
	clipPolygon: clipPolygon
});

/*
 * @namespace Projection
 * @section
 * Leaflet comes with a set of already defined Projections out of the box:
 *
 * @projection L.Projection.LonLat
 *
 * Equirectangular, or Plate Carree projection — the most simple projection,
 * mostly used by GIS enthusiasts. Directly maps `x` as longitude, and `y` as
 * latitude. Also suitable for flat worlds, e.g. game maps. Used by the
 * `EPSG:4326` and `Simple` CRS.
 */

var LonLat = {
	project: function (latlng) {
		return new Point(latlng.lng, latlng.lat);
	},

	unproject: function (point) {
		return new LatLng(point.y, point.x);
	},

	bounds: new Bounds([-180, -90], [180, 90])
};

/*
 * @namespace Projection
 * @projection L.Projection.Mercator
 *
 * Elliptical Mercator projection — more complex than Spherical Mercator. Takes into account that Earth is a geoid, not a perfect sphere. Used by the EPSG:3395 CRS.
 */

var Mercator = {
	R: 6378137,
	R_MINOR: 6356752.314245179,

	bounds: new Bounds([-20037508.34279, -15496570.73972], [20037508.34279, 18764656.23138]),

	project: function (latlng) {
		var d = Math.PI / 180,
		    r = this.R,
		    y = latlng.lat * d,
		    tmp = this.R_MINOR / r,
		    e = Math.sqrt(1 - tmp * tmp),
		    con = e * Math.sin(y);

		var ts = Math.tan(Math.PI / 4 - y / 2) / Math.pow((1 - con) / (1 + con), e / 2);
		y = -r * Math.log(Math.max(ts, 1E-10));

		return new Point(latlng.lng * d * r, y);
	},

	unproject: function (point) {
		var d = 180 / Math.PI,
		    r = this.R,
		    tmp = this.R_MINOR / r,
		    e = Math.sqrt(1 - tmp * tmp),
		    ts = Math.exp(-point.y / r),
		    phi = Math.PI / 2 - 2 * Math.atan(ts);

		for (var i = 0, dphi = 0.1, con; i < 15 && Math.abs(dphi) > 1e-7; i++) {
			con = e * Math.sin(phi);
			con = Math.pow((1 - con) / (1 + con), e / 2);
			dphi = Math.PI / 2 - 2 * Math.atan(ts * con) - phi;
			phi += dphi;
		}

		return new LatLng(phi * d, point.x * d / r);
	}
};

/*
 * @class Projection

 * An object with methods for projecting geographical coordinates of the world onto
 * a flat surface (and back). See [Map projection](http://en.wikipedia.org/wiki/Map_projection).

 * @property bounds: Bounds
 * The bounds (specified in CRS units) where the projection is valid

 * @method project(latlng: LatLng): Point
 * Projects geographical coordinates into a 2D point.
 * Only accepts actual `L.LatLng` instances, not arrays.

 * @method unproject(point: Point): LatLng
 * The inverse of `project`. Projects a 2D point into a geographical location.
 * Only accepts actual `L.Point` instances, not arrays.

 */




var index = (Object.freeze || Object)({
	LonLat: LonLat,
	Mercator: Mercator,
	SphericalMercator: SphericalMercator
});

/*
 * @namespace CRS
 * @crs L.CRS.EPSG3395
 *
 * Rarely used by some commercial tile providers. Uses Elliptical Mercator projection.
 */
var EPSG3395 = extend({}, Earth, {
	code: 'EPSG:3395',
	projection: Mercator,

	transformation: (function () {
		var scale = 0.5 / (Math.PI * Mercator.R);
		return toTransformation(scale, 0.5, -scale, 0.5);
	}())
});

/*
 * @namespace CRS
 * @crs L.CRS.EPSG4326
 *
 * A common CRS among GIS enthusiasts. Uses simple Equirectangular projection.
 *
 * Leaflet 1.0.x complies with the [TMS coordinate scheme for EPSG:4326](https://wiki.osgeo.org/wiki/Tile_Map_Service_Specification#global-geodetic),
 * which is a breaking change from 0.7.x behaviour.  If you are using a `TileLayer`
 * with this CRS, ensure that there are two 256x256 pixel tiles covering the
 * whole earth at zoom level zero, and that the tile coordinate origin is (-180,+90),
 * or (-180,-90) for `TileLayer`s with [the `tms` option](#tilelayer-tms) set.
 */

var EPSG4326 = extend({}, Earth, {
	code: 'EPSG:4326',
	projection: LonLat,
	transformation: toTransformation(1 / 180, 1, -1 / 180, 0.5)
});

/*
 * @namespace CRS
 * @crs L.CRS.Simple
 *
 * A simple CRS that maps longitude and latitude into `x` and `y` directly.
 * May be used for maps of flat surfaces (e.g. game maps). Note that the `y`
 * axis should still be inverted (going from bottom to top). `distance()` returns
 * simple euclidean distance.
 */

var Simple = extend({}, CRS, {
	projection: LonLat,
	transformation: toTransformation(1, 0, -1, 0),

	scale: function (zoom) {
		return Math.pow(2, zoom);
	},

	zoom: function (scale) {
		return Math.log(scale) / Math.LN2;
	},

	distance: function (latlng1, latlng2) {
		var dx = latlng2.lng - latlng1.lng,
		    dy = latlng2.lat - latlng1.lat;

		return Math.sqrt(dx * dx + dy * dy);
	},

	infinite: true
});

CRS.Earth = Earth;
CRS.EPSG3395 = EPSG3395;
CRS.EPSG3857 = EPSG3857;
CRS.EPSG900913 = EPSG900913;
CRS.EPSG4326 = EPSG4326;
CRS.Simple = Simple;

/*
 * @class Layer
 * @inherits Evented
 * @aka L.Layer
 * @aka ILayer
 *
 * A set of methods from the Layer base class that all Leaflet layers use.
 * Inherits all methods, options and events from `L.Evented`.
 *
 * @example
 *
 * ```js
 * var layer = L.Marker(latlng).addTo(map);
 * layer.addTo(map);
 * layer.remove();
 * ```
 *
 * @event add: Event
 * Fired after the layer is added to a map
 *
 * @event remove: Event
 * Fired after the layer is removed from a map
 */


var Layer = Evented.extend({

	// Classes extending `L.Layer` will inherit the following options:
	options: {
		// @option pane: String = 'overlayPane'
		// By default the layer will be added to the map's [overlay pane](#map-overlaypane). Overriding this option will cause the layer to be placed on another pane by default.
		pane: 'overlayPane',

		// @option attribution: String = null
		// String to be shown in the attribution control, describes the layer data, e.g. "© Mapbox".
		attribution: null,

		bubblingMouseEvents: true
	},

	/* @section
	 * Classes extending `L.Layer` will inherit the following methods:
	 *
	 * @method addTo(map: Map|LayerGroup): this
	 * Adds the layer to the given map or layer group.
	 */
	addTo: function (map) {
		map.addLayer(this);
		return this;
	},

	// @method remove: this
	// Removes the layer from the map it is currently active on.
	remove: function () {
		return this.removeFrom(this._map || this._mapToAdd);
	},

	// @method removeFrom(map: Map): this
	// Removes the layer from the given map
	removeFrom: function (obj) {
		if (obj) {
			obj.removeLayer(this);
		}
		return this;
	},

	// @method getPane(name? : String): HTMLElement
	// Returns the `HTMLElement` representing the named pane on the map. If `name` is omitted, returns the pane for this layer.
	getPane: function (name) {
		return this._map.getPane(name ? (this.options[name] || name) : this.options.pane);
	},

	addInteractiveTarget: function (targetEl) {
		this._map._targets[stamp(targetEl)] = this;
		return this;
	},

	removeInteractiveTarget: function (targetEl) {
		delete this._map._targets[stamp(targetEl)];
		return this;
	},

	// @method getAttribution: String
	// Used by the `attribution control`, returns the [attribution option](#gridlayer-attribution).
	getAttribution: function () {
		return this.options.attribution;
	},

	_layerAdd: function (e) {
		var map = e.target;

		// check in case layer gets added and then removed before the map is ready
		if (!map.hasLayer(this)) { return; }

		this._map = map;
		this._zoomAnimated = map._zoomAnimated;

		if (this.getEvents) {
			var events = this.getEvents();
			map.on(events, this);
			this.once('remove', function () {
				map.off(events, this);
			}, this);
		}

		this.onAdd(map);

		if (this.getAttribution && map.attributionControl) {
			map.attributionControl.addAttribution(this.getAttribution());
		}

		this.fire('add');
		map.fire('layeradd', {layer: this});
	}
});

/* @section Extension methods
 * @uninheritable
 *
 * Every layer should extend from `L.Layer` and (re-)implement the following methods.
 *
 * @method onAdd(map: Map): this
 * Should contain code that creates DOM elements for the layer, adds them to `map panes` where they should belong and puts listeners on relevant map events. Called on [`map.addLayer(layer)`](#map-addlayer).
 *
 * @method onRemove(map: Map): this
 * Should contain all clean up code that removes the layer's elements from the DOM and removes listeners previously added in [`onAdd`](#layer-onadd). Called on [`map.removeLayer(layer)`](#map-removelayer).
 *
 * @method getEvents(): Object
 * This optional method should return an object like `{ viewreset: this._reset }` for [`addEventListener`](#evented-addeventlistener). The event handlers in this object will be automatically added and removed from the map with your layer.
 *
 * @method getAttribution(): String
 * This optional method should return a string containing HTML to be shown on the `Attribution control` whenever the layer is visible.
 *
 * @method beforeAdd(map: Map): this
 * Optional method. Called on [`map.addLayer(layer)`](#map-addlayer), before the layer is added to the map, before events are initialized, without waiting until the map is in a usable state. Use for early initialization only.
 */


/* @namespace Map
 * @section Layer events
 *
 * @event layeradd: LayerEvent
 * Fired when a new layer is added to the map.
 *
 * @event layerremove: LayerEvent
 * Fired when some layer is removed from the map
 *
 * @section Methods for Layers and Controls
 */
Map.include({
	// @method addLayer(layer: Layer): this
	// Adds the given layer to the map
	addLayer: function (layer) {
		if (!layer._layerAdd) {
			throw new Error('The provided object is not a Layer.');
		}

		var id = stamp(layer);
		if (this._layers[id]) { return this; }
		this._layers[id] = layer;

		layer._mapToAdd = this;

		if (layer.beforeAdd) {
			layer.beforeAdd(this);
		}

		this.whenReady(layer._layerAdd, layer);

		return this;
	},

	// @method removeLayer(layer: Layer): this
	// Removes the given layer from the map.
	removeLayer: function (layer) {
		var id = stamp(layer);

		if (!this._layers[id]) { return this; }

		if (this._loaded) {
			layer.onRemove(this);
		}

		if (layer.getAttribution && this.attributionControl) {
			this.attributionControl.removeAttribution(layer.getAttribution());
		}

		delete this._layers[id];

		if (this._loaded) {
			this.fire('layerremove', {layer: layer});
			layer.fire('remove');
		}

		layer._map = layer._mapToAdd = null;

		return this;
	},

	// @method hasLayer(layer: Layer): Boolean
	// Returns `true` if the given layer is currently added to the map
	hasLayer: function (layer) {
		return !!layer && (stamp(layer) in this._layers);
	},

	/* @method eachLayer(fn: Function, context?: Object): this
	 * Iterates over the layers of the map, optionally specifying context of the iterator function.
	 * ```
	 * map.eachLayer(function(layer){
	 *     layer.bindPopup('Hello');
	 * });
	 * ```
	 */
	eachLayer: function (method, context) {
		for (var i in this._layers) {
			method.call(context, this._layers[i]);
		}
		return this;
	},

	_addLayers: function (layers) {
		layers = layers ? (isArray(layers) ? layers : [layers]) : [];

		for (var i = 0, len = layers.length; i < len; i++) {
			this.addLayer(layers[i]);
		}
	},

	_addZoomLimit: function (layer) {
		if (isNaN(layer.options.maxZoom) || !isNaN(layer.options.minZoom)) {
			this._zoomBoundLayers[stamp(layer)] = layer;
			this._updateZoomLevels();
		}
	},

	_removeZoomLimit: function (layer) {
		var id = stamp(layer);

		if (this._zoomBoundLayers[id]) {
			delete this._zoomBoundLayers[id];
			this._updateZoomLevels();
		}
	},

	_updateZoomLevels: function () {
		var minZoom = Infinity,
		    maxZoom = -Infinity,
		    oldZoomSpan = this._getZoomSpan();

		for (var i in this._zoomBoundLayers) {
			var options = this._zoomBoundLayers[i].options;

			minZoom = options.minZoom === undefined ? minZoom : Math.min(minZoom, options.minZoom);
			maxZoom = options.maxZoom === undefined ? maxZoom : Math.max(maxZoom, options.maxZoom);
		}

		this._layersMaxZoom = maxZoom === -Infinity ? undefined : maxZoom;
		this._layersMinZoom = minZoom === Infinity ? undefined : minZoom;

		// @section Map state change events
		// @event zoomlevelschange: Event
		// Fired when the number of zoomlevels on the map is changed due
		// to adding or removing a layer.
		if (oldZoomSpan !== this._getZoomSpan()) {
			this.fire('zoomlevelschange');
		}

		if (this.options.maxZoom === undefined && this._layersMaxZoom && this.getZoom() > this._layersMaxZoom) {
			this.setZoom(this._layersMaxZoom);
		}
		if (this.options.minZoom === undefined && this._layersMinZoom && this.getZoom() < this._layersMinZoom) {
			this.setZoom(this._layersMinZoom);
		}
	}
});

/*
 * @class LayerGroup
 * @aka L.LayerGroup
 * @inherits Layer
 *
 * Used to group several layers and handle them as one. If you add it to the map,
 * any layers added or removed from the group will be added/removed on the map as
 * well. Extends `Layer`.
 *
 * @example
 *
 * ```js
 * L.layerGroup([marker1, marker2])
 * 	.addLayer(polyline)
 * 	.addTo(map);
 * ```
 */

var LayerGroup = Layer.extend({

	initialize: function (layers) {
		this._layers = {};

		var i, len;

		if (layers) {
			for (i = 0, len = layers.length; i < len; i++) {
				this.addLayer(layers[i]);
			}
		}
	},

	// @method addLayer(layer: Layer): this
	// Adds the given layer to the group.
	addLayer: function (layer) {
		var id = this.getLayerId(layer);

		this._layers[id] = layer;

		if (this._map) {
			this._map.addLayer(layer);
		}

		return this;
	},

	// @method removeLayer(layer: Layer): this
	// Removes the given layer from the group.
	// @alternative
	// @method removeLayer(id: Number): this
	// Removes the layer with the given internal ID from the group.
	removeLayer: function (layer) {
		var id = layer in this._layers ? layer : this.getLayerId(layer);

		if (this._map && this._layers[id]) {
			this._map.removeLayer(this._layers[id]);
		}

		delete this._layers[id];

		return this;
	},

	// @method hasLayer(layer: Layer): Boolean
	// Returns `true` if the given layer is currently added to the group.
	// @alternative
	// @method hasLayer(id: Number): Boolean
	// Returns `true` if the given internal ID is currently added to the group.
	hasLayer: function (layer) {
		return !!layer && (layer in this._layers || this.getLayerId(layer) in this._layers);
	},

	// @method clearLayers(): this
	// Removes all the layers from the group.
	clearLayers: function () {
		for (var i in this._layers) {
			this.removeLayer(this._layers[i]);
		}
		return this;
	},

	// @method invoke(methodName: String, …): this
	// Calls `methodName` on every layer contained in this group, passing any
	// additional parameters. Has no effect if the layers contained do not
	// implement `methodName`.
	invoke: function (methodName) {
		var args = Array.prototype.slice.call(arguments, 1),
		    i, layer;

		for (i in this._layers) {
			layer = this._layers[i];

			if (layer[methodName]) {
				layer[methodName].apply(layer, args);
			}
		}

		return this;
	},

	onAdd: function (map) {
		for (var i in this._layers) {
			map.addLayer(this._layers[i]);
		}
	},

	onRemove: function (map) {
		for (var i in this._layers) {
			map.removeLayer(this._layers[i]);
		}
	},

	// @method eachLayer(fn: Function, context?: Object): this
	// Iterates over the layers of the group, optionally specifying context of the iterator function.
	// ```js
	// group.eachLayer(function (layer) {
	// 	layer.bindPopup('Hello');
	// });
	// ```
	eachLayer: function (method, context) {
		for (var i in this._layers) {
			method.call(context, this._layers[i]);
		}
		return this;
	},

	// @method getLayer(id: Number): Layer
	// Returns the layer with the given internal ID.
	getLayer: function (id) {
		return this._layers[id];
	},

	// @method getLayers(): Layer[]
	// Returns an array of all the layers added to the group.
	getLayers: function () {
		var layers = [];

		for (var i in this._layers) {
			layers.push(this._layers[i]);
		}
		return layers;
	},

	// @method setZIndex(zIndex: Number): this
	// Calls `setZIndex` on every layer contained in this group, passing the z-index.
	setZIndex: function (zIndex) {
		return this.invoke('setZIndex', zIndex);
	},

	// @method getLayerId(layer: Layer): Number
	// Returns the internal ID for a layer
	getLayerId: function (layer) {
		return stamp(layer);
	}
});


// @factory L.layerGroup(layers?: Layer[])
// Create a layer group, optionally given an initial set of layers.
var layerGroup = function (layers) {
	return new LayerGroup(layers);
};

/*
 * @class FeatureGroup
 * @aka L.FeatureGroup
 * @inherits LayerGroup
 *
 * Extended `LayerGroup` that makes it easier to do the same thing to all its member layers:
 *  * [`bindPopup`](#layer-bindpopup) binds a popup to all of the layers at once (likewise with [`bindTooltip`](#layer-bindtooltip))
 *  * Events are propagated to the `FeatureGroup`, so if the group has an event
 * handler, it will handle events from any of the layers. This includes mouse events
 * and custom events.
 *  * Has `layeradd` and `layerremove` events
 *
 * @example
 *
 * ```js
 * L.featureGroup([marker1, marker2, polyline])
 * 	.bindPopup('Hello world!')
 * 	.on('click', function() { alert('Clicked on a member of the group!'); })
 * 	.addTo(map);
 * ```
 */

var FeatureGroup = LayerGroup.extend({

	addLayer: function (layer) {
		if (this.hasLayer(layer)) {
			return this;
		}

		layer.addEventParent(this);

		LayerGroup.prototype.addLayer.call(this, layer);

		// @event layeradd: LayerEvent
		// Fired when a layer is added to this `FeatureGroup`
		return this.fire('layeradd', {layer: layer});
	},

	removeLayer: function (layer) {
		if (!this.hasLayer(layer)) {
			return this;
		}
		if (layer in this._layers) {
			layer = this._layers[layer];
		}

		layer.removeEventParent(this);

		LayerGroup.prototype.removeLayer.call(this, layer);

		// @event layerremove: LayerEvent
		// Fired when a layer is removed from this `FeatureGroup`
		return this.fire('layerremove', {layer: layer});
	},

	// @method setStyle(style: Path options): this
	// Sets the given path options to each layer of the group that has a `setStyle` method.
	setStyle: function (style) {
		return this.invoke('setStyle', style);
	},

	// @method bringToFront(): this
	// Brings the layer group to the top of all other layers
	bringToFront: function () {
		return this.invoke('bringToFront');
	},

	// @method bringToBack(): this
	// Brings the layer group to the top of all other layers
	bringToBack: function () {
		return this.invoke('bringToBack');
	},

	// @method getBounds(): LatLngBounds
	// Returns the LatLngBounds of the Feature Group (created from bounds and coordinates of its children).
	getBounds: function () {
		var bounds = new LatLngBounds();

		for (var id in this._layers) {
			var layer = this._layers[id];
			bounds.extend(layer.getBounds ? layer.getBounds() : layer.getLatLng());
		}
		return bounds;
	}
});

// @factory L.featureGroup(layers: Layer[])
// Create a feature group, optionally given an initial set of layers.
var featureGroup = function (layers) {
	return new FeatureGroup(layers);
};

/*
 * @class Icon
 * @aka L.Icon
 *
 * Represents an icon to provide when creating a marker.
 *
 * @example
 *
 * ```js
 * var myIcon = L.icon({
 *     iconUrl: 'my-icon.png',
 *     iconRetinaUrl: 'my-icon@2x.png',
 *     iconSize: [38, 95],
 *     iconAnchor: [22, 94],
 *     popupAnchor: [-3, -76],
 *     shadowUrl: 'my-icon-shadow.png',
 *     shadowRetinaUrl: 'my-icon-shadow@2x.png',
 *     shadowSize: [68, 95],
 *     shadowAnchor: [22, 94]
 * });
 *
 * L.marker([50.505, 30.57], {icon: myIcon}).addTo(map);
 * ```
 *
 * `L.Icon.Default` extends `L.Icon` and is the blue icon Leaflet uses for markers by default.
 *
 */

var Icon = Class.extend({

	/* @section
	 * @aka Icon options
	 *
	 * @option iconUrl: String = null
	 * **(required)** The URL to the icon image (absolute or relative to your script path).
	 *
	 * @option iconRetinaUrl: String = null
	 * The URL to a retina sized version of the icon image (absolute or relative to your
	 * script path). Used for Retina screen devices.
	 *
	 * @option iconSize: Point = null
	 * Size of the icon image in pixels.
	 *
	 * @option iconAnchor: Point = null
	 * The coordinates of the "tip" of the icon (relative to its top left corner). The icon
	 * will be aligned so that this point is at the marker's geographical location. Centered
	 * by default if size is specified, also can be set in CSS with negative margins.
	 *
	 * @option popupAnchor: Point = null
	 * The coordinates of the point from which popups will "open", relative to the icon anchor.
	 *
	 * @option shadowUrl: String = null
	 * The URL to the icon shadow image. If not specified, no shadow image will be created.
	 *
	 * @option shadowRetinaUrl: String = null
	 *
	 * @option shadowSize: Point = null
	 * Size of the shadow image in pixels.
	 *
	 * @option shadowAnchor: Point = null
	 * The coordinates of the "tip" of the shadow (relative to its top left corner) (the same
	 * as iconAnchor if not specified).
	 *
	 * @option className: String = ''
	 * A custom class name to assign to both icon and shadow images. Empty by default.
	 */

	initialize: function (options) {
		setOptions(this, options);
	},

	// @method createIcon(oldIcon?: HTMLElement): HTMLElement
	// Called internally when the icon has to be shown, returns a `<img>` HTML element
	// styled according to the options.
	createIcon: function (oldIcon) {
		return this._createIcon('icon', oldIcon);
	},

	// @method createShadow(oldIcon?: HTMLElement): HTMLElement
	// As `createIcon`, but for the shadow beneath it.
	createShadow: function (oldIcon) {
		return this._createIcon('shadow', oldIcon);
	},

	_createIcon: function (name, oldIcon) {
		var src = this._getIconUrl(name);

		if (!src) {
			if (name === 'icon') {
				throw new Error('iconUrl not set in Icon options (see the docs).');
			}
			return null;
		}

		var img = this._createImg(src, oldIcon && oldIcon.tagName === 'IMG' ? oldIcon : null);
		this._setIconStyles(img, name);

		return img;
	},

	_setIconStyles: function (img, name) {
		var options = this.options;
		var sizeOption = options[name + 'Size'];

		if (typeof sizeOption === 'number') {
			sizeOption = [sizeOption, sizeOption];
		}

		var size = toPoint(sizeOption),
		    anchor = toPoint(name === 'shadow' && options.shadowAnchor || options.iconAnchor ||
		            size && size.divideBy(2, true));

		img.className = 'leaflet-marker-' + name + ' ' + (options.className || '');

		if (anchor) {
			img.style.marginLeft = (-anchor.x) + 'px';
			img.style.marginTop  = (-anchor.y) + 'px';
		}

		if (size) {
			img.style.width  = size.x + 'px';
			img.style.height = size.y + 'px';
		}
	},

	_createImg: function (src, el) {
		el = el || document.createElement('img');
		el.src = src;
		return el;
	},

	_getIconUrl: function (name) {
		return retina && this.options[name + 'RetinaUrl'] || this.options[name + 'Url'];
	}
});


// @factory L.icon(options: Icon options)
// Creates an icon instance with the given options.
function icon(options) {
	return new Icon(options);
}

/*
 * @miniclass Icon.Default (Icon)
 * @aka L.Icon.Default
 * @section
 *
 * A trivial subclass of `Icon`, represents the icon to use in `Marker`s when
 * no icon is specified. Points to the blue marker image distributed with Leaflet
 * releases.
 *
 * In order to customize the default icon, just change the properties of `L.Icon.Default.prototype.options`
 * (which is a set of `Icon options`).
 *
 * If you want to _completely_ replace the default icon, override the
 * `L.Marker.prototype.options.icon` with your own icon instead.
 */

var IconDefault = Icon.extend({

	options: {
		iconUrl:       'marker-icon.png',
		iconRetinaUrl: 'marker-icon-2x.png',
		shadowUrl:     'marker-shadow.png',
		iconSize:    [25, 41],
		iconAnchor:  [12, 41],
		popupAnchor: [1, -34],
		tooltipAnchor: [16, -28],
		shadowSize:  [41, 41]
	},

	_getIconUrl: function (name) {
		if (!IconDefault.imagePath) {	// Deprecated, backwards-compatibility only
			IconDefault.imagePath = this._detectIconPath();
		}

		// @option imagePath: String
		// `Icon.Default` will try to auto-detect the absolute location of the
		// blue icon images. If you are placing these images in a non-standard
		// way, set this option to point to the right absolute path.
		return (this.options.imagePath || IconDefault.imagePath) + Icon.prototype._getIconUrl.call(this, name);
	},

	_detectIconPath: function () {
		var el = create$1('div',  'leaflet-default-icon-path', document.body);
		var path = getStyle(el, 'background-image') ||
		           getStyle(el, 'backgroundImage');	// IE8

		document.body.removeChild(el);

		if (path === null || path.indexOf('url') !== 0) {
			path = '';
		} else {
			path = path.replace(/^url\([\"\']?/, '').replace(/marker-icon\.png[\"\']?\)$/, '');
		}

		return path;
	}
});

/*
 * L.Handler.MarkerDrag is used internally by L.Marker to make the markers draggable.
 */


/* @namespace Marker
 * @section Interaction handlers
 *
 * Interaction handlers are properties of a marker instance that allow you to control interaction behavior in runtime, enabling or disabling certain features such as dragging (see `Handler` methods). Example:
 *
 * ```js
 * marker.dragging.disable();
 * ```
 *
 * @property dragging: Handler
 * Marker dragging handler (by both mouse and touch). Only valid when the marker is on the map (Otherwise set [`marker.options.draggable`](#marker-draggable)).
 */

var MarkerDrag = Handler.extend({
	initialize: function (marker) {
		this._marker = marker;
	},

	addHooks: function () {
		var icon = this._marker._icon;

		if (!this._draggable) {
			this._draggable = new Draggable(icon, icon, true);
		}

		this._draggable.on({
			dragstart: this._onDragStart,
			drag: this._onDrag,
			dragend: this._onDragEnd
		}, this).enable();

		addClass(icon, 'leaflet-marker-draggable');
	},

	removeHooks: function () {
		this._draggable.off({
			dragstart: this._onDragStart,
			drag: this._onDrag,
			dragend: this._onDragEnd
		}, this).disable();

		if (this._marker._icon) {
			removeClass(this._marker._icon, 'leaflet-marker-draggable');
		}
	},

	moved: function () {
		return this._draggable && this._draggable._moved;
	},

	_onDragStart: function () {
		// @section Dragging events
		// @event dragstart: Event
		// Fired when the user starts dragging the marker.

		// @event movestart: Event
		// Fired when the marker starts moving (because of dragging).

		this._oldLatLng = this._marker.getLatLng();
		this._marker
		    .closePopup()
		    .fire('movestart')
		    .fire('dragstart');
	},

	_onDrag: function (e) {
		var marker = this._marker,
		    shadow = marker._shadow,
		iconPos = getPosition(marker._icon),
		    latlng = marker._map.layerPointToLatLng(iconPos);

		// update shadow position
		if (shadow) {
			setPosition(shadow, iconPos);
		}

		marker._latlng = latlng;
		e.latlng = latlng;
		e.oldLatLng = this._oldLatLng;

		// @event drag: Event
		// Fired repeatedly while the user drags the marker.
		marker
		    .fire('move', e)
		    .fire('drag', e);
	},

	_onDragEnd: function (e) {
		// @event dragend: DragEndEvent
		// Fired when the user stops dragging the marker.

		// @event moveend: Event
		// Fired when the marker stops moving (because of dragging).
		delete this._oldLatLng;
		this._marker
		    .fire('moveend')
		    .fire('dragend', e);
	}
});

/*
 * @class Marker
 * @inherits Interactive layer
 * @aka L.Marker
 * L.Marker is used to display clickable/draggable icons on the map. Extends `Layer`.
 *
 * @example
 *
 * ```js
 * L.marker([50.5, 30.5]).addTo(map);
 * ```
 */

var Marker = Layer.extend({

	// @section
	// @aka Marker options
	options: {
		// @option icon: Icon = *
		// Icon instance to use for rendering the marker.
		// See [Icon documentation](#L.Icon) for details on how to customize the marker icon.
		// If not specified, a common instance of `L.Icon.Default` is used.
		icon: new IconDefault(),

		// Option inherited from "Interactive layer" abstract class
		interactive: true,

		// @option draggable: Boolean = false
		// Whether the marker is draggable with mouse/touch or not.
		draggable: false,

		// @option keyboard: Boolean = true
		// Whether the marker can be tabbed to with a keyboard and clicked by pressing enter.
		keyboard: true,

		// @option title: String = ''
		// Text for the browser tooltip that appear on marker hover (no tooltip by default).
		title: '',

		// @option alt: String = ''
		// Text for the `alt` attribute of the icon image (useful for accessibility).
		alt: '',

		// @option zIndexOffset: Number = 0
		// By default, marker images zIndex is set automatically based on its latitude. Use this option if you want to put the marker on top of all others (or below), specifying a high value like `1000` (or high negative value, respectively).
		zIndexOffset: 0,

		// @option opacity: Number = 1.0
		// The opacity of the marker.
		opacity: 1,

		// @option riseOnHover: Boolean = false
		// If `true`, the marker will get on top of others when you hover the mouse over it.
		riseOnHover: false,

		// @option riseOffset: Number = 250
		// The z-index offset used for the `riseOnHover` feature.
		riseOffset: 250,

		// @option pane: String = 'markerPane'
		// `Map pane` where the markers icon will be added.
		pane: 'markerPane',

		// @option bubblingMouseEvents: Boolean = false
		// When `true`, a mouse event on this marker will trigger the same event on the map
		// (unless [`L.DomEvent.stopPropagation`](#domevent-stoppropagation) is used).
		bubblingMouseEvents: false
	},

	/* @section
	 *
	 * In addition to [shared layer methods](#Layer) like `addTo()` and `remove()` and [popup methods](#Popup) like bindPopup() you can also use the following methods:
	 */

	initialize: function (latlng, options) {
		setOptions(this, options);
		this._latlng = toLatLng(latlng);
	},

	onAdd: function (map) {
		this._zoomAnimated = this._zoomAnimated && map.options.markerZoomAnimation;

		if (this._zoomAnimated) {
			map.on('zoomanim', this._animateZoom, this);
		}

		this._initIcon();
		this.update();
	},

	onRemove: function (map) {
		if (this.dragging && this.dragging.enabled()) {
			this.options.draggable = true;
			this.dragging.removeHooks();
		}
		delete this.dragging;

		if (this._zoomAnimated) {
			map.off('zoomanim', this._animateZoom, this);
		}

		this._removeIcon();
		this._removeShadow();
	},

	getEvents: function () {
		return {
			zoom: this.update,
			viewreset: this.update
		};
	},

	// @method getLatLng: LatLng
	// Returns the current geographical position of the marker.
	getLatLng: function () {
		return this._latlng;
	},

	// @method setLatLng(latlng: LatLng): this
	// Changes the marker position to the given point.
	setLatLng: function (latlng) {
		var oldLatLng = this._latlng;
		this._latlng = toLatLng(latlng);
		this.update();

		// @event move: Event
		// Fired when the marker is moved via [`setLatLng`](#marker-setlatlng) or by [dragging](#marker-dragging). Old and new coordinates are included in event arguments as `oldLatLng`, `latlng`.
		return this.fire('move', {oldLatLng: oldLatLng, latlng: this._latlng});
	},

	// @method setZIndexOffset(offset: Number): this
	// Changes the [zIndex offset](#marker-zindexoffset) of the marker.
	setZIndexOffset: function (offset) {
		this.options.zIndexOffset = offset;
		return this.update();
	},

	// @method setIcon(icon: Icon): this
	// Changes the marker icon.
	setIcon: function (icon) {

		this.options.icon = icon;

		if (this._map) {
			this._initIcon();
			this.update();
		}

		if (this._popup) {
			this.bindPopup(this._popup, this._popup.options);
		}

		return this;
	},

	getElement: function () {
		return this._icon;
	},

	update: function () {

		if (this._icon) {
			var pos = this._map.latLngToLayerPoint(this._latlng).round();
			this._setPos(pos);
		}

		return this;
	},

	_initIcon: function () {
		var options = this.options,
		    classToAdd = 'leaflet-zoom-' + (this._zoomAnimated ? 'animated' : 'hide');

		var icon = options.icon.createIcon(this._icon),
		    addIcon = false;

		// if we're not reusing the icon, remove the old one and init new one
		if (icon !== this._icon) {
			if (this._icon) {
				this._removeIcon();
			}
			addIcon = true;

			if (options.title) {
				icon.title = options.title;
			}
			if (options.alt) {
				icon.alt = options.alt;
			}
		}

		addClass(icon, classToAdd);

		if (options.keyboard) {
			icon.tabIndex = '0';
		}

		this._icon = icon;

		if (options.riseOnHover) {
			this.on({
				mouseover: this._bringToFront,
				mouseout: this._resetZIndex
			});
		}

		var newShadow = options.icon.createShadow(this._shadow),
		    addShadow = false;

		if (newShadow !== this._shadow) {
			this._removeShadow();
			addShadow = true;
		}

		if (newShadow) {
			addClass(newShadow, classToAdd);
			newShadow.alt = '';
		}
		this._shadow = newShadow;


		if (options.opacity < 1) {
			this._updateOpacity();
		}


		if (addIcon) {
			this.getPane().appendChild(this._icon);
		}
		this._initInteraction();
		if (newShadow && addShadow) {
			this.getPane('shadowPane').appendChild(this._shadow);
		}
	},

	_removeIcon: function () {
		if (this.options.riseOnHover) {
			this.off({
				mouseover: this._bringToFront,
				mouseout: this._resetZIndex
			});
		}

		remove(this._icon);
		this.removeInteractiveTarget(this._icon);

		this._icon = null;
	},

	_removeShadow: function () {
		if (this._shadow) {
			remove(this._shadow);
		}
		this._shadow = null;
	},

	_setPos: function (pos) {
		setPosition(this._icon, pos);

		if (this._shadow) {
			setPosition(this._shadow, pos);
		}

		this._zIndex = pos.y + this.options.zIndexOffset;

		this._resetZIndex();
	},

	_updateZIndex: function (offset) {
		this._icon.style.zIndex = this._zIndex + offset;
	},

	_animateZoom: function (opt) {
		var pos = this._map._latLngToNewLayerPoint(this._latlng, opt.zoom, opt.center).round();

		this._setPos(pos);
	},

	_initInteraction: function () {

		if (!this.options.interactive) { return; }

		addClass(this._icon, 'leaflet-interactive');

		this.addInteractiveTarget(this._icon);

		if (MarkerDrag) {
			var draggable = this.options.draggable;
			if (this.dragging) {
				draggable = this.dragging.enabled();
				this.dragging.disable();
			}

			this.dragging = new MarkerDrag(this);

			if (draggable) {
				this.dragging.enable();
			}
		}
	},

	// @method setOpacity(opacity: Number): this
	// Changes the opacity of the marker.
	setOpacity: function (opacity) {
		this.options.opacity = opacity;
		if (this._map) {
			this._updateOpacity();
		}

		return this;
	},

	_updateOpacity: function () {
		var opacity = this.options.opacity;

		setOpacity(this._icon, opacity);

		if (this._shadow) {
			setOpacity(this._shadow, opacity);
		}
	},

	_bringToFront: function () {
		this._updateZIndex(this.options.riseOffset);
	},

	_resetZIndex: function () {
		this._updateZIndex(0);
	},

	_getPopupAnchor: function () {
		return this.options.icon.options.popupAnchor || [0, 0];
	},

	_getTooltipAnchor: function () {
		return this.options.icon.options.tooltipAnchor || [0, 0];
	}
});


// factory L.marker(latlng: LatLng, options? : Marker options)

// @factory L.marker(latlng: LatLng, options? : Marker options)
// Instantiates a Marker object given a geographical point and optionally an options object.
function marker(latlng, options) {
	return new Marker(latlng, options);
}

/*
 * @class Path
 * @aka L.Path
 * @inherits Interactive layer
 *
 * An abstract class that contains options and constants shared between vector
 * overlays (Polygon, Polyline, Circle). Do not use it directly. Extends `Layer`.
 */

var Path = Layer.extend({

	// @section
	// @aka Path options
	options: {
		// @option stroke: Boolean = true
		// Whether to draw stroke along the path. Set it to `false` to disable borders on polygons or circles.
		stroke: true,

		// @option color: String = '#3388ff'
		// Stroke color
		color: '#3388ff',

		// @option weight: Number = 3
		// Stroke width in pixels
		weight: 3,

		// @option opacity: Number = 1.0
		// Stroke opacity
		opacity: 1,

		// @option lineCap: String= 'round'
		// A string that defines [shape to be used at the end](https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linecap) of the stroke.
		lineCap: 'round',

		// @option lineJoin: String = 'round'
		// A string that defines [shape to be used at the corners](https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-linejoin) of the stroke.
		lineJoin: 'round',

		// @option dashArray: String = null
		// A string that defines the stroke [dash pattern](https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-dasharray). Doesn't work on `Canvas`-powered layers in [some old browsers](https://developer.mozilla.org/docs/Web/API/CanvasRenderingContext2D/setLineDash#Browser_compatibility).
		dashArray: null,

		// @option dashOffset: String = null
		// A string that defines the [distance into the dash pattern to start the dash](https://developer.mozilla.org/docs/Web/SVG/Attribute/stroke-dashoffset). Doesn't work on `Canvas`-powered layers in [some old browsers](https://developer.mozilla.org/docs/Web/API/CanvasRenderingContext2D/setLineDash#Browser_compatibility).
		dashOffset: null,

		// @option fill: Boolean = depends
		// Whether to fill the path with color. Set it to `false` to disable filling on polygons or circles.
		fill: false,

		// @option fillColor: String = *
		// Fill color. Defaults to the value of the [`color`](#path-color) option
		fillColor: null,

		// @option fillOpacity: Number = 0.2
		// Fill opacity.
		fillOpacity: 0.2,

		// @option fillRule: String = 'evenodd'
		// A string that defines [how the inside of a shape](https://developer.mozilla.org/docs/Web/SVG/Attribute/fill-rule) is determined.
		fillRule: 'evenodd',

		// className: '',

		// Option inherited from "Interactive layer" abstract class
		interactive: true,

		// @option bubblingMouseEvents: Boolean = true
		// When `true`, a mouse event on this path will trigger the same event on the map
		// (unless [`L.DomEvent.stopPropagation`](#domevent-stoppropagation) is used).
		bubblingMouseEvents: true
	},

	beforeAdd: function (map) {
		// Renderer is set here because we need to call renderer.getEvents
		// before this.getEvents.
		this._renderer = map.getRenderer(this);
	},

	onAdd: function () {
		this._renderer._initPath(this);
		this._reset();
		this._renderer._addPath(this);
	},

	onRemove: function () {
		this._renderer._removePath(this);
	},

	// @method redraw(): this
	// Redraws the layer. Sometimes useful after you changed the coordinates that the path uses.
	redraw: function () {
		if (this._map) {
			this._renderer._updatePath(this);
		}
		return this;
	},

	// @method setStyle(style: Path options): this
	// Changes the appearance of a Path based on the options in the `Path options` object.
	setStyle: function (style) {
		setOptions(this, style);
		if (this._renderer) {
			this._renderer._updateStyle(this);
		}
		return this;
	},

	// @method bringToFront(): this
	// Brings the layer to the top of all path layers.
	bringToFront: function () {
		if (this._renderer) {
			this._renderer._bringToFront(this);
		}
		return this;
	},

	// @method bringToBack(): this
	// Brings the layer to the bottom of all path layers.
	bringToBack: function () {
		if (this._renderer) {
			this._renderer._bringToBack(this);
		}
		return this;
	},

	getElement: function () {
		return this._path;
	},

	_reset: function () {
		// defined in child classes
		this._project();
		this._update();
	},

	_clickTolerance: function () {
		// used when doing hit detection for Canvas layers
		return (this.options.stroke ? this.options.weight / 2 : 0) + (touch ? 10 : 0);
	}
});

/*
 * @class CircleMarker
 * @aka L.CircleMarker
 * @inherits Path
 *
 * A circle of a fixed size with radius specified in pixels. Extends `Path`.
 */

var CircleMarker = Path.extend({

	// @section
	// @aka CircleMarker options
	options: {
		fill: true,

		// @option radius: Number = 10
		// Radius of the circle marker, in pixels
		radius: 10
	},

	initialize: function (latlng, options) {
		setOptions(this, options);
		this._latlng = toLatLng(latlng);
		this._radius = this.options.radius;
	},

	// @method setLatLng(latLng: LatLng): this
	// Sets the position of a circle marker to a new location.
	setLatLng: function (latlng) {
		this._latlng = toLatLng(latlng);
		this.redraw();
		return this.fire('move', {latlng: this._latlng});
	},

	// @method getLatLng(): LatLng
	// Returns the current geographical position of the circle marker
	getLatLng: function () {
		return this._latlng;
	},

	// @method setRadius(radius: Number): this
	// Sets the radius of a circle marker. Units are in pixels.
	setRadius: function (radius) {
		this.options.radius = this._radius = radius;
		return this.redraw();
	},

	// @method getRadius(): Number
	// Returns the current radius of the circle
	getRadius: function () {
		return this._radius;
	},

	setStyle : function (options) {
		var radius = options && options.radius || this._radius;
		Path.prototype.setStyle.call(this, options);
		this.setRadius(radius);
		return this;
	},

	_project: function () {
		this._point = this._map.latLngToLayerPoint(this._latlng);
		this._updateBounds();
	},

	_updateBounds: function () {
		var r = this._radius,
		    r2 = this._radiusY || r,
		    w = this._clickTolerance(),
		    p = [r + w, r2 + w];
		this._pxBounds = new Bounds(this._point.subtract(p), this._point.add(p));
	},

	_update: function () {
		if (this._map) {
			this._updatePath();
		}
	},

	_updatePath: function () {
		this._renderer._updateCircle(this);
	},

	_empty: function () {
		return this._radius && !this._renderer._bounds.intersects(this._pxBounds);
	},

	// Needed by the `Canvas` renderer for interactivity
	_containsPoint: function (p) {
		return p.distanceTo(this._point) <= this._radius + this._clickTolerance();
	}
});


// @factory L.circleMarker(latlng: LatLng, options?: CircleMarker options)
// Instantiates a circle marker object given a geographical point, and an optional options object.
function circleMarker(latlng, options) {
	return new CircleMarker(latlng, options);
}

/*
 * @class Circle
 * @aka L.Circle
 * @inherits CircleMarker
 *
 * A class for drawing circle overlays on a map. Extends `CircleMarker`.
 *
 * It's an approximation and starts to diverge from a real circle closer to poles (due to projection distortion).
 *
 * @example
 *
 * ```js
 * L.circle([50.5, 30.5], {radius: 200}).addTo(map);
 * ```
 */

var Circle = CircleMarker.extend({

	initialize: function (latlng, options, legacyOptions) {
		if (typeof options === 'number') {
			// Backwards compatibility with 0.7.x factory (latlng, radius, options?)
			options = extend({}, legacyOptions, {radius: options});
		}
		setOptions(this, options);
		this._latlng = toLatLng(latlng);

		if (isNaN(this.options.radius)) { throw new Error('Circle radius cannot be NaN'); }

		// @section
		// @aka Circle options
		// @option radius: Number; Radius of the circle, in meters.
		this._mRadius = this.options.radius;
	},

	// @method setRadius(radius: Number): this
	// Sets the radius of a circle. Units are in meters.
	setRadius: function (radius) {
		this._mRadius = radius;
		return this.redraw();
	},

	// @method getRadius(): Number
	// Returns the current radius of a circle. Units are in meters.
	getRadius: function () {
		return this._mRadius;
	},

	// @method getBounds(): LatLngBounds
	// Returns the `LatLngBounds` of the path.
	getBounds: function () {
		var half = [this._radius, this._radiusY || this._radius];

		return new LatLngBounds(
			this._map.layerPointToLatLng(this._point.subtract(half)),
			this._map.layerPointToLatLng(this._point.add(half)));
	},

	setStyle: Path.prototype.setStyle,

	_project: function () {

		var lng = this._latlng.lng,
		    lat = this._latlng.lat,
		    map = this._map,
		    crs = map.options.crs;

		if (crs.distance === Earth.distance) {
			var d = Math.PI / 180,
			    latR = (this._mRadius / Earth.R) / d,
			    top = map.project([lat + latR, lng]),
			    bottom = map.project([lat - latR, lng]),
			    p = top.add(bottom).divideBy(2),
			    lat2 = map.unproject(p).lat,
			    lngR = Math.acos((Math.cos(latR * d) - Math.sin(lat * d) * Math.sin(lat2 * d)) /
			            (Math.cos(lat * d) * Math.cos(lat2 * d))) / d;

			if (isNaN(lngR) || lngR === 0) {
				lngR = latR / Math.cos(Math.PI / 180 * lat); // Fallback for edge case, #2425
			}

			this._point = p.subtract(map.getPixelOrigin());
			this._radius = isNaN(lngR) ? 0 : Math.max(Math.round(p.x - map.project([lat2, lng - lngR]).x), 1);
			this._radiusY = Math.max(Math.round(p.y - top.y), 1);

		} else {
			var latlng2 = crs.unproject(crs.project(this._latlng).subtract([this._mRadius, 0]));

			this._point = map.latLngToLayerPoint(this._latlng);
			this._radius = this._point.x - map.latLngToLayerPoint(latlng2).x;
		}

		this._updateBounds();
	}
});

// @factory L.circle(latlng: LatLng, options?: Circle options)
// Instantiates a circle object given a geographical point, and an options object
// which contains the circle radius.
// @alternative
// @factory L.circle(latlng: LatLng, radius: Number, options?: Circle options)
// Obsolete way of instantiating a circle, for compatibility with 0.7.x code.
// Do not use in new applications or plugins.
function circle(latlng, options, legacyOptions) {
	return new Circle(latlng, options, legacyOptions);
}

/*
 * @class Polyline
 * @aka L.Polyline
 * @inherits Path
 *
 * A class for drawing polyline overlays on a map. Extends `Path`.
 *
 * @example
 *
 * ```js
 * // create a red polyline from an array of LatLng points
 * var latlngs = [
 * 	[45.51, -122.68],
 * 	[37.77, -122.43],
 * 	[34.04, -118.2]
 * ];
 *
 * var polyline = L.polyline(latlngs, {color: 'red'}).addTo(map);
 *
 * // zoom the map to the polyline
 * map.fitBounds(polyline.getBounds());
 * ```
 *
 * You can also pass a multi-dimensional array to represent a `MultiPolyline` shape:
 *
 * ```js
 * // create a red polyline from an array of arrays of LatLng points
 * var latlngs = [
 * 	[[45.51, -122.68],
 * 	 [37.77, -122.43],
 * 	 [34.04, -118.2]],
 * 	[[40.78, -73.91],
 * 	 [41.83, -87.62],
 * 	 [32.76, -96.72]]
 * ];
 * ```
 */


var Polyline = Path.extend({

	// @section
	// @aka Polyline options
	options: {
		// @option smoothFactor: Number = 1.0
		// How much to simplify the polyline on each zoom level. More means
		// better performance and smoother look, and less means more accurate representation.
		smoothFactor: 1.0,

		// @option noClip: Boolean = false
		// Disable polyline clipping.
		noClip: false
	},

	initialize: function (latlngs, options) {
		setOptions(this, options);
		this._setLatLngs(latlngs);
	},

	// @method getLatLngs(): LatLng[]
	// Returns an array of the points in the path, or nested arrays of points in case of multi-polyline.
	getLatLngs: function () {
		return this._latlngs;
	},

	// @method setLatLngs(latlngs: LatLng[]): this
	// Replaces all the points in the polyline with the given array of geographical points.
	setLatLngs: function (latlngs) {
		this._setLatLngs(latlngs);
		return this.redraw();
	},

	// @method isEmpty(): Boolean
	// Returns `true` if the Polyline has no LatLngs.
	isEmpty: function () {
		return !this._latlngs.length;
	},

	closestLayerPoint: function (p) {
		var minDistance = Infinity,
		    minPoint = null,
		    closest = _sqClosestPointOnSegment,
		    p1, p2;

		for (var j = 0, jLen = this._parts.length; j < jLen; j++) {
			var points = this._parts[j];

			for (var i = 1, len = points.length; i < len; i++) {
				p1 = points[i - 1];
				p2 = points[i];

				var sqDist = closest(p, p1, p2, true);

				if (sqDist < minDistance) {
					minDistance = sqDist;
					minPoint = closest(p, p1, p2);
				}
			}
		}
		if (minPoint) {
			minPoint.distance = Math.sqrt(minDistance);
		}
		return minPoint;
	},

	// @method getCenter(): LatLng
	// Returns the center ([centroid](http://en.wikipedia.org/wiki/Centroid)) of the polyline.
	getCenter: function () {
		// throws error when not yet added to map as this center calculation requires projected coordinates
		if (!this._map) {
			throw new Error('Must add layer to map before using getCenter()');
		}

		var i, halfDist, segDist, dist, p1, p2, ratio,
		    points = this._rings[0],
		    len = points.length;

		if (!len) { return null; }

		// polyline centroid algorithm; only uses the first ring if there are multiple

		for (i = 0, halfDist = 0; i < len - 1; i++) {
			halfDist += points[i].distanceTo(points[i + 1]) / 2;
		}

		// The line is so small in the current view that all points are on the same pixel.
		if (halfDist === 0) {
			return this._map.layerPointToLatLng(points[0]);
		}

		for (i = 0, dist = 0; i < len - 1; i++) {
			p1 = points[i];
			p2 = points[i + 1];
			segDist = p1.distanceTo(p2);
			dist += segDist;

			if (dist > halfDist) {
				ratio = (dist - halfDist) / segDist;
				return this._map.layerPointToLatLng([
					p2.x - ratio * (p2.x - p1.x),
					p2.y - ratio * (p2.y - p1.y)
				]);
			}
		}
	},

	// @method getBounds(): LatLngBounds
	// Returns the `LatLngBounds` of the path.
	getBounds: function () {
		return this._bounds;
	},

	// @method addLatLng(latlng: LatLng, latlngs? LatLng[]): this
	// Adds a given point to the polyline. By default, adds to the first ring of
	// the polyline in case of a multi-polyline, but can be overridden by passing
	// a specific ring as a LatLng array (that you can earlier access with [`getLatLngs`](#polyline-getlatlngs)).
	addLatLng: function (latlng, latlngs) {
		latlngs = latlngs || this._defaultShape();
		latlng = toLatLng(latlng);
		latlngs.push(latlng);
		this._bounds.extend(latlng);
		return this.redraw();
	},

	_setLatLngs: function (latlngs) {
		this._bounds = new LatLngBounds();
		this._latlngs = this._convertLatLngs(latlngs);
	},

	_defaultShape: function () {
		return isFlat(this._latlngs) ? this._latlngs : this._latlngs[0];
	},

	// recursively convert latlngs input into actual LatLng instances; calculate bounds along the way
	_convertLatLngs: function (latlngs) {
		var result = [],
		    flat = isFlat(latlngs);

		for (var i = 0, len = latlngs.length; i < len; i++) {
			if (flat) {
				result[i] = toLatLng(latlngs[i]);
				this._bounds.extend(result[i]);
			} else {
				result[i] = this._convertLatLngs(latlngs[i]);
			}
		}

		return result;
	},

	_project: function () {
		var pxBounds = new Bounds();
		this._rings = [];
		this._projectLatlngs(this._latlngs, this._rings, pxBounds);

		var w = this._clickTolerance(),
		    p = new Point(w, w);

		if (this._bounds.isValid() && pxBounds.isValid()) {
			pxBounds.min._subtract(p);
			pxBounds.max._add(p);
			this._pxBounds = pxBounds;
		}
	},

	// recursively turns latlngs into a set of rings with projected coordinates
	_projectLatlngs: function (latlngs, result, projectedBounds) {
		var flat = latlngs[0] instanceof LatLng,
		    len = latlngs.length,
		    i, ring;

		if (flat) {
			ring = [];
			for (i = 0; i < len; i++) {
				ring[i] = this._map.latLngToLayerPoint(latlngs[i]);
				projectedBounds.extend(ring[i]);
			}
			result.push(ring);
		} else {
			for (i = 0; i < len; i++) {
				this._projectLatlngs(latlngs[i], result, projectedBounds);
			}
		}
	},

	// clip polyline by renderer bounds so that we have less to render for performance
	_clipPoints: function () {
		var bounds = this._renderer._bounds;

		this._parts = [];
		if (!this._pxBounds || !this._pxBounds.intersects(bounds)) {
			return;
		}

		if (this.options.noClip) {
			this._parts = this._rings;
			return;
		}

		var parts = this._parts,
		    i, j, k, len, len2, segment, points;

		for (i = 0, k = 0, len = this._rings.length; i < len; i++) {
			points = this._rings[i];

			for (j = 0, len2 = points.length; j < len2 - 1; j++) {
				segment = clipSegment(points[j], points[j + 1], bounds, j, true);

				if (!segment) { continue; }

				parts[k] = parts[k] || [];
				parts[k].push(segment[0]);

				// if segment goes out of screen, or it's the last one, it's the end of the line part
				if ((segment[1] !== points[j + 1]) || (j === len2 - 2)) {
					parts[k].push(segment[1]);
					k++;
				}
			}
		}
	},

	// simplify each clipped part of the polyline for performance
	_simplifyPoints: function () {
		var parts = this._parts,
		    tolerance = this.options.smoothFactor;

		for (var i = 0, len = parts.length; i < len; i++) {
			parts[i] = simplify(parts[i], tolerance);
		}
	},

	_update: function () {
		if (!this._map) { return; }

		this._clipPoints();
		this._simplifyPoints();
		this._updatePath();
	},

	_updatePath: function () {
		this._renderer._updatePoly(this);
	},

	// Needed by the `Canvas` renderer for interactivity
	_containsPoint: function (p, closed) {
		var i, j, k, len, len2, part,
		    w = this._clickTolerance();

		if (!this._pxBounds || !this._pxBounds.contains(p)) { return false; }

		// hit detection for polylines
		for (i = 0, len = this._parts.length; i < len; i++) {
			part = this._parts[i];

			for (j = 0, len2 = part.length, k = len2 - 1; j < len2; k = j++) {
				if (!closed && (j === 0)) { continue; }

				if (pointToSegmentDistance(p, part[k], part[j]) <= w) {
					return true;
				}
			}
		}
		return false;
	}
});

// @factory L.polyline(latlngs: LatLng[], options?: Polyline options)
// Instantiates a polyline object given an array of geographical points and
// optionally an options object. You can create a `Polyline` object with
// multiple separate lines (`MultiPolyline`) by passing an array of arrays
// of geographic points.
function polyline(latlngs, options) {
	return new Polyline(latlngs, options);
}

// Retrocompat. Allow plugins to support Leaflet versions before and after 1.1.
Polyline._flat = _flat;

/*
 * @class Polygon
 * @aka L.Polygon
 * @inherits Polyline
 *
 * A class for drawing polygon overlays on a map. Extends `Polyline`.
 *
 * Note that points you pass when creating a polygon shouldn't have an additional last point equal to the first one — it's better to filter out such points.
 *
 *
 * @example
 *
 * ```js
 * // create a red polygon from an array of LatLng points
 * var latlngs = [[37, -109.05],[41, -109.03],[41, -102.05],[37, -102.04]];
 *
 * var polygon = L.polygon(latlngs, {color: 'red'}).addTo(map);
 *
 * // zoom the map to the polygon
 * map.fitBounds(polygon.getBounds());
 * ```
 *
 * You can also pass an array of arrays of latlngs, with the first array representing the outer shape and the other arrays representing holes in the outer shape:
 *
 * ```js
 * var latlngs = [
 *   [[37, -109.05],[41, -109.03],[41, -102.05],[37, -102.04]], // outer ring
 *   [[37.29, -108.58],[40.71, -108.58],[40.71, -102.50],[37.29, -102.50]] // hole
 * ];
 * ```
 *
 * Additionally, you can pass a multi-dimensional array to represent a MultiPolygon shape.
 *
 * ```js
 * var latlngs = [
 *   [ // first polygon
 *     [[37, -109.05],[41, -109.03],[41, -102.05],[37, -102.04]], // outer ring
 *     [[37.29, -108.58],[40.71, -108.58],[40.71, -102.50],[37.29, -102.50]] // hole
 *   ],
 *   [ // second polygon
 *     [[41, -111.03],[45, -111.04],[45, -104.05],[41, -104.05]]
 *   ]
 * ];
 * ```
 */

var Polygon = Polyline.extend({

	options: {
		fill: true
	},

	isEmpty: function () {
		return !this._latlngs.length || !this._latlngs[0].length;
	},

	getCenter: function () {
		// throws error when not yet added to map as this center calculation requires projected coordinates
		if (!this._map) {
			throw new Error('Must add layer to map before using getCenter()');
		}

		var i, j, p1, p2, f, area, x, y, center,
		    points = this._rings[0],
		    len = points.length;

		if (!len) { return null; }

		// polygon centroid algorithm; only uses the first ring if there are multiple

		area = x = y = 0;

		for (i = 0, j = len - 1; i < len; j = i++) {
			p1 = points[i];
			p2 = points[j];

			f = p1.y * p2.x - p2.y * p1.x;
			x += (p1.x + p2.x) * f;
			y += (p1.y + p2.y) * f;
			area += f * 3;
		}

		if (area === 0) {
			// Polygon is so small that all points are on same pixel.
			center = points[0];
		} else {
			center = [x / area, y / area];
		}
		return this._map.layerPointToLatLng(center);
	},

	_convertLatLngs: function (latlngs) {
		var result = Polyline.prototype._convertLatLngs.call(this, latlngs),
		    len = result.length;

		// remove last point if it equals first one
		if (len >= 2 && result[0] instanceof LatLng && result[0].equals(result[len - 1])) {
			result.pop();
		}
		return result;
	},

	_setLatLngs: function (latlngs) {
		Polyline.prototype._setLatLngs.call(this, latlngs);
		if (isFlat(this._latlngs)) {
			this._latlngs = [this._latlngs];
		}
	},

	_defaultShape: function () {
		return isFlat(this._latlngs[0]) ? this._latlngs[0] : this._latlngs[0][0];
	},

	_clipPoints: function () {
		// polygons need a different clipping algorithm so we redefine that

		var bounds = this._renderer._bounds,
		    w = this.options.weight,
		    p = new Point(w, w);

		// increase clip padding by stroke width to avoid stroke on clip edges
		bounds = new Bounds(bounds.min.subtract(p), bounds.max.add(p));

		this._parts = [];
		if (!this._pxBounds || !this._pxBounds.intersects(bounds)) {
			return;
		}

		if (this.options.noClip) {
			this._parts = this._rings;
			return;
		}

		for (var i = 0, len = this._rings.length, clipped; i < len; i++) {
			clipped = clipPolygon(this._rings[i], bounds, true);
			if (clipped.length) {
				this._parts.push(clipped);
			}
		}
	},

	_updatePath: function () {
		this._renderer._updatePoly(this, true);
	},

	// Needed by the `Canvas` renderer for interactivity
	_containsPoint: function (p) {
		var inside = false,
		    part, p1, p2, i, j, k, len, len2;

		if (!this._pxBounds.contains(p)) { return false; }

		// ray casting algorithm for detecting if point is in polygon
		for (i = 0, len = this._parts.length; i < len; i++) {
			part = this._parts[i];

			for (j = 0, len2 = part.length, k = len2 - 1; j < len2; k = j++) {
				p1 = part[j];
				p2 = part[k];

				if (((p1.y > p.y) !== (p2.y > p.y)) && (p.x < (p2.x - p1.x) * (p.y - p1.y) / (p2.y - p1.y) + p1.x)) {
					inside = !inside;
				}
			}
		}

		// also check if it's on polygon stroke
		return inside || Polyline.prototype._containsPoint.call(this, p, true);
	}

});


// @factory L.polygon(latlngs: LatLng[], options?: Polyline options)
function polygon(latlngs, options) {
	return new Polygon(latlngs, options);
}

/*
 * @class GeoJSON
 * @aka L.GeoJSON
 * @inherits FeatureGroup
 *
 * Represents a GeoJSON object or an array of GeoJSON objects. Allows you to parse
 * GeoJSON data and display it on the map. Extends `FeatureGroup`.
 *
 * @example
 *
 * ```js
 * L.geoJSON(data, {
 * 	style: function (feature) {
 * 		return {color: feature.properties.color};
 * 	}
 * }).bindPopup(function (layer) {
 * 	return layer.feature.properties.description;
 * }).addTo(map);
 * ```
 */

var GeoJSON = FeatureGroup.extend({

	/* @section
	 * @aka GeoJSON options
	 *
	 * @option pointToLayer: Function = *
	 * A `Function` defining how GeoJSON points spawn Leaflet layers. It is internally
	 * called when data is added, passing the GeoJSON point feature and its `LatLng`.
	 * The default is to spawn a default `Marker`:
	 * ```js
	 * function(geoJsonPoint, latlng) {
	 * 	return L.marker(latlng);
	 * }
	 * ```
	 *
	 * @option style: Function = *
	 * A `Function` defining the `Path options` for styling GeoJSON lines and polygons,
	 * called internally when data is added.
	 * The default value is to not override any defaults:
	 * ```js
	 * function (geoJsonFeature) {
	 * 	return {}
	 * }
	 * ```
	 *
	 * @option onEachFeature: Function = *
	 * A `Function` that will be called once for each created `Feature`, after it has
	 * been created and styled. Useful for attaching events and popups to features.
	 * The default is to do nothing with the newly created layers:
	 * ```js
	 * function (feature, layer) {}
	 * ```
	 *
	 * @option filter: Function = *
	 * A `Function` that will be used to decide whether to include a feature or not.
	 * The default is to include all features:
	 * ```js
	 * function (geoJsonFeature) {
	 * 	return true;
	 * }
	 * ```
	 * Note: dynamically changing the `filter` option will have effect only on newly
	 * added data. It will _not_ re-evaluate already included features.
	 *
	 * @option coordsToLatLng: Function = *
	 * A `Function` that will be used for converting GeoJSON coordinates to `LatLng`s.
	 * The default is the `coordsToLatLng` static method.
	 */

	initialize: function (geojson, options) {
		setOptions(this, options);

		this._layers = {};

		if (geojson) {
			this.addData(geojson);
		}
	},

	// @method addData( <GeoJSON> data ): this
	// Adds a GeoJSON object to the layer.
	addData: function (geojson) {
		var features = isArray(geojson) ? geojson : geojson.features,
		    i, len, feature;

		if (features) {
			for (i = 0, len = features.length; i < len; i++) {
				// only add this if geometry or geometries are set and not null
				feature = features[i];
				if (feature.geometries || feature.geometry || feature.features || feature.coordinates) {
					this.addData(feature);
				}
			}
			return this;
		}

		var options = this.options;

		if (options.filter && !options.filter(geojson)) { return this; }

		var layer = geometryToLayer(geojson, options);
		if (!layer) {
			return this;
		}
		layer.feature = asFeature(geojson);

		layer.defaultOptions = layer.options;
		this.resetStyle(layer);

		if (options.onEachFeature) {
			options.onEachFeature(geojson, layer);
		}

		return this.addLayer(layer);
	},

	// @method resetStyle( <Path> layer ): this
	// Resets the given vector layer's style to the original GeoJSON style, useful for resetting style after hover events.
	resetStyle: function (layer) {
		// reset any custom styles
		layer.options = extend({}, layer.defaultOptions);
		this._setLayerStyle(layer, this.options.style);
		return this;
	},

	// @method setStyle( <Function> style ): this
	// Changes styles of GeoJSON vector layers with the given style function.
	setStyle: function (style) {
		return this.eachLayer(function (layer) {
			this._setLayerStyle(layer, style);
		}, this);
	},

	_setLayerStyle: function (layer, style) {
		if (typeof style === 'function') {
			style = style(layer.feature);
		}
		if (layer.setStyle) {
			layer.setStyle(style);
		}
	}
});

// @section
// There are several static functions which can be called without instantiating L.GeoJSON:

// @function geometryToLayer(featureData: Object, options?: GeoJSON options): Layer
// Creates a `Layer` from a given GeoJSON feature. Can use a custom
// [`pointToLayer`](#geojson-pointtolayer) and/or [`coordsToLatLng`](#geojson-coordstolatlng)
// functions if provided as options.
function geometryToLayer(geojson, options) {

	var geometry = geojson.type === 'Feature' ? geojson.geometry : geojson,
	    coords = geometry ? geometry.coordinates : null,
	    layers = [],
	    pointToLayer = options && options.pointToLayer,
	    _coordsToLatLng = options && options.coordsToLatLng || coordsToLatLng,
	    latlng, latlngs, i, len;

	if (!coords && !geometry) {
		return null;
	}

	switch (geometry.type) {
	case 'Point':
		latlng = _coordsToLatLng(coords);
		return pointToLayer ? pointToLayer(geojson, latlng) : new Marker(latlng);

	case 'MultiPoint':
		for (i = 0, len = coords.length; i < len; i++) {
			latlng = _coordsToLatLng(coords[i]);
			layers.push(pointToLayer ? pointToLayer(geojson, latlng) : new Marker(latlng));
		}
		return new FeatureGroup(layers);

	case 'LineString':
	case 'MultiLineString':
		latlngs = coordsToLatLngs(coords, geometry.type === 'LineString' ? 0 : 1, _coordsToLatLng);
		return new Polyline(latlngs, options);

	case 'Polygon':
	case 'MultiPolygon':
		latlngs = coordsToLatLngs(coords, geometry.type === 'Polygon' ? 1 : 2, _coordsToLatLng);
		return new Polygon(latlngs, options);

	case 'GeometryCollection':
		for (i = 0, len = geometry.geometries.length; i < len; i++) {
			var layer = geometryToLayer({
				geometry: geometry.geometries[i],
				type: 'Feature',
				properties: geojson.properties
			}, options);

			if (layer) {
				layers.push(layer);
			}
		}
		return new FeatureGroup(layers);

	default:
		throw new Error('Invalid GeoJSON object.');
	}
}

// @function coordsToLatLng(coords: Array): LatLng
// Creates a `LatLng` object from an array of 2 numbers (longitude, latitude)
// or 3 numbers (longitude, latitude, altitude) used in GeoJSON for points.
function coordsToLatLng(coords) {
	return new LatLng(coords[1], coords[0], coords[2]);
}

// @function coordsToLatLngs(coords: Array, levelsDeep?: Number, coordsToLatLng?: Function): Array
// Creates a multidimensional array of `LatLng`s from a GeoJSON coordinates array.
// `levelsDeep` specifies the nesting level (0 is for an array of points, 1 for an array of arrays of points, etc., 0 by default).
// Can use a custom [`coordsToLatLng`](#geojson-coordstolatlng) function.
function coordsToLatLngs(coords, levelsDeep, _coordsToLatLng) {
	var latlngs = [];

	for (var i = 0, len = coords.length, latlng; i < len; i++) {
		latlng = levelsDeep ?
				coordsToLatLngs(coords[i], levelsDeep - 1, _coordsToLatLng) :
				(_coordsToLatLng || coordsToLatLng)(coords[i]);

		latlngs.push(latlng);
	}

	return latlngs;
}

// @function latLngToCoords(latlng: LatLng, precision?: Number): Array
// Reverse of [`coordsToLatLng`](#geojson-coordstolatlng)
function latLngToCoords(latlng, precision) {
	precision = typeof precision === 'number' ? precision : 6;
	return latlng.alt !== undefined ?
			[formatNum(latlng.lng, precision), formatNum(latlng.lat, precision), formatNum(latlng.alt, precision)] :
			[formatNum(latlng.lng, precision), formatNum(latlng.lat, precision)];
}

// @function latLngsToCoords(latlngs: Array, levelsDeep?: Number, closed?: Boolean): Array
// Reverse of [`coordsToLatLngs`](#geojson-coordstolatlngs)
// `closed` determines whether the first point should be appended to the end of the array to close the feature, only used when `levelsDeep` is 0. False by default.
function latLngsToCoords(latlngs, levelsDeep, closed, precision) {
	var coords = [];

	for (var i = 0, len = latlngs.length; i < len; i++) {
		coords.push(levelsDeep ?
			latLngsToCoords(latlngs[i], levelsDeep - 1, closed, precision) :
			latLngToCoords(latlngs[i], precision));
	}

	if (!levelsDeep && closed) {
		coords.push(coords[0]);
	}

	return coords;
}

function getFeature(layer, newGeometry) {
	return layer.feature ?
			extend({}, layer.feature, {geometry: newGeometry}) :
			asFeature(newGeometry);
}

// @function asFeature(geojson: Object): Object
// Normalize GeoJSON geometries/features into GeoJSON features.
function asFeature(geojson) {
	if (geojson.type === 'Feature' || geojson.type === 'FeatureCollection') {
		return geojson;
	}

	return {
		type: 'Feature',
		properties: {},
		geometry: geojson
	};
}

var PointToGeoJSON = {
	toGeoJSON: function (precision) {
		return getFeature(this, {
			type: 'Point',
			coordinates: latLngToCoords(this.getLatLng(), precision)
		});
	}
};

// @namespace Marker
// @method toGeoJSON(): Object
// Returns a [`GeoJSON`](http://en.wikipedia.org/wiki/GeoJSON) representation of the marker (as a GeoJSON `Point` Feature).
Marker.include(PointToGeoJSON);

// @namespace CircleMarker
// @method toGeoJSON(): Object
// Returns a [`GeoJSON`](http://en.wikipedia.org/wiki/GeoJSON) representation of the circle marker (as a GeoJSON `Point` Feature).
Circle.include(PointToGeoJSON);
CircleMarker.include(PointToGeoJSON);


// @namespace Polyline
// @method toGeoJSON(): Object
// Returns a [`GeoJSON`](http://en.wikipedia.org/wiki/GeoJSON) representation of the polyline (as a GeoJSON `LineString` or `MultiLineString` Feature).
Polyline.include({
	toGeoJSON: function (precision) {
		var multi = !isFlat(this._latlngs);

		var coords = latLngsToCoords(this._latlngs, multi ? 1 : 0, false, precision);

		return getFeature(this, {
			type: (multi ? 'Multi' : '') + 'LineString',
			coordinates: coords
		});
	}
});

// @namespace Polygon
// @method toGeoJSON(): Object
// Returns a [`GeoJSON`](http://en.wikipedia.org/wiki/GeoJSON) representation of the polygon (as a GeoJSON `Polygon` or `MultiPolygon` Feature).
Polygon.include({
	toGeoJSON: function (precision) {
		var holes = !isFlat(this._latlngs),
		    multi = holes && !isFlat(this._latlngs[0]);

		var coords = latLngsToCoords(this._latlngs, multi ? 2 : holes ? 1 : 0, true, precision);

		if (!holes) {
			coords = [coords];
		}

		return getFeature(this, {
			type: (multi ? 'Multi' : '') + 'Polygon',
			coordinates: coords
		});
	}
});


// @namespace LayerGroup
LayerGroup.include({
	toMultiPoint: function (precision) {
		var coords = [];

		this.eachLayer(function (layer) {
			coords.push(layer.toGeoJSON(precision).geometry.coordinates);
		});

		return getFeature(this, {
			type: 'MultiPoint',
			coordinates: coords
		});
	},

	// @method toGeoJSON(): Object
	// Returns a [`GeoJSON`](http://en.wikipedia.org/wiki/GeoJSON) representation of the layer group (as a GeoJSON `FeatureCollection`, `GeometryCollection`, or `MultiPoint`).
	toGeoJSON: function (precision) {

		var type = this.feature && this.feature.geometry && this.feature.geometry.type;

		if (type === 'MultiPoint') {
			return this.toMultiPoint(precision);
		}

		var isGeometryCollection = type === 'GeometryCollection',
		    jsons = [];

		this.eachLayer(function (layer) {
			if (layer.toGeoJSON) {
				var json = layer.toGeoJSON(precision);
				if (isGeometryCollection) {
					jsons.push(json.geometry);
				} else {
					var feature = asFeature(json);
					// Squash nested feature collections
					if (feature.type === 'FeatureCollection') {
						jsons.push.apply(jsons, feature.features);
					} else {
						jsons.push(feature);
					}
				}
			}
		});

		if (isGeometryCollection) {
			return getFeature(this, {
				geometries: jsons,
				type: 'GeometryCollection'
			});
		}

		return {
			type: 'FeatureCollection',
			features: jsons
		};
	}
});

// @namespace GeoJSON
// @factory L.geoJSON(geojson?: Object, options?: GeoJSON options)
// Creates a GeoJSON layer. Optionally accepts an object in
// [GeoJSON format](http://geojson.org/geojson-spec.html) to display on the map
// (you can alternatively add it later with `addData` method) and an `options` object.
function geoJSON(geojson, options) {
	return new GeoJSON(geojson, options);
}

// Backward compatibility.
var geoJson = geoJSON;

/*
 * @class ImageOverlay
 * @aka L.ImageOverlay
 * @inherits Interactive layer
 *
 * Used to load and display a single image over specific bounds of the map. Extends `Layer`.
 *
 * @example
 *
 * ```js
 * var imageUrl = 'http://www.lib.utexas.edu/maps/historical/newark_nj_1922.jpg',
 * 	imageBounds = [[40.712216, -74.22655], [40.773941, -74.12544]];
 * L.imageOverlay(imageUrl, imageBounds).addTo(map);
 * ```
 */

var ImageOverlay = Layer.extend({

	// @section
	// @aka ImageOverlay options
	options: {
		// @option opacity: Number = 1.0
		// The opacity of the image overlay.
		opacity: 1,

		// @option alt: String = ''
		// Text for the `alt` attribute of the image (useful for accessibility).
		alt: '',

		// @option interactive: Boolean = false
		// If `true`, the image overlay will emit [mouse events](#interactive-layer) when clicked or hovered.
		interactive: false,

		// @option crossOrigin: Boolean = false
		// If true, the image will have its crossOrigin attribute set to ''. This is needed if you want to access image pixel data.
		crossOrigin: false,

		// @option errorOverlayUrl: String = ''
		// URL to the overlay image to show in place of the overlay that failed to load.
		errorOverlayUrl: '',

		// @option zIndex: Number = 1
		// The explicit [zIndex](https://developer.mozilla.org/docs/Web/CSS/CSS_Positioning/Understanding_z_index) of the tile layer.
		zIndex: 1,

		// @option className: String = ''
		// A custom class name to assign to the image. Empty by default.
		className: '',
	},

	initialize: function (url, bounds, options) { // (String, LatLngBounds, Object)
		this._url = url;
		this._bounds = toLatLngBounds(bounds);

		setOptions(this, options);
	},

	onAdd: function () {
		if (!this._image) {
			this._initImage();

			if (this.options.opacity < 1) {
				this._updateOpacity();
			}
		}

		if (this.options.interactive) {
			addClass(this._image, 'leaflet-interactive');
			this.addInteractiveTarget(this._image);
		}

		this.getPane().appendChild(this._image);
		this._reset();
	},

	onRemove: function () {
		remove(this._image);
		if (this.options.interactive) {
			this.removeInteractiveTarget(this._image);
		}
	},

	// @method setOpacity(opacity: Number): this
	// Sets the opacity of the overlay.
	setOpacity: function (opacity) {
		this.options.opacity = opacity;

		if (this._image) {
			this._updateOpacity();
		}
		return this;
	},

	setStyle: function (styleOpts) {
		if (styleOpts.opacity) {
			this.setOpacity(styleOpts.opacity);
		}
		return this;
	},

	// @method bringToFront(): this
	// Brings the layer to the top of all overlays.
	bringToFront: function () {
		if (this._map) {
			toFront(this._image);
		}
		return this;
	},

	// @method bringToBack(): this
	// Brings the layer to the bottom of all overlays.
	bringToBack: function () {
		if (this._map) {
			toBack(this._image);
		}
		return this;
	},

	// @method setUrl(url: String): this
	// Changes the URL of the image.
	setUrl: function (url) {
		this._url = url;

		if (this._image) {
			this._image.src = url;
		}
		return this;
	},

	// @method setBounds(bounds: LatLngBounds): this
	// Update the bounds that this ImageOverlay covers
	setBounds: function (bounds) {
		this._bounds = toLatLngBounds(bounds);

		if (this._map) {
			this._reset();
		}
		return this;
	},

	getEvents: function () {
		var events = {
			zoom: this._reset,
			viewreset: this._reset
		};

		if (this._zoomAnimated) {
			events.zoomanim = this._animateZoom;
		}

		return events;
	},

	// @method: setZIndex(value: Number) : this
	// Changes the [zIndex](#imageoverlay-zindex) of the image overlay.
	setZIndex: function (value) {
		this.options.zIndex = value;
		this._updateZIndex();
		return this;
	},

	// @method getBounds(): LatLngBounds
	// Get the bounds that this ImageOverlay covers
	getBounds: function () {
		return this._bounds;
	},

	// @method getElement(): HTMLElement
	// Returns the instance of [`HTMLImageElement`](https://developer.mozilla.org/docs/Web/API/HTMLImageElement)
	// used by this overlay.
	getElement: function () {
		return this._image;
	},

	_initImage: function () {
		var img = this._image = create$1('img',
				'leaflet-image-layer ' + (this._zoomAnimated ? 'leaflet-zoom-animated' : '') +
				 (this.options.className || ''));

		img.onselectstart = falseFn;
		img.onmousemove = falseFn;

		// @event load: Event
		// Fired when the ImageOverlay layer has loaded its image
		img.onload = bind(this.fire, this, 'load');
		img.onerror = bind(this._overlayOnError, this, 'error');

		if (this.options.crossOrigin) {
			img.crossOrigin = '';
		}

		if (this.options.zIndex) {
			this._updateZIndex();
		}

		img.src = this._url;
		img.alt = this.options.alt;
	},

	_animateZoom: function (e) {
		var scale = this._map.getZoomScale(e.zoom),
		    offset = this._map._latLngBoundsToNewLayerBounds(this._bounds, e.zoom, e.center).min;

		setTransform(this._image, offset, scale);
	},

	_reset: function () {
		var image = this._image,
		    bounds = new Bounds(
		        this._map.latLngToLayerPoint(this._bounds.getNorthWest()),
		        this._map.latLngToLayerPoint(this._bounds.getSouthEast())),
		    size = bounds.getSize();

		setPosition(image, bounds.min);

		image.style.width  = size.x + 'px';
		image.style.height = size.y + 'px';
	},

	_updateOpacity: function () {
		setOpacity(this._image, this.options.opacity);
	},

	_updateZIndex: function () {
		if (this._image && this.options.zIndex !== undefined && this.options.zIndex !== null) {
			this._image.style.zIndex = this.options.zIndex;
		}
	},

	_overlayOnError: function () {
		// @event error: Event
		// Fired when the ImageOverlay layer has loaded its image
		this.fire('error');

		var errorUrl = this.options.errorOverlayUrl;
		if (errorUrl && this._url !== errorUrl) {
			this._url = errorUrl;
			this._image.src = errorUrl;
		}
	}
});

// @factory L.imageOverlay(imageUrl: String, bounds: LatLngBounds, options?: ImageOverlay options)
// Instantiates an image overlay object given the URL of the image and the
// geographical bounds it is tied to.
var imageOverlay = function (url, bounds, options) {
	return new ImageOverlay(url, bounds, options);
};

/*
 * @class VideoOverlay
 * @aka L.VideoOverlay
 * @inherits ImageOverlay
 *
 * Used to load and display a video player over specific bounds of the map. Extends `ImageOverlay`.
 *
 * A video overlay uses the [`<video>`](https://developer.mozilla.org/docs/Web/HTML/Element/video)
 * HTML5 element.
 *
 * @example
 *
 * ```js
 * var videoUrl = 'https://www.mapbox.com/bites/00188/patricia_nasa.webm',
 * 	videoBounds = [[ 32, -130], [ 13, -100]];
 * L.VideoOverlay(videoUrl, videoBounds ).addTo(map);
 * ```
 */

var VideoOverlay = ImageOverlay.extend({

	// @section
	// @aka VideoOverlay options
	options: {
		// @option autoplay: Boolean = true
		// Whether the video starts playing automatically when loaded.
		autoplay: true,

		// @option loop: Boolean = true
		// Whether the video will loop back to the beginning when played.
		loop: true
	},

	_initImage: function () {
		var wasElementSupplied = this._url.tagName === 'VIDEO';
		var vid = this._image = wasElementSupplied ? this._url : create$1('video');

		vid.class = vid.class || '';
		vid.class += 'leaflet-image-layer ' + (this._zoomAnimated ? 'leaflet-zoom-animated' : '');

		vid.onselectstart = falseFn;
		vid.onmousemove = falseFn;

		// @event load: Event
		// Fired when the video has finished loading the first frame
		vid.onloadeddata = bind(this.fire, this, 'load');

		if (wasElementSupplied) { return; }

		if (!isArray(this._url)) { this._url = [this._url]; }

		vid.autoplay = !!this.options.autoplay;
		vid.loop = !!this.options.loop;
		for (var i = 0; i < this._url.length; i++) {
			var source = create$1('source');
			source.src = this._url[i];
			vid.appendChild(source);
		}
	}

	// @method getElement(): HTMLVideoElement
	// Returns the instance of [`HTMLVideoElement`](https://developer.mozilla.org/docs/Web/API/HTMLVideoElement)
	// used by this overlay.
});


// @factory L.videoOverlay(video: String|Array|HTMLVideoElement, bounds: LatLngBounds, options?: VideoOverlay options)
// Instantiates an image overlay object given the URL of the video (or array of URLs, or even a video element) and the
// geographical bounds it is tied to.

function videoOverlay(video, bounds, options) {
	return new VideoOverlay(video, bounds, options);
}

/*
 * @class DivOverlay
 * @inherits Layer
 * @aka L.DivOverlay
 * Base model for L.Popup and L.Tooltip. Inherit from it for custom popup like plugins.
 */

// @namespace DivOverlay
var DivOverlay = Layer.extend({

	// @section
	// @aka DivOverlay options
	options: {
		// @option offset: Point = Point(0, 7)
		// The offset of the popup position. Useful to control the anchor
		// of the popup when opening it on some overlays.
		offset: [0, 7],

		// @option className: String = ''
		// A custom CSS class name to assign to the popup.
		className: '',

		// @option pane: String = 'popupPane'
		// `Map pane` where the popup will be added.
		pane: 'popupPane'
	},

	initialize: function (options, source) {
		setOptions(this, options);

		this._source = source;
	},

	onAdd: function (map) {
		this._zoomAnimated = map._zoomAnimated;

		if (!this._container) {
			this._initLayout();
		}

		if (map._fadeAnimated) {
			setOpacity(this._container, 0);
		}

		clearTimeout(this._removeTimeout);
		this.getPane().appendChild(this._container);
		this.update();

		if (map._fadeAnimated) {
			setOpacity(this._container, 1);
		}

		this.bringToFront();
	},

	onRemove: function (map) {
		if (map._fadeAnimated) {
			setOpacity(this._container, 0);
			this._removeTimeout = setTimeout(bind(remove, undefined, this._container), 200);
		} else {
			remove(this._container);
		}
	},

	// @namespace Popup
	// @method getLatLng: LatLng
	// Returns the geographical point of popup.
	getLatLng: function () {
		return this._latlng;
	},

	// @method setLatLng(latlng: LatLng): this
	// Sets the geographical point where the popup will open.
	setLatLng: function (latlng) {
		this._latlng = toLatLng(latlng);
		if (this._map) {
			this._updatePosition();
			this._adjustPan();
		}
		return this;
	},

	// @method getContent: String|HTMLElement
	// Returns the content of the popup.
	getContent: function () {
		return this._content;
	},

	// @method setContent(htmlContent: String|HTMLElement|Function): this
	// Sets the HTML content of the popup. If a function is passed the source layer will be passed to the function. The function should return a `String` or `HTMLElement` to be used in the popup.
	setContent: function (content) {
		this._content = content;
		this.update();
		return this;
	},

	// @method getElement: String|HTMLElement
	// Alias for [getContent()](#popup-getcontent)
	getElement: function () {
		return this._container;
	},

	// @method update: null
	// Updates the popup content, layout and position. Useful for updating the popup after something inside changed, e.g. image loaded.
	update: function () {
		if (!this._map) { return; }

		this._container.style.visibility = 'hidden';

		this._updateContent();
		this._updateLayout();
		this._updatePosition();

		this._container.style.visibility = '';

		this._adjustPan();
	},

	getEvents: function () {
		var events = {
			zoom: this._updatePosition,
			viewreset: this._updatePosition
		};

		if (this._zoomAnimated) {
			events.zoomanim = this._animateZoom;
		}
		return events;
	},

	// @method isOpen: Boolean
	// Returns `true` when the popup is visible on the map.
	isOpen: function () {
		return !!this._map && this._map.hasLayer(this);
	},

	// @method bringToFront: this
	// Brings this popup in front of other popups (in the same map pane).
	bringToFront: function () {
		if (this._map) {
			toFront(this._container);
		}
		return this;
	},

	// @method bringToBack: this
	// Brings this popup to the back of other popups (in the same map pane).
	bringToBack: function () {
		if (this._map) {
			toBack(this._container);
		}
		return this;
	},

	_updateContent: function () {
		if (!this._content) { return; }

		var node = this._contentNode;
		var content = (typeof this._content === 'function') ? this._content(this._source || this) : this._content;

		if (typeof content === 'string') {
			node.innerHTML = content;
		} else {
			while (node.hasChildNodes()) {
				node.removeChild(node.firstChild);
			}
			node.appendChild(content);
		}
		this.fire('contentupdate');
	},

	_updatePosition: function () {
		if (!this._map) { return; }

		var pos = this._map.latLngToLayerPoint(this._latlng),
		    offset = toPoint(this.options.offset),
		    anchor = this._getAnchor();

		if (this._zoomAnimated) {
			setPosition(this._container, pos.add(anchor));
		} else {
			offset = offset.add(pos).add(anchor);
		}

		var bottom = this._containerBottom = -offset.y,
		    left = this._containerLeft = -Math.round(this._containerWidth / 2) + offset.x;

		// bottom position the popup in case the height of the popup changes (images loading etc)
		this._container.style.bottom = bottom + 'px';
		this._container.style.left = left + 'px';
	},

	_getAnchor: function () {
		return [0, 0];
	}

});

/*
 * @class Popup
 * @inherits DivOverlay
 * @aka L.Popup
 * Used to open popups in certain places of the map. Use [Map.openPopup](#map-openpopup) to
 * open popups while making sure that only one popup is open at one time
 * (recommended for usability), or use [Map.addLayer](#map-addlayer) to open as many as you want.
 *
 * @example
 *
 * If you want to just bind a popup to marker click and then open it, it's really easy:
 *
 * ```js
 * marker.bindPopup(popupContent).openPopup();
 * ```
 * Path overlays like polylines also have a `bindPopup` method.
 * Here's a more complicated way to open a popup on a map:
 *
 * ```js
 * var popup = L.popup()
 * 	.setLatLng(latlng)
 * 	.setContent('<p>Hello world!<br />This is a nice popup.</p>')
 * 	.openOn(map);
 * ```
 */


// @namespace Popup
var Popup = DivOverlay.extend({

	// @section
	// @aka Popup options
	options: {
		// @option maxWidth: Number = 300
		// Max width of the popup, in pixels.
		maxWidth: 300,

		// @option minWidth: Number = 50
		// Min width of the popup, in pixels.
		minWidth: 50,

		// @option maxHeight: Number = null
		// If set, creates a scrollable container of the given height
		// inside a popup if its content exceeds it.
		maxHeight: null,

		// @option autoPan: Boolean = true
		// Set it to `false` if you don't want the map to do panning animation
		// to fit the opened popup.
		autoPan: true,

		// @option autoPanPaddingTopLeft: Point = null
		// The margin between the popup and the top left corner of the map
		// view after autopanning was performed.
		autoPanPaddingTopLeft: null,

		// @option autoPanPaddingBottomRight: Point = null
		// The margin between the popup and the bottom right corner of the map
		// view after autopanning was performed.
		autoPanPaddingBottomRight: null,

		// @option autoPanPadding: Point = Point(5, 5)
		// Equivalent of setting both top left and bottom right autopan padding to the same value.
		autoPanPadding: [5, 5],

		// @option keepInView: Boolean = false
		// Set it to `true` if you want to prevent users from panning the popup
		// off of the screen while it is open.
		keepInView: false,

		// @option closeButton: Boolean = true
		// Controls the presence of a close button in the popup.
		closeButton: true,

		// @option autoClose: Boolean = true
		// Set it to `false` if you want to override the default behavior of
		// the popup closing when another popup is opened.
		autoClose: true,

		// @option closeOnClick: Boolean = *
		// Set it if you want to override the default behavior of the popup closing when user clicks
		// on the map. Defaults to the map's [`closePopupOnClick`](#map-closepopuponclick) option.

		// @option className: String = ''
		// A custom CSS class name to assign to the popup.
		className: ''
	},

	// @namespace Popup
	// @method openOn(map: Map): this
	// Adds the popup to the map and closes the previous one. The same as `map.openPopup(popup)`.
	openOn: function (map) {
		map.openPopup(this);
		return this;
	},

	onAdd: function (map) {
		DivOverlay.prototype.onAdd.call(this, map);

		// @namespace Map
		// @section Popup events
		// @event popupopen: PopupEvent
		// Fired when a popup is opened in the map
		map.fire('popupopen', {popup: this});

		if (this._source) {
			// @namespace Layer
			// @section Popup events
			// @event popupopen: PopupEvent
			// Fired when a popup bound to this layer is opened
			this._source.fire('popupopen', {popup: this}, true);
			// For non-path layers, we toggle the popup when clicking
			// again the layer, so prevent the map to reopen it.
			if (!(this._source instanceof Path)) {
				this._source.on('preclick', stopPropagation);
			}
		}
	},

	onRemove: function (map) {
		DivOverlay.prototype.onRemove.call(this, map);

		// @namespace Map
		// @section Popup events
		// @event popupclose: PopupEvent
		// Fired when a popup in the map is closed
		map.fire('popupclose', {popup: this});

		if (this._source) {
			// @namespace Layer
			// @section Popup events
			// @event popupclose: PopupEvent
			// Fired when a popup bound to this layer is closed
			this._source.fire('popupclose', {popup: this}, true);
			if (!(this._source instanceof Path)) {
				this._source.off('preclick', stopPropagation);
			}
		}
	},

	getEvents: function () {
		var events = DivOverlay.prototype.getEvents.call(this);

		if (this.options.closeOnClick !== undefined ? this.options.closeOnClick : this._map.options.closePopupOnClick) {
			events.preclick = this._close;
		}

		if (this.options.keepInView) {
			events.moveend = this._adjustPan;
		}

		return events;
	},

	_close: function () {
		if (this._map) {
			this._map.closePopup(this);
		}
	},

	_initLayout: function () {
		var prefix = 'leaflet-popup',
		    container = this._container = create$1('div',
			prefix + ' ' + (this.options.className || '') +
			' leaflet-zoom-animated');

		var wrapper = this._wrapper = create$1('div', prefix + '-content-wrapper', container);
		this._contentNode = create$1('div', prefix + '-content', wrapper);

		disableClickPropagation(wrapper);
		disableScrollPropagation(this._contentNode);
		on(wrapper, 'contextmenu', stopPropagation);

		this._tipContainer = create$1('div', prefix + '-tip-container', container);
		this._tip = create$1('div', prefix + '-tip', this._tipContainer);

		if (this.options.closeButton) {
			var closeButton = this._closeButton = create$1('a', prefix + '-close-button', container);
			closeButton.href = '#close';
			closeButton.innerHTML = '&#215;';

			on(closeButton, 'click', this._onCloseButtonClick, this);
		}
	},

	_updateLayout: function () {
		var container = this._contentNode,
		    style = container.style;

		style.width = '';
		style.whiteSpace = 'nowrap';

		var width = container.offsetWidth;
		width = Math.min(width, this.options.maxWidth);
		width = Math.max(width, this.options.minWidth);

		style.width = (width + 1) + 'px';
		style.whiteSpace = '';

		style.height = '';

		var height = container.offsetHeight,
		    maxHeight = this.options.maxHeight,
		    scrolledClass = 'leaflet-popup-scrolled';

		if (maxHeight && height > maxHeight) {
			style.height = maxHeight + 'px';
			addClass(container, scrolledClass);
		} else {
			removeClass(container, scrolledClass);
		}

		this._containerWidth = this._container.offsetWidth;
	},

	_animateZoom: function (e) {
		var pos = this._map._latLngToNewLayerPoint(this._latlng, e.zoom, e.center),
		    anchor = this._getAnchor();
		setPosition(this._container, pos.add(anchor));
	},

	_adjustPan: function () {
		if (!this.options.autoPan || (this._map._panAnim && this._map._panAnim._inProgress)) { return; }

		var map = this._map,
		    marginBottom = parseInt(getStyle(this._container, 'marginBottom'), 10) || 0,
		    containerHeight = this._container.offsetHeight + marginBottom,
		    containerWidth = this._containerWidth,
		    layerPos = new Point(this._containerLeft, -containerHeight - this._containerBottom);

		layerPos._add(getPosition(this._container));

		var containerPos = map.layerPointToContainerPoint(layerPos),
		    padding = toPoint(this.options.autoPanPadding),
		    paddingTL = toPoint(this.options.autoPanPaddingTopLeft || padding),
		    paddingBR = toPoint(this.options.autoPanPaddingBottomRight || padding),
		    size = map.getSize(),
		    dx = 0,
		    dy = 0;

		if (containerPos.x + containerWidth + paddingBR.x > size.x) { // right
			dx = containerPos.x + containerWidth - size.x + paddingBR.x;
		}
		if (containerPos.x - dx - paddingTL.x < 0) { // left
			dx = containerPos.x - paddingTL.x;
		}
		if (containerPos.y + containerHeight + paddingBR.y > size.y) { // bottom
			dy = containerPos.y + containerHeight - size.y + paddingBR.y;
		}
		if (containerPos.y - dy - paddingTL.y < 0) { // top
			dy = containerPos.y - paddingTL.y;
		}

		// @namespace Map
		// @section Popup events
		// @event autopanstart: Event
		// Fired when the map starts autopanning when opening a popup.
		if (dx || dy) {
			map
			    .fire('autopanstart')
			    .panBy([dx, dy]);
		}
	},

	_onCloseButtonClick: function (e) {
		this._close();
		stop(e);
	},

	_getAnchor: function () {
		// Where should we anchor the popup on the source layer?
		return toPoint(this._source && this._source._getPopupAnchor ? this._source._getPopupAnchor() : [0, 0]);
	}

});

// @namespace Popup
// @factory L.popup(options?: Popup options, source?: Layer)
// Instantiates a `Popup` object given an optional `options` object that describes its appearance and location and an optional `source` object that is used to tag the popup with a reference to the Layer to which it refers.
var popup = function (options, source) {
	return new Popup(options, source);
};


/* @namespace Map
 * @section Interaction Options
 * @option closePopupOnClick: Boolean = true
 * Set it to `false` if you don't want popups to close when user clicks the map.
 */
Map.mergeOptions({
	closePopupOnClick: true
});


// @namespace Map
// @section Methods for Layers and Controls
Map.include({
	// @method openPopup(popup: Popup): this
	// Opens the specified popup while closing the previously opened (to make sure only one is opened at one time for usability).
	// @alternative
	// @method openPopup(content: String|HTMLElement, latlng: LatLng, options?: Popup options): this
	// Creates a popup with the specified content and options and opens it in the given point on a map.
	openPopup: function (popup, latlng, options) {
		if (!(popup instanceof Popup)) {
			popup = new Popup(options).setContent(popup);
		}

		if (latlng) {
			popup.setLatLng(latlng);
		}

		if (this.hasLayer(popup)) {
			return this;
		}

		if (this._popup && this._popup.options.autoClose) {
			this.closePopup();
		}

		this._popup = popup;
		return this.addLayer(popup);
	},

	// @method closePopup(popup?: Popup): this
	// Closes the popup previously opened with [openPopup](#map-openpopup) (or the given one).
	closePopup: function (popup) {
		if (!popup || popup === this._popup) {
			popup = this._popup;
			this._popup = null;
		}
		if (popup) {
			this.removeLayer(popup);
		}
		return this;
	}
});

/*
 * @namespace Layer
 * @section Popup methods example
 *
 * All layers share a set of methods convenient for binding popups to it.
 *
 * ```js
 * var layer = L.Polygon(latlngs).bindPopup('Hi There!').addTo(map);
 * layer.openPopup();
 * layer.closePopup();
 * ```
 *
 * Popups will also be automatically opened when the layer is clicked on and closed when the layer is removed from the map or another popup is opened.
 */

// @section Popup methods
Layer.include({

	// @method bindPopup(content: String|HTMLElement|Function|Popup, options?: Popup options): this
	// Binds a popup to the layer with the passed `content` and sets up the
	// necessary event listeners. If a `Function` is passed it will receive
	// the layer as the first argument and should return a `String` or `HTMLElement`.
	bindPopup: function (content, options) {

		if (content instanceof Popup) {
			setOptions(content, options);
			this._popup = content;
			content._source = this;
		} else {
			if (!this._popup || options) {
				this._popup = new Popup(options, this);
			}
			this._popup.setContent(content);
		}

		if (!this._popupHandlersAdded) {
			this.on({
				click: this._openPopup,
				keypress: this._onKeyPress,
				remove: this.closePopup,
				move: this._movePopup
			});
			this._popupHandlersAdded = true;
		}

		return this;
	},

	// @method unbindPopup(): this
	// Removes the popup previously bound with `bindPopup`.
	unbindPopup: function () {
		if (this._popup) {
			this.off({
				click: this._openPopup,
				keypress: this._onKeyPress,
				remove: this.closePopup,
				move: this._movePopup
			});
			this._popupHandlersAdded = false;
			this._popup = null;
		}
		return this;
	},

	// @method openPopup(latlng?: LatLng): this
	// Opens the bound popup at the specificed `latlng` or at the default popup anchor if no `latlng` is passed.
	openPopup: function (layer, latlng) {
		if (!(layer instanceof Layer)) {
			latlng = layer;
			layer = this;
		}

		if (layer instanceof FeatureGroup) {
			for (var id in this._layers) {
				layer = this._layers[id];
				break;
			}
		}

		if (!latlng) {
			latlng = layer.getCenter ? layer.getCenter() : layer.getLatLng();
		}

		if (this._popup && this._map) {
			// set popup source to this layer
			this._popup._source = layer;

			// update the popup (content, layout, ect...)
			this._popup.update();

			// open the popup on the map
			this._map.openPopup(this._popup, latlng);
		}

		return this;
	},

	// @method closePopup(): this
	// Closes the popup bound to this layer if it is open.
	closePopup: function () {
		if (this._popup) {
			this._popup._close();
		}
		return this;
	},

	// @method togglePopup(): this
	// Opens or closes the popup bound to this layer depending on its current state.
	togglePopup: function (target) {
		if (this._popup) {
			if (this._popup._map) {
				this.closePopup();
			} else {
				this.openPopup(target);
			}
		}
		return this;
	},

	// @method isPopupOpen(): boolean
	// Returns `true` if the popup bound to this layer is currently open.
	isPopupOpen: function () {
		return (this._popup ? this._popup.isOpen() : false);
	},

	// @method setPopupContent(content: String|HTMLElement|Popup): this
	// Sets the content of the popup bound to this layer.
	setPopupContent: function (content) {
		if (this._popup) {
			this._popup.setContent(content);
		}
		return this;
	},

	// @method getPopup(): Popup
	// Returns the popup bound to this layer.
	getPopup: function () {
		return this._popup;
	},

	_openPopup: function (e) {
		var layer = e.layer || e.target;

		if (!this._popup) {
			return;
		}

		if (!this._map) {
			return;
		}

		// prevent map click
		stop(e);

		// if this inherits from Path its a vector and we can just
		// open the popup at the new location
		if (layer instanceof Path) {
			this.openPopup(e.layer || e.target, e.latlng);
			return;
		}

		// otherwise treat it like a marker and figure out
		// if we should toggle it open/closed
		if (this._map.hasLayer(this._popup) && this._popup._source === layer) {
			this.closePopup();
		} else {
			this.openPopup(layer, e.latlng);
		}
	},

	_movePopup: function (e) {
		this._popup.setLatLng(e.latlng);
	},

	_onKeyPress: function (e) {
		if (e.originalEvent.keyCode === 13) {
			this._openPopup(e);
		}
	}
});

/*
 * @class Tooltip
 * @inherits DivOverlay
 * @aka L.Tooltip
 * Used to display small texts on top of map layers.
 *
 * @example
 *
 * ```js
 * marker.bindTooltip("my tooltip text").openTooltip();
 * ```
 * Note about tooltip offset. Leaflet takes two options in consideration
 * for computing tooltip offseting:
 * - the `offset` Tooltip option: it defaults to [0, 0], and it's specific to one tooltip.
 *   Add a positive x offset to move the tooltip to the right, and a positive y offset to
 *   move it to the bottom. Negatives will move to the left and top.
 * - the `tooltipAnchor` Icon option: this will only be considered for Marker. You
 *   should adapt this value if you use a custom icon.
 */


// @namespace Tooltip
var Tooltip = DivOverlay.extend({

	// @section
	// @aka Tooltip options
	options: {
		// @option pane: String = 'tooltipPane'
		// `Map pane` where the tooltip will be added.
		pane: 'tooltipPane',

		// @option offset: Point = Point(0, 0)
		// Optional offset of the tooltip position.
		offset: [0, 0],

		// @option direction: String = 'auto'
		// Direction where to open the tooltip. Possible values are: `right`, `left`,
		// `top`, `bottom`, `center`, `auto`.
		// `auto` will dynamicaly switch between `right` and `left` according to the tooltip
		// position on the map.
		direction: 'auto',

		// @option permanent: Boolean = false
		// Whether to open the tooltip permanently or only on mouseover.
		permanent: false,

		// @option sticky: Boolean = false
		// If true, the tooltip will follow the mouse instead of being fixed at the feature center.
		sticky: false,

		// @option interactive: Boolean = false
		// If true, the tooltip will listen to the feature events.
		interactive: false,

		// @option opacity: Number = 0.9
		// Tooltip container opacity.
		opacity: 0.9
	},

	onAdd: function (map) {
		DivOverlay.prototype.onAdd.call(this, map);
		this.setOpacity(this.options.opacity);

		// @namespace Map
		// @section Tooltip events
		// @event tooltipopen: TooltipEvent
		// Fired when a tooltip is opened in the map.
		map.fire('tooltipopen', {tooltip: this});

		if (this._source) {
			// @namespace Layer
			// @section Tooltip events
			// @event tooltipopen: TooltipEvent
			// Fired when a tooltip bound to this layer is opened.
			this._source.fire('tooltipopen', {tooltip: this}, true);
		}
	},

	onRemove: function (map) {
		DivOverlay.prototype.onRemove.call(this, map);

		// @namespace Map
		// @section Tooltip events
		// @event tooltipclose: TooltipEvent
		// Fired when a tooltip in the map is closed.
		map.fire('tooltipclose', {tooltip: this});

		if (this._source) {
			// @namespace Layer
			// @section Tooltip events
			// @event tooltipclose: TooltipEvent
			// Fired when a tooltip bound to this layer is closed.
			this._source.fire('tooltipclose', {tooltip: this}, true);
		}
	},

	getEvents: function () {
		var events = DivOverlay.prototype.getEvents.call(this);

		if (touch && !this.options.permanent) {
			events.preclick = this._close;
		}

		return events;
	},

	_close: function () {
		if (this._map) {
			this._map.closeTooltip(this);
		}
	},

	_initLayout: function () {
		var prefix = 'leaflet-tooltip',
		    className = prefix + ' ' + (this.options.className || '') + ' leaflet-zoom-' + (this._zoomAnimated ? 'animated' : 'hide');

		this._contentNode = this._container = create$1('div', className);
	},

	_updateLayout: function () {},

	_adjustPan: function () {},

	_setPosition: function (pos) {
		var map = this._map,
		    container = this._container,
		    centerPoint = map.latLngToContainerPoint(map.getCenter()),
		    tooltipPoint = map.layerPointToContainerPoint(pos),
		    direction = this.options.direction,
		    tooltipWidth = container.offsetWidth,
		    tooltipHeight = container.offsetHeight,
		    offset = toPoint(this.options.offset),
		    anchor = this._getAnchor();

		if (direction === 'top') {
			pos = pos.add(toPoint(-tooltipWidth / 2 + offset.x, -tooltipHeight + offset.y + anchor.y, true));
		} else if (direction === 'bottom') {
			pos = pos.subtract(toPoint(tooltipWidth / 2 - offset.x, -offset.y, true));
		} else if (direction === 'center') {
			pos = pos.subtract(toPoint(tooltipWidth / 2 + offset.x, tooltipHeight / 2 - anchor.y + offset.y, true));
		} else if (direction === 'right' || direction === 'auto' && tooltipPoint.x < centerPoint.x) {
			direction = 'right';
			pos = pos.add(toPoint(offset.x + anchor.x, anchor.y - tooltipHeight / 2 + offset.y, true));
		} else {
			direction = 'left';
			pos = pos.subtract(toPoint(tooltipWidth + anchor.x - offset.x, tooltipHeight / 2 - anchor.y - offset.y, true));
		}

		removeClass(container, 'leaflet-tooltip-right');
		removeClass(container, 'leaflet-tooltip-left');
		removeClass(container, 'leaflet-tooltip-top');
		removeClass(container, 'leaflet-tooltip-bottom');
		addClass(container, 'leaflet-tooltip-' + direction);
		setPosition(container, pos);
	},

	_updatePosition: function () {
		var pos = this._map.latLngToLayerPoint(this._latlng);
		this._setPosition(pos);
	},

	setOpacity: function (opacity) {
		this.options.opacity = opacity;

		if (this._container) {
			setOpacity(this._container, opacity);
		}
	},

	_animateZoom: function (e) {
		var pos = this._map._latLngToNewLayerPoint(this._latlng, e.zoom, e.center);
		this._setPosition(pos);
	},

	_getAnchor: function () {
		// Where should we anchor the tooltip on the source layer?
		return toPoint(this._source && this._source._getTooltipAnchor && !this.options.sticky ? this._source._getTooltipAnchor() : [0, 0]);
	}

});

// @namespace Tooltip
// @factory L.tooltip(options?: Tooltip options, source?: Layer)
// Instantiates a Tooltip object given an optional `options` object that describes its appearance and location and an optional `source` object that is used to tag the tooltip with a reference to the Layer to which it refers.
var tooltip = function (options, source) {
	return new Tooltip(options, source);
};

// @namespace Map
// @section Methods for Layers and Controls
Map.include({

	// @method openTooltip(tooltip: Tooltip): this
	// Opens the specified tooltip.
	// @alternative
	// @method openTooltip(content: String|HTMLElement, latlng: LatLng, options?: Tooltip options): this
	// Creates a tooltip with the specified content and options and open it.
	openTooltip: function (tooltip, latlng, options) {
		if (!(tooltip instanceof Tooltip)) {
			tooltip = new Tooltip(options).setContent(tooltip);
		}

		if (latlng) {
			tooltip.setLatLng(latlng);
		}

		if (this.hasLayer(tooltip)) {
			return this;
		}

		return this.addLayer(tooltip);
	},

	// @method closeTooltip(tooltip?: Tooltip): this
	// Closes the tooltip given as parameter.
	closeTooltip: function (tooltip) {
		if (tooltip) {
			this.removeLayer(tooltip);
		}
		return this;
	}

});

/*
 * @namespace Layer
 * @section Tooltip methods example
 *
 * All layers share a set of methods convenient for binding tooltips to it.
 *
 * ```js
 * var layer = L.Polygon(latlngs).bindTooltip('Hi There!').addTo(map);
 * layer.openTooltip();
 * layer.closeTooltip();
 * ```
 */

// @section Tooltip methods
Layer.include({

	// @method bindTooltip(content: String|HTMLElement|Function|Tooltip, options?: Tooltip options): this
	// Binds a tooltip to the layer with the passed `content` and sets up the
	// necessary event listeners. If a `Function` is passed it will receive
	// the layer as the first argument and should return a `String` or `HTMLElement`.
	bindTooltip: function (content, options) {

		if (content instanceof Tooltip) {
			setOptions(content, options);
			this._tooltip = content;
			content._source = this;
		} else {
			if (!this._tooltip || options) {
				this._tooltip = new Tooltip(options, this);
			}
			this._tooltip.setContent(content);

		}

		this._initTooltipInteractions();

		if (this._tooltip.options.permanent && this._map && this._map.hasLayer(this)) {
			this.openTooltip();
		}

		return this;
	},

	// @method unbindTooltip(): this
	// Removes the tooltip previously bound with `bindTooltip`.
	unbindTooltip: function () {
		if (this._tooltip) {
			this._initTooltipInteractions(true);
			this.closeTooltip();
			this._tooltip = null;
		}
		return this;
	},

	_initTooltipInteractions: function (remove$$1) {
		if (!remove$$1 && this._tooltipHandlersAdded) { return; }
		var onOff = remove$$1 ? 'off' : 'on',
		    events = {
			remove: this.closeTooltip,
			move: this._moveTooltip
		    };
		if (!this._tooltip.options.permanent) {
			events.mouseover = this._openTooltip;
			events.mouseout = this.closeTooltip;
			if (this._tooltip.options.sticky) {
				events.mousemove = this._moveTooltip;
			}
			if (touch) {
				events.click = this._openTooltip;
			}
		} else {
			events.add = this._openTooltip;
		}
		this[onOff](events);
		this._tooltipHandlersAdded = !remove$$1;
	},

	// @method openTooltip(latlng?: LatLng): this
	// Opens the bound tooltip at the specificed `latlng` or at the default tooltip anchor if no `latlng` is passed.
	openTooltip: function (layer, latlng) {
		if (!(layer instanceof Layer)) {
			latlng = layer;
			layer = this;
		}

		if (layer instanceof FeatureGroup) {
			for (var id in this._layers) {
				layer = this._layers[id];
				break;
			}
		}

		if (!latlng) {
			latlng = layer.getCenter ? layer.getCenter() : layer.getLatLng();
		}

		if (this._tooltip && this._map) {

			// set tooltip source to this layer
			this._tooltip._source = layer;

			// update the tooltip (content, layout, ect...)
			this._tooltip.update();

			// open the tooltip on the map
			this._map.openTooltip(this._tooltip, latlng);

			// Tooltip container may not be defined if not permanent and never
			// opened.
			if (this._tooltip.options.interactive && this._tooltip._container) {
				addClass(this._tooltip._container, 'leaflet-clickable');
				this.addInteractiveTarget(this._tooltip._container);
			}
		}

		return this;
	},

	// @method closeTooltip(): this
	// Closes the tooltip bound to this layer if it is open.
	closeTooltip: function () {
		if (this._tooltip) {
			this._tooltip._close();
			if (this._tooltip.options.interactive && this._tooltip._container) {
				removeClass(this._tooltip._container, 'leaflet-clickable');
				this.removeInteractiveTarget(this._tooltip._container);
			}
		}
		return this;
	},

	// @method toggleTooltip(): this
	// Opens or closes the tooltip bound to this layer depending on its current state.
	toggleTooltip: function (target) {
		if (this._tooltip) {
			if (this._tooltip._map) {
				this.closeTooltip();
			} else {
				this.openTooltip(target);
			}
		}
		return this;
	},

	// @method isTooltipOpen(): boolean
	// Returns `true` if the tooltip bound to this layer is currently open.
	isTooltipOpen: function () {
		return this._tooltip.isOpen();
	},

	// @method setTooltipContent(content: String|HTMLElement|Tooltip): this
	// Sets the content of the tooltip bound to this layer.
	setTooltipContent: function (content) {
		if (this._tooltip) {
			this._tooltip.setContent(content);
		}
		return this;
	},

	// @method getTooltip(): Tooltip
	// Returns the tooltip bound to this layer.
	getTooltip: function () {
		return this._tooltip;
	},

	_openTooltip: function (e) {
		var layer = e.layer || e.target;

		if (!this._tooltip || !this._map) {
			return;
		}
		this.openTooltip(layer, this._tooltip.options.sticky ? e.latlng : undefined);
	},

	_moveTooltip: function (e) {
		var latlng = e.latlng, containerPoint, layerPoint;
		if (this._tooltip.options.sticky && e.originalEvent) {
			containerPoint = this._map.mouseEventToContainerPoint(e.originalEvent);
			layerPoint = this._map.containerPointToLayerPoint(containerPoint);
			latlng = this._map.layerPointToLatLng(layerPoint);
		}
		this._tooltip.setLatLng(latlng);
	}
});

/*
 * @class DivIcon
 * @aka L.DivIcon
 * @inherits Icon
 *
 * Represents a lightweight icon for markers that uses a simple `<div>`
 * element instead of an image. Inherits from `Icon` but ignores the `iconUrl` and shadow options.
 *
 * @example
 * ```js
 * var myIcon = L.divIcon({className: 'my-div-icon'});
 * // you can set .my-div-icon styles in CSS
 *
 * L.marker([50.505, 30.57], {icon: myIcon}).addTo(map);
 * ```
 *
 * By default, it has a 'leaflet-div-icon' CSS class and is styled as a little white square with a shadow.
 */

var DivIcon = Icon.extend({
	options: {
		// @section
		// @aka DivIcon options
		iconSize: [12, 12], // also can be set through CSS

		// iconAnchor: (Point),
		// popupAnchor: (Point),

		// @option html: String = ''
		// Custom HTML code to put inside the div element, empty by default.
		html: false,

		// @option bgPos: Point = [0, 0]
		// Optional relative position of the background, in pixels
		bgPos: null,

		className: 'leaflet-div-icon'
	},

	createIcon: function (oldIcon) {
		var div = (oldIcon && oldIcon.tagName === 'DIV') ? oldIcon : document.createElement('div'),
		    options = this.options;

		div.innerHTML = options.html !== false ? options.html : '';

		if (options.bgPos) {
			var bgPos = toPoint(options.bgPos);
			div.style.backgroundPosition = (-bgPos.x) + 'px ' + (-bgPos.y) + 'px';
		}
		this._setIconStyles(div, 'icon');

		return div;
	},

	createShadow: function () {
		return null;
	}
});

// @factory L.divIcon(options: DivIcon options)
// Creates a `DivIcon` instance with the given options.
function divIcon(options) {
	return new DivIcon(options);
}

Icon.Default = IconDefault;

/*
 * @class GridLayer
 * @inherits Layer
 * @aka L.GridLayer
 *
 * Generic class for handling a tiled grid of HTML elements. This is the base class for all tile layers and replaces `TileLayer.Canvas`.
 * GridLayer can be extended to create a tiled grid of HTML elements like `<canvas>`, `<img>` or `<div>`. GridLayer will handle creating and animating these DOM elements for you.
 *
 *
 * @section Synchronous usage
 * @example
 *
 * To create a custom layer, extend GridLayer and implement the `createTile()` method, which will be passed a `Point` object with the `x`, `y`, and `z` (zoom level) coordinates to draw your tile.
 *
 * ```js
 * var CanvasLayer = L.GridLayer.extend({
 *     createTile: function(coords){
 *         // create a <canvas> element for drawing
 *         var tile = L.DomUtil.create('canvas', 'leaflet-tile');
 *
 *         // setup tile width and height according to the options
 *         var size = this.getTileSize();
 *         tile.width = size.x;
 *         tile.height = size.y;
 *
 *         // get a canvas context and draw something on it using coords.x, coords.y and coords.z
 *         var ctx = tile.getContext('2d');
 *
 *         // return the tile so it can be rendered on screen
 *         return tile;
 *     }
 * });
 * ```
 *
 * @section Asynchronous usage
 * @example
 *
 * Tile creation can also be asynchronous, this is useful when using a third-party drawing library. Once the tile is finished drawing it can be passed to the `done()` callback.
 *
 * ```js
 * var CanvasLayer = L.GridLayer.extend({
 *     createTile: function(coords, done){
 *         var error;
 *
 *         // create a <canvas> element for drawing
 *         var tile = L.DomUtil.create('canvas', 'leaflet-tile');
 *
 *         // setup tile width and height according to the options
 *         var size = this.getTileSize();
 *         tile.width = size.x;
 *         tile.height = size.y;
 *
 *         // draw something asynchronously and pass the tile to the done() callback
 *         setTimeout(function() {
 *             done(error, tile);
 *         }, 1000);
 *
 *         return tile;
 *     }
 * });
 * ```
 *
 * @section
 */


var GridLayer = Layer.extend({

	// @section
	// @aka GridLayer options
	options: {
		// @option tileSize: Number|Point = 256
		// Width and height of tiles in the grid. Use a number if width and height are equal, or `L.point(width, height)` otherwise.
		tileSize: 256,

		// @option opacity: Number = 1.0
		// Opacity of the tiles. Can be used in the `createTile()` function.
		opacity: 1,

		// @option updateWhenIdle: Boolean = (depends)
		// Load new tiles only when panning ends.
		// `true` by default on mobile browsers, in order to avoid too many requests and keep smooth navigation.
		// `false` otherwise in order to display new tiles _during_ panning, since it is easy to pan outside the
		// [`keepBuffer`](#gridlayer-keepbuffer) option in desktop browsers.
		updateWhenIdle: mobile,

		// @option updateWhenZooming: Boolean = true
		// By default, a smooth zoom animation (during a [touch zoom](#map-touchzoom) or a [`flyTo()`](#map-flyto)) will update grid layers every integer zoom level. Setting this option to `false` will update the grid layer only when the smooth animation ends.
		updateWhenZooming: true,

		// @option updateInterval: Number = 200
		// Tiles will not update more than once every `updateInterval` milliseconds when panning.
		updateInterval: 200,

		// @option zIndex: Number = 1
		// The explicit zIndex of the tile layer.
		zIndex: 1,

		// @option bounds: LatLngBounds = undefined
		// If set, tiles will only be loaded inside the set `LatLngBounds`.
		bounds: null,

		// @option minZoom: Number = 0
		// The minimum zoom level down to which this layer will be displayed (inclusive).
		minZoom: 0,

		// @option maxZoom: Number = undefined
		// The maximum zoom level up to which this layer will be displayed (inclusive).
		maxZoom: undefined,

		// @option maxNativeZoom: Number = undefined
		// Maximum zoom number the tile source has available. If it is specified,
		// the tiles on all zoom levels higher than `maxNativeZoom` will be loaded
		// from `maxNativeZoom` level and auto-scaled.
		maxNativeZoom: undefined,

		// @option minNativeZoom: Number = undefined
		// Minimum zoom number the tile source has available. If it is specified,
		// the tiles on all zoom levels lower than `minNativeZoom` will be loaded
		// from `minNativeZoom` level and auto-scaled.
		minNativeZoom: undefined,

		// @option noWrap: Boolean = false
		// Whether the layer is wrapped around the antimeridian. If `true`, the
		// GridLayer will only be displayed once at low zoom levels. Has no
		// effect when the [map CRS](#map-crs) doesn't wrap around. Can be used
		// in combination with [`bounds`](#gridlayer-bounds) to prevent requesting
		// tiles outside the CRS limits.
		noWrap: false,

		// @option pane: String = 'tilePane'
		// `Map pane` where the grid layer will be added.
		pane: 'tilePane',

		// @option className: String = ''
		// A custom class name to assign to the tile layer. Empty by default.
		className: '',

		// @option keepBuffer: Number = 2
		// When panning the map, keep this many rows and columns of tiles before unloading them.
		keepBuffer: 2
	},

	initialize: function (options) {
		setOptions(this, options);
	},

	onAdd: function () {
		this._initContainer();

		this._levels = {};
		this._tiles = {};

		this._resetView();
		this._update();
	},

	beforeAdd: function (map) {
		map._addZoomLimit(this);
	},

	onRemove: function (map) {
		this._removeAllTiles();
		remove(this._container);
		map._removeZoomLimit(this);
		this._container = null;
		this._tileZoom = null;
	},

	// @method bringToFront: this
	// Brings the tile layer to the top of all tile layers.
	bringToFront: function () {
		if (this._map) {
			toFront(this._container);
			this._setAutoZIndex(Math.max);
		}
		return this;
	},

	// @method bringToBack: this
	// Brings the tile layer to the bottom of all tile layers.
	bringToBack: function () {
		if (this._map) {
			toBack(this._container);
			this._setAutoZIndex(Math.min);
		}
		return this;
	},

	// @method getContainer: HTMLElement
	// Returns the HTML element that contains the tiles for this layer.
	getContainer: function () {
		return this._container;
	},

	// @method setOpacity(opacity: Number): this
	// Changes the [opacity](#gridlayer-opacity) of the grid layer.
	setOpacity: function (opacity) {
		this.options.opacity = opacity;
		this._updateOpacity();
		return this;
	},

	// @method setZIndex(zIndex: Number): this
	// Changes the [zIndex](#gridlayer-zindex) of the grid layer.
	setZIndex: function (zIndex) {
		this.options.zIndex = zIndex;
		this._updateZIndex();

		return this;
	},

	// @method isLoading: Boolean
	// Returns `true` if any tile in the grid layer has not finished loading.
	isLoading: function () {
		return this._loading;
	},

	// @method redraw: this
	// Causes the layer to clear all the tiles and request them again.
	redraw: function () {
		if (this._map) {
			this._removeAllTiles();
			this._update();
		}
		return this;
	},

	getEvents: function () {
		var events = {
			viewprereset: this._invalidateAll,
			viewreset: this._resetView,
			zoom: this._resetView,
			moveend: this._onMoveEnd
		};

		if (!this.options.updateWhenIdle) {
			// update tiles on move, but not more often than once per given interval
			if (!this._onMove) {
				this._onMove = throttle(this._onMoveEnd, this.options.updateInterval, this);
			}

			events.move = this._onMove;
		}

		if (this._zoomAnimated) {
			events.zoomanim = this._animateZoom;
		}

		return events;
	},

	// @section Extension methods
	// Layers extending `GridLayer` shall reimplement the following method.
	// @method createTile(coords: Object, done?: Function): HTMLElement
	// Called only internally, must be overriden by classes extending `GridLayer`.
	// Returns the `HTMLElement` corresponding to the given `coords`. If the `done` callback
	// is specified, it must be called when the tile has finished loading and drawing.
	createTile: function () {
		return document.createElement('div');
	},

	// @section
	// @method getTileSize: Point
	// Normalizes the [tileSize option](#gridlayer-tilesize) into a point. Used by the `createTile()` method.
	getTileSize: function () {
		var s = this.options.tileSize;
		return s instanceof Point ? s : new Point(s, s);
	},

	_updateZIndex: function () {
		if (this._container && this.options.zIndex !== undefined && this.options.zIndex !== null) {
			this._container.style.zIndex = this.options.zIndex;
		}
	},

	_setAutoZIndex: function (compare) {
		// go through all other layers of the same pane, set zIndex to max + 1 (front) or min - 1 (back)

		var layers = this.getPane().children,
		    edgeZIndex = -compare(-Infinity, Infinity); // -Infinity for max, Infinity for min

		for (var i = 0, len = layers.length, zIndex; i < len; i++) {

			zIndex = layers[i].style.zIndex;

			if (layers[i] !== this._container && zIndex) {
				edgeZIndex = compare(edgeZIndex, +zIndex);
			}
		}

		if (isFinite(edgeZIndex)) {
			this.options.zIndex = edgeZIndex + compare(-1, 1);
			this._updateZIndex();
		}
	},

	_updateOpacity: function () {
		if (!this._map) { return; }

		// IE doesn't inherit filter opacity properly, so we're forced to set it on tiles
		if (ielt9) { return; }

		setOpacity(this._container, this.options.opacity);

		var now = +new Date(),
		    nextFrame = false,
		    willPrune = false;

		for (var key in this._tiles) {
			var tile = this._tiles[key];
			if (!tile.current || !tile.loaded) { continue; }

			var fade = Math.min(1, (now - tile.loaded) / 200);

			setOpacity(tile.el, fade);
			if (fade < 1) {
				nextFrame = true;
			} else {
				if (tile.active) {
					willPrune = true;
				} else {
					this._onOpaqueTile(tile);
				}
				tile.active = true;
			}
		}

		if (willPrune && !this._noPrune) { this._pruneTiles(); }

		if (nextFrame) {
			cancelAnimFrame(this._fadeFrame);
			this._fadeFrame = requestAnimFrame(this._updateOpacity, this);
		}
	},

	_onOpaqueTile: falseFn,

	_initContainer: function () {
		if (this._container) { return; }

		this._container = create$1('div', 'leaflet-layer ' + (this.options.className || ''));
		this._updateZIndex();

		if (this.options.opacity < 1) {
			this._updateOpacity();
		}

		this.getPane().appendChild(this._container);
	},

	_updateLevels: function () {

		var zoom = this._tileZoom,
		    maxZoom = this.options.maxZoom;

		if (zoom === undefined) { return undefined; }

		for (var z in this._levels) {
			if (this._levels[z].el.children.length || z === zoom) {
				this._levels[z].el.style.zIndex = maxZoom - Math.abs(zoom - z);
				this._onUpdateLevel(z);
			} else {
				remove(this._levels[z].el);
				this._removeTilesAtZoom(z);
				this._onRemoveLevel(z);
				delete this._levels[z];
			}
		}

		var level = this._levels[zoom],
		    map = this._map;

		if (!level) {
			level = this._levels[zoom] = {};

			level.el = create$1('div', 'leaflet-tile-container leaflet-zoom-animated', this._container);
			level.el.style.zIndex = maxZoom;

			level.origin = map.project(map.unproject(map.getPixelOrigin()), zoom).round();
			level.zoom = zoom;

			this._setZoomTransform(level, map.getCenter(), map.getZoom());

			// force the browser to consider the newly added element for transition
			falseFn(level.el.offsetWidth);

			this._onCreateLevel(level);
		}

		this._level = level;

		return level;
	},

	_onUpdateLevel: falseFn,

	_onRemoveLevel: falseFn,

	_onCreateLevel: falseFn,

	_pruneTiles: function () {
		if (!this._map) {
			return;
		}

		var key, tile;

		var zoom = this._map.getZoom();
		if (zoom > this.options.maxZoom ||
			zoom < this.options.minZoom) {
			this._removeAllTiles();
			return;
		}

		for (key in this._tiles) {
			tile = this._tiles[key];
			tile.retain = tile.current;
		}

		for (key in this._tiles) {
			tile = this._tiles[key];
			if (tile.current && !tile.active) {
				var coords = tile.coords;
				if (!this._retainParent(coords.x, coords.y, coords.z, coords.z - 5)) {
					this._retainChildren(coords.x, coords.y, coords.z, coords.z + 2);
				}
			}
		}

		for (key in this._tiles) {
			if (!this._tiles[key].retain) {
				this._removeTile(key);
			}
		}
	},

	_removeTilesAtZoom: function (zoom) {
		for (var key in this._tiles) {
			if (this._tiles[key].coords.z !== zoom) {
				continue;
			}
			this._removeTile(key);
		}
	},

	_removeAllTiles: function () {
		for (var key in this._tiles) {
			this._removeTile(key);
		}
	},

	_invalidateAll: function () {
		for (var z in this._levels) {
			remove(this._levels[z].el);
			this._onRemoveLevel(z);
			delete this._levels[z];
		}
		this._removeAllTiles();

		this._tileZoom = null;
	},

	_retainParent: function (x, y, z, minZoom) {
		var x2 = Math.floor(x / 2),
		    y2 = Math.floor(y / 2),
		    z2 = z - 1,
		    coords2 = new Point(+x2, +y2);
		coords2.z = +z2;

		var key = this._tileCoordsToKey(coords2),
		    tile = this._tiles[key];

		if (tile && tile.active) {
			tile.retain = true;
			return true;

		} else if (tile && tile.loaded) {
			tile.retain = true;
		}

		if (z2 > minZoom) {
			return this._retainParent(x2, y2, z2, minZoom);
		}

		return false;
	},

	_retainChildren: function (x, y, z, maxZoom) {

		for (var i = 2 * x; i < 2 * x + 2; i++) {
			for (var j = 2 * y; j < 2 * y + 2; j++) {

				var coords = new Point(i, j);
				coords.z = z + 1;

				var key = this._tileCoordsToKey(coords),
				    tile = this._tiles[key];

				if (tile && tile.active) {
					tile.retain = true;
					continue;

				} else if (tile && tile.loaded) {
					tile.retain = true;
				}

				if (z + 1 < maxZoom) {
					this._retainChildren(i, j, z + 1, maxZoom);
				}
			}
		}
	},

	_resetView: function (e) {
		var animating = e && (e.pinch || e.flyTo);
		this._setView(this._map.getCenter(), this._map.getZoom(), animating, animating);
	},

	_animateZoom: function (e) {
		this._setView(e.center, e.zoom, true, e.noUpdate);
	},

	_clampZoom: function (zoom) {
		var options = this.options;

		if (undefined !== options.minNativeZoom && zoom < options.minNativeZoom) {
			return options.minNativeZoom;
		}

		if (undefined !== options.maxNativeZoom && options.maxNativeZoom < zoom) {
			return options.maxNativeZoom;
		}

		return zoom;
	},

	_setView: function (center, zoom, noPrune, noUpdate) {
		var tileZoom = this._clampZoom(Math.round(zoom));
		if ((this.options.maxZoom !== undefined && tileZoom > this.options.maxZoom) ||
		    (this.options.minZoom !== undefined && tileZoom < this.options.minZoom)) {
			tileZoom = undefined;
		}

		var tileZoomChanged = this.options.updateWhenZooming && (tileZoom !== this._tileZoom);

		if (!noUpdate || tileZoomChanged) {

			this._tileZoom = tileZoom;

			if (this._abortLoading) {
				this._abortLoading();
			}

			this._updateLevels();
			this._resetGrid();

			if (tileZoom !== undefined) {
				this._update(center);
			}

			if (!noPrune) {
				this._pruneTiles();
			}

			// Flag to prevent _updateOpacity from pruning tiles during
			// a zoom anim or a pinch gesture
			this._noPrune = !!noPrune;
		}

		this._setZoomTransforms(center, zoom);
	},

	_setZoomTransforms: function (center, zoom) {
		for (var i in this._levels) {
			this._setZoomTransform(this._levels[i], center, zoom);
		}
	},

	_setZoomTransform: function (level, center, zoom) {
		var scale = this._map.getZoomScale(zoom, level.zoom),
		    translate = level.origin.multiplyBy(scale)
		        .subtract(this._map._getNewPixelOrigin(center, zoom)).round();

		if (any3d) {
			setTransform(level.el, translate, scale);
		} else {
			setPosition(level.el, translate);
		}
	},

	_resetGrid: function () {
		var map = this._map,
		    crs = map.options.crs,
		    tileSize = this._tileSize = this.getTileSize(),
		    tileZoom = this._tileZoom;

		var bounds = this._map.getPixelWorldBounds(this._tileZoom);
		if (bounds) {
			this._globalTileRange = this._pxBoundsToTileRange(bounds);
		}

		this._wrapX = crs.wrapLng && !this.options.noWrap && [
			Math.floor(map.project([0, crs.wrapLng[0]], tileZoom).x / tileSize.x),
			Math.ceil(map.project([0, crs.wrapLng[1]], tileZoom).x / tileSize.y)
		];
		this._wrapY = crs.wrapLat && !this.options.noWrap && [
			Math.floor(map.project([crs.wrapLat[0], 0], tileZoom).y / tileSize.x),
			Math.ceil(map.project([crs.wrapLat[1], 0], tileZoom).y / tileSize.y)
		];
	},

	_onMoveEnd: function () {
		if (!this._map || this._map._animatingZoom) { return; }

		this._update();
	},

	_getTiledPixelBounds: function (center) {
		var map = this._map,
		    mapZoom = map._animatingZoom ? Math.max(map._animateToZoom, map.getZoom()) : map.getZoom(),
		    scale = map.getZoomScale(mapZoom, this._tileZoom),
		    pixelCenter = map.project(center, this._tileZoom).floor(),
		    halfSize = map.getSize().divideBy(scale * 2);

		return new Bounds(pixelCenter.subtract(halfSize), pixelCenter.add(halfSize));
	},

	// Private method to load tiles in the grid's active zoom level according to map bounds
	_update: function (center) {
		var map = this._map;
		if (!map) { return; }
		var zoom = this._clampZoom(map.getZoom());

		if (center === undefined) { center = map.getCenter(); }
		if (this._tileZoom === undefined) { return; }	// if out of minzoom/maxzoom

		var pixelBounds = this._getTiledPixelBounds(center),
		    tileRange = this._pxBoundsToTileRange(pixelBounds),
		    tileCenter = tileRange.getCenter(),
		    queue = [],
		    margin = this.options.keepBuffer,
		    noPruneRange = new Bounds(tileRange.getBottomLeft().subtract([margin, -margin]),
		                              tileRange.getTopRight().add([margin, -margin]));

		// Sanity check: panic if the tile range contains Infinity somewhere.
		if (!(isFinite(tileRange.min.x) &&
		      isFinite(tileRange.min.y) &&
		      isFinite(tileRange.max.x) &&
		      isFinite(tileRange.max.y))) { throw new Error('Attempted to load an infinite number of tiles'); }

		for (var key in this._tiles) {
			var c = this._tiles[key].coords;
			if (c.z !== this._tileZoom || !noPruneRange.contains(new Point(c.x, c.y))) {
				this._tiles[key].current = false;
			}
		}

		// _update just loads more tiles. If the tile zoom level differs too much
		// from the map's, let _setView reset levels and prune old tiles.
		if (Math.abs(zoom - this._tileZoom) > 1) { this._setView(center, zoom); return; }

		// create a queue of coordinates to load tiles from
		for (var j = tileRange.min.y; j <= tileRange.max.y; j++) {
			for (var i = tileRange.min.x; i <= tileRange.max.x; i++) {
				var coords = new Point(i, j);
				coords.z = this._tileZoom;

				if (!this._isValidTile(coords)) { continue; }

				if (!this._tiles[this._tileCoordsToKey(coords)]) {
					queue.push(coords);
				}
			}
		}

		// sort tile queue to load tiles in order of their distance to center
		queue.sort(function (a, b) {
			return a.distanceTo(tileCenter) - b.distanceTo(tileCenter);
		});

		if (queue.length !== 0) {
			// if it's the first batch of tiles to load
			if (!this._loading) {
				this._loading = true;
				// @event loading: Event
				// Fired when the grid layer starts loading tiles.
				this.fire('loading');
			}

			// create DOM fragment to append tiles in one batch
			var fragment = document.createDocumentFragment();

			for (i = 0; i < queue.length; i++) {
				this._addTile(queue[i], fragment);
			}

			this._level.el.appendChild(fragment);
		}
	},

	_isValidTile: function (coords) {
		var crs = this._map.options.crs;

		if (!crs.infinite) {
			// don't load tile if it's out of bounds and not wrapped
			var bounds = this._globalTileRange;
			if ((!crs.wrapLng && (coords.x < bounds.min.x || coords.x > bounds.max.x)) ||
			    (!crs.wrapLat && (coords.y < bounds.min.y || coords.y > bounds.max.y))) { return false; }
		}

		if (!this.options.bounds) { return true; }

		// don't load tile if it doesn't intersect the bounds in options
		var tileBounds = this._tileCoordsToBounds(coords);
		return toLatLngBounds(this.options.bounds).overlaps(tileBounds);
	},

	_keyToBounds: function (key) {
		return this._tileCoordsToBounds(this._keyToTileCoords(key));
	},

	// converts tile coordinates to its geographical bounds
	_tileCoordsToBounds: function (coords) {

		var map = this._map,
		    tileSize = this.getTileSize(),

		    nwPoint = coords.scaleBy(tileSize),
		    sePoint = nwPoint.add(tileSize),

		    nw = map.unproject(nwPoint, coords.z),
		    se = map.unproject(sePoint, coords.z),
		    bounds = new LatLngBounds(nw, se);

		if (!this.options.noWrap) {
			map.wrapLatLngBounds(bounds);
		}

		return bounds;
	},

	// converts tile coordinates to key for the tile cache
	_tileCoordsToKey: function (coords) {
		return coords.x + ':' + coords.y + ':' + coords.z;
	},

	// converts tile cache key to coordinates
	_keyToTileCoords: function (key) {
		var k = key.split(':'),
		    coords = new Point(+k[0], +k[1]);
		coords.z = +k[2];
		return coords;
	},

	_removeTile: function (key) {
		var tile = this._tiles[key];
		if (!tile) { return; }

		remove(tile.el);

		delete this._tiles[key];

		// @event tileunload: TileEvent
		// Fired when a tile is removed (e.g. when a tile goes off the screen).
		this.fire('tileunload', {
			tile: tile.el,
			coords: this._keyToTileCoords(key)
		});
	},

	_initTile: function (tile) {
		addClass(tile, 'leaflet-tile');

		var tileSize = this.getTileSize();
		tile.style.width = tileSize.x + 'px';
		tile.style.height = tileSize.y + 'px';

		tile.onselectstart = falseFn;
		tile.onmousemove = falseFn;

		// update opacity on tiles in IE7-8 because of filter inheritance problems
		if (ielt9 && this.options.opacity < 1) {
			setOpacity(tile, this.options.opacity);
		}

		// without this hack, tiles disappear after zoom on Chrome for Android
		// https://github.com/Leaflet/Leaflet/issues/2078
		if (android && !android23) {
			tile.style.WebkitBackfaceVisibility = 'hidden';
		}
	},

	_addTile: function (coords, container) {
		var tilePos = this._getTilePos(coords),
		    key = this._tileCoordsToKey(coords);

		var tile = this.createTile(this._wrapCoords(coords), bind(this._tileReady, this, coords));

		this._initTile(tile);

		// if createTile is defined with a second argument ("done" callback),
		// we know that tile is async and will be ready later; otherwise
		if (this.createTile.length < 2) {
			// mark tile as ready, but delay one frame for opacity animation to happen
			requestAnimFrame(bind(this._tileReady, this, coords, null, tile));
		}

		setPosition(tile, tilePos);

		// save tile in cache
		this._tiles[key] = {
			el: tile,
			coords: coords,
			current: true
		};

		container.appendChild(tile);
		// @event tileloadstart: TileEvent
		// Fired when a tile is requested and starts loading.
		this.fire('tileloadstart', {
			tile: tile,
			coords: coords
		});
	},

	_tileReady: function (coords, err, tile) {
		if (!this._map) { return; }

		if (err) {
			// @event tileerror: TileErrorEvent
			// Fired when there is an error loading a tile.
			this.fire('tileerror', {
				error: err,
				tile: tile,
				coords: coords
			});
		}

		var key = this._tileCoordsToKey(coords);

		tile = this._tiles[key];
		if (!tile) { return; }

		tile.loaded = +new Date();
		if (this._map._fadeAnimated) {
			setOpacity(tile.el, 0);
			cancelAnimFrame(this._fadeFrame);
			this._fadeFrame = requestAnimFrame(this._updateOpacity, this);
		} else {
			tile.active = true;
			this._pruneTiles();
		}

		if (!err) {
			addClass(tile.el, 'leaflet-tile-loaded');

			// @event tileload: TileEvent
			// Fired when a tile loads.
			this.fire('tileload', {
				tile: tile.el,
				coords: coords
			});
		}

		if (this._noTilesToLoad()) {
			this._loading = false;
			// @event load: Event
			// Fired when the grid layer loaded all visible tiles.
			this.fire('load');

			if (ielt9 || !this._map._fadeAnimated) {
				requestAnimFrame(this._pruneTiles, this);
			} else {
				// Wait a bit more than 0.2 secs (the duration of the tile fade-in)
				// to trigger a pruning.
				setTimeout(bind(this._pruneTiles, this), 250);
			}
		}
	},

	_getTilePos: function (coords) {
		return coords.scaleBy(this.getTileSize()).subtract(this._level.origin);
	},

	_wrapCoords: function (coords) {
		var newCoords = new Point(
			this._wrapX ? wrapNum(coords.x, this._wrapX) : coords.x,
			this._wrapY ? wrapNum(coords.y, this._wrapY) : coords.y);
		newCoords.z = coords.z;
		return newCoords;
	},

	_pxBoundsToTileRange: function (bounds) {
		var tileSize = this.getTileSize();
		return new Bounds(
			bounds.min.unscaleBy(tileSize).floor(),
			bounds.max.unscaleBy(tileSize).ceil().subtract([1, 1]));
	},

	_noTilesToLoad: function () {
		for (var key in this._tiles) {
			if (!this._tiles[key].loaded) { return false; }
		}
		return true;
	}
});

// @factory L.gridLayer(options?: GridLayer options)
// Creates a new instance of GridLayer with the supplied options.
function gridLayer(options) {
	return new GridLayer(options);
}

/*
 * @class TileLayer
 * @inherits GridLayer
 * @aka L.TileLayer
 * Used to load and display tile layers on the map. Extends `GridLayer`.
 *
 * @example
 *
 * ```js
 * L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png?{foo}', {foo: 'bar'}).addTo(map);
 * ```
 *
 * @section URL template
 * @example
 *
 * A string of the following form:
 *
 * ```
 * 'http://{s}.somedomain.com/blabla/{z}/{x}/{y}{r}.png'
 * ```
 *
 * `{s}` means one of the available subdomains (used sequentially to help with browser parallel requests per domain limitation; subdomain values are specified in options; `a`, `b` or `c` by default, can be omitted), `{z}` — zoom level, `{x}` and `{y}` — tile coordinates. `{r}` can be used to add "&commat;2x" to the URL to load retina tiles.
 *
 * You can use custom keys in the template, which will be [evaluated](#util-template) from TileLayer options, like this:
 *
 * ```
 * L.tileLayer('http://{s}.somedomain.com/{foo}/{z}/{x}/{y}.png', {foo: 'bar'});
 * ```
 */


var TileLayer = GridLayer.extend({

	// @section
	// @aka TileLayer options
	options: {
		// @option minZoom: Number = 0
		// The minimum zoom level down to which this layer will be displayed (inclusive).
		minZoom: 0,

		// @option maxZoom: Number = 18
		// The maximum zoom level up to which this layer will be displayed (inclusive).
		maxZoom: 18,

		// @option subdomains: String|String[] = 'abc'
		// Subdomains of the tile service. Can be passed in the form of one string (where each letter is a subdomain name) or an array of strings.
		subdomains: 'abc',

		// @option errorTileUrl: String = ''
		// URL to the tile image to show in place of the tile that failed to load.
		errorTileUrl: '',

		// @option zoomOffset: Number = 0
		// The zoom number used in tile URLs will be offset with this value.
		zoomOffset: 0,

		// @option tms: Boolean = false
		// If `true`, inverses Y axis numbering for tiles (turn this on for [TMS](https://en.wikipedia.org/wiki/Tile_Map_Service) services).
		tms: false,

		// @option zoomReverse: Boolean = false
		// If set to true, the zoom number used in tile URLs will be reversed (`maxZoom - zoom` instead of `zoom`)
		zoomReverse: false,

		// @option detectRetina: Boolean = false
		// If `true` and user is on a retina display, it will request four tiles of half the specified size and a bigger zoom level in place of one to utilize the high resolution.
		detectRetina: false,

		// @option crossOrigin: Boolean = false
		// If true, all tiles will have their crossOrigin attribute set to ''. This is needed if you want to access tile pixel data.
		crossOrigin: false
	},

	initialize: function (url, options) {

		this._url = url;

		options = setOptions(this, options);

		// detecting retina displays, adjusting tileSize and zoom levels
		if (options.detectRetina && retina && options.maxZoom > 0) {

			options.tileSize = Math.floor(options.tileSize / 2);

			if (!options.zoomReverse) {
				options.zoomOffset++;
				options.maxZoom--;
			} else {
				options.zoomOffset--;
				options.minZoom++;
			}

			options.minZoom = Math.max(0, options.minZoom);
		}

		if (typeof options.subdomains === 'string') {
			options.subdomains = options.subdomains.split('');
		}

		// for https://github.com/Leaflet/Leaflet/issues/137
		if (!android) {
			this.on('tileunload', this._onTileRemove);
		}
	},

	// @method setUrl(url: String, noRedraw?: Boolean): this
	// Updates the layer's URL template and redraws it (unless `noRedraw` is set to `true`).
	setUrl: function (url, noRedraw) {
		this._url = url;

		if (!noRedraw) {
			this.redraw();
		}
		return this;
	},

	// @method createTile(coords: Object, done?: Function): HTMLElement
	// Called only internally, overrides GridLayer's [`createTile()`](#gridlayer-createtile)
	// to return an `<img>` HTML element with the appropiate image URL given `coords`. The `done`
	// callback is called when the tile has been loaded.
	createTile: function (coords, done) {
		var tile = document.createElement('img');

		on(tile, 'load', bind(this._tileOnLoad, this, done, tile));
		on(tile, 'error', bind(this._tileOnError, this, done, tile));

		if (this.options.crossOrigin) {
			tile.crossOrigin = '';
		}

		/*
		 Alt tag is set to empty string to keep screen readers from reading URL and for compliance reasons
		 http://www.w3.org/TR/WCAG20-TECHS/H67
		*/
		tile.alt = '';

		/*
		 Set role="presentation" to force screen readers to ignore this
		 https://www.w3.org/TR/wai-aria/roles#textalternativecomputation
		*/
		tile.setAttribute('role', 'presentation');

		tile.src = this.getTileUrl(coords);

		return tile;
	},

	// @section Extension methods
	// @uninheritable
	// Layers extending `TileLayer` might reimplement the following method.
	// @method getTileUrl(coords: Object): String
	// Called only internally, returns the URL for a tile given its coordinates.
	// Classes extending `TileLayer` can override this function to provide custom tile URL naming schemes.
	getTileUrl: function (coords) {
		var data = {
			r: retina ? '@2x' : '',
			s: this._getSubdomain(coords),
			x: coords.x,
			y: coords.y,
			z: this._getZoomForUrl()
		};
		if (this._map && !this._map.options.crs.infinite) {
			var invertedY = this._globalTileRange.max.y - coords.y;
			if (this.options.tms) {
				data['y'] = invertedY;
			}
			data['-y'] = invertedY;
		}

		return template(this._url, extend(data, this.options));
	},

	_tileOnLoad: function (done, tile) {
		// For https://github.com/Leaflet/Leaflet/issues/3332
		if (ielt9) {
			setTimeout(bind(done, this, null, tile), 0);
		} else {
			done(null, tile);
		}
	},

	_tileOnError: function (done, tile, e) {
		var errorUrl = this.options.errorTileUrl;
		if (errorUrl && tile.src !== errorUrl) {
			tile.src = errorUrl;
		}
		done(e, tile);
	},

	_onTileRemove: function (e) {
		e.tile.onload = null;
	},

	_getZoomForUrl: function () {
		var zoom = this._tileZoom,
		maxZoom = this.options.maxZoom,
		zoomReverse = this.options.zoomReverse,
		zoomOffset = this.options.zoomOffset;

		if (zoomReverse) {
			zoom = maxZoom - zoom;
		}

		return zoom + zoomOffset;
	},

	_getSubdomain: function (tilePoint) {
		var index = Math.abs(tilePoint.x + tilePoint.y) % this.options.subdomains.length;
		return this.options.subdomains[index];
	},

	// stops loading all tiles in the background layer
	_abortLoading: function () {
		var i, tile;
		for (i in this._tiles) {
			if (this._tiles[i].coords.z !== this._tileZoom) {
				tile = this._tiles[i].el;

				tile.onload = falseFn;
				tile.onerror = falseFn;

				if (!tile.complete) {
					tile.src = emptyImageUrl;
					remove(tile);
				}
			}
		}
	}
});


// @factory L.tilelayer(urlTemplate: String, options?: TileLayer options)
// Instantiates a tile layer object given a `URL template` and optionally an options object.

function tileLayer(url, options) {
	return new TileLayer(url, options);
}

/*
 * @class TileLayer.WMS
 * @inherits TileLayer
 * @aka L.TileLayer.WMS
 * Used to display [WMS](https://en.wikipedia.org/wiki/Web_Map_Service) services as tile layers on the map. Extends `TileLayer`.
 *
 * @example
 *
 * ```js
 * var nexrad = L.tileLayer.wms("http://mesonet.agron.iastate.edu/cgi-bin/wms/nexrad/n0r.cgi", {
 * 	layers: 'nexrad-n0r-900913',
 * 	format: 'image/png',
 * 	transparent: true,
 * 	attribution: "Weather data © 2012 IEM Nexrad"
 * });
 * ```
 */

var TileLayerWMS = TileLayer.extend({

	// @section
	// @aka TileLayer.WMS options
	// If any custom options not documented here are used, they will be sent to the
	// WMS server as extra parameters in each request URL. This can be useful for
	// [non-standard vendor WMS parameters](http://docs.geoserver.org/stable/en/user/services/wms/vendor.html).
	defaultWmsParams: {
		service: 'WMS',
		request: 'GetMap',

		// @option layers: String = ''
		// **(required)** Comma-separated list of WMS layers to show.
		layers: '',

		// @option styles: String = ''
		// Comma-separated list of WMS styles.
		styles: '',

		// @option format: String = 'image/jpeg'
		// WMS image format (use `'image/png'` for layers with transparency).
		format: 'image/jpeg',

		// @option transparent: Boolean = false
		// If `true`, the WMS service will return images with transparency.
		transparent: false,

		// @option version: String = '1.1.1'
		// Version of the WMS service to use
		version: '1.1.1'
	},

	options: {
		// @option crs: CRS = null
		// Coordinate Reference System to use for the WMS requests, defaults to
		// map CRS. Don't change this if you're not sure what it means.
		crs: null,

		// @option uppercase: Boolean = false
		// If `true`, WMS request parameter keys will be uppercase.
		uppercase: false
	},

	initialize: function (url, options) {

		this._url = url;

		var wmsParams = extend({}, this.defaultWmsParams);

		// all keys that are not TileLayer options go to WMS params
		for (var i in options) {
			if (!(i in this.options)) {
				wmsParams[i] = options[i];
			}
		}

		options = setOptions(this, options);

		wmsParams.width = wmsParams.height = options.tileSize * (options.detectRetina && retina ? 2 : 1);

		this.wmsParams = wmsParams;
	},

	onAdd: function (map) {

		this._crs = this.options.crs || map.options.crs;
		this._wmsVersion = parseFloat(this.wmsParams.version);

		var projectionKey = this._wmsVersion >= 1.3 ? 'crs' : 'srs';
		this.wmsParams[projectionKey] = this._crs.code;

		TileLayer.prototype.onAdd.call(this, map);
	},

	getTileUrl: function (coords) {

		var tileBounds = this._tileCoordsToBounds(coords),
		    nw = this._crs.project(tileBounds.getNorthWest()),
		    se = this._crs.project(tileBounds.getSouthEast()),

		    bbox = (this._wmsVersion >= 1.3 && this._crs === EPSG4326 ?
			    [se.y, nw.x, nw.y, se.x] :
			    [nw.x, se.y, se.x, nw.y]).join(','),

		    url = TileLayer.prototype.getTileUrl.call(this, coords);

		return url +
			getParamString(this.wmsParams, url, this.options.uppercase) +
			(this.options.uppercase ? '&BBOX=' : '&bbox=') + bbox;
	},

	// @method setParams(params: Object, noRedraw?: Boolean): this
	// Merges an object with the new parameters and re-requests tiles on the current screen (unless `noRedraw` was set to true).
	setParams: function (params, noRedraw) {

		extend(this.wmsParams, params);

		if (!noRedraw) {
			this.redraw();
		}

		return this;
	}
});


// @factory L.tileLayer.wms(baseUrl: String, options: TileLayer.WMS options)
// Instantiates a WMS tile layer object given a base URL of the WMS service and a WMS parameters/options object.
function tileLayerWMS(url, options) {
	return new TileLayerWMS(url, options);
}

TileLayer.WMS = TileLayerWMS;
tileLayer.wms = tileLayerWMS;

/*
 * @class Renderer
 * @inherits Layer
 * @aka L.Renderer
 *
 * Base class for vector renderer implementations (`SVG`, `Canvas`). Handles the
 * DOM container of the renderer, its bounds, and its zoom animation.
 *
 * A `Renderer` works as an implicit layer group for all `Path`s - the renderer
 * itself can be added or removed to the map. All paths use a renderer, which can
 * be implicit (the map will decide the type of renderer and use it automatically)
 * or explicit (using the [`renderer`](#path-renderer) option of the path).
 *
 * Do not use this class directly, use `SVG` and `Canvas` instead.
 *
 * @event update: Event
 * Fired when the renderer updates its bounds, center and zoom, for example when
 * its map has moved
 */

var Renderer = Layer.extend({

	// @section
	// @aka Renderer options
	options: {
		// @option padding: Number = 0.1
		// How much to extend the clip area around the map view (relative to its size)
		// e.g. 0.1 would be 10% of map view in each direction
		padding: 0.1
	},

	initialize: function (options) {
		setOptions(this, options);
		stamp(this);
		this._layers = this._layers || {};
	},

	onAdd: function () {
		if (!this._container) {
			this._initContainer(); // defined by renderer implementations

			if (this._zoomAnimated) {
				addClass(this._container, 'leaflet-zoom-animated');
			}
		}

		this.getPane().appendChild(this._container);
		this._update();
		this.on('update', this._updatePaths, this);
	},

	onRemove: function () {
		this.off('update', this._updatePaths, this);
		this._destroyContainer();
	},

	getEvents: function () {
		var events = {
			viewreset: this._reset,
			zoom: this._onZoom,
			moveend: this._update,
			zoomend: this._onZoomEnd
		};
		if (this._zoomAnimated) {
			events.zoomanim = this._onAnimZoom;
		}
		return events;
	},

	_onAnimZoom: function (ev) {
		this._updateTransform(ev.center, ev.zoom);
	},

	_onZoom: function () {
		this._updateTransform(this._map.getCenter(), this._map.getZoom());
	},

	_updateTransform: function (center, zoom) {
		var scale = this._map.getZoomScale(zoom, this._zoom),
		    position = getPosition(this._container),
		    viewHalf = this._map.getSize().multiplyBy(0.5 + this.options.padding),
		    currentCenterPoint = this._map.project(this._center, zoom),
		    destCenterPoint = this._map.project(center, zoom),
		    centerOffset = destCenterPoint.subtract(currentCenterPoint),

		    topLeftOffset = viewHalf.multiplyBy(-scale).add(position).add(viewHalf).subtract(centerOffset);

		if (any3d) {
			setTransform(this._container, topLeftOffset, scale);
		} else {
			setPosition(this._container, topLeftOffset);
		}
	},

	_reset: function () {
		this._update();
		this._updateTransform(this._center, this._zoom);

		for (var id in this._layers) {
			this._layers[id]._reset();
		}
	},

	_onZoomEnd: function () {
		for (var id in this._layers) {
			this._layers[id]._project();
		}
	},

	_updatePaths: function () {
		for (var id in this._layers) {
			this._layers[id]._update();
		}
	},

	_update: function () {
		// Update pixel bounds of renderer container (for positioning/sizing/clipping later)
		// Subclasses are responsible of firing the 'update' event.
		var p = this.options.padding,
		    size = this._map.getSize(),
		    min = this._map.containerPointToLayerPoint(size.multiplyBy(-p)).round();

		this._bounds = new Bounds(min, min.add(size.multiplyBy(1 + p * 2)).round());

		this._center = this._map.getCenter();
		this._zoom = this._map.getZoom();
	}
});

/*
 * @class Canvas
 * @inherits Renderer
 * @aka L.Canvas
 *
 * Allows vector layers to be displayed with [`<canvas>`](https://developer.mozilla.org/docs/Web/API/Canvas_API).
 * Inherits `Renderer`.
 *
 * Due to [technical limitations](http://caniuse.com/#search=canvas), Canvas is not
 * available in all web browsers, notably IE8, and overlapping geometries might
 * not display properly in some edge cases.
 *
 * @example
 *
 * Use Canvas by default for all paths in the map:
 *
 * ```js
 * var map = L.map('map', {
 * 	renderer: L.canvas()
 * });
 * ```
 *
 * Use a Canvas renderer with extra padding for specific vector geometries:
 *
 * ```js
 * var map = L.map('map');
 * var myRenderer = L.canvas({ padding: 0.5 });
 * var line = L.polyline( coordinates, { renderer: myRenderer } );
 * var circle = L.circle( center, { renderer: myRenderer } );
 * ```
 */

var Canvas = Renderer.extend({
	getEvents: function () {
		var events = Renderer.prototype.getEvents.call(this);
		events.viewprereset = this._onViewPreReset;
		return events;
	},

	_onViewPreReset: function () {
		// Set a flag so that a viewprereset+moveend+viewreset only updates&redraws once
		this._postponeUpdatePaths = true;
	},

	onAdd: function () {
		Renderer.prototype.onAdd.call(this);

		// Redraw vectors since canvas is cleared upon removal,
		// in case of removing the renderer itself from the map.
		this._draw();
	},

	_initContainer: function () {
		var container = this._container = document.createElement('canvas');

		on(container, 'mousemove', throttle(this._onMouseMove, 32, this), this);
		on(container, 'click dblclick mousedown mouseup contextmenu', this._onClick, this);
		on(container, 'mouseout', this._handleMouseOut, this);

		this._ctx = container.getContext('2d');
	},

	_destroyContainer: function () {
		delete this._ctx;
		remove(this._container);
		off(this._container);
		delete this._container;
	},

	_updatePaths: function () {
		if (this._postponeUpdatePaths) { return; }

		var layer;
		this._redrawBounds = null;
		for (var id in this._layers) {
			layer = this._layers[id];
			layer._update();
		}
		this._redraw();
	},

	_update: function () {
		if (this._map._animatingZoom && this._bounds) { return; }

		this._drawnLayers = {};

		Renderer.prototype._update.call(this);

		var b = this._bounds,
		    container = this._container,
		    size = b.getSize(),
		    m = retina ? 2 : 1;

		setPosition(container, b.min);

		// set canvas size (also clearing it); use double size on retina
		container.width = m * size.x;
		container.height = m * size.y;
		container.style.width = size.x + 'px';
		container.style.height = size.y + 'px';

		if (retina) {
			this._ctx.scale(2, 2);
		}

		// translate so we use the same path coordinates after canvas element moves
		this._ctx.translate(-b.min.x, -b.min.y);

		// Tell paths to redraw themselves
		this.fire('update');
	},

	_reset: function () {
		Renderer.prototype._reset.call(this);

		if (this._postponeUpdatePaths) {
			this._postponeUpdatePaths = false;
			this._updatePaths();
		}
	},

	_initPath: function (layer) {
		this._updateDashArray(layer);
		this._layers[stamp(layer)] = layer;

		var order = layer._order = {
			layer: layer,
			prev: this._drawLast,
			next: null
		};
		if (this._drawLast) { this._drawLast.next = order; }
		this._drawLast = order;
		this._drawFirst = this._drawFirst || this._drawLast;
	},

	_addPath: function (layer) {
		this._requestRedraw(layer);
	},

	_removePath: function (layer) {
		var order = layer._order;
		var next = order.next;
		var prev = order.prev;

		if (next) {
			next.prev = prev;
		} else {
			this._drawLast = prev;
		}
		if (prev) {
			prev.next = next;
		} else {
			this._drawFirst = next;
		}

		delete layer._order;

		delete this._layers[L.stamp(layer)];

		this._requestRedraw(layer);
	},

	_updatePath: function (layer) {
		// Redraw the union of the layer's old pixel
		// bounds and the new pixel bounds.
		this._extendRedrawBounds(layer);
		layer._project();
		layer._update();
		// The redraw will extend the redraw bounds
		// with the new pixel bounds.
		this._requestRedraw(layer);
	},

	_updateStyle: function (layer) {
		this._updateDashArray(layer);
		this._requestRedraw(layer);
	},

	_updateDashArray: function (layer) {
		if (layer.options.dashArray) {
			var parts = layer.options.dashArray.split(','),
			    dashArray = [],
			    i;
			for (i = 0; i < parts.length; i++) {
				dashArray.push(Number(parts[i]));
			}
			layer.options._dashArray = dashArray;
		}
	},

	_requestRedraw: function (layer) {
		if (!this._map) { return; }

		this._extendRedrawBounds(layer);
		this._redrawRequest = this._redrawRequest || requestAnimFrame(this._redraw, this);
	},

	_extendRedrawBounds: function (layer) {
		if (layer._pxBounds) {
			var padding = (layer.options.weight || 0) + 1;
			this._redrawBounds = this._redrawBounds || new Bounds();
			this._redrawBounds.extend(layer._pxBounds.min.subtract([padding, padding]));
			this._redrawBounds.extend(layer._pxBounds.max.add([padding, padding]));
		}
	},

	_redraw: function () {
		this._redrawRequest = null;

		if (this._redrawBounds) {
			this._redrawBounds.min._floor();
			this._redrawBounds.max._ceil();
		}

		this._clear(); // clear layers in redraw bounds
		this._draw(); // draw layers

		this._redrawBounds = null;
	},

	_clear: function () {
		var bounds = this._redrawBounds;
		if (bounds) {
			var size = bounds.getSize();
			this._ctx.clearRect(bounds.min.x, bounds.min.y, size.x, size.y);
		} else {
			this._ctx.clearRect(0, 0, this._container.width, this._container.height);
		}
	},

	_draw: function () {
		var layer, bounds = this._redrawBounds;
		this._ctx.save();
		if (bounds) {
			var size = bounds.getSize();
			this._ctx.beginPath();
			this._ctx.rect(bounds.min.x, bounds.min.y, size.x, size.y);
			this._ctx.clip();
		}

		this._drawing = true;

		for (var order = this._drawFirst; order; order = order.next) {
			layer = order.layer;
			if (!bounds || (layer._pxBounds && layer._pxBounds.intersects(bounds))) {
				layer._updatePath();
			}
		}

		this._drawing = false;

		this._ctx.restore();  // Restore state before clipping.
	},

	_updatePoly: function (layer, closed) {
		if (!this._drawing) { return; }

		var i, j, len2, p,
		    parts = layer._parts,
		    len = parts.length,
		    ctx = this._ctx;

		if (!len) { return; }

		this._drawnLayers[layer._leaflet_id] = layer;

		ctx.beginPath();

		for (i = 0; i < len; i++) {
			for (j = 0, len2 = parts[i].length; j < len2; j++) {
				p = parts[i][j];
				ctx[j ? 'lineTo' : 'moveTo'](p.x, p.y);
			}
			if (closed) {
				ctx.closePath();
			}
		}

		this._fillStroke(ctx, layer);

		// TODO optimization: 1 fill/stroke for all features with equal style instead of 1 for each feature
	},

	_updateCircle: function (layer) {

		if (!this._drawing || layer._empty()) { return; }

		var p = layer._point,
		    ctx = this._ctx,
		    r = layer._radius,
		    s = (layer._radiusY || r) / r;

		this._drawnLayers[layer._leaflet_id] = layer;

		if (s !== 1) {
			ctx.save();
			ctx.scale(1, s);
		}

		ctx.beginPath();
		ctx.arc(p.x, p.y / s, r, 0, Math.PI * 2, false);

		if (s !== 1) {
			ctx.restore();
		}

		this._fillStroke(ctx, layer);
	},

	_fillStroke: function (ctx, layer) {
		var options = layer.options;

		if (options.fill) {
			ctx.globalAlpha = options.fillOpacity;
			ctx.fillStyle = options.fillColor || options.color;
			ctx.fill(options.fillRule || 'evenodd');
		}

		if (options.stroke && options.weight !== 0) {
			if (ctx.setLineDash) {
				ctx.setLineDash(layer.options && layer.options._dashArray || []);
			}
			ctx.globalAlpha = options.opacity;
			ctx.lineWidth = options.weight;
			ctx.strokeStyle = options.color;
			ctx.lineCap = options.lineCap;
			ctx.lineJoin = options.lineJoin;
			ctx.stroke();
		}
	},

	// Canvas obviously doesn't have mouse events for individual drawn objects,
	// so we emulate that by calculating what's under the mouse on mousemove/click manually

	_onClick: function (e) {
		var point = this._map.mouseEventToLayerPoint(e), layer, clickedLayer;

		for (var order = this._drawFirst; order; order = order.next) {
			layer = order.layer;
			if (layer.options.interactive && layer._containsPoint(point) && !this._map._draggableMoved(layer)) {
				clickedLayer = layer;
			}
		}
		if (clickedLayer)  {
			fakeStop(e);
			this._fireEvent([clickedLayer], e);
		}
	},

	_onMouseMove: function (e) {
		if (!this._map || this._map.dragging.moving() || this._map._animatingZoom) { return; }

		var point = this._map.mouseEventToLayerPoint(e);
		this._handleMouseHover(e, point);
	},


	_handleMouseOut: function (e) {
		var layer = this._hoveredLayer;
		if (layer) {
			// if we're leaving the layer, fire mouseout
			removeClass(this._container, 'leaflet-interactive');
			this._fireEvent([layer], e, 'mouseout');
			this._hoveredLayer = null;
		}
	},

	_handleMouseHover: function (e, point) {
		var layer, candidateHoveredLayer;

		for (var order = this._drawFirst; order; order = order.next) {
			layer = order.layer;
			if (layer.options.interactive && layer._containsPoint(point)) {
				candidateHoveredLayer = layer;
			}
		}

		if (candidateHoveredLayer !== this._hoveredLayer) {
			this._handleMouseOut(e);

			if (candidateHoveredLayer) {
				addClass(this._container, 'leaflet-interactive'); // change cursor
				this._fireEvent([candidateHoveredLayer], e, 'mouseover');
				this._hoveredLayer = candidateHoveredLayer;
			}
		}

		if (this._hoveredLayer) {
			this._fireEvent([this._hoveredLayer], e);
		}
	},

	_fireEvent: function (layers, e, type) {
		this._map._fireDOMEvent(e, type || e.type, layers);
	},

	_bringToFront: function (layer) {
		var order = layer._order;
		var next = order.next;
		var prev = order.prev;

		if (next) {
			next.prev = prev;
		} else {
			// Already last
			return;
		}
		if (prev) {
			prev.next = next;
		} else if (next) {
			// Update first entry unless this is the
			// signle entry
			this._drawFirst = next;
		}

		order.prev = this._drawLast;
		this._drawLast.next = order;

		order.next = null;
		this._drawLast = order;

		this._requestRedraw(layer);
	},

	_bringToBack: function (layer) {
		var order = layer._order;
		var next = order.next;
		var prev = order.prev;

		if (prev) {
			prev.next = next;
		} else {
			// Already first
			return;
		}
		if (next) {
			next.prev = prev;
		} else if (prev) {
			// Update last entry unless this is the
			// signle entry
			this._drawLast = prev;
		}

		order.prev = null;

		order.next = this._drawFirst;
		this._drawFirst.prev = order;
		this._drawFirst = order;

		this._requestRedraw(layer);
	}
});

// @factory L.canvas(options?: Renderer options)
// Creates a Canvas renderer with the given options.
function canvas$1(options) {
	return canvas ? new Canvas(options) : null;
}

/*
 * Thanks to Dmitry Baranovsky and his Raphael library for inspiration!
 */


var vmlCreate = (function () {
	try {
		document.namespaces.add('lvml', 'urn:schemas-microsoft-com:vml');
		return function (name) {
			return document.createElement('<lvml:' + name + ' class="lvml">');
		};
	} catch (e) {
		return function (name) {
			return document.createElement('<' + name + ' xmlns="urn:schemas-microsoft.com:vml" class="lvml">');
		};
	}
})();


/*
 * @class SVG
 *
 * Although SVG is not available on IE7 and IE8, these browsers support [VML](https://en.wikipedia.org/wiki/Vector_Markup_Language), and the SVG renderer will fall back to VML in this case.
 *
 * VML was deprecated in 2012, which means VML functionality exists only for backwards compatibility
 * with old versions of Internet Explorer.
 */

// mixin to redefine some SVG methods to handle VML syntax which is similar but with some differences
var vmlMixin = {

	_initContainer: function () {
		this._container = create$1('div', 'leaflet-vml-container');
	},

	_update: function () {
		if (this._map._animatingZoom) { return; }
		Renderer.prototype._update.call(this);
		this.fire('update');
	},

	_initPath: function (layer) {
		var container = layer._container = vmlCreate('shape');

		addClass(container, 'leaflet-vml-shape ' + (this.options.className || ''));

		container.coordsize = '1 1';

		layer._path = vmlCreate('path');
		container.appendChild(layer._path);

		this._updateStyle(layer);
		this._layers[stamp(layer)] = layer;
	},

	_addPath: function (layer) {
		var container = layer._container;
		this._container.appendChild(container);

		if (layer.options.interactive) {
			layer.addInteractiveTarget(container);
		}
	},

	_removePath: function (layer) {
		var container = layer._container;
		remove(container);
		layer.removeInteractiveTarget(container);
		delete this._layers[stamp(layer)];
	},

	_updateStyle: function (layer) {
		var stroke = layer._stroke,
		    fill = layer._fill,
		    options = layer.options,
		    container = layer._container;

		container.stroked = !!options.stroke;
		container.filled = !!options.fill;

		if (options.stroke) {
			if (!stroke) {
				stroke = layer._stroke = vmlCreate('stroke');
			}
			container.appendChild(stroke);
			stroke.weight = options.weight + 'px';
			stroke.color = options.color;
			stroke.opacity = options.opacity;

			if (options.dashArray) {
				stroke.dashStyle = isArray(options.dashArray) ?
				    options.dashArray.join(' ') :
				    options.dashArray.replace(/( *, *)/g, ' ');
			} else {
				stroke.dashStyle = '';
			}
			stroke.endcap = options.lineCap.replace('butt', 'flat');
			stroke.joinstyle = options.lineJoin;

		} else if (stroke) {
			container.removeChild(stroke);
			layer._stroke = null;
		}

		if (options.fill) {
			if (!fill) {
				fill = layer._fill = vmlCreate('fill');
			}
			container.appendChild(fill);
			fill.color = options.fillColor || options.color;
			fill.opacity = options.fillOpacity;

		} else if (fill) {
			container.removeChild(fill);
			layer._fill = null;
		}
	},

	_updateCircle: function (layer) {
		var p = layer._point.round(),
		    r = Math.round(layer._radius),
		    r2 = Math.round(layer._radiusY || r);

		this._setPath(layer, layer._empty() ? 'M0 0' :
				'AL ' + p.x + ',' + p.y + ' ' + r + ',' + r2 + ' 0,' + (65535 * 360));
	},

	_setPath: function (layer, path) {
		layer._path.v = path;
	},

	_bringToFront: function (layer) {
		toFront(layer._container);
	},

	_bringToBack: function (layer) {
		toBack(layer._container);
	}
};

var create$2 = vml ? vmlCreate : svgCreate;

/*
 * @class SVG
 * @inherits Renderer
 * @aka L.SVG
 *
 * Allows vector layers to be displayed with [SVG](https://developer.mozilla.org/docs/Web/SVG).
 * Inherits `Renderer`.
 *
 * Due to [technical limitations](http://caniuse.com/#search=svg), SVG is not
 * available in all web browsers, notably Android 2.x and 3.x.
 *
 * Although SVG is not available on IE7 and IE8, these browsers support
 * [VML](https://en.wikipedia.org/wiki/Vector_Markup_Language)
 * (a now deprecated technology), and the SVG renderer will fall back to VML in
 * this case.
 *
 * @example
 *
 * Use SVG by default for all paths in the map:
 *
 * ```js
 * var map = L.map('map', {
 * 	renderer: L.svg()
 * });
 * ```
 *
 * Use a SVG renderer with extra padding for specific vector geometries:
 *
 * ```js
 * var map = L.map('map');
 * var myRenderer = L.svg({ padding: 0.5 });
 * var line = L.polyline( coordinates, { renderer: myRenderer } );
 * var circle = L.circle( center, { renderer: myRenderer } );
 * ```
 */

var SVG = Renderer.extend({

	getEvents: function () {
		var events = Renderer.prototype.getEvents.call(this);
		events.zoomstart = this._onZoomStart;
		return events;
	},

	_initContainer: function () {
		this._container = create$2('svg');

		// makes it possible to click through svg root; we'll reset it back in individual paths
		this._container.setAttribute('pointer-events', 'none');

		this._rootGroup = create$2('g');
		this._container.appendChild(this._rootGroup);
	},

	_destroyContainer: function () {
		remove(this._container);
		off(this._container);
		delete this._container;
		delete this._rootGroup;
	},

	_onZoomStart: function () {
		// Drag-then-pinch interactions might mess up the center and zoom.
		// In this case, the easiest way to prevent this is re-do the renderer
		//   bounds and padding when the zooming starts.
		this._update();
	},

	_update: function () {
		if (this._map._animatingZoom && this._bounds) { return; }

		Renderer.prototype._update.call(this);

		var b = this._bounds,
		    size = b.getSize(),
		    container = this._container;

		// set size of svg-container if changed
		if (!this._svgSize || !this._svgSize.equals(size)) {
			this._svgSize = size;
			container.setAttribute('width', size.x);
			container.setAttribute('height', size.y);
		}

		// movement: update container viewBox so that we don't have to change coordinates of individual layers
		setPosition(container, b.min);
		container.setAttribute('viewBox', [b.min.x, b.min.y, size.x, size.y].join(' '));

		this.fire('update');
	},

	// methods below are called by vector layers implementations

	_initPath: function (layer) {
		var path = layer._path = create$2('path');

		// @namespace Path
		// @option className: String = null
		// Custom class name set on an element. Only for SVG renderer.
		if (layer.options.className) {
			addClass(path, layer.options.className);
		}

		if (layer.options.interactive) {
			addClass(path, 'leaflet-interactive');
		}

		this._updateStyle(layer);
		this._layers[stamp(layer)] = layer;
	},

	_addPath: function (layer) {
		if (!this._rootGroup) { this._initContainer(); }
		this._rootGroup.appendChild(layer._path);
		layer.addInteractiveTarget(layer._path);
	},

	_removePath: function (layer) {
		remove(layer._path);
		layer.removeInteractiveTarget(layer._path);
		delete this._layers[stamp(layer)];
	},

	_updatePath: function (layer) {
		layer._project();
		layer._update();
	},

	_updateStyle: function (layer) {
		var path = layer._path,
		    options = layer.options;

		if (!path) { return; }

		if (options.stroke) {
			path.setAttribute('stroke', options.color);
			path.setAttribute('stroke-opacity', options.opacity);
			path.setAttribute('stroke-width', options.weight);
			path.setAttribute('stroke-linecap', options.lineCap);
			path.setAttribute('stroke-linejoin', options.lineJoin);

			if (options.dashArray) {
				path.setAttribute('stroke-dasharray', options.dashArray);
			} else {
				path.removeAttribute('stroke-dasharray');
			}

			if (options.dashOffset) {
				path.setAttribute('stroke-dashoffset', options.dashOffset);
			} else {
				path.removeAttribute('stroke-dashoffset');
			}
		} else {
			path.setAttribute('stroke', 'none');
		}

		if (options.fill) {
			path.setAttribute('fill', options.fillColor || options.color);
			path.setAttribute('fill-opacity', options.fillOpacity);
			path.setAttribute('fill-rule', options.fillRule || 'evenodd');
		} else {
			path.setAttribute('fill', 'none');
		}
	},

	_updatePoly: function (layer, closed) {
		this._setPath(layer, pointsToPath(layer._parts, closed));
	},

	_updateCircle: function (layer) {
		var p = layer._point,
		    r = layer._radius,
		    r2 = layer._radiusY || r,
		    arc = 'a' + r + ',' + r2 + ' 0 1,0 ';

		// drawing a circle with two half-arcs
		var d = layer._empty() ? 'M0 0' :
				'M' + (p.x - r) + ',' + p.y +
				arc + (r * 2) + ',0 ' +
				arc + (-r * 2) + ',0 ';

		this._setPath(layer, d);
	},

	_setPath: function (layer, path) {
		layer._path.setAttribute('d', path);
	},

	// SVG does not have the concept of zIndex so we resort to changing the DOM order of elements
	_bringToFront: function (layer) {
		toFront(layer._path);
	},

	_bringToBack: function (layer) {
		toBack(layer._path);
	}
});

if (vml) {
	SVG.include(vmlMixin);
}

// @factory L.svg(options?: Renderer options)
// Creates a SVG renderer with the given options.
function svg$1(options) {
	return svg || vml ? new SVG(options) : null;
}

Map.include({
	// @namespace Map; @method getRenderer(layer: Path): Renderer
	// Returns the instance of `Renderer` that should be used to render the given
	// `Path`. It will ensure that the `renderer` options of the map and paths
	// are respected, and that the renderers do exist on the map.
	getRenderer: function (layer) {
		// @namespace Path; @option renderer: Renderer
		// Use this specific instance of `Renderer` for this path. Takes
		// precedence over the map's [default renderer](#map-renderer).
		var renderer = layer.options.renderer || this._getPaneRenderer(layer.options.pane) || this.options.renderer || this._renderer;

		if (!renderer) {
			// @namespace Map; @option preferCanvas: Boolean = false
			// Whether `Path`s should be rendered on a `Canvas` renderer.
			// By default, all `Path`s are rendered in a `SVG` renderer.
			renderer = this._renderer = (this.options.preferCanvas && canvas$1()) || svg$1();
		}

		if (!this.hasLayer(renderer)) {
			this.addLayer(renderer);
		}
		return renderer;
	},

	_getPaneRenderer: function (name) {
		if (name === 'overlayPane' || name === undefined) {
			return false;
		}

		var renderer = this._paneRenderers[name];
		if (renderer === undefined) {
			renderer = (SVG && svg$1({pane: name})) || (Canvas && canvas$1({pane: name}));
			this._paneRenderers[name] = renderer;
		}
		return renderer;
	}
});

/*
 * L.Rectangle extends Polygon and creates a rectangle when passed a LatLngBounds object.
 */

/*
 * @class Rectangle
 * @aka L.Retangle
 * @inherits Polygon
 *
 * A class for drawing rectangle overlays on a map. Extends `Polygon`.
 *
 * @example
 *
 * ```js
 * // define rectangle geographical bounds
 * var bounds = [[54.559322, -5.767822], [56.1210604, -3.021240]];
 *
 * // create an orange rectangle
 * L.rectangle(bounds, {color: "#ff7800", weight: 1}).addTo(map);
 *
 * // zoom the map to the rectangle bounds
 * map.fitBounds(bounds);
 * ```
 *
 */


var Rectangle = Polygon.extend({
	initialize: function (latLngBounds, options) {
		Polygon.prototype.initialize.call(this, this._boundsToLatLngs(latLngBounds), options);
	},

	// @method setBounds(latLngBounds: LatLngBounds): this
	// Redraws the rectangle with the passed bounds.
	setBounds: function (latLngBounds) {
		return this.setLatLngs(this._boundsToLatLngs(latLngBounds));
	},

	_boundsToLatLngs: function (latLngBounds) {
		latLngBounds = toLatLngBounds(latLngBounds);
		return [
			latLngBounds.getSouthWest(),
			latLngBounds.getNorthWest(),
			latLngBounds.getNorthEast(),
			latLngBounds.getSouthEast()
		];
	}
});


// @factory L.rectangle(latLngBounds: LatLngBounds, options?: Polyline options)
function rectangle(latLngBounds, options) {
	return new Rectangle(latLngBounds, options);
}

SVG.create = create$2;
SVG.pointsToPath = pointsToPath;

GeoJSON.geometryToLayer = geometryToLayer;
GeoJSON.coordsToLatLng = coordsToLatLng;
GeoJSON.coordsToLatLngs = coordsToLatLngs;
GeoJSON.latLngToCoords = latLngToCoords;
GeoJSON.latLngsToCoords = latLngsToCoords;
GeoJSON.getFeature = getFeature;
GeoJSON.asFeature = asFeature;

/*
 * L.Handler.BoxZoom is used to add shift-drag zoom interaction to the map
 * (zoom to a selected bounding box), enabled by default.
 */

// @namespace Map
// @section Interaction Options
Map.mergeOptions({
	// @option boxZoom: Boolean = true
	// Whether the map can be zoomed to a rectangular area specified by
	// dragging the mouse while pressing the shift key.
	boxZoom: true
});

var BoxZoom = Handler.extend({
	initialize: function (map) {
		this._map = map;
		this._container = map._container;
		this._pane = map._panes.overlayPane;
		this._resetStateTimeout = 0;
		map.on('unload', this._destroy, this);
	},

	addHooks: function () {
		on(this._container, 'mousedown', this._onMouseDown, this);
	},

	removeHooks: function () {
		off(this._container, 'mousedown', this._onMouseDown, this);
	},

	moved: function () {
		return this._moved;
	},

	_destroy: function () {
		remove(this._pane);
		delete this._pane;
	},

	_resetState: function () {
		this._resetStateTimeout = 0;
		this._moved = false;
	},

	_clearDeferredResetState: function () {
		if (this._resetStateTimeout !== 0) {
			clearTimeout(this._resetStateTimeout);
			this._resetStateTimeout = 0;
		}
	},

	_onMouseDown: function (e) {
		if (!e.shiftKey || ((e.which !== 1) && (e.button !== 1))) { return false; }

		// Clear the deferred resetState if it hasn't executed yet, otherwise it
		// will interrupt the interaction and orphan a box element in the container.
		this._clearDeferredResetState();
		this._resetState();

		disableTextSelection();
		disableImageDrag();

		this._startPoint = this._map.mouseEventToContainerPoint(e);

		on(document, {
			contextmenu: stop,
			mousemove: this._onMouseMove,
			mouseup: this._onMouseUp,
			keydown: this._onKeyDown
		}, this);
	},

	_onMouseMove: function (e) {
		if (!this._moved) {
			this._moved = true;

			this._box = create$1('div', 'leaflet-zoom-box', this._container);
			addClass(this._container, 'leaflet-crosshair');

			this._map.fire('boxzoomstart');
		}

		this._point = this._map.mouseEventToContainerPoint(e);

		var bounds = new Bounds(this._point, this._startPoint),
		    size = bounds.getSize();

		setPosition(this._box, bounds.min);

		this._box.style.width  = size.x + 'px';
		this._box.style.height = size.y + 'px';
	},

	_finish: function () {
		if (this._moved) {
			remove(this._box);
			removeClass(this._container, 'leaflet-crosshair');
		}

		enableTextSelection();
		enableImageDrag();

		off(document, {
			contextmenu: stop,
			mousemove: this._onMouseMove,
			mouseup: this._onMouseUp,
			keydown: this._onKeyDown
		}, this);
	},

	_onMouseUp: function (e) {
		if ((e.which !== 1) && (e.button !== 1)) { return; }

		this._finish();

		if (!this._moved) { return; }
		// Postpone to next JS tick so internal click event handling
		// still see it as "moved".
		this._clearDeferredResetState();
		this._resetStateTimeout = setTimeout(bind(this._resetState, this), 0);

		var bounds = new LatLngBounds(
		        this._map.containerPointToLatLng(this._startPoint),
		        this._map.containerPointToLatLng(this._point));

		this._map
			.fitBounds(bounds)
			.fire('boxzoomend', {boxZoomBounds: bounds});
	},

	_onKeyDown: function (e) {
		if (e.keyCode === 27) {
			this._finish();
		}
	}
});

// @section Handlers
// @property boxZoom: Handler
// Box (shift-drag with mouse) zoom handler.
Map.addInitHook('addHandler', 'boxZoom', BoxZoom);

/*
 * L.Handler.DoubleClickZoom is used to handle double-click zoom on the map, enabled by default.
 */

// @namespace Map
// @section Interaction Options

Map.mergeOptions({
	// @option doubleClickZoom: Boolean|String = true
	// Whether the map can be zoomed in by double clicking on it and
	// zoomed out by double clicking while holding shift. If passed
	// `'center'`, double-click zoom will zoom to the center of the
	//  view regardless of where the mouse was.
	doubleClickZoom: true
});

var DoubleClickZoom = Handler.extend({
	addHooks: function () {
		this._map.on('dblclick', this._onDoubleClick, this);
	},

	removeHooks: function () {
		this._map.off('dblclick', this._onDoubleClick, this);
	},

	_onDoubleClick: function (e) {
		var map = this._map,
		    oldZoom = map.getZoom(),
		    delta = map.options.zoomDelta,
		    zoom = e.originalEvent.shiftKey ? oldZoom - delta : oldZoom + delta;

		if (map.options.doubleClickZoom === 'center') {
			map.setZoom(zoom);
		} else {
			map.setZoomAround(e.containerPoint, zoom);
		}
	}
});

// @section Handlers
//
// Map properties include interaction handlers that allow you to control
// interaction behavior in runtime, enabling or disabling certain features such
// as dragging or touch zoom (see `Handler` methods). For example:
//
// ```js
// map.doubleClickZoom.disable();
// ```
//
// @property doubleClickZoom: Handler
// Double click zoom handler.
Map.addInitHook('addHandler', 'doubleClickZoom', DoubleClickZoom);

/*
 * L.Handler.MapDrag is used to make the map draggable (with panning inertia), enabled by default.
 */

// @namespace Map
// @section Interaction Options
Map.mergeOptions({
	// @option dragging: Boolean = true
	// Whether the map be draggable with mouse/touch or not.
	dragging: true,

	// @section Panning Inertia Options
	// @option inertia: Boolean = *
	// If enabled, panning of the map will have an inertia effect where
	// the map builds momentum while dragging and continues moving in
	// the same direction for some time. Feels especially nice on touch
	// devices. Enabled by default unless running on old Android devices.
	inertia: !android23,

	// @option inertiaDeceleration: Number = 3000
	// The rate with which the inertial movement slows down, in pixels/second².
	inertiaDeceleration: 3400, // px/s^2

	// @option inertiaMaxSpeed: Number = Infinity
	// Max speed of the inertial movement, in pixels/second.
	inertiaMaxSpeed: Infinity, // px/s

	// @option easeLinearity: Number = 0.2
	easeLinearity: 0.2,

	// TODO refactor, move to CRS
	// @option worldCopyJump: Boolean = false
	// With this option enabled, the map tracks when you pan to another "copy"
	// of the world and seamlessly jumps to the original one so that all overlays
	// like markers and vector layers are still visible.
	worldCopyJump: false,

	// @option maxBoundsViscosity: Number = 0.0
	// If `maxBounds` is set, this option will control how solid the bounds
	// are when dragging the map around. The default value of `0.0` allows the
	// user to drag outside the bounds at normal speed, higher values will
	// slow down map dragging outside bounds, and `1.0` makes the bounds fully
	// solid, preventing the user from dragging outside the bounds.
	maxBoundsViscosity: 0.0
});

var Drag = Handler.extend({
	addHooks: function () {
		if (!this._draggable) {
			var map = this._map;

			this._draggable = new Draggable(map._mapPane, map._container);

			this._draggable.on({
				dragstart: this._onDragStart,
				drag: this._onDrag,
				dragend: this._onDragEnd
			}, this);

			this._draggable.on('predrag', this._onPreDragLimit, this);
			if (map.options.worldCopyJump) {
				this._draggable.on('predrag', this._onPreDragWrap, this);
				map.on('zoomend', this._onZoomEnd, this);

				map.whenReady(this._onZoomEnd, this);
			}
		}
		addClass(this._map._container, 'leaflet-grab leaflet-touch-drag');
		this._draggable.enable();
		this._positions = [];
		this._times = [];
	},

	removeHooks: function () {
		removeClass(this._map._container, 'leaflet-grab');
		removeClass(this._map._container, 'leaflet-touch-drag');
		this._draggable.disable();
	},

	moved: function () {
		return this._draggable && this._draggable._moved;
	},

	moving: function () {
		return this._draggable && this._draggable._moving;
	},

	_onDragStart: function () {
		var map = this._map;

		map._stop();
		if (this._map.options.maxBounds && this._map.options.maxBoundsViscosity) {
			var bounds = toLatLngBounds(this._map.options.maxBounds);

			this._offsetLimit = toBounds(
				this._map.latLngToContainerPoint(bounds.getNorthWest()).multiplyBy(-1),
				this._map.latLngToContainerPoint(bounds.getSouthEast()).multiplyBy(-1)
					.add(this._map.getSize()));

			this._viscosity = Math.min(1.0, Math.max(0.0, this._map.options.maxBoundsViscosity));
		} else {
			this._offsetLimit = null;
		}

		map
		    .fire('movestart')
		    .fire('dragstart');

		if (map.options.inertia) {
			this._positions = [];
			this._times = [];
		}
	},

	_onDrag: function (e) {
		if (this._map.options.inertia) {
			var time = this._lastTime = +new Date(),
			    pos = this._lastPos = this._draggable._absPos || this._draggable._newPos;

			this._positions.push(pos);
			this._times.push(time);

			if (time - this._times[0] > 50) {
				this._positions.shift();
				this._times.shift();
			}
		}

		this._map
		    .fire('move', e)
		    .fire('drag', e);
	},

	_onZoomEnd: function () {
		var pxCenter = this._map.getSize().divideBy(2),
		    pxWorldCenter = this._map.latLngToLayerPoint([0, 0]);

		this._initialWorldOffset = pxWorldCenter.subtract(pxCenter).x;
		this._worldWidth = this._map.getPixelWorldBounds().getSize().x;
	},

	_viscousLimit: function (value, threshold) {
		return value - (value - threshold) * this._viscosity;
	},

	_onPreDragLimit: function () {
		if (!this._viscosity || !this._offsetLimit) { return; }

		var offset = this._draggable._newPos.subtract(this._draggable._startPos);

		var limit = this._offsetLimit;
		if (offset.x < limit.min.x) { offset.x = this._viscousLimit(offset.x, limit.min.x); }
		if (offset.y < limit.min.y) { offset.y = this._viscousLimit(offset.y, limit.min.y); }
		if (offset.x > limit.max.x) { offset.x = this._viscousLimit(offset.x, limit.max.x); }
		if (offset.y > limit.max.y) { offset.y = this._viscousLimit(offset.y, limit.max.y); }

		this._draggable._newPos = this._draggable._startPos.add(offset);
	},

	_onPreDragWrap: function () {
		// TODO refactor to be able to adjust map pane position after zoom
		var worldWidth = this._worldWidth,
		    halfWidth = Math.round(worldWidth / 2),
		    dx = this._initialWorldOffset,
		    x = this._draggable._newPos.x,
		    newX1 = (x - halfWidth + dx) % worldWidth + halfWidth - dx,
		    newX2 = (x + halfWidth + dx) % worldWidth - halfWidth - dx,
		    newX = Math.abs(newX1 + dx) < Math.abs(newX2 + dx) ? newX1 : newX2;

		this._draggable._absPos = this._draggable._newPos.clone();
		this._draggable._newPos.x = newX;
	},

	_onDragEnd: function (e) {
		var map = this._map,
		    options = map.options,

		    noInertia = !options.inertia || this._times.length < 2;

		map.fire('dragend', e);

		if (noInertia) {
			map.fire('moveend');

		} else {

			var direction = this._lastPos.subtract(this._positions[0]),
			    duration = (this._lastTime - this._times[0]) / 1000,
			    ease = options.easeLinearity,

			    speedVector = direction.multiplyBy(ease / duration),
			    speed = speedVector.distanceTo([0, 0]),

			    limitedSpeed = Math.min(options.inertiaMaxSpeed, speed),
			    limitedSpeedVector = speedVector.multiplyBy(limitedSpeed / speed),

			    decelerationDuration = limitedSpeed / (options.inertiaDeceleration * ease),
			    offset = limitedSpeedVector.multiplyBy(-decelerationDuration / 2).round();

			if (!offset.x && !offset.y) {
				map.fire('moveend');

			} else {
				offset = map._limitOffset(offset, map.options.maxBounds);

				requestAnimFrame(function () {
					map.panBy(offset, {
						duration: decelerationDuration,
						easeLinearity: ease,
						noMoveStart: true,
						animate: true
					});
				});
			}
		}
	}
});

// @section Handlers
// @property dragging: Handler
// Map dragging handler (by both mouse and touch).
Map.addInitHook('addHandler', 'dragging', Drag);

/*
 * L.Map.Keyboard is handling keyboard interaction with the map, enabled by default.
 */

// @namespace Map
// @section Keyboard Navigation Options
Map.mergeOptions({
	// @option keyboard: Boolean = true
	// Makes the map focusable and allows users to navigate the map with keyboard
	// arrows and `+`/`-` keys.
	keyboard: true,

	// @option keyboardPanDelta: Number = 80
	// Amount of pixels to pan when pressing an arrow key.
	keyboardPanDelta: 80
});

var Keyboard = Handler.extend({

	keyCodes: {
		left:    [37],
		right:   [39],
		down:    [40],
		up:      [38],
		zoomIn:  [187, 107, 61, 171],
		zoomOut: [189, 109, 54, 173]
	},

	initialize: function (map) {
		this._map = map;

		this._setPanDelta(map.options.keyboardPanDelta);
		this._setZoomDelta(map.options.zoomDelta);
	},

	addHooks: function () {
		var container = this._map._container;

		// make the container focusable by tabbing
		if (container.tabIndex <= 0) {
			container.tabIndex = '0';
		}

		on(container, {
			focus: this._onFocus,
			blur: this._onBlur,
			mousedown: this._onMouseDown
		}, this);

		this._map.on({
			focus: this._addHooks,
			blur: this._removeHooks
		}, this);
	},

	removeHooks: function () {
		this._removeHooks();

		off(this._map._container, {
			focus: this._onFocus,
			blur: this._onBlur,
			mousedown: this._onMouseDown
		}, this);

		this._map.off({
			focus: this._addHooks,
			blur: this._removeHooks
		}, this);
	},

	_onMouseDown: function () {
		if (this._focused) { return; }

		var body = document.body,
		    docEl = document.documentElement,
		    top = body.scrollTop || docEl.scrollTop,
		    left = body.scrollLeft || docEl.scrollLeft;

		this._map._container.focus();

		window.scrollTo(left, top);
	},

	_onFocus: function () {
		this._focused = true;
		this._map.fire('focus');
	},

	_onBlur: function () {
		this._focused = false;
		this._map.fire('blur');
	},

	_setPanDelta: function (panDelta) {
		var keys = this._panKeys = {},
		    codes = this.keyCodes,
		    i, len;

		for (i = 0, len = codes.left.length; i < len; i++) {
			keys[codes.left[i]] = [-1 * panDelta, 0];
		}
		for (i = 0, len = codes.right.length; i < len; i++) {
			keys[codes.right[i]] = [panDelta, 0];
		}
		for (i = 0, len = codes.down.length; i < len; i++) {
			keys[codes.down[i]] = [0, panDelta];
		}
		for (i = 0, len = codes.up.length; i < len; i++) {
			keys[codes.up[i]] = [0, -1 * panDelta];
		}
	},

	_setZoomDelta: function (zoomDelta) {
		var keys = this._zoomKeys = {},
		    codes = this.keyCodes,
		    i, len;

		for (i = 0, len = codes.zoomIn.length; i < len; i++) {
			keys[codes.zoomIn[i]] = zoomDelta;
		}
		for (i = 0, len = codes.zoomOut.length; i < len; i++) {
			keys[codes.zoomOut[i]] = -zoomDelta;
		}
	},

	_addHooks: function () {
		on(document, 'keydown', this._onKeyDown, this);
	},

	_removeHooks: function () {
		off(document, 'keydown', this._onKeyDown, this);
	},

	_onKeyDown: function (e) {
		if (e.altKey || e.ctrlKey || e.metaKey) { return; }

		var key = e.keyCode,
		    map = this._map,
		    offset;

		if (key in this._panKeys) {

			if (map._panAnim && map._panAnim._inProgress) { return; }

			offset = this._panKeys[key];
			if (e.shiftKey) {
				offset = toPoint(offset).multiplyBy(3);
			}

			map.panBy(offset);

			if (map.options.maxBounds) {
				map.panInsideBounds(map.options.maxBounds);
			}

		} else if (key in this._zoomKeys) {
			map.setZoom(map.getZoom() + (e.shiftKey ? 3 : 1) * this._zoomKeys[key]);

		} else if (key === 27 && map._popup) {
			map.closePopup();

		} else {
			return;
		}

		stop(e);
	}
});

// @section Handlers
// @section Handlers
// @property keyboard: Handler
// Keyboard navigation handler.
Map.addInitHook('addHandler', 'keyboard', Keyboard);

/*
 * L.Handler.ScrollWheelZoom is used by L.Map to enable mouse scroll wheel zoom on the map.
 */

// @namespace Map
// @section Interaction Options
Map.mergeOptions({
	// @section Mousewheel options
	// @option scrollWheelZoom: Boolean|String = true
	// Whether the map can be zoomed by using the mouse wheel. If passed `'center'`,
	// it will zoom to the center of the view regardless of where the mouse was.
	scrollWheelZoom: true,

	// @option wheelDebounceTime: Number = 40
	// Limits the rate at which a wheel can fire (in milliseconds). By default
	// user can't zoom via wheel more often than once per 40 ms.
	wheelDebounceTime: 40,

	// @option wheelPxPerZoomLevel: Number = 60
	// How many scroll pixels (as reported by [L.DomEvent.getWheelDelta](#domevent-getwheeldelta))
	// mean a change of one full zoom level. Smaller values will make wheel-zooming
	// faster (and vice versa).
	wheelPxPerZoomLevel: 60
});

var ScrollWheelZoom = Handler.extend({
	addHooks: function () {
		on(this._map._container, 'mousewheel', this._onWheelScroll, this);

		this._delta = 0;
	},

	removeHooks: function () {
		off(this._map._container, 'mousewheel', this._onWheelScroll, this);
	},

	_onWheelScroll: function (e) {
		var delta = getWheelDelta(e);

		var debounce = this._map.options.wheelDebounceTime;

		this._delta += delta;
		this._lastMousePos = this._map.mouseEventToContainerPoint(e);

		if (!this._startTime) {
			this._startTime = +new Date();
		}

		var left = Math.max(debounce - (+new Date() - this._startTime), 0);

		clearTimeout(this._timer);
		this._timer = setTimeout(bind(this._performZoom, this), left);

		stop(e);
	},

	_performZoom: function () {
		var map = this._map,
		    zoom = map.getZoom(),
		    snap = this._map.options.zoomSnap || 0;

		map._stop(); // stop panning and fly animations if any

		// map the delta with a sigmoid function to -4..4 range leaning on -1..1
		var d2 = this._delta / (this._map.options.wheelPxPerZoomLevel * 4),
		    d3 = 4 * Math.log(2 / (1 + Math.exp(-Math.abs(d2)))) / Math.LN2,
		    d4 = snap ? Math.ceil(d3 / snap) * snap : d3,
		    delta = map._limitZoom(zoom + (this._delta > 0 ? d4 : -d4)) - zoom;

		this._delta = 0;
		this._startTime = null;

		if (!delta) { return; }

		if (map.options.scrollWheelZoom === 'center') {
			map.setZoom(zoom + delta);
		} else {
			map.setZoomAround(this._lastMousePos, zoom + delta);
		}
	}
});

// @section Handlers
// @property scrollWheelZoom: Handler
// Scroll wheel zoom handler.
Map.addInitHook('addHandler', 'scrollWheelZoom', ScrollWheelZoom);

/*
 * L.Map.Tap is used to enable mobile hacks like quick taps and long hold.
 */

// @namespace Map
// @section Interaction Options
Map.mergeOptions({
	// @section Touch interaction options
	// @option tap: Boolean = true
	// Enables mobile hacks for supporting instant taps (fixing 200ms click
	// delay on iOS/Android) and touch holds (fired as `contextmenu` events).
	tap: true,

	// @option tapTolerance: Number = 15
	// The max number of pixels a user can shift his finger during touch
	// for it to be considered a valid tap.
	tapTolerance: 15
});

var Tap = Handler.extend({
	addHooks: function () {
		on(this._map._container, 'touchstart', this._onDown, this);
	},

	removeHooks: function () {
		off(this._map._container, 'touchstart', this._onDown, this);
	},

	_onDown: function (e) {
		if (!e.touches) { return; }

		preventDefault(e);

		this._fireClick = true;

		// don't simulate click or track longpress if more than 1 touch
		if (e.touches.length > 1) {
			this._fireClick = false;
			clearTimeout(this._holdTimeout);
			return;
		}

		var first = e.touches[0],
		    el = first.target;

		this._startPos = this._newPos = new Point(first.clientX, first.clientY);

		// if touching a link, highlight it
		if (el.tagName && el.tagName.toLowerCase() === 'a') {
			addClass(el, 'leaflet-active');
		}

		// simulate long hold but setting a timeout
		this._holdTimeout = setTimeout(bind(function () {
			if (this._isTapValid()) {
				this._fireClick = false;
				this._onUp();
				this._simulateEvent('contextmenu', first);
			}
		}, this), 1000);

		this._simulateEvent('mousedown', first);

		on(document, {
			touchmove: this._onMove,
			touchend: this._onUp
		}, this);
	},

	_onUp: function (e) {
		clearTimeout(this._holdTimeout);

		off(document, {
			touchmove: this._onMove,
			touchend: this._onUp
		}, this);

		if (this._fireClick && e && e.changedTouches) {

			var first = e.changedTouches[0],
			    el = first.target;

			if (el && el.tagName && el.tagName.toLowerCase() === 'a') {
				removeClass(el, 'leaflet-active');
			}

			this._simulateEvent('mouseup', first);

			// simulate click if the touch didn't move too much
			if (this._isTapValid()) {
				this._simulateEvent('click', first);
			}
		}
	},

	_isTapValid: function () {
		return this._newPos.distanceTo(this._startPos) <= this._map.options.tapTolerance;
	},

	_onMove: function (e) {
		var first = e.touches[0];
		this._newPos = new Point(first.clientX, first.clientY);
		this._simulateEvent('mousemove', first);
	},

	_simulateEvent: function (type, e) {
		var simulatedEvent = document.createEvent('MouseEvents');

		simulatedEvent._simulated = true;
		e.target._simulatedClick = true;

		simulatedEvent.initMouseEvent(
		        type, true, true, window, 1,
		        e.screenX, e.screenY,
		        e.clientX, e.clientY,
		        false, false, false, false, 0, null);

		e.target.dispatchEvent(simulatedEvent);
	}
});

// @section Handlers
// @property tap: Handler
// Mobile touch hacks (quick tap and touch hold) handler.
if (touch && !pointer) {
	Map.addInitHook('addHandler', 'tap', Tap);
}

/*
 * L.Handler.TouchZoom is used by L.Map to add pinch zoom on supported mobile browsers.
 */

// @namespace Map
// @section Interaction Options
Map.mergeOptions({
	// @section Touch interaction options
	// @option touchZoom: Boolean|String = *
	// Whether the map can be zoomed by touch-dragging with two fingers. If
	// passed `'center'`, it will zoom to the center of the view regardless of
	// where the touch events (fingers) were. Enabled for touch-capable web
	// browsers except for old Androids.
	touchZoom: touch && !android23,

	// @option bounceAtZoomLimits: Boolean = true
	// Set it to false if you don't want the map to zoom beyond min/max zoom
	// and then bounce back when pinch-zooming.
	bounceAtZoomLimits: true
});

var TouchZoom = Handler.extend({
	addHooks: function () {
		addClass(this._map._container, 'leaflet-touch-zoom');
		on(this._map._container, 'touchstart', this._onTouchStart, this);
	},

	removeHooks: function () {
		removeClass(this._map._container, 'leaflet-touch-zoom');
		off(this._map._container, 'touchstart', this._onTouchStart, this);
	},

	_onTouchStart: function (e) {
		var map = this._map;
		if (!e.touches || e.touches.length !== 2 || map._animatingZoom || this._zooming) { return; }

		var p1 = map.mouseEventToContainerPoint(e.touches[0]),
		    p2 = map.mouseEventToContainerPoint(e.touches[1]);

		this._centerPoint = map.getSize()._divideBy(2);
		this._startLatLng = map.containerPointToLatLng(this._centerPoint);
		if (map.options.touchZoom !== 'center') {
			this._pinchStartLatLng = map.containerPointToLatLng(p1.add(p2)._divideBy(2));
		}

		this._startDist = p1.distanceTo(p2);
		this._startZoom = map.getZoom();

		this._moved = false;
		this._zooming = true;

		map._stop();

		on(document, 'touchmove', this._onTouchMove, this);
		on(document, 'touchend', this._onTouchEnd, this);

		preventDefault(e);
	},

	_onTouchMove: function (e) {
		if (!e.touches || e.touches.length !== 2 || !this._zooming) { return; }

		var map = this._map,
		    p1 = map.mouseEventToContainerPoint(e.touches[0]),
		    p2 = map.mouseEventToContainerPoint(e.touches[1]),
		    scale = p1.distanceTo(p2) / this._startDist;

		this._zoom = map.getScaleZoom(scale, this._startZoom);

		if (!map.options.bounceAtZoomLimits && (
			(this._zoom < map.getMinZoom() && scale < 1) ||
			(this._zoom > map.getMaxZoom() && scale > 1))) {
			this._zoom = map._limitZoom(this._zoom);
		}

		if (map.options.touchZoom === 'center') {
			this._center = this._startLatLng;
			if (scale === 1) { return; }
		} else {
			// Get delta from pinch to center, so centerLatLng is delta applied to initial pinchLatLng
			var delta = p1._add(p2)._divideBy(2)._subtract(this._centerPoint);
			if (scale === 1 && delta.x === 0 && delta.y === 0) { return; }
			this._center = map.unproject(map.project(this._pinchStartLatLng, this._zoom).subtract(delta), this._zoom);
		}

		if (!this._moved) {
			map._moveStart(true);
			this._moved = true;
		}

		cancelAnimFrame(this._animRequest);

		var moveFn = bind(map._move, map, this._center, this._zoom, {pinch: true, round: false});
		this._animRequest = requestAnimFrame(moveFn, this, true);

		preventDefault(e);
	},

	_onTouchEnd: function () {
		if (!this._moved || !this._zooming) {
			this._zooming = false;
			return;
		}

		this._zooming = false;
		cancelAnimFrame(this._animRequest);

		off(document, 'touchmove', this._onTouchMove);
		off(document, 'touchend', this._onTouchEnd);

		// Pinch updates GridLayers' levels only when zoomSnap is off, so zoomSnap becomes noUpdate.
		if (this._map.options.zoomAnimation) {
			this._map._animateZoom(this._center, this._map._limitZoom(this._zoom), true, this._map.options.zoomSnap);
		} else {
			this._map._resetView(this._center, this._map._limitZoom(this._zoom));
		}
	}
});

// @section Handlers
// @property touchZoom: Handler
// Touch zoom handler.
Map.addInitHook('addHandler', 'touchZoom', TouchZoom);

Map.BoxZoom = BoxZoom;
Map.DoubleClickZoom = DoubleClickZoom;
Map.Drag = Drag;
Map.Keyboard = Keyboard;
Map.ScrollWheelZoom = ScrollWheelZoom;
Map.Tap = Tap;
Map.TouchZoom = TouchZoom;

// misc

var oldL = window.L;
function noConflict() {
	window.L = oldL;
	return this;
}

// Always export us to window global (see #2364)
window.L = exports;

Object.freeze = freeze;

exports.version = version;
exports.noConflict = noConflict;
exports.Control = Control;
exports.control = control;
exports.Browser = Browser;
exports.Evented = Evented;
exports.Mixin = Mixin;
exports.Util = Util;
exports.Class = Class;
exports.Handler = Handler;
exports.extend = extend;
exports.bind = bind;
exports.stamp = stamp;
exports.setOptions = setOptions;
exports.DomEvent = DomEvent;
exports.DomUtil = DomUtil;
exports.PosAnimation = PosAnimation;
exports.Draggable = Draggable;
exports.LineUtil = LineUtil;
exports.PolyUtil = PolyUtil;
exports.Point = Point;
exports.point = toPoint;
exports.Bounds = Bounds;
exports.bounds = toBounds;
exports.Transformation = Transformation;
exports.transformation = toTransformation;
exports.Projection = index;
exports.LatLng = LatLng;
exports.latLng = toLatLng;
exports.LatLngBounds = LatLngBounds;
exports.latLngBounds = toLatLngBounds;
exports.CRS = CRS;
exports.GeoJSON = GeoJSON;
exports.geoJSON = geoJSON;
exports.geoJson = geoJson;
exports.Layer = Layer;
exports.LayerGroup = LayerGroup;
exports.layerGroup = layerGroup;
exports.FeatureGroup = FeatureGroup;
exports.featureGroup = featureGroup;
exports.ImageOverlay = ImageOverlay;
exports.imageOverlay = imageOverlay;
exports.VideoOverlay = VideoOverlay;
exports.videoOverlay = videoOverlay;
exports.DivOverlay = DivOverlay;
exports.Popup = Popup;
exports.popup = popup;
exports.Tooltip = Tooltip;
exports.tooltip = tooltip;
exports.Icon = Icon;
exports.icon = icon;
exports.DivIcon = DivIcon;
exports.divIcon = divIcon;
exports.Marker = Marker;
exports.marker = marker;
exports.TileLayer = TileLayer;
exports.tileLayer = tileLayer;
exports.GridLayer = GridLayer;
exports.gridLayer = gridLayer;
exports.SVG = SVG;
exports.svg = svg$1;
exports.Renderer = Renderer;
exports.Canvas = Canvas;
exports.canvas = canvas$1;
exports.Path = Path;
exports.CircleMarker = CircleMarker;
exports.circleMarker = circleMarker;
exports.Circle = Circle;
exports.circle = circle;
exports.Polyline = Polyline;
exports.polyline = polyline;
exports.Polygon = Polygon;
exports.polygon = polygon;
exports.Rectangle = Rectangle;
exports.rectangle = rectangle;
exports.Map = Map;
exports.map = createMap;

})));
//# sourceMappingURL=leaflet-src.js.map
   /* Ende leaflet_bibliothek */
   /* Start leitfaehigkeitsmessnetz_workflow */
   /**
 * Diese Datei beeinhaltet den kompletten Workflow von Leaflet, die zur Darstellung des Leitfähigkeitsmessnetzes
 * auf der Portalseite >>Portaltideelbe.de<< notwendig sind. Dieses Framework untersteht der BSD-Lizenz.
 * Leaflet ist ein freie JavaScript Bibliothek, mit der WebGis-Anwendungen erstellt werden können.
 * Aus einer Reihe von Kartenanbieter, ist man in der Lage über eine Schnittstelle, diese auf seiner
 * eigenen Seite darzustellen. Openstreemap ist positiv anzuraten, da die Nutzung kostenlos ist und sonst
 * keine besonderen Bedingungen in der Lizenz vorgegeben sind, außer, dass auf der Mappe angegeben wird, welchen Anbieter hier genutzt wird.
 *
 *
 * @author      D.C.
 * @created     A.O.
 * @version     1.0
 * @since      	1.2.0
 */
// Variable der Leaflet-Karten-Mappe
var mapLeitfaehigkeitsmessnetz = null;
// Initialisierungswert beim ersten Start vom Leitfähigkeitsmessnetz
var startWertLeitfahigketismessnetz = 0; 
function initialisiereLeitfaehigkeitsmessnetz(fromBrowser){
    // Prüfe, ob die ID >>leitfaehigkeitsmessnetz<< vorhanden ist
    if($('#leitfaehigkeitsmessnetz').length !== 0){
        ///////////////////////Korrektur der Ebenen für GSB Navigation ////////////////
        // Mappe
        $('#leitfaehigkeitsmessnetz').css('zIndex', '999');
        // Legende / Infotafel / Infobox
        $('.legendBox').css('zIndex', '999');
        ///////////////////////Ende Korrektur der Ebenen für GSB Navigation ///////////
        ////////////////////// Globale Variablen //////////////////////////////////////
        //Pufferwert für den Grenzbereich des Wertes (oranges Symbol)
        var puffer = 360;
        // Array für die einzelnen Marker
        var markersArray = [];
        startWertLeitfahigketismessnetz = fromBrowser;
        ////////////////////// Ende Globale Variablen ////////////////////////////////
        ////////////////////// Modalbox (Steckbrief) /////////////////////////////////
        // Erstelle das Modalobjekt
        var modal = document.getElementById('modalBoxSteckbrief');
        // Erstelle ein Objekt auf das '<span>' Element, das die Modalbox schließt
        var span = document.getElementsByClassName("close")[0];
        // Auf das Objekt 'span' ein Event gesetzt, das beim Klick auf das >>X<< die Modalbox schließt
        span.onclick = function () {
	        modal.style.display = "none";
        };
        // Auch außerhalb der Modalbox ist ein Event aktiviert, das die Modalbox schließt, wenn der Benutzer außerhalb der Modalbox irgendwo klickt
        window.onclick = function (event) {
	        if (event.target == modal) {
		    modal.style.display = "none";
	        } 
        }; 
        ////////////////////// Ende Modalbox (Steckbrief) ////////////////////////////
        ////////////////////// Kartenausschnitt //////////////////////////////////////
        // Karte initialisieren und Kartenzentrum definieren, Doppelklick auf der Karte deaktiviert (Zoomfunktion)
        if(mapLeitfaehigkeitsmessnetz !== null){
            mapLeitfaehigkeitsmessnetz.remove();
        }
        // Kartenmappe initialisieren
        mapLeitfaehigkeitsmessnetz = L.map('leitfaehigkeitsmessnetz', {doubleClickZoom : false}).setView([53.68800, 9.32250], 10); //[53.6201, 9.29577]
        // Openstreetmapkarte laden
        L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
        	crs: L.CRS.EPSG25832
        }).addTo(mapLeitfaehigkeitsmessnetz);
        ////////////////////// Ende Kartenausschnitt //////////////////////////////////
        ////////////////////// Anfang Objekt für Leaflet Sprite-Image /////////////////
        // Roter Kreis
        var roterKreis = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-red-button', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var roterKreisPfeilHoch = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-red-arrow-up', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var roterKreisPfeilGleich = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-red-arrow-qual',iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var roterKreisPfeilUnten = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-red-arrow-down', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        // Orangener Kreis
        var orangenerKreis = L.divIcon({className: 'leaflet-sprite-image leaflet-orange-button', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var orangenerKreisPfeilHoch = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-orange-arrow-up', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var orangenerKreisPfeilGleich = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-orange-arrow-equal', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var orangenerKreisPfeilUnten = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-orange-arrow-down', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        //Grüner Kreis
        var gruenerKreis = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-green-button', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var gruenerKreisPfeilHoch = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-green-arrow-up', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var gruenerKreisPfeilGleich = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-green-arrow-equal', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        var gruenerKreisPfeilUnten = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-green-arrow-down', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        //Dauermessstation
        var dauerMessStation = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-continuous-measurement', iconSize: [25, 25], iconAnchor: [1, 24], popupAnchor: [0, 200]});
        //Wartung
        var wartungsArbeiten = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-maintenance', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]}); 
        //Offline
        var stationOffline = L.divIcon({className: 'leaflet-sprite-image leaflet-icon-offline', iconSize: [25, 25], iconAnchor: [12, 12], popupAnchor: [0, 200]});
        ////////////////////// Ende Objekt für Leaflet Sprite-Image ///////////////////
        ////////////////////// Funktion für Auswertung der Pegelstationen für das Leitfähigkeitsmessnetz
        var fullPathName = window.location.pathname;
        var partOfPathName = fullPathName.substring(0,fullPathName.lastIndexOf('/'));
        var url_praefix_relativer_pfad = partOfPathName + "/"; // Am Ende immer einen Slash, am Anfang nicht
        // Array mit allen anzuzeigenden Pegelstationen
        var pegelStationen = [
            {
                // Abbenfleth Sperrwerk
                arrayElement: 'Abbenfleth_Sperrwerk', 
                positionMarker : [53.66896299867038, 9.494306874416917], 
                titelDerStation : 'Abbenfleth Sperrwerk', 
                direction : 'left', 
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'abbenfleth_sperrwerk/abbenfleth_sperrwerk_node',
                uuid : '28ec91e8-90c0-44d1-8fd2-b0b64c00c43b',
                dstelle: false,
                wartung: false
            },
            {
                // Achthöfener Fleth Siel
                arrayElement: 'Achthoefener_Fleth_Siel',
                positionMarker : [53.701580829948504, 9.163392656155676],
                titelDerStation : 'Achthöfener Fleth Siel', 
                direction : 'left',
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad +'achthoefener_fleth_siel/achthoefener_fleth_siel_node',
                uuid : '86d07669-4670-48a1-8f30-7d7b26224ad6',
                dstelle: false,
                wartung: false
            },
            {
                // Dornbusch Brücke
                arrayElement: 'Dornbusch_Bruecke',
                positionMarker : [53.737785, 9.347929],
                titelDerStation : 'Dornbusch Brücke',
                direction : 'top',
                offset : L.point(0, -12),
                urlSteckbrief : url_praefix_relativer_pfad + 'dornbusch_bruecke/dornbusch_bruecke_node',
                uuid : '5b94e4b9-cc96-433f-abf7-08287137ec54',
                dstelle: false,
                wartung: false
            },
            {
                // Este Inn. Sperrwerk
                arrayElement: 'Este_Inn_Sperrwerk',
                positionMarker : [53.53302651845584, 9.776735847843426],
                titelDerStation : 'Este Inn. Sperrwerk',
                direction : 'bottom',
                offset : L.point(0, 12),
                urlSteckbrief : url_praefix_relativer_pfad + 'este_inn_sperrwerk/este_inn_sperrwerk_node',
                uuid : '227b83f7-1302-4d7e-8d70-899036ff4ce2',
                dstelle: false,
                wartung: false
            },
            {
                // Freiburg Sperrwerk
                arrayElement: 'Freiburg_Sperrwerk',
                positionMarker : [53.8269255247804, 9.295079440408715],
                titelDerStation : 'Freiburg Sperrwerk',
                direction : 'left',
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'freiburg_sperrwerk/freiburg_sperrwerk_node',
                uuid : 'd3e21e8a-99ce-4033-8db6-9d0f0648beb3',
                dstelle: false,
                wartung: false
            },
            {
                // Geversdorf Brücke
                arrayElement: 'Geversdorf_Bruecke',
                positionMarker : [53.80126936, 9.08051349],
                titelDerStation : 'Geversdorf Brücke',
                direction : 'left',
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'geversdorf_bruecke/geversdorf_bruecke_node',
                uuid : 'f44e7220-892e-40b5-8ef4-3da129c2a6ae',
                dstelle: false,
                wartung: false
            },
            {
                // Hahnöfer Sand West Siel
                arrayElement: 'Hahnoefer_Sand_West_Siel',
                positionMarker : [53.54823793633717, 9.690490444910013],
                titelDerStation : 'Hahnöfer Sand West Siel',
                direction : 'left',
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'hahnoefer_sand_west_siel/hahnoefer_sand_west_siel_node',
                uuid : '81c95d3a-3193-4e9c-a411-adf97c751a43',
                dstelle: false,
                wartung: false
            },
            {
                // Krückau Sperrwerk BP
                arrayElement: 'Krueckau_Sperrwerk_BP',
                positionMarker : [53.71640017, 9.52666946],
                titelDerStation : 'Krückau Sperrwerk BP',
                direction : 'right',
                offset : L.point(12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'krueckau_sperrwerk_bp/krueckau_sperrwerk_bp_node',
                uuid : '53c277c3-7ddb-4281-9937-97dcfe3753fa',
                dstelle: false,
                wartung: false
            },
            {
                // Lühort
                arrayElement: 'Luehort',
                positionMarker : [53.5713750668064, 9.633671953854105],
                titelDerStation : 'Lühort',
                direction : 'left',
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'luehort_sperrwerk/luehort_sperrwerk_node',
                uuid : '259dcb4a-2366-45a6-a448-b06a8941ae16',
                dstelle: false,
                wartung: false
            },
            {
                // Nalje Siel
                arrayElement: 'Nalje_Siel',
                positionMarker : [53.8314180384358, 9.033508373850022],
                titelDerStation : 'Nalje Siel',
                direction : 'top',
                offset : L.point(0, -12),
                urlSteckbrief : url_praefix_relativer_pfad + 'nalje_siel/nalje_siel_node',
                uuid : 'c93a87fb-a3d3-4d4f-a55d-8fabcfb9ab64',
                dstelle: false,
                wartung: false
            },
            {
                // Pinnau Sperrwerk BP
                arrayElement: 'Pinnau_Sperrwerk_BP',
                positionMarker : [53.67108042, 9.55841599],
                titelDerStation : 'Pinnau Sperrwerk BP',
                direction : 'right',
                offset : L.point(12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'pinnau_sperrwerk_bp/pinnau_sperrwerk_bp_node',
                uuid : '26259e8f-7d2d-47e0-8851-0257542b63b3',
                dstelle: false,
                wartung: false
            },
            {
                // Ruthenstrom-Sperrwerk
                arrayElement: 'Ruthenstrom_Sperrwerk',
                positionMarker : [53.71995967263287, 9.419406799690005],
                titelDerStation : 'Ruthenstrom-Sperrwerk',
                direction : 'left',
                offset : L.point(-12, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'ruthenstrom_sperrwerk/ruthenstrom_sperrwerk_node',
                uuid : '752a8c5a-5f2a-4575-af2f-d825cffc6eb3',
                dstelle: false,
                wartung: false
            },
            {
                // Schöneworth Siel
                arrayElement: 'Schoeneworth_Siel',
                positionMarker : [53.84689709635665, 9.28820059301234],
                titelDerStation : 'Schöneworth Siel',
                direction : 'top',
                offset : L.point(0, -12),
                urlSteckbrief : url_praefix_relativer_pfad + 'schoeneworth_siel/schoeneworth_siel_node',
                uuid : 'f973fb71-4a44-4603-8b13-25002dd6dbd8',
                dstelle: false,
                wartung: false
            },
            {
                // Schwinge Sperrwerk
                arrayElement: 'Schwinge_Sperrwerk',
                positionMarker : [53.6248522984709, 9.514315636026312],
                titelDerStation : 'Schwinge Sperrwerk',
                direction : 'left',
                offset : L.point(-12,0),
                urlSteckbrief : url_praefix_relativer_pfad + 'schwinge_sperrwerk/schwinge_sperrwerk_node',
                uuid : 'cc57198c-b40f-4c11-ad50-fceae4669a3e',
                dstelle: false,
                wartung: false
            },
            {
                // Stör Sperrwerk BP
                arrayElement: 'Stoer_Sperrwerk_BP',
                positionMarker : [53.82604528, 9.40182551],
                titelDerStation : 'Stör Sperrwerk BP',
                direction : 'top',
                offset : L.point(0, -12),
                urlSteckbrief : url_praefix_relativer_pfad + 'stoer_sperrwerk_bp/stoer_sperrwerk_bp_node',
                uuid : 'e5b8e9f3-f0cc-4ad7-8707-577ee1b25b3e',
                dstelle: false,
                wartung: false
            },
            {
                // Twielenfleth Siel
                arrayElement: 'Twielenfleth_Siel',
                positionMarker : [53.59875445021304, 9.567137091002857],
                titelDerStation : 'Twielenfleth Siel',
                direction : 'left',
                offset : L.point(-12,0),
                urlSteckbrief : url_praefix_relativer_pfad + 'twielenfleth_siel/twielenfleth_siel_node',
                uuid : '6d43098c-632d-4e89-996f-98b1c99e5630',
                dstelle: false,
                wartung: false
            },
            {
                // Wischhafen
                arrayElement: 'Wischhafen_Sperrwerk',
                positionMarker : [53.78517818926822, 9.340786122111078],
                titelDerStation : 'Wischhafen Sperrwerk',
                direction : 'left',
                offset : L.point(-12,0),
                urlSteckbrief : url_praefix_relativer_pfad + 'wischhafen_sperrwerk/wischhafen_sperrwerk_node',
                uuid : 'e0747660-6d75-476a-be0f-73865bf47363',
                dstelle: false,
                wartung: false
            },
            {
                // Dauermessstelle D1 Hanskalbsand
                arrayElement: 'D1_Hanskalbsand',
                positionMarker : [53.56571317, 9.67208509],
                titelDerStation : 'Dauermessstelle D1',
                direction : 'right',
                offset : L.point(22, -20),
                urlSteckbrief : url_praefix_relativer_pfad + 'dauermessstelle_d1/dauermessstelle_d1_node',
                uuid : '1c80e441-a1d7-400c-b569-e038c9876a4f',
                dstelle: true,
                wartung: false
            },
            {
                // Dauermessstelle D2 Lühesand Oberfläche
                arrayElement: 'D2_Luehesand_Oberflaeche',
                positionMarker : [53.60802833, 9.56926690],
                titelDerStation : 'Dauermessstelle D2',
                direction : 'right',
                offset : L.point(22, -20),
                urlSteckbrief : url_praefix_relativer_pfad + 'dauermessstelle_d2/dauermessstelle_d2_node',
                uuid : '3988cd01-1c7c-4bd3-a8ce-d324f73f346d',
                dstelle: true,
                wartung: false
            },
            {
                // Dauermessstelle D3 - Pagensand
                arrayElement: 'D3_Pagensand',
                positionMarker : [53.71176678, 9.47525412],
                titelDerStation : 'Dauermessstelle D3',
                direction : 'top',
                offset : L.point(3, -24),
                urlSteckbrief : url_praefix_relativer_pfad + 'dauermessstelle_d3/dauermessstelle_d3_node',
                uuid : '71160598',
                dstelle: true,
                wartung: true
            },
            {
                // Dauermessstelle D4 Rhinplatte-Nord Oberfläche
                arrayElement: 'D4_Rhinplatte_Nord_Oberflaeche',
                positionMarker : [53.79659241, 9.37225769],
                titelDerStation : 'Dauermessstelle D4',
                direction : 'right',
                offset : L.point(8, 0),
                urlSteckbrief : url_praefix_relativer_pfad + 'dauermessstelle_d4/dauermessstelle_d4_node',
                uuid : 'c8ff06a7-eac9-4db7-ab22-46954d4481cb',
                dstelle: true,
                wartung: true
            }];
        ////////////////////// Ende Funktion für Auswertung der Pegelstationen für das Leitfähigkeitsmessnetz
        ////////////////////// Kontrollelement Checkbox für den Benutzer zur An-/Abschaltung der Tooltips
        // Checkboxobjekt
        var checkBoxToolTip = L.control({
		    position: 'topright'
	    });
	    // Checkbox in einem Div-Element wrappen
	    checkBoxToolTip.onAdd = function (map) {
	        var div = L.DomUtil.create('div');
	        div.innerHTML = 
	        '<p style="background-color:#fefefe;">' +
	            '<label style="padding-right: 2px;font-size: 18px;" for="checkbox">' +
	            '<input style="vertical-align:middle;width: 22px;margin-top: 8px;" type="checkbox" id="checkBox" name="checkbox" />' +
	            '<span style="vertical-align:middle;font-size:14px;">Tooltips an-/ausschalten</span>' +
				'</label>' +
			'</p>';
	        div.id = 'DivCreatedByLeaflet';
	        return div;
	    };
	    // Hinzufügen der Checkbox mit gewrappten Div-Element auf die Map
        checkBoxToolTip.addTo(mapLeitfaehigkeitsmessnetz);
        // Verbinde das Div-Element mit einer Variable
        var element = window.document.getElementById("checkBox");
        // Setze die Checkbox defaultmäßig auf gesetzt
        element.checked = true;
        // Setzt einen Listener und die dazugehörige Funktion
        element.addEventListener("click", function (){
            	// Wenn die Checkbox gesetzt ist
            	if (this.checked) {
            		mapLeitfaehigkeitsmessnetz.eachLayer(function (layer) {
            		    // Alle Tooltips öffnen
            			if (layer.getTooltip) {
            				var toolTip = layer.getTooltip();
            				if (toolTip) {
            					mapLeitfaehigkeitsmessnetz.openTooltip(toolTip);
            				}
            			}
            		});
            	} else {
            		// Wenn die Checkbox nicht gesetzt ist
            		mapLeitfaehigkeitsmessnetz.eachLayer(function (layer) {
            			// Alle Tooltips schließen
            			if (layer.getTooltip) {
            				var toolTip = layer.getTooltip();
            				if (toolTip) {
            					mapLeitfaehigkeitsmessnetz.closeTooltip(toolTip);
            				}
            			}
            		});
            	}
        }, false);
        // Variable, die auf das <DIV> zeigt von der Checkbox
        var divElement = window.document.getElementById('DivWithCheckBox');
        ////////////////////// Ende Kontrollelement Checkbox für den Benutzer zur An-/Abschaltung der Tooltips
        ////////////////////// Anfang Funktion zur Berechnung der Leitfähigkeit und zur Ermittlung des Farbstatus der Pfeile
        var starteAuswertungPegelstationen = function (fromBrowser){
            //////////////////////  Anfang Variablen initialisieren Zeit, Grenzwert 
           	// Gewählten Grenzwert zuweisen
        	//window.document.getElementById("answer").value = browser;
        	var grenzwert = fromBrowser;
        	// aktuelles Datum zuweisen
        	var tag = new Date();
        	// Zeit in Millisekunden seit dem 01.01.1970 ermitteln
        	var syszeit = tag.getTime();
            //////////////////////  Ende Variablen initialisieren Zeit, Grenzwert
            //////////////////////  Anfang Funktion AjaxRequest an Pegelonline.de
            // Zähler initialisieren
            var zaehler = 0;
            // Funktionsbeginn
            function getAjaxRequests(){
                // Überprüfe, ob die Anzahl des durchzulaufenden Arrays kleiner als die Zählvariable ist
                if(zaehler < pegelStationen.length){
                    // Zählervariable ist kleiner
                    // Enthält den temporären Namen der Pegelstation für das assioziative Array
                    var arrayElement = pegelStationen[zaehler].arrayElement;
                    // Positionsdaten der Pegestation
                    var positionMarker = pegelStationen[zaehler].positionMarker;
                    // Stationsname der im Tooltip angezeigt wird
                    var titelDerStation = pegelStationen[zaehler].titelDerStation;
                    // Ausrichtung des Tooltips (Top, Left, Bottom, Right)
                    var direction = pegelStationen[zaehler].direction;
                    // Geographische Korrektur der Grafik
                    var offset = pegelStationen[zaehler].offset;
                    // URL zum Aufruf des Steckbriefes
                    var urlSteckbrief = pegelStationen[zaehler].urlSteckbrief;
                    // Eindeutige Id der Pegelstation für REST
                    var uuid = pegelStationen[zaehler].uuid;
                    // Boolsche Abfrage, ob sich die betreffende Pegelstation in Wartung befindet
                    var wartung = pegelStationen[zaehler].wartung;
                    // Anfragefunktion über Rest an Pegelonline.de -> über JQuery
                    $.getJSON('https://www.pegelonline.wsv.de/webservices/rest-api/v2/stations/'+ uuid +'/LF/currentmeasurement.json',
        		        function pegelonline(response) {
        		            // Wenn der Request positiv verläuft
        		            // Übergabe des Reponse in eine Variable
        		            var jsonResponse = response;
        		            // Initialisierung des Arrays
                            pegelStationen[zaehler].ajax = [];
                            // Übergabe des Response als komplettes Objekt in ein Arrayelement
                            pegelStationen[zaehler].ajax.object         = jsonResponse;
                            // Zeitstempel aus dem Response
                            pegelStationen[zaehler].ajax.timestamp      = jsonResponse.timestamp;
                            // Leitfaehigkeitswert
                            pegelStationen[zaehler].ajax.value          = jsonResponse.value;
                            // Vorliegende Trend
                            pegelStationen[zaehler].ajax.trend          = jsonResponse.trend;
                            // Berücksichtigung, wenn eine Station sich in Wartungsstatus befindet
                            if(typeof(wartung) === "boolean"){
                                // Variable 'wartung' ist ein boolscher Wert
                                if(wartung){
                                    pegelStationen[zaehler].ajax.preArrow = 'wartungsArbeiten';
                                }
                            }else{
                                // Variable 'wartung' ist kein boolscher Wert
                                console.log('Die Variable >>wartung<< ist kein bolscher Wert. Bitte prüfen Sie Ihre Eingabe.'+
                                'Station ' + titelDerStation + ' ist betroffen.');
                                alert('Die Variable >>wartung<< ist kein bolscher Wert.' +
                                ' \n\rBitte prüfen Sie Ihre Eingabe.' +
                                ' \n\rNäheres finden Sie in der Konsole des Browsers.');
                            }
                            // Erhöhe die Variable 'zaehler' um eins
                            zaehler++;
                            // Ruf die Funktion 'getAjaxRequests' auf
                            getAjaxRequests();
                    })
                .fail(function (jqxhr, textStatus, error) {
                    // Der Request ist fehlgeschlagen
                    // Gib Fehlermeldung in der Konsole aus
                    var err = textStatus + "," + error;
    		        console.log("Request Failed: " + err);
    		        // Setze ein Offlinesymbol als Marker
    		        pegelStationen[zaehler].ajax = [];
                    pegelStationen[zaehler].ajax.object       = '';
                    pegelStationen[zaehler].ajax.timestamp    = '';
                    pegelStationen[zaehler].ajax.value        = '';
                    pegelStationen[zaehler].ajax.trend        = '';
                    pegelStationen[zaehler].ajax.preArrow     = 'stationOffline';
                    zaehler++;
                    getAjaxRequests();
                });
            }else{
                // Zählervariable ist größer
                continueAfterAjaxRequests();
            }
        }
        ////////////////////// Ende Funktion zur Berechnung der Leitfähigkeit und zur Ermittlung des Farbstatus der Pfeile
        ////////////////////// Anfang Funktion zur Bestimmung der Symbole der Leitfähigkeit
        function continueAfterAjaxRequests(){
            // Durchlaufe das komplette Array
            for(var i = 0; i < pegelStationen.length; i++){
                // Enthält den temporären Namen der Pegelstation für das assioziative Array
                var arrayElement = pegelStationen[i].arrayElement;
                // Positionsdaten der Pegestation
                var positionMarker = pegelStationen[i].positionMarker;
            	// Stationsname der im Tooltip angezeigt wird
                var titelDerStation = pegelStationen[i].titelDerStation;
            	// Ausrichtung des Tooltips (Top, Left, Bottom, Right)
                var direction = pegelStationen[i].direction;
            	// Geographische Korrektur der Grafik
                var offset = pegelStationen[i].offset;
            	// URL zum Aufruf des Steckbriefes
                var urlSteckbrief = pegelStationen[i].urlSteckbrief;
            	// Eindeutige Id der Pegelstation für REST
                var uuid = pegelStationen[i].uuid;
            	// Boolsche Abfrage, ob sich die betreffende Pegelstation in Wartung befindet
                var wartung = pegelStationen[i].wartung;
                // Ist die betreffende Pegelstation eine Dauermessstelle
                var dauerMessStelle = pegelStationen[i].dstelle;
                // Zeitstempel aus Requestanfrage von Pegelonline.de  
                var timeStamp       = pegelStationen[i].ajax.timestamp;
                // Leitfähigkeitswert aus Requestanfrage von Pegelonline.de
                var value           = pegelStationen[i].ajax.value;
                // Errechneter Trend aus Requestanfrage von Pegelonline.de
                var trend           = pegelStationen[i].ajax.trend;
                // Komplettes Objekt aus Requestanfrage von Pegelonline.de
                var stationObject   = pegelStationen[i].ajax.object;
                // Errechneter Statuspfeil
                var preArrow        = pegelStationen[i].ajax.preArrow;
                // Berechnung des Status, um die Pfeildarstellung zu berechen
                var getStatusForArrow;
                if (preArrow === 'stationOffline'){
                    // Wenn die Station offline ist, hier durch nicht Erreichbarkeit des Servers
                    getStatusForArrow = stationOffline;
                }else if(preArrow === 'wartungsArbeiten'){
                    // Wenn die Station auf Wartung gesetzt wird
                    getStatusForArrow = wartungsArbeiten;
                }else{
                    // Für den Normalablauf
                    getStatusForArrow = berechneStatus(stationObject, syszeit, grenzwert);
                }
                // Abfrage, ob es hier um eine Dauermessstelle handelt
                if(dauerMessStelle){
                    getStatusForArrow = dauerMessStation;
                }
                // Prüfe, ob Markersarray bereits initialisert wurde
                if (typeof markersArray[arrayElement] === 'undefined') {
                    // Wenn der Marker zum ersten Mal instanziert wird
        		    var markerForLeafletMap = erschaffeMarkerObject(positionMarker, titelDerStation, direction, offset, urlSteckbrief, getStatusForArrow);
        		    markersArray[arrayElement] = markerForLeafletMap;
                }else{
                    // Wenn es Änderungen gibt
                    var tmpChange = markersArray[arrayElement];
                    tmpChange.setIcon(getStatusForArrow);
                }
            }
        }
        ////////////////////// Ende Funktion zur Bestimmung der Symbole der Leitfähigkeit
        ////////////////////// Anfang Unterfunktion der Funktion 'continueAfterAjaxRequests' -> Liefert als Rückgabewert das entsprechende Leafletobjekt zurück, um den errechneten Statuspfeil darzustellen
        function berechneStatus(station, syszeit, grenzwert){
            // Zeitstempel aus der Ajaxanfrage
    		var Zeit = station.timestamp;
    		// Leitfähigkeitswert aus der Ajaxanfrage
    		var Wert = station.value;
    		// Momentaner Trend aus der Ajaxanfrage
    		var Trend = station.trend;
    		// Zeit in Millisekunden seit dem 01.01.1970 für den Messwert ermitteln
    		var messzeit = Date.parse(Zeit);
    		// Variable für Zuweisung der Statusanzeige (Statuspfeil)
    		var aktuellIcon = null;
    		//Prüfung ob die Messwerte aktuell sind
    		if (Math.abs(syszeit - messzeit) < 30 * 1000 * 60) {
    			//Prüfung ob der aktuelle Wert unter dem Grenzwert und dem Pufferwert liegt --> grünes Symbol
    			if (Wert < (grenzwert - puffer)) {
    				if (Trend == -1) {
    					aktuellIcon = gruenerKreisPfeilUnten;
    				} else if (Trend == 1) {
    					aktuellIcon = gruenerKreisPfeilHoch;
    				} else {
    					aktuellIcon = gruenerKreisPfeilGleich;
    				}
    				//Prüfung ob der aktuelle Wert im Pufferbereich liegt --> oranges Symbol
    			} else if (Wert >= (grenzwert - puffer) && Wert <= grenzwert) {
    				if (Trend == -1) {
    					aktuellIcon = orangenerKreisPfeilUnten;
    				} else if (Trend == 1) {
    					aktuellIcon = orangenerKreisPfeilHoch;
    				} else {
    					aktuellIcon = orangenerKreisPfeilGleich;
    				}
    				//Prüfung ob der aktuelle Wert über dem Grenzwert liegt --> rotes Symbol
    			} else if (Wert > grenzwert) {
    				if (Trend == -1) {
    					aktuellIcon = roterKreisPfeilUnten;
    				} else if (Trend == 1) {
    					aktuellIcon = roterKreisPfeilHoch;
    				} else {
    				    aktuellIcon = roterKreisPfeilGleich;
    				}
    			}
    			} else {
    				// Station erhält keine aktuellen Werte von Pegelonline
    				aktuellIcon = stationOffline;
    			}
    			// Rückgabe Pegelstatus
    			return aktuellIcon;
    	}
    	////////////////////// Ende Unterfunktion der Funktion 'continueAfterAjaxRequests' -> Liefert als Rückgabewert das entsprechende Leafletobjekt zurück, um den errechneten Statuspfeil darzustellen
    	////////////////////// Anfang Funktion zur Intialisierung der Markersobjekt von Leaflet
        function erschaffeMarkerObject(positionMarker, titelDerStation, direction, offset, urlSteckbrief, aktuellIcon){
            var tmpMarker = L.marker(positionMarker, {
                icon: aktuellIcon,
        		title: titelDerStation,
        		alt: titelDerStation
        			})
    			.bindTooltip(titelDerStation, {
    				permanent: true,
    				direction: direction,
    				offset: offset
    			}).openTooltip().addTo(mapLeitfaehigkeitsmessnetz);
    			// Checke, ob ein Clickevent gesetzt werden kann außer bei Wartungsarbeiten oder Offline
                tmpMarker.on('click',
                    function(){
                        var quelle = "<iframe src='" + urlSteckbrief + "' width=95%, height=95%></iframe>";
                        if(this.options.icon.options.className !== 'leaflet-sprite-image leaflet-icon-maintenance' && this.options.icon.options.className !== 'leaflet-sprite-image leaflet-icon-offline'){
    		                document.getElementById("quelletext").innerHTML = quelle;
    		                modal.style.display = "block";
                        }
                    });
      		// Funktionsrückgabe
      		return tmpMarker;
        }
        ////////////////////// Ende Funktion zur Intialisierung der Markersobjekt von Leaflet
        // Starte die REST - Request
        getAjaxRequests();
        ///////////////////////////////////////////////////////////////////////////
        setTimeout(function () {
    	    starteAuswertungPegelstationen(startWertLeitfahigketismessnetz);
    	}, 120000);
    }; // Ende Funktion >>starteAuswertungPegelstationen<<
    ////////////////////// Starte die Funktion zur Auswertung der Pegelstationen //
    starteAuswertungPegelstationen(startWertLeitfahigketismessnetz);
    } // Ende Funktion initialisiereLeitfaehigkeitsmessnetz
}
////////////////////// Anfang Starte das Skript für Leitfähigkeitsmessnetz /////////////////
$(document).ready(function() {
    initialisiereLeitfaehigkeitsmessnetz(1900);
}); // Ende $(document).ready....
////////////////////// Ende Starte das Skript für Leitfähigkeitsmessnetz /////////////////
   /* Ende leitfaehigkeitsmessnetz_workflow */
   /* Start gsb_bkgMap */
   (function($){if(!$.gsb){$.gsb={};}$.gsb.bkgMap=function(el,options){var base=this;base.$el=$(el);base.el=el;base.$el.data("gsb.bkgMap",base);base.init=function(){base.options=$.extend({},$.gsb.bkgMap.defaultOptions,options);};$.gsb.bkgMap.prototype.createMap=function(){var map=new BKGWebMap.MapBuilder().setDiv(base.$el.attr("id")).setLayers([{type:"WMS.WEBATLASDE.LIGHT.FARBE"}]).create();map.setCenter(new OpenLayers.LonLat(601223.88377499,5668989.26347984),2);markers=new BKGWebMap.Layer.MarkerLayer("Planfeststellungsverfahren");map.addLayer(markers);};$.gsb.bkgMap.prototype.createMarker=function(){$.ajax({url:json_url_planfeststellung,dataType:"json",success:processData});};function processData(items){var i,item,icon;for(i in items){if(items.hasOwnProperty(i)){item=items[i];icon=new OpenLayers.Icon(image_url_map_marker,new OpenLayers.Size(26,39));markers.mark([item.longitude,item.latitude],unescape(item.content),{icon:icon,overflow:"auto",popupSize:new OpenLayers.Size(300,400),closeBox:true});}}base.$el.find(".olLayerDiv >div").on("mousedown",function(e){e.stopPropagation();var featureId=parseInt($(this).attr("id").replace("OL_Icon_",""));var excludedId="OpenLayers_Feature_"+(featureId+3)+"_popup";$(".bkgWebMapPopup").not("#"+excludedId).hide();});}base.init();base.createMap();base.createMarker();};$.gsb.bkgMap.defaultOptions={respondToEvents:false,onRefresh:function(){}};$.fn.gsb_bkgMap=function(options){return this.each(function(){(new $.gsb.bkgMap(this,options));});};})(jQuery);
   /* Ende gsb_bkgMap */
   /* Start init */
   ;$(document).ready(function() {
  // set js class
  $('body').addClass('js-on').removeClass('js-off');
  // stage slider
  var $stage = $('#stage').find('.stage-container');
  $stage.gsb_Slideshow({
    containerElement : '.teaser',
    pagination : true,
    autoplay : true,
    autoSpeed : 9000,
    pauseOnHover : true,
    nextButtonImage : image_url_next,
    prevButtonImage : image_url_back,
    prevButtonInactiveImage : image_url_back_g,
    nextButtonInactiveImage : image_url_next_g,
    playButtonImagePaused : image_url_play,
    playButtonImagePlaying : image_url_paused,
    slickOptions: {
      arrows: false,
      fade: false,
      nextArrow: $stage.parent().find('.next'),
      prevArrow: $stage.parent().find('.prev'),
      responsive: [
        {
          breakpoint: 1023,
          settings: {
            gsb: {
              playButton: false
            },
            autoplay : false,
            arrows: true
          }
        }
      ]
    }
  });
  // carousel slider
  var $carousel = $('.startseite').find('.teaser-switcher .karussell');
  $carousel.gsb_Slideshow({
    containerElement : '.teaser',
    autoplay : false,
    pagination: true,
    nextButtonImage : image_url_next,
    prevButtonImage : image_url_back,
    prevButtonInactiveImage : image_url_back_g,
    nextButtonInactiveImage: image_url_next_g,
    onSlickInit: function(slick){
      slick.$slider.materna_maxHeight({boxes: '.slick-slide a'});
    },
    slickOptions: {
      slidesToShow: 2,
      slidesToScroll: 2,
      fade: false,
      nextArrow: $carousel.parent().find('.next'),
      prevArrow: $carousel.parent().find('.prev'),
      responsive: [
        {
          breakpoint: 1023,
          settings: {
            slidesToShow: 2,
            slidesToScroll: 2
          }
        },
        {
          breakpoint: 610,
          settings: {
            slidesToShow: 1,
            slidesToScroll: 1
          }
        },
        {
          breakpoint: 440,
          settings: {
            slidesToShow: 1,
            slidesToScroll: 1
          }
        }
      ]
    }
  });
  // gallery slider
  $('#content').find('.galleryBox').gsb_Slideshow({
    slideshowControls : true,
    generateButtonContainer : false,
    autoplay : false,
    pagination: false,
    nextButtonImage : image_url_next,
    prevButtonImage : image_url_back,
    prevButtonInactiveImage : image_url_back_g,
    nextButtonInactiveImage : image_url_next_g,
    slickOptions: {
      adaptiveHeight: true,
      fade: false
    }
  });

  // gallery slider
  $('#supplement').find('.galleryBox').gsb_Slideshow({
    slideshowControls : true,
    generateButtonContainer : false,
    autoplay : false,
    pagination: false,
    nextButtonImage : image_url_gallery_supplement_next,
    prevButtonImage : image_url_gallery_supplement_back,
    prevButtonInactiveImage : image_url_gallery_supplement_back_g,
    nextButtonInactiveImage : image_url_gallery_supplement_next_g,
    slickOptions: {
      adaptiveHeight: true,
      fade: false
    }
  });

    // publicationslider
  $('.galleryPublicationBox').gsb_Slideshow({
    slideshowControls : true,
    generateButtonContainer : false,
    autoplay : false,
    pagination: false,
    nextButtonImage : image_url_pub_next,
    prevButtonImage : image_url_pub_back,
    prevButtonInactiveImage : image_url_pub_back_g,
    nextButtonInactiveImage : image_url_pub_next_g,
    slickOptions: {
      adaptiveHeight: true,
      fade: false
    }
  });
  
  // gsb_banner
  $("#gsbbanner").gsb_banner();
  // calendar
  $('#supplement .live-calendar').gsb_calendar({
    beforeInit: function(){
      $('#supplement .live-calendar').find('.has-tip').each(function(_, el){
        $('#' + $(el).attr('aria-describedby')).remove();
      });
    },
    afterInit: function afterInit(){
      $('#supplement .live-calendar').find('abbr, acronym').gsb_tooltip().foundation();
    }
  });
  // flyout
  $('#navPrimary').gsb_navigation();
  //navigation mobile
  /*
  $('#wrapperDivisions').gsb_togglebar();
  $('#togglenav').gsb_navigation_mobile({
    list : true,
    closeButtonImage : image_url_close_w,
    searchImage : image_url_loupe_b,
    autosuggestURL: json_url_mobileSearch
  });
  */
  // responsive table wrapper
  $('#content').gsb_responsiveTables();
  // service tabs
  $('#service-border').gsb_Serviceborder({
    close_button_markup_mode:'text'
  });
  // print link
  $('#navFunctions').gsb_printlink();
  // lightboxes
  $('.GlossarEntry').gsb_lightbox();
  $('.picture .loupe').not('.galleryBox *').gsb_lightbox();
  $('.galleryBox').gsb_lightbox({
    lightboxType: 'multiple',
    element: '.slide',
    imagePrev: image_url_back,
    imageNext: image_url_next,
    imagePrevInactive: image_url_back_g,
    imageNextInactive: image_url_next_g
  });
  // two-click share
  // see options for version 1.4 on http://www.heise.de/extras/socialshareprivacy/
  $("#navFunctionsWrapper").gsb_twoclickshare({
    toggleElement: '#navFunctionsRecommend a',
    socialSharePrivacyOptions: {
      'services': {
        'facebook': {
          'status':         'on',
          'txt_info':       '2 Klicks f&uuml;r mehr Datenschutz: Erst wenn Sie hier klicken, wird der Button aktiv und Sie k&ouml;nnen Ihre Empfehlung an Facebook senden. Schon beim Aktivieren werden Daten an Dritte &uuml;bertragen &ndash; siehe <em>i</em>.',
          'txt_fb_off':     'nicht mit Facebook verbunden',
          'txt_fb_on':      'mit Facebook verbunden',
          'perma_option':   'on',
          'display_name':   'Facebook',
          'language':       'de_DE',
          'dummy_caption':  'Facebook'
        },
        'twitter':  {
          'status':          'on',
          'txt_info':        '2 Klicks f&uuml;r mehr Datenschutz: Erst wenn Sie hier klicken, wird der Button aktiv und Sie k&ouml;nnen Ihre Empfehlung an Twitter senden. Schon beim Aktivieren werden Daten an Dritte &uuml;bertragen &ndash; siehe <em>i</em>.',
          'txt_twitter_off': 'nicht mit Twitter verbunden',
          'txt_twitter_on':  'mit Twitter verbunden',
          'perma_option':    'on',
          'display_name':    'Twitter',
          'language':        'en',
          'dummy_caption':   'Tweet'
        },
        'gplus':    {
          'status':         'on',
          'txt_info':       '2 Klicks f&uuml;r mehr Datenschutz: Erst wenn Sie hier klicken, wird der Button aktiv und Sie k&ouml;nnen Ihre Empfehlung an Google+ senden. Schon beim Aktivieren werden Daten an Dritte &uuml;bertragen &ndash; siehe <em>i</em>.',
          'txt_gplus_off':  'nicht mit Google+ verbunden',
          'txt_gplus_on':   'mit Google+ verbunden',
          'perma_option':   'on',
          'display_name':   'Google+',
          'referrer_track': '',
          'language':       'de'
        }
      },
      'info_link':      'http://www.heise.de/ct/artikel/2-Klicks-fuer-mehr-Datenschutz-1333879.html',
      'txt_help':       'Wenn Sie diese Felder durch einen Klick aktivieren, werden Informationen an Facebook, Twitter oder Google in die USA &uuml;bertragen und unter Umst&auml;nden auch dort gespeichert. N&auml;heres erfahren Sie durch einen Klick auf das <em>i</em>.',
      'settings_perma': 'Dauerhaft aktivieren und Daten&uuml;ber&shy;tragung zustimmen:'
    }
  });
  // clearfields
  $.gsb_clearfields();
  // toggle for faq and glossary
  $('.tabaccordion').gsb_toggle({
    tab : false
  });
  // toggle for search facets
  $('.toggleFacet').gsb_toggle({
    tab: false
  });
  // Accordion im Footer fuer small
  $('#siteInfo').gsb_toggle({
    onRefresh: function () {
      this.uninitAccordion();
    },
    responsive: [
      {
        breakpoint: 600,
        onRefresh: function () {
          this.initialize();
          this.refreshAccordion();
        },
        tab: false,
        accordion: true
      }
    ]
  });

  $('.togglewrapper').gsb_toggle({
    tabControls: ".tabs-list > li"
  });

  // open selected facets
  $('.selected .toggle').click();
  $('.toggleFacets').gsb_facetSlider();
  // tooltips for abbreviations
  //$('abbr').gsb_tooltip();
  //$('acronym').gsb_tooltip();
  // player for multimedia content
  $('#content video, #content audio').gsb_Multimedia();
  $('#supplement video, #supplement audio').gsb_Multimedia({
    settings:{
      mediaelementplayerOptions: {
        videoWidth: 280,
        videoHeight: 158
      }
    },
    responsive: [
      {
        breakpoint: 600,
        settings: {
          mediaelementplayerOptions: {
            videoWidth: 280,
            videoHeight: 158
          }
        }
      }
    ]
  });

  // Dienststellen Direktauswahl
  $('select[name=agencySelect]').gsb_webcodeRedirect();

  // Wasserstraßen Direktauswahl
  $('form[name=searchWaterways]').on('change',function(){
    $(this).find('input[name=submit]').click();
  });

  $('table.printable').gsb_printTable();
  $('#stickyService-border a[href*=#]').on('click', function(e){
      e.preventDefault();
      e.stopPropagation();
      var anchor_id = $(this).attr('href').split('#')[1];
      var tag = $("#"+anchor_id+"");
      $('html, body').animate(
        {
          scrollTop:$(tag).offset().top
        },
        {
          duration: 2000
        },'slow');
  });

  // Map
  $('#bkgMap').gsb_bkgMap();

  $( document ).tooltip({
    items: "a.imageTooltip",
    content: function() {
      var element = $( this );
      if ( element.is( "a.imageTooltip" ) ) {
        var text = element.attr('alt');
        return "<img class='map' alt='" + text +
          "' src='"+element.data('href')+"'>";
      }
    }
  });



  // shrinkheader
  $('#header').gsb_shrinkheader({
   shrinkOn : '100',
   shrinkCssClass : 'smaller',
   changeImage: {
       from: image_url_wsv_logo,
       to: image_url_shrinkheader_wsv_logo
   }
  });

  if ($('a[href*="#"]:not([href="#"])').length) {
    $('a[href*="#"]:not([href="#"])').click(function () {
      if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '') && location.hostname == this.hostname) {
        var target = $(this.hash);
        target = target.length ? target : $(document.getElementById( this.hash.slice(1) ));
        if (target.length) {
          var offset = (target.offset().top - 330);
          $('html, body').animate({
            scrollTop: (offset)
          }, 'slow');
          return false;
        }
      }
    });
  }

  // Map
  $('#bkgMap').gsb_bkgMap();
  $( document ).tooltip({
    items: "a.imageTooltip",
    content: function() {
      var element = $( this );
      if ( element.is( "a.imageTooltip" ) ) {
        var text = element.attr('alt');
        return "<img class='map' alt='" + text +
          "' src='"+element.data('href')+"'>";
      }
    }
  });

  //jQuery-Maphilight
  $('img[usemap]').maphilight();

  // Foundation initialisieren
  $(document).foundation();
});
   /* Ende init */
jQuery(document).ready(function (jQuery){
  // Autosuggest
  var searchforms = [];
  searchforms[1] = {
    id : 'searchService',
    action : 'SiteGlobals/Forms/Suche/Servicesuche_Autosuggest_Formular.html?nn=523912'
  };
  searchforms[2] = {
    id : 'searchExpert',
    action : 'SiteGlobals/Forms/Suche/Expertensuche_Autosuggest_Formular.html?nn=523912'
  };

  if(searchforms.length > 0){
    $(searchforms).each(function(index){
      var valueObj = searchforms[index],form;
      if(valueObj !== undefined){
        form = jQuery('form[name="'+ valueObj.id +'"]');
        if(form.length > 0){
          form.gsb_AutoSuggest(valueObj.action);
        }
      }
    });
  }
   setTimeout(function () {
    if (!!$('#stickyService-border').offset()) { // make sure ".js-sticky" element exists
      var serviceborder = $('#stickyService-border'); // returns number
      var stickyTop = serviceborder.offset().top; // returns number
   
       $(window).scroll(function () { // scroll event
        var windowTop = $(window).scrollTop(); // returns number
        //+ 60 = 10px border von body + 50px von position fixed
        if ((stickyTop) < windowTop+60) {
           serviceborder.css({position: 'fixed', top: '50px'});
        } else {
           serviceborder.removeAttr('style');
        }
      });
    }
  }, 500);

});

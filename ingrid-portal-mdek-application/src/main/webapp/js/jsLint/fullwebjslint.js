// (C)2011 Douglas Crockford
// www.JSLint.com
// webjslint.js

var JSON;if(!JSON){JSON={};}
(function(){'use strict';function f(n){return n<10?'0'+n:n;}
if(typeof Date.prototype.toJSON!=='function'){Date.prototype.toJSON=function(key){return isFinite(this.valueOf())?this.getUTCFullYear()+'-'+
f(this.getUTCMonth()+1)+'-'+
f(this.getUTCDate())+'T'+
f(this.getUTCHours())+':'+
f(this.getUTCMinutes())+':'+
f(this.getUTCSeconds())+'Z':null;};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf();};}
var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','"':'\\"','\\':'\\\\'},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];return typeof c==='string'?c:'\\u'+('0000'+a.charCodeAt(0).toString(16)).slice(-4);})+'"':'"'+string+'"';}
function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==='object'&&typeof value.toJSON==='function'){value=value.toJSON(key);}
if(typeof rep==='function'){value=rep.call(holder,key,value);}
switch(typeof value){case'string':return quote(value);case'number':return isFinite(value)?String(value):'null';case'boolean':case'null':return String(value);case'object':if(!value){return'null';}
gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==='[object Array]'){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||'null';}
v=partial.length===0?'[]':gap?'[\n'+gap+partial.join(',\n'+gap)+'\n'+mind+']':'['+partial.join(',')+']';gap=mind;return v;}
if(rep&&typeof rep==='object'){length=rep.length;for(i=0;i<length;i+=1){if(typeof rep[i]==='string'){k=rep[i];v=str(k,value);if(v){partial.push(quote(k)+(gap?': ':':')+v);}}}}else{for(k in value){if(Object.prototype.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?': ':':')+v);}}}}
v=partial.length===0?'{}':gap?'{\n'+gap+partial.join(',\n'+gap)+'\n'+mind+'}':'{'+partial.join(',')+'}';gap=mind;return v;}}
if(typeof JSON.stringify!=='function'){JSON.stringify=function(value,replacer,space){var i;gap='';indent='';if(typeof space==='number'){for(i=0;i<space;i+=1){indent+=' ';}}else if(typeof space==='string'){indent=space;}
rep=replacer;if(replacer&&typeof replacer!=='function'&&(typeof replacer!=='object'||typeof replacer.length!=='number')){throw new Error('JSON.stringify');}
return str('',{'':value});};}
if(typeof JSON.parse!=='function'){JSON.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==='object'){for(k in value){if(Object.prototype.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v;}else{delete value[k];}}}}
return reviver.call(holder,key,value);}
text=String(text);cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return'\\u'+
('0000'+a.charCodeAt(0).toString(16)).slice(-4);});}
if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,'@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']').replace(/(?:^|:|,)(?:\s*\[)+/g,''))){j=eval('('+text+')');return typeof reviver==='function'?walk({'':j},''):j;}
throw new SyntaxError('JSON.parse');};}}());var JSLINT=(function(){'use strict';function array_to_object(array,value){var i,length=array.length,object=Object.create(null);for(i=0;i<length;i+=1){object[array[i]]=value;}
return object;}
var allowed_option={ass:true,bitwise:true,browser:true,closure:true,continue:true,couch:true,debug:true,devel:true,eqeq:true,evil:true,forin:true,indent:10,maxerr:1000,maxlen:256,newcap:true,node:true,nomen:true,passfail:true,plusplus:true,properties:true,regexp:true,rhino:true,unparam:true,sloppy:true,stupid:true,sub:true,todo:true,vars:true,white:true},anonname,bang={'<':true,'<=':true,'==':true,'===':true,'!==':true,'!=':true,'>':true,'>=':true,'+':true,'-':true,'*':true,'/':true,'%':true},begin,block_var,browser=array_to_object(['clearInterval','clearTimeout','document','event','FormData','frames','history','Image','localStorage','location','name','navigator','Option','parent','screen','sessionStorage','setInterval','setTimeout','Storage','window','XMLHttpRequest'],false),bundle={a_label:"'{a}' is a statement label.",a_scope:"'{a}' used out of scope.",already_defined:"'{a}' is already defined.",and:"The '&&' subexpression should be wrapped in parens.",assignment_expression:"Unexpected assignment expression.",assignment_function_expression:"Expected an assignment or "+"function call and instead saw an expression.",avoid_a:"Avoid '{a}'.",bad_assignment:"Bad assignment.",bad_constructor:"Bad constructor.",bad_in_a:"Bad for in variable '{a}'.",bad_invocation:"Bad invocation.",bad_new:"Do not use 'new' for side effects.",bad_number:"Bad number '{a}'.",bad_operand:"Bad operand.",bad_wrap:"Do not wrap function literals in parens unless they "+"are to be immediately invoked.",combine_var:"Combine this with the previous 'var' statement.",conditional_assignment:"Expected a conditional expression and "+"instead saw an assignment.",confusing_a:"Confusing use of '{a}'.",confusing_regexp:"Confusing regular expression.",constructor_name_a:"A constructor name '{a}' should start with "+"an uppercase letter.",control_a:"Unexpected control character '{a}'.",dangling_a:"Unexpected dangling '_' in '{a}'.",deleted:"Only properties should be deleted.",duplicate_a:"Duplicate '{a}'.",empty_block:"Empty block.",empty_case:"Empty case.",empty_class:"Empty class.",evil:"eval is evil.",expected_a_b:"Expected '{a}' and instead saw '{b}'.",expected_a_b_from_c_d:"Expected '{a}' to match '{b}' from line "+"{c} and instead saw '{d}'.",expected_a_at_b_c:"Expected '{a}' at column {b}, not column {c}.",expected_id_a:"Expected an id, and instead saw #{a}.",expected_identifier_a:"Expected an identifier and instead saw '{a}'.",expected_identifier_a_reserved:"Expected an identifier and "+"instead saw '{a}' (a reserved word).",expected_number_a:"Expected a number and instead saw '{a}'.",expected_operator_a:"Expected an operator and instead saw '{a}'.",expected_positive_a:"Expected a positive number and instead saw '{a}'",expected_small_a:"Expected a small positive integer and instead saw '{a}'",expected_space_a_b:"Expected exactly one space between '{a}' and '{b}'.",expected_string_a:"Expected a string and instead saw '{a}'.",for_if:"The body of a for in should be wrapped in an if "+"statement to filter unwanted properties from the prototype.",function_block:"Function statements should not be placed in blocks."+"Use a function expression or move the statement to the top of "+"the outer function.",function_eval:"The Function constructor is eval.",function_loop:"Don't make functions within a loop.",function_statement:"Function statements are not invocable. "+"Wrap the whole function invocation in parens.",function_strict:"Use the function form of 'use strict'.",identifier_function:"Expected an identifier in an assignment "+"and instead saw a function invocation.",implied_evil:"Implied eval is evil. Pass a function instead of a string.",infix_in:"Unexpected 'in'. Compare with undefined, or use the "+"hasOwnProperty method instead.",insecure_a:"Insecure '{a}'.",isNaN:"Use the isNaN function to compare with NaN.",leading_decimal_a:"A leading decimal point can be confused with a dot: '.{a}'.",missing_a:"Missing '{a}'.",missing_a_after_b:"Missing '{a}' after '{b}'.",missing_property:"Missing property name.",missing_space_a_b:"Missing space between '{a}' and '{b}'.",missing_use_strict:"Missing 'use strict' statement.",move_invocation:"Move the invocation into the parens that "+"contain the function.",move_var:"Move 'var' declarations to the top of the function.",name_function:"Missing name in function statement.",nested_comment:"Nested comment.",not:"Nested not.",not_a_constructor:"Do not use {a} as a constructor.",not_a_defined:"'{a}' has not been fully defined yet.",not_a_function:"'{a}' is not a function.",not_a_label:"'{a}' is not a label.",not_a_scope:"'{a}' is out of scope.",not_greater:"'{a}' should not be greater than '{b}'.",octal_a:"Don't use octal: '{a}'. Use '\\u....' instead.",parameter_arguments_a:"Do not mutate parameter '{a}' when using 'arguments'.",parameter_a_get_b:"Unexpected parameter '{a}' in get {b} function.",parameter_set_a:"Expected parameter (value) in set {a} function.",radix:"Missing radix parameter.",read_only:"Read only.",redefinition_a_b:"Redefinition of '{a}' from line {b}.",reserved_a:"Reserved name '{a}'.",scanned_a_b:"{a} ({b}% scanned).",slash_equal:"A regular expression literal can be confused with '/='.",statement_block:"Expected to see a statement and instead saw a block.",stopping:"Stopping.",strange_loop:"Strange loop.",strict:"Strict violation.",subscript:"['{a}'] is better written in dot notation.",sync_a:"Unexpected sync method: '{a}'.",tag_a_in_b:"A '<{a}>' must be within '<{b}>'.",todo_comment:"Unexpected TODO comment.",too_long:"Line too long.",too_many:"Too many errors.",trailing_decimal_a:"A trailing decimal point can be confused "+"with a dot: '.{a}'.",unclosed:"Unclosed string.",unclosed_comment:"Unclosed comment.",unclosed_regexp:"Unclosed regular expression.",unescaped_a:"Unescaped '{a}'.",unexpected_a:"Unexpected '{a}'.",unexpected_char_a:"Unexpected character '{a}'.",unexpected_comment:"Unexpected comment.",unexpected_label_a:"Unexpected label '{a}'.",unexpected_property_a:"Unexpected /*property*/ '{a}'.",unexpected_space_a_b:"Unexpected space between '{a}' and '{b}'.",unexpected_typeof_a:"Unexpected 'typeof'. "+"Use '===' to compare directly with {a}.",uninitialized_a:"Uninitialized '{a}'.",unnecessary_else:"Unnecessary 'else' after disruption.",unnecessary_initialize:"It is not necessary to initialize '{a}' "+"to 'undefined'.",unnecessary_use:"Unnecessary 'use strict'.",unreachable_a_b:"Unreachable '{a}' after '{b}'.",unsafe:"Unsafe character.",unused_a:"Unused '{a}'.",url:"JavaScript URL.",use_array:"Use the array literal notation [].",use_braces:"Spaces are hard to count. Use {{a}}.",use_nested_if:"Expected 'else { if' and instead saw 'else if'.",use_object:"Use the object literal notation {} or Object.create(null).",use_or:"Use the || operator.",use_param:"Use a named parameter.",use_spaces:"Use spaces, not tabs.",used_before_a:"'{a}' was used before it was defined.",var_a_not:"Variable {a} was not declared correctly.",var_loop:"Don't declare variables in a loop.",weird_assignment:"Weird assignment.",weird_condition:"Weird condition.",weird_new:"Weird construction. Delete 'new'.",weird_program:"Weird program.",weird_relation:"Weird relation.",weird_ternary:"Weird ternary.",wrap_immediate:"Wrap an immediate function invocation in "+"parentheses to assist the reader in understanding that the "+"expression is the result of a function, and not the "+"function itself.",wrap_regexp:"Wrap the /regexp/ literal in parens to "+"disambiguate the slash operator.",write_is_wrong:"document.write can be a form of eval."},closure=array_to_object(['goog'],false),comments,comments_off,couch=array_to_object(['emit','getRow','isArray','log','provides','registerType','require','send','start','sum','toJSON'],false),descapes={'b':'\b','t':'\t','n':'\n','f':'\f','r':'\r','"':'"','/':'/','\\':'\\','!':'!'},devel=array_to_object(['alert','confirm','console','Debug','opera','prompt','WSH'],false),directive,escapes={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','\'':'\\\'','"':'\\"','/':'\\/','\\':'\\\\'},funct,functions,global_funct,global_scope,in_block,indent,itself,json_mode,lex,lines,lookahead,node=array_to_object(['Buffer','clearImmediate','clearInterval','clearTimeout','console','exports','global','module','process','require','setImmediate','setInterval','setTimeout','__dirname','__filename'],false),node_js,numbery=array_to_object(['indexOf','lastIndexOf','search'],true),next_token,option,predefined,prereg,prev_token,property,protosymbol,regexp_flag=array_to_object(['g','i','m'],true),return_this=function return_this(){return this;},rhino=array_to_object(['defineClass','deserialize','gc','help','load','loadClass','print','quit','readFile','readUrl','runCommand','seal','serialize','spawn','sync','toint32','version'],false),scope,semicolon_coda=array_to_object([';','"','\'',')'],true),standard=array_to_object(['Array','Boolean','Date','decodeURI','decodeURIComponent','encodeURI','encodeURIComponent','Error','eval','EvalError','Function','isFinite','isNaN','JSON','Map','Math','Number','Object','parseInt','parseFloat','Promise','Proxy','RangeError','ReferenceError','Reflect','RegExp','Set','String','Symbol','SyntaxError','System','TypeError','URIError','WeakMap','WeakSet'],false),strict_mode,syntax=Object.create(null),token,tokens,var_mode,warnings,crlfx=/\r\n?|\n/,cx=/[\u0000-\u0008\u000a-\u001f\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/,ix=/^([a-zA-Z_$][a-zA-Z0-9_$]*)$/,jx=/^(?:javascript|jscript|ecmascript|vbscript)\s*:/i,lx=/\*\/|\/\*/,nx=/[\u0000-\u001f'\\\u007f-\u009f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,syx=/Sync$/,tox=/^\W*to\s*do(?:\W|$)/i,tx=/^\s*([(){}\[\]\?.,:;'"~#@`]|={1,3}|\/(\*(jslint|properties|property|members?|globals?)?|=|\/)?|\*[\/=]?|\+(?:=|\++)?|-(?:=|-+)?|[\^%]=?|&[&=]?|\|[|=]?|>{1,3}=?|<(?:[\/=!]|\!(\[|--)?|<=?)?|\!(\!|==?)?|[a-zA-Z_$][a-zA-Z0-9_$]*|[0-9]+(?:[xX][0-9a-fA-F]+|\.[0-9]*)?(?:[eE][+\-]?[0-9]+)?)/;if(typeof String.prototype.entityify!=='function'){String.prototype.entityify=function(){return this.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;');};}
if(typeof String.prototype.isAlpha!=='function'){String.prototype.isAlpha=function(){return(this>='a'&&this<='z\uffff')||(this>='A'&&this<='Z\uffff');};}
if(typeof String.prototype.isDigit!=='function'){String.prototype.isDigit=function(){return(this>='0'&&this<='9');};}
if(typeof String.prototype.supplant!=='function'){String.prototype.supplant=function(o){return this.replace(/\{([^{}]*)\}/g,function(a,b){var replacement=o[b];return typeof replacement==='string'||typeof replacement==='number'?replacement:a;});};}
function sanitize(a){return escapes[a]||'\\u'+('0000'+a.charCodeAt().toString(16)).slice(-4);}
function add_to_predefined(group){Object.keys(group).forEach(function(name){predefined[name]=group[name];});}
function assume(){if(option.browser){add_to_predefined(browser);option.browser=false;}
if(option.closure){add_to_predefined(closure);}
if(option.couch){add_to_predefined(couch);option.couch=false;}
if(option.devel){add_to_predefined(devel);option.devel=false;}
if(option.node){add_to_predefined(node);option.node=false;node_js=true;}
if(option.rhino){add_to_predefined(rhino);option.rhino=false;}}
function artifact(tok){if(!tok){tok=next_token;}
return tok.id==='(number)'?tok.number:tok.string;}
function quit(message,line,character){throw{name:'JSLintError',line:line,character:character,message:bundle.scanned_a_b.supplant({a:bundle[message]||message,b:Math.floor((line/lines.length)*100)})};}
function warn(code,line,character,a,b,c,d){var warning={id:'(error)',raw:bundle[code]||code,code:code,evidence:lines[line-1]||'',line:line,character:character,a:a||artifact(this),b:b,c:c,d:d};warning.reason=warning.raw.supplant(warning);itself.errors.push(warning);if(option.passfail){quit('stopping',line,character);}
warnings+=1;if(warnings>=option.maxerr){quit('too_many',line,character);}
return warning;}
function stop(code,line,character,a,b,c,d){var warning=warn(code,line,character,a,b,c,d);quit('stopping',warning.line,warning.character);}
function expected_at(at){if(!option.white&&next_token.from!==at){next_token.warn('expected_a_at_b_c','',at,next_token.from);}}
lex=(function lex(){var character,c,from,length,line,pos,source_row;function next_line(){var at;character=1;source_row=lines[line];line+=1;if(source_row===undefined){return false;}
at=source_row.search(/\t/);if(at>=0){if(option.white){source_row=source_row.replace(/\t/g,' ');}else{warn('use_spaces',line,at+1);}}
at=source_row.search(cx);if(at>=0){warn('unsafe',line,at);}
if(option.maxlen&&option.maxlen<source_row.length){warn('too_long',line,source_row.length);}
return true;}
function it(type,value){var id,the_token;if(type==='(string)'){if(jx.test(value)){warn('url',line,from);}}
the_token=Object.create(syntax[(type==='(punctuator)'||(type==='(identifier)'&&Object.prototype.hasOwnProperty.call(syntax,value))?value:type)]||syntax['(error)']);if(type==='(identifier)'){the_token.identifier=true;if(value==='__iterator__'||value==='__proto__'){stop('reserved_a',line,from,value);}else if(!option.nomen&&(value.charAt(0)==='_'||value.charAt(value.length-1)==='_')){warn('dangling_a',line,from,value);}}
if(type==='(number)'){the_token.number=+value;}else if(value!==undefined){the_token.string=String(value);}
the_token.line=line;the_token.from=from;the_token.thru=character;if(comments.length){the_token.comments=comments;comments=[];}
id=the_token.id;prereg=id&&(('(,=:[!&|?{};~+-*%^<>'.indexOf(id.charAt(id.length-1))>=0)||id==='return'||id==='case');return the_token;}
function match(x){var exec=x.exec(source_row),first;if(exec){length=exec[0].length;first=exec[1];c=first.charAt(0);source_row=source_row.slice(length);from=character+length-first.length;character+=length;return first;}
for(;;){if(!source_row){if(!option.white){warn('unexpected_char_a',line,character-1,'(space)');}
return;}
c=source_row.charAt(0);if(c!==' '){break;}
source_row=source_row.slice(1);character+=1;}
stop('unexpected_char_a',line,character,c);}
function string(x){var ch,at=0,r='',result;function hex(n){var i=parseInt(source_row.substr(at+1,n),16);at+=n;if(i>=32&&i<=126&&i!==34&&i!==92&&i!==39){warn('unexpected_a',line,character,'\\');}
character+=n;ch=String.fromCharCode(i);}
if(json_mode&&x!=='"'){warn('expected_a_b',line,character,'"',x);}
for(;;){while(at>=source_row.length){at=0;if(!next_line()){stop('unclosed',line-1,from);}}
ch=source_row.charAt(at);if(ch===x){character+=1;source_row=source_row.slice(at+1);result=it('(string)',r);result.quote=x;return result;}
if(ch<' '){if(ch==='\n'||ch==='\r'){break;}
warn('control_a',line,character+at,source_row.slice(0,at));}else if(ch==='\\'){at+=1;character+=1;ch=source_row.charAt(at);switch(ch){case'':warn('unexpected_a',line,character,'\\');next_line();at=-1;break;case'\'':if(json_mode){warn('unexpected_a',line,character,'\\\'');}
break;case'u':hex(4);break;case'v':if(json_mode){warn('unexpected_a',line,character,'\\v');}
ch='\v';break;case'x':if(json_mode){warn('unexpected_a',line,character,'\\x');}
hex(2);break;default:if(typeof descapes[ch]!=='string'){warn(ch>='0'&&ch<='7'?'octal_a':'unexpected_a',line,character,'\\'+ch);}else{ch=descapes[ch];}}}
r+=ch;character+=1;at+=1;}}
function number(snippet){var digit;if(source_row.charAt(0).isAlpha()){warn('expected_space_a_b',line,character,c,source_row.charAt(0));}
if(c==='0'){digit=snippet.charAt(1);if(digit.isDigit()){if(token.id!=='.'){warn('unexpected_a',line,character,snippet);}}else if(json_mode&&(digit==='x'||digit==='X')){warn('unexpected_a',line,character,'0x');}}
if(snippet.slice(snippet.length-1)==='.'){warn('trailing_decimal_a',line,character,snippet);}
digit=+snippet;if(!isFinite(digit)){warn('bad_number',line,character,snippet);}
snippet=digit;return it('(number)',snippet);}
function comment(snippet,type){if(comments_off){warn('unexpected_comment',line,character);}else if(!option.todo&&tox.test(snippet)){warn('todo_comment',line,character);}
comments.push({id:type,from:from,thru:character,line:line,string:snippet});}
function regexp(){var at=0,b,bit,depth=0,flag='',high,letter,low,potential,quote,result;for(;;){b=true;c=source_row.charAt(at);at+=1;switch(c){case'':stop('unclosed_regexp',line,from);return;case'/':if(depth>0){warn('unescaped_a',line,from+at,'/');}
c=source_row.slice(0,at-1);potential=Object.create(regexp_flag);for(;;){letter=source_row.charAt(at);if(potential[letter]!==true){break;}
potential[letter]=false;at+=1;flag+=letter;}
if(source_row.charAt(at).isAlpha()){stop('unexpected_a',line,from,source_row.charAt(at));}
character+=at;source_row=source_row.slice(at);quote=source_row.charAt(0);if(quote==='/'||quote==='*'){stop('confusing_regexp',line,from);}
result=it('(regexp)',c);result.flag=flag;return result;case'\\':c=source_row.charAt(at);if(c<' '){warn('control_a',line,from+at,String(c));}else if(c==='<'){warn('unexpected_a',line,from+at,'\\');}
at+=1;break;case'(':depth+=1;b=false;if(source_row.charAt(at)==='?'){at+=1;switch(source_row.charAt(at)){case':':case'=':case'!':at+=1;break;default:warn('expected_a_b',line,from+at,':',source_row.charAt(at));}}
break;case'|':b=false;break;case')':if(depth===0){warn('unescaped_a',line,from+at,')');}else{depth-=1;}
break;case' ':pos=1;while(source_row.charAt(at)===' '){at+=1;pos+=1;}
if(pos>1){warn('use_braces',line,from+at,pos);}
break;case'[':c=source_row.charAt(at);if(c==='^'){at+=1;if(!option.regexp){warn('insecure_a',line,from+at,c);}else if(source_row.charAt(at)===']'){stop('unescaped_a',line,from+at,'^');}}
bit=false;if(c===']'){warn('empty_class',line,from+at-1);bit=true;}
klass:do{c=source_row.charAt(at);at+=1;switch(c){case'[':case'^':warn('unescaped_a',line,from+at,c);bit=true;break;case'-':if(bit){bit=false;}else{warn('unescaped_a',line,from+at,'-');bit=true;}
break;case']':if(!bit){warn('unescaped_a',line,from+at-1,'-');}
break klass;case'\\':c=source_row.charAt(at);if(c<' '){warn('control_a',line,from+at,String(c));}else if(c==='<'){warn('unexpected_a',line,from+at,'\\');}
at+=1;bit=true;break;case'/':warn('unescaped_a',line,from+at-1,'/');bit=true;break;default:bit=true;}}while(c);break;case'.':if(!option.regexp){warn('insecure_a',line,from+at,c);}
break;case']':case'?':case'{':case'}':case'+':case'*':warn('unescaped_a',line,from+at,c);break;}
if(b){switch(source_row.charAt(at)){case'?':case'+':case'*':at+=1;if(source_row.charAt(at)==='?'){at+=1;}
break;case'{':at+=1;c=source_row.charAt(at);if(c<'0'||c>'9'){warn('expected_number_a',line,from+at,c);}
at+=1;low=+c;for(;;){c=source_row.charAt(at);if(c<'0'||c>'9'){break;}
at+=1;low=+c+(low*10);}
high=low;if(c===','){at+=1;high=Infinity;c=source_row.charAt(at);if(c>='0'&&c<='9'){at+=1;high=+c;for(;;){c=source_row.charAt(at);if(c<'0'||c>'9'){break;}
at+=1;high=+c+(high*10);}}}
if(source_row.charAt(at)!=='}'){warn('expected_a_b',line,from+at,'}',c);}else{at+=1;}
if(source_row.charAt(at)==='?'){at+=1;}
if(low>high){warn('not_greater',line,from+at,low,high);}
break;}}}
c=source_row.slice(0,at-1);character+=at;source_row=source_row.slice(at);return it('(regexp)',c);}
return{init:function(source){if(typeof source==='string'){lines=source.split(crlfx);}else{lines=source;}
line=0;next_line();from=1;},token:function(){var first,i,snippet;for(;;){while(!source_row){if(!next_line()){return it('(end)');}}
snippet=match(tx);if(snippet){first=snippet.charAt(0);if(first.isAlpha()||first==='_'||first==='$'){return it('(identifier)',snippet);}
if(first.isDigit()){return number(snippet);}
switch(snippet){case'"':case"'":return string(snippet);case'//':comment(source_row,'//');source_row='';break;case'/*':for(;;){i=source_row.search(lx);if(i>=0){break;}
character=source_row.length;comment(source_row);from=0;if(!next_line()){stop('unclosed_comment',line,character);}}
comment(source_row.slice(0,i),'/*');character+=i+2;if(source_row.charAt(i)==='/'){stop('nested_comment',line,character);}
source_row=source_row.slice(i+2);break;case'':break;case'/':if(token.id==='/='){stop('slash_equal',line,from);}
return prereg?regexp():it('(punctuator)',snippet);default:return it('(punctuator)',snippet);}}}}};}());function define(kind,token){var name=token.string,master=scope[name];token.dead=false;token.init=false;token.kind=kind;token.master=master;token.used=0;token.writeable=true;if(kind==='var'&&funct===global_funct){if(!master){if(predefined[name]===false){token.writeable=false;}
global_scope[name]=token;}}else{if(master){if(master.function===funct){if(master.kind!=='exception'||kind!=='exception'||!master.dead){token.warn('already_defined',name);}}else if(master.function!==global_funct){if(kind==='var'){token.warn('redefinition_a_b',name,master.line);}}}
scope[name]=token;if(kind==='var'){block_var.push(name);}}}
function peek(distance){var found,slot=0;distance=distance||0;while(slot<=distance){found=lookahead[slot];if(!found){found=lookahead[slot]=lex.token();}
slot+=1;}
return found;}
function advance(id,match){if(indent){if(var_mode&&next_token.line!==token.line){if((var_mode!==indent||!next_token.edge)&&next_token.from===indent.at-
(next_token.edge?option.indent:0)){var dent=indent;for(;;){dent.at-=option.indent;if(dent===var_mode){break;}
dent=dent.was;}
dent.open=false;}
var_mode=null;}
if(next_token.id==='?'&&indent.mode===':'&&token.line!==next_token.line){indent.at-=option.indent;}
if(indent.open){if(next_token.edge){if(next_token.edge==='label'){expected_at(1);}else if(next_token.edge==='case'||indent.mode==='statement'){expected_at(indent.at-option.indent);}else if(indent.mode!=='array'||next_token.line!==token.line){expected_at(indent.at);}}else if(next_token.line!==token.line){if(next_token.from<indent.at+(indent.mode==='expression'?0:option.indent)){expected_at(indent.at+option.indent);}
indent.wrap=true;}}else if(next_token.line!==token.line){if(next_token.edge){expected_at(indent.at);}else{indent.wrap=true;if(indent.mode==='statement'||indent.mode==='var'){expected_at(indent.at+option.indent);}else if(next_token.from<indent.at+(indent.mode==='expression'?0:option.indent)){expected_at(indent.at+option.indent);}}}}
switch(token.id){case'(number)':if(next_token.id==='.'){next_token.warn('trailing_decimal_a');}
break;case'-':if(next_token.id==='-'||next_token.id==='--'){next_token.warn('confusing_a');}
break;case'+':if(next_token.id==='+'||next_token.id==='++'){next_token.warn('confusing_a');}
break;}
if(token.id==='(string)'||token.identifier){anonname=token.string;}
if(id&&next_token.id!==id){if(match){next_token.warn('expected_a_b_from_c_d',id,match.id,match.line,artifact());}else if(!next_token.identifier||next_token.string!==id){next_token.warn('expected_a_b',id,artifact());}}
prev_token=token;token=next_token;next_token=lookahead.shift()||lex.token();next_token.function=funct;tokens.push(next_token);}
function do_globals(){var name,writeable;for(;;){if(next_token.id!=='(string)'&&!next_token.identifier){return;}
name=next_token.string;advance();writeable=false;if(next_token.id===':'){advance(':');switch(next_token.id){case'true':writeable=predefined[name]!==false;advance('true');break;case'false':advance('false');break;default:next_token.stop('unexpected_a');}}
predefined[name]=writeable;if(next_token.id!==','){return;}
advance(',');}}
function do_jslint(){var name,value;while(next_token.id==='(string)'||next_token.identifier){name=next_token.string;if(!allowed_option[name]){next_token.stop('unexpected_a');}
advance();if(next_token.id!==':'){next_token.stop('expected_a_b',':',artifact());}
advance(':');if(typeof allowed_option[name]==='number'){value=next_token.number;if(value>allowed_option[name]||value<=0||Math.floor(value)!==value){next_token.stop('expected_small_a');}
option[name]=value;}else{if(next_token.id==='true'){option[name]=true;}else if(next_token.id==='false'){option[name]=false;}else{next_token.stop('unexpected_a');}}
advance();if(next_token.id===','){advance(',');}}
assume();}
function do_properties(){var name;option.properties=true;for(;;){if(next_token.id!=='(string)'&&!next_token.identifier){return;}
name=next_token.string;advance();if(next_token.id===':'){for(;;){advance();if(next_token.id!=='(string)'&&!next_token.identifier){break;}}}
property[name]=0;if(next_token.id!==','){return;}
advance(',');}}
directive=function directive(){var command=this.id,old_comments_off=comments_off,old_indent=indent;comments_off=true;indent=null;if(next_token.line===token.line&&next_token.from===token.thru){next_token.warn('missing_space_a_b',artifact(token),artifact());}
if(lookahead.length>0){this.warn('unexpected_a');}
switch(command){case'/*properties':case'/*property':case'/*members':case'/*member':do_properties();break;case'/*jslint':do_jslint();break;case'/*globals':case'/*global':do_globals();break;default:this.stop('unexpected_a');}
comments_off=old_comments_off;advance('*/');indent=old_indent;};function edge(mode){next_token.edge=indent?indent.open&&(mode||'edge'):'';}
function step_in(mode){var open;if(typeof mode==='number'){indent={at:+mode,open:true,was:indent};}else if(!indent){indent={at:1,mode:'statement',open:true};}else if(mode==='statement'){indent={at:indent.at,open:true,was:indent};}else{open=mode==='var'||next_token.line!==token.line;indent={at:(open||mode==='control'?indent.at+option.indent:indent.at)+(indent.wrap?option.indent:0),mode:mode,open:open,was:indent};if(mode==='var'&&open){var_mode=indent;}}}
function step_out(id,symbol){if(id){if(indent&&indent.open){indent.at-=option.indent;edge();}
advance(id,symbol);}
if(indent){indent=indent.was;}}
function one_space(left,right){left=left||token;right=right||next_token;if(right.id!=='(end)'&&!option.white&&(token.line!==right.line||token.thru+1!==right.from)){right.warn('expected_space_a_b',artifact(token),artifact(right));}}
function one_space_only(left,right){left=left||token;right=right||next_token;if(right.id!=='(end)'&&(left.line!==right.line||(!option.white&&left.thru+1!==right.from))){right.warn('expected_space_a_b',artifact(left),artifact(right));}}
function no_space(left,right){left=left||token;right=right||next_token;if((!option.white)&&left.thru!==right.from&&left.line===right.line){right.warn('unexpected_space_a_b',artifact(left),artifact(right));}}
function no_space_only(left,right){left=left||token;right=right||next_token;if(right.id!=='(end)'&&(left.line!==right.line||(!option.white&&left.thru!==right.from))){right.warn('unexpected_space_a_b',artifact(left),artifact(right));}}
function spaces(left,right){if(!option.white){left=left||token;right=right||next_token;if(left.thru===right.from&&left.line===right.line){right.warn('missing_space_a_b',artifact(left),artifact(right));}}}
function comma(){if(next_token.id!==','){warn('expected_a_b',token.line,token.thru,',',artifact());}else{if(!option.white){no_space_only();}
advance(',');spaces();}}
function semicolon(){if(next_token.id!==';'){warn('expected_a_b',token.line,token.thru,';',artifact());}else{if(!option.white){no_space_only();}
advance(';');if(semicolon_coda[next_token.id]!==true){spaces();}}}
function use_strict(){if(next_token.string==='use strict'){if(strict_mode){next_token.warn('unnecessary_use');}
edge();advance();semicolon();strict_mode=true;return true;}
return false;}
function are_similar(a,b){if(a===b){return true;}
if(Array.isArray(a)){if(Array.isArray(b)&&a.length===b.length){var i;for(i=0;i<a.length;i+=1){if(!are_similar(a[i],b[i])){return false;}}
return true;}
return false;}
if(Array.isArray(b)){return false;}
if(a.id==='(number)'&&b.id==='(number)'){return a.number===b.number;}
if(a.arity===b.arity&&a.string===b.string){switch(a.arity){case undefined:return a.string===b.string;case'prefix':case'suffix':return a.id===b.id&&are_similar(a.first,b.first)&&a.id!=='{'&&a.id!=='[';case'infix':return are_similar(a.first,b.first)&&are_similar(a.second,b.second);case'ternary':return are_similar(a.first,b.first)&&are_similar(a.second,b.second)&&are_similar(a.third,b.third);case'function':case'regexp':return false;default:return true;}}
if(a.id==='.'&&b.id==='['&&b.arity==='infix'){return a.second.string===b.second.string&&b.second.id==='(string)';}
if(a.id==='['&&a.arity==='infix'&&b.id==='.'){return a.second.string===b.second.string&&a.second.id==='(string)';}
return false;}
function expression(rbp,initial){var left;if(next_token.id==='(end)'){token.stop('unexpected_a',next_token.id);}
advance();if(initial){anonname='anonymous';}
if(initial===true&&token.fud){left=token.fud();}else{if(token.nud){left=token.nud();}else{if(next_token.id==='(number)'&&token.id==='.'){token.warn('leading_decimal_a',artifact());advance();return token;}
token.stop('expected_identifier_a',artifact(token));}
while(rbp<next_token.lbp){advance();left=token.led(left);}}
if(left&&left.assign&&!initial){if(!option.ass){left.warn('assignment_expression');}
if(left.id!=='='&&left.first.master){left.first.master.used=true;}}
return left;}
protosymbol={nud:function(){this.stop('unexpected_a');},led:function(){this.stop('expected_operator_a');},warn:function(code,a,b,c,d){if(!this.warning){this.warning=warn(code,this.line||0,this.from||0,a||artifact(this),b,c,d);}},stop:function(code,a,b,c,d){this.warning=undefined;this.warn(code,a,b,c,d);return quit('stopping',this.line,this.character);},lbp:0};function symbol(s,bp){var x=syntax[s];if(!x){x=Object.create(protosymbol);x.id=x.string=s;x.lbp=bp||0;syntax[s]=x;}
return x;}
function postscript(x){x.postscript=true;return x;}
function ultimate(s){var x=symbol(s,0);x.from=1;x.thru=1;x.line=0;x.edge='edge';x.string=s;return postscript(x);}
function reserve_name(x){var c=x.id.charAt(0);if((c>='a'&&c<='z')||(c>='A'&&c<='Z')){x.identifier=x.reserved=true;}
return x;}
function stmt(s,f){var x=symbol(s);x.fud=f;return reserve_name(x);}
function disrupt_stmt(s,f){var x=stmt(s,f);x.disrupt=true;}
function labeled_stmt(s,f){var x=stmt(s,function labeled(){var the_statement;if(funct.breakage){funct.breakage.push(this);}else{funct.breakage=[this];}
the_statement=f.apply(this);if(funct.breakage.length>1){funct.breakage.pop();}else{delete funct.breakage;}
return the_statement;});x.labeled=true;}
function prefix(s,f){var x=symbol(s,150);reserve_name(x);x.nud=function(){var that=this;that.arity='prefix';if(typeof f==='function'){that=f(that);if(that.arity!=='prefix'){return that;}}else{if(s==='typeof'){one_space();}else{no_space_only();}
that.first=expression(150);}
switch(that.id){case'++':case'--':if(!option.plusplus){that.warn('unexpected_a');}else if((!that.first.identifier||that.first.reserved)&&that.first.id!=='.'&&that.first.id!=='['){that.warn('bad_operand');}
break;default:if(that.first.arity==='prefix'||that.first.arity==='function'){that.warn('unexpected_a');}}
return that;};return x;}
function type(s,t,nud){var x=symbol(s);x.arity=t;if(nud){x.nud=nud;}
return x;}
function reserve(s,f){var x=symbol(s);x.identifier=x.reserved=true;if(typeof f==='function'){x.nud=f;}
return x;}
function constant(name){var x=reserve(name);x.string=name;x.nud=return_this;return x;}
function reservevar(s,v){return reserve(s,function(){if(typeof v==='function'){v(this);}
return this;});}
function infix(s,p,f,w){var x=symbol(s,p);reserve_name(x);x.led=function(left){this.arity='infix';if(!w){spaces(prev_token,token);spaces();}
if(!option.bitwise&&this.bitwise){this.warn('unexpected_a');}
if(typeof f==='function'){return f(left,this);}
this.first=left;this.second=expression(p);return this;};return x;}
function expected_relation(node,message){if(node.assign){node.warn(message||'conditional_assignment');}
return node;}
function expected_condition(node,message){switch(node.id){case'[':case'-':if(node.arity!=='infix'){node.warn(message||'weird_condition');}
break;case'false':case'function':case'Infinity':case'NaN':case'null':case'true':case'undefined':case'void':case'(number)':case'(regexp)':case'(string)':case'{':case'?':case'~':node.warn(message||'weird_condition');break;case'(':if(node.first.id==='new'||(node.first.string==='Boolean')||(node.first.id==='.'&&numbery[node.first.second.string]===true)){node.warn(message||'weird_condition');}
break;}
return node;}
function check_relation(node){switch(node.arity){case'prefix':switch(node.id){case'{':case'[':node.warn('unexpected_a');break;case'!':node.warn('confusing_a');break;}
break;case'function':case'regexp':node.warn('unexpected_a');break;default:if(node.id==='NaN'){node.warn('isNaN');}else if(node.relation){node.warn('weird_relation');}}
return node;}
function relation(s,eqeq){var x=infix(s,100,function(left,that){check_relation(left);if(eqeq&&!option.eqeq){that.warn('expected_a_b',eqeq,that.id);}
var right=expression(100);if(are_similar(left,right)||((left.id==='(string)'||left.id==='(number)')&&(right.id==='(string)'||right.id==='(number)'))){that.warn('weird_relation');}else if(left.id==='typeof'){if(right.id!=='(string)'){right.warn("expected_string_a",artifact(right));}else if(right.string==='undefined'||right.string==='null'){left.warn("unexpected_typeof_a",right.string);}}else if(right.id==='typeof'){if(left.id!=='(string)'){left.warn("expected_string_a",artifact(left));}else if(left.string==='undefined'||left.string==='null'){right.warn("unexpected_typeof_a",left.string);}}
that.first=left;that.second=check_relation(right);return that;});x.relation=true;return x;}
function lvalue(that,s){var master;if(that.identifier){master=scope[that.string];if(master){if(scope[that.string].writeable!==true){that.warn('read_only');}
master.used-=1;if(s==='='){master.init=true;}}else if(that.reserved){that.warn('expected_identifier_a_reserved');}}else if(that.id==='.'||that.id==='['){if(!that.first||that.first.string==='arguments'){that.warn('bad_assignment');}}else{that.warn('bad_assignment');}}
function assignop(s,op){var x=infix(s,20,function(left,that){var next;that.first=left;lvalue(left,s);that.second=expression(20);if(that.id==='='&&are_similar(that.first,that.second)){that.warn('weird_assignment');}
next=that;while(next_token.id==='='){lvalue(next.second,'=');next_token.first=next.second;next.second=next_token;next=next_token;advance('=');next.second=expression(20);}
return that;});x.assign=true;if(op){if(syntax[op].bitwise){x.bitwise=true;}}
return x;}
function bitwise(s,p){var x=infix(s,p,'number');x.bitwise=true;return x;}
function suffix(s){var x=symbol(s,150);x.led=function(left){no_space_only(prev_token,token);if(!option.plusplus){this.warn('unexpected_a');}else if((!left.identifier||left.reserved)&&left.id!=='.'&&left.id!=='['){this.warn('bad_operand');}
this.first=left;this.arity='suffix';return this;};return x;}
function optional_identifier(variable){if(next_token.identifier){advance();if(token.reserved&&variable){token.warn('expected_identifier_a_reserved');}
return token.string;}}
function identifier(variable){var i=optional_identifier(variable);if(!i){next_token.stop(token.id==='function'&&next_token.id==='('?'name_function':'expected_identifier_a');}
return i;}
function statement(){var label,preamble,the_statement;if(next_token.id===';'){next_token.warn('unexpected_a');semicolon();return;}
if(next_token.identifier&&!next_token.reserved&&peek().id===':'){edge('label');label=next_token;advance();advance(':');define('label',label);if(next_token.labeled!==true||funct===global_funct){label.stop('unexpected_label_a');}else if(jx.test(label.string+':')){label.warn('url');}
next_token.label=label;label.init=true;label.statement=next_token;}
preamble=next_token;if(token.id!=='else'){edge();}
step_in('statement');the_statement=expression(0,true);if(the_statement){if(the_statement.arity==='statement'){if(the_statement.id==='switch'||(the_statement.block&&the_statement.id!=='do')){spaces();}else{semicolon();}}else{if(the_statement.id==='('){if(the_statement.first.id==='new'){next_token.warn('bad_new');}}else if(the_statement.id==='++'||the_statement.id==='--'){lvalue(the_statement.first);}else if(!the_statement.assign&&the_statement.id!=='delete'){if(!option.closure||!preamble.comments){preamble.warn('assignment_function_expression');}}
semicolon();}}
step_out();if(label){label.dead=true;}
return the_statement;}
function statements(){var array=[],disruptor,the_statement;while(next_token.postscript!==true){if(next_token.id===';'){next_token.warn('unexpected_a');semicolon();}else{if(next_token.string==='use strict'){if((!node_js)||funct!==global_funct||array.length>0){next_token.warn('function_strict');}
use_strict();}
if(disruptor){next_token.warn('unreachable_a_b',next_token.string,disruptor.string);disruptor=null;}
the_statement=statement();if(the_statement){array.push(the_statement);if(the_statement.disrupt){disruptor=the_statement;array.disrupt=true;}}}}
return array;}
function block(kind){var array,curly=next_token,old_block_var=block_var,old_in_block=in_block,old_strict_mode=strict_mode;in_block=kind!=='function'&&kind!=='try'&&kind!=='catch';block_var=[];if(curly.id==='{'){spaces();advance('{');step_in();if(kind==='function'&&!use_strict()&&!old_strict_mode&&!option.sloppy&&funct.level===1){next_token.warn('missing_use_strict');}
array=statements();strict_mode=old_strict_mode;step_out('}',curly);}else if(in_block){curly.stop('expected_a_b','{',artifact());}else{curly.warn('expected_a_b','{',artifact());array=[statement()];array.disrupt=array[0].disrupt;}
if(kind!=='catch'&&array.length===0&&!option.debug){curly.warn('empty_block');}
block_var.forEach(function(name){scope[name].dead=true;});block_var=old_block_var;in_block=old_in_block;return array;}
function tally_property(name){if(option.properties&&typeof property[name]!=='number'){token.warn('unexpected_property_a',name);}
if(property[name]){property[name]+=1;}else{property[name]=1;}}
(function(){var x=symbol('(identifier)');x.nud=function(){var name=this.string,master=scope[name],writeable;if(!master){writeable=predefined[name];if(typeof writeable==='boolean'){global_scope[name]=master={dead:false,function:global_funct,kind:'var',string:name,writeable:writeable};}else{token.warn('used_before_a');}}else{this.master=master;}
if(master){if(master.kind==='label'){this.warn('a_label');}else{if(master.dead===true||master.dead===funct){this.warn('a_scope');}
master.used+=1;if(master.function!==funct){if(master.function===global_funct){funct.global.push(name);}else{master.function.closure.push(name);funct.outer.push(name);}}}}
return this;};x.identifier=true;}());type('(array)','array');type('(function)','function');type('(number)','number',return_this);type('(object)','object');type('(string)','string',return_this);type('(boolean)','boolean',return_this);type('(regexp)','regexp',return_this);ultimate('(begin)');ultimate('(end)');ultimate('(error)');postscript(symbol('}'));symbol(')');symbol(']');postscript(symbol('"'));postscript(symbol('\''));symbol(';');symbol(':');symbol(',');symbol('#');symbol('@');symbol('*/');postscript(reserve('case'));reserve('catch');postscript(reserve('default'));reserve('else');reserve('finally');reservevar('arguments',function(x){if(strict_mode&&funct===global_funct){x.warn('strict');}
funct.arguments=true;});reservevar('eval');constant('false','boolean');constant('Infinity','number');constant('NaN','number');constant('null','');reservevar('this',function(x){if(strict_mode&&funct.statement&&funct.name.charAt(0)>'Z'){x.warn('strict');}});constant('true','boolean');constant('undefined','');infix('?',30,function(left,that){step_in('?');that.first=expected_condition(expected_relation(left));that.second=expression(0);spaces();step_out();var colon=next_token;advance(':');step_in(':');spaces();that.third=expression(10);that.arity='ternary';if(are_similar(that.second,that.third)){colon.warn('weird_ternary');}else if(are_similar(that.first,that.second)){that.warn('use_or');}
step_out();return that;});infix('||',40,function(left,that){function paren_check(that){if(that.id==='&&'&&!that.paren){that.warn('and');}
return that;}
that.first=paren_check(expected_condition(expected_relation(left)));that.second=paren_check(expected_relation(expression(40)));if(are_similar(that.first,that.second)){that.warn('weird_condition');}
return that;});infix('&&',50,function(left,that){that.first=expected_condition(expected_relation(left));that.second=expected_relation(expression(50));if(are_similar(that.first,that.second)){that.warn('weird_condition');}
return that;});prefix('void',function(that){that.first=expression(0);that.warn('expected_a_b','undefined','void');return that;});bitwise('|',70);bitwise('^',80);bitwise('&',90);relation('==','===');relation('===');relation('!=','!==');relation('!==');relation('<');relation('>');relation('<=');relation('>=');bitwise('<<',120);bitwise('>>',120);bitwise('>>>',120);infix('in',120,function(left,that){that.warn('infix_in');that.left=left;that.right=expression(130);return that;});infix('instanceof',120);infix('+',130,function(left,that){if(left.id==='(number)'){if(left.number===0){left.warn('unexpected_a','0');}}else if(left.id==='(string)'){if(left.string===''){left.warn('expected_a_b','String','\'\'');}}
var right=expression(130);if(right.id==='(number)'){if(right.number===0){right.warn('unexpected_a','0');}}else if(right.id==='(string)'){if(right.string===''){right.warn('expected_a_b','String','\'\'');}}
if(left.id===right.id){if(left.id==='(string)'||left.id==='(number)'){if(left.id==='(string)'){left.string+=right.string;if(jx.test(left.string)){left.warn('url');}}else{left.number+=right.number;}
left.thru=right.thru;return left;}}
that.first=left;that.second=right;return that;});prefix('+');prefix('+++',function(){token.warn('confusing_a');this.first=expression(150);this.arity='prefix';return this;});infix('+++',130,function(left){token.warn('confusing_a');this.first=left;this.second=expression(130);return this;});infix('-',130,function(left,that){if((left.id==='(number)'&&left.number===0)||left.id==='(string)'){left.warn('unexpected_a');}
var right=expression(130);if((right.id==='(number)'&&right.number===0)||right.id==='(string)'){right.warn('unexpected_a');}
if(left.id===right.id&&left.id==='(number)'){left.number-=right.number;left.thru=right.thru;return left;}
that.first=left;that.second=right;return that;});prefix('-');prefix('---',function(){token.warn('confusing_a');this.first=expression(150);this.arity='prefix';return this;});infix('---',130,function(left){token.warn('confusing_a');this.first=left;this.second=expression(130);return this;});infix('*',140,function(left,that){if((left.id==='(number)'&&(left.number===0||left.number===1))||left.id==='(string)'){left.warn('unexpected_a');}
var right=expression(140);if((right.id==='(number)'&&(right.number===0||right.number===1))||right.id==='(string)'){right.warn('unexpected_a');}
if(left.id===right.id&&left.id==='(number)'){left.number*=right.number;left.thru=right.thru;return left;}
that.first=left;that.second=right;return that;});infix('/',140,function(left,that){if((left.id==='(number)'&&left.number===0)||left.id==='(string)'){left.warn('unexpected_a');}
var right=expression(140);if((right.id==='(number)'&&(right.number===0||right.number===1))||right.id==='(string)'){right.warn('unexpected_a');}
if(left.id===right.id&&left.id==='(number)'){left.number/=right.number;left.thru=right.thru;return left;}
that.first=left;that.second=right;return that;});infix('%',140,function(left,that){if((left.id==='(number)'&&(left.number===0||left.number===1))||left.id==='(string)'){left.warn('unexpected_a');}
var right=expression(140);if((right.id==='(number)'&&right.number===0)||right.id==='(string)'){right.warn('unexpected_a');}
if(left.id===right.id&&left.id==='(number)'){left.number%=right.number;left.thru=right.thru;return left;}
that.first=left;that.second=right;return that;});suffix('++');prefix('++');suffix('--');prefix('--');prefix('delete',function(that){one_space();var p=expression(0);if(!p||(p.id!=='.'&&p.id!=='[')){next_token.warn('deleted');}
that.first=p;return that;});prefix('~',function(that){no_space_only();if(!option.bitwise){that.warn('unexpected_a');}
that.first=expression(150);return that;});function banger(that){no_space_only();that.first=expected_condition(expression(150));if(bang[that.first.id]===that||that.first.assign){that.warn('confusing_a');}
return that;}
prefix('!',banger);prefix('!!',banger);prefix('typeof');prefix('new',function(that){one_space();var c=expression(160),n,p,v;that.first=c;if(c.id!=='function'){if(c.identifier){switch(c.string){case'Object':token.warn('use_object');break;case'Array':if(next_token.id==='('){p=next_token;p.first=this;advance('(');if(next_token.id!==')'){n=expression(0);p.second=[n];if(n.id==='(string)'||next_token.id===','){p.warn('use_array');}
while(next_token.id===','){advance(',');p.second.push(expression(0));}}else{token.warn('use_array');}
advance(')',p);return p;}
token.warn('use_array');break;case'Number':case'String':case'Boolean':case'Math':case'JSON':c.warn('not_a_constructor');break;case'Function':if(!option.evil){next_token.warn('function_eval');}
break;case'Date':case'RegExp':case'this':break;default:if(c.id!=='function'){v=c.string.charAt(0);if(!option.newcap&&(v<'A'||v>'Z')){token.warn('constructor_name_a');}}}}else{if(c.id!=='.'&&c.id!=='['&&c.id!=='('){token.warn('bad_constructor');}}}else{that.warn('weird_new');}
if(next_token.id!=='('){next_token.warn('missing_a','()');}
return that;});infix('(',160,function(left,that){var e,p;if(indent&&indent.mode==='expression'){no_space(prev_token,token);}else{no_space_only(prev_token,token);}
if(!left.immed&&left.id==='function'){next_token.warn('wrap_immediate');}
p=[];if(left.identifier){if(left.string.match(/^[A-Z]([A-Z0-9_$]*[a-z][A-Za-z0-9_$]*)?$/)){if(left.string!=='Number'&&left.string!=='String'&&left.string!=='Boolean'&&left.string!=='Date'){if(left.string==='Math'){left.warn('not_a_function');}else if(left.string==='Object'){token.warn('use_object');}else if(left.string==='Array'||!option.newcap){left.warn('missing_a','new');}}}else if(left.string==='JSON'){left.warn('not_a_function');}}else if(left.id==='.'){if(left.second.string==='split'&&left.first.id==='(string)'){left.second.warn('use_array');}}
step_in();if(next_token.id!==')'){no_space();for(;;){edge();e=expression(10);if(left.string==='Boolean'&&(e.id==='!'||e.id==='~')){e.warn('weird_condition');}
p.push(e);if(next_token.id!==','){break;}
comma();}}
no_space();step_out(')',that);if(typeof left==='object'){if(left.string==='parseInt'&&p.length===1){left.warn('radix');}else if(left.string==='String'&&p.length>=1&&p[0].id==='(string)'){left.warn('unexpected_a');}
if(!option.evil){if(left.string==='eval'||left.string==='Function'||left.string==='execScript'){left.warn('evil');}else if(p[0]&&p[0].id==='(string)'&&(left.string==='setTimeout'||left.string==='setInterval')){left.warn('implied_evil');}}
if(!left.identifier&&left.id!=='.'&&left.id!=='['&&left.id!=='('&&left.id!=='&&'&&left.id!=='||'&&left.id!=='?'){left.warn('bad_invocation');}
if(left.id==='.'){if(p.length>0&&left.first&&left.first.first&&are_similar(p[0],left.first.first)){if(left.second.string==='call'||(left.second.string==='apply'&&(p.length===1||(p[1].arity==='prefix'&&p[1].id==='[')))){left.second.warn('unexpected_a');}}
if(left.second.string==='toString'){if(left.first.id==='(string)'||left.first.id==='(number)'){left.second.warn('unexpected_a');}}}}
that.first=left;that.second=p;return that;},true);prefix('(',function(that){step_in('expression');no_space();edge();if(next_token.id==='function'){next_token.immed=true;}
var value=expression(0);value.paren=true;no_space();step_out(')',that);if(value.id==='function'){switch(next_token.id){case'(':next_token.warn('move_invocation');break;case'.':case'[':next_token.warn('unexpected_a');break;default:that.warn('bad_wrap');}}else if(!value.arity){if(!option.closure||!that.comments){that.warn('unexpected_a');}}
return value;});infix('.',170,function(left,that){no_space(prev_token,token);no_space();var name=identifier();if(typeof name==='string'){tally_property(name);}
that.first=left;that.second=token;if(left&&left.string==='arguments'&&(name==='callee'||name==='caller')){left.warn('avoid_a','arguments.'+name);}else if(!option.evil&&left&&left.string==='document'&&(name==='write'||name==='writeln')){left.warn('write_is_wrong');}else if(!option.stupid&&syx.test(name)){token.warn('sync_a');}else if(left&&left.id==='{'){that.warn('unexpected_a');}
if(!option.evil&&(name==='eval'||name==='execScript')){next_token.warn('evil');}
return that;},true);infix('[',170,function(left,that){var e,s;no_space_only(prev_token,token);no_space();step_in();edge();e=expression(0);switch(e.id){case'(number)':if(e.id==='(number)'&&left.id==='arguments'){left.warn('use_param');}
break;case'(string)':if(!option.evil&&(e.string==='eval'||e.string==='execScript')){e.warn('evil');}else if(!option.sub&&ix.test(e.string)){s=syntax[e.string];if(!s||!s.reserved){e.warn('subscript');}}
tally_property(e.string);break;}
if(left&&(left.id==='{'||(left.id==='['&&left.arity==='prefix'))){that.warn('unexpected_a');}
step_out(']',that);no_space(prev_token,token);that.first=left;that.second=e;return that;},true);prefix('[',function(that){that.first=[];step_in('array');while(next_token.id!=='(end)'){while(next_token.id===','){next_token.warn('unexpected_a');advance(',');}
if(next_token.id===']'){break;}
indent.wrap=false;edge();that.first.push(expression(10));if(next_token.id===','){comma();if(next_token.id===']'){token.warn('unexpected_a');break;}}else{break;}}
step_out(']',that);return that;},170);function property_name(){var id=optional_identifier();if(!id){if(next_token.id==='(string)'){id=next_token.string;advance();}else if(next_token.id==='(number)'){id=next_token.number.toString();advance();}}
return id;}
assignop('=');assignop('+=','+');assignop('-=','-');assignop('*=','*');assignop('/=','/').nud=function(){next_token.stop('slash_equal');};assignop('%=','%');assignop('&=','&');assignop('|=','|');assignop('^=','^');assignop('<<=','<<');assignop('>>=','>>');assignop('>>>=','>>>');function function_parameters(){var id,parameters=[],paren=next_token;advance('(');token.function=funct;step_in();no_space();if(next_token.id!==')'){for(;;){edge();id=identifier();if(token.reserved){token.warn('expected_identifier_a_reserved');}
define('parameter',token);parameters.push(id);token.init=true;token.writeable=true;if(next_token.id!==','){break;}
comma();}}
no_space();step_out(')',paren);return parameters;}
function do_function(func,name){var old_funct=funct,old_option=option,old_scope=scope;scope=Object.create(old_scope);funct={closure:[],global:[],level:old_funct.level+1,line:next_token.line,loopage:0,name:name||'\''+(anonname||'').replace(nx,sanitize)+'\'',outer:[],scope:scope};funct.parameter=function_parameters();func.function=funct;option=Object.create(old_option);functions.push(funct);if(name){func.name=name;func.string=name;define('function',func);func.init=true;func.used+=1;}
func.writeable=false;one_space();func.block=block('function');Object.keys(scope).forEach(function(name){var master=scope[name];if(!master.used&&master.kind!=='exception'&&(master.kind!=='parameter'||!option.unparam)){master.warn('unused_a');}else if(!master.init){master.warn('uninitialized_a');}});funct=old_funct;option=old_option;scope=old_scope;}
prefix('{',function(that){var get,i,j,name,set,seen=Object.create(null);that.first=[];step_in();while(next_token.id!=='}'){indent.wrap=false;edge();if(next_token.string==='get'&&peek().id!==':'){get=next_token;advance('get');one_space_only();name=next_token;i=property_name();if(!i){next_token.stop('missing_property');}
get.string='';do_function(get);if(funct.loopage){get.warn('function_loop');}
if(get.function.parameter.length){get.warn('parameter_a_get_b',get.function.parameter[0],i);}
comma();set=next_token;spaces();edge();advance('set');set.string='';one_space_only();j=property_name();if(i!==j){token.stop('expected_a_b',i,j||next_token.string);}
do_function(set);if(set.block.length===0){token.warn('missing_a','throw');}
if(set.function.parameter.length===0){set.stop('parameter_set_a','value');}else if(set.function.parameter[0]!=='value'){set.stop('expected_a_b','value',set.function.parameter[0]);}
name.first=[get,set];}else{name=next_token;i=property_name();if(typeof i!=='string'){next_token.stop('missing_property');}
advance(':');spaces();name.first=expression(10);}
that.first.push(name);if(seen[i]===true){next_token.warn('duplicate_a',i);}
seen[i]=true;tally_property(i);if(next_token.id!==','){break;}
for(;;){comma();if(next_token.id!==','){break;}
next_token.warn('unexpected_a');}
if(next_token.id==='}'){token.warn('unexpected_a');}}
step_out('}',that);return that;});stmt('{',function(){next_token.warn('statement_block');this.arity='statement';this.block=statements();this.disrupt=this.block.disrupt;advance('}',this);return this;});stmt('/*global',directive);stmt('/*globals',directive);stmt('/*jslint',directive);stmt('/*member',directive);stmt('/*members',directive);stmt('/*property',directive);stmt('/*properties',directive);stmt('var',function(){var assign,id,name;if(funct.loopage){next_token.warn('var_loop');}else if(funct.varstatement&&!option.vars){next_token.warn('combine_var');}
if(funct!==global_funct){funct.varstatement=true;}
this.arity='statement';this.first=[];step_in('var');for(;;){name=next_token;id=identifier(true);define('var',name);name.dead=funct;if(next_token.id==='='){if(funct===global_funct&&!name.writeable){name.warn('read_only');}
assign=next_token;assign.first=name;spaces();advance('=');spaces();if(next_token.id==='undefined'){token.warn('unnecessary_initialize',id);}
if(peek(0).id==='='&&next_token.identifier){next_token.stop('var_a_not');}
assign.second=expression(0);assign.arity='infix';name.init=true;this.first.push(assign);}else{this.first.push(name);}
name.dead=false;name.writeable=true;if(next_token.id!==','){break;}
comma();indent.wrap=false;if(var_mode&&next_token.line===token.line&&this.first.length===1){var_mode=null;indent.open=false;indent.at-=option.indent;}
spaces();edge();}
var_mode=null;step_out();return this;});stmt('function',function(){one_space();if(in_block){token.warn('function_block');}
var name=next_token,id=identifier(true);define('var',name);if(!name.writeable){name.warn('read_only');}
name.init=true;name.statement=true;no_space();this.arity='statement';do_function(this,id);if(next_token.id==='('&&next_token.line===token.line){next_token.stop('function_statement');}
return this;});prefix('function',function(that){var id=optional_identifier(true),name;if(id){name=token;no_space();}else{id='';one_space();}
do_function(that,id);if(name){name.function=that.function;}
if(funct.loopage){that.warn('function_loop');}
switch(next_token.id){case';':case'(':case')':case',':case']':case'}':case':':case'(end)':break;case'.':if(peek().string!=='bind'||peek(1).id!=='('){next_token.warn('unexpected_a');}
break;default:next_token.stop('unexpected_a');}
that.arity='function';return that;});stmt('if',function(){var paren=next_token;one_space();advance('(');step_in('control');no_space();edge();this.arity='statement';this.first=expected_condition(expected_relation(expression(0)));no_space();step_out(')',paren);one_space();this.block=block('if');if(next_token.id==='else'){if(this.block.disrupt){next_token.warn(this.elif?'use_nested_if':'unnecessary_else');}
one_space();advance('else');one_space();if(next_token.id==='if'){next_token.elif=true;this.else=statement(true);}else{this.else=block('else');}
if(this.else.disrupt&&this.block.disrupt){this.disrupt=true;}}
return this;});stmt('try',function(){var exception_variable,paren;one_space();this.arity='statement';this.block=block('try');if(next_token.id==='catch'){one_space();advance('catch');one_space();paren=next_token;advance('(');step_in('control');no_space();edge();exception_variable=next_token;this.first=identifier();define('exception',exception_variable);exception_variable.init=true;no_space();step_out(')',paren);one_space();this.second=block('catch');if(this.second.length){if(this.first==='ignore'){exception_variable.warn('unexpected_a');}}else{if(this.first!=='ignore'){exception_variable.warn('expected_a_b','ignore',exception_variable.string);}}
exception_variable.dead=true;}
if(next_token.id==='finally'){one_space();advance('finally');one_space();this.third=block('finally');}else if(!this.second){next_token.stop('expected_a_b','catch',artifact());}
return this;});labeled_stmt('while',function(){one_space();var paren=next_token;funct.loopage+=1;advance('(');step_in('control');no_space();edge();this.arity='statement';this.first=expected_relation(expression(0));if(this.first.id!=='true'){expected_condition(this.first,'unexpected_a');}
no_space();step_out(')',paren);one_space();this.block=block('while');if(this.block.disrupt){prev_token.warn('strange_loop');}
funct.loopage-=1;return this;});reserve('with');labeled_stmt('switch',function(){var cases=[],old_in_block=in_block,particular,that=token,the_case=next_token;function find_duplicate_case(value){if(are_similar(particular,value)){value.warn('duplicate_a');}}
one_space();advance('(');no_space();step_in();this.arity='statement';this.first=expected_condition(expected_relation(expression(0)));no_space();step_out(')',the_case);one_space();advance('{');step_in();in_block=true;this.second=[];if(that.from!==next_token.from&&!option.white){next_token.warn('expected_a_at_b_c',next_token.string,that.from,next_token.from);}
while(next_token.id==='case'){the_case=next_token;the_case.first=[];the_case.arity='case';for(;;){spaces();edge('case');advance('case');one_space();particular=expression(0);cases.forEach(find_duplicate_case);cases.push(particular);the_case.first.push(particular);if(particular.id==='NaN'){particular.warn('unexpected_a');}
no_space_only();advance(':');if(next_token.id!=='case'){break;}}
spaces();the_case.second=statements();if(the_case.second&&the_case.second.length>0){if(!the_case.second[the_case.second.length-1].disrupt){next_token.warn('missing_a_after_b','break','case');}}else{next_token.warn('empty_case');}
this.second.push(the_case);}
if(this.second.length===0){next_token.warn('missing_a','case');}
if(next_token.id==='default'){spaces();the_case=next_token;the_case.arity='case';edge('case');advance('default');no_space_only();advance(':');spaces();the_case.second=statements();if(the_case.second&&the_case.second.length>0){this.disrupt=the_case.second[the_case.second.length-1].disrupt;}else{the_case.warn('empty_case');}
this.second.push(the_case);}
if(this.break){this.disrupt=false;}
spaces();step_out('}',this);in_block=old_in_block;return this;});stmt('debugger',function(){if(!option.debug){this.warn('unexpected_a');}
this.arity='statement';return this;});labeled_stmt('do',function(){funct.loopage+=1;one_space();this.arity='statement';this.block=block('do');if(this.block.disrupt){prev_token.warn('strange_loop');}
one_space();advance('while');var paren=next_token;one_space();advance('(');step_in();no_space();edge();this.first=expected_condition(expected_relation(expression(0)),'unexpected_a');no_space();step_out(')',paren);funct.loopage-=1;return this;});labeled_stmt('for',function(){var blok,filter,master,ok=false,paren=next_token,value;this.arity='statement';funct.loopage+=1;advance('(');if(next_token.id===';'){no_space();advance(';');no_space();advance(';');no_space();advance(')');blok=block('for');}else{step_in('control');spaces(this,paren);no_space();if(next_token.id==='var'){next_token.stop('move_var');}
edge();if(peek(0).id==='in'){this.forin=true;value=expression(1000);master=value.master;if(!master){value.stop('bad_in_a');}
if(master.kind!=='var'||master.function!==funct||!master.writeable||master.dead){value.warn('bad_in_a');}
master.init=true;master.used-=1;this.first=value;advance('in');this.second=expression(20);step_out(')',paren);blok=block('for');if(!option.forin){if(blok.length===1&&typeof blok[0]==='object'){if(blok[0].id==='if'&&!blok[0].else){filter=blok[0].first;while(filter.id==='&&'){filter=filter.first;}
switch(filter.id){case'===':case'!==':ok=filter.first.id==='['?are_similar(filter.first.first,this.second)&&are_similar(filter.first.second,this.first):filter.first.id==='typeof'&&filter.first.first.id==='['&&are_similar(filter.first.first.first,this.second)&&are_similar(filter.first.first.second,this.first);break;case'(':ok=filter.first.id==='.'&&((are_similar(filter.first.first,this.second)&&filter.first.second.string==='hasOwnProperty'&&are_similar(filter.second[0],this.first))||(filter.first.first.id==='.'&&filter.first.first.first.first&&filter.first.first.first.first.string==='Object'&&filter.first.first.first.id==='.'&&filter.first.first.first.second.string==='prototype'&&filter.first.first.second.string==='hasOwnProperty'&&filter.first.second.string==='call'&&are_similar(filter.second[0],this.second)&&are_similar(filter.second[1],this.first)));break;}}else if(blok[0].id==='switch'){ok=blok[0].id==='switch'&&blok[0].first.id==='typeof'&&blok[0].first.first.id==='['&&are_similar(blok[0].first.first.first,this.second)&&are_similar(blok[0].first.first.second,this.first);}}
if(!ok){this.warn('for_if');}}}else{edge();this.first=[];for(;;){this.first.push(expression(0,'for'));if(next_token.id!==','){break;}
comma();}
semicolon();edge();this.second=expected_relation(expression(0));if(this.second.id!=='true'){expected_condition(this.second,'unexpected_a');}
semicolon(token);if(next_token.id===';'){next_token.stop('expected_a_b',')',';');}
this.third=[];edge();for(;;){this.third.push(expression(0,'for'));if(next_token.id!==','){break;}
comma();}
no_space();step_out(')',paren);one_space();blok=block('for');}}
if(blok.disrupt){prev_token.warn('strange_loop');}
this.block=blok;funct.loopage-=1;return this;});function optional_label(that){var label=next_token.string,master;that.arity='statement';if(!funct.breakage||(!option.continue&&that.id==='continue')){that.warn('unexpected_a');}else if(next_token.identifier&&token.line===next_token.line){one_space_only();master=scope[label];if(!master||master.kind!=='label'){next_token.warn('not_a_label');}else if(master.dead||master.function!==funct){next_token.warn('not_a_scope');}else{master.used+=1;if(that.id==='break'){master.statement.break=true;}
if(funct.breakage[funct.breakage.length-1]===master.statement){next_token.warn('unexpected_a');}}
that.first=next_token;advance();}else{if(that.id==='break'){funct.breakage[funct.breakage.length-1].break=true;}}
return that;}
disrupt_stmt('break',function(){return optional_label(this);});disrupt_stmt('continue',function(){return optional_label(this);});disrupt_stmt('return',function(){if(funct===global_funct){this.warn('unexpected_a');}
this.arity='statement';if(next_token.id!==';'&&next_token.line===token.line){if(option.closure){spaces();}else{one_space_only();}
if(next_token.id==='/'||next_token.id==='(regexp)'){next_token.warn('wrap_regexp');}
this.first=expression(0);if(this.first.assign){this.first.warn('unexpected_a');}}
return this;});disrupt_stmt('throw',function(){this.arity='statement';one_space_only();this.first=expression(20);return this;});reserve('class');reserve('const');reserve('enum');reserve('export');reserve('extends');reserve('import');reserve('super');reserve('implements');reserve('interface');reserve('let');reserve('package');reserve('private');reserve('protected');reserve('public');reserve('static');reserve('yield');function json_value(){function json_object(){var brace=next_token,object=Object.create(null);advance('{');if(next_token.id!=='}'){while(next_token.id!=='(end)'){while(next_token.id===','){next_token.warn('unexpected_a');advance(',');}
if(next_token.id!=='(string)'){next_token.warn('expected_string_a');}
if(object[next_token.string]===true){next_token.warn('duplicate_a');}else if(next_token.string==='__proto__'){next_token.warn('dangling_a');}else{object[next_token.string]=true;}
advance();advance(':');json_value();if(next_token.id!==','){break;}
advance(',');if(next_token.id==='}'){token.warn('unexpected_a');break;}}}
advance('}',brace);}
function json_array(){var bracket=next_token;advance('[');if(next_token.id!==']'){while(next_token.id!=='(end)'){while(next_token.id===','){next_token.warn('unexpected_a');advance(',');}
json_value();if(next_token.id!==','){break;}
advance(',');if(next_token.id===']'){token.warn('unexpected_a');break;}}}
advance(']',bracket);}
switch(next_token.id){case'{':json_object();break;case'[':json_array();break;case'true':case'false':case'null':case'(number)':case'(string)':advance();break;case'-':advance('-');no_space_only();advance('(number)');break;default:next_token.stop('unexpected_a');}}
itself=function JSLint(the_source,the_option){var i,predef,tree;itself.errors=[];itself.tree='';itself.properties='';begin=prev_token=token=next_token=Object.create(syntax['(begin)']);tokens=[];predefined=Object.create(null);add_to_predefined(standard);property=Object.create(null);if(the_option){option=Object.create(the_option);predef=option.predef;if(predef){if(Array.isArray(predef)){for(i=0;i<predef.length;i+=1){predefined[predef[i]]=true;}}else if(typeof predef==='object'){add_to_predefined(predef);}}}else{option=Object.create(null);}
option.indent=+option.indent||4;option.maxerr=+option.maxerr||50;global_scope=scope=Object.create(null);global_funct=funct={scope:scope,loopage:0,level:0};functions=[funct];block_var=[];comments=[];comments_off=false;in_block=false;indent=null;json_mode=false;lookahead=[];node_js=false;prereg=true;strict_mode=false;var_mode=null;warnings=0;lex.init(the_source);assume();try{advance();if(next_token.id==='(number)'){next_token.stop('unexpected_a');}else{switch(next_token.id){case'{':case'[':comments_off=true;json_mode=true;json_value();break;default:step_in(1);if(next_token.id===';'&&!node_js){next_token.edge=true;advance(';');}
tree=statements();begin.first=tree;itself.tree=begin;if(tree.disrupt){prev_token.warn('weird_program');}}}
indent=null;advance('(end)');itself.property=property;}catch(e){if(e){itself.errors.push({reason:e.message,line:e.line||next_token.line,character:e.character||next_token.from},null);}}
return itself.errors.length===0;};function unique(array){array=array.sort();var i,length=0,previous,value;for(i=0;i<array.length;i+=1){value=array[i];if(value!==previous){array[length]=value;previous=value;length+=1;}}
array.length=length;return array;}
itself.data=function(){var data={functions:[]},function_data,i,the_function,the_scope;data.errors=itself.errors;data.json=json_mode;data.global=unique(Object.keys(global_scope));function selects(name){var kind=the_scope[name].kind;switch(kind){case'var':case'exception':case'label':function_data[kind].push(name);break;}}
for(i=1;i<functions.length;i+=1){the_function=functions[i];function_data={name:the_function.name,line:the_function.line,level:the_function.level,parameter:the_function.parameter,var:[],exception:[],closure:unique(the_function.closure),outer:unique(the_function.outer),global:unique(the_function.global),label:[]};the_scope=the_function.scope;Object.keys(the_scope).forEach(selects);function_data.var.sort();function_data.exception.sort();function_data.label.sort();data.functions.push(function_data);}
data.tokens=tokens;return data;};itself.error_report=function(data){var evidence,i,output=[],warning;if(data.errors.length){if(data.json){output.push('<cite>JSON: bad.</cite><br>');}
for(i=0;i<data.errors.length;i+=1){warning=data.errors[i];if(warning){evidence=warning.evidence||'';output.push('<cite>');if(isFinite(warning.line)){output.push('<address>line '+
String(warning.line)+' character '+String(warning.character)+'</address>');}
output.push(warning.reason.entityify()+'</cite>');if(evidence){output.push('<pre>'+evidence.entityify()+'</pre>');}}}}
return output.join('');};itself.report=function(data){var dl,i,j,names,output=[],the_function;function detail(h,array){var comma_needed=false;if(array.length){output.push("<dt>"+h+"</dt><dd>");array.forEach(function(item){output.push((comma_needed?', ':'')+item);comma_needed=true;});output.push("</dd>");}}
output.push('<dl class=level0>');if(data.global.length){detail('global',data.global);dl=true;}else if(data.json){if(!data.errors.length){output.push("<dt>JSON: good.</dt>");}}else{output.push("<dt><i>No new global variables introduced.</i></dt>");}
if(dl){output.push("</dl>");}else{output[0]='';}
if(data.functions){for(i=0;i<data.functions.length;i+=1){the_function=data.functions[i];names=[];if(the_function.params){for(j=0;j<the_function.params.length;j+=1){names[j]=the_function.params[j].string;}}
output.push('<dl class=level'+the_function.level+'><address>line '+String(the_function.line)+'</address>'+the_function.name.entityify());detail('parameter',the_function.parameter);detail('variable',the_function.var);detail('exception',the_function.exception);detail('closure',the_function.closure);detail('outer',the_function.outer);detail('global',the_function.global);detail('label',the_function.label);output.push('</dl>');}}
return output.join('');};itself.properties_report=function(property){if(!property){return'';}
var i,key,keys=Object.keys(property).sort(),mem='   ',name,not_first=false,output=['/*properties'];for(i=0;i<keys.length;i+=1){key=keys[i];if(property[key]>0){if(not_first){mem+=',';}
name=ix.test(key)?key:'\''+key.replace(nx,sanitize)+'\'';if(mem.length+name.length>=80){output.push(mem);mem='    ';}else{mem+=' ';}
mem+=name;not_first=true;}}
output.push(mem,'*/\n');return output.join('\n');};itself.color=function(data){var from,i=1,level,line,result=[],thru,data_token=data.tokens[0];while(data_token&&data_token.id!=='(end)'){from=data_token.from;line=data_token.line;thru=data_token.thru;level=data_token.function.level;do{thru=data_token.thru;data_token=data.tokens[i];i+=1;}while(data_token&&data_token.line===line&&data_token.from-thru<5&&level===data_token.function.level);result.push({line:line,level:level,from:from,thru:thru});}
return result;};itself.jslint=itself;itself.edition='2014-07-08';return itself;}());var ADSAFE=(function(){"use strict";var adsafe_id,adsafe_lib,banned={'arguments':true,callee:true,caller:true,constructor:true,'eval':true,prototype:true,stack:true,unwatch:true,valueOf:true,watch:true},cache_style_object,cache_style_node,defaultView=document.defaultView,ephemeral,flipflop,has_focus,hunter,interceptors=[],makeableTagName={a:true,abbr:true,acronym:true,address:true,area:true,b:true,bdo:true,big:true,blockquote:true,br:true,button:true,canvas:true,caption:true,center:true,cite:true,code:true,col:true,colgroup:true,dd:true,del:true,dfn:true,dir:true,div:true,dl:true,dt:true,em:true,fieldset:true,font:true,form:true,h1:true,h2:true,h3:true,h4:true,h5:true,h6:true,hr:true,i:true,img:true,input:true,ins:true,kbd:true,label:true,legend:true,li:true,map:true,menu:true,object:true,ol:true,optgroup:true,option:true,p:true,pre:true,q:true,samp:true,select:true,small:true,span:true,strong:true,sub:true,sup:true,table:true,tbody:true,td:true,textarea:true,tfoot:true,th:true,thead:true,tr:true,tt:true,u:true,ul:true,'var':true},name,pecker,result,star,the_range,value;function error(message){ADSAFE.log("ADsafe error: "+(message||"ADsafe violation."));throw{name:"ADsafe",message:message||"ADsafe violation."};}
function string_check(string){if(typeof string!=='string'){error("ADsafe string violation.");}
return string;}
function owns(object,string){return object&&typeof object==='object'&&Object.prototype.hasOwnProperty.call(object,string_check(string));}
function reject_name(name){return typeof name!=='number'&&(typeof name!=='string'||banned[name]||name.charAt(0)==='_'||name.slice(-1)==='_');}
function reject_property(object,name){return typeof object!=='object'||reject_name(name);}
function reject_global(that){if(that.window){error();}}
function getStyleObject(node){if(node===cache_style_node){return cache_style_object;}
cache_style_node=node;cache_style_object=node.currentStyle||defaultView.getComputedStyle(node,'');return cache_style_object;}
function walkTheDOM(node,func,skip){if(!skip){func(node);}
node=node.firstChild;while(node){walkTheDOM(node,func);node=node.nextSibling;}}
function purge_event_handlers(node){walkTheDOM(node,function(node){if(node.tagName){node['___ on ___']=node.change=null;}});}
function parse_query(text,id){var match,query=[],selector,qx=id?/^\s*(?:([\*\/])|\[\s*([a-z][0-9a-z_\-]*)\s*(?:([!*~|$\^]?\=)\s*([0-9A-Za-z_\-*%&;.\/:!]+)\s*)?\]|#\s*([A-Z]+_[A-Z0-9]+)|:\s*([a-z]+)|([.&_>\+]?)\s*([a-z][0-9a-z\-]*))\s*/:/^\s*(?:([\*\/])|\[\s*([a-z][0-9a-z_\-]*)\s*(?:([!*~|$\^]?\=)\s*([0-9A-Za-z_\-*%&;.\/:!]+)\s*)?\]|#\s*([\-A-Za-z0-9_]+)|:\s*([a-z]+)|([.&_>\+]?)\s*([a-z][0-9a-z\-]*))\s*/;do{match=qx.exec(string_check(text));if(!match){error("ADsafe: Bad query:"+text);}
if(match[1]){selector={op:match[1]};}else if(match[2]){selector=match[3]?{op:'['+match[3],name:match[2],value:match[4]}:{op:'[',name:match[2]};}else if(match[5]){if(query.length>0||match[5].length<=id.length||match[5].slice(0,id.length)!==id){error("ADsafe: Bad query: "+text);}
selector={op:'#',name:match[5]};}else if(match[6]){selector={op:':'+match[6]};}else{selector={op:match[7],name:match[8]};}
query.push(selector);text=text.slice(match[0].length);}while(text);return query;}
hunter={'':function(node){var array,nodelist=node.getElementsByTagName(name),i,length;try{array=Array.prototype.slice.call(nodelist,0);result=result.length?result.concat(array):array;}catch(ie){length=nodelist.length;for(i=0;i<length;i+=1){result.push(nodelist[i]);}}},'+':function(node){node=node.nextSibling;name=name.toUpperCase();while(node&&!node.tagName){node=node.nextSibling;}
if(node&&node.tagName===name){result.push(node);}},'>':function(node){node=node.firstChild;name=name.toUpperCase();while(node){if(node.tagName===name){result.push(node);}
node=node.nextSibling;}},'#':function(){var n=document.getElementById(name);if(n.tagName){result.push(n);}},'/':function(node){var nodes=node.childNodes,i,length=nodes.length;for(i=0;i<length;i+=1){result.push(nodes[i]);}},'*':function(node){star=true;walkTheDOM(node,function(node){result.push(node);},true);}};pecker={'.':function(node){return(' '+node.className+' ').indexOf(' '+name+' ')>=0;},'&':function(node){return node.name===name;},'_':function(node){return node.type===name;},'[':function(node){return typeof node[name]==='string';},'[=':function(node){var member=node[name];return typeof member==='string'&&member===value;},'[!=':function(node){var member=node[name];return typeof member==='string'&&member!==value;},'[^=':function(node){var member=node[name];return typeof member==='string'&&member.slice(0,member.length)===value;},'[$=':function(node){var member=node[name];return typeof member==='string'&&member.slice(-member.length)===value;},'[*=':function(node){var member=node[name];return typeof member==='string'&&member.indexOf(value)>=0;},'[~=':function(node){var member=node[name];return typeof member==='string'&&(' '+member+' ').indexOf(' '+value+' ')>=0;},'[|=':function(node){var member=node[name];return typeof member==='string'&&('-'+member+'-').indexOf('-'+value+'-')>=0;},':blur':function(node){return node!==has_focus;},':checked':function(node){return node.checked;},':disabled':function(node){return node.tagName&&node.disabled;},':enabled':function(node){return node.tagName&&!node.disabled;},':even':function(node){var f;if(node.tagName){f=flipflop;flipflop=!flipflop;return f;}
return false;},':focus':function(node){return node===has_focus;},':hidden':function(node){return node.tagName&&getStyleObject(node).visibility!=='visible';},':odd':function(node){if(node.tagName){flipflop=!flipflop;return flipflop;}
return false;},':tag':function(node){return node.tagName;},':text':function(node){return node.nodeName==='#text';},':trim':function(node){return node.nodeName!=='#text'||/\W/.test(node.nodeValue);},':unchecked':function(node){return node.tagName&&!node.checked;},':visible':function(node){return node.tagName&&getStyleObject(node).visibility==='visible';}};function quest(query,nodes){var selector,func,i,j;for(i=0;i<query.length;i+=1){selector=query[i];name=selector.name;func=hunter[selector.op];if(typeof func==='function'){if(star){error("ADsafe: Query violation: *"+selector.op+
(selector.name||''));}
result=[];for(j=0;j<nodes.length;j+=1){func(nodes[j]);}}else{value=selector.value;flipflop=false;func=pecker[selector.op];if(typeof func!=='function'){switch(selector.op){case':first':result=nodes.slice(0,1);break;case':rest':result=nodes.slice(1);break;default:error('ADsafe: Query violation: :'+selector.op);}}else{result=[];for(j=0;j<nodes.length;j+=1){if(func(nodes[j])){result.push(nodes[j]);}}}}
nodes=result;}
return result;}
function make_root(root,id){if(id){if(root.tagName!=='DIV'){error('ADsafe: Bad node.');}}else{if(root.tagName!=='BODY'){error('ADsafe: Bad node.');}}
function Bunch(nodes){this.___nodes___=nodes;this.___star___=star&&nodes.length>1;star=false;}
var allow_focus=true,dom,dom_event=function(e){var key,target,that,the_event,the_target,the_actual_event=e||event,type=the_actual_event.type;the_target=the_actual_event.target||the_actual_event.srcElement;target=new Bunch([the_target]);that=target;switch(type){case'mousedown':allow_focus=true;if(document.selection){the_range=document.selection.createRange();}
break;case'focus':case'focusin':allow_focus=true;has_focus=the_target;the_actual_event.cancelBubble=false;type='focus';break;case'blur':case'focusout':allow_focus=false;has_focus=null;type='blur';break;case'keypress':allow_focus=true;has_focus=the_target;key=String.fromCharCode(the_actual_event.charCode||the_actual_event.keyCode);switch(key){case'\u000d':case'\u000a':type='enterkey';break;case'\u001b':type='escapekey';break;}
break;case'click':allow_focus=true;break;}
if(the_actual_event.cancelBubble&&the_actual_event.stopPropagation){the_actual_event.stopPropagation();}
the_event={altKey:the_actual_event.altKey,ctrlKey:the_actual_event.ctrlKey,bubble:function(){try{var parent=that.getParent(),b=parent.___nodes___[0];that=parent;the_event.that=that;if(b['___ on ___']&&b['___ on ___'][type]){that.fire(the_event);}else{the_event.bubble();}}catch(e){error(e);}},key:key,preventDefault:function(){if(the_actual_event.preventDefault){the_actual_event.preventDefault();}
the_actual_event.returnValue=false;},shiftKey:the_actual_event.shiftKey,target:target,that:that,type:type,x:the_actual_event.clientX,y:the_actual_event.clientY};if(the_target['___ on ___']&&the_target['___ on ___'][the_event.type]){target.fire(the_event);}else{for(;;){the_target=the_target.parentNode;if(!the_target){break;}
if(the_target['___ on ___']&&the_target['___ on ___'][the_event.type]){that=new Bunch([the_target]);the_event.that=that;that.fire(the_event);break;}
if(the_target['___adsafe root___']){break;}}}
if(the_event.type==='escapekey'){if(ephemeral){ephemeral.remove();}
ephemeral=null;}
that=the_target=the_event=the_actual_event=null;return;};root['___adsafe root___']='___adsafe root___';Bunch.prototype={append:function(appendage){reject_global(this);var b=this.___nodes___,flag=false,i,j,node,rep;if(b.length===0||!appendage){return this;}
if(appendage instanceof Array){if(appendage.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+
value.length);}
for(i=0;i<b.length;i+=1){rep=appendage[i].___nodes___;for(j=0;j<rep.length;j+=1){b[i].appendChild(rep[j]);}}}else{if(typeof appendage!=='string'){rep=appendage.___nodes___;}
for(i=0;i<b.length;i+=1){node=b[i];if(rep){for(j=0;j<rep.length;j+=1){node.appendChild(flag?rep[j].cloneNode(true):rep[j]);}
flag=true;}else{node.appendChild(document.createTextNode(appendage));}}}
return this;},blur:function(){reject_global(this);var b=this.___nodes___,i,node;has_focus=null;for(i=0;i<b.length;i+=1){node=b[i];if(node.blur){node.blur();}}
return this;},check:function(value){reject_global(this);var b=this.___nodes___,i,node;if(value instanceof Array){if(value.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+
value.length);}
for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.checked=!!value[i];}}}else{for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.checked=!!value;}}}
return this;},'class':function(value){reject_global(this);var b=this.___nodes___,i,node;if(value instanceof Array){if(value.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+
value.length);}
for(i=0;i<b.length;i+=1){if(/url/i.test(string_check(value[i]))){error('ADsafe error.');}
node=b[i];if(node.tagName){node.className=value[i];}}}else{if(/url/i.test(string_check(value))){error('ADsafe error.');}
for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.className=value;}}}
return this;},clone:function(deep,n){var a=[],b=this.___nodes___,c,i,j,k=n||1;for(i=0;i<k;i+=1){c=[];for(j=0;j<b.length;j+=1){c.push(b[j].cloneNode(deep));}
a.push(new Bunch(c));}
return n?a:a[0];},count:function(){reject_global(this);return this.___nodes___.length;},each:function(func){reject_global(this);var b=this.___nodes___,i;if(typeof func==='function'){for(i=0;i<b.length;i+=1){func(new Bunch([b[i]]));}
return this;}
error();},empty:function(){reject_global(this);var b=this.___nodes___,i,node;if(value instanceof Array){if(value.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+
value.length);}
for(i=0;i<b.length;i+=1){node=b[i];while(node.firstChild){purge_event_handlers(node);node.removeChild(node.firstChild);}}}else{for(i=0;i<b.length;i+=1){node=b[i];while(node.firstChild){purge_event_handlers(node);node.removeChild(node.firstChild);}}}
return this;},enable:function(enable){reject_global(this);var b=this.___nodes___,i,node;if(enable instanceof Array){if(enable.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+
enable.length);}
for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.disabled=!enable[i];}}}else{for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.disabled=!enable;}}}
return this;},ephemeral:function(){reject_global(this);if(ephemeral){ephemeral.remove();}
ephemeral=this;return this;},explode:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=new Bunch([b[i]]);}
return a;},fire:function(event){reject_global(this);var array,b,i,j,n,node,on,type;if(typeof event==='string'){type=event;event={type:type};}else if(typeof event==='object'){type=event.type;}else{error();}
b=this.___nodes___;n=b.length;for(i=0;i<n;i+=1){node=b[i];on=node['___ on ___'];if(owns(on,type)){array=on[type];for(j=0;j<array.length;j+=1){array[j].call(this,event);}}}
return this;},focus:function(){reject_global(this);var b=this.___nodes___;if(b.length>0&&allow_focus){has_focus=b[0].focus();return this;}
error();},fragment:function(){reject_global(this);return new Bunch([document.createDocumentFragment()]);},getCheck:function(){return this.getChecks()[0];},getChecks:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i].checked;}
return a;},getClass:function(){return this.getClasses()[0];},getClasses:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i].className;}
return a;},getMark:function(){return this.getMarks()[0];},getMarks:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i]['_adsafe mark_'];}
return a;},getName:function(){return this.getNames()[0];},getNames:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i].name;}
return a;},getOffsetHeight:function(){return this.getOffsetHeights()[0];},getOffsetHeights:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i].offsetHeight;}
return a;},getOffsetWidth:function(){return this.getOffsetWidths()[0];},getOffsetWidths:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i].offsetWidth;}
return a;},getParent:function(){reject_global(this);var a=[],b=this.___nodes___,i,n;for(i=0;i<b.length;i+=1){n=b[i].parentNode;if(n['___adsafe root___']){error('ADsafe parent violation.');}
a[i]=n;}
return new Bunch(a);},getSelection:function(){reject_global(this);var b=this.___nodes___,end,node,start,range;if(b.length===1&&allow_focus){node=b[0];if(typeof node.selectionStart==='number'){start=node.selectionStart;end=node.selectionEnd;return node.value.slice(start,end);}
range=node.createTextRange();range.expand('textedit');if(range.inRange(the_range)){return the_range.text;}}
return null;},getStyle:function(name){return this.getStyles(name)[0];},getStyles:function(name){reject_global(this);if(reject_name(name)){error("ADsafe style violation.");}
var a=[],b=this.___nodes___,i,node,s;for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){s=name!=='float'?getStyleObject(node)[name]:getStyleObject(node).cssFloat||getStyleObject(node).styleFloat;if(typeof s==='string'){a[i]=s;}}}
return a;},getTagName:function(){return this.getTagNames()[0];},getTagNames:function(){reject_global(this);var a=[],b=this.___nodes___,i,name;for(i=0;i<b.length;i+=1){name=b[i].tagName;a[i]=typeof name==='string'?name.toLowerCase():name;}
return a;},getTitle:function(){return this.getTitles()[0];},getTitles:function(){reject_global(this);var a=[],b=this.___nodes___,i;for(i=0;i<b.length;i+=1){a[i]=b[i].title;}
return a;},getValue:function(){return this.getValues()[0];},getValues:function(){reject_global(this);var a=[],b=this.___nodes___,i,node;for(i=0;i<b.length;i+=1){node=b[i];if(node.nodeName==='#text'){a[i]=node.nodeValue;}else if(node.tagName&&node.type!=='password'){a[i]=node.value;if(!a[i]&&node.firstChild&&node.firstChild.nodeName==='#text'){a[i]=node.firstChild.nodeValue;}}}
return a;},klass:function(value){return this['class'](value);},mark:function(value){reject_global(this);var b=this.___nodes___,i,node;if(value instanceof Array){if(value.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+
value.length);}
for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node['_adsafe mark_']=String(value[i]);}}}else{for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node['_adsafe mark_']=String(value);}}}
return this;},off:function(type){reject_global(this);var b=this.___nodes___,i,node;for(i=0;i<b.length;i+=1){node=b[i];if(typeof type==='string'){if(typeof node['___ on ___']){node['___ on ___'][type]=null;}}else{node['___ on ___']=null;}}
return this;},on:function(type,func){reject_global(this);if(typeof type!=='string'||typeof func!=='function'){error();}
var b=this.___nodes___,i,node,on,ontype;for(i=0;i<b.length;i+=1){node=b[i];if(type==='change'){ontype='on'+type;if(node[ontype]!==dom_event){node[ontype]=dom_event;}}
on=node['___ on ___'];if(!on){on={};node['___ on ___']=on;}
if(owns(on,type)){on[type].push(func);}else{on[type]=[func];}}
return this;},protect:function(){reject_global(this);var b=this.___nodes___,i;for(i=0;i<b.length;i+=1){b[i]['___adsafe root___']='___adsafe root___';}
return this;},q:function(text){reject_global(this);star=this.___star___;return new Bunch(quest(parse_query(string_check(text),id),this.___nodes___));},remove:function(){reject_global(this);this.replace();},replace:function(replacement){reject_global(this);var b=this.___nodes___,flag=false,i,j,newnode,node,parent,rep;if(b.length===0){return;}
for(i=0;i<b.length;i+=1){purge_event_handlers(b[i]);}
if(!replacement||replacement.length===0||(replacement.___nodes___&&replacement.___nodes___.length===0)){for(i=0;i<b.length;i+=1){node=b[i];purge_event_handlers(node);if(node.parentNode){node.parentNode.removeChild(node);}}}else if(replacement instanceof Array){if(replacement.length!==b.length){error('ADsafe: Array length: '+
b.length+'-'+value.length);}
for(i=0;i<b.length;i+=1){node=b[i];parent=node.parentNode;purge_event_handlers(node);if(parent){rep=replacement[i].___nodes___;if(rep.length>0){newnode=rep[0];parent.replaceChild(newnode,node);for(j=1;j<rep.length;j+=1){node=newnode;newnode=rep[j];parent.insertBefore(newnode,node.nextSibling);}}else{parent.removeChild(node);}}}}else{rep=replacement.___nodes___;for(i=0;i<b.length;i+=1){node=b[i];purge_event_handlers(node);parent=node.parentNode;if(parent){newnode=flag?rep[0].cloneNode(true):rep[0];parent.replaceChild(newnode,node);for(j=1;j<rep.length;j+=1){node=newnode;newnode=flag?rep[j].clone(true):rep[j];parent.insertBefore(newnode,node.nextSibling);}
flag=true;}}}
return this;},select:function(){reject_global(this);var b=this.___nodes___;if(b.length<1||!allow_focus){error();}
b[0].focus();b[0].select();return this;},selection:function(string){reject_global(this);string_check(string);var b=this.___nodes___,end,node,old,start,range;if(b.length===1&&allow_focus){node=b[0];if(typeof node.selectionStart==='number'){start=node.selectionStart;end=node.selectionEnd;old=node.value;node.value=old.slice(0,start)+string+old.slice(end);node.selectionStart=node.selectionEnd=start+
string.length;node.focus();}else{range=node.createTextRange();range.expand('textedit');if(range.inRange(the_range)){the_range.select();the_range.text=string;the_range.select();}}}
return this;},style:function(name,value){reject_global(this);if(reject_name(name)){error("ADsafe style violation.");}
if(value===undefined||/url/i.test(string_check(value))){error();}
var b=this.___nodes___,i,node,v;if(value instanceof Array){if(value.length!==b.length){error('ADsafe: Array length: '+
b.length+'-'+value.length);}
for(i=0;i<b.length;i+=1){node=b[i];v=string_check(value[i]);if(/url/i.test(v)){error();}
if(node.tagName){if(name!=='float'){node.style[name]=v;}else{node.style.cssFloat=node.style.styleFloat=v;}}}}else{v=string_check(value);if(/url/i.test(v)){error();}
for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){if(name!=='float'){node.style[name]=v;}else{node.style.cssFloat=node.style.styleFloat=v;}}}}
return this;},tag:function(tag,type,name){reject_global(this);var node;if(typeof tag!=='string'){error();}
if(makeableTagName[tag]!==true){error('ADsafe: Bad tag: '+tag);}
node=document.createElement(tag);if(name){node.autocomplete='off';node.name=string_check(name);}
if(type){node.type=string_check(type);}
return new Bunch([node]);},text:function(text){reject_global(this);var a,i;if(text instanceof Array){a=[];for(i=0;i<text.length;i+=1){a[i]=document.createTextNode(string_check(text[i]));}
return new Bunch(a);}
return new Bunch([document.createTextNode(string_check(text))]);},title:function(value){reject_global(this);var b=this.___nodes___,i,node;if(value instanceof Array){if(value.length!==b.length){error('ADsafe: Array length: '+b.length+'-'+value.length);}
for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.title=string_check(value[i]);}}}else{string_check(value);for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){node.title=value;}}}
return this;},value:function(value){reject_global(this);if(value===undefined){error();}
var b=this.___nodes___,i,node;if(value instanceof Array&&b.length===value.length){for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){if(node.type!=='password'){if(typeof node.value==='string'){node.value=value[i];}else{while(node.firstChild){purge_event_handlers(node.firstChild);node.removeChild(node.firstChild);}
node.appendChild(document.createTextNode(String(value[i])));}}}else if(node.nodeName==='#text'){node.nodeValue=String(value[i]);}}}else{value=String(value);for(i=0;i<b.length;i+=1){node=b[i];if(node.tagName){if(node.tagName!=='BUTTON'&&typeof node.value==='string'){node.value=value;}else{while(node.firstChild){purge_event_handlers(node.firstChild);node.removeChild(node.firstChild);}
node.appendChild(document.createTextNode(value));}}else if(node.nodeName==='#text'){node.nodeValue=value;}}}
return this;}};dom={append:function(bunch){var b=typeof bunch==='string'?[document.createTextNode(bunch)]:bunch.___nodes___,i,n;for(i=0;i<b.length;i+=1){n=b[i];if(typeof n==='string'||typeof n==='number'){n=document.createTextNode(String(n));}
root.appendChild(n);}
return dom;},combine:function(array){if(!array||!array.length){error('ADsafe: Bad combination.');}
var b=array[0].___nodes___,i;for(i=0;i<array.length;i+=1){b=b.concat(array[i].___nodes___);}
return new Bunch(b);},count:function(){return 1;},ephemeral:function(bunch){if(ephemeral){ephemeral.remove();}
ephemeral=bunch;return dom;},fragment:function(){return new Bunch([document.createDocumentFragment()]);},prepend:function(bunch){var b=bunch.___nodes___,i;for(i=0;i<b.length;i+=1){root.insertBefore(b[i],root.firstChild);}
return dom;},q:function(text){star=false;var query=parse_query(text,id);if(typeof hunter[query[0].op]!=='function'){error('ADsafe: Bad query: '+query[0]);}
return new Bunch(quest(query,[root]));},remove:function(){purge_event_handlers(root);root.parent.removeElement(root);root=null;},row:function(values){var tr=document.createElement('tr'),td,i;for(i=0;i<values.length;i+=1){td=document.createElement('td');td.appendChild(document.createTextNode(String(values[i])));tr.appendChild(td);}
return new Bunch([tr]);},tag:function(tag,type,name){var node;if(typeof tag!=='string'){error();}
if(makeableTagName[tag]!==true){error('ADsafe: Bad tag: '+tag);}
node=document.createElement(tag);if(name){node.autocomplete='off';node.name=name;}
if(type){node.type=type;}
return new Bunch([node]);},text:function(text){var a,i;if(text instanceof Array){a=[];for(i=0;i<text.length;i+=1){a[i]=document.createTextNode(string_check(text[i]));}
return new Bunch(a);}
return new Bunch([document.createTextNode(string_check(text))]);}};if(typeof root.addEventListener==='function'){root.addEventListener('focus',dom_event,true);root.addEventListener('blur',dom_event,true);root.addEventListener('mouseover',dom_event,true);root.addEventListener('mouseout',dom_event,true);root.addEventListener('mouseup',dom_event,true);root.addEventListener('mousedown',dom_event,true);root.addEventListener('mousemove',dom_event,true);root.addEventListener('click',dom_event,true);root.addEventListener('dblclick',dom_event,true);root.addEventListener('keypress',dom_event,true);}else{root.onfocusin=root.onfocusout=root.onmouseout=root.onmousedown=root.onmousemove=root.onmouseup=root.onmouseover=root.onclick=root.ondblclick=root.onkeypress=dom_event;}
return[dom,Bunch.prototype];}
function F(){}
return{create:function(o){reject_global(o);if(Object.hasOwnProperty('create')){return Object.create(o);}
F.prototype=typeof o==='object'&&o?o:Object.prototype;return new F();},get:function(object,name){reject_global(object);if(arguments.length===2&&!reject_property(object,name)){return object[name];}
error();},go:function(id,f){var dom,fun,root,i,scripts;if(adsafe_id&&adsafe_id!==id){error();}
root=document.getElementById(id);if(root.tagName!=='DIV'){error();}
adsafe_id=null;scripts=root.getElementsByTagName('script');i=scripts.length-1;if(i<0){error();}
do{root.removeChild(scripts[i]);i-=1;}while(i>=0);root=make_root(root,id);dom=root[0];for(i=0;i<interceptors.length;i+=1){fun=interceptors[i];if(typeof fun==='function'){try{fun(id,dom,adsafe_lib,root[1]);}catch(e1){ADSAFE.log(e1);}}}
try{f(dom,adsafe_lib);}catch(e2){ADSAFE.log(e2);}
root=null;adsafe_lib=null;},has:function(object,name){return owns(object,name);},id:function(id){if(adsafe_id){error();}
adsafe_id=id;adsafe_lib={};},isArray:Array.isArray||function(value){return Object.prototype.toString.apply(value)==='[object Array]';},keys:Object.keys||function(object){var key,result=[];for(key in object){if(owns(object,key)){result.push(key);}}
return result;},later:function(func,timeout){if(typeof func==='function'){setTimeout(func,timeout||0);}else{error();}},lib:function(name,f){if(!adsafe_id||reject_name(name)){error("ADsafe lib violation.");}
adsafe_lib[name]=f(adsafe_lib);},log:function log(s){if(window.console){console.log(s);}else if(typeof Debug==='object'){Debug.writeln(s);}else if(typeof opera==='opera'){opera.postError(s);}},remove:function(object,name){if(arguments.length===2&&!reject_property(object,name)){delete object[name];return;}
error();},set:function(object,name,value){reject_global(object);if(arguments.length===3&&!reject_property(object,name)){object[name]=value;return;}
error();},_intercept:function(f){interceptors.push(f);}};}());ADSAFE._intercept(function(id,dom,lib,bunch){'use strict';lib.cookie={get:function(){var c=' '+document.cookie+';',s=c.indexOf((' '+id+'=')),v;try{if(s>=0){s+=id.length+2;v=JSON.parse(c.slice(s,c.indexOf(';',s)));}}catch(ignore){}
return v;},set:function(value){var d,j=JSON.stringify(value).replace(/[=]/g,'\\u003d').replace(/[;]/g,'\\u003b');if(j.length<2000){d=new Date();d.setTime(d.getTime()+1e9);document.cookie=id+"="+j+';expires='+d.toGMTString();}}};});ADSAFE._intercept(function(id,dom,lib,bunch){'use strict';var now=Date.now||function(){return new Date().getTime();};if(id==='JSLINT_'){lib.jslint=function(source,options,errors,report,properties,edition){var after,before=now(),data,errtext,protext,retext;JSLINT(source,options);data=JSLINT.data();errtext=JSLINT.error_report(data);retext=JSLINT.report(data);protext=JSLINT.properties_report(JSLINT.property);after=now();edition.value(((after-before)/1000)+' seconds.');errors.___nodes___[0].innerHTML=errtext;report.___nodes___[0].innerHTML=retext;properties.value(protext);return errtext!=='';};lib.edition=function(){return JSLINT.edition;};}});
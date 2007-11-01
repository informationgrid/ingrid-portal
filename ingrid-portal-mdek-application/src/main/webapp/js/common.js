/*
 * Get parameter from url string
 */
function gup( name )
{
  name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
  var regexS = "[\\?&]"+name+"=([^&#]*)";
  var regex = new RegExp( regexS );
  var results = regex.exec( window.location.href );
  if( results == null )
    return "";
  else
    return results[1];
}


function toggleInfo(element)
{
  var toggleContent = document.getElementById(element + "Content");
  var arrow = document.getElementById(element).getElementsByTagName('img')[1];
  if (toggleContent.style.display == "block" || toggleContent.style.display == "")
  {
    toggleContent.style.display = "none";
    arrow.src = "img/ic_info_expand.gif";
  }
  else
  {
    toggleContent.style.display = "block";
    arrow.src = "img/ic_info_deflate.gif";
  }
}

/*
 * Toggle a checkbox list
 */
function toggleSelection(elementsBaseName)
{
  var btn = dojo.byId(elementsBaseName + "Btn");
  // check if the button is a link or a checkbox
  var isCheckBox = (btn.nodeName.toLowerCase() == 'input');

  var mode = false;
  if (btn.innerHTML.substring(0,4) == "Alle" || btn.checked == true)
    mode = true;
  
  var i = 0;
  var checkbox = dojo.byId(elementsBaseName + i);
  while (checkbox) {
    checkbox.checked = mode;
    checkbox = dojo.byId(elementsBaseName + (++i));
  }

  if (!isCheckBox) {
    if (mode == true)
      btn.innerHTML = "Keine ausw&auml;hlen";
    else
      btn.innerHTML = "Alle ausw&auml;hlen";
  }
}

/*
 * Toggle a table display
 */
function switchTableDisplay(onTableId, offTableId, /* boolean */isOn)
{
  if (isOn) {
    dojo.byId(onTableId).style.display = "block";
    dojo.byId(offTableId).style.display = "none";
  }
  else {
    dojo.byId(onTableId).style.display = "none";
    dojo.byId(offTableId).style.display = "block";
  }
}

function showResults()
{
  document.getElementById("results").style.display = "block";
}

function setEnabled(controlId, isEnabled)
{
  var control = dojo.widget.byId(controlId);
  if (control) {
    if (isEnabled)
      control.enable();
    else
      control.disable();
  }
}

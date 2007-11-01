dojo.addOnLoad(function()
{
  // initialite debug console if necessary
  if (djConfig.isDebug)
  {
    dojo.debug("The current version of dojo is: ", dojo.version.toString());
    var console = dojo.byId("dojoDebugConsole");
    console.style.visibility = "visible";
  }
  // hide search results
  if (dojo.byId("results"))
    dojo.byId("results").style.display = "none";
});

function toggleContent(contentArea, contentURL, searchField)
{
  var contentAreaNode = dojo.byId(contentArea);
  var searchFieldNode = dojo.byId(searchField);
  
  // expand arrow
  var arrow = dojo.byId(contentArea + "ToggleArrow");
  // toggle arrow and content
  if (arrow.src.indexOf("ic_info_expand.gif") != -1)
  {
    // load or toggle content for extended search
    contentPane = dojo.widget.byId(contentArea);
    if (contentPane.isLoaded == false)
    {
      // load content
      contentPane.setUrl(contentURL);
      // init sub navigation
      contentPane.addOnLoad(function(){
        if (dojo.byId('extContentObjContent'))
        {
          navInnerTab('objTopic', 0, 3);
          navInnerTab('objSpace', 0, 3);
          navInnerTab('objTime', 0, 1);
        }
        if (dojo.byId('extContentAddrContent'))
        {
          navInnerTab('addrTopic', 0, 2);
        }
      });
    }
    contentAreaNode.style.display = "block";
    arrow.src = "img/ic_info_deflate.gif";

    searchFieldNode.disabled = true;
		dojo.html.addClass(searchFieldNode, "noEdit");
  }
  else
  {
    contentAreaNode.style.display = "none";
    arrow.src = "img/ic_info_expand.gif";

    searchFieldNode.disabled = false;
		dojo.html.removeClass(searchFieldNode, "noEdit");
  }
  searchFieldNode.value = "";
}

function navInnerTab(sectionName, subSectionIndex, numSubSections)
{
  for (var i=0; i<numSubSections; i++)
  {
    if (i == subSectionIndex)
      dojo.byId(sectionName + i).style.display = "block";
    else
      dojo.byId(sectionName + i).style.display = "none";
  }
}

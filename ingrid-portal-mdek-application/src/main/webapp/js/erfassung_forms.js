dojo.addOnLoad(function()
{
  if (document.getElementById("linksLinkType1"))
  {
    document.getElementById("linksLinkType1").onclick = selectLinkType;
    document.getElementById("linksLinkType2").onclick = selectLinkType;
    selectLinkType();
  }
  // hide search results
  if (document.getElementById("results"))
    document.getElementById("results").style.display = "none";
});

function selectLinkType(e)
{
  // if (!e) var e = window.event

  if (this.id == "linksLinkType2")
  {
    document.getElementById("linkToObject").style.display = "none";
    document.getElementById("linkToURL").style.display = "block";
    document.getElementById("linksLinkType1").checked = false;
  }
  else
  {
    document.getElementById("linkToObject").style.display = "block";
    document.getElementById("linkToURL").style.display = "none";
    document.getElementById("linksLinkType2").checked = false;
  }
}

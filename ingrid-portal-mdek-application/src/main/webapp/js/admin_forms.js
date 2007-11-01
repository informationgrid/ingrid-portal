dojo.addOnLoad(function()
{
  if (document.getElementById("importType1"))
  {
    document.getElementById("importType1").onclick = selectImportType;
    document.getElementById("importType2").onclick = selectImportType;
    selectImportType();
  }
});

function selectImportType(e)
{
  // if (!e) var e = window.event
  if (document.getElementById("importType1"))
  {
    if (this.id == "importType2")
    {
      document.getElementById("importTree").style.display = "none";
      document.getElementById("importUniversal").style.visibility = "visible";
      document.getElementById("importUniversal").style.display = "block";
      document.getElementById("importType1").checked = false;
      document.getElementById("processInfo").style.top = 667 + "px";

      document.getElementById("analyzeBtn").style.visibility = "visible";
    }
    else if (this.id == "importType1")
    {
      document.getElementById("importTree").style.visibility = "visible";
      document.getElementById("importTree").style.display = "block";
      document.getElementById("importUniversal").style.display = "none";
      document.getElementById("importType2").checked = false;
      document.getElementById("processInfo").style.top = 667 + "px";

      document.getElementById("analyzeBtn").style.visibility = "hidden";
    }
    else
    {
      document.getElementById("importTree").style.visibility = "hidden";
      document.getElementById("importUniversal").style.visibility = "hidden";
      document.getElementById("processInfo").style.top = 167 + "px";

      document.getElementById("analyzeBtn").style.visibility = "hidden";
    }
  }
}

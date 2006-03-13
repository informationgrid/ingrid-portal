function ingrid_openWindow(section, winWidth, winHeight)
{
  popupWin = window.open(section+".html", "InternalWin", 'width=' + winWidth + ',height=' + winHeight + ',resizable=yes,scrollbars=yes,location=no,toolbar=yes');
  popupWin.focus();
}

function ingrid_contextHelp()
{
  openWindow('faq',750,550);
}

function ingrid_checkAll(group) {
  // NOTICE: first field in group has to be "checkAll" field
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

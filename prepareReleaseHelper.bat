@echo off
setlocal ENABLEDELAYEDEXPANSION

set /p q=1) Changes.xml updated?
set /p q=2) SQL files (Portal/MDEK) added to installer?
set /p q=3) Patches will be generated now. Please update build-patch.xml if changes?
echo

svn list https://213.144.28.209:444/svn/ingrid/ingrid-portal/tags/
set /p tag="Enter tag-version of the last release (e.g. 3.3.0): "

set COUNT=0
set svnCall=svn log --limit 1 https://213.144.28.209:444/svn/ingrid/ingrid-portal/tags/ingrid-portal-project-%tag%
for /f %%a in ('%svnCall%') do (
    set /A COUNT=!COUNT! + 1
    if !COUNT! == 2 ( set rev=%%a )
)

REM trim spaces to right (work around)
set rev=%rev%##
set rev=%rev: ##=##%
set rev=%rev:##=%
echo Using revision: %rev%
svn diff -r %rev%:HEAD ingrid-portal-apps\src\java\ingrid-portal-apps.properties > ingrid-portal-apps.properties.patch
svn diff -r %rev%:HEAD ../../ingrid-webmap-client/trunk/src/main/resources/ingrid_webmap_client_config.xml > ingrid_webmap_client_config.xml.patch
svn diff -r %rev%:HEAD ingrid-portal-mdek-application/src/main/resources/default-datasource.properties > default-datasource.properties.patch
svn diff -r %rev%:HEAD ingrid-portal-mdek-application/src/main/resources/mdek.properties > mdek.properties.patch
svn diff -r %rev%:HEAD ingrid-portal-mdek-application/src/main/webapp/WEB-INF/configurations.xml > configurations.xml.patch

echo Patches have been generated. Please copy them into the patch dir of the installer!
pause
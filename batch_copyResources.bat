REM This script copies all generic resource files to the English resource files.
REM So there's only the need in maintaining one file instead of two!

echo "press CTRL+C to abort"
PAUSE

@echo off & setlocal enabledelayedexpansion
for /r "ingrid-portal-base" %%x in (*Resources.properties) do @(
    set tempFile=%%x
    copy %%x !tempFile:~0,-11%!_en.properties
)

for /r "ingrid-portal-apps" %%x in (*Resources.properties) do @(
    set tempFile=%%x
    copy %%x !tempFile:~0,-11%!_en.properties
)

REM Copy resource files to PortalU-Profile!

REM ============= BASE ================
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid\decorator-macros.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid\footer.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid\header.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid\css\styles.css ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid\css

copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-help\decorator-macros.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-help
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-help\header.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-help
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-help\css\styles.css ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-help\css

copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-popup\footer.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-popup
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-popup\header.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-popup
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-popup\css\styles.css ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-popup\css
copy ingrid-portal-base\src\webapp\decorations\layout\ingrid-popup\css\print.css ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\layout\ingrid-popup\css

copy ingrid-portal-base\src\webapp\decorations\portlet\ingrid-marginal-teaser\decorator.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\portlet\ingrid-marginal-teaser
copy ingrid-portal-base\src\webapp\decorations\portlet\ingrid-marginal-teaser\css\styles.css ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\portlet\ingrid-marginal-teaser\css

copy ingrid-portal-base\src\webapp\decorations\portlet\ingrid-teaser\decorator.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\portlet\ingrid-teaser
copy ingrid-portal-base\src\webapp\decorations\portlet\ingrid-teaser\css\styles.css ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\decorations\portlet\ingrid-teaser\css

copy ingrid-portal-base\src\java\de\ingrid\portal\resources\PortalLayoutResources*.properties ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal\WEB-INF\classes\de\ingrid\portal\resources

REM ============= APPS ================
copy ingrid-portal-apps\src\webapp\dwd_warnmodul\wmDWDconfig.cfg ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\dwd_warnmodul
copy ingrid-portal-apps\src\webapp\WEB-INF\portlet.xml ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF
REM copy ingrid-portal-apps\src\webapp\WEB-INF\classes\ingrid-portal-apps.properties ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\classes
copy ingrid-portal-apps\src\java\ingrid-portal-help*.xml ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\classes
copy ingrid-portal-apps\src\java\quartz_jobs.xml ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\classes
REM copy ingrid-portal-apps\src\java\wms_interface.properties ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\classes

copy ingrid-portal-apps\src\java\de\ingrid\portal\resources\SearchCatalogResources*.properties ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\classes\de\ingrid\portal\resources
copy ingrid-portal-apps\src\java\de\ingrid\portal\resources\ShowPartnerPortletResources*.properties ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\classes\de\ingrid\portal\resources

copy ingrid-portal-apps\src\webapp\WEB-INF\templates\about_portalu_links.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\templates
copy ingrid-portal-apps\src\webapp\WEB-INF\templates\search_detail_footer.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\templates
copy ingrid-portal-apps\src\webapp\WEB-INF\templates\search_detail_header.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\templates
copy ingrid-portal-apps\src\webapp\WEB-INF\velocity\velocity-macros.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-apps\WEB-INF\velocity

REM ============= MDEK ================
copy ingrid-portal-mdek\src\webapp\WEB-INF\velocity\velocity-macros.vm ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-mdek\WEB-INF\velocity

REM ============= MDEK-APPLICATION ================
copy  ingrid-portal-mdek-application\src\main\resources\mdek-application-help*.xml ingrid-portal-apps\src\webapp\profiles\portalu\ingrid-portal-mdek-application\WEB-INF\classes
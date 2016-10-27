/*
 * **************************************************-
 * InGrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
// Default
var settingDefaultTopicId = 'themen';
var settingExtent = undefined;
var settingEpsgExtent = undefined;
var settingEpsg = undefined;
var settingShortURLService = undefined;
var settingSearchServiceUrl = 'http://dev.informationgrid.eu/opensearch/query?q={query}+t011_obj_serv_op_connpoint.connect_point:http*+t011_obj_serv.type:view+cache:off+datatype:metadata+ranking:score%26ingrid=1%26h=100';
var settingSearchNominatimUrl = 'http://nominatim.openstreetmap.org/search?format=json%26countrycodes=de';
var settingSearchBwaLocatorUrl = 'https://atlas.wsv.bund.de/bwastr-locator/rest/bwastrinfo/query?limit=200%26searchfield=all';
var settingSearchBwaLocatorGeoUrl = 'https://atlas.wsv.bund.de/bwastr-locator/rest/geokodierung/query';
var settingSearchBwaLocatorStationUrl = 'https://atlas.wsv.bund.de/bwastr-locator/rest/stationierung/query';
var settingCopyrightURL = undefined;
var settingSitemapURL = undefined;
var settingUseGeodesic = true;
var settingDefaultMousePositionIndex = 0;
// KML
var settingKMLName = 'INGRID';
// WMS Import
var settingDefaultWMSList = [
     'http://atlas.wsv.bund.de/bwastr/wms?',
     'http://gdi.uba.de/arcgis/services/BoFlLa/WMS_UBA_BoFlLa_NitratGrundWa_MST/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/Wa/WMS_UBA_Wa_FlNetz/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/BoFlLa/WMS_UBA_BoFlLa_CLL_UeberS/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/PrKo/WMS_UBA_PrKo_GFA/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/Wa/WMS_UBA_Wa_GWL/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/GeobD/WMS_UBA_GeobD_DE_DGM/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/Wa/WMS_UBA_Wa_NitratOberflWa/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/BoFlLa/WMS_UBA_BoFlLa_CLL_UeberS/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/Wa/WMS_UBA_Wa_GSK/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/Wa/WMS_UBA_Wa_MST/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/Wa/WMS_UBA_Wa_Seetyp/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/BoFlLa/WMS_UBA_BoFlLa_WaldBrRisiko/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/FDaten/WMS_UBA_FDaten_OekologRaumGl/MapServer/WMSServer?',
     'http://gdi.uba.de/arcgis/services/KlEn/WMS_UBA_KlEn_Kraftw/MapServer/WMSServer?',
     'http://wms.geo.admin.ch/'
   ];
// Share
var settingShareFacebook = true;
var settingShareMail = true;
var settingShareGoogle = true;
var settingShareTwitter = true;
var settingShareIFrame = true;
var settingShareLink = true;
var settingPrintLogo = location.protocol + '//' + location.host + '/ingrid-webmap-client/frontend/prd/img/print_logo.png';
var settingPrintNorthArrow = location.protocol + '//' + location.host + '/ingrid-webmap-client/frontend/prd/img/north_arrow.png';
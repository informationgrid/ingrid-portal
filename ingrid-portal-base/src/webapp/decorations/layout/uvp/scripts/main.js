$(function() {

  $(document).foundation();

  initSelect2();

  //show and hide header menu
  $('.header-menu-open button').click(function() {
    $('.header-menu').show();
  });
  $('.header-menu-close button').click(function() {
    $('.header-menu').hide();
  });

  //custom scrollbar in contact form textarea
  $('.custom-scrollbar').niceScroll({
    cursorcolor:"#c6cfe2",
    cursorwidth:"8px",
    cursorborderradius: "6px",
    railpadding: { top: 25, right: 0, left: 0, bottom: 5 }
  });

  //OpenStreetMaps with leaflet
  //vorhaben home oberer div
  if ($('#map-id-1').length) {
    var mymap1 = L.map('map-id-1').setView([49.86147, 8.68290], 17);
    L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      subdomains: ['a','b','c']
    }).addTo(mymap1);
  }
  //vorhaben home unterer div
  if ($('#map-id-2').length) {
    var mymap2 = L.map('map-id-2').setView([49.86147, 8.68290], 17);
    L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      subdomains: ['a','b','c']
    }).addTo(mymap2);
  }
  //filter search mobile div
  if ($('#map-id-3').length) {
    var mymap3 = L.map('map-id-3').setView([49.275, 8.026], 7);
    L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      subdomains: ['a','b','c']
    }).addTo(mymap3);
  }
  //filter search desktop div
  if ($('#map-id-4').length) {
    var mymap4 = L.map('map-id-4').setView([49.275, 8.026], 7);
    L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      subdomains: ['a','b','c']
    }).addTo(mymap4);
  }

  //large map in blocks
  if ($('#map-id-5').length) {
    var mymap5 = L.map('map-id-5').setView([50.702, 11.995], 7);
    L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      subdomains: ['a','b','c']
    }).addTo(mymap5);
  }

  //custom marker icon
  var myIcon = L.icon({
    iconUrl: '/assets/styles/vendor/leaflet/images/blau@2x.png',
    iconSize: [44, 48],
    iconAnchor: [17, 49],
    popupAnchor: [0, -42]
  });
  //single marker with popups
  if (mymap5) {
    var marker = L.marker([51.34, 12.36], {icon: myIcon}).addTo(mymap5);
    marker.bindPopup("<h5>Errichtung und Betrieb von sechs Windkraftanlagen in 17291 Nordwestuckermark - Reg.-Nr. G05616, G05816 und G00417</h5><div class='helper icon'><span class='ic-ic-road'></span><span>Wärmeerzeugung, Bergbau, Energie</span></div><h6 class='no-margin'>Letzter Verfahrensschritt</h6><p>Öffentliche Auslegung</p>", {maxWidth: 380}).openPopup();
    var marker1 = L.marker([49.4, 13.8], {icon: myIcon}).addTo(mymap5);
    marker1.bindPopup("<h5>Errichtung und Betrieb von sechs Windkraftanlagen in 17291 Nordwestuckermark - Reg.-Nr. G05616, G05816 und G00417</h5><div class='helper icon'><span class='ic-ic-road'></span><span>Wärmeerzeugung, Bergbau, Energie</span></div><div class='helper icon check'><span class='ic-ic-check'></span><span>Verfahren abgeschlossen</span></div>", {maxWidth: 380});
  }
  //marker groups
  if (mymap5) {
    var markers = L.markerClusterGroup();
    markers.addLayer(L.marker([51.508, 9.11], {icon: myIcon}));
    markers.addLayer(L.marker([51.43, 9.2], {icon: myIcon}));
    markers.addLayer(L.marker([51.45, 9.4], {icon: myIcon}));
    mymap5.addLayer(markers);
    var markers1 = L.markerClusterGroup();
    markers1.addLayer(L.marker([50.01, 8.64], {icon: myIcon}));
    markers1.addLayer(L.marker([49.81, 8.44], {icon: myIcon}));
    markers1.addLayer(L.marker([50.101, 8.31], {icon: myIcon}));
    markers1.addLayer(L.marker([49.91, 8.54], {icon: myIcon}));
    markers1.addLayer(L.marker([49.781, 8.14], {icon: myIcon}));
    mymap5.addLayer(markers1);
  }

});

$(window).resize(function() {
  initSelect2();
});

function initSelect2() {
  $('select.select2').select2({
    minimumResultsForSearch: Infinity, // no search box for single selects
  });

  $('select.location_select').select2({
    placeholder: "PLZ oder Ort eingeben",
  });
}

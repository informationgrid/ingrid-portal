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

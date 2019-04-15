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

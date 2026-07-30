$(function(){

    var checkExpandTableBox = function(){
        $('.js-expand-table-box').each(function(index) {
            var expanderBox = $(this);
            var openButton = expanderBox.find('.js-open-expand-table');
            var closeButton = expanderBox.find('.js-close-expand-table');
            var isActive = false;

            if(expanderBox.hasClass("is-active")){
                isActive = true;
            }

            closeButton.addClass('is-hidden');
            openButton.addClass('is-hidden');

            if(isActive){
                expanderBox.addClass('is-expand');
                closeButton.removeClass('is-hidden');
            } else {
                openButton.removeClass('is-hidden');
            }

            openButton.click(function() {
                $(this).addClass('is-hidden');
                expanderBox.addClass('is-active');
                expanderBox.addClass('is-expand');
                closeButton.removeClass('is-hidden');
                expanderBox.find('tr.js-expand-table.is-hidden').removeClass('is-hidden');
            });

            closeButton.click(function() {
                $(this).addClass('is-hidden');
                expanderBox.removeClass('is-active');
                expanderBox.removeClass('is-expand');
                openButton.removeClass('is-hidden');
                expanderBox.find('tr.js-expand-table').addClass('is-hidden');
            });
        });
    };
    checkExpandTableBox();
    $(window).resize(function(){
        checkExpandTableBox();
    });
});

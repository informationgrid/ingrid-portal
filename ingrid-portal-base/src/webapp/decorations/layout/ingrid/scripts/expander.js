var expander = (function () {

    "use strict";

    var expander_open = null;
    var expander_close = null;
    var expander_content = null;
    
    function expand(ident) {
        if(ident){
            expander_content = $('.js-expander-content.' + ident);
            expander_content.removeClass('is-hidden');
            expander_close = $('.js-expander-close.' + ident);
            expander_close.removeClass('is-hidden');
            expander_open = $('#' + ident +'.js-expander');
            expander_open.addClass('is-hidden');
        }
    }

    function close() {
        expander_content.addClass('is-hidden');
        expander_close.addClass('is-hidden');
        expander_open.removeClass('is-hidden');
    }

    return {
        open: expand,
        close: close
    }

})();


$('.js-expander').on('click', function (event) {
    event.preventDefault();
    expander.open(this.id);
});

$('.js-expander-close').on('click', function (event) {
    event.preventDefault();
    expander.close();
});

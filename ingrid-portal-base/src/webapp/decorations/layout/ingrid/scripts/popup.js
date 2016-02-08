var popup = (function () {

    "use strict";

    var is_open = false;

    var box = $('.popup');
    var title = $('.popup__title');
    var content = $('.popup__content');

    function open(message) {

        if (message === "" || message === undefined) {
            return;
        }

        update(message);

        box.addClass('is-open');

    }

    function close() {

        is_open = false;

        box.removeClass('is-open');

    }

    function update(message) {
        content.html(message);
    }

    return {
        open: open,
        close: close
    }

})();


$('.js-popup').on('click', function (event) {
    event.preventDefault();
    popup.open($(this).data('content'));
});

$('.js-popup-close').on('click', function (event) {
    event.preventDefault();
    popup.close();
});

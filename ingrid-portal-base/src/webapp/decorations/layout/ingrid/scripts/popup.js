var popup = (function () {

    "use strict";

    var is_open = false;

    var box = $('.popup');
    var title = $('.popup__title');
    var content = $('.popup__content');

    function open(messageTitle, message) {

        if (message === "" || message === undefined) {
            return;
        }

        update(messageTitle, message);

        box.addClass('is-open');

    }

    function close() {

        is_open = false;

        box.removeClass('is-open');

    }

    function update(messageTitle, message) {
        title.html(messageTitle);
        content.html(message);
    }

    return {
        open: open,
        close: close
    }

})();


$('.js-popup').on('click', function (event) {
    event.preventDefault();
    popup.open($(this).data('title'), $(this).data('content'));
});

$('.js-popup-close').on('click', function (event) {
    event.preventDefault();
    popup.close();
});

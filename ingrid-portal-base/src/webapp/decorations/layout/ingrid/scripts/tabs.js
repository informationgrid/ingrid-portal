(function () {

    "use strict";

    $.fn.tabs = function (options) {

        return this.each(function () {

            //var activeIndex = 0;

            var activeTab = $(this).find('.tab__toggle.is-active');

            var select = $(this).find('.tab__select');
            var select_text = $(this).find('.tab__select__text');
            var toggles = $(this).find('.tab__toggle');
            var tabs = $(this).find('.tab__content');


            toggles.on('click', function (event) {

                event.preventDefault();
                event.stopPropagation();

                if (activeTab == $(this)) {
                    return;
                }

                var id = this.getAttribute('data-tabid');

                $(select_text[0]).text(this.textContent);

                if (activeTab !== null) {
                    $(activeTab).removeClass('is-active');
                    $('.tab__content[data-tabid="' + $(activeTab).attr('data-tabid') + '"]').addClass('is-hidden');
                }

                $(this).addClass('is-active');
                $('.tab__content[data-tabid="' + id + '"]').removeClass('is-hidden');

                activeTab = $(this);

            });

            select.on('click', function (event) {

                event.preventDefault();
                event.stopPropagation();

                $(this).toggleClass('is-active');

            });

        });
    }

})();

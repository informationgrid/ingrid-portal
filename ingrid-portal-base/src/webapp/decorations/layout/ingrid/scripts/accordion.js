(function () {

    "use strict";

    $.fn.accordion = function (options) {

        var defaults = {
            multi: false
        }

        return this.each(function () {

            var settings = $.extend(defaults, options);

            var accordion = this;
            var toggle = $(this).children('.js-accordion-toggle');
            var content = $(this).children('.js-accordion-content');
            var ui = $(this).find('.js-accordion-ui'); // <-- Not full proof when nesting accordions

            toggle.on('click', function (event) {

                event.preventDefault();

                if (!settings.multi) {

                    var group = accordion.getAttribute('data-accordion-group');

                    if (group !== null && group !== "") {

                        $('[data-accordion-group]').not(accordion).each(function () {

                            $(this).children('.js-accordion-toggle').removeClass('is-active');
                            $(this).children('.js-accordion-content').addClass('is-hidden');

                        });

                    }

                }

                toggle.toggleClass('is-active');
                content.toggleClass('is-hidden');
                ui.toggleClass('is-hidden');

            });

        });
    }

})();

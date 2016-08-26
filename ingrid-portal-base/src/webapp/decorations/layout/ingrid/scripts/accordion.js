/*
 * **************************************************-
 * InGrid Portal Base
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

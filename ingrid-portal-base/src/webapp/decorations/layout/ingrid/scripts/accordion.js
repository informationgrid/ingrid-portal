/*
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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

            if(accordion.tagName === 'DIV') {
                if(toggle.hasClass("is-active")) {
                  content.attr('aria-expanded', 'true');
                  content.attr('aria-hidden', 'false');
                } else {
                  content.attr('aria-expanded', 'false');
                  content.attr('aria-hidden', 'true');
                }
            }
            if(toggle.attr('id')) {
              content.attr('aria-labelledby', toggle.attr('id'));
            }

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
                if(toggle.hasClass("is-active")) {
                  toggle.removeClass('is-active');
                  if(accordion.tagName === 'DIV') {
                    content.attr('aria-expanded', 'false');
                    content.attr('aria-hidden', 'true');
                  }
                } else {
                  toggle.addClass('is-active');
                  if(accordion.tagName === 'DIV') {
                    content.attr('aria-expanded', 'true');
                    content.attr('aria-hidden', 'false');
                  }
                }
                
                content.toggleClass('is-hidden');
                ui.toggleClass('is-hidden');

            });

        });
    }

})();

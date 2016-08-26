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

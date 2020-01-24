/*
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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

    $.fn.selection = function (options) {

        return this.each(function () {

            var activeOption = $(this).find('.selection-options.is-active');
            var options = $(this).find('.selection-options');

            $('#' + this.id).on('change', function (event) {

                event.preventDefault();
                event.stopPropagation();

                if (activeOption == $(this)) {
                    return;
                }
                
                if (activeOption !== null) {
                    $(activeOption).removeClass('is-active');
                    $('.selection-content[for="' + $(activeOption).attr('id') + '"]').addClass('is-hidden');
                }
                
                var id = this.getAttribute('id');
                var selectedOption = $('option:selected', this);
                selectedOption.addClass('is-active');
                $('.selection-content[for="' + $(selectedOption).attr('id') + '"]').removeClass('is-hidden');
                
                activeOption = selectedOption;
            });
        });
    }

})();

/*
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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

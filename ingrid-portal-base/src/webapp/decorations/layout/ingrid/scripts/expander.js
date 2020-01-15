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

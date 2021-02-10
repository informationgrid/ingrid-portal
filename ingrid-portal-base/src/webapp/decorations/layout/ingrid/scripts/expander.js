/*
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
            updateParams("more", ident);
        }
    }

    function close(ident) {
        if(ident){
          expander_content = $('.js-expander-content.' + ident);
          expander_close = $('.js-expander-close.' + ident);
          expander_open = $('#' + ident +'.js-expander');
          updateParams("more", ident);
        }
        expander_content.addClass('is-hidden');
        expander_close.addClass('is-hidden');
        expander_open.removeClass('is-hidden');
    }

    function updateParams(key, id) {
      var queryParams = new URLSearchParams(window.location.search);
      var paramKey = queryParams.get(key);
      if(paramKey) {
        var paramKeys = paramKey.split('|');
        if(paramKeys.includes(id)){
          paramKeys = paramKeys.filter(e => e !== id);
        } else {
          paramKeys.push(id);
        }
        paramKey = ''
        for (var i = 0; i < paramKeys.length; i++) {
          if(i > 0) {
            paramKey = paramKey + '|';
          }
          paramKey = paramKey + paramKeys[i];
        }
      } else {
        paramKey = id
      }
      if(paramKey === '') {
        queryParams.delete(key);
      } else {
        queryParams.set(key, paramKey);
      }
      history.replaceState(null, null, "?"+queryParams.toString());
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
    var id = $(event.currentTarget).prevUntil(event.currentTarget,".js-expander")[0].id;
    expander.close(id);
});


$(function(){

    $('.js-expand-box').removeClass('js-non-expand-text');

    var wrapHeight = $('.js-expand-box .js-expand-text-content').height();
    var descHeight = $('.js-expand-box').height();

    if (wrapHeight <= descHeight) {
        $('.js-expand-box .js-expand-text-fade').addClass('is-hidden');
    } else {
        $('.js-expand-box ~ .js-open-expand-text').removeClass('is-hidden');
    }

    $('.js-expand-box ~ .js-open-expand-text').click(function() {
        $(this).addClass('is-hidden');
        $('.js-expand-box').addClass('is-active');
        $('.js-expand-box ~ .js-close-expand-text').removeClass('is-hidden');
        $('.js-expand-box .js-expand-text-fade').addClass('is-hidden');
    });

    $('.js-expand-box ~ .js-close-expand-text').click(function() {
        $(this).addClass('is-hidden');
        $('.js-expand-box').removeClass('is-active');
        $('.js-expand-box ~ .js-open-expand-text').removeClass('is-hidden');
        $('.js-expand-box .js-expand-text-fade').removeClass('is-hidden');
    });
});

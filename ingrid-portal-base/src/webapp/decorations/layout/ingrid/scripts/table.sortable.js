/*
 * **************************************************-
 * InGrid Portal Base
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

    $.fn.sortable = function (options) {

        return this.each(function () {

            var tbody = $(this).find('tbody');
            var rows = tbody.find('.js-row-sortable');
            var newRows;
            var isAscending;

            var toggles = $(this).find('.js-column-sortable');

            toggles.on('click', function (event) {
                if ($(this).hasClass('toggle-ignore') == false) {
                    toggles.not(this).removeClass('is-active is-asc is-desc')
    
                    if ($(this).hasClass('is-desc')) {
    
                        $(this).removeClass('is-desc').addClass('is-active is-asc');
                        isAscending = true;
    
                    }
                    else {
    
                        $(this).removeClass('is-asc').addClass('is-active is-desc');
                        isAscending = false;
    
                    }
    
                    var index = $(this).index();
                    var columnData = [];
                    newRows = [];
    
                    for (var i = 0, j = rows.length; i < j; i++) {
    
                        var cells = $(rows[i]).find('td');
    
                        columnData.push($(cells[index]).text());
    
                    }
    
                    columnData.sort();
    
                    if (isAscending) {
    
                        for (var i = -1, j = columnData.length; i < j; --j) {
    
                            for (var k = 0, l = rows.length; k < l; k++) {
    
                                if ($(rows[k]).find('td').eq(index).text() === columnData[j]) {
    
                                    newRows.push(rows[k]);
    
                                }
    
                            }
    
                        }
    
                    }
    
                    else {
    
                        for (var i = 0, j = columnData.length; i < j; i++) {
    
                            for (var k = 0, l = rows.length; k < l; k++) {
    
                                if ($(rows[k]).find('td').eq(index).text() === columnData[i]) {
    
                                    newRows.push(rows[k]);
    
                                }
    
                            }
    
                        }
    
                    }
    
                    rows.remove();
    
                    tbody.append(newRows);
                }
            });

        });
    }

})();

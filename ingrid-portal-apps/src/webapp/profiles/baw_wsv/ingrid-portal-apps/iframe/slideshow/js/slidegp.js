/*
 * **************************************************-
 * Ingrid Portal Apps
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
window.addEvent('domready',function() {
  /* settings */
  var showDuration = 3000;
  var container = $('slideshow-container');
  var images = container.getElements('img');
  var currentIndex = 0;
  var interval;
  /* opacity and fade */
  images.each(function(img,i){ 
    if(i > 0) {
      img.set('opacity',0);
    }
  });
  /* worker */
  var show = function() {
    images[currentIndex].fade('out');
    images[currentIndex = currentIndex < images.length - 1 ? currentIndex+1 : 0].fade('in');
  };
  /* start once the page is finished loading */
  window.addEvent('load',function(){
    interval = show.periodical(showDuration);
  });
});

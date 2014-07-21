
define([
        "dojo/_base/declare",
        "dojo/string",
        "ingrid/message"
    ], function(declare, string, message){
		return declare(null, {
			
			 
			/**
			 * Indirect Page Navigation obj
			 * DEPRECATED
			 *  The purpose of this object is to display a simple page navigation for a given result set.
			 *  It is initialised with the number of displayed results per page and the info/paging span where
			 *  the navigation should be displayed. 
			 *  To display/update the nav bar, use setTotalNumHits to set the number of hits that should be navigated.
			 *  When the user selects a page, the 'onPageSelected' function is called with the selected page number as argument.
			 */
			IndirectPageNavigation: function(args) {
				this.resultsPerPage = args.resultsPerPage;
				this.infoSpan = args.infoSpan;
				this.pagingSpan = args.pagingSpan;

				this.startHit = 0;
				this.totalNumHits = 0;
				this.pageSelectorStart = 1;

				this.reset = function() {
					this.startHit = 0;
					this.totalNumHits = 0;
					this.pageSelectorStart = 1;
				};

				this.setTotalNumHits = function(numHits) {
					this.totalNumHits = numHits;
				};

				this.getStartHit = function() {
					return this.startHit;
				};

				this.updateDomNodes = function() {
					var _this = this;
					var totalNumHits = this.totalNumHits;
					var startHit = this.startHit;
					var endHit = startHit+this.resultsPerPage-1;

					if (totalNumHits <= endHit)
						endHit = totalNumHits - 1;
				
					var currentPage = Math.floor(startHit/this.resultsPerPage) + 1;
					var lastPage = Math.ceil(totalNumHits/this.resultsPerPage);
				
					// Update the dom elements
					if (totalNumHits === 0) {
						this.infoSpan.innerHTML = message.get("ui.pageNav.zeroHits");
					} else if (totalNumHits == 1) {
						this.infoSpan.innerHTML = string.substitute(message.get("ui.pageNav.beginEndTotalHit"), [startHit+1, endHit+1, totalNumHits]);
					} else {
						this.infoSpan.innerHTML = string.substitute(message.get("ui.pageNav.beginEndTotalHits"), [startHit+1, endHit+1, totalNumHits]);
					}
				
					// Clear the paging span
					while (this.pagingSpan.hasChildNodes())
						this.pagingSpan.removeChild(this.pagingSpan.firstChild);
				
					// Add info text to the paging span
					if (totalNumHits === 0) {
						this.pagingSpan.appendChild(document.createTextNode(string.substitute(message.get("ui.pageNav.currentToLastPages"), [1, 1])));
					} else {
						this.pagingSpan.appendChild(document.createTextNode(string.substitute(message.get("ui.pageNav.currentToLastPages"), [currentPage, lastPage])));
					}
				
					// Add navigation to the next set of page navigation links
					if (this.pageSelectorStart > 1) {
						// Add '<<' jump to page 1
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.onclick = function() {
							_this.pageSelectorStart = 1;
							_this.updateDomNodes();
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.firstPage"));
						link.innerHTML = "&lt;&lt;";

						this.pagingSpan.appendChild(link);

						// Add '<' jump to currentPage - 4
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.onclick = function() {
							_this.pageSelectorStart -= 4;
							_this.updateDomNodes();
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.navBack"));
						link.innerHTML = "&lt;";

						this.pagingSpan.appendChild(link);
					}
				
					// Add the page selection links
					for (var i = this.pageSelectorStart; i < this.pageSelectorStart+4; ++i) {
				//		console.debug("i: "+i);
				//		console.debug("pageSelectorStart+4: "+pageSelectorStart+4);
				
						if (i > lastPage)
							break;
				
						this.pagingSpan.appendChild(document.createTextNode(" "));
				
						if (i == currentPage) {
							var textNode = document.createElement("em");
							textNode.innerHTML = i;
							this.pagingSpan.appendChild(textNode);
						} else {		
							var link = document.createElement("a");
							link.onclick = function() {
								_this.onPageSelected(this.innerHTML);
							};
							link.setAttribute("href", "javascript:void(0);");
							link.setAttribute("title", string.substitute(message.get("ui.pageNav.pageNum"), [i]));
							link.innerHTML = i;
					
							this.pagingSpan.appendChild(link);
						}
					}
				
					// Add navigation to the next set of page navigation links
					if (this.pageSelectorStart+4 <= lastPage) {
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.onclick = function() {
							_this.pageSelectorStart += 4;
							_this.updateDomNodes();
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.navForward"));
						link.innerHTML = "&gt;";
						this.pagingSpan.appendChild(link);

				
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.onclick = function() {
							while (_this.pageSelectorStart+4 <= lastPage)
								_this.pageSelectorStart += 4;
							_this.updateDomNodes();
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.lastPage"));
						link.innerHTML = "&gt;&gt;";
						this.pagingSpan.appendChild(link);
					}
				};

				this.onPageSelected = function(pageIndex) {
					this.startHit = (pageIndex-1) * this.resultsPerPage;
				};
			},


			/**
			 * Page Navigation obj
			 *  The purpose of this object is to display a simple page navigation for a given result set.
			 *  It behaves similar to 'IndirectPageNavigation' with the following difference:
			 *    If the user clicks the '<<', '<', '>' or '>>' button, an onPageSelected event is fired for the next page
			 */
			PageNavigation: function(args) {
				this.resultsPerPage = args.resultsPerPage;
				this.infoSpan = args.infoSpan;
				this.pagingSpan = args.pagingSpan;

				this.startHit = 0;
				this.totalNumHits = 0;

				this.reset = function() {
					this.startHit = 0;
					this.totalNumHits = 0;
				};

				this.setTotalNumHits = function(numHits) {
					this.totalNumHits = numHits;
				};

				this.getStartHit = function() {
					return this.startHit;
				};

				this.updateDomNodes = function() {
					var _this = this;
					var totalNumHits = this.totalNumHits;
					var startHit = this.startHit;
					var endHit = startHit+this.resultsPerPage-1;

					if (totalNumHits <= endHit)
						endHit = totalNumHits - 1;
				
					var currentPage = Math.floor(startHit/this.resultsPerPage) + 1;
					var lastPage = Math.ceil(totalNumHits/this.resultsPerPage);

					// startPage determines which page should be shown first
					var startPage = currentPage - 2;
					if (startPage+4 > lastPage) {
						startPage = lastPage-4;
					}
					if (startPage < 1) {
						startPage = 1;
					}
			/*
					console.debug("totalNumHits: "+totalNumHits);
					console.debug("startHit: "+startHit);
					console.debug("endHit: "+endHit);
					console.debug("currentPage: "+currentPage);
					console.debug("lastPage: "+lastPage);
			*/

					// Update the dom elements
					var self = this;
					require(["dojo/string"], function(string) {
						if (totalNumHits == 0) {
							self.infoSpan.innerHTML = message.get("ui.pageNav.zeroHits");
						} else if (totalNumHits == 1) {
							self.infoSpan.innerHTML = string.substitute(message.get("ui.pageNav.beginEndTotalHit"), [startHit+1, endHit+1, totalNumHits]);
						} else {
							self.infoSpan.innerHTML = string.substitute(message.get("ui.pageNav.beginEndTotalHits"), [startHit+1, endHit+1, totalNumHits]);
						}
					
						// Clear the paging span
						while (self.pagingSpan.hasChildNodes())
							self.pagingSpan.removeChild(self.pagingSpan.firstChild);
					
						// Add info text to the paging span
						if (totalNumHits == 0) {
							self.pagingSpan.appendChild(document.createTextNode(string.substitute(message.get("ui.pageNav.currentToLastPages"), [1, 1])));
						} else {
							self.pagingSpan.appendChild(document.createTextNode(string.substitute(message.get("ui.pageNav.currentToLastPages"), [currentPage, lastPage])));
						}
					});

					// Add navigation to the next set of page navigation links
					if (startPage > 1) {
						// Add '<<' jump to page 1
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.onclick = function() {
							_this.onPageSelected(1);
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.firstPage"));
						link.innerHTML = "&lt;&lt;";
						this.pagingSpan.appendChild(link);

						// Add '<' jump to currentPage - 1
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.previousPage = currentPage - 1;
						link.onclick = function() {
							_this.onPageSelected(this.previousPage);
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.navBack"));
						link.innerHTML = "&lt;";
						this.pagingSpan.appendChild(link);
					}
				
					// Add the page selection links
					for (var i = startPage; i < startPage+5; ++i) {
				//		console.debug("i: "+i);
				//		console.debug("pageSelectorStart+4: "+pageSelectorStart+4);

						if (i < 1)
							continue;

						if (i > lastPage)
							break;
				
						this.pagingSpan.appendChild(document.createTextNode(" "));
				
						if (i == currentPage) {
							var textNode = document.createElement("em");
							textNode.innerHTML = i;
							this.pagingSpan.appendChild(textNode);
						} else {		
							var link = document.createElement("a");
							link.onclick = function() {
								_this.onPageSelected(this.innerHTML);
							};
							link.setAttribute("href", "javascript:void(0);");
							link.setAttribute("title", string.substitute(message.get("ui.pageNav.pageNum"), [i]));
							link.innerHTML = i;
							this.pagingSpan.appendChild(link);
						}
					}
				
					// Add navigation to the next set of page navigation links
					if (startPage+4 < lastPage) {
						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.nextPage = currentPage + 1;
						link.onclick = function() {
							_this.onPageSelected(this.nextPage);
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.navForward"));
						link.innerHTML = "&gt;";
						this.pagingSpan.appendChild(link);

						this.pagingSpan.appendChild(document.createTextNode(" "));
						var link = document.createElement("a");
						link.lastPage = lastPage;
						link.onclick = function() {
							_this.onPageSelected(this.lastPage);
						};
						link.setAttribute("href", "javascript:void(0);");
						link.setAttribute("title", message.get("ui.pageNav.lastPage"));
						link.innerHTML = "&gt;&gt;";
						this.pagingSpan.appendChild(link);
					}
				};

				this.onPageSelected = function(pageIndex) {
					this.startHit = (pageIndex-1) * this.resultsPerPage;
				};
			}
			
	})();
});
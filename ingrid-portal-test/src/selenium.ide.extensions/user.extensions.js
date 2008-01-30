
Selenium.prototype.doContextMenu = function(locator) {
       var element = this.page().findElement(locator);
       this.page()._fireEventOnElement("contextmenu", element, 0, 0);
};

Selenium.prototype.doContextMenuAt = function(locator, coordString) {
        if (!coordString)
        coordString = '2, 2';
       
        var element = this.page().findElement(locator);
        var clientXY = getClientXY(element, coordString)
        this.page()._fireEventOnElement("contextmenu", element, clientXY[0], clientXY[1]);
}; 
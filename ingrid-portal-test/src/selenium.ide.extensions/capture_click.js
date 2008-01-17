Recorder.removeEventHandler('clickLocator');
Recorder.addEventHandler('clickLocator', 'click', function(event) {
if (event.button == 0) {
this.clickLocator = this.findLocator(event.target);
}
}, { capture: true });
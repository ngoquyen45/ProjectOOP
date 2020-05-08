(function() {
  'use strict';
  angular.module('app.toast', []).service('toast', function() {
    var logIt;
    toastr.options = {
      closeButton: true,
      positionClass: 'toast-bottom-right',
      timeOut: '3000'
    };
    logIt = function(message, type) {
      return toastr[type](message);
    };
    this.log = function(message) {
      return logIt(message, 'info');
    };
    this.logWarning = function(message) {
      return logIt(message, 'warning');
    };
    this.logSuccess = function(message) {
      return logIt(message, 'success');
    };
    this.logError = function(message) {
      return logIt(message, 'error');
    };
  });

}).call(this);

//# sourceMappingURL=toast.js.map

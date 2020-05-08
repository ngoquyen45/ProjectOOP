(function() {
  'use strict';
  angular.module('app.directives').directive('enterPressed', function() {
    return function(scope, element, attrs) {
      return element.bind('keydown keypress', function(event) {
        if (event.which === 13) {
          scope.$apply(function() {
            return scope.$eval(attrs.enterPressed);
          });
          return event.preventDefault();
        }
      });
    };
  });

}).call(this);

//# sourceMappingURL=enter-pressed.js.map

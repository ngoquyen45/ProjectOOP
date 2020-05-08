(function() {
  'use strict';
  angular.module('app.directives').controller('ICheckController', [
    '$scope', function($scope) {
      var ngModelCtrl;
      ngModelCtrl = {
        $setViewValue: angular.noop
      };
      this.init = function(ngModelCtrl_) {
        return ngModelCtrl = ngModelCtrl_;
      };
      $scope.click = function() {
        if ((typeof $attrs === "undefined" || $attrs === null) || ($attrs.disabled == null)) {
          if (ngModelCtrl.$viewValue) {
            return ngModelCtrl.$setViewValue(false);
          } else {
            return ngModelCtrl.$setViewValue(true);
          }
        }
      };
      return $scope.isChecked = function() {
        return ngModelCtrl.$viewValue;
      };
    }
  ]).directive('iCheck', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        ngDisabled: '='
      },
      require: ['iCheck', '?ngModel'],
      templateUrl: 'views/directive/i-check.html',
      controller: 'ICheckController',
      link: function(scope, element, attrs, controllers) {
        var iCheckCtrl, ngModelCtrl;
        iCheckCtrl = controllers[0];
        ngModelCtrl = controllers[1];
        if (ngModelCtrl == null) {
          return;
        }
        return iCheckCtrl.init(ngModelCtrl);
      }
    };
  });

}).call(this);

//# sourceMappingURL=i-check.js.map

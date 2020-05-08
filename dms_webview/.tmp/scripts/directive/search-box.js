(function() {
  'use strict';
  angular.module('app.directives').controller('SearchBoxController', [
    '$scope', '$attrs', function($scope) {
      var lastText, listener, ngModelCtrl;
      ngModelCtrl = {
        $setViewValue: angular.noop
      };
      lastText = null;
      this.init = function(ngModelCtrl_) {
        ngModelCtrl = ngModelCtrl_;
        return $scope.searchText = null;
      };
      listener = $scope.$watch(ngModelCtrl.$viewValue, function() {
        $scope.searchText = ngModelCtrl.$viewValue;
        lastText = $scope.searchText;
        return listener();
      });
      $scope.textChanged = function() {
        if ((lastText != null) && lastText.length > 0 && ($scope.searchText != null) && $scope.searchText.length > 0 && lastText === $scope.searchText) {
          return false;
        } else {
          return true;
        }
      };
      $scope.performSearch = function() {
        listener();
        ngModelCtrl.$setViewValue($scope.searchText);
        ngModelCtrl.$render();
        return lastText = $scope.searchText;
      };
      $scope.clearText = function() {
        listener();
        lastText = '';
        $scope.searchText = '';
        ngModelCtrl.$setViewValue($scope.searchText);
        return ngModelCtrl.$render();
      };
      $scope.onEnter = function() {
        if ($scope.textChanged()) {
          return $scope.performSearch();
        } else {
          return $scope.clearText();
        }
      };
      return $scope.onBlur = function() {
        if ($scope.textChanged()) {
          if (((lastText != null) && lastText.length > 0) || (($scope.searchText != null) && $scope.searchText.length > 0)) {
            return $scope.performSearch();
          }
        }
      };
    }
  ]).directive('searchBox', function() {
    return {
      restrict: 'E',
      replace: true,
      scope: {
        placeholder: '@',
        ngDisabled: '='
      },
      require: ['searchBox', '?ngModel'],
      templateUrl: 'views/directive/search-box.html',
      controller: 'SearchBoxController',
      link: function(scope, element, attrs, controllers) {
        var ngModelCtrl, searchBoxCtrl;
        searchBoxCtrl = controllers[0];
        ngModelCtrl = controllers[1];
        if (ngModelCtrl == null) {
          return;
        }
        return searchBoxCtrl.init(ngModelCtrl);
      }
    };
  });

}).call(this);

//# sourceMappingURL=search-box.js.map

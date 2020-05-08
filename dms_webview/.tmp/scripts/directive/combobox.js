(function() {
  'use strict';
  angular.module('app.directives').directive('nameCategoryCombobox', [
    function() {
      return {
        restrict: 'E',
        replace: true,
        scope: {
          datas: '=',
          readonlyName: '=',
          placeholder: '@'
        },
        templateUrl: 'views/directive/name-category-combobox.html',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModelCtrl) {
          scope.container = {};
          scope.getPlaceholder = function() {
            if (scope.placeholder != null) {
              return '-- ' + scope.placeholder + ' --';
            } else {
              return '--';
            }
          };
          scope.onSelect = function($item, $model) {
            return ngModelCtrl.$setViewValue($model);
          };
          ngModelCtrl.$render = function() {
            return scope.container.selectedId = ngModelCtrl.$viewValue;
          };
          return scope.getReadonlyName = function() {
            var data, _i, _len, _ref;
            if (scope.readonlyName != null) {
              return scope.readonlyName;
            }
            if (scope.datas != null) {
              _ref = scope.datas;
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                data = _ref[_i];
                if (data.id === scope.container.selectedId || !((data.id != null) && scope.container.selectedId)) {
                  return data.name;
                }
              }
            }
            return '';
          };
        }
      };
    }
  ]).directive('nameCodeCategoryCombobox', [
    function() {
      return {
        restrict: 'E',
        replace: true,
        scope: {
          datas: '=',
          readonlyName: '=',
          placeholder: '@'
        },
        templateUrl: 'views/directive/name-code-category-combobox.html',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModelCtrl) {
          scope.container = {};
          scope.getPlaceholder = function() {
            if (scope.placeholder != null) {
              return '-- ' + scope.placeholder + ' --';
            } else {
              return '--';
            }
          };
          scope.onSelect = function($item, $model) {
            return ngModelCtrl.$setViewValue($model);
          };
          ngModelCtrl.$render = function() {
            return scope.container.selectedId = ngModelCtrl.$viewValue;
          };
          return scope.getReadonlyName = function() {
            var data, _i, _len, _ref;
            if (scope.readonlyName != null) {
              return scope.readonlyName;
            }
            if ((scope.datas != null) && (scope.container.selectedId != null)) {
              _ref = scope.datas;
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                data = _ref[_i];
                if (data.id === scope.container.selectedId) {
                  return data.name;
                }
              }
            }
            return '';
          };
        }
      };
    }
  ]).directive('userCombobox', [
    function() {
      return {
        restrict: 'E',
        replace: true,
        scope: {
          datas: '=',
          readonlyName: '=',
          placeholder: '@'
        },
        templateUrl: 'views/directive/user-combobox.html',
        require: 'ngModel',
        link: function(scope, element, attrs, ngModelCtrl) {
          scope.container = {};
          scope.getPlaceholder = function() {
            if (scope.placeholder != null) {
              return '-- ' + scope.placeholder + ' --';
            } else {
              return '--';
            }
          };
          scope.onSelect = function($item, $model) {
            return ngModelCtrl.$setViewValue($model);
          };
          ngModelCtrl.$render = function() {
            return scope.container.selectedId = ngModelCtrl.$viewValue;
          };
          return scope.getReadonlyName = function() {
            var data, _i, _len, _ref;
            if (scope.readonlyName != null) {
              return scope.readonlyName;
            }
            if ((scope.datas != null) && (scope.container.selectedId != null)) {
              _ref = scope.datas;
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                data = _ref[_i];
                if (data.id === scope.container.selectedId) {
                  return data.fullname;
                }
              }
            }
            return '';
          };
        }
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=combobox.js.map

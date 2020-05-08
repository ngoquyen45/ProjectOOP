(function() {
  'use strict';
  angular.module('app').controller('UserCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$filter', function($scope, CtrlCategoryInitiatorService, $filter) {
      $scope.title = 'user.title';
      $scope.categoryName = 'user';
      $scope.usePopup = false;
      $scope.roles = [
        {
          id: 'all',
          name: '-- ' + $filter('translate')('all') + ' --'
        }, {
          id: 'AD',
          name: $filter('translate')('role.ad')
        }, {
          id: 'OBS',
          name: $filter('translate')('role.obs')
        }, {
          id: 'SUP',
          name: $filter('translate')('role.sup')
        }, {
          id: 'DIS',
          name: $filter('translate')('role.dis')
        }, {
          id: 'SM',
          name: $filter('translate')('role.sm')
        }
      ];
      $scope.isUseDistributorFilter = function() {
        return ($scope.filter.role != null) && ($scope.filter.role === 'DIS' || $scope.filter.role === 'SM');
      };
      $scope.roleFilterChange = function() {
        $scope.filter.distributorId = null;
        return $scope.distributorFilterChange();
      };
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      $scope.getReloadDataParams = function(params) {
        params = params != null ? params : {};
        params.category = $scope.categoryName;
        if (($scope.filter.role != null) && $scope.filter.role !== 'all') {
          params.role = $scope.filter.role;
        }
        if ($scope.isUseDistributorFilter() && ($scope.filter.distributorId != null) && $scope.filter.distributorId !== 'all') {
          params.distributorId = $scope.filter.distributorId;
        }
        if (!_.isEmpty($scope.filter.searchText)) {
          params.q = $scope.filter.searchText;
        }
        return params;
      };
      $scope.addInitFunction(function() {
        var _ref;
        $scope.filter.role = (_ref = $scope.filter.role) != null ? _ref : 'all';
        if ($scope.isUseDistributorFilter() || $scope.filter.role === 'all') {
          return $scope.columns = [
            {
              header: 'fullname',
              property: 'fullname'
            }, {
              header: 'username',
              property: 'usernameFull'
            }, {
              header: 'role',
              property: function(record) {
                return $filter('translate')('role.' + record.role.toLowerCase());
              }
            }, {
              header: 'distributor',
              property: function(record) {
                if (record.distributor != null) {
                  return record.distributor.name;
                }
              }
            }
          ];
        } else {
          return $scope.columns = [
            {
              header: 'fullname',
              property: 'fullname'
            }, {
              header: 'username',
              property: 'usernameFull'
            }, {
              header: 'role',
              property: function(record) {
                return $filter('translate')('role.' + record.role.toLowerCase());
              }
            }
          ];
        }
      });
      return $scope.init();
    }
  ]).controller('UserDetailCtrl', [
    '$scope', '$state', '$stateParams', '$filter', 'CtrlUtilsService', 'LoadingUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', function($scope, $state, $stateParams, $filter, CtrlUtilsService, LoadingUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService) {
      $scope.title = 'user.title';
      $scope.categoryName = 'user';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'user';
      $scope.onLoadSuccess = function() {
        var salesman;
        if ($scope.record.distributor != null) {
          $scope.record.distributorId = $scope.record.distributor.id;
        }
        if (($scope.record.salesmen != null) && $scope.record.salesman.length > 0) {
          $scope.record.salesmanIds = (function() {
            var _i, _len, _ref, _results;
            _ref = $scope.record.salesmen;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              salesman = _ref[_i];
              _results.push(salesman.id);
            }
            return _results;
          })();
        }
        if ($scope.record.role != null) {
          return $scope.roleName = $filter('translate')('role.' + $scope.record.role.toLowerCase());
        }
      };
      $scope.clientCode = CtrlUtilsService.getUserInfo().clientCode.toLowerCase() + '_';
      $scope.roles = [
        {
          id: 'AD',
          name: $filter('translate')('role.ad')
        }, {
          id: 'OBS',
          name: $filter('translate')('role.obs')
        }, {
          id: 'SUP',
          name: $filter('translate')('role.sup')
        }, {
          id: 'DIS',
          name: $filter('translate')('role.dis')
        }, {
          id: 'SM',
          name: $filter('translate')('role.sm')
        }
      ];
      $scope.useDistributor = function() {
        if (($scope.record != null) && ($scope.record.role != null)) {
          return $scope.record.role === 'DIS' || $scope.record.role === 'SM';
        }
      };
      $scope.resetPassword = function() {
        var params, status;
        status = $scope.loadStatus.getStatusByDataName('reset.password');
        params = {
          who: $scope.who,
          category: $scope.categoryName,
          id: $scope.record.id,
          action: 'reset-password'
        };
        return CtrlUtilsService.doPut(params, status, null, null, null, 'reset.password');
      };
      $scope.goToObserverDistributors = function() {
        return $state.go('user-observer-distributors', {
          id: $scope.record.id,
          filter: $stateParams.filter
        });
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      $scope.addInitFunction(function() {
        return LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributor'), function(list) {
          return $scope.distributors = list;
        });
      });
      return $scope.init();
    }
  ]).controller('UserObserverDistributorsCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', '$confirm', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, $confirm) {
      $scope.title = 'user.observer.distributors.title';
      $scope.reloadData = function() {
        var params;
        $scope.records = [];
        params = {
          who: $scope.who,
          category: 'user',
          id: $stateParams.id,
          param: 'distributors'
        };
        return CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records) {
          return $scope.records = records;
        }, function() {
          return toast.logError($filter('translate')('loading.error'));
        });
      };
      $scope.isEmpty = function() {
        return _.isEmpty($scope.records);
      };
      $scope.checkBeforeSave = function() {
        return true;
      };
      $scope.getObjectToSave = function() {
        var record, saveData, _i, _len, _ref;
        saveData = {
          list: []
        };
        _ref = $scope.records;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          record = _ref[_i];
          if (record.selected) {
            saveData.list.push(record.id);
          }
        }
        return saveData;
      };
      $scope.getSaveParams = function(params) {
        params.category = 'user';
        params.id = $stateParams.id;
        params.param = 'distributors';
        return params;
      };
      $scope.moreThan20 = function() {
        return ($scope.records != null) && $scope.records.length > 20;
      };
      $scope.onSaveSuccess = function() {};
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return false;
      };
      $scope.back = function() {
        if ($scope.isChanged()) {
          return $confirm({
            text: $filter('translate')('confirm.dialog.are.you.sure.back')
          }).then(function() {
            return $state.go('user-detail', {
              id: $stateParams.id,
              filter: $stateParams.filter
            });
          });
        } else {
          return $state.go('user-detail', {
            id: $stateParams.id,
            filter: $stateParams.filter
          });
        }
      };
      CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.addInitFunction(function() {
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=user-ctrl.js.map

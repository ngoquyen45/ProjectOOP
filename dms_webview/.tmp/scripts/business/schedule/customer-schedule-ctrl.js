(function() {
  'use strict';
  angular.module('app').controller('CustomerScheduleCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var convertWeeks, createNewScheduleItem, createWeekHeader, isItemDayValid, reconvertWeeks, weekIndex;
      $scope.title = 'customer.schedule.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.mustSelectDistributor = function() {
        return $scope.isUseDistributorFilter() && ($scope.filter.distributorId == null);
      };
      $scope.goToImport = function() {
        return $state.go('import-customer-schedule', {
          filter: $stateParams.filter
        });
      };
      $scope.dayOfWeeks = [
        {
          id: null,
          name: $filter('translate')('all')
        }, {
          id: 2,
          name: $filter('translate')('monday')
        }, {
          id: 3,
          name: $filter('translate')('tuesday')
        }, {
          id: 4,
          name: $filter('translate')('wednesday')
        }, {
          id: 5,
          name: $filter('translate')('thursday')
        }, {
          id: 6,
          name: $filter('translate')('friday')
        }, {
          id: 7,
          name: $filter('translate')('saturday')
        }, {
          id: 1,
          name: $filter('translate')('sunday')
        }
      ];
      $scope.itemsPerPageOptions = [
        {
          id: 10,
          name: '10'
        }, {
          id: 20,
          name: '20'
        }, {
          id: 30,
          name: '30'
        }
      ];
      $scope.useWeekOfFrequency = CtrlUtilsService.getUserInfo().numberWeekOfFrequency > 1;
      $scope.numberWeekOfFrequency = $scope.useWeekOfFrequency ? CtrlUtilsService.getUserInfo().numberWeekOfFrequency : 0;
      $scope.weeks = [];
      if ($scope.useWeekOfFrequency) {
        weekIndex = 1;
        createWeekHeader = function() {
          $scope.weeks.push({
            header: 'W' + weekIndex,
            index: weekIndex
          });
          return weekIndex++;
        };
        while (!(weekIndex > $scope.numberWeekOfFrequency)) {
          createWeekHeader();
        }
      }
      createNewScheduleItem = function() {
        var item, weekConverted, _i, _len, _ref;
        item = {
          monday: false,
          tuesday: false,
          wednesday: false,
          thursday: false,
          friday: false,
          saturday: false,
          sunday: false
        };
        if ($scope.useWeekOfFrequency) {
          item.weeksConverted = convertWeeks([]);
          _ref = item.weeksConverted;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            weekConverted = _ref[_i];
            weekConverted.selected = true;
          }
        }
        return item;
      };
      convertWeeks = function(weeks) {
        var f, weeksConverted;
        weekIndex = 1;
        weeksConverted = [];
        f = function() {
          weeksConverted.push({
            selected: _.include(weeks, weekIndex),
            index: weekIndex
          });
          return weekIndex++;
        };
        while (!(weekIndex > $scope.numberWeekOfFrequency)) {
          f();
        }
        return weeksConverted;
      };
      reconvertWeeks = function(weeksConverted) {
        var weekConverted, weeks, _i, _len;
        if ($scope.useWeekOfFrequency) {
          weeks = [];
          for (_i = 0, _len = weeksConverted.length; _i < _len; _i++) {
            weekConverted = weeksConverted[_i];
            if (weekConverted.selected) {
              weeks.push(weekConverted.index);
            }
          }
          return weeks;
        } else {
          return null;
        }
      };
      isItemDayValid = function(item) {
        return (item != null) && (item.monday || item.tuesday || item.wednesday || item.thursday || item.friday || item.saturday || item.sunday);
      };
      $scope.editRoute = function(record) {
        $scope.changeStatus.markAsChanged();
        if (record.schedule.routeId == null) {
          record.schedule.items = [];
          return record.schedule.items.push(createNewScheduleItem());
        }
      };
      $scope.getReloadDataParams = function(params) {
        if ($scope.mustSelectDistributor()) {
          return null;
        } else {
          params.category = 'customer-schedule';
          if ($scope.isUseDistributorFilter()) {
            params.distributorId = $scope.filter.distributorId;
          }
          if (($scope.filter.searchText != null) && $scope.filter.searchText.length > 0) {
            params.q = $scope.filter.searchText;
          }
          if (($scope.filter.routeId != null) && $scope.filter.routeId !== 'all') {
            params.searchByRoute = true;
            if ($scope.filter.routeId !== 'null') {
              params.routeId = $scope.filter.routeId;
            }
          } else {
            params.searchByRoute = false;
          }
          if ($scope.filter.day != null) {
            params.day = $scope.filter.day;
          }
          return params;
        }
      };
      $scope.afterLoad = function() {
        var item, record, _i, _len, _ref, _results;
        _ref = $scope.records;
        _results = [];
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          record = _ref[_i];
          if (record.schedule != null) {
            if (record.schedule.items != null) {
              _results.push((function() {
                var _j, _len1, _ref1, _results1;
                _ref1 = record.schedule.items;
                _results1 = [];
                for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
                  item = _ref1[_j];
                  _results1.push(item.weeksConverted = convertWeeks(item.weeks));
                }
                return _results1;
              })());
            } else {
              record.schedule.items = [];
              _results.push(record.schedule.items.push(createNewScheduleItem()));
            }
          } else {
            record.schedule = {};
            record.schedule.items = [];
            _results.push(record.schedule.items.push(createNewScheduleItem()));
          }
        }
        return _results;
      };
      $scope.checkBeforeSave = function() {
        var item, record, _i, _j, _len, _len1, _ref, _ref1;
        _ref = $scope.records;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          record = _ref[_i];
          if (record.schedule != null) {
            if (record.schedule.routeId != null) {
              if (_.isEmpty(record.schedule.items)) {
                toast.logError($filter('translate')('error.data.input.not.valid'));
                return false;
              }
              _ref1 = record.schedule.items;
              for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
                item = _ref1[_j];
                if (item != null) {
                  if (!isItemDayValid(item)) {
                    toast.logError($filter('translate')('error.data.input.not.valid'));
                    return false;
                  }
                  if ($scope.useWeekOfFrequency && _.isEmpty(reconvertWeeks(item.weeksConverted))) {
                    toast.logError($filter('translate')('error.data.input.not.valid'));
                    return false;
                  }
                } else {
                  toast.logError($filter('translate')('error.data.input.not.valid'));
                  return false;
                }
              }
            }
          }
        }
        return true;
      };
      $scope.getObjectToSave = function() {
        var item, itemCopy, record, recordForSave, recordForSaves, _i, _j, _len, _len1, _ref, _ref1;
        recordForSaves = [];
        _ref = $scope.records;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          record = _ref[_i];
          recordForSave = {
            id: record.id
          };
          if (record.schedule != null) {
            if (record.schedule.routeId != null) {
              recordForSave.schedule = {
                routeId: record.schedule.routeId
              };
              recordForSave.schedule.items = [];
              _ref1 = record.schedule.items;
              for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
                item = _ref1[_j];
                itemCopy = angular.copy(item);
                itemCopy.weeks = reconvertWeeks(item.weeksConverted);
                recordForSave.schedule.items.push(itemCopy);
              }
            }
          }
          recordForSaves.push(recordForSave);
        }
        return {
          list: recordForSaves
        };
      };
      $scope.getSaveParams = function(params) {
        params.category = 'customer-schedule';
        params.param = 'by-distributor';
        if ($scope.isUseDistributorFilter()) {
          params.distributorId = $scope.filter.distributorId;
        }
        return params;
      };
      $scope.onSaveSuccess = function() {};
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return false;
      };
      $scope.distributorFilterChange = function() {
        $scope.filter.routeId = null;
        return $scope.filterChange();
      };
      $scope.filterChange = function() {
        $scope.filter.searchText = null;
        return $scope.searchFilterChange();
      };
      $scope.searchFilterChange = function() {
        $scope.filter.currentPage = 1;
        return $scope.pageChange();
      };
      CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.addInitFunction(function() {
        var _ref;
        $scope.filter.routeId = (_ref = $scope.filter.routeId) != null ? _ref : 'all';
        if ($scope.isUseDistributorFilter()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = list;
          });
        }
        if (!$scope.mustSelectDistributor()) {
          LoadingUtilsService.loadRoutesByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('routes'), function(list) {
            $scope.routesFilter = _.union([
              {
                id: 'all',
                name: '-- ' + $filter('translate')('all') + ' --'
              }, {
                id: 'null',
                name: '-- ' + $filter('translate')('customer.schedule.route.null') + ' --'
              }
            ], list);
            return $scope.routes = _.union([
              {
                id: null,
                name: '-- ' + $filter('translate')('customer.schedule.route.null') + ' --'
              }
            ], list);
          });
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('CustomerScheduleSingleCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$stateParams', '$state', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $stateParams, $state) {
      var convertWeeks, createNewScheduleItem, createWeekHeader, isItemDayValid, reconvertWeeks, weekIndex;
      $scope.title = 'customer.schedule.title';
      $scope.defaultBackState = 'customer-pending';
      $scope.useWeekOfFrequency = CtrlUtilsService.getUserInfo().numberWeekOfFrequency > 1;
      $scope.numberWeekOfFrequency = $scope.useWeekOfFrequency ? CtrlUtilsService.getUserInfo().numberWeekOfFrequency : 0;
      $scope.weeks = [];
      if ($scope.useWeekOfFrequency) {
        weekIndex = 1;
        createWeekHeader = function() {
          $scope.weeks.push({
            header: 'W' + weekIndex,
            index: weekIndex
          });
          return weekIndex++;
        };
        while (!(weekIndex > $scope.numberWeekOfFrequency)) {
          createWeekHeader();
        }
      }
      createNewScheduleItem = function() {
        var item, weekConverted, _i, _len, _ref;
        item = {
          monday: false,
          tuesday: false,
          wednesday: false,
          thursday: false,
          friday: false,
          saturday: false,
          sunday: false
        };
        if ($scope.useWeekOfFrequency) {
          item.weeksConverted = convertWeeks([]);
          _ref = item.weeksConverted;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            weekConverted = _ref[_i];
            weekConverted.selected = true;
          }
        }
        return item;
      };
      convertWeeks = function(weeks) {
        var f, weeksConverted;
        weekIndex = 1;
        weeksConverted = [];
        f = function() {
          weeksConverted.push({
            selected: _.include(weeks, weekIndex),
            index: weekIndex
          });
          return weekIndex++;
        };
        while (!(weekIndex > $scope.numberWeekOfFrequency)) {
          f();
        }
        return weeksConverted;
      };
      reconvertWeeks = function(weeksConverted) {
        var weekConverted, weeks, _i, _len;
        if ($scope.useWeekOfFrequency) {
          weeks = [];
          for (_i = 0, _len = weeksConverted.length; _i < _len; _i++) {
            weekConverted = weeksConverted[_i];
            if (weekConverted.selected) {
              weeks.push(weekConverted.index);
            }
          }
          return weeks;
        } else {
          return null;
        }
      };
      isItemDayValid = function(item) {
        return (item != null) && (item.monday || item.tuesday || item.wednesday || item.thursday || item.friday || item.saturday || item.sunday);
      };
      $scope.editRoute = function(record) {
        $scope.changeStatus.markAsChanged();
        if (record.schedule.routeId == null) {
          record.schedule.items = [];
          return record.schedule.items.push(createNewScheduleItem());
        }
      };
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'customer-schedule',
          id: $stateParams.id
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          var item, _i, _len, _ref, _results;
          $scope.record = record;
          LoadingUtilsService.loadRoutesByDistributor($scope.who, $scope.record.distributor.id, $scope.loadStatus.getStatusByDataName('routes'), function(list) {
            return $scope.routes = _.union([
              {
                id: null,
                name: '-- ' + $filter('translate')('customer.schedule.route.null') + ' --'
              }
            ], list);
          });
          if ($scope.record.schedule != null) {
            if ($scope.record.schedule.items != null) {
              _ref = $scope.record.schedule.items;
              _results = [];
              for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                item = _ref[_i];
                _results.push(item.weeksConverted = convertWeeks(item.weeks));
              }
              return _results;
            } else {
              $scope.record.schedule.items = [];
              return $scope.record.schedule.items.push(createNewScheduleItem());
            }
          } else {
            $scope.record.schedule = {};
            $scope.record.schedule.items = [];
            return $scope.record.schedule.items.push(createNewScheduleItem());
          }
        }, function(error) {
          if (error.status === 400) {
            return $state.go('404');
          } else {
            return toast.logError($filter('translate')('loading.error'));
          }
        });
      };
      $scope.checkBeforeSave = function() {
        var item, record, _i, _len, _ref;
        record = $scope.record;
        if (record.schedule != null) {
          if (record.schedule.routeId != null) {
            if (_.isEmpty(record.schedule.items)) {
              toast.logError($filter('translate')('error.data.input.not.valid'));
              return false;
            }
            _ref = record.schedule.items;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              item = _ref[_i];
              if (item != null) {
                if (!isItemDayValid(item)) {
                  toast.logError($filter('translate')('error.data.input.not.valid'));
                  return false;
                }
                if ($scope.useWeekOfFrequency && _.isEmpty(reconvertWeeks(item.weeksConverted))) {
                  toast.logError($filter('translate')('error.data.input.not.valid'));
                  return false;
                }
              } else {
                toast.logError($filter('translate')('error.data.input.not.valid'));
                return false;
              }
            }
          }
        }
        return true;
      };
      $scope.getObjectToSave = function() {
        var item, itemCopy, record, recordForSave, _i, _len, _ref;
        record = $scope.record;
        recordForSave = {
          id: record.id
        };
        if (record.schedule != null) {
          if (record.schedule.routeId != null) {
            recordForSave.schedule = {
              routeId: record.schedule.routeId
            };
            recordForSave.schedule.items = [];
            _ref = record.schedule.items;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              item = _ref[_i];
              itemCopy = angular.copy(item);
              itemCopy.weeks = reconvertWeeks(item.weeksConverted);
              recordForSave.schedule.items.push(itemCopy);
            }
          }
        }
        return recordForSave;
      };
      $scope.getSaveParams = function(params) {
        params.category = 'customer-schedule';
        params.id = $stateParams.id;
        return params;
      };
      $scope.onSaveSuccess = function() {};
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return false;
      };
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.addInitFunction(function() {
        if ($stateParams.id == null) {
          return $state.go('404');
        } else {
          return $scope.reloadData();
        }
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=customer-schedule-ctrl.js.map

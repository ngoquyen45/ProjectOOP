(function() {
  'use strict';
  var CtrlCategoryInitiatorService;

  CtrlCategoryInitiatorService = (function() {
    function CtrlCategoryInitiatorService($log, $filter, toast, $modal, $state, $stateParams, CtrlUtilsService, LoadingUtilsService, CATEGORY_POPUP_TEMPLATE, CATEGORY_POPUP_CTRL, $confirm, CtrlInitiatorService) {
      this.$log = $log;
      this.$filter = $filter;
      this.toast = toast;
      this.$modal = $modal;
      this.$state = $state;
      this.$stateParams = $stateParams;
      this.CtrlUtilsService = CtrlUtilsService;
      this.LoadingUtilsService = LoadingUtilsService;
      this.CATEGORY_POPUP_TEMPLATE = CATEGORY_POPUP_TEMPLATE;
      this.CATEGORY_POPUP_CTRL = CATEGORY_POPUP_CTRL;
      this.$confirm = $confirm;
      this.CtrlInitiatorService = CtrlInitiatorService;
    }

    CtrlCategoryInitiatorService.prototype.initCategoryListViewCtrl = function($scope) {
      var openDetailPopup, _ref, _ref1, _ref2, _ref3, _ref4, _ref5, _ref6, _this;
      _this = this;
      if ($scope.title == null) {
        throw "must set value $scope.title";
      }
      if ($scope.categoryName == null) {
        throw "must set value $scope.categoryName";
      }
      if ($scope.usePopup == null) {
        throw "must set value $scope.usePopup";
      }
      if (!jQuery.isFunction($scope.isUseDistributorFilter)) {
        throw "must implement $scope.isUseDistributorFilter";
      }
      $scope.columns = (_ref = $scope.columns) != null ? _ref : [
        {
          header: 'name',
          property: 'name'
        }
      ];
      if ($scope.usePopup) {
        $scope.popupTemplateUrl = (_ref1 = $scope.popupTemplateUrl) != null ? _ref1 : _this.CATEGORY_POPUP_TEMPLATE;
        $scope.popupCtrl = (_ref2 = $scope.popupCtrl) != null ? _ref2 : _this.CATEGORY_POPUP_CTRL;
      } else {
        $scope.detailState = (_ref3 = $scope.detailState) != null ? _ref3 : _this.$state.get('.').name + '-detail';
      }
      $scope.useDeleteButton = (_ref4 = $scope.useDeleteButton) != null ? _ref4 : true;
      $scope.useEnableButton = (_ref5 = $scope.useEnableButton) != null ? _ref5 : true;
      $scope.useActivateButton = (_ref6 = $scope.useActivateButton) != null ? _ref6 : true;
      $scope.isDisplayImportBtn = function() {
        return $scope.importState != null;
      };
      $scope.goToImport = function() {
        return _this.$state.go($scope.importState, {
          filter: _this.$stateParams.filter
        });
      };
      $scope.distributorFilterChange = function() {
        $scope.filter.searchText = null;
        return $scope.searchFilterChange();
      };
      $scope.searchFilterChange = function() {
        $scope.filter.currentPage = 1;
        return $scope.pageChange();
      };
      $scope.getPropertyValue = function(column, record) {
        if ((column != null) && (column.property != null)) {
          if (typeof column.property === 'function') {
            return column.property(record);
          } else {
            return record[column.property];
          }
        }
        return '';
      };
      $scope.enableRecord = function(record) {
        var action, onSuccess, params, status;
        action = 'enable';
        status = $scope.loadStatus.getStatusByDataName(action);
        params = {
          who: $scope.who,
          category: $scope.categoryName,
          id: record.id,
          action: action
        };
        onSuccess = function() {
          return $scope.refresh();
        };
        return _this.CtrlUtilsService.doPut(params, status, null, onSuccess, null, action);
      };
      $scope.changeActiveState = function(record) {
        var action, onSuccess, params, status;
        action = record.active ? 'deactivate' : 'activate';
        status = $scope.loadStatus.getStatusByDataName(action);
        params = {
          who: $scope.who,
          category: $scope.categoryName,
          id: record.id,
          action: action
        };
        onSuccess = function() {
          return $scope.refresh();
        };
        return _this.CtrlUtilsService.doPut(params, status, null, onSuccess, null, action);
      };
      $scope.deleteRecord = function(record) {
        var onSuccess, params, status;
        status = $scope.loadStatus.getStatusByDataName('data');
        params = {
          who: $scope.who,
          category: $scope.categoryName,
          id: record.id
        };
        onSuccess = function() {
          return $scope.refresh();
        };
        return _this.CtrlUtilsService.doDelete(params, status, onSuccess, null);
      };
      openDetailPopup = function(id) {
        var modalInstance;
        modalInstance = _this.$modal.open({
          templateUrl: $scope.popupTemplateUrl,
          controller: $scope.popupCtrl,
          resolve: {
            title: function() {
              return $scope.title;
            },
            categoryName: function() {
              return $scope.categoryName;
            },
            id: function() {
              return id;
            },
            useDistributorFilter: function() {
              return $scope.isUseDistributorFilter();
            }
          },
          backdrop: true
        });
        return modalInstance.result.then(function() {
          return $scope.refresh();
        });
      };
      $scope.createRecord = function() {
        if ($scope.usePopup) {
          return openDetailPopup(null);
        } else {
          return _this.$state.go($scope.detailState);
        }
      };
      $scope.editRecord = function(record) {
        if ($scope.usePopup) {
          return openDetailPopup(record.id);
        } else {
          return _this.$state.go($scope.detailState, {
            id: record.id,
            filter: $scope.getFilterAsString()
          });
        }
      };
      $scope.getReloadDataParams = function(params) {
        params = params != null ? params : {};
        params.category = $scope.categoryName;
        if ($scope.subCategory != null) {
          params.subCategory = $scope.subCategory;
        }
        if ($scope.isUseDistributorFilter() && ($scope.filter.distributorId != null) && $scope.filter.distributorId !== 'all') {
          params.distributorId = $scope.filter.distributorId;
        }
        if (!_.isEmpty($scope.filter.searchText)) {
          params.q = $scope.filter.searchText;
        }
        return params;
      };
      _this.CtrlInitiatorService.initPagingFilterViewCtrl($scope);
      return $scope.addInitFunction((function() {
        if ($scope.isUseDistributorFilter()) {
          return _this.LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            var distributor, _ref7;
            $scope.distributors = _.union([
              {
                id: 'all',
                name: '-- ' + _this.$filter('translate')('all') + ' --'
              }
            ], list);
            $scope.filter.distributorId = (_ref7 = $scope.filter.distributorId) != null ? _ref7 : 'all';
            if ($scope.filter.distributorId != null) {
              distributor = _.find($scope.distributors, function(distributor) {
                return distributor.id === $scope.filter.distributorId;
              });
              if (distributor != null) {
                return $scope.reloadData();
              } else {
                if (distributor == null) {
                  return _this.$state.go('404');
                }
              }
            } else {
              return $scope.reloadData();
            }
          });
        } else {
          return $scope.reloadData();
        }
      }), 'category-list-view');
    };

    CtrlCategoryInitiatorService.prototype.initCategoryDetailViewCtrl = function($scope) {
      var afterAction, currentCheckBeforeSave, _ref, _ref1, _this;
      _this = this;
      if ($scope.title == null) {
        throw "must set value $scope.title";
      }
      if ($scope.categoryName == null) {
        throw "must set value $scope.categoryName";
      }
      if (!jQuery.isFunction($scope.isIdRequire)) {
        throw "must implement $scope.isIdRequire";
      }
      $scope.useMap = (_ref = $scope.useMap) != null ? _ref : false;
      if ($scope.useMap) {
        if (!jQuery.isFunction($scope.getLocation)) {
          throw "must implement $scope.getLocation";
        }
        $scope.firstLoadDone = false;
        $scope.$on('locationSelectorReady', function() {
          if ($scope.firstLoadDone) {
            return $scope.$broadcast('refreshLocationSelector', {
              id: null,
              location: $scope.getLocation()
            });
          }
        });
      }
      $scope.isNew = function() {
        return $scope.isIdRequire() && (_this.$stateParams.id == null);
      };
      $scope.isEdit = function() {
        return !$scope.isNew();
      };
      $scope.isDraft = function() {
        return !(($scope.record != null) && ($scope.record.draft != null) && !$scope.record.draft);
      };
      afterAction = function(data) {
        var _newStateParam;
        if ($scope.isNew()) {
          _newStateParam = angular.copy(_this.$stateParams);
          _newStateParam.id = data.id;
          return _this.$state.go('.', _newStateParam);
        } else {
          return $scope.refresh();
        }
      };
      currentCheckBeforeSave = $scope.checkBeforeSave;
      $scope.checkBeforeSave = function() {
        if ($scope.isFormValid('form')) {
          if (!jQuery.isFunction(currentCheckBeforeSave) || currentCheckBeforeSave()) {
            return true;
          } else {
            return false;
          }
        } else {
          _this.toast.logError(_this.$filter('translate')('error.data.input.not.valid'));
          return false;
        }
      };
      $scope.getObjectToSave = (_ref1 = $scope.getObjectToSave) != null ? _ref1 : function() {
        return $scope.record;
      };
      $scope.getSaveParams = function(params) {
        params.category = $scope.categoryName;
        if (!$scope.isNew()) {
          params.id = _this.$stateParams.id;
        }
        return params;
      };
      $scope.onSaveSuccess = function(data) {
        return afterAction(data);
      };
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return $scope.isNew();
      };
      $scope.enable = function() {
        var loadStatusOfData, onFailure, onSuccess, params, requestBody;
        if (!$scope.isChanged() || $scope.checkBeforeSave()) {
          loadStatusOfData = $scope.loadStatus.getStatusByDataName('enable');
          requestBody = null;
          if ($scope.isChanged()) {
            requestBody = $scope.getObjectToSave();
          }
          params = {
            who: $scope.who
          };
          params = $scope.getSaveParams(params);
          params.action = 'enable';
          onSuccess = function(data) {
            $scope.changeStatus.reset();
            return $scope.onSaveSuccess(data);
          };
          onFailure = function(error) {
            return $scope.onSaveFailure(error);
          };
          if ($scope.isPost()) {
            return _this.CtrlUtilsService.doPost(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable');
          } else {
            return _this.CtrlUtilsService.doPut(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable');
          }
        }
      };
      _this.CtrlInitiatorService.initCanSaveViewCtrl($scope);
      if ($scope.isIdRequire()) {
        _this.CtrlInitiatorService.initCanBackViewCtrl($scope);
      }
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: $scope.categoryName
        };
        if ($scope.isIdRequire()) {
          params.id = _this.$stateParams.id;
        }
        return _this.CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          $scope.record = record;
          if ($scope.useMap) {
            $scope.$broadcast('refreshLocationSelector', {
              id: null,
              location: $scope.getLocation()
            });
            $scope.firstLoadDone = true;
          }
          if (jQuery.isFunction($scope.onLoadSuccess)) {
            return $scope.onLoadSuccess($scope.record);
          }
        }, function(error) {
          if (jQuery.isFunction($scope.onLoadFailure)) {
            $scope.onLoadFailure(error);
          }
          if (error.status === 400) {
            return _this.$state.go('404');
          } else {
            return _this.toast.logError(_this.$filter('translate')('loading.error'));
          }
        });
      };
      return $scope.addInitFunction((function() {
        return $scope.reloadData();
      }), 'category-detail-view');
    };

    CtrlCategoryInitiatorService.prototype.initCategoryDetailPopupCtrl = function($scope, $modalInstance) {
      var afterAction, currentCheckBeforeSave, _ref, _this;
      _this = this;
      if ($scope.title == null) {
        throw "must set value $scope.title";
      }
      if ($scope.categoryName == null) {
        throw "must set value $scope.categoryName";
      }
      if (!jQuery.isFunction($scope.isIdRequire)) {
        throw "must implement $scope.isIdRequire";
      }
      if (!jQuery.isFunction($scope.isUseDistributorFilter)) {
        throw "must implement $scope.isUseDistributorFilter";
      }
      $scope.isNew = function() {
        return $scope.isIdRequire() && ($scope.id == null);
      };
      $scope.isEdit = function() {
        return !$scope.isNew();
      };
      $scope.isDraft = function() {
        return !(($scope.record != null) && ($scope.record.draft != null) && !$scope.record.draft);
      };
      $scope.cancel = function() {
        return $modalInstance.dismiss('cancel');
      };
      afterAction = function() {
        return $modalInstance.close();
      };
      currentCheckBeforeSave = $scope.checkBeforeSave;
      $scope.checkBeforeSave = function() {
        if ($scope.isFormValid('form')) {
          if (!jQuery.isFunction(currentCheckBeforeSave) || currentCheckBeforeSave()) {
            return true;
          } else {
            return false;
          }
        } else {
          _this.toast.logError(_this.$filter('translate')('error.data.input.not.valid'));
          return false;
        }
      };
      $scope.getObjectToSave = (_ref = $scope.getObjectToSave) != null ? _ref : function() {
        return $scope.record;
      };
      $scope.getSaveParams = function(params) {
        params.category = $scope.categoryName;
        if (!$scope.isNew()) {
          params.id = $scope.id;
        }
        return params;
      };
      $scope.onSaveSuccess = function() {
        return afterAction();
      };
      $scope.onSaveFailure = function() {};
      $scope.isPost = function() {
        return $scope.isNew();
      };
      $scope.enable = function() {
        var loadStatusOfData, onFailure, onSuccess, params, requestBody;
        if (!$scope.isChanged() || $scope.checkBeforeSave()) {
          loadStatusOfData = $scope.loadStatus.getStatusByDataName('enable');
          requestBody = null;
          if ($scope.isChanged()) {
            requestBody = $scope.getObjectToSave();
          }
          params = {
            who: $scope.who
          };
          params = $scope.getSaveParams(params);
          params.action = 'enable';
          onSuccess = function(data) {
            $scope.changeStatus.reset();
            return $scope.onSaveSuccess(data);
          };
          onFailure = function(error) {
            return $scope.onSaveFailure(error);
          };
          if ($scope.isPost()) {
            return _this.CtrlUtilsService.doPost(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable');
          } else {
            return _this.CtrlUtilsService.doPut(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'enable');
          }
        }
      };
      _this.CtrlInitiatorService.initCanSaveViewCtrl($scope);
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: $scope.categoryName
        };
        if ($scope.isIdRequire()) {
          params.id = $scope.id;
        }
        return _this.CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(record) {
          $scope.record = record;
          if ($scope.isUseDistributorFilter()) {
            if ($scope.isNew() || $scope.isDraft()) {
              _this.LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
                return $scope.distributors = list;
              });
            }
            if ($scope.record.distributor != null) {
              return $scope.record.distributorId = $scope.record.distributor.id;
            }
          }
        }, function(error) {
          if (error.status === 400) {
            return _this.$state.go('404');
          } else {
            return _this.toast.logError(_this.$filter('translate')('loading.error'));
          }
        });
      };
      return $scope.addInitFunction((function() {
        return $scope.reloadData();
      }), 'category-detail-popup');
    };

    return CtrlCategoryInitiatorService;

  })();

  angular.module('app').service('CtrlCategoryInitiatorService', ['$log', '$filter', 'toast', '$modal', '$state', '$stateParams', 'CtrlUtilsService', 'LoadingUtilsService', 'CATEGORY_POPUP_TEMPLATE', 'CATEGORY_POPUP_CTRL', '$confirm', 'CtrlInitiatorService', CtrlCategoryInitiatorService]);

}).call(this);

//# sourceMappingURL=ctrl-category-initiator-service.js.map

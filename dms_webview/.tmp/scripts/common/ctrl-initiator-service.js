(function() {
  'use strict';
  var CtrlInitiatorService,
    __slice = [].slice;

  CtrlInitiatorService = (function() {
    function CtrlInitiatorService($log, $filter, toast, $modal, $state, $stateParams, CtrlUtilsService, LoadingUtilsService, $confirm) {
      this.$log = $log;
      this.$filter = $filter;
      this.toast = toast;
      this.$modal = $modal;
      this.$state = $state;
      this.$stateParams = $stateParams;
      this.CtrlUtilsService = CtrlUtilsService;
      this.LoadingUtilsService = LoadingUtilsService;
      this.$confirm = $confirm;
    }

    CtrlInitiatorService.prototype.initBasicViewCtrl = function($scope) {
      var _ref, _ref1, _ref2, _ref3, _ref4, _this;
      _this = this;
      $scope.isEmpty = (_ref = $scope.isEmpty) != null ? _ref : function() {
        return false;
      };
      $scope.isOnePage = (_ref1 = $scope.isOnePage) != null ? _ref1 : function() {
        return false;
      };
      $scope.initFunctions = (_ref2 = $scope.initFunctions) != null ? _ref2 : [];
      $scope.addInitFunction = function(initFunction, name) {
        if (!jQuery.isFunction(initFunction)) {
          throw "initFunction cannot null and must be a function";
        }
        if (name != null) {
          if (_.findIndex($scope.initFunctions, function(o) {
            return (o.name != null) && o.name === name;
          }) < 0) {
            return $scope.initFunctions.push({
              "function": initFunction,
              name: name
            });
          }
        } else {
          return $scope.initFunctions.push({
            "function": initFunction,
            name: null
          });
        }
      };
      $scope.init = function() {
        var container, _i, _len, _ref3, _results;
        _ref3 = $scope.initFunctions;
        _results = [];
        for (_i = 0, _len = _ref3.length; _i < _len; _i++) {
          container = _ref3[_i];
          _results.push(container["function"]());
        }
        return _results;
      };
      $scope.refresh = (_ref3 = $scope.refresh) != null ? _ref3 : (function() {
        return $scope.init();
      });
      $scope.isFormValid = function(formName) {
        var _scope;
        _scope = $scope;
        while (_scope != null) {
          if (_scope[formName] != null) {
            return _scope[formName].$valid;
          } else {
            _scope = _scope.$$childHead;
          }
        }
        return false;
      };
      $scope.who = (_ref4 = $scope.who) != null ? _ref4 : _this.CtrlUtilsService.getWho();
      $scope.isLoading = function() {
        return $scope.loadStatus.isProcessing() && !$scope.loadStatus.isError();
      };
      $scope.isError = function() {
        return $scope.loadStatus.isError();
      };
      $scope.isDisplayContent = function() {
        return !($scope.isLoading() || $scope.isError());
      };
      return $scope.addInitFunction((function() {
        return $scope.loadStatus = _this.CtrlUtilsService.createLoadStatus('data');
      }), 'basic-view');
    };

    CtrlInitiatorService.prototype.initFilterViewCtrl = function($scope, defaultFilter) {
      var b64_to_object, b64_to_utf8, object_to_b64, utf8_to_b64, _this;
      _this = this;
      if (!jQuery.isFunction($scope.afterFilterLoad)) {
        throw "must implement $scope.afterFilterLoad";
      }
      _this.initBasicViewCtrl($scope);
      utf8_to_b64 = function(str) {
        str = window.btoa(unescape(encodeURIComponent(str)));
        str = str.replace(/\+/g, '-').replace(/\//g, '_').replace(/\=+$/, '');
        return str;
      };
      b64_to_utf8 = function(str) {
        if ((str.length % 4) === 3) {
          str = str + '=';
        } else if ((str.length % 4) === 2) {
          str = str + '==';
        } else if ((str.length % 4) === 1) {
          str = str + '===';
        }
        str = str.replace(/-/g, '+').replace(/_/g, '/');
        return decodeURIComponent(escape(window.atob(str)));
      };
      object_to_b64 = function(object) {
        if (object != null) {
          return utf8_to_b64(JSON.stringify(object));
        } else {
          return null;
        }
      };
      b64_to_object = function(str, onError) {
        var error;
        if (str != null) {
          try {
            return JSON.parse(b64_to_utf8(str));
          } catch (_error) {
            error = _error;
            _this.$log.log(error);
            if (jQuery.isFunction(onError)) {
              onError();
            }
          }
        }
        return null;
      };
      $scope.getFilterAsString = function() {
        return object_to_b64($scope.filter);
      };
      $scope.reloadForNewFilter = function() {
        return _this.$state.go('.', {
          filter: $scope.getFilterAsString()
        }, {
          reload: true
        });
      };
      return $scope.addInitFunction((function() {
        var key;
        $scope.filter = b64_to_object(_this.$stateParams.filter, function() {
          return _this.$state.go('404');
        });
        if ($scope.filter != null) {
          if (defaultFilter != null) {
            for (key in defaultFilter) {
              if ($scope.filter[key] == null) {
                $scope.filter[key] = angular.copy(defaultFilter[key]);
              }
            }
          }
        } else {
          $scope.filter = defaultFilter != null ? defaultFilter : {};
        }
        return $scope.afterFilterLoad();
      }), 'filter-view');
    };

    CtrlInitiatorService.prototype.initPagingFilterViewCtrl = function($scope) {
      var defaultFilter, _ref, _this;
      _this = this;
      if (!jQuery.isFunction($scope.getReloadDataParams)) {
        throw "must implement $scope.getReloadDataParams";
      }
      defaultFilter = {
        currentPage: 1,
        itemsPerPage: 10
      };
      $scope.afterFilterLoad = function() {
        if ($scope.filter.currentPage < 1) {
          $scope.filter.currentPage = defaultFilter.currentPage;
        }
        if ($scope.filter.itemsPerPage <= 0) {
          return $scope.filter.itemsPerPage = defaultFilter.itemsPerPage;
        }
      };
      _this.initFilterViewCtrl($scope, defaultFilter);
      $scope.pageChange = function() {
        if ($scope.loaded) {
          $scope.filter.currentPage = $scope.pagingData.currentPage;
          return $scope.reloadForNewFilter();
        }
      };
      $scope.afterLoad = (_ref = $scope.afterLoad) != null ? _ref : (function() {});
      $scope.reloadData = function() {
        var params;
        $scope.pagingData = {
          pagingMaxSize: 5
        };
        params = {
          who: $scope.who,
          page: $scope.filter.currentPage,
          size: $scope.filter.itemsPerPage
        };
        params = $scope.getReloadDataParams(params);
        if (params == null) {
          return $scope.loaded = true;
        } else {
          $scope.loaded = false;
          return _this.CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records, count) {
            var newPage;
            if (_.isEmpty(records) && count > 0 && params.page > 1) {
              newPage = Math.floor(count / $scope.filter.itemsPerPage);
              newPage += (count % $scope.filter.itemsPerPage > 0 ? 1 : 0);
              $scope.filter.currentPage = newPage;
              $scope.loaded = true;
              return $scope.reloadForNewFilter();
            } else {
              $scope.records = records;
              $scope.pagingData.count = count;
              $scope.pagingData.currentPage = $scope.filter.currentPage;
              $scope.pagingData.itemsPerPage = $scope.filter.itemsPerPage;
              $scope.afterLoad(records, count);
              return $scope.loaded = true;
            }
          }, function(error) {
            if (error.status === 400) {
              return _this.$state.go('404');
            } else {
              return _this.toast.logError(_this.$filter('translate')('loading.error'));
            }
          });
        }
      };
      $scope.isEmpty = function() {
        return _.isEmpty($scope.records);
      };
      $scope.isOnePage = function() {
        return ($scope.pagingData != null) && ($scope.pagingData.count != null) && ($scope.pagingData.itemsPerPage != null) && $scope.pagingData.count <= $scope.pagingData.itemsPerPage;
      };
      return $scope.addInitFunction((function() {
        $scope.loaded = false;
        return $scope.pagingData = {
          pagingMaxSize: 5
        };
      }), 'paging-filter-view');
    };

    CtrlInitiatorService.prototype.initCanSaveViewCtrl = function($scope) {
      var _ref, _save, _this;
      _this = this;
      if (!jQuery.isFunction($scope.checkBeforeSave)) {
        throw "must implement $scope.checkBeforeSave";
      }
      if (!jQuery.isFunction($scope.getObjectToSave)) {
        throw "must implement $scope.getObjectToSave";
      }
      if (!jQuery.isFunction($scope.getSaveParams)) {
        throw "must implement $scope.getSaveParams";
      }
      if (!jQuery.isFunction($scope.onSaveSuccess)) {
        throw "must implement $scope.onSaveSuccess";
      }
      if (!jQuery.isFunction($scope.onSaveFailure)) {
        throw "must implement $scope.onSaveFailure";
      }
      if (!jQuery.isFunction($scope.isPost)) {
        throw "must implement $scope.isPost";
      }
      _this.initBasicViewCtrl($scope);
      $scope.confirmOnSave = (_ref = $scope.confirmOnSave) != null ? _ref : (function() {
        return null;
      });
      $scope.isChanged = function() {
        return ($scope.changeStatus != null) && $scope.changeStatus.isChanged;
      };
      $scope.isDisplaySaveBtn = function() {
        return $scope.isChanged();
      };
      $scope.refresh = function() {
        if ($scope.isDisplaySaveBtn()) {
          return _this.$confirm({
            text: _this.$filter('translate')('confirm.dialog.are.you.sure.refresh')
          }).then(function() {
            return $scope.init();
          });
        } else {
          return $scope.init();
        }
      };
      _save = function() {
        var loadStatusOfData, onFailure, onSuccess, params, requestBody;
        loadStatusOfData = $scope.loadStatus.getStatusByDataName('save');
        requestBody = $scope.getObjectToSave();
        params = {
          who: $scope.who
        };
        params = $scope.getSaveParams(params);
        onSuccess = function(data) {
          $scope.changeStatus.reset();
          return $scope.onSaveSuccess(data);
        };
        onFailure = function(error) {
          return $scope.onSaveFailure(error);
        };
        if ($scope.isPost()) {
          return _this.CtrlUtilsService.doPost(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'save');
        } else {
          return _this.CtrlUtilsService.doPut(params, loadStatusOfData, requestBody, onSuccess, onFailure, 'save');
        }
      };
      $scope.save = function() {
        var confirmOnSave;
        if ($scope.checkBeforeSave()) {
          confirmOnSave = $scope.confirmOnSave();
          if (confirmOnSave != null) {
            return _this.$confirm(confirmOnSave.data, confirmOnSave.settings).then(function() {
              return _save();
            });
          } else {
            return _save();
          }
        }
      };
      return $scope.addInitFunction((function() {
        return $scope.changeStatus = _this.CtrlUtilsService.createChangeStatus();
      }), 'can-save-view');
    };

    CtrlInitiatorService.prototype.initCanBackViewCtrl = function($scope) {
      var _this;
      _this = this;
      if ($scope.defaultBackState == null) {
        throw "must set value $scope.defaultBackState";
      }
      _this.initBasicViewCtrl($scope);
      $scope.isDisplayBackBtn = function() {
        return true;
      };
      return $scope.back = function() {
        var backState, _ref;
        if (jQuery.isFunction($scope.isChanged) && $scope.isChanged()) {
          return _this.$confirm({
            text: _this.$filter('translate')('confirm.dialog.are.you.sure.back')
          }).then(function() {
            var backState, _ref;
            backState = (_ref = _this.$stateParams.parent) != null ? _ref : $scope.defaultBackState;
            return _this.$state.go(backState, {
              filter: _this.$stateParams.filter
            });
          });
        } else {
          backState = (_ref = _this.$stateParams.parent) != null ? _ref : $scope.defaultBackState;
          return _this.$state.go(backState, {
            filter: _this.$stateParams.filter
          });
        }
      };
    };

    CtrlInitiatorService.prototype.initUseDatePickerCtrl = function($scope) {
      var _this;
      _this = this;
      _this.initBasicViewCtrl($scope);
      $scope.createDatePickerModel = function(date) {
        var model;
        model = {
          date: date,
          opened: false
        };
        $scope.datePickerModels.push(model);
        return model;
      };
      $scope.removeDatePickerModel = function(model) {
        if (model != null) {
          return $scope.datePickerModels.splice(_.indexOf($scope.datePickerModels, model), 1);
        }
      };
      $scope.open = function($event, model) {
        var otherModel, _i, _len, _ref, _results;
        if (jQuery.isFunction($scope.onOpenDatePicker)) {
          $scope.onOpenDatePicker(model);
        }
        $event.preventDefault();
        $event.stopPropagation();
        if (model != null) {
          model.opened = true;
          _ref = $scope.datePickerModels;
          _results = [];
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            otherModel = _ref[_i];
            if ((otherModel != null) && otherModel !== model) {
              _results.push(otherModel.opened = false);
            }
          }
          return _results;
        }
      };
      $scope.dateOptions = {
        'year-format': 'yy',
        'starting-day': 1
      };
      $scope.dateFormat = _this.CtrlUtilsService.getUserInfo().dateFormat;
      return $scope.addInitFunction((function() {
        return $scope.datePickerModels = [];
      }), 'use-date-view');
    };

    return CtrlInitiatorService;

  })();

  angular.module('app').service('CtrlInitiatorService', ['$log', '$filter', 'toast', '$modal', '$state', '$stateParams', 'CtrlUtilsService', 'LoadingUtilsService', '$confirm', CtrlInitiatorService]);

  window.ListenerContainer = (function() {
    function ListenerContainer() {
      this.container = {};
    }

    ListenerContainer.prototype.addListener = function(name, callback) {
      var listeners;
      listeners = this.container[name];
      if (listeners == null) {
        listeners = [];
        this.container[name] = listeners;
      }
      return listeners.push(callback);
    };

    ListenerContainer.prototype.executeCallback = function() {
      var args, listener, listenerName, listeners, value, _i, _len;
      listenerName = arguments[0], args = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
      listeners = this.container[listenerName];
      if (listeners != null) {
        for (_i = 0, _len = listeners.length; _i < _len; _i++) {
          listener = listeners[_i];
          value = listener.apply(this, args);
          if (value !== null && value === false) {
            return false;
          }
        }
      }
      return true;
    };

    ListenerContainer.prototype.getContainer = function() {
      return this.container;
    };

    ListenerContainer.prototype.merge = function(listenerContainer) {
      var listener, listenerName, listeners, _container, _results;
      if (listenerContainer != null) {
        _container = listenerContainer.getContainer();
        if (_container != null) {
          _results = [];
          for (listenerName in _container) {
            listeners = _container[listenerName];
            _results.push((function() {
              var _i, _len, _results1;
              _results1 = [];
              for (_i = 0, _len = listeners.length; _i < _len; _i++) {
                listener = listeners[_i];
                _results1.push(this.addListener(listenerName, listener));
              }
              return _results1;
            }).call(this));
          }
          return _results;
        }
      }
    };

    return ListenerContainer;

  })();

}).call(this);

//# sourceMappingURL=ctrl-initiator-service.js.map

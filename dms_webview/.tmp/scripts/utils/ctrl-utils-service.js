(function() {
  'use strict';
  var ChangeStatus, CtrlUtilsService, LoadStatus,
    __slice = [].slice;

  CtrlUtilsService = (function() {
    function CtrlUtilsService(Factory, $log, $filter, $token, toast) {
      this.Factory = Factory;
      this.$log = $log;
      this.$filter = $filter;
      this.$token = $token;
      this.toast = toast;
    }

    CtrlUtilsService.prototype.getWho = function() {
      var roleCode;
      roleCode = this.$token.getRoleCode();
      if (roleCode != null) {
        switch (roleCode) {
          case 'SUPER':
            return 'super-admin';
          case 'SUPPORTER':
            return 'supporter';
          case 'AD':
            return 'admin';
          case 'OBS':
            return 'observer';
          case 'SUP':
            return 'supervisor';
          case 'DIS':
            return 'distributor';
          default:
            return null;
        }
      } else {
        return null;
      }
    };

    CtrlUtilsService.prototype.getUserLanguage = function() {
      return this.$token.getUserLanguage();
    };

    CtrlUtilsService.prototype.doPost = function(params, loadStatusOfData, requestBody, callbackSuccess, callbackFailure, action) {
      var onFailure, onSuccess, _this;
      _this = this;
      loadStatusOfData.processing = true;
      action = action != null ? action : 'save';
      onSuccess = function(data) {
        loadStatusOfData.processing = false;
        if (jQuery.isFunction(callbackSuccess)) {
          callbackSuccess(data);
        }
        return _this.toast.logSuccess(_this.$filter('translate')(action + '.success'));
      };
      onFailure = function(error) {
        loadStatusOfData.processing = false;
        _this.$log.log(error);
        if (jQuery.isFunction(callbackFailure)) {
          callbackFailure(error);
        }
        if (error.status === 400) {
          return _this.toast.logError(_this.$filter('translate')(error.data.meta.error_message));
        } else {
          return _this.toast.logError(_this.$filter('translate')(action + '.error'));
        }
      };
      return _this.Factory.doPost(params, requestBody, onSuccess, onFailure);
    };

    CtrlUtilsService.prototype.doPut = function(params, loadStatusOfData, requestBody, callbackSuccess, callbackFailure, action) {
      var onFailure, onSuccess, _this;
      _this = this;
      loadStatusOfData.processing = true;
      action = action != null ? action : 'save';
      onSuccess = function(data) {
        loadStatusOfData.processing = false;
        if (jQuery.isFunction(callbackSuccess)) {
          callbackSuccess(data);
        }
        return _this.toast.logSuccess(_this.$filter('translate')(action + '.success'));
      };
      onFailure = function(error) {
        loadStatusOfData.processing = false;
        _this.$log.log(error);
        if (jQuery.isFunction(callbackFailure)) {
          callbackFailure(error);
        }
        if (error.status === 400) {
          return _this.toast.logError(_this.$filter('translate')(error.data.meta.error_message));
        } else {
          return _this.toast.logError(_this.$filter('translate')(action + '.error'));
        }
      };
      return _this.Factory.doPut(params, requestBody, onSuccess, onFailure);
    };

    CtrlUtilsService.prototype.doDelete = function(params, loadStatusOfData, callbackSuccess, callbackFailure) {
      var onFailure, onSuccess, _this;
      _this = this;
      loadStatusOfData.processing = true;
      onSuccess = function(data) {
        loadStatusOfData.processing = false;
        if (jQuery.isFunction(callbackSuccess)) {
          callbackSuccess(data);
        }
        return _this.toast.logSuccess(_this.$filter('translate')('delete.success'));
      };
      onFailure = function(error) {
        loadStatusOfData.processing = false;
        _this.$log.log(error);
        if (jQuery.isFunction(callbackFailure)) {
          callbackFailure(error);
        }
        if (error.status === 400) {
          return _this.toast.logError(_this.$filter('translate')(error.data.meta.error_message));
        } else {
          return _this.toast.logError(_this.$filter('translate')('delete.error'));
        }
      };
      return _this.Factory.doDelete(params, onSuccess, onFailure);
    };

    CtrlUtilsService.prototype.loadSingleData = function(params, loadStatusOfData, callbackSuccess, callbackFailure) {
      var onFailure, onSuccess, _this;
      _this = this;
      loadStatusOfData.processing = true;
      loadStatusOfData.error = false;
      onSuccess = function(data) {
        loadStatusOfData.processing = false;
        if (jQuery.isFunction(callbackSuccess)) {
          return callbackSuccess(data != null ? data : {});
        }
      };
      onFailure = function(error) {
        loadStatusOfData.processing = false;
        loadStatusOfData.error = true;
        _this.$log.log(error);
        if (jQuery.isFunction(callbackFailure)) {
          return callbackFailure(error);
        }
      };
      return _this.Factory.doGet(params, onSuccess, onFailure);
    };

    CtrlUtilsService.prototype.loadListData = function(params, loadStatusOfData, callbackSuccess, callbackFailure) {
      var onFailure, onSuccess, _this;
      _this = this;
      loadStatusOfData.processing = true;
      loadStatusOfData.error = false;
      onSuccess = function(data) {
        var count, list;
        loadStatusOfData.processing = false;
        list = (data != null) && (data.list != null) ? data.list : [];
        count = (data != null) && (data.list != null) ? data.count : 0;
        if (jQuery.isFunction(callbackSuccess)) {
          return callbackSuccess(list, count);
        }
      };
      onFailure = function(error) {
        loadStatusOfData.processing = false;
        loadStatusOfData.error = true;
        _this.$log.log(error);
        if (jQuery.isFunction(callbackFailure)) {
          return callbackFailure(error);
        }
      };
      return _this.Factory.doGet(params, onSuccess, onFailure);
    };

    CtrlUtilsService.prototype.createLoadStatus = function() {
      var dataNames;
      dataNames = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return new LoadStatus(dataNames);
    };

    CtrlUtilsService.prototype.createChangeStatus = function() {
      return new ChangeStatus();
    };

    CtrlUtilsService.prototype.printOrder = function(orderId) {
      var popupWin, printContents;
      printContents = document.getElementById(orderId).innerHTML;
      popupWin = window.open();
      popupWin.document.open();
      popupWin.document.write('<html>' + '<head>' + '<link rel="stylesheet" type="text/css" href="styles/main.css"/>' + '</head>' + '<body onload="window.print()" class="print">' + printContents + '</body>' + '</html>');
      return popupWin.document.close();
    };

    CtrlUtilsService.prototype.getUserInfo = function() {
      return this.$token.getUserInfo();
    };

    CtrlUtilsService.prototype.getDefaultZoom = function() {
      return 14;
    };

    return CtrlUtilsService;

  })();

  LoadStatus = (function() {
    function LoadStatus(dataNames) {
      var dataName, _i, _len;
      this.status = {};
      for (_i = 0, _len = dataNames.length; _i < _len; _i++) {
        dataName = dataNames[_i];
        this.status[dataName] = {
          processing: false,
          error: false
        };
      }
    }

    LoadStatus.prototype.getStatusByDataName = function(dataName) {
      var _status;
      _status = this.status[dataName];
      if (_status == null) {
        _status = {
          processing: false,
          error: false
        };
        this.status[dataName] = _status;
      }
      return _status;
    };

    LoadStatus.prototype.isProcessing = function() {
      var dataName;
      if (this.status != null) {
        for (dataName in this.status) {
          if (this.status[dataName].processing) {
            return true;
          }
        }
      }
      return false;
    };

    LoadStatus.prototype.isError = function() {
      var dataName;
      if (this.status != null) {
        for (dataName in this.status) {
          if (this.status[dataName].error) {
            return true;
          }
        }
      }
      return false;
    };

    return LoadStatus;

  })();

  ChangeStatus = (function() {
    function ChangeStatus() {
      this.isChanged = false;
    }

    ChangeStatus.prototype.markAsChanged = function() {
      return this.isChanged = true;
    };

    ChangeStatus.prototype.reset = function() {
      return this.isChanged = false;
    };

    return ChangeStatus;

  })();

  angular.module('app').service('CtrlUtilsService', ['Factory', '$log', '$filter', '$token', 'toast', CtrlUtilsService]);

}).call(this);

//# sourceMappingURL=ctrl-utils-service.js.map

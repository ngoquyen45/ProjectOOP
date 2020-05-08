(function() {
  'use strict';
  var LoadingUtilsService;

  LoadingUtilsService = (function() {
    function LoadingUtilsService(CtrlUtilsService) {
      this.CtrlUtilsService = CtrlUtilsService;
    }

    LoadingUtilsService.prototype.loadSupervisors = function(who, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'supervisor'
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadProductCategories = function(who, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'product-category',
        subCategory: 'all'
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadUOMs = function(who, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'uom',
        subCategory: 'all'
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadCustomerTypes = function(who, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'customer-type',
        subCategory: 'all'
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadDistributors = function(who, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'distributor',
        subCategory: 'all'
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadProductsByDistributor = function(who, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'product',
        subCategory: 'all'
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadAreasByDistributor = function(who, distributorId, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'area',
        subCategory: 'all',
        distributorId: distributorId
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadRoutesByDistributor = function(who, distributorId, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'route',
        subCategory: 'all',
        distributorId: distributorId
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadSalesmenByDistributor = function(who, distributorId, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'salesman',
        distributorId: distributorId
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    LoadingUtilsService.prototype.loadStoreCheckersByDistributor = function(who, distributorId, loadStatusOfData, callback) {
      var params;
      params = {
        who: who,
        category: 'store-checker',
        distributorId: distributorId
      };
      return this.CtrlUtilsService.loadListData(params, loadStatusOfData, callback);
    };

    return LoadingUtilsService;

  })();

  angular.module('app').service('LoadingUtilsService', ['CtrlUtilsService', LoadingUtilsService]);

}).call(this);

//# sourceMappingURL=loading-utils-service.js.map

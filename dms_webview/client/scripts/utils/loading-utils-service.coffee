'use strict'

class LoadingUtilsService
    constructor: (@CtrlUtilsService) ->

    #  LOAD SUPERVISOR
    loadSupervisors: (who, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'supervisor'

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    #  LOAD PRODUCT CATEGORY
    loadProductCategories: (who, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'product-category'
            subCategory: 'all'

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    #  LOAD UOM
    loadUOMs: (who, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'uom'
            subCategory: 'all'

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    #  LOAD PRODUCT CATEGORY
    loadCustomerTypes: (who, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'customer-type'
            subCategory: 'all'

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    #  LOAD DISTRIBUTOR
    loadDistributors: (who, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'distributor'
            subCategory: 'all'

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    #  LOAD PRODUCT
    loadProductsByDistributor: (who, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'product'
            subCategory: 'all'

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    loadAreasByDistributor: (who, distributorId, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'area'
            subCategory: 'all'
            distributorId : distributorId

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    loadRoutesByDistributor: (who, distributorId, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'route'
            subCategory: 'all'
            distributorId : distributorId

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    loadSalesmenByDistributor: (who, distributorId, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'salesman'
            distributorId : distributorId

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

    loadStoreCheckersByDistributor: (who, distributorId, loadStatusOfData, callback) ->
        params =
            who: who
            category: 'store-checker'
            distributorId : distributorId

        @CtrlUtilsService.loadListData(params, loadStatusOfData, callback)

# **********************************************************************************************************************
angular.module('app')
.service('LoadingUtilsService', [
    'CtrlUtilsService'
    LoadingUtilsService
])

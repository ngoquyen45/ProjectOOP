'use strict'

angular.module('app')

.controller('DistributorPriceListCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) ->
            $scope.title = 'distributor.price.list.title'

            $scope.goToImport = -> $state.go('import-price-list', { filter: $stateParams.filter })

            # LOADING
            $scope.reloadData = ->
                $scope.datas = []

                params =
                    who: $scope.who
                    category: 'price-list'

                CtrlUtilsService.loadListData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (records) ->
                        productCategoryMap = _.groupBy(records, (product) -> product.productCategory.name )
                        $scope.datas = []
                        for productCategory of productCategoryMap
                            $scope.datas.push({ name: productCategory, products: productCategoryMap[productCategory]})
                        $scope.datas = _.sortByOrder($scope.datas, ['name'], ['asc'])

                    ->
                        toast.logError($filter('translate')('loading.error'))
                )

            $scope.isEmpty = -> _.isEmpty($scope.datas)

            # SAVE
            $scope.checkBeforeSave = ->
                if $scope.isFormValid('form')
                    return true
                else
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                    return false

            $scope.getObjectToSave = ->
                saveData = {priceList: {}}

                for data in $scope.datas
                    for product in data.products
                        if product.distributorPrice?
                            saveData.priceList[product.id] = product.distributorPrice

                return saveData

            $scope.getSaveParams = (params) ->
                params.category = 'price-list'
                return params

            $scope.onSaveSuccess = -> # DO NOTHING
            $scope.onSaveFailure = -> # DO NOTHING
            $scope.isPost = -> false

             # INIT
            CtrlInitiatorService.initCanSaveViewCtrl($scope)

            $scope.addInitFunction(->
                $scope.reloadData()
            )

            $scope.init()

    ])

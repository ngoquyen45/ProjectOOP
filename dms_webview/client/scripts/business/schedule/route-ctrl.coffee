'use strict'

angular.module('app')

.controller('RouteCtrl', [
        '$scope', 'CtrlCategoryInitiatorService'
        ($scope, CtrlCategoryInitiatorService) ->
            $scope.title = 'route.title'
            $scope.categoryName = 'route'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> $scope.who isnt 'distributor'

            if $scope.isUseDistributorFilter()
                $scope.columns = [
                    { header: 'name', property: 'name' }
                    { header: 'distributor', property: (record) -> record.distributor.name }
                    { header: 'salesman', property: (record) -> if record.salesman? then record.salesman.fullname else '' }
                ]
            else
                $scope.columns = [
                    { header: 'name', property: 'name' }
                    { header: 'salesman', property: (record) -> if record.salesman? then record.salesman.fullname else '' }
                ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
    ])

.controller('RouteDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter) ->
            $scope.title = 'route.title'
            $scope.categoryName  = 'route'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'route'

            $scope.isUseDistributorFilter = -> $scope.who isnt 'distributor'

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                if $scope.record?
                    if $scope.isDraft()
                        LoadingUtilsService.loadDistributors(
                            $scope.who
                            $scope.loadStatus.getStatusByDataName('distributors')
                            (list) -> $scope.distributors = list
                        )

                    if $scope.record.distributor? and $scope.record.distributor.id?
                        $scope.record.distributorId = $scope.record.distributor.id
                        reloadSalesmen()

                    if $scope.record.salesman? and $scope.record.salesman.id?
                        $scope.record.salesmanId = $scope.record.salesman.id

            reloadSalesmen = ->
                if $scope.record? and $scope.record.distributorId?
                    LoadingUtilsService.loadSalesmenByDistributor(
                        $scope.who
                        $scope.record.distributorId
                        $scope.loadStatus.getStatusByDataName('salesmen')
                        (list) -> $scope.salesmen = _.union(
                            [
                                { id: null, fullname: '-- ' + $filter('translate')('none') + ' --'  }
                            ]
                            list
                        )
                    )
                else
                    $scope.salesmen = []

            $scope.changeDistributor = ->
                $scope.record.salesmanId = null
                $scope.changeStatus.markAsChanged()
                reloadSalesmen()

            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)

            $scope.init()
    ])


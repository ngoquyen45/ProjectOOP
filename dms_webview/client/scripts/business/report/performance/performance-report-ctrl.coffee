'use strict'

chartOptions = {
    colors: ["#26A69A", "#607D8B", "#26C6DA"]
    tooltip: { show: true }
    tooltipOpts:
        defaultTheme: false
    grid:
        hoverable: true
        clickable: true
        tickColor: "#f9f9f9"
        borderWidth: 1
        borderColor: "#eeeeee"
    yaxes: [ {}, { position: "right"} ]
}

angular.module('app')

.controller('PerformanceReportCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state) ->
            $scope.title = 'performance.report.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'

            $scope.afterFilterLoad = -> # DO NOTHING

            $scope.goToReport = ->
                $scope.filter.date = globalUtils.createIsoDate($scope.date.date)
                if $scope.isUseDistributorFilter() and not $scope.filter.distributorId?
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                else if not $scope.filter.salesmanId?
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                else
                    $state.go('performance-report-salesman', { filter: $scope.getFilterAsString() })

            $scope.isDisplaySalesman = -> not $scope.isUseDistributorFilter() or $scope.filter.distributorId?

            $scope.changeDistributor = ->
                $scope.filter.salesmanId = null
                $scope.loadSalesmen()

            $scope.loadSalesmen = ->
                LoadingUtilsService.loadSalesmenByDistributor(
                    $scope.who
                    $scope.filter.distributorId
                    $scope.loadStatus.getStatusByDataName('salesmen')
                    (list) -> $scope.salesmen = list
                )

            CtrlInitiatorService.initFilterViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.addInitFunction( ->
                if $scope.filter.date?
                    $scope.date = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.date))
                else
                    $scope.date = $scope.createDatePickerModel(new Date())

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )

                if $scope.isDisplaySalesman()
                    $scope.loadSalesmen()
            )

            $scope.init()
    ])

.controller('PerformanceReportSalesmanCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'performance.report.salesman.title'
            $scope.defaultBackState = 'performance-report'

            $scope.afterFilterLoad = -> # DO NOTHING

            tooltip = (label, xval, yval, flotItem) -> label + ' - ' + $scope.nameMap[xval - 1] + ': ' + $filter('number')(yval, 0)

            afterLoad = ->
                revenues = []
                nbOrders = []
                ticks = []
                $scope.nameMap = []

                $scope.total = { revenue: 0, nbOrder: 0 }
                for salesResultDaily, i in $scope.record.salesResultsDaily
                    revenues.push([ (i + 1), salesResultDaily.salesResult.revenue ])
                    nbOrders.push([ (i + 1), salesResultDaily.salesResult.nbOrder ])

                    if (i + 1) is 1 or (i + 1) % 5 is 0
                        ticks.push([(i + 1), '' + (i + 1)])
                    else
                        ticks.push([(i + 1), ''])

                    $scope.nameMap.push($filter('isoDate')(salesResultDaily.date))

                $scope.chart.data = [
                    data: revenues
                    label: $filter('translate')('revenue')
                ,
                    data: nbOrders
                    label: $filter('translate')('order')
                    yaxis: 2
                ]

                for data, i in $scope.chart.data
                    if i is 0 and revenues.length < 20
                        data.bars = {
                            show: true
                            fill: true
                            barWidth: .1
                            align: 'center'
                        }
                    else
                        data.lines = {
                            show: true
                            fill: false
                            fillColor: { colors: [ { opacity: 0 }, { opacity: 0.3 } ] }
                        }
                        data.points = {
                            show: true
                            lineWidth: 2
                            fill: false
                            fillColor: "#ffffff"
                            symbol: "circle"
                            radius: 5
                        }
                        data.lines.fill = true if i is 0

                $scope.chart.options.xaxis = { autoscaleMargin: .10, ticks: ticks }

            $scope.isDisplayChart = -> $scope.chart.data? and $scope.chart.data[0].data? and $scope.chart.data[0].data.length > 1

            $scope.getMonthDisplay = -> moment($scope.date).format('MM/YYYY')

            $scope.clickOnProductCategory = (productCategory) ->
                if productCategory? and productCategory.id?
                    if $scope.productCategoriySalesQuantityId? and $scope.productCategoriySalesQuantityId is productCategory.id
                        $scope.productCategoriySalesQuantityId = null
                    else
                        $scope.productCategoriySalesQuantityId = productCategory.id

            $scope.isProductCategoryOpened = (productCategory) ->
                productCategory? and $scope.productCategoriySalesQuantityId is productCategory.id

            CtrlInitiatorService.initCanBackViewCtrl($scope)
            CtrlInitiatorService.initFilterViewCtrl($scope)

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'performance'
                    subCategory: 'by-salesman'
                    month: $scope.date.getMonth()
                    year: $scope.date.getFullYear()
                    salesmanId: $scope.filter.salesmanId

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (data) ->
                        $scope.record = data
                        afterLoad()
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                $scope.chart = {}
                $scope.chart.options = angular.copy(chartOptions)
                $scope.chart.options.tooltip.content = tooltip

                $scope.productCategoriySalesQuantityId = null

                if $scope.filter? and $scope.filter.date?
                    $scope.date = globalUtils.parseIsoDate($scope.filter.date)
                    if not $scope.date? or not $scope.filter.salesmanId?
                        $state.go('404')
                    else
                        $scope.reloadData()
                else
                    $state.go('404')
            )

            $scope.init()
    ])

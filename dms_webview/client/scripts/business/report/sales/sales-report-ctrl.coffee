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

.controller('SalesReportCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state) ->
            $scope.title = 'sales.report.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'

            $scope.afterFilterLoad = -> # DO NOTHING

            $scope.goToReport = ->
                $scope.filter.date = globalUtils.createIsoDate($scope.date.date)
                if $scope.filter.reportType is 'daily'
                    $state.go('sales-report-daily', { filter: $scope.getFilterAsString() })
                else if $scope.filter.reportType is 'distributor'
                    $state.go('sales-report-distributor', { filter: $scope.getFilterAsString() })
                else if $scope.filter.reportType is 'product'
                    if $scope.filter.productCategoryId?
                        $state.go('sales-report-product', { filter: $scope.getFilterAsString() })
                    else
                        toast.logError($filter('translate')('error.data.input.not.valid'))
                else if $scope.filter.reportType is 'salesman'
                    $state.go('sales-report-salesman', { filter: $scope.getFilterAsString() })

            $scope.isDisplayProductCategory = -> $scope.filter.reportType is 'product'

            CtrlInitiatorService.initFilterViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.addInitFunction( ->
                if not $scope.filter.reportType?
                    $scope.filter.reportType = 'daily'

                if $scope.filter.date?
                    $scope.date = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.date))
                else
                    $scope.date = $scope.createDatePickerModel(new Date())

                LoadingUtilsService.loadProductCategories(
                    $scope.who
                    $scope.loadStatus.getStatusByDataName('productCategories')
                    (list) -> $scope.productCategories = list
                )
            )

            $scope.init()
    ])

.controller('SalesReportDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = '' # UPDATE LATER
            $scope.defaultBackState = 'sales-report'

            $scope.afterFilterLoad = -> # DO NOTHING

            CtrlInitiatorService.initCanBackViewCtrl($scope)
            CtrlInitiatorService.initFilterViewCtrl($scope)

            tooltip = (label, xval, yval, flotItem) -> label + ' - ' + $scope.nameMap[xval - 1] + ': ' + $filter('number')(yval, 0)

            afterLoad = ->
                revenues = []
                nbOrders = []
                ticks = []
                $scope.nameMap = []

                $scope.total = { revenue: 0, nbOrder: 0 }
                for dto, i in $scope.records
                    revenues.push([ (i + 1), dto.salesResult.revenue ])
                    nbOrders.push([ (i + 1), dto.salesResult.nbOrder ])

                    $scope.total.revenue += dto.salesResult.revenue
                    $scope.total.nbOrder += dto.salesResult.nbOrder

                    if $scope.filter.reportType is 'daily'
                        if (i + 1) is 1 or (i + 1) % 5 is 0
                            ticks.push([(i + 1), '' + (i + 1)])
                        else
                            ticks.push([(i + 1), ''])
                        $scope.nameMap.push((i + 1) + '/' + moment($scope.date).format('MM/YYYY'))
                    else if $scope.filter.reportType is 'distributor'
                        ticks.push([(i + 1), globalUtils.getChartLabel(dto.name)])
                        $scope.nameMap.push(globalUtils.escapeHTML(dto.name))
                    else if $scope.filter.reportType is 'product'
                        ticks.push([(i + 1), globalUtils.getChartLabel(dto.name)])
                        $scope.nameMap.push(globalUtils.escapeHTML(dto.name))
                    else if $scope.filter.reportType is 'salesman'
                        ticks.push([(i + 1), globalUtils.getChartLabel(dto.fullname)])
                        $scope.nameMap.push(globalUtils.escapeHTML(dto.fullname))

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

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'report'
                    subCategory: 'sales'
                    month: $scope.date.getMonth()
                    year: $scope.date.getFullYear()

                if $scope.filter.reportType is 'daily'
                    params.param = 'daily'
                else if $scope.filter.reportType is 'distributor'
                    params.param = 'by-distributor'
                else if $scope.filter.reportType is 'product'
                    params.param = 'by-product'
                    params.productCategoryId = $scope.filter.productCategoryId
                else if $scope.filter.reportType is 'salesman'
                    params.param = 'by-salesman'

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (data) ->
                        $scope.records = data.list
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

                if $scope.filter? and $scope.filter.date?
                    $scope.date = globalUtils.parseIsoDate($scope.filter.date)
                    if not $scope.date? or (not $scope.filter.productCategoryId? and $scope.filter.reportType is 'product')
                        $state.go('404')
                    else
                        reportTitle = null
                        if $scope.filter.reportType is 'daily'
                            reportTitle = $filter('translate')('sales.report.daily.title')
                        else if $scope.filter.reportType is 'distributor'
                            reportTitle = $filter('translate')('sales.report.distributor.title')
                        else if $scope.filter.reportType is 'product'
                            reportTitle = $filter('translate')('sales.report.product.title')
                        else if $scope.filter.reportType is 'salesman'
                            reportTitle = $filter('translate')('sales.report.salesman.title')

                        $scope.title = reportTitle + ' - ' + moment($scope.date).format('MM-YYYY')

                        $scope.reloadData()
                else
                    $state.go('404')
            )

            $scope.init()
    ])

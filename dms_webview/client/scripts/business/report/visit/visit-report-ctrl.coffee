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
}

angular.module('app')

.controller('VisitReportCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state) ->
            $scope.title = 'visit.report.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'

            $scope.afterFilterLoad = -> # DO NOTHING

            $scope.goToReport = ->
                $scope.filter.date = globalUtils.createIsoDate($scope.date.date)
                if $scope.filter.reportType is 'daily'
                    $state.go('visit-report-daily', { filter: $scope.getFilterAsString() })
                else if $scope.filter.reportType is 'distributor'
                    $state.go('visit-report-distributor', { filter: $scope.getFilterAsString() })
                else if $scope.filter.reportType is 'salesman'
                    $state.go('visit-report-salesman', { filter: $scope.getFilterAsString() })

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
            )

            $scope.init()
    ])

.controller('VisitReportDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = '' # UPDATE LATER
            $scope.defaultBackState = 'visit-report'
            tooltip = (label, xval, yval, flotItem) -> label + ' - ' + $scope.nameMap[xval - 1] + ': ' + $filter('number')(yval, 0)

            $scope.afterFilterLoad = -> # DO NOTHING

            CtrlInitiatorService.initCanBackViewCtrl($scope)
            CtrlInitiatorService.initFilterViewCtrl($scope)

            afterLoad = ->
                nbVisits = []
                nbVisitErrorDurations = []
                nbVisitErrorPositions = []
                ticks = []
                $scope.nameMap = []

                $scope.total = { nbVisit: 0, nbVisitErrorDuration: 0, nbVisitErrorPosition: 0 }
                for dto, i in $scope.records
                    nbVisits.push([ (i + 1), dto.visitResult.nbVisit ])
                    nbVisitErrorDurations.push([ (i + 1), dto.visitResult.nbVisitErrorDuration ])
                    nbVisitErrorPositions.push([ (i + 1), dto.visitResult.nbVisitErrorPosition ])

                    $scope.total.nbVisit += dto.visitResult.nbVisit
                    $scope.total.nbVisitErrorDuration += dto.visitResult.nbVisitErrorDuration
                    $scope.total.nbVisitErrorPosition += dto.visitResult.nbVisitErrorPosition

                    if $scope.filter.reportType is 'daily'
                        if (i + 1) is 1 or (i + 1) % 5 is 0
                            ticks.push([(i + 1), '' + (i + 1)])
                        else
                            ticks.push([(i + 1), ''])
                        $scope.nameMap.push((i + 1) + '/' + moment($scope.date).format('MM/YYYY'))
                    else if $scope.filter.reportType is 'distributor'
                        ticks.push([(i + 1), globalUtils.getChartLabel(dto.name)])
                        $scope.nameMap.push(globalUtils.escapeHTML(dto.name))
                    else if $scope.filter.reportType is 'salesman'
                        ticks.push([(i + 1), globalUtils.getChartLabel(dto.fullname)])
                        $scope.nameMap.push(globalUtils.escapeHTML(dto.fullname))

                $scope.chart.data = [
                    data: nbVisits
                    label: $filter('translate')('visit')
                ,
                    data: nbVisitErrorPositions
                    label: $filter('translate')('visit.error.duration')
                ]

                for data, i in $scope.chart.data
                    if i is 0 and nbVisits.length < 20
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
                    subCategory: 'visit'
                    month: $scope.date.getMonth()
                    year: $scope.date.getFullYear()

                if $scope.filter.reportType is 'daily'
                    params.param = 'daily'
                else if $scope.filter.reportType is 'distributor'
                    params.param = 'by-distributor'
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
                            reportTitle = $filter('translate')('visit.report.daily.title')
                        else if $scope.filter.reportType is 'distributor'
                            reportTitle = $filter('translate')('visit.report.distributor.title')
                        else if $scope.filter.reportType is 'salesman'
                            reportTitle = $filter('translate')('visit.report.salesman.title')

                        $scope.title = reportTitle + ' - ' + moment($scope.date).format('MM-YYYY')

                        $scope.reloadData()
                else
                    $state.go('404')
            )

            $scope.init()
    ])

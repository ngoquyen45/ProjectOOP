'use strict'

angular.module('app')

.controller('SurveyReportCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$token'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $token) ->
            $scope.title = 'survey.report.title'

            $scope.goToReport = (record) ->
                $state.go('survey-report-detail', { id: record.id, filter: $stateParams.filter })

            $scope.export = (record) ->
                location.href = ADDRESS_BACKEND + $scope.who + '/report/survey/' +
                        record.id + '/export?access_token=' + $token.getAccessToken() +
                        '&lang=' + CtrlUtilsService.getUserLanguage()

            # LOADING
            $scope.getReloadDataParams = (params) ->
                params.category = 'report'
                params.subCategory = 'survey'
                return params

            CtrlInitiatorService.initPagingFilterViewCtrl($scope)

            $scope.addInitFunction( ->
                $scope.reloadData()
            )

            $scope.init()
    ])

.controller('SurveyReportDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'survey.report.title'
            $scope.defaultBackState = 'survey-report'

            $scope.afterFilterLoad = -> # DO NOTHING

            CtrlInitiatorService.initCanBackViewCtrl($scope)
            CtrlInitiatorService.initFilterViewCtrl($scope)

            pieOptions = {
                series:
                    pie:
                        show: true
                legend:
                    show: true
                grid:
                    hoverable: true
                    clickable: false
                colors: ["#60CD9B", "#66B5D7", "#EEC95A", "#E87352"]
                tooltip: true
                tooltipOpts:
                    content: "%p.0%, %s"
                    defaultTheme: false
            }

            afterLoad = ->
                if $scope.record? and $scope.record.questions?
                    for question in $scope.record.questions
                        question.data = [];
                        question.option = {};

                        if question.options?
                            for option in question.options
                                question.data.push(
                                    {
                                        label: globalUtils.escapeHTML(option.name) + ' (' + option.result + ') '
                                        data: option.result
                                    }
                                )

                            question.option = pieOptions

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'report'
                    subCategory: 'survey'
                    id: $stateParams.id

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
                if not $stateParams.id?
                    $state.go('404')
                else
                    $scope.reloadData()
            )

            $scope.init()
    ])

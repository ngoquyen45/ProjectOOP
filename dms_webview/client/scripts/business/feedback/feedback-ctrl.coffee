'use strict'

angular.module('app')

.controller('FeedbackCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams) ->
            $scope.title = 'feedback.title'

            # FILTER
            $scope.filterChange = ->
                $scope.filter.currentPage = 1
                $scope.pageChange()

            $scope.goToDetail = (record) ->  $state.go('feedback-detail', { id: record.id, filter: $stateParams.filter })

            $scope.getFeedbackDisplay = (feedbacks) -> if feedbacks? and feedbacks.length > 0 then feedbacks[0] else 'N/A'
            $scope.getTimeDisplay = (isoTime) ->
                date = globalUtils.parseIsoTime(isoTime)

                if $scope.today.getFullYear() is date.getFullYear() and $scope.today.getMonth() is date.getMonth() and $scope.today.getDate() == date.getDate()
                    return moment(date).format('HH:mm')
                else
                    return moment(date).format(CtrlUtilsService.getUserInfo().dateFormat.toUpperCase())

            # LOADING
            $scope.getReloadDataParams = (params) ->
                params.category = 'feedback'

                if $scope.filter.orderForDP?
                    if $scope.filter.orderForDP is 'normal'
                        params.orderForDP = false
                    else if $scope.filter.orderForDP is 'dp'
                        params.orderForDP = true

                return params

            CtrlInitiatorService.initPagingFilterViewCtrl($scope)

            $scope.addInitFunction( ->
                $scope.today = new Date()

                $scope.reloadData()
            )

            $scope.init()
])

.controller('FeedbackDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
         $filter, toast, $state, $stateParams) ->
            $scope.title = 'feedback.title'
            $scope.defaultBackState = 'feedback'

            CtrlInitiatorService.initCanBackViewCtrl($scope)

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'feedback'
                    id: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) ->
                        $scope.record = record
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )

            $scope.addInitFunction( ->
                if not $stateParams.id?
                    $state.go('404')

                $scope.reloadData()
            )

            $scope.init()
    ])


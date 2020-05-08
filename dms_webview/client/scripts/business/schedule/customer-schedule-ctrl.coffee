'use strict'

angular.module('app')

.controller('CustomerScheduleCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) ->
            $scope.title = 'customer.schedule.title'
            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'
            $scope.mustSelectDistributor = -> $scope.isUseDistributorFilter() and not $scope.filter.distributorId?

            $scope.goToImport = -> $state.go('import-customer-schedule', { filter: $stateParams.filter })

            $scope.dayOfWeeks = [
                { id: null, name: $filter('translate')('all') }
                { id: 2, name: $filter('translate')('monday') }
                { id: 3, name: $filter('translate')('tuesday') }
                { id: 4, name: $filter('translate')('wednesday') }
                { id: 5, name: $filter('translate')('thursday') }
                { id: 6, name: $filter('translate')('friday') }
                { id: 7, name: $filter('translate')('saturday') }
                { id: 1, name: $filter('translate')('sunday') }
            ]

            $scope.itemsPerPageOptions = [
                { id: 10, name: '10' }
                { id: 20, name: '20' }
                { id: 30, name: '30' }
            ]

            # INIT HEADER FOR WEEKS
            $scope.useWeekOfFrequency = CtrlUtilsService.getUserInfo().numberWeekOfFrequency > 1
            $scope.numberWeekOfFrequency = if $scope.useWeekOfFrequency then CtrlUtilsService.getUserInfo().numberWeekOfFrequency else 0
            $scope.weeks = []
            if $scope.useWeekOfFrequency
                weekIndex = 1
                createWeekHeader = ->
                    $scope.weeks.push( { header: 'W' + weekIndex, index: weekIndex  } )
                    weekIndex++
                createWeekHeader() until weekIndex > $scope.numberWeekOfFrequency

            createNewScheduleItem = ->
                item =
                    monday: false
                    tuesday: false
                    wednesday: false
                    thursday: false
                    friday: false
                    saturday: false
                    sunday: false

                if $scope.useWeekOfFrequency
                    item.weeksConverted = convertWeeks([])
                    weekConverted.selected = true for weekConverted in item.weeksConverted

                return item

            convertWeeks = (weeks) ->
                weekIndex = 1
                weeksConverted = []
                f = ->
                    weeksConverted.push( { selected: _.include(weeks, weekIndex), index: weekIndex  } )
                    weekIndex++

                f() until weekIndex > $scope.numberWeekOfFrequency

                return weeksConverted

            reconvertWeeks = (weeksConverted) ->
                if $scope.useWeekOfFrequency
                    weeks = []
                    weeks.push(weekConverted.index) for weekConverted in weeksConverted when weekConverted.selected

                    return weeks
                else
                    return null

            isItemDayValid = (item) ->
                item? and (item.monday or item.tuesday or item.wednesday or item.thursday  or item.friday or item.saturday or item.sunday)

            $scope.editRoute = (record) ->
                $scope.changeStatus.markAsChanged()
                if not record.schedule.routeId?
                    record.schedule.items = []
                    record.schedule.items.push(createNewScheduleItem())

            # LOADING
            $scope.getReloadDataParams = (params) ->
                if $scope.mustSelectDistributor()
                    return null
                else
                    params.category = 'customer-schedule'

                    if $scope.isUseDistributorFilter()
                        params.distributorId = $scope.filter.distributorId

                    if $scope.filter.searchText? && $scope.filter.searchText.length > 0
                        params.q = $scope.filter.searchText

                    if $scope.filter.routeId? and $scope.filter.routeId isnt 'all'
                        params.searchByRoute = true
                        if $scope.filter.routeId isnt 'null'
                            params.routeId = $scope.filter.routeId
                    else
                        params.searchByRoute = false

                    if $scope.filter.day?
                        params.day = $scope.filter.day

                    return params

            $scope.afterLoad = ->
                for record in $scope.records
                    if record.schedule?
                        if record.schedule.items?
                            item.weeksConverted = convertWeeks(item.weeks) for item in record.schedule.items
                        else
                            record.schedule.items = []
                            record.schedule.items.push(createNewScheduleItem())

                    else
                        record.schedule = {}
                        record.schedule.items = []
                        record.schedule.items.push(createNewScheduleItem())

            # SAVE
            $scope.checkBeforeSave = ->
                for record in $scope.records
                    if record.schedule?
                        if record.schedule.routeId?
                            if _.isEmpty(record.schedule.items)
                                toast.logError($filter('translate')('error.data.input.not.valid'))
                                return false

                            for item in record.schedule.items
                                if item?
                                    if not isItemDayValid(item)
                                        toast.logError($filter('translate')('error.data.input.not.valid'))
                                        return false

                                    if $scope.useWeekOfFrequency and _.isEmpty(reconvertWeeks(item.weeksConverted))
                                        toast.logError($filter('translate')('error.data.input.not.valid'))
                                        return false
                                else
                                    toast.logError($filter('translate')('error.data.input.not.valid'))
                                    return false
                return true

            $scope.getObjectToSave = ->
                recordForSaves = []

                for record in $scope.records
                    recordForSave = { id: record.id }

                    if record.schedule?
                        if record.schedule.routeId?
                            recordForSave.schedule = { routeId: record.schedule.routeId }
                            recordForSave.schedule.items = []

                            for item in record.schedule.items
                                itemCopy = angular.copy(item)
                                itemCopy.weeks = reconvertWeeks(item.weeksConverted)
                                recordForSave.schedule.items.push(itemCopy)

                    recordForSaves.push(recordForSave)
                return { list: recordForSaves }

            $scope.getSaveParams = (params) ->
                params.category = 'customer-schedule'
                params.param = 'by-distributor'

                if $scope.isUseDistributorFilter()
                    params.distributorId = $scope.filter.distributorId
                return params

            $scope.onSaveSuccess = -> # DO NOTHING
            $scope.onSaveFailure = -> # DO NOTHING
            $scope.isPost = -> false

            # FILTER
            $scope.distributorFilterChange = ->
                $scope.filter.routeId = null
                $scope.filterChange()

            $scope.filterChange = ->
                $scope.filter.searchText = null
                $scope.searchFilterChange()

            $scope.searchFilterChange = ->
                $scope.filter.currentPage = 1
                $scope.pageChange()

            # INIT
            CtrlInitiatorService.initPagingFilterViewCtrl($scope)
            CtrlInitiatorService.initCanSaveViewCtrl($scope)

            $scope.addInitFunction(->
                $scope.filter.routeId = $scope.filter.routeId ? 'all'

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )

                if not $scope.mustSelectDistributor()
                    LoadingUtilsService.loadRoutesByDistributor(
                        $scope.who
                        $scope.filter.distributorId
                        $scope.loadStatus.getStatusByDataName('routes')
                        (list) ->
                            $scope.routesFilter = _.union(
                                [
                                    { id: 'all', name: '-- ' + $filter('translate')('all') + ' --'  }
                                    { id: 'null', name: '-- ' + $filter('translate')('customer.schedule.route.null') + ' --'  }
                                ]
                                list
                            )
                            $scope.routes = _.union(
                                [
                                    { id: null, name: '-- ' + $filter('translate')('customer.schedule.route.null') + ' --' }
                                ]
                                list
                            )
                    )

                $scope.reloadData()
            )

            $scope.init()

    ])

.controller('CustomerScheduleSingleCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$stateParams', '$state'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
         $filter, toast, $stateParams, $state) ->
            $scope.title = 'customer.schedule.title'
            $scope.defaultBackState = 'customer-pending'

            # INIT HEADER FOR WEEKS
            $scope.useWeekOfFrequency = CtrlUtilsService.getUserInfo().numberWeekOfFrequency > 1
            $scope.numberWeekOfFrequency = if $scope.useWeekOfFrequency then CtrlUtilsService.getUserInfo().numberWeekOfFrequency else 0
            $scope.weeks = []
            if $scope.useWeekOfFrequency
                weekIndex = 1
                createWeekHeader = ->
                    $scope.weeks.push( { header: 'W' + weekIndex, index: weekIndex  } )
                    weekIndex++
                createWeekHeader() until weekIndex > $scope.numberWeekOfFrequency

            createNewScheduleItem = ->
                item =
                    monday: false
                    tuesday: false
                    wednesday: false
                    thursday: false
                    friday: false
                    saturday: false
                    sunday: false

                if $scope.useWeekOfFrequency
                    item.weeksConverted = convertWeeks([])
                    weekConverted.selected = true for weekConverted in item.weeksConverted

                return item

            convertWeeks = (weeks) ->
                weekIndex = 1
                weeksConverted = []
                f = ->
                    weeksConverted.push( { selected: _.include(weeks, weekIndex), index: weekIndex  } )
                    weekIndex++

                f() until weekIndex > $scope.numberWeekOfFrequency

                return weeksConverted

            reconvertWeeks = (weeksConverted) ->
                if $scope.useWeekOfFrequency
                    weeks = []
                    weeks.push(weekConverted.index) for weekConverted in weeksConverted when weekConverted.selected

                    return weeks
                else
                    return null

            isItemDayValid = (item) ->
                item? and (item.monday or item.tuesday or item.wednesday or item.thursday  or item.friday or item.saturday or item.sunday)

            $scope.editRoute = (record) ->
                $scope.changeStatus.markAsChanged()
                if not record.schedule.routeId?
                    record.schedule.items = []
                    record.schedule.items.push(createNewScheduleItem())

            # LOADING
            $scope.reloadData = ->
                params =
                    who: $scope.who
                    category: 'customer-schedule'
                    id: $stateParams.id

                CtrlUtilsService.loadSingleData(params
                    $scope.loadStatus.getStatusByDataName('data')
                    (record) ->
                        $scope.record = record

                        LoadingUtilsService.loadRoutesByDistributor(
                            $scope.who
                            $scope.record.distributor.id
                            $scope.loadStatus.getStatusByDataName('routes')
                            (list) ->
                                $scope.routes = _.union(
                                    [
                                        { id: null, name: '-- ' + $filter('translate')('customer.schedule.route.null') + ' --' }
                                    ]
                                    list
                                )
                        )

                        if $scope.record.schedule?
                            if $scope.record.schedule.items?
                                item.weeksConverted = convertWeeks(item.weeks) for item in $scope.record.schedule.items
                            else
                                $scope.record.schedule.items = []
                                $scope.record.schedule.items.push(createNewScheduleItem())
                        else
                            $scope.record.schedule = {}
                            $scope.record.schedule.items = []
                            $scope.record.schedule.items.push(createNewScheduleItem())
                    (error) ->
                        if (error.status is 400)
                            $state.go('404')
                        else
                            toast.logError($filter('translate')('loading.error'))
                )
            # SAVE
            $scope.checkBeforeSave = ->
                record = $scope.record
                if record.schedule?
                    if record.schedule.routeId?
                        if _.isEmpty(record.schedule.items)
                            toast.logError($filter('translate')('error.data.input.not.valid'))
                            return false

                        for item in record.schedule.items
                            if item?
                                if not isItemDayValid(item)
                                    toast.logError($filter('translate')('error.data.input.not.valid'))
                                    return false

                                if $scope.useWeekOfFrequency and _.isEmpty(reconvertWeeks(item.weeksConverted))
                                    toast.logError($filter('translate')('error.data.input.not.valid'))
                                    return false
                            else
                                toast.logError($filter('translate')('error.data.input.not.valid'))
                                return false
                return true

            $scope.getObjectToSave = ->
                record = $scope.record
                recordForSave = { id: record.id }

                if record.schedule?
                    if record.schedule.routeId?
                        recordForSave.schedule = { routeId: record.schedule.routeId }
                        recordForSave.schedule.items = []

                        for item in record.schedule.items
                            itemCopy = angular.copy(item)
                            itemCopy.weeks = reconvertWeeks(item.weeksConverted)
                            recordForSave.schedule.items.push(itemCopy)

                return recordForSave

            $scope.getSaveParams = (params) ->
                params.category = 'customer-schedule'
                params.id = $stateParams.id
                return params

            $scope.onSaveSuccess = -> # DO NOTHING
            $scope.onSaveFailure = -> # DO NOTHING
            $scope.isPost = -> false

            # INIT
            CtrlInitiatorService.initCanBackViewCtrl($scope)
            CtrlInitiatorService.initCanSaveViewCtrl($scope)

            $scope.addInitFunction(->
                if not $stateParams.id?
                    $state.go('404')
                else
                    $scope.reloadData()
            )

            $scope.init()

    ])

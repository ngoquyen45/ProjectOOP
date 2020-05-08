'use strict'

angular.module('app')

.controller('VisitPhotoCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService',
        '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$modal'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService,
        $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $modal) ->
            $scope.title = 'visit.photo.title'

            $scope.isUseDistributorFilter = -> CtrlUtilsService.getWho() isnt 'distributor'
            $scope.mustSelectDistributor  = -> $scope.isUseDistributorFilter() and not $scope.filter.distributorId?
            $scope.mustSelectSalesman     = -> not $scope.filter.salesmanId?
            $scope.afterFilterLoad        = ->

            # FILTER
            $scope.changeDistributor = ->
                $scope.filter.salesmanId = null
                LoadingUtilsService.loadSalesmenByDistributor(
                    $scope.who
                    $scope.filter.distributorId
                    $scope.loadStatus.getStatusByDataName('salesmen')
                    (list) ->
                        $scope.salesmen = list
                )

            $scope.search = ->
                if checkDate()
                    if checkDateDuration()
                        if not $scope.mustSelectDistributor()
                            if not $scope.mustSelectSalesman()
                                $scope.filter.fromDate = globalUtils.createIsoDate($scope.fromDate.date)
                                $scope.filter.toDate = globalUtils.createIsoDate($scope.toDate.date)
                                $scope.reloadForNewFilter()
                            else
                                toast.logError($filter('translate')('please.select.salesman'))
                        else
                            toast.logError($filter('translate')('please.select.distributor'))
                    else
                        toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.1.month'))
                else
                    toast.logError($filter('translate')('from.date.cannot.be.greater.than.to.date'))

            checkDateDuration = ->
                if moment($scope.fromDate.date).add(1, 'months').toDate().getTime() < $scope.toDate.date.getTime()
                    return false
                return true

            checkDate = ->
                if $scope.fromDate? and  $scope.fromDate.date? and $scope.toDate and $scope.toDate.date?
                    if $scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()
                        return true
                return false

            $scope.isEmpty = -> not ($scope.dates? && $scope.dates.length > 0)

            # LOADING
            $scope.reloadData = ->
                if not ($scope.mustSelectDistributor() || $scope.mustSelectSalesman())
                    params =
                        who: $scope.who
                        category: 'visit-photo'
                        salesmanId: $scope.filter.salesmanId
                        fromDate: globalUtils.createIsoDate($scope.fromDate.date)
                        toDate: globalUtils.createIsoDate($scope.toDate.date)

                    $scope.map = {}
                    $scope.dates = []

                    CtrlUtilsService.loadListData(params
                        $scope.loadStatus.getStatusByDataName('data')
                        (records) ->
                            for record in records
                                date = $filter('isoDate')(record.createdTime)
                                visitPhotos = $scope.map[date]
                                if not visitPhotos?
                                    visitPhotos = []
                                    $scope.dates.push(date)

                                visitPhotos.push(record)
                                $scope.map[date] = visitPhotos
                        (error) ->
                            if (error.status is 400)
                                $state.go('404')
                            else
                                toast.logError($filter('translate')('loading.error'))
                    )

            # FOR THIS PAGE ONLY
            $scope.getPhoto = (visitPhoto) ->
                if visitPhoto? and visitPhoto.photo?
                    return ADDRESS_BACKEND + 'image/' + visitPhoto.photo

            $scope.zoomPhoto = (visitPhoto) ->
                $modal.open(
                    templateUrl: 'views/business/visit-photo/photo-popup.html',
                    controller: 'PhotoPopupCtrl',
                    resolve:
                        title: -> visitPhoto.customer.name
                        date: -> $filter('isoDate')(visitPhoto.createdTime)
                        photoLink: -> $scope.getPhoto(visitPhoto)
                    backdrop: true
                )

            $scope.getVisitLink = (visitPhoto) ->
                return '#/visit-detail?' +
                    'id=' + visitPhoto.id + '&' +
                    'parent=visit-photo&' +
                    'filter=' + $scope.getFilterAsString()

            # INIT
            CtrlInitiatorService.initFilterViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.addInitFunction( ->
                $scope.map = {}
                $scope.dates = []

                if $scope.filter.fromDate? and $scope.filter.toDate?
                    $scope.fromDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.fromDate))
                    $scope.toDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.toDate))
                    if not checkDate() or not checkDateDuration()
                        $state.go('404')
                else
                    $scope.fromDate = $scope.createDatePickerModel(new Date())
                    $scope.toDate = $scope.createDatePickerModel(new Date())

                if $scope.isUseDistributorFilter()
                    LoadingUtilsService.loadDistributors(
                        $scope.who
                        $scope.loadStatus.getStatusByDataName('distributors')
                        (list) -> $scope.distributors = list
                    )

                if not $scope.mustSelectDistributor()
                    LoadingUtilsService.loadSalesmenByDistributor(
                        $scope.who
                        $scope.filter.distributorId
                        $scope.loadStatus.getStatusByDataName('salesmen')
                        (list) ->
                            $scope.salesmen = list
                    )

                $scope.reloadData()
            )

            $scope.init()

])

.controller('PhotoPopupCtrl', [
        '$scope', '$modalInstance', 'title', 'date', 'photoLink'
        ($scope, $modalInstance, title, date, photoLink) ->
            $scope.title = title
            $scope.date = date
            $scope.photoLink = photoLink

            $scope.cancel = -> $modalInstance.dismiss('cancel')
    ])

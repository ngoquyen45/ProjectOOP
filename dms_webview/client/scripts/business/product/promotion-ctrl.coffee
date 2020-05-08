'use strict'

angular.module('app')

.controller('PromotionCtrl', [
        '$scope', 'CtrlCategoryInitiatorService', '$filter'
        ($scope, CtrlCategoryInitiatorService, $filter) ->
            $scope.title = 'promotion.title'
            $scope.categoryName = 'promotion'
            $scope.usePopup = false
            $scope.isUseDistributorFilter = -> false

            $scope.useActivateButton = false

            $scope.columns = [
                { header: 'name', property: 'name' }
                { header: 'start.date', property: (record) -> $filter('isoDate')(record.startDate) }
                { header: 'end.date', property: (record) -> $filter('isoDate')(record.endDate) }
            ]

            CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope)

            $scope.init()
    ])

.controller('PromotionDetailCtrl', [
        '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$filter', 'LoadingUtilsService', 'ADDRESS_BACKEND', 'toast'
        ($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $filter, LoadingUtilsService, ADDRESS_BACKEND, toast) ->
            $scope.title = 'promotion.title'
            $scope.categoryName  = 'promotion'
            $scope.isIdRequire = -> true
            $scope.defaultBackState = 'promotion'

            # AFTER LOAD
            $scope.onLoadSuccess = ->
                $scope.startDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.startDate))
                $scope.endDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.endDate))

                $scope.promotionTypes = [
                    type: 0,
                    name: $filter('translate')('promotion.type.c.product.qty.r.percentage.amt')
                ,
                    type: 1,
                    name: $filter('translate')('promotion.type.c.product.qty.r.product.qty')
                ]
                if $scope.record.details?
                    for detail in $scope.record.details
                        if detail? and detail.type < $scope.promotionTypes.length
                            promotionType = $scope.promotionTypes[detail.type]
                            promotionType.details = [] if not promotionType.details?
                            promotionType.details.push(detail)

            # CHECK BEFORE SAVE
            $scope.checkBeforeSave = ->
                hasAtLeastOne = false
                for promotionType in $scope.promotionTypes
                    if $scope.isPromotionTypeApplied(promotionType)
                        hasAtLeastOne = true

                if $scope.startDate.date.getTime() > $scope.endDate.date.getTime()
                    toast.logError($filter('translate')('error.start.date.after.end.date'))
                    return false
                else if not hasAtLeastOne
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                    return false
                else
                    return true

            # GET OBJECT TO SAVE
            $scope.getObjectToSave = ->
                record = angular.copy($scope.record)

                record.startDate = globalUtils.createIsoDate($scope.startDate.date)
                record.endDate = globalUtils.createIsoDate($scope.endDate.date)

                details = []
                for promotionType in $scope.promotionTypes
                    if $scope.isPromotionTypeApplied(promotionType)
                        for detail in promotionType.details
                            details.push({
                                type: detail.type
                                condition: {
                                    productId: detail.condition.product.id
                                    quantity: detail.condition.quantity
                                }
                                reward: {
                                    percentage: detail.reward.percentage
                                    quantity: detail.reward.quantity
                                    productId: if detail.reward.product? then detail.reward.product.id else null
                                    productText: detail.reward.productText
                                }
                            })
                record.details = details

                return record

            # PROMOTION TYPE TABLE
            $scope.hasPromotionTypeApplied = ->
                if $scope.promotionTypes?
                    for promotionType in $scope.promotionTypes
                        return true if $scope.isPromotionTypeApplied(promotionType)
                return false

            $scope.isPromotionTypeApplied = (promotionType) ->
                promotionType? and promotionType.details? and promotionType.details.length > 0

            $scope.sortPromotionType = (promotionType) ->
                if $scope.isPromotionTypeApplied(promotionType) then 0 else 1

            $scope.getPromotionDetailDisplay = (type, detail) ->
                if type is 0
                    display =   '<strong>' +
                                    globalUtils.escapeHTML(detail.condition.product.name) +
                                '</strong>' +
                                ' (' + detail.condition.product.code + ')'

                    display = display +
                            ' - (' + $filter('translate')('promotion.discount') +
                            ' <strong>' + detail.reward.percentage + '%</strong>)'

                    if detail.condition.quantity > 1
                        display = display +
                                ' - (' + $filter('translate')('promotion.buy.from.quantity') +
                                ' ' + detail.condition.quantity + ' ' +
                                detail.condition.product.uom.name + ')'

                    return display
                else if type is 1
                    display = $filter('translate')('promotion.buy')
                    display = display + ' ' + detail.condition.quantity + ' x'
                    display = display + ' <strong>' +
                                            globalUtils.escapeHTML(detail.condition.product.name) +
                                        '</strong>' +
                                        ' (' + detail.condition.product.code + ')'

                    display = display + ' - ' + $filter('translate')('promotion.get')
                    display = display + ' ' + detail.reward.quantity + ' x'
                    if detail.reward.product?
                        display = display + ' <strong>' +
                                                globalUtils.escapeHTML(detail.reward.product.name) +
                                            '</strong>' +
                                            ' (' + detail.reward.product.code + ')'
                    else if detail.reward.productText?
                        display = display + ' <strong>' +
                                                globalUtils.escapeHTML(detail.reward.productText) +
                                            '</strong>'

                    return display

            $scope.clearPromotionType = (promotionType) ->
                $scope.changeStatus.markAsChanged()
                promotionType.details = null

            $scope.isEditPromotionTypeMode = -> $scope.currentPromotionType?

            $scope.editPromotionType = (promotionType) ->
                $scope.changeStatus.markAsChanged()
                $scope.currentPromotionType = promotionType
                $scope.currentPromtionTypeDetails = []

                detailMap = {}

                if $scope.currentPromotionType? and $scope.currentPromotionType.details? and $scope.currentPromotionType.details.length > 0
                    detailMap[detail.condition.product.id] = detail for detail in $scope.currentPromotionType.details when detail? and detail.condition?

                if $scope.products? and $scope.productMap?
                    for product in $scope.products
                        detail = detailMap[product.id]
                        if detail?
                            $scope.currentPromtionTypeDetails.push(
                                type: $scope.currentPromotionType.type
                                condition: { product: product, quantity: angular.copy(detail.condition.quantity) }
                                reward: {
                                    percentage: angular.copy(detail.reward.percentage)
                                    quantity: angular.copy(detail.reward.quantity)
                                    productId: if detail.reward.product? then angular.copy(detail.reward.product.id) else null
                                    productText: angular.copy(detail.reward.productText)
                                }
                                textMode: detail.reward.productText?
                            )
                        else
                            $scope.currentPromtionTypeDetails.push(
                                type: $scope.currentPromotionType.type
                                condition: { product: product, quantity: null }
                                reward: {}
                                textMode: false
                            )

            # DETAIL MODE
            $scope.getPhotoLink = (photoId) -> ADDRESS_BACKEND + 'image/' + photoId
            $scope.isDisplayRewardProduct = -> $scope.currentPromotionType? and  _.includes([1], $scope.currentPromotionType.type)
            $scope.isDisplayRewardQuantity = -> $scope.currentPromotionType? and  _.includes([1], $scope.currentPromotionType.type)
            $scope.isDisplayRewardPercentage = -> $scope.currentPromotionType? and  _.includes([0], $scope.currentPromotionType.type)
            $scope.isDetailUsed = (detail) ->  detail.condition.quantity > 0
            $scope.sortDetail = (detail) -> if $scope.isDetailUsed(detail) then 0 else 1
            $scope.changeProductMode = (detail) -> detail.textMode = not detail.textMode
            $scope.getNumberRewardColumn = ->
                numberColumn = 0
                numberColumn++ if $scope.isDisplayRewardProduct()
                numberColumn++ if $scope.isDisplayRewardQuantity()
                numberColumn++ if $scope.isDisplayRewardPercentage()
                return numberColumn

            $scope.applyPromotionTypeEdit = ->
                if not $scope.isFormValid('form2')
                    toast.logError($filter('translate')('error.data.input.not.valid'))
                else
                    $scope.currentPromotionType.details = []
                    for detail in $scope.currentPromtionTypeDetails
                        if $scope.isDetailUsed(detail)
                            temp = {}
                            temp.type = $scope.currentPromotionType.type
                            temp.condition = detail.condition
                            temp.reward = {
                                percentage: detail.reward.percentage
                                quantity: detail.reward.quantity
                            }

                            if detail.textMode
                                temp.reward.productText = detail.reward.productText
                            else
                                temp.reward.product = $scope.productMap[detail.reward.productId]

                            $scope.currentPromotionType.details.push(temp)

                    $scope.cancelPromotionTypeEdit()

            $scope.cancelPromotionTypeEdit = ->
                $scope.currentPromotionType = null
                $scope.currentPromtionTypeDetails = null

            # INIT
            CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope)
            CtrlInitiatorService.initUseDatePickerCtrl($scope)

            $scope.addInitFunction( ->
                $scope.startDate = null
                $scope.endDate = null
                $scope.promotionTypes = null
                $scope.currentPromotionType = null
                $scope.currentPromtionTypeDetails = null
                $scope.products = null
                $scope.productMap = null

                LoadingUtilsService.loadProductsByDistributor(
                    $scope.who
                    $scope.loadStatus.getStatusByDataName('product')
                    (list) ->
                        $scope.products = list
                        $scope.productMap = {}
                        $scope.productMap[product.id] = product for product in $scope.products
                )
            )

            $scope.init()
    ])

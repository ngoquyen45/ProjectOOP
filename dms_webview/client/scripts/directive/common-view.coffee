'use strict'

angular.module('app.directives')

.directive('basicView', [
        () ->
            restrict: 'E'
            transclude: true
            templateUrl: 'views/directive/basic-view.html'
    ])

.directive('categoryDetail', [
        () ->
            restrict: 'E'
            transclude: true
            templateUrl: 'views/directive/category-detail.html'
    ])

.controller('OrderController', [
        '$scope', '$filter'
        ($scope, $filter) ->
            $scope.getDeliveryDisplay = ->
                if $scope.data?
                    if $scope.data.vanSales
                        $filter('translate')('van.sales')
                    else
                        if $scope.data.deliveryType?
                            if $scope.data.deliveryType is 0
                                $filter('translate')('order.delivery.immediate')
                            else if $scope.data.deliveryType is 1
                                $filter('translate')('order.delivery.same.day')
                            else if $scope.data.deliveryType is 2 and $scope.data.deliveryTime?
                                $filter('isoDate')($scope.data.deliveryTime)

            $scope.hasPromotion = -> $scope.data? and $scope.data.promotionResults? and $scope.data.promotionResults.length > 0
            $scope.promotionProductRewards = null
            $scope.getPromotionProductRewards = ->
                if $scope.hasPromotion()
                    if $scope.promotionProductRewards?
                        return $scope.promotionProductRewards
                    else
                        promotionProductRewardsMap = {}

                        for promotionResult in $scope.data.promotionResults
                            if promotionResult? and promotionResult.details? and promotionResult.details.length > 0
                                for detail in promotionResult.details
                                    if detail? and detail.reward? and detail.reward.product? and detail.reward.quantity?
                                        promotionProductReward = promotionProductRewardsMap[_.camelCase(detail.reward.product.name)]

                                        if promotionProductReward?
                                            promotionProductReward.quantity = promotionProductReward.quantity + detail.reward.quantity
                                        else
                                            promotionProductRewardsMap[_.camelCase(detail.reward.product.name)] = angular.copy(detail.reward)

                        $scope.promotionProductRewards = []
                        for key of promotionProductRewardsMap
                            $scope.promotionProductRewards.push(promotionProductRewardsMap[key])

                        return $scope.promotionProductRewards
    ])

.directive('order', [
        ->
            restrict: 'E'
            scope:
                data: '='
            templateUrl: 'views/directive/order.html'
            controller: 'OrderController'
    ])



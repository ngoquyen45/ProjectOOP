(function() {
  'use strict';
  angular.module('app.directives').directive('basicView', [
    function() {
      return {
        restrict: 'E',
        transclude: true,
        templateUrl: 'views/directive/basic-view.html'
      };
    }
  ]).directive('categoryDetail', [
    function() {
      return {
        restrict: 'E',
        transclude: true,
        templateUrl: 'views/directive/category-detail.html'
      };
    }
  ]).controller('OrderController', [
    '$scope', '$filter', function($scope, $filter) {
      $scope.getDeliveryDisplay = function() {
        if ($scope.data != null) {
          if ($scope.data.vanSales) {
            return $filter('translate')('van.sales');
          } else {
            if ($scope.data.deliveryType != null) {
              if ($scope.data.deliveryType === 0) {
                return $filter('translate')('order.delivery.immediate');
              } else if ($scope.data.deliveryType === 1) {
                return $filter('translate')('order.delivery.same.day');
              } else if ($scope.data.deliveryType === 2 && ($scope.data.deliveryTime != null)) {
                return $filter('isoDate')($scope.data.deliveryTime);
              }
            }
          }
        }
      };
      $scope.hasPromotion = function() {
        return ($scope.data != null) && ($scope.data.promotionResults != null) && $scope.data.promotionResults.length > 0;
      };
      $scope.promotionProductRewards = null;
      return $scope.getPromotionProductRewards = function() {
        var detail, key, promotionProductReward, promotionProductRewardsMap, promotionResult, _i, _j, _len, _len1, _ref, _ref1;
        if ($scope.hasPromotion()) {
          if ($scope.promotionProductRewards != null) {
            return $scope.promotionProductRewards;
          } else {
            promotionProductRewardsMap = {};
            _ref = $scope.data.promotionResults;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
              promotionResult = _ref[_i];
              if ((promotionResult != null) && (promotionResult.details != null) && promotionResult.details.length > 0) {
                _ref1 = promotionResult.details;
                for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
                  detail = _ref1[_j];
                  if ((detail != null) && (detail.reward != null) && (detail.reward.product != null) && (detail.reward.quantity != null)) {
                    promotionProductReward = promotionProductRewardsMap[_.camelCase(detail.reward.product.name)];
                    if (promotionProductReward != null) {
                      promotionProductReward.quantity = promotionProductReward.quantity + detail.reward.quantity;
                    } else {
                      promotionProductRewardsMap[_.camelCase(detail.reward.product.name)] = angular.copy(detail.reward);
                    }
                  }
                }
              }
            }
            $scope.promotionProductRewards = [];
            for (key in promotionProductRewardsMap) {
              $scope.promotionProductRewards.push(promotionProductRewardsMap[key]);
            }
            return $scope.promotionProductRewards;
          }
        }
      };
    }
  ]).directive('order', [
    function() {
      return {
        restrict: 'E',
        scope: {
          data: '='
        },
        templateUrl: 'views/directive/order.html',
        controller: 'OrderController'
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=common-view.js.map

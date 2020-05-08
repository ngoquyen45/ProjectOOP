(function() {
  'use strict';
  angular.module('app').controller('PromotionCtrl', [
    '$scope', 'CtrlCategoryInitiatorService', '$filter', function($scope, CtrlCategoryInitiatorService, $filter) {
      $scope.title = 'promotion.title';
      $scope.categoryName = 'promotion';
      $scope.usePopup = false;
      $scope.isUseDistributorFilter = function() {
        return false;
      };
      $scope.useActivateButton = false;
      $scope.columns = [
        {
          header: 'name',
          property: 'name'
        }, {
          header: 'start.date',
          property: function(record) {
            return $filter('isoDate')(record.startDate);
          }
        }, {
          header: 'end.date',
          property: function(record) {
            return $filter('isoDate')(record.endDate);
          }
        }
      ];
      CtrlCategoryInitiatorService.initCategoryListViewCtrl($scope);
      return $scope.init();
    }
  ]).controller('PromotionDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', '$filter', 'LoadingUtilsService', 'ADDRESS_BACKEND', 'toast', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, $filter, LoadingUtilsService, ADDRESS_BACKEND, toast) {
      $scope.title = 'promotion.title';
      $scope.categoryName = 'promotion';
      $scope.isIdRequire = function() {
        return true;
      };
      $scope.defaultBackState = 'promotion';
      $scope.onLoadSuccess = function() {
        var detail, promotionType, _i, _len, _ref, _results;
        $scope.startDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.startDate));
        $scope.endDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.record.endDate));
        $scope.promotionTypes = [
          {
            type: 0,
            name: $filter('translate')('promotion.type.c.product.qty.r.percentage.amt')
          }, {
            type: 1,
            name: $filter('translate')('promotion.type.c.product.qty.r.product.qty')
          }
        ];
        if ($scope.record.details != null) {
          _ref = $scope.record.details;
          _results = [];
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            detail = _ref[_i];
            if ((detail != null) && detail.type < $scope.promotionTypes.length) {
              promotionType = $scope.promotionTypes[detail.type];
              if (promotionType.details == null) {
                promotionType.details = [];
              }
              _results.push(promotionType.details.push(detail));
            } else {
              _results.push(void 0);
            }
          }
          return _results;
        }
      };
      $scope.checkBeforeSave = function() {
        var hasAtLeastOne, promotionType, _i, _len, _ref;
        hasAtLeastOne = false;
        _ref = $scope.promotionTypes;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          promotionType = _ref[_i];
          if ($scope.isPromotionTypeApplied(promotionType)) {
            hasAtLeastOne = true;
          }
        }
        if ($scope.startDate.date.getTime() > $scope.endDate.date.getTime()) {
          toast.logError($filter('translate')('error.start.date.after.end.date'));
          return false;
        } else if (!hasAtLeastOne) {
          toast.logError($filter('translate')('error.data.input.not.valid'));
          return false;
        } else {
          return true;
        }
      };
      $scope.getObjectToSave = function() {
        var detail, details, promotionType, record, _i, _j, _len, _len1, _ref, _ref1;
        record = angular.copy($scope.record);
        record.startDate = globalUtils.createIsoDate($scope.startDate.date);
        record.endDate = globalUtils.createIsoDate($scope.endDate.date);
        details = [];
        _ref = $scope.promotionTypes;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
          promotionType = _ref[_i];
          if ($scope.isPromotionTypeApplied(promotionType)) {
            _ref1 = promotionType.details;
            for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
              detail = _ref1[_j];
              details.push({
                type: detail.type,
                condition: {
                  productId: detail.condition.product.id,
                  quantity: detail.condition.quantity
                },
                reward: {
                  percentage: detail.reward.percentage,
                  quantity: detail.reward.quantity,
                  productId: detail.reward.product != null ? detail.reward.product.id : null,
                  productText: detail.reward.productText
                }
              });
            }
          }
        }
        record.details = details;
        return record;
      };
      $scope.hasPromotionTypeApplied = function() {
        var promotionType, _i, _len, _ref;
        if ($scope.promotionTypes != null) {
          _ref = $scope.promotionTypes;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            promotionType = _ref[_i];
            if ($scope.isPromotionTypeApplied(promotionType)) {
              return true;
            }
          }
        }
        return false;
      };
      $scope.isPromotionTypeApplied = function(promotionType) {
        return (promotionType != null) && (promotionType.details != null) && promotionType.details.length > 0;
      };
      $scope.sortPromotionType = function(promotionType) {
        if ($scope.isPromotionTypeApplied(promotionType)) {
          return 0;
        } else {
          return 1;
        }
      };
      $scope.getPromotionDetailDisplay = function(type, detail) {
        var display;
        if (type === 0) {
          display = '<strong>' + globalUtils.escapeHTML(detail.condition.product.name) + '</strong>' + ' (' + detail.condition.product.code + ')';
          display = display + ' - (' + $filter('translate')('promotion.discount') + ' <strong>' + detail.reward.percentage + '%</strong>)';
          if (detail.condition.quantity > 1) {
            display = display + ' - (' + $filter('translate')('promotion.buy.from.quantity') + ' ' + detail.condition.quantity + ' ' + detail.condition.product.uom.name + ')';
          }
          return display;
        } else if (type === 1) {
          display = $filter('translate')('promotion.buy');
          display = display + ' ' + detail.condition.quantity + ' x';
          display = display + ' <strong>' + globalUtils.escapeHTML(detail.condition.product.name) + '</strong>' + ' (' + detail.condition.product.code + ')';
          display = display + ' - ' + $filter('translate')('promotion.get');
          display = display + ' ' + detail.reward.quantity + ' x';
          if (detail.reward.product != null) {
            display = display + ' <strong>' + globalUtils.escapeHTML(detail.reward.product.name) + '</strong>' + ' (' + detail.reward.product.code + ')';
          } else if (detail.reward.productText != null) {
            display = display + ' <strong>' + globalUtils.escapeHTML(detail.reward.productText) + '</strong>';
          }
          return display;
        }
      };
      $scope.clearPromotionType = function(promotionType) {
        $scope.changeStatus.markAsChanged();
        return promotionType.details = null;
      };
      $scope.isEditPromotionTypeMode = function() {
        return $scope.currentPromotionType != null;
      };
      $scope.editPromotionType = function(promotionType) {
        var detail, detailMap, product, _i, _j, _len, _len1, _ref, _ref1, _results;
        $scope.changeStatus.markAsChanged();
        $scope.currentPromotionType = promotionType;
        $scope.currentPromtionTypeDetails = [];
        detailMap = {};
        if (($scope.currentPromotionType != null) && ($scope.currentPromotionType.details != null) && $scope.currentPromotionType.details.length > 0) {
          _ref = $scope.currentPromotionType.details;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            detail = _ref[_i];
            if ((detail != null) && (detail.condition != null)) {
              detailMap[detail.condition.product.id] = detail;
            }
          }
        }
        if (($scope.products != null) && ($scope.productMap != null)) {
          _ref1 = $scope.products;
          _results = [];
          for (_j = 0, _len1 = _ref1.length; _j < _len1; _j++) {
            product = _ref1[_j];
            detail = detailMap[product.id];
            if (detail != null) {
              _results.push($scope.currentPromtionTypeDetails.push({
                type: $scope.currentPromotionType.type,
                condition: {
                  product: product,
                  quantity: angular.copy(detail.condition.quantity)
                },
                reward: {
                  percentage: angular.copy(detail.reward.percentage),
                  quantity: angular.copy(detail.reward.quantity),
                  productId: detail.reward.product != null ? angular.copy(detail.reward.product.id) : null,
                  productText: angular.copy(detail.reward.productText)
                },
                textMode: detail.reward.productText != null
              }));
            } else {
              _results.push($scope.currentPromtionTypeDetails.push({
                type: $scope.currentPromotionType.type,
                condition: {
                  product: product,
                  quantity: null
                },
                reward: {},
                textMode: false
              }));
            }
          }
          return _results;
        }
      };
      $scope.getPhotoLink = function(photoId) {
        return ADDRESS_BACKEND + 'image/' + photoId;
      };
      $scope.isDisplayRewardProduct = function() {
        return ($scope.currentPromotionType != null) && _.includes([1], $scope.currentPromotionType.type);
      };
      $scope.isDisplayRewardQuantity = function() {
        return ($scope.currentPromotionType != null) && _.includes([1], $scope.currentPromotionType.type);
      };
      $scope.isDisplayRewardPercentage = function() {
        return ($scope.currentPromotionType != null) && _.includes([0], $scope.currentPromotionType.type);
      };
      $scope.isDetailUsed = function(detail) {
        return detail.condition.quantity > 0;
      };
      $scope.sortDetail = function(detail) {
        if ($scope.isDetailUsed(detail)) {
          return 0;
        } else {
          return 1;
        }
      };
      $scope.changeProductMode = function(detail) {
        return detail.textMode = !detail.textMode;
      };
      $scope.getNumberRewardColumn = function() {
        var numberColumn;
        numberColumn = 0;
        if ($scope.isDisplayRewardProduct()) {
          numberColumn++;
        }
        if ($scope.isDisplayRewardQuantity()) {
          numberColumn++;
        }
        if ($scope.isDisplayRewardPercentage()) {
          numberColumn++;
        }
        return numberColumn;
      };
      $scope.applyPromotionTypeEdit = function() {
        var detail, temp, _i, _len, _ref;
        if (!$scope.isFormValid('form2')) {
          return toast.logError($filter('translate')('error.data.input.not.valid'));
        } else {
          $scope.currentPromotionType.details = [];
          _ref = $scope.currentPromtionTypeDetails;
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            detail = _ref[_i];
            if ($scope.isDetailUsed(detail)) {
              temp = {};
              temp.type = $scope.currentPromotionType.type;
              temp.condition = detail.condition;
              temp.reward = {
                percentage: detail.reward.percentage,
                quantity: detail.reward.quantity
              };
              if (detail.textMode) {
                temp.reward.productText = detail.reward.productText;
              } else {
                temp.reward.product = $scope.productMap[detail.reward.productId];
              }
              $scope.currentPromotionType.details.push(temp);
            }
          }
          return $scope.cancelPromotionTypeEdit();
        }
      };
      $scope.cancelPromotionTypeEdit = function() {
        $scope.currentPromotionType = null;
        return $scope.currentPromtionTypeDetails = null;
      };
      CtrlCategoryInitiatorService.initCategoryDetailViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        $scope.startDate = null;
        $scope.endDate = null;
        $scope.promotionTypes = null;
        $scope.currentPromotionType = null;
        $scope.currentPromtionTypeDetails = null;
        $scope.products = null;
        $scope.productMap = null;
        return LoadingUtilsService.loadProductsByDistributor($scope.who, $scope.loadStatus.getStatusByDataName('product'), function(list) {
          var product, _i, _len, _ref, _results;
          $scope.products = list;
          $scope.productMap = {};
          _ref = $scope.products;
          _results = [];
          for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            product = _ref[_i];
            _results.push($scope.productMap[product.id] = product);
          }
          return _results;
        });
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=promotion-ctrl.js.map

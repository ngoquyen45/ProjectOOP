(function() {
  'use strict';
  angular.module('app').controller('VisitPhotoCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', 'ADDRESS_BACKEND', '$modal', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams, ADDRESS_BACKEND, $modal) {
      var checkDate, checkDateDuration;
      $scope.title = 'visit.photo.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.mustSelectDistributor = function() {
        return $scope.isUseDistributorFilter() && ($scope.filter.distributorId == null);
      };
      $scope.mustSelectSalesman = function() {
        return $scope.filter.salesmanId == null;
      };
      $scope.afterFilterLoad = function() {};
      $scope.changeDistributor = function() {
        $scope.filter.salesmanId = null;
        return LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
          return $scope.salesmen = list;
        });
      };
      $scope.search = function() {
        if (checkDate()) {
          if (checkDateDuration()) {
            if (!$scope.mustSelectDistributor()) {
              if (!$scope.mustSelectSalesman()) {
                $scope.filter.fromDate = globalUtils.createIsoDate($scope.fromDate.date);
                $scope.filter.toDate = globalUtils.createIsoDate($scope.toDate.date);
                return $scope.reloadForNewFilter();
              } else {
                return toast.logError($filter('translate')('please.select.salesman'));
              }
            } else {
              return toast.logError($filter('translate')('please.select.distributor'));
            }
          } else {
            return toast.logError($filter('translate')('max.duration.between.from.date.and.to.date.is.1.month'));
          }
        } else {
          return toast.logError($filter('translate')('from.date.cannot.be.greater.than.to.date'));
        }
      };
      checkDateDuration = function() {
        if (moment($scope.fromDate.date).add(1, 'months').toDate().getTime() < $scope.toDate.date.getTime()) {
          return false;
        }
        return true;
      };
      checkDate = function() {
        if (($scope.fromDate != null) && ($scope.fromDate.date != null) && $scope.toDate && ($scope.toDate.date != null)) {
          if ($scope.fromDate.date.getTime() <= $scope.toDate.date.getTime()) {
            return true;
          }
        }
        return false;
      };
      $scope.isEmpty = function() {
        return !(($scope.dates != null) && $scope.dates.length > 0);
      };
      $scope.reloadData = function() {
        var params;
        if (!($scope.mustSelectDistributor() || $scope.mustSelectSalesman())) {
          params = {
            who: $scope.who,
            category: 'visit-photo',
            salesmanId: $scope.filter.salesmanId,
            fromDate: globalUtils.createIsoDate($scope.fromDate.date),
            toDate: globalUtils.createIsoDate($scope.toDate.date)
          };
          $scope.map = {};
          $scope.dates = [];
          return CtrlUtilsService.loadListData(params, $scope.loadStatus.getStatusByDataName('data'), function(records) {
            var date, record, visitPhotos, _i, _len, _results;
            _results = [];
            for (_i = 0, _len = records.length; _i < _len; _i++) {
              record = records[_i];
              date = $filter('isoDate')(record.createdTime);
              visitPhotos = $scope.map[date];
              if (visitPhotos == null) {
                visitPhotos = [];
                $scope.dates.push(date);
              }
              visitPhotos.push(record);
              _results.push($scope.map[date] = visitPhotos);
            }
            return _results;
          }, function(error) {
            if (error.status === 400) {
              return $state.go('404');
            } else {
              return toast.logError($filter('translate')('loading.error'));
            }
          });
        }
      };
      $scope.getPhoto = function(visitPhoto) {
        if ((visitPhoto != null) && (visitPhoto.photo != null)) {
          return ADDRESS_BACKEND + 'image/' + visitPhoto.photo;
        }
      };
      $scope.zoomPhoto = function(visitPhoto) {
        return $modal.open({
          templateUrl: 'views/business/visit-photo/photo-popup.html',
          controller: 'PhotoPopupCtrl',
          resolve: {
            title: function() {
              return visitPhoto.customer.name;
            },
            date: function() {
              return $filter('isoDate')(visitPhoto.createdTime);
            },
            photoLink: function() {
              return $scope.getPhoto(visitPhoto);
            }
          },
          backdrop: true
        });
      };
      $scope.getVisitLink = function(visitPhoto) {
        return '#/visit-detail?' + 'id=' + visitPhoto.id + '&' + 'parent=visit-photo&' + 'filter=' + $scope.getFilterAsString();
      };
      CtrlInitiatorService.initFilterViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        $scope.map = {};
        $scope.dates = [];
        if (($scope.filter.fromDate != null) && ($scope.filter.toDate != null)) {
          $scope.fromDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.fromDate));
          $scope.toDate = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.toDate));
          if (!checkDate() || !checkDateDuration()) {
            $state.go('404');
          }
        } else {
          $scope.fromDate = $scope.createDatePickerModel(new Date());
          $scope.toDate = $scope.createDatePickerModel(new Date());
        }
        if ($scope.isUseDistributorFilter()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = list;
          });
        }
        if (!$scope.mustSelectDistributor()) {
          LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
            return $scope.salesmen = list;
          });
        }
        return $scope.reloadData();
      });
      return $scope.init();
    }
  ]).controller('PhotoPopupCtrl', [
    '$scope', '$modalInstance', 'title', 'date', 'photoLink', function($scope, $modalInstance, title, date, photoLink) {
      $scope.title = title;
      $scope.date = date;
      $scope.photoLink = photoLink;
      return $scope.cancel = function() {
        return $modalInstance.dismiss('cancel');
      };
    }
  ]);

}).call(this);

//# sourceMappingURL=visit-photo-ctrl.js.map

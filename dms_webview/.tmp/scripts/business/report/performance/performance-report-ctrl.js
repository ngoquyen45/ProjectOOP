(function() {
  'use strict';
  var chartOptions;

  chartOptions = {
    colors: ["#26A69A", "#607D8B", "#26C6DA"],
    tooltip: {
      show: true
    },
    tooltipOpts: {
      defaultTheme: false
    },
    grid: {
      hoverable: true,
      clickable: true,
      tickColor: "#f9f9f9",
      borderWidth: 1,
      borderColor: "#eeeeee"
    },
    yaxes: [
      {}, {
        position: "right"
      }
    ]
  };

  angular.module('app').controller('PerformanceReportCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state) {
      $scope.title = 'performance.report.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.afterFilterLoad = function() {};
      $scope.goToReport = function() {
        $scope.filter.date = globalUtils.createIsoDate($scope.date.date);
        if ($scope.isUseDistributorFilter() && ($scope.filter.distributorId == null)) {
          return toast.logError($filter('translate')('error.data.input.not.valid'));
        } else if ($scope.filter.salesmanId == null) {
          return toast.logError($filter('translate')('error.data.input.not.valid'));
        } else {
          return $state.go('performance-report-salesman', {
            filter: $scope.getFilterAsString()
          });
        }
      };
      $scope.isDisplaySalesman = function() {
        return !$scope.isUseDistributorFilter() || ($scope.filter.distributorId != null);
      };
      $scope.changeDistributor = function() {
        $scope.filter.salesmanId = null;
        return $scope.loadSalesmen();
      };
      $scope.loadSalesmen = function() {
        return LoadingUtilsService.loadSalesmenByDistributor($scope.who, $scope.filter.distributorId, $scope.loadStatus.getStatusByDataName('salesmen'), function(list) {
          return $scope.salesmen = list;
        });
      };
      CtrlInitiatorService.initFilterViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        if ($scope.filter.date != null) {
          $scope.date = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.date));
        } else {
          $scope.date = $scope.createDatePickerModel(new Date());
        }
        if ($scope.isUseDistributorFilter()) {
          LoadingUtilsService.loadDistributors($scope.who, $scope.loadStatus.getStatusByDataName('distributors'), function(list) {
            return $scope.distributors = list;
          });
        }
        if ($scope.isDisplaySalesman()) {
          return $scope.loadSalesmen();
        }
      });
      return $scope.init();
    }
  ]).controller('PerformanceReportSalesmanCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var afterLoad, tooltip;
      $scope.title = 'performance.report.salesman.title';
      $scope.defaultBackState = 'performance-report';
      $scope.afterFilterLoad = function() {};
      tooltip = function(label, xval, yval, flotItem) {
        return label + ' - ' + $scope.nameMap[xval - 1] + ': ' + $filter('number')(yval, 0);
      };
      afterLoad = function() {
        var data, i, nbOrders, revenues, salesResultDaily, ticks, _i, _j, _len, _len1, _ref, _ref1;
        revenues = [];
        nbOrders = [];
        ticks = [];
        $scope.nameMap = [];
        $scope.total = {
          revenue: 0,
          nbOrder: 0
        };
        _ref = $scope.record.salesResultsDaily;
        for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
          salesResultDaily = _ref[i];
          revenues.push([i + 1, salesResultDaily.salesResult.revenue]);
          nbOrders.push([i + 1, salesResultDaily.salesResult.nbOrder]);
          if ((i + 1) === 1 || (i + 1) % 5 === 0) {
            ticks.push([i + 1, '' + (i + 1)]);
          } else {
            ticks.push([i + 1, '']);
          }
          $scope.nameMap.push($filter('isoDate')(salesResultDaily.date));
        }
        $scope.chart.data = [
          {
            data: revenues,
            label: $filter('translate')('revenue')
          }, {
            data: nbOrders,
            label: $filter('translate')('order'),
            yaxis: 2
          }
        ];
        _ref1 = $scope.chart.data;
        for (i = _j = 0, _len1 = _ref1.length; _j < _len1; i = ++_j) {
          data = _ref1[i];
          if (i === 0 && revenues.length < 20) {
            data.bars = {
              show: true,
              fill: true,
              barWidth: .1,
              align: 'center'
            };
          } else {
            data.lines = {
              show: true,
              fill: false,
              fillColor: {
                colors: [
                  {
                    opacity: 0
                  }, {
                    opacity: 0.3
                  }
                ]
              }
            };
            data.points = {
              show: true,
              lineWidth: 2,
              fill: false,
              fillColor: "#ffffff",
              symbol: "circle",
              radius: 5
            };
            if (i === 0) {
              data.lines.fill = true;
            }
          }
        }
        return $scope.chart.options.xaxis = {
          autoscaleMargin: .10,
          ticks: ticks
        };
      };
      $scope.isDisplayChart = function() {
        return ($scope.chart.data != null) && ($scope.chart.data[0].data != null) && $scope.chart.data[0].data.length > 1;
      };
      $scope.getMonthDisplay = function() {
        return moment($scope.date).format('MM/YYYY');
      };
      $scope.clickOnProductCategory = function(productCategory) {
        if ((productCategory != null) && (productCategory.id != null)) {
          if (($scope.productCategoriySalesQuantityId != null) && $scope.productCategoriySalesQuantityId === productCategory.id) {
            return $scope.productCategoriySalesQuantityId = null;
          } else {
            return $scope.productCategoriySalesQuantityId = productCategory.id;
          }
        }
      };
      $scope.isProductCategoryOpened = function(productCategory) {
        return (productCategory != null) && $scope.productCategoriySalesQuantityId === productCategory.id;
      };
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      CtrlInitiatorService.initFilterViewCtrl($scope);
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'performance',
          subCategory: 'by-salesman',
          month: $scope.date.getMonth(),
          year: $scope.date.getFullYear(),
          salesmanId: $scope.filter.salesmanId
        };
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(data) {
          $scope.record = data;
          return afterLoad();
        }, function(error) {
          if (error.status === 400) {
            return $state.go('404');
          } else {
            return toast.logError($filter('translate')('loading.error'));
          }
        });
      };
      $scope.addInitFunction(function() {
        $scope.chart = {};
        $scope.chart.options = angular.copy(chartOptions);
        $scope.chart.options.tooltip.content = tooltip;
        $scope.productCategoriySalesQuantityId = null;
        if (($scope.filter != null) && ($scope.filter.date != null)) {
          $scope.date = globalUtils.parseIsoDate($scope.filter.date);
          if (($scope.date == null) || ($scope.filter.salesmanId == null)) {
            return $state.go('404');
          } else {
            return $scope.reloadData();
          }
        } else {
          return $state.go('404');
        }
      });
      return $scope.init();
    }
  ]);

}).call(this);

//# sourceMappingURL=performance-report-ctrl.js.map

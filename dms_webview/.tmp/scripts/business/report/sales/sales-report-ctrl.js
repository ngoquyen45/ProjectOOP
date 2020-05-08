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

  angular.module('app').controller('SalesReportCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state) {
      $scope.title = 'sales.report.title';
      $scope.isUseDistributorFilter = function() {
        return CtrlUtilsService.getWho() !== 'distributor';
      };
      $scope.afterFilterLoad = function() {};
      $scope.goToReport = function() {
        $scope.filter.date = globalUtils.createIsoDate($scope.date.date);
        if ($scope.filter.reportType === 'daily') {
          return $state.go('sales-report-daily', {
            filter: $scope.getFilterAsString()
          });
        } else if ($scope.filter.reportType === 'distributor') {
          return $state.go('sales-report-distributor', {
            filter: $scope.getFilterAsString()
          });
        } else if ($scope.filter.reportType === 'product') {
          if ($scope.filter.productCategoryId != null) {
            return $state.go('sales-report-product', {
              filter: $scope.getFilterAsString()
            });
          } else {
            return toast.logError($filter('translate')('error.data.input.not.valid'));
          }
        } else if ($scope.filter.reportType === 'salesman') {
          return $state.go('sales-report-salesman', {
            filter: $scope.getFilterAsString()
          });
        }
      };
      $scope.isDisplayProductCategory = function() {
        return $scope.filter.reportType === 'product';
      };
      CtrlInitiatorService.initFilterViewCtrl($scope);
      CtrlInitiatorService.initUseDatePickerCtrl($scope);
      $scope.addInitFunction(function() {
        if ($scope.filter.reportType == null) {
          $scope.filter.reportType = 'daily';
        }
        if ($scope.filter.date != null) {
          $scope.date = $scope.createDatePickerModel(globalUtils.parseIsoDate($scope.filter.date));
        } else {
          $scope.date = $scope.createDatePickerModel(new Date());
        }
        return LoadingUtilsService.loadProductCategories($scope.who, $scope.loadStatus.getStatusByDataName('productCategories'), function(list) {
          return $scope.productCategories = list;
        });
      });
      return $scope.init();
    }
  ]).controller('SalesReportDetailCtrl', [
    '$scope', 'CtrlUtilsService', 'CtrlInitiatorService', 'CtrlCategoryInitiatorService', 'LoadingUtilsService', '$filter', 'toast', '$state', '$stateParams', function($scope, CtrlUtilsService, CtrlInitiatorService, CtrlCategoryInitiatorService, LoadingUtilsService, $filter, toast, $state, $stateParams) {
      var afterLoad, tooltip;
      $scope.title = '';
      $scope.defaultBackState = 'sales-report';
      $scope.afterFilterLoad = function() {};
      CtrlInitiatorService.initCanBackViewCtrl($scope);
      CtrlInitiatorService.initFilterViewCtrl($scope);
      tooltip = function(label, xval, yval, flotItem) {
        return label + ' - ' + $scope.nameMap[xval - 1] + ': ' + $filter('number')(yval, 0);
      };
      afterLoad = function() {
        var data, dto, i, nbOrders, revenues, ticks, _i, _j, _len, _len1, _ref, _ref1;
        revenues = [];
        nbOrders = [];
        ticks = [];
        $scope.nameMap = [];
        $scope.total = {
          revenue: 0,
          nbOrder: 0
        };
        _ref = $scope.records;
        for (i = _i = 0, _len = _ref.length; _i < _len; i = ++_i) {
          dto = _ref[i];
          revenues.push([i + 1, dto.salesResult.revenue]);
          nbOrders.push([i + 1, dto.salesResult.nbOrder]);
          $scope.total.revenue += dto.salesResult.revenue;
          $scope.total.nbOrder += dto.salesResult.nbOrder;
          if ($scope.filter.reportType === 'daily') {
            if ((i + 1) === 1 || (i + 1) % 5 === 0) {
              ticks.push([i + 1, '' + (i + 1)]);
            } else {
              ticks.push([i + 1, '']);
            }
            $scope.nameMap.push((i + 1) + '/' + moment($scope.date).format('MM/YYYY'));
          } else if ($scope.filter.reportType === 'distributor') {
            ticks.push([i + 1, globalUtils.getChartLabel(dto.name)]);
            $scope.nameMap.push(globalUtils.escapeHTML(dto.name));
          } else if ($scope.filter.reportType === 'product') {
            ticks.push([i + 1, globalUtils.getChartLabel(dto.name)]);
            $scope.nameMap.push(globalUtils.escapeHTML(dto.name));
          } else if ($scope.filter.reportType === 'salesman') {
            ticks.push([i + 1, globalUtils.getChartLabel(dto.fullname)]);
            $scope.nameMap.push(globalUtils.escapeHTML(dto.fullname));
          }
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
      $scope.reloadData = function() {
        var params;
        params = {
          who: $scope.who,
          category: 'report',
          subCategory: 'sales',
          month: $scope.date.getMonth(),
          year: $scope.date.getFullYear()
        };
        if ($scope.filter.reportType === 'daily') {
          params.param = 'daily';
        } else if ($scope.filter.reportType === 'distributor') {
          params.param = 'by-distributor';
        } else if ($scope.filter.reportType === 'product') {
          params.param = 'by-product';
          params.productCategoryId = $scope.filter.productCategoryId;
        } else if ($scope.filter.reportType === 'salesman') {
          params.param = 'by-salesman';
        }
        return CtrlUtilsService.loadSingleData(params, $scope.loadStatus.getStatusByDataName('data'), function(data) {
          $scope.records = data.list;
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
        var reportTitle;
        $scope.chart = {};
        $scope.chart.options = angular.copy(chartOptions);
        $scope.chart.options.tooltip.content = tooltip;
        if (($scope.filter != null) && ($scope.filter.date != null)) {
          $scope.date = globalUtils.parseIsoDate($scope.filter.date);
          if (($scope.date == null) || (($scope.filter.productCategoryId == null) && $scope.filter.reportType === 'product')) {
            return $state.go('404');
          } else {
            reportTitle = null;
            if ($scope.filter.reportType === 'daily') {
              reportTitle = $filter('translate')('sales.report.daily.title');
            } else if ($scope.filter.reportType === 'distributor') {
              reportTitle = $filter('translate')('sales.report.distributor.title');
            } else if ($scope.filter.reportType === 'product') {
              reportTitle = $filter('translate')('sales.report.product.title');
            } else if ($scope.filter.reportType === 'salesman') {
              reportTitle = $filter('translate')('sales.report.salesman.title');
            }
            $scope.title = reportTitle + ' - ' + moment($scope.date).format('MM-YYYY');
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

//# sourceMappingURL=sales-report-ctrl.js.map

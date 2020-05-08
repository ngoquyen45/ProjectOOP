(function() {
  'use strict';
  angular.module('app.state').constant('STATES', [
    {
      name: 'client',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'ClientCtrl',
      roles: ['SUPER', 'SUPPORTER']
    }, {
      name: 'client-detail',
      templateUrl: 'views/business/super-admin/client-detail.html',
      controller: 'ClientDetailCtrl',
      roles: ['SUPER', 'SUPPORTER']
    }, {
      name: 'import-master-data',
      templateUrl: 'views/business/import/import-master-data.html',
      controller: 'ImportMasterDataCtrl',
      roles: ['SUPER', 'SUPPORTER']
    }, {
      name: 'system-config',
      templateUrl: 'views/business/super-admin/system-config.html',
      controller: 'SystemConfigCtrl',
      roles: ['SUPER']
    }, {
      name: 'system-action',
      templateUrl: 'views/business/super-admin/system-action.html',
      controller: 'SystemActionCtrl',
      roles: ['SUPER']
    }, {
      name: 'dashboard',
      templateUrl: 'views/business/dashboard/dashboard.html',
      controller: 'DashboardCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'user',
      templateUrl: 'views/business/system/user.html',
      controller: 'UserCtrl',
      roles: ['AD']
    }, {
      name: 'user-detail',
      templateUrl: 'views/business/system/user-detail.html',
      controller: 'UserDetailCtrl',
      roles: ['AD']
    }, {
      name: 'user-observer-distributors',
      templateUrl: 'views/business/system/user-observer-distributors.html',
      controller: 'UserObserverDistributorsCtrl',
      roles: ['AD']
    }, {
      name: 'distributor',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'DistributorCtrl',
      roles: ['AD']
    }, {
      name: 'distributor-detail',
      templateUrl: 'views/business/system/distributor-detail.html',
      controller: 'DistributorDetailCtrl',
      roles: ['AD']
    }, {
      name: 'client-config',
      templateUrl: 'views/business/system/client-config.html',
      controller: 'ClientConfigCtrl',
      roles: ['AD']
    }, {
      name: 'calendar-config',
      templateUrl: 'views/business/system/calendar-config.html',
      controller: 'CalendarConfigCtrl',
      roles: ['AD']
    }, {
      name: 'uom',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'UomCtrl',
      roles: ['AD']
    }, {
      name: 'product-category',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'ProductCategoryCtrl',
      roles: ['AD']
    }, {
      name: 'product',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'ProductCtrl',
      roles: ['AD']
    }, {
      name: 'product-detail',
      templateUrl: 'views/business/product/product-detail.html',
      controller: 'ProductDetailCtrl',
      roles: ['AD']
    }, {
      name: 'import-product',
      templateUrl: 'views/business/import/import-product.html',
      controller: 'ImportProductCtrl',
      roles: ['AD']
    }, {
      name: 'promotion',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'PromotionCtrl',
      roles: ['AD']
    }, {
      name: 'promotion-detail',
      templateUrl: 'views/business/product/promotion-detail.html',
      controller: 'PromotionDetailCtrl',
      roles: ['AD']
    }, {
      name: 'customer-type',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'CustomerTypeCtrl',
      roles: ['AD']
    }, {
      name: 'area',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'AreaCtrl',
      roles: ['AD']
    }, {
      name: 'customer',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'CustomerCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'customer-detail',
      templateUrl: 'views/business/customer/customer-detail.html',
      controller: 'CustomerDetailCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'import-customer',
      templateUrl: 'views/business/import/import-customer.html',
      controller: 'ImportCustomerCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'route',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'RouteCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'route-detail',
      templateUrl: 'views/business/schedule/route-detail.html',
      controller: 'RouteDetailCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'customer-schedule',
      templateUrl: 'views/business/schedule/customer-schedule.html',
      controller: 'CustomerScheduleCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'import-customer-schedule',
      templateUrl: 'views/business/import/import-customer-schedule.html',
      controller: 'ImportCustomerScheduleCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'survey',
      templateUrl: 'CATEGORY_LIST_TEMPLATE',
      controller: 'SurveyCtrl',
      roles: ['AD']
    }, {
      name: 'survey-detail',
      templateUrl: 'views/business/survey/survey-detail.html',
      controller: 'SurveyDetailCtrl',
      roles: ['AD']
    }, {
      name: 'target',
      templateUrl: 'views/business/target/target.html',
      controller: 'TargetCtrl',
      roles: ['SUP']
    }, {
      name: 'target-detail',
      templateUrl: 'views/business/target/target-detail.html',
      controller: 'TargetDetailCtrl',
      roles: ['SUP']
    }, {
      name: 'visit-today',
      templateUrl: 'views/business/visit-today/visit-today.html',
      controller: 'VisitTodayCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'visit-today-map',
      templateUrl: 'views/business/visit-today/visit-today-map.html',
      controller: 'VisitTodayMapCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'visit-detail',
      templateUrl: 'views/business/visit-today/visit-detail.html',
      controller: 'VisitDetailCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'order-pending',
      templateUrl: 'views/business/approval/order-pending.html',
      controller: 'OrderPendingCtrl',
      roles: ['AD', 'SUP', 'DIS']
    }, {
      name: 'order-approval',
      templateUrl: 'views/business/approval/order-approval.html',
      controller: 'OrderApprovalCtrl',
      roles: ['AD', 'SUP', 'DIS']
    }, {
      name: 'customer-pending',
      templateUrl: 'views/business/approval/customer-pending.html',
      controller: 'CustomerPendingCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'customer-approval',
      templateUrl: 'views/business/approval/customer-approval.html',
      controller: 'CustomerApprovalCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'customer-schedule-single',
      templateUrl: 'views/business/schedule/customer-schedule-single.html',
      controller: 'CustomerScheduleSingleCtrl',
      roles: ['AD', 'SUP']
    }, {
      name: 'feedback',
      templateUrl: 'views/business/feedback/feedback.html',
      controller: 'FeedbackCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'feedback-detail',
      templateUrl: 'views/business/feedback/feedback-detail.html',
      controller: 'FeedbackDetailCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'order',
      templateUrl: 'views/business/history/order.html',
      controller: 'OrderCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'order-detail',
      templateUrl: 'views/business/history/order-detail.html',
      controller: 'OrderDetailCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'visit',
      templateUrl: 'views/business/history/visit.html',
      controller: 'VisitCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'visit-photo',
      templateUrl: 'views/business/visit-photo/visit-photo.html',
      controller: 'VisitPhotoCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'sales-report',
      templateUrl: 'views/business/report/sales/sales-report.html',
      controller: 'SalesReportCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'sales-report-daily',
      templateUrl: 'views/business/report/sales/sales-report-daily.html',
      controller: 'SalesReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'sales-report-distributor',
      templateUrl: 'views/business/report/sales/sales-report-distributor.html',
      controller: 'SalesReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'sales-report-product',
      templateUrl: 'views/business/report/sales/sales-report-product.html',
      controller: 'SalesReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'sales-report-salesman',
      templateUrl: 'views/business/report/sales/sales-report-salesman.html',
      controller: 'SalesReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'visit-report',
      templateUrl: 'views/business/report/visit/visit-report.html',
      controller: 'VisitReportCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'visit-report-daily',
      templateUrl: 'views/business/report/visit/visit-report-daily.html',
      controller: 'VisitReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'visit-report-distributor',
      templateUrl: 'views/business/report/visit/visit-report-distributor.html',
      controller: 'VisitReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'visit-report-salesman',
      templateUrl: 'views/business/report/visit/visit-report-salesman.html',
      controller: 'VisitReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'survey-report',
      templateUrl: 'views/business/report/survey/survey-report.html',
      controller: 'SurveyReportCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'survey-report-detail',
      templateUrl: 'views/business/report/survey/survey-report-detail.html',
      controller: 'SurveyReportDetailCtrl',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'performance-report',
      templateUrl: 'views/business/report/performance/performance-report.html',
      controller: 'PerformanceReportCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'performance-report-salesman',
      templateUrl: 'views/business/report/performance/performance-report-salesman.html',
      controller: 'PerformanceReportSalesmanCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'order-export',
      templateUrl: 'views/business/export/order-export.html',
      controller: 'OrderExportCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'exchange-return-export',
      templateUrl: 'views/business/export/exchange-return-export.html',
      controller: 'ExchangeReturnExportCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'change-password',
      templateUrl: 'views/system/change-password.html',
      controller: 'ChangePasswordCtrl',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'distributor-price-list',
      templateUrl: 'views/business/price-list/distributor-price-list.html',
      controller: 'DistributorPriceListCtrl',
      roles: ['DIS']
    }, {
      name: 'import-price-list',
      templateUrl: 'views/business/import/import-distributor-price-list.html',
      controller: 'ImportDistributorPriceListCtrl',
      roles: ['DIS']
    }, {
      name: 'van-sales',
      templateUrl: 'views/business/van-sales/van-sales.html',
      controller: 'VanSalesCtrl',
      roles: ['DIS']
    }
  ]);

}).call(this);

//# sourceMappingURL=state.js.map

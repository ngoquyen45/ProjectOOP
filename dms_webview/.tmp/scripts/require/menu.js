(function() {
  'use strict';
  angular.module('app').constant('HOME_STATE', {
    SUPER: 'system-config',
    SUPPORTER: 'client',
    AD: 'dashboard',
    OBS: 'dashboard',
    SUP: 'dashboard',
    DIS: 'order-pending'
  }).constant('MENUS', [
    {
      name: 'system.config.title',
      href: '#/system-config',
      icon: 'fa fa-cog',
      roles: ['SUPER']
    }, {
      name: 'client.title',
      href: '#/client',
      icon: 'fa fa-building',
      roles: ['SUPER', 'SUPPORTER']
    }, {
      name: 'system.action.title',
      href: '#/system-action',
      icon: 'fa fa-hourglass-half',
      roles: ['SUPER']
    }, {
      name: 'dashboard.menu',
      href: '#/dashboard',
      icon: 'fa fa-dashboard',
      roles: ['AD', 'OBS', 'SUP']
    }, {
      name: 'system.menu',
      icon: 'fa fa-cogs',
      roles: ['AD'],
      children: [
        {
          name: 'user.title',
          href: '#/user',
          roles: ['AD']
        }, {
          name: 'distributor.title',
          href: '#/distributor',
          roles: ['AD']
        }, {
          name: 'client.config.title',
          href: '#/client-config',
          roles: ['AD']
        }, {
          name: 'calendar.config.title',
          href: '#/calendar-config',
          roles: ['AD']
        }
      ]
    }, {
      name: 'product.menu',
      icon: 'fa fa-barcode',
      roles: ['AD'],
      children: [
        {
          name: 'uom.title',
          href: '#/uom',
          roles: ['AD']
        }, {
          name: 'product.category.title',
          href: '#/product-category',
          roles: ['AD']
        }, {
          name: 'product.title',
          href: '#/product',
          roles: ['AD']
        }, {
          name: 'promotion.title',
          href: '#/promotion',
          roles: ['AD']
        }
      ]
    }, {
      name: 'customer.menu',
      icon: 'fa fa-group',
      roles: ['AD', 'SUP'],
      children: [
        {
          name: 'customer.type.title',
          href: '#/customer-type',
          roles: ['AD']
        }, {
          name: 'area.title',
          href: '#/area',
          roles: ['AD']
        }, {
          name: 'customer.title',
          href: '#/customer',
          roles: ['AD', 'SUP']
        }
      ]
    }, {
      name: 'survey.title',
      icon: 'fa fa-question-circle',
      href: '#/survey',
      roles: ['AD']
    }, {
      name: 'target.menu',
      icon: 'fa fa-crosshairs',
      href: '#/target',
      roles: ['SUP']
    }, {
      name: 'schedule.menu',
      icon: 'fa fa-code-fork',
      roles: ['AD', 'SUP'],
      children: [
        {
          name: 'route.title',
          href: '#/route',
          roles: ['AD', 'SUP']
        }, {
          name: 'customer.schedule.title',
          href: '#/customer-schedule',
          roles: ['AD', 'SUP']
        }
      ]
    }, {
      name: 'visit.today.menu',
      icon: 'fa fa-user-secret',
      roles: ['AD', 'OBS', 'SUP'],
      children: [
        {
          name: 'visit.today.list.menu',
          href: '#/visit-today',
          roles: ['AD', 'OBS', 'SUP']
        }, {
          name: 'visit.today.map.menu',
          href: '#/visit-today-map',
          roles: ['AD', 'OBS', 'SUP']
        }
      ]
    }, {
      name: 'approval.menu',
      icon: 'fa fa-gavel',
      roles: ['AD', 'SUP', 'DIS'],
      children: [
        {
          name: 'order',
          href: '#/order-pending',
          roles: ['AD', 'SUP', 'DIS']
        }, {
          name: 'customer',
          href: '#/customer-pending',
          roles: ['AD', 'SUP']
        }
      ]
    }, {
      name: 'feedback',
      icon: 'fa fa-comment',
      href: '#/feedback',
      roles: ['AD', 'OBS', 'SUP', 'DIS']
    }, {
      name: 'history.menu',
      icon: 'fa fa-archive',
      roles: ['AD', 'OBS', 'SUP', 'DIS'],
      children: [
        {
          name: 'order',
          href: '#/order',
          roles: ['AD', 'OBS', 'SUP', 'DIS']
        }, {
          name: 'visit',
          href: '#/visit',
          roles: ['AD', 'OBS', 'SUP']
        }, {
          name: 'visit.photo',
          href: '#/visit-photo',
          roles: ['AD', 'OBS', 'SUP', 'DIS']
        }
      ]
    }, {
      name: 'report.menu',
      icon: 'fa fa-pie-chart',
      roles: ['AD', 'OBS', 'SUP', 'DIS'],
      children: [
        {
          name: 'sales.report.menu',
          href: '#/sales-report',
          roles: ['AD', 'OBS', 'SUP', 'DIS']
        }, {
          name: 'visit.report.menu',
          href: '#/visit-report',
          roles: ['AD', 'OBS', 'SUP']
        }, {
          name: 'survey.report.menu',
          href: '#/survey-report',
          roles: ['AD', 'OBS', 'SUP']
        }, {
          name: 'performance.report.menu',
          href: '#/performance-report',
          roles: ['AD', 'OBS', 'SUP']
        }
      ]
    }, {
      name: 'export.menu',
      icon: 'fa fa-file-excel-o',
      roles: ['AD', 'OBS', 'SUP', 'DIS'],
      children: [
        {
          name: 'order.export.menu',
          href: '#/order-export',
          roles: ['AD', 'OBS', 'SUP', 'DIS']
        }, {
          name: 'exchange.return.export.menu',
          href: '#/exchange-return-export',
          roles: ['AD', 'OBS', 'SUP', 'DIS']
        }
      ]
    }, {
      name: 'distributor.price.list.menu',
      icon: 'fa fa-tag',
      href: '#/distributor-price-list',
      roles: ['DIS']
    }, {
      name: 'van.sales.menu',
      icon: 'fa fa-truck',
      href: '#/van-sales',
      roles: ['DIS']
    }
  ]);

}).call(this);

//# sourceMappingURL=menu.js.map

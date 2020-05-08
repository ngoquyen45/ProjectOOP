(function() {
  'use strict';
  angular.module('app').controller('NotificationCtrl', [
    '$scope', '$token', 'CtrlUtilsService', 'PushService', 'Factory', '$filter', 'toast', '$state', function($scope, $token, CtrlUtilsService, PushService, Factory, $filter, toast, $state) {
      var addToList, findRecordIndex, init, loadPendingCustomers, loadPendingOrders, loadUnreadFeedbacks, onCustomerMessage, onFeedbackMessage, onMessage, onOrderMessage, onPushConnected, removeFromList, syncData, transformDisplayData;
      init = function() {
        $scope.MIN_ITEM_COUNT = 5;
        $scope.INITIAL_ITEM_COUNT = 20;
        $scope.SYNC_EXPIRED_TIME = 1 * 60 * 1000;
        $scope.userId = $token.getUserInfo().id;
        $scope.who = CtrlUtilsService.getWho();
        $scope.data = {
          customers: [],
          feedbacks: [],
          orders: []
        };
        $scope.lastSyncTime = null;
        syncData();
        PushService.registerSubscribe('/user/queue/notify').then(null, null, onMessage);
        PushService.start();
        $scope.$on('PushReconnected', onPushConnected);
        return $scope.$on('PushConnected', onPushConnected);
      };
      syncData = function() {
        if ($scope.canApproveCustomer()) {
          loadPendingCustomers();
        }
        if ($scope.canReadFeedback()) {
          loadUnreadFeedbacks();
        }
        if ($scope.canApproveOrder()) {
          loadPendingOrders();
        }
        return $scope.lastSyncTime = new Date();
      };
      onPushConnected = function() {
        var currentDate, dateDiff;
        if ($scope.lastSyncTime != null) {
          currentDate = new Date();
          dateDiff = currentDate.getTime() - $scope.lastSyncTime.getTime();
          if (dateDiff > $scope.SYNC_EXPIRED_TIME) {
            return syncData();
          }
        }
      };
      onMessage = function(message) {
        if ((message != null) && (message.type != null)) {
          if (message.type === 'order' && $scope.canApproveOrder()) {
            return onOrderMessage(message);
          } else if (message.type === 'customer' && $scope.canApproveCustomer()) {
            return onCustomerMessage(message);
          } else if (message.type === 'feedback' && $scope.canReadFeedback()) {
            return onFeedbackMessage(message);
          }
        }
      };
      onCustomerMessage = function(message) {
        var notice, skipNotice;
        $scope.data.customers.count = message.data.count;
        skipNotice = message.senderId === $scope.userId;
        if (message.data.action === 'add') {
          notice = $filter('translate')('header.notification.customer.alert.add', {
            count: message.data.count
          });
          addToList($scope.data.customers, message.data.customer);
        } else if (message.data.action === 'approve') {
          notice = $filter('translate')('header.notification.customer.alert.approve', {
            name: message.data.customer.name
          });
          removeFromList($scope.data.customers, message.data.customer);
        } else if (message.data.action === 'reject') {
          notice = $filter('translate')('header.notification.customer.alert.reject', {
            name: message.data.customer.name
          });
          removeFromList($scope.data.customers, message.data.customer);
        }
        if ((notice != null) && !skipNotice) {
          toast.log(notice);
        }
        if ($scope.data.customers.original.length < $scope.MIN_ITEM_COUNT && $scope.data.customers.count >= $scope.MIN_ITEM_COUNT) {
          return loadPendingCustomers();
        }
      };
      onFeedbackMessage = function(message) {
        var notice, skipNotice;
        $scope.data.feedbacks.count = message.data.count;
        skipNotice = message.senderId === $scope.userId;
        if (message.data.action === 'add') {
          notice = $filter('translate')('header.notification.feedback.alert.add', {
            count: message.data.count
          });
          addToList($scope.data.feedbacks, message.data.feedback);
        } else if (message.data.action === 'read') {
          removeFromList($scope.data.feedbacks, message.data.feedback);
        }
        if ((notice != null) && !skipNotice) {
          toast.log(notice);
        }
        if ($scope.data.feedbacks.original.length < $scope.MIN_ITEM_COUNT && $scope.data.feedbacks.count >= $scope.MIN_ITEM_COUNT) {
          return loadUnreadFeedbacks();
        }
      };
      onOrderMessage = function(message) {
        var notice, skipNotice;
        $scope.data.orders.count = message.data.count;
        skipNotice = message.senderId === $scope.userId;
        if (message.data.action === 'add') {
          notice = $filter('translate')('header.notification.order.alert.add', {
            count: message.data.count
          });
          addToList($scope.data.orders, message.data.order);
        } else if (message.data.action === 'approve') {
          notice = $filter('translate')('header.notification.order.alert.approve', {
            code: message.data.order.code
          });
          removeFromList($scope.data.orders, message.data.order);
        } else if (message.data.action === 'reject') {
          notice = $filter('translate')('header.notification.order.alert.reject', {
            code: message.data.order.code
          });
          removeFromList($scope.data.orders, message.data.order);
        }
        if ((notice != null) && !skipNotice) {
          toast.log(notice);
        }
        if ($scope.data.orders.original.length < $scope.MIN_ITEM_COUNT && $scope.data.orders.count >= $scope.MIN_ITEM_COUNT) {
          return loadPendingOrders();
        }
      };
      addToList = function(list, record) {
        list.original.unshift(record);
        return transformDisplayData(list);
      };
      removeFromList = function(list, record) {
        var index;
        index = findRecordIndex(list.original, record.id);
        if (index > -1) {
          list.original.splice(index, 1);
        }
        return transformDisplayData(list);
      };
      findRecordIndex = function(arr, id) {
        var i, size;
        i = 0;
        size = arr.length;
        while (i < size) {
          if (arr[i].id === id) {
            return i;
          }
          i++;
        }
        return -1;
      };
      loadPendingCustomers = function() {
        var loadSuccess, params;
        params = {
          who: $scope.who,
          category: 'customer',
          subCategory: 'pending',
          page: 1,
          size: $scope.INITIAL_ITEM_COUNT
        };
        loadSuccess = function(data) {
          $scope.data.customers.count = data.count;
          $scope.data.customers.original = data.list ? data.list : [];
          return transformDisplayData($scope.data.customers);
        };
        return Factory.doGet(params, loadSuccess);
      };
      loadUnreadFeedbacks = function() {
        var loadSuccess, params;
        params = {
          who: $scope.who,
          category: 'feedback',
          page: 1,
          size: $scope.INITIAL_ITEM_COUNT,
          read: false
        };
        loadSuccess = function(data) {
          $scope.data.feedbacks.count = data.count;
          $scope.data.feedbacks.original = data.list ? data.list : [];
          return transformDisplayData($scope.data.feedbacks);
        };
        return Factory.doGet(params, loadSuccess);
      };
      loadPendingOrders = function() {
        var loadSuccess, params;
        params = {
          who: $scope.who,
          category: 'order',
          subCategory: 'pending',
          page: 1,
          size: $scope.INITIAL_ITEM_COUNT
        };
        loadSuccess = function(data) {
          $scope.data.orders.count = data.count;
          $scope.data.orders.original = data.list ? data.list : [];
          return transformDisplayData($scope.data.orders);
        };
        return Factory.doGet(params, loadSuccess);
      };
      transformDisplayData = function(list) {
        var dataLength, i, size, _results;
        list.display = [];
        if ((list.original != null) && list.original.length > 0) {
          size = $scope.MIN_ITEM_COUNT;
          dataLength = list.original.length;
          if (dataLength < size) {
            size = dataLength;
          }
          i = 0;
          _results = [];
          while (i < size) {
            list.display.push(list.original[i]);
            _results.push(i++);
          }
          return _results;
        }
      };
      $scope.gotoCustomerPending = function() {
        return $state.go('customer-pending');
      };
      $scope.gotoCustomerPendingDetail = function(customer) {
        return $state.go('customer-approval', {
          id: customer.id
        });
      };
      $scope.gotoFeedbackList = function() {
        return $state.go('feedback');
      };
      $scope.gotoFeedbackDetail = function(feedback) {
        return $state.go('feedback-detail', {
          id: feedback.id
        });
      };
      $scope.gotoApproveOrderList = function() {
        return $state.go('order-pending');
      };
      $scope.gotoOrderApproveDetail = function(order) {
        return $state.go('order-approval', {
          id: order.id
        });
      };
      $scope.canApproveOrder = function() {
        return _.includes(['admin', 'supervisor', 'distributor'], $scope.who);
      };
      $scope.canApproveCustomer = function() {
        return _.includes(['admin', 'supervisor'], $scope.who);
      };
      $scope.canReadFeedback = function() {
        return _.includes(['admin', 'supervisor', 'distributor'], $scope.who);
      };
      return init();
    }
  ]);

}).call(this);

//# sourceMappingURL=notification-ctrl.js.map

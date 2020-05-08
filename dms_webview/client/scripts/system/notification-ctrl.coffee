'use strict'

angular.module('app')

.controller('NotificationCtrl', [
        '$scope', '$token', 'CtrlUtilsService', 'PushService', 'Factory', '$filter', 'toast', '$state'
        ($scope, $token, CtrlUtilsService, PushService, Factory, $filter, toast, $state) ->

            init = () ->
                # Minimum item count in browser.
                # Because number of items may be reduce when action occurs (eg: approve an order)
                # So, when number of items is smaller than this value, we need to fetch more from server
                $scope.MIN_ITEM_COUNT = 5

                # Number of items we fetch from server each time
                $scope.INITIAL_ITEM_COUNT = 20

                # Maximum period of time that the notification's data can live without Push
                # After this period, when Push connected, we need to fetch it again
                $scope.SYNC_EXPIRED_TIME = 1 * 60 * 1000

                $scope.userId = $token.getUserInfo().id
                $scope.who = CtrlUtilsService.getWho()
                # customers {
                #     original: Original list from server
                #     display: First 5 items to display
                #     count: Total count
                # }
                $scope.data = {
                    customers: []
                    feedbacks: []
                    orders: []
                }

                $scope.lastSyncTime = null

                syncData()

                PushService.registerSubscribe('/user/queue/notify').then(null, null, onMessage)
                PushService.start()

                $scope.$on('PushReconnected', onPushConnected)
                $scope.$on('PushConnected', onPushConnected)

            syncData = () ->
                loadPendingCustomers() if $scope.canApproveCustomer()
                loadUnreadFeedbacks() if $scope.canReadFeedback()
                loadPendingOrders() if $scope.canApproveOrder()
                $scope.lastSyncTime = new Date()

            onPushConnected = () ->
                if $scope.lastSyncTime?
                    currentDate = new Date()
                    dateDiff = currentDate.getTime() - $scope.lastSyncTime.getTime()
                    if dateDiff > $scope.SYNC_EXPIRED_TIME
                        syncData()

            onMessage = (message) ->
                if message? and message.type?
                    if message.type is 'order' and $scope.canApproveOrder() then onOrderMessage(message)
                    else if message.type is 'customer' and $scope.canApproveCustomer() then onCustomerMessage(message)
                    else if message.type is 'feedback' and $scope.canReadFeedback() then onFeedbackMessage(message)

            onCustomerMessage = (message) ->
                $scope.data.customers.count = message.data.count

                # Skip notify if current user generate this message
                skipNotice = message.senderId is $scope.userId

                if message.data.action is 'add'
                    notice = $filter('translate')('header.notification.customer.alert.add', { count: message.data.count })
                    addToList($scope.data.customers, message.data.customer)

                else if message.data.action is 'approve'
                    notice = $filter('translate')('header.notification.customer.alert.approve', { name: message.data.customer.name })
                    removeFromList($scope.data.customers, message.data.customer)

                else if message.data.action is 'reject'
                    notice = $filter('translate')('header.notification.customer.alert.reject', { name: message.data.customer.name })
                    removeFromList($scope.data.customers, message.data.customer)

                # Show toast
                if notice? and not skipNotice then toast.log(notice)

                # Reload if number of item in list too minimum
                if $scope.data.customers.original.length < $scope.MIN_ITEM_COUNT and $scope.data.customers.count >= $scope.MIN_ITEM_COUNT then loadPendingCustomers()

            onFeedbackMessage = (message) ->
                $scope.data.feedbacks.count = message.data.count

                # Skip notify if current user generate this message
                skipNotice = message.senderId is $scope.userId

                if message.data.action is 'add'
                    notice = $filter('translate')('header.notification.feedback.alert.add', { count: message.data.count })
                    addToList($scope.data.feedbacks, message.data.feedback)

                else if message.data.action is 'read'
                    removeFromList($scope.data.feedbacks, message.data.feedback)

                # Show toast
                if notice? and not skipNotice then toast.log(notice)

                # Reload if number of item in list too minimum
                if $scope.data.feedbacks.original.length < $scope.MIN_ITEM_COUNT and $scope.data.feedbacks.count >= $scope.MIN_ITEM_COUNT then loadUnreadFeedbacks()


            onOrderMessage = (message) ->
                $scope.data.orders.count = message.data.count

                # Skip notify if current user generate this message
                skipNotice = message.senderId is $scope.userId

                if message.data.action is 'add'
                    notice = $filter('translate')('header.notification.order.alert.add', { count: message.data.count })
                    addToList($scope.data.orders, message.data.order)

                else if message.data.action is 'approve'
                    notice = $filter('translate')('header.notification.order.alert.approve', { code: message.data.order.code })
                    removeFromList($scope.data.orders, message.data.order)

                else if message.data.action is 'reject'
                    notice = $filter('translate')('header.notification.order.alert.reject', { code: message.data.order.code })
                    removeFromList($scope.data.orders, message.data.order)

                # Show toast
                if notice? and not skipNotice then toast.log(notice)

                # Reload if number of item in list too minimum
                if $scope.data.orders.original.length < $scope.MIN_ITEM_COUNT and $scope.data.orders.count >= $scope.MIN_ITEM_COUNT then loadPendingOrders()


            # Add to head of list
            addToList = (list, record) ->
                list.original.unshift(record)
                transformDisplayData(list)

            # Remove from list
            removeFromList = (list, record) ->
                index = findRecordIndex(list.original, record.id)
                if index > -1 then list.original.splice(index, 1)
                transformDisplayData(list)

            findRecordIndex = (arr, id) ->
                i = 0
                size = arr.length
                while i < size
                    if arr[i].id is id then return i
                    i++
                -1

            loadPendingCustomers = () ->
                params =
                    who: $scope.who
                    category: 'customer'
                    subCategory: 'pending'
                    page: 1
                    size: $scope.INITIAL_ITEM_COUNT

                loadSuccess = (data) ->
                    $scope.data.customers.count = data.count
                    $scope.data.customers.original = if data.list then data.list else []

                    transformDisplayData($scope.data.customers)

                Factory.doGet(params, loadSuccess)

            loadUnreadFeedbacks = () ->
                params =
                    who: $scope.who
                    category: 'feedback'
                    page: 1
                    size: $scope.INITIAL_ITEM_COUNT
                    read: false

                loadSuccess = (data) ->
                    $scope.data.feedbacks.count = data.count
                    $scope.data.feedbacks.original = if data.list then data.list else []

                    transformDisplayData($scope.data.feedbacks)

                Factory.doGet(params, loadSuccess)

            loadPendingOrders = () ->
                params =
                    who: $scope.who
                    category: 'order'
                    subCategory: 'pending'
                    page: 1
                    size: $scope.INITIAL_ITEM_COUNT

                loadSuccess = (data) ->
                    $scope.data.orders.count = data.count
                    $scope.data.orders.original = if data.list then data.list else []

                    transformDisplayData($scope.data.orders)

                Factory.doGet(params, loadSuccess)

            transformDisplayData = (list) ->
                list.display = [];
                if list.original? and list.original.length > 0
                    size = $scope.MIN_ITEM_COUNT
                    dataLength = list.original.length
                    if dataLength < size then size = dataLength
                    i = 0
                    while i < size
                        list.display.push(list.original[i])
                        i++

            # Open approve customer list screen
            $scope.gotoCustomerPending = () -> $state.go('customer-pending')

            # Open approve customer details screen
            $scope.gotoCustomerPendingDetail = (customer) -> $state.go('customer-approval', { id: customer.id })

            # Open feedback list
            $scope.gotoFeedbackList = () -> $state.go('feedback')

            # Open feedback detail
            $scope.gotoFeedbackDetail = (feedback) -> $state.go('feedback-detail', { id: feedback.id })

            # Open approve order list
            $scope.gotoApproveOrderList = () -> $state.go('order-pending')

            # Open approve order detail
            $scope.gotoOrderApproveDetail = (order) -> $state.go('order-approval', { id: order.id })

            $scope.canApproveOrder = -> _.includes(['admin', 'supervisor', 'distributor'], $scope.who)

            $scope.canApproveCustomer = -> _.includes(['admin', 'supervisor'], $scope.who)

            $scope.canReadFeedback = -> _.includes(['admin', 'supervisor', 'distributor'], $scope.who)

            init()
    ])

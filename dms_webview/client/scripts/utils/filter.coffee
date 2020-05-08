'use strict'

angular.module('app.filter', [])

.filter('propsFilter', ->
    (items, props) ->
        out = []

        if angular.isArray(items)
            for item in items
                itemMatches = false
                keys = Object.keys(props)

                for key in keys
                    text = if (not props[key]?) then '' else props[key].toLowerCase()
                    value = ''
                    if item? and item[key]?
                        value = item[key].toString().toLowerCase()

                    if value.indexOf(text) isnt -1
                        itemMatches = true
                        break

                if itemMatches
                    out.push(item)

        else
            out = items

        return out
)

.filter('isoDate', ['CtrlUtilsService', (CtrlUtilsService) ->
    (str) ->
        if str?
            date = globalUtils.parseIsoDate(str)
            if date?
                return moment(date).format(CtrlUtilsService.getUserInfo().dateFormat.toUpperCase())

        return ''
])

.filter('isoDateTime', ['CtrlUtilsService', (CtrlUtilsService) ->
    (str) ->
        if str?
            date = globalUtils.parseIsoTime(str)
            if date?
                return moment(date).format(CtrlUtilsService.getUserInfo().dateFormat.toUpperCase() + ' HH:mm:ss')

        return ''
])

.filter('isoTime', () ->
    (str) ->
        if str?
            date = globalUtils.parseIsoTime(str)
            if date?
                return moment(date).format('HH:mm:ss')

        return ''
)

.filter('bigNumber', ['$filter', ($filter) ->
    (value) ->
        if value?
            if isNaN(value)
                return value
            else
                unit = ''
                if value > 10000000000
                    value = value / 1000000000
                    unit = ' B'
                else if value > 10000000
                    value = value / 1000000
                    unit = ' M'
                else if value > 10000
                    value = value / 1000
                    unit = ' k'

                if value > 99
                    return $filter('number')(value, 0) + unit
                else if (value > 1)
                    return $filter('number')(value, 1) + unit
                else
                    return $filter('number')(value, 2) + unit
])

.filter('bigInteger', ['$filter', ($filter) ->
    (value) ->
        if value?
            if isNaN(value)
                return value
            else
                return $filter('bigNumber')($filter('number')(value, 0))
])

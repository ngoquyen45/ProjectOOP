'use strict'

utils = {}

utils.parseIsoDate = (isoDate) ->
    if isoDate?
        if isoDate.length is 19
            moment(isoDate, 'YYYY-MM-DDTHH:mm:ss').toDate()
        else if isoDate.length is 10
            moment(isoDate, 'YYYY-MM-DD').toDate()

utils.parseIsoTime = (isoTime) ->
    if isoTime?
        if isoTime.length is 10
            moment(isoTime, 'YYYY-MM-DD').toDate()
        else if isoTime.length is 19
            moment(isoTime, 'YYYY-MM-DDTHH:mm:ss').toDate()

utils.createIsoDate = (date) ->
    if date?
        moment(date).format('YYYY-MM-DD')

utils.escapeHTML = (html) ->
    html = _.escape(html)
    return html

utils.getChartLabel = (label) ->
    if label.length > 23
        length = label.length
        label = label.substring(0, 10) + '...' + label.substring(length - 10)
    label = _.escape(label)
    return label

window.globalUtils = utils

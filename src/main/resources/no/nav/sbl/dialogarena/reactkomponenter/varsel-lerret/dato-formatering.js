const moment = window.moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

export function prettyDate(date, format = 'DD. MMM, HH.mm') {
    return moment(date).format(format);
}

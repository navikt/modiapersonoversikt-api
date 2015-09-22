var moment = window.moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

export function prettyDate(date) {
    return moment(date).format('DD. MMM, HH.mm');
}

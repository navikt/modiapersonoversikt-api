const moment = window.moment = require('moment');
require('moment/locale/nb');

moment.locale('nb');

export function prettyDate(date, format = 'DD. MMM') {
    return moment(date).format(format);
}

export function nyesteDatoForst(varsel1, varsel2) {
    if (varsel1.utsendingsTidspunkt === null) {
        return -1;
    }
    if (varsel2.utsendingsTidspunkt === null) {
        return 1;
    }
    return varsel1.utsendingsTidspunkt < varsel2.utsendingsTidspunkt ? 1 : -1;
}

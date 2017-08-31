import moment from 'moment';

export const formaterJavaDate = (dato) =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth)).format('DD.MM.YYYY');

export const formaterBelop = (belop) =>
    belop.toLocaleString('nb-NO', {
        style: 'currency',
        currency: 'NOK',
        currencyDisplay: 'code'
    });

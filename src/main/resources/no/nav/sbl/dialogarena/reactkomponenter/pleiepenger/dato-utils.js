import moment from 'moment';

const formaterJavaDate = (dato) =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth)).format('DD.MM.YYYY');

export default formaterJavaDate;

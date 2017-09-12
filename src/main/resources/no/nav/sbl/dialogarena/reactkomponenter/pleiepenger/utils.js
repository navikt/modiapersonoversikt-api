import moment from 'moment';

export const konverterTilMomentDato = dato =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth));

export const formaterJavaDate = dato =>
    konverterTilMomentDato(dato).format('DD.MM.YYYY');

export const formaterBelop = (belop) =>
    belop.toLocaleString('nb-NO', {
        style: 'currency',
        currency: 'NOK',
        currencyDisplay: 'code'
    });

export const emdash = '\u2014';
export const formaterOptionalProsentVerdi = verdi => (
    verdi ? verdi + ' %' : emdash
);

export const formaterOptionalVerdi = verdi => (
  verdi || emdash
);

export const kjonnFraIdent = ident => {
    if (!ident || ident.length < 11) {
        return 'U';
    }
    return ident.charAt(8) % 2 ? 'M' : 'K';
};

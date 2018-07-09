import moment from 'moment';

export const konverterTilMomentDato = dato =>
    moment(new Date(dato.year, dato.monthValue - 1, dato.dayOfMonth));

export const emdash = '\u2014';

export const formaterJavaDate = dato =>
    (dato ? konverterTilMomentDato(dato).format('DD.MM.YYYY') : emdash);

export const formaterBelop = (belop) =>
    belop.toLocaleString('nb-NO', {
        style: 'currency',
        currency: 'NOK',
        currencyDisplay: 'code'
    });

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

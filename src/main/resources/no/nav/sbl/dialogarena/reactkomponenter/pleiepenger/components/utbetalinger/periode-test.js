import { expect } from 'chai';

import '../../../test-config';
import { sorterVedtak } from './periode';

const mockVedtak = tilOgMedDato => ({
    periode: {
        fraOgMed: {
            year: 2017,
            monthValue: 5,
            dayOfMonth: 12
        },
        tilOgMed: {
            year: tilOgMedDato,
            monthValue: 5,
            dayOfMonth: 12
        }
    },
    anvistUtbetaling: {
        year: 2017,
        monthValue: 5,
        dayOfMonth: 12
    },
    bruttoBelop: 500,
    dagsats: 10

});

describe('Periode', () => {
    describe('sortering av vedtak', () => {
        it('viser vedtak med siste tilOgMed når nyest er sist i listen', () => {
            const vedtakListe = sorterVedtak([mockVedtak(2016), mockVedtak(2017)]);
            expect(vedtakListe[0].periode.tilOgMed.year).to.equal(2017);
        });
        it('viser vedtak med siste tilOgMed når nyest er først i listen', () => {
            const vedtakListe = sorterVedtak([mockVedtak(2017), mockVedtak(2016)]);
            expect(vedtakListe[0].periode.tilOgMed.year).to.equal(2017);
        });
    });
});

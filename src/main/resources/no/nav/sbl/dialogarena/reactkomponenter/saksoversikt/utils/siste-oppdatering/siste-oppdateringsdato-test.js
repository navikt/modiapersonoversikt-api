/* eslint no-unused-expressions:0 */
import './../../../test-config';
import { expect } from 'chai';
import { sisteOppdatering } from './siste-oppdateringsdato';
import { javaLocalDateTimeToJSDate } from './../dato-utils';

describe('SisteOppdateringsDato', () => {
    const nyesteDato = {
        year: 2016,
        monthValue: 4,
        dayOfMonth: 29,
        hour: 10,
        minute: 39,
        second: 55
    };

    const eldreDato = {
        year: 2016,
        monthValue: 4,
        dayOfMonth: 1,
        hour: 10,
        minute: 39,
        second: 55
    };

    const dokumentMedNyDato = { dato: nyesteDato };
    const dokumentMedEldreDato = { dato: eldreDato };
    const behandlingskjedeMedNyDato = { sistOppdatert: nyesteDato };
    const behandligskjedeMedEldreDato = { sistOppdatert: eldreDato };

    it('Viser ingen dato om hverken dokument eller behandlingskjede finnes', () => {
        const sisteOppdateringsdato = sisteOppdatering(null, null);

        expect(sisteOppdateringsdato).to.be.null;
    });

    it('Viser dato til nyeste behandligskjede om dokument ikke finnes', () => {
        const sisteOppdateringsdato = sisteOppdatering(null, behandlingskjedeMedNyDato);

        expect(sisteOppdateringsdato).to.be.eql(javaLocalDateTimeToJSDate(nyesteDato));
    });

    it('Viser dato til nyeste dokument om behandligskjede ikke finnes', () => {
        const sisteOppdateringsdato = sisteOppdatering(dokumentMedNyDato, null);

        expect(sisteOppdateringsdato).to.be.eql(javaLocalDateTimeToJSDate(nyesteDato));
    });

    it('Viser dokumentets dato om dokumentets dato er nyere en behandlingskjedens', () => {
        const sisteOppdateringsdato = sisteOppdatering(dokumentMedNyDato, behandligskjedeMedEldreDato);

        expect(sisteOppdateringsdato).to.be.eql(javaLocalDateTimeToJSDate(nyesteDato));
    });

    // Viser datoen til behandlingskjede
    it('Finner siste oppdateringsdato for sakstema naar dokumenter er sist oppdatert', () => {
        const sisteOppdateringsdato = sisteOppdatering(dokumentMedEldreDato, behandlingskjedeMedNyDato);

        expect(sisteOppdateringsdato).to.be.eql(javaLocalDateTimeToJSDate(nyesteDato));
    });
});

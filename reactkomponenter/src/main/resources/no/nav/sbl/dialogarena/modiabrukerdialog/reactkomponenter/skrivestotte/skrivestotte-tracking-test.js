/* eslint-env mocha */
/* eslint no-unused-expressions:0 new-cap:0 */
import './../test-config';
import chai, { expect } from 'chai';
import SkrivestotteTracking from './skrivestotte-tracking';

describe('SkrivestotteTracking', () => {
    it('tracker bruk for hver tag som field', () => {
        const tekst = {
            tags: ['ks', 'dagpenger', 'arbeid', 'helse', 'søknad', 'foo']
        };

        const event = SkrivestotteTracking.trackUsage(tekst);

        expect(event.name).to.equal('modiabrukerdialog.skrivestotte.tekstValgt');
        expect(event.fields).to.include.all.keys('ks', 'dagpenger', 'arbeid', 'helse', 'søknad', 'foo');
    });

    it('tracker bruk og legger på tag for tema', () => {
        const tekst = {
            tags: ['ks', 'dagpenger', 'arbeid', 'helse' ]
        };

        const event = SkrivestotteTracking.trackUsage(tekst);

        expect(event.tags).to.include({ 'tema': 'dagpenger' });
    });

    it('tracker bruk og legger på komma-separert tag for tema', () => {
        const tekst = {
            tags: ['ks', 'dagpenger', 'sykepenger' ]
        };

        const event = SkrivestotteTracking.trackUsage(tekst);

        expect(event.tags).to.include({ 'tema': 'dagpenger,sykepenger' });
    });

    it('tracker bruk og legger på komma-separert tag for fagområder', () => {
        const tekst = {
            tags: ['ks', 'arbeid', 'helse']
        };

        const event = SkrivestotteTracking.trackUsage(tekst);

        expect(event.tags).to.include({ 'fagomraade': 'arbeid,helse' });
    });
});

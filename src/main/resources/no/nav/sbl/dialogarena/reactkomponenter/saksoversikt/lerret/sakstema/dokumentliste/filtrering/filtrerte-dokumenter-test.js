/* eslint-env mocha */
import './../../../../../test-config';
import { expect } from 'chai';
import { skalViseDokument } from './filtrerte-dokumenter';

describe('FiltrerDokumenter', () => {
    const utgaaendeDokument = {
        avsender: 'NAV',
        retning: 'UT'
    };

    const inngaaaendeDokumentSluttbruker = {
        avsender: 'SLUTTBRUKER',
        retning: 'INN'
    };

    const inngaaendeDokumentAndre = {
        avsender: 'ANDRE',
        retning: 'INN'
    };

    const internDokument = {
        avsender: 'NAV',
        retning: 'INTERN'
    };

    it('Skal vise alle dokumenter naar alle filtreringsvalg er valgt', () => {
        const filtreringsvalg = {
            NAV: true,
            BRUKER: true,
            ANDRE: true
        };

        const skalViseUtgaaendeDokument = skalViseDokument(utgaaendeDokument, filtreringsvalg);
        expect(skalViseUtgaaendeDokument).to.equal(true);

        const skalViseInngaaendeDokumentSluttbruker = skalViseDokument(inngaaaendeDokumentSluttbruker, filtreringsvalg);
        expect(skalViseInngaaendeDokumentSluttbruker).to.equal(true);

        const skalViseInngaaendeDokumentAndre = skalViseDokument(inngaaendeDokumentAndre, filtreringsvalg);
        expect(skalViseInngaaendeDokumentAndre).to.equal(true);

        const skalViseinternDokument = skalViseDokument(internDokument, filtreringsvalg);
        expect(skalViseinternDokument).to.equal(true);
    });

    it('Skal vise ingen dokumenter naar ingen filtreringsvalg er valgt', () => {
        const filtreringsvalg = {
            NAV: false,
            BRUKER: false,
            ANDRE: false
        };

        const skalViseUtgaaendeDokument = skalViseDokument(utgaaendeDokument, filtreringsvalg);
        expect(skalViseUtgaaendeDokument).to.equal(false);

        const skalViseInngaaendeDokumentSluttbruker = skalViseDokument(inngaaaendeDokumentSluttbruker, filtreringsvalg);
        expect(skalViseInngaaendeDokumentSluttbruker).to.equal(false);

        const skalViseInngaaendeDokumentAndre = skalViseDokument(inngaaendeDokumentAndre, filtreringsvalg);
        expect(skalViseInngaaendeDokumentAndre).to.equal(false);

        const skalViseinternDokument = skalViseDokument(internDokument, filtreringsvalg);
        expect(skalViseinternDokument).to.equal(false);
    });

    it('Skal vise alle dokumenter fra NAV naar filtreringsvalget NAV er valgt', () => {
        const filtreringsvalg = {
            NAV: true,
            BRUKER: false,
            ANDRE: false
        };

        const skalViseUtgaaendeDokument = skalViseDokument(utgaaendeDokument, filtreringsvalg);
        expect(skalViseUtgaaendeDokument).to.equal(true);

        const skalViseInngaaendeDokumentSluttbruker = skalViseDokument(inngaaaendeDokumentSluttbruker, filtreringsvalg);
        expect(skalViseInngaaendeDokumentSluttbruker).to.equal(false);

        const skalViseInngaaendeDokumentAndre = skalViseDokument(inngaaendeDokumentAndre, filtreringsvalg);
        expect(skalViseInngaaendeDokumentAndre).to.equal(false);

        const skalViseinternDokument = skalViseDokument(internDokument, filtreringsvalg);
        expect(skalViseinternDokument).to.equal(true);
    });
});

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
        expect(skalViseUtgaaendeDokument).to.be.true;

        const skalViseInngaaendeDokumentSluttbruker = skalViseDokument(inngaaaendeDokumentSluttbruker, filtreringsvalg);
        expect(skalViseInngaaendeDokumentSluttbruker).to.be.true;

        const skalViseInngaaendeDokumentAndre = skalViseDokument(inngaaendeDokumentAndre, filtreringsvalg);
        expect(skalViseInngaaendeDokumentAndre).to.be.true;

        const skalViseinternDokument = skalViseDokument(internDokument, filtreringsvalg);
        expect(skalViseinternDokument).to.be.true;
    });

    it('Skal vise ingen dokumenter naar ingen filtreringsvalg er valgt', () => {
        const filtreringsvalg = {
            NAV: false,
            BRUKER: false,
            ANDRE: false
        };

        const skalViseUtgaaendeDokument = skalViseDokument(utgaaendeDokument, filtreringsvalg);
        expect(skalViseUtgaaendeDokument).to.be.false;

        const skalViseInngaaendeDokumentSluttbruker = skalViseDokument(inngaaaendeDokumentSluttbruker, filtreringsvalg);
        expect(skalViseInngaaendeDokumentSluttbruker).to.be.false;

        const skalViseInngaaendeDokumentAndre = skalViseDokument(inngaaendeDokumentAndre, filtreringsvalg);
        expect(skalViseInngaaendeDokumentAndre).to.be.false;

        const skalViseinternDokument = skalViseDokument(internDokument, filtreringsvalg);
        expect(skalViseinternDokument).to.be.false;
    });

    it('Skal vise alle dokumenter fra NAV naar filtreringsvalget NAV er valgt', () => {
        const filtreringsvalg = {
            NAV: true,
            BRUKER: false,
            ANDRE: false
        };

        const skalViseUtgaaendeDokument = skalViseDokument(utgaaendeDokument, filtreringsvalg);
        expect(skalViseUtgaaendeDokument).to.be.true;

        const skalViseInngaaendeDokumentSluttbruker = skalViseDokument(inngaaaendeDokumentSluttbruker, filtreringsvalg);
        expect(skalViseInngaaendeDokumentSluttbruker).to.be.false;

        const skalViseInngaaendeDokumentAndre = skalViseDokument(inngaaendeDokumentAndre, filtreringsvalg);
        expect(skalViseInngaaendeDokumentAndre).to.be.false;

        const skalViseinternDokument = skalViseDokument(internDokument, filtreringsvalg);
        expect(skalViseinternDokument).to.be.true;
    });
});

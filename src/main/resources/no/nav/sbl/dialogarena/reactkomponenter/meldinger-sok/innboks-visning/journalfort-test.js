/* eslint-env mocha */
/* eslint no-unused-expressions:0 new-cap:0*/
import '../../test-config';
import React from 'react';
import chai, { expect } from 'chai';
import Journalfort from './journalfort';
import sinonChai from 'sinon-chai';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass } from 'react-addons-test-utils';

chai.use(sinonChai);

describe('Journalfort', () => {
    it('Returnerer noscript om temagruppeNavn ikke er satt', () => {
        const melding = {
            journalfortDatoTekst: '',
            journalfortSaksId: '',
            journalfortAvNavIdent: '',
            journalfortAv: {}
        };
        const element = renderIntoDocument(
            <Journalfort melding={melding} />
        );

        const renderedJournalpostElement = scryRenderedDOMComponentsWithClass(element, 'journalpost-link');
        expect(renderedJournalpostElement.length).to.equal(0);
    });

    it('Returnerer journalfortelement om temagruppeNavn  er satt', () => {
        const melding = {
            journalfortTemanavn: 'temanavn',
            journalfortDatoTekst: '',
            journalfortSaksId: '',
            journalfortAvNavIdent: '',
            journalfortAv: {}
        };
        const element = renderIntoDocument(
            <Journalfort melding={melding} />
        );

        const renderedJournalpostElement = scryRenderedDOMComponentsWithClass(element, 'journalpost-link');
        expect(renderedJournalpostElement.length).to.equal(1);
    });
});

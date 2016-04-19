/* eslint-env mocha */
import './../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import 'intl/locale-data/jsonp/en';
import DokumentVisningListe from './dokument-visning-liste';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass } from 'react-addons-test-utils';

describe('DokumentVisningListe', () => {
    const messages = {
        'dokumentvisning.pdf.aapne.pdf': 'aapne pdf',
        'dokumentvisning.pdf.skriv.ut': 'skriv ut',
        'dokumentvisning.pdf.feilmelding.innhold': 'feilmelding innhold',
        'dokumentvisning.pdf.feilmelding.tittel': 'feilmelding tittel'
    };

    it('Gir tom liste om ingen dokumenter til aa vise', () => {
        const props = {
            dokumenter: []
        };
        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningListe {...props} />
            </IntlProvider>
        );

        const renderedDokumentVisningListe = scryRenderedDOMComponentsWithClass(element, 'dokumentervisning-liste');
        expect(renderedDokumentVisningListe.length).to.equal(1);
        const renderedDokumentliste = scryRenderedDOMComponentsWithClass(element, 'dokumentheader');
        expect(renderedDokumentliste.length).to.equal(0);
    });

    it('Gir dokumentliste om det finnes dokumenter', () => {
        const props = {
            dokumenter: [
                {
                    antallSider: 2,
                    dokumentreferanse: '123',
                    journalpostId: '321',
                    pdfUrl: '2134'
                },
                {
                    antallSider: 2,
                    dokumentreferanse: '456',
                    journalpostId: '654',
                    pdfUrl: '2134'
                }
            ]
        };
        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningListe {...props} />
            </IntlProvider>
        );

        const renderedDokumentVisningListe = scryRenderedDOMComponentsWithClass(element, 'dokumentervisning-liste');
        expect(renderedDokumentVisningListe.length).to.equal(1);
        const renderedDokumentliste = scryRenderedDOMComponentsWithClass(element, 'dokumentheader');
        expect(renderedDokumentliste.length).to.equal(2);
    });
});

/* eslint-env mocha */
import React from 'react';
import { expect } from 'chai';
import DokumentVisning from './dokument-visning';
import { IntlProvider } from 'react-intl';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass } from 'react-addons-test-utils';
import 'intl/locale-data/jsonp/en';

describe('Dokumentvisning', () => {
    const messages = {
        'dokumentvisning.pdf.aapne.pdf': 'aapne pdf',
        'dokumentvisning.pdf.skriv.ut': 'skriv ut',
        'dokumentvisning.pdf.feilmelding.innhold': 'feilmelding innhold',
        'dokumentvisning.pdf.feilmelding.tittel': 'feilmelding tittel'
    };

    it('Gir generell feilmelding om dokumentet ikke kan vises', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            dokument: {
                antallSider: 0,
                dokumentreferanse: '123',
                journalpostId: '321',
                pdfUrl: 'x'
            },
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisning {...props}/>
            </IntlProvider>
        );

        const renderedDokumentVisning = scryRenderedDOMComponentsWithClass(element, 'dokumentheader');
        expect(renderedDokumentVisning.length).to.equal(1);

        const renderedFeilmeldingContainer = scryRenderedDOMComponentsWithClass(element, 'feilmelding-container');
        expect(renderedFeilmeldingContainer.length).to.equal(1);
        expect(renderedFeilmeldingContainer[0].textContent).to.contain('feilmelding tittel');
    });
});

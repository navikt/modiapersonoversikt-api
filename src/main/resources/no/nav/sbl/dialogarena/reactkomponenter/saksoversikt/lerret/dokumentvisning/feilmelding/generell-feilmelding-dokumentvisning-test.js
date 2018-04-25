/* eslint-env mocha */
import './../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import 'intl/locale-data/jsonp/en';
import GenerellFeilmeldingDokumentvisning from './generell-feilmelding-dokumentvisning';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass } from 'react-dom/test-utils';

describe('GenerellFeilmeldingDokumentvisning', () => {
    const messages = {
        'dokumentvisning.pdf.feilmelding.innhold': 'feilmelding innhold',
        'dokumentvisning.pdf.feilmelding.tittel': 'feilmelding tittel'
    };

    it('Gir feilmelding med PDF-link', () => {
        const props = {
            aapneSomPDFLink: <a href="">pdf-link</a>
        };
        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <GenerellFeilmeldingDokumentvisning {...props} />
            </IntlProvider>
        );

        const renderedGenerellFeilmelding = scryRenderedDOMComponentsWithClass(element, 'feilmelding-container');
        expect(renderedGenerellFeilmelding.length).to.equal(1);

        const renderedPDFLink = scryRenderedDOMComponentsWithClass(element, 'feilmelding');
        expect(renderedPDFLink.length).to.equal(1);
        expect(renderedPDFLink[0].textContent).to.contain('pdf-link');
    });
});

/* eslint-env mocha */
import './../../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import DokumentinfoVedlegg from './dokument-info-vedlegg';
import { createRenderer, renderIntoDocument, scryRenderedDOMComponentsWithClass,
    scryRenderedDOMComponentsWithTag } from 'react-addons-test-utils';

describe('DokumentinfoVedlegg', () => {
    const dokumentinfo = {
        vedlegg: [
            { tittel: 'test1' },
            { tittel: 'test2' }
        ]
    };

    function noop() {}

    const props = {
        dokumentinfo,
        visSide: noop,
        velgJournalpost: noop
    };

    it('Gir noscript om vedlegglisten er tom', () => {
        const shallowRenderer = createRenderer();
        const emptyProps = {
            dokumentinfo: { vedlegg: [] },
            visSide: noop,
            velgJournalpost: noop
        };

        shallowRenderer.render(<DokumentinfoVedlegg {...emptyProps} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type).to.equal('noscript');
    });
    it('Gir vedleggliste om det finnes vedlegg', () => {
        const shallowRenderer = createRenderer();

        shallowRenderer.render(<DokumentinfoVedlegg {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type).to.equal('div');
        expect(result.props.className).to.equal('vedleggcontainer');
    });
    it('Gir riktig antall vedlegg i vedlegglista', () => {
        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={{ 'dokumentinfo.vedlegg': 'vedlegg' }}>
                <DokumentinfoVedlegg {...props} />
            </IntlProvider>
        );

        const renderedVedleggContainer = scryRenderedDOMComponentsWithClass(element, 'vedleggcontainer');
        expect(renderedVedleggContainer.length).to.equal(1);

        const antallVedlegg = scryRenderedDOMComponentsWithTag(element, 'li');
        expect(antallVedlegg.length).to.equal(2);

        const renderedVedlegg = scryRenderedDOMComponentsWithClass(element, 'vedlegg-element');
        expect(renderedVedlegg.length).to.equal(2);
    });
});

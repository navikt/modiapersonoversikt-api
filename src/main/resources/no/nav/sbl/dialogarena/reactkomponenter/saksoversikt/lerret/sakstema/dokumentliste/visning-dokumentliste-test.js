/* eslint-env mocha */
import './../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import 'intl/locale-data/jsonp/en';
import VisningDokumentliste from './visning-dokumentliste';
import { fromDateToJSON } from './../../../utils/dato-utils';
import formats from './../../../utils/formater/formats';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass,
    scryRenderedDOMComponentsWithTag } from 'react-addons-test-utils';
import MiljovariablerProvider from './../../../miljovariabler-provider';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

const middlewares = [thunkMiddleware];
const mockStore = configureStore(middlewares);

describe('VisningDokumentliste', () => {
    const miljovariabler = {
        'temasider.viktigavitelenke': 'DAG'
    };
    const messages = {
        'dokumentinfo.sakstema.ingen.dokumenter.bidrag': 'Ingen dokumenter bidrag',
        'dokumentinfo.sakstema.lenke.gosys': 'gosys-lenke',
        'dokumentinfo.sakstema.ingen.dokumenter.header': 'ingen dokumenter header',
        'dokumentliste.filtrering.NAV': 'NAV',
        'dokumentliste.filtrering.BRUKER': 'BRUKER',
        'dokumentliste.filtrering.ANDRE': 'ANDRE',
        'dokumentliste.filtrering.forklaring': 'filtrering forklaring',
        'dokumentinfo.ikke.vise.tidligere.dokumenter.head': 'ikke tidligere dokumenter',
        'dokumentinfo.avsender.fra': 'fra'
    };
    const dokumentinfo = {
        retning: 'INN',
        avsender: 'SLUTTBRUKER',
        mottaker: 'NAV',
        navn: 'Test Testesen',
        hoveddokument: {
            tittel: 'tittel'
        },
        vedlegg: [],
        temakodeVisning: 'Test',
        feilWrapper: {},
        dato: fromDateToJSON(new Date())
    };
    function noop() {}

    it('Gir IngenDokumenterBidrag om temakode er bidrag og ingen dokumenter', () => {
        const props = {
            brukerNavn: 'Test testesen',
            valgtTema: {
                dokumentMetadata: [],
                temakode: 'BID'
            },
            visSide: noop,
            velgJournalpost: noop,
            filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true }
        };
        const element = renderIntoDocument(
            <MiljovariablerProvider miljovariabler={miljovariabler}>
                <IntlProvider locale="en" messages={messages} formats={formats}>
                    <VisningDokumentliste {...props} />
                </IntlProvider>
            </MiljovariablerProvider>
        );

        const renderedVisningDokumentliste = scryRenderedDOMComponentsWithTag(element, 'div');
        expect(renderedVisningDokumentliste.length).to.equal(1);

        expect(renderedVisningDokumentliste[0].textContent).to.contain('gosys-lenke');
    });

    it('Gir IngenDokumenterBidrag om temakode er bidrag og det finnes dokumenter', () => {
        const props = {
            brukerNavn: 'Test testesen',
            valgtTema: {
                dokumentMetadata: [dokumentinfo, dokumentinfo],
                brukerNavn: 'test',
                visTema: false,
                temakode: 'BID'
            },
            visSide: noop,
            velgJournalpost: noop,
            filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true }
        };

        const getState = {};
        const store = mockStore(getState);
        const element = renderIntoDocument(
            <Provider store={store}>
                <MiljovariablerProvider miljovariabler={miljovariabler}>
                    <IntlProvider locale="en" messages={messages} formats={formats}>
                        <VisningDokumentliste {...props} />
                    </IntlProvider>
                </MiljovariablerProvider>
            </Provider>
        );

        const renderedInfoIngenBidrag = scryRenderedDOMComponentsWithClass(element, 'infoingenbidrag');
        expect(renderedInfoIngenBidrag.length).to.equal(1);
        const renderedDokumentliste = scryRenderedDOMComponentsWithClass(element, 'ustilet');
        expect(renderedDokumentliste.length).to.equal(1);
        const renderedDokumentinfoElement = scryRenderedDOMComponentsWithClass(element, 'dokumentliste-element');
        expect(renderedDokumentinfoElement.length).to.equal(2);
    });

    it('Gir ingen dokumenter om det ikke er noen dokumenter og temakode ikke er BID', () => {
        const props = {
            brukerNavn: 'Test testesen',
            valgtTema: {
                dokumentMetadata: [],
                brukerNavn: 'test',
                visTema: false,
                temakode: 'BID'
            },
            visSide: noop,
            velgJournalpost: noop,
            filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true }
        };

        const element = renderIntoDocument(
            <MiljovariablerProvider miljovariabler={miljovariabler}>
                <IntlProvider locale="en" messages={messages} formats={formats}>
                    <VisningDokumentliste {...props} />
                </IntlProvider>
            </MiljovariablerProvider>
        );

        const renderedIngenDokumenterListe = scryRenderedDOMComponentsWithClass(element, 'ingendokumenter');
        expect(renderedIngenDokumenterListe.length).to.equal(1);
        const renderedDokumentinfoElement = scryRenderedDOMComponentsWithClass(element, 'dokumentliste-element');
        expect(renderedDokumentinfoElement.length).to.equal(0);
    });
    it('Gir dokumentliste om det finnes dokumenter', () => {
        const props = {
            brukerNavn: 'Test testesen',
            valgtTema: {
                dokumentMetadata: [dokumentinfo, dokumentinfo, dokumentinfo],
                brukerNavn: 'test',
                visTema: true,
                temakode: 'DAG'
            },
            visSide: noop,
            velgJournalpost: noop,
            filtreringsvalg: { NAV: true, BRUKER: true, ANDRE: true }
        };

        const getState = {};
        const store = mockStore(getState);
        const element = renderIntoDocument(
            <Provider store={store}>
                <MiljovariablerProvider miljovariabler={miljovariabler}>
                    <IntlProvider locale="en" messages={messages} formats={formats}>
                        <VisningDokumentliste {...props} />
                    </IntlProvider>
                </MiljovariablerProvider>
            </Provider>
        );

        const renderedInfoIngenBidrag = scryRenderedDOMComponentsWithClass(element, 'infoingenbidrag');
        expect(renderedInfoIngenBidrag.length).to.equal(0);
        const renderedDokumentliste = scryRenderedDOMComponentsWithClass(element, 'ustilet');
        expect(renderedDokumentliste.length).to.equal(1);
        const renderedDokumentinfoElement = scryRenderedDOMComponentsWithClass(element, 'dokumentliste-element');
        expect(renderedDokumentinfoElement.length).to.equal(3);
    });
});

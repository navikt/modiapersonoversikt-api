import './../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import 'intl/locale-data/jsonp/en';
import SakstemaPage from './sakstema-page';
import { fromDateToJSON } from './../../utils/dato-utils';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass,
    scryRenderedDOMComponentsWithTag } from 'react-addons-test-utils';
import MiljovariablerProvider from './../../miljovariabler-provider';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import thunkMiddleware from 'redux-thunk';

const middlewares = [thunkMiddleware];
const mockStore = configureStore(middlewares);

function noop() {}

const propsMock = {
    sakstema: [],
    valgtTema: {},
    velgSak: noop,
    brukerNavn: 'brukernavn',
    visSide: noop,
    purgeScrollId: noop,
    velgJournalpost: noop,
    filtreringsvalg: {},
    gt: '',
    norgUrl: ''
};

describe('SakstemaPage', () => {
    const miljovariabler = {
        'behandlingsstatus.synlig.antallDager': 28,
        'temasider.viktigavitelenke': 'DAG,AAP'
    };
    const messages = {
        'sakslamell.ingensaker': 'ingen saker',
        'dokumentinfo.sakstema.ingen.dokumenter.header': 'ingen dokumenter header',
        'dokumentinfo.sakstema.ingen.dokumenter.forklaring': 'ingen dokuemnter forklaring',
        'dokumentinfo.sakstema.lenke.gosys': 'lenke gosys'
    };
    it('Gir tom sakstemaside om det ikke finnes saksteamer', () => {

        const getState = {};
        const store = mockStore(getState);

        const element = renderIntoDocument(
            <Provider store={store}>
                <MiljovariablerProvider miljovariabler={miljovariabler}>
                    <IntlProvider locale="en" messages={messages}>
                        <SakstemaPage {...propsMock} />
                    </IntlProvider>
                </MiljovariablerProvider>
            </Provider>
        );

        const renderedSakstemaIngenSakstemaer = scryRenderedDOMComponentsWithClass(element, 'ingen-sakstemaer');
        expect(renderedSakstemaIngenSakstemaer.length).to.equal(1);
        const renderedIngenSakstemaerOversikt = scryRenderedDOMComponentsWithTag(element, 'h1');
        expect(renderedIngenSakstemaerOversikt.length).to.equal(1);
        expect(renderedIngenSakstemaerOversikt[0].textContent).to.contain('ingen saker');
    });

    it('Gir sakstemapage med sakstemaliste om det finnes sakstemaer', () => {
        const props = {
            ...propsMock,
            sakstema: [
                {
                    temakode: 'DAG',
                    temanavn: 'Dagpenger',
                    behandlingskjeder: [{ sistOppdatert: fromDateToJSON(new Date()) }],
                    dokumentMetadata: [],
                    harTilgang: true
                },
                {
                    temakode: 'BID',
                    temanavn: 'Bidrag',
                    behandlingskjeder: [{ sistOppdatert: fromDateToJSON(new Date()) }],
                    dokumentMetadata: [],
                    harTilgang: true
                },
                {
                    temakode: 'AAP',
                    temanavn: 'Arbeidsavklaringspenger',
                    behandlingskjeder: [{ sistOppdatert: fromDateToJSON(new Date()) }],
                    dokumentMetadata: [],
                    harTilgang: true
                }
            ],
            valgtTema: {
                temakode: 'DAG',
                dokumentMetadata: []
            }
        };

        const getState = { lerret: { temakodeliste: []  }};
        const store = mockStore(getState);

        const element = renderIntoDocument(
            <Provider store={store}>
                <MiljovariablerProvider miljovariabler={miljovariabler}>
                    <IntlProvider locale="en" messages={messages}>
                        <SakstemaPage {...props} />
                    </IntlProvider>
                </MiljovariablerProvider>
            </Provider>
        );

        const renderedSakstemaIngenSakstemaer = scryRenderedDOMComponentsWithClass(element, 'ingen-sakstemaer');
        expect(renderedSakstemaIngenSakstemaer.length).to.equal(0);
        const renderedIngenSakstemaerOversikt = scryRenderedDOMComponentsWithClass(element, 'saksoversikt-liste');
        expect(renderedIngenSakstemaerOversikt.length).to.equal(1);
    });
});

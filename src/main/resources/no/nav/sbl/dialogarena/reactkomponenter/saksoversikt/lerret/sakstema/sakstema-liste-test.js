/* eslint-env mocha */
import './../../../test-config';
import React from 'react';
import { expect } from 'chai';
import { IntlProvider } from 'react-intl';
import 'intl/locale-data/jsonp/en';
import SakstemaListe from './sakstema-liste';
import { fromDateToJSON } from './../../utils/dato-utils';
import formats from './../../utils/formater/formats';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass } from 'react-addons-test-utils';
import MiljovariablerProvider from './../../miljovariabler-provider';

describe('SakstemaListe', () => {
    const miljovariabler = {
        'behandlingsstatus.synlig.antallDager': 28
    };
    const messages = {};
    function noop() {}
    it('Gir tom sakstemaliste naar det ikke er noen saksteamer', () => {
        const props = {
            sakstema: [],
            valgtTema: {},
            velgSak: noop
        };

        const element = renderIntoDocument(
            <MiljovariablerProvider miljovariabler={miljovariabler}>
                <IntlProvider locale="en" messages={messages}>
                    <SakstemaListe {...props} />
                </IntlProvider>
            </MiljovariablerProvider>
        );

        const renderedSakstemaliste = scryRenderedDOMComponentsWithClass(element, 'sakstemaliste');
        expect(renderedSakstemaliste.length).to.equal(1);

        const renderedSakstemaListeElement = scryRenderedDOMComponentsWithClass(element, 'saksoversikt-liste-element ');
        expect(renderedSakstemaListeElement.length).to.equal(0);
    });

    it('Gir sakstemaliste naar det finnes saksteamer', () => {
        const props = {
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
            valgtTema: { temakode: 'DAG' },
            velgSak: noop
        };

        const element = renderIntoDocument(
            <MiljovariablerProvider miljovariabler={miljovariabler}>
                <IntlProvider locale="en" messages={messages} formats={formats}>
                    <SakstemaListe {...props} />
                </IntlProvider>
            </MiljovariablerProvider>
        );

        const renderedSakstemaliste = scryRenderedDOMComponentsWithClass(element, 'sakstemaliste');
        expect(renderedSakstemaliste.length).to.equal(1);

        const renderedSakstemaListeElement = scryRenderedDOMComponentsWithClass(element, 'saksoversikt-liste-element ');
        expect(renderedSakstemaListeElement.length).to.equal(3);
    });
});

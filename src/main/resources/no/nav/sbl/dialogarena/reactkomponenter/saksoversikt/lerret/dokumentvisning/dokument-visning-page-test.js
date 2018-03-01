/* eslint-env mocha */
import './../../../test-config';
import React from 'react';
import { expect } from 'chai';
import sinon from 'sinon';
import { IntlProvider } from 'react-intl';
import 'intl/locale-data/jsonp/en';
import { DokumentVisningPage } from './dokument-visning-page';
import { renderIntoDocument, scryRenderedDOMComponentsWithClass,
    scryRenderedDOMComponentsWithTag } from 'react-addons-test-utils';
import * as Const from './../../konstanter';
import { fromDateToJSON } from './../../utils/dato-utils';

describe('DokumentVisningPage', () => {
    const messages = {
        'dokumentvisning.pdf.aapne.pdf': 'aapne pdf',
        'dokumentvisning.pdf.skriv.ut': 'skriv ut',
        'dokumentvisning.pdf.feilmelding.innhold': 'feilmelding innhold',
        'dokumentvisning.pdf.feilmelding.tittel': 'feilmelding tittel',
        'dokumentinfo.forvaltningsnotat': 'forvaltningsnotat',
        'dokumentinfo.internnotat': 'internnotat',
        'dokumentvisning.retningsstatus': 'retningsstatus: {retning} {navn} {typeIntern} {dato}'
    };

    function noop() {}
    function hentDokumentData() {}
    const fnr = '12346578910';

    it('Gir feilmelding om dokumentstatus feilet', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                vedlegg: []
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.INIT_OK,
            dokumentstatus: Const.FEILET,
            journalpostmetadata: {},
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(1);
        const renderedGenerellFeilmelding = scryRenderedDOMComponentsWithClass(element, 'feilmelding-container');
        expect(renderedGenerellFeilmelding.length).to.equal(1);
        const renderedFeilmelding = scryRenderedDOMComponentsWithClass(element, 'feilmelding');
        expect(renderedFeilmelding.length).to.equal(1);
    });

    it('Gir snurrepipp om lerretstatus ikke er ferdig lastet', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                vedlegg: []
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.INIT_OK,
            dokumentstatus: Const.LASTET,
            journalpostmetadata: {},
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(1);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(0);
    });

    it('Gir snurrepipp om dokumentstatus ikke er ferdig lastet', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                vedlegg: []
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.LASTET,
            dokumentstatus: Const.LASTER,
            journalpostmetadata: {},
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(1);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(0);
    });

    it('Gir visningspage uten dokument hvis alt er ferdig lastet og ingen dokumenter', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();
        const intlStub = sinon.stub(intl, 'formatMessage');

        const props = {
            fnr,
            valgtJournalpost: {
                vedlegg: [],
                dato: fromDateToJSON(new Date())
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.LASTET,
            dokumentstatus: Const.LASTET,
            journalpostmetadata: {
                dokumenter: [],
                feilendeDokumenter: []
            },
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(0);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(1);
        const renderedDokumentElement = scryRenderedDOMComponentsWithClass(element, 'dokumentheader');
        expect(renderedDokumentElement.length).to.equal(0);

        intlStub.restore();
    });

    it('Gir visningspage med dokument hvis alt er ferdig lastet og ett dokumenter', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                vedlegg: [],
                dato: fromDateToJSON(new Date())
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.LASTET,
            dokumentstatus: Const.LASTET,
            journalpostmetadata: {
                dokumenter: [
                    {
                        dokumentreferanse: '123',
                        tittel: 'tittel'
                    }
                ],
                feilendeDokumenter: []
            },
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(0);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(1);
        const renderedDokumentElement = scryRenderedDOMComponentsWithClass(element, 'dokumentheader');
        expect(renderedDokumentElement.length).to.equal(1);
    });

    it('Gir forvaltningsnotat om valgtjournalpost er et forvaltningsnotat', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                journalpostId: '123',
                temakode: 'DAG',
                dato: fromDateToJSON(new Date()),
                retning: 'INTERN',
                kategoriNotat: 'FORVALTNINGSNOTAT',
                vedlegg: []
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.LASTET,
            dokumentstatus: Const.LASTET,
            journalpostmetadata: {
                dokumenter: [],
                feilendeDokumenter: []
            },
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(0);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(1);
        const renderedForvaltningsnotat = scryRenderedDOMComponentsWithTag(element, 'h1');
        expect(renderedForvaltningsnotat.length).to.equal(1);
        expect(renderedForvaltningsnotat[0].textContent).to.contain('forvaltningsnotat');
    });

    it('Gir internnotat om valgtjournalpost er et internnotat', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                journalpostId: '123',
                temakode: 'DAG',
                dato: fromDateToJSON(new Date()),
                retning: 'INTERN',
                kategoriNotat: 'INTERNNOTAT',
                vedlegg: []
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.LASTET,
            dokumentstatus: Const.LASTET,
            journalpostmetadata: {
                dokumenter: [],
                feilendeDokumenter: []
            },
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(0);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(1);
        const renderedForvaltningsnotat = scryRenderedDOMComponentsWithTag(element, 'h1');
        expect(renderedForvaltningsnotat.length).to.equal(1);
        expect(renderedForvaltningsnotat[0].textContent).to.contain('internnotat');
    });

    it('Gir ingen notattype om valgtjournalposts retning ikke er intern', () => {
        const intlProvider = new IntlProvider({ locale: 'en', messages }, {});
        const { intl } = intlProvider.getChildContext();

        const props = {
            fnr,
            valgtJournalpost: {
                journalpostId: '123',
                temakode: 'DAG',
                dato: fromDateToJSON(new Date()),
                retning: 'INN',
                vedlegg: []
            },
            hentDokumentData,
            visSide: noop,
            lerretstatus: Const.LASTET,
            dokumentstatus: Const.LASTET,
            journalpostmetadata: {
                dokumenter: [],
                feilendeDokumenter: []
            },
            intl
        };

        const element = renderIntoDocument(
            <IntlProvider locale="en" messages={messages}>
                <DokumentVisningPage {...props} />
            </IntlProvider>
        );

        const renderedSnurrepipp = scryRenderedDOMComponentsWithClass(element, 'snurrepipp');
        expect(renderedSnurrepipp.length).to.equal(0);
        const renderedDokumentVisningPage = scryRenderedDOMComponentsWithClass(element, 'dokument-visning-page');
        expect(renderedDokumentVisningPage.length).to.equal(1);
        const renderedForvaltningsnotat = scryRenderedDOMComponentsWithTag(element, 'h1');
        expect(renderedForvaltningsnotat.length).to.equal(1);
        expect(renderedForvaltningsnotat[0].textContent).to.not.contain('forvaltningsnotat');
        expect(renderedForvaltningsnotat[0].textContent).to.not.contain('internnotat');
    });
});

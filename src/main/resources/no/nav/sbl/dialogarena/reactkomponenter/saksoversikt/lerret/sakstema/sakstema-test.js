/* eslint-env mocha */
import './../../../test-config';
import React from 'react';
import { expect } from 'chai';
import 'intl/locale-data/jsonp/en';
import Sakstema from './sakstema';
import { fromDateToJSON } from './../../utils/dato-utils';
import { createRenderer } from 'react-addons-test-utils';

describe('Sakstema', () => {
    function noop() {}
    it('Viser sakstema med classname "valgt" om temakoden er lik valgtTema', () => {
        const props = {
            tema: {
                temakode: 'DAG',
                temanavn: 'Dagpenger',
                behandlingskjeder: [{ sistOppdatert: fromDateToJSON(new Date()) }],
                dokumentMetadata: [],
                harTilgang: true
            },
            valgtTema: { temakode: 'DAG' },
            nokkelinfo: {},
            velgSak: noop
        };

        const shallowRenderer = createRenderer();

        shallowRenderer.render(<Sakstema {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type).to.equal('div');
        expect(result.props.className).to.contain('valgt');
    });

    it('Viser sakstema uten classname "valgt" om temakoden er ulik valgtTema', () => {
        const props = {
            tema: {
                temakode: 'DAG',
                temanavn: 'Dagpenger',
                behandlingskjeder: [{ sistOppdatert: fromDateToJSON(new Date()) }],
                dokumentMetadata: [],
                harTilgang: true
            },
            valgtTema: { temakode: 'BID' },
            nokkelinfo: {},
            velgSak: noop
        };

        const shallowRenderer = createRenderer();

        shallowRenderer.render(<Sakstema {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type).to.equal('div');
        expect(result.props.className).to.not.contain('valgt');
    });

    it('Viser sakstema med classname "ikke tilgang" om ikke tilgang til sakstema', () => {
        const props = {
            tema: {
                temakode: 'DAG',
                temanavn: 'Dagpenger',
                behandlingskjeder: [{ sistOppdatert: fromDateToJSON(new Date()) }],
                dokumentMetadata: [],
                harTilgang: false
            },
            valgtTema: { temakode: 'BID' },
            nokkelinfo: {},
            velgSak: noop
        };

        const shallowRenderer = createRenderer();

        shallowRenderer.render(<Sakstema {...props} />);
        const result = shallowRenderer.getRenderOutput();
        expect(result.type).to.equal('div');
        expect(result.props.className).to.contain('tema-ikke-tilgang');
    });
});

/* eslint-env mocha */
import './../../../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import DokumentAvsender from './dokument-avsender';
import { createRenderer } from 'react-addons-test-utils';

describe('DokumentAvsender', () => {
    it('Viser inngaaende hvis retningen er inn', () => {
        const shallowRenderer = createRenderer();

        const props = {
            retning: 'INN',
            avsender: 'SLUTTBRUKER',
            navn: 'Test testesen'
        };

        shallowRenderer.render(<DokumentAvsender {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type.name).to.equal('Inngaaende');
    });

    it('Viser utgaaende hvis retningen er ut', () => {
        const shallowRenderer = createRenderer();

        const props = {
            retning: 'UT',
            avsender: 'NAV',
            navn: 'NAV'
        };

        shallowRenderer.render(<DokumentAvsender {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type.name).to.equal('Utgaaende');
    });

    it('Viser intern hvis retningen er intern', () => {
        const shallowRenderer = createRenderer();

        const props = {
            retning: 'INTERN',
            avsender: 'NAV',
            navn: null
        };

        shallowRenderer.render(<DokumentAvsender {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type.name).to.equal('Intern');
    });

    it('Viser ukjent hvis retningen er udefinert', () => {
        const shallowRenderer = createRenderer();

        const props = {
            retning: 'NOE_ANNET',
            avsender: 'NAV',
            navn: null
        };

        shallowRenderer.render(<DokumentAvsender {...props} />);
        const result = shallowRenderer.getRenderOutput();

        expect(result.type).to.equal('span');
    });
});

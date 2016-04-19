/* eslint-env mocha */
import React from 'react';
import { expect } from 'chai';
import KulemenyListe from './kulemeny-liste';
import { createRenderer } from 'react-addons-test-utils';

describe('KulemenyListe', () => {
    it('Gir noscript om listen er mindre enn 2', () => {
        const shallowRenderer = createRenderer();
        const dokumentmetadata = [];

        shallowRenderer.render(<KulemenyListe dokumentmetadata={dokumentmetadata} />);
        const result = shallowRenderer.getRenderOutput();
        expect(result.type).to.equal('noscript');
    });

    it('Gir liste hvis dokumentmetadata har to eller flere objekter', () => {
        const shallowRenderer = createRenderer();
        const dokumentmetadataObjekt = {
            dokumentreferanse: '123',
            tittel: 'tittel'
        };
        const dokumentmetadata = [dokumentmetadataObjekt, dokumentmetadataObjekt];

        shallowRenderer.render(<KulemenyListe dokumentmetadata={dokumentmetadata} />);
        const result = shallowRenderer.getRenderOutput();
        expect(result.type).to.equal('ul');
        expect(result.props.className).to.equal('kulemeny');
    });
});

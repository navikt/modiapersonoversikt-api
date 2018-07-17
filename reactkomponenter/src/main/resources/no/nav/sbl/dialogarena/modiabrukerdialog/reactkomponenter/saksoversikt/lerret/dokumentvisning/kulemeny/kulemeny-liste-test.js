import './../../../../test-config';
import React from 'react';
import { expect } from 'chai';
import KulemenyListe from './kulemeny-liste';
import { shallow } from 'enzyme';

describe('KulemenyListe', () => {
    it('Gir noscript om listen er mindre enn 2', () => {
        const dokumentmetadata = [];

        const result = shallow(<KulemenyListe dokumentmetadata={dokumentmetadata} />);

        expect(result.is('noscript')).to.be.true;
    });

    it('Gir liste hvis dokumentmetadata har to eller flere objekter', () => {
        const dokumentmetadataObjekt = {
            dokumentreferanse: '123',
            tittel: 'tittel'
        };
        const dokumentmetadata = [dokumentmetadataObjekt, dokumentmetadataObjekt];

        const result = shallow(<KulemenyListe dokumentmetadata={dokumentmetadata} />);
        expect(result.is('ul')).to.be.true;
        expect(result.props().className).to.equal('kulemeny');
    });
});

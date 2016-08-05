/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import MeldingerSokModule from './meldinger-sok-module';

describe('Medinger Sok Module', () => {
    it('skal rendre en tom spinner inni en modal', () => {
        const element = shallow(<MeldingerSokModule />);

        element.setState({
            traader: []
        });

        expect(element.is('Modal')).to.be.true;
        expect(element.find('.sok-visning').find('img').length).to.equal(1);
    });

    it('skal rendre en feilmelding når state er feilet', () => {
        const element = shallow(<MeldingerSokModule />);

        element.setState({
            traader: [],
            feilet: true
        });

        expect(element.find('.tom').text()).to.equal('Noe feilet');
    });

    it('feilmelding/status skjult når det finnes traader', () => {
        const element = shallow(<MeldingerSokModule />);

        element.setState({
            traader: [{}]
        });

        expect(element.find('.sok-visning.hidden').find('.tom').length).to.equal(1);
    });

    it('skal rendre tekstlinjekomponenter i en scrollportal', () => {
        const element = shallow(<MeldingerSokModule />);

        element.setState({
            traader: [{
                traadId: 1
            }, {
                traadId: 1
            }]
        });

        expect(element.find('ScrollPortal').length).to.equal(1);
        expect(element.find('ScrollPortal').find('Listevisning').length).to.equal(2);
    });
});

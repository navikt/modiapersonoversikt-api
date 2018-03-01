/* eslint no-unused-expressions:0 */
import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import MeldingerSokModal from './meldinger-sok-modal';

describe('Meldinger Sok Modal', () => {
    it('Skal rendre en modal', () => {
        const element = shallow(<MeldingerSokModal moduleName="Test" />);

        expect(element.is('Modal')).to.be.true;
    });
    it('Skal generere fornuftige modal-props', () => {
        const element = shallow(<MeldingerSokModal moduleName="Test" />);

        const props = element.props();

        expect(props.title.text).to.equal("Test modal");
        expect(props.closeButton.text).to.equal("Lukk test modal");
    });
});

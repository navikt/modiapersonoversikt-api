import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import Traadvisning from './traadvisning';
import { TraadMock } from '../utils/traad-utils';

describe('TraadvisningKomponent', () => {
    it('Skal rendre meldinger', () => {
        const element = shallow(<Traadvisning traad={TraadMock} />);

        expect(element.find('Melding').length).to.equal(1);
    });
});

//TODO bedre tester her
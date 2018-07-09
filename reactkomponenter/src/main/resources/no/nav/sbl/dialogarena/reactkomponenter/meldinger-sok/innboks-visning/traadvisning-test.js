import '../../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';

import Traadvisning from './traadvisning';
import { TraadMock } from '../../utils/traad-utils';

const mockProps = {
    traadBegrep: {
        bestemtEntall: ''
    }
};

describe('TraadvisningKomponent', () => {
    it('Skal rendre meldinger', () => {
        const element = shallow(<Traadvisning {...mockProps} traad={TraadMock} />);

        expect(element.find('Melding').length).to.equal(1);
    });
});

//TODO bedre tester her
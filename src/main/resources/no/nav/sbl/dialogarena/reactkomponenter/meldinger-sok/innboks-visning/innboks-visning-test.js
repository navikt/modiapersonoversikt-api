import '../../test-config';
import React from 'react';
import { shallow, mount } from 'enzyme';
import { expect } from 'chai';

import { TraadMock } from '../../utils/traad-utils';
import InnboksVisning from './innboks-visning';

const innboksVisningPropsMock = {
    traader: [TraadMock],
    nyTraadValgtCallback: () => {},
    valgtTraad: TraadMock,
    listePanelId: '',
    traadvisningsPanelId: '',
    submitButtonProps: {
        buttonText: '',
        errorMessage: '',
        error: false
    }
};

describe('InnboksVisning', () => {
    it('Skal rendre liste-elementer i en scrollportal', () => {
        const element = mount(<InnboksVisning
            {...innboksVisningPropsMock}
            traader={[TraadMock, { ...TraadMock, traadId: '02' }]}
        />);

        expect(element.find('ScrollPortal.sok-liste').length).to.equal(1);
        expect(element.find('.sok-element').length).to.equal(2);
    });
});

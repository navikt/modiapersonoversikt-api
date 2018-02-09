import '../test-config';
import React from 'react';
import { shallow, mount } from 'enzyme';
import { expect } from 'chai';

import { TraadMock } from '../utils/traad-utils';
import InnboksVisning from './innboks-visning';

const innboksVisningPropsMock = {
    traader: [TraadMock],
    nyTraadValgtCallback: () => {},
    valgtTraad: TraadMock,
    listePanelId: '',
    forhandsvisningsPanelId: '',
    submitButtonProps: {
        buttonText: '',
        errorMessage: '',
        error: false
    }
};

describe('InnboksVisning', () => {
    it('skal rendre submit-knapp med riktig tekst', () => {
        const element = shallow(<InnboksVisning
            {...innboksVisningPropsMock}
            submitButtonProps={{
                buttonText: 'Dette er en knapp',
                errorMessage: '',
                error: false
            }}
        />);

        expect(element.find('.velgPanel input').props().value).to.equal('Dette er en knapp');
    });
    it('skal rendre feilmelding på submitError', () => {
        const element = shallow(<InnboksVisning
            {...innboksVisningPropsMock}
            submitButtonProps={{
                buttonText: '',
                errorMessage: 'Det skjedde en feil',
                error: true
            }}
        />);

        expect(element.find('.velgPanel p').text()).to.equal("Det skjedde en feil");
    });
    it('Skal rendre tekstlinjekomponenter i en scrollportal', () => {
        const element = mount(<InnboksVisning
            {...innboksVisningPropsMock}
            traader={[TraadMock, { ...TraadMock, traadId: '02' }]}
        />);

        expect(element.find('ScrollPortal.sok-liste').length).to.equal(1);
        expect(element.find('.sok-element').length).to.equal(2);
    });
});

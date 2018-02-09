import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import { TraadMock } from '../utils/traad-utils';

import MeldingerSokView from "./meldinger-sok-view";

const PropsMock = {
    feilet: false,
    initialisert: false,
    store: {
        traadChanged: () => {}
    },
    onChangeProxy: () => {},
    keyDownHandler: () => {},
    onSubmit: () => {}
};

const StateMock = {
    traader: [],
    valgtTraad: {},
    listePanelId: '',
    forhandsvisningsPanelId: '',
    visCheckbox: false,
    submitButtonProps: {
        buttonText: '',
        errorMessage: '',
        error: false
    },
    visSok: false
};

describe('Medinger Sok Module View', () => {
    it('Skal rendre en tom spinner', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                traader: []
            }}
        />);

        expect(element.find('.sok-visning').find('img').length).to.equal(1);
    });

    it('Skal rendre en feilmelding når state er feilet', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            feilet={true}
            state={{
                ...StateMock
            }}
        />);

        expect(element.find('.tom').text()).to.equal('Noe feilet');
    });

    it('Feilmelding/status skjult når det finnes traader', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                traader: [TraadMock]
            }}
        />);

        expect(element.find('.sok-visning.hidden').find('.tom').length).to.equal(1);
    });
});

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
    traadvisningsPanelId: '',
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

        expect(element.find('NavFrontendSpinner').length).to.equal(1);
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

        expect(element.find('.tom-visning.hidden').length).to.equal(1);
    });

    it('skal rendre submit-knapp med riktig tekst', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                submitButtonProps: {
                    buttonText: 'Dette er en knapp',
                    errorMessage: '',
                    error: false
                }

            }}
        />);

        expect(element.find('.velgPanel input').props().value).to.equal('Dette er en knapp');
    });

    it('skal rendre feilmelding på submitError', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                submitButtonProps: {
                    buttonText: '',
                    errorMessage: 'Det skjedde en feil',
                    error: true
                }
            }}
        />);

        expect(element.find('.velgPanel p').text()).to.equal("Det skjedde en feil");
    });
});

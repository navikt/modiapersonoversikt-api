import './../test-config';
import React from 'react';
import { shallow } from 'enzyme';
import { expect } from 'chai';
import { TraadMock } from '../utils/traad-utils';

import MeldingerSokView from "./meldinger-sok-view";

const PropsMock = {
    store: {},
    onChangeProxy: () => {},
    keyDownHandler: () => {},
    onSubmit: () => {}
};

const StateMock = {
    valgtTraad: {},
    visCheckbox: false,
    submitButtonValue: '',
    traader: {}
};

describe('Medinger Sok Module View', () => {

    it('Skal rendre en tom spinner', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                traader: [TraadMock]
            }}
        />);

        expect(element.find('.sok-visning').find('img').length).to.equal(1);
    });

    it('Skal rendre en feilmelding når state er feilet', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                traader: [],
                feilet: true
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

    it('Skal rendre tekstlinjekomponenter i en scrollportal', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                traader: [TraadMock, TraadMock]
            }}
        />);

        expect(element.find('ScrollPortal').length).to.equal(1);
        expect(element.find('ScrollPortal').find('Listevisning').length).to.equal(2);
    });

    it('Skal rendre en ForhåndvisningsKomponent med fornuftige props', () => {
        const element = shallow(<MeldingerSokView
            {...PropsMock}
            state={{
                ...StateMock,
                traader: [TraadMock],
                submitButtonValue: 'Knapp',
                submitError: true,
                submitErrorMessage: 'Det skjedde en feil'
            }}
        />);

        const props = element.find('Forhandsvisning').props();

        expect(props.submitErrorMessage).to.equal('Det skjedde en feil');
        expect(props.submitError).to.equal(true);
        expect(props.submitButtonValue).to.equal('Knapp');
    });
});

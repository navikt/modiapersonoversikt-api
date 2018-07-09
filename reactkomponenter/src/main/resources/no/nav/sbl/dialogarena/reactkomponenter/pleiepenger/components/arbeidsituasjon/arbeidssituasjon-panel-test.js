import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import '../../../test-config';
import Arbeidssituasjon, { sorterArbeidsforhold } from './arbeidssituasjon-panel';

const mockArbeidsforhold = refusjonAr => (
    {
        arbeidsgiversNavn: 'Test Testesen',
        arbeidsgiverKontonr: '1231231',
        inntektsperiode: 'Inntektsperiode',
        refusjonstype: 'refusjonstype',
        refusjonTom: {
            year: refusjonAr,
            monthValue: 5,
            dayOfMonth: 8
        },
        inntektForPerioden: 504,
        arbeidskategori: ''
    }
);

const mockText = {
    arbeidssituasjon: '',
    arbeidskategori: '',
    arbeidsgiver: '',
    kontonummer: '',
    inntektsperiode: '',
    inntektForPerioden: '',
    refusjonstype: '',
    refusjonTilDato: ''
};

function toggleArbeidsforholdVisningElement(element) {
    element.find('a').simulate('click');
}

describe('Arbeidssituasjon', () => {
    it('viser en arbeidssituasjon', () => {
        const element = shallow(
            <Arbeidssituasjon tekst={mockText} arbeidsforhold={[mockArbeidsforhold(2006), mockArbeidsforhold(2007)]} />);
        expect(element.find('ArbeidsforholdKomponent').length).to.equal(1);
    });
    it('viser alle arbeidsforhold ved klikk på link', () => {
        const element = shallow(
            <Arbeidssituasjon tekst={mockText} arbeidsforhold={[mockArbeidsforhold(2006), mockArbeidsforhold(2007)]} />);
        toggleArbeidsforholdVisningElement(element);
        expect(element.find('ArbeidsforholdKomponent').length).to.equal(2);
    });
    it('viser kun et arbeidsforhold ved trykk på link to ganger', () => {
        const element = shallow(
            <Arbeidssituasjon tekst={mockText} arbeidsforhold={[mockArbeidsforhold(2006), mockArbeidsforhold(2007)]} />);
        toggleArbeidsforholdVisningElement(element);
        toggleArbeidsforholdVisningElement(element);
        expect(element.find('ArbeidsforholdKomponent').length).to.equal(1);
    });
    it('viser ingen arbeidsforhold ved tom liste', () => {
        const element = shallow(
            <Arbeidssituasjon tekst={mockText} arbeidsforhold={[]} />);
        expect(element.find('ArbeidsforholdKomponent').length).to.equal(0);
    });
    describe('sorterArbeidsforhold', () => {
        it('sorterer arbeidsforhold med siste refusjonsdato først når nyest er sist i listen', () => {
            const arbeidsforhold = sorterArbeidsforhold([mockArbeidsforhold(2016), mockArbeidsforhold(2017)]);
            expect(arbeidsforhold[0].refusjonTom.year).to.equal(2017);
        });
        it('sorterer arbeidsforhold med siste refusjonsdato først når nyest er først i listen', () => {
            const arbeidsforhold = sorterArbeidsforhold([mockArbeidsforhold(2017), mockArbeidsforhold(2016)]);
            expect(arbeidsforhold[0].refusjonTom.year).to.equal(2017);
        });
    });
});

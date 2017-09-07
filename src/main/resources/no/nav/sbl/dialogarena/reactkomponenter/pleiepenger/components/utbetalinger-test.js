import React from 'react';
import { expect } from 'chai';
import { shallow } from 'enzyme';

import './../../test-config';
import PleiepengerUtbetalingerPanel, { sorterEtterIdDato } from './utbetalinger';

const mockPeriode = (fraOgMedAr) => ({
    vedtakListe: [
        {
            periode: {
                fraOgMed: {
                    year: 2017,
                    monthValue: 5,
                    dayOfMonth: 12
                },
                tilOgMed: {
                    year: 2017,
                    monthValue: 5,
                    dayOfMonth: 12
                }
            },
            anvistUtbetaling: {
                year: 2017,
                monthValue: 5,
                dayOfMonth: 12
            },
            bruttoBelop: 500,
            dagsats: 10
        }
    ],
    fraOgMed: {
        year: fraOgMedAr,
        monthValue: 5,
        dayOfMonth: 12
    }
});

const mockPerioder = [
    mockPeriode(2015), mockPeriode(2016)
];

describe('Utbetalinger', () => {
    it('lager en liste med periode like lang som antall perioder', () => {
        const element = shallow(<PleiepengerUtbetalingerPanel perioder={mockPerioder} tekst={{}} />);
        expect(element.find('Periode').length).to.equal(2);
    });

    describe('periodeliste', () => {
        describe('sortering av perioder', () => {
            it('viser periode med siste ID-dato først når nyest er sist i listen', () => {
                const perioder = sorterEtterIdDato([mockPeriode(2016), mockPeriode(2017)]);
                expect(perioder[0].fraOgMed.year).to.equal(2017);
            });
            it('viser periode med siste ID-dato først når nyest er først i listen', () => {
                const perioder = sorterEtterIdDato([mockPeriode(2017), mockPeriode(2016)]);
                expect(perioder[0].fraOgMed.year).to.equal(2017);
            });
        });
    });
});

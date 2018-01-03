import '../../test-config';
import React from 'react';
import { mount } from 'enzyme';
import { expect } from 'chai';

import { ADRESSETYPER } from '../../constants';
import FolkeregistrertAdresse from './folkeregistrert-adresse-module';
import Gateadresse from './components/gateadresse';
import Adresse from './components/adresse';
import Tilleggsadresse from './components/tilleggsadresse';
import Matrikkeladresse from './components/matrikkeladresse';
import UstrukturertAdresse from './components/ustrukturert-adresse';

describe('Folkeregistrert-adresse', () => {
    describe('med tilleggsadresse', () => {
        it('skal vise tilleggsadresse hvis tilstede', () => {
            const adresse = {
                gatenavn: 'SANDAKERVEIEN',
                husnummer: '111',
                poststed: 'OSLO',
                postnummer: '0477'
            };
            const tilleggsadresse = 'C/O Jørn Berg';
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.GATEADRESSE} adresse={adresse} tilleggsadresse={tilleggsadresse} />);

            expect(element.find(Tilleggsadresse).text()).to.equal(tilleggsadresse);
        });
        it('skal ikke vise tilleggsadresse hvis fraværende', () => {
            const adresse = {
                gatenavn: 'SANDAKERVEIEN',
                husnummer: '111',
                poststed: 'OSLO',
                postnummer: '0477'
            };
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.GATEADRESSE} adresse={adresse} />);

            expect(element.find(Tilleggsadresse).html()).to.equal(null);
        });
    });
    describe('uten registrert adresse', () => {
        it('skal vise relevant melding til bruker', () => {
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.INGEN_ADRESSE_REGISTRERT} />);

            expect(element.find(Adresse).text()).to.equal('INGEN REGISTRERT ADRESSE');
        });
    });
    describe('som Gateadresse', () => {
        it('skal vise adresse med husbokstav med riktig formatering', () => {
            const adresse = {
                gatenavn: 'SANDAKERVEIEN',
                husnummer: '111',
                husbokstav: 'D',
                poststed: 'OSLO',
                postnummer: '0477'
            };
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.GATEADRESSE} adresse={adresse} />);

            expect(element.find(Gateadresse).text()).to.equal('SANDAKERVEIEN 111D, 0477 OSLO');
        });
        it('skal vise adresse uten husbokstav med riktig formatering', () => {
            const adresse = {
                gatenavn: 'SANDAKERVEIEN',
                husnummer: '111',
                poststed: 'OSLO',
                postnummer: '0477'
            };
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.GATEADRESSE} adresse={adresse} />);

            expect(element.find(Gateadresse).text()).to.equal('SANDAKERVEIEN 111, 0477 OSLO');
        });
        it('skal vise adresse med etasje med riktig formatering', () => {
            const adresse = {
                gatenavn: 'SANDAKERVEIEN',
                husnummer: '111',
                poststed: 'OSLO',
                postnummer: '0477',
                husbokstav: 'D',
                bolignummer: 'H0403'
            };
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.GATEADRESSE} adresse={adresse} />);

            expect(element.find(Gateadresse).text()).to.equal('SANDAKERVEIEN 111D H0403, 0477 OSLO');
        });
    });
    describe('som matrikkeladresse', () => {
        it('skal vise adresse riktig formatering', () => {
            const adresse = {
                eiendomsnavn: 'BØVERDALEN FJELLSTUE',
                poststed: 'LOM',
                postnummer: '6052'
            };
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.MATRIKKELADRESSE} adresse={adresse} />);

            expect(element.find(Matrikkeladresse).text()).to.equal('BØVERDALEN FJELLSTUE, 6052 LOM');
        });
        it('skal adresse uten eiendomsnavn formatert', () => {
            const adresse = {
                poststed: 'LOM',
                postnummer: '6052'
            };
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.MATRIKKELADRESSE} adresse={adresse} />);

            expect(element.find(Matrikkeladresse).text()).to.equal('6052 LOM');
        });
    });
    describe('ustrukturert adresse', () => {
        it('skal vises', () => {
            const element = mount(<FolkeregistrertAdresse adresseType={ADRESSETYPER.USTRUKTURERT} adresse={'Ustrukturert adresse'} />);

            expect(element.find(UstrukturertAdresse).text()).to.equal('Ustrukturert adresse');
        });
    });
});

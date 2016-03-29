import './../../test-config';
import {expect} from 'chai';
import { nyesteSakstema } from './dato-sortering';
import { nyesteElementISakstema } from './dato-sortering'

describe('Datosortering - ', () => {

    it('skal returnere den nyeste av datoene for et sakstema', () => {
        const datoFasit = new Date(2016,2,27).getTime();
        const datoFraMetode = nyesteElementISakstema(sakstema1);

        expect(datoFraMetode).to.be.eql(datoFasit);
    });

    it('skal sortere sakstema på nyeste dato', () => {
        const sakstemaListe = [].concat(sakstema1).concat(sakstema3).concat(sakstema2);

        sakstemaListe.sort(nyesteSakstema);

        expect(sakstemaListe[0].temakode).to.be.eql('DAG');
        expect(sakstemaListe[2].temakode).to.be.eql('IND');
    });
});

const sakstema1 = {
    temakode: 'AAP',
    behandlingskjeder: [
        {
            sistOppdatert: {
                year: 2016,
                dayOfMonth: 27,
                monthValue: 3,
                hour: 0,
                minute: 0,
                second: 0
            }
        }
    ],
    dokumentMetadata: [
        {
            dato: {
                year: 2016,
                dayOfMonth: 26,
                monthValue: 3,
                hour: 0,
                minute: 0,
                second: 0
            }
        }
    ]
};

const sakstema2 = {
    temakode: 'DAG',
    behandlingskjeder: [],
    dokumentMetadata: [
        {
            dato: {
                year: 2016,
                dayOfMonth: 28,
                monthValue: 3,
                hour: 0,
                minute: 0,
                second: 0
            }
        }
    ]
};

const sakstema3 = {
    temakode: 'IND',
    behandlingskjeder: [
        {
            sistOppdatert: {
                year: 2015,
                dayOfMonth: 27,
                monthValue: 3,
                hour: 0,
                minute: 0,
                second: 0
            }
        }
    ],
    dokumentMetadata: []
};
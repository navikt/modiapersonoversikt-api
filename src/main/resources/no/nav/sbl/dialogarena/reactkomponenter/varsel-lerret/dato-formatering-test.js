/* eslint-env mocha */
import './../test-config';
import { assert } from 'chai';
import moment from 'moment';
import { prettyDate, nyesteDatoForst } from './dato-formatering';

describe('DatoFormatering', () => {
    it('Default datoformat blir satt', () => {
        const dato = new Date();
        const datoStreng = prettyDate(dato);

        assert.isTrue(moment(datoStreng, 'DD. MMM').isValid());
    });

    it('Bruker innsendt datoformat', () => {
        const dato = new Date();
        const datoFormat = 'DD. MMMM';

        const datoStreng = prettyDate(dato, datoFormat);

        assert.isTrue(moment(datoStreng, datoFormat, true).isValid());
    });

    it('Sorterer nyeste dato forst', () => {
        const nyesteVarsel = { utsendingsTidspunkt: '2016-10-17T10:11:20.539Z' };
        const eldsteVarsel = { utsendingsTidspunkt: '2016-10-10T13:38:06.399Z' };

        const varslerEldsteForst = [eldsteVarsel, nyesteVarsel];
        const sorterteVarsler = varslerEldsteForst.sort(nyesteDatoForst);

        assert.strictEqual(sorterteVarsler[0], nyesteVarsel);
        assert.strictEqual(sorterteVarsler[1], eldsteVarsel);
    });

    it('Sorterer varsler som ikke er sendt forst', () => {
        const nyesteVarsel = { utsendingsTidspunkt: '2016-10-17T10:11:20.539Z' };
        const ikkeSendtVarsel = { utsendingsTidspunkt: null };

        const varsler = [nyesteVarsel, ikkeSendtVarsel];
        const sorterteVarsler = varsler.sort(nyesteDatoForst);

        assert.strictEqual(sorterteVarsler[0], ikkeSendtVarsel);
        assert.strictEqual(sorterteVarsler[1], nyesteVarsel);
    });
});

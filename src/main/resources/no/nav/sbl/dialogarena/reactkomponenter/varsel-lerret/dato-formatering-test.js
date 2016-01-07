/* eslint-env mocha */
import './../test-config';
import { assert } from 'chai';
import moment from 'moment';
import { prettyDate } from './dato-formatering';

describe('DatoFormatering', () => {
/*    it('Default datoformat blir satt', () => {
        const dato = new Date();
        const datoStreng = prettyDate(dato);

        assert.isTrue(moment(datoStreng, 'DD. MMM, HH.mm', true).isValid());
    });*/

    it('Bruker innsendt datoformat', () => {
        const dato = new Date();
        const datoFormat = 'DD. MMMM, HH.mm';

        const datoStreng = prettyDate(dato, datoFormat);

        assert.isTrue(moment(datoStreng, datoFormat, true).isValid());
    });
});

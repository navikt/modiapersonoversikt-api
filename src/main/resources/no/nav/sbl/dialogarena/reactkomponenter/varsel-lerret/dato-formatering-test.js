import './../test-config';
import { assert } from 'chai';
import { prettyDate } from './dato-formatering';

describe('DatoFormatering', function () {
    it('Default datoformat blir satt', function(){
        const dato = new Date();
        const datoStreng = prettyDate(dato);

        assert.isTrue(moment(datoStreng, 'DD. MMM, HH.mm', true).isValid());
    });

    it('Bruker innsendt datoformat', function(){
        const dato = new Date();
        const datoFormat = 'DD. MMMM, HH.mm';

        const datoStreng = prettyDate(dato, datoFormat);

        assert.isTrue(moment(datoStreng, datoFormat, true).isValid());
    });
});

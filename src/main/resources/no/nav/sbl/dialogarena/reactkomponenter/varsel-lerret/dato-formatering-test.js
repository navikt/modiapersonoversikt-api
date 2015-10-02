require('./../test-config.js');
var DatoFormatering = require('./dato-formatering');
var assert = require('chai').assert;


describe('DatoFormatering', function () {
    it('Default datoformat blir satt', function(){
        var dato = new Date();
        var datoStreng = DatoFormatering.prettyDate(dato);

        assert.isTrue(moment(datoStreng, 'DD. MMM, HH.mm', true).isValid());
    });

    it('Bruker innsendt datoformat', function(){
        var dato = new Date();
        var datoFormat = 'DD. MMMM, HH.mm';

        var datoStreng = DatoFormatering.prettyDate(dato, datoFormat);

        assert.isTrue(moment(datoStreng, datoFormat, true).isValid());
    });
});
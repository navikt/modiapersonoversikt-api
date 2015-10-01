require('./../test-config.js');
var ResourceMap = require('./resource-map');
var assert = require('chai').assert;


describe('ResourceMap', function () {

    it('Returnerer defaultValue om key ikke finnes', function(){
        var rm = new ResourceMap({});
        assert.equal(rm.getOrElse('tull', 'default'), 'default');
    });

});
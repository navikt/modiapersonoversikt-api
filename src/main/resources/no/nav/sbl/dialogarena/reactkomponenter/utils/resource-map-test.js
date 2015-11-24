import './../test-config';
import ResourceMap from './resource-map';
import { assert } from 'chai';

describe('ResourceMap', function () {

    it('Returnerer defaultValue om key ikke finnes', function(){
        var rm = new ResourceMap({});
        assert.equal(rm.getOrElse('tull', 'default'), 'default');
    });

});
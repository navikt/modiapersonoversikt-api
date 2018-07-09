/* eslint-env mocha */
import './../test-config';
import ResourceMap from './resource-map';
import { assert } from 'chai';

describe('ResourceMap', () => {
    it('Returnerer defaultValue om key ikke finnes', () => {
        const rm = new ResourceMap({});
        assert.equal(rm.getOrElse('tull', 'default'), 'default');
    });
});

/* eslint-env mocha */
import './../test-config';
import React from 'react';
import { render, strEndsWith } from './../test-utils';
import { assert } from 'chai';
import Snurrepipp from './snurrepipp';

describe('Snurrepipp test', () => {
    it('Skal rendre default farge og størrelse på bilde', () => {
        const { dom } = render(<Snurrepipp/>);

        const image = dom.querySelector('img');

        assert.isDefined(image);
        assert.isTrue(strEndsWith(image.src, 'img/ajaxloader/graa/loader_graa_128.gif'));
    });

    it('Skal rendre custom farge og størrelse på bilde', () => {
        const { dom } = render(<Snurrepipp farge="indigo" storrelse="1337"/>);

        const image = dom.querySelector('img');

        assert.isDefined(image);
        assert.isTrue(strEndsWith(image.src, 'img/ajaxloader/indigo/loader_indigo_1337.gif'));
    });
});

import './../test-config';
import { expect } from 'chai';
import React from 'react/addons';
import sinon from 'sinon';
import 'sinon-chai';
import AdvarselBoks from './advarsel-boks';
const TestUtils = React.addons.TestUtils;

describe('AdvarselBoks test', function(){
    it("Skal vise tekst og ha klassen warn", function(){
        const text = "TEXT"
        const advarselboks = TestUtils.renderIntoDocument(<AdvarselBoks tekst={text}/>);

        const rendered = React.findDOMNode(advarselboks);


        expect(rendered.tagName).to.equal('DIV');
        expect(rendered.classList[0]).to.equal('advarsel-boks');
        expect(rendered.classList[1]).to.equal('warn');

        const children = rendered.querySelectorAll('p');
        expect(children.length).to.equal(1);

        const avsnitt = children[0];
        expect(avsnitt.tagName).to.equal('P');
        expect(avsnitt.innerHTML).to.equal(text);
    });
});
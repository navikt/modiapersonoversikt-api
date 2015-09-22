require('./../test-config');
var expect = require('chai').expect;
var React = require('react/addons');
var TestUtils = React.addons.TestUtils;
var sinon = require('sinon');
require('sinon-chai');
var SakerListe = require('./saker-liste');
var SakerForTema = require('./saker-for-tema');

describe('SakerListe', function () {

    it('skal gruppere saker på samme temaKode', function () {
        const saker = [
            lagSak('DAG'),
            lagSak('DAG'),
            lagSak('AAP'),
            lagSak('DAG'),
            lagSak('AAP'),
            lagSak('BIL')
        ];
        const sakerForTema = setup(saker);

        expect(sakerForTema.length).to.equal(3);

        const filtrerPaaTema = (tema) => sakerForTema.filter(elem => elem.props.tema === tema)[0];

        // DAG har tre saker
        expect(filtrerPaaTema('DAG').props.saker.length).to.equal(3);

        // AAP har to sak
        expect(filtrerPaaTema('AAP').props.saker.length).to.equal(2);

        // BIL har én sak
        expect(filtrerPaaTema('BIL').props.saker.length).to.equal(1);

    });

    it("sorterer på alfabetisk på temaNavn", function () {
        const saker = [
            lagSak('AAB'),
            lagSak('AAA'),
            lagSak('AAAC'),
            lagSak('AAAA')
        ];
        const sakerForTema = setup(saker);

        expect(sakerForTema.map(elem => elem.props.tema)).to.eql(['AAA', 'AAAA', 'AAAC', 'AAB']);
    });

    const lagSak = (tema) => {
        return {temaKode: tema, temaNavn: tema}
    };

    const setup = (saker) => {
        const sakerListe = TestUtils.renderIntoDocument(<SakerListe saker={saker}/>);
        return TestUtils.scryRenderedComponentsWithType(sakerListe, SakerForTema);
    }
});

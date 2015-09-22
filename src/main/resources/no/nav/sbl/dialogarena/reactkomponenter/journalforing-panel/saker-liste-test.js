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

    it("skal liste prioritete temagrupper først", function(){
        const saker = [
            lagSak('DAG'),
            lagSak('tema1'),
            lagSak('tema2'),
            lagSak('BIL')
        ];
        const sakerForTema = setupMedTemagruppe(saker, 'ARBD', {ARBD: ['tema1', 'tema2']});

        expect(sakerForTema.map(elem => elem.props.tema)).to.eql(['tema1', 'tema2', 'BIL', 'DAG']);
        expect(sakerForTema.map(elem => elem.props.tema)).to.not.eql(['tema1', 'tema2', 'DAG', 'BIL' ]);
    });

    const lagSak = (tema) => {
        return {temaKode: tema, temaNavn: tema}
    };

    const setup = (saker) => {
        const sakerListe = TestUtils.renderIntoDocument(<SakerListe saker={saker}/>);
        return TestUtils.scryRenderedComponentsWithType(sakerListe, SakerForTema);
    };
    const setupMedTemagruppe = (saker, temagruppe, mapping) => {
        const sakerListe = TestUtils.renderIntoDocument(<SakerListe saker={saker} temagruppe={temagruppe} temagruppeTemaMapping={mapping}/>);
        return TestUtils.scryRenderedComponentsWithType(sakerListe, SakerForTema);
    }
});

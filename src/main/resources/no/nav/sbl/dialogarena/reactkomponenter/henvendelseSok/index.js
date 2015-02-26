var React = require('react');
var Modal = require('modal');
var Soklayout = require('sokLayout');

var SokKomponent = require('./SokKomponent');
var ListevisningKomponent = require('./ListevisningKomponent');
var ForhandsvisningKomponent = require('./ForhandsvisningKomponent');

module.exports = React.createClass({
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    submit: function(){

    },
    render: function(){
        return (
            <Modal ref="modal">
                <Soklayout {...this.props} sok={sok} submit={this.submit}
                    sokKomponent={SokKomponent}
                    listeelementKomponent={ListevisningKomponent}
                    visningsKomponent={ForhandsvisningKomponent}
                    valgtElement={{innhold: {nb_NO: ''}}}
                />
            </Modal>
        );
    }
});

function sok(query) {
    return hentFraRestAPI(query)
        .done(function(traader){
            traader.forEach(function(traad){
                traad.key = traad.traadId;
                traad.type = "sporsmal-ikke-lest-av-bruker";
                traad.typeBeskrivelse = "Spørsmål fra NAV, sendt";
                traad.innhold = traad.meldinger[0].fritekst;
            });
        });
}
function hentFraRestAPI(query) {
    var d = $.Deferred();

    setTimeout(function(){
        var lst = [
            lagTraad(1, "ARBD"),
            lagTraad(2, "FMLI"),
            lagTraad(3, "OVGR"),
            lagTraad(4, "FMLI"),
            lagTraad(5, "ARBD")
        ];
        d.resolve(lst);
    }, 50);

    return d.promise();
}
function lagTraad(traadId, tema) {
    return {
        'traadId': 'traadId' + traadId,
        'temagruppe': tema,
        'journalfortTema': undefined,
        'meldinger': [
            lagMelding(traadId+0.1, "u143410", "10108000398", "ARBD", "TELEFON", "lorem ipsim"),
            lagMelding(traadId+0.2, "u143410", "10108000398", "ARBD", "TELEFON", "lorem ipsim"),
            lagMelding(traadId+0.3, "u143410", "10108000398", "ARBD", "TELEFON", "lorem ipsim"),
            lagMelding(traadId+0.4, "u143410", "10108000398", "ARBD", "TELEFON", "lorem ipsim"),
            lagMelding(traadId+0.5, "u143410", "10108000398", "ARBD", "TELEFON", "lorem ipsim")
        ]
    }
}

function lagMelding(traadId, nav, bruker, tema, kanal, tekst) {
    return {
        traadId: 'traadId' + traadId,
        fnrBruker: bruker,
        navIdent: nav,
        temagruppe: tema,
        kanal: kanal,
        fritekst: tekst,
        opprettetDato: new Date(new Date().toDateString() - traadId * 3600000)
    }
}
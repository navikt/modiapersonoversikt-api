var React = require('react');
var Modal = require('modal');
var Soklayout = require('sokLayout');

var SokKomponent = require('./SokKomponent');
var ListevisningKomponent = require('./ListevisningKomponent');
var ForhandsvisningKomponent = require('./ForhandsvisningKomponent');

module.exports = React.createClass({
    componentWillMount: function () {
        $.get('/modiabrukerdialog/rest/meldinger/' + this.props.fnr + '/indekser');
    },
    getInitialState: function () {
        return {traadRef: {}}
    },
    oppdaterTraadRef: function (traadRef) {
        this.setState({traadRef: traadRef});
    },
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    submit: function (valgtTraad) {
        $('#' + this.state.traadRef[valgtTraad.traadId]).click();
        this.skjul();
    },
    render: function () {
        return (
            <Modal ref="modal">
                <Soklayout {...this.props} sok={sok.bind(this, this.props.fnr)} submit={this.submit}
                    containerClassName="henvendelse-sok"
                    sokKomponent={SokKomponent}
                    listeelementKomponent={ListevisningKomponent}
                    visningsKomponent={ForhandsvisningKomponent}
                    valgtElement={{innhold: {nb_NO: ''}}}
                />
            </Modal>
        );
    }
});

function sok(fnr, query) {
    return hentFraRestAPI(fnr, query)
        .done(function (traader) {
            traader.forEach(function (traad) {
                traad.key = traad.traadId;
                traad.datoInMillis = traad.dato.millis;
                traad.innhold = traad.meldinger[0].fritekst;
                traad.meldinger.forEach(function (melding) {
                    melding.erInngaaende = ['SPORSMAL_SKRIFTLIG', 'SVAR_SBL_INNGAAENDE'].indexOf(melding.meldingstype) >= 0;
                    melding.fraBruker = melding.erInngaaende ? melding.fnrBruker : melding.eksternAktor;
                });
            });
        });
}
function hentFraRestAPI(fnr, query) {
    if (typeof query !== "string") {
        query = "";
    }
    var url = '/modiabrukerdialog/rest/meldinger/' + fnr + '/sok/' + encodeURIComponent(query);
    return $.get(url);
}

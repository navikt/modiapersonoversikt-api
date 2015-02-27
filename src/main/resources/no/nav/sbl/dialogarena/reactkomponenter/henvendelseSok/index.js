var React = require('react');
var Modal = require('modal');
var Soklayout = require('sokLayout');

var SokKomponent = require('./SokKomponent');
var ListevisningKomponent = require('./ListevisningKomponent');
var ForhandsvisningKomponent = require('./ForhandsvisningKomponent');

module.exports = React.createClass({
    componentDidMount: function(){
        $.get('/modiabrukerdialog/rest/meldinger/'+this.props.fnr+'/indekser');
    },
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
        .done(function(traader){
            traader.forEach(function(traad){
                traad.key = traad.traadId;
                traad.type = "sporsmal-ikke-lest-av-bruker";
                traad.typeBeskrivelse = "Spørsmål fra NAV, sendt";
                traad.innhold = traad.meldinger[0].fritekst;
            });
        });
}
function hentFraRestAPI(fnr, query) {
    if (typeof query !== "string") {
        query = "";
    }
    var url = '/modiabrukerdialog/rest/meldinger/'+fnr+'/sok/' + encodeURIComponent(query);
    return $.get(url);
}
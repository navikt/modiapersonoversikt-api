var React = require('react');
var Modal = require('modal');
var Soklayout = require('sokLayout');
var SokKomponent = require('./Filter');
var ListeElementKomponent = require('./TekstListeKomponent');
var TekstForhandsvisningKomponent = require('./TekstForhandsvisning');
var Utils = require('./Utils');

module.exports = React.createClass({
    vis: function () {
        this.refs.modal.open();
    },
    skjul: function () {
        this.refs.modal.close();
    },
    sok: function(query){
        query = query || {};
        var fritekst = query.fritekst || '';
        var knagger = query.knagger || [];

        fritekst = fritekst.replace(/^#*(.*)$/, '$1');

        var url = '/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + encodeURIComponent(fritekst);
        if (knagger.length !== 0) {
            url += '&tags=' + encodeURIComponent(knagger);
        }

        return $.get(url);
    },
    submit: function (valgtTekst, valgtLocale) {
        $('#' + this.props.tekstfeltId)
            .focus()
            .val(autofullfor.bind(this)(stripEmTags(Utils.getInnhold(valgtTekst, valgtLocale))))
            .trigger('input');

        this.skjul();
    },
    render: function () {
        return (
            <Modal ref="modal" skipFocus={['div', '.knagg > button']}>
                <Soklayout {...this.props} sok={this.sok} submit={this.submit}
                    containerClassName="tekstforslag"
                    sokKomponent={SokKomponent}
                    listeelementKomponent={ListeElementKomponent}
                    visningsKomponent={TekstForhandsvisningKomponent}
                    valgtElement={{innhold: {nb_NO: ''}}}
                />
            </Modal>
        );
    }
});
function stripEmTags(tekst) {
    return tekst.replace(/<em>(.*?)<\/em>/g, '$1')
}

function autofullfor(tekst) {
    var nokler = {
        'bruker.fnr': this.props.autofullfor.bruker.fnr,
        'bruker.fornavn': this.props.autofullfor.bruker.fornavn,
        'bruker.etternavn': this.props.autofullfor.bruker.etternavn,
        'saksbehandler.ident': this.props.autofullfor.saksbehandler.ident,
        'saksbehandler.enhet': this.props.autofullfor.saksbehandler.enhet,
        'saksbehandler.fornavn': this.props.autofullfor.saksbehandler.fornavn,
        'saksbehandler.etternavn': this.props.autofullfor.saksbehandler.etternavn
    };

    return tekst.replace(/\[(.*?)]/g, function (tekst, resultat) {
        return nokler[resultat] || '[ukjent nøkkel]';
    });
}
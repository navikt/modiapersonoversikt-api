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
    sok: function (query) {
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
        var $tekstfelt = $('#' + this.props.tekstfeltId);
        var eksisterendeTekst = $tekstfelt.focus().val();
        eksisterendeTekst += eksisterendeTekst.length === 0 ? "" : "\n";

        $tekstfelt
            .focus()
            .val(eksisterendeTekst + autofullfor.bind(this)(stripEmTags(Utils.getInnhold(valgtTekst, valgtLocale))))
            .trigger('input');

        this.skjul();
    },
    render: function () {
        return (
            <Modal ref="modal" skipFocus={['div', '.knagg > button']}>
                <Soklayout {...this.props} sok={this.sok} submit={this.submit}
                    containerClassName="tekstforslag"
                    sokKomponent={SokKomponent}
                    sokKomponentProps={{knagger: this.props.knagger}}
                    listeelementKomponent={ListeElementKomponent}
                    visningsKomponent={TekstForhandsvisningKomponent}
                    valgtElement={{innhold: {nb_NO: ''}, tags: []}}
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
        'bruker.navn': this.props.autofullfor.bruker.navn,
        'bruker.navkontor': this.props.autofullfor.bruker.navkontor,
        'saksbehandler.fornavn': this.props.autofullfor.saksbehandler.fornavn,
        'saksbehandler.etternavn': this.props.autofullfor.saksbehandler.etternavn,
        'saksbehandler.navn': this.props.autofullfor.saksbehandler.navn,
        'saksbehandler.enhet': this.props.autofullfor.saksbehandler.enhet
    };

    return tekst.replace(/\[(.*?)]/g, function (tekst, resultat) {
        return nokler[resultat] || '[ukjent nøkkel]';
    });
}
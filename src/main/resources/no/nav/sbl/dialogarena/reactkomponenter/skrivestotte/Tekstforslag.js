/** @jsx React.DOM */
var React = ModiaJS.React;
var Modal = ModiaJS.Components.Modal;

var Utils = require('./Utils');

var Filter = require('./Filter');
var Tekstvisning = require('./Tekstvisning');

var Tekstforslag = React.createClass({
    getInitialState: function () {
        return {
            tekster: [],
            valgtTekst: {innhold: {nb_NO: ''}},
            valgtLocale: Utils.Constants.LOCALE_DEFAULT,
            knagger: [],
            sokTekst: '',
            vis: false
        };
    },
    componentDidMount: function () {
        hentEnonicTekster('', []).done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {nb_NO: ''}},
                tekster: tekster
            });
        }.bind(this));
    },
    setValgtTekst: function (tekst) {
        this.setState({valgtTekst: tekst})
    },
    setValgtLocale: function (locale) {
        this.setState({valgtLocale: locale})
    },
    sok: function (sokTekst, knagger) {
        this.setState({sokTekst: sokTekst, knagger: knagger});
        sok.bind(this)(sokTekst.replace(/#/g, ""), knagger);
    },
    sokNavigasjon: function (event) {
        switch (event.keyCode) {
            case 38: /* pil opp */
                event.preventDefault();
                    this.setValgtTekst(hentTekst(forrigeTekst, this.state.tekster, this.state.valgtTekst));
                break;
            case 40: /* pil ned */
                event.preventDefault();
                this.setValgtTekst(hentTekst(nesteTekst, this.state.tekster, this.state.valgtTekst));
                break;
            case 13: /* enter */
                event.preventDefault();
                this.settInnTekst();
                break;
        }
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId)
            .focus()
            .val(autofullfor.bind(this)(stripEmTags(Utils.getInnhold(this.state.valgtTekst, this.state.valgtLocale))))
            .trigger('input');
        this.skjul();
    },
    vis: function() {
        this.setState({vis: true});
    },
    skjul: function(){
        this.setState({vis: false});
    },
    render: function () {
        return (
            <Modal isOpen={this.state.vis}>
                <div className="tekstforslag">
                    <Filter sok={this.sok} sokNavigasjon={this.sokNavigasjon} sokTekst={this.state.sokTekst} knagger={this.state.knagger} />
                    <Tekstvisning
                        tekster={this.state.tekster} valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale}
                        setValgtTekst={this.setValgtTekst} setValgtLocale={this.setValgtLocale} settInnTekst={this.settInnTekst} />
                </div>
            </Modal>
        );
    }
});

function hentEnonicTekster(fritekst, knagger) {
    var url = '/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + encodeURIComponent(fritekst);
    if (knagger.length !== 0) {
        url += '&tags=' + encodeURIComponent(knagger);
    }
    return $.get(url);
}

var sok = Utils.debounce(function (sokTekst, knagger) {
    hentEnonicTekster(sokTekst, knagger).done(function (tekster) {
        this.setState({
            valgtTekst: tekster[0] || {innhold: {nb_NO: ''}},
            tekster: tekster
        });
    }.bind(this))
}, 150);

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

function hentTekst(hentTekst, tekster, valgtTekst) {
    for (var i = 0; i < tekster.length; i++) {
        if (tekster[i].key === valgtTekst.key) {
            return hentTekst(tekster, i);
        }
    }
}
function forrigeTekst(tekster, index) {
    return index === 0 ? tekster[0] : tekster[index - 1];
}
function nesteTekst(tekster, index) {
    return index === tekster.length - 1 ? tekster[tekster.length - 1] : tekster[index + 1];
}

module.exports = Tekstforslag;

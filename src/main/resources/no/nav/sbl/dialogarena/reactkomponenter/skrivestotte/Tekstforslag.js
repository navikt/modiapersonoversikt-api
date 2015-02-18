/** @jsx React.DOM */
var React = ModiaJS.React;

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
            fokusertElement: {},
            vis: false
        };
    },
    componentWillMount: function () {
        Utils.moveNodeToParent(this.props.reactContainer, document.body);
    },
    componentDidMount: function () {
        hentEnonicTekster('', []).done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {nb_NO: ''}},
                tekster: tekster
            });
        }.bind(this));
    },
    vis: function () {
        this.setState({fokusertElement: $(':focus')});
        this.setState({vis: true});
    },
    skjul: function () {
        this.state.fokusertElement.blur().focus();
        this.setState({vis: false});
    },
    setValgtTekst: function (tekst) {
        this.setState({valgtTekst: tekst})
    },
    setValgtLocale: function (locale) {
        this.setState({valgtLocale: locale})
    },
    sok: function (sokTekst, knagger) {
        this.setState({sokTekst: sokTekst, knagger: knagger});
        sok.bind(this)(sokTekst, knagger);
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
    generellNavigasjon: function (event) {
        switch (event.keyCode) {
            case 27: /* esc */
                event.stopPropagation();
                this.skjul();
                break;
            case 9: /* tab */
                var domNode = $(this.getDOMNode());
                var focusable = domNode.find(':focusable');
                var index = focusable.index(domNode.find(':focus'));

                if (event.shiftKey && index === 0) {
                    event.preventDefault();
                    focusable.eq(focusable.length - 1).focus();
                } else if (!event.shiftKey && focusable.length - 1 === index) {
                    event.preventDefault();
                    focusable.eq(0).focus();
                }

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
    render: function () {
        if (!this.state.vis) {
            return null;
        }

        return (
            <div className="tekstforslagModal" tabIndex="-1" onKeyDown={this.generellNavigasjon} >
                <div className="backdrop" onClick={this.skjul}></div>
                <div className="tekstforslag">
                    <Filter sok={this.sok} sokNavigasjon={this.sokNavigasjon} sokTekst={this.state.sokTekst} knagger={this.state.knagger} />
                    <Tekstvisning
                        tekster={this.state.tekster} valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale}
                        setValgtTekst={this.setValgtTekst} setValgtLocale={this.setValgtLocale} settInnTekst={this.settInnTekst} />
                </div>
            </div>
        );
    }
});

function hentEnonicTekster(fritekst, knagger) {
    var url = '/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + fritekst;
    if (knagger.length !== 0) {
        url += '&tags=' + knagger;
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

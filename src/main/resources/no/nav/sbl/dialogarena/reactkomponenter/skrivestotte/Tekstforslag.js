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
            sokTekst: '',
            show: false
        };
    },
    componentDidMount: function () {
        hentEnonicTekster('').done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {nb_NO: ''}},
                tekster: tekster
            });
        }.bind(this));
    },
    toggle: function () {
        this.setState({show: !this.state.show});
    },
    setValgtTekst: function (tekst) {
        this.setState({valgtTekst: tekst})
    },
    setValgtLocale: function (locale) {
        this.setState({valgtLocale: locale})
    },
    sok: function (sokTekst) {
        this.setState({sokTekst: sokTekst});
        sok.bind(this)(sokTekst);
    },
    sokNavigasjon: function (event) {
        switch (event.keyCode) {
            case 38:
                event.preventDefault();
                this.setValgtTekst(hentTekst(forrigeTekst, this.state.tekster, this.state.valgtTekst));
                break;
            case 40:
                event.preventDefault();
                this.setValgtTekst(hentTekst(nesteTekst, this.state.tekster, this.state.valgtTekst));
                break;
            case 13:
                this.settInnTekst();
                break;
        }
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId)
            .focus()
            .val(stripEmTags(Utils.getInnhold(this.state.valgtTekst, this.state.valgtLocale)))
            .trigger('input');
        this.setState({show: false});
    },
    render: function () {
        if (!this.state.show) {
            return null;
        }

        return (
            <div className="tekstforslag">
                <Filter sok={this.sok} sokNavigasjon={this.sokNavigasjon} />
                <Tekstvisning
                    tekster={this.state.tekster} valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale}
                    setValgtTekst={this.setValgtTekst} setValgtLocale={this.setValgtLocale} settInnTekst={this.settInnTekst} />
            </div>
        );
    }
});

function hentEnonicTekster(fritekst) {
    return $.get('/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + fritekst);
}

var sok = Utils.debounce(function (sokTekst) {
    hentEnonicTekster(sokTekst).done(function (tekster) {
        this.setState({
            valgtTekst: tekster[0] || {innhold: {nb_NO: ''}},
            tekster: tekster
        });
    }.bind(this))
}, 150);

function stripEmTags(tekst) {
    return tekst.replace(/<em>(.*?)<\/em>/g, '$1')
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

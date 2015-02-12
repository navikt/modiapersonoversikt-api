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
    sok: Utils.debounce(function (sokTekst) {
        hentEnonicTekster(sokTekst).done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {nb_NO: ''}},
                tekster: tekster
            });
        }.bind(this));
    }, 150),
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId).focus().val(Utils.getInnhold(this.state.valgtTekst, this.state.valgtLocale));
        this.setState({show: false});
    },
    render: function () {
        if (!this.state.show) {
            return null;
        }

        return (
            <div className="tekstforslag">
                <Filter sok={this.sok} />
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

module.exports = Tekstforslag;

/** @jsx React.DOM */
var React = require('react');

var Filter = require('./Filter');
var Tekstvisning = require('./Tekstvisning');

var Tekstforslag = React.createClass({
    getInitialState: function () {
        return {tekster: [], valgtTekst: {innhold: {nb_NO: ''}}, valgtLocale: 'nb_NO', show: false};
    },
    componentDidMount: function () {
        hentEnonicTekster('').done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {}},
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
    setSokTekst: function (sokTekst) {
        hentEnonicTekster(sokTekst).done(function (tekster) {
            this.setState({
                valgtTekst: tekster[0] || {innhold: {}},
                tekster: tekster
            });
        }.bind(this));
    },
    settInnTekst: function () {
        $('#' + this.props.tekstfeltId).focus().val(this.state.valgtTekst.innhold[this.state.valgtLocale]);
        this.setState({show: false});
    },
    render: function () {
        if (!this.state.show) {
            return null;
        }

        return (
            <div className="tekstforslag">
                <Filter setSokTekst={this.setSokTekst} />
                <Tekstvisning
                    tekster={this.state.tekster} valgtTekst={this.state.valgtTekst} valgtLocale={this.state.valgtLocale}
                    setValgtTekst={this.setValgtTekst} setValgtLocale={this.setValgtLocale} />
                <input type="button" value="Velg tekst" onClick={this.settInnTekst}/>
            </div>
        );
    }

});

function hentEnonicTekster(fritekst) {
    return $.get('/modiabrukerdialog/rest/skrivestotte/sok?fritekst=' + fritekst);
}

module.exports = Tekstforslag;

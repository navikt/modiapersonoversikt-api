/** @jsx React.DOM */
var React = ModiaJS.React;

var Filter = React.createClass({
    componentDidMount: function () {
        this.refs.sok.getDOMNode().focus();
    },
    sok: function (event) {
        this.props.sok(event.target.value);
    },
    render: function () {
        return (
            <div className="filter-container">
                <input type="text" placeholder="Søk" ref="sok" value={this.props.sokTekst} onChange={this.sok} onKeyDown={this.props.sokNavigasjon}
                aria-label="Søk etter hjelpetekster" aria-controls="tekstListePanel"/>
            </div>
        );
    }
});

module.exports = Filter;

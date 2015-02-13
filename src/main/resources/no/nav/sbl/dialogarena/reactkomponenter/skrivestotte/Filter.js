/** @jsx React.DOM */
var React = ModiaJS.React;

var Filter = React.createClass({
    sok: function (event) {
        if (event.target.value === this.props.sokTekst) {
            return;
        }
        this.props.sok(event.nativeEvent.target.value);
    },
    render: function () {
        return (
            <div className="filter-container">
                <input type="text" id="sok" placeholder="Søk" onKeyUp={this.sok} onKeyDown={this.props.sokNavigasjon} />
            </div>
        );
    }
});

module.exports = Filter;

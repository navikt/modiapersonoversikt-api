/** @jsx React.DOM */
var React = require('react');

var Filter = React.createClass({
    setSokTekst: function (event) {
        this.props.setSokTekst(event.nativeEvent.target.value);
    },
    render: function () {
        return (
            <div className="filter-container">
                <input type="text" placeholder="Søk" onChange={this.setSokTekst} />
            </div>
        );
    }
});

module.exports = Filter;

/** @jsx React.DOM */
var React = require('react');

var Filter = React.createClass({
    setFritekst: function (event) {
        this.props.setFritekst(event.nativeEvent.target.value);
    },
    render: function () {
        return (
            <div className="filter-container">
                <input type="text" placeholder="Søk" onChange={this.setFritekst} value={this.props.tekst}/>
            </div>
        );
    }
});

module.exports = Filter;

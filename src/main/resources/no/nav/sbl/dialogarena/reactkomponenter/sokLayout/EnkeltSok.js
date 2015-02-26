/** @jsx React.DOM */
var React = require('react');
var MockData = require('./mockData.js');

var EnkeltSok = React.createClass({
    componentDidMount: function(){
        console.warn('Du bruker er default implementasjon av søk for søkelayouten.');
    },
    statics: {
        sok: function (query) {
            var d = $.Deferred();

            setTimeout(function () {
                d.resolve(MockData.slice(query.length))
            }, Math.random() * 1000);

            return d.promise();
        }
    },
    onChange: function(event){
        this.props.onChange(event.target.value);
    },
    render: function () {
        return <div tabIndex="-1" className="filter-container">
            <input type="text" placeholder="Skriv her" onChange={this.onChange}/>
        </div>;
    }
});

module.exports = EnkeltSok;
var React = require('react');
var Melding = require('./Melding');

module.exports = React.createClass({
    render: function(){
        console.log('render visning');
        if (!this.props.element.hasOwnProperty('meldinger')){
            return null;
        }

        var meldinger = this.props.element.meldinger.map(function(melding){
            return <Melding melding={melding} />
        });

        return (
            <div>
                <div className="traadPanel">
                    {meldinger}
                </div>
                <div className="velgPanel">
                    <input type="submit" value="Velg tråd" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});
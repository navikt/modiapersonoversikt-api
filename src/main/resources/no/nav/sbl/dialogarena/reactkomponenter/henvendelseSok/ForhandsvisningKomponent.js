var React = require('react');
var Melding = require('./Melding');

module.exports = React.createClass({
    render: function(){
        if (!this.props.henvendelse.hasOwnProperty('meldinger')){
            return null;
        }

        var meldinger = this.props.henvendelse.meldinger.map(function(melding){
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
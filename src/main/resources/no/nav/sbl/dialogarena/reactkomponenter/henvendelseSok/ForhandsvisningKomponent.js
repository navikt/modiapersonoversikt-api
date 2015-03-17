var React = require('react');
var Melding = require('./Melding');

var ForhandsvisningKomponent = React.createClass({
    render: function(){
        if (!this.props.traad.hasOwnProperty('meldinger')){
            return null;
        }

        var meldinger = this.props.traad.meldinger.map(function(melding){
            return <Melding key={melding.id} melding={melding} />
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

module.exports = ForhandsvisningKomponent;
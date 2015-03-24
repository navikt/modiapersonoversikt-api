var React = require('react');
var Melding = require('./Melding');

var ForhandsvisningKomponent = React.createClass({
    render: function() {
        if (!this.props.traad.hasOwnProperty('meldinger')) {
            return null;
        }

        var meldinger = this.props.traad.meldinger;

        var meldingElementer = meldinger.map(function(melding){
            return <Melding key={melding.id} melding={melding} />
        });

        return (
            <div>
                <div className="traadPanel">
                    <div className="traadinfo">
                        {"Viser "}
                        <span className="antall-meldinger">{meldinger.length}</span>
                        {" av "}
                        <span className="antall-meldinger">{meldinger[0].antallMeldingerITraad}</span>
                        {" " + (meldinger[0].antallMeldingerITraad === "1" ? "melding" : "meldinger") + " i dialogen"}
                    </div>
                    <div>{meldingElementer}</div>
                </div>
                <div className="velgPanel">
                    <input type="submit" value="Velg tråd" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});

module.exports = ForhandsvisningKomponent;
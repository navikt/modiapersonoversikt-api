var React = require('react/addons');
var Melding = require('./Melding');
var format = require('string-format');

var ForhandsvisningKomponent = React.createClass({
    render: function () {
        if (!this.props.traad.hasOwnProperty('meldinger')) {
            return null;
        }

        var traad = this.props.traad;
        var meldinger = traad.meldinger;

        var meldingElementer = meldinger.map(function(melding){
            return <Melding key={melding.id} melding={melding} />
        });

        var antallInformasjon = format('Viser <b>{}</b> av <b>{}</b> {} i dialogen',
            meldinger.length,
            traad.antallMeldingerIOpprinneligTraad,
            traad.antallMeldingerIOpprinneligTraad === 1 ? "melding" : "meldinger"
        );

        return (
            <div>
                <div className="traadPanel">
                    <div className="traadinfo">
                        <span dangerouslySetInnerHTML={{__html: antallInformasjon}}></span>
                    </div>
                    <div>{meldingElementer}</div>
                </div>
                <div className="velgPanel">
                    <input type="submit" value="Vis dialog" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});

module.exports = ForhandsvisningKomponent;
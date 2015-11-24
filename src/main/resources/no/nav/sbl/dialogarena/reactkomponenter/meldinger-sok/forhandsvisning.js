import React from 'react/addons';
import Melding from './melding';
import ScrollPortal from './../utils/scroll-portal';
import format from 'string-format';

const ForhandsvisningKomponent = React.createClass({
    propTypes: {
        traad: React.PropTypes.object.isRequired
    },
    render: function render() {
        if (!this.props.traad.hasOwnProperty('meldinger')) {
            return null;
        }

        const traad = this.props.traad;
        const meldinger = traad.meldinger;

        const meldingElementer = meldinger.map((melding) => <Melding key={melding.id} melding={melding} />);

        const antallInformasjon = format('Viser <b>{}</b> av <b>{}</b> {} i dialogen',
            meldinger.length,
            traad.antallMeldingerIOpprinneligTraad,
            traad.antallMeldingerIOpprinneligTraad === 1 ? 'melding' : 'meldinger'
        );

        return (
            <div>
                <ScrollPortal className="traadPanel" innerClassName="traad-panel-wrapper">
                    <div className="traadinfo">
                        <span dangerouslySetInnerHTML={{__html: antallInformasjon}}></span>
                    </div>
                    <div>{meldingElementer}</div>
                </ScrollPortal>
                <div className="velgPanel">
                    <input type="submit" value="Vis dialog" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
});

module.exports = ForhandsvisningKomponent;

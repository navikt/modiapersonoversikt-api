var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

module.exports = React.createClass({
    render: function(){
        var melding = this.props.melding;
        var clsExt = erUtgaaende(melding) ? 'utgaaende' : 'ingaaende';
        var cls = 'melding clearfix ' + clsExt;

        var dato = moment(melding.dato || new Date()).format('LLL');
        return (
            <div className={cls}>
                <img className={'avsenderBilde '+clsExt} src="/modiabrukerdialog/img/nav-logo.svg" alt="Melding fra NAV" />
                <div className="meldingData">
                    <h2>{melding.temagruppe}</h2>
                    <h3>melding.meldingstype</h3>
                    <h3>{dato}</h3>
                    <div className="fritekst">{melding.fritekst}</div>
                </div>
            </div>
        );
    }
});

function erUtgaaende(melding) {
    return Math.random() > 0.5;
}
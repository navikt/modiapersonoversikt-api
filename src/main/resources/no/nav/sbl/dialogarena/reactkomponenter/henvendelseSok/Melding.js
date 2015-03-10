var React = require('react');
var moment = require('moment');
var Utils = require('utils');
require('moment/locale/nb');
moment.locale('nb');

module.exports = React.createClass({
    render: function(){
        var melding = this.props.melding;
        var clsExt = melding.erInngaaende ? 'inngaaende' : 'utgaaende';
        var cls = 'melding clearfix ' + clsExt;
        var src = '/modiabrukerdialog/img/' + (melding.erInngaaende ? 'personligoppmote.svg' : 'nav-logo.svg');
        var altTekst = melding.erInngaaende ? 'Melding fra bruker' : 'Melding fra NAV';
        var meldingsStatusTekst = melding.statusTekst + ", " + melding.temagruppeNavn;

        var paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        var dato = moment(melding.dato || new Date()).format('LLL');
        return (
            <div className={cls}>
                <img className={'avsenderBilde '+clsExt} src={src} alt={altTekst} />
                <div className="meldingData">
                    <p>{dato} - {melding.fraBruker}</p>
                    <p className="meldingstatus">
                        <span dangerouslySetInnerHTML={{__html: meldingsStatusTekst}}></span>
                    </p>
                    <div className="fritekst">{paragrafer}</div>
                </div>
            </div>
        );
    }
});
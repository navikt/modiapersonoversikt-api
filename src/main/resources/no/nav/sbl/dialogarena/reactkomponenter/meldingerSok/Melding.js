var React = require('react/addons');
var Utils = require('./../utils');
var sanitize = require('sanitize-html');

var Melding = React.createClass({
    render: function () {
        var melding = this.props.melding;
        var clsExt = melding.erInngaaende ? 'inngaaende' : 'utgaaende';
        var cls = 'melding clearfix ' + clsExt;
        var src = '/modiabrukerdialog/img/' + (melding.erInngaaende ? 'personikon.svg' : 'nav-logo.svg');
        var altTekst = melding.erInngaaende ? 'Melding fra bruker' : 'Melding fra NAV';
        var meldingsStatusTekst = melding.statusTekst + ", ";
        if (!melding.erInngaaende) {
            meldingsStatusTekst += melding.lestStatus + " ";
        }
        meldingsStatusTekst += melding.temagruppeNavn;

        var erJournalfort = melding.journalfortTemanavn ? true : false;
        var journalfortMelding = 'Journalført av: ' + melding.journalfortAv.navn + ' (' + melding.journalfortAvNavIdent + ') | ' + melding.journalfortTemanavn;
        var journalfortVisning = !erJournalfort ? null : <div className="journalpost-element ikon">
            <span className="ikon"></span>
            <span dangerouslySetInnerHTML={{__html: journalfortMelding}}></span>
        </div>;

        var paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        paragrafer = React.addons.createFragment({
            paragrafer: paragrafer
        });

        var dato = sanitize(melding.opprettetDatoTekst || 'Fant ingen data', {allowedTags: ['em']});
        return (
            <div className={cls}>
                <img className={'avsenderBilde ' + clsExt} src={src} alt={altTekst}/>

                <div className="meldingData">
                    <article className="melding-header">
                        <p className="meldingstatus" dangerouslySetInnerHTML={{__html: meldingsStatusTekst}}></p>

                        <p dangerouslySetInnerHTML={{__html: dato}}></p>

                        <p>
                            <span>Skrevet av: </span>
                            <span dangerouslySetInnerHTML={{__html: melding.fraBruker}}></span>
                        </p>
                    </article>
                    <article className="fritekst">{paragrafer}</article>
                </div>
                {journalfortVisning}
            </div>
        );
    }
});

module.exports = Melding;
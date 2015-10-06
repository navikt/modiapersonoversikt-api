var React = require('react/addons');
var Utils = require('./../utils/utils-module');
var sanitize = require('sanitize-html');
var format = require('string-format');

var Melding = React.createClass({

    toNameCase: function (navn) {
        return navn.replace(/\b(?!em)\w+?\b/g, function (txt) {
            return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
        });
    },

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
        var journalfortMelding = format('Journalført av: {} ({}) | {} | {} | Saksid {}',
            melding.journalfortAv.navn,
            melding.journalfortAvNavIdent,
            melding.journalfortDatoTekst,
            melding.journalfortTemanavn,
            melding.journalfortSaksId);
        var journalfortVisning = !erJournalfort ? null :
            <div className="journalpost-link">
                <div className="journalpost-element ikon">
                    <span className="ikon"></span>
                    <span dangerouslySetInnerHTML={{__html: journalfortMelding}}></span>
                </div>
            </div>;

        var paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        paragrafer = React.addons.createFragment({
            paragrafer: paragrafer
        });

        var dato = sanitize(melding.opprettetDatoTekst || 'Fant ingen data', {allowedTags: ['em']});
        var skrevetMelding = format('Skrevet av: {} ({})',
            this.toNameCase(melding.skrevetAv.navn),
            melding.fraBruker);

        return (
            <div className={cls}>
                <img className={'avsenderBilde ' + clsExt} src={src} alt={altTekst}/>

                <div className="meldingData">
                    <article className="melding-header">
                        <p className="meldingstatus" dangerouslySetInnerHTML={{__html: meldingsStatusTekst}}></p>

                        <p dangerouslySetInnerHTML={{__html: dato}}></p>

                        <p>
                            <span dangerouslySetInnerHTML={{__html: skrevetMelding}}></span>
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
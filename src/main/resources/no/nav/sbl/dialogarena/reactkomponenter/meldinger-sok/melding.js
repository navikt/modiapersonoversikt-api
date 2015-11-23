import React from 'react/addons';
import Utils from './../utils/utils-module';
import sanitize from 'sanitize-html';
import format from 'string-format';

const Melding = React.createClass({
    propTypes: {
        melding: React.PropTypes.object.isRequired
    },

    toNameCase: function toNameCase(navn) {
        return navn.replace(/\b(?!em)\w+?\b/g, (txt) => txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase());
    },

    render: function render() {
        const melding = this.props.melding;
        const clsExt = melding.erInngaaende ? 'inngaaende' : 'utgaaende';
        const cls = 'melding clearfix ' + clsExt;
        const src = '/modiabrukerdialog/img/' + (melding.erInngaaende ? 'personikon.svg' : 'nav-logo.svg');
        const altTekst = melding.erInngaaende ? 'Melding fra bruker' : 'Melding fra NAV';
        let meldingsStatusTekst = melding.statusTekst + ', ';
        if (!melding.erInngaaende) {
            meldingsStatusTekst += melding.lestStatus + ' ';
        }
        meldingsStatusTekst += melding.temagruppeNavn;

        const erJournalfort = melding.journalfortTemanavn ? true : false;
        const journalfortMelding = format('Journalført av: {} ({}) | {} | {} | Saksid {}',
            melding.journalfortAv.navn,
            melding.journalfortAvNavIdent,
            melding.journalfortDatoTekst,
            melding.journalfortTemanavn,
            melding.journalfortSaksId);
        const journalfortVisning = !erJournalfort ? null :
            <div className="journalpost-link">
                <div className="journalpost-element ikon">
                    <span className="ikon"></span>
                    <span dangerouslySetInnerHTML={{__html: journalfortMelding}}></span>
                </div>
            </div>;

        let paragrafer = melding.fritekst.split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        paragrafer = React.addons.createFragment({
            paragrafer: paragrafer
        });

        const dato = sanitize(melding.opprettetDatoTekst || 'Fant ingen data', {allowedTags: ['em']});
        const skrevetMelding = format('Skrevet av: {} ({})',
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

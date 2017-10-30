import React from 'react';
import PT from 'prop-types';
import sanitize from 'sanitize-html';

class OppgaveMelding extends React.Component {

    render() {
        const { melding: { statusTekst, erInngaaende, lestStatus, visningsDatoTekst, fritekst } } = this.props;

        const clsExt = erInngaaende ? 'inngaaende' : 'utgaaende';
        const cls = `melding clearfix ${clsExt}`;
        const src = '/modiabrukerdialog/img/nav-logo.svg';
        const altTekst = 'Melding om dokument';
        let meldingOgLestStatus = `${statusTekst} - `;
        if (!erInngaaende) {
            meldingOgLestStatus += `${lestStatus} `;
        }
        const dato = sanitize(visningsDatoTekst || 'Fant ingen data', { allowedTags: ['em'] });

        return (
            <div className={cls}>
                <img className={`avsenderBilde ${clsExt}`} src={src} alt={altTekst} />
                <div className="meldingData">
                    <article className="melding-header">
                        <p className="meldingstatus" dangerouslySetInnerHTML={{ __html: meldingOgLestStatus }}></p>
                        <p>{dato}</p>
                    </article>
                    <article className="fritekst">{fritekst}</article>
                </div>
            </div>
        );
    }
}

OppgaveMelding.propTypes = {
    melding: PT.shape({
        statusTekst: PT.string,
        erInngaaende: PT.bool,
        lestStatus: PT.string,
        visningsDatoTekst: PT.string,
        fritekst: PT.string
    }).isRequired
};

export default OppgaveMelding;

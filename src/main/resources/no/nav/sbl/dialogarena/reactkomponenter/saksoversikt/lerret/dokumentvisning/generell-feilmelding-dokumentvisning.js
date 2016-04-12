import React from 'react';
import { FormattedMessage } from 'react-intl';

const GenerellFeilMeldingDokumentvisning = ({ aapneSomPDFLink }) => {
    const visPDFLink = aapneSomPDFLink ? <p className="text-center">{aapneSomPDFLink}</p> : <noscript/>;
    return (
        <div className="feilmelding-container">
            <img className="feilmelding-bakgrunn"
              src="/modiabrukerdialog/img/saksoversikt/Dummy_dokument.jpg" alt=""
            />
            <div className="feilmelding panel panel-ramme">
                <h1 className="-ikon-feil-strek teknisk-feil-ikon">
                    <FormattedMessage
                      id="dokumentvisning.pdf.feilmelding.tittel"
                    />
                </h1>
                <p className="text-center">
                    <FormattedMessage id="dokumentvisning.pdf.feilmelding.innhold"/>
                </p>
                {visPDFLink}
            </div>
        </div>
    );
};
GenerellFeilMeldingDokumentvisning.propTypes = {
    aapneSomPDFLink: React.PropTypes.func
};

export default GenerellFeilMeldingDokumentvisning;

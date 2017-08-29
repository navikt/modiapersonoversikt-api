import React from 'react';

class ArbeidssituasjonPanel extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const { tekst, arbeidsgiver } = this.props;
        return (
            <div className="arbeidssituasjon">
                <h1>{ tekst['title'] }</h1>
                <dl>
                    <dt>{ tekst['arbeidsgiver'] }</dt>
                    <dd>{ arbeidsgiver }</dd>
                </dl>
            </div>
        );
    }
}

ArbeidssituasjonPanel.propTypes = {
    arbeidsgiver: React.PropTypes.string.isRequired,
    tekst: React.PropTypes.object.isRequired,
};

export default ArbeidssituasjonPanel;

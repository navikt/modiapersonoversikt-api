import React, { PropTypes as PT } from 'react';
import SakstemaListe from './SakstemaListe';

class SakstemaPage extends React.Component {
    render() {
        const { sakstema, valgtTema, velgSak } = this.props;
        console.log('this.props', this.props);

        return (
            <div>
                <section className="saksoversikt-liste">
                    <SakstemaListe sakstema={sakstema} velgSak={velgSak}/>
                </section>
                <section className="saksoversikt-innhold">
                    <h2>Innhold</h2>
                    {valgtTema}
                </section>
            </div>
        )
    };
}

SakstemaPage.propTypes = {
    sakstema: PT.array.isRequired
};

export default SakstemaPage;


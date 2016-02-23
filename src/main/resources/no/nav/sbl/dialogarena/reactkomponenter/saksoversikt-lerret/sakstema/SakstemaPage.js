import React from 'react';
import SakstemaListe from './SakstemaListe';

class SakstemaPage extends React.Component {
    render() {
        const {store} = this.props;
        const sakstema = store.state.sakstema;
        const tekster = store.state.tekster;

        return (
            <div>
                <section className="saksoversikt-liste">
                    <SakstemaListe tekster={tekster} sakstema={sakstema} erValgt={this.props.erValgt.bind(this)}
                                   velgSak={this.props.velgSak} />
                </section>
                <section className="saksoversikt-innhold">
                    <h2>Innhold</h2>
                    {store.state.valgtTema}
                </section>
            </div>
        )
    };
}

export default SakstemaPage;


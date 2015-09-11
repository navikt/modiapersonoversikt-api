import React from 'react';
import {generateId} from '../utils';

class TypeValg extends React.Component {
    constructor(props) {
        super(props);

        this.id1=generateId("typevalg");
        this.id2=generateId("typevalg");

        this.endre = this.endre.bind(this)
    }

    componentDidMount() {
        const elementReference = 'typevalg.' + (this.props.valgtKategori || 'FAG');
        React.findDOMNode(this.refs[elementReference]).focus();
    }

    endre(event) {
        this.props.endreKategori(event.target.value);
    }

    render() {
        return (
            <div className="type-valg">
                <input name="typeVelger" id={this.id1} type="radio" checked={this.props.valgtKategori === 'FAG'} value="FAG" onChange={this.endre}/>
                <label className="label" htmlFor={this.id1} ref="typevalg.FAG">Fagsaker</label>
                <input name="typeVelger" id={this.id2} type="radio" checked={this.props.valgtKategori === 'GEN'} value="GEN" onChange={this.endre}/>
                <label className="label" htmlFor={this.id2} ref="typevalg.GEN">Generelle saker</label>
            </div>
        );
    }
}

export default TypeValg;
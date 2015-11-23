import React from 'react';
import {generateId} from '../utils/utils-module';

class TypeValg extends React.Component {
    constructor(props) {
        super(props);

        this.id1 = generateId('typevalg');
        this.id2 = generateId('typevalg');

        this.endre = this.endre.bind(this);
    }

    componentDidMount() {
        const elementReference = 'typevalg.' + (this.props.valgtKategori || 'FAG');
        React.findDOMNode(this.refs[elementReference]).focus();
    }

    endre(event) {
        this.props.endreKategori(event.target.value);
    }

    render() {
        const preventDefault = (event) => event.preventDefault();
        return (
            <div className="type-valg">
                <input name="typeVelger"
                       id={this.id1}
                       type="radio"
                       checked={this.props.valgtKategori === 'FAG'}
                       value="FAG"
                       ref="typevalg.FAG"
                       onKeyPress={preventDefault}
                       onChange={this.endre}/>
                <label className="label" htmlFor={this.id1}>Fagsaker</label>
                <input name="typeVelger"
                       id={this.id2}
                       type="radio"
                       checked={this.props.valgtKategori === 'GEN'}
                       value="GEN"
                       ref="typevalg.GEN"
                       onKeyPress={preventDefault}
                       onChange={this.endre}/>
                <label className="label" htmlFor={this.id2}>Generelle saker</label>
            </div>
        );
    }
}

TypeValg.propTypes = {
    valgtKategori: React.PropTypes.string.isRequired,
    endreKategori: React.PropTypes.func.isRequired
};

export default TypeValg;

import React from 'react';

class TypeValg extends React.Component {
    constructor(props) {
        super(props);
        this.endre = this.endre.bind(this)
    }

    endre(event) {
        this.props.endreKategori(event.target.value);
    }

    render() {
        return (
            <form className="type-valg" onChange={this.endre}>
                <input name="typeVelger" id="t1" type="radio" checked={this.props.valgtKategori === 'FAG'} value="FAG" onChange={this.endre}/>
                <label className="label" htmlFor="t1">Fagsaker</label>
                <input name="typeVelger" id="t2" type="radio" checked={this.props.valgtKategori === 'GEN'} value="GEN" onChange={this.endre}/>
                <label className="label" htmlFor="t2">Generelle saker</label>
            </form>
        );
    }
}

export default TypeValg;
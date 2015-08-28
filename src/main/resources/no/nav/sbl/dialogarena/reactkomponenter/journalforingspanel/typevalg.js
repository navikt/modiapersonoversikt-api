import React from 'react';

class TypeValg extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <form className="type-valg">
                <input name="typeVelger" type="radio" checked="checked" value="FAG"/>
                <label className="label">Fagsaker</label>
                <input name="typeVelger" type="radio" value="GEN"/>
                <label className="label">Generelle saker</label>
            </form>
        );
    }
}

export default TypeValg;
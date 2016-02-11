import React from 'react/addons';

class TemaListeKomponent extends React.Component {

    render() {
        const tema = this.props.tema;
        return (
            <div>
                <input id={"radio-"+tema} name="tema" type="radio" value={tema}/>
                <label htmlFor={"radio-"+tema}>
                    {tema}
                </label>
            </div>
        );
    }
}

export default TemaListeKomponent;
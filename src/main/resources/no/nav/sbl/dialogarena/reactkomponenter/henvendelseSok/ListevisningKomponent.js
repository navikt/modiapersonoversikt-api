var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

module.exports = React.createClass({
    statics: {
        lagAriaLabel: function (element) {
            return element.temagruppe;
        }
    },
    render: function () {
        var cls = this.props.erValgt ? "melding valgt" : "melding";
        var dato = this.props.element.datoInMillis || new Date();
        dato = moment(dato).format('LLL');

        return (
            <div {...this.props}>
                <input id={"melding" + this.props.element.key} name="tekstListeRadio" type="radio" readOnly checked={this.props.erValgt} />
                <label htmlFor={"melding" + this.props.element.key} className={cls}>
                    <header>
                        <p className={'meldingstatus ' + this.props.element.statusKlasse}>{this.props.element.statusTekst}</p>
                        <p className="opprettet">{dato}</p>
                        <h1 dangerouslySetInnerHTML={{__html: this.props.element.temagruppe}}></h1>
                    </header>
                    <p dangerouslySetInnerHTML={{__html: this.props.element.innhold}}></p>
                </label>
            </div>
        );
    }
});

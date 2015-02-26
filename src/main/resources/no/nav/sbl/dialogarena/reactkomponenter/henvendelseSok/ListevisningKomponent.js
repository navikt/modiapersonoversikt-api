var React = require('react');
var moment = require('moment');
require('moment/locale/nb');
moment.locale('nb');

window.moment = moment;

module.exports = React.createClass({
    render: function () {
        var cls = this.props.erValgt ? "melding valgt" : "melding";
        var dato = this.props.element.dato || new Date();
        dato = moment(dato).format('LLL');

        return (
            <div {...this.props}>
                <article className={cls}>
                    <header>
                        <p className={'meldingstatus ' + this.props.element.type}>{this.props.element.typeBeskrivelse}</p>
                        <p className="opprettet">{dato}</p>
                        <h1>{this.props.element.temagruppe}</h1>
                    </header>
                    <p>{this.props.element.innhold}</p>
                </article>
            </div>
        );
    }
});

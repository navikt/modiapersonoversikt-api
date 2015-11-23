import React from 'react/addons';
import Utils from './../utils/utils-module';
import ScrollPortal from './../utils/scroll-portal';
import LocaleSelect from './locale-select';

function onClickProxy(tag, event) {
    event.preventDefault();
    this.leggTilKnagg(tag);
}

class TekstForhandsvisning extends React.Component {
    render() {
        const tekst = this.props.tekst.hasOwnProperty('innhold') ? this.props.tekst : {innhold: {nb_NO: ''}, tags: []};

        const paragrafer = Utils.getInnhold(tekst, this.props.locale)
            .split(/[\r\n]+/)
            .map(Utils.leggTilLenkerTags)
            .map(Utils.tilParagraf);

        const paragraferFragment = React.addons.createFragment({
            paragrafer: paragrafer
        });

        const knagger = tekst.tags.map((tag) => {
            return (
                <button key={tag} className="knagg" onClick={onClickProxy.bind(this.props.store, tag)}>
                    <span>{'#' + tag}</span>
                </button>
            );
        });

        return (
            <div>
                <ScrollPortal className="tekstPanel" innerClassName="tekst-panel-wrapper">
                    {paragraferFragment}
                    {knagger}
                </ScrollPortal>
                <div className="velgPanel">
                    <LocaleSelect tekst={tekst} locale={this.props.locale} store={this.props.store}/>
                    <input type="submit" value="Velg tekst" className="knapp-hoved-liten" />
                </div>
            </div>
        );
    }
}

TekstForhandsvisning.propTypes = {
    'tekst': React.PropTypes.object.isRequired,
    'locale': React.PropTypes.string.isRequired,
    'store': React.PropTypes.object.isRequired
};

export default TekstForhandsvisning;

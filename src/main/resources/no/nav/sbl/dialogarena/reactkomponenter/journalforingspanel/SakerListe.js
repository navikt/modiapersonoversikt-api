import React from 'react';
import { groupBy } from 'lodash';
import { mapValues } from 'lodash';

const TIL_SAK_ELEMENT = (sak) => {
    return (
        <li>
            <span className="text-cell">{sak.saksIdVisning}</span>
            <span className="text-cell">{sak.opprettetDatoFormatert}</span>
            <span className="text-cell">{sak.fagsystemKode}</span>
        </li>
    );
};

const TIL_SAK_GROUPED = (group) => {
    console.log('group', group);
    const sakerElementer = group.map(TIL_SAK_ELEMENT);
    return (
        <div >
            <h3> {group[0].temaKode} </h3>
            <span className="text-cell">SAKSID</span>
            <span className="text-cell">OPPRETTET</span>
            <span className="text-cell">FAGSYSTEM</span>
            <ul className="list-saker">
                {sakerElementer}
            </ul>
        </div>
    );
};


class SakerListe extends React.Component {
    constructor(props) {
        super(props);
    }
    render(){
        const grouped = groupBy(this.props.saker, sak => sak.temaKode);
        console.log('groups', grouped);
        const sakerGruppert = mapValues(grouped,TIL_SAK_GROUPED);

        return (
            <div className="alla-saker">
                {sakerGruppert}
            </div>
        );
    }

    /*  render() {
     const sakerElementer = this.props.saker.map(TIL_SAK_ELEMENT);
     const grouped = groupBy(this.props.saker, sak => sak.temaKode);
     console.log('gr', grouped);
     var allaPanelSaker = document.createElement('div');
     allaPanelSaker.id='allasakerpanel';

     //document.getElementsByClassName('journalforings-panel').appendChild(panelSaker);
     for(var key in grouped){
     var currGroup ={name: key, group: grouped[key]};


     console.log(key);
     console.log(grouped[key]);
     //var entry = '<li>' + grouped[key] + '</li>';

     var allasaker = document.createElement('ul');
     allasaker.className = 'saker_liste';
     var subset_saker = grouped[key].map(TIL_SAK_ELEMENT);

     }

     return (
     <div>
     {allaPanelSaker}

     </div>

     );
     }*/
}

/*TODO:
 1. create div in journalforings-panel
 2. create ul inside that div
 3. In the for-loop add li-entries for ul
 */

//inne i div i return:
// <ul className="saker_liste">
//    {sakerElementer}
//</ul>

export
default
SakerListe;
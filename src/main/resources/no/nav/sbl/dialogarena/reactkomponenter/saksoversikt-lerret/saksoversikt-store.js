import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';
import { finnSisteOppdatering } from './finn-siste-oppdatering';


class SaksoversiktStore extends Store {
    constructor(fnr) {
        super();
        this._resourcesResolved = this._resourcesResolved.bind(this);
        const temaer = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer');
        const journalposter = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/journalposter');
        const sakstema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/sakstema');

        this.state = {
            temaer: [],
            journalposter: [],
            sakstema: [],
            promise: Q.all([temaer, journalposter, sakstema]),
            valgtTema: null
        };
        this.state.promise.done(this._resourcesResolved);
    }

    componentDidMount() {
        this.store.addListener(this.updateState);
    }

    componentWillUnmount() {
        this.store.removeListener(this.updateState);
    }

    updateState() {
        this.setState(this.store.getState());
    }

    getResources() {
        return this.state.resources;
    }

    velgTema(temakode) {
        this.state.valgtTema = temakode;
        this.fireUpdate();
    }

    _resourcesResolved([temaer, journalposter, sakstema]) {
        this.state.temaer = temaer;
        this.state.journalposter = journalposter;

        this.state.sakstema = sakstema.filter(fjernTommeTema);
        this.state.sakstema = sakstema.filter(fjernTommeTema)
            .map((tema) => {
                return {
                    temakode: tema.temakode,
                    temanavn: tema.temanavn,
                    sistOppdatertDato: finnSisteOppdatering(tema.behandlingskjeder, tema.dokumentMetadata)
                };
            });

        this.fireUpdate();
    }
}

const fjernTommeTema = tema => tema.dokumentMetadata.length > 0 || tema.behandlingskjeder.length > 0;

export default SaksoversiktStore;

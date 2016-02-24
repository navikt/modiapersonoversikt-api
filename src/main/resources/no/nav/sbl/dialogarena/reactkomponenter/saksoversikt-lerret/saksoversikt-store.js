import Store from './../utils/store';
import ResourceMap from './../utils/resource-map';
import { sortBy } from 'lodash';
import Ajax from './../utils/ajax';
import Q from 'q';
import { finnSisteOppdatering } from './utils/finn-siste-oppdatering';


class SaksoversiktStore extends Store {
    constructor(fnr) {
        super();
        this._resourcesResolved = this._resourcesResolved.bind(this);
        const temaer = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/temaer');
        const sakstema = Ajax.get('/modiabrukerdialog/rest/saksoversikt/' + fnr + '/sakstema');
        const tekster = Ajax.get('/modiabrukerdialog/rest/informasjon/tekster');
        const miljovariabler = Ajax.get('/modiabrukerdialog/rest/informasjon/miljovariabler');

        this.state = {
            tekster: {},
            miljovariabler: {},
            temaer: [],
            sakstema: [],
            promise: Q.all([temaer, sakstema, tekster, miljovariabler]),
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

    velgTema(tema) {
        this.state.valgtTema = tema;
        this.fireUpdate();
    }

    _resourcesResolved([temaer, sakstema, tekster, miljovariabler]) {
        this.state.temaer = temaer;
        this.state.sakstema = sakstema.filter(fjernTommeTema)
            .map((tema) => {
                return {
                    temakode: tema.temakode,
                    dokumentmetadata: tema.dokumentMetadata,
                    temanavn: tema.temanavn,
                    sistOppdatertDato: finnSisteOppdatering(tema.behandlingskjeder, tema.dokumentMetadata),
                    dokumentMetadata: tema.dokumentMetadata
                };
            });

        this.state.tekster = tekster;
        this.state.miljovariabler = miljovariabler;
        this.fireUpdate();
    }
}

const fjernTommeTema = tema => tema.dokumentMetadata.length > 0 || tema.behandlingskjeder.length > 0;

export default SaksoversiktStore;

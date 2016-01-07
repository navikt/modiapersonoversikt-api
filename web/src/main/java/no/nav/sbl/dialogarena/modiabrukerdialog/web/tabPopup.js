function createTabHandler(application, options) {
    var localStorageId = application + ".activeTab";
    var sessionStorageId = application + ".tabGuid";
    var isReloadingId = application + ".reloading";
    var isModalOpen = false;

    /**
     * Generates a GUID string, according to RFC4122 standards.
     * @returns {String} The generated guid
     * @example eff5d22c-74fa-7139-d4e8-6879736b04fa
     * @author Slavik Meltser (slavik@meltser.info)
     * @link http://slavik.meltser.info/?p=142
     */
    var createGuid = function () {
        function _p8(s) {
            var p = (Math.random().toString(16) + "000000000").substr(2, 8);
            return s ? "-" + p.substr(0, 4) + "-" + p.substr(4, 4) : p;
        }

        return _p8(false) + _p8(true) + _p8(true) + _p8(false);
    };

    var getTabGuid = function () {
        var tabGuid = window.sessionStorage.getItem(sessionStorageId);
        if (!tabGuid) {
            tabGuid = createGuid();
            window.sessionStorage.setItem(sessionStorageId, tabGuid);
        }
        return tabGuid;
    };

    var getCurrentActiveTab = function () {
        return window.localStorage.getItem(localStorageId);
    };

    var setActiveTab = function (tabGuid) {
        window.localStorage.setItem(localStorageId, tabGuid);
    };

    var activateTab = function () {
        setActiveTab(getTabGuid());
        setIsReloadingFlag();
        window.location.reload();
    };

    var closeTab = function () {
        //funker i IE og Chrome
        //funker ikke i Firefox
        window.open('', '_self', '');
        window.close();
    };

    var isReloading = function () {
        var isReloading = window.sessionStorage.getItem(isReloadingId);
        return isReloading === "true";
    };

    var setIsReloadingFlag = function () {
        window.sessionStorage.setItem(isReloadingId, "true");
    };

    var clearIsReloadingFlag = function () {
        window.sessionStorage.removeItem(isReloadingId);
    };

    var isModalDialogVisible = function () {
        return isModalOpen;
    };

    var createModalDialog = function () {
        var modalComponent = ModiaJS.React.createElement(
            ModiaJS.Components.FeilmeldingsModaler.FlereApneVinduer,
            {
                isOpen: true,
                hovedtekst: options.hovedtekst,
                avbryttekst: options.avbryttekst,
                fortsetttekst: options.fortsetttekst,
                avbrytCallback: closeTab,
                fortsettCallback: activateTab
            }
        );
        ModiaJS.React.render(modalComponent, document.getElementById("feilmeldingsmodaler"));
        isModalOpen = true;
    };
    window.test = createModalDialog;

    var storageEventListener = function (e) {
        var tabGuid;
        if (e.key === localStorageId) { // bryr oss bare om localStorage events
            if (!e.newValue) { // hvis ny verdi er null eller tom, ignorer event
                return;
            }
            tabGuid = getTabGuid();
            if (e.newValue !== tabGuid && !isModalDialogVisible() && !isReloading()) {  // dette er en forskjellig verdi og vi ikke viser dialog fra før, og vi ikke er i en refresh, vis dialog
                createModalDialog();
            }
        }
    };

    var unloadListener = function () {
        var tabGuid = getTabGuid();
        var currentActiveTab = getCurrentActiveTab();
        if (tabGuid == currentActiveTab) { //hvis det er aktiv tab som lukkes, slette fra localStorage
            window.localStorage.removeItem(localStorageId);
        }
    };

    var init = function () {
        var tabGuid = getTabGuid();
        var currentActiveTab = getCurrentActiveTab();
        if (isReloading()) {
            setActiveTab(tabGuid);
        } else {
            if (!currentActiveTab) { //ingen aktiv tab fra før, setter denne som aktiv
                setActiveTab(tabGuid);
                currentActiveTab = tabGuid;
            }
            if (currentActiveTab !== tabGuid) {//Aktiv tab er en annen en denne. Spør bruker om vi skal bruke denne tab
                createModalDialog();
            }
        }
        window.addEventListener("storage", storageEventListener);
        window.addEventListener("unload", unloadListener);
        clearIsReloadingFlag();
    };

    init();
}
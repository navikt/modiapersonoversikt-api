jQuery(document).ready(function ($) {
    'use strict';

    createTabHandler("modiabrukerdialog");

    if ($('.main-content .lamell').length === 1) {
        $('.main-content .lamell').first().find('.lamellhode').hide();
    }

    $('#toggle-personsok').on('click', toggleAvansertSok);
    Modig.shortcutListener.on({key: 'A'}, toggleAvansertSok);

    Modig.shortcutListener.on({alt: true, keyCode: 114}, focusSearchField); // F3
    Modig.shortcutListener.on({alt: true, keyCode: 116}, closeResetPerson); // F5
    Modig.shortcutListener.on({alt: true, keyCode: 117}, focusLamellHead);  // F6
    Modig.shortcutListener.on({alt: true, keyCode: 118}, closeLamellHead);  // F7


    addPrintEventListener();

    $('body').on('click', '.lamell .lamellhode > a', function () {
        if ($('.main > .personsok').is(':visible')) {
            toggleAvansertSok();
        }
    });

    Modig.shortcutListener.on({alt: false, shift: true, keyCode: 13}, gjennomfoerAvansertSok);

    // IE9 fiks til søk-knapp i personsøk pga bugg med :hover på anet en A-element
    $('.search-intern input[type=button]').hover(function () {
        $(this).addClass('hover');
    }, function () {
        $(this).removeClass('hover');
    });

    /** Registrer ajax loadere **/

    Modig.ajaxLoader.register({
        urlPattern: /utvidetPersonsokForm-hiddenSumbitLink/,
        placeElement: '#personsokResult',
        imageUrl: '/modiabrukerdialog/img/ajaxloader/svart/loader_svart_96.gif',
        css: 'margin-bottom: 20px;'
    });

    var submitFodselsnummer = $('#submitFodselsnummer');
    if (submitFodselsnummer.length > 0) {
        var scaling = 0.8;
        var height = submitFodselsnummer.innerHeight();
        var posDiff = (height * (1 - scaling) / 2);
        var top = submitFodselsnummer.position().top + posDiff + "px";
        var left = submitFodselsnummer.position().left + posDiff + "px";
        height = height * scaling;

        Modig.ajaxLoader.register({
            urlPattern: /searchPanel-hentPersonForm-submitFodselsnummer/,
            placeElement: '#submitFodselsnummer',
            placement: 'hide',
            imageUrl: '/modiabrukerdialog/img/ajaxloader/hvit/loader_hvit_48.gif',
            css: 'position: absolute; top:' + top + '; left:' + left + '; height:' + height + 'px'
        });
    }

    settHovedinnholdHoyde();
});

function focusSearchField() {
    $('#foedselsnummerInput').focus()
}

function focusLamellHead() {
    $('.lamell.selected .lamellhode a').focus();
}

function closeLamellHead() {
    $('.lamell.selected button.close').click();
}

function closeResetPerson() {
    $('.nullstill-button').click();
}

function toggleAvansertSok() {
    var personsokElement = $('#personsok'), toppmeny = $('.main > .menu-intern'),
        fornavnInput = $('#utvidetPersonsokForm').find('input[name="fornavn"]'), sokKnapp = $('#toggle-personsok'),
        dataApne = sokKnapp.attr('data-apne'),
        dataLukke = sokKnapp.attr('data-lukke');
    if (personsokElement.is(':visible')) {
        personsokElement.hide();
        toppmeny.removeClass('active');
        sokKnapp.removeClass('active');
        sokKnapp.attr('title', dataApne);
        sokKnapp.attr('aria-label', dataApne);
    } else {
        personsokElement.show();
        toppmeny.addClass('active');
        sokKnapp.addClass('active');
        sokKnapp.attr('title', dataLukke);
        sokKnapp.attr('aria-label', dataLukke);

        if (fornavnInput.length != 0) {
            fornavnInput.focus();
        }
    }
}

function gjennomfoerAvansertSok() {
    var personsokElement = $('.main > .personsok');
    if (personsokElement.is(":visible")) {
        $('#utvidetPersonsokForm:visible').submit();
    }
}

function setSessionTimeoutBox() {
    var timeoutValue = 1000 * 60 * 55;
    var timeout = setTimeout(function () {
        createTimeoutBox();
    }, timeoutValue);

    Wicket.Event.subscribe('/ajax/call/after', function () {
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            createTimeoutBox();
        }, timeoutValue);
    });
}

function createTimeoutBox() {
    if (!$('.wicket-mask-dark')[0]) {
        $('body').append($('<div/>').addClass('wicket-mask-dark'));
    }
    $('.informasjonsboks.timeout').show();
}

function createTabHandler(application) {
    var localStorageId = application + ".activeTab";
    var sessionStorageId = application + ".tabGuid";
    var isReloadingId = application + ".reloading";
    var greyBackground = '<div class="wicket-mask-dark" style="z-index: 20000; background-image: none; position: absolute; top: 0px; left: 0px;"></div>';
    var modalDialog = '<div class="wicket-modal" id="_wicket_window_0" tabindex="-1" role="dialog" style="position: absolute; width: 600px; left: 660px; top: 180.5px; visibility: visible;"><form><div class="w_content_1" ><div class="w_caption" id="_wicket_window_1"><a class="w_close" role="button" id="closeModalButton" style="z-index:1" href="#"></a></div><div id="_wicket_window_2" class="w_content_container" style="overflow: auto; height: 300px;"><div id="content5c"><section class="bekreft-dialog"><h1 class="robust-ikon-hjelp-strek">Du har allerede et vindu med ModiaBrukerDialog åpent. Hvis du fortsetter i dette vinduet så vil du miste ulagret arbeide i det andre vinduet. Ønsker du å fortsette med dette vinduet?</h1><ul><li><submit class="knapp-stor" id="confirmCloseTab" >Avbryt, jeg vil ikke miste ulagret arbeide</submit></li><li><a id="confirmActivateTab" href="#">Fortsett med dette vinduet</a></li></ul></section></div></div></div></form></div>';

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
        removeModalDialog();
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
        var backGround = $('.wicket-mask-dark');
        return backGround.length != 0;

    };

    var createModalDialog = function () {
        $("body").append(modalDialog).append(greyBackground);
        $("#confirmActivateTab").click(activateTab);
        $('#confirmCloseTab').click(closeTab);
        $('#closeModalButton').click(closeTab);
    };

    var removeModalDialog = function () {
        $('.wicket-mask-dark').remove();
        $('wicket-modal').remove();
    };

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

function prepareElementForPrint(element, additionalClass) {
    if (additionalClass) {
        $('.print').addClass(additionalClass);
    }
    $('.print .content').append(element.clone());
}

function addPrintEventListener() {
    var called = 0; // Chrome kjører listeneren 2 ganger, men vi vil bare kjøre beforePrint første gang og afterPrint siste gang de kjører
    var print = $('.print');
    var printContent = print.find('.content');

    function afterPrint() {
        if (window.chrome) {
            if (called === 0) {
                called = called + 1;
                return;
            }
            called = 0;
        }
        printContent.empty();
        print.attr('class', 'print');
    }

    function beforePrint() {
        if (window.chrome && called === 0) {
            return;
        }
        var selectedLamell = $('.lamell.selected');
        if (printContent.children().length === 0 && !selectedLamell.hasClass('oversikt')) {
            prepareElementForPrint(selectedLamell);
        }
    }

    if (window.matchMedia) {
        var mediaQueryList = window.matchMedia('print');
        mediaQueryList.addListener(function (mql) {
            if (mql.matches) {
                beforePrint();
            } else {
                afterPrint();
            }

        });
    } else {
        window.onafterprint = afterPrint;
        window.onbeforeprint = beforePrint;
    }
}

function settHovedinnholdHoyde() {
    settHoyde();
    $(window).resize(settHoyde);

    function settHoyde() {
        $('.main-content').css('min-height', $('html').height() - $('.navbar ').outerHeight());
    }
}


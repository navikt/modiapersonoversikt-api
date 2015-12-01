jQuery(document).ready(function ($) {
    'use strict';

    Modig.lagScrollbars();

    $('#toggle-personsok').on('click', toggleAvansertSok);
    Modig.shortcutListener.on({key: 'A'}, toggleAvansertSok);

    Modig.shortcutListener.on({alt: true, keyCode: 114}, focusSearchField); // F3
    Modig.shortcutListener.on({alt: true, keyCode: 116}, closeResetPerson); // F5
    Modig.shortcutListener.on({alt: true, keyCode: 117}, focusLamellHead);  // F6
    Modig.shortcutListener.on({alt: true, keyCode: 118}, closeLamellHead);  // F7
    Modig.shortcutListener.on({alt: true, keyCode: 71}, openGosys);  // Alt+g
    Modig.shortcutListener.on({alt: true, keyCode: 80}, openArenaPersonmappe);  // Alt+p
    Modig.shortcutListener.on({alt: true, keyCode: 89}, openArenaUtbetalinger);  // Alt+y
    Modig.shortcutListener.on({alt: true, keyCode: 73}, openPesys);  // Alt+i
    Modig.shortcutListener.on({alt: true, keyCode: 67}, openSkrivestotte);  // Alt+c
    Modig.shortcutListener.on({alt: true, keyCode: 81}, openInnboksSok);  // Alt+q

    addPrintEventListener();

    $('body').on('click', '.lamell .lamellhode > a', function () {
        if ($('.main > .personsok').is(':visible')) {
            toggleAvansertSok();
        }
    });

    // Åpner for bruk av hotkey mens man skriver
    $('.sidebar-hoyre').on('keydown', '.expandingtextarea', function (event) {
        if (event.altKey && event.keyCode === 67 /* alt + c */) {
            openSkrivestotte();
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

function openGosys() {
    $('.hiddenGosysLenkePanel').click();
}

function openPesys() {
    $('.hiddenPesysLenkePanel').click();
}

function openArenaPersonmappe() {
    $('.hiddenArenaPersonmappeLenkePanel').click();
}

function openArenaUtbetalinger() {
    $('.hiddenArenaUtbetalingLenkePanel').click();
}

function openSkrivestotte() {
    $('.skrivestotteToggle').click();
}

function openInnboksSok() {
    $('.innboksSokToggle button').click();
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

function setSessionTimeoutBox(timeoutBox) {
    var timeoutValue = 1000 * 60 * 55;
    var timeout = setTimeout(function () {
        timeoutBox.vis();
    }, timeoutValue);

    Wicket.Event.subscribe('/ajax/call/after', function () {
        clearTimeout(timeout);
        timeout = setTimeout(function () {
            timeoutBox.vis();
        }, timeoutValue);
    });
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

    if (window.onafterprint === undefined) {
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

function delayed(func, time) {
    time = time || 0;
    var deferred = $.Deferred();

    setTimeout(function () {
        deferred.resolve(func());
    }, time);

    return deferred.promise();
}
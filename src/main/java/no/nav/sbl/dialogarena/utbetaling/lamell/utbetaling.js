var Utbetalinger = (function () {
    var aapneUtbetalingText = 'Ekspander';
    var lukkUtbetalingText = 'Minimer';
    var ENTER_KEYCODE = 13;
    var HOYDE_MAANED_HEADER = 26;

    var init = function () {
        addKeyNavigation();

        // Event listeners
        $(document).on('keydown', '.ekspander-pil', function (event) {
            if (event.which === ENTER_KEYCODE) {
                Utbetalinger.toggleDetaljPanel($(this));
                event.stopPropagation();
            }
        });

        toggleTotaltUtbetalt();
        toggleUtbetaling();
        toggleFiltrering();
    };

    var toggleTotaltUtbetalt = function () {
        // Totalt utbetalt
        $(document).on('keydown', '.utbetaling-ramme-innhold.oppsummering-total', function (event) {
            if (event.which === ENTER_KEYCODE) {
                Utbetalinger.toggleDetaljPanel($(this));
            }
        });

        $(document).on('click', '.utbetaling-ramme-innhold.oppsummering-total', function () {
            Utbetalinger.toggleDetaljPanel($(this));
        });
    };

    var toggleUtbetaling = function () {

        $(document).on('keydown', '.utbetaling-ramme-innhold .utbetalingslinje', function (event) {
            if (event.which === ENTER_KEYCODE) {
                Utbetalinger.toggleDetaljPanel($(this));
            }
        });

        $(document).on('click', '.utbetaling-ramme .utbetalingslinje', function () {
            Utbetalinger.toggleDetaljPanel($(this));
        });
    };

    var toggleFiltrering = function () {
        $(document).on('click', '.ekspander-pil-filtrering', function () {
            $('#filter-content').slideToggle();
            $('#filter-content').parent().toggleClass('skjul-innhold');
            ariaLabel($('#filter-content').parent());
        });

        var ariaLabel = function ($element) {
            if ($element.hasClass('skjul-innhold')) {
                console.log('has class');
                $element.find('.ekspander-pil-filtrering span').text(aapneUtbetalingText);
                $element.find('.ekspander-pil-filtrering').attr('aria-label', aapneUtbetalingText);
                $element.find('.ekspander-pil-filtrering').attr('title', aapneUtbetalingText);
            } else {
                console.log('nope');
                $element.find('.ekspander-pil-filtrering span').text(lukkUtbetalingText);
                $element.find('.ekspander-pil-filtrering').attr('aria-label', lukkUtbetalingText);
                $element.find('.ekspander-pil-filtrering').attr('title', lukkUtbetalingText);
            }
        };
    };

    var addKeyNavigation = function () {
        $('.utbetaling-ramme').addKeyNavigation({itemsSelector: '.utbetalingslinje'});
    };

    var haandterDetaljPanelVisning = function (detaljPanelID) {
        var $detaljPanel = $("#" + detaljPanelID);
        scrollTilElement($detaljPanel);

        $detaljPanel.animate({height: 'toggle'}, 900);
        $detaljPanel.parent().toggleClass('ekspandert');
        $detaljPanel.parent().focus();
        toggleEkspandertHjelpetekst($detaljPanel.parent());
    };

    var toggleDetaljPanel = function ($element) {
        $element.toggleClass('ekspandert');
        $element.children('.detaljpanel').animate({height: 'toggle'}, 200);

        if ($element.hasClass('ekspandert')) {
            scrollTilElement($element);
        }

        toggleEkspandertHjelpetekst($element);
    };

    var toggleEkspandertHjelpetekst = function ($element) {
        if ($element.hasClass('ekspandert')) {
            $element.find('.ekspander-pil span').text(lukkUtbetalingText);
            $element.find('.ekspander-pil').attr('aria-label', lukkUtbetalingText);
            $element.find('.ekspander-pil').attr('title', lukkUtbetalingText);
        } else {
            $element.find('.ekspander-pil span').text(aapneUtbetalingText);
            $element.find('.ekspander-pil').attr('aria-label', aapneUtbetalingText);
            $element.find('.ekspander-pil').attr('title', aapneUtbetalingText);
        }
    };

    var skrivUt = function ($element) {
        var $printCopy = $element.clone();
        $printCopy.children('.detaljpanel').css('display', 'block');
        $printCopy.css('padding', '0');
        kopierOgSkrivUt($('<div>').append($printCopy).html());
    };

    function kopierOgSkrivUt(html) {
        var ddmmyyyy = finnUtskriftsdato();
        var urlPathname = window.location.pathname;
        var fnr = urlPathname.split("person/")[1] || "";
        var printerInformasjon = '<p>Utskriftsdato: ' + ddmmyyyy + '</p>' + '<p>Brukers fødselsnummer: ' + fnr + '</p>';

        $('body > .print .content')
            .html('<div class="utbetalinger">' + printerInformasjon + html + '</div>')
            .css('padding-top', '2rem');

        $('body > .print .dato-utskrift #dato').text(ddmmyyyy);
        $('body > .print').css('padding-top', '5rem');
        window.print();
    }

    function finnUtskriftsdato() {
        var date = new Date();
        var day = date.getDate();
        var month = date.getMonth() + 1;
        var year = date.getFullYear();
        return ((day < 10 ? '0' : '') + day) + '.' + ((month < 10 ? '0' : '') + month) + '.' + year;
    }

    var visSnurrepipp = function () {
        $('#ajax-indikator').css('display', 'block');
    };

    var skjulSnurrepipp = function () {
        $('#ajax-indikator').css('display', 'none');
    };

    var scrollTilElement = function($element) {
        var $utbetalingslinje = $element.closest('.utbetalingslinje');
        var $lerret = $utbetalingslinje.closest('.lerret');
        var scrollPos = $lerret.get(0).scrollTop + $utbetalingslinje.position().top - HOYDE_MAANED_HEADER;
        $lerret.animate({scrollTop: scrollPos});
    };

    return {
        init: init,
        addKeyNavigation: addKeyNavigation,
        haandterDetaljPanelVisning: haandterDetaljPanelVisning,
        toggleDetaljPanel: toggleDetaljPanel,
        toggleEkspandertHjelpetekst: toggleEkspandertHjelpetekst,
        skrivUt: skrivUt,
        visSnurrepipp: visSnurrepipp,
        skjulSnurrepipp: skjulSnurrepipp
    };
})();

$(document).ready(function () {
    Utbetalinger.init();
});
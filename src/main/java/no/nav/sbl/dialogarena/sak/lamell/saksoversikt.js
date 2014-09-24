function addKvitteringsPanelEvents() {
    addExpandClickEvent();
    addPrintClickEvent();
}

function addPrintClickEvent() {
    $('.saksoversikt .skriv-ut').click(function(event) {
        event.preventDefault();
        event.stopPropagation();
        prepareElementForPrint($(this).closest('.behandling-informasjon'), 'saksoversikt');
        window.print();
    });
}

function addExpandClickEvent() {
    $('.saksoversikt .kollapsbar').each(function() {
        var $kollapsbar, $toggleKollapsbar;

        $kollapsbar = $(this);
        $toggleKollapsbar = $kollapsbar.siblings('.toggle-kollapsbar');
        addExpandOnClickListener($toggleKollapsbar, $kollapsbar);
    });
}

function oppdaterSaksinformasjonSynlighet() {
    $(".behandling-container").removeClass("usynlig");
    $(".saksinformasjon-container").addClass("usynlig");
    $("#behandlingerListeLenke").addClass("active");
    $("#saksinformasjonLenke").removeClass("active");
    if ($(".aktiv .saksinformasjon-container").length > 0) {
        $("#saksinformasjonLenke").removeClass("usynlig");
    } else {
        $("#saksinformasjonLenke").addClass("usynlig");
    }
}

function addLamellTemaOnClickListeners() {
    function visBehandling(sakstema) {
        $(".sak-informasjon > ul > li").removeClass("aktiv");
        if(sakstema != undefined) {
            $("#behandling_"+sakstema).addClass("aktiv");
        }
        oppdaterSaksinformasjonSynlighet();
    }

    $(".sak-navigering > ul > li > a").click(function(event) {
        event.preventDefault();
        event.stopPropagation();
        var $el = $(event.currentTarget);

        $(".sak-navigering > UL > LI.aktiv").removeClass("aktiv");
        $el.parent("li").addClass("aktiv");

        visBehandling($el.context.hash.substr(1));
        $el.focus();
    });
}

function addSaksinformasjonClickListeners() {
    $("#behandlingerListeLenke").click(function(event) {
        event.preventDefault();
        event.stopPropagation();

        $(".behandling-container").removeClass("usynlig");
        $(".saksinformasjon-container").addClass("usynlig");

        $("#behandlingerListeLenke").addClass("active");
        $("#saksinformasjonLenke").removeClass("active");

    });

    $("#saksinformasjonLenke").click(function(event) {
        event.preventDefault();
        event.stopPropagation();

        $(".saksinformasjon-container").removeClass("usynlig");
        $(".behandling-container").addClass("usynlig");

        $("#saksinformasjonLenke").addClass("active");
        $("#behandlingerListeLenke").removeClass("active");
    });
}

function addExpandOnClickListener(toggleElement, expandableElement) {
    toggleElement.click(function(event) {
        event.preventDefault();
        event.stopPropagation();

        expandableElement.slideToggle(300);
        $(this).find('span').toggle(0);
        return false;
    });
}

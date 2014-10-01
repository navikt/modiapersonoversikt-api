function initSaksoversikt(element, hotkey) {
    addOnClickListeners();
    new Modig.Modia.SaksoversiktView("#"+element, hotkey);
}

function addKvitteringsPanelEvents() {
    addExpandClickEvent();
    addPrintClickEvent();
}

function addOnClickListeners() {
    addLamellTemaOnClickListeners();
    addSaksinformasjonClickListeners();
    oppdaterSaksinformasjonSynlighet();
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
    $(".saksinformasjon-container").addClass("hide");
    $(".behandling-container").removeClass("hide");

    $(".saksinformasjon-lenker a").first().addClass("active");
    $(".saksinformasjon-lenker a").last().removeClass("active");

    if ($(".aktiv .saksinformasjon-container").text().trim().length > 0) {
        $(".saksinformasjon-lenker").removeClass("hide");
    } else {
        $(".saksinformasjon-lenker").addClass("hide");
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
    $(".saksinformasjon-lenker a").click(function(event) {
        event.preventDefault();
        event.stopPropagation();
        $(".behandling-container, .saksinformasjon-container").toggleClass("hide");
        $(".saksinformasjon-lenker a").toggleClass("active");
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

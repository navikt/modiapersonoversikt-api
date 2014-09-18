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

function addLamellTemaOnClickListeners() {
    function visBehandling(sakstema) {
        $(".sak-informasjon > ul > li").removeClass("aktiv");
        if(sakstema != undefined) {
            $("#behandling_"+sakstema).addClass("aktiv");
        }
    }

    $(".sak-navigering > ul > li > a").click(function(event, notClearFocus) {
        event.preventDefault();
        event.stopPropagation();
        var $el = $(event.currentTarget);

        $(".sak-navigering > UL > LI.aktiv").removeClass("aktiv");
        $el.parent("li").addClass("aktiv");

        visBehandling($el.context.hash.substr(1));
        if(notClearFocus != true) {
            $(".saksoversikt > header.lamellhode a").focus();
        }
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

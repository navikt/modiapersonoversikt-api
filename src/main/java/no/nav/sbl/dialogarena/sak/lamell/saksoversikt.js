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
        $el.trigger("updateaktivttema");

        visBehandling($el.context.hash.substr(1));
        $el.focus();
    });

    $("a.oppdater-innhold").click(function(event) {
        window.updateAnimation = true;
        rotate(0);
    });
}

function stopRotation() {
    window.updateAnimation = false;
}

function rotate(index) {
    degPerUpdate = 6;
    var objectToRotate = $(".oppdater-innhold > IMG")[0];
    var rotateString = "rotate(" + (index++*degPerUpdate)%360 + "deg)";
    objectToRotate.style.transform = rotateString;
    objectToRotate.style.msTransform = rotateString;
    objectToRotate.style.MozTransform = rotateString;
    objectToRotate.style.WebkitTransform = rotateString;

    if(window.updateAnimation || index*degPerUpdate < 180 ) {
        window.rotateTimer = setTimeout(function () {
            rotate(index);
        }, 30);
    }
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

window.Modig = window.Modig || {};

Modig.scrollOptions = {
    theme: '3d',
    scrollbarPosition: 'outside',
    alwaysShowScrollbar: 0,
    live: 'true',
    scrollInertia: 100,
    keyboard: false,
    advanced: {
        autoScrollOnFocus: "input,textarea,select,button,datalist,keygen,a[tabindex],area,object,[contenteditable='true'],article[tabindex]"
    }
};

Modig.lagScrollbars = function () {
    $('.sidebar-venstre').mCustomScrollbar(Modig.scrollOptions);
    $('.sidebar-hoyre').mCustomScrollbar(Modig.scrollOptions);

    //Alle lerret med unntak av meldinger og saksoversikt
    $('.lamell:not(.meldinger):not(.saksoversikt) .lerret').mCustomScrollbar(Modig.scrollOptions);

    //For meldinger lamell
    $('.lamell.meldinger .meldingsliste').mCustomScrollbar(Modig.scrollOptions);
    $('.lamell.meldinger .traadvisning').mCustomScrollbar(Modig.scrollOptions);

    //For saksoversikt lamell
    $('.lamell.saksoversikt .sak-navigering').mCustomScrollbar(Modig.scrollOptions);
    $('.lamell.saksoversikt .sak-informasjon').mCustomScrollbar(Modig.scrollOptions);

    //Modalvinduer
    $('.sok-layout .sok-liste').mCustomScrollbar($.extend({},Modig.scrollOptions, {scrollbarPosition: 'inside'}));
};
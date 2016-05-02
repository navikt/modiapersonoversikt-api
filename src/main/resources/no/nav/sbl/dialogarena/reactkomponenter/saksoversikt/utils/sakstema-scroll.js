import Utils from './../../utils/utils-module';

export const sakstemaScroll = (event, props) => {
    const elements = document.querySelectorAll(".saksoversikt-liste-element");
    const parent = document.querySelector(".saksoversikt-liste");
    const valgtTema = props.valgtTema;

    let index = 0;
    switch (event.keyCode) {
        case 38:
            event.preventDefault();

            index = props.sakstema.indexOf(valgtTema) - 1 < 0 ? props.sakstema.length - 1 : props.sakstema.indexOf(valgtTema) -1;
            props.velgSak(props.sakstema[index]);

            Utils.adjustScroll(parent, elements[index]);

            break;
        case 40:
            event.preventDefault();

            index = props.sakstema.indexOf(valgtTema) + 1 >= props.sakstema.length? 0 : props.sakstema.indexOf(valgtTema) + 1;
            props.velgSak(props.sakstema[index]);

            Utils.adjustScroll(parent, elements[index]);
            break;
        default:
    }
};

export const scrollTilDokument = (props) => {
    const scrollToDokumentId = props.scrollToDokumentId;
    const element = document.querySelector(`#a${scrollToDokumentId}`);
    const parent = document.querySelector('.saksoversikt-innhold');

    if (element && parent) {
        setTimeout(() => {
            parent.scrollTop = element.offsetTop - 200;
        }, 0);
    }
    props.purgeScrollId();
};


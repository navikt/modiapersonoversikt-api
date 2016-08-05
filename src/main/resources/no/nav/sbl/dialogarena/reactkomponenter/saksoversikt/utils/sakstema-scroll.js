import Utils from './../../utils/utils-module';

function settFokusPaRadioButton(element) {
    element.querySelector('input[type=radio]').focus();
}

export const pilnavigeringScroll = (event, props) => {
    const elements = document.querySelectorAll('.saksoversikt-liste-element');
    const parent = document.querySelector('.saksoversikt-liste');
    const valgtTema = props.valgtTema;

    let index;
    switch (event.keyCode) {
        // Pil opp
        case 38:
            event.preventDefault();

            index = props.sakstema.indexOf(valgtTema) - 1 < 0 ? props.sakstema.length - 1 : props.sakstema.indexOf(valgtTema) - 1;
            props.velgSak(props.sakstema[index]);

            settFokusPaRadioButton(elements[index]);
            Utils.adjustScroll(parent, elements[index]);
            break;
        // Pil ned
        case 40:
            event.preventDefault();

            index = props.sakstema.indexOf(valgtTema) + 1 >= props.sakstema.length ? 0 : props.sakstema.indexOf(valgtTema) + 1;
            props.velgSak(props.sakstema[index]);

            settFokusPaRadioButton(elements[index]);
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


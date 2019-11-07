const NAV_FAGOMRAADE = [
    'generell',
    'arbeid',
    'bidrag',
    'helse'
];
const NAV_TEMA = [
    'aap',
    'arbeidsavklaringspenger',
    'bil',
    'dagpenger',
    'hjelpemiddelsentralen',
    'hjelpemidler',
    'jobbsøkere',
    'oppfølging',
    'sykepenger'
];

const createTekstValgtEvent = ({ tags }) => {
    const fagomraade = createTagForKeywords(tags, NAV_FAGOMRAADE);
    const tema = createTagForKeywords(tags, NAV_TEMA);
    const fields = createFieldsForEveryTag(tags);

    return {
        name: 'modiabrukerdialog.skrivestotte.tekstValgt',
        fields,
        tags: {
            ...(fagomraade ? {fagomraade} : { fagomraade: "ukjent" }),
            ...(tema ? {tema} : { tema: "ukjent" })
        }
    }
};
const createTagForKeywords = (tags, keywords) => (tags && keywords) ?
    tags.filter(tag => keywords.includes(tag.toLowerCase()))
    .sort() 
    .join(',') : '';

const createFieldsForEveryTag = (tags) =>
    tags.reduce((acc, tag) => ({...acc, [tag]: 1}), {});

const trackUsage = (valgtTekst) => {
    const event = createTekstValgtEvent(valgtTekst);
    window.frontendlogger && window.frontendlogger.event(
        event.name,
        event.fields,
        event.tags
    );
    return event;
};

export default {
    trackUsage
};
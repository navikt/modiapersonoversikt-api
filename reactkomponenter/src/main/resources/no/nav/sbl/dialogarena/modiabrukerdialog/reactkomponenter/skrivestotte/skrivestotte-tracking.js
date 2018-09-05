const NAV_FAGOMRAADE = ['arbeid', 'helse'];
const NAV_TEMA = ['dagpenger', 'sykepenger', 'jobbsøkere', 'oppfølging', 'arbeidsavklaringspenger'];

const createTekstValgtEvent = ({ tags }) => {
    const fagomraade = createTagForKeywords(tags, NAV_FAGOMRAADE);
    const tema = createTagForKeywords(tags, NAV_TEMA);
    const fields = createFieldsForEveryTag(tags);

    return {
        name: 'modiabrukerdialog.skrivestotte.tekstValgt',
        fields,
        tags: {
            ...(fagomraade && {fagomraade}),
            ...(tema && {tema})
        }   
    }
};
const createTagForKeywords = (tags, keywords) => tags
    .filter(tag => keywords.includes(tag))
    .join(',');

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
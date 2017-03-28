package no.nav.sbl.modiabrukerdialog.pip.geografisk;

import no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.domain.norg.Arbeidsfordeling;
import no.nav.sbl.modiabrukerdialog.pip.geografisk.support.EnhetAttributeLocatorDelegate;
import org.jboss.security.xacml.interfaces.XACMLConstants;
import org.jboss.security.xacml.locators.AttributeLocator;
import org.jboss.security.xacml.sunxacml.EvaluationCtx;
import org.jboss.security.xacml.sunxacml.attr.*;
import org.jboss.security.xacml.sunxacml.cond.EvaluationResult;
import org.jboss.security.xacml.util.JBossXACMLUtil;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static no.nav.nav.sbl.dialogarena.modiabrukerdialog.api.config.ApplicationContextProvider.context;


/**
 * PIP for henting av lokal og fylkes enheter for en saksbehandler.
 */
public class EnhetAttributeLocator extends AttributeLocator {

    public static final URI STRING_TYPE = URI.create("http://www.w3.org/2001/XMLSchema#string");
    public static final URI SUBJECT_CATEGORY = URI.create(AttributeDesignator.SUBJECT_CATEGORY_DEFAULT);
    public static final URI SUBJECT_ID = URI.create(XACMLConstants.ATTRIBUTEID_SUBJECT_ID);
    public static final URI ATTRIBUTEID_SUBJECT_VALGT_ENHET = URI.create("urn:oasis:names:tc:xacml:1.0:subject:valgt-enhet");
    public static final URI ATTRIBUTEID_LOCAL_ENHET = URI.create("urn:nav:ikt:tilgangskontroll:xacml:subject:localenhet");
    public static final URI ATTRIBUTEID_FYLKESENHET = URI.create("urn:nav:ikt:tilgangskontroll:xacml:subject:fylkesenhet");
    public static final URI ATTRIBUTEID_GEOGRAFISK_NEDSLAGSFELT = URI.create("urn:nav:ikt:tilgangskontroll:xacml:subject:geografisk-nedslagsfelt");
    public static final URI ATTRIBUTEID_ROLLE = URI.create("urn:oasis:names:tc:xacml:2.0:subject:role");


    private EnhetAttributeLocatorDelegate delegate;

    public EnhetAttributeLocator() {
        this.attributeDesignatorSupported = true;
        this.attributeSelectorSupported = true;
        this.designatorTypes.add(0);
        this.delegate = context.getBean(EnhetAttributeLocatorDelegate.class);
    }

    @Override
    public EvaluationResult findAttribute(URI attributeType, URI attributeId, URI issuer, URI subjectCategory, EvaluationCtx context, int designatorType) {
        if (!this.ids.contains(attributeId)) {
            if (attributeType != null) {
                return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
            }
            return new EvaluationResult(BagAttribute.createEmptyBag(attributeId));
        }

        String subjectId = getSubjectId(context);
        Set<AttributeValue> values = Collections.emptySet();

        if (attributeId.equals(ATTRIBUTEID_LOCAL_ENHET)) {
            values = convertSet(delegate.getLokalEnheterForAnsatt(subjectId));
        } else if (attributeId.equals(ATTRIBUTEID_FYLKESENHET)) {
            values = convertSet(delegate.getFylkesenheterForAnsatt(subjectId));
        } else if (attributeId.equals(ATTRIBUTEID_GEOGRAFISK_NEDSLAGSFELT)) {
            String saksbehandlersValgteEnhet = getSaksbehandlerValgteEnhet(context);
            Set<String> geografiskeNedslagsfelt = getGeografiskeNedslagsfelt(saksbehandlersValgteEnhet);
            values = convertSet(geografiskeNedslagsfelt);
        }

        return new EvaluationResult(new BagAttribute(attributeType, values));
    }

    private Set<String> getGeografiskeNedslagsfelt(String enhet) {
        return delegate.getArbeidsfordelingForEnhet(enhet)
                        .stream()
                        .map(Arbeidsfordeling::getGeografiskNedslagsfelt)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
    }

    private Set<AttributeValue> convertSet(Set<String> inputSet) {
        Set<AttributeValue> outputSet = new HashSet<>(inputSet.size());
        outputSet.addAll(inputSet.stream().map(JBossXACMLUtil::getAttributeValue).collect(Collectors.toList()));
        return outputSet;
    }

    private String getSubjectId(EvaluationCtx context) {
        return (String) context.getSubjectAttribute(STRING_TYPE, SUBJECT_ID, SUBJECT_CATEGORY).getAttributeValue().getValue();
    }

    private String getSaksbehandlerValgteEnhet(EvaluationCtx context) {
        return (String) context.getSubjectAttribute(STRING_TYPE, ATTRIBUTEID_SUBJECT_VALGT_ENHET, SUBJECT_CATEGORY ).getAttributeValue().getValue();
    }
}

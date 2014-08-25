package no.nav.sbl.modiabrukerdialog.pdp.test.util;

import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.AttributeType;
import org.jboss.security.xacml.core.model.context.EnvironmentType;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceType;
import org.jboss.security.xacml.core.model.context.SubjectType;
import org.jboss.security.xacml.interfaces.RequestContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.jboss.security.xacml.factories.RequestAttributeFactory.createStringAttributeType;
import static org.jboss.security.xacml.factories.RequestResponseContextFactory.createRequestCtx;

public class XACMLRequestBuilder {
	Map<String, Object> subjectAttributes = new HashMap<String, Object>();
	Map<String, Object> resourceAttributes = new HashMap<String, Object>();
	Map<String, Object> actionAttributes = new HashMap<String, Object>();
	Map<String, Object> environmentAttributes = new HashMap<String, Object>();

	public static XACMLRequestBuilder create() {
		return new XACMLRequestBuilder();
	}

	public XACMLRequestBuilder withSubjectAttr(String attr, String value) {
		subjectAttributes.put(attr, value);
		return this;
	}

	public XACMLRequestBuilder withSubjectAttr(String attr, Boolean value) {
		subjectAttributes.put(attr, value);
		return this;
	}

	public XACMLRequestBuilder withResourceAttr(String attr, String value) {
		resourceAttributes.put(attr, value);
		return this;
	}

	public XACMLRequestBuilder withActionAttr(String attr, String value) {
		actionAttributes.put(attr, value);
		return this;
	}

	public XACMLRequestBuilder withEnvironmentAttr(String attr, String value) {
		environmentAttributes.put(attr, value);
		return this;
	}

	private AttributeType createAttribute(String attrName, Object attrValue) {
		return createStringAttributeType(attrName, null, (String) attrValue);
	}

	public RequestContext build() {
		RequestContext request = createRequestCtx();

		// Create a subject type
		SubjectType subject = new SubjectType();
		for (String attrName : subjectAttributes.keySet()) {
			AttributeType attr = createAttribute(attrName, subjectAttributes.get(attrName));
			subject.getAttribute().add(attr);
		}

		// Create a resource type
		ResourceType resourceType = new ResourceType();
		for (String attrName : resourceAttributes.keySet()) {
			AttributeType attr = createAttribute(attrName, resourceAttributes.get(attrName));
			resourceType.getAttribute().add(attr);
		}

		// Create an action type
		ActionType actionType = new ActionType();
		for (String attrName : actionAttributes.keySet()) {
			AttributeType attr = createAttribute(attrName, actionAttributes.get(attrName));
			actionType.getAttribute().add(attr);
		}

		// Create an Environment Type (Optional)
		EnvironmentType environmentType = new EnvironmentType();
		for (String attrName : environmentAttributes.keySet()) {
			AttributeType attr = createAttribute(attrName, environmentAttributes.get(attrName));
			environmentType.getAttribute().add(attr);
		}

		// Create a Request Type
		RequestType requestType = new RequestType();
		requestType.getSubject().add(subject);
		requestType.getResource().add(resourceType);
		requestType.setAction(actionType);
		requestType.setEnvironment(environmentType);

		try {
			request.setRequest(requestType);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create XACML request.", e);
		}

		return request;
	}
}

package no.nav.sbl.dialogarena.modiabrukerdialog.web.util;

import org.apache.wicket.Component;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.request.component.IRequestableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * Compound implementation of the IAuthorizationStrategy that lets you use two or more strategies
 * together.
 */
public class UnchainedCompoundAuthorizationStrategy implements IAuthorizationStrategy
{
    private final List<IAuthorizationStrategy> strategies = new ArrayList<>();

    public final void add(IAuthorizationStrategy strategy)
    {
        if (strategy == null)
        {
            throw new IllegalArgumentException("Strategy argument cannot be null");
        }
        strategies.add(strategy);
    }

    /**
     * @see org.apache.wicket.authorization.IAuthorizationStrategy#isInstantiationAuthorized(java.lang.Class)
     */
    @Override
    public final <T extends IRequestableComponent> boolean isInstantiationAuthorized(
            Class<T> componentClass)
    {
        for (IAuthorizationStrategy strategy : strategies)
        {
            if (strategy.isInstantiationAuthorized(componentClass))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.apache.wicket.authorization.IAuthorizationStrategy#isActionAuthorized(org.apache.wicket.Component,
     *      org.apache.wicket.authorization.Action)
     */
    @Override
    public final boolean isActionAuthorized(Component component, Action action)
    {
        for (IAuthorizationStrategy strategy : strategies)
        {
            if (strategy.isActionAuthorized(component, action))
            {
                return true;
            }
        }
        return false;
    }
}
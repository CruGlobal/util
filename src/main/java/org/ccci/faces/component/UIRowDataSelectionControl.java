package org.ccci.faces.component;

import java.util.List;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionListener;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class UIRowDataSelectionControl extends UIComponentBase
{

    public static String facetName()
    {
        return UIRowDataSelectionControl.class.getName();
    }

    @Override
    public String getFamily()
    {
        return null;
    }

    @Override
    public void processDecodes(FacesContext context)
    {
        addListeners(getParent(), getId());
    }

    private void addListeners(UIComponent component, String id)
    {
        if (component == null) return;

        if (component instanceof UICommand)
        {
            ensureListenerPresent((UICommand) component, id);
        }

        for (UIComponent child : component.getChildren())
        {
            addListeners(child, id);
        }
    }

    private void ensureListenerPresent(UICommand uiCommand, String id)
    {
        RowDataSelectionListener listener = new RowDataSelectionListener(id);
        if (isListenerAbsent(uiCommand, listener))
        {
            addListener(uiCommand, listener);
        }
    }

    /**
     * Adds {@code listener} to the front of the ActionListener list, because other ActionListeners
     * may depend on the row data being set.  Adding it to the front requires removing the others,
     * adding the listener, and then re-adding the others.
     */
    private void addListener(UICommand uiCommand, RowDataSelectionListener listener)
    {
        List<ActionListener> registeredListeners = ImmutableList.copyOf(uiCommand.getActionListeners());
        for (ActionListener registeredListener : registeredListeners)
        {
            uiCommand.removeActionListener(registeredListener);
        }
        uiCommand.addActionListener(listener);
        for (ActionListener registeredListener : registeredListeners)
        {
            uiCommand.addActionListener(registeredListener);
        }
    }


    private boolean isListenerAbsent(UICommand uiCommand, RowDataSelectionListener listener)
    {
        return !ImmutableSet.copyOf(uiCommand.getActionListeners()).contains(listener);
    }

    public UIData getUIData()
    {
        return (UIData) getParent();
    }

}

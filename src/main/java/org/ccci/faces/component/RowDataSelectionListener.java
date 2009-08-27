package org.ccci.faces.component;

import java.io.Serializable;

import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.ccci.util.SimpleValueObject;
import org.ccci.util.strings.ToStringBuilder;
import org.ccci.util.strings.ToStringProperty;

import com.google.common.base.Preconditions;

public class RowDataSelectionListener extends SimpleValueObject implements ActionListener, Serializable
{

    @ToStringProperty
    private final String uiRowDataSelectionControlId;

    @Override
    protected Object[] getMembers()
    {
        return new Object[]{uiRowDataSelectionControlId};
    }
    
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).toString();
    }
    
    public RowDataSelectionListener(String uiRowDataSelectionControlId)
    {
        Preconditions.checkNotNull(uiRowDataSelectionControlId, "uiRowDataSelectionControlId is null");
        this.uiRowDataSelectionControlId = uiRowDataSelectionControlId;
    }

    public void processAction(ActionEvent event) throws AbortProcessingException
    {
        UIRowDataSelectionControl control = getUIRowDataSelectionControl(event.getComponent());

        if (control == null) 
        { 
            throw new IllegalStateException("Could not find UIRowDataSelectionControl with id "
                + uiRowDataSelectionControlId); 
        }

        UIData uiData = control.getUIData();

        Object rowData = uiData.getRowData();

        if (rowData != null)
        {
            ValueExpression rowDataSelectionPropertyExpression = control.getValueExpression("value");
            rowDataSelectionPropertyExpression.setValue(FacesContext.getCurrentInstance().getELContext(), rowData);
        }
    }

    private UIRowDataSelectionControl getUIRowDataSelectionControl(UIComponent component)
    {
        if (component instanceof UIViewRoot || component == null) return null;

        if (component instanceof UIData)
        {
            UIComponent uiRowDataSelectionControl = component.getFacet(UIRowDataSelectionControl.facetName());
            if (uiRowDataSelectionControl != null 
                    && uiRowDataSelectionControl instanceof UIRowDataSelectionControl
                    && uiRowDataSelectionControlId.equals(uiRowDataSelectionControl.getId())) 
            {
                return (UIRowDataSelectionControl) uiRowDataSelectionControl; 
            }
        }

        return getUIRowDataSelectionControl(component.getParent());
    }

    private static final long serialVersionUID = 1L;
}

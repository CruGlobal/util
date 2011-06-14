package org.ccci.facelets.tag.jsf;


import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagException;
import javax.faces.view.facelets.TagHandler;

import org.ccci.faces.component.UIRowDataSelectionControl;


public class UIRowDataSelectionControlHandler extends TagHandler
{

    private final TagAttribute value;
   
    public UIRowDataSelectionControlHandler(TagConfig config) {
        super(config);
        this.value = getRequiredAttribute("value");
    }

    // code adapted from ComponentHandler
    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException, FaceletException, ELException {
       
        // make sure our parent is not null
        if (parent == null) {
            throw new TagException(this.tag, "Parent UIComponent was null");
        }
       
        if (parent instanceof UIData != true) {
            throw new TagException(this.tag, "Parent should be instance of UIData");
        }
       
        String facetName = UIRowDataSelectionControl.facetName();
        
        if (parent.getFacet(facetName) == null)
        {
            //TODO: I really don't know if this logic is correct or not.  
            UIComponent c = new UIRowDataSelectionControl();

            String id = ctx.generateUniqueId(this.tagId);
            c.setId("rowDataSelectionControl_" + id);
            
            ValueExpression valueExpression = value.getValueExpression(ctx, Object.class);
            c.setValueExpression("value", valueExpression);
           
            parent.getFacets().put(facetName, c);
        }
    }
}
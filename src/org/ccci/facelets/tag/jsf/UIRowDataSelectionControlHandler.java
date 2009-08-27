package org.ccci.facelets.tag.jsf;


import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;

import org.ccci.faces.component.UIRowDataSelectionControl;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import com.sun.facelets.tag.TagException;
import com.sun.facelets.tag.TagHandler;
import com.sun.facelets.tag.jsf.ComponentSupport;

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

        // our id
        String id = ctx.generateUniqueId(this.tagId);

        // grab our component
        UIComponent c = ComponentSupport.findChildByTagId(parent, id);
        boolean componentFound = false;
        if (c != null) {
            componentFound = true;
            // mark all children for cleaning
            ComponentSupport.markForDeletion(c);
        } else {
            c = new UIRowDataSelectionControl();
           
            ValueExpression valueExpression = value.getValueExpression(ctx, Object.class);
            c.setValueExpression("value", valueExpression);
           
            // mark it owned by a facelet instance
            c.getAttributes().put(ComponentSupport.MARK_CREATED, id);
           
            // assign our unique id
            UIViewRoot root = ComponentSupport.getViewRoot(ctx, parent);
            if (root != null) {
                String uid = root.createUniqueId();
                c.setId(uid);
            }
           
        }

        this.nextHandler.apply(ctx, c);

        // finish cleaning up orphaned children
        if (componentFound) {
            ComponentSupport.finalizeForDeletion(c);
        }
       

        // add to the tree afterwards
        // this allows children to determine if it's
        // been part of the tree or not yet
        parent.getFacets().put(facetName, c);
    }
}
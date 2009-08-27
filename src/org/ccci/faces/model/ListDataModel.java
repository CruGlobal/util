package org.ccci.faces.model;

import java.util.List;

/**
 * Simply a generic version of JSF's ListDataModel, as JSF targets java 1.4
 * @author Matt Drees
 *
 * @param <E>
 */
public class ListDataModel<E> extends javax.faces.model.ListDataModel
{

    public ListDataModel(List<E> workPeriods)
    {
        super(workPeriods);
    }

    @SuppressWarnings("unchecked")
    @Override
    public E getRowData()
    {
        return (E) super.getRowData();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> getWrappedData()
    {
        return (List<E>) super.getWrappedData();
    }

}

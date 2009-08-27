package org.ccci.util;

import java.util.Collection;
import java.util.List;


public abstract class EffectiveSequencedRelationshipManagingList<T extends Child<P> & EffectiveSequenced, P> extends RelationshipManagingList<T, P>
{
    
    public EffectiveSequencedRelationshipManagingList(List<T> delegate, String parentPropertyName, P parent)
    {
        super(delegate, parentPropertyName, parent);
    }
    
    @Override
    public boolean add(T child)
    {
        int effectiveSequence;
        if (delegate().isEmpty())
        {
            effectiveSequence = 1;
        }
        else
        {
            effectiveSequence = getMaxEffectiveSequence() + 1;
        }
        boolean added = super.add(child);
        //call #setEffectiveSequence() after super.add(), because it may need to know the parent
        child.updateEffectiveSequence(effectiveSequence);
        return added;
    }

    private int getMaxEffectiveSequence()
    {
        int max = -1;
        for (EffectiveSequenced item : delegate())
        {
            if (item.getEffectiveSequence() > max)
            {
                max = item.getEffectiveSequence();
            }
        }
        return max;
    }
    
    @Override
    public void add(int index, T element)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends T> elements)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public T set(int index, T element)
    {
        throw new UnsupportedOperationException();
    }

    private static final long serialVersionUID = 1L;
}
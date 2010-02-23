package org.ccci.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingList;

/**
 * Use something like this:
 * 
 * <code><pre>
 * 
 *  public class Parent
 *  {
 *      static class ChildList extends RelationshipManagingList&lt;Child, Parent&gt;
 *      {
 *
 *          public ChildList(List&lt;Child&gt; delegate, String inversePropertyName, Parent parent)
 *          {
 *              super(delegate, inversePropertyName, parent);
 *          }
 *      }
 *
 *      List&lt;Child&gt; children = Lists.newArrayList();
 *
 *      List&lt;Child&gt; getChildren()
 *      {
 *          return new ChildList(children, "parent", this);
 *      }
 *  }
 *
 *  public class Child implements org.ccci.util.Child&lt;Parent&gt;
 *  {
 *      Parent parent;
 *
 *      public Parent getParent()
 *      {
 *          return parent;
 *      }
 *
 *      public void setParent(Parent parent)
 *      {
 *          this.parent = parent;
 *      }
 *  }
 * 
 * </pre></code>
 * 
 * @author Matt Drees
 * 
 * @param <C>
 *            the 'child' type - the type of the objects contained in the list
 * @param <P>
 *            the 'parent' type - the type of the object who owns the list
 */
public abstract class RelationshipManagingList<C extends Child<P>, P> extends ForwardingList<C>
{

    private final String parentPropertyName;
    private final P parent;
    private final List<C> delegate;

    /**
     * 
     * @param delegate the "real" list, whose implementation may be jpa-vendor specific
     * @param parentPropertyName the name of the property in the child type that refers to the parent
     * @param parent the parent instance owning this list
     */
    public RelationshipManagingList(List<C> delegate, String parentPropertyName, P parent)
    {
    	this.delegate = Preconditions.checkNotNull(delegate, "delegate is null");
        this.parentPropertyName = Preconditions.checkNotNull(parentPropertyName, "parentPropertyName is null");
        this.parent = Preconditions.checkNotNull(parent, "parent is null");

        // do at least a little verification
        Preconditions.checkState(getTypeArguments().length > 1, "can't get type argument");
    }

    @Override
    public boolean add(C child)
    {
        prepareForAdd(child);
        return super.add(child);
    }

    private void prepareForAdd(C child)
    {
        preconditionCheck(child);
        setParentForChild(child, parent);
    }

    private void preconditionCheck(C child)
    {
        Preconditions.checkNotNull(child, "null elements are not supported");
        Preconditions.checkArgument(getParentForChild(child) == null, "%s already has a %s (%s)", child,
            getParentPropertyName(), getParentForChild(child));
        Preconditions.checkState(!contains(child), "%s is already contains %s", parent, child);
        checkAdd(child);
    }
    
    @Override
    public boolean addAll(Collection<? extends C> childCollection)
    {
        prepareAllForAdd(childCollection);
        return super.addAll(childCollection);
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends C> childCollection)
    {
        prepareAllForAdd(childCollection);
        return super.addAll(index, childCollection);
    }

    private void prepareAllForAdd(Collection<? extends C> childCollection)
    {
        for(C child : childCollection)
        {
            preconditionCheck(child);
        }
        for(C child : childCollection)
        {
            setParentForChild(child, parent);
        }
    }

    private String getParentPropertyName()
    {
        return parentPropertyName;
    }

    protected void setParentForChild(C child, P parent)
    {
        child.setParent(parent);
    }

    protected P getParentForChild(C child)
    {
        return child.getParent();
    }

    @SuppressWarnings("unchecked")
    private Class<C> getChildClass()
    {
        return (Class<C>) getTypeArguments()[0];
    }

    private Type[] getTypeArguments()
    {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Type[] actualTypeArguments = type.getActualTypeArguments();
        return actualTypeArguments;
    }

    @Override
    public boolean remove(Object object)
    {
        Preconditions.checkArgument(getChildClass().isInstance(object), "%s is not a %s", object,
            getChildClass().getSimpleName());
        C child = getChildClass().cast(object);
        Preconditions.checkArgument(parent.equals(getParentForChild(child)),
            "%s is associated with a different %s (%s), not this one (%s)", child, getParentPropertyName(),
            getParentForChild(child), parent);
        Preconditions.checkState(contains(child), "%s is not associated with %s", child, parent);
        setParentForChild(child, null);
        return super.remove(object);
    }

    protected P getParent()
    {
        return parent;
    }

    /**
     * do any list-specific precondition checks
     * 
     * @param newChild
     */
    protected void checkAdd(C newChild)
    {
        return;
    }
    
    @Override
    protected final List<C> delegate() {
    	return delegate;
    }

    private static final long serialVersionUID = 1L;
}

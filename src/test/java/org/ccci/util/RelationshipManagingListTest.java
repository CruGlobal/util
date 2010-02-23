package org.ccci.util;

import java.util.List;

import org.junit.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class RelationshipManagingListTest
{
    static class Parent
    {
        static class ChildList extends RelationshipManagingList<Child, Parent>
        {
            private static final long serialVersionUID = 1L;

            public ChildList(List<Child> delegate, String inversePropertyName, Parent parent)
            {
                super(delegate, inversePropertyName, parent);
            }
        }

        List<Child> children = Lists.newArrayList();

        List<Child> getChildren()
        {
            return new ChildList(children, "parent", this);
        }
    }

    static class Child implements org.ccci.util.Child<Parent>
    {
        Parent parent;

        public Parent getParent()
        {
            return parent;
        }

        public void setParent(Parent parent)
        {
            this.parent = parent;
        }
    }

    @Test
    public void add()
    {
        Parent parent = new Parent();
        Child child = new Child();
        parent.getChildren().add(child);
        Assert.assertEquals(parent, child.parent);
        assert parent.getChildren().contains(child);
    }
    
    @Test
    public void addAll()
    {
        Parent parent = new Parent();
        Child child1 = new Child();
        Child child2 = new Child();
        
        List<Child> list = Lists.newArrayList();
        list.add(child1);
        list.add(child2);
        
        parent.getChildren().addAll(list);
        Assert.assertEquals(parent, child1.parent);
        Assert.assertEquals(parent, child2.parent);
        assert parent.getChildren().contains(child1);
        assert parent.getChildren().contains(child2);
    }
    
    @Test
    public void addAllWithIndex()
    {
        Parent parent = new Parent();
        Child child1 = new Child();
        Child child2 = new Child();
        
        List<Child> list = Lists.newArrayList();
        list.add(child1);
        list.add(child2);
        
        parent.getChildren().addAll(0, list);
        Assert.assertEquals(parent, child1.parent);
        Assert.assertEquals(parent, child2.parent);
        assert parent.getChildren().contains(child1);
        assert parent.getChildren().contains(child2);
    }

    @Test
    public void remove()
    {
        Parent parent = new Parent();
        Child child = new Child();
        child.parent = parent;
        parent.children.add(child);

        parent.getChildren().remove(child);
        assert !parent.children.contains(child);
        Assert.assertNull(child.parent);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void duplicateAddBombs()
    {
        Parent parent = new Parent();
        Child child = new Child();
        parent.getChildren().add(child);
        parent.getChildren().add(child);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void alreadyAssociatedChildAddBombs()
    {
        Parent parent1 = new Parent();
        Child child = new Child();
        parent1.children.add(child);
        child.parent = parent1;

        Parent parent2 = new Parent();
        parent2.getChildren().add(child);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void nonexistingRemoveBombs()
    {
        Parent parent = new Parent();
        Child child = new Child();
        parent.getChildren().remove(child);
    }
}

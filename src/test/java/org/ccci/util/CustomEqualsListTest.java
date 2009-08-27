package org.ccci.util;

import java.util.List;

import org.junit.Test;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class CustomEqualsListTest
{
    /**
     * hey, it's a fun name
     * @author Matt Drees
     */
    private final class FooEquivalator implements EqualsOverride<Foo>
    {
        public boolean isEqual(Foo t1, Foo t2)
        {
            return Objects.equal(t1.name, t2.name);
        }
    }

    public static class Foo
    {
        Foo(String name)
        {
            this.name = name;
        }
        
        String name;
    }
    @Test
    public void testCustomEqualsList()
    {
        List<Foo> list = CollectionsExtension.customEqualsList(new FooEquivalator());
        
        Foo foo1 = new Foo("foo1");
        
        list.add(foo1);
        assert list.contains(new Foo("foo1"));
        assert list.size() == 1;
        assert ! list.contains(new Foo("foo2"));
        assert ! list.remove(new Foo("foo2"));
        
        Foo removed = list.remove(0);
        assert removed != null;
        assert removed.name == "foo1";
        assert list.size() == 0;
        
    }

    @Test
    public void testCollectionsSame()
    {
        List<Foo> list = Lists.newArrayList(new Foo("one"), new Foo("two"));
        List<Foo> backwards = Lists.newArrayList(new Foo("two"), new Foo("one"));
        
        assert CollectionsExtension.containEqualElements(list, backwards, new FooEquivalator());
    }

}

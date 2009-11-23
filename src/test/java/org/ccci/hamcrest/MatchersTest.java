package org.ccci.hamcrest;

import static org.ccci.hamcrest.Matchers.isListOf;
import static org.ccci.hamcrest.Matchers.isSetOf;
import static org.ccci.hamcrest.Matchers.messageWasAddedContaining;
import static org.ccci.hamcrest.Matchers.noMessagesWereAdded;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ccci.testutils.StatusMessagesStub;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.jboss.seam.international.StatusMessage.Severity;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class MatchersTest
{
    
    @Test
    public void testIsSetOf()
    {
        String foo1 = "foo1";
        String foo2 = "foo2";
        HashSet<String> set = Sets.newHashSet(foo1, foo2);
        
        Matcher<? super Set<String>> setOf = isSetOf(foo2, foo1);
        Assert.assertTrue(setOf.matches(set));
    }
    
    @Test
    public void testIsSetOfDescription()
    {
        String foo1 = "foo1";
        Matcher<? super Set<String>> setOf = isSetOf(foo1);
        StringDescription description = new StringDescription();
        setOf.describeTo(description);

        assertThat(description.toString(), containsString("Set:"));
        assertThat(description.toString(), containsString("foo1"));
    }
    
    @Test
    public void testIsSetOfWithNotEnough()
    {
        String foo1 = "foo1";
        String foo2 = "foo2";
        HashSet<String> set = Sets.newHashSet(foo1, foo2);
        
        Matcher<? super Set<String>> setOf = isSetOf(foo1);
        Assert.assertFalse(setOf.matches(set));
        
        StringDescription description = new StringDescription();
        setOf.describeMismatch(set, description);
        
        assertThat(description.toString(), containsString("foo1"));
        assertThat(description.toString(), containsString("foo2"));
    }
    

    @Test
    public void testIsListOf()
    {
        String foo1 = "foo1";
        String foo2 = "foo2";
        List<String> set = Lists.newArrayList(foo1, foo2);
        
        Matcher<? super List<String>> listOf = isListOf(foo1, foo2);
        Assert.assertTrue(listOf.matches(set));
    }
    
    @Test
    public void testIsListOfDescription()
    {
        String foo1 = "foo1";
        Matcher<? super List<String>> listOf = isListOf(foo1);
        StringDescription description = new StringDescription();
        listOf.describeTo(description);

        assertThat(description.toString(), containsString("List:"));
        assertThat(description.toString(), containsString("foo1"));
    }
    
    @Test
    public void testIsListOfWithNotEnough()
    {
        String foo1 = "foo1";
        String foo2 = "foo2";
        List<String> set = Lists.newArrayList(foo1, foo2);
        
        Matcher<? super List<String>> listOf = isListOf(foo1);
        Assert.assertFalse(listOf.matches(set));
        
        StringDescription description = new StringDescription();
        listOf.describeMismatch(set, description);
        
        assertThat(description.toString(), containsString("foo1"));
        assertThat(description.toString(), containsString("foo2"));
    }
    
    @Test
    public void testIsListOfWithWrongOrder()
    {
        String foo1 = "foo1";
        String foo2 = "foo2";
        List<String> set = Lists.newArrayList(foo1, foo2);
        
        Matcher<? super List<String>> listOf = isListOf(foo2, foo1);
        Assert.assertFalse(listOf.matches(set));
    }
    
    
    @Test
    public void testNoMessagesAdded()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        Assert.assertTrue(noMessagesWereAdded().matches(messagesStub));
    }

    @Test
    public void testNoMessagesAddedDescription()
    {
        Matcher<? super StatusMessagesStub> noMessagesWereAdded = noMessagesWereAdded();
        Description description = new StringDescription();
        noMessagesWereAdded.describeTo(description);
        assertThat(description.toString(), containsString("no status messages"));
    }

    @Test
    public void testNoMessagesAddedWhenOneMessageAdded()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        messagesStub.add(Severity.ERROR, "a test message with param {0}", "foo");
        Matcher<? super StatusMessagesStub> noMessagesWereAdded = noMessagesWereAdded();
        Assert.assertFalse(noMessagesWereAdded.matches(messagesStub));
        
        Description description = new StringDescription();
        noMessagesWereAdded.describeMismatch(messagesStub, description);
        assertThat(description.toString(), containsString(Severity.ERROR.toString()));
        assertThat(description.toString(), containsString("1 message was added"));
        assertThat(description.toString(), containsString("a test message with param foo"));
    }

    @Test
    public void testNoMessagesAddedWhenMoreThanOneMessageAdded()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        messagesStub.add(Severity.ERROR, "a test message");
        messagesStub.add(Severity.INFO, "another message");
        Matcher<? super StatusMessagesStub> noMessagesWereAdded = noMessagesWereAdded();
        Assert.assertFalse(noMessagesWereAdded.matches(messagesStub));
        
        Description description = new StringDescription();
        noMessagesWereAdded.describeMismatch(messagesStub, description);
        assertThat(description.toString(), containsString("2 messages were added"));
        assertThat(description.toString(), containsString(Severity.ERROR.toString()));
        assertThat(description.toString(), containsString("a test message"));
        assertThat(description.toString(), containsString(Severity.INFO.toString()));
        assertThat(description.toString(), containsString("another message"));
    }
    
    @Test
    public void testMessageWasAddedContaining()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        messagesStub.add(Severity.ERROR, "a test error message with param {0}", "foo");
        Matcher<? super StatusMessagesStub> matcher = messageWasAddedContaining(Severity.ERROR, "a test error message", "foo");
        Assert.assertTrue(matcher.matches(messagesStub));
    }

    @Test
    public void testErrorMessageWasAddedContainingDescription()
    {
        Matcher<? super StatusMessagesStub> matcher = messageWasAddedContaining(Severity.ERROR, "a test error message", "foo");
        
        Description description = new StringDescription();
        matcher.describeTo(description);
        
        assertThat(description.toString(), containsString("message was added"));
        assertThat(description.toString(), containsString(Severity.ERROR.toString()));
        assertThat(description.toString(), containsString("a test error message"));
        assertThat(description.toString(), containsString("foo"));
    }
    

    @Test
    public void testMessageWasAddedContainingWhenNoMessageWasAdded()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        Matcher<? super StatusMessagesStub> matcher = messageWasAddedContaining(Severity.ERROR, "a test error message", "foo");
        Assert.assertFalse(matcher.matches(messagesStub));

        Description description = new StringDescription();
        matcher.describeMismatch(messagesStub, description);
        assertThat(description.toString(), containsString("no ERROR messages were added"));
    }

    @Test
    public void testMessageWasAddedContainingWhenOneWrongMessageWasAdded()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        Matcher<? super StatusMessagesStub> matcher = messageWasAddedContaining(Severity.ERROR, "a test error message", "foo");
        messagesStub.add(Severity.ERROR, "a different test message with param {0}", "bar");
        Assert.assertFalse(matcher.matches(messagesStub));

        Description description = new StringDescription();
        matcher.describeMismatch(messagesStub, description);
        assertThat(description.toString(), containsString("1 " + Severity.ERROR + " message was added"));
        assertThat(description.toString(), containsString("a different test message with param bar"));
    }
    
    
    @Test
    public void testMessageWasAddedContainingWhenTwoWrongMessageWasAdded()
    {
        StatusMessagesStub messagesStub = new StatusMessagesStub();
        Matcher<? super StatusMessagesStub> matcher = messageWasAddedContaining(Severity.ERROR, "a test error message", "foo");
        messagesStub.add(Severity.ERROR, "a different test message with param {0}", "bar");
        messagesStub.add(Severity.ERROR, "another wrong test message");
        Assert.assertFalse(matcher.matches(messagesStub));
        
        Description description = new StringDescription();
        matcher.describeMismatch(messagesStub, description);
        assertThat(description.toString(), containsString("2 " + Severity.ERROR + " messages were added"));
        assertThat(description.toString(), containsString("a different test message with param bar"));
        assertThat(description.toString(), containsString("another wrong test message"));
    }
    
}

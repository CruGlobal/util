package org.ccci.hamcrest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.ccci.testutils.StatusMessagesStub;
import org.ccci.util.Counter;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.Is;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class Matchers
{

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");


    public static <T> Matcher<? super Set<T>> isSetOf(final T... elements)
    {
        return new DelegateMatcher<Set<T>>(Is.<Set<T>>is(ImmutableSet.of(elements)))
        {

            @Override
            public void describeTo(Description description)
            {
                description.appendText("Set: ").appendValue(elements);
            }
        };
    }
    
    public static <T> Matcher<? super List<T>> isListOf(final T... elements)
    {
        return new DelegateMatcher<List<T>>(Is.is(Arrays.asList(elements)))
        {

            @Override
            public void describeTo(Description description)
            {
                description.appendText("List: ").appendValue(elements);
            }
        };
    }

    
    public static Matcher<? super StatusMessagesStub> noMessagesWereAdded()
    {
        return new TypeSafeDiagnosingMatcher<StatusMessagesStub>()
        {

            @Override
            protected boolean matchesSafely(StatusMessagesStub item, Description mismatchDescription)
            {
                List<StatusMessage> messages = item.getMessages();
                if (messages.isEmpty())
                {
                    return true;
                }
                else
                {
                    int size = messages.size();
                    mismatchDescription
                        .appendText(String.valueOf(size))
                        .appendText(" message")
                        .appendText(size > 1 ? "s were " : " was ")
                        .appendText("added. ");
                    if (size == 1)
                    {
                        mismatchDescription
                            .appendText("It was: ")
                            .appendValue(messages.get(0))
                            .appendText(".");
                    }
                    else
                    {
                        mismatchDescription
                            .appendText("They were:" + LINE_SEPARATOR);
                        appendMessagesToDescription(mismatchDescription, messages);
                    }
                    return false;
                }
            }


            @Override
            public void describeTo(Description description)
            {
                description.appendText("no status messages were added");
            }
        };
    }

    private static void appendMessagesToDescription(Description mismatchDescription, List<StatusMessage> messages)
    {
        Counter counter = new Counter();
        for (StatusMessage message : messages)
        {
            mismatchDescription
                .appendText(String.valueOf(counter.next()))
                .appendText(": ")
                .appendValue(message)
                .appendText(LINE_SEPARATOR);
        }
    }
    
    public static Matcher<? super StatusMessagesStub> errorMessageWasAddedContaining(Object... args)
    {
        return messageWasAddedContaining(Severity.ERROR, args); 
    }
    
    public static Matcher<? super StatusMessagesStub> messageWasAddedContaining(final Severity severity, final Object... args)
    {
        return new TypeSafeDiagnosingMatcher<StatusMessagesStub>()
        {
    
            @Override
            protected boolean matchesSafely(StatusMessagesStub item, Description mismatchDescription)
            {
                List<StatusMessage> messages = item.getMessages(severity);
                for (StatusMessage message : messages)
                {
                    if (getMissingArguments(message).isEmpty())
                        return true;
                }
                if (messages.isEmpty())
                {
                    mismatchDescription
                        .appendText("no ")
                        .appendText(severity.toString())
                        .appendText(" messages were added to StatusMessages");
                }
                else
                {
                    mismatchDescription
                        .appendText("The following ")
                        .appendText(String.valueOf(messages.size()))
                        .appendText(" ")
                        .appendText(severity.toString())
                        .appendText(" message")
                        .appendText(messages.size() > 1 ? "s were" : " was")
                        .appendText(" added, but did not contain all of the required Strings:" + LINE_SEPARATOR);
                    appendMessagesToDescription(mismatchDescription, messages);
                }
                return false;
            }
    
            private List<Object> getMissingArguments(StatusMessage message)
            {
                List<Object> missing = Lists.newArrayList();
                for (Object arg : args)
                {
                    if (message.getSummary() == null || !message.getSummary().contains(arg.toString()))
                        missing.add(arg);
                }
                return missing;
            }
    
            @Override
            public void describeTo(Description description)
            {
                description
                .appendText("some ")
                    .appendText(severity.toString())
                    .appendText(" message was added that contains all of the following Strings:")
                    .appendValueList("[", ", ", "]", args);
            }
        };
        
    }

    

    public static abstract class DelegateMatcher<T> extends BaseMatcher<T>
    {
        private final Matcher<? super T> delegate;
        
        public DelegateMatcher(Matcher<? super T> delegate)
        {
            this.delegate = delegate;
        }

        public Matcher<? super T> delegate()
        {
            return delegate;
        }
        
        @Override
        public void describeMismatch(Object item, Description mismatchDescription)
        {
            delegate().describeMismatch(item, mismatchDescription);
        }

        @Override
        public boolean matches(Object item)
        {
            return delegate().matches(item);
        }

        @Override
        public void describeTo(Description description)
        {
            delegate().describeTo(description);
        }
    };
    
}

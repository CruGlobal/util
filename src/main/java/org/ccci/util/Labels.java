package org.ccci.util;

import org.ccci.util.strings.Strings;


public class Labels
{

    public static String fromEnum(Enum<?> enumInstance)
    {
        if (enumInstance instanceof Labelled)
        {
            return ((Labelled)enumInstance).getLabel();
        }
        return Strings.capitalsAndUnderscoresToLabel(enumInstance.name());
    }

}

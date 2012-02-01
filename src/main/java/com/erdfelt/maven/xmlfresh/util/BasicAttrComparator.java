package com.erdfelt.maven.xmlfresh.util;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Comparator;

public class BasicAttrComparator implements Comparator<String>
{
    private static Collator collator = Collator.getInstance();

    @Override
    public int compare(String o1, String o2)
    {
        CollationKey key1 = toKey(o1);
        CollationKey key2 = toKey(o2);
        return key1.compareTo(key2);
    }

    private CollationKey toKey(String str)
    {
        if (str == null)
        {
            return collator.getCollationKey("");
        }
        return collator.getCollationKey(str.trim());
    }
}
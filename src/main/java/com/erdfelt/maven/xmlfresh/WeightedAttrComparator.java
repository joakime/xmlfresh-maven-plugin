package com.erdfelt.maven.xmlfresh;

import java.util.Comparator;

public class WeightedAttrComparator implements Comparator<String> {
    public static final WeightedAttrComparator INSTANCE = new WeightedAttrComparator();

    @Override
    public int compare(String o1, String o2) {
        String s1 = o1 == null ? "" : o1;
        String s2 = o2 == null ? "" : o2;
        int diff = s1.compareTo(s2);

        diff = diff - weighted(s1);
        diff = diff + weighted(s2);

        return diff;
    }

    private int weighted(String str) {
        // xmlns is grouped first
        if (str.startsWith("xmlns:")) {
            return 1000000; // always first
        }

        if ("id".equals(str)) {
            return 900000;
        }

        // non-namespaced are next group
        if (str.indexOf(':') < 0) {
            return 1000;
        }

        // namespaced is next
        return 0;
    }
}

package com.myorg.detection.util;

import java.util.Collections;
import java.util.List;

public class IQR {

    public class IQRAccum {
        public long iqr = 0;
    }

    public void IQR() {}

    public double IQR(List<Double> a) {
        int n = a.size();
        Collections.sort(a);

        // Index of median of entire data
        int mid_index = median(0, n);

        // Median of first half
        double Q1 = a.get(median(0, mid_index));

        // Median of second half
        double Q3 = a.get(median(mid_index + 1, n));

        // IQR calculation
        return (Q3 - Q1);
    }

    private static int median(int l, int r) {
        int n = r - l + 1;
        n = (n + 1) / 2 - 1;
        return n + l;
    }
}

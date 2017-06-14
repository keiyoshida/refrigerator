package com.example.refrigerator;

public class Mathematics {

    /**
     * 入力された値の平方根を求める。
     * <p>
     * <pre>
     * 0が入力された場合、0を返却する、
     * 負の値が入力された場合、{@link RuntimeException}を返却する。
     * 最大値を超える値が入力された場合、{@link RuntimeException}を返却する。</pre>
     * @param source 0を許容する。負の値は許容しない。最大値1.34E154
     * @return 入力された値の平方根を実数値で返却する。
     * @throws RuntimeException 負の値が入力された場合。
     */
    public static double sqrt(double source) {
        if (source == 0) {
            return 0;
        }

        if (source < 0) {
            throw new RuntimeException("負の値が入力されました。");
        }

        if (source > 1.34 * Math.pow(10, 154)) {
            throw new RuntimeException("限界を超える値が入力されました。");
        }

        double x0 = 0;
        while(x0*x0 < source) {
            x0++;
        }

        double xn = x0;
        int count = 10;
        for (int i=0; i<count; i++) {
            xn = (xn+source/xn)/2;
        }

        return xn;
    }
}

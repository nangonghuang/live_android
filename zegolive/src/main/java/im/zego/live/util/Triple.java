package im.zego.live.util;

import androidx.annotation.NonNull;

public class Triple<F, S, T> {
    public final F first;
    public final S second;
    public final T third;

    public Triple(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }

    @NonNull
    @SuppressWarnings("UnknownNullness") // Generic nullness should come from type annotations.
    public static <A, B, C> Triple<A, B, C> create(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }
}

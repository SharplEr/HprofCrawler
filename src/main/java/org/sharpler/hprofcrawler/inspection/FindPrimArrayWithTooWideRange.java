package org.sharpler.hprofcrawler.inspection;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.sharpler.hprofcrawler.api.Collector;
import org.sharpler.hprofcrawler.api.InstanceConsumer;
import org.sharpler.hprofcrawler.parser.PrimArray;
import org.sharpler.hprofcrawler.parser.Type;

import java.util.function.LongFunction;

public final class FindPrimArrayWithTooWideRange implements
        Collector<Type, PrimArray, Object2LongOpenHashMap<FindPrimArrayWithTooWideRange.RangeShift>> {
    private final Object2LongOpenHashMap<RangeShift> stats = new Object2LongOpenHashMap<>();

    @Override
    public InstanceConsumer<PrimArray> getConsumer(Type type) {
        switch (type) {
            case LONG:
                return x -> {
                    if (x.getLength() == 0) {
                        return false;
                    }
                    Type minType = of((long[]) x.getArrayRaw());
                    if (minType != Type.LONG) {
                        stats.addTo(new RangeShift(Type.LONG, minType), 1L);
                    }

                    return false;
                };
            case INT:
                return x -> {
                    if (x.getLength() == 0) {
                        return false;
                    }

                    Type minType = of((int[]) x.getArrayRaw());
                    if (minType != Type.INT) {
                        stats.addTo(new RangeShift(Type.INT, minType), 1L);
                    }

                    return false;
                };
            case SHORT:
                return x -> {
                    if (x.getLength() == 0) {
                        return false;
                    }

                    Type minType = of((short[]) x.getArrayRaw());
                    if (minType != Type.SHORT) {
                        stats.addTo(new RangeShift(Type.SHORT, minType), 1L);
                    }

                    return false;
                };
        }

        throw new IllegalStateException();
    }

    ;

    @Override
    public Object2LongOpenHashMap<RangeShift> buildResult(LongFunction<String> nameResolver) {
        return stats;
    }

    private static Type of(long[] array) {
        assert array.length > 0;

        Type currentMin = Type.BYTE;

        for (long x : array) {
            currentMin = newCurrentMin(x, currentMin);
            if (currentMin == Type.LONG) {
                break;
            }
        }

        return currentMin;
    }

    private static Type of(int[] array) {
        assert array.length > 0;

        Type currentMin = Type.BYTE;

        for (int x : array) {
            currentMin = newCurrentMin(x, currentMin);
            if (currentMin == Type.INT) {
                break;
            }
        }

        return currentMin;
    }

    private static Type of(short[] array) {
        assert array.length > 0;

        Type currentMin = Type.BYTE;

        for (short x : array) {
            currentMin = newCurrentMin(x, currentMin);
            if (currentMin == Type.SHORT) {
                break;
            }
        }

        return currentMin;
    }

    @SuppressWarnings("AssignmentToMethodParameter")
    public static Type newCurrentMin(long value, Type currentMin) {
        if (currentMin == Type.BYTE) {
            if (value <= Byte.MAX_VALUE && value >= Byte.MIN_VALUE) {
                return Type.BYTE;
            }
            currentMin = Type.SHORT;
        }

        if (currentMin == Type.SHORT) {
            if (value <= Short.MAX_VALUE && value >= Short.MIN_VALUE) {
                return Type.SHORT;
            }
            currentMin = Type.INT;
        }

        if (currentMin == Type.INT) {
            if (value <= Integer.MAX_VALUE && value >= Integer.MIN_VALUE) {
                return Type.INT;
            }
        }

        return Type.LONG;
    }

    public static final class RangeShift {
        private final Type origin;
        private final Type enough;

        public RangeShift(Type origin, Type enough) {
            this.origin = origin;
            this.enough = enough;
        }

        public Type getOrigin() {
            return origin;
        }

        public Type getEnough() {
            return enough;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RangeShift)) return false;

            RangeShift that = (RangeShift) o;

            if (origin != that.origin) return false;
            if (enough != that.enough) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = origin.hashCode();
            result = 31 * result + enough.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "RangeShift{" +
                    "origin=" + origin +
                    ", enough=" + enough +
                    '}';
        }
    }
}


import com.sun.management.HotSpotDiagnosticMXBean;

import javax.management.MBeanServer;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Objects;

public class ManFuncUnderstanding {
    public interface ManFunc<T, R> {

        R apply(T t);

        default <V> ManFunc<V, R> compose(ManFunc<? super V, ? extends T> before) {
            Objects.requireNonNull(before);
            return (V v) -> apply(before.apply(v));
        }

        default <V> ManFunc<T, V> andThen(ManFunc<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (T t) -> after.apply(apply(t));
        }

        /**
         * This is the older way of doing, that can be achieved by lambda check andThen
         * Do note that both of the methods returns a new functions which operates on the existing function
         */
        default <V> ManFunc<T, V> andThenOld(ManFunc<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return new ManFunc<T, V>() {
                @Override
                public V apply(T t) {
                    return after.apply(ManFunc.this.apply(t));
                }
            };
        }
    }

    public static ManFunc<String, String> getMyFunc(String text) {
        return s -> s + "" + text;
    }

    /**
     * Check the comments with VM status
     * @param args
     */
    public static void main(String[] args) {

        // 3 functions we created
        ManFunc<String, String> mf0 = s -> s + "0"; // ManFuncUnderstanding$$Lambda$14 1 instance
        ManFunc<String, String> mf1 = s -> s + "1"; // ManFuncUnderstanding$$Lambda$15 1 instance
        ManFunc<String, String> mf2 = s -> s + "2"; // ManFuncUnderstanding$$Lambda$16 1 instance
        heapDump("3lambda.hprof");

        ManFunc<String, String> mf3 = getMyFunc("3"); // ManFuncUnderstanding$$Lambda$50 1 instance

        heapDump("a0.hprof");

        System.out.println(mf0
                .andThen(mf1) // ManFuncUnderstanding$ManFunc$$Lambda$5' 1 instance
                .andThen(mf2) // ManFuncUnderstanding$ManFunc$$Lambda$5' 2 instance
                .andThen(mf3) // ManFuncUnderstanding$ManFunc$$Lambda$5' 3 instance
                .andThen(getMyFunc("4")) // ManFuncUnderstanding$ManFunc$$Lambda$5' 4 instance and ManFuncUnderstanding$$Lambda$50 2 instance
                .apply("a"));
        // In total 9 func exists, apply is called on ManFuncUnderstanding$ManFunc$$Lambda$5' its last instance created
        // check a1.hprof image Note: ManFunc is interface
        heapDump("a1.hprof");

        System.out.println(mf0
                .andThenOld(mf1) // ManFuncUnderstanding$ManFunc$1 1 instance
                .andThenOld(mf2) // ManFuncUnderstanding$ManFunc$1 2 instance
                .andThenOld(mf3) // ManFuncUnderstanding$ManFunc$1 3 instance
                .andThenOld(getMyFunc("4")) // ManFuncUnderstanding$ManFunc$1 4 instance and ManFuncUnderstanding$$Lambda$50 3 instance
                .apply("a"));
        // In total 14 func exists, apply is called on ManFuncUnderstanding$ManFunc$1 -> last instance, also it represents anonymous class
        // check a2.hprof image Note: ManFunc is interface
        heapDump("a2.hprof");
    }

    private static void heapDump(String file) {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        HotSpotDiagnosticMXBean mxBean = null;
        try {
            mxBean = ManagementFactory.newPlatformMXBeanProxy(
                    server, "com.sun.management:type=HotSpotDiagnostic", HotSpotDiagnosticMXBean.class);
            mxBean.dumpHeap(System.getProperty("user.home") + "/Downloads/" + file, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

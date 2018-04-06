import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.TestWatchman;
import org.junit.runners.model.FrameworkMethod;

public class UnitTest {
    @Rule
    public MethodRule watchman = new TestWatchman() {
        public void starting(FrameworkMethod method) {
            System.out.println("\u001B[32m[ RUN     ] \u001B[0m" + method.getName() + "()");
        }
        public void succeeded(FrameworkMethod method) {
            System.out.println("\u001B[32m[      OK ] \u001B[0m");
        }
    };
    void log(String s) {
        System.out.println("-- " + s);
    }
    void error(String s) {
        System.out.println("-- \u001B[31mERROR:   " + s + "\u001B[0m");
    }
    void warning(String s) {
        System.out.println("-- \u001B[33mWARNING: " + s + "\u001B[0m");
    }
}

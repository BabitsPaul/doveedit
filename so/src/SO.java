import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SO {
    public static void main(String[] args) {
        replaceFor(0, i -> i <= 10, i -> System.out.println(i), k -> ++k);
    }

    public static void replaceFor(int i, Predicate<Integer> p, Consumer<Integer> c, Function<Integer, Integer> f) {
        //check whether the termination-condition is true
        if (!p.test(i))
            return;

        //this consumer does what would be in the for-loop
        c.accept(i);

        //continue with the next value for i
        replaceFor(f.apply(i), p, c, f);
    }
}
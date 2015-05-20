import java.util.ArrayList;
import java.util.List;

public class SO {

    public static void main(String[] args) {
        int x = -1757151608;//multiplicand
        int y = -1000;//expected result
        int z = 0;//multiplicator (searched)

        List<Integer> input_hi_bits = hi_bits(x);
        List<Integer> output_hi_bits = hi_bits(y);

        List<Integer> todo = new ArrayList<>();
        for (int i = input_hi_bits.get(0); i < 32; i++) {
            int mask = (1 << i);

            if (input_hi_bits.contains(i))
                todo.add(i);

            //find all bits of z that sum up to bit[i] of y
            List<Integer> bits = new ArrayList<>();
            for (int z_start : todo)
                bits.add(i - z_start);

            boolean output_hi_bit = ((y & mask) != 0);
            for (int j = 0; j < bits.size() - 1; j++)
                if ((z & (1 << bits.get(i))) != 1)
                    output_hi_bit = !output_hi_bit;

            if (output_hi_bit)
                z |= mask;
        }
    }

    private static List<Integer> hi_bits(int n) {
        ArrayList<Integer> result = new ArrayList<>();

        for (int i = 0; i < 32; i++)
            if ((n & (1 << i)) != 0)
                result.add(i);

        return result;
    }
}
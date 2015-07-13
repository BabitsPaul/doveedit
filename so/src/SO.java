import java.util.ArrayList;
import java.util.List;

public class SO {

    public static void main(String[] args) {
        SO so = new SO();
        System.out.println(so.split(123));
    }

    public List<List<String>> split(int in) {
        List<List<String>> result = new ArrayList<>();

        String num = String.valueOf(in);

        //the maximum is to split the number at each possible position
        int maxSplit = 0;
        for (int i = 0; i < num.length() - 1; i++)
            maxSplit |= (1 << i);

        for (int i = 0; i <= maxSplit; i++) {
            List<Integer> split = new ArrayList<>();

            //translate the representation of the splitting into the respective indices
            for (int b = 0; b < num.length() - 1; b++)
                if ((i & (1 << b)) != 0)
                    split.add(b + 1);

            //last char must be part of the solution aswell
            split.add(num.length());

            //split the input in the specified way
            List<String> strings = new ArrayList<>();
            int lastSplit = 0;
            for (int s : split) {
                strings.add(num.substring(lastSplit, s));

                lastSplit = s;
            }

            result.add(strings);
        }

        return result;
    }
}
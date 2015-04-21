import java.util.ArrayList;

/**
 * Created by Babits on 21/04/2015.
 */
public class SO {
    public static void main(String[] args) {
        int n = 7;

        ArrayList<Integer> divisors = new ArrayList<>();

        for (int i = 2; i <= n; i++) {
            int tmp = i;

            //simplify div, until it can't be created by multiplying elements of divisors
            for (int div : divisors)
                if (tmp % div == 0)
                    tmp /= div;

            if (tmp != 1)
            //tmp cant be generated from number that are content of divisors
            //-> next step
            {
                //split tmp up into it's primdivisors and add them to the
                //list of divisors
                for (int j = 0; j < divisors.size(); j++)
                    while (tmp % divisors.get(j) == 0) {
                        divisors.add(divisors.get(j));
                        tmp /= divisors.get(j);
                    }

                //found a new primdivisor that isn't content of divisors
                if (tmp != 1)
                    divisors.add(tmp);
            }
        }

        //calculate the final result
        int result = 1;
        for (Integer div : divisors)
            result *= div;

        System.out.println(result);
    }
}

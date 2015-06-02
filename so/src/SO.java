import java.util.ArrayList;

public class SO {
    public static void main(String[] args) {
        /*ArrayList<Integer> list = new ArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        mystery2(list);*/
        double tmp = 1.0;

        for (double i = 1.0; i < 10; i++)
            tmp += Math.pow(-1, i) / (2 * i + 1);

        tmp *= 4;

        System.out.println(tmp);
    }

    public static void mystery2(ArrayList<Integer> list) {

        for (int i = list.size() - 1; i >= 0; i--) {

            if (i % 2 == 0) {

                list.add(list.get(i));

            } else {

                list.add(0, list.get(i));

            }

        }

        System.out.println(list);
    }
}
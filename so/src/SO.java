import java.util.Arrays;
import java.util.Random;

public class SO {
    public static void main(String[] args) {
        System.out.println("Max Split Bin Array");
        System.out.println("Generating bits...");
        int len = 1000;
        int[] in = new int[len];
        Random r = new Random();
        for (int i = 0; i < len; i++)
            in[i] = r.nextInt(2);
        System.out.println("Bits generated");
        for (int i : in)
            System.out.print(i);
        System.out.println();

        System.out.println("Generating count...");
        int tmp = 0;
        for (int i = 0; i < len; i++)
            in[i] = tmp += in[i];
        System.out.println("Count generated");
        System.out.println(Arrays.toString(in));

        //next task: find the biggest possible matching arrays
        //start, mid, end so that arr[mid] - arr[start] > (mid - start) / 2
        //arr[end] - arr[end] > (end - mid) / 2 and no bigger value for
        //start - end exists
    }
}

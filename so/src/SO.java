public class SO {
    private Object lockA = new Object(), lockB = new Object();

    public static void main(String[] args) {
        final SO so = new SO();

        Thread t1 = new Thread(() -> so.a());
        Thread t2 = new Thread(() -> so.b());

        t1.start();
        t2.start();
    }

    private void a() {
        synchronized (lockA) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            b();
        }
    }

    private synchronized void b() {
        synchronized (lockB) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            a();
        }
    }
}
import com.example.thread.ThreadPoolHolder;

/**
 * @desc 测试线程池ExecutorService中的submit和excute执行异常
 *       submit的执行异常会被吞掉，需要重写afterExecute方法来处理
 *       而execute的执行异常不存在此问题
 * @author timothyer
 * @date 2018-5-9
 */
public class Test {

    public static void main(String[] args) {
        ThreadPoolHolder.THREAD_POOL.submit(new Runnable() {
            @Override
            public void run() {
               domain();
            }
        });
        /*ThreadPoolHolder.execute(new Runnable() {
            @Override
            public void run() {
                domain();
            }
        });*/
    }

    private static void domain() {
        int a = 0;
        while (a < 10) {
            System.out.println(String.valueOf(a));
            if (a == 5) {
                throw new RuntimeException("存在异常");
            }
            a ++;
        }
    }
}

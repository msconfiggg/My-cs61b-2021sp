package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int M = 10000;

        for (int i = 1; i < 256; i *= 2) {
            SLList<Integer> helpList = new SLList<>();
            for (int j = 0; j < i * 1000; j++) {
                helpList.addLast(j);
            }

            Stopwatch sw = new Stopwatch();
            for (int n = 0; n < M; n++) {
                helpList.getLast();
            }

            times.addLast(sw.elapsedTime());
            Ns.addLast(i * 1000);
            opCounts.addLast(M);
        }

        printTimingTable(Ns, times, opCounts);
    }

}

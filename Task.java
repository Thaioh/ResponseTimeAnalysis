public class Task implements Comparable<Task>{
    String name = "Task";
    final int C,D,T;
    int O;
    int P = Integer.MAX_VALUE;

    public Task(int computation, int period){
        C = computation;
        D = period;
        T = period;
        O = 0;
    }

    public Task(int computation, int deadline, int period) {
        C = computation;
        D = deadline;
        T = period;
        O = 0;
    }

    public Task(int computation, int deadline, int period, int offset) {
        C = computation;
        D = deadline;
        T = period;
        O = offset;
    }

    public Task(String name, int computation, int period){
        this.name = name;
        C = computation;
        D = period;
        T = period;
        O = 0;
    }

    public Task(String name, int computation, int deadline, int period) {
        this.name = name;
        C = computation;
        D = deadline;
        T = period;
        O = 0;
    }

    public Task(String name, int computation, int deadline, int period, int offset) {
        this.name = name;
        C = computation;
        D = deadline;
        T = period;
        O = offset;
    }

    @Override
    public String toString() {
        return name+"{" +
                "C=" + C +
                ", D=" + D +
                ", T=" + T +
                ", O=" + O +
                '}';
    }


    @Override
    public int compareTo(Task o) {
        return Integer.compare(P,o.P);
    }
}

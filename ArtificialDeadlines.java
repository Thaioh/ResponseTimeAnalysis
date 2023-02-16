import java.util.ArrayList;

/** This Program implements a way to determine Response Times
 * based on approach by N. C. Audsley as presented in his work
 * "OPTIMAL PRIORITY ASSIGNMENT AND FEASIBILITY
 * OF STATIC PRIORITY TASKS WITH ARBITRARY START TIMES", 1991
 */
public class ArtificialDeadlines {
    final ArrayList<Task> taskSet;
    int L = 0;
    ArrayList<Tuple> tupleSet;


    public ArtificialDeadlines(ArrayList<Task> taskSet) {
        this.taskSet = taskSet;
        setPriorities(); //all priorities are set to 0
    }

    void setPriorities(){
        for(Task t: taskSet){
            t.P = 0;
        }
    }

    // least common multiple
    int lcm(Task task1){
        int lcm = task1.T;
        for (Task task2:taskSet){
            if (task2.P <= task1.P)
                lcm = LCM(lcm,task2.T);
        }
        return lcm;
    }

    void adjustOffset(Task task1){
        for(Task task2: taskSet){
            if (task2.P < task1.P){
                if (task1.O > task2.O)
                    task2.O = (int) Math.ceil((double)(task1.O-task2.O)/task2.T) * task2.T + task2.O;
                task2.O -= task1.O;
            }
        }
        task1.O = 0;
    }

    int stabilisation(Task task1){
        adjustOffset(task1);
        int maxOffset = 0;
        for(Task task2: taskSet){
            if (task2.P < task1.P && task2.O > maxOffset)
                maxOffset = task2.O;
        }
        return ((int)Math.ceil((double)maxOffset/task1.T))*task1.T;
    }

    int remainingInterference(Task task, int t){
        if (t == 0) return 0;
        int time = t - task.T + task.D;
        int R = 0;
        createTupleSet(task,time, t);
        if(L > 0) tupleSet.add(0,new Tuple(L,time));
        int tupleTime = 0;
        int activeSince = time;
        for(Tuple tuple : tupleSet){
            tupleTime = tuple.T;
            if(tupleTime >= time + R) {
                activeSince = tupleTime;
                R = 0;
            }
            time = tupleTime;
            R = R + tuple.C;
        }
        R = R - (t - activeSince);
        if (R < 0) R = 0;
        return R;
    }

    int createdInterference(Task task, int t, int R){
        int next_free = R + t;
        int K = 0;
        int total_created = R;
        createTupleSet(task,t,t+task.D);
        for(Tuple tuple : tupleSet){
            total_created = total_created + tuple.C;
            if (next_free < tuple.T) next_free = tuple.T;
            K = K + Math.min(t + task.D - next_free, tuple.C);
            next_free = Math.min(t + task.D, next_free + tuple.C);
        }
        L = total_created - K - Math.min(task.D,R);
        return K;
    }

    int createTupleSet(Task task1, int start, int end){
        tupleSet = new ArrayList<Tuple>();
        for (int time = start; time < end; time++) {
            for (Task task2 : taskSet) {
                if (task2.P < task1.P) {
                    int k = 0;
                    while (task2.O+k*task2.T < time) k++;
                    if (task2.O+k*task2.T == time)
                        tupleSet.add(new Tuple(task2.C,time));
                }
            }
        }
        return 0;
    }

    boolean feasibilityTest(){
        for(int priority = taskSet.size(); priority > 0; priority--){
            boolean feasibleTaskFound = false;
            for (int i = 0; !feasibleTaskFound && i < taskSet.size(); i++){
                Task checkTask = taskSet.get(i);
                if(checkTask.P > priority) continue;
                feasibleTaskFound = true;
                checkTask.P = priority;
                int t = 0;
                L = 0;
                int s = stabilisation(checkTask);
                int p = lcm(checkTask);
                while (t < s+p) {
                    int RI = remainingInterference(checkTask,t);
                    int CI = createdInterference(checkTask,t,RI);
                    if(checkTask.C + RI + CI > checkTask.D){
                        checkTask.P = 0;
                        feasibleTaskFound = false;
                        break;
                    }
                    t = t + checkTask.T;
                }
            }
            if (!feasibleTaskFound) {
                System.out.println("No Task found to be feasible at priority " + priority);
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        //Test 1

        ArrayList<Task> smallTaskSet = new ArrayList<Task>(){};
        Task Task1 = new Task("T1", 2, 3, 4,2);
        Task Task2 = new Task("T2", 3, 4, 8,0);
        Task Task3 = new Task("T3", 1, 5, 8,1);
        smallTaskSet.add(Task3);
        smallTaskSet.add(Task2);
        smallTaskSet.add(Task1);
        ArtificialDeadlines smallTaskSetAssignment = new ArtificialDeadlines(smallTaskSet);
        smallTaskSetAssignment.feasibilityTest();
        System.out.println("Test 1:");
        smallTaskSet.forEach(task -> System.out.println(task.name + ", Priority: "+ task.P));

        //Test 2
        ArrayList<Task> bigTaskSet = new ArrayList<Task>() {};
        Task TA = new Task("TA",1,1,10,4);
        Task TB = new Task("TB",1,2,10,5);
        Task TC = new Task("TC",5,6,20,0);
        Task TD = new Task("TD",8,9,40,7);
        Task TE = new Task("TE",8,14,40,27);
        Task TF = new Task("TF",6,30,40,0);
        bigTaskSet.add(TF);
        bigTaskSet.add(TE);
        bigTaskSet.add(TD);
        bigTaskSet.add(TC);
        bigTaskSet.add(TB);
        bigTaskSet.add(TA);
        ArtificialDeadlines ad = new ArtificialDeadlines(bigTaskSet);
        ad.feasibilityTest();
        System.out.println("\nTest 2:");
        bigTaskSet.forEach(task -> System.out.println(task.name + ", Priority: "+ task.P));

        /*
        Expected:
        Test 1:
        T3, Priority: 3
        T2, Priority: 1
        T1, Priority: 2

        Test 2:
        TF, Priority: 5
        TE, Priority: 6
        TD, Priority: 3
        TC, Priority: 2
        TB, Priority: 4
        TA, Priority: 1
         */

    }

    //LCM by java2s.com through Open Source License
    public static int LCM(int a, int b) {
        int largerValue = a;
        int smallerValue = b;
        if (b > a) {
            largerValue = b;/*  w ww.j a  v a  2  s. c  o  m*/
            smallerValue = a;
        }
        for (int i = 1; i <= largerValue; i++) {
            if ((largerValue * i) % smallerValue == 0) {
                return largerValue * i;
            }
        }
        return largerValue * smallerValue;
    }

    class Tuple{
        final int C, T;

        Tuple(int C, int T){
            this.C = C;
            this.T = T;
        }

        @Override
        public String toString() {
            return "Tuple{" +
                    "C=" + C +
                    ", T=" + T +
                    '}';
        }
    }
}

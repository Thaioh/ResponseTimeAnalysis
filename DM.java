import java.util.ArrayList;
import java.util.Comparator;

public class DM {
    final ArrayList<Task> taskSet;

    DM(ArrayList<Task> taskSet){
        this.taskSet = taskSet;
        sortTaskSet();
        setPriorities();
    }

    void sortTaskSet(){
        taskSet.sort(new Comparator<Task>() {
            @Override
            public int compare(Task t1, Task t2) {
                return Integer.compare(t1.D,t2.D);
            }
        });
    }

    void setPriorities(){
        int priority = 1;
        for(Task t: taskSet){
            t.P = priority;
            priority++;
        }
    }

    int inputs(int start, int end, Task task){
        return (int)(Math.ceil((float)end/task.T)-Math.ceil((float)start/task.T));
    }

    int comp(int start, int end, Task task){
        int compTime = 0;
        for (Task t:taskSet){
            if( t.P < task.P){
                compTime += inputs(start,end,t)*t.C;
            } else
                break;
        }
        return compTime;
    }

    int response(int start, int end, Task task){
        int comp = comp(start,end,task);
        if (comp==0)
            return end;
        else
            return response(end,end+comp,task);
    }

    int responseTime(Task task){
        return response(0,task.C,task);
    }

    void feasibilityTest(){
        for(Task t: taskSet){
            int responseTime = responseTime(t);
            if (responseTime <= t.D)
                System.out.println("Task " + t.name + " is feasible: " + responseTime + " <= " + t.D);
            else
                System.out.println("Task " + t.name + " is not feasible: " + responseTime + " > " + t.D);
        }
    }

    double processingLoad(Task task){
        double load = 0;
        for (Task t:taskSet){
            if( t.P <= task.P){
                load += (double) t.C/t.T;
            } else
                break;
        }
        return load;
    }

    double processingLoad(){
        double load = 0;
        for (Task t:taskSet){
            load += (double) t.C/t.T;
        }
        return load;
    }

    public static void main(String[] args) {
        ArrayList<Task> taskSet =  new ArrayList<Task>(){};
        taskSet.add(new Task("task3",2,5,5));
        taskSet.add(new Task("task4",1,10,10));
        taskSet.add(new Task("task1",1,3,4));
        taskSet.add(new Task("task2",1,4,5));
        DM dm = new DM(taskSet);
        if (dm.processingLoad() > 1)
            System.out.println("The task set cannot be scheduled");
        else
            dm.feasibilityTest();
    }
}

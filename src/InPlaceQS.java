import java.util.ArrayList;//####[1]####
import java.util.List;//####[2]####
import paratask.runtime.TaskIDGroup;//####[4]####
//####[4]####
//-- ParaTask related imports//####[4]####
import paratask.runtime.*;//####[4]####
import java.util.concurrent.ExecutionException;//####[4]####
import java.util.concurrent.locks.*;//####[4]####
import java.lang.reflect.*;//####[4]####
import javax.swing.SwingUtilities;//####[4]####
//####[4]####
public class InPlaceQS<T extends Comparable<? super T>> {//####[7]####
//####[7]####
    /*  ParaTask helper method to access private/protected slots *///####[7]####
    public void __pt__accessPrivateSlot(Method m, Object instance, TaskID arg, Object interResult ) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {//####[7]####
        if (m.getParameterTypes().length == 0)//####[7]####
            m.invoke(instance);//####[7]####
        else if ((m.getParameterTypes().length == 1))//####[7]####
            m.invoke(instance, arg);//####[7]####
        else //####[7]####
            m.invoke(instance, arg, interResult);//####[7]####
    }//####[7]####
//####[9]####
    private Integer nThreads;//####[9]####
//####[10]####
    private Integer granularity;//####[10]####
//####[11]####
    private List<T> list;//####[11]####
//####[13]####
    public InPlaceQS(int granularity) {//####[13]####
        this.granularity = granularity;//####[14]####
    }//####[15]####
//####[18]####
    public List<T> sort(List<T> unsorted) {//####[18]####
        list = new ArrayList<T>(unsorted);//####[19]####
        TaskID wholeList = run(0, list.size() - 1);//####[20]####
        TaskIDGroup g = new TaskIDGroup(1);//####[21]####
        g.add(wholeList);//####[22]####
        try {//####[23]####
            g.waitTillFinished();//####[24]####
        } catch (InterruptedException e) {//####[25]####
            System.out.println("Interrupted");//####[26]####
        } catch (ExecutionException e) {//####[27]####
            System.out.println("Execution");//####[28]####
        }//####[30]####
        return list;//####[31]####
    }//####[32]####
//####[34]####
    private Method __pt__run_int_int_method = null;//####[34]####
    private Lock __pt__run_int_int_lock = new ReentrantLock();//####[34]####
    public TaskID<Void> run(int left, int right)  {//####[34]####
//####[34]####
        //-- execute asynchronously by enqueuing onto the taskpool//####[34]####
        return run(left, right, new TaskInfo());//####[34]####
    }//####[34]####
    public TaskID<Void> run(int left, int right, TaskInfo taskinfo)  {//####[34]####
        if (__pt__run_int_int_method == null) {//####[34]####
            try {//####[34]####
                __pt__run_int_int_lock.lock();//####[34]####
                if (__pt__run_int_int_method == null) //####[34]####
                    __pt__run_int_int_method = ParaTaskHelper.getDeclaredMethod(getClass(), "__pt__run", new Class[] {int.class, int.class});//####[34]####
            } catch (Exception e) {//####[34]####
                e.printStackTrace();//####[34]####
            } finally {//####[34]####
                __pt__run_int_int_lock.unlock();//####[34]####
            }//####[34]####
        }//####[34]####
//####[34]####
        Object[] args = new Object[] {left, right};//####[34]####
        taskinfo.setTaskArguments(args);//####[34]####
        taskinfo.setMethod(__pt__run_int_int_method);//####[34]####
        taskinfo.setInstance(this);//####[34]####
//####[34]####
        return TaskpoolFactory.getTaskpool().enqueue(taskinfo);//####[34]####
    }//####[34]####
    public void __pt__run(int left, int right) {//####[34]####
        qssort(left, right);//####[35]####
    }//####[36]####
//####[36]####
//####[38]####
    private void qssort(int left, int right) {//####[38]####
        if (right - left <= granularity) //####[39]####
        {//####[39]####
            insertion(left, right);//####[40]####
        } else if (left < right) //####[41]####
        {//####[41]####
            int pivotIndex = left + (right - left) / 2;//####[42]####
            int pivotNewIndex = partition(left, right, pivotIndex);//####[43]####
            TaskIDGroup g = new TaskIDGroup(1);//####[45]####
            TaskID leftSubList = run(left, pivotNewIndex - 1);//####[47]####
            g.add(leftSubList);//####[48]####
            qssort(pivotNewIndex + 1, right);//####[54]####
            try {//####[55]####
                g.waitTillFinished();//####[56]####
            } catch (InterruptedException e) {//####[57]####
            } catch (ExecutionException e) {//####[59]####
            }//####[61]####
        }//####[62]####
    }//####[63]####
//####[65]####
    private int partition(int leftIndex, int rightIndex, int pivotIndex) {//####[66]####
        T pivot = list.get(pivotIndex);//####[68]####
        swap(pivotIndex, rightIndex);//####[69]####
        int storeIndex = leftIndex;//####[70]####
        for (int i = leftIndex; i < rightIndex; i++) //####[72]####
        {//####[72]####
            T t = list.get(i);//####[73]####
            if (t.compareTo(pivot) <= 0) //####[75]####
            {//####[75]####
                swap(i, storeIndex);//####[76]####
                storeIndex++;//####[77]####
            }//####[78]####
        }//####[79]####
        swap(storeIndex, rightIndex);//####[81]####
        return storeIndex;//####[82]####
    }//####[83]####
//####[85]####
    private void swap(int leftIndex, int rightIndex) {//####[85]####
        T t = list.get(leftIndex);//####[86]####
        list.set(leftIndex, list.get(rightIndex));//####[87]####
        list.set(rightIndex, t);//####[88]####
    }//####[89]####
//####[92]####
    private void insertion(int offset, int limit) {//####[92]####
        for (int i = offset; i < limit + 1; i++) //####[93]####
        {//####[93]####
            T valueToInsert = list.get(i);//####[94]####
            int hole = i;//####[95]####
            while (hole > 0 && valueToInsert.compareTo(list.get(hole - 1)) < 0) //####[97]####
            {//####[98]####
                list.set(hole, list.get(hole - 1));//####[99]####
                hole--;//####[100]####
            }//####[101]####
            list.set(hole, valueToInsert);//####[103]####
        }//####[104]####
    }//####[105]####
}//####[105]####

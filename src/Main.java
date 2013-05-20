import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import paratask.runtime.ParaTask;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		List<Integer> unsorted = generateRandomNumbers(10000000);
		InPlaceQS qs = new InPlaceQS(250);
		
		long start,end;
		start = System.currentTimeMillis();
		List<Integer>sorted = qs.sort(unsorted);
		end = System.currentTimeMillis();

		System.out.println("Time (ms): " +(end-start));
		System.out.println("Is sorted? " + inOrderInteger(sorted));
		System.out.println("Is same size? " + (sorted.size() == unsorted.size()));
		
	}
	
	public static final List<Integer> generateRandomNumbers(int amount) {
		List<Integer> nums = new ArrayList<Integer>(amount);
		Random r = new Random();
		for (int i = 0; i < amount; i++) {
			nums.add(r.nextInt(amount));
		}

		return nums;
	}

	public static final boolean inOrderInteger(List<Integer> list) {
		Integer previous, current;
		previous = list.get(0);

		for (int i = 1; i < list.size(); i++) {
			current = list.get(i);

			if (previous.compareTo(current) > 0){
				System.out.println(previous + "  " + current);
				return false;
			}

			previous = current;
		}

		return true;
	}
}

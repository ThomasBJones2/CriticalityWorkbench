import java.util.*;
import java.lang.reflect.*;
import java.lang.Thread;

public aspect RandomMethod{

	static ArrayList<Distance> distances = new ArrayList<>();

	static ArrayList<Integer> timeCounts = new ArrayList<>();


	public static synchronized void registerTimeCount(){
		RunId curId = new RunId(Thread.currentThread().getId());
		curId = Experimenter.getId(curId);
		Distance theDistance = getDistance(curId);
		timeCounts.add(theDistance.timeCount);
	}

	public static int getAverageTimeCount(){
		double out = 0;
		for(int i = 0; i < timeCounts.size(); i ++){
			out += ((double) timeCounts.get(i) / (double) timeCounts.size());
		}
		return (int)out;
	}

	public void printThisAspect(){
		RunId curId = new RunId(Thread.currentThread().getId());
		curId = Experimenter.getId(curId);
		Distance theDistance = getDistance(curId);

		System.out.println("On thread: " + curId.getThreadId() + ": ");
		System.out.println("The time count is: " + theDistance.timeCount);
		for(MethodTimeCount mTC : theDistance.getMethodTimeCounts()){
		System.out.println("The time count on method " + mTC.methodName + 
			" is " + mTC.timeCount);
		}
	}

	pointcut Randomize(): call(@Randomize * *(..));

	pointcut PrintAspect() : call(void *.printAspect());

	after() : PrintAspect() {
		printThisAspect();
	}

	private static synchronized Distance getDistance(RunId curId){
		Distance checkDistance = new Distance(curId.getRunName(), curId.getThreadId());
		while(distances.indexOf(checkDistance) != -1 &&
			distances.get(distances.indexOf(checkDistance)).runName != curId.getRunName()){
			distances.remove(distances.indexOf(checkDistance));
		}

		if(distances.indexOf(checkDistance) == -1){
			distances.add(checkDistance);
		}
		
		return distances.get(distances.indexOf(checkDistance));
	}

	boolean forcedError(int timeCount, RunId curId){
		return timeCount == curId.errorPoint && curId.errorful;
	}

	boolean unForcedError(int timeCount, RunId curId){
		return timeCount != curId.errorPoint;
	}

	Object around() : Randomize() {
		Object targetObject = thisJoinPoint.getTarget();    	
		final Object[] args = thisJoinPoint.getArgs();

		RunId curId = new RunId(Thread.currentThread().getId());
		curId = Experimenter.getId(curId);
		if(curId.getExperiment() == true){
			Random rand = curId.getRand();
			Distance theDistance = getDistance(curId);


	/*		for(Object arg : args){
				System.out.println(arg.getClass());
			}
			System.out.println(thisJoinPoint.getTarget().toString());

			for(Object arg : args){
				System.out.print("the arguments: " + arg);
			}
			System.out.println();
			System.out.println(thisJoinPointStaticPart.getSignature().getName());
		
			System.out.println(targetObject.getClass().toString());
			for(Method m : targetObject.getClass().getDeclaredMethods()){
				System.out.println(m.toString());
			}
			System.out.println();
			for(Method m : Graph.class.getMethods()){
				System.out.println(m.toString());
			}*/

			//incrememnt the time count, i.e. how often a randomizable object has been called.
			theDistance.timeCount ++;

			String methodName = thisJoinPointStaticPart.getSignature().getDeclaringTypeName()
			+ "." + thisJoinPointStaticPart.getSignature().getName();
		
			MethodTimeCount handle = new MethodTimeCount(methodName);		

			if(theDistance.mTimeCount.contains(handle)){
				theDistance.mTimeCount.get(theDistance.mTimeCount.indexOf(handle)).increment();

			} else {
				theDistance.mTimeCount.add(new MethodTimeCount(methodName, 1));
			}

			if((rand.nextDouble() < 0.1 && 
					unForcedError(theDistance.timeCount, curId)) || 
					forcedError(theDistance.timeCount, curId)) {
				try {
					Class[] types = new Class[args.length + 1];
					types[0] = Random.class;
					for(int i = 1; i < args.length + 1; i ++){
						types[i] = args[i - 1].getClass();
					}

					Object[] arguments = new Object[args.length + 1];
					arguments[0] = rand;
					for(int i = 1; i < args.length + 1; i ++){
						arguments[i] = args[i - 1];
					}

					Method m = targetObject.getClass().getDeclaredMethod(thisJoinPointStaticPart.getSignature().getName() + "Rand", types);

					return m.invoke(targetObject, arguments);
				} catch (IllegalAccessException e) {
					System.out.println("IllegalAccessException " + e);
				} catch (InvocationTargetException e) {
					System.out.println("InvocationTargetException " + e);
				} catch (NoSuchMethodException e) {
					System.out.println("NoSuchMethodException " + e);
				}
			}
		}


		return proceed();
	}

/*	after() returning (double cow): callMaxFlowFF() {
		System.out.println("doubleBarf " + cow);
	}

	pointcut callMaxFlowFF2(): call(!@Randomize double *()) ;

	after() returning (double cow): callMaxFlowFF2() {
		System.out.println("superbarf " + cow);
	}*/
}

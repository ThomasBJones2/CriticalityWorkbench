package InputObjects;

import RandComp.*;
import java.util.ArrayList;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.*;
import java.lang.Math;

public class NaiveMultiply 
	implements Experiment<InputIntegers, NaiveMultiply> {
	BigInteger theProduct;

	NaiveMultiply(){}

	public static NaiveMultiply emptyObject(){return new NaiveMultiply();}

	void multiply(InputIntegers xy){
		BigInteger x = xy.getX();
		BigInteger y = xy.getY();
		
		BigInteger out = BigInteger.ZERO;
		boolean negative = x.compareTo(BigInteger.ZERO)<0;
		x = x.abs();

		for(int i = 0; i < x.bitLength(); i ++){
			
			if(check(x.testBit(i))){
				//System.out.println(i +  " " + x.bitCount());
				BigInteger curStep = new BigInteger(y.toString());
				for(int j = 0; j < i; j ++){
					curStep = curStep.multiply(new BigInteger("2", 10));
				}
				out = add(out, curStep);
			}
		}
		theProduct = out;		
		if (negative)
			theProduct = BigInteger.ZERO.subtract(theProduct);
	}

	@Randomize
	public BigInteger add(BigInteger a, BigInteger b){
		return a.add(b);
	}

	public BigInteger addRand(Random rand, BigInteger a, BigInteger b){
		BigInteger out = a.add(b);
		out = out.flipBit(Math.abs(rand.nextInt(out.bitLength())));
		return out;
	}


	@Randomize
	public Boolean check(Boolean a){
		return a;
	}

	public Boolean checkRand(Random rand, Boolean a){
		return !a;
	}


	public void experiment(InputIntegers in){
		multiply(in);
	}

	public ArrayList<DefinedLocation> getCurrentLocations(){
		return new ArrayList<DefinedLocation>();
	}

	public Score[] scores(NaiveMultiply correctObject){
		Score[] out = new Score [3];
		out[0] = ScorePool.absolutePercentValueBigInteger(this.theProduct, correctObject.theProduct);
		out[1] = ScorePool.absoluteValueBigInteger(this.theProduct, correctObject.theProduct);
		out[2] = ScorePool.logarithmAbsoluteValueBigInteger(this.theProduct, 
				correctObject.theProduct);
		return out; 

	}

	public static void main(String[] args){
		InputIntegers a = new InputIntegers(args[0], args[1]);	
		NaiveMultiply bob = new NaiveMultiply();
		bob.experiment(a);
		System.out.println(a.getX().toString(10) + "X" + 
				a.getY().toString(10) + 
				" = " + bob.theProduct.doubleValue());
	}
}

package com.criticalityworkbench.inputobjects;

import com.criticalityworkbench.randcomphandler.*;
import java.util.ArrayList;
import java.lang.reflect.*;
import java.math.BigInteger;
import java.util.Random; 

//Implements Strassen's Matrix Multiplication
public class DACMatrixMultiply
	implements Experiment<InputMatrices, DACMatrixMultiply> {
	Matrix output;
	InputMatrices inputs;

	//'empty object' must be scorable
	DACMatrixMultiply(){
	    output = new Matrix(1);
      output.randomize(new Random());
	}

	public static DACMatrixMultiply emptyObject(){return new DACMatrixMultiply();}

	public void experiment(InputMatrices in){
		inputs = in;
		output = dacMultiply(in);
	}

	public Matrix dacMultiply(InputMatrices input){
		Matrix out = new Matrix(input.size);
		if(input.size == 1){	
			NaiveMultiply mult = NaiveMultiply.emptyObject();
			mult.multiply(new InputIntegers(input.A.values[0][0], input.B.values[0][0]));
			out.values[0][0] = mult.theProduct;
			return out;
		}
			
		Matrix a = Matrix.halfSplit(input.A, 0);
		Matrix b = Matrix.halfSplit(input.A, 1);
		Matrix c = Matrix.halfSplit(input.A, 2);
		Matrix d = Matrix.halfSplit(input.A, 3);
		Matrix e = Matrix.halfSplit(input.B, 0);
		Matrix f = Matrix.halfSplit(input.B, 1);
		Matrix g = Matrix.halfSplit(input.B, 2);
		Matrix h = Matrix.halfSplit(input.B, 3);

		Matrix p1 = dacMultiply(new InputMatrices(a, f.subtract(h)));
		Matrix p2 = dacMultiply(new InputMatrices(a.add(b), h));
		Matrix p3 = dacMultiply(new InputMatrices(c.add(d), e));
		Matrix p4 = dacMultiply(new InputMatrices(d, g.subtract(e)));
		Matrix p5 = dacMultiply(new InputMatrices(a.add(d), e.add(h)));
		Matrix p6 = dacMultiply(new InputMatrices(b.subtract(d), g.add(h)));
		Matrix p7 = dacMultiply(new InputMatrices(a.subtract(c), e.add(f)));
			
		Matrix new_a = p5.add(p4).add(p6).subtract(p2);
		Matrix new_b = p1.add(p2);
		Matrix new_c = p3.add(p4);
		Matrix new_d = p1.add(p5).subtract(p3).subtract(p7);

		out = Matrix.merge(new_a, new_b, new_c, new_d); 
		return out;
	}

	public ArrayList<DefinedLocation> getCurrentLocations(){
		return output.getCurrentLocations();
	}

  public String toString(){
    return output.toString();
	}

	public Score[] scores(DACMatrixMultiply correctObject){
		Score[] out = new Score [3];
		out[0] = ScorePool.frobeniusNorm(this.output, correctObject.output);
		out[1] = ScorePool.infinityNorm(this.output, correctObject.output);
		out[2] = ScorePool.logFrobeniusNorm(this.output, correctObject.output);
		return out; 

	}

	public static void main(String[] args){
		com.criticalityworkbench.randcomphandler.RandomMethod.randomize = false;
		Matrix a = new Matrix(8);
		Matrix b = new Matrix(8);
		Random rand = new Random();
		a.randomize(rand);
		b.randomize(rand);
		InputMatrices input = new InputMatrices(a,b);
		InputMatrices input2 = new InputMatrices(input);
		DACMatrixMultiply mult = new DACMatrixMultiply();
		Matrix out = mult.dacMultiply(input);
		NaiveMatrixMultiply mult2 = new NaiveMatrixMultiply();
		Matrix out2 = mult2.naiveMultiply(input2);
		System.out.print("Printing the matrices\n");
		a.print();
		System.out.print("=====================================\n");
		b.print();
		System.out.print("=====================================\n");
		out.print();
		out2.print();
	}
}

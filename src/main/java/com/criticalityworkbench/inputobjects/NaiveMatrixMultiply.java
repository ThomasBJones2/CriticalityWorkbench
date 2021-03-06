package com.criticalityworkbench.inputobjects;

import com.criticalityworkbench.randcomphandler.*;
import java.util.ArrayList;
import java.lang.reflect.*;
import java.math.BigInteger;

public class NaiveMatrixMultiply
	implements Experiment<InputMatrices, NaiveMatrixMultiply> {
	Matrix output;
	InputMatrices inputs;

	NaiveMatrixMultiply(){}

	public static NaiveMatrixMultiply emptyObject(){return new NaiveMatrixMultiply();}

	public void experiment(InputMatrices in){
		inputs = in;
		output = naiveMultiply(in);
	}

	public String toString(){
      return output.toString();
	}

	public Matrix naiveMultiply(InputMatrices input){
		Matrix out = new Matrix(input.size);
		for(int i = 0; i < input.size; i ++){
			for(int j = 0; j < input.size; j ++){
				out.values[i][j] = BigInteger.ZERO;
				for(int k = 0; k < input.size; k ++){
					NaiveMultiply mult = NaiveMultiply.emptyObject();
					mult.multiply(new InputIntegers(input.A.values[i][k], input.B.values[k][j]));
					out.values[i][j] = out.values[i][j].add(mult.theProduct);
				}
			}
		}
		return out;
	}

	public ArrayList<DefinedLocation> getCurrentLocations(){
		return output.getCurrentLocations();
	}

	public Score[] scores(NaiveMatrixMultiply correctObject){
		Score[] out = new Score [3];
		out[0] = ScorePool.frobeniusNorm(this.output, correctObject.output);
		out[1] = ScorePool.infinityNorm(this.output, correctObject.output);
		out[2] = ScorePool.logFrobeniusNorm(this.output, correctObject.output);
		return out; 

	}
}

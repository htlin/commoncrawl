package gov.ameslab.cydime.cc;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class DoubleMaxReducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

	private DoubleWritable mResult = new DoubleWritable();
	
	@Override
	public void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
		double max = 0.0;
		for (DoubleWritable v : values) {
			double d = v.get();
			if (d > max) {
				max = d;
			}
		}
		
		mResult.set(max);
		context.write(key, mResult);
	}
	
}

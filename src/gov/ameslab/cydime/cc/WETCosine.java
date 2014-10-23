package gov.ameslab.cydime.cc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.commoncrawl.warc.WARCFileInputFormat;

/**
 * @author 
 */
public class WETCosine extends Configured implements Tool {
	private static final Logger LOG = Logger.getLogger(WETCosine.class);
	
	/**
	 * Main entry point that uses the {@link ToolRunner} class to run the Hadoop job. 
	 */
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new WETCosine(), args);
		System.exit(res);
	}

	/**
	 * Builds and runs the Hadoop job.
	 * @return	0 if the Hadoop job completes successfully and 1 otherwise.
	 */
	@Override
	public int run(String[] arg0) throws Exception {
		Configuration conf = getConf();
		Job job = new Job(conf);
		job.setJarByClass(WETCosine.class);
		job.setNumReduceTasks(1);
		
//		String inputPath = "s3n://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1409535925433.20/wet/CC-MAIN-20140901014525-00468-ip-10-180-136-8.ec2.internal.warc.wet.gz";
		String inputPath = "s3n://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2014-35/segments/1409535925433.20/wet/*.warc.wet.gz";
		LOG.info("Input path: " + inputPath);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		
		String outputPath = "s3n://commoncrawl.ameslab.gov/out/";
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		job.setInputFormatClass(WARCFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(DoubleWritable.class);
	    
	    job.setMapperClass(DocCosineMapper.class);
	    job.setCombinerClass(DoubleMaxReducer.class);
		job.setReducerClass(DoubleMaxReducer.class);
		
	    if (job.waitForCompletion(true)) {
	    	return 0;
	    } else {
	    	return 1;
	    }
	}
}

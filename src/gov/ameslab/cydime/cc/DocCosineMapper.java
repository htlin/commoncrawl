package gov.ameslab.cydime.cc;

import gov.ameslab.cydime.util.Histogram;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;

public class DocCosineMapper extends Mapper<Text, ArchiveReader, Text, DoubleWritable> {
	
	static final Logger LOG = Logger.getLogger(DocCosineMapper.class);
	
	private Text mOutKey = new Text();
	private DoubleWritable mOutVal = new DoubleWritable(0.0);
	
	private Histogram<String> mBaseDoc;
	
	@Override
	protected void setup(Context context) throws IOException, InterruptedException {
		super.setup(context);
		
		mBaseDoc = loadHistogram(context, "s3n://commoncrawl.ameslab.gov/in/ameslab.txt");
	}
	
	private Histogram<String> loadHistogram(String text) throws IOException {
		Histogram<String> hist = new Histogram<String>();
		StringTokenizer tok = new StringTokenizer(text);
		while (tok.hasMoreTokens()) {
			hist.increment(tok.nextToken());
		}
		hist.cacheTwoNorm();
		return hist;
	}
	
	private Histogram<String> loadHistogram(Context context, String s3File) throws IOException {
		Configuration conf = context.getConfiguration();
		Path path = new Path(s3File);
		FileSystem fs = path.getFileSystem(conf);
		String content = IOUtils.toString(fs.open(path));
		return loadHistogram(content);
	}

	private String getHostname(String url) {
		int begin = url.indexOf("//");
		if (begin < 0) {
			begin = 0;
		} else {
			begin = begin + 2;
		}
		
		int end = url.indexOf("/", begin);
		if (end < 0) {
			end = url.length();
		}
		
		return url.substring(begin, end);
	}
	
	@Override
	public void map(Text key, ArchiveReader value, Context context) throws IOException {
		for (ArchiveRecord r : value) {
			try {
				if (r.getHeader().getMimetype().equals("text/plain")) {
//					LOG.debug(r.getHeader().getUrl() + " -- " + r.available());
					String hostname = getHostname(r.getHeader().getUrl().toLowerCase());
					
					String content = IOUtils.toString(r);
					Histogram<String> doc = loadHistogram(content);
					double cosine = 0.0;
					if (doc.getTwoNorm() > 0.0) {
						cosine = Histogram.getCosine(mBaseDoc, doc);
					}
					
					mOutKey.set(hostname);
					mOutVal.set(cosine);
					context.write(mOutKey, mOutVal);
				}
			} catch (Exception ex) {
				LOG.error("Caught Exception", ex);
			}
		}
	}

}
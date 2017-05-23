//Wrapper for maximum-entropy training

//NYU - Natural Language Processing - Prof. Grishman

//invoke by:  java  MEtrain  dataFile  modelFile

import java.io.*;
import opennlp.maxent.*;
import opennlp.maxent.io.*;
import opennlp.model.*;

public class MEtrain {

 public void train (String dataFileName, String modelFileName) {
	try {
	    // read events with tab-separated features
	    FileReader datafr = new FileReader(new File(dataFileName));
	    EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr), "\t");
	    // train model using 100 iterations, ignoring events occurring fewer than 4 times
	    GISModel model = GIS.trainModel(es, 100, 4);
	    // save model
	    File outputFile = new File(modelFileName);
	    GISModelWriter writer = new SuffixSensitiveGISModelWriter(model, outputFile);
	    writer.persist();
	    System.out.println("Model created, the output file is Model.MEtrain");
	} catch (Exception e) {
	    System.out.print("Unable to create model due to exception: ");
	    System.out.println(e);
	}
 }
}
// Wrapper for maximum-entropy tagging

// NYU - Natural Language Processing - Prof. Grishman

// invoke by:  java  MEtag dataFile  model  responseFile

import java.io.*;
import opennlp.maxent.*;
import opennlp.maxent.io.*;

// reads line with tab separated features
//  writes feature[0] (token) and predicted tag

public class MEtag {

    public void tag (String dataFileName, String modelFileName, String responseFileName) {
	try {
	    GISModel m = (GISModel) new SuffixSensitiveGISModelReader(new File(modelFileName)).getModel();
	    BufferedReader dataReader = new BufferedReader (new FileReader (dataFileName));
	    PrintWriter responseWriter = new PrintWriter (new FileWriter (responseFileName));
	    String priorTag = "@@";
	    String line;
	    String[] features = new String[7];
	    while ((line = dataReader.readLine()) != null) {
			if (line.equals("")) {
				priorTag = "@@";
			    responseWriter.println();
			} else {
			    String[] tokens = line.split("\t");
			    for (int i = 0; i < 6; ++i) {
					features[i] = tokens[i];
				}
			    features[6] = "preTag=" + priorTag;
			    priorTag = m.getBestOutcome(m.eval(features));
			    responseWriter.println(features[0].substring(9) + "\t" + priorTag);
			}
	    }
	    dataReader.close();
	    responseWriter.close();
	    System.out.println("Model tagging finished, the output file is"+responseFileName);
	} catch (Exception e) {
	    System.out.print("Error in data tagging: ");
	    e.printStackTrace();
	}
    }

}
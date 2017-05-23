import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.text.*;


public class MEMMmain {
	public static void main(String[] args) throws Exception {
       
		// Build feature from WSJ_02-21.pos-chunk
		System.out.println("Start reading from WSJ_02-21.pos-chunk...");
		String pathRead = "WSJ_02-21.pos-chunk";
		String pathWrite = "WSJ_02-21_feature-enhanced.txt";
		FeatureBuilder t = new FeatureBuilder();
		t.readFile(pathRead, pathWrite);
		
        //Train a model from WSJ_02-21_feature-enhanced.txt
		System.out.println("Training the model...");
		String dataFileName = "WSJ_02-21_feature-enhanced.txt";
		String modelFileName = "Model.MEtrain";
		MEtrain trainModel = new MEtrain();
		trainModel.train(dataFileName, modelFileName);
		
        // Build feature from WSJ_24.pos
		System.out.println("Start reading from WSJ_24.pos...");
		FeatureBuilder2 test = new FeatureBuilder2();
		String testFile = "WSJ_24.pos";
		String testProcessedFile = "WSJ_24_feature-enhanced.txt";
		test.readFile(testFile, testProcessedFile);
		
		//Tagging WSJ_24.pos by using the model
		System.out.println("Tagging with model...");
		String resultFileName = "WSJ_24_test.chunk";
		MEtag modelTagger = new MEtag();
		modelTagger.tag(testProcessedFile,modelFileName,resultFileName);	
		
		// evaluation for the tagAccuracy
		System.out.println("Evaluate the results. ");
		MEMMmain maxEnt = new MEMMmain();
		maxEnt.tagAccuracy("WSJ_24_test.chunk", "WSJ_24.chunk");
		maxEnt.precisionAndRecall("WSJ_24_test.chunk", "WSJ_24.chunk");
		
	}

	public void tagAccuracy(String resultFile, String correctFile) throws FileNotFoundException {
		Scanner sc1 = new Scanner(new File(resultFile));
		Scanner sc2 = new Scanner(new File(correctFile));
		int totalTags = 0, correctTags = 0;

		while (sc1.hasNextLine() && sc2.hasNextLine()) {
			String line1 = sc1.nextLine();
			String line2 = sc2.nextLine();
			if (line1.trim().length() > 0 && line2.trim().length() > 0) {
				String[] t1 = line1.split("\t");
				String[] t2 = line2.split("\t");
				totalTags++;
				if (t1[t1.length - 1].equals(t2[t2.length - 1]))
					correctTags++;
			}
		}
		sc1.close();
		sc2.close();
		System.out.println("Total tokens = " + totalTags);
		System.out.println("Tokens with correct tag = " + correctTags);
		DecimalFormat df = new DecimalFormat("##.##");
		System.out.println("Accuracy = " + df.format(100*(double) correctTags / (double) totalTags));
	}

	public void precisionAndRecall(String resultFile, String correctFile) throws FileNotFoundException {
		ArrayList<String> result = new ArrayList<String>();
		ArrayList<String> test = new ArrayList<String>();
		ArrayList<ChunkRange> checkBag = new ArrayList<ChunkRange>();
		int response = 0, key = 0, correct = 0;
		Scanner sc1 = new Scanner(new File(resultFile));
		Scanner sc2 = new Scanner(new File(correctFile));
		while (sc1.hasNextLine() && sc2.hasNextLine()) {
			String line1 = sc1.nextLine();
			String line2 = sc2.nextLine();
			if (line1.trim().length() == 0) {
				int len = result.size();
				int i = 0;
				while (i < len) {
					if (test.get(i).equals("O")) {
						i++;
						continue;
					}
					if (test.get(i).equals("I-NP") || test.get(i).equals("B-NP")) {
						int j = i + 1;
						while ( j < len && test.get(j).equals("I-NP") ) {
							j++;
						}
						ChunkRange chunk = new ChunkRange(i, j - 1);
						checkBag.add(chunk);
						key++;
						i = j;
					}
				}

				int x = 0;
				while (x < len) {
					if (result.get(x).equals("O")) {
						x++;
						continue;
					}
					if (result.get(x).equals("I-NP") || result.get(x).equals("B-NP")) {
						int j = x + 1;
						while (j < len && result.get(j).equals("I-NP") ) {
							j++;
						}
						ChunkRange chunk = new ChunkRange(x, j - 1);
						for(ChunkRange c : checkBag){
							if(c.equals(chunk)){
								correct++;
								break;		
							}
						}
						response++;
						x = j;
					}
				}
				result.clear();
				test.clear();
				checkBag.clear();
				continue;
			}
			result.add(getTag(line1));
			test.add(getTag(line2));
			
		}
		sc1.close();
		sc2.close();
		DecimalFormat df = new DecimalFormat("##.##");
		System.out.println( key +"groups in key");
		System.out.println( response +"groups in response");
		System.out.println( correct +"correct groups");
		double precision = 100*(double) correct / (double) response;
		System.out.println("Precision = " + df.format(precision));
		double recall = 100*(double) correct / (double) key;
		System.out.println("Recall = "+ df.format(recall));
		double F = (double) (2 * precision * recall / (precision + recall));
		System.out.println("F-measure = " + df.format(F));
	}

	private String getTag(String line) {
		String[] tokens = line.split("\t");
		String tag = tokens[tokens.length - 1];
		return tag;
	}

}
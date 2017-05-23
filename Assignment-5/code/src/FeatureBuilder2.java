import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class FeatureBuilder2 {
	private Scanner reader;
	private PrintWriter writer;
	private ArrayList<ArrayList<String>> a;

	public FeatureBuilder2() {
		a = new ArrayList<ArrayList<String>>();
	}

	public void readFile(String pr, String pw) throws Exception {
		writer = new PrintWriter(pw);
		reader = new Scanner(new File(pr));
		String line = "";

		while (reader.hasNextLine()) {
			line = reader.nextLine();
			if (line.trim().length() == 0) {
				int len = a.size();
				for (int i = 0; i < len; i++) {
					writer.print("curToken=" + a.get(i).get(0) + "\t");
					writer.print("curPOS=" + a.get(i).get(1) + "\t");
					writer.print("prePOS=");
					if (i > 0)
						writer.print(a.get(i - 1).get(1) + "\t");
					else
						writer.print("@@\t");
					writer.print("postPOS=");
					if (i < len - 1)
						writer.print(a.get(i + 1).get(1) + "\t");
					else
						writer.print("#\t");
					writer.print("preCur=");
					if (i > 0)
						writer.print(a.get(i - 1).get(1) + "+" + a.get(i).get(1) + "\t");
					else
						writer.print("@@" + "+" + a.get(i).get(1) + "\t");
					writer.print("curPost=");
					if (i < len - 1)
						writer.print(a.get(i).get(1) + "+" + a.get(i + 1).get(1) + "\n");
					else
						writer.print(a.get(i).get(1) + "+" + "#\t"+ "\n");
				}
				writer.println();
				a.clear();
			} else {
				String[] tokens = line.split("\t");
				ArrayList<String> list = new ArrayList<String>();
				for (int i = 0; i < tokens.length; i++) {
					list.add(tokens[i]);
				}
				a.add(list);

			}
		}
		reader.close();
		writer.close();
		System.out.println("Test data processed, the output file is "+pw);
	}
}
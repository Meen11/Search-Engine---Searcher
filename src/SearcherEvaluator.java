//Name: Ravikan T., Pranpariya S., Pawin S.
//Section: 1
//ID: 5988046, 5988202, 5988222

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SearcherEvaluator {
	private List<Document> queries = null;				//List of test queries. Each query can be treated as a Document object.
	private  Map<Integer, Set<Integer>> answers = null;	//Mapping between query ID and a set of relevant document IDs

	public List<Document> getQueries() {
		return queries;
	}

	public Map<Integer, Set<Integer>> getAnswers() {
		return answers;
	}

	/**
	 * Load queries into "queries"
	 * Load corresponding documents into "answers"
	 * Other initialization, depending on your design.
	 * @param corpus
	 */
	public SearcherEvaluator(String corpus)
	{
		String queryFilename = corpus+"/queries.txt";
		String answerFilename = corpus+"/relevance.txt";

		//load queries. Treat each query as a document.
		this.queries = Searcher.parseDocumentFromFile(queryFilename);
		this.answers = new HashMap<Integer, Set<Integer>>();
		//load answers
		try {
			List<String> lines = FileUtils.readLines(new File(answerFilename), "UTF-8");
			for(String line: lines)
			{
				line = line.trim();
				if(line.isEmpty()) continue;
				String[] parts = line.split("\\t");
				Integer qid = Integer.parseInt(parts[0]);
				String[] docIDs = parts[1].trim().split("\\s+");
				Set<Integer> relDocIDs = new HashSet<Integer>();
				for(String docID: docIDs)
				{
					relDocIDs.add(Integer.parseInt(docID));
				}
				this.answers.put(qid, relDocIDs);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Returns an array of 3 numbers: precision, recall, F1, computed from the top *k* search results
	 * returned from *searcher* for *query*
	 * @param query
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getQueryPRF(Document query, Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/

		List<SearchResult> searchResults = searcher.search(query.getRawText(), k);
		Set<Integer> R = new HashSet<>();
		for (SearchResult searchResult: searchResults){
			R.add(searchResult.getDocument().getId());
		}
		Set<Integer> G = new HashSet<>();
		G.addAll(answers.get(query.getId()));
		Set<Integer> Intersection = new HashSet<>(R);
		Intersection.retainAll(G);

		double Precision = (double) Intersection.size() / (double) R.size();
		if (Double.isNaN(Precision)) Precision = 0;
		double Recall = (double) Intersection.size() / (double) G.size();
		if (Double.isNaN(Recall)) Recall = 0;
		double F1 = (2 * Precision * Recall) / (Precision + Recall);
		if (Double.isNaN(F1)) F1 = 0;

		return new double[]{Precision, Recall, F1};
		/****************************************************************/
	}

	/**
	 * Test all the queries in *queries*, from the top *k* search results returned by *searcher*
	 * and take the average of the precision, recall, and F1.
	 * @param searcher
	 * @param k
	 * @return
	 */
	public double[] getAveragePRF(Searcher searcher, int k)
	{
		/*********************** YOUR CODE HERE *************************/

		List<List<Double>> PRF = new ArrayList<>();
		for (Document query: queries){
			double[] prf = getQueryPRF(query, searcher, k);
			List<Double> list = new ArrayList<>();
			for (int i=0; i<prf.length; i++) list.add(prf[i]);
			PRF.add(list);
		}

		double sumP=0, sumR=0, sumF=0;
		for (List<Double> list: PRF){
			sumP += list.get(0);
			sumR += list.get(1);
			sumF += list.get(2);

		}

		double avgP, avgR, avgF;
		avgP = sumP / queries.size();
		avgR = sumR / queries.size();
		avgF = sumF / queries.size();

		return new double[]{avgP, avgR, avgF};
		/****************************************************************/
	}
}

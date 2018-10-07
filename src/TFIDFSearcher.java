//Name:
//Section:
//ID:

import java.util.*;

public class TFIDFSearcher extends Searcher
{
	String[] termsArray;	/** Implemented */
	double[] TermIDF;		/** Implemented */
	double[][] DocVector;

	public TFIDFSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/

		Set<String> terms = new HashSet<>();
		for (Document document: documents){
			terms.addAll(document.getTokens());
		}
		termsArray = terms.toArray(new String[terms.size()]);
		TermIDF = new double[termsArray.length];
		double[][] TermTF = new double[termsArray.length][documents.size()];
		DocVector = new double[termsArray.length][documents.size()];

		/** Computing TF&IDF for each term in documents */
		for (int i=0; i<termsArray.length; i++){
			int IDF_count=0;
			for (int j=0; j<documents.size(); j++){
				int TF_count=0;
				if (documents.get(j).getTokens().contains(termsArray[i])) IDF_count++;
				for (String token: documents.get(j).getTokens()){
					if (termsArray[i].equals(token)) TF_count++;
				}
				if (TF_count == 0) TermTF[i][j] = 0.0;
				else TermTF[i][j] = 1 + Math.log10(TF_count);
			}
			TermIDF[i] = Math.log10(1 + ((double) documents.size() / (double) IDF_count));
		}

		for (int i=0; i<termsArray.length; i++){
			for (int j=0; j<documents.size(); j++){
				DocVector[i][j] = TermIDF[i] * TermTF[i][j];
			}
		}
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/

		List<String> queries = tokenize(queryString);
		double[] QueryVector = new double[termsArray.length];
		List<SearchResult> searchResultList = new ArrayList<>();

		/** Computing TF for query & create a Query vector*/
		for (int i=0; i<termsArray.length; i++){
			int TF_count=0;
			for (String query: queries){
				if (query.equals(termsArray[i])) TF_count++;
			}
			double QueryTF;
			if (TF_count == 0) QueryTF = 0.0;
			else QueryTF = 1 + Math.log10(TF_count);
			QueryVector[i] = TermIDF[i] * QueryTF;
		}

		/** Computing scores using cosine similarity technique for each document*/
		for (int i=0; i<DocVector[0].length; i++){
			double sum_DmulQ=0, sum_Qsquare=0, sum_Dsquare=0;
			for (int j=0; j<DocVector.length; j++){
				sum_DmulQ += (QueryVector[j] * DocVector[j][i]);
				sum_Qsquare += Math.pow(QueryVector[j], 2);
				sum_Dsquare += Math.pow(DocVector[j][i], 2);
			}
			double score = sum_DmulQ / (Math.sqrt(sum_Qsquare) * Math.sqrt(sum_Dsquare));
			searchResultList.add(new SearchResult(documents.get(i), score));
		}

		/** Sorting searchResultList by score*/
		int n = searchResultList.size();
		List<SearchResult> sortedSearchResultList = new ArrayList<>();
		for(int i=0; i<n; i++){
			for(int j=1; j<(n-i); j++){
				if(searchResultList.get(j-1).getScore() >= searchResultList.get(j).getScore()){
					if (searchResultList.get(j-1).getScore() == searchResultList.get(j).getScore()){
						if (searchResultList.get(j-1).getDocument().getId() < searchResultList.get(j).getDocument().getId()){
							Collections.swap(searchResultList,j-1,j);
						}
					}
					else{
						Collections.swap(searchResultList,j-1,j);
					}
				}
				if (Double.isNaN(searchResultList.get(j-1).getScore()) && Double.isNaN(searchResultList.get(j).getScore())){
					if (searchResultList.get(j-1).getDocument().getId() < searchResultList.get(j).getDocument().getId()){
						Collections.swap(searchResultList,j-1,j);
					}
				}

			}
			sortedSearchResultList.add(searchResultList.get(n-i-1));
		}
		if (k >= sortedSearchResultList.size()) return sortedSearchResultList;
		else return sortedSearchResultList.subList(0,k);
		/***********************************************/
	}
}



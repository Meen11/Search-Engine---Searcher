//Name: Ravikan T., Pranpariya S., Pawin S.
//Section: 1
//ID: 5988046, 5988202, 5988222

import java.util.*;

public class JaccardSearcher extends Searcher{

	Map<Integer, Document> docID_map = new HashMap<>();
	Map<Integer, Set<String>> docMap = new HashMap<>(); /** Implemented */

	public JaccardSearcher(String docFilename) {
		super(docFilename);

		/************* YOUR CODE HERE ******************/
		for (Document document: documents){
			Set<String> tokens = new HashSet<>();
			tokens.addAll(document.getTokens());
			docMap.put(document.getId(), tokens);
			docID_map.put(document.getId(), document);
		}
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/

		List<String> queryList = tokenize(queryString);
		Set<String> querySet = new HashSet<>(queryList);
		List<SearchResult> searchResultList = new ArrayList<>();

		/** Computing scores using Jaacard technique for each document*/
		for (Integer docID: docMap.keySet()){
			Set<String> intersectSet =  new HashSet<>(docMap.get(docID));
			Set<String> unionSet =  new HashSet<>(docMap.get(docID));
			intersectSet.retainAll(querySet);
			unionSet.addAll(querySet);
			double score = (double) intersectSet.size() / (double) unionSet.size();
			searchResultList.add(new SearchResult(docID_map.get(docID), score));
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
			}
			sortedSearchResultList.add(searchResultList.get(n-i-1));
		}

		if (k >= sortedSearchResultList.size()) return sortedSearchResultList;
		else return sortedSearchResultList.subList(0,k);
		/***********************************************/
	}

}

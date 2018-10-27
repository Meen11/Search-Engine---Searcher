//Name: Ravikan T., Pranpariya S., Pawin S.
//Section: 1
//ID: 5988046, 5988202, 5988222

import java.util.*;

public class TFIDFSearcher extends Searcher
{
    public HashSet<String> vocabulary = new HashSet<>();
    public HashMap<String, Double> IDF = new HashMap<>();


    public TFIDFSearcher(String docFilename) {
        super(docFilename);
        /************* YOUR CODE HERE ******************/

        HashMap<String, Set<Integer>> DocFreq = new HashMap<>();

        for(Document doc: documents) {
            for(String term : doc.getTokens()) {
                vocabulary.add(term);
                if (!DocFreq.containsKey(term)) DocFreq.put(term, new HashSet<>());
                DocFreq.get(term).add(doc.getId());
            }
        }
        for(String term: DocFreq.keySet()) {
            IDF.put(term, Math.log10(1.0 +  ((double) documents.size() / (double) DocFreq.get(term).size())));
        }

        /***********************************************/
    }

    @Override
    public List<SearchResult> search(String queryString, int k) {
        /************* YOUR CODE HERE ******************/

        List<String> Queries = Searcher.tokenize(queryString);
        Map<String, Double> qVec = new HashMap<>();
        List<SearchResult> searchResultList = new ArrayList<>();

        /**Adding IDF of terms in query to IDF*/
        for(String query: Queries) {
            int qFreq = 0;
            vocabulary.add(query);
            for(Document document: documents) {
                if(document.getTokens().contains(query)) {
                    qFreq++;
                }
            }
            if (qFreq != 0) IDF.put(query, Math.log10(1.0 + ( (double) documents.size() / (double) qFreq)));
            else IDF.put(query, 0.0);

        }

        /**Creating Query vector*/
        for(String term: vocabulary) {
            double TF;
            int freq;
            freq = Collections.frequency(Queries, term);
            if(freq == 0) TF = 0;
            else TF = 1 + Math.log10(freq);
            qVec.put(term, TF * IDF.get(term));
        }

        /**Calculating the cosine similarity*/
        for(Document document : documents) {
            Set<String> union = new TreeSet<>(document.getTokens());
            union.addAll(Queries);

            double w, sum_DmulQ = 0.0, sum_Qsquare = 0.0, sum_Dsquare = 0.0;

            for(String term: union) {
                double TF_D;
                int freq;
                freq = Collections.frequency(document.getTokens(), term);
                if(freq == 0) TF_D = 0.0;
                else TF_D = 1.0 + Math.log10(freq);

                w = TF_D * IDF.get(term);
                sum_DmulQ += w * qVec.get(term);
                sum_Qsquare += Math.pow(qVec.get(term),2.0);
                sum_Dsquare += Math.pow(w,2.0);
            }
            double score = sum_DmulQ / (Math.sqrt(sum_Qsquare) * Math.sqrt(sum_Dsquare));
            searchResultList.add(new SearchResult(document, score));
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

        if (k > sortedSearchResultList.size()) return sortedSearchResultList;
        else return sortedSearchResultList.subList(0,k);
        /***********************************************/
    }
}
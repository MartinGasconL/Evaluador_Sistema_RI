import com.recinfo.collection.Judgements;
import com.recinfo.collection.RecoveredDocument;
import com.recinfo.collection.RecoveredDocuments;

import java.io.*;
import java.util.*;

/**
 * Evaluation of a information retrieval system.
 * Calculate a set of metrics
 */
public class Evaluation {


    Evaluation(String[] args) throws IOException {

        String relevancePath = "qrels";
        String outputPath = "index";
        String recoverSistemResultPath = "index";

        for (int i = 0; i < args.length; i++) {
            if ("-qrels".equals(args[i])) {
                relevancePath = args[i + 1];
                i++;
            } else if ("-results".equals(args[i])) {
                recoverSistemResultPath = args[i + 1];
                i++;
            } else if ("-output".equals(args[i])) {
                outputPath = args[i + 1];
                i++;
            }
        }

        Judgements judgements = new Judgements(relevancePath);
        RecoveredDocuments documents = new RecoveredDocuments(recoverSistemResultPath);
        Metrics metrics = new Metrics();

        metrics.setPrecision(caluclatePrecision(judgements, documents));
        metrics.setRecall(caluclateRecall(judgements, documents));
        metrics.setPrecToK(calculatePrecisionToK(10, documents, judgements));
        metrics.setMAP (calculateMAP(45, documents, judgements));
        metrics.setF1Score (calculateF1Score(documents, metrics));


        for(String infoNeed : documents.getInfoNeeds()) {
            for (Map.Entry<Float,Float> value : calculateRecallPrecision(documents, judgements, infoNeed).entrySet())
                metrics.addRecallPrecisionLine(infoNeed, value.getKey(), value.getValue());
            for (Map.Entry<Float,Float> value : calculateInterpolatedPrecision(documents, judgements, infoNeed).entrySet()) {
                metrics.addRInterpolatedPrecisionLine(infoNeed, value.getKey(), value.getValue());
            }
        }

        BufferedWriter out = new BufferedWriter(new PrintWriter(outputPath));

        out.write(metrics.printMetrics());

        out.close();
    }

    /**
     * Calculate the interpolated precision of a set of documents.
     * @param documents Collection of document which you need to calculate the information need.
     * @param judgements Judgements of the retrieval information system.
     * @param infoNeed infoneed to calculate the interpolated precision.
     * @return A map with the percent of recall and interpolated precision of that recall.
     */
    private Map<Float, Float> calculateInterpolatedPrecision(RecoveredDocuments documents,
                                                 Judgements judgements, String infoNeed) {
        float currentPrecision;
        float currentRecall;
        Map<Float, Float> retval = new HashMap<>();

        for(float targetRecall : Metrics.recalls) {
            int numRelevants = 0;
            int cont = 1;
            float maxPrecision = 0;
            float totalNumRelevants = judgements.getNumRelevantJudgementsByInfoNeed(infoNeed);

            for (Map.Entry<String, RecoveredDocument> doc : documents.getDocumentsByInfoNeed(infoNeed).entrySet()) {
                if (judgements.isRelevant(doc.getValue())) {
                    numRelevants ++;
                }
                currentRecall = (float) numRelevants / totalNumRelevants;
                currentPrecision = (float) numRelevants / cont;
                if (currentRecall >= targetRecall && currentPrecision > maxPrecision) {
                    maxPrecision = currentPrecision;
                }
                cont++;

            }
            retval.put(targetRecall,maxPrecision);
        }
        return retval;
    }

    /**
     * Calculate the recall precision of a set of documents.
     * @param documents Collection of recovered documents to calculate the recall-precision metrics.
     * @param judgements Judgements of the retrieval information system.
     * @param infoNeed infoneed to calculate the recall-precision.
     * @return Return a map with the percent of recall and the associated precision of that recall.
     */
    private Map<Float, Float> calculateRecallPrecision(RecoveredDocuments documents,
                                                       Judgements judgements, String infoNeed) {
        float precision;
        float currentRecall;
        Map<Float, Float> retval = new LinkedHashMap<>();


        float totalNumRelevants = judgements.getNumRelevantJudgementsByInfoNeed(infoNeed);
        for(float r: Metrics.recallsRP) {
            int numRelevants = 0;
            int cont = 1;
            precision = 0;
            for (Map.Entry<String, RecoveredDocument> doc : documents.getDocumentsByInfoNeed(infoNeed).entrySet()) {
                if (judgements.isRelevant(doc.getValue())) {
                    numRelevants++;
                }
                currentRecall = (float) numRelevants / totalNumRelevants;
                if (currentRecall >=  r){
                    precision = (float) numRelevants / cont;
                    break;
                }

                cont++;
            }
            retval.put(r, precision);

        }


        return retval;
    }


    /**
     * Calculate the precision of the first k recovered documents.
     * @param k Number of the recovered top docs.
     * @param documents Collection of documents to evaluate.
     * @param judgements Judgements of the information retrieval system.
     * @return Return a map with the information need and the precision of the k first documents.
     */
    private  Map<String, Float> calculatePrecisionToK(int k, RecoveredDocuments documents, Judgements judgements) {
        Map<String, Float> retval = new HashMap<>();
        int cont;
        int numRelevants;
        for(String infoNeed : documents.getInfoNeeds()) {                                   //Every infoneed
            cont = 1;
            numRelevants = 0;
            for (Map.Entry<String, RecoveredDocument> me : documents.getDocumentsByInfoNeed(infoNeed).entrySet()) {    //Every document per infoneed
                RecoveredDocument rd = me.getValue();
                if(judgements.isRelevant(rd))
                    numRelevants ++;
                if (cont == k)
                    break;

                cont++;
            }
            retval.put(infoNeed, (float)numRelevants/k);
        }
        return retval;
    }


    /**
     * Calculate the MAP measure of a set of documents in every information need.
     * @param k Number of the k top recovered documents.
     * @param documents Collection of documents to evaluate.
     * @param judgements Judgements of the information retrieval system.
     * @return Return a map with the information need and his MAP value associated.
     */
    private  Map<String, Float> calculateMAP(int k, RecoveredDocuments documents, Judgements judgements) {
        Map<String, Float> retval = new HashMap<>();
        int cont;
        int numRelevants;
        float total;
        for(String infoNeed : documents.getInfoNeeds()) {
            cont = 1;
            numRelevants = 0;
            total = 0;
            for (Map.Entry<String ,RecoveredDocument> me : documents.getDocumentsByInfoNeed(infoNeed).entrySet()) {
                RecoveredDocument rd = me.getValue();
                if(judgements.isRelevant(rd)){
                    numRelevants ++;
                    total+= (((float) numRelevants)/cont);
                }

                if (cont == k) break;
                cont++;

            }
            retval.put(infoNeed, total/numRelevants);
        }
        return retval;
    }

    /**
     * Calculates the F1 score metric of a set of recovered documents of every information need.
     * @param documents Collection of documents to evaluate.
     * @param metrics Contains the precision and recall metrics of the system.
     * @return Return the F1 Score of a set of recovered documents.
     */
    private Map<String, Float>  calculateF1Score(RecoveredDocuments documents, Metrics metrics) {
        HashMap<String, Float> retval = new HashMap<>();
        for(String infoNeed: documents.getInfoNeeds()) {
            float precision = metrics.getPrecision(infoNeed);
            float recall = metrics.getRecall(infoNeed);
            retval.put(infoNeed, (2 * precision * recall) / (precision + recall));
        }
            return retval;
    }

    /**
     * Calculate the recall of a set of recovered document in all information need queries
     * @param judgements Judgements of the retrieval information System.
     * @param documents Documents recovered in a information need query
     * @return Returns a map with the information need an his associated recall
     */
    private  HashMap<String, Float> caluclateRecall(Judgements judgements, RecoveredDocuments documents) {
        HashMap<String, Float> retval = new HashMap<>();
        for(String infoNeed: documents.getInfoNeeds()) {
            LinkedHashMap<String, RecoveredDocument> infoNeedDocuments = (LinkedHashMap<String, RecoveredDocument>) documents.getDocumentsByInfoNeed(infoNeed);

            int numRelevantRecoveredDocuments = judgements.getRelevanceOfDocumentSet(infoNeedDocuments);

            retval.put(infoNeed,(float) numRelevantRecoveredDocuments / judgements.getNumRelevantJudgementsByInfoNeed( infoNeed));
        }
        return retval;
    }


    /**
     * Calculate the precision of a set of recovered document in all information need queries
     * @param judgements Judgements of the retrieval information System.
     * @param documents Documents recovered in a information need query
     * @return Returns a map with the information need an his associated recall
     */
    private HashMap<String, Float> caluclatePrecision(Judgements judgements, RecoveredDocuments documents) {
        HashMap<String, Float> retval = new HashMap<>();
        for(String infoNeed: documents.getInfoNeeds()) {
            LinkedHashMap<String, RecoveredDocument> infoNeedDocuments = (LinkedHashMap<String, RecoveredDocument>) documents.getDocumentsByInfoNeed(infoNeed);

            int numRelevantRecoveredDocuments = judgements.getRelevanceOfDocumentSet(infoNeedDocuments);
            retval.put(infoNeed,(float) numRelevantRecoveredDocuments / infoNeedDocuments.size());
        }
        return retval;
    }


    public static void main (String[] args) throws IOException {
        new Evaluation(args);
    }


}

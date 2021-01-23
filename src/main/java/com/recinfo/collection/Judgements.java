package com.recinfo.collection;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class who provides an API to manage the access to a concrete collection of judgements relative to a information need
 */
public class Judgements {
    LinkedHashMap<String, Judgement> judgements;

    public Judgements(String relevancePath) {
        this.judgements = loadJudgements(relevancePath);;
    }

    /**
     * Loads the judgement dataset from a file
     * @param path Path of the file which contains de judgements.
     * @return A map with the documents with his judgement.
     */
    private static LinkedHashMap<String,Judgement> loadJudgements(String path) {
        LinkedHashMap<String, Judgement> retval = new LinkedHashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            Judgement j;
            String line;
            while ((line = in.readLine()) != null) {
                j = new Judgement(line);
                retval.put(j.getIdRecoveredDoc(), j);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retval;
    }

    /**
     * Calculate the number of relevant judgements relatives to an provided information need
     * @param infoNeed Information need which you want to calculate the number of relevant judgements
     * @return The number of relevant
     */
    public int getNumRelevantJudgementsByInfoNeed(String infoNeed) {
        int cont = 0;
        for(Map.Entry me : judgements.entrySet()) {
            Judgement j = ((Judgement) me.getValue());
            if(j.getIdInformationNeed().equals(infoNeed) && j.isRelevant()) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Count the number of relevant documents in a set of documents
     * THE INFORMATION NEED IS NOT NECESSARY BECAUSE THE DOCUMENT ID CONTAINS IT. (DOC ID + INFORMATION NEED)
     * BECAUSE OF THAT YOU ONLY EVALUATE THE SET OF DOCUMENT RELATIVE TO AN INFORMATION NEED INSTEAD OF ALL INFORMATION NEEDS.
     * @param recoveredDocuments Set of documents which you need to evaluate (and count)
     * @return return the number of relevant documents in a set of documents.
     */
    public int getRelevanceOfDocumentSet(LinkedHashMap<String, RecoveredDocument> recoveredDocuments) {
        int cont = 0;
        for (Map.Entry<String, RecoveredDocument> me : recoveredDocuments.entrySet()) {
            if(isRelevant(me.getValue())) {
                cont++;
            }else if(judgements.get(me.getKey()) == null){
                judgements.put(String.valueOf(me.getKey()),new Judgement((me.getValue()).getInfoNeedId(),
                        (me.getValue()).getInfoNeedId(), 0));
            }
        }
        return cont;
    }

    /**
     * Evaluate if a document is relevant in a information need (The information need is included in the document id)
     * @param rd document to evaluate
     * @return true if the document is relevant in the information need.
     */
    public boolean isRelevant(RecoveredDocument rd){
        return judgements.get(rd.getDocumentId()) != null && judgements.get(rd.getDocumentId()).isRelevant();
    }
}

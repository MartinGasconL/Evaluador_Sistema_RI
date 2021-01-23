package com.recinfo.collection;

/**
 * Class who defines a judgement of a document relative to an information need
 */
public class Judgement {
    String idInformationNeed;
    String idRecoveredDoc;
    int documentRelevance;

    public Judgement(String s) {
        String params[] = s.split("\t");
        idInformationNeed = params[0];
        idRecoveredDoc = params[0] + params[1];
        documentRelevance = Integer.parseInt(params[2]);
    }

    public Judgement(String idInformationNeed, String idRecoveredDoc, int documentRelevance) {
        this.idInformationNeed = idInformationNeed;
        this.idRecoveredDoc = idRecoveredDoc;
        this.documentRelevance = documentRelevance;
    }

    public String getIdInformationNeed() {
        return idInformationNeed;
    }


    public String getIdRecoveredDoc() {
        return idRecoveredDoc;
    }

    public boolean isRelevant() {
        return this.documentRelevance == 1;
    }
}

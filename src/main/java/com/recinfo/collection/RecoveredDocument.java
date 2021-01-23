package com.recinfo.collection;

/**
 * Class which define a document recovered with an information need.
 * Remember that a document can be in different information need so the id is the combination of the document id and the
 * information need id.
 */
public class RecoveredDocument {
    private String documentId;
    private String infoNeedId;
    public RecoveredDocument(String s) {
        System.out.println(s);
        String params[] = s.split("\t");
        infoNeedId = params[0];
        documentId = params[0] + params[1];
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getInfoNeedId() {
        return infoNeedId;
    }

}

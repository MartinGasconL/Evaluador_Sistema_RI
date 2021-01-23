package com.recinfo.collection;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Class who provides an API to manage the access to a concrete collection of documents obtained in a information need search
 */
public class RecoveredDocuments {
    private LinkedHashMap<String, RecoveredDocument> recoveredDocuments;
    public RecoveredDocuments(String recoverSystemResultPath) {
        recoveredDocuments = loadRecoveredDocuments(recoverSystemResultPath);

    }

    /**
     * Loads the document dataset from a file
     * @param path Path of the file which contains de documents.
     * @return A map with the documentid and his information.
     */
    private LinkedHashMap<String,RecoveredDocument> loadRecoveredDocuments(String path) {
        LinkedHashMap<String,RecoveredDocument> retval = new LinkedHashMap<>();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            RecoveredDocument j;
            String line;
            while((line = in.readLine()) != null){
                j = new RecoveredDocument(line);
                retval.put(j.getDocumentId(), j);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retval;
    }

    /**
     * Get the information needs in the dataset
     * @return set with the name of the information needs in the dataset.
     */
    public Set<String> getInfoNeeds() {
        Set<String> retval = new HashSet<>();
        for(Map.Entry me : recoveredDocuments.entrySet()){
            RecoveredDocument rd = (RecoveredDocument) me.getValue();
            retval.add(rd.getInfoNeedId());
        }
        return retval;
    }

    /**
     * Get the documents recovered in the information need query.
     * @param infoNeed information need of the documents you want to obtain.
     * @return the document recovered in the information need passed by parameter.
     */
    public Map<String,RecoveredDocument> getDocumentsByInfoNeed(String infoNeed) {
        Map<String,RecoveredDocument> retval = new LinkedHashMap<>();
        for(Map.Entry<String, RecoveredDocument> me : recoveredDocuments.entrySet()){
            RecoveredDocument rd = me.getValue();
            if(rd.getInfoNeedId().equals(infoNeed)) {
                retval.put(rd.getDocumentId(),rd);
            }
        }
        return retval;
    }
    
}

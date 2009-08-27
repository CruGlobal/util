package org.ccci.model;


public enum Gender {

    M("he", "his"),
    F("she", "her"),
    U("he/she", "his/her");
    
    private final String pronoun;
    private final String possessivePronoun;
    
    Gender(String pronoun, String possessivePronoun)
    {
        this.pronoun = pronoun;
        this.possessivePronoun = possessivePronoun;
    }

    public String getPronoun()
    {
        return pronoun;
    }
    
    public String getPossessivePronoun()
    {
        return possessivePronoun;
    }
    
}

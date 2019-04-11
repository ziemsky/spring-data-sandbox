package com.ziemsky.springdata.jpa.entities;

public class ClassBPrime extends AbstractClassB {

    private String textPropB;

    public ClassBPrime() {
    }

    public ClassBPrime(final String textPropB, final ClassC classC) {
        super(classC);
        this.textPropB = textPropB;
    }

    public void setTextPropB(final String textPropB) {
        this.textPropB = textPropB;
    }

    @Override public String getTextPropB() {
        return textPropB;
    }

    @Override public String toString() {
        return "ClassBPrime{" +
            "textPropB='" + textPropB + '\'' +
            ", classC=" + classC +
            '}';
    }
}

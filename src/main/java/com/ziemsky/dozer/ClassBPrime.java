package com.ziemsky.dozer;

public class ClassBPrime {


    private String textPropBPrime;

    private ClassC classC;

    public ClassBPrime() {
    }

    public ClassBPrime(final String textPropBPrime, final ClassC classC) {
        this.textPropBPrime = textPropBPrime;
        this.classC = classC;
    }

    public void setTextPropBPrime(final String textPropBPrime) {
        this.textPropBPrime = textPropBPrime;
    }

    public void setClassC(final ClassC classC) {
        this.classC = classC;
    }

    public String getTextPropBPrime() {
        return textPropBPrime;
    }

    public ClassC getClassC() {
        return classC;
    }

    @Override public String toString() {
        return "ClassBPrime{" +
            "textPropBPrime='" + textPropBPrime + '\'' +
            ", classC=" + classC +
            '}';
    }
}

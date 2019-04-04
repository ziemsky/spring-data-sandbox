package com.ziemsky.dozer;

public class ClassBPrime extends AbstractClassB {


    private String textPropBPrime;

    public ClassBPrime() {
    }

    public ClassBPrime(final String textPropBPrime, final ClassC classC) {
        super(classC);
        this.textPropBPrime = textPropBPrime;
    }

    public void setTextPropBPrime(final String textPropBPrime) {
        this.textPropBPrime = textPropBPrime;
    }

    public String getTextPropBPrime() {
        return textPropBPrime;
    }

    @Override public String toString() {
        return "ClassBPrime{" +
            "textPropBPrime='" + textPropBPrime + '\'' +
            ", classC=" + classC +
            '}';
    }
}

package com.ziemsky.dozer;

public class ClassBPrime {


    private String textPropBPrime;

    private ClassC classC;

    private ClassBPrime() {
    }

    private ClassBPrime(final String textPropBPrime, final ClassC classC) {
        this.textPropBPrime = textPropBPrime;
        this.classC = classC;
    }

    @Override public String toString() {
        return "ClassBPrime{" +
            "textPropBPrime='" + textPropBPrime + '\'' +
            ", classC=" + classC +
            '}';
    }
}

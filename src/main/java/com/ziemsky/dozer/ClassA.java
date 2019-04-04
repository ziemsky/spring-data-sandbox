package com.ziemsky.dozer;

public class ClassA {

    private String textPropA;

    private ClassB classB;

    private ClassA() {
    }

    public ClassA(final String textPropA, final ClassB classB) {
        this.textPropA = textPropA;
        this.classB = classB;
    }

    @Override public String toString() {
        return "ClassA{" +
            "textPropA='" + textPropA + '\'' +
            ", classB=" + classB +
            '}';
    }
}

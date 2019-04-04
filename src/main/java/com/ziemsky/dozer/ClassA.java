package com.ziemsky.dozer;

public class ClassA {

    private String textPropA;

    private AbstractClassB classB;

    public ClassA() {
    }

    public ClassA(final String textPropA, final ClassB classB) {
        this.textPropA = textPropA;
        this.classB = classB;
    }

    public void setTextPropA(final String textPropA) {
        this.textPropA = textPropA;
    }

    public void setClassB(final AbstractClassB classB) {
        this.classB = classB;
    }

    public String getTextPropA() {
        return textPropA;
    }

    public AbstractClassB getClassB() {
        return classB;
    }

    @Override public String toString() {
        return "ClassA{" +
            "textPropA='" + textPropA + '\'' +
            ", classB=" + classB +
            '}';
    }
}

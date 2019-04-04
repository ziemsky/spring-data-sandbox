package com.ziemsky.dozer;

public abstract class AbstractClassB {

    protected ClassC classC;

    public AbstractClassB() {
    }

    public AbstractClassB(final ClassC classC) {
        this.classC = classC;
    }

    public ClassC getClassC() {
        return classC;
    }

    public void setClassC(final ClassC classC) {
        this.classC = classC;
    }

    public abstract String getTextPropB();


    @Override public String toString() {
        return "AbstractClassB{" +
            "classC=" + classC +
            '}';
    }
}

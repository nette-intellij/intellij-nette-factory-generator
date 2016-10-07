package cz.jiripudil.intellij.nette.factoryGenerator.ui;

import com.jetbrains.php.lang.psi.elements.Parameter;

import java.io.Serializable;

class ConstructorParameter implements Serializable {
    private Parameter parameter;

    ConstructorParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    Parameter getParameter() {
        return this.parameter;
    }

    public String toString() {
        return this.parameter.getType().toString() + " $" + this.parameter.getName();
    }
}

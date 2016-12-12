package cz.jiripudil.intellij.nette.factoryGenerator.codeGeneration;

import com.intellij.codeStyle.CodeStyleFacade;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.config.PhpLanguageFeature;
import com.jetbrains.php.config.PhpLanguageLevel;
import com.jetbrains.php.config.PhpProjectConfigurationFacade;
import com.jetbrains.php.lang.PhpFileType;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.refactoring.PhpFileCreator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FactoryInterfaceGenerator implements ApplicationComponent {
    public PsiFile generateFactory(Project project, PsiFile psiFile, PhpClass originalClass, String factoryName, ArrayList<Parameter> parameters) {
        String fileName = factoryName + ".php";
        if (psiFile.getContainingDirectory().findFile(fileName) != null) {
            return null;
        }

        PhpLanguageLevel languageLevel = PhpProjectConfigurationFacade.getInstance(project).getLanguageLevel();
        String namespace = originalClass.getNamespaceName();
        String trimmedNamespace = StringUtil.trimStart(StringUtil.trimEnd(namespace, "\\"), "\\");
        StringBuilder contentBuilder = new StringBuilder();

        CodeStyleFacade cs = CodeStyleFacade.getInstance(project);
        boolean useTab = cs.useTabCharacter(PhpFileType.INSTANCE);
        int indentSize = cs.getIndentSize(PhpFileType.INSTANCE);
        String indent = useTab ? "\t" : StringUtil.repeat(" ", indentSize);
        String newline = cs.getLineSeparator();

        contentBuilder.append(StringUtil.repeat(newline, 2))
            .append("namespace ")
            .append(trimmedNamespace)
            .append(";")
            .append(StringUtil.repeat(newline, 3));

        contentBuilder.append("interface ")
            .append(factoryName)
            .append(newline)
            .append("{")
            .append(StringUtil.repeat(newline, 2))
            .append(indent);

        if ( ! languageLevel.hasFeature(PhpLanguageFeature.RETURN_TYPES)) {
            contentBuilder.append("/**")
                .append(newline)
                .append(indent)
                .append(" * @return ")
                .append(originalClass.getName())
                .append(newline)
                .append(indent)
                .append(" */")
                .append(newline)
                .append(indent);
        }

        contentBuilder.append("public function create(");

        // factory parameters
        for (int index = 0; index < parameters.size(); index++) {
            Parameter parameter = parameters.get(index);

            // type hint
            if ( ! StringUtil.isEmpty(parameter.getType().toString())) {
                contentBuilder.append(parameter.getType().toStringRelativized(namespace))
                    .append(" ");
            }

            // name
            contentBuilder.append("$")
                .append(parameter.getName());

            // default value
            if (parameter.getDefaultValue() != null) {
                contentBuilder.append(" = ")
                    .append(parameter.getDefaultValue().getText());
            }

            if (index != parameters.size() - 1) {
                contentBuilder.append(", ");
            }
        }
        contentBuilder.append(")");

        if (languageLevel.hasFeature(PhpLanguageFeature.RETURN_TYPES)) {
            contentBuilder.append(": ")
                .append(originalClass.getName());
        }

        contentBuilder.append(";")
            .append(StringUtil.repeat(newline, 2))
            .append("}")
            .append(newline);

        return PhpFileCreator.createPhpFile(
            project,
            psiFile.getContainingDirectory(),
            fileName,
            contentBuilder.toString()
        );
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "nette-factory-generator.generator";
    }
}

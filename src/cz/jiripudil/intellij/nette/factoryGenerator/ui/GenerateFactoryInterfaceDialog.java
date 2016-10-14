package cz.jiripudil.intellij.nette.factoryGenerator.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.ui.CollectionComboBoxModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.Parameter;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.factoryGenerator.codeGeneration.FactoryInterfaceGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GenerateFactoryInterfaceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JBTextField interfaceName;
    private JBList factoryParams;

    @NotNull private Project project;
    @Nullable private PsiFile psiFile;
    @Nullable private Editor editor;
    @NotNull private PhpClass originalClass;

    private GenerateFactoryInterfaceDialog(@NotNull final Project project, @Nullable PsiFile psiFile, @Nullable Editor editor, @NotNull PhpClass originalClass) {
        this.project = project;
        this.psiFile = psiFile;
        this.editor = editor;
        this.originalClass = originalClass;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> generateFactory());
        buttonCancel.addActionListener(e -> dispose());

        interfaceName.setText("I" + originalClass.getName() + "Factory");
        initFactoryParams();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        contentPane.registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static GenerateFactoryInterfaceDialog create(@NotNull Component component, @NotNull Project project, @NotNull PsiFile psiFile, @NotNull PhpClass phpClass, @Nullable Editor editor) {
        GenerateFactoryInterfaceDialog dialog = new GenerateFactoryInterfaceDialog(project, psiFile, editor, phpClass);
        dialog.setTitle("Nette factory interface generator");
        dialog.pack();

        dialog.setMinimumSize(new Dimension(550, 250));
        dialog.setLocationRelativeTo(component);
        dialog.setVisible(true);

        return dialog;
    }

    private void createUIComponents() {
        interfaceName = new JBTextField();
        factoryParams = new JBList();
    }

    private void initFactoryParams() {
        Method ctor = originalClass.getConstructor();
        java.util.List<ConstructorParameter> list = new ArrayList<>();
        if (ctor != null) {
            for (Parameter parameter : ctor.getParameters()) {
                list.add(new ConstructorParameter(parameter));
            }
        }

        ListModel<ConstructorParameter> model = new CollectionComboBoxModel<>(list);
        factoryParams.setModel(model);
        factoryParams.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private void generateFactory() {
        String name = interfaceName.getText();
        ArrayList<Parameter> parameters = new ArrayList<>();

        ListModel model = factoryParams.getModel();
        for (int index : factoryParams.getSelectedIndices()) {
            ConstructorParameter parameter = (ConstructorParameter) model.getElementAt(index);
            parameters.add(parameter.getParameter());
        }

        FactoryInterfaceGenerator generator = ApplicationManager.getApplication().getComponent(FactoryInterfaceGenerator.class);
        generator.generateFactory(project, psiFile, originalClass, name, parameters);

        dispose();
    }
}

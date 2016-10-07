package cz.jiripudil.intellij.nette.factoryGenerator.intention;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import cz.jiripudil.intellij.nette.factoryGenerator.ui.GenerateFactoryInterfaceDialog;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class GenerateFactoryInterfaceIntention extends PsiElementBaseIntentionAction {
    @Nls
    @NotNull
    @Override
    public String getText() {
        return "Generate service factory interface";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return getText();
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) throws IncorrectOperationException {
        PhpClass phpClass = PhpPsiUtil.getParentByCondition(psiElement, PhpClass.INSTANCEOF);

        if (phpClass == null || editor == null) {
            return;
        }

        GenerateFactoryInterfaceDialog.create(editor.getComponent(), project, psiElement.getContainingFile(), phpClass, editor);
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement psiElement) {
        PhpClass phpClass = PhpPsiUtil.getParentByCondition(psiElement, PhpClass.INSTANCEOF);
        return phpClass != null && !phpClass.isInternal() && !phpClass.isInterface() && !phpClass.isTrait() && !phpClass.isAbstract() && !phpClass.isAnonymous();
    }
}

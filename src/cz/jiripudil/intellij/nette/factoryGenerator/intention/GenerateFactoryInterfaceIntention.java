package cz.jiripudil.intellij.nette.factoryGenerator.intention;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.php.PhpWorkaroundUtil;
import com.jetbrains.php.lang.lexer.PhpTokenTypes;
import com.jetbrains.php.lang.psi.PhpFile;
import com.jetbrains.php.lang.psi.PhpPsiUtil;
import com.jetbrains.php.lang.psi.elements.Function;
import com.jetbrains.php.lang.psi.elements.ParameterList;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.refactoring.PhpRefactoringUtil;
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
    public boolean startInWriteAction() {
        return false;
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
        PsiFile file = psiElement.getContainingFile();
        return file instanceof PhpFile
            && PhpWorkaroundUtil.isIntentionAvailable(psiElement)
            && (isInvokedOnConstructor(psiElement) || isInvokedOnClass(psiElement));

    }

    private boolean isInvokedOnConstructor(@NotNull PsiElement psiElement) {
        PsiElement parameters = PhpPsiUtil.getParentByCondition(psiElement, false, ParameterList.INSTANCEOF);
        if (parameters != null) {
            return PhpRefactoringUtil.isElementConstructor(parameters.getParent());

        } else {
            PsiElement parent = psiElement.getParent();
            return PhpRefactoringUtil.isElementConstructor(parent)
                && (
                    ((Function) parent).getNameIdentifier() == psiElement
                    || PhpPsiUtil.isOfType(psiElement, PhpTokenTypes.kwFUNCTION)
                    || PhpPsiUtil.isOfType(psiElement, PhpTokenTypes.chLPAREN)
                    || PhpPsiUtil.isOfType(psiElement, PhpTokenTypes.chRPAREN)
                );

        }
    }

    private boolean isInvokedOnClass(@NotNull PsiElement psiElement) {
        PhpClass phpClass = PhpPsiUtil.getParentByCondition(psiElement, PhpClass.INSTANCEOF);
        return phpClass != null
            && (
                phpClass.getNameIdentifier() == psiElement
                || PhpPsiUtil.isOfType(psiElement, PhpTokenTypes.kwCLASS)
            );

    }
}

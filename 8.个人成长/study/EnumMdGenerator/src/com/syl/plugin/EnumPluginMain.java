package com.syl.plugin;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.syl.ui.MarkDownLayer;


/**
 * 使用markdown表格语法生成枚举类
 *
 * @author syl
 * @create 2018-06-21 10:24
 **/
public class EnumPluginMain extends BaseGenerateAction {

    @SuppressWarnings("unused")
    public EnumPluginMain() {
        super(null);
    }
    @SuppressWarnings("unused")
    public EnumPluginMain(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    protected boolean isValidForClass(final PsiClass targetClass) {
        return super.isValidForClass(targetClass);
    }

    @Override
    public boolean isValidForFile(Project project, Editor editor, PsiFile file) {
        return super.isValidForFile(project, editor, file);
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);
        PsiFile mFile = PsiUtilBase.getPsiFileInEditor(editor, project);
        PsiClass psiClass = getTargetClass(editor, mFile);

        MarkDownLayer markDownLayer = new MarkDownLayer(psiClass, mFile, project);
        markDownLayer.setVisible(true);
    }

}

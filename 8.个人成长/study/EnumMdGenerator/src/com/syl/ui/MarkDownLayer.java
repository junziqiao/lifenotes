package com.syl.ui;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.javadoc.PsiDocComment;
import com.syl.bean.EnumField;
import com.syl.bean.Field;
import com.syl.util.PsiClassUtils;
import com.syl.workshop.CodeFoundry;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * markdown 输入
 *
 * @author syl
 * @create 2018-06-21 10:57
 **/
@SuppressWarnings("all")
public class MarkDownLayer extends JFrame {
    private PsiClass psiClass;
    private PsiFile psiFile;

    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel centerPanel;

    private Project project;
    private PsiElementFactory factory;

    private JPanel initPanel;
    private JLabel layerName;
    private JTextArea markdownTextBox;
    private JCheckBox lombokCheckBox;
    private JButton clearCheckBox;
    private JButton okCheckBox;
    private JCheckBox packtechCheckBox;
    private JButton demoButton;

    public MarkDownLayer(PsiClass psiClass, PsiFile psiFile, Project project) throws HeadlessException {
        this(psiClass, psiFile, project, 600, 400);
    }

    public MarkDownLayer(PsiClass psiClass, PsiFile psiFile, Project project, int width, int height) {
        this.psiClass = psiClass;
        this.psiFile = psiFile;
        this.project = project;
        this.factory = JavaPsiFacade.getElementFactory(project);
        setSize(width, height);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setContentPane(initPanel);
        setTitle("EnumGenerator");
        initControl();
    }

    private void initControl() {
        layerName.setText("请在excel里复制下内容贴到上面一个文本框里");
        clearCheckBox.addActionListener(e -> {
            markdownTextBox.setText("");
        });

        okCheckBox.addActionListener(e -> {
            String text = markdownTextBox.getText();
            text = text.trim();
            boolean lombok = lombokCheckBox.isSelected();
            boolean packaging = packtechCheckBox.isSelected();

            String[] split = text.split("\n");
            if(!isRightText(split)){
                return;
            }
            startGenerator(split,lombok,packaging);
            this.setVisible(false);
        });

        demoButton.addActionListener(e -> {
            String str = "enumName \t value \t titile\n" +
                    "NO \t 0\t  否\n" +
                    "YES \t 1\t 是";
            markdownTextBox.setText(str);
        });
    }

    private void startGenerator(String[] split, boolean lombok, boolean packaging) {
        CodeFoundry codeFoundry = new CodeFoundry(split, PsiClassUtils.getClassName(psiClass), lombok, packaging);
        codeFoundry.prepareGenerator();
        List<String> annotationList = codeFoundry.getAnnotationList();
        List<Field> fieldList = codeFoundry.getFieldList();
        List<EnumField> enumList = codeFoundry.getEnumList();
        String constructorMethod = codeFoundry.getConstructorMethod();
        List<String> otherMethod = codeFoundry.getOtherMethod();
        //子线程处理代码生成片段
        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (String annotation : annotationList) {
                PsiAnnotation pa = factory.createAnnotationFromText(annotation, psiClass);
                psiClass.addBefore(pa,psiClass);
            }

            for (EnumField enumField : enumList) {
                String enumFeildContent = enumField.getEnumName()+enumField.getEnumContent();
                PsiEnumConstant ec = factory.createEnumConstantFromText(enumFeildContent, psiClass);
                PsiComment enumComent = factory.createCommentFromText(enumField.getEnumComment(), psiClass);
                ec.addBefore(enumComent,ec.getFirstChild());
                CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
                styleManager.reformat(ec);
                psiClass.add(ec);
            }

            for (Field field : fieldList) {
                PsiField fieldFromTextA = factory.createFieldFromText("private "+field.getType()+" "+field.getName()+";", psiClass);
                String javadoc="/**"+field.getName()+"*/";
                PsiComment fieldComment = factory.createCommentFromText(javadoc, null);
                PsiDocComment doc = fieldFromTextA.getDocComment();
                fieldFromTextA.addBefore(fieldComment, fieldFromTextA.getFirstChild());
                CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
                styleManager.reformat(fieldFromTextA);
                psiClass.add(fieldFromTextA);
            }
            PsiMethod constructor = factory.createMethodFromText(constructorMethod, psiClass);
            PsiComment constructorComent = factory.createCommentFromText(codeFoundry.getConstructComment(), psiClass);
            constructor.addAfter(constructorComent,constructor.getDocComment());
            psiClass.add(constructor);

            for (String other : otherMethod) {
                PsiMethod psiMethod = factory.createMethodFromText(other, psiClass);
                psiClass.add(psiMethod);
            }
            CodeStyleManager styleManager = CodeStyleManager.getInstance(project);
            styleManager.reformat(psiClass,true);
        });
    }

    private boolean isRightText(String[] split){
        if(split.length < 2){
            Toast.make(project,MessageType.ERROR,"markdown table At least 3 lines");
            return false;
        }
        Set<Integer> set = new HashSet<>();
        for (String sp : split) {
            String[] sp2 = sp.split("\\|");
            set.add(sp2.length);
        }
        if(set.size() > 1){
            Toast.make(project,MessageType.ERROR,"markdown table formal error");
            return false;
        }
        return true;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
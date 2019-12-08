package com.syl.util;

import com.intellij.psi.PsiClass;

/**
 * @author syl
 * @create 2018-06-22 15:58
 **/
public class PsiClassUtils {

    /**
     * 获取 class name
     * @param psiClass
     * @return
     */
    public static String getClassName(PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        int i = qualifiedName.lastIndexOf(".");
        if(i < 0)return qualifiedName;
        return qualifiedName.substring(i+1, qualifiedName.length());
    }

    /**
     * 获取 包
     * @param psiClass
     * @return
     */
    public static String getPackage(PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null || qualifiedName.isEmpty()) {
            return null;
        }
        int i = qualifiedName.lastIndexOf(".");
        if(i < 0)return null;
        return qualifiedName.substring(0, i);
    }

}

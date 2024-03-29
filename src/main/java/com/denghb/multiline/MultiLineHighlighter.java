package com.denghb.multiline;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Auther: denghb
 * @Date: 2019-06-23 17:21
 */
public class MultiLineHighlighter implements Annotator {

    private static TextAttributes DEFAULT_TEXT_ATTR = new TextAttributes(new Color(32, 61, 191), null, null, null, Font.TRUETYPE_FONT);
    private static TextAttributes KEYWORD_TEXT_ATTR = new TextAttributes(new Color(0, 128, 0), null, null, null, Font.TRUETYPE_FONT);
    private static TextAttributes FUNCTION_TEXT_ATTR = new TextAttributes(new Color(255, 128, 0), null, null, null, Font.TRUETYPE_FONT);

    private static Set<String> KEYWORDS = new HashSet<String>();

    private static Set<String> FUNCTIONS = new HashSet<String>();

    static {
        // TODO 可能列得不全
        KEYWORDS.addAll(Arrays.asList("select ", "update ", "create ", "delete ", "truncate ", "insert "));
        KEYWORDS.addAll(Arrays.asList(" from ", " on ", " by ", " where ", " left ", " join ", " right ", " as ", " group ", " order ", " having ", "distinct "));
        KEYWORDS.addAll(Arrays.asList(" and ", " or ", " > ", " < ", " <= ", " >= ", " not ", " desc", " asc", " between", " union ", " is ", " null"));
        KEYWORDS.addAll(Arrays.asList("limit ", " like ", " case ", " when ", " else ", " end"));

        FUNCTIONS.addAll(Arrays.asList("count(", "sum(", "avg(", "min(", "max(", "avg(", "concat(", "date_format(", "date_sub(", "date_add(", "now("));
    }

    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {

        String code = psiElement.getText();
        if (code.startsWith("/*{")) {

            int originStart = psiElement.getNode().getTextRange().getStartOffset();

            // /*{}*/
            annotationHolder.createInfoAnnotation(new TextRange(originStart + 3, originStart + 3 + code.length() - 6), null).setEnforcedTextAttributes(DEFAULT_TEXT_ATTR);

            for (String keyword : KEYWORDS) {
                doColor(originStart, code, keyword, 0, annotationHolder, KEYWORD_TEXT_ATTR);
            }

            for (String function : FUNCTIONS) {
                doColor(originStart, code, function, -1, annotationHolder, FUNCTION_TEXT_ATTR);
            }

        }

//        Notifications.Bus.notify(new Notification("", "", code, NotificationType.INFORMATION));
    }

    private void doColor(int originStart, String code, String key, int offset, AnnotationHolder annotationHolder, TextAttributes textAttributes) {
        int start = code.indexOf(key);
        if (-1 == start) {
            return;
        }
        int newStart = originStart + start;
        int newEnd = newStart + key.length() + offset;
        TextRange textRange = new TextRange(newStart, newEnd);

        annotationHolder.createInfoAnnotation(textRange, null).setEnforcedTextAttributes(textAttributes);

        doColor(newStart + key.length(), code.substring(start + key.length()), key, offset, annotationHolder, textAttributes);
    }
}

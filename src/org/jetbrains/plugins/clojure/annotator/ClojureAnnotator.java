package org.jetbrains.plugins.clojure.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.clojure.highlighter.ClojureSyntaxHighlighter;
import org.jetbrains.plugins.clojure.psi.api.ClList;
import org.jetbrains.plugins.clojure.psi.api.symbols.ClSymbol;
import org.jetbrains.plugins.clojure.repl.ClojureConsoleRunner;

import java.util.Set;
import java.util.Arrays;

/**
 * @author ilyas
 */
public class ClojureAnnotator implements Annotator {
  public static final Set<String> IMPLICIT_NAMES = new HashSet<String>();

  static {
    IMPLICIT_NAMES.addAll(Arrays.asList("def", "new", "try", "throw", "catch", "finally", "ns", "in-ns", "if", "do",
        "recur", "quote", "var", "set!"));
  }

  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (element instanceof ClList) {
      annotateList((ClList) element, holder);
    }
    if (element instanceof ClSymbol) {
      ClSymbol symbol = (ClSymbol) element;
      if (symbol.isQualified()) {
        checkNonQualifiedSymbol(symbol, holder);
      }
    }
  }

  private void checkNonQualifiedSymbol(ClSymbol symbol, AnnotationHolder holder) {
    // todo add import fixo
  }

  private void annotateList(ClList list, AnnotationHolder holder) {
    final ClSymbol first = list.getFirstSymbol();
    if (first != null && (first.multiResolve(false).length > 0 ||
            IMPLICIT_NAMES.contains(list.getHeadText()))) {
      Annotation annotation = holder.createInfoAnnotation(first, null);
      annotation.setTextAttributes(ClojureSyntaxHighlighter.DEF);
    }
  }
}

/*
 *
 *  Copyright 2018-2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package springfox.javadoc.doclet;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import jdk.javadoc.doclet.DocletEnvironment;
import org.apache.commons.text.StringEscapeUtils;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class DocletHelper {

    static CharSequence asQualifiedName(AnnotationMirror annotationMirror) {
        return ((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName();
    }

    static Optional<? extends AnnotationMirror> getAnnotationOnElement(DocletEnvironment docletEnvironment,
                                                                       Element element, String annotationClassName) {
        return docletEnvironment.getElementUtils().getAllAnnotationMirrors(element)
          .stream()
          .filter(annotationMirror -> annotationClassName.contentEquals(asQualifiedName(annotationMirror)))
          .findFirst();
    }

    /**
     * Return the value of an annotation parameter retrieved from the annotation passed as parameter.
     * Example: @RequestMapping(method = POST) with paramName = POST would request POST
     *
     * @param annotationMirror the annotation mirror
     * @param paramName        param name
     * @return the annotationValue or empty if none matches the name
     */
    static Optional<AnnotationValue> getAnnotationParam(AnnotationMirror annotationMirror, String paramName) {
        Set<? extends Map.Entry<? extends ExecutableElement, ? extends AnnotationValue>> entries =
          annotationMirror.getElementValues().entrySet();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : entries) {
            Name annotationAttributeName = entry.getKey().getSimpleName();
            if (paramName.contentEquals(annotationAttributeName)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    static Optional<String> getFullBody(DocletEnvironment environment, Element element) {
        return getDocCommentTree(environment, element)
                .map(DocletHelper::getFullBody);
    }

    static String getFirstSentence(DocCommentTree docCommentTree) {
        return docTreesToStr(docCommentTree.getFirstSentence());
    }

    static String getFullBody(DocCommentTree docCommentTree) {
        return docTreesToStr(docCommentTree.getFullBody());
    }

    static Optional<DocCommentTree> getDocCommentTree(DocletEnvironment environment, Element element) {
        DocCommentTree docCommentTree = environment.getDocTrees().getDocCommentTree(element);

        return Optional.ofNullable(docCommentTree);
    }

    static String docTreesToStr(List<? extends DocTree> trees) {
        return trees.stream()
                .map(DocTree::toString)
                // toString in DCTree uses DocPretty, which uses Convert.escapeUnicode
                // here we need to unescape escaped string to store (and then display)
                // Unicode characters (e.g. cyrillic) properly
                .map(StringEscapeUtils::unescapeJava)
                .collect(Collectors.joining());
    }

}

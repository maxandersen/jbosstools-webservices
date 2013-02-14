/******************************************************************************* 
 * Copyright (c) 2008 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Xavier Coulon - Initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.ws.jaxrs.core.internal.metamodel.domain;

import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_APPLICATION_PATH_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_CONSUMES_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_DEFAULT_VALUE_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_ELEMENT_KIND;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_HTTP_METHOD_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_MATRIX_PARAM_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_NONE;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_PATH_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_PATH_PARAM_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_PRODUCES_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_PROVIDER_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_QUERY_PARAM_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_RETENTION_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.internal.metamodel.builder.JaxrsElementDelta.F_TARGET_ANNOTATION;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.APPLICATION_PATH;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.CONSUMES;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.DEFAULT_VALUE;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.HTTP_METHOD;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.MATRIX_PARAM;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.PATH;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.PATH_PARAM;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.PRODUCES;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.PROVIDER;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.QUERY_PARAM;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.RETENTION;
import static org.jboss.tools.ws.jaxrs.core.jdt.EnumJaxrsClassname.TARGET;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.ws.jaxrs.core.internal.utils.CollectionUtils;
import org.jboss.tools.ws.jaxrs.core.jdt.Annotation;
import org.jboss.tools.ws.jaxrs.core.metamodel.EnumElementKind;
import org.jboss.tools.ws.jaxrs.core.metamodel.IJaxrsHttpMethod;

/**
 * Base class for all elements in the JAX-RS Metamodel.
 * 
 * @author xcoulon
 * 
 * @param <T>
 *            the underlying Java type managed by the JAX-RS ElementKind.
 */
public abstract class JaxrsJavaElement<T extends IMember> extends JaxrsBaseElement {

	/** The underlying java element. */
	private final T javaElement;

	/**
	 * Map of Annotations on the associated Java Element, indexed by the
	 * annotation class name.
	 */
	private final Map<String, Annotation> annotations = new HashMap<String, Annotation>();

	/**
	 * Full constructor for element with multiple annotations.
	 * 
	 * @param element
	 *            the java element
	 * @param annotations
	 *            the java element annotations
	 * @param metamodel
	 *            the associated metamodel
	 */
	public JaxrsJavaElement(final T element, final Map<String, Annotation> annotations, final JaxrsMetamodel metamodel) {
		super(metamodel);
		this.javaElement = element;
		if (annotations != null) {
			this.annotations.putAll(annotations);
		}
	}

	static Map<String, Annotation> singleToMap(final Annotation annotation) {
		if (annotation != null) {
			return CollectionUtils.toMap(annotation.getFullyQualifiedName(), annotation);
		}
		return Collections.emptyMap();
	}

	@Override
	public boolean isBinary() {
		if (this.javaElement == null) {
			return true;
		}
		return this.javaElement.isBinary();
	}

	/**
	 * @param className the fully qualified name of the annotation to retrieve.
	 * @return the annotation matching the given java fully qualified name, null otherwise. 
	 */
	public Annotation getAnnotation(final String className) {
		return annotations.get(className);
	}

	/**
	 * @param className the fully qualified name of the annotation to check.
	 * @return true if the given element has the annotation matching the given java fully qualified name, false otherwise. 
	 */
	public boolean hasAnnotation(final String className) {
		return annotations.get(className) != null;
	}

	/** @return the underlying java element */
	public final T getJavaElement() {
		return javaElement;
	}

	@Override
	public String getName() {
		return javaElement != null ? javaElement.getElementName() : "*unknown java element*";
	}

	public Map<String, Annotation> getAnnotations() {
		return annotations;
	}

	public int addOrUpdateAnnotation(final Annotation annotation) {
		if (annotation == null) {
			return F_NONE;
		}
		boolean changed = false;
		final EnumElementKind previousKind = getElementKind();
		final String annotationName = annotation.getFullyQualifiedName();
		if (annotations.containsKey(annotation.getFullyQualifiedName())) {
			changed = annotations.get(annotation.getFullyQualifiedName()).update(annotation);
		} else {
			annotations.put(annotation.getFullyQualifiedName(), annotation);
			changed = true;
		}
		if (changed) {
			getMetamodel().indexElement(this, annotation);
			return qualifyChange(annotationName, previousKind);
		}
		return F_NONE;
	}

	public int updateAnnotations(Map<String, Annotation> otherAnnotations) {
		int flags = 0;
		// added annotations (ie: found in 'otherAnnotation' but not
		// this.annotations)
		final Map<String, Annotation> addedAnnotations = CollectionUtils.difference(otherAnnotations, this.annotations);
		// removed annotations (ie: found in this.annotations but not in
		// 'otherAnnotation')
		final Map<String, Annotation> removedAnnotations = CollectionUtils.difference(this.annotations,
				otherAnnotations);
		// may-be-changed annotations (ie: available in both collections, but
		// not sure all values are equal)
		final Map<String, Annotation> changedAnnotations = CollectionUtils.intersection(otherAnnotations,
				this.annotations);
		for (Entry<String, Annotation> entry : addedAnnotations.entrySet()) {
			flags += this.addOrUpdateAnnotation(entry.getValue());
		}
		for (Entry<String, Annotation> entry : changedAnnotations.entrySet()) {
			flags += this.addOrUpdateAnnotation(entry.getValue());
		}
		for (Entry<String, Annotation> entry : removedAnnotations.entrySet()) {
			flags += this.removeAnnotation(entry.getValue());
		}
		return flags;
	}

	private int qualifyChange(final String annotationName, EnumElementKind previousKind) {
		int flag = F_NONE;
		final EnumElementKind currentKind = getElementKind();
		if (annotationName.equals(PATH.qualifiedName)) {
			flag = F_PATH_ANNOTATION;
		} else if (annotationName.equals(APPLICATION_PATH.qualifiedName)) {
			flag = F_APPLICATION_PATH_ANNOTATION;
		} else if (annotationName.equals(HTTP_METHOD.qualifiedName)) {
			flag = F_HTTP_METHOD_ANNOTATION;
		} else if (annotationName.equals(TARGET.qualifiedName)) {
			flag = F_TARGET_ANNOTATION;
		} else if (annotationName.equals(RETENTION.qualifiedName)) {
			flag = F_RETENTION_ANNOTATION;
		} else if (annotationName.equals(PROVIDER.qualifiedName)) {
			flag = F_PROVIDER_ANNOTATION;
		} else if (annotationName.equals(PATH_PARAM.qualifiedName)) {
			flag = F_PATH_PARAM_ANNOTATION;
		} else if (annotationName.equals(QUERY_PARAM.qualifiedName)) {
			flag = F_QUERY_PARAM_ANNOTATION;
		} else if (annotationName.equals(MATRIX_PARAM.qualifiedName)) {
			flag = F_MATRIX_PARAM_ANNOTATION;
		} else if (annotationName.equals(DEFAULT_VALUE.qualifiedName)) {
			flag = F_DEFAULT_VALUE_ANNOTATION;
		} else if (annotationName.equals(CONSUMES.qualifiedName)) {
			flag = F_CONSUMES_ANNOTATION;
		} else if (annotationName.equals(PRODUCES.qualifiedName)) {
			flag = F_PRODUCES_ANNOTATION;
		} else {
			for (IJaxrsHttpMethod httpMethod : metamodel.getAllHttpMethods()) {
				if (httpMethod.getJavaClassName().equals(annotationName)) {
					flag = F_HTTP_METHOD_ANNOTATION;
					break;
				}
			}
		}

		if (currentKind != previousKind) {
			flag += F_ELEMENT_KIND;
		}
		return flag;
	}

	public int removeAnnotation(Annotation annotation) {
		return removeAnnotation(annotation.getJavaAnnotation().getHandleIdentifier());
	}

	public int removeAnnotation(final String handleIdentifier) {
		int flag = F_NONE;
		for (Iterator<Entry<String, Annotation>> iterator = annotations.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Annotation> entry = iterator.next();
			Annotation annotation = entry.getValue();
			if (annotation.getJavaAnnotation().getHandleIdentifier().equals(handleIdentifier)) {
				this.metamodel.unindexElement(this, handleIdentifier);
				final EnumElementKind previousKind = getElementKind();
				final String annotationName = entry.getKey();
				// this removes the annotation, which can cause a change of the
				// element type as well.
				iterator.remove();
				flag = qualifyChange(annotationName, previousKind);
				break;
			}
		}
		return flag;
	}

	public IResource getResource() {
		return this.javaElement.getResource();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((javaElement == null) ? 0 : javaElement.getHandleIdentifier().hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JaxrsJavaElement<?> other = (JaxrsJavaElement<?>) obj;
		if (javaElement == null) {
			if (other.javaElement != null) {
				return false;
			}
		} else if (!javaElement.getHandleIdentifier().equals(other.javaElement.getHandleIdentifier())) {
			return false;
		}
		return true;
	}

	public int update(T javaElement) {
		return 0;
	}

	/**
	 * Creates a validation message from the given parameters. The created
	 * validation messages is of the 'JAX-RS' type.
	 * 
	 * @param msg
	 *            the message to display
	 * @param severity
	 *            the severity of the marker
	 * @param region
	 *            the region that the validation marker points to
	 * @return the created validation message.
	 * @throws JavaModelException
	 *             ValidatorMessage createValidatorMessage(final String id,
	 *             final String msg, int severity, final ISourceRange
	 *             sourceRange) throws JavaModelException { final
	 *             ValidatorMessage validationMsg = ValidatorMessage.create(msg,
	 *             this.getResource());
	 *             validationMsg.setType(JaxrsMetamodelValidator
	 *             .JAXRS_PROBLEM_TYPE); final ICompilationUnit compilationUnit
	 *             = this.getJavaElement().getCompilationUnit(); final
	 *             CompilationUnit ast =
	 *             CompilationUnitsRepository.getInstance()
	 *             .getAST(compilationUnit);
	 *             validationMsg.setAttribute(IMarker.LOCATION,
	 *             NLS.bind(ValidationMessages.LINE_NUMBER,
	 *             ast.getLineNumber(sourceRange.getOffset())));
	 *             validationMsg.setAttribute(IMarker.MARKER,
	 *             JaxrsMetamodelValidator.JAXRS_PROBLEM_TYPE);
	 *             validationMsg.setAttribute(IMarker.SEVERITY, severity);
	 *             validationMsg.setAttribute(IMarker.CHAR_START,
	 *             sourceRange.getOffset());
	 *             validationMsg.setAttribute(IMarker.CHAR_END,
	 *             sourceRange.getOffset() + sourceRange.getLength());
	 *             Logger.debug("Validation message for {}: {}",
	 *             this.getJavaElement().getElementName(),
	 *             validationMsg.getAttribute(IMarker.MESSAGE)); return
	 *             validationMsg; }
	 */

}

//   Copyright 2011 Palantir Technologies
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
package com.palantir.ptoss.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.palantir.ptoss.cinch.core.ObjectFieldMethod;

/**
 * A collection of utility methods and classes to handle all the of the Java Reflection calls
 * need to wire and fire bindings.
 *
 * @see <a href='http://docs.oracle.com/javase/6/docs/api/index.html?java/lang/reflect/package-summary.html'>java.lang.reflect</a>
 */
public class Reflections {

    /**
     * {@link Function} that maps a {@link Field} to the simple name for the containing class.
     * @see Class#getSimpleName()
     */
    public static final Function<Field, String> FIELD_TO_CONTAINING_CLASS_NAME = new Function<Field, String>() {
        public String apply(Field input) {
            return input.getDeclaringClass().getSimpleName();
        }
    };

    /**
     * {@link Function} that maps a {@link Field} to its string name.
     * @see Field#getName()
     */
    public static final Function<Field, String> FIELD_TO_NAME = new Function<Field, String>() {
        public String apply(Field from) {
            return from.getName();
        }
    };

    /**
     * {@link Predicate} to determine whether or not the specified field is final.
     * @see Modifier#isFinal(int)
     */
    public static final Predicate<Field> IS_FIELD_FINAL = new Predicate<Field>() {
        public boolean apply(Field from) {
            return isFieldFinal(from);
        }
    };

    /**
     * Starting at the bottom of a class hierarchy, visit all classes (ancestors) in the hierarchy. Does
     * not visit interfaces.
     * @param klass Class to use as the bottom of the class hierarchy
     * @param visitor Visitor object
     */
    public static void visitClassHierarchy(Class<?> klass, Visitor<Class<?>> visitor) {
        while (klass != null) {
            visitor.visit(klass);
            klass = klass.getSuperclass();
        }
    }

    /**
     * Given an {@link Object} and a {@link Field} of a known {@link Class} type, get the field.
     * This will return the value of the field regardless of visibility modifiers (i.e., it will
     * return the value of private fields.)
     */
    public static <T> T getFieldObject(Object object, Field field, Class<T> klass) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            Object fieldObject = field.get(object);
            field.setAccessible(accessible);
            return klass.cast(fieldObject);
        } catch (IllegalAccessException e) {
            // shouldn't happen since we set accessibility above.
            return null;
        }
    }

    /**
     * Returns whether or not the given {@link Field} is final.
     */
    public static boolean isFieldFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers);
    }

    /**
     * Returns whether or not the given {@link Field} is static.
     */
    public static boolean isFieldStatic(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isStatic(modifiers);
    }

    /**
     * Returns whether or not the given {@link Method} is public.
     */
    public static boolean isMethodPublic(Method method) {
        int modifiers = method.getModifiers();
        return Modifier.isPublic(modifiers);
    }

    /**
     * Find a {@link Field} based on the field name.  Will return private fields but will not
     * look in superclasses.
     *
     * @return null if there is no field found
     */
    // TODO (dcervelli) fix for superclasses
    public static Field getFieldByName(Class<?> klass, String fieldName) {
        for (Field f : klass.getDeclaredFields()) {
            if (f.getName().equals(fieldName)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Gets all inner classes from a given class that are assignable from the target class.
     * @param klass type to query for inner-classes.
     * @param targetClass interface or class that inner classes must be assignable from to be
     * returned.
     * @return all inner classes in <code>klass</code> that are assignable from
     * <code>targetClass</code>
     * @see Class#isAssignableFrom(Class)
     * @see Class#getDeclaredClasses()
     */
    public static List<Class<?>> getTypesOfType(Class<?> klass, Class<?> targetClass) {
        List<Class<?>> classes = Lists.newArrayList();
        for (Class<?> cl : klass.getDeclaredClasses()) {
            if (targetClass.isAssignableFrom(cl)) {
                classes.add(cl);
            }
        }
        return classes;
    }

    /**
     * Gets all inner classes assignable from <code>targetClass</code> in the passed class's type
     * hierarchy.
     *
     * @param klass starting point in the type stack to query for inner classes.
     * @param targetClass looks for inner classes that are assignable from this type.
     * @return all inner classes in <code>klass</code>'s type hierarchy assignable from
     * <code>targetclass</code>
     * @see Class#isAssignableFrom(Class)
     * @see Class#getDeclaredClasses()
     * @see #getTypesOfType(Class, Class)
     */
    public static List<Class<?>> getTypesOfTypeForClassHierarchy(Class<?> klass, final Class<?> targetClass) {
        final List<Class<?>> classes = Lists.newArrayList();
        visitClassHierarchy(klass, new Visitor<Class<?>>() {
            public void visit(Class<?> c) {
                classes.addAll(getTypesOfType(c, targetClass));
            }
        });
        return classes;
    }

    /**
     * Gets all fields from a given class that are assignable from the target class.
     * @param klass type to query for fields.
     * @param targetClass interface or class that fields must be assignable from to be
     * returned.
     * @return all fields in <code>klass</code> that are assignable from
     * <code>targetClass</code>
     * @see Class#isAssignableFrom(Class)
     * @see Class#getDeclaredFields()
     */
    public static List<Field> getFieldsOfType(Class<?> klass, Class<?> targetClass) {
        List<Field> fields = Lists.newArrayList();
        for (Field f : klass.getDeclaredFields()) {
            if (targetClass.isAssignableFrom(f.getType())) {
                fields.add(f);
            }
        }
        return fields;
    }

    /**
     * Gets all fields assignable from <code>targetClass</code> in the passed class's type
     * hierarchy.
     *
     * @param klass starting point in the type stack to query for fields of the specified type.
     * @param targetClass looks for fields that are assignable from this type.
     * @return all fields declared by classes in <code>klass</code>'s type hierarchy assignable from
     * <code>targetclass</code>
     * @see Class#isAssignableFrom(Class)
     * @see Class#getDeclaredClasses()
     * @see #getTypesOfType(Class, Class)
     */
    public static List<Field> getFieldsOfTypeForClassHierarchy(Class<?> klass, final Class<?> targetClass) {
        final List<Field> fields = Lists.newArrayList();
        visitClassHierarchy(klass, new Visitor<Class<?>>() {
            public void visit(Class<?> c) {
                fields.addAll(getFieldsOfType(c, targetClass));
            }
        });
        return fields;
    }

    /**
     * Looks up an {@link Enum} value by its {@link String} name.
     * @param enumType {@link Enum} class to query.
     * @param value {@link String} name for the {@link Enum} value.
     * @return the actual {@link Enum} value specified by the passed name.
     * @see Enum#valueOf(Class, String)
     */
    public static Object evalEnum(Class<?> enumType, String value) {
        try {
            Method method = enumType.getMethod("valueOf", String.class);
            method.setAccessible(true);
            return method.invoke(null, value);
        } catch (Exception ew) {
            throw new IllegalArgumentException("could not find enum value: " + value);
        }
    }

    /**
     * Checks whether or not the specified {@link Annotation} exists in the passed {@link Object}'s
     * class hierarchy.
     * @param object object to check
     * @param annotation annotation to look for
     * @return true is a class in this passed object's type hierarchy is annotated with the
     * passed {@link Annotation}
     */
    public static boolean isClassAnnotatedForClassHierarchy(Object object, final Class<? extends Annotation> annotation) {
        final boolean[] bool = new boolean[1];
        visitClassHierarchy(object.getClass(), new Visitor<Class<?>>() {
            public void visit(Class<?> klass) {
                if (klass.isAnnotationPresent(annotation)) {
                    bool[0] = true;
                }
            }
        });
        return bool[0];
    }

    /**
     * Returns the list of fields on this class annotated with the passed {@link Annotation}
     * @param klass checks the {@link Field}s on this class
     * @param annotation looks for this {@link Annotation}
     * @return list of all {@link Field}s that are annotated with the specified {@link Annotation}
     */
    public static List<Field> getAnnotatedFields(Class<?> klass, Class<? extends Annotation> annotation) {
        List<Field> annotatedFields = Lists.newArrayList();
        for (Field f : klass.getDeclaredFields()) {
            if (f.isAnnotationPresent(annotation)) {
                annotatedFields.add(f);
            }
        }
        return annotatedFields;
    }

    /**
     * Returns the list of fields on this class or any of its ancestors annotated with the
     * passed {@link Annotation}.
     * @param klass checks the {@link Field}s on this class and its ancestors
     * @param annotation looks for this {@link Annotation}
     * @return list of all {@link Field}s that are annotated with the specified {@link Annotation}
     */
    public static List<Field> getAnnotatedFieldsForClassHierarchy(Class<?> klass,
            final Class<? extends Annotation> annotation) {
        final List<Field> annotatedFields = Lists.newArrayList();
        visitClassHierarchy(klass, new Visitor<Class<?>>() {
            public void visit(Class<?> c) {
                annotatedFields.addAll(getAnnotatedFields(c, annotation));
            }
        });
        return annotatedFields;
    }

    private static List<ObjectFieldMethod> getParameterlessMethods(Object tupleObject, Class<?> klass) {
        List<ObjectFieldMethod> methods = Lists.newArrayList();
        for (Method method : klass.getDeclaredMethods()) {
            if (method.getParameterTypes().length == 0) {
                methods.add(new ObjectFieldMethod(tupleObject, null, method));
            }
        }
        return methods;
    }

    /**
     * Returns all methods in the passed object's class hierarchy that do no not take parameters
     * @param object object to query for parameterless methods
     * @return a list {@link ObjectFieldMethod} tuples mapping the parameterless methods to
     * the passed object.
     */
    public static List<ObjectFieldMethod> getParameterlessMethodsForClassHierarchy(final Object object) {
        final List<ObjectFieldMethod> methods = Lists.newArrayList();
        visitClassHierarchy(object.getClass(), new Visitor<Class<?>>() {
            public void visit(Class<?> c) {
                methods.addAll(getParameterlessMethods(object, c));
            }
        });
        return methods;
    }

    /**
     * Returns a {@link Function} that will read values from the named field from a passed object.
     * @param klass type to read values from
     * @param returnType return type of read field
     * @param getter name of the field
     * @return a {@link Function} object that, when applied to an instance of <code>klass</code>, returns the
     * of type <code>returnType</code> that resides in field <code>getter</code>
     */
    public static <F, T> Function<F, T> getterFunction(final Class<F> klass, final Class<T> returnType, String getter) {
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(klass);
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            Method method = null;
            for (PropertyDescriptor descriptor : props) {
                if (descriptor.getName().equals(getter)) {
                    method = descriptor.getReadMethod();
                    break;
                }
            }
            if (method == null) {
                throw new IllegalStateException();
            }
            final Method readMethod = method;
            return new Function<F, T>() {
                public T apply(F from) {
                    try {
                        return returnType.cast(readMethod.invoke(from));
                    } catch (Exception e) {
                        throw Throwables.throwUncheckedException(e);
                    }
                }
            };
        } catch (IntrospectionException e) {
            throw Throwables.throwUncheckedException(e);
        }
    }
}

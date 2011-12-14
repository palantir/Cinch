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
package com.palantir.ptoss.cinch.core;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.Validate;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.palantir.ptoss.cinch.swing.Bound;
import com.palantir.ptoss.cinch.swing.OnChange;
import com.palantir.ptoss.util.Reflections;

/**
 * <p>A {@link BindingContext} holds information about how to bind various parts of a Java Object.
 *
 * <p><b>Binding Constants</b> - TODO
 *
 * <p><b>Visibility</b> - TODO
 *
 * <p><b>Subclassing</b> - TODO
 *
 * <p><b>Bindable models</b> - have to be final
 */
public class BindingContext {
    /**
     * The object for which the context has been built.
     */
    private final Object object;

    /**
     * All of the fields of type {@link BindableModel} on the object.
     * @see #indexBindableModels()
     */
    private final Map<String, Field> bindableModels;

    /**
     * All of the bindable methods on the object.
     * @see #indexBindableMethods()
     */
    private final Map<String, ObjectFieldMethod> bindableMethods;

    /**
     * @see #indexBindableModelMethods()
     */
    private final Map<String, ObjectFieldMethod> bindableModelMethods;

    /**
     * The map from all static final field names on the object to the objects contained in those
     * fields.
     * @see #indexBindableConstants()
     */
    private final Map<String, Object> bindableConstants;

    /**
     * All of the getters available on the bindable models.
     */
    private final Map<String, ObjectFieldMethod> bindableGetters;

    /**
     * All of the setters available on the bindable models.
     */
    private final Map<String, ObjectFieldMethod> bindableSetters;

    /**
     * Create a BindingContext for the given, non-null object.  Throws a {@link BindingException}
     * if there is a problem.
     * @param object the object - cannot be null
     */
    public BindingContext(Object object) {
        Validate.notNull(object);
        this.object = object;
        try {
            bindableModels = indexBindableModels();
            bindableMethods = indexBindableMethods();
            bindableModelMethods = indexBindableModelMethods();
            bindableConstants = indexBindableConstants();
            bindableGetters = indexBindableProperties(Reflections.getterFunction(PropertyDescriptor.class, Method.class, "readMethod"));
            bindableSetters = indexBindableProperties(Reflections.getterFunction(PropertyDescriptor.class, Method.class, "writeMethod"));
        } catch (Exception e) {
            throw new BindingException("could not create BindingContext", e);
        }
    }

    /**
     * Gets a constant from the binding context.  Constants are static, final fields of the bound
     * object.
     *
     * @param key the name of the field
     * @return the value of the field
     */
    public Object getBindableConstant(String key) {
        return bindableConstants.get(key);
    }

    /**
     * Look through all of the declared, static, final fields of the context object, grab the value,
     * and insert a mapping from the field's name to the object.
     *
     * Note that this will index non-public fields.
     *
     * @return the bindable constants map
     * @throws IllegalArgumentException on reflection error
     * @throws IllegalAccessException on reflection error
     */
    private Map<String, Object> indexBindableConstants() throws IllegalArgumentException, IllegalAccessException {
        Map<String, Object> map = Maps.newHashMap();
        for (Field field : object.getClass().getDeclaredFields()) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            if (Reflections.isFieldFinal(field) && Reflections.isFieldStatic(field)) {
                map.put(field.getName(), field.get(object));
            }
            field.setAccessible(accessible);
        }
        return map;
    }

    /**
     * Returns the value of the specified Field on the object bound by this {@link BindingContext}
     *
     * @param field {@link Field} to pull the value from
     * @param klass return type of value in the {@link Field}
     * @return value of type <code>klass</code> from field <code>field</code> on bound object.
     * @throws IllegalArgumentException if the passed {@link Field} is not a field on the object
     * bound by this {@link BindingContext}
     */
    public <T> T getFieldObject(Field field, Class<T> klass) throws IllegalArgumentException {
        return Reflections.getFieldObject(object, field, klass);
    }

    /**
     * Looks up an {@link ObjectFieldMethod} tuple by its key.
     * @param key - generated by {@link OnChange#call()}
     * @return the tuple for this key (or null, if it doesn't exist)
     */
    public ObjectFieldMethod getBindableMethod(String key) {
        ObjectFieldMethod ofm = bindableMethods.get(key);
        return ofm;
    }

    /**
     * Looks up an {@link ObjectFieldMethod} tuple by its key.
     * @param key - generated by {@link OnChange#call()}
     * @return the tuple for this key (or null, if it doesn't exist)
     */
    // TODO (regs) dead code?
    public ObjectFieldMethod getBindableModelMethod(String key) {
        ObjectFieldMethod ofm = bindableModelMethods.get(key);
        return ofm;
    }

    public BindableModel getBindableModel(String key) {
        Field field = bindableModels.get(key);
        if (field == null) {
            return null;
        }
        return getFieldObject(field, BindableModel.class);
    }

    public Object evalOnObject(String on, BindableModel model) {
        return findOnObject(on, model);
    }

    /**
     * Returns the list of {@link ModelUpdate} types in this binding context.
     * @param modelClass
     * @return the of {@link Class}es that implement {@link ModelUpdate} in this binding context.
     */
    public static List<Class<?>> findModelUpdateClass(final BindableModel modelClass) {
        List<Class<?>> classes = Reflections.getTypesOfTypeForClassHierarchy(
                modelClass.getClass(), ModelUpdate.class);
        Predicate<Class<?>> isEnum = new Predicate<Class<?>>() {
            public boolean apply(final Class<?> input) {
                return input.isEnum();
            }
        };
        // Look for ModelUpdate classes in implemented interfaces
        classes = Lists.newArrayList(Iterables.filter(classes, isEnum));
        for (Class<?> iface : modelClass.getClass().getInterfaces()) {
            classes.addAll(Lists.newArrayList(Iterables.filter(
                    Reflections.getTypesOfTypeForClassHierarchy(
                    iface, ModelUpdate.class), isEnum)));
        }
        if (classes.size() == 0) {
            return null;
        }
        return classes;
    }

    /**
     * Resolves a string reference, as specified in the <code>on</code> parameter of
     * a {@link Bound} annotation to an Enum object in this runtime.
     * @param on <code>on</code> parameter from a {@link Bound} annotation.
     * @param model
     * @return the resolved object
     * @throws IllegalArgumentException if the referenced object can't be found.
     */
    public static ModelUpdate findOnObject(final String on, final BindableModel model) {
        ModelUpdate onObject = null;
        if (on != null && on.trim().length() > 0) {
            final List<Class<?>> updateClasses = findModelUpdateClass(model);
            for (Class<?> updateClass : updateClasses) {
                try {
                    onObject = (ModelUpdate)Reflections.evalEnum(updateClass, on);
                    return onObject;
                } catch (IllegalArgumentException e) {
                    // swallow this if we don't find the enum on one of the
                    // classes, continue to next class.
                }
            }
            throw new IllegalArgumentException("could not find \"on\" parameter " + on);
        }
        return onObject;
    }

    private Map<String, ObjectFieldMethod> indexBindableProperties(Function<PropertyDescriptor, Method> methodFn) throws IntrospectionException {
        final Map<ObjectFieldMethod, String> getterOfms = Maps.newHashMap();
        for (Field field : Sets.newHashSet(bindableModels.values())) {
            BeanInfo beanInfo = Introspector.getBeanInfo(field.getType());
            PropertyDescriptor[] props = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : props) {
                Method method = methodFn.apply(descriptor);
                if (method == null) {
                    continue;
                }
                BindableModel model = getFieldObject(field, BindableModel.class);
                getterOfms.put(new ObjectFieldMethod(model, field, method), descriptor.getName());
            }
        }
        return dotIndex(getterOfms.keySet(), ObjectFieldMethod.TO_FIELD_NAME, Functions.forMap(getterOfms));
    }

    private static <T> Map<String, T> dotIndex(Collection<T> items, Function<T, String> qualifierFn, Function<T, String> blindFn) {
        Set<String> ambiguousNames = Sets.newHashSet();
        Map<String, T> results = Maps.newHashMap();
        for (T item : items) {
            String blindKey = blindFn.apply(item);
            if (!ambiguousNames.contains(blindKey)) {
                if (results.containsKey(blindKey)) {
                    results.remove(blindKey);
                    ambiguousNames.add(blindKey);
                } else {
                    results.put(blindKey, item);
                }
            }
            String qualifiedKey = qualifierFn.apply(item) + "." + blindKey;
            results.put(qualifiedKey, item);
        }
        return results;
    }

    public ObjectFieldMethod findGetter(String property) {
        return bindableGetters.get(property);
    }

    public ObjectFieldMethod findSetter(String property) {
        return bindableSetters.get(property);
    }

    public Set<BindableModel> getBindableModels() {
        Function<Field, BindableModel> f = new Function<Field, BindableModel>() {
            public BindableModel apply(Field from) {
                return getFieldObject(from, BindableModel.class);
            }
        };
        return ImmutableSet.copyOf(Iterables.transform(bindableModels.values(), f));
    }

    public List<Field> getAnnotatedFields(Class<? extends Annotation> klass) {
        return Reflections.getAnnotatedFieldsForClassHierarchy(object.getClass(), klass);
    }

    public List<ObjectFieldMethod> getAnnotatedParameterlessMethods(final Class<? extends Annotation> annotation) {
        return Lists.newArrayList(Iterables.filter(Reflections.getParameterlessMethodsForClassHierarchy(object),
                new Predicate<ObjectFieldMethod>() {
                    public boolean apply(ObjectFieldMethod input) {
                        return input.getMethod().isAnnotationPresent(annotation);
                    }
                }));
    }

    private static List<ObjectFieldMethod> getParameterlessMethods(Object object, Field field) {
        List<ObjectFieldMethod> methods = Lists.newArrayList();
        for (Method method : field.getType().getDeclaredMethods()) {
            if (method.getParameterTypes().length == 0 && Reflections.isMethodPublic(method)) {
                methods.add(new ObjectFieldMethod(object, field, method));
            }
        }
        return methods;
    }

    private static List<ObjectFieldMethod> getParameterlessMethodsOnFieldTypes(Object object, List<Field> fields) throws IllegalArgumentException {
        List<ObjectFieldMethod> methods = Lists.newArrayList();
        for (Field field : fields) {
            Object fieldObject = Reflections.getFieldObject(object, field, Object.class);
            methods.addAll(getParameterlessMethods(fieldObject, field));
        }
        return methods;
    }

    private static Map<String, ObjectFieldMethod> indexMethods(List<ObjectFieldMethod> methods) throws IllegalArgumentException {
        Map<String, ObjectFieldMethod> map = Maps.newHashMap();
        Set<String> ambiguousNames = Sets.newHashSet();
        for (ObjectFieldMethod ofm : methods) {
            Method method = ofm.getMethod();
            String blindKey = method.getName();
            if (!ambiguousNames.contains(blindKey)) {
                if (map.containsKey(blindKey)) {
                    map.remove(blindKey);
                    ambiguousNames.add(blindKey);
                } else {
                    map.put(blindKey, ofm);
                }
            }
            String fieldName = ofm.getField() == null ? "this" : ofm.getField().getName();
            String qualifiedKey = fieldName + "." + blindKey;
            map.put(qualifiedKey, ofm);
        }
        return map;
    }

    private List<Field> getBindableModelFields() {
        List<Field> allModelFields = Reflections.getFieldsOfTypeForClassHierarchy(object.getClass(), BindableModel.class);
        List<Field> notBindableFields = Reflections.getAnnotatedFieldsForClassHierarchy(object.getClass(), NotBindable.class);
        allModelFields = ImmutableList.copyOf(Iterables.filter(allModelFields, Predicates.not(Predicates.in(notBindableFields))));
        List<Field> nonFinalModelFields = ImmutableList.copyOf(Iterables.filter(allModelFields, Predicates.not(Reflections.IS_FIELD_FINAL)));
        if (!nonFinalModelFields.isEmpty()) {
            throw new BindingException("All BindableModels have to be final or marked with @NotBindable, but "+
                Iterables.transform(nonFinalModelFields, Reflections.FIELD_TO_NAME)+" are not.");
        }
        return allModelFields;
    }

    /**
     * Indexes all bindable models within the binding context. If there are two bindable models
     * in a class hierarchy with identical names then they are indexed as
     * "DeclaringClass.modelFieldName". If this is not unique then one of them will win
     * non-deterministically, don't do this.
     * @return the index
     */
    private Map<String, Field> indexBindableModels() {
        return dotIndex(getBindableModelFields(),
                Reflections.FIELD_TO_CONTAINING_CLASS_NAME,
                Reflections.FIELD_TO_NAME);
    }

    /*
     * TODO Current behavior is if ANY class in a class hierarchy is Bindable then all methods in that
     * hierarchy are bindable.  Really this should be for each class in the hierarchy, if it's
     * marked Bindable then its methods are bindable.
     */
    private Map<String, ObjectFieldMethod> indexBindableMethods() throws IllegalArgumentException {
        // Get all fields marked @Bindable
        List<Field> bindables = getAnnotatedFields(Bindable.class);
        if (Iterables.any(bindables, Predicates.not(Reflections.IS_FIELD_FINAL))) {
            throw new BindingException("all @Bindables have to be final");
        }
        // Add all BindableModels
        bindables.addAll(getBindableModelFields());

        // Index those methods.
        List<ObjectFieldMethod> methods = getParameterlessMethodsOnFieldTypes(object, bindables);

        // Add methods for classes marked @Bindable
        if (Reflections.isClassAnnotatedForClassHierarchy(object, Bindable.class)) {
            methods.addAll(Reflections.getParameterlessMethodsForClassHierarchy(object));
        }

        return indexMethods(methods);
    }

    private Map<String, ObjectFieldMethod> indexBindableModelMethods() throws IllegalArgumentException {
        List<ObjectFieldMethod> methods = getParameterlessMethodsOnFieldTypes(object, getBindableModelFields());
        return indexMethods(methods);
    }

    static <T> boolean isOn(Object onObject, Set<T> changedSet) {
        if (changedSet.contains(ModelUpdates.ALL)) {
            return true;
        }
        return changedSet.contains(onObject);
    }

    public static <T extends Enum<?> & ModelUpdate> boolean isOn(Object onObject, T... changed) {
        if (onObject == null) {
            return true;
        }
        final Set<T> changedSet = Sets.newHashSet(changed);
        return BindingContext.isOn(onObject, changedSet);
    }

    public static <T extends Enum<?> & ModelUpdate> boolean isOn(Collection<Object> ons, T... changed) {
        if (ons == null || ons.isEmpty()) {
            return true;
        }
        final Set<T> changedSet = Sets.newHashSet(changed);
        for (Object on : ons) {
            if (BindingContext.isOn(on, changedSet)) {
                return true;
            }
        }
        return false;
    }

    public static List<Object> getOnObjects(String[] ons, BindableModel model) {
        if (ons == null) {
            return null;
        }
        List<Object> onObjects = Lists.newArrayList();
        for (int i = 0; i < ons.length; i++) {
            Object onObject = findOnObject(ons[i], model);
            if (onObject != null) {
                onObjects.add(onObject);
            }
        }
        return onObjects;
    }
}

/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.context;

import org.instancio.TargetSelector;
import org.instancio.internal.nodes.ArrayNode;
import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.nodes.NodeContext;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

class SelectorMapTest {
    private final NodeFactory nodeFactory = new NodeFactory(
            new NodeContext(Collections.emptyMap(), new SubtypeSelectorMap(Collections.emptyMap())));

    private final Node rootNode = nodeFactory.createRootNode(Person.class, null);
    private final Node personNameNode = getNodeWithField(rootNode, Person.class, "name");
    private final Node phoneNumberNode = getNodeWithField(rootNode, Phone.class, "number");
    private final Node petNameNode = getNodeWithField(rootNode, Pet.class, "name");

    private final SelectorMap<String> selectorMap = new SelectorMap<>();

    @Test
    void precedence() {
        put(field(Person.class, "name"), "foo");
        put(field(Person.class, "name"), "bar");
        assertThat(selectorMap.getValue(personNameNode)).contains("bar");
    }

    @Test
    void precedenceWithScope() {
        put(field(Person.class, "name").within(scope(String.class)), "foo");
        put(field(Person.class, "name").within(scope(String.class)), "bar");
        assertThat(selectorMap.getValue(personNameNode)).contains("bar");
    }

    @MethodSource("phoneNumberFieldMatchingSelectors")
    @ParameterizedTest
    void phoneNumberFieldScopeMatches(TargetSelector selector) {
        put(selector, "foo");
        assertThat(selectorMap.getValue(phoneNumberNode)).contains("foo");
    }

    private static Stream<Arguments> phoneNumberFieldMatchingSelectors() {
        return Stream.of(
                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(String.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Person.class, "address"))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Address.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(List.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Person.class, "address"),
                        scope(Address.class, "phoneNumbers"),
                        scope(Phone.class, "number"))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Address.class),
                        scope(List.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Phone.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Address.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(List.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Phone.class, "number")))
        );
    }

    @MethodSource("phoneNumberFieldNonMatchingSelectors")
    @ParameterizedTest
    void phoneNumberFieldScopeNonMatches(TargetSelector selector) {
        put(selector, "foo");
        assertThat(selectorMap.getValue(phoneNumberNode)).isEmpty();
    }

    private static Stream<Arguments> phoneNumberFieldNonMatchingSelectors() {
        return Stream.of(
                Arguments.of(field(Phone.class, "number").within(
                        scope(Pet.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Phone.class, "countryCode"))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Object.class)))
        );
    }

    @Test
    void allStringsMatching() {
        put(allStrings().within(scope(Pet.class)), "foo");
        assertThat(selectorMap.getValue(petNameNode)).contains("foo");
        assertThat(selectorMap.getValue(personNameNode)).isEmpty();
        assertThat(selectorMap.getValue(phoneNumberNode)).isEmpty();

        final Node stringNode = nodeFactory.createRootNode(String.class, null);
        assertThat(selectorMap.getValue(stringNode)).isEmpty();
    }

    private static Node getNodeWithField(final Node node, final Class<?> declaringClass, final String fieldName) {
        final Field field = ReflectionUtils.getField(declaringClass, fieldName);
        assertThat(field).as("null field").isNotNull();
        if (Objects.equals(node.getField(), field)) {
            return node;
        }

        final List<Node> children = new ArrayList<>(node.getChildren());
        if (node instanceof CollectionNode) {
            children.add(((CollectionNode) node).getElementNode());
        } else if (node instanceof ArrayNode) {
            children.add(((ArrayNode) node).getElementNode());
        }
        Node result = null;
        for (Node child : children) {
            result = getNodeWithField(child, declaringClass, fieldName);
            if (result != null) break;
        }
        return result;
    }

    private void put(final TargetSelector selector, final String value) {
        selectorMap.put(cast(selector), value);
    }

    private static SelectorImpl cast(final TargetSelector selector) {
        return (SelectorImpl) selector;
    }


}
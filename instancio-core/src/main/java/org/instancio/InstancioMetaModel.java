/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides support for generating metamodels for classes in order to
 * avoid referencing fields as literal strings.
 * <p>
 * For example, instead of:
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .generate(field(Address.class, "city"), gen -> gen.oneOf("London", "Tokyo", "Paris"))
 *     .create();
 * }</pre>
 * <p>
 * you would write:
 *
 * <pre>{@code
 * Person person = Instancio.of(Person.class)
 *     .generate(Address_.city, gen -> gen.oneOf("London", "Tokyo", "Paris"))
 *     .create();
 * }</pre>
 * <p>
 * Metamodels can be generated by placing <code>&#064;InstancioMetaModel</code> annotation on a class,
 * for instance a JUnit test class, and activating the Instancio annotation processor in your build
 * (see documentation for Maven and Gradle examples).
 *
 * <pre class="code"><code class="java">
 * &#064;InstancioMetaModel(classes = {
 *         Address.class,
 *         Person.class
 * })
 * class PersonTest {
 *     // ... snip
 * }
 * </code></pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface InstancioMetaModel {

    /**
     * Classes for which metamodel should be generated.
     *
     * @return classes to generate metamodels for
     */
    Class<?>[] classes() default {};
}
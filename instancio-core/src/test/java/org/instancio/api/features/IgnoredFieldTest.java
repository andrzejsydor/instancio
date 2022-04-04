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
package org.instancio.api.features;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.pojo.basic.ClassWithInitializedField;
import org.instancio.pojo.basic.SupportedNumericTypes;
import org.instancio.testsupport.tags.NonDeterministicTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;
import static org.instancio.Bindings.allShorts;
import static org.instancio.Bindings.field;

@NonDeterministicTag
class IgnoredFieldTest {

    @Test
    @DisplayName("Ignored field should retain the original value")
    void fieldIsIgnored() {
        final ClassWithInitializedField holder = Instancio.of(ClassWithInitializedField.class)
                .ignore(field("stringValue"))
                .ignore(field("intValue"))
                .create();

        assertThat(holder.getStringValue()).isEqualTo(ClassWithInitializedField.DEFAULT_STRING_FIELD_VALUE);
        assertThat(holder.getIntValue()).isEqualTo(ClassWithInitializedField.DEFAULT_INT_FIELD_VALUE);
    }

    @Test
    void primitiveAndWrapperTypes() {
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .ignore(field("bigInteger"))
                .ignore(all(BigDecimal.class))
                .ignore(all(int.class)) // does NOT include Integer wrapper
                .ignore(all(Long.class)) // does NOT include primitive long
                .ignore(allShorts()) // includes BOTH, primitive and wrapper short
                .toModel();

        final SupportedNumericTypes result = Instancio.create(model);

        assertThat(result.getBigInteger()).isNull();
        assertThat(result.getBigDecimal()).isNull();
        assertThat(result.getLongWrapper()).isNull();
        assertThat(result.getPrimitiveInt()).isZero();
        assertThat(result.getPrimitiveLong()).isNotZero();
        assertThat(result.getIntegerWrapper()).isNotNull();
        assertThat(result.getPrimitiveShort()).isZero();
        assertThat(result.getShortWrapper()).isNull();
    }
}
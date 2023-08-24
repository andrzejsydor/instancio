/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.pojo.beanvalidation.AssertBooleanBV;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.util.Constants.SAMPLE_SIZE_DD;

@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class AssertBooleanBVTest {

    @RepeatedTest(SAMPLE_SIZE_DD)
    void assertFalse() {
        final AssertBooleanBV.WithFalse result = Instancio.create(AssertBooleanBV.WithFalse.class);
        assertThat(result.primitiveBoolean()).isFalse();
        assertThat(result.booleanWrapper()).isEqualTo(Boolean.FALSE);
    }

    @RepeatedTest(SAMPLE_SIZE_DD)
    void assertTrue() {
        final AssertBooleanBV.WithTrue result = Instancio.create(AssertBooleanBV.WithTrue.class);
        assertThat(result.primitiveBoolean()).isTrue();
        assertThat(result.booleanWrapper()).isEqualTo(Boolean.TRUE);
    }
}
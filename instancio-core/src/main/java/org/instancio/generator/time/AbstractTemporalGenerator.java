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
package org.instancio.generator.time;

import org.instancio.generator.AbstractGenerator;
import org.instancio.generator.GeneratorContext;
import org.instancio.util.Verify;

import java.time.temporal.Temporal;

abstract class AbstractTemporalGenerator<T extends Temporal> extends AbstractGenerator<T> implements TemporalGeneratorSpec<T> {

    private final T defaultMin;
    private final T defaultMax;

    protected T min;
    protected T max;

    AbstractTemporalGenerator(final GeneratorContext context, final T min, final T max) {
        super(context);
        this.defaultMin = min;
        this.defaultMax = max;
        this.min = min;
        this.max = max;
    }

    abstract T now();

    abstract T getEarliestFuture();

    abstract void validateRange();

    @Override
    public final TemporalGeneratorSpec<T> past() {
        min = defaultMin;
        max = now();
        return this;
    }

    @Override
    public final TemporalGeneratorSpec<T> future() {
        min = getEarliestFuture();
        max = defaultMax;
        return this;
    }

    @Override
    public TemporalGeneratorSpec<T> range(final T startInclusive, final T endExclusive) {
        min = Verify.notNull(startInclusive, "Start parameter must not be null");
        max = Verify.notNull(endExclusive, "End parameter must not be null");
        validateRange();
        return this;
    }
}
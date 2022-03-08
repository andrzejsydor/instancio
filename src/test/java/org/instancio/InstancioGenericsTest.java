package org.instancio;

import org.instancio.exception.InstancioException;
import org.instancio.pojo.generics.FooContainer;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.pojo.person.Address;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InstancioGenericsTest {

    @Test
    void address() {
        Address result = Instancio.of(Address.class).create();

        System.out.println(result);
        assertThat(result.getPhoneNumbers()).isNotEmpty();

        result.getPhoneNumbers().forEach(phone -> {
            assertThat(phone.getCountryCode()).isNotBlank();
            assertThat(phone.getNumber()).isNotBlank();
        });
    }

    @Test
    void fooContainerWithUserSuppliedInstance() {
        FooContainer result = Instancio.of(FooContainer.class)
                .with("item", () -> {
                    FooContainer.Foo<String> foo = new FooContainer.Foo<>();
                    foo.setFooValue("test");
                    return foo;
                })
                .create();

        assertThat(result).isNotNull();
        assertThat(result.getItem()).isNotNull();
        assertThat(result.getItem().getFooValue()).isNotNull().isInstanceOf(String.class);
        assertThat(result.getItem().getOtherFooValue()).as("Value was not set").isNull();
    }


    @Test
    void unboundTypeVariablesErrorMessage() {
        assertThatThrownBy(() -> Instancio.of(GenericItemContainer.class).create())
                .isInstanceOf(InstancioException.class)
                .hasMessage("Generic class %s " +
                        "has 2 type parameters: [X, Y]. Please specify all type parameters using " +
                        "'withType(Class... types)`", GenericItemContainer.class.getName());

        assertThatThrownBy(() -> Instancio.of(List.class).create())
                .hasMessage("Generic class java.util.List has 1 type parameters: [E]." +
                        " Please specify all type parameters using 'withType(Class... types)`");
    }


}
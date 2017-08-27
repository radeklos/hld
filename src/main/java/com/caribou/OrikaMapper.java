package com.caribou;

import com.caribou.auth.domain.UserAccount;
import com.caribou.auth.rest.dto.NestedSingleObject;
import com.caribou.company.domain.CompanyEmployee;
import com.caribou.company.rest.dto.EmployeeDto;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Timestamp;
import java.time.LocalDate;


@Component
public class OrikaMapper implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        orikaMapperFactory.getConverterFactory().registerConverter(new Timestamp2LocalDate());
        orikaMapperFactory.getConverterFactory().registerConverter(new EmployeeConverter());
        orikaMapperFactory.getConverterFactory().registerConverter(new UserAccountToSingleNested());
    }

    private class Timestamp2LocalDate extends BidirectionalConverter<Timestamp, LocalDate> {

        @Override
        public LocalDate convertTo(Timestamp source, Type<LocalDate> destinationType, MappingContext mappingContext) {
            return source.toLocalDateTime().toLocalDate();
        }

        @Override
        public Timestamp convertFrom(LocalDate source, Type<Timestamp> destinationType, MappingContext mappingContext) {
            return Timestamp.valueOf(source.atStartOfDay());
        }

    }

    private class EmployeeConverter extends BidirectionalConverter<CompanyEmployee, EmployeeDto> {
        @Override
        public EmployeeDto convertTo(CompanyEmployee source, Type<EmployeeDto> destinationType, MappingContext mappingContext) {
            return EmployeeDto.builder()
                    .email(source.getMember().getEmail())
                    .firstName(source.getMember().getFirstName())
                    .lastName(source.getMember().getLastName())
                    .role(source.getRole())
                    .uid(source.getMember().getUid())
                    .build();
        }

        @Override
        public CompanyEmployee convertFrom(EmployeeDto source, Type<CompanyEmployee> destinationType, MappingContext mappingContext) {
            throw new NotImplementedException();
        }
    }

    private class UserAccountToSingleNested extends BidirectionalConverter<UserAccount, NestedSingleObject> {

        @Override
        public NestedSingleObject convertTo(UserAccount source, Type<NestedSingleObject> destinationType, MappingContext mappingContext) {
            return NestedSingleObject.builder()
                    .uid(source.getUid().toString())
                    .label(source.getFullName())
                    .build();
        }

        @Override
        public UserAccount convertFrom(NestedSingleObject source, Type<UserAccount> destinationType, MappingContext mappingContext) {
            throw new NotImplementedException();
        }
    }

}

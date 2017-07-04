package com.caribou;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import net.rakugakibox.spring.boot.orika.OrikaMapperFactoryConfigurer;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;


@Component
public class OrikaMapper implements OrikaMapperFactoryConfigurer {

    @Override
    public void configure(MapperFactory orikaMapperFactory) {
        orikaMapperFactory.getConverterFactory().registerConverter(new Timestamp2LocalDate());
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
}

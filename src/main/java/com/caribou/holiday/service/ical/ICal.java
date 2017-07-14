package com.caribou.holiday.service.ical;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public abstract class ICal {

    public String toICal() throws IllegalAccessException {
        StringBuilder builder = new StringBuilder();

        ICalRoot[] root = this.getClass().getAnnotationsByType(ICalRoot.class);
        boolean hasRootElement = root.length == 1;
        if (hasRootElement) {
            builder.append("BEGIN:").append(root[0].value()).append("\n");
        }
        Field[] annotations = this.getClass().getDeclaredFields();
        for (Field field : annotations) {
            ICalField[] fieldAnn = field.getAnnotationsByType(ICalField.class);
            if (fieldAnn.length == 1 && field.get(this) != null) {
                ICalField ann = fieldAnn[0];
                builder.append(ann.value());
                if (!ann.extra()) {
                    builder.append(":");
                }
                builder.append(simpleTransformer(field.get(this))).append("\n");
            }
            ICalNested[] nestedAnn = field.getAnnotationsByType(ICalNested.class);
            if (nestedAnn.length == 1 && field.get(this) != null) {
                if (field.get(this) instanceof Collection) {
                    Collection collection = (Collection) field.get(this);
                    for (Object o : collection) {
                        if (o instanceof ICal) {
                            builder.append(((ICal) o).toICal()).append("\n");
                        }
                    }
                }
            }
        }
        if (hasRootElement) {
            builder.append("END:").append(root[0].value()).append("\n");
        }
        return builder.toString().trim();
    }

    private String simpleTransformer(Object obj) {
        if (obj instanceof LocalDate) {
            return ((LocalDate) obj).format(DateTimeFormatter.ofPattern("YYYYMMdd"));
        } else if (obj instanceof LocalDateTime) {
            return ((LocalDateTime) obj).format(DateTimeFormatter.ofPattern("YYYYMMdd'T'HHmmss'Z'"));
        } else if (obj instanceof Instant) {
            return ((Instant) obj).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("YYYYMMdd'T'HHmmss'Z'"));
        } else if (obj instanceof ZonedDateTime) {
            ZonedDateTime time = (ZonedDateTime) obj;
            return ";TZID=" + time.getZone() + ":" + time.toLocalDateTime().format(DateTimeFormatter.ofPattern("YYYYMMdd'T'HHmmss"));
        }
        return obj.toString();
    }

}

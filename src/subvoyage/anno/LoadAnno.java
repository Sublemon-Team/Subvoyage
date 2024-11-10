package subvoyage.anno;

import java.lang.annotation.*;
@Target(value=ElementType.FIELD)
@Retention(value= RetentionPolicy.RUNTIME)
public @interface LoadAnno {
    String value();
    String def() default "";
}

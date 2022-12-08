module com.github.lc.oss.commons.google.auth {
    requires com.github.lc.oss.commons.jwt;
    requires com.github.lc.oss.commons.util;

    requires com.fasterxml.jackson.annotation;

    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;

    opens com.github.lc.oss.commons.google.auth.model to com.fasterxml.jackson.databind;

    exports com.github.lc.oss.commons.google.auth;
}

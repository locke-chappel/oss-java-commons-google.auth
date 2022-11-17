module com.github.lc.oss.commons.google.auth {
    requires com.github.lc.oss.commons.jwt;
    requires com.github.lc.oss.commons.util;

    requires com.fasterxml.jackson.annotation;

    requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;

    opens com.github.lc.oss.commons.google.auth.model to com.fasterxml.jackson.databind;

    exports com.github.lc.oss.commons.google.auth;
}

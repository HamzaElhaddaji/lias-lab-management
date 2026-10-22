package com.lias.lias_backend;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class DeploymentConfigurationTests {
 private String url(Map<String,Object> values)throws Exception {
  var config=new Properties();try(var stream=getClass().getResourceAsStream("/application-production.properties")){config.load(stream);}
  var sources=new MutablePropertySources();sources.addFirst(new MapPropertySource("deployment",values));
  return new PropertySourcesPropertyResolver(sources).resolveRequiredPlaceholders(config.getProperty("spring.datasource.url"));
 }
 @Test void supportsExistingJdbcUrlWithoutRenderVariables()throws Exception {assertEquals("jdbc:postgresql://existing.example.test/lab",url(Map.of("DB_URL","jdbc:postgresql://existing.example.test/lab")));}
 @Test void buildsJdbcUrlFromManagedDatabaseComponents()throws Exception {assertEquals("jdbc:postgresql://render-db.internal:5432/lias_db",url(Map.of("DB_HOST","render-db.internal","DB_NAME","lias_db")));}
}

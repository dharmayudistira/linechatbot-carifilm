package com.pandeka.CariFilm.database;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.LineMessagingClientBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@PropertySource("classpath:application.properties")
public class Config {

    @Autowired
    private Environment mEnvironment;

    @Bean
    public String getChannelSecret() {
        return mEnvironment.getProperty("com.linecorp.channel_secret");
    }

    @Bean
    public String getChannelAccessToken() {
        return mEnvironment.getProperty("com.linecorp.channel_access_token");
    }

    @Bean(name="lineMessagingClient")
    public LineMessagingClient getMessagingClient()
    {
        return LineMessagingClient
                .builder(getChannelAccessToken())
                .apiEndPoint(LineMessagingClientBuilder.DEFAULT_API_END_POINT)
                .connectTimeout(LineMessagingClientBuilder.DEFAULT_CONNECT_TIMEOUT)
                .readTimeout(LineMessagingClientBuilder.DEFAULT_READ_TIMEOUT)
                .writeTimeout(LineMessagingClientBuilder.DEFAULT_WRITE_TIMEOUT)
                .build();
    }

    @Bean(name="lineSignatureValidator")
    public LineSignatureValidator getSignatureValidator()
    {
        return new LineSignatureValidator(getChannelSecret().getBytes());
    }

    @Bean
    DataSource getDataSource()
    {
        String dbUrl=System.getenv("JDBC_DATABASE_URL");
        String username=System.getenv("JDBC_DATABASE_USERNAME");
        String password=System.getenv("JDBC_DATABASE_PASSWORD");

        DriverManagerDataSource ds=new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(dbUrl);
        ds.setUsername(username);
        ds.setPassword(password);

        return ds;
    }

    @Bean
    public Dao getDao()
    {
        return new DaoImpl(getDataSource());
    }
}

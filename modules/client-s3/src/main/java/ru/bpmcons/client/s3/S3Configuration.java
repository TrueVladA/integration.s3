package ru.bpmcons.client.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@ComponentScan
public class S3Configuration {
    @Bean
    public AmazonS3 s3client(S3Properties properties) {
        // AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
        ClientConfiguration customeConfiguration = new ClientConfiguration()
                .withMaxConnections(properties.getMaxConnections())
                .withConnectionTimeout((int) properties.getConnectionTimeout().toMillis())
                .withSocketTimeout((int) properties.getSocketTimeout().toMillis())
                .withConnectionMaxIdleMillis(properties.getConnectionMaxIde().toMillis());
        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withPathStyleAccessEnabled(properties.isPathStyle())
                .withClientConfiguration(customeConfiguration)
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(properties.getEndPoint(), properties.getRegion()))
                .build();
    }
}

package br.com.wiremock;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import com.github.tomakehurst.wiremock.standalone.MappingsSource;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.github.tomakehurst.wiremock.stubbing.StubMappings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class S3MappingSource  implements MappingsSource {

    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
    final String bucketName = "wiremock-qa";

    @Override
    public void save(List<StubMapping> stubMappings) {}

    @Override
    public void save(StubMapping stubMapping) { }

    @Override
    public void remove(StubMapping stubMapping) {}

    @Override
    public void removeAll() {}

    @Override
    public void loadMappingsInto(StubMappings stubMappings) {
        getMocksFromS3("", stubMappings);
    }

    public void getMocksFromS3(String prefix, StubMappings stubMappings){
        try {
            ListObjectsV2Result result = s3.listObjectsV2(bucketName, prefix);
            List<S3ObjectSummary> objects = result.getObjectSummaries();
            for (S3ObjectSummary os : objects)
                if (os.getKey().endsWith("/") && prefix.equals("")) getMocksFromS3(os.getKey(), stubMappings);
                else if (os.getKey().endsWith("/") && !prefix.equals("")) continue;
                else {
                    S3Object object = s3.getObject(bucketName, os.getKey());
                    System.out.println("Read " + os.getKey());
                    stubMappings.addMapping(StubMapping.buildFrom(new String(IOUtils.toByteArray(object.getObjectContent()))));
                }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getMockLocal(StubMappings stubMappings){
        try {
            File folder = new File("src/main/resources");
            File[] listOfFiles = folder.listFiles();

            for (File file : listOfFiles)
                if (file.isFile()) {
                    String fileString = new String(Files.readAllBytes(file.toPath()));
                    stubMappings.addMapping(StubMapping.buildFrom(fileString));
                }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
package ru.bpmcons.client.s3.domain;

public record S3File(
        Bucket bucket,
        String id
) {
    public S3File withBucket(Bucket bucket) {
        return new S3File(bucket, id);
    }
}

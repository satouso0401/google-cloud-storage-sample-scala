package example

import better.files._
import com.google.cloud.storage._

import java.time.Instant
import java.util.concurrent.TimeUnit
import scala.collection.JavaConverters._

// export GOOGLE_APPLICATION_CREDENTIALS=/Users/$(whoami)/Downloads/credential.json

object GcsSample extends App {
  // create local file
  val fileName = s"filename-${Instant.now().getEpochSecond}.txt"
  val localFile = File(fileName)
  localFile.append("hello world")

  // gcs setting
  val storage = StorageOptions.getDefaultInstance.getService
  val bucketName = "bucket-name"
  val bucket = storage.get(bucketName)

  // create signed download url after direct upload
  val uploadGcsPath1 = s"any-path/direct-upload/$fileName"
  val gcsFile = bucket.create(uploadGcsPath1, localFile.newInputStream, "text/plain")
  val signedUrl = gcsFile.signUrl(1000, TimeUnit.SECONDS)
  println("\nsignedUrl:\n" + signedUrl.toString + "\n")

  // create pre-signed upload url
  val uploadGcsPath2 = s"any-path/pre-signed/$fileName"
  val blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, uploadGcsPath2)).build
  val uploadUrl = storage.signUrl(
    blobInfo,
    15,
    TimeUnit.MINUTES,
    Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
    Storage.SignUrlOption.withExtHeaders(Map("Content-Type" -> "application/octet-stream").asJava),
    Storage.SignUrlOption.withV4Signature())

  println("uploadUrl:")
  println(s"curl -X PUT -H 'Content-Type: application/octet-stream' --upload-file $fileName '$uploadUrl'\n")

  // can sign the download URL before upload
  val downloadUrl = storage.signUrl(blobInfo, 5, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature)
  println("downloadUrl:")
  println(downloadUrl)
}

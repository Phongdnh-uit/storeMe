package com.DPhong.storeMe.service.general;

import com.DPhong.storeMe.exception.TikaAnalysisException;
import java.io.InputStream;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

/**
 * This class is responsible for analyzing files using Apache Tika. It can extract metadata and text
 * content from various file formats. The extracted information can be used for indexing, searching,
 * or other purposes.
 */
public class TikaAnalysis {
  private static final Tika tika = new Tika();

  /**
   * This method returns the MIME type of the input stream.
   *
   * @param stream The input stream of the file.
   * @return The MIME type as a string.
   * @throws TikaAnalysisException if there is an error detecting the MIME type.
   * @apiNote The function is not closed the stream, so the caller is responsible for closing it.
   */
  public static String getMimeType(InputStream stream) {
    try {
      return tika.detect(stream);
    } catch (Exception e) {
      throw new TikaAnalysisException("Error detecting MIME type", e);
    }
  }

  /**
   * This method returns the file extension based on the MIME type of the input stream.
   *
   * @param stream The input stream of the file.
   * @return The file extension as a string.
   * @throws TikaAnalysisException if there is an error getting the MIME type.
   * @apiNote The function is not closed the stream, so the caller is responsible for closing it.
   */
  public static String getExtension(InputStream stream) {
    String mimeType = getMimeType(stream);
    MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
    MimeType mime;
    try {
      mime = mimeTypes.forName(mimeType);
    } catch (MimeTypeException e) {
      throw new TikaAnalysisException("Error getting MIME type", e);
    }
    return mime.getExtension();
  }

  /**
   * This method extracts metadata from the input stream and returns it as a Metadata object.
   *
   * @param stream The input stream of the file.
   * @return The extracted metadata as a Metadata object.
   * @throws TikaAnalysisException if there is an error parsing the metadata file.
   * @apiNote The function is not closed the stream, so the caller is responsible for closing it.
   */
  public static Metadata extractMetadata(InputStream stream) {
    Metadata metadata = new Metadata();
    try {
      tika.parse(stream, metadata);
    } catch (Exception e) {
      throw new TikaAnalysisException("Error parsing metadata file", e);
    }
    return metadata;
  }

  /**
   * This method extracts text content from the input stream and returns it as a string.
   *
   * @param stream The input stream of the file.
   * @return The extracted text content as a string.
   * @throws TikaAnalysisException if there is an error parsing the file.
   * @apiNote The function is not closed the stream, so the caller is responsible for closing it.
   */
  public static String extractContent(InputStream stream) {
    try {
      return tika.parseToString(stream);
    } catch (Exception e) {
      throw new TikaAnalysisException("Error parsing file", e);
    }
  }
}

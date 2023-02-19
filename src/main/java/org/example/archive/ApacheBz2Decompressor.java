package org.example.archive;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ApacheBz2Decompressor implements Bz2Decompressor {

    @Override
    public InputStream decompressBz2Archive(File archive) throws IOException {
        return new BZip2CompressorInputStream(new FileInputStream(archive));
    }
}

package org.example.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface Bz2Decompressor {

    InputStream decompressBz2Archive(File archive) throws IOException;

}

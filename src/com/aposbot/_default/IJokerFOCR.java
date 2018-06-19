package com.aposbot._default;

import java.io.Closeable;

public interface IJokerFOCR
    extends Closeable {

    @Override
    public void close();

    public void setFilePaths(String file_model, String file_dict);

    public String getGuess();

    public boolean loadNativeLibrary();

    public boolean isLibraryLoaded();
}

package com.creiss.network;

/**
 * Created by Upendra.Patil on 7/15/2016.
 */
public interface ApiListener {
    void progressDialog(boolean show);

    void success();

    void failure(Throwable t);
}

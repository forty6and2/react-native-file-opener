package com.fileopener;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.Map;
import java.util.HashMap;

public class FileOpener extends ReactContextBaseJavaModule {

  public FileOpener(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  public String getName() {
    return "FileOpener";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    return constants;
  }

  @ReactMethod
  public void open(String fileArg, String contentType, Promise promise) throws JSONException {
    File file = new File(fileArg);
    if (!file.exists()) {
      promise.reject("file");
    }
    try {
      Intent intent;
      if (Build.VERSION.SDK_INT >= 24) {
        Uri fileURI = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() + ".provider", file);
        intent = new Intent(Intent.ACTION_VIEW).setDataAndType(fileURI, contentType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PackageManager pm = getCurrentActivity().getPackageManager();
        if (intent.resolveActivity(pm) == null) {
          // set intent to null in order to reject the promise in the next step
          intent = null;
        }
      } else {
        Uri fileURI = Uri.parse("file://" + fileArg);
        intent = new Intent(Intent.ACTION_VIEW).setDataAndType(fileURI, contentType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      }
      if (intent != null) {
        this.getReactApplicationContext().startActivity(intent);
        promise.resolve("success");
      }
      else {
        promise.reject("application");
      }
      // ***
      // Uri fileURI = FileProvider.getUriForFile(getReactApplicationContext(), getReactApplicationContext().getPackageName() + ".provider", file);
      // Intent intent = new Intent(Intent.ACTION_VIEW);
      // intent.setDataAndType(path, contentType);
      // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      // intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      // getReactApplicationContext().startActivity(intent);
      // promise.resolve("success");
      // ***
    } catch (android.content.ActivityNotFoundException e) {
      // no application can handle the file type
      promise.reject("application");
    }
  }

}

package com.cjs.qa.utilities;

import java.net.HttpURLConnection;

public interface IHTTP {
  static String getResponseValue(int httpResponseCode) {
    switch (httpResponseCode) {
      case HttpURLConnection.HTTP_ACCEPTED:
        return "HTTP_ACCEPTED";
      case HttpURLConnection.HTTP_BAD_GATEWAY:
        return "HTTP_BAD_GATEWAY";
      case HttpURLConnection.HTTP_BAD_METHOD:
        return "HTTP_BAD_METHOD";
      case HttpURLConnection.HTTP_BAD_REQUEST:
        return "HTTP_BAD_REQUEST";
      case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
        return "HTTP_CLIENT_TIMEOUT";
      case HttpURLConnection.HTTP_CONFLICT:
        return "HTTP_CONFLICT";
      case HttpURLConnection.HTTP_CREATED:
        return "HTTP_CREATED";
      case HttpURLConnection.HTTP_ENTITY_TOO_LARGE:
        return "HTTP_ENTITY_TOO_LARGE";
      case HttpURLConnection.HTTP_FORBIDDEN:
        return "HTTP_FORBIDDEN";
      case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
        return "HTTP_GATEWAY_TIMEOUT";
      case HttpURLConnection.HTTP_GONE:
        return "HTTP_GONE";
      // case HttpURLConnection.HTTP_SERVER_ERROR:// Deprecated. it is
      // misplaced and shouldn't have existed.
      case HttpURLConnection.HTTP_INTERNAL_ERROR:
        return "HTTP_INTERNAL_ERROR/HTTP_SERVER_ERROR";
      case HttpURLConnection.HTTP_LENGTH_REQUIRED:
        return "HTTP_LENGTH_REQUIRED";
      case HttpURLConnection.HTTP_MOVED_PERM:
        return "HTTP_MOVED_PERM";
      case HttpURLConnection.HTTP_MOVED_TEMP:
        return "HTTP_MOVED_TEMP";
      case HttpURLConnection.HTTP_MULT_CHOICE:
        return "HTTP_MULT_CHOICE";
      case HttpURLConnection.HTTP_NO_CONTENT:
        return "HTTP_NO_CONTENT";
      case HttpURLConnection.HTTP_NOT_ACCEPTABLE:
        return "HTTP_NOT_ACCEPTABLE";
      case HttpURLConnection.HTTP_NOT_AUTHORITATIVE:
        return "HTTP_NOT_AUTHORITATIVE";
      case HttpURLConnection.HTTP_NOT_FOUND:
        return "HTTP_NOT_FOUND";
      case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
        return "HTTP_NOT_IMPLEMENTED";
      case HttpURLConnection.HTTP_NOT_MODIFIED:
        return "HTTP_NOT_MODIFIED";
      case HttpURLConnection.HTTP_OK:
        return "HTTP_OK";
      case HttpURLConnection.HTTP_PARTIAL:
        return "HTTP_PARTIAL";
      case HttpURLConnection.HTTP_PAYMENT_REQUIRED:
        return "HTTP_PAYMENT_REQUIRED";
      case HttpURLConnection.HTTP_PRECON_FAILED:
        return "HTTP_PRECON_FAILED";
      case HttpURLConnection.HTTP_PROXY_AUTH:
        return "HTTP_PROXY_AUTH";
      case HttpURLConnection.HTTP_REQ_TOO_LONG:
        return "HTTP_REQ_TOO_LONG";
      case HttpURLConnection.HTTP_RESET:
        return "HTTP_RESET";
      case HttpURLConnection.HTTP_SEE_OTHER:
        return "HTTP_SEE_OTHER";
      case HttpURLConnection.HTTP_UNAUTHORIZED:
        return "HTTP_UNAUTHORIZED";
      case HttpURLConnection.HTTP_UNAVAILABLE:
        return "HTTP_UNAVAILABLE";
      case HttpURLConnection.HTTP_UNSUPPORTED_TYPE:
        return "HTTP_UNSUPPORTED_TYPE";
      case HttpURLConnection.HTTP_USE_PROXY:
        return "HTTP_USE_PROXY";
      case HttpURLConnection.HTTP_VERSION:
        return "HTTP_VERSION";
      default:
        return "***Response not found in IHTTP/getReturnValue-switch statement***";
    }
  }
}

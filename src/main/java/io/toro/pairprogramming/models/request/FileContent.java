package io.toro.pairprogramming.models.request;

import io.toro.pairprogramming.models.request.Content;

public class FileContent extends Content {

  private String rawContent;

  public String getRawContent() {
    return rawContent;
  }

  public void setRawContent(String rawContent) {
    this.rawContent = rawContent;
  }
}
